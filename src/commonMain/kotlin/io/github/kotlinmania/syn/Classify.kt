// port-lint: source classify.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

// Generated classifiers: upstream classify.rs:16-68. Raw output and integration
// receipt: rust-kotlin/tmp/syn-emission-06-txkhdd7f/adoption/.
// Generated from Syn AST; exact original retained in input.rs.
// Rust use resolved through explicit library bindings.
internal fun `requires_semi_to_be_stmt`(`expr`: io.github.kotlinmania.syn.Expr): kotlin.Boolean =
    (when (val __rtkValue0: io.github.kotlinmania.syn.Expr = `expr`) {
        is io.github.kotlinmania.syn.Expr.Macro ->
            kotlin.run {
                val `expr__binding1`: io.github.kotlinmania.syn.Expr.Macro = __rtkValue0
                (!kotlin.run {
                    val __rtkValue2 = ((`expr__binding1`).`mac`).`delimiter`
                    __rtkValue2 is io.github.kotlinmania.syn.MacroDelimiter.Brace
                })
            }
        else -> `requires_comma_to_be_match_arm`(`expr`)
    })

internal fun `requires_comma_to_be_match_arm`(
    `expr`: io.github.kotlinmania.syn.Expr,
): kotlin.Boolean =
    (when (val __rtkValue0: io.github.kotlinmania.syn.Expr = `expr`) {
        is io.github.kotlinmania.syn.Expr.If,
        is io.github.kotlinmania.syn.Expr.Match,
        is io.github.kotlinmania.syn.Expr.BlockExpr,
        is io.github.kotlinmania.syn.Expr.Unsafe // both under ExprKind::Block in rustc
        ,
        is io.github.kotlinmania.syn.Expr.While,
        is io.github.kotlinmania.syn.Expr.Loop,
        is io.github.kotlinmania.syn.Expr.ForLoop,
        is io.github.kotlinmania.syn.Expr.TryBlock,
        is io.github.kotlinmania.syn.Expr.Const -> false
        is io.github.kotlinmania.syn.Expr.Array,
        is io.github.kotlinmania.syn.Expr.Assign,
        is io.github.kotlinmania.syn.Expr.Async,
        is io.github.kotlinmania.syn.Expr.Await,
        is io.github.kotlinmania.syn.Expr.Binary,
        is io.github.kotlinmania.syn.Expr.Break,
        is io.github.kotlinmania.syn.Expr.Call,
        is io.github.kotlinmania.syn.Expr.Cast,
        is io.github.kotlinmania.syn.Expr.Closure,
        is io.github.kotlinmania.syn.Expr.Continue,
        is io.github.kotlinmania.syn.Expr.Field,
        is io.github.kotlinmania.syn.Expr.Group,
        is io.github.kotlinmania.syn.Expr.Index,
        is io.github.kotlinmania.syn.Expr.Infer,
        is io.github.kotlinmania.syn.Expr.Let,
        is io.github.kotlinmania.syn.Expr.Lit,
        is io.github.kotlinmania.syn.Expr.Macro,
        is io.github.kotlinmania.syn.Expr.MethodCall,
        is io.github.kotlinmania.syn.Expr.Paren,
        is io.github.kotlinmania.syn.Expr.Path,
        is io.github.kotlinmania.syn.Expr.Range,
        is io.github.kotlinmania.syn.Expr.RawAddr,
        is io.github.kotlinmania.syn.Expr.Reference,
        is io.github.kotlinmania.syn.Expr.Repeat,
        is io.github.kotlinmania.syn.Expr.Return,
        is io.github.kotlinmania.syn.Expr.Struct,
        is io.github.kotlinmania.syn.Expr.Try,
        is io.github.kotlinmania.syn.Expr.Tuple,
        is io.github.kotlinmania.syn.Expr.Unary,
        is io.github.kotlinmania.syn.Expr.Yield,
        is io.github.kotlinmania.syn.Expr.Verbatim -> true
    })
// Third generated classifier: unchanged input with printing:on/full:off.
// Raw output, alpha-renaming/privacy/format integration patch and Rust receipt:
// rust-kotlin/tmp/syn-emission-08-u6iwcz6i/adoption/.
// Generated from Syn AST; exact original retained in input.rs.
// Rust cfg item excluded by explicit configuration; original retained in input.rs.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust cfg item excluded by explicit configuration; original retained in input.rs.
// Rust cfg item excluded by explicit configuration; original retained in input.rs.
// Rust cfg item excluded by explicit configuration; original retained in input.rs.
internal fun `trailing_unparameterized_path`(
    `ty`: io.github.kotlinmania.syn.SynType,
): kotlin.Boolean {
    kotlin.run {
        var `ty__binding0`: io.github.kotlinmania.syn.SynType = `ty`
        kotlin.run {
            kotlin.run {
                while (true) (when (
                    val __rtkClassifyValue1: io.github.kotlinmania.syn.SynType = `ty__binding0`
                ) {
                    is io.github.kotlinmania.syn.SynType.BareFn ->
                        kotlin.run {
                            val `t`: io.github.kotlinmania.syn.SynType.BareFn = __rtkClassifyValue1
                            (when (
                                val __rtkClassifyValue2: io.github.kotlinmania.syn.ReturnType =
                                    (`t`).`output`
                            ) {
                                io.github.kotlinmania.syn.ReturnType.Default -> return false
                                is io.github.kotlinmania.syn.ReturnType.TypeReturn ->
                                    kotlin.run {
                                        val `ret`: io.github.kotlinmania.syn.SynType =
                                            __rtkClassifyValue2.`ty`
                                        kotlin.run {
                                            val __rtkClassifyValue3:
                                                io.github.kotlinmania.syn.SynType =
                                                `ret`
                                            `ty__binding0` = __rtkClassifyValue3
                                        }
                                    }
                            })
                        }
                    is io.github.kotlinmania.syn.SynType.ImplTrait ->
                        kotlin.run {
                            val `t__binding4`: io.github.kotlinmania.syn.SynType.ImplTrait =
                                __rtkClassifyValue1
                            (when (
                                val __rtkClassifyValue5:
                                    __rtkClassifyControlFlow<
                                        kotlin.Boolean,
                                        io.github.kotlinmania.syn.SynType,
                                    > =
                                    `__rtkClassifyNested1_last_type_in_bounds`(
                                        (`t__binding4`).`bounds`,
                                    )
                            ) {
                                is __rtkClassifyControlFlow.Break ->
                                    kotlin.run {
                                        val `trailing_path`: kotlin.Boolean =
                                            __rtkClassifyValue5.`value`
                                        return `trailing_path`
                                    }
                                is __rtkClassifyControlFlow.Continue ->
                                    kotlin.run {
                                        val `t__binding6`: io.github.kotlinmania.syn.SynType =
                                            __rtkClassifyValue5.`value`
                                        kotlin.run {
                                            val __rtkClassifyValue7:
                                                io.github.kotlinmania.syn.SynType =
                                                `t__binding6`
                                            `ty__binding0` = __rtkClassifyValue7
                                        }
                                    }
                            })
                        }
                    is io.github.kotlinmania.syn.SynType.Path ->
                        kotlin.run {
                            val `t__binding8`: io.github.kotlinmania.syn.SynType.Path =
                                __rtkClassifyValue1
                            (when (
                                val __rtkClassifyValue9:
                                    __rtkClassifyControlFlow<
                                        kotlin.Boolean,
                                        io.github.kotlinmania.syn.SynType,
                                    > =
                                    `__rtkClassifyNested0_last_type_in_path`((`t__binding8`).`path`)
                            ) {
                                is __rtkClassifyControlFlow.Break ->
                                    kotlin.run {
                                        val `trailing_path__binding10`: kotlin.Boolean =
                                            __rtkClassifyValue9.`value`
                                        return `trailing_path__binding10`
                                    }
                                is __rtkClassifyControlFlow.Continue ->
                                    kotlin.run {
                                        val `t__binding11`: io.github.kotlinmania.syn.SynType =
                                            __rtkClassifyValue9.`value`
                                        kotlin.run {
                                            val __rtkClassifyValue12:
                                                io.github.kotlinmania.syn.SynType =
                                                `t__binding11`
                                            `ty__binding0` = __rtkClassifyValue12
                                        }
                                    }
                            })
                        }
                    is io.github.kotlinmania.syn.SynType.Ptr ->
                        kotlin.run {
                            val `t__binding13`: io.github.kotlinmania.syn.SynType.Ptr =
                                __rtkClassifyValue1
                            kotlin.run {
                                val __rtkClassifyValue14: io.github.kotlinmania.syn.SynType =
                                    (`t__binding13`).`elem`
                                `ty__binding0` = __rtkClassifyValue14
                            }
                        }
                    is io.github.kotlinmania.syn.SynType.Reference ->
                        kotlin.run {
                            val `t__binding15`: io.github.kotlinmania.syn.SynType.Reference =
                                __rtkClassifyValue1
                            kotlin.run {
                                val __rtkClassifyValue16: io.github.kotlinmania.syn.SynType =
                                    (`t__binding15`).`elem`
                                `ty__binding0` = __rtkClassifyValue16
                            }
                        }
                    is io.github.kotlinmania.syn.SynType.TraitObject ->
                        kotlin.run {
                            val `t__binding17`: io.github.kotlinmania.syn.SynType.TraitObject =
                                __rtkClassifyValue1
                            (when (
                                val __rtkClassifyValue18:
                                    __rtkClassifyControlFlow<
                                        kotlin.Boolean,
                                        io.github.kotlinmania.syn.SynType,
                                    > =
                                    `__rtkClassifyNested1_last_type_in_bounds`(
                                        (`t__binding17`).`bounds`,
                                    )
                            ) {
                                is __rtkClassifyControlFlow.Break ->
                                    kotlin.run {
                                        val `trailing_path__binding19`: kotlin.Boolean =
                                            __rtkClassifyValue18.`value`
                                        return `trailing_path__binding19`
                                    }
                                is __rtkClassifyControlFlow.Continue ->
                                    kotlin.run {
                                        val `t__binding20`: io.github.kotlinmania.syn.SynType =
                                            __rtkClassifyValue18.`value`
                                        kotlin.run {
                                            val __rtkClassifyValue21:
                                                io.github.kotlinmania.syn.SynType =
                                                `t__binding20`
                                            `ty__binding0` = __rtkClassifyValue21
                                        }
                                    }
                            })
                        }
                    is io.github.kotlinmania.syn.SynType.Array,
                    is io.github.kotlinmania.syn.SynType.Group,
                    is io.github.kotlinmania.syn.SynType.Infer,
                    is io.github.kotlinmania.syn.SynType.Macro,
                    is io.github.kotlinmania.syn.SynType.Never,
                    is io.github.kotlinmania.syn.SynType.Paren,
                    is io.github.kotlinmania.syn.SynType.Slice,
                    is io.github.kotlinmania.syn.SynType.Tuple,
                    is io.github.kotlinmania.syn.SynType.Verbatim -> return false
                })

                kotlin.error("unreachable: Rust loop has no exiting break")
            }
            // Rust nested function last_type_in_path emitted at module scope as
            // __rtkClassifyNested0_last_type_in_path.
            // Rust nested function last_type_in_bounds emitted at module scope as
            // __rtkClassifyNested1_last_type_in_bounds.
        }
    }
}

private fun `__rtkClassifyNested0_last_type_in_path`(
    `path`: io.github.kotlinmania.syn.Path,
): __rtkClassifyControlFlow<kotlin.Boolean, io.github.kotlinmania.syn.SynType> =
    (when (
        val __rtkClassifyValue1: io.github.kotlinmania.syn.PathArguments =
            (__rtkClassifyUnwrap(
                    kotlin.run {
                        val __rtkClassifyValue0 = ((`path`).`segments`).last()
                        if (__rtkClassifyValue0 == null) __rtkClassifyOption.None
                        else __rtkClassifyOption.Some(__rtkClassifyValue0)
                    },
                ))
                .`arguments`
    ) {
        io.github.kotlinmania.syn.PathArguments.None -> __rtkClassifyControlFlow.Break(true)
        is io.github.kotlinmania.syn.PathArguments.AngleBracketed ->
            __rtkClassifyControlFlow.Break(false)
        is io.github.kotlinmania.syn.PathArguments.Parenthesized ->
            kotlin.run {
                val `arg`: io.github.kotlinmania.syn.PathArguments.Parenthesized =
                    __rtkClassifyValue1
                (when (
                    val __rtkClassifyValue2: io.github.kotlinmania.syn.ReturnType = (`arg`).`output`
                ) {
                    io.github.kotlinmania.syn.ReturnType.Default ->
                        __rtkClassifyControlFlow.Break(false)
                    is io.github.kotlinmania.syn.ReturnType.TypeReturn ->
                        kotlin.run {
                            val `ret`: io.github.kotlinmania.syn.SynType = __rtkClassifyValue2.`ty`
                            __rtkClassifyControlFlow.Continue(`ret`)
                        }
                })
            }
    })

private fun `__rtkClassifyNested1_last_type_in_bounds`(
    `bounds`: io.github.kotlinmania.syn.TypeParamBoundList,
): __rtkClassifyControlFlow<kotlin.Boolean, io.github.kotlinmania.syn.SynType> =
    (when (
        val __rtkClassifyValue1: io.github.kotlinmania.syn.TypeParamBound =
            __rtkClassifyUnwrap(
                kotlin.run {
                    val __rtkClassifyValue0 = (`bounds`).last()
                    if (__rtkClassifyValue0 == null) __rtkClassifyOption.None
                    else __rtkClassifyOption.Some(__rtkClassifyValue0)
                },
            )
    ) {
        is io.github.kotlinmania.syn.TypeParamBound.Trait ->
            kotlin.run {
                val `t`: io.github.kotlinmania.syn.TypeParamBound.Trait = __rtkClassifyValue1
                `__rtkClassifyNested0_last_type_in_path`((`t`).`path`)
            }
        is io.github.kotlinmania.syn.TypeParamBound.LifetimeBound,
        is io.github.kotlinmania.syn.TypeParamBound.PreciseCapture,
        is io.github.kotlinmania.syn.TypeParamBound.Verbatim ->
            __rtkClassifyControlFlow.Break(false)
    })

// Rust cfg item excluded by explicit configuration; original retained in input.rs.
// Rust cfg item excluded by explicit configuration; original retained in input.rs.

// Rust core::ops::ControlFlow<B, C>: continuation and break payloads stay distinct.
private sealed class __rtkClassifyControlFlow<out B, out C> {
    class Continue<C>(val value: C) : __rtkClassifyControlFlow<kotlin.Nothing, C>()

    class Break<B>(val value: B) : __rtkClassifyControlFlow<B, kotlin.Nothing>()
}

// Tagged Rust Option; owned payloads require a checked fresh-value context.
private sealed class __rtkClassifyOption<out T> {
    data object None : __rtkClassifyOption<kotlin.Nothing>()

    data class Some<T>(val value: T) : __rtkClassifyOption<T>()
}

private fun <T> __rtkClassifyUnwrapOr(value: __rtkClassifyOption<T>, fallback: T): T =
    when (value) {
        __rtkClassifyOption.None -> fallback
        is __rtkClassifyOption.Some -> value.value
    }

private class __rtkClassifyPanic(message: String) : kotlin.RuntimeException(message)

private fun <T> __rtkClassifyUnwrap(value: __rtkClassifyOption<T>): T =
    when (value) {
        is __rtkClassifyOption.Some -> value.value
        __rtkClassifyOption.None ->
            throw __rtkClassifyPanic("called `Option::unwrap()` on a `None` value")
    }

// End generated classifiers. Remaining helpers await full-file generation.

internal fun tokensTrailingBrace(tokens: TokenStream): Boolean =
    (tokens.lastOrNull() as? TokenTree.Group)?.value?.delimiter() == Delimiter.Brace

internal object Classify {
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
                    -> {
                        null
                    }

                    is PathArguments.Parenthesized -> {
                        when (val output = args.output) {
                            ReturnType.Default -> null
                            is ReturnType.TypeReturn -> output.ty
                        }
                    }
                }

            fun lastTypeInBounds(bounds: TypeParamBoundList): TypeTail =
                when (val bound = bounds.last()) {
                    is TypeParamBound.Trait -> {
                        when (val next = lastTypeInPath(bound.path)) {
                            null -> TypeTail.Done(false)
                            else -> TypeTail.More(next)
                        }
                    }

                    is TypeParamBound.LifetimeBound,
                    is TypeParamBound.PreciseCapture,
                    null,
                    -> {
                        TypeTail.Done(false)
                    }

                    is TypeParamBound.Verbatim -> {
                        TypeTail.Done(tokensTrailingBrace(bound.tokens))
                    }
                }

            var current = ty
            while (true) {
                current =
                    when (current) {
                        is SynType.BareFn -> {
                            when (val output = current.output) {
                                ReturnType.Default -> return false
                                is ReturnType.TypeReturn -> output.ty
                            }
                        }

                        is SynType.ImplTrait -> {
                            when (val next = lastTypeInBounds(current.bounds)) {
                                is TypeTail.Done -> return next.value
                                is TypeTail.More -> next.type
                            }
                        }

                        is SynType.Macro -> {
                            return current.mac.isBrace()
                        }

                        is SynType.Path -> {
                            lastTypeInPath(current.path) ?: return false
                        }

                        is SynType.Ptr -> {
                            current.elem
                        }

                        is SynType.Reference -> {
                            current.elem
                        }

                        is SynType.TraitObject -> {
                            when (val next = lastTypeInBounds(current.bounds)) {
                                is TypeTail.Done -> return next.value
                                is TypeTail.More -> next.type
                            }
                        }

                        is SynType.Verbatim -> {
                            return tokensTrailingBrace(current.tokens)
                        }

                        is SynType.Array,
                        is SynType.Group,
                        is SynType.Infer,
                        is SynType.Never,
                        is SynType.Paren,
                        is SynType.Slice,
                        is SynType.Tuple,
                        -> {
                            return false
                        }
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
        data class Done(
            val value: Boolean,
        ) : TypeTail()

        data class More(
            val type: SynType,
        ) : TypeTail()
    }
}
