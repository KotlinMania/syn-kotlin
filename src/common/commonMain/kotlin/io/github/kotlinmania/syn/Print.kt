// port-lint: source print.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

internal class TokensOrDefault<T : ToTokens>(
    public val value: T?,
    private val defaultFactory: () -> T,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        val current = value
        if (current != null) {
            current.toTokens(tokens)
        } else {
            defaultFactory().toTokens(tokens)
        }
    }
}

internal fun <T : ToTokens> tokensOrDefault(
    value: T?,
    default: () -> T,
): ToTokens = TokensOrDefault(value, default)
