// port-lint: source precedence.rs
package io.github.kotlinmania.syn

/** Expression precedence used while printing nested expressions. */
internal enum class Precedence {
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

 internal companion object {
 internal val MIN: Precedence = Jump

 internal fun ofBinop(op: BinOp): Precedence =
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
 }
}
