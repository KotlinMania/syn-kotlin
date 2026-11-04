// port-lint: source classify.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

internal fun requiresSemiToBeStmt(expr: Expr): Boolean =
    when (expr) {
        is Expr.Macro -> !expr.mac.delimiter.isBrace
        else -> requiresCommaToBeMatchArm(expr)
    }

internal fun requiresCommaToBeMatchArm(expr: Expr): Boolean =
    when (expr) {
        is Expr.If,
        is Expr.Match,
        is Expr.BlockExpr,
        is Expr.Unsafe,
        is Expr.While,
        is Expr.Loop,
        is Expr.ForLoop,
        is Expr.TryBlock,
        is Expr.Const,
        -> false

        is Expr.Array,
        is Expr.Assign,
        is Expr.Async,
        is Expr.Await,
        is Expr.Binary,
        is Expr.Break,
        is Expr.Call,
        is Expr.Cast,
        is Expr.Closure,
        is Expr.Continue,
        is Expr.Field,
        is Expr.Group,
        is Expr.Index,
        is Expr.Infer,
        is Expr.Let,
        is Expr.Lit,
        is Expr.Macro,
        is Expr.MethodCall,
        is Expr.Paren,
        is Expr.Path,
        is Expr.Range,
        is Expr.RawAddr,
        is Expr.Reference,
        is Expr.Repeat,
        is Expr.Return,
        is Expr.Struct,
        is Expr.Try,
        is Expr.Tuple,
        is Expr.Unary,
        is Expr.Yield,
        is Expr.Verbatim,
        -> true
    }

private sealed class ClassifyControlFlow {
    class Break(
        val value: Boolean,
    ) : ClassifyControlFlow()

    class Continue(
        val ty: SynType,
    ) : ClassifyControlFlow()
}

internal fun trailingUnparameterizedPath(ty: SynType): Boolean {
    var current = ty
    while (true) {
        when (val node = current) {
            is SynType.FnPtr ->
                when (val out = node.output) {
                    ReturnType.Default -> return false
                    is ReturnType.TypeReturn -> current = out.ty
                }

            is SynType.ImplTrait ->
                when (val step = lastTypeInBoundsForTrailingPath(node.bounds)) {
                    is ClassifyControlFlow.Break -> return step.value
                    is ClassifyControlFlow.Continue -> current = step.ty
                }

            is SynType.Path ->
                when (val step = lastTypeInPathForTrailingPath(node.path)) {
                    is ClassifyControlFlow.Break -> return step.value
                    is ClassifyControlFlow.Continue -> current = step.ty
                }

            is SynType.Ptr -> current = node.elem
            is SynType.Reference -> current = node.elem

            is SynType.TraitObject ->
                when (val step = lastTypeInBoundsForTrailingPath(node.bounds)) {
                    is ClassifyControlFlow.Break -> return step.value
                    is ClassifyControlFlow.Continue -> current = step.ty
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

private fun lastTypeInPathForTrailingPath(path: Path): ClassifyControlFlow {
    val lastSeg =
        path.segments.last()
            ?: error("called `Option::unwrap()` on a `None` value")
    return when (val args = lastSeg.arguments) {
        PathArguments.None -> ClassifyControlFlow.Break(true)
        is PathArguments.AngleBracketed -> ClassifyControlFlow.Break(false)
        is PathArguments.Parenthesized ->
            when (val out = args.output) {
                ReturnType.Default -> ClassifyControlFlow.Break(false)
                is ReturnType.TypeReturn -> ClassifyControlFlow.Continue(out.ty)
            }
    }
}

private fun lastTypeInBoundsForTrailingPath(bounds: TypeParamBoundList): ClassifyControlFlow {
    val lastBound =
        bounds.last()
            ?: error("called `Option::unwrap()` on a `None` value")
    return when (lastBound) {
        is TypeParamBound.Trait -> lastTypeInPathForTrailingPath(lastBound.path)
        is TypeParamBound.LifetimeBound,
        is TypeParamBound.PreciseCapture,
        is TypeParamBound.Verbatim,
        -> ClassifyControlFlow.Break(false)
    }
}

/** Whether the expression's first token is the label of a loop or block. */
internal fun exprLeadingLabel(expr: Expr): Boolean {
    var current = expr
    while (true) {
        when (val node = current) {
            is Expr.BlockExpr -> return node.label != null
            is Expr.ForLoop -> return node.label != null
            is Expr.Loop -> return node.label != null
            is Expr.While -> return node.label != null

            is Expr.Assign -> current = node.left
            is Expr.Await -> current = node.base
            is Expr.Binary -> current = node.left
            is Expr.Call -> current = node.func
            is Expr.Cast -> current = node.expr
            is Expr.Field -> current = node.base
            is Expr.Index -> current = node.expr
            is Expr.MethodCall -> current = node.receiver
            is Expr.Range -> {
                val start = node.start ?: return false
                current = start
            }
            is Expr.Try -> current = node.expr

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

/** Whether the expression's last token is `}`. */
internal fun exprTrailingBrace(expr: Expr): Boolean {
    var current = expr
    while (true) {
        when (val node = current) {
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

            is Expr.Assign -> current = node.right
            is Expr.Binary -> current = node.right
            is Expr.Break -> {
                val inner = node.expr ?: return false
                current = inner
            }
            is Expr.Cast -> return typeTrailingBrace(node.ty)
            is Expr.Closure -> current = node.body
            is Expr.Let -> current = node.expr
            is Expr.Macro -> return node.mac.delimiter.isBrace
            is Expr.Range -> {
                val end = node.end ?: return false
                current = end
            }
            is Expr.RawAddr -> current = node.expr
            is Expr.Reference -> current = node.expr
            is Expr.Return -> {
                val inner = node.expr ?: return false
                current = inner
            }
            is Expr.Unary -> current = node.expr
            is Expr.Verbatim -> return tokensTrailingBrace(node.tokens)
            is Expr.Yield -> {
                val inner = node.expr ?: return false
                current = inner
            }

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

private fun typeTrailingBrace(ty: SynType): Boolean {
    var current = ty
    while (true) {
        when (val node = current) {
            is SynType.FnPtr ->
                when (val out = node.output) {
                    ReturnType.Default -> return false
                    is ReturnType.TypeReturn -> current = out.ty
                }

            is SynType.ImplTrait ->
                when (val step = lastTypeInBoundsForTrailingBrace(node.bounds)) {
                    is ClassifyControlFlow.Break -> return step.value
                    is ClassifyControlFlow.Continue -> current = step.ty
                }

            is SynType.Macro -> return node.mac.delimiter.isBrace

            is SynType.Path -> {
                val next = lastTypeInPathForTrailingBrace(node.path) ?: return false
                current = next
            }

            is SynType.Ptr -> current = node.elem
            is SynType.Reference -> current = node.elem

            is SynType.TraitObject ->
                when (val step = lastTypeInBoundsForTrailingBrace(node.bounds)) {
                    is ClassifyControlFlow.Break -> return step.value
                    is ClassifyControlFlow.Continue -> current = step.ty
                }

            is SynType.Verbatim -> return tokensTrailingBrace(node.tokens)

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

private fun lastTypeInPathForTrailingBrace(path: Path): SynType? {
    val lastSeg =
        path.segments.last()
            ?: error("called `Option::unwrap()` on a `None` value")
    return when (val args = lastSeg.arguments) {
        PathArguments.None,
        is PathArguments.AngleBracketed,
        -> null

        is PathArguments.Parenthesized ->
            when (val out = args.output) {
                ReturnType.Default -> null
                is ReturnType.TypeReturn -> out.ty
            }
    }
}

private fun lastTypeInBoundsForTrailingBrace(bounds: TypeParamBoundList): ClassifyControlFlow {
    val lastBound =
        bounds.last()
            ?: error("called `Option::unwrap()` on a `None` value")
    return when (lastBound) {
        is TypeParamBound.Trait -> {
            val next = lastTypeInPathForTrailingBrace(lastBound.path)
            if (next != null) {
                ClassifyControlFlow.Continue(next)
            } else {
                ClassifyControlFlow.Break(false)
            }
        }

        is TypeParamBound.LifetimeBound,
        is TypeParamBound.PreciseCapture,
        -> ClassifyControlFlow.Break(false)

        is TypeParamBound.Verbatim -> ClassifyControlFlow.Break(tokensTrailingBrace(lastBound.tokens))
    }
}

private fun tokensTrailingBrace(tokens: TokenStream): Boolean {
    val lastTree = tokens.clone().lastOrNull()
    return lastTree is TokenTree.Group && lastTree.value.delimiter() == Delimiter.Brace
}
