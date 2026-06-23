package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.Group as ProcMacroGroup
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.syn.token.Group as TokenGroup
import io.github.kotlinmania.syn.token.group as tokenGroup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GroupTest {
    @Test
    fun parseInvisibleGroupExposesTokenAndContent() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(
                        ProcMacroGroup(
                            Delimiter.None,
                            TokenStream.fromString("inner").getOrThrow(),
                        ),
                    ),
                ),
            )

        val parsed =
            parserFromFunction { input ->
                val group = parseGroup(input).getOrElse { return@parserFromFunction SynResult.failure(it) }
                assertFalse(group.content.isEmpty())
                val content = group.content.parse(TokenStreamParse).getOrElse { return@parserFromFunction SynResult.failure(it) }
                assertEquals("inner", content.toString())
                assertTrue(group.content.isEmpty())
                SynResult.success(group)
            }
                .parse2(tokens)
                .getOrThrow()

        assertEquals("Group", parsed.token.toString())
    }

    @Test
    fun parseInvisibleGroupRejectsVisibleGroup() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(
                        ProcMacroGroup(
                            Delimiter.Parenthesis,
                            TokenStream.new(),
                        ),
                    ),
                ),
            )

        val result = parserFromFunction(::parseGroup).parse2(tokens)

        assertTrue(result.isFailure)
        assertEquals("expected invisible group", result.exceptionOrNull()?.toString())
    }

    @Test
    fun tokenGroupCloneCopiesGroupToken() {
        val group = TokenGroup.from(Span.callSite())
        val clone = group.clone()

        assertEquals(group, clone)
        assertEquals("Group", clone.toString())
    }

    @Test
    fun tokenGroupFactoryMatchesCompanionConstructor() {
        val span = Span.callSite()

        assertEquals(TokenGroup.from(span), tokenGroup(span))
    }
}
