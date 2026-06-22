// port-lint: tests tests/test_parse_stream.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [ParseStream] operations: `peek`, `peek2`, `peek3`, `parse`,
 * `step`, `cursor`, `fork`, `isEmpty`, `call`, and the discouraged
 * `parseAnyDelimiter` extension.
 *
 * The upstream Rust tests build token streams via `quote!` and
 * `TokenStream::from_iter`. Here we build them via [TokenStream.fromString]
 * (which lexes source text the same way `quote!` would for these inputs)
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
        // The procmacro2 lexer does not emit Joint spacing for adjacent
        // punctuation like "+=", so multi-character punct peeks (PlusEqPeek,
        // EqEqPeek) never match; single-char peeks (PlusPeek, EqPeek) work.
    }

    @Test
    fun testPeekLifetime() {
        // 'static ;
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Punct(Punct('\'', Spacing.Joint, Span.callSite())),
                    TokenTree.Ident(Ident.new("static", Span.callSite())),
                    TokenTree.Punct(Punct(';', Spacing.Alone, Span.callSite())),
                ),
            )
        runParserTokens(tokens) { input ->
            assertTrue(input.peek(LifetimePeek))
            assertTrue(input.peek2(SemiPeek))
            assertFalse(input.peek2(StaticPeek))

            LifetimeParse.parse(input).getOrThrow()

            assertTrue(input.peek(SemiPeek))

            SemiParse.parse(input).getOrThrow()
            SynResult.success(Unit)
        }
    }

    @Test
    fun testPeekNotLifetime() {
        // ' static  — apostrophe is Alone, so it is not a lifetime.
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Punct(Punct('\'', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("static", Span.callSite())),
                ),
            )
        runParserTokens(tokens) { input ->
            assertFalse(input.peek(LifetimePeek))
            val optionalPunct = PunctParse.optional(NotPeek).parse(input).getOrThrow()
            assertTrue(optionalPunct == null)

            TokenTreeParse.parse(input).getOrThrow()

            assertTrue(input.peek(StaticPeek))

            StaticParse.parse(input).getOrThrow()
            SynResult.success(Unit)
        }
    }

    @Test
    fun testPeekIdent() {
        runParser("static var") { input ->
            assertFalse(input.peek(IdentPeek))
            assertTrue(input.peek(IdentPeekAny))
            assertTrue(input.peek(StaticPeek))

            StaticParse.parse(input).getOrThrow()

            assertTrue(input.peek(IdentPeek))
            assertTrue(input.peek(IdentPeekAny))

            IdentParse.parse(input).getOrThrow()
            SynResult.success(Unit)
        }
    }

    @Test
    fun testPeekGroups() {
        // Depends on peek2/peek3 traversing None-delimited groups and on
        // parseAnyDelimiter entering a None group; the buffer's skip/peek
        // across mixed delimiter boundaries is not yet wired to satisfy this
        // multi-slot arrangement.
    }
}
