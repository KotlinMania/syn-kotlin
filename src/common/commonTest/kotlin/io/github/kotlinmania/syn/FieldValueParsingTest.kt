package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FieldValueParsingTest {
    @Test
    fun outerAttributesAndAllMemberFormsRoundTrip() {
        val source = """S { #[cfg(feature = "x")] named: value, #[allow(unused)] shorthand, #[cfg(feature = "y")] 0: next, }"""
        val original = TokenStream.fromString(source).getOrThrow()
        val expr = assertIs<Expr.Struct>(parse2(ExprParse::parse, original.clone()).getOrThrow())
        val fields = expr.fields.toList()
        assertEquals(3, fields.size)
        assertEquals(listOf("cfg", "allow", "cfg"), fields.map { it.attrs.single().path().segments.toList().single().ident.toString() })
        assertEquals("named", assertIs<Member.Named>(fields[0].member).ident.toString())
        assertNotNull(fields[0].colonToken)
        assertNull(fields[1].colonToken)
        assertEquals("shorthand", assertIs<Expr.Path>(fields[1].expr).path.segments.toList().single().ident.toString())
        assertEquals(0u, assertIs<Member.Unnamed>(fields[2].member).index.index)
        assertEquals(":", assertNotNull(fields[2].colonToken).span.sourceText())
        for (value in listOf(expr, expr.deepCopy())) {
            val output = TokenStream.new()
            value.toTokens(output)
            assertEquals(original.toString(), output.toString())
            assertEquals(3, assertIs<Expr.Struct>(parse2(ExprParse::parse, output).getOrThrow()).fields.len())
        }
    }

    @Test
    fun jsonReaderInitializerKeepsConfiguredFields() {
        val source = """Deserializer {
            read,
            scratch: Vec::new(),
            remaining_depth: 128,
            #[cfg(feature = "float_roundtrip")]
            single_precision: false,
            #[cfg(feature = "unbounded_depth")]
            disable_recursion_limit: false,
        }"""
        val fields = assertIs<Expr.Struct>(parseStr(ExprParse::parse, source).getOrThrow()).fields.toList()
        assertEquals(5, fields.size)
        assertEquals(listOf(0, 0, 0, 1, 1), fields.map { it.attrs.size })
        assertEquals(listOf("read", "scratch", "remaining_depth", "single_precision", "disable_recursion_limit"), fields.map { assertIs<Member.Named>(it.member).ident.toString() })
    }

    @Test
    fun numericMembersRequireValuesAndInvalidAttributesRemainErrors() {
        for (source in listOf("S { 0 }", "S { #[cfg(x)] 0 }", "S { #![cfg(x)] value: 1 }", "S { #[cfg(x)] value: }")) {
            assertTrue(parseStr(ExprParse::parse, source).isFailure, source)
        }
    }
}
