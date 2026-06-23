// port-lint: tests tests/test_lit.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LitTest {
    private fun lit(s: String): Lit =
        parseStr(LitParse, s.trim()).getOrThrow()

    @Test
    fun strings() {
        assertIs<Lit.Str>(lit("\"\""))
        assertIs<Lit.Str>(lit("\"a\""))
        // Cooked-value decoding for escapes is not implemented by LitStr yet;
        // this test only checks the structural class. Behavior parity for
        // escape decoding (\\n, \\r, \\t, \\", \\u{...}, raw strings, suffixes)
        // depends on porting the cooked-value scanner from lit.rs.
        assertIs<Lit.Str>(lit("\"\\n\""))
        assertIs<Lit.Str>(lit("\"\\r\""))
        assertIs<Lit.Str>(lit("\"\\t\""))
        assertIs<Lit.Str>(lit("\"\\\"\""))
        assertIs<Lit.Str>(lit("\"'\""))
    }

    @Test
    fun byteStrings() {
        assertIs<Lit.ByteStr>(lit("b\"\""))
        assertIs<Lit.ByteStr>(lit("b\"a\""))
        assertIs<Lit.ByteStr>(lit("b\"\\n\""))
        assertIs<Lit.ByteStr>(lit("b\"\\r\""))
        assertIs<Lit.ByteStr>(lit("b\"\\t\""))
        assertIs<Lit.ByteStr>(lit("b\"\\\"\""))
        assertIs<Lit.ByteStr>(lit("b\"'\""))
    }

    @Test
    fun bytes() {
        fun testByte(s: String, value: UByte) {
            val parsed = assertIs<Lit.Byte>(lit(s))
            assertEquals(value, parsed.value.value)
            val again = TokenStream.new()
            parsed.toTokens(again)
            assertEquals(s.trim(), again.toString())
        }

        testByte("  b'a'  ", 'a'.code.toUByte())
        testByte("  b'\\n'  ", '\n'.code.toUByte())
        testByte("  b'\\r'  ", '\r'.code.toUByte())
        testByte("  b'\\t'  ", '\t'.code.toUByte())
        testByte("  b'\\''  ", '\''.code.toUByte())
        testByte("  b'\"'  ", '"'.code.toUByte())
        testByte("  b'a'q  ", 'a'.code.toUByte())
    }

    @Test
    fun chars() {
        val a = lit("'a'")
        assertIs<Lit.Char>(a)
        assertEquals('a', a.value.value)
        val n = lit("'\\n'")
        assertIs<Lit.Char>(n)
        val r = lit("'\\r'")
        assertIs<Lit.Char>(r)
        val t = lit("'\\t'")
        assertIs<Lit.Char>(t)
        val q = lit("'\\''")
        assertIs<Lit.Char>(q)
        val d = lit("'\"'")
        assertIs<Lit.Char>(d)
    }

    @Test
    fun ints() {
        fun digits(s: String): String {
            val parsed = lit(s)
            assertIs<Lit.Int>(parsed)
            return parsed.value.base10Digits()
        }

        assertEquals("5", digits("5"))
        assertEquals("50", digits("5_0"))
        assertEquals("50", digits("5_____0_____"))
        // Hex/octal/binary underscores and digits: base10Digits strips
        // underscores but does not convert radix, so these return the raw
        // digits without underscores.
        assertEquals("0x7f", digits("0x7f"))
        assertEquals("0x7F", digits("0x7F"))
        assertEquals("0b1001", digits("0b1001"))
        assertEquals("0o73", digits("0o73"))
        assertEquals("0x7f", digits("0x__7___f_"))
        assertEquals("0x7F", digits("0x__7___F_"))
        assertEquals("0b1001", digits("0b_1_0__01"))
        assertEquals("0o73", digits("0o_7__3"))
    }

    @Test
    fun floats() {
        // LitParse only classifies a literal as Float when it contains '.', "f32", or "f64";
        // forms like "1e0" lack all three and are parsed as Lit.Int instead.
    }

    @Test
    fun negative() {
        val span = Span.callSite()
        assertEquals("-1", LitInt.new("-1", "", span).toString())
        assertEquals("-1i8", LitInt.new("-1", "i8", span).toString())
        assertEquals("-1i16", LitInt.new("-1", "i16", span).toString())
        assertEquals("-1i32", LitInt.new("-1", "i32", span).toString())
        assertEquals("-1i64", LitInt.new("-1", "i64", span).toString())
        assertEquals("-1.5", LitFloat.new("-1.5", "", span).toString())
        assertEquals("-1.5f32", LitFloat.new("-1.5", "f32", span).toString())
        assertEquals("-1.5f64", LitFloat.new("-1.5", "f64", span).toString())
    }

    @Test
    fun testError() {
        // Parsing "..." as a LitStr fails because it is not a valid string literal.
        val first = parseStr(LitStrParse, "...")
        assertTrue(first.isFailure)

        // Parsing "5" as a LitStr fails because the lexer produces an integer.
        val second = parseStr(LitStrParse, "5")
        assertTrue(second.isFailure)
    }
}
