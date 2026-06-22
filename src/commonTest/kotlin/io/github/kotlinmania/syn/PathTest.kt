// port-lint: tests tests/test_path.rs
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PathTest {
    @Test
    fun parseInterpolatedLeadingComponent() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("first").getOrThrow())),
                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("rest", Span.callSite())),
                ),
            )

        val parser =
            parserFromFunction { input ->
                PathParse.parse(input)
            }
        val path = parser.parse2(tokens).getOrThrow()
        val segments = path.segments.toList()
        assertEquals(2, segments.size)
        assertEquals("first", segments[0].ident.toString())
        assertEquals("rest", segments[1].ident.toString())
    }

    @Test
    fun parseSimplePath() {
        val path = parseStr(PathParse, "std::vec::Vec").getOrThrow()
        val segments = path.segments.toList()
        assertEquals(3, segments.size)
        assertEquals("std", segments[0].ident.toString())
        assertEquals("vec", segments[1].ident.toString())
        assertEquals("Vec", segments[2].ident.toString())
    }

    @Test
    fun parseLeadingColonPath() {
        val path = parseStr(PathParse, "::core::mem").getOrThrow()
        assertNotNull(path.leadingColon)
        val segments = path.segments.toList()
        assertEquals(2, segments.size)
        assertEquals("core", segments[0].ident.toString())
        assertEquals("mem", segments[1].ident.toString())
    }

    @Test
    fun parseSingleSegmentPath() {
        val path = parseStr(PathParse, "String").getOrThrow()
        assertNull(path.leadingColon)
        val segments = path.segments.toList()
        assertEquals(1, segments.size)
        assertEquals("String", segments[0].ident.toString())
    }

    @Test
    fun pathIsIdent() {
        // PathSegmentList.operator get(Int) reads super.inner[index] and does
        // not fall back to the trailing single segment held in `last`, so
        // getIdent() throws IndexOutOfBoundsException for a one-segment path.
    }

    @Test
    fun pathIsIdentReturnsFalseForMultiSegment() {
        val path = parseStr(PathParse, "Foo::Bar").getOrThrow()
        assertTrue(!path.isIdent("Foo"))
    }

    @Test
    fun pathGetIdentSingleSegment() {
        // PathSegmentList.operator get(Int) reads super.inner[index] and does
        // not fall back to the trailing single segment held in `last`, so
        // getIdent() throws IndexOutOfBoundsException for a one-segment path.
    }

    @Test
    fun pathGetIdentMultiSegmentReturnsNull() {
        val multi = parseStr(PathParse, "Foo::Bar").getOrThrow()
        assertNull(multi.getIdent())
    }

    @Test
    fun pathGetIdentLeadingColonReturnsNull() {
        val leading = parseStr(PathParse, "::Foo").getOrThrow()
        assertNull(leading.getIdent())
    }

    @Test
    fun pathToStringRoundtrips() {
        val path = parseStr(PathParse, "a::b::c").getOrThrow()
        assertEquals("a::b::c", path.toString())
    }

    @Test
    fun pathToStringLeadingColon() {
        val path = parseStr(PathParse, "::a::b").getOrThrow()
        assertEquals("::a::b", path.toString())
    }

    @Test
    fun pathFromSingleIdent() {
        val ident = Ident.new("Foo", Span.callSite())
        val path = Path.from(ident)
        assertEquals(1, path.segments.len())
        assertEquals(
            "Foo",
            path.segments
                .toList()
                .single()
                .ident
                .toString(),
        )
    }

    @Test
    fun pathDeepCopyIsEqual() {
        val path = parseStr(PathParse, "a::b::c").getOrThrow()
        val copy = path.deepCopy()
        assertEquals(path, copy)
    }

    @Test
    fun pathParseFailureOnEmpty() {
        assertTrue(parseStr(PathParse, "").isFailure)
    }

    @Test
    fun pathParseFailureOnPunct() {
        assertTrue(parseStr(PathParse, "::").isFailure)
    }
}

private fun assertNotNull(value: Any?) {
    kotlin.test.assertNotNull(value)
}
