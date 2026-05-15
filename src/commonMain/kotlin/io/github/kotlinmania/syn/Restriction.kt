// port-lint: source restriction.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

/** Visibility of an item. */
public sealed class Visibility {
    public data object Public : Visibility()
    public data object Inherited : Visibility()
    public data class Restricted(val path: Path) : Visibility()
}

/** Field mutability marker. */
public sealed class FieldMutability {
    public data object None : FieldMutability()
    public data class Mut(val token: io.github.kotlinmania.syn.token.Mut) : FieldMutability()
}

/** Tokens that are preserved exactly when a more specific syntax node is not yet modeled. */
public data class Verbatim(
    public val tokens: TokenStream,
)
