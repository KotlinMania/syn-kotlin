// port-lint: source generics.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Where

/** Generic parameters attached to a declaration. */
public data class Generics(
    public var ltToken: Lt? = null,
    public var params: GenericParamList = GenericParamList(),
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
            whereClause = WhereClause(Where(Span.callSite()), WherePredicateList())
        }
        return whereClause!!
    }

    public fun splitForImpl(): SplitForImpl {
        val implGenerics = Generics(ltToken, GenericParamList(), gtToken)
        val typeGenerics = Generics(ltToken, GenericParamList(), gtToken)
        val turbofish = Turbofish(ltToken, GenericArgumentList(), gtToken)
        for ((value, punct) in params.pairsList()) {
            when (value) {
                is GenericParam.LifetimeParam -> {
                    implGenerics.params.push(value) { Comma(Span.callSite()) }
                    typeGenerics.params.push(value) { Comma(Span.callSite()) }
                }
                is GenericParam.TypeParam -> {
                    implGenerics.params.push(value) { Comma(Span.callSite()) }
                    turbofish.params.pushValue(GenericArgument.TypeArg(SynType.Path(null, Path.from(value.ident))))
                }
                is GenericParam.ConstParam -> {
                    implGenerics.params.push(value) { Comma(Span.callSite()) }
                }
            }
        }
        return SplitForImpl(implGenerics, typeGenerics, turbofish)
    }

    override fun toTokens(tokens: TokenStream) {
        ltToken?.toTokens(tokens)
        params.toTokens(tokens)
        gtToken?.toTokens(tokens)
        whereClause?.toTokens(tokens)
    }

    public fun copy(): Generics =
        Generics(
            ltToken = ltToken,
            gtToken = gtToken,
            whereClause = whereClause?.copy(),
            params = params.copy({ it.deepCopy() }, { it }),
        )
}

public data class SplitForImpl(
    public val implGenerics: Generics,
    public val typeGenerics: Generics,
    public val turbofish: Turbofish,
)

public data class Turbofish(
    public val ltToken: Lt?,
    public val params: GenericArgumentList,
    public val gtToken: Gt?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ltToken?.toTokens(tokens)
        params.toTokens(tokens)
        gtToken?.toTokens(tokens)
    }
}

public sealed class GenericParam : ToTokens {
    public abstract fun deepCopy(): GenericParam

    public data class LifetimeParam(
        public var attrs: List<Attribute>,
        public var lifetime: Lifetime,
        public var colonToken: Colon?,
        public var bounds: LifetimeList,
    ) : GenericParam() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
            colonToken?.toTokens(tokens)
            for ((bound, plus) in bounds.pairsList()) {
                plus?.toTokens(tokens)
                bound.toTokens(tokens)
            }
        }

        override fun deepCopy(): LifetimeParam =
            LifetimeParam(attrs.map { it.deepCopy() }, lifetime.deepCopy(), colonToken, bounds.copy({ it.deepCopy() }, { it }))
    }

    public data class TypeParam(
        public var attrs: List<Attribute>,
        public var ident: Ident,
        public var colonToken: Colon?,
        public var bounds: TypeParamBoundList,
        public var eqToken: Eq?,
        public var default: SynType?,
    ) : GenericParam() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
            colonToken?.toTokens(tokens)
            for ((bound, plus) in bounds.pairsList()) {
                bound.toTokens(tokens)
                plus?.toTokens(tokens)
            }
            eqToken?.toTokens(tokens)
        }

        override fun deepCopy(): TypeParam =
            TypeParam(attrs.map { it.deepCopy() }, ident.copy(), colonToken, bounds.copy({ it.deepCopy() }, { it }), eqToken, default)
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
        }

        override fun deepCopy(): ConstParam =
            ConstParam(attrs.map { it.deepCopy() }, constToken, ident.copy(), colonToken, ty, eqToken, default)
    }
}

public data class WhereClause(
    public val whereToken: Where,
    public val predicates: WherePredicateList,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        whereToken.toTokens(tokens)
        predicates.toTokens(tokens)
    }

    public fun deepCopy(): WhereClause =
        WhereClause(whereToken, predicates.copy({ it.deepCopy() }, { it }))
}

public sealed class WherePredicate : ToTokens {
    public abstract fun deepCopy(): WherePredicate

    public data class TypePredicate(
        public val boundedTy: SynType,
        public val colonToken: Colon,
        public val bounds: TypeParamBoundList,
    ) : WherePredicate() {
        override fun toTokens(tokens: TokenStream) {
            boundedTy.toTokens(tokens)
            colonToken.toTokens(tokens)
            for ((bound, plus) in bounds.pairsList()) {
                bound.toTokens(tokens)
                plus?.toTokens(tokens)
            }
        }

        override fun deepCopy(): TypePredicate =
            TypePredicate(boundedTy.deepCopy(), colonToken, bounds.copy({ it.deepCopy() }, { it }))
    }

    public data class LifetimePredicate(
        public val lifetime: Lifetime,
        public val colonToken: Colon?,
        public val bounds: LifetimeList,
    ) : WherePredicate() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
            colonToken?.toTokens(tokens)
            for ((bound, plus) in bounds.pairsList()) {
                plus?.toTokens(tokens)
                bound.toTokens(tokens)
            }
        }

        override fun deepCopy(): LifetimePredicate =
            LifetimePredicate(lifetime.deepCopy(), colonToken, bounds.copy({ it.deepCopy() }, { it }))
    }
}

public sealed class TypeParamBound : ToTokens {
    public abstract fun deepCopy(): TypeParamBound

    public data class Trait(
        val parenToken: io.github.kotlinmania.syn.token.Paren?,
        val modifier: TraitBoundModifier,
        val lifetimes: BoundLifetimes?,
        val path: Path,
    ) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            if (parenToken != null) {
                parenToken.surround(tokens) { inner ->
                    modifier.toTokens(inner)
                    lifetimes?.toTokens(inner)
                    path.toTokens(inner)
                }
            } else {
                modifier.toTokens(tokens)
                lifetimes?.toTokens(tokens)
                path.toTokens(tokens)
            }
        }

        override fun deepCopy(): Trait = Trait(parenToken, modifier, lifetimes?.deepCopy(), path.deepCopy())
    }

    public data class LifetimeBound(
        val lifetime: Lifetime,
    ) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
        }

        override fun deepCopy(): LifetimeBound = LifetimeBound(lifetime.deepCopy())
    }

    public data class PreciseCapture(
        val useToken: io.github.kotlinmania.syn.token.Use,
        val ltToken: Lt,
        val params: CapturedParamList,
        val gtToken: Gt,
    ) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            useToken.toTokens(tokens)
            ltToken.toTokens(tokens)
            params.toTokens(tokens)
            gtToken.toTokens(tokens)
        }

        override fun deepCopy(): PreciseCapture = PreciseCapture(useToken, ltToken, params.copy({ it.deepCopy() }, { it }), gtToken)
    }

    public data class Verbatim(
        val tokens: TokenStream,
    ) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }

        override fun deepCopy(): Verbatim = this
    }
}

public sealed class TraitBoundModifier : ToTokens {
    public abstract fun deepCopy(): TraitBoundModifier

    public data object None : TraitBoundModifier() {
        override fun toTokens(tokens: TokenStream) {}

        override fun deepCopy(): None = this
    }

    public data class Maybe(
        val token: io.github.kotlinmania.syn.token.Question,
    ) : TraitBoundModifier() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }

        override fun deepCopy(): Maybe = this
    }
}

public data class BoundLifetimes(
    val forToken: io.github.kotlinmania.syn.token.For,
    val ltToken: Lt,
    val lifetimes: GenericParamList,
    val gtToken: Gt,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        forToken.toTokens(tokens)
        ltToken.toTokens(tokens)
        lifetimes.toTokens(tokens)
        gtToken.toTokens(tokens)
    }

    public fun deepCopy(): BoundLifetimes = BoundLifetimes(forToken, ltToken, lifetimes.copy({ it.deepCopy() }, { it }), gtToken)
}

public sealed class CapturedParam : ToTokens {
    public abstract fun deepCopy(): CapturedParam

    public data class Lifetime(
        val lifetime: io.github.kotlinmania.syn.Lifetime,
    ) : CapturedParam() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
        }

        override fun deepCopy(): Lifetime = Lifetime(lifetime.deepCopy())
    }

    public data class Ident(
        val ident: io.github.kotlinmania.procmacro2.Ident,
    ) : CapturedParam() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
        }

        override fun deepCopy(): Ident = Ident(ident.copy())
    }
}
