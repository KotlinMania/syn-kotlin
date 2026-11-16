// port-lint: source gen/clone.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.AndLifetime
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.AsIdent
import io.github.kotlinmania.syn.AssocConst
import io.github.kotlinmania.syn.AssocType
import io.github.kotlinmania.syn.AttrStyle
import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.BinOp
import io.github.kotlinmania.syn.Block
import io.github.kotlinmania.syn.BoundLifetimes
import io.github.kotlinmania.syn.CapturedParam
import io.github.kotlinmania.syn.ConstModifiers
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
import io.github.kotlinmania.syn.FieldPat
import io.github.kotlinmania.syn.FieldValue
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
import io.github.kotlinmania.syn.File
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.FnModifiers
import io.github.kotlinmania.syn.FnPtrVariadic
import io.github.kotlinmania.syn.ForeignItem
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.IdentColon
import io.github.kotlinmania.syn.IfExpr
import io.github.kotlinmania.syn.ImplItem
import io.github.kotlinmania.syn.ImplModifiers
import io.github.kotlinmania.syn.Index
import io.github.kotlinmania.syn.Item
import io.github.kotlinmania.syn.Label
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.LocalInit
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.MacroDelimiter
import io.github.kotlinmania.syn.Member
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.ModContent
import io.github.kotlinmania.syn.NamedArg
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatColon
import io.github.kotlinmania.syn.PatRest
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathForPair
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.PointerMutability
import io.github.kotlinmania.syn.QSelf
import io.github.kotlinmania.syn.RangeLimits
import io.github.kotlinmania.syn.ReceiverKind
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Safety
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.StaticMutability
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.TraitItem
import io.github.kotlinmania.syn.TraitModifiers
import io.github.kotlinmania.syn.TypeModifiers
import io.github.kotlinmania.syn.TypeParamBound
import io.github.kotlinmania.syn.UnOp
import io.github.kotlinmania.syn.UseTree
import io.github.kotlinmania.syn.Variadic
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.WhereClause
import io.github.kotlinmania.syn.WhereClausePlacement
import io.github.kotlinmania.syn.WherePredicate
import io.github.kotlinmania.syn.cloneLiteral
import io.github.kotlinmania.syn.copy

public fun Abi.clone(): Abi =
    Abi(externToken, name?.clone())

public fun QSelf.clone(): QSelf =
    QSelf(ltToken, ty.clone(), position, asToken, gtToken)

public fun PathArguments.AngleBracketed.clone(): PathArguments.AngleBracketed =
    PathArguments.AngleBracketed(colon2Token, ltToken, args.copy({ it.clone() }, { it }), gtToken)

public fun Arm.clone(): Arm =
    Arm(attrs.cloneAttrs(), pat.clone(), fatArrowToken, body.clone(), comma)

public fun AssocConst.clone(): AssocConst =
    AssocConst(ident.copy(), generics?.let { it.clone() }, eqToken, value.clone())

public fun AssocType.clone(): AssocType =
    AssocType(ident.copy(), generics?.let { it.clone() }, eqToken, ty.clone())

public fun AttrStyle.clone(): AttrStyle = this

public fun Attribute.clone(): Attribute =
    Attribute(poundToken, style.clone(), bracketToken, meta.clone())

public fun NamedArg.clone(): NamedArg =
    NamedArg(attrs.cloneAttrs(), name?.clone(), ty.clone())

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
    Block(braceToken, stmts.cloneStmts())

public fun BoundLifetimes.clone(): BoundLifetimes =
    BoundLifetimes(forToken, ltToken, lifetimes.copy({ it.clone() }, { it }), gtToken)

public fun CapturedParam.clone(): CapturedParam =
    when (this) {
        is CapturedParam.Lifetime -> CapturedParam.Lifetime(lifetime.clone())
        is CapturedParam.Ident -> CapturedParam.Ident(ident.copy())
    }

public fun ConstModifiers.clone(): ConstModifiers =
    ConstModifiers(defaultness)

public fun Constraint.clone(): Constraint =
    Constraint(ident.copy(), generics?.let { it.clone() }, colonToken, bounds.copy({ it.clone() }, { it }))

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
    DeriveInput(attrs.cloneAttrs(), vis.clone(), ident.copy(), generics.clone(), data.clone())

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
    Expr.Array(attrs.cloneAttrs(), bracketToken, elems.copy({ it.clone() }, { it }))

public fun Expr.Assign.clone(): Expr.Assign =
    Expr.Assign(attrs.cloneAttrs(), left.clone(), eqToken, right.clone())

public fun Expr.Async.clone(): Expr.Async =
    Expr.Async(attrs.cloneAttrs(), asyncToken, capture, modifiers.clone(), block.clone())

public fun Expr.Await.clone(): Expr.Await =
    Expr.Await(attrs.cloneAttrs(), base.clone(), dotToken, awaitToken)

public fun Expr.Binary.clone(): Expr.Binary =
    Expr.Binary(attrs.cloneAttrs(), left.clone(), op.clone(), right.clone())

public fun Expr.BlockExpr.clone(): Expr.BlockExpr =
    Expr.BlockExpr(attrs.cloneAttrs(), label?.clone(), block.clone())

public fun Expr.Break.clone(): Expr.Break =
    Expr.Break(attrs.cloneAttrs(), breakToken, label?.clone(), expr?.clone())

public fun Expr.Call.clone(): Expr.Call =
    Expr.Call(attrs.cloneAttrs(), func.clone(), parenToken, args.copy({ it.clone() }, { it }))

public fun Expr.Cast.clone(): Expr.Cast =
    Expr.Cast(attrs.cloneAttrs(), expr.clone(), asToken, ty.clone())

public fun Expr.Closure.clone(): Expr.Closure =
    Expr.Closure(
        attrs.cloneAttrs(),
        lifetimes?.clone(),
        modifiers.clone(),
        constness,
        asyncness,
        capture,
        inputsBegin,
        inputs.copy({ it.clone() }, { it }),
        inputsEnd,
        output.clone(),
        body.clone(),
    )

public fun Expr.Const.clone(): Expr.Const =
    Expr.Const(attrs.cloneAttrs(), constToken, modifiers.clone(), block.clone())

public fun Expr.Continue.clone(): Expr.Continue =
    Expr.Continue(attrs.cloneAttrs(), continueToken, label?.clone())

public fun Expr.Field.clone(): Expr.Field =
    Expr.Field(attrs.cloneAttrs(), base.clone(), dotToken, member.clone())

public fun Expr.ForLoop.clone(): Expr.ForLoop =
    Expr.ForLoop(attrs.cloneAttrs(), label?.clone(), forToken, pat.clone(), inToken, expr.clone(), body.clone())

public fun Expr.Group.clone(): Expr.Group =
    Expr.Group(attrs.cloneAttrs(), groupToken, expr.clone())

public fun Expr.If.clone(): Expr.If =
    Expr.If(attrs.cloneAttrs(), ifToken, cond.clone(), thenBranch.clone(), elseBranch?.clone())

public fun Expr.Index.clone(): Expr.Index =
    Expr.Index(attrs.cloneAttrs(), expr.clone(), bracketToken, index.clone())

public fun Expr.Infer.clone(): Expr.Infer =
    Expr.Infer(attrs.cloneAttrs(), underscoreToken)

public fun Expr.Let.clone(): Expr.Let =
    Expr.Let(attrs.cloneAttrs(), letToken, pat.clone(), eqToken, expr.clone())

public fun Expr.Lit.clone(): Expr.Lit =
    Expr.Lit(attrs.cloneAttrs(), lit.clone())

public fun Expr.Loop.clone(): Expr.Loop =
    Expr.Loop(attrs.cloneAttrs(), label?.clone(), loopToken, body.clone())

public fun Expr.Macro.clone(): Expr.Macro =
    Expr.Macro(attrs.cloneAttrs(), mac.clone())

public fun Expr.Match.clone(): Expr.Match =
    Expr.Match(attrs.cloneAttrs(), matchToken, expr.clone(), braceToken, arms.cloneArms())

public fun Expr.MethodCall.clone(): Expr.MethodCall =
    Expr.MethodCall(attrs.cloneAttrs(), receiver.clone(), dotToken, method.copy(), turbofish?.let { it.clone() }, parenToken, args.copy({ it.clone() }, { it }))

public fun Expr.Paren.clone(): Expr.Paren =
    Expr.Paren(attrs.cloneAttrs(), parenToken, expr.clone())

public fun Expr.Path.clone(): Expr.Path =
    Expr.Path(attrs.cloneAttrs(), qself?.clone(), path.clone())

public fun Expr.Range.clone(): Expr.Range =
    Expr.Range(attrs.cloneAttrs(), start?.clone(), limits.clone(), end?.clone())

public fun Expr.RawAddr.clone(): Expr.RawAddr =
    Expr.RawAddr(attrs.cloneAttrs(), andToken, raw, mutability.clone(), expr.clone())

public fun Expr.Reference.clone(): Expr.Reference =
    Expr.Reference(attrs.cloneAttrs(), andToken, mutability, expr.clone())

public fun Expr.Repeat.clone(): Expr.Repeat =
    Expr.Repeat(attrs.cloneAttrs(), bracketToken, expr.clone(), semiToken, len.clone())

public fun Expr.Return.clone(): Expr.Return =
    Expr.Return(attrs.cloneAttrs(), returnToken, expr?.clone())

public fun Expr.Struct.clone(): Expr.Struct =
    Expr.Struct(attrs.cloneAttrs(), qself?.clone(), path.clone(), braceToken, fields.copy({ it.clone() }, { it }), dot2Token, rest?.clone())

public fun Expr.Try.clone(): Expr.Try =
    Expr.Try(attrs.cloneAttrs(), expr.clone(), questionToken)

public fun Expr.TryBlock.clone(): Expr.TryBlock =
    Expr.TryBlock(attrs.cloneAttrs(), tryToken, modifiers.clone(), block.clone())

public fun Expr.Tuple.clone(): Expr.Tuple =
    Expr.Tuple(attrs.cloneAttrs(), parenToken, elems.copy({ it.clone() }, { it }))

public fun Expr.Unary.clone(): Expr.Unary =
    Expr.Unary(attrs.cloneAttrs(), op.clone(), expr.clone())

public fun Expr.Unsafe.clone(): Expr.Unsafe =
    Expr.Unsafe(attrs.cloneAttrs(), unsafeToken, block.clone())

public fun Expr.Verbatim.clone(): Expr.Verbatim =
    Expr.Verbatim(tokens.clone())

public fun Expr.While.clone(): Expr.While =
    Expr.While(attrs.cloneAttrs(), label?.clone(), whileToken, cond.clone(), body.clone())

public fun Expr.Yield.clone(): Expr.Yield =
    Expr.Yield(attrs.cloneAttrs(), yieldToken, expr?.clone())

public fun Field.clone(): Field =
    Field(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), ident?.let { it.copy() }, colonToken, ty.clone(), default?.clone())

public fun FieldPat.clone(): FieldPat =
    FieldPat(member.clone(), colonToken, pat.clone(), attrs.cloneAttrs())

public fun FieldValue.clone(): FieldValue =
    FieldValue(attrs.cloneAttrs(), member.clone(), colonToken, expr.clone())

public fun Fields.clone(): Fields =
    when (this) {
        is Fields.Named -> Fields.Named(fields.clone())
        is Fields.Unnamed -> Fields.Unnamed(fields.clone())
        is Fields.Unit -> Fields.Unit
    }

public fun FieldsNamed.clone(): FieldsNamed =
    FieldsNamed(braceToken, named.copy({ it.clone() }, { it }))

public fun FieldsUnnamed.clone(): FieldsUnnamed =
    FieldsUnnamed(parenToken, unnamed.copy({ it.clone() }, { it }))

public fun File.clone(): File =
    File(shebang, frontmatter?.clone(), attrs.cloneAttrs(), items.cloneItems())

public fun FnArg.clone(): FnArg =
    when (this) {
        is FnArg.Receiver -> this.clone()
        is FnArg.Typed -> this.clone()
    }

public fun FnArg.Typed.clone(): FnArg.Typed =
    FnArg.Typed(patType.clone())

public fun FnModifiers.clone(): FnModifiers =
    FnModifiers(defaultness)

public fun FnPtrVariadic.clone(): FnPtrVariadic =
    FnPtrVariadic(attrs.cloneAttrs(), name?.clone(), dots, comma)

public fun ForeignItem.clone(): ForeignItem =
    when (this) {
        is ForeignItem.Fn -> this.clone()
        is ForeignItem.Static -> this.clone()
        is ForeignItem.ItemType -> this.clone()
        is ForeignItem.Macro -> this.clone()
        is ForeignItem.Verbatim -> this.clone()
    }

public fun ForeignItem.Fn.clone(): ForeignItem.Fn =
    ForeignItem.Fn(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), sig.clone(), semiToken)

public fun ForeignItem.Macro.clone(): ForeignItem.Macro =
    ForeignItem.Macro(attrs.cloneAttrs(), mac.clone(), semiToken)

public fun ForeignItem.Static.clone(): ForeignItem.Static =
    ForeignItem.Static(attrs.cloneAttrs(), vis.clone(), safety.clone(), staticToken, mutability.clone(), ident.copy(), colonToken, ty.clone(), semiToken)

public fun ForeignItem.ItemType.clone(): ForeignItem.ItemType =
    ForeignItem.ItemType(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), typeToken, ident.copy(), generics.clone(), semiToken)

public fun ForeignItem.Verbatim.clone(): ForeignItem.Verbatim =
    ForeignItem.Verbatim(tokens.clone())

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
        is GenericParam.LifetimeParam -> this.clone()
        is GenericParam.TypeParam -> this.clone()
        is GenericParam.ConstParam -> this.clone()
    }

public fun GenericParam.ConstParam.clone(): GenericParam.ConstParam =
    GenericParam.ConstParam(attrs.cloneAttrs(), constToken, ident.copy(), colonToken, ty.clone(), default?.clone())

public fun Generics.clone(): Generics =
    Generics(ltToken, params.copy({ it.clone() }, { it }), gtToken, whereClause?.clone())

public fun ImplItem.clone(): ImplItem =
    when (this) {
        is ImplItem.Const -> this.clone()
        is ImplItem.Fn -> this.clone()
        is ImplItem.AssocType -> this.clone()
        is ImplItem.Macro -> this.clone()
        is ImplItem.Verbatim -> this.clone()
    }

public fun ImplItem.Const.clone(): ImplItem.Const =
    ImplItem.Const(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), constToken, ident.copy(), generics.clone(), colonToken, ty.clone(), eqToken, expr.clone(), semiToken)

public fun ImplItem.Fn.clone(): ImplItem.Fn =
    ImplItem.Fn(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), sig.clone(), block.clone())

public fun ImplItem.Macro.clone(): ImplItem.Macro =
    ImplItem.Macro(attrs.cloneAttrs(), mac.clone(), semiToken)

public fun ImplItem.AssocType.clone(): ImplItem.AssocType =
    ImplItem.AssocType(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), typeToken, ident.copy(), generics.clone(), eqToken, ty.clone(), semiToken)

public fun ImplItem.Verbatim.clone(): ImplItem.Verbatim =
    ImplItem.Verbatim(tokens.clone())

public fun ImplModifiers.clone(): ImplModifiers =
    ImplModifiers(defaultness, polarity)

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
        is Item.ItemType -> this.clone()
        is Item.Union -> this.clone()
        is Item.Use -> this.clone()
        is Item.Verbatim -> this.clone()
    }

public fun Item.Const.clone(): Item.Const =
    Item.Const(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), constToken, ident.copy(), generics.clone(), colonToken, ty.clone(), eqToken, expr.clone(), semiToken)

public fun Item.Enum.clone(): Item.Enum =
    Item.Enum(attrs.cloneAttrs(), vis.clone(), enumToken, ident.copy(), generics.clone(), braceToken, variants.copy({ it.clone() }, { it }))

public fun Item.ExternCrate.clone(): Item.ExternCrate =
    Item.ExternCrate(attrs.cloneAttrs(), vis.clone(), externToken, crateToken, ident.copy(), rename?.clone(), semiToken)

public fun Item.Fn.clone(): Item.Fn =
    Item.Fn(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), sig.clone(), block.clone())

public fun Item.ForeignMod.clone(): Item.ForeignMod =
    Item.ForeignMod(attrs.cloneAttrs(), unsafety, abi.clone(), braceToken, items.cloneForeignItems())

public fun Item.Impl.clone(): Item.Impl =
    Item.Impl(attrs.cloneAttrs(), modifiers.clone(), unsafety, implToken, generics.clone(), traitPath?.clone(), selfType.clone(), braceToken, items.cloneImplItems())

public fun Item.Macro.clone(): Item.Macro =
    Item.Macro(attrs.cloneAttrs(), ident?.let { it.copy() }, mac.clone(), semiToken)

public fun Item.Mod.clone(): Item.Mod =
    Item.Mod(attrs.cloneAttrs(), vis.clone(), unsafety, modToken, ident.copy(), content?.clone())

public fun Item.Static.clone(): Item.Static =
    Item.Static(attrs.cloneAttrs(), vis.clone(), staticToken, mutability.clone(), ident.copy(), colonToken, ty.clone(), eqToken, expr.clone(), semiToken)

public fun Item.Struct.clone(): Item.Struct =
    Item.Struct(attrs.cloneAttrs(), vis.clone(), structToken, ident.copy(), generics.clone(), fields.clone(), semiToken)

public fun Item.Trait.clone(): Item.Trait =
    Item.Trait(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), unsafety, traitToken, ident.copy(), generics.clone(), colonToken, supertraits.copy({ it.clone() }, { it }), braceToken, items.cloneTraitItems())

public fun Item.TraitAlias.clone(): Item.TraitAlias =
    Item.TraitAlias(attrs.cloneAttrs(), vis.clone(), traitToken, ident.copy(), generics.clone(), eqToken, bounds.copy({ it.clone() }, { it }), semiToken)

public fun Item.ItemType.clone(): Item.ItemType =
    Item.ItemType(attrs.cloneAttrs(), vis.clone(), modifiers.clone(), typeToken, ident.copy(), generics.clone(), eqToken, ty.clone(), semiToken, whereClausePlacement)

public fun Item.Union.clone(): Item.Union =
    Item.Union(attrs.cloneAttrs(), vis.clone(), unionToken, ident.copy(), generics.clone(), fields.clone())

public fun Item.Use.clone(): Item.Use =
    Item.Use(attrs.cloneAttrs(), vis.clone(), useToken, leadingColon, tree.clone(), semiToken)

public fun Item.Verbatim.clone(): Item.Verbatim =
    Item.Verbatim(tokens.clone())

public fun Label.clone(): Label =
    Label(name.clone(), colonToken)

public fun GenericParam.LifetimeParam.clone(): GenericParam.LifetimeParam =
    GenericParam.LifetimeParam(attrs.cloneAttrs(), lifetime.clone(), colonToken, bounds.copy({ it.clone() }, { it }))

public fun Lit.clone(): Lit =
    when (this) {
        is Lit.Str -> Lit.Str(value.clone())
        is Lit.ByteStr -> Lit.ByteStr(value.clone())
        is Lit.CStr -> Lit.CStr(value.clone())
        is Lit.Byte -> Lit.Byte(value.clone())
        is Lit.Char -> Lit.Char(value.clone())
        is Lit.Int -> Lit.Int(value.clone())
        is Lit.Float -> Lit.Float(value.clone())
        is Lit.Bool -> Lit.Bool(value.clone())
        is Lit.Verbatim -> Lit.Verbatim(cloneLiteral(value))
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
        is Member.Named -> Member.Named(ident.copy())
        is Member.Unnamed -> Member.Unnamed(index.clone())
    }

public fun Meta.clone(): Meta =
    when (this) {
        is Meta.PathMeta -> this.clone()
        is Meta.List -> this.clone()
        is Meta.NameValue -> this.clone()
    }

public fun Meta.PathMeta.clone(): Meta.PathMeta =
    Meta.PathMeta(path.clone())

public fun Meta.List.clone(): Meta.List =
    Meta.List(path.clone(), delimiter.clone(), tokens.clone())

public fun Meta.NameValue.clone(): Meta.NameValue =
    Meta.NameValue(path.clone(), eqToken, value.clone())

public fun Pat.clone(): Pat =
    when (this) {
        is Pat.Guard -> this.clone()
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

public fun Pat.Guard.clone(): Pat.Guard =
    Pat.Guard(attrs.cloneAttrs(), pat.clone(), ifToken, guard.clone())

public fun Pat.Ident.clone(): Pat.Ident =
    Pat.Ident(attrs.cloneAttrs(), byRef, mutability, ident.copy(), atToken, subpat?.clone())

public fun Pat.Tuple.clone(): Pat.Tuple =
    Pat.Tuple(parenToken, elems.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun Pat.Or.clone(): Pat.Or =
    Pat.Or(leadingVert, cases.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun Pat.PatParen.clone(): Pat.PatParen =
    Pat.PatParen(parenToken, pat.clone(), attrs.cloneAttrs())

public fun Pat.Reference.clone(): Pat.Reference =
    Pat.Reference(andToken, mutability, pat.clone(), attrs.cloneAttrs())

public fun Pat.Struct.clone(): Pat.Struct =
    Pat.Struct(qself?.clone(), path.clone(), braceToken, fields.copy({ it.clone() }, { it }), rest?.clone(), dot2Token, attrs.cloneAttrs())

public fun Pat.Slice.clone(): Pat.Slice =
    Pat.Slice(bracketToken, elems.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun Pat.TypeAscription.clone(): Pat.TypeAscription =
    Pat.TypeAscription(attrs.cloneAttrs(), pat.clone(), colonToken, ty.clone())

public fun Pat.Const.clone(): Pat.Const =
    Pat.Const(attrs.cloneAttrs(), constToken, block.clone())

public fun Pat.Lit.clone(): Pat.Lit =
    Pat.Lit(attrs.cloneAttrs(), lit.clone())

public fun Pat.Macro.clone(): Pat.Macro =
    Pat.Macro(attrs.cloneAttrs(), mac.clone())

public fun Pat.Path.clone(): Pat.Path =
    Pat.Path(attrs.cloneAttrs(), qself?.clone(), path.clone())

public fun Pat.Range.clone(): Pat.Range =
    Pat.Range(attrs.cloneAttrs(), start?.clone(), limits.clone(), end?.clone())

public fun Pat.Rest.clone(): Pat.Rest =
    Pat.Rest(attrs.cloneAttrs(), dot2Token)

public fun Pat.TupleStruct.clone(): Pat.TupleStruct =
    Pat.TupleStruct(attrs.cloneAttrs(), qself?.clone(), path.clone(), parenToken, elems.copy({ it.clone() }, { it }))

public fun Pat.Wild.clone(): Pat.Wild =
    Pat.Wild(attrs.cloneAttrs(), underscoreToken)

public fun Pat.Verbatim.clone(): Pat.Verbatim =
    Pat.Verbatim(tokens.clone())

public fun PatRest.clone(): PatRest =
    PatRest(dot2Token, attrs.cloneAttrs())

public fun PatType.clone(): PatType =
    PatType(attrs.cloneAttrs(), pat.clone(), colonToken, ty.clone())

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
    PathSegment(ident.copy(), arguments.clone())

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
    FnArg.Receiver(attrs.cloneAttrs(), mutability, selfToken, kind.clone())

public fun ReceiverKind.clone(): ReceiverKind =
    when (this) {
        ReceiverKind.Value -> ReceiverKind.Value
        is ReceiverKind.Reference -> ReceiverKind.Reference(andToken, lifetime?.clone(), mutability)
        is ReceiverKind.Typed -> ReceiverKind.Typed(colonToken, ty.clone())
    }

public fun ReturnType.clone(): ReturnType =
    when (this) {
        is ReturnType.Default -> ReturnType.Default
        is ReturnType.TypeReturn -> ReturnType.TypeReturn(arrowToken, ty.clone())
    }

public fun Safety.clone(): Safety =
    when (this) {
        is Safety.Safe -> Safety.Safe(token)
        is Safety.Unsafe -> Safety.Unsafe(token)
        Safety.Default -> Safety.Default
    }

public fun Signature.clone(): Signature =
    Signature(constness, asyncness, safety.clone(), abi?.clone(), fnToken, ident.copy(), generics.clone(), parenToken, inputs.copy({ it.clone() }, { it }), variadic?.clone(), output.clone())

public fun Stmt.clone(): Stmt =
    when (this) {
        is Stmt.Local -> this.clone()
        is Stmt.ItemStmt -> this.clone()
        is Stmt.ExprStmt -> this.clone()
        is Stmt.MacroStmt -> this.clone()
    }

public fun Stmt.Local.clone(): Stmt.Local =
    Stmt.Local(attrs.cloneAttrs(), letToken, modifiers.clone(), pat.clone(), init?.clone(), semiToken)

public fun Stmt.ItemStmt.clone(): Stmt.ItemStmt =
    Stmt.ItemStmt(item.clone())

public fun Stmt.ExprStmt.clone(): Stmt.ExprStmt =
    Stmt.ExprStmt(expr.clone(), semiToken)

public fun Stmt.MacroStmt.clone(): Stmt.MacroStmt =
    Stmt.MacroStmt(attrs.cloneAttrs(), mac.clone(), semiToken)

public fun TraitItem.clone(): TraitItem =
    when (this) {
        is TraitItem.Const -> this.clone()
        is TraitItem.Fn -> this.clone()
        is TraitItem.AssocType -> this.clone()
        is TraitItem.Macro -> this.clone()
        is TraitItem.Verbatim -> this.clone()
    }

public fun TraitItem.Const.clone(): TraitItem.Const =
    TraitItem.Const(attrs.cloneAttrs(), modifiers.clone(), constToken, ident.copy(), generics.clone(), colonToken, ty.clone(), default?.clone(), semiToken)

public fun TraitItem.Fn.clone(): TraitItem.Fn =
    TraitItem.Fn(attrs.cloneAttrs(), modifiers.clone(), sig.clone(), default?.clone(), semiToken)

public fun TraitItem.AssocType.clone(): TraitItem.AssocType =
    TraitItem.AssocType(attrs.cloneAttrs(), modifiers.clone(), typeToken, ident.copy(), generics.clone(), colonToken, bounds.copy({ it.clone() }, { it }), default?.clone(), semiToken)

public fun TraitItem.Macro.clone(): TraitItem.Macro =
    TraitItem.Macro(attrs.cloneAttrs(), mac.clone(), semiToken)

public fun TraitItem.Verbatim.clone(): TraitItem.Verbatim =
    TraitItem.Verbatim(tokens.clone())

public fun TraitModifiers.clone(): TraitModifiers =
    TraitModifiers(autoToken)

public fun SynType.clone(): SynType =
    when (this) {
        is SynType.Array -> this.clone()
        is SynType.FnPtr -> this.clone()
        is SynType.Group -> this.clone()
        is SynType.ImplTrait -> this.clone()
        is SynType.Infer -> this.clone()
        is SynType.Macro -> this.clone()
        is SynType.Never -> this.clone()
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
    SynType.Array(
        elem.clone(),
        len.clone(),
        io.github.kotlinmania.syn.token.Bracket
            .from(bracketToken.span),
        io.github.kotlinmania.syn.token.Semi
            .from(semiToken.span),
        attrs.cloneAttrs(),
    )

public fun SynType.FnPtr.clone(): SynType.FnPtr =
    SynType.FnPtr(lifetimes?.clone(), unsafety, abi?.clone(), fnToken, parenToken, inputs.copy({ it.clone() }, { it }), variadic?.clone(), output.clone(), attrs.cloneAttrs())

public fun SynType.Group.clone(): SynType.Group =
    SynType.Group(groupToken, elem.clone(), attrs.cloneAttrs())

public fun SynType.ImplTrait.clone(): SynType.ImplTrait =
    SynType.ImplTrait(implToken, bounds.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun SynType.Infer.clone(): SynType.Infer =
    SynType.Infer(underscoreToken, attrs.cloneAttrs())

public fun SynType.Macro.clone(): SynType.Macro =
    SynType.Macro(mac.clone(), attrs.cloneAttrs())

public fun SynType.Never.clone(): SynType.Never =
    SynType.Never(bangToken, attrs.cloneAttrs())

public fun SynType.Paren.clone(): SynType.Paren =
    SynType.Paren(parenToken, elem.clone(), attrs.cloneAttrs())

public fun SynType.Path.clone(): SynType.Path =
    SynType.Path(qself?.clone(), path.clone(), attrs.cloneAttrs())

public fun SynType.Ptr.clone(): SynType.Ptr =
    SynType.Ptr(starToken, mutability.clone(), elem.clone(), attrs.cloneAttrs())

public fun SynType.Reference.clone(): SynType.Reference =
    SynType.Reference(andToken, lifetime?.clone(), mutability, elem.clone(), attrs.cloneAttrs())

public fun SynType.Slice.clone(): SynType.Slice =
    SynType.Slice(
        elem.clone(),
        io.github.kotlinmania.syn.token.Bracket
            .from(bracketToken.span),
        attrs.cloneAttrs(),
    )

public fun SynType.TraitObject.clone(): SynType.TraitObject =
    SynType.TraitObject(dynToken, bounds.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun SynType.Tuple.clone(): SynType.Tuple =
    SynType.Tuple(parenToken, elems.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun SynType.Verbatim.clone(): SynType.Verbatim =
    SynType.Verbatim(tokens.clone())

public fun TypeModifiers.clone(): TypeModifiers =
    TypeModifiers(defaultness)

public fun GenericParam.TypeParam.clone(): GenericParam.TypeParam =
    GenericParam.TypeParam(attrs.cloneAttrs(), ident.copy(), colonToken, bounds.copy({ it.clone() }, { it }), default?.clone())

public fun TypeParamBound.clone(): TypeParamBound =
    when (this) {
        is TypeParamBound.Trait -> this.clone()
        is TypeParamBound.LifetimeBound -> this.clone()
        is TypeParamBound.PreciseCapture -> this.clone()
        is TypeParamBound.Verbatim -> this.clone()
    }

public fun TypeParamBound.Trait.clone(): TypeParamBound.Trait =
    TypeParamBound.Trait(parenToken, lifetimes?.clone(), modifiers.clone(), maybe, path.clone())

public fun TypeParamBound.LifetimeBound.clone(): TypeParamBound.LifetimeBound =
    TypeParamBound.LifetimeBound(lifetime.clone())

public fun TypeParamBound.PreciseCapture.clone(): TypeParamBound.PreciseCapture =
    TypeParamBound.PreciseCapture(useToken, ltToken, params.copy({ it.clone() }, { it }), gtToken)

public fun TypeParamBound.Verbatim.clone(): TypeParamBound.Verbatim =
    TypeParamBound.Verbatim(tokens.clone())

public fun UnOp.clone(): UnOp =
    when (this) {
        is UnOp.Deref -> this
        is UnOp.NotOp -> this
        is UnOp.Neg -> this
    }

public fun Variant.clone(): Variant =
    Variant(attrs.cloneAttrs(), ident.copy(), fields.clone(), discriminant?.let { it.copy(expr = it.expr.clone()) })

public fun Visibility.clone(): Visibility =
    when (this) {
        is Visibility.Public -> Visibility.Public(pubToken)
        is Visibility.Restricted -> this.clone()
        is Visibility.Inherited -> Visibility.Inherited
    }

public fun Visibility.Restricted.clone(): Visibility.Restricted =
    Visibility.Restricted(pubToken, parenToken, inToken, path.clone())

public fun WhereClause.clone(): WhereClause =
    WhereClause(whereToken, predicates.copy({ it.clone() }, { it }))

public fun WhereClausePlacement.clone(): WhereClausePlacement = this

public fun WherePredicate.clone(): WherePredicate =
    when (this) {
        is WherePredicate.TypePredicate -> this.clone()
        is WherePredicate.LifetimePredicate -> this.clone()
    }

public fun WherePredicate.TypePredicate.clone(): WherePredicate.TypePredicate =
    WherePredicate.TypePredicate(lifetimes?.clone(), boundedTy.clone(), colonToken, bounds.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun WherePredicate.LifetimePredicate.clone(): WherePredicate.LifetimePredicate =
    WherePredicate.LifetimePredicate(lifetime.clone(), colonToken, bounds.copy({ it.clone() }, { it }), attrs.cloneAttrs())

public fun EqExpr.clone(): EqExpr = EqExpr(eqToken, expr.clone())

public fun EqSynType.clone(): EqSynType = EqSynType(eqToken, type.clone())

public fun ElseExpr.clone(): ElseExpr = ElseExpr(elseToken, expr.clone())

public fun IfExpr.clone(): IfExpr = IfExpr(ifToken, expr.clone())

public fun AndLifetime.clone(): AndLifetime = AndLifetime(andToken, lifetime?.clone())

public fun AsIdent.clone(): AsIdent = AsIdent(asToken, ident.copy())

public fun IdentColon.clone(): IdentColon = IdentColon(ident.copy(), colonToken)

public fun PatColon.clone(): PatColon = PatColon(pat.clone(), colonToken)

public fun LocalInit.clone(): LocalInit = LocalInit(eqToken, expr.clone(), diverge?.clone())

internal fun List<Attribute>.cloneAttrs(): MutableList<Attribute> =
    mapTo(mutableListOf()) { it.clone() }

internal fun List<Stmt>.cloneStmts(): MutableList<Stmt> =
    mapTo(mutableListOf()) { it.clone() }

internal fun List<Arm>.cloneArms(): MutableList<Arm> =
    mapTo(mutableListOf()) { it.clone() }

internal fun List<Item>.cloneItems(): MutableList<Item> =
    mapTo(mutableListOf()) { it.clone() }

internal fun List<ForeignItem>.cloneForeignItems(): MutableList<ForeignItem> =
    mapTo(mutableListOf()) { it.clone() }

internal fun List<ImplItem>.cloneImplItems(): MutableList<ImplItem> =
    mapTo(mutableListOf()) { it.clone() }

internal fun List<TraitItem>.cloneTraitItems(): MutableList<TraitItem> =
    mapTo(mutableListOf()) { it.clone() }

public fun PathForPair.clone(): PathForPair =
    PathForPair(path.clone(), forToken)

public fun ModContent.clone(): ModContent =
    when (this) {
        is ModContent.Unnamed -> ModContent.Unnamed(semiToken)
        is ModContent.Inline -> ModContent.Inline(braceToken, items.cloneItems())
    }

public fun UseTree.clone(): UseTree =
    when (this) {
        is UseTree.Path -> this.clone()
        is UseTree.Name -> this.clone()
        is UseTree.Group -> this.clone()
        is UseTree.Glob -> this.clone()
    }

public fun UseTree.Path.clone(): UseTree.Path =
    UseTree.Path(ident.copy(), colon2Token, tree?.clone())

public fun UseTree.Name.clone(): UseTree.Name =
    UseTree.Name(ident.copy(), rename?.clone())

public fun UseTree.Group.clone(): UseTree.Group =
    UseTree.Group(braceToken, items.copy({ it.clone() }, { it }))

public fun UseTree.Glob.clone(): UseTree.Glob =
    UseTree.Glob(starToken)

public fun StaticMutability.clone(): StaticMutability =
    when (this) {
        is StaticMutability.Mut -> this
        is StaticMutability.None -> StaticMutability.None
    }

public fun Variadic.clone(): Variadic =
    Variadic(attrs.cloneAttrs(), pat?.clone(), dots, comma)
