// port-lint: tests tests/test_unparenthesize.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.intoTokenStream
import io.github.kotlinmania.syn.gen.VisitMut
import kotlin.test.Test
import kotlin.test.assertEquals

class UnparenthesizeTest {
    @Test
    fun testUnparenthesize() {
        test(
            """
            fn main() {
                let _ = (((1 + (2))));
                let _ = (return);
                call!(((a + b)));
            }
            """.trimIndent(),
        )
    }

    private fun test(content: String) {
        val before = parseFile(content).getOrThrow()
        val flatBefore = FlattenParens.discardAttrs().visitFileMut(before.deepCopy())
        val printed = flatBefore.intoTokenStream()
        val after = parse2(FileParse::parse, printed).getOrThrow()
        val flatAfter = FlattenParens.discardAttrs().visitFileMut(after.deepCopy())
        val expected = AsIfPrinted().visitFileMut(flatBefore)

        assertEquals(expected.intoTokenStream().toString(), flatAfter.intoTokenStream().toString())
    }
}

private class FlattenParens(
    private val discardParenAttrs: Boolean,
) : VisitMut() {
    override fun visitExpr(e: Expr): Expr {
        var expr = e
        while (expr is Expr.Paren) {
            val parenAttrs = expr.attrs
            expr = expr.expr
            if (parenAttrs.isNotEmpty() && !discardParenAttrs) {
                expr = combineAttrs(expr, parenAttrs)
            }
        }
        return super.visitExpr(expr)
    }

    override fun visitTokenStreamMut(tokens: TokenStream): TokenStream =
        TokenStream.fromTokenTrees(tokens.flatMap(::flattenTokenTree))

    private fun flattenTokenTree(token: TokenTree): List<TokenTree> =
        when (token) {
            is TokenTree.Group -> {
                val delimiter = token.value.delimiter()
                val content = visitTokenStreamMut(token.value.stream())
                if (delimiter == Delimiter.Parenthesis) {
                    content.toList()
                } else {
                    listOf(TokenTree.Group(Group(delimiter, content)))
                }
            }
            else -> listOf(token)
        }

    private fun combineAttrs(expr: Expr, attrs: List<Attribute>): Expr =
        when (expr) {
            is Expr.Assign -> {
                require(expr.attrs.isEmpty())
                expr.copy(attrs = attrs)
            }
            is Expr.Binary -> {
                require(expr.attrs.isEmpty())
                expr.copy(attrs = attrs)
            }
            is Expr.Cast -> {
                require(expr.attrs.isEmpty())
                expr.copy(attrs = attrs)
            }
            else -> error("cannot combine parenthesized attributes into ${expr::class.simpleName}")
        }

    companion object {
        fun discardAttrs(): FlattenParens = FlattenParens(discardParenAttrs = true)
    }
}

internal class AsIfPrinted : VisitMut() {
    override fun visitFile(f: File): File =
        super.visitFile(f.copy(shebang = null))

    override fun visitGenerics(g: Generics): Generics {
        val generics = g.copy()
        if (generics.params.isEmpty()) {
            generics.ltToken = null
            generics.gtToken = null
        }
        if (generics.whereClause?.predicates?.isEmpty() == true) {
            generics.whereClause = null
        }
        return super.visitGenerics(generics)
    }

    override fun visitLifetimeParamMut(param: GenericParam.LifetimeParam): GenericParam.LifetimeParam {
        val visited = super.visitLifetimeParamMut(param)
        return if (visited.bounds.isEmpty()) visited.copy(colonToken = null) else visited
    }

    override fun visitTypeParamMut(param: GenericParam.TypeParam): GenericParam.TypeParam {
        val visited = super.visitTypeParamMut(param)
        return if (visited.bounds.isEmpty()) visited.copy(colonToken = null) else visited
    }

    override fun visitStmt(s: Stmt): Stmt {
        if (s is Stmt.ExprStmt && s.expr is Expr.Macro) {
            val expr = s.expr
            val printsAsMacroStmt =
                when (expr.mac.delimiter) {
                    is MacroDelimiter.Brace -> true
                    is MacroDelimiter.Paren,
                    is MacroDelimiter.Bracket,
                    -> s.semiToken != null
                }
            if (printsAsMacroStmt) {
                return super.visitStmt(Stmt.MacroStmt(expr.attrs, expr.mac, s.semiToken))
            }
        }
        return super.visitStmt(s)
    }
}
