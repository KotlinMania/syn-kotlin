// port-lint: tests tests/test_item.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test

/**
 * Tests for parsing of items.
 *
 * The upstream Rust tests rely on the `snapshot!` macro, which expands to
 * `syn::parse_quote!` followed by `insta::assert_debug_snapshot!` against a
 * `Lite` debug wrapper. Neither the `ItemParse` / `Parse<Item>` entry point,
 * the `ItemTraitParse` / `Parse<ItemTrait>` entry point, nor the `Lite`
 * snapshot helper are ported to this Kotlin codebase yet. Each test below
 * carries an honest one-line comment naming the specific missing semantic,
 * rather than emitting a fake simulation that tests a different invariant.
 *
 * The Rust source also constructs token streams via `quote!` and
 * `TokenStream::from_iter`; the equivalent constructors
 * ([TokenStream.fromString] and [TokenStream.fromTokenTrees]) are available
 * here, but without a `Parse<Item>` implementation they cannot be driven
 * through to a structural assertion.
 */
class ItemTest {
    // Not ported: `Parse<Item>` (the top-level item parser entry point) is
    // not implemented in this Kotlin port, so the interpolated attribute
    // token stream `$attr fn f() {}` cannot be parsed into an `Item.Fn` for
    // snapshot comparison.
    @Test
    fun testMacroVariableAttr() {
        // Not ported: `Parse<Item>` is not implemented; the upstream test
        // builds a token stream with a `Delimiter::None` group containing
        // `#[test]` and parses it into `Item::Fn` with one outer `test`
        // attribute, asserting the attribute path and signature shape via
        // `insta::assert_debug_snapshot!`.
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
        // A `Parse<Item>` implementation would turn `tokens` into an
        // `Item.Fn` whose `attrs` has one outer `test` attribute, `vis` is
        // `Visibility.Inherited`, `ident` is `"f"`, `generics` is empty, and
        // `block` is an empty `Block`.
        tokens.toString()
    }

    // Not ported: `Parse<Item>` is not implemented; the upstream test parses
    // `impl ! {}` into `Item::Impl` with `self_ty: Type::Never`, and asserts
    // that `impl !Trait {}` fails with "inherent impls cannot be negative",
    // then parses `impl !Trait for T {}` as a negative trait impl.
    @Test
    fun testNegativeImpl() {
        // Not ported: `Parse<Item>` is not implemented; the upstream test
        // parses `impl ! {}` into an `Item::Impl` whose `self_ty` is
        // `Type::Never`, asserts that `impl !Trait {}` fails with the
        // specific error message "inherent impls cannot be negative", and
        // parses `impl !Trait for T {}` as a negative trait impl with
        // `trait_` polarity `Some(!)` and `self_ty` `Type::Path(T)`.
        TokenStream.fromString("impl ! {}").getOrThrow()
        TokenStream.fromString("impl !Trait {}").getOrThrow()
        TokenStream.fromString("impl !Trait for T {}").getOrThrow()
    }

    // Not ported: `Parse<Item>` is not implemented; the upstream test builds
    // an `impl $trait for $ty {}` token stream with two `Delimiter::None`
    // groups and parses it into an `Item::Impl` with a non-negative trait
    // path and a `Type::Group` self type.
    @Test
    fun testMacroVariableImpl() {
        // Not ported: `Parse<Item>` is not implemented; the upstream test
        // parses an `impl $trait for $ty {}` interpolation into
        // `Item::Impl` with `trait_: Some((None, Path(Trait)))` and
        // `self_ty: Type::Group { elem: Type::Path { path: Path(Type) } }`.
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
        tokens.toString()
    }

    // Not ported: `Parse<ItemTrait>` is not implemented; the upstream test
    // parses four trait declarations (`trait Trait where {}`,
    // `trait Trait: where {}`, `trait Trait: Sized where {}`,
    // `trait Trait: Sized + where {}`) and asserts the `colon_token`,
    // `supertraits`, and `where_clause` shape of each via snapshot.
    @Test
    fun testSupertraits() {
        // Not ported: `Parse<ItemTrait>` is not implemented; the upstream
        // test asserts that `trait Trait where {}` has a `where_clause` but
        // no `colon_token`, `trait Trait: where {}` has both, `trait Trait:
        // Sized where {}` adds a single `Sized` supertrait, and `trait
        // Trait: Sized + where {}` adds a trailing `+` punctuation to the
        // supertraits list.
        TokenStream.fromString("trait Trait where {}").getOrThrow()
        TokenStream.fromString("trait Trait: where {}").getOrThrow()
        TokenStream.fromString("trait Trait: Sized where {}").getOrThrow()
        TokenStream.fromString("trait Trait: Sized + where {}").getOrThrow()
    }

    // Not ported: `Parse<ItemTrait>` is not implemented; the upstream test
    // parses `trait Foo { type Bar: ; }` and asserts the associated type
    // item has `ident: "Bar"`, empty `generics`, and a `colon_token` with
    // empty bounds.
    @Test
    fun testTypeEmptyBounds() {
        // Not ported: `Parse<ItemTrait>` is not implemented; the upstream
        // test asserts that `trait Foo { type Bar: ; }` parses into an
        // `ItemTrait` whose `items` contains a single `TraitItem::Type`
        // with `ident: "Bar"`, `colon_token: Some`, and no bounds.
        TokenStream.fromString("trait Foo { type Bar: ; }").getOrThrow()
    }

    // Not ported: `Parse<Item>` is not implemented; the upstream test parses
    // `pub default unsafe impl union {}` and asserts it round-trips as
    // `Item::Verbatim` with the exact token span.
    @Test
    fun testImplVisibility() {
        // Not ported: `Parse<Item>` is not implemented; the upstream test
        // asserts that `pub default unsafe impl union {}` parses into
        // `Item::Verbatim("pub default unsafe impl union { }")` because the
        // combination of `pub default` visibility on an impl is not a
        // recognized item shape and falls through to verbatim.
        TokenStream.fromString("pub default unsafe impl union {}").getOrThrow()
    }

    // Not ported: `Parse<Item>` is not implemented; the upstream test parses
    // `impl<T = ()> () {}` and asserts the generic parameter has
    // `eq_token: Some` and `default: Some(Type::Tuple)`.
    @Test
    fun testImplTypeParameterDefaults() {
        // Not ported: `Parse<Item>` is not implemented; the upstream test
        // asserts that `impl<T = ()> () {}` parses into an `Item::Impl`
        // whose `generics.params` contains a single `GenericParam::Type`
        // with `ident: "T"`, `eq_token: Some`, and `default: Some(Type::Tuple)`.
        TokenStream.fromString("impl<T = ()> () {}").getOrThrow()
    }

    // Not ported: `Parse<Item>` is not implemented; the upstream test parses
    // `fn f() -> impl Sized + {}` and asserts the return type is an
    // `impl Trait` bound with a trailing `+` punctuation.
    @Test
    fun testImplTraitTrailingPlus() {
        // Not ported: `Parse<Item>` is not implemented; the upstream test
        // asserts that `fn f() -> impl Sized + {}` parses into an
        // `Item::Fn` whose `sig.output` is `ReturnType::Type(Type::ImplTrait
        // { bounds: [TraitBound(Sized), Token![+]] })`, exercising the
        // trailing-plus-on-impl-trait parser path.
        TokenStream.fromString("fn f() -> impl Sized + {}").getOrThrow()
    }
}
