// port-lint: source lifetime.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append

/** A named duration marker in a syntax tree. */
public data class Lifetime(
    public var apostrophe: Span,
    public var ident: Ident,
) : Comparable<Lifetime>,
    ToTokens {
    public companion object {
        public fun new(symbol: String, span: Span): Lifetime {
            require(symbol.startsWith('\'')) {
                "lifetime name must start with apostrophe"
            }
            require(symbol != "'") {
                "lifetime name must not be empty"
            }
            require(xidOk(symbol.substring(1))) {
                "$symbol is not a valid lifetime name"
            }
            return Lifetime(
                apostrophe = span,
                ident = Ident.new(symbol.substring(1), span),
            )
        }
    }

    public fun span(): Span =
        apostrophe.join(ident.span()) ?: apostrophe

    public fun setSpan(span: Span) {
        apostrophe = span
        ident.setSpan(span)
    }

    public fun deepCopy(): Lifetime =
        Lifetime(apostrophe, ident.copy())

    override fun toTokens(tokens: TokenStream) {
        tokens.append(Punct('\'', Spacing.Joint, apostrophe))
        tokens.append(ident)
    }

    override fun compareTo(other: Lifetime): Int =
        ident.compareTo(other.ident)

    override fun toString(): String =
        "'$ident"

    override fun equals(other: Any?): Boolean =
        other is Lifetime && ident == other.ident

    override fun hashCode(): Int =
        ident.hashCode()

    public fun clone(): Lifetime = deepCopy()

    public fun fmt(): String = toString()

    public fun eq(other: Lifetime): Boolean = equals(other)

    public fun cmp(other: Lifetime): Int = compareTo(other)

    public fun hash(): Int = hashCode()
}

public object LifetimeParse : Parse<Lifetime> {
    override fun parse(input: ParseStream): SynResult<Lifetime> =
        input.step { cursor: StepCursor ->
            val pair: Pair<Lifetime, Cursor>? = cursor.lifetime()
            if (pair == null) {
                SynResult.failure(cursor.error("expected lifetime"))
            } else {
                val (lifetime, rest) = pair
                SynResult.success(lifetime to rest)
            }
        }
}

public object LifetimePeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.lifetime() != null

    override fun display(): String = "lifetime"
}
