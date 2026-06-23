// port-lint: tests tests/test_parse_buffer.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParseBufferTest {
    @Test
    fun smuggledSpeculativeCursorBetweenSources() {
        val parser =
            parserFromFunction { input1 ->
                val nested =
                    parserFromFunction { input2 ->
                        input1.advanceTo(input2)
                        SynResult.success(Unit)
                    }
                nested.parse2(TokenStream.new())
            }
        assertFailsWith<IllegalArgumentException> {
            parser.parse2(TokenStream.new()).getOrThrow()
        }
    }

    @Test
    fun smuggledSpeculativeCursorBetweenBrackets() {
        val parser =
            parserFromFunction { input ->
                val a = parenthesized(input).getOrThrow()
                val b = parenthesized(input).getOrThrow()
                a.content.advanceTo(b.content)
                SynResult.success(Unit)
            }
        assertFailsWith<IllegalArgumentException> {
            parser.parseStr("()()").getOrThrow()
        }
    }

    @Test
    fun smuggledSpeculativeCursorIntoBrackets() {
        val parser =
            parserFromFunction { input ->
                val a = parenthesized(input).getOrThrow()
                input.advanceTo(a.content)
                SynResult.success(Unit)
            }
        assertFailsWith<IllegalArgumentException> {
            parser.parseStr("()").getOrThrow()
        }
    }

    @Test
    fun trailingEmptyNoneGroup() {
        val inner =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Punct(Punct('+', Spacing.Alone, Span.callSite())),
                    TokenTree.Group(Group(Delimiter.None, TokenStream.new())),
                ),
            )
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Punct(Punct('+', Spacing.Alone, Span.callSite())),
                    TokenTree.Group(Group(Delimiter.Parenthesis, inner)),
                    TokenTree.Group(Group(Delimiter.None, TokenStream.new())),
                    TokenTree.Group(
                        Group(
                            Delimiter.None,
                            TokenStream.fromTokenTree(
                                TokenTree.Group(Group(Delimiter.None, TokenStream.new())),
                            ),
                        ),
                    ),
                ),
            )

        val parser =
            parserFromFunction { input ->
                PlusParse.parse(input).getOrThrow()
                val parens = parenthesized(input).getOrThrow()
                PlusParse.parse(parens.content).getOrThrow()
                parens.content.finishChildBuffer()
                SynResult.success(Unit)
            }
        parser.parse2(tokens).getOrThrow()
    }

    @Test
    fun parseBufferDropPropagatesUnexpectedChildTokens() {
        val parser =
            parserFromFunction { input ->
                val parens = parenthesized(input).getOrThrow()
                parens.content.drop()
                SynResult.success(Unit)
            }

        val result = parser.parseStr("(+)")

        assertTrue(result.isFailure)
        assertEquals("unexpected token, expected `)`", result.exceptionOrNull()?.toString())
    }

    // Upstream test_unwind_safe depends on panic catching and unwind-safety marker traits, which Kotlin does not expose.

    @Test
    fun parseStreamIsEmptyAtEof() {
        val parser =
            parserFromFunction { input ->
                assertTrue(input.isEmpty())
                SynResult.success(Unit)
            }
        parser.parseStr("").getOrThrow()
    }

    @Test
    fun parseStreamIsNotEmptyForContent() {
        val parser =
            parserFromFunction { input ->
                assertFalse(input.isEmpty())
                IdentParse.parse(input).getOrThrow()
                SynResult.success(Unit)
            }
        parser.parseStr("foo").getOrThrow()
    }

    @Test
    fun parseStreamCursorReturnsIdent() {
        val parser =
            parserFromFunction { input ->
                val cursor = input.cursor()
                val pair = cursor.ident()
                assertNotNull(pair)
                assertEquals("foo", pair.first.toString())
                assertFalse(input.isEmpty())

                val ident = IdentParse.parse(input).getOrThrow()
                assertEquals("foo", ident.toString())
                assertTrue(input.isEmpty())
                SynResult.success(Unit)
            }
        parser.parseStr("foo").getOrThrow()
    }

    @Test
    fun parseStreamCursorReturnsNullAtEof() {
        val parser =
            parserFromFunction { input ->
                val cursor = input.cursor()
                assertNull(cursor.ident())
                SynResult.success(Unit)
            }
        parser.parseStr("").getOrThrow()
    }

    @Test
    fun cursorCloneAndPartialCmpFollowBufferPosition() {
        val buffer =
            TokenBuffer.new2(
                TokenStream.fromTokenTrees(
                    listOf(
                        TokenTree.Ident(Ident.new("a", Span.callSite())),
                        TokenTree.Ident(Ident.new("b", Span.callSite())),
                    ),
                ),
            )
        val first = buffer.begin()
        val second = first.ident()!!.second
        val other =
            TokenBuffer.new2(
                TokenStream.fromTokenTree(TokenTree.Ident(Ident.new("c", Span.callSite()))),
            ).begin()

        assertEquals(first, first.clone())
        assertEquals(0, first.partialCmp(first.clone()))
        assertTrue(first.partialCmp(second)!! < 0)
        assertTrue(second.partialCmp(first)!! > 0)
        assertNull(first.partialCmp(other))
    }

    @Test
    fun parseStreamForkDoesNotAdvanceParent() {
        val parser =
            parserFromFunction { input ->
                val fork = input.fork()
                IdentParse.parse(fork).getOrThrow()
                assertTrue(!input.isEmpty(), "parsing from fork must not advance the parent")
                IdentParse.parse(input).getOrThrow()
                SynResult.success(Unit)
            }
        parser.parseStr("foo").getOrThrow()
    }

    @Test
    fun parseStreamPeekDoesNotAdvance() {
        val parser =
            parserFromFunction { input ->
                assertTrue(input.peek(IdentPeek))
                assertTrue(!input.isEmpty())
                IdentParse.parse(input).getOrThrow()
                assertTrue(input.isEmpty())
                SynResult.success(Unit)
            }
        parser.parseStr("foo").getOrThrow()
    }

    @Test
    fun parseStreamStepAdvancesOnSuccess() {
        val parser =
            parserFromFunction { input ->
                val parsed: SynResult<Ident> =
                    input.step { cursor ->
                        val (ident, rest) =
                            cursor.ident()
                                ?: return@step SynResult.failure<Pair<Ident, Cursor>>(cursor.error("expected identifier"))
                        SynResult.success(ident to rest)
                    }
                parsed.getOrThrow()
                assertTrue(input.isEmpty())
                SynResult.success(Unit)
            }
        parser.parseStr("foo").getOrThrow()
    }

    @Test
    fun stepCursorDerefExposesCursor() {
        val parser =
            parserFromFunction { input ->
                input.step { cursor ->
                    assertEquals(cursor.raw, cursor.deref())
                    val (ident, rest) =
                        cursor.deref().ident()
                            ?: return@step SynResult.failure<Pair<Unit, Cursor>>(cursor.error("expected identifier"))
                    assertEquals("foo", ident.toString())
                    SynResult.success(Unit to rest)
                }
            }

        parser.parseStr("foo").getOrThrow()
    }

    @Test
    fun parseStreamStepDoesNotAdvanceOnFailure() {
        val parser =
            parserFromFunction { input ->
                val parsed: SynResult<Unit> =
                    input.step { cursor ->
                        SynResult.failure<Pair<Unit, Cursor>>(cursor.error("expected failure"))
                    }
                assertTrue(parsed.isFailure)
                assertTrue(input.peek(IdentPeek))

                val ident = IdentParse.parse(input).getOrThrow()
                assertEquals("foo", ident.toString())
                assertTrue(input.isEmpty())
                SynResult.success(Unit)
            }
        parser.parseStr("foo").getOrThrow()
    }
}

private fun assertNotNull(value: Any?) {
    kotlin.test.assertNotNull(value)
}

private fun assertNull(value: Any?) {
    kotlin.test.assertNull(value)
}
