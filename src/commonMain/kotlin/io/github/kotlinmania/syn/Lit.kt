// port-lint: source lit.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens

/** A Rust literal such as a string or integer or boolean. */
public sealed class Lit {
    public data class Str(val value: LitStr) : Lit()
    public data class ByteStr(val value: LitByteStr) : Lit()
    public data class Byte(val value: LitByte) : Lit()
    public data class Char(val value: LitChar) : Lit()
    public data class Int(val value: LitInt) : Lit()
    public data class Float(val value: LitFloat) : Lit()
    public data class Bool(val value: LitBool) : Lit()
    public data class Verbatim(val value: Literal) : Lit()
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
}

public class LitByteStr(public val bytes: List<UByte>, private val spanValue: Span)
public class LitByte(public val value: UByte, private val spanValue: Span)
public class LitChar(public val value: Char, private val spanValue: Span)
public class LitInt(public val digits: String, public val suffix: String, private val spanValue: Span)
public class LitFloat(public val digits: String, public val suffix: String, private val spanValue: Span)

/** A boolean literal: `true` or `false`. */
public data class LitBool(
    public val value: Boolean,
    public val span: Span,
)
