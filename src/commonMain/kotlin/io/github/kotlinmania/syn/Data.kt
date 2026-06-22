// port-lint: source data.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Semi

/**
 * An enum variant.
 *
 * Hidden from the Objective-C / Swift Export bridge: the `discriminant`
 * field of type `kotlin.Pair<Eq, Expr>?` is bridged with type-parameters
 * erased to `Pair<Any?, Any?>?`, and the auto-generated `Syn.kt` then
 * fails to re-pass the value through the typed call site. Same shape as
 * the [Punctuated] erasure.
 */
public data class Variant(
    public val attrs: List<Attribute>,
    public val ident: Ident,
    public val fields: Fields,
    public val discriminant: Pair<Eq, Expr>?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        ident.toTokens(tokens)
        fields.toTokens(tokens)
        discriminant?.let { (eq, expr) ->
            eq.toTokens(tokens)
            expr.toTokens(tokens)
        }
    }
}

/** Data stored within an enum variant or data structure. */
public sealed class Fields :
    Iterable<Field>,
    ToTokens {
    public data class Named(
        val fields: FieldsNamed,
    ) : Fields() {
        override fun toTokens(tokens: TokenStream) {
            fields.toTokens(tokens)
        }
    }

    public data class Unnamed(
        val fields: FieldsUnnamed,
    ) : Fields() {
        override fun toTokens(tokens: TokenStream) {
            fields.toTokens(tokens)
        }
    }

    public data object Unit : Fields() {
        override fun toTokens(tokens: TokenStream) {
            // unit fields emit nothing
        }
    }

    override fun iterator(): Iterator<Field> =
        when (this) {
            Unit -> emptyPunctuatedIter()
            is Named -> fields.named.iterator()
            is Unnamed -> fields.unnamed.iterator()
        }

    public fun len(): Int =
        when (this) {
            Unit -> 0
            is Named -> fields.named.len()
            is Unnamed -> fields.unnamed.len()
        }

    public fun isEmpty(): Boolean =
        len() == 0

    public fun members(): Sequence<Member> =
        sequence {
            var index = 0u
            for (field in this@Fields) {
                val member =
                    field.ident?.let(Member::Named)
                        ?: Member.Unnamed(Index(index, field.tySpan()))
                yield(member)
                index += 1u
            }
        }
}

/** Named fields of a data structure such as `Point { x: f64, y: f64 }`. */
public data class FieldsNamed(
    public val braceToken: Brace,
    public val named: Punctuated<Field, Comma>,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        braceToken.surround(tokens) { inner ->
            for ((field, comma) in named.pairs()) {
                field.toTokens(inner)
                comma?.toTokens(inner)
            }
        }
    }
}

/** Unnamed fields of a tuple-style data structure such as `Some(T)`. */
public data class FieldsUnnamed(
    public val parenToken: Paren,
    public val unnamed: Punctuated<Field, Comma>,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        parenToken.surround(tokens) { inner ->
            for ((field, comma) in unnamed.pairs()) {
                field.toTokens(inner)
                comma?.toTokens(inner)
            }
        }
    }
}

/** A field of a data class or enum variant. */
public data class Field(
    public val attrs: List<Attribute>,
    public val vis: Visibility,
    public val mutability: FieldMutability,
    public val ident: Ident?,
    public val colonToken: Colon?,
    public val ty: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        vis.toTokens(tokens)
        mutability.toTokens(tokens)
        ident?.toTokens(tokens)
        colonToken?.toTokens(tokens)
        ty.toTokens(tokens)
    }
}

private fun Field.tySpan(): io.github.kotlinmania.procmacro2.Span =
    when (val t = ty) {
        is SynType.Path ->
            t.path.getIdent()?.span() ?: io.github.kotlinmania.procmacro2.Span
                .callSite()
        else ->
            io.github.kotlinmania.procmacro2.Span
                .callSite()
    }

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
        ident.toTokens(tokens)
        generics.toTokens(tokens)
        data.toTokens(tokens)
    }
}

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
        public val variants: Punctuated<Variant, Comma> get() = value.variants

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
    public val variants: Punctuated<Variant, Comma>,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        enumToken.toTokens(tokens)
        braceToken.surround(tokens) { inner ->
            for ((variant, comma) in variants.pairs()) {
                variant.toTokens(inner)
                comma?.toTokens(inner)
            }
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
