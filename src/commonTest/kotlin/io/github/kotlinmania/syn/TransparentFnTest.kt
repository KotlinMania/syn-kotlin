package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class TransparentFnTest {
    @Test
    fun acceptsLambdasFunctionValuesAndReferences() {
        val tokens = TokenStream.fromString("input").getOrThrow()
        val span = Span.callSite()
        val transform: (TokenStream) -> TokenStream = { it }
        val handlers =
            listOf(
                TransparentFn({ it }, span),
                TransparentFn(transform, span),
                TransparentFn(::identity, span),
            )

        for (handler in handlers) {
            assertSame(tokens, handler.fn(tokens))
            assertEquals(span, handler.span)
        }
    }

    @Test
    fun acceptsAnExplicitTokenStreamTransform() {
        val transform =
            object : TokenStreamTransform {
                override fun invoke(tokens: TokenStream): TokenStream =
                    TokenStream.fromString("$tokens output").getOrThrow()
            }
        val handler = TransparentFn(transform, Span.callSite())
        val input = TokenStream.fromString("input").getOrThrow()

        assertEquals("input output", handler.fn(input).toString())
        assertEquals("input", input.toString())
    }

    @Test
    fun preservesTheOriginalThrownException() {
        val error = IllegalStateException("invalid macro input")
        val handler = TransparentFn({ throw error }, Span.callSite())

        assertSame(error, assertFailsWith<IllegalStateException> { handler.fn(TokenStream.new()) })
    }

    private fun identity(tokens: TokenStream): TokenStream = tokens
}
