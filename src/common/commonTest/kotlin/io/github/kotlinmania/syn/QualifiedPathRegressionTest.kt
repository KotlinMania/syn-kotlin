package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class QualifiedPathRegressionTest {
    @Test
    fun qualifiedPathPreservesTypeTraitAndSpans() {
        val expr = assertIs<Expr.Path>(expression("<Vec<T> as a::b::Trait>::serialize::<U>"))
        val qself = assertNotNull(expr.qself)
        assertEquals(3, qself.position)
        assertEquals("Vec < T >", tokens(qself.ty))
        assertEquals("as", assertNotNull(qself.asToken).span.sourceText())
        assertEquals("<", qself.ltToken.span.sourceText())
        assertEquals(">", qself.gtToken.span.sourceText())
        assertEquals(listOf("a", "b", "Trait", "serialize"), expr.path.segments.toList().map { it.ident.toString() })
        assertEquals("serialize", expr.path.segments.toList().last().ident.span().sourceText())
        assertIs<PathArguments.AngleBracketed>(expr.path.segments.toList().last().arguments)
    }

    @Test
    fun qualifiedPathWithoutTraitRetainsTheAssociatedType() {
        val expr = assertIs<Expr.Path>(expression("<T>::serialize"))
        val qself = assertNotNull(expr.qself)
        assertEquals(0, qself.position)
        assertNull(qself.asToken)
        assertEquals("T", tokens(qself.ty))
        assertNotNull(expr.path.leadingColon)
        assertEquals("< T > :: serialize", tokens(expr))
    }

    @Test
    fun nestedQualifiedTypeParsesAndRoundTrips() {
        val source = "<<T as Trait>::Output as Other>::serialize"
        val expr = assertIs<Expr.Path>(expression(source))
        val inner = assertIs<SynType.Path>(assertNotNull(expr.qself).ty)
        assertNotNull(inner.qself)
        assertEquals("< < T as Trait > :: Output as Other > :: serialize", tokens(expr))
        assertEquals(tokens(expr), tokens(expression(tokens(expr))))
    }

    @Test
    fun expressionPrintingUsesTheQualifiedTraitBoundary() {
        val ty = typePath("<T as ::a::Trait<U>>::serialize::<V>")
        val expr = Expr.Path(emptyList(), ty.qself, ty.path)
        assertEquals("< T as :: a :: Trait < U > > :: serialize :: < V >", tokens(expr))
        assertEquals(2, assertNotNull(assertIs<Expr.Path>(expression(tokens(expr))).qself).position)
    }

    @Test
    fun expressionPrintingInsertsRequiredTurbofish() {
        val ty = typePath("Type<T>::member")
        val expr = Expr.Path(emptyList(), ty.qself, ty.path)
        assertEquals("Type :: < T > :: member", tokens(expr))
        assertIs<Expr.Path>(expression(tokens(expr)))
    }

    @Test
    fun qualifiedStructPrintingUsesTheSamePathRules() {
        val expr = assertIs<Expr.Struct>(expression("S { value: 1 }"))
        val ty = typePath("<T as Trait>::Output<U>")
        expr.qself = ty.qself
        expr.path = ty.path
        assertEquals("< T as Trait > :: Output :: < U > { value : 1 }", tokens(expr))
        val reparsed = assertIs<Expr.Struct>(expression(tokens(expr)))
        assertEquals(1, assertNotNull(reparsed.qself).position)
        assertEquals(1, reparsed.fields.len())
    }

    @Test
    fun structPrintingRetainsRestWhenPunctuationIsDefaulted() {
        val expr = assertIs<Expr.Struct>(expression("S { value: 1, ..base }"))
        expr.dot2Token = null
        assertEquals("S { value : 1 , .. base }", tokens(expr))
        val reparsed = assertIs<Expr.Struct>(expression(tokens(expr)))
        assertNotNull(reparsed.dot2Token)
        assertIs<Expr.Path>(reparsed.rest)
    }

    @Test
    fun returnConsumesQualifiedPathAsItsValue() {
        val expr = assertIs<Expr.Return>(expression("return <T as Trait>::VALUE"))
        assertNotNull(assertIs<Expr.Path>(expr.expr).qself)
    }

    @Test
    fun yieldConsumesQualifiedPathAsItsValue() {
        val expr = assertIs<Expr.Yield>(expression("yield <T as Trait>::VALUE"))
        assertNotNull(assertIs<Expr.Path>(expr.expr).qself)
    }

    @Test
    fun labeledBreakConsumesQualifiedPathAsItsValue() {
        val expr = assertIs<Expr.Break>(expression("break 'outer <T as Trait>::VALUE"))
        assertEquals("outer", assertNotNull(expr.label).ident.toString())
        assertNotNull(assertIs<Expr.Path>(expr.expr).qself)
    }

    @Test
    fun returnComparisonOperatorsDoNotStartQualifiedValues() {
        for (operator in listOf("<=", "<<=", ">")) {
            val expr = assertIs<Expr.Binary>(expression("return $operator value"))
            assertNull(assertIs<Expr.Return>(expr.left).expr)
        }
    }

    @Test
    fun malformedQualifiedPathsRemainErrors() {
        for (source in listOf("<T as>::value", "<T as Trait>", "<T>::", "return < value")) {
            assertTrue(parseStr(ExprParse::parse, source).isFailure, source)
        }
    }

    private fun expression(source: String): Expr = parseStr(ExprParse::parse, source).getOrThrow()

    private fun typePath(source: String): SynType.Path = assertIs<SynType.Path>(parseStr(SynTypeParseExpr::parse, source).getOrThrow())

    private fun tokens(value: ToTokens): String {
        val output = TokenStream.new()
        value.toTokens(output)
        return output.toString()
    }
}
