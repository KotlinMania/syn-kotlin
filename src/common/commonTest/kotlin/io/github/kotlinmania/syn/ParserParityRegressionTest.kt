package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.syn.gen.Fold
import io.github.kotlinmania.syn.gen.Visit
import io.github.kotlinmania.syn.gen.VisitMut
import io.github.kotlinmania.syn.gen.clone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

/** Regressions checked against the local original Rust Syn 2.0.114 parser. */
class ParserParityRegressionTest {
    private fun impl(source: String): Item.Impl =
        assertIs<Item.Impl>(parseStr(ItemParse::parse, source).getOrThrow())

    private fun assertRoundTrip(source: String) {
        val item = impl(source)
        assertEquals(TokenStream.fromString(source).getOrThrow().toString(), item.toTokenStream().toString())
    }

    @Test
    fun associatedConstantPrefixesRetainVisibilityAndAttributes() {
        val source = "impl S { #[first] #[second] pub(crate) default const VALUE: u8 = 7; }"
        val value = assertIs<ImplItem.Const>(impl(source).items.single())
        assertEquals(listOf("first", "second"), value.attrs.map { it.path().getIdent().toString() })
        assertIs<Visibility.Restricted>(value.vis)
        assertNotNull(value.defaultness)
        assertEquals("VALUE", value.ident.toString())
        assertRoundTrip(source)
        val documented = assertIs<ImplItem.Const>(impl("impl S { /// Kept with VALUE.\n pub const VALUE: bool = true; }").items.single())
        assertIs<Visibility.Public>(documented.vis)
        assertTrue(
            documented.attrs
                .single()
                .path()
                .isIdent("doc"),
        )
    }

    @Test
    fun associatedDispatchPreservesSourceOrderAndConstFunctions() {
        val source = "impl S { pub const A: u8 = 1; #[method] pub const fn f() {} #[kind] pub type T = u8; const _: u8 = 2; }"
        val items = impl(source).items
        assertEquals(4, items.size)
        assertIs<ImplItem.Const>(items[0])
        val method = assertIs<ImplItem.Fn>(items[1])
        assertNotNull(method.sig.constness)
        assertTrue(
            method.attrs
                .single()
                .path()
                .isIdent("method"),
        )
        assertTrue(
            assertIs<ImplItem.AssocType>(items[2])
                .attrs
                .single()
                .path()
                .isIdent("kind"),
        )
        assertEquals("_", assertIs<ImplItem.Const>(items[3]).ident.toString())
        assertRoundTrip(source)
    }

    @Test
    fun associatedMacrosUseTheirRealDelimiterAndPrefixRules() {
        for (invocation in listOf("default!();", "crate::m![];", "self::m!{}", "::m!{}")) {
            val source = "impl S { #[doc = \"macro\"] $invocation }"
            assertIs<ImplItem.Macro>(impl(source).items.single())
            assertRoundTrip(source)
        }
        for (member in listOf("pub m!();", "default m!();", "m!()", ";")) {
            assertTrue(parseStr(ItemParse::parse, "impl S { $member }").isFailure, member)
        }
    }

    @Test
    fun syntacticallyAcceptedUnrepresentedItemsRetainAllTokens() {
        for (member in listOf(
            "#[kept] pub fn no_body();",
            "#[kept] pub const MISSING: u8;",
            "#[kept] const GENERIC<T>: u8 = 1;",
            "#[kept] const WHERE: u8 = 1 where T: Bound;",
            "const X: u8 = 1 where ;",
            "#[kept] type Output: Bound;",
        )) {
            val source = "impl S { $member }"
            assertIs<ImplItem.Verbatim>(impl(source).items.single())
            assertRoundTrip(source)
        }
    }

    @Test
    fun malformedAssociatedConstantsPropagateErrors() {
        for (member in listOf("pub const X: u8 = ;", "#[x] const X: = 1;")) {
            assertTrue(parseStr(ItemParse::parse, "impl S { $member }").isFailure, member)
        }
    }

    @Test
    fun malformedLetInitializersCannotDisappear() {
        for (body in listOf("let x = ;", "let x: u8 = ;", "#[attr] let x = 1 + ;", "let x = value else ;")) {
            val result = parseFile("fn f() { $body }")
            assertTrue(result.isFailure, body)
        }
        val error = assertNotNull(parseFile("pub fn f() {\n    let x = ;\n}").exceptionOrNull())
        assertEquals(2, error.span().start().line)
        assertEquals(12, error.span().start().column)
    }

    @Test
    fun absentAndPresentLetInitializersRemainDistinct() {
        val item = assertIs<Item.Fn>(parseStr(ItemParse::parse, "fn f() { let x; let y = 7; }").getOrThrow())
        val block = assertNotNull(item.block)
        assertEquals(null, assertIs<Stmt.Local>(block.stmts[0]).init)
        assertNotNull(assertIs<Stmt.Local>(block.stmts[1]).init)
    }

    @Test
    fun topLevelConstantsFollowTheSameRequiredInitializerRule() {
        for (source in listOf("const X: u8 = ;", "const X: = 1;")) {
            assertTrue(parseFile(source).isFailure, source)
        }
        for (source in listOf("pub const X: u8 = 1;", "#[kept] pub const _: u8 = 1;")) {
            val item = assertIs<Item.Const>(parseStr(ItemParse::parse, source).getOrThrow())
            assertNotNull(item.expr)
            assertEquals(TokenStream.fromString(source).getOrThrow().toString(), item.toTokenStream().toString())
        }
        for (source in listOf(
            "#[kept] pub const X: u8;",
            "#[kept] pub const X<T>: u8 = 1;",
            "#[kept] const X: u8 = 1 where T: Bound;",
            "const X: u8 = 1 where ;",
        )) {
            val item = assertIs<Item.Verbatim>(parseStr(ItemParse::parse, source).getOrThrow())
            assertEquals(TokenStream.fromString(source).getOrThrow().toString(), item.toTokenStream().toString())
        }
    }

    @Test
    fun closurePrefixesSurviveCopiesVisitorsAndFolds() {
        val source = "for<'a> static move || -> u8 { 1 }"
        val closure = assertIs<Expr.Closure>(parseStr(ExprParse::parse, source).getOrThrow())
        val expected = tokenShape(TokenStream.fromString(source).getOrThrow())
        assertEquals(expected, tokenShape(closure.toTokenStream()))
        for (copy in listOf(closure.deepCopy(), assertIs<Expr.Closure>(closure.clone()))) {
            assertEquals(expected, tokenShape(copy.toTokenStream()))
            assertNotSame(closure.lifetimes, copy.lifetimes)
        }
        val seen = mutableListOf<String>()
        object : Visit() {
            override fun visitLifetime(lt: Lifetime) {
                seen.add(lt.toString())
            }
        }.visitExpr(closure)
        assertEquals(listOf("'a"), seen)
        var mutations = 0
        object : VisitMut() {
            override fun visitBoundLifetimesMut(boundLifetimes: BoundLifetimes) {
                mutations++
            }
        }.visitExprClosureMut(closure)
        assertEquals(1, mutations)
        var folds = 0
        val folded =
            object : Fold() {
                override fun foldBoundLifetimes(boundLifetimes: BoundLifetimes): BoundLifetimes {
                    folds++
                    return super.foldBoundLifetimes(boundLifetimes)
                }
            }.foldExprClosure(closure)
        assertEquals(1, folds)
        assertEquals(expected, tokenShape(folded.toTokenStream()))
    }

    @Test
    fun bracedMatchArmCannotConsumeTheFollowingTuplePattern() {
        val source = "match x { First => { true && match y { _ => true } } (None, _) => false, _ => false }"
        val expr = assertIs<Expr.Match>(parseStr(ExprParse::parse, source).getOrThrow())
        assertEquals(3, expr.arms.size)
        assertIs<Expr.BlockExpr>(expr.arms[0].body)
        assertIs<Pat.Tuple>(expr.arms[1].pat)
        assertEquals(tokenShape(TokenStream.fromString(source).getOrThrow()), tokenShape(expr.toTokenStream()))
        assertTrue(parseStr(ExprParse::parse, "match x { First => value (None, _) => false }").isFailure)
    }

    // Token spacing can change in ToTokens (e.g. the two closure bars); syntax must not.
    private fun tokenShape(tokens: TokenStream): List<String> =
        buildList {
            for (token in tokens) {
                when (token) {
                    is TokenTree.Group -> {
                        add("open:${token.value.delimiter()}")
                        addAll(tokenShape(token.value.stream()))
                        add("close:${token.value.delimiter()}")
                    }
                    is TokenTree.Ident -> add("ident:${token.value}")
                    is TokenTree.Punct -> add("punct:${token.value.asChar()}")
                    is TokenTree.Literal -> add("literal:${token.value}")
                }
            }
        }
}
