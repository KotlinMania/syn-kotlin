// port-lint: source attr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens

/** An attribute attached to an item or field. */
public data class Attribute(
    public val poundToken: io.github.kotlinmania.syn.token.Pound,
    public val style: AttrStyle,
    public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
    public val meta: Meta,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        poundToken.toTokens(tokens)
        style.toTokens(tokens)
        bracketToken.surround(tokens) { inner ->
            meta.toTokens(inner)
        }
    }

    public fun path(): Path =
        meta.path()

    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
        when (val metaValue = meta) {
            is Meta.List -> metaValue.parseNestedMeta(logic)
            else ->
                SynResult.failure(
                    SynError.new(
                        path().getIdent()?.span() ?: io.github.kotlinmania.procmacro2.Span
                            .callSite(),
                        "expected attribute arguments in parentheses",
                    ),
                )
        }

    public fun deepCopy(): Attribute =
        copy(meta = meta.copy())
}

public object AttributeParse : Parse<Attribute> {
    override fun parse(input: ParseStream): SynResult<Attribute> =
        parseAttribute(input)
}

internal fun parseInnerAttributes(input: ParseStream): SynResult<List<Attribute>> =
    parseAttributes(input, inner = true)

internal fun parseOuterAttributes(input: ParseStream): SynResult<List<Attribute>> =
    parseAttributes(input, inner = false)

private fun parseAttributes(
    input: ParseStream,
    inner: Boolean,
): SynResult<List<Attribute>> {
    val attrs = mutableListOf<Attribute>()
    while (input.peek(PoundPeek) && (inner == input.peek2(NotPeek))) {
        attrs.add(parseAttribute(input).getOrElse { return SynResult.failure(it) })
    }
    return SynResult.success(attrs)
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
        val path = parseMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaAfterPath(path, input)
    }
}

/** Parser for [Meta.List]: a path followed by a delimited token stream. */
public object MetaListParse : Parse<Meta.List> {
    override fun parse(input: ParseStream): SynResult<Meta.List> {
        val path = parseMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaListAfterPath(path, input)
    }
}

/** Parser for [Meta.NameValue]: a path followed by `=` and an expression. */
public object MetaNameValueParse : Parse<Meta.NameValue> {
    override fun parse(input: ParseStream): SynResult<Meta.NameValue> {
        val path = parseMetaPath(input).getOrElse { return SynResult.failure(it) }
        return parseMetaNameValueAfterPath(path, input)
    }
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

/** Context passed to a nested attribute parser. */
public data class ParseNestedMeta(
    public val path: Path,
    public val input: TokenStream = TokenStream.new(),
) {
    public fun value(): TokenStream =
        input

    /** Reports an error spanning from the path to the current position. */
    public fun error(msg: String): SynError {
        val startSpan =
            path.segments
                .first()
                ?.ident
                ?.span()
                ?: io.github.kotlinmania.procmacro2.Span
                    .callSite()
        val endSpan =
            io.github.kotlinmania.procmacro2.Span
                .callSite()
        return SynError.newAt(startSpan, endSpan, msg)
    }

    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
        parseNestedMetaTokens(input, logic)
}

public fun Meta.List.parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
    parseNestedMetaTokens(tokens, logic)

private fun parseNestedMetaTokens(
    tokens: TokenStream,
    logic: (ParseNestedMeta) -> SynResult<Unit>,
): SynResult<Unit> {
    for (path in nestedMetaPaths(tokens)) {
        val result = logic(ParseNestedMeta(path))
        if (result.isFailure) {
            return result
        }
    }
    return SynResult.success(Unit)
}

private fun nestedMetaPaths(tokens: TokenStream): List<Path> =
    buildList {
        fun visit(stream: TokenStream) {
            for (tree in stream) {
                when (tree) {
                    is TokenTree.Ident -> add(Path.from(tree.value.copy()))
                    is TokenTree.Group -> {
                        if (tree.value.delimiter() == Delimiter.Parenthesis) {
                            visit(tree.value.stream())
                        }
                    }
                    else -> {}
                }
            }
        }
        visit(tokens)
    }
