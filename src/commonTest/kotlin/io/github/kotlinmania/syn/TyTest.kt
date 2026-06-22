// port-lint: tests tests/test_ty.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests for parsing of types.
 *
 * The type parser (`SynTypeParseExpr`, equivalent to upstream
 * `Parse<SynType>`) currently handles the infer (`_`), reference
 * (`&` / `&'lt` / `&mut`), pointer (`*const` / `*mut`), parenthesized,
 * tuple, and path forms. Bare-fn types, `dyn`/`impl Trait` trait-object
 * types, `AngleBracketed` generic-argument disambiguation against
 * `Delimiter::None` groups, `QSelf`-bearing path types, and the
 * `PreciseCapture` `use<...>` bound are not yet handled; the
 * corresponding upstream tests below carry an honest one-line comment
 * naming the specific missing semantic.
 */
class TyTest {
    // Not ported: `SynTypeParseExpr` has no bare-fn branch; the upstream
    // test parses `fn(mut self)` and variants as `Type::BareFn`.
    @Test
    fun testMutSelf() {
        // Not ported: bare-fn types (`fn(...) -> ...`) are not handled by
        // `SynTypeParseExpr`; the upstream `Type::BareFn` shape cannot be
        // reproduced.
        TokenStream.fromString("fn(mut self)").getOrThrow()
        TokenStream.fromString("fn(mut self,)").getOrThrow()
        TokenStream.fromString("fn(mut self: ())").getOrThrow()
        TokenStream.fromString("fn(mut self: ...)").getOrThrow()
        TokenStream.fromString("fn(mut self: mut self)").getOrThrow()
        TokenStream.fromString("fn(mut self::T)").getOrThrow()
    }

    // Not ported: `SynTypeParseExpr` parses a path but does not fold a
    // following `<` into `PathArguments::AngleBracketed` against a
    // `Delimiter::None` group; the upstream test asserts the resulting
    // `Type::Path` with a single `AngleBracketed` generic argument.
    @Test
    fun testMacroVariableType() {
        // Not ported: `AngleBracketed` generic-argument disambiguation
        // against a `Delimiter::None` group (`$ty<T>`) is not handled by
        // `SynTypeParseExpr`.
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("ty").getOrThrow())),
                    TokenTree.Punct(Punct('<', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("T", Span.callSite())),
                    TokenTree.Punct(Punct('>', Spacing.Alone, Span.callSite())),
                ),
            )
        tokens.toString()
    }

    // Not ported: same `AngleBracketed`-vs-group disambiguation gap as
    // `testMacroVariableType`; the upstream test asserts a
    // `GenericArgument::Type(Type::Group)` element.
    @Test
    fun testGroupAngleBrackets() {
        // Not ported: `Option<$ty>` with `$ty` a `Delimiter::None` group
        // requires folding the group into `Type::Group` inside
        // `AngleBracketed` args; not handled by `SynTypeParseExpr`.
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("Option", Span.callSite())),
                    TokenTree.Punct(Punct('<', Spacing.Alone, Span.callSite())),
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("Vec<u8>").getOrThrow())),
                    TokenTree.Punct(Punct('>', Spacing.Alone, Span.callSite())),
                ),
            )
        tokens.toString()
    }

    // Not ported: `SynTypeParseExpr` does not resolve a `Delimiter::None`
    // group followed by `::` into a multi-segment path, nor into a
    // `QSelf`-bearing path; the upstream test asserts both shapes.
    @Test
    fun testGroupColons() {
        // Not ported: `$ty::Item` and `[$ty]::Element` require group
        // resolution plus `QSelf` construction; not handled by
        // `SynTypeParseExpr`.
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("Vec<u8>").getOrThrow())),
                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("Item", Span.callSite())),
                ),
            )
        tokens.toString()
    }

    // Not ported: `SynTypeParseExpr` has no `dyn`/`for`/trait-object
    // branch; the upstream test parses `dyn for<'a> Trait<'a> + 'static`
    // and `dyn 'a + Trait` into `Type::TraitObject`.
    @Test
    fun testTraitObject() {
        // Not ported: `dyn ...` and `for<'a> ...` trait-object types are
        // not handled by `SynTypeParseExpr`.
        TokenStream.fromString("dyn for<'a> Trait<'a> + 'static").getOrThrow()
        TokenStream.fromString("dyn 'a + Trait").getOrThrow()
        TokenStream.fromString("for<'a> dyn Trait<'a>").getOrThrow()
        TokenStream.fromString("dyn for<'a> 'a + Trait").getOrThrow()
    }

    // Not ported: `SynTypeParseExpr` has no `impl Trait +` / `Trait +`
    // branch with trailing `+`; the upstream test asserts the bound
    // list shape including the trailing punctuation.
    @Test
    fun testTrailingPlus() {
        // Not ported: `impl Trait +`, `dyn Trait +`, and `Trait +` with
        // trailing `+` are not handled by `SynTypeParseExpr`.
        TokenStream.fromString("impl Trait +").getOrThrow()
        TokenStream.fromString("dyn Trait +").getOrThrow()
        TokenStream.fromString("Trait +").getOrThrow()
    }

    @Test
    fun testTupleComma() {
        // Empty tuple `()` parses as `SynType.Tuple` with zero elements.
        val empty = parseStr(SynTypeParseExpr, "()").getOrThrow()
        assertIs<SynType.Tuple>(empty)
        assertEquals(0, empty.elems.size)

        // A single element with a trailing comma must parse as
        // `SynType.Tuple` (not `SynType.Paren`); the element is a
        // `SynType.Infer`.
        val oneTrailing = parseStr(SynTypeParseExpr, "(_,)").getOrThrow()
        assertIs<SynType.Tuple>(oneTrailing)
        assertEquals(1, oneTrailing.elems.size)
        assertTrue(oneTrailing.elems.trailingPunct())
        assertIs<SynType.Infer>(oneTrailing.elems.first())

        // Two elements without a trailing comma parse as `SynType.Tuple`.
        val two = parseStr(SynTypeParseExpr, "(_, _)").getOrThrow()
        assertIs<SynType.Tuple>(two)
        assertEquals(2, two.elems.size)
        val twoList = two.elems.toList()
        assertIs<SynType.Infer>(twoList[0])
        assertIs<SynType.Infer>(twoList[1])

        // Two elements with a trailing comma parse as `SynType.Tuple`
        // and retain the trailing punctuation.
        val twoTrailing = parseStr(SynTypeParseExpr, "(_, _,)").getOrThrow()
        assertIs<SynType.Tuple>(twoTrailing)
        assertEquals(2, twoTrailing.elems.size)
        assertTrue(twoTrailing.elems.trailingPunct())
    }

    // Not ported: `SynTypeParseExpr` has no `impl ... use<...>` branch;
    // the upstream test parses `impl Sized + use<'_, 'a, A, Test>` into
    // `Type::ImplTrait` with a `PreciseCapture` bound.
    @Test
    fun testImplTraitUse() {
        // Not ported: `impl Trait + use<...>` with a `PreciseCapture`
        // bound is not handled by `SynTypeParseExpr`.
        TokenStream.fromString("impl Sized + use<'_, 'a, A, Test>").getOrThrow()
        TokenStream.fromString("impl Sized + use<'_,>").getOrThrow()
    }
}
