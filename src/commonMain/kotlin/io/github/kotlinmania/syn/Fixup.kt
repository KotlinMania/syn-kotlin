// port-lint: source fixup.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * Tracks context needed to print an expression without unnecessary parentheses.
 *
 * When printing nested expressions, some subexpressions need parentheses
 * depending on the precedence of the surrounding operator. [FixupContext]
 * carries that context through the print traversal.
 *
 * This is an internal helper for expression printing.
 */
public data class FixupContext(
    public val nextOperator: Precedence?,
    public val stmtContext: Boolean,
) {
    public companion object {
        /** No fixup — the expression is in an unambiguous position. */
        public val NONE: FixupContext = FixupContext(nextOperator = null, stmtContext = false)
    }
}

/**
 * Wraps a value with a default token representation to use when the value is absent.
 */
internal class FixupOrDefault<T : ToTokens>(
    private val value: T?,
    private val default: () -> T,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        when (val v = value) {
            null -> default().toTokens(tokens)
            else -> v.toTokens(tokens)
        }
    }
}
