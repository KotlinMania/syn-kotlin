package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.syn.gen.clone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ArraySourceSpanTest {
    @Test
    fun arrayKeepsOriginalBracketAndSemicolonTokensAcrossCopies() {
        val source = "type A = [u8; 4];"
        val file = parseStr(FileParse::parse, source).getOrThrow()
        val array = assertIs<SynType.Array>(assertIs<Item.ItemType>(file.items.single()).ty)
        for (value in listOf(array, array.deepCopy(), array.clone(), array.copy())) {
            assertEquals(9, value.span().start().column)
            assertEquals(16, value.span().end().column)
            assertEquals(12, value.semiToken.span.start().column)
            val tokens = TokenStream.new()
            value.toTokens(tokens)
            val group = assertIs<TokenTree.Group>(tokens.toList().single()).value
            assertEquals(9, group.spanOpen().start().column)
            assertEquals(15, group.spanClose().start().column)
            val semicolon = assertIs<TokenTree.Punct>(group.stream().toList()[1]).value
            assertEquals(';', semicolon.asChar())
            assertEquals(12, semicolon.span().start().column)
        }
    }

    @Test
    fun nestedMultilineArraysRetainDistinctTypeSpans() {
        val source = "// lead\ntype A = [\n    [u16; 2];\n    4\n];"
        val outer = assertIs<SynType.Array>(assertIs<Item.ItemType>(parseStr(FileParse::parse, source).getOrThrow().items.single()).ty)
        val inner = assertIs<SynType.Array>(outer.elem)
        assertEquals(2, outer.span().start().line)
        assertEquals(9, outer.span().start().column)
        assertEquals(5, outer.span().end().line)
        assertEquals(1, outer.span().end().column)
        assertEquals(3, inner.span().start().line)
        assertEquals(4, inner.span().start().column)
        assertEquals(12, inner.span().end().column)
        assertEquals(8, inner.semiToken.span.start().column)
    }
}
