// port-lint: source path.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.PathSep

/** A path at which a named item is exported. */
public class Path(
    public var leadingColon: PathSep?,
    public var segments: PathSegmentList,
) {
    public companion object {
        public fun from(segment: Ident): Path {
            val path = Path(null, PathSegmentList())
            path.segments.pushValue(PathSegment.from(segment))
            return path
        }

        public fun from(segment: PathSegment): Path {
            val path = Path(null, PathSegmentList())
            path.segments.pushValue(segment)
            return path
        }

        /** Parse a path containing no path arguments on any of its segments. */
        public fun parseModStyle(input: ParseStream): SynResult<Path> {
            val leadingColon =
                if (input.peek(PathSepPeek)) {
                    PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
                } else {
                    null
                }
            val segments = PathSegmentList()
            while (
                input.peek(IdentPeek) ||
                input.peek(SuperPeek) ||
                input.peek(SelfValuePeek) ||
                input.peek(SelfTypePeek) ||
                input.peek(CratePeek)
            ) {
                val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
                segments.pushValue(PathSegment.from(ident))
                if (!input.peek(PathSepPeek)) break
                val punct = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
                segments.pushPunct(punct)
            }
            if (segments.isEmpty()) {
                return SynResult.failure(IdentParse.parse(input).exceptionOrNull()!!)
            }
            if (segments.trailingPunct()) {
                return SynResult.failure(input.error("expected path segment after `::`"))
            }
            return SynResult.success(Path(leadingColon, segments))
        }

        internal fun parseHelper(input: ParseStream, exprStyle: Boolean): SynResult<Path> {
            val leadingColon =
                if (input.peek(PathSepPeek)) {
                    PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
                } else {
                    null
                }
            val segments = PathSegmentList()
            segments.pushValue(PathSegment.parseHelper(input, exprStyle).getOrElse { return SynResult.failure(it) })
            val path = Path(leadingColon, segments)
            parseRest(input, path, exprStyle).getOrElse { return SynResult.failure(it) }
            return SynResult.success(path)
        }

        internal fun parseRest(
            input: ParseStream,
            path: Path,
            exprStyle: Boolean,
        ): SynResult<Unit> {
            while (input.peek(PathSepPeek) && !input.peek3(ParenPeek)) {
                val punct = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
                path.segments.pushPunct(punct)
                val value = PathSegment.parseHelper(input, exprStyle).getOrElse { return SynResult.failure(it) }
                path.segments.pushValue(value)
            }
            return SynResult.success(Unit)
        }
    }

    /** Determines whether this is a path of length 1 equal to the given ident. */
    public fun isIdent(ident: String): Boolean =
        getIdent()?.toString() == ident

    public fun isIdent(ident: Ident): Boolean =
        getIdent() == ident

    /** If this path consists of a single ident, returns the ident. */
    public fun getIdent(): Ident? {
        if (leadingColon != null || segments.len() != 1) {
            return null
        }
        val segment = segments.first() ?: return null
        return if (segment.arguments.isNone()) segment.ident else null
    }

    public fun requireIdent(): Ident =
        getIdent() ?: throw SynError.new(
            segments.first()?.ident?.span() ?: io.github.kotlinmania.procmacro2.Span
                .callSite(),
            "expected this path to be an identifier",
        )

    public fun span(): Span {
        val first = segments.first()?.ident?.span() ?: Span.callSite()
        val last = segments.last()?.ident?.span() ?: first
        return first.join(last) ?: first
    }

    public fun isModStyle(): Boolean {
        if (leadingColon != null) return false
        for (segment in segments.toList()) {
            if (segment.arguments !is PathArguments.None) return false
        }
        return true
    }

    public fun toTokens(tokens: TokenStream) {
        printPath(tokens, this, PathStyle.AsWritten)
    }

    override fun toString(): String =
        segments.toList().joinToString("::") { it.ident.toString() }.let {
            if (leadingColon != null) "::$it" else it
        }

    override fun equals(other: Any?): Boolean =
        other is Path && leadingColon == other.leadingColon && segments == other.segments

    override fun hashCode(): Int =
        31 * (leadingColon?.hashCode() ?: 0) + segments.hashCode()

    public fun deepCopy(): Path =
        Path(
            leadingColon = leadingColon,
            segments = segments.copy({ it.deepCopy() }, { it }),
        )

    public fun clone(): Path = deepCopy()
}

/** A segment of a path together with any path arguments on that segment. */
public data class PathSegment(
    public var ident: Ident,
    public var arguments: PathArguments = PathArguments.None,
) : ToTokens {
    public companion object {
        public fun from(ident: Ident): PathSegment =
            PathSegment(ident, PathArguments.None)

        internal fun parseHelper(input: ParseStream, exprStyle: Boolean): SynResult<PathSegment> {
            if (
                input.peek(SuperPeek) ||
                input.peek(SelfValuePeek) ||
                input.peek(CratePeek) ||
                input.peek(TryPeek)
            ) {
                val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
                return SynResult.success(from(ident))
            }

            val ident =
                if (input.peek(SelfTypePeek)) {
                    identParseAny(input).getOrElse { return SynResult.failure(it) }
                } else {
                    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                }
            val arguments =
                if (
                    (!exprStyle && input.peek(GenericLtPeek) && !input.peek(LePeek) && !input.peek(ShlEqPeek)) ||
                    (input.peek(PathSepPeek) && input.peek3(GenericLtPeek))
                ) {
                    PathArguments.AngleBracketed.parse(input).getOrElse { return SynResult.failure(it) }
                } else {
                    PathArguments.None
                }
            return SynResult.success(PathSegment(ident, arguments))
        }
    }

    public fun deepCopy(): PathSegment =
        PathSegment(ident.copy(), arguments.deepCopy())

    override fun toTokens(tokens: TokenStream) {
        printPathSegment(tokens, this, PathStyle.AsWritten)
    }

    override fun toString(): String =
        if (arguments.isNone()) ident.toString() else "$ident$arguments"
}

/** Angle bracketed or parenthesized arguments of a path segment. */
public sealed class PathArguments : ToTokens {
    public companion object {
        public fun default(): PathArguments =
            None
    }

    public data object None : PathArguments() {
        override fun toTokens(tokens: TokenStream) {
        }
    }

    /** Generic arguments surrounded by angle brackets. */
    public data class AngleBracketed(
        public var colon2Token: PathSep?,
        public var ltToken: Lt,
        public var args: GenericArgumentList,
        public var gtToken: Gt,
    ) : PathArguments() {
        public companion object {
            /** Parse `::<...>` with mandatory leading `::`. */
            public fun parseTurbofish(input: ParseStream): SynResult<AngleBracketed> {
                val colon2Token = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
                return doParse(colon2Token, input)
            }

            internal fun parse(input: ParseStream): SynResult<AngleBracketed> {
                val colon2Token =
                    if (input.peek(PathSepPeek)) {
                        PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
                    } else {
                        null
                    }
                return doParse(colon2Token, input)
            }

            internal fun doParse(
                colon2Token: PathSep?,
                input: ParseStream,
            ): SynResult<AngleBracketed> {
                val ltToken = parseGenericLt(input).getOrElse { return SynResult.failure(it) }
                val args = GenericArgumentList()
                while (!input.peek(GenericsGtPeek)) {
                    val value = GenericArgumentParse.parse(input).getOrElse { return SynResult.failure(it) }
                    args.pushValue(value)
                    if (input.peek(GenericsGtPeek)) break
                    val punct = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
                    args.pushPunct(punct)
                }
                val gtToken = GenericsGtParse.parse(input).getOrElse { return SynResult.failure(it) }
                return SynResult.success(AngleBracketed(colon2Token, ltToken, args, gtToken))
            }
        }
    }

    /** The `(A, B) -> C` in `Fn(A, B) -> C`. */
    public data class Parenthesized(
        public var parenToken: Paren,
        public var inputs: SynTypeList,
        public var output: ReturnType,
    ) : PathArguments()

    public fun isEmpty(): Boolean =
        when (this) {
            None -> true
            is AngleBracketed -> args.isEmpty()
            is Parenthesized -> false
        }

    public fun isNone(): Boolean =
        this is None

    override fun toTokens(tokens: TokenStream) {
        printPathArguments(tokens, this, PathStyle.AsWritten)
    }

    public fun deepCopy(): PathArguments =
        when (this) {
            None -> None
            is AngleBracketed -> copy(args = args.copy({ it.deepCopy() }, { it }))
            is Parenthesized -> copy(inputs = inputs.copy({ it.deepCopy() }, { it }), output = output.deepCopy())
        }
}

/** An individual generic argument, like `T`, `T`, or `Item = T`. */
public sealed class GenericArgument : ToTokens {
    public data class LifetimeArg(
        val lifetime: Lifetime,
    ) : GenericArgument()

    public data class TypeArg(
        val type: SynType,
    ) : GenericArgument()

    public data class ConstArg(
        val expr: Expr,
    ) : GenericArgument()

    public data class AssocTypeArg(
        val assoc: AssocType,
    ) : GenericArgument()

    public data class AssocConstArg(
        val assoc: AssocConst,
    ) : GenericArgument()

    public data class ConstraintArg(
        val constraint: Constraint,
    ) : GenericArgument()

    public override fun toTokens(tokens: TokenStream) {
        when (this) {
            is LifetimeArg -> lifetime.toTokens(tokens)
            is TypeArg -> type.toTokens(tokens)
            is ConstArg -> expr.toTokens(tokens)
            is AssocTypeArg -> assoc.toTokens(tokens)
            is AssocConstArg -> assoc.toTokens(tokens)
            is ConstraintArg -> constraint.toTokens(tokens)
        }
    }

    public fun deepCopy(): GenericArgument =
        when (this) {
            is LifetimeArg -> copy(lifetime = lifetime.deepCopy())
            is TypeArg -> copy(type = type.deepCopy())
            is ConstArg -> copy(expr = expr.deepCopy())
            is AssocTypeArg -> copy(assoc = assoc.deepCopy())
            is AssocConstArg -> copy(assoc = assoc.deepCopy())
            is ConstraintArg -> copy(constraint = constraint.deepCopy())
        }
}

/** A binding on an associated type, such as `Item = UByte`. */
public data class AssocType(
    public var ident: Ident,
    public var generics: PathArguments.AngleBracketed?,
    public var eqToken: io.github.kotlinmania.syn.token.Eq,
    public var ty: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ident.toTokens(tokens)
        generics?.toTokens(tokens)
        eqToken.toTokens(tokens)
        ty.toTokens(tokens)
    }

    public fun deepCopy(): AssocType = AssocType(ident.copy(), generics?.deepCopy() as? PathArguments.AngleBracketed?, eqToken, ty.deepCopy())
}

/** An equality constraint on an associated constant. */
public data class AssocConst(
    public var ident: Ident,
    public var generics: PathArguments.AngleBracketed?,
    public var eqToken: io.github.kotlinmania.syn.token.Eq,
    public var value: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ident.toTokens(tokens)
        generics?.toTokens(tokens)
        eqToken.toTokens(tokens)
        value.toTokens(tokens)
    }

    public fun deepCopy(): AssocConst = AssocConst(ident.copy(), generics?.deepCopy() as? PathArguments.AngleBracketed?, eqToken, value.deepCopy())
}

/** An associated type bound such as `Iterator<Item: Display>`. */
public data class Constraint(
    public var ident: Ident,
    public var generics: PathArguments.AngleBracketed?,
    public var colonToken: Colon,
    public var bounds: TypeParamBoundList,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        ident.toTokens(tokens)
        generics?.toTokens(tokens)
        colonToken.toTokens(tokens)
        bounds.toTokens(tokens)
    }

    public fun deepCopy(): Constraint = Constraint(ident.copy(), generics?.deepCopy() as? PathArguments.AngleBracketed?, colonToken, bounds.copy({ it.deepCopy() }, { it }))
}

/** The explicit Self type in a qualified path. */
public data class QSelf(
    public var ltToken: Lt,
    public var ty: SynType,
    public var position: Int,
    public var asToken: io.github.kotlinmania.syn.token.As?,
    public var gtToken: Gt,
) : Spanned {
    override fun span(): Span =
        QSelfDelimiters(this).span()
}

private class QSelfDelimiters(
    private val qself: QSelf,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        qself.ltToken.toTokens(tokens)
        qself.gtToken.toTokens(tokens)
    }
}

/** Style of path parsing. */
public enum class PathStyle {
    Expr,
    Recursion,
    Mod,
    AsWritten,
}

internal fun printPath(
    tokens: TokenStream,
    path: Path,
    style: PathStyle,
) {
    path.leadingColon?.toTokens(tokens)
    for ((segment, punct) in path.segments.pairsList()) {
        printPathSegment(tokens, segment as PathSegment, style)
        punct?.toTokens(tokens)
    }
}

private fun printPathSegment(
    tokens: TokenStream,
    segment: PathSegment,
    style: PathStyle,
) {
    segment.ident.toTokens(tokens)
    printPathArguments(tokens, segment.arguments, style)
}

private fun printPathArguments(
    tokens: TokenStream,
    arguments: PathArguments,
    style: PathStyle,
) {
    when (arguments) {
        PathArguments.None -> Unit
        is PathArguments.AngleBracketed -> printAngleBracketedGenericArguments(tokens, arguments, style)
        is PathArguments.Parenthesized -> printParenthesizedGenericArguments(tokens, arguments, style)
    }
}

internal fun printAngleBracketedGenericArguments(
    tokens: TokenStream,
    arguments: PathArguments.AngleBracketed,
    style: PathStyle,
) {
    if (style == PathStyle.Mod) return

    conditionallyPrintTurbofish(tokens, arguments.colon2Token, style)
    arguments.ltToken.toTokens(tokens)

    var trailingOrEmpty = true
    for ((argument, punct) in arguments.args.pairsList()) {
        if (argument is GenericArgument.LifetimeArg) {
            argument.toTokens(tokens)
            punct?.toTokens(tokens)
            trailingOrEmpty = punct != null
        }
    }
    for ((argument, punct) in arguments.args.pairsList()) {
        if (argument !is GenericArgument.LifetimeArg) {
            if (!trailingOrEmpty) Comma.default().toTokens(tokens)
            (argument as GenericArgument).toTokens(tokens)
            punct?.toTokens(tokens)
            trailingOrEmpty = punct != null
        }
    }

    arguments.gtToken.toTokens(tokens)
}

private fun printParenthesizedGenericArguments(
    tokens: TokenStream,
    arguments: PathArguments.Parenthesized,
    style: PathStyle,
) {
    if (style == PathStyle.Mod) return

    conditionallyPrintTurbofish(tokens, null, style)
    arguments.parenToken.surround(tokens) { inner ->
        arguments.inputs.toTokens(inner)
    }
    arguments.output.toTokens(tokens)
}

internal fun printQpath(
    tokens: TokenStream,
    qself: QSelf?,
    path: Path,
    style: PathStyle,
) {
    if (qself == null) {
        printPath(tokens, path, style)
        return
    }

    qself.ltToken.toTokens(tokens)
    qself.ty.toTokens(tokens)

    val position = minOf(qself.position, path.segments.len())
    val segments = path.segments.pairsList()
    if (position > 0) {
        TokensOrDefault(qself.asToken, io.github.kotlinmania.syn.token.As::default).toTokens(tokens)
        path.leadingColon?.toTokens(tokens)
        for (index in 0 until position) {
            val (segment, punct) = segments[index]
            printPathSegment(tokens, segment as PathSegment, PathStyle.AsWritten)
            if (index + 1 == position) qself.gtToken.toTokens(tokens)
            punct?.toTokens(tokens)
        }
    } else {
        qself.gtToken.toTokens(tokens)
        path.leadingColon?.toTokens(tokens)
    }
    for (index in position until segments.size) {
        val (segment, punct) = segments[index]
        printPathSegment(tokens, segment as PathSegment, style)
        punct?.toTokens(tokens)
    }
}

private fun conditionallyPrintTurbofish(
    tokens: TokenStream,
    colon2Token: PathSep?,
    style: PathStyle,
) {
    when (style) {
        PathStyle.Expr,
        PathStyle.Recursion,
        -> TokensOrDefault(colon2Token, PathSep::default).toTokens(tokens)
        PathStyle.Mod -> error("module-style paths do not print path arguments")
        PathStyle.AsWritten -> colon2Token?.toTokens(tokens)
    }
}

public object PathParse {
    fun parse(input: ParseStream): SynResult<Path> =
        Path.parseHelper(input, exprStyle = false)
}

internal fun parseModStylePath(input: ParseStream): SynResult<Path> =
    Path.parseModStyle(input)

public object PathSegmentParse {
    fun parse(input: ParseStream): SynResult<PathSegment> =
        PathSegment.parseHelper(input, exprStyle = false)
}

internal fun parseAngleBracketedPathArguments(input: ParseStream): SynResult<PathArguments.AngleBracketed> =
    PathArguments.AngleBracketed.parse(input)

public object GenericArgumentParse {
    fun parse(input: ParseStream): SynResult<GenericArgument> {
        if (input.peek(LifetimePeek) && !input.peek2(PlusPeek)) {
            return SynResult.success(GenericArgument.LifetimeArg(LifetimeParse.parse(input).getOrElse { return SynResult.failure(it) }))
        }

        if (input.peek(LitPeek) || input.peek(BracePeek)) {
            return constArgument(input).map(GenericArgument::ConstArg)
        }

        val argument = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        if (argument is SynType.Path &&
            argument.qself == null &&
            argument.path.leadingColon == null &&
            argument.path.segments.len() == 1
        ) {
            val segment = argument.path.segments.first()
            val arguments = segment?.arguments
            if (segment != null && (arguments is PathArguments.None || arguments is PathArguments.AngleBracketed)) {
                val eqToken = EqParse.parse(input).getOrNull()
                if (eqToken != null) {
                    argument.path.segments.pop()
                    val generics = arguments as? PathArguments.AngleBracketed
                    return if (input.peek(LitPeek) || input.peek(BracePeek)) {
                        SynResult.success(
                            GenericArgument.AssocConstArg(
                                AssocConst(
                                    segment.ident,
                                    generics,
                                    eqToken,
                                    constArgument(input).getOrElse { return SynResult.failure(it) },
                                ),
                            ),
                        )
                    } else {
                        SynResult.success(
                            GenericArgument.AssocTypeArg(
                                AssocType(
                                    segment.ident,
                                    generics,
                                    eqToken,
                                    parseTypeFull(input).getOrElse { return SynResult.failure(it) },
                                ),
                            ),
                        )
                    }
                }

                val colonToken = ColonParse.parse(input).getOrNull()
                if (colonToken != null) {
                    argument.path.segments.pop()
                    val generics = arguments as? PathArguments.AngleBracketed
                    val bounds = parseTypeParamBounds(input, stopAtEq = false).getOrElse { return SynResult.failure(it) }
                    return SynResult.success(GenericArgument.ConstraintArg(Constraint(segment.ident, generics, colonToken, bounds)))
                }
            }
        }

        return SynResult.success(GenericArgument.TypeArg(argument))
    }
}

internal fun constArgument(input: ParseStream): SynResult<Expr> {
    val lookahead = input.lookahead1()
    if (input.peek(LitPeek)) {
        val lit = LitParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Expr.Lit(emptyList(), lit))
    }
    if (input.peek(IdentPeek)) {
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Expr.Path(emptyList(), null, Path.from(ident)))
    }
    if (input.peek(BracePeek)) {
        return parseExprFull(input)
    }
    return SynResult.failure(lookahead.error())
}

private object GenericLtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '<'
    }

    override fun display(): String = "`<`"
}

private fun parseGenericLt(input: ParseStream): SynResult<Lt> =
    input.step { cursor ->
        val (punct, rest) = cursor.punct() ?: return@step SynResult.failure(cursor.error("expected `<`"))
        if (punct.asChar() != '<') {
            return@step SynResult.failure(cursor.error("expected `<`"))
        }
        SynResult.success(Lt.from(punct.span()) to rest)
    }

internal fun parseParenthesizedPathArguments(input: ParseStream): SynResult<PathArguments.Parenthesized> {
    val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
    val inputs = SynTypeList()
    while (!parens.content.isEmpty()) {
        val ty = parseTypeFull(parens.content).getOrElse { return SynResult.failure(it) }
        inputs.pushValue(ty)
        if (parens.content.isEmpty()) break
        val comma = CommaParse.parse(parens.content).getOrElse { return SynResult.failure(it) }
        inputs.pushPunct(comma)
    }
    parens.content.finishChildBuffer()
    val output = parseReturnTypeWithoutPlus(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(PathArguments.Parenthesized(parens.token, inputs, output))
}

internal fun qpath(input: ParseStream, exprStyle: Boolean): SynResult<Pair<QSelf?, Path>> {
    if (input.peek(LtPeek)) {
        val ltToken = LtParse.parse(input).getOrElse { return SynResult.failure(it) }
        val thisTy = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        val pathAndAs =
            if (input.peek(AsPeek)) {
                val asToken = AsParse.parse(input).getOrElse { return SynResult.failure(it) }
                val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
                asToken to path
            } else {
                null
            }
        val gtToken = GenericsGtParse.parse(input).getOrElse { return SynResult.failure(it) }
        val colon2Token = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
        val rest = PathSegmentList()
        while (true) {
            val segment = PathSegment.parseHelper(input, exprStyle).getOrElse { return SynResult.failure(it) }
            rest.pushValue(segment)
            if (!input.peek(PathSepPeek)) break
            val punct = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
            rest.pushPunct(punct)
        }
        val (position, asToken, path) =
            if (pathAndAs != null) {
                val (asTokenValue, pathValue) = pathAndAs
                val positionValue = pathValue.segments.len()
                pathValue.segments.pushPunct(colon2Token)
                for ((segment, punct) in rest.pairsIterator()) {
                    pathValue.segments.pushValue(segment as PathSegment)
                    if (punct != null) pathValue.segments.pushPunct(punct)
                }
                Triple(positionValue, asTokenValue, pathValue)
            } else {
                Triple(0, null, Path(colon2Token, rest))
            }
        val qself = QSelf(ltToken, thisTy, position, asToken, gtToken)
        return SynResult.success(qself to path)
    }

    val path = Path.parseHelper(input, exprStyle).getOrElse { return SynResult.failure(it) }
    return SynResult.success(null to path)
}

public object PathPeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.ident() != null

    override fun display(): String = "path"
}

public object PathSepPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == ':' && punct.spacing() == Spacing.Joint
    }

    override fun display(): String = "`::`"
}

public object PathSepParse {
    fun parse(input: ParseStream): SynResult<PathSep> =
        input.step { cursor ->
            val (punct, rest) = cursor.punct() ?: return@step SynResult.failure(cursor.error("expected `::`"))
            if (punct.asChar() != ':' || punct.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `::`"))
            }
            val second = rest.punct()
            if (second == null || second.first.asChar() != ':') {
                return@step SynResult.failure(cursor.error("expected `::`"))
            }
            val span = punct.span()
            SynResult.success(PathSep.from(span) to second.second)
        }
}

public object CommaPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == ','
    }

    override fun display(): String = "`,`"
}

/** Parser for a comma token. */
public object CommaParse {
    fun parse(input: ParseStream): SynResult<Comma> =
        input.step { cursor ->
            val (punct, rest) = cursor.punct() ?: return@step SynResult.failure(cursor.error("expected `,`"))
            if (punct.asChar() != ',') {
                return@step SynResult.failure(cursor.error("expected `,`"))
            }
            SynResult.success(Comma.from(punct.span()) to rest)
        }
}
