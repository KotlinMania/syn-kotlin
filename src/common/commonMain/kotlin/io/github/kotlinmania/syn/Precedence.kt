// port-lint: source precedence.rs
package io.github.kotlinmania.syn

// Reference: https://doc.rust-lang.org/reference/expressions.html#expression-precedence
public enum class Precedence {
    // return, break, closures
    Jump,

    // = += -= *= /= %= &= |= ^= <<= >>=
    Assign,

    // .. ..=
    Range,

    // ||
    Or,

    // &&
    And,

    // let
    Let,

    // == != < > <= >=
    Compare,

    // |
    BitOr,

    // ^
    BitXor,

    // &
    BitAnd,

    // << >>
    Shift,

    // + -
    Sum,

    // * / %
    Product,

    // as
    Cast,

    // unary - * ! &
    Prefix,

    // paths, loops, function calls, array indexing, field expressions, method calls
    Unambiguous,

    ;

    public companion object {
        internal val MIN: Precedence = Jump

        internal fun ofBinop(op: BinOp): Precedence =
            when (op) {
                is BinOp.Add,
                is BinOp.Sub,
                -> Sum

                is BinOp.Mul,
                is BinOp.Div,
                is BinOp.Rem,
                -> Product

                is BinOp.And -> And
                is BinOp.Or -> Or
                is BinOp.BitXor -> BitXor
                is BinOp.BitAnd -> BitAnd
                is BinOp.BitOr -> BitOr

                is BinOp.Shl,
                is BinOp.Shr,
                -> Shift

                is BinOp.Eq,
                is BinOp.Lt,
                is BinOp.Le,
                is BinOp.Ne,
                is BinOp.Ge,
                is BinOp.Gt,
                -> Compare

                is BinOp.AddAssign,
                is BinOp.SubAssign,
                is BinOp.MulAssign,
                is BinOp.DivAssign,
                is BinOp.RemAssign,
                is BinOp.BitXorAssign,
                is BinOp.BitAndAssign,
                is BinOp.BitOrAssign,
                is BinOp.ShlAssign,
                is BinOp.ShrAssign,
                -> Assign
            }

        internal fun of(e: Expr): Precedence =
            when (e) {
                is Expr.Closure ->
                    when (e.output) {
                        ReturnType.Default -> Jump
                        is ReturnType.TypeReturn -> prefixAttrs(e.attrs)
                    }

                is Expr.Break -> if (e.expr != null) Jump else Unambiguous
                is Expr.Return -> if (e.expr != null) Jump else Unambiguous
                is Expr.Yield -> if (e.expr != null) Jump else Unambiguous

                is Expr.Assign -> Assign
                is Expr.Range -> Range
                is Expr.Binary -> ofBinop(e.op)
                is Expr.Let -> Let
                is Expr.Cast -> Cast

                is Expr.RawAddr,
                is Expr.Reference,
                is Expr.Unary,
                -> Prefix

                is Expr.Array -> prefixAttrs(e.attrs)
                is Expr.Async -> prefixAttrs(e.attrs)
                is Expr.Await -> prefixAttrs(e.attrs)
                is Expr.BlockExpr -> prefixAttrs(e.attrs)
                is Expr.Call -> prefixAttrs(e.attrs)
                is Expr.Const -> prefixAttrs(e.attrs)
                is Expr.Continue -> prefixAttrs(e.attrs)
                is Expr.Field -> prefixAttrs(e.attrs)
                is Expr.ForLoop -> prefixAttrs(e.attrs)
                is Expr.Group -> prefixAttrs(e.attrs)
                is Expr.If -> prefixAttrs(e.attrs)
                is Expr.Index -> prefixAttrs(e.attrs)
                is Expr.Infer -> prefixAttrs(e.attrs)
                is Expr.Lit -> prefixAttrs(e.attrs)
                is Expr.Loop -> prefixAttrs(e.attrs)
                is Expr.Macro -> prefixAttrs(e.attrs)
                is Expr.Match -> prefixAttrs(e.attrs)
                is Expr.MethodCall -> prefixAttrs(e.attrs)
                is Expr.Paren -> prefixAttrs(e.attrs)
                is Expr.Path -> prefixAttrs(e.attrs)
                is Expr.Repeat -> prefixAttrs(e.attrs)
                is Expr.Struct -> prefixAttrs(e.attrs)
                is Expr.Try -> prefixAttrs(e.attrs)
                is Expr.TryBlock -> prefixAttrs(e.attrs)
                is Expr.Tuple -> prefixAttrs(e.attrs)
                is Expr.Unsafe -> prefixAttrs(e.attrs)
                is Expr.While -> prefixAttrs(e.attrs)
                is Expr.Verbatim -> Unambiguous
            }

        private fun prefixAttrs(attrs: List<Attribute>): Precedence {
            for (attr in attrs) {
                if (attr.style === AttrStyle.Outer) {
                    return Prefix
                }
            }
            return Unambiguous
        }
    }
}

internal fun Precedence.clone(): Precedence = this

internal fun Precedence.eq(other: Precedence): Boolean = this.ordinal == other.ordinal

internal fun Precedence.partialCmp(other: Precedence): Int = this.ordinal.compareTo(other.ordinal)
