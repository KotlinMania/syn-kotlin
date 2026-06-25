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
}
