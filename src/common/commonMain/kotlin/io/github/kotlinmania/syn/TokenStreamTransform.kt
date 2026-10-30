package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

/** Transforms a token stream into the tokens produced by a procedural macro. */
public fun interface TokenStreamTransform {
    public operator fun invoke(tokens: TokenStream): TokenStream
}
