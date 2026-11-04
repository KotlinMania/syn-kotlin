// port-lint: source scan_expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Spacing

internal sealed class Input {
    data class Keyword(
        val expected: String,
    ) : Input()

    data class Punct(
        val expected: String,
    ) : Input()

    data object ConsumeAny : Input()

    data object ConsumeBinOp : Input()

    data object ConsumeBrace : Input()

    data object ConsumeDelimiter : Input()

    data object ConsumeIdent : Input()

    data object ConsumeLifetime : Input()

    data object ConsumeLiteral : Input()

    data object ConsumeNestedBrace : Input()

    data object ExpectPath : Input()

    data object ExpectTurbofish : Input()

    data object ExpectType : Input()

    data object CanBeginExpr : Input()

    data object Otherwise : Input()

    data object Empty : Input()
}

internal sealed class Action {
    class SetState(
        val next: () -> List<ScanRule>,
    ) : Action()

    data object IncDepth : Action()

    data object DecDepth : Action()

    data object Finish : Action()
}

internal data class ScanRule(
    val input: Input,
    val action: Action,
)

private val POUND_TAIL: List<ScanRule> =
    listOf(ScanRule(Input.ConsumeDelimiter, Action.SetState { INIT }))

private val LIFETIME_TAIL: List<ScanRule> =
    listOf(ScanRule(Input.Punct(":"), Action.SetState { INIT }))

private val AS_TAIL: List<ScanRule> =
    listOf(ScanRule(Input.ExpectType, Action.SetState { POSTFIX }))

private val CLOSURE_RET_TAIL: List<ScanRule> =
    listOf(ScanRule(Input.ExpectType, Action.SetState { BLOCK }))

private val INIT: List<ScanRule> =
    listOf(
        ScanRule(Input.ConsumeDelimiter, Action.SetState { POSTFIX }),
        ScanRule(Input.Keyword("async"), Action.SetState { ASYNC }),
        ScanRule(Input.Keyword("break"), Action.SetState { BREAK_LABEL }),
        ScanRule(Input.Keyword("const"), Action.SetState { CONST }),
        ScanRule(Input.Keyword("continue"), Action.SetState { CONTINUE }),
        ScanRule(Input.Keyword("for"), Action.SetState { FOR }),
        ScanRule(Input.Keyword("if"), Action.IncDepth),
        ScanRule(Input.Keyword("let"), Action.SetState { PATTERN }),
        ScanRule(Input.Keyword("loop"), Action.SetState { BLOCK }),
        ScanRule(Input.Keyword("match"), Action.IncDepth),
        ScanRule(Input.Keyword("move"), Action.SetState { CLOSURE }),
        ScanRule(Input.Keyword("return"), Action.SetState { RETURN }),
        ScanRule(Input.Keyword("static"), Action.SetState { CLOSURE }),
        ScanRule(Input.Keyword("try"), Action.SetState { BLOCK }),
        ScanRule(Input.Keyword("unsafe"), Action.SetState { BLOCK }),
        ScanRule(Input.Keyword("while"), Action.IncDepth),
        ScanRule(Input.Keyword("yield"), Action.SetState { RETURN }),
        ScanRule(Input.Keyword("_"), Action.SetState { POSTFIX }),
        ScanRule(Input.Punct("!"), Action.SetState { INIT }),
        ScanRule(Input.Punct("#"), Action.SetState { POUND_TAIL }),
        ScanRule(Input.Punct("&"), Action.SetState { REFERENCE }),
        ScanRule(Input.Punct("*"), Action.SetState { INIT }),
        ScanRule(Input.Punct("-"), Action.SetState { INIT }),
        ScanRule(Input.Punct("..="), Action.SetState { INIT }),
        ScanRule(Input.Punct(".."), Action.SetState { RANGE }),
        ScanRule(Input.Punct("|"), Action.SetState { CLOSURE_ARGS }),
        ScanRule(Input.ConsumeLifetime, Action.SetState { LIFETIME_TAIL }),
        ScanRule(Input.ConsumeLiteral, Action.SetState { POSTFIX }),
        ScanRule(Input.ExpectPath, Action.SetState { PATH }),
    )

private val POSTFIX: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("as"), Action.SetState { AS_TAIL }),
        ScanRule(Input.Punct("..="), Action.SetState { INIT }),
        ScanRule(Input.Punct(".."), Action.SetState { RANGE }),
        ScanRule(Input.Punct("."), Action.SetState { DOT }),
        ScanRule(Input.Punct("?"), Action.SetState { POSTFIX }),
        ScanRule(Input.ConsumeBinOp, Action.SetState { INIT }),
        ScanRule(Input.Punct("="), Action.SetState { INIT }),
        ScanRule(Input.ConsumeNestedBrace, Action.SetState { IF_THEN }),
        ScanRule(Input.ConsumeDelimiter, Action.SetState { POSTFIX }),
        ScanRule(Input.Empty, Action.Finish),
    )

private val ASYNC: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("move"), Action.SetState { ASYNC }),
        ScanRule(Input.Punct("|"), Action.SetState { CLOSURE_ARGS }),
        ScanRule(Input.ConsumeBrace, Action.SetState { POSTFIX }),
    )

private val BLOCK: List<ScanRule> =
    listOf(
        ScanRule(Input.ConsumeBrace, Action.SetState { POSTFIX }),
    )

private val BREAK_LABEL: List<ScanRule> =
    listOf(
        ScanRule(Input.ConsumeLifetime, Action.SetState { BREAK_VALUE }),
        ScanRule(Input.Otherwise, Action.SetState { BREAK_VALUE }),
    )

private val BREAK_VALUE: List<ScanRule> =
    listOf(
        ScanRule(Input.ConsumeNestedBrace, Action.SetState { IF_THEN }),
        ScanRule(Input.CanBeginExpr, Action.SetState { INIT }),
        ScanRule(Input.Otherwise, Action.SetState { POSTFIX }),
    )

private val CLOSURE: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("async"), Action.SetState { CLOSURE }),
        ScanRule(Input.Keyword("move"), Action.SetState { CLOSURE }),
        ScanRule(Input.Punct(","), Action.SetState { CLOSURE }),
        ScanRule(Input.Punct(">"), Action.SetState { CLOSURE }),
        ScanRule(Input.Punct("|"), Action.SetState { CLOSURE_ARGS }),
        ScanRule(Input.ConsumeLifetime, Action.SetState { CLOSURE }),
        ScanRule(Input.ConsumeIdent, Action.SetState { CLOSURE }),
    )

private val CLOSURE_ARGS: List<ScanRule> =
    listOf(
        ScanRule(Input.Punct("|"), Action.SetState { CLOSURE_RET }),
        ScanRule(Input.ConsumeAny, Action.SetState { CLOSURE_ARGS }),
    )

private val CLOSURE_RET: List<ScanRule> =
    listOf(
        ScanRule(Input.Punct("->"), Action.SetState { CLOSURE_RET_TAIL }),
        ScanRule(Input.Otherwise, Action.SetState { INIT }),
    )

private val CONST: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("move"), Action.SetState { CLOSURE }),
        ScanRule(Input.Punct("|"), Action.SetState { CLOSURE_ARGS }),
        ScanRule(Input.ConsumeBrace, Action.SetState { POSTFIX }),
    )

private val CONTINUE: List<ScanRule> =
    listOf(
        ScanRule(Input.ConsumeLifetime, Action.SetState { POSTFIX }),
        ScanRule(Input.Otherwise, Action.SetState { POSTFIX }),
    )

private val DOT: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("await"), Action.SetState { POSTFIX }),
        ScanRule(Input.ConsumeIdent, Action.SetState { METHOD }),
        ScanRule(Input.ConsumeLiteral, Action.SetState { POSTFIX }),
    )

private val FOR: List<ScanRule> =
    listOf(
        ScanRule(Input.Punct("<"), Action.SetState { CLOSURE }),
        ScanRule(Input.Otherwise, Action.SetState { PATTERN }),
    )

private val IF_ELSE: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("if"), Action.SetState { INIT }),
        ScanRule(Input.ConsumeBrace, Action.DecDepth),
    )

private val IF_THEN: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("else"), Action.SetState { IF_ELSE }),
        ScanRule(Input.Otherwise, Action.DecDepth),
    )

private val METHOD: List<ScanRule> =
    listOf(
        ScanRule(Input.ExpectTurbofish, Action.SetState { POSTFIX }),
    )

private val PATH: List<ScanRule> =
    listOf(
        ScanRule(Input.Punct("!="), Action.SetState { INIT }),
        ScanRule(Input.Punct("!"), Action.SetState { INIT }),
        ScanRule(Input.ConsumeNestedBrace, Action.SetState { IF_THEN }),
        ScanRule(Input.Otherwise, Action.SetState { POSTFIX }),
    )

private val PATTERN: List<ScanRule> =
    listOf(
        ScanRule(Input.ConsumeDelimiter, Action.SetState { PATTERN }),
        ScanRule(Input.Keyword("box"), Action.SetState { PATTERN }),
        ScanRule(Input.Keyword("in"), Action.IncDepth),
        ScanRule(Input.Keyword("mut"), Action.SetState { PATTERN }),
        ScanRule(Input.Keyword("ref"), Action.SetState { PATTERN }),
        ScanRule(Input.Keyword("_"), Action.SetState { PATTERN }),
        ScanRule(Input.Punct("!"), Action.SetState { PATTERN }),
        ScanRule(Input.Punct("&"), Action.SetState { PATTERN }),
        ScanRule(Input.Punct("..="), Action.SetState { PATTERN }),
        ScanRule(Input.Punct(".."), Action.SetState { PATTERN }),
        ScanRule(Input.Punct("="), Action.SetState { INIT }),
        ScanRule(Input.Punct("@"), Action.SetState { PATTERN }),
        ScanRule(Input.Punct("|"), Action.SetState { PATTERN }),
        ScanRule(Input.ConsumeLiteral, Action.SetState { PATTERN }),
        ScanRule(Input.ExpectPath, Action.SetState { PATTERN }),
    )

private val RANGE: List<ScanRule> =
    listOf(
        ScanRule(Input.Punct("..="), Action.SetState { INIT }),
        ScanRule(Input.Punct(".."), Action.SetState { RANGE }),
        ScanRule(Input.Punct("."), Action.SetState { DOT }),
        ScanRule(Input.ConsumeNestedBrace, Action.SetState { IF_THEN }),
        ScanRule(Input.Empty, Action.Finish),
        ScanRule(Input.Otherwise, Action.SetState { INIT }),
    )

private val RAW: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("const"), Action.SetState { INIT }),
        ScanRule(Input.Keyword("mut"), Action.SetState { INIT }),
        ScanRule(Input.Otherwise, Action.SetState { POSTFIX }),
    )

private val REFERENCE: List<ScanRule> =
    listOf(
        ScanRule(Input.Keyword("mut"), Action.SetState { INIT }),
        ScanRule(Input.Keyword("raw"), Action.SetState { RAW }),
        ScanRule(Input.Otherwise, Action.SetState { INIT }),
    )

private val RETURN: List<ScanRule> =
    listOf(
        ScanRule(Input.CanBeginExpr, Action.SetState { INIT }),
        ScanRule(Input.Otherwise, Action.SetState { POSTFIX }),
    )

internal fun scanExpr(input: ParseStream): SynResult<Unit> {
    var state = INIT
    var depth = 0
    table@ while (true) {
        for (rule in state) {
            val matched =
                when (val expectedInput = rule.input) {
                    is Input.Keyword -> {
                        input
                            .step { cursor ->
                                val pair = cursor.ident()
                                if (pair != null && pair.first.toString() == expectedInput.expected) {
                                    SynResult.success(true to pair.second)
                                } else {
                                    SynResult.success(false to cursor.raw)
                                }
                            }.getOrElse { return SynResult.failure(it) }
                    }

                    is Input.Punct -> {
                        input
                            .step { stepCursor ->
                                val begin = stepCursor.raw
                                var cursor = begin
                                val chars = expectedInput.expected
                                for ((i, ch) in chars.withIndex()) {
                                    val pair = cursor.punct() ?: break
                                    val (punct, rest) = pair
                                    if (punct.asChar() != ch) {
                                        break
                                    } else if (i == chars.length - 1) {
                                        return@step SynResult.success(true to rest)
                                    } else if (punct.spacing() == Spacing.Joint) {
                                        cursor = rest
                                    } else {
                                        break
                                    }
                                }
                                SynResult.success(false to begin)
                            }.getOrElse { return SynResult.failure(it) }
                    }

                    Input.ConsumeAny -> {
                        if (input.isEmpty()) {
                            false
                        } else {
                            TokenTreeParse.parse(input).getOrElse { return SynResult.failure(it) }
                            true
                        }
                    }

                    Input.ConsumeBinOp -> {
                        BinOpParse.parse(input).isSuccess
                    }

                    Input.ConsumeBrace, Input.ConsumeNestedBrace -> {
                        (expectedInput === Input.ConsumeBrace || depth > 0) &&
                            input
                                .step { cursor ->
                                    val group = cursor.group(Delimiter.Brace)
                                    if (group != null) {
                                        SynResult.success(true to group.third)
                                    } else {
                                        SynResult.success(false to cursor.raw)
                                    }
                                }.getOrElse { return SynResult.failure(it) }
                    }

                    Input.ConsumeDelimiter -> {
                        input
                            .step { cursor ->
                                val group = cursor.anyGroup()
                                if (group != null) {
                                    SynResult.success(true to group.after)
                                } else {
                                    SynResult.success(false to cursor.raw)
                                }
                            }.getOrElse { return SynResult.failure(it) }
                    }

                    Input.ConsumeIdent -> {
                        if (input.peek(IdentPeek)) {
                            IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                            true
                        } else {
                            false
                        }
                    }

                    Input.ConsumeLifetime -> {
                        if (input.peek(LifetimePeek)) {
                            LifetimeParse.parse(input).getOrElse { return SynResult.failure(it) }
                            true
                        } else {
                            false
                        }
                    }

                    Input.ConsumeLiteral -> {
                        if (input.peek(LitPeek)) {
                            LitParse.parse(input).getOrElse { return SynResult.failure(it) }
                            true
                        } else {
                            false
                        }
                    }

                    Input.ExpectPath -> {
                        parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
                        qpath(input, exprStyle = true).getOrElse { return SynResult.failure(it) }
                        true
                    }

                    Input.ExpectTurbofish -> {
                        if (input.peek(PathSepPeek)) {
                            PathArguments.AngleBracketed.parse(input).getOrElse { return SynResult.failure(it) }
                        }
                        true
                    }

                    Input.ExpectType -> {
                        parseTypeWithoutPlus(input).getOrElse { return SynResult.failure(it) }
                        true
                    }

                    Input.CanBeginExpr -> {
                        peekExpr(input)
                    }

                    Input.Otherwise -> {
                        true
                    }

                    Input.Empty -> {
                        input.isEmpty() || input.peek(CommaPeek)
                    }
                }

            if (matched) {
                state =
                    when (val action = rule.action) {
                        is Action.SetState -> {
                            action.next()
                        }

                        Action.IncDepth -> {
                            depth += 1
                            INIT
                        }

                        Action.DecDepth -> {
                            depth -= 1
                            POSTFIX
                        }

                        Action.Finish -> {
                            if (depth == 0) {
                                return SynResult.success(Unit)
                            } else {
                                break
                            }
                        }
                    }
                continue@table
            }
        }
        return SynResult.failure(input.error("unsupported expression"))
    }
}
