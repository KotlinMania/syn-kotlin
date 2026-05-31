// port-lint: source precedence.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import kotlin.native.HiddenFromObjC

/** Expression precedence used while printing nested expressions. */
@HiddenFromObjC
public enum class Precedence {
    Jump,
    Assign,
    Range,
    Or,
    And,
    Let,
    Compare,
    BitOr,
    BitXor,
    BitAnd,
    Shift,
    Sum,
    Product,
    Cast,
    Prefix,
    Unambiguous;

    public companion object {
        public val MIN: Precedence = Jump

        public fun ofBinop(op: BinOp): Precedence =
            when (op) {
                is BinOp.Add, is BinOp.Sub -> Sum
                is BinOp.Mul, is BinOp.Div, is BinOp.Rem -> Product
                is BinOp.And -> And
                is BinOp.Or -> Or
                is BinOp.BitXor -> BitXor
                is BinOp.BitAnd -> BitAnd
                is BinOp.BitOr -> BitOr
                is BinOp.Shl, is BinOp.Shr -> Shift
                is BinOp.Eq, is BinOp.Lt, is BinOp.Le, is BinOp.Ne, is BinOp.Ge, is BinOp.Gt -> Compare
                is BinOp.AddAssign,
                is BinOp.SubAssign,
                is BinOp.MulAssign,
                is BinOp.DivAssign,
                is BinOp.RemAssign,
                is BinOp.BitXorAssign,
                is BinOp.BitAndAssign,
                is BinOp.BitOrAssign,
                is BinOp.ShlAssign,
                is BinOp.ShrAssign -> Assign
            }

        /** Returns the precedence of the given expression. */
        public fun of(expr: Expr): Precedence =
            when (expr) {
                is Expr.Assign -> Assign
                is Expr.Range -> Range
                is Expr.Binary -> ofBinop(expr.op)
                is Expr.Cast -> Cast
                is Expr.Unary -> Prefix
                is Expr.Reference -> if (expr.mutability != null) Prefix else Unambiguous
                is Expr.Lit -> Unambiguous
                is Expr.Path -> Unambiguous
                is Expr.Paren -> Unambiguous
                is Expr.Array -> Unambiguous
                is Expr.Call -> Unambiguous
                is Expr.MethodCall -> Unambiguous
                is Expr.Field -> Unambiguous
                is Expr.Index -> Unambiguous
                is Expr.Try -> Unambiguous
                is Expr.Await -> Unambiguous
                is Expr.Repeat -> Unambiguous
                is Expr.Group -> Unambiguous
                is Expr.Infer -> Unambiguous
                is Expr.Tuple -> Unambiguous
                is Expr.Struct -> Unambiguous
                is Expr.Let -> Let
                is Expr.Loop -> Unambiguous
                is Expr.Match -> Unambiguous
                is Expr.If -> Unambiguous
                is Expr.While -> Unambiguous
                is Expr.ForLoop -> Unambiguous
                is Expr.Unsafe -> Unambiguous
                is Expr.BlockExpr -> Unambiguous
                is Expr.Const -> Unambiguous
                is Expr.Async -> Unambiguous
                is Expr.TryBlock -> Unambiguous
                is Expr.Yield -> Jump
                is Expr.Return -> Jump
                is Expr.Break -> if (expr.expr != null) Jump else Unambiguous
                is Expr.Continue -> Unambiguous
                is Expr.Closure -> if (expr.output != ReturnType.Default) prefixAttrs(expr.attrs) else Jump
                is Expr.Macro -> prefixAttrs(expr.attrs)
                is Expr.Verbatim -> Unambiguous
            }

        /** If any attribute is outer, the expression is in prefix position. */
        public fun prefixAttrs(attrs: List<Attribute>): Precedence =
            if (attrs.any { it.style is AttrStyle.Outer }) Prefix else Unambiguous
    }
}
