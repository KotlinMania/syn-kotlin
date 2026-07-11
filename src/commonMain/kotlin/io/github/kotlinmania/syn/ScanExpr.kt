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
    val result = parseExprFull(input)
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
        is Expr.BlockExpr,
        -> false
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
        val opResult = BinOpParse.parse(ahead)
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
            current = Expr.Binary(mutableListOf(), current, op, rhsResult.getOrThrow())
        } else if (Precedence.Assign.ordinal >= base.ordinal &&
            input.peek(EqPeek) &&
            !input.peek(FatArrowPeek) &&
            current !is Expr.Range
        ) {
            val eqResult = EqParse.parse(input)
            if (eqResult.isFailure) return eqResult.asFailure()
            val rhsResult = parseBinopRhsImpl(input, allowStruct, Precedence.Assign)
            if (rhsResult.isFailure) return rhsResult
            current = Expr.Assign(mutableListOf(), current, eqResult.getOrThrow(), rhsResult.getOrThrow())
        } else if (Precedence.Range.ordinal >= base.ordinal &&
            (input.peek(DotDotPeek) || input.peek(DotDotEqPeek))
        ) {
            val limitsResult = parseRangeLimits(input)
            if (limitsResult.isFailure) return limitsResult.asFailure()
            val endResult = parseRangeEnd(input, limitsResult.getOrThrow(), allowStruct)
            if (endResult.isFailure) return endResult.asFailure()
            current = Expr.Range(mutableListOf(), current, limitsResult.getOrThrow(), endResult.getOrThrow())
        } else if (Precedence.Cast.ordinal >= base.ordinal && input.peek(AsPeek)) {
            val asResult = AsParse.parse(input)
            if (asResult.isFailure) return asResult.asFailure()
            val tyResult = parseTypeWithoutPlus(input, allowGroupGeneric = false)
            if (tyResult.isFailure) return tyResult.asFailure()
            val castCheck = checkCastImpl(input)
            if (castCheck.isFailure) return castCheck.asFailure()
            current = Expr.Cast(mutableListOf(), current, asResult.getOrThrow(), tyResult.getOrThrow())
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
    val op = BinOpParse.parse(input.fork())
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

internal fun exprAttrsImpl(input: ParseStream): SynResult<MutableList<Attribute>> {
    val attrs = mutableListOf<Attribute>()
    while (!startsWithNoneGroup(input) && input.peek(PoundPeek) && !input.peek2(NotPeek)) {
        attrs.add(AttributeParse.parse(input).getOrElse { return SynResult.failure(it) })
    }
    return SynResult.success(attrs)
}

internal fun unaryExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val attrs = exprAttrsImpl(input).getOrElse { return SynResult.failure(it) }
    if (peekExprGroup(input, allowStruct)) {
        return trailerExprImpl(input, allowStruct, attrs)
    }
    if (input.peek(AndPeek)) {
        val andToken = AndParse.parse(input)
        if (andToken.isFailure) return andToken.asFailure()
        if (input.peek(RawPeek) && (input.peek2(MutPeek) || input.peek2(ConstPeek))) {
            val rawToken = RawParse.parse(input)
            if (rawToken.isFailure) return rawToken.asFailure()
            val mutResult = MutParse.parse(input)
            val mutability =
                if (mutResult.isSuccess) {
                    PointerMutability.Mut(mutResult.getOrThrow())
                } else {
                    val constResult = ConstParse.parse(input)
                    if (constResult.isFailure) return constResult.asFailure()
                    PointerMutability.Const(constResult.getOrThrow())
                }
            val inner = unaryExprImpl(input, allowStruct)
            if (inner.isFailure) return inner
            return SynResult.success(
                Expr.RawAddr(attrs, andToken.getOrThrow(), rawToken.getOrThrow(), mutability, inner.getOrThrow()),
            )
        }
        val mutResult = MutParse.parse(input)
        val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
        val inner = unaryExprImpl(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Reference(attrs, andToken.getOrThrow(), mutability, inner.getOrThrow()))
    }
    if (input.peek(NotPeek) || input.peek(StarPeek) || input.peek(MinusPeek)) {
        val opResult = UnOpParse.parse(input)
        if (opResult.isFailure) return opResult.asFailure()
        val inner = unaryExprImpl(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Unary(attrs, opResult.getOrThrow(), inner.getOrThrow()))
    }
    return trailerExprImpl(input, allowStruct, attrs)
}

internal fun trailerExprImpl(input: ParseStream, allowStruct: Boolean, attrs: List<Attribute> = mutableListOf()): SynResult<Expr> {
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
        is Expr.Array -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Assign -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Async -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Await -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Binary -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.BlockExpr -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Break -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Call -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Cast -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Closure -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Const -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Continue -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Field -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.ForLoop -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Group -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.If -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Index -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Infer -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Let -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Lit -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Loop -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Macro -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Match -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.MethodCall -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Paren -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Path -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Range -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.RawAddr -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Reference -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Repeat -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Return -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Struct -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Try -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.TryBlock -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Tuple -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Unary -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Unsafe -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.While -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Yield -> copy(attrs = (attrs + this.attrs).toMutableList())
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
                val argResult = parseExprFull(content)
                if (argResult.isFailure) return argResult
                args.pushValue(argResult.getOrThrow())
                if (content.isEmpty()) break
                val commaResult = CommaParse.parse(content)
                if (commaResult.isFailure) break
                args.pushPunct(commaResult.getOrThrow())
            }
            content.finishChildBuffer()
            current = Expr.Call(mutableListOf(), current, paren, args)
        } else if (input.peek(DotPeek) && !input.peek2(DotDotPeek) && current !is Expr.Range) {
            val dotResult = DotParse.parse(input)
            if (dotResult.isFailure) return dotResult.asFailure()
            val dotToken = dotResult.getOrThrow()
            val floatAhead = input.fork()
            val floatResult = LitFloatParse.parse(floatAhead)
            if (floatResult.isSuccess) {
                input.advanceTo(floatAhead)
                val multi = multiIndexImpl(current, dotToken, floatResult.getOrThrow()).getOrElse { return SynResult.failure(it) }
                current = multi.expr
                if (multi.complete) {
                    continue
                }
            }
            if (input.peek(AwaitPeek)) {
                val awaitResult = AwaitParse.parse(input)
                if (awaitResult.isFailure) return awaitResult.asFailure()
                current = Expr.Await(mutableListOf(), current, dotToken, awaitResult.getOrThrow())
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
                    val argResult = parseExprFull(content)
                    if (argResult.isFailure) return argResult
                    args.pushValue(argResult.getOrThrow())
                    if (content.isEmpty()) break
                    val commaResult = CommaParse.parse(content)
                    if (commaResult.isFailure) break
                    args.pushPunct(commaResult.getOrThrow())
                }
                content.finishChildBuffer()
                current = Expr.MethodCall(mutableListOf(), current, dotToken, member.ident, null, paren, args)
                continue
            }
            current = Expr.Field(mutableListOf(), current, dotToken, member)
        } else if (input.peek(BracketPeek)) {
            val brackets = bracketed(input)
            if (brackets.isFailure) return brackets.asFailure()
            val bracketsVal = brackets.getOrThrow()
            val bracket = bracketsVal.token
            val content = bracketsVal.content
            val indexResult = parseExprFull(content)
            if (indexResult.isFailure) return indexResult
            content.finishChildBuffer()
            current = Expr.Index(mutableListOf(), current, bracket, indexResult.getOrThrow())
        } else if (input.peek(QuestionPeek) && current !is Expr.Range) {
            val qResult = QuestionParse.parse(input)
            if (qResult.isFailure) return qResult.asFailure()
            current = Expr.Try(mutableListOf(), current, qResult.getOrThrow())
        } else {
            break
        }
    }
    return SynResult.success(current)
}

internal fun parseMemberImpl(input: ParseStream): SynResult<Member> {
    if (input.peek(IdentPeek)) {
        val identResult = IdentParse.parse(input)
        if (identResult.isFailure) return identResult.asFailure()
        return SynResult.success(Member.Named(identResult.getOrThrow()))
    }
    val ahead = input.fork()
    val litResult = LitIntParse.parse(ahead)
    if (litResult.isSuccess) {
        val lit = litResult.getOrThrow()
        val index = parseIndex(lit).getOrElse { return SynResult.failure(it) }
        input.advanceTo(ahead)
        return SynResult.success(Member.Unnamed(index))
    }
    return SynResult.failure(input.error("expected field name or index"))
}

public data class MultiIndexResult(
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
        current = Expr.Field(mutableListOf(), current, nextDot, Member.Unnamed(index))
        nextDot =
            io.github.kotlinmania.syn.token.Dot
                .from(floatToken.subspan(partEnd..partEnd) ?: floatSpan)
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
    val idx =
        digits.toUIntOrNull()
            ?: return SynResult.failure(SynError.new(span, "expected unsuffixed integer"))
    return SynResult.success(Index(idx, span))
}

private fun parseIndex(lit: LitInt): SynResult<Index> {
    val digits = lit.base10Digits()
    val idx =
        digits.toUIntOrNull()
            ?: return SynResult.failure(SynError.new(lit.span(), "expected unsuffixed integer"))
    return SynResult.success(Index(idx, lit.span()))
}

internal fun atomExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    if (peekExprGroup(input, allowStruct)) {
        return parseExprGroupImpl(input)
    }
    if (input.peek(LitPeek)) {
        val lit = LitParse.parse(input)
        if (lit.isSuccess) return SynResult.success(Expr.Lit(mutableListOf(), lit.getOrThrow()))
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
        val retToken = ReturnParse.parse(input)
        if (retToken.isFailure) return retToken.asFailure()
        val expr =
            if (peekExprStart(input, allowStruct = true)) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Return(mutableListOf(), retToken.getOrThrow(), expr.getOrThrow()))
    }
    if (input.peek(LetPeek)) return parseExprLetImpl(input, allowStruct)
    if (input.peek(BreakPeek)) {
        val brkToken = BreakParse.parse(input)
        if (brkToken.isFailure) return brkToken.asFailure()
        val labelResult = LifetimeParse.parse(input)
        val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
        val expr =
            if (peekExprStart(input, allowStruct = true) && (allowStruct || !input.peek(BracePeek))) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Break(mutableListOf(), brkToken.getOrThrow(), label, expr.getOrThrow()))
    }
    if (input.peek(ContinuePeek)) {
        val contToken = ContinueParse.parse(input)
        if (contToken.isFailure) return contToken.asFailure()
        val labelResult = LifetimeParse.parse(input)
        val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
        return SynResult.success(Expr.Continue(mutableListOf(), contToken.getOrThrow(), label))
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
    val letToken = LetParse.parse(input)
    if (letToken.isFailure) return letToken.asFailure()
    var pat = parsePatFull(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(ColonPeek)) {
        val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        pat = Pat.TypeAscription(mutableListOf(), pat, colonToken, ty)
    }
    val eqToken = EqParse.parse(input)
    if (eqToken.isFailure) return eqToken.asFailure()
    val lhs = unaryExprImpl(input, allowStruct)
    if (lhs.isFailure) return lhs.asFailure()
    val expr = parseExprBinaryImpl(input, lhs.getOrThrow(), allowStruct, Precedence.Compare)
    if (expr.isFailure) return expr.asFailure()
    return SynResult.success(Expr.Let(mutableListOf(), letToken.getOrThrow(), pat, eqToken.getOrThrow(), expr.getOrThrow()))
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
    val expr = parseExprFull(group.content).getOrElse { return SynResult.failure(it) }
    group.content.finishChildBuffer()
    return SynResult.success(Expr.Group(mutableListOf(), group.token, expr))
}

private fun parseLabeledLoopOrWhile(input: ParseStream): SynResult<Expr> {
    val ahead = input.fork()
    val lifetime = LifetimeParse.parse(ahead).getOrElse { return SynResult.failure(it) }
    val colon = ColonParse.parse(ahead).getOrElse { return SynResult.failure(it) }
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
    val qpathResult = qpath(input, exprStyle = true)
    if (qpathResult.isFailure) return qpathResult.asFailure()
    val (qself, path) = qpathResult.getOrThrow()
    if (qself == null && input.peek(NotPeek) && path.getIdent() != null) {
        val bangResult = NotParse.parse(input)
        if (bangResult.isFailure) return bangResult.asFailure()
        val delimResult = parseDelimiter(input)
        if (delimResult.isFailure) return delimResult.asFailure()
        val delimPair = delimResult.getOrThrow()
        return SynResult.success(
            Expr.Macro(
                mutableListOf(),
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
            val fieldResult = parseFieldValue(content)
            if (fieldResult.isFailure) return fieldResult.asFailure()
            fields.pushValue(fieldResult.getOrThrow())
            if (content.isEmpty()) break
            val commaResult = CommaParse.parse(content)
            if (commaResult.isFailure) break
            fields.pushPunct(commaResult.getOrThrow())
        }
        content.finishChildBuffer()
        return SynResult.success(Expr.Struct(mutableListOf(), qself, path, brace, fields, null, null))
    }
    return SynResult.success(Expr.Path(mutableListOf(), qself, path))
}

internal fun parseFieldValueImpl(input: ParseStream): SynResult<FieldValue> {
    val identResult = IdentParse.parse(input)
    if (identResult.isFailure) {
        return identResult.map {
            FieldValue(mutableListOf(), Member.Named(it), null, Expr.Path(mutableListOf(), null, Path.from(it)))
        }
    }
    val ident = identResult.getOrThrow()
    if (input.peek(ColonPeek)) {
        val colonResult = ColonParse.parse(input)
        if (colonResult.isFailure) {
            return colonResult.map {
                FieldValue(mutableListOf(), Member.Named(ident), it, Expr.Path(mutableListOf(), null, Path.from(ident)))
            }
        }
        val exprResult = parseExprFull(input)
        if (exprResult.isFailure) {
            return exprResult.map {
                FieldValue(mutableListOf(), Member.Named(ident), colonResult.getOrThrow(), it)
            }
        }
        return SynResult.success(
            FieldValue(mutableListOf(), Member.Named(ident), colonResult.getOrThrow(), exprResult.getOrThrow()),
        )
    }
    return SynResult.success(
        FieldValue(mutableListOf(), Member.Named(ident), null, Expr.Path(mutableListOf(), null, Path.from(ident))),
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
        return SynResult.success(Expr.Tuple(mutableListOf(), paren, ExprList()))
    }
    val first = parseExprFull(content)
    if (first.isFailure) return first
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Paren(mutableListOf(), paren, first.getOrThrow()))
    }
    val elems = ExprList()
    elems.pushValue(first.getOrThrow())
    while (!content.isEmpty()) {
        val commaResult = CommaParse.parse(content)
        if (commaResult.isFailure) break
        elems.pushPunct(commaResult.getOrThrow())
        if (content.isEmpty()) break
        val valResult = parseExprFull(content)
        if (valResult.isFailure) return valResult
        elems.pushValue(valResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Tuple(mutableListOf(), paren, elems))
}

internal fun arrayOrRepeatImpl(input: ParseStream): SynResult<Expr> {
    val brackets = bracketed(input)
    if (brackets.isFailure) return brackets.asFailure()
    val bracketsVal = brackets.getOrThrow()
    val bracket = bracketsVal.token
    val content = bracketsVal.content
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Array(mutableListOf(), bracket, ExprList()))
    }
    val first = parseExprFull(content)
    if (first.isFailure) return first
    if (content.isEmpty() || content.peek(CommaPeek)) {
        val elems = ExprList()
        elems.pushValue(first.getOrThrow())
        while (!content.isEmpty()) {
            val commaResult = CommaParse.parse(content)
            if (commaResult.isFailure) break
            elems.pushPunct(commaResult.getOrThrow())
            if (content.isEmpty()) break
            val valResult = parseExprFull(content)
            if (valResult.isFailure) return valResult
            elems.pushValue(valResult.getOrThrow())
        }
        content.finishChildBuffer()
        return SynResult.success(Expr.Array(mutableListOf(), bracket, elems))
    }
    if (content.peek(SemiPeek)) {
        val semiResult = SemiParse.parse(content)
        if (semiResult.isFailure) return semiResult.asFailure()
        val lenResult = parseExprFull(content)
        if (lenResult.isFailure) return lenResult
        content.finishChildBuffer()
        return SynResult.success(Expr.Repeat(mutableListOf(), bracket, first.getOrThrow(), semiResult.getOrThrow(), lenResult.getOrThrow()))
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
    return SynResult.success(Expr.Range(mutableListOf(), start, limits, endResult.getOrThrow()))
}

private fun parseRangeLimits(input: ParseStream): SynResult<RangeLimits> =
    when {
        input.peek(DotDotEqPeek) -> {
            val dotDotEqResult = DotDotEqParse.parse(input)
            if (dotDotEqResult.isFailure) dotDotEqResult.asFailure() else SynResult.success(RangeLimits.Closed(dotDotEqResult.getOrThrow()))
        }
        input.peek(DotDotPeek) -> {
            val dotDotResult = DotDotParse.parse(input)
            if (dotDotResult.isFailure) dotDotResult.asFailure() else SynResult.success(RangeLimits.HalfOpen(dotDotResult.getOrThrow()))
        }
        else -> SynResult.failure(input.error("expected range limits"))
    }

private fun parseExprIf(input: ParseStream): SynResult<Expr> {
    val ifToken = IfParse.parse(input).getOrThrow()
    val cond = ambiguousExprImpl(input, allowStruct = false)
    if (cond.isFailure) return cond
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val stmtResult = parseStmtFull(content)
        if (stmtResult.isFailure) break
        stmts.add(stmtResult.getOrThrow())
    }
    content.finishChildBuffer()
    val thenBranch = Block(brace, stmts)
    var elseBranch: ElseExpr? = null
    if (input.peek(ElsePeek)) {
        val elseToken = ElseParse.parse(input).getOrThrow()
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
                val s = parseStmtFull(eContent)
                if (s.isFailure) break
                eStmts.add(s.getOrThrow())
            }
            eContent.finishChildBuffer()
            elseBranch = ElseExpr(elseToken, Expr.BlockExpr(mutableListOf(), null, Block(eBrace, eStmts)))
        }
    }
    return SynResult.success(Expr.If(mutableListOf(), ifToken, cond.getOrThrow(), thenBranch, elseBranch))
}

internal fun parseExprWhile(input: ParseStream, label: Label? = null): SynResult<Expr> {
    val whileToken = WhileParse.parse(input).getOrThrow()
    val cond = ambiguousExprImpl(input, allowStruct = false)
    if (cond.isFailure) return cond
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = parseStmtFull(content)
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.While(mutableListOf(), label, whileToken, cond.getOrThrow(), Block(brace, stmts)))
}

internal fun parseExprLoop(input: ParseStream, label: Label? = null): SynResult<Expr> {
    val loopToken = LoopParse.parse(input).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = parseStmtFull(content)
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Loop(mutableListOf(), label, loopToken, Block(brace, stmts)))
}

private fun parseExprMatch(input: ParseStream): SynResult<Expr> {
    val matchToken = MatchParse.parse(input).getOrThrow()
    val scrutinee = ambiguousExprImpl(input, allowStruct = false)
    if (scrutinee.isFailure) return scrutinee
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val arms = mutableListOf<Arm>()
    while (!content.isEmpty()) {
        val armResult = parseMatchArm(content)
        if (armResult.isFailure) break
        arms.add(armResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Match(mutableListOf(), matchToken, scrutinee.getOrThrow(), brace, arms))
}

private fun parseMatchArm(input: ParseStream): SynResult<Arm> {
    val patResult = parsePatFull(input)
    if (patResult.isFailure) {
        return patResult.map { Arm(mutableListOf(), it, null, fatArrowSentinel(input), inferSentinel(input), null) }
    }
    val fatArrowResult = FatArrowParse.parse(input)
    if (fatArrowResult.isFailure) {
        return fatArrowResult.map {
            Arm(mutableListOf(), patResult.getOrThrow(), null, it, inferSentinel(input), null)
        }
    }
    val bodyResult = parseExprFull(input)
    if (bodyResult.isFailure) {
        return bodyResult.map {
            Arm(mutableListOf(), patResult.getOrThrow(), null, fatArrowResult.getOrThrow(), it, null)
        }
    }
    val commaResult = CommaParse.parse(input)
    val comma = if (commaResult.isSuccess) commaResult.getOrThrow() else null
    return SynResult.success(
        Arm(mutableListOf(), patResult.getOrThrow(), null, fatArrowResult.getOrThrow(), bodyResult.getOrThrow(), comma),
    )
}

private fun fatArrowSentinel(input: ParseStream): io.github.kotlinmania.syn.token.FatArrow =
    io.github.kotlinmania.syn.token.FatArrow
        .from(input.span())

private fun inferSentinel(input: ParseStream): Expr =
    Expr.Infer(
        mutableListOf(),
        io.github.kotlinmania.syn.token.Underscore
            .from(input.span()),
    )

internal fun inferSentinelType(input: ParseStream): SynType =
    SynType.Infer(
        io.github.kotlinmania.syn.token.Underscore
            .from(input.span()),
    )

private fun parseExprAsync(input: ParseStream): SynResult<Expr> {
    val asyncToken = AsyncParse.parse(input).getOrThrow()
    val moveResult = MoveParse.parse(input)
    val capture = if (moveResult.isSuccess) moveResult.getOrThrow() else null
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = parseStmtFull(content)
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Async(mutableListOf(), asyncToken, capture, Block(brace, stmts)))
}

private fun parseExprUnsafe(input: ParseStream): SynResult<Expr> {
    val unsafeToken = UnsafeParse.parse(input).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = parseStmtFull(content)
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Unsafe(mutableListOf(), unsafeToken, Block(brace, stmts)))
}

private fun parseExprConst(input: ParseStream): SynResult<Expr> {
    val constToken = ConstParse.parse(input).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = parseStmtFull(content)
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Const(mutableListOf(), constToken, Block(brace, stmts)))
}

internal fun parseExprBlock(input: ParseStream): SynResult<Expr> {
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmts = mutableListOf<Stmt>()
    while (!content.isEmpty()) {
        val s = parseStmtFull(content)
        if (s.isFailure) break
        stmts.add(s.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.BlockExpr(mutableListOf(), null, Block(brace, stmts)))
}

private fun parseExprClosure(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val constnessResult = ConstParse.parse(input)
    val constness = if (constnessResult.isSuccess) constnessResult.getOrThrow() else null
    val asyncnessResult = AsyncParse.parse(input)
    val asyncness = if (asyncnessResult.isSuccess) asyncnessResult.getOrThrow() else null
    val moveResult = MoveParse.parse(input)
    val capture = if (moveResult.isSuccess) moveResult.getOrThrow() else null
    val or1Result = parseClosureOr(input)
    if (or1Result.isFailure) return or1Result.asFailure()
    val inputs = PatList()
    while (!input.isEmpty() && !input.peek(OrPeek)) {
        val patResult = parsePatFull(input)
        if (patResult.isFailure) return patResult.asFailure()
        inputs.pushValue(patResult.getOrThrow())
        if (input.isEmpty()) break
        val commaResult = CommaParse.parse(input)
        if (commaResult.isFailure) break
        inputs.pushPunct(commaResult.getOrThrow())
    }
    val or2Result = parseClosureOr(input)
    if (or2Result.isFailure) return or2Result.asFailure()
    var output: ReturnType = ReturnType.Default
    if (input.peek(RArrowPeek)) {
        val arrowResult = RArrowParse.parse(input)
        if (arrowResult.isFailure) return arrowResult.asFailure()
        val tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return tyResult.asFailure()
        output = ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow())
        val bodyResult = parseExprBlock(input)
        if (bodyResult.isFailure) return bodyResult
        return SynResult.success(Expr.Closure(mutableListOf(), constness, asyncness, capture, or1Result.getOrThrow(), inputs, or2Result.getOrThrow(), output, bodyResult.getOrThrow()))
    }
    val bodyResult = ambiguousExprImpl(input, allowStruct)
    if (bodyResult.isFailure) return bodyResult
    return SynResult.success(Expr.Closure(mutableListOf(), constness, asyncness, capture, or1Result.getOrThrow(), inputs, or2Result.getOrThrow(), output, bodyResult.getOrThrow()))
}

private fun parseClosureOr(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Or> =
    input.step { cursor ->
        val (punct, rest) =
            cursor.punct()
                ?: return@step SynResult.failure(cursor.error("expected `|`"))
        if (punct.asChar() != '|') {
            return@step SynResult.failure(cursor.error("expected `|`"))
        }
        SynResult.success(
            io.github.kotlinmania.syn.token.Or
                .from(punct.span()) to rest,
        )
    }

internal fun parseStmtFull(input: ParseStream): SynResult<Stmt> {
    if (input.peek(LetPeek) && !startsWithNoneGroup(input)) {
        val letToken = LetParse.parse(input).getOrThrow()
        var pat = parsePatFull(input).getOrElse { return SynResult.failure(it) }
        if (input.peek(ColonPeek)) {
            val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            pat = Pat.TypeAscription(mutableListOf(), pat, colonToken, ty)
        }
        val init: LocalInit? =
            if (input.peek(EqPeek)) {
                val eq = EqParse.parse(input).getOrThrow()
                val e = parseExprFull(input)
                if (e.isFailure) {
                    null
                } else {
                    val diverge =
                        if (input.peek(ElsePeek)) {
                            val elseToken = ElseParse.parse(input).getOrThrow()
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
        val semi = SemiParse.parse(input).getOrThrow()
        return SynResult.success(Stmt.Local(mutableListOf(), letToken, pat, init, semi))
    }
    if (peekItemStatement(input)) {
        val item = ItemParse.parse(input)
        if (item.isFailure) return item.asFailure()
        return SynResult.success(Stmt.ItemStmt(item.getOrThrow()))
    }
    val exprResult = parseExprWithEarlierBoundaryRule(input)
    if (exprResult.isFailure) return exprResult.asFailure()
    if (input.peek(SemiPeek)) {
        val semi = SemiParse.parse(input).getOrThrow()
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
    ambigTyImpl(input, allowPlus = true, allowGroupGeneric = true)

internal fun parseTypeWithoutPlus(
    input: ParseStream,
    allowGroupGeneric: Boolean = true,
): SynResult<SynType> =
    ambigTyImpl(input, allowPlus = false, allowGroupGeneric = allowGroupGeneric)

internal fun ambigTyWrapper(
    input: ParseStream,
    allowPlus: Boolean,
    allowGroupGeneric: Boolean,
): SynResult<SynType> = ambigTyImpl(input, allowPlus, allowGroupGeneric)

internal object PatParseImpl {
    fun parse(input: ParseStream): SynResult<Pat> = parsePatSingle(input)
}

internal fun parsePatSingle(input: ParseStream): SynResult<Pat> {
    if (input.peek(UnderscorePeek)) {
        val underscore = UnderscoreParse.parse(input).getOrThrow()
        return SynResult.success(Pat.Wild(mutableListOf(), underscore))
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
            OrParse.parse(input).getOrElse { return SynResult.failure(it) }
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
            cases.pushPunct(OrParse.parse(input).getOrElse { return SynResult.failure(it) })
            cases.pushValue(parsePatSingle(input).getOrElse { return SynResult.failure(it) })
        }
        pat = Pat.Or(leadingVert, cases)
    }
    return SynResult.success(pat)
}

private fun parsePatPathOrMacroOrStructOrRange(input: ParseStream): SynResult<Pat> {
    val path = parseModStylePath(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(NotPeek) && !input.peek(NePeek)) {
        val bangToken = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
        val delimiter = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Pat.Macro(mutableListOf(), Macro(path, bangToken, delimiter.first, delimiter.second)))
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
        return parsePatRange(input, Expr.Path(mutableListOf(), null, path))
    }
    val ident = path.getIdent()
    if (ident != null) {
        return SynResult.success(Pat.Ident(mutableListOf(), null, FieldMutability.None, ident, null, null))
    }
    return SynResult.success(Pat.Path(mutableListOf(), null, path))
}

private fun parsePatIdent(input: ParseStream): SynResult<Pat.Ident> {
    val byRef = RefParse.parse(input).getOrNull()
    val mutability =
        MutParse.parse(input).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
    val ident =
        if (input.peek(SelfValuePeek)) {
            identFromSelfValue(SelfValueParse.parse(input).getOrThrow())
        } else {
            IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    val atToken = AtParse.parse(input).getOrNull()
    val subpat =
        if (atToken != null) {
            parsePatSingle(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    return SynResult.success(Pat.Ident(mutableListOf(), byRef, mutability, ident, atToken, subpat))
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
        elems.pushPunct(CommaParse.parse(parens.content).getOrElse { return SynResult.failure(it) })
    }
    parens.content.finishChildBuffer()
    return SynResult.success(Pat.TupleStruct(mutableListOf(), null, path, parens.token, elems))
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
            rest = PatRest(DotDotParse.parse(braces.content).getOrElse { return SynResult.failure(it) })
            break
        }
        fields.pushValue(parseFieldPat(braces.content).getOrElse { return SynResult.failure(it) })
        if (braces.content.isEmpty()) break
        fields.pushPunct(CommaParse.parse(braces.content).getOrElse { return SynResult.failure(it) })
    }
    braces.content.finishChildBuffer()
    return SynResult.success(Pat.Struct(null, path, braces.token, fields, rest, null))
}

private fun parseFieldPat(input: ParseStream): SynResult<FieldPat> {
    val byRef = RefParse.parse(input).getOrNull()
    val mutability =
        MutParse.parse(input).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
    val member = parseMemberImpl(input).getOrElse { return SynResult.failure(it) }
    if ((byRef == null && mutability is FieldMutability.None && input.peek(ColonPeek)) || member is Member.Unnamed) {
        val colon = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val pat = parsePatMultiWithLeadingVert(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(FieldPat(member, colon, pat))
    }
    val ident =
        when (member) {
            is Member.Named -> member.ident
            is Member.Unnamed -> return SynResult.failure(input.error("expected named field"))
        }
    val pat = Pat.Ident(mutableListOf(), byRef, mutability, ident, null, null)
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
    return SynResult.success(Pat.Range(mutableListOf(), start, limits, end))
}

private fun parsePatRangeHalfOpen(input: ParseStream): SynResult<Pat> {
    val limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
    val end = parsePatRangeBound(input).getOrElse { return SynResult.failure(it) }
    if (end != null) {
        return SynResult.success(Pat.Range(mutableListOf(), null, limits, end))
    }
    return when (limits) {
        is RangeLimits.HalfOpen -> SynResult.success(Pat.Rest(mutableListOf(), limits.token))
        is RangeLimits.Closed -> SynResult.failure(input.error("expected range upper bound"))
    }
}

private fun parsePatLitOrRange(input: ParseStream): SynResult<Pat> {
    val start =
        parsePatRangeBound(input).getOrElse { return SynResult.failure(it) }
            ?: return SynResult.failure(input.error("expected range bound"))
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek) || input.peek(DotDotDotPeek)) {
        val limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
        val end = parsePatRangeBound(input).getOrElse { return SynResult.failure(it) }
        if (limits is RangeLimits.Closed && end == null) {
            return SynResult.failure(input.error("expected range upper bound"))
        }
        return SynResult.success(Pat.Range(mutableListOf(), start, limits, end))
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
        val dots = DotDotDotParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(
            RangeLimits.Closed(
                io.github.kotlinmania.syn.token.DotDotEq
                    .from(dots.spans),
            ),
        )
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
        val lit = LitParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Expr.Lit(mutableListOf(), lit))
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
        return SynResult.success(Expr.Path(mutableListOf(), null, path))
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
        elems.pushPunct(CommaParse.parse(content).getOrElse { return SynResult.failure(it) })
    }
    content.finishChildBuffer()
    return SynResult.success(Pat.Tuple(parens.token, elems))
}

private fun parsePatReference(input: ParseStream): SynResult<Pat.Reference> {
    val andToken = AndParse.parse(input).getOrElse { return SynResult.failure(it) }
    val mutability =
        MutParse.parse(input).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
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
        elems.pushPunct(CommaParse.parse(brackets.content).getOrElse { return SynResult.failure(it) })
    }
    brackets.content.finishChildBuffer()
    return SynResult.success(Pat.Slice(brackets.token, elems))
}

internal object SynTypeParseExpr {
    fun parse(input: ParseStream): SynResult<SynType> =
        ambigTyImpl(input, allowPlus = true, allowGroupGeneric = true)
}

private fun ambigTyImpl(
    input: ParseStream,
    allowPlus: Boolean,
    allowGroupGeneric: Boolean,
): SynResult<SynType> {
    val begin = input.fork()
    val groupAhead = input.fork()
    val groupResult = parseGroup(groupAhead)
    if (groupResult.isSuccess) {
        input.advanceTo(groupAhead)
        val group = groupResult.getOrThrow()
        val elem = parseTypeFull(group.content)
        if (elem.isFailure) return elem.asFailure()
        group.content.finishChildBuffer()
        val elemValue = elem.getOrThrow()
        if (elemValue is SynType.Path) {
            if (allowGroupGeneric && (input.peek(LtPeek) || (input.peek(PathSepPeek) && input.peek3(LtPeek)))) {
                val last = elemValue.path.segments.last()
                if (last != null && last.arguments.isNone()) {
                    last.arguments =
                        parseAngleBracketedPathArguments(input).getOrElse {
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
            val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
            val qself =
                QSelf(
                    io.github.kotlinmania.syn.token.Lt
                        .from(group.token.span),
                    elemValue,
                    position = 0,
                    asToken = null,
                    gtToken =
                        io.github.kotlinmania.syn.token.Gt
                            .from(group.token.span),
                )
            return SynResult.success(SynType.Path(qself, path))
        }
        return SynResult.success(SynType.Group(group.token, elemValue))
    }

    val lifetimes =
        if (input.peek(ForPeek)) {
            val parsed = parseBoundLifetimes(input).getOrElse { return SynResult.failure(it) }
            if (!canContinueAfterBoundLifetimes(input) || input.peek(DynPeek)) {
                return SynResult.failure(input.error("expected a type"))
            }
            parsed
        } else {
            null
        }

    if (input.peek(ParenPeek)) {
        val parens = parenthesized(input)
        if (parens.isFailure) return parens.asFailure()
        val parensVal = parens.getOrThrow()
        val content = parensVal.content
        if (content.isEmpty()) {
            content.finishChildBuffer()
            return SynResult.success(SynType.Tuple(parensVal.token, SynTypeList()))
        }
        if (content.peek(LifetimePeek)) {
            val traitObject = parseTypeTraitObject(content, allowPlus = true).getOrElse { return SynResult.failure(it) }
            content.finishChildBuffer()
            return SynResult.success(SynType.Paren(parensVal.token, traitObject))
        }
        if (content.peek(QuestionPeek)) {
            val bounds =
                parseTypeParamBoundsMultiple(
                    content,
                    allowPlus = true,
                    allowPreciseCapture = false,
                    allowConst = false,
                ).getOrElse { return SynResult.failure(it) }
            content.finishChildBuffer()
            val first = bounds.first()
            val traitBounds =
                if (first is TypeParamBound.Trait) {
                    withFirstTypeParamBound(bounds, first.copy(parenToken = parensVal.token))
                } else {
                    bounds
                }
            parseOuterTraitObjectBounds(input, traitBounds, allowPlus).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.TraitObject(null, traitBounds))
        }

        val first = ambigTyImpl(content, allowPlus = true, allowGroupGeneric = true).getOrElse { return SynResult.failure(it) }
        if (content.peek(CommaPeek)) {
            val elems = SynTypeList()
            elems.pushValue(first)
            elems.pushPunct(CommaParse.parse(content).getOrElse { return SynResult.failure(it) })
            while (!content.isEmpty()) {
                elems.pushValue(ambigTyImpl(content, allowPlus = true, allowGroupGeneric = true).getOrElse { return SynResult.failure(it) })
                if (content.isEmpty()) break
                elems.pushPunct(CommaParse.parse(content).getOrElse { return SynResult.failure(it) })
            }
            content.finishChildBuffer()
            return SynResult.success(SynType.Tuple(parensVal.token, elems))
        }
        content.finishChildBuffer()
        val firstBound = parenthesizedTypeAsBound(parensVal.token, first)
        if (allowPlus && firstBound != null && input.peek(PlusPeek)) {
            val bounds = TypeParamBoundList()
            bounds.pushValue(firstBound)
            parseOuterTraitObjectBounds(input, bounds, allowPlus).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.TraitObject(null, bounds))
        }
        return SynResult.success(SynType.Paren(parensVal.token, first))
    }

    if (input.peek(FnPeek) || input.peek(UnsafePeek) || input.peek(ExternPeek)) {
        return parseBareFnType(input, lifetimes).getOrElse { return SynResult.failure(it) }.let {
            SynResult.success(it)
        }
    }

    if (input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek) ||
        input.peek(LtPeek)
    ) {
        val qpathResult = qpath(input, exprStyle = false)
        if (qpathResult.isFailure) return qpathResult.asFailure()
        val (qself, path) = qpathResult.getOrThrow()
        if (qself != null) {
            return SynResult.success(SynType.Path(qself, path))
        }
        if (input.peek(NotPeek) && !input.peek(NePeek) && path.isModStyle()) {
            val bang = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
            val delimiter = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.Macro(Macro(path, bang, delimiter.first, delimiter.second)))
        }
        if (lifetimes != null || allowPlus && input.peek(PlusPeek)) {
            val bounds = TypeParamBoundList()
            bounds.pushValue(TypeParamBound.Trait(null, TraitBoundModifier.None, lifetimes, path))
            parseOuterTraitObjectBounds(input, bounds, allowPlus).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.TraitObject(null, bounds))
        }
        return SynResult.success(SynType.Path(null, path))
    }

    if (input.peek(DynPeek)) {
        val dynToken = DynParse.parse(input).getOrElse { return SynResult.failure(it) }
        val starToken = StarParse.parse(input).getOrNull()
        val bounds = parseTraitObjectBounds(input, allowPlus).getOrElse { return SynResult.failure(it) }
        return if (starToken != null) {
            SynResult.success(SynType.Verbatim(between(begin, input)))
        } else {
            SynResult.success(SynType.TraitObject(dynToken, bounds))
        }
    }

    if (input.peek(BracketPeek)) {
        val brackets = bracketed(input)
        if (brackets.isFailure) return brackets.asFailure()
        val bracketsVal = brackets.getOrThrow()
        val elem = parseTypeFull(bracketsVal.content).getOrElse { return SynResult.failure(it) }
        if (bracketsVal.content.peek(SemiPeek)) {
            SemiParse.parse(bracketsVal.content).getOrElse { return SynResult.failure(it) }
            val len = parseExprFull(bracketsVal.content).getOrElse { return SynResult.failure(it) }
            bracketsVal.content.finishChildBuffer()
            return SynResult.success(SynType.Array(elem, len))
        }
        bracketsVal.content.finishChildBuffer()
        return SynResult.success(SynType.Slice(elem))
    }

    if (input.peek(StarPeek)) {
        val starToken = StarParse.parse(input).getOrThrow()
        val constToken: io.github.kotlinmania.syn.token.Const?
        val mutability: io.github.kotlinmania.syn.token.Mut?
        when {
            input.peek(ConstPeek) -> {
                constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
                mutability = null
            }
            input.peek(MutPeek) -> {
                constToken = null
                mutability = MutParse.parse(input).getOrElse { return SynResult.failure(it) }
            }
            else -> return SynResult.failure(input.error("expected `const` or `mut`"))
        }
        val inner = parseTypeWithoutPlus(input)
        if (inner.isFailure) return inner.asFailure()
        return SynResult.success(SynType.Ptr(starToken, constToken, mutability, inner.getOrThrow()))
    }

    if (input.peek(AndPeek)) {
        val andToken = AndParse.parse(input).getOrThrow()
        val ltResult = LifetimeParse.parse(input)
        val lifetime = if (ltResult.isSuccess) ltResult.getOrThrow() else null
        val mutResult = MutParse.parse(input)
        val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
        val inner = parseTypeWithoutPlus(input)
        if (inner.isFailure) return inner.asFailure()
        return SynResult.success(SynType.Reference(andToken, lifetime, mutability, inner.getOrThrow()))
    }

    if (input.peek(NotPeek) && !input.peek(NePeek)) {
        val bang = NotParse.parse(input).getOrThrow()
        return SynResult.success(SynType.Never(bang))
    }

    if (input.peek(ImplPeek)) {
        return parseTypeImplTrait(input, allowPlus).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }

    if (input.peek(UnderscorePeek)) {
        val underscore = UnderscoreParse.parse(input).getOrThrow()
        return SynResult.success(SynType.Infer(underscore))
    }

    if (input.peek(LifetimePeek)) {
        return parseTypeTraitObject(input, allowPlus).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }

    return SynResult.failure(input.error("expected a type"))
}

private fun canContinueAfterBoundLifetimes(input: ParseStream): Boolean =
    input.peek(IdentPeekAny) ||
        input.peek(FnPeek) ||
        input.peek(UnsafePeek) ||
        input.peek(ExternPeek) ||
        input.peek(SuperPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(CratePeek)

private fun parenthesizedTypeAsBound(
    parenToken: io.github.kotlinmania.syn.token.Paren,
    ty: SynType,
): TypeParamBound? =
    when (ty) {
        is SynType.Path ->
            if (ty.qself == null) {
                TypeParamBound.Trait(parenToken, TraitBoundModifier.None, null, ty.path)
            } else {
                null
            }
        is SynType.TraitObject ->
            if (ty.dynToken == null && ty.bounds.size == 1 && !ty.bounds.trailingPunct()) {
                when (val bound = ty.bounds.first()) {
                    is TypeParamBound.Trait -> bound.copy(parenToken = parenToken)
                    is TypeParamBound.LifetimeBound -> bound
                    else -> null
                }
            } else {
                null
            }
        else -> null
    }

private fun parseOuterTraitObjectBounds(
    input: ParseStream,
    bounds: TypeParamBoundList,
    allowPlus: Boolean,
): SynResult<Unit> {
    while (allowPlus && input.peek(PlusPeek)) {
        bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
        if (!canStartTypeParamBound(input, allowConst = false)) break
        val rest =
            parseTypeParamBoundsMultiple(
                input,
                allowPlus = true,
                allowPreciseCapture = false,
                allowConst = false,
            ).getOrElse { return SynResult.failure(it) }
        appendTypeParamBounds(bounds, rest)
        break
    }
    return SynResult.success(Unit)
}

private fun parseTraitObjectBounds(
    input: ParseStream,
    allowPlus: Boolean,
): SynResult<TypeParamBoundList> {
    val dynSpan = input.span()
    return SynType.TraitObject.parseBounds(dynSpan, input, allowPlus)
}

private fun canStartTypeParamBound(
    input: ParseStream,
    allowConst: Boolean,
): Boolean =
    input.peek(IdentPeekAny) ||
        input.peek(PathSepPeek) ||
        input.peek(QuestionPeek) ||
        input.peek(LifetimePeek) ||
        input.peek(ParenPeek) ||
        allowConst &&
        (input.peek(BracketPeek) || input.peek(ConstPeek))

private fun appendTypeParamBounds(
    target: TypeParamBoundList,
    source: TypeParamBoundList,
) {
    for ((bound, punct) in source.pairsList()) {
        target.pushValue(bound as TypeParamBound)
        punct?.let { target.pushPunct(it) }
    }
}

private fun withFirstTypeParamBound(
    source: TypeParamBoundList,
    first: TypeParamBound,
): TypeParamBoundList {
    val replaced = TypeParamBoundList()
    for ((index, pair) in source.pairsList().withIndex()) {
        val (bound, punct) = pair
        replaced.pushValue(if (index == 0) first else bound as TypeParamBound)
        punct?.let { replaced.pushPunct(it) }
    }
    return replaced
}

internal fun parseTypeTraitObject(
    input: ParseStream,
    allowPlus: Boolean,
): SynResult<SynType.TraitObject> {
    val dynToken = DynParse.parse(input).getOrNull()
    val bounds = parseTraitObjectBounds(input, allowPlus).getOrElse { return SynResult.failure(it) }
    return SynResult.success(SynType.TraitObject(dynToken, bounds))
}

internal fun parseTypeImplTrait(
    input: ParseStream,
    allowPlus: Boolean,
): SynResult<SynType.ImplTrait> {
    val implToken = ImplParse.parse(input).getOrElse { return SynResult.failure(it) }
    val bounds =
        parseTypeParamBoundsMultiple(
            input,
            allowPlus = allowPlus,
            allowPreciseCapture = true,
            allowConst = true,
        ).getOrElse { return SynResult.failure(it) }
    if (!bounds.hasTraitLikeImplBound()) {
        return SynResult.failure(input.error("at least one trait must be specified"))
    }
    return SynResult.success(SynType.ImplTrait(implToken, bounds))
}

private fun TypeParamBoundList.hasTraitBound(): Boolean =
    toList().any { it is TypeParamBound.Trait }

private fun TypeParamBoundList.hasTraitLikeImplBound(): Boolean =
    toList().any { it is TypeParamBound.Trait || it is TypeParamBound.Verbatim }

private fun parseBareFnType(
    input: ParseStream,
    lifetimes: BoundLifetimes?,
): SynResult<SynType.BareFn> {
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val abi =
        if (input.peek(ExternPeek)) {
            parseAbi(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    val fnToken = FnParse.parse(input).getOrElse { return SynResult.failure(it) }
    val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    val inputs = BareFnArgList()
    var variadic: BareVariadic? = null
    val args = parens.content
    while (!args.isEmpty()) {
        val attrs = parseOuterAttributes(args).getOrElse { return SynResult.failure(it) }
        if (inputs.emptyOrTrailing() &&
            (
                args.peek(DotDotDotPeek) ||
                    (args.peek(IdentPeek) || args.peek(UnderscorePeek)) &&
                    args.peek2(ColonPeek) &&
                    args.peek3(DotDotDotPeek)
            )
        ) {
            variadic = parseBareVariadic(args, attrs).getOrElse { return SynResult.failure(it) }
            break
        }
        val arg =
            parseBareFnArg(args, attrs, allowSelf = inputs.isEmpty()).getOrElse {
                return SynResult.failure(it)
            }
        inputs.pushValue(arg)
        if (args.isEmpty()) break
        val comma = CommaParse.parse(args).getOrElse { return SynResult.failure(it) }
        inputs.pushPunct(comma)
    }
    args.finishChildBuffer()
    val output = parseReturnTypeWithoutPlus(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(SynType.BareFn(lifetimes, unsafety, abi, fnToken, parens.token, inputs, variadic, output))
}

private fun parsePathRest(input: ParseStream, path: Path): SynResult<Unit> {
    while (input.peek(PathSepPeek)) {
        val sep = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
        path.segments.pushPunct(sep)
        val segment = PathSegmentParse.parse(input).getOrElse { return SynResult.failure(it) }
        path.segments.pushValue(segment)
    }
    return SynResult.success(Unit)
}

internal enum class Input {
    Keyword,
    Punct,
    ConsumeAny,
    ConsumeBinOp,
    ConsumeBrace,
    ConsumeDelimiter,
    ConsumeIdent,
    ConsumeLifetime,
    ConsumeLiteral,
    ConsumeNestedBrace,
    ExpectPath,
    ExpectTurbofish,
    ExpectType,
    CanBeginExpr,
    Otherwise,
    Empty,
}

internal enum class Action {
    SetState,
    IncDepth,
    DecDepth,
    Finish,
}
