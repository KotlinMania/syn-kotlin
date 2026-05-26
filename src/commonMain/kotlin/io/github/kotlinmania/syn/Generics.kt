// port-lint: source generics.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.syn.token.Where
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import kotlin.native.HiddenFromObjC

/** Generic parameters attached to a declaration. */
@HiddenFromObjC
public data class Generics(
    public var ltToken: Lt? = null,
    public var params: Punctuated<GenericParam, Comma> = Punctuated.new(),
    public var gtToken: Gt? = null,
    public var whereClause: WhereClause? = null,
) : ToTokens {
    public companion object {
        public fun default(): Generics = Generics()
        public fun new(): Generics = Generics()
    }

    public fun lifetimes(): List<GenericParam.LifetimeParam> =
        params.toList().filterIsInstance<GenericParam.LifetimeParam>()

    public fun typeParams(): List<GenericParam.TypeParam> =
        params.toList().filterIsInstance<GenericParam.TypeParam>()

    public fun constParams(): List<GenericParam.ConstParam> =
        params.toList().filterIsInstance<GenericParam.ConstParam>()

    public fun makeWhereClause(): WhereClause {
        if (whereClause == null) {
            whereClause = WhereClause(Where(Span.callSite()), Punctuated.new())
        }
        return whereClause!!
    }

    public fun splitForImpl(): SplitForImpl {
        val implGenerics = Generics(ltToken, Punctuated.new(), gtToken)
        val typeGenerics = Generics(ltToken, Punctuated.new(), gtToken)
        val turbofish = Turbofish(ltToken, Punctuated.new(), gtToken)
        for (param in params.pairs()) {
            when (val value = param.value) {
                is GenericParam.LifetimeParam -> {
                    implGenerics.params.push(value, { Comma(Span.callSite()) })
                    typeGenerics.params.push(value, { Comma(Span.callSite()) })
                }
                is GenericParam.TypeParam -> {
                    implGenerics.params.push(value, { Comma(Span.callSite()) })
                    turbofish.params.push(GenericArgument.TypeArg(SynType.Path(null, value.ident.let { io.github.kotlinmania.syn.Path.from(it) })), { Comma(Span.callSite()) })
                }
                is GenericParam.ConstParam -> {
                    implGenerics.params.push(value, { Comma(Span.callSite()) })
                }
            }
        }
        return SplitForImpl(implGenerics, typeGenerics, turbofish)
    }

    override fun toTokens(tokens: TokenStream) {
        ltToken?.toTokens(tokens)
        for ((param, comma) in params.pairs()) {
            param.toTokens(tokens)
            comma?.toTokens(tokens)
        }
        gtToken?.toTokens(tokens)
        whereClause?.toTokens(tokens)
    }

    public fun copy(): Generics = Generics(
        ltToken = ltToken,
        params = params.copy({ it.copy() }, { it }),
        gtToken = gtToken,
        whereClause = whereClause?.copy(),
    )
}

public data class SplitForImpl(
    public val implGenerics: Generics,
    public val typeGenerics: Generics,
    public val turbofish: Turbofish,
)

public data class Turbofish(
    public val ltToken: Lt?,
    public val params: Punctuated<GenericArgument, Comma>,
    public val gtToken: Gt?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ltToken?.toTokens(tokens)
        for ((arg, comma) in params.pairs()) {
            arg.toTokens(tokens)
            comma?.toTokens(tokens)
        }
        gtToken?.toTokens(tokens)
    }
}

@HiddenFromObjC
public sealed class GenericParam : ToTokens {
    public data class LifetimeParam(
        public var attrs: List<Attribute>,
        public var lifetime: Lifetime,
        public var colonToken: Colon?,
        public var bounds: Punctuated<Lifetime, Plus>,
    ) : GenericParam() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
            colonToken?.toTokens(tokens)
            for ((bound, plus) in bounds.pairs()) {
                plus?.toTokens(tokens)
                bound.toTokens(tokens)
            }
        }

        public fun copy(): LifetimeParam =
            LifetimeParam(attrs.map { it.deepCopy() }, lifetime.deepCopy(), colonToken, bounds.copy({ it.deepCopy() }, { it }))
    }

    public data class TypeParam(
        public var attrs: List<Attribute>,
        public var ident: Ident,
        public var colonToken: Colon?,
        public var bounds: Punctuated<TypeParamBound, Plus>,
        public var eqToken: Eq?,
        public var default: SynType?,
    ) : GenericParam() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
            colonToken?.toTokens(tokens)
            for ((bound, plus) in bounds.pairs()) {
                bound.toTokens(tokens)
                plus?.toTokens(tokens)
            }
            eqToken?.toTokens(tokens)
            default?.toTokens(tokens)
        }

        public fun copy(): TypeParam =
            TypeParam(attrs.map { it.deepCopy() }, ident.copy(), colonToken, bounds.copy({ it.copy() }, { it }), eqToken, default?.copy())
    }

    public data class ConstParam(
        public var attrs: List<Attribute>,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var colonToken: Colon,
        public var ty: SynType,
        public var eqToken: Eq?,
        public var default: Expr?,
    ) : GenericParam() {
        override fun toTokens(tokens: TokenStream) {
            constToken.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            eqToken?.toTokens(tokens)
            default?.toTokens(tokens)
        }

        public fun copy(): ConstParam =
            ConstParam(attrs.map { it.deepCopy() }, constToken, ident.copy(), colonToken, ty.copy(), eqToken, default?.copy())
    }
}

@HiddenFromObjC
public data class WhereClause(
    public val whereToken: Where,
    public val predicates: Punctuated<WherePredicate, Comma>,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        whereToken.toTokens(tokens)
        for ((pred, comma) in predicates.pairs()) {
            pred.toTokens(tokens)
            comma?.toTokens(tokens)
        }
    }

    public fun copy(): WhereClause =
        WhereClause(whereToken, predicates.copy({ it.copy() }, { it }))
}

@HiddenFromObjC
public sealed class WherePredicate : ToTokens {
    public data class TypePredicate(
        public val boundedTy: SynType,
        public val colonToken: Colon,
        public val bounds: Punctuated<TypeParamBound, Plus>,
    ) : WherePredicate() {
        override fun toTokens(tokens: TokenStream) {
            boundedTy.toTokens(tokens)
            colonToken.toTokens(tokens)
            for ((bound, plus) in bounds.pairs()) {
                bound.toTokens(tokens)
                plus?.toTokens(tokens)
            }
        }

        public fun copy(): TypePredicate =
            TypePredicate(boundedTy.copy(), colonToken, bounds.copy({ it.copy() }, { it }))
    }

    public data class LifetimePredicate(
        public val lifetime: Lifetime,
        public val colonToken: Colon?,
        public val bounds: Punctuated<Lifetime, Plus>,
    ) : WherePredicate() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
            colonToken?.toTokens(tokens)
            for ((bound, plus) in bounds.pairs()) {
                plus?.toTokens(tokens)
                bound.toTokens(tokens)
            }
        }

        public fun copy(): LifetimePredicate =
            LifetimePredicate(lifetime.deepCopy(), colonToken, bounds.copy({ it.deepCopy() }, { it }))
    }
}

@HiddenFromObjC
public sealed class TypeParamBound : ToTokens {
    public data class Trait(val path: Path) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            path.toTokens(tokens)
        }

        public fun copy(): Trait = Trait(path.copy())
    }

    public data class LifetimeBound(val lifetime: Lifetime) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
        }

        public fun copy(): LifetimeBound = LifetimeBound(lifetime.deepCopy())
    }
}
