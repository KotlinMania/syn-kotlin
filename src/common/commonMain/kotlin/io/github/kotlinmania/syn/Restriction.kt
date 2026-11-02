// port-lint: source restriction.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.syn.token.In
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Pub

/**
 * The visibility level of an item: inherited, `pub`, or `pub(restricted)`.
 */
public sealed class Visibility : ToTokens {
    /**
     * A public visibility level: `pub`.
     */
    public data class Public(
        var pubToken: Pub,
    ) : Visibility()

    /**
     * A visibility level restricted to a module path, such as `pub(self)`,
     * `pub(super)`, `pub(crate)`, or `pub(in module)`.
     */
    public data class Restricted(
        var pubToken: Pub,
        var parenToken: Paren,
        var inToken: In?,
        var path: Path,
    ) : Visibility()

    /**
     * An inherited visibility, which usually means private.
     */
    public data object Inherited : Visibility()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Public -> pubToken.toTokens(tokens)
            is Restricted -> {
                pubToken.toTokens(tokens)
                parenToken.surround(tokens) { inner ->
                    inToken?.toTokens(inner)
                    printPath(inner, path, PathStyle.Mod)
                }
            }
            Inherited -> Unit
        }
    }

    /** Returns true if this is not inherited (i.e., has an explicit visibility keyword). */
    public fun isSome(): Boolean = this !is Inherited
}

/** Field mutability marker. Reserved for RFC 3323. */
public sealed class FieldMutability : ToTokens {
    public data object None : FieldMutability()

    public data class Mut(
        var token: io.github.kotlinmania.syn.token.Mut,
    ) : FieldMutability()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            None -> Unit
            is Mut -> token.toTokens(tokens)
        }
    }
}

/** Strongly-typed parser for visibility. */
public object VisibilityParse {
    fun parse(input: ParseStream): SynResult<Visibility> {
        val emptyGroup = input.fork()
        val group = parseGroup(emptyGroup)
        if (group.isSuccess && group.getOrThrow().content.isEmpty()) {
            input.advanceTo(emptyGroup)
            return SynResult.success(Visibility.Inherited)
        }

        return if (input.peek(PubPeek)) {
            parseVisibilityPub(input)
        } else {
            SynResult.success(Visibility.Inherited)
        }
    }
}

private fun parseVisibilityPub(input: ParseStream): SynResult<Visibility> {
    val pubToken = PubParse.parse(input).getOrElse { return SynResult.failure(it) }

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
