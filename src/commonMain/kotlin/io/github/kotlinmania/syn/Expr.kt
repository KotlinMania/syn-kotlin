// port-lint: source expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.quote.append

/** An expression syntax tree node. */
public sealed class Expr : ToTokens {
 public data class Lit(val attrs: List<Attribute>, val lit: io.github.kotlinmania.syn.Lit) : Expr() {
 override fun toTokens(tokens: TokenStream) {
 lit.toTokens(tokens)
 }

 override fun deepCopy(): Lit = Lit(attrs.map { it.deepCopy() }, lit)
 }

 public data class Path(val attrs: List<Attribute>, val qself: QSelf?, val path: io.github.kotlinmania.syn.Path) : Expr() {
 override fun toTokens(tokens: TokenStream) {
 qself?.let {
 it.ltToken.toTokens(tokens)
 it.ty.toTokens(tokens)
 it.asToken?.toTokens(tokens)
 it.gtToken.toTokens(tokens)
 }
 path.toTokens(tokens)
 }

 override fun deepCopy(): Path = Path(attrs.map { it.deepCopy() }, qself, path.deepCopy())
 }

 public data class Verbatim(val tokens: TokenStream) : Expr() {
 override fun toTokens(tokens: TokenStream) {
 tokens.extendTokenStreams(listOf(tokens))
 }

 override fun deepCopy(): Verbatim = this
 }

 public abstract fun deepCopy(): Expr
}

/** A member of a data structure or tuple. */
public sealed class Member {
 public data class Named(val ident: Ident) : Member()
 public data class Unnamed(val index: Index) : Member()
}

/** A tuple field index such as `0` in `obj.0`. */
public data class Index(
 public val index: UInt,
 public val span: Span,
)
