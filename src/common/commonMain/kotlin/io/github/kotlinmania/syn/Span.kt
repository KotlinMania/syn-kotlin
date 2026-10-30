// port-lint: source span.rs
@file:Suppress("ClassNaming")

package io.github.kotlinmania.syn

// Generated from Syn AST; exact original retained in input.rs.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust #[doc(hidden)]: omitted from source API documentation.
interface `IntoSpans`<`S`> {
    fun `into_spans`(): `S`
}

class `__rtkImpl0`(receiver: io.github.kotlinmania.procmacro2.Span) :
    `IntoSpans`<io.github.kotlinmania.procmacro2.Span> {
    private val __rtkReceiver: io.github.kotlinmania.procmacro2.Span = (receiver).`clone`()

    override fun `into_spans`(): io.github.kotlinmania.procmacro2.Span =
        (this.__rtkReceiver).`clone`()
}

class `__rtkImpl1`(receiver: io.github.kotlinmania.procmacro2.Span) : `IntoSpans`<__rtkArray0> {
    private val __rtkReceiver: io.github.kotlinmania.procmacro2.Span = (receiver).`clone`()

    override fun `into_spans`(): __rtkArray0 = __rtkArray0((this.__rtkReceiver).`clone`())
}

class `__rtkImpl2`(receiver: io.github.kotlinmania.procmacro2.Span) : `IntoSpans`<__rtkArray1> {
    private val __rtkReceiver: io.github.kotlinmania.procmacro2.Span = (receiver).`clone`()

    override fun `into_spans`(): __rtkArray1 =
        __rtkArray1((this.__rtkReceiver).`clone`(), (this.__rtkReceiver).`clone`())
}

class `__rtkImpl3`(receiver: io.github.kotlinmania.procmacro2.Span) : `IntoSpans`<__rtkArray2> {
    private val __rtkReceiver: io.github.kotlinmania.procmacro2.Span = (receiver).`clone`()

    override fun `into_spans`(): __rtkArray2 =
        __rtkArray2(
            (this.__rtkReceiver).`clone`(),
            (this.__rtkReceiver).`clone`(),
            (this.__rtkReceiver).`clone`(),
        )
}

class `__rtkImpl4`(receiver: __rtkArray0) : `IntoSpans`<__rtkArray0> {
    private val __rtkReceiver: __rtkArray0 = (receiver).copyValue()

    override fun `into_spans`(): __rtkArray0 = (this.__rtkReceiver).copyValue()
}

class `__rtkImpl5`(receiver: __rtkArray1) : `IntoSpans`<__rtkArray1> {
    private val __rtkReceiver: __rtkArray1 = (receiver).copyValue()

    override fun `into_spans`(): __rtkArray1 = (this.__rtkReceiver).copyValue()
}

class `__rtkImpl6`(receiver: __rtkArray2) : `IntoSpans`<__rtkArray2> {
    private val __rtkReceiver: __rtkArray2 = (receiver).copyValue()

    override fun `into_spans`(): __rtkArray2 = (this.__rtkReceiver).copyValue()
}

class `__rtkImpl7`(receiver: io.github.kotlinmania.procmacro2.Span) :
    `IntoSpans`<io.github.kotlinmania.procmacro2.DelimSpan> {
    private val __rtkReceiver: io.github.kotlinmania.procmacro2.Span = (receiver).`clone`()

    override fun `into_spans`(): io.github.kotlinmania.procmacro2.DelimSpan = kotlin.run {
        var `group`: io.github.kotlinmania.procmacro2.Group =
            io.github.kotlinmania.procmacro2.Group(
                io.github.kotlinmania.procmacro2.Delimiter.None,
                io.github.kotlinmania.procmacro2.TokenStream.new(),
            )
        (`group`).setSpan((this.__rtkReceiver).`clone`())
        (`group`).delimSpan()
    }
}

class `__rtkImpl8`(receiver: io.github.kotlinmania.procmacro2.DelimSpan) :
    `IntoSpans`<io.github.kotlinmania.procmacro2.DelimSpan> {
    private val __rtkReceiver: io.github.kotlinmania.procmacro2.DelimSpan = receiver

    override fun `into_spans`(): io.github.kotlinmania.procmacro2.DelimSpan = this.__rtkReceiver
}

// Rust fixed array with 1 Copy elements; length is part of the type.
class __rtkArray0(element0: io.github.kotlinmania.procmacro2.Span) {
    private val stored0: io.github.kotlinmania.procmacro2.Span = (element0).`clone`()
    val element0: io.github.kotlinmania.procmacro2.Span
        get() = (stored0).`clone`()

    fun copyValue(): __rtkArray0 = __rtkArray0(stored0)
}

// Rust fixed array with 2 Copy elements; length is part of the type.
class __rtkArray1(
    element0: io.github.kotlinmania.procmacro2.Span,
    element1: io.github.kotlinmania.procmacro2.Span,
) {
    private val stored0: io.github.kotlinmania.procmacro2.Span = (element0).`clone`()
    val element0: io.github.kotlinmania.procmacro2.Span
        get() = (stored0).`clone`()

    private val stored1: io.github.kotlinmania.procmacro2.Span = (element1).`clone`()
    val element1: io.github.kotlinmania.procmacro2.Span
        get() = (stored1).`clone`()

    fun copyValue(): __rtkArray1 = __rtkArray1(stored0, stored1)
}

// Rust fixed array with 3 Copy elements; length is part of the type.
class __rtkArray2(
    element0: io.github.kotlinmania.procmacro2.Span,
    element1: io.github.kotlinmania.procmacro2.Span,
    element2: io.github.kotlinmania.procmacro2.Span,
) {
    private val stored0: io.github.kotlinmania.procmacro2.Span = (element0).`clone`()
    val element0: io.github.kotlinmania.procmacro2.Span
        get() = (stored0).`clone`()

    private val stored1: io.github.kotlinmania.procmacro2.Span = (element1).`clone`()
    val element1: io.github.kotlinmania.procmacro2.Span
        get() = (stored1).`clone`()

    private val stored2: io.github.kotlinmania.procmacro2.Span = (element2).`clone`()
    val element2: io.github.kotlinmania.procmacro2.Span
        get() = (stored2).`clone`()

    fun copyValue(): __rtkArray2 = __rtkArray2(stored0, stored1, stored2)
}
