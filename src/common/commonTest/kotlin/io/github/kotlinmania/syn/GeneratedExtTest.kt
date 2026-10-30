package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GeneratedExtTest {
    @Test
    fun unrawKeepsOriginalTokenIndependent() {
        for (source in listOf("word", "impl", "r#type", "é")) {
            val original =
                (TokenStream.fromString(source).getOrThrow().first() as TokenTree.Ident).value
            val before = original.span().end()
            val result = SynExtImpl0(original).unraw()
            assertEquals(source.removePrefix("r#"), result.toString())
            assertEquals(before, result.span().end())
            result.setSpan(Span.callSite())
            assertEquals(before, original.span().end())
        }
    }

    @Test
    fun generatedParseAnyCommitsOnlyOnSuccess() {
        for (source in listOf("impl rest", "r#type rest", "+ rest", "")) {
            parse2(
                { input ->
                    val before = input.cursor()
                    val peek = input.peek(IdentPeekAny)
                    val result = SynExtImpl0.parse_any(input)
                    assertEquals(peek, result.isSuccess)
                    if (result.isSuccess) {
                        assertEquals("rest", input.cursor().tokenStream().toString())
                        result.getOrThrow().setSpan(Span.callSite())
                        assertTrue(
                            before
                                .ident()!!
                                .first
                                .span()
                                .end()
                                .column > 0,
                        )
                    } else {
                        assertEquals(before, input.cursor())
                        assertTrue(
                            result.exceptionOrNull().toString().contains("expected ident"),
                        )
                    }
                    TokenStreamParse.parse(input).getOrThrow()
                    SynResult.success(Unit)
                },
                TokenStream.fromString(source).getOrThrow(),
            ).getOrThrow()
        }
    }

    @Test
    fun stepKeepsOriginalErrorAndCallsCallbackOnce() {
        parse2(
            { input ->
                val before = input.cursor()
                val error = input.error("chosen")
                var calls = 0
                val result =
                    input.step<Unit> { cursor ->
                        calls++
                        assertTrue(cursor.ident() != null)
                        SynResult.failure(error)
                    }
                assertSame(error, result.exceptionOrNull())
                assertEquals(1, calls)
                assertEquals(before, input.cursor())
                TokenStreamParse.parse(input).getOrThrow()
                SynResult.success(Unit)
            },
            TokenStream.fromString("token").getOrThrow(),
        ).getOrThrow()
    }

    @Test
    fun generatedAppendAndPunctuationKeepDataAndSpacing() {
        val stream = TokenStream.fromString("first").getOrThrow()
        for (token in TokenStream.fromString("r#type += 12u8 (x)").getOrThrow()) {
            SynExtImpl4(stream).append(token)
        }
        assertEquals("first r#type += 12u8 (x)", stream.toString())
        val span = stream.first().span()
        for (spacing in listOf(Spacing.Alone, Spacing.Joint)) {
            val punct = SynExtImpl5.new_spanned(SynExtChar('='.code), spacing, span)
            assertEquals('=', punct.asChar())
            assertEquals(spacing, punct.spacing())
            assertEquals(span.end(), punct.span().end())
        }
    }
}
