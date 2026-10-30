package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ExprPrintingRegressionTest {
    @Test
    fun nestedValuelessJumpsUseFollowingOperatorContext() {
        for (jump in listOf("break", "return", "yield")) {
            val expr = assertIs<Expr.Binary>(expression("left < right"))
            expr.left = expression("left + $jump")
            assertEquals("left + ($jump) < right", tokens(expr))
            val reparsed = assertIs<Expr.Binary>(expression(tokens(expr)))
            val addition = assertIs<Expr.Binary>(reparsed.left)
            val grouped = assertIs<Expr.Paren>(addition.right)
            assertEquals(jump, tokens(grouped.expr))
        }
    }

    @Test
    fun typedClosureWrapsNonBlockBody() {
        val expr = assertIs<Expr.Closure>(expression("|| -> i32 { value }"))
        expr.body = expression("left + right")
        assertEquals("| | -> i32 { left + right }", tokens(expr))
        assertIs<Expr.BlockExpr>(assertIs<Expr.Closure>(expression(tokens(expr))).body)
    }

    @Test
    fun elseBranchWrapsNonBlockExpression() {
        val expr = assertIs<Expr.If>(expression("if condition {} else {}"))
        assertNotNull(expr.elseBranch).expr = expression("value")
        assertEquals("if condition { } else { value }", tokens(expr))
        assertIs<Expr.BlockExpr>(assertNotNull(assertIs<Expr.If>(expression(tokens(expr))).elseBranch).expr)
    }

    @Test
    fun matchPrintingRestoresRequiredComma() {
        val expr = assertIs<Expr.Match>(expression("match value { A => first, B => second }"))
        expr.arms[0].comma = null
        assertEquals("match value { A => first , B => second }", tokens(expr))
        assertEquals(2, assertIs<Expr.Match>(expression(tokens(expr))).arms.size)
    }

    @Test
    fun blockAttributesKeepTheirOuterAndInnerLocations() {
        val cases =
            listOf(
                "#[outer] { #![inner] value }" to "# [outer] { # ! [inner] value }",
                "#[outer] loop { #![inner] break }" to "# [outer] loop { # ! [inner] break }",
                "#[outer] unsafe { #![inner] value }" to "# [outer] unsafe { # ! [inner] value }",
            )
        for ((source, expected) in cases) {
            val expr = expression(source)
            assertEquals(expected, tokens(expr))
            assertEquals(expected, tokens(expression(tokens(expr))))
        }
    }

    private fun expression(source: String): Expr = parseStr(ExprParse::parse, source).getOrThrow()

    private fun tokens(value: ToTokens): String {
        val output = TokenStream.new()
        value.toTokens(output)
        return output.toString()
    }
}
