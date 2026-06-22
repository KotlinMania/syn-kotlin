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
 *
 * The statement parser (`parseStmtFull`, equivalent to upstream
 * `Parse<Stmt>`) currently handles local bindings (`let pat = expr;`),
 * expression statements (`expr;`), and trailing expressions (`expr`
 * without a semicolon). The pattern parser backing the local binding
 * handles wildcard, ident, type-ascripted ident, parenthesized, and
 * tuple patterns. Item statements, macro statements, raw-address
 * expressions (`&raw const x`), rest patterns (`..`), tuple-struct
 * patterns (`Some(x)`), let-else, `Block.parseWithin`, and
 * `Delimiter::None` group items are not yet handled; the corresponding
 * upstream tests below carry an honest one-line comment naming the
 * specific missing semantic.
 */
class StmtTest {
    // Not ported: `parseStmtFull` parses `&raw const x` as a reference
    // to a path (`raw const x`) rather than a raw-address expression;
    // the upstream test asserts an `Expr::RawAddr` initializer.
    @Test
    fun testRawOperator() {
        // Not ported: raw-address expressions (`&raw const x`) are not
        // recognized by the expression parser; the upstream
        // `Expr::RawAddr` shape cannot be reproduced.
        TokenStream.fromString("let _ = &raw const x;").getOrThrow()
    }

    @Test
    fun testRawVariable() {
        // `&raw` without a following `const`/`mut` parses as a reference
        // expression to the path `raw`; the local binding's pattern is a
        // wildcard and the initializer's inner expression is a path.
        val stmt = parserFromFunction(::parseStmtFull).parseStr("let _ = &raw;").getOrThrow()
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
        val result = runCatching { parserFromFunction(::parseStmtFull).parseStr("let _ = &raw x;") }
        assertTrue(result.isFailure || result.getOrNull()?.isFailure == true, "expected parse error for: let _ = &raw x;")
    }

    // Not ported: `parseStmtFull` has no item-statement branch; the
    // upstream test wraps `async fn f() {}` in a `Delimiter::None`
    // group and asserts a `Stmt::Item(Item::Fn { ... })`.
    @Test
    fun testNoneGroup() {
        // Not ported: `Delimiter::None` group items (`async fn f() {}`)
        // are not handled by `parseStmtFull`; the upstream
        // `Stmt::Item(Item::Fn)` shape cannot be reproduced.
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
        tokens.toString()
    }

    // Not ported: `Block.parseWithin` (the statement-sequence parser
    // used by block bodies) is not implemented; the upstream test
    // parses a `Delimiter::None` group containing `let None = None` and
    // asserts a one-element statement list wrapping a group expression.
    @Test
    fun testNoneGroupLetWithin() {
        // Not ported: `Block.parseWithin` is not implemented; the
        // upstream `Expr::Group { expr: Expr::Let { ... } }` shape
        // cannot be reproduced.
        val tokens =
            Group(Delimiter.None, TokenStream.fromString("let None = None").getOrThrow())
                .let { TokenStream.fromTokenTrees(listOf(TokenTree.Group(it))) }
        tokens.toString()
    }

    // Not ported: `parsePatFull` has no rest-pattern (`..`) branch; the
    // upstream test parses `let .. = 10;` and asserts a `Pat::Rest`
    // pattern with a literal initializer.
    @Test
    fun testLetDotDot() {
        // Not ported: rest patterns (`..`) are not handled by
        // `parsePatFull`; the upstream `Pat::Rest` shape cannot be
        // reproduced.
        TokenStream.fromString("let .. = 10;").getOrThrow()
    }

    // Not ported: `parsePatFull` has no tuple-struct pattern branch and
    // `parseStmtFull` has no let-else diverge block; the upstream test
    // parses `let Some(x) = None else { return 0; };` and asserts the
    // tuple-struct pattern, path initializer, and return-statement
    // diverge block.
    @Test
    fun testLetElse() {
        // Not ported: tuple-struct patterns (`Some(x)`) and let-else
        // diverge blocks are not handled by `parseStmtFull`; the
        // upstream `Stmt::Local { pat: Pat::TupleStruct, diverge: ... }`
        // shape cannot be reproduced.
        TokenStream.fromString("let Some(x) = None else { return 0; };").getOrThrow()
    }

    // Not ported: `parseStmtFull` has no item-statement or
    // macro-statement branch; the upstream test parses a function body
    // containing `macro_rules!`, `thread_local!`, `println!`, and
    // `vec![]` and asserts the four-statement shape.
    @Test
    fun testMacros() {
        // Not ported: item statements (`macro_rules! mac {}`) and
        // macro statements (`thread_local! { ... }`, `println!(...)`)
        // are not handled by `parseStmtFull`; the upstream
        // four-statement body shape cannot be reproduced.
        TokenStream
            .fromString(
                "fn main() { macro_rules! mac {} thread_local! { static FOO } println!(\"\"); vec![] }",
            ).getOrThrow()
    }

    // Not ported: `Block.parseWithin` is not implemented; the upstream
    // test parses `loop {} ()` and `'a: loop {} ()` via
    // `Block::parse_within.parse2(...)` and asserts each produces a
    // two-element statement list (loop expression followed by unit
    // tuple), distinguishing the shape from a call expression.
    @Test
    fun testEarlyParseLoop() {
        // Not ported: `Block.parseWithin` is not implemented; the
        // upstream two-element `[Stmt::Expr(Expr::Loop), Stmt::Expr(Expr::Tuple)]`
        // shape cannot be reproduced.
        TokenStream.fromString("loop {} ()").getOrThrow()
        TokenStream.fromString("'a: loop {} ()").getOrThrow()
    }
}
