// port-lint: source restriction.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.syn.token.In
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Pub

/** Visibility of an item: `pub`, `pub(restricted)`, or inherited (private). */
public sealed class Visibility : ToTokens {
    public data class Public(
        var pubToken: Pub,
    ) : Visibility() {
        override fun toTokens(tokens: TokenStream) {
            pubToken.toTokens(tokens)
        }
    }

    public data class Restricted(
        var pubToken: Pub,
        var parenToken: Paren,
        var inToken: In?,
        var path: Path,
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

    public data class Mut(
        var token: io.github.kotlinmania.syn.token.Mut,
    ) : FieldMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

/** Strongly-typed parser for visibility. */
public object VisibilityParse {
    fun parse(input: ParseStream): SynResult<Visibility> {
        var emptyGroup = input.fork()
        var group = parseGroup(emptyGroup)
        if (group.isSuccess && group.getOrThrow().content.isEmpty()) {
            input.advanceTo(emptyGroup)
            return SynResult.success(Visibility.Inherited)
        }

        if (!input.peek(PubPeek)) {
            return SynResult.success(Visibility.Inherited)
        }

        var pubToken = PubParse.parse(input).getOrThrow()

        if (input.peek(ParenPeek)) {
            val ahead = input.fork()
            val parens =
                parseParens(ahead).getOrElse {
                    return SynResult.success(Visibility.Public(pubToken))
                }
            val content = parens.content
            if (content.peek(CratePeek) || content.peek(SelfValuePeek) || content.peek(SuperPeek)) {
                val ident =
                    when {
                        content.peek(CratePeek) -> identFromCrate(CrateParse.parse(content).getOrThrow())
                        content.peek(SelfValuePeek) -> identFromSelfValue(SelfValueParse.parse(content).getOrThrow())
                        else -> identFromSuper(SuperParse.parse(content).getOrThrow())
                    }
                if (content.isEmpty()) {
                    input.advanceTo(ahead)
                    return SynResult.success(Visibility.Restricted(pubToken, parens.token, null, Path.from(ident)))
                }
            } else if (content.peek(InPeek)) {
                val inToken = InParse.parse(content).getOrElse { return SynResult.failure(it) }
                val path = parseModStylePath(content).getOrElse { return SynResult.failure(it) }
                if (!content.isEmpty()) {
                    return SynResult.failure(content.error("unexpected token"))
                }
                input.advanceTo(ahead)
                return SynResult.success(Visibility.Restricted(pubToken, parens.token, inToken, path))
            }
        }

        return SynResult.success(Visibility.Public(pubToken))
    }
}

public object PubPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        var (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "pub"
    }

    override fun display(): String = "`pub`"
}

public object PubParse {
    fun parse(input: ParseStream): SynResult<Pub> =
        input.step { cursor ->
            val (ident, rest) = cursor.ident() ?: return@step SynResult.failure(cursor.error("expected `pub`"))
            if (ident.toString() != "pub") return@step SynResult.failure(cursor.error("expected `pub`"))
            SynResult.success(Pub.from(ident.span()) to rest)
        }
}

internal fun parsePub(input: ParseStream): SynResult<Pub> = PubParse.parse(input)

public object InPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        var (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "in"
    }

    override fun display(): String = "`in`"
}

public object InParse {
    fun parse(input: ParseStream): SynResult<In> =
        input.step { cursor ->
            val (ident, rest) = cursor.ident() ?: return@step SynResult.failure(cursor.error("expected `in`"))
            if (ident.toString() != "in") return@step SynResult.failure(cursor.error("expected `in`"))
            SynResult.success(In.from(ident.span()) to rest)
        }
}
