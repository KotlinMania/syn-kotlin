// port-lint: source op.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
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

/** A binary operator. */
public sealed class BinOp : ToTokens {
 public data class Add(val token: Plus) : BinOp()
 public data class Sub(val token: Minus) : BinOp()
 public data class Mul(val token: Star) : BinOp()
 public data class Div(val token: Slash) : BinOp()
 public data class Rem(val token: Percent) : BinOp()
 public data class And(val token: AndAnd) : BinOp()
 public data class Or(val token: OrOr) : BinOp()
 public data class BitXor(val token: Caret) : BinOp()
 public data class BitAnd(val token: And) : BinOp()
 public data class BitOr(val token: Or) : BinOp()
 public data class Shl(val token: Shl) : BinOp()
 public data class Shr(val token: Shr) : BinOp()
 public data class Eq(val token: EqEq) : BinOp()
 public data class Lt(val token: Lt) : BinOp()
 public data class Le(val token: Le) : BinOp()
 public data class Ne(val token: Ne) : BinOp()
 public data class Ge(val token: Ge) : BinOp()
 public data class Gt(val token: Gt) : BinOp()
 public data class AddAssign(val token: PlusEq) : BinOp()
 public data class SubAssign(val token: MinusEq) : BinOp()
 public data class MulAssign(val token: StarEq) : BinOp()
 public data class DivAssign(val token: SlashEq) : BinOp()
 public data class RemAssign(val token: PercentEq) : BinOp()
 public data class BitXorAssign(val token: CaretEq) : BinOp()
 public data class BitAndAssign(val token: AndEq) : BinOp()
 public data class BitOrAssign(val token: OrEq) : BinOp()
 public data class ShlAssign(val token: ShlEq) : BinOp()
 public data class ShrAssign(val token: ShrEq) : BinOp()

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
 public data class Deref(val token: Star) : UnOp()
 public data class NotOp(val token: Not) : UnOp()
 public data class Neg(val token: Minus) : UnOp()

 override fun toTokens(tokens: TokenStream) {
 when (this) {
 is Deref -> token.toTokens(tokens)
 is NotOp -> token.toTokens(tokens)
 is Neg -> token.toTokens(tokens)
 }
 }
}
