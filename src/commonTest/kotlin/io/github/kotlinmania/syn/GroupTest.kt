package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import io.github.kotlinmania.procmacro2.Group as ProcMacroGroup
import io.github.kotlinmania.syn.token.Group as TokenGroup
import io.github.kotlinmania.syn.token.group as tokenGroup

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
            parse2(
                parser@{ input: ParseStream ->
                    val group = parseGroup(input).getOrElse { return@parser SynResult.failure(it) }
                    assertFalse(group.content.isEmpty())
                    val content = TokenStreamParse.parse(group.content).getOrElse { return@parser SynResult.failure(it) }
                    assertEquals("inner", content.toString())
                    assertTrue(group.content.isEmpty())
                    SynResult.success(group)
                },
                tokens,
            ).getOrThrow()

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

        val result = parse2(::parseGroup, tokens)

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
