// port-lint: source pat.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Underscore

/**
 * A pattern in a local binding, function signature, pattern-matching expression, or
 * various other places.
 */
public sealed class Pat : ToTokens {
    public abstract fun deepCopy(): Pat

    public companion object {
        fun parse(input: ParseStream): SynResult<Pat> =
            parseSingle(input)

        fun parseSingle(input: ParseStream): SynResult<Pat> {
            val begin = input.fork()
            val lookahead = input.lookahead1()
            if (
                input.peek(IdentPeek) &&
                (
                    input.peek2(PathSepPeek) ||
                        input.peek2(NotPeek) ||
                        input.peek2(BracePeek) ||
                        input.peek2(ParenPeek) ||
                        input.peek2(DotDotPeek) ||
                        input.peek2(DotDotEqPeek) ||
                        input.peek2(DotDotDotPeek)
                ) ||
                input.peek(SelfValuePeek) &&
                input.peek2(PathSepPeek) ||
                input.peek(PathSepPeek) ||
                input.peek(LtPeek) ||
                input.peek(SelfTypePeek) ||
                input.peek(SuperPeek) ||
                input.peek(CratePeek)
            ) {
                return patPathOrMacroOrStructOrRange(input)
            } else if (input.peek(UnderscorePeek)) {
                return patWild(input).map { it }
            } else if (input.peek(BoxPeek)) {
                return patBox(begin, input)
            } else if (input.peek(MinusPeek) || input.peek(LitPeek) || input.peek(ConstPeek)) {
                return patLitOrRange(input)
            } else if (
                input.peek(RefPeek) ||
                input.peek(MutPeek) ||
                input.peek(SelfValuePeek) ||
                input.peek(IdentPeek)
            ) {
                return patIdent(input).map { it }
            } else if (input.peek(AndPeek)) {
                return patReference(input).map { it }
            } else if (input.peek(ParenPeek)) {
                return patParenOrTuple(input)
            } else if (input.peek(BracketPeek)) {
                return patSlice(input).map { it }
            } else if ((input.peek(DotDotPeek) || input.peek(DotDotEqPeek)) && !input.peek(DotDotDotPeek)) {
                return patRangeHalfOpen(input)
            } else if (input.peek(ConstPeek)) {
                return patConst(input).map(::Verbatim)
            }
            return SynResult.failure(lookahead.error())
        }

        /** Parse a pattern, possibly involving `|`, but not a leading `|`. */
        public fun parseMulti(input: ParseStream): SynResult<Pat> =
            multiPatImpl(input, leadingVert = null)

        /** Parse a pattern, possibly involving `|`, possibly including a leading `|`. */
        public fun parseMultiWithLeadingVert(input: ParseStream): SynResult<Pat> {
            val leadingVert =
                if (input.peek(OrPeek) && !input.peek(OrOrPeek) && !input.peek(OrEqPeek)) {
                    OrParse.parse(input).getOrElse { return SynResult.failure(it) }
                } else {
                    null
                }
            return multiPatImpl(input, leadingVert)
        }
    }

    /** A pattern that binds a new variable, optionally with a reference, mutability, and sub-pattern. */
    public data class Ident(
        public var attrs: MutableList<Attribute>,
        public var byRef: io.github.kotlinmania.syn.token.Ref?,
        public var mutability: FieldMutability,
        public var ident: io.github.kotlinmania.procmacro2.Ident,
        public var atToken: io.github.kotlinmania.syn.token.At?,
        public var subpat: Pat?,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() })

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            byRef?.toTokens(tokens)
            mutability.toTokens(tokens)
            ident.toTokens(tokens)
            atToken?.toTokens(tokens)
            subpat?.toTokens(tokens)
        }
    }

    /** A tuple pattern: `(A, B, C)`. */
    public data class Tuple(
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var elems: PatList,
        public var attrs: MutableList<Attribute> = mutableListOf(),
    ) : Pat() {
        override fun deepCopy(): Pat =
            copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, elems = elems.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
                if (elems.len() == 1 && !elems.trailingPunct() && elems.first() !is Rest) {
                    io.github.kotlinmania.syn.token.Comma
                        .default()
                        .toTokens(inner)
                }
            }
        }
    }

    /** A pattern that matches any one of a set of cases. */
    public data class Or(
        public var leadingVert: io.github.kotlinmania.syn.token.Or?,
        public var cases: PatList,
        public var attrs: MutableList<Attribute> = mutableListOf(),
    ) : Pat() {
        override fun deepCopy(): Pat =
            copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, cases = cases.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            leadingVert?.toTokens(tokens)
            for ((case, vert) in cases.pairsList()) {
                case.toTokens(tokens)
                vert?.toTokens(tokens)
            }
        }
    }

    /** A parenthesized pattern: `(A | B)`. */
    public data class PatParen(
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var pat: Pat,
        public var attrs: MutableList<Attribute> = mutableListOf(),
    ) : Pat() {
        override fun deepCopy(): Pat =
            copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, pat = pat.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner -> pat.toTokens(inner) }
        }
    }

    /** A mutable reference pattern. */
    public data class Reference(
        public var andToken: io.github.kotlinmania.syn.token.And,
        public var mutability: FieldMutability,
        public var pat: Pat,
        public var attrs: MutableList<Attribute> = mutableListOf(),
    ) : Pat() {
        override fun deepCopy(): Pat =
            copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, andToken = andToken, mutability = mutability, pat = pat.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            andToken.toTokens(tokens)
            mutability.toTokens(tokens)
            pat.toTokens(tokens)
        }
    }

    /** A data-object pattern. */
    public data class Struct(
        public var qself: QSelf?,
        public var path: io.github.kotlinmania.syn.Path,
        public var braceToken: io.github.kotlinmania.syn.token.Brace,
        public var fields: FieldPatList,
        public var rest: PatRest?,
        public var dot2Token: io.github.kotlinmania.syn.token.DotDot?,
        public var attrs: MutableList<Attribute> = mutableListOf(),
    ) : Pat() {
        override fun deepCopy(): Pat =
            copy(
                attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() },
                qself = qself,
                path = path.deepCopy(),
                fields = fields.copy({ it.deepCopy() }, { it }),
                rest = rest?.deepCopy(),
                dot2Token = dot2Token,
            )

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                fields.toTokens(inner)
                if (!fields.emptyOrTrailing() && rest != null) {
                    io.github.kotlinmania.syn.token.Comma
                        .default()
                        .toTokens(inner)
                }
                rest?.toTokens(inner)
                dot2Token?.toTokens(inner)
            }
        }
    }

    /** A slice pattern: `[a, b.., c]`. */
    public data class Slice(
        public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public var elems: PatList,
        public var attrs: MutableList<Attribute> = mutableListOf(),
    ) : Pat() {
        override fun deepCopy(): Pat =
            copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, elems = elems.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            bracketToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }
    }

    /** A type ascription pattern. */
    public data class TypeAscription(
        public var attrs: MutableList<Attribute>,
        public var pat: Pat,
        public var colonToken: Colon,
        public var ty: SynType,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, pat = pat.deepCopy(), ty = ty.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            pat.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
        }
    }

    /** A const block pattern: `const { ... }`. */
    public data class Const(
        public var attrs: MutableList<Attribute>,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var block: Block,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, block = block.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constToken.toTokens(tokens)
            block.toTokens(tokens)
        }
    }

    /** A literal pattern: `0`. */
    public data class Lit(
        public var attrs: MutableList<Attribute>,
        public var lit: io.github.kotlinmania.syn.Lit,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, lit = lit)

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            lit.toTokens(tokens)
        }
    }

    /** A macro invocation in pattern position. */
    public data class Macro(
        public var attrs: MutableList<Attribute>,
        public var mac: io.github.kotlinmania.syn.Macro,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, mac = mac.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
        }
    }

    /** A path pattern like `Color::Red`, optionally qualified with a self-type. */
    public data class Path(
        public var attrs: MutableList<Attribute>,
        public var qself: QSelf?,
        public var path: io.github.kotlinmania.syn.Path,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, qself = qself, path = path.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
        }
    }

    /** A range pattern: `1..=2`. */
    public data class Range(
        public var attrs: MutableList<Attribute>,
        public var start: Expr?,
        public var limits: RangeLimits,
        public var end: Expr?,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, start = start?.deepCopy(), end = end?.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            start?.toTokens(tokens)
            limits.toTokens(tokens)
            end?.toTokens(tokens)
        }
    }

    /** The dots in a tuple or slice pattern: `[0, 1, ..]`. */
    public data class Rest(
        public var attrs: MutableList<Attribute>,
        public var dot2Token: io.github.kotlinmania.syn.token.DotDot,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() })

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            dot2Token.toTokens(tokens)
        }
    }

    /** A tuple struct or tuple variant pattern: `Variant(x, y, .., z)`. */
    public data class TupleStruct(
        public var attrs: MutableList<Attribute>,
        public var qself: QSelf?,
        public var path: io.github.kotlinmania.syn.Path,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var elems: PatList,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() }, qself = qself, path = path.deepCopy(), elems = elems.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
            parenToken.surround(tokens) { inner -> elems.toTokens(inner) }
        }
    }

    /** A pattern that matches any value. */
    public data class Wild(
        public var attrs: MutableList<Attribute>,
        public var underscoreToken: Underscore,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.mapTo(mutableListOf()) { it.deepCopy() })

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            underscoreToken.toTokens(tokens)
        }
    }

    /** Tokens forming a pattern not interpreted by Syn. */
    public data class Verbatim(
        public var tokens: TokenStream,
    ) : Pat() {
        override fun deepCopy(): Pat = this

        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }
    }
}

/** A field in a data-object pattern. */
public data class FieldPat(
    public var member: Member,
    public var colonToken: Colon?,
    public var pat: Pat,
    public var attrs: MutableList<Attribute> = mutableListOf(),
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        if (colonToken != null) {
            member.toTokens(tokens)
            colonToken.toTokens(tokens)
        }
        pat.toTokens(tokens)
    }

    public fun deepCopy(): FieldPat =
        FieldPat(member, colonToken, pat.deepCopy(), attrs.mapTo(mutableListOf()) { it.deepCopy() })
}

/** The rest pattern in a data-object pattern. */
public data class PatRest(
    public var dot2Token: io.github.kotlinmania.syn.token.DotDot?,
    public var attrs: MutableList<Attribute> = mutableListOf(),
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        dot2Token?.toTokens(tokens)
    }

    public fun deepCopy(): PatRest =
        PatRest(dot2Token, attrs.mapTo(mutableListOf()) { it.deepCopy() })
}

/** A type ascription pattern. */
public data class PatType(
    public var attrs: MutableList<Attribute>,
    public var pat: Pat,
    public var colonToken: Colon,
    public var ty: SynType,
) : ToTokens {
    public companion object {
        fun parse(input: ParseStream): SynResult<PatType> {
            val pat = Pat.parseSingle(input).getOrElse { return SynResult.failure(it) }
            val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(PatType(emptyList(), pat, colonToken, ty))
        }
    }

    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        pat.toTokens(tokens)
        colonToken.toTokens(tokens)
        ty.toTokens(tokens)
    }

    public fun deepCopy(): PatType = PatType(attrs.mapTo(mutableListOf()) { it.deepCopy() }, pat.deepCopy(), colonToken, ty.deepCopy())
}

private fun multiPatImpl(
    input: ParseStream,
    leadingVert: io.github.kotlinmania.syn.token.Or?,
): SynResult<Pat> {
    var pat = Pat.parseSingle(input).getOrElse { return SynResult.failure(it) }
    if (leadingVert != null || (input.peek(OrPeek) && !input.peek(OrOrPeek) && !input.peek(OrEqPeek))) {
        var cases = PatList()
        cases.pushValue(pat)
        while (input.peek(OrPeek) && !input.peek(OrOrPeek) && !input.peek(OrEqPeek)) {
            cases.pushPunct(OrParse.parse(input).getOrElse { return SynResult.failure(it) })
            cases.pushValue(Pat.parseSingle(input).getOrElse { return SynResult.failure(it) })
        }
        pat = Pat.Or(leadingVert = leadingVert, cases = cases, attrs = mutableListOf())
    }
    return SynResult.success(pat)
}

private fun patPathOrMacroOrStructOrRange(input: ParseStream): SynResult<Pat> {
    var (qself, path) = qpath(input, exprStyle = true).getOrElse { return SynResult.failure(it) }

    if (qself == null && input.peek(NotPeek) && !input.peek(NePeek) && path.isModStyle()) {
        var bangToken = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
        var (delimiter, tokens) = parseDelimiter(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(
            Pat.Macro(
                attrs = mutableListOf(),
                mac = Macro(path, bangToken, delimiter, tokens),
            ),
        )
    }

    if (input.peek(BracePeek)) {
        return patStruct(input, qself, path).map { it }
    }
    if (input.peek(ParenPeek)) {
        return patTupleStruct(input, qself, path).map { it }
    }
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek) || input.peek(DotDotDotPeek)) {
        return patRange(input, qself, path)
    }
    return SynResult.success(Pat.Path(emptyList(), qself, path))
}

private fun patWild(input: ParseStream): SynResult<Pat.Wild> =
    SynResult.success(Pat.Wild(emptyList(), UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) }))

private fun patBox(begin: ParseStream, input: ParseStream): SynResult<Pat> {
    BoxParse.parse(input).getOrElse { return SynResult.failure(it) }
    Pat.parseSingle(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Pat.Verbatim(between(begin, input)))
}

private fun patIdent(input: ParseStream): SynResult<Pat.Ident> {
    var byRef = RefParse.parse(input).getOrNull()
    var mutability =
        MutParse.parse(input).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
    var ident =
        if (input.peek(SelfValuePeek)) {
            identFromSelfValue(SelfValueParse.parse(input).getOrElse { return SynResult.failure(it) })
        } else {
            IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    var atToken =
        if (input.peek(AtPeek)) {
            AtParse.parse(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    var subpat =
        if (atToken != null) {
            Pat.parseSingle(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    return SynResult.success(Pat.Ident(emptyList(), byRef, mutability, ident, atToken, subpat))
}

private fun patTupleStruct(
    input: ParseStream,
    qself: QSelf?,
    path: Path,
): SynResult<Pat.TupleStruct> {
    var parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    var elems = PatList()
    while (!parens.content.isEmpty()) {
        elems.pushValue(Pat.parseMultiWithLeadingVert(parens.content).getOrElse { return SynResult.failure(it) })
        if (parens.content.isEmpty()) break
        elems.pushPunct(CommaParse.parse(parens.content).getOrElse { return SynResult.failure(it) })
    }
    parens.content.finishChildBuffer()
    return SynResult.success(Pat.TupleStruct(emptyList(), qself, path, parens.token, elems))
}

private fun patStruct(
    input: ParseStream,
    qself: QSelf?,
    path: Path,
): SynResult<Pat.Struct> {
    var braces = braced(input).getOrElse { return SynResult.failure(it) }
    var fields = FieldPatList()
    var rest: PatRest? = null
    while (!braces.content.isEmpty()) {
        var attrs = parseOuterAttributes(braces.content).getOrElse { return SynResult.failure(it) }
        if (braces.content.peek(DotDotPeek) && !braces.content.peek(DotDotDotPeek)) {
            rest =
                PatRest(
                    dot2Token = DotDotParse.parse(braces.content).getOrElse { return SynResult.failure(it) },
                    attrs = attrs,
                )
            break
        }
        var value = fieldPat(braces.content).getOrElse { return SynResult.failure(it) }
        fields.pushValue(value.copy(attrs = attrs))
        if (braces.content.isEmpty()) break
        fields.pushPunct(CommaParse.parse(braces.content).getOrElse { return SynResult.failure(it) })
    }
    braces.content.finishChildBuffer()
    return SynResult.success(Pat.Struct(qself, path, braces.token, fields, rest, null, emptyList()))
}

private fun fieldPat(input: ParseStream): SynResult<FieldPat> {
    var begin = input.fork()
    var boxed = BoxParse.parse(input).getOrNull()
    var byRef = RefParse.parse(input).getOrNull()
    var mutability =
        MutParse.parse(input).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None

    var member =
        if (boxed != null || byRef != null || mutability is FieldMutability.Mut) {
            Member.Named(IdentParse.parse(input).getOrElse { return SynResult.failure(it) })
        } else {
            parseMemberImpl(input).getOrElse { return SynResult.failure(it) }
        }

    if ((boxed == null && byRef == null && mutability is FieldMutability.None && input.peek(ColonPeek)) ||
        member !is Member.Named
    ) {
        var colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        var pat = Pat.parseMultiWithLeadingVert(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(FieldPat(member, colonToken, pat))
    }

    var ident = member.ident
    var pat =
        if (boxed != null) {
            Pat.Verbatim(between(begin, input))
        } else {
            Pat.Ident(emptyList(), byRef, mutability, ident, null, null)
        }
    return SynResult.success(FieldPat(Member.Named(ident), null, pat))
}

private fun patRange(
    input: ParseStream,
    qself: QSelf?,
    path: Path,
): SynResult<Pat> {
    var limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
    var end = patRangeBound(input).getOrElse { return SynResult.failure(it) }
    if (limits is RangeLimits.Closed && end == null) {
        return SynResult.failure(input.error("expected range upper bound"))
    }
    return SynResult.success(
        Pat.Range(
            attrs = mutableListOf(),
            start = Expr.Path(emptyList(), qself, path),
            limits = limits,
            end = end?.intoExpr(),
        ),
    )
}

private fun patRangeHalfOpen(input: ParseStream): SynResult<Pat> {
    var limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
    var end = patRangeBound(input).getOrElse { return SynResult.failure(it) }
    if (end != null) {
        return SynResult.success(Pat.Range(emptyList(), null, limits, end.intoExpr()))
    }
    return when (limits) {
        is RangeLimits.HalfOpen -> SynResult.success(Pat.Rest(emptyList(), limits.token))
        is RangeLimits.Closed -> SynResult.failure(input.error("expected range upper bound"))
    }
}

private fun patParenOrTuple(input: ParseStream): SynResult<Pat> {
    var parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    var content = parens.content
    var elems = PatList()
    while (!content.isEmpty()) {
        var value = Pat.parseMultiWithLeadingVert(content).getOrElse { return SynResult.failure(it) }
        if (content.isEmpty()) {
            if (elems.isEmpty() && value !is Pat.Rest) {
                content.finishChildBuffer()
                return SynResult.success(Pat.PatParen(parens.token, value, emptyList()))
            }
            elems.pushValue(value)
            break
        }
        elems.pushValue(value)
        elems.pushPunct(CommaParse.parse(content).getOrElse { return SynResult.failure(it) })
    }
    content.finishChildBuffer()
    return SynResult.success(Pat.Tuple(parens.token, elems, emptyList()))
}

private fun patReference(input: ParseStream): SynResult<Pat.Reference> {
    var andToken = AndParse.parse(input).getOrElse { return SynResult.failure(it) }
    var mutability =
        MutParse.parse(input).getOrNull()?.let { FieldMutability.Mut(it) } ?: FieldMutability.None
    var pat = Pat.parseSingle(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Pat.Reference(andToken, mutability, pat, emptyList()))
}

private fun patLitOrRange(input: ParseStream): SynResult<Pat> {
    var start =
        patRangeBound(input).getOrElse { return SynResult.failure(it) }
            ?: return SynResult.failure(input.error("expected range bound"))
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek) || input.peek(DotDotDotPeek)) {
        var limits = parsePatRangeLimitsObsolete(input).getOrElse { return SynResult.failure(it) }
        var end = patRangeBound(input).getOrElse { return SynResult.failure(it) }
        if (limits is RangeLimits.Closed && end == null) {
            return SynResult.failure(input.error("expected range upper bound"))
        }
        return SynResult.success(Pat.Range(emptyList(), start.intoExpr(), limits, end?.intoExpr()))
    }
    return SynResult.success(start.intoPat())
}

private sealed class PatRangeBound {
    data class Const(
        var pat: Expr.Const,
    ) : PatRangeBound()

    data class Lit(
        var pat: Expr.Lit,
    ) : PatRangeBound()

    data class Path(
        var pat: Expr.Path,
    ) : PatRangeBound()

    fun intoExpr(): Expr =
        when (this) {
            is Const -> pat
            is Lit -> pat
            is Path -> pat
        }

    fun intoPat(): Pat =
        when (this) {
            is Const -> Pat.Const(pat.attrs, pat.constToken, pat.block)
            is Lit -> Pat.Lit(pat.attrs, pat.lit)
            is Path -> Pat.Path(pat.attrs, pat.qself, pat.path)
        }
}

private fun patRangeBound(input: ParseStream): SynResult<PatRangeBound?> {
    if (
        input.isEmpty() ||
        input.peek(OrPeek) ||
        input.peek(EqPeek) ||
        input.peek(ColonPeek) &&
        !input.peek(PathSepPeek) ||
        input.peek(CommaPeek) ||
        input.peek(SemiPeek) ||
        input.peek(IfPeek)
    ) {
        return SynResult.success(null)
    }

    var lookahead = input.lookahead1()
    if (input.peek(LitPeek) || input.peek(MinusPeek)) {
        var lit = LitParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(PatRangeBound.Lit(Expr.Lit(emptyList(), lit)))
    }
    if (
        input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(LtPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek)
    ) {
        var (qself, path) = qpath(input, exprStyle = true).getOrElse { return SynResult.failure(it) }
        return SynResult.success(PatRangeBound.Path(Expr.Path(emptyList(), qself, path)))
    }
    if (input.peek(ConstPeek)) {
        return parsePatConstExpr(input).map { PatRangeBound.Const(it) }
    }
    return SynResult.failure(lookahead.error())
}

private fun patSlice(input: ParseStream): SynResult<Pat.Slice> {
    var brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
    var elems = PatList()
    while (!brackets.content.isEmpty()) {
        var value = Pat.parseMultiWithLeadingVert(brackets.content).getOrElse { return SynResult.failure(it) }
        if (value is Pat.Range && (value.start == null || value.end == null)) {
            val (start, end) =
                when (val limits = value.limits) {
                    is RangeLimits.HalfOpen -> limits.token.spans[0] to limits.token.spans[1]
                    is RangeLimits.Closed -> limits.token.spans[0] to limits.token.spans[2]
                }
            return SynResult.failure(
                SynError.new2(start, end, "range pattern is not allowed unparenthesized inside slice pattern"),
            )
        }
        elems.pushValue(value)
        if (brackets.content.isEmpty()) break
        elems.pushPunct(CommaParse.parse(brackets.content).getOrElse { return SynResult.failure(it) })
    }
    brackets.content.finishChildBuffer()
    return SynResult.success(Pat.Slice(brackets.token, elems, emptyList()))
}

private fun patConst(input: ParseStream): SynResult<TokenStream> {
    var begin = input.fork()
    ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
    var braces = braced(input).getOrElse { return SynResult.failure(it) }
    parseInnerAttributes(braces.content).getOrElse { return SynResult.failure(it) }
    parseWithin(braces.content).getOrElse { return SynResult.failure(it) }
    braces.content.finishChildBuffer()
    return SynResult.success(between(begin, input))
}

private fun parsePatConstExpr(input: ParseStream): SynResult<Expr.Const> {
    var constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
    var braces = braced(input).getOrElse { return SynResult.failure(it) }
    var stmts = parseWithin(braces.content).getOrElse { return SynResult.failure(it) }
    braces.content.finishChildBuffer()
    return SynResult.success(Expr.Const(emptyList(), constToken, Block(braces.token, stmts)))
}

private fun parsePatRangeLimitsObsolete(input: ParseStream): SynResult<RangeLimits> {
    if (input.peek(DotDotDotPeek)) {
        var dots = DotDotDotParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(
            RangeLimits.Closed(
                io.github.kotlinmania.syn.token.DotDotEq
                    .from(dots.spans),
            ),
        )
    }
    return RangeLimitsParse.parse(input)
}
