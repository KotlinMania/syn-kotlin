// port-lint: source scan_expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span

private fun <T, R> SynResult<T>.asFailure(): SynResult<R> =
    SynResult.failure((this as SynResult.Failure).error)

internal fun scanExpr(input: ParseStream): SynResult<Unit> {
    if (input.isEmpty()) {
        return SynResult.failure(SynError.new(input.span(), "unexpected end of input"))
    }
    val result = input.call { parseExprFull(it) }
    if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
    return SynResult.success(Unit)
}

internal fun parseExprFull(input: ParseStream): SynResult<Expr> =
    ambiguousExprImpl(input, allowStruct = true)

internal fun parseExprWithEarlierBoundaryRuleImpl(input: ParseStream): SynResult<Expr> {
    var expr =
        when {
            input.peek(IfPeek) -> parseExprIf(input).getOrElse { return SynResult.failure(it) }
            input.peek(WhilePeek) -> parseExprWhile(input).getOrElse { return SynResult.failure(it) }
            input.peek(LoopPeek) -> parseExprLoop(input).getOrElse { return SynResult.failure(it) }
            input.peek(MatchPeek) -> parseExprMatch(input).getOrElse { return SynResult.failure(it) }
            input.peek(UnsafePeek) -> parseExprUnsafe(input).getOrElse { return SynResult.failure(it) }
            input.peek(ConstPeek) && input.peek2(BracePeek) -> parseExprConst(input).getOrElse { return SynResult.failure(it) }
            input.peek(BracePeek) -> parseExprBlock(input).getOrElse { return SynResult.failure(it) }
            input.peek(LifetimePeek) -> parseLabeledLoopOrWhile(input).getOrElse { return SynResult.failure(it) }
            else -> unaryExprImpl(input, allowStruct = true).getOrElse { return SynResult.failure(it) }
        }

    if (continueParsingEarlyImpl(expr)) {
        return parseExprBinaryImpl(input, expr, allowStruct = true, base = Precedence.MIN)
    }

    if ((input.peek(DotPeek) && !input.peek(DotDotPeek)) || input.peek(QuestionPeek)) {
        val trailed = trailerHelperImpl(input, expr, allowStruct = true)
        if (trailed.isFailure) return SynResult.failure((trailed as SynResult.Failure).error)
        expr = trailed.getOrThrow()
        return parseExprBinaryImpl(input, expr, allowStruct = true, base = Precedence.MIN)
    }
    return SynResult.success(expr)
}

internal fun continueParsingEarlyImpl(expr: Expr): Boolean =
    when (expr) {
        is Expr.If,
        is Expr.While,
        is Expr.Loop,
        is Expr.Match,
        is Expr.Unsafe,
        is Expr.Const,
        is Expr.BlockExpr -> false
        else -> true
    }

internal fun ambiguousExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val lhs = unaryExprImpl(input, allowStruct)
    if (lhs.isFailure) return lhs
    return parseExprBinaryImpl(input, lhs.getOrThrow(), allowStruct, Precedence.MIN)
}

internal fun parseExprBinaryImpl(
    input: ParseStream,
    lhs: Expr,
    allowStruct: Boolean,
    base: Precedence,
): SynResult<Expr> {
    var current = lhs
    while (true) {
        if (current is Expr.Range) break
        val ahead = input.fork()
        val opResult = ahead.parse(BinOpParse)
        if (opResult.isSuccess) {
            val op = opResult.getOrThrow()
            val precedence = Precedence.ofBinop(op)
            if (precedence.ordinal < base.ordinal) break
            if (precedence == Precedence.Compare && current is Expr.Binary) {
                if (Precedence.ofBinop(current.op) == Precedence.Compare) {
                    return SynResult.failure(input.error("comparison operators cannot be chained"))
                }
            }
            input.advanceTo(ahead)
            val rhsResult = parseBinopRhsImpl(input, allowStruct, precedence)
            if (rhsResult.isFailure) return rhsResult
            current = Expr.Binary(emptyList(), current, op, rhsResult.getOrThrow())
        } else if (Precedence.Assign.ordinal >= base.ordinal &&
            input.peek(EqPeek) &&
            !input.peek(FatArrowPeek) &&
            current !is Expr.Range
        ) {
            val eqResult = input.parse(EqParse)
            if (eqResult.isFailure) return eqResult.asFailure()
            val rhsResult = parseBinopRhsImpl(input, allowStruct, Precedence.Assign)
            if (rhsResult.isFailure) return rhsResult
            current = Expr.Assign(emptyList(), current, eqResult.getOrThrow(), rhsResult.getOrThrow())
        } else if (Precedence.Range.ordinal >= base.ordinal &&
            (input.peek(DotDotPeek) || input.peek(DotDotEqPeek))
        ) {
            val limitsResult = parseRangeLimits(input)
            if (limitsResult.isFailure) return limitsResult.asFailure()
            val endResult = parseRangeEnd(input, limitsResult.getOrThrow(), allowStruct)
            if (endResult.isFailure) return endResult.asFailure()
            current = Expr.Range(emptyList(), current, limitsResult.getOrThrow(), endResult.getOrThrow())
        } else if (Precedence.Cast.ordinal >= base.ordinal && input.peek(AsPeek)) {
            val asResult = input.parse(AsParse)
            if (asResult.isFailure) return asResult.asFailure()
            val tyResult = parseTypeWithoutPlus(input, allowGroupGeneric = false)
            if (tyResult.isFailure) return tyResult.asFailure()
            val castCheck = checkCastImpl(input)
            if (castCheck.isFailure) return castCheck.asFailure()
            current = Expr.Cast(emptyList(), current, asResult.getOrThrow(), tyResult.getOrThrow())
        } else {
            break
        }
    }
    return SynResult.success(current)
}

internal fun parseBinopRhsImpl(
    input: ParseStream,
    allowStruct: Boolean,
    left: Precedence,
): SynResult<Expr> {
    var rhs = unaryExprImpl(input, allowStruct)
    if (rhs.isFailure) return rhs
    var rhsExpr = rhs.getOrThrow()
    while (true) {
        val next = peekPrecedenceImpl(input)
        if (next.ordinal <= left.ordinal && !(next == left && left == Precedence.Assign)) break
        val cursor = input.currentCursor
        val inner = parseExprBinaryImpl(input, rhsExpr, allowStruct, next)
        if (inner.isFailure) return inner
        if (sameCursor(cursor, input.currentCursor)) break
        rhsExpr = inner.getOrThrow()
    }
    return SynResult.success(rhsExpr)
}

private fun sameCursor(left: Cursor, right: Cursor): Boolean =
    left.entries === right.entries && left.index == right.index && left.scope == right.scope

internal fun peekPrecedenceImpl(input: ParseStream): Precedence {
    val op = input.fork().parse(BinOpParse)
    if (op.isSuccess) return Precedence.ofBinop(op.getOrThrow())
    if (input.peek(EqPeek) && !input.peek(FatArrowPeek)) return Precedence.Assign
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek)) return Precedence.Range
    if (input.peek(AsPeek)) return Precedence.Cast
    return Precedence.MIN
}

internal fun checkCastImpl(input: ParseStream): SynResult<Unit> {
    val kind =
        when {
            input.peek(DotPeek) && !input.peek(DotDotPeek) ->
                if (input.peek2(AwaitPeek)) {
                    "`.await`"
                } else if (input.peek2(IdentPeek) && (input.peek3(ParenPeek) || input.peek3(PathSepPeek))) {
                    "a method call"
                } else {
                    "a field access"
                }
            input.peek(QuestionPeek) -> "`?`"
            input.peek(BracketPeek) -> "indexing"
            input.peek(ParenPeek) -> "a function call"
            else -> return SynResult.success(Unit)
        }
    return SynResult.failure(input.error("casts cannot be followed by $kind"))
}

internal fun exprAttrsImpl(input: ParseStream): SynResult<List<Attribute>> {
    val attrs = mutableListOf<Attribute>()
    while (!startsWithNoneGroup(input) && input.peek(PoundPeek) && !input.peek2(NotPeek)) {
        attrs.add(input.parse(AttributeParse).getOrElse { return SynResult.failure(it) })
    }
    return SynResult.success(attrs)
}

internal fun unaryExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val attrs = exprAttrsImpl(input).getOrElse { return SynResult.failure(it) }
    if (peekExprGroup(input, allowStruct)) {
        return trailerExprImpl(input, allowStruct, attrs)
    }
    if (input.peek(AndPeek)) {
        val andToken = input.parse(AndParse)
        if (andToken.isFailure) return andToken.asFailure()
        if (input.peek(RawPeek) && (input.peek2(MutPeek) || input.peek2(ConstPeek))) {
            val rawToken = input.parse(RawParse)
            if (rawToken.isFailure) return rawToken.asFailure()
            val mutResult = input.parse(MutParse)
            val mutability =
                if (mutResult.isSuccess) {
                    PointerMutability.Mut(mutResult.getOrThrow())
                } else {
                    val constResult = input.parse(ConstParse)
                    if (constResult.isFailure) return constResult.asFailure()
                    PointerMutability.Const(constResult.getOrThrow())
                }
            val inner = unaryExprImpl(input, allowStruct)
            if (inner.isFailure) return inner
            return SynResult.success(
                Expr.RawAddr(attrs, andToken.getOrThrow(), rawToken.getOrThrow(), mutability, inner.getOrThrow()),
            )
        }
        val mutResult = input.parse(MutParse)
        val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
        val inner = unaryExprImpl(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Reference(attrs, andToken.getOrThrow(), mutability, inner.getOrThrow()))
    }
    if (input.peek(NotPeek) || input.peek(StarPeek) || input.peek(MinusPeek)) {
        val opResult = input.parse(UnOpParse)
        if (opResult.isFailure) return opResult.asFailure()
        val inner = unaryExprImpl(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Unary(attrs, opResult.getOrThrow(), inner.getOrThrow()))
    }
    return trailerExprImpl(input, allowStruct, attrs)
}

internal fun trailerExprImpl(input: ParseStream, allowStruct: Boolean, attrs: List<Attribute> = emptyList()): SynResult<Expr> {
    val atomResult = atomExprImpl(input, allowStruct)
    if (atomResult.isFailure) return atomResult
    val trailed = trailerHelperImpl(input, atomResult.getOrThrow(), allowStruct)
    if (trailed.isFailure) return trailed
    val expr = trailed.getOrThrow()
    if (attrs.isEmpty()) return SynResult.success(expr)
    if (expr is Expr.Range && expr.start == null) {
        return SynResult.failure(input.error("attributes are not allowed on range expressions starting with `..`"))
    }
    return SynResult.success(expr.withPrependedAttrs(attrs))
}

private fun Expr.withPrependedAttrs(attrs: List<Attribute>): Expr =
    when (this) {
        is Expr.Array -> copy(attrs = attrs + this.attrs)
        is Expr.Assign -> copy(attrs = attrs + this.attrs)
        is Expr.Async -> copy(attrs = attrs + this.attrs)
        is Expr.Await -> copy(attrs = attrs + this.attrs)
        is Expr.Binary -> copy(attrs = attrs + this.attrs)
        is Expr.BlockExpr -> copy(attrs = attrs + this.attrs)
        is Expr.Break -> copy(attrs = attrs + this.attrs)
        is Expr.Call -> copy(attrs = attrs + this.attrs)
        is Expr.Cast -> copy(attrs = attrs + this.attrs)
        is Expr.Closure -> copy(attrs = attrs + this.attrs)
        is Expr.Const -> copy(attrs = attrs + this.attrs)
        is Expr.Continue -> copy(attrs = attrs + this.attrs)
        is Expr.Field -> copy(attrs = attrs + this.attrs)
        is Expr.ForLoop -> copy(attrs = attrs + this.attrs)
        is Expr.Group -> copy(attrs = attrs + this.attrs)
        is Expr.If -> copy(attrs = attrs + this.attrs)
        is Expr.Index -> copy(attrs = attrs + this.attrs)
        is Expr.Infer -> copy(attrs = attrs + this.attrs)
        is Expr.Let -> copy(attrs = attrs + this.attrs)
        is Expr.Lit -> copy(attrs = attrs + this.attrs)
        is Expr.Loop -> copy(attrs = attrs + this.attrs)
        is Expr.Macro -> copy(attrs = attrs + this.attrs)
        is Expr.Match -> copy(attrs = attrs + this.attrs)
        is Expr.MethodCall -> copy(attrs = attrs + this.attrs)
        is Expr.Paren -> copy(attrs = attrs + this.attrs)
        is Expr.Path -> copy(attrs = attrs + this.attrs)
        is Expr.Range -> copy(attrs = attrs + this.attrs)
        is Expr.RawAddr -> copy(attrs = attrs + this.attrs)
        is Expr.Reference -> copy(attrs = attrs + this.attrs)
        is Expr.Repeat -> copy(attrs = attrs + this.attrs)
        is Expr.Return -> copy(attrs = attrs + this.attrs)
        is Expr.Struct -> copy(attrs = attrs + this.attrs)
        is Expr.Try -> copy(attrs = attrs + this.attrs)
        is Expr.TryBlock -> copy(attrs = attrs + this.attrs)
        is Expr.Tuple -> copy(attrs = attrs + this.attrs)
        is Expr.Unary -> copy(attrs = attrs + this.attrs)
        is Expr.Unsafe -> copy(attrs = attrs + this.attrs)
        is Expr.While -> copy(attrs = attrs + this.attrs)
        is Expr.Yield -> copy(attrs = attrs + this.attrs)
        is Expr.Verbatim -> this
    }

internal fun trailerHelperImpl(input: ParseStream, e: Expr, allowStruct: Boolean): SynResult<Expr> {
    var current = e
    while (true) {
        if (input.peek(ParenPeek)) {
            val parens = parenthesized(input)
            if (parens.isFailure) return parens.asFailure()
            val parensVal = parens.getOrThrow()
            val paren = parensVal.token
            val content = parensVal.content
            val args = ExprList()
            while (!content.isEmpty()) {
                val argResult = content.call { parseExprFull(it) }
                if (argResult.isFailure) return argResult
                args.pushValue(argResult.getOrThrow())
                if (content.isEmpty()) break
                val commaResult = content.parse(CommaParse)
                if (commaResult.isFailure) break
                args.pushPunct(commaResult.getOrThrow())
            }
            content.finishChildBuffer()
            current = Expr.Call(emptyList(), current, paren, args)
        } else if (input.peek(DotPeek) && !input.peek2(DotDotPeek) && current !is Expr.Range) {
            val dotResult = input.parse(DotParse)
            if (dotResult.isFailure) return dotResult.asFailure()
            val dotToken = dotResult.getOrThrow()
            val floatAhead = input.fork()
            val floatResult = floatAhead.parse(LitFloatParse)
            if (floatResult.isSuccess) {
                input.advanceTo(floatAhead)
                val multi = multiIndexImpl(current, dotToken, floatResult.getOrThrow()).getOrElse { return SynResult.failure(it) }
                current = multi.expr
                if (multi.complete) {
                    continue
                }
            }
            if (input.peek(AwaitPeek)) {
                val awaitResult = input.parse(AwaitParse)
                if (awaitResult.isFailure) return awaitResult.asFailure()
                current = Expr.Await(emptyList(), current, dotToken, awaitResult.getOrThrow())
                continue
            }
            val memberResult = parseMemberImpl(input)
            if (memberResult.isFailure) return memberResult.asFailure()
            val member = memberResult.getOrThrow()
            if (input.peek(ParenPeek) && member is Member.Named) {
                val parens = parenthesized(input)
                if (parens.isFailure) return parens.asFailure()
                val parensVal = parens.getOrThrow()
                val paren = parensVal.token
                val content = parensVal.content
                val args = ExprList()
                while (!content.isEmpty()) {
                    val argResult = content.call { parseExprFull(it) }
                    if (argResult.isFailure) return argResult
                    args.pushValue(argResult.getOrThrow())
                    if (content.isEmpty()) break
                    val commaResult = content.parse(CommaParse)
                    if (commaResult.isFailure) break
                    args.pushPunct(commaResult.getOrThrow())
                }
                content.finishChildBuffer()
                current = Expr.MethodCall(emptyList(), current, dotToken, member.ident, null, paren, args)
                continue
            }
            current = Expr.Field(emptyList(), current, dotToken, member)
        } else if (input.peek(BracketPeek)) {
            val brackets = bracketed(input)
            if (brackets.isFailure) return brackets.asFailure()
            val bracketsVal = brackets.getOrThrow()
            val bracket = bracketsVal.token
            val content = bracketsVal.content
            val indexResult = content.call { parseExprFull(it) }
            if (indexResult.isFailure) return indexResult
            content.finishChildBuffer()
            current = Expr.Index(emptyList(), current, bracket, indexResult.getOrThrow())
        } else if (input.peek(QuestionPeek) && current !is Expr.Range) {
            val qResult = input.parse(QuestionParse)
            if (qResult.isFailure) return qResult.asFailure()
            current = Expr.Try(emptyList(), current, qResult.getOrThrow())
        } else {
            break
        }
    }
    return SynResult.success(current)
}

internal fun parseMemberImpl(input: ParseStream): SynResult<Member> {
    if (input.peek(IdentPeek)) {
        val identResult = input.parse(IdentParse)
        if (identResult.isFailure) return identResult.asFailure()
        return SynResult.success(Member.Named(identResult.getOrThrow()))
    }
    val ahead = input.fork()
    val litResult = ahead.parse(LitIntParse)
    if (litResult.isSuccess) {
        val lit = litResult.getOrThrow()
        val index = parseIndex(lit).getOrElse { return SynResult.failure(it) }
        input.advanceTo(ahead)
        return SynResult.success(Member.Unnamed(index))
    }
    return SynResult.failure(input.error("expected field name or index"))
}

internal data class MultiIndexResult(
    val expr: Expr,
    val complete: Boolean,
)

internal fun multiIndexImpl(
    expr: Expr,
    dotToken: io.github.kotlinmania.syn.token.Dot,
    float: LitFloat,
): SynResult<MultiIndexResult> {
    val floatToken = float.token()
    val floatSpan = floatToken.span()
    var floatRepr = floatToken.toString()
    val trailingDot = floatRepr.endsWith('.')
    if (trailingDot) {
        floatRepr = floatRepr.dropLast(1)
    }

    var current = expr
    var nextDot = dotToken
    var offset = 0
    for (part in floatRepr.split('.')) {
        if (part.isEmpty() && offset == 0) {
            offset = 1
            continue
        }
        val partEnd = offset + part.length
        val partSpan = floatToken.subspan(offset untilIntRange partEnd) ?: floatSpan
        val index = parseIndexPart(part, partSpan).getOrElse { return SynResult.failure(it) }
        current = Expr.Field(emptyList(), current, nextDot, Member.Unnamed(index))
        nextDot = io.github.kotlinmania.syn.token.Dot.from(floatToken.subspan(partEnd..partEnd) ?: floatSpan)
        offset = partEnd + 1
    }
    return SynResult.success(MultiIndexResult(current, complete = !trailingDot))
}

private infix fun Int.untilIntRange(endExclusive: Int): IntRange =
    if (this < endExclusive) this..(endExclusive - 1) else this..this

private fun parseIndexPart(part: String, span: Span): SynResult<Index> {
    if (part.isEmpty() || part.any { it !in '0'..'9' && it != '_' }) {
        return SynResult.failure(SynError.new(span, "expected unsuffixed integer"))
    }
    val digits = part.replace("_", "")
    val idx = digits.toUIntOrNull()
        ?: return SynResult.failure(SynError.new(span, "expected unsuffixed integer"))
    return SynResult.success(Index(idx, span))
}

private fun parseIndex(lit: LitInt): SynResult<Index> {
    val digits = lit.base10Digits()
    val idx = digits.toUIntOrNull()
        ?: return SynResult.failure(SynError.new(lit.span, "expected unsuffixed integer"))
    return SynResult.success(Index(idx, lit.span))
}

internal fun atomExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    if (peekExprGroup(input, allowStruct)) {
        return parseExprGroupImpl(input)
    }
    if (input.peek(LitPeek)) {
        val lit = input.parse(LitParse)
        if (lit.isSuccess) return SynResult.success(Expr.Lit(emptyList(), lit.getOrThrow()))
        return lit.asFailure()
    }
    if (input.peek(IfPeek)) return parseExprIf(input)
    if (input.peek(WhilePeek)) return parseExprWhile(input)
    if (input.peek(LoopPeek)) return parseExprLoop(input)
    if (input.peek(MatchPeek)) return parseExprMatch(input)
    if (input.peek(AsyncPeek) && input.peek2(BracePeek)) return parseExprAsync(input)
    if (input.peek(UnsafePeek) && input.peek2(BracePeek)) return parseExprUnsafe(input)
    if (input.peek(ConstPeek) && input.peek2(BracePeek)) return parseExprConst(input)
    if (input.peek(BracePeek)) return parseExprBlock(input)
    if (input.peek(ParenPeek)) return parenOrTupleImpl(input)
    if (input.peek(BracketPeek)) return arrayOrRepeatImpl(input)
    if (input.peek(LifetimePeek)) {
        val labeled = parseLabeledLoopOrWhile(input)
        if (labeled.isSuccess) return labeled
    }
    if (input.peek(ReturnPeek)) {
        val retToken = input.parse(ReturnParse)
        if (retToken.isFailure) return retToken.asFailure()
        val expr =
            if (peekExprStart(input, allowStruct = true)) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Return(emptyList(), retToken.getOrThrow(), expr.getOrThrow()))
    }
    if (input.peek(LetPeek)) return parseExprLetImpl(input, allowStruct)
    if (input.peek(BreakPeek)) {
        val brkToken = input.parse(BreakParse)
        if (brkToken.isFailure) return brkToken.asFailure()
        val labelResult = input.parse(LifetimeParse)
        val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
        val expr =
            if (peekExprStart(input, allowStruct = true) && (allowStruct || !input.peek(BracePeek))) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Break(emptyList(), brkToken.getOrThrow(), label, expr.getOrThrow()))
    }
    if (input.peek(ContinuePeek)) {
        val contToken = input.parse(ContinueParse)
        if (contToken.isFailure) return contToken.asFailure()
        val labelResult = input.parse(LifetimeParse)
        val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
        return SynResult.success(Expr.Continue(emptyList(), contToken.getOrThrow(), label))
    }
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek)) {
        return parseExprRange(input, null, allowStruct)
    }
    if (input.peek(MovePeek) ||
        input.peek(OrPeek) ||
        input.peek(OrOrPeek) ||
        (input.peek(ConstPeek) && !input.peek2(BracePeek)) ||
        (input.peek(AsyncPeek) && (input.peek2(OrPeek) || input.peek2(OrOrPeek) || input.peek2(MovePeek)))
    ) {
        return parseExprClosure(input, allowStruct)
    }
    if (input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek)
    ) {
        return pathOrMacroOrStructImpl(input, allowStruct)
    }
    if (input.peek(NotPeek) && input.peek2(IdentPeek)) {
        return pathOrMacroOrStructImpl(input, allowStruct)
    }
    return SynResult.failure(input.error("expected an expression"))
}

private fun peekExprStart(input: ParseStream, allowStruct: Boolean): Boolean =
    input.peek(LitPeek) ||
        (input.peek(PoundPeek) && !input.peek2(NotPeek)) ||
        input.peek(IfPeek) ||
        input.peek(WhilePeek) ||
        input.peek(LoopPeek) ||
        input.peek(MatchPeek) ||
        (input.peek(AsyncPeek) && (input.peek2(BracePeek) || input.peek2(OrPeek) || input.peek2(OrOrPeek) || input.peek2(MovePeek))) ||
        (input.peek(UnsafePeek) && input.peek2(BracePeek)) ||
        input.peek(ConstPeek) ||
        (allowStruct && input.peek(BracePeek)) ||
        input.peek(ParenPeek) ||
        input.peek(BracketPeek) ||
        input.peek(LifetimePeek) ||
        input.peek(ReturnPeek) ||
        input.peek(LetPeek) ||
        input.peek(BreakPeek) ||
        input.peek(ContinuePeek) ||
        input.peek(DotDotPeek) ||
        input.peek(DotDotEqPeek) ||
        input.peek(MovePeek) ||
        input.peek(OrPeek) ||
        input.peek(OrOrPeek) ||
        input.peek(AndPeek) ||
        input.peek(NotPeek) ||
        input.peek(StarPeek) ||
        input.peek(MinusPeek) ||
        input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek)

internal fun parseExprLetImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Let> {
    val letToken = input.parse(LetParse)
    if (letToken.isFailure) return letToken.asFailure()
    var pat = input.call { parsePatFull(it) }.getOrElse { return SynResult.failure(it) }
    if (input.peek(ColonPeek)) {
        val colonToken = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        pat = Pat.TypeAscription(emptyList(), pat, colonToken, ty)
    }
    val eqToken = input.parse(EqParse)
    if (eqToken.isFailure) return eqToken.asFailure()
    val lhs = unaryExprImpl(input, allowStruct)
    if (lhs.isFailure) return lhs.asFailure()
    val expr = parseExprBinaryImpl(input, lhs.getOrThrow(), allowStruct, Precedence.Compare)
    if (expr.isFailure) return expr.asFailure()
    return SynResult.success(Expr.Let(emptyList(), letToken.getOrThrow(), pat, eqToken.getOrThrow(), expr.getOrThrow()))
}

private fun peekExprGroup(input: ParseStream, allowStruct: Boolean): Boolean {
    val ahead = input.fork()
    if (parseGroup(ahead).isFailure) return false
    if (ahead.peek(NotPeek) || ahead.peek(PathSepPeek)) return false
    if (allowStruct && ahead.peek(BracePeek)) return false
    return true
}

internal fun parseExprGroupImpl(input: ParseStream): SynResult<Expr> {
    val group = parseGroup(input).getOrElse { return SynResult.failure(it) }
    val expr = group.content.call { parseExprFull(it) }.getOrElse { return SynResult.failure(it) }
    group.content.finishChildBuffer()
    return SynResult.success(Expr.Group(emptyList(), group.token, expr))
}

private fun parseLabeledLoopOrWhile(input: ParseStream): SynResult<Expr> {
    val ahead = input.fork()
    val lifetime = ahead.parse(LifetimeParse).getOrElse { return SynResult.failure(it) }
    val colon = ahead.parse(ColonParse).getOrElse { return SynResult.failure(it) }
    if (!ahead.peek(LoopPeek) && !ahead.peek(WhilePeek) && !ahead.peek(BracePeek)) {
        return SynResult.failure(input.error("expected loop or block expression"))
    }
    input.advanceTo(ahead)
    val label = Label(lifetime, colon)
    return if (input.peek(LoopPeek)) {
        parseExprLoop(input, label)
    } else if (input.peek(WhilePeek)) {
        parseExprWhile(input, label)
    } else {
        val block = parseExprBlock(input)
        if (block.isFailure) return block
        val expr = block.getOrThrow()
        if (expr !is Expr.BlockExpr) return SynResult.failure(input.error("expected block expression"))
        SynResult.success(expr.copy(label = label))
    }
}

internal fun pathOrMacroOrStructImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val pathResult = input.parse(PathParse)
    if (pathResult.isFailure) return pathResult.asFailure()
    val path = pathResult.getOrThrow()
    if (input.peek(NotPeek) && path.getIdent() != null) {
        val bangResult = input.parse(NotParse)
        if (bangResult.isFailure) return bangResult.asFailure()
        val delimResult = parseDelimiter(input)
        if (delimResult.isFailure) return delimResult.asFailure()
        val delimPair = delimResult.getOrThrow()
        return SynResult.success(
            Expr.Macro(
                emptyList(),
                Macro(path, bangResult.getOrThrow(), delimPair.first, delimPair.second),
            ),
        )
    }
    if (allowStruct && input.peek(BracePeek)) {
        val braceResult = braced(input)
        if (braceResult.isFailure) return braceResult.asFailure()
        val bracesVal = braceResult.getOrThrow()
        val brace = bracesVal.token
        val content = bracesVal.content
        val fields = FieldValueList()
        while (!content.isEmpty()) {
            val fieldResult = content.call { parseFieldValue(it) }
            if (fieldResult.isFailure) return fieldResult.asFailure()
            fields.pushValue(fieldResult.getOrThrow())
            if (content.isEmpty()) break
            val commaResult = content.parse(CommaParse)
            if (commaResult.isFailure) break
            fields.pushPunct(commaResult.getOrThrow())
        }
        content.finishChildBuffer()
        return SynResult.success(Expr.Struct(emptyList(), null, path, brace, fields, null, null))
    }
    return SynResult.success(Expr.Path(emptyList(), null, path))
}

internal fun parseFieldValueImpl(input: ParseStream): SynResult<FieldValue> {
    val identResult = input.parse(IdentParse)
    if (identResult.isFailure) {
        return identResult.map {
            FieldValue(emptyList(), Member.Named(it), null, Expr.Path(emptyList(), null, Path.from(it)))
        }
    }
    val ident = identResult.getOrThrow()
    if (input.peek(ColonPeek)) {
        val colonResult = input.parse(ColonParse)
        if (colonResult.isFailure) {
            return colonResult.map {
                FieldValue(emptyList(), Member.Named(ident), it, Expr.Path(emptyList(), null, Path.from(ident)))
            }
        }
        val exprResult = parseExprFull(input)
        if (exprResult.isFailure) {
            return exprResult.map {
                FieldValue(emptyList(), Member.Named(ident), colonResult.getOrThrow(), it)
            }
        }
        return SynResult.success(
            FieldValue(emptyList(), Member.Named(ident), colonResult.getOrThrow(), exprResult.getOrThrow()),
        )
    }
    return SynResult.success(
        FieldValue(emptyList(), Member.Named(ident), null, Expr.Path(emptyList(), null, Path.from(ident))),
    )
}

internal fun parenOrTupleImpl(input: ParseStream): SynResult<Expr> {
    val parens = parenthesized(input)
    if (parens.isFailure) return parens.asFailure()
    val parensVal = parens.getOrThrow()
    val paren = parensVal.token
    val content = parensVal.content
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Tuple(emptyList(), paren, ExprList()))
    }
    val first = content.call { parseExprFull(it) }
    if (first.isFailure) return first
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Paren(emptyList(), paren, first.getOrThrow()))
    }
    val elems = ExprList()
    elems.pushValue(first.getOrThrow())
    while (!content.isEmpty()) {
        val commaResult = content.parse(CommaParse)
        if (commaResult.isFailure) break
        elems.pushPunct(commaResult.getOrThrow())
        if (content.isEmpty()) break
        val valResult = content.call { parseExprFull(it) }
        if (valResult.isFailure) return valResult
        elems.pushValue(valResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Tuple(emptyList(), paren, elems))
}

internal fun arrayOrRepeatImpl(input: ParseStream): SynResult<Expr> {
    val brackets = bracketed(input)
    if (brackets.isFailure) return brackets.asFailure()
    val bracketsVal = brackets.getOrThrow()
    val bracket = bracketsVal.token
    val content = bracketsVal.content
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Array(emptyList(), bracket, ExprList()))
    }
    val first = content.call { parseExprFull(it) }
    if (first.isFailure) return first
    if (content.isEmpty() || content.peek(CommaPeek)) {
        val elems = ExprList()
        elems.pushValue(first.getOrThrow())
        while (!content.isEmpty()) {
            val commaResult = content.parse(CommaParse)
            if (commaResult.isFailure) break
            elems.pushPunct(commaResult.getOrThrow())
            if (content.isEmpty()) break
            val valResult = content.call { parseExprFull(it) }
            if (valResult.isFailure) return valResult
            elems.pushValue(valResult.getOrThrow())
        }
        content.finishChildBuffer()
        return SynResult.success(Expr.Array(emptyList(), bracket, elems))
    }
    if (content.peek(SemiPeek)) {
        val semiResult = content.parse(SemiParse)
        if (semiResult.isFailure) return semiResult.asFailure()
        val lenResult = content.call { parseExprFull(it) }
        if (lenResult.isFailure) return lenResult
        content.finishChildBuffer()
        return SynResult.success(Expr.Repeat(emptyList(), bracket, first.getOrThrow(), semiResult.getOrThrow(), lenResult.getOrThrow()))
    }
    content.finishChildBuffer()
    return SynResult.failure(content.error("expected `,` or `;`"))
}

private fun parseExprRange(input: ParseStream, start: Expr?, allowStruct: Boolean): SynResult<Expr> {
    val limitsResult = parseRangeLimits(input)
    if (limitsResult.isFailure) return limitsResult.asFailure()
    val limits = limitsResult.getOrThrow()
    val endResult = parseRangeEnd(input, limits, allowStruct)
    if (endResult.isFailure) return endResult.asFailure()
    return SynResult.success(Expr.Range(emptyList(), start, limits, endResult.getOrThrow()))
}

private fun parseRangeLimits(input: ParseStream): SynResult<RangeLimits> =
    when {
        input.peek(DotDotEqPeek) -> {
            val dotDotEqResult = input.parse(DotDotEqParse)
            if (dotDotEqResult.isFailure) dotDotEqResult.asFailure() else SynResult.success(RangeLimits.Closed(dotDotEqResult.getOrThrow()))
        }
        input.peek(DotDotPeek) -> {
            val dotDotResult = input.parse(DotDotParse)
            if (dotDotResult.isFailure) dotDotResult.asFailure() else SynResult.success(RangeLimits.HalfOpen(dotDotResult.getOrThrow()))
        }
        else -> SynResult.failure(input.error("expected range limits"))
    }

private fun parseRangeEnd(input: ParseStream, limits: RangeLimits, allowStruct: Boolean): SynResult<Expr?> {
    if (input.isEmpty() ||
        input.peek(SemiPeek) ||
        input.peek(CommaPeek) ||
        (input.peek(DotPeek) && !input.peek(DotDotPeek)) ||
        input.peek(QuestionPeek) ||
        input.peek(FatArrowPeek) ||
        (!allowStruct && input.peek(BracePeek)) ||
        input.peek(EqPeek) ||
        input.peek(AsPeek) ||
        input.fork().parse(BinOpParse).isSuccess
    ) {
        if (limits is RangeLimits.HalfOpen) {
            return SynResult.success(null)
        }
    }
    val endResult = parseBinopRhsImpl(input, allowStruct, Precedence.Range)
    if (endResult.isFailure) return endResult
    return SynResult.success(endResult.getOrThrow())
}

private fun parseExprIf(input: ParseStream): SynResult<Expr> {
    val ifToken = input.parse(IfParse).getOrThrow()
    val cond = ambiguousExprImpl(input, allowStruct = false)
    if (cond.isFailure) return cond
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val stmtResult = content.call { parseStmtFull(it) }
        if (stmtResult.isFailure) break
        stmts.add(stmtResult.getOrThrow())
    }
    content.finishChildBuffer()
    val thenBranch = Block(brace, stmts)
    var elseBranch: ElseExpr? = null
    if (input.peek(ElsePeek)) {
        val elseToken = input.parse(ElseParse).getOrThrow()
        if (input.peek(IfPeek)) {
            val inner = parseExprIf(input)
            if (inner.isFailure) return inner
            elseBranch = ElseExpr(elseToken, inner.getOrThrow())
        } else if (input.peek(BracePeek)) {
            val elseBrace = braced(input)
            if (elseBrace.isFailure) return elseBrace.asFailure()
            val eBracesVal = elseBrace.getOrThrow()
            val eBrace = eBracesVal.token
            val eContent = eBracesVal.content
            val eStmts = mutableListOf<Stmt>()
            while (!eContent.isEmpty()) {
                val s = eContent.call { parseStmtFull(it) }
                if (s.isFailure) break
                eStmts.add(s.getOrThrow())
            }
            eContent.finishChildBuffer()
            elseBranch = ElseExpr(elseToken, Expr.BlockExpr(emptyList(), null, Block(eBrace, eStmts)))
        }
    }
    return SynResult.success(Expr.If(emptyList(), ifToken, cond.getOrThrow(), thenBranch, elseBranch))
}

private fun parseExprWhile(input: ParseStream, label: Label? = null): SynResult<Expr> {
    val whileToken = input.parse(WhileParse).getOrThrow()
    val cond = ambiguousExprImpl(input, allowStruct = false)
    if (cond.isFailure) return cond
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = content.call { parseStmtFull(it) }
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.While(emptyList(), label, whileToken, cond.getOrThrow(), Block(brace, stmts)))
}

private fun parseExprLoop(input: ParseStream, label: Label? = null): SynResult<Expr> {
    val loopToken = input.parse(LoopParse).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = content.call { parseStmtFull(it) }
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Loop(emptyList(), label, loopToken, Block(brace, stmts)))
}

private fun parseExprMatch(input: ParseStream): SynResult<Expr> {
    val matchToken = input.parse(MatchParse).getOrThrow()
    val scrutinee = ambiguousExprImpl(input, allowStruct = false)
    if (scrutinee.isFailure) return scrutinee
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val arms = mutableListOf<Arm>()
    while (!content.isEmpty()) {
        val armResult = content.call { parseMatchArm(it) }
        if (armResult.isFailure) break
        arms.add(armResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Match(emptyList(), matchToken, scrutinee.getOrThrow(), brace, arms))
}

private fun parseMatchArm(input: ParseStream): SynResult<Arm> {
    val patResult = input.call { parsePatFull(it) }
    if (patResult.isFailure) {
        return patResult.map { Arm(emptyList(), it, null, fatArrowSentinel(input), inferSentinel(input), null) }
    }
    val fatArrowResult = input.parse(FatArrowParse)
    if (fatArrowResult.isFailure) {
        return fatArrowResult.map {
            Arm(emptyList(), patResult.getOrThrow(), null, it, inferSentinel(input), null)
        }
    }
    val bodyResult = parseExprFull(input)
    if (bodyResult.isFailure) {
        return bodyResult.map {
            Arm(emptyList(), patResult.getOrThrow(), null, fatArrowResult.getOrThrow(), it, null)
        }
    }
    val commaResult = input.parse(CommaParse)
    val comma = if (commaResult.isSuccess) commaResult.getOrThrow() else null
    return SynResult.success(
        Arm(emptyList(), patResult.getOrThrow(), null, fatArrowResult.getOrThrow(), bodyResult.getOrThrow(), comma),
    )
}

private fun fatArrowSentinel(input: ParseStream): io.github.kotlinmania.syn.token.FatArrow =
    io.github.kotlinmania.syn.token.FatArrow
        .from(input.span())

private fun inferSentinel(input: ParseStream): Expr =
    Expr.Infer(
        emptyList(),
        io.github.kotlinmania.syn.token.Underscore
            .from(input.span()),
    )

internal fun inferSentinelType(input: ParseStream): SynType =
    SynType.Infer(
        io.github.kotlinmania.syn.token.Underscore
            .from(input.span()),
    )

private fun parseExprAsync(input: ParseStream): SynResult<Expr> {
    val asyncToken = input.parse(AsyncParse).getOrThrow()
    val moveResult = input.parse(MoveParse)
    val capture = if (moveResult.isSuccess) moveResult.getOrThrow() else null
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = content.call { parseStmtFull(it) }
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Async(emptyList(), asyncToken, capture, Block(brace, stmts)))
}

private fun parseExprUnsafe(input: ParseStream): SynResult<Expr> {
    val unsafeToken = input.parse(UnsafeParse).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = content.call { parseStmtFull(it) }
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Unsafe(emptyList(), unsafeToken, Block(brace, stmts)))
}

private fun parseExprConst(input: ParseStream): SynResult<Expr> {
    val constToken = input.parse(ConstParse).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = content.call { parseStmtFull(it) }
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Const(emptyList(), constToken, Block(brace, stmts)))
}

private fun parseExprBlock(input: ParseStream): SynResult<Expr> {
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = content.call { parseStmtFull(it) }
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.BlockExpr(emptyList(), null, Block(brace, stmts)))
}

private fun parseExprClosure(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val constnessResult = input.parse(ConstParse)
    val constness = if (constnessResult.isSuccess) constnessResult.getOrThrow() else null
    val asyncnessResult = input.parse(AsyncParse)
    val asyncness = if (asyncnessResult.isSuccess) asyncnessResult.getOrThrow() else null
    val moveResult = input.parse(MoveParse)
    val capture = if (moveResult.isSuccess) moveResult.getOrThrow() else null
    val or1Result = parseClosureOr(input)
    if (or1Result.isFailure) return or1Result.asFailure()
    val inputs = PatList()
    while (!input.isEmpty() && !input.peek(OrPeek)) {
        val patResult = input.call { parsePatFull(it) }
        if (patResult.isFailure) return patResult.asFailure()
        inputs.pushValue(patResult.getOrThrow())
        if (input.isEmpty()) break
        val commaResult = input.parse(CommaParse)
        if (commaResult.isFailure) break
        inputs.pushPunct(commaResult.getOrThrow())
    }
    val or2Result = parseClosureOr(input)
    if (or2Result.isFailure) return or2Result.asFailure()
    var output: ReturnType = ReturnType.Default
    if (input.peek(RArrowPeek)) {
        val arrowResult = input.parse(RArrowParse)
        if (arrowResult.isFailure) return arrowResult.asFailure()
        val tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return tyResult.asFailure()
        output = ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow())
        val bodyResult = parseExprBlock(input)
        if (bodyResult.isFailure) return bodyResult
        return SynResult.success(Expr.Closure(emptyList(), constness, asyncness, capture, or1Result.getOrThrow(), inputs, or2Result.getOrThrow(), output, bodyResult.getOrThrow()))
    }
    val bodyResult = ambiguousExprImpl(input, allowStruct)
    if (bodyResult.isFailure) return bodyResult
    return SynResult.success(Expr.Closure(emptyList(), constness, asyncness, capture, or1Result.getOrThrow(), inputs, or2Result.getOrThrow(), output, bodyResult.getOrThrow()))
}

private fun parseClosureOr(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Or> =
    input.step { cursor ->
        val (punct, rest) =
            cursor.punct()
                ?: return@step SynResult.failure(cursor.error("expected `|`"))
        if (punct.asChar() != '|') {
            return@step SynResult.failure(cursor.error("expected `|`"))
        }
        SynResult.success(io.github.kotlinmania.syn.token.Or.from(punct.span()) to rest)
    }

internal fun parseStmtFull(input: ParseStream): SynResult<Stmt> {
    if (input.peek(LetPeek) && !startsWithNoneGroup(input)) {
        val letToken = input.parse(LetParse).getOrThrow()
        var pat = input.call { parsePatFull(it) }.getOrElse { return SynResult.failure(it) }
        if (input.peek(ColonPeek)) {
            val colonToken = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            pat = Pat.TypeAscription(emptyList(), pat, colonToken, ty)
        }
        val init: LocalInit? =
            if (input.peek(EqPeek)) {
                val eq = input.parse(EqParse).getOrThrow()
                val e = parseExprFull(input)
                if (e.isFailure) {
                    null
                } else {
                    val diverge =
                        if (input.peek(ElsePeek)) {
                            val elseToken = input.parse(ElseParse).getOrThrow()
                            val block = parseExprBlock(input)
                            if (block.isFailure) return block.asFailure()
                            ElseExpr(elseToken, block.getOrThrow())
                        } else {
                            null
                        }
                    LocalInit(eq, e.getOrThrow(), diverge)
                }
            } else {
                null
            }
        val semi = input.parse(SemiParse).getOrThrow()
        return SynResult.success(Stmt.Local(emptyList(), letToken, pat, init, semi))
    }
    if (peekItemStatement(input)) {
        val item = ItemParse.parse(input)
        if (item.isFailure) return item.asFailure()
        return SynResult.success(Stmt.ItemStmt(item.getOrThrow()))
    }
    val exprResult = parseExprWithEarlierBoundaryRule(input)
    if (exprResult.isFailure) return exprResult.asFailure()
    if (input.peek(SemiPeek)) {
        val semi = input.parse(SemiParse).getOrThrow()
        val expr = exprResult.getOrThrow()
        if (expr is Expr.Macro) {
            return SynResult.success(Stmt.MacroStmt(expr.attrs, expr.mac, semi))
        }
        return SynResult.success(Stmt.ExprStmt(exprResult.getOrThrow(), semi))
    }
    val expr = exprResult.getOrThrow()
    if (expr is Expr.Macro && expr.mac.isBrace()) {
        return SynResult.success(Stmt.MacroStmt(expr.attrs, expr.mac, null))
    }
    return SynResult.success(Stmt.ExprStmt(expr, null))
}

private fun startsWithNoneGroup(input: ParseStream): Boolean =
    parseGroup(input.fork()).isSuccess

private fun peekItemStatement(input: ParseStream): Boolean =
    peekSignature(input) ||
        input.peek(StructPeek) ||
        input.peek(EnumPeek) ||
        input.peek(TraitPeek) ||
        input.peek(ImplPeek) ||
        input.peek(UsePeek) ||
        input.peek(ModPeek) ||
        peekItemMacro(input)

private fun peekItemMacro(input: ParseStream): Boolean {
    val ahead = input.fork()
    if (parseModStylePath(ahead).isFailure) return false
    if (!ahead.peek(NotPeek)) return false
    return ahead.peek2(IdentPeek)
}

internal fun parsePatFull(input: ParseStream): SynResult<Pat> = PatParseImpl.parse(input)

internal fun parseTypeFull(input: ParseStream): SynResult<SynType> =
    parseType(input, allowPlus = true, allowGroupGeneric = true)

internal fun parseTypeWithoutPlus(
    input: ParseStream,
    allowGroupGeneric: Boolean = true,
): SynResult<SynType> =
    parseType(input, allowPlus = false, allowGroupGeneric = allowGroupGeneric)

internal object PatParseImpl : Parse<Pat> {
    override fun parse(input: ParseStream): SynResult<Pat> = parsePatSingle(input)
}

private fun parsePatSingle(input: ParseStream): SynResult<Pat> {
    if (input.peek(UnderscorePeek)) {
        val underscore = input.parse(UnderscoreParse).getOrThrow()
        return SynResult.success(Pat.Wild(emptyList(), underscore))
    }
    if ((input.peek(DotDotPeek) || input.peek(DotDotEqPeek)) && !input.peek(DotDotDotPeek)) {
        return parsePatRangeHalfOpen(input)
    }
    if (input.peek(BracketPeek)) {
        return parsePatSlice(input).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }
    if (input.peek(AndPeek)) {
        return parsePatReference(input).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }
    if (input.peek(ParenPeek)) {
        return parsePatParenOrTuple(input)
    }
    if (input.peek(RefPeek) || input.peek(MutPeek)) {
        return parsePatIdent(input).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }
    if (input.peek(LitPeek) || (input.peek(ConstPeek) && input.peek2(BracePeek))) {
        return parsePatLitOrRange(input)
    }
    if (input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek)
    ) {
        return parsePatPathOrMacroOrStructOrRange(input)
    }
    return SynResult.failure(input.error("unsupported pattern"))
}

internal fun parsePatMultiWithLeadingVert(input: ParseStream): SynResult<Pat> {
    val leadingVert =
        if (input.peek(OrPeek) && !input.peek(OrOrPeek) && !input.peek(OrEqPeek)) {
            input.parse(OrParse).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    return multiPatImpl(input, leadingVert)
}

private fun multiPatImpl(
    input: ParseStream,
    leadingVert: io.github.kotlinmania.syn.token.Or?,
): SynResult<Pat> {
    var pat = parsePatSingle(input).getOrElse { return SynResult.failure(it) }
    if (leadingVert != null || (input.peek(OrPeek) && !input.peek(OrOrPeek) && !input.peek(OrEqPeek))) {
        val cases = PatList()
        cases.pushValue(pat)
        while (input.peek(OrPeek) && !input.peek(OrOrPeek) && !input.peek(OrEqPeek)) {
            cases.pushPunct(input.parse(OrParse).getOrElse { return SynResult.failure(it) })
            cases.pushValue(parsePatSingle(input).getOrElse { return SynResult.failure(it) })
        }
        pat = Pat.Or(leadingVert, cases)
    }
    return SynResult.success(pat)
}

private fun parsePatPathOrMacroOrStructOrRange(input: ParseStream): SynResult<Pat> {
    val path = parseModStylePath(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(NotPeek) && !input.peek(NePeek)) {
        val bangToken = input.parse(NotParse).getOrElse { return SynResult.failure(it) }
        val delimiter = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Pat.Macro(emptyList(), Macro(path, bangToken, delimiter.first, delimiter.second)))
    }
    if (input.peek(BracePeek)) {
        return parsePatStruct(input, path).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }
    if (input.peek(ParenPeek)) {
        return parsePatTupleStruct(input, path).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek) || input.peek(DotDotDotPeek)) {
        return parsePatRange(input, Expr.Path(emptyList(), null, path))
    }
    val ident = path.getIdent()
    if (ident != null) {
        return SynResult.success(Pat.Ident(emptyList(), null, FieldMutability.None, ident, null, null))
    }
    return SynResult.success(Pat.Path(emptyList(), null, path))
}

private fun parsePatIdent(input: ParseStream): SynResult<Pat.Ident> {
    val byRef = input.parse(RefParse).getOrNull()
    val mutability =
        input.parse(MutParse).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
    val ident =
        if (input.peek(SelfValuePeek)) {
            identFromSelfValue(input.parse(SelfValueParse).getOrThrow())
        } else {
            input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
        }
    val atToken = input.parse(AtParse).getOrNull()
    val subpat =
        if (atToken != null) {
            parsePatSingle(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    return SynResult.success(Pat.Ident(emptyList(), byRef, mutability, ident, atToken, subpat))
}

private fun parsePatTupleStruct(
    input: ParseStream,
    path: Path,
): SynResult<Pat.TupleStruct> {
    val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    val elems = PatList()
    while (!parens.content.isEmpty()) {
        elems.pushValue(parsePatMultiWithLeadingVert(parens.content).getOrElse { return SynResult.failure(it) })
        if (parens.content.isEmpty()) break
        elems.pushPunct(parens.content.parse(CommaParse).getOrElse { return SynResult.failure(it) })
    }
    parens.content.finishChildBuffer()
    return SynResult.success(Pat.TupleStruct(emptyList(), null, path, parens.token, elems))
}

private fun parsePatStruct(
    input: ParseStream,
    path: Path,
): SynResult<Pat.Struct> {
    val braces = braced(input).getOrElse { return SynResult.failure(it) }
    val fields = FieldPatList()
    var rest: PatRest? = null
    while (!braces.content.isEmpty()) {
        parseOuterAttributes(braces.content).getOrElse { return SynResult.failure(it) }
        if (braces.content.peek(DotDotPeek) && !braces.content.peek(DotDotDotPeek)) {
            rest = PatRest(braces.content.parse(DotDotParse).getOrElse { return SynResult.failure(it) })
            break
        }
        fields.pushValue(parseFieldPat(braces.content).getOrElse { return SynResult.failure(it) })
        if (braces.content.isEmpty()) break
        fields.pushPunct(braces.content.parse(CommaParse).getOrElse { return SynResult.failure(it) })
    }
    braces.content.finishChildBuffer()
    return SynResult.success(Pat.Struct(null, path, braces.token, fields, rest, null))
}

private fun parseFieldPat(input: ParseStream): SynResult<FieldPat> {
    val byRef = input.parse(RefParse).getOrNull()
    val mutability =
        input.parse(MutParse).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
    val member = parseMemberImpl(input).getOrElse { return SynResult.failure(it) }
    if ((byRef == null && mutability is FieldMutability.None && input.peek(ColonPeek)) || member is Member.Unnamed) {
        val colon = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
        val pat = parsePatMultiWithLeadingVert(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(FieldPat(member, colon, pat))
    }
    val ident =
        when (member) {
            is Member.Named -> member.ident
            is Member.Unnamed -> return SynResult.failure(input.error("expected named field"))
        }
    val pat = Pat.Ident(emptyList(), byRef, mutability, ident, null, null)
    return SynResult.success(FieldPat(Member.Named(ident), null, pat))
}

private fun parsePatRange(
    input: ParseStream,
    start: Expr,
): SynResult<Pat> {
    val limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
    val end = parsePatRangeBound(input).getOrElse { return SynResult.failure(it) }
    if (limits is RangeLimits.Closed && end == null) {
        return SynResult.failure(input.error("expected range upper bound"))
    }
    return SynResult.success(Pat.Range(emptyList(), start, limits, end))
}

private fun parsePatRangeHalfOpen(input: ParseStream): SynResult<Pat> {
    val limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
    val end = parsePatRangeBound(input).getOrElse { return SynResult.failure(it) }
    if (end != null) {
        return SynResult.success(Pat.Range(emptyList(), null, limits, end))
    }
    return when (limits) {
        is RangeLimits.HalfOpen -> SynResult.success(Pat.Rest(emptyList(), limits.token))
        is RangeLimits.Closed -> SynResult.failure(input.error("expected range upper bound"))
    }
}

private fun parsePatLitOrRange(input: ParseStream): SynResult<Pat> {
    val start = parsePatRangeBound(input).getOrElse { return SynResult.failure(it) }
        ?: return SynResult.failure(input.error("expected range bound"))
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek) || input.peek(DotDotDotPeek)) {
        val limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
        val end = parsePatRangeBound(input).getOrElse { return SynResult.failure(it) }
        if (limits is RangeLimits.Closed && end == null) {
            return SynResult.failure(input.error("expected range upper bound"))
        }
        return SynResult.success(Pat.Range(emptyList(), start, limits, end))
    }
    return when (start) {
        is Expr.Const -> SynResult.success(Pat.Const(start.attrs, start.constToken, start.block))
        is Expr.Lit -> SynResult.success(Pat.Lit(start.attrs, start.lit))
        is Expr.Path -> SynResult.success(Pat.Path(start.attrs, start.qself, start.path))
        else -> SynResult.failure(input.error("expected literal, const block, or path pattern"))
    }
}

private fun parsePatRangeLimitsObsolete(input: ParseStream): SynResult<RangeLimits> {
    if (input.peek(DotDotDotPeek)) {
        val dots = input.parse(DotDotDotParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(RangeLimits.Closed(io.github.kotlinmania.syn.token.DotDotEq.from(dots.spans)))
    }
    return parseRangeLimits(input)
}

private fun parsePatRangeBound(input: ParseStream): SynResult<Expr?> {
    if (input.isEmpty() ||
        input.peek(OrPeek) ||
        input.peek(EqPeek) ||
        (input.peek(ColonPeek) && !input.peek(PathSepPeek)) ||
        input.peek(CommaPeek) ||
        input.peek(SemiPeek) ||
        input.peek(IfPeek)
    ) {
        return SynResult.success(null)
    }
    if (input.peek(LitPeek)) {
        val lit = input.parse(LitParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Expr.Lit(emptyList(), lit))
    }
    if (input.peek(ConstPeek) && input.peek2(BracePeek)) {
        return parseExprConst(input).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }
    if (input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek)
    ) {
        val path = parseModStylePath(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Expr.Path(emptyList(), null, path))
    }
    return SynResult.failure(input.error("expected range bound"))
}

private fun parsePatParenOrTuple(input: ParseStream): SynResult<Pat> {
    val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    val content = parens.content
    val elems = PatList()
    while (!content.isEmpty()) {
        val value = parsePatMultiWithLeadingVert(content).getOrElse { return SynResult.failure(it) }
        if (content.isEmpty()) {
            if (elems.isEmpty() && value !is Pat.Rest) {
                content.finishChildBuffer()
                return SynResult.success(Pat.PatParen(parens.token, value))
            }
            elems.pushValue(value)
            break
        }
        elems.pushValue(value)
        elems.pushPunct(content.parse(CommaParse).getOrElse { return SynResult.failure(it) })
    }
    content.finishChildBuffer()
    return SynResult.success(Pat.Tuple(parens.token, elems))
}

private fun parsePatReference(input: ParseStream): SynResult<Pat.Reference> {
    val andToken = input.parse(AndParse).getOrElse { return SynResult.failure(it) }
    val mutability =
        input.parse(MutParse).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
    val pat = parsePatSingle(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Pat.Reference(andToken, mutability, pat))
}

private fun parsePatSlice(input: ParseStream): SynResult<Pat.Slice> {
    val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    val elems = PatList()
    while (!brackets.content.isEmpty()) {
        val value = parsePatMultiWithLeadingVert(brackets.content).getOrElse { return SynResult.failure(it) }
        if (value is Pat.Range && (value.start == null || value.end == null)) {
            return SynResult.failure(brackets.content.error("range pattern is not allowed unparenthesized inside slice pattern"))
        }
        elems.pushValue(value)
        if (brackets.content.isEmpty()) break
        elems.pushPunct(brackets.content.parse(CommaParse).getOrElse { return SynResult.failure(it) })
    }
    brackets.content.finishChildBuffer()
    return SynResult.success(Pat.Slice(brackets.token, elems))
}

internal object SynTypeParseExpr : Parse<SynType> {
    override fun parse(input: ParseStream): SynResult<SynType> =
        parseType(input, allowPlus = true, allowGroupGeneric = true)
}

private fun parseType(
    input: ParseStream,
    allowPlus: Boolean,
    allowGroupGeneric: Boolean,
): SynResult<SynType> {
        val groupAhead = input.fork()
        val groupResult = parseGroup(groupAhead)
        if (groupResult.isSuccess) {
            input.advanceTo(groupAhead)
            val group = groupResult.getOrThrow()
            val elem = group.content.call { parseTypeFull(it) }
            if (elem.isFailure) return elem.asFailure()
            group.content.finishChildBuffer()
            val elemValue = elem.getOrThrow()
            if (elemValue is SynType.Path) {
                if (allowGroupGeneric && (input.peek(LtPeek) || (input.peek(PathSepPeek) && input.peek3(LtPeek)))) {
                    val last = elemValue.path.segments.last()
                    if (last != null && last.arguments.isNone()) {
                        last.arguments = parseAngleBracketedPathArguments(input).getOrElse {
                            return SynResult.failure(it)
                        }
                        parsePathRest(input, elemValue.path).getOrElse { return SynResult.failure(it) }
                        return SynResult.success(elemValue)
                    }
                }
                if (input.peek(PathSepPeek) && input.peek3(IdentPeekAny)) {
                    parsePathRest(input, elemValue.path).getOrElse { return SynResult.failure(it) }
                    return SynResult.success(elemValue)
                }
            } else if (input.peek(PathSepPeek) && input.peek3(IdentPeekAny)) {
                val path = input.parse(PathParse).getOrElse { return SynResult.failure(it) }
                val qself =
                    QSelf(
                        io.github.kotlinmania.syn.token.Lt.from(group.token.span),
                        elemValue,
                        position = 0,
                        asToken = null,
                        gtToken = io.github.kotlinmania.syn.token.Gt.from(group.token.span),
                    )
                return SynResult.success(SynType.Path(qself, path))
            }
            return SynResult.success(SynType.Group(group.token, elemValue))
        }
        if (input.peek(UnderscorePeek)) {
            val underscore = input.parse(UnderscoreParse).getOrThrow()
            return SynResult.success(SynType.Infer(underscore))
        }
        if (input.peek(NotPeek)) {
            val bang = input.parse(NotParse).getOrThrow()
            return SynResult.success(SynType.Never(bang))
        }
        if (input.peek(ImplPeek)) {
            val implToken = input.parse(ImplParse).getOrThrow()
            val bounds = parseTypeParamBounds(input, stopAtEq = false, allowPreciseCapture = true, allowPlus = allowPlus)
            if (bounds.isFailure) return bounds.asFailure()
            return SynResult.success(SynType.ImplTrait(implToken, bounds.getOrThrow()))
        }
        if (input.peek(DynPeek)) {
            val dynToken = input.parse(DynParse).getOrThrow()
            val bounds = parseTypeParamBounds(input, stopAtEq = false, allowPlus = allowPlus)
            if (bounds.isFailure) return bounds.asFailure()
            return SynResult.success(SynType.TraitObject(dynToken, bounds.getOrThrow()))
        }
        if (input.peek(AndPeek)) {
            val andToken = input.parse(AndParse).getOrThrow()
            val ltResult = input.parse(LifetimeParse)
            val lifetime = if (ltResult.isSuccess) ltResult.getOrThrow() else null
            val mutResult = input.parse(MutParse)
            val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
            val inner = parseTypeWithoutPlus(input)
            if (inner.isFailure) return inner.asFailure()
            return SynResult.success(SynType.Reference(andToken, lifetime, mutability, inner.getOrThrow()))
        }
        if (input.peek(StarPeek)) {
            val starToken = input.parse(StarParse).getOrThrow()
            val constResult = input.parse(ConstParse)
            val mutResult = input.parse(MutParse)
            val constToken = if (constResult.isSuccess) constResult.getOrThrow() else null
            val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
            val inner = parseTypeWithoutPlus(input)
            if (inner.isFailure) return inner.asFailure()
            return SynResult.success(SynType.Ptr(starToken, constToken, mutability, inner.getOrThrow()))
        }
        if (input.peek(BracketPeek)) {
            val brackets = bracketed(input)
            if (brackets.isFailure) return brackets.asFailure()
            val bracketsVal = brackets.getOrThrow()
            val elem = parseTypeFull(bracketsVal.content).getOrElse { return SynResult.failure(it) }
            if (bracketsVal.content.peek(SemiPeek)) {
                bracketsVal.content.parse(SemiParse).getOrElse { return SynResult.failure(it) }
                val len = parseExprFull(bracketsVal.content).getOrElse { return SynResult.failure(it) }
                bracketsVal.content.finishChildBuffer()
                return SynResult.success(SynType.Array(elem, len))
            }
            bracketsVal.content.finishChildBuffer()
            return SynResult.success(SynType.Slice(elem))
        }
        if (input.peek(ParenPeek)) {
            val parens = parenthesized(input)
            if (parens.isFailure) return parens.asFailure()
            val parensVal = parens.getOrThrow()
            val content = parensVal.content
            val elems = SynTypeList()
            while (!content.isEmpty()) {
                val t = content.call { parseTypeFull(it) }
                if (t.isFailure) return t.asFailure()
                elems.pushValue(t.getOrThrow())
                if (content.isEmpty()) break
                val c = content.parse(CommaParse)
                if (c.isFailure) break
                elems.pushPunct(c.getOrThrow())
            }
            content.finishChildBuffer()
            if (elems.size == 1 && !elems.trailingPunct()) {
                return SynResult.success(SynType.Paren(parensVal.token, elems.first()!!))
            }
            return SynResult.success(SynType.Tuple(parensVal.token, elems))
        }
        val bareFnAhead = input.fork()
        val lifetimes =
            if (bareFnAhead.peek(ForPeek)) {
                parseBoundLifetimes(bareFnAhead).getOrElse { return SynResult.failure(it) }
            } else {
                null
            }
        if (bareFnAhead.peek(FnPeek) || bareFnAhead.peek(UnsafePeek) || bareFnAhead.peek(ExternPeek)) {
            input.advanceTo(bareFnAhead)
            return parseBareFnType(input, lifetimes).getOrElse { return SynResult.failure(it) }.let {
                SynResult.success(it)
            }
        }
        if (input.peek(FnPeek) || input.peek(UnsafePeek) || input.peek(ExternPeek)) {
            return parseBareFnType(input, null).getOrElse { return SynResult.failure(it) }.let {
                SynResult.success(it)
            }
        }
        val traitObjectAhead = input.fork()
        val traitObjectBounds = parseTypeParamBounds(traitObjectAhead, stopAtEq = false, allowPlus = allowPlus)
        if (traitObjectBounds.isSuccess) {
            val bounds = traitObjectBounds.getOrThrow()
            if (!bounds.isEmpty() && (bounds.size > 1 || bounds.trailingPunct())) {
                input.advanceTo(traitObjectAhead)
                return SynResult.success(SynType.TraitObject(null, bounds))
            }
        }
        if (input.peek(IdentPeek) ||
            input.peek(PathSepPeek) ||
            input.peek(SelfTypePeek) ||
            input.peek(SuperPeek) ||
            input.peek(CratePeek)
        ) {
            val pathResult = input.parse(PathParse)
            if (pathResult.isFailure) return pathResult.asFailure()
            return SynResult.success(SynType.Path(null, pathResult.getOrThrow()))
        }
        return SynResult.failure(input.error("expected a type"))
}

private fun parseBareFnType(
    input: ParseStream,
    lifetimes: BoundLifetimes?,
): SynResult<SynType.BareFn> {
    val unsafety = input.parse(UnsafeParse).getOrNull()
    val abi =
        if (input.peek(ExternPeek)) {
            parseAbi(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    val fnToken = input.parse(FnParse).getOrElse { return SynResult.failure(it) }
    val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    val inputs = BareFnArgList()
    var variadic: BareVariadic? = null
    val args = parens.content
    while (!args.isEmpty()) {
        val attrs = parseOuterAttributes(args).getOrElse { return SynResult.failure(it) }
        if (inputs.emptyOrTrailing() &&
            (args.peek(DotDotDotPeek) ||
                (args.peek(IdentPeekAny) || args.peek(UnderscorePeek)) &&
                args.peek2(ColonPeek) &&
                args.peek3(DotDotDotPeek))
        ) {
            variadic = parseBareVariadic(args, attrs).getOrElse { return SynResult.failure(it) }
            break
        }
        val arg = parseBareFnArg(args, attrs, allowSelf = inputs.isEmpty()).getOrElse {
            return SynResult.failure(it)
        }
        inputs.pushValue(arg)
        if (args.isEmpty()) break
        val comma = args.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        inputs.pushPunct(comma)
    }
    args.finishChildBuffer()
    val output = parseReturnTypeWithoutPlus(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(SynType.BareFn(lifetimes, unsafety, abi, fnToken, parens.token, inputs, variadic, output))
}

private fun parseBareFnArg(
    input: ParseStream,
    attrs: List<Attribute>,
    allowSelf: Boolean,
): SynResult<BareFnArg> {
    val begin = input.fork()
    val hasMutSelf = allowSelf && input.peek(MutPeek) && input.peek2(SelfValuePeek)
    if (hasMutSelf) {
        input.parse(MutParse).getOrElse { return SynResult.failure(it) }
    }

    val selfAtHead = allowSelf && input.peek(SelfValuePeek)
    var hasSelf = false
    var name =
        if ((input.peek(IdentPeekAny) || input.peek(UnderscorePeek) || selfAtHead) &&
            input.peek2(ColonPeek) &&
            !input.peek2(PathSepPeek)
        ) {
            hasSelf = selfAtHead
            val ident = parseBareFnName(input).getOrElse { return SynResult.failure(it) }
            val colon = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
            IdentColon(ident, colon)
        } else {
            null
        }

    val parsedTy =
        if (allowSelf && !hasSelf && input.peek(MutPeek) && input.peek2(SelfValuePeek)) {
            input.parse(MutParse).getOrElse { return SynResult.failure(it) }
            input.parse(SelfValueParse).getOrElse { return SynResult.failure(it) }
            null
        } else if (hasMutSelf && name == null) {
            input.parse(SelfValueParse).getOrElse { return SynResult.failure(it) }
            null
        } else {
            parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        }

    val ty =
        if (parsedTy != null && !hasMutSelf) {
            parsedTy
        } else {
            name = null
            SynType.Verbatim(between(begin, input))
        }
    return SynResult.success(BareFnArg(attrs, name, ty))
}

private fun parseBareVariadic(
    input: ParseStream,
    attrs: List<Attribute>,
): SynResult<BareVariadic> {
    val name =
        if (input.peek(IdentPeekAny) || input.peek(UnderscorePeek)) {
            val ident = parseBareFnName(input).getOrElse { return SynResult.failure(it) }
            val colon = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
            IdentColon(ident, colon)
        } else {
            null
        }
    val dots = input.parse(DotDotDotParse).getOrElse { return SynResult.failure(it) }
    val comma = input.parse(CommaParse).getOrNull()
    return SynResult.success(BareVariadic(attrs, name, dots, comma))
}

private fun parseBareFnName(input: ParseStream): SynResult<Ident> {
    if (input.peek(UnderscorePeek)) {
        val underscore = input.parse(UnderscoreParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(from(underscore))
    }
    return identParseAny(input)
}

private fun parsePathRest(input: ParseStream, path: Path): SynResult<Unit> {
    while (input.peek(PathSepPeek)) {
        val sep = input.parse(PathSepParse).getOrElse { return SynResult.failure(it) }
        path.segments.pushPunct(sep)
        val segment = input.parse(PathSegmentParse).getOrElse { return SynResult.failure(it) }
        path.segments.pushValue(segment)
    }
    return SynResult.success(Unit)
}
