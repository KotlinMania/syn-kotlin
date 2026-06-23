// port-lint: source ext.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

/** Extension helpers for parser behavior on procmacro2 token types. */
public object IdentExt {
    /** Parses any identifier, including language keywords. */
    public fun parseAny(input: ParseStream): SynResult<Ident> =
        identParseAny(input)

    /** Peek target for any identifier, including language keywords. */
    public val peekAny: Peek
        get() = IdentPeekAny

    /** Removes a raw-identifier marker from [ident], if present. */
    public fun unraw(ident: Ident): Ident =
        ident.unraw()
}

/** Parses any identifier, including language keywords. */
public fun Ident.Companion.parseAny(input: ParseStream): SynResult<Ident> =
    identParseAny(input)

/** Peek target for any identifier, including language keywords. */
public val Ident.Companion.peekAny: Peek
    get() = IdentPeekAny

/** Parses any identifier, including language keywords. */
public fun identParseAny(input: ParseStream): SynResult<Ident> =
    input.step { cursor ->
        val pair =
            cursor.ident()
                ?: return@step SynResult.failure(cursor.error("expected ident"))
        SynResult.success(pair)
    }

/** Peek target for any identifier, including language keywords. */
public val IdentPeekAny: Peek = PeekFn

internal object PeekFn : Peek {
    public typealias Token = IdentAny

    override fun peek(cursor: Cursor): Boolean = cursor.ident() != null

    override fun display(): String = "identifier"

    fun clone(): PeekFn = this
}

internal class IdentAny private constructor()

internal object ExtPrivate {
    interface Sealed
}

/** Removes a raw-identifier marker from this identifier, if present. */
public fun Ident.unraw(): Ident {
    val string = this.toString()
    return if (string.startsWith("r#")) {
        Ident.new(string.removePrefix("r#"), this.span())
    } else {
        this
    }
}

internal object TokenStreamExt {
    fun append(tokens: TokenStream, token: TokenTree) {
        tokens.extendTokenTrees(listOf(token))
    }
}

/** Appends a single [TokenTree] onto a [TokenStream]. */
internal fun TokenStream.appendTokenTree(token: TokenTree) {
    TokenStreamExt.append(this, token)
}

internal object PunctExt {
    fun newSpanned(ch: Char, spacing: Spacing, span: Span): Punct {
        val punct = Punct(ch, spacing)
        punct.setSpan(span)
        return punct
    }
}

/** Constructs a [Punct] with the given character, spacing, and span. */
internal fun punctNewSpanned(ch: Char, spacing: Spacing, span: Span): Punct {
    return PunctExt.newSpanned(ch, spacing, span)
}
