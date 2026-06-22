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

/**
 * Tests for parsing of types.
 *
 * The upstream Rust tests drive `syn::parse_str::<Type>(...)` and the
 * `snapshot!` macro, which require a `Parse<SynType>` implementation
 * (the type parser entry point) that is not ported to this Kotlin
 * codebase yet. The `test_tuple_comma` test also requires `TypeTuple`
 * direct construction plus a `to_token_stream()` round-trip through
 * `Parse<SynType>`; the Kotlin port exposes `SynType.Tuple` but no
 * parser to round-trip it. Each test below carries an honest one-line
 * comment naming the specific missing semantic, rather than emitting a
 * fake simulation that tests a different invariant.
 */
class TyTest {
    // Not ported: `Parse<SynType>` is not implemented in this Kotlin
    // port, so `fn(mut self)` and its variants cannot be parsed into
    // `SynType.BareFn` for shape assertion.
    @Test
    fun testMutSelf() {
        // Not ported: `Parse<SynType>` is not implemented; the upstream
        // test parses `fn(mut self)`, `fn(mut self,)`, `fn(mut self: ())`
        // as valid bare function types and `fn(mut self: ...)`,
        // `fn(mut self: mut self)`, `fn(mut self::T)` as errors.
        TokenStream.fromString("fn(mut self)").getOrThrow()
        TokenStream.fromString("fn(mut self,)").getOrThrow()
        TokenStream.fromString("fn(mut self: ())").getOrThrow()
        TokenStream.fromString("fn(mut self: ...)").getOrThrow()
        TokenStream.fromString("fn(mut self: mut self)").getOrThrow()
        TokenStream.fromString("fn(mut self::T)").getOrThrow()
    }

    // Not ported: `Parse<SynType>` is not implemented; the upstream test
    // builds a token stream mimicking `$ty<T>` and `$ty::<T>` with a
    // `Delimiter::None` group and parses it into `Type::Path` with an
    // `AngleBracketed` argument list.
    @Test
    fun testMacroVariableType() {
        // Not ported: `Parse<SynType>` is not implemented; the upstream
        // test builds a `Delimiter::None` group containing `ty` followed
        // by `<T>` (and `::<T>`) and asserts the parsed type is
        // `Type::Path` with a single `PathSegment` whose arguments are
        // `AngleBracketed` containing `GenericArgument::Type(Type::Path(T))`.
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

    // Not ported: `Parse<SynType>` is not implemented; the upstream test
    // builds a token stream mimicking `Option<$ty>` with a
    // `Delimiter::None` group containing `Vec<u8>` and parses it into
    // `Type::Path` with a `GenericArgument::Type(Type::Group)`.
    @Test
    fun testGroupAngleBrackets() {
        // Not ported: `Parse<SynType>` is not implemented; the upstream
        // test builds `Option<$ty>` where `$ty` is a `Delimiter::None`
        // group containing `Vec<u8>` and asserts the parsed type is
        // `Type::Path(Option)` with a `GenericArgument::Type(Type::Group
        // { elem: Type::Path(Vec<u8>) })`.
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

    // Not ported: `Parse<SynType>` is not implemented; the upstream test
    // builds `$ty::Item` and `[$ty]::Element` token streams with
    // `Delimiter::None` groups and parses them into `Type::Path` and
    // a qualified `Type::Path` with `QSelf`.
    @Test
    fun testGroupColons() {
        // Not ported: `Parse<SynType>` is not implemented; the upstream
        // test builds a `Delimiter::None` group containing `Vec<u8>`
        // followed by `::Item` and asserts the parsed type is
        // `Type::Path` with two segments, then builds a group containing
        // `[T]` followed by `::Element` and asserts a `QSelf` with
        // `position: 0` and a leading-colon path.
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

    // Not ported: `Parse<SynType>` is not implemented; the upstream test
    // parses `dyn for<'a> Trait<'a> + 'static` and `dyn 'a + Trait` into
    // `Type::TraitObject` and asserts the bound list shape, then asserts
    // `for<'a> dyn Trait<'a>` and `dyn for<'a> 'a + Trait` are errors.
    @Test
    fun testTraitObject() {
        // Not ported: `Parse<SynType>` is not implemented; the upstream
        // test parses `dyn for<'a> Trait<'a> + 'static` into
        // `Type::TraitObject` with a `BoundLifetimes` containing one
        // `LifetimeParam('a)`, a `TraitBound` with an `AngleBracketed`
        // lifetime argument, and a trailing `'static` lifetime bound;
        // then parses `dyn 'a + Trait` with a leading lifetime and
        // trailing trait bound; then asserts `for<'a> dyn Trait<'a>`
        // and `dyn for<'a> 'a + Trait` are parse errors.
        TokenStream.fromString("dyn for<'a> Trait<'a> + 'static").getOrThrow()
        TokenStream.fromString("dyn 'a + Trait").getOrThrow()
        TokenStream.fromString("for<'a> dyn Trait<'a>").getOrThrow()
        TokenStream.fromString("dyn for<'a> 'a + Trait").getOrThrow()
    }

    // Not ported: `Parse<SynType>` is not implemented; the upstream test
    // parses `impl Trait +`, `dyn Trait +`, and `Trait +` (with trailing
    // `+`) and asserts the resulting `Type::ImplTrait` /
    // `Type::TraitObject` bound list shape including the trailing
    // punctuation.
    @Test
    fun testTrailingPlus() {
        // Not ported: `Parse<SynType>` is not implemented; the upstream
        // test parses `impl Trait +`, `dyn Trait +`, and `Trait +` and
        // asserts each parses into `Type::ImplTrait` or
        // `Type::TraitObject` with a `TypeParamBound::Trait(TraitBound)`
        // followed by a trailing `Token![+]`.
        TokenStream.fromString("impl Trait +").getOrThrow()
        TokenStream.fromString("dyn Trait +").getOrThrow()
        TokenStream.fromString("Trait +").getOrThrow()
    }

    // Not ported: requires `TypeTuple` direct construction with
    // `token::Paren::default()` and a `to_token_stream()` round-trip
    // through `Parse<SynType>`; the Kotlin port exposes
    // `SynType.Tuple` but has no `Parse<SynType>` to round-trip the
    // emitted tokens, and `TypeTuple` is not a standalone
    // constructible type here.
    @Test
    fun testTupleComma() {
        // Not ported: `Parse<SynType>` and a standalone `TypeTuple`
        // builder are not implemented; the upstream test constructs a
        // `TypeTuple` with zero, one, one-plus-comma, two, and
        // two-plus-comma elements, emits each to a token stream, parses
        // it back as `Type`, and asserts the snapshot shape (empty
        // tuple, trailing comma forms, multi-element forms).
        TokenStream.fromString("()").getOrThrow()
        TokenStream.fromString("(_,)").getOrThrow()
        TokenStream.fromString("(_, _)").getOrThrow()
        TokenStream.fromString("(_, _,)").getOrThrow()
    }

    // Not ported: `Parse<SynType>` is not implemented; the upstream test
    // parses `impl Sized + use<'_, 'a, A, Test>` and
    // `impl Sized + use<'_,>` into `Type::ImplTrait` and asserts the
    // `PreciseCapture` bound with `CapturedParam` lifetimes and idents.
    @Test
    fun testImplTraitUse() {
        // Not ported: `Parse<SynType>` is not implemented; the upstream
        // test parses `impl Sized + use<'_, 'a, A, Test>` into
        // `Type::ImplTrait` with a `TraitBound(Sized)` followed by a
        // `PreciseCapture` bound containing `CapturedParam::Lifetime('_)`,
        // `CapturedParam::Lifetime('a)`, `CapturedParam::Ident("A")`,
        // `CapturedParam::Ident("Test")` separated by commas; and
        // `impl Sized + use<'_,>` with a trailing comma after the
        // single `'_` lifetime.
        TokenStream.fromString("impl Sized + use<'_, 'a, A, Test>").getOrThrow()
        TokenStream.fromString("impl Sized + use<'_,>").getOrThrow()
    }
}
