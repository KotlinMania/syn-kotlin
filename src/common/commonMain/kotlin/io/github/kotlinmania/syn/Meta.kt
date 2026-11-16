// port-lint: source meta.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span

// Like Path.parseModStyle, but accepts keywords in the path.
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
 * Make a parser that is usable with `parseMacroInput` in a
 * `@ProcMacroAttribute` macro.
 *
 * *Warning:* When parsing attribute args **other than** the
 * `procMacro.TokenStream` input of a `procMacroAttribute`, you do **not**
 * need this function. In several cases your callers will get worse error
 * messages if you use this function, because the surrounding delimiter's span
 * is concealed from attribute macros by rustc. Use
 * [Attribute.parseNestedMeta] instead.
 *
 * # Example
 *
 * This example implements an attribute macro whose invocations look like this:
 *
 * ```kotlin
 * @tea(kind = "EarlGrey", hot)
 * public open class Picard
 * ```
 *
 * The "parameters" supported by the attribute are:
 *
 * - `kind = "..."`
 * - `hot`
 * - `with(sugar, milk, ...)`, a comma-separated list of ingredients
 *
 * ```kotlin
 * import procmacro.TokenStream
 * import syn.LitStr
 * import syn.Path
 * import syn.SynResult
 * import syn.parseMacroInput
 *
 * @ProcMacroAttribute
 * fun tea(args: TokenStream, input: TokenStream): TokenStream {
 *     var kind: LitStr? = null
 *     var hot: Boolean = false
 *     var with: MutableList<Path> = mutableListOf()
 *     val teaParser = parser(closure@{ meta ->
 *         if (meta.path.isIdent("kind")) {
 *             kind = meta.value().getOrElse { return@closure SynResult.failure(it) }.parse(LitStr::parse).getOrElse { return@closure SynResult.failure(it) }
 *             SynResult.success(Unit)
 *         } else if (meta.path.isIdent("hot")) {
 *             hot = true
 *             SynResult.success(Unit)
 *         } else if (meta.path.isIdent("with")) {
 *             meta.parseNestedMeta({ meta ->
 *                 with.add(meta.path)
 *                 SynResult.success(Unit)
 *             })
 *         } else {
 *             SynResult.failure(meta.error("unsupported tea property"))
 *         }
 *     })
 *     parseMacroInput(args, with, teaParser)
 *     println("kind=$kind hot=$hot with=$with")
 *     // ...
 * }
 * ```
 *
 * The `syn.meta` library will take care of dealing with the commas including
 * trailing commas, and producing sensible error messages on unexpected input.
 *
 * ```console
 * error: expected `,`
 *  --> src/main.rs:3:37
 *   |
 * 3 | @tea(kind = "EarlGrey", with(sugar = "lol", milk))
 *   |                                     ^
 * ```
 *
 * # Example
 *
 * Same as above but we factor out most of the logic into a separate function.
 *
 * ```kotlin
 * import procmacro.TokenStream
 * import syn.LitStr
 * import syn.Path
 * import syn.SynResult
 * import syn.meta.ParseNestedMeta
 * import syn.parseMacroInput
 *
 * @ProcMacroAttribute
 * fun tea(args: TokenStream, input: TokenStream): TokenStream {
 *     var attrs = TeaAttributes.default()
 *     val teaParser = parser({ meta -> attrs.parse(meta) })
 *     parseMacroInput(args, with, teaParser)
 *     // ...
 *     return TokenStream.new()
 * }
 *
 * open class TeaAttributes(
 *     public var kind: LitStr?,
 *     public var hot: Boolean,
 *     public var with: List<Path>,
 * ) {
 *     public open fun parse(meta: ParseNestedMeta): SynResult<Unit> {
 *         return if (meta.path.isIdent("kind")) {
 *             this.kind = meta.value().getOrElse { return SynResult.failure(it) }.parse(LitStr::parse).getOrElse { return SynResult.failure(it) }
 *             SynResult.success(Unit)
 *         } else {
 *             // just like in last example
 *             SynResult.success(Unit)
 *         }
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
 * - [Attribute.parseNestedMeta] if you have an entire `Attribute` to
 *   parse. Always use this if possible. Generally this is able to produce
 *   better error messages because `Attribute` holds span information for all
 *   of the delimiters therein.
 *
 * - [parser] if you are implementing a `procMacroAttribute`
 *   macro and parsing the arguments to the attribute macro, i.e. the ones
 *   written in the same attribute that dispatched the macro invocation. Rustc
 *   does not pass span information for the surrounding delimiters into the
 *   attribute macro invocation in this situation, so error messages might be
 *   less precise.
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
     * `meta.parse<Eq>()`, so at most it is a minor convenience to
     * use `meta.value()`.
     *
     * # Example
     *
     * ```kotlin
     * import syn.Attribute
     * import syn.LitStr
     * import syn.SynResult
     * import syn.parseQuote
     *
     * val attr: Attribute = parseQuote("@tea(kind = \"EarlGrey\")")
     * // conceptually:
     * if (attr.path().isIdent("tea")) {
     *     // this parses the `tea`
     *     attr.parseNestedMeta(closure@{ meta ->
     *         // this parses the `(`
     *         if (meta.path.isIdent("kind")) {
     *             // this parses the `kind`
     *             val value = meta.value().getOrElse { return@closure SynResult.failure(it) }
     *             // this parses the `=`
     *             val s: LitStr = value.parse(LitStr::parse).getOrElse { return@closure SynResult.failure(it) }
     *             // this parses `"EarlGrey"`
     *             if (s.value() == "EarlGrey") {
     *                 // ...
     *             }
     *             SynResult.success(Unit)
     *         } else {
     *             SynResult.failure(meta.error("unsupported attribute"))
     *         }
     *     }).getOrElse { return SynResult.failure(it) }
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
     * ```kotlin
     * import syn.Attribute
     * import syn.SynResult
     * import syn.parseQuote
     *
     * val attr: Attribute = parseQuote("@tea(with(sugar, milk))")
     * if (attr.path().isIdent("tea")) {
     *     attr.parseNestedMeta({ meta ->
     *         if (meta.path.isIdent("with")) {
     *             meta.parseNestedMeta({ meta ->
     *                 // <---
     *                 if (meta.path.isIdent("sugar")) {
     *                     // Here we can go even deeper if needed.
     *                     SynResult.success(Unit)
     *                 } else if (meta.path.isIdent("milk")) {
     *                     SynResult.success(Unit)
     *                 } else {
     *                     SynResult.failure(meta.error("unsupported ingredient"))
     *                 }
     *             })
     *         } else {
     *             SynResult.failure(meta.error("unsupported tea property"))
     *         }
     *     }).getOrElse { return SynResult.failure(it) }
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
     * ```kotlin
     * import syn.Attribute
     * import syn.LitInt
     * import syn.SynResult
     * import syn.parenthesized
     * import syn.parseQuote
     *
     * val attr: Attribute = parseQuote("@repr(align(32))")
     * var align: LitInt? = null
     * if (attr.path().isIdent("repr")) {
     *     attr.parseNestedMeta(closure@{ meta ->
     *         if (meta.path.isIdent("align")) {
     *             val content = parenthesized(meta.input).getOrElse { return@closure SynResult.failure(it) }.content
     *             align = content.parse(LitInt::parse).getOrElse { return@closure SynResult.failure(it) }
     *             SynResult.success(Unit)
     *         } else {
     *             SynResult.failure(meta.error("unsupported repr"))
     *         }
     *     }).getOrElse { return SynResult.failure(it) }
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
     * ```kotlin
     * attr.parseNestedMeta({ meta ->
     *     if (meta.path.isIdent("kind")) {
     *         // ...
     *         SynResult.success(Unit)
     *     } else {
     *         SynResult.failure(meta.error("unsupported tea property"))
     *     }
     * }).getOrElse { return SynResult.failure(it) }
     * ```
     *
     * In this case, it behaves exactly like
     * `SynError.newSpanned(meta.path, "message...")`.
     *
     * ```console
     * error: unsupported tea property
     *  --> src/main.rs:3:26
     *   |
     * 3 | @tea(kind = "EarlGrey", wat = "foo")
     *   |                          ^^^
     * ```
     *
     * More usefully, the second place is if you've already parsed a value but
     * have decided not to accept the value:
     *
     * ```kotlin
     * import syn.Attribute
     * import syn.Expr
     * import syn.SynResult
     *
     * fun example(attr: Attribute): SynResult<Unit> {
     *     attr.parseNestedMeta(closure@{ meta ->
     *         if (meta.path.isIdent("kind")) {
     *             val expr: Expr = meta.value().getOrElse { return@closure SynResult.failure(it) }.parse(Expr::parse).getOrElse { return@closure SynResult.failure(it) }
     *             when (expr) {
     *                 is Expr.Lit -> SynResult.success(Unit)
     *                 is Expr.Path -> SynResult.success(Unit)
     *                 is Expr.Macro -> SynResult.success(Unit)
     *                 else -> SynResult.failure(meta.error("tea kind must be a string literal, path, or macro"))
     *             }
     *         } else {
     *             SynResult.success(Unit)
     *         }
     *     }).getOrElse { return SynResult.failure(it) }
     *     return SynResult.success(Unit)
     * }
     * ```
     *
     * ```console
     * error: tea kind must be a string literal, path, or macro
     *  --> src/main.rs:3:7
     *   |
     * 3 | @tea(kind = async { replicator.await })
     *   |       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     * ```
     *
     * Often you may want to use `SynError.newSpanned` even in this
     * situation. In the above code, that would be:
     *
     * ```kotlin
     * import syn.Expr
     * import syn.SynError
     * import syn.SynResult
     *
     * fun example(expr: Expr): SynResult<Unit> {
     *     return when (expr) {
     *         is Expr.Lit -> SynResult.success(Unit)
     *         is Expr.Path -> SynResult.success(Unit)
     *         is Expr.Macro -> SynResult.success(Unit)
     *         else -> SynResult.failure(SynError.newSpanned(expr, "unsupported expression type for `kind`"))
     *     }
     * }
     * ```
     *
     * ```console
     * error: unsupported expression type for `kind`
     *  --> src/main.rs:3:14
     *   |
     * 3 | @tea(kind = async { replicator.await })
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
