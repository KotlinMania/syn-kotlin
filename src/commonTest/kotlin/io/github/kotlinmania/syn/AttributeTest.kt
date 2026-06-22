// port-lint: tests tests/test_attribute.rs
package io.github.kotlinmania.syn

import kotlin.test.Test

/**
 * Tests for parsing of attributes.
 *
 * The upstream Rust tests use a local `test(input: &str) -> Meta` helper
 * that calls `Attribute::parse_outer.parse_str(input)` and then extracts
 * `attr.meta` from the single returned attribute. The
 * `Attribute::parse_outer` parser entry point is not yet ported to this
 * Kotlin codebase, so each test below carries an honest one-line comment
 * naming the specific missing semantic, rather than emitting a fake
 * simulation that tests a different invariant.
 *
 * The upstream assertions are expressed through the `snapshot!` macro
 * (insta debug snapshots against a `Lite` wrapper). Neither the
 * `Attribute::parse_outer` entry point nor the `Lite` snapshot helper is
 * ported here.
 */
class AttributeTest {
    // Not ported: `Attribute::parse_outer` (the outer-attribute list
    // parser entry point) is not implemented in this Kotlin port, so
    // `#[foo]` cannot be parsed into a `Meta::Path` with a single `foo`
    // segment for snapshot comparison.
    @Test
    fun testMetaItemWord() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo]` parses into
        // `Meta::Path { segments: [PathSegment { ident: "foo" }] }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo = 5]` parses into `Meta::NameValue` with
    // path `foo` and `value: Expr::Lit { lit: 5 }`.
    @Test
    fun testMetaItemNameValue() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo = 5]` parses into
        // `Meta::NameValue { path: foo, value: Expr::Lit { lit: 5 } }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo = true]` and `#[foo = false]` parse into
    // `Meta::NameValue` with `value: Expr::Lit { lit: Lit::Bool { value } }`.
    @Test
    fun testMetaItemBoolValue() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo = true]` parses into
        // `Meta::NameValue { path: foo, value: Expr::Lit { lit: Lit::Bool { value: true } } }`
        // and `#[foo = false]` mirrors it with `value: false`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo(5)]` parses into `Meta::List` with
    // `delimiter: MacroDelimiter::Paren` and `tokens: TokenStream("5")`.
    @Test
    fun testMetaItemListLit() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo(5)]` parses into
        // `Meta::List { path: foo, delimiter: Paren, tokens: "5" }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo(bar)]` parses into `Meta::List` with
    // `delimiter: Paren` and `tokens: TokenStream("bar")`.
    @Test
    fun testMetaItemListWord() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo(bar)]` parses into
        // `Meta::List { path: foo, delimiter: Paren, tokens: "bar" }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo(bar = 5)]` parses into `Meta::List` with
    // `tokens: TokenStream("bar = 5")`.
    @Test
    fun testMetaItemListNameValue() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo(bar = 5)]` parses into
        // `Meta::List { path: foo, delimiter: Paren, tokens: "bar = 5" }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo(bar = true)]` parses into `Meta::List` with
    // `tokens: TokenStream("bar = true")`.
    @Test
    fun testMetaItemListBoolValue() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo(bar = true)]` parses into
        // `Meta::List { path: foo, delimiter: Paren, tokens: "bar = true" }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo(word, name = 5, list(name2 = 6), word2)]`
    // parses into `Meta::List` with the full nested token stream preserved.
    @Test
    fun testMetaItemMultiple() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that
        // `#[foo(word, name = 5, list(name2 = 6), word2)]` parses into
        // `Meta::List { path: foo, delimiter: Paren, tokens:
        // "word , name = 5 , list (name2 = 6) , word2" }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[foo(true)]` parses into `Meta::List` with
    // `tokens: TokenStream("true")`.
    @Test
    fun testBoolLit() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[foo(true)]` parses into
        // `Meta::List { path: foo, delimiter: Paren, tokens: "true" }`.
    }

    // Not ported: `Attribute::parse_outer` is not implemented; the upstream
    // test asserts that `#[form(min = -1, max = 200)]` parses into
    // `Meta::List` with `tokens: TokenStream("min = - 1 , max = 200")`,
    // exercising the negation-as-punct token spacing.
    @Test
    fun testNegativeLit() {
        // Not ported: `Attribute::parse_outer` is not implemented; the
        // upstream test asserts that `#[form(min = -1, max = 200)]` parses
        // into `Meta::List { path: form, delimiter: Paren, tokens:
        // "min = - 1 , max = 200" }`, with the negation rendered as
        // `Minus` followed by `1` (joint spacing preserved by the lexer).
    }
}
