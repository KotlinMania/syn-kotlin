// port-lint: source gen/hash.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.AndLifetime
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.AsIdent
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
import io.github.kotlinmania.syn.ElseExpr
import io.github.kotlinmania.syn.EqExpr
import io.github.kotlinmania.syn.EqSynType
import io.github.kotlinmania.syn.Expr
import io.github.kotlinmania.syn.Field
import io.github.kotlinmania.syn.FieldMutability
import io.github.kotlinmania.syn.FieldPat
import io.github.kotlinmania.syn.FieldValue
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
import io.github.kotlinmania.syn.File
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.ForeignItem
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.IdentColon
import io.github.kotlinmania.syn.IfExpr
import io.github.kotlinmania.syn.ImplItem
import io.github.kotlinmania.syn.ImplRestriction
import io.github.kotlinmania.syn.Index
import io.github.kotlinmania.syn.Item
import io.github.kotlinmania.syn.Label
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.LitBool
import io.github.kotlinmania.syn.LitByte
import io.github.kotlinmania.syn.LitByteStr
import io.github.kotlinmania.syn.LitCStr
import io.github.kotlinmania.syn.LitChar
import io.github.kotlinmania.syn.LitFloat
import io.github.kotlinmania.syn.LitInt
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.LocalInit
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.MacroDelimiter
import io.github.kotlinmania.syn.Member
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.ModContent
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatColon
import io.github.kotlinmania.syn.PatRest
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.PathTrait
import io.github.kotlinmania.syn.PointerMutability
import io.github.kotlinmania.syn.QSelf
import io.github.kotlinmania.syn.RangeLimits
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.StaticMutability
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynPunctuated
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.TraitBoundModifier
import io.github.kotlinmania.syn.TraitItem
import io.github.kotlinmania.syn.TypeParamBound
import io.github.kotlinmania.syn.UnOp
import io.github.kotlinmania.syn.UseTree
import io.github.kotlinmania.syn.Variadic
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.WhereClause
import io.github.kotlinmania.syn.WherePredicate
import io.github.kotlinmania.syn.tokenStreamHash

public class SynHasher {
    private var value: Int = -0x7ee3623b

    public fun writeU8(byte: Int) {
        mix(byte and 0xff)
    }

    public fun write(value: Any?) {
        when (value) {
            null -> {
                writeU8(0)
            }

            is TokenStream -> {
                writeU8(1)
                mix(tokenStreamHash(value))
            }

            is SynPunctuated -> {
                writeU8(1)
                writeU8(if (value.trailingPunct()) 1 else 0)
                for (elem in value) {
                    write(elem)
                }
            }

            is List<*> -> {
                writeU8(1)
                mix(value.size)
                for (elem in value) {
                    write(elem)
                }
            }

            is Abi -> {
                writeU8(1)
                value.hash(this)
            }

            is AndLifetime -> {
                writeU8(1)
                value.hash(this)
            }

            is Arm -> {
                writeU8(1)
                value.hash(this)
            }

            is AsIdent -> {
                writeU8(1)
                value.hash(this)
            }

            is AssocConst -> {
                writeU8(1)
                value.hash(this)
            }

            is AssocType -> {
                writeU8(1)
                value.hash(this)
            }

            is AttrStyle -> {
                writeU8(1)
                value.hash(this)
            }

            is Attribute -> {
                writeU8(1)
                value.hash(this)
            }

            is BareFnArg -> {
                writeU8(1)
                value.hash(this)
            }

            is BareVariadic -> {
                writeU8(1)
                value.hash(this)
            }

            is BinOp -> {
                writeU8(1)
                value.hash(this)
            }

            is Block -> {
                writeU8(1)
                value.hash(this)
            }

            is BoundLifetimes -> {
                writeU8(1)
                value.hash(this)
            }

            is CapturedParam -> {
                writeU8(1)
                value.hash(this)
            }

            is Constraint -> {
                writeU8(1)
                value.hash(this)
            }

            is Data -> {
                writeU8(1)
                value.hash(this)
            }

            is DataEnum -> {
                writeU8(1)
                value.hash(this)
            }

            is DataStruct -> {
                writeU8(1)
                value.hash(this)
            }

            is DataUnion -> {
                writeU8(1)
                value.hash(this)
            }

            is DeriveInput -> {
                writeU8(1)
                value.hash(this)
            }

            is ElseExpr -> {
                writeU8(1)
                value.hash(this)
            }

            is EqExpr -> {
                writeU8(1)
                value.hash(this)
            }

            is EqSynType -> {
                writeU8(1)
                value.hash(this)
            }

            is Expr -> {
                writeU8(1)
                value.hash(this)
            }

            is Field -> {
                writeU8(1)
                value.hash(this)
            }

            is FieldMutability -> {
                writeU8(1)
                value.hash(this)
            }

            is FieldPat -> {
                writeU8(1)
                value.hash(this)
            }

            is FieldValue -> {
                writeU8(1)
                value.hash(this)
            }

            is Fields -> {
                writeU8(1)
                value.hash(this)
            }

            is FieldsNamed -> {
                writeU8(1)
                value.hash(this)
            }

            is FieldsUnnamed -> {
                writeU8(1)
                value.hash(this)
            }

            is File -> {
                writeU8(1)
                value.hash(this)
            }

            is FnArg -> {
                writeU8(1)
                value.hash(this)
            }

            is ForeignItem -> {
                writeU8(1)
                value.hash(this)
            }

            is GenericArgument -> {
                writeU8(1)
                value.hash(this)
            }

            is GenericParam -> {
                writeU8(1)
                value.hash(this)
            }

            is Generics -> {
                writeU8(1)
                value.hash(this)
            }

            is IdentColon -> {
                writeU8(1)
                value.hash(this)
            }

            is IfExpr -> {
                writeU8(1)
                value.hash(this)
            }

            is ImplItem -> {
                writeU8(1)
                value.hash(this)
            }

            is ImplRestriction -> {
                writeU8(1)
                value.hash(this)
            }

            is Index -> {
                writeU8(1)
                value.hash(this)
            }

            is Item -> {
                writeU8(1)
                value.hash(this)
            }

            is Label -> {
                writeU8(1)
                value.hash(this)
            }

            is Lifetime -> {
                writeU8(1)
                value.hash(this)
            }

            is Lit -> {
                writeU8(1)
                value.hash(this)
            }

            is LitBool -> {
                writeU8(1)
                value.hash(this)
            }

            is LitByte -> {
                writeU8(1)
                value.hash(this)
            }

            is LitByteStr -> {
                writeU8(1)
                value.hash(this)
            }

            is LitCStr -> {
                writeU8(1)
                value.hash(this)
            }

            is LitChar -> {
                writeU8(1)
                value.hash(this)
            }

            is LitFloat -> {
                writeU8(1)
                value.hash(this)
            }

            is LitInt -> {
                writeU8(1)
                value.hash(this)
            }

            is LitStr -> {
                writeU8(1)
                value.hash(this)
            }

            is LocalInit -> {
                writeU8(1)
                value.hash(this)
            }

            is Macro -> {
                writeU8(1)
                value.hash(this)
            }

            is MacroDelimiter -> {
                writeU8(1)
                value.hash(this)
            }

            is Member -> {
                writeU8(1)
                value.hash(this)
            }

            is Meta -> {
                writeU8(1)
                value.hash(this)
            }

            is ModContent -> {
                writeU8(1)
                value.hash(this)
            }

            is Pat -> {
                writeU8(1)
                value.hash(this)
            }

            is PatColon -> {
                writeU8(1)
                value.hash(this)
            }

            is PatRest -> {
                writeU8(1)
                value.hash(this)
            }

            is PatType -> {
                writeU8(1)
                value.hash(this)
            }

            is Path -> {
                writeU8(1)
                value.hash(this)
            }

            is PathArguments -> {
                writeU8(1)
                value.hash(this)
            }

            is PathSegment -> {
                writeU8(1)
                value.hash(this)
            }

            is PathTrait -> {
                writeU8(1)
                value.hash(this)
            }

            is PointerMutability -> {
                writeU8(1)
                value.hash(this)
            }

            is QSelf -> {
                writeU8(1)
                value.hash(this)
            }

            is RangeLimits -> {
                writeU8(1)
                value.hash(this)
            }

            is ReturnType -> {
                writeU8(1)
                value.hash(this)
            }

            is Signature -> {
                writeU8(1)
                value.hash(this)
            }

            is StaticMutability -> {
                writeU8(1)
                value.hash(this)
            }

            is Stmt -> {
                writeU8(1)
                value.hash(this)
            }

            is SynType -> {
                writeU8(1)
                value.hash(this)
            }

            is TraitBoundModifier -> {
                writeU8(1)
                value.hash(this)
            }

            is TraitItem -> {
                writeU8(1)
                value.hash(this)
            }

            is TypeParamBound -> {
                writeU8(1)
                value.hash(this)
            }

            is UnOp -> {
                writeU8(1)
                value.hash(this)
            }

            is UseTree -> {
                writeU8(1)
                value.hash(this)
            }

            is Variadic -> {
                writeU8(1)
                value.hash(this)
            }

            is Variant -> {
                writeU8(1)
                value.hash(this)
            }

            is Visibility -> {
                writeU8(1)
                value.hash(this)
            }

            is WhereClause -> {
                writeU8(1)
                value.hash(this)
            }

            is WherePredicate -> {
                writeU8(1)
                value.hash(this)
            }

            else -> {
                writeU8(1)
                mix(value.hashCode())
            }
        }
    }

    public fun finish(): Int =
        value

    private fun mix(component: Int) {
        value = value xor component
        value *= 16777619
    }
}

private inline fun hashToInt(hash: (SynHasher) -> Unit): Int {
    val state = SynHasher()
    hash(state)
    return state.finish()
}

internal fun exprHash(expr: Expr): Int =
    hashToInt { expr.hash(it) }

internal fun punctuatedHash(list: SynPunctuated): Int =
    hashToInt { it.write(list) }

public fun Abi.hash(state: SynHasher) {
    state.write(name)
}

public fun Abi.hash(): Int =
    hashToInt { hash(it) }

public fun AndLifetime.hash(state: SynHasher) {
    state.write(lifetime)
}

public fun AndLifetime.hash(): Int =
    hashToInt { hash(it) }

public fun Arm.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
    state.write(guard)
    state.write(body)
    state.write(comma)
}

public fun Arm.hash(): Int =
    hashToInt { hash(it) }

public fun AsIdent.hash(state: SynHasher) {
    state.write(ident)
}

public fun AsIdent.hash(): Int =
    hashToInt { hash(it) }

public fun AssocConst.hash(state: SynHasher) {
    state.write(ident)
    state.write(generics)
    state.write(value)
}

public fun AssocConst.hash(): Int =
    hashToInt { hash(it) }

public fun AssocType.hash(state: SynHasher) {
    state.write(ident)
    state.write(generics)
    state.write(ty)
}

public fun AssocType.hash(): Int =
    hashToInt { hash(it) }

public fun Attribute.hash(state: SynHasher) {
    state.write(style)
    state.write(meta)
}

public fun Attribute.hash(): Int =
    hashToInt { hash(it) }

public fun BareFnArg.hash(state: SynHasher) {
    state.write(attrs)
    state.write(name)
    state.write(ty)
}

public fun BareFnArg.hash(): Int =
    hashToInt { hash(it) }

public fun BareVariadic.hash(state: SynHasher) {
    state.write(attrs)
    state.write(name)
    state.write(comma)
}

public fun BareVariadic.hash(): Int =
    hashToInt { hash(it) }

public fun Block.hash(state: SynHasher) {
    state.write(stmts)
}

public fun Block.hash(): Int =
    hashToInt { hash(it) }

public fun BoundLifetimes.hash(state: SynHasher) {
    state.write(lifetimes)
}

public fun BoundLifetimes.hash(): Int =
    hashToInt { hash(it) }

public fun Constraint.hash(state: SynHasher) {
    state.write(ident)
    state.write(generics)
    state.write(bounds)
}

public fun Constraint.hash(): Int =
    hashToInt { hash(it) }

public fun DataEnum.hash(state: SynHasher) {
    state.write(variants)
}

public fun DataEnum.hash(): Int =
    hashToInt { hash(it) }

public fun DataStruct.hash(state: SynHasher) {
    state.write(fields)
    state.write(semiToken)
}

public fun DataStruct.hash(): Int =
    hashToInt { hash(it) }

public fun DataUnion.hash(state: SynHasher) {
    state.write(fields)
}

public fun DataUnion.hash(): Int =
    hashToInt { hash(it) }

public fun DeriveInput.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
    state.write(data)
}

public fun DeriveInput.hash(): Int =
    hashToInt { hash(it) }

public fun ElseExpr.hash(state: SynHasher) {
    state.write(expr)
}

public fun ElseExpr.hash(): Int =
    hashToInt { hash(it) }

public fun EqExpr.hash(state: SynHasher) {
    state.write(expr)
}

public fun EqExpr.hash(): Int =
    hashToInt { hash(it) }

public fun EqSynType.hash(state: SynHasher) {
    state.write(type)
}

public fun EqSynType.hash(): Int =
    hashToInt { hash(it) }

public fun Expr.Array.hash(state: SynHasher) {
    state.write(attrs)
    state.write(elems)
}

public fun Expr.Assign.hash(state: SynHasher) {
    state.write(attrs)
    state.write(left)
    state.write(right)
}

public fun Expr.Async.hash(state: SynHasher) {
    state.write(attrs)
    state.write(capture)
    state.write(block)
}

public fun Expr.Await.hash(state: SynHasher) {
    state.write(attrs)
    state.write(base)
}

public fun Expr.Binary.hash(state: SynHasher) {
    state.write(attrs)
    state.write(left)
    state.write(op)
    state.write(right)
}

public fun Expr.BlockExpr.hash(state: SynHasher) {
    state.write(attrs)
    state.write(label)
    state.write(block)
}

public fun Expr.Break.hash(state: SynHasher) {
    state.write(attrs)
    state.write(label)
    state.write(expr)
}

public fun Expr.Call.hash(state: SynHasher) {
    state.write(attrs)
    state.write(func)
    state.write(args)
}

public fun Expr.Cast.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
    state.write(ty)
}

public fun Expr.Closure.hash(state: SynHasher) {
    state.write(attrs)
    state.write(lifetimes)
    state.write(constness)
    state.write(movability)
    state.write(asyncness)
    state.write(capture)
    state.write(inputs)
    state.write(output)
    state.write(body)
}

public fun Expr.Const.hash(state: SynHasher) {
    state.write(attrs)
    state.write(block)
}

public fun Expr.Continue.hash(state: SynHasher) {
    state.write(attrs)
    state.write(label)
}

public fun Expr.Field.hash(state: SynHasher) {
    state.write(attrs)
    state.write(base)
    state.write(member)
}

public fun Expr.ForLoop.hash(state: SynHasher) {
    state.write(attrs)
    state.write(label)
    state.write(pat)
    state.write(expr)
    state.write(body)
}

public fun Expr.Group.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
}

public fun Expr.If.hash(state: SynHasher) {
    state.write(attrs)
    state.write(cond)
    state.write(thenBranch)
    state.write(elseBranch)
}

public fun Expr.Index.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
    state.write(index)
}

public fun Expr.Infer.hash(state: SynHasher) {
    state.write(attrs)
}

public fun Expr.Let.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
    state.write(expr)
}

public fun Expr.Lit.hash(state: SynHasher) {
    state.write(attrs)
    state.write(lit)
}

public fun Expr.Loop.hash(state: SynHasher) {
    state.write(attrs)
    state.write(label)
    state.write(body)
}

public fun Expr.Macro.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mac)
}

public fun Expr.Match.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
    state.write(arms)
}

public fun Expr.MethodCall.hash(state: SynHasher) {
    state.write(attrs)
    state.write(receiver)
    state.write(method)
    state.write(turbofish)
    state.write(args)
}

public fun Expr.Paren.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
}

public fun Expr.Path.hash(state: SynHasher) {
    state.write(attrs)
    state.write(qself)
    state.write(path)
}

public fun Expr.Range.hash(state: SynHasher) {
    state.write(attrs)
    state.write(start)
    state.write(limits)
    state.write(end)
}

public fun Expr.RawAddr.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mutability)
    state.write(expr)
}

public fun Expr.Reference.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mutability)
    state.write(expr)
}

public fun Expr.Repeat.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
    state.write(len)
}

public fun Expr.Return.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
}

public fun Expr.Struct.hash(state: SynHasher) {
    state.write(attrs)
    state.write(qself)
    state.write(path)
    state.write(fields)
    state.write(dot2Token)
    state.write(rest)
}

public fun Expr.Try.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
}

public fun Expr.TryBlock.hash(state: SynHasher) {
    state.write(attrs)
    state.write(block)
}

public fun Expr.Tuple.hash(state: SynHasher) {
    state.write(attrs)
    state.write(elems)
}

public fun Expr.Unary.hash(state: SynHasher) {
    state.write(attrs)
    state.write(op)
    state.write(expr)
}

public fun Expr.Unsafe.hash(state: SynHasher) {
    state.write(attrs)
    state.write(block)
}

public fun Expr.While.hash(state: SynHasher) {
    state.write(attrs)
    state.write(label)
    state.write(cond)
    state.write(body)
}

public fun Expr.Yield.hash(state: SynHasher) {
    state.write(attrs)
    state.write(expr)
}

public fun Field.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(mutability)
    state.write(ident)
    state.write(colonToken)
    state.write(ty)
}

public fun Field.hash(): Int =
    hashToInt { hash(it) }

public fun FieldPat.hash(state: SynHasher) {
    state.write(attrs)
    state.write(member)
    state.write(colonToken)
    state.write(pat)
}

public fun FieldPat.hash(): Int =
    hashToInt { hash(it) }

public fun FieldValue.hash(state: SynHasher) {
    state.write(attrs)
    state.write(member)
    state.write(colonToken)
    state.write(expr)
}

public fun FieldValue.hash(): Int =
    hashToInt { hash(it) }

public fun FieldsNamed.hash(state: SynHasher) {
    state.write(named)
}

public fun FieldsNamed.hash(): Int =
    hashToInt { hash(it) }

public fun FieldsUnnamed.hash(state: SynHasher) {
    state.write(unnamed)
}

public fun FieldsUnnamed.hash(): Int =
    hashToInt { hash(it) }

public fun File.hash(state: SynHasher) {
    state.write(shebang)
    state.write(attrs)
    state.write(items)
}

public fun File.hash(): Int =
    hashToInt { hash(it) }

public fun FnArg.Receiver.hash(state: SynHasher) {
    state.write(attrs)
    state.write(reference)
    state.write(mutability)
    state.write(colonToken)
    state.write(type)
}

public fun FnArg.Receiver.hash(): Int =
    hashToInt { hash(it) }

public fun ForeignItem.Fn.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(sig)
}

public fun ForeignItem.Fn.hash(): Int =
    hashToInt { hash(it) }

public fun ForeignItem.Macro.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mac)
    state.write(semiToken)
}

public fun ForeignItem.Macro.hash(): Int =
    hashToInt { hash(it) }

public fun ForeignItem.Static.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(mutability)
    state.write(ident)
    state.write(ty)
}

public fun ForeignItem.Static.hash(): Int =
    hashToInt { hash(it) }

public fun ForeignItem.ItemType.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
}

public fun ForeignItem.ItemType.hash(): Int =
    hashToInt { hash(it) }

public fun GenericParam.ConstParam.hash(state: SynHasher) {
    state.write(attrs)
    state.write(ident)
    state.write(ty)
    state.write(eqToken)
    state.write(default)
}

public fun GenericParam.ConstParam.hash(): Int =
    hashToInt { hash(it) }

public fun GenericParam.LifetimeParam.hash(state: SynHasher) {
    state.write(attrs)
    state.write(lifetime)
    state.write(colonToken)
    state.write(bounds)
}

public fun GenericParam.LifetimeParam.hash(): Int =
    hashToInt { hash(it) }

public fun GenericParam.TypeParam.hash(state: SynHasher) {
    state.write(attrs)
    state.write(ident)
    state.write(colonToken)
    state.write(bounds)
    state.write(eqToken)
    state.write(default)
}

public fun GenericParam.TypeParam.hash(): Int =
    hashToInt { hash(it) }

public fun Generics.hash(state: SynHasher) {
    state.write(ltToken)
    state.write(params)
    state.write(gtToken)
    state.write(whereClause)
}

public fun Generics.hash(): Int =
    hashToInt { hash(it) }

public fun IdentColon.hash(state: SynHasher) {
    state.write(ident)
}

public fun IdentColon.hash(): Int =
    hashToInt { hash(it) }

public fun IfExpr.hash(state: SynHasher) {
    state.write(expr)
}

public fun IfExpr.hash(): Int =
    hashToInt { hash(it) }

public fun ImplItem.Const.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(defaultness)
    state.write(ident)
    state.write(generics)
    state.write(ty)
    state.write(expr)
}

public fun ImplItem.Const.hash(): Int =
    hashToInt { hash(it) }

public fun ImplItem.Fn.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(defaultness)
    state.write(sig)
    state.write(block)
}

public fun ImplItem.Fn.hash(): Int =
    hashToInt { hash(it) }

public fun ImplItem.Macro.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mac)
    state.write(semiToken)
}

public fun ImplItem.Macro.hash(): Int =
    hashToInt { hash(it) }

public fun ImplItem.AssocType.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(defaultness)
    state.write(ident)
    state.write(generics)
    state.write(ty)
}

public fun ImplItem.AssocType.hash(): Int =
    hashToInt { hash(it) }

public fun Index.hash(state: SynHasher) {
    state.write(index)
}

public fun Index.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Const.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(colonToken)
    state.write(ty)
    state.write(eqToken)
    state.write(expr)
}

public fun Item.Const.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Enum.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
    state.write(variants)
}

public fun Item.Enum.hash(): Int =
    hashToInt { hash(it) }

public fun Item.ExternCrate.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(rename)
}

public fun Item.ExternCrate.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Fn.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(sig)
    state.write(block)
}

public fun Item.Fn.hash(): Int =
    hashToInt { hash(it) }

public fun Item.ForeignMod.hash(state: SynHasher) {
    state.write(attrs)
    state.write(unsafety)
    state.write(abi)
    state.write(items)
}

public fun Item.ForeignMod.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Impl.hash(state: SynHasher) {
    state.write(attrs)
    state.write(defaultness)
    state.write(unsafety)
    state.write(generics)
    state.write(traitPath)
    state.write(selfType)
    state.write(items)
}

public fun Item.Impl.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Macro.hash(state: SynHasher) {
    state.write(attrs)
    state.write(ident)
    state.write(mac)
    state.write(semiToken)
}

public fun Item.Macro.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Mod.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(unsafety)
    state.write(ident)
    state.write(content)
}

public fun Item.Mod.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Static.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(mutability)
    state.write(ident)
    state.write(ty)
    state.write(expr)
}

public fun Item.Static.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Struct.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
    state.write(fields)
    state.write(semiToken)
}

public fun Item.Struct.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Trait.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(unsafety)
    state.write(autoToken)
    state.write(restriction)
    state.write(ident)
    state.write(generics)
    state.write(colonToken)
    state.write(supertraits)
    state.write(items)
}

public fun Item.Trait.hash(): Int =
    hashToInt { hash(it) }

public fun Item.TraitAlias.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
    state.write(bounds)
}

public fun Item.TraitAlias.hash(): Int =
    hashToInt { hash(it) }

public fun Item.ItemType.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
    state.write(ty)
}

public fun Item.ItemType.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Union.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
    state.write(fields)
}

public fun Item.Union.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Use.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(leadingColon)
    state.write(tree)
}

public fun Item.Use.hash(): Int =
    hashToInt { hash(it) }

public fun Label.hash(state: SynHasher) {
    state.write(name)
}

public fun Label.hash(): Int =
    hashToInt { hash(it) }

public fun LocalInit.hash(state: SynHasher) {
    state.write(expr)
    state.write(diverge)
}

public fun LocalInit.hash(): Int =
    hashToInt { hash(it) }

public fun Macro.hash(state: SynHasher) {
    state.write(path)
    state.write(delimiter)
    state.write(tokens)
}

public fun Macro.hash(): Int =
    hashToInt { hash(it) }

public fun Meta.List.hash(state: SynHasher) {
    state.write(path)
    state.write(delimiter)
    state.write(tokens)
}

public fun Meta.List.hash(): Int =
    hashToInt { hash(it) }

public fun Meta.NameValue.hash(state: SynHasher) {
    state.write(path)
    state.write(value)
}

public fun Meta.NameValue.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Const.hash(state: SynHasher) {
    state.write(attrs)
    state.write(block)
}

public fun Pat.Const.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Ident.hash(state: SynHasher) {
    state.write(attrs)
    state.write(byRef)
    state.write(mutability)
    state.write(ident)
    state.write(atToken)
    state.write(subpat)
}

public fun Pat.Ident.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Lit.hash(state: SynHasher) {
    state.write(attrs)
    state.write(lit)
}

public fun Pat.Lit.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Macro.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mac)
}

public fun Pat.Macro.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Or.hash(state: SynHasher) {
    state.write(attrs)
    state.write(leadingVert)
    state.write(cases)
}

public fun Pat.Or.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.PatParen.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
}

public fun Pat.PatParen.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Path.hash(state: SynHasher) {
    state.write(attrs)
    state.write(qself)
    state.write(path)
}

public fun Pat.Path.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Range.hash(state: SynHasher) {
    state.write(attrs)
    state.write(start)
    state.write(limits)
    state.write(end)
}

public fun Pat.Range.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Reference.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mutability)
    state.write(pat)
}

public fun Pat.Reference.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Rest.hash(state: SynHasher) {
    state.write(attrs)
}

public fun Pat.Rest.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Slice.hash(state: SynHasher) {
    state.write(attrs)
    state.write(elems)
}

public fun Pat.Slice.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Struct.hash(state: SynHasher) {
    state.write(attrs)
    state.write(qself)
    state.write(path)
    state.write(fields)
    state.write(dot2Token)
    state.write(rest)
}

public fun Pat.Struct.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Tuple.hash(state: SynHasher) {
    state.write(attrs)
    state.write(elems)
}

public fun Pat.Tuple.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.TupleStruct.hash(state: SynHasher) {
    state.write(attrs)
    state.write(qself)
    state.write(path)
    state.write(elems)
}

public fun Pat.TupleStruct.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.TypeAscription.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
    state.write(ty)
}

public fun Pat.TypeAscription.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Wild.hash(state: SynHasher) {
    state.write(attrs)
}

public fun Pat.Wild.hash(): Int =
    hashToInt { hash(it) }

public fun PatColon.hash(state: SynHasher) {
    state.write(pat)
}

public fun PatColon.hash(): Int =
    hashToInt { hash(it) }

public fun PatRest.hash(state: SynHasher) {
    state.write(attrs)
    state.write(dot2Token)
}

public fun PatRest.hash(): Int =
    hashToInt { hash(it) }

public fun PatType.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
    state.write(ty)
}

public fun PatType.hash(): Int =
    hashToInt { hash(it) }

public fun Path.hash(state: SynHasher) {
    state.write(leadingColon)
    state.write(segments)
}

public fun Path.hash(): Int =
    hashToInt { hash(it) }

public fun PathArguments.AngleBracketed.hash(state: SynHasher) {
    state.write(colon2Token)
    state.write(args)
}

public fun PathArguments.AngleBracketed.hash(): Int =
    hashToInt { hash(it) }

public fun PathArguments.Parenthesized.hash(state: SynHasher) {
    state.write(inputs)
    state.write(output)
}

public fun PathArguments.Parenthesized.hash(): Int =
    hashToInt { hash(it) }

public fun PathSegment.hash(state: SynHasher) {
    state.write(ident)
    state.write(arguments)
}

public fun PathSegment.hash(): Int =
    hashToInt { hash(it) }

public fun PathTrait.hash(state: SynHasher) {
    state.write(polarity)
    state.write(path)
}

public fun PathTrait.hash(): Int =
    hashToInt { hash(it) }

public fun QSelf.hash(state: SynHasher) {
    state.write(ty)
    state.write(position)
    state.write(asToken)
}

public fun QSelf.hash(): Int =
    hashToInt { hash(it) }

public fun Signature.hash(state: SynHasher) {
    state.write(constness)
    state.write(asyncness)
    state.write(unsafety)
    state.write(abi)
    state.write(ident)
    state.write(generics)
    state.write(inputs)
    state.write(variadic)
    state.write(output)
}

public fun Signature.hash(): Int =
    hashToInt { hash(it) }

public fun Stmt.Local.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
    state.write(init)
}

public fun Stmt.Local.hash(): Int =
    hashToInt { hash(it) }

public fun Stmt.MacroStmt.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mac)
    state.write(semiToken)
}

public fun Stmt.MacroStmt.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Array.hash(state: SynHasher) {
    state.write(elem)
    state.write(len)
}

public fun SynType.Array.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.BareFn.hash(state: SynHasher) {
    state.write(lifetimes)
    state.write(unsafety)
    state.write(abi)
    state.write(inputs)
    state.write(variadic)
    state.write(output)
}

public fun SynType.BareFn.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Group.hash(state: SynHasher) {
    state.write(elem)
}

public fun SynType.Group.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.ImplTrait.hash(state: SynHasher) {
    state.write(bounds)
}

public fun SynType.ImplTrait.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Infer.hash(state: SynHasher) = Unit

public fun SynType.Infer.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Macro.hash(state: SynHasher) {
    state.write(mac)
}

public fun SynType.Macro.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Never.hash(state: SynHasher) = Unit

public fun SynType.Never.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Paren.hash(state: SynHasher) {
    state.write(elem)
}

public fun SynType.Paren.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Path.hash(state: SynHasher) {
    state.write(qself)
    state.write(path)
}

public fun SynType.Path.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Ptr.hash(state: SynHasher) {
    state.write(constToken)
    state.write(mutability)
    state.write(elem)
}

public fun SynType.Ptr.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Reference.hash(state: SynHasher) {
    state.write(lifetime)
    state.write(mutability)
    state.write(elem)
}

public fun SynType.Reference.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Slice.hash(state: SynHasher) {
    state.write(elem)
}

public fun SynType.Slice.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.TraitObject.hash(state: SynHasher) {
    state.write(dynToken)
    state.write(bounds)
}

public fun SynType.TraitObject.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Tuple.hash(state: SynHasher) {
    state.write(elems)
}

public fun SynType.Tuple.hash(): Int =
    hashToInt { hash(it) }

public fun TraitItem.Const.hash(state: SynHasher) {
    state.write(attrs)
    state.write(ident)
    state.write(generics)
    state.write(ty)
    state.write(default)
}

public fun TraitItem.Const.hash(): Int =
    hashToInt { hash(it) }

public fun TraitItem.Fn.hash(state: SynHasher) {
    state.write(attrs)
    state.write(sig)
    state.write(default)
    state.write(semiToken)
}

public fun TraitItem.Fn.hash(): Int =
    hashToInt { hash(it) }

public fun TraitItem.Macro.hash(state: SynHasher) {
    state.write(attrs)
    state.write(mac)
    state.write(semiToken)
}

public fun TraitItem.Macro.hash(): Int =
    hashToInt { hash(it) }

public fun TraitItem.AssocType.hash(state: SynHasher) {
    state.write(attrs)
    state.write(ident)
    state.write(generics)
    state.write(colonToken)
    state.write(bounds)
    state.write(default)
}

public fun TraitItem.AssocType.hash(): Int =
    hashToInt { hash(it) }

public fun TypeParamBound.PreciseCapture.hash(state: SynHasher) {
    state.write(params)
}

public fun TypeParamBound.PreciseCapture.hash(): Int =
    hashToInt { hash(it) }

public fun TypeParamBound.Trait.hash(state: SynHasher) {
    state.write(parenToken)
    state.write(modifier)
    state.write(lifetimes)
    state.write(path)
}

public fun TypeParamBound.Trait.hash(): Int =
    hashToInt { hash(it) }

public fun UseTree.Glob.hash(state: SynHasher) = Unit

public fun UseTree.Glob.hash(): Int =
    hashToInt { hash(it) }

public fun UseTree.Group.hash(state: SynHasher) {
    state.write(items)
}

public fun UseTree.Group.hash(): Int =
    hashToInt { hash(it) }

public fun UseTree.Name.hash(state: SynHasher) {
    state.write(ident)
    state.write(rename)
}

public fun UseTree.Name.hash(): Int =
    hashToInt { hash(it) }

public fun UseTree.Path.hash(state: SynHasher) {
    state.write(ident)
    state.write(colon2Token)
    state.write(tree)
}

public fun UseTree.Path.hash(): Int =
    hashToInt { hash(it) }

public fun Variadic.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
    state.write(comma)
}

public fun Variadic.hash(): Int =
    hashToInt { hash(it) }

public fun Variant.hash(state: SynHasher) {
    state.write(attrs)
    state.write(ident)
    state.write(fields)
    state.write(discriminant)
}

public fun Variant.hash(): Int =
    hashToInt { hash(it) }

public fun Visibility.Restricted.hash(state: SynHasher) {
    state.write(inToken)
    state.write(path)
}

public fun Visibility.Restricted.hash(): Int =
    hashToInt { hash(it) }

public fun WhereClause.hash(state: SynHasher) {
    state.write(predicates)
}

public fun WhereClause.hash(): Int =
    hashToInt { hash(it) }

public fun WherePredicate.LifetimePredicate.hash(state: SynHasher) {
    state.write(lifetime)
    state.write(bounds)
}

public fun WherePredicate.LifetimePredicate.hash(): Int =
    hashToInt { hash(it) }

public fun WherePredicate.TypePredicate.hash(state: SynHasher) {
    state.write(lifetimes)
    state.write(boundedTy)
    state.write(bounds)
}

public fun WherePredicate.TypePredicate.hash(): Int =
    hashToInt { hash(it) }

public fun Expr.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun ForeignItem.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun ForeignItem.Verbatim.hash(): Int =
    hashToInt { hash(it) }

public fun ImplItem.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun ImplItem.Verbatim.hash(): Int =
    hashToInt { hash(it) }

public fun Item.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun Item.Verbatim.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun Pat.Verbatim.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun SynType.Verbatim.hash(): Int =
    hashToInt { hash(it) }

public fun TraitItem.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun TraitItem.Verbatim.hash(): Int =
    hashToInt { hash(it) }

public fun TypeParamBound.Verbatim.hash(state: SynHasher) {
    state.write(tokens)
}

public fun TypeParamBound.Verbatim.hash(): Int =
    hashToInt { hash(it) }

public fun Lifetime.hash(state: SynHasher) {
    state.write(ident)
}

public fun LitBool.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitBool.hash(): Int =
    hashToInt { hash(it) }

public fun LitByte.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitByte.hash(): Int =
    hashToInt { hash(it) }

public fun LitByteStr.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitByteStr.hash(): Int =
    hashToInt { hash(it) }

public fun LitCStr.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitCStr.hash(): Int =
    hashToInt { hash(it) }

public fun LitChar.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitChar.hash(): Int =
    hashToInt { hash(it) }

public fun LitFloat.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitFloat.hash(): Int =
    hashToInt { hash(it) }

public fun LitInt.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitInt.hash(): Int =
    hashToInt { hash(it) }

public fun LitStr.hash(state: SynHasher) {
    state.write(hashCode())
}

public fun LitStr.hash(): Int =
    hashToInt { hash(it) }

public fun AttrStyle.hash(state: SynHasher) {
    when (this) {
        AttrStyle.Outer -> state.writeU8(0)
        is AttrStyle.Inner -> state.writeU8(1)
    }
}

public fun AttrStyle.hash(): Int =
    hashToInt { hash(it) }

public fun BinOp.hash(state: SynHasher) {
    when (this) {
        is BinOp.Add -> state.writeU8(0)
        is BinOp.Sub -> state.writeU8(1)
        is BinOp.Mul -> state.writeU8(2)
        is BinOp.Div -> state.writeU8(3)
        is BinOp.Rem -> state.writeU8(4)
        is BinOp.And -> state.writeU8(5)
        is BinOp.Or -> state.writeU8(6)
        is BinOp.BitXor -> state.writeU8(7)
        is BinOp.BitAnd -> state.writeU8(8)
        is BinOp.BitOr -> state.writeU8(9)
        is BinOp.Shl -> state.writeU8(10)
        is BinOp.Shr -> state.writeU8(11)
        is BinOp.Eq -> state.writeU8(12)
        is BinOp.Lt -> state.writeU8(13)
        is BinOp.Le -> state.writeU8(14)
        is BinOp.Ne -> state.writeU8(15)
        is BinOp.Ge -> state.writeU8(16)
        is BinOp.Gt -> state.writeU8(17)
        is BinOp.AddAssign -> state.writeU8(18)
        is BinOp.SubAssign -> state.writeU8(19)
        is BinOp.MulAssign -> state.writeU8(20)
        is BinOp.DivAssign -> state.writeU8(21)
        is BinOp.RemAssign -> state.writeU8(22)
        is BinOp.BitXorAssign -> state.writeU8(23)
        is BinOp.BitAndAssign -> state.writeU8(24)
        is BinOp.BitOrAssign -> state.writeU8(25)
        is BinOp.ShlAssign -> state.writeU8(26)
        is BinOp.ShrAssign -> state.writeU8(27)
    }
}

public fun BinOp.hash(): Int =
    hashToInt { hash(it) }

public fun CapturedParam.hash(state: SynHasher) {
    when (this) {
        is CapturedParam.Lifetime -> {
            state.writeU8(0)
            state.write(lifetime)
        }

        is CapturedParam.Ident -> {
            state.writeU8(1)
            state.write(ident)
        }
    }
}

public fun CapturedParam.hash(): Int =
    hashToInt { hash(it) }

public fun Data.hash(state: SynHasher) {
    when (this) {
        is Data.Struct -> {
            state.writeU8(0)
            value.hash(state)
        }

        is Data.Enum -> {
            state.writeU8(1)
            value.hash(state)
        }

        is Data.Union -> {
            state.writeU8(2)
            value.hash(state)
        }
    }
}

public fun Data.hash(): Int =
    hashToInt { hash(it) }

public fun Expr.hash(state: SynHasher) {
    when (this) {
        is Expr.Array -> {
            state.writeU8(0)
            this.hash(state)
        }

        is Expr.Assign -> {
            state.writeU8(1)
            this.hash(state)
        }

        is Expr.Async -> {
            state.writeU8(2)
            this.hash(state)
        }

        is Expr.Await -> {
            state.writeU8(3)
            this.hash(state)
        }

        is Expr.Binary -> {
            state.writeU8(4)
            this.hash(state)
        }

        is Expr.BlockExpr -> {
            state.writeU8(5)
            this.hash(state)
        }

        is Expr.Break -> {
            state.writeU8(6)
            this.hash(state)
        }

        is Expr.Call -> {
            state.writeU8(7)
            this.hash(state)
        }

        is Expr.Cast -> {
            state.writeU8(8)
            this.hash(state)
        }

        is Expr.Closure -> {
            state.writeU8(9)
            this.hash(state)
        }

        is Expr.Const -> {
            state.writeU8(10)
            this.hash(state)
        }

        is Expr.Continue -> {
            state.writeU8(11)
            this.hash(state)
        }

        is Expr.Field -> {
            state.writeU8(12)
            this.hash(state)
        }

        is Expr.ForLoop -> {
            state.writeU8(13)
            this.hash(state)
        }

        is Expr.Group -> {
            state.writeU8(14)
            this.hash(state)
        }

        is Expr.If -> {
            state.writeU8(15)
            this.hash(state)
        }

        is Expr.Index -> {
            state.writeU8(16)
            this.hash(state)
        }

        is Expr.Infer -> {
            state.writeU8(17)
            this.hash(state)
        }

        is Expr.Let -> {
            state.writeU8(18)
            this.hash(state)
        }

        is Expr.Lit -> {
            state.writeU8(19)
            this.hash(state)
        }

        is Expr.Loop -> {
            state.writeU8(20)
            this.hash(state)
        }

        is Expr.Macro -> {
            state.writeU8(21)
            this.hash(state)
        }

        is Expr.Match -> {
            state.writeU8(22)
            this.hash(state)
        }

        is Expr.MethodCall -> {
            state.writeU8(23)
            this.hash(state)
        }

        is Expr.Paren -> {
            state.writeU8(24)
            this.hash(state)
        }

        is Expr.Path -> {
            state.writeU8(25)
            this.hash(state)
        }

        is Expr.Range -> {
            state.writeU8(26)
            this.hash(state)
        }

        is Expr.RawAddr -> {
            state.writeU8(27)
            this.hash(state)
        }

        is Expr.Reference -> {
            state.writeU8(28)
            this.hash(state)
        }

        is Expr.Repeat -> {
            state.writeU8(29)
            this.hash(state)
        }

        is Expr.Return -> {
            state.writeU8(30)
            this.hash(state)
        }

        is Expr.Struct -> {
            state.writeU8(31)
            this.hash(state)
        }

        is Expr.Try -> {
            state.writeU8(32)
            this.hash(state)
        }

        is Expr.TryBlock -> {
            state.writeU8(33)
            this.hash(state)
        }

        is Expr.Tuple -> {
            state.writeU8(34)
            this.hash(state)
        }

        is Expr.Unary -> {
            state.writeU8(35)
            this.hash(state)
        }

        is Expr.Unsafe -> {
            state.writeU8(36)
            this.hash(state)
        }

        is Expr.While -> {
            state.writeU8(37)
            this.hash(state)
        }

        is Expr.Yield -> {
            state.writeU8(38)
            this.hash(state)
        }

        is Expr.Verbatim -> {
            state.writeU8(39)
            this.hash(state)
        }
    }
}

public fun FieldMutability.hash(state: SynHasher) {
    when (this) {
        FieldMutability.None -> state.writeU8(0)
        is FieldMutability.Mut -> state.writeU8(1)
    }
}

public fun FieldMutability.hash(): Int =
    hashToInt { hash(it) }

public fun Fields.hash(state: SynHasher) {
    when (this) {
        is Fields.Named -> {
            state.writeU8(0)
            fields.hash(state)
        }

        is Fields.Unnamed -> {
            state.writeU8(1)
            fields.hash(state)
        }

        Fields.Unit -> {
            state.writeU8(2)
        }
    }
}

public fun Fields.hash(): Int =
    hashToInt { hash(it) }

public fun FnArg.hash(state: SynHasher) {
    when (this) {
        is FnArg.Receiver -> {
            state.writeU8(0)
            this.hash(state)
        }

        is FnArg.Typed -> {
            state.writeU8(1)
            this.hash(state)
        }
    }
}

public fun FnArg.hash(): Int =
    hashToInt { hash(it) }

public fun FnArg.Typed.hash(state: SynHasher) {
    patType.hash(state)
}

public fun FnArg.Typed.hash(): Int =
    hashToInt { hash(it) }

public fun ForeignItem.hash(state: SynHasher) {
    when (this) {
        is ForeignItem.Fn -> {
            state.writeU8(0)
            this.hash(state)
        }

        is ForeignItem.Static -> {
            state.writeU8(1)
            this.hash(state)
        }

        is ForeignItem.ItemType -> {
            state.writeU8(2)
            this.hash(state)
        }

        is ForeignItem.Macro -> {
            state.writeU8(3)
            this.hash(state)
        }

        is ForeignItem.Verbatim -> {
            state.writeU8(4)
            this.hash(state)
        }
    }
}

public fun ForeignItem.hash(): Int =
    hashToInt { hash(it) }

public fun GenericArgument.hash(state: SynHasher) {
    when (this) {
        is GenericArgument.LifetimeArg -> {
            state.writeU8(0)
            lifetime.hash(state)
        }

        is GenericArgument.TypeArg -> {
            state.writeU8(1)
            type.hash(state)
        }

        is GenericArgument.ConstArg -> {
            state.writeU8(2)
            expr.hash(state)
        }

        is GenericArgument.AssocTypeArg -> {
            state.writeU8(3)
            assoc.hash(state)
        }

        is GenericArgument.AssocConstArg -> {
            state.writeU8(4)
            assoc.hash(state)
        }

        is GenericArgument.ConstraintArg -> {
            state.writeU8(5)
            constraint.hash(state)
        }
    }
}

public fun GenericArgument.hash(): Int =
    hashToInt { hash(it) }

public fun GenericParam.hash(state: SynHasher) {
    when (this) {
        is GenericParam.LifetimeParam -> {
            state.writeU8(0)
            this.hash(state)
        }

        is GenericParam.TypeParam -> {
            state.writeU8(1)
            this.hash(state)
        }

        is GenericParam.ConstParam -> {
            state.writeU8(2)
            this.hash(state)
        }
    }
}

public fun GenericParam.hash(): Int =
    hashToInt { hash(it) }

public fun ImplItem.hash(state: SynHasher) {
    when (this) {
        is ImplItem.Const -> {
            state.writeU8(0)
            this.hash(state)
        }

        is ImplItem.Fn -> {
            state.writeU8(1)
            this.hash(state)
        }

        is ImplItem.AssocType -> {
            state.writeU8(2)
            this.hash(state)
        }

        is ImplItem.Macro -> {
            state.writeU8(3)
            this.hash(state)
        }

        is ImplItem.Verbatim -> {
            state.writeU8(4)
            this.hash(state)
        }
    }
}

public fun ImplItem.hash(): Int =
    hashToInt { hash(it) }

public fun ImplRestriction.hash(state: SynHasher) = Unit

public fun ImplRestriction.hash(): Int =
    hashToInt { hash(it) }

public fun Item.hash(state: SynHasher) {
    when (this) {
        is Item.Const -> {
            state.writeU8(0)
            this.hash(state)
        }

        is Item.Enum -> {
            state.writeU8(1)
            this.hash(state)
        }

        is Item.ExternCrate -> {
            state.writeU8(2)
            this.hash(state)
        }

        is Item.Fn -> {
            state.writeU8(3)
            this.hash(state)
        }

        is Item.ForeignMod -> {
            state.writeU8(4)
            this.hash(state)
        }

        is Item.Impl -> {
            state.writeU8(5)
            this.hash(state)
        }

        is Item.Macro -> {
            state.writeU8(6)
            this.hash(state)
        }

        is Item.Mod -> {
            state.writeU8(7)
            this.hash(state)
        }

        is Item.Static -> {
            state.writeU8(8)
            this.hash(state)
        }

        is Item.Struct -> {
            state.writeU8(9)
            this.hash(state)
        }

        is Item.Trait -> {
            state.writeU8(10)
            this.hash(state)
        }

        is Item.TraitAlias -> {
            state.writeU8(11)
            this.hash(state)
        }

        is Item.ItemType -> {
            state.writeU8(12)
            this.hash(state)
        }

        is Item.Union -> {
            state.writeU8(13)
            this.hash(state)
        }

        is Item.Use -> {
            state.writeU8(14)
            this.hash(state)
        }

        is Item.Verbatim -> {
            state.writeU8(15)
            this.hash(state)
        }
    }
}

public fun Item.hash(): Int =
    hashToInt { hash(it) }

public fun Lit.hash(state: SynHasher) {
    when (this) {
        is Lit.Str -> {
            state.writeU8(0)
            value.hash(state)
        }

        is Lit.ByteStr -> {
            state.writeU8(1)
            value.hash(state)
        }

        is Lit.CStr -> {
            state.writeU8(2)
            value.hash(state)
        }

        is Lit.Byte -> {
            state.writeU8(3)
            value.hash(state)
        }

        is Lit.Char -> {
            state.writeU8(4)
            value.hash(state)
        }

        is Lit.Int -> {
            state.writeU8(5)
            value.hash(state)
        }

        is Lit.Float -> {
            state.writeU8(6)
            value.hash(state)
        }

        is Lit.Bool -> {
            state.writeU8(7)
            value.hash(state)
        }

        is Lit.Verbatim -> {
            state.writeU8(8)
            state.write(hashCode())
        }
    }
}

public fun Lit.hash(): Int =
    hashToInt { hash(it) }

public fun MacroDelimiter.hash(state: SynHasher) {
    when (this) {
        is MacroDelimiter.Paren -> state.writeU8(0)
        is MacroDelimiter.Brace -> state.writeU8(1)
        is MacroDelimiter.Bracket -> state.writeU8(2)
    }
}

public fun MacroDelimiter.hash(): Int =
    hashToInt { hash(it) }

public fun Member.hash(state: SynHasher) {
    when (this) {
        is Member.Named -> {
            state.writeU8(0)
            state.write(ident)
        }

        is Member.Unnamed -> {
            state.writeU8(1)
            index.hash(state)
        }
    }
}

public fun Member.hash(): Int =
    hashToInt { hash(it) }

public fun Meta.hash(state: SynHasher) {
    when (this) {
        is Meta.PathMeta -> {
            state.writeU8(0)
            this.hash(state)
        }

        is Meta.List -> {
            state.writeU8(1)
            this.hash(state)
        }

        is Meta.NameValue -> {
            state.writeU8(2)
            this.hash(state)
        }
    }
}

public fun Meta.hash(): Int =
    hashToInt { hash(it) }

public fun Meta.PathMeta.hash(state: SynHasher) {
    path.hash(state)
}

public fun Meta.PathMeta.hash(): Int =
    hashToInt { hash(it) }

public fun ModContent.hash(state: SynHasher) {
    when (this) {
        is ModContent.Unnamed -> {
            state.writeU8(0)
        }

        is ModContent.Inline -> {
            state.writeU8(1)
            state.write(items)
        }
    }
}

public fun ModContent.hash(): Int =
    hashToInt { hash(it) }

public fun Pat.hash(state: SynHasher) {
    when (this) {
        is Pat.Const -> {
            state.writeU8(0)
            this.hash(state)
        }

        is Pat.Ident -> {
            state.writeU8(1)
            this.hash(state)
        }

        is Pat.Lit -> {
            state.writeU8(2)
            this.hash(state)
        }

        is Pat.Macro -> {
            state.writeU8(3)
            this.hash(state)
        }

        is Pat.Or -> {
            state.writeU8(4)
            this.hash(state)
        }

        is Pat.PatParen -> {
            state.writeU8(5)
            this.hash(state)
        }

        is Pat.Path -> {
            state.writeU8(6)
            this.hash(state)
        }

        is Pat.Range -> {
            state.writeU8(7)
            this.hash(state)
        }

        is Pat.Reference -> {
            state.writeU8(8)
            this.hash(state)
        }

        is Pat.Rest -> {
            state.writeU8(9)
            this.hash(state)
        }

        is Pat.Slice -> {
            state.writeU8(10)
            this.hash(state)
        }

        is Pat.Struct -> {
            state.writeU8(11)
            this.hash(state)
        }

        is Pat.Tuple -> {
            state.writeU8(12)
            this.hash(state)
        }

        is Pat.TupleStruct -> {
            state.writeU8(13)
            this.hash(state)
        }

        is Pat.TypeAscription -> {
            state.writeU8(14)
            this.hash(state)
        }

        is Pat.Wild -> {
            state.writeU8(15)
            this.hash(state)
        }

        is Pat.Verbatim -> {
            state.writeU8(16)
            this.hash(state)
        }
    }
}

public fun Pat.hash(): Int =
    hashToInt { hash(it) }

public fun PathArguments.hash(state: SynHasher) {
    when (this) {
        PathArguments.None -> {
            state.writeU8(0)
        }

        is PathArguments.AngleBracketed -> {
            state.writeU8(1)
            this.hash(state)
        }

        is PathArguments.Parenthesized -> {
            state.writeU8(2)
            this.hash(state)
        }
    }
}

public fun PathArguments.hash(): Int =
    hashToInt { hash(it) }

public fun PointerMutability.hash(state: SynHasher) {
    when (this) {
        is PointerMutability.Const -> state.writeU8(0)
        is PointerMutability.Mut -> state.writeU8(1)
    }
}

public fun PointerMutability.hash(): Int =
    hashToInt { hash(it) }

public fun RangeLimits.hash(state: SynHasher) {
    when (this) {
        is RangeLimits.HalfOpen -> state.writeU8(0)
        is RangeLimits.Closed -> state.writeU8(1)
    }
}

public fun RangeLimits.hash(): Int =
    hashToInt { hash(it) }

public fun ReturnType.hash(state: SynHasher) {
    when (this) {
        ReturnType.Default -> {
            state.writeU8(0)
        }

        is ReturnType.TypeReturn -> {
            state.writeU8(1)
            ty.hash(state)
        }
    }
}

public fun ReturnType.hash(): Int =
    hashToInt { hash(it) }

public fun StaticMutability.hash(state: SynHasher) {
    when (this) {
        is StaticMutability.Mut -> state.writeU8(0)
        StaticMutability.None -> state.writeU8(1)
    }
}

public fun StaticMutability.hash(): Int =
    hashToInt { hash(it) }

public fun Stmt.hash(state: SynHasher) {
    when (this) {
        is Stmt.Local -> {
            state.writeU8(0)
            this.hash(state)
        }

        is Stmt.ItemStmt -> {
            state.writeU8(1)
            this.hash(state)
        }

        is Stmt.ExprStmt -> {
            state.writeU8(2)
            this.hash(state)
        }

        is Stmt.MacroStmt -> {
            state.writeU8(3)
            this.hash(state)
        }
    }
}

public fun Stmt.hash(): Int =
    hashToInt { hash(it) }

public fun Stmt.ItemStmt.hash(state: SynHasher) {
    item.hash(state)
}

public fun Stmt.ItemStmt.hash(): Int =
    hashToInt { hash(it) }

public fun Stmt.ExprStmt.hash(state: SynHasher) {
    expr.hash(state)
    state.write(semiToken)
}

public fun Stmt.ExprStmt.hash(): Int =
    hashToInt { hash(it) }

public fun SynType.hash(state: SynHasher) {
    when (this) {
        is SynType.Array -> {
            state.writeU8(0)
            this.hash(state)
        }

        is SynType.BareFn -> {
            state.writeU8(1)
            this.hash(state)
        }

        is SynType.Group -> {
            state.writeU8(2)
            this.hash(state)
        }

        is SynType.ImplTrait -> {
            state.writeU8(3)
            this.hash(state)
        }

        is SynType.Infer -> {
            state.writeU8(4)
            this.hash(state)
        }

        is SynType.Macro -> {
            state.writeU8(5)
            this.hash(state)
        }

        is SynType.Never -> {
            state.writeU8(6)
            this.hash(state)
        }

        is SynType.Paren -> {
            state.writeU8(7)
            this.hash(state)
        }

        is SynType.Path -> {
            state.writeU8(8)
            this.hash(state)
        }

        is SynType.Ptr -> {
            state.writeU8(9)
            this.hash(state)
        }

        is SynType.Reference -> {
            state.writeU8(10)
            this.hash(state)
        }

        is SynType.Slice -> {
            state.writeU8(11)
            this.hash(state)
        }

        is SynType.TraitObject -> {
            state.writeU8(12)
            this.hash(state)
        }

        is SynType.Tuple -> {
            state.writeU8(13)
            this.hash(state)
        }

        is SynType.Verbatim -> {
            state.writeU8(14)
            this.hash(state)
        }
    }
}

public fun SynType.hash(): Int =
    hashToInt { hash(it) }

public fun TraitBoundModifier.hash(state: SynHasher) {
    when (this) {
        TraitBoundModifier.None -> state.writeU8(0)
        is TraitBoundModifier.Maybe -> state.writeU8(1)
    }
}

public fun TraitBoundModifier.hash(): Int =
    hashToInt { hash(it) }

public fun TraitItem.hash(state: SynHasher) {
    when (this) {
        is TraitItem.Const -> {
            state.writeU8(0)
            this.hash(state)
        }

        is TraitItem.Fn -> {
            state.writeU8(1)
            this.hash(state)
        }

        is TraitItem.AssocType -> {
            state.writeU8(2)
            this.hash(state)
        }

        is TraitItem.Macro -> {
            state.writeU8(3)
            this.hash(state)
        }

        is TraitItem.Verbatim -> {
            state.writeU8(4)
            this.hash(state)
        }
    }
}

public fun TraitItem.hash(): Int =
    hashToInt { hash(it) }

public fun TypeParamBound.hash(state: SynHasher) {
    when (this) {
        is TypeParamBound.Trait -> {
            state.writeU8(0)
            this.hash(state)
        }

        is TypeParamBound.LifetimeBound -> {
            state.writeU8(1)
            lifetime.hash(state)
        }

        is TypeParamBound.PreciseCapture -> {
            state.writeU8(2)
            this.hash(state)
        }

        is TypeParamBound.Verbatim -> {
            state.writeU8(3)
            this.hash(state)
        }
    }
}

public fun TypeParamBound.hash(): Int =
    hashToInt { hash(it) }

public fun UnOp.hash(state: SynHasher) {
    when (this) {
        is UnOp.Deref -> state.writeU8(0)
        is UnOp.NotOp -> state.writeU8(1)
        is UnOp.Neg -> state.writeU8(2)
    }
}

public fun UnOp.hash(): Int =
    hashToInt { hash(it) }

public fun UseTree.hash(state: SynHasher) {
    when (this) {
        is UseTree.Path -> {
            state.writeU8(0)
            this.hash(state)
        }

        is UseTree.Name -> {
            state.writeU8(1)
            this.hash(state)
        }

        is UseTree.Group -> {
            state.writeU8(2)
            this.hash(state)
        }

        is UseTree.Glob -> {
            state.writeU8(3)
            this.hash(state)
        }
    }
}

public fun UseTree.hash(): Int =
    hashToInt { hash(it) }

public fun Visibility.hash(state: SynHasher) {
    when (this) {
        is Visibility.Public -> {
            state.writeU8(0)
        }

        is Visibility.Restricted -> {
            state.writeU8(1)
            this.hash(state)
        }

        Visibility.Inherited -> {
            state.writeU8(2)
        }
    }
}

public fun Visibility.hash(): Int =
    hashToInt { hash(it) }

public fun WherePredicate.hash(state: SynHasher) {
    when (this) {
        is WherePredicate.LifetimePredicate -> {
            state.writeU8(0)
            this.hash(state)
        }

        is WherePredicate.TypePredicate -> {
            state.writeU8(1)
            this.hash(state)
        }
    }
}

public fun WherePredicate.hash(): Int =
    hashToInt { hash(it) }
