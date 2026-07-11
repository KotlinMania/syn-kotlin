// port-lint: source span.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream

public interface IntoSpans<out S> {
    public fun intoSpans(): S
}

public fun Span.intoSpans(): Span = this

public fun Span.intoSpansArray1(): List<Span> = listOf(this)

public fun Span.intoSpansArray2(): List<Span> = listOf(this, this)

public fun Span.intoSpansArray3(): List<Span> = listOf(this, this, this)

public fun List<Span>.intoSpansArray1(): List<Span> {
    require(size == 1) { "expected one span" }
    return this
}

public fun List<Span>.intoSpansArray2(): List<Span> {
    require(size == 2) { "expected two spans" }
    return this
}

public fun List<Span>.intoSpansArray3(): List<Span> {
    require(size == 3) { "expected three spans" }
    return this
}

public fun Span.intoDelimSpan(): DelimSpan {
    val group = Group(Delimiter.None, TokenStream.new())
    group.setSpan(this)
    return group.delimSpan()
}

public fun DelimSpan.intoDelimSpan(): DelimSpan = this

public fun DelimSpan.intoSpans(): DelimSpan = this

public fun List<Span>.intoSpans(): List<Span> = this
