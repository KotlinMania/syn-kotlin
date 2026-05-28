@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
// port-lint: source op.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.And
import io.github.kotlinmania.syn.token.AndAnd
import io.github.kotlinmania.syn.token.AndEq
import io.github.kotlinmania.syn.token.Caret
import io.github.kotlinmania.syn.token.CaretEq
import io.github.kotlinmania.syn.token.EqEq
import io.github.kotlinmania.syn.token.Ge
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Le
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Minus
import io.github.kotlinmania.syn.token.MinusEq
import io.github.kotlinmania.syn.token.Ne
import io.github.kotlinmania.syn.token.Not
import io.github.kotlinmania.syn.token.Or
import io.github.kotlinmania.syn.token.OrEq
import io.github.kotlinmania.syn.token.OrOr
import io.github.kotlinmania.syn.token.Percent
import io.github.kotlinmania.syn.token.PercentEq
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.syn.token.PlusEq
import io.github.kotlinmania.syn.token.Shl
import io.github.kotlinmania.syn.token.ShlEq
import io.github.kotlinmania.syn.token.Shr
import io.github.kotlinmania.syn.token.ShrEq
import io.github.kotlinmania.syn.token.Slash
import io.github.kotlinmania.syn.token.SlashEq
import io.github.kotlinmania.syn.token.Star
import io.github.kotlinmania.syn.token.StarEq
import io.github.kotlinmania.quote.ToTokens
import kotlin.native.HiddenFromObjC

/** A binary operator. */
public sealed class BinOp : ToTokens {
    public data class Add(val token: io.github.kotlinmania.syn.token.Plus) : BinOp()
    public data class Sub(val token: io.github.kotlinmania.syn.token.Minus) : BinOp()
    public data class Mul(val token: io.github.kotlinmania.syn.token.Star) : BinOp()
    public data class Div(val token: io.github.kotlinmania.syn.token.Slash) : BinOp()
    public data class Rem(val token: io.github.kotlinmania.syn.token.Percent) : BinOp()
    public data class And(val token: io.github.kotlinmania.syn.token.AndAnd) : BinOp()
    public data class Or(val token: io.github.kotlinmania.syn.token.OrOr) : BinOp()
    public data class BitXor(val token: io.github.kotlinmania.syn.token.Caret) : BinOp()
    public data class BitAnd(val token: io.github.kotlinmania.syn.token.And) : BinOp()
    public data class BitOr(val token: io.github.kotlinmania.syn.token.Or) : BinOp()
    public data class Shl(val token: io.github.kotlinmania.syn.token.Shl) : BinOp()
    public data class Shr(val token: io.github.kotlinmania.syn.token.Shr) : BinOp()
    public data class Eq(val token: io.github.kotlinmania.syn.token.EqEq) : BinOp()
    public data class Lt(val token: io.github.kotlinmania.syn.token.Lt) : BinOp()
    public data class Le(val token: io.github.kotlinmania.syn.token.Le) : BinOp()
    public data class Ne(val token: io.github.kotlinmania.syn.token.Ne) : BinOp()
    public data class Ge(val token: io.github.kotlinmania.syn.token.Ge) : BinOp()
    public data class Gt(val token: io.github.kotlinmania.syn.token.Gt) : BinOp()
    public data class AddAssign(val token: io.github.kotlinmania.syn.token.PlusEq) : BinOp()
    public data class SubAssign(val token: io.github.kotlinmania.syn.token.MinusEq) : BinOp()
    public data class MulAssign(val token: io.github.kotlinmania.syn.token.StarEq) : BinOp()
    public data class DivAssign(val token: io.github.kotlinmania.syn.token.SlashEq) : BinOp()
    public data class RemAssign(val token: io.github.kotlinmania.syn.token.PercentEq) : BinOp()
    public data class BitXorAssign(val token: io.github.kotlinmania.syn.token.CaretEq) : BinOp()
    public data class BitAndAssign(val token: io.github.kotlinmania.syn.token.AndEq) : BinOp()
    public data class BitOrAssign(val token: io.github.kotlinmania.syn.token.OrEq) : BinOp()
    public data class ShlAssign(val token: io.github.kotlinmania.syn.token.ShlEq) : BinOp()
    public data class ShrAssign(val token: io.github.kotlinmania.syn.token.ShrEq) : BinOp()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Add -> token.toTokens(tokens)
            is Sub -> token.toTokens(tokens)
            is Mul -> token.toTokens(tokens)
            is Div -> token.toTokens(tokens)
            is Rem -> token.toTokens(tokens)
            is And -> token.toTokens(tokens)
            is Or -> token.toTokens(tokens)
            is BitXor -> token.toTokens(tokens)
            is BitAnd -> token.toTokens(tokens)
            is BitOr -> token.toTokens(tokens)
            is Shl -> token.toTokens(tokens)
            is Shr -> token.toTokens(tokens)
            is Eq -> token.toTokens(tokens)
            is Lt -> token.toTokens(tokens)
            is Le -> token.toTokens(tokens)
            is Ne -> token.toTokens(tokens)
            is Ge -> token.toTokens(tokens)
            is Gt -> token.toTokens(tokens)
            is AddAssign -> token.toTokens(tokens)
            is SubAssign -> token.toTokens(tokens)
            is MulAssign -> token.toTokens(tokens)
            is DivAssign -> token.toTokens(tokens)
            is RemAssign -> token.toTokens(tokens)
            is BitXorAssign -> token.toTokens(tokens)
            is BitAndAssign -> token.toTokens(tokens)
            is BitOrAssign -> token.toTokens(tokens)
            is ShlAssign -> token.toTokens(tokens)
            is ShrAssign -> token.toTokens(tokens)
        }
    }
}

/** A unary operator. */
public sealed class UnOp : ToTokens {
    public data class Deref(val token: io.github.kotlinmania.syn.token.Star) : UnOp()
    public data class NotOp(val token: io.github.kotlinmania.syn.token.Not) : UnOp()
    public data class Neg(val token: io.github.kotlinmania.syn.token.Minus) : UnOp()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Deref -> token.toTokens(tokens)
            is NotOp -> token.toTokens(tokens)
            is Neg -> token.toTokens(tokens)
        }
    }
}

/** Parses a binary operator by examining punctuation at the current position. */
@HiddenFromObjC
public object BinOpParse : Parse<BinOp> {
    override fun parse(input: ParseStream): SynResult<BinOp> =
        input.step { cursor ->
            val (punct, rest) = cursor.punct()
                ?: return@step SynResult.failure(cursor.error("expected binary operator"))
            val ch = punct.asChar()
            val span = punct.span()
            val spaced = punct.spacing() == Spacing.Alone
            // Multi-character operators need joint spacing on all but the last char
            if (spaced) {
                // Single-char operators with Alone spacing
                return@step when (ch) {
                    '+' -> SynResult.success(BinOp.Add(io.github.kotlinmania.syn.token.Plus.from(span)) to rest)
                    '-' -> SynResult.success(BinOp.Sub(io.github.kotlinmania.syn.token.Minus.from(span)) to rest)
                    '*' -> SynResult.success(BinOp.Mul(io.github.kotlinmania.syn.token.Star.from(span)) to rest)
                    '/' -> SynResult.success(BinOp.Div(io.github.kotlinmania.syn.token.Slash.from(span)) to rest)
                    '%' -> SynResult.success(BinOp.Rem(io.github.kotlinmania.syn.token.Percent.from(span)) to rest)
                    '^' -> SynResult.success(BinOp.BitXor(io.github.kotlinmania.syn.token.Caret.from(span)) to rest)
                    '&' -> SynResult.success(BinOp.BitAnd(io.github.kotlinmania.syn.token.And.from(span)) to rest)
                    '|' -> SynResult.success(BinOp.BitOr(io.github.kotlinmania.syn.token.Or.from(span)) to rest)
                    '<' -> SynResult.success(BinOp.Lt(io.github.kotlinmania.syn.token.Lt.from(span)) to rest)
                    '>' -> SynResult.success(BinOp.Gt(io.github.kotlinmania.syn.token.Gt.from(span)) to rest)
                    else -> SynResult.failure(cursor.error("expected binary operator"))
                }
            }
            // Joint spacing: peek the second char
            val secondPunct = rest.punct()
            val second = secondPunct?.first
            when (ch) {
                '=' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.Eq(io.github.kotlinmania.syn.token.EqEq.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.failure(cursor.error("expected binary operator"))
                }
                '!' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.Ne(io.github.kotlinmania.syn.token.Ne.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.failure(cursor.error("expected binary operator"))
                }
                '<' -> when {
                    second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone ->
                        SynResult.success(BinOp.Le(io.github.kotlinmania.syn.token.Le.from(listOf(span, second.span()))) to secondPunct.second)
                    second != null && second.asChar() == '<' -> {
                        val thirdPunct = secondPunct.second.punct()
                        if (thirdPunct != null && thirdPunct.first.asChar() == '=' && thirdPunct.first.spacing() == Spacing.Alone) {
                            SynResult.success(BinOp.ShlAssign(ShlEq.from(listOf(span, second.span(), thirdPunct.first.span()))) to thirdPunct.second)
                        } else if (thirdPunct != null && second.spacing() == Spacing.Joint) {
                            SynResult.success(BinOp.Shl(io.github.kotlinmania.syn.token.Shl.from(listOf(span, second.span()))) to thirdPunct.second)
                        } else if (second.spacing() == Spacing.Joint) {
                            SynResult.success(BinOp.Shl(io.github.kotlinmania.syn.token.Shl.from(listOf(span, second.span()))) to secondPunct.second)
                        } else {
                            SynResult.failure(cursor.error("expected binary operator"))
                        }
                    }
                    else -> SynResult.success(BinOp.Lt(io.github.kotlinmania.syn.token.Lt.from(span)) to rest)
                }
                '>' -> when {
                    second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone ->
                        SynResult.success(BinOp.Ge(io.github.kotlinmania.syn.token.Ge.from(listOf(span, second.span()))) to secondPunct.second)
                    second != null && second.asChar() == '>' -> {
                        val thirdPunct = secondPunct.second.punct()
                        if (thirdPunct != null && thirdPunct.first.asChar() == '=' && thirdPunct.first.spacing() == Spacing.Alone) {
                            SynResult.success(BinOp.ShrAssign(ShrEq.from(listOf(span, second.span(), thirdPunct.first.span()))) to thirdPunct.second)
                        } else if (thirdPunct != null && second.spacing() == Spacing.Joint) {
                            SynResult.success(BinOp.Shr(io.github.kotlinmania.syn.token.Shr.from(listOf(span, second.span()))) to thirdPunct.second)
                        } else if (second.spacing() == Spacing.Joint) {
                            SynResult.success(BinOp.Shr(io.github.kotlinmania.syn.token.Shr.from(listOf(span, second.span()))) to secondPunct.second)
                        } else {
                            SynResult.failure(cursor.error("expected binary operator"))
                        }
                    }
                    else -> SynResult.success(BinOp.Gt(io.github.kotlinmania.syn.token.Gt.from(span)) to rest)
                }
                '&' -> if (second != null && second.asChar() == '&' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.And(io.github.kotlinmania.syn.token.AndAnd.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.success(BinOp.BitAnd(io.github.kotlinmania.syn.token.And.from(span)) to rest)
                }
                '|' -> if (second != null && second.asChar() == '|' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.Or(io.github.kotlinmania.syn.token.OrOr.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.success(BinOp.BitOr(io.github.kotlinmania.syn.token.Or.from(span)) to rest)
                }
                '+' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.AddAssign(PlusEq.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.success(BinOp.Add(io.github.kotlinmania.syn.token.Plus.from(span)) to rest)
                }
                '-' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.SubAssign(MinusEq.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.success(BinOp.Sub(io.github.kotlinmania.syn.token.Minus.from(span)) to rest)
                }
                '*' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.MulAssign(StarEq.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.success(BinOp.Mul(io.github.kotlinmania.syn.token.Star.from(span)) to rest)
                }
                '/' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.DivAssign(SlashEq.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.success(BinOp.Div(io.github.kotlinmania.syn.token.Slash.from(span)) to rest)
                }
                '%' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.RemAssign(PercentEq.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.success(BinOp.Rem(io.github.kotlinmania.syn.token.Percent.from(span)) to rest)
                }
                '^' -> if (second != null && second.asChar() == '=' && second.spacing() == Spacing.Alone) {
                    SynResult.success(BinOp.BitXorAssign(CaretEq.from(listOf(span, second.span()))) to secondPunct.second)
                } else {
                    SynResult.failure(cursor.error("expected binary operator"))
                }
                else -> SynResult.failure(cursor.error("expected binary operator"))
            }
        }
}

/** Parses a unary operator by examining the next punctuation token. */
@HiddenFromObjC
public object UnOpParse : Parse<UnOp> {
    override fun parse(input: ParseStream): SynResult<UnOp> =
        input.step { cursor ->
            val (punct, rest) = cursor.punct()
                ?: return@step SynResult.failure(cursor.error("expected unary operator"))
            val span = punct.span()
            when (punct.asChar()) {
                '*' -> SynResult.success(UnOp.Deref(io.github.kotlinmania.syn.token.Star.from(span)) to rest)
                '!' -> SynResult.success(UnOp.NotOp(io.github.kotlinmania.syn.token.Not.from(span)) to rest)
                '-' -> SynResult.success(UnOp.Neg(io.github.kotlinmania.syn.token.Minus.from(span)) to rest)
                else -> SynResult.failure(cursor.error("expected unary operator"))
            }
        }
}
