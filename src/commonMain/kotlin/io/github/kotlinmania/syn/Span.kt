// port-lint: source span.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream

/**
 * Converts a single span or a list of spans into the shape required by
 * multi-span token constructors.
 *
 * The `IntoSpans` interface provides type-safe conversion from spans and span
 * lists into the shapes required by multi-span token constructors. Extension
 * functions on [Span], [List<Span>], and [DelimSpan] satisfy each supported
 * arity at the call site.
 */
public interface IntoSpans<out S> {
    /** Converts this value into the target span shape. */
    public fun intoSpans(): S
}

/** Converts a single [Span] into itself (identity). */
public fun Span.intoOneSpan(): List<Span> = listOf(this)

/** Converts a single [Span] into a two-element list where both spans are this one. */
public fun Span.intoTwoSpans(): List<Span> = listOf(this, this)

/** Converts a single [Span] into a three-element list where all three spans are this one. */
public fun Span.intoThreeSpans(): List<Span> = listOf(this, this, this)

/** Asserts that the list has exactly one span and returns it. */
public fun List<Span>.intoOneSpan(): List<Span> {
    require(size == 1) { "expected one span" }
    return this
}

/** Asserts that the list has exactly two spans and returns it. */
public fun List<Span>.intoTwoSpans(): List<Span> {
    require(size == 2) { "expected two spans" }
    return this
}

/** Asserts that the list has exactly three spans and returns it. */
public fun List<Span>.intoThreeSpans(): List<Span> {
    require(size == 3) { "expected three spans" }
    return this
}

/**
 * Converts a [Span] into a [DelimSpan].
 *
 * Creates an invisible group with [Delimiter.None] to obtain the delimiter span,
 * which carries open and close span information.
 */
public fun Span.intoDelimSpan(): DelimSpan {
    val group = Group(Delimiter.None, TokenStream.new())
    group.setSpan(this)
    return group.delimSpan()
}

/** Converts a [DelimSpan] into itself (identity). */
public fun DelimSpan.intoDelimSpan(): DelimSpan = this

/** Converts a single [Span] into a [Span] (identity). */
public fun Span.intoSpan(): Span = this

public fun Span.intoSpans(): Span = this

public fun List<Span>.intoSpans(): List<Span> = this

public fun DelimSpan.intoSpans(): DelimSpan = this
