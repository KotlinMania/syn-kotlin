// port-lint: source expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.quote.toTokens
import kotlin.jvm.JvmInline

private enum class ExprPosition {
    LeftOperand,
    RightOperand,
    PostfixBase,
    PrefixOperand,
    Condition,
}

private fun Expr.toTokensWithParens(
    tokens: TokenStream,
    parentPrecedence: Precedence,
    position: ExprPosition,
) {
    val needsParens = needsParens(parentPrecedence, position)
    if (needsParens) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else if (position == ExprPosition.Condition) {
        toTokensInCondition(tokens)
    } else {
        toTokens(tokens)
    }
}

internal fun Expr.toTokensAsStmt(tokens: TokenStream) {
    if (this is Expr.Let) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokens(tokens)
    }
}

private fun Expr.toTokensInCondition(tokens: TokenStream) {
    when (this) {
        is Expr.Await -> toTokensAsCondition(tokens)
        is Expr.Break -> toTokensAsCondition(tokens)
        is Expr.Call -> toTokensAsCondition(tokens)
        is Expr.Closure -> toTokensAsCondition(tokens)
        is Expr.Field -> toTokensAsCondition(tokens)
        is Expr.Index -> toTokensAsCondition(tokens)
        is Expr.MethodCall -> toTokensAsCondition(tokens)
        is Expr.Return -> toTokensAsCondition(tokens)
        is Expr.Try -> toTokensAsCondition(tokens)
        is Expr.Yield -> toTokensAsCondition(tokens)
        else -> toTokens(tokens)
    }
}

private fun Expr.toTokensAsConditionPostfixBase(tokens: TokenStream) {
    if (Precedence.of(this) < Precedence.Unambiguous || this is Expr.Struct) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensInCondition(tokens)
    }
}

private fun Expr.toTokensAsConditionJumpValue(tokens: TokenStream) {
    if (needsParensAsConditionJumpValue()) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokens(tokens)
    }
}

private fun Expr.toTokensAsConditionBreakValue(tokens: TokenStream) {
    if (needsParensAsConditionJumpValue()) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensAsOptionalOperand(tokens)
    }
}

private fun Expr.toTokensAsOptionalOperand(tokens: TokenStream) {
    when (this) {
        is Expr.Await -> toTokensAsOptionalOperand(tokens)
        is Expr.Call -> toTokensAsOptionalOperand(tokens)
        is Expr.Field -> toTokensAsOptionalOperand(tokens)
        is Expr.Index -> toTokensAsOptionalOperand(tokens)
        is Expr.MethodCall -> toTokensAsOptionalOperand(tokens)
        is Expr.Try -> toTokensAsOptionalOperand(tokens)
        else -> toTokens(tokens)
    }
}

private fun Expr.toTokensAsOptionalOperandPostfixBase(tokens: TokenStream) {
    if (Precedence.of(this) < Precedence.Unambiguous ||
        this is Expr.BlockExpr && attrs.isEmpty() && label == null
    ) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokens(tokens)
    }
}

private fun Expr.toTokensAsCallee(tokens: TokenStream) {
    if (this is Expr.Field && member is Member.Named) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
    }
}

private fun Expr.toTokensAsConditionCallee(tokens: TokenStream) {
    if (this is Expr.Field && member is Member.Named) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensAsConditionPostfixBase(tokens)
    }
}

private fun Expr.toTokensAsOptionalOperandCallee(tokens: TokenStream) {
    if (this is Expr.Field && member is Member.Named) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensAsOptionalOperandPostfixBase(tokens)
    }
}

private fun Expr.toTokensAsRangeStart(tokens: TokenStream) {
    if (this is Expr.Binary && attrs.isEmpty()) {
        toTokensAsRangeStart(tokens)
    } else {
        toTokensWithParens(tokens, Precedence.Range, ExprPosition.LeftOperand)
    }
}

private fun Expr.needsParensAsConditionJumpValue(): Boolean =
    this is Expr.Break && expr == null ||
        this is Expr.Path ||
        this is Expr.Range && end == null ||
        this is Expr.Return && expr == null ||
        this is Expr.Yield && expr == null

private fun Expr.Await.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    base.toTokensAsConditionPostfixBase(tokens)
    dotToken.toTokens(tokens)
    awaitToken.toTokens(tokens)
}

private fun Expr.Break.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    breakToken.toTokens(tokens)
    label?.toTokens(tokens)
    if (expr != null) {
        if (label == null && exprLeadingLabel(expr)) {
            io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
                expr.toTokens(inner)
            }
        } else {
            expr.toTokensAsConditionBreakValue(tokens)
        }
    }
}

private fun Expr.Call.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    func.toTokensAsConditionCallee(tokens)
    parenToken.surround(tokens) { inner ->
        args.toTokens(inner)
    }
}

private fun Expr.Closure.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    constness?.toTokens(tokens)
    asyncness?.toTokens(tokens)
    capture?.toTokens(tokens)
    or1Token.toTokens(tokens)
    inputs.toTokens(tokens)
    or2Token.toTokens(tokens)
    output.toTokens(tokens)
    body.toTokensWithParens(tokens, Precedence.MIN, ExprPosition.Condition)
}

private fun Expr.Await.toTokensAsOptionalOperand(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    base.toTokensAsOptionalOperandPostfixBase(tokens)
    dotToken.toTokens(tokens)
    awaitToken.toTokens(tokens)
}

private fun Expr.Call.toTokensAsOptionalOperand(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    func.toTokensAsOptionalOperandCallee(tokens)
    parenToken.surround(tokens) { inner ->
        args.toTokens(inner)
    }
}

private fun Expr.Field.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    base.toTokensAsConditionPostfixBase(tokens)
    dotToken.toTokens(tokens)
    member.toTokens(tokens)
}

private fun Expr.Field.toTokensAsOptionalOperand(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    base.toTokensAsOptionalOperandPostfixBase(tokens)
    dotToken.toTokens(tokens)
    member.toTokens(tokens)
}

private fun Expr.Index.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    expr.toTokensAsConditionPostfixBase(tokens)
    bracketToken.surround(tokens) { inner -> index.toTokens(inner) }
}

private fun Expr.Index.toTokensAsOptionalOperand(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    expr.toTokensAsOptionalOperandPostfixBase(tokens)
    bracketToken.surround(tokens) { inner -> index.toTokens(inner) }
}

private fun Expr.MethodCall.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    receiver.toTokensAsConditionPostfixBase(tokens)
    dotToken.toTokens(tokens)
    method.toTokens(tokens)
    turbofish?.toTokens(tokens)
    parenToken.surround(tokens) { inner ->
        args.toTokens(inner)
    }
}

private fun Expr.MethodCall.toTokensAsOptionalOperand(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    receiver.toTokensAsOptionalOperandPostfixBase(tokens)
    dotToken.toTokens(tokens)
    method.toTokens(tokens)
    turbofish?.toTokens(tokens)
    parenToken.surround(tokens) { inner ->
        args.toTokens(inner)
    }
}

private fun Expr.Return.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    returnToken.toTokens(tokens)
    expr?.toTokensAsConditionJumpValue(tokens)
}

private fun Expr.Try.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    expr.toTokensAsConditionPostfixBase(tokens)
    questionToken.toTokens(tokens)
}

private fun Expr.Try.toTokensAsOptionalOperand(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    expr.toTokensAsOptionalOperandPostfixBase(tokens)
    questionToken.toTokens(tokens)
}

private fun Expr.Yield.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    yieldToken.toTokens(tokens)
    expr?.toTokensAsConditionJumpValue(tokens)
}

private fun Expr.Binary.toTokensAsRangeStart(tokens: TokenStream) {
    val precedence = Precedence.ofBinop(op)
    if (left.isValueLessJump() && binOpCanBeginExpr(op)) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            left.toTokens(inner)
        }
    } else {
        left.toTokensWithParens(tokens, precedence, ExprPosition.LeftOperand)
    }
    op.toTokens(tokens)
    if (right.isValueLessJump()) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            right.toTokens(inner)
        }
    } else {
        right.toTokensWithParens(tokens, precedence, ExprPosition.RightOperand)
    }
}

private fun Expr.needsParens(parentPrecedence: Precedence, position: ExprPosition): Boolean {
    if (position == ExprPosition.Condition) {
        return this is Expr.Struct
    }

    if (position == ExprPosition.PostfixBase) {
        return Precedence.of(this) < Precedence.Unambiguous ||
            this is Expr.Break && expr == null ||
            this is Expr.Return && expr == null ||
            this is Expr.Yield && expr == null
    }

    if ((position == ExprPosition.LeftOperand || position == ExprPosition.RightOperand) &&
        parentPrecedence == Precedence.Compare &&
        this is Expr.Range &&
        start == null &&
        end == null
    ) {
        return false
    }

    if (position == ExprPosition.LeftOperand && parentPrecedence == Precedence.Assign && this is Expr.Range) {
        return true
    }

    if (position == ExprPosition.LeftOperand && parentPrecedence == Precedence.Range && this.isValueLessJump()) {
        return true
    }

    if (position == ExprPosition.RightOperand && this is Expr.Range && start == null) {
        return false
    }

    if (position == ExprPosition.RightOperand &&
        parentPrecedence == Precedence.Range &&
        (this is Expr.Return && expr == null || this is Expr.Yield && expr == null)
    ) {
        return false
    }

    if ((position == ExprPosition.LeftOperand || position == ExprPosition.RightOperand) &&
        (this is Expr.Assign ||
            (this is Expr.BlockExpr && !(parentPrecedence == Precedence.Range && position == ExprPosition.RightOperand)) ||
            this is Expr.Cast ||
            this is Expr.Struct ||
            (this is Expr.Macro && mac.isBrace()))
    ) {
        return true
    }

    val childPrecedence = Precedence.of(this)
    if (childPrecedence < parentPrecedence) return true
    if (childPrecedence > parentPrecedence) return false

    return when (position) {
        ExprPosition.LeftOperand ->
            parentPrecedence == Precedence.Assign ||
                parentPrecedence == Precedence.Range ||
                parentPrecedence == Precedence.Compare
        ExprPosition.RightOperand ->
            parentPrecedence != Precedence.Assign
        ExprPosition.PrefixOperand ->
            childPrecedence <= Precedence.Prefix
        ExprPosition.Condition -> false
        ExprPosition.PostfixBase -> false
    }
}

private fun binOpCanBeginExpr(op: BinOp): Boolean =
    when (op) {
        is BinOp.Sub,
        is BinOp.Mul,
        is BinOp.And,
        is BinOp.Or,
        is BinOp.BitAnd,
        is BinOp.BitOr,
        is BinOp.Shl,
        is BinOp.Lt -> true
        else -> false
    }

private fun Expr.isValueLessJump(): Boolean =
    this is Expr.Break && expr == null ||
        this is Expr.Return && expr == null ||
        this is Expr.Yield && expr == null

private fun exprLeadingLabel(expr: Expr): Boolean {
    var current = expr
    while (true) {
        when (current) {
            is Expr.BlockExpr -> return current.label != null
            is Expr.ForLoop -> return current.label != null
            is Expr.Loop -> return current.label != null
            is Expr.While -> return current.label != null
            is Expr.Assign -> current = current.left
            is Expr.Await -> current = current.base
            is Expr.Binary -> current = current.left
            is Expr.Call -> current = current.func
            is Expr.Cast -> current = current.expr
            is Expr.Field -> current = current.base
            is Expr.Index -> current = current.expr
            is Expr.MethodCall -> current = current.receiver
            is Expr.Range -> current = current.start ?: return false
            is Expr.Try -> current = current.expr
            else -> return false
        }
    }
}

/** An expression syntax tree node. */
public sealed class Expr : ToTokens {
    /** A slice literal expression: `[a, b, c, d]`. */
    public data class Array(
        public val attrs: List<Attribute>,
        public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public val elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            bracketToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }

        override fun deepCopy(): Array = Array(attrs.map { it.deepCopy() }, bracketToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** An assignment expression: `a = compute()`. */
    public data class Assign(
        public val attrs: List<Attribute>,
        public val left: Expr,
        public val eqToken: io.github.kotlinmania.syn.token.Eq,
        public val right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            val emit = { target: TokenStream ->
                left.toTokensWithParens(target, Precedence.Assign, ExprPosition.LeftOperand)
                eqToken.toTokens(target)
                right.toTokensWithParens(target, Precedence.Assign, ExprPosition.RightOperand)
            }
            if (attrs.isNotEmpty()) {
                io.github.kotlinmania.syn.token.Paren.default().surround(tokens, emit)
            } else {
                emit(tokens)
            }
        }

        override fun deepCopy(): Assign = Assign(attrs.map { it.deepCopy() }, left.deepCopy(), eqToken, right.deepCopy())
    }

    /** An async block: `async { ... }`. */
    public data class Async(
        public val attrs: List<Attribute>,
        public val asyncToken: io.github.kotlinmania.syn.token.Async,
        public val capture: io.github.kotlinmania.syn.token.Move?,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            asyncToken.toTokens(tokens)
            capture?.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Async = Async(attrs.map { it.deepCopy() }, asyncToken, capture, block)
    }

    /** An await expression: `fut.await`. */
    public data class Await(
        public val attrs: List<Attribute>,
        public val base: Expr,
        public val dotToken: io.github.kotlinmania.syn.token.Dot,
        public val awaitToken: io.github.kotlinmania.syn.token.Await,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            base.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            dotToken.toTokens(tokens)
            awaitToken.toTokens(tokens)
        }

        override fun deepCopy(): Await = Await(attrs.map { it.deepCopy() }, base.deepCopy(), dotToken, awaitToken)
    }

    /** A binary operation: `a + b`, `a += b`. */
    public data class Binary(
        public val attrs: List<Attribute>,
        public val left: Expr,
        public val op: BinOp,
        public val right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            val emit = { target: TokenStream ->
                val precedence = Precedence.ofBinop(op)
                if (left.isValueLessJump() && binOpCanBeginExpr(op)) {
                    io.github.kotlinmania.syn.token.Paren.default().surround(target) { inner ->
                        left.toTokens(inner)
                    }
                } else {
                    left.toTokensWithParens(target, precedence, ExprPosition.LeftOperand)
                }
                op.toTokens(target)
                right.toTokensWithParens(target, precedence, ExprPosition.RightOperand)
            }
            if (attrs.isNotEmpty()) {
                io.github.kotlinmania.syn.token.Paren.default().surround(tokens, emit)
            } else {
                emit(tokens)
            }
        }

        override fun deepCopy(): Binary = Binary(attrs.map { it.deepCopy() }, left.deepCopy(), op, right.deepCopy())
    }

    /** A blocked scope: `{ ... }`. */
    public data class BlockExpr(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): BlockExpr = BlockExpr(attrs.map { it.deepCopy() }, label?.deepCopy(), block)
    }

    /** A `break`, with an optional label to break and an optional expression. */
    public data class Break(
        public val attrs: List<Attribute>,
        public val breakToken: io.github.kotlinmania.syn.token.Break,
        public val label: Lifetime?,
        public val expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            breakToken.toTokens(tokens)
            label?.toTokens(tokens)
            if (expr != null) {
                if ((label == null && exprLeadingLabel(expr)) || (expr is Break && expr.expr == null)) {
                    io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
                        expr.toTokens(inner)
                    }
                } else {
                    expr.toTokens(tokens)
                }
            }
        }

        override fun deepCopy(): Break = Break(attrs.map { it.deepCopy() }, breakToken, label?.deepCopy(), expr?.deepCopy())
    }

    /** A function call expression: `invoke(a, b)`. */
    public data class Call(
        public val attrs: List<Attribute>,
        public val func: Expr,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val args: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            func.toTokensAsCallee(tokens)
            parenToken.surround(tokens) { inner ->
                args.toTokens(inner)
            }
        }

        override fun deepCopy(): Call = Call(attrs.map { it.deepCopy() }, func.deepCopy(), parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A cast expression: `foo as f64`. */
    public data class Cast(
        public val attrs: List<Attribute>,
        public val expr: Expr,
        public val asToken: io.github.kotlinmania.syn.token.As,
        public val ty: SynType,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            val emit = { target: TokenStream ->
                expr.toTokensWithParens(target, Precedence.Cast, ExprPosition.LeftOperand)
                asToken.toTokens(target)
                ty.toTokens(target)
            }
            if (attrs.isNotEmpty()) {
                io.github.kotlinmania.syn.token.Paren.default().surround(tokens, emit)
            } else {
                emit(tokens)
            }
        }

        override fun deepCopy(): Cast = Cast(attrs.map { it.deepCopy() }, expr.deepCopy(), asToken, ty.deepCopy())
    }

    /** A closure expression: `|a, b| a + b`. */
    public data class Closure(
        public val attrs: List<Attribute>,
        public val constness: io.github.kotlinmania.syn.token.Const?,
        public val asyncness: io.github.kotlinmania.syn.token.Async?,
        public val capture: io.github.kotlinmania.syn.token.Move?,
        public val or1Token: io.github.kotlinmania.syn.token.Or,
        public val inputs: PatList,
        public val or2Token: io.github.kotlinmania.syn.token.Or,
        public val output: ReturnType,
        public val body: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constness?.toTokens(tokens)
            asyncness?.toTokens(tokens)
            capture?.toTokens(tokens)
            or1Token.toTokens(tokens)
            inputs.toTokens(tokens)
            or2Token.toTokens(tokens)
            output.toTokens(tokens)
            body.toTokens(tokens)
        }

        override fun deepCopy(): Closure = Closure(attrs.map { it.deepCopy() }, constness, asyncness, capture, or1Token, inputs.copy({ it.deepCopy() }, { it }), or2Token, output.deepCopy(), body.deepCopy())
    }

    /** A const block: `const { ... }`. */
    public data class Const(
        public val attrs: List<Attribute>,
        public val constToken: io.github.kotlinmania.syn.token.Const,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Const = Const(attrs.map { it.deepCopy() }, constToken, block)
    }

    /** A `continue`, with an optional label. */
    public data class Continue(
        public val attrs: List<Attribute>,
        public val continueToken: io.github.kotlinmania.syn.token.Continue,
        public val label: Lifetime?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            continueToken.toTokens(tokens)
            label?.toTokens(tokens)
        }

        override fun deepCopy(): Continue = Continue(attrs.map { it.deepCopy() }, continueToken, label?.deepCopy())
    }

    /** Access of a named field of a data class (`obj.k`) or indexed element of a tuple-like compound (`obj.0`). */
    public data class Field(
        public val attrs: List<Attribute>,
        public val base: Expr,
        public val dotToken: io.github.kotlinmania.syn.token.Dot,
        public val member: Member,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            base.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            dotToken.toTokens(tokens)
            member.toTokens(tokens)
        }

        override fun deepCopy(): Field = Field(attrs.map { it.deepCopy() }, base.deepCopy(), dotToken, member)
    }

    /** A for loop: `for pat in expr { ... }`. */
    public data class ForLoop(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val forToken: io.github.kotlinmania.syn.token.For,
        public val pat: Pat,
        public val inToken: io.github.kotlinmania.syn.token.In,
        public val expr: Expr,
        public val body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            forToken.toTokens(tokens)
            pat.toTokens(tokens)
            inToken.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Jump, ExprPosition.RightOperand)
            body.toTokens(tokens)
        }

        override fun deepCopy(): ForLoop = ForLoop(attrs.map { it.deepCopy() }, label?.deepCopy(), forToken, pat.deepCopy(), inToken, expr.deepCopy(), body)
    }

    /** An expression contained within invisible delimiters. */
    public data class Group(
        public val attrs: List<Attribute>,
        public val groupToken: io.github.kotlinmania.syn.token.Group,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            groupToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Group = Group(attrs.map { it.deepCopy() }, groupToken, expr.deepCopy())
    }

    /** An `if` expression with an optional `else` block. */
    public data class If(
        public val attrs: List<Attribute>,
        public val ifToken: io.github.kotlinmania.syn.token.If,
        public val cond: Expr,
        public val thenBranch: Block,
        public val elseBranch: ElseExpr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            ifToken.toTokens(tokens)
            cond.toTokensWithParens(tokens, Precedence.MIN, ExprPosition.Condition)
            thenBranch.toTokens(tokens)
            elseBranch?.toTokens(tokens)
        }

        override fun deepCopy(): If = If(attrs.map { it.deepCopy() }, ifToken, cond.deepCopy(), thenBranch, elseBranch?.let { it.copy(expr = it.expr.deepCopy()) })
    }

    /** A square bracketed indexing expression: `vector[2]`. */
    public data class Index(
        public val attrs: List<Attribute>,
        public val expr: Expr,
        public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public val index: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            bracketToken.surround(tokens) { inner -> index.toTokens(inner) }
        }

        override fun deepCopy(): Index = Index(attrs.map { it.deepCopy() }, expr.deepCopy(), bracketToken, index.deepCopy())
    }

    /** The inferred value of a const generic argument, denoted `_`. */
    public data class Infer(
        public val attrs: List<Attribute>,
        public val underscoreToken: io.github.kotlinmania.syn.token.Underscore,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            underscoreToken.toTokens(tokens)
        }

        override fun deepCopy(): Infer = Infer(attrs.map { it.deepCopy() }, underscoreToken)
    }

    /** A pattern guard that tests whether a pattern matches a value. */
    public data class Let(
        public val attrs: List<Attribute>,
        public val letToken: io.github.kotlinmania.syn.token.Let,
        public val pat: Pat,
        public val eqToken: io.github.kotlinmania.syn.token.Eq,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            letToken.toTokens(tokens)
            pat.toTokens(tokens)
            eqToken.toTokens(tokens)
            if (expr is Struct) {
                io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
                    expr.toTokens(inner)
                }
            } else {
                expr.toTokensWithParens(tokens, Precedence.Let, ExprPosition.RightOperand)
            }
        }

        override fun deepCopy(): Let = Let(attrs.map { it.deepCopy() }, letToken, pat.deepCopy(), eqToken, expr.deepCopy())
    }

    /** A literal in place of an expression: `1`, `"foo"`. */
    public data class Lit(
        val attrs: List<Attribute>,
        val lit: io.github.kotlinmania.syn.Lit,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            lit.toTokens(tokens)
        }

        override fun deepCopy(): Lit = Lit(attrs.map { it.deepCopy() }, lit)
    }

    /** Conditionless loop: `loop { ... }`. */
    public data class Loop(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val loopToken: io.github.kotlinmania.syn.token.Loop,
        public val body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            loopToken.toTokens(tokens)
            body.toTokens(tokens)
        }

        override fun deepCopy(): Loop = Loop(attrs.map { it.deepCopy() }, label?.deepCopy(), loopToken, body)
    }

    /** A macro invocation expression. */
    public data class Macro(
        val attrs: List<Attribute>,
        val mac: io.github.kotlinmania.syn.Macro,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
        }

        override fun deepCopy(): Macro = Macro(attrs.map { it.deepCopy() }, mac.deepCopy())
    }

    /** A `match` expression. */
    public data class Match(
        public val attrs: List<Attribute>,
        public val matchToken: io.github.kotlinmania.syn.token.Match,
        public val expr: Expr,
        public val braceToken: io.github.kotlinmania.syn.token.Brace,
        public val arms: List<Arm>,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            matchToken.toTokens(tokens)
            expr.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (arm in arms) arm.toTokens(inner)
            }
        }

        override fun deepCopy(): Match = Match(attrs.map { it.deepCopy() }, matchToken, expr.deepCopy(), braceToken, arms.map { it.deepCopy() })
    }

    /** A method call expression with optional turbofish and arguments. */
    public data class MethodCall(
        public val attrs: List<Attribute>,
        public val receiver: Expr,
        public val dotToken: io.github.kotlinmania.syn.token.Dot,
        public val method: Ident,
        public val turbofish: PathArguments.AngleBracketed?,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val args: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            receiver.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            dotToken.toTokens(tokens)
            method.toTokens(tokens)
            turbofish?.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                args.toTokens(inner)
            }
        }

        override fun deepCopy(): MethodCall = MethodCall(attrs.map { it.deepCopy() }, receiver.deepCopy(), dotToken, method.copy(), turbofish?.deepCopy() as? PathArguments.AngleBracketed?, parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A parenthesized expression: `(a + b)`. */
    public data class Paren(
        public val attrs: List<Attribute>,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Paren = Paren(attrs.map { it.deepCopy() }, parenToken, expr.deepCopy())
    }

    /** A path expression possibly containing generic parameters. */
    public data class Path(
        val attrs: List<Attribute>,
        val qself: QSelf?,
        val path: io.github.kotlinmania.syn.Path,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
        }

        override fun deepCopy(): Path = Path(attrs.map { it.deepCopy() }, qself, path.deepCopy())
    }

    /** A range expression: `1..2`, `1..`, `..2`, `1..=2`, `..=2`. */
    public data class Range(
        public val attrs: List<Attribute>,
        public val start: Expr?,
        public val limits: RangeLimits,
        public val end: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            start?.toTokensAsRangeStart(tokens)
            limits.toTokens(tokens)
            end?.toTokensWithParens(tokens, Precedence.Range, ExprPosition.RightOperand)
        }

        override fun deepCopy(): Range = Range(attrs.map { it.deepCopy() }, start?.deepCopy(), limits, end?.deepCopy())
    }

    /** Address-of operation: `&raw const place` or `&raw mut place`. */
    public data class RawAddr(
        public val attrs: List<Attribute>,
        public val andToken: io.github.kotlinmania.syn.token.And,
        public val raw: io.github.kotlinmania.syn.token.Raw,
        public val mutability: PointerMutability,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            andToken.toTokens(tokens)
            raw.toTokens(tokens)
            mutability.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
        }

        override fun deepCopy(): RawAddr = RawAddr(attrs.map { it.deepCopy() }, andToken, raw, mutability, expr.deepCopy())
    }

    /** A referencing operation. */
    public data class Reference(
        public val attrs: List<Attribute>,
        public val andToken: io.github.kotlinmania.syn.token.And,
        public val mutability: io.github.kotlinmania.syn.token.Mut?,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            andToken.toTokens(tokens)
            mutability?.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
        }

        override fun deepCopy(): Reference = Reference(attrs.map { it.deepCopy() }, andToken, mutability, expr.deepCopy())
    }

    /** An array literal constructed from one repeated element: `[0u8; N]`. */
    public data class Repeat(
        public val attrs: List<Attribute>,
        public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public val expr: Expr,
        public val semiToken: io.github.kotlinmania.syn.token.Semi,
        public val len: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            bracketToken.surround(tokens) { inner ->
                expr.toTokens(inner)
                semiToken.toTokens(inner)
                len.toTokens(inner)
            }
        }

        override fun deepCopy(): Repeat = Repeat(attrs.map { it.deepCopy() }, bracketToken, expr.deepCopy(), semiToken, len.deepCopy())
    }

    /** A `return`, with an optional value to be returned. */
    public data class Return(
        public val attrs: List<Attribute>,
        public val returnToken: io.github.kotlinmania.syn.token.Return,
        public val expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            returnToken.toTokens(tokens)
            expr?.toTokens(tokens)
        }

        override fun deepCopy(): Return = Return(attrs.map { it.deepCopy() }, returnToken, expr?.deepCopy())
    }

    /** A data-object initialization expression. */
    public data class Struct(
        public val attrs: List<Attribute>,
        public val qself: QSelf?,
        public val path: io.github.kotlinmania.syn.Path,
        public val braceToken: io.github.kotlinmania.syn.token.Brace,
        public val fields: FieldValueList,
        public val dot2Token: io.github.kotlinmania.syn.token.DotDot?,
        public val rest: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                fields.toTokens(inner)
                dot2Token?.toTokens(inner)
                rest?.toTokens(inner)
            }
        }

        override fun deepCopy(): Struct = Struct(attrs.map { it.deepCopy() }, qself, path.deepCopy(), braceToken, fields.copy({ it.deepCopy() }, { it }), dot2Token, rest?.deepCopy())
    }

    /** A try-expression: `expr?`. */
    public data class Try(
        public val attrs: List<Attribute>,
        public val expr: Expr,
        public val questionToken: io.github.kotlinmania.syn.token.Question,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            questionToken.toTokens(tokens)
        }

        override fun deepCopy(): Try = Try(attrs.map { it.deepCopy() }, expr.deepCopy(), questionToken)
    }

    /** A try block: `try { ... }`. */
    public data class TryBlock(
        public val attrs: List<Attribute>,
        public val tryToken: io.github.kotlinmania.syn.token.Try,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            tryToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): TryBlock = TryBlock(attrs.map { it.deepCopy() }, tryToken, block)
    }

    /** A tuple expression: `(a, b, c, d)`. */
    public data class Tuple(
        public val attrs: List<Attribute>,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
                if (elems.len() == 1 && !elems.trailingPunct()) {
                    io.github.kotlinmania.syn.token.Comma.default().toTokens(inner)
                }
            }
        }

        override fun deepCopy(): Tuple = Tuple(attrs.map { it.deepCopy() }, parenToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** A unary prefix operation: negation or dereference. */
    public data class Unary(
        public val attrs: List<Attribute>,
        public val op: UnOp,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            op.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
        }

        override fun deepCopy(): Unary = Unary(attrs.map { it.deepCopy() }, op, expr.deepCopy())
    }

    /** A block expression that permits operations violating memory safety invariants. */
    public data class Unsafe(
        public val attrs: List<Attribute>,
        public val unsafeToken: io.github.kotlinmania.syn.token.Unsafe,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            unsafeToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Unsafe = Unsafe(attrs.map { it.deepCopy() }, unsafeToken, block)
    }

    /** A while loop: `while expr { ... }`. */
    public data class While(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val whileToken: io.github.kotlinmania.syn.token.While,
        public val cond: Expr,
        public val body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            whileToken.toTokens(tokens)
            cond.toTokensWithParens(tokens, Precedence.MIN, ExprPosition.Condition)
            body.toTokens(tokens)
        }

        override fun deepCopy(): While = While(attrs.map { it.deepCopy() }, label?.deepCopy(), whileToken, cond.deepCopy(), body)
    }

    /** A yield expression: `yield expr`. */
    public data class Yield(
        public val attrs: List<Attribute>,
        public val yieldToken: io.github.kotlinmania.syn.token.Yield,
        public val expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            yieldToken.toTokens(tokens)
            expr?.toTokens(tokens)
        }

        override fun deepCopy(): Yield = Yield(attrs.map { it.deepCopy() }, yieldToken, expr?.deepCopy())
    }

    /** Tokens in expression position not interpreted by Syn. */
    public data class Verbatim(
        val tokens: TokenStream,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(tokens))
        }

        override fun deepCopy(): Verbatim = this
    }

    public abstract fun deepCopy(): Expr

    public fun eq(other: Expr): Boolean = equals(other)

    public fun hash(): Int = hashCode()

    public fun fmt(): String = toString()
}

/** A member of a data structure or tuple. */
public sealed class Member : ToTokens {
    public data class Named(
        val ident: Ident,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
        }
    }

    public data class Unnamed(
        val index: Index,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            index.toTokens(tokens)
        }
    }

}

/** A tuple field index such as `0` in `obj.0`. */
public data class Index(
    public val index: UInt,
    public val span: Span,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        val literal = io.github.kotlinmania.procmacro2.Literal.i64Unsuffixed(index.toLong())
        literal.setSpan(span)
        tokens.append(literal)
    }
}

/** A field-value pair in a data-object initialization. */
public data class FieldValue(
    public val attrs: List<Attribute>,
    public val member: Member,
    public val colonToken: io.github.kotlinmania.syn.token.Colon?,
    public val expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        member.toTokens(tokens)
        colonToken?.toTokens(tokens)
        expr.toTokens(tokens)
    }

    public fun deepCopy(): FieldValue = FieldValue(attrs.map { it.deepCopy() }, member, colonToken, expr.deepCopy())
}

/** A label on a `for`, `while`, or `loop`. */
public data class Label(
    public val name: Lifetime,
    public val colonToken: io.github.kotlinmania.syn.token.Colon,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        name.toTokens(tokens)
        colonToken.toTokens(tokens)
    }

    public fun deepCopy(): Label = Label(name.deepCopy(), colonToken)
}

/** One arm of a `match` expression. */
public data class Arm(
    public val attrs: List<Attribute>,
    public val pat: Pat,
    public val guard: IfExpr?,
    public val fatArrowToken: io.github.kotlinmania.syn.token.FatArrow,
    public val body: Expr,
    public val comma: io.github.kotlinmania.syn.token.Comma?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        pat.toTokens(tokens)
        guard?.toTokens(tokens)
        fatArrowToken.toTokens(tokens)
        body.toTokens(tokens)
        comma?.toTokens(tokens)
    }

    public fun deepCopy(): Arm = Arm(attrs.map { it.deepCopy() }, pat.deepCopy(), guard?.let { it.copy(expr = it.expr.deepCopy()) }, fatArrowToken, body.deepCopy(), comma)
}

/** Limit types of a range, inclusive or exclusive. */
public sealed class RangeLimits : ToTokens {
    public data class HalfOpen(
        val token: io.github.kotlinmania.syn.token.DotDot,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    public data class Closed(
        val token: io.github.kotlinmania.syn.token.DotDotEq,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

/** Mutability of a raw pointer. */
public sealed class PointerMutability : ToTokens {
    public data class Const(
        val token: io.github.kotlinmania.syn.token.Const,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    public data class Mut(
        val token: io.github.kotlinmania.syn.token.Mut,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

public object ExprParse : Parse<Expr> {
    override fun parse(input: ParseStream): SynResult<Expr> = parseExpr(input)
}

public fun parseExpr(input: ParseStream): SynResult<Expr> = parseExprFull(input)

public fun parseExprWithEarlierBoundaryRule(input: ParseStream): SynResult<Expr> =
    parseExprWithEarlierBoundaryRuleImpl(input)

public fun peekPrecedence(input: ParseStream): Precedence = peekPrecedenceImpl(input)

public fun checkCast(input: ParseStream): SynResult<Unit> = checkCastImpl(input)

public fun ambiguousExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    ambiguousExprImpl(input, allowStruct)

public fun exprAttrs(input: ParseStream): SynResult<List<Attribute>> = exprAttrsImpl(input)

public fun unaryExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    unaryExprImpl(input, allowStruct)

public fun trailerExpr(input: ParseStream, allowStruct: Boolean, attrs: List<Attribute> = emptyList()): SynResult<Expr> =
    trailerExprImpl(input, allowStruct, attrs)

public fun trailerHelper(input: ParseStream, e: Expr, allowStruct: Boolean): SynResult<Expr> =
    trailerHelperImpl(input, e, allowStruct)

public fun atomExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    atomExprImpl(input, allowStruct)

public fun pathOrMacroOrStruct(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    pathOrMacroOrStructImpl(input, allowStruct)

public fun parenOrTuple(input: ParseStream): SynResult<Expr> = parenOrTupleImpl(input)

public fun arrayOrRepeat(input: ParseStream): SynResult<Expr> = arrayOrRepeatImpl(input)

public fun parseExprBinary(input: ParseStream, lhs: Expr, allowStruct: Boolean, base: Precedence): SynResult<Expr> =
    parseExprBinaryImpl(input, lhs, allowStruct, base)

public fun parseBinopRhs(input: ParseStream, allowStruct: Boolean, left: Precedence): SynResult<Expr> =
    parseBinopRhsImpl(input, allowStruct, left)

public fun parseExprGroup(input: ParseStream): SynResult<Expr> = parseExprGroupImpl(input)

public fun parseExprLet(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Let> =
    parseExprLetImpl(input, allowStruct)

public fun parseFieldValue(input: ParseStream): SynResult<FieldValue> = parseFieldValueImpl(input)

public fun parseMember(input: ParseStream): SynResult<Member> = parseMemberImpl(input)

public fun continueParsingEarly(expr: Expr): Boolean = continueParsingEarlyImpl(expr)

public fun Expr.replaceAttrs(attrs: List<Attribute>): Expr {
    return when (this) {
        is Expr.Binary -> copy(attrs = attrs)
        is Expr.Assign -> copy(attrs = attrs)
        is Expr.Unary -> copy(attrs = attrs)
        is Expr.Call -> copy(attrs = attrs)
        is Expr.MethodCall -> copy(attrs = attrs)
        is Expr.Field -> copy(attrs = attrs)
        is Expr.Index -> copy(attrs = attrs)
        is Expr.Try -> copy(attrs = attrs)
        is Expr.Await -> copy(attrs = attrs)
        is Expr.Paren -> copy(attrs = attrs)
        is Expr.Tuple -> copy(attrs = attrs)
        is Expr.Array -> copy(attrs = attrs)
        is Expr.Repeat -> copy(attrs = attrs)
        is Expr.Range -> copy(attrs = attrs)
        is Expr.If -> copy(attrs = attrs)
        is Expr.While -> copy(attrs = attrs)
        is Expr.ForLoop -> copy(attrs = attrs)
        is Expr.Loop -> copy(attrs = attrs)
        is Expr.Match -> copy(attrs = attrs)
        is Expr.BlockExpr -> copy(attrs = attrs)
        is Expr.Async -> copy(attrs = attrs)
        is Expr.Unsafe -> copy(attrs = attrs)
        is Expr.Return -> copy(attrs = attrs)
        is Expr.Break -> copy(attrs = attrs)
        is Expr.Continue -> copy(attrs = attrs)
        is Expr.Closure -> copy(attrs = attrs)
        is Expr.Macro -> copy(attrs = attrs)
        is Expr.Struct -> copy(attrs = attrs)
        is Expr.Path -> copy(attrs = attrs)
        is Expr.Lit -> copy(attrs = attrs)
        is Expr.Group -> copy(attrs = attrs)
        is Expr.Infer -> copy(attrs = attrs)
        is Expr.Let -> copy(attrs = attrs)
        is Expr.Yield -> copy(attrs = attrs)
        is Expr.TryBlock -> copy(attrs = attrs)
        is Expr.Const -> copy(attrs = attrs)
        is Expr.Reference -> copy(attrs = attrs)
        is Expr.RawAddr -> copy(attrs = attrs)
        is Expr.Cast -> copy(attrs = attrs)
        is Expr.Verbatim -> this
    }
}

public fun Expr.isNamed(name: String): Boolean {
    if (this is Expr.Path) {
        val last = path.segments.last()
        return last?.ident?.toString() == name
    }
    return false
}

public fun Expr.span(): io.github.kotlinmania.procmacro2.Span {
    return spanOf(this)
}

public fun printExpr(expr: Expr, tokens: TokenStream) {
    expr.toTokens(tokens)
}

public fun printSubexpression(expr: Expr, tokens: TokenStream) {
    expr.toTokens(tokens)
}

public fun printExprAssign(e: Expr.Assign, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprAwait(e: Expr.Await, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprBinary(e: Expr.Binary, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprBlock(e: Expr.BlockExpr, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprBreak(e: Expr.Break, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprCall(e: Expr.Call, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprCast(e: Expr.Cast, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprClosure(e: Expr.Closure, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprField(e: Expr.Field, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprIndex(e: Expr.Index, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprLet(e: Expr.Let, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprMethodCall(e: Expr.MethodCall, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprRange(e: Expr.Range, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprRawAddr(e: Expr.Reference, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprReference(e: Expr.Reference, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprReturn(e: Expr.Return, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprTry(e: Expr.Try, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprUnary(e: Expr.Unary, tokens: TokenStream) { e.toTokens(tokens) }
public fun printExprYield(e: Expr.Yield, tokens: TokenStream) { e.toTokens(tokens) }

public fun outerAttrsToTokens(attrs: List<Attribute>, tokens: TokenStream) {
    for (attr in attrs) {
        if (attr.style is AttrStyle.Outer) {
            attr.toTokens(tokens)
        }
    }
}

public fun innerAttrsToTokens(attrs: List<Attribute>, tokens: TokenStream) {
    for (attr in attrs) {
        if (attr.style is AttrStyle.Inner) {
            attr.toTokens(tokens)
        }
    }
}

public fun peekExpr(input: ParseStream): Boolean {
    return input.peek(IdentPeekAny) && !input.peek(AsPeek)
        || input.peek(ParenPeek)
        || input.peek(BracketPeek)
        || input.peek(BracePeek)
        || input.peek(LitPeek)
        || input.peek(NotPeek) && !input.peek(NePeek)
        || input.peek(MinusPeek) && !input.peek(MinusEqPeek) && !input.peek(RArrowPeek)
        || input.peek(StarPeek) && !input.peek(StarEqPeek)
        || input.peek(OrPeek) && !input.peek(OrEqPeek)
        || input.peek(AndPeek) && !input.peek(AndEqPeek)
        || input.peek(DotDotPeek)
        || input.peek(LtPeek) && !input.peek(LePeek) && !input.peek(ShlEqPeek)
        || input.peek(PathSepPeek)
        || input.peek(LifetimePeek)
        || input.peek(PoundPeek)
}

public fun memberFromIdent(ident: io.github.kotlinmania.procmacro2.Ident): Member =
    Member.Named(ident)

public fun memberFromIndex(index: Index): Member =
    Member.Unnamed(index)

public fun memberFromUSize(index: Int): Member =
    Member.Unnamed(indexFromUSize(index))

public fun indexFromUSize(index: Int): Index {
    require(index < 0xFFFFFFFF) { "index overflow" }
    return Index(index.toUInt(), io.github.kotlinmania.procmacro2.Span.callSite())
}

public fun atomLabeled(input: ParseStream): SynResult<Expr> {
    val labelResult = input.parse(LifetimeParse)
    if (labelResult.isFailure) return SynResult.failure((labelResult as SynResult.Failure).error)
    val theLabel = labelResult.getOrThrow()
    val colonResult = input.parse(ColonParse)
    if (colonResult.isFailure) return SynResult.failure((colonResult as SynResult.Failure).error)
    val theLabelColon = colonResult.getOrThrow()
    val label = Label(theLabel, theLabelColon)
    val expr: Expr = when {
        input.peek(WhilePeek) -> {
            val whileResult = parseExprWhileLabeled(input)
            if (whileResult.isFailure) return whileResult
            whileResult.getOrThrow()
        }
        input.peek(ForPeek) -> {
            val forResult = parseExprForLabeled(input)
            if (forResult.isFailure) return forResult
            forResult.getOrThrow()
        }
        input.peek(LoopPeek) -> {
            val loopResult = parseExprLoopLabeled(input)
            if (loopResult.isFailure) return loopResult
            loopResult.getOrThrow()
        }
        input.peek(BracePeek) -> {
            val blockResult = parseExprBlock(input)
            if (blockResult.isFailure) return blockResult
            blockResult.getOrThrow()
        }
        else -> return SynResult.failure(input.error("expected loop or block expression"))
    }
    return when (expr) {
        is Expr.While -> SynResult.success(expr.copy(label = label))
        is Expr.ForLoop -> SynResult.success(expr.copy(label = label))
        is Expr.Loop -> SynResult.success(expr.copy(label = label))
        is Expr.BlockExpr -> SynResult.success(expr.copy(label = label))
        else -> SynResult.success(expr)
    }
}

internal fun parseExprWhileLabeled(input: ParseStream): SynResult<Expr> =
    parseExprWhile(input)

internal fun parseExprForLabeled(input: ParseStream): SynResult<Expr> =
    parseExprFull(input)

internal fun parseExprLoopLabeled(input: ParseStream): SynResult<Expr> =
    parseExprLoop(input)

public fun exprBuiltin(input: ParseStream): SynResult<Expr> {
    val begin = input.fork()
    val kwResult = keyword(input, "builtin")
    if (kwResult.isFailure) return SynResult.failure((kwResult as SynResult.Failure).error)
    val poundResult = input.parse(PoundParse)
    if (poundResult.isFailure) return SynResult.failure((poundResult as SynResult.Failure).error)
    val identResult = input.parse(IdentParse)
    if (identResult.isFailure) return SynResult.failure((identResult as SynResult.Failure).error)
    val parens = parenthesized(input)
    if (parens.isFailure) return SynResult.failure((parens as SynResult.Failure).error)
    val parensVal = parens.getOrThrow()
    parensVal.content.finishChildBuffer()
    val tokens = verbatimBetween(begin, input)
    return SynResult.success(Expr.Verbatim(tokens))
}

public fun restOfPathOrMacroOrStruct(
    qself: QSelf?,
    path: Path,
    input: ParseStream,
    allowStruct: Boolean,
): SynResult<Expr> {
    if (qself == null && input.peek(NotPeek) && !input.peek(NePeek) && path.isModStyle()) {
        val bangResult = input.parse(NotParse)
        if (bangResult.isFailure) return SynResult.failure((bangResult as SynResult.Failure).error)
        val delimResult = parseDelimiter(input)
        if (delimResult.isFailure) return SynResult.failure((delimResult as SynResult.Failure).error)
        val (delimiter, tokens) = delimResult.getOrThrow()
        return SynResult.success(
            Expr.Macro(
                emptyList(),
                Macro(path, bangResult.getOrThrow(), delimiter, tokens),
            ),
        )
    }
    if (allowStruct && input.peek(BracePeek)) {
        val structResult = exprStructHelper(input, qself, path)
        if (structResult.isFailure) return structResult
        return SynResult.success(structResult.getOrThrow())
    }
    return SynResult.success(Expr.Path(emptyList(), qself, path))
}

public fun exprStructHelper(
    input: ParseStream,
    qself: QSelf?,
    path: Path,
): SynResult<Expr.Struct> {
    val braces = braced(input)
    if (braces.isFailure) return SynResult.failure((braces as SynResult.Failure).error)
    val bracesVal = braces.getOrThrow()
    val content = bracesVal.content
    val fields = FieldValueList()
    while (!content.isEmpty()) {
        if (content.peek(DotDotPeek)) {
            val dot2Result = content.parse(DotDotParse)
            if (dot2Result.isFailure) return SynResult.failure((dot2Result as SynResult.Failure).error)
            val rest: Expr? = if (content.isEmpty()) null else {
                val restResult = content.call { parseExprFull(it) }
                if (restResult.isFailure) return SynResult.failure((restResult as SynResult.Failure).error)
                restResult.getOrThrow()
            }
            content.finishChildBuffer()
            return SynResult.success(
                Expr.Struct(
                    emptyList(),
                    qself,
                    path,
                    bracesVal.token,
                    fields,
                    dot2Result.getOrThrow(),
                    rest,
                ),
            )
        }
        val fieldResult = content.call { parseFieldValueImpl(it) }
        if (fieldResult.isFailure) return SynResult.failure((fieldResult as SynResult.Failure).error)
        fields.pushValue(fieldResult.getOrThrow())
        if (content.isEmpty()) break
        val punctResult = content.parse(CommaParse)
        if (punctResult.isFailure) break
        fields.pushPunct(punctResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(
        Expr.Struct(emptyList(), qself, path, bracesVal.token, fields, null, null),
    )
}

public fun exprLet(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Let> {
    val letResult = input.parse(LetParse)
    if (letResult.isFailure) return SynResult.failure((letResult as SynResult.Failure).error)
    val patResult = parsePatMultiWithLeadingVert(input)
    if (patResult.isFailure) return SynResult.failure((patResult as SynResult.Failure).error)
    val eqResult = input.parse(EqParse)
    if (eqResult.isFailure) return SynResult.failure((eqResult as SynResult.Failure).error)
    val lhsResult = unaryExprImpl(input, allowStruct)
    if (lhsResult.isFailure) return SynResult.failure((lhsResult as SynResult.Failure).error)
    val exprResult = parseExprBinaryImpl(input, lhsResult.getOrThrow(), allowStruct, Precedence.Compare)
    if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
    return SynResult.success(
        Expr.Let(
            emptyList(),
            letResult.getOrThrow(),
            patResult.getOrThrow(),
            eqResult.getOrThrow(),
            exprResult.getOrThrow(),
        ),
    )
}

public fun exprUnary(input: ParseStream, attrs: List<Attribute>, allowStruct: Boolean): SynResult<Expr.Unary> {
    val opResult = input.parse(UnOpParse)
    if (opResult.isFailure) return SynResult.failure((opResult as SynResult.Failure).error)
    val innerResult = unaryExprImpl(input, allowStruct)
    if (innerResult.isFailure) return SynResult.failure((innerResult as SynResult.Failure).error)
    return SynResult.success(Expr.Unary(attrs, opResult.getOrThrow(), innerResult.getOrThrow()))
}

public fun exprBecome(input: ParseStream): SynResult<Expr> {
    val begin = input.fork()
    val becomeResult = input.parse(BecomeParse)
    if (becomeResult.isFailure) return SynResult.failure((becomeResult as SynResult.Failure).error)
    val exprResult = parseExprFull(input)
    if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
    val tokens = verbatimBetween(begin, input)
    return SynResult.success(Expr.Verbatim(tokens))
}

public fun exprClosure(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Closure> {
    val lifetimes: BoundLifetimes? = null
    val constnessResult = input.parse(ConstParse)
    val constness = if (constnessResult.isSuccess) constnessResult.getOrThrow() else null
    val movabilityResult = input.parse(StaticParse)
    val movability = if (movabilityResult.isSuccess) movabilityResult.getOrThrow() else null
    val asyncnessResult = input.parse(AsyncParse)
    val asyncness = if (asyncnessResult.isSuccess) asyncnessResult.getOrThrow() else null
    val captureResult = input.parse(MoveParse)
    val capture = if (captureResult.isSuccess) captureResult.getOrThrow() else null
    val or1Result = input.parse(OrParse)
    if (or1Result.isFailure) return SynResult.failure((or1Result as SynResult.Failure).error)
    val inputs = PatList()
    while (true) {
        if (input.peek(OrPeek)) break
        val valueResult = closureArg(input)
        if (valueResult.isFailure) return SynResult.failure((valueResult as SynResult.Failure).error)
        inputs.pushValue(valueResult.getOrThrow())
        if (input.peek(OrPeek)) break
        val punctResult = input.parse(CommaParse)
        if (punctResult.isFailure) return SynResult.failure((punctResult as SynResult.Failure).error)
        inputs.pushPunct(punctResult.getOrThrow())
    }
    val or2Result = input.parse(OrParse)
    if (or2Result.isFailure) return SynResult.failure((or2Result as SynResult.Failure).error)
    val output: ReturnType
    val body: Expr
    if (input.peek(RArrowPeek)) {
        val arrowResult = input.parse(RArrowParse)
        if (arrowResult.isFailure) return SynResult.failure((arrowResult as SynResult.Failure).error)
        val tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return SynResult.failure((tyResult as SynResult.Failure).error)
        val blockResult = parseExprBlock(input)
        if (blockResult.isFailure) return SynResult.failure((blockResult as SynResult.Failure).error)
        output = ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow())
        body = blockResult.getOrThrow()
    } else {
        val bodyResult = ambiguousExprImpl(input, allowStruct)
        if (bodyResult.isFailure) return SynResult.failure((bodyResult as SynResult.Failure).error)
        output = ReturnType.Default
        body = bodyResult.getOrThrow()
    }
    return SynResult.success(
        Expr.Closure(
            emptyList(),
            constness,
            asyncness,
            capture,
            or1Result.getOrThrow(),
            inputs,
            or2Result.getOrThrow(),
            output,
            body,
        ),
    )
}

public fun closureArg(input: ParseStream): SynResult<Pat> {
    val patResult = parsePatSingle(input)
    if (patResult.isFailure) return patResult
    val pat = patResult.getOrThrow()
    if (input.peek(ColonPeek)) {
        val colonResult = input.parse(ColonParse)
        if (colonResult.isFailure) return SynResult.failure((colonResult as SynResult.Failure).error)
        val tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return SynResult.failure((tyResult as SynResult.Failure).error)
        return SynResult.success(Pat.TypeAscription(emptyList(), pat, colonResult.getOrThrow(), tyResult.getOrThrow()))
    }
    return SynResult.success(pat)
}

public fun exprBreak(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Break> {
    val breakResult = input.parse(BreakParse)
    if (breakResult.isFailure) return SynResult.failure((breakResult as SynResult.Failure).error)
    val ahead = input.fork()
    val labelResult = ahead.parse(LifetimeParse)
    val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
    if (label != null && ahead.peek(ColonPeek)) {
        val exprResult = parseExprFull(input)
        if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
        return SynResult.failure(SynError.new2(label.apostrophe, input.span(), "parentheses required"))
    }
    input.advanceTo(ahead)
    val expr: Expr? = if (peekExpr(input) && (allowStruct || !input.peek(BracePeek))) {
        val exprResult = parseExprFull(input)
        if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
        exprResult.getOrThrow()
    } else {
        null
    }
    return SynResult.success(
        Expr.Break(emptyList(), breakResult.getOrThrow(), label, expr),
    )
}

public fun exprRange(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Range> {
    val limitsResult = input.parse(RangeLimitsParse)
    if (limitsResult.isFailure) return SynResult.failure((limitsResult as SynResult.Failure).error)
    val limits = limitsResult.getOrThrow()
    val endResult = parseRangeEnd(input, limits, allowStruct)
    if (endResult.isFailure) return SynResult.failure((endResult as SynResult.Failure).error)
    return SynResult.success(
        Expr.Range(emptyList(), null, limits, endResult.getOrThrow()),
    )
}

public object RangeLimitsParse : Parse<RangeLimits> {
    override fun parse(input: ParseStream): SynResult<RangeLimits> {
        if (input.peek(DotDotEqPeek)) {
            val result = input.parse(DotDotEqParse)
            if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
            return SynResult.success(RangeLimits.Closed(result.getOrThrow()))
        }
        if (input.peek(DotDotPeek) && !input.peek(DotDotDotPeek)) {
            val result = input.parse(DotDotParse)
            if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
            return SynResult.success(RangeLimits.HalfOpen(result.getOrThrow()))
        }
        return SynResult.failure(input.error("expected .. or ..="))
    }
}

public object ArmParse : Parse<Arm> {
    override fun parse(input: ParseStream): SynResult<Arm> {
        val attrs = emptyList<Attribute>()
        val patResult = parsePatMultiWithLeadingVert(input)
        if (patResult.isFailure) return SynResult.failure((patResult as SynResult.Failure).error)
        val guard: IfExpr? = if (input.peek(IfPeek)) {
            val ifToken = input.parse(IfParse).getOrThrow()
            val guardExpr = parseExprFull(input)
            if (guardExpr.isFailure) return SynResult.failure((guardExpr as SynResult.Failure).error)
            IfExpr(ifToken, guardExpr.getOrThrow())
        } else {
            null
        }
        val fatArrowResult = input.parse(FatArrowParse)
        if (fatArrowResult.isFailure) return SynResult.failure((fatArrowResult as SynResult.Failure).error)
        val bodyResult = parseExprWithEarlierBoundaryRuleImpl(input)
        if (bodyResult.isFailure) return SynResult.failure((bodyResult as SynResult.Failure).error)
        val commaResult = input.parse(CommaParse)
        val comma = if (commaResult.isSuccess) commaResult.getOrThrow() else null
        return SynResult.success(
            Arm(
                attrs,
                patResult.getOrThrow(),
                guard,
                fatArrowResult.getOrThrow(),
                bodyResult.getOrThrow(),
                comma,
            ),
        )
    }
}

public fun parseRangeEnd(input: ParseStream, limits: RangeLimits, allowStruct: Boolean): SynResult<Expr?> {
    val isHalfOpen = limits is RangeLimits.HalfOpen
    val stop = isHalfOpen && (
        input.isEmpty() ||
            input.peek(CommaPeek) ||
            input.peek(SemiPeek) ||
            (input.peek(DotPeek) && !input.peek(DotDotPeek)) ||
            input.peek(QuestionPeek) ||
            input.peek(FatArrowPeek) ||
            (!allowStruct && input.peek(BracePeek)) ||
            input.peek(EqPeek) ||
            input.peek(PlusPeek) ||
            input.peek(AsPeek)
    )
    if (stop) {
        return SynResult.success(null)
    }
    val endResult = parseBinopRhsImpl(input, allowStruct, Precedence.Range)
    if (endResult.isFailure) return SynResult.failure((endResult as SynResult.Failure).error)
    return SynResult.success(endResult.getOrThrow())
}

public fun parseObsoleteRangeLimits(input: ParseStream): SynResult<RangeLimits> {
    val dotDot = input.peek(DotDotPeek)
    val dotDotEq = dotDot && input.peek(DotDotEqPeek)
    val dotDotDot = dotDot && input.peek(DotDotDotPeek)
    if (dotDotEq) {
        val result = input.parse(DotDotEqParse)
        if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
        return SynResult.success(RangeLimits.Closed(result.getOrThrow()))
    }
    if (dotDot) {
        val result = input.parse(DotDotParse)
        if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
        return SynResult.success(RangeLimits.HalfOpen(result.getOrThrow()))
    }
    return SynResult.failure(input.error("expected .. or ..="))
}

public fun parseMultipleArms(input: ParseStream): SynResult<List<Arm>> {
    val arms = mutableListOf<Arm>()
    while (!input.isEmpty()) {
        val armResult = input.parse(ArmParse)
        if (armResult.isFailure) return SynResult.failure((armResult as SynResult.Failure).error)
        arms.add(armResult.getOrThrow())
    }
    return SynResult.success(arms)
}

public fun multiIndex(e: Expr, dotToken: io.github.kotlinmania.syn.token.Dot, float: LitFloat): SynResult<MultiIndexResult> {
    val floatToken = float.token()
    val floatSpan = floatToken.span()
    var floatRepr = floatToken.toString()
    val trailingDot = floatRepr.endsWith('.')
    if (trailingDot) {
        floatRepr = floatRepr.dropLast(1)
    }
    var offset = 0
    var currentExpr = e
    var currentDot = dotToken
    for (part in floatRepr.split('.')) {
        val index: Index = Index(part.toUInt(), floatSpan)
        val partEnd = offset + part.length
        val base = currentExpr
        currentExpr = Expr.Field(
            emptyList(),
            base,
            currentDot,
            Member.Unnamed(index),
        )
        currentDot = io.github.kotlinmania.syn.token.Dot.from(floatSpan)
        offset = partEnd + 1
    }
    return SynResult.success(MultiIndexResult(currentExpr, !trailingDot))
}

@JvmInline
internal value class AllowStruct(val value: Boolean)

public fun parseWithoutEagerBrace(input: ParseStream): SynResult<Expr> =
    ambiguousExprImpl(input, allowStruct = false)

public fun parseWithEarlierBoundaryRule(input: ParseStream): SynResult<Expr> =
    parseExprWithEarlierBoundaryRuleImpl(input)

public fun peek(input: ParseStream): Boolean = peekExpr(input)

public fun from(ident: io.github.kotlinmania.procmacro2.Ident): Member = Member.Named(ident)

public fun from(index: Index): Member = Member.Unnamed(index)

public fun from(index: Int): Member = Member.Unnamed(Index(index.toUInt(), Span.callSite()))

public fun exprGroup(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    parseExprGroupImpl(input)

internal fun clone(allowStruct: AllowStruct): AllowStruct = allowStruct

public fun parseObsolete(input: ParseStream): SynResult<RangeLimits> = parseObsoleteRangeLimits(input)

public fun parseMultiple(input: ParseStream): SynResult<List<Arm>> = parseMultipleArms(input)
