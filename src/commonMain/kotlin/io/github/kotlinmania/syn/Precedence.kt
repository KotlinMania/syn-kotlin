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

        /** If any attribute is outer, the expression is in prefix position. */
        public fun prefixAttrs(attrs: List<Attribute>): Precedence =
            if (attrs.any { it.style is AttrStyle.Outer }) Prefix else Unambiguous
    }
}
