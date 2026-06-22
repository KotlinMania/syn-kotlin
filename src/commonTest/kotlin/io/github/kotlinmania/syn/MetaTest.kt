// port-lint: tests tests/test_meta.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetaTest {
    @Test
    fun testParseMetaItemWord() {
        val meta = parseStr(MetaParse, "hello").getOrThrow()
        assertTrue(meta is Meta.PathMeta)
        assertEquals("hello", meta.path.toString())
    }

    @Test
    fun testParseMetaNameValue() {
        val inner = parseStr(MetaNameValueParse, "foo = 5").getOrThrow()
        assertEquals("foo", inner.path.toString())
        assertTrue(inner.value is Expr.Lit)

        val meta = parseStr(MetaParse, "foo = 5").getOrThrow()
        assertTrue(meta is Meta.NameValue)
        assertEquals("foo", meta.path.toString())
        assertTrue(meta.value is Expr.Lit)
        // TokenStream has no value equality, so compare field-by-field instead of the whole data class.
        assertEquals(inner.path.toString(), meta.path.toString())
        assertEquals(inner.value.toString(), meta.value.toString())
    }

    @Test
    fun testParseMetaItemListLit() {
        val inner = parseStr(MetaListParse, "foo(5)").getOrThrow()
        assertEquals("foo", inner.path.toString())
        assertTrue(inner.delimiter is MacroDelimiter.Paren)
        assertEquals("5", inner.tokens.toString())

        val meta = parseStr(MetaParse, "foo(5)").getOrThrow()
        assertTrue(meta is Meta.List)
        assertEquals("foo", meta.path.toString())
        assertTrue(meta.delimiter is MacroDelimiter.Paren)
        assertEquals("5", meta.tokens.toString())
        assertEquals(inner, meta)
    }

    @Test
    fun testParseMetaItemMultiple() {
        val inner = parseStr(MetaListParse, "foo(word, name = 5, list(name2 = 6), word2)").getOrThrow()
        assertEquals("foo", inner.path.toString())
        assertTrue(inner.delimiter is MacroDelimiter.Paren)
        assertEquals("word , name = 5 , list (name2 = 6) , word2", inner.tokens.toString())

        val meta = parseStr(MetaParse, "foo(word, name = 5, list(name2 = 6), word2)").getOrThrow()
        assertTrue(meta is Meta.List)
        assertEquals("foo", meta.path.toString())
        assertTrue(meta.delimiter is MacroDelimiter.Paren)
        assertEquals("word , name = 5 , list (name2 = 6) , word2", meta.tokens.toString())
        // TokenStream has no value equality, so compare field-by-field instead of the whole data class.
        assertEquals(inner.path.toString(), meta.path.toString())
        assertEquals(inner.tokens.toString(), meta.tokens.toString())
    }

    @Test
    fun testParsePath() {
        val meta = parseStr(MetaParse, "::serde::Serialize").getOrThrow()
        assertTrue(meta is Meta.PathMeta)
        assertTrue(meta.path.leadingColon != null)
        assertEquals(2, meta.path.segments.len())
        assertEquals(
            "serde",
            meta.path.segments
                .toList()[0]
                .ident
                .toString(),
        )
        assertEquals(
            "Serialize",
            meta.path.segments
                .toList()[1]
                .ident
                .toString(),
        )
    }

    @Test
    fun testFatArrowAfterMeta() {
        val parser =
            parserFromFunction { input ->
                while (!input.isEmpty()) {
                    input.parse(MetaParse).getOrElse { return@parserFromFunction SynResult.failure(it) }
                    input.parse(FatArrowParse).getOrElse { return@parserFromFunction SynResult.failure(it) }
                    val braces = braced(input).getOrElse { return@parserFromFunction SynResult.failure(it) }
                    braces.content.finishChildBuffer()
                }
                SynResult.success(Unit)
            }

        val input = "target_os = \"linux\" => {} windows => {}"
        val result = parser.parseStr(input)
        assertTrue(result.isSuccess)
    }
}
