// port-lint: source generics.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.PathSep
import io.github.kotlinmania.syn.token.Where

/**
 * scopes and type parameters attached to a declaration of a function,
 * enum, trait, etc.
 *
 * This struct represents two distinct optional syntactic elements,
 * [generic parameters] and [where clause]. In some locations of the
 * grammar, there may be other tokens in between these two things.
 *
 * [generic parameters]: https://doc.rust-lang.org/stable/reference/items/generics.html#generic-parameters
 * [where clause]: https://doc.rust-lang.org/stable/reference/items/generics.html#where-clauses
 */
public data class Generics(
    public var ltToken: Lt? = null,
    public var params: GenericParamList = GenericParamList(),
    public var gtToken: Gt? = null,
    public var whereClause: WhereClause? = null,
) : ToTokens {
    public typealias Item = GenericParam

    public companion object {
        public fun default(): Generics = Generics()

        public fun new(): Generics = Generics()

        public fun parse(input: ParseStream): SynResult<Generics> {
            if (!input.peek(GenericsLtPeek)) return SynResult.success(default())

            val ltToken = GenericsLtParse.parse(input).getOrElse { return SynResult.failure(it) }
            val params = GenericParamList()
            while (!input.isEmpty() && !input.peek(GenericsGtPeek)) {
                val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
                val lookahead = input.lookahead1()
                val param =
                    when {
                        lookahead.peek(LifetimePeek) -> {
                            GenericParam.LifetimeParam.parseWithAttrs(attrs, input).getOrElse {
                                return SynResult.failure(it)
                            }
                        }

                        lookahead.peek(IdentPeek) -> {
                            GenericParam.TypeParam.parseWithAttrs(attrs, input).getOrElse {
                                return SynResult.failure(it)
                            }
                        }

                        lookahead.peek(ConstPeek) -> {
                            GenericParam.ConstParam.parseWithAttrs(attrs, input).getOrElse {
                                return SynResult.failure(it)
                            }
                        }

                        input.peek(UnderscorePeek) -> {
                            GenericParam.TypeParam(
                                attrs,
                                identFromUnderscore(UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) }),
                                null,
                                TypeParamBoundList(),
                                null,
                            )
                        }

                        else -> {
                            return SynResult.failure(lookahead.error())
                        }
                    }
                params.pushValue(param)
                if (input.peek(GenericsGtPeek)) break
                params.pushPunct(CommaParse.parse(input).getOrElse { return SynResult.failure(it) })
            }
            val gtToken = GenericsGtParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(Generics(ltToken, params, gtToken))
        }
    }

    /**
     * Iterator over the scope parameters in `self.params`.
     */
    public fun lifetimes(): Lifetimes =
        Lifetimes(params.toList())

    /**
     * Iterator over the scope parameters in `self.params`.
     */
    public fun lifetimesMut(): LifetimesMut =
        LifetimesMut(params.toList())

    /**
     * Iterator over the type parameters in `self.params`.
     */
    public fun typeParams(): TypeParams =
        TypeParams(params.toList())

    /**
     * Iterator over the type parameters in `self.params`.
     */
    public fun typeParamsMut(): TypeParamsMut =
        TypeParamsMut(params.toList())

    /**
     * Iterator over the constant parameters in `self.params`.
     */
    public fun constParams(): ConstParams =
        ConstParams(params.toList())

    /**
     * Iterator over the constant parameters in `self.params`.
     */
    public fun constParamsMut(): ConstParamsMut =
        ConstParamsMut(params.toList())

    /**
     * Initializes an empty `where`-clause if there is not one present already.
     */
    public fun makeWhereClause(): WhereClause {
        if (whereClause == null) {
            whereClause = WhereClause(Where(Span.callSite()), WherePredicateList())
        }
        return whereClause!!
    }

    /**
     * Split a type's generics into the pieces required for implementing a trait
     * for that type.
     *
     * ```kotlin
     * val (implGenerics, tyGenerics, whereClause) = generics.splitForImpl()
     * quote(
     *     "impl `#`implGenerics MyTrait for `#`name `#`tyGenerics `#`whereClause {}",
     *     "implGenerics" to implGenerics,
     *     "name" to name,
     *     "tyGenerics" to tyGenerics,
     *     "whereClause" to whereClause,
     * )
     * ```
     */
    public fun splitForImpl(): SplitForImpl =
        SplitForImpl(
            ImplGenerics(this),
            TypeGenerics(this),
            whereClause,
        )

    override fun toTokens(tokens: TokenStream) {
        if (params.isEmpty()) return
        (ltToken ?: Lt.default()).toTokens(tokens)
        printGenericParams(params, tokens)
        (gtToken ?: Gt.default()).toTokens(tokens)
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
    public var implGenerics: ImplGenerics,
    public var typeGenerics: TypeGenerics,
    public var whereClause: WhereClause?,
) {
    public val turbofish: Turbofish
        get() = typeGenerics.asTurbofish()
}

public class Lifetimes(
    private val params: List<GenericParam>,
) : Iterable<GenericParam.LifetimeParam> {
    private val iter = params.iterator()

    public fun next(): GenericParam.LifetimeParam? =
        iter.nextParamOfType()

    override fun iterator(): Iterator<GenericParam.LifetimeParam> =
        params.asSequence().filterIsInstance<GenericParam.LifetimeParam>().iterator()
}

public class LifetimesMut(
    private val params: List<GenericParam>,
) : Iterable<GenericParam.LifetimeParam> {
    private val iter = params.iterator()

    public fun next(): GenericParam.LifetimeParam? =
        iter.nextParamOfType()

    override fun iterator(): Iterator<GenericParam.LifetimeParam> =
        params.asSequence().filterIsInstance<GenericParam.LifetimeParam>().iterator()
}

public class TypeParams(
    private val params: List<GenericParam>,
) : Iterable<GenericParam.TypeParam> {
    private val iter = params.iterator()

    public fun next(): GenericParam.TypeParam? =
        iter.nextParamOfType()

    override fun iterator(): Iterator<GenericParam.TypeParam> =
        params.asSequence().filterIsInstance<GenericParam.TypeParam>().iterator()
}

public class TypeParamsMut(
    private val params: List<GenericParam>,
) : Iterable<GenericParam.TypeParam> {
    private val iter = params.iterator()

    public fun next(): GenericParam.TypeParam? =
        iter.nextParamOfType()

    override fun iterator(): Iterator<GenericParam.TypeParam> =
        params.asSequence().filterIsInstance<GenericParam.TypeParam>().iterator()
}

public class ConstParams(
    private val params: List<GenericParam>,
) : Iterable<GenericParam.ConstParam> {
    private val iter = params.iterator()

    public fun next(): GenericParam.ConstParam? =
        iter.nextParamOfType()

    override fun iterator(): Iterator<GenericParam.ConstParam> =
        params.asSequence().filterIsInstance<GenericParam.ConstParam>().iterator()
}

public class ConstParamsMut(
    private val params: List<GenericParam>,
) : Iterable<GenericParam.ConstParam> {
    private val iter = params.iterator()

    public fun next(): GenericParam.ConstParam? =
        iter.nextParamOfType()

    override fun iterator(): Iterator<GenericParam.ConstParam> =
        params.asSequence().filterIsInstance<GenericParam.ConstParam>().iterator()
}

/**
 * Returned by [Generics.splitForImpl].
 */
public data class ImplGenerics(
    public var generics: Generics,
) : ToTokens {
    public val ltToken: Lt?
        get() = generics.ltToken

    public val params: GenericParamList
        get() = generics.implGenerics().params

    public val gtToken: Gt?
        get() = generics.gtToken

    override fun toTokens(tokens: TokenStream) {
        generics.implGenerics().toTokens(tokens)
    }
}

/**
 * Returned by [Generics.splitForImpl].
 */
public data class TypeGenerics(
    public var generics: Generics,
) : ToTokens {
    public val ltToken: Lt?
        get() = generics.ltToken

    public val params: GenericParamList
        get() = generics.typeGenerics().params

    public val gtToken: Gt?
        get() = generics.gtToken

    /**
     * Turn a type's generics like `<X, Y>` into a turbofish generic argument list.
     */
    public fun asTurbofish(): Turbofish =
        Turbofish(generics)

    override fun toTokens(tokens: TokenStream) {
        if (generics.params.isEmpty()) return
        (generics.ltToken ?: Lt.default()).toTokens(tokens)
        generics.turbofishArguments().toTokens(tokens)
        (generics.gtToken ?: Gt.default()).toTokens(tokens)
    }
}

/**
 * Returned by [TypeGenerics.asTurbofish].
 */
public data class Turbofish(
    public var generics: Generics,
) : ToTokens {
    public val ltToken: Lt?
        get() = generics.ltToken

    public val params: GenericArgumentList
        get() = generics.turbofishArguments()

    public val gtToken: Gt?
        get() = generics.gtToken

    override fun toTokens(tokens: TokenStream) {
        if (generics.params.isEmpty()) return
        PathSep.default().toTokens(tokens)
        TypeGenerics(generics).toTokens(tokens)
    }
}

private inline fun <reified T : GenericParam> Iterator<GenericParam>.nextParamOfType(): T? {
    while (hasNext()) {
        val value = next()
        if (value is T) return value
    }
    return null
}

internal fun chooseGenericsOverQpath(input: ParseStream): Boolean =
    input.peek(GenericsDisambiguationLtPeek) &&
        (
            input.peek2(GenericsDisambiguationGtPeek) ||
                input.peek2(PoundPeek) ||
                (
                    (input.peek2(LifetimePeek) || input.peek2(IdentPeekAny)) &&
                        (
                            input.peek3(GenericsDisambiguationGtPeek) ||
                                input.peek3(CommaPeek) ||
                                (input.peek3(ColonPeek) && !input.peek3(PathSepPeek)) ||
                                input.peek3(EqPeek)
                        )
                ) ||
                input.peek2(ConstPeek)
        )

internal fun chooseGenericsOverQpathAfterKeyword(input: ParseStream): Boolean {
    val fork = input.fork()
    identParseAny(fork).getOrElse { return false }
    return chooseGenericsOverQpath(fork)
}

private object GenericsDisambiguationLtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        var (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '<'
    }

    override fun display(): String = "`<`"
}

private object GenericsDisambiguationGtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        var (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '>'
    }

    override fun display(): String = "`>`"
}

private fun Generics.implGenerics(): Generics {
    val implGenerics = Generics(ltToken, GenericParamList(), gtToken)
    for ((value, _) in params.pairsList()) {
        when (value) {
            is GenericParam.LifetimeParam -> {
                implGenerics.params.push(value.deepCopy()) { Comma(Span.callSite()) }
            }

            is GenericParam.TypeParam -> {
                implGenerics.params.push(
                    value.deepCopy().also {
                        it.default = null
                    },
                ) { Comma(Span.callSite()) }
            }

            is GenericParam.ConstParam -> {
                implGenerics.params.push(
                    value.deepCopy().also {
                        it.default = null
                    },
                ) { Comma(Span.callSite()) }
            }
        }
    }
    return implGenerics
}

private fun Generics.typeGenerics(): Generics {
    val typeGenerics = Generics(ltToken, GenericParamList(), gtToken)
    for ((value, _) in params.pairsList()) {
        when (value) {
            is GenericParam.LifetimeParam -> {
                typeGenerics.params.push(
                    GenericParam.LifetimeParam(
                        mutableListOf(),
                        value.lifetime.deepCopy(),
                        null,
                        LifetimeList(),
                    ),
                ) { Comma(Span.callSite()) }
            }

            is GenericParam.TypeParam -> {
                typeGenerics.params.push(
                    GenericParam.TypeParam(
                        mutableListOf(),
                        value.ident.copy(),
                        null,
                        TypeParamBoundList(),
                        null,
                    ),
                ) { Comma(Span.callSite()) }
            }

            is GenericParam.ConstParam -> {
                typeGenerics.params.push(
                    value.deepCopy().also {
                        it.default = null
                    },
                ) { Comma(Span.callSite()) }
            }
        }
    }
    return typeGenerics
}

private fun Generics.turbofishArguments(): GenericArgumentList {
    val args = GenericArgumentList()
    for ((value, _) in params.pairsList()) {
        if (value is GenericParam.LifetimeParam) {
            args.push(GenericArgument.LifetimeArg(value.lifetime.deepCopy())) { Comma(Span.callSite()) }
        }
    }
    for ((value, _) in params.pairsList()) {
        when (value) {
            is GenericParam.LifetimeParam -> {}

            is GenericParam.TypeParam -> {
                args.push(GenericArgument.TypeArg(SynType.Path(null, Path.from(value.ident.copy())))) { Comma(Span.callSite()) }
            }

            is GenericParam.ConstParam -> {
                args.push(GenericArgument.ConstArg(Expr.Path(mutableListOf(), null, Path.from(value.ident.copy())))) { Comma(Span.callSite()) }
            }
        }
    }
    return args
}

private fun printGenericParams(params: GenericParamList, tokens: TokenStream) {
    var trailingOrEmpty = true
    for ((value, punctuation) in params.pairsList()) {
        if (value is GenericParam.LifetimeParam) {
            value.toTokens(tokens)
            punctuation?.toTokens(tokens)
            trailingOrEmpty = punctuation != null
        }
    }
    for ((value, punctuation) in params.pairsList()) {
        when (value) {
            is GenericParam.LifetimeParam -> {}

            is GenericParam.TypeParam -> {
                if (!trailingOrEmpty) {
                    Comma.default().toTokens(tokens)
                    trailingOrEmpty = true
                }
                value.toTokens(tokens)
                punctuation?.toTokens(tokens)
                trailingOrEmpty = punctuation != null
            }

            is GenericParam.ConstParam -> {
                if (!trailingOrEmpty) {
                    Comma.default().toTokens(tokens)
                    trailingOrEmpty = true
                }
                value.toTokens(tokens)
                punctuation?.toTokens(tokens)
                trailingOrEmpty = punctuation != null
            }
        }
    }
}

/**
 * A generic type parameter, scope, or const generic: `T: Into<String>`,
 * `a: b`, `const LEN: Int`.
 *
 * # Syntax tree enum
 *
 * This type is a syntax tree enum; see [Expr].
 */
public sealed class GenericParam : ToTokens {
    public abstract fun deepCopy(): GenericParam

    public companion object {
        public fun parse(input: ParseStream): SynResult<GenericParam> {
            val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
            val lookahead = input.lookahead1()
            return when {
                lookahead.peek(IdentPeek) -> {
                    GenericParam.TypeParam.parseWithAttrs(attrs, input)
                }

                lookahead.peek(LifetimePeek) -> {
                    GenericParam.LifetimeParam.parseWithAttrs(attrs, input)
                }

                lookahead.peek(ConstPeek) -> {
                    GenericParam.ConstParam.parseWithAttrs(attrs, input)
                }

                else -> {
                    SynResult.failure(lookahead.error())
                }
            }
        }
    }

    /**
     * A scope definition: `'a:  +  + `.
     */
    public data class LifetimeParam(
        public var attrs: List<Attribute>,
        public var lifetime: Lifetime,
        public var colonToken: Colon?,
        public var bounds: LifetimeList,
    ) : GenericParam() {
        public companion object {
            public fun new(lifetime: Lifetime): LifetimeParam =
                LifetimeParam(mutableListOf(), lifetime, null, LifetimeList())

            public fun parse(input: ParseStream): SynResult<LifetimeParam> {
                val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
                return parseWithAttrs(attrs, input)
            }

            internal fun parseWithAttrs(attrs: List<Attribute>, input: ParseStream): SynResult<LifetimeParam> {
                val lifetime = Lifetime.parseAny(input).getOrElse { return SynResult.failure(it) }
                val colonToken = ColonParse.parse(input).getOrNull()
                val bounds = LifetimeList()
                if (colonToken != null) {
                    while (!input.isEmpty() && !input.peek(CommaPeek) && !input.peek(GenericsGtPeek)) {
                        bounds.pushValue(Lifetime.parseAny(input).getOrElse { return SynResult.failure(it) })
                        if (!input.peek(PlusPeek)) break
                        bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
                    }
                }
                return SynResult.success(LifetimeParam(attrs, lifetime, colonToken, bounds))
            }
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            lifetime.toTokens(tokens)
            if (!bounds.isEmpty()) {
                (colonToken ?: Colon.default()).toTokens(tokens)
                bounds.toTokens(tokens)
            }
        }

        override fun deepCopy(): LifetimeParam =
            LifetimeParam(attrs.mapTo(mutableListOf()) { it.deepCopy() }, lifetime.deepCopy(), colonToken, bounds.copy({ it.deepCopy() }, { it }))
    }

    /**
     * A generic type parameter: `T: Into<String>`.
     */
    public data class TypeParam(
        public var attrs: List<Attribute>,
        public var ident: Ident,
        public var colonToken: Colon?,
        public var bounds: TypeParamBoundList,
        public var default: EqSynType?,
    ) : GenericParam() {
        public companion object {
            public fun from(ident: Ident): TypeParam =
                TypeParam(mutableListOf(), ident, null, TypeParamBoundList(), null)

            public fun parse(input: ParseStream): SynResult<TypeParam> {
                val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
                return parseWithAttrs(attrs, input)
            }

            internal fun parseWithAttrs(attrs: List<Attribute>, input: ParseStream): SynResult<TypeParam> {
                val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                val colonToken = ColonParse.parse(input).getOrNull()
                val bounds = TypeParamBoundList()
                if (colonToken != null) {
                    while (!input.isEmpty() &&
                        !input.peek(CommaPeek) &&
                        !input.peek(GenericsGtPeek) &&
                        !input.peek(EqPeek)
                    ) {
                        bounds.pushValue(
                            TypeParamBound
                                .parseSingle(
                                    input,
                                    allowPreciseCapture = false,
                                    allowConst = true,
                                ).getOrElse { return SynResult.failure(it) },
                        )
                        if (!input.peek(PlusPeek)) break
                        bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
                    }
                }
                val eqToken = EqParse.parse(input).getOrNull()
                val default =
                    if (eqToken != null) {
                        EqSynType(eqToken, parseTypeFull(input).getOrElse { return SynResult.failure(it) })
                    } else {
                        null
                    }
                return SynResult.success(TypeParam(attrs, ident, colonToken, bounds, default))
            }
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            ident.toTokens(tokens)
            if (!bounds.isEmpty()) {
                (colonToken ?: Colon.default()).toTokens(tokens)
                bounds.toTokens(tokens)
            }
            default?.let { defaultPair ->
                defaultPair.eqToken.toTokens(tokens)
                defaultPair.type.toTokens(tokens)
            }
        }

        override fun deepCopy(): TypeParam =
            TypeParam(attrs.mapTo(mutableListOf()) { it.deepCopy() }, ident.copy(), colonToken, bounds.copy({ it.deepCopy() }, { it }), default?.deepCopy())
    }

    /**
     * A const generic parameter: `const LENGTH: Int`.
     */
    public data class ConstParam(
        public var attrs: List<Attribute>,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var colonToken: Colon,
        public var ty: SynType,
        public var default: EqExpr?,
    ) : GenericParam() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<ConstParam> {
                val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
                return parseWithAttrs(attrs, input)
            }

            internal fun parseWithAttrs(attrs: List<Attribute>, input: ParseStream): SynResult<ConstParam> {
                val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
                val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
                val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
                val default =
                    if (input.peek(EqPeek)) {
                        val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                        val defaultExpr = constArgument(input).getOrElse { return SynResult.failure(it) }
                        EqExpr(eqToken, defaultExpr)
                    } else {
                        null
                    }
                return SynResult.success(ConstParam(attrs, constToken, ident, colonToken, ty, default))
            }
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constToken.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            default?.let { defaultPair ->
                defaultPair.eqToken.toTokens(tokens)
                printConstArgument(defaultPair.expr, tokens)
            }
        }

        override fun deepCopy(): ConstParam =
            ConstParam(attrs.mapTo(mutableListOf()) { it.deepCopy() }, constToken, ident.copy(), colonToken, ty.deepCopy(), default?.deepCopy())
    }
}

/**
 * A `where` clause in a definition: `where T: Deserialize<>, D:
 * `.
 */
public data class WhereClause(
    public var whereToken: Where,
    public var predicates: WherePredicateList,
) : ToTokens {
    public companion object {
        public fun parse(input: ParseStream): SynResult<WhereClause> {
            val whereToken = WhereParse.parse(input).getOrElse { return SynResult.failure(it) }
            if (chooseGenericsOverQpath(input)) {
                return SynResult.failure(
                    input.error("generic parameters on `where` clauses are reserved for future use"),
                )
            }
            val predicates = WherePredicateList()
            while (!input.isEmpty() &&
                !input.peek(BracePeek) &&
                !input.peek(CommaPeek) &&
                !input.peek(SemiPeek) &&
                !(input.peek(ColonPeek) && !input.peek(PathSepPeek)) &&
                !input.peek(EqPeek)
            ) {
                predicates.pushValue(WherePredicate.parse(input).getOrElse { return SynResult.failure(it) })
                if (!input.peek(CommaPeek)) break
                predicates.pushPunct(CommaParse.parse(input).getOrElse { return SynResult.failure(it) })
            }
            return SynResult.success(WhereClause(whereToken, predicates))
        }
    }

    override fun toTokens(tokens: TokenStream) {
        if (!predicates.isEmpty()) {
            whereToken.toTokens(tokens)
            predicates.toTokens(tokens)
        }
    }

    public fun deepCopy(): WhereClause =
        WhereClause(whereToken, predicates.copy({ it.deepCopy() }, { it }))
}

internal fun printConstArgument(expr: Expr, tokens: TokenStream) {
    when (expr) {
        is Expr.Lit -> {
            expr.toTokens(tokens)
        }

        is Expr.Path -> {
            if (expr.attrs.isEmpty() && expr.qself == null && expr.path.getIdent() != null) {
                expr.toTokens(tokens)
            } else {
                io.github.kotlinmania.syn.token.Brace.default().surround(tokens) { inner ->
                    printExpr(expr, inner)
                }
            }
        }

        is Expr.BlockExpr,
        is Expr.Verbatim,
        -> {
            expr.toTokens(tokens)
        }

        else -> {
            io.github.kotlinmania.syn.token.Brace.default().surround(tokens) { inner ->
                printExpr(expr, inner)
            }
        }
    }
}

/**
 * A single predicate in a `where` clause: `T: Deserialize<>`.
 *
 * # Syntax tree enum
 *
 * This type is a syntax tree enum; see [Expr].
 */
public sealed class WherePredicate : ToTokens {
    public abstract fun deepCopy(): WherePredicate

    public companion object {
        public fun parse(input: ParseStream): SynResult<WherePredicate> {
            val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
            val lifetimePair = input.cursor().lifetime()
            if (lifetimePair != null && ColonPeek.peek(lifetimePair.second)) {
                val lifetime = Lifetime.parseAny(input).getOrElse { return SynResult.failure(it) }
                val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
                val bounds = LifetimeList()
                while (!input.isEmpty() &&
                    !input.peek(BracePeek) &&
                    !input.peek(CommaPeek) &&
                    !input.peek(SemiPeek) &&
                    !input.peek(ColonPeek) &&
                    !input.peek(EqPeek)
                ) {
                    bounds.pushValue(Lifetime.parseAny(input).getOrElse { return SynResult.failure(it) })
                    if (!input.peek(PlusPeek)) break
                    bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
                }
                return SynResult.success(LifetimePredicate(lifetime, colonToken, bounds, attrs))
            }

            val lifetimes = BoundLifetimes.parseOptional(input).getOrElse { return SynResult.failure(it) }
            val boundedTy = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            val bounds = TypeParamBoundList()
            while (!input.isEmpty() &&
                !input.peek(BracePeek) &&
                !input.peek(CommaPeek) &&
                !input.peek(SemiPeek) &&
                !(input.peek(ColonPeek) && !input.peek(PathSepPeek)) &&
                !input.peek(EqPeek)
            ) {
                bounds.pushValue(
                    TypeParamBound
                        .parseSingle(
                            input,
                            allowPreciseCapture = false,
                            allowConst = true,
                        ).getOrElse { return SynResult.failure(it) },
                )
                if (!input.peek(PlusPeek)) break
                bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
            }
            return SynResult.success(TypePredicate(lifetimes, boundedTy, colonToken, bounds, attrs))
        }
    }

    public data class TypePredicate(
        public var lifetimes: BoundLifetimes?,
        /**
         * The type being bounded
         */
        public var boundedTy: SynType,
        public var colonToken: Colon,
        public var bounds: TypeParamBoundList,
        public var attrs: List<Attribute> = mutableListOf(),
    ) : WherePredicate() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            lifetimes?.toTokens(tokens)
            boundedTy.toTokens(tokens)
            colonToken.toTokens(tokens)
            for ((bound, plus) in bounds.pairsList()) {
                bound.toTokens(tokens)
                plus?.toTokens(tokens)
            }
        }

        override fun deepCopy(): TypePredicate =
            TypePredicate(
                lifetimes?.deepCopy(),
                boundedTy.deepCopy(),
                colonToken,
                bounds.copy({ it.deepCopy() }, { it }),
                attrs.mapTo(mutableListOf()) { it.deepCopy() },
            )
    }

    public data class LifetimePredicate(
        public var lifetime: Lifetime,
        public var colonToken: Colon,
        public var bounds: LifetimeList,
        public var attrs: List<Attribute> = mutableListOf(),
    ) : WherePredicate() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            lifetime.toTokens(tokens)
            colonToken.toTokens(tokens)
            bounds.toTokens(tokens)
        }

        override fun deepCopy(): LifetimePredicate =
            LifetimePredicate(
                lifetime.deepCopy(),
                colonToken,
                bounds.copy({ it.deepCopy() }, { it }),
                attrs.mapTo(mutableListOf()) { it.deepCopy() },
            )
    }
}

/**
 * A trait or scope used as a bound on a type parameter.
 */
public sealed class TypeParamBound : ToTokens {
    public abstract fun deepCopy(): TypeParamBound

    public companion object {
        public fun parse(input: ParseStream): SynResult<TypeParamBound> =
            parseSingle(input, allowPreciseCapture = true, allowConst = true)

        public fun parseSingle(
            input: ParseStream,
            allowPreciseCapture: Boolean,
            allowConst: Boolean,
        ): SynResult<TypeParamBound> {
            if (input.peek(LifetimePeek)) {
                return SynResult.success(LifetimeBound(Lifetime.parseAny(input).getOrElse { return SynResult.failure(it) }))
            }

            if (input.peek(UsePeek)) {
                val preciseCaptureBegin = input.cursor()
                val preciseCapture = PreciseCapture.parse(input).getOrElse { return SynResult.failure(it) }
                return if (allowPreciseCapture) {
                    SynResult.success(preciseCapture)
                } else {
                    SynResult.failure(
                        SynError.newRange(
                            preciseCaptureBegin,
                            input.cursor(),
                            "`use<...>` precise capturing syntax is not allowed here",
                        ),
                    )
                }
            }

            val begin = input.cursor()
            val parens =
                if (input.peek(ParenPeek)) {
                    parenthesized(input).getOrElse { return SynResult.failure(it) }
                } else {
                    null
                }
            val content = parens?.content ?: input
            val parsed = Trait.doParse(content, allowConst).getOrElse { return SynResult.failure(it) }
            if (parens != null) {
                parens.content.finishChildBuffer()
            }
            return if (parsed != null) {
                SynResult.success(parsed.copy(parenToken = parens?.token))
            } else {
                SynResult.success(Verbatim(between(begin, input.cursor())))
            }
        }

        public fun parseMultiple(
            input: ParseStream,
            allowPlus: Boolean,
            allowPreciseCapture: Boolean,
            allowConst: Boolean,
        ): SynResult<TypeParamBoundList> {
            val bounds = TypeParamBoundList()
            while (true) {
                bounds.pushValue(
                    parseSingle(input, allowPreciseCapture, allowConst).getOrElse {
                        return SynResult.failure(it)
                    },
                )
                if (!(allowPlus && input.peek(PlusPeek))) break
                bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
                if (!(
                        input.peek(IdentPeekAny) ||
                            input.peek(PathSepPeek) ||
                            input.peek(QuestionPeek) ||
                            input.peek(LifetimePeek) ||
                            input.peek(ParenPeek) ||
                            (allowConst && (input.peek(BracketPeek) || input.peek(ConstPeek)))
                    )
                ) {
                    break
                }
            }
            return SynResult.success(bounds)
        }
    }

    public data class Trait(
        var parenToken: io.github.kotlinmania.syn.token.Paren?,
        var lifetimes: BoundLifetimes?,
        var modifiers: TraitBoundModifiers,
        var maybe: io.github.kotlinmania.syn.token.Question?,
        var path: Path,
    ) : TypeParamBound() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Trait> {
                val allowConst = false
                return doParse(input, allowConst).map { it ?: error("expected trait bound") }
            }

            public fun doParse(input: ParseStream, allowConst: Boolean): SynResult<Trait?> {
                var lifetimes = BoundLifetimes.parseOptional(input).getOrElse { return SynResult.failure(it) }
                var isConditionallyConst = false
                var isUnconditionallyConst = false

                if (input.peek(BracketPeek)) {
                    val conditionallyConst = bracketed(input).getOrElse { return SynResult.failure(it) }
                    ConstParse.parse(conditionallyConst.content).getOrElse { return SynResult.failure(it) }
                    if (!conditionallyConst.content.isEmpty()) {
                        return SynResult.failure(conditionallyConst.content.error("unexpected token"))
                    }
                    conditionallyConst.content.finishChildBuffer()
                    if (!allowConst) {
                        return SynResult.failure(
                            SynError.new(
                                conditionallyConst.token.span.join(),
                                "`[const]` is not allowed here",
                            ),
                        )
                    }
                    isConditionallyConst = true
                } else if (input.peek(ConstPeek)) {
                    val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
                    if (!allowConst) {
                        return SynResult.failure(
                            SynError.new(
                                constToken.span,
                                "`const` is not allowed here",
                            ),
                        )
                    }
                    isUnconditionallyConst = true
                }

                val maybe = QuestionParse.parse(input).getOrNull()
                if (lifetimes == null && maybe != null) {
                    lifetimes = BoundLifetimes.parseOptional(input).getOrElse { return SynResult.failure(it) }
                }

                val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
                val last = path.segments.last()
                if (last != null &&
                    last.arguments.isEmpty() &&
                    (input.peek(ParenPeek) || (input.peek(PathSepPeek) && input.peek3(ParenPeek)))
                ) {
                    if (input.peek(PathSepPeek)) {
                        PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
                    }
                    last.arguments = parseParenthesizedPathArguments(input).getOrElse { return SynResult.failure(it) }
                }

                if (lifetimes != null && maybe != null) {
                    return SynResult.failure(
                        SynError.new(
                            maybe.span,
                            "`for<...>` binder not allowed with `?` trait polarity modifier",
                        ),
                    )
                }

                return if (isConditionallyConst || isUnconditionallyConst) {
                    SynResult.success(null)
                } else {
                    SynResult.success(Trait(null, lifetimes, TraitBoundModifiers(), maybe, path))
                }
            }
        }

        override fun toTokens(tokens: TokenStream) {
            val parenToken = this.parenToken
            if (parenToken != null) {
                parenToken.surround(tokens) { inner ->
                    lifetimes?.toTokens(inner)
                    maybe?.toTokens(inner)
                    path.toTokens(inner)
                }
            } else {
                lifetimes?.toTokens(tokens)
                maybe?.toTokens(tokens)
                path.toTokens(tokens)
            }
        }

        override fun deepCopy(): Trait = Trait(parenToken, lifetimes?.deepCopy(), modifiers.deepCopy(), maybe, path.deepCopy())
    }

    public data class LifetimeBound(
        var lifetime: Lifetime,
    ) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
        }

        override fun deepCopy(): LifetimeBound = LifetimeBound(lifetime.deepCopy())
    }

    public data class PreciseCapture(
        var useToken: io.github.kotlinmania.syn.token.Use,
        var ltToken: Lt,
        var params: CapturedParamList,
        var gtToken: Gt,
    ) : TypeParamBound() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<PreciseCapture> {
                val useToken = UseParse.parse(input).getOrElse { return SynResult.failure(it) }
                val ltToken = GenericsLtParse.parse(input).getOrElse { return SynResult.failure(it) }
                val params = CapturedParamList()
                loop@ while (true) {
                    val lookahead = input.lookahead1()
                    params.pushValue(
                        if (lookahead.peek(LifetimePeek) ||
                            lookahead.peek(IdentPeek) ||
                            input.peek(SelfTypePeek)
                        ) {
                            CapturedParam.parse(input).getOrElse { return SynResult.failure(it) }
                        } else if (lookahead.peek(GenericsGtPeek)) {
                            break@loop
                        } else {
                            return SynResult.failure(lookahead.error())
                        },
                    )
                    val separator = input.lookahead1()
                    params.pushPunct(
                        if (separator.peek(CommaPeek)) {
                            CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
                        } else if (separator.peek(GenericsGtPeek)) {
                            break@loop
                        } else {
                            return SynResult.failure(separator.error())
                        },
                    )
                }
                val gtToken = GenericsGtParse.parse(input).getOrElse { return SynResult.failure(it) }
                return SynResult.success(PreciseCapture(useToken, ltToken, params, gtToken))
            }
        }

        override fun toTokens(tokens: TokenStream) {
            useToken.toTokens(tokens)
            ltToken.toTokens(tokens)
            var trailingOrEmpty = true
            for ((value, punctuation) in params.pairsList()) {
                if (value is CapturedParam.Lifetime) {
                    value.toTokens(tokens)
                    punctuation?.toTokens(tokens)
                    trailingOrEmpty = punctuation != null
                }
            }
            for ((value, punctuation) in params.pairsList()) {
                if (value is CapturedParam.Ident) {
                    if (!trailingOrEmpty) {
                        Comma.default().toTokens(tokens)
                        trailingOrEmpty = true
                    }
                    value.toTokens(tokens)
                    punctuation?.toTokens(tokens)
                    trailingOrEmpty = punctuation != null
                }
            }
            gtToken.toTokens(tokens)
        }

        override fun deepCopy(): PreciseCapture = PreciseCapture(useToken, ltToken, params.copy({ it.deepCopy() }, { it }), gtToken)
    }

    public data class Verbatim(
        var tokens: TokenStream,
    ) : TypeParamBound() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }

        override fun deepCopy(): Verbatim = copy(tokens = tokens.clone())
    }
}

/**
 * Additional optional information about a trait bound.
 */
public class TraitBoundModifiers {
    public companion object {
        public fun default(): TraitBoundModifiers = TraitBoundModifiers()
    }

    public fun requireEmpty(): SynResult<Unit> = SynResult.success(Unit)

    public fun deepCopy(): TraitBoundModifiers = TraitBoundModifiers()

    public fun clone(): TraitBoundModifiers = TraitBoundModifiers()

    override fun equals(other: Any?): Boolean = other is TraitBoundModifiers

    override fun hashCode(): Int = 0

    override fun toString(): String = "TraitBoundModifiers"
}

/**
 * A set of bound scopes: `for<, , >`.
 */
public data class BoundLifetimes(
    var forToken: io.github.kotlinmania.syn.token.For,
    var ltToken: Lt,
    var lifetimes: GenericParamList,
    var gtToken: Gt,
) : ToTokens {
    public companion object {
        public fun default(): BoundLifetimes =
            BoundLifetimes(
                io.github.kotlinmania.syn.token.For
                    .default(),
                Lt.default(),
                GenericParamList(),
                Gt.default(),
            )

        public fun parse(input: ParseStream): SynResult<BoundLifetimes> {
            val forToken = ForParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ltToken = GenericsLtParse.parse(input).getOrElse { return SynResult.failure(it) }
            val lifetimes = GenericParamList()
            while (!input.peek(GenericsGtPeek)) {
                lifetimes.pushValue(GenericParam.parse(input).getOrElse { return SynResult.failure(it) })
                if (input.peek(GenericsGtPeek)) break
                lifetimes.pushPunct(CommaParse.parse(input).getOrElse { return SynResult.failure(it) })
            }
            val gtToken = GenericsGtParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(BoundLifetimes(forToken, ltToken, lifetimes, gtToken))
        }

        public fun parseOptional(input: ParseStream): SynResult<BoundLifetimes?> =
            if (input.peek(ForPeek)) {
                parse(input).map { it }
            } else {
                SynResult.success(null)
            }
    }

    override fun toTokens(tokens: TokenStream) {
        forToken.toTokens(tokens)
        ltToken.toTokens(tokens)
        lifetimes.toTokens(tokens)
        gtToken.toTokens(tokens)
    }

    public fun deepCopy(): BoundLifetimes = BoundLifetimes(forToken, ltToken, lifetimes.copy({ it.deepCopy() }, { it }), gtToken)
}

/**
 * Single parameter in a precise capturing bound.
 */
public sealed class CapturedParam : ToTokens {
    public abstract fun deepCopy(): CapturedParam

    public companion object {
        public fun parse(input: ParseStream): SynResult<CapturedParam> {
            val lookahead = input.lookahead1()
            return when {
                lookahead.peek(LifetimePeek) -> {
                    io.github.kotlinmania.syn.Lifetime
                        .parseAny(input)
                        .map { Lifetime(it) }
                }

                lookahead.peek(IdentPeek) || input.peek(SelfTypePeek) -> {
                    val ident =
                        if (input.peek(SelfTypePeek)) {
                            identFromSelfType(SelfTypeParse.parse(input).getOrElse { return SynResult.failure(it) })
                        } else {
                            identParseAny(input).getOrElse { return SynResult.failure(it) }
                        }
                    SynResult.success(Ident(ident))
                }

                else -> {
                    SynResult.failure(lookahead.error())
                }
            }
        }
    }

    public data class Lifetime(
        var lifetime: io.github.kotlinmania.syn.Lifetime,
    ) : CapturedParam() {
        override fun toTokens(tokens: TokenStream) {
            lifetime.toTokens(tokens)
        }

        override fun deepCopy(): Lifetime = Lifetime(lifetime.deepCopy())
    }

    /**
     * A type parameter or const generic parameter in a precise capturing
     * bound.
     */
    public data class Ident(
        var ident: io.github.kotlinmania.procmacro2.Ident,
    ) : CapturedParam() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
        }

        override fun deepCopy(): Ident = Ident(ident.copy())
    }
}

internal fun parseGenerics(input: ParseStream): SynResult<Generics> = Generics.parse(input)

internal object GenericsLtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '<'
    }

    override fun display(): String = "`<`"
}

internal object GenericsLtParse {
    fun parse(input: ParseStream): SynResult<Lt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<`"))
            if (punct.asChar() != '<') return@step SynResult.failure(cursor.error("expected `<`"))
            SynResult.success(Lt.from(punct.span()) to rest)
        }
}

internal object GenericsGtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '>'
    }

    override fun display(): String = "`>`"
}

internal object GenericsGtParse {
    fun parse(input: ParseStream): SynResult<Gt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>`"))
            if (punct.asChar() != '>') return@step SynResult.failure(cursor.error("expected `>`"))
            SynResult.success(Gt.from(punct.span()) to rest)
        }
}

public object GenericParamParse {
    fun parse(input: ParseStream): SynResult<GenericParam> =
        GenericParam.parse(input)
}

public object WhereClauseParse {
    fun parse(input: ParseStream): SynResult<WhereClause> =
        parseWhereClause(input)
}

internal fun parseWhereClause(input: ParseStream): SynResult<WhereClause> = WhereClause.parse(input)

internal fun parseTypeParamBounds(
    input: ParseStream,
    stopAtEq: Boolean,
    allowPreciseCapture: Boolean = false,
    allowPlus: Boolean = true,
): SynResult<TypeParamBoundList> {
    val bounds = TypeParamBoundList()
    while (!input.isEmpty() && !input.peek(CommaPeek) && !input.peek(GenericsGtPeek) && !input.peek(BracePeek) && !input.peek(WherePeek) && !input.peek(SemiPeek) && !(stopAtEq && input.peek(EqPeek))) {
        val bound =
            TypeParamBound
                .parseSingle(
                    input,
                    allowPreciseCapture = allowPreciseCapture,
                    allowConst = false,
                ).getOrElse { return SynResult.failure(it) }
        bounds.pushValue(bound)
        if (!allowPlus || !input.peek(PlusPeek)) break
        bounds.pushPunct(PlusParse.parse(input).getOrThrow())
    }
    return SynResult.success(bounds)
}

internal fun parseTypeParamBoundsMultiple(
    input: ParseStream,
    allowPlus: Boolean,
    allowPreciseCapture: Boolean,
    allowConst: Boolean,
): SynResult<TypeParamBoundList> = TypeParamBound.parseMultiple(input, allowPlus, allowPreciseCapture, allowConst)

internal fun parseBoundLifetimes(input: ParseStream): SynResult<BoundLifetimes> = BoundLifetimes.parse(input)

public object TypeParamBoundParse {
    fun parse(input: ParseStream): SynResult<TypeParamBound> =
        TypeParamBound.parse(input)
}
