// port-lint: source data.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Paren

/**
 * An enum variant.
 */
public data class Variant(
    public var attrs: List<Attribute>,
    public var ident: Ident,
    public var fields: Fields,
    public var discriminant: EqExpr?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        ident.toTokens(tokens)
        fields.toTokens(tokens)
        discriminant?.toTokens(tokens)
    }
}

/** Data stored within an enum variant or data structure. */
public sealed class Fields :
    Iterable<Field>,
    ToTokens {
    public typealias Item = Field
    public typealias IntoIter = Iterator<Field>

    public data class Named(
        var fields: FieldsNamed,
    ) : Fields() {
        override fun toTokens(tokens: TokenStream) {
            fields.toTokens(tokens)
        }
    }

    public data class Unnamed(
        var fields: FieldsUnnamed,
    ) : Fields() {
        override fun toTokens(tokens: TokenStream) {
            fields.toTokens(tokens)
        }
    }

    public data object Unit : Fields() {
        override fun toTokens(tokens: TokenStream) {
        }
    }

    override fun iterator(): Iterator<Field> =
        iter()

    public fun iter(): Iterator<Field> =
        when (this) {
            Unit -> emptyList<Field>().iterator()
            is Named -> fields.named.toList().iterator()
            is Unnamed -> fields.unnamed.toList().iterator()
        }

    public fun iterMut(): Iterator<Field> =
        iter()

    public fun intoIter(): Iterator<Field> =
        iter()

    public fun len(): Int =
        when (this) {
            Unit -> 0
            is Named -> fields.named.len()
            is Unnamed -> fields.unnamed.len()
        }

    public fun isEmpty(): Boolean =
        len() == 0

    public fun members(): Members =
        Members(iter().asSequence().toList())
}

/** Iterator over the fields of a data structure as members. */
public class Members internal constructor(
    private val fields: List<Field>,
    private var position: Int = 0,
    private var index: UInt = 0u,
) : Iterator<Member> {
    override fun hasNext(): Boolean =
        position < fields.size

    override fun next(): Member {
        if (!hasNext()) throw NoSuchElementException()
        var field = fields[position]
        position += 1
        var member =
            field.ident?.let(Member::Named)
                ?: Member.Unnamed(Index(index, field.tySpan()))
        index += 1u
        return member
    }

    public fun clone(): Members =
        Members(fields, position, index)
}

/** Named fields of a data structure such as `Point { x: f64, y: f64 }`. */
public data class FieldsNamed(
    public var braceToken: Brace,
    public var named: FieldList,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        braceToken.surround(tokens) { inner ->
            named.toTokens(inner)
        }
    }
}

public object FieldsNamedParse {
    fun parse(input: ParseStream): SynResult<FieldsNamed> {
        var braces = braced(input).getOrElse { return SynResult.failure(it) }
        var named = parseNamedFieldList(braces.content).getOrElse { return SynResult.failure(it) }
        braces.content.finishChildBuffer()
        return SynResult.success(FieldsNamed(braces.token, named))
    }
}

/** Unnamed fields of a tuple-style data structure such as `Some(T)`. */
public data class FieldsUnnamed(
    public var parenToken: Paren,
    public var unnamed: FieldList,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        parenToken.surround(tokens) { inner ->
            unnamed.toTokens(inner)
        }
    }
}

public object FieldsUnnamedParse {
    fun parse(input: ParseStream): SynResult<FieldsUnnamed> {
        var parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
        var unnamed = parseUnnamedFieldList(parens.content).getOrElse { return SynResult.failure(it) }
        parens.content.finishChildBuffer()
        return SynResult.success(FieldsUnnamed(parens.token, unnamed))
    }
}

/** A field of a data class or enum variant. */
public data class Field(
    public var attrs: List<Attribute>,
    public var vis: Visibility,
    public var mutability: FieldMutability,
    public var ident: Ident?,
    public var colonToken: Colon?,
    public var ty: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        vis.toTokens(tokens)
        mutability.toTokens(tokens)
        ident?.toTokens(tokens)
        colonToken?.toTokens(tokens)
        ty.toTokens(tokens)
    }

    public companion object {
        /** Parses a named field. */
        public fun parseNamed(input: ParseStream): SynResult<Field> {
            val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
            val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }

            val unnamedField = input.peek(UnderscorePeek)
            val ident =
                if (unnamedField) {
                    identFromUnderscore(UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) })
                } else {
                    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                }

            val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty =
                if (unnamedField && (input.peek(StructPeek) || input.peek(UnionPeek) && input.peek2(BracePeek))) {
                    val begin = input.fork()
                    if (input.peek(StructPeek)) {
                        StructParse.parse(input).getOrElse { return SynResult.failure(it) }
                    } else {
                        UnionParse.parse(input).getOrElse { return SynResult.failure(it) }
                    }
                    FieldsNamedParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynType.Verbatim(between(begin, input))
                } else {
                    parseTypeFull(input).getOrElse { return SynResult.failure(it) }
                }

            return SynResult.success(Field(attrs, vis, FieldMutability.None, ident, colonToken, ty))
        }

        /** Parses an unnamed field. */
        public fun parseUnnamed(input: ParseStream): SynResult<Field> {
            val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
            val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(Field(attrs, vis, FieldMutability.None, null, null, ty))
        }
    }
}

private fun Field.tySpan(): io.github.kotlinmania.procmacro2.Span =
    ty.span()

public object VariantParse {
    fun parse(input: ParseStream): SynResult<Variant> {
        var attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
        var ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        var fields =
            when {
                input.peek(BracePeek) -> Fields.Named(FieldsNamedParse.parse(input).getOrElse { return SynResult.failure(it) })
                input.peek(ParenPeek) -> Fields.Unnamed(FieldsUnnamedParse.parse(input).getOrElse { return SynResult.failure(it) })
                else -> Fields.Unit
            }
        var discriminant =
            if (input.peek(EqPeek)) {
                val eq = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
                EqExpr(eq, expr)
            } else {
                null
            }
        return SynResult.success(Variant(attrs, ident, fields, discriminant))
    }
}
