// port-lint: source gen/debug.rs
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

private fun debugStruct(
    name: String,
    fields: List<Pair<String, Any?>>,
): String =
    fields.joinToString(prefix = "$name { ", postfix = " }") { (field, value) ->
        "$field: ${debugValue(value)}"
    }

private fun debugTuple(
    name: String,
    values: List<Any?>,
): String =
    values.joinToString(prefix = "$name(", postfix = ")") { debugValue(it) }

private fun debugValue(value: Any?): String =
    when (value) {
        null -> "None"
        is Abi -> value.debug()
        is PathArguments.AngleBracketed -> value.debug()
        is Arm -> value.debug()
        is AssocConst -> value.debug()
        is AssocType -> value.debug()
        is AttrStyle -> value.debug()
        is Attribute -> value.debug()
        is BareFnArg -> value.debug()
        is BareVariadic -> value.debug()
        is BinOp -> value.debug()
        is Block -> value.debug()
        is BoundLifetimes -> value.debug()
        is CapturedParam -> value.debug()
        is GenericParam.ConstParam -> value.debug()
        is Constraint -> value.debug()
        is Data -> value.debug()
        is DataEnum -> value.debug()
        is DataStruct -> value.debug()
        is DataUnion -> value.debug()
        is DeriveInput -> value.debug()
        is Iterable<*> -> value.joinToString(prefix = "[", postfix = "]") { debugValue(it) }
        else -> value.toString()
    }

public fun Abi.debug(): String =
    debugStruct(
        "Abi",
        listOf(
            "extern_token" to externToken,
            "name" to name,
        ),
    )

public fun PathArguments.AngleBracketed.debug(): String =
    debug("AngleBracketedGenericArguments")

public fun PathArguments.AngleBracketed.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "colon2_token" to colon2Token,
            "lt_token" to ltToken,
            "args" to args,
            "gt_token" to gtToken,
        ),
    )

public fun Arm.debug(): String =
    debugStruct(
        "Arm",
        listOf(
            "attrs" to attrs,
            "pat" to pat,
            "guard" to guard,
            "fat_arrow_token" to fatArrowToken,
            "body" to body,
            "comma" to comma,
        ),
    )

public fun AssocConst.debug(): String =
    debugStruct(
        "AssocConst",
        listOf(
            "ident" to ident,
            "generics" to generics,
            "eq_token" to eqToken,
            "value" to value,
        ),
    )

public fun AssocType.debug(): String =
    debugStruct(
        "AssocType",
        listOf(
            "ident" to ident,
            "generics" to generics,
            "eq_token" to eqToken,
            "ty" to ty,
        ),
    )

public fun AttrStyle.debug(): String =
    "AttrStyle::" +
        when (this) {
            AttrStyle.Outer -> "Outer"
            is AttrStyle.Inner -> debugTuple("Inner", listOf(bangToken))
        }

public fun Attribute.debug(): String =
    debugStruct(
        "Attribute",
        listOf(
            "pound_token" to poundToken,
            "style" to style,
            "bracket_token" to bracketToken,
            "meta" to meta,
        ),
    )

public fun BareFnArg.debug(): String =
    debugStruct(
        "BareFnArg",
        listOf(
            "attrs" to attrs,
            "name" to name,
            "ty" to ty,
        ),
    )

public fun BareVariadic.debug(): String =
    debugStruct(
        "BareVariadic",
        listOf(
            "attrs" to attrs,
            "name" to name,
            "dots" to dots,
            "comma" to comma,
        ),
    )

public fun BinOp.debug(): String =
    "BinOp::" +
        when (this) {
            is BinOp.Add -> debugTuple("Add", listOf(token))
            is BinOp.Sub -> debugTuple("Sub", listOf(token))
            is BinOp.Mul -> debugTuple("Mul", listOf(token))
            is BinOp.Div -> debugTuple("Div", listOf(token))
            is BinOp.Rem -> debugTuple("Rem", listOf(token))
            is BinOp.And -> debugTuple("And", listOf(token))
            is BinOp.Or -> debugTuple("Or", listOf(token))
            is BinOp.BitXor -> debugTuple("BitXor", listOf(token))
            is BinOp.BitAnd -> debugTuple("BitAnd", listOf(token))
            is BinOp.BitOr -> debugTuple("BitOr", listOf(token))
            is BinOp.Shl -> debugTuple("Shl", listOf(token))
            is BinOp.Shr -> debugTuple("Shr", listOf(token))
            is BinOp.Eq -> debugTuple("Eq", listOf(token))
            is BinOp.Lt -> debugTuple("Lt", listOf(token))
            is BinOp.Le -> debugTuple("Le", listOf(token))
            is BinOp.Ne -> debugTuple("Ne", listOf(token))
            is BinOp.Ge -> debugTuple("Ge", listOf(token))
            is BinOp.Gt -> debugTuple("Gt", listOf(token))
            is BinOp.AddAssign -> debugTuple("AddAssign", listOf(token))
            is BinOp.SubAssign -> debugTuple("SubAssign", listOf(token))
            is BinOp.MulAssign -> debugTuple("MulAssign", listOf(token))
            is BinOp.DivAssign -> debugTuple("DivAssign", listOf(token))
            is BinOp.RemAssign -> debugTuple("RemAssign", listOf(token))
            is BinOp.BitXorAssign -> debugTuple("BitXorAssign", listOf(token))
            is BinOp.BitAndAssign -> debugTuple("BitAndAssign", listOf(token))
            is BinOp.BitOrAssign -> debugTuple("BitOrAssign", listOf(token))
            is BinOp.ShlAssign -> debugTuple("ShlAssign", listOf(token))
            is BinOp.ShrAssign -> debugTuple("ShrAssign", listOf(token))
        }

public fun Block.debug(): String =
    debugStruct(
        "Block",
        listOf(
            "brace_token" to braceToken,
            "stmts" to stmts,
        ),
    )

public fun BoundLifetimes.debug(): String =
    debugStruct(
        "BoundLifetimes",
        listOf(
            "for_token" to forToken,
            "lt_token" to ltToken,
            "lifetimes" to lifetimes,
            "gt_token" to gtToken,
        ),
    )

public fun CapturedParam.debug(): String =
    "CapturedParam::" +
        when (this) {
            is CapturedParam.Lifetime -> debugTuple("Lifetime", listOf(lifetime))
            is CapturedParam.Ident -> debugTuple("Ident", listOf(ident))
        }

public fun GenericParam.ConstParam.debug(): String =
    debugStruct(
        "ConstParam",
        listOf(
            "attrs" to attrs,
            "const_token" to constToken,
            "ident" to ident,
            "colon_token" to colonToken,
            "ty" to ty,
            "eq_token" to eqToken,
            "default" to default,
        ),
    )

public fun Constraint.debug(): String =
    debugStruct(
        "Constraint",
        listOf(
            "ident" to ident,
            "generics" to generics,
            "colon_token" to colonToken,
            "bounds" to bounds,
        ),
    )

public fun Data.debug(): String =
    "Data::" +
        when (this) {
            is Data.Struct -> value.debug("Struct")
            is Data.Enum -> value.debug("Enum")
            is Data.Union -> value.debug("Union")
        }

public fun DataEnum.debug(): String =
    debug("DataEnum")

public fun DataEnum.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "enum_token" to enumToken,
            "brace_token" to braceToken,
            "variants" to variants,
        ),
    )

public fun DataStruct.debug(): String =
    debug("DataStruct")

public fun DataStruct.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "struct_token" to structToken,
            "fields" to fields,
            "semi_token" to semiToken,
        ),
    )

public fun DataUnion.debug(): String =
    debug("DataUnion")

public fun DataUnion.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "union_token" to unionToken,
            "fields" to fields,
        ),
    )

public fun DeriveInput.debug(): String =
    debugStruct(
        "DeriveInput",
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "ident" to ident,
            "generics" to generics,
            "data" to data,
        ),
    )
