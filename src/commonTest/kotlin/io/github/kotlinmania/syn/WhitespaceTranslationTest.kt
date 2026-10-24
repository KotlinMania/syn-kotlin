package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WhitespaceTranslationTest {
    @Test
    fun matchesRetainedRustWhitespaceCorpus() {
        val cases =
            listOf(
                "" to "",
                "abc" to "abc",
                " \t\r\nX" to "X",
                "// no newline" to "",
                "// hi\n  X" to "X",
                "/// doc\nX" to "/// doc\nX",
                "//// ordinary\nX" to "X",
                "//! inner\nX" to "//! inner\nX",
                "/**/ X" to "X",
                "/** doc */X" to "/** doc */X",
                "/***/X" to "X",
                "/*! inner */X" to "/*! inner */X",
                "/* a /* nested */ c */ X" to "X",
                " /* missing" to "/* missing",
                "/*\ud83e\udd80/*\u00e9*/x*/\tX" to "X",
                "//\ud83e\udd80\u00e9\nX" to "X",
                "/****" to "/****",
                "/" to "/",
                "/*/" to "/*/",
                "/**/ /**/X" to "X",
                "\u200e\u200fX" to "X",
                "\u001cX" to "\u001cX",
                "\u200bX" to "\u200bX",
                "\ud83e\udd80X" to "\ud83e\udd80X",
                "\u00e9X" to "\u00e9X",
                "\bX" to "\bX",
                "\tX" to "X",
                "\nX" to "X",
                "\u000bX" to "X",
                "\u000cX" to "X",
                "\rX" to "X",
                "\u000eX" to "\u000eX",
                "\u001fX" to "\u001fX",
                " X" to "X",
                "!X" to "!X",
                "\u0084X" to "\u0084X",
                "\u0085X" to "X",
                "\u0086X" to "\u0086X",
                "\u009fX" to "\u009fX",
                "\u00a0X" to "X",
                "\u00a1X" to "\u00a1X",
                "\u167fX" to "\u167fX",
                "\u1680X" to "X",
                "\u1681X" to "\u1681X",
                "\u1fffX" to "\u1fffX",
                "\u2000X" to "X",
                "\u2001X" to "X",
                "\u2002X" to "X",
                "\u2003X" to "X",
                "\u2004X" to "X",
                "\u2005X" to "X",
                "\u2006X" to "X",
                "\u2007X" to "X",
                "\u2008X" to "X",
                "\u2009X" to "X",
                "\u200aX" to "X",
                "\u2027X" to "\u2027X",
                "\u2028X" to "X",
                "\u2029X" to "X",
                "\u202aX" to "\u202aX",
                "\u202eX" to "\u202eX",
                "\u202fX" to "X",
                "\u2030X" to "\u2030X",
                "\u205eX" to "\u205eX",
                "\u205fX" to "X",
                "\u2060X" to "\u2060X",
                "\u2fffX" to "\u2fffX",
                "\u3000X" to "X",
                "\u3001X" to "\u3001X",
            )
        for ((source, expected) in cases) assertEquals(expected, skip(source), source)
    }

    @Test
    fun nextLineWhitespaceBeforeInnerAttributeIsNotAShebang() {
        val file = parseFile("#!\u0085[allow(dead_code)]\nfn f() {}").getOrThrow()
        assertNull(file.shebang)
        assertEquals(1, file.attrs.size)
        assertEquals(1, file.items.size)
    }
}
