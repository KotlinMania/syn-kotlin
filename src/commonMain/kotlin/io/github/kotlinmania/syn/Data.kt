// port-lint: source data.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Semi
import kotlin.native.HiddenFromObjC

/**
 * An enum variant.
 *
 * Hidden from the Objective-C / Swift Export bridge: the `discriminant`
 * field of type `kotlin.Pair<Eq, Expr>?` is bridged with type-parameters
 * erased to `Pair<Any?, Any?>?`, and the auto-generated `Syn.kt` then
 * fails to re-pass the value through the typed call site. Same shape as
 * the [Punctuated] erasure.
 */
@HiddenFromObjC
public data class Variant(
 public val attrs: List<Attribute>,
 public val ident: Ident,
 public val fields: Fields,
 public val discriminant: Pair<Eq, Expr>?,
)

/** Data stored within an enum variant or struct. */
public sealed class Fields : Iterable<Field> {
 public data class Named(val fields: FieldsNamed) : Fields()
 public data class Unnamed(val fields: FieldsUnnamed) : Fields()
 public data object Unit : Fields()

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
 val member = field.ident?.let(Member::Named)
 ?: Member.Unnamed(Index(index, field.tySpan()))
 yield(member)
 index += 1u
 }
 }
}

/** Named fields of a struct or struct variant such as `Point { x: f64, y: f64 }`. */
public data class FieldsNamed(
 public val braceToken: Brace,
 public val named: Punctuated<Field, Comma>,
)

/** Unnamed fields of a tuple struct or tuple variant such as `Some(T)`. */
public data class FieldsUnnamed(
 public val parenToken: Paren,
 public val unnamed: Punctuated<Field, Comma>,
)

/** A field of a struct or enum variant. */
public data class Field(
 public val attrs: List<Attribute>,
 public val vis: Visibility,
 public val mutability: FieldMutability,
 public val ident: Ident?,
 public val colonToken: Colon?,
 public val ty: SynType,
)

private fun Field.tySpan(): io.github.kotlinmania.procmacro2.Span =
 when (val t = ty) {
 is SynType.Path -> t.path.getIdent()?.span() ?: io.github.kotlinmania.procmacro2.Span.callSite()
 else -> io.github.kotlinmania.procmacro2.Span.callSite()
 }

/** Data structure supplied to a derive macro. */
public data class DeriveInput(
 public val attrs: List<Attribute>,
 public val vis: Visibility,
 public val ident: Ident,
 public val generics: Generics,
 public val data: Data,
)

/** The storage of a struct, enum or union data structure. */
public sealed class Data {
 public data class Struct(val value: DataStruct) : Data() {
 public val fields: Fields get() = value.fields
 }

 public data class Enum(val value: DataEnum) : Data() {
 public val variants: Punctuated<Variant, Comma> get() = value.variants
 }

 public data class Union(val value: DataUnion) : Data() {
 public val fields: FieldsNamed get() = value.fields
 }
}

/** A struct input to a derive macro. */
public data class DataStruct(
 public val structToken: io.github.kotlinmania.syn.token.Struct,
 public val fields: Fields,
 public val semiToken: Semi?,
)

/** An enum input to a derive macro. */
public data class DataEnum(
 public val enumToken: io.github.kotlinmania.syn.token.Enum,
 public val braceToken: Brace,
 public val variants: Punctuated<Variant, Comma>,
)

/** An untagged union input to a derive macro. */
public data class DataUnion(
 public val unionToken: io.github.kotlinmania.syn.token.Union,
 public val fields: FieldsNamed,
)
