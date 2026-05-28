// port-lint: source restriction.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens


/** Visibility of an item. */
public sealed class Visibility : ToTokens {
 public data object Public : Visibility() {
 override fun toTokens(tokens: TokenStream) {
  // public visibility may or may not emit a keyword depending on context
 }
}
 public data object Inherited : Visibility() {
 override fun toTokens(tokens: TokenStream) {
  // inherited (private) visibility emits nothing
 }
}
 public data class Restricted(val path: Path) : Visibility() {
 override fun toTokens(tokens: TokenStream) {
  path.toTokens(tokens)
 }
}
}

/** Field mutability marker. */
public sealed class FieldMutability : ToTokens {
 public data object None : FieldMutability() {
 override fun toTokens(tokens: TokenStream) {
  // immutable field emits nothing
 }
}
 public data class Mut(val token: io.github.kotlinmania.syn.token.Mut) : FieldMutability() {
 override fun toTokens(tokens: TokenStream) {
  token.toTokens(tokens)
 }
}
}

/** Tokens that are preserved exactly when a more specific syntax node is not yet modeled. */
public data class Verbatim(
 public val tokens: TokenStream,
)
