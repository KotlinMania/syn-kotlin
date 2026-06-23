// port-lint: source derive.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Semi

/** Data structure supplied to a derive macro. */
public data class DeriveInput(
    public val attrs: List<Attribute>,
    public val vis: Visibility,
    public val ident: Ident,
    public val generics: Generics,
    public val data: Data,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        vis.toTokens(tokens)
        when (val data = data) {
            is Data.Struct -> data.value.structToken.toTokens(tokens)
            is Data.Enum -> data.value.enumToken.toTokens(tokens)
            is Data.Union -> data.value.unionToken.toTokens(tokens)
        }
        ident.toTokens(tokens)
        generics.withoutWhereClause().toTokens(tokens)
        when (val data = data) {
            is Data.Struct ->
                when (val fields = data.value.fields) {
                    is Fields.Named -> {
                        generics.whereClause?.toTokens(tokens)
                        fields.toTokens(tokens)
                    }
                    is Fields.Unnamed -> {
                        fields.toTokens(tokens)
                        generics.whereClause?.toTokens(tokens)
                        TokensOrDefault(data.value.semiToken, Semi::default).toTokens(tokens)
                    }
                    Fields.Unit -> {
                        generics.whereClause?.toTokens(tokens)
                        TokensOrDefault(data.value.semiToken, Semi::default).toTokens(tokens)
                    }
                }
            is Data.Enum -> {
                generics.whereClause?.toTokens(tokens)
                data.value.braceToken.surround(tokens) { inner ->
                    data.value.variants.toTokens(inner)
                }
            }
            is Data.Union -> {
                generics.whereClause?.toTokens(tokens)
                data.value.fields.toTokens(tokens)
            }
        }
    }
}

private fun Generics.withoutWhereClause(): Generics =
    copy().also { it.whereClause = null }

/** The storage of an enum-like, data-class-like, or union data structure. */
public sealed class Data : ToTokens {
    public data class Struct(
        val value: DataStruct,
    ) : Data() {
        public val fields: Fields get() = value.fields

        override fun toTokens(tokens: TokenStream) {
            value.toTokens(tokens)
        }
    }

    public data class Enum(
        val value: DataEnum,
    ) : Data() {
        public val variants: VariantList get() = value.variants

        override fun toTokens(tokens: TokenStream) {
            value.toTokens(tokens)
        }
    }

    public data class Union(
        val value: DataUnion,
    ) : Data() {
        public val fields: FieldsNamed get() = value.fields

        override fun toTokens(tokens: TokenStream) {
            value.toTokens(tokens)
        }
    }
}

/** A data-class-like input to a derive macro. */
public data class DataStruct(
    public val structToken: io.github.kotlinmania.syn.token.Struct,
    public val fields: Fields,
    public val semiToken: Semi?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        structToken.toTokens(tokens)
        fields.toTokens(tokens)
        semiToken?.toTokens(tokens)
    }
}

/** An enum input to a derive macro. */
public data class DataEnum(
    public val enumToken: io.github.kotlinmania.syn.token.Enum,
    public val braceToken: Brace,
    public val variants: VariantList,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        enumToken.toTokens(tokens)
        braceToken.surround(tokens) { inner ->
            variants.toTokens(inner)
        }
    }
}

/** An untagged union input to a derive macro. */
public data class DataUnion(
    public val unionToken: io.github.kotlinmania.syn.token.Union,
    public val fields: FieldsNamed,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        unionToken.toTokens(tokens)
        fields.toTokens(tokens)
    }
}

public fun parseDeriveInput(input: ParseStream): SynResult<DeriveInput> =
    input.parse(DeriveInputParse)

public object DeriveInputParse : Parse<DeriveInput> {
    override fun parse(input: ParseStream): SynResult<DeriveInput> =
        DeriveInputParseImpl.parse(input)
}

internal object DeriveInputParseImpl : Parse<DeriveInput> {
    override fun parse(input: ParseStream): SynResult<DeriveInput> {
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        val vis = input.parse(VisibilityParse).getOrNull() ?: Visibility.Inherited
        if (input.peek(StructPeek)) {
            val structToken = input.parse(StructParse).getOrElse { return SynResult.failure(it) }
            val ident = input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val data = dataStruct(input).getOrElse { return SynResult.failure(it) }
            generics.whereClause = data.whereClause
            return SynResult.success(
                DeriveInput(
                    attrs,
                    vis,
                    ident,
                    generics,
                    Data.Struct(DataStruct(structToken, data.fields, data.semiToken)),
                ),
            )
        }
        if (input.peek(EnumPeek)) {
            val enumToken = input.parse(EnumParse).getOrElse { return SynResult.failure(it) }
            val ident = input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val data = dataEnum(input).getOrElse { return SynResult.failure(it) }
            generics.whereClause = data.whereClause
            return SynResult.success(
                DeriveInput(
                    attrs,
                    vis,
                    ident,
                    generics,
                    Data.Enum(DataEnum(enumToken, data.braceToken, data.variants)),
                ),
            )
        }
        if (input.peek(UnionPeek)) {
            val unionToken = input.parse(UnionParse).getOrElse { return SynResult.failure(it) }
            val ident = input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val data = dataUnion(input).getOrElse { return SynResult.failure(it) }
            generics.whereClause = data.whereClause
            return SynResult.success(
                DeriveInput(
                    attrs,
                    vis,
                    ident,
                    generics,
                    Data.Union(DataUnion(unionToken, data.fields)),
                ),
            )
        }
        return SynResult.failure(input.error("expected struct, enum, or union"))
    }
}

internal data class DataStructParts(
    val whereClause: WhereClause?,
    val fields: Fields,
    val semiToken: io.github.kotlinmania.syn.token.Semi?,
)

internal data class DataEnumParts(
    val whereClause: WhereClause?,
    val braceToken: io.github.kotlinmania.syn.token.Brace,
    val variants: VariantList,
)

internal data class DataUnionParts(
    val whereClause: WhereClause?,
    val fields: FieldsNamed,
)

internal fun dataStruct(input: ParseStream): SynResult<DataStructParts> {
    var whereClause = parseWhereClause(input).getOrNull()

    if (whereClause == null && input.peek(ParenPeek)) {
        val fields = input.parse(FieldsUnnamedParse).getOrElse { return SynResult.failure(it) }
        whereClause = parseWhereClause(input).getOrNull()
        val semi = input.parse(SemiParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(DataStructParts(whereClause, Fields.Unnamed(fields), semi))
    }

    if (input.peek(BracePeek)) {
        val fields = input.parse(FieldsNamedParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(DataStructParts(whereClause, Fields.Named(fields), null))
    }

    if (input.peek(SemiPeek)) {
        val semi = input.parse(SemiParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(DataStructParts(whereClause, Fields.Unit, semi))
    }

    return SynResult.failure(input.error("expected struct fields"))
}

internal fun dataEnum(input: ParseStream): SynResult<DataEnumParts> {
    val whereClause = parseWhereClause(input).getOrNull()
    val braces = braced(input).getOrElse { return SynResult.failure(it) }
    val variants = parseVariantList(braces.content).getOrElse { return SynResult.failure(it) }
    braces.content.finishChildBuffer()
    return SynResult.success(DataEnumParts(whereClause, braces.token, variants))
}

internal fun dataUnion(input: ParseStream): SynResult<DataUnionParts> {
    val whereClause = parseWhereClause(input).getOrNull()
    val fields = input.parse(FieldsNamedParse).getOrElse { return SynResult.failure(it) }
    return SynResult.success(DataUnionParts(whereClause, fields))
}
