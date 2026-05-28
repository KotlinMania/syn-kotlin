// port-lint: source stmt.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Let
import io.github.kotlinmania.syn.token.Semi
import io.github.kotlinmania.syn.token.Else
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import kotlin.native.HiddenFromObjC

/**
 * A braced block containing statements.
 */
public data class Block(
 public val braceToken: Brace,
 public val stmts: List<Stmt>,
) : ToTokens {
 override fun toTokens(tokens: TokenStream) {
  braceToken.surround(tokens) { inner ->
   for (stmt in stmts) stmt.toTokens(inner)
  }
 }
}

/**
 * A statement, usually ending in a semicolon.
 */
@HiddenFromObjC
public sealed class Stmt : ToTokens {
 /** A local binding. */
 public data class Local(
  public val attrs: List<Attribute>,
  public val letToken: Let,
  public val pat: Pat,
  public val init: LocalInit?,
  public val semiToken: Semi,
 ) : Stmt() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   letToken.toTokens(tokens)
   pat.toTokens(tokens)
   init?.toTokens(tokens)
   semiToken.toTokens(tokens)
  }
 }

 /** An item definition. */
 public data class ItemStmt(
  public val item: Item,
 ) : Stmt() {
  override fun toTokens(tokens: TokenStream) {
   item.toTokens(tokens)
  }
 }


 /** Expression, with or without trailing semicolon. */
 public data class ExprStmt(
  public val expr: Expr,
  public val semiToken: Semi?,
 ) : Stmt() {
  override fun toTokens(tokens: TokenStream) {
   expr.toTokens(tokens)
   semiToken?.toTokens(tokens)
  }
 }

 /** A macro invocation in statement position. */
 public data class MacroStmt(
  public val attrs: List<Attribute>,
  public val mac: Macro,
  public val semiToken: Semi?,
 ) : Stmt() {
  override fun toTokens(tokens: TokenStream) {
   for (attr in attrs) attr.toTokens(tokens)
   mac.toTokens(tokens)
   semiToken?.toTokens(tokens)
  }
 }
}

/**
 * The expression assigned in a local binding, including optional
 * diverging else block.
 */
public data class LocalInit(
 public val eqToken: io.github.kotlinmania.syn.token.Eq,
 public val expr: Expr,
 public val diverge: Pair<io.github.kotlinmania.syn.token.Else, Expr>?,
) : ToTokens {
 override fun toTokens(tokens: TokenStream) {
  eqToken.toTokens(tokens)
  expr.toTokens(tokens)
  diverge?.let { (elseToken, divergeExpr) ->
   elseToken.toTokens(tokens)
   divergeExpr.toTokens(tokens)
  }
 }
}
