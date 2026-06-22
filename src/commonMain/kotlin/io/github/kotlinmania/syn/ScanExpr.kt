// port-lint: source scan_expr.rs
package io.github.kotlinmania.syn

/**
 * Scans ahead in a token stream to determine whether an expression-like
 * sequence of tokens constitutes a valid expression, advancing past it.
 *
 * This is an internal helper used by expression-parsing and printing logic.
 * It walks a state machine over the token stream, consuming tokens that
 * form valid expression prefixes and stopping at the boundary of a complete
 * expression.
 *
 * The simplified implementation delegates to the full expression parser.
 */

internal fun scanExpr(input: ParseStream): SynResult<Unit> {
    if (input.isEmpty()) {
        return SynResult.failure(SynError.new(input.currentCursor.span(), "unexpected end of input"))
    }
    val result = input.call { stream -> parseExprFull(stream) }
    if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
    return SynResult.success(Unit)
}

/** Full expression parser entry point. */
internal fun parseExprFull(input: ParseStream): SynResult<Expr> =
    input.parse(ExprParse)

/** Parse implementation for expressions. */
internal object ExprParse : Parse<Expr> {
    override fun parse(input: ParseStream): SynResult<Expr> {
        // Simplified: parse a path or literal. Full expression parsing
        // will be added as Expr.kt is completed.
        if (input.peek(LitPeek)) {
            val lit = input.parse(LitParse)
            if (lit.isSuccess) return lit.map { Expr.Lit(attrs = emptyList(), lit = it) }
        }
        if (input.peek(IdentPeek)) {
            val ident = input.parse(IdentParse)
            if (ident.isSuccess) {
                val segment = PathSegment.from(ident.getOrThrow())
                val segments = Punctuated<PathSegment, io.github.kotlinmania.syn.token.PathSep>().also { it.add(segment) }
                val path = Path(null, segments)
                return SynResult.success(Expr.Path(attrs = emptyList(), qself = null, path = path))
            }
        }
        return SynResult.failure(input.error("expected expression"))
    }
}
