// port-lint: tests tests/test_round_trip.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Round-trip parse-then-emit parity tests.
 *
 * The upstream test walks the rust-lang/rust source tree, parses each file
 * with `syn::parse_file`, re-emits the token stream via `quote!(#krate)`,
 * then parses both the original and re-emitted source with the nightly
 * `librustc_parse` and compares the resulting `rustc_ast::ast::Crate`
 * nodes with `SpanlessEq`, after normalizing generic-argument ordering.
 * The portable `syn::parse_file` and `File::to_tokens` parts are covered
 * below. The repo-wide nightly `librustc_parse` comparison still has no
 * Kotlin counterpart because the rustc parser, rustc AST, span-ignoring
 * rustc equality helper, and rust-lang/rust checkout harness are not
 * available in this port.
 */
class RoundTripTest {
    private fun emit(file: File): TokenStream {
        val tokens = TokenStream.new()
        file.toTokens(tokens)
        return tokens
    }

    @Test
    fun testRoundTrip() {
        val file =
            parseFile("\uFEFF#!/usr/bin/env rustx\n#![allow(dead_code)]\nfn main() {}")
                .getOrThrow()

        assertEquals("#!/usr/bin/env rustx", file.shebang)
        assertEquals(1, file.attrs.size)
        assertIs<AttrStyle.Inner>(file.attrs.single().style)
        assertEquals("allow", file.attrs.single().path().toString())
        val item = assertIs<Item.Fn>(file.items.single())
        assertEquals("main", item.ident.toString())

        val emitted = emit(file)
        val emittedString = emitted.toString()
        assertFalse(emittedString.contains("rustx"))
        assertFalse(emittedString.contains("#!/"))

        val reparsed = parse2(FileParse::parse, emitted).getOrThrow()
        assertNull(reparsed.shebang)
        assertEquals(1, reparsed.attrs.size)
        assertIs<AttrStyle.Inner>(reparsed.attrs.single().style)
        assertIs<Item.Fn>(reparsed.items.single())
    }
}
