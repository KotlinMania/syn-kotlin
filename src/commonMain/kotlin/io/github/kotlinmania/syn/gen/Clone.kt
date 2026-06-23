// port-lint: source gen/clone.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.AssocConst
import io.github.kotlinmania.syn.AssocType
import io.github.kotlinmania.syn.AttrStyle
import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.BareFnArg
import io.github.kotlinmania.syn.BareVariadic
import io.github.kotlinmania.syn.BinOp
import io.github.kotlinmania.syn.Block
import io.github.kotlinmania.syn.BoundLifetimes
import io.github.kotlinmania.syn.CapturedParam
import io.github.kotlinmania.syn.Constraint
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DataEnum
import io.github.kotlinmania.syn.DataStruct
import io.github.kotlinmania.syn.DataUnion
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.EqExpr
import io.github.kotlinmania.syn.Field
import io.github.kotlinmania.syn.FieldMutability
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.copy

public fun Abi.clone(): Abi =
    Abi(
        externToken = externToken,
        name = name?.copy(),
    )

public fun PathArguments.AngleBracketed.clone(): PathArguments.AngleBracketed =
    copy(args = args.copy({ it.deepCopy() }, { it }))

public fun Arm.clone(): Arm =
    deepCopy()

public fun AssocConst.clone(): AssocConst =
    deepCopy()

public fun AssocType.clone(): AssocType =
    deepCopy()

public fun AttrStyle.clone(): AttrStyle =
    this

public fun Attribute.clone(): Attribute =
    deepCopy()

public fun BareFnArg.clone(): BareFnArg =
    deepCopy()

public fun BareVariadic.clone(): BareVariadic =
    deepCopy()

public fun BinOp.clone(): BinOp =
    this

public fun Block.clone(): Block =
    deepCopy()

public fun BoundLifetimes.clone(): BoundLifetimes =
    deepCopy()

public fun CapturedParam.clone(): CapturedParam =
    deepCopy()

public fun GenericParam.ConstParam.clone(): GenericParam.ConstParam =
    deepCopy()

public fun Constraint.clone(): Constraint =
    deepCopy()

public fun Data.clone(): Data =
    when (this) {
        is Data.Struct -> Data.Struct(value.clone())
        is Data.Enum -> Data.Enum(value.clone())
        is Data.Union -> Data.Union(value.clone())
    }

public fun DataEnum.clone(): DataEnum =
    DataEnum(
        enumToken = enumToken,
        braceToken = braceToken,
        variants = variants.copy({ it.cloneVariant() }, { it }),
    )

public fun DataStruct.clone(): DataStruct =
    DataStruct(
        structToken = structToken,
        fields = fields.cloneFields(),
        semiToken = semiToken,
    )

public fun DataUnion.clone(): DataUnion =
    DataUnion(
        unionToken = unionToken,
        fields = fields.cloneFieldsNamed(),
    )

public fun DeriveInput.clone(): DeriveInput =
    DeriveInput(
        attrs = attrs.map { it.deepCopy() },
        vis = vis.cloneVisibility(),
        ident = ident.copy(),
        generics = generics.cloneGenerics(),
        data = data.clone(),
    )

private fun Generics.cloneGenerics(): Generics =
    Generics(
        ltToken = ltToken,
        params = params.copy({ it.deepCopy() }, { it }),
        gtToken = gtToken,
        whereClause = whereClause?.deepCopy(),
    )

private fun Visibility.cloneVisibility(): Visibility =
    when (this) {
        is Visibility.Public -> copy()
        is Visibility.Restricted -> copy(path = path.deepCopy())
        Visibility.Inherited -> Visibility.Inherited
    }

private fun FieldMutability.cloneFieldMutability(): FieldMutability =
    when (this) {
        FieldMutability.None -> FieldMutability.None
        is FieldMutability.Mut -> copy()
    }

private fun Fields.cloneFields(): Fields =
    when (this) {
        is Fields.Named -> Fields.Named(fields.cloneFieldsNamed())
        is Fields.Unnamed -> Fields.Unnamed(fields.cloneFieldsUnnamed())
        Fields.Unit -> Fields.Unit
    }

private fun FieldsNamed.cloneFieldsNamed(): FieldsNamed =
    FieldsNamed(
        braceToken = braceToken,
        named = named.copy({ it.cloneField() }, { it }),
    )

private fun FieldsUnnamed.cloneFieldsUnnamed(): FieldsUnnamed =
    FieldsUnnamed(
        parenToken = parenToken,
        unnamed = unnamed.copy({ it.cloneField() }, { it }),
    )

private fun Field.cloneField(): Field =
    Field(
        attrs = attrs.map { it.deepCopy() },
        vis = vis.cloneVisibility(),
        mutability = mutability.cloneFieldMutability(),
        ident = ident?.copy(),
        colonToken = colonToken,
        ty = ty.deepCopy(),
    )

private fun EqExpr.cloneEqExpr(): EqExpr =
    copy(expr = expr.deepCopy())

private fun Variant.cloneVariant(): Variant =
    Variant(
        attrs = attrs.map { it.deepCopy() },
        ident = ident.copy(),
        fields = fields.cloneFields(),
        discriminant = discriminant?.cloneEqExpr(),
    )
