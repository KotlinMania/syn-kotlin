// port-lint: tests parse.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParseTest {
    @Test
    fun nothingParsesOnlyEmptyInputAndPrintsNoTokens() {
        val parsed = parseStr(Nothing::parse, "").getOrThrow()
        assertEquals(Nothing, parsed)
        assertFalse(parsed.equals(Any()))
        assertEquals(0, parsed.hashCode())
        assertEquals("Nothing", parsed.toString())
        assertTrue(parsed.toTokenStream().isEmpty())

        val tokens = TokenStream.new()
        parsed.toTokens(tokens)
        assertTrue(tokens.isEmpty())

        val extra = parseStr(Nothing::parse, "asdf")
        assertTrue(extra.isFailure)
        assertEquals("unexpected token", extra.exceptionOrNull()?.toString())
    }
}
