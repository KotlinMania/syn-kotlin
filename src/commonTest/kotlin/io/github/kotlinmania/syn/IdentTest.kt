// port-lint: tests tests/test_ident.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private fun parse(s: String): SynResult<Ident> {
    val parseResult = TokenStream.fromString(s)
    if (parseResult.isFailure()) {
        return SynResult.failure(SynError.new(Span.callSite(), parseResult.error ?: "cannot parse string"))
    }
    return parse2(IdentParse::parse, parseResult.getOrThrow())
}

private fun new(s: String): Ident = Ident.new(s, Span.callSite())

class IdentTest {
    @Test
    fun identParse() {
        parse("String").getOrThrow()
    }

    @Test
    fun identParseKeyword() {
        assertTrue(parse("abstract").isFailure)
    }

    @Test
    fun identParseEmpty() {
        assertTrue(parse("").isFailure)
    }

    @Test
    fun identParseLifetime() {
        assertTrue(parse("'static").isFailure)
    }

    @Test
    fun identParseUnderscore() {
        assertTrue(parse("_").isFailure)
    }

    @Test
    fun identParseNumber() {
        assertTrue(parse("255").isFailure)
    }

    @Test
    fun identParseInvalid() {
        assertTrue(parse("a#").isFailure)
    }

    @Test
    fun identNew() {
        new("String")
    }

    @Test
    fun identNewKeyword() {
        new("abstract")
    }

    @Test
    fun identNewEmpty() {
        assertFailsWith<IllegalArgumentException> {
            new("")
        }
    }

    @Test
    fun identNewLifetime() {
        assertFailsWith<IllegalArgumentException> {
            new("'static")
        }
    }

    @Test
    fun identNewUnderscore() {
        new("_")
    }

    @Test
    fun identNewNumber() {
        assertFailsWith<IllegalArgumentException> {
            new("255")
        }
    }

    @Test
    fun identNewInvalid() {
        assertFailsWith<IllegalArgumentException> {
            new("a#")
        }
    }

    @Test
    fun identUnraw() {
        val raw = Ident.newRaw("move", Span.callSite())
        assertEquals("move", raw.unraw().toString())
        assertEquals("move", IdentExt.unraw(raw).toString())
    }
}
