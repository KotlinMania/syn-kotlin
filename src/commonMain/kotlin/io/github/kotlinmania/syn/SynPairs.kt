// port-lint: source item.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.And
import io.github.kotlinmania.syn.token.As
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Else
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.If

public data class EqExpr(
    public var eqToken: Eq,
    public var expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        eqToken.toTokens(tokens)
        expr.toTokens(tokens)
    }

}

public data class EqSynType(
    public var eqToken: Eq,
    public var type: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        eqToken.toTokens(tokens)
        type.toTokens(tokens)
    }

}

public data class ElseExpr(
    public var elseToken: Else,
    public var expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        elseToken.toTokens(tokens)
        expr.toTokens(tokens)
    }

}

public data class IfExpr(
    public var ifToken: If,
    public var expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ifToken.toTokens(tokens)
        expr.toTokens(tokens)
    }

}

public data class AndLifetime(
    public var andToken: And,
    public var lifetime: Lifetime?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        andToken.toTokens(tokens)
        lifetime?.toTokens(tokens)
    }

}

public data class AsIdent(
    public var asToken: As,
    public var ident: Ident,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        asToken.toTokens(tokens)
        ident.toTokens(tokens)
    }

}

public data class IdentColon(
    public var ident: Ident,
    public var colonToken: io.github.kotlinmania.syn.token.Colon,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ident.toTokens(tokens)
        colonToken.toTokens(tokens)
    }

}

public data class PatColon(
    public var pat: Pat,
    public var colonToken: Colon,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        pat.toTokens(tokens)
        colonToken.toTokens(tokens)
    }

}
