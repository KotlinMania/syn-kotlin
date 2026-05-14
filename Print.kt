// port-lint: source src/print.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

internal class TokensOrDefault<T : ToTokens>(
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
