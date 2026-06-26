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
    var needsParens = needsParens(parentPrecedence, position)
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
        is Expr.Assign -> toTokensAsCondition(tokens)
        is Expr.Await -> toTokensAsCondition(tokens)
        is Expr.Binary -> toTokensAsCondition(tokens)
        is Expr.Break -> toTokensAsCondition(tokens)
        is Expr.Call -> toTokensAsCondition(tokens)
        is Expr.Closure -> toTokensAsCondition(tokens)
        is Expr.Field -> toTokensAsCondition(tokens)
        is Expr.Index -> toTokensAsCondition(tokens)
        is Expr.MethodCall -> toTokensAsCondition(tokens)
        is Expr.Let -> toTokensAsCondition(tokens)
        is Expr.RawAddr -> toTokensAsCondition(tokens)
        is Expr.Reference -> toTokensAsCondition(tokens)
        is Expr.Range -> toTokensAsCondition(tokens)
        is Expr.Return -> toTokensAsCondition(tokens)
        is Expr.Try -> toTokensAsCondition(tokens)
        is Expr.Unary -> toTokensAsCondition(tokens)
        is Expr.Yield -> toTokensAsCondition(tokens)
        else -> toTokens(tokens)
    }
}

private fun Expr.toTokensAsRightmostCondition(tokens: TokenStream) {
    if (this is Expr.BlockExpr && attrs.isEmpty() && label == null) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensWithParens(tokens, Precedence.MIN, ExprPosition.Condition)
    }
}

private fun Expr.toTokensAsRightmostConditionOperand(
    tokens: TokenStream,
    parentPrecedence: Precedence,
    position: ExprPosition,
) {
    if (needsParens(parentPrecedence, position)) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensAsRightmostCondition(tokens)
    }
}

private fun Expr.toTokensAsConditionPostfixBase(tokens: TokenStream) {
    if (Precedence.of(this) < Precedence.Unambiguous ||
        this is Expr.Struct ||
        this is Expr.BlockExpr &&
        attrs.isEmpty() &&
        label == null ||
        this is Expr.Break &&
        expr == null ||
        this is Expr.Return &&
        expr == null ||
        this is Expr.Yield &&
        expr == null
    ) {
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
        toTokensAsConditionTail(tokens)
    }
}

private fun Expr.toTokensAsConditionBreakValue(tokens: TokenStream) {
    if (needsParensAsConditionJumpValue()) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensAsConditionTail(tokens)
    }
}

private fun Expr.toTokensAsOptionalOperand(tokens: TokenStream) {
    when (this) {
        is Expr.Await -> toTokensAsOptionalOperand(tokens)
        is Expr.Call -> toTokensAsOptionalOperand(tokens)
        is Expr.Closure -> toTokensAsOptionalOperand(tokens)
        is Expr.Field -> toTokensAsOptionalOperand(tokens)
        is Expr.Index -> toTokensAsOptionalOperand(tokens)
        is Expr.MethodCall -> toTokensAsOptionalOperand(tokens)
        is Expr.Try -> toTokensAsOptionalOperand(tokens)
        else -> toTokens(tokens)
    }
}

private fun Expr.toTokensAsOptionalOperandPostfixBase(tokens: TokenStream) {
    if (Precedence.of(this) < Precedence.Unambiguous ||
        this is Expr.BlockExpr &&
        attrs.isEmpty() &&
        label == null ||
        this is Expr.Break &&
        expr == null ||
        this is Expr.Return &&
        expr == null ||
        this is Expr.Yield &&
        expr == null
    ) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokens(tokens)
    }
}

private fun Expr.toTokensAsConditionTail(tokens: TokenStream) {
    if (this is Expr.Break &&
        expr == null ||
        this is Expr.Path ||
        this is Expr.Range &&
        end == null ||
        this is Expr.Return &&
        expr == null ||
        this is Expr.Yield &&
        expr == null
    ) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
        return
    }

    when (this) {
        is Expr.Assign -> toTokensAsConditionTail(tokens)
        is Expr.Await -> toTokensAsOptionalOperand(tokens)
        is Expr.Binary -> toTokensAsConditionTail(tokens)
        is Expr.Break -> toTokensAsConditionTail(tokens)
        is Expr.Call -> toTokensAsOptionalOperand(tokens)
        is Expr.Closure -> toTokensAsConditionTail(tokens)
        is Expr.Field -> toTokensAsOptionalOperand(tokens)
        is Expr.Index -> toTokensAsOptionalOperand(tokens)
        is Expr.Let -> toTokensAsConditionTail(tokens)
        is Expr.MethodCall -> toTokensAsOptionalOperand(tokens)
        is Expr.Range -> toTokensAsConditionTail(tokens)
        is Expr.RawAddr -> toTokensAsConditionTail(tokens)
        is Expr.Reference -> toTokensAsConditionTail(tokens)
        is Expr.Return -> toTokensAsConditionTail(tokens)
        is Expr.Try -> toTokensAsOptionalOperand(tokens)
        is Expr.Unary -> toTokensAsConditionTail(tokens)
        is Expr.Yield -> toTokensAsConditionTail(tokens)
        else -> toTokens(tokens)
    }
}

private fun Expr.toTokensAsConditionTailOperand(
    tokens: TokenStream,
    parentPrecedence: Precedence,
    position: ExprPosition,
) {
    if (needsParens(parentPrecedence, position)) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else {
        toTokensAsConditionTail(tokens)
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
    if (Precedence.of(this).ordinal <= Precedence.Range.ordinal ||
        endsWithRange() ||
        this !is Expr.Binary &&
        rightEdgeNeedsGroupBeforeRange()
    ) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            toTokens(inner)
        }
    } else if (this is Expr.Binary && attrs.isEmpty()) {
        toTokensAsRangeStart(tokens)
    } else {
        toTokensWithParens(tokens, Precedence.Range, ExprPosition.LeftOperand)
    }
}

private fun Expr.needsParensAsConditionJumpValue(): Boolean {
    if (canConsumeTrailingBraceAsStruct()) return true
    return when (this) {
        is Expr.Assign -> true
        is Expr.Binary -> true
        is Expr.BlockExpr -> attrs.isEmpty() && label == null
        is Expr.Break -> {
            val e = expr
            e !is Expr.BlockExpr || e.attrs.isNotEmpty() || label != null
        }
        is Expr.Let -> true
        is Expr.Path -> true
        is Expr.Range -> {
            val e = end
            e == null || e.canConsumeTrailingBraceAsStruct()
        }
        is Expr.Return -> expr == null
        is Expr.Yield -> expr == null
        else -> false
    }
}

private fun Expr.canConsumeTrailingBraceAsStruct(): Boolean =
    when (this) {
        is Expr.Assign -> right.canConsumeTrailingBraceAsStruct()
        is Expr.Binary -> right.canConsumeTrailingBraceAsStruct()
        is Expr.Break -> expr?.canConsumeTrailingBraceAsStruct() == true
        is Expr.Cast -> false
        is Expr.Path -> true
        is Expr.RawAddr -> expr.canConsumeTrailingBraceAsStruct()
        is Expr.Reference -> expr.canConsumeTrailingBraceAsStruct()
        is Expr.Range -> end?.canConsumeTrailingBraceAsStruct() == true
        is Expr.Return -> expr?.canConsumeTrailingBraceAsStruct() == true
        is Expr.Try -> expr.canConsumeTrailingBraceAsStruct()
        is Expr.Unary -> expr.canConsumeTrailingBraceAsStruct()
        is Expr.Yield -> expr?.canConsumeTrailingBraceAsStruct() == true
        else -> false
    }

private fun Expr.Await.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    base.toTokensAsConditionPostfixBase(tokens)
    dotToken.toTokens(tokens)
    awaitToken.toTokens(tokens)
}

private fun Expr.Assign.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    var emit = { target: TokenStream ->
        left.toTokensWithParens(target, Precedence.Assign, ExprPosition.LeftOperand)
        eqToken.toTokens(target)
        right.toTokensAsRightmostConditionOperand(target, Precedence.Assign, ExprPosition.RightOperand)
    }
    if (attrs.isNotEmpty()) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, emit)
    } else {
        emit(tokens)
    }
}

private fun Expr.Assign.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    var emit = { target: TokenStream ->
        left.toTokensWithParens(target, Precedence.Assign, ExprPosition.LeftOperand)
        eqToken.toTokens(target)
        right.toTokensAsConditionTailOperand(target, Precedence.Assign, ExprPosition.RightOperand)
    }
    if (attrs.isNotEmpty()) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, emit)
    } else {
        emit(tokens)
    }
}

private fun Expr.Break.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    breakToken.toTokens(tokens)
    label?.toTokens(tokens)
    var e = expr
    if (e != null) {
        if (label == null && exprLeadingLabel(e)) {
            io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
                e.toTokens(inner)
            }
        } else {
            e.toTokensAsConditionBreakValue(tokens)
        }
    }
}

private fun Expr.Break.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    breakToken.toTokens(tokens)
    label?.toTokens(tokens)
    var e = expr
    if (e != null) {
        if (label == null && exprLeadingLabel(e)) {
            io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
                e.toTokens(inner)
            }
        } else {
            e.toTokensAsConditionTail(tokens)
        }
    }
}

private fun Expr.Binary.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    var emit = { target: TokenStream ->
        var precedence = Precedence.ofBinop(op)
        if (left.isValueLessJump() && binOpCanBeginExpr(op)) {
            io.github.kotlinmania.syn.token.Paren.default().surround(target) { inner ->
                left.toTokens(inner)
            }
        } else if (left.endsWithRange()) {
            io.github.kotlinmania.syn.token.Paren.default().surround(target) { inner ->
                left.toTokens(inner)
            }
        } else {
            left.toTokensWithParens(target, precedence, ExprPosition.LeftOperand)
        }
        op.toTokens(target)
        right.toTokensAsRightmostConditionOperand(target, precedence, ExprPosition.RightOperand)
    }
    if (attrs.isNotEmpty()) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, emit)
    } else {
        emit(tokens)
    }
}

private fun Expr.Binary.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    var emit = { target: TokenStream ->
        var precedence = Precedence.ofBinop(op)
        if (left.isValueLessJump() && binOpCanBeginExpr(op)) {
            io.github.kotlinmania.syn.token.Paren.default().surround(target) { inner ->
                left.toTokens(inner)
            }
        } else if (left.endsWithRange()) {
            io.github.kotlinmania.syn.token.Paren.default().surround(target) { inner ->
                left.toTokens(inner)
            }
        } else {
            left.toTokensWithParens(target, precedence, ExprPosition.LeftOperand)
        }
        op.toTokens(target)
        right.toTokensAsConditionTailOperand(target, precedence, ExprPosition.RightOperand)
    }
    if (attrs.isNotEmpty()) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, emit)
    } else {
        emit(tokens)
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

private fun Expr.Closure.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    constness?.toTokens(tokens)
    asyncness?.toTokens(tokens)
    capture?.toTokens(tokens)
    or1Token.toTokens(tokens)
    inputs.toTokens(tokens)
    or2Token.toTokens(tokens)
    output.toTokens(tokens)
    body.toTokensAsConditionTail(tokens)
}

private fun Expr.Closure.toTokensAsOptionalOperand(tokens: TokenStream) {
    toTokensAsConditionTail(tokens)
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

private fun Expr.Let.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    letToken.toTokens(tokens)
    pat.toTokens(tokens)
    eqToken.toTokens(tokens)
    expr.toTokensAsRightmostConditionOperand(tokens, Precedence.Let, ExprPosition.RightOperand)
}

private fun Expr.Let.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    letToken.toTokens(tokens)
    pat.toTokens(tokens)
    eqToken.toTokens(tokens)
    expr.toTokensAsConditionTailOperand(tokens, Precedence.Let, ExprPosition.RightOperand)
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

private fun Expr.Range.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    start?.toTokensAsRangeStart(tokens)
    limits.toTokens(tokens)
    end?.toTokensAsRightmostConditionOperand(tokens, Precedence.Range, ExprPosition.RightOperand)
}

private fun Expr.Range.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    start?.toTokensAsRangeStart(tokens)
    limits.toTokens(tokens)
    end?.toTokensAsConditionTailOperand(tokens, Precedence.Range, ExprPosition.RightOperand)
}

private fun Expr.RawAddr.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    andToken.toTokens(tokens)
    raw.toTokens(tokens)
    mutability.toTokens(tokens)
    expr.toTokensAsRightmostConditionOperand(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
}

private fun Expr.RawAddr.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    andToken.toTokens(tokens)
    raw.toTokens(tokens)
    mutability.toTokens(tokens)
    expr.toTokensAsConditionTailOperand(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
}

private fun Expr.Reference.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    andToken.toTokens(tokens)
    mutability?.toTokens(tokens)
    expr.toTokensAsRightmostConditionOperand(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
}

private fun Expr.Reference.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    andToken.toTokens(tokens)
    mutability?.toTokens(tokens)
    expr.toTokensAsConditionTailOperand(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
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

private fun Expr.Return.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    returnToken.toTokens(tokens)
    expr?.toTokensAsConditionTail(tokens)
}

private fun Expr.Yield.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    yieldToken.toTokens(tokens)
    expr?.toTokensAsConditionTail(tokens)
}

private fun Expr.Unary.toTokensAsCondition(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    op.toTokens(tokens)
    expr.toTokensAsRightmostConditionOperand(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
}

private fun Expr.Unary.toTokensAsConditionTail(tokens: TokenStream) {
    for (attr in attrs) attr.toTokens(tokens)
    op.toTokens(tokens)
    expr.toTokensAsConditionTailOperand(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
}

private fun Expr.Binary.toTokensAsRangeStart(tokens: TokenStream) {
    var precedence = Precedence.ofBinop(op)
    if (left.isValueLessJump() && binOpCanBeginExpr(op)) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            left.toTokens(inner)
        }
    } else if (left.endsWithRange()) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            left.toTokens(inner)
        }
    } else {
        left.toTokensWithParens(tokens, precedence, ExprPosition.LeftOperand)
    }
    op.toTokens(tokens)
    var r = right
    if (r.isValueLessJump()) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            r.toTokens(inner)
        }
    } else if (r is Expr.Binary &&
        r.attrs.isEmpty() &&
        !r.needsParens(precedence, ExprPosition.RightOperand)
    ) {
        r.toTokensAsRangeStart(tokens)
    } else if (r.rightEdgeNeedsGroupBeforeRange()) {
        io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
            r.toTokens(inner)
        }
    } else {
        r.toTokensWithParens(tokens, precedence, ExprPosition.RightOperand)
    }
}

private fun Expr.needsParens(parentPrecedence: Precedence, position: ExprPosition): Boolean {
    if (position == ExprPosition.Condition) {
        return this is Expr.Struct ||
            this is Expr.Return &&
            expr == null ||
            this is Expr.Yield &&
            expr == null
    }

    if (position == ExprPosition.PostfixBase) {
        return Precedence.of(this) < Precedence.Unambiguous ||
            this is Expr.Break &&
            expr == null ||
            this is Expr.Return &&
            expr == null ||
            this is Expr.Yield &&
            expr == null
    }

    if (position == ExprPosition.LeftOperand && parentPrecedence == Precedence.Assign && this is Expr.Range) {
        return true
    }

    if (position == ExprPosition.LeftOperand && parentPrecedence == Precedence.Assign && this is Expr.Let) {
        return true
    }

    if (position == ExprPosition.LeftOperand && parentPrecedence == Precedence.Range && this.isValueLessJump()) {
        return true
    }

    if (position == ExprPosition.RightOperand &&
        parentPrecedence == Precedence.Compare &&
        this is Expr.Range &&
        start == null
    ) {
        return false
    }

    if (position == ExprPosition.RightOperand &&
        this is Expr.Range &&
        start == null
    ) {
        return false
    }

    if (position == ExprPosition.RightOperand &&
        parentPrecedence == Precedence.Range &&
        (this is Expr.Return && expr == null || this is Expr.Yield && expr == null)
    ) {
        return false
    }

    if ((position == ExprPosition.LeftOperand || position == ExprPosition.RightOperand) &&
        (
            this is Expr.Assign ||
                (this is Expr.BlockExpr && !(parentPrecedence == Precedence.Range && position == ExprPosition.RightOperand)) ||
                this is Expr.Cast ||
                this is Expr.Struct ||
                (this is Expr.Macro && mac.isBrace())
        )
    ) {
        return true
    }

    var childPrecedence = Precedence.of(this)
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
        is BinOp.Lt,
        -> true
        else -> false
    }

private fun Expr.isValueLessJump(): Boolean =
    this is Expr.Break &&
        expr == null ||
        this is Expr.Return &&
        expr == null ||
        this is Expr.Yield &&
        expr == null

private fun Expr.endsWithRange(): Boolean =
    when (this) {
        is Expr.Assign -> right.endsWithRange()
        is Expr.Binary -> right.endsWithRange()
        is Expr.Cast -> expr.endsWithRange()
        is Expr.Range -> true
        else -> false
    }

private fun Expr.rightEdgeNeedsGroupBeforeRange(): Boolean =
    when (this) {
        is Expr.Assign -> right.rightEdgeNeedsGroupBeforeRange()
        is Expr.Binary -> right.isValueLessJump() || right.rightEdgeNeedsGroupBeforeRange()
        is Expr.Break -> expr?.rightEdgeNeedsGroupBeforeRange() ?: true
        is Expr.Cast -> expr.rightEdgeNeedsGroupBeforeRange()
        is Expr.Closure -> body.rightEdgeNeedsGroupBeforeRange()
        is Expr.Let -> expr.rightEdgeNeedsGroupBeforeRange()
        is Expr.RawAddr -> expr.rightEdgeNeedsGroupBeforeRange()
        is Expr.Reference -> expr.rightEdgeNeedsGroupBeforeRange()
        is Expr.Range -> true
        is Expr.Return -> expr?.rightEdgeNeedsGroupBeforeRange() ?: true
        is Expr.Try -> expr.rightEdgeNeedsGroupBeforeRange()
        is Expr.Unary -> expr.rightEdgeNeedsGroupBeforeRange()
        is Expr.Yield -> expr?.rightEdgeNeedsGroupBeforeRange() ?: true
        else -> false
    }

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
        public var attrs: MutableList<Attribute>,
        public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public var elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            bracketToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }

        override fun deepCopy(): Array = Array(attrs.mapTo(mutableListOf()) { it.deepCopy() }, bracketToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** An assignment expression: `a = compute()`. */
    public data class Assign(
        public var attrs: MutableList<Attribute>,
        public var left: Expr,
        public var eqToken: io.github.kotlinmania.syn.token.Eq,
        public var right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            val emit = { target: TokenStream ->
                left.toTokensWithParens(target, Precedence.Assign, ExprPosition.LeftOperand)
                eqToken.toTokens(target)
                right.toTokensWithParens(target, Precedence.Assign, ExprPosition.RightOperand)
            }
            if (attrs.isNotEmpty()) {
                io.github.kotlinmania.syn.token.Paren
                    .default()
                    .surround(tokens, emit)
            } else {
                emit(tokens)
            }
        }

        override fun deepCopy(): Assign = Assign(attrs.mapTo(mutableListOf()) { it.deepCopy() }, left.deepCopy(), eqToken, right.deepCopy())
    }

    /** An async block: `async { ... }`. */
    public data class Async(
        public var attrs: MutableList<Attribute>,
        public var asyncToken: io.github.kotlinmania.syn.token.Async,
        public var capture: io.github.kotlinmania.syn.token.Move?,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            asyncToken.toTokens(tokens)
            capture?.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Async = Async(attrs.mapTo(mutableListOf()) { it.deepCopy() }, asyncToken, capture, block)
    }

    /** An await expression: `fut.await`. */
    public data class Await(
        public var attrs: MutableList<Attribute>,
        public var base: Expr,
        public var dotToken: io.github.kotlinmania.syn.token.Dot,
        public var awaitToken: io.github.kotlinmania.syn.token.Await,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            base.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            dotToken.toTokens(tokens)
            awaitToken.toTokens(tokens)
        }

        override fun deepCopy(): Await = Await(attrs.mapTo(mutableListOf()) { it.deepCopy() }, base.deepCopy(), dotToken, awaitToken)
    }

    /** A binary operation: `a + b`, `a += b`. */
    public data class Binary(
        public var attrs: MutableList<Attribute>,
        public var left: Expr,
        public var op: BinOp,
        public var right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            val emit = { target: TokenStream ->
                val precedence = Precedence.ofBinop(op)
                if (left.isValueLessJump() && binOpCanBeginExpr(op)) {
                    io.github.kotlinmania.syn.token.Paren.default().surround(target) { inner ->
                        left.toTokens(inner)
                    }
                } else if (left.endsWithRange()) {
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
                io.github.kotlinmania.syn.token.Paren
                    .default()
                    .surround(tokens, emit)
            } else {
                emit(tokens)
            }
        }

        override fun deepCopy(): Binary = Binary(attrs.mapTo(mutableListOf()) { it.deepCopy() }, left.deepCopy(), op, right.deepCopy())
    }

    /** A blocked scope: `{ ... }`. */
    public data class BlockExpr(
        public var attrs: MutableList<Attribute>,
        public var label: Label?,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): BlockExpr = BlockExpr(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), block)
    }

    /** A `break`, with an optional label to break and an optional expression. */
    public data class Break(
        public var attrs: MutableList<Attribute>,
        public var breakToken: io.github.kotlinmania.syn.token.Break,
        public var label: Lifetime?,
        public var expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            breakToken.toTokens(tokens)
            label?.toTokens(tokens)
            val e = expr
            if (e != null) {
                if ((label == null && exprLeadingLabel(e)) || (e is Break && e.expr == null)) {
                    io.github.kotlinmania.syn.token.Paren.default().surround(tokens) { inner ->
                        e.toTokens(inner)
                    }
                } else {
                    e.toTokens(tokens)
                }
            }
        }

        override fun deepCopy(): Break = Break(attrs.mapTo(mutableListOf()) { it.deepCopy() }, breakToken, label?.deepCopy(), expr?.deepCopy())
    }

    /** A function call expression: `invoke(a, b)`. */
    public data class Call(
        public var attrs: MutableList<Attribute>,
        public var func: Expr,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var args: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            func.toTokensAsCallee(tokens)
            parenToken.surround(tokens) { inner ->
                args.toTokens(inner)
            }
        }

        override fun deepCopy(): Call = Call(attrs.mapTo(mutableListOf()) { it.deepCopy() }, func.deepCopy(), parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A cast expression: `foo as f64`. */
    public data class Cast(
        public var attrs: MutableList<Attribute>,
        public var expr: Expr,
        public var asToken: io.github.kotlinmania.syn.token.As,
        public var ty: SynType,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            val emit = { target: TokenStream ->
                expr.toTokensWithParens(target, Precedence.Cast, ExprPosition.LeftOperand)
                asToken.toTokens(target)
                ty.toTokens(target)
            }
            if (attrs.isNotEmpty()) {
                io.github.kotlinmania.syn.token.Paren
                    .default()
                    .surround(tokens, emit)
            } else {
                emit(tokens)
            }
        }

        override fun deepCopy(): Cast = Cast(attrs.mapTo(mutableListOf()) { it.deepCopy() }, expr.deepCopy(), asToken, ty.deepCopy())
    }

    /** A closure expression: `|a, b| a + b`. */
    public data class Closure(
        public var attrs: MutableList<Attribute>,
        public var constness: io.github.kotlinmania.syn.token.Const?,
        public var asyncness: io.github.kotlinmania.syn.token.Async?,
        public var capture: io.github.kotlinmania.syn.token.Move?,
        public var or1Token: io.github.kotlinmania.syn.token.Or,
        public var inputs: PatList,
        public var or2Token: io.github.kotlinmania.syn.token.Or,
        public var output: ReturnType,
        public var body: Expr,
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

        override fun deepCopy(): Closure = Closure(attrs.mapTo(mutableListOf()) { it.deepCopy() }, constness, asyncness, capture, or1Token, inputs.copy({ it.deepCopy() }, { it }), or2Token, output.deepCopy(), body.deepCopy())
    }

    /** A const block: `const { ... }`. */
    public data class Const(
        public var attrs: MutableList<Attribute>,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Const = Const(attrs.mapTo(mutableListOf()) { it.deepCopy() }, constToken, block)
    }

    /** A `continue`, with an optional label. */
    public data class Continue(
        public var attrs: MutableList<Attribute>,
        public var continueToken: io.github.kotlinmania.syn.token.Continue,
        public var label: Lifetime?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            continueToken.toTokens(tokens)
            label?.toTokens(tokens)
        }

        override fun deepCopy(): Continue = Continue(attrs.mapTo(mutableListOf()) { it.deepCopy() }, continueToken, label?.deepCopy())
    }

    /** Access of a named field of a data class (`obj.k`) or indexed element of a tuple-like compound (`obj.0`). */
    public data class Field(
        public var attrs: MutableList<Attribute>,
        public var base: Expr,
        public var dotToken: io.github.kotlinmania.syn.token.Dot,
        public var member: Member,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            base.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            dotToken.toTokens(tokens)
            member.toTokens(tokens)
        }

        override fun deepCopy(): Field = Field(attrs.mapTo(mutableListOf()) { it.deepCopy() }, base.deepCopy(), dotToken, member)
    }

    /** A for loop: `for pat in expr { ... }`. */
    public data class ForLoop(
        public var attrs: MutableList<Attribute>,
        public var label: Label?,
        public var forToken: io.github.kotlinmania.syn.token.For,
        public var pat: Pat,
        public var inToken: io.github.kotlinmania.syn.token.In,
        public var expr: Expr,
        public var body: Block,
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

        override fun deepCopy(): ForLoop = ForLoop(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), forToken, pat.deepCopy(), inToken, expr.deepCopy(), body)
    }

    /** An expression contained within invisible delimiters. */
    public data class Group(
        public var attrs: MutableList<Attribute>,
        public var groupToken: io.github.kotlinmania.syn.token.Group,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            groupToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Group = Group(attrs.mapTo(mutableListOf()) { it.deepCopy() }, groupToken, expr.deepCopy())
    }

    /** An `if` expression with an optional `else` block. */
    public data class If(
        public var attrs: MutableList<Attribute>,
        public var ifToken: io.github.kotlinmania.syn.token.If,
        public var cond: Expr,
        public var thenBranch: Block,
        public var elseBranch: ElseExpr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            ifToken.toTokens(tokens)
            cond.toTokensWithParens(tokens, Precedence.MIN, ExprPosition.Condition)
            thenBranch.toTokens(tokens)
            elseBranch?.toTokens(tokens)
        }

        override fun deepCopy(): If = If(attrs.mapTo(mutableListOf()) { it.deepCopy() }, ifToken, cond.deepCopy(), thenBranch, elseBranch?.let { it.copy(expr = it.expr.deepCopy()) })
    }

    /** A square bracketed indexing expression: `vector[2]`. */
    public data class Index(
        public var attrs: MutableList<Attribute>,
        public var expr: Expr,
        public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public var index: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            bracketToken.surround(tokens) { inner -> index.toTokens(inner) }
        }

        override fun deepCopy(): Index = Index(attrs.mapTo(mutableListOf()) { it.deepCopy() }, expr.deepCopy(), bracketToken, index.deepCopy())
    }

    /** The inferred value of a const generic argument, denoted `_`. */
    public data class Infer(
        public var attrs: MutableList<Attribute>,
        public var underscoreToken: io.github.kotlinmania.syn.token.Underscore,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            underscoreToken.toTokens(tokens)
        }

        override fun deepCopy(): Infer = Infer(attrs.mapTo(mutableListOf()) { it.deepCopy() }, underscoreToken)
    }

    /** A pattern guard that tests whether a pattern matches a value. */
    public data class Let(
        public var attrs: MutableList<Attribute>,
        public var letToken: io.github.kotlinmania.syn.token.Let,
        public var pat: Pat,
        public var eqToken: io.github.kotlinmania.syn.token.Eq,
        public var expr: Expr,
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

        override fun deepCopy(): Let = Let(attrs.mapTo(mutableListOf()) { it.deepCopy() }, letToken, pat.deepCopy(), eqToken, expr.deepCopy())
    }

    /** A literal in place of an expression: `1`, `"foo"`. */
    public data class Lit(
        var attrs: MutableList<Attribute>,
        var lit: io.github.kotlinmania.syn.Lit,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            lit.toTokens(tokens)
        }

        override fun deepCopy(): Lit = Lit(attrs.mapTo(mutableListOf()) { it.deepCopy() }, lit)
    }

    /** Conditionless loop: `loop { ... }`. */
    public data class Loop(
        public var attrs: MutableList<Attribute>,
        public var label: Label?,
        public var loopToken: io.github.kotlinmania.syn.token.Loop,
        public var body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            loopToken.toTokens(tokens)
            body.toTokens(tokens)
        }

        override fun deepCopy(): Loop = Loop(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), loopToken, body)
    }

    /** A macro invocation expression. */
    public data class Macro(
        var attrs: MutableList<Attribute>,
        var mac: io.github.kotlinmania.syn.Macro,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
        }

        override fun deepCopy(): Macro = Macro(attrs.mapTo(mutableListOf()) { it.deepCopy() }, mac.deepCopy())
    }

    /** A `match` expression. */
    public data class Match(
        public var attrs: MutableList<Attribute>,
        public var matchToken: io.github.kotlinmania.syn.token.Match,
        public var expr: Expr,
        public var braceToken: io.github.kotlinmania.syn.token.Brace,
        public var arms: MutableList<Arm>,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            matchToken.toTokens(tokens)
            expr.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (arm in arms) arm.toTokens(inner)
            }
        }

        override fun deepCopy(): Match = Match(attrs.mapTo(mutableListOf()) { it.deepCopy() }, matchToken, expr.deepCopy(), braceToken, arms.map { it.deepCopy() })
    }

    /** A method call expression with optional turbofish and arguments. */
    public data class MethodCall(
        public var attrs: MutableList<Attribute>,
        public var receiver: Expr,
        public var dotToken: io.github.kotlinmania.syn.token.Dot,
        public var method: Ident,
        public var turbofish: PathArguments.AngleBracketed?,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var args: ExprList,
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

        override fun deepCopy(): MethodCall = MethodCall(attrs.mapTo(mutableListOf()) { it.deepCopy() }, receiver.deepCopy(), dotToken, method.copy(), turbofish?.deepCopy() as? PathArguments.AngleBracketed?, parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A parenthesized expression: `(a + b)`. */
    public data class Paren(
        public var attrs: MutableList<Attribute>,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Paren = Paren(attrs.mapTo(mutableListOf()) { it.deepCopy() }, parenToken, expr.deepCopy())
    }

    /** A path expression possibly containing generic parameters. */
    public data class Path(
        var attrs: MutableList<Attribute>,
        var qself: QSelf?,
        var path: io.github.kotlinmania.syn.Path,
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

        override fun deepCopy(): Path = Path(attrs.mapTo(mutableListOf()) { it.deepCopy() }, qself, path.deepCopy())
    }

    /** A range expression: `1..2`, `1..`, `..2`, `1..=2`, `..=2`. */
    public data class Range(
        public var attrs: MutableList<Attribute>,
        public var start: Expr?,
        public var limits: RangeLimits,
        public var end: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            start?.toTokensAsRangeStart(tokens)
            limits.toTokens(tokens)
            end?.toTokensWithParens(tokens, Precedence.Range, ExprPosition.RightOperand)
        }

        override fun deepCopy(): Range = Range(attrs.mapTo(mutableListOf()) { it.deepCopy() }, start?.deepCopy(), limits, end?.deepCopy())
    }

    /** Address-of operation: `&raw const place` or `&raw mut place`. */
    public data class RawAddr(
        public var attrs: MutableList<Attribute>,
        public var andToken: io.github.kotlinmania.syn.token.And,
        public var raw: io.github.kotlinmania.syn.token.Raw,
        public var mutability: PointerMutability,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            andToken.toTokens(tokens)
            raw.toTokens(tokens)
            mutability.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
        }

        override fun deepCopy(): RawAddr = RawAddr(attrs.mapTo(mutableListOf()) { it.deepCopy() }, andToken, raw, mutability, expr.deepCopy())
    }

    /** A referencing operation. */
    public data class Reference(
        public var attrs: MutableList<Attribute>,
        public var andToken: io.github.kotlinmania.syn.token.And,
        public var mutability: io.github.kotlinmania.syn.token.Mut?,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            andToken.toTokens(tokens)
            mutability?.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
        }

        override fun deepCopy(): Reference = Reference(attrs.mapTo(mutableListOf()) { it.deepCopy() }, andToken, mutability, expr.deepCopy())
    }

    /** An array literal constructed from one repeated element: `[0u8; N]`. */
    public data class Repeat(
        public var attrs: MutableList<Attribute>,
        public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public var expr: Expr,
        public var semiToken: io.github.kotlinmania.syn.token.Semi,
        public var len: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            bracketToken.surround(tokens) { inner ->
                expr.toTokens(inner)
                semiToken.toTokens(inner)
                len.toTokens(inner)
            }
        }

        override fun deepCopy(): Repeat = Repeat(attrs.mapTo(mutableListOf()) { it.deepCopy() }, bracketToken, expr.deepCopy(), semiToken, len.deepCopy())
    }

    /** A `return`, with an optional value to be returned. */
    public data class Return(
        public var attrs: MutableList<Attribute>,
        public var returnToken: io.github.kotlinmania.syn.token.Return,
        public var expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            returnToken.toTokens(tokens)
            expr?.toTokens(tokens)
        }

        override fun deepCopy(): Return = Return(attrs.mapTo(mutableListOf()) { it.deepCopy() }, returnToken, expr?.deepCopy())
    }

    /** A data-object initialization expression. */
    public data class Struct(
        public var attrs: MutableList<Attribute>,
        public var qself: QSelf?,
        public var path: io.github.kotlinmania.syn.Path,
        public var braceToken: io.github.kotlinmania.syn.token.Brace,
        public var fields: FieldValueList,
        public var dot2Token: io.github.kotlinmania.syn.token.DotDot?,
        public var rest: Expr?,
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

        override fun deepCopy(): Struct = Struct(attrs.mapTo(mutableListOf()) { it.deepCopy() }, qself, path.deepCopy(), braceToken, fields.copy({ it.deepCopy() }, { it }), dot2Token, rest?.deepCopy())
    }

    /** A try-expression: `expr?`. */
    public data class Try(
        public var attrs: MutableList<Attribute>,
        public var expr: Expr,
        public var questionToken: io.github.kotlinmania.syn.token.Question,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Unambiguous, ExprPosition.PostfixBase)
            questionToken.toTokens(tokens)
        }

        override fun deepCopy(): Try = Try(attrs.mapTo(mutableListOf()) { it.deepCopy() }, expr.deepCopy(), questionToken)
    }

    /** A try block: `try { ... }`. */
    public data class TryBlock(
        public var attrs: MutableList<Attribute>,
        public var tryToken: io.github.kotlinmania.syn.token.Try,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            tryToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): TryBlock = TryBlock(attrs.mapTo(mutableListOf()) { it.deepCopy() }, tryToken, block)
    }

    /** A tuple expression: `(a, b, c, d)`. */
    public data class Tuple(
        public var attrs: MutableList<Attribute>,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
                if (elems.len() == 1 && !elems.trailingPunct()) {
                    io.github.kotlinmania.syn.token.Comma
                        .default()
                        .toTokens(inner)
                }
            }
        }

        override fun deepCopy(): Tuple = Tuple(attrs.mapTo(mutableListOf()) { it.deepCopy() }, parenToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** A unary prefix operation: negation or dereference. */
    public data class Unary(
        public var attrs: MutableList<Attribute>,
        public var op: UnOp,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            op.toTokens(tokens)
            expr.toTokensWithParens(tokens, Precedence.Prefix, ExprPosition.PrefixOperand)
        }

        override fun deepCopy(): Unary = Unary(attrs.mapTo(mutableListOf()) { it.deepCopy() }, op, expr.deepCopy())
    }

    /** A block expression that permits operations violating memory safety invariants. */
    public data class Unsafe(
        public var attrs: MutableList<Attribute>,
        public var unsafeToken: io.github.kotlinmania.syn.token.Unsafe,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            unsafeToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Unsafe = Unsafe(attrs.mapTo(mutableListOf()) { it.deepCopy() }, unsafeToken, block)
    }

    /** A while loop: `while expr { ... }`. */
    public data class While(
        public var attrs: MutableList<Attribute>,
        public var label: Label?,
        public var whileToken: io.github.kotlinmania.syn.token.While,
        public var cond: Expr,
        public var body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            whileToken.toTokens(tokens)
            cond.toTokensWithParens(tokens, Precedence.MIN, ExprPosition.Condition)
            body.toTokens(tokens)
        }

        override fun deepCopy(): While = While(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), whileToken, cond.deepCopy(), body)
    }

    /** A yield expression: `yield expr`. */
    public data class Yield(
        public var attrs: MutableList<Attribute>,
        public var yieldToken: io.github.kotlinmania.syn.token.Yield,
        public var expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            yieldToken.toTokens(tokens)
            expr?.toTokens(tokens)
        }

        override fun deepCopy(): Yield = Yield(attrs.mapTo(mutableListOf()) { it.deepCopy() }, yieldToken, expr?.deepCopy())
    }

    /** Tokens in expression position not interpreted by Syn. */
    public data class Verbatim(
        var tokens: TokenStream,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
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
        var ident: Ident,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
        }
    }

    public data class Unnamed(
        var index: Index,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            index.toTokens(tokens)
        }
    }
}

/** A tuple field index such as `0` in `obj.0`. */
public data class Index(
    public var index: UInt,
    public var span: Span,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        var literal =
            io.github.kotlinmania.procmacro2.Literal
                .i64Unsuffixed(index.toLong())
        literal.setSpan(span)
        tokens.append(literal)
    }
}

/** A field-value pair in a data-object initialization. */
public data class FieldValue(
    public var attrs: MutableList<Attribute>,
    public var member: Member,
    public var colonToken: io.github.kotlinmania.syn.token.Colon?,
    public var expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        member.toTokens(tokens)
        colonToken?.toTokens(tokens)
        expr.toTokens(tokens)
    }

    public fun deepCopy(): FieldValue = FieldValue(attrs.mapTo(mutableListOf()) { it.deepCopy() }, member, colonToken, expr.deepCopy())
}

/** A label on a `for`, `while`, or `loop`. */
public data class Label(
    public var name: Lifetime,
    public var colonToken: io.github.kotlinmania.syn.token.Colon,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        name.toTokens(tokens)
        colonToken.toTokens(tokens)
    }

    public fun deepCopy(): Label = Label(name.deepCopy(), colonToken)

    public fun clone(): Label = deepCopy()
}

/** One arm of a `match` expression. */
public data class Arm(
    public var attrs: MutableList<Attribute>,
    public var pat: Pat,
    public var guard: IfExpr?,
    public var fatArrowToken: io.github.kotlinmania.syn.token.FatArrow,
    public var body: Expr,
    public var comma: io.github.kotlinmania.syn.token.Comma?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        pat.toTokens(tokens)
        guard?.toTokens(tokens)
        fatArrowToken.toTokens(tokens)
        body.toTokens(tokens)
        comma?.toTokens(tokens)
    }

    public fun deepCopy(): Arm = Arm(attrs.mapTo(mutableListOf()) { it.deepCopy() }, pat.deepCopy(), guard?.let { it.copy(expr = it.expr.deepCopy()) }, fatArrowToken, body.deepCopy(), comma)
}

/** Limit types of a range, inclusive or exclusive. */
public sealed class RangeLimits : ToTokens {
    public data class HalfOpen(
        var token: io.github.kotlinmania.syn.token.DotDot,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    public data class Closed(
        var token: io.github.kotlinmania.syn.token.DotDotEq,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

/** Mutability of a raw pointer. */
public sealed class PointerMutability : ToTokens {
    public data class Const(
        var token: io.github.kotlinmania.syn.token.Const,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    public data class Mut(
        var token: io.github.kotlinmania.syn.token.Mut,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

public object ExprParse {
    fun parse(input: ParseStream): SynResult<Expr> = parseExpr(input)
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

public fun Expr.replaceAttrs(attrs: List<Attribute>): Expr =
    when (this) {
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

public fun Expr.isNamed(name: String): Boolean {
    if (this is Expr.Path) {
        var last = path.segments.last()
        return last?.ident?.toString() == name
    }
    return false
}

public fun Expr.span(): io.github.kotlinmania.procmacro2.Span = spanOf(this)

public fun printExpr(expr: Expr, tokens: TokenStream) {
    expr.toTokens(tokens)
}

public fun printSubexpression(expr: Expr, tokens: TokenStream) {
    expr.toTokens(tokens)
}

public fun printExprAssign(e: Expr.Assign, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprAwait(e: Expr.Await, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprBinary(e: Expr.Binary, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprBlock(e: Expr.BlockExpr, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprBreak(e: Expr.Break, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprCall(e: Expr.Call, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprCast(e: Expr.Cast, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprClosure(e: Expr.Closure, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprField(e: Expr.Field, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprIndex(e: Expr.Index, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprLet(e: Expr.Let, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprMethodCall(e: Expr.MethodCall, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprRange(e: Expr.Range, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprRawAddr(e: Expr.Reference, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprReference(e: Expr.Reference, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprReturn(e: Expr.Return, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprTry(e: Expr.Try, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprUnary(e: Expr.Unary, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprYield(e: Expr.Yield, tokens: TokenStream) {
    e.toTokens(tokens)
}

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

public fun peekExpr(input: ParseStream): Boolean =
    input.peek(IdentPeekAny) &&
        !input.peek(AsPeek) ||
        input.peek(ParenPeek) ||
        input.peek(BracketPeek) ||
        input.peek(BracePeek) ||
        input.peek(LitPeek) ||
        input.peek(NotPeek) &&
        !input.peek(NePeek) ||
        input.peek(MinusPeek) &&
        !input.peek(MinusEqPeek) &&
        !input.peek(RArrowPeek) ||
        input.peek(StarPeek) &&
        !input.peek(StarEqPeek) ||
        input.peek(OrPeek) &&
        !input.peek(OrEqPeek) ||
        input.peek(AndPeek) &&
        !input.peek(AndEqPeek) ||
        input.peek(DotDotPeek) ||
        input.peek(LtPeek) &&
        !input.peek(LePeek) &&
        !input.peek(ShlEqPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(LifetimePeek) ||
        input.peek(PoundPeek)

public fun memberFromIdent(ident: io.github.kotlinmania.procmacro2.Ident): Member =
    Member.Named(ident)

public fun memberFromIndex(index: Index): Member =
    Member.Unnamed(index)

public fun memberFromUSize(index: Int): Member =
    Member.Unnamed(indexFromUSize(index))

public fun indexFromUSize(index: Int): Index {
    require(index < 0xFFFFFFFF) { "index overflow" }
    return Index(
        index.toUInt(),
        io.github.kotlinmania.procmacro2.Span
            .callSite(),
    )
}

public fun atomLabeled(input: ParseStream): SynResult<Expr> {
    var labelResult = LifetimeParse.parse(input)
    if (labelResult.isFailure) return SynResult.failure((labelResult as SynResult.Failure).error)
    var theLabel = labelResult.getOrThrow()
    var colonResult = ColonParse.parse(input)
    if (colonResult.isFailure) return SynResult.failure((colonResult as SynResult.Failure).error)
    var theLabelColon = colonResult.getOrThrow()
    var label = Label(theLabel, theLabelColon)
    var expr: Expr =
        when {
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
    var begin = input.fork()
    var kwResult = keyword(input, "builtin")
    if (kwResult.isFailure) return SynResult.failure((kwResult as SynResult.Failure).error)
    var poundResult = PoundParse.parse(input)
    if (poundResult.isFailure) return SynResult.failure((poundResult as SynResult.Failure).error)
    var identResult = IdentParse.parse(input)
    if (identResult.isFailure) return SynResult.failure((identResult as SynResult.Failure).error)
    var parens = parenthesized(input)
    if (parens.isFailure) return SynResult.failure((parens as SynResult.Failure).error)
    var parensVal = parens.getOrThrow()
    parensVal.content.finishChildBuffer()
    var tokens = verbatimBetween(begin, input)
    return SynResult.success(Expr.Verbatim(tokens))
}

public fun restOfPathOrMacroOrStruct(
    qself: QSelf?,
    path: Path,
    input: ParseStream,
    allowStruct: Boolean,
): SynResult<Expr> {
    if (qself == null && input.peek(NotPeek) && !input.peek(NePeek) && path.isModStyle()) {
        var bangResult = NotParse.parse(input)
        if (bangResult.isFailure) return SynResult.failure((bangResult as SynResult.Failure).error)
        var delimResult = parseDelimiter(input)
        if (delimResult.isFailure) return SynResult.failure((delimResult as SynResult.Failure).error)
        var (delimiter, tokens) = delimResult.getOrThrow()
        return SynResult.success(
            Expr.Macro(
                emptyList(),
                Macro(path, bangResult.getOrThrow(), delimiter, tokens),
            ),
        )
    }
    if (allowStruct && input.peek(BracePeek)) {
        var structResult = exprStructHelper(input, qself, path)
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
    var braces = braced(input)
    if (braces.isFailure) return SynResult.failure((braces as SynResult.Failure).error)
    var bracesVal = braces.getOrThrow()
    var content = bracesVal.content
    var fields = FieldValueList()
    while (!content.isEmpty()) {
        if (content.peek(DotDotPeek)) {
            val dot2Result = DotDotParse.parse(content)
            if (dot2Result.isFailure) return SynResult.failure((dot2Result as SynResult.Failure).error)
            val rest: Expr? =
                if (content.isEmpty()) {
                    null
                } else {
                    val restResult = parseExprFull(content)
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
        var fieldResult = parseFieldValueImpl(content)
        if (fieldResult.isFailure) return SynResult.failure((fieldResult as SynResult.Failure).error)
        fields.pushValue(fieldResult.getOrThrow())
        if (content.isEmpty()) break
        var punctResult = CommaParse.parse(content)
        if (punctResult.isFailure) break
        fields.pushPunct(punctResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(
        Expr.Struct(emptyList(), qself, path, bracesVal.token, fields, null, null),
    )
}

public fun exprLet(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Let> {
    var letResult = LetParse.parse(input)
    if (letResult.isFailure) return SynResult.failure((letResult as SynResult.Failure).error)
    var patResult = parsePatMultiWithLeadingVert(input)
    if (patResult.isFailure) return SynResult.failure((patResult as SynResult.Failure).error)
    var eqResult = EqParse.parse(input)
    if (eqResult.isFailure) return SynResult.failure((eqResult as SynResult.Failure).error)
    var lhsResult = unaryExprImpl(input, allowStruct)
    if (lhsResult.isFailure) return SynResult.failure((lhsResult as SynResult.Failure).error)
    var exprResult = parseExprBinaryImpl(input, lhsResult.getOrThrow(), allowStruct, Precedence.Compare)
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
    var opResult = UnOpParse.parse(input)
    if (opResult.isFailure) return SynResult.failure((opResult as SynResult.Failure).error)
    var innerResult = unaryExprImpl(input, allowStruct)
    if (innerResult.isFailure) return SynResult.failure((innerResult as SynResult.Failure).error)
    return SynResult.success(Expr.Unary(attrs, opResult.getOrThrow(), innerResult.getOrThrow()))
}

public fun exprBecome(input: ParseStream): SynResult<Expr> {
    var begin = input.fork()
    var becomeResult = BecomeParse.parse(input)
    if (becomeResult.isFailure) return SynResult.failure((becomeResult as SynResult.Failure).error)
    var exprResult = parseExprFull(input)
    if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
    var tokens = verbatimBetween(begin, input)
    return SynResult.success(Expr.Verbatim(tokens))
}

public fun exprClosure(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Closure> {
    var lifetimes: BoundLifetimes? = null
    var constnessResult = ConstParse.parse(input)
    var constness = if (constnessResult.isSuccess) constnessResult.getOrThrow() else null
    var movabilityResult = StaticParse.parse(input)
    var movability = if (movabilityResult.isSuccess) movabilityResult.getOrThrow() else null
    var asyncnessResult = AsyncParse.parse(input)
    var asyncness = if (asyncnessResult.isSuccess) asyncnessResult.getOrThrow() else null
    var captureResult = MoveParse.parse(input)
    var capture = if (captureResult.isSuccess) captureResult.getOrThrow() else null
    var or1Result = OrParse.parse(input)
    if (or1Result.isFailure) return SynResult.failure((or1Result as SynResult.Failure).error)
    var inputs = PatList()
    while (true) {
        if (input.peek(OrPeek)) break
        var valueResult = closureArg(input)
        if (valueResult.isFailure) return SynResult.failure((valueResult as SynResult.Failure).error)
        inputs.pushValue(valueResult.getOrThrow())
        if (input.peek(OrPeek)) break
        var punctResult = CommaParse.parse(input)
        if (punctResult.isFailure) return SynResult.failure((punctResult as SynResult.Failure).error)
        inputs.pushPunct(punctResult.getOrThrow())
    }
    var or2Result = OrParse.parse(input)
    if (or2Result.isFailure) return SynResult.failure((or2Result as SynResult.Failure).error)
    var output: ReturnType
    var body: Expr
    if (input.peek(RArrowPeek)) {
        var arrowResult = RArrowParse.parse(input)
        if (arrowResult.isFailure) return SynResult.failure((arrowResult as SynResult.Failure).error)
        var tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return SynResult.failure((tyResult as SynResult.Failure).error)
        var blockResult = parseExprBlock(input)
        if (blockResult.isFailure) return SynResult.failure((blockResult as SynResult.Failure).error)
        output = ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow())
        body = blockResult.getOrThrow()
    } else {
        var bodyResult = ambiguousExprImpl(input, allowStruct)
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
    var patResult = parsePatSingle(input)
    if (patResult.isFailure) return patResult
    var pat = patResult.getOrThrow()
    if (input.peek(ColonPeek)) {
        var colonResult = ColonParse.parse(input)
        if (colonResult.isFailure) return SynResult.failure((colonResult as SynResult.Failure).error)
        var tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return SynResult.failure((tyResult as SynResult.Failure).error)
        return SynResult.success(Pat.TypeAscription(emptyList(), pat, colonResult.getOrThrow(), tyResult.getOrThrow()))
    }
    return SynResult.success(pat)
}

public fun exprBreak(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Break> {
    var breakResult = BreakParse.parse(input)
    if (breakResult.isFailure) return SynResult.failure((breakResult as SynResult.Failure).error)
    var ahead = input.fork()
    var labelResult = LifetimeParse.parse(ahead)
    var label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
    if (label != null && ahead.peek(ColonPeek)) {
        var exprResult = parseExprFull(input)
        if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
        return SynResult.failure(SynError.new2(label.apostrophe, input.span(), "parentheses required"))
    }
    input.advanceTo(ahead)
    var expr: Expr? =
        if (peekExpr(input) && (allowStruct || !input.peek(BracePeek))) {
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
    var limitsResult = RangeLimitsParse.parse(input)
    if (limitsResult.isFailure) return SynResult.failure((limitsResult as SynResult.Failure).error)
    var limits = limitsResult.getOrThrow()
    var endResult = parseRangeEnd(input, limits, allowStruct)
    if (endResult.isFailure) return SynResult.failure((endResult as SynResult.Failure).error)
    return SynResult.success(
        Expr.Range(emptyList(), null, limits, endResult.getOrThrow()),
    )
}

public object RangeLimitsParse {
    fun parse(input: ParseStream): SynResult<RangeLimits> {
        if (input.peek(DotDotEqPeek)) {
            val result = DotDotEqParse.parse(input)
            if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
            return SynResult.success(RangeLimits.Closed(result.getOrThrow()))
        }
        if (input.peek(DotDotPeek) && !input.peek(DotDotDotPeek)) {
            val result = DotDotParse.parse(input)
            if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
            return SynResult.success(RangeLimits.HalfOpen(result.getOrThrow()))
        }
        return SynResult.failure(input.error("expected .. or ..="))
    }
}

public object ArmParse {
    fun parse(input: ParseStream): SynResult<Arm> {
        var attrs = mutableListOf<Attribute>()
        var patResult = parsePatMultiWithLeadingVert(input)
        if (patResult.isFailure) return SynResult.failure((patResult as SynResult.Failure).error)
        var guard: IfExpr? =
            if (input.peek(IfPeek)) {
                val ifToken = IfParse.parse(input).getOrThrow()
                val guardExpr = parseExprFull(input)
                if (guardExpr.isFailure) return SynResult.failure((guardExpr as SynResult.Failure).error)
                IfExpr(ifToken, guardExpr.getOrThrow())
            } else {
                null
            }
        var fatArrowResult = FatArrowParse.parse(input)
        if (fatArrowResult.isFailure) return SynResult.failure((fatArrowResult as SynResult.Failure).error)
        var bodyResult = parseExprWithEarlierBoundaryRuleImpl(input)
        if (bodyResult.isFailure) return SynResult.failure((bodyResult as SynResult.Failure).error)
        var commaResult = CommaParse.parse(input)
        var comma = if (commaResult.isSuccess) commaResult.getOrThrow() else null
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
    var isHalfOpen = limits is RangeLimits.HalfOpen
    var stop =
        isHalfOpen &&
            (
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
    var endResult = parseBinopRhsImpl(input, allowStruct, Precedence.Range)
    if (endResult.isFailure) return SynResult.failure((endResult as SynResult.Failure).error)
    return SynResult.success(endResult.getOrThrow())
}

public fun parseObsoleteRangeLimits(input: ParseStream): SynResult<RangeLimits> {
    var dotDot = input.peek(DotDotPeek)
    var dotDotEq = dotDot && input.peek(DotDotEqPeek)
    var dotDotDot = dotDot && input.peek(DotDotDotPeek)
    if (dotDotEq) {
        var result = DotDotEqParse.parse(input)
        if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
        return SynResult.success(RangeLimits.Closed(result.getOrThrow()))
    }
    if (dotDot) {
        var result = DotDotParse.parse(input)
        if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
        return SynResult.success(RangeLimits.HalfOpen(result.getOrThrow()))
    }
    return SynResult.failure(input.error("expected .. or ..="))
}

public fun parseMultipleArms(input: ParseStream): SynResult<List<Arm>> {
    var arms = mutableListOf<Arm>()
    while (!input.isEmpty()) {
        var armResult = ArmParse.parse(input)
        if (armResult.isFailure) return SynResult.failure((armResult as SynResult.Failure).error)
        arms.add(armResult.getOrThrow())
    }
    return SynResult.success(arms)
}

public fun multiIndex(e: Expr, dotToken: io.github.kotlinmania.syn.token.Dot, float: LitFloat): SynResult<MultiIndexResult> {
    var floatToken = float.token()
    var floatSpan = floatToken.span()
    var floatRepr = floatToken.toString()
    var trailingDot = floatRepr.endsWith('.')
    if (trailingDot) {
        floatRepr = floatRepr.dropLast(1)
    }
    var offset = 0
    var currentExpr = e
    var currentDot = dotToken
    for (part in floatRepr.split('.')) {
        var index: Index = Index(part.toUInt(), floatSpan)
        var partEnd = offset + part.length
        var base = currentExpr
        currentExpr =
            Expr.Field(
                emptyList(),
                base,
                currentDot,
                Member.Unnamed(index),
            )
        currentDot =
            io.github.kotlinmania.syn.token.Dot
                .from(floatSpan)
        offset = partEnd + 1
    }
    return SynResult.success(MultiIndexResult(currentExpr, !trailingDot))
}

@JvmInline
internal value class AllowStruct(
    var value: Boolean,
)

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
