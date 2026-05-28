// port-lint: source classify.rs
package io.github.kotlinmania.syn

/**
 * Classification helpers that determine whether an expression
 * requires a semicolon or comma to be unambiguously recognized
 * as a statement or match arm.
 */
internal object Classify {
 /**
  * Returns true when the expression requires a semicolon to be
  * recognized as a statement.
  */
 internal fun requiresSemiToBeStmt(expr: Expr): Boolean =
  requiresCommaToBeMatchArm(expr)

 /**
  * Returns true when the expression requires a comma to be
  * recognized as a match arm.
  */
 internal fun requiresCommaToBeMatchArm(expr: Expr): Boolean =
  when (expr) {
   is Expr.Lit -> true
   is Expr.Path -> true
   is Expr.Verbatim -> true
   else -> false
  }
}
