// port-lint: source spanned.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.__span

/**
 * A syntax node that can report the span covering its contents.
 *
 * This interface is automatically implemented for all types that implement
 * [ToTokens] from the quote library, as well as for [Span] itself.
 *
 * In the common case of wanting to use the joined span as the span of a
 * [SynError], consider instead using [SynError.newSpanned] which is
 * able to span the error correctly under the complete syntax tree node
 * without needing the unstable span join.
 */
public interface Spanned {
    /** Returns a span covering the complete contents of this syntax tree node. */
    public fun span(): Span
}

/** Returns the span covering the complete contents of this token stream. */
public fun ToTokens.span(): Span =
    __span()

/** Returns this span itself (identity conversion). */
public fun Span.span(): Span =
    this

/** Private seal to prevent external implementation of [Spanned]. */
internal interface SpannedSealed

/** Auto-implement [Spanned] for all [ToTokens] implementations. */
internal fun <T : ToTokens> spanOf(value: T): Span =
    value.__span()

