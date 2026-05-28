// port-lint: source span.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.native.HiddenFromObjC

/** Converts a single span into itself. */
@HiddenFromObjC
public interface IntoSpans<out S> {
    public fun intoSpans(): S
}

/** Converts a [Span] into a single-element span list. */
public fun Span.intoOneSpan(): List<Span> = listOf(this)

/** Converts a [Span] into a two-element span list (both elements are this span). */
public fun Span.intoTwoSpans(): List<Span> = listOf(this, this)

/** Converts a [Span] into a three-element span list (all elements are this span). */
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

/** Converts a [Span] into a [DelimSpan]. */
public fun Span.intoDelimSpan(): DelimSpan {
    val group = Group(Delimiter.None, TokenStream.new())
    group.setSpan(this)
    return group.delimSpan()
}

/** Converts a [DelimSpan] into itself. */
public fun DelimSpan.intoDelimSpan(): DelimSpan = this

/** Converts a single [Span] into a [Span] (identity). */
public fun Span.intoSpan(): Span = this
