@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
// port-lint: source attr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

/** An attribute attached to an item or field. */
public data class Attribute(
    public val poundToken: io.github.kotlinmania.syn.token.Pound,
    public val style: AttrStyle,
    public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
    public val meta: Meta,
) {
    public fun path(): Path =
        meta.path()

    @HiddenFromObjC
    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
        when (val metaValue = meta) {
            is Meta.List -> metaValue.parseNestedMeta(logic)
            else -> SynResult.failure(SynError.new(path().getIdent()?.span() ?: io.github.kotlinmania.procmacro2.Span.callSite(), "expected attribute arguments in parentheses"))
        }

    public fun deepCopy(): Attribute =
        copy(meta = meta.copy())
}

public sealed class AttrStyle {
    public data object Outer : AttrStyle()
    public data class Inner(val bangToken: io.github.kotlinmania.syn.token.Not) : AttrStyle()
}

/** Content of an attribute. */
public sealed class Meta {
    public data class PathMeta(val path: Path) : Meta()
    public data class List(val path: Path, val delimiter: MacroDelimiter, val tokens: TokenStream) : Meta()
    public data class NameValue(val path: Path, val eqToken: io.github.kotlinmania.syn.token.Eq, val value: Expr) : Meta()

    public fun path(): Path =
        when (this) {
            is PathMeta -> path
            is List -> path
            is NameValue -> path
        }

    public fun copy(): Meta =
        when (this) {
            is PathMeta -> copy(path = path.copy())
            is List -> copy(path = path.copy())
            is NameValue -> copy(path = path.copy(), value = value.copy())
        }

}

/** Context passed to a nested attribute parser. */
public data class ParseNestedMeta(
    public val path: Path,
    public val input: TokenStream = TokenStream.new(),
) {
    public fun value(): TokenStream =
        input

    @HiddenFromObjC
    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> =
        parseNestedMetaTokens(input, logic)
}

@HiddenFromObjC
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

public typealias MetaList = Meta.List
public typealias MetaNameValue = Meta.NameValue
