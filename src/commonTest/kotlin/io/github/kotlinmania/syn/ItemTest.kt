// port-lint: tests tests/test_item.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ItemTest {
    @Test
    fun testMacroVariableAttr() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(
                        Group(
                            Delimiter.None,
                            TokenStream.fromString("#[test]").getOrThrow(),
                        ),
                    ),
                    TokenTree.Ident(Ident.new("fn", Span.callSite())),
                    TokenTree.Ident(Ident.new("f", Span.callSite())),
                    TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
                    TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
                ),
            )

        val item = assertIs<Item.Fn>(parseItem(tokens))
        assertEquals(1, item.attrs.size)
        assertIs<AttrStyle.Outer>(item.attrs.single().style)
        assertPath(item.attrs.single().path(), "test")
        assertIs<Visibility.Inherited>(item.vis)
        assertEquals("f", item.ident.toString())
        assertTrue(item.generics.params.isEmpty())
        assertIs<ReturnType.Default>(item.output)
        assertTrue(item.inputs.isEmpty())
        assertTrue(item.block?.stmts.orEmpty().isEmpty())
    }

    @Test
    fun testNegativeImpl() {
        val neverImpl = assertIs<Item.Impl>(parseItem("impl ! {}"))
        assertNull(neverImpl.traitPath)
        assertIs<SynType.Never>(neverImpl.selfType)
        assertTrue(neverImpl.generics.params.isEmpty())
        assertTrue(neverImpl.items.isEmpty())

        val failure = parseStr(ItemParse, "impl !Trait {}")
        assertTrue(failure.isFailure)
        assertEquals("inherent impls cannot be negative", failure.exceptionOrNull()?.toString())

        val negativeTrait = assertIs<Item.Impl>(parseItem("impl !Trait for T {}"))
        val traitPath = assertNotNull(negativeTrait.traitPath)
        assertNotNull(traitPath.polarity)
        assertPath(traitPath.path, "Trait")
        assertTypePath(negativeTrait.selfType, "T")
    }

    @Test
    fun testMacroVariableImpl() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("impl", Span.callSite())),
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("Trait").getOrThrow())),
                    TokenTree.Ident(Ident.new("for", Span.callSite())),
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("Type").getOrThrow())),
                    TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
                ),
            )

        val item = assertIs<Item.Impl>(parseItem(tokens))
        val traitPath = assertNotNull(item.traitPath)
        assertNull(traitPath.polarity)
        assertPath(traitPath.path, "Trait")
        val group = assertIs<SynType.Group>(item.selfType)
        assertTypePath(group.elem, "Type")
        assertTrue(item.items.isEmpty())
    }

    @Test
    fun testSupertraits() {
        val noColon = assertIs<Item.Trait>(parseItem("trait Trait where {}"))
        assertNull(noColon.colonToken)
        assertTrue(noColon.supertraits.isEmpty())
        assertNotNull(noColon.generics.whereClause)

        val emptyColon = assertIs<Item.Trait>(parseItem("trait Trait: where {}"))
        assertNotNull(emptyColon.colonToken)
        assertTrue(emptyColon.supertraits.isEmpty())
        assertNotNull(emptyColon.generics.whereClause)

        val sized = assertIs<Item.Trait>(parseItem("trait Trait: Sized where {}"))
        assertNotNull(sized.colonToken)
        assertEquals(1, sized.supertraits.size)
        assertTraitBound(sized.supertraits.toList().single(), "Sized")
        assertTrue(!sized.supertraits.trailingPunct())
        assertNotNull(sized.generics.whereClause)

        val sizedTrailingPlus = assertIs<Item.Trait>(parseItem("trait Trait: Sized + where {}"))
        assertNotNull(sizedTrailingPlus.colonToken)
        assertEquals(1, sizedTrailingPlus.supertraits.size)
        assertTraitBound(sizedTrailingPlus.supertraits.toList().single(), "Sized")
        assertTrue(sizedTrailingPlus.supertraits.trailingPunct())
        assertNotNull(sizedTrailingPlus.generics.whereClause)
    }

    @Test
    fun testTypeEmptyBounds() {
        val item = assertIs<Item.Trait>(parseItem("trait Foo { type Bar: ; }"))
        val assocType = assertIs<TraitItem.AssocType>(item.items.single())
        assertEquals("Bar", assocType.ident.toString())
        assertTrue(assocType.generics.params.isEmpty())
        assertNotNull(assocType.colonToken)
        assertTrue(assocType.bounds.isEmpty())
        assertNull(assocType.default)
    }

    @Test
    fun testImplVisibility() {
        val item = assertIs<Item.Verbatim>(parseItem("pub default unsafe impl union {}"))
        assertEquals("pub default unsafe impl union { }", item.tokens.toString())
    }

    @Test
    fun testImplTypeParameterDefaults() {
        val item = assertIs<Item.Impl>(parseItem("impl<T = ()> () {}"))
        assertNull(item.traitPath)
        assertIs<SynType.Tuple>(item.selfType)
        val typeParam = assertIs<GenericParam.TypeParam>(item.generics.params.toList().single())
        assertEquals("T", typeParam.ident.toString())
        assertNotNull(typeParam.eqToken)
        assertIs<SynType.Tuple>(typeParam.default)
    }

    @Test
    fun testImplTraitTrailingPlus() {
        val item = assertIs<Item.Fn>(parseItem("fn f() -> impl Sized + {}"))
        val output = assertIs<ReturnType.TypeReturn>(item.output)
        val implTrait = assertIs<SynType.ImplTrait>(output.ty)
        assertEquals(1, implTrait.bounds.size)
        assertTraitBound(implTrait.bounds.toList().single(), "Sized")
        assertTrue(implTrait.bounds.trailingPunct())
        assertTrue(item.block?.stmts.orEmpty().isEmpty())
    }

    private fun parseItem(source: String): Item =
        parseStr(ItemParse, source).getOrThrow()

    private fun parseItem(tokens: TokenStream): Item =
        parse2(ItemParse, tokens).getOrThrow()

    private fun assertPath(path: Path, vararg segments: String) {
        assertEquals(segments.toList(), path.segments.toList().map { it.ident.toString() })
    }

    private fun assertTypePath(type: SynType, vararg segments: String): SynType.Path {
        val path = assertIs<SynType.Path>(type)
        assertPath(path.path, *segments)
        return path
    }

    private fun assertTraitBound(bound: TypeParamBound, vararg segments: String): TypeParamBound.Trait {
        val trait = assertIs<TypeParamBound.Trait>(bound)
        assertIs<TraitBoundModifier.None>(trait.modifier)
        assertNull(trait.lifetimes)
        assertNull(trait.parenToken)
        assertPath(trait.path, *segments)
        return trait
    }
}
