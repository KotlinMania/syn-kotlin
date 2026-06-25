package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.gen.hash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HashTest {
    @Test
    fun deriveInputHashMatchesGeneratedFieldOrder() {
        val left = parseStr(DeriveInputParse::parse, "struct S<T> where T: Copy { field: T }").getOrThrow()
        val right = parseStr(DeriveInputParse::parse, "struct S<T> where T: Copy { field: T }").getOrThrow()
        val different = parseStr(DeriveInputParse::parse, "struct S<T> where T: Clone { field: T }").getOrThrow()

        assertEquals(left.hash(), right.hash())
        assertNotEquals(left.hash(), different.hash())
    }

    @Test
    fun variantHashesUseRustDiscriminants() {
        assertEquals(AttrStyle.Outer.hash(), AttrStyle.Outer.hash())
        assertNotEquals(
            AttrStyle.Outer.hash(),
            AttrStyle
                .Inner(
                    io.github.kotlinmania.syn.token.Not
                        .default(),
                ).hash(),
        )

        assertEquals(
            BinOp
                .Add(
                    io.github.kotlinmania.syn.token.Plus
                        .default(),
                ).hash(),
            BinOp
                .Add(
                    io.github.kotlinmania.syn.token.Plus
                        .default(),
                ).hash(),
        )
        assertNotEquals(
            BinOp
                .Add(
                    io.github.kotlinmania.syn.token.Plus
                        .default(),
                ).hash(),
            BinOp
                .Sub(
                    io.github.kotlinmania.syn.token.Minus
                        .default(),
                ).hash(),
        )
    }

    @Test
    fun dataHashIncludesVariantTag() {
        val structData = parseStr(DeriveInputParse::parse, "struct S;").getOrThrow().data
        val sameStructData = parseStr(DeriveInputParse::parse, "struct S;").getOrThrow().data
        val enumData = parseStr(DeriveInputParse::parse, "enum S {}").getOrThrow().data

        assertEquals(structData.hash(), sameStructData.hash())
        assertNotEquals(structData.hash(), enumData.hash())
    }
}
