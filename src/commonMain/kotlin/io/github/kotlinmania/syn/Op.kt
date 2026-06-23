// port-lint: source op.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/** A binary operator. */
public sealed class BinOp : ToTokens {
    public data class Add(
        val token: io.github.kotlinmania.syn.token.Plus,
    ) : BinOp()

    public data class Sub(
        val token: io.github.kotlinmania.syn.token.Minus,
    ) : BinOp()

    public data class Mul(
        val token: io.github.kotlinmania.syn.token.Star,
    ) : BinOp()

    public data class Div(
        val token: io.github.kotlinmania.syn.token.Slash,
    ) : BinOp()

    public data class Rem(
        val token: io.github.kotlinmania.syn.token.Percent,
    ) : BinOp()

    public data class And(
        val token: io.github.kotlinmania.syn.token.AndAnd,
    ) : BinOp()

    public data class Or(
        val token: io.github.kotlinmania.syn.token.OrOr,
    ) : BinOp()

    public data class BitXor(
        val token: io.github.kotlinmania.syn.token.Caret,
    ) : BinOp()

    public data class BitAnd(
        val token: io.github.kotlinmania.syn.token.And,
    ) : BinOp()

    public data class BitOr(
        val token: io.github.kotlinmania.syn.token.Or,
    ) : BinOp()

    public data class Shl(
        val token: io.github.kotlinmania.syn.token.Shl,
    ) : BinOp()

    public data class Shr(
        val token: io.github.kotlinmania.syn.token.Shr,
    ) : BinOp()

    public data class Eq(
        val token: io.github.kotlinmania.syn.token.EqEq,
    ) : BinOp()

    public data class Lt(
        val token: io.github.kotlinmania.syn.token.Lt,
    ) : BinOp()

    public data class Le(
        val token: io.github.kotlinmania.syn.token.Le,
    ) : BinOp()

    public data class Ne(
        val token: io.github.kotlinmania.syn.token.Ne,
    ) : BinOp()

    public data class Ge(
        val token: io.github.kotlinmania.syn.token.Ge,
    ) : BinOp()

    public data class Gt(
        val token: io.github.kotlinmania.syn.token.Gt,
    ) : BinOp()

    public data class AddAssign(
        val token: io.github.kotlinmania.syn.token.PlusEq,
    ) : BinOp()

    public data class SubAssign(
        val token: io.github.kotlinmania.syn.token.MinusEq,
    ) : BinOp()

    public data class MulAssign(
        val token: io.github.kotlinmania.syn.token.StarEq,
    ) : BinOp()

    public data class DivAssign(
        val token: io.github.kotlinmania.syn.token.SlashEq,
    ) : BinOp()

    public data class RemAssign(
        val token: io.github.kotlinmania.syn.token.PercentEq,
    ) : BinOp()

    public data class BitXorAssign(
        val token: io.github.kotlinmania.syn.token.CaretEq,
    ) : BinOp()

    public data class BitAndAssign(
        val token: io.github.kotlinmania.syn.token.AndEq,
    ) : BinOp()

    public data class BitOrAssign(
        val token: io.github.kotlinmania.syn.token.OrEq,
    ) : BinOp()

    public data class ShlAssign(
        val token: io.github.kotlinmania.syn.token.ShlEq,
    ) : BinOp()

    public data class ShrAssign(
        val token: io.github.kotlinmania.syn.token.ShrEq,
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
        val token: io.github.kotlinmania.syn.token.Star,
    ) : UnOp()

    public data class NotOp(
        val token: io.github.kotlinmania.syn.token.Not,
    ) : UnOp()

    public data class Neg(
        val token: io.github.kotlinmania.syn.token.Minus,
    ) : UnOp()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Deref -> token.toTokens(tokens)
            is NotOp -> token.toTokens(tokens)
            is Neg -> token.toTokens(tokens)
        }
    }
}

/** Parses a binary operator. */
public object BinOpParse : Parse<BinOp> {
    override fun parse(input: ParseStream): SynResult<BinOp> =
        when {
            input.peek(PlusEqPeek) -> input.parse(PlusEqParse).map(BinOp::AddAssign)
            input.peek(MinusEqPeek) -> input.parse(MinusEqParse).map(BinOp::SubAssign)
            input.peek(StarEqPeek) -> input.parse(StarEqParse).map(BinOp::MulAssign)
            input.peek(SlashEqPeek) -> input.parse(SlashEqParse).map(BinOp::DivAssign)
            input.peek(PercentEqPeek) -> input.parse(PercentEqParse).map(BinOp::RemAssign)
            input.peek(CaretEqPeek) -> input.parse(CaretEqParse).map(BinOp::BitXorAssign)
            input.peek(AndEqPeek) -> input.parse(AndEqParse).map(BinOp::BitAndAssign)
            input.peek(OrEqPeek) -> input.parse(OrEqParse).map(BinOp::BitOrAssign)
            input.peek(ShlEqPeek) -> input.parse(ShlEqParse).map(BinOp::ShlAssign)
            input.peek(ShrEqPeek) -> input.parse(ShrEqParse).map(BinOp::ShrAssign)
            input.peek(AndAndPeek) -> input.parse(AndAndParse).map(BinOp::And)
            input.peek(OrOrPeek) -> input.parse(OrOrParse).map(BinOp::Or)
            input.peek(ShlPeek) -> input.parse(ShlParse).map(BinOp::Shl)
            input.peek(ShrPeek) -> input.parse(ShrParse).map(BinOp::Shr)
            input.peek(EqEqPeek) -> input.parse(EqEqParse).map(BinOp::Eq)
            input.peek(LePeek) -> input.parse(LeParse).map(BinOp::Le)
            input.peek(NePeek) -> input.parse(NeParse).map(BinOp::Ne)
            input.peek(GePeek) -> input.parse(GeParse).map(BinOp::Ge)
            input.peek(PlusPeek) -> input.parse(PlusParse).map(BinOp::Add)
            input.peek(MinusPeek) -> input.parse(MinusParse).map(BinOp::Sub)
            input.peek(StarPeek) -> input.parse(StarParse).map(BinOp::Mul)
            input.peek(SlashPeek) -> input.parse(SlashParse).map(BinOp::Div)
            input.peek(PercentPeek) -> input.parse(PercentParse).map(BinOp::Rem)
            input.peek(CaretPeek) -> input.parse(CaretParse).map(BinOp::BitXor)
            input.peek(AndPeek) -> input.parse(AndParse).map(BinOp::BitAnd)
            input.peek(OrPeek) -> input.parse(OrParse).map(BinOp::BitOr)
            input.peek(LtPeek) -> input.parse(LtParse).map(BinOp::Lt)
            input.peek(GtPeek) -> input.parse(GtParse).map(BinOp::Gt)
            else -> SynResult.failure(input.error("expected binary operator"))
        }
}

/** Parses a unary operator by examining the next punctuation token. */
public object UnOpParse : Parse<UnOp> {
    override fun parse(input: ParseStream): SynResult<UnOp> =
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
