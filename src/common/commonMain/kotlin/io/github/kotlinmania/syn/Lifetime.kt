// port-lint: source lifetime.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append

/**
 * A named scope label token prefixed by an apostrophe.
 *
 * Scope names must conform to the following rules:
 *
 * - Must start with an apostrophe.
 * - Must not consist of just an apostrophe.
 * - Character after the apostrophe must be `_` or a Unicode code point with
 *   the XidStart property.
 * - All following characters must be Unicode code points with the XidContinue
 *   property.
 */
public data class Lifetime(
    public var apostrophe: Span,
    public var ident: Ident,
) : Comparable<Lifetime>,
    ToTokens {
    public companion object {
        /**
         * Throws an [IllegalArgumentException] if the symbol does not conform
         * to the bulleted rules above.
         */
        public fun new(symbol: String, span: Span): Lifetime {
            require(symbol.startsWith('\'')) {
                "lifetime name must start with apostrophe as in \"'a\", got \"$symbol\""
            }
            val ident = symbol.substring(1)
            val unraw = if (ident.startsWith("r#")) ident.substring(2) else null
            val validate = unraw ?: ident
            require(validate.isNotEmpty()) {
                "lifetime name must not be empty"
            }
            require(xidOk(validate)) {
                "\"$symbol\" is not a valid lifetime name"
            }
            return Lifetime(
                apostrophe = span,
                ident = if (unraw != null) Ident.newRaw(unraw, span) else Ident.new(ident, span),
            )
        }

        public fun parseAny(input: ParseStream): SynResult<Lifetime> =
            input.step { cursor: StepCursor ->
                val pair: Pair<Lifetime, Cursor>? = cursor.lifetime()
                if (pair == null) {
                    SynResult.failure(cursor.error("expected lifetime"))
                } else {
                    SynResult.success(pair)
                }
            }

        internal fun parseOptionalAny(input: ParseStream): Lifetime? =
            input
                .step { cursor: StepCursor ->
                    val pair: Pair<Lifetime, Cursor>? = cursor.lifetime()
                    if (pair != null) {
                        SynResult.success(pair.first as Lifetime? to pair.second)
                    } else {
                        SynResult.success(null to cursor.raw)
                    }
                }.getOrThrow()
    }

    public fun span(): Span =
        apostrophe.join(ident.span()) ?: apostrophe

    public fun setSpan(span: Span) {
        apostrophe = span
        ident.setSpan(span)
    }

    public fun deepCopy(): Lifetime =
        Lifetime(apostrophe, ident.copy())

    public fun clone(): Lifetime =
        deepCopy()

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

    public fun fmt(): String = toString()

    public fun eq(other: Lifetime): Boolean = equals(other)

    public fun partialCmp(other: Lifetime): Int = compareTo(other)

    public fun cmp(other: Lifetime): Int = compareTo(other)

    public fun hash(): Int = hashCode()
}

public object LifetimeParse {
    public fun parse(input: ParseStream): SynResult<Lifetime> =
        input.step { cursor: StepCursor ->
            val pair: Pair<Lifetime, Cursor>? = cursor.lifetime()
            if (pair == null) {
                SynResult.failure(cursor.error("expected lifetime"))
            } else {
                val (lifetime, rest) = pair
                val repr = lifetime.ident.toString()
                if (acceptAsIdent(repr) || repr == "static" || repr == "_") {
                    SynResult.success(lifetime to rest)
                } else {
                    SynResult.failure(cursor.error("unexpected keyword lifetime \"$lifetime\""))
                }
            }
        }
}

public object LifetimePeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.lifetime() != null

    override fun display(): String = "lifetime"
}
