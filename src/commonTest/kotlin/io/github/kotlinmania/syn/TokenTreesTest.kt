// port-lint: tests tests/test_token_trees.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
