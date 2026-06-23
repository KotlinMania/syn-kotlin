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
    public val segments: PathSegmentList,
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

    public fun toTokens(tokens: TokenStream) {
        leadingColon?.toTokens(tokens)
        segments.toTokens(tokens)
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
}

/** A segment of a path together with any path arguments on that segment. */
public data class PathSegment(
    public var ident: Ident,
    public var arguments: PathArguments = PathArguments.None,
) : ToTokens {
    public companion object {
        public fun from(ident: Ident): PathSegment =
            PathSegment(ident, PathArguments.None)
    }

    public fun deepCopy(): PathSegment =
        PathSegment(ident.copy(), arguments.deepCopy())

    override fun toTokens(tokens: TokenStream) {
        ident.toTokens(tokens)
        arguments.toTokens(tokens)
    }

    override fun toString(): String =
        if (arguments.isNone()) ident.toString() else "$ident$arguments"
}

/** Angle bracketed or parenthesized arguments of a path segment. */
public sealed class PathArguments : ToTokens {
    public data object None : PathArguments() {
        override fun toTokens(tokens: TokenStream) {
            // no arguments to emit
        }
    }

    /** Generic arguments surrounded by angle brackets. */
    public data class AngleBracketed(
        public var colon2Token: PathSep?,
        public val ltToken: Lt,
        public val args: GenericArgumentList,
        public val gtToken: Gt,
    ) : PathArguments()

    /** The `(A, B) -> C` in `Fn(A, B) -> C`. */
    public data class Parenthesized(
        public val parenToken: Paren,
        public val inputs: SynTypeList,
        public val output: ReturnType,
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
        when (this) {
            None -> { }
            is AngleBracketed -> {
                colon2Token?.toTokens(tokens)
                ltToken.toTokens(tokens)
                args.toTokens(tokens)
                gtToken.toTokens(tokens)
            }
            is Parenthesized -> {
                parenToken.surround(tokens) { inner ->
                    inputs.toTokens(inner)
                }
                output.toTokens(tokens)
            }
        }
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
    public val ident: Ident,
    public val generics: PathArguments.AngleBracketed?,
    public val eqToken: io.github.kotlinmania.syn.token.Eq,
    public val ty: SynType,
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
    public val ident: Ident,
    public val generics: PathArguments.AngleBracketed?,
    public val eqToken: io.github.kotlinmania.syn.token.Eq,
    public val value: Expr,
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
    public val ident: Ident,
    public val generics: PathArguments.AngleBracketed?,
    public val colonToken: Colon,
    public val bounds: TypeParamBoundList,
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
    public val ltToken: Lt,
    public val ty: SynType,
    public val position: Int,
    public val asToken: io.github.kotlinmania.syn.token.As?,
    public val gtToken: Gt,
)

/** Style of path parsing. */
public enum class PathStyle {
    Recursion,
    Mod,
}

public object PathParse : Parse<Path> {
    override fun parse(input: ParseStream): SynResult<Path> {
        val leadingColon: PathSep? =
            if (input.peek(PathSepPeek)) {
                input.parse(PathSepParse).getOrNull()
            } else {
                null
            }
        val segments = PathSegmentList()
        val firstSegment = input.parse(PathSegmentParse).getOrElse { return SynResult.failure(it) }
        segments.pushValue(firstSegment)
        while (input.peek(PathSepPeek)) {
            input.parse(PathSepParse).getOrElse { return SynResult.failure(it) }.also { segments.pushPunct(it) }
            val segment = input.parse(PathSegmentParse).getOrElse { return SynResult.failure(it) }
            segments.pushValue(segment)
        }
        return SynResult.success(Path(leadingColon, segments))
    }
}

internal fun parseModStylePath(input: ParseStream): SynResult<Path> {
    val leadingColon: PathSep? =
        if (input.peek(PathSepPeek)) {
            input.parse(PathSepParse).getOrNull()
        } else {
            null
        }
    val segments = PathSegmentList()
    val firstSegment = parseModStylePathSegment(input).getOrElse { return SynResult.failure(it) }
    segments.pushValue(firstSegment)
    while (input.peek(PathSepPeek)) {
        input.parse(PathSepParse).getOrElse { return SynResult.failure(it) }.also { segments.pushPunct(it) }
        val segment = parseModStylePathSegment(input).getOrElse { return SynResult.failure(it) }
        segments.pushValue(segment)
    }
    return SynResult.success(Path(leadingColon, segments))
}

private fun parseModStylePathSegment(input: ParseStream): SynResult<PathSegment> {
    val ident =
        when {
            input.peek(SelfValuePeek) -> identFromSelfValue(input.parse(SelfValueParse).getOrThrow())
            input.peek(SelfTypePeek) -> identFromSelfType(input.parse(SelfTypeParse).getOrThrow())
            input.peek(SuperPeek) -> identFromSuper(input.parse(SuperParse).getOrThrow())
            input.peek(CratePeek) -> identFromCrate(input.parse(CrateParse).getOrThrow())
            else -> input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(PathSegment(ident, PathArguments.None))
}

public object PathSegmentParse : Parse<PathSegment> {
    override fun parse(input: ParseStream): SynResult<PathSegment> {
        val ident =
            when {
                input.peek(SelfValuePeek) -> identFromSelfValue(input.parse(SelfValueParse).getOrThrow())
                input.peek(SelfTypePeek) -> identFromSelfType(input.parse(SelfTypeParse).getOrThrow())
                input.peek(SuperPeek) -> identFromSuper(input.parse(SuperParse).getOrThrow())
                input.peek(CratePeek) -> identFromCrate(input.parse(CrateParse).getOrThrow())
                else -> input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
            }
        val ahead = input.fork()
        val parsed = parseAngleBracketedPathArguments(ahead)
        val arguments =
            if (parsed.isSuccess) {
                input.advanceTo(ahead)
                parsed.getOrThrow()
            } else {
                PathArguments.None
            }
        return SynResult.success(PathSegment(ident, arguments))
    }
}

internal fun parseAngleBracketedPathArguments(input: ParseStream): SynResult<PathArguments.AngleBracketed> {
    val colon2Token =
        if (input.peek(PathSepPeek)) {
            if (!input.peek3(LtPeek)) {
                return SynResult.failure(input.error("expected `<`"))
            }
            input.parse(PathSepParse).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    val ltToken = parseGenericLt(input).getOrElse { return SynResult.failure(it) }
    val args = GenericArgumentList()
    while (!input.isEmpty() && !input.peek(GtPeek)) {
        val arg =
            if (input.peek(LifetimePeek)) {
                GenericArgument.LifetimeArg(input.parse(LifetimeParse).getOrElse { return SynResult.failure(it) })
            } else {
                GenericArgument.TypeArg(parseTypeFull(input).getOrElse { return SynResult.failure(it) })
            }
        args.pushValue(arg)
        if (input.peek(GtPeek)) break
        val comma = input.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        args.pushPunct(comma)
    }
    val gtToken = input.parse(GtParse).getOrElse { return SynResult.failure(it) }
    return SynResult.success(PathArguments.AngleBracketed(colon2Token, ltToken, args, gtToken))
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
        val comma = parens.content.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        inputs.pushPunct(comma)
    }
    parens.content.finishChildBuffer()
    val output =
        if (input.peek(RArrowPeek)) {
            val arrow = input.parse(RArrowParse).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            ReturnType.TypeReturn(arrow, ty)
        } else {
            ReturnType.Default
        }
    return SynResult.success(PathArguments.Parenthesized(parens.token, inputs, output))
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

public object PathSepParse : Parse<PathSep> {
    override fun parse(input: ParseStream): SynResult<PathSep> =
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
public object CommaParse : Parse<Comma> {
    override fun parse(input: ParseStream): SynResult<Comma> =
        input.step { cursor ->
            val (punct, rest) = cursor.punct() ?: return@step SynResult.failure(cursor.error("expected `,`"))
            if (punct.asChar() != ',') {
                return@step SynResult.failure(cursor.error("expected `,`"))
            }
            SynResult.success(Comma.from(punct.span()) to rest)
        }
}
