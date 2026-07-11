// port-lint: source mac.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Bracket
import io.github.kotlinmania.syn.token.Paren

/**
 * A macro invocation consisting of a path, bang token, delimiter, and token stream.
 */
public data class Macro(
    public var path: Path,
    public var bangToken: io.github.kotlinmania.syn.token.Not,
    public var delimiter: MacroDelimiter,
    public var tokens: TokenStream,
) : ToTokens {
    public companion object {
        fun parse(input: ParseStream): SynResult<Macro> {
            val path = Path.parseModStyle(input).getOrElse { return SynResult.failure(it) }
            val bangToken = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
            val (delimiter, content) = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(Macro(path, bangToken, delimiter, content))
        }
    }

    override fun toTokens(tokens: TokenStream) {
        path.toTokens(tokens)
        bangToken.toTokens(tokens)
        delimiter.surround(tokens, this.tokens)
    }

    public fun deepCopy(): Macro =
        Macro(path.deepCopy(), bangToken, delimiter, tokens)

    public fun span(): DelimSpan =
        when (val value = delimiter) {
            is MacroDelimiter.Paren -> value.token.span
            is MacroDelimiter.Brace -> value.token.span
            is MacroDelimiter.Bracket -> value.token.span
        }

    public fun isBrace(): Boolean = delimiter is MacroDelimiter.Brace
}

/**
 * Extension to check if a [MacroDelimiter] is brace-delimited.
 */
public val MacroDelimiter.isBrace: Boolean
    get() = this is MacroDelimiter.Brace

/** Surrounds the given content with this delimiter. */
public fun MacroDelimiter.surround(tokens: TokenStream, content: TokenStream) {
    var (delim, span) =
        when (this) {
            is MacroDelimiter.Paren -> Delimiter.Parenthesis to token.span
            is MacroDelimiter.Brace -> Delimiter.Brace to token.span
            is MacroDelimiter.Bracket -> Delimiter.Bracket to token.span
        }
    var group = Group(delim, content)
    group.setSpan(span.join())
    tokens.append(TokenTree.Group(group))
}

/** Parse the tokens within the macro invocation's delimiters into an expression. */
public fun Macro.parseBodyExpr(): SynResult<Expr> =
    parseBodyWith(ExprParse::parse)

/** Parse the tokens within the macro invocation's delimiters using the given parser. */
public fun <T> Macro.parseBody(parser: (ParseStream) -> SynResult<T>): SynResult<T> =
    parseBodyWith(parser)

/** Parse the tokens within the macro invocation's delimiters using the given parser. */
public fun <T> Macro.parseBodyWith(parser: (ParseStream) -> SynResult<T>): SynResult<T> {
    val delimiter = this.delimiter
    var scope =
        when (delimiter) {
            is MacroDelimiter.Paren -> delimiter.token.span.close()
            is MacroDelimiter.Brace -> delimiter.token.span.close()
            is MacroDelimiter.Bracket -> delimiter.token.span.close()
        }
    return parseScoped(parser, scope, tokens)
}

/** Parses a delimiter from the input stream. */
public fun parseDelimiter(input: ParseStream): SynResult<Pair<MacroDelimiter, TokenStream>> =
    input.step { cursor ->
        var (tt, rest) =
            cursor.tokenTree()
                ?: return@step SynResult.failure(cursor.error("expected delimiter"))
        if (tt is TokenTree.Group) {
            val span = tt.value.delimSpan()
            val delimiter =
                when (tt.value.delimiter()) {
                    Delimiter.Parenthesis -> MacroDelimiter.Paren(Paren.from(span.open()))
                    Delimiter.Brace -> MacroDelimiter.Brace(Brace.from(span.open()))
                    Delimiter.Bracket -> MacroDelimiter.Bracket(Bracket.from(span.open()))
                    Delimiter.None -> return@step SynResult.failure(cursor.error("expected delimiter"))
                }
            SynResult.success((delimiter to tt.value.stream()) to rest)
        } else {
            SynResult.failure(cursor.error("expected delimiter"))
        }
    }
