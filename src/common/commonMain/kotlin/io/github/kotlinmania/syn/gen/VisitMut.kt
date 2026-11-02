// port-lint: source gen/visit_mut.rs
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

/**
 * AST mutable visitor.
 *
 * Override methods to intercept and rewrite specific node types. Default
 * implementations recurse into sub-nodes.
 */
public open class VisitMut {
    public open fun visitExpr(e: Expr): Expr =
        visitExprMut(this, e)

    public open fun visitExprArrayMut(e: Expr.Array) =
        visitExprArrayMut(this, e)

    public open fun visitExprAssignMut(e: Expr.Assign) =
        visitExprAssignMut(this, e)

    public open fun visitExprAsyncMut(e: Expr.Async) =
        visitExprAsyncMut(this, e)

    public open fun visitExprAwaitMut(e: Expr.Await) =
        visitExprAwaitMut(this, e)

    public open fun visitExprBinaryMut(e: Expr.Binary) =
        visitExprBinaryMut(this, e)

    public open fun visitExprBlockMut(e: Expr.BlockExpr) =
        visitExprBlockMut(this, e)

    public open fun visitExprBreakMut(e: Expr.Break) =
        visitExprBreakMut(this, e)

    public open fun visitExprCallMut(e: Expr.Call) =
        visitExprCallMut(this, e)

    public open fun visitExprCastMut(e: Expr.Cast) =
        visitExprCastMut(this, e)

    public open fun visitExprClosureMut(e: Expr.Closure) =
        visitExprClosureMut(this, e)

    public open fun visitExprConstMut(e: Expr.Const) =
        visitExprConstMut(this, e)

    public open fun visitExprContinueMut(e: Expr.Continue) =
        visitExprContinueMut(this, e)

    public open fun visitExprFieldMut(e: Expr.Field) =
        visitExprFieldMut(this, e)

    public open fun visitExprForLoopMut(e: Expr.ForLoop) =
        visitExprForLoopMut(this, e)

    public open fun visitExprGroupMut(e: Expr.Group) =
        visitExprGroupMut(this, e)

    public open fun visitExprIfMut(e: Expr.If) =
        visitExprIfMut(this, e)

    public open fun visitExprIndexMut(e: Expr.Index) =
        visitExprIndexMut(this, e)

    public open fun visitExprInferMut(e: Expr.Infer) =
        visitExprInferMut(this, e)

    public open fun visitExprLetMut(e: Expr.Let) =
        visitExprLetMut(this, e)

    public open fun visitExprLitMut(e: Expr.Lit) =
        visitExprLitMut(this, e)

    public open fun visitExprLoopMut(e: Expr.Loop) =
        visitExprLoopMut(this, e)

    public open fun visitExprMacroMut(e: Expr.Macro) =
        visitExprMacroMut(this, e)

    public open fun visitExprMatchMut(e: Expr.Match) =
        visitExprMatchMut(this, e)

    public open fun visitExprMethodCallMut(e: Expr.MethodCall) =
        visitExprMethodCallMut(this, e)

    public open fun visitExprParenMut(e: Expr.Paren) =
        visitExprParenMut(this, e)

    public open fun visitExprRangeMut(e: Expr.Range) =
        visitExprRangeMut(this, e)

    public open fun visitExprRawAddrMut(e: Expr.RawAddr) =
        visitExprRawAddrMut(this, e)

    public open fun visitExprReferenceMut(e: Expr.Reference) =
        visitExprReferenceMut(this, e)

    public open fun visitExprRepeatMut(e: Expr.Repeat) =
        visitExprRepeatMut(this, e)

    public open fun visitExprReturnMut(e: Expr.Return) =
        visitExprReturnMut(this, e)

    public open fun visitExprStructMut(e: Expr.Struct) =
        visitExprStructMut(this, e)

    public open fun visitExprTryMut(e: Expr.Try) =
        visitExprTryMut(this, e)

    public open fun visitExprTryBlockMut(e: Expr.TryBlock) =
        visitExprTryBlockMut(this, e)

    public open fun visitExprTupleMut(e: Expr.Tuple) =
        visitExprTupleMut(this, e)

    public open fun visitExprUnaryMut(e: Expr.Unary) =
        visitExprUnaryMut(this, e)

    public open fun visitExprUnsafeMut(e: Expr.Unsafe) =
        visitExprUnsafeMut(this, e)

    public open fun visitExprWhileMut(e: Expr.While) =
        visitExprWhileMut(this, e)

    public open fun visitExprYieldMut(e: Expr.Yield) =
        visitExprYieldMut(this, e)

    public open fun visitType(t: SynType) =
        visitTypeMut(this, t)

    public open fun visitPath(p: Path) =
        visitPathMut(this, p)

    public open fun visitPat(p: Pat) =
        visitPatMut(this, p)

    public open fun visitItem(i: Item) =
        visitItemMut(this, i)

    public open fun visitFile(f: File) =
        visitFileMut(this, f)

    public open fun visitAttribute(a: Attribute) =
        visitAttributeMut(this, a)

    public open fun visitAttrStyle(style: AttrStyle) =
        visitAttrStyleMut(this, style)

    public open fun visitMeta(m: Meta) =
        visitMetaMut(this, m)

    public open fun visitMetaList(m: Meta.List) =
        visitMetaListMut(this, m)

    public open fun visitMetaNameValue(m: Meta.NameValue) =
        visitMetaNameValueMut(this, m)

    public open fun visitGenerics(g: Generics) =
        visitGenericsMut(this, g)

    public open fun visitLit(l: Lit) =
        visitLitMut(this, l)

    public open fun visitLitBoolMut(l: LitBool) =
        visitLitBoolMut(this, l)

    public open fun visitLitByteMut(l: LitByte) =
        visitLitByteMut(this, l)

    public open fun visitLitByteStrMut(l: LitByteStr) =
        visitLitByteStrMut(this, l)

    public open fun visitLitCStrMut(l: LitCStr) =
        visitLitCStrMut(this, l)

    public open fun visitLitCstrMut(l: LitCStr) =
        visitLitCstrMut(this, l)

    public open fun visitLitCharMut(l: LitChar) =
        visitLitCharMut(this, l)

    public open fun visitLitFloatMut(l: LitFloat) =
        visitLitFloatMut(this, l)

    public open fun visitLitIntMut(l: LitInt) =
        visitLitIntMut(this, l)

    public open fun visitLitStrMut(l: LitStr) =
        visitLitStrMut(this, l)

    public open fun visitLifetime(lt: Lifetime) =
        visitLifetimeMut(this, lt)

    public open fun visitIdent(id: Ident) =
        visitIdentMut(this, id)

    public open fun visitStmt(s: Stmt): Stmt =
        visitStmtMut(this, s)

    public open fun visitData(d: Data) =
        visitDataMut(this, d)

    public open fun visitDataEnum(d: DataEnum) =
        visitDataEnumMut(this, d)

    public open fun visitDataStruct(d: DataStruct) =
        visitDataStructMut(this, d)

    public open fun visitDataUnion(d: DataUnion) =
        visitDataUnionMut(this, d)

    public open fun visitLabelMut(label: Label) =
        visitLabelMut(this, label)

    public open fun visitDeriveInput(di: DeriveInput) =
        visitDeriveInputMut(this, di)

    public open fun visitBlock(block: Block) =
        visitBlockMut(this, block)

    public open fun visitAttributes(attrs: List<Attribute>) =
        visitAttributesMut(this, attrs)

    public open fun visitSignature(sig: Signature) =
        visitSignatureMut(this, sig)

    public open fun visitAbi(a: Abi) =
        visitAbiMut(this, a)

    public open fun visitReturnType(rt: ReturnType) =
        visitReturnTypeMut(this, rt)

    public open fun visitFnArg(arg: FnArg) =
        visitFnArgMut(this, arg)

    public open fun visitForeignItem(item: ForeignItem) =
        visitForeignItemMut(this, item)

    public open fun visitForeignItemFn(item: ForeignItem.Fn) =
        visitForeignItemFnMut(this, item)

    public open fun visitForeignItemMacro(item: ForeignItem.Macro) =
        visitForeignItemMacroMut(this, item)

    public open fun visitForeignItemStatic(item: ForeignItem.Static) =
        visitForeignItemStaticMut(this, item)

    public open fun visitForeignItemType(item: ForeignItem.ItemType) =
        visitForeignItemTypeMut(this, item)

    public open fun visitReceiver(receiver: FnArg.Receiver) =
        visitReceiverMut(this, receiver)

    public open fun visitPatType(patType: PatType) =
        visitPatTypeMut(this, patType)

    public open fun visitPatIdent(patIdent: Pat.Ident) =
        visitPatIdentMut(this, patIdent)

    public open fun visitPatOrMut(pat: Pat.Or) =
        visitPatOrMut(this, pat)

    public open fun visitPatParenMut(pat: Pat.PatParen) =
        visitPatParenMut(this, pat)

    public open fun visitPatReferenceMut(pat: Pat.Reference) =
        visitPatReferenceMut(this, pat)

    public open fun visitPatRestMut(pat: Pat.Rest) =
        visitPatRestMut(this, pat)

    public open fun visitPatRestMut(rest: PatRest) =
        visitPatRestMut(this, rest)

    public open fun visitPatSliceMut(pat: Pat.Slice) =
        visitPatSliceMut(this, pat)

    public open fun visitPatStructMut(pat: Pat.Struct) =
        visitPatStructMut(this, pat)

    public open fun visitPatTupleMut(pat: Pat.Tuple) =
        visitPatTupleMut(this, pat)

    public open fun visitPatTupleStructMut(pat: Pat.TupleStruct) =
        visitPatTupleStructMut(this, pat)

    public open fun visitPatWildMut(pat: Pat.Wild) =
        visitPatWildMut(this, pat)

    public open fun visitPatTypeMut(pat: Pat.TypeAscription) =
        visitPatTypeMut(this, pat)

    public open fun visitTypePath(typePath: SynType.Path) =
        visitTypePathMut(this, typePath)

    public open fun visitTypeReference(ty: SynType.Reference) =
        visitTypeReferenceMut(this, ty)

    public open fun visitTypeArray(ty: SynType.Array) =
        visitTypeArrayMut(this, ty)

    public open fun visitTypeGroup(ty: SynType.Group) =
        visitTypeGroupMut(this, ty)

    public open fun visitTypeImplTrait(ty: SynType.ImplTrait) =
        visitTypeImplTraitMut(this, ty)

    public open fun visitTypeInfer(ty: SynType.Infer) =
        visitTypeInferMut(this, ty)

    public open fun visitTypeMacro(ty: SynType.Macro) =
        visitTypeMacroMut(this, ty)

    public open fun visitTypeNever(ty: SynType.Never) =
        visitTypeNeverMut(this, ty)

    public open fun visitTypePtr(ty: SynType.Ptr) =
        visitTypePtrMut(this, ty)

    public open fun visitPointerMutability(mutability: PointerMutability) =
        visitPointerMutabilityMut(this, mutability)

    public open fun visitPointerMutabilityMut(mutability: io.github.kotlinmania.syn.token.Mut?) =
        visitPointerMutabilityMut(this, mutability)

    public open fun visitTypeBareFn(ty: SynType.BareFn) =
        visitTypeBareFnMut(this, ty)

    public open fun visitBareFnArg(arg: BareFnArg) =
        visitBareFnArgMut(this, arg)

    public open fun visitBareVariadic(variadic: BareVariadic) =
        visitBareVariadicMut(this, variadic)

    public open fun visitTypeParen(ty: SynType.Paren) =
        visitTypeParenMut(this, ty)

    public open fun visitTypeSlice(ty: SynType.Slice) =
        visitTypeSliceMut(this, ty)

    public open fun visitTypeTraitObject(ty: SynType.TraitObject) =
        visitTypeTraitObjectMut(this, ty)

    public open fun visitTypeTuple(ty: SynType.Tuple) =
        visitTypeTupleMut(this, ty)

    public open fun visitExprPath(exprPath: Expr.Path) =
        visitExprPathMut(this, exprPath)

    public open fun visitMacro(mac: Macro) =
        visitMacroMut(this, mac)

    public open fun visitPathArguments(pathArgs: PathArguments) =
        visitPathArgumentsMut(this, pathArgs)

    public open fun visitAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed) =
        visitAngleBracketedGenericArgumentsMut(this, pathArgs)

    public open fun visitParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized) =
        visitParenthesizedGenericArgumentsMut(this, pathArgs)

    public open fun visitGenericArgument(genArg: GenericArgument) =
        visitGenericArgumentMut(this, genArg)

    public open fun visitAssocType(assoc: AssocType) =
        visitAssocTypeMut(this, assoc)

    public open fun visitAssocConst(assoc: AssocConst) =
        visitAssocConstMut(this, assoc)

    public open fun visitConstraint(constraint: Constraint) =
        visitConstraintMut(this, constraint)

    public open fun visitTypeParamBound(bound: TypeParamBound) =
        visitTypeParamBoundMut(this, bound)

    public open fun visitTraitBound(bound: TypeParamBound.Trait) =
        visitTraitBoundMut(this, bound)

    public open fun visitTraitBoundModifier(modifier: TraitBoundModifier) =
        visitTraitBoundModifierMut(this, modifier)

    public open fun visitBinOp(op: BinOp) =
        visitBinOpMut(this, op)

    public open fun visitUnOpMut(op: UnOp) =
        visitUnOpMut(this, op)

    public open fun visitBoundLifetimes(boundLifetimes: BoundLifetimes) =
        visitBoundLifetimesMut(this, boundLifetimes)

    public open fun visitCapturedParam(param: CapturedParam) =
        visitCapturedParamMut(this, param)

    public open fun visitPreciseCaptureMut(param: TypeParamBound.PreciseCapture) =
        visitPreciseCaptureMut(this, param)

    public open fun visitPathSegment(segment: PathSegment) =
        visitPathSegmentMut(this, segment)

    public open fun visitArm(arm: Arm) =
        visitArmMut(this, arm)

    public open fun visitElseExpr(elseExpr: ElseExpr) =
        visitElseExprMut(this, elseExpr)

    public open fun visitFieldPat(fieldPat: FieldPat) =
        visitFieldPatMut(this, fieldPat)

    public open fun visitFieldValue(fieldValue: FieldValue) =
        visitFieldValueMut(this, fieldValue)

    public open fun visitGenericParam(param: GenericParam) =
        visitGenericParamMut(this, param)

    public open fun visitLifetimeParamMut(param: GenericParam.LifetimeParam) =
        visitLifetimeParamMut(this, param)

    public open fun visitTypeParamMut(param: GenericParam.TypeParam) =
        visitTypeParamMut(this, param)

    public open fun visitConstParamMut(param: GenericParam.ConstParam) =
        visitConstParamMut(this, param)

    public open fun visitField(field: Field) =
        visitFieldMut(this, field)

    public open fun visitFieldMutability(fieldMutability: FieldMutability) =
        visitFieldMutabilityMut(this, fieldMutability)

    public open fun visitFields(fields: Fields) =
        visitFieldsMut(this, fields)

    public open fun visitFieldsNamed(fields: FieldsNamed) =
        visitFieldsNamedMut(this, fields)

    public open fun visitFieldsUnnamed(fields: FieldsUnnamed) =
        visitFieldsUnnamedMut(this, fields)

    public open fun visitImplItem(item: ImplItem) =
        visitImplItemMut(this, item)

    public open fun visitImplItemConst(item: ImplItem.Const) =
        visitImplItemConstMut(this, item)

    public open fun visitImplItemFn(item: ImplItem.Fn) =
        visitImplItemFnMut(this, item)

    public open fun visitImplItemMacro(item: ImplItem.Macro) =
        visitImplItemMacroMut(this, item)

    public open fun visitImplItemType(item: ImplItem.AssocType) =
        visitImplItemTypeMut(this, item)

    public open fun visitImplRestriction(restriction: ImplRestriction) =
        visitImplRestrictionMut(this, restriction)

    public open fun visitItemConst(item: Item.Const) =
        visitItemConstMut(this, item)

    public open fun visitItemEnum(item: Item.Enum) =
        visitItemEnumMut(this, item)

    public open fun visitItemExternCrate(item: Item.ExternCrate) =
        visitItemExternCrateMut(this, item)

    public open fun visitItemFn(item: Item.Fn) =
        visitItemFnMut(this, item)

    public open fun visitItemForeignMod(item: Item.ForeignMod) =
        visitItemForeignModMut(this, item)

    public open fun visitItemImpl(item: Item.Impl) =
        visitItemImplMut(this, item)

    public open fun visitItemMacro(item: Item.Macro) =
        visitItemMacroMut(this, item)

    public open fun visitItemMod(item: Item.Mod) =
        visitItemModMut(this, item)

    public open fun visitItemStatic(item: Item.Static) =
        visitItemStaticMut(this, item)

    public open fun visitItemStruct(item: Item.Struct) =
        visitItemStructMut(this, item)

    public open fun visitItemTrait(item: Item.Trait) =
        visitItemTraitMut(this, item)

    public open fun visitItemTraitAlias(item: Item.TraitAlias) =
        visitItemTraitAliasMut(this, item)

    public open fun visitItemType(item: Item.ItemType) =
        visitItemTypeMut(this, item)

    public open fun visitItemUnion(item: Item.Union) =
        visitItemUnionMut(this, item)

    public open fun visitItemUse(item: Item.Use) =
        visitItemUseMut(this, item)

    public open fun visitStaticMutability(mutability: StaticMutability) =
        visitStaticMutabilityMut(this, mutability)

    public open fun visitRangeLimitsMut(limits: RangeLimits) =
        visitRangeLimitsMut(this, limits)

    public open fun visitMacroDelimiterMut(delimiter: MacroDelimiter) =
        visitMacroDelimiterMut(this, delimiter)

    public open fun visitModContent(modContent: ModContent) =
        visitModContentMut(this, modContent)

    public open fun visitLocalMut(local: Stmt.Local) =
        visitLocalMut(this, local)

    public open fun visitLocalInit(init: LocalInit) =
        visitLocalInitMut(this, init)

    public open fun visitMember(member: Member) =
        visitMemberMut(this, member)

    public open fun visitIndexMut(index: Index) =
        visitIndexMut(this, index)

    public open fun visitQSelf(qself: QSelf) =
        visitQSelfMut(this, qself)

    public open fun visitQselfMut(qself: QSelf) =
        visitQselfMut(this, qself)

    public open fun visitPathTrait(pathTrait: PathTrait) =
        visitPathTraitMut(this, pathTrait)

    public open fun visitTraitItem(item: TraitItem) =
        visitTraitItemMut(this, item)

    public open fun visitTraitItemConst(item: TraitItem.Const) =
        visitTraitItemConstMut(this, item)

    public open fun visitTraitItemFn(item: TraitItem.Fn) =
        visitTraitItemFnMut(this, item)

    public open fun visitTraitItemMacro(item: TraitItem.Macro) =
        visitTraitItemMacroMut(this, item)

    public open fun visitTraitItemType(item: TraitItem.AssocType) =
        visitTraitItemTypeMut(this, item)

    public open fun visitUseTree(useTree: UseTree) =
        visitUseTreeMut(this, useTree)

    public open fun visitUseGlob(useTree: UseTree.Glob) =
        visitUseGlobMut(this, useTree)

    public open fun visitUseGroup(useTree: UseTree.Group) =
        visitUseGroupMut(this, useTree)

    public open fun visitUseName(useTree: UseTree.Name) =
        visitUseNameMut(this, useTree)

    public open fun visitUsePath(useTree: UseTree.Path) =
        visitUsePathMut(this, useTree)

    public open fun visitUseRename(useTree: UseTree.Name) =
        visitUseRenameMut(this, useTree)

    public open fun visitVariadic(variadic: Variadic) =
        visitVariadicMut(this, variadic)

    public open fun visitVariant(variant: Variant) =
        visitVariantMut(this, variant)

    public open fun visitVisibility(visibility: Visibility) =
        visitVisibilityMut(this, visibility)

    public open fun visitVisRestrictedMut(visibility: Visibility.Restricted) =
        visitVisRestrictedMut(this, visibility)

    public open fun visitWhereClause(whereClause: WhereClause) =
        visitWhereClauseMut(this, whereClause)

    public open fun visitWherePredicate(wherePredicate: WherePredicate) =
        visitWherePredicateMut(this, wherePredicate)

    public open fun visitPredicateLifetimeMut(predicate: WherePredicate.LifetimePredicate) =
        visitPredicateLifetimeMut(this, predicate)

    public open fun visitPredicateTypeMut(predicate: WherePredicate.TypePredicate) =
        visitPredicateTypeMut(this, predicate)

    public open fun visitStmtMacroMut(stmt: Stmt.MacroStmt) =
        visitStmtMacroMut(this, stmt)

    public open fun visitSpanMut(span: Span) =
        visitSpanMut(this, span)

    public open fun visitTokenStreamMut(tokens: TokenStream) =
        visitTokenStreamMut(this, tokens)

    public open fun visitExprMut(e: Expr): Expr =
        visitExpr(e)

    public open fun visitTypeMut(t: SynType) =
        visitType(t)

    public open fun visitPathMut(p: Path) =
        visitPath(p)

    public open fun visitPatMut(p: Pat) =
        visitPat(p)

    public open fun visitItemMut(i: Item) =
        visitItem(i)

    public open fun visitFileMut(f: File) =
        visitFile(f)

    public open fun visitAttributeMut(a: Attribute) =
        visitAttribute(a)

    public open fun visitAttrStyleMut(style: AttrStyle) =
        visitAttrStyle(style)

    public open fun visitMetaMut(m: Meta) =
        visitMeta(m)

    public open fun visitMetaListMut(m: Meta.List) =
        visitMetaList(m)

    public open fun visitMetaNameValueMut(m: Meta.NameValue) =
        visitMetaNameValue(m)

    public open fun visitGenericsMut(g: Generics) =
        visitGenerics(g)

    public open fun visitLitMut(l: Lit) =
        visitLit(l)

    public open fun visitLifetimeMut(lt: Lifetime) =
        visitLifetime(lt)

    public open fun visitIdentMut(id: Ident) =
        visitIdent(id)

    public open fun visitStmtMut(s: Stmt): Stmt =
        visitStmt(s)

    public open fun visitDataMut(d: Data) =
        visitData(d)

    public open fun visitDataEnumMut(d: DataEnum) =
        visitDataEnum(d)

    public open fun visitDataStructMut(d: DataStruct) =
        visitDataStruct(d)

    public open fun visitDataUnionMut(d: DataUnion) =
        visitDataUnion(d)

    public open fun visitDeriveInputMut(di: DeriveInput) =
        visitDeriveInput(di)

    public open fun visitBlockMut(block: Block) =
        visitBlock(block)

    public open fun visitAttributesMut(attrs: List<Attribute>) =
        visitAttributes(attrs)

    public open fun visitSignatureMut(sig: Signature) =
        visitSignature(sig)

    public open fun visitAbiMut(a: Abi) =
        visitAbi(a)

    public open fun visitReturnTypeMut(rt: ReturnType) =
        visitReturnType(rt)

    public open fun visitFnArgMut(arg: FnArg) =
        visitFnArg(arg)

    public open fun visitReceiverMut(receiver: FnArg.Receiver) =
        visitReceiver(receiver)

    public open fun visitPatTypeMut(patType: PatType) =
        visitPatType(patType)

    public open fun visitPatIdentMut(patIdent: Pat.Ident) =
        visitPatIdent(patIdent)

    public open fun visitTypePathMut(typePath: SynType.Path) =
        visitTypePath(typePath)

    public open fun visitTypeReferenceMut(ty: SynType.Reference) =
        visitTypeReference(ty)

    public open fun visitTypeArrayMut(ty: SynType.Array) =
        visitTypeArray(ty)

    public open fun visitTypeGroupMut(ty: SynType.Group) =
        visitTypeGroup(ty)

    public open fun visitTypeImplTraitMut(ty: SynType.ImplTrait) =
        visitTypeImplTrait(ty)

    public open fun visitTypeInferMut(ty: SynType.Infer) =
        visitTypeInfer(ty)

    public open fun visitTypeMacroMut(ty: SynType.Macro) =
        visitTypeMacro(ty)

    public open fun visitTypeNeverMut(ty: SynType.Never) =
        visitTypeNever(ty)

    public open fun visitTypePtrMut(ty: SynType.Ptr) =
        visitTypePtr(ty)

    public open fun visitPointerMutabilityMut(mutability: PointerMutability) =
        visitPointerMutability(mutability)

    public open fun visitTypeBareFnMut(ty: SynType.BareFn) =
        visitTypeBareFn(ty)

    public open fun visitBareFnArgMut(arg: BareFnArg) =
        visitBareFnArg(arg)

    public open fun visitBareVariadicMut(variadic: BareVariadic) =
        visitBareVariadic(variadic)

    public open fun visitTypeParenMut(ty: SynType.Paren) =
        visitTypeParen(ty)

    public open fun visitTypeSliceMut(ty: SynType.Slice) =
        visitTypeSlice(ty)

    public open fun visitTypeTraitObjectMut(ty: SynType.TraitObject) =
        visitTypeTraitObject(ty)

    public open fun visitTypeTupleMut(ty: SynType.Tuple) =
        visitTypeTuple(ty)

    public open fun visitExprPathMut(exprPath: Expr.Path) =
        visitExprPath(exprPath)

    public open fun visitMacroMut(mac: Macro) =
        visitMacro(mac)

    public open fun visitPathArgumentsMut(pathArgs: PathArguments) =
        visitPathArguments(pathArgs)

    public open fun visitAngleBracketedGenericArgumentsMut(pathArgs: PathArguments.AngleBracketed) =
        visitAngleBracketedGenericArguments(pathArgs)

    public open fun visitParenthesizedGenericArgumentsMut(pathArgs: PathArguments.Parenthesized) =
        visitParenthesizedGenericArguments(pathArgs)

    public open fun visitGenericArgumentMut(genArg: GenericArgument) =
        visitGenericArgument(genArg)

    public open fun visitAssocTypeMut(assoc: AssocType) =
        visitAssocType(assoc)

    public open fun visitAssocConstMut(assoc: AssocConst) =
        visitAssocConst(assoc)

    public open fun visitConstraintMut(constraint: Constraint) =
        visitConstraint(constraint)

    public open fun visitTypeParamBoundMut(bound: TypeParamBound) =
        visitTypeParamBound(bound)

    public open fun visitTraitBoundMut(bound: TypeParamBound.Trait) =
        visitTraitBound(bound)

    public open fun visitTraitBoundModifierMut(modifier: TraitBoundModifier) =
        visitTraitBoundModifier(modifier)

    public open fun visitBinOpMut(op: BinOp) =
        visitBinOp(op)

    public open fun visitBoundLifetimesMut(boundLifetimes: BoundLifetimes) =
        visitBoundLifetimes(boundLifetimes)

    public open fun visitCapturedParamMut(param: CapturedParam) =
        visitCapturedParam(param)

    public open fun visitPathSegmentMut(segment: PathSegment) =
        visitPathSegment(segment)

    public open fun visitArmMut(arm: Arm) =
        visitArm(arm)

    public open fun visitElseExprMut(elseExpr: ElseExpr) =
        visitElseExpr(elseExpr)

    public open fun visitFieldPatMut(fieldPat: FieldPat) =
        visitFieldPat(fieldPat)

    public open fun visitFieldValueMut(fieldValue: FieldValue) =
        visitFieldValue(fieldValue)

    public open fun visitGenericParamMut(param: GenericParam) =
        visitGenericParam(param)

    public open fun visitFieldMut(field: Field) =
        visitField(field)

    public open fun visitFieldMutabilityMut(fieldMutability: FieldMutability) =
        visitFieldMutability(fieldMutability)

    public open fun visitFieldsMut(fields: Fields) =
        visitFields(fields)

    public open fun visitFieldsNamedMut(fields: FieldsNamed) =
        visitFieldsNamed(fields)

    public open fun visitFieldsUnnamedMut(fields: FieldsUnnamed) =
        visitFieldsUnnamed(fields)

    public open fun visitImplItemMut(item: ImplItem) =
        visitImplItem(item)

    public open fun visitImplItemConstMut(item: ImplItem.Const) =
        visitImplItemConst(item)

    public open fun visitImplItemFnMut(item: ImplItem.Fn) =
        visitImplItemFn(item)

    public open fun visitImplItemMacroMut(item: ImplItem.Macro) =
        visitImplItemMacro(item)

    public open fun visitImplItemTypeMut(item: ImplItem.AssocType) =
        visitImplItemType(item)

    public open fun visitForeignItemMut(item: ForeignItem) =
        visitForeignItem(item)

    public open fun visitForeignItemFnMut(item: ForeignItem.Fn) =
        visitForeignItemFn(item)

    public open fun visitForeignItemMacroMut(item: ForeignItem.Macro) =
        visitForeignItemMacro(item)

    public open fun visitForeignItemStaticMut(item: ForeignItem.Static) =
        visitForeignItemStatic(item)

    public open fun visitForeignItemTypeMut(item: ForeignItem.ItemType) =
        visitForeignItemType(item)

    public open fun visitImplRestrictionMut(restriction: ImplRestriction) =
        visitImplRestriction(restriction)

    public open fun visitItemConstMut(item: Item.Const) =
        visitItemConst(item)

    public open fun visitItemEnumMut(item: Item.Enum) =
        visitItemEnum(item)

    public open fun visitItemExternCrateMut(item: Item.ExternCrate) =
        visitItemExternCrate(item)

    public open fun visitItemFnMut(item: Item.Fn) =
        visitItemFn(item)

    public open fun visitItemForeignModMut(item: Item.ForeignMod) =
        visitItemForeignMod(item)

    public open fun visitItemImplMut(item: Item.Impl) =
        visitItemImpl(item)

    public open fun visitItemMacroMut(item: Item.Macro) =
        visitItemMacro(item)

    public open fun visitItemModMut(item: Item.Mod) =
        visitItemMod(item)

    public open fun visitItemStaticMut(item: Item.Static) =
        visitItemStatic(item)

    public open fun visitItemStructMut(item: Item.Struct) =
        visitItemStruct(item)

    public open fun visitItemTraitMut(item: Item.Trait) =
        visitItemTrait(item)

    public open fun visitItemTraitAliasMut(item: Item.TraitAlias) =
        visitItemTraitAlias(item)

    public open fun visitItemTypeMut(item: Item.ItemType) =
        visitItemType(item)

    public open fun visitItemUnionMut(item: Item.Union) =
        visitItemUnion(item)

    public open fun visitItemUseMut(item: Item.Use) =
        visitItemUse(item)

    public open fun visitStaticMutabilityMut(mutability: StaticMutability) =
        visitStaticMutability(mutability)

    public open fun visitModContentMut(modContent: ModContent) =
        visitModContent(modContent)

    public open fun visitLocalInitMut(init: LocalInit) =
        visitLocalInit(init)

    public open fun visitMemberMut(member: Member) =
        visitMember(member)

    public open fun visitQSelfMut(qself: QSelf) =
        visitQSelf(qself)

    public open fun visitPathTraitMut(pathTrait: PathTrait) =
        visitPathTrait(pathTrait)

    public open fun visitTraitItemMut(item: TraitItem) =
        visitTraitItem(item)

    public open fun visitTraitItemConstMut(item: TraitItem.Const) =
        visitTraitItemConst(item)

    public open fun visitTraitItemFnMut(item: TraitItem.Fn) =
        visitTraitItemFn(item)

    public open fun visitTraitItemMacroMut(item: TraitItem.Macro) =
        visitTraitItemMacro(item)

    public open fun visitTraitItemTypeMut(item: TraitItem.AssocType) =
        visitTraitItemType(item)

    public open fun visitUseTreeMut(useTree: UseTree) =
        visitUseTree(useTree)

    public open fun visitUseGlobMut(useTree: UseTree.Glob) =
        visitUseGlob(useTree)

    public open fun visitUseGroupMut(useTree: UseTree.Group) =
        visitUseGroup(useTree)

    public open fun visitUseNameMut(useTree: UseTree.Name) =
        visitUseName(useTree)

    public open fun visitUsePathMut(useTree: UseTree.Path) =
        visitUsePath(useTree)

    public open fun visitUseRenameMut(useTree: UseTree.Name) =
        visitUseRename(useTree)

    public open fun visitVariadicMut(variadic: Variadic) =
        visitVariadic(variadic)

    public open fun visitVariantMut(variant: Variant) =
        visitVariant(variant)

    public open fun visitVisibilityMut(visibility: Visibility) =
        visitVisibility(visibility)

    public open fun visitWhereClauseMut(whereClause: WhereClause) =
        visitWhereClause(whereClause)

    public open fun visitWherePredicateMut(wherePredicate: WherePredicate) =
        visitWherePredicate(wherePredicate)
}

public fun visitExprMut(v: VisitMut, e: Expr): Expr {
    when (e) {
        is Expr.Array -> v.visitExprArrayMut(e)
        is Expr.Assign -> v.visitExprAssignMut(e)
        is Expr.Async -> v.visitExprAsyncMut(e)
        is Expr.Await -> v.visitExprAwaitMut(e)
        is Expr.Binary -> v.visitExprBinaryMut(e)
        is Expr.BlockExpr -> v.visitExprBlockMut(e)
        is Expr.Break -> v.visitExprBreakMut(e)
        is Expr.Call -> v.visitExprCallMut(e)
        is Expr.Cast -> v.visitExprCastMut(e)
        is Expr.Closure -> v.visitExprClosureMut(e)
        is Expr.Const -> v.visitExprConstMut(e)
        is Expr.Continue -> v.visitExprContinueMut(e)
        is Expr.Field -> v.visitExprFieldMut(e)
        is Expr.ForLoop -> v.visitExprForLoopMut(e)
        is Expr.Group -> v.visitExprGroupMut(e)
        is Expr.If -> v.visitExprIfMut(e)
        is Expr.Index -> v.visitExprIndexMut(e)
        is Expr.Infer -> v.visitExprInferMut(e)
        is Expr.Let -> v.visitExprLetMut(e)
        is Expr.Lit -> v.visitExprLitMut(e)
        is Expr.Loop -> v.visitExprLoopMut(e)
        is Expr.Macro -> v.visitExprMacroMut(e)
        is Expr.Match -> v.visitExprMatchMut(e)
        is Expr.MethodCall -> v.visitExprMethodCallMut(e)
        is Expr.Paren -> v.visitExprParenMut(e)
        is Expr.Path -> v.visitExprPathMut(e)
        is Expr.Range -> v.visitExprRangeMut(e)
        is Expr.RawAddr -> v.visitExprRawAddrMut(e)
        is Expr.Reference -> v.visitExprReferenceMut(e)
        is Expr.Repeat -> v.visitExprRepeatMut(e)
        is Expr.Return -> v.visitExprReturnMut(e)
        is Expr.Struct -> v.visitExprStructMut(e)
        is Expr.Try -> v.visitExprTryMut(e)
        is Expr.TryBlock -> v.visitExprTryBlockMut(e)
        is Expr.Tuple -> v.visitExprTupleMut(e)
        is Expr.Unary -> v.visitExprUnaryMut(e)
        is Expr.Unsafe -> v.visitExprUnsafeMut(e)
        is Expr.While -> v.visitExprWhileMut(e)
        is Expr.Yield -> v.visitExprYieldMut(e)
        is Expr.Verbatim -> v.visitTokenStreamMut(e.tokens)
    }
    return e
}

public fun visitExprArrayMut(v: VisitMut, e: Expr.Array) {
    v.visitAttributesMut(e.attrs)
    for (i in 0 until e.elems.size) e.elems[i] = v.visitExprMut(e.elems[i])
}

public fun visitExprAssignMut(v: VisitMut, e: Expr.Assign) {
    v.visitAttributesMut(e.attrs)
    e.left = v.visitExprMut(e.left)
    e.right = v.visitExprMut(e.right)
}

public fun visitExprAsyncMut(v: VisitMut, e: Expr.Async) {
    v.visitAttributesMut(e.attrs)
    v.visitBlockMut(e.block)
}

public fun visitExprAwaitMut(v: VisitMut, e: Expr.Await) {
    v.visitAttributesMut(e.attrs)
    e.base = v.visitExprMut(e.base)
}

public fun visitExprBinaryMut(v: VisitMut, e: Expr.Binary) {
    v.visitAttributesMut(e.attrs)
    e.left = v.visitExprMut(e.left)
    v.visitBinOpMut(e.op)
    e.right = v.visitExprMut(e.right)
}

public fun visitExprBlockMut(v: VisitMut, e: Expr.BlockExpr) {
    v.visitAttributesMut(e.attrs)
    e.label?.let { v.visitLabelMut(it) }
    v.visitBlockMut(e.block)
}

public fun visitExprBreakMut(v: VisitMut, e: Expr.Break) {
    v.visitAttributesMut(e.attrs)
    e.label?.let { v.visitLifetimeMut(it) }
    e.expr = e.expr?.let { v.visitExprMut(it) }
}

public fun visitExprCallMut(v: VisitMut, e: Expr.Call) {
    v.visitAttributesMut(e.attrs)
    e.func = v.visitExprMut(e.func)
    for (i in 0 until e.args.size) e.args[i] = v.visitExprMut(e.args[i])
}

public fun visitExprCastMut(v: VisitMut, e: Expr.Cast) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
    v.visitTypeMut(e.ty)
}

public fun visitExprClosureMut(v: VisitMut, e: Expr.Closure) {
    v.visitAttributesMut(e.attrs)
    e.lifetimes?.let { v.visitBoundLifetimesMut(it) }
    for (i in 0 until e.inputs.size) v.visitPatMut(e.inputs[i])
    v.visitReturnTypeMut(e.output)
    e.body = v.visitExprMut(e.body)
}

public fun visitExprConstMut(v: VisitMut, e: Expr.Const) {
    v.visitAttributesMut(e.attrs)
    v.visitBlockMut(e.block)
}

public fun visitExprContinueMut(v: VisitMut, e: Expr.Continue) {
    v.visitAttributesMut(e.attrs)
    e.label?.let { v.visitLifetimeMut(it) }
}

public fun visitExprFieldMut(v: VisitMut, e: Expr.Field) {
    v.visitAttributesMut(e.attrs)
    e.base = v.visitExprMut(e.base)
    v.visitMemberMut(e.member)
}

public fun visitExprForLoopMut(v: VisitMut, e: Expr.ForLoop) {
    v.visitAttributesMut(e.attrs)
    e.label?.let { v.visitLabelMut(it) }
    v.visitPatMut(e.pat)
    e.expr = v.visitExprMut(e.expr)
    v.visitBlockMut(e.body)
}

public fun visitExprGroupMut(v: VisitMut, e: Expr.Group) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
}

public fun visitExprIfMut(v: VisitMut, e: Expr.If) {
    v.visitAttributesMut(e.attrs)
    e.cond = v.visitExprMut(e.cond)
    v.visitBlockMut(e.thenBranch)
    e.elseBranch?.let { v.visitElseExprMut(it) }
}

public fun visitExprIndexMut(v: VisitMut, e: Expr.Index) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
    e.index = v.visitExprMut(e.index)
}

public fun visitExprInferMut(v: VisitMut, e: Expr.Infer) {
    v.visitAttributesMut(e.attrs)
}

public fun visitExprLetMut(v: VisitMut, e: Expr.Let) {
    v.visitAttributesMut(e.attrs)
    v.visitPatMut(e.pat)
    e.expr = v.visitExprMut(e.expr)
}

public fun visitExprLitMut(v: VisitMut, e: Expr.Lit) {
    v.visitAttributesMut(e.attrs)
    v.visitLitMut(e.lit)
}

public fun visitExprLoopMut(v: VisitMut, e: Expr.Loop) {
    v.visitAttributesMut(e.attrs)
    e.label?.let { v.visitLabelMut(it) }
    v.visitBlockMut(e.body)
}

public fun visitExprMacroMut(v: VisitMut, e: Expr.Macro) {
    v.visitAttributesMut(e.attrs)
    v.visitMacroMut(e.mac)
}

public fun visitExprMatchMut(v: VisitMut, e: Expr.Match) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
    for (i in e.arms.indices) v.visitArmMut(e.arms[i])
}

public fun visitExprMethodCallMut(v: VisitMut, e: Expr.MethodCall) {
    v.visitAttributesMut(e.attrs)
    e.receiver = v.visitExprMut(e.receiver)
    v.visitIdentMut(e.method)
    e.turbofish?.let { v.visitAngleBracketedGenericArgumentsMut(it) }
    for (i in 0 until e.args.size) e.args[i] = v.visitExprMut(e.args[i])
}

public fun visitExprParenMut(v: VisitMut, e: Expr.Paren) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
}

public fun visitExprRangeMut(v: VisitMut, e: Expr.Range) {
    v.visitAttributesMut(e.attrs)
    e.start = e.start?.let { v.visitExprMut(it) }
    v.visitRangeLimitsMut(e.limits)
    e.end = e.end?.let { v.visitExprMut(it) }
}

public fun visitExprRawAddrMut(v: VisitMut, e: Expr.RawAddr) {
    v.visitAttributesMut(e.attrs)
    v.visitPointerMutabilityMut(e.mutability)
    e.expr = v.visitExprMut(e.expr)
}

public fun visitExprReferenceMut(v: VisitMut, e: Expr.Reference) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
}

public fun visitExprRepeatMut(v: VisitMut, e: Expr.Repeat) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
    e.len = v.visitExprMut(e.len)
}

public fun visitExprReturnMut(v: VisitMut, e: Expr.Return) {
    v.visitAttributesMut(e.attrs)
    e.expr = e.expr?.let { v.visitExprMut(it) }
}

public fun visitExprStructMut(v: VisitMut, e: Expr.Struct) {
    v.visitAttributesMut(e.attrs)
    e.qself?.let { v.visitQSelfMut(it) }
    v.visitPathMut(e.path)
    for (i in 0 until e.fields.size) v.visitFieldValueMut(e.fields[i])
    e.rest = e.rest?.let { v.visitExprMut(it) }
}

public fun visitExprTryMut(v: VisitMut, e: Expr.Try) {
    v.visitAttributesMut(e.attrs)
    e.expr = v.visitExprMut(e.expr)
}

public fun visitExprTryBlockMut(v: VisitMut, e: Expr.TryBlock) {
    v.visitAttributesMut(e.attrs)
    v.visitBlockMut(e.block)
}

public fun visitExprTupleMut(v: VisitMut, e: Expr.Tuple) {
    v.visitAttributesMut(e.attrs)
    for (i in 0 until e.elems.size) e.elems[i] = v.visitExprMut(e.elems[i])
}

public fun visitExprUnaryMut(v: VisitMut, e: Expr.Unary) {
    v.visitAttributesMut(e.attrs)
    v.visitUnOpMut(e.op)
    e.expr = v.visitExprMut(e.expr)
}

public fun visitExprUnsafeMut(v: VisitMut, e: Expr.Unsafe) {
    v.visitAttributesMut(e.attrs)
    v.visitBlockMut(e.block)
}

public fun visitExprWhileMut(v: VisitMut, e: Expr.While) {
    v.visitAttributesMut(e.attrs)
    e.label?.let { v.visitLabelMut(it) }
    e.cond = v.visitExprMut(e.cond)
    v.visitBlockMut(e.body)
}

public fun visitExprYieldMut(v: VisitMut, e: Expr.Yield) {
    v.visitAttributesMut(e.attrs)
    e.expr = e.expr?.let { v.visitExprMut(it) }
}

public fun visitTypeMut(v: VisitMut, t: SynType) =
    when (t) {
        is SynType.Array -> {
            v.visitTypeArrayMut(t)
        }

        is SynType.BareFn -> {
            v.visitTypeBareFnMut(t)
        }

        is SynType.Group -> {
            v.visitTypeGroupMut(t)
        }

        is SynType.ImplTrait -> {
            v.visitTypeImplTraitMut(t)
        }

        is SynType.Infer -> {
            v.visitTypeInferMut(t)
        }

        is SynType.Macro -> {
            v.visitTypeMacroMut(t)
        }

        is SynType.Never -> {
            v.visitTypeNeverMut(t)
        }

        is SynType.Paren -> {
            v.visitTypeParenMut(t)
        }

        is SynType.Path -> {
            v.visitTypePathMut(t)
        }

        is SynType.Ptr -> {
            v.visitTypePtrMut(t)
        }

        is SynType.Reference -> {
            v.visitTypeReferenceMut(t)
        }

        is SynType.Slice -> {
            v.visitTypeSliceMut(t)
        }

        is SynType.TraitObject -> {
            v.visitTypeTraitObjectMut(t)
        }

        is SynType.Tuple -> {
            v.visitTypeTupleMut(t)
        }

        is SynType.Verbatim -> {
            v.visitTokenStreamMut(t.tokens)
        }
    }

public fun visitPathMut(v: VisitMut, p: Path) {
    for (i in 0 until p.segments.size) v.visitPathSegmentMut(p.segments[i])
}

public fun visitPatMut(v: VisitMut, p: Pat) =
    when (p) {
        is Pat.Const -> {
            v.visitAttributesMut(p.attrs)
            v.visitBlockMut(p.block)
        }

        is Pat.Ident -> {
            v.visitPatIdentMut(p)
        }

        is Pat.Lit -> {
            v.visitAttributesMut(p.attrs)
            v.visitLitMut(p.lit)
        }

        is Pat.Macro -> {
            v.visitAttributesMut(p.attrs)
            v.visitMacroMut(p.mac)
        }

        is Pat.Or -> {
            v.visitPatOrMut(p)
        }

        is Pat.PatParen -> {
            v.visitPatParenMut(p)
        }

        is Pat.Path -> {
            v.visitAttributesMut(p.attrs)
            p.qself?.let { v.visitQSelfMut(it) }
            v.visitPathMut(p.path)
        }

        is Pat.Range -> {
            v.visitAttributesMut(p.attrs)
            p.start = p.start?.let { v.visitExprMut(it) }
            v.visitRangeLimitsMut(p.limits)
            p.end = p.end?.let { v.visitExprMut(it) }
        }

        is Pat.Reference -> {
            v.visitPatReferenceMut(p)
        }

        is Pat.Rest -> {
            v.visitPatRestMut(p)
        }

        is Pat.Slice -> {
            v.visitPatSliceMut(p)
        }

        is Pat.Struct -> {
            v.visitPatStructMut(p)
        }

        is Pat.Tuple -> {
            v.visitPatTupleMut(p)
        }

        is Pat.TupleStruct -> {
            v.visitPatTupleStructMut(p)
        }

        is Pat.TypeAscription -> {
            v.visitPatTypeMut(p)
        }

        is Pat.Wild -> {
            v.visitPatWildMut(p)
        }

        is Pat.Verbatim -> {
            v.visitTokenStreamMut(p.tokens)
        }
    }

public fun visitItemMut(v: VisitMut, i: Item) =
    when (i) {
        is Item.Const -> {
            v.visitItemConstMut(i)
        }

        is Item.Enum -> {
            v.visitItemEnumMut(i)
        }

        is Item.ExternCrate -> {
            v.visitItemExternCrateMut(i)
        }

        is Item.Fn -> {
            v.visitItemFnMut(i)
        }

        is Item.ForeignMod -> {
            v.visitItemForeignModMut(i)
        }

        is Item.Impl -> {
            v.visitItemImplMut(i)
        }

        is Item.Macro -> {
            v.visitItemMacroMut(i)
        }

        is Item.Mod -> {
            v.visitItemModMut(i)
        }

        is Item.Static -> {
            v.visitItemStaticMut(i)
        }

        is Item.Struct -> {
            v.visitItemStructMut(i)
        }

        is Item.Trait -> {
            v.visitItemTraitMut(i)
        }

        is Item.TraitAlias -> {
            v.visitItemTraitAliasMut(i)
        }

        is Item.ItemType -> {
            v.visitItemTypeMut(i)
        }

        is Item.Union -> {
            v.visitItemUnionMut(i)
        }

        is Item.Use -> {
            v.visitItemUseMut(i)
        }

        is Item.Verbatim -> {
            v.visitTokenStreamMut(i.tokens)
        }
    }

public fun visitFileMut(v: VisitMut, f: File) {
    v.visitAttributesMut(f.attrs)
    for (i in f.items.indices) v.visitItemMut(f.items[i])
}

public fun visitAttributeMut(v: VisitMut, a: Attribute) {
    v.visitAttrStyleMut(a.style)
    v.visitMetaMut(a.meta)
}

public fun visitAttrStyleMut(v: VisitMut, style: AttrStyle) = Unit

public fun visitMetaMut(v: VisitMut, m: Meta) =
    when (m) {
        is Meta.PathMeta -> {
            v.visitPathMut(m.path)
        }

        is Meta.List -> {
            v.visitMetaListMut(m)
        }

        is Meta.NameValue -> {
            v.visitMetaNameValueMut(m)
        }
    }

public fun visitMetaListMut(v: VisitMut, m: Meta.List) {
    v.visitMacroDelimiterMut(m.delimiter)
    v.visitTokenStreamMut(m.tokens)
    v.visitPathMut(m.path)
}

public fun visitMetaNameValueMut(v: VisitMut, m: Meta.NameValue) {
    v.visitPathMut(m.path)
    m.value = v.visitExprMut(m.value)
}

public fun visitGenericsMut(v: VisitMut, g: Generics) {
    for (i in 0 until g.params.size) v.visitGenericParamMut(g.params[i])
    g.whereClause?.let { v.visitWhereClauseMut(it) }
}

public fun visitLitMut(v: VisitMut, l: Lit) =
    when (l) {
        is Lit.Str -> {
            v.visitLitStrMut(l.value)
        }

        is Lit.ByteStr -> {
            v.visitLitByteStrMut(l.value)
        }

        is Lit.CStr -> {
            v.visitLitCStrMut(l.value)
        }

        is Lit.Byte -> {
            v.visitLitByteMut(l.value)
        }

        is Lit.Char -> {
            v.visitLitCharMut(l.value)
        }

        is Lit.Int -> {
            v.visitLitIntMut(l.value)
        }

        is Lit.Float -> {
            v.visitLitFloatMut(l.value)
        }

        is Lit.Bool -> {
            v.visitLitBoolMut(l.value)
        }

        is Lit.Verbatim -> Unit
    }

public fun visitLitBoolMut(v: VisitMut, l: LitBool) {
    val span = l.span()
    v.visitSpanMut(span)
    l.setSpan(span)
}

public fun visitLitByteMut(v: VisitMut, l: LitByte) = Unit

public fun visitLitByteStrMut(v: VisitMut, l: LitByteStr) = Unit

public fun visitLitCStrMut(v: VisitMut, l: LitCStr) = Unit

public fun visitLitCstrMut(v: VisitMut, l: LitCStr) {
    v.visitLitCStrMut(l)
}

public fun visitLitCharMut(v: VisitMut, l: LitChar) = Unit

public fun visitLitFloatMut(v: VisitMut, l: LitFloat) = Unit

public fun visitLitIntMut(v: VisitMut, l: LitInt) = Unit

public fun visitLitStrMut(v: VisitMut, l: LitStr) = Unit

public fun visitLifetimeMut(v: VisitMut, lt: Lifetime) {
    v.visitSpanMut(lt.apostrophe)
    v.visitIdentMut(lt.ident)
}

public fun visitIdentMut(v: VisitMut, id: Ident) {
    val span = id.span()
    v.visitSpanMut(span)
    id.setSpan(span)
}

public fun visitStmtMut(v: VisitMut, s: Stmt): Stmt =
    when (s) {
        is Stmt.Local -> {
            v.visitLocalMut(s)
            s
        }

        is Stmt.ItemStmt -> {
            v.visitItemMut(s.item)
            s
        }

        is Stmt.ExprStmt -> {
            s.expr = v.visitExprMut(s.expr)
            s
        }

        is Stmt.MacroStmt -> {
            v.visitStmtMacroMut(s)
            s
        }
    }

public fun visitDataMut(v: VisitMut, d: Data) =
    when (d) {
        is Data.Struct -> {
            v.visitDataStructMut(d.value)
        }

        is Data.Enum -> {
            v.visitDataEnumMut(d.value)
        }

        is Data.Union -> {
            v.visitDataUnionMut(d.value)
        }
    }

public fun visitDataEnumMut(v: VisitMut, d: DataEnum) {
    for (i in 0 until d.variants.size) v.visitVariantMut(d.variants[i])
}

public fun visitDataStructMut(v: VisitMut, d: DataStruct) {
    v.visitFieldsMut(d.fields)
}

public fun visitDataUnionMut(v: VisitMut, d: DataUnion) {
    v.visitFieldsNamedMut(d.fields)
}

public fun visitLabelMut(v: VisitMut, label: Label) {
    v.visitLifetimeMut(label.name)
}

public fun visitDeriveInputMut(v: VisitMut, di: DeriveInput) {
    v.visitAttributesMut(di.attrs)
    v.visitVisibilityMut(di.vis)
    v.visitIdentMut(di.ident)
    v.visitGenericsMut(di.generics)
    v.visitDataMut(di.data)
}

public fun visitBlockMut(v: VisitMut, block: Block) {
    block.stmts = block.stmts.mapTo(mutableListOf()) { v.visitStmtMut(it) }
}

public fun visitAttributesMut(v: VisitMut, attrs: List<Attribute>) {
    for (i in attrs.indices) v.visitAttributeMut(attrs[i])
}

public fun visitSignatureMut(v: VisitMut, sig: Signature) {
    sig.abi?.let { v.visitAbiMut(it) }
    v.visitIdentMut(sig.ident)
    v.visitGenericsMut(sig.generics)
    for (i in 0 until sig.inputs.size) v.visitFnArgMut(sig.inputs[i])
    sig.variadic?.let { v.visitVariadicMut(it) }
    v.visitReturnTypeMut(sig.output)
}

public fun visitAbiMut(v: VisitMut, a: Abi) {
    val name = a.name
    if (name != null) {
        v.visitLitStrMut(name)
    }
}

public fun visitReturnTypeMut(v: VisitMut, rt: ReturnType) =
    when (rt) {
        is ReturnType.Default -> Unit

        is ReturnType.TypeReturn -> {
            v.visitTypeMut(rt.ty)
        }
    }

public fun visitFnArgMut(v: VisitMut, arg: FnArg) =
    when (arg) {
        is FnArg.Receiver -> {
            v.visitReceiverMut(arg)
        }

        is FnArg.Typed -> {
            v.visitPatTypeMut(arg.patType)
        }
    }

public fun visitForeignItemMut(v: VisitMut, item: ForeignItem) =
    when (item) {
        is ForeignItem.Fn -> {
            v.visitForeignItemFnMut(item)
        }

        is ForeignItem.Static -> {
            v.visitForeignItemStaticMut(item)
        }

        is ForeignItem.ItemType -> {
            v.visitForeignItemTypeMut(item)
        }

        is ForeignItem.Macro -> {
            v.visitForeignItemMacroMut(item)
        }

        is ForeignItem.Verbatim -> {
            v.visitTokenStreamMut(item.tokens)
        }
    }

public fun visitForeignItemFnMut(v: VisitMut, item: ForeignItem.Fn) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitSignatureMut(item.sig)
}

public fun visitForeignItemMacroMut(v: VisitMut, item: ForeignItem.Macro) {
    v.visitAttributesMut(item.attrs)
    v.visitMacroMut(item.mac)
}

public fun visitForeignItemStaticMut(v: VisitMut, item: ForeignItem.Static) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitStaticMutabilityMut(item.mutability)
    v.visitIdentMut(item.ident)
    v.visitTypeMut(item.ty)
}

public fun visitForeignItemTypeMut(v: VisitMut, item: ForeignItem.ItemType) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
}

public fun visitReceiverMut(v: VisitMut, receiver: FnArg.Receiver) {
    v.visitAttributesMut(receiver.attrs)
    receiver.reference?.let { it.lifetime?.let { lt -> v.visitLifetimeMut(lt) } }
    v.visitTypeMut(receiver.type)
}

public fun visitPatTypeMut(v: VisitMut, patType: PatType) {
    v.visitPatMut(patType.pat)
    v.visitTypeMut(patType.ty)
}

public fun visitPatIdentMut(v: VisitMut, patIdent: Pat.Ident) {
    v.visitAttributesMut(patIdent.attrs)
    v.visitIdentMut(patIdent.ident)
    patIdent.subpat?.let { v.visitPatMut(it) }
}

public fun visitPatOrMut(v: VisitMut, pat: Pat.Or) {
    v.visitAttributesMut(pat.attrs)
    for (i in 0 until pat.cases.size) v.visitPatMut(pat.cases[i])
}

public fun visitPatParenMut(v: VisitMut, pat: Pat.PatParen) {
    v.visitAttributesMut(pat.attrs)
    v.visitPatMut(pat.pat)
}

public fun visitPatReferenceMut(v: VisitMut, pat: Pat.Reference) {
    v.visitAttributesMut(pat.attrs)
    v.visitPatMut(pat.pat)
}

public fun visitPatRestMut(v: VisitMut, pat: Pat.Rest) {
    v.visitAttributesMut(pat.attrs)
}

public fun visitPatRestMut(v: VisitMut, rest: PatRest) {
    v.visitAttributesMut(rest.attrs)
}

public fun visitPatSliceMut(v: VisitMut, pat: Pat.Slice) {
    v.visitAttributesMut(pat.attrs)
    for (i in 0 until pat.elems.size) v.visitPatMut(pat.elems[i])
}

public fun visitPatStructMut(v: VisitMut, pat: Pat.Struct) {
    v.visitAttributesMut(pat.attrs)
    pat.qself?.let { v.visitQSelfMut(it) }
    v.visitPathMut(pat.path)
    for (i in 0 until pat.fields.size) v.visitFieldPatMut(pat.fields[i])
    pat.rest?.let { v.visitPatRestMut(it) }
}

public fun visitPatTupleMut(v: VisitMut, pat: Pat.Tuple) {
    v.visitAttributesMut(pat.attrs)
    for (i in 0 until pat.elems.size) v.visitPatMut(pat.elems[i])
}

public fun visitPatTupleStructMut(v: VisitMut, pat: Pat.TupleStruct) {
    v.visitAttributesMut(pat.attrs)
    pat.qself?.let { v.visitQSelfMut(it) }
    v.visitPathMut(pat.path)
    for (i in 0 until pat.elems.size) v.visitPatMut(pat.elems[i])
}

public fun visitPatWildMut(v: VisitMut, pat: Pat.Wild) {
    v.visitAttributesMut(pat.attrs)
}

public fun visitPatTypeMut(v: VisitMut, pat: Pat.TypeAscription) {
    v.visitAttributesMut(pat.attrs)
    v.visitPatMut(pat.pat)
    v.visitTypeMut(pat.ty)
}

public fun visitTypePathMut(v: VisitMut, typePath: SynType.Path) {
    typePath.qself?.let { v.visitQSelfMut(it) }
    v.visitPathMut(typePath.path)
}

public fun visitTypeReferenceMut(v: VisitMut, ty: SynType.Reference) {
    ty.lifetime?.let { v.visitLifetimeMut(it) }
    v.visitTypeMut(ty.elem)
}

public fun visitTypeArrayMut(v: VisitMut, ty: SynType.Array) {
    v.visitTypeMut(ty.elem)
    ty.len = v.visitExprMut(ty.len)
}

public fun visitTypeGroupMut(v: VisitMut, ty: SynType.Group) {
    v.visitTypeMut(ty.elem)
}

public fun visitTypeImplTraitMut(v: VisitMut, ty: SynType.ImplTrait) {
    for (i in 0 until ty.bounds.size) v.visitTypeParamBoundMut(ty.bounds[i])
}

public fun visitTypeInferMut(v: VisitMut, ty: SynType.Infer) = Unit

public fun visitTypeMacroMut(v: VisitMut, ty: SynType.Macro) {
    v.visitMacroMut(ty.mac)
}

public fun visitTypeNeverMut(v: VisitMut, ty: SynType.Never) = Unit

public fun visitTypePtrMut(v: VisitMut, ty: SynType.Ptr) {
    v.visitPointerMutabilityMut(ty.mutability)
    v.visitTypeMut(ty.elem)
}

public fun visitPointerMutabilityMut(v: VisitMut, mutability: PointerMutability) = Unit

public fun visitPointerMutabilityMut(v: VisitMut, mutability: io.github.kotlinmania.syn.token.Mut?) = Unit

public fun visitTypeBareFnMut(v: VisitMut, ty: SynType.BareFn) {
    ty.lifetimes?.let { v.visitBoundLifetimesMut(it) }
    ty.abi?.let { v.visitAbiMut(it) }
    for (i in 0 until ty.inputs.size) v.visitBareFnArgMut(ty.inputs[i])
    ty.variadic?.let { v.visitBareVariadicMut(it) }
    v.visitReturnTypeMut(ty.output)
}

public fun visitBareFnArgMut(v: VisitMut, arg: BareFnArg) {
    v.visitAttributesMut(arg.attrs)
    arg.name?.let { v.visitIdentMut(it.ident) }
    v.visitTypeMut(arg.ty)
}

public fun visitBareVariadicMut(v: VisitMut, variadic: BareVariadic) {
    v.visitAttributesMut(variadic.attrs)
    variadic.name?.let { v.visitIdentMut(it.ident) }
}

public fun visitTypeParenMut(v: VisitMut, ty: SynType.Paren) {
    v.visitTypeMut(ty.elem)
}

public fun visitTypeSliceMut(v: VisitMut, ty: SynType.Slice) {
    v.visitTypeMut(ty.elem)
}

public fun visitTypeTraitObjectMut(v: VisitMut, ty: SynType.TraitObject) {
    for (i in 0 until ty.bounds.size) v.visitTypeParamBoundMut(ty.bounds[i])
}

public fun visitTypeTupleMut(v: VisitMut, ty: SynType.Tuple) {
    for (i in 0 until ty.elems.size) v.visitTypeMut(ty.elems[i])
}

public fun visitExprPathMut(v: VisitMut, exprPath: Expr.Path) {
    v.visitAttributesMut(exprPath.attrs)
    exprPath.qself?.let { v.visitQSelfMut(it) }
    v.visitPathMut(exprPath.path)
}

public fun visitMacroMut(v: VisitMut, mac: Macro) {
    v.visitPathMut(mac.path)
    v.visitMacroDelimiterMut(mac.delimiter)
    v.visitTokenStreamMut(mac.tokens)
}

public fun visitPathArgumentsMut(v: VisitMut, pathArgs: PathArguments) =
    when (pathArgs) {
        is PathArguments.None -> Unit

        is PathArguments.AngleBracketed -> {
            v.visitAngleBracketedGenericArgumentsMut(pathArgs)
        }

        is PathArguments.Parenthesized -> {
            v.visitParenthesizedGenericArgumentsMut(pathArgs)
        }
    }

public fun visitAngleBracketedGenericArgumentsMut(v: VisitMut, pathArgs: PathArguments.AngleBracketed) {
    for (i in 0 until pathArgs.args.size) v.visitGenericArgumentMut(pathArgs.args[i])
}

public fun visitParenthesizedGenericArgumentsMut(v: VisitMut, pathArgs: PathArguments.Parenthesized) {
    for (i in 0 until pathArgs.inputs.size) v.visitTypeMut(pathArgs.inputs[i])
    v.visitReturnTypeMut(pathArgs.output)
}

public fun visitGenericArgumentMut(v: VisitMut, genArg: GenericArgument) =
    when (genArg) {
        is GenericArgument.LifetimeArg -> {
            v.visitLifetimeMut(genArg.lifetime)
        }

        is GenericArgument.TypeArg -> {
            v.visitTypeMut(genArg.type)
        }

        is GenericArgument.ConstArg -> {
            genArg.expr = v.visitExprMut(genArg.expr)
        }

        is GenericArgument.AssocTypeArg -> {
            v.visitAssocTypeMut(genArg.assoc)
        }

        is GenericArgument.AssocConstArg -> {
            v.visitAssocConstMut(genArg.assoc)
        }

        is GenericArgument.ConstraintArg -> {
            v.visitConstraintMut(genArg.constraint)
        }
    }

public fun visitAssocTypeMut(v: VisitMut, assoc: AssocType) {
    v.visitIdentMut(assoc.ident)
    assoc.generics?.let { v.visitAngleBracketedGenericArgumentsMut(it) }
    v.visitTypeMut(assoc.ty)
}

public fun visitAssocConstMut(v: VisitMut, assoc: AssocConst) {
    v.visitIdentMut(assoc.ident)
    assoc.generics?.let { v.visitAngleBracketedGenericArgumentsMut(it) }
    assoc.value = v.visitExprMut(assoc.value)
}

public fun visitConstraintMut(v: VisitMut, constraint: Constraint) {
    v.visitIdentMut(constraint.ident)
    constraint.generics?.let { v.visitAngleBracketedGenericArgumentsMut(it) }
    for (i in 0 until constraint.bounds.size) v.visitTypeParamBoundMut(constraint.bounds[i])
}

public fun visitTypeParamBoundMut(v: VisitMut, bound: TypeParamBound) =
    when (bound) {
        is TypeParamBound.Trait -> {
            v.visitTraitBoundMut(bound)
        }

        is TypeParamBound.LifetimeBound -> {
            v.visitLifetimeMut(bound.lifetime)
        }

        is TypeParamBound.PreciseCapture -> {
            v.visitPreciseCaptureMut(bound)
        }

        is TypeParamBound.Verbatim -> {
            v.visitTokenStreamMut(bound.tokens)
        }
    }

public fun visitTraitBoundMut(v: VisitMut, bound: TypeParamBound.Trait) {
    v.visitTraitBoundModifierMut(bound.modifier)
    bound.lifetimes?.let { v.visitBoundLifetimesMut(it) }
    v.visitPathMut(bound.path)
}

public fun visitTraitBoundModifierMut(v: VisitMut, modifier: TraitBoundModifier) = Unit

public fun visitBinOpMut(v: VisitMut, op: BinOp) = Unit

public fun visitUnOpMut(v: VisitMut, op: UnOp) = Unit

public fun visitBoundLifetimesMut(v: VisitMut, boundLifetimes: BoundLifetimes) {
    for (i in 0 until boundLifetimes.lifetimes.size) v.visitGenericParamMut(boundLifetimes.lifetimes[i])
}

public fun visitCapturedParamMut(v: VisitMut, param: CapturedParam) =
    when (param) {
        is CapturedParam.Lifetime -> {
            v.visitLifetimeMut(param.lifetime)
        }

        is CapturedParam.Ident -> {
            v.visitIdentMut(param.ident)
        }
    }

public fun visitPreciseCaptureMut(v: VisitMut, param: TypeParamBound.PreciseCapture) {
    for (i in 0 until param.params.size) v.visitCapturedParamMut(param.params[i])
}

public fun visitPathSegmentMut(v: VisitMut, segment: PathSegment) {
    v.visitIdentMut(segment.ident)
    v.visitPathArgumentsMut(segment.arguments)
}

public fun visitArmMut(v: VisitMut, arm: Arm) {
    v.visitAttributesMut(arm.attrs)
    v.visitPatMut(arm.pat)
    arm.guard?.let { it.expr = v.visitExprMut(it.expr) }
    arm.body = v.visitExprMut(arm.body)
}

public fun visitElseExprMut(v: VisitMut, elseExpr: ElseExpr) {
    elseExpr.expr = v.visitExprMut(elseExpr.expr)
}

public fun visitFieldPatMut(v: VisitMut, fieldPat: FieldPat) {
    v.visitAttributesMut(fieldPat.attrs)
    v.visitMemberMut(fieldPat.member)
    v.visitPatMut(fieldPat.pat)
}

public fun visitFieldValueMut(v: VisitMut, fieldValue: FieldValue) {
    v.visitAttributesMut(fieldValue.attrs)
    v.visitMemberMut(fieldValue.member)
    fieldValue.expr = v.visitExprMut(fieldValue.expr)
}

public fun visitGenericParamMut(v: VisitMut, param: GenericParam) =
    when (param) {
        is GenericParam.LifetimeParam -> v.visitLifetimeParamMut(param)
        is GenericParam.TypeParam -> v.visitTypeParamMut(param)
        is GenericParam.ConstParam -> v.visitConstParamMut(param)
    }

public fun visitLifetimeParamMut(v: VisitMut, param: GenericParam.LifetimeParam) {
    v.visitAttributesMut(param.attrs)
    v.visitLifetimeMut(param.lifetime)
    for (i in 0 until param.bounds.size) v.visitLifetimeMut(param.bounds[i])
}

public fun visitTypeParamMut(v: VisitMut, param: GenericParam.TypeParam) {
    v.visitAttributesMut(param.attrs)
    v.visitIdentMut(param.ident)
    for (i in 0 until param.bounds.size) v.visitTypeParamBoundMut(param.bounds[i])
    param.default?.let { v.visitTypeMut(it) }
}

public fun visitConstParamMut(v: VisitMut, param: GenericParam.ConstParam) {
    v.visitAttributesMut(param.attrs)
    v.visitIdentMut(param.ident)
    v.visitTypeMut(param.ty)
    param.default = param.default?.let { v.visitExprMut(it) }
}

public fun visitFieldMut(v: VisitMut, field: Field) {
    v.visitAttributesMut(field.attrs)
    v.visitVisibilityMut(field.vis)
    v.visitFieldMutabilityMut(field.mutability)
    field.ident?.let { v.visitIdentMut(it) }
    v.visitTypeMut(field.ty)
}

public fun visitFieldMutabilityMut(v: VisitMut, fieldMutability: FieldMutability) = Unit

public fun visitFieldsMut(v: VisitMut, fields: Fields) =
    when (fields) {
        is Fields.Named -> {
            v.visitFieldsNamedMut(fields.fields)
        }

        is Fields.Unnamed -> {
            v.visitFieldsUnnamedMut(fields.fields)
        }

        Fields.Unit -> Unit
    }

public fun visitFieldsNamedMut(v: VisitMut, fields: FieldsNamed) {
    for (i in 0 until fields.named.size) v.visitFieldMut(fields.named[i])
}

public fun visitFieldsUnnamedMut(v: VisitMut, fields: FieldsUnnamed) {
    for (i in 0 until fields.unnamed.size) v.visitFieldMut(fields.unnamed[i])
}

public fun visitImplItemMut(v: VisitMut, item: ImplItem) =
    when (item) {
        is ImplItem.Const -> {
            v.visitImplItemConstMut(item)
        }

        is ImplItem.Fn -> {
            v.visitImplItemFnMut(item)
        }

        is ImplItem.AssocType -> {
            v.visitImplItemTypeMut(item)
        }

        is ImplItem.Macro -> {
            v.visitImplItemMacroMut(item)
        }

        is ImplItem.Verbatim -> {
            v.visitTokenStreamMut(item.tokens)
        }
    }

public fun visitImplItemConstMut(v: VisitMut, item: ImplItem.Const) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    v.visitTypeMut(item.ty)
    item.expr = v.visitExprMut(item.expr)
}

public fun visitImplItemFnMut(v: VisitMut, item: ImplItem.Fn) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitSignatureMut(item.sig)
    v.visitBlockMut(item.block)
}

public fun visitImplItemMacroMut(v: VisitMut, item: ImplItem.Macro) {
    v.visitAttributesMut(item.attrs)
    v.visitMacroMut(item.mac)
}

public fun visitImplItemTypeMut(v: VisitMut, item: ImplItem.AssocType) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    v.visitTypeMut(item.ty)
}

public fun visitImplRestrictionMut(v: VisitMut, restriction: ImplRestriction) = Unit

public fun visitItemConstMut(v: VisitMut, item: Item.Const) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitTypeMut(item.ty)
    item.expr = item.expr?.let { v.visitExprMut(it) }
}

public fun visitItemEnumMut(v: VisitMut, item: Item.Enum) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    for (i in 0 until item.variants.size) v.visitVariantMut(item.variants[i])
}

public fun visitItemExternCrateMut(v: VisitMut, item: Item.ExternCrate) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    item.rename?.let { v.visitIdentMut(it.ident) }
}

public fun visitItemFnMut(v: VisitMut, item: Item.Fn) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitSignatureMut(item.sig)
    item.block?.let { v.visitBlockMut(it) }
}

public fun visitItemForeignModMut(v: VisitMut, item: Item.ForeignMod) {
    v.visitAttributesMut(item.attrs)
    v.visitAbiMut(item.abi)
    for (i in item.items.indices) v.visitForeignItemMut(item.items[i])
}

public fun visitItemImplMut(v: VisitMut, item: Item.Impl) {
    v.visitAttributesMut(item.attrs)
    v.visitGenericsMut(item.generics)
    item.traitPath?.let { v.visitPathTraitMut(it) }
    v.visitTypeMut(item.selfType)
    for (i in item.items.indices) v.visitImplItemMut(item.items[i])
}

public fun visitItemMacroMut(v: VisitMut, item: Item.Macro) {
    v.visitAttributesMut(item.attrs)
    item.ident?.let { v.visitIdentMut(it) }
    v.visitMacroMut(item.mac)
}

public fun visitItemModMut(v: VisitMut, item: Item.Mod) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    item.content?.let { v.visitModContentMut(it) }
}

public fun visitItemStaticMut(v: VisitMut, item: Item.Static) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitStaticMutabilityMut(item.mutability)
    v.visitIdentMut(item.ident)
    v.visitTypeMut(item.ty)
    item.expr = v.visitExprMut(item.expr)
}

public fun visitItemStructMut(v: VisitMut, item: Item.Struct) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    v.visitFieldsMut(item.fields)
}

public fun visitItemTraitMut(v: VisitMut, item: Item.Trait) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    item.restriction?.let { v.visitImplRestrictionMut(it) }
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    for (i in 0 until item.supertraits.size) v.visitTypeParamBoundMut(item.supertraits[i])
    for (i in item.items.indices) v.visitTraitItemMut(item.items[i])
}

public fun visitItemTraitAliasMut(v: VisitMut, item: Item.TraitAlias) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    for (i in 0 until item.bounds.size) v.visitTypeParamBoundMut(item.bounds[i])
}

public fun visitItemTypeMut(v: VisitMut, item: Item.ItemType) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    v.visitTypeMut(item.ty)
}

public fun visitItemUnionMut(v: VisitMut, item: Item.Union) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    v.visitFieldsNamedMut(item.fields)
}

public fun visitItemUseMut(v: VisitMut, item: Item.Use) {
    v.visitAttributesMut(item.attrs)
    v.visitVisibilityMut(item.vis)
    v.visitUseTreeMut(item.tree)
}

public fun visitStaticMutabilityMut(v: VisitMut, mutability: StaticMutability) = Unit

public fun visitRangeLimitsMut(v: VisitMut, limits: RangeLimits) = Unit

public fun visitMacroDelimiterMut(v: VisitMut, delimiter: MacroDelimiter) = Unit

public fun visitModContentMut(v: VisitMut, modContent: ModContent) =
    when (modContent) {
        is ModContent.Inline -> {
            for (i in modContent.items.indices) v.visitItemMut(modContent.items[i])
        }

        is ModContent.Unnamed -> Unit
    }

public fun visitLocalMut(v: VisitMut, local: Stmt.Local) {
    v.visitAttributesMut(local.attrs)
    v.visitPatMut(local.pat)
    local.init?.let { v.visitLocalInitMut(it) }
}

public fun visitLocalInitMut(v: VisitMut, init: LocalInit) {
    init.expr = v.visitExprMut(init.expr)
    init.diverge?.let { v.visitElseExprMut(it) }
}

public fun visitMemberMut(v: VisitMut, member: Member) =
    when (member) {
        is Member.Named -> {
            v.visitIdentMut(member.ident)
        }

        is Member.Unnamed -> {
            v.visitIndexMut(member.index)
        }
    }

public fun visitIndexMut(v: VisitMut, index: Index) {
    v.visitSpanMut(index.span)
}

public fun visitQSelfMut(v: VisitMut, qself: QSelf) {
    v.visitTypeMut(qself.ty)
}

public fun visitQselfMut(v: VisitMut, qself: QSelf) {
    v.visitQSelfMut(qself)
}

public fun visitPathTraitMut(v: VisitMut, pathTrait: PathTrait) {
    v.visitPathMut(pathTrait.path)
}

public fun visitTraitItemMut(v: VisitMut, item: TraitItem) =
    when (item) {
        is TraitItem.Const -> {
            v.visitTraitItemConstMut(item)
        }

        is TraitItem.Fn -> {
            v.visitTraitItemFnMut(item)
        }

        is TraitItem.AssocType -> {
            v.visitTraitItemTypeMut(item)
        }

        is TraitItem.Macro -> {
            v.visitTraitItemMacroMut(item)
        }

        is TraitItem.Verbatim -> {
            v.visitTokenStreamMut(item.tokens)
        }
    }

public fun visitTraitItemConstMut(v: VisitMut, item: TraitItem.Const) {
    v.visitAttributesMut(item.attrs)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    v.visitTypeMut(item.ty)
    item.default?.let { it.expr = v.visitExprMut(it.expr) }
}

public fun visitTraitItemFnMut(v: VisitMut, item: TraitItem.Fn) {
    v.visitAttributesMut(item.attrs)
    v.visitSignatureMut(item.sig)
    item.default?.let { v.visitBlockMut(it) }
}

public fun visitTraitItemMacroMut(v: VisitMut, item: TraitItem.Macro) {
    v.visitAttributesMut(item.attrs)
    v.visitMacroMut(item.mac)
}

public fun visitTraitItemTypeMut(v: VisitMut, item: TraitItem.AssocType) {
    v.visitAttributesMut(item.attrs)
    v.visitIdentMut(item.ident)
    v.visitGenericsMut(item.generics)
    for (i in 0 until item.bounds.size) v.visitTypeParamBoundMut(item.bounds[i])
    item.default?.let { v.visitTypeMut(it.type) }
}

public fun visitUseTreeMut(v: VisitMut, useTree: UseTree) =
    when (useTree) {
        is UseTree.Path -> {
            v.visitUsePathMut(useTree)
        }

        is UseTree.Name -> {
            if (useTree.rename == null) {
                v.visitUseNameMut(useTree)
            } else {
                v.visitUseRenameMut(useTree)
            }
        }

        is UseTree.Group -> {
            v.visitUseGroupMut(useTree)
        }

        is UseTree.Glob -> {
            v.visitUseGlobMut(useTree)
        }
    }

public fun visitUseGlobMut(v: VisitMut, useTree: UseTree.Glob) = Unit

public fun visitUseGroupMut(v: VisitMut, useTree: UseTree.Group) {
    for (i in 0 until useTree.items.size) v.visitUseTreeMut(useTree.items[i])
}

public fun visitUseNameMut(v: VisitMut, useTree: UseTree.Name) {
    v.visitIdentMut(useTree.ident)
}

public fun visitUsePathMut(v: VisitMut, useTree: UseTree.Path) {
    v.visitIdentMut(useTree.ident)
    useTree.tree?.let { v.visitUseTreeMut(it) }
}

public fun visitUseRenameMut(v: VisitMut, useTree: UseTree.Name) {
    v.visitIdentMut(useTree.ident)
    useTree.rename?.let { v.visitIdentMut(it.ident) }
}

public fun visitVariadicMut(v: VisitMut, variadic: Variadic) {
    v.visitAttributesMut(variadic.attrs)
    variadic.pat?.let { v.visitPatMut(it.pat) }
}

public fun visitVariantMut(v: VisitMut, variant: Variant) {
    v.visitAttributesMut(variant.attrs)
    v.visitIdentMut(variant.ident)
    v.visitFieldsMut(variant.fields)
    variant.discriminant?.let { it.expr = v.visitExprMut(it.expr) }
}

public fun visitVisibilityMut(v: VisitMut, visibility: Visibility) =
    when (visibility) {
        is Visibility.Public -> Unit

        is Visibility.Restricted -> {
            v.visitVisRestrictedMut(visibility)
        }

        Visibility.Inherited -> Unit
    }

public fun visitVisRestrictedMut(v: VisitMut, visibility: Visibility.Restricted) {
    v.visitPathMut(visibility.path)
}

public fun visitWhereClauseMut(v: VisitMut, whereClause: WhereClause) {
    for (i in 0 until whereClause.predicates.size) v.visitWherePredicateMut(whereClause.predicates[i])
}

public fun visitWherePredicateMut(v: VisitMut, wherePredicate: WherePredicate) =
    when (wherePredicate) {
        is WherePredicate.LifetimePredicate -> v.visitPredicateLifetimeMut(wherePredicate)
        is WherePredicate.TypePredicate -> v.visitPredicateTypeMut(wherePredicate)
    }

public fun visitPredicateLifetimeMut(v: VisitMut, predicate: WherePredicate.LifetimePredicate) {
    v.visitLifetimeMut(predicate.lifetime)
    for (i in 0 until predicate.bounds.size) v.visitLifetimeMut(predicate.bounds[i])
}

public fun visitPredicateTypeMut(v: VisitMut, predicate: WherePredicate.TypePredicate) {
    predicate.lifetimes?.let { v.visitBoundLifetimesMut(it) }
    v.visitTypeMut(predicate.boundedTy)
    for (i in 0 until predicate.bounds.size) v.visitTypeParamBoundMut(predicate.bounds[i])
}

public fun visitStmtMacroMut(v: VisitMut, stmt: Stmt.MacroStmt) {
    v.visitAttributesMut(stmt.attrs)
    v.visitMacroMut(stmt.mac)
}

public fun visitSpanMut(v: VisitMut, span: Span) = Unit

public fun visitTokenStreamMut(v: VisitMut, tokens: TokenStream) = Unit
