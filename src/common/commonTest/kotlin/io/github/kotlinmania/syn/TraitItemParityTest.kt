package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TraitItemParityTest {
    private fun trait(source: String): Item.Trait =
        assertIs<Item.Trait>(parseStr(ItemParse::parse, source).getOrThrow())

    private fun assertRoundTrip(source: String) {
        assertEquals(
            tokenShape(TokenStream.fromString(source).getOrThrow()),
            tokenShape(trait(source).toTokenStream()),
        )
    }

    @Test
    fun traitConstantsRetainUnderscoresAndOptionalDefaults() {
        val source = "trait T { #[kept] const _: u8; const VALUE: u8 = 7; }"
        val items = trait(source).items
        val unnamed = assertIs<TraitItem.Const>(items[0])
        assertEquals("_", unnamed.ident.toString())
        assertTrue(
            unnamed.attrs
                .single()
                .path()
                .isIdent("kept"),
        )
        assertEquals(null, unnamed.default)
        assertNotNull(assertIs<TraitItem.Const>(items[1]).default)
        assertRoundTrip(source)
    }

    @Test
    fun acceptedUnrepresentedTraitItemsPreserveAllTokens() {
        for (member in listOf(
            "#[kept] const X<A>: u8;",
            "#[kept] const X: u8 = 1 where A: B;",
            "#[kept] pub const X: u8;",
            "#[kept] default const X: u8 = 1;",
            "#[kept] pub fn f();",
            "#[kept] default fn f() {}",
            "#[kept] pub type X;",
        )) {
            val source = "trait T { $member }"
            assertIs<TraitItem.Verbatim>(trait(source).items.single(), source)
            assertRoundTrip(source)
        }
    }

    @Test
    fun traitMacrosRetainDelimitersAttributesAndItemOrder() {
        for (invocation in listOf("m!(x);", "crate::m! { x }", "self::m![x];", "::m!{}")) {
            val source = "trait T { #[kept] $invocation }"
            val item = assertIs<TraitItem.Macro>(trait(source).items.single())
            assertTrue(
                item.attrs
                    .single()
                    .path()
                    .isIdent("kept"),
            )
            assertRoundTrip(source)
        }
        val source = "trait T { const X: u8; #[kept] const fn f() { #![inner] x(); } m!{} type Output; }"
        val items = trait(source).items
        assertEquals(4, items.size)
        assertIs<TraitItem.Const>(items[0])
        val method = assertIs<TraitItem.Fn>(items[1])
        assertNotNull(method.sig.constness)
        assertEquals(listOf("kept", "inner"), method.attrs.map { it.path().getIdent().toString() })
        assertIs<TraitItem.Macro>(items[2])
        assertIs<TraitItem.AssocType>(items[3])
        assertRoundTrip(source)
    }

    @Test
    fun invalidTraitItemsReturnErrorsWithoutDroppingSyntax() {
        for (member in listOf(";", "#[gone] ;", "m!()", "pub m!();", "default!();", "const X: u8 = ;", "const X: = 1;")) {
            assertTrue(parseFile("trait T { $member }").isFailure, member)
        }
    }

    @Test
    fun malformedRestrictedVisibilityCannotBecomeInherited() {
        val result = parseFile("pub(in) fn f() {}")
        assertTrue(result.isFailure)
        val error = assertNotNull(result.exceptionOrNull())
        assertEquals(1, error.span().start().line)
        assertEquals(6, error.span().start().column)
        assertIs<Item.Fn>(parseFile("pub(in crate) fn f() {}").getOrThrow().items.single())
    }

    // ToTokens can separate # and !; retain every token and grouped delimiter.
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
