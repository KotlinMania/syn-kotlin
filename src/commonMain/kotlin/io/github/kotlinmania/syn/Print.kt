// port-lint: source print.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * Prints the contained token value, or prints the token value's default when
 * the optional value is absent.
 */
internal class TokensOrDefault<T>(
    private val value: T?,
    private val default: DefaultTokens<T>,
) : ToTokens where T : ToTokens {
    internal constructor(
        value: T?,
        default: () -> T,
    ) : this(value, DefaultTokens(default))

    override fun toTokens(tokens: TokenStream) {
        when (val v = value) {
            null -> default.value().toTokens(tokens)
            else -> v.toTokens(tokens)
        }
    }
}

/**
 * Kotlin's stand-in for the upstream `Default` bound used by
 * `TokensOrDefault`.
 */
internal class DefaultTokens<T>(
    private val makeDefault: () -> T,
) where T : ToTokens {
    internal fun value(): T =
        makeDefault()
}
