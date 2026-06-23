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
    fun testMacro2ItemFallsBackToVerbatim() {
        val withParens = assertIs<Item.Verbatim>(parseItem("pub macro make() {}"))
        assertEquals("pub macro make () { }", withParens.tokens.toString())

        val withoutParens = assertIs<Item.Verbatim>(parseItem("macro make {}"))
        assertEquals("macro make { }", withoutParens.tokens.toString())

        assertTrue(parseStr(ItemParse, "macro make();").isFailure)
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
    fun testTraitAliasItem() {
        val item = assertIs<Item.TraitAlias>(parseItem("pub trait SharableIterator<T> = Iterator + Sync where T: Clone;"))

        assertIs<Visibility.Public>(item.vis)
        assertEquals("SharableIterator", item.ident.toString())
        val param = assertIs<GenericParam.TypeParam>(item.generics.params.toList().single())
        assertEquals("T", param.ident.toString())
        assertTraitBound(item.bounds.toList()[0], "Iterator")
        assertTraitBound(item.bounds.toList()[1], "Sync")
        assertNotNull(item.generics.whereClause)
    }

    @Test
    fun testStaticItem() {
        val item = assertIs<Item.Static>(parseItem("pub static mut COUNT: usize = 0;"))

        assertIs<Visibility.Public>(item.vis)
        assertIs<StaticMutability.Mut>(item.mutability)
        assertEquals("COUNT", item.ident.toString())
        assertTypePath(item.ty, "usize")
        assertIs<Expr.Lit>(item.expr)

        val tokens = TokenStream.new()
        item.toTokens(tokens)
        assertEquals("pub static mut COUNT : usize = 0 ;", tokens.toString())

        val immutable = assertIs<Item.Static>(parseItem("static NAME: usize = 1;"))
        assertIs<StaticMutability.None>(immutable.mutability)

        assertIs<Item.Verbatim>(parseItem("static COUNT = 0;"))
        assertIs<Item.Verbatim>(parseItem("static COUNT: usize;"))
    }

    @Test
    fun testExternCrateItem() {
        val item = assertIs<Item.ExternCrate>(parseItem("pub extern crate alloc as memory;"))

        assertIs<Visibility.Public>(item.vis)
        assertEquals("alloc", item.ident.toString())
        assertEquals("memory", assertNotNull(item.rename).ident.toString())

        val underscore = assertIs<Item.ExternCrate>(parseItem("extern crate self as _;"))
        assertEquals("self", underscore.ident.toString())
        assertEquals("_", assertNotNull(underscore.rename).ident.toString())
    }

    @Test
    fun testForeignModItem() {
        val item =
            assertIs<Item.ForeignMod>(
                parseItem(
                    """
                    unsafe extern "C" {
                        #![allow(improper_ctypes)]
                        pub fn puts(s: *const c_char);
                        static errno: i32;
                        type Opaque;
                        callback!();
                    }
                    """.trimIndent(),
                ),
            )

        assertNotNull(item.unsafety)
        assertEquals("C", assertNotNull(item.abi.name).value())
        assertEquals(1, item.attrs.size)
        assertIs<AttrStyle.Inner>(item.attrs.single().style)
        assertEquals(4, item.items.size)

        val fn = assertIs<ForeignItem.Fn>(item.items[0])
        assertIs<Visibility.Public>(fn.vis)
        assertEquals("puts", fn.sig.ident.toString())
        val arg = assertIs<FnArg.Typed>(fn.sig.inputs.single())
        val ptr = assertIs<SynType.Ptr>(arg.patType.ty)
        assertNotNull(ptr.constToken)
        assertNull(ptr.mutability)

        val static = assertIs<ForeignItem.Static>(item.items[1])
        assertIs<StaticMutability.None>(static.mutability)
        assertEquals("errno", static.ident.toString())
        assertTypePath(static.ty, "i32")

        val type = assertIs<ForeignItem.ItemType>(item.items[2])
        assertEquals("Opaque", type.ident.toString())
        assertTrue(type.generics.params.isEmpty())

        val macro = assertIs<ForeignItem.Macro>(item.items[3])
        assertPath(macro.mac.path, "callback")
        assertNotNull(macro.semiToken)
    }

    @Test
    fun testForeignItemsWithUnsupportedRustShapesFallBackToVerbatim() {
        val item =
            assertIs<Item.ForeignMod>(
                parseItem(
                    """
                    extern "C" {
                        fn has_body() {}
                        unsafe static MUTABLE: i32;
                        static VALUE: i32 = 1;
                        type Bounded: Sized;
                    }
                    """.trimIndent(),
                ),
            )

        assertEquals(4, item.items.size)
        item.items.forEach { assertIs<ForeignItem.Verbatim>(it) }
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
    fun testTraitAssociatedTypeWhereAfterDefault() {
        val item = assertIs<Item.Trait>(parseItem("trait Foo { type Bar<T> = T where T: Copy; }"))
        val assocType = assertIs<TraitItem.AssocType>(item.items.single())

        assertEquals("Bar", assocType.ident.toString())
        val traitParam = assertIs<GenericParam.TypeParam>(assocType.generics.params.toList().single())
        assertEquals("T", traitParam.ident.toString())
        assertTypePath(assertNotNull(assocType.default).type, "T")
        assertNotNull(assocType.generics.whereClause)
        assertNull(assocType.colonToken)
        assertTrue(assocType.bounds.isEmpty())
    }

    @Test
    fun testImplAssociatedTypeWhereAfterDefault() {
        val item = assertIs<Item.Impl>(parseItem("impl Trait for Ty { default type Bar<T> = T where T: Copy; }"))
        val assocType = assertIs<ImplItem.AssocType>(item.items.single())

        assertEquals("Bar", assocType.ident.toString())
        assertIs<Visibility.Inherited>(assocType.vis)
        assertNotNull(assocType.defaultness)
        val implParam = assertIs<GenericParam.TypeParam>(assocType.generics.params.toList().single())
        assertEquals("T", implParam.ident.toString())
        assertTypePath(assocType.ty, "T")
        assertNotNull(assocType.generics.whereClause)
    }

    @Test
    fun testTypeAliasItem() {
        val item = assertIs<Item.ItemType>(parseItem("pub type Alias<T> where T: Copy = T;"))

        assertIs<Visibility.Public>(item.vis)
        assertEquals("Alias", item.ident.toString())
        val param = assertIs<GenericParam.TypeParam>(item.generics.params.toList().single())
        assertEquals("T", param.ident.toString())
        assertNotNull(item.generics.whereClause)
        assertTypePath(item.ty, "T")
    }

    @Test
    fun testTypeAliasWithBoundsFallsBackToVerbatim() {
        val item = assertIs<Item.Verbatim>(parseItem("type Alias: Bound;"))

        assertEquals("type Alias : Bound ;", item.tokens.toString())
    }

    @Test
    fun testReplaceAttrs() {
        val item = assertIs<Item.Fn>(parseItem("#[old] fn f() {}"))
        val newAttrs = assertIs<Item.Fn>(parseItem("#[new] fn g() {}")).attrs

        val replacement = item.replaceAttrs(newAttrs)
        val updated = assertIs<Item.Fn>(replacement.item)

        assertPath(replacement.oldAttrs.single().path(), "old")
        assertPath(updated.attrs.single().path(), "new")
        assertPath(item.attrs.single().path(), "old")
    }

    @Test
    fun testDeriveInputItemConversions() {
        val structInput = parseDeriveInput("pub struct S;")
        val structItem = assertIs<Item.Struct>(from(structInput))
        assertEquals("S", structItem.ident.toString())
        assertIs<Visibility.Public>(structItem.vis)
        assertIs<Data.Struct>(from(structItem).data)

        val enumInput = parseDeriveInput("enum E { A }")
        val enumItem = assertIs<Item.Enum>(from(enumInput))
        assertEquals("E", enumItem.ident.toString())
        assertEquals(1, enumItem.variants.size)
        assertIs<Data.Enum>(from(enumItem).data)

        val unionInput = parseDeriveInput("union U { x: i32 }")
        val unionItem = assertIs<Item.Union>(from(unionInput))
        assertEquals("U", unionItem.ident.toString())
        assertEquals(1, unionItem.fields.named.size)
        assertIs<Data.Union>(from(unionItem).data)
    }

    @Test
    fun testUseItemKeepsLeadingColonNestedTreeAndSemicolon() {
        val item = assertIs<Item.Use>(parseItem("pub use ::alloc::collections::{HashMap as Map, HashSet, *};"))

        assertIs<Visibility.Public>(item.vis)
        assertNotNull(item.leadingColon)
        val alloc = assertIs<UseTree.Path>(item.tree)
        assertEquals("alloc", alloc.ident.toString())
        assertNotNull(alloc.colon2Token)
        val collections = assertIs<UseTree.Path>(assertNotNull(alloc.tree))
        assertEquals("collections", collections.ident.toString())
        assertNotNull(collections.colon2Token)
        val group = assertIs<UseTree.Group>(assertNotNull(collections.tree))
        val imports = group.items.toList()
        val map = assertIs<UseTree.Name>(imports[0])
        assertEquals("HashMap", map.ident.toString())
        assertEquals("Map", assertNotNull(map.rename).ident.toString())
        val set = assertIs<UseTree.Name>(imports[1])
        assertEquals("HashSet", set.ident.toString())
        assertNull(set.rename)
        assertIs<UseTree.Glob>(imports[2])

        val tokens = TokenStream.new()
        item.toTokens(tokens)
        assertTrue(tokens.toString().endsWith(";"))
    }

    @Test
    fun testUseItemGroupCrateRootFallsBackToVerbatim() {
        val item = assertIs<Item.Verbatim>(parseItem("use {::foo};"))

        assertEquals("use { :: foo } ;", item.tokens.toString())
    }

    @Test
    fun testFunctionSignatureBareVariadic() {
        val item = assertIs<Item.Fn>(parseItem("""unsafe extern "C" fn call(#[cold] arg: u8, ...) {}"""))

        assertNotNull(item.sig.unsafety)
        assertEquals("C", assertNotNull(item.sig.abi?.name).value())
        val arg = assertIs<FnArg.Typed>(item.sig.inputs.single())
        assertEquals(1, arg.patType.attrs.size)
        assertNull(item.sig.variadic?.pat)
        assertNotNull(item.sig.variadic?.dots)
    }

    @Test
    fun testFunctionSignatureNamedVariadic() {
        val item = assertIs<Item.Fn>(parseItem("fn call(#[cold] args: ...,) {}"))

        assertTrue(item.sig.inputs.isEmpty())
        val variadic = assertNotNull(item.sig.variadic)
        assertEquals(1, variadic.attrs.size)
        val pat = assertIs<Pat.Ident>(assertNotNull(variadic.pat).pat)
        assertEquals("args", pat.ident.toString())
        assertNotNull(variadic.comma)
    }

    @Test
    fun testFunctionInnerAttributes() {
        val item = assertIs<Item.Fn>(parseItem("#[outer] fn f() { #![inner] }"))

        assertEquals(2, item.attrs.size)
        assertIs<AttrStyle.Outer>(item.attrs[0].style)
        assertPath(item.attrs[0].path(), "outer")
        assertIs<AttrStyle.Inner>(item.attrs[1].style)
        assertPath(item.attrs[1].path(), "inner")
        assertTrue(assertNotNull(item.block).stmts.isEmpty())
    }

    @Test
    fun testImplFunctionAttributesVisibilityAndDefaultness() {
        val item = assertIs<Item.Impl>(parseItem("impl Trait for Ty { #[outer] pub default fn f() { #![inner] } }"))
        val fn = assertIs<ImplItem.Fn>(item.items.single())

        assertEquals(2, fn.attrs.size)
        assertIs<AttrStyle.Outer>(fn.attrs[0].style)
        assertPath(fn.attrs[0].path(), "outer")
        assertIs<AttrStyle.Inner>(fn.attrs[1].style)
        assertPath(fn.attrs[1].path(), "inner")
        assertIs<Visibility.Public>(fn.vis)
        assertNotNull(fn.defaultness)
        assertEquals("f", fn.sig.ident.toString())
        assertTrue(fn.block.stmts.isEmpty())
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

    private fun parseDeriveInput(source: String): DeriveInput =
        parseStr(DeriveInputParse, source).getOrThrow()

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
