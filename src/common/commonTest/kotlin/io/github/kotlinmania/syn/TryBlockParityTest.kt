package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TryBlockParityTest {
    @Test
    fun tryBlockUsesExistingBlockParserAndRetainsOuterAttributes() {
        val source = "#[outer] try { let value = nested()?; value }"
        val expr = assertIs<Expr.TryBlock>(parseStr(::parseExpr, source).getOrThrow())
        assertEquals(
            "outer",
            expr.attrs
                .single()
                .path()
                .getIdent()
                .toString(),
        )
        assertEquals(2, expr.block.stmts.size)
        assertIs<Expr.Try>(assertIs<Stmt.Local>(expr.block.stmts.first()).init!!.expr)
        assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(expr.toTokenStream()))
        val trailed = assertIs<Expr.Try>(parseStr(::parseExpr, "try {}?").getOrThrow())
        assertIs<Expr.TryBlock>(trailed.expr)
    }

    @Test
    fun tryBlockMatchArmHonorsEarlierBoundary() {
        val source = "match n { 0 => try {} _ => {} }"
        val match = assertIs<Expr.Match>(parseStr(::parseExpr, source).getOrThrow())
        assertEquals(2, match.arms.size)
        assertIs<Expr.TryBlock>(match.arms.first().body)
        assertEquals(null, match.arms.first().comma)
        assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(match.toTokenStream()))
    }

    @Test
    fun tryBlockRejectsInvalidContentsWithoutDroppingTokens() {
        for (source in listOf("try", "try ()", "try { #![inner] }", "try { let x = ; }")) {
            assertTrue(parseStr(::parseExpr, source).isFailure, source)
        }
    }

    // Display spacing between '?' and ';' is not Rust syntax identity. Compare
    // existing token-tree kinds, delimiters and contents as the parity suite does.
    private fun shape(tokens: TokenStream): List<String> =
        buildList {
            for (token in tokens) {
                when (token) {
                    is TokenTree.Group -> {
                        add("open:${token.value.delimiter()}")
                        addAll(shape(token.value.stream()))
                        add("close:${token.value.delimiter()}")
                    }
                    is TokenTree.Ident -> add("ident:${token.value}")
                    is TokenTree.Punct -> add("punct:${token.value.asChar()}")
                    is TokenTree.Literal -> add("literal:${token.value}")
                }
            }
        }
}
