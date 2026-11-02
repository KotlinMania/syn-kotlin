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
        delimiter.surround(tokens, this.tokens.clone())
    }

    public fun deepCopy(): Macro =
        Macro(path.deepCopy(), bangToken, delimiter, tokens.clone())

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
    val group = Group(delim, content)
    group.setSpan(span.join())
    tokens.append(TokenTree.Group(group))
}

/** Parse the tokens within the macro invocation's delimiters into an expression. */
public fun Macro.parseBodyExpr(): SynResult<Expr> =
    parseBodyWith(ExprParse::parse)

/**
     * Parse the tokens within the macro invocation's delimiters into a syntax
     * tree.
     *
     * This is equivalent to `syn::parse2::<T>(mac.tokens)` except that it
     * produces a more useful span when `tokens` is empty.
     *
     * # Example
     *
     * ```
     * import syn..parseQuote, Expr, ExprLit, Ident, Lit, LitStr, Macro, Token};
     * import syn.ext.IdentExt;
     * import syn.parse..Error, Parse, ParseStream, Result};
     * import syn.punctuated.Punctuated;
     *
     * // The arguments expected by libcore's formatArgs macro, and as a
     * // result most other formatting and printing macros like println.
     * //
     * //     println("{} is {number:.prec$}", "x", prec=5, number=0.01)
     * struct FormatArgs {
     *     formatString: Expr,
     *     positionalArgs: Vec<Expr>,
     *     namedArgs: Vec<(Ident, Expr)>,
     * }
     *
     * class FormatArgs : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         val formatString: Expr;
     *         var positionalArgs = Vec::new();
     *         var namedArgs = Vec::new();
     *
     *         formatString = input.parse()?;
     *         while !input.isEmpty() {
     *             input.parse::<token[,]>()?;
     *             if input.isEmpty() {
     *                 break;
     *             }
     *             if input.peek(Ident::peekAny) && input.peek2(token[=]) {
     *                 while !input.isEmpty() {
     *                     val name: Ident = input.call(Ident::parseAny)?;
     *                     input.parse::<token[=]>()?;
     *                     val value: Expr = input.parse()?;
     *                     namedArgs.push((name, value));
     *                     if input.isEmpty() {
     *                         break;
     *                     }
     *                     input.parse::<token[,]>()?;
     *                 }
     *                 break;
     *             }
     *             positionalArgs.push(input.parse()?);
     *         }
     *
     *         Ok(FormatArgs {
     *             formatString,
     *             positionalArgs,
     *             namedArgs,
     *         })
     *     }
     * }
     *
     * // Extract the first argument, the format string literal, from an
     * // invocation of a formatting or printing macro.
     * fun getFormatString(m: &Macro) -> Result<LitStr> {
     *     val args: FormatArgs = m.parseBody()?;
     *     when (args.formatString) {
     *         Expr::Lit(ExprLit { lit: Lit::Str(lit), .. }) => Ok(lit),
     *         other => {
     *             // First argument was not a string literal expression.
     *             // Maybe something like: println(concat(...), ...)
     *             Err(Error::newSpanned(other, "format string must be a string literal"))
     *         }
     *     }
     * }
     *
     * fun main() {
     *     val invocation = parseQuote{
     *         println("{:?}", Instant::now())
     *     };
     *     val lit = getFormatString(&invocation).unwrap();
     *     assertEq(lit.value(), "{:?}");
     * }
     * ```
     */
public fun <T> Macro.parseBody(parser: (ParseStream) -> SynResult<T>): SynResult<T> =
    parseBodyWith(parser)

/** Parse the tokens within the macro invocation's delimiters using the given parser. */
public fun <T> Macro.parseBodyWith(parser: (ParseStream) -> SynResult<T>): SynResult<T> {
    val delimiter = this.delimiter
    val scope =
        when (delimiter) {
            is MacroDelimiter.Paren -> delimiter.token.span.close()
            is MacroDelimiter.Brace -> delimiter.token.span.close()
            is MacroDelimiter.Bracket -> delimiter.token.span.close()
        }
    return parseScoped(parser, scope, tokens.clone())
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
                    Delimiter.Parenthesis -> MacroDelimiter.Paren(Paren.from(span))
                    Delimiter.Brace -> MacroDelimiter.Brace(Brace.from(span))
                    Delimiter.Bracket -> MacroDelimiter.Bracket(Bracket.from(span))
                    Delimiter.None -> return@step SynResult.failure(cursor.error("expected delimiter"))
                }
            SynResult.success((delimiter to tt.value.stream()) to rest)
        } else {
            SynResult.failure(cursor.error("expected delimiter"))
        }
    }
