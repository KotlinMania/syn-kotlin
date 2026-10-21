package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

/** Parsed or expanded tokens, or their diagnostic. Exactly one branch is present. */
public class TokenStreamParseResult private constructor(
    public val value: TokenStream?,
    public val error: SynError?,
) {
    public val isSuccess: Boolean
        get() = value != null

    public val isFailure: Boolean
        get() = error != null

    public fun getOrNull(): TokenStream? = value

    public fun exceptionOrNull(): SynError? = error

    /** Returns the tokens or raises an internal argument exception carrying their diagnostic. */
    public fun getOrThrow(): TokenStream = value ?: requireNotNull(error).raise()

    public companion object {
        public fun success(value: TokenStream): TokenStreamParseResult = TokenStreamParseResult(value, null)

        public fun failure(error: SynError): TokenStreamParseResult = TokenStreamParseResult(null, error)
    }
}

/** Adapts a parser callback result without copying its tokens or diagnostic. */
public fun SynResult<TokenStream>.toTokenStreamParseResult(): TokenStreamParseResult =
    when (this) {
        is SynResult.Success -> TokenStreamParseResult.success(value)
        is SynResult.Failure -> TokenStreamParseResult.failure(error)
    }
