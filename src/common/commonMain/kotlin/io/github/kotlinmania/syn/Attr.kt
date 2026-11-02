// port-lint: source attr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * An outer or inner attribute node in a syntax tree.
 *
 * The [style] field of type [AttrStyle] distinguishes whether an attribute is
 * outer (decorating the following item) or inner (decorating the enclosing
 * module or block).
 *
 * Every attribute has a path indicating the interpretation of its contents,
 * stored together with any optional arguments inside [meta] in one of three
 * varieties:
 * - [Meta.PathMeta] — attributes carrying only a path.
 * - [Meta.List] — attributes carrying tokens surrounded by parentheses,
 *   brackets, or braces after the path.
 * - [Meta.NameValue] — attributes with an `=` token and expression after the
 *   path (including doc comments lowered to `doc = "..."`).
 *
 * Use [Attribute.parseOuter] or [Attribute.parseInner] to parse attributes
 * from a [ParseStream], and [parseArgs], [parseArgsWith], or [parseNestedMeta]
 * to parse structured arguments inside [Meta.List].
 */
public data class Attribute(
    public var poundToken: io.github.kotlinmania.syn.token.Pound,
    public var style: AttrStyle,
    public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
    public var meta: Meta,
) : ToTokens {
    public typealias Ret = Iterator<Attribute>

    public companion object {
        /**
         * Parses zero or more outer attributes from the stream.
         */
        public fun parseOuter(input: ParseStream): SynResult<List<Attribute>> =
            parseOuterAttributes(input)

        /**
         * Parses zero or more inner attributes from the stream.
         */
        public fun parseInner(input: ParseStream): SynResult<List<Attribute>> =
            parseInnerAttributes(input)
    }

    override fun toTokens(tokens: TokenStream) {
        poundToken.toTokens(tokens)
        style.toTokens(tokens)
        bracketToken.surround(tokens) { inner ->
            meta.toTokens(inner)
        }
    }

    public fun path(): Path =
        meta.path()

    public fun <T> parseArgs(parser: (ParseStream) -> SynResult<T>): SynResult<T> =
        parseArgsWith(parser)

    public fun <T> parseArgsWith(parser: (ParseStream) -> SynResult<T>): SynResult<T> {
        val metaList = metaListForArgs().getOrElse { return SynResult.failure(it) }
        return metaList.parseArgsWith(parser)
    }

    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> {
        val metaList = metaListForArgs().getOrElse { return SynResult.failure(it) }
        return metaList.parseNestedMeta(logic)
    }

    public fun deepCopy(): Attribute =
        copy(meta = meta.copy())

    public fun isOuter(): Boolean = style is AttrStyle.Outer

    public fun isInner(): Boolean = style is AttrStyle.Inner

    public fun fmt(): String = toString()
}

private fun Attribute.metaListForArgs(): SynResult<Meta.List> =
    when (val metaValue = meta) {
        is Meta.PathMeta -> {
            val first =
                metaValue.path
                    .segments
                    .first()
                    ?.ident
                    ?.span()
                    ?: Span.callSite()
            val last =
                metaValue.path
                    .segments
                    .last()
                    ?.ident
                    ?.span()
                    ?: first
            SynResult.failure(
                SynError.new2(
                    first,
                    last,
                    "expected attribute arguments in parentheses: ${DisplayAttrStyle(style)}[${DisplayPath(metaValue.path)}(...)]",
                ),
            )
        }

        is Meta.NameValue -> {
            SynResult.failure(
                SynError.new(
                    metaValue.eqToken.span,
                    "expected parentheses: ${DisplayAttrStyle(style)}[${DisplayPath(metaValue.path)}(...)]",
                ),
            )
        }

        is Meta.List -> {
            SynResult.success(metaValue)
        }
    }

public object AttributeParse {
    fun parse(input: ParseStream): SynResult<Attribute> =
        parseAttribute(input)
}

internal fun parseInnerAttributes(input: ParseStream): SynResult<List<Attribute>> =
    mutableListOf<Attribute>()
        .also { attrs ->
            parseInner(input, attrs).getOrElse { return SynResult.failure(it) }
        }.let { SynResult.success(it) }

internal fun parseOuterAttributes(input: ParseStream): SynResult<List<Attribute>> {
    val attrs = mutableListOf<Attribute>()
    while (input.peek(PoundPeek)) {
        attrs.add(singleParseOuter(input).getOrElse { return SynResult.failure(it) })
    }
    return SynResult.success(attrs)
}

internal fun parseInner(
    input: ParseStream,
    attrs: MutableList<Attribute>,
): SynResult<Unit> {
    while (input.peek(PoundPeek) && input.peek2(NotPeek)) {
        attrs.add(singleParseInner(input).getOrElse { return SynResult.failure(it) })
    }
    return SynResult.success(Unit)
}

internal fun singleParseInner(input: ParseStream): SynResult<Attribute> {
    val pound = PoundParse.parse(input).getOrElse { return SynResult.failure(it) }
    val style = AttrStyle.Inner(NotParse.parse(input).getOrElse { return SynResult.failure(it) })
    val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    val meta = MetaParse.parse(brackets.content).getOrElse { return SynResult.failure(it) }
    brackets.content.finishChildBuffer()
    val check = brackets.content.checkUnexpected()
    if (check.isFailure) return SynResult.failure(check.exceptionOrNull()!!)
    return SynResult.success(Attribute(pound, style, brackets.token, meta))
}

internal fun singleParseOuter(input: ParseStream): SynResult<Attribute> {
    val pound = PoundParse.parse(input).getOrElse { return SynResult.failure(it) }
    val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    val meta = MetaParse.parse(brackets.content).getOrElse { return SynResult.failure(it) }
    brackets.content.finishChildBuffer()
    val check = brackets.content.checkUnexpected()
    if (check.isFailure) return SynResult.failure(check.exceptionOrNull()!!)
    return SynResult.success(Attribute(pound, AttrStyle.Outer, brackets.token, meta))
}

private fun parseAttribute(input: ParseStream): SynResult<Attribute> {
    val pound = PoundParse.parse(input).getOrElse { return SynResult.failure(it) }
    val style: AttrStyle =
        if (input.peek(NotPeek)) {
            AttrStyle.Inner(NotParse.parse(input).getOrElse { return SynResult.failure(it) })
        } else {
            AttrStyle.Outer
        }
    val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    val meta = MetaParse.parse(brackets.content).getOrElse { return SynResult.failure(it) }
    brackets.content.finishChildBuffer()
    val check = brackets.content.checkUnexpected()
    if (check.isFailure) return SynResult.failure(check.exceptionOrNull()!!)
    return SynResult.success(Attribute(pound, style, brackets.token, meta))
}

/**
 * Distinguishes between attributes that decorate an item and attributes
 * that are contained within an item.
 */
public sealed class AttrStyle : ToTokens {
    public data object Outer : AttrStyle()

    public data class Inner(
        var bangToken: io.github.kotlinmania.syn.token.Not,
    ) : AttrStyle()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            Outer -> Unit
            is Inner -> bangToken.toTokens(tokens)
        }
    }
}

/**
 * Content of a compile-time structured attribute.
 *
 * - [PathMeta] — a bare path such as `test`.
 * - [List] — a path with delimited arguments such as `derive(Copy)`.
 * - [NameValue] — a key-value pair such as `path = "sys.rs"`.
 */
public sealed class Meta : ToTokens {
    public companion object {
        internal fun from(meta: Path): Meta =
            PathMeta(meta)

        internal fun from(meta: List): Meta =
            meta

        internal fun from(meta: NameValue): Meta =
            meta
    }

    public data class PathMeta(
        var path: Path,
    ) : Meta() {
        override fun toTokens(tokens: TokenStream) {
            printPath(tokens, path, PathStyle.Mod)
        }
    }

    /**
     * A structured list within an attribute, like `derive(Copy, Clone)`.
     */
    public data class List(
        var path: Path,
        var delimiter: MacroDelimiter,
        var tokens: TokenStream,
    ) : Meta() {
        public fun <T> parseArgs(parser: (ParseStream) -> SynResult<T>): SynResult<T> =
            parseArgsWith(parser)

        public fun <T> parseArgsWith(parser: (ParseStream) -> SynResult<T>): SynResult<T> =
            parseScoped(parser, delimiter.closeSpan(), tokens.clone())

        public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
            parseScoped(parser(logic), delimiter.closeSpan(), tokens.clone())

        override fun toTokens(tokens: TokenStream) {
            printPath(tokens, path, PathStyle.Mod)
            delimiter.surround(tokens, this.tokens)
        }
    }

    /**
     * A name-value pair within an attribute, like `feature = "nightly"`.
     */
    public data class NameValue(
        var path: Path,
        var eqToken: io.github.kotlinmania.syn.token.Eq,
        var value: Expr,
    ) : Meta() {
        override fun toTokens(tokens: TokenStream) {
            printPath(tokens, path, PathStyle.Mod)
            eqToken.toTokens(tokens)
            value.toTokens(tokens)
        }
    }

    public fun path(): Path =
        when (this) {
            is PathMeta -> path
            is List -> path
            is NameValue -> path
        }

    /**
     * Error if this is a [Meta.List] or [Meta.NameValue].
     */
    public fun requirePathOnly(): SynResult<Path> =
        when (this) {
            is PathMeta -> SynResult.success(path)
            is List -> SynResult.failure(SynError.new(delimiter.openSpan(), "unexpected token in attribute"))
            is NameValue -> SynResult.failure(SynError.new(eqToken.span, "unexpected token in attribute"))
        }

    /**
     * Error if this is a [Meta.PathMeta] or [Meta.NameValue].
     */
    public fun requireList(): SynResult<List> =
        when (this) {
            is List -> {
                SynResult.success(this)
            }

            is PathMeta -> {
                val first =
                    path
                        .segments
                        .first()
                        ?.ident
                        ?.span()
                        ?: Span.callSite()
                val last =
                    path
                        .segments
                        .last()
                        ?.ident
                        ?.span()
                        ?: first
                SynResult.failure(
                    SynError.new2(
                        first,
                        last,
                        "expected attribute arguments in parentheses: `${DisplayPath(path)}(...)`",
                    ),
                )
            }

            is NameValue -> {
                SynResult.failure(SynError.new(eqToken.span, "expected `(`"))
            }
        }

    /**
     * Error if this is a [Meta.PathMeta] or [Meta.List].
     */
    public fun requireNameValue(): SynResult<NameValue> =
        when (this) {
            is NameValue -> {
                SynResult.success(this)
            }

            is PathMeta -> {
                val first =
                    path
                        .segments
                        .first()
                        ?.ident
                        ?.span()
                        ?: Span.callSite()
                val last =
                    path
                        .segments
                        .last()
                        ?.ident
                        ?.span()
                        ?: first
                SynResult.failure(
                    SynError.new2(
                        first,
                        last,
                        "expected a value for this attribute: `${DisplayPath(path)} = ...`",
                    ),
                )
            }

            is List -> {
                SynResult.failure(SynError.new(delimiter.openSpan(), "expected `=`"))
            }
        }

    public fun copy(): Meta =
        when (this) {
            is PathMeta -> copy(path = path.deepCopy())
            is List -> copy(path = path.deepCopy(), tokens = tokens.clone())
            is NameValue -> copy(path = path.deepCopy(), value = value.deepCopy())
        }
}

/** Parser for [Meta]: a path, a path followed by a delimited token stream, or a path followed by `=` and an expression. */
public object MetaParse {
    fun parse(input: ParseStream): SynResult<Meta> {
        val path = parseOutermostMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaAfterPath(path, input)
    }
}

public object MetaListParse {
    fun parse(input: ParseStream): SynResult<Meta.List> {
        val path = parseOutermostMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaListAfterPath(path, input)
    }
}

public object MetaNameValueParse {
    fun parse(input: ParseStream): SynResult<Meta.NameValue> {
        val path = parseOutermostMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaNameValueAfterPath(path, input)
    }
}

internal fun parseOutermostMetaPath(input: ParseStream): SynResult<Path> =
    if (input.peek(UnsafePeek)) {
        val unsafeToken = UnsafeParse.parse(input).getOrElse { return SynResult.failure(it) }
        SynResult.success(Path.from(Ident.new("unsafe", unsafeToken.span)))
    } else {
        Path.parseModStyle(input)
    }

internal fun parseMetaAfterPath(path: Path, input: ParseStream): SynResult<Meta> =
    if (input.peek(ParenPeek) || input.peek(BracketPeek) || input.peek(BracePeek)) {
        parseMetaListAfterPath(path, input).map { Meta.List(it.path, it.delimiter, it.tokens) }
    } else if (input.peek(EqPeek) && !input.peek(EqEqPeek) && !input.peek(FatArrowPeek)) {
        parseMetaNameValueAfterPath(path, input).map { Meta.NameValue(it.path, it.eqToken, it.value) }
    } else {
        SynResult.success(Meta.PathMeta(path))
    }

internal fun parseMetaListAfterPath(path: Path, input: ParseStream): SynResult<Meta.List> {
    var (delimiter, tokens) = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Meta.List(path, delimiter, tokens))
}

internal fun parseMetaNameValueAfterPath(path: Path, input: ParseStream): SynResult<Meta.NameValue> {
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ahead = input.fork()
    val lit = LitParse.parse(ahead)
    val value: Expr =
        if (lit is SynResult.Success && ahead.isEmpty()) {
            input.advanceTo(ahead)
            Expr.Lit(attrs = mutableListOf(), lit = lit.value)
        } else if (input.peek(PoundPeek) && input.peek2(BracketPeek)) {
            return SynResult.failure(input.error("unexpected attribute inside of attribute"))
        } else {
            parseExprFull(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(Meta.NameValue(path, eqToken, value))
}

internal interface FilterAttrs {
    fun outer(): Iterable<Attribute>

    fun inner(): Iterable<Attribute>
}

internal class AttributeFilterAttrs(
    private val attrs: Iterable<Attribute>,
) : FilterAttrs {
    override fun outer(): Iterable<Attribute> =
        attrs.filter { it.style is AttrStyle.Outer }

    override fun inner(): Iterable<Attribute> =
        attrs.filter { it.style is AttrStyle.Inner }
}

internal fun Iterable<Attribute>.filterAttrs(): FilterAttrs =
    AttributeFilterAttrs(this)

internal fun MacroDelimiter.openSpan(): Span =
    when (this) {
        is MacroDelimiter.Paren -> token.span.open()
        is MacroDelimiter.Brace -> token.span.open()
        is MacroDelimiter.Bracket -> token.span.open()
    }

internal fun MacroDelimiter.closeSpan(): Span =
    when (this) {
        is MacroDelimiter.Paren -> token.span.close()
        is MacroDelimiter.Brace -> token.span.close()
        is MacroDelimiter.Bracket -> token.span.close()
    }

internal class DisplayAttrStyle(
    private val style: AttrStyle,
) {
    override fun toString(): String =
        when (style) {
            AttrStyle.Outer -> "#"
            is AttrStyle.Inner -> "#!"
        }
}

internal class DisplayPath(
    private val path: Path,
) {
    override fun toString(): String =
        path.toString()
}
