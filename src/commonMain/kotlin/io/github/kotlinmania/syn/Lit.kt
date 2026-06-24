// port-lint: source lit.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.quote.toTokens

/** A literal such as a string or integer or boolean. */
public sealed class Lit : ToTokens {
    public companion object {
        /** Interpret a literal token as a Syn literal. */
        public fun new(token: Literal): Lit =
            fromStr(token, token.toString())

        public fun fromStrForFuzzing(repr: String): Lit =
            fromStr(Literal.u8Unsuffixed(0u), repr)

        private fun fromStr(token: Literal, repr: String): Lit {
            when (byte(repr, 0)) {
                '"'.code,
                'r'.code,
                -> parseLitStr(repr)?.let { return Str(LitStr.fromLiteral(token, it.value, it.suffix)) }
                'b'.code -> {
                    when (byte(repr, 1)) {
                        '"'.code,
                        'r'.code,
                        -> parseLitByteStr(repr)?.let { return ByteStr(LitByteStr(it.value, token.span(), it.suffix, token)) }
                        '\''.code -> parseLitByteParts(repr)?.let { return Byte(LitByte(it.value, it.suffix, token.span(), token)) }
                    }
                }
                'c'.code -> {
                    when (byte(repr, 1)) {
                        '"'.code,
                        'r'.code,
                        -> parseLitCStr(repr)?.let { return CStr(LitCStr(it.value, token.span(), it.suffix, token)) }
                    }
                }
                '\''.code -> parseLitChar(repr)?.let { return Char(LitChar(it.value, token.span(), it.suffix, token)) }
                in '0'.code..'9'.code,
                '-'.code,
                -> {
                    parseLitInt(repr)?.let { return Int(LitInt.from(token, it.digits, it.suffix)) }
                    parseLitFloat(repr)?.let { return Float(LitFloat.from(token, it.digits, it.suffix)) }
                }
                't'.code,
                'f'.code,
                -> if (repr == "true" || repr == "false") return Bool(LitBool(repr == "true", token.span()))
            }
            return Verbatim(token)
        }
    }

    public data class Str(
        val value: LitStr,
    ) : Lit()

    public data class ByteStr(
        val value: LitByteStr,
    ) : Lit()

    public data class CStr(
        val value: LitCStr,
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
            is ByteStr -> value.span()
            is CStr -> value.span()
            is Byte -> value.span()
            is Char -> value.span()
            is Int -> value.span()
            is Float -> value.span()
            is Bool -> value.span()
            is Verbatim -> value.span()
        }

    public fun setSpan(span: Span) {
        when (this) {
            is Str -> value.setSpan(span)
            is ByteStr -> value.setSpan(span)
            is CStr -> value.setSpan(span)
            is Byte -> value.setSpan(span)
            is Char -> value.setSpan(span)
            is Int -> value.setSpan(span)
            is Float -> value.setSpan(span)
            is Bool -> value.setSpan(span)
            is Verbatim -> value.setSpan(span)
        }
    }

    public override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Str -> value.toTokens(tokens)
            is ByteStr -> value.toTokens(tokens)
            is CStr -> value.toTokens(tokens)
            is Byte -> value.toTokens(tokens)
            is Char -> value.toTokens(tokens)
            is Int -> value.toTokens(tokens)
            is Float -> value.toTokens(tokens)
            is Bool -> value.toTokens(tokens)
            is Verbatim -> value.toTokens(tokens)
        }
    }

    public fun fmt(): String = toString()
}

private data class LitRepr(
    val token: Literal,
    val suffix: String,
) {
    fun clone(): LitRepr =
        LitRepr(cloneLiteral(token), suffix)
}

private data class LitIntRepr(
    val token: Literal,
    val digits: String,
    val suffix: String,
) {
    fun clone(): LitIntRepr =
        LitIntRepr(cloneLiteral(token), digits, suffix)
}

private data class LitFloatRepr(
    val token: Literal,
    val digits: String,
    val suffix: String,
) {
    fun clone(): LitFloatRepr =
        LitFloatRepr(cloneLiteral(token), digits, suffix)
}

private fun cloneLiteral(token: Literal): Literal =
    Literal.fromStrUnchecked(token.toString()).also { it.setSpan(token.span()) }

/** A UTF-8 string literal: `"foo"`. */
public class LitStr private constructor(
    private val repr: LitRepr,
    private val cooked: String,
) : ToTokens {
    public companion object {
        public fun new(value: String, span: Span): LitStr {
            val token = Literal.string(value)
            token.setSpan(span)
            return LitStr(LitRepr(token, ""), value)
        }

        internal fun fromLiteral(
            literal: Literal,
            cooked: String,
            suffix: String,
        ): LitStr = LitStr(LitRepr(literal, suffix), cooked)
    }

    public fun value(): String =
        cooked

    public fun <T> parseWith(parser: (ParseStream) -> SynResult<T>): SynResult<T> {
        val span = span()
        val tokenStream =
            TokenStream.fromString(value()).fold(
                onSuccess = { it },
                onFailure = { return SynResult.failure(SynError.new(span, it.message ?: it.toString())) },
            )
        val result = parseScoped(parser, span, respanTokenStream(tokenStream, span))
        if (result.isFailure) return result
        val litSuffix = suffix()
        return if (litSuffix.isNotEmpty()) {
            SynResult.failure(SynError.new(span, "unexpected suffix `$litSuffix` on string literal"))
        } else {
            result
        }
    }

    public fun span(): Span =
        repr.token.span()

    public fun setSpan(span: Span) {
        repr.token.setSpan(span)
    }

    public fun suffix(): String =
        repr.suffix

    public fun token(): Literal =
        repr.token

    override fun toTokens(tokens: TokenStream) {
        repr.token.toTokens(tokens)
    }

    internal fun debug(name: String): String =
        "$name(token=${repr.token})"

    public fun copy(): LitStr =
        LitStr(repr.clone(), cooked)

    override fun toString(): String = "\"$cooked\""

    override fun equals(other: Any?): Boolean =
        other is LitStr && cooked == other.cooked

    override fun hashCode(): Int = cooked.hashCode()
}

/** A byte string literal: `b"foo"`. */
public class LitByteStr(
    public val bytes: List<UByte>,
    private var spanValue: Span,
    private val suffix: String = "",
    private val literal: Literal? = null,
) : ToTokens {
    public companion object {
        public fun new(value: List<UByte>, span: Span): LitByteStr =
            LitByteStr(value, span)
    }

    public fun value(): List<UByte> =
        bytes

    public fun span(): Span =
        spanValue

    public fun setSpan(span: Span) {
        spanValue = span
    }

    public fun suffix(): String =
        suffix

    public fun token(): Literal {
        val token = literal ?: Literal.byteString(bytes.map { it.toByte() }.toByteArray())
        token.setSpan(spanValue)
        return token
    }

    override fun toTokens(tokens: TokenStream) {
        tokens.append(TokenTree.Literal(token()))
    }

    internal fun debug(name: String): String =
        "$name(token=${token()})"

    public fun copy(): LitByteStr = LitByteStr(bytes, spanValue, suffix, literal)

    override fun toString(): String = "b\"${bytes.map { it.toInt().toChar() }.joinToString("")}\""
}

/** A nul-terminated C-string literal: `c"foo"`. */
public class LitCStr(
    private val bytes: ByteArray,
    private var spanValue: Span,
    private val suffix: String = "",
    private val literal: Literal? = null,
) : ToTokens {
    public companion object {
        public fun new(value: ByteArray, span: Span): LitCStr =
            LitCStr(value.copyOf(), span)
    }

    public fun value(): ByteArray =
        bytes.copyOf()

    public fun span(): Span =
        spanValue

    public fun setSpan(span: Span) {
        spanValue = span
        literal?.setSpan(span)
    }

    public fun suffix(): String =
        suffix

    public fun token(): Literal {
        val token = literal ?: Literal.cString(bytes)
        token.setSpan(spanValue)
        return token
    }

    override fun toTokens(tokens: TokenStream) {
        tokens.append(TokenTree.Literal(token()))
    }

    internal fun debug(name: String): String =
        "$name(token=${token()})"

    public fun copy(): LitCStr = LitCStr(bytes.copyOf(), spanValue, suffix, literal)

    override fun toString(): String = token().toString()

    override fun equals(other: Any?): Boolean =
        other is LitCStr && bytes.contentEquals(other.bytes) && suffix == other.suffix

    override fun hashCode(): Int = bytes.contentHashCode() * 31 + suffix.hashCode()
}

/** A byte literal: `bf`. */
public class LitByte(
    private val value: UByte,
    private val suffix: String,
    private var spanValue: Span,
    private val literal: Literal? = null,
) : ToTokens {
    public companion object {
        public fun new(value: UByte, span: Span): LitByte =
            LitByte(value, "", span)
    }

    public fun value(): UByte =
        value

    public fun span(): Span =
        spanValue

    public fun setSpan(span: Span) {
        spanValue = span
        literal?.setSpan(span)
    }

    public fun suffix(): String =
        suffix

    public fun token(): Literal {
        val token =
            literal
                ?: run {
                    val base = Literal.byteCharacter(value).toString()
                    Literal.fromStrUnchecked(base + suffix)
                }
        token.setSpan(spanValue)
        return token
    }

    override fun toTokens(tokens: TokenStream) {
        tokens.append(TokenTree.Literal(token()))
    }

    internal fun debug(name: String): String =
        "$name(token=${token()})"

    public fun copy(): LitByte = LitByte(value, suffix, spanValue, literal)

    override fun toString(): String = Literal.byteCharacter(value).toString() + suffix
}

/** A character literal: `a`. */
public class LitChar(
    private val value: Int,
    private var spanValue: Span,
    private val suffix: String = "",
    private val literal: Literal? = null,
) : ToTokens {
    public companion object {
        public fun new(value: Char, span: Span): LitChar =
            LitChar(value.code, span)

        public fun new(value: Int, span: Span): LitChar =
            LitChar(value, span)
    }

    init {
        require(value in 0..0x10ffff && value !in 0xd800..0xdfff)
    }

    public fun value(): Int =
        value

    public fun span(): Span =
        spanValue

    public fun setSpan(span: Span) {
        spanValue = span
        literal?.setSpan(span)
    }

    public fun suffix(): String =
        suffix

    public fun token(): Literal {
        val token =
            literal
                ?: if (value <= Char.MAX_VALUE.code) {
                    Literal.character(value.toChar())
                } else {
                    Literal.fromStrUnchecked("'${codePointToString(value)}'")
                }
        token.setSpan(spanValue)
        return token
    }

    override fun toTokens(tokens: TokenStream) {
        tokens.append(TokenTree.Literal(token()))
    }

    internal fun debug(name: String): String =
        "$name(token=${token()})"

    public fun copy(): LitChar = LitChar(value, spanValue, suffix, literal)

    override fun toString(): String = token().toString()
}

/** An integer literal: `1` or `1` with an optional suffix like `i32`. */
public class LitInt private constructor(
    private val repr: LitIntRepr,
) : ToTokens {
    public constructor(digits: String, suffix: String, span: Span) : this(newRepr(digits + suffix, span))

    public companion object {
        public fun new(digits: String, suffix: String, span: Span): LitInt =
            LitInt(digits, suffix, span)

        internal fun from(token: Literal): LitInt {
            val parsed = parseLitInt(token.toString()) ?: error("not an integer literal: `${token}`")
            return from(token, parsed.digits, parsed.suffix)
        }

        internal fun from(token: Literal, digits: String, suffix: String): LitInt =
            LitInt(LitIntRepr(token, digits, suffix))

        private fun newRepr(text: String, span: Span): LitIntRepr {
            val parsed = parseLitInt(text) ?: error("not an integer literal: `$text`")
            val token = Literal.fromStrUnchecked(text)
            token.setSpan(span)
            return LitIntRepr(token, parsed.digits, parsed.suffix)
        }
    }

    public val digits: String
        get() = repr.digits

    public fun base10Digits(): String = repr.digits

    public fun base10Parse(): Long = base10Digits().toLong()

    public fun suffix(): String = repr.suffix

    public fun span(): Span = repr.token.span()

    public fun setSpan(span: Span) {
        repr.token.setSpan(span)
    }

    public fun token(): Literal {
        return cloneLiteral(repr.token)
    }

    override fun toTokens(tokens: TokenStream) {
        tokens.append(TokenTree.Literal(token()))
    }

    internal fun debug(name: String): String =
        "$name(token=${repr.token})"

    public fun copy(): LitInt = LitInt(repr.clone())

    override fun toString(): String = repr.token.toString()
}

/** A floating point literal: `1.0` or `1f64`. */
public class LitFloat private constructor(
    private val repr: LitFloatRepr,
) : ToTokens {
    public constructor(digits: String, suffix: String, span: Span) : this(newRepr(digits + suffix, span))

    public companion object {
        public fun new(digits: String, suffix: String, span: Span): LitFloat =
            LitFloat(digits, suffix, span)

        internal fun from(token: Literal): LitFloat {
            val parsed = parseLitFloat(token.toString()) ?: error("not a float literal: `${token}`")
            return from(token, parsed.digits, parsed.suffix)
        }

        internal fun from(token: Literal, digits: String, suffix: String): LitFloat =
            LitFloat(LitFloatRepr(token, digits, suffix))

        private fun newRepr(text: String, span: Span): LitFloatRepr {
            val parsed = parseLitFloat(text) ?: error("not a float literal: `$text`")
            val token = Literal.fromStrUnchecked(text)
            token.setSpan(span)
            return LitFloatRepr(token, parsed.digits, parsed.suffix)
        }
    }

    public val digits: String
        get() = repr.digits

    public fun base10Digits(): String = repr.digits

    public fun base10Parse(): Double = base10Digits().toDouble()

    public fun suffix(): String = repr.suffix

    public fun span(): Span = repr.token.span()

    public fun setSpan(span: Span) {
        repr.token.setSpan(span)
    }

    public fun token(): Literal {
        return cloneLiteral(repr.token)
    }

    override fun toTokens(tokens: TokenStream) {
        tokens.append(TokenTree.Literal(token()))
    }

    internal fun debug(name: String): String =
        "$name(token=${repr.token})"

    public fun copy(): LitFloat = LitFloat(repr.clone())

    override fun toString(): String = repr.token.toString()
}

/** A boolean literal: `true` or `false`. */
public data class LitBool(
    private val value: Boolean,
    private var spanValue: Span,
) : ToTokens {
    public fun value(): Boolean =
        value

    public fun span(): Span =
        spanValue

    public fun setSpan(span: Span) {
        spanValue = span
    }

    public fun token(): Ident =
        Ident.new(if (value) "true" else "false", spanValue)

    override fun toTokens(tokens: TokenStream) {
        tokens.append(token())
    }

    internal fun debug(name: String): String =
        "$name(value=$value)"

    public fun copy(): LitBool = LitBool(value, spanValue)

    override fun toString(): String = value.toString()
}

/** String literal spelling style. */
public sealed class StrStyle {
    public data object Cooked : StrStyle()

    public data class Raw(
        public val pounds: Int,
    ) : StrStyle()
}

private fun respanTokenStream(stream: TokenStream, span: Span): TokenStream =
    TokenStream.fromTokenTrees(stream.map { respanTokenTree(it, span) })

private fun respanTokenTree(token: TokenTree, span: Span): TokenTree =
    when (token) {
        is TokenTree.Group -> {
            val group = Group(token.value.delimiter(), respanTokenStream(token.value.stream(), span))
            group.setSpan(span)
            TokenTree.Group(group)
        }
        else -> token.setSpan(span)
    }

private data class StringLiteralParts(
    val value: String,
    val suffix: String,
    val style: StrStyle,
)

private data class ByteStringLiteralParts(
    val value: List<UByte>,
    val suffix: String,
)

private data class CStringLiteralParts(
    val value: ByteArray,
    val suffix: String,
)

private data class CharLiteralParts(
    val value: Int,
    val suffix: String,
)

private data class ByteLiteralParts(
    val value: UByte,
    val suffix: String,
)

private data class DigitsLiteralParts(
    val digits: String,
    val suffix: String,
)

private fun byte(s: String, index: Int): Int =
    s.getOrNull(index)?.code ?: 0

private fun nextChr(s: String, index: Int): Pair<Int, Int>? {
    val first = s.getOrNull(index) ?: return null
    if (first in '\ud800'..'\udbff') {
        val second = s.getOrNull(index + 1) ?: return null
        if (second !in '\udc00'..'\udfff') return null
        val high = first.code - 0xd800
        val low = second.code - 0xdc00
        return ((high shl 10) + low + 0x10000) to index + 2
    }
    if (first in '\udc00'..'\udfff') return null
    return first.code to index + 1
}

private fun parseLitStr(s: String): StringLiteralParts? =
    when (byte(s, 0)) {
        '"'.code -> parseLitStrCooked(s)
        'r'.code -> parseLitStrRaw(s)
        else -> null
    }

private fun parseLitStrCooked(s: String): StringLiteralParts? {
    if (byte(s, 0) != '"'.code) return null
    var index = 1
    val content = StringBuilder()
    outer@ while (true) {
        val ch = s.getOrNull(index) ?: return null
        when (ch) {
            '"' -> break
            '\\' -> {
                index += 1
                val escaped = s.getOrNull(index) ?: return null
                index += 1
                when (escaped) {
                    'x' -> {
                        val (value, next) = backslashX(s, index) ?: return null
                        if (value > 0x7f) return null
                        index = next
                        content.append(value.toChar())
                    }
                    'u' -> {
                        val (value, next) = backslashU(s, index) ?: return null
                        index = next
                        content.appendCodePoint(value)
                    }
                    'n' -> content.append('\n')
                    'r' -> content.append('\r')
                    't' -> content.append('\t')
                    '\\' -> content.append('\\')
                    '0' -> content.append('\u0000')
                    '\'' -> content.append('\'')
                    '"' -> content.append('"')
                    '\r',
                    '\n',
                    -> {
                        while (true) {
                            when (s.getOrNull(index)) {
                                ' ', '\t', '\n', '\r' -> index += 1
                                else -> continue@outer
                            }
                        }
                    }
                    else -> return null
                }
            }
            '\r' -> {
                if (s.getOrNull(index + 1) != '\n') return null
                index += 2
                content.append('\n')
            }
            else -> {
                val (codePoint, next) = nextChr(s, index) ?: return null
                content.appendCodePoint(codePoint)
                index = next
            }
        }
    }
    return StringLiteralParts(content.toString(), s.substring(index + 1), StrStyle.Cooked)
}

private fun parseLitStrRaw(s: String): StringLiteralParts? {
    if (byte(s, 0) != 'r'.code) return null
    var pounds = 0
    while (s.getOrNull(1 + pounds) == '#') {
        pounds += 1
    }
    if (s.getOrNull(1 + pounds) != '"') return null

    val close = s.lastIndexOf('"')
    if (close <= 1 + pounds) return null
    val hashesEnd = close + 1 + pounds
    if (hashesEnd > s.length) return null
    for (index in close + 1 until hashesEnd) {
        if (s[index] != '#') return null
    }
    return StringLiteralParts(
        value = s.substring(2 + pounds, close),
        suffix = s.substring(hashesEnd),
        style = StrStyle.Raw(pounds),
    )
}

private fun parseLitByteStr(s: String): ByteStringLiteralParts? {
    if (byte(s, 0) != 'b'.code) return null
    return when (byte(s, 1)) {
        '"'.code -> parseLitByteStrCooked(s)
        'r'.code -> parseLitByteStrRaw(s)
        else -> null
    }
}

private fun parseLitByteStrCooked(s: String): ByteStringLiteralParts? {
    if (!s.startsWith("b\"")) return null
    var index = 2
    val content = mutableListOf<UByte>()
    outer@ while (true) {
        val ch = s.getOrNull(index) ?: return null
        when (ch) {
            '"' -> break
            '\\' -> {
                index += 1
                val escaped = s.getOrNull(index) ?: return null
                index += 1
                if (escaped == '\r' || escaped == '\n') {
                    while (true) {
                        when (s.getOrNull(index)) {
                            ' ', '\t', '\n', '\r' -> index += 1
                            else -> continue@outer
                        }
                    }
                }
                val value =
                    when (escaped) {
                        'x' -> {
                            val (value, next) = backslashX(s, index) ?: return null
                            index = next
                            value
                        }
                        'n' -> '\n'.code
                        'r' -> '\r'.code
                        't' -> '\t'.code
                        '\\' -> '\\'.code
                        '0' -> 0
                        '\'' -> '\''.code
                        '"' -> '"'.code
                        else -> return null
                    }
                content += value.toUByte()
            }
            '\r' -> {
                if (s.getOrNull(index + 1) != '\n') return null
                index += 2
                content += '\n'.code.toUByte()
            }
            else -> {
                if (ch.code > 0x7f) return null
                index += 1
                content += ch.code.toUByte()
            }
        }
    }
    return ByteStringLiteralParts(content, s.substring(index + 1))
}

private fun parseLitByteStrRaw(s: String): ByteStringLiteralParts? {
    if (!s.startsWith("br")) return null
    val parsed = parseLitStrRaw(s.substring(1)) ?: return null
    val bytes = parsed.value.map { ch ->
        if (ch.code > 0x7f) return null
        ch.code.toUByte()
    }
    return ByteStringLiteralParts(bytes, parsed.suffix)
}

private fun parseLitCStr(s: String): CStringLiteralParts? {
    if (byte(s, 0) != 'c'.code) return null
    return when (byte(s, 1)) {
        '"'.code -> parseLitCStrCooked(s)
        'r'.code -> parseLitCStrRaw(s)
        else -> null
    }
}

private fun parseLitCStrCooked(s: String): CStringLiteralParts? {
    if (!s.startsWith("c\"")) return null
    var index = 2
    val content = mutableListOf<Byte>()
    outer@ while (true) {
        val ch = s.getOrNull(index) ?: return null
        when (ch) {
            '"' -> break
            '\\' -> {
                index += 1
                val escaped = s.getOrNull(index) ?: return null
                index += 1
                if (escaped == '\r' || escaped == '\n') {
                    while (true) {
                        when (s.getOrNull(index)) {
                            ' ', '\t', '\n', '\r' -> index += 1
                            else -> continue@outer
                        }
                    }
                }
                when (escaped) {
                    'x' -> {
                        val (value, next) = backslashX(s, index) ?: return null
                        if (value == 0) return null
                        index = next
                        content += value.toByte()
                    }
                    'u' -> {
                        val (value, next) = backslashU(s, index) ?: return null
                        if (value == 0) return null
                        index = next
                        content.addUtf8(value)
                    }
                    'n' -> content += '\n'.code.toByte()
                    'r' -> content += '\r'.code.toByte()
                    't' -> content += '\t'.code.toByte()
                    '\\' -> content += '\\'.code.toByte()
                    '\'' -> content += '\''.code.toByte()
                    '"' -> content += '"'.code.toByte()
                    else -> return null
                }
            }
            '\r' -> {
                if (s.getOrNull(index + 1) != '\n') return null
                index += 2
                content += '\n'.code.toByte()
            }
            '\u0000' -> return null
            else -> {
                val (codePoint, next) = nextChr(s, index) ?: return null
                if (codePoint == 0) return null
                content.addUtf8(codePoint)
                index = next
            }
        }
    }
    return CStringLiteralParts(content.toByteArray(), s.substring(index + 1))
}

private fun parseLitCStrRaw(s: String): CStringLiteralParts? {
    if (!s.startsWith("cr")) return null
    val parsed = parseLitStrRaw(s.substring(1)) ?: return null
    if (parsed.value.any { it == '\u0000' }) return null
    return CStringLiteralParts(parsed.value.encodeToByteArray(), parsed.suffix)
}

private fun parseLitByte(text: String, span: Span): LitByte? {
    val parsed = parseLitByteParts(text) ?: return null
    return LitByte(parsed.value, parsed.suffix, span, Literal.fromStrUnchecked(text).also { it.setSpan(span) })
}

private fun parseLitByteParts(s: String): ByteLiteralParts? {
    if (!s.startsWith("b'")) return null
    var index = 2
    val value =
        when (val ch = s.getOrNull(index) ?: return null) {
            '\\' -> {
                index += 1
                val escaped = s.getOrNull(index) ?: return null
                index += 1
                when (escaped) {
                    'x' -> {
                        val (value, next) = backslashX(s, index) ?: return null
                        index = next
                        value
                    }
                    'n' -> '\n'.code
                    'r' -> '\r'.code
                    't' -> '\t'.code
                    '\\' -> '\\'.code
                    '0' -> 0
                    '\'' -> '\''.code
                    '"' -> '"'.code
                    else -> return null
                }
            }
            else -> {
                index += 1
                if (ch.code > 0xff) return null
                ch.code
            }
        }
    if (value > 0xff) return null
    if (s.getOrNull(index) != '\'') return null
    return ByteLiteralParts(value.toUByte(), s.substring(index + 1))
}

private fun parseLitChar(s: String): CharLiteralParts? {
    if (byte(s, 0) != '\''.code) return null
    var index = 1
    val value =
        when (val ch = s.getOrNull(index) ?: return null) {
            '\\' -> {
                index += 1
                val escaped = s.getOrNull(index) ?: return null
                index += 1
                when (escaped) {
                    'x' -> {
                        val (byte, next) = backslashX(s, index) ?: return null
                        if (byte > 0x7f) return null
                        index = next
                        byte
                    }
                    'u' -> {
                        val (codePoint, next) = backslashU(s, index) ?: return null
                        index = next
                        codePoint
                    }
                    'n' -> '\n'.code
                    'r' -> '\r'.code
                    't' -> '\t'.code
                    '\\' -> '\\'.code
                    '0' -> 0
                    '\'' -> '\''.code
                    '"' -> '"'.code
                    else -> return null
                }
            }
            else -> {
                val (codePoint, next) = nextChr(s, index) ?: return null
                index = next
                codePoint
            }
        }
    if (s.getOrNull(index) != '\'') return null
    return CharLiteralParts(value, s.substring(index + 1))
}

private fun backslashX(s: String, index: Int): Pair<Int, Int>? {
    val first = s.getOrNull(index)?.digitToIntOrNull(16) ?: return null
    val second = s.getOrNull(index + 1)?.digitToIntOrNull(16) ?: return null
    return first * 0x10 + second to index + 2
}

private fun backslashU(s: String, index: Int): Pair<Int, Int>? {
    if (s.getOrNull(index) != '{') return null
    var value = 0
    var digits = 0
    var cursor = index + 1
    while (cursor < s.length) {
        val ch = s[cursor]
        val digit =
            when {
                ch in '0'..'9' -> ch.code - '0'.code
                ch in 'a'..'f' -> 10 + ch.code - 'a'.code
                ch in 'A'..'F' -> 10 + ch.code - 'A'.code
                ch == '_' && digits > 0 -> {
                    cursor += 1
                    continue
                }
                ch == '}' && digits == 0 -> return null
                ch == '}' -> break
                else -> return null
            }
        if (digits == 6) return null
        value = value * 0x10 + digit
        digits += 1
        cursor += 1
    }
    if (s.getOrNull(cursor) != '}') return null
    if (value > 0x10ffff || value in 0xd800..0xdfff) return null
    return value to cursor + 1
}

private fun parseLitInt(s: String): DigitsLiteralParts? {
    var index = 0
    val negative = byte(s, 0) == '-'.code
    if (negative) {
        index += 1
    }
    val base =
        when {
            byte(s, index) == '0'.code && byte(s, index + 1) == 'x'.code -> {
                index += 2
                16
            }
            byte(s, index) == '0'.code && byte(s, index + 1) == 'o'.code -> {
                index += 2
                8
            }
            byte(s, index) == '0'.code && byte(s, index + 1) == 'b'.code -> {
                index += 2
                2
            }
            byte(s, index) in '0'.code..'9'.code -> 10
            else -> return null
        }

    val value = BigInt.new()
    var hasDigit = false
    loop@ while (index < s.length) {
        val ch = s[index]
        val digit =
            when {
                ch in '0'..'9' -> ch.code - '0'.code
                ch in 'a'..'f' && base > 10 -> ch.code - 'a'.code + 10
                ch in 'A'..'F' && base > 10 -> ch.code - 'A'.code + 10
                ch == '_' -> {
                    index += 1
                    continue@loop
                }
                ch == '.' && base == 10 -> return null
                (ch == 'e' || ch == 'E') && base == 10 -> {
                    var hasExp = false
                    var suffixIndex = index + 1
                    while (suffixIndex < s.length) {
                        when (val exp = s[suffixIndex]) {
                            '_' -> Unit
                            '-', '+' -> return null
                            in '0'..'9' -> hasExp = true
                            else -> {
                                val suffix = s.substring(suffixIndex)
                                if (hasExp && xidOk(suffix)) {
                                    return null
                                }
                                break@loop
                            }
                        }
                        suffixIndex += 1
                    }
                    if (hasExp) {
                        return null
                    }
                    break@loop
                }
                else -> break@loop
            }
        if (digit >= base) return null
        hasDigit = true
        value.mulAssign(base)
        value.addAssign(digit)
        index += 1
    }
    if (!hasDigit) return null
    val suffix = s.substring(index)
    if (suffix.isNotEmpty() && !xidOk(suffix)) return null
    val digits = buildString {
        if (negative) append('-')
        append(value.toString())
    }
    return DigitsLiteralParts(digits, suffix)
}

private fun parseLitFloat(input: String): DigitsLiteralParts? {
    if (input.isEmpty()) return null
    val chars = input.toMutableList()
    val start = if (chars.first() == '-') 1 else 0
    if (chars.getOrNull(start) !in '0'..'9') return null

    var read = start
    var write = start
    var hasDot = false
    var hasE = false
    var hasSign = false
    var hasExponent = false
    while (read < chars.size) {
        when (val ch = chars[read]) {
            '_' -> {
                read += 1
                continue
            }
            in '0'..'9' -> {
                if (hasE) hasExponent = true
                chars[write] = ch
            }
            '.' -> {
                if (hasE || hasDot) return null
                hasDot = true
                chars[write] = '.'
            }
            'e',
            'E',
            -> {
                val next = chars.drop(read + 1).firstOrNull { it != '_' } ?: '\u0000'
                if (next != '-' && next != '+' && next !in '0'..'9') break
                if (hasE) {
                    if (hasExponent) break else return null
                }
                hasE = true
                chars[write] = 'e'
            }
            '-',
            '+',
            -> {
                if (hasSign || hasExponent || !hasE) return null
                hasSign = true
                if (ch == '-') {
                    chars[write] = ch
                } else {
                    read += 1
                    continue
                }
            }
            else -> break
        }
        read += 1
        write += 1
    }
    if (hasE && !hasExponent) return null
    val suffix = input.substring(read)
    if (suffix.isNotEmpty() && !xidOk(suffix)) return null
    val digits = chars.take(write).joinToString("")
    return DigitsLiteralParts(digits, suffix)
}

private fun litFromLiteral(literal: Literal): Lit =
    Lit.new(literal)

private fun parseNegativeLit(neg: Punct, cursor: Cursor): Pair<Lit, Cursor>? {
    val (literal, rest) = cursor.literal() ?: return null
    var span = neg.span()
    span = span.join(literal.span()) ?: span
    val repr = "-${literal}"
    parseLitInt(repr)?.let {
        val token = Literal.fromStrUnchecked(repr)
        token.setSpan(span)
        return Lit.Int(LitInt.from(token)) to rest
    }
    parseLitFloat(repr)?.let {
        val token = Literal.fromStrUnchecked(repr)
        token.setSpan(span)
        return Lit.Float(LitFloat.from(token)) to rest
    }
    return null
}

private fun <T> peekImpl(cursor: Cursor, parser: (ParseStream) -> SynResult<T>): Boolean {
    val buffer = newParseBuffer(Span.callSite(), cursor, UnexpectedRef(Unexpected.None))
    return parser(buffer).isSuccess
}

private fun MutableList<Byte>.addUtf8(codePoint: Int) {
    val string = codePointToString(codePoint)
    for (byte in string.encodeToByteArray()) {
        add(byte)
    }
}

private fun StringBuilder.appendCodePoint(codePoint: Int) {
    append(codePointToString(codePoint))
}

private fun codePointToString(codePoint: Int): String {
    if (codePoint <= Char.MAX_VALUE.code) {
        return codePoint.toChar().toString()
    }
    val shifted = codePoint - 0x10000
    val high = (0xd800 + (shifted shr 10)).toChar()
    val low = (0xdc00 + (shifted and 0x3ff)).toChar()
    return charArrayOf(high, low).concatToString()
}

public object LitParse {
    public fun parse(input: ParseStream): SynResult<Lit> =
        input.step { cursor ->
            cursor.literal()?.let { (lit, rest) ->
                return@step SynResult.success(litFromLiteral(lit) to rest)
            }
            val identPair = cursor.ident()
            if (identPair != null) {
                val (ident, rest) = identPair
                when (ident.toString()) {
                    "true" -> return@step SynResult.success(Lit.Bool(LitBool(true, ident.span())) to rest)
                    "false" -> return@step SynResult.success(Lit.Bool(LitBool(false, ident.span())) to rest)
                }
            }
            val punctPair = cursor.punct()
            if (punctPair != null) {
                val (punct, rest) = punctPair
                if (punct.asChar() == '-') {
                    parseNegativeLit(punct, rest)?.let { return@step SynResult.success(it) }
                }
            }
            SynResult.failure(cursor.error("expected literal"))
        }
}

public object LitStrParse {
    public fun parse(input: ParseStream): SynResult<LitStr> {
        val result = LitParse.parse(input)
        if (result is SynResult.Success && result.value is Lit.Str) {
            return SynResult.success(result.value.value)
        }
        return SynResult.failure(input.error("expected string literal"))
    }
}

public object LitIntParse {
    public fun parse(input: ParseStream): SynResult<LitInt> {
        val result = LitParse.parse(input)
        if (result is SynResult.Success && result.value is Lit.Int) {
            return SynResult.success(result.value.value)
        }
        return SynResult.failure(input.error("expected integer literal"))
    }
}

public object LitFloatParse {
    public fun parse(input: ParseStream): SynResult<LitFloat> {
        val result = LitParse.parse(input)
        if (result is SynResult.Success && result.value is Lit.Float) {
            return SynResult.success(result.value.value)
        }
        return SynResult.failure(input.error("expected float literal"))
    }
}

public object LitBoolParse {
    public fun parse(input: ParseStream): SynResult<LitBool> =
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
        peekImpl(cursor, LitParse::parse)

    override fun display(): String = "literal"
}
