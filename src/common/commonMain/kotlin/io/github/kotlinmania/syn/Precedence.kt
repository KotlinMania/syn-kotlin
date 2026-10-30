// port-lint: source precedence.rs
// Generated from complete Rust precedence.rs (full + printing); integration recorded in
// docs/generated-precedence-2026-10-24.md.
@file:Suppress("ClassNaming", "UnusedPrivateFunction")

package io.github.kotlinmania.syn

// Generated from Syn AST; exact original retained in input.rs.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.

// Reference: https://doc.rust-lang.org/reference/expressions.html#expression-precedence
public enum class `Precedence` {
    // return, break, closures
    `Jump`,
    // = += -= *= /= %= &= |= ^= <<= >>=
    `Assign`,
    // .. ..=
    `Range`,
    // ||
    `Or`,
    // &&
    `And`,
    // let
    `Let`,
    // == != < > <= >=
    `Compare`,
    // |
    `BitOr`,
    // ^
    `BitXor`,
    // &
    `BitAnd`,
    // << >>
    `Shift`,
    // + -
    `Sum`,
    // * / %
    `Product`,
    // as
    `Cast`,
    // unary - * ! & &mut
    `Prefix`,
    // paths, loops, function calls, array indexing, field expressions, method calls
    `Unambiguous`;

    public companion object
}

internal val `Precedence`.Companion.`MIN`: `Precedence`
    get() = `Precedence`.`Jump`

internal fun `Precedence`.Companion.`of_binop`(
    `op`: io.github.kotlinmania.syn.BinOp,
): `Precedence` =
    (when (`op`) {
        is io.github.kotlinmania.syn.BinOp.Add,
        is io.github.kotlinmania.syn.BinOp.Sub -> `Precedence`.`Sum`
        is io.github.kotlinmania.syn.BinOp.Mul,
        is io.github.kotlinmania.syn.BinOp.Div,
        is io.github.kotlinmania.syn.BinOp.Rem -> `Precedence`.`Product`
        is io.github.kotlinmania.syn.BinOp.And -> `Precedence`.`And`
        is io.github.kotlinmania.syn.BinOp.Or -> `Precedence`.`Or`
        is io.github.kotlinmania.syn.BinOp.BitXor -> `Precedence`.`BitXor`
        is io.github.kotlinmania.syn.BinOp.BitAnd -> `Precedence`.`BitAnd`
        is io.github.kotlinmania.syn.BinOp.BitOr -> `Precedence`.`BitOr`
        is io.github.kotlinmania.syn.BinOp.Shl,
        is io.github.kotlinmania.syn.BinOp.Shr -> `Precedence`.`Shift`
        is io.github.kotlinmania.syn.BinOp.Eq,
        is io.github.kotlinmania.syn.BinOp.Lt,
        is io.github.kotlinmania.syn.BinOp.Le,
        is io.github.kotlinmania.syn.BinOp.Ne,
        is io.github.kotlinmania.syn.BinOp.Ge,
        is io.github.kotlinmania.syn.BinOp.Gt -> `Precedence`.`Compare`
        is io.github.kotlinmania.syn.BinOp.AddAssign,
        is io.github.kotlinmania.syn.BinOp.SubAssign,
        is io.github.kotlinmania.syn.BinOp.MulAssign,
        is io.github.kotlinmania.syn.BinOp.DivAssign,
        is io.github.kotlinmania.syn.BinOp.RemAssign,
        is io.github.kotlinmania.syn.BinOp.BitXorAssign,
        is io.github.kotlinmania.syn.BinOp.BitAndAssign,
        is io.github.kotlinmania.syn.BinOp.BitOrAssign,
        is io.github.kotlinmania.syn.BinOp.ShlAssign,
        is io.github.kotlinmania.syn.BinOp.ShrAssign -> `Precedence`.`Assign`
    })

internal fun `Precedence`.Companion.`of`(`e`: io.github.kotlinmania.syn.Expr): `Precedence` =
    kotlin.run {
        // Rust nested function prefix_attrs emitted at module scope as
        // __rtkPrecedenceNested0_prefix_attrs.
        (when (val __rtkPrecedenceValue0: io.github.kotlinmania.syn.Expr = `e`) {
            is io.github.kotlinmania.syn.Expr.Closure ->
                kotlin.run {
                    val `e__binding1`: io.github.kotlinmania.syn.Expr.Closure =
                        __rtkPrecedenceValue0
                    (when ((`e__binding1`).`output`) {
                        io.github.kotlinmania.syn.ReturnType.Default -> `Precedence`.`Jump`
                        is io.github.kotlinmania.syn.ReturnType.TypeReturn ->
                            `__rtkPrecedenceNested0_prefix_attrs`((`e__binding1`).`attrs`)
                    })
                }
            is io.github.kotlinmania.syn.Expr.Break,
            is io.github.kotlinmania.syn.Expr.Return,
            is io.github.kotlinmania.syn.Expr.Yield ->
                kotlin.run {
                    val `expr`: __rtkPrecedenceOption<io.github.kotlinmania.syn.Expr> =
                        when (__rtkPrecedenceValue0) {
                            is io.github.kotlinmania.syn.Expr.Break ->
                                kotlin.run {
                                    val __rtkPrecedenceValue3 = (__rtkPrecedenceValue0).`expr`
                                    if (__rtkPrecedenceValue3 == null) __rtkPrecedenceOption.None
                                    else __rtkPrecedenceOption.Some(__rtkPrecedenceValue3)
                                }
                            is io.github.kotlinmania.syn.Expr.Return ->
                                kotlin.run {
                                    val __rtkPrecedenceValue4 = (__rtkPrecedenceValue0).`expr`
                                    if (__rtkPrecedenceValue4 == null) __rtkPrecedenceOption.None
                                    else __rtkPrecedenceOption.Some(__rtkPrecedenceValue4)
                                }
                            is io.github.kotlinmania.syn.Expr.Yield ->
                                kotlin.run {
                                    val __rtkPrecedenceValue5 = (__rtkPrecedenceValue0).`expr`
                                    if (__rtkPrecedenceValue5 == null) __rtkPrecedenceOption.None
                                    else __rtkPrecedenceOption.Some(__rtkPrecedenceValue5)
                                }
                        }
                    (when (`expr`) {
                        is __rtkPrecedenceOption.Some -> `Precedence`.`Jump`
                        __rtkPrecedenceOption.None -> `Precedence`.`Unambiguous`
                    })
                }
            is io.github.kotlinmania.syn.Expr.Assign -> `Precedence`.`Assign`
            is io.github.kotlinmania.syn.Expr.Range -> `Precedence`.`Range`
            is io.github.kotlinmania.syn.Expr.Binary ->
                kotlin.run {
                    val `e__binding7`: io.github.kotlinmania.syn.Expr.Binary = __rtkPrecedenceValue0
                    `Precedence`.`of_binop`((`e__binding7`).`op`)
                }
            is io.github.kotlinmania.syn.Expr.Let -> `Precedence`.`Let`
            is io.github.kotlinmania.syn.Expr.Cast -> `Precedence`.`Cast`
            is io.github.kotlinmania.syn.Expr.RawAddr,
            is io.github.kotlinmania.syn.Expr.Reference,
            is io.github.kotlinmania.syn.Expr.Unary -> `Precedence`.`Prefix`
            is io.github.kotlinmania.syn.Expr.Array,
            is io.github.kotlinmania.syn.Expr.Async,
            is io.github.kotlinmania.syn.Expr.Await,
            is io.github.kotlinmania.syn.Expr.BlockExpr,
            is io.github.kotlinmania.syn.Expr.Call,
            is io.github.kotlinmania.syn.Expr.Const,
            is io.github.kotlinmania.syn.Expr.Continue,
            is io.github.kotlinmania.syn.Expr.Field,
            is io.github.kotlinmania.syn.Expr.ForLoop,
            is io.github.kotlinmania.syn.Expr.Group,
            is io.github.kotlinmania.syn.Expr.If,
            is io.github.kotlinmania.syn.Expr.Index,
            is io.github.kotlinmania.syn.Expr.Infer,
            is io.github.kotlinmania.syn.Expr.Lit,
            is io.github.kotlinmania.syn.Expr.Loop,
            is io.github.kotlinmania.syn.Expr.Macro,
            is io.github.kotlinmania.syn.Expr.Match,
            is io.github.kotlinmania.syn.Expr.MethodCall,
            is io.github.kotlinmania.syn.Expr.Paren,
            is io.github.kotlinmania.syn.Expr.Path,
            is io.github.kotlinmania.syn.Expr.Repeat,
            is io.github.kotlinmania.syn.Expr.Struct,
            is io.github.kotlinmania.syn.Expr.Try,
            is io.github.kotlinmania.syn.Expr.TryBlock,
            is io.github.kotlinmania.syn.Expr.Tuple,
            is io.github.kotlinmania.syn.Expr.Unsafe,
            is io.github.kotlinmania.syn.Expr.While ->
                kotlin.run {
                    val `attrs`: kotlin.collections.List<io.github.kotlinmania.syn.Attribute> =
                        when (__rtkPrecedenceValue0) {
                            is io.github.kotlinmania.syn.Expr.Array ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Async ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Await ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.BlockExpr ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Call ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Const ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Continue ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Field ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.ForLoop ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Group ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.If -> (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Index ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Infer ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Lit -> (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Loop ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Macro ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Match ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.MethodCall ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Paren ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Path ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Repeat ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Struct ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Try -> (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.TryBlock ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Tuple ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.Unsafe ->
                                (__rtkPrecedenceValue0).`attrs`
                            is io.github.kotlinmania.syn.Expr.While ->
                                (__rtkPrecedenceValue0).`attrs`
                        }
                    `__rtkPrecedenceNested0_prefix_attrs`(`attrs`)
                }
            is io.github.kotlinmania.syn.Expr.Verbatim -> `Precedence`.`Unambiguous`
        })
    }

private fun `__rtkPrecedenceNested0_prefix_attrs`(
    `attrs`: kotlin.collections.List<io.github.kotlinmania.syn.Attribute>,
): `Precedence` {
    return kotlin.run {
        kotlin.run {
            for (`attr` in `attrs`) kotlin.run {
                val __rtkPrecedenceValue0 = (`attr`).`style`
                if (__rtkPrecedenceValue0 === io.github.kotlinmania.syn.AttrStyle.Outer)
                    kotlin.run {
                        return `Precedence`.`Prefix`
                    }
            }
        }
        `Precedence`.`Unambiguous`
    }
}

// Rust Copy marker: value copying requires no runtime operation.
internal fun `Precedence`.`__rtkPrecedenceClone_clone`(): `Precedence` = this

internal fun `Precedence`.`__rtkPrecedencePartialEq_eq`(`other`: `Precedence`): kotlin.Boolean =
    ((this).ordinal.toUByte() == (`other`).ordinal.toUByte())

internal fun `Precedence`.`__rtkPrecedencePartialOrd_partial_cmp`(
    `other`: `Precedence`,
): __rtkPrecedenceOption<__rtkPrecedenceOrdering> = kotlin.run {
    val `this`: kotlin.UByte = (this).ordinal.toUByte()
    val `other__binding0`: kotlin.UByte = (`other`).ordinal.toUByte()
    __rtkPrecedenceOption.Some(
        __rtkPrecedenceOrderingFromComparison((`this`).compareTo(`other__binding0`)),
    )
}

// Rust core::cmp::Ordering: tags are distinct from Kotlin enum ordinals.
internal enum class __rtkPrecedenceOrdering {
    Less,
    Equal,
    Greater,
}

private fun __rtkPrecedenceOrderingFromComparison(value: kotlin.Int): __rtkPrecedenceOrdering =
    when {
        value < 0 -> __rtkPrecedenceOrdering.Less
        value > 0 -> __rtkPrecedenceOrdering.Greater
        else -> __rtkPrecedenceOrdering.Equal
    }

// Tagged Rust Option; owned payloads require a checked fresh-value context.
internal sealed class __rtkPrecedenceOption<out T> {
    data object None : __rtkPrecedenceOption<kotlin.Nothing>()

    data class Some<T>(val value: T) : __rtkPrecedenceOption<T>()
}

private fun <T> __rtkPrecedenceUnwrapOr(value: __rtkPrecedenceOption<T>, fallback: T): T =
    when (value) {
        __rtkPrecedenceOption.None -> fallback
        is __rtkPrecedenceOption.Some -> value.value
    }
