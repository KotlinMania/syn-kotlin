// port-lint: tests tests/test_parse_quote.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.Or
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for `parseQuote`-style construction of syntax tree nodes from
 * token streams.
 *
 * The upstream Rust tests use the `parse_quote!` macro, which expands to a
 * type-inferred call into the `Parse` / `ParseQuote` machinery. In Kotlin,
 * the equivalent is [parseQuote] paired with a [ParseQuote] implementation
 * for the target type, or [parseStr] when a default [Parse] exists.
 *
 * The Kotlin equivalent is [parseQuote] paired with explicit [ParseQuote]
 * adapters for the target type.
 */
class ParseQuoteTest {
    private fun tokens(source: String): TokenStream =
        TokenStream.fromString(source).getOrThrow()

    private fun <T> parseQuote(source: String, parser: ParseQuote<T>): T =
        parseQuote(tokens(source), parser)

    private fun parseLitOrPunctuated(source: String): Punctuated<Lit, Or> =
        parserFromFunction { input ->
            Punctuated.parseTerminatedWith(
                input,
                { stream -> stream.parse(LitParse) },
                OrParse,
            )
        }.parse2(tokens(source)).getOrThrow()

    @Test
    fun testAttribute() {
        val outer = parseQuote("#[test]", AttributeParseQuote)
        assertIs<AttrStyle.Outer>(outer.style)
        val outerMeta = assertIs<Meta.PathMeta>(outer.meta)
        assertEquals("test", outerMeta.path.toString())

        val inner = parseQuote("#![no_std]", AttributeParseQuote)
        assertIs<AttrStyle.Inner>(inner.style)
        val innerMeta = assertIs<Meta.PathMeta>(inner.meta)
        assertEquals("no_std", innerMeta.path.toString())
    }

    @Test
    fun testField() {
        val named = parseQuote("pub enabled: bool", FieldParseQuote)
        assertIs<Visibility.Public>(named.vis)
        assertEquals("enabled", named.ident?.toString())
        assertNotNull(named.colonToken)
        val namedTy = assertIs<SynType.Path>(named.ty)
        assertEquals(listOf("bool"), namedTy.path.segments.toList().map { it.ident.toString() })

        val unnamed = parseQuote("primitive::bool", FieldParseQuote)
        assertIs<Visibility.Inherited>(unnamed.vis)
        assertNull(unnamed.ident)
        assertNull(unnamed.colonToken)
        val unnamedTy = assertIs<SynType.Path>(unnamed.ty)
        assertEquals(listOf("primitive", "bool"), unnamedTy.path.segments.toList().map { it.ident.toString() })
    }

    @Test
    fun testPat() {
        val pat = assertIs<Pat.Or>(parseQuote("Some(false) | None", PatParseQuote))
        assertNull(pat.leadingVert)
        val cases = pat.cases.toList()
        assertEquals(2, cases.size)

        val some = assertIs<Pat.TupleStruct>(cases[0])
        assertEquals("Some", some.path.toString())
        val lit = assertIs<Pat.Lit>(some.elems.single())
        assertEquals(false, assertIs<Lit.Bool>(lit.lit).value.value())

        val none = assertIs<Pat.Ident>(cases[1])
        assertEquals("None", none.ident.toString())
    }

    @Test
    fun testPunctuated() {
        val punctuated = parseLitOrPunctuated("true | true")
        assertEquals(2, punctuated.len())
        assertEquals(false, punctuated.trailingPunct())
        assertTrue(punctuated.toList().all { assertIs<Lit.Bool>(it).value.value() })
        assertIs<Or>(punctuated.punct(0))
    }

    @Test
    fun testPunctuatedTrailing() {
        val punctuated = parseLitOrPunctuated("true | true |")
        assertEquals(2, punctuated.len())
        assertEquals(true, punctuated.trailingPunct())
        assertTrue(punctuated.toList().all { assertIs<Lit.Bool>(it).value.value() })
        assertIs<Or>(punctuated.punct(0))
        assertIs<Or>(punctuated.punct(1))
    }

    @Test
    fun testVecStmt() {
        val stmts = parseQuote("let _; true", StmtListParseQuote)
        assertEquals(2, stmts.size)

        val local = assertIs<Stmt.Local>(stmts[0])
        assertIs<Pat.Wild>(local.pat)
        assertNull(local.init)

        val expr = assertIs<Stmt.ExprStmt>(stmts[1])
        val lit = assertIs<Expr.Lit>(expr.expr)
        assertEquals(true, assertIs<Lit.Bool>(lit.lit).value.value())
        assertNull(expr.semiToken)
    }
}
