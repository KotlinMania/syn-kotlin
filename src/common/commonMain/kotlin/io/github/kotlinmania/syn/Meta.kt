// port-lint: source meta.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span

// Like Path::parseModStyle, but accepts keywords in the path.
public fun parseMetaPath(input: ParseStream): SynResult<Path> {
    val leadingColon =
        if (input.peek(PathSepPeek)) {
            PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }

    val segments = PathSegmentList()
    if (input.peek(IdentPeekAny)) {
        val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
        segments.pushValue(PathSegment.from(ident))
    } else if (input.isEmpty()) {
        return SynResult.failure(input.error("expected nested attribute"))
    } else if (input.peek(LitPeek)) {
        return SynResult.failure(input.error("unexpected literal in nested attribute, expected ident"))
    } else {
        return SynResult.failure(input.error("unexpected token in nested attribute, expected ident"))
    }

    while (input.peek(PathSepPeek)) {
        val punct = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
        segments.pushPunct(punct)
        val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
        segments.pushValue(PathSegment.from(ident))
    }

    return SynResult.success(Path(leadingColon, segments))
}

/**
 * Make a parser that is usable with `parseMacroInput!` in a
 * `` macro.
 *
 * *Warning:* When parsing attribute args **other than** the
 * `procMacro::TokenStream` input of a `procMacroAttribute`, you do **not**
 * need this function. In several cases your callers will get worse error
 * messages if you use this function, because the surrounding delimiter's span
 * is concealed from attribute macros by rustc. Use
 * [`Attribute::parseNestedMeta`] instead.
 *
 * [`Attribute::parseNestedMeta`]: crate::Attribute::parseNestedMeta
 *
 * # Example
 *
 * This example implements an attribute macro whose invocations look like this:
 *
 * ```
 *
 * struct Picard {...}
 * ```
 *
 * The "parameters" supported by the attribute are:
 *
 * - `kind = "..."`
 * - `hot`
 * - `with(sugar, milk, ...)`, a comma-separated list of ingredients
 *
 * ```
 * import procMacro.TokenStream;
 * import syn..parseMacroInput, LitStr, Path};
 *
 *
 * public fun tea(args: TokenStream, input: TokenStream) -> TokenStream {
 *     var kind: Option<LitStr> = None;
 *     var hot: bool = false;
 *     var with: Vec<Path> = Vec::new();
 *     val teaParser = syn::meta::parser(|meta| {
 *         if meta.path.isIdent("kind") {
 *             kind = Some(meta.value()?.parse()?);
 *             Ok(())
 *         } else if meta.path.isIdent("hot") {
 *             hot = true;
 *             Ok(())
 *         } else if meta.path.isIdent("with") {
 *             meta.parseNestedMeta(|meta| {
 *                 with.push(meta.path);
 *                 Ok(())
 *             })
 *         } else {
 *             Err(meta.error("unsupported tea property"))
 *         }
 *     });
 *
 *     parseMacroInput(args with teaParser);
 *     eprintln("kind={kind:?} hot={hot} with={with:?}");
 *
 *     / * ... * /
 * }
 * ```
 *
 * The `syn::meta` library will take care of dealing with the commas including
 * trailing commas, and producing sensible error messages on unexpected input.
 *
 * ```console
 * error: expected `,`
 *  --> src/main.rs:3:37
 *   |
 * 3 |
 *   |                                     ^
 * ```
 *
 * # Example
 *
 * Same as above but we factor out most of the logic into a separate function.
 *
 * ```
 * import procMacro.TokenStream;
 * import syn.meta.ParseNestedMeta;
 * import syn.parse..Parser, Result};
 * import syn..parseMacroInput, LitStr, Path};
 *
 *
 * public fun tea(args: TokenStream, input: TokenStream) -> TokenStream {
 *     var attrs = TeaAttributes::default();
 *     val teaParser = syn::meta::parser(|meta| attrs.parse(meta));
 *     parseMacroInput(args with teaParser);
 *
 *     / * ... * /
 * }
 *
 *
 * struct TeaAttributes {
 *     kind: Option<LitStr>,
 *     hot: bool,
 *     with: Vec<Path>,
 * }
 *
 * class TeaAttributes {
 *     fun parse(&mut self, meta: ParseNestedMeta) -> Result<()> {
 *         if meta.path.isIdent("kind") {
 *             self.kind = Some(meta.value()?.parse()?);
 *             Ok(())
 *         } else / * just like in last example * /
 *
 *     }
 * }
 * ```
 */
public fun parser(logic: (ParseNestedMeta) -> SynResult<Unit>): (ParseStream) -> SynResult<Unit> =
    parser@{ input: ParseStream ->
        if (input.isEmpty()) {
            SynResult.success(Unit)
        } else {
            parseNestedMetaInternal(input, logic)
        }
    }

public fun metaParser(logic: (ParseNestedMeta) -> SynResult<Unit>): (ParseStream) -> SynResult<Unit> =
    parser(logic)

/**
 * Context for parsing a single property in the conventional syntax for
 * structured attributes.
 *
 * # Examples
 *
 * Refer to usage examples on the following two entry-points:
 *
 * - [`Attribute::parseNestedMeta`] if you have an entire `Attribute` to
 *   parse. Always use this if possible. Generally this is able to produce
 *   better error messages because `Attribute` holds span information for all
 *   of the delimiters therein.
 *
 * - [`syn::meta::parser`] if you are implementing a `procMacroAttribute`
 *   macro and parsing the arguments to the attribute macro, i.e. the ones
 *   written in the same attribute that dispatched the macro invocation. Rustc
 *   does not pass span information for the surrounding delimiters into the
 *   attribute macro invocation in this situation, so error messages might be
 *   less precise.
 *
 * [`Attribute::parseNestedMeta`]: crate::Attribute::parseNestedMeta
 * [`syn::meta::parser`]: crate::meta::parser
 */
public class ParseNestedMeta(
    public val path: Path,
    public val input: ParseStream,
) {
    /**
     * Used when parsing `key = "value"` syntax.
     *
     * All it does is advance `meta.input` past the `=` sign in the input. You
     * could accomplish the same effect by writing
     * `meta.parse::<token[=]>()?`, so at most it is a minor convenience to
     * use `meta.value()?`.
     *
     * # Example
     *
     * ```
     * import syn..parseQuote, Attribute, LitStr};
     *
     * val attr: Attribute = parseQuote{
     *
     * };
     *                                          // conceptually:
     * if attr.path().isIdent("tea") {         // this parses the `tea`
     *     attr.parseNestedMeta(|meta| {      // this parses the `(`
     *         if meta.path.isIdent("kind") {  // this parses the `kind`
     *             val value = meta.value()?;   // this parses the `=`
     *             val s: LitStr = value.parse()?;  // this parses `"EarlGrey"`
     *             if s.value() == "EarlGrey" {
     *                 // ...
     *             }
     *             Ok(())
     *         } else {
     *             Err(meta.error("unsupported attribute"))
     *         }
     *     })?;
     * }
     * ```
     */
    public fun value(): SynResult<ParseStream> {
        EqParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(input)
    }

    /**
     * Used when parsing `list(...)` syntax **if** the content inside the
     * nested parentheses is also expected to conform to Rust's structured
     * attribute convention.
     *
     * # Example
     *
     * ```
     * import syn..parseQuote, Attribute};
     *
     * val attr: Attribute = parseQuote{
     *
     * };
     *
     * if attr.path().isIdent("tea") {
     *     attr.parseNestedMeta(|meta| {
     *         if meta.path.isIdent("with") {
     *             meta.parseNestedMeta(|meta| {  // <---
     *                 if meta.path.isIdent("sugar") {
     *                     // Here we can go even deeper if needed.
     *                     Ok(())
     *                 } else if meta.path.isIdent("milk") {
     *                     Ok(())
     *                 } else {
     *                     Err(meta.error("unsupported ingredient"))
     *                 }
     *             })
     *         } else {
     *             Err(meta.error("unsupported tea property"))
     *         }
     *     })?;
     * }
     * ```
     *
     * # Counterexample
     *
     * If you don't need `parseNestedMeta`'s help in parsing the content
     * written within the nested parentheses, keep in mind that you can always
     * just parse it yourself from the exposed ParseStream. Rust syntax permits
     * arbitrary tokens within those parentheses so for the crazier stuff,
     * `parseNestedMeta` is not what you want.
     *
     * ```
     * import syn..parenthesized, parseQuote, Attribute, LitInt};
     *
     * val attr: Attribute = parseQuote{
     *
     * };
     *
     * var align: Option<LitInt> = None;
     * if attr.path().isIdent("repr") {
     *     attr.parseNestedMeta(|meta| {
     *         if meta.path.isIdent("align") {
     *             val content;
     *             parenthesized(content in meta.input);
     *             align = Some(content.parse()?);
     *             Ok(())
     *         } else {
     *             Err(meta.error("unsupported repr"))
     *         }
     *     })?;
     * }
     * ```
     */
    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> {
        val content = parenthesized(input).getOrElse { return SynResult.failure(it) }
        val result = parseNestedMetaInternal(content.content, logic)
        content.content.finishChildBuffer()
        return result
    }

    /**
     * Report that the attribute's content did not conform to expectations.
     *
     * The span of the resulting error will cover `meta.path` *and* everything
     * that has been parsed so far since it.
     *
     * There are 2 ways you might call this. First, if `meta.path` is not
     * something you recognize:
     *
     * ```
     * attr.parseNestedMeta(|meta| {
     *     if meta.path.isIdent("kind") {
     *         // ...
     *         Ok(())
     *     } else {
     *         Err(meta.error("unsupported tea property"))
     *     }
     * })?;
     * ```
     *
     * In this case, it behaves exactly like
     * `syn::Error::newSpanned(&meta.path, "message...")`.
     *
     * ```console
     * error: unsupported tea property
     *  --> src/main.rs:3:26
     *   |
     * 3 |
     *   |                          ^^^
     * ```
     *
     * More usefully, the second place is if you've already parsed a value but
     * have decided not to accept the value:
     *
     * ```
     * import syn.Expr;
     *
     * attr.parseNestedMeta(|meta| {
     *     if meta.path.isIdent("kind") {
     *         val expr: Expr = meta.value()?.parse()?;
     *         when (expr) {
     *             Expr::Lit(expr) => / * ... * /
     *             Expr::Path(expr) => / * ... * /
     *             Expr::Macro(expr) => / * ... * /
     *             _ => Err(meta.error("tea kind must be a string literal, path, or macro")),
     *         }
     *     } else / * as above * /
     *
     * })?;
     * ```
     *
     * ```console
     * error: tea kind must be a string literal, path, or macro
     *  --> src/main.rs:3:7
     *   |
     * 3 |
     *   |       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * ```
     *
     * Often you may want to use `syn::Error::newSpanned` even in this
     * situation. In the above code, that would be:
     *
     * ```
     *     when (expr) {
     *         Expr::Lit(expr) => / * ... * /
     *         Expr::Path(expr) => / * ... * /
     *         Expr::Macro(expr) => / * ... * /
     *         _ => Err(Error::newSpanned(expr, "unsupported expression type for `kind`")),
     *     }
     * ```
     *
     * ```console
     * error: unsupported expression type for `kind`
     *  --> src/main.rs:3:14
     *   |
     * 3 |
     *   |              ^^^^^^^^^^^^^^^^^^^^^^^^^^
     * ```
     */
    public fun error(msg: String): SynError {
        val startSpan =
            path.segments
                .first()
                ?.ident
                ?.span()
                ?: Span.callSite()
        val endSpan = input.cursor().prevSpan()
        return SynError.new2(startSpan, endSpan, msg)
    }
}

internal fun parseNestedMetaInternal(
    input: ParseStream,
    logic: (ParseNestedMeta) -> SynResult<Unit>,
): SynResult<Unit> {
    while (true) {
        val path = parseMetaPath(input).getOrElse { return SynResult.failure(it) }
        val result = logic(ParseNestedMeta(path, input))
        if (result.isFailure) return result
        if (input.isEmpty()) return SynResult.success(Unit)
        CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        if (input.isEmpty()) return SynResult.success(Unit)
    }
}
