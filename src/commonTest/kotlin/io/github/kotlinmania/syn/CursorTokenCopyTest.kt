package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class CursorTokenCopyTest {
    private fun stream(source: String): TokenStream = TokenStream.fromString(source).getOrThrow()

    private fun location(span: Span): String =
        "${span.start()}|${span.end()}|${span.sourceText()}"

    private fun treeData(tree: TokenTree): String =
        when (tree) {
            is TokenTree.Group -> {
                val group = tree.value
                "group:${group.delimiter()}:${location(group.span())}:${location(group.spanOpen())}:${location(group.spanClose())}" +
                    group.stream().joinToString(prefix = "[", postfix = "]") { treeData(it) }
            }
            is TokenTree.Ident -> "ident:$tree:${location(tree.span())}"
            is TokenTree.Punct -> "punct:$tree:${tree.value.spacing()}:${location(tree.span())}"
            is TokenTree.Literal -> "literal:$tree:${location(tree.span())}"
        }

    @Test
    fun eachReturnedTokenIsIndependentOfItsBuffer() {
        val buffer = TokenBuffer.new2(stream("r#type + 0xffu8 (nested)"))
        var cursor = buffer.begin()
        var visited = 0
        while (true) {
            val (tree, next) = cursor.tokenTree() ?: break
            val before = treeData(tree)
            tree.setSpan(Span.callSite())
            assertEquals(before, treeData(assertNotNull(cursor.tokenTree()).first))
            cursor = next
            visited++
        }
        assertEquals(4, visited)
    }

    @Test
    fun specializedAccessorsAndWholeStreamAlsoCopy() {
        val cursor = TokenBuffer.new2(stream("r#type += 12u16")).begin()
        val ident = assertNotNull(cursor.ident())
        val originalIdent = location(ident.first.span())
        ident.first.setSpan(Span.callSite())
        assertEquals(originalIdent, location(assertNotNull(cursor.ident()).first.span()))

        val punct = assertNotNull(ident.second.punct())
        val originalPunct = location(punct.first.span())
        punct.first.setSpan(Span.callSite())
        assertEquals(originalPunct, location(assertNotNull(ident.second.punct()).first.span()))

        val literalCursor = assertNotNull(punct.second.punct()).second
        val literal = assertNotNull(literalCursor.literal())
        val originalLiteral = location(literal.first.span())
        literal.first.setSpan(Span.callSite())
        assertEquals(originalLiteral, location(assertNotNull(literalCursor.literal()).first.span()))

        val before = cursor.tokenStream().map(::treeData)
        cursor.tokenStream().forEach { it.setSpan(Span.callSite()) }
        assertEquals(before, cursor.tokenStream().map(::treeData))
    }

    @Test
    fun nestedAndTransparentGroupsRetainIndependentDataAndDelimiterSpans() {
        val nested = assertIs<TokenTree.Group>(stream("(r#type [\"é\"]) ").single()).value
        val transparent = Group(Delimiter.None, TokenStream.fromTokenTree(TokenTree.Group(nested)))
        transparent.setSpan(nested.span())
        val cursor = TokenBuffer.new2(TokenStream.fromTokenTree(TokenTree.Group(transparent))).begin()
        val before = treeData(assertNotNull(cursor.tokenTree()).first)
        val returned = assertIs<TokenTree.Group>(assertNotNull(cursor.tokenTree()).first).value
        val returnedNested = assertIs<TokenTree.Group>(returned.stream().single()).value
        returnedNested.setSpan(Span.callSite())
        returnedNested.stream().forEach { it.setSpan(Span.callSite()) }
        returned.setSpan(Span.callSite())
        assertEquals(before, treeData(assertNotNull(cursor.tokenTree()).first))
        assertEquals(Delimiter.None, assertIs<TokenTree.Group>(assertNotNull(cursor.tokenTree()).first).value.delimiter())
    }
}
