package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Span
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class LiteralEqualityTest {
    @Test
    fun independentlyParsedLiteralsIgnoreSpansAndWorkAsKeys() {
        val spellings = listOf("\"text\"", "b\"text\"", "c\"text\"", "b't'", "'t'", "123u32", "1.25f64", "true")
        for (spelling in spellings) {
            val first = parseStr(LitParse::parse, spelling).getOrThrow()
            val second = parseStr(LitParse::parse, spelling).getOrThrow()
            first.setSpan(Span.callSite())
            second.setSpan(Span.mixedSite())
            assertEquals(first, second, spelling)
            assertEquals(first.hashCode(), second.hashCode(), spelling)
            assertEquals("found", mapOf(first to "found")[second], spelling)
        }
    }

    @Test
    fun differentTokenSpellingsRemainDistinct() {
        val pairs =
            listOf(
                "1" to "1u8",
                "10" to "0xa",
                "10" to "1_0",
                "1.0" to "1e0",
                "\"a\"" to "r#\"a\"#",
                "\"a\"" to "\"\\x61\"",
                "b'a'" to "b'\\x61'",
                "c\"a\"" to "c\"\\x61\"",
                "true" to "false",
            )
        for ((first, second) in pairs) {
            assertNotEquals(parseStr(LitParse::parse, first).getOrThrow(), parseStr(LitParse::parse, second).getOrThrow())
        }
    }

    @Test
    fun verbatimEqualityPreservesTheLiteralVariant() {
        val first = Lit.Verbatim(Literal.fromStrUnchecked("17"))
        val second = Lit.Verbatim(Literal.fromStrUnchecked("17"))
        first.setSpan(Span.callSite())
        second.setSpan(Span.mixedSite())
        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertNotEquals<Lit>(first, parseStr(LitParse::parse, "17").getOrThrow())
    }
}
