// port-lint: tests tests/test_grouping.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GroupingTest {
    @Test
    fun testGrouping() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Literal(Literal.i32Suffixed(1)),
                    TokenTree.Punct(Punct('+', Spacing.Alone, Span.callSite())),
                    TokenTree.Group(
                        Group(
                            Delimiter.None,
                            TokenStream.fromTokenTrees(
                                listOf(
                                    TokenTree.Literal(Literal.i32Suffixed(2)),
                                    TokenTree.Punct(Punct('+', Spacing.Alone, Span.callSite())),
                                    TokenTree.Literal(Literal.i32Suffixed(3)),
                                ),
                            ),
                        ),
                    ),
                    TokenTree.Punct(Punct('*', Spacing.Alone, Span.callSite())),
                    TokenTree.Literal(Literal.i32Suffixed(4)),
                ),
            )

        assertEquals("1i32 + 2i32 + 3i32 * 4i32", tokens.toString())

        val expr = assertIs<Expr.Binary>(parse2(ExprParse::parse, tokens).getOrThrow())
        assertIntLiteral("1i32", expr.left)
        assertIs<BinOp.Add>(expr.op)

        val right = assertIs<Expr.Binary>(expr.right)
        val group = assertIs<Expr.Group>(right.left)
        val grouped = assertIs<Expr.Binary>(group.expr)
        assertIntLiteral("2i32", grouped.left)
        assertIs<BinOp.Add>(grouped.op)
        assertIntLiteral("3i32", grouped.right)

        assertIs<BinOp.Mul>(right.op)
        assertIntLiteral("4i32", right.right)
    }

    private fun assertIntLiteral(
        expected: String,
        expr: Expr,
    ) {
        val lit = assertIs<Lit.Int>(assertIs<Expr.Lit>(expr).lit)
        assertEquals(expected, lit.value.toString())
    }
}
