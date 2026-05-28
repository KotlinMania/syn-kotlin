// port-lint: source tests/test_ident.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertFailsWith

// Identifier strings that the parser rejects even though the token
// constructor accepts them at the lexer level. Includes strict and reserved
// keywords plus the bare underscore, which the lexer tokenizes as an Ident but
// the parser reserves as a pattern placeholder. Kept inline here until the full
// Syn identifier parser is ported.
private val RESERVED_IDENTIFIERS: Set<String> = setOf(
    "_",
    "abstract", "as", "async", "await", "become", "box", "break",
    "const", "continue", "crate", "do", "dyn", "else", "enum", "extern",
    "false", "final", "fn", "for", "if", "impl", "in", "let", "loop",
    "macro", "match", "mod", "move", "mut", "override", "priv", "pub",
    "ref", "return", "self", "Self", "static", "struct", "super",
    "trait", "true", "try", "type", "typeof", "unsafe", "unsized",
    "use", "virtual", "where", "while", "yield",
)

private fun parse(s: String): SynResult<Ident> {
    return try {
        val stream: TokenStream = TokenStream.fromString(s).getOrThrow()
        val tokens = stream.toList()
        if (tokens.size != 1) error("expected exactly one identifier, found ${tokens.size} tokens")
        val first = tokens.single()
        if (first !is TokenTree.Ident) error("expected identifier, found $first")
        val ident = first.value
        if (ident.toString() in RESERVED_IDENTIFIERS) error("expected identifier, found reserved keyword")
        SynResult.success(ident)
    } catch (cause: Throwable) {
        SynResult.failure(SynError.new(Span.callSite(), cause.message ?: cause.toString()))
    }
}

private fun new(s: String): Ident = Ident.new(s, Span.callSite())

class IdentTest {
    @Test
    fun identParse() {
        parse("String").getOrThrow()
    }

    @Test
    fun identParseKeyword() {
        check(parse("abstract").isFailure)
    }

    @Test
    fun identParseEmpty() {
        check(parse("").isFailure)
    }

    @Test
    fun identParseLifetime() {
        check(parse("'static").isFailure)
    }

    @Test
    fun identParseUnderscore() {
        check(parse("_").isFailure)
    }

    @Test
    fun identParseNumber() {
        check(parse("255").isFailure)
    }

    @Test
    fun identParseInvalid() {
        check(parse("a#").isFailure)
    }

    @Test
    fun identNew() {
        new("String")
    }

    @Test
    fun identNewKeyword() {
        new("abstract")
    }

    // Should panic(expected = "use Option<Ident>"). The lower-level
    // `Ident::new` constructor in proc-macro2-kotlin throws
    // [IllegalArgumentException] for the empty string with that exact
    // diagnostic intent; the panic-shape contract is mapped to a thrown
    // exception here.
    @Test
    fun identNewEmpty() {
        assertFailsWith<IllegalArgumentException> {
            new("")
        }
    }

    // Should panic(expected = "not a valid Ident"). A lifetime
    // literal is not a valid identifier; the lower-level constructor throws.
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

    // Should panic(expected = "use Literal instead"). A bare numeric
    // literal is not a valid identifier; the lower-level constructor throws.
    @Test
    fun identNewNumber() {
        assertFailsWith<IllegalArgumentException> {
            new("255")
        }
    }

    // Should panic(expected = "\"a#\" is not a valid Ident"). The
    // identifier syntax does not permit `#`; the lower-level constructor
    // throws.
    @Test
    fun identNewInvalid() {
        assertFailsWith<IllegalArgumentException> {
            new("a#")
        }
    }
}
