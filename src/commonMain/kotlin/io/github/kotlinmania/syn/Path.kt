// port-lint: source path.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.PathSep
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import kotlin.native.HiddenFromObjC

/** A path at which a named item is exported. */
public class Path(
    public var leadingColon: PathSep?,
    public val segments: Punctuated<PathSegment, PathSep>,
) {
    public companion object {
        public fun from(segment: Ident): Path {
            val path = Path(null, Punctuated.new())
            path.segments.pushValue(PathSegment.from(segment))
            return path
        }

        public fun from(segment: PathSegment): Path {
            val path = Path(null, Punctuated.new())
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
        val segment = segments[0]
        return if (segment.arguments.isNone()) segment.ident else null
    }

    public fun requireIdent(): Ident =
        getIdent() ?: throw SynError.new(
            segments.first()?.ident?.span() ?: io.github.kotlinmania.procmacro2.Span.callSite(),
            "expected this path to be an identifier",
        )

    public fun span(): Span {
        val first = segments.first()?.ident?.span() ?: Span.callSite()
        val last = segments.last()?.ident?.span() ?: first
        return first.join(last) ?: first
    }

    public fun toTokens(tokens: TokenStream) {
        leadingColon?.toTokens(tokens)
        var first = true
        for ((segment, punct) in segments.pairs()) {
            if (!first) {
                // PathSep already written via punct
            }
            segment.toTokens(tokens)
            punct?.toTokens(tokens)
            first = false
        }
    }

    override fun toString(): String =
        segments.toList().joinToString("::") { it.ident.toString() }.let {
            if (leadingColon != null) "::$it" else it
        }

    override fun equals(other: Any?): Boolean =
        other is Path && leadingColon == other.leadingColon && segments == other.segments

    override fun hashCode(): Int =
        31 * (leadingColon?.hashCode() ?: 0) + segments.hashCode()

    public fun copy(): Path =
        Path(
            leadingColon = leadingColon,
            segments = segments.copy({ it.deepCopy() }, { it }),
        )
}

/** A segment of a path together with any path arguments on that segment. */
public data class PathSegment(
    public var ident: Ident,
    public var arguments: PathArguments = PathArguments.None,
) {
    public companion object {
        public fun from(ident: Ident): PathSegment =
            PathSegment(ident, PathArguments.None)
    }

    public fun deepCopy(): PathSegment =
        PathSegment(ident.copy(), arguments.copy())

    public fun toTokens(tokens: TokenStream) {
        ident.toTokens(tokens)
        arguments.toTokens(tokens)
    }

    override fun toString(): String =
        if (arguments.isNone()) ident.toString() else "$ident$arguments"
}

/** Angle bracketed or parenthesized arguments of a path segment. */
public sealed class PathArguments {
    public data object None : PathArguments()

    /** Generic arguments surrounded by angle brackets. */
    public data class AngleBracketed(
        public var colon2Token: PathSep?,
        public val ltToken: Lt,
        public val args: Punctuated<GenericArgument, Comma>,
        public val gtToken: Gt,
    ) : PathArguments()

    /** The `(A, B) -> C` in `Fn(A, B) -> C`. */
    public data class Parenthesized(
        public val parenToken: Paren,
        public val inputs: Punctuated<SynType, Comma>,
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

    public fun toTokens(tokens: TokenStream) {
        when (this) {
            None -> { }
            is AngleBracketed -> {
                colon2Token?.toTokens(tokens)
                ltToken.toTokens(tokens)
                for ((arg, comma) in args.pairs()) {
                    arg.toTokens(tokens)
                    comma?.toTokens(tokens)
                }
                gtToken.toTokens(tokens)
            }
            is Parenthesized -> {
                parenToken.toTokens(tokens)
                for ((input, comma) in inputs.pairs()) {
                    input.toTokens(tokens)
                    comma?.toTokens(tokens)
                }
                output.toTokens(tokens)
            }
        }
    }

    public fun copy(): PathArguments =
        when (this) {
            None -> None
            is AngleBracketed -> copy(args = args.copy({ it.copy() }, { it }))
            is Parenthesized -> copy(inputs = inputs.copy({ it.copy() }, { it }), output = output.copy())
        }
}

/** An individual generic argument, like `'a`, `T`, or `Item = T`. */
public sealed class GenericArgument {
    public data class LifetimeArg(val lifetime: Lifetime) : GenericArgument()
    public data class TypeArg(val type: SynType) : GenericArgument()
    public data class ConstArg(val expr: Expr) : GenericArgument()
    public data class AssocTypeArg(val assoc: AssocType) : GenericArgument()
    public data class AssocConstArg(val assoc: AssocConst) : GenericArgument()
    public data class ConstraintArg(val constraint: Constraint) : GenericArgument()

    public fun toTokens(tokens: TokenStream) {
        when (this) {
            is LifetimeArg -> lifetime.toTokens(tokens)
            is TypeArg -> type.toTokens(tokens)
            is ConstArg -> expr.toTokens(tokens)
            is AssocTypeArg -> assoc.toTokens(tokens)
            is AssocConstArg -> assoc.toTokens(tokens)
            is ConstraintArg -> constraint.toTokens(tokens)
        }
    }

    public fun copy(): GenericArgument =
        when (this) {
            is LifetimeArg -> copy(lifetime = lifetime.deepCopy())
            is TypeArg -> copy(type = type.copy())
            is ConstArg -> copy(expr = expr.copy())
            is AssocTypeArg -> copy(assoc = assoc.copy())
            is AssocConstArg -> copy(assoc = assoc.copy())
            is ConstraintArg -> copy(constraint = constraint.copy())
        }
}

/** A binding on an associated type, such as `Item = u8`. */
public data class AssocType(
    public val ident: Ident,
    public val generics: PathArguments.AngleBracketed?,
    public val eqToken: io.github.kotlinmania.syn.token.Eq,
    public val ty: SynType,
)

/** An equality constraint on an associated constant. */
public data class AssocConst(
    public val ident: Ident,
    public val generics: PathArguments.AngleBracketed?,
    public val eqToken: io.github.kotlinmania.syn.token.Eq,
    public val value: Expr,
)

/** An associated type bound such as `Iterator<Item: Display>`. */
public data class Constraint(
    public val ident: Ident,
    public val generics: PathArguments.AngleBracketed?,
    public val colonToken: Colon,
    public val bounds: Punctuated<TypeParamBound, Plus>,
)

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

@HiddenFromObjC
public object PathParse : Parse<Path> {
    override fun parse(input: ParseStream): SynResult<Path> {
        val leadingColon: PathSep? = if (input.peek(PathSepPeek)) {
            input.parse<PathSep>().getOrNull()
        } else {
            null
        }
        val segments = Punctuated.new<PathSegment, PathSep>()
        val firstSegment = input.parse<PathSegment>().getOrElse { return SynResult.failure(it) }
        segments.pushValue(firstSegment)
        while (input.peek(PathSepPeek)) {
            input.parse<PathSep>().getOrElse { return SynResult.failure(it) }.also { segments.pushPunct(it) }
            val segment = input.parse<PathSegment>().getOrElse { return SynResult.failure(it) }
            segments.pushValue(segment)
        }
        return SynResult.success(Path(leadingColon, segments))
    }
}

@HiddenFromObjC
public object PathSegmentParse : Parse<PathSegment> {
    override fun parse(input: ParseStream): SynResult<PathSegment> {
        val ident = input.parse<Ident>().getOrElse { return SynResult.failure(it) }
        val arguments = PathArguments.None
        return SynResult.success(PathSegment(ident, arguments))
    }
}

@HiddenFromObjC
public object PathPeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.ident() != null

    override fun display(): String = "path"
}

@HiddenFromObjC
public object PathSepPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.char == ':' && punct.spacing == io.github.kotlinmania.procmacro2.Spacing.Joint
    }

    override fun display(): String = "`::`"
}

@HiddenFromObjC
public object CommaPeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.punct()?.first?.char == ','

    override fun display(): String = "`,`"
}
