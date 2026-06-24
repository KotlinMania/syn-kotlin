// port-lint: tests tests/test_lit.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LitTest {
    private fun lit(s: String): Lit =
        parseStr(LitParse::parse, s.trim()).getOrThrow()

    private fun litLiteral(s: String): Lit {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(TokenTree.Literal(Literal.fromStrUnchecked(s.trim()))),
            )
        return parse2(LitParse::parse, tokens).getOrThrow()
    }

    @Test
    fun strings() {
        fun testString(s: String, value: String) {
            val parsed = assertIs<Lit.Str>(lit(s))
            assertEquals(value, parsed.value.value())
            val again = TokenStream.new()
            parsed.toTokens(again)
            if (again.toString() != s.trim()) {
                testString(again.toString(), value)
            }
        }

        fun testStringLiteral(s: String, value: String) {
            val parsed = assertIs<Lit.Str>(litLiteral(s))
            assertEquals(value, parsed.value.value())
            val again = TokenStream.new()
            parsed.toTokens(again)
            assertEquals(s.trim(), again.toString())
        }

        testString("\"\"", "")
        testString("\"a\"", "a")
        testString("\"\\n\"", "\n")
        testString("\"\\r\"", "\r")
        testString("\"\\t\"", "\t")
        testString("\"\\\"\"", "\"")
        testString("\"'\"", "'")
        testString("\"\\x41\"", "A")
        testString("\"\\u{2764}\"", "❤")
        testString("\"🐕\"", "🐕")
        testStringLiteral("\"\\u{1F415}\"", "🐕")
        testString("\"\\u{1_2__3_}\"", "\u0123")
        testString(
            "\"contains\nnewlines\\\nescaped newlines\"",
            "contains\nnewlinesescaped newlines",
        )
        testString(
            "\"escaped newline\\\n \u000C unsupported whitespace\"",
            "escaped newline\u000C unsupported whitespace",
        )
        testString("r\"raw\nstring\\\nhere\"", "raw\nstring\\\nhere")
        testString("\"...\"q", "...")
        testString("r\"...\"q", "...")
        testString("r##\"...\"##q", "...")
        testString("r#\"a\\n\"#", "a\\n")
    }

    @Test
    fun byteStrings() {
        fun testByteString(s: String, value: List<UByte>) {
            val parsed = assertIs<Lit.ByteStr>(lit(s))
            assertEquals(value, parsed.value.value())
            val again = TokenStream.new()
            parsed.toTokens(again)
            if (again.toString() != s.trim()) {
                testByteString(again.toString(), value)
            }
        }

        fun testByteStringLiteral(s: String, value: List<UByte>) {
            val parsed = assertIs<Lit.ByteStr>(litLiteral(s))
            assertEquals(value, parsed.value.value())
            val again = TokenStream.new()
            parsed.toTokens(again)
            if (again.toString() != s.trim()) {
                testByteStringLiteral(again.toString(), value)
            }
        }

        testByteString("b\"\"", emptyList())
        testByteString("b\"a\"", listOf('a'.code.toUByte()))
        testByteString("b\"\\n\"", listOf('\n'.code.toUByte()))
        testByteString("b\"\\r\"", listOf('\r'.code.toUByte()))
        testByteString("b\"\\t\"", listOf('\t'.code.toUByte()))
        testByteString("b\"\\\"\"", listOf('"'.code.toUByte()))
        testByteString("b\"'\"", listOf('\''.code.toUByte()))
        testByteString("b\"\\x41\"", listOf('A'.code.toUByte()))
        testByteStringLiteral(
            "b\"contains\nnewlines\\\nescaped newlines\"",
            "contains\nnewlinesescaped newlines".encodeToByteArray().map { it.toUByte() },
        )
        testByteString(
            "br\"raw\nstring\\\nhere\"",
            "raw\nstring\\\nhere".encodeToByteArray().map { it.toUByte() },
        )
        testByteString("b\"...\"q", "...".encodeToByteArray().map { it.toUByte() })
        testByteString("br\"...\"q", "...".encodeToByteArray().map { it.toUByte() })
        testByteString("br##\"...\"##q", "...".encodeToByteArray().map { it.toUByte() })
        testByteString("br#\"a\\n\"#", listOf('a'.code.toUByte(), '\\'.code.toUByte(), 'n'.code.toUByte()))
    }

    @Test
    fun cStrings() {
        fun testCString(s: String, value: ByteArray) {
            val parsed = assertIs<Lit.CStr>(lit(s))
            assertTrue(value.contentEquals(parsed.value.value()))
            val again = TokenStream.new()
            parsed.toTokens(again)
            if (again.toString() != s.trim()) {
                testCString(again.toString(), value)
            }
        }

        fun testCStringLiteral(s: String, value: ByteArray) {
            val parsed = assertIs<Lit.CStr>(litLiteral(s))
            assertTrue(value.contentEquals(parsed.value.value()))
            val again = TokenStream.new()
            parsed.toTokens(again)
            assertEquals(s.trim(), again.toString())
        }

        testCString("c\"\"", byteArrayOf())
        testCString("c\"a\"", byteArrayOf('a'.code.toByte()))
        testCString("c\"\\n\"", byteArrayOf('\n'.code.toByte()))
        testCString("c\"\\r\"", byteArrayOf('\r'.code.toByte()))
        testCString("c\"\\t\"", byteArrayOf('\t'.code.toByte()))
        testCString("c\"\\\\\"", byteArrayOf('\\'.code.toByte()))
        testCString("c\"\\'\"", byteArrayOf('\''.code.toByte()))
        testCString("c\"\\\"\"", byteArrayOf('"'.code.toByte()))
        testCString("c\"\\x41\"", byteArrayOf('A'.code.toByte()))
        testCString("c\"\\u{2764}\"", "❤".encodeToByteArray())
        testCString(
            "c\"contains\nnewlines\\\nescaped newlines\"",
            "contains\nnewlinesescaped newlines".encodeToByteArray(),
        )
        testCString("cr\"raw\nstring\\\nhere\"", "raw\nstring\\\nhere".encodeToByteArray())
        testCString("c\"...\"q", "...".encodeToByteArray())
        testCString("cr\"...\"", "...".encodeToByteArray())
        testCString("cr##\"...\"##", "...".encodeToByteArray())
        testCStringLiteral(
            "c\"hello\\x80我叫\\u{1F980}\"",
            byteArrayOf(
                'h'.code.toByte(),
                'e'.code.toByte(),
                'l'.code.toByte(),
                'l'.code.toByte(),
                'o'.code.toByte(),
                0x80.toByte(),
            ) + "我叫🦀".encodeToByteArray(),
        )
        testCString("cr#\"a\\n\"#", "a\\n".encodeToByteArray())
    }

    @Test
    fun bytes() {
        fun testByte(s: String, value: UByte) {
            val parsed = assertIs<Lit.Byte>(lit(s))
            assertEquals(value, parsed.value.value())
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
        fun testChar(s: String, value: Int) {
            val parsed = assertIs<Lit.Char>(lit(s))
            assertEquals(value, parsed.value.value())
            val again = TokenStream.new()
            parsed.toTokens(again)
            assertEquals(s.trim(), again.toString())
        }

        fun testCharLiteral(s: String, value: Int) {
            val parsed = assertIs<Lit.Char>(litLiteral(s))
            assertEquals(value, parsed.value.value())
            val again = TokenStream.new()
            parsed.toTokens(again)
            assertEquals(s.trim(), again.toString())
        }

        testChar("'a'", 'a'.code)
        testChar("'\\n'", '\n'.code)
        testChar("'\\r'", '\r'.code)
        testChar("'\\t'", '\t'.code)
        testChar("'\\''", '\''.code)
        testChar("'\"'", '"'.code)
        testChar("'\\x41'", 'A'.code)
        testChar("'\\u{2764}'", '❤'.code)
        testCharLiteral("'🐕'", 0x1f415)
        testCharLiteral("'\\u{1F415}'", 0x1f415)
    }

    @Test
    fun ints() {
        fun testInt(s: String, value: Long, suffix: String) {
            val parsed = assertIs<Lit.Int>(lit(s))
            assertEquals(value, parsed.value.base10Digits().toLong())
            assertEquals(suffix, parsed.value.suffix())
            val again = TokenStream.new()
            parsed.toTokens(again)
            if (again.toString() != s) {
                testInt(again.toString(), value, suffix)
            }
        }

        testInt("5", 5, "")
        testInt("5u32", 5, "u32")
        testInt("0E", 0, "E")
        testInt("0ECMA", 0, "ECMA")
        testInt("0o0A", 0, "A")
        testInt("5_0", 50, "")
        testInt("5_____0_____", 50, "")
        testInt("0x7f", 127, "")
        testInt("0x7F", 127, "")
        testInt("0b1001", 9, "")
        testInt("0o73", 59, "")
        testInt("0x7Fu8", 127, "u8")
        testInt("0b1001i8", 9, "i8")
        testInt("0o73u32", 59, "u32")
        testInt("0x__7___f_", 127, "")
        testInt("0x__7___F_", 127, "")
        testInt("0b_1_0__01", 9, "")
        testInt("0o_7__3", 59, "")
        testInt("0x_7F__u8", 127, "u8")
        testInt("0b__10__0_1i8", 9, "i8")
        testInt("0o__7__________________3u32", 59, "u32")

        val unicodeSuffix = LitInt.new("0", "e1\u05c5", Span.callSite())
        assertEquals("0", unicodeSuffix.base10Digits())
        assertEquals("e1\u05c5", unicodeSuffix.suffix())
    }

    @Test
    fun floats() {
        fun testFloat(s: String, value: Double, suffix: String) {
            val parsed = assertIs<Lit.Float>(lit(s))
            assertEquals(value, parsed.value.base10Digits().toDouble())
            assertEquals(suffix, parsed.value.suffix())
            val again = TokenStream.new()
            parsed.toTokens(again)
            if (again.toString() != s) {
                testFloat(again.toString(), value, suffix)
            }
        }

        testFloat("5.5", 5.5, "")
        testFloat("5.5E12", 5.5e12, "")
        testFloat("5.5e12", 5.5e12, "")
        testFloat("1.0__3e-12", 1.03e-12, "")
        testFloat("1.03e+12", 1.03e12, "")
        testFloat("9e99e99", 9e99, "e99")
        testFloat("1e_0", 1.0, "")
        testFloat("0.0ECMA", 0.0, "ECMA")
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

        assertEquals("-1", assertIs<Lit.Int>(lit("-1")).value.base10Digits())
        assertEquals("-1.5", assertIs<Lit.Float>(lit("-1.5")).value.base10Digits())
    }

    @Test
    fun suffix() {
        fun getSuffix(token: String): String =
            when (val parsed = lit(token)) {
                is Lit.Str -> parsed.value.suffix()
                is Lit.ByteStr -> parsed.value.suffix()
                is Lit.CStr -> parsed.value.suffix()
                is Lit.Byte -> parsed.value.suffix()
                is Lit.Char -> parsed.value.suffix()
                is Lit.Int -> parsed.value.suffix()
                is Lit.Float -> parsed.value.suffix()
                else -> error("unimplemented literal suffix branch")
            }

        assertEquals("s", getSuffix("\"\"s"))
        assertEquals("r", getSuffix("r\"\"r"))
        assertEquals("r", getSuffix("r#\"\"#r"))
        assertEquals("b", getSuffix("b\"\"b"))
        assertEquals("br", getSuffix("br\"\"br"))
        assertEquals("br", getSuffix("br#\"\"#br"))
        assertEquals("c", getSuffix("c\"\"c"))
        assertEquals("cr", getSuffix("cr\"\"cr"))
        assertEquals("cr", getSuffix("cr#\"\"#cr"))
        assertEquals("c", getSuffix("'c'c"))
        assertEquals("b", getSuffix("b'b'b"))
        assertEquals("i32", getSuffix("1i32"))
        assertEquals("i32", getSuffix("1_i32"))
        assertEquals("f32", getSuffix("1.0f32"))
        assertEquals("f32", getSuffix("1.0_f32"))
    }

    @Test
    fun testDeepGroupEmpty() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(
                        Group(
                            Delimiter.None,
                            TokenStream.fromTokenTrees(
                                listOf(
                                    TokenTree.Group(
                                        Group(
                                            Delimiter.None,
                                            TokenStream.fromTokenTrees(
                                                listOf(TokenTree.Literal(Literal.string("hi"))),
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            )

        val parsed = assertIs<Lit.Str>(parse2(LitParse::parse, tokens).getOrThrow())
        val emitted = TokenStream.new()
        parsed.toTokens(emitted)
        assertEquals("\"hi\"", emitted.toString())
    }

    @Test
    fun litPeekAcceptsNegativeLiteral() {
        val parser =
            parser@ { input: ParseStream ->
                assertTrue(input.peek(LitPeek))
                LitParse.parse(input)
            }
        assertIs<Lit.Int>(parseStr(parser, "-1").getOrThrow())
    }

    @Test
    fun testError() {
        // Parsing "..." as a LitStr fails because it is not a valid string literal.
        val first = parseStr(LitStrParse::parse, "...")
        assertTrue(first.isFailure)

        // Parsing "5" as a LitStr fails because the lexer produces an integer.
        val second = parseStr(LitStrParse::parse, "5")
        assertTrue(second.isFailure)
    }

    @Test
    fun litStrParseWith() {
        val lit = LitStr.new("a::b::c", Span.mixedSite())

        val modStyle = lit.parseWith { input -> Path.parseModStyle(input) }.getOrThrow()
        assertEquals(listOf("a", "b", "c"), modStyle.segments.toList().map { it.ident.toString() })

        val defaultPath = lit.parseWith(PathParse::parse).getOrThrow()
        assertEquals(listOf("a", "b", "c"), defaultPath.segments.toList().map { it.ident.toString() })
    }

    @Test
    fun setSpan() {
        val span = Span.mixedSite()

        val litStr = LitStr.new("value", Span.callSite())
        litStr.setSpan(span)
        assertEquals(span, litStr.span())
        assertEquals(span, litStr.token().span())

        val lit = Lit.Int(LitInt.new("1", "", Span.callSite()))
        lit.setSpan(span)
        assertEquals(span, lit.span())
        assertEquals(span, lit.value.token().span())
    }
}
