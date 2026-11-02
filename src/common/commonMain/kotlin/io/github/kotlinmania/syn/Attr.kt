// port-lint: source attr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * An attribute, like ``.
 *
 * <br>
 *
 * # Syntax
 *
 * Rust has six types of attributes.
 *
 * - Outer attributes like ``. These appear outside or
 *   in front of the item they describe.
 *
 * - Inner attributes like ``. These appear inside
 *   of the item they describe, usually a module.
 *
 * - Outer one-line doc comments like `/// Example`.
 *
 * - Inner one-line doc comments like `//! Please file an issue`.
 *
 * - Outer documentation blocks `/ ** Example * /`.
 *
 * - Inner documentation blocks `/ *! Please file an issue * /`.
 *
 * The `style` field of type `AttrStyle` distinguishes whether an attribute
 * is outer or inner.
 *
 * Every attribute has a `path` that indicates the intended interpretation
 * of the rest of the attribute's contents. The path and the optional
 * additional contents are represented together in the `meta` field of the
 * attribute in three possible varieties:
 *
 * - Meta::Path &mdash; attributes whose information content conveys just a
 *   path, for example the `` attribute.
 *
 * - Meta::List &mdash; attributes that carry arbitrary tokens after the
 *   path, surrounded by a delimiter (parenthesis, bracket, or brace). For
 *   example `` or ``.
 *
 * - Meta::NameValue &mdash; attributes with an `=` sign after the path,
 *   followed by a Rust expression. For example `#[path =
 *   "sys/windows.rs"]`.
 *
 * All doc comments are represented in the NameValue style with a path of
 * "doc", as this is how they are processed by the compiler and by
 * `macroRules` macros.
 *
 * ```text
 *
 *   ~~~~~~Path
 *   ^^^^^^^^^^^^^^^^^^^Meta::List
 *
 *
 *   ~~~~Path
 *   ^^^^^^^^^^^^^^^^^^^^^^^Meta::NameValue
 *
 *
 *   ^^^^Meta::Path
 * ```
 *
 * <br>
 *
 * # Parsing from tokens to Attribute
 *
 * This type does not implement the [`Parse`] trait and thus cannot be
 * parsed directly by [`ParseStream::parse`]. Instead use
 * [`ParseStream::call`] with one of the two parser functions
 * [`Attribute::parseOuter`] or [`Attribute::parseInner`] depending on
 * which you intend to parse.
 *
 * [`Parse`]: crate::parse::Parse
 * [`ParseStream::parse`]: crate::parse::ParseBuffer::parse
 * [`ParseStream::call`]: crate::parse::ParseBuffer::call
 *
 * ```
 * import syn..Attribute, Ident, Result, Token};
 * import syn.parse..Parse, ParseStream};
 *
 * // Parses a unit struct with attributes.
 * //
 * //
 * //     struct S;
 * struct UnitStruct {
 *     attrs: Vec<Attribute>,
 *     structToken: token[struct],
 *     name: Ident,
 *     semiToken: token[;],
 * }
 *
 * class UnitStruct : Parse {
 *     fun parse(input: ParseStream) -> Result<Self> {
 *         Ok(UnitStruct {
 *             attrs: input.call(Attribute::parseOuter)?,
 *             structToken: input.parse()?,
 *             name: input.parse()?,
 *             semiToken: input.parse()?,
 *         })
 *     }
 * }
 * ```
 *
 * <p><br></p>
 *
 * # Parsing from Attribute to structured arguments
 *
 * The grammar of attributes in Rust is very flexible, which makes the
 * syntax tree not that useful on its own. In particular, arguments of the
 * `Meta::List` variety of attribute are held in an arbitrary `tokens:
 * TokenStream`. Macros are expected to check the `path` of the attribute,
 * decide whether they recognize it, and then parse the remaining tokens
 * according to whatever grammar they wish to require for that kind of
 * attribute. Use [`parseArgs()`] to parse those tokens into the expected
 * data structure.
 *
 * [`parseArgs()`]: Attribute::parseArgs
 *
 * <p><br></p>
 *
 * # Doc comments
 *
 * The compiler transforms doc comments, such as `/// comment` and `/ *!
 * comment * /`, into attributes before macros are expanded. Each comment is
 * expanded into an attribute of the form ``.
 *
 * As an example, the following `mod` items are expanded identically:
 *
 * ```
 * val doc: ItemMod = parseQuote{
 *     /// Single line doc comments
 *     /// We write so many!
 *     / **
 *      * Multi-line comments...
 *      * May span many lines
 *      * /
 *     mod example {
 *         //! Of course, they can be inner too
 *         / *! And fit in a single line * /
 *     }
 * };
 * val attr: ItemMod = parseQuote{
 *
 *
 *     #[doc = r"
 *      * Multi-line comments...
 *      * May span many lines
 *      "]
 *     mod example {
 *
 *
 *     }
 * };
 * assertEq(doc, attr);
 * ```
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
         *
         * # Example
         *
         * See
         * [*Parsing from tokens to Attribute*](#parsing-from-tokens-to-attribute).
         */
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
    while (input.peek(PoundPeek) && input.peek2(BracketPeek)) {
        attrs.add(singleParseOuter(input).getOrElse { return SynResult.failure(it) })
    }
    return SynResult.success(attrs)
}

internal fun parseInner(
    input: ParseStream,
    attrs: MutableList<Attribute>,
): SynResult<Unit> {
    while (input.peek(PoundPeek) && input.peek2(NotPeek) && input.peek3(BracketPeek)) {
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
 *
 * # Outer attributes
 *
 * - ``
 * - `/// # Example`
 * - `/ ** Please file an issue * /`
 *
 * # Inner attributes
 *
 * - ``
 * - `//! # Example`
 * - `/ *! Please file an issue * /`
 */
public sealed class AttrStyle : ToTokens {
    public data object Outer : AttrStyle() {
        override fun toTokens(tokens: TokenStream) {
            // outer attribute emits nothing before the bracket
        }
    }

    public data class Inner(
        var bangToken: io.github.kotlinmania.syn.token.Not,
    ) : AttrStyle() {
        override fun toTokens(tokens: TokenStream) {
            bangToken.toTokens(tokens)
        }
    }
}

/**
 * Content of a compile-time structured attribute.
 *
 * ## Path
 *
 * A meta path is like the `test` in ``.
 *
 * ## List
 *
 * A meta list is like the `derive(Copy)` in ``.
 *
 * ## NameValue
 *
 * A name-value meta is like the `path = "..."` in `#[path =
 * "sys/windows.rs"]`.
 *
 * # Syntax tree enum
 *
 * This type is a [syntax tree enum].
 *
 * [syntax tree enum]: crate::expr::Expr#syntax-tree-enums
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
            path.toTokens(tokens)
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
            parseScoped(parser, delimiter.closeSpan(), tokens)

        public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
            parseScoped(parser(logic), delimiter.closeSpan(), tokens)

        override fun toTokens(tokens: TokenStream) {
            path.toTokens(tokens)
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

    /**
     * Error if this is a `Meta::List` or `Meta::NameValue`.
     */
    public fun requirePathOnly(): SynResult<Path> =
        when (this) {
            is PathMeta -> SynResult.success(path)
            is List -> SynResult.failure(SynError.new(delimiter.openSpan(), "unexpected token in attribute"))
            is NameValue -> SynResult.failure(SynError.new(eqToken.span, "unexpected token in attribute"))
        }

    /**
     * Error if this is a `Meta::Path` or `Meta::NameValue`.
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
     * Error if this is a `Meta::Path` or `Meta::List`.
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
            is List -> copy(path = path.deepCopy())
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
