// port-lint: source scan_expr.rs
package io.github.kotlinmania.syn

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
    ambiguousExpr(input, allowStruct = true)

private fun ambiguousExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val lhs = unaryExpr(input, allowStruct)
    if (lhs.isFailure) return lhs
    return parseExprBinary(input, lhs.getOrThrow(), allowStruct, Precedence.MIN)
}

private fun parseExprBinary(
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
        if (opResult.isFailure) break
        val op = opResult.getOrThrow()
        val precedence = Precedence.ofBinop(op)
        if (precedence.ordinal < base.ordinal) break
        if (precedence == Precedence.Compare && current is Expr.Binary) {
            if (Precedence.ofBinop(current.op) == Precedence.Compare) {
                return SynResult.failure(input.error("comparison operators cannot be chained"))
            }
        }
        input.advanceTo(ahead)
        val rhsResult = parseBinopRhs(input, allowStruct, precedence)
        if (rhsResult.isFailure) return rhsResult
        current = Expr.Binary(emptyList(), current, op, rhsResult.getOrThrow())
    }
    return SynResult.success(current)
}

private fun parseBinopRhs(
    input: ParseStream,
    allowStruct: Boolean,
    left: Precedence,
): SynResult<Expr> {
    var rhs = unaryExpr(input, allowStruct)
    if (rhs.isFailure) return rhs
    var rhsExpr = rhs.getOrThrow()
    while (true) {
        if (rhsExpr is Expr.Range) break
        val ahead = input.fork()
        val nextOp = ahead.parse(BinOpParse)
        if (nextOp.isFailure) break
        val nextPrec = Precedence.ofBinop(nextOp.getOrThrow())
        if (nextPrec.ordinal <= left.ordinal) break
        input.advanceTo(ahead)
        val inner = parseBinopRhs(input, allowStruct, nextPrec)
        if (inner.isFailure) return inner
        rhsExpr = Expr.Binary(emptyList(), rhsExpr, nextOp.getOrThrow(), inner.getOrThrow())
    }
    return SynResult.success(rhsExpr)
}

private fun unaryExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    if (input.peek(AndPeek)) {
        val andToken = input.parse(AndParse)
        if (andToken.isFailure) return andToken.asFailure()
        val mutResult = input.parse(MutParse)
        val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
        val inner = unaryExpr(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Reference(emptyList(), andToken.getOrThrow(), mutability, inner.getOrThrow()))
    }
    if (input.peek(NotPeek) || input.peek(StarPeek) || input.peek(MinusPeek)) {
        val opResult = input.parse(UnOpParse)
        if (opResult.isFailure) return opResult.asFailure()
        val inner = unaryExpr(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Unary(emptyList(), opResult.getOrThrow(), inner.getOrThrow()))
    }
    return trailerExpr(input, allowStruct)
}

private fun trailerExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val atomResult = atomExpr(input, allowStruct)
    if (atomResult.isFailure) return atomResult
    return trailerHelper(input, atomResult.getOrThrow(), allowStruct)
}

private fun trailerHelper(input: ParseStream, e: Expr, allowStruct: Boolean): SynResult<Expr> {
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
        } else if (input.peek(DotPeek) && !input.peek2(DotDotPeek)) {
            val dotResult = input.parse(DotParse)
            if (dotResult.isFailure) return dotResult.asFailure()
            val dotToken = dotResult.getOrThrow()
            if (input.peek(AwaitPeek)) {
                val awaitResult = input.parse(AwaitParse)
                if (awaitResult.isFailure) return awaitResult.asFailure()
                current = Expr.Await(emptyList(), current, dotToken, awaitResult.getOrThrow())
                continue
            }
            val memberResult = parseMember(input)
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

private fun parseMember(input: ParseStream): SynResult<Member> {
    if (input.peek(IdentPeek)) {
        val identResult = input.parse(IdentParse)
        if (identResult.isFailure) return identResult.asFailure()
        return SynResult.success(Member.Named(identResult.getOrThrow()))
    }
    val litResult = input.parse(LitIntParse)
    if (litResult.isSuccess) {
        val digits = litResult.getOrThrow().base10Digits()
        val idx = digits.toUIntOrNull() ?: 0u
        return SynResult.success(Member.Unnamed(Index(idx, litResult.getOrThrow().span)))
    }
    return SynResult.failure(input.error("expected field name or index"))
}

private fun atomExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
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
    if (input.peek(BracePeek)) return parseExprBlock(input)
    if (input.peek(ParenPeek)) return parenOrTuple(input)
    if (input.peek(BracketPeek)) return arrayOrRepeat(input)
    if (input.peek(ReturnPeek)) {
        val retToken = input.parse(ReturnParse)
        if (retToken.isFailure) return retToken.asFailure()
        val expr =
            if (!input.isEmpty() && !input.peek(SemiPeek)) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Return(emptyList(), retToken.getOrThrow(), expr.getOrThrow()))
    }
    if (input.peek(BreakPeek)) {
        val brkToken = input.parse(BreakParse)
        if (brkToken.isFailure) return brkToken.asFailure()
        val labelResult = input.parse(LifetimeParse)
        val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
        val expr =
            if (!input.isEmpty() && !input.peek(SemiPeek) && !input.peek(CommaPeek)) {
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
    if (input.peek(DotDotPeek)) {
        return parseExprRange(input, null, allowStruct)
    }
    if (input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek)
    ) {
        return pathOrMacroOrStruct(input, allowStruct)
    }
    if (input.peek(MovePeek) ||
        input.peek(OrPeek) ||
        (input.peek(AsyncPeek) && (input.peek2(OrPeek) || input.peek2(MovePeek)))
    ) {
        return parseExprClosure(input, allowStruct)
    }
    if (input.peek(NotPeek) && input.peek2(IdentPeek)) {
        return pathOrMacroOrStruct(input, allowStruct)
    }
    return SynResult.failure(input.error("expected an expression"))
}

private fun pathOrMacroOrStruct(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
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

private fun parseFieldValue(input: ParseStream): SynResult<FieldValue> {
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

private fun parenOrTuple(input: ParseStream): SynResult<Expr> {
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

private fun arrayOrRepeat(input: ParseStream): SynResult<Expr> {
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
    val dotDotResult = input.parse(DotDotParse)
    if (dotDotResult.isFailure) return dotDotResult.asFailure()
    val limits = RangeLimits.HalfOpen(dotDotResult.getOrThrow())
    if (input.isEmpty() ||
        input.peek(SemiPeek) ||
        input.peek(CommaPeek) ||
        input.peek(BracePeek) ||
        input.peek(BracketPeek)
    ) {
        return SynResult.success(Expr.Range(emptyList(), start, limits, null))
    }
    val endResult = parseExprFull(input)
    if (endResult.isFailure) return endResult
    return SynResult.success(Expr.Range(emptyList(), start, limits, endResult.getOrThrow()))
}

private fun parseExprIf(input: ParseStream): SynResult<Expr> {
    val ifToken = input.parse(IfParse).getOrThrow()
    val cond = parseExprFull(input)
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

private fun parseExprWhile(input: ParseStream): SynResult<Expr> {
    val whileToken = input.parse(WhileParse).getOrThrow()
    val cond = parseExprFull(input)
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
    return SynResult.success(Expr.While(emptyList(), null, whileToken, cond.getOrThrow(), Block(brace, stmts)))
}

private fun parseExprLoop(input: ParseStream): SynResult<Expr> {
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
    return SynResult.success(Expr.Loop(emptyList(), null, loopToken, Block(brace, stmts)))
}

private fun parseExprMatch(input: ParseStream): SynResult<Expr> {
    val matchToken = input.parse(MatchParse).getOrThrow()
    val scrutinee = parseExprFull(input)
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
    val or1Result = input.parse(OrParse)
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
    val or2Result = input.parse(OrParse)
    if (or2Result.isFailure) return or2Result.asFailure()
    var output: ReturnType = ReturnType.Default
    if (input.peek(FatArrowPeek)) {
        val arrowResult = input.parse(FatArrowParse)
        if (arrowResult.isFailure) return arrowResult.asFailure()
        if (input.peek(BracePeek)) {
            val bodyResult = parseExprBlock(input)
            if (bodyResult.isFailure) return bodyResult
            return SynResult.success(Expr.Closure(emptyList(), constness, asyncness, capture, or1Result.getOrThrow(), inputs, or2Result.getOrThrow(), output, bodyResult.getOrThrow()))
        }
        val bodyResult = parseExprFull(input)
        if (bodyResult.isFailure) return bodyResult
        return SynResult.success(Expr.Closure(emptyList(), constness, asyncness, capture, or1Result.getOrThrow(), inputs, or2Result.getOrThrow(), output, bodyResult.getOrThrow()))
    }
    val bodyResult = parseExprBlock(input)
    if (bodyResult.isFailure) return bodyResult
    return SynResult.success(Expr.Closure(emptyList(), constness, asyncness, capture, or1Result.getOrThrow(), inputs, or2Result.getOrThrow(), output, bodyResult.getOrThrow()))
}

internal fun parseStmtFull(input: ParseStream): SynResult<Stmt> {
    if (input.peek(LetPeek)) {
        val letToken = input.parse(LetParse).getOrThrow()
        val patResult = input.call { parsePatFull(it) }
        if (patResult.isFailure) {
            val semi = input.parse(SemiParse)
            return patResult.map {
                Stmt.Local(emptyList(), letToken, it, null, semi.getOrThrow())
            }
        }
        val init: LocalInit? =
            if (input.peek(EqPeek)) {
                val eq = input.parse(EqParse).getOrThrow()
                val e = parseExprFull(input)
                if (e.isFailure) null else LocalInit(eq, e.getOrThrow(), null)
            } else {
                null
            }
        val semi = input.parse(SemiParse).getOrThrow()
        return SynResult.success(Stmt.Local(emptyList(), letToken, patResult.getOrThrow(), init, semi))
    }
    val exprResult = parseExprFull(input)
    if (exprResult.isFailure) return exprResult.asFailure()
    if (input.peek(SemiPeek)) {
        val semi = input.parse(SemiParse).getOrThrow()
        return SynResult.success(Stmt.ExprStmt(exprResult.getOrThrow(), semi))
    }
    return SynResult.success(Stmt.ExprStmt(exprResult.getOrThrow(), null))
}

internal fun parsePatFull(input: ParseStream): SynResult<Pat> = PatParseImpl.parse(input)

internal fun parseTypeFull(input: ParseStream): SynResult<SynType> = SynTypeParseExpr.parse(input)

internal object ExprParse : Parse<Expr> {
    override fun parse(input: ParseStream): SynResult<Expr> =
        ambiguousExpr(input, allowStruct = true)
}

internal object PatParseImpl : Parse<Pat> {
    override fun parse(input: ParseStream): SynResult<Pat> {
        if (input.peek(UnderscorePeek)) {
            val underscore = input.parse(UnderscoreParse).getOrThrow()
            return SynResult.success(Pat.Wild(emptyList(), underscore))
        }
        if (input.peek(IdentPeek)) {
            val ident: io.github.kotlinmania.procmacro2.Ident = input.parse(IdentParse).getOrThrow()
            if (input.peek(ColonPeek)) {
                val colon = input.parse(ColonParse).getOrThrow()
                val ty = parseTypeFull(input)
                if (ty.isFailure) return ty.asFailure()
                return SynResult.success(
                    Pat.TypeAscription(
                        emptyList(),
                        Pat.Ident(emptyList(), null, FieldMutability.None, ident, null, null),
                        colon,
                        ty.getOrThrow(),
                    ),
                )
            }
            return SynResult.success(
                Pat.Ident(emptyList(), null, FieldMutability.None, ident, null, null),
            )
        }
        if (input.peek(ParenPeek)) {
            val parens = parenthesized(input)
            if (parens.isFailure) return parens.asFailure()
            val parensVal = parens.getOrThrow()
            val content = parensVal.content
            val elems = PatList()
            while (!content.isEmpty()) {
                val p = content.call { parsePatFull(it) }
                if (p.isFailure) return p.asFailure()
                elems.pushValue(p.getOrThrow())
                if (content.isEmpty()) break
                val c = content.parse(CommaParse)
                if (c.isFailure) break
                elems.pushPunct(c.getOrThrow())
            }
            content.finishChildBuffer()
            if (elems.size == 1 && !elems.trailingPunct()) {
                return SynResult.success(Pat.PatParen(parensVal.token, elems.first()!!))
            }
            return SynResult.success(Pat.Tuple(parensVal.token, elems))
        }
        return SynResult.failure(input.error("unsupported pattern"))
    }
}

internal object SynTypeParseExpr : Parse<SynType> {
    override fun parse(input: ParseStream): SynResult<SynType> {
        if (input.peek(UnderscorePeek)) {
            val underscore = input.parse(UnderscoreParse).getOrThrow()
            return SynResult.success(SynType.Infer(underscore))
        }
        if (input.peek(AndPeek)) {
            val andToken = input.parse(AndParse).getOrThrow()
            val ltResult = input.parse(LifetimeParse)
            val lifetime = if (ltResult.isSuccess) ltResult.getOrThrow() else null
            val mutResult = input.parse(MutParse)
            val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
            val inner = parseTypeFull(input)
            if (inner.isFailure) return inner.asFailure()
            return SynResult.success(SynType.Reference(andToken, lifetime, mutability, inner.getOrThrow()))
        }
        if (input.peek(StarPeek)) {
            val starToken = input.parse(StarParse).getOrThrow()
            val constResult = input.parse(ConstParse)
            val mutResult = input.parse(MutParse)
            val constToken = if (constResult.isSuccess) constResult.getOrThrow() else null
            val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
            val inner = parseTypeFull(input)
            if (inner.isFailure) return inner.asFailure()
            return SynResult.success(SynType.Ptr(starToken, constToken, mutability, inner.getOrThrow()))
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
}
