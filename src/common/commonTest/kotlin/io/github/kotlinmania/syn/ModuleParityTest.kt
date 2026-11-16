package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ModuleParityTest {
    @Test
    fun moduleErrorsRetainTheirOriginalLocationInsteadOfTheFollowingItem() {
        val source = "mod m {\nfn broken() { let x = ; }\nfn next() {}\n}"
        val result = parseFile(source)
        assertTrue(result.isFailure)
        val error = assertNotNull(result.exceptionOrNull())
        assertEquals(2, error.span().start().line)
        assertEquals(22, error.span().start().column)
    }

    @Test
    fun innerAttributesBelongToTheirModuleAndRoundTripInsideItsBraces() {
        val source = "#[outer] pub mod m { #![inner] //! module docs\n fn f() {} mod n { #![nested] struct S; } }"
        val file = parseFile(source).getOrThrow()
        val module = assertIs<Item.Mod>(file.items.single())
        assertEquals(listOf("outer", "inner", "doc"), module.attrs.map { it.path().getIdent().toString() })
        assertIs<AttrStyle.Outer>(module.attrs[0].style)
        assertIs<AttrStyle.Inner>(module.attrs[1].style)
        val content = assertIs<ModContent.Inline>(module.content)
        assertEquals(2, content.items.size)
        assertIs<Item.Fn>(content.items[0])
        assertEquals(
            "nested",
            assertIs<Item.Mod>(content.items[1])
                .attrs
                .single()
                .path()
                .getIdent()
                .toString(),
        )
        assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(file.toTokenStream()))
    }

    @Test
    fun moduleFormsFollowRustSynAndMalformedBodiesReturnFailures() {
        for (source in listOf("mod m;", "pub unsafe mod m {}", "mod try {}", "mod r#type {}")) {
            val file = parseFile(source).getOrThrow()
            assertIs<Item.Mod>(file.items.single())
            assertEquals(shape(TokenStream.fromString(source).getOrThrow()), shape(file.toTokenStream()))
        }
        for (source in listOf("mod m", "mod m()", "mod m { let x = 1; }", "mod m { #![x] fn broken( }")) {
            assertTrue(parseFile(source).isFailure, source)
        }
    }

    private fun shape(tokens: TokenStream): List<String> =
        buildList {
            for (token in tokens) {
                when (token) {
                    is TokenTree.Group -> {
                        add("open:${token.value.delimiter()}")
                        addAll(shape(token.value.stream()))
                        add("close:${token.value.delimiter()}")
                    }
                    is TokenTree.Ident -> add("ident:${token.value}")
                    is TokenTree.Punct -> add("punct:${token.value.asChar()}")
                    is TokenTree.Literal -> add("literal:${token.value}")
                }
            }
        }
}
