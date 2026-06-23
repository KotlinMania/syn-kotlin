// port-lint: source parse_quote.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

/**
 * Quasi-quotation helper that accepts input like the `quote` macro but uses
 * type inference to figure out a return type for those tokens.
 *
 * The return type can be any syntax tree node that implements the [Parse]
 * interface (via [ParseQuote]).
 *
 * The `parseQuote` and `parseQuoteSpanned`
 * macros, which expand to `parseQuote` internally. Callers manually invoke the quote-kotlin builder and pass the resulting [TokenStream] to [parseQuote].
 *
 * ## Example
 *
 * ```kotlin
 * import io.github.kotlinmania.quote.quote
 * import io.github.kotlinmania.syn.parseQuote
 *
 * val name = quote { ident("v") }
 * val ty = quote { ident("UByte") }
 *
 * val stmt: Stmt = parseQuote(quote { `let` { #name `:` #ty `=` Default.defaultValue() } }, Stmt)
 * println(stmt)
 * ```
 *
 * ## Special cases
 *
 * Can parse additional types as a special case even though
 * they do not implement the [Parse] interface:
 *
 * - `Attribute` — parses one attribute, allowing either outer like `...` attribute
 *   or inner like `...` inner attribute
 * - `List<Attribute>` — parses multiple attributes, including mixed kinds
 * - [Punctuated] — parses zero or more T separated by punctuation P with
 *   optional trailing punctuation
 * - `List<Arm>` — parses arms separated by optional commas according to the
 *   same grammar as the body of a pattern-matching expression
 * - `List<Stmt>` — parses the same as `Block.parseWithin`
 * - `Pat` — parses the same as `Pat.parseMultiWithLeadingVert`
 * - `Field` — parses a named or unnamed data class field
 *
 * Those special-case implementations live alongside their respective syntax-tree types
 * once those are ported; they are not re-declared here so [ParseQuote] stays
 * a thin extension of [Parse].
 *
 * Not public API.
 */

/**
 * Parses [tokenStream] via [parser], panicking on parse failure. Mirrors the
 * `parseQuote` private helper that the `parseQuote` macro
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
    public fun parse(input: ParseStream): SynResult<T>
}

/** Adapts any [Parse] implementation into a [ParseQuote] implementation. */
public fun <T> parseQuoteFromParse(parse: Parse<T>): ParseQuote<T> =
    object : ParseQuote<T> {
        override fun parse(input: ParseStream): SynResult<T> = parse.parse(input)
    }

public object AttributeParseQuote : ParseQuote<Attribute> {
    override fun parse(input: ParseStream): SynResult<Attribute> =
        AttributeParse.parse(input)
}

public object AttributeListParseQuote : ParseQuote<List<Attribute>> {
    override fun parse(input: ParseStream): SynResult<List<Attribute>> {
        val attrs = mutableListOf<Attribute>()
        while (!input.isEmpty()) {
            attrs += AttributeParseQuote.parse(input).getOrElse { return SynResult.failure(it) }
        }
        return SynResult.success(attrs)
    }
}

public object FieldParseQuote : ParseQuote<Field> {
    override fun parse(input: ParseStream): SynResult<Field> {
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        val vis = input.parse(VisibilityParse).getOrElse { return SynResult.failure(it) }

        val ident: Ident?
        val colonToken: io.github.kotlinmania.syn.token.Colon?
        val isNamed = input.peek(IdentPeek) && input.peek2(ColonPeek) && !input.peek2(PathSepPeek)
        if (isNamed) {
            ident = input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
            colonToken = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
        } else {
            ident = null
            colonToken = null
        }

        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Field(attrs, vis, FieldMutability.None, ident, colonToken, ty))
    }
}

public object PatParseQuote : ParseQuote<Pat> {
    override fun parse(input: ParseStream): SynResult<Pat> =
        parsePatMultiWithLeadingVert(input)
}

public object StmtListParseQuote : ParseQuote<List<Stmt>> {
    override fun parse(input: ParseStream): SynResult<List<Stmt>> =
        parseWithin(input)
}
