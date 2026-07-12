// port-lint: tests tests/test_expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.intoTokenStream
import io.github.kotlinmania.syn.gen.VisitMut
import io.github.kotlinmania.syn.token.And
import io.github.kotlinmania.syn.token.As
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Break
import io.github.kotlinmania.syn.token.Dot
import io.github.kotlinmania.syn.token.DotDot
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.If
import io.github.kotlinmania.syn.token.Let
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Or
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.syn.token.Question
import io.github.kotlinmania.syn.token.Return
import io.github.kotlinmania.syn.token.ShlEq
import io.github.kotlinmania.syn.token.Star
import io.github.kotlinmania.syn.token.Underscore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for parsing of expressions.
 *
 * The upstream Rust tests drive `syn::parse2::<Expr>` and
 * `syn::parse_str::<Expr>` to parse expression token streams, then
 * assert the structural shape via the `snapshot!` macro (which expands
 * to `insta::assert_debug_snapshot!` against a `Lite` debug wrapper).
 * These Kotlin tests assert the resulting [Expr] variant and key fields
 * directly via [parseStr] / [parse2] against [ExprParse].
 */
class ExprTest {
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

        private fun combineAttrs(expr: Expr, attrs: List<Attribute>) {
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
            fun combineAttrs(): FlattenParens = FlattenParens(discardParenAttrs = false)
        }
    }

    private fun parse(s: String): Expr = parseStr(ExprParse::parse, s).getOrThrow()

    private fun parseTokens(ts: TokenStream): Expr = parse2(ExprParse::parse, ts).getOrThrow()

    private fun roundTrip(expr: Expr): Expr {
        val tokens = TokenStream.new()
        expr.toTokens(tokens)
        return parseTokens(tokens)
    }

    private fun tokens(expr: Expr): TokenStream =
        TokenStream.new().also(expr::toTokens)

    private fun assertUnboundedHalfOpenRange(expr: Expr) {
        val range = assertIs<Expr.Range>(expr)
        assertIs<RangeLimits.HalfOpen>(range.limits)
        assertEquals(null, range.start)
        assertEquals(null, range.end)
    }

    private fun assertTupleToTupleRange(expr: Expr) {
        val range = assertIs<Expr.Range>(expr)
        assertIs<RangeLimits.HalfOpen>(range.limits)
        val start = range.start
        assertTrue(start != null)
        assertIs<Expr.Tuple>(start)
        val end = range.end
        assertTrue(end != null)
        assertIs<Expr.Tuple>(end)
    }

    private fun assertPathExpr(expr: Expr, ident: String) {
        val path = assertIs<Expr.Path>(expr).path
        assertEquals(1, path.segments.len())
        assertEquals(
            ident,
            path.segments
                .first()
                ?.ident
                ?.toString(),
        )
    }

    private fun assertPathExpr(expr: Expr, vararg segments: String) {
        val path = assertIs<Expr.Path>(expr).path
        assertEquals(segments.size, path.segments.len())
        assertEquals(segments.toList(), path.segments.toList().map { it.ident.toString() })
    }

    // Upstream parses `..100u32` as `Expr::Range` with `limits: HalfOpen`
    // and `end: Some(Expr::Lit { lit: 100u32 })`.
    @Test
    fun testExprParse() {
        val expr = parse("..100u32")
        val range = assertIs<Expr.Range>(expr)
        assertIs<RangeLimits.HalfOpen>(range.limits)
        assertEquals(null, range.start)
        val end = range.end
        assertTrue(end != null)
        val lit = assertIs<Expr.Lit>(end)
        val intLit = assertIs<Lit.Int>(lit.lit)
        assertEquals("100", intLit.value.base10Digits())
        assertEquals("u32", intLit.value.suffix())
    }

    // Upstream parses `fut.await` as `Expr::Await` with
    // `base: Expr::Path { path: Path { segments: [fut] } }`, verifying
    // it does not parse as `Expr::Field`.
    @Test
    fun testAwait() {
        val expr = parse("fut.await")
        val await = assertIs<Expr.Await>(expr)
        val base = assertIs<Expr.Path>(await.base)
        assertEquals(1, base.path.segments.len())
        assertEquals(
            "fut",
            base.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
    }

    @Test
    fun testTupleMultiIndex() {
        val single = parse("tuple.0")
        val field = assertIs<Expr.Field>(single)
        val base = assertIs<Expr.Path>(field.base)
        assertEquals(1, base.path.segments.len())
        assertEquals(
            "tuple",
            base.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
        assertIs<Member.Unnamed>(field.member)

        val compactTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("tuple", Span.callSite())),
                    TokenTree.Punct(Punct('.', Spacing.Alone, Span.callSite())),
                    TokenTree.Literal(Literal.u32Unsuffixed(0u)),
                    TokenTree.Punct(Punct('.', Spacing.Alone, Span.callSite())),
                    TokenTree.Literal(Literal.u32Unsuffixed(0u)),
                ),
            )
        assertNestedTupleZero(parseTokens(compactTokens))

        for (input in listOf("tuple .0.0", "tuple. 0.0", "tuple.0 .0", "tuple.0. 0", "tuple . 0 . 0")) {
            assertNestedTupleZero(parse(input))
            assertNestedTupleZero(parseTokens(TokenStream.fromString(input).getOrThrow()))
        }
    }

    private fun assertNestedTupleZero(expr: Expr) {
        val outer = assertIs<Expr.Field>(expr)
        val outerMember = assertIs<Member.Unnamed>(outer.member)
        assertEquals(0u, outerMember.index.index)
        val inner = assertIs<Expr.Field>(outer.base)
        val innerMember = assertIs<Member.Unnamed>(inner.member)
        assertEquals(0u, innerMember.index.index)
        val innerBase = assertIs<Expr.Path>(inner.base)
        assertEquals(1, innerBase.path.segments.len())
        assertEquals(
            "tuple",
            innerBase.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
    }

    // Upstream builds a `Delimiter::None` group containing `f`, parses
    // `#path()` as `Expr::Call { func: Expr::Group { expr: Expr::Path
    // { path: f } } }`, and asserts the shape via snapshot.
    @Test
    fun testMacroVariableFunc() {
        val path = Group(Delimiter.None, TokenStream.fromString("f").getOrThrow())
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(path),
                    TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
                ),
            )
        val expr = parseTokens(tokens)
        val call = assertIs<Expr.Call>(expr)
        assertEquals(0, call.args.len())
        val group = assertIs<Expr.Group>(call.func)
        assertPathExpr(group.expr, "f")

        val attributedPath = Group(Delimiter.None, TokenStream.fromString("#[inside] f").getOrThrow())
        val attributedTokens = TokenStream.fromString("#[outside]").getOrThrow()
        attributedTokens.extendTokenTrees(
            listOf(
                TokenTree.Group(attributedPath),
                TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
            ),
        )
        val attributedCall = assertIs<Expr.Call>(parseTokens(attributedTokens))
        assertEquals(
            "outside",
            attributedCall.attrs
                .single()
                .path()
                .toString(),
        )
        val attributedGroup = assertIs<Expr.Group>(attributedCall.func)
        val attributedFunc = assertIs<Expr.Path>(attributedGroup.expr)
        assertEquals(
            "inside",
            attributedFunc.attrs
                .single()
                .path()
                .toString(),
        )
        assertPathExpr(attributedFunc, "f")
    }

    @Test
    fun testBlockLikeExpressionCanBeCallee() {
        val loopCall = assertIs<Expr.Call>(parse("loop {} ()"))
        assertIs<Expr.Loop>(loopCall.func)
        assertEquals(0, loopCall.args.len())

        val ifCall = assertIs<Expr.Call>(parse("if true { f } else { f } ()"))
        assertIs<Expr.If>(ifCall.func)
        assertEquals(0, ifCall.args.len())
    }

    // Upstream builds a `Delimiter::None` group containing `m` and parses
    // `#mac!()` as `Expr::Macro` with path `m`, paren delimiter, and
    // empty token stream.
    @Test
    fun testMacroVariableMacro() {
        val mac = Group(Delimiter.None, TokenStream.fromString("m").getOrThrow())
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(mac),
                    TokenTree.Punct(Punct('!', Spacing.Alone, Span.callSite())),
                    TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
                ),
            )
        val expr = parseTokens(tokens)
        val macro = assertIs<Expr.Macro>(expr)
        assertEquals(
            1,
            macro.mac.path.segments
                .len(),
        )
        assertEquals(
            "m",
            macro.mac.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
        assertIs<MacroDelimiter.Paren>(macro.mac.delimiter)
        assertEquals("", macro.mac.tokens.toString())
    }

    // Upstream builds a `Delimiter::None` group containing `S`, parses
    // `#s {}` as `Expr::Struct` with path `S` and empty fields. The
    // Kotlin parser unwraps the group and parses the struct shape
    // (path `S`, empty fields) but without the `Expr::Group` wrapper.
    @Test
    fun testMacroVariableStruct() {
        val s = Group(Delimiter.None, TokenStream.fromString("S").getOrThrow())
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(s),
                    TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
                ),
            )
        val expr = parseTokens(tokens)
        val struct = assertIs<Expr.Struct>(expr)
        assertEquals(1, struct.path.segments.len())
        assertEquals(
            "S",
            struct.path.segments
                .first()
                ?.ident
                ?.toString(),
        )
        assertEquals(0, struct.fields.len())
    }

    // Upstream builds a `Delimiter::None` group containing `&self`,
    // parses `#inner.method()` as `Expr::MethodCall { receiver:
    // Expr::Group { expr: Expr::Reference { expr: Expr::Path { path:
    // self } } }, method: "method" }`.
    @Test
    fun testMacroVariableUnary() {
        val inner = Group(Delimiter.None, TokenStream.fromString("&self").getOrThrow())
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(inner),
                    TokenTree.Punct(Punct('.', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("method", Span.callSite())),
                    TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
                ),
            )
        val expr = parseTokens(tokens)
        val method = assertIs<Expr.MethodCall>(expr)
        assertEquals("method", method.method.toString())
        val group = assertIs<Expr.Group>(method.receiver)
        val ref = assertIs<Expr.Reference>(group.expr)
        assertNull(ref.mutability)
        assertPathExpr(ref.expr, "self")
    }

    // Upstream builds `Delimiter::None` groups containing `#[a] ()` and
    // `loop {} + 1`, parses `match v { _ => #expr }` as `Expr::Match`
    // with a `Pat::Wild` arm whose body is the group.
    @Test
    fun testMacroVariableMatchArm() {
        val attrBody = Group(Delimiter.None, TokenStream.fromString("#[a] ()").getOrThrow())
        val attrTokens = TokenStream.fromString("_ =>").getOrThrow()
        attrTokens.extendTokenTrees(listOf(TokenTree.Group(attrBody)))
        val attrMatchTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("match", Span.callSite())),
                    TokenTree.Ident(Ident.new("v", Span.callSite())),
                    TokenTree.Group(Group(Delimiter.Brace, attrTokens)),
                ),
            )
        val attrMatch = assertIs<Expr.Match>(parseTokens(attrMatchTokens))
        assertPathExpr(attrMatch.expr, "v")
        val attrArm = attrMatch.arms.single()
        assertIs<Pat.Wild>(attrArm.pat)
        val attrGroup = assertIs<Expr.Group>(attrArm.body)
        val attrTuple = assertIs<Expr.Tuple>(attrGroup.expr)
        assertEquals(
            "a",
            attrTuple.attrs
                .single()
                .path()
                .toString(),
        )

        val armBody = Group(Delimiter.None, TokenStream.fromString("loop {} + 1").getOrThrow())
        val armTokens = TokenStream.fromString("_ =>").getOrThrow()
        armTokens.extendTokenTrees(listOf(TokenTree.Group(armBody)))
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("match", Span.callSite())),
                    TokenTree.Ident(Ident.new("v", Span.callSite())),
                    TokenTree.Group(Group(Delimiter.Brace, armTokens)),
                ),
            )

        val expr = parseTokens(tokens)
        val match = assertIs<Expr.Match>(expr)
        assertPathExpr(match.expr, "v")
        assertEquals(1, match.arms.size)
        val arm = match.arms.first()
        assertIs<Pat.Wild>(arm.pat)
        val group = assertIs<Expr.Group>(arm.body)
        val binary = assertIs<Expr.Binary>(group.expr)
        assertIs<Expr.Loop>(binary.left)
        assertIs<BinOp.Add>(binary.op)
        val lit = assertIs<Expr.Lit>(binary.right)
        assertIs<Lit.Int>(lit.lit)
    }

    // Upstream parses `|| .. .method()` as `Expr::MethodCall` with
    // `receiver: Expr::Closure` whose `body` is `Expr::Range` with
    // `limits: HalfOpen` and no end.
    @Test
    fun testClosureVsRangefull() {
        val expr = parse("|| .. .method()")
        val call = assertIs<Expr.MethodCall>(expr)
        val closure = assertIs<Expr.Closure>(call.receiver)
        assertIs<ReturnType.Default>(closure.output)
        val range = assertIs<Expr.Range>(closure.body)
        assertIs<RangeLimits.HalfOpen>(range.limits)
        assertEquals(null, range.start)
        assertEquals(null, range.end)
        assertEquals("method", call.method.toString())
    }

    // Upstream asserts that `|| &x as T[0]` and `|| () as ()()` fail to
    // parse (postfix operators are not allowed after a cast in closure
    // body position).
    @Test
    fun testPostfixOperatorAfterCast() {
        assertTrue(parseStr(ExprParse::parse, "|| &x as T[0]").isFailure)
        assertTrue(parseStr(ExprParse::parse, "|| () as ()()").isFailure)
    }

    // Upstream parses `..`, `..hi`, `lo..`, `lo..hi` as valid, `..=`
    // and `lo..=` as errors, `..=hi` and `lo..=hi` as valid inclusive
    // ranges, and `...` forms as errors.
    @Test
    fun testRangeKinds() {
        assertUnboundedHalfOpenRange(parse(".."))

        val openEnd = assertIs<Expr.Range>(parse("..hi"))
        assertIs<RangeLimits.HalfOpen>(openEnd.limits)
        assertEquals(null, openEnd.start)
        val openEndExpr = openEnd.end
        assertTrue(openEndExpr != null)
        assertPathExpr(openEndExpr, "hi")

        val openStart = assertIs<Expr.Range>(parse("lo.."))
        assertIs<RangeLimits.HalfOpen>(openStart.limits)
        val openStartExpr = openStart.start
        assertTrue(openStartExpr != null)
        assertPathExpr(openStartExpr, "lo")
        assertEquals(null, openStart.end)

        val openBoth = assertIs<Expr.Range>(parse("lo..hi"))
        assertIs<RangeLimits.HalfOpen>(openBoth.limits)
        val openBothStart = openBoth.start
        val openBothEnd = openBoth.end
        assertTrue(openBothStart != null)
        assertTrue(openBothEnd != null)
        assertPathExpr(openBothStart, "lo")
        assertPathExpr(openBothEnd, "hi")

        assertTrue(parseStr(ExprParse::parse, "..=").isFailure)

        val closedEnd = assertIs<Expr.Range>(parse("..=hi"))
        assertIs<RangeLimits.Closed>(closedEnd.limits)
        assertEquals(null, closedEnd.start)
        val closedEndExpr = closedEnd.end
        assertTrue(closedEndExpr != null)
        assertPathExpr(closedEndExpr, "hi")

        assertTrue(parseStr(ExprParse::parse, "lo..=").isFailure)

        val closedBoth = assertIs<Expr.Range>(parse("lo..=hi"))
        assertIs<RangeLimits.Closed>(closedBoth.limits)
        val closedBothStart = closedBoth.start
        val closedBothEnd = closedBoth.end
        assertTrue(closedBothStart != null)
        assertTrue(closedBothEnd != null)
        assertPathExpr(closedBothStart, "lo")
        assertPathExpr(closedBothEnd, "hi")

        assertTrue(parseStr(ExprParse::parse, "...").isFailure)
        assertTrue(parseStr(ExprParse::parse, "...hi").isFailure)
        assertTrue(parseStr(ExprParse::parse, "lo...").isFailure)
        assertTrue(parseStr(ExprParse::parse, "lo...hi").isFailure)
    }

    // Upstream parses `.. ..`, `.. .. ()`, `() .. ..` as nested
    // `Expr::Range` trees, parses `() = .. + ()` as an `Expr::Binary`
    // wrapping `Expr::Assign` wrapping `Expr::Range`, and asserts
    // `.. x ..` and `x .. x ..` are errors.
    @Test
    fun testRangePrecedence() {
        val r1 = parse(".. ..")
        val outer1 = assertIs<Expr.Range>(r1)
        assertIs<RangeLimits.HalfOpen>(outer1.limits)
        assertEquals(null, outer1.start)
        val end1 = outer1.end
        assertTrue(end1 != null)
        val inner1 = assertIs<Expr.Range>(end1)
        assertIs<RangeLimits.HalfOpen>(inner1.limits)
        assertEquals(null, inner1.start)
        assertEquals(null, inner1.end)

        val r2 = parse(".. .. ()")
        val outer2 = assertIs<Expr.Range>(r2)
        assertIs<RangeLimits.HalfOpen>(outer2.limits)
        val end2 = outer2.end
        assertTrue(end2 != null)
        val inner2 = assertIs<Expr.Range>(end2)
        assertIs<RangeLimits.HalfOpen>(inner2.limits)
        val innerEnd2 = inner2.end
        assertTrue(innerEnd2 != null)
        assertIs<Expr.Tuple>(innerEnd2)

        val r3 = assertIs<Expr.Range>(parse("() .. .."))
        val r3Start = r3.start
        assertTrue(r3Start != null)
        assertIs<Expr.Tuple>(r3Start)
        val r3End = r3.end
        assertTrue(r3End != null)
        assertUnboundedHalfOpenRange(r3End)

        val r4 = assertIs<Expr.Binary>(parse("() = .. + ()"))
        val r4Assign = assertIs<Expr.Assign>(r4.left)
        assertIs<Expr.Tuple>(r4Assign.left)
        assertUnboundedHalfOpenRange(r4Assign.right)
        assertIs<BinOp.Add>(r4.op)
        assertIs<Expr.Tuple>(r4.right)

        assertTrue(parseStr(ExprParse::parse, ".. x ..").isFailure)
        assertTrue(parseStr(ExprParse::parse, "x .. x ..").isFailure)
    }

    // Upstream asserts `#[allow()] ..` and `#[allow()] .. hi` fail,
    // and parses `#[allow()] lo .. hi` as a range whose start path
    // carries the attribute.
    @Test
    fun testRangeAttrs() {
        assertTrue(parseStr(ExprParse::parse, "#[allow()] ..").isFailure)
        assertTrue(parseStr(ExprParse::parse, "#[allow()] .. hi").isFailure)

        val range = assertIs<Expr.Range>(parse("#[allow()] lo .. hi"))
        val start = range.start
        assertTrue(start != null)
        val startPath = assertIs<Expr.Path>(start)
        val attr = startPath.attrs.single()
        assertEquals("allow", attr.path().toString())
        assertPathExpr(startPath, "lo")
        assertIs<RangeLimits.HalfOpen>(range.limits)
        assertPathExpr(assertNotNull(range.end), "hi")
    }

    // Upstream asserts `.. ?` and `.. .field` are errors, then parses
    // `return .. ?`, `break .. ?`, `|| .. ?` as `Expr::Try` wrapping
    // the keyword+range, `return .. .field` / `break .. .field` /
    // `|| .. .field` as `Expr::Field` wrapping the keyword+range, and
    // `return .. = ()` / `return .. += ()` as assign/compound-assign
    // wrapping the return-of-range.
    @Test
    fun testRangesBailout() {
        assertTrue(parseStr(ExprParse::parse, ".. ?").isFailure)
        assertTrue(parseStr(ExprParse::parse, ".. .field").isFailure)

        val returnTry = assertIs<Expr.Try>(parse("return .. ?"))
        val returnExpr = assertIs<Expr.Return>(returnTry.expr)
        val returned = returnExpr.expr
        assertTrue(returned != null)
        assertUnboundedHalfOpenRange(returned)

        val breakTry = assertIs<Expr.Try>(parse("break .. ?"))
        val breakExpr = assertIs<Expr.Break>(breakTry.expr)
        val broken = breakExpr.expr
        assertTrue(broken != null)
        assertUnboundedHalfOpenRange(broken)

        val closureTry = assertIs<Expr.Try>(parse("|| .. ?"))
        val tryClosure = assertIs<Expr.Closure>(closureTry.expr)
        assertUnboundedHalfOpenRange(tryClosure.body)

        val returnField = assertIs<Expr.Field>(parse("return .. .field"))
        val fieldReturn = assertIs<Expr.Return>(returnField.base)
        val fieldReturned = fieldReturn.expr
        assertTrue(fieldReturned != null)
        assertUnboundedHalfOpenRange(fieldReturned)
        assertEquals("field", assertIs<Member.Named>(returnField.member).ident.toString())

        val breakField = assertIs<Expr.Field>(parse("break .. .field"))
        val fieldBreak = assertIs<Expr.Break>(breakField.base)
        val fieldBroken = fieldBreak.expr
        assertTrue(fieldBroken != null)
        assertUnboundedHalfOpenRange(fieldBroken)
        assertEquals("field", assertIs<Member.Named>(breakField.member).ident.toString())

        val closureField = assertIs<Expr.Field>(parse("|| .. .field"))
        val fieldClosure = assertIs<Expr.Closure>(closureField.base)
        assertUnboundedHalfOpenRange(fieldClosure.body)
        assertEquals("field", assertIs<Member.Named>(closureField.member).ident.toString())

        val returnAssign = assertIs<Expr.Assign>(parse("return .. = ()"))
        val assignReturn = assertIs<Expr.Return>(returnAssign.left)
        val assignedReturned = assignReturn.expr
        assertTrue(assignedReturned != null)
        assertUnboundedHalfOpenRange(assignedReturned)
        assertIs<Expr.Tuple>(returnAssign.right)

        val returnCompound = assertIs<Expr.Binary>(parse("return .. += ()"))
        val compoundReturn = assertIs<Expr.Return>(returnCompound.left)
        val compoundReturned = compoundReturn.expr
        assertTrue(compoundReturned != null)
        assertUnboundedHalfOpenRange(compoundReturned)
        assertIs<BinOp.AddAssign>(returnCompound.op)
        assertIs<Expr.Tuple>(returnCompound.right)
    }

    // Upstream parses four `return`/`break` forms with `'label: loop`
    // bodies and asserts they succeed, then asserts one
    // `break 'label: loop { ... }` form is rejected (parentheses
    // required).
    @Test
    fun testAmbiguousLabel() {
        val returnStmt =
            assertIs<Stmt.ExprStmt>(
                parseStr(StmtParse::parse, "return 'label: loop { break 'label 42; };").getOrThrow(),
            )
        val returnExpr = assertIs<Expr.Return>(returnStmt.expr)
        val returnLoop = assertIs<Expr.Loop>(returnExpr.expr)
        assertEquals(
            "label",
            returnLoop.label
                ?.name
                ?.ident
                ?.toString(),
        )

        val parenthesizedBreak =
            assertIs<Stmt.ExprStmt>(
                parseStr(StmtParse::parse, "break ('label: loop { break 'label 42; });").getOrThrow(),
            )
        val breakExpr = assertIs<Expr.Break>(parenthesizedBreak.expr)
        val paren = assertIs<Expr.Paren>(breakExpr.expr)
        val parenLoop = assertIs<Expr.Loop>(paren.expr)
        assertEquals(
            "label",
            parenLoop.label
                ?.name
                ?.ident
                ?.toString(),
        )

        val binaryBreak =
            assertIs<Stmt.ExprStmt>(
                parseStr(StmtParse::parse, "break 1 + 'label: loop { break 'label 42; };").getOrThrow(),
            )
        val binaryBreakExpr = assertIs<Expr.Break>(binaryBreak.expr)
        val binary = assertIs<Expr.Binary>(binaryBreakExpr.expr)
        assertIs<BinOp.Add>(binary.op)
        val rhsLoop = assertIs<Expr.Loop>(binary.right)
        assertEquals(
            "label",
            rhsLoop.label
                ?.name
                ?.ident
                ?.toString(),
        )

        val nestedBreak =
            assertIs<Stmt.ExprStmt>(
                parseStr(StmtParse::parse, "break 'outer 'inner: loop { break 'inner 42; };").getOrThrow(),
            )
        val nestedBreakExpr = assertIs<Expr.Break>(nestedBreak.expr)
        assertEquals("outer", nestedBreakExpr.label?.ident?.toString())
        val innerLoop = assertIs<Expr.Loop>(nestedBreakExpr.expr)
        assertEquals(
            "inner",
            innerLoop.label
                ?.name
                ?.ident
                ?.toString(),
        )

        assertTrue(parseStr(StmtParse::parse, "break 'label: loop { break 'label 42; };").isFailure)
    }

    // Upstream builds a `Delimiter::None` group containing `a::b` and
    // parses `if #path {}` as `Expr::If` with a group condition,
    // `#path {}` as `Expr::Struct`, `#path :: c` as `Expr::Path`, and
    // `if #nested && false {}` as `Expr::If` with a binary condition
    // wrapping a group.
    @Test
    fun testExtendedInterpolatedPath() {
        val path = Group(Delimiter.None, TokenStream.fromString("a::b").getOrThrow())

        val ifTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("if", Span.callSite())),
                    TokenTree.Group(path),
                    TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
                ),
            )
        val ifExpr = assertIs<Expr.If>(parseTokens(ifTokens))
        val ifCond = assertIs<Expr.Group>(ifExpr.cond)
        assertPathExpr(ifCond.expr, "a", "b")
        assertEquals(0, ifExpr.thenBranch.stmts.size)

        val structTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(path),
                    TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
                ),
            )
        val structExpr = assertIs<Expr.Struct>(parseTokens(structTokens))
        assertEquals(
            listOf("a", "b"),
            structExpr.path.segments
                .toList()
                .map { it.ident.toString() },
        )

        val pathTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(path),
                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("c", Span.callSite())),
                ),
            )
        assertPathExpr(parseTokens(pathTokens), "a", "b", "c")

        val nested = Group(Delimiter.None, TokenStream.fromString("a::b || true").getOrThrow())
        val nestedIfTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("if", Span.callSite())),
                    TokenTree.Group(nested),
                    TokenTree.Punct(Punct('&', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct('&', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("false", Span.callSite())),
                    TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
                ),
            )
        val nestedIf = assertIs<Expr.If>(parseTokens(nestedIfTokens))
        val cond = assertIs<Expr.Binary>(nestedIf.cond)
        assertIs<BinOp.And>(cond.op)
        val leftGroup = assertIs<Expr.Group>(cond.left)
        val leftBinary = assertIs<Expr.Binary>(leftGroup.expr)
        assertIs<BinOp.Or>(leftBinary.op)
        assertPathExpr(leftBinary.left, "a", "b")
        assertIs<Lit.Bool>(assertIs<Expr.Lit>(leftBinary.right).lit)
        assertIs<Lit.Bool>(assertIs<Expr.Lit>(cond.right).lit)
        assertEquals(0, nestedIf.thenBranch.stmts.size)
    }

    @Test
    fun testTupleComma() {
        val elems = ExprList()
        val expr =
            Expr.Tuple(
                mutableListOf(),
                io.github.kotlinmania.syn.token.Paren
                    .default(),
                elems,
            )

        val empty = roundTrip(expr)
        val emptyTuple = assertIs<Expr.Tuple>(empty)
        assertEquals(0, emptyTuple.elems.len())

        elems.pushValue(
            Expr.Continue(
                mutableListOf(),
                io.github.kotlinmania.syn.token.Continue
                    .default(),
                null,
            ),
        )
        val one = roundTrip(expr)
        val oneTuple = assertIs<Expr.Tuple>(one)
        assertEquals(1, oneTuple.elems.len())
        assertIs<Expr.Continue>(oneTuple.elems.first())

        elems.pushPunct(
            io.github.kotlinmania.syn.token.Comma
                .default(),
        )
        val oneTrailing = roundTrip(expr)
        val oneTrailingTuple = assertIs<Expr.Tuple>(oneTrailing)
        assertEquals(1, oneTrailingTuple.elems.len())
        assertIs<Expr.Continue>(oneTrailingTuple.elems.first())

        elems.pushValue(
            Expr.Continue(
                mutableListOf(),
                io.github.kotlinmania.syn.token.Continue
                    .default(),
                null,
            ),
        )
        val two = roundTrip(expr)
        val twoTuple = assertIs<Expr.Tuple>(two)
        assertEquals(2, twoTuple.elems.len())
        val twoElems = twoTuple.elems.toList()
        assertIs<Expr.Continue>(twoElems[0])
        assertIs<Expr.Continue>(twoElems[1])

        elems.pushPunct(
            io.github.kotlinmania.syn.token.Comma
                .default(),
        )
        val twoTrailing = roundTrip(expr)
        val twoTrailingTuple = assertIs<Expr.Tuple>(twoTrailing)
        assertEquals(2, twoTrailingTuple.elems.len())
        val twoTrailingElems = twoTrailingTuple.elems.toList()
        assertIs<Expr.Continue>(twoTrailingElems[0])
        assertIs<Expr.Continue>(twoTrailingElems[1])
    }

    // Upstream parses `() + () + ()` as left-associative `Expr::Binary`,
    // `() += () += ()` as right-associative `Expr::Binary`, and asserts
    // `() == () == ()` is rejected (comparison operators cannot be
    // chained).
    @Test
    fun testBinopAssociativity() {
        val left = parse("() + () + ()")
        val leftOuter = assertIs<Expr.Binary>(left)
        assertIs<BinOp.Add>(leftOuter.op)
        val leftInner = assertIs<Expr.Binary>(leftOuter.left)
        assertIs<BinOp.Add>(leftInner.op)
        assertIs<Expr.Tuple>(leftInner.left)
        assertIs<Expr.Tuple>(leftInner.right)
        assertIs<Expr.Tuple>(leftOuter.right)

        val right = parse("() += () += ()")
        val rightOuter = assertIs<Expr.Binary>(right)
        assertIs<BinOp.AddAssign>(rightOuter.op)
        assertIs<Expr.Tuple>(rightOuter.left)
        val rightInner = assertIs<Expr.Binary>(rightOuter.right)
        assertIs<BinOp.AddAssign>(rightInner.op)
        assertIs<Expr.Tuple>(rightInner.left)
        assertIs<Expr.Tuple>(rightInner.right)

        // Chained comparison is rejected.
        val chained = parseStr(ExprParse::parse, "() == () == ()")
        assertTrue(chained.isFailure)
        val err = (chained as SynResult.Failure).error
        assertEquals("comparison operators cannot be chained", err.toString())
    }

    // Upstream parses `() = () .. ()` as `Expr::Assign` with a range
    // right-hand side, `() += () .. ()` as `Expr::Binary` with a range
    // right-hand side, and asserts `() .. () = ()` and `() .. () += ()`
    // are errors.
    @Test
    fun testAssignRangePrecedence() {
        val assign = assertIs<Expr.Assign>(parse("() = () .. ()"))
        assertIs<Expr.Tuple>(assign.left)
        assertTupleToTupleRange(assign.right)

        val compound = assertIs<Expr.Binary>(parse("() += () .. ()"))
        assertIs<Expr.Tuple>(compound.left)
        assertIs<BinOp.AddAssign>(compound.op)
        assertTupleToTupleRange(compound.right)

        assertTrue(parseStr(ExprParse::parse, "() .. () = ()").isFailure)
        assertTrue(parseStr(ExprParse::parse, "() .. () += ()").isFailure)
    }

    // Upstream asserts `a = a < a <` and `a = a .. a ..` and
    // `a = a .. a +=` fail to parse, then asserts `a < a < a` fails
    // with "comparison operators cannot be chained", `a .. a .. a`
    // fails with "unexpected token", and `a .. a += a` fails with
    // "unexpected token".
    @Test
    fun testChainedComparison() {
        val cmpErr = parseStr(ExprParse::parse, "a < a < a")
        assertTrue(cmpErr.isFailure)
        assertEquals("comparison operators cannot be chained", (cmpErr as SynResult.Failure).error.toString())

        assertTrue(parseStr(ExprParse::parse, "a .. a .. a").isFailure)
        assertTrue(parseStr(ExprParse::parse, "a .. a += a").isFailure)
    }

    @Test
    fun testParseUnparenthesize() {
        val cases =
            listOf(
                "2 * (1 + 1)",
                "0 + (0 + 0)",
                "(a = b) = c",
                "(x as i32) < 0",
                "1 + (x as i32) < 0",
                "(1 + 1).abs()",
                "(lo..hi)[..]",
                "(a..b)..(c..d)",
                "(x > ..) > x",
                "(&mut fut).await",
                "&mut (x as i32)",
                "-(x as i32)",
                "if (S {}) == 1 {}",
                "{ (m! {}) - 1 }",
                "match m { _ => ({}) - 1 }",
                "if let _ = (a && b) && c {}",
                "if let _ = (S {}) {}",
                "if (S {}) == 0 && let Some(_) = x {}",
                "break ('a: loop { break 'a 1 } + 1)",
                "a + (|| b) + c",
                "if let _ = ((break) - 1 || true) {}",
                "if let _ = (break + 1 || true) {}",
                "if break (break) {}",
                "if break break {} {}",
                "if return (..) {}",
                "if return .. {} {}",
                "if || (Struct {}) {}",
                "if || (Struct {}).await {}",
                "if break || Struct {}.await {}",
                "if break 'outer 'block: {} {}",
                "if ..'block: {} {}",
                "if break ({}).await {}",
                "(break)()",
                "(..) = ()",
                "(..) += ()",
                "(1 < 2) == (3 < 4)",
                "{ (let _ = ()) }",
                "(#[attr] thing).field",
                "#[attr] (1 + 1)",
                "#[attr] (x = 1)",
                "#[attr] (x += 1)",
                "#[attr] (1 as T)",
                "(return #[attr] (x + ..)).field",
                "(self.f)()",
                "(return)..=return",
                "1 + (return)..=1 + return",
                ".. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. .. ..",
            )

        for (case in cases) {
            val original = parseStr(ExprParse::parse, case).getOrElse { error("failed to parse `$case`: $it") }
            val flat = FlattenParens.combineAttrs().visitExpr(original.deepCopy())
            val reconstructed =
                runCatching { roundTrip(flat) }
                    .getOrElse { error("failed to reparse flattened `$case` as `${flat.intoTokenStream()}`: $it") }
            assertEquals(original.toString(), reconstructed.toString(), case)
        }
    }

    @Test
    fun testPermutations() {
        var checked = 0
        iterExprPermutations(4) { original ->
            assertPermutationRoundTrip(original)
            checked += 1
        }
        assertEquals(243_101, checked)
    }

    private fun assertPermutationRoundTrip(original: Expr) {
        val emitted = tokens(original)
        val parsed =
            runCatching { parseTokens(emitted) }
                .getOrElse { error("failed to parse: $emitted\n$original\n$it") }
        val asIfPrinted = AsIfPrinted.visitExprMut(original)
        val normalized = FlattenParens.combineAttrs().visitExpr(parsed)
        assertEquals(asIfPrinted, normalized, "before: $emitted\nafter: ${tokens(normalized)}")

        val tokensNoParen = FlattenParens.combineAttrs().flattened(emitted)
        if (emitted.toString() == tokensNoParen.toString()) return

        val parsedNoParen = parse2(ExprParse::parse, tokensNoParen).getOrNull() ?: return
        val normalizedNoParen = FlattenParens.combineAttrs().visitExpr(parsedNoParen)
        if (original == normalizedNoParen) {
            error("redundant parens: $tokensNoParen")
        }
    }

    private fun iterExprPermutations(depth: Int, emit: (Expr) -> Unit) {
        emit(pathExpr("x"))
        if (depth == 0) return

        val nextDepth = depth - 1

        iterExprPermutations(nextDepth) { expr ->
            iterExprPermutations(0) { simple ->
                emit(Expr.Assign(mutableListOf(), simple.deepCopy(), Eq.default(), expr.deepCopy()))
                emit(Expr.Assign(mutableListOf(), expr.deepCopy(), Eq.default(), simple.deepCopy()))
            }
        }

        iterExprPermutations(nextDepth) { expr ->
            iterExprPermutations(0) { simple ->
                for (op in listOf(BinOp.Add(Plus.default()), BinOp.Lt(Lt.default()), BinOp.ShlAssign(ShlEq.default()))) {
                    emit(Expr.Binary(mutableListOf(), simple.deepCopy(), op, expr.deepCopy()))
                    emit(Expr.Binary(mutableListOf(), expr.deepCopy(), op, simple.deepCopy()))
                }
            }
        }

        emit(Expr.BlockExpr(mutableListOf(), null, emptyBlock()))
        emit(Expr.Break(mutableListOf(), Break.default(), null, null))

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Break(mutableListOf(), Break.default(), null, expr.deepCopy()))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Call(mutableListOf(), expr.deepCopy(), Paren.default(), ExprList()))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Cast(mutableListOf(), expr.deepCopy(), As.default(), typePath("T")))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(
                Expr.Closure(
                    attrs = mutableListOf(),
                    constness = null,
                    asyncness = null,
                    capture = null,
                    or1Token = Or.default(),
                    inputs = PatList(),
                    or2Token = Or.default(),
                    output = ReturnType.Default,
                    body = expr.deepCopy(),
                ),
            )
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Field(mutableListOf(), expr.deepCopy(), Dot.default(), Member.Named(Ident.new("field", Span.callSite()))))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.If(mutableListOf(), If.default(), expr.deepCopy(), emptyBlock(), null))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Let(mutableListOf(), Let.default(), wildPat(), Eq.default(), expr.deepCopy()))
        }

        emit(Expr.Range(mutableListOf(), null, RangeLimits.HalfOpen(DotDot.default()), null))

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Range(mutableListOf(), null, RangeLimits.HalfOpen(DotDot.default()), expr.deepCopy()))
            emit(Expr.Range(mutableListOf(), expr.deepCopy(), RangeLimits.HalfOpen(DotDot.default()), null))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Reference(mutableListOf(), And.default(), null, expr.deepCopy()))
        }

        emit(Expr.Return(mutableListOf(), Return.default(), null))

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Return(mutableListOf(), Return.default(), expr.deepCopy()))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Try(mutableListOf(), expr.deepCopy(), Question.default()))
        }

        iterExprPermutations(nextDepth) { expr ->
            emit(Expr.Unary(mutableListOf(), UnOp.Deref(Star.default()), expr.deepCopy()))
        }
    }

    private fun pathExpr(ident: String): Expr =
        Expr.Path(mutableListOf(), null, Path.from(Ident.new(ident, Span.callSite())))

    private fun typePath(ident: String): SynType =
        SynType.Path(null, Path.from(Ident.new(ident, Span.callSite())))

    private fun emptyBlock(): Block =
        Block(Brace.default(), mutableListOf())

    private fun wildPat(): Pat =
        Pat.Wild(mutableListOf(), Underscore.default())
}
