// port-lint: tests tests/test_precedence.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.syn.token.And
import io.github.kotlinmania.syn.token.AndAnd
import io.github.kotlinmania.syn.token.AndEq
import io.github.kotlinmania.syn.token.As
import io.github.kotlinmania.syn.token.Bracket
import io.github.kotlinmania.syn.token.Break
import io.github.kotlinmania.syn.token.Caret
import io.github.kotlinmania.syn.token.CaretEq
import io.github.kotlinmania.syn.token.Continue
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.EqEq
import io.github.kotlinmania.syn.token.Ge
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Le
import io.github.kotlinmania.syn.token.Let
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Minus
import io.github.kotlinmania.syn.token.MinusEq
import io.github.kotlinmania.syn.token.Ne
import io.github.kotlinmania.syn.token.Not
import io.github.kotlinmania.syn.token.Or
import io.github.kotlinmania.syn.token.OrEq
import io.github.kotlinmania.syn.token.OrOr
import io.github.kotlinmania.syn.token.Percent
import io.github.kotlinmania.syn.token.PercentEq
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.syn.token.PlusEq
import io.github.kotlinmania.syn.token.Pound
import io.github.kotlinmania.syn.token.Shl
import io.github.kotlinmania.syn.token.ShlEq
import io.github.kotlinmania.syn.token.Shr
import io.github.kotlinmania.syn.token.ShrEq
import io.github.kotlinmania.syn.token.Slash
import io.github.kotlinmania.syn.token.SlashEq
import io.github.kotlinmania.syn.token.Star
import io.github.kotlinmania.syn.token.StarEq
import io.github.kotlinmania.syn.token.Underscore
import kotlin.test.Test
import kotlin.test.assertEquals

class PrecedenceTest {
    // Upstream rustc precedence stress testing depends on nightly compiler internals and a full compiler source checkout.

    private fun intLit(): Expr.Lit =
        Expr.Lit(attrs = mutableListOf(), lit = Lit.Int(LitInt.new("1", "", Span.callSite())))

    private fun identPath(name: String): Expr.Path {
        val segments = PathSegmentList()
        segments.pushValue(PathSegment.from(Ident.new(name, Span.callSite())))
        return Expr.Path(attrs = mutableListOf(), qself = null, path = Path(null, segments))
    }

    private fun typePath(name: String): SynType.Path {
        val segments = PathSegmentList()
        segments.pushValue(PathSegment.from(Ident.new(name, Span.callSite())))
        return SynType.Path(qself = null, path = Path(null, segments))
    }

    private fun cfgPathMeta(): Meta.PathMeta {
        val segments = PathSegmentList()
        segments.pushValue(PathSegment.from(Ident.new("cfg", Span.callSite())))
        return Meta.PathMeta(Path(null, segments))
    }

    @Test
    fun testArithmeticBinopPrecedence() {
        assertEquals(Precedence.Sum, Precedence.ofBinop(BinOp.Add(Plus.default())))
        assertEquals(Precedence.Sum, Precedence.ofBinop(BinOp.Sub(Minus.default())))
        assertEquals(Precedence.Product, Precedence.ofBinop(BinOp.Mul(Star.default())))
        assertEquals(Precedence.Product, Precedence.ofBinop(BinOp.Div(Slash.default())))
        assertEquals(Precedence.Product, Precedence.ofBinop(BinOp.Rem(Percent.default())))
    }

    @Test
    fun testShiftBinopPrecedence() {
        assertEquals(Precedence.Shift, Precedence.ofBinop(BinOp.Shl(Shl.default())))
        assertEquals(Precedence.Shift, Precedence.ofBinop(BinOp.Shr(Shr.default())))
    }

    @Test
    fun testBitwiseBinopPrecedence() {
        assertEquals(Precedence.BitAnd, Precedence.ofBinop(BinOp.BitAnd(And.default())))
        assertEquals(Precedence.BitOr, Precedence.ofBinop(BinOp.BitOr(Or.default())))
        assertEquals(Precedence.BitXor, Precedence.ofBinop(BinOp.BitXor(Caret.default())))
    }

    @Test
    fun testLogicalBinopPrecedence() {
        assertEquals(Precedence.And, Precedence.ofBinop(BinOp.And(AndAnd.default())))
        assertEquals(Precedence.Or, Precedence.ofBinop(BinOp.Or(OrOr.default())))
    }

    @Test
    fun testComparisonBinopPrecedence() {
        assertEquals(Precedence.Compare, Precedence.ofBinop(BinOp.Eq(EqEq.default())))
        assertEquals(Precedence.Compare, Precedence.ofBinop(BinOp.Ne(Ne.default())))
        assertEquals(Precedence.Compare, Precedence.ofBinop(BinOp.Lt(Lt.default())))
        assertEquals(Precedence.Compare, Precedence.ofBinop(BinOp.Le(Le.default())))
        assertEquals(Precedence.Compare, Precedence.ofBinop(BinOp.Ge(Ge.default())))
        assertEquals(Precedence.Compare, Precedence.ofBinop(BinOp.Gt(Gt.default())))
    }

    @Test
    fun testAssignBinopPrecedence() {
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.AddAssign(PlusEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.SubAssign(MinusEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.MulAssign(StarEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.DivAssign(SlashEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.RemAssign(PercentEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.BitXorAssign(CaretEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.BitAndAssign(AndEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.BitOrAssign(OrEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.ShlAssign(ShlEq.default())))
        assertEquals(Precedence.Assign, Precedence.ofBinop(BinOp.ShrAssign(ShrEq.default())))
    }

    @Test
    fun testPrefixExprPrecedence() {
        val unary =
            Expr.Unary(
                attrs = mutableListOf(),
                op = UnOp.Deref(Star.default()),
                expr = intLit(),
            )
        assertEquals(Precedence.Prefix, Precedence.of(unary))
    }

    @Test
    fun testCastExprPrecedence() {
        val cast =
            Expr.Cast(
                attrs = mutableListOf(),
                expr = intLit(),
                asToken = As.default(),
                ty = typePath("f64"),
            )
        assertEquals(Precedence.Cast, Precedence.of(cast))
    }

    @Test
    fun testAssignExprPrecedence() {
        val assign =
            Expr.Assign(
                attrs = mutableListOf(),
                left = identPath("x"),
                eqToken = Eq.default(),
                right = intLit(),
            )
        assertEquals(Precedence.Assign, Precedence.of(assign))
    }

    @Test
    fun testBinaryExprPrecedenceDelegatesToOp() {
        val binary =
            Expr.Binary(
                attrs = mutableListOf(),
                left = intLit(),
                op = BinOp.Add(Plus.default()),
                right = intLit(),
            )
        assertEquals(Precedence.ofBinop(BinOp.Add(Plus.default())), Precedence.of(binary))
        assertEquals(Precedence.Sum, Precedence.of(binary))
    }

    @Test
    fun testUnambiguousExprPrecedence() {
        assertEquals(Precedence.Unambiguous, Precedence.of(identPath("x")))
        assertEquals(Precedence.Unambiguous, Precedence.of(intLit()))
    }

    @Test
    fun testLetExprPrecedence() {
        val letExpr =
            Expr.Let(
                attrs = mutableListOf(),
                letToken = Let.default(),
                pat = Pat.Wild(attrs = mutableListOf(), underscoreToken = Underscore.default()),
                eqToken = Eq.default(),
                expr = intLit(),
            )
        assertEquals(Precedence.Let, Precedence.of(letExpr))
    }

    @Test
    fun testBreakExprPrecedence() {
        val bareBreak =
            Expr.Break(
                attrs = mutableListOf(),
                breakToken = Break.default(),
                label = null,
                expr = null,
            )
        assertEquals(Precedence.Unambiguous, Precedence.of(bareBreak))

        val valuedBreak =
            Expr.Break(
                attrs = mutableListOf(),
                breakToken = Break.default(),
                label = null,
                expr = intLit(),
            )
        assertEquals(Precedence.Jump, Precedence.of(valuedBreak))
    }

    @Test
    fun testContinueExprPrecedence() {
        val continueExpr =
            Expr.Continue(
                attrs = mutableListOf(),
                continueToken = Continue.default(),
                label = null,
            )
        assertEquals(Precedence.Unambiguous, Precedence.of(continueExpr))
    }

    @Test
    fun testPrefixAttrsClassifiesOuterAttributeAsPrefix() {
        val outerAttr =
            Attribute(
                poundToken = Pound.default(),
                style = AttrStyle.Outer,
                bracketToken = Bracket.default(),
                meta = cfgPathMeta(),
            )
        assertEquals(Precedence.Prefix, Precedence.prefixAttrs(listOf(outerAttr)))
    }

    @Test
    fun testPrefixAttrsClassifiesInnerAttributeAsUnambiguous() {
        val innerAttr =
            Attribute(
                poundToken = Pound.default(),
                style = AttrStyle.Inner(Not.default()),
                bracketToken = Bracket.default(),
                meta = cfgPathMeta(),
            )
        assertEquals(Precedence.Unambiguous, Precedence.prefixAttrs(listOf(innerAttr)))
    }

    @Test
    fun testEmptyAttrsAreUnambiguous() {
        assertEquals(Precedence.Unambiguous, Precedence.prefixAttrs(emptyList()))
    }

    @Test
    fun testMinPrecedenceIsJump() {
        assertEquals(Precedence.Jump, Precedence.MIN)
    }
}
