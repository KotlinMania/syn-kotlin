// port-lint: source classify.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

internal fun tokensTrailingBrace(tokens: TokenStream): Boolean =
    (tokens.lastOrNull() as? TokenTree.Group)?.value?.delimiter() == Delimiter.Brace

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

    internal fun trailingUnparameterizedPath(ty: SynType): Boolean {
        fun lastTypeInPath(path: Path): TypeTail =
            when (val args = path.segments.last()?.arguments) {
                PathArguments.None -> TypeTail.Done(true)
                is PathArguments.AngleBracketed -> TypeTail.Done(false)
                is PathArguments.Parenthesized ->
                    when (val output = args.output) {
                        ReturnType.Default -> TypeTail.Done(false)
                        is ReturnType.TypeReturn -> TypeTail.More(output.ty)
                    }
                null -> TypeTail.Done(false)
            }

        fun lastTypeInBounds(bounds: TypeParamBoundList): TypeTail =
            when (val bound = bounds.last()) {
                is TypeParamBound.Trait -> lastTypeInPath(bound.path)
                is TypeParamBound.LifetimeBound,
                is TypeParamBound.PreciseCapture,
                is TypeParamBound.Verbatim,
                null,
                -> TypeTail.Done(false)
            }

        var current = ty
        while (true) {
            current =
                when (current) {
                    is SynType.BareFn ->
                        when (val output = current.output) {
                            ReturnType.Default -> return false
                            is ReturnType.TypeReturn -> output.ty
                        }
                    is SynType.ImplTrait ->
                        when (val next = lastTypeInBounds(current.bounds)) {
                            is TypeTail.Done -> return next.value
                            is TypeTail.More -> next.type
                        }
                    is SynType.Path ->
                        when (val next = lastTypeInPath(current.path)) {
                            is TypeTail.Done -> return next.value
                            is TypeTail.More -> next.type
                        }
                    is SynType.Ptr -> current.elem
                    is SynType.Reference -> current.elem
                    is SynType.TraitObject ->
                        when (val next = lastTypeInBounds(current.bounds)) {
                            is TypeTail.Done -> return next.value
                            is TypeTail.More -> next.type
                        }
                    is SynType.Array,
                    is SynType.Group,
                    is SynType.Infer,
                    is SynType.Macro,
                    is SynType.Never,
                    is SynType.Paren,
                    is SynType.Slice,
                    is SynType.Tuple,
                    is SynType.Verbatim,
                    -> return false
                }
        }
    }

    internal fun exprLeadingLabel(expr: Expr): Boolean {
        var current = expr
        while (true) {
            current =
                when (current) {
                    is Expr.BlockExpr -> return current.label != null
                    is Expr.ForLoop -> return current.label != null
                    is Expr.Loop -> return current.label != null
                    is Expr.While -> return current.label != null
                    is Expr.Assign -> current.left
                    is Expr.Await -> current.base
                    is Expr.Binary -> current.left
                    is Expr.Call -> current.func
                    is Expr.Cast -> current.expr
                    is Expr.Field -> current.base
                    is Expr.Index -> current.expr
                    is Expr.MethodCall -> current.receiver
                    is Expr.Range -> current.start ?: return false
                    is Expr.Try -> current.expr
                    is Expr.Array,
                    is Expr.Async,
                    is Expr.Break,
                    is Expr.Closure,
                    is Expr.Const,
                    is Expr.Continue,
                    is Expr.Group,
                    is Expr.If,
                    is Expr.Infer,
                    is Expr.Let,
                    is Expr.Lit,
                    is Expr.Macro,
                    is Expr.Match,
                    is Expr.Paren,
                    is Expr.Path,
                    is Expr.RawAddr,
                    is Expr.Reference,
                    is Expr.Repeat,
                    is Expr.Return,
                    is Expr.Struct,
                    is Expr.TryBlock,
                    is Expr.Tuple,
                    is Expr.Unary,
                    is Expr.Unsafe,
                    is Expr.Verbatim,
                    is Expr.Yield,
                    -> return false
                }
        }
    }

    internal fun exprTrailingBrace(expr: Expr): Boolean {
        fun tokensTrailingBrace(tokens: TokenStream): Boolean =
            (tokens.lastOrNull() as? TokenTree.Group)?.value?.delimiter() == Delimiter.Brace

        fun typeTrailingBrace(ty: SynType): Boolean {
            fun lastTypeInPath(path: Path): SynType? =
                when (val args = path.segments.last()?.arguments) {
                    PathArguments.None,
                    is PathArguments.AngleBracketed,
                    null,
                    -> null
                    is PathArguments.Parenthesized ->
                        when (val output = args.output) {
                            ReturnType.Default -> null
                            is ReturnType.TypeReturn -> output.ty
                        }
                }

            fun lastTypeInBounds(bounds: TypeParamBoundList): TypeTail =
                when (val bound = bounds.last()) {
                    is TypeParamBound.Trait ->
                        when (val next = lastTypeInPath(bound.path)) {
                            null -> TypeTail.Done(false)
                            else -> TypeTail.More(next)
                        }
                    is TypeParamBound.LifetimeBound,
                    is TypeParamBound.PreciseCapture,
                    null,
                    -> TypeTail.Done(false)
                    is TypeParamBound.Verbatim -> TypeTail.Done(tokensTrailingBrace(bound.tokens))
                }

            var current = ty
            while (true) {
                current =
                    when (current) {
                        is SynType.BareFn ->
                            when (val output = current.output) {
                                ReturnType.Default -> return false
                                is ReturnType.TypeReturn -> output.ty
                            }
                        is SynType.ImplTrait ->
                            when (val next = lastTypeInBounds(current.bounds)) {
                                is TypeTail.Done -> return next.value
                                is TypeTail.More -> next.type
                            }
                        is SynType.Macro -> return current.mac.isBrace()
                        is SynType.Path -> lastTypeInPath(current.path) ?: return false
                        is SynType.Ptr -> current.elem
                        is SynType.Reference -> current.elem
                        is SynType.TraitObject ->
                            when (val next = lastTypeInBounds(current.bounds)) {
                                is TypeTail.Done -> return next.value
                                is TypeTail.More -> next.type
                            }
                        is SynType.Verbatim -> return tokensTrailingBrace(current.tokens)
                        is SynType.Array,
                        is SynType.Group,
                        is SynType.Infer,
                        is SynType.Never,
                        is SynType.Paren,
                        is SynType.Slice,
                        is SynType.Tuple,
                        -> return false
                    }
                }
        }

        var current = expr
        while (true) {
            current =
                when (current) {
                    is Expr.Async,
                    is Expr.BlockExpr,
                    is Expr.Const,
                    is Expr.ForLoop,
                    is Expr.If,
                    is Expr.Loop,
                    is Expr.Match,
                    is Expr.Struct,
                    is Expr.TryBlock,
                    is Expr.Unsafe,
                    is Expr.While,
                    -> return true
                    is Expr.Assign -> current.right
                    is Expr.Binary -> current.right
                    is Expr.Break -> current.expr ?: return false
                    is Expr.Cast -> return typeTrailingBrace(current.ty)
                    is Expr.Closure -> current.body
                    is Expr.Let -> current.expr
                    is Expr.Macro -> return current.mac.isBrace()
                    is Expr.Range -> current.end ?: return false
                    is Expr.RawAddr -> current.expr
                    is Expr.Reference -> current.expr
                    is Expr.Return -> current.expr ?: return false
                    is Expr.Unary -> current.expr
                    is Expr.Verbatim -> return tokensTrailingBrace(current.tokens)
                    is Expr.Yield -> current.expr ?: return false
                    is Expr.Array,
                    is Expr.Await,
                    is Expr.Call,
                    is Expr.Continue,
                    is Expr.Field,
                    is Expr.Group,
                    is Expr.Index,
                    is Expr.Infer,
                    is Expr.Lit,
                    is Expr.MethodCall,
                    is Expr.Paren,
                    is Expr.Path,
                    is Expr.Repeat,
                    is Expr.Try,
                    is Expr.Tuple,
                    -> return false
                }
        }
    }

    private sealed class TypeTail {
        data class Done(val value: Boolean) : TypeTail()
        data class More(val type: SynType) : TypeTail()
    }
}
