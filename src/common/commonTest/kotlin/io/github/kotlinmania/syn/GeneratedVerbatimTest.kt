package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GeneratedVerbatimTest {
    private fun tokens(text: String) = TokenStream.fromString(text).getOrThrow()

    private fun none(stream: TokenStream) =
        TokenStream.fromTokenTree(TokenTree.Group(Group(Delimiter.None, stream)))

    private fun move(input: ParseStream, path: String) {
        input
            .step { step ->
                var cursor = step.raw
                for (operation in path) {
                    cursor =
                        when (operation) {
                            'n' -> cursor.tokenTree()!!.second
                            'g' -> cursor.group(Delimiter.None)!!.first
                            'p' -> cursor.group(Delimiter.Parenthesis)!!.first
                            'e' -> {
                                while (true) {
                                    val pair = cursor.tokenTree() ?: break
                                    cursor = pair.second
                                }
                                cursor
                            }
                            else -> error("invalid test cursor path")
                        }
                }
                SynResult.success(Unit to cursor)
            }.getOrThrow()
    }

    private fun <T> withInput(stream: TokenStream, body: (ParseStream) -> T): T =
        parse2(
            { input ->
                val result = body(input)
                TokenStreamParse.parse(input).getOrThrow()
                SynResult.success(result)
            },
            stream,
        ).getOrThrow()

    @Test
    fun copiesSelectedTokensWithoutAdvancingInputs() {
        withInput(tokens("a b c")) { input ->
            val begin = input.fork()
            val end = input.fork()
            move(begin, "n")
            move(end, "e")
            val before = begin.cursor()
            val after = end.cursor()
            assertEquals("b c", between(begin, end).toString())
            assertEquals(before, begin.cursor())
            assertEquals(after, end.cursor())
            assertTrue(between(begin, begin).isEmpty())
        }
        withInput(TokenStream.new()) { assertTrue(between(it, it).isEmpty()) }
    }

    @Test
    fun entersTransparentGroupsOnlyWhenEndpointRequiresIt() {
        withInput(none(none(tokens("a b")))) { input ->
            val end = input.fork()
            move(end, "ggn")
            assertEquals("a", between(input, end).toString())
        }
        withInput(none(tokens("a b"))) { input ->
            val end = input.fork()
            move(end, "e")
            val tree = between(input, end).single() as TokenTree.Group
            assertEquals(Delimiter.None, tree.value.delimiter())
            assertEquals("a b", tree.value.stream().toString())
        }
        withInput(none(TokenStream.new())) { input ->
            val end = input.fork()
            move(end, "e")
            assertEquals(
                Delimiter.None,
                (between(input, end).single() as TokenTree.Group).value.delimiter(),
            )
        }
    }

    @Test
    fun rejectsCrossBufferAndInsideDelimitedEndpointsWithRustMessages() {
        withInput(tokens("a")) { a ->
            withInput(tokens("a")) { b ->
                assertEquals(
                    "assertion failed: crate::buffer::same_buffer(end, cursor)",
                    assertFailsWith<__rtkVerbatimPanic> { between(a, b) }.message,
                )
            }
        }
        withInput(tokens("(a b) c")) { input ->
            val end = input.fork()
            move(end, "pn")
            val original = input.cursor()
            assertEquals(
                "verbatim end must not be inside a delimited group",
                assertFailsWith<__rtkVerbatimPanic> { between(input, end) }.message,
            )
            assertEquals(original, input.cursor())
        }
        withInput(tokens("a b")) { input ->
            val begin = input.fork()
            move(begin, "n")
            assertEquals(
                "verbatim end must not be inside a delimited group",
                assertFailsWith<__rtkVerbatimPanic> { between(begin, input) }.message,
            )
        }
    }

    @Test
    fun reversedRangeStartingAtEndRetainsUnwrapPanic() {
        withInput(tokens("a b")) { input ->
            val begin = input.fork()
            move(begin, "e")
            assertEquals(
                "called `Option::unwrap()` on a `None` value",
                assertFailsWith<__rtkVerbatimPanic> { between(begin, input) }.message,
            )
        }
    }

    @Test
    fun outputRetainsSpansAndOwnsIndependentTokens() {
        withInput(tokens("r#type += 12u16 (a)")) { input ->
            val end = input.fork()
            move(end, "e")
            val original = input.cursor().tokenStream().toList()
            val result = between(input, end).toList()
            assertEquals(original.map { it.toString() }, result.map { it.toString() })
            assertEquals(
                original.map { it.span().start() to it.span().end() },
                result.map { it.span().start() to it.span().end() },
            )
            result.forEach { it.setSpan(Span.callSite()) }
            assertEquals(
                original.map { it.span().start() to it.span().end() },
                input.cursor().tokenStream().map { it.span().start() to it.span().end() },
            )
        }
    }
}
