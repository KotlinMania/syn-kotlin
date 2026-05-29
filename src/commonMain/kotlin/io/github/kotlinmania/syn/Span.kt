// port-lint: source span.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.native.HiddenFromObjC

/**
 * Converts a single span or a list of spans into the shape required by
 * multi-span token constructors.
 *
 * In the upstream Rust crate, `IntoSpans` is a trait with implementations for
 * `Span`, `[Span; 1]`, `[Span; 2]`, `[Span; 3]`, and `DelimSpan`. In Kotlin
 * this becomes a set of top-level extension functions because Kotlin has no
 * type-level integer generics for array sizes.
 *
 * The `S` type parameter is phantom — it carries no runtime data, but
 * constrains which conversion is available at the call site.
 */
@HiddenFromObjC
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
