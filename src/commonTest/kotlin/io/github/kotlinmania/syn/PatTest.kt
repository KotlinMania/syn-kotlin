// port-lint: tests tests/test_pat.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests for parsing of patterns.
 */
class PatTest {
    @Test
    fun testPatIdent() {
        val pat = assertIs<Pat.Ident>(parsePat("self"))
        assertEquals("self", pat.ident.toString())
    }

    @Test
    fun testPatPath() {
        val pat = assertIs<Pat.Path>(parsePat("self::CONST"))
        assertPath(pat.path, "self", "CONST")
    }

    @Test
    fun testLeadingVert() {
        assertIs<Item.Fn>(parseStr(ItemParse, "fn f() {}").getOrThrow())
        assertTrue(parseStr(ItemParse, "fn fun1(| A: E) {}").isFailure)
        assertTrue(parseStr(ItemParse, "fn fun2(|| A: E) {}").isFailure)

        assertTrue(parseStr(StmtParse, "let | () = ();").isFailure)

        assertSingleLeadingOr(assertIs<Pat.PatParen>(assertLocalType("let (| A): E;").pat).pat)
        assertTrue(parseStr(StmtParse, "let (|| A): (E);").isFailure)

        val tuple = assertIs<Pat.Tuple>(assertLocalType("let (| A,): (E,);").pat)
        assertSingleLeadingOr(tuple.elems.toList().single())

        val slice = assertIs<Pat.Slice>(assertLocalType("let [| A]: [E; 1];").pat)
        assertSingleLeadingOr(slice.elems.toList().single())
        assertTrue(parseStr(StmtParse, "let [|| A]: [E; 1];").isFailure)

        val tupleStruct = assertIs<Pat.TupleStruct>(assertLocalType("let TS(| A): TS;").pat)
        assertSingleLeadingOr(tupleStruct.elems.toList().single())
        assertTrue(parseStr(StmtParse, "let TS(|| A): TS;").isFailure)

        val struct = assertIs<Pat.Struct>(assertLocalType("let NS { f: | A }: NS;").pat)
        assertSingleLeadingOr(struct.fields.toList().single().pat)
        assertTrue(parseStr(StmtParse, "let NS { f: || A }: NS;").isFailure)
    }

    @Test
    fun testGroup() {
        val group = Group(Delimiter.None, TokenStream.fromString("Some(_)").getOrThrow())
        val pat = assertIs<Pat.TupleStruct>(parsePat(TokenStream.fromTokenTrees(listOf(TokenTree.Group(group)))))
        assertPath(pat.path, "Some")
        assertEquals(1, pat.elems.size)
        assertIs<Pat.Wild>(pat.elems.first())
    }

    @Test
    fun testRanges() {
        assertIs<Pat.Rest>(parsePat(".."))

        assertRange(parsePat("..hi"), start = null, end = "hi", closed = false)
        assertRange(parsePat("lo.."), start = "lo", end = null, closed = false)
        assertRange(parsePat("lo..hi"), start = "lo", end = "hi", closed = false)

        assertFailsPat("..=")
        assertRange(parsePat("..=hi"), start = null, end = "hi", closed = true)
        assertFailsPat("lo..=")
        assertRange(parsePat("lo..=hi"), start = "lo", end = "hi", closed = true)

        assertFailsPat("...")
        assertFailsPat("...hi")
        assertFailsPat("lo...")
        assertRange(parsePat("lo...hi"), start = "lo", end = "hi", closed = true)

        assertFailsPat("[lo..]")
        assertFailsPat("[..=hi]")
        assertIs<Pat.Slice>(parsePat("[(lo..)]"))
        assertIs<Pat.Slice>(parsePat("[(..=hi)]"))
        assertIs<Pat.Slice>(parsePat("[lo..=hi]"))

        assertFailsPat("[_, lo.., _]")
        assertFailsPat("[_, ..=hi, _]")
        assertIs<Pat.Slice>(parsePat("[_, (lo..), _]"))
        assertIs<Pat.Slice>(parsePat("[_, (..=hi), _]"))
        assertIs<Pat.Slice>(parsePat("[_, lo..=hi, _]"))
    }

    @Test
    fun testTupleComma() {
        // Empty tuple `()` parses as `Pat.Tuple` with zero elements.
        val empty = parseStr(Pat, "()").getOrThrow()
        assertIs<Pat.Tuple>(empty)
        assertEquals(0, empty.elems.size)

        // A single element with a trailing comma must parse as
        // `Pat.Tuple` (not `Pat.PatParen`); the element is a `Pat.Wild`.
        val oneTrailing = parseStr(Pat, "(_,)").getOrThrow()
        assertIs<Pat.Tuple>(oneTrailing)
        assertEquals(1, oneTrailing.elems.size)
        assertTrue(oneTrailing.elems.trailingPunct())
        assertIs<Pat.Wild>(oneTrailing.elems.first())

        // Two elements without a trailing comma parse as `Pat.Tuple`.
        val two = parseStr(Pat, "(_, _)").getOrThrow()
        assertIs<Pat.Tuple>(two)
        assertEquals(2, two.elems.size)
        val twoList = two.elems.toList()
        assertIs<Pat.Wild>(twoList[0])
        assertIs<Pat.Wild>(twoList[1])

        // Two elements with a trailing comma parse as `Pat.Tuple` and
        // retain the trailing punctuation.
        val twoTrailing = parseStr(Pat, "(_, _,)").getOrThrow()
        assertIs<Pat.Tuple>(twoTrailing)
        assertEquals(2, twoTrailing.elems.size)
        assertTrue(twoTrailing.elems.trailingPunct())

        val tuple = Pat.Tuple(io.github.kotlinmania.syn.token.Paren.default(), PatList())
        assertIs<Pat.Tuple>(roundTrip(tuple))

        val wild = assertIs<Pat.Wild>(parsePat("_"))
        tuple.elems.pushValue(wild)
        val onePrinted = assertIs<Pat.Tuple>(roundTrip(tuple))
        assertEquals(1, onePrinted.elems.size)
        assertTrue(onePrinted.elems.trailingPunct())

        tuple.elems.pushPunct(io.github.kotlinmania.syn.token.Comma.default())
        val oneTrailingPrinted = assertIs<Pat.Tuple>(roundTrip(tuple))
        assertEquals(1, oneTrailingPrinted.elems.size)
        assertTrue(oneTrailingPrinted.elems.trailingPunct())

        tuple.elems.pushValue(wild.deepCopy())
        val twoPrinted = assertIs<Pat.Tuple>(roundTrip(tuple))
        assertEquals(2, twoPrinted.elems.size)
        assertFalse(twoPrinted.elems.trailingPunct())

        tuple.elems.pushPunct(io.github.kotlinmania.syn.token.Comma.default())
        val twoTrailingPrinted = assertIs<Pat.Tuple>(roundTrip(tuple))
        assertEquals(2, twoTrailingPrinted.elems.size)
        assertTrue(twoTrailingPrinted.elems.trailingPunct())
    }

    private fun parsePat(source: String): Pat =
        parseStr(Pat, source).getOrThrow()

    private fun parsePat(tokens: TokenStream): Pat =
        parse2(Pat, tokens).getOrThrow()

    private fun roundTrip(pat: Pat): Pat {
        val tokens = TokenStream.new()
        pat.toTokens(tokens)
        return parsePat(tokens)
    }

    private fun assertFailsPat(source: String) {
        assertTrue(parseStr(Pat, source).isFailure, source)
    }

    private fun assertLocalType(source: String): Pat.TypeAscription {
        val stmt = assertIs<Stmt.Local>(parseStr(StmtParse, source).getOrThrow())
        return assertIs<Pat.TypeAscription>(stmt.pat)
    }

    private fun assertSingleLeadingOr(pat: Pat) {
        val or = assertIs<Pat.Or>(pat)
        assertTrue(or.leadingVert != null)
        val cases = or.cases.toList()
        assertEquals(1, cases.size)
        val ident = assertIs<Pat.Ident>(cases.single())
        assertEquals("A", ident.ident.toString())
    }

    private fun assertRange(
        pat: Pat,
        start: String?,
        end: String?,
        closed: Boolean,
    ) {
        val range = assertIs<Pat.Range>(pat)
        if (closed) {
            assertIs<RangeLimits.Closed>(range.limits)
        } else {
            assertIs<RangeLimits.HalfOpen>(range.limits)
        }
        assertPathExpr(range.start, start)
        assertPathExpr(range.end, end)
    }

    private fun assertPathExpr(expr: Expr?, expected: String?) {
        if (expected == null) {
            assertEquals(null, expr)
            return
        }
        val path = assertIs<Expr.Path>(expr).path
        assertPath(path, expected)
    }

    private fun assertPath(path: Path, vararg segments: String) {
        assertFalse(path.segments.isEmpty())
        assertEquals(segments.toList(), path.segments.toList().map { it.ident.toString() })
    }
}
