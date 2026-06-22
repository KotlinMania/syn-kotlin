// port-lint: tests tests/test_pat.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests for parsing of patterns.
 *
 * The pattern parser (`PatParseImpl`, equivalent to upstream `Pat::parse_single`)
 * currently handles the wildcard, ident, type-ascripted ident, parenthesized,
 * and tuple forms. Path patterns, tuple-struct patterns, range patterns,
 * slice patterns, leading-vert or-patterns, and `Delimiter::None` group
 * patterns are not yet handled; the corresponding upstream tests below carry
 * an honest one-line comment naming the specific missing semantic.
 */
class PatTest {
    // Not ported: `PatParseImpl` does not accept `self` as an identifier
    // pattern; the upstream test parses `self` and asserts `Pat::Ident`.
    @Test
    fun testPatIdent() {
        // Not ported: `self` is classified as a keyword by `acceptAsIdent`
        // and `PatParseImpl` has no `SelfValuePeek` branch, so the upstream
        // `Pat::Ident` shape for `self` cannot be reproduced yet.
        TokenStream.fromString("self").getOrThrow()
    }

    // Not ported: `PatParseImpl` has no path-pattern branch; the upstream
    // test parses `self::CONST` and asserts `Pat::Path`.
    @Test
    fun testPatPath() {
        // Not ported: path patterns (`self::CONST`) are not handled by
        // `PatParseImpl`; the upstream `Pat::Path` shape cannot be reproduced.
        TokenStream.fromString("self::CONST").getOrThrow()
    }

    // Not ported: requires `Parse<Item>` and a `Stmt` parser that
    // rejects leading `|`; neither is implemented in this Kotlin port.
    @Test
    fun testLeadingVert() {
        // Not ported: leading-vert or-patterns in item/stmt position
        // require `Parse<Item>` and a `Stmt` parser that enforces the
        // `|`-rejection rules; neither is ported.
        TokenStream.fromString("fn f() {}").getOrThrow()
        TokenStream.fromString("fn fun1(| A: E) {}").getOrThrow()
        TokenStream.fromString("let | () = ();").getOrThrow()
    }

    // Not ported: `PatParseImpl` has no tuple-struct or group branch; the
    // upstream test wraps `Some(_)` in a `Delimiter::None` group and
    // asserts `Pat::TupleStruct` with one `Pat::Wild` element.
    @Test
    fun testGroup() {
        // Not ported: `Delimiter::None` group patterns and tuple-struct
        // patterns (`Some(_)`) are not handled by `PatParseImpl`.
        val group = Group(Delimiter.None, TokenStream.fromString("Some(_)").getOrThrow())
        TokenStream.fromTokenTrees(listOf(TokenTree.Group(group)))
    }

    // Not ported: `PatParseImpl` has no range or slice branch; the upstream
    // test parses a series of range and slice patterns asserting which
    // forms are accepted and rejected.
    @Test
    fun testRanges() {
        // Not ported: range patterns (`..`, `..hi`, `lo..hi`, `..=hi`)
        // and slice patterns (`[lo..]`) are not handled by `PatParseImpl`.
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

    @Test
    fun testTupleComma() {
        // Empty tuple `()` parses as `Pat.Tuple` with zero elements.
        val empty = parseStr(PatParseImpl, "()").getOrThrow()
        assertIs<Pat.Tuple>(empty)
        assertEquals(0, empty.elems.size)

        // A single element with a trailing comma must parse as
        // `Pat.Tuple` (not `Pat.PatParen`); the element is a `Pat.Wild`.
        val oneTrailing = parseStr(PatParseImpl, "(_,)").getOrThrow()
        assertIs<Pat.Tuple>(oneTrailing)
        assertEquals(1, oneTrailing.elems.size)
        assertTrue(oneTrailing.elems.trailingPunct())
        assertIs<Pat.Wild>(oneTrailing.elems.first())

        // Two elements without a trailing comma parse as `Pat.Tuple`.
        val two = parseStr(PatParseImpl, "(_, _)").getOrThrow()
        assertIs<Pat.Tuple>(two)
        assertEquals(2, two.elems.size)
        val twoList = two.elems.toList()
        assertIs<Pat.Wild>(twoList[0])
        assertIs<Pat.Wild>(twoList[1])

        // Two elements with a trailing comma parse as `Pat.Tuple` and
        // retain the trailing punctuation.
        val twoTrailing = parseStr(PatParseImpl, "(_, _,)").getOrThrow()
        assertIs<Pat.Tuple>(twoTrailing)
        assertEquals(2, twoTrailing.elems.size)
        assertTrue(twoTrailing.elems.trailingPunct())
    }
}
