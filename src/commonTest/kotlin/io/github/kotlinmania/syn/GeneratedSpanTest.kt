package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Bracket
import io.github.kotlinmania.syn.token.Paren
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class GeneratedSpanTest {
    private fun values() =
        TokenStream.fromString("alpha\n  βeta (z)").getOrThrow().map { it.span() }

    private fun pos(s: Span): String =
        "${s.start().line}:${s.start().column}-${s.end().line}:${s.end().column}"

    private fun positions(s: DelimSpan) = listOf(pos(s.join()), pos(s.open()), pos(s.close()))

    @Test
    fun byValueSpanConversionCopiesBeforeAndAfterInvocation() {
        val values = values()
        val original = values[0].clone()
        val expected = pos(original)
        val adapter = __rtkImpl0(original)
        original.replaceFrom(values[1])
        val result = adapter.into_spans()
        assertEquals(expected, pos(result))
        assertNotSame(original, result)
        result.replaceFrom(values[2])
        assertEquals(expected, pos(adapter.into_spans()))
    }

    @Test
    fun repeatedArrayElementsAndArrayCopiesHaveIndependentWrappers() {
        val values = values()
        val source = values[0].clone()
        val expected = pos(source)
        val one = __rtkImpl1(source).into_spans()
        val two = __rtkImpl2(source).into_spans()
        val three = __rtkImpl3(source).into_spans()
        source.replaceFrom(values[1])
        assertEquals(expected, pos(one.element0))
        assertEquals(listOf(expected, expected), listOf(pos(two.element0), pos(two.element1)))
        assertEquals(
            listOf(expected, expected, expected),
            listOf(pos(three.element0), pos(three.element1), pos(three.element2)),
        )
        val a = __rtkImpl4(one).into_spans()
        val b = __rtkImpl5(two).into_spans()
        val c = __rtkImpl6(three).into_spans()
        two.element0.replaceFrom(values[1])
        three.element1.replaceFrom(values[2])
        assertEquals(expected, pos(a.element0))
        assertEquals(expected, pos(b.element0))
        assertEquals(expected, pos(b.element1))
        assertEquals(expected, pos(c.element0))
        assertEquals(expected, pos(c.element1))
        assertEquals(expected, pos(c.element2))
        assertNotSame(two.element0, two.element1)
        val distinct = __rtkImpl6(__rtkArray2(values[0], values[1], values[2])).into_spans()
        assertEquals(
            values.map(::pos),
            listOf(pos(distinct.element0), pos(distinct.element1), pos(distinct.element2)),
        )
    }

    @Test
    fun delimiterConversionsAndMigratedConstructorsPreserveSourcePositions() {
        val span = values()[2]
        val expected = listOf("2:7-2:10", "2:7-2:8", "2:9-2:10")
        assertEquals(expected, positions(__rtkImpl7(span).into_spans()))
        val group =
            (TokenStream.fromString("alpha\n  βeta (z)").getOrThrow().last() as TokenTree.Group)
                .value
        assertEquals(expected, positions(__rtkImpl8(group.delimSpan()).into_spans()))
        for (token in listOf(Brace(span), Bracket(span), Paren(span))) assertEquals(
            expected,
            positions(token.span),
        )
        for (token in listOf(Brace.default(), Bracket.default(), Paren.default())) assertEquals(
            pos(Span.callSite()),
            pos(token.span.join()),
        )
    }
}
