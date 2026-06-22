// port-lint: tests tests/test_stmt.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test

/**
 * Tests for parsing of statements.
 *
 * The upstream Rust tests drive `syn::parse_str::<Stmt>(...)` and
 * `Block::parse_within.parse2(...)`, which require a `Parse<Stmt>` entry
 * point and a `Block.parseWithin` statement-sequence parser, neither of
 * which is ported to this Kotlin codebase yet. The `snapshot!` macro
 * expands to `insta::assert_debug_snapshot!` against a `Lite` debug
 * wrapper, which is also not ported. Each test below carries an honest
 * one-line comment naming the specific missing semantic, rather than
 * emitting a fake simulation that tests a different invariant.
 *
 * The Rust source constructs token streams via `quote!` and
 * `TokenStream::from_iter`; the equivalent constructors
 * ([TokenStream.fromString] and [TokenStream.fromTokenTrees]) are
 * available here, but without a `Parse<Stmt>` implementation they cannot
 * be driven through to a structural assertion.
 */
class StmtTest {
    // Not ported: `Parse<Stmt>` (the top-level statement parser entry
    // point) is not implemented in this Kotlin port, so `let _ = &raw
    // const x;` cannot be parsed into a `Stmt.Local` whose `init.expr`
    // is an `Expr.RawAddr` for snapshot comparison.
    @Test
    fun testRawOperator() {
        // Not ported: `Parse<Stmt>` is not implemented; the upstream test
        // parses `let _ = &raw const x;` and asserts the local binding's
        // initializer expression is a raw-address expression with const
        // mutability and a path expression to `x`.
        TokenStream.fromString("let _ = &raw const x;").getOrThrow()
    }

    // Not ported: `Parse<Stmt>` is not implemented; the upstream test
    // parses `let _ = &raw;` and asserts the initializer is a reference
    // expression to the path `raw` (not a raw-address expression).
    @Test
    fun testRawVariable() {
        // Not ported: `Parse<Stmt>` is not implemented; the upstream test
        // asserts that `&raw` without a following `const`/`mut` is parsed
        // as a reference expression to a path named `raw`, distinguishing
        // it from the raw-address operator form.
        TokenStream.fromString("let _ = &raw;").getOrThrow()
    }

    // Not ported: `Parse<Stmt>` is not implemented; the upstream test
    // asserts that `let _ = &raw x;` fails to parse because the raw
    // address operator requires `const` or `mut` after `raw`.
    @Test
    fun testRawInvalid() {
        // Not ported: `Parse<Stmt>` is not implemented; the upstream test
        // asserts that parsing `let _ = &raw x;` returns an error because
        // `&raw` must be followed by `const` or `mut`, not a bare path.
        TokenStream.fromString("let _ = &raw x;").getOrThrow()
    }

    // Not ported: `Parse<Stmt>` is not implemented; the upstream test
    // wraps an `async fn f() {}` token stream in a `Delimiter::None`
    // group and parses it as a `Stmt::Item(Item::Fn { ... })` asserting
    // the asyncness, ident, generics, output, and empty block shape.
    @Test
    fun testNoneGroup() {
        // Not ported: `Parse<Stmt>` is not implemented; the upstream test
        // builds a `Delimiter::None` group containing `async fn f() {}`
        // and asserts it parses as a function item statement with
        // asyncness set, ident `f`, default return type, and empty block.
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
    // used by block bodies) is not implemented in this Kotlin port, so
    // a `Delimiter::None` group containing `let None = None` cannot be
    // parsed into a one-element statement list whose single statement is
    // a `Stmt::Expr(Expr::Group { expr: Expr::Let { ... } }, None)`.
    @Test
    fun testNoneGroupLetWithin() {
        // Not ported: `Block.parseWithin` is not implemented; the upstream
        // test wraps `let None = None` in a `Delimiter::None` group,
        // parses it via `Block::parse_within.parse2(...)`, and asserts the
        // result is a one-element list containing a group expression
        // wrapping a let expression whose pattern and expr are both the
        // path `None`.
        val tokens =
            Group(Delimiter.None, TokenStream.fromString("let None = None").getOrThrow())
                .let { TokenStream.fromTokenTrees(listOf(TokenTree.Group(it))) }
        tokens.toString()
    }

    // Not ported: `Parse<Stmt>` is not implemented; the upstream test
    // parses `let .. = 10;` and asserts the local binding's pattern is
    // `Pat::Rest` and the initializer expression is `Expr::Lit { lit: 10 }`.
    @Test
    fun testLetDotDot() {
        // Not ported: `Parse<Stmt>` is not implemented; the upstream test
        // asserts that `let .. = 10;` parses into a local binding with a
        // rest pattern and an integer-literal initializer.
        TokenStream.fromString("let .. = 10;").getOrThrow()
    }

    // Not ported: `Parse<Stmt>` is not implemented; the upstream test
    // parses `let Some(x) = None else { return 0; };` and asserts the
    // pattern is a tuple-struct pattern, the initializer expression is
    // the path `None`, and the diverging else block contains a return
    // statement with an integer-literal expression.
    @Test
    fun testLetElse() {
        // Not ported: `Parse<Stmt>` is not implemented; the upstream test
        // asserts the let-else shape: a `Some(x)` tuple-struct pattern,
        // a `None` path initializer, and a diverging block whose single
        // statement is `return 0` with an integer-literal expression.
        TokenStream.fromString("let Some(x) = None else { return 0; };").getOrThrow()
    }

    // Not ported: `Parse<Stmt>` is not implemented; the upstream test
    // parses a function body containing a `macro_rules!` item, a
    // `thread_local!` statement macro, a `println!("")` statement macro,
    // and a `vec![]` expression macro, asserting the four-statement shape
    // with each macro's path, delimiter, and token stream.
    @Test
    fun testMacros() {
        // Not ported: `Parse<Stmt>` is not implemented; the upstream test
        // asserts the four-statement body: an `Item::Macro` statement for
        // `macro_rules! mac {}` with brace delimiter, a `Stmt::Macro` for
        // `thread_local! { static FOO }` with brace delimiter, a
        // `Stmt::Macro` for `println!("")` with paren delimiter and a
        // trailing semicolon, and a `Stmt::Expr` wrapping an `Expr::Macro`
        // for `vec![]` with bracket delimiter and no trailing semicolon.
        TokenStream
            .fromString(
                "fn main() { macro_rules! mac {} thread_local! { static FOO } println!(\"\"); vec![] }",
            ).getOrThrow()
    }

    // Not ported: `Block.parseWithin` is not implemented; the upstream
    // test parses `loop {} ()` and `'a: loop {} ()` via
    // `Block::parse_within.parse2(...)` and asserts each produces a
    // two-element statement list: a loop expression (with optional label)
    // followed by a unit tuple expression, distinguishing the loop-then-
    // tuple shape from a call expression.
    @Test
    fun testEarlyParseLoop() {
        // Not ported: `Block.parseWithin` is not implemented; the upstream
        // test asserts that `loop {} ()` parses as a loop expression
        // followed by a unit tuple expression (not a call), and that
        // `'a: loop {} ()` parses as a labeled loop expression followed
        // by a unit tuple expression, exercising the early-parse-loop
        // disambiguation in the statement parser.
        TokenStream.fromString("loop {} ()").getOrThrow()
        TokenStream.fromString("'a: loop {} ()").getOrThrow()
    }
}
