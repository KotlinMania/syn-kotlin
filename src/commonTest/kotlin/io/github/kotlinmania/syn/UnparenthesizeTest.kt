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
        val flatBefore = before.deepCopy()
        FlattenParens.discardAttrs().visitFileMut(flatBefore)
        val printed = flatBefore.intoTokenStream()
        val after = parse2(FileParse::parse, printed).getOrThrow()
        val flatAfter = after.deepCopy()
        FlattenParens.discardAttrs().visitFileMut(flatAfter)
        AsIfPrinted.visitFileMut(flatBefore)
        val expected = flatBefore

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
            expr.attrs = mutableListOf()
            expr = expr.expr
            if (parenAttrs.isNotEmpty() && !discardParenAttrs) {
                combineAttrs(expr, parenAttrs)
            }
        }
        return super.visitExpr(expr)
    }

    fun flattened(tokens: TokenStream): TokenStream =
        TokenStream.fromTokenTrees(tokens.toList().flatMap(::flattenTokenTree))

    override fun visitTokenStreamMut(tokens: TokenStream) {
        tokens.replaceFrom(flattened(tokens))
    }

    private fun flattenTokenTree(token: TokenTree): List<TokenTree> =
        when (token) {
            is TokenTree.Group -> {
                val delimiter = token.value.delimiter()
                val content = token.value.stream()
                visitTokenStreamMut(content)
                if (delimiter == Delimiter.Parenthesis) {
                    content.toList()
                } else {
                    listOf(TokenTree.Group(Group(delimiter, content)))
                }
            }
            else -> listOf(token)
        }

    private fun combineAttrs(expr: Expr, attrs: MutableList<Attribute>) {
        when (expr) {
            is Expr.Assign -> {
                require(expr.attrs.isEmpty())
                expr.attrs = attrs
            }
            is Expr.Binary -> {
                require(expr.attrs.isEmpty())
                expr.attrs = attrs
            }
            is Expr.Cast -> {
                require(expr.attrs.isEmpty())
                expr.attrs = attrs
            }
            else -> error("cannot combine parenthesized attributes into ${expr::class.simpleName}")
        }
    }

    companion object {
        fun discardAttrs(): FlattenParens = FlattenParens(discardParenAttrs = true)
    }
}

internal object AsIfPrinted : VisitMut() {
    override fun visitFile(f: File) {
        f.shebang = null
        super.visitFile(f)
    }

    override fun visitGenerics(g: Generics) {
        if (g.params.isEmpty()) {
            g.ltToken = null
            g.gtToken = null
        }
        if (g.whereClause?.predicates?.isEmpty() == true) {
            g.whereClause = null
        }
        super.visitGenerics(g)
    }

    override fun visitLifetimeParamMut(param: GenericParam.LifetimeParam) {
        if (param.bounds.isEmpty()) param.colonToken = null
        super.visitLifetimeParamMut(param)
    }

    override fun visitTypeParamMut(param: GenericParam.TypeParam) {
        if (param.bounds.isEmpty()) param.colonToken = null
        super.visitTypeParamMut(param)
    }

    override fun visitStmt(s: Stmt): Stmt {
        if (s is Stmt.ExprStmt) {
            val expr = s.expr
            if (expr is Expr.Macro) {
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
        }
        return super.visitStmt(s)
    }
}
