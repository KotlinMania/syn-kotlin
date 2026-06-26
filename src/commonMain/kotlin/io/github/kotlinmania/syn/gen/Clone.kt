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
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.ImplItem
import io.github.kotlinmania.syn.ImplRestriction
import io.github.kotlinmania.syn.Index
import io.github.kotlinmania.syn.Item
import io.github.kotlinmania.syn.Label
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.MacroDelimiter
import io.github.kotlinmania.syn.Member
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatRest
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.PointerMutability
import io.github.kotlinmania.syn.RangeLimits
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.UnOp
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.WhereClause
import io.github.kotlinmania.syn.WherePredicate
import io.github.kotlinmania.procmacro2.TokenStream

public fun Abi.clone(): Abi =
    Abi(externToken, name?.clone())

public fun PathArguments.AngleBracketed.clone(): PathArguments.AngleBracketed =
    PathArguments.AngleBracketed(colon2Token, ltToken, args.copy({ it.clone() }, { it }), gtToken)

public fun Arm.clone(): Arm =
    Arm(attrs.cloneList(), pat.clone(), guard?.let { it.clone() }, fatArrowToken, body.clone(), comma)

public fun AssocConst.clone(): AssocConst =
    AssocConst(ident.clone(), generics?.clone() as? PathArguments.AngleBracketed?, eqToken, value.clone())

public fun AssocType.clone(): AssocType =
    AssocType(ident.clone(), generics?.clone() as? PathArguments.AngleBracketed?, eqToken, ty.clone())

public fun AttrStyle.clone(): AttrStyle = this

public fun Attribute.clone(): Attribute =
    Attribute(poundToken, style.clone(), bracketToken, meta.clone())

public fun BareFnArg.clone(): BareFnArg =
    BareFnArg(attrs.cloneList(), name?.clone(), ty.clone())

public fun BareVariadic.clone(): BareVariadic =
    BareVariadic(attrs.cloneList(), name?.clone(), dots, comma)

public fun BinOp.clone(): BinOp =
    when (this) {
        is BinOp.Add -> this
        is BinOp.Sub -> this
        is BinOp.Mul -> this
        is BinOp.Div -> this
        is BinOp.Rem -> this
        is BinOp.And -> this
        is BinOp.Or -> this
        is BinOp.BitXor -> this
        is BinOp.BitAnd -> this
        is BinOp.BitOr -> this
        is BinOp.Shl -> this
        is BinOp.Shr -> this
        is BinOp.Eq -> this
        is BinOp.Lt -> this
        is BinOp.Le -> this
        is BinOp.Ne -> this
        is BinOp.Ge -> this
        is BinOp.Gt -> this
        is BinOp.AddAssign -> this
        is BinOp.SubAssign -> this
        is BinOp.MulAssign -> this
        is BinOp.DivAssign -> this
        is BinOp.RemAssign -> this
        is BinOp.BitXorAssign -> this
        is BinOp.BitAndAssign -> this
        is BinOp.BitOrAssign -> this
        is BinOp.ShlAssign -> this
        is BinOp.ShrAssign -> this
    }

public fun Block.clone(): Block =
    Block(braceToken, stmts.cloneList())

public fun BoundLifetimes.clone(): BoundLifetimes =
    BoundLifetimes(forToken, ltToken, lifetimes.copy({ it.clone() }, { it }), gtToken)

public fun CapturedParam.clone(): CapturedParam =
    when (this) {
        is CapturedParam.Lifetime -> CapturedParam.Lifetime(lifetime.clone())
        is CapturedParam.Ident -> CapturedParam.Ident(ident.clone())
    }

public fun GenericParam.ConstParam.clone(): GenericParam.ConstParam =
    GenericParam.ConstParam(attrs.cloneList(), constToken, ident.clone(), colonToken, ty.clone(), eqToken, default?.clone())

public fun Constraint.clone(): Constraint =
    Constraint(ident.clone(), generics?.clone() as? PathArguments.AngleBracketed?, colonToken, bounds.copy({ it.clone() }, { it }))

public fun Data.clone(): Data =
    when (this) {
        is Data.Struct -> Data.Struct(value.clone())
        is Data.Enum -> Data.Enum(value.clone())
        is Data.Union -> Data.Union(value.clone())
    }

public fun DataEnum.clone(): DataEnum =
    DataEnum(enumToken, braceToken, variants.copy({ it.clone() }, { it }))

public fun DataStruct.clone(): DataStruct =
    DataStruct(structToken, fields.clone(), semiToken)

public fun DataUnion.clone(): DataUnion =
    DataUnion(unionToken, fields.clone())

public fun DeriveInput.clone(): DeriveInput =
    DeriveInput(attrs.cloneList(), vis.clone(), ident.clone(), generics.clone(), data.clone())

public fun Expr.clone(): Expr =
    when (this) {
        is Expr.Array -> this.clone()
        is Expr.Assign -> this.clone()
        is Expr.Async -> this.clone()
        is Expr.Await -> this.clone()
        is Expr.Binary -> this.clone()
        is Expr.BlockExpr -> this.clone()
        is Expr.Break -> this.clone()
        is Expr.Call -> this.clone()
        is Expr.Cast -> this.clone()
        is Expr.Closure -> this.clone()
        is Expr.Const -> this.clone()
        is Expr.Continue -> this.clone()
        is Expr.Field -> this.clone()
        is Expr.ForLoop -> this.clone()
        is Expr.Group -> this.clone()
        is Expr.If -> this.clone()
        is Expr.Index -> this.clone()
        is Expr.Infer -> this.clone()
        is Expr.Let -> this.clone()
        is Expr.Lit -> this.clone()
        is Expr.Loop -> this.clone()
        is Expr.Macro -> this.clone()
        is Expr.Match -> this.clone()
        is Expr.MethodCall -> this.clone()
        is Expr.Paren -> this.clone()
        is Expr.Path -> this.clone()
        is Expr.Range -> this.clone()
        is Expr.RawAddr -> this.clone()
        is Expr.Reference -> this.clone()
        is Expr.Repeat -> this.clone()
        is Expr.Return -> this.clone()
        is Expr.Struct -> this.clone()
        is Expr.Try -> this.clone()
        is Expr.TryBlock -> this.clone()
        is Expr.Tuple -> this.clone()
        is Expr.Unary -> this.clone()
        is Expr.Unsafe -> this.clone()
        is Expr.Verbatim -> this.clone()
        is Expr.While -> this.clone()
        is Expr.Yield -> this.clone()
    }

public fun Expr.Array.clone(): Expr.Array =
    Expr.Array(attrs.cloneList(), bracketToken, elems.copy({ it.clone() }, { it }))

public fun Expr.Assign.clone(): Expr.Assign =
    Expr.Assign(attrs.cloneList(), left.clone(), eqToken, right.clone())

public fun Expr.Async.clone(): Expr.Async =
    Expr.Async(attrs.cloneList(), asyncToken, capture, block.clone())

public fun Expr.Await.clone(): Expr.Await =
    Expr.Await(attrs.cloneList(), base.clone(), dotToken, awaitToken)

public fun Expr.Binary.clone(): Expr.Binary =
    Expr.Binary(attrs.cloneList(), left.clone(), op.clone(), right.clone())

public fun Expr.BlockExpr.clone(): Expr.BlockExpr =
    Expr.BlockExpr(attrs.cloneList(), label?.clone(), block.clone())

public fun Expr.Break.clone(): Expr.Break =
    Expr.Break(attrs.cloneList(), breakToken, label?.clone(), expr?.clone())

public fun Expr.Call.clone(): Expr.Call =
    Expr.Call(attrs.cloneList(), func.clone(), parenToken, args.copy({ it.clone() }, { it }))

public fun Expr.Cast.clone(): Expr.Cast =
    Expr.Cast(attrs.cloneList(), expr.clone(), asToken, ty.clone())

public fun Expr.Closure.clone(): Expr.Closure =
    Expr.Closure(attrs.cloneList(), constness, asyncness, capture, or1Token, inputs.copy({ it.clone() }, { it }), or2Token, output.clone(), body.clone())

public fun Expr.Const.clone(): Expr.Const =
    Expr.Const(attrs.cloneList(), constToken, block.clone())

public fun Expr.Continue.clone(): Expr.Continue =
    Expr.Continue(attrs.cloneList(), continueToken, label?.clone())

public fun Expr.Field.clone(): Expr.Field =
    Expr.Field(attrs.cloneList(), base.clone(), dotToken, member.clone())

public fun Expr.ForLoop.clone(): Expr.ForLoop =
    Expr.ForLoop(attrs.cloneList(), label?.clone(), forToken, pat.clone(), inToken, expr.clone(), body.clone())

public fun Expr.Group.clone(): Expr.Group =
    Expr.Group(attrs.cloneList(), groupToken, expr.clone())

public fun Expr.If.clone(): Expr.If =
    Expr.If(attrs.cloneList(), ifToken, cond.clone(), thenBranch.clone(), elseBranch?.let { (it.first.clone() to it.second.clone()) })

public fun Expr.Index.clone(): Expr.Index =
    Expr.Index(attrs.cloneList(), expr.clone(), bracketToken, index.clone())

public fun Expr.Infer.clone(): Expr.Infer =
    Expr.Infer(attrs.cloneList(), underscoreToken)

public fun Expr.Let.clone(): Expr.Let =
    Expr.Let(attrs.cloneList(), letToken, pat.clone(), eqToken, expr.clone())

public fun Expr.Lit.clone(): Expr.Lit =
    Expr.Lit(attrs.cloneList(), lit.clone())

public fun Expr.Loop.clone(): Expr.Loop =
    Expr.Loop(attrs.cloneList(), label?.clone(), loopToken, body.clone())

public fun Expr.Macro.clone(): Expr.Macro =
    Expr.Macro(attrs.cloneList(), mac.clone())

public fun Expr.Match.clone(): Expr.Match =
    Expr.Match(attrs.cloneList(), matchToken, expr.clone(), braceToken, arms.cloneList())

public fun Expr.MethodCall.clone(): Expr.MethodCall =
    Expr.MethodCall(attrs.cloneList(), receiver.clone(), dotToken, method.clone(), turbofish?.clone() as? PathArguments.AngleBracketed?, parenToken, args.copy({ it.clone() }, { it }))

public fun Expr.Paren.clone(): Expr.Paren =
    Expr.Paren(attrs.cloneList(), parenToken, expr.clone())

public fun Expr.Path.clone(): Expr.Path =
    Expr.Path(attrs.cloneList(), qself?.clone(), path.clone())

public fun Expr.Range.clone(): Expr.Range =
    Expr.Range(attrs.cloneList(), start?.clone(), limits.clone(), end?.clone())

public fun Expr.RawAddr.clone(): Expr.RawAddr =
    Expr.RawAddr(attrs.cloneList(), andToken, raw, mutability.clone(), expr.clone())

public fun Expr.Reference.clone(): Expr.Reference =
    Expr.Reference(attrs.cloneList(), andToken, mutability, expr.clone())

public fun Expr.Repeat.clone(): Expr.Repeat =
    Expr.Repeat(attrs.cloneList(), bracketToken, expr.clone(), semiToken, len.clone())

public fun Expr.Return.clone(): Expr.Return =
    Expr.Return(attrs.cloneList(), returnToken, expr?.clone())

public fun Expr.Struct.clone(): Expr.Struct =
    Expr.Struct(attrs.cloneList(), qself?.clone(), path.clone(), braceToken, fields.copy({ it.clone() }, { it }), dot2Token, rest?.clone())

public fun Expr.Try.clone(): Expr.Try =
    Expr.Try(attrs.cloneList(), expr.clone(), questionToken)

public fun Expr.TryBlock.clone(): Expr.TryBlock =
    Expr.TryBlock(attrs.cloneList(), tryToken, block.clone())

public fun Expr.Tuple.clone(): Expr.Tuple =
    Expr.Tuple(attrs.cloneList(), parenToken, elems.copy({ it.clone() }, { it }))

public fun Expr.Unary.clone(): Expr.Unary =
    Expr.Unary(attrs.cloneList(), op.clone(), expr.clone())

public fun Expr.Unsafe.clone(): Expr.Unsafe =
    Expr.Unsafe(attrs.cloneList(), unsafeToken, block.clone())

public fun Expr.Verbatim.clone(): Expr.Verbatim =
    Expr.Verbatim(tokens.clone())

public fun Expr.While.clone(): Expr.While =
    Expr.While(attrs.cloneList(), label?.clone(), whileToken, cond.clone(), body.clone())

public fun Expr.Yield.clone(): Expr.Yield =
    Expr.Yield(attrs.cloneList(), yieldToken, expr?.clone())

public fun Field.clone(): Field =
    Field(attrs.cloneList(), vis.clone(), mutability.clone(), ident?.clone(), colonToken, ty.clone())

public fun FieldMutability.clone(): FieldMutability = this

public fun FieldPat.clone(): FieldPat =
    FieldPat(member.clone(), colonToken, pat.clone(), attrs.cloneList())

public fun FieldValue.clone(): FieldValue =
    FieldValue(attrs.cloneList(), member.clone(), colonToken?.clone(), expr.clone())

public fun Fields.clone(): Fields =
    when (this) {
        is Fields.Named -> Fields.Named(this.clone())
        is Fields.Unnamed -> Fields.Unnamed(this.clone())
        is Fields.Unit -> Fields.Unit
    }

public fun FieldsNamed.clone(): FieldsNamed =
    FieldsNamed(braceToken, named.copy({ it.clone() }, { it }))

public fun FieldsUnnamed.clone(): FieldsUnnamed =
    FieldsUnnamed(parenToken, unnamed.copy({ it.clone() }, { it }))

public fun File.clone(): File =
    File(shebang, attrs.cloneList(), items.cloneList())

public fun FnArg.clone(): FnArg =
    when (this) {
        is FnArg.Receiver -> this.clone()
        is FnArg.Typed -> this.clone()
    }

public fun FnArg.Receiver.clone(): FnArg.Receiver =
    FnArg.Receiver(attrs.cloneList(), this.receiver, andToken, orToken, selfRef, mutability, shorthand)

public fun FnArg.Typed.clone(): FnArg.Typed =
    FnArg.Typed(attrs.cloneList(), pat.clone(), colonToken, ty.clone())

public fun ForeignItem.clone(): ForeignItem =
    when (this) {
        is ForeignItem.Fn -> this.clone()
        is ForeignItem.Static -> this.clone()
        is ForeignItem.Type -> this.clone()
        is ForeignItem.Macro -> this.clone()
        is ForeignItem.Verbatim -> this.clone()
    }

public fun ForeignItem.Fn.clone(): ForeignItem.Fn =
    ForeignItem.Fn(attrs.cloneList(), vis.clone(), sig.clone(), semiToken)

public fun ForeignItem.Macro.clone(): ForeignItem.Macro =
    ForeignItem.Macro(attrs.cloneList(), mac.clone(), semiToken)

public fun ForeignItem.Static.clone(): ForeignItem.Static =
    ForeignItem.Static(attrs.cloneList(), vis.clone(), staticToken, mutability, ident.clone(), colonToken, ty.clone(), semiToken)

public fun ForeignItem.Type.clone(): ForeignItem.Type =
    ForeignItem.Type(attrs.cloneList(), vis.clone(), typeToken, ident.clone(), generics.clone(), semiToken)

public fun ForeignItem.Verbatim.clone(): ForeignItem.Verbatim =
    ForeignItem.Verbatim(attrs.cloneList(), tokens.clone())

public fun GenericArgument.clone(): GenericArgument =
    when (this) {
        is GenericArgument.LifetimeArg -> GenericArgument.LifetimeArg(lifetime.clone())
        is GenericArgument.TypeArg -> GenericArgument.TypeArg(type.clone())
        is GenericArgument.ConstArg -> GenericArgument.ConstArg(expr.clone())
        is GenericArgument.AssocTypeArg -> GenericArgument.AssocTypeArg(assoc.clone())
        is GenericArgument.AssocConstArg -> GenericArgument.AssocConstArg(assoc.clone())
        is GenericArgument.ConstraintArg -> GenericArgument.ConstraintArg(constraint.clone())
    }

public fun GenericParam.clone(): GenericParam =
    when (this) {
        is GenericParam.LifetimeParam -> GenericParam.LifetimeParam(this.clone())
        is GenericParam.TypeParam -> GenericParam.TypeParam(this.clone())
        is GenericParam.ConstParam -> GenericParam.ConstParam(this.clone())
    }

public fun GenericParam.LifetimeParam.clone(): GenericParam.LifetimeParam =
    GenericParam.LifetimeParam(attrs.cloneList(), lifetime.clone(), colonToken, bounds.copy({ it.clone() }, { it }))

public fun GenericParam.TypeParam.clone(): GenericParam.TypeParam =
    GenericParam.TypeParam(attrs.cloneList(), ident.clone(), colonToken, bounds.copy({ it.clone() }, { it }), eqToken, default?.clone())

public fun GenericParam.ConstParam.clone(): GenericParam.ConstParam =
    GenericParam.ConstParam(attrs.cloneList(), constToken, ident.clone(), colonToken, ty.clone(), eqToken, default?.clone())

public fun Generics.clone(): Generics =
    Generics(ltToken, params.copy({ it.clone() }, { it }), gtToken, whereClause?.clone())

public fun ImplItem.clone(): ImplItem =
    when (this) {
        is ImplItem.Const -> this.clone()
        is ImplItem.Fn -> this.clone()
        is ImplItem.Type -> this.clone()
        is ImplItem.Macro -> this.clone()
        is ImplItem.Verbatim -> this.clone()
    }

public fun ImplItem.Const.clone(): ImplItem.Const =
    ImplItem.Const(attrs.cloneList(), vis.clone(), defaultness, constToken, ident.clone(), generics.clone(), colonToken, ty.clone(), eqToken, expr.clone(), semiToken)

public fun ImplItem.Fn.clone(): ImplItem.Fn =
    ImplItem.Fn(attrs.cloneList(), vis.clone(), defaultness, sig.clone(), block.clone())

public fun ImplItem.Macro.clone(): ImplItem.Macro =
    ImplItem.Macro(attrs.cloneList(), mac.clone(), semiToken)

public fun ImplItem.Type.clone(): ImplItem.Type =
    ImplItem.Type(attrs.cloneList(), vis.clone(), defaultness, typeToken, ident.clone(), generics.clone(), eqToken, ty.clone(), semiToken)

public fun ImplItem.Verbatim.clone(): ImplItem.Verbatim =
    ImplItem.Verbatim(attrs.cloneList(), tokens.clone())

public fun ImplRestriction.clone(): ImplRestriction = this

public fun Index.clone(): Index =
    Index(index, span)

public fun Item.clone(): Item =
    when (this) {
        is Item.Const -> this.clone()
        is Item.Enum -> this.clone()
        is Item.ExternCrate -> this.clone()
        is Item.Fn -> this.clone()
        is Item.ForeignMod -> this.clone()
        is Item.Impl -> this.clone()
        is Item.Macro -> this.clone()
        is Item.Mod -> this.clone()
        is Item.Static -> this.clone()
        is Item.Struct -> this.clone()
        is Item.Trait -> this.clone()
        is Item.TraitAlias -> this.clone()
        is Item.Type -> this.clone()
        is Item.Union -> this.clone()
        is Item.Use -> this.clone()
        is Item.Verbatim -> this.clone()
    }

public fun Item.Const.clone(): Item.Const =
    Item.Const(attrs.cloneList(), vis.clone(), constToken, ident.clone(), colonToken, ty.clone(), eqToken, expr.clone(), semiToken)

public fun Item.Enum.clone(): Item.Enum =
    Item.Enum(attrs.cloneList(), vis.clone(), enumToken, ident.clone(), generics.clone(), braceToken, variants.copy({ it.clone() }, { it }), semiToken)

public fun Item.ExternCrate.clone(): Item.ExternCrate =
    Item.ExternCrate(attrs.cloneList(), vis.clone(), externCrateToken, ident.clone(), asToken, rename?.clone(), semiToken)

public fun Item.Fn.clone(): Item.Fn =
    Item.Fn(attrs.cloneList(), vis.clone(), defaultness, sig.clone(), block)

public fun Item.ForeignMod.clone(): Item.ForeignMod =
    Item.ForeignMod(attrs.cloneList(), vis.clone(), abi.clone(), braceToken, items.cloneList())

public fun Item.Impl.clone(): Item.Impl =
    Item.Impl(attrs.cloneList(), defaultness, implToken, generics.clone(), trait?.clone(), forToken, selfTy.clone(), items.copy({ it.clone() }, { it }), braceToken)

public fun Item.Macro.clone(): Item.Macro =
    Item.Macro(attrs.cloneList(), mac.clone(), ident?.clone(), semiToken)

public fun Item.Mod.clone(): Item.Mod =
    when (this) {
        is Item.Mod.Loaded -> Item.Mod.Loaded(attrs.cloneList(), vis.clone(), modToken, ident.clone(), content)
        is Item.Mod.Unloaded -> Item.Mod.Unloaded(attrs.cloneList(), vis.clone(), modToken, ident.clone(), semiToken)
    }

public fun Item.Static.clone(): Item.Static =
    Item.Static(attrs.cloneList(), vis.clone(), staticToken, mutability, ident.clone(), colonToken, ty.clone(), eqToken, expr.clone(), semiToken)

public fun Item.Struct.clone(): Item.Struct =
    Item.Struct(attrs.cloneList(), vis.clone(), structToken, ident.clone(), generics.clone(), fields.clone(), semiToken)

public fun Item.Trait.clone(): Item.Trait =
    Item.Trait(attrs.cloneList(), vis.clone(), traitToken, ident.clone(), generics.clone(), colonToken, supertraits.copy({ it.clone() }, { it }), braceToken, items.copy({ it.clone() }, { it }))

public fun Item.TraitAlias.clone(): Item.TraitAlias =
    Item.TraitAlias(attrs.cloneList(), vis.clone(), traitToken, ident.clone(), generics.clone(), eqToken, bounds.copy({ it.clone() }, { it }), semiToken)

public fun Item.Type.clone(): Item.Type =
    Item.Type(attrs.cloneList(), vis.clone(), typeToken, ident.clone(), generics.clone(), eqToken, ty.clone(), semiToken)

public fun Item.Union.clone(): Item.Union =
    Item.Union(attrs.cloneList(), vis.clone(), unionToken, ident.clone(), generics.clone(), fields.clone())

public fun Item.Use.clone(): Item.Use =
    Item.Use(attrs.cloneList(), vis.clone(), useToken, tree.clone(), semiToken)

public fun Item.Verbatim.clone(): Item.Verbatim =
    Item.Verbatim(attrs.cloneList(), tokens.clone())

public fun Label.clone(): Label =
    Label(name.clone(), colonToken)

public fun Lifetime.clone(): Lifetime =
    Lifetime(toToken(), apostrophe)

public fun GenericParam.LifetimeParam.clone(): GenericParam.LifetimeParam =
    GenericParam.LifetimeParam(attrs.cloneList(), lifetime.clone(), colonToken, bounds.copy({ it.clone() }, { it }))

public fun Lit.clone(): Lit =
    when (this) {
        is Lit.Str -> Lit.Str(value.clone(), suffix?.clone())
        is Lit.ByteStr -> Lit.ByteStr(value.clone(), suffix?.clone())
        is Lit.CStr -> Lit.CStr(value.clone(), suffix?.clone())
        is Lit.Byte -> Lit.Byte(value.clone(), suffix?.clone())
        is Lit.Char -> Lit.Char(value.clone(), suffix?.clone())
        is Lit.Int -> Lit.Int(value.clone(), suffix?.clone())
        is Lit.Float -> Lit.Float(value.clone(), suffix?.clone())
        is Lit.Bool -> Lit.Bool(value.clone())
        is Lit.CStrRaw -> Lit.CStrRaw(value, suffix)
        is Lit.ByteStrRaw -> Lit.ByteStrRaw(value, suffix)
        is Lit.StrRaw -> Lit.StrRaw(value, suffix)
    }

public fun Macro.clone(): Macro =
    Macro(path.clone(), bangToken, delimiter.clone(), tokens.clone())

public fun MacroDelimiter.clone(): MacroDelimiter =
    when (this) {
        is MacroDelimiter.Paren -> this
        is MacroDelimiter.Brace -> this
        is MacroDelimiter.Bracket -> this
    }

public fun Member.clone(): Member =
    when (this) {
        is Member.Named -> Member.Named(ident.clone())
        is Member.Unnamed -> Member.Unnamed(index.clone())
    }

public fun Meta.clone(): Meta =
    when (this) {
        is Meta.Path -> Meta.Path(path.clone())
        is Meta.List -> Meta.List(path.clone(), parenToken, tokens.clone())
        is Meta.NameValue -> Meta.NameValue(path.clone(), eqToken, value.clone())
    }

public fun Pat.clone(): Pat =
    when (this) {
        is Pat.Ident -> this.clone()
        is Pat.Tuple -> this.clone()
        is Pat.Or -> this.clone()
        is Pat.PatParen -> this.clone()
        is Pat.Reference -> this.clone()
        is Pat.Struct -> this.clone()
        is Pat.Slice -> this.clone()
        is Pat.TypeAscription -> this.clone()
        is Pat.Const -> this.clone()
        is Pat.Lit -> this.clone()
        is Pat.Macro -> this.clone()
        is Pat.Path -> this.clone()
        is Pat.Range -> this.clone()
        is Pat.Rest -> this.clone()
        is Pat.TupleStruct -> this.clone()
        is Pat.Wild -> this.clone()
        is Pat.Verbatim -> this.clone()
    }

public fun Pat.Ident.clone(): Pat.Ident =
    Pat.Ident(attrs.cloneList(), ident.clone(), atToken, subpat?.clone())

public fun Pat.Tuple.clone(): Pat.Tuple =
    Pat.Tuple(attrs.cloneList(), parenToken, elems.copy({ it.clone() }, { it }))

public fun Pat.Or.clone(): Pat.Or =
    Pat.Or(attrs.cloneList(), leadingVert, cases.copy({ it.clone() }, { it }))

public fun Pat.PatParen.clone(): Pat.PatParen =
    Pat.PatParen(attrs.cloneList(), parenToken, pat.clone())

public fun Pat.Reference.clone(): Pat.Reference =
    Pat.Reference(attrs.cloneList(), andToken, mutability, pat.clone())

public fun Pat.Struct.clone(): Pat.Struct =
    Pat.Struct(attrs.cloneList(), qself?.clone(), path.clone(), braceToken, fields.copy({ it.clone() }, { it }), dot2Token, rest?.clone())

public fun Pat.Slice.clone(): Pat.Slice =
    Pat.Slice(attrs.cloneList(), bracketToken, elems.copy({ it.clone() }, { it }))

public fun Pat.TypeAscription.clone(): Pat.TypeAscription =
    Pat.TypeAscription(attrs.cloneList(), pat.clone(), colonToken, ty.clone())

public fun Pat.Const.clone(): Pat.Const =
    Pat.Const(attrs.cloneList(), constToken, block.clone())

public fun Pat.Lit.clone(): Pat.Lit =
    Pat.Lit(attrs.cloneList(), expr.clone())

public fun Pat.Macro.clone(): Pat.Macro =
    Pat.Macro(attrs.cloneList(), mac.clone())

public fun Pat.Path.clone(): Pat.Path =
    Pat.Path(attrs.cloneList(), qself?.clone(), path.clone())

public fun Pat.Range.clone(): Pat.Range =
    Pat.Range(attrs.cloneList(), start.clone(), limits.clone(), end?.clone())

public fun Pat.Rest.clone(): Pat.Rest =
    Pat.Rest(dot2Token, attrs.cloneList())

public fun Pat.TupleStruct.clone(): Pat.TupleStruct =
    Pat.TupleStruct(attrs.cloneList(), qself?.clone(), path.clone(), parenToken, elems.copy({ it.clone() }, { it }))

public fun Pat.Wild.clone(): Pat.Wild =
    Pat.Wild(attrs.cloneList(), underscoreToken)

public fun Pat.Verbatim.clone(): Pat.Verbatim =
    Pat.Verbatim(attrs.cloneList(), tokens.clone())

public fun PatRest.clone(): PatRest =
    PatRest(dot2Token, attrs.cloneList())

public fun PatType.clone(): PatType =
    PatType(attrs.cloneList(), pat.clone(), colonToken, ty.clone())

public fun Path.clone(): Path =
    Path(leadingColon, segments.copy({ it.clone() }, { it }))

public fun PathArguments.clone(): PathArguments =
    when (this) {
        is PathArguments.None -> PathArguments.None
        is PathArguments.AngleBracketed -> this.clone()
        is PathArguments.Parenthesized -> this.clone()
    }

public fun PathArguments.Parenthesized.clone(): PathArguments.Parenthesized =
    PathArguments.Parenthesized(parenToken, inputs.copy({ it.clone() }, { it }), output.clone())

public fun PathSegment.clone(): PathSegment =
    PathSegment(ident.clone(), arguments.clone())

public fun PointerMutability.clone(): PointerMutability =
    when (this) {
        is PointerMutability.Const -> this
        is PointerMutability.Mut -> this
    }

public fun RangeLimits.clone(): RangeLimits =
    when (this) {
        is RangeLimits.HalfOpen -> this
        is RangeLimits.Closed -> this
    }

public fun FnArg.Receiver.clone(): FnArg.Receiver =
    FnArg.Receiver(attrs.cloneList(), this.receiver, andToken, orToken, selfRef, mutability, shorthand)

public fun ReturnType.clone(): ReturnType =
    when (this) {
        is ReturnType.Default -> ReturnType.Default
        is ReturnType.Type -> ReturnType.Type(arrowToken, ty.clone())
    }

public fun Signature.clone(): Signature =
    Signature(constness, asyncness, unsafety, abi?.clone(), fnToken, ident.clone(), generics.clone(), parenToken, inputs.copy({ it.clone() }, { it }), variadic?.clone(), output.clone())

public fun Stmt.clone(): Stmt =
    when (this) {
        is Stmt.Local -> this.clone()
        is Stmt.ItemStmt -> this.clone()
        is Stmt.ExprStmt -> this.clone()
        is Stmt.MacroStmt -> this.clone()
    }

public fun Stmt.Local.clone(): Stmt.Local =
    Stmt.Local(attrs.cloneList(), letToken, pat.clone(), init?.let { (it.first.clone() to it.second.clone()) }, semiToken)

public fun Stmt.ItemStmt.clone(): Stmt.ItemStmt =
    Stmt.ItemStmt(item.clone(), semiToken)

public fun Stmt.ExprStmt.clone(): Stmt.ExprStmt =
    Stmt.ExprStmt(attrs.cloneList(), expr.clone(), semiToken)

public fun Stmt.MacroStmt.clone(): Stmt.MacroStmt =
    Stmt.MacroStmt(attrs.cloneList(), mac.clone(), semiToken)

public fun SynType.clone(): SynType =
    when (this) {
        is SynType.Array -> this.clone()
        is SynType.BareFn -> this.clone()
        is SynType.Group -> this.clone()
        is SynType.ImplTrait -> this.clone()
        is SynType.Infer -> this.clone()
        is SynType.Macro -> this.clone()
        is SynType.Never -> this
        is SynType.Paren -> this.clone()
        is SynType.Path -> this.clone()
        is SynType.Ptr -> this.clone()
        is SynType.Reference -> this.clone()
        is SynType.Slice -> this.clone()
        is SynType.TraitObject -> this.clone()
        is SynType.Tuple -> this.clone()
        is SynType.Verbatim -> this.clone()
    }

public fun SynType.Array.clone(): SynType.Array =
    SynType.Array(bracketToken, elem.clone(), semiToken, len.clone())

public fun SynType.BareFn.clone(): SynType.BareFn =
    SynType.BareFn(lifetimes?.clone(), forToken, unsafety, abi?.clone(), fnToken, inputs.copy({ it.clone() }, { it }), variadic?.clone(), output.clone())

public fun SynType.Group.clone(): SynType.Group =
    SynType.Group(groupToken, elem.clone())

public fun SynType.ImplTrait.clone(): SynType.ImplTrait =
    SynType.ImplTrait(implToken, bounds.copy({ it.clone() }, { it }))

public fun SynType.Infer.clone(): SynType.Infer =
    SynType.Infer(underscoreToken)

public fun SynType.Macro.clone(): SynType.Macro =
    SynType.Macro(mac.clone())

public fun SynType.Paren.clone(): SynType.Paren =
    SynType.Paren(parenToken, elem.clone())

public fun SynType.Path.clone(): SynType.Path =
    SynType.Path(qself?.clone(), path.clone())

public fun SynType.Ptr.clone(): SynType.Ptr =
    SynType.Ptr(starToken, mutability.clone())

public fun SynType.Reference.clone(): SynType.Reference =
    SynType.Reference(andToken, lifetime?.clone(), mutability, elem.clone())

public fun SynType.Slice.clone(): SynType.Slice =
    SynType.Slice(bracketToken, elem.clone())

public fun SynType.TraitObject.clone(): SynType.TraitObject =
    SynType.TraitObject(dynToken, bounds.copy({ it.clone() }, { it }))

public fun SynType.Tuple.clone(): SynType.Tuple =
    SynType.Tuple(parenToken, elems.copy({ it.clone() }, { it }))

public fun SynType.Verbatim.clone(): SynType.Verbatim =
    SynType.Verbatim(tokens.clone())

public fun GenericParam.TypeParam.clone(): GenericParam.TypeParam =
    GenericParam.TypeParam(attrs.cloneList(), ident.clone(), colonToken, bounds.copy({ it.clone() }, { it }), eqToken, default?.clone())

public fun TypeParamBound.clone(): TypeParamBound =
    when (this) {
        is TypeParamBound.Trait -> this.clone()
        is TypeParamBound.Lifetime -> TypeParamBound.Lifetime(lifetime.clone())
    }

public fun TypeParamBound.Trait.clone(): TypeParamBound.Trait =
    TypeParamBound.Trait(path.clone(), parenToken)

public fun UnOp.clone(): UnOp =
    when (this) {
        is UnOp.Deref -> this
        is UnOp.Not -> this
        is UnOp.Neg -> this
    }

public fun Variant.clone(): Variant =
    Variant(attrs.cloneList(), ident.clone(), fields.clone(), discriminant?.let { it.copy(expr = it.expr.clone()) })

public fun Visibility.clone(): Visibility =
    when (this) {
        is Visibility.Public -> this
        is Visibility.Restricted -> Visibility.Restricted(pubToken, parenToken, path.clone())
        is Visibility.Inherited -> Visibility.Inherited
    }

public fun WhereClause.clone(): WhereClause =
    WhereClause(whereToken, predicates.copy({ it.clone() }, { it }))

public fun WherePredicate.clone(): WherePredicate =
    when (this) {
        is WherePredicate.TypePredicate -> WherePredicate.TypePredicate(lifetimes?.clone(), boundedTy.clone(), colonToken, bounds.copy({ it.clone() }, { it }))
        is WherePredicate.LifetimePredicate -> WherePredicate.LifetimePredicate(lifetime.clone(), colonToken, bounds.copy({ it.clone() }, { it }))
    }

internal fun <T> MutableList<T>.cloneList(): MutableList<T> =
    mapTo(mutableListOf()) { it }