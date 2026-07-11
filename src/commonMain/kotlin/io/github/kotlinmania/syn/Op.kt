// port-lint: source op.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/** A binary operator. */
public sealed class BinOp : ToTokens {
    public data class Add(
        var token: io.github.kotlinmania.syn.token.Plus,
    ) : BinOp()

    public data class Sub(
        var token: io.github.kotlinmania.syn.token.Minus,
    ) : BinOp()

    public data class Mul(
        var token: io.github.kotlinmania.syn.token.Star,
    ) : BinOp()

    public data class Div(
        var token: io.github.kotlinmania.syn.token.Slash,
    ) : BinOp()

    public data class Rem(
        var token: io.github.kotlinmania.syn.token.Percent,
    ) : BinOp()

    public data class And(
        var token: io.github.kotlinmania.syn.token.AndAnd,
    ) : BinOp()

    public data class Or(
        var token: io.github.kotlinmania.syn.token.OrOr,
    ) : BinOp()

    public data class BitXor(
        var token: io.github.kotlinmania.syn.token.Caret,
    ) : BinOp()

    public data class BitAnd(
        var token: io.github.kotlinmania.syn.token.And,
    ) : BinOp()

    public data class BitOr(
        var token: io.github.kotlinmania.syn.token.Or,
    ) : BinOp()

    public data class Shl(
        var token: io.github.kotlinmania.syn.token.Shl,
    ) : BinOp()

    public data class Shr(
        var token: io.github.kotlinmania.syn.token.Shr,
    ) : BinOp()

    public data class Eq(
        var token: io.github.kotlinmania.syn.token.EqEq,
    ) : BinOp()

    public data class Lt(
        var token: io.github.kotlinmania.syn.token.Lt,
    ) : BinOp()

    public data class Le(
        var token: io.github.kotlinmania.syn.token.Le,
    ) : BinOp()

    public data class Ne(
        var token: io.github.kotlinmania.syn.token.Ne,
    ) : BinOp()

    public data class Ge(
        var token: io.github.kotlinmania.syn.token.Ge,
    ) : BinOp()

    public data class Gt(
        var token: io.github.kotlinmania.syn.token.Gt,
    ) : BinOp()

    public data class AddAssign(
        var token: io.github.kotlinmania.syn.token.PlusEq,
    ) : BinOp()

    public data class SubAssign(
        var token: io.github.kotlinmania.syn.token.MinusEq,
    ) : BinOp()

    public data class MulAssign(
        var token: io.github.kotlinmania.syn.token.StarEq,
    ) : BinOp()

    public data class DivAssign(
        var token: io.github.kotlinmania.syn.token.SlashEq,
    ) : BinOp()

    public data class RemAssign(
        var token: io.github.kotlinmania.syn.token.PercentEq,
    ) : BinOp()

    public data class BitXorAssign(
        var token: io.github.kotlinmania.syn.token.CaretEq,
    ) : BinOp()

    public data class BitAndAssign(
        var token: io.github.kotlinmania.syn.token.AndEq,
    ) : BinOp()

    public data class BitOrAssign(
        var token: io.github.kotlinmania.syn.token.OrEq,
    ) : BinOp()

    public data class ShlAssign(
        var token: io.github.kotlinmania.syn.token.ShlEq,
    ) : BinOp()

    public data class ShrAssign(
        var token: io.github.kotlinmania.syn.token.ShrEq,
    ) : BinOp()

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
    public data class Deref(
        var token: io.github.kotlinmania.syn.token.Star,
    ) : UnOp()

    public data class NotOp(
        var token: io.github.kotlinmania.syn.token.Not,
    ) : UnOp()

    public data class Neg(
        var token: io.github.kotlinmania.syn.token.Minus,
    ) : UnOp()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Deref -> token.toTokens(tokens)
            is NotOp -> token.toTokens(tokens)
            is Neg -> token.toTokens(tokens)
        }
    }
}

public object BinOpParse {
    fun parse(input: ParseStream): SynResult<BinOp> =
        when {
            input.peek(PlusEqPeek) -> PlusEqParse.parse(input).map(BinOp::AddAssign)
            input.peek(MinusEqPeek) -> MinusEqParse.parse(input).map(BinOp::SubAssign)
            input.peek(StarEqPeek) -> StarEqParse.parse(input).map(BinOp::MulAssign)
            input.peek(SlashEqPeek) -> SlashEqParse.parse(input).map(BinOp::DivAssign)
            input.peek(PercentEqPeek) -> PercentEqParse.parse(input).map(BinOp::RemAssign)
            input.peek(CaretEqPeek) -> CaretEqParse.parse(input).map(BinOp::BitXorAssign)
            input.peek(AndEqPeek) -> AndEqParse.parse(input).map(BinOp::BitAndAssign)
            input.peek(OrEqPeek) -> OrEqParse.parse(input).map(BinOp::BitOrAssign)
            input.peek(ShlEqPeek) -> ShlEqParse.parse(input).map(BinOp::ShlAssign)
            input.peek(ShrEqPeek) -> ShrEqParse.parse(input).map(BinOp::ShrAssign)
            input.peek(AndAndPeek) -> AndAndParse.parse(input).map(BinOp::And)
            input.peek(OrOrPeek) -> OrOrParse.parse(input).map(BinOp::Or)
            input.peek(ShlPeek) -> ShlParse.parse(input).map(BinOp::Shl)
            input.peek(ShrPeek) -> ShrParse.parse(input).map(BinOp::Shr)
            input.peek(EqEqPeek) -> EqEqParse.parse(input).map(BinOp::Eq)
            input.peek(LePeek) -> LeParse.parse(input).map(BinOp::Le)
            input.peek(NePeek) -> NeParse.parse(input).map(BinOp::Ne)
            input.peek(GePeek) -> GeParse.parse(input).map(BinOp::Ge)
            input.peek(PlusPeek) -> PlusParse.parse(input).map(BinOp::Add)
            input.peek(MinusPeek) -> MinusParse.parse(input).map(BinOp::Sub)
            input.peek(StarPeek) -> StarParse.parse(input).map(BinOp::Mul)
            input.peek(SlashPeek) -> SlashParse.parse(input).map(BinOp::Div)
            input.peek(PercentPeek) -> PercentParse.parse(input).map(BinOp::Rem)
            input.peek(CaretPeek) -> CaretParse.parse(input).map(BinOp::BitXor)
            input.peek(AndPeek) -> AndParse.parse(input).map(BinOp::BitAnd)
            input.peek(OrPeek) -> OrParse.parse(input).map(BinOp::BitOr)
            input.peek(LtPeek) -> LtParse.parse(input).map(BinOp::Lt)
            input.peek(GtPeek) -> GtParse.parse(input).map(BinOp::Gt)
            else -> SynResult.failure(input.error("expected binary operator"))
        }
}

public object UnOpParse {
    fun parse(input: ParseStream): SynResult<UnOp> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected unary operator"))
            val span = punct.span()
            when (punct.asChar()) {
                '*' ->
                    SynResult.success(
                        UnOp.Deref(
                            io.github.kotlinmania.syn.token.Star
                                .from(span),
                        ) to rest,
                    )
                '!' ->
                    SynResult.success(
                        UnOp.NotOp(
                            io.github.kotlinmania.syn.token.Not
                                .from(span),
                        ) to rest,
                    )
                '-' ->
                    SynResult.success(
                        UnOp.Neg(
                            io.github.kotlinmania.syn.token.Minus
                                .from(span),
                        ) to rest,
                    )
                else -> SynResult.failure(cursor.error("expected unary operator"))
            }
        }
}
