// port-lint: source pat.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Or
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import kotlin.native.HiddenFromObjC

/**
 * A pattern in a local binding, function signature, match expression, or
 * various other places.
 */
@HiddenFromObjC
public sealed class Pat : ToTokens {
 public abstract fun deepCopy(): Pat

 /** A pattern that binds a new variable, optionally with a reference, mutability, and sub-pattern. */
 public data class Ident(
  public val attrs: List<Attribute>,
  public val byRef: io.github.kotlinmania.syn.token.Ref?,
  public val mutability: FieldMutability,
  public val ident: Ident,
  public val atToken: io.github.kotlinmania.syn.token.At?,
  public val subpat: Pat?,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() })

  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   byRef?.toTokens(tokens)
   mutability.toTokens(tokens)
   ident.toTokens(tokens)
   atToken?.toTokens(tokens)
   subpat?.toTokens(tokens)
  }
 }

 /** A tuple pattern: `(A, B, C)`. */
 public data class Tuple(
  public val parenToken: io.github.kotlinmania.syn.token.Paren,
  public val elems: Punctuated<Pat, Comma>,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(elems = elems.copy({ it.deepCopy() }, { it }))

  override fun toTokens(tokens: TokenStream) {
   parenToken.surround(tokens) { inner ->
    for ((elem, comma) in elems.pairs()) {
     elem.toTokens(inner)
     comma?.toTokens(inner)
    }
   }
  }
 }

 /** A pattern that matches any one of a set of cases. */
 public data class Or(
  public val leadingVert: Or?,
  public val cases: Punctuated<Pat, Or>,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(cases = cases.copy({ it.deepCopy() }, { it }))

  override fun toTokens(tokens: TokenStream) {
   leadingVert?.toTokens(tokens)
   for ((case, vert) in cases.pairs()) {
    case.toTokens(tokens)
    vert?.toTokens(tokens)
   }
  }
 }

 /** A parenthesized pattern: `(A | B)`. */
 public data class PatParen(
  public val parenToken: io.github.kotlinmania.syn.token.Paren,
  public val pat: Pat,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(pat = pat.deepCopy())

  override fun toTokens(tokens: TokenStream) {
   parenToken.surround(tokens) { inner -> pat.toTokens(inner) }
  }
 }

 /** A mutable reference pattern. */
 public data class Reference(
  public val andToken: io.github.kotlinmania.syn.token.And,
  public val mutability: FieldMutability,
  public val pat: Pat,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(andToken = andToken, mutability = mutability, pat = pat.deepCopy())

  override fun toTokens(tokens: TokenStream) {
   andToken.toTokens(tokens)
   mutability.toTokens(tokens)
   pat.toTokens(tokens)
  }
 }

 /** A struct pattern: `Point { x: 0, y: 0 }`. */
 public data class Struct(
  public val qself: QSelf?,
  public val path: Path,
  public val braceToken: io.github.kotlinmania.syn.token.Brace,
  public val fields: Punctuated<FieldPat, Comma>,
  public val rest: PatRest?,
  public val dot2Token: io.github.kotlinmania.syn.token.DotDot?,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(qself = qself, path = path.deepCopy(), fields = fields.copy({ it.deepCopy() }, { it }), rest = rest?.deepCopy(), dot2Token = dot2Token)

  override fun toTokens(tokens: TokenStream) {
   qself?.let { it.ltToken.toTokens(tokens); it.ty.toTokens(tokens); it.asToken?.toTokens(tokens); it.gtToken.toTokens(tokens) }
   path.toTokens(tokens)
   braceToken.surround(tokens) { inner ->
    for ((field, comma) in fields.pairs()) {
     field.toTokens(inner)
     comma?.toTokens(inner)
    }
    rest?.toTokens(inner)
    dot2Token?.toTokens(inner)
   }
  }
 }

 /** A slice pattern: `[a, b.., c]`. */
 public data class Slice(
  public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
  public val elems: Punctuated<Pat, Comma>,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(elems = elems.copy({ it.deepCopy() }, { it }))

  override fun toTokens(tokens: TokenStream) {
   bracketToken.surround(tokens) { inner ->
    for ((elem, comma) in elems.pairs()) {
     elem.toTokens(inner)
     comma?.toTokens(inner)
    }
   }
  }
 }

 /** A type ascription pattern: `v: Int`. */
 public data class Type(
  public val pat: Pat,
  public val colonToken: Colon,
  public val ty: SynType,
 ) : Pat() {
  override fun deepCopy(): Pat = copy(pat = pat.deepCopy(), ty = ty.deepCopy())

  override fun toTokens(tokens: TokenStream) {
   pat.toTokens(tokens)
   colonToken.toTokens(tokens)
   ty.toTokens(tokens)
  }
 }

 /** Tokens forming a pattern not interpreted by Syn. */
 public data class Verbatim(
  public val tokens: TokenStream,
 ) : Pat() {
  override fun deepCopy(): Pat = this

  override fun toTokens(tokens: TokenStream) {
   tokens.extendTokenStreams(listOf(tokens))
  }
 }
}

/** A field in a struct pattern. */
public data class FieldPat(
 public val member: Member,
 public val colonToken: Colon?,
 public val pat: Pat,
) : ToTokens {
 override fun toTokens(tokens: TokenStream) {
  member.toTokens(tokens)
  colonToken?.toTokens(tokens)
  pat.toTokens(tokens)
 }

 public fun deepCopy(): FieldPat = FieldPat(member, colonToken, pat.deepCopy())
}

/** The `..` in a struct pattern. */
public data class PatRest(
 public val dot2Token: io.github.kotlinmania.syn.token.DotDot?,
) : ToTokens {
 override fun toTokens(tokens: TokenStream) {
  dot2Token?.toTokens(tokens)
 }

 public fun deepCopy(): PatRest = this
}
