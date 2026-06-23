// port-lint: source classify.rs
package io.github.kotlinmania.syn

/**
 * Classification helpers that determine whether an expression
 * requires a semicolon or comma to be unambiguously recognized
 * as a statement or pattern-matching arm.
 */
internal object Classify {
    /**
     * Returns true when the expression requires a semicolon to be
     * recognized as a statement.
     */
    internal fun requiresSemiToBeStmt(expr: Expr): Boolean =
        when (expr) {
            is Expr.Macro -> !expr.mac.delimiter.isBrace
            else -> requiresCommaToBeMatchArm(expr)
        }

    /**
     * Returns true when the expression requires a comma to be
     * recognized as a pattern-matching arm.
     */
    internal fun requiresCommaToBeMatchArm(expr: Expr): Boolean =
        when (expr) {
            is Expr.If -> false
            is Expr.Match -> false
            is Expr.BlockExpr -> false
            is Expr.Unsafe -> false
            is Expr.While -> false
            is Expr.Loop -> false
            is Expr.ForLoop -> false
            is Expr.TryBlock -> false
            is Expr.Const -> false

            is Expr.Array -> true
            is Expr.Assign -> true
            is Expr.Async -> true
            is Expr.Await -> true
            is Expr.Binary -> true
            is Expr.Break -> true
            is Expr.Call -> true
            is Expr.Cast -> true
            is Expr.Closure -> true
            is Expr.Continue -> true
            is Expr.Field -> true
            is Expr.Group -> true
            is Expr.Index -> true
            is Expr.Infer -> true
            is Expr.Let -> true
            is Expr.Lit -> true
            is Expr.Macro -> true
            is Expr.MethodCall -> true
            is Expr.Paren -> true
            is Expr.Path -> true
            is Expr.RawAddr -> true
            is Expr.Range -> true

            is Expr.Reference -> true
            is Expr.Repeat -> true
            is Expr.Return -> true
            is Expr.Struct -> true
            is Expr.Try -> true
            is Expr.Tuple -> true
            is Expr.Unary -> true
            is Expr.Yield -> true
            is Expr.Verbatim -> true
        }
}
