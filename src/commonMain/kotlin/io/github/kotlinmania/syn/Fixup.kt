// port-lint: source fixup.rs
package io.github.kotlinmania.syn

/**
 * Context needed to print nested expressions with only the parentheses required
 * to make the output parse back to the same tree.
 */
public data class FixupContext(
    var previousOperator: Precedence,
    var nextOperator: Precedence,
    var stmt: Boolean,
    var leftmostSubexpressionInStmt: Boolean,
    var matchArm: Boolean,
    var leftmostSubexpressionInMatchArm: Boolean,
    var condition: Boolean,
    var rightmostSubexpressionInCondition: Boolean,
    var leftmostSubexpressionInOptionalOperand: Boolean,
    var nextOperatorCanBeginExpr: Boolean,
    var nextOperatorCanContinueExpr: Boolean,
    var nextOperatorCanBeginGenerics: Boolean,
) {
    public companion object {
        public var NONE: FixupContext =
            FixupContext(
                previousOperator = Precedence.MIN,
                nextOperator = Precedence.MIN,
                stmt = false,
                leftmostSubexpressionInStmt = false,
                matchArm = false,
                leftmostSubexpressionInMatchArm = false,
                condition = false,
                rightmostSubexpressionInCondition = false,
                leftmostSubexpressionInOptionalOperand = false,
                nextOperatorCanBeginExpr = false,
                nextOperatorCanContinueExpr = false,
                nextOperatorCanBeginGenerics = false,
            )

        public fun newStmt(): FixupContext =
            NONE.copy(stmt = true)

        public fun newMatchArm(): FixupContext =
            NONE.copy(matchArm = true)

        public fun newCondition(): FixupContext =
            NONE.copy(condition = true, rightmostSubexpressionInCondition = true)
    }

    public fun leftmostSubexpressionWithOperator(
        expr: Expr,
        nextOperatorCanBeginExpr: Boolean,
        nextOperatorCanBeginGenerics: Boolean,
        precedence: Precedence,
    ): Pair<Precedence, FixupContext> {
        var fixup =
            copy(
                nextOperator = precedence,
                stmt = false,
                leftmostSubexpressionInStmt = stmt || leftmostSubexpressionInStmt,
                matchArm = false,
                leftmostSubexpressionInMatchArm = matchArm || leftmostSubexpressionInMatchArm,
                rightmostSubexpressionInCondition = false,
                nextOperatorCanBeginExpr = nextOperatorCanBeginExpr,
                nextOperatorCanContinueExpr = true,
                nextOperatorCanBeginGenerics = nextOperatorCanBeginGenerics,
            )
        return fixup.leftmostSubexpressionPrecedence(expr) to fixup
    }

    public fun leftmostSubexpressionWithDot(expr: Expr): Pair<Precedence, FixupContext> {
        var fixup =
            copy(
                nextOperator = Precedence.Unambiguous,
                stmt = stmt || leftmostSubexpressionInStmt,
                leftmostSubexpressionInStmt = false,
                matchArm = matchArm || leftmostSubexpressionInMatchArm,
                leftmostSubexpressionInMatchArm = false,
                rightmostSubexpressionInCondition = false,
                nextOperatorCanBeginExpr = false,
                nextOperatorCanContinueExpr = true,
                nextOperatorCanBeginGenerics = false,
            )
        return fixup.leftmostSubexpressionPrecedence(expr) to fixup
    }

    public fun leftmostSubexpressionPrecedence(expr: Expr): Precedence {
        if (!nextOperatorCanBeginExpr || nextOperator == Precedence.Range) {
            if (scanRight(expr, this, Precedence.MIN, failOffset = 0, bailoutOffset = 0) == Scan.Bailout) {
                if (scanLeft(expr, this)) {
                    return Precedence.Unambiguous
                }
            }
        }
        return precedence(expr)
    }

    public fun rightmostSubexpression(
        expr: Expr,
        precedence: Precedence,
    ): Pair<Precedence, FixupContext> {
        var fixup = rightmostSubexpressionFixup(resetAllowStruct = false, optionalOperand = false, precedence)
        return fixup.rightmostSubexpressionPrecedence(expr) to fixup
    }

    public fun rightmostSubexpressionFixup(
        resetAllowStruct: Boolean,
        optionalOperand: Boolean,
        precedence: Precedence,
    ): FixupContext =
        copy(
            previousOperator = precedence,
            stmt = false,
            leftmostSubexpressionInStmt = false,
            matchArm = false,
            leftmostSubexpressionInMatchArm = false,
            condition = condition && !resetAllowStruct,
            leftmostSubexpressionInOptionalOperand = condition && optionalOperand,
        )

    public fun rightmostSubexpressionPrecedence(expr: Expr): Precedence {
        var defaultPrec = precedence(expr)
        var needsScan =
            when (previousOperator) {
                Precedence.Assign, Precedence.Let, Precedence.Prefix -> defaultPrec < previousOperator
                else -> defaultPrec <= previousOperator
            } &&
                when (nextOperator) {
                    Precedence.Range, Precedence.Or, Precedence.And -> true
                    else -> !nextOperatorCanBeginExpr
                }
        if (needsScan) {
            val scan = scanRight(expr, this, previousOperator, failOffset = 1, bailoutOffset = 0)
            if ((scan == Scan.Bailout || scan == Scan.Fail) && scanLeft(expr, this)) {
                return Precedence.Prefix
            }
        }
        return defaultPrec
    }

    public fun parenthesize(expr: Expr): Boolean =
        (leftmostSubexpressionInStmt && !Classify.requiresSemiToBeStmt(expr)) ||
            ((stmt || leftmostSubexpressionInStmt) && expr is Expr.Let) ||
            (leftmostSubexpressionInMatchArm && !Classify.requiresCommaToBeMatchArm(expr)) ||
            (condition && expr is Expr.Struct) ||
            (
                rightmostSubexpressionInCondition &&
                    (expr is Expr.Return && expr.expr == null || expr is Expr.Yield && expr.expr == null)
            ) ||
            (
                rightmostSubexpressionInCondition &&
                    !condition &&
                    (
                        expr is Expr.Break &&
                            expr.expr == null ||
                            expr is Expr.Path ||
                            expr is Expr.Range &&
                            expr.end == null
                    )
            ) ||
            (
                leftmostSubexpressionInOptionalOperand &&
                    expr is Expr.BlockExpr &&
                    expr.attrs.isEmpty() &&
                    expr.label == null
            )

    public fun precedence(expr: Expr): Precedence {
        if (nextOperatorCanBeginExpr) {
            if (expr is Expr.Break &&
                expr.expr == null ||
                expr is Expr.Return &&
                expr.expr == null ||
                expr is Expr.Yield &&
                expr.expr == null
            ) {
                return Precedence.Jump
            }
        }

        if (!nextOperatorCanContinueExpr) {
            when (expr) {
                is Expr.Break,
                is Expr.Closure,
                is Expr.Let,
                is Expr.Return,
                is Expr.Yield,
                -> return Precedence.Prefix
                is Expr.Range -> if (expr.start == null) return Precedence.Prefix
                else -> {}
            }
        }

        if (nextOperatorCanBeginGenerics && expr is Expr.Cast) {
            if (Classify.trailingUnparameterizedPath(expr.ty)) {
                return Precedence.MIN
            }
        }

        return Precedence.of(expr)
    }

    public fun clone(): FixupContext = copy()
}

private enum class Scan {
    Fail,
    Bailout,
    Consume,
    ;

    fun eq(other: Scan): Boolean = this == other
}

private fun scanLeft(expr: Expr, fixup: FixupContext): Boolean =
    when (expr) {
        is Expr.Assign -> fixup.previousOperator <= Precedence.Assign
        is Expr.Binary -> {
            val binopPrec = Precedence.ofBinop(expr.op)
            if (binopPrec == Precedence.Assign) {
                fixup.previousOperator <= Precedence.Assign
            } else {
                fixup.previousOperator < binopPrec
            }
        }
        is Expr.Cast -> fixup.previousOperator < Precedence.Cast
        is Expr.Range -> expr.start == null || fixup.previousOperator < Precedence.Assign
        else -> true
    }

private fun scanRight(
    expr: Expr,
    fixup: FixupContext,
    precedence: Precedence,
    failOffset: Int,
    bailoutOffset: Int,
): Scan {
    var consumeByPrecedence =
        if ((
                when (precedence) {
                    Precedence.Assign, Precedence.Compare -> precedence <= fixup.nextOperator
                    else -> precedence < fixup.nextOperator
                }
            ) ||
            fixup.nextOperator == Precedence.MIN
        ) {
            Scan.Consume
        } else {
            Scan.Bailout
        }

    if (fixup.parenthesize(expr)) {
        return consumeByPrecedence
    }

    return when (expr) {
        is Expr.Assign ->
            if (expr.attrs.isEmpty()) {
                scanRightAssign(expr, fixup, consumeByPrecedence, failOffset, bailoutOffset)
            } else {
                scanRightLeaf(fixup, precedence, consumeByPrecedence)
            }
        is Expr.Binary ->
            if (expr.attrs.isEmpty()) {
                scanRightBinary(expr, fixup, consumeByPrecedence, failOffset, bailoutOffset)
            } else {
                scanRightLeaf(fixup, precedence, consumeByPrecedence)
            }
        is Expr.RawAddr -> scanRightPrefix(expr.expr, fixup, precedence, consumeByPrecedence, failOffset, bailoutOffset)
        is Expr.Reference -> scanRightPrefix(expr.expr, fixup, precedence, consumeByPrecedence, failOffset, bailoutOffset)
        is Expr.Unary -> scanRightPrefix(expr.expr, fixup, precedence, consumeByPrecedence, failOffset, bailoutOffset)
        is Expr.Range ->
            if (expr.attrs.isEmpty()) {
                scanRightRange(expr, fixup, failOffset)
            } else {
                scanRightLeaf(fixup, precedence, consumeByPrecedence)
            }
        is Expr.Break -> scanRightBreak(expr, fixup, precedence, bailoutOffset)
        is Expr.Return -> scanRightJump(expr.expr, fixup, precedence, bailoutOffset)
        is Expr.Yield -> scanRightJump(expr.expr, fixup, precedence, bailoutOffset)
        is Expr.Closure -> scanRightClosure(expr, fixup, bailoutOffset)
        is Expr.Let -> scanRightLet(expr, fixup, bailoutOffset)
        else -> scanRightLeaf(fixup, precedence, consumeByPrecedence)
    }
}

private fun scanRightAssign(
    expr: Expr.Assign,
    fixup: FixupContext,
    consumeByPrecedence: Scan,
    failOffset: Int,
    bailoutOffset: Int,
): Scan {
    if (when (fixup.nextOperator) {
            Precedence.Unambiguous -> failOffset >= 2
            else -> bailoutOffset >= 1
        }
    ) {
        return Scan.Consume
    }
    var rightFixup = fixup.rightmostSubexpressionFixup(false, false, Precedence.Assign)
    var scan =
        scanRight(
            expr.right,
            rightFixup,
            Precedence.Assign,
            failOffset = if (fixup.nextOperator == Precedence.Unambiguous) failOffset else 1,
            bailoutOffset = 1,
        )
    return if (scan == Scan.Bailout || scan == Scan.Consume) {
        Scan.Consume
    } else if (fixup.nextOperator == Precedence.Unambiguous) {
        Scan.Fail
    } else {
        Scan.Bailout
    }
}

private fun scanRightBinary(
    expr: Expr.Binary,
    fixup: FixupContext,
    consumeByPrecedence: Scan,
    failOffset: Int,
    bailoutOffset: Int,
): Scan {
    if (when (fixup.nextOperator) {
            Precedence.Unambiguous -> failOffset >= 2 && (consumeByPrecedence == Scan.Consume || bailoutOffset >= 1)
            else -> bailoutOffset >= 1
        }
    ) {
        return Scan.Consume
    }
    var binopPrec = Precedence.ofBinop(expr.op)
    if (binopPrec == Precedence.Compare && fixup.nextOperator == Precedence.Compare) {
        return Scan.Consume
    }
    var rightFixup = fixup.rightmostSubexpressionFixup(false, false, binopPrec)
    var scan =
        scanRight(
            expr.right,
            rightFixup,
            binopPrec,
            failOffset = if (fixup.nextOperator == Precedence.Unambiguous) failOffset else 1,
            bailoutOffset = consumeByPrecedence.ordinal - Scan.Bailout.ordinal,
        )
    when (scan) {
        Scan.Fail -> {}
        Scan.Bailout -> return consumeByPrecedence
        Scan.Consume -> return Scan.Consume
    }
    var rightNeedsGroup =
        binopPrec != Precedence.Assign &&
            rightFixup.rightmostSubexpressionPrecedence(expr.right) <= binopPrec
    return if (rightNeedsGroup) {
        consumeByPrecedence
    } else if (scan == Scan.Fail && fixup.nextOperator == Precedence.Unambiguous) {
        Scan.Fail
    } else {
        Scan.Bailout
    }
}

private fun scanRightPrefix(
    expr: Expr,
    fixup: FixupContext,
    precedence: Precedence,
    consumeByPrecedence: Scan,
    failOffset: Int,
    bailoutOffset: Int,
): Scan {
    if (when (fixup.nextOperator) {
            Precedence.Unambiguous -> failOffset >= 2 && (consumeByPrecedence == Scan.Consume || bailoutOffset >= 1)
            else -> bailoutOffset >= 1
        }
    ) {
        return Scan.Consume
    }
    var rightFixup = fixup.rightmostSubexpressionFixup(false, false, Precedence.Prefix)
    var scan =
        scanRight(
            expr,
            rightFixup,
            precedence,
            failOffset = if (fixup.nextOperator == Precedence.Unambiguous) failOffset else 1,
            bailoutOffset = consumeByPrecedence.ordinal - Scan.Bailout.ordinal,
        )
    when (scan) {
        Scan.Fail -> {}
        Scan.Bailout -> return consumeByPrecedence
        Scan.Consume -> return Scan.Consume
    }
    return if (rightFixup.rightmostSubexpressionPrecedence(expr) < Precedence.Prefix) {
        consumeByPrecedence
    } else if (scan == Scan.Fail && fixup.nextOperator == Precedence.Unambiguous) {
        Scan.Fail
    } else {
        Scan.Bailout
    }
}

private fun scanRightRange(
    expr: Expr.Range,
    fixup: FixupContext,
    failOffset: Int,
): Scan =
    when (val end = expr.end) {
        null ->
            if (fixup.nextOperatorCanBeginExpr) {
                Scan.Consume
            } else {
                Scan.Fail
            }
        else -> {
            if (failOffset >= 2) {
                return Scan.Consume
            }
            val rightFixup = fixup.rightmostSubexpressionFixup(false, true, Precedence.Range)
            val scan =
                scanRight(
                    end,
                    rightFixup,
                    Precedence.Range,
                    failOffset,
                    bailoutOffset =
                        when (fixup.nextOperator) {
                            Precedence.Assign, Precedence.Range -> 0
                            else -> 1
                        },
                )
            val consumes =
                when (scan) {
                    Scan.Fail -> false
                    Scan.Bailout -> fixup.nextOperator != Precedence.Assign && fixup.nextOperator != Precedence.Range
                    Scan.Consume -> true
                }
            if (consumes) {
                Scan.Consume
            } else if (rightFixup.rightmostSubexpressionPrecedence(end) <= Precedence.Range) {
                Scan.Consume
            } else {
                Scan.Fail
            }
        }
    }

private fun scanRightBreak(
    expr: Expr.Break,
    fixup: FixupContext,
    precedence: Precedence,
    bailoutOffset: Int,
): Scan =
    when (val value = expr.expr) {
        null ->
            if (fixup.nextOperator == Precedence.Assign && precedence > Precedence.Assign) {
                Scan.Fail
            } else {
                Scan.Consume
            }
        else -> {
            if (bailoutOffset >= 1 || expr.label == null && Classify.exprLeadingLabel(value)) {
                return Scan.Consume
            }
            val rightFixup = fixup.rightmostSubexpressionFixup(true, true, Precedence.Jump)
            when (scanRight(value, rightFixup, Precedence.Jump, failOffset = 1, bailoutOffset = 1)) {
                Scan.Fail -> Scan.Bailout
                Scan.Bailout, Scan.Consume -> Scan.Consume
            }
        }
    }

private fun scanRightJump(
    expr: Expr?,
    fixup: FixupContext,
    precedence: Precedence,
    bailoutOffset: Int,
): Scan =
    when (expr) {
        null ->
            if (fixup.nextOperator == Precedence.Assign && precedence > Precedence.Assign) {
                Scan.Fail
            } else {
                Scan.Consume
            }
        else -> {
            if (bailoutOffset >= 1) {
                return Scan.Consume
            }
            val rightFixup = fixup.rightmostSubexpressionFixup(true, false, Precedence.Jump)
            when (scanRight(expr, rightFixup, Precedence.Jump, failOffset = 1, bailoutOffset = 1)) {
                Scan.Fail -> Scan.Bailout
                Scan.Bailout, Scan.Consume -> Scan.Consume
            }
        }
    }

private fun scanRightClosure(
    expr: Expr.Closure,
    fixup: FixupContext,
    bailoutOffset: Int,
): Scan {
    val body = expr.body
    return if (expr.output == ReturnType.Default ||
        body is Expr.BlockExpr &&
        body.attrs.isEmpty() &&
        body.label == null
    ) {
        if (bailoutOffset >= 1) {
            Scan.Consume
        } else {
            val rightFixup = fixup.rightmostSubexpressionFixup(false, false, Precedence.Jump)
            when (scanRight(expr.body, rightFixup, Precedence.Jump, failOffset = 1, bailoutOffset = 1)) {
                Scan.Fail -> Scan.Bailout
                Scan.Bailout, Scan.Consume -> Scan.Consume
            }
        }
    } else {
        Scan.Consume
    }
}

private fun scanRightLet(
    expr: Expr.Let,
    fixup: FixupContext,
    bailoutOffset: Int,
): Scan {
    if (bailoutOffset >= 1) {
        return Scan.Consume
    }
    var rightFixup = fixup.rightmostSubexpressionFixup(false, false, Precedence.Let)
    var scan =
        scanRight(
            expr.expr,
            rightFixup,
            Precedence.Let,
            failOffset = 1,
            bailoutOffset = if (fixup.nextOperator < Precedence.Let) 0 else 1,
        )
    when (scan) {
        Scan.Fail, Scan.Bailout ->
            if (fixup.nextOperator < Precedence.Let) {
                return Scan.Bailout
            }
        Scan.Consume -> return Scan.Consume
    }
    return if (rightFixup.rightmostSubexpressionPrecedence(expr.expr) < Precedence.Let) {
        Scan.Consume
    } else if (scan == Scan.Fail) {
        Scan.Bailout
    } else {
        Scan.Consume
    }
}

private fun scanRightLeaf(
    fixup: FixupContext,
    precedence: Precedence,
    consumeByPrecedence: Scan,
): Scan =
    when {
        (fixup.nextOperator == Precedence.Assign || fixup.nextOperator == Precedence.Range) &&
            precedence == Precedence.Range -> Scan.Fail
        precedence == Precedence.Let && fixup.nextOperator < Precedence.Let -> Scan.Fail
        else -> consumeByPrecedence
    }
