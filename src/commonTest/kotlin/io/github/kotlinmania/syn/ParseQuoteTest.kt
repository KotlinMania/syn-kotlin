// port-lint: tests tests/test_parse_quote.rs
package io.github.kotlinmania.syn

import kotlin.test.Test

/**
 * Tests for `parseQuote`-style construction of syntax tree nodes from
 * token streams.
 *
 * The upstream Rust tests use the `parse_quote!` macro, which expands to a
 * type-inferred call into the `Parse` / `ParseQuote` machinery. In Kotlin,
 * the equivalent is [parseQuote] paired with a [ParseQuote] implementation
 * for the target type, or [parseStr] when a default [Parse] exists.
 *
 * Every test in this upstream file targets a type whose `ParseQuote`
 * special-case implementation is not yet ported. Each is documented inline
 * with an honest one-line comment naming the missing semantic, rather than
 * emitting a fake simulation that tests a different invariant.
 */
class ParseQuoteTest {
    @Test
    fun testAttribute() {
        // Attribute parse-quote special case (outer `#[...]` and inner `#![...]`)
        // is not yet ported as a ParseQuote implementation in Attr.kt; the
        // upstream test asserts structural equality against a snapshot which
        // requires that special-case parser to round-trip the bracket/pound
        // token shape.
    }

    @Test
    fun testField() {
        // Field parse-quote special case (named or unnamed struct field with
        // optional visibility, colon, and type) is not yet ported as a
        // ParseQuote implementation in Data.kt; the upstream test asserts the
        // Field::Parse impl that lifts visibility + type path from token
        // stream shape that the port does not yet expose.
    }

    @Test
    fun testPat() {
        // Pat parse-quote special case (parseMultiWithLeadingVert, which
        // accepts a leading `|` before the pattern alternatives) is not yet
        // ported as a ParseQuote implementation in Pat.kt; the upstream test
        // parses `Some(false) | None` which depends on the multi-vert Pat
        // parser entry point.
    }

    @Test
    fun testPunctuated() {
        // Punctuated<Lit, Or> parse-quote special case is not yet ported:
        // the internal Punctuated<T, P> requires T : ToTokens, but the
        // sealed Lit class does not yet implement ToTokens in Lit.kt, so
        // Punctuated.parseTerminatedWith cannot accept LitParse as the
        // element parser.
    }

    @Test
    fun testPunctuatedTrailing() {
        // Punctuated<Lit, Or> trailing-punctuation variant shares the same
        // missing ToTokens bound on Lit as testPunctuated; see that test's
        // comment for the specific semantic.
    }

    @Test
    fun testVecStmt() {
        // List<Stmt> parse-quote special case (Block.parseWithin, which
        // parses a sequence of statements the same way a block body does)
        // is not yet ported as a ParseQuote implementation in Stmt.kt; the
        // upstream test parses `let _; true` into a two-element statement
        // list which requires that block-within statement parser.
    }
}
