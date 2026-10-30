package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.syn.gen.clone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MacroBorrowedTokensTest {
    @Test
    fun borrowedMacroOperationsPreserveTokensAndPositions() {
        val mac = parseStr(Macro::parse, "windows_sys::core::w!(\"hello\")").getOrThrow()
        val before = mac.tokens.toString()
        val start =
            mac.tokens
                .clone()
                .toList()
                .single()
                .span()
                .start()
        repeat(2) {
            assertEquals("hello", mac.parseBodyWith(LitStrParse::parse).getOrThrow().value())
            val output = TokenStream.new()
            mac.toTokens(output)
            val group = assertIs<TokenTree.Group>(output.toList().last()).value
            assertEquals(before, group.stream().toString())
            assertEquals(before, mac.tokens.toString())
            assertEquals(
                start,
                mac.tokens
                    .clone()
                    .toList()
                    .single()
                    .span()
                    .start(),
            )
        }
    }

    @Test
    fun consumingCopiesDoesNotEmptyNestedMacroOriginal() {
        val mac = parseStr(Macro::parse, "m!([one, (two)])").getOrThrow()
        val before = mac.tokens.toString()
        for (copy in listOf(mac.deepCopy(), mac.clone())) {
            copy.tokens.toList()
            assertEquals(before, mac.tokens.toString())
        }
    }

    @Test
    fun allMacroDelimiterFormsRetainOpeningAndClosingSpans() {
        for (source in listOf("m!(\"value\")", "m![\"value\"]", "m!{\"value\"}")) {
            val mac = parseStr(Macro::parse, source).getOrThrow()
            assertEquals(
                2,
                mac
                    .span()
                    .open()
                    .start()
                    .column,
            )
            assertEquals(
                source.length - 1,
                mac
                    .span()
                    .close()
                    .start()
                    .column,
            )
            assertEquals(
                source.length,
                mac
                    .span()
                    .join()
                    .end()
                    .column,
            )
            val output = TokenStream.new()
            mac.toTokens(output)
            val group = assertIs<TokenTree.Group>(output.toList().last()).value
            assertEquals(source.length - 1, group.spanClose().start().column)
            assertEquals(2, group.spanOpen().start().column)
        }
    }
}
