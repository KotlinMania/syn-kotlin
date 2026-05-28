// port-lint: source restriction.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.In
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Pub
import io.github.kotlinmania.quote.ToTokens
import kotlin.native.HiddenFromObjC

/** Visibility of an item: `pub`, `pub(restricted)`, or inherited (private). */
public sealed class Visibility : ToTokens {
    public data class Public(val pubToken: Pub) : Visibility() {
        override fun toTokens(tokens: TokenStream) {
            pubToken.toTokens(tokens)
        }
    }

    public data class Restricted(
        val pubToken: Pub,
        val parenToken: Paren,
        val inToken: In?,
        val path: Path,
    ) : Visibility() {
        override fun toTokens(tokens: TokenStream) {
            pubToken.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                inToken?.toTokens(inner)
                path.toTokens(inner)
            }
        }
    }

    public data object Inherited : Visibility() {
        override fun toTokens(tokens: TokenStream) {
            // inherited (private) visibility emits nothing
        }
    }

    /** Returns true if this is not inherited (i.e., has an explicit visibility keyword). */
    public fun isSome(): Boolean = this !is Inherited
}

/** Field mutability marker. Reserved for RFC 3323. */
public sealed class FieldMutability : ToTokens {
    public data object None : FieldMutability() {
        override fun toTokens(tokens: TokenStream) {
            // immutable field emits nothing
        }
    }

    public data class Mut(val token: io.github.kotlinmania.syn.token.Mut) : FieldMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

/** Strongly-typed parser for visibility. */
@HiddenFromObjC
public object VisibilityParse : Parse<Visibility> {
    override fun parse(input: ParseStream): SynResult<Visibility> {
        if (!input.peek(PubPeek)) {
            return SynResult.success(Visibility.Inherited)
        }

        val pubToken = input.parse(PubParse).getOrThrow()

        if (input.peek(ParenPeek)) {
            val ahead = input.fork()
            val parens = parseParens(ahead).getOrElse {
                return SynResult.success(Visibility.Public(pubToken))
            }
            val content = parens.content
            if (content.peek(IdentPeekAny)) {
                val ident = content.parse(IdentParse).getOrElse {
                    input.advanceTo(ahead)
                    return SynResult.success(Visibility.Public(pubToken))
                }
                val identStr = ident.toString()
                if ((identStr == "crate" || identStr == "self" || identStr == "super") && content.isEmpty()) {
                    input.advanceTo(ahead)
                    return SynResult.success(Visibility.Restricted(pubToken, parens.token, null, Path.from(ident)))
                }
            }
            if (content.peek(InPeek)) {
                val inToken = content.parse(InParse).getOrNull()
                val path = content.parse(PathParse).getOrElse {
                    input.advanceTo(ahead)
                    return SynResult.success(Visibility.Public(pubToken))
                }
                if (content.isEmpty()) {
                    input.advanceTo(ahead)
                    return SynResult.success(Visibility.Restricted(pubToken, parens.token, inToken, path))
                }
            }
        }

        return SynResult.success(Visibility.Public(pubToken))
    }
}

@HiddenFromObjC
public object PubPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "pub"
    }
    override fun display(): String = "`pub`"
}

@HiddenFromObjC
public object PubParse : Parse<Pub> {
    override fun parse(input: ParseStream): SynResult<Pub> =
        input.step { cursor ->
            val (ident, rest) = cursor.ident() ?: return@step SynResult.failure(cursor.error("expected `pub`"))
            if (ident.toString() != "pub") return@step SynResult.failure(cursor.error("expected `pub`"))
            SynResult.success(Pub.from(ident.span()) to rest)
        }
}

@HiddenFromObjC
public object InPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "in"
    }
    override fun display(): String = "`in`"
}

@HiddenFromObjC
public object InParse : Parse<In> {
    override fun parse(input: ParseStream): SynResult<In> =
        input.step { cursor ->
            val (ident, rest) = cursor.ident() ?: return@step SynResult.failure(cursor.error("expected `in`"))
            if (ident.toString() != "in") return@step SynResult.failure(cursor.error("expected `in`"))
            SynResult.success(In.from(ident.span()) to rest)
        }
}

@HiddenFromObjC
public object ParenPeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.group(io.github.kotlinmania.procmacro2.Delimiter.Parenthesis) != null
    override fun display(): String = "`(`"
}
