// port-lint: source span.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.native.HiddenFromObjC

@HiddenFromObjC
interface IntoSpans<S> {
    fun intoSpans(): S
}

fun Span.intoSpan(): Span =
    this

fun Span.intoOneSpan(): List<Span> =
    listOf(this)

fun Span.intoTwoSpans(): List<Span> =
    listOf(this, this)

fun Span.intoThreeSpans(): List<Span> =
    listOf(this, this, this)

fun List<Span>.intoOneSpan(): List<Span> {
    require(size == 1) { "expected one span" }
    return this
}

fun List<Span>.intoTwoSpans(): List<Span> {
    require(size == 2) { "expected two spans" }
    return this
}

fun List<Span>.intoThreeSpans(): List<Span> {
    require(size == 3) { "expected three spans" }
    return this
}

fun Span.intoDelimSpan(): DelimSpan {
    val group = Group(Delimiter.None, TokenStream.new())
    group.setSpan(this)
    return group.delimSpan()
}

fun DelimSpan.intoDelimSpan(): DelimSpan =
    this
