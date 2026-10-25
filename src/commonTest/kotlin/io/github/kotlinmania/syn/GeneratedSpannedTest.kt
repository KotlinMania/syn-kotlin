package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class GeneratedSpannedTest {
  private fun pos(span: Span): String =
      "${span.start().line}:${span.start().column}-${span.end().line}:${span.end().column}"

  private class Counted(val tokens: TokenStream) : ToTokens {
    var calls = 0

    override fun toTokens(tokens: TokenStream) {
      calls++
      tokens.extendTokenTrees(this.tokens.clone().toList())
    }

    override fun toTokenStream(): TokenStream =
        TokenStream.fromString("borrowed_override").getOrThrow()

    override fun intoTokenStream(): TokenStream =
        TokenStream.fromString("owned_override").getOrThrow()
  }

  @Test
  fun sharedBlanketDispatchForwardsToTokensWithoutCallingOwnedOverrides() {
    val value = Counted(TokenStream.fromString("x\n y").getOrThrow())
    val adapter = SynSpannedImpl0(value, SynSpannedBound0.Case2<Counted>())
    assertEquals("1:0-2:2", pos(adapter.span()))
    assertEquals("1:0-2:2", pos(value.span()))
    val erased: ToTokens = value
    assertEquals("1:0-2:2", pos(erased.span()))
    assertEquals(3, value.calls)
    assertEquals(2, value.tokens.clone().count())
  }

  @Test
  fun spanResultIsAnIndependentCopy() {
    val values = TokenStream.fromString("first second").getOrThrow().map { it.span() }
    val result = values[0].span()
    assertNotSame(values[0], result)
    result.replaceFrom(values[1])
    assertEquals("1:0-1:5", pos(values[0]))
    assertEquals("1:6-1:12", pos(result))
  }

  @Test
  fun delimiterAndEmptySpanUseExistingEngineOperations() {
    val group = (TokenStream.fromString(" (body) ").getOrThrow().single() as TokenTree.Group).value
    assertEquals("1:1-1:7", pos(group.delimSpan().span()))
    val empty = Counted(TokenStream.new())
    assertEquals(pos(Span.callSite()), pos(empty.span()))
    assertEquals(1, empty.calls)
  }

  @Test
  fun joiningSeparateSourceFilesFallsBackToFirstSpan() {
    val tokens = TokenStream.fromString("first").getOrThrow()
    tokens.extendTokenTrees(TokenStream.fromString("   last").getOrThrow().toList())
    assertEquals("1:0-1:5", pos(Counted(tokens).span()))
  }
}
