// port-lint: source item.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Bracket
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Semi
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import kotlin.native.HiddenFromObjC

/**
 * Things that can appear directly inside of a module or scope.
 */
@HiddenFromObjC
public sealed class Item : ToTokens {
 /** A constant item: `const MAX: UShort = 65535`. */
 public data class Const(
  public val attrs: List<Attribute>,
  public val vis: Visibility,
  public val constToken: io.github.kotlinmania.syn.token.Const,
  public val ident: Ident,
  public val colonToken: Colon?,
  public val ty: SynType,
  public val eqToken: Eq?,
  public val expr: Expr?,
  public val semiToken: Semi,
 ) : Item() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   vis.toTokens(tokens)
   constToken.toTokens(tokens)
   ident.toTokens(tokens)
   colonToken?.toTokens(tokens)
   ty.toTokens(tokens)
   eqToken?.toTokens(tokens)
   expr?.toTokens(tokens)
   semiToken.toTokens(tokens)
  }
 }

 /** An enum definition. */
 public data class Enum(
  public val attrs: List<Attribute>,
  public val vis: Visibility,
  public val enumToken: io.github.kotlinmania.syn.token.Enum,
  public val ident: Ident,
  public val generics: Generics,
  public val braceToken: Brace,
  public val variants: Punctuated<Variant, Comma>,
 ) : Item() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   vis.toTokens(tokens)
   enumToken.toTokens(tokens)
   ident.toTokens(tokens)
   generics.toTokens(tokens)
   braceToken.surround(tokens) { inner ->
    for ((variant, comma) in variants.pairs()) {
     variant.toTokens(inner)
     comma?.toTokens(inner)
    }
   }
  }
 }

 /** A free-standing function. */
 public data class Fn(
  public val attrs: List<Attribute>,
  public val vis: Visibility,
  public val fnToken: io.github.kotlinmania.syn.token.Fn,
  public val ident: Ident,
  public val generics: Generics,
  public val parenToken: Paren,
  public val inputs: Punctuated<FnArg, Comma>,
  public val output: ReturnType?,
  public val block: Block?,
 ) : Item() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   vis.toTokens(tokens)
   fnToken.toTokens(tokens)
   ident.toTokens(tokens)
   generics.toTokens(tokens)
   parenToken.surround(tokens) { inner ->
    for ((arg, comma) in inputs.pairs()) {
     arg.toTokens(inner)
     comma?.toTokens(inner)
    }
   }
   output?.toTokens(tokens)
   block?.toTokens(tokens)
  }
 }

 /** A data class definition. */
 public data class Struct(
  public val attrs: List<Attribute>,
  public val vis: Visibility,
  public val structToken: io.github.kotlinmania.syn.token.Struct,
  public val ident: Ident,
  public val generics: Generics,
  public val fields: Fields,
  public val semiToken: Semi?,
 ) : Item() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   vis.toTokens(tokens)
   structToken.toTokens(tokens)
   ident.toTokens(tokens)
   generics.toTokens(tokens)
   fields.toTokens(tokens)
   semiToken?.toTokens(tokens)
  }
 }

 /** A module or module declaration. */
 public data class Mod(
  public val attrs: List<Attribute>,
  public val vis: Visibility,
  public val modToken: io.github.kotlinmania.syn.token.Mod,
  public val ident: Ident,
  public val content: ModContent?,
 ) : Item() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   vis.toTokens(tokens)
   modToken.toTokens(tokens)
   ident.toTokens(tokens)
   content?.toTokens(tokens)
  }
 }

 /** A use declaration. */
 public data class Use(
  public val attrs: List<Attribute>,
  public val vis: Visibility,
  public val useToken: io.github.kotlinmania.syn.token.Use,
  public val tree: UseTree,
 ) : Item() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   vis.toTokens(tokens)
   useToken.toTokens(tokens)
   tree.toTokens(tokens)
  }
 }

 /** Tokens forming an item not interpreted by Syn. */
 public data class Verbatim(
  public val tokens: TokenStream,
 ) : Item() {
  override fun toTokens(tokens: TokenStream) {
   tokens.extendTokenStreams(listOf(tokens))
  }
 }
}

/** A function argument. */
public data class FnArg(
 public val attrs: List<Attribute>,
 public val pat: Pat,
 public val ty: SynType,
) : ToTokens {
 override fun toTokens(tokens: TokenStream) {
  for (attr in attrs) attr.toTokens(tokens)
  pat.toTokens(tokens)
  ty.toTokens(tokens)
 }
}

/** Module content: either an inline block or just a semicolon. */
public sealed class ModContent : ToTokens {
 public data class Unnamed(val semiToken: Semi) : ModContent() {
  override fun toTokens(tokens: TokenStream) {
   semiToken.toTokens(tokens)
  }
 }
 public data class Inline(val braceToken: Brace, val items: List<Item>) : ModContent() {
  override fun toTokens(tokens: TokenStream) {
   braceToken.surround(tokens) { inner ->
    for (item in items) item.toTokens(inner)
   }
  }
 }
}

/** A use tree in a use declaration. */
public sealed class UseTree : ToTokens {
 public data class Path(val ident: Ident, val colon2Token: io.github.kotlinmania.syn.token.PathSep?, val tree: UseTree?) : UseTree() {
  override fun toTokens(tokens: TokenStream) {
   ident.toTokens(tokens)
   colon2Token?.toTokens(tokens)
   tree?.toTokens(tokens)
  }
 }
 public data class Name(val ident: Ident, val rename: Pair<io.github.kotlinmania.syn.token.As, Ident>?) : UseTree() {
  override fun toTokens(tokens: TokenStream) {
   ident.toTokens(tokens)
   rename?.let { (asToken, renameIdent) -> asToken.toTokens(tokens); renameIdent.toTokens(tokens) }
  }
 }
 public data class Group(val braceToken: Brace, val items: Punctuated<UseTree, Comma>) : UseTree() {
  override fun toTokens(tokens: TokenStream) {
   braceToken.surround(tokens) { inner ->
    for ((item, comma) in items.pairs()) {
     item.toTokens(inner)
     comma?.toTokens(inner)
    }
   }
  }
 }
 public data class Glob(val starToken: io.github.kotlinmania.syn.token.Star) : UseTree() {
  override fun toTokens(tokens: TokenStream) {
   starToken.toTokens(tokens)
  }
 }
}
