// port-lint: source generics.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.syn.token.Where

/** Generic parameters attached to a declaration. */
public data class Generics(
    public val ltToken: Lt? = null,
    public val params: Punctuated<GenericParam, Comma> = Punctuated.new(),
    public val gtToken: Gt? = null,
    public val whereClause: WhereClause? = null,
)

public sealed class GenericParam {
    public data class LifetimeParam(val lifetime: Lifetime) : GenericParam()
    public data class TypeParam(val ident: Ident, val bounds: Punctuated<TypeParamBound, Plus>) : GenericParam()
    public data class ConstParam(val ident: Ident, val ty: Type) : GenericParam()
}

public data class WhereClause(
    public val whereToken: Where,
    public val predicates: Punctuated<WherePredicate, Comma>,
)

public sealed class WherePredicate {
    public data class TypePredicate(val boundedTy: Type, val bounds: Punctuated<TypeParamBound, Plus>) : WherePredicate()
    public data class LifetimePredicate(val lifetime: Lifetime, val bounds: Punctuated<Lifetime, Plus>) : WherePredicate()
}

public sealed class TypeParamBound {
    public data class Trait(val path: Path) : TypeParamBound()
    public data class LifetimeBound(val lifetime: Lifetime) : TypeParamBound()
}
