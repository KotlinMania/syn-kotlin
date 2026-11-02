// port-lint: source gen/visit.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.Abi
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
 * AST visitor — walks a syntax tree without mutating it.
 *
 * This Visit interface provides a visitor-style API for callers who need
 * to walk trees without rewriting them. Default implementations recurse
 * into child nodes; override the methods you care about and call `super`
 * to continue walking.
 */
public open class Visit {
    public open fun visitExpr(e: Expr) =
        visitExpr(this, e)

    public open fun visitExprArray(e: Expr.Array) =
        visitExprArray(this, e)

    public open fun visitExprAssign(e: Expr.Assign) =
        visitExprAssign(this, e)

    public open fun visitExprAsync(e: Expr.Async) =
        visitExprAsync(this, e)

    public open fun visitExprAwait(e: Expr.Await) =
        visitExprAwait(this, e)

    public open fun visitExprBinary(e: Expr.Binary) =
        visitExprBinary(this, e)

    public open fun visitExprBlock(e: Expr.BlockExpr) =
        visitExprBlock(this, e)

    public open fun visitExprBreak(e: Expr.Break) =
        visitExprBreak(this, e)

    public open fun visitExprCall(e: Expr.Call) =
        visitExprCall(this, e)

    public open fun visitExprCast(e: Expr.Cast) =
        visitExprCast(this, e)

    public open fun visitExprClosure(e: Expr.Closure) =
        visitExprClosure(this, e)

    public open fun visitExprConst(e: Expr.Const) =
        visitExprConst(this, e)

    public open fun visitExprContinue(e: Expr.Continue) =
        visitExprContinue(this, e)

    public open fun visitExprField(e: Expr.Field) =
        visitExprField(this, e)

    public open fun visitExprForLoop(e: Expr.ForLoop) =
        visitExprForLoop(this, e)

    public open fun visitExprGroup(e: Expr.Group) =
        visitExprGroup(this, e)

    public open fun visitExprIf(e: Expr.If) =
        visitExprIf(this, e)

    public open fun visitExprIndex(e: Expr.Index) =
        visitExprIndex(this, e)

    public open fun visitExprInfer(e: Expr.Infer) =
        visitExprInfer(this, e)

    public open fun visitExprLet(e: Expr.Let) =
        visitExprLet(this, e)

    public open fun visitExprLit(e: Expr.Lit) =
        visitExprLit(this, e)

    public open fun visitExprLoop(e: Expr.Loop) =
        visitExprLoop(this, e)

    public open fun visitExprMacro(e: Expr.Macro) =
        visitExprMacro(this, e)

    public open fun visitExprMatch(e: Expr.Match) =
        visitExprMatch(this, e)

    public open fun visitExprMethodCall(e: Expr.MethodCall) =
        visitExprMethodCall(this, e)

    public open fun visitExprParen(e: Expr.Paren) =
        visitExprParen(this, e)

    public open fun visitExprPath(e: Expr.Path) =
        visitExprPath(this, e)

    public open fun visitExprRange(e: Expr.Range) =
        visitExprRange(this, e)

    public open fun visitExprRawAddr(e: Expr.RawAddr) =
        visitExprRawAddr(this, e)

    public open fun visitExprReference(e: Expr.Reference) =
        visitExprReference(this, e)

    public open fun visitExprRepeat(e: Expr.Repeat) =
        visitExprRepeat(this, e)

    public open fun visitExprReturn(e: Expr.Return) =
        visitExprReturn(this, e)

    public open fun visitExprStruct(e: Expr.Struct) =
        visitExprStruct(this, e)

    public open fun visitExprTry(e: Expr.Try) =
        visitExprTry(this, e)

    public open fun visitExprTryBlock(e: Expr.TryBlock) =
        visitExprTryBlock(this, e)

    public open fun visitExprTuple(e: Expr.Tuple) =
        visitExprTuple(this, e)

    public open fun visitExprUnary(e: Expr.Unary) =
        visitExprUnary(this, e)

    public open fun visitExprUnsafe(e: Expr.Unsafe) =
        visitExprUnsafe(this, e)

    public open fun visitExprWhile(e: Expr.While) =
        visitExprWhile(this, e)

    public open fun visitExprYield(e: Expr.Yield) =
        visitExprYield(this, e)

    public open fun visitType(t: SynType) =
        visitType(this, t)

    public open fun visitPath(p: Path) =
        visitPath(this, p)

    public open fun visitPat(p: Pat) =
        visitPat(this, p)

    public open fun visitItem(i: Item) =
        visitItem(this, i)

    public open fun visitAttribute(a: Attribute) =
        visitAttribute(this, a)

    public open fun visitAttrStyle(s: AttrStyle) =
        visitAttrStyle(this, s)

    public open fun visitMeta(m: Meta) =
        visitMeta(this, m)

    public open fun visitMetaList(m: Meta.List) =
        visitMetaList(this, m)

    public open fun visitMetaNameValue(m: Meta.NameValue) =
        visitMetaNameValue(this, m)

    public open fun visitGenerics(g: Generics) =
        visitGenerics(this, g)

    public open fun visitLit(l: Lit) =
        visitLit(this, l)

    public open fun visitLitBool(l: LitBool) =
        visitLitBool(this, l)

    public open fun visitLitByte(l: LitByte) =
        visitLitByte(this, l)

    public open fun visitLitByteStr(l: LitByteStr) =
        visitLitByteStr(this, l)

    public open fun visitLitCstr(l: LitCStr) =
        visitLitCstr(this, l)

    public open fun visitLitCStr(l: LitCStr) =
        visitLitCStr(this, l)

    public open fun visitLitChar(l: LitChar) =
        visitLitChar(this, l)

    public open fun visitLitFloat(l: LitFloat) =
        visitLitFloat(this, l)

    public open fun visitLitInt(l: LitInt) =
        visitLitInt(this, l)

    public open fun visitLitStr(l: LitStr) =
        visitLitStr(this, l)

    public open fun visitLocal(l: Stmt.Local) =
        visitLocal(this, l)

    public open fun visitLocalInit(i: LocalInit) =
        visitLocalInit(this, i)

    public open fun visitLifetime(lt: Lifetime) =
        visitLifetime(this, lt)

    public open fun visitIdent(id: Ident) =
        visitIdent(this, id)

    public open fun visitStmt(s: Stmt) =
        visitStmt(this, s)

    public open fun visitData(d: Data) =
        visitData(this, d)

    public open fun visitDataEnum(d: DataEnum) =
        visitDataEnum(this, d)

    public open fun visitDataStruct(d: DataStruct) =
        visitDataStruct(this, d)

    public open fun visitDataUnion(d: DataUnion) =
        visitDataUnion(this, d)

    public open fun visitDeriveInput(di: DeriveInput) =
        visitDeriveInput(this, di)

    public open fun visitAbi(a: Abi) =
        visitAbi(this, a)

    public open fun visitAngleBracketedGenericArguments(a: PathArguments.AngleBracketed) =
        visitAngleBracketedGenericArguments(this, a)

    public open fun visitAssocConst(a: AssocConst) =
        visitAssocConst(this, a)

    public open fun visitAssocType(a: AssocType) =
        visitAssocType(this, a)

    public open fun visitBareFnArg(a: BareFnArg) =
        visitBareFnArg(this, a)

    public open fun visitBareVariadic(v: BareVariadic) =
        visitBareVariadic(this, v)

    public open fun visitBlock(b: Block) =
        visitBlock(this, b)

    public open fun visitBoundLifetimes(b: BoundLifetimes) =
        visitBoundLifetimes(this, b)

    public open fun visitConstraint(c: Constraint) =
        visitConstraint(this, c)

    public open fun visitField(f: Field) =
        visitField(this, f)

    public open fun visitFieldPat(f: FieldPat) =
        visitFieldPat(this, f)

    public open fun visitFieldMutability(f: FieldMutability) =
        visitFieldMutability(this, f)

    public open fun visitFields(f: Fields) =
        visitFields(this, f)

    public open fun visitFieldsNamed(f: FieldsNamed) =
        visitFieldsNamed(this, f)

    public open fun visitFieldsUnnamed(f: FieldsUnnamed) =
        visitFieldsUnnamed(this, f)

    public open fun visitFile(f: File) =
        visitFile(this, f)

    public open fun visitFnArg(a: FnArg) =
        visitFnArg(this, a)

    public open fun visitForeignItem(i: ForeignItem) =
        visitForeignItem(this, i)

    public open fun visitForeignItemFn(i: ForeignItem.Fn) =
        visitForeignItemFn(this, i)

    public open fun visitForeignItemMacro(i: ForeignItem.Macro) =
        visitForeignItemMacro(this, i)

    public open fun visitForeignItemStatic(i: ForeignItem.Static) =
        visitForeignItemStatic(this, i)

    public open fun visitForeignItemType(i: ForeignItem.ItemType) =
        visitForeignItemType(this, i)

    public open fun visitGenericArgument(g: GenericArgument) =
        visitGenericArgument(this, g)

    public open fun visitGenericParam(g: GenericParam) =
        visitGenericParam(this, g)

    public open fun visitConstParam(c: GenericParam.ConstParam) =
        visitConstParam(this, c)

    public open fun visitLifetimeParam(l: GenericParam.LifetimeParam) =
        visitLifetimeParam(this, l)

    public open fun visitTypeParam(t: GenericParam.TypeParam) =
        visitTypeParam(this, t)

    public open fun visitMacro(m: Macro) =
        visitMacro(this, m)

    public open fun visitMacroDelimiter(m: MacroDelimiter) =
        visitMacroDelimiter(this, m)

    public open fun visitMember(m: Member) =
        visitMember(this, m)

    public open fun visitIndex(i: Index) =
        visitIndex(this, i)

    public open fun visitImplItem(i: ImplItem) =
        visitImplItem(this, i)

    public open fun visitImplItemConst(i: ImplItem.Const) =
        visitImplItemConst(this, i)

    public open fun visitImplItemFn(i: ImplItem.Fn) =
        visitImplItemFn(this, i)

    public open fun visitImplItemMacro(i: ImplItem.Macro) =
        visitImplItemMacro(this, i)

    public open fun visitImplItemType(i: ImplItem.AssocType) =
        visitImplItemType(this, i)

    public open fun visitImplRestriction(i: ImplRestriction) =
        visitImplRestriction(this, i)

    public open fun visitItemConst(i: Item.Const) =
        visitItemConst(this, i)

    public open fun visitItemEnum(i: Item.Enum) =
        visitItemEnum(this, i)

    public open fun visitItemExternCrate(i: Item.ExternCrate) =
        visitItemExternCrate(this, i)

    public open fun visitItemFn(i: Item.Fn) =
        visitItemFn(this, i)

    public open fun visitItemForeignMod(i: Item.ForeignMod) =
        visitItemForeignMod(this, i)

    public open fun visitItemImpl(i: Item.Impl) =
        visitItemImpl(this, i)

    public open fun visitItemMacro(i: Item.Macro) =
        visitItemMacro(this, i)

    public open fun visitItemMod(i: Item.Mod) =
        visitItemMod(this, i)

    public open fun visitItemStatic(i: Item.Static) =
        visitItemStatic(this, i)

    public open fun visitItemStruct(i: Item.Struct) =
        visitItemStruct(this, i)

    public open fun visitItemTrait(i: Item.Trait) =
        visitItemTrait(this, i)

    public open fun visitItemTraitAlias(i: Item.TraitAlias) =
        visitItemTraitAlias(this, i)

    public open fun visitItemType(i: Item.ItemType) =
        visitItemType(this, i)

    public open fun visitItemUnion(i: Item.Union) =
        visitItemUnion(this, i)

    public open fun visitItemUse(i: Item.Use) =
        visitItemUse(this, i)

    public open fun visitStaticMutability(mutability: StaticMutability) =
        visitStaticMutability(this, mutability)

    public open fun visitModContent(m: ModContent) =
        visitModContent(this, m)

    public open fun visitParenthesizedGenericArguments(p: PathArguments.Parenthesized) =
        visitParenthesizedGenericArguments(this, p)

    public open fun visitPatConst(p: Pat.Const) =
        visitPatConst(this, p)

    public open fun visitPatIdent(p: Pat.Ident) =
        visitPatIdent(this, p)

    public open fun visitPatLit(p: Pat.Lit) =
        visitPatLit(this, p)

    public open fun visitPatMacro(p: Pat.Macro) =
        visitPatMacro(this, p)

    public open fun visitPatOr(p: Pat.Or) =
        visitPatOr(this, p)

    public open fun visitPatParen(p: Pat.PatParen) =
        visitPatParen(this, p)

    public open fun visitPatPath(p: Pat.Path) =
        visitPatPath(this, p)

    public open fun visitPatRange(p: Pat.Range) =
        visitPatRange(this, p)

    public open fun visitPatReference(p: Pat.Reference) =
        visitPatReference(this, p)

    public open fun visitPatRest(p: Pat.Rest) =
        visitPatRest(this, p)

    public open fun visitPatRest(p: PatRest) =
        visitPatRest(this, p)

    public open fun visitPatSlice(p: Pat.Slice) =
        visitPatSlice(this, p)

    public open fun visitPatStruct(p: Pat.Struct) =
        visitPatStruct(this, p)

    public open fun visitPatTuple(p: Pat.Tuple) =
        visitPatTuple(this, p)

    public open fun visitPatTupleStruct(p: Pat.TupleStruct) =
        visitPatTupleStruct(this, p)

    public open fun visitPatType(p: PatType) =
        visitPatType(this, p)

    public open fun visitPatTypeAscription(p: Pat.TypeAscription) =
        visitPatTypeAscription(this, p)

    public open fun visitPatWild(p: Pat.Wild) =
        visitPatWild(this, p)

    public open fun visitPathTrait(p: PathTrait) =
        visitPathTrait(this, p)

    public open fun visitPathArguments(p: PathArguments) =
        visitPathArguments(this, p)

    public open fun visitPathSegment(p: PathSegment) =
        visitPathSegment(this, p)

    public open fun visitArm(arm: io.github.kotlinmania.syn.Arm) =
        visitArm(this, arm)

    public open fun visitElseExpr(elseExpr: ElseExpr) =
        visitElseExpr(this, elseExpr)

    public open fun visitFieldValue(fieldValue: FieldValue) =
        visitFieldValue(this, fieldValue)

    public open fun visitPredicateLifetime(p: WherePredicate.LifetimePredicate) =
        visitPredicateLifetime(this, p)

    public open fun visitPredicateType(p: WherePredicate.TypePredicate) =
        visitPredicateType(this, p)

    public open fun visitLabel(label: Label) =
        visitLabel(this, label)

    public open fun visitQself(q: QSelf) =
        visitQself(this, q)

    public open fun visitQSelf(q: QSelf) =
        visitQSelf(this, q)

    public open fun visitRangeLimits(limits: RangeLimits) =
        visitRangeLimits(this, limits)

    public open fun visitReceiver(r: FnArg.Receiver) =
        visitReceiver(this, r)

    public open fun visitReturnType(r: ReturnType) =
        visitReturnType(this, r)

    public open fun visitSignature(s: Signature) =
        visitSignature(this, s)

    public open fun visitStmtMacro(s: Stmt.MacroStmt) =
        visitStmtMacro(this, s)

    public open fun visitTraitBound(t: TypeParamBound.Trait) =
        visitTraitBound(this, t)

    public open fun visitTraitBoundModifier(t: TraitBoundModifier) =
        visitTraitBoundModifier(this, t)

    public open fun visitTraitItem(t: TraitItem) =
        visitTraitItem(this, t)

    public open fun visitTraitItemConst(t: TraitItem.Const) =
        visitTraitItemConst(this, t)

    public open fun visitTraitItemFn(t: TraitItem.Fn) =
        visitTraitItemFn(this, t)

    public open fun visitTraitItemMacro(t: TraitItem.Macro) =
        visitTraitItemMacro(this, t)

    public open fun visitTraitItemType(t: TraitItem.AssocType) =
        visitTraitItemType(this, t)

    public open fun visitTypeBareFn(t: SynType.BareFn) =
        visitTypeBareFn(this, t)

    public open fun visitTypeArray(t: SynType.Array) =
        visitTypeArray(this, t)

    public open fun visitTypeGroup(t: SynType.Group) =
        visitTypeGroup(this, t)

    public open fun visitTypeImplTrait(t: SynType.ImplTrait) =
        visitTypeImplTrait(this, t)

    public open fun visitTypeInfer(t: SynType.Infer) =
        visitTypeInfer(this, t)

    public open fun visitTypeMacro(t: SynType.Macro) =
        visitTypeMacro(this, t)

    public open fun visitTypeNever(t: SynType.Never) =
        visitTypeNever(this, t)

    public open fun visitTypeParamBound(t: TypeParamBound) =
        visitTypeParamBound(this, t)

    public open fun visitTypeParen(t: SynType.Paren) =
        visitTypeParen(this, t)

    public open fun visitTypePath(t: SynType.Path) =
        visitTypePath(this, t)

    public open fun visitTypePtr(t: SynType.Ptr) =
        visitTypePtr(this, t)

    public open fun visitTypeReference(t: SynType.Reference) =
        visitTypeReference(this, t)

    public open fun visitTypeSlice(t: SynType.Slice) =
        visitTypeSlice(this, t)

    public open fun visitTypeTraitObject(t: SynType.TraitObject) =
        visitTypeTraitObject(this, t)

    public open fun visitTypeTuple(t: SynType.Tuple) =
        visitTypeTuple(this, t)

    public open fun visitUnOp(op: UnOp) =
        visitUnOp(this, op)

    public open fun visitBinOp(op: BinOp) =
        visitBinOp(this, op)

    public open fun visitPointerMutability(mutability: io.github.kotlinmania.syn.token.Mut?) =
        visitPointerMutability(this, mutability)

    public open fun visitPointerMutability(mutability: PointerMutability) =
        visitPointerMutability(this, mutability)

    public open fun visitUseGlob(u: UseTree.Glob) =
        visitUseGlob(this, u)

    public open fun visitUseGroup(u: UseTree.Group) =
        visitUseGroup(this, u)

    public open fun visitUseName(u: UseTree.Name) =
        visitUseName(this, u)

    public open fun visitUsePath(u: UseTree.Path) =
        visitUsePath(this, u)

    public open fun visitUseRename(u: UseTree.Name) =
        visitUseRename(this, u)

    public open fun visitUseTree(u: UseTree) =
        visitUseTree(this, u)

    public open fun visitVariadic(v: Variadic) =
        visitVariadic(this, v)

    public open fun visitPreciseCapture(p: TypeParamBound.PreciseCapture) =
        visitPreciseCapture(this, p)

    public open fun visitCapturedParam(c: CapturedParam) =
        visitCapturedParam(this, c)

    public open fun visitVariant(v: Variant) =
        visitVariant(this, v)

    public open fun visitVisRestricted(v: Visibility.Restricted) =
        visitVisRestricted(this, v)

    public open fun visitVisibility(v: Visibility) =
        visitVisibility(this, v)

    public open fun visitWhereClause(w: WhereClause) =
        visitWhereClause(this, w)

    public open fun visitWherePredicate(w: WherePredicate) =
        visitWherePredicate(this, w)

    public open fun visitSpan(s: Span) =
        visitSpan(this, s)

    public open fun visitTokenStream(t: TokenStream) =
        visitTokenStream(this, t)
}

public fun visitExpr(v: Visit, e: Expr) =
    when (e) {
        is Expr.Array -> v.visitExprArray(e)
        is Expr.Assign -> v.visitExprAssign(e)
        is Expr.Async -> v.visitExprAsync(e)
        is Expr.Await -> v.visitExprAwait(e)
        is Expr.Binary -> v.visitExprBinary(e)
        is Expr.BlockExpr -> v.visitExprBlock(e)
        is Expr.Break -> v.visitExprBreak(e)
        is Expr.Call -> v.visitExprCall(e)
        is Expr.Cast -> v.visitExprCast(e)
        is Expr.Closure -> v.visitExprClosure(e)
        is Expr.Const -> v.visitExprConst(e)
        is Expr.Continue -> v.visitExprContinue(e)
        is Expr.Field -> v.visitExprField(e)
        is Expr.ForLoop -> v.visitExprForLoop(e)
        is Expr.Group -> v.visitExprGroup(e)
        is Expr.If -> v.visitExprIf(e)
        is Expr.Index -> v.visitExprIndex(e)
        is Expr.Infer -> v.visitExprInfer(e)
        is Expr.Let -> v.visitExprLet(e)
        is Expr.Lit -> v.visitExprLit(e)
        is Expr.Loop -> v.visitExprLoop(e)
        is Expr.Macro -> v.visitExprMacro(e)
        is Expr.Match -> v.visitExprMatch(e)
        is Expr.MethodCall -> v.visitExprMethodCall(e)
        is Expr.Paren -> v.visitExprParen(e)
        is Expr.Path -> v.visitExprPath(e)
        is Expr.Range -> v.visitExprRange(e)
        is Expr.RawAddr -> v.visitExprRawAddr(e)
        is Expr.Reference -> v.visitExprReference(e)
        is Expr.Repeat -> v.visitExprRepeat(e)
        is Expr.Return -> v.visitExprReturn(e)
        is Expr.Struct -> v.visitExprStruct(e)
        is Expr.Try -> v.visitExprTry(e)
        is Expr.TryBlock -> v.visitExprTryBlock(e)
        is Expr.Tuple -> v.visitExprTuple(e)
        is Expr.Unary -> v.visitExprUnary(e)
        is Expr.Unsafe -> v.visitExprUnsafe(e)
        is Expr.While -> v.visitExprWhile(e)
        is Expr.Yield -> v.visitExprYield(e)
        is Expr.Verbatim -> v.visitTokenStream(e.tokens)
    }

public fun visitExprArray(v: Visit, e: Expr.Array) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.elems.toList().forEach { v.visitExpr(it) }
}

public fun visitExprAssign(v: Visit, e: Expr.Assign) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.left)
    v.visitExpr(e.right)
}

public fun visitExprAsync(v: Visit, e: Expr.Async) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitBlock(e.block)
}

public fun visitExprAwait(v: Visit, e: Expr.Await) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.base)
}

public fun visitExprBinary(v: Visit, e: Expr.Binary) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.left)
    v.visitBinOp(e.op)
    v.visitExpr(e.right)
}

public fun visitExprBlock(v: Visit, e: Expr.BlockExpr) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.label?.let { v.visitLabel(it) }
    v.visitBlock(e.block)
}

public fun visitExprBreak(v: Visit, e: Expr.Break) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.label?.let { v.visitLifetime(it) }
    e.expr?.let { v.visitExpr(it) }
}

public fun visitExprCall(v: Visit, e: Expr.Call) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.func)
    e.args.toList().forEach { v.visitExpr(it) }
}

public fun visitExprCast(v: Visit, e: Expr.Cast) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
    v.visitType(e.ty)
}

public fun visitExprClosure(v: Visit, e: Expr.Closure) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.lifetimes?.let { v.visitBoundLifetimes(it) }
    e.inputs.toList().forEach { v.visitPat(it) }
    v.visitReturnType(e.output)
    v.visitExpr(e.body)
}

public fun visitExprConst(v: Visit, e: Expr.Const) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitBlock(e.block)
}

public fun visitExprContinue(v: Visit, e: Expr.Continue) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.label?.let { v.visitLifetime(it) }
}

public fun visitExprField(v: Visit, e: Expr.Field) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.base)
    v.visitMember(e.member)
}

public fun visitExprForLoop(v: Visit, e: Expr.ForLoop) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.label?.let { v.visitLabel(it) }
    v.visitPat(e.pat)
    v.visitExpr(e.expr)
    v.visitBlock(e.body)
}

public fun visitExprGroup(v: Visit, e: Expr.Group) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
}

public fun visitExprIf(v: Visit, e: Expr.If) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.cond)
    v.visitBlock(e.thenBranch)
    e.elseBranch?.let { v.visitElseExpr(it) }
}

public fun visitExprIndex(v: Visit, e: Expr.Index) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
    v.visitExpr(e.index)
}

public fun visitExprInfer(v: Visit, e: Expr.Infer) {
    e.attrs.forEach { v.visitAttribute(it) }
}

public fun visitExprLet(v: Visit, e: Expr.Let) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitPat(e.pat)
    v.visitExpr(e.expr)
}

public fun visitExprLit(v: Visit, e: Expr.Lit) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitLit(e.lit)
}

public fun visitExprLoop(v: Visit, e: Expr.Loop) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.label?.let { v.visitLabel(it) }
    v.visitBlock(e.body)
}

public fun visitExprMacro(v: Visit, e: Expr.Macro) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitMacro(e.mac)
}

public fun visitExprMatch(v: Visit, e: Expr.Match) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
    e.arms.forEach { v.visitArm(it) }
}

public fun visitExprMethodCall(v: Visit, e: Expr.MethodCall) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.receiver)
    v.visitIdent(e.method)
    e.turbofish?.let { v.visitAngleBracketedGenericArguments(it) }
    e.args.toList().forEach { v.visitExpr(it) }
}

public fun visitExprParen(v: Visit, e: Expr.Paren) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
}

public fun visitExprPath(v: Visit, e: Expr.Path) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.qself?.let { v.visitQself(it) }
    v.visitPath(e.path)
}

public fun visitExprRange(v: Visit, e: Expr.Range) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.start?.let { v.visitExpr(it) }
    v.visitRangeLimits(e.limits)
    e.end?.let { v.visitExpr(it) }
}

public fun visitExprRawAddr(v: Visit, e: Expr.RawAddr) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitPointerMutability(e.mutability)
    v.visitExpr(e.expr)
}

public fun visitExprReference(v: Visit, e: Expr.Reference) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
}

public fun visitExprRepeat(v: Visit, e: Expr.Repeat) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
    v.visitExpr(e.len)
}

public fun visitExprReturn(v: Visit, e: Expr.Return) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.expr?.let { v.visitExpr(it) }
}

public fun visitExprStruct(v: Visit, e: Expr.Struct) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.qself?.let { v.visitQself(it) }
    v.visitPath(e.path)
    e.fields.toList().forEach { v.visitFieldValue(it) }
    e.rest?.let { v.visitExpr(it) }
}

public fun visitExprTry(v: Visit, e: Expr.Try) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitExpr(e.expr)
}

public fun visitExprTryBlock(v: Visit, e: Expr.TryBlock) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitBlock(e.block)
}

public fun visitExprTuple(v: Visit, e: Expr.Tuple) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.elems.toList().forEach { v.visitExpr(it) }
}

public fun visitExprUnary(v: Visit, e: Expr.Unary) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitUnOp(e.op)
    v.visitExpr(e.expr)
}

public fun visitExprUnsafe(v: Visit, e: Expr.Unsafe) {
    e.attrs.forEach { v.visitAttribute(it) }
    v.visitBlock(e.block)
}

public fun visitExprWhile(v: Visit, e: Expr.While) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.label?.let { v.visitLabel(it) }
    v.visitExpr(e.cond)
    v.visitBlock(e.body)
}

public fun visitExprYield(v: Visit, e: Expr.Yield) {
    e.attrs.forEach { v.visitAttribute(it) }
    e.expr?.let { v.visitExpr(it) }
}

public fun visitType(v: Visit, t: SynType) =
    when (t) {
        is SynType.Array -> v.visitTypeArray(t)
        is SynType.BareFn -> v.visitTypeBareFn(t)
        is SynType.Group -> v.visitTypeGroup(t)
        is SynType.ImplTrait -> v.visitTypeImplTrait(t)
        is SynType.Infer -> v.visitTypeInfer(t)
        is SynType.Macro -> v.visitTypeMacro(t)
        is SynType.Never -> v.visitTypeNever(t)
        is SynType.Paren -> v.visitTypeParen(t)
        is SynType.Path -> v.visitTypePath(t)
        is SynType.Ptr -> v.visitTypePtr(t)
        is SynType.Reference -> v.visitTypeReference(t)
        is SynType.Slice -> v.visitTypeSlice(t)
        is SynType.TraitObject -> v.visitTypeTraitObject(t)
        is SynType.Tuple -> v.visitTypeTuple(t)
        is SynType.Verbatim -> v.visitTokenStream(t.tokens)
    }

public fun visitPath(v: Visit, p: Path) {
    p.segments.toList().forEach { v.visitPathSegment(it) }
}

public fun visitPat(v: Visit, p: Pat) =
    when (p) {
        is Pat.Const -> v.visitPatConst(p)
        is Pat.Ident -> v.visitPatIdent(p)
        is Pat.Lit -> v.visitPatLit(p)
        is Pat.Macro -> v.visitPatMacro(p)
        is Pat.Or -> v.visitPatOr(p)
        is Pat.PatParen -> v.visitPatParen(p)
        is Pat.Path -> v.visitPatPath(p)
        is Pat.Range -> v.visitPatRange(p)
        is Pat.Reference -> v.visitPatReference(p)
        is Pat.Rest -> v.visitPatRest(p)
        is Pat.Slice -> v.visitPatSlice(p)
        is Pat.Struct -> v.visitPatStruct(p)
        is Pat.Tuple -> v.visitPatTuple(p)
        is Pat.TupleStruct -> v.visitPatTupleStruct(p)
        is Pat.TypeAscription -> v.visitPatTypeAscription(p)
        is Pat.Verbatim -> v.visitTokenStream(p.tokens)
        is Pat.Wild -> v.visitPatWild(p)
    }

public fun visitItem(v: Visit, i: Item) =
    when (i) {
        is Item.Const -> v.visitItemConst(i)
        is Item.Enum -> v.visitItemEnum(i)
        is Item.ExternCrate -> v.visitItemExternCrate(i)
        is Item.Fn -> v.visitItemFn(i)
        is Item.ForeignMod -> v.visitItemForeignMod(i)
        is Item.Impl -> v.visitItemImpl(i)
        is Item.Macro -> v.visitItemMacro(i)
        is Item.Mod -> v.visitItemMod(i)
        is Item.Static -> v.visitItemStatic(i)
        is Item.Struct -> v.visitItemStruct(i)
        is Item.Trait -> v.visitItemTrait(i)
        is Item.TraitAlias -> v.visitItemTraitAlias(i)
        is Item.ItemType -> v.visitItemType(i)
        is Item.Union -> v.visitItemUnion(i)
        is Item.Use -> v.visitItemUse(i)
        is Item.Verbatim -> v.visitTokenStream(i.tokens)
    }

public fun visitAttribute(v: Visit, a: Attribute) {
    v.visitAttrStyle(a.style)
    v.visitMeta(a.meta)
}

public fun visitAttrStyle(v: Visit, s: AttrStyle) =
    when (s) {
        AttrStyle.Outer -> Unit
        is AttrStyle.Inner -> Unit
    }

public fun visitMeta(v: Visit, m: Meta) =
    when (m) {
        is Meta.PathMeta -> v.visitPath(m.path)
        is Meta.List -> v.visitMetaList(m)
        is Meta.NameValue -> v.visitMetaNameValue(m)
    }

public fun visitMetaList(v: Visit, m: Meta.List) {
    v.visitPath(m.path)
    v.visitMacroDelimiter(m.delimiter)
    v.visitTokenStream(m.tokens)
}

public fun visitMetaNameValue(v: Visit, m: Meta.NameValue) {
    v.visitPath(m.path)
    v.visitExpr(m.value)
}

public fun visitGenerics(v: Visit, g: Generics) {
    g.params.toList().forEach { v.visitGenericParam(it) }
    g.whereClause?.let { v.visitWhereClause(it) }
}

public fun visitLit(v: Visit, l: Lit) =
    when (l) {
        is Lit.Str -> {
            v.visitLitStr(l.value)
        }

        is Lit.ByteStr -> {
            v.visitLitByteStr(l.value)
        }

        is Lit.CStr -> {
            v.visitLitCstr(l.value)
        }

        is Lit.Byte -> {
            v.visitLitByte(l.value)
        }

        is Lit.Char -> {
            v.visitLitChar(l.value)
        }

        is Lit.Int -> {
            v.visitLitInt(l.value)
        }

        is Lit.Float -> {
            v.visitLitFloat(l.value)
        }

        is Lit.Bool -> {
            v.visitLitBool(l.value)
        }

        is Lit.Verbatim -> Unit
    }

public fun visitLitBool(v: Visit, l: LitBool) {
    v.visitSpan(l.span())
}

public fun visitLitByte(v: Visit, l: LitByte) = Unit

public fun visitLitByteStr(v: Visit, l: LitByteStr) = Unit

public fun visitLitCstr(v: Visit, l: LitCStr) {
    v.visitLitCStr(l)
}

public fun visitLitCStr(v: Visit, l: LitCStr) = Unit

public fun visitLitChar(v: Visit, l: LitChar) = Unit

public fun visitLitFloat(v: Visit, l: LitFloat) = Unit

public fun visitLitInt(v: Visit, l: LitInt) = Unit

public fun visitLitStr(v: Visit, l: LitStr) = Unit

public fun visitLocal(v: Visit, l: Stmt.Local) {
    l.attrs.forEach { v.visitAttribute(it) }
    v.visitPat(l.pat)
    l.init?.let { v.visitLocalInit(it) }
}

public fun visitLocalInit(v: Visit, i: LocalInit) {
    v.visitExpr(i.expr)
    i.diverge?.let { v.visitExpr(it.expr) }
}

public fun visitLifetime(v: Visit, lt: Lifetime) {
    v.visitSpan(lt.apostrophe)
    v.visitIdent(lt.ident)
}

public fun visitIdent(v: Visit, id: Ident) {
    v.visitSpan(id.span())
}

public fun visitStmt(v: Visit, s: Stmt) =
    when (s) {
        is Stmt.Local -> v.visitLocal(s)
        is Stmt.ItemStmt -> v.visitItem(s.item)
        is Stmt.ExprStmt -> v.visitExpr(s.expr)
        is Stmt.MacroStmt -> v.visitStmtMacro(s)
    }

public fun visitData(v: Visit, d: Data) =
    when (d) {
        is Data.Struct -> v.visitDataStruct(d.value)
        is Data.Enum -> v.visitDataEnum(d.value)
        is Data.Union -> v.visitDataUnion(d.value)
    }

public fun visitDataEnum(v: Visit, d: DataEnum) {
    d.variants.toList().forEach { v.visitVariant(it) }
}

public fun visitDataStruct(v: Visit, d: DataStruct) {
    v.visitFields(d.fields)
}

public fun visitDataUnion(v: Visit, d: DataUnion) {
    v.visitFieldsNamed(d.fields)
}

public fun visitDeriveInput(v: Visit, di: DeriveInput) {
    di.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(di.vis)
    v.visitIdent(di.ident)
    v.visitGenerics(di.generics)
    v.visitData(di.data)
}

public fun visitAbi(v: Visit, a: Abi) {
    a.name?.let { v.visitLitStr(it) }
}

public fun visitAngleBracketedGenericArguments(v: Visit, a: PathArguments.AngleBracketed) {
    a.args.toList().forEach { v.visitGenericArgument(it) }
}

public fun visitAssocConst(v: Visit, a: AssocConst) {
    v.visitIdent(a.ident)
    a.generics?.let { v.visitAngleBracketedGenericArguments(it) }
    v.visitExpr(a.value)
}

public fun visitAssocType(v: Visit, a: AssocType) {
    v.visitIdent(a.ident)
    a.generics?.let { v.visitAngleBracketedGenericArguments(it) }
    v.visitType(a.ty)
}

public fun visitBareFnArg(v: Visit, a: BareFnArg) {
    a.attrs.forEach { v.visitAttribute(it) }
    a.name?.let { v.visitIdent(it.ident) }
    v.visitType(a.ty)
}

public fun visitBareVariadic(v: Visit, node: BareVariadic) {
    node.attrs.forEach { v.visitAttribute(it) }
    node.name?.let { v.visitIdent(it.ident) }
}

public fun visitBlock(v: Visit, b: Block) {
    b.stmts.forEach { v.visitStmt(it) }
}

public fun visitBoundLifetimes(v: Visit, b: BoundLifetimes) {
    b.lifetimes.toList().forEach { v.visitGenericParam(it) }
}

public fun visitConstraint(v: Visit, c: Constraint) {
    v.visitIdent(c.ident)
    c.generics?.let { v.visitAngleBracketedGenericArguments(it) }
    c.bounds.toList().forEach { v.visitTypeParamBound(it) }
}

public fun visitField(v: Visit, f: Field) {
    f.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(f.vis)
    v.visitFieldMutability(f.mutability)
    f.ident?.let { v.visitIdent(it) }
    v.visitType(f.ty)
}

public fun visitFieldPat(v: Visit, f: FieldPat) {
    f.attrs.forEach { v.visitAttribute(it) }
    v.visitMember(f.member)
    v.visitPat(f.pat)
}

public fun visitFieldMutability(v: Visit, f: FieldMutability) =
    when (f) {
        FieldMutability.None -> Unit
        is FieldMutability.Mut -> Unit
    }

public fun visitFields(v: Visit, f: Fields) =
    when (f) {
        is Fields.Named -> {
            v.visitFieldsNamed(f.fields)
        }

        is Fields.Unnamed -> {
            v.visitFieldsUnnamed(f.fields)
        }

        Fields.Unit -> Unit
    }

public fun visitFieldsNamed(v: Visit, f: FieldsNamed) {
    f.named.toList().forEach { v.visitField(it) }
}

public fun visitFieldsUnnamed(v: Visit, f: FieldsUnnamed) {
    f.unnamed.toList().forEach { v.visitField(it) }
}

public fun visitFile(v: Visit, f: File) {
    f.attrs.forEach { v.visitAttribute(it) }
    f.items.forEach { v.visitItem(it) }
}

public fun visitFnArg(v: Visit, a: FnArg) =
    when (a) {
        is FnArg.Receiver -> v.visitReceiver(a)
        is FnArg.Typed -> v.visitPatType(a.patType)
    }

public fun visitForeignItem(v: Visit, i: ForeignItem) =
    when (i) {
        is ForeignItem.Fn -> v.visitForeignItemFn(i)
        is ForeignItem.Static -> v.visitForeignItemStatic(i)
        is ForeignItem.ItemType -> v.visitForeignItemType(i)
        is ForeignItem.Macro -> v.visitForeignItemMacro(i)
        is ForeignItem.Verbatim -> v.visitTokenStream(i.tokens)
    }

public fun visitForeignItemFn(v: Visit, i: ForeignItem.Fn) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitSignature(i.sig)
}

public fun visitForeignItemMacro(v: Visit, i: ForeignItem.Macro) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitMacro(i.mac)
}

public fun visitForeignItemStatic(v: Visit, i: ForeignItem.Static) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitStaticMutability(i.mutability)
    v.visitIdent(i.ident)
    v.visitType(i.ty)
}

public fun visitForeignItemType(v: Visit, i: ForeignItem.ItemType) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
}

public fun visitGenericArgument(v: Visit, g: GenericArgument) =
    when (g) {
        is GenericArgument.LifetimeArg -> v.visitLifetime(g.lifetime)
        is GenericArgument.TypeArg -> v.visitType(g.type)
        is GenericArgument.ConstArg -> v.visitExpr(g.expr)
        is GenericArgument.AssocTypeArg -> v.visitAssocType(g.assoc)
        is GenericArgument.AssocConstArg -> v.visitAssocConst(g.assoc)
        is GenericArgument.ConstraintArg -> v.visitConstraint(g.constraint)
    }

public fun visitGenericParam(v: Visit, g: GenericParam) =
    when (g) {
        is GenericParam.LifetimeParam -> v.visitLifetimeParam(g)
        is GenericParam.TypeParam -> v.visitTypeParam(g)
        is GenericParam.ConstParam -> v.visitConstParam(g)
    }

public fun visitConstParam(v: Visit, c: GenericParam.ConstParam) {
    c.attrs.forEach { v.visitAttribute(it) }
    v.visitIdent(c.ident)
    v.visitType(c.ty)
    c.default?.let { v.visitExpr(it) }
}

public fun visitLifetimeParam(v: Visit, l: GenericParam.LifetimeParam) {
    l.attrs.forEach { v.visitAttribute(it) }
    v.visitLifetime(l.lifetime)
    l.bounds.toList().forEach { v.visitLifetime(it) }
}

public fun visitTypeParam(v: Visit, t: GenericParam.TypeParam) {
    t.attrs.forEach { v.visitAttribute(it) }
    v.visitIdent(t.ident)
    t.bounds.toList().forEach { v.visitTypeParamBound(it) }
    t.default?.let { v.visitType(it) }
}

public fun visitMacro(v: Visit, m: Macro) {
    v.visitPath(m.path)
    v.visitMacroDelimiter(m.delimiter)
    v.visitTokenStream(m.tokens)
}

public fun visitMacroDelimiter(v: Visit, m: MacroDelimiter) =
    when (m) {
        is MacroDelimiter.Paren -> Unit
        is MacroDelimiter.Brace -> Unit
        is MacroDelimiter.Bracket -> Unit
    }

public fun visitMember(v: Visit, m: Member) =
    when (m) {
        is Member.Named -> v.visitIdent(m.ident)
        is Member.Unnamed -> v.visitIndex(m.index)
    }

public fun visitIndex(v: Visit, i: Index) {
    v.visitSpan(i.span)
}

public fun visitImplItem(v: Visit, i: ImplItem) =
    when (i) {
        is ImplItem.Const -> v.visitImplItemConst(i)
        is ImplItem.Fn -> v.visitImplItemFn(i)
        is ImplItem.AssocType -> v.visitImplItemType(i)
        is ImplItem.Macro -> v.visitImplItemMacro(i)
        is ImplItem.Verbatim -> v.visitTokenStream(i.tokens)
    }

public fun visitImplItemConst(v: Visit, i: ImplItem.Const) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    v.visitType(i.ty)
    v.visitExpr(i.expr)
}

public fun visitImplItemFn(v: Visit, i: ImplItem.Fn) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitSignature(i.sig)
    v.visitBlock(i.block)
}

public fun visitImplItemMacro(v: Visit, i: ImplItem.Macro) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitMacro(i.mac)
}

public fun visitImplItemType(v: Visit, i: ImplItem.AssocType) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    v.visitType(i.ty)
}

public fun visitImplRestriction(v: Visit, i: ImplRestriction) = Unit

public fun visitItemConst(v: Visit, i: Item.Const) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitType(i.ty)
    i.expr?.let { v.visitExpr(it) }
}

public fun visitItemEnum(v: Visit, i: Item.Enum) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    i.variants.toList().forEach { v.visitVariant(it) }
}

public fun visitItemExternCrate(v: Visit, i: Item.ExternCrate) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    i.rename?.let { v.visitIdent(it.ident) }
}

public fun visitItemFn(v: Visit, i: Item.Fn) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitSignature(i.sig)
    i.block?.let { v.visitBlock(it) }
}

public fun visitItemForeignMod(v: Visit, i: Item.ForeignMod) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitAbi(i.abi)
    i.items.forEach { v.visitForeignItem(it) }
}

public fun visitItemImpl(v: Visit, i: Item.Impl) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitGenerics(i.generics)
    i.traitPath?.let { v.visitPathTrait(it) }
    v.visitType(i.selfType)
    i.items.forEach { v.visitImplItem(it) }
}

public fun visitItemMacro(v: Visit, i: Item.Macro) {
    i.attrs.forEach { v.visitAttribute(it) }
    i.ident?.let { v.visitIdent(it) }
    v.visitMacro(i.mac)
}

public fun visitItemMod(v: Visit, i: Item.Mod) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    i.content?.let { v.visitModContent(it) }
}

public fun visitItemStatic(v: Visit, i: Item.Static) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitStaticMutability(i.mutability)
    v.visitIdent(i.ident)
    v.visitType(i.ty)
    v.visitExpr(i.expr)
}

public fun visitItemStruct(v: Visit, i: Item.Struct) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    v.visitFields(i.fields)
}

public fun visitItemTrait(v: Visit, i: Item.Trait) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    i.restriction?.let { v.visitImplRestriction(it) }
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    i.supertraits.toList().forEach { v.visitTypeParamBound(it) }
    i.items.forEach { v.visitTraitItem(it) }
}

public fun visitItemTraitAlias(v: Visit, i: Item.TraitAlias) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    i.bounds.toList().forEach { v.visitTypeParamBound(it) }
}

public fun visitItemType(v: Visit, i: Item.ItemType) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    v.visitType(i.ty)
}

public fun visitItemUnion(v: Visit, i: Item.Union) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitIdent(i.ident)
    v.visitGenerics(i.generics)
    v.visitFieldsNamed(i.fields)
}

public fun visitItemUse(v: Visit, i: Item.Use) {
    i.attrs.forEach { v.visitAttribute(it) }
    v.visitVisibility(i.vis)
    v.visitUseTree(i.tree)
}

public fun visitStaticMutability(v: Visit, mutability: StaticMutability) = Unit

public fun visitModContent(v: Visit, m: ModContent) =
    when (m) {
        is ModContent.Inline -> {
            m.items.forEach { v.visitItem(it) }
        }

        is ModContent.Unnamed -> Unit
    }

public fun visitParenthesizedGenericArguments(v: Visit, p: PathArguments.Parenthesized) {
    p.inputs.toList().forEach { v.visitType(it) }
    v.visitReturnType(p.output)
}

public fun visitPatConst(v: Visit, p: Pat.Const) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitBlock(p.block)
}

public fun visitPatIdent(v: Visit, p: Pat.Ident) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitFieldMutability(p.mutability)
    v.visitIdent(p.ident)
    p.subpat?.let { v.visitPat(it) }
}

public fun visitPatLit(v: Visit, p: Pat.Lit) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitLit(p.lit)
}

public fun visitPatMacro(v: Visit, p: Pat.Macro) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitMacro(p.mac)
}

public fun visitPatOr(v: Visit, p: Pat.Or) {
    p.attrs.forEach { v.visitAttribute(it) }
    p.cases.toList().forEach { v.visitPat(it) }
}

public fun visitPatParen(v: Visit, p: Pat.PatParen) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitPat(p.pat)
}

public fun visitPatPath(v: Visit, p: Pat.Path) {
    p.attrs.forEach { v.visitAttribute(it) }
    p.qself?.let { v.visitQself(it) }
    v.visitPath(p.path)
}

public fun visitPatRange(v: Visit, p: Pat.Range) {
    p.attrs.forEach { v.visitAttribute(it) }
    p.start?.let { v.visitExpr(it) }
    v.visitRangeLimits(p.limits)
    p.end?.let { v.visitExpr(it) }
}

public fun visitPatReference(v: Visit, p: Pat.Reference) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitFieldMutability(p.mutability)
    v.visitPat(p.pat)
}

public fun visitPatRest(v: Visit, p: Pat.Rest) {
    p.attrs.forEach { v.visitAttribute(it) }
}

public fun visitPatRest(v: Visit, p: PatRest) {
    p.attrs.forEach { v.visitAttribute(it) }
}

public fun visitPatSlice(v: Visit, p: Pat.Slice) {
    p.attrs.forEach { v.visitAttribute(it) }
    p.elems.toList().forEach { v.visitPat(it) }
}

public fun visitPatStruct(v: Visit, p: Pat.Struct) {
    p.attrs.forEach { v.visitAttribute(it) }
    p.qself?.let { v.visitQself(it) }
    v.visitPath(p.path)
    p.fields.toList().forEach { v.visitFieldPat(it) }
    p.rest?.let { v.visitPatRest(it) }
}

public fun visitPatTuple(v: Visit, p: Pat.Tuple) {
    p.attrs.forEach { v.visitAttribute(it) }
    p.elems.toList().forEach { v.visitPat(it) }
}

public fun visitPatTupleStruct(v: Visit, p: Pat.TupleStruct) {
    p.attrs.forEach { v.visitAttribute(it) }
    p.qself?.let { v.visitQself(it) }
    v.visitPath(p.path)
    p.elems.toList().forEach { v.visitPat(it) }
}

public fun visitPatType(v: Visit, p: PatType) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitPat(p.pat)
    v.visitType(p.ty)
}

public fun visitPatTypeAscription(v: Visit, p: Pat.TypeAscription) {
    p.attrs.forEach { v.visitAttribute(it) }
    v.visitPat(p.pat)
    v.visitType(p.ty)
}

public fun visitPatWild(v: Visit, p: Pat.Wild) {
    p.attrs.forEach { v.visitAttribute(it) }
}

public fun visitPathTrait(v: Visit, p: PathTrait) {
    v.visitPath(p.path)
}

public fun visitPathArguments(v: Visit, p: PathArguments) =
    when (p) {
        PathArguments.None -> Unit

        is PathArguments.AngleBracketed -> {
            v.visitAngleBracketedGenericArguments(p)
        }

        is PathArguments.Parenthesized -> {
            v.visitParenthesizedGenericArguments(p)
        }
    }

public fun visitPathSegment(v: Visit, p: PathSegment) {
    v.visitIdent(p.ident)
    v.visitPathArguments(p.arguments)
}

public fun visitArm(v: Visit, arm: io.github.kotlinmania.syn.Arm) {
    arm.attrs.forEach { v.visitAttribute(it) }
    v.visitPat(arm.pat)
    arm.guard?.let { v.visitExpr(it.expr) }
    v.visitExpr(arm.body)
}

public fun visitElseExpr(v: Visit, elseExpr: ElseExpr) {
    v.visitExpr(elseExpr.expr)
}

public fun visitFieldValue(v: Visit, fieldValue: FieldValue) {
    fieldValue.attrs.forEach { v.visitAttribute(it) }
    v.visitMember(fieldValue.member)
    v.visitExpr(fieldValue.expr)
}

public fun visitPredicateLifetime(v: Visit, p: WherePredicate.LifetimePredicate) {
    v.visitLifetime(p.lifetime)
    p.bounds.toList().forEach { v.visitLifetime(it) }
}

public fun visitPredicateType(v: Visit, p: WherePredicate.TypePredicate) {
    p.lifetimes?.let { v.visitBoundLifetimes(it) }
    v.visitType(p.boundedTy)
    p.bounds.toList().forEach { v.visitTypeParamBound(it) }
}

public fun visitLabel(v: Visit, label: Label) {
    v.visitLifetime(label.name)
}

public fun visitQself(v: Visit, q: QSelf) {
    v.visitQSelf(q)
}

public fun visitQSelf(v: Visit, q: QSelf) {
    v.visitType(q.ty)
}

public fun visitRangeLimits(v: Visit, limits: RangeLimits) =
    when (limits) {
        is RangeLimits.HalfOpen -> Unit
        is RangeLimits.Closed -> Unit
    }

public fun visitReceiver(v: Visit, r: FnArg.Receiver) {
    r.attrs.forEach { v.visitAttribute(it) }
    r.reference?.lifetime?.let { v.visitLifetime(it) }
    v.visitType(r.type)
}

public fun visitReturnType(v: Visit, r: ReturnType) =
    when (r) {
        ReturnType.Default -> Unit

        is ReturnType.TypeReturn -> {
            v.visitType(r.ty)
        }
    }

public fun visitSignature(v: Visit, s: Signature) {
    s.abi?.let { v.visitAbi(it) }
    v.visitIdent(s.ident)
    v.visitGenerics(s.generics)
    s.inputs.toList().forEach { v.visitFnArg(it) }
    s.variadic?.let { v.visitVariadic(it) }
    v.visitReturnType(s.output)
}

public fun visitStmtMacro(v: Visit, s: Stmt.MacroStmt) {
    s.attrs.forEach { v.visitAttribute(it) }
    v.visitMacro(s.mac)
}

public fun visitTraitBound(v: Visit, t: TypeParamBound.Trait) {
    v.visitTraitBoundModifier(t.modifier)
    t.lifetimes?.let { v.visitBoundLifetimes(it) }
    v.visitPath(t.path)
}

public fun visitTraitBoundModifier(v: Visit, t: TraitBoundModifier) =
    when (t) {
        TraitBoundModifier.None -> Unit
        is TraitBoundModifier.Maybe -> Unit
    }

public fun visitTraitItem(v: Visit, t: TraitItem) =
    when (t) {
        is TraitItem.Const -> v.visitTraitItemConst(t)
        is TraitItem.Fn -> v.visitTraitItemFn(t)
        is TraitItem.AssocType -> v.visitTraitItemType(t)
        is TraitItem.Macro -> v.visitTraitItemMacro(t)
        is TraitItem.Verbatim -> v.visitTokenStream(t.tokens)
    }

public fun visitTraitItemConst(v: Visit, t: TraitItem.Const) {
    t.attrs.forEach { v.visitAttribute(it) }
    v.visitIdent(t.ident)
    v.visitGenerics(t.generics)
    v.visitType(t.ty)
    t.default?.let { v.visitExpr(it.expr) }
}

public fun visitTraitItemFn(v: Visit, t: TraitItem.Fn) {
    t.attrs.forEach { v.visitAttribute(it) }
    v.visitSignature(t.sig)
    t.default?.let { v.visitBlock(it) }
}

public fun visitTraitItemMacro(v: Visit, t: TraitItem.Macro) {
    t.attrs.forEach { v.visitAttribute(it) }
    v.visitMacro(t.mac)
}

public fun visitTraitItemType(v: Visit, t: TraitItem.AssocType) {
    t.attrs.forEach { v.visitAttribute(it) }
    v.visitIdent(t.ident)
    v.visitGenerics(t.generics)
    t.bounds.toList().forEach { v.visitTypeParamBound(it) }
    t.default?.let { v.visitType(it.type) }
}

public fun visitTypeBareFn(v: Visit, t: SynType.BareFn) {
    t.lifetimes?.let { v.visitBoundLifetimes(it) }
    t.abi?.let { v.visitAbi(it) }
    t.inputs.toList().forEach { v.visitBareFnArg(it) }
    t.variadic?.let { v.visitBareVariadic(it) }
    v.visitReturnType(t.output)
}

public fun visitTypeArray(v: Visit, t: SynType.Array) {
    v.visitType(t.elem)
    v.visitExpr(t.len)
}

public fun visitTypeGroup(v: Visit, t: SynType.Group) {
    v.visitType(t.elem)
}

public fun visitTypeImplTrait(v: Visit, t: SynType.ImplTrait) {
    t.bounds.toList().forEach { v.visitTypeParamBound(it) }
}

public fun visitTypeInfer(v: Visit, t: SynType.Infer) = Unit

public fun visitTypeMacro(v: Visit, t: SynType.Macro) {
    v.visitMacro(t.mac)
}

public fun visitTypeNever(v: Visit, t: SynType.Never) = Unit

public fun visitTypeParamBound(v: Visit, t: TypeParamBound) =
    when (t) {
        is TypeParamBound.Trait -> v.visitTraitBound(t)
        is TypeParamBound.LifetimeBound -> v.visitLifetime(t.lifetime)
        is TypeParamBound.PreciseCapture -> v.visitPreciseCapture(t)
        is TypeParamBound.Verbatim -> v.visitTokenStream(t.tokens)
    }

public fun visitTypeParen(v: Visit, t: SynType.Paren) {
    v.visitType(t.elem)
}

public fun visitTypePath(v: Visit, t: SynType.Path) {
    t.qself?.let { v.visitQself(it) }
    v.visitPath(t.path)
}

public fun visitTypePtr(v: Visit, t: SynType.Ptr) {
    v.visitPointerMutability(t.mutability)
    v.visitType(t.elem)
}

public fun visitTypeReference(v: Visit, t: SynType.Reference) {
    t.lifetime?.let { v.visitLifetime(it) }
    v.visitType(t.elem)
}

public fun visitTypeSlice(v: Visit, t: SynType.Slice) {
    v.visitType(t.elem)
}

public fun visitTypeTraitObject(v: Visit, t: SynType.TraitObject) {
    t.bounds.toList().forEach { v.visitTypeParamBound(it) }
}

public fun visitTypeTuple(v: Visit, t: SynType.Tuple) {
    t.elems.toList().forEach { v.visitType(it) }
}

public fun visitUnOp(v: Visit, op: UnOp) =
    when (op) {
        is UnOp.Deref -> Unit
        is UnOp.NotOp -> Unit
        is UnOp.Neg -> Unit
    }

public fun visitBinOp(v: Visit, op: BinOp) =
    when (op) {
        is BinOp.Add -> Unit
        is BinOp.Sub -> Unit
        is BinOp.Mul -> Unit
        is BinOp.Div -> Unit
        is BinOp.Rem -> Unit
        is BinOp.And -> Unit
        is BinOp.Or -> Unit
        is BinOp.BitXor -> Unit
        is BinOp.BitAnd -> Unit
        is BinOp.BitOr -> Unit
        is BinOp.Shl -> Unit
        is BinOp.Shr -> Unit
        is BinOp.Eq -> Unit
        is BinOp.Lt -> Unit
        is BinOp.Le -> Unit
        is BinOp.Ne -> Unit
        is BinOp.Ge -> Unit
        is BinOp.Gt -> Unit
        is BinOp.AddAssign -> Unit
        is BinOp.SubAssign -> Unit
        is BinOp.MulAssign -> Unit
        is BinOp.DivAssign -> Unit
        is BinOp.RemAssign -> Unit
        is BinOp.BitXorAssign -> Unit
        is BinOp.BitAndAssign -> Unit
        is BinOp.BitOrAssign -> Unit
        is BinOp.ShlAssign -> Unit
        is BinOp.ShrAssign -> Unit
    }

public fun visitPointerMutability(v: Visit, mutability: io.github.kotlinmania.syn.token.Mut?) = Unit

public fun visitPointerMutability(v: Visit, mutability: PointerMutability) =
    when (mutability) {
        is PointerMutability.Const -> Unit
        is PointerMutability.Mut -> Unit
    }

public fun visitUseGlob(v: Visit, u: UseTree.Glob) = Unit

public fun visitUseGroup(v: Visit, u: UseTree.Group) {
    u.items.toList().forEach { v.visitUseTree(it) }
}

public fun visitUseName(v: Visit, u: UseTree.Name) {
    v.visitIdent(u.ident)
}

public fun visitUsePath(v: Visit, u: UseTree.Path) {
    v.visitIdent(u.ident)
    u.tree?.let { v.visitUseTree(it) }
}

public fun visitUseRename(v: Visit, u: UseTree.Name) {
    v.visitIdent(u.ident)
    u.rename?.let { v.visitIdent(it.ident) }
}

public fun visitUseTree(v: Visit, u: UseTree) =
    when (u) {
        is UseTree.Path -> {
            v.visitUsePath(u)
        }

        is UseTree.Name -> {
            if (u.rename == null) {
                v.visitUseName(u)
            } else {
                v.visitUseRename(u)
            }
        }

        is UseTree.Group -> {
            v.visitUseGroup(u)
        }

        is UseTree.Glob -> {
            v.visitUseGlob(u)
        }
    }

public fun visitVariadic(v: Visit, node: Variadic) {
    node.attrs.forEach { v.visitAttribute(it) }
    node.pat?.let { v.visitPat(it.pat) }
}

public fun visitPreciseCapture(v: Visit, p: TypeParamBound.PreciseCapture) {
    p.params.toList().forEach { v.visitCapturedParam(it) }
}

public fun visitCapturedParam(v: Visit, c: CapturedParam) =
    when (c) {
        is CapturedParam.Lifetime -> v.visitLifetime(c.lifetime)
        is CapturedParam.Ident -> v.visitIdent(c.ident)
    }

public fun visitVariant(v: Visit, node: Variant) {
    node.attrs.forEach { v.visitAttribute(it) }
    v.visitIdent(node.ident)
    v.visitFields(node.fields)
    node.discriminant?.let { v.visitExpr(it.expr) }
}

public fun visitVisRestricted(v: Visit, node: Visibility.Restricted) {
    v.visitPath(node.path)
}

public fun visitVisibility(v: Visit, node: Visibility) =
    when (node) {
        is Visibility.Public -> Unit

        is Visibility.Restricted -> {
            v.visitVisRestricted(node)
        }

        Visibility.Inherited -> Unit
    }

public fun visitWhereClause(v: Visit, w: WhereClause) {
    w.predicates.toList().forEach { v.visitWherePredicate(it) }
}

public fun visitWherePredicate(v: Visit, w: WherePredicate) =
    when (w) {
        is WherePredicate.LifetimePredicate -> v.visitPredicateLifetime(w)
        is WherePredicate.TypePredicate -> v.visitPredicateType(w)
    }

public fun visitSpan(v: Visit, s: Span) = Unit

public fun visitTokenStream(v: Visit, t: TokenStream) = Unit
