// port-lint: source expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream

/** An expression syntax tree node. */
public sealed class Expr {
    public data class Lit(val attrs: List<Attribute>, val lit: io.github.kotlinmania.syn.Lit) : Expr()
    public data class Path(val attrs: List<Attribute>, val qself: QSelf?, val path: io.github.kotlinmania.syn.Path) : Expr()
    public data class Verbatim(val tokens: TokenStream) : Expr()

    public fun copy(): Expr =
        when (this) {
            is Lit -> copy(attrs = attrs.map { it.deepCopy() })
            is Path -> copy(attrs = attrs.map { it.deepCopy() }, path = path.deepCopy())
            is Verbatim -> copy()
        }
}

/** A member of a struct or tuple. */
public sealed class Member {
    public data class Named(val ident: Ident) : Member()
    public data class Unnamed(val index: Index) : Member()
}

/** A tuple field index such as `0` in `self.0`. */
public data class Index(
    public val index: UInt,
    public val span: Span,
)
