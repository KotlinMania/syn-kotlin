// port-lint: source ty.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Bracket
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.RArrow
import io.github.kotlinmania.syn.token.Semi

/**
 * A type syntax tree node.
 *
 * Named `SynType` to avoid colliding with Swift's built-in `Type`
 * metatype expression (`foo.Type`), which the Swift compiler rejects
 * as `error: type member must not be named Type`. The `Type` class
 * preserves the original Kotlin API name.
 */
public sealed class SynType : ToTokens {
    public companion object {
        fun parse(input: ParseStream): SynResult<SynType> = parseTypeFull(input)

        public fun withoutPlus(input: ParseStream): SynResult<SynType> = parseTypeWithoutPlus(input)
    }

    /**
     * A fixed size array type: `[T; n]`.
     */
    public data class Array(
        var elem: SynType,
        var len: Expr,
        var bracketToken: Bracket = Bracket.default(),
        var semiToken: Semi = Semi.default(),
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            bracketToken.surround(tokens) { inner ->
                elem.toTokens(inner)
                semiToken.toTokens(inner)
                len.toTokens(inner)
            }
        }

        override fun deepCopy(): Array = Array(elem.deepCopy(), len.deepCopy(), Bracket.from(bracketToken.span), Semi.from(semiToken.span))
    }

    /**
     * A bare function type: `fn(Int) -> bool`.
     */
    public data class BareFn(
        var lifetimes: BoundLifetimes?,
        var unsafety: io.github.kotlinmania.syn.token.Unsafe?,
        var abi: Abi?,
        var fnToken: io.github.kotlinmania.syn.token.Fn,
        var parenToken: io.github.kotlinmania.syn.token.Paren,
        var inputs: BareFnArgList,
        var variadic: BareVariadic?,
        var output: ReturnType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            lifetimes?.toTokens(tokens)
            unsafety?.toTokens(tokens)
            abi?.toTokens(tokens)
            fnToken.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                inputs.toTokens(inner)
                val variadic = this.variadic
                if (variadic != null) {
                    if (!inputs.emptyOrTrailing()) {
                        io.github.kotlinmania.syn.token.Comma
                            .from(variadic.dots.spans.first())
                            .toTokens(inner)
                    }
                    variadic.toTokens(inner)
                }
            }
            output.toTokens(tokens)
        }

        override fun deepCopy(): BareFn =
            BareFn(
                lifetimes?.deepCopy(),
                unsafety,
                abi,
                fnToken,
                parenToken,
                inputs.copy({ it.deepCopy() }, { it }),
                variadic?.deepCopy(),
                output.deepCopy(),
            )
    }

    /**
     * A type contained within invisible delimiters.
     */
    public data class Group(
        var groupToken: io.github.kotlinmania.syn.token.Group,
        var elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            groupToken.surround(tokens) { inner -> elem.toTokens(inner) }
        }

        override fun deepCopy(): Group = Group(groupToken, elem.deepCopy())
    }

    /**
     * An `implementation Bound1 + Bound2 + Bound3` type where `Bound` is a trait or
     * a scope.
     */
    public data class ImplTrait(
        var implToken: io.github.kotlinmania.syn.token.Impl,
        var bounds: TypeParamBoundList,
    ) : SynType() {
        public companion object {
            fun parse(input: ParseStream): SynResult<ImplTrait> =
                parseTypeImplTrait(input, allowPlus = true)

            fun withoutPlus(input: ParseStream): SynResult<ImplTrait> =
                parseTypeImplTrait(input, allowPlus = false)
        }

        override fun toTokens(tokens: TokenStream) {
            implToken.toTokens(tokens)
            bounds.toTokens(tokens)
        }

        override fun deepCopy(): ImplTrait = ImplTrait(implToken, bounds.copy({ it.deepCopy() }, { it }))
    }

    /**
     * Indication that a type should be inferred by the compiler: `_`.
     */
    public data class Infer(
        var underscoreToken: io.github.kotlinmania.syn.token.Underscore,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            underscoreToken.toTokens(tokens)
        }

        override fun deepCopy(): Infer = this
    }

    /**
     * A macro in the type position.
     */
    public data class Macro(
        var mac: io.github.kotlinmania.syn.Macro,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            mac.toTokens(tokens)
        }

        override fun deepCopy(): Macro = Macro(mac.deepCopy())
    }

    /**
     * The never type: `!`.
     */
    public data class Never(
        var bangToken: io.github.kotlinmania.syn.token.Not,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            bangToken.toTokens(tokens)
        }

        override fun deepCopy(): Never = this
    }

    public data class Paren(
        var parenToken: io.github.kotlinmania.syn.token.Paren,
        var elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            parenToken.surround(tokens) { inner ->
                elem.toTokens(inner)
            }
        }

        override fun deepCopy(): Paren = Paren(parenToken, elem.deepCopy())
    }

    /**
     * A path like `core::slice::Iter`, optionally qualified with a
     * self-type as in `<Vec<T> as SomeTrait>::Associated`.
     */
    public data class Path(
        var qself: QSelf?,
        var path: io.github.kotlinmania.syn.Path,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            printQpath(tokens, qself, path, PathStyle.AsWritten)
        }

        override fun deepCopy(): Path = Path(qself, path.deepCopy())
    }

    /**
     * A raw pointer type: `*const T` or `*mut T`.
     */
    public data class Ptr(
        var starToken: io.github.kotlinmania.syn.token.Star,
        var constToken: io.github.kotlinmania.syn.token.Const?,
        var mutability: io.github.kotlinmania.syn.token.Mut?,
        var elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            starToken.toTokens(tokens)
            val mutToken = mutability
            if (mutToken != null) mutToken.toTokens(tokens) else constToken?.toTokens(tokens)
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Ptr = Ptr(starToken, constToken, mutability, elem.deepCopy())
    }

    /**
     * A reference type: `& T` or `& mut T`.
     */
    public data class Reference(
        var andToken: io.github.kotlinmania.syn.token.And,
        var lifetime: Lifetime?,
        var mutability: io.github.kotlinmania.syn.token.Mut?,
        var elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            andToken.toTokens(tokens)
            lifetime?.toTokens(tokens)
            mutability?.toTokens(tokens)
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Reference = Reference(andToken, lifetime?.deepCopy(), mutability, elem.deepCopy())
    }

    /**
     * A dynamically sized slice type: `[T]`.
     */
    public data class Slice(
        var elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            Bracket.default().surround(tokens) { inner ->
                elem.toTokens(inner)
            }
        }

        override fun deepCopy(): Slice = Slice(elem.deepCopy())
    }

    /**
     * A trait object type `dynamic Bound1 + Bound2 + Bound3` where `Bound` is a
     * trait or a scope.
     */
    public data class TraitObject(
        var dynToken: io.github.kotlinmania.syn.token.Dyn?,
        var bounds: TypeParamBoundList,
    ) : SynType() {
        public companion object {
            fun parse(input: ParseStream): SynResult<TraitObject> =
                parseTypeTraitObject(input, allowPlus = true)

            fun withoutPlus(input: ParseStream): SynResult<TraitObject> =
                parseTypeTraitObject(input, allowPlus = false)

            internal fun parseBounds(
                dynSpan: Span,
                input: ParseStream,
                allowPlus: Boolean,
            ): SynResult<TypeParamBoundList> {
                val bounds =
                    parseTypeParamBoundsMultiple(
                        input,
                        allowPlus = allowPlus,
                        allowPreciseCapture = false,
                        allowConst = false,
                    ).getOrElse { return SynResult.failure(it) }
                var lastLifetimeSpan: Span? = null
                var atLeastOneTrait = false
                for (bound in bounds.toList()) {
                    when (bound) {
                        is TypeParamBound.Trait -> {
                            atLeastOneTrait = true
                            break
                        }

                        is TypeParamBound.LifetimeBound -> {
                            lastLifetimeSpan = bound.lifetime.ident.span()
                        }

                        is TypeParamBound.PreciseCapture,
                        is TypeParamBound.Verbatim,
                        -> {}
                    }
                }
                if (!atLeastOneTrait) {
                    return SynResult.failure(
                        SynError.new2(
                            dynSpan,
                            lastLifetimeSpan ?: dynSpan,
                            "at least one trait is required for an object type",
                        ),
                    )
                }
                return SynResult.success(bounds)
            }
        }

        override fun toTokens(tokens: TokenStream) {
            dynToken?.toTokens(tokens)
            bounds.toTokens(tokens)
        }

        override fun deepCopy(): TraitObject = TraitObject(dynToken, bounds.copy({ it.deepCopy() }, { it }))
    }

    /**
     * A tuple type: `(A, B, C, String)`.
     */
    public data class Tuple(
        var parenToken: io.github.kotlinmania.syn.token.Paren,
        var elems: SynTypeList,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }

        override fun deepCopy(): Tuple = Tuple(parenToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /**
     * Tokens in type position not interpreted by Syn.
     */
    public data class Verbatim(
        var tokens: TokenStream,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }

        override fun deepCopy(): Verbatim = this
    }

    public abstract fun deepCopy(): SynType
}

/**
 * An argument in a function type: the `Int` in `fn(Int) -> bool`.
 */
public data class BareFnArg(
    public var attrs: List<Attribute>,
    public var name: IdentColon?,
    public var ty: SynType,
) : ToTokens {
    public companion object {
        fun parse(input: ParseStream): SynResult<BareFnArg> {
            val allowSelf = false
            return parseBareFnArg(input, allowSelf)
        }
    }

    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        name?.ident?.toTokens(tokens)
        name?.colonToken?.toTokens(tokens)
        ty.toTokens(tokens)
    }

    public fun deepCopy(): BareFnArg =
        BareFnArg(attrs.mapTo(mutableListOf()) { it.deepCopy() }, name, ty.deepCopy())
}

/** The variadic argument of a function pointer. */
public data class BareVariadic(
    public var attrs: List<Attribute>,
    public var name: IdentColon?,
    public var dots: io.github.kotlinmania.syn.token.DotDotDot,
    public var comma: io.github.kotlinmania.syn.token.Comma?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        name?.ident?.toTokens(tokens)
        name?.colonToken?.toTokens(tokens)
        dots.toTokens(tokens)
        comma?.toTokens(tokens)
    }

    public fun deepCopy(): BareVariadic = BareVariadic(attrs.mapTo(mutableListOf()) { it.deepCopy() }, name, dots, comma)
}

/**
 * Return type of a function signature.
 */
public sealed class ReturnType : ToTokens {
    public companion object {
        fun parse(input: ParseStream): SynResult<ReturnType> = parseReturnType(input)

        fun withoutPlus(input: ParseStream): SynResult<ReturnType> =
            parseReturnTypeWithoutPlus(input)
    }

    /**
     * Return type is not specified.
     *
     * Functions default to `()` and closures default to type inference.
     */
    public data object Default : ReturnType() {
        override fun deepCopy(): Default = this
    }

    public data class TypeReturn(
        var arrowToken: RArrow,
        var ty: SynType,
    ) : ReturnType() {
        override fun deepCopy(): TypeReturn = TypeReturn(arrowToken, ty.deepCopy())
    }

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            Default -> Unit
            is TypeReturn -> {
                arrowToken.toTokens(tokens)
                ty.toTokens(tokens)
            }
        }
    }

    public abstract fun deepCopy(): ReturnType
}

internal fun parseBareFnArg(
    input: ParseStream,
    allowSelf: Boolean,
): SynResult<BareFnArg> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    return parseBareFnArg(input, attrs, allowSelf)
}

internal fun parseBareFnArg(
    input: ParseStream,
    attrs: List<Attribute>,
    allowSelf: Boolean,
): SynResult<BareFnArg> {
    val begin = input.fork()
    val hasMutSelf = allowSelf && input.peek(MutPeek) && input.peek2(SelfValuePeek)
    if (hasMutSelf) {
        MutParse.parse(input).getOrElse { return SynResult.failure(it) }
    }

    var hasSelf = false
    var name =
        if ((
                input.peek(IdentPeek) ||
                    input.peek(UnderscorePeek) ||
                    run {
                        hasSelf = allowSelf && input.peek(SelfValuePeek)
                        hasSelf
                    }
            ) &&
            input.peek2(ColonPeek) &&
            !input.peek2(PathSepPeek)
        ) {
            val ident = parseBareFnName(input).getOrElse { return SynResult.failure(it) }
            val colon = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            IdentColon(ident, colon)
        } else {
            hasSelf = false
            null
        }

    val parsedTy =
        if (allowSelf && !hasSelf && input.peek(MutPeek) && input.peek2(SelfValuePeek)) {
            MutParse.parse(input).getOrElse { return SynResult.failure(it) }
            SelfValueParse.parse(input).getOrElse { return SynResult.failure(it) }
            null
        } else if (hasMutSelf && name == null) {
            SelfValueParse.parse(input).getOrElse { return SynResult.failure(it) }
            null
        } else {
            parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        }

    val ty =
        if (parsedTy != null && !hasMutSelf) {
            parsedTy
        } else {
            name = null
            SynType.Verbatim(between(begin, input))
        }
    return SynResult.success(BareFnArg(attrs, name, ty))
}

internal fun parseBareVariadic(
    input: ParseStream,
    attrs: List<Attribute>,
): SynResult<BareVariadic> {
    val name =
        if (input.peek(IdentPeek) || input.peek(UnderscorePeek)) {
            val ident = parseBareFnName(input).getOrElse { return SynResult.failure(it) }
            val colon = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            IdentColon(ident, colon)
        } else {
            null
        }
    val dots = DotDotDotParse.parse(input).getOrElse { return SynResult.failure(it) }
    val comma = CommaParse.parse(input).getOrNull()
    return SynResult.success(BareVariadic(attrs, name, dots, comma))
}

private fun parseBareFnName(input: ParseStream): SynResult<Ident> {
    if (input.peek(UnderscorePeek)) {
        val underscore = UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(from(underscore))
    }
    return identParseAny(input)
}

public sealed class MacroDelimiter : ToTokens {
    public data class Paren(
        var token: io.github.kotlinmania.syn.token.Paren,
    ) : MacroDelimiter() {
        override fun toTokens(tokens: TokenStream) {
            token.surround(tokens) { }
        }
    }

    public data class Brace(
        var token: io.github.kotlinmania.syn.token.Brace,
    ) : MacroDelimiter() {
        override fun toTokens(tokens: TokenStream) {
            token.surround(tokens) { }
        }
    }

    public data class Bracket(
        var token: io.github.kotlinmania.syn.token.Bracket,
    ) : MacroDelimiter() {
        override fun toTokens(tokens: TokenStream) {
            token.surround(tokens) { }
        }
    }
}

public fun ambigTy(
    input: ParseStream,
    allowPlus: Boolean,
    allowGroupGeneric: Boolean,
): SynResult<SynType> = ambigTyWrapper(input, allowPlus, allowGroupGeneric)

private fun <T, R> SynResult<T>.asFailure(): SynResult<R> =
    SynResult.failure((this as SynResult.Failure).error)

internal fun parseTypeFull(input: ParseStream): SynResult<SynType> =
    ambigTyImpl(input, allowPlus = true, allowGroupGeneric = true)

internal fun parseTypeWithoutPlus(
    input: ParseStream,
    allowGroupGeneric: Boolean = true,
): SynResult<SynType> =
    ambigTyImpl(input, allowPlus = false, allowGroupGeneric = allowGroupGeneric)

internal fun ambigTyWrapper(
    input: ParseStream,
    allowPlus: Boolean,
    allowGroupGeneric: Boolean,
): SynResult<SynType> = ambigTyImpl(input, allowPlus, allowGroupGeneric)

internal object SynTypeParseExpr {
    fun parse(input: ParseStream): SynResult<SynType> =
        ambigTyImpl(input, allowPlus = true, allowGroupGeneric = true)
}

private fun ambigTyImpl(
    input: ParseStream,
    allowPlus: Boolean,
    allowGroupGeneric: Boolean,
): SynResult<SynType> {
    val begin = input.fork()
    val groupAhead = input.fork()
    val groupResult = parseGroup(groupAhead)
    if (groupResult.isSuccess) {
        input.advanceTo(groupAhead)
        val group = groupResult.getOrThrow()
        val elem = parseTypeFull(group.content)
        if (elem.isFailure) return elem.asFailure()
        group.content.finishChildBuffer()
        val elemValue = elem.getOrThrow()
        if (elemValue is SynType.Path) {
            if (allowGroupGeneric && (input.peek(LtPeek) || (input.peek(PathSepPeek) && input.peek3(LtPeek)))) {
                val last = elemValue.path.segments.last()
                if (last != null && last.arguments.isNone()) {
                    last.arguments =
                        parseAngleBracketedPathArguments(input).getOrElse {
                            return SynResult.failure(it)
                        }
                    parsePathRest(input, elemValue.path).getOrElse { return SynResult.failure(it) }
                    return SynResult.success(elemValue)
                }
            }
            if (input.peek(PathSepPeek) && input.peek3(IdentPeekAny)) {
                parsePathRest(input, elemValue.path).getOrElse { return SynResult.failure(it) }
                return SynResult.success(elemValue)
            }
        } else if (input.peek(PathSepPeek) && input.peek3(IdentPeekAny)) {
            val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
            val qself =
                QSelf(
                    io.github.kotlinmania.syn.token.Lt
                        .from(group.token.span),
                    elemValue,
                    position = 0,
                    asToken = null,
                    gtToken =
                        io.github.kotlinmania.syn.token.Gt
                            .from(group.token.span),
                )
            return SynResult.success(SynType.Path(qself, path))
        }
        return SynResult.success(SynType.Group(group.token, elemValue))
    }

    val lifetimes =
        if (input.peek(ForPeek)) {
            val parsed = parseBoundLifetimes(input).getOrElse { return SynResult.failure(it) }
            if (!canContinueAfterBoundLifetimes(input) || input.peek(DynPeek)) {
                return SynResult.failure(input.error("expected a type"))
            }
            parsed
        } else {
            null
        }

    if (input.peek(ParenPeek)) {
        val parens = parenthesized(input)
        if (parens.isFailure) return parens.asFailure()
        val parensVal = parens.getOrThrow()
        val content = parensVal.content
        if (content.isEmpty()) {
            content.finishChildBuffer()
            return SynResult.success(SynType.Tuple(parensVal.token, SynTypeList()))
        }
        if (content.peek(LifetimePeek)) {
            val traitObject = parseTypeTraitObject(content, allowPlus = true).getOrElse { return SynResult.failure(it) }
            content.finishChildBuffer()
            return SynResult.success(SynType.Paren(parensVal.token, traitObject))
        }
        if (content.peek(QuestionPeek)) {
            val bounds =
                parseTypeParamBoundsMultiple(
                    content,
                    allowPlus = true,
                    allowPreciseCapture = false,
                    allowConst = false,
                ).getOrElse { return SynResult.failure(it) }
            content.finishChildBuffer()
            val first = bounds.first()
            val traitBounds =
                if (first is TypeParamBound.Trait) {
                    withFirstTypeParamBound(bounds, first.copy(parenToken = parensVal.token))
                } else {
                    bounds
                }
            parseOuterTraitObjectBounds(input, traitBounds, allowPlus).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.TraitObject(null, traitBounds))
        }

        val first = ambigTyImpl(content, allowPlus = true, allowGroupGeneric = true).getOrElse { return SynResult.failure(it) }
        if (content.peek(CommaPeek)) {
            val elems = SynTypeList()
            elems.pushValue(first)
            elems.pushPunct(CommaParse.parse(content).getOrElse { return SynResult.failure(it) })
            while (!content.isEmpty()) {
                elems.pushValue(ambigTyImpl(content, allowPlus = true, allowGroupGeneric = true).getOrElse { return SynResult.failure(it) })
                if (content.isEmpty()) break
                elems.pushPunct(CommaParse.parse(content).getOrElse { return SynResult.failure(it) })
            }
            content.finishChildBuffer()
            return SynResult.success(SynType.Tuple(parensVal.token, elems))
        }
        content.finishChildBuffer()
        val firstBound = parenthesizedTypeAsBound(parensVal.token, first)
        if (allowPlus && firstBound != null && input.peek(PlusPeek)) {
            val bounds = TypeParamBoundList()
            bounds.pushValue(firstBound)
            parseOuterTraitObjectBounds(input, bounds, allowPlus).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.TraitObject(null, bounds))
        }
        return SynResult.success(SynType.Paren(parensVal.token, first))
    }

    if (input.peek(FnPeek) || input.peek(UnsafePeek) || input.peek(ExternPeek)) {
        return parseBareFnType(input, lifetimes).getOrElse { return SynResult.failure(it) }.let {
            SynResult.success(it)
        }
    }

    if (input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek) ||
        input.peek(LtPeek)
    ) {
        val qpathResult = qpath(input, exprStyle = false)
        if (qpathResult.isFailure) return qpathResult.asFailure()
        val (qself, path) = qpathResult.getOrThrow()
        if (qself != null) {
            return SynResult.success(SynType.Path(qself, path))
        }
        if (input.peek(NotPeek) && !input.peek(NePeek) && path.isModStyle()) {
            val bang = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
            val delimiter = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.Macro(Macro(path, bang, delimiter.first, delimiter.second)))
        }
        if (lifetimes != null || (allowPlus && input.peek(PlusPeek))) {
            val bounds = TypeParamBoundList()
            bounds.pushValue(TypeParamBound.Trait(null, TraitBoundModifier.None, lifetimes, path))
            parseOuterTraitObjectBounds(input, bounds, allowPlus).getOrElse { return SynResult.failure(it) }
            return SynResult.success(SynType.TraitObject(null, bounds))
        }
        return SynResult.success(SynType.Path(null, path))
    }

    if (input.peek(DynPeek)) {
        val dynToken = DynParse.parse(input).getOrElse { return SynResult.failure(it) }
        val starToken = StarParse.parse(input).getOrNull()
        val bounds = parseTraitObjectBounds(input, allowPlus).getOrElse { return SynResult.failure(it) }
        return if (starToken != null) {
            SynResult.success(SynType.Verbatim(between(begin, input)))
        } else {
            SynResult.success(SynType.TraitObject(dynToken, bounds))
        }
    }

    if (input.peek(BracketPeek)) {
        val brackets = bracketed(input)
        if (brackets.isFailure) return brackets.asFailure()
        val bracketsVal = brackets.getOrThrow()
        val elem = parseTypeFull(bracketsVal.content).getOrElse { return SynResult.failure(it) }
        if (bracketsVal.content.peek(SemiPeek)) {
            val semiToken = SemiParse.parse(bracketsVal.content).getOrElse { return SynResult.failure(it) }
            val len = parseExprFull(bracketsVal.content).getOrElse { return SynResult.failure(it) }
            bracketsVal.content.finishChildBuffer()
            return SynResult.success(SynType.Array(elem, len, bracketsVal.token, semiToken))
        }
        bracketsVal.content.finishChildBuffer()
        return SynResult.success(SynType.Slice(elem))
    }

    if (input.peek(StarPeek)) {
        val starToken = StarParse.parse(input).getOrThrow()
        val constToken: io.github.kotlinmania.syn.token.Const?
        val mutability: io.github.kotlinmania.syn.token.Mut?
        when {
            input.peek(ConstPeek) -> {
                constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
                mutability = null
            }

            input.peek(MutPeek) -> {
                constToken = null
                mutability = MutParse.parse(input).getOrElse { return SynResult.failure(it) }
            }

            else -> {
                return SynResult.failure(input.error("expected `const` or `mut`"))
            }
        }
        val inner = parseTypeWithoutPlus(input)
        if (inner.isFailure) return inner.asFailure()
        return SynResult.success(SynType.Ptr(starToken, constToken, mutability, inner.getOrThrow()))
    }

    if (input.peek(AndPeek)) {
        val andToken = AndParse.parse(input).getOrThrow()
        val ltResult = LifetimeParse.parse(input)
        val lifetime = if (ltResult.isSuccess) ltResult.getOrThrow() else null
        val mutResult = MutParse.parse(input)
        val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
        val inner = parseTypeWithoutPlus(input)
        if (inner.isFailure) return inner.asFailure()
        return SynResult.success(SynType.Reference(andToken, lifetime, mutability, inner.getOrThrow()))
    }

    if (input.peek(NotPeek) && !input.peek(NePeek)) {
        val bang = NotParse.parse(input).getOrThrow()
        return SynResult.success(SynType.Never(bang))
    }

    if (input.peek(ImplPeek)) {
        return parseTypeImplTrait(input, allowPlus).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }

    if (input.peek(UnderscorePeek)) {
        val underscore = UnderscoreParse.parse(input).getOrThrow()
        return SynResult.success(SynType.Infer(underscore))
    }

    if (input.peek(LifetimePeek)) {
        return parseTypeTraitObject(input, allowPlus).let { result ->
            if (result.isFailure) result.asFailure() else SynResult.success(result.getOrThrow())
        }
    }

    return SynResult.failure(input.error("expected a type"))
}

private fun canContinueAfterBoundLifetimes(input: ParseStream): Boolean =
    input.peek(IdentPeekAny) ||
        input.peek(FnPeek) ||
        input.peek(UnsafePeek) ||
        input.peek(ExternPeek) ||
        input.peek(SuperPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(CratePeek)

private fun parenthesizedTypeAsBound(
    parenToken: io.github.kotlinmania.syn.token.Paren,
    ty: SynType,
): TypeParamBound? =
    when (ty) {
        is SynType.Path -> {
            if (ty.qself == null) {
                TypeParamBound.Trait(parenToken, TraitBoundModifier.None, null, ty.path)
            } else {
                null
            }
        }

        is SynType.TraitObject -> {
            if (ty.dynToken == null && ty.bounds.size == 1 && !ty.bounds.trailingPunct()) {
                when (val bound = ty.bounds.first()) {
                    is TypeParamBound.Trait -> bound.copy(parenToken = parenToken)
                    is TypeParamBound.LifetimeBound -> bound
                    else -> null
                }
            } else {
                null
            }
        }

        else -> {
            null
        }
    }

private fun parseOuterTraitObjectBounds(
    input: ParseStream,
    bounds: TypeParamBoundList,
    allowPlus: Boolean,
): SynResult<Unit> {
    while (allowPlus && input.peek(PlusPeek)) {
        bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
        if (!canStartTypeParamBound(input, allowConst = false)) break
        val rest =
            parseTypeParamBoundsMultiple(
                input,
                allowPlus = true,
                allowPreciseCapture = false,
                allowConst = false,
            ).getOrElse { return SynResult.failure(it) }
        appendTypeParamBounds(bounds, rest)
        break
    }
    return SynResult.success(Unit)
}

private fun parseTraitObjectBounds(
    input: ParseStream,
    allowPlus: Boolean,
): SynResult<TypeParamBoundList> {
    val dynSpan = input.span()
    return SynType.TraitObject.parseBounds(dynSpan, input, allowPlus)
}

private fun canStartTypeParamBound(
    input: ParseStream,
    allowConst: Boolean,
): Boolean =
    input.peek(IdentPeekAny) ||
        input.peek(PathSepPeek) ||
        input.peek(QuestionPeek) ||
        input.peek(LifetimePeek) ||
        input.peek(ParenPeek) ||
        (allowConst && (input.peek(BracketPeek) || input.peek(ConstPeek)))

private fun appendTypeParamBounds(
    target: TypeParamBoundList,
    source: TypeParamBoundList,
) {
    for ((bound, punct) in source.pairsList()) {
        target.pushValue(bound as TypeParamBound)
        punct?.let { target.pushPunct(it) }
    }
}

private fun withFirstTypeParamBound(
    source: TypeParamBoundList,
    first: TypeParamBound,
): TypeParamBoundList {
    val replaced = TypeParamBoundList()
    for ((index, pair) in source.pairsList().withIndex()) {
        val (bound, punct) = pair
        replaced.pushValue(if (index == 0) first else bound as TypeParamBound)
        punct?.let { replaced.pushPunct(it) }
    }
    return replaced
}

internal fun parseTypeTraitObject(
    input: ParseStream,
    allowPlus: Boolean,
): SynResult<SynType.TraitObject> {
    val dynToken = DynParse.parse(input).getOrNull()
    val bounds = parseTraitObjectBounds(input, allowPlus).getOrElse { return SynResult.failure(it) }
    return SynResult.success(SynType.TraitObject(dynToken, bounds))
}

internal fun parseTypeImplTrait(
    input: ParseStream,
    allowPlus: Boolean,
): SynResult<SynType.ImplTrait> {
    val implToken = ImplParse.parse(input).getOrElse { return SynResult.failure(it) }
    val bounds =
        parseTypeParamBoundsMultiple(
            input,
            allowPlus = allowPlus,
            allowPreciseCapture = true,
            allowConst = true,
        ).getOrElse { return SynResult.failure(it) }
    if (!bounds.hasTraitLikeImplBound()) {
        return SynResult.failure(input.error("at least one trait must be specified"))
    }
    return SynResult.success(SynType.ImplTrait(implToken, bounds))
}

private fun TypeParamBoundList.hasTraitLikeImplBound(): Boolean =
    toList().any { it is TypeParamBound.Trait || it is TypeParamBound.Verbatim }

private fun parseBareFnType(
    input: ParseStream,
    lifetimes: BoundLifetimes?,
): SynResult<SynType.BareFn> {
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val abi =
        if (input.peek(ExternPeek)) {
            parseAbi(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    val fnToken = FnParse.parse(input).getOrElse { return SynResult.failure(it) }
    val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    val inputs = BareFnArgList()
    var variadic: BareVariadic? = null
    val args = parens.content
    while (!args.isEmpty()) {
        val attrs = parseOuterAttributes(args).getOrElse { return SynResult.failure(it) }
        if (inputs.emptyOrTrailing() &&
            (
                args.peek(DotDotDotPeek) ||
                    (
                        (args.peek(IdentPeek) || args.peek(UnderscorePeek)) &&
                            args.peek2(ColonPeek) &&
                            args.peek3(DotDotDotPeek)
                    )
            )
        ) {
            variadic = parseBareVariadic(args, attrs).getOrElse { return SynResult.failure(it) }
            break
        }
        val arg =
            parseBareFnArg(args, attrs, allowSelf = inputs.isEmpty()).getOrElse {
                return SynResult.failure(it)
            }
        inputs.pushValue(arg)
        if (args.isEmpty()) break
        val comma = CommaParse.parse(args).getOrElse { return SynResult.failure(it) }
        inputs.pushPunct(comma)
    }
    args.finishChildBuffer()
    val output = parseReturnTypeWithoutPlus(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(SynType.BareFn(lifetimes, unsafety, abi, fnToken, parens.token, inputs, variadic, output))
}

private fun parsePathRest(input: ParseStream, path: Path): SynResult<Unit> {
    while (input.peek(PathSepPeek)) {
        val sep = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
        path.segments.pushPunct(sep)
        val segment = PathSegmentParse.parse(input).getOrElse { return SynResult.failure(it) }
        path.segments.pushValue(segment)
    }
    return SynResult.success(Unit)
}

public fun parseReturnType(input: ParseStream): SynResult<ReturnType> =
    parseReturnType(input, allowPlus = true)

public fun parseReturnTypeWithoutPlus(input: ParseStream): SynResult<ReturnType> =
    parseReturnType(input, allowPlus = false)

private fun parseReturnType(input: ParseStream, allowPlus: Boolean): SynResult<ReturnType> {
    if (!input.peek(RArrowPeek)) {
        return SynResult.success(ReturnType.Default)
    }
    val arrow = RArrowParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty =
        if (allowPlus) {
            parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        } else {
            parseTypeWithoutPlus(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(ReturnType.TypeReturn(arrow, ty))
}
