package io.github.kotlinmania.syn

import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.syn.generated.print.TokensOrDefault
import io.github.kotlinmania.syn.generated.print.__rtkOption

/** Retains the optional value for the generated shared borrow and supplies the
 * existing token provider plus a lazy default factory. */
internal fun tokensOrDefault(value: ToTokens?, default: () -> ToTokens): ToTokens =
    TokensOrDefault(
        if (value == null) __rtkOption.None else __rtkOption.Some(value),
        { token, output -> token.toTokens(output) },
        default,
    )
