// port-lint: source punctuated.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * Base class for strongly-typed punctuated sequences.
 *
 * Replaces the generic `Punctuated<T, P>` which Swift Export could not
 * bridge. Each subclass is a concrete named type for a specific element
 * type, preserving strong typing without exposing type parameters to the
 * Swift Export bridge.
 */
public sealed class SynPunctuated :
    ToTokens,
    Iterable<ToTokens> {
    internal val inner: MutableList<Pair<ToTokens, ToTokens>> = mutableListOf()
    internal var last: ToTokens? = null

    protected constructor()
    protected constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) {
        inner.addAll(values)
        last = trailing
    }

    public fun isEmpty(): Boolean = inner.isEmpty() && last == null

    public fun len(): Int = inner.size + if (last == null) 0 else 1

    public val size: Int get() = len()

    public fun trailingPunct(): Boolean = last == null && !isEmpty()

    public fun emptyOrTrailing(): Boolean = last == null

    internal fun pushValueRaw(value: ToTokens) {
        require(emptyOrTrailing()) { "cannot push value if missing trailing punctuation" }
        last = value
    }

    internal fun pushPunctRaw(punctuation: ToTokens) {
        val value = last
        require(value != null) { "cannot push punctuation if empty or already has trailing punctuation" }
        inner.add(value to punctuation)
        last = null
    }

    internal fun popRaw(): ToTokens? {
        val tail = last
        return if (tail != null) {
            last = null
            tail
        } else {
            inner.removeLastOrNull()?.first
        }
    }

    internal fun popPunctRaw(): ToTokens? {
        if (last != null) return null
        val removed = inner.removeLastOrNull()
        if (removed != null) {
            last = removed.first
            return removed.second
        }
        return null
    }

    public fun clear() {
        inner.clear()
        last = null
    }

    override fun iterator(): Iterator<ToTokens> =
        sequence {
            for ((value, _) in inner) yield(value)
            val tail = last
            if (tail != null) yield(tail)
        }.iterator()

    override fun toTokens(tokens: TokenStream) {
        for ((value, punctuation) in inner) {
            value.toTokens(tokens)
            punctuation.toTokens(tokens)
        }
        last?.toTokens(tokens)
    }

    internal fun pairsIterator(): Iterator<Pair<ToTokens, ToTokens?>> =
        sequence {
            for ((v, p) in inner) yield(v to p)
            val tail = last
            if (tail != null) yield(tail to null)
        }.iterator()

    internal fun pairsList(): List<Pair<ToTokens, ToTokens?>> = pairsIterator().asSequence().toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SynPunctuated) return false
        return inner == other.inner && last == other.last
    }

    override fun hashCode(): Int = inner.hashCode() * 31 + (last?.hashCode() ?: 0)

    override fun toString(): String = inner.map { it.first.toString() }.joinToString(", ", "[", "]")
}

public class VariantList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Variant? = super.inner.firstOrNull()?.first as? Variant ?: super.last as? Variant

    public fun last(): Variant? = super.last as? Variant ?: super.inner.lastOrNull()?.first as? Variant

    public operator fun get(index: Int): Variant = super.inner[index].first as Variant

    public fun toList(): List<Variant> = map { it as Variant }

    public fun pushValue(value: Variant) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: Variant, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): Variant? = popRaw() as? Variant

    public fun copy(copyValue: (Variant) -> Variant = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): VariantList =
        VariantList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Variant) to copyPunct(p) }, super.last?.let { copyValue(it as Variant) })
}

public class FieldList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Field? = super.inner.firstOrNull()?.first as? Field ?: super.last as? Field

    public fun last(): Field? = super.last as? Field ?: super.inner.lastOrNull()?.first as? Field

    public operator fun get(index: Int): Field = super.inner[index].first as Field

    public fun toList(): List<Field> = map { it as Field }

    public fun pushValue(value: Field) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: Field, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): Field? = popRaw() as? Field

    public fun copy(copyValue: (Field) -> Field = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FieldList =
        FieldList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Field) to copyPunct(p) }, super.last?.let { copyValue(it as Field) })
}

public class FnArgList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): FnArg? = super.inner.firstOrNull()?.first as? FnArg ?: super.last as? FnArg

    public fun last(): FnArg? = super.last as? FnArg ?: super.inner.lastOrNull()?.first as? FnArg

    public operator fun get(index: Int): FnArg = super.inner[index].first as FnArg

    public fun toList(): List<FnArg> = map { it as FnArg }

    public fun pushValue(value: FnArg) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: FnArg, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): FnArg? = popRaw() as? FnArg

    public fun copy(copyValue: (FnArg) -> FnArg = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FnArgList =
        FnArgList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as FnArg) to copyPunct(p) }, super.last?.let { copyValue(it as FnArg) })
}

public class ExprList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Expr? = super.inner.firstOrNull()?.first as? Expr ?: super.last as? Expr

    public fun last(): Expr? = super.last as? Expr ?: super.inner.lastOrNull()?.first as? Expr

    public operator fun get(index: Int): Expr = super.inner[index].first as Expr

    public fun toList(): List<Expr> = map { it as Expr }

    public fun pushValue(value: Expr) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: Expr, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): Expr? = popRaw() as? Expr

    public fun copy(copyValue: (Expr) -> Expr = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): ExprList =
        ExprList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Expr) to copyPunct(p) }, super.last?.let { copyValue(it as Expr) })
}

public class PatList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Pat? = super.inner.firstOrNull()?.first as? Pat ?: super.last as? Pat

    public fun last(): Pat? = super.last as? Pat ?: super.inner.lastOrNull()?.first as? Pat

    public operator fun get(index: Int): Pat = super.inner[index].first as Pat

    public fun toList(): List<Pat> = map { it as Pat }

    public fun pushValue(value: Pat) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: Pat, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): Pat? = popRaw() as? Pat

    public fun copy(copyValue: (Pat) -> Pat = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): PatList =
        PatList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Pat) to copyPunct(p) }, super.last?.let { copyValue(it as Pat) })
}

public class FieldPatList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): FieldPat? = super.inner.firstOrNull()?.first as? FieldPat ?: super.last as? FieldPat

    public fun last(): FieldPat? = super.last as? FieldPat ?: super.inner.lastOrNull()?.first as? FieldPat

    public operator fun get(index: Int): FieldPat = super.inner[index].first as FieldPat

    public fun toList(): List<FieldPat> = map { it as FieldPat }

    public fun pushValue(value: FieldPat) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: FieldPat, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): FieldPat? = popRaw() as? FieldPat

    public fun copy(copyValue: (FieldPat) -> FieldPat = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FieldPatList =
        FieldPatList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as FieldPat) to copyPunct(p) }, super.last?.let { copyValue(it as FieldPat) })
}

public class FieldValueList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): FieldValue? = super.inner.firstOrNull()?.first as? FieldValue ?: super.last as? FieldValue

    public fun last(): FieldValue? = super.last as? FieldValue ?: super.inner.lastOrNull()?.first as? FieldValue

    public operator fun get(index: Int): FieldValue = super.inner[index].first as FieldValue

    public fun toList(): List<FieldValue> = map { it as FieldValue }

    public fun pushValue(value: FieldValue) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: FieldValue, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): FieldValue? = popRaw() as? FieldValue

    public fun copy(copyValue: (FieldValue) -> FieldValue = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FieldValueList =
        FieldValueList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as FieldValue) to copyPunct(p) }, super.last?.let { copyValue(it as FieldValue) })
}

public class GenericParamList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): GenericParam? = super.inner.firstOrNull()?.first as? GenericParam ?: super.last as? GenericParam

    public fun last(): GenericParam? = super.last as? GenericParam ?: super.inner.lastOrNull()?.first as? GenericParam

    public operator fun get(index: Int): GenericParam = super.inner[index].first as GenericParam

    public fun toList(): List<GenericParam> = map { it as GenericParam }

    public fun pushValue(value: GenericParam) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: GenericParam, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): GenericParam? = popRaw() as? GenericParam

    public fun copy(copyValue: (GenericParam) -> GenericParam = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): GenericParamList =
        GenericParamList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as GenericParam) to copyPunct(p) }, super.last?.let { copyValue(it as GenericParam) })
}

public class GenericArgumentList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): GenericArgument? = super.inner.firstOrNull()?.first as? GenericArgument ?: super.last as? GenericArgument

    public fun last(): GenericArgument? = super.last as? GenericArgument ?: super.inner.lastOrNull()?.first as? GenericArgument

    public operator fun get(index: Int): GenericArgument = super.inner[index].first as GenericArgument

    public fun toList(): List<GenericArgument> = map { it as GenericArgument }

    public fun pushValue(value: GenericArgument) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: GenericArgument, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): GenericArgument? = popRaw() as? GenericArgument

    public fun copy(copyValue: (GenericArgument) -> GenericArgument = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): GenericArgumentList =
        GenericArgumentList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as GenericArgument) to copyPunct(p) }, super.last?.let { copyValue(it as GenericArgument) })
}

public class LifetimeList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Lifetime? = super.inner.firstOrNull()?.first as? Lifetime ?: super.last as? Lifetime

    public fun last(): Lifetime? = super.last as? Lifetime ?: super.inner.lastOrNull()?.first as? Lifetime

    public operator fun get(index: Int): Lifetime = super.inner[index].first as Lifetime

    public fun toList(): List<Lifetime> = map { it as Lifetime }

    public fun pushValue(value: Lifetime) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: Lifetime, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): Lifetime? = popRaw() as? Lifetime

    public fun copy(copyValue: (Lifetime) -> Lifetime = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): LifetimeList =
        LifetimeList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Lifetime) to copyPunct(p) }, super.last?.let { copyValue(it as Lifetime) })
}

public class TypeParamBoundList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): TypeParamBound? = super.inner.firstOrNull()?.first as? TypeParamBound ?: super.last as? TypeParamBound

    public fun last(): TypeParamBound? = super.last as? TypeParamBound ?: super.inner.lastOrNull()?.first as? TypeParamBound

    public operator fun get(index: Int): TypeParamBound = super.inner[index].first as TypeParamBound

    public fun toList(): List<TypeParamBound> = map { it as TypeParamBound }

    public fun pushValue(value: TypeParamBound) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: TypeParamBound, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): TypeParamBound? = popRaw() as? TypeParamBound

    public fun copy(copyValue: (TypeParamBound) -> TypeParamBound = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): TypeParamBoundList =
        TypeParamBoundList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as TypeParamBound) to copyPunct(p) }, super.last?.let { copyValue(it as TypeParamBound) })
}

public class CapturedParamList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): CapturedParam? = super.inner.firstOrNull()?.first as? CapturedParam ?: super.last as? CapturedParam

    public fun last(): CapturedParam? = super.last as? CapturedParam ?: super.inner.lastOrNull()?.first as? CapturedParam

    public operator fun get(index: Int): CapturedParam = super.inner[index].first as CapturedParam

    public fun toList(): List<CapturedParam> = map { it as CapturedParam }

    public fun pushValue(value: CapturedParam) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: CapturedParam, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): CapturedParam? = popRaw() as? CapturedParam

    public fun copy(copyValue: (CapturedParam) -> CapturedParam = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): CapturedParamList =
        CapturedParamList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as CapturedParam) to copyPunct(p) }, super.last?.let { copyValue(it as CapturedParam) })
}

public class WherePredicateList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): WherePredicate? = super.inner.firstOrNull()?.first as? WherePredicate ?: super.last as? WherePredicate

    public fun last(): WherePredicate? = super.last as? WherePredicate ?: super.inner.lastOrNull()?.first as? WherePredicate

    public operator fun get(index: Int): WherePredicate = super.inner[index].first as WherePredicate

    public fun toList(): List<WherePredicate> = map { it as WherePredicate }

    public fun pushValue(value: WherePredicate) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: WherePredicate, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): WherePredicate? = popRaw() as? WherePredicate

    public fun copy(copyValue: (WherePredicate) -> WherePredicate = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): WherePredicateList =
        WherePredicateList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as WherePredicate) to copyPunct(p) }, super.last?.let { copyValue(it as WherePredicate) })
}

public class PathSegmentList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): PathSegment? = super.inner.firstOrNull()?.first as? PathSegment ?: super.last as? PathSegment

    public fun last(): PathSegment? = super.last as? PathSegment ?: super.inner.lastOrNull()?.first as? PathSegment

    public operator fun get(index: Int): PathSegment = super.inner[index].first as PathSegment

    public fun toList(): List<PathSegment> = map { it as PathSegment }

    public fun pushValue(value: PathSegment) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: PathSegment, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): PathSegment? = popRaw() as? PathSegment

    public fun copy(copyValue: (PathSegment) -> PathSegment = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): PathSegmentList =
        PathSegmentList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as PathSegment) to copyPunct(p) }, super.last?.let { copyValue(it as PathSegment) })
}

public class UseTreeList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): UseTree? = super.inner.firstOrNull()?.first as? UseTree ?: super.last as? UseTree

    public fun last(): UseTree? = super.last as? UseTree ?: super.inner.lastOrNull()?.first as? UseTree

    public operator fun get(index: Int): UseTree = super.inner[index].first as UseTree

    public fun toList(): List<UseTree> = map { it as UseTree }

    public fun pushValue(value: UseTree) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: UseTree, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): UseTree? = popRaw() as? UseTree

    public fun copy(copyValue: (UseTree) -> UseTree = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): UseTreeList =
        UseTreeList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as UseTree) to copyPunct(p) }, super.last?.let { copyValue(it as UseTree) })
}

public class BareFnArgList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): BareFnArg? = super.inner.firstOrNull()?.first as? BareFnArg ?: super.last as? BareFnArg

    public fun last(): BareFnArg? = super.last as? BareFnArg ?: super.inner.lastOrNull()?.first as? BareFnArg

    public operator fun get(index: Int): BareFnArg = super.inner[index].first as BareFnArg

    public fun toList(): List<BareFnArg> = map { it as BareFnArg }

    public fun pushValue(value: BareFnArg) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: BareFnArg, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): BareFnArg? = popRaw() as? BareFnArg

    public fun copy(copyValue: (BareFnArg) -> BareFnArg = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): BareFnArgList =
        BareFnArgList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as BareFnArg) to copyPunct(p) }, super.last?.let { copyValue(it as BareFnArg) })
}

public class SynTypeList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<Pair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): SynType? = super.inner.firstOrNull()?.first as? SynType ?: super.last as? SynType

    public fun last(): SynType? = super.last as? SynType ?: super.inner.lastOrNull()?.first as? SynType

    public operator fun get(index: Int): SynType = super.inner[index].first as SynType

    public fun toList(): List<SynType> = map { it as SynType }

    public fun pushValue(value: SynType) {
        pushValueRaw(value)
    }

    public fun pushPunct(punctuation: ToTokens) {
        pushPunctRaw(punctuation)
    }

    public fun push(value: SynType, defaultPunctuation: () -> ToTokens) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun pop(): SynType? = popRaw() as? SynType

    public fun copy(copyValue: (SynType) -> SynType = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): SynTypeList =
        SynTypeList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as SynType) to copyPunct(p) }, super.last?.let { copyValue(it as SynType) })
}

/**
 * Internal two-type-parameter punctuated sequence used by the parser.
 * Kept internal so Swift Export never sees the two type parameters.
 */
internal class Punctuated<T : ToTokens, P : ToTokens> private constructor(
    private val innerList: MutableList<Pair<T, P>>,
    private var lastValue: T?,
) : ToTokens,
    Iterable<T> {
    public constructor() : this(mutableListOf(), null)

    public companion object {
        public fun <T : ToTokens, P : ToTokens> new(): Punctuated<T, P> = Punctuated()

        public fun <T : ToTokens, P : ToTokens> parseTerminatedWith(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<T>,
            punctParse: Parse<P>,
        ): SynResult<Punctuated<T, P>> {
            val punctuated = Punctuated<T, P>()
            while (true) {
                if (input.isEmpty()) break
                val value = parser(input).getOrElse { return SynResult.failure(it) }
                punctuated.pushValue(value)
                if (input.isEmpty()) break
                val punct = punctParse.parse(input).getOrElse { return SynResult.failure(it) }
                punctuated.pushPunct(punct)
            }
            return SynResult.success(punctuated)
        }
    }

    public fun isEmpty(): Boolean = innerList.isEmpty() && lastValue == null

    public fun len(): Int = innerList.size + if (lastValue == null) 0 else 1

    public val size: Int get() = len()

    public fun first(): T? = innerList.firstOrNull()?.first ?: lastValue

    public fun last(): T? = lastValue ?: innerList.lastOrNull()?.first

    public operator fun get(index: Int): T =
        getOrNull(index)
            ?: throw IndexOutOfBoundsException("index: $index")

    public fun getOrNull(index: Int): T? =
        if (index < 0) {
            null
        } else {
            innerList.getOrNull(index)?.first ?: if (index == innerList.size) lastValue else null
        }

    public fun trailingPunct(): Boolean = lastValue == null && !isEmpty()

    public fun emptyOrTrailing(): Boolean = lastValue == null

    public fun pushValue(value: T) {
        require(emptyOrTrailing()) { "cannot push value if missing trailing punctuation" }
        lastValue = value
    }

    public fun pushPunct(punctuation: P) {
        val value = lastValue
        require(value != null) { "cannot push punctuation if empty or already has trailing punctuation" }
        innerList.add(value to punctuation)
        lastValue = null
    }

    public fun push(value: T, defaultPunctuation: () -> P) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    public fun add(value: T) {
        require(emptyOrTrailing()) { "Punctuated.add requires trailing punctuation before appending another value" }
        pushValue(value)
    }

    public fun pop(): T? {
        val tail = lastValue
        return if (tail != null) {
            lastValue = null
            tail
        } else {
            innerList.removeLastOrNull()?.first
        }
    }

    public fun popPunct(): P? {
        if (lastValue != null) return null
        val removed = innerList.removeLastOrNull()
        if (removed != null) {
            lastValue = removed.first
            return removed.second
        }
        return null
    }

    public fun pairs(): Iterator<Pair<T, P?>> =
        sequence {
            for ((v, p) in innerList) yield(v to p)
            val tail = lastValue
            if (tail != null) yield(tail to null)
        }.iterator()

    public fun toList(): List<T> = iterator().asSequence().toList()

    override fun iterator(): Iterator<T> =
        sequence {
            for ((v, _) in innerList) yield(v)
            val tail = lastValue
            if (tail != null) yield(tail)
        }.iterator()

    public fun iter(): Iterator<T> = iterator()

    public fun intoValue(): T? = if (len() == 1) first() else null

    public fun value(): T? = intoValue()

    public fun punct(index: Int): P? = innerList.getOrNull(index)?.second

    internal fun intoPairs(): List<Pair<T, P?>> =
        sequence {
            for ((v, p) in innerList) yield(v to p)
            val tail = lastValue
            if (tail != null) yield(tail to null)
        }.toList()

    public fun <R> fold(initial: R, operation: (acc: R, T) -> R): R {
        var acc = initial
        for (item in this) acc = operation(acc, item)
        return acc
    }

    public operator fun set(index: Int, value: T) {
        if (index < innerList.size) {
            innerList[index] = innerList[index].copy(first = value)
        } else if (index == innerList.size && lastValue != null) {
            lastValue = value
        } else {
            throw IndexOutOfBoundsException("index: $index")
        }
    }

    public fun fromIterable(elements: Iterable<T>, defaultPunctuation: () -> P): Punctuated<T, P> {
        val result = Punctuated<T, P>()
        for (e in elements) result.push(e, defaultPunctuation)
        return result
    }

    public fun extend(elements: Iterable<T>, defaultPunctuation: () -> P) {
        for (e in elements) push(e, defaultPunctuation)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Punctuated<*, *>) return false
        return this.toList() == other.toList()
    }

    override fun hashCode(): Int = toList().hashCode()

    override fun toString(): String = toList().joinToString(", ", "[", "]")

    public fun copy(copyValue: (T) -> T = { it }, copyPunctuation: (P) -> P = { it }): Punctuated<T, P> =
        Punctuated(
            innerList = innerList.mapTo(mutableListOf()) { (v, p) -> copyValue(v) to copyPunctuation(p) },
            lastValue = lastValue?.let(copyValue),
        )

    override fun toTokens(tokens: TokenStream) {
        for ((v, p) in innerList) {
            v.toTokens(tokens)
            p.toTokens(tokens)
        }
        lastValue?.toTokens(tokens)
    }

    internal fun toSynPunctuated(): SynPunctuated = throw UnsupportedOperationException("use specific subclass")
}
