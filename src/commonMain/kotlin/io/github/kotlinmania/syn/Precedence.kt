// port-lint: source precedence.rs

package io.github.kotlinmania.syn

/** Expression precedence used while printing nested expressions. */
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
    Unambiguous,
    ;

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
                is BinOp.ShrAssign,
                -> Assign
            }

        /** Returns the precedence of the given expression. */
        public fun of(expr: Expr): Precedence =
            when (expr) {
                is Expr.Assign -> Assign
                is Expr.Range -> Range
                is Expr.Binary -> ofBinop(expr.op)
                is Expr.Cast -> Cast
                is Expr.Unary -> Prefix
                is Expr.RawAddr -> Prefix
                is Expr.Reference -> Prefix
                is Expr.Lit -> prefixAttrs(expr.attrs)
                is Expr.Path -> prefixAttrs(expr.attrs)
                is Expr.Paren -> prefixAttrs(expr.attrs)
                is Expr.Array -> prefixAttrs(expr.attrs)
                is Expr.Call -> prefixAttrs(expr.attrs)
                is Expr.MethodCall -> prefixAttrs(expr.attrs)
                is Expr.Field -> prefixAttrs(expr.attrs)
                is Expr.Index -> prefixAttrs(expr.attrs)
                is Expr.Try -> prefixAttrs(expr.attrs)
                is Expr.Await -> prefixAttrs(expr.attrs)
                is Expr.Repeat -> prefixAttrs(expr.attrs)
                is Expr.Group -> prefixAttrs(expr.attrs)
                is Expr.Infer -> prefixAttrs(expr.attrs)
                is Expr.Tuple -> prefixAttrs(expr.attrs)
                is Expr.Struct -> prefixAttrs(expr.attrs)
                is Expr.Let -> Let
                is Expr.Loop -> prefixAttrs(expr.attrs)
                is Expr.Match -> prefixAttrs(expr.attrs)
                is Expr.If -> prefixAttrs(expr.attrs)
                is Expr.While -> prefixAttrs(expr.attrs)
                is Expr.ForLoop -> prefixAttrs(expr.attrs)
                is Expr.Unsafe -> prefixAttrs(expr.attrs)
                is Expr.BlockExpr -> prefixAttrs(expr.attrs)
                is Expr.Const -> prefixAttrs(expr.attrs)
                is Expr.Async -> prefixAttrs(expr.attrs)
                is Expr.TryBlock -> prefixAttrs(expr.attrs)
                is Expr.Yield -> if (expr.expr != null) Jump else Unambiguous
                is Expr.Return -> if (expr.expr != null) Jump else Unambiguous
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

    public fun clone(): Precedence = this

    public fun partialCmp(other: Precedence): Int = this.compareTo(other)

    public fun eq(other: Precedence): Boolean = this == other
}
