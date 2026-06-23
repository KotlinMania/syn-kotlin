// port-lint: source attr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens

/** An attribute attached to an item or field. */
public data class Attribute(
    public val poundToken: io.github.kotlinmania.syn.token.Pound,
    public val style: AttrStyle,
    public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
    public val meta: Meta,
) : ToTokens {
    public companion object {
        public fun parseOuter(input: ParseStream): SynResult<List<Attribute>> =
            parseOuterAttributes(input)

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

    public fun <T> parseArgs(parser: Parse<T>): SynResult<T> =
        parseArgsWith(parserFromFunction(parser::parse))

    public fun <T> parseArgsWith(parser: (ParseStream) -> SynResult<T>): SynResult<T> =
        parseArgsWith(parserFromFunction(parser))

    public fun <T> parseArgsWith(parser: Parser<T>): SynResult<T> =
        when (val metaValue = meta) {
            is Meta.PathMeta -> {
                val first = metaValue.path.segments.first()?.ident?.span() ?: Span.callSite()
                val last = metaValue.path.segments.last()?.ident?.span() ?: first
                SynResult.failure(
                    SynError.new2(
                        first,
                        last,
                        "expected attribute arguments in parentheses: ${DisplayAttrStyle(style)}[${DisplayPath(metaValue.path)}(...)]",
                    ),
                )
            }
            is Meta.NameValue ->
                SynResult.failure(
                    SynError.new(
                        metaValue.eqToken.span,
                        "expected parentheses: ${DisplayAttrStyle(style)}[${DisplayPath(metaValue.path)}(...)]",
                    ),
                )
            is Meta.List -> metaValue.parseArgsWith(parser)
        }

    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
        parseArgsWith(parser(logic))

    public fun deepCopy(): Attribute =
        copy(meta = meta.copy())
}

public object AttributeParse : Parse<Attribute> {
    override fun parse(input: ParseStream): SynResult<Attribute> =
        parseAttribute(input)
}

internal fun parseInnerAttributes(input: ParseStream): SynResult<List<Attribute>> =
    mutableListOf<Attribute>().also { attrs ->
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
    val pound = input.parse(PoundParse).getOrElse { return SynResult.failure(it) }
    val style = AttrStyle.Inner(input.parse(NotParse).getOrElse { return SynResult.failure(it) })
    val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    val meta = brackets.content.parse(MetaParse).getOrElse { return SynResult.failure(it) }
    brackets.content.finishChildBuffer()
    val check = brackets.content.checkUnexpected()
    if (check.isFailure) return SynResult.failure(check.exceptionOrNull()!!)
    return SynResult.success(Attribute(pound, style, brackets.token, meta))
}

internal fun singleParseOuter(input: ParseStream): SynResult<Attribute> {
    val pound = input.parse(PoundParse).getOrElse { return SynResult.failure(it) }
    val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    val meta = brackets.content.parse(MetaParse).getOrElse { return SynResult.failure(it) }
    brackets.content.finishChildBuffer()
    val check = brackets.content.checkUnexpected()
    if (check.isFailure) return SynResult.failure(check.exceptionOrNull()!!)
    return SynResult.success(Attribute(pound, AttrStyle.Outer, brackets.token, meta))
}

private fun parseAttribute(input: ParseStream): SynResult<Attribute> {
    val pound = input.parse(PoundParse).getOrElse { return SynResult.failure(it) }
    val style: AttrStyle =
        if (input.peek(NotPeek)) {
            AttrStyle.Inner(input.parse(NotParse).getOrElse { return SynResult.failure(it) })
        } else {
            AttrStyle.Outer
        }
    val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    val meta = brackets.content.parse(MetaParse).getOrElse { return SynResult.failure(it) }
    brackets.content.finishChildBuffer()
    val check = brackets.content.checkUnexpected()
    if (check.isFailure) return SynResult.failure(check.exceptionOrNull()!!)
    return SynResult.success(Attribute(pound, style, brackets.token, meta))
}

public sealed class AttrStyle : ToTokens {
    public data object Outer : AttrStyle() {
        override fun toTokens(tokens: TokenStream) {
            // outer attribute emits nothing before the bracket
        }
    }

    public data class Inner(
        val bangToken: io.github.kotlinmania.syn.token.Not,
    ) : AttrStyle() {
        override fun toTokens(tokens: TokenStream) {
            bangToken.toTokens(tokens)
        }
    }
}

/** Content of an attribute. */
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
        val path: Path,
    ) : Meta() {
        override fun toTokens(tokens: TokenStream) {
            path.toTokens(tokens)
        }
    }

    public data class List(
        val path: Path,
        val delimiter: MacroDelimiter,
        val tokens: TokenStream,
    ) : Meta() {
        public fun <T> parseArgs(parser: Parse<T>): SynResult<T> =
            parseArgsWith(parserFromFunction(parser::parse))

        public fun <T> parseArgsWith(parser: (ParseStream) -> SynResult<T>): SynResult<T> =
            parseArgsWith(parserFromFunction(parser))

        public fun <T> parseArgsWith(parser: Parser<T>): SynResult<T> =
            parseScoped(parser, delimiter.closeSpan(), tokens)

        public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
            parseArgsWith(parser(logic))

        override fun toTokens(tokens: TokenStream) {
            path.toTokens(tokens)
            delimiter.surround(tokens, this.tokens)
        }
    }

    public data class NameValue(
        val path: Path,
        val eqToken: io.github.kotlinmania.syn.token.Eq,
        val value: Expr,
    ) : Meta() {
        override fun toTokens(tokens: TokenStream) {
            path.toTokens(tokens)
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

    public fun requirePathOnly(): SynResult<Path> =
        when (this) {
            is PathMeta -> SynResult.success(path)
            is List -> SynResult.failure(SynError.new(delimiter.openSpan(), "unexpected token in attribute"))
            is NameValue -> SynResult.failure(SynError.new(eqToken.span, "unexpected token in attribute"))
        }

    public fun requireList(): SynResult<List> =
        when (this) {
            is List -> SynResult.success(this)
            is PathMeta -> {
                val first = path.segments.first()?.ident?.span() ?: Span.callSite()
                val last = path.segments.last()?.ident?.span() ?: first
                SynResult.failure(
                    SynError.new2(
                        first,
                        last,
                        "expected attribute arguments in parentheses: `${DisplayPath(path)}(...)`",
                    ),
                )
            }
            is NameValue -> SynResult.failure(SynError.new(eqToken.span, "expected `(`"))
        }

    public fun requireNameValue(): SynResult<NameValue> =
        when (this) {
            is NameValue -> SynResult.success(this)
            is PathMeta -> {
                val first = path.segments.first()?.ident?.span() ?: Span.callSite()
                val last = path.segments.last()?.ident?.span() ?: first
                SynResult.failure(
                    SynError.new2(
                        first,
                        last,
                        "expected a value for this attribute: `${DisplayPath(path)} = ...`",
                    ),
                )
            }
            is List -> SynResult.failure(SynError.new(delimiter.openSpan(), "expected `=`"))
        }

    public fun copy(): Meta =
        when (this) {
            is PathMeta -> copy(path = path.deepCopy())
            is List -> copy(path = path.deepCopy())
            is NameValue -> copy(path = path.deepCopy(), value = value.deepCopy())
        }
}

/** Parser for [Meta]: a path, a path followed by a delimited token stream, or a path followed by `=` and an expression. */
public object MetaParse : Parse<Meta> {
    override fun parse(input: ParseStream): SynResult<Meta> {
        val path = parseOutermostMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaAfterPath(path, input)
    }
}

/** Parser for [Meta.List]: a path followed by a delimited token stream. */
public object MetaListParse : Parse<Meta.List> {
    override fun parse(input: ParseStream): SynResult<Meta.List> {
        val path = parseOutermostMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaListAfterPath(path, input)
    }
}

/** Parser for [Meta.NameValue]: a path followed by `=` and an expression. */
public object MetaNameValueParse : Parse<Meta.NameValue> {
    override fun parse(input: ParseStream): SynResult<Meta.NameValue> {
        val path = parseOutermostMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaNameValueAfterPath(path, input)
    }
}

internal fun parseOutermostMetaPath(input: ParseStream): SynResult<Path> =
    if (input.peek(UnsafePeek)) {
        val unsafeToken = input.parse(UnsafeParse).getOrElse { return SynResult.failure(it) }
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
    val (delimiter, tokens) = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Meta.List(path, delimiter, tokens))
}

internal fun parseMetaNameValueAfterPath(path: Path, input: ParseStream): SynResult<Meta.NameValue> {
    val eqToken = input.parse(EqParse).getOrElse { return SynResult.failure(it) }
    val ahead = input.fork()
    val lit = ahead.parse(LitParse)
    val value: Expr =
        if (lit is SynResult.Success && ahead.isEmpty()) {
            input.advanceTo(ahead)
            Expr.Lit(attrs = emptyList(), lit = lit.value)
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
