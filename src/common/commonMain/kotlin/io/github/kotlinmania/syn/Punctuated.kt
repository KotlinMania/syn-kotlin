// port-lint: source punctuated.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

internal typealias RawPair<T, P> = kotlin.Pair<T, P>

/**
 * Base class for strongly-typed punctuated sequences.
 *
 * Each subclass is a concrete named type for a specific element and
 * punctuation shape, preserving strong typing across the Swift Export bridge.
 */
public sealed class SynPunctuated :
    ToTokens,
    Iterable<ToTokens> {
    public typealias Item = ToTokens
    public typealias Output = ToTokens

    internal val inner: MutableList<RawPair<ToTokens, ToTokens>> = mutableListOf()
    internal var last: ToTokens? = null

    protected constructor()
    protected constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) {
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
        val value = requireNotNull(last) { "cannot push punctuation if empty or already has trailing punctuation" }
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

    /**
     * Clears the sequence of all values and punctuation, making it empty.
     */
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

    internal fun pairsIterator(): Iterator<RawPair<ToTokens, ToTokens?>> =
        sequence {
            for ((v, p) in inner) yield(v to p)
            val tail = last
            if (tail != null) yield(tail to null)
        }.iterator()

    internal fun pairsList(): List<RawPair<ToTokens, ToTokens?>> = pairsIterator().asSequence().toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SynPunctuated) return false
        return inner == other.inner && last == other.last
    }

    override fun hashCode(): Int = inner.hashCode() * 31 + (last?.hashCode() ?: 0)

    override fun toString(): String = inner.map { it.first.toString() }.joinToString(", ", "[", "]")

    public fun eq(other: SynPunctuated): Boolean = equals(other)

    public fun hash(): Int = hashCode()

    public fun fmt(): String = toString()

    public fun index(index: Int): ToTokens =
        if (index + 1 == len()) {
            last ?: inner[index].first
        } else {
            inner[index].first
        }

    public fun indexMut(index: Int): ToTokens = index(index)

    internal fun setRaw(index: Int, value: ToTokens) {
        val currentLast = last
        if (index < inner.size) {
            inner[index] = inner[index].copy(first = value)
        } else if (index == inner.size && currentLast != null) {
            last = value
        } else {
            throw IndexOutOfBoundsException("index: $index")
        }
    }
}

public class VariantList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Variant? = super.inner.firstOrNull()?.first as? Variant ?: super.last as? Variant

    public fun last(): Variant? = super.last as? Variant ?: super.inner.lastOrNull()?.first as? Variant

    public operator fun get(index: Int): Variant = super.index(index) as Variant

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

    public operator fun set(index: Int, value: Variant) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (Variant) -> Variant) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (Variant) -> Variant = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): VariantList =
        VariantList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Variant) to copyPunct(p) }, super.last?.let { copyValue(it as Variant) })
}

public class FieldList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Field? = super.inner.firstOrNull()?.first as? Field ?: super.last as? Field

    public fun last(): Field? = super.last as? Field ?: super.inner.lastOrNull()?.first as? Field

    public operator fun get(index: Int): Field = super.index(index) as Field

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

    public operator fun set(index: Int, value: Field) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (Field) -> Field) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (Field) -> Field = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FieldList =
        FieldList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Field) to copyPunct(p) }, super.last?.let { copyValue(it as Field) })
}

public class FnArgList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): FnArg? = super.inner.firstOrNull()?.first as? FnArg ?: super.last as? FnArg

    public fun last(): FnArg? = super.last as? FnArg ?: super.inner.lastOrNull()?.first as? FnArg

    public operator fun get(index: Int): FnArg = super.index(index) as FnArg

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

    public operator fun set(index: Int, value: FnArg) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (FnArg) -> FnArg) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (FnArg) -> FnArg = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FnArgList =
        FnArgList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as FnArg) to copyPunct(p) }, super.last?.let { copyValue(it as FnArg) })
}

public class ExprList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Expr? = super.inner.firstOrNull()?.first as? Expr ?: super.last as? Expr

    public fun last(): Expr? = super.last as? Expr ?: super.inner.lastOrNull()?.first as? Expr

    public operator fun get(index: Int): Expr = super.index(index) as Expr

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

    public operator fun set(index: Int, value: Expr) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (Expr) -> Expr) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (Expr) -> Expr = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): ExprList =
        ExprList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Expr) to copyPunct(p) }, super.last?.let { copyValue(it as Expr) })

    public companion object {
        public fun parseTerminated(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<Expr>,
            punctParse: (ParseStream) -> SynResult<ToTokens>,
        ): SynResult<ExprList> =
            parseTerminatedWith(input, parser, punctParse)

        public fun parseTerminatedWith(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<Expr>,
            punctParse: (ParseStream) -> SynResult<ToTokens>,
        ): SynResult<ExprList> {
            val list = ExprList()
            while (true) {
                if (input.isEmpty()) break
                val value = parser(input).getOrElse { return SynResult.failure(it) }
                list.pushValue(value)
                if (input.isEmpty()) break
                val punct = punctParse(input).getOrElse { return SynResult.failure(it) }
                list.pushPunct(punct)
            }
            return SynResult.success(list)
        }

        public fun parseSeparatedNonempty(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<Expr>,
            punctPeek: Peek,
            punctParse: (ParseStream) -> SynResult<ToTokens>,
        ): SynResult<ExprList> =
            parseSeparatedNonemptyWith(input, parser, punctPeek, punctParse)

        public fun parseSeparatedNonemptyWith(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<Expr>,
            punctPeek: Peek,
            punctParse: (ParseStream) -> SynResult<ToTokens>,
        ): SynResult<ExprList> {
            val list = ExprList()
            while (true) {
                val value = parser(input).getOrElse { return SynResult.failure(it) }
                list.pushValue(value)
                if (!input.peek(punctPeek)) break
                val punct = punctParse(input).getOrElse { return SynResult.failure(it) }
                list.pushPunct(punct)
            }
            return SynResult.success(list)
        }
    }
}

public class PatList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Pat? = super.inner.firstOrNull()?.first as? Pat ?: super.last as? Pat

    public fun last(): Pat? = super.last as? Pat ?: super.inner.lastOrNull()?.first as? Pat

    public operator fun get(index: Int): Pat = super.index(index) as Pat

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

    public operator fun set(index: Int, value: Pat) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (Pat) -> Pat) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (Pat) -> Pat = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): PatList =
        PatList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Pat) to copyPunct(p) }, super.last?.let { copyValue(it as Pat) })
}

public class FieldPatList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): FieldPat? = super.inner.firstOrNull()?.first as? FieldPat ?: super.last as? FieldPat

    public fun last(): FieldPat? = super.last as? FieldPat ?: super.inner.lastOrNull()?.first as? FieldPat

    public operator fun get(index: Int): FieldPat = super.index(index) as FieldPat

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

    public operator fun set(index: Int, value: FieldPat) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (FieldPat) -> FieldPat) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (FieldPat) -> FieldPat = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FieldPatList =
        FieldPatList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as FieldPat) to copyPunct(p) }, super.last?.let { copyValue(it as FieldPat) })
}

public class FieldValueList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): FieldValue? = super.inner.firstOrNull()?.first as? FieldValue ?: super.last as? FieldValue

    public fun last(): FieldValue? = super.last as? FieldValue ?: super.inner.lastOrNull()?.first as? FieldValue

    public operator fun get(index: Int): FieldValue = super.index(index) as FieldValue

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

    public operator fun set(index: Int, value: FieldValue) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (FieldValue) -> FieldValue) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (FieldValue) -> FieldValue = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): FieldValueList =
        FieldValueList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as FieldValue) to copyPunct(p) }, super.last?.let { copyValue(it as FieldValue) })
}

public class GenericParamList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): GenericParam? = super.inner.firstOrNull()?.first as? GenericParam ?: super.last as? GenericParam

    public fun last(): GenericParam? = super.last as? GenericParam ?: super.inner.lastOrNull()?.first as? GenericParam

    public operator fun get(index: Int): GenericParam = super.index(index) as GenericParam

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

    public operator fun set(index: Int, value: GenericParam) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (GenericParam) -> GenericParam) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (GenericParam) -> GenericParam = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): GenericParamList =
        GenericParamList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as GenericParam) to copyPunct(p) }, super.last?.let { copyValue(it as GenericParam) })
}

public class GenericArgumentList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): GenericArgument? = super.inner.firstOrNull()?.first as? GenericArgument ?: super.last as? GenericArgument

    public fun last(): GenericArgument? = super.last as? GenericArgument ?: super.inner.lastOrNull()?.first as? GenericArgument

    public operator fun get(index: Int): GenericArgument = super.index(index) as GenericArgument

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

    public operator fun set(index: Int, value: GenericArgument) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (GenericArgument) -> GenericArgument) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (GenericArgument) -> GenericArgument = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): GenericArgumentList =
        GenericArgumentList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as GenericArgument) to copyPunct(p) }, super.last?.let { copyValue(it as GenericArgument) })
}

public class LifetimeList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): Lifetime? = super.inner.firstOrNull()?.first as? Lifetime ?: super.last as? Lifetime

    public fun last(): Lifetime? = super.last as? Lifetime ?: super.inner.lastOrNull()?.first as? Lifetime

    public operator fun get(index: Int): Lifetime = super.index(index) as Lifetime

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

    public operator fun set(index: Int, value: Lifetime) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (Lifetime) -> Lifetime) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (Lifetime) -> Lifetime = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): LifetimeList =
        LifetimeList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as Lifetime) to copyPunct(p) }, super.last?.let { copyValue(it as Lifetime) })
}

public class TypeParamBoundList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): TypeParamBound? = super.inner.firstOrNull()?.first as? TypeParamBound ?: super.last as? TypeParamBound

    public fun last(): TypeParamBound? = super.last as? TypeParamBound ?: super.inner.lastOrNull()?.first as? TypeParamBound

    public operator fun get(index: Int): TypeParamBound = super.index(index) as TypeParamBound

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

    public operator fun set(index: Int, value: TypeParamBound) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (TypeParamBound) -> TypeParamBound) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (TypeParamBound) -> TypeParamBound = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): TypeParamBoundList =
        TypeParamBoundList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as TypeParamBound) to copyPunct(p) }, super.last?.let { copyValue(it as TypeParamBound) })
}

public class CapturedParamList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): CapturedParam? = super.inner.firstOrNull()?.first as? CapturedParam ?: super.last as? CapturedParam

    public fun last(): CapturedParam? = super.last as? CapturedParam ?: super.inner.lastOrNull()?.first as? CapturedParam

    public operator fun get(index: Int): CapturedParam = super.index(index) as CapturedParam

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

    public operator fun set(index: Int, value: CapturedParam) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (CapturedParam) -> CapturedParam) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (CapturedParam) -> CapturedParam = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): CapturedParamList =
        CapturedParamList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as CapturedParam) to copyPunct(p) }, super.last?.let { copyValue(it as CapturedParam) })
}

public class WherePredicateList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): WherePredicate? = super.inner.firstOrNull()?.first as? WherePredicate ?: super.last as? WherePredicate

    public fun last(): WherePredicate? = super.last as? WherePredicate ?: super.inner.lastOrNull()?.first as? WherePredicate

    public operator fun get(index: Int): WherePredicate = super.index(index) as WherePredicate

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

    public operator fun set(index: Int, value: WherePredicate) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (WherePredicate) -> WherePredicate) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (WherePredicate) -> WherePredicate = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): WherePredicateList =
        WherePredicateList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as WherePredicate) to copyPunct(p) }, super.last?.let { copyValue(it as WherePredicate) })
}

public class PathSegmentList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): PathSegment? = super.inner.firstOrNull()?.first as? PathSegment ?: super.last as? PathSegment

    public fun last(): PathSegment? = super.last as? PathSegment ?: super.inner.lastOrNull()?.first as? PathSegment

    public operator fun get(index: Int): PathSegment = super.index(index) as PathSegment

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

    public operator fun set(index: Int, value: PathSegment) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (PathSegment) -> PathSegment) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (PathSegment) -> PathSegment = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): PathSegmentList =
        PathSegmentList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as PathSegment) to copyPunct(p) }, super.last?.let { copyValue(it as PathSegment) })
}

public class UseTreeList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): UseTree? = super.inner.firstOrNull()?.first as? UseTree ?: super.last as? UseTree

    public fun last(): UseTree? = super.last as? UseTree ?: super.inner.lastOrNull()?.first as? UseTree

    public operator fun get(index: Int): UseTree = super.index(index) as UseTree

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

    public operator fun set(index: Int, value: UseTree) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (UseTree) -> UseTree) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (UseTree) -> UseTree = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): UseTreeList =
        UseTreeList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as UseTree) to copyPunct(p) }, super.last?.let { copyValue(it as UseTree) })
}

public class BareFnArgList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): BareFnArg? = super.inner.firstOrNull()?.first as? BareFnArg ?: super.last as? BareFnArg

    public fun last(): BareFnArg? = super.last as? BareFnArg ?: super.inner.lastOrNull()?.first as? BareFnArg

    public operator fun get(index: Int): BareFnArg = super.index(index) as BareFnArg

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

    public operator fun set(index: Int, value: BareFnArg) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (BareFnArg) -> BareFnArg) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (BareFnArg) -> BareFnArg = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): BareFnArgList =
        BareFnArgList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as BareFnArg) to copyPunct(p) }, super.last?.let { copyValue(it as BareFnArg) })
}

public class SynTypeList : SynPunctuated {
    public constructor() : super()
    internal constructor(values: List<RawPair<ToTokens, ToTokens>>, trailing: ToTokens?) : super(values, trailing)

    public fun first(): SynType? = super.inner.firstOrNull()?.first as? SynType ?: super.last as? SynType

    public fun last(): SynType? = super.last as? SynType ?: super.inner.lastOrNull()?.first as? SynType

    public operator fun get(index: Int): SynType = super.index(index) as SynType

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

    public operator fun set(index: Int, value: SynType) {
        super.setRaw(index, value)
    }

    public fun mapValuesInPlace(transform: (SynType) -> SynType) {
        for (i in 0 until size) this[i] = transform(this[i])
    }

    public fun copy(copyValue: (SynType) -> SynType = { it }, copyPunct: (ToTokens) -> ToTokens = { it }): SynTypeList =
        SynTypeList(super.inner.mapTo(mutableListOf()) { (v, p) -> copyValue(v as SynType) to copyPunct(p) }, super.last?.let { copyValue(it as SynType) })
}

internal class GenericPunctuatedList(
    values: List<RawPair<ToTokens, ToTokens>>,
    trailing: ToTokens?,
) : SynPunctuated(values, trailing)

/**
 * **A punctuated sequence of syntax tree nodes of type `T` separated by
 * punctuation of type `P`.**
 *
 * Refer to the [module documentation] for details about punctuated sequences.
 *
 * [module documentation]: self
 */
public class Punctuated<T, P> private constructor(
    private val innerList: MutableList<RawPair<T, P>>,
    private var lastValue: LastValue<T>?,
) : Iterable<T> {
    private data class LastValue<T>(
        val value: T,
    )

    public constructor() : this(mutableListOf(), null)

    /**
     * A single syntax tree node of type `T` followed by its trailing punctuation
     * of type `P` if any.
     *
     * Refer to the [module documentation] for details about punctuated sequences.
     *
     * [module documentation]: self
     */
    public sealed class Pair<T, P> {
        public abstract fun intoValue(): T

        public abstract fun value(): T

        /**
         * Mutably borrows the syntax tree node from this punctuated pair.
         */
        public fun valueMut(): T = value()

        public abstract fun punct(): P?

        public fun punctMut(): P? = punct()

        /**
         * Produces this punctuated pair as a tuple of syntax tree node and
         * optional following punctuation.
         */
        public fun intoTuple(): RawPair<T, P?> = value() to punct()

        public fun cloned(
            copyValue: (T) -> T,
            copyPunct: (P) -> P,
        ): Pair<T, P> =
            when (this) {
                is Punctuated -> Punctuated(copyValue(value()), copyPunct(punct()))
                is End -> End(copyValue(value()))
            }

        public class Punctuated<T, P>(
            private val itemValue: T,
            private val itemPunct: P,
        ) : Pair<T, P>() {
            override fun intoValue(): T = itemValue

            override fun value(): T = itemValue

            override fun punct(): P = itemPunct

            override fun equals(other: Any?): Boolean =
                other is Punctuated<*, *> && other.itemValue == itemValue && other.itemPunct == itemPunct

            override fun hashCode(): Int = itemValue.hashCode() * 31 + itemPunct.hashCode()
        }

        public class End<T, P>(
            private val itemValue: T,
        ) : Pair<T, P>() {
            override fun intoValue(): T = itemValue

            override fun value(): T = itemValue

            override fun punct(): P? = null

            override fun equals(other: Any?): Boolean =
                other is End<*, *> && other.itemValue == itemValue

            override fun hashCode(): Int = itemValue.hashCode()
        }

        public companion object {
            public fun <T, P> new(
                value: T,
                punctuation: P?,
            ): Pair<T, P> =
                if (punctuation == null) {
                    End(value)
                } else {
                    Punctuated(value, punctuation)
                }
        }
    }

    internal interface IterTrait<T> : Iterator<T> {
        fun nextBack(): T?

        fun sizeHint(): RawPair<Int, Int>

        fun len(): Int

        fun cloneBox(): IterTrait<T>
    }

    internal interface IterMutTrait<T> : Iterator<T> {
        fun nextBack(): T?

        fun sizeHint(): RawPair<Int, Int>

        fun len(): Int
    }

    // Compile-time destructor analysis, manual destruction suppression, and custom destructor behavior have no Kotlin equivalent.
    private class Cursor<E>(
        private val entries: List<E>,
        private var front: Int = 0,
        private var back: Int = entries.size,
    ) {
        fun hasNext(): Boolean = front < back

        fun next(): E {
            if (!hasNext()) throw NoSuchElementException()
            return entries[front++]
        }

        fun nextBack(): E? =
            if (hasNext()) {
                entries[--back]
            } else {
                null
            }

        fun len(): Int = back - front

        fun sizeHint(): RawPair<Int, Int> = len() to len()

        fun copy(): Cursor<E> = Cursor(entries, front, back)
    }

    /**
     * An iterator over borrowed pairs of type `Pair<&T, &P>`.
     *
     * Refer to the [module documentation] for details about punctuated sequences.
     *
     * [module documentation]: self
     */
    public class Pairs<T, P> private constructor(
        private val cursor: Cursor<Pair<T, P>>,
    ) : Iterator<Pair<T, P>>,
        Iterable<Pair<T, P>> {
        public constructor(entries: List<Pair<T, P>>) : this(Cursor(entries))

        override fun iterator(): Iterator<Pair<T, P>> = clone()

        override fun hasNext(): Boolean = cursor.hasNext()

        override fun next(): Pair<T, P> {
            if (!hasNext()) throw NoSuchElementException()
            return cursor.next()
        }

        public fun nextBack(): Pair<T, P>? = cursor.nextBack()

        public fun sizeHint(): RawPair<Int, Int> = cursor.sizeHint()

        public fun len(): Int = cursor.len()

        public fun clone(): Pairs<T, P> = Pairs<T, P>(cursor.copy())
    }

    /**
     * An iterator over mutably borrowed pairs of type `Pair<&mut T, &mut P>`.
     *
     * Refer to the [module documentation] for details about punctuated sequences.
     *
     * [module documentation]: self
     */
    public class PairsMut<T, P> private constructor(
        private val cursor: Cursor<Pair<T, P>>,
    ) : Iterator<Pair<T, P>>,
        Iterable<Pair<T, P>> {
        public constructor(entries: List<Pair<T, P>>) : this(Cursor(entries))

        override fun iterator(): Iterator<Pair<T, P>> = clone()

        override fun hasNext(): Boolean = cursor.hasNext()

        override fun next(): Pair<T, P> {
            if (!hasNext()) throw NoSuchElementException()
            return cursor.next()
        }

        public fun nextBack(): Pair<T, P>? = cursor.nextBack()

        public fun sizeHint(): RawPair<Int, Int> = cursor.sizeHint()

        public fun len(): Int = cursor.len()

        public fun clone(): PairsMut<T, P> = PairsMut<T, P>(cursor.copy())
    }

    /**
     * An iterator over owned pairs of type `Pair<T, P>`.
     *
     * Refer to the [module documentation] for details about punctuated sequences.
     *
     * [module documentation]: self
     */
    public class IntoPairs<T, P> private constructor(
        private val cursor: Cursor<Pair<T, P>>,
    ) : Iterator<Pair<T, P>>,
        Iterable<Pair<T, P>> {
        public constructor(entries: List<Pair<T, P>>) : this(Cursor(entries))

        override fun iterator(): Iterator<Pair<T, P>> = this

        override fun hasNext(): Boolean = cursor.hasNext()

        override fun next(): Pair<T, P> {
            if (!hasNext()) throw NoSuchElementException()
            return cursor.next()
        }

        public fun nextBack(): Pair<T, P>? = cursor.nextBack()

        public fun sizeHint(): RawPair<Int, Int> = cursor.sizeHint()

        public fun len(): Int = cursor.len()

        public fun clone(): IntoPairs<T, P> = IntoPairs<T, P>(cursor.copy())
    }

    internal class PrivateIter<T> private constructor(
        private val cursor: Cursor<T>,
    ) : IterTrait<T> {
        constructor(entries: List<T>) : this(Cursor(entries))

        override fun hasNext(): Boolean = cursor.hasNext()

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return cursor.next()
        }

        override fun nextBack(): T? = cursor.nextBack()

        override fun sizeHint(): RawPair<Int, Int> = cursor.sizeHint()

        override fun len(): Int = cursor.len()

        override fun cloneBox(): IterTrait<T> = clone()

        fun clone(): PrivateIter<T> = PrivateIter<T>(cursor.copy())
    }

    internal class PrivateIterMut<T> private constructor(
        private val cursor: Cursor<T>,
    ) : IterMutTrait<T> {
        constructor(entries: List<T>) : this(Cursor(entries))

        override fun hasNext(): Boolean = cursor.hasNext()

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return cursor.next()
        }

        override fun nextBack(): T? = cursor.nextBack()

        override fun sizeHint(): RawPair<Int, Int> = cursor.sizeHint()

        override fun len(): Int = cursor.len()

        fun clone(): PrivateIterMut<T> = PrivateIterMut<T>(cursor.copy())
    }

    /**
     * An iterator over borrowed values of type `&T`.
     *
     * Refer to the [module documentation] for details about punctuated sequences.
     *
     * [module documentation]: self
     */
    public class Iter<T> private constructor(
        private val inner: IterTrait<T>,
    ) : Iterator<T>,
        Iterable<T> {
        public constructor(entries: List<T>) : this(PrivateIter<T>(entries))

        override fun iterator(): Iterator<T> = clone()

        override fun hasNext(): Boolean = inner.hasNext()

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return inner.next()
        }

        public fun nextBack(): T? = inner.nextBack()

        public fun sizeHint(): RawPair<Int, Int> = inner.sizeHint()

        public fun len(): Int = inner.len()

        public fun clone(): Iter<T> = Iter<T>(inner.cloneBox())
    }

    /**
     * An iterator over mutably borrowed values of type `&mut T`.
     *
     * Refer to the [module documentation] for details about punctuated sequences.
     *
     * [module documentation]: self
     */
    public class IterMut<T> private constructor(
        private val inner: PrivateIterMut<T>,
    ) : Iterator<T>,
        Iterable<T> {
        public constructor(entries: List<T>) : this(PrivateIterMut<T>(entries))

        override fun iterator(): Iterator<T> = clone()

        override fun hasNext(): Boolean = inner.hasNext()

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return inner.next()
        }

        public fun nextBack(): T? = inner.nextBack()

        public fun sizeHint(): RawPair<Int, Int> = inner.sizeHint()

        public fun len(): Int = inner.len()

        public fun clone(): IterMut<T> = IterMut<T>(inner.clone())
    }

    /**
     * An iterator over owned values of type `T`.
     *
     * Refer to the [module documentation] for details about punctuated sequences.
     *
     * [module documentation]: self
     */
    public class IntoIter<T, P> private constructor(
        private val inner: IntoPairs<T, P>,
    ) : Iterator<T>,
        Iterable<T> {
        public constructor(entries: List<Pair<T, P>>) : this(IntoPairs<T, P>(entries))

        override fun iterator(): Iterator<T> = this

        override fun hasNext(): Boolean = inner.hasNext()

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return inner.next().intoValue()
        }

        public fun nextBack(): T? = inner.nextBack()?.intoValue()

        public fun sizeHint(): RawPair<Int, Int> = inner.sizeHint()

        public fun len(): Int = inner.len()

        public fun clone(): IntoIter<T, P> = IntoIter<T, P>(inner.clone())
    }

    public companion object {
        public fun <T, P> new(): Punctuated<T, P> = Punctuated()

        public fun <T, P> default(): Punctuated<T, P> = new()

        public fun <T, P> fromPairs(
            pairs: Iterable<Pair<T, P>>,
        ): Punctuated<T, P> {
            val result = Punctuated<T, P>()
            doExtend(result, pairs.iterator())
            return result
        }

        public fun <T, P> fromIter(
            elements: Iterable<T>,
            defaultPunctuation: () -> P,
        ): Punctuated<T, P> {
            val result = Punctuated<T, P>()
            result.extend(elements, defaultPunctuation)
            return result
        }

        public fun <T, P> fromIter(
            pairs: Sequence<Pair<T, P>>,
        ): Punctuated<T, P> {
            val result = Punctuated<T, P>()
            doExtend(result, pairs.iterator())
            return result
        }

        public fun <T, P> parseTerminated(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<T>,
            punctParse: (ParseStream) -> SynResult<P>,
        ): SynResult<Punctuated<T, P>> =
            parseTerminatedWith(input, parser, punctParse)

        public fun <T, P> parseTerminatedWith(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<T>,
            punctParse: (ParseStream) -> SynResult<P>,
        ): SynResult<Punctuated<T, P>> {
            val punctuated = Punctuated<T, P>()
            while (true) {
                if (input.isEmpty()) break
                val value = parser(input).getOrElse { return SynResult.failure(it) }
                punctuated.pushValue(value)
                if (input.isEmpty()) break
                val punct = punctParse(input).getOrElse { return SynResult.failure(it) }
                punctuated.pushPunct(punct)
            }
            return SynResult.success(punctuated)
        }

        public fun <T, P> parseSeparatedNonempty(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<T>,
            punctPeek: Peek,
            punctParse: (ParseStream) -> SynResult<P>,
        ): SynResult<Punctuated<T, P>> =
            parseSeparatedNonemptyWith(input, parser, punctPeek, punctParse)

        public fun <T, P> parseSeparatedNonemptyWith(
            input: ParseStream,
            parser: (ParseStream) -> SynResult<T>,
            punctPeek: Peek,
            punctParse: (ParseStream) -> SynResult<P>,
        ): SynResult<Punctuated<T, P>> {
            val punctuated = Punctuated<T, P>()
            while (true) {
                val value = parser(input).getOrElse { return SynResult.failure(it) }
                punctuated.pushValue(value)
                if (!input.peek(punctPeek)) break
                val punct = punctParse(input).getOrElse { return SynResult.failure(it) }
                punctuated.pushPunct(punct)
            }
            return SynResult.success(punctuated)
        }
    }

    public fun isEmpty(): Boolean = innerList.isEmpty() && lastValue == null

    public fun len(): Int = innerList.size + if (lastValue == null) 0 else 1

    public val size: Int get() = len()

    public fun first(): T? =
        if (innerList.isNotEmpty()) innerList[0].first else lastValue?.value

    /**
     * Mutably borrows the first element in this sequence.
     */
    public fun firstMut(): T? = first()

    public fun last(): T? {
        val tail = lastValue
        return if (tail != null) tail.value else innerList.lastOrNull()?.first
    }

    /**
     * Mutably borrows the last element in this sequence.
     */
    public fun lastMut(): T? = last()

    public operator fun get(index: Int): T {
        val tail = lastValue
        return when {
            index in innerList.indices -> innerList[index].first
            index == innerList.size && tail != null -> tail.value
            else -> throw IndexOutOfBoundsException("index: $index")
        }
    }

    public fun getOrNull(index: Int): T? =
        if (index < 0) {
            null
        } else {
            if (index in innerList.indices) {
                innerList[index].first
            } else if (index == innerList.size) {
                lastValue?.value
            } else {
                null
            }
        }

    /**
     * Mutably borrows the element at the given index.
     */
    public fun getMut(index: Int): T? = getOrNull(index)

    /** Clears the sequence of all values and punctuation, making it empty. */
    public fun clear() {
        innerList.clear()
        lastValue = null
    }

    public fun trailingPunct(): Boolean = lastValue == null && !isEmpty()

    public fun emptyOrTrailing(): Boolean = lastValue == null

    public fun pushValue(value: T) {
        require(emptyOrTrailing()) { "cannot push value if missing trailing punctuation" }
        lastValue = LastValue(value)
    }

    public fun pushPunct(punctuation: P) {
        val value = requireNotNull(lastValue) { "cannot push punctuation if empty or already has trailing punctuation" }.value
        innerList.add(value to punctuation)
        lastValue = null
    }

    public fun push(value: T, defaultPunctuation: () -> P) {
        if (!emptyOrTrailing()) pushPunct(defaultPunctuation())
        pushValue(value)
    }

    /**
     * Inserts an element at position `index`.
     *
     * # Panics
     *
     * Panics if `index` is greater than the number of elements previously in
     * this punctuated sequence.
     */
    public fun insert(index: Int, value: T, defaultPunctuation: () -> P) {
        require(index <= len()) { "Punctuated::insert: index out of range" }
        if (index == len()) {
            push(value, defaultPunctuation)
        } else {
            innerList.add(index, value to defaultPunctuation())
        }
    }

    public fun add(value: T) {
        require(emptyOrTrailing()) { "Punctuated.add requires trailing punctuation before appending another value" }
        pushValue(value)
    }

    public fun pop(): Pair<T, P>? {
        val tail = lastValue
        return if (tail != null) {
            lastValue = null
            Pair.End(tail.value)
        } else {
            innerList.removeLastOrNull()?.let { (value, punctuation) -> Pair.Punctuated(value, punctuation) }
        }
    }

    /**
     * Removes the trailing punctuation from this punctuated sequence, or
     * `None` if there isn't any.
     */
    public fun popPunct(): P? {
        if (lastValue != null) return null
        val removed = innerList.removeLastOrNull()
        if (removed != null) {
            lastValue = LastValue(removed.first)
            return removed.second
        }
        return null
    }

    /**
     * Returns an iterator over the contents of this sequence as borrowed
     * punctuated pairs.
     */
    public fun pairs(): Pairs<T, P> = Pairs(pairEntries())

    /**
     * Returns an iterator over the contents of this sequence as mutably
     * borrowed punctuated pairs.
     */
    public fun pairsMut(): PairsMut<T, P> = PairsMut(pairEntries())

    public fun toList(): List<T> = valueEntries()

    override fun iterator(): Iterator<T> = iter()

    /**
     * Returns an iterator over borrowed syntax tree nodes of type `&T`.
     */
    public fun iter(): Iter<T> = Iter(valueEntries())

    /**
     * Returns an iterator over mutably borrowed syntax tree nodes of type
     * `&mut T`.
     */
    public fun iterMut(): IterMut<T> = IterMut(valueEntries())

    public fun intoIter(): IntoIter<T, P> = IntoIter(pairEntries())

    public fun intoValue(): T? = if (len() == 1) first() else null

    public fun value(): T? = intoValue()

    public fun punct(index: Int): P? = innerList.getOrNull(index)?.second

    public fun punctMut(index: Int): P? = punct(index)

    /**
     * Returns an iterator over the contents of this sequence as owned
     * punctuated pairs.
     */
    public fun intoPairs(): IntoPairs<T, P> = IntoPairs(pairEntries())

    public fun <R> fold(initial: R, operation: (acc: R, T) -> R): R {
        var acc = initial
        for (item in this) acc = operation(acc, item)
        return acc
    }

    public operator fun set(index: Int, value: T) {
        val currentLast = lastValue
        if (index < innerList.size) {
            innerList[index] = innerList[index].copy(first = value)
        } else if (index == innerList.size && currentLast != null) {
            lastValue = LastValue(value)
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

    public fun extend(elements: Sequence<Pair<T, P>>, defaultPunctuation: () -> P) {
        if (!emptyOrTrailing()) {
            pushPunct(defaultPunctuation())
        }
        doExtend(this, elements.iterator())
    }

    public fun extendPairs(elements: Iterable<Pair<T, P>>, defaultPunctuation: () -> P) {
        if (!emptyOrTrailing()) {
            pushPunct(defaultPunctuation())
        }
        doExtend(this, elements.iterator())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Punctuated<*, *>) return false
        return innerList == other.innerList && lastValue == other.lastValue
    }

    override fun hashCode(): Int = innerList.hashCode() * 31 + (lastValue?.hashCode() ?: 0)

    override fun toString(): String = toList().joinToString(", ", "[", "]")

    public fun copy(copyValue: (T) -> T, copyPunctuation: (P) -> P): Punctuated<T, P> =
        Punctuated<T, P>(
            innerList = innerList.mapTo(mutableListOf()) { (v, p) -> copyValue(v) to copyPunctuation(p) },
            lastValue = lastValue?.let { LastValue(copyValue(it.value)) },
        )

    public fun clone(copyValue: (T) -> T, copyPunctuation: (P) -> P): Punctuated<T, P> =
        copy(copyValue, copyPunctuation)

    public fun cloneFrom(
        other: Punctuated<T, P>,
        copyValue: (T) -> T,
        copyPunctuation: (P) -> P,
    ) {
        innerList.clear()
        innerList.addAll(other.innerList.map { (value, punctuation) -> copyValue(value) to copyPunctuation(punctuation) })
        lastValue = other.lastValue?.let { LastValue(copyValue(it.value)) }
    }

    private fun pairEntries(): List<Pair<T, P>> =
        buildList {
            for ((value, punctuation) in innerList) add(Pair.Punctuated(value, punctuation))
            val tail = lastValue
            if (tail != null) add(Pair.End(tail.value))
        }

    private fun valueEntries(): List<T> =
        buildList {
            for ((value, _) in innerList) add(value)
            val tail = lastValue
            if (tail != null) add(tail.value)
        }
}

private fun <T, P> doExtend(
    punctuated: Punctuated<T, P>,
    pairs: Iterator<Punctuated.Pair<T, P>>,
) {
    var noMore = false
    for (pair in pairs) {
        check(!noMore) { "punctuated extended with items after a Pair::End" }
        when (pair) {
            is Punctuated.Pair.End -> {
                punctuated.pushValue(pair.value())
                noMore = true
            }
            is Punctuated.Pair.Punctuated -> {
                punctuated.pushValue(pair.value())
                punctuated.pushPunct(pair.punct())
            }
        }
    }
}

internal fun <T> emptyPunctuatedIter(): Iterator<T> = emptyList<T>().iterator()

internal fun <T> emptyPunctuatedIterMut(): Iterator<T> = emptyPunctuatedIter()

public fun <T : ToTokens, P : ToTokens> Punctuated.Pair<T, P>.toTokens(tokens: TokenStream) {
    value().toTokens(tokens)
    punct()?.toTokens(tokens)
}

public fun <T : ToTokens, P : ToTokens> Punctuated<T, P>.toTokens(tokens: TokenStream) {
    for (pair in pairs()) pair.toTokens(tokens)
}

internal fun <T : ToTokens, P : ToTokens> Punctuated<T, P>.toSynPunctuated(): SynPunctuated {
    val entries = pairs().iterator().asSequence().toList()
    val punctuated =
        entries.mapNotNull { pair ->
            when (pair) {
                is Punctuated.Pair.Punctuated -> pair.value() to pair.punct()
                is Punctuated.Pair.End -> null
            }
        }
    val trailing =
        entries.lastOrNull()?.let { pair ->
            if (pair is Punctuated.Pair.End) pair.value() else null
        }
    return GenericPunctuatedList(punctuated, trailing)
}

internal fun <T, P, V> fold(
    punctuated: Punctuated<T, P>,
    folder: V,
    transform: (V, T) -> T,
): Punctuated<T, P> =
    punctuated.copy(copyValue = { transform(folder, it) }, copyPunctuation = { it })
