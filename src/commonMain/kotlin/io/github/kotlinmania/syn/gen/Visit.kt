// port-lint: source gen/visit.rs
package io.github.kotlinmania.syn.gen

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
import io.github.kotlinmania.syn.Ident
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
    public open fun visitExpr(e: Expr) {
        when (e) {
            is Expr.Array -> visitExprArray(e)
            is Expr.Assign -> visitExprAssign(e)
            is Expr.Async -> visitExprAsync(e)
            is Expr.Await -> visitExprAwait(e)
            is Expr.Binary -> visitExprBinary(e)
            is Expr.BlockExpr -> visitExprBlock(e)
            is Expr.Break -> visitExprBreak(e)
            is Expr.Call -> visitExprCall(e)
            is Expr.Cast -> visitExprCast(e)
            is Expr.Closure -> visitExprClosure(e)
            is Expr.Const -> visitExprConst(e)
            is Expr.Continue -> visitExprContinue(e)
            is Expr.Field -> visitExprField(e)
            is Expr.ForLoop -> visitExprForLoop(e)
            is Expr.Group -> visitExprGroup(e)
            is Expr.If -> visitExprIf(e)
            is Expr.Index -> visitExprIndex(e)
            is Expr.Infer -> visitExprInfer(e)
            is Expr.Let -> visitExprLet(e)
            is Expr.Lit -> visitExprLit(e)
            is Expr.Loop -> visitExprLoop(e)
            is Expr.Macro -> visitExprMacro(e)
            is Expr.Match -> visitExprMatch(e)
            is Expr.MethodCall -> visitExprMethodCall(e)
            is Expr.Paren -> visitExprParen(e)
            is Expr.Path -> visitExprPath(e)
            is Expr.Range -> visitExprRange(e)
            is Expr.RawAddr -> visitExprRawAddr(e)
            is Expr.Reference -> visitExprReference(e)
            is Expr.Repeat -> visitExprRepeat(e)
            is Expr.Return -> visitExprReturn(e)
            is Expr.Struct -> visitExprStruct(e)
            is Expr.Try -> visitExprTry(e)
            is Expr.TryBlock -> visitExprTryBlock(e)
            is Expr.Tuple -> visitExprTuple(e)
            is Expr.Unary -> visitExprUnary(e)
            is Expr.Unsafe -> visitExprUnsafe(e)
            is Expr.While -> visitExprWhile(e)
            is Expr.Yield -> visitExprYield(e)
            is Expr.Verbatim -> visitTokenStream(e.tokens)
        }
    }

    public open fun visitExprArray(e: Expr.Array) {
        e.attrs.forEach { visitAttribute(it) }
        e.elems.toList().forEach { visitExpr(it) }
    }

    public open fun visitExprAssign(e: Expr.Assign) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.left)
        visitExpr(e.right)
    }

    public open fun visitExprAsync(e: Expr.Async) {
        e.attrs.forEach { visitAttribute(it) }
        visitBlock(e.block)
    }

    public open fun visitExprAwait(e: Expr.Await) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.base)
    }

    public open fun visitExprBinary(e: Expr.Binary) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.left)
        visitBinOp(e.op)
        visitExpr(e.right)
    }

    public open fun visitExprBlock(e: Expr.BlockExpr) {
        e.attrs.forEach { visitAttribute(it) }
        e.label?.let { visitLabel(it) }
        visitBlock(e.block)
    }

    public open fun visitExprBreak(e: Expr.Break) {
        e.attrs.forEach { visitAttribute(it) }
        e.label?.let { visitLifetime(it) }
        e.expr?.let { visitExpr(it) }
    }

    public open fun visitExprCall(e: Expr.Call) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.func)
        e.args.toList().forEach { visitExpr(it) }
    }

    public open fun visitExprCast(e: Expr.Cast) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
        visitType(e.ty)
    }

    public open fun visitExprClosure(e: Expr.Closure) {
        e.attrs.forEach { visitAttribute(it) }
        e.inputs.toList().forEach { visitPat(it) }
        visitReturnType(e.output)
        visitExpr(e.body)
    }

    public open fun visitExprConst(e: Expr.Const) {
        e.attrs.forEach { visitAttribute(it) }
        visitBlock(e.block)
    }

    public open fun visitExprContinue(e: Expr.Continue) {
        e.attrs.forEach { visitAttribute(it) }
        e.label?.let { visitLifetime(it) }
    }

    public open fun visitExprField(e: Expr.Field) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.base)
        visitMember(e.member)
    }

    public open fun visitExprForLoop(e: Expr.ForLoop) {
        e.attrs.forEach { visitAttribute(it) }
        e.label?.let { visitLabel(it) }
        visitPat(e.pat)
        visitExpr(e.expr)
        visitBlock(e.body)
    }

    public open fun visitExprGroup(e: Expr.Group) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
    }

    public open fun visitExprIf(e: Expr.If) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.cond)
        visitBlock(e.thenBranch)
        e.elseBranch?.let { visitElseExpr(it) }
    }

    public open fun visitExprIndex(e: Expr.Index) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
        visitExpr(e.index)
    }

    public open fun visitExprInfer(e: Expr.Infer) {
        e.attrs.forEach { visitAttribute(it) }
    }

    public open fun visitExprLet(e: Expr.Let) {
        e.attrs.forEach { visitAttribute(it) }
        visitPat(e.pat)
        visitExpr(e.expr)
    }

    public open fun visitExprLit(e: Expr.Lit) {
        e.attrs.forEach { visitAttribute(it) }
        visitLit(e.lit)
    }

    public open fun visitExprLoop(e: Expr.Loop) {
        e.attrs.forEach { visitAttribute(it) }
        e.label?.let { visitLabel(it) }
        visitBlock(e.body)
    }

    public open fun visitExprMacro(e: Expr.Macro) {
        e.attrs.forEach { visitAttribute(it) }
        visitMacro(e.mac)
    }

    public open fun visitExprMatch(e: Expr.Match) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
        e.arms.forEach { visitArm(it) }
    }

    public open fun visitExprMethodCall(e: Expr.MethodCall) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.receiver)
        visitIdent(e.method)
        e.turbofish?.let { visitAngleBracketedGenericArguments(it) }
        e.args.toList().forEach { visitExpr(it) }
    }

    public open fun visitExprParen(e: Expr.Paren) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
    }

    public open fun visitExprPath(e: Expr.Path) {
        e.attrs.forEach { visitAttribute(it) }
        e.qself?.let { visitQself(it) }
        visitPath(e.path)
    }

    public open fun visitExprRange(e: Expr.Range) {
        e.attrs.forEach { visitAttribute(it) }
        e.start?.let { visitExpr(it) }
        visitRangeLimits(e.limits)
        e.end?.let { visitExpr(it) }
    }

    public open fun visitExprRawAddr(e: Expr.RawAddr) {
        e.attrs.forEach { visitAttribute(it) }
        visitPointerMutability(e.mutability)
        visitExpr(e.expr)
    }

    public open fun visitExprReference(e: Expr.Reference) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
    }

    public open fun visitExprRepeat(e: Expr.Repeat) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
        visitExpr(e.len)
    }

    public open fun visitExprReturn(e: Expr.Return) {
        e.attrs.forEach { visitAttribute(it) }
        e.expr?.let { visitExpr(it) }
    }

    public open fun visitExprStruct(e: Expr.Struct) {
        e.attrs.forEach { visitAttribute(it) }
        e.qself?.let { visitQself(it) }
        visitPath(e.path)
        e.fields.toList().forEach { visitFieldValue(it) }
        e.rest?.let { visitExpr(it) }
    }

    public open fun visitExprTry(e: Expr.Try) {
        e.attrs.forEach { visitAttribute(it) }
        visitExpr(e.expr)
    }

    public open fun visitExprTryBlock(e: Expr.TryBlock) {
        e.attrs.forEach { visitAttribute(it) }
        visitBlock(e.block)
    }

    public open fun visitExprTuple(e: Expr.Tuple) {
        e.attrs.forEach { visitAttribute(it) }
        e.elems.toList().forEach { visitExpr(it) }
    }

    public open fun visitExprUnary(e: Expr.Unary) {
        e.attrs.forEach { visitAttribute(it) }
        visitUnOp(e.op)
        visitExpr(e.expr)
    }

    public open fun visitExprUnsafe(e: Expr.Unsafe) {
        e.attrs.forEach { visitAttribute(it) }
        visitBlock(e.block)
    }

    public open fun visitExprWhile(e: Expr.While) {
        e.attrs.forEach { visitAttribute(it) }
        e.label?.let { visitLabel(it) }
        visitExpr(e.cond)
        visitBlock(e.body)
    }

    public open fun visitExprYield(e: Expr.Yield) {
        e.attrs.forEach { visitAttribute(it) }
        e.expr?.let { visitExpr(it) }
    }

    public open fun visitType(t: SynType) {
        when (t) {
            is SynType.Array -> visitTypeArray(t)
            is SynType.BareFn -> visitTypeBareFn(t)
            is SynType.Group -> visitTypeGroup(t)
            is SynType.ImplTrait -> visitTypeImplTrait(t)
            is SynType.Infer -> visitTypeInfer(t)
            is SynType.Macro -> visitTypeMacro(t)
            is SynType.Never -> visitTypeNever(t)
            is SynType.Paren -> visitTypeParen(t)
            is SynType.Path -> visitTypePath(t)
            is SynType.Ptr -> visitTypePtr(t)
            is SynType.Reference -> visitTypeReference(t)
            is SynType.Slice -> visitTypeSlice(t)
            is SynType.TraitObject -> visitTypeTraitObject(t)
            is SynType.Tuple -> visitTypeTuple(t)
            is SynType.Verbatim -> visitTokenStream(t.tokens)
        }
    }

    public open fun visitPath(p: Path) {
        p.segments.toList().forEach { visitPathSegment(it) }
    }

    public open fun visitPat(p: Pat) {
        when (p) {
            is Pat.Const -> visitPatConst(p)
            is Pat.Ident -> visitPatIdent(p)
            is Pat.Lit -> visitPatLit(p)
            is Pat.Macro -> visitPatMacro(p)
            is Pat.Or -> visitPatOr(p)
            is Pat.PatParen -> visitPatParen(p)
            is Pat.Path -> visitPatPath(p)
            is Pat.Range -> visitPatRange(p)
            is Pat.Reference -> visitPatReference(p)
            is Pat.Rest -> visitPatRest(p)
            is Pat.Slice -> visitPatSlice(p)
            is Pat.Struct -> visitPatStruct(p)
            is Pat.Tuple -> visitPatTuple(p)
            is Pat.TupleStruct -> visitPatTupleStruct(p)
            is Pat.TypeAscription -> visitPatTypeAscription(p)
            is Pat.Verbatim -> visitTokenStream(p.tokens)
            is Pat.Wild -> visitPatWild(p)
        }
    }

    public open fun visitItem(i: Item) {
        when (i) {
            is Item.Const -> visitItemConst(i)
            is Item.Enum -> visitItemEnum(i)
            is Item.ExternCrate -> visitItemExternCrate(i)
            is Item.Fn -> visitItemFn(i)
            is Item.ForeignMod -> visitItemForeignMod(i)
            is Item.Impl -> visitItemImpl(i)
            is Item.Macro -> visitItemMacro(i)
            is Item.Mod -> visitItemMod(i)
            is Item.Static -> visitItemStatic(i)
            is Item.Struct -> visitItemStruct(i)
            is Item.Trait -> visitItemTrait(i)
            is Item.TraitAlias -> visitItemTraitAlias(i)
            is Item.ItemType -> visitItemType(i)
            is Item.Union -> visitItemUnion(i)
            is Item.Use -> visitItemUse(i)
            is Item.Verbatim -> visitTokenStream(i.tokens)
        }
    }

    public open fun visitAttribute(a: Attribute) {
        visitAttrStyle(a.style)
        visitMeta(a.meta)
    }

    public open fun visitAttrStyle(s: AttrStyle) {
        when (s) {
            AttrStyle.Outer -> {}
            is AttrStyle.Inner -> {}
        }
    }

    public open fun visitMeta(m: Meta) {
        when (m) {
            is Meta.PathMeta -> visitPath(m.path)
            is Meta.List -> visitMetaList(m)
            is Meta.NameValue -> visitMetaNameValue(m)
        }
    }

    public open fun visitMetaList(m: Meta.List) {
        visitPath(m.path)
        visitMacroDelimiter(m.delimiter)
        visitTokenStream(m.tokens)
    }

    public open fun visitMetaNameValue(m: Meta.NameValue) {
        visitPath(m.path)
        visitExpr(m.value)
    }

    public open fun visitGenerics(g: Generics) {
        g.params.toList().forEach { visitGenericParam(it) }
        g.whereClause?.let { visitWhereClause(it) }
    }

    public open fun visitLit(l: Lit) {
        when (l) {
            is Lit.Str -> visitLitStr(l.value)
            is Lit.ByteStr -> visitLitByteStr(l.value)
            is Lit.CStr -> visitLitCstr(l.value)
            is Lit.Byte -> visitLitByte(l.value)
            is Lit.Char -> visitLitChar(l.value)
            is Lit.Int -> visitLitInt(l.value)
            is Lit.Float -> visitLitFloat(l.value)
            is Lit.Bool -> visitLitBool(l.value)
            is Lit.Verbatim -> {}
        }
    }

    public open fun visitLitBool(l: LitBool) {
        visitSpan(l.span())
    }

    public open fun visitLitByte(l: LitByte) { /* leaf */ }

    public open fun visitLitByteStr(l: LitByteStr) { /* leaf */ }

    public open fun visitLitCstr(l: LitCStr) {
        visitLitCStr(l)
    }

    public open fun visitLitCStr(l: LitCStr) { /* leaf */ }

    public open fun visitLitChar(l: LitChar) { /* leaf */ }

    public open fun visitLitFloat(l: LitFloat) { /* leaf */ }

    public open fun visitLitInt(l: LitInt) { /* leaf */ }

    public open fun visitLitStr(l: LitStr) { /* leaf */ }

    public open fun visitLocal(l: Stmt.Local) {
        l.attrs.forEach { visitAttribute(it) }
        visitPat(l.pat)
        l.init?.let { visitLocalInit(it) }
    }

    public open fun visitLocalInit(i: LocalInit) {
        visitExpr(i.expr)
        i.diverge?.let { visitExpr(it.expr) }
    }

    public open fun visitLifetime(lt: Lifetime) {
        visitSpan(lt.apostrophe)
        visitIdent(lt.ident)
    }

    public open fun visitIdent(id: Ident) {
        visitSpan(id.span())
    }

    public open fun visitStmt(s: Stmt) {
        when (s) {
            is Stmt.Local -> visitLocal(s)
            is Stmt.ItemStmt -> visitItem(s.item)
            is Stmt.ExprStmt -> visitExpr(s.expr)
            is Stmt.MacroStmt -> visitStmtMacro(s)
        }
    }

    public open fun visitData(d: Data) {
        when (d) {
            is Data.Struct -> visitDataStruct(d.value)
            is Data.Enum -> visitDataEnum(d.value)
            is Data.Union -> visitDataUnion(d.value)
        }
    }

    public open fun visitDataEnum(d: DataEnum) {
        d.variants.toList().forEach { visitVariant(it) }
    }

    public open fun visitDataStruct(d: DataStruct) {
        visitFields(d.fields)
    }

    public open fun visitDataUnion(d: DataUnion) {
        visitFieldsNamed(d.fields)
    }

    public open fun visitDeriveInput(di: DeriveInput) {
        di.attrs.forEach { visitAttribute(it) }
        visitVisibility(di.vis)
        visitIdent(di.ident)
        visitGenerics(di.generics)
        visitData(di.data)
    }

    public open fun visitAbi(a: Abi) {
        a.name?.let { visitLitStr(it) }
    }

    public open fun visitAngleBracketedGenericArguments(a: PathArguments.AngleBracketed) {
        a.args.toList().forEach { visitGenericArgument(it) }
    }

    public open fun visitAssocConst(a: AssocConst) {
        visitIdent(a.ident)
        a.generics?.let { visitAngleBracketedGenericArguments(it) }
        visitExpr(a.value)
    }

    public open fun visitAssocType(a: AssocType) {
        visitIdent(a.ident)
        a.generics?.let { visitAngleBracketedGenericArguments(it) }
        visitType(a.ty)
    }

    public open fun visitBareFnArg(a: BareFnArg) {
        a.attrs.forEach { visitAttribute(it) }
        a.name?.let { visitIdent(it.ident) }
        visitType(a.ty)
    }

    public open fun visitBareVariadic(v: BareVariadic) {
        v.attrs.forEach { visitAttribute(it) }
        v.name?.let { visitIdent(it.ident) }
    }

    public open fun visitBlock(b: Block) {
        b.stmts.forEach { visitStmt(it) }
    }

    public open fun visitBoundLifetimes(b: BoundLifetimes) {
        b.lifetimes.toList().forEach { visitGenericParam(it) }
    }

    public open fun visitConstraint(c: Constraint) {
        visitIdent(c.ident)
        c.generics?.let { visitAngleBracketedGenericArguments(it) }
        c.bounds.toList().forEach { visitTypeParamBound(it) }
    }

    public open fun visitField(f: Field) {
        f.attrs.forEach { visitAttribute(it) }
        visitVisibility(f.vis)
        visitFieldMutability(f.mutability)
        f.ident?.let { visitIdent(it) }
        visitType(f.ty)
    }

    public open fun visitFieldPat(f: FieldPat) {
        f.attrs.forEach { visitAttribute(it) }
        visitMember(f.member)
        visitPat(f.pat)
    }

    public open fun visitFieldMutability(f: FieldMutability) {
        when (f) {
            FieldMutability.None -> {}
            is FieldMutability.Mut -> {}
        }
    }

    public open fun visitFields(f: Fields) {
        when (f) {
            is Fields.Named -> visitFieldsNamed(f.fields)
            is Fields.Unnamed -> visitFieldsUnnamed(f.fields)
            Fields.Unit -> {}
        }
    }

    public open fun visitFieldsNamed(f: FieldsNamed) {
        f.named.toList().forEach { visitField(it) }
    }

    public open fun visitFieldsUnnamed(f: FieldsUnnamed) {
        f.unnamed.toList().forEach { visitField(it) }
    }

    public open fun visitFile(f: File) {
        f.attrs.forEach { visitAttribute(it) }
        f.items.forEach { visitItem(it) }
    }

    public open fun visitFnArg(a: FnArg) {
        when (a) {
            is FnArg.Receiver -> visitReceiver(a)
            is FnArg.Typed -> visitPatType(a.patType)
        }
    }

    public open fun visitForeignItem(i: ForeignItem) {
        when (i) {
            is ForeignItem.Fn -> visitForeignItemFn(i)
            is ForeignItem.Static -> visitForeignItemStatic(i)
            is ForeignItem.ItemType -> visitForeignItemType(i)
            is ForeignItem.Macro -> visitForeignItemMacro(i)
            is ForeignItem.Verbatim -> visitTokenStream(i.tokens)
        }
    }

    public open fun visitForeignItemFn(i: ForeignItem.Fn) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitSignature(i.sig)
    }

    public open fun visitForeignItemMacro(i: ForeignItem.Macro) {
        i.attrs.forEach { visitAttribute(it) }
        visitMacro(i.mac)
    }

    public open fun visitForeignItemStatic(i: ForeignItem.Static) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitStaticMutability(i.mutability)
        visitIdent(i.ident)
        visitType(i.ty)
    }

    public open fun visitForeignItemType(i: ForeignItem.ItemType) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
    }

    public open fun visitGenericArgument(g: GenericArgument) {
        when (g) {
            is GenericArgument.LifetimeArg -> visitLifetime(g.lifetime)
            is GenericArgument.TypeArg -> visitType(g.type)
            is GenericArgument.ConstArg -> visitExpr(g.expr)
            is GenericArgument.AssocTypeArg -> visitAssocType(g.assoc)
            is GenericArgument.AssocConstArg -> visitAssocConst(g.assoc)
            is GenericArgument.ConstraintArg -> visitConstraint(g.constraint)
        }
    }

    public open fun visitGenericParam(g: GenericParam) {
        when (g) {
            is GenericParam.LifetimeParam -> visitLifetimeParam(g)
            is GenericParam.TypeParam -> visitTypeParam(g)
            is GenericParam.ConstParam -> visitConstParam(g)
        }
    }

    public open fun visitConstParam(c: GenericParam.ConstParam) {
        c.attrs.forEach { visitAttribute(it) }
        visitIdent(c.ident)
        visitType(c.ty)
        c.default?.let { visitExpr(it) }
    }

    public open fun visitLifetimeParam(l: GenericParam.LifetimeParam) {
        l.attrs.forEach { visitAttribute(it) }
        visitLifetime(l.lifetime)
        l.bounds.toList().forEach { visitLifetime(it) }
    }

    public open fun visitTypeParam(t: GenericParam.TypeParam) {
        t.attrs.forEach { visitAttribute(it) }
        visitIdent(t.ident)
        t.bounds.toList().forEach { visitTypeParamBound(it) }
        t.default?.let { visitType(it) }
    }

    public open fun visitMacro(m: Macro) {
        visitPath(m.path)
        visitMacroDelimiter(m.delimiter)
        visitTokenStream(m.tokens)
    }

    public open fun visitMacroDelimiter(m: MacroDelimiter) {
        when (m) {
            is MacroDelimiter.Paren -> {}
            is MacroDelimiter.Brace -> {}
            is MacroDelimiter.Bracket -> {}
        }
    }

    public open fun visitMember(m: Member) {
        when (m) {
            is Member.Named -> visitIdent(m.ident)
            is Member.Unnamed -> visitIndex(m.index)
        }
    }

    public open fun visitIndex(i: Index) {
        visitSpan(i.span)
    }

    public open fun visitImplItem(i: ImplItem) {
        when (i) {
            is ImplItem.Const -> visitImplItemConst(i)
            is ImplItem.Fn -> visitImplItemFn(i)
            is ImplItem.AssocType -> visitImplItemType(i)
            is ImplItem.Macro -> visitImplItemMacro(i)
            is ImplItem.Verbatim -> visitTokenStream(i.tokens)
        }
    }

    public open fun visitImplItemConst(i: ImplItem.Const) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
        visitType(i.ty)
        visitExpr(i.expr)
    }

    public open fun visitImplItemFn(i: ImplItem.Fn) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitSignature(i.sig)
        visitBlock(i.block)
    }

    public open fun visitImplItemMacro(i: ImplItem.Macro) {
        i.attrs.forEach { visitAttribute(it) }
        visitMacro(i.mac)
    }

    public open fun visitImplItemType(i: ImplItem.AssocType) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
        visitType(i.ty)
    }

    public open fun visitImplRestriction(i: ImplRestriction) {
    }

    public open fun visitItemConst(i: Item.Const) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitType(i.ty)
        i.expr?.let { visitExpr(it) }
    }

    public open fun visitItemEnum(i: Item.Enum) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
        i.variants.toList().forEach { visitVariant(it) }
    }

    public open fun visitItemExternCrate(i: Item.ExternCrate) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        i.rename?.let { visitIdent(it.ident) }
    }

    public open fun visitItemFn(i: Item.Fn) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitSignature(i.sig)
        i.block?.let { visitBlock(it) }
    }

    public open fun visitItemForeignMod(i: Item.ForeignMod) {
        i.attrs.forEach { visitAttribute(it) }
        visitAbi(i.abi)
        i.items.forEach { visitForeignItem(it) }
    }

    public open fun visitItemImpl(i: Item.Impl) {
        i.attrs.forEach { visitAttribute(it) }
        visitGenerics(i.generics)
        i.traitPath?.let { visitPathTrait(it) }
        visitType(i.selfType)
        i.items.forEach { visitImplItem(it) }
    }

    public open fun visitItemMacro(i: Item.Macro) {
        i.attrs.forEach { visitAttribute(it) }
        i.ident?.let { visitIdent(it) }
        visitMacro(i.mac)
    }

    public open fun visitItemMod(i: Item.Mod) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        i.content?.let { visitModContent(it) }
    }

    public open fun visitItemStatic(i: Item.Static) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitStaticMutability(i.mutability)
        visitIdent(i.ident)
        visitType(i.ty)
        visitExpr(i.expr)
    }

    public open fun visitItemStruct(i: Item.Struct) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
        visitFields(i.fields)
    }

    public open fun visitItemTrait(i: Item.Trait) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        i.restriction?.let { visitImplRestriction(it) }
        visitIdent(i.ident)
        visitGenerics(i.generics)
        i.supertraits.toList().forEach { visitTypeParamBound(it) }
        i.items.forEach { visitTraitItem(it) }
    }

    public open fun visitItemTraitAlias(i: Item.TraitAlias) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
        i.bounds.toList().forEach { visitTypeParamBound(it) }
    }

    public open fun visitItemType(i: Item.ItemType) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
        visitType(i.ty)
    }

    public open fun visitItemUnion(i: Item.Union) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitIdent(i.ident)
        visitGenerics(i.generics)
        visitFieldsNamed(i.fields)
    }

    public open fun visitItemUse(i: Item.Use) {
        i.attrs.forEach { visitAttribute(it) }
        visitVisibility(i.vis)
        visitUseTree(i.tree)
    }

    public open fun visitStaticMutability(mutability: StaticMutability) {
    }

    public open fun visitModContent(m: ModContent) {
        when (m) {
            is ModContent.Inline -> m.items.forEach { visitItem(it) }
            is ModContent.Unnamed -> {}
        }
    }

    public open fun visitParenthesizedGenericArguments(p: PathArguments.Parenthesized) {
        p.inputs.toList().forEach { visitType(it) }
        visitReturnType(p.output)
    }

    public open fun visitPatConst(p: Pat.Const) {
        p.attrs.forEach { visitAttribute(it) }
        visitBlock(p.block)
    }

    public open fun visitPatIdent(p: Pat.Ident) {
        p.attrs.forEach { visitAttribute(it) }
        visitFieldMutability(p.mutability)
        visitIdent(p.ident)
        p.subpat?.let { visitPat(it) }
    }

    public open fun visitPatLit(p: Pat.Lit) {
        p.attrs.forEach { visitAttribute(it) }
        visitLit(p.lit)
    }

    public open fun visitPatMacro(p: Pat.Macro) {
        p.attrs.forEach { visitAttribute(it) }
        visitMacro(p.mac)
    }

    public open fun visitPatOr(p: Pat.Or) {
        p.attrs.forEach { visitAttribute(it) }
        p.cases.toList().forEach { visitPat(it) }
    }

    public open fun visitPatParen(p: Pat.PatParen) {
        p.attrs.forEach { visitAttribute(it) }
        visitPat(p.pat)
    }

    public open fun visitPatPath(p: Pat.Path) {
        p.attrs.forEach { visitAttribute(it) }
        p.qself?.let { visitQself(it) }
        visitPath(p.path)
    }

    public open fun visitPatRange(p: Pat.Range) {
        p.attrs.forEach { visitAttribute(it) }
        p.start?.let { visitExpr(it) }
        visitRangeLimits(p.limits)
        p.end?.let { visitExpr(it) }
    }

    public open fun visitPatReference(p: Pat.Reference) {
        p.attrs.forEach { visitAttribute(it) }
        visitFieldMutability(p.mutability)
        visitPat(p.pat)
    }

    public open fun visitPatRest(p: Pat.Rest) {
        p.attrs.forEach { visitAttribute(it) }
    }

    public open fun visitPatRest(p: PatRest) {
        p.attrs.forEach { visitAttribute(it) }
    }

    public open fun visitPatSlice(p: Pat.Slice) {
        p.attrs.forEach { visitAttribute(it) }
        p.elems.toList().forEach { visitPat(it) }
    }

    public open fun visitPatStruct(p: Pat.Struct) {
        p.attrs.forEach { visitAttribute(it) }
        p.qself?.let { visitQself(it) }
        visitPath(p.path)
        p.fields.toList().forEach { visitFieldPat(it) }
        p.rest?.let { visitPatRest(it) }
    }

    public open fun visitPatTuple(p: Pat.Tuple) {
        p.attrs.forEach { visitAttribute(it) }
        p.elems.toList().forEach { visitPat(it) }
    }

    public open fun visitPatTupleStruct(p: Pat.TupleStruct) {
        p.attrs.forEach { visitAttribute(it) }
        p.qself?.let { visitQself(it) }
        visitPath(p.path)
        p.elems.toList().forEach { visitPat(it) }
    }

    public open fun visitPatType(p: PatType) {
        p.attrs.forEach { visitAttribute(it) }
        visitPat(p.pat)
        visitType(p.ty)
    }

    public open fun visitPatTypeAscription(p: Pat.TypeAscription) {
        p.attrs.forEach { visitAttribute(it) }
        visitPat(p.pat)
        visitType(p.ty)
    }

    public open fun visitPatWild(p: Pat.Wild) {
        p.attrs.forEach { visitAttribute(it) }
    }

    public open fun visitPathTrait(p: PathTrait) {
        visitPath(p.path)
    }

    public open fun visitPathArguments(p: PathArguments) {
        when (p) {
            PathArguments.None -> {}
            is PathArguments.AngleBracketed -> visitAngleBracketedGenericArguments(p)
            is PathArguments.Parenthesized -> visitParenthesizedGenericArguments(p)
        }
    }

    public open fun visitPathSegment(p: PathSegment) {
        visitIdent(p.ident)
        visitPathArguments(p.arguments)
    }

    public open fun visitArm(arm: io.github.kotlinmania.syn.Arm) {
        arm.attrs.forEach { visitAttribute(it) }
        visitPat(arm.pat)
        arm.guard?.let { visitExpr(it.expr) }
        visitExpr(arm.body)
    }

    public open fun visitElseExpr(elseExpr: ElseExpr) {
        visitExpr(elseExpr.expr)
    }

    public open fun visitFieldValue(fieldValue: FieldValue) {
        fieldValue.attrs.forEach { visitAttribute(it) }
        visitMember(fieldValue.member)
        visitExpr(fieldValue.expr)
    }

    public open fun visitPredicateLifetime(p: WherePredicate.LifetimePredicate) {
        visitLifetime(p.lifetime)
        p.bounds.toList().forEach { visitLifetime(it) }
    }

    public open fun visitPredicateType(p: WherePredicate.TypePredicate) {
        p.lifetimes?.let { visitBoundLifetimes(it) }
        visitType(p.boundedTy)
        p.bounds.toList().forEach { visitTypeParamBound(it) }
    }

    public open fun visitLabel(label: Label) {
        visitLifetime(label.name)
    }

    public open fun visitQself(q: QSelf) {
        visitQSelf(q)
    }

    public open fun visitQSelf(q: QSelf) {
        visitType(q.ty)
    }

    public open fun visitRangeLimits(limits: RangeLimits) {
        when (limits) {
            is RangeLimits.HalfOpen -> {}
            is RangeLimits.Closed -> {}
        }
    }

    public open fun visitReceiver(r: FnArg.Receiver) {
        r.attrs.forEach { visitAttribute(it) }
        r.reference?.lifetime?.let { visitLifetime(it) }
        visitType(r.type)
    }

    public open fun visitReturnType(r: ReturnType) {
        when (r) {
            ReturnType.Default -> {}
            is ReturnType.TypeReturn -> visitType(r.ty)
        }
    }

    public open fun visitSignature(s: Signature) {
        s.abi?.let { visitAbi(it) }
        visitIdent(s.ident)
        visitGenerics(s.generics)
        s.inputs.toList().forEach { visitFnArg(it) }
        s.variadic?.let { visitVariadic(it) }
        visitReturnType(s.output)
    }

    public open fun visitStmtMacro(s: Stmt.MacroStmt) {
        s.attrs.forEach { visitAttribute(it) }
        visitMacro(s.mac)
    }

    public open fun visitTraitBound(t: TypeParamBound.Trait) {
        visitTraitBoundModifier(t.modifier)
        t.lifetimes?.let { visitBoundLifetimes(it) }
        visitPath(t.path)
    }

    public open fun visitTraitBoundModifier(t: TraitBoundModifier) {
        when (t) {
            TraitBoundModifier.None -> {}
            is TraitBoundModifier.Maybe -> {}
        }
    }

    public open fun visitTraitItem(t: TraitItem) {
        when (t) {
            is TraitItem.Const -> visitTraitItemConst(t)
            is TraitItem.Fn -> visitTraitItemFn(t)
            is TraitItem.AssocType -> visitTraitItemType(t)
            is TraitItem.Macro -> visitTraitItemMacro(t)
            is TraitItem.Verbatim -> visitTokenStream(t.tokens)
        }
    }

    public open fun visitTraitItemConst(t: TraitItem.Const) {
        t.attrs.forEach { visitAttribute(it) }
        visitIdent(t.ident)
        visitGenerics(t.generics)
        visitType(t.ty)
        t.default?.let { visitExpr(it.expr) }
    }

    public open fun visitTraitItemFn(t: TraitItem.Fn) {
        t.attrs.forEach { visitAttribute(it) }
        visitSignature(t.sig)
        t.default?.let { visitBlock(it) }
    }

    public open fun visitTraitItemMacro(t: TraitItem.Macro) {
        t.attrs.forEach { visitAttribute(it) }
        visitMacro(t.mac)
    }

    public open fun visitTraitItemType(t: TraitItem.AssocType) {
        t.attrs.forEach { visitAttribute(it) }
        visitIdent(t.ident)
        visitGenerics(t.generics)
        t.bounds.toList().forEach { visitTypeParamBound(it) }
        t.default?.let { visitType(it.type) }
    }

    public open fun visitTypeBareFn(t: SynType.BareFn) {
        t.lifetimes?.let { visitBoundLifetimes(it) }
        t.abi?.let { visitAbi(it) }
        t.inputs.toList().forEach { visitBareFnArg(it) }
        t.variadic?.let { visitBareVariadic(it) }
        visitReturnType(t.output)
    }

    public open fun visitTypeArray(t: SynType.Array) {
        visitType(t.elem)
        visitExpr(t.len)
    }

    public open fun visitTypeGroup(t: SynType.Group) {
        visitType(t.elem)
    }

    public open fun visitTypeImplTrait(t: SynType.ImplTrait) {
        t.bounds.toList().forEach { visitTypeParamBound(it) }
    }

    public open fun visitTypeInfer(t: SynType.Infer) { /* leaf */ }

    public open fun visitTypeMacro(t: SynType.Macro) {
        visitMacro(t.mac)
    }

    public open fun visitTypeNever(t: SynType.Never) { /* leaf */ }

    public open fun visitTypeParamBound(t: TypeParamBound) {
        when (t) {
            is TypeParamBound.Trait -> visitTraitBound(t)
            is TypeParamBound.LifetimeBound -> visitLifetime(t.lifetime)
            is TypeParamBound.PreciseCapture -> visitPreciseCapture(t)
            is TypeParamBound.Verbatim -> visitTokenStream(t.tokens)
        }
    }

    public open fun visitTypeParen(t: SynType.Paren) {
        visitType(t.elem)
    }

    public open fun visitTypePath(t: SynType.Path) {
        t.qself?.let { visitQself(it) }
        visitPath(t.path)
    }

    public open fun visitTypePtr(t: SynType.Ptr) {
        visitPointerMutability(t.mutability)
        visitType(t.elem)
    }

    public open fun visitTypeReference(t: SynType.Reference) {
        t.lifetime?.let { visitLifetime(it) }
        visitType(t.elem)
    }

    public open fun visitTypeSlice(t: SynType.Slice) {
        visitType(t.elem)
    }

    public open fun visitTypeTraitObject(t: SynType.TraitObject) {
        t.bounds.toList().forEach { visitTypeParamBound(it) }
    }

    public open fun visitTypeTuple(t: SynType.Tuple) {
        t.elems.toList().forEach { visitType(it) }
    }

    public open fun visitUnOp(op: UnOp) {
        when (op) {
            is UnOp.Deref -> {}
            is UnOp.NotOp -> {}
            is UnOp.Neg -> {}
        }
    }

    public open fun visitBinOp(op: BinOp) {
        when (op) {
            is BinOp.Add -> {}
            is BinOp.Sub -> {}
            is BinOp.Mul -> {}
            is BinOp.Div -> {}
            is BinOp.Rem -> {}
            is BinOp.And -> {}
            is BinOp.Or -> {}
            is BinOp.BitXor -> {}
            is BinOp.BitAnd -> {}
            is BinOp.BitOr -> {}
            is BinOp.Shl -> {}
            is BinOp.Shr -> {}
            is BinOp.Eq -> {}
            is BinOp.Lt -> {}
            is BinOp.Le -> {}
            is BinOp.Ne -> {}
            is BinOp.Ge -> {}
            is BinOp.Gt -> {}
            is BinOp.AddAssign -> {}
            is BinOp.SubAssign -> {}
            is BinOp.MulAssign -> {}
            is BinOp.DivAssign -> {}
            is BinOp.RemAssign -> {}
            is BinOp.BitXorAssign -> {}
            is BinOp.BitAndAssign -> {}
            is BinOp.BitOrAssign -> {}
            is BinOp.ShlAssign -> {}
            is BinOp.ShrAssign -> {}
        }
    }

    public open fun visitPointerMutability(mutability: io.github.kotlinmania.syn.token.Mut?) { /* leaf */ }

    public open fun visitPointerMutability(mutability: PointerMutability) {
        when (mutability) {
            is PointerMutability.Const -> {}
            is PointerMutability.Mut -> {}
        }
    }

    public open fun visitUseGlob(u: UseTree.Glob) { /* leaf */ }

    public open fun visitUseGroup(u: UseTree.Group) {
        u.items.toList().forEach { visitUseTree(it) }
    }

    public open fun visitUseName(u: UseTree.Name) {
        visitIdent(u.ident)
    }

    public open fun visitUsePath(u: UseTree.Path) {
        visitIdent(u.ident)
        u.tree?.let { visitUseTree(it) }
    }

    public open fun visitUseRename(u: UseTree.Name) {
        visitIdent(u.ident)
        u.rename?.let { visitIdent(it.ident) }
    }

    public open fun visitUseTree(u: UseTree) {
        when (u) {
            is UseTree.Path -> visitUsePath(u)
            is UseTree.Name ->
                if (u.rename == null) {
                    visitUseName(u)
                } else {
                    visitUseRename(u)
                }
            is UseTree.Group -> visitUseGroup(u)
            is UseTree.Glob -> visitUseGlob(u)
        }
    }

    public open fun visitVariadic(v: Variadic) {
        v.attrs.forEach { visitAttribute(it) }
        v.pat?.let { visitPat(it.pat) }
    }

    public open fun visitPreciseCapture(p: TypeParamBound.PreciseCapture) {
        p.params.toList().forEach { visitCapturedParam(it) }
    }

    public open fun visitCapturedParam(c: CapturedParam) {
        when (c) {
            is CapturedParam.Lifetime -> visitLifetime(c.lifetime)
            is CapturedParam.Ident -> visitIdent(c.ident)
        }
    }

    public open fun visitVariant(v: Variant) {
        v.attrs.forEach { visitAttribute(it) }
        visitIdent(v.ident)
        visitFields(v.fields)
        v.discriminant?.let { visitExpr(it.expr) }
    }

    public open fun visitVisRestricted(v: Visibility.Restricted) {
        visitPath(v.path)
    }

    public open fun visitVisibility(v: Visibility) {
        when (v) {
            is Visibility.Public -> {}
            is Visibility.Restricted -> visitVisRestricted(v)
            Visibility.Inherited -> {}
        }
    }

    public open fun visitWhereClause(w: WhereClause) {
        w.predicates.toList().forEach { visitWherePredicate(it) }
    }

    public open fun visitWherePredicate(w: WherePredicate) {
        when (w) {
            is WherePredicate.LifetimePredicate -> visitPredicateLifetime(w)
            is WherePredicate.TypePredicate -> visitPredicateType(w)
        }
    }

    public open fun visitSpan(s: Span) { /* leaf */ }

    public open fun visitTokenStream(t: TokenStream) { /* leaf */ }
}
