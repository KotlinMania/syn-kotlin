package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.gen.debug
import io.github.kotlinmania.syn.token.Extern
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Not
import io.github.kotlinmania.syn.token.Plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DebugTest {
    @Test
    fun deriveInputDebugMatchesGeneratedFieldOrder() {
        val item = parseStr(DeriveInputParse::parse, "#[repr(C)] pub struct S<T> where T: Copy { field: T }").getOrThrow()
        val rendered = item.debug()

        assertTrue(rendered.startsWith("DeriveInput { "))
        assertInOrder(rendered, "attrs:", "vis:", "ident:", "generics:", "data:")
        assertTrue(rendered.contains("Attribute { pound_token:"))
        assertTrue(rendered.contains("data: Data::Struct { struct_token:"))
    }

    @Test
    fun tokenFieldsIncludedWhereGeneratedDebugIncludesThem() {
        assertEquals("Abi { extern_token: Extern, name: None }", Abi(Extern.default(), null).debug())

        val arguments = PathArguments.AngleBracketed(null, Lt.default(), GenericArgumentList(), Gt.default())
        val rendered = arguments.debug()

        assertTrue(rendered.startsWith("AngleBracketedGenericArguments { "))
        assertInOrder(rendered, "colon2_token:", "lt_token:", "args:", "gt_token:")
        assertTrue(rendered.contains("colon2_token: None"))
        assertTrue(rendered.contains("lt_token: Lt"))
        assertTrue(rendered.contains("gt_token: Gt"))
    }

    @Test
    fun enumDebugUsesRustVariantPrefixes() {
        assertEquals("AttrStyle::Outer", AttrStyle.Outer.debug())
        assertEquals("AttrStyle::Inner(Not)", AttrStyle.Inner(Not.default()).debug())
        assertEquals("BinOp::Add(Plus)", BinOp.Add(Plus.default()).debug())

        val data = parseStr(DeriveInputParse::parse, "struct S;").getOrThrow().data
        assertTrue(data.debug().startsWith("Data::Struct { "))
    }

    private fun assertInOrder(
        text: String,
        vararg fragments: String,
    ) {
        var previous = -1
        for (fragment in fragments) {
            val index = text.indexOf(fragment)
            assertTrue(index >= 0, "missing fragment: $fragment in $text")
            assertTrue(index > previous, "fragment out of order: $fragment in $text")
            previous = index
        }
    }
}
