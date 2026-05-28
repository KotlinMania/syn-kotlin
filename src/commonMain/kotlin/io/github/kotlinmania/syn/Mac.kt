// port-lint: source mac.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * A macro invocation: `println!("{}", mac)`.
 */
public data class Macro(
 public val path: Path,
 public val bangToken: io.github.kotlinmania.syn.token.Not,
 public val delimiter: MacroDelimiter,
 public val tokens: TokenStream,
) : ToTokens {
 override fun toTokens(tokens: TokenStream) {
  path.toTokens(tokens)
  bangToken.toTokens(tokens)
  delimiter.toTokens(tokens)
 }

 public fun deepCopy(): Macro =
  Macro(path.deepCopy(), bangToken, delimiter, tokens)
}

/**
 * Extension to check if a [MacroDelimiter] is brace-delimited.
 */
public val MacroDelimiter.isBrace: Boolean
 get() = this is MacroDelimiter.Brace
