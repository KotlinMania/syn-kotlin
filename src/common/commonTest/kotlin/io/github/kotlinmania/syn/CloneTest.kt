package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.gen.clone
import io.github.kotlinmania.syn.token.Not
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class CloneTest {
    @Test
    fun deriveInputCloneCopiesNestedMutableContainers() {
        val original =
            parseStr(
                DeriveInputParse::parse,
                "struct S<T> where T: Copy { field: T }",
            ).getOrThrow()

        val cloned = original.clone()
        val originalFields = assertIs<Fields.Named>(assertIs<Data.Struct>(original.data).value.fields).fields.named
        original.generics.params.clear()
        original.generics.whereClause = null
        originalFields.clear()

        assertEquals(1, cloned.generics.params.len())
        assertNotNull(cloned.generics.whereClause)
        val clonedFields = assertIs<Fields.Named>(assertIs<Data.Struct>(cloned.data).value.fields).fields.named
        assertEquals(1, clonedFields.len())
        assertEquals("field", clonedFields.first()?.ident.toString())
    }

    @Test
    fun fileAndConstGenericCloneDeepCopiesAttrsBlocksAndStmts() {
        val original =
            parseStr(
                FileParse::parse,
                """
                #[allow(unused)]
                fn demo<const N: usize = 4>() {
                    let x = N;
                }
                """.trimIndent(),
            ).getOrThrow()

        val cloned = original.clone()
        val origFn = assertIs<Item.Fn>(original.items.single())
        val clonedFn = assertIs<Item.Fn>(cloned.items.single())

        origFn.attrs[0].style = AttrStyle.Inner(Not.default())
        assertNotNull(origFn.block).stmts.let { stmts ->
            val local = assertIs<Stmt.Local>(stmts.single())
            local.init = null
        }

        assertIs<AttrStyle.Outer>(clonedFn.attrs.single().style)
        val clonedLocal = assertIs<Stmt.Local>(assertNotNull(clonedFn.block).stmts.single())
        assertNotNull(clonedLocal.init)
        val genericParams = clonedFn.sig.generics.params
        val constParam = assertIs<GenericParam.ConstParam>(genericParams.toList().single())
        assertEquals("N", constParam.ident.toString())
        assertNotNull(constParam.default)
    }
}
