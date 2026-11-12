package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.syn.token.Fn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KotlinSourceTokensTest {
    @Test
    fun kotlinFunctionUsesTheExistingSharedTokenTree() {
        val source = "fun answer(value: Int): Int { val result = value + 1; return result }"
        val tokens = parseKotlinTokenStream(source).getOrThrow().toList()
        val keyword = assertIs<TokenTree.Ident>(tokens[0])
        assertEquals("fun", keyword.value.toString())
        assertEquals("Token![fn]", Fn(keyword.span()).toString())
        assertEquals("answer", assertIs<TokenTree.Ident>(tokens[1]).value.toString())
        val parameters = assertIs<TokenTree.Group>(tokens[2]).value
        assertEquals(Delimiter.Parenthesis, parameters.delimiter())
        assertEquals(listOf("value", ":", "Int"), parameters.stream().map { it.toString() })
        val body = assertIs<TokenTree.Group>(tokens.last()).value
        assertEquals(Delimiter.Brace, body.delimiter())
        assertTrue(body.stream().any { it is TokenTree.Ident && it.value.toString() == "return" })
    }

    @Test
    fun kotlinKeywordsAssociateWithSynWithoutRewritingTheirSpelling() {
        val tokens = parseKotlinTokenStream("fun if else return while for break continue when ordinary").getOrThrow().toList()
        val associated = tokens.map { kotlinKeywordRustToken(it)?.toString() }
        assertEquals(
            listOf(
                "Token![fn]",
                "Token![if]",
                "Token![else]",
                "Token![return]",
                "Token![while]",
                "Token![for]",
                "Token![break]",
                "Token![continue]",
                "Token![match]",
                null,
            ),
            associated,
        )
        assertEquals("fun", tokens.first().toString())
        assertNull(kotlinKeywordRustToken(parseKotlinTokenStream("\"fun when\"").getOrThrow().single()))
    }
}
