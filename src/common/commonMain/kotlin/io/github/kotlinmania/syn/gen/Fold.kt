// port-lint: source gen/fold.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.AssocConst
import io.github.kotlinmania.syn.AssocType
import io.github.kotlinmania.syn.AttrStyle
import io.github.kotlinmania.syn.Attribute
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
import io.github.kotlinmania.syn.Expr
import io.github.kotlinmania.syn.Field
import io.github.kotlinmania.syn.FieldPat
import io.github.kotlinmania.syn.FieldValue
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
import io.github.kotlinmania.syn.File
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.FnPtrVariadic
import io.github.kotlinmania.syn.ForeignItem
import io.github.kotlinmania.syn.Frontmatter
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.ImplItem
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
import io.github.kotlinmania.syn.NamedArg
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatRest
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
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
import io.github.kotlinmania.syn.TypeParamBound
import io.github.kotlinmania.syn.UnOp
import io.github.kotlinmania.syn.UseTree
import io.github.kotlinmania.syn.Variadic
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.WhereClause
import io.github.kotlinmania.syn.WhereClausePlacement
import io.github.kotlinmania.syn.WherePredicate

/**
 * AST fold -- traverses a syntax tree and returns rewritten nodes.
 *
 * Override methods to intercept and transform specific node types. Default
 * implementations recurse into sub-nodes and rebuild the original shape.
 */
public open class Fold {
    public open fun foldExpr(e: Expr): Expr =
        foldExpr(this, e)

    public open fun foldType(t: SynType): SynType =
        foldType(this, t)

    public open fun foldPath(p: Path): Path =
        foldPath(this, p)

    public open fun foldPat(p: Pat): Pat =
        foldPat(this, p)

    public open fun foldItem(i: Item): Item =
        foldItem(this, i)

    public open fun foldAttribute(a: Attribute): Attribute =
        foldAttribute(this, a)

    public open fun foldAttrStyle(s: AttrStyle): AttrStyle =
        foldAttrStyle(this, s)

    public open fun foldMeta(m: Meta): Meta =
        foldMeta(this, m)

    public open fun foldMetaList(m: Meta.List): Meta.List =
        foldMetaList(this, m)

    public open fun foldMetaNameValue(m: Meta.NameValue): Meta.NameValue =
        foldMetaNameValue(this, m)

    public open fun foldGenerics(g: Generics): Generics =
        foldGenerics(this, g)

    public open fun foldLit(l: Lit): Lit =
        foldLit(this, l)

    public open fun foldLitBool(l: LitBool): LitBool =
        foldLitBool(this, l)

    public open fun foldLitByte(l: LitByte): LitByte =
        foldLitByte(this, l)

    public open fun foldLitByteStr(l: LitByteStr): LitByteStr =
        foldLitByteStr(this, l)

    public open fun foldLitCstr(l: LitCStr): LitCStr =
        foldLitCstr(this, l)

    public open fun foldLitCStr(l: LitCStr): LitCStr =
        foldLitCStr(this, l)

    public open fun foldLitChar(l: LitChar): LitChar =
        foldLitChar(this, l)

    public open fun foldLitFloat(l: LitFloat): LitFloat =
        foldLitFloat(this, l)

    public open fun foldLitInt(l: LitInt): LitInt =
        foldLitInt(this, l)

    public open fun foldLitStr(l: LitStr): LitStr =
        foldLitStr(this, l)

    public open fun foldLifetime(lt: Lifetime): Lifetime =
        foldLifetime(this, lt)

    public open fun foldIdent(id: Ident): Ident =
        foldIdent(this, id)

    public open fun foldStmt(s: Stmt): Stmt =
        foldStmt(this, s)

    public open fun foldData(d: Data): Data =
        foldData(this, d)

    public open fun foldDataEnum(d: DataEnum): DataEnum =
        foldDataEnum(this, d)

    public open fun foldDataStruct(d: DataStruct): DataStruct =
        foldDataStruct(this, d)

    public open fun foldDataUnion(d: DataUnion): DataUnion =
        foldDataUnion(this, d)

    public open fun foldDeriveInput(di: DeriveInput): DeriveInput =
        foldDeriveInput(this, di)

    public open fun foldBlock(block: Block): Block =
        foldBlock(this, block)

    public open fun foldAttributes(attrs: List<Attribute>): List<Attribute> =
        foldAttributes(this, attrs)

    public open fun foldFile(file: File): File =
        foldFile(this, file)

    public open fun foldFrontmatter(frontmatter: Frontmatter): Frontmatter =
        foldFrontmatter(this, frontmatter)

    public open fun foldSignature(sig: Signature): Signature =
        foldSignature(this, sig)

    public open fun foldSafety(safety: Safety): Safety =
        foldSafety(this, safety)

    public open fun foldAbi(a: Abi): Abi =
        foldAbi(this, a)

    public open fun foldReturnType(rt: ReturnType): ReturnType =
        foldReturnType(this, rt)

    public open fun foldFnArg(arg: FnArg): FnArg =
        foldFnArg(this, arg)

    public open fun foldReceiver(receiver: FnArg.Receiver): FnArg.Receiver =
        foldReceiver(this, receiver)

    public open fun foldReceiverKind(kind: ReceiverKind): ReceiverKind =
        foldReceiverKind(this, kind)

    public open fun foldPatType(patType: PatType): PatType =
        foldPatType(this, patType)

    public open fun foldPatGuard(pat: Pat.Guard): Pat.Guard =
        foldPatGuard(this, pat)

    public open fun foldPatIdent(patIdent: Pat.Ident): Pat.Ident =
        foldPatIdent(this, patIdent)

    public open fun foldPatOr(pat: Pat.Or): Pat.Or =
        foldPatOr(this, pat)

    public open fun foldPatParen(pat: Pat.PatParen): Pat.PatParen =
        foldPatParen(this, pat)

    public open fun foldPatRange(pat: Pat.Range): Pat.Range =
        foldPatRange(this, pat)

    public open fun foldPatReference(pat: Pat.Reference): Pat.Reference =
        foldPatReference(this, pat)

    public open fun foldPatRest(pat: Pat.Rest): Pat.Rest =
        foldPatRest(this, pat)

    public open fun foldPatRest(patRest: PatRest): PatRest =
        foldPatRest(this, patRest)

    public open fun foldPatSlice(pat: Pat.Slice): Pat.Slice =
        foldPatSlice(this, pat)

    public open fun foldPatStruct(pat: Pat.Struct): Pat.Struct =
        foldPatStruct(this, pat)

    public open fun foldPatTuple(pat: Pat.Tuple): Pat.Tuple =
        foldPatTuple(this, pat)

    public open fun foldPatTupleStruct(pat: Pat.TupleStruct): Pat.TupleStruct =
        foldPatTupleStruct(this, pat)

    public open fun foldPatWild(pat: Pat.Wild): Pat.Wild =
        foldPatWild(this, pat)

    public open fun foldTypePath(typePath: SynType.Path): SynType.Path =
        foldTypePath(this, typePath)

    public open fun foldTypeArray(ty: SynType.Array): SynType.Array =
        foldTypeArray(this, ty)

    public open fun foldTypeGroup(ty: SynType.Group): SynType.Group =
        foldTypeGroup(this, ty)

    public open fun foldTypeReference(ty: SynType.Reference): SynType.Reference =
        foldTypeReference(this, ty)

    public open fun foldTypeImplTrait(ty: SynType.ImplTrait): SynType.ImplTrait =
        foldTypeImplTrait(this, ty)

    public open fun foldTypeInfer(ty: SynType.Infer): SynType.Infer =
        foldTypeInfer(this, ty)

    public open fun foldTypeMacro(ty: SynType.Macro): SynType.Macro =
        foldTypeMacro(this, ty)

    public open fun foldTypeNever(ty: SynType.Never): SynType.Never =
        foldTypeNever(this, ty)

    public open fun foldTypePtr(ty: SynType.Ptr): SynType.Ptr =
        foldTypePtr(this, ty)

    public open fun foldTypeSlice(ty: SynType.Slice): SynType.Slice =
        foldTypeSlice(this, ty)

    public open fun foldTypeTraitObject(ty: SynType.TraitObject): SynType.TraitObject =
        foldTypeTraitObject(this, ty)

    public open fun foldTypeTuple(ty: SynType.Tuple): SynType.Tuple =
        foldTypeTuple(this, ty)

    public open fun foldPointerMutability(mutability: PointerMutability): PointerMutability =
        foldPointerMutability(this, mutability)

    public open fun foldTypeFnPtr(ty: SynType.FnPtr): SynType.FnPtr =
        foldTypeFnPtr(this, ty)

    public open fun foldNamedArg(arg: NamedArg): NamedArg =
        foldNamedArg(this, arg)

    public open fun foldFnPtrVariadic(variadic: FnPtrVariadic): FnPtrVariadic =
        foldFnPtrVariadic(this, variadic)

    public open fun foldTypeParen(ty: SynType.Paren): SynType.Paren =
        foldTypeParen(this, ty)

    public open fun foldExprArray(expr: Expr.Array): Expr.Array =
        foldExprArray(this, expr)

    public open fun foldExprAssign(expr: Expr.Assign): Expr.Assign =
        foldExprAssign(this, expr)

    public open fun foldExprAsync(expr: Expr.Async): Expr.Async =
        foldExprAsync(this, expr)

    public open fun foldExprAwait(expr: Expr.Await): Expr.Await =
        foldExprAwait(this, expr)

    public open fun foldExprBinary(expr: Expr.Binary): Expr.Binary =
        foldExprBinary(this, expr)

    public open fun foldExprBlock(expr: Expr.BlockExpr): Expr.BlockExpr =
        foldExprBlock(this, expr)

    public open fun foldExprBreak(expr: Expr.Break): Expr.Break =
        foldExprBreak(this, expr)

    public open fun foldExprCall(expr: Expr.Call): Expr.Call =
        foldExprCall(this, expr)

    public open fun foldExprCast(expr: Expr.Cast): Expr.Cast =
        foldExprCast(this, expr)

    public open fun foldExprClosure(expr: Expr.Closure): Expr.Closure =
        foldExprClosure(this, expr)

    public open fun foldExprConst(expr: Expr.Const): Expr.Const =
        foldExprConst(this, expr)

    public open fun foldExprContinue(expr: Expr.Continue): Expr.Continue =
        foldExprContinue(this, expr)

    public open fun foldExprField(expr: Expr.Field): Expr.Field =
        foldExprField(this, expr)

    public open fun foldExprForLoop(expr: Expr.ForLoop): Expr.ForLoop =
        foldExprForLoop(this, expr)

    public open fun foldExprGroup(expr: Expr.Group): Expr.Group =
        foldExprGroup(this, expr)

    public open fun foldExprIf(expr: Expr.If): Expr.If =
        foldExprIf(this, expr)

    public open fun foldExprIndex(expr: Expr.Index): Expr.Index =
        foldExprIndex(this, expr)

    public open fun foldExprInfer(expr: Expr.Infer): Expr.Infer =
        foldExprInfer(this, expr)

    public open fun foldExprLet(expr: Expr.Let): Expr.Let =
        foldExprLet(this, expr)

    public open fun foldExprLit(expr: Expr.Lit): Expr.Lit =
        foldExprLit(this, expr)

    public open fun foldExprLoop(expr: Expr.Loop): Expr.Loop =
        foldExprLoop(this, expr)

    public open fun foldExprMacro(expr: Expr.Macro): Expr.Macro =
        foldExprMacro(this, expr)

    public open fun foldExprMatch(expr: Expr.Match): Expr.Match =
        foldExprMatch(this, expr)

    public open fun foldExprMethodCall(expr: Expr.MethodCall): Expr.MethodCall =
        foldExprMethodCall(this, expr)

    public open fun foldExprParen(expr: Expr.Paren): Expr.Paren =
        foldExprParen(this, expr)

    public open fun foldExprPath(exprPath: Expr.Path): Expr.Path =
        foldExprPath(this, exprPath)

    public open fun foldExprRange(expr: Expr.Range): Expr.Range =
        foldExprRange(this, expr)

    public open fun foldExprRawAddr(expr: Expr.RawAddr): Expr.RawAddr =
        foldExprRawAddr(this, expr)

    public open fun foldExprReference(expr: Expr.Reference): Expr.Reference =
        foldExprReference(this, expr)

    public open fun foldExprRepeat(expr: Expr.Repeat): Expr.Repeat =
        foldExprRepeat(this, expr)

    public open fun foldExprReturn(expr: Expr.Return): Expr.Return =
        foldExprReturn(this, expr)

    public open fun foldExprStruct(expr: Expr.Struct): Expr.Struct =
        foldExprStruct(this, expr)

    public open fun foldExprTry(expr: Expr.Try): Expr.Try =
        foldExprTry(this, expr)

    public open fun foldExprTryBlock(expr: Expr.TryBlock): Expr.TryBlock =
        foldExprTryBlock(this, expr)

    public open fun foldExprTuple(expr: Expr.Tuple): Expr.Tuple =
        foldExprTuple(this, expr)

    public open fun foldExprUnary(expr: Expr.Unary): Expr.Unary =
        foldExprUnary(this, expr)

    public open fun foldExprUnsafe(expr: Expr.Unsafe): Expr.Unsafe =
        foldExprUnsafe(this, expr)

    public open fun foldExprWhile(expr: Expr.While): Expr.While =
        foldExprWhile(this, expr)

    public open fun foldExprYield(expr: Expr.Yield): Expr.Yield =
        foldExprYield(this, expr)

    public open fun foldBinOp(op: BinOp): BinOp =
        foldBinOp(this, op)

    public open fun foldUnOp(op: UnOp): UnOp =
        foldUnOp(this, op)

    public open fun foldLabel(label: Label): Label =
        foldLabel(this, label)

    public open fun foldRangeLimits(limits: RangeLimits): RangeLimits =
        foldRangeLimits(this, limits)

    public open fun foldMacro(mac: Macro): Macro =
        foldMacro(this, mac)

    public open fun foldMacroDelimiter(delimiter: MacroDelimiter): MacroDelimiter =
        foldMacroDelimiter(this, delimiter)

    public open fun foldPathArguments(pathArgs: PathArguments): PathArguments =
        foldPathArguments(this, pathArgs)

    public open fun foldAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed =
        foldAngleBracketedGenericArguments(this, pathArgs)

    public open fun foldParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized =
        foldParenthesizedGenericArguments(this, pathArgs)

    public open fun foldGenericArgument(genArg: GenericArgument): GenericArgument =
        foldGenericArgument(this, genArg)

    public open fun foldAssocConst(assoc: AssocConst): AssocConst =
        foldAssocConst(this, assoc)

    public open fun foldAssocType(assoc: AssocType): AssocType =
        foldAssocType(this, assoc)

    public open fun foldConstraint(constraint: Constraint): Constraint =
        foldConstraint(this, constraint)

    public open fun foldTypeParamBound(bound: TypeParamBound): TypeParamBound =
        foldTypeParamBound(this, bound)

    public open fun foldTraitBound(bound: TypeParamBound.Trait): TypeParamBound.Trait =
        foldTraitBound(this, bound)

    public open fun foldBoundLifetimes(boundLifetimes: BoundLifetimes): BoundLifetimes =
        foldBoundLifetimes(this, boundLifetimes)

    public open fun foldCapturedParam(param: CapturedParam): CapturedParam =
        foldCapturedParam(this, param)

    public open fun foldPreciseCapture(preciseCapture: TypeParamBound.PreciseCapture): TypeParamBound.PreciseCapture =
        foldPreciseCapture(this, preciseCapture)

    public open fun foldPathSegment(segment: PathSegment): PathSegment =
        foldPathSegment(this, segment)

    public open fun foldArm(arm: Arm): Arm =
        foldArm(this, arm)

    public open fun foldElseExpr(elseExpr: ElseExpr): ElseExpr =
        foldElseExpr(this, elseExpr)

    public open fun foldFieldPat(fieldPat: FieldPat): FieldPat =
        foldFieldPat(this, fieldPat)

    public open fun foldFieldValue(fieldValue: FieldValue): FieldValue =
        foldFieldValue(this, fieldValue)

    public open fun foldGenericParam(param: GenericParam): GenericParam =
        foldGenericParam(this, param)

    public open fun foldConstParam(param: GenericParam.ConstParam): GenericParam.ConstParam =
        foldConstParam(this, param)

    public open fun foldLifetimeParam(param: GenericParam.LifetimeParam): GenericParam.LifetimeParam =
        foldLifetimeParam(this, param)

    public open fun foldTypeParam(param: GenericParam.TypeParam): GenericParam.TypeParam =
        foldTypeParam(this, param)

    public open fun foldField(field: Field): Field =
        foldField(this, field)

    public open fun foldFields(fields: Fields): Fields =
        foldFields(this, fields)

    public open fun foldFieldsNamed(fields: FieldsNamed): FieldsNamed =
        foldFieldsNamed(this, fields)

    public open fun foldFieldsUnnamed(fields: FieldsUnnamed): FieldsUnnamed =
        foldFieldsUnnamed(this, fields)

    public open fun foldImplItem(item: ImplItem): ImplItem =
        foldImplItem(this, item)

    public open fun foldForeignItem(item: ForeignItem): ForeignItem =
        foldForeignItem(this, item)

    public open fun foldForeignItemFn(item: ForeignItem.Fn): ForeignItem.Fn =
        foldForeignItemFn(this, item)

    public open fun foldForeignItemMacro(item: ForeignItem.Macro): ForeignItem.Macro =
        foldForeignItemMacro(this, item)

    public open fun foldForeignItemStatic(item: ForeignItem.Static): ForeignItem.Static =
        foldForeignItemStatic(this, item)

    public open fun foldForeignItemType(item: ForeignItem.ItemType): ForeignItem.ItemType =
        foldForeignItemType(this, item)

    public open fun foldImplItemConst(item: ImplItem.Const): ImplItem.Const =
        foldImplItemConst(this, item)

    public open fun foldImplItemFn(item: ImplItem.Fn): ImplItem.Fn =
        foldImplItemFn(this, item)

    public open fun foldImplItemMacro(item: ImplItem.Macro): ImplItem.Macro =
        foldImplItemMacro(this, item)

    public open fun foldImplItemType(item: ImplItem.AssocType): ImplItem.AssocType =
        foldImplItemType(this, item)

    public open fun foldItemConst(item: Item.Const): Item.Const =
        foldItemConst(this, item)

    public open fun foldItemEnum(item: Item.Enum): Item.Enum =
        foldItemEnum(this, item)

    public open fun foldItemExternCrate(item: Item.ExternCrate): Item.ExternCrate =
        foldItemExternCrate(this, item)

    public open fun foldItemFn(item: Item.Fn): Item.Fn =
        foldItemFn(this, item)

    public open fun foldItemForeignMod(item: Item.ForeignMod): Item.ForeignMod =
        foldItemForeignMod(this, item)

    public open fun foldItemImpl(item: Item.Impl): Item.Impl =
        foldItemImpl(this, item)

    public open fun foldItemMacro(item: Item.Macro): Item.Macro =
        foldItemMacro(this, item)

    public open fun foldItemMod(item: Item.Mod): Item.Mod =
        foldItemMod(this, item)

    public open fun foldItemStatic(item: Item.Static): Item.Static =
        foldItemStatic(this, item)

    public open fun foldItemStruct(item: Item.Struct): Item.Struct =
        foldItemStruct(this, item)

    public open fun foldItemTrait(item: Item.Trait): Item.Trait =
        foldItemTrait(this, item)

    public open fun foldItemTraitAlias(item: Item.TraitAlias): Item.TraitAlias =
        foldItemTraitAlias(this, item)

    public open fun foldItemType(item: Item.ItemType): Item.ItemType =
        foldItemType(this, item)

    public open fun foldItemUnion(item: Item.Union): Item.Union =
        foldItemUnion(this, item)

    public open fun foldItemUse(item: Item.Use): Item.Use =
        foldItemUse(this, item)

    public open fun foldStaticMutability(mutability: StaticMutability): StaticMutability =
        foldStaticMutability(this, mutability)

    public open fun foldModContent(modContent: ModContent): ModContent =
        foldModContent(this, modContent)

    public open fun foldLocal(local: Stmt.Local): Stmt.Local =
        foldLocal(this, local)

    public open fun foldLocalInit(init: LocalInit): LocalInit =
        foldLocalInit(this, init)

    public open fun foldStmtMacro(stmt: Stmt.MacroStmt): Stmt.MacroStmt =
        foldStmtMacro(this, stmt)

    public open fun foldMember(member: Member): Member =
        foldMember(this, member)

    public open fun foldIndex(index: Index): Index =
        foldIndex(this, index)

    public open fun foldQself(qself: QSelf): QSelf =
        foldQself(this, qself)

    public open fun foldQSelf(qself: QSelf): QSelf =
        foldQSelf(this, qself)

    public open fun foldTraitItem(item: TraitItem): TraitItem =
        foldTraitItem(this, item)

    public open fun foldTraitItemConst(item: TraitItem.Const): TraitItem.Const =
        foldTraitItemConst(this, item)

    public open fun foldTraitItemFn(item: TraitItem.Fn): TraitItem.Fn =
        foldTraitItemFn(this, item)

    public open fun foldTraitItemMacro(item: TraitItem.Macro): TraitItem.Macro =
        foldTraitItemMacro(this, item)

    public open fun foldTraitItemType(item: TraitItem.AssocType): TraitItem.AssocType =
        foldTraitItemType(this, item)

    public open fun foldUseTree(useTree: UseTree): UseTree =
        foldUseTree(this, useTree)

    public open fun foldUseGlob(useTree: UseTree.Glob): UseTree.Glob =
        foldUseGlob(this, useTree)

    public open fun foldUseGroup(useTree: UseTree.Group): UseTree.Group =
        foldUseGroup(this, useTree)

    public open fun foldUseName(useTree: UseTree.Name): UseTree.Name =
        foldUseName(this, useTree)

    public open fun foldUsePath(useTree: UseTree.Path): UseTree.Path =
        foldUsePath(this, useTree)

    public open fun foldUseRename(useTree: UseTree.Name): UseTree.Name =
        foldUseRename(this, useTree)

    public open fun foldVariadic(variadic: Variadic): Variadic =
        foldVariadic(this, variadic)

    public open fun foldVariant(variant: Variant): Variant =
        foldVariant(this, variant)

    public open fun foldVisibility(visibility: Visibility): Visibility =
        foldVisibility(this, visibility)

    public open fun foldVisRestricted(visibility: Visibility.Restricted): Visibility.Restricted =
        foldVisRestricted(this, visibility)

    public open fun foldWhereClause(whereClause: WhereClause): WhereClause =
        foldWhereClause(this, whereClause)

    public open fun foldWhereClausePlacement(placement: WhereClausePlacement): WhereClausePlacement =
        foldWhereClausePlacement(this, placement)

    public open fun foldWherePredicate(wherePredicate: WherePredicate): WherePredicate =
        foldWherePredicate(this, wherePredicate)

    public open fun foldPredicateLifetime(predicate: WherePredicate.LifetimePredicate): WherePredicate.LifetimePredicate =
        foldPredicateLifetime(this, predicate)

    public open fun foldPredicateType(predicate: WherePredicate.TypePredicate): WherePredicate.TypePredicate =
        foldPredicateType(this, predicate)

    public open fun foldSpan(span: Span): Span =
        foldSpan(this, span)

    public open fun foldTokenStream(tokens: TokenStream): TokenStream =
        foldTokenStream(this, tokens)
}

public fun foldExpr(f: Fold, e: Expr): Expr =
    when (e) {
        is Expr.Array -> f.foldExprArray(e)
        is Expr.Assign -> f.foldExprAssign(e)
        is Expr.Async -> f.foldExprAsync(e)
        is Expr.Await -> f.foldExprAwait(e)
        is Expr.Binary -> f.foldExprBinary(e)
        is Expr.BlockExpr -> f.foldExprBlock(e)
        is Expr.Break -> f.foldExprBreak(e)
        is Expr.Call -> f.foldExprCall(e)
        is Expr.Cast -> f.foldExprCast(e)
        is Expr.Closure -> f.foldExprClosure(e)
        is Expr.Const -> f.foldExprConst(e)
        is Expr.Continue -> f.foldExprContinue(e)
        is Expr.Field -> f.foldExprField(e)
        is Expr.ForLoop -> f.foldExprForLoop(e)
        is Expr.Group -> f.foldExprGroup(e)
        is Expr.If -> f.foldExprIf(e)
        is Expr.Index -> f.foldExprIndex(e)
        is Expr.Infer -> f.foldExprInfer(e)
        is Expr.Let -> f.foldExprLet(e)
        is Expr.Lit -> f.foldExprLit(e)
        is Expr.Loop -> f.foldExprLoop(e)
        is Expr.Macro -> f.foldExprMacro(e)
        is Expr.Match -> f.foldExprMatch(e)
        is Expr.MethodCall -> f.foldExprMethodCall(e)
        is Expr.Paren -> f.foldExprParen(e)
        is Expr.Path -> f.foldExprPath(e)
        is Expr.Range -> f.foldExprRange(e)
        is Expr.RawAddr -> f.foldExprRawAddr(e)
        is Expr.Reference -> f.foldExprReference(e)
        is Expr.Repeat -> f.foldExprRepeat(e)
        is Expr.Return -> f.foldExprReturn(e)
        is Expr.Struct -> f.foldExprStruct(e)
        is Expr.Try -> f.foldExprTry(e)
        is Expr.TryBlock -> f.foldExprTryBlock(e)
        is Expr.Tuple -> f.foldExprTuple(e)
        is Expr.Unary -> f.foldExprUnary(e)
        is Expr.Unsafe -> f.foldExprUnsafe(e)
        is Expr.While -> f.foldExprWhile(e)
        is Expr.Yield -> f.foldExprYield(e)
        is Expr.Verbatim -> e.copy(tokens = f.foldTokenStream(e.tokens))
    }

public fun foldType(f: Fold, t: SynType): SynType =
    when (t) {
        is SynType.Array -> {
            f.foldTypeArray(t)
        }

        is SynType.FnPtr -> {
            f.foldTypeFnPtr(t)
        }

        is SynType.Group -> {
            f.foldTypeGroup(t)
        }

        is SynType.ImplTrait -> {
            f.foldTypeImplTrait(t)
        }

        is SynType.Infer -> {
            f.foldTypeInfer(t)
        }

        is SynType.Macro -> {
            f.foldTypeMacro(t)
        }

        is SynType.Never -> {
            f.foldTypeNever(t)
        }

        is SynType.Paren -> {
            f.foldTypeParen(t)
        }

        is SynType.Path -> {
            f.foldTypePath(t)
        }

        is SynType.Ptr -> {
            f.foldTypePtr(t)
        }

        is SynType.Reference -> {
            f.foldTypeReference(t)
        }

        is SynType.Slice -> {
            f.foldTypeSlice(t)
        }

        is SynType.TraitObject -> {
            f.foldTypeTraitObject(t)
        }

        is SynType.Tuple -> {
            f.foldTypeTuple(t)
        }

        is SynType.Verbatim -> {
            t.copy(tokens = f.foldTokenStream(t.tokens))
        }
    }

public fun foldPath(f: Fold, p: Path): Path =
    Path(
        leadingColon = p.leadingColon,
        segments = p.segments.copy({ f.foldPathSegment(it) }, { it }),
    )

public fun foldPat(f: Fold, p: Pat): Pat =
    when (p) {
        is Pat.Const -> {
            p.copy(attrs = f.foldAttributes(p.attrs), block = f.foldBlock(p.block))
        }

        is Pat.Guard -> {
            f.foldPatGuard(p)
        }

        is Pat.Ident -> {
            f.foldPatIdent(p)
        }

        is Pat.Lit -> {
            p.copy(attrs = f.foldAttributes(p.attrs), lit = f.foldLit(p.lit))
        }

        is Pat.Macro -> {
            p.copy(attrs = f.foldAttributes(p.attrs), mac = f.foldMacro(p.mac))
        }

        is Pat.Or -> {
            f.foldPatOr(p)
        }

        is Pat.PatParen -> {
            f.foldPatParen(p)
        }

        is Pat.Path -> {
            p.copy(attrs = f.foldAttributes(p.attrs), qself = p.qself?.let { f.foldQself(it) }, path = f.foldPath(p.path))
        }

        is Pat.Range -> {
            f.foldPatRange(p)
        }

        is Pat.Reference -> {
            f.foldPatReference(p)
        }

        is Pat.Rest -> {
            f.foldPatRest(p)
        }

        is Pat.Slice -> {
            f.foldPatSlice(p)
        }

        is Pat.Struct -> {
            f.foldPatStruct(p)
        }

        is Pat.Tuple -> {
            f.foldPatTuple(p)
        }

        is Pat.TupleStruct -> {
            f.foldPatTupleStruct(p)
        }

        is Pat.TypeAscription -> {
            p.copy(attrs = f.foldAttributes(p.attrs), pat = f.foldPat(p.pat), ty = f.foldType(p.ty))
        }

        is Pat.Wild -> {
            f.foldPatWild(p)
        }

        is Pat.Verbatim -> {
            p.copy(tokens = f.foldTokenStream(p.tokens))
        }
    }

public fun foldItem(f: Fold, i: Item): Item =
    when (i) {
        is Item.Const -> {
            f.foldItemConst(i)
        }

        is Item.Enum -> {
            f.foldItemEnum(i)
        }

        is Item.ExternCrate -> {
            f.foldItemExternCrate(i)
        }

        is Item.Fn -> {
            f.foldItemFn(i)
        }

        is Item.ForeignMod -> {
            f.foldItemForeignMod(i)
        }

        is Item.Impl -> {
            f.foldItemImpl(i)
        }

        is Item.Macro -> {
            f.foldItemMacro(i)
        }

        is Item.Mod -> {
            f.foldItemMod(i)
        }

        is Item.Static -> {
            f.foldItemStatic(i)
        }

        is Item.Struct -> {
            f.foldItemStruct(i)
        }

        is Item.Trait -> {
            f.foldItemTrait(i)
        }

        is Item.TraitAlias -> {
            f.foldItemTraitAlias(i)
        }

        is Item.ItemType -> {
            f.foldItemType(i)
        }

        is Item.Union -> {
            f.foldItemUnion(i)
        }

        is Item.Use -> {
            f.foldItemUse(i)
        }

        is Item.Verbatim -> {
            i.copy(tokens = f.foldTokenStream(i.tokens))
        }
    }

public fun foldAttribute(f: Fold, a: Attribute): Attribute =
    a.copy(style = f.foldAttrStyle(a.style), meta = f.foldMeta(a.meta))

public fun foldAttrStyle(f: Fold, s: AttrStyle): AttrStyle =
    s

public fun foldMeta(f: Fold, m: Meta): Meta =
    when (m) {
        is Meta.PathMeta -> m.copy(path = f.foldPath(m.path))
        is Meta.List -> f.foldMetaList(m)
        is Meta.NameValue -> f.foldMetaNameValue(m)
    }

public fun foldMetaList(f: Fold, m: Meta.List): Meta.List {
    f.foldMacroDelimiter(m.delimiter)
    f.foldTokenStream(m.tokens)
    return m.copy(path = f.foldPath(m.path))
}

public fun foldMetaNameValue(f: Fold, m: Meta.NameValue): Meta.NameValue =
    m.copy(path = f.foldPath(m.path), value = f.foldExpr(m.value))

public fun foldGenerics(f: Fold, g: Generics): Generics =
    Generics(
        ltToken = g.ltToken,
        params = g.params.copy({ f.foldGenericParam(it) }, { it }),
        gtToken = g.gtToken,
        whereClause = g.whereClause?.let { f.foldWhereClause(it) },
    )

public fun foldLit(f: Fold, l: Lit): Lit =
    when (l) {
        is Lit.Str -> Lit.Str(f.foldLitStr(l.value))
        is Lit.ByteStr -> Lit.ByteStr(f.foldLitByteStr(l.value))
        is Lit.CStr -> Lit.CStr(f.foldLitCstr(l.value))
        is Lit.Byte -> Lit.Byte(f.foldLitByte(l.value))
        is Lit.Char -> Lit.Char(f.foldLitChar(l.value))
        is Lit.Int -> Lit.Int(f.foldLitInt(l.value))
        is Lit.Float -> Lit.Float(f.foldLitFloat(l.value))
        is Lit.Bool -> Lit.Bool(f.foldLitBool(l.value))
        is Lit.Verbatim -> l
    }

public fun foldLitBool(f: Fold, l: LitBool): LitBool {
    f.foldSpan(l.span())
    return l
}

public fun foldLitByte(f: Fold, l: LitByte): LitByte =
    l

public fun foldLitByteStr(f: Fold, l: LitByteStr): LitByteStr =
    l

public fun foldLitCstr(f: Fold, l: LitCStr): LitCStr =
    f.foldLitCStr(l)

public fun foldLitCStr(f: Fold, l: LitCStr): LitCStr =
    l

public fun foldLitChar(f: Fold, l: LitChar): LitChar =
    l

public fun foldLitFloat(f: Fold, l: LitFloat): LitFloat =
    l

public fun foldLitInt(f: Fold, l: LitInt): LitInt =
    l

public fun foldLitStr(f: Fold, l: LitStr): LitStr =
    l

public fun foldLifetime(f: Fold, lt: Lifetime): Lifetime =
    lt.copy(apostrophe = f.foldSpan(lt.apostrophe), ident = f.foldIdent(lt.ident))

public fun foldIdent(f: Fold, id: Ident): Ident {
    f.foldSpan(id.span())
    return id
}

public fun foldStmt(f: Fold, s: Stmt): Stmt =
    when (s) {
        is Stmt.Local -> f.foldLocal(s)
        is Stmt.ItemStmt -> s.copy(item = f.foldItem(s.item))
        is Stmt.ExprStmt -> s.copy(expr = f.foldExpr(s.expr))
        is Stmt.MacroStmt -> f.foldStmtMacro(s)
    }

public fun foldData(f: Fold, d: Data): Data =
    when (d) {
        is Data.Struct -> Data.Struct(f.foldDataStruct(d.value))
        is Data.Enum -> Data.Enum(f.foldDataEnum(d.value))
        is Data.Union -> Data.Union(f.foldDataUnion(d.value))
    }

public fun foldDataEnum(f: Fold, d: DataEnum): DataEnum =
    d.copy(variants = d.variants.copy({ f.foldVariant(it) }, { it }))

public fun foldDataStruct(f: Fold, d: DataStruct): DataStruct =
    d.copy(fields = f.foldFields(d.fields))

public fun foldDataUnion(f: Fold, d: DataUnion): DataUnion =
    d.copy(fields = f.foldFieldsNamed(d.fields))

public fun foldDeriveInput(f: Fold, di: DeriveInput): DeriveInput =
    di.copy(
        attrs = f.foldAttributes(di.attrs),
        vis = f.foldVisibility(di.vis),
        ident = f.foldIdent(di.ident),
        generics = f.foldGenerics(di.generics),
        data = f.foldData(di.data),
    )

public fun foldBlock(f: Fold, block: Block): Block =
    block.copy(stmts = block.stmts.mapTo(mutableListOf()) { f.foldStmt(it) })

public fun foldAttributes(f: Fold, attrs: List<Attribute>): List<Attribute> =
    attrs.mapTo(mutableListOf()) { f.foldAttribute(it) }

public fun foldFile(f: Fold, file: File): File =
    file.copy(
        frontmatter = file.frontmatter?.let { f.foldFrontmatter(it) },
        attrs = f.foldAttributes(file.attrs),
        items = file.items.mapTo(mutableListOf()) { f.foldItem(it) },
    )

public fun foldFrontmatter(f: Fold, frontmatter: Frontmatter): Frontmatter =
    frontmatter

public fun foldSignature(f: Fold, sig: Signature): Signature =
    sig.copy(
        safety = f.foldSafety(sig.safety),
        abi = sig.abi?.let { f.foldAbi(it) },
        ident = f.foldIdent(sig.ident),
        generics = f.foldGenerics(sig.generics),
        inputs = sig.inputs.copy({ f.foldFnArg(it) }, { it }),
        variadic = sig.variadic?.let { f.foldVariadic(it) },
        output = f.foldReturnType(sig.output),
    )

public fun foldSafety(f: Fold, safety: Safety): Safety =
    when (safety) {
        is Safety.Safe -> safety
        is Safety.Unsafe -> safety
        Safety.Default -> safety
    }

public fun foldAbi(f: Fold, a: Abi): Abi =
    a.copy(name = a.name?.let { f.foldLitStr(it) })

public fun foldReturnType(f: Fold, rt: ReturnType): ReturnType =
    when (rt) {
        is ReturnType.Default -> rt
        is ReturnType.TypeReturn -> rt.copy(ty = f.foldType(rt.ty))
    }

public fun foldFnArg(f: Fold, arg: FnArg): FnArg =
    when (arg) {
        is FnArg.Receiver -> f.foldReceiver(arg)
        is FnArg.Typed -> arg.copy(patType = f.foldPatType(arg.patType))
    }

public fun foldReceiver(f: Fold, receiver: FnArg.Receiver): FnArg.Receiver =
    receiver.copy(
        attrs = f.foldAttributes(receiver.attrs),
        kind = f.foldReceiverKind(receiver.kind),
    )

public fun foldReceiverKind(f: Fold, kind: ReceiverKind): ReceiverKind =
    when (kind) {
        ReceiverKind.Value -> kind
        is ReceiverKind.Reference -> kind.copy(lifetime = kind.lifetime?.let { f.foldLifetime(it) })
        is ReceiverKind.Typed -> kind.copy(ty = f.foldType(kind.ty))
    }

public fun foldPatType(f: Fold, patType: PatType): PatType =
    patType.copy(
        attrs = f.foldAttributes(patType.attrs),
        pat = f.foldPat(patType.pat),
        ty = f.foldType(patType.ty),
    )

public fun foldPatGuard(f: Fold, pat: Pat.Guard): Pat.Guard =
    pat.copy(
        attrs = f.foldAttributes(pat.attrs),
        pat = f.foldPat(pat.pat),
        guard = f.foldExpr(pat.guard),
    )

public fun foldPatIdent(f: Fold, patIdent: Pat.Ident): Pat.Ident =
    patIdent.copy(
        attrs = f.foldAttributes(patIdent.attrs),
        ident = f.foldIdent(patIdent.ident),
        subpat = patIdent.subpat?.let { f.foldPat(it) },
    )

public fun foldPatOr(f: Fold, pat: Pat.Or): Pat.Or =
    pat.copy(attrs = f.foldAttributes(pat.attrs), cases = pat.cases.copy({ f.foldPat(it) }, { it }))

public fun foldPatParen(f: Fold, pat: Pat.PatParen): Pat.PatParen =
    pat.copy(attrs = f.foldAttributes(pat.attrs), pat = f.foldPat(pat.pat))

public fun foldPatRange(f: Fold, pat: Pat.Range): Pat.Range =
    pat.copy(
        attrs = f.foldAttributes(pat.attrs),
        start = pat.start?.let { f.foldExpr(it) },
        limits = f.foldRangeLimits(pat.limits),
        end = pat.end?.let { f.foldExpr(it) },
    )

public fun foldPatReference(f: Fold, pat: Pat.Reference): Pat.Reference =
    pat.copy(attrs = f.foldAttributes(pat.attrs), pat = f.foldPat(pat.pat))

public fun foldPatRest(f: Fold, pat: Pat.Rest): Pat.Rest =
    pat.copy(attrs = f.foldAttributes(pat.attrs))

public fun foldPatRest(f: Fold, patRest: PatRest): PatRest =
    patRest.copy(attrs = f.foldAttributes(patRest.attrs))

public fun foldPatSlice(f: Fold, pat: Pat.Slice): Pat.Slice =
    pat.copy(attrs = f.foldAttributes(pat.attrs), elems = pat.elems.copy({ f.foldPat(it) }, { it }))

public fun foldPatStruct(f: Fold, pat: Pat.Struct): Pat.Struct =
    pat.copy(
        attrs = f.foldAttributes(pat.attrs),
        qself = pat.qself?.let { f.foldQself(it) },
        path = f.foldPath(pat.path),
        fields = pat.fields.copy({ f.foldFieldPat(it) }, { it }),
        rest = pat.rest?.let { f.foldPatRest(it) },
    )

public fun foldPatTuple(f: Fold, pat: Pat.Tuple): Pat.Tuple =
    pat.copy(attrs = f.foldAttributes(pat.attrs), elems = pat.elems.copy({ f.foldPat(it) }, { it }))

public fun foldPatTupleStruct(f: Fold, pat: Pat.TupleStruct): Pat.TupleStruct =
    pat.copy(
        attrs = f.foldAttributes(pat.attrs),
        qself = pat.qself?.let { f.foldQself(it) },
        path = f.foldPath(pat.path),
        elems = pat.elems.copy({ f.foldPat(it) }, { it }),
    )

public fun foldPatWild(f: Fold, pat: Pat.Wild): Pat.Wild =
    pat.copy(attrs = f.foldAttributes(pat.attrs))

public fun foldTypePath(f: Fold, typePath: SynType.Path): SynType.Path =
    typePath.copy(
        attrs = f.foldAttributes(typePath.attrs),
        qself = typePath.qself?.let { f.foldQself(it) },
        path = f.foldPath(typePath.path),
    )

public fun foldTypeArray(f: Fold, ty: SynType.Array): SynType.Array =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        elem = f.foldType(ty.elem),
        len = f.foldExpr(ty.len),
    )

public fun foldTypeGroup(f: Fold, ty: SynType.Group): SynType.Group =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        elem = f.foldType(ty.elem),
    )

public fun foldTypeReference(f: Fold, ty: SynType.Reference): SynType.Reference =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        lifetime = ty.lifetime?.let { f.foldLifetime(it) },
        elem = f.foldType(ty.elem),
    )

public fun foldTypeImplTrait(f: Fold, ty: SynType.ImplTrait): SynType.ImplTrait =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        bounds = ty.bounds.copy({ f.foldTypeParamBound(it) }, { it }),
    )

public fun foldTypeInfer(f: Fold, ty: SynType.Infer): SynType.Infer =
    ty.copy(attrs = f.foldAttributes(ty.attrs))

public fun foldTypeMacro(f: Fold, ty: SynType.Macro): SynType.Macro =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        mac = f.foldMacro(ty.mac),
    )

public fun foldTypeNever(f: Fold, ty: SynType.Never): SynType.Never =
    ty.copy(attrs = f.foldAttributes(ty.attrs))

public fun foldTypePtr(f: Fold, ty: SynType.Ptr): SynType.Ptr =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        mutability = f.foldPointerMutability(ty.mutability),
        elem = f.foldType(ty.elem),
    )

public fun foldTypeSlice(f: Fold, ty: SynType.Slice): SynType.Slice =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        elem = f.foldType(ty.elem),
    )

public fun foldTypeTraitObject(f: Fold, ty: SynType.TraitObject): SynType.TraitObject =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        bounds = ty.bounds.copy({ f.foldTypeParamBound(it) }, { it }),
    )

public fun foldTypeTuple(f: Fold, ty: SynType.Tuple): SynType.Tuple =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        elems = ty.elems.copy({ f.foldType(it) }, { it }),
    )

public fun foldPointerMutability(f: Fold, mutability: PointerMutability): PointerMutability =
    when (mutability) {
        is PointerMutability.Const -> mutability
        is PointerMutability.Mut -> mutability
    }

public fun foldTypeFnPtr(f: Fold, ty: SynType.FnPtr): SynType.FnPtr =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        lifetimes = ty.lifetimes?.let { f.foldBoundLifetimes(it) },
        abi = ty.abi?.let { f.foldAbi(it) },
        inputs = ty.inputs.copy({ f.foldNamedArg(it) }, { it }),
        variadic = ty.variadic?.let { f.foldFnPtrVariadic(it) },
        output = f.foldReturnType(ty.output),
    )

public fun foldNamedArg(f: Fold, arg: NamedArg): NamedArg {
    val name = arg.name
    return arg.copy(
        attrs = f.foldAttributes(arg.attrs),
        name = name?.copy(ident = f.foldIdent(name.ident)),
        ty = f.foldType(arg.ty),
    )
}

public fun foldFnPtrVariadic(f: Fold, variadic: FnPtrVariadic): FnPtrVariadic {
    val name = variadic.name
    return variadic.copy(
        attrs = f.foldAttributes(variadic.attrs),
        name = name?.copy(ident = f.foldIdent(name.ident)),
    )
}

public fun foldTypeParen(f: Fold, ty: SynType.Paren): SynType.Paren =
    ty.copy(
        attrs = f.foldAttributes(ty.attrs),
        elem = f.foldType(ty.elem),
    )

public fun foldExprArray(f: Fold, expr: Expr.Array): Expr.Array =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        elems = expr.elems.copy({ f.foldExpr(it) }, { it }),
    )

public fun foldExprAssign(f: Fold, expr: Expr.Assign): Expr.Assign =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        left = f.foldExpr(expr.left),
        right = f.foldExpr(expr.right),
    )

public fun foldExprAsync(f: Fold, expr: Expr.Async): Expr.Async =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        block = f.foldBlock(expr.block),
    )

public fun foldExprAwait(f: Fold, expr: Expr.Await): Expr.Await =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        base = f.foldExpr(expr.base),
    )

public fun foldExprBinary(f: Fold, expr: Expr.Binary): Expr.Binary =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        left = f.foldExpr(expr.left),
        op = f.foldBinOp(expr.op),
        right = f.foldExpr(expr.right),
    )

public fun foldExprBlock(f: Fold, expr: Expr.BlockExpr): Expr.BlockExpr =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        label = expr.label?.let { f.foldLabel(it) },
        block = f.foldBlock(expr.block),
    )

public fun foldExprBreak(f: Fold, expr: Expr.Break): Expr.Break =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        label = expr.label?.let { f.foldLifetime(it) },
        expr = expr.expr?.let { f.foldExpr(it) },
    )

public fun foldExprCall(f: Fold, expr: Expr.Call): Expr.Call =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        func = f.foldExpr(expr.func),
        args = expr.args.copy({ f.foldExpr(it) }, { it }),
    )

public fun foldExprCast(f: Fold, expr: Expr.Cast): Expr.Cast =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
        ty = f.foldType(expr.ty),
    )

public fun foldExprClosure(f: Fold, expr: Expr.Closure): Expr.Closure =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        lifetimes = expr.lifetimes?.let { f.foldBoundLifetimes(it) },
        inputs = expr.inputs.copy({ f.foldPat(it) }, { it }),
        output = f.foldReturnType(expr.output),
        body = f.foldExpr(expr.body),
    )

public fun foldExprConst(f: Fold, expr: Expr.Const): Expr.Const =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        block = f.foldBlock(expr.block),
    )

public fun foldExprContinue(f: Fold, expr: Expr.Continue): Expr.Continue =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        label = expr.label?.let { f.foldLifetime(it) },
    )

public fun foldExprField(f: Fold, expr: Expr.Field): Expr.Field =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        base = f.foldExpr(expr.base),
        member = f.foldMember(expr.member),
    )

public fun foldExprForLoop(f: Fold, expr: Expr.ForLoop): Expr.ForLoop =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        label = expr.label?.let { f.foldLabel(it) },
        pat = f.foldPat(expr.pat),
        expr = f.foldExpr(expr.expr),
        body = f.foldBlock(expr.body),
    )

public fun foldExprGroup(f: Fold, expr: Expr.Group): Expr.Group =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
    )

public fun foldExprIf(f: Fold, expr: Expr.If): Expr.If =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        cond = f.foldExpr(expr.cond),
        thenBranch = f.foldBlock(expr.thenBranch),
        elseBranch = expr.elseBranch?.let { f.foldElseExpr(it) },
    )

public fun foldExprIndex(f: Fold, expr: Expr.Index): Expr.Index =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
        index = f.foldExpr(expr.index),
    )

public fun foldExprInfer(f: Fold, expr: Expr.Infer): Expr.Infer =
    expr.copy(attrs = f.foldAttributes(expr.attrs))

public fun foldExprLet(f: Fold, expr: Expr.Let): Expr.Let =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        pat = f.foldPat(expr.pat),
        expr = f.foldExpr(expr.expr),
    )

public fun foldExprLit(f: Fold, expr: Expr.Lit): Expr.Lit =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        lit = f.foldLit(expr.lit),
    )

public fun foldExprLoop(f: Fold, expr: Expr.Loop): Expr.Loop =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        label = expr.label?.let { f.foldLabel(it) },
        body = f.foldBlock(expr.body),
    )

public fun foldExprMacro(f: Fold, expr: Expr.Macro): Expr.Macro =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        mac = f.foldMacro(expr.mac),
    )

public fun foldExprMatch(f: Fold, expr: Expr.Match): Expr.Match =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
        arms = expr.arms.mapTo(mutableListOf()) { f.foldArm(it) },
    )

public fun foldExprMethodCall(f: Fold, expr: Expr.MethodCall): Expr.MethodCall =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        receiver = f.foldExpr(expr.receiver),
        method = f.foldIdent(expr.method),
        turbofish = expr.turbofish?.let { f.foldAngleBracketedGenericArguments(it) },
        args = expr.args.copy({ f.foldExpr(it) }, { it }),
    )

public fun foldExprParen(f: Fold, expr: Expr.Paren): Expr.Paren =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
    )

public fun foldExprPath(f: Fold, exprPath: Expr.Path): Expr.Path =
    exprPath.copy(
        attrs = f.foldAttributes(exprPath.attrs),
        qself = exprPath.qself?.let { f.foldQself(it) },
        path = f.foldPath(exprPath.path),
    )

public fun foldExprRange(f: Fold, expr: Expr.Range): Expr.Range =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        start = expr.start?.let { f.foldExpr(it) },
        limits = f.foldRangeLimits(expr.limits),
        end = expr.end?.let { f.foldExpr(it) },
    )

public fun foldExprRawAddr(f: Fold, expr: Expr.RawAddr): Expr.RawAddr =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        mutability = f.foldPointerMutability(expr.mutability),
        expr = f.foldExpr(expr.expr),
    )

public fun foldExprReference(f: Fold, expr: Expr.Reference): Expr.Reference =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
    )

public fun foldExprRepeat(f: Fold, expr: Expr.Repeat): Expr.Repeat =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
        len = f.foldExpr(expr.len),
    )

public fun foldExprReturn(f: Fold, expr: Expr.Return): Expr.Return =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = expr.expr?.let { f.foldExpr(it) },
    )

public fun foldExprStruct(f: Fold, expr: Expr.Struct): Expr.Struct =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        qself = expr.qself?.let { f.foldQself(it) },
        path = f.foldPath(expr.path),
        fields = expr.fields.copy({ f.foldFieldValue(it) }, { it }),
        rest = expr.rest?.let { f.foldExpr(it) },
    )

public fun foldExprTry(f: Fold, expr: Expr.Try): Expr.Try =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = f.foldExpr(expr.expr),
    )

public fun foldExprTryBlock(f: Fold, expr: Expr.TryBlock): Expr.TryBlock =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        block = f.foldBlock(expr.block),
    )

public fun foldExprTuple(f: Fold, expr: Expr.Tuple): Expr.Tuple =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        elems = expr.elems.copy({ f.foldExpr(it) }, { it }),
    )

public fun foldExprUnary(f: Fold, expr: Expr.Unary): Expr.Unary =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        op = f.foldUnOp(expr.op),
        expr = f.foldExpr(expr.expr),
    )

public fun foldExprUnsafe(f: Fold, expr: Expr.Unsafe): Expr.Unsafe =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        block = f.foldBlock(expr.block),
    )

public fun foldExprWhile(f: Fold, expr: Expr.While): Expr.While =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        label = expr.label?.let { f.foldLabel(it) },
        cond = f.foldExpr(expr.cond),
        body = f.foldBlock(expr.body),
    )

public fun foldExprYield(f: Fold, expr: Expr.Yield): Expr.Yield =
    expr.copy(
        attrs = f.foldAttributes(expr.attrs),
        expr = expr.expr?.let { f.foldExpr(it) },
    )

public fun foldBinOp(f: Fold, op: BinOp): BinOp =
    when (op) {
        is BinOp.Add -> op
        is BinOp.Sub -> op
        is BinOp.Mul -> op
        is BinOp.Div -> op
        is BinOp.Rem -> op
        is BinOp.And -> op
        is BinOp.Or -> op
        is BinOp.BitXor -> op
        is BinOp.BitAnd -> op
        is BinOp.BitOr -> op
        is BinOp.Shl -> op
        is BinOp.Shr -> op
        is BinOp.Eq -> op
        is BinOp.Lt -> op
        is BinOp.Le -> op
        is BinOp.Ne -> op
        is BinOp.Ge -> op
        is BinOp.Gt -> op
        is BinOp.AddAssign -> op
        is BinOp.SubAssign -> op
        is BinOp.MulAssign -> op
        is BinOp.DivAssign -> op
        is BinOp.RemAssign -> op
        is BinOp.BitXorAssign -> op
        is BinOp.BitAndAssign -> op
        is BinOp.BitOrAssign -> op
        is BinOp.ShlAssign -> op
        is BinOp.ShrAssign -> op
    }

public fun foldUnOp(f: Fold, op: UnOp): UnOp =
    when (op) {
        is UnOp.Deref -> op
        is UnOp.NotOp -> op
        is UnOp.Neg -> op
    }

public fun foldLabel(f: Fold, label: Label): Label =
    label.copy(name = f.foldLifetime(label.name))

public fun foldRangeLimits(f: Fold, limits: RangeLimits): RangeLimits =
    when (limits) {
        is RangeLimits.HalfOpen -> limits
        is RangeLimits.Closed -> limits
    }

public fun foldMacro(f: Fold, mac: Macro): Macro =
    mac.copy(path = f.foldPath(mac.path), delimiter = f.foldMacroDelimiter(mac.delimiter), tokens = f.foldTokenStream(mac.tokens))

public fun foldMacroDelimiter(f: Fold, delimiter: MacroDelimiter): MacroDelimiter =
    delimiter

public fun foldPathArguments(f: Fold, pathArgs: PathArguments): PathArguments =
    when (pathArgs) {
        is PathArguments.None -> pathArgs
        is PathArguments.AngleBracketed -> f.foldAngleBracketedGenericArguments(pathArgs)
        is PathArguments.Parenthesized -> f.foldParenthesizedGenericArguments(pathArgs)
    }

public fun foldAngleBracketedGenericArguments(f: Fold, pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed =
    pathArgs.copy(args = pathArgs.args.copy({ f.foldGenericArgument(it) }, { it }))

public fun foldParenthesizedGenericArguments(f: Fold, pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized =
    pathArgs.copy(
        inputs = pathArgs.inputs.copy({ f.foldNamedArg(it) }, { it }),
        output = f.foldReturnType(pathArgs.output),
    )

public fun foldGenericArgument(f: Fold, genArg: GenericArgument): GenericArgument =
    when (genArg) {
        is GenericArgument.LifetimeArg -> genArg.copy(lifetime = f.foldLifetime(genArg.lifetime))
        is GenericArgument.TypeArg -> genArg.copy(type = f.foldType(genArg.type))
        is GenericArgument.ConstArg -> genArg.copy(expr = f.foldExpr(genArg.expr))
        is GenericArgument.AssocTypeArg -> genArg.copy(assoc = f.foldAssocType(genArg.assoc))
        is GenericArgument.AssocConstArg -> genArg.copy(assoc = f.foldAssocConst(genArg.assoc))
        is GenericArgument.ConstraintArg -> genArg.copy(constraint = f.foldConstraint(genArg.constraint))
    }

public fun foldAssocConst(f: Fold, assoc: AssocConst): AssocConst =
    assoc.copy(
        ident = f.foldIdent(assoc.ident),
        generics = assoc.generics?.let { f.foldAngleBracketedGenericArguments(it) },
        value = f.foldExpr(assoc.value),
    )

public fun foldAssocType(f: Fold, assoc: AssocType): AssocType =
    assoc.copy(
        ident = f.foldIdent(assoc.ident),
        generics = assoc.generics?.let { f.foldAngleBracketedGenericArguments(it) },
        ty = f.foldType(assoc.ty),
    )

public fun foldConstraint(f: Fold, constraint: Constraint): Constraint =
    constraint.copy(
        ident = f.foldIdent(constraint.ident),
        generics = constraint.generics?.let { f.foldAngleBracketedGenericArguments(it) },
        bounds = constraint.bounds.copy({ f.foldTypeParamBound(it) }, { it }),
    )

public fun foldTypeParamBound(f: Fold, bound: TypeParamBound): TypeParamBound =
    when (bound) {
        is TypeParamBound.Trait -> {
            f.foldTraitBound(bound)
        }

        is TypeParamBound.LifetimeBound -> {
            bound.copy(lifetime = f.foldLifetime(bound.lifetime))
        }

        is TypeParamBound.PreciseCapture -> {
            f.foldPreciseCapture(bound)
        }

        is TypeParamBound.Verbatim -> {
            bound.copy(tokens = f.foldTokenStream(bound.tokens))
        }
    }

public fun foldTraitBound(f: Fold, bound: TypeParamBound.Trait): TypeParamBound.Trait =
    bound.copy(
        lifetimes = bound.lifetimes?.let { f.foldBoundLifetimes(it) },
        path = f.foldPath(bound.path),
    )

public fun foldBoundLifetimes(f: Fold, boundLifetimes: BoundLifetimes): BoundLifetimes =
    boundLifetimes.copy(lifetimes = boundLifetimes.lifetimes.copy({ f.foldGenericParam(it) }, { it }))

public fun foldCapturedParam(f: Fold, param: CapturedParam): CapturedParam =
    when (param) {
        is CapturedParam.Lifetime -> param.copy(lifetime = f.foldLifetime(param.lifetime))
        is CapturedParam.Ident -> param.copy(ident = f.foldIdent(param.ident))
    }

public fun foldPreciseCapture(f: Fold, preciseCapture: TypeParamBound.PreciseCapture): TypeParamBound.PreciseCapture =
    preciseCapture.copy(params = preciseCapture.params.copy({ f.foldCapturedParam(it) }, { it }))

public fun foldPathSegment(f: Fold, segment: PathSegment): PathSegment =
    segment.copy(ident = f.foldIdent(segment.ident), arguments = f.foldPathArguments(segment.arguments))

public fun foldArm(f: Fold, arm: Arm): Arm =
    arm.copy(
        attrs = f.foldAttributes(arm.attrs),
        pat = f.foldPat(arm.pat),
        body = f.foldExpr(arm.body),
    )

public fun foldElseExpr(f: Fold, elseExpr: ElseExpr): ElseExpr =
    elseExpr.copy(expr = f.foldExpr(elseExpr.expr))

public fun foldFieldPat(f: Fold, fieldPat: FieldPat): FieldPat =
    fieldPat.copy(
        attrs = f.foldAttributes(fieldPat.attrs),
        member = f.foldMember(fieldPat.member),
        pat = f.foldPat(fieldPat.pat),
    )

public fun foldFieldValue(f: Fold, fieldValue: FieldValue): FieldValue =
    fieldValue.copy(
        attrs = f.foldAttributes(fieldValue.attrs),
        member = f.foldMember(fieldValue.member),
        expr = f.foldExpr(fieldValue.expr),
    )

public fun foldGenericParam(f: Fold, param: GenericParam): GenericParam =
    when (param) {
        is GenericParam.LifetimeParam -> f.foldLifetimeParam(param)
        is GenericParam.TypeParam -> f.foldTypeParam(param)
        is GenericParam.ConstParam -> f.foldConstParam(param)
    }

public fun foldConstParam(f: Fold, param: GenericParam.ConstParam): GenericParam.ConstParam =
    param.copy(
        attrs = f.foldAttributes(param.attrs),
        ident = f.foldIdent(param.ident),
        ty = f.foldType(param.ty),
        default = param.default?.let { it.copy(expr = f.foldExpr(it.expr)) },
    )

public fun foldLifetimeParam(f: Fold, param: GenericParam.LifetimeParam): GenericParam.LifetimeParam =
    param.copy(
        attrs = f.foldAttributes(param.attrs),
        lifetime = f.foldLifetime(param.lifetime),
        bounds = param.bounds.copy({ f.foldLifetime(it) }, { it }),
    )

public fun foldTypeParam(f: Fold, param: GenericParam.TypeParam): GenericParam.TypeParam =
    param.copy(
        attrs = f.foldAttributes(param.attrs),
        ident = f.foldIdent(param.ident),
        bounds = param.bounds.copy({ f.foldTypeParamBound(it) }, { it }),
        default = param.default?.let { it.copy(type = f.foldType(it.type)) },
    )

public fun foldField(f: Fold, field: Field): Field =
    field.copy(
        attrs = f.foldAttributes(field.attrs),
        vis = f.foldVisibility(field.vis),
        ident = field.ident?.let { f.foldIdent(it) },
        ty = f.foldType(field.ty),
        default = field.default?.let { it.copy(expr = f.foldExpr(it.expr)) },
    )

public fun foldFields(f: Fold, fields: Fields): Fields =
    when (fields) {
        is Fields.Named -> Fields.Named(f.foldFieldsNamed(fields.fields))
        is Fields.Unnamed -> Fields.Unnamed(f.foldFieldsUnnamed(fields.fields))
        Fields.Unit -> fields
    }

public fun foldFieldsNamed(f: Fold, fields: FieldsNamed): FieldsNamed =
    fields.copy(named = fields.named.copy({ f.foldField(it) }, { it }))

public fun foldFieldsUnnamed(f: Fold, fields: FieldsUnnamed): FieldsUnnamed =
    fields.copy(unnamed = fields.unnamed.copy({ f.foldField(it) }, { it }))

public fun foldImplItem(f: Fold, item: ImplItem): ImplItem =
    when (item) {
        is ImplItem.Const -> {
            f.foldImplItemConst(item)
        }

        is ImplItem.Fn -> {
            f.foldImplItemFn(item)
        }

        is ImplItem.AssocType -> {
            f.foldImplItemType(item)
        }

        is ImplItem.Macro -> {
            f.foldImplItemMacro(item)
        }

        is ImplItem.Verbatim -> {
            item.copy(tokens = f.foldTokenStream(item.tokens))
        }
    }

public fun foldForeignItem(f: Fold, item: ForeignItem): ForeignItem =
    when (item) {
        is ForeignItem.Fn -> f.foldForeignItemFn(item)
        is ForeignItem.Static -> f.foldForeignItemStatic(item)
        is ForeignItem.ItemType -> f.foldForeignItemType(item)
        is ForeignItem.Macro -> f.foldForeignItemMacro(item)
        is ForeignItem.Verbatim -> ForeignItem.Verbatim(f.foldTokenStream(item.tokens))
    }

public fun foldForeignItemFn(f: Fold, item: ForeignItem.Fn): ForeignItem.Fn =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        sig = f.foldSignature(item.sig),
    )

public fun foldForeignItemMacro(f: Fold, item: ForeignItem.Macro): ForeignItem.Macro =
    item.copy(attrs = f.foldAttributes(item.attrs), mac = f.foldMacro(item.mac))

public fun foldForeignItemStatic(f: Fold, item: ForeignItem.Static): ForeignItem.Static =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        safety = f.foldSafety(item.safety),
        mutability = f.foldStaticMutability(item.mutability),
        ident = f.foldIdent(item.ident),
        ty = f.foldType(item.ty),
    )

public fun foldForeignItemType(f: Fold, item: ForeignItem.ItemType): ForeignItem.ItemType =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
    )

public fun foldImplItemConst(f: Fold, item: ImplItem.Const): ImplItem.Const =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        ty = f.foldType(item.ty),
        expr = f.foldExpr(item.expr),
    )

public fun foldImplItemFn(f: Fold, item: ImplItem.Fn): ImplItem.Fn =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        sig = f.foldSignature(item.sig),
        block = f.foldBlock(item.block),
    )

public fun foldImplItemMacro(f: Fold, item: ImplItem.Macro): ImplItem.Macro =
    item.copy(attrs = f.foldAttributes(item.attrs), mac = f.foldMacro(item.mac))

public fun foldImplItemType(f: Fold, item: ImplItem.AssocType): ImplItem.AssocType =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        ty = f.foldType(item.ty),
    )

public fun foldItemConst(f: Fold, item: Item.Const): Item.Const =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        ty = f.foldType(item.ty),
        expr = f.foldExpr(item.expr),
    )

public fun foldItemEnum(f: Fold, item: Item.Enum): Item.Enum =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        variants = item.variants.copy({ f.foldVariant(it) }, { it }),
    )

public fun foldItemExternCrate(f: Fold, item: Item.ExternCrate): Item.ExternCrate =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        rename = item.rename?.let { it.copy(ident = f.foldIdent(it.ident)) },
    )

public fun foldItemFn(f: Fold, item: Item.Fn): Item.Fn =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        sig = f.foldSignature(item.sig),
        block = f.foldBlock(item.block),
    )

public fun foldItemForeignMod(f: Fold, item: Item.ForeignMod): Item.ForeignMod =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        abi = f.foldAbi(item.abi),
        items = item.items.mapTo(mutableListOf()) { f.foldForeignItem(it) },
    )

public fun foldItemImpl(f: Fold, item: Item.Impl): Item.Impl =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        generics = f.foldGenerics(item.generics),
        traitPath = item.traitPath?.let { it.copy(path = f.foldPath(it.path)) },
        selfType = f.foldType(item.selfType),
        items = item.items.mapTo(mutableListOf()) { f.foldImplItem(it) },
    )

public fun foldItemMacro(f: Fold, item: Item.Macro): Item.Macro =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        ident = item.ident?.let { f.foldIdent(it) },
        mac = f.foldMacro(item.mac),
    )

public fun foldItemMod(f: Fold, item: Item.Mod): Item.Mod =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        content = item.content?.let { f.foldModContent(it) },
    )

public fun foldItemStatic(f: Fold, item: Item.Static): Item.Static =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        mutability = f.foldStaticMutability(item.mutability),
        ident = f.foldIdent(item.ident),
        ty = f.foldType(item.ty),
        expr = f.foldExpr(item.expr),
    )

public fun foldItemStruct(f: Fold, item: Item.Struct): Item.Struct =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        fields = f.foldFields(item.fields),
    )

public fun foldItemTrait(f: Fold, item: Item.Trait): Item.Trait =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        supertraits = item.supertraits.copy({ f.foldTypeParamBound(it) }, { it }),
        items = item.items.mapTo(mutableListOf()) { f.foldTraitItem(it) },
    )

public fun foldItemTraitAlias(f: Fold, item: Item.TraitAlias): Item.TraitAlias =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        bounds = item.bounds.copy({ f.foldTypeParamBound(it) }, { it }),
    )

public fun foldItemType(f: Fold, item: Item.ItemType): Item.ItemType =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        ty = f.foldType(item.ty),
        whereClausePlacement = f.foldWhereClausePlacement(item.whereClausePlacement),
    )

public fun foldItemUnion(f: Fold, item: Item.Union): Item.Union =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        fields = f.foldFieldsNamed(item.fields),
    )

public fun foldItemUse(f: Fold, item: Item.Use): Item.Use =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        vis = f.foldVisibility(item.vis),
        tree = f.foldUseTree(item.tree),
    )

public fun foldStaticMutability(f: Fold, mutability: StaticMutability): StaticMutability =
    mutability

public fun foldModContent(f: Fold, modContent: ModContent): ModContent =
    when (modContent) {
        is ModContent.Inline -> modContent.copy(items = modContent.items.mapTo(mutableListOf()) { f.foldItem(it) })
        is ModContent.Unnamed -> modContent
    }

public fun foldLocal(f: Fold, local: Stmt.Local): Stmt.Local =
    local.copy(
        attrs = f.foldAttributes(local.attrs),
        pat = f.foldPat(local.pat),
        init = local.init?.let { f.foldLocalInit(it) },
    )

public fun foldLocalInit(f: Fold, init: LocalInit): LocalInit =
    init.copy(
        expr = f.foldExpr(init.expr),
        diverge = init.diverge?.let { f.foldElseExpr(it) },
    )

public fun foldStmtMacro(f: Fold, stmt: Stmt.MacroStmt): Stmt.MacroStmt =
    stmt.copy(attrs = f.foldAttributes(stmt.attrs), mac = f.foldMacro(stmt.mac))

public fun foldMember(f: Fold, member: Member): Member =
    when (member) {
        is Member.Named -> member.copy(ident = f.foldIdent(member.ident))
        is Member.Unnamed -> member.copy(index = f.foldIndex(member.index))
    }

public fun foldIndex(f: Fold, index: Index): Index =
    index.copy(span = f.foldSpan(index.span))

public fun foldQself(f: Fold, qself: QSelf): QSelf =
    f.foldQSelf(qself)

public fun foldQSelf(f: Fold, qself: QSelf): QSelf =
    qself.copy(ty = f.foldType(qself.ty))

public fun foldTraitItem(f: Fold, item: TraitItem): TraitItem =
    when (item) {
        is TraitItem.Const -> {
            f.foldTraitItemConst(item)
        }

        is TraitItem.Fn -> {
            f.foldTraitItemFn(item)
        }

        is TraitItem.AssocType -> {
            f.foldTraitItemType(item)
        }

        is TraitItem.Macro -> {
            f.foldTraitItemMacro(item)
        }

        is TraitItem.Verbatim -> {
            item.copy(tokens = f.foldTokenStream(item.tokens))
        }
    }

public fun foldTraitItemConst(f: Fold, item: TraitItem.Const): TraitItem.Const {
    val default = item.default
    return item.copy(
        attrs = f.foldAttributes(item.attrs),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        ty = f.foldType(item.ty),
        default = default?.copy(expr = f.foldExpr(default.expr)),
    )
}

public fun foldTraitItemFn(f: Fold, item: TraitItem.Fn): TraitItem.Fn =
    item.copy(
        attrs = f.foldAttributes(item.attrs),
        sig = f.foldSignature(item.sig),
        default = item.default?.let { f.foldBlock(it) },
    )

public fun foldTraitItemMacro(f: Fold, item: TraitItem.Macro): TraitItem.Macro =
    item.copy(attrs = f.foldAttributes(item.attrs), mac = f.foldMacro(item.mac))

public fun foldTraitItemType(f: Fold, item: TraitItem.AssocType): TraitItem.AssocType {
    val default = item.default
    return item.copy(
        attrs = f.foldAttributes(item.attrs),
        ident = f.foldIdent(item.ident),
        generics = f.foldGenerics(item.generics),
        bounds = item.bounds.copy({ f.foldTypeParamBound(it) }, { it }),
        default = default?.copy(type = f.foldType(default.type)),
    )
}

public fun foldUseTree(f: Fold, useTree: UseTree): UseTree =
    when (useTree) {
        is UseTree.Path -> {
            f.foldUsePath(useTree)
        }

        is UseTree.Name -> {
            if (useTree.rename == null) {
                f.foldUseName(useTree)
            } else {
                f.foldUseRename(useTree)
            }
        }

        is UseTree.Group -> {
            f.foldUseGroup(useTree)
        }

        is UseTree.Glob -> {
            f.foldUseGlob(useTree)
        }
    }

public fun foldUseGlob(f: Fold, useTree: UseTree.Glob): UseTree.Glob =
    useTree

public fun foldUseGroup(f: Fold, useTree: UseTree.Group): UseTree.Group =
    useTree.copy(items = useTree.items.copy({ f.foldUseTree(it) }, { it }))

public fun foldUseName(f: Fold, useTree: UseTree.Name): UseTree.Name =
    useTree.copy(ident = f.foldIdent(useTree.ident))

public fun foldUsePath(f: Fold, useTree: UseTree.Path): UseTree.Path =
    useTree.copy(
        ident = f.foldIdent(useTree.ident),
        tree = useTree.tree?.let { f.foldUseTree(it) },
    )

public fun foldUseRename(f: Fold, useTree: UseTree.Name): UseTree.Name {
    val rename = useTree.rename
    return useTree.copy(
        ident = f.foldIdent(useTree.ident),
        rename = rename?.copy(ident = f.foldIdent(rename.ident)),
    )
}

public fun foldVariadic(f: Fold, variadic: Variadic): Variadic {
    val pat = variadic.pat
    return variadic.copy(
        attrs = f.foldAttributes(variadic.attrs),
        pat = pat?.copy(pat = f.foldPat(pat.pat)),
    )
}

public fun foldVariant(f: Fold, variant: Variant): Variant {
    val discriminant = variant.discriminant
    return variant.copy(
        attrs = f.foldAttributes(variant.attrs),
        ident = f.foldIdent(variant.ident),
        fields = f.foldFields(variant.fields),
        discriminant = discriminant?.copy(expr = f.foldExpr(discriminant.expr)),
    )
}

public fun foldVisibility(f: Fold, visibility: Visibility): Visibility =
    when (visibility) {
        is Visibility.Public -> visibility
        is Visibility.Restricted -> f.foldVisRestricted(visibility)
        Visibility.Inherited -> visibility
    }

public fun foldVisRestricted(f: Fold, visibility: Visibility.Restricted): Visibility.Restricted =
    visibility.copy(path = f.foldPath(visibility.path))

public fun foldWhereClause(f: Fold, whereClause: WhereClause): WhereClause =
    whereClause.copy(predicates = whereClause.predicates.copy({ f.foldWherePredicate(it) }, { it }))

public fun foldWhereClausePlacement(f: Fold, placement: WhereClausePlacement): WhereClausePlacement =
    when (placement) {
        WhereClausePlacement.Early -> placement
        WhereClausePlacement.Late -> placement
    }

public fun foldWherePredicate(f: Fold, wherePredicate: WherePredicate): WherePredicate =
    when (wherePredicate) {
        is WherePredicate.LifetimePredicate -> f.foldPredicateLifetime(wherePredicate)
        is WherePredicate.TypePredicate -> f.foldPredicateType(wherePredicate)
    }

public fun foldPredicateLifetime(f: Fold, predicate: WherePredicate.LifetimePredicate): WherePredicate.LifetimePredicate =
    predicate.copy(
        attrs = f.foldAttributes(predicate.attrs),
        lifetime = f.foldLifetime(predicate.lifetime),
        bounds = predicate.bounds.copy({ f.foldLifetime(it) }, { it }),
    )

public fun foldPredicateType(f: Fold, predicate: WherePredicate.TypePredicate): WherePredicate.TypePredicate =
    predicate.copy(
        attrs = f.foldAttributes(predicate.attrs),
        lifetimes = predicate.lifetimes?.let { f.foldBoundLifetimes(it) },
        boundedTy = f.foldType(predicate.boundedTy),
        bounds = predicate.bounds.copy({ f.foldTypeParamBound(it) }, { it }),
    )

public fun foldSpan(f: Fold, span: Span): Span =
    span

public fun foldTokenStream(f: Fold, tokens: TokenStream): TokenStream =
    tokens

internal fun <T, V> foldVec(vec: List<T>, fold: V, f: (V, T) -> T): List<T> =
    vec.map { f(fold, it) }
