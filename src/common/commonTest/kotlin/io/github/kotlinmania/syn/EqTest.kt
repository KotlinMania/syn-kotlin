package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.gen.eq
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EqTest {
    @Test
    fun deriveInputEqMatchesGeneratedFieldSet() {
        val left = parseStr(DeriveInputParse::parse, "struct S<T> where T: Copy { field: T }").getOrThrow()
        val right = parseStr(DeriveInputParse::parse, "struct S<T> where T: Copy { field: T }").getOrThrow()
        val different = parseStr(DeriveInputParse::parse, "struct S<T> where T: Clone { field: T }").getOrThrow()

        assertTrue(left.eq(right))
        assertFalse(left.eq(different))
    }

    @Test
    fun attrStyleAndBinOpEqCompareVariants() {
        assertTrue(AttrStyle.Outer.eq(AttrStyle.Outer))
        assertTrue(
            AttrStyle
                .Inner(
                    io.github.kotlinmania.syn.token.Not
                        .default(),
                ).eq(
                    AttrStyle.Inner(
                        io.github.kotlinmania.syn.token.Not
                            .default(),
                    ),
                ),
        )
        assertFalse(
            AttrStyle.Outer.eq(
                AttrStyle.Inner(
                    io.github.kotlinmania.syn.token.Not
                        .default(),
                ),
            ),
        )

        assertTrue(
            BinOp
                .Add(
                    io.github.kotlinmania.syn.token.Plus
                        .default(),
                ).eq(
                    BinOp.Add(
                        io.github.kotlinmania.syn.token.Plus
                            .default(),
                    ),
                ),
        )
        assertFalse(
            BinOp
                .Add(
                    io.github.kotlinmania.syn.token.Plus
                        .default(),
                ).eq(
                    BinOp.Sub(
                        io.github.kotlinmania.syn.token.Minus
                            .default(),
                    ),
                ),
        )
    }

    @Test
    fun dataEqDispatchesByVariant() {
        val left = parseStr(DeriveInputParse::parse, "struct S;").getOrThrow().data
        val right = parseStr(DeriveInputParse::parse, "struct S;").getOrThrow().data
        val different = parseStr(DeriveInputParse::parse, "enum S {}").getOrThrow().data

        assertTrue(left.eq(right))
        assertFalse(left.eq(different))
    }

    @Test
    fun exprItemPatAndTypeEqIgnoreSpansAndTokenPositions() {
        val exprLeft = parseStr(ExprParse::parse, "(a, b).0 + m!(1 + 2)").getOrThrow()
        val exprRight = parseStr(ExprParse::parse, "\n  ( a , b ) . 0  +  m! ( 1 + 2 ) ").getOrThrow()
        val exprDiff = parseStr(ExprParse::parse, "(a, b).1 + m!(1 + 2)").getOrThrow()
        assertTrue(exprLeft.eq(exprRight))
        assertFalse(exprLeft.eq(exprDiff))

        val itemLeft = parseStr(ItemParse::parse, "pub fn f<const N: usize>(x: &[u8; N]) -> bool { true }").getOrThrow()
        val itemRight = parseStr(ItemParse::parse, "\npub  fn  f < const N : usize > ( x : & [ u8 ; N ] ) -> bool { true }").getOrThrow()
        val itemDiff = parseStr(ItemParse::parse, "pub fn f<const N: usize>(x: &[u8; N]) -> bool { false }").getOrThrow()
        assertTrue(itemLeft.eq(itemRight))
        assertFalse(itemLeft.eq(itemDiff))

        val patLeft = parseStr(Pat::parseSingle, "Some(ref mut x @ 1..=5)").getOrThrow()
        val patRight = parseStr(Pat::parseSingle, "  Some ( ref  mut  x  @  1 ..= 5 ) ").getOrThrow()
        assertTrue(patLeft.eq(patRight))
    }
}
