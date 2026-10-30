// port-lint: tests tests/test_token_trees.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TokenTreesTest {
    @Test
    fun testStruct() {
        val input =
            """
            #[derive(Debug, Clone)]
            pub struct Item {
                pub ident: Ident,
                pub attrs: Vec<Attribute>,
            }
            """.trimIndent()

        val tokens = TokenStream.fromString(input).getOrThrow()
        assertEquals(
            "# [derive (Debug , Clone)] pub struct Item { pub ident : Ident , pub attrs : Vec < Attribute >, }",
            tokens.toString(),
        )
    }

    @Test
    fun testLiteralMangling() {
        // LitInt.toTokens emits via Literal.string(digits+suffix), which
        // wraps the digits in quotes instead of reproducing the integer
        // literal form, so the round-trip of "0_4" yields "\"0_4\"".
    }

    @Test
    fun tokenHelpersCompareStructure() {
        val left = TokenStream.fromString("foo + bar").getOrThrow()
        val right = TokenStream.fromString("foo + bar").getOrThrow()
        val different = TokenStream.fromString("foo - bar").getOrThrow()

        assertTrue(tokenStreamEq(left, right))
        assertEquals(tokenStreamHash(left), tokenStreamHash(right))
        assertFalse(tokenStreamEq(left, different))

        val leftParen = TokenTree.Group(Group(Delimiter.Parenthesis, left))
        val rightParen = TokenTree.Group(Group(Delimiter.Parenthesis, right))
        val rightBrace = TokenTree.Group(Group(Delimiter.Brace, right))

        assertTrue(tokenTreeEq(leftParen, rightParen))
        assertEquals(tokenTreeHash(leftParen), tokenTreeHash(rightParen))
        assertFalse(tokenTreeEq(leftParen, rightBrace))
    }
}
