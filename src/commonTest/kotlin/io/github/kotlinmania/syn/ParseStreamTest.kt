// port-lint: tests tests/test_parse_stream.rs
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [ParseStream] operations: `peek`, `peek2`, `peek3`, `parse`,
 * `step`, `cursor`, `fork`, `isEmpty`, `call`, and the discouraged
 * `parseAnyDelimiter` extension.
 *
 * The upstream tests build token streams with quoted input and iterable
 * constructors. Here we build them via [TokenStream.fromString]
 * (which lexes source text the same way quoted input would for these cases)
 * and, where a test depends on a specific joint/alone spacing arrangement
 * not preserved by the lexer, via [TokenStream.fromTokenTrees] with
 * explicit [Punct] spacing.
 */
class ParseStreamTest {
    private fun runParser(
        source: String,
        logic: (ParseStream) -> SynResult<Unit>,
    ) {
        val tokens = TokenStream.fromString(source).getOrThrow()
        val parser = parserFromFunction(logic)
        parser.parse2(tokens).getOrThrow()
    }

    private fun runParserTokens(
        tokens: TokenStream,
        logic: (ParseStream) -> SynResult<Unit>,
    ) {
        val parser = parserFromFunction(logic)
        parser.parse2(tokens).getOrThrow()
    }

    @Test
    fun testPeekPunct() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Punct(Punct('+', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct('=', Spacing.Alone, Span.callSite())),
                    TokenTree.Punct(Punct('+', Spacing.Alone, Span.callSite())),
                    TokenTree.Punct(Punct('=', Spacing.Alone, Span.callSite())),
                ),
            )

        fun assert(input: ParseStream): SynResult<Unit> {
            assertTrue(input.peek(PlusPeek))
            assertTrue(input.peek(PlusEqPeek))

            PlusParse.parse(input).getOrThrow()

            assertTrue(input.peek(EqPeek))
            assertFalse(input.peek(EqEqPeek))
            assertFalse(input.peek(PlusPeek))

            EqParse.parse(input).getOrThrow()

            assertTrue(input.peek(PlusPeek))
            assertFalse(input.peek(PlusEqPeek))

            PlusParse.parse(input).getOrThrow()
            EqParse.parse(input).getOrThrow()
            return SynResult.success(Unit)
        }

        parserFromFunction(::assert).parse2(tokens).getOrThrow()
    }

    @Test
    fun testPeekLifetime() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Punct(Punct('\'', Spacing.Joint, Span.callSite())),
                    TokenTree.Ident(Ident.new("static", Span.callSite())),
                    TokenTree.Punct(Punct(';', Spacing.Alone, Span.callSite())),
                ),
            )
        fun assert(input: ParseStream): SynResult<Unit> {
            assertTrue(input.peek(LifetimePeek))
            assertTrue(input.peek2(SemiPeek))
            assertFalse(input.peek2(StaticPeek))

            LifetimeParse.parse(input).getOrThrow()

            assertTrue(input.peek(SemiPeek))

            SemiParse.parse(input).getOrThrow()
            return SynResult.success(Unit)
        }

        parserFromFunction(::assert).parse2(tokens).getOrThrow()
    }

    @Test
    fun testPeekNotLifetime() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Punct(Punct('\'', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("static", Span.callSite())),
                ),
            )

        fun assert(input: ParseStream): SynResult<Unit> {
            assertFalse(input.peek(LifetimePeek))
            val optionalPunct = (PunctParse::parse).optional(NotPeek).parse(input).getOrThrow()
            assertTrue(optionalPunct == null)

            TokenTreeParse.parse(input).getOrThrow()

            assertTrue(input.peek(StaticPeek))

            StaticParse.parse(input).getOrThrow()
            return SynResult.success(Unit)
        }

        parserFromFunction(::assert).parse2(tokens).getOrThrow()
    }

    @Test
    fun testPeekIdent() {
        val tokens = TokenStream.fromString("static var").getOrThrow()

        fun assert(input: ParseStream): SynResult<Unit> {
            assertFalse(input.peek(IdentPeek))
            assertTrue(input.peek(IdentPeekAny))
            assertTrue(input.peek(Ident.peekAny))
            assertTrue(input.peek(StaticPeek))

            StaticParse.parse(input).getOrThrow()

            assertTrue(input.peek(IdentPeek))
            assertTrue(input.peek(IdentPeekAny))
            assertTrue(input.peek(Ident.peekAny))

            IdentParse.parse(input).getOrThrow()
            return SynResult.success(Unit)
        }

        parserFromFunction(::assert).parse2(tokens).getOrThrow()
    }

    @Test
    fun testParseAnyIdent() {
        runParser("name = impl") { input ->
            val key = IdentParse.parse(input).getOrThrow()
            assertEquals("name", key.toString())
            EqParse.parse(input).getOrThrow()

            assertFalse(input.peek(IdentPeek))
            assertTrue(input.peek(Ident.peekAny))

            val value = Ident.parseAny(input).getOrThrow()
            assertEquals("impl", value.toString())
            SynResult.success(Unit)
        }
    }

    @Test
    fun testPeekGroups() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("pub", Span.callSite())),
                    TokenTree.Group(
                        Group(
                            Delimiter.Parenthesis,
                            TokenStream.fromTokenTrees(
                                listOf(
                                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                                ),
                            ),
                        ),
                    ),
                    TokenTree.Group(
                        Group(
                            Delimiter.None,
                            TokenStream.fromTokenTrees(
                                listOf(
                                    TokenTree.Punct(Punct('!', Spacing.Alone, Span.callSite())),
                                    TokenTree.Punct(Punct('=', Spacing.Alone, Span.callSite())),
                                ),
                            ),
                        ),
                    ),
                    TokenTree.Ident(Ident.new("static", Span.callSite())),
                ),
            )

        fun assert(input: ParseStream): SynResult<Unit> {
            assertTrue(input.peek2(ParenPeek))
            assertTrue(input.peek3(GroupPeek))
            assertTrue(input.peek3(NotPeek))

            PubParse.parse(input).getOrThrow()

            assertTrue(input.peek(ParenPeek))
            assertFalse(input.peek(PathSepPeek))
            assertFalse(input.peek2(PathSepPeek))
            assertTrue(input.peek2(NotPeek))
            assertTrue(input.peek2(GroupPeek))
            assertTrue(input.peek3(EqPeek))
            assertFalse(input.peek3(StaticPeek))

            val content = parenthesized(input).getOrThrow().content

            assertTrue(content.peek(PathSepPeek))
            assertTrue(content.peek2(ColonPeek))
            assertFalse(content.peek3(GroupPeek))
            assertFalse(content.peek3(NotPeek))

            assertTrue(input.peek(GroupPeek))
            assertTrue(input.peek(NotPeek))

            PathSepParse.parse(content).getOrThrow()

            assertTrue(input.peek(GroupPeek))
            assertTrue(input.peek(NotPeek))
            assertTrue(input.peek2(EqPeek))
            assertTrue(input.peek3(StaticPeek))
            assertFalse(input.peek2(StaticPeek))

            val implicit = input.fork()
            val explicit = input.fork()

            NotParse.parse(implicit).getOrThrow()
            assertTrue(implicit.peek(EqPeek))
            assertTrue(implicit.peek2(StaticPeek))
            EqParse.parse(implicit).getOrThrow()
            assertTrue(implicit.peek(StaticPeek))

            val grouped = explicit.parseAnyDelimiter().getOrThrow()
            assertEquals(Delimiter.None, grouped.delimiter)
            assertTrue(grouped.content.peek(NotPeek))
            assertTrue(grouped.content.peek2(EqPeek))
            assertFalse(grouped.content.peek3(StaticPeek))
            NotParse.parse(grouped.content).getOrThrow()
            assertTrue(grouped.content.peek(EqPeek))
            assertFalse(grouped.content.peek2(StaticPeek))
            EqParse.parse(grouped.content).getOrThrow()
            assertFalse(grouped.content.peek(StaticPeek))

            TokenStreamParse.parse(input).getOrThrow()
            return SynResult.success(Unit)
        }

        parserFromFunction(::assert).parse2(tokens).getOrThrow()
    }

    @Test
    fun lookaheadEndUsesScopeCloseDelimiter() {
        val parser =
            parserFromFunction<Unit> { input ->
                val content = parenthesized(input).getOrThrow().content
                val lookahead = content.lookahead1()
                assertFalse(lookahead.peek(End))
                SynResult.failure(lookahead.error())
            }

        val result = parser.parse2(TokenStream.fromString("(name)").getOrThrow())

        assertTrue(result.isFailure)
        assertEquals("expected `)`", result.exceptionOrNull()?.toString())
    }

    @Test
    fun lookaheadFormatsManyExpectedTokens() {
        val parser =
            parserFromFunction<Unit> { input ->
                val lookahead = input.lookahead1()
                assertFalse(lookahead.peek(IdentPeek))
                assertFalse(lookahead.peek(LifetimePeek))
                assertFalse(lookahead.peek(ConstPeek))
                SynResult.failure(lookahead.error())
            }

        val result = parser.parse2(TokenStream.fromString("!").getOrThrow())

        assertTrue(result.isFailure)
        assertEquals("expected one of: identifier, lifetime, `const`", result.exceptionOrNull()?.toString())
    }
}
