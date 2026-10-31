// port-lint: source span.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream

public interface IntoSpans<S> {
    public fun intoSpans(): S
}

public class SpanArray1(
    element0: Span,
) {
    private val stored0: Span = element0.clone()

    public val element0: Span
        get() = stored0.clone()

    public fun copyValue(): SpanArray1 = SpanArray1(stored0)
}

public class SpanArray2(
    element0: Span,
    element1: Span,
) {
    private val stored0: Span = element0.clone()
    private val stored1: Span = element1.clone()

    public val element0: Span
        get() = stored0.clone()

    public val element1: Span
        get() = stored1.clone()

    public fun copyValue(): SpanArray2 = SpanArray2(stored0, stored1)
}

public class SpanArray3(
    element0: Span,
    element1: Span,
    element2: Span,
) {
    private val stored0: Span = element0.clone()
    private val stored1: Span = element1.clone()
    private val stored2: Span = element2.clone()

    public val element0: Span
        get() = stored0.clone()

    public val element1: Span
        get() = stored1.clone()

    public val element2: Span
        get() = stored2.clone()

    public fun copyValue(): SpanArray3 = SpanArray3(stored0, stored1, stored2)
}

public class SpanIntoSpan(
    span: Span,
) : IntoSpans<Span> {
    private val storedSpan: Span = span.clone()

    override fun intoSpans(): Span = storedSpan.clone()
}

public class SpanIntoArray1(
    span: Span,
) : IntoSpans<SpanArray1> {
    private val storedSpan: Span = span.clone()

    override fun intoSpans(): SpanArray1 = SpanArray1(storedSpan.clone())
}

public class SpanIntoArray2(
    span: Span,
) : IntoSpans<SpanArray2> {
    private val storedSpan: Span = span.clone()

    override fun intoSpans(): SpanArray2 = SpanArray2(storedSpan.clone(), storedSpan.clone())
}

public class SpanIntoArray3(
    span: Span,
) : IntoSpans<SpanArray3> {
    private val storedSpan: Span = span.clone()

    override fun intoSpans(): SpanArray3 =
        SpanArray3(storedSpan.clone(), storedSpan.clone(), storedSpan.clone())
}

public class SpanArray1IntoSpans(
    spans: SpanArray1,
) : IntoSpans<SpanArray1> {
    private val storedSpans: SpanArray1 = spans.copyValue()

    override fun intoSpans(): SpanArray1 = storedSpans.copyValue()
}

public class SpanArray2IntoSpans(
    spans: SpanArray2,
) : IntoSpans<SpanArray2> {
    private val storedSpans: SpanArray2 = spans.copyValue()

    override fun intoSpans(): SpanArray2 = storedSpans.copyValue()
}

public class SpanArray3IntoSpans(
    spans: SpanArray3,
) : IntoSpans<SpanArray3> {
    private val storedSpans: SpanArray3 = spans.copyValue()

    override fun intoSpans(): SpanArray3 = storedSpans.copyValue()
}

public class SpanIntoDelimSpan(
    span: Span,
) : IntoSpans<DelimSpan> {
    private val storedSpan: Span = span.clone()

    override fun intoSpans(): DelimSpan {
        val group = Group(Delimiter.None, TokenStream.new())
        group.setSpan(storedSpan.clone())
        return group.delimSpan()
    }
}

public class DelimSpanIntoSpans(
    private val span: DelimSpan,
) : IntoSpans<DelimSpan> {
    override fun intoSpans(): DelimSpan = span
}
