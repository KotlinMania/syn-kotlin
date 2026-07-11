package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MacTest {
    @Test
    fun parseMacroWithParenDelimiter() {
        val mac = parseStr(Macro::parse, "println!(\"\")").getOrThrow()

        assertEquals(
            listOf("println"),
            mac.path.segments
                .toList()
                .map { it.ident.toString() },
        )
        assertIs<MacroDelimiter.Paren>(mac.delimiter)
        assertEquals("\"\"", mac.tokens.toString())
    }

    @Test
    fun parseMacroWithBracketDelimiter() {
        val mac = parseStr(Macro::parse, "vec![a, b]").getOrThrow()

        assertEquals(
            listOf("vec"),
            mac.path.segments
                .toList()
                .map { it.ident.toString() },
        )
        assertIs<MacroDelimiter.Bracket>(mac.delimiter)
        assertEquals("a , b", mac.tokens.toString())
    }

    @Test
    fun parseMacroWithModStylePathAndBraceDelimiter() {
        val mac = parseStr(Macro::parse, "foo::bar! { baz }").getOrThrow()

        assertEquals(
            listOf("foo", "bar"),
            mac.path.segments
                .toList()
                .map { it.ident.toString() },
        )
        assertIs<MacroDelimiter.Brace>(mac.delimiter)
        assertEquals("baz", mac.tokens.toString())
    }

    @Test
    fun parseMacroRequiresBangToken() {
        val result = parseStr(Macro::parse, "println(\"\")")

        assertTrue(result.isFailure)
    }
}
