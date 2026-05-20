// port-lint: source tests/test_ident.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertFailsWith

// Identifier strings that the upstream `syn::Ident` parser rejects even
// though `proc_macro2::Ident::new` accepts them at the lexer level. Includes
// strict and reserved keywords plus the bare underscore (which the lexer
// tokenizes as an Ident but the parser reserves as a pattern placeholder).
// Kept inline here so this test file's `parse` mirrors
// `syn::parse2::<Ident>(...)` for the invariants the upstream test
// exercises. When a full `syn::Ident` parser is ported, this set moves
// there.
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

private fun parse(s: String): Result<Ident> = runCatching {
    val stream: TokenStream = TokenStream.fromString(s).getOrThrow()
    val tokens = stream.toList()
    if (tokens.size != 1) error("expected exactly one identifier, found ${tokens.size} tokens")
    val first = tokens.single()
    if (first !is TokenTree.Ident) error("expected identifier, found $first")
    val ident = first.value
    if (ident.toString() in RESERVED_IDENTIFIERS) error("expected identifier, found reserved keyword")
    ident
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

    // Upstream: should_panic(expected = "use Option<Ident>"). The lower-level
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

    // Upstream: should_panic(expected = "not a valid Ident"). A lifetime
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

    // Upstream: should_panic(expected = "use Literal instead"). A bare numeric
    // literal is not a valid identifier; the lower-level constructor throws.
    @Test
    fun identNewNumber() {
        assertFailsWith<IllegalArgumentException> {
            new("255")
        }
    }

    // Upstream: should_panic(expected = "\"a#\" is not a valid Ident"). The
    // identifier syntax does not permit `#`; the lower-level constructor
    // throws.
    @Test
    fun identNewInvalid() {
        assertFailsWith<IllegalArgumentException> {
            new("a#")
        }
    }
}
