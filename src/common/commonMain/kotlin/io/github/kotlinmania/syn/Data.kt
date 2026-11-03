// port-lint: source data.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
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
    /**
     * Explicit discriminant: `Variant = 1`
     */
    public var discriminant: EqExpr?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        ident.toTokens(tokens)
        fields.toTokens(tokens)
        discriminant?.toTokens(tokens)
    }
}

/**
 * Data stored within an enum variant or struct.
 *
 * # Syntax tree enum
 *
 * This type is a syntax tree enum; see [Expr].
 */
public sealed class Fields :
    Iterable<Field>,
    ToTokens {
    public typealias Item = Field
    public typealias IntoIter = Iterator<Field>

    /**
     * Named fields of a struct or struct variant such as `Point { x: f64,
     * y: f64 }`.
     */
    public data class Named(
        var fields: FieldsNamed,
    ) : Fields()

    /**
     * Unnamed fields of a tuple data structure or tuple variant such as `Some(T)`.
     */
    public data class Unnamed(
        var fields: FieldsUnnamed,
    ) : Fields()

    /**
     * Unit data structure or unit variant such as `None`.
     */
    public data object Unit : Fields()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Named -> fields.toTokens(tokens)
            is Unnamed -> fields.toTokens(tokens)
            Unit -> kotlin.Unit
        }
    }

    override fun iterator(): Iterator<Field> =
        iter()

    /**
     * Get an iterator over the [Field] items in this object.
     */
    public fun iter(): Iterator<Field> =
        when (this) {
            Unit -> emptyList<Field>().iterator()
            is Named -> fields.named.toList().iterator()
            is Unnamed -> fields.unnamed.toList().iterator()
        }

    /**
     * Get an iterator over the [Field] items in this object.
     */
    public fun iterMut(): Iterator<Field> =
        iter()

    public fun intoIter(): Iterator<Field> =
        iter()

    /**
     * Returns the number of fields.
     */
    public fun len(): Int =
        when (this) {
            Unit -> 0
            is Named -> fields.named.len()
            is Unnamed -> fields.unnamed.len()
        }

    /**
     * Returns `true` if there are zero fields.
     */
    public fun isEmpty(): Boolean =
        len() == 0

    /**
     * Get an iterator over the fields of a data structure or variant as [Member]s.
     *
     * Named fields yield [Member.Named] and unnamed fields yield [Member.Unnamed]
     * with their positional index.
     */
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
        val field = fields[position]
        position += 1
        val member =
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
        val braces = braced(input).getOrElse { return SynResult.failure(it) }
        val named = parseNamedFieldList(braces.content).getOrElse { return SynResult.failure(it) }
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
        val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
        val unnamed = parseUnnamedFieldList(parens.content).getOrElse { return SynResult.failure(it) }
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
                if (unnamedField && (input.peek(StructPeek) || (input.peek(UnionPeek) && input.peek2(BracePeek)))) {
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
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        val fields =
            when {
                input.peek(BracePeek) -> Fields.Named(FieldsNamedParse.parse(input).getOrElse { return SynResult.failure(it) })
                input.peek(ParenPeek) -> Fields.Unnamed(FieldsUnnamedParse.parse(input).getOrElse { return SynResult.failure(it) })
                else -> Fields.Unit
            }
        val discriminant =
            if (input.peek(EqPeek)) {
                val eq = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                val begin = input.fork()
                val ahead = input.fork()
                var parsedExpr = parseExprFull(ahead)
                if (parsedExpr.isSuccess && (ahead.isEmpty() || ahead.peek(CommaPeek))) {
                    input.advanceTo(ahead)
                } else if (scanExpr(input).isSuccess) {
                    parsedExpr = SynResult.success(Expr.Verbatim(between(begin, input)))
                }
                val expr = parsedExpr.getOrElse { return SynResult.failure(it) }
                EqExpr(eq, expr)
            } else {
                null
            }
        return SynResult.success(Variant(attrs, ident, fields, discriminant))
    }
}

internal fun parseNamedFieldList(input: ParseStream): SynResult<FieldList> {
    val fields = FieldList()
    while (!input.isEmpty()) {
        val field = parseNamedField(input).getOrElse { return SynResult.failure(it) }
        fields.pushValue(field)
        if (input.isEmpty()) break
        val comma = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        fields.pushPunct(comma)
    }
    return SynResult.success(fields)
}

internal fun parseUnnamedFieldList(input: ParseStream): SynResult<FieldList> {
    val fields = FieldList()
    while (!input.isEmpty()) {
        val field = parseUnnamedField(input).getOrElse { return SynResult.failure(it) }
        fields.pushValue(field)
        if (input.isEmpty()) break
        val comma = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        fields.pushPunct(comma)
    }
    return SynResult.success(fields)
}

internal fun parseVariantList(input: ParseStream): SynResult<VariantList> {
    val variants = VariantList()
    while (!input.isEmpty()) {
        val variant = parseVariant(input).getOrElse { return SynResult.failure(it) }
        variants.pushValue(variant)
        if (input.isEmpty()) break
        val comma = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        variants.pushPunct(comma)
    }
    return SynResult.success(variants)
}

internal fun parseNamedField(input: ParseStream): SynResult<Field> = Field.parseNamed(input)

internal fun parseVariant(input: ParseStream): SynResult<Variant> = VariantParse.parse(input)

internal fun parseUnnamedField(input: ParseStream): SynResult<Field> = Field.parseUnnamed(input)
