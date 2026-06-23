// port-lint: source print.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

internal class TokensOrDefault(
    private val value: ToTokens?,
    private val default: DefaultTokens,
) : ToTokens {
    internal constructor(
        value: ToTokens?,
        default: () -> ToTokens,
    ) : this(value, DefaultTokens(default))

    override fun toTokens(tokens: TokenStream) {
        when (val v = value) {
            null -> default.value().toTokens(tokens)
            else -> v.toTokens(tokens)
        }
    }
}

internal class DefaultTokens(
    private val makeDefault: () -> ToTokens,
) {
    internal fun value(): ToTokens =
        makeDefault()
}