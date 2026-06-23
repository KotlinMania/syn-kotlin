package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.gen.clone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class CloneTest {
    @Test
    fun deriveInputCloneCopiesNestedMutableContainers() {
        val original =
            parseStr(
                DeriveInputParse,
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
}
