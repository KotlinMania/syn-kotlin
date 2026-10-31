// port-lint: source ext.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

/**
 * Additional methods for [Ident] not provided by `proc-macro2`.
 *
 * This interface is sealed and cannot be implemented for types outside of Syn.
 */
public sealed interface IdentExt {
    /** Strips the raw marker prefix, if any, from the beginning of an identifier. */
    public fun unraw(): Ident
}

internal class IdentExtImpl(
    private val ident: Ident,
) : IdentExt {
    override fun unraw(): Ident = ident.unraw()
}

/**
 * Parses any identifier including keywords.
 *
 * This is useful when parsing macro input which allows keywords as identifiers.
 */
public fun Ident.Companion.parseAny(input: ParseStream): SynResult<Ident> =
    identParseAny(input)

/**
 * Peeks any identifier including keywords: `input.peek(Ident.peekAny)`.
 *
 * This differs from [IdentPeek], which only returns `true` for identifiers that
 * are not keywords.
 */
public val Ident.Companion.peekAny: Peek
    get() = PeekFn

/** Parses any identifier including keywords from [input]. */
public fun identParseAny(input: ParseStream): SynResult<Ident> =
    input.step { cursor ->
        val pair = cursor.ident()
        if (pair != null) {
            SynResult.success(pair)
        } else {
            SynResult.failure(cursor.error("expected ident"))
        }
    }

/** Lookahead peek target matching any identifier including keywords. */
public val IdentPeekAny: Peek
    get() = PeekFn

/**
 * Strips the raw marker prefix, if any, from the beginning of this [Ident] and
 * returns an independent token preserving the span.
 */
public fun Ident.unraw(): Ident {
    val string = this.toString()
    return if (string.startsWith("r#")) {
        Ident.new(string.substring(2), this.span().clone())
    } else {
        this.cloneToken()
    }
}

internal object PeekFn : Peek, LookaheadSealed {
    override fun peek(cursor: Cursor): Boolean = IdentAny.peek(cursor)

    override fun display(): String = IdentAny.display()

    public fun clone(): PeekFn = this
}

private object IdentAny {
    fun peek(cursor: Cursor): Boolean = cursor.ident() != null

    fun display(): String = "identifier"
}

internal interface TokenStreamExt {
    fun append(token: TokenTree)
}

internal fun TokenStream.appendTokenTree(token: TokenTree) {
    extendTokenTrees(listOf(token))
}

internal interface PunctExt {
    fun newSpanned(
        ch: Char,
        spacing: Spacing,
        span: Span,
    ): Punct
}

internal fun punctNewSpanned(
    ch: Char,
    spacing: Spacing,
    span: Span,
): Punct {
    val punct = Punct(ch, spacing)
    punct.setSpan(span.clone())
    return punct
}
