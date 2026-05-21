// port-lint: source parse_quote.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

// Quasi-quotation helper that accepts input like the `quote` macro but uses
// type inference to figure out a return type for those tokens.
//
// The return type can be any syntax tree node that implements the [Parse]
// trait (via [ParseQuote]).
//
// The upstream Rust spelling is the `parse_quote!` and `parse_quote_spanned!`
// macros, which expand to `__private::parse(quote::quote!(...))`. Kotlin has
// no macro system, so callers manually invoke the quote-kotlin builder and
// then pass the resulting [TokenStream] to [parseQuote].
//
// # Example
//
// ```kotlin
// import io.github.kotlinmania.quote.quote
// import io.github.kotlinmania.syn.parseQuote
//
// val name = quote { ident("v") }
// val ty = quote { ident("u8") }
//
// val stmt: Stmt = parseQuote(quote { `let` { #name `:` #ty `=` Default::default() } }, Stmt)
// println(stmt)
// ```
//
// # Special cases
//
// The upstream macro can parse additional types as a special case even though
// they do not implement the [Parse] trait:
//
// - `Attribute` — parses one attribute, allowing either outer like `#[...]`
//   or inner like `#![...]`
// - `List<Attribute>` — parses multiple attributes, including mixed kinds
// - [Punctuated] — parses zero or more T separated by punctuation P with
//   optional trailing punctuation
// - `List<Arm>` — parses arms separated by optional commas according to the
//   same grammar as the inside of a `match` expression
// - `List<Stmt>` — parses the same as `Block.parseWithin`
// - `Pat` — parses the same as `Pat.parseMultiWithLeadingVert`
// - `Field` — parses a named or unnamed struct field
//
// Those special-case impls live alongside their respective syntax-tree types
// once those are ported; they are not re-declared here so [ParseQuote] stays
// a thin extension of [Parse].

// Not public API.

/**
 * Parses [tokenStream] via [parser], panicking on parse failure. Mirrors the
 * upstream `parse_quote` private helper that the `parse_quote!` macro
 * expands to. The caller is responsible for ensuring that the input tokens
 * are syntactically valid; downstream parser failures are unrecoverable from
 * a [parseQuote] call site by design.
 */
public fun <T> parseQuote(tokenStream: TokenStream, parser: ParseQuote<T>): T {
    val result = parserFromFunction(parser::parse).parse2(tokenStream)
    return result.getOrElse { err -> error(err.message ?: err.toString()) }
}

/**
 * Marker interface for types parseable through [parseQuote]. Every [Parse]
 * implementation is automatically a [ParseQuote] via [parseQuoteFromParse];
 * types whose parsing is not the default-shaped [Parse.parse] (e.g.
 * `Attribute`, `Field`, `List<Stmt>`) provide a custom [ParseQuote] in their
 * own port file.
 */
public interface ParseQuote<T> {
    public fun parse(input: ParseStream): Result<T>
}

/** Adapts any [Parse] implementation into a [ParseQuote] implementation. */
public fun <T> parseQuoteFromParse(parse: Parse<T>): ParseQuote<T> = object : ParseQuote<T> {
    override fun parse(input: ParseStream): Result<T> = parse.parse(input)
}
