// port-lint: source gen/eq.rs
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
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.PathArguments

public fun Abi.eq(other: Abi): Boolean =
    name == other.name

public fun PathArguments.AngleBracketed.eq(other: PathArguments.AngleBracketed): Boolean =
    colon2Token == other.colon2Token &&
        args == other.args

public fun Arm.eq(other: Arm): Boolean =
    attrs == other.attrs &&
        pat == other.pat &&
        guard == other.guard &&
        body == other.body &&
        comma == other.comma

public fun AssocConst.eq(other: AssocConst): Boolean =
    ident == other.ident &&
        generics == other.generics &&
        value == other.value

public fun AssocType.eq(other: AssocType): Boolean =
    ident == other.ident &&
        generics == other.generics &&
        ty == other.ty

public fun AttrStyle.eq(other: AttrStyle): Boolean =
    when {
        this is AttrStyle.Outer && other is AttrStyle.Outer -> true
        this is AttrStyle.Inner && other is AttrStyle.Inner -> true
        else -> false
    }

public fun Attribute.eq(other: Attribute): Boolean =
    style.eq(other.style) &&
        meta == other.meta

public fun BareFnArg.eq(other: BareFnArg): Boolean =
    attrs == other.attrs &&
        name == other.name &&
        ty == other.ty

public fun BareVariadic.eq(other: BareVariadic): Boolean =
    attrs == other.attrs &&
        name == other.name &&
        comma == other.comma

public fun BinOp.eq(other: BinOp): Boolean =
    when {
        this is BinOp.Add && other is BinOp.Add -> true
        this is BinOp.Sub && other is BinOp.Sub -> true
        this is BinOp.Mul && other is BinOp.Mul -> true
        this is BinOp.Div && other is BinOp.Div -> true
        this is BinOp.Rem && other is BinOp.Rem -> true
        this is BinOp.And && other is BinOp.And -> true
        this is BinOp.Or && other is BinOp.Or -> true
        this is BinOp.BitXor && other is BinOp.BitXor -> true
        this is BinOp.BitAnd && other is BinOp.BitAnd -> true
        this is BinOp.BitOr && other is BinOp.BitOr -> true
        this is BinOp.Shl && other is BinOp.Shl -> true
        this is BinOp.Shr && other is BinOp.Shr -> true
        this is BinOp.Eq && other is BinOp.Eq -> true
        this is BinOp.Lt && other is BinOp.Lt -> true
        this is BinOp.Le && other is BinOp.Le -> true
        this is BinOp.Ne && other is BinOp.Ne -> true
        this is BinOp.Ge && other is BinOp.Ge -> true
        this is BinOp.Gt && other is BinOp.Gt -> true
        this is BinOp.AddAssign && other is BinOp.AddAssign -> true
        this is BinOp.SubAssign && other is BinOp.SubAssign -> true
        this is BinOp.MulAssign && other is BinOp.MulAssign -> true
        this is BinOp.DivAssign && other is BinOp.DivAssign -> true
        this is BinOp.RemAssign && other is BinOp.RemAssign -> true
        this is BinOp.BitXorAssign && other is BinOp.BitXorAssign -> true
        this is BinOp.BitAndAssign && other is BinOp.BitAndAssign -> true
        this is BinOp.BitOrAssign && other is BinOp.BitOrAssign -> true
        this is BinOp.ShlAssign && other is BinOp.ShlAssign -> true
        this is BinOp.ShrAssign && other is BinOp.ShrAssign -> true
        else -> false
    }

public fun Block.eq(other: Block): Boolean =
    stmts == other.stmts

public fun BoundLifetimes.eq(other: BoundLifetimes): Boolean =
    lifetimes == other.lifetimes

public fun CapturedParam.eq(other: CapturedParam): Boolean =
    when {
        this is CapturedParam.Lifetime && other is CapturedParam.Lifetime -> lifetime == other.lifetime
        this is CapturedParam.Ident && other is CapturedParam.Ident -> ident == other.ident
        else -> false
    }

public fun GenericParam.ConstParam.eq(other: GenericParam.ConstParam): Boolean =
    attrs == other.attrs &&
        ident == other.ident &&
        ty == other.ty &&
        eqToken == other.eqToken &&
        default == other.default

public fun Constraint.eq(other: Constraint): Boolean =
    ident == other.ident &&
        generics == other.generics &&
        bounds == other.bounds

public fun Data.eq(other: Data): Boolean =
    when {
        this is Data.Struct && other is Data.Struct -> value.eq(other.value)
        this is Data.Enum && other is Data.Enum -> value.eq(other.value)
        this is Data.Union && other is Data.Union -> value.eq(other.value)
        else -> false
    }

public fun DataEnum.eq(other: DataEnum): Boolean =
    variants == other.variants

public fun DataStruct.eq(other: DataStruct): Boolean =
    fields == other.fields &&
        semiToken == other.semiToken

public fun DataUnion.eq(other: DataUnion): Boolean =
    fields == other.fields

public fun DeriveInput.eq(other: DeriveInput): Boolean =
    attrs == other.attrs &&
        vis == other.vis &&
        ident == other.ident &&
        generics == other.generics &&
        data.eq(other.data)
