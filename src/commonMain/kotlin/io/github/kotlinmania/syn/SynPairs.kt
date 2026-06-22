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
    public val eqToken: Eq,
    public val expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        eqToken.toTokens(tokens)
        expr.toTokens(tokens)
    }
}

public data class EqSynType(
    public val eqToken: Eq,
    public val type: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        eqToken.toTokens(tokens)
        type.toTokens(tokens)
    }
}

public data class ElseExpr(
    public val elseToken: Else,
    public val expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        elseToken.toTokens(tokens)
        expr.toTokens(tokens)
    }
}

public data class IfExpr(
    public val ifToken: If,
    public val expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ifToken.toTokens(tokens)
        expr.toTokens(tokens)
    }
}

public data class AndLifetime(
    public val andToken: And,
    public val lifetime: Lifetime?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        andToken.toTokens(tokens)
        lifetime?.toTokens(tokens)
    }
}

public data class AsIdent(
    public val asToken: As,
    public val ident: Ident,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        asToken.toTokens(tokens)
        ident.toTokens(tokens)
    }
}

public data class PatColon(
    public val pat: Pat,
    public val colonToken: Colon,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        pat.toTokens(tokens)
        colonToken.toTokens(tokens)
    }
}
