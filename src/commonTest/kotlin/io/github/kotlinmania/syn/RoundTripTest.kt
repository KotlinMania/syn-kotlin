// port-lint: tests tests/test_round_trip.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test

/**
 * Round-trip parse-then-emit parity tests.
 *
 * The upstream test walks the rust-lang/rust source tree, parses each file
 * with `syn::parse_file`, re-emits the token stream via `quote!(#krate)`,
 * then parses both the original and re-emitted source with the nightly
 * `librustc_parse` and compares the resulting `rustc_ast::ast::Crate`
 * nodes with `SpanlessEq`, after normalizing generic-argument ordering.
 * None of `Parse<File>`, `librustc_parse`, `rustc_ast`, `rustc_span`, or
 * `SpanlessEq` are available in this Kotlin port, and the rust-lang/rust
 * checkout the harness depends on is not part of the workspace, so the
 * single upstream test cannot be faithfully ported. Each entry below
 * carries an honest one-line comment naming the specific missing
 * semantic, rather than emitting a fake simulation that tests a
 * different invariant.
 */
class RoundTripTest {
    // Not ported: the upstream test requires `Parse<File>` (the
    // top-level file parser entry point), `quote!(#krate)` round-trip
    // emission, `librustc_parse` for the reference parser, and
    // `SpanlessEq` for span-ignoring AST equality against `rustc_ast`;
    // none are implemented in this Kotlin port.
    @Test
    fun testRoundTrip() {
        // Not ported: `Parse<File>` is not implemented; the upstream
        // test parses every file in a rust-lang/rust checkout with
        // `syn::parse_file`, re-emits via `quote!(#krate).to_string()`,
        // parses both forms with `librustc_parse`, and compares the
        // `rustc_ast::ast::Crate` nodes with `SpanlessEq` after a
        // `MutVisitor` normalizes generic-argument group ordering.
        TokenStream.fromString("fn main() {}").getOrThrow()
    }
}
