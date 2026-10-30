package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ExpressionDispatchParityTest {
    private fun expression(source: String): Expr = parseStr(::parseExpr, source).getOrThrow()

    @Test
    fun forLoopsRetainLabelsPatternsIteratorAndInnerAttributes() {
        val source = "#[outer] 'rows: for (x, y) in values { #![inner] consume(x, y); }"
        val loop = assertIs<Expr.ForLoop>(expression(source))
        assertEquals(
            "rows",
            loop.label
                ?.name
                ?.ident
                .toString(),
        )
        assertIs<Pat.Tuple>(loop.pat)
        assertIs<Expr.Path>(loop.expr)
        assertEquals(listOf("outer", "inner"), loop.attrs.map { it.path().getIdent().toString() })
        assertEquals(1, loop.body.stmts.size)
        assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(loop.toTokenStream()))
        assertIs<Expr.ForLoop>(expression("for item in (Values { field: 1 }) {}"))
    }

    @Test
    fun forLoopMatchArmUsesEarlyBoundaryAndDoesNotStealHigherRankedClosures() {
        val source = "match n { 0 => for item in values {} _ => {} }"
        val match = assertIs<Expr.Match>(expression(source))
        assertEquals(2, match.arms.size)
        assertIs<Expr.ForLoop>(match.arms.first().body)
        assertEquals(null, match.arms.first().comma)
        assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(match.toTokenStream()))
        assertIs<Expr.Closure>(expression("for<'a> |x: &'a u8| -> &'a u8 { x }"))
    }

    @Test
    fun bindingSubpatternsUseTheExistingCompletePatternParser() {
        val source = "match value { other @ (Kind::One(_) | Kind::Two(_)) => other }"
        val match = assertIs<Expr.Match>(expression(source))
        val binding = assertIs<Pat.Ident>(match.arms.single().pat)
        assertEquals("other", binding.ident.toString())
        assertIs<Pat.Or>(assertIs<Pat.PatParen>(binding.subpat).pat)
        assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(match.toTokenStream()))
        val function = assertIs<Item.Fn>(parseFile("fn f() { let x @ Some(_) = source; }").getOrThrow().items.single())
        val local = assertIs<Stmt.Local>(function.block!!.stmts.single())
        assertIs<Pat.TupleStruct>(assertIs<Pat.Ident>(local.pat).subpat)
    }

    @Test
    fun placeholderExpressionsRetainDiscardAssignmentsAndTuplePositions() {
        assertIs<Expr.Infer>(expression("_"))
        val source = "_ = precise_capture"
        val assignment = assertIs<Expr.Assign>(expression(source))
        assertIs<Expr.Infer>(assignment.left)
        assertIs<Expr.Path>(assignment.right)
        assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(assignment.toTokenStream()))
        val destructure = assertIs<Expr.Assign>(expression("(_, x) = pair"))
        assertIs<Expr.Infer>(assertIs<Expr.Tuple>(destructure.left).elems.toList().first())
    }

    @Test
    fun malformedLoopsAndSubpatternsRejectWithoutLostTokens() {
        for (source in listOf("for x values {}", "for in values {}", "for x in {}", "'a: for<'b> |x| x", "match n { x @ => 1 }")) {
            assertTrue(parseStr(::parseExpr, source).isFailure, source)
        }
        assertTrue(parseFile("fn f(A | B: T) {}").isFailure)
    }

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
