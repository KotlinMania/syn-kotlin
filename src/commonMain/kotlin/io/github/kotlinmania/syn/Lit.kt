// port-lint: source lit.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.quote.toTokens

/** A literal such as a string or integer or boolean. */
public sealed class Lit : ToTokens {
    public data class Str(
        val value: LitStr,
    ) : Lit()

    public data class ByteStr(
        val value: LitByteStr,
    ) : Lit()

    public data class Byte(
        val value: LitByte,
    ) : Lit()

    public data class Char(
        val value: LitChar,
    ) : Lit()

    public data class Int(
        val value: LitInt,
    ) : Lit()

    public data class Float(
        val value: LitFloat,
    ) : Lit()

    public data class Bool(
        val value: LitBool,
    ) : Lit()

    public data class Verbatim(
        val value: Literal,
    ) : Lit()

    public fun span(): Span =
        when (this) {
            is Str -> value.span()
            is ByteStr -> value.span
            is Byte -> value.span
            is Char -> value.span
            is Int -> value.span
            is Float -> value.span
            is Bool -> value.span
            is Verbatim -> value.span()
        }

    public override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Str -> value.toTokens(tokens)
            is ByteStr -> value.toTokens(tokens)
            is Byte -> value.toTokens(tokens)
            is Char -> value.toTokens(tokens)
            is Int -> value.toTokens(tokens)
            is Float -> value.toTokens(tokens)
            is Bool -> value.toTokens(tokens)
            is Verbatim -> value.toTokens(tokens)
        }
    }
}

/** A UTF-8 string literal: `"foo"`. */
public class LitStr private constructor(
    private val literal: Literal,
    private val cooked: String,
) : ToTokens {
    public companion object {
        public fun new(value: String, span: Span): LitStr {
            val token = Literal.string(value)
            token.setSpan(span)
            return LitStr(token, value)
        }
    }

    public fun value(): String =
        cooked

    public fun span(): Span =
        literal.span()

    override fun toTokens(tokens: TokenStream) {
        literal.toTokens(tokens)
    }

    public fun copy(): LitStr =
        new(cooked, span())

    override fun toString(): String = "\"$cooked\""

    override fun equals(other: Any?): Boolean =
        other is LitStr && cooked == other.cooked

    override fun hashCode(): Int = cooked.hashCode()
}

/** A byte string literal: `b"foo"`. */
public class LitByteStr(
    public val bytes: List<UByte>,
    public val span: Span,
) : ToTokens {
    public companion object {
        public fun new(value: List<UByte>, span: Span): LitByteStr =
            LitByteStr(value, span)
    }

    override fun toTokens(tokens: TokenStream) {
        val literal = Literal.string(bytes.map { it.toInt().toChar() }.joinToString(""))
        literal.setSpan(span)
        tokens.append(TokenTree.Literal(literal))
    }

    public fun copy(): LitByteStr = LitByteStr(bytes, span)

    override fun toString(): String = "b\"${bytes.map { it.toInt().toChar() }.joinToString("")}\""
}

/** A byte literal: `bf`. */
public class LitByte(
    public val value: UByte,
    public val suffix: String,
    public val span: Span,
) : ToTokens {
    public companion object {
        public fun new(value: UByte, span: Span): LitByte =
            LitByte(value, "", span)
    }

    override fun toTokens(tokens: TokenStream) {
        val base = Literal.byteCharacter(value).toString()
        val literal = Literal.fromStrUnchecked(base + suffix)
        literal.setSpan(span)
        tokens.append(TokenTree.Literal(literal))
    }

    public fun copy(): LitByte = LitByte(value, suffix, span)

    override fun toString(): String = Literal.byteCharacter(value).toString() + suffix
}

/** A character literal: `a`. */
public class LitChar(
    public val value: Char,
    public val span: Span,
) : ToTokens {
    public companion object {
        public fun new(value: Char, span: Span): LitChar =
            LitChar(value, span)
    }

    override fun toTokens(tokens: TokenStream) {
        val literal = Literal.character(value)
        literal.setSpan(span)
        tokens.append(TokenTree.Literal(literal))
    }

    public fun copy(): LitChar = LitChar(value, span)
}

/** An integer literal: `1` or `1` with an optional suffix like `i32`. */
public class LitInt(
    public val digits: String,
    public val suffix: String,
    public val span: Span,
) : ToTokens {
    public companion object {
        public fun new(digits: String, suffix: String, span: Span): LitInt =
            LitInt(digits, suffix, span)
    }

    public fun base10Digits(): String = digits.replace("_", "")

    public fun base10Parse(): Long = base10Digits().toLong()

    public fun token(): Literal {
        val token = Literal.fromStrUnchecked(digits + suffix)
        token.setSpan(span)
        return token
    }

    override fun toTokens(tokens: TokenStream) {
        val literal = Literal.fromStrUnchecked(digits + suffix)
        literal.setSpan(span)
        tokens.append(TokenTree.Literal(literal))
    }

    public fun copy(): LitInt = LitInt(digits, suffix, span)

    override fun toString(): String = digits + suffix
}

/** A floating point literal: `1.0` or `1f64`. */
public class LitFloat(
    public val digits: String,
    public val suffix: String,
    public val span: Span,
) : ToTokens {
    public companion object {
        public fun new(digits: String, suffix: String, span: Span): LitFloat =
            LitFloat(digits, suffix, span)
    }

    public fun base10Digits(): String = digits.replace("_", "")

    public fun token(): Literal {
        val token = Literal.fromStrUnchecked(digits + suffix)
        token.setSpan(span)
        return token
    }

    override fun toTokens(tokens: TokenStream) {
        val literal = Literal.fromStrUnchecked(digits + suffix)
        literal.setSpan(span)
        tokens.append(TokenTree.Literal(literal))
    }

    public fun copy(): LitFloat = LitFloat(digits, suffix, span)

    override fun toString(): String = digits + suffix
}

/** A boolean literal: `true` or `false`. */
public data class LitBool(
    public val value: Boolean,
    public val span: Span,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        tokens.append(Ident.new(if (value) "true" else "false", span))
    }

    public fun copy(): LitBool = LitBool(value, span)
}

public object LitParse : Parse<Lit> {
    override fun parse(input: ParseStream): SynResult<Lit> =
        input.step { cursor ->
            val identPair = cursor.ident()
            if (identPair != null) {
                val (ident, rest) = identPair
                when (ident.toString()) {
                    "true" -> return@step SynResult.success(Lit.Bool(LitBool(true, ident.span())) to rest)
                    "false" -> return@step SynResult.success(Lit.Bool(LitBool(false, ident.span())) to rest)
                }
            }
            val (lit, rest) =
                cursor.literal()
                    ?: return@step SynResult.failure(cursor.error("expected literal"))
            val span = lit.span()
            val text = lit.toString()
            val result: Lit =
                when {
                    text.startsWith('"') || text.startsWith("r\"") || text.startsWith("r#") ->
                        Lit.Str(LitStr.new(text.removeSurrounding("\""), span))
                    text.startsWith("b'") ->
                        parseLitByte(text, span)?.let { Lit.Byte(it) } ?: Lit.Verbatim(lit)
                    text.startsWith('\'') ->
                        Lit.Char(
                            io.github.kotlinmania.syn
                                .LitChar(text.trim('\'')[0], span),
                        )
                    text.startsWith("b\"") ->
                        Lit.ByteStr(LitByteStr(text.drop(2).dropLast(1).map { it.code.toUByte() }, span))
                    text == "true" ->
                        Lit.Bool(LitBool(true, span))
                    text == "false" ->
                        Lit.Bool(LitBool(false, span))
                    text.contains('.') || text.contains("f32") || text.contains("f64") ->
                        Lit.Float(LitFloat(text, "", span))
                    else ->
                        Lit.Int(LitInt(text, "", span))
                }
            SynResult.success(result to rest)
        }
}

private fun parseLitByte(text: String, span: Span): LitByte? {
    if (!text.startsWith("b'")) return null
    var index = 2
    if (index >= text.length) return null
    val byte =
        if (text[index] == '\\') {
            index += 1
            if (index >= text.length) return null
            when (val escaped = text[index++]) {
                '0' -> 0u
                'n' -> '\n'.code.toUInt()
                'r' -> '\r'.code.toUInt()
                't' -> '\t'.code.toUInt()
                '\'', '"', '\\' -> escaped.code.toUInt()
                'x' -> {
                    if (index + 2 > text.length) return null
                    val value = text.substring(index, index + 2).toUIntOrNull(16) ?: return null
                    index += 2
                    value
                }
                else -> return null
            }
        } else {
            val ch = text[index++]
            if (ch.code > UByte.MAX_VALUE.toInt()) return null
            ch.code.toUInt()
        }
    if (byte > UByte.MAX_VALUE.toUInt()) return null
    if (index >= text.length || text[index] != '\'') return null
    val suffix = text.substring(index + 1)
    return LitByte(byte.toUByte(), suffix, span)
}

public object LitStrParse : Parse<LitStr> {
    override fun parse(input: ParseStream): SynResult<LitStr> {
        val result = LitParse.parse(input)
        if (result is SynResult.Success && result.value is Lit.Str) {
            return SynResult.success(result.value.value)
        }
        return SynResult.failure(input.error("expected string literal"))
    }
}

public object LitIntParse : Parse<LitInt> {
    override fun parse(input: ParseStream): SynResult<LitInt> {
        val result = LitParse.parse(input)
        if (result is SynResult.Success && result.value is Lit.Int) {
            return SynResult.success(result.value.value)
        }
        return SynResult.failure(input.error("expected integer literal"))
    }
}

public object LitFloatParse : Parse<LitFloat> {
    override fun parse(input: ParseStream): SynResult<LitFloat> {
        val result = LitParse.parse(input)
        if (result is SynResult.Success && result.value is Lit.Float) {
            return SynResult.success(result.value.value)
        }
        return SynResult.failure(input.error("expected float literal"))
    }
}

public object LitBoolParse : Parse<LitBool> {
    override fun parse(input: ParseStream): SynResult<LitBool> =
        input.step { cursor ->
            val pair = cursor.ident()
            if (pair != null) {
                val (ident, rest) = pair
                when (ident.toString()) {
                    "true" -> return@step SynResult.success(LitBool(true, ident.span()) to rest)
                    "false" -> return@step SynResult.success(LitBool(false, ident.span()) to rest)
                }
            }
            SynResult.failure(cursor.error("expected `true` or `false`"))
        }
}

public object LitPeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.literal() != null ||
            cursor.ident()?.first?.toString()?.let { it == "true" || it == "false" } == true

    override fun display(): String = "literal"
}
