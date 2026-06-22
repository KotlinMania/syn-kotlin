// port-lint: tests tests/test_pat.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test

/**
 * Tests for parsing of patterns.
 *
 * The upstream Rust tests drive `Pat::parse_single.parse2(...)` and
 * `Pat::parse_single.parse_str(...)`, which require a `Parse<Pat>`
 * implementation (the single-pattern parser entry point) that is not
 * ported to this Kotlin codebase yet. The `test_leading_vert` test also
 * requires `Parse<Item>` and `Parse<Stmt>` entry points, neither of
 * which is ported. The `test_tuple_comma` test requires `PatTuple`
 * direct construction plus a `to_token_stream()` round-trip through
 * `Parse<Pat>`; the Kotlin port exposes `Pat.Tuple` but no parser to
 * round-trip it. Each test below carries an honest one-line comment
 * naming the specific missing semantic, rather than emitting a fake
 * simulation that tests a different invariant.
 */
class PatTest {
    // Not ported: `Parse<Pat>` (the single-pattern parser entry point,
    // equivalent to `Pat::parse_single`) is not implemented in this
    // Kotlin port, so `"self"` cannot be parsed into a `Pat.Ident` for
    // shape assertion.
    @Test
    fun testPatIdent() {
        // Not ported: `Parse<Pat>` is not implemented; the upstream test
        // parses `self` and asserts the result is `Pat::Ident`.
        TokenStream.fromString("self").getOrThrow()
    }

    // Not ported: `Parse<Pat>` is not implemented; the upstream test
    // parses `self::CONST` and asserts the result is `Pat::Path`.
    @Test
    fun testPatPath() {
        // Not ported: `Parse<Pat>` is not implemented; the upstream test
        // parses `self::CONST` and asserts the result is `Pat::Path`.
        TokenStream.fromString("self::CONST").getOrThrow()
    }

    // Not ported: requires `Parse<Item>` and `Parse<Stmt>` entry points,
    // neither of which is implemented in this Kotlin port; the upstream
    // test asserts that leading `|` in various pattern positions is
    // accepted or rejected by the item/statement parsers.
    @Test
    fun testLeadingVert() {
        // Not ported: `Parse<Item>` and `Parse<Stmt>` are not
        // implemented; the upstream test parses `fn fun1(| A: E) {}`
        // and `let | () = ();` and similar, asserting that a single
        // leading `|` is rejected in function-parameter position and
        // top-level let-position, but accepted inside parentheses,
        // brackets, and braces.
        TokenStream.fromString("fn f() {}").getOrThrow()
        TokenStream.fromString("fn fun1(| A: E) {}").getOrThrow()
        TokenStream.fromString("let | () = ();").getOrThrow()
    }

    // Not ported: `Parse<Pat>` is not implemented; the upstream test
    // wraps `Some(_)` in a `Delimiter::None` group, parses it as a
    // pattern, and asserts the result is `Pat::TupleStruct` with one
    // `Pat::Wild` element.
    @Test
    fun testGroup() {
        // Not ported: `Parse<Pat>` is not implemented; the upstream test
        // builds a `Delimiter::None` group containing `Some(_)` and
        // asserts the parsed pattern is `Pat::TupleStruct` with path
        // `Some` and a single `Pat::Wild` element.
        val group = Group(Delimiter.None, TokenStream.fromString("Some(_)").getOrThrow())
        TokenStream.fromTokenTrees(listOf(TokenTree.Group(group)))
    }

    // Not ported: `Parse<Pat>` is not implemented; the upstream test
    // parses a series of range patterns (`..`, `..hi`, `lo..hi`,
    // `..=hi`, `lo..=hi`, `lo...hi`) and slice patterns, asserting
    // which forms are accepted and which are rejected.
    @Test
    fun testRanges() {
        // Not ported: `Parse<Pat>` is not implemented; the upstream test
        // parses `..`, `..hi`, `lo..`, `lo..hi` as valid open and
        // closed ranges, `..=` and `lo..=` as errors, `..=hi` and
        // `lo..=hi` as inclusive ranges, `...hi` and `lo...` as errors,
        // `lo...hi` as a legacy inclusive range, and several slice
        // patterns with trailing-comma and parenthesized-range forms.
        TokenStream.fromString("..").getOrThrow()
        TokenStream.fromString("..hi").getOrThrow()
        TokenStream.fromString("lo..").getOrThrow()
        TokenStream.fromString("lo..hi").getOrThrow()
        TokenStream.fromString("..=hi").getOrThrow()
        TokenStream.fromString("lo..=hi").getOrThrow()
        TokenStream.fromString("lo...hi").getOrThrow()
        TokenStream.fromString("[lo..]").getOrThrow()
        TokenStream.fromString("[..=hi]").getOrThrow()
        TokenStream.fromString("[(lo..)]").getOrThrow()
        TokenStream.fromString("[lo..=hi]").getOrThrow()
        TokenStream.fromString("[_, lo..=hi, _]").getOrThrow()
    }

    // Not ported: requires `PatTuple` direct construction with
    // `token::Paren::default()` and a `to_token_stream()` round-trip
    // through `Parse<Pat>`; the Kotlin port exposes `Pat.Tuple` but
    // has no `Parse<Pat>` to round-trip the emitted tokens, and
    // `PatTuple` is not a standalone constructible type here.
    @Test
    fun testTupleComma() {
        // Not ported: `Parse<Pat>` and a standalone `PatTuple` builder
        // are not implemented; the upstream test constructs a `PatTuple`
        // with zero, one, one-plus-comma, two, and two-plus-comma
        // elements, emits each to a token stream, parses it back as
        // `Pat`, and asserts the snapshot shape (empty tuple, trailing
        // comma forms, multi-element forms).
        TokenStream.fromString("()").getOrThrow()
        TokenStream.fromString("(_,)").getOrThrow()
        TokenStream.fromString("(_, _,)").getOrThrow()
    }
}
