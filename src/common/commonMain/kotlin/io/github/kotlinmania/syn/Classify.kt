// port-lint: source classify.rs
@file:Suppress("ClassNaming", "UnusedPrivateFunction", "VarCouldBeVal")

package io.github.kotlinmania.syn

// Complete generated classify.rs: full:on, printing:on; no items excluded.
// Raw source/output, source mappings and integration patch:
// rust-kotlin/tmp/syn-emission-09-y_ypslip/adoption/.
// Generated from Syn AST; exact original retained in input.rs.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
internal fun `requires_semi_to_be_stmt`(`expr`: io.github.kotlinmania.syn.Expr): kotlin.Boolean =
    (
        when (val __rtkClassifyValue0: io.github.kotlinmania.syn.Expr = `expr`) {
            is io.github.kotlinmania.syn.Expr.Macro ->
                kotlin.run {
                    val `expr__binding1`: io.github.kotlinmania.syn.Expr.Macro = __rtkClassifyValue0
                    (
                        !kotlin.run {
                            val __rtkClassifyValue2 = ((`expr__binding1`).`mac`).`delimiter`
                            __rtkClassifyValue2 is io.github.kotlinmania.syn.MacroDelimiter.Brace
                        }
                    )
                }
            else -> `requires_comma_to_be_match_arm`(`expr`)
        }
    )

internal fun `requires_comma_to_be_match_arm`(
    `expr`: io.github.kotlinmania.syn.Expr,
): kotlin.Boolean =
    (
        when (`expr`) {
            is io.github.kotlinmania.syn.Expr.If,
            is io.github.kotlinmania.syn.Expr.Match,
            is io.github.kotlinmania.syn.Expr.BlockExpr,
            is io.github.kotlinmania.syn.Expr.Unsafe, // both under ExprKind::Block in rustc
            is io.github.kotlinmania.syn.Expr.While,
            is io.github.kotlinmania.syn.Expr.Loop,
            is io.github.kotlinmania.syn.Expr.ForLoop,
            is io.github.kotlinmania.syn.Expr.TryBlock,
            is io.github.kotlinmania.syn.Expr.Const,
            -> false
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
            is io.github.kotlinmania.syn.Expr.Verbatim,
            -> true
        }
    )

internal fun `trailing_unparameterized_path`(
    `ty`: io.github.kotlinmania.syn.SynType,
): kotlin.Boolean {
    kotlin.run {
        var `ty__binding0`: io.github.kotlinmania.syn.SynType = `ty`
        kotlin.run {
            kotlin.run {
                while (true) {
                    (
                        when (
                            val __rtkClassifyValue1: io.github.kotlinmania.syn.SynType = `ty__binding0`
                        ) {
                            is io.github.kotlinmania.syn.SynType.BareFn ->
                                kotlin.run {
                                    val `t`: io.github.kotlinmania.syn.SynType.BareFn = __rtkClassifyValue1
                                    (
                                        when (
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
                                        }
                                    )
                                }
                            is io.github.kotlinmania.syn.SynType.ImplTrait ->
                                kotlin.run {
                                    val `t__binding4`: io.github.kotlinmania.syn.SynType.ImplTrait =
                                        __rtkClassifyValue1
                                    (
                                        when (
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
                                        }
                                    )
                                }
                            is io.github.kotlinmania.syn.SynType.Path ->
                                kotlin.run {
                                    val `t__binding8`: io.github.kotlinmania.syn.SynType.Path =
                                        __rtkClassifyValue1
                                    (
                                        when (
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
                                        }
                                    )
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
                                    (
                                        when (
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
                                        }
                                    )
                                }
                            is io.github.kotlinmania.syn.SynType.Array,
                            is io.github.kotlinmania.syn.SynType.Group,
                            is io.github.kotlinmania.syn.SynType.Infer,
                            is io.github.kotlinmania.syn.SynType.Macro,
                            is io.github.kotlinmania.syn.SynType.Never,
                            is io.github.kotlinmania.syn.SynType.Paren,
                            is io.github.kotlinmania.syn.SynType.Slice,
                            is io.github.kotlinmania.syn.SynType.Tuple,
                            is io.github.kotlinmania.syn.SynType.Verbatim,
                            -> return false
                        }
                    )
                }
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
    (
        when (
            val __rtkClassifyValue1: io.github.kotlinmania.syn.PathArguments =
                (
                    __rtkClassifyUnwrap(
                        kotlin.run {
                            val __rtkClassifyValue0 = ((`path`).`segments`).last()
                            if (__rtkClassifyValue0 == null) {
                                __rtkClassifyOption.None
                            } else {
                                __rtkClassifyOption.Some(__rtkClassifyValue0)
                            }
                        },
                    )
                ).`arguments`
        ) {
            io.github.kotlinmania.syn.PathArguments.None -> __rtkClassifyControlFlow.Break(true)
            is io.github.kotlinmania.syn.PathArguments.AngleBracketed ->
                __rtkClassifyControlFlow.Break(false)
            is io.github.kotlinmania.syn.PathArguments.Parenthesized ->
                kotlin.run {
                    val `arg`: io.github.kotlinmania.syn.PathArguments.Parenthesized =
                        __rtkClassifyValue1
                    (
                        when (
                            val __rtkClassifyValue2: io.github.kotlinmania.syn.ReturnType = (`arg`).`output`
                        ) {
                            io.github.kotlinmania.syn.ReturnType.Default ->
                                __rtkClassifyControlFlow.Break(false)
                            is io.github.kotlinmania.syn.ReturnType.TypeReturn ->
                                kotlin.run {
                                    val `ret`: io.github.kotlinmania.syn.SynType = __rtkClassifyValue2.`ty`
                                    __rtkClassifyControlFlow.Continue(`ret`)
                                }
                        }
                    )
                }
        }
    )

private fun `__rtkClassifyNested1_last_type_in_bounds`(
    `bounds`: io.github.kotlinmania.syn.TypeParamBoundList,
): __rtkClassifyControlFlow<kotlin.Boolean, io.github.kotlinmania.syn.SynType> =
    (
        when (
            val __rtkClassifyValue1: io.github.kotlinmania.syn.TypeParamBound =
                __rtkClassifyUnwrap(
                    kotlin.run {
                        val __rtkClassifyValue0 = (`bounds`).last()
                        if (__rtkClassifyValue0 == null) {
                            __rtkClassifyOption.None
                        } else {
                            __rtkClassifyOption.Some(__rtkClassifyValue0)
                        }
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
            is io.github.kotlinmania.syn.TypeParamBound.Verbatim,
            ->
                __rtkClassifyControlFlow.Break(false)
        }
    )

// / Whether the expression's first token is the label of a loop/block.
internal fun `expr_leading_label`(`expr`: io.github.kotlinmania.syn.Expr): kotlin.Boolean {
    kotlin.run {
        var `expr__binding0`: io.github.kotlinmania.syn.Expr = `expr`
        kotlin.run {
            while (true) {
                (
                    when (
                        val __rtkClassifyValue1: io.github.kotlinmania.syn.Expr = `expr__binding0`
                    ) {
                        is io.github.kotlinmania.syn.Expr.BlockExpr ->
                            kotlin.run {
                                val `e`: io.github.kotlinmania.syn.Expr.BlockExpr = __rtkClassifyValue1
                                return (
                                    kotlin.run {
                                        val __rtkClassifyValue2 = (`e`).`label`
                                        if (__rtkClassifyValue2 == null) {
                                            __rtkClassifyOption.None
                                        } else {
                                            __rtkClassifyOption.Some(__rtkClassifyValue2)
                                        }
                                    } is __rtkClassifyOption.Some
                                )
                            }
                        is io.github.kotlinmania.syn.Expr.ForLoop ->
                            kotlin.run {
                                val `e__binding3`: io.github.kotlinmania.syn.Expr.ForLoop =
                                    __rtkClassifyValue1
                                return (
                                    kotlin.run {
                                        val __rtkClassifyValue4 = (`e__binding3`).`label`
                                        if (__rtkClassifyValue4 == null) {
                                            __rtkClassifyOption.None
                                        } else {
                                            __rtkClassifyOption.Some(__rtkClassifyValue4)
                                        }
                                    } is __rtkClassifyOption.Some
                                )
                            }
                        is io.github.kotlinmania.syn.Expr.Loop ->
                            kotlin.run {
                                val `e__binding5`: io.github.kotlinmania.syn.Expr.Loop = __rtkClassifyValue1
                                return (
                                    kotlin.run {
                                        val __rtkClassifyValue6 = (`e__binding5`).`label`
                                        if (__rtkClassifyValue6 == null) {
                                            __rtkClassifyOption.None
                                        } else {
                                            __rtkClassifyOption.Some(__rtkClassifyValue6)
                                        }
                                    } is __rtkClassifyOption.Some
                                )
                            }
                        is io.github.kotlinmania.syn.Expr.While ->
                            kotlin.run {
                                val `e__binding7`: io.github.kotlinmania.syn.Expr.While =
                                    __rtkClassifyValue1
                                return (
                                    kotlin.run {
                                        val __rtkClassifyValue8 = (`e__binding7`).`label`
                                        if (__rtkClassifyValue8 == null) {
                                            __rtkClassifyOption.None
                                        } else {
                                            __rtkClassifyOption.Some(__rtkClassifyValue8)
                                        }
                                    } is __rtkClassifyOption.Some
                                )
                            }
                        is io.github.kotlinmania.syn.Expr.Assign ->
                            kotlin.run {
                                val `e__binding9`: io.github.kotlinmania.syn.Expr.Assign =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue10: io.github.kotlinmania.syn.Expr =
                                        (`e__binding9`).`left`
                                    `expr__binding0` = __rtkClassifyValue10
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Await ->
                            kotlin.run {
                                val `e__binding11`: io.github.kotlinmania.syn.Expr.Await =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue12: io.github.kotlinmania.syn.Expr =
                                        (`e__binding11`).`base`
                                    `expr__binding0` = __rtkClassifyValue12
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Binary ->
                            kotlin.run {
                                val `e__binding13`: io.github.kotlinmania.syn.Expr.Binary =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue14: io.github.kotlinmania.syn.Expr =
                                        (`e__binding13`).`left`
                                    `expr__binding0` = __rtkClassifyValue14
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Call ->
                            kotlin.run {
                                val `e__binding15`: io.github.kotlinmania.syn.Expr.Call =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue16: io.github.kotlinmania.syn.Expr =
                                        (`e__binding15`).`func`
                                    `expr__binding0` = __rtkClassifyValue16
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Cast ->
                            kotlin.run {
                                val `e__binding17`: io.github.kotlinmania.syn.Expr.Cast =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue18: io.github.kotlinmania.syn.Expr =
                                        (`e__binding17`).`expr`
                                    `expr__binding0` = __rtkClassifyValue18
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Field ->
                            kotlin.run {
                                val `e__binding19`: io.github.kotlinmania.syn.Expr.Field =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue20: io.github.kotlinmania.syn.Expr =
                                        (`e__binding19`).`base`
                                    `expr__binding0` = __rtkClassifyValue20
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Index ->
                            kotlin.run {
                                val `e__binding21`: io.github.kotlinmania.syn.Expr.Index =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue22: io.github.kotlinmania.syn.Expr =
                                        (`e__binding21`).`expr`
                                    `expr__binding0` = __rtkClassifyValue22
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.MethodCall ->
                            kotlin.run {
                                val `e__binding23`: io.github.kotlinmania.syn.Expr.MethodCall =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue24: io.github.kotlinmania.syn.Expr =
                                        (`e__binding23`).`receiver`
                                    `expr__binding0` = __rtkClassifyValue24
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Range ->
                            kotlin.run {
                                val `e__binding25`: io.github.kotlinmania.syn.Expr.Range =
                                    __rtkClassifyValue1
                                (
                                    when (
                                        val __rtkClassifyValue27:
                                            __rtkClassifyOption<io.github.kotlinmania.syn.Expr> =
                                            kotlin.run {
                                                val __rtkClassifyValue26 = (`e__binding25`).`start`
                                                if (__rtkClassifyValue26 == null) {
                                                    __rtkClassifyOption.None
                                                } else {
                                                    __rtkClassifyOption.Some(__rtkClassifyValue26)
                                                }
                                            }
                                    ) {
                                        is __rtkClassifyOption.Some ->
                                            kotlin.run {
                                                val `start`: io.github.kotlinmania.syn.Expr =
                                                    __rtkClassifyValue27.`value`
                                                kotlin.run {
                                                    val __rtkClassifyValue28: io.github.kotlinmania.syn.Expr =
                                                        `start`
                                                    `expr__binding0` = __rtkClassifyValue28
                                                }
                                            }
                                        __rtkClassifyOption.None -> return false
                                    }
                                )
                            }
                        is io.github.kotlinmania.syn.Expr.Try ->
                            kotlin.run {
                                val `e__binding29`: io.github.kotlinmania.syn.Expr.Try = __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue30: io.github.kotlinmania.syn.Expr =
                                        (`e__binding29`).`expr`
                                    `expr__binding0` = __rtkClassifyValue30
                                }
                            }
                        is io.github.kotlinmania.syn.Expr.Array,
                        is io.github.kotlinmania.syn.Expr.Async,
                        is io.github.kotlinmania.syn.Expr.Break,
                        is io.github.kotlinmania.syn.Expr.Closure,
                        is io.github.kotlinmania.syn.Expr.Const,
                        is io.github.kotlinmania.syn.Expr.Continue,
                        is io.github.kotlinmania.syn.Expr.Group,
                        is io.github.kotlinmania.syn.Expr.If,
                        is io.github.kotlinmania.syn.Expr.Infer,
                        is io.github.kotlinmania.syn.Expr.Let,
                        is io.github.kotlinmania.syn.Expr.Lit,
                        is io.github.kotlinmania.syn.Expr.Macro,
                        is io.github.kotlinmania.syn.Expr.Match,
                        is io.github.kotlinmania.syn.Expr.Paren,
                        is io.github.kotlinmania.syn.Expr.Path,
                        is io.github.kotlinmania.syn.Expr.RawAddr,
                        is io.github.kotlinmania.syn.Expr.Reference,
                        is io.github.kotlinmania.syn.Expr.Repeat,
                        is io.github.kotlinmania.syn.Expr.Return,
                        is io.github.kotlinmania.syn.Expr.Struct,
                        is io.github.kotlinmania.syn.Expr.TryBlock,
                        is io.github.kotlinmania.syn.Expr.Tuple,
                        is io.github.kotlinmania.syn.Expr.Unary,
                        is io.github.kotlinmania.syn.Expr.Unsafe,
                        is io.github.kotlinmania.syn.Expr.Verbatim,
                        is io.github.kotlinmania.syn.Expr.Yield,
                        -> return false
                    }
                )
            }
        }
    }
}

// / Whether the expression's last token is `}`.
internal fun `expr_trailing_brace`(`expr`: io.github.kotlinmania.syn.Expr): kotlin.Boolean {
    kotlin.run {
        var `expr__binding0`: io.github.kotlinmania.syn.Expr = `expr`
        kotlin.run {
            kotlin.run {
                while (true) {
                    (
                        when (
                            val __rtkClassifyValue1: io.github.kotlinmania.syn.Expr = `expr__binding0`
                        ) {
                            is io.github.kotlinmania.syn.Expr.Async,
                            is io.github.kotlinmania.syn.Expr.BlockExpr,
                            is io.github.kotlinmania.syn.Expr.Const,
                            is io.github.kotlinmania.syn.Expr.ForLoop,
                            is io.github.kotlinmania.syn.Expr.If,
                            is io.github.kotlinmania.syn.Expr.Loop,
                            is io.github.kotlinmania.syn.Expr.Match,
                            is io.github.kotlinmania.syn.Expr.Struct,
                            is io.github.kotlinmania.syn.Expr.TryBlock,
                            is io.github.kotlinmania.syn.Expr.Unsafe,
                            is io.github.kotlinmania.syn.Expr.While,
                            -> return true
                            is io.github.kotlinmania.syn.Expr.Assign ->
                                kotlin.run {
                                    val `e`: io.github.kotlinmania.syn.Expr.Assign = __rtkClassifyValue1
                                    kotlin.run {
                                        val __rtkClassifyValue2: io.github.kotlinmania.syn.Expr =
                                            (`e`).`right`
                                        `expr__binding0` = __rtkClassifyValue2
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Binary ->
                                kotlin.run {
                                    val `e__binding3`: io.github.kotlinmania.syn.Expr.Binary =
                                        __rtkClassifyValue1
                                    kotlin.run {
                                        val __rtkClassifyValue4: io.github.kotlinmania.syn.Expr =
                                            (`e__binding3`).`right`
                                        `expr__binding0` = __rtkClassifyValue4
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Break ->
                                kotlin.run {
                                    val `e__binding5`: io.github.kotlinmania.syn.Expr.Break =
                                        __rtkClassifyValue1
                                    (
                                        when (
                                            val __rtkClassifyValue7:
                                                __rtkClassifyOption<io.github.kotlinmania.syn.Expr> =
                                                kotlin.run {
                                                    val __rtkClassifyValue6 = (`e__binding5`).`expr`
                                                    if (__rtkClassifyValue6 == null) {
                                                        __rtkClassifyOption.None
                                                    } else {
                                                        __rtkClassifyOption.Some(__rtkClassifyValue6)
                                                    }
                                                }
                                        ) {
                                            is __rtkClassifyOption.Some ->
                                                kotlin.run {
                                                    val `e__binding8`: io.github.kotlinmania.syn.Expr =
                                                        __rtkClassifyValue7.`value`
                                                    kotlin.run {
                                                        val __rtkClassifyValue9:
                                                            io.github.kotlinmania.syn.Expr =
                                                            `e__binding8`
                                                        `expr__binding0` = __rtkClassifyValue9
                                                    }
                                                }
                                            __rtkClassifyOption.None -> return false
                                        }
                                    )
                                }
                            is io.github.kotlinmania.syn.Expr.Cast ->
                                kotlin.run {
                                    val `e__binding10`: io.github.kotlinmania.syn.Expr.Cast =
                                        __rtkClassifyValue1
                                    return `__rtkClassifyNested2_type_trailing_brace`((`e__binding10`).`ty`)
                                }
                            is io.github.kotlinmania.syn.Expr.Closure ->
                                kotlin.run {
                                    val `e__binding11`: io.github.kotlinmania.syn.Expr.Closure =
                                        __rtkClassifyValue1
                                    kotlin.run {
                                        val __rtkClassifyValue12: io.github.kotlinmania.syn.Expr =
                                            (`e__binding11`).`body`
                                        `expr__binding0` = __rtkClassifyValue12
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Let ->
                                kotlin.run {
                                    val `e__binding13`: io.github.kotlinmania.syn.Expr.Let =
                                        __rtkClassifyValue1
                                    kotlin.run {
                                        val __rtkClassifyValue14: io.github.kotlinmania.syn.Expr =
                                            (`e__binding13`).`expr`
                                        `expr__binding0` = __rtkClassifyValue14
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Macro ->
                                kotlin.run {
                                    val `e__binding15`: io.github.kotlinmania.syn.Expr.Macro =
                                        __rtkClassifyValue1
                                    return kotlin.run {
                                        val __rtkClassifyValue16 = ((`e__binding15`).`mac`).`delimiter`
                                        __rtkClassifyValue16 is
                                            io.github.kotlinmania.syn.MacroDelimiter.Brace
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Range ->
                                kotlin.run {
                                    val `e__binding17`: io.github.kotlinmania.syn.Expr.Range =
                                        __rtkClassifyValue1
                                    (
                                        when (
                                            val __rtkClassifyValue19:
                                                __rtkClassifyOption<io.github.kotlinmania.syn.Expr> =
                                                kotlin.run {
                                                    val __rtkClassifyValue18 = (`e__binding17`).`end`
                                                    if (__rtkClassifyValue18 == null) {
                                                        __rtkClassifyOption.None
                                                    } else {
                                                        __rtkClassifyOption.Some(__rtkClassifyValue18)
                                                    }
                                                }
                                        ) {
                                            is __rtkClassifyOption.Some ->
                                                kotlin.run {
                                                    val `end`: io.github.kotlinmania.syn.Expr =
                                                        __rtkClassifyValue19.`value`
                                                    kotlin.run {
                                                        val __rtkClassifyValue20:
                                                            io.github.kotlinmania.syn.Expr =
                                                            `end`
                                                        `expr__binding0` = __rtkClassifyValue20
                                                    }
                                                }
                                            __rtkClassifyOption.None -> return false
                                        }
                                    )
                                }
                            is io.github.kotlinmania.syn.Expr.RawAddr ->
                                kotlin.run {
                                    val `e__binding21`: io.github.kotlinmania.syn.Expr.RawAddr =
                                        __rtkClassifyValue1
                                    kotlin.run {
                                        val __rtkClassifyValue22: io.github.kotlinmania.syn.Expr =
                                            (`e__binding21`).`expr`
                                        `expr__binding0` = __rtkClassifyValue22
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Reference ->
                                kotlin.run {
                                    val `e__binding23`: io.github.kotlinmania.syn.Expr.Reference =
                                        __rtkClassifyValue1
                                    kotlin.run {
                                        val __rtkClassifyValue24: io.github.kotlinmania.syn.Expr =
                                            (`e__binding23`).`expr`
                                        `expr__binding0` = __rtkClassifyValue24
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Return ->
                                kotlin.run {
                                    val `e__binding25`: io.github.kotlinmania.syn.Expr.Return =
                                        __rtkClassifyValue1
                                    (
                                        when (
                                            val __rtkClassifyValue27:
                                                __rtkClassifyOption<io.github.kotlinmania.syn.Expr> =
                                                kotlin.run {
                                                    val __rtkClassifyValue26 = (`e__binding25`).`expr`
                                                    if (__rtkClassifyValue26 == null) {
                                                        __rtkClassifyOption.None
                                                    } else {
                                                        __rtkClassifyOption.Some(__rtkClassifyValue26)
                                                    }
                                                }
                                        ) {
                                            is __rtkClassifyOption.Some ->
                                                kotlin.run {
                                                    val `e__binding28`: io.github.kotlinmania.syn.Expr =
                                                        __rtkClassifyValue27.`value`
                                                    kotlin.run {
                                                        val __rtkClassifyValue29:
                                                            io.github.kotlinmania.syn.Expr =
                                                            `e__binding28`
                                                        `expr__binding0` = __rtkClassifyValue29
                                                    }
                                                }
                                            __rtkClassifyOption.None -> return false
                                        }
                                    )
                                }
                            is io.github.kotlinmania.syn.Expr.Unary ->
                                kotlin.run {
                                    val `e__binding30`: io.github.kotlinmania.syn.Expr.Unary =
                                        __rtkClassifyValue1
                                    kotlin.run {
                                        val __rtkClassifyValue31: io.github.kotlinmania.syn.Expr =
                                            (`e__binding30`).`expr`
                                        `expr__binding0` = __rtkClassifyValue31
                                    }
                                }
                            is io.github.kotlinmania.syn.Expr.Verbatim ->
                                kotlin.run {
                                    val `e__binding32`: io.github.kotlinmania.procmacro2.TokenStream =
                                        __rtkClassifyValue1.`tokens`
                                    return `__rtkClassifyNested5_tokens_trailing_brace`(`e__binding32`)
                                }
                            is io.github.kotlinmania.syn.Expr.Yield ->
                                kotlin.run {
                                    val `e__binding33`: io.github.kotlinmania.syn.Expr.Yield =
                                        __rtkClassifyValue1
                                    (
                                        when (
                                            val __rtkClassifyValue35:
                                                __rtkClassifyOption<io.github.kotlinmania.syn.Expr> =
                                                kotlin.run {
                                                    val __rtkClassifyValue34 = (`e__binding33`).`expr`
                                                    if (__rtkClassifyValue34 == null) {
                                                        __rtkClassifyOption.None
                                                    } else {
                                                        __rtkClassifyOption.Some(__rtkClassifyValue34)
                                                    }
                                                }
                                        ) {
                                            is __rtkClassifyOption.Some ->
                                                kotlin.run {
                                                    val `e__binding36`: io.github.kotlinmania.syn.Expr =
                                                        __rtkClassifyValue35.`value`
                                                    kotlin.run {
                                                        val __rtkClassifyValue37:
                                                            io.github.kotlinmania.syn.Expr =
                                                            `e__binding36`
                                                        `expr__binding0` = __rtkClassifyValue37
                                                    }
                                                }
                                            __rtkClassifyOption.None -> return false
                                        }
                                    )
                                }
                            is io.github.kotlinmania.syn.Expr.Array,
                            is io.github.kotlinmania.syn.Expr.Await,
                            is io.github.kotlinmania.syn.Expr.Call,
                            is io.github.kotlinmania.syn.Expr.Continue,
                            is io.github.kotlinmania.syn.Expr.Field,
                            is io.github.kotlinmania.syn.Expr.Group,
                            is io.github.kotlinmania.syn.Expr.Index,
                            is io.github.kotlinmania.syn.Expr.Infer,
                            is io.github.kotlinmania.syn.Expr.Lit,
                            is io.github.kotlinmania.syn.Expr.MethodCall,
                            is io.github.kotlinmania.syn.Expr.Paren,
                            is io.github.kotlinmania.syn.Expr.Path,
                            is io.github.kotlinmania.syn.Expr.Repeat,
                            is io.github.kotlinmania.syn.Expr.Try,
                            is io.github.kotlinmania.syn.Expr.Tuple,
                            -> return false
                        }
                    )
                }
            }
            // Rust nested function type_trailing_brace emitted at module scope as
            // __rtkClassifyNested2_type_trailing_brace.
            // Rust nested function last_type_in_path emitted at module scope as
            // __rtkClassifyNested3_last_type_in_path.
            // Rust nested function last_type_in_bounds emitted at module scope as
            // __rtkClassifyNested4_last_type_in_bounds.
            // Rust nested function tokens_trailing_brace emitted at module scope as
            // __rtkClassifyNested5_tokens_trailing_brace.
        }
    }
}

private fun `__rtkClassifyNested2_type_trailing_brace`(
    `ty`: io.github.kotlinmania.syn.SynType,
): kotlin.Boolean {
    kotlin.run {
        var `ty__binding0`: io.github.kotlinmania.syn.SynType = `ty`
        kotlin.run {
            while (true) {
                (
                    when (
                        val __rtkClassifyValue1: io.github.kotlinmania.syn.SynType = `ty__binding0`
                    ) {
                        is io.github.kotlinmania.syn.SynType.BareFn ->
                            kotlin.run {
                                val `t`: io.github.kotlinmania.syn.SynType.BareFn = __rtkClassifyValue1
                                (
                                    when (
                                        val __rtkClassifyValue2: io.github.kotlinmania.syn.ReturnType =
                                            (`t`).`output`
                                    ) {
                                        io.github.kotlinmania.syn.ReturnType.Default -> return false
                                        is io.github.kotlinmania.syn.ReturnType.TypeReturn ->
                                            kotlin.run {
                                                val `ret`: io.github.kotlinmania.syn.SynType =
                                                    __rtkClassifyValue2.`ty`
                                                kotlin.run {
                                                    val __rtkClassifyValue3: io.github.kotlinmania.syn.SynType =
                                                        `ret`
                                                    `ty__binding0` = __rtkClassifyValue3
                                                }
                                            }
                                    }
                                )
                            }
                        is io.github.kotlinmania.syn.SynType.ImplTrait ->
                            kotlin.run {
                                val `t__binding4`: io.github.kotlinmania.syn.SynType.ImplTrait =
                                    __rtkClassifyValue1
                                (
                                    when (
                                        val __rtkClassifyValue5:
                                            __rtkClassifyControlFlow<
                                                kotlin.Boolean,
                                                io.github.kotlinmania.syn.SynType,
                                            > =
                                            `__rtkClassifyNested4_last_type_in_bounds`((`t__binding4`).`bounds`)
                                    ) {
                                        is __rtkClassifyControlFlow.Break ->
                                            kotlin.run {
                                                val `trailing_brace`: kotlin.Boolean =
                                                    __rtkClassifyValue5.`value`
                                                return `trailing_brace`
                                            }
                                        is __rtkClassifyControlFlow.Continue ->
                                            kotlin.run {
                                                val `t__binding6`: io.github.kotlinmania.syn.SynType =
                                                    __rtkClassifyValue5.`value`
                                                kotlin.run {
                                                    val __rtkClassifyValue7: io.github.kotlinmania.syn.SynType =
                                                        `t__binding6`
                                                    `ty__binding0` = __rtkClassifyValue7
                                                }
                                            }
                                    }
                                )
                            }
                        is io.github.kotlinmania.syn.SynType.Macro ->
                            kotlin.run {
                                val `t__binding8`: io.github.kotlinmania.syn.SynType.Macro =
                                    __rtkClassifyValue1
                                return kotlin.run {
                                    val __rtkClassifyValue9 = ((`t__binding8`).`mac`).`delimiter`
                                    __rtkClassifyValue9 is io.github.kotlinmania.syn.MacroDelimiter.Brace
                                }
                            }
                        is io.github.kotlinmania.syn.SynType.Path ->
                            kotlin.run {
                                val `t__binding10`: io.github.kotlinmania.syn.SynType.Path =
                                    __rtkClassifyValue1
                                (
                                    when (
                                        val __rtkClassifyValue11:
                                            __rtkClassifyOption<io.github.kotlinmania.syn.SynType> =
                                            `__rtkClassifyNested3_last_type_in_path`((`t__binding10`).`path`)
                                    ) {
                                        is __rtkClassifyOption.Some ->
                                            kotlin.run {
                                                val `t__binding12`: io.github.kotlinmania.syn.SynType =
                                                    __rtkClassifyValue11.`value`
                                                kotlin.run {
                                                    val __rtkClassifyValue13:
                                                        io.github.kotlinmania.syn.SynType =
                                                        `t__binding12`
                                                    `ty__binding0` = __rtkClassifyValue13
                                                }
                                            }
                                        __rtkClassifyOption.None -> return false
                                    }
                                )
                            }
                        is io.github.kotlinmania.syn.SynType.Ptr ->
                            kotlin.run {
                                val `t__binding14`: io.github.kotlinmania.syn.SynType.Ptr =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue15: io.github.kotlinmania.syn.SynType =
                                        (`t__binding14`).`elem`
                                    `ty__binding0` = __rtkClassifyValue15
                                }
                            }
                        is io.github.kotlinmania.syn.SynType.Reference ->
                            kotlin.run {
                                val `t__binding16`: io.github.kotlinmania.syn.SynType.Reference =
                                    __rtkClassifyValue1
                                kotlin.run {
                                    val __rtkClassifyValue17: io.github.kotlinmania.syn.SynType =
                                        (`t__binding16`).`elem`
                                    `ty__binding0` = __rtkClassifyValue17
                                }
                            }
                        is io.github.kotlinmania.syn.SynType.TraitObject ->
                            kotlin.run {
                                val `t__binding18`: io.github.kotlinmania.syn.SynType.TraitObject =
                                    __rtkClassifyValue1
                                (
                                    when (
                                        val __rtkClassifyValue19:
                                            __rtkClassifyControlFlow<
                                                kotlin.Boolean,
                                                io.github.kotlinmania.syn.SynType,
                                            > =
                                            `__rtkClassifyNested4_last_type_in_bounds`(
                                                (`t__binding18`).`bounds`,
                                            )
                                    ) {
                                        is __rtkClassifyControlFlow.Break ->
                                            kotlin.run {
                                                val `trailing_brace__binding20`: kotlin.Boolean =
                                                    __rtkClassifyValue19.`value`
                                                return `trailing_brace__binding20`
                                            }
                                        is __rtkClassifyControlFlow.Continue ->
                                            kotlin.run {
                                                val `t__binding21`: io.github.kotlinmania.syn.SynType =
                                                    __rtkClassifyValue19.`value`
                                                kotlin.run {
                                                    val __rtkClassifyValue22:
                                                        io.github.kotlinmania.syn.SynType =
                                                        `t__binding21`
                                                    `ty__binding0` = __rtkClassifyValue22
                                                }
                                            }
                                    }
                                )
                            }
                        is io.github.kotlinmania.syn.SynType.Verbatim ->
                            kotlin.run {
                                val `t__binding23`: io.github.kotlinmania.procmacro2.TokenStream =
                                    __rtkClassifyValue1.`tokens`
                                return `__rtkClassifyNested5_tokens_trailing_brace`(`t__binding23`)
                            }
                        is io.github.kotlinmania.syn.SynType.Array,
                        is io.github.kotlinmania.syn.SynType.Group,
                        is io.github.kotlinmania.syn.SynType.Infer,
                        is io.github.kotlinmania.syn.SynType.Never,
                        is io.github.kotlinmania.syn.SynType.Paren,
                        is io.github.kotlinmania.syn.SynType.Slice,
                        is io.github.kotlinmania.syn.SynType.Tuple,
                        -> return false
                    }
                )
            }
        }
    }
}

private fun `__rtkClassifyNested3_last_type_in_path`(
    `path`: io.github.kotlinmania.syn.Path,
): __rtkClassifyOption<io.github.kotlinmania.syn.SynType> =
    (
        when (
            val __rtkClassifyValue1: io.github.kotlinmania.syn.PathArguments =
                (
                    __rtkClassifyUnwrap(
                        kotlin.run {
                            val __rtkClassifyValue0 = ((`path`).`segments`).last()
                            if (__rtkClassifyValue0 == null) {
                                __rtkClassifyOption.None
                            } else {
                                __rtkClassifyOption.Some(__rtkClassifyValue0)
                            }
                        },
                    )
                ).`arguments`
        ) {
            io.github.kotlinmania.syn.PathArguments.None,
            is io.github.kotlinmania.syn.PathArguments.AngleBracketed,
            -> __rtkClassifyOption.None
            is io.github.kotlinmania.syn.PathArguments.Parenthesized ->
                kotlin.run {
                    val `arg`: io.github.kotlinmania.syn.PathArguments.Parenthesized =
                        __rtkClassifyValue1
                    (
                        when (
                            val __rtkClassifyValue2: io.github.kotlinmania.syn.ReturnType = (`arg`).`output`
                        ) {
                            io.github.kotlinmania.syn.ReturnType.Default -> __rtkClassifyOption.None
                            is io.github.kotlinmania.syn.ReturnType.TypeReturn ->
                                kotlin.run {
                                    val `ret`: io.github.kotlinmania.syn.SynType = __rtkClassifyValue2.`ty`
                                    __rtkClassifyOption.Some(`ret`)
                                }
                        }
                    )
                }
        }
    )

private fun `__rtkClassifyNested4_last_type_in_bounds`(
    `bounds`: io.github.kotlinmania.syn.TypeParamBoundList,
): __rtkClassifyControlFlow<kotlin.Boolean, io.github.kotlinmania.syn.SynType> =
    (
        when (
            val __rtkClassifyValue1: io.github.kotlinmania.syn.TypeParamBound =
                __rtkClassifyUnwrap(
                    kotlin.run {
                        val __rtkClassifyValue0 = (`bounds`).last()
                        if (__rtkClassifyValue0 == null) {
                            __rtkClassifyOption.None
                        } else {
                            __rtkClassifyOption.Some(__rtkClassifyValue0)
                        }
                    },
                )
        ) {
            is io.github.kotlinmania.syn.TypeParamBound.Trait ->
                kotlin.run {
                    val `t`: io.github.kotlinmania.syn.TypeParamBound.Trait = __rtkClassifyValue1
                    (
                        when (
                            val __rtkClassifyValue2:
                                __rtkClassifyOption<io.github.kotlinmania.syn.SynType> =
                                `__rtkClassifyNested3_last_type_in_path`((`t`).`path`)
                        ) {
                            is __rtkClassifyOption.Some ->
                                kotlin.run {
                                    val `t__binding3`: io.github.kotlinmania.syn.SynType =
                                        __rtkClassifyValue2.`value`
                                    __rtkClassifyControlFlow.Continue(`t__binding3`)
                                }
                            __rtkClassifyOption.None -> __rtkClassifyControlFlow.Break(false)
                        }
                    )
                }
            is io.github.kotlinmania.syn.TypeParamBound.LifetimeBound,
            is io.github.kotlinmania.syn.TypeParamBound.PreciseCapture,
            ->
                (__rtkClassifyControlFlow.Break(false))
            is io.github.kotlinmania.syn.TypeParamBound.Verbatim ->
                kotlin.run {
                    val `t__binding4`: io.github.kotlinmania.procmacro2.TokenStream =
                        __rtkClassifyValue1.`tokens`
                    __rtkClassifyControlFlow.Break(
                        `__rtkClassifyNested5_tokens_trailing_brace`(`t__binding4`),
                    )
                }
        }
    )

private fun `__rtkClassifyNested5_tokens_trailing_brace`(
    `tokens`: io.github.kotlinmania.procmacro2.TokenStream,
): kotlin.Boolean =
    kotlin.run {
        val __rtkClassifyValue1: __rtkClassifyOption<io.github.kotlinmania.procmacro2.TokenTree> =
            kotlin.run {
                val __rtkClassifyValue0 = (((`tokens`).clone()).iterator()).asSequence().lastOrNull()
                if (__rtkClassifyValue0 == null) {
                    __rtkClassifyOption.None
                } else {
                    __rtkClassifyOption.Some(__rtkClassifyValue0)
                }
            }
        if (
            __rtkClassifyValue1 is __rtkClassifyOption.Some &&
            __rtkClassifyValue1.value is io.github.kotlinmania.procmacro2.TokenTree.Group
        ) {
            kotlin.run {
                val `last`: io.github.kotlinmania.procmacro2.Group = (__rtkClassifyValue1.value).`value`
                ((`last`).delimiter() == io.github.kotlinmania.procmacro2.Delimiter.Brace)
            }
        } else {
            (false)
        }
    }

// Rust core::ops::ControlFlow<B, C>: continuation and break payloads stay distinct.
private sealed class __rtkClassifyControlFlow<out B, out C> {
    class Continue<C>(
        val value: C,
    ) : __rtkClassifyControlFlow<kotlin.Nothing, C>()

    class Break<B>(
        val value: B,
    ) : __rtkClassifyControlFlow<B, kotlin.Nothing>()
}

// Tagged Rust Option; owned payloads require a checked fresh-value context.
private sealed class __rtkClassifyOption<out T> {
    data object None : __rtkClassifyOption<kotlin.Nothing>()

    data class Some<T>(
        val value: T,
    ) : __rtkClassifyOption<T>()
}

private fun <T> __rtkClassifyUnwrapOr(value: __rtkClassifyOption<T>, fallback: T): T =
    when (value) {
        __rtkClassifyOption.None -> fallback
        is __rtkClassifyOption.Some -> value.value
    }

private class __rtkClassifyPanic(
    message: String,
) : kotlin.RuntimeException(message)

private fun <T> __rtkClassifyUnwrap(value: __rtkClassifyOption<T>): T =
    when (value) {
        is __rtkClassifyOption.Some -> value.value
        __rtkClassifyOption.None ->
            throw __rtkClassifyPanic("called `Option::unwrap()` on a `None` value")
    }
