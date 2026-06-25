// port-lint: tests tests/test_stmt.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for parsing of statements.
 */
class StmtTest {
    private fun assertMacroPath(mac: Macro, ident: String) {
        assertEquals(1, mac.path.segments.len())
        assertEquals(
            ident,
            mac.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
    }

    private fun assertPathExpr(expr: Expr, ident: String) {
        val path = assertIs<Expr.Path>(expr)
        assertEquals(1, path.path.segments.len())
        assertEquals(
            ident,
            path.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
    }

    @Test
    fun testRawOperator() {
        val stmt = parseStr(::parseStmtFull, "let _ = &raw const x;").getOrThrow()
        val local = assertIs<Stmt.Local>(stmt)
        assertIs<Pat.Wild>(local.pat)
        val init = local.init
        assertTrue(init != null)
        val raw = assertIs<Expr.RawAddr>(init.expr)
        assertIs<PointerMutability.Const>(raw.mutability)
        val path = assertIs<Expr.Path>(raw.expr)
        assertEquals(1, path.path.segments.len())
        assertEquals(
            "x",
            path.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
    }

    @Test
    fun testRawVariable() {
        // `&raw` without a following `const`/`mut` parses as a reference
        // expression to the path `raw`; the local binding's pattern is a
        // wildcard and the initializer's inner expression is a path.
        val stmt = parseStr(::parseStmtFull, "let _ = &raw;").getOrThrow()
        val local = assertIs<Stmt.Local>(stmt)
        assertIs<Pat.Wild>(local.pat)
        val init = local.init
        assertTrue(init != null)
        val ref = assertIs<Expr.Reference>(init.expr)
        assertNull(ref.mutability)
        val path = assertIs<Expr.Path>(ref.expr)
        val segment = path.path.segments.first()
        assertTrue(segment != null)
        assertEquals("raw", segment.ident.toString())
    }

    @Test
    fun testRawInvalid() {
        // `&raw x;` is not a valid statement: after `&raw` parses as a
        // reference to the path `raw`, the trailing `x` leaves the
        // stream unconsumed, so the statement parser rejects it.
        // `parseStmtFull` throws `SynError` on the missing `;` rather
        // than returning a `Failure`, so the assertion uses
        // `runCatching` to treat either outcome as a parse rejection.
        val result = runCatching { parseStr(::parseStmtFull, "let _ = &raw x;") }
        assertTrue(result.isFailure || result.getOrNull()?.isFailure == true, "expected parse error for: let _ = &raw x;")
    }

    @Test
    fun testNoneGroup() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(
                        Group(
                            Delimiter.None,
                            TokenStream.fromTokenTrees(
                                listOf(
                                    TokenTree.Ident(Ident.new("async", Span.callSite())),
                                    TokenTree.Ident(Ident.new("fn", Span.callSite())),
                                    TokenTree.Ident(Ident.new("f", Span.callSite())),
                                    TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
                                    TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
                                ),
                            ),
                        ),
                    ),
                ),
            )
        val stmt = parse2(StmtParse::parse, tokens).getOrThrow()
        val itemStmt = assertIs<Stmt.ItemStmt>(stmt)
        val fn = assertIs<Item.Fn>(itemStmt.item)
        assertIs<Visibility.Inherited>(fn.vis)
        assertTrue(fn.sig.asyncness != null)
        assertEquals("f", fn.sig.ident.toString())
        assertEquals(ReturnType.Default, fn.sig.output)
        val block = fn.block
        assertTrue(block != null)
        assertEquals(0, block.stmts.size)
    }

    @Test
    fun testNoneGroupLetWithin() {
        val tokens =
            Group(Delimiter.None, TokenStream.fromString("let None = None").getOrThrow())
                .let { TokenStream.fromTokenTrees(listOf(TokenTree.Group(it))) }
        val stmts = parse2(::parseWithin, tokens).getOrThrow()

        assertEquals(1, stmts.size)
        val stmt = assertIs<Stmt.ExprStmt>(stmts[0])
        assertNull(stmt.semiToken)
        val group = assertIs<Expr.Group>(stmt.expr)
        val let = assertIs<Expr.Let>(group.expr)
        val pat = assertIs<Pat.Ident>(let.pat)
        assertEquals("None", pat.ident.toString())
        assertPathExpr(let.expr, "None")
    }

    @Test
    fun testLetDotDot() {
        val stmt = parseStr(::parseStmtFull, "let .. = 10;").getOrThrow()
        val local = assertIs<Stmt.Local>(stmt)
        assertIs<Pat.Rest>(local.pat)
        val init = local.init
        assertTrue(init != null)
        val lit = assertIs<Expr.Lit>(init.expr)
        assertEquals("10", assertIs<Lit.Int>(lit.lit).value.base10Digits())
    }

    @Test
    fun testLetElse() {
        val stmt =
            parseStr(::parseStmtFull, "let Some(x) = None else { return 0; };").getOrThrow()

        val local = assertIs<Stmt.Local>(stmt)
        val pat = assertIs<Pat.TupleStruct>(local.pat)
        assertEquals("Some", pat.path.getIdent()?.toString())
        assertEquals(1, pat.elems.len())
        assertEquals("x", assertIs<Pat.Ident>(pat.elems.first()).ident.toString())
        val init = local.init
        assertTrue(init != null)
        assertPathExpr(init.expr, "None")
        val diverge = init.diverge
        assertTrue(diverge != null)
        val block = assertIs<Expr.BlockExpr>(diverge.expr)
        assertEquals(1, block.block.stmts.size)
        val returnStmt = assertIs<Stmt.ExprStmt>(block.block.stmts.first())
        assertTrue(returnStmt.semiToken != null)
        val ret = assertIs<Expr.Return>(returnStmt.expr)
        val retExpr = ret.expr
        assertTrue(retExpr != null)
        val lit = assertIs<Expr.Lit>(retExpr)
        assertEquals("0", assertIs<Lit.Int>(lit.lit).value.base10Digits())
    }

    @Test
    fun testMacros() {
        val stmt =
            parseStr(::parseStmtFull, "fn main() { macro_rules! mac {} thread_local! { static FOO } println!(\"\"); vec![] }")
                .getOrThrow()

        val itemStmt = assertIs<Stmt.ItemStmt>(stmt)
        val fn = assertIs<Item.Fn>(itemStmt.item)
        assertEquals("main", fn.sig.ident.toString())
        val block = fn.block
        assertTrue(block != null)
        assertEquals(4, block.stmts.size)

        val macroRulesStmt = assertIs<Stmt.ItemStmt>(block.stmts[0])
        val macroRules = assertIs<Item.Macro>(macroRulesStmt.item)
        assertEquals("mac", macroRules.ident?.toString())
        assertMacroPath(macroRules.mac, "macro_rules")
        assertIs<MacroDelimiter.Brace>(macroRules.mac.delimiter)
        assertNull(macroRules.semiToken)

        val threadLocal = assertIs<Stmt.MacroStmt>(block.stmts[1])
        assertMacroPath(threadLocal.mac, "thread_local")
        assertIs<MacroDelimiter.Brace>(threadLocal.mac.delimiter)
        assertNull(threadLocal.semiToken)
        assertEquals("static FOO", threadLocal.mac.tokens.toString())

        val println = assertIs<Stmt.MacroStmt>(block.stmts[2])
        assertMacroPath(println.mac, "println")
        assertIs<MacroDelimiter.Paren>(println.mac.delimiter)
        assertTrue(println.semiToken != null)
        assertEquals("\"\"", println.mac.tokens.toString())

        val vecStmt = assertIs<Stmt.ExprStmt>(block.stmts[3])
        assertNull(vecStmt.semiToken)
        val vec = assertIs<Expr.Macro>(vecStmt.expr)
        assertMacroPath(vec.mac, "vec")
        assertIs<MacroDelimiter.Bracket>(vec.mac.delimiter)
        assertEquals("", vec.mac.tokens.toString())
    }

    @Test
    fun testEarlyParseLoop() {
        val stmts = parseStr(::parseWithin, "loop {} ()").getOrThrow()

        assertEquals(2, stmts.size)
        val loopStmt = assertIs<Stmt.ExprStmt>(stmts[0])
        assertIs<Expr.Loop>(loopStmt.expr)
        assertNull(loopStmt.semiToken)
        val tupleStmt = assertIs<Stmt.ExprStmt>(stmts[1])
        assertIs<Expr.Tuple>(tupleStmt.expr)
        assertNull(tupleStmt.semiToken)

        val labeled = parseStr(::parseWithin, "'a: loop {} ()").getOrThrow()
        assertEquals(2, labeled.size)
        val labeledLoopStmt = assertIs<Stmt.ExprStmt>(labeled[0])
        val labeledLoop = assertIs<Expr.Loop>(labeledLoopStmt.expr)
        assertEquals(
            "a",
            labeledLoop.label
                ?.name
                ?.ident
                ?.toString(),
        )
        assertNull(labeledLoopStmt.semiToken)
        val labeledTupleStmt = assertIs<Stmt.ExprStmt>(labeled[1])
        assertIs<Expr.Tuple>(labeledTupleStmt.expr)
        assertNull(labeledTupleStmt.semiToken)
    }

    @Test
    fun testStatementSemicolonRules() {
        assertTrue(parseStr(StmtParse::parse, "x").isFailure)
        assertTrue(parseStr(StmtParse::parse, "return 1").isFailure)
        assertTrue(parseStr(::parseWithin, "x y").isFailure)

        val loopStmt = assertIs<Stmt.ExprStmt>(parseStr(StmtParse::parse, "loop {}").getOrThrow())
        assertIs<Expr.Loop>(loopStmt.expr)
        assertNull(loopStmt.semiToken)
    }
}
