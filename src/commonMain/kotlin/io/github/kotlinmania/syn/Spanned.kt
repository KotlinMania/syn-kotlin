// port-lint: source spanned.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.__span

/** A syntax node that can report the span covering its contents. */
public interface Spanned {
    public fun span(): Span
}

/** Returns the span covering the complete contents of this token stream. */
public fun ToTokens.span(): Span =
    __span()

/** Returns this span itself (identity conversion). */
public fun Span.span(): Span =
    this
