// port-lint: source gen/visit_mut.rs
package io.github.kotlinmania.syn.gen

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
 * AST mutable visitor.
 *
 * Override methods to intercept and rewrite specific node types. Default
 * implementations recurse into sub-nodes.
 */
public open class VisitMut {
    public open fun visitExpr(e: Expr): Expr {
        when (e) {
            is Expr.Array -> visitExprArrayMut(e)
            is Expr.Assign -> visitExprAssignMut(e)
            is Expr.Async -> visitExprAsyncMut(e)
            is Expr.Await -> visitExprAwaitMut(e)
            is Expr.Binary -> visitExprBinaryMut(e)
            is Expr.BlockExpr -> visitExprBlockMut(e)
            is Expr.Break -> visitExprBreakMut(e)
            is Expr.Call -> visitExprCallMut(e)
            is Expr.Cast -> visitExprCastMut(e)
            is Expr.Closure -> visitExprClosureMut(e)
            is Expr.Const -> visitExprConstMut(e)
            is Expr.Continue -> visitExprContinueMut(e)
            is Expr.Field -> visitExprFieldMut(e)
            is Expr.ForLoop -> visitExprForLoopMut(e)
            is Expr.Group -> visitExprGroupMut(e)
            is Expr.If -> visitExprIfMut(e)
            is Expr.Index -> visitExprIndexMut(e)
            is Expr.Infer -> visitExprInferMut(e)
            is Expr.Let -> visitExprLetMut(e)
            is Expr.Lit -> visitExprLitMut(e)
            is Expr.Loop -> visitExprLoopMut(e)
            is Expr.Macro -> visitExprMacroMut(e)
            is Expr.Match -> visitExprMatchMut(e)
            is Expr.MethodCall -> visitExprMethodCallMut(e)
            is Expr.Paren -> visitExprParenMut(e)
            is Expr.Path -> visitExprPathMut(e)
            is Expr.Range -> visitExprRangeMut(e)
            is Expr.RawAddr -> visitExprRawAddrMut(e)
            is Expr.Reference -> visitExprReferenceMut(e)
            is Expr.Repeat -> visitExprRepeatMut(e)
            is Expr.Return -> visitExprReturnMut(e)
            is Expr.Struct -> visitExprStructMut(e)
            is Expr.Try -> visitExprTryMut(e)
            is Expr.TryBlock -> visitExprTryBlockMut(e)
            is Expr.Tuple -> visitExprTupleMut(e)
            is Expr.Unary -> visitExprUnaryMut(e)
            is Expr.Unsafe -> visitExprUnsafeMut(e)
            is Expr.While -> visitExprWhileMut(e)
            is Expr.Yield -> visitExprYieldMut(e)
            is Expr.Verbatim -> visitTokenStreamMut(e.tokens)
        }
        return e
    }

    public open fun visitExprArrayMut(e: Expr.Array) {
        visitAttributesMut(e.attrs)
        for (i in 0 until e.elems.size) e.elems[i] = visitExprMut(e.elems[i])
    }

    public open fun visitExprAssignMut(e: Expr.Assign) {
        visitAttributesMut(e.attrs)
        e.left = visitExprMut(e.left)
        e.right = visitExprMut(e.right)
    }

    public open fun visitExprAsyncMut(e: Expr.Async) {
        visitAttributesMut(e.attrs)
        visitBlockMut(e.block)
    }

    public open fun visitExprAwaitMut(e: Expr.Await) {
        visitAttributesMut(e.attrs)
        e.base = visitExprMut(e.base)
    }

    public open fun visitExprBinaryMut(e: Expr.Binary) {
        visitAttributesMut(e.attrs)
        e.left = visitExprMut(e.left)
        visitBinOpMut(e.op)
        e.right = visitExprMut(e.right)
    }

    public open fun visitExprBlockMut(e: Expr.BlockExpr) {
        visitAttributesMut(e.attrs)
        e.label?.let { visitLabelMut(it) }
        visitBlockMut(e.block)
    }

    public open fun visitExprBreakMut(e: Expr.Break) {
        visitAttributesMut(e.attrs)
        e.label?.let { visitLifetimeMut(it) }
        e.expr = e.expr?.let { visitExprMut(it) }
    }

    public open fun visitExprCallMut(e: Expr.Call) {
        visitAttributesMut(e.attrs)
        e.func = visitExprMut(e.func)
        for (i in 0 until e.args.size) e.args[i] = visitExprMut(e.args[i])
    }

    public open fun visitExprCastMut(e: Expr.Cast) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        visitTypeMut(e.ty)
    }

    public open fun visitExprClosureMut(e: Expr.Closure) {
        visitAttributesMut(e.attrs)
        for (i in 0 until e.inputs.size) visitPatMut(e.inputs[i])
        visitReturnTypeMut(e.output)
        e.body = visitExprMut(e.body)
    }

    public open fun visitExprConstMut(e: Expr.Const) {
        visitAttributesMut(e.attrs)
        visitBlockMut(e.block)
    }

    public open fun visitExprContinueMut(e: Expr.Continue) {
        visitAttributesMut(e.attrs)
        e.label?.let { visitLifetimeMut(it) }
    }

    public open fun visitExprFieldMut(e: Expr.Field) {
        visitAttributesMut(e.attrs)
        e.base = visitExprMut(e.base)
        visitMemberMut(e.member)
    }

    public open fun visitExprForLoopMut(e: Expr.ForLoop) {
        visitAttributesMut(e.attrs)
        e.label?.let { visitLabelMut(it) }
        visitPatMut(e.pat)
        e.expr = visitExprMut(e.expr)
        visitBlockMut(e.body)
    }

    public open fun visitExprGroupMut(e: Expr.Group) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
    }

    public open fun visitExprIfMut(e: Expr.If) {
        visitAttributesMut(e.attrs)
        e.cond = visitExprMut(e.cond)
        visitBlockMut(e.thenBranch)
        e.elseBranch?.let { visitElseExprMut(it) }
    }

    public open fun visitExprIndexMut(e: Expr.Index) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        e.index = visitExprMut(e.index)
    }

    public open fun visitExprInferMut(e: Expr.Infer) {
        visitAttributesMut(e.attrs)
    }

    public open fun visitExprLetMut(e: Expr.Let) {
        visitAttributesMut(e.attrs)
        visitPatMut(e.pat)
        e.expr = visitExprMut(e.expr)
    }

    public open fun visitExprLitMut(e: Expr.Lit) {
        visitAttributesMut(e.attrs)
        visitLitMut(e.lit)
    }

    public open fun visitExprLoopMut(e: Expr.Loop) {
        visitAttributesMut(e.attrs)
        e.label?.let { visitLabelMut(it) }
        visitBlockMut(e.body)
    }

    public open fun visitExprMacroMut(e: Expr.Macro) {
        visitAttributesMut(e.attrs)
        visitMacroMut(e.mac)
    }

    public open fun visitExprMatchMut(e: Expr.Match) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        for (i in e.arms.indices) visitArmMut(e.arms[i])
    }

    public open fun visitExprMethodCallMut(e: Expr.MethodCall) {
        visitAttributesMut(e.attrs)
        e.receiver = visitExprMut(e.receiver)
        visitIdentMut(e.method)
        e.turbofish?.let { visitAngleBracketedGenericArgumentsMut(it) }
        for (i in 0 until e.args.size) e.args[i] = visitExprMut(e.args[i])
    }

    public open fun visitExprParenMut(e: Expr.Paren) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
    }

    public open fun visitExprRangeMut(e: Expr.Range) {
        visitAttributesMut(e.attrs)
        e.start = e.start?.let { visitExprMut(it) }
        visitRangeLimitsMut(e.limits)
        e.end = e.end?.let { visitExprMut(it) }
    }

    public open fun visitExprRawAddrMut(e: Expr.RawAddr) {
        visitAttributesMut(e.attrs)
        visitPointerMutabilityMut(e.mutability)
        e.expr = visitExprMut(e.expr)
    }

    public open fun visitExprReferenceMut(e: Expr.Reference) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
    }

    public open fun visitExprRepeatMut(e: Expr.Repeat) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        e.len = visitExprMut(e.len)
    }

    public open fun visitExprReturnMut(e: Expr.Return) {
        visitAttributesMut(e.attrs)
        e.expr = e.expr?.let { visitExprMut(it) }
    }

    public open fun visitExprStructMut(e: Expr.Struct) {
        visitAttributesMut(e.attrs)
        e.qself?.let { visitQSelfMut(it) }
        visitPathMut(e.path)
        for (i in 0 until e.fields.size) visitFieldValueMut(e.fields[i])
        e.rest = e.rest?.let { visitExprMut(it) }
    }

    public open fun visitExprTryMut(e: Expr.Try) {
        visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
    }

    public open fun visitExprTryBlockMut(e: Expr.TryBlock) {
        visitAttributesMut(e.attrs)
        visitBlockMut(e.block)
    }

    public open fun visitExprTupleMut(e: Expr.Tuple) {
        visitAttributesMut(e.attrs)
        for (i in 0 until e.elems.size) e.elems[i] = visitExprMut(e.elems[i])
    }

    public open fun visitExprUnaryMut(e: Expr.Unary) {
        visitAttributesMut(e.attrs)
        visitUnOpMut(e.op)
        e.expr = visitExprMut(e.expr)
    }

    public open fun visitExprUnsafeMut(e: Expr.Unsafe) {
        visitAttributesMut(e.attrs)
        visitBlockMut(e.block)
    }

    public open fun visitExprWhileMut(e: Expr.While) {
        visitAttributesMut(e.attrs)
        e.label?.let { visitLabelMut(it) }
        e.cond = visitExprMut(e.cond)
        visitBlockMut(e.body)
    }

    public open fun visitExprYieldMut(e: Expr.Yield) {
        visitAttributesMut(e.attrs)
        e.expr = e.expr?.let { visitExprMut(it) }
    }

    public open fun visitType(t: SynType) {
        when (t) {
            is SynType.Array -> visitTypeArrayMut(t)
            is SynType.BareFn -> visitTypeBareFnMut(t)
            is SynType.Group -> visitTypeGroupMut(t)
            is SynType.ImplTrait -> visitTypeImplTraitMut(t)
            is SynType.Infer -> visitTypeInferMut(t)
            is SynType.Macro -> visitTypeMacroMut(t)
            is SynType.Never -> visitTypeNeverMut(t)
            is SynType.Paren -> visitTypeParenMut(t)
            is SynType.Path -> visitTypePathMut(t)
            is SynType.Ptr -> visitTypePtrMut(t)
            is SynType.Reference -> visitTypeReferenceMut(t)
            is SynType.Slice -> visitTypeSliceMut(t)
            is SynType.TraitObject -> visitTypeTraitObjectMut(t)
            is SynType.Tuple -> visitTypeTupleMut(t)
            is SynType.Verbatim -> {
                visitTokenStreamMut(t.tokens)
            }
        }
    }

    public open fun visitPath(p: Path) {
        for (i in 0 until p.segments.size) visitPathSegmentMut(p.segments[i])
    }

    public open fun visitPat(p: Pat) {
        when (p) {
            is Pat.Const -> {
                visitAttributesMut(p.attrs)
                visitBlockMut(p.block)
            }
            is Pat.Ident -> visitPatIdentMut(p)
            is Pat.Lit -> {
                visitAttributesMut(p.attrs)
                visitLitMut(p.lit)
            }
            is Pat.Macro -> {
                visitAttributesMut(p.attrs)
                visitMacroMut(p.mac)
            }
            is Pat.Or -> visitPatOrMut(p)
            is Pat.PatParen -> visitPatParenMut(p)
            is Pat.Path -> {
                visitAttributesMut(p.attrs)
                p.qself?.let { visitQSelfMut(it) }
                visitPathMut(p.path)
            }
            is Pat.Range -> {
                visitAttributesMut(p.attrs)
                p.start = p.start?.let { visitExprMut(it) }
                visitRangeLimitsMut(p.limits)
                p.end = p.end?.let { visitExprMut(it) }
            }
            is Pat.Reference -> visitPatReferenceMut(p)
            is Pat.Rest -> visitPatRestMut(p)
            is Pat.Slice -> visitPatSliceMut(p)
            is Pat.Struct -> visitPatStructMut(p)
            is Pat.Tuple -> visitPatTupleMut(p)
            is Pat.TupleStruct -> visitPatTupleStructMut(p)
            is Pat.TypeAscription -> visitPatTypeMut(p)
            is Pat.Wild -> visitPatWildMut(p)
            is Pat.Verbatim -> {
                visitTokenStreamMut(p.tokens)
            }
        }
    }

    public open fun visitItem(i: Item) {
        when (i) {
            is Item.Const -> visitItemConstMut(i)
            is Item.Enum -> visitItemEnumMut(i)
            is Item.ExternCrate -> visitItemExternCrateMut(i)
            is Item.Fn -> visitItemFnMut(i)
            is Item.ForeignMod -> visitItemForeignModMut(i)
            is Item.Impl -> visitItemImplMut(i)
            is Item.Macro -> visitItemMacroMut(i)
            is Item.Mod -> visitItemModMut(i)
            is Item.Static -> visitItemStaticMut(i)
            is Item.Struct -> visitItemStructMut(i)
            is Item.Trait -> visitItemTraitMut(i)
            is Item.TraitAlias -> visitItemTraitAliasMut(i)
            is Item.ItemType -> visitItemTypeMut(i)
            is Item.Union -> visitItemUnionMut(i)
            is Item.Use -> visitItemUseMut(i)
            is Item.Verbatim -> {
                visitTokenStreamMut(i.tokens)
            }
        }
    }

    public open fun visitFile(f: File) {
        visitAttributesMut(f.attrs)
        for (i in f.items.indices) visitItemMut(f.items[i])
    }

    public open fun visitAttribute(a: Attribute) {
        visitAttrStyleMut(a.style)
        visitMetaMut(a.meta)
    }

    public open fun visitAttrStyle(style: AttrStyle) { }

    public open fun visitMeta(m: Meta) {
        when (m) {
            is Meta.PathMeta -> {
                visitPathMut(m.path)
            }
            is Meta.List -> visitMetaListMut(m)
            is Meta.NameValue -> visitMetaNameValueMut(m)
        }
    }

    public open fun visitMetaList(m: Meta.List) {
        visitMacroDelimiterMut(m.delimiter)
        visitTokenStreamMut(m.tokens)
        visitPathMut(m.path)
    }

    public open fun visitMetaNameValue(m: Meta.NameValue) {
        visitPathMut(m.path)
        m.value = visitExprMut(m.value)
    }

    public open fun visitGenerics(g: Generics) {
        for (i in 0 until g.params.size) visitGenericParamMut(g.params[i])
        g.whereClause?.let { visitWhereClauseMut(it) }
    }

    public open fun visitLit(l: Lit) {
        when (l) {
            is Lit.Str -> {
                visitLitStrMut(l.value)
            }
            is Lit.ByteStr -> {
                visitLitByteStrMut(l.value)
            }
            is Lit.CStr -> {
                visitLitCStrMut(l.value)
            }
            is Lit.Byte -> {
                visitLitByteMut(l.value)
            }
            is Lit.Char -> {
                visitLitCharMut(l.value)
            }
            is Lit.Int -> {
                visitLitIntMut(l.value)
            }
            is Lit.Float -> {
                visitLitFloatMut(l.value)
            }
            is Lit.Bool -> {
                visitLitBoolMut(l.value)
            }
            is Lit.Verbatim -> { }
        }
    }

    public open fun visitLitBoolMut(l: LitBool) {
        val span = l.span()
        visitSpanMut(span)
        l.setSpan(span)
    }

    public open fun visitLitByteMut(l: LitByte) { }

    public open fun visitLitByteStrMut(l: LitByteStr) { }

    public open fun visitLitCStrMut(l: LitCStr) { }

    public open fun visitLitCstrMut(l: LitCStr) { visitLitCStrMut(l) }

    public open fun visitLitCharMut(l: LitChar) { }

    public open fun visitLitFloatMut(l: LitFloat) { }

    public open fun visitLitIntMut(l: LitInt) { }

    public open fun visitLitStrMut(l: LitStr) { }

    public open fun visitLifetime(lt: Lifetime) {
        visitSpanMut(lt.apostrophe)
        visitIdentMut(lt.ident)
    }

    public open fun visitIdent(id: Ident) {
        val span = id.span()
        visitSpanMut(span)
        id.setSpan(span)
    }

    public open fun visitStmt(s: Stmt): Stmt =
        when (s) {
            is Stmt.Local -> { visitLocalMut(s); s }
            is Stmt.ItemStmt -> {
                visitItemMut(s.item)
                s
            }
            is Stmt.ExprStmt -> {
                s.expr = visitExprMut(s.expr)
                s
            }
            is Stmt.MacroStmt -> { visitStmtMacroMut(s); s }
        }

    public open fun visitData(d: Data) {
        when (d) {
            is Data.Struct -> {
                visitDataStructMut(d.value)
            }
            is Data.Enum -> {
                visitDataEnumMut(d.value)
            }
            is Data.Union -> {
                visitDataUnionMut(d.value)
            }
        }
    }

    public open fun visitDataEnum(d: DataEnum) {
        for (i in 0 until d.variants.size) visitVariantMut(d.variants[i])
    }

    public open fun visitDataStruct(d: DataStruct) {
        visitFieldsMut(d.fields)
    }

    public open fun visitDataUnion(d: DataUnion) {
        visitFieldsNamedMut(d.fields)
    }

    public open fun visitLabelMut(label: Label) {
        visitLifetimeMut(label.name)
    }

    public open fun visitDeriveInput(di: DeriveInput) {
        visitAttributesMut(di.attrs)
        visitVisibilityMut(di.vis)
        visitIdentMut(di.ident)
        visitGenericsMut(di.generics)
        visitDataMut(di.data)
    }

    public open fun visitBlock(block: Block) {
        for (i in 0 until block.stmts.size) block.stmts[i] = visitStmtMut(block.stmts[i])
    }

    public open fun visitAttributes(attrs: MutableList<Attribute>) {
        for (i in attrs.indices) visitAttributeMut(attrs[i])
    }

    public open fun visitSignature(sig: Signature) {
        sig.abi?.let { visitAbiMut(it) }
        visitIdentMut(sig.ident)
        visitGenericsMut(sig.generics)
        for (i in 0 until sig.inputs.size) visitFnArgMut(sig.inputs[i])
        sig.variadic?.let { visitVariadicMut(it) }
        visitReturnTypeMut(sig.output)
    }

    public open fun visitAbi(a: Abi) { }

    public open fun visitReturnType(rt: ReturnType) {
        when (rt) {
            is ReturnType.Default -> { }
            is ReturnType.TypeReturn -> {
                visitTypeMut(rt.ty)
            }
        }
    }

    public open fun visitFnArg(arg: FnArg) {
        when (arg) {
            is FnArg.Receiver -> visitReceiverMut(arg)
            is FnArg.Typed -> {
                visitPatTypeMut(arg.patType)
            }
        }
    }

    public open fun visitForeignItem(item: ForeignItem) {
        when (item) {
            is ForeignItem.Fn -> visitForeignItemFnMut(item)
            is ForeignItem.Static -> visitForeignItemStaticMut(item)
            is ForeignItem.ItemType -> visitForeignItemTypeMut(item)
            is ForeignItem.Macro -> visitForeignItemMacroMut(item)
            is ForeignItem.Verbatim -> {
                visitTokenStreamMut(item.tokens)
            }
        }
    }

    public open fun visitForeignItemFn(item: ForeignItem.Fn) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitSignatureMut(item.sig)
    }

    public open fun visitForeignItemMacro(item: ForeignItem.Macro) {
        visitAttributesMut(item.attrs)
        visitMacroMut(item.mac)
    }

    public open fun visitForeignItemStatic(item: ForeignItem.Static) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitStaticMutabilityMut(item.mutability)
        visitIdentMut(item.ident)
        visitTypeMut(item.ty)
    }

    public open fun visitForeignItemType(item: ForeignItem.ItemType) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
    }

    public open fun visitReceiver(receiver: FnArg.Receiver) {
        visitAttributesMut(receiver.attrs)
        receiver.reference?.let { it.lifetime?.let { lt -> visitLifetimeMut(lt) } }
        visitTypeMut(receiver.type)
    }

    public open fun visitPatType(patType: PatType) {
        visitPatMut(patType.pat)
        visitTypeMut(patType.ty)
    }

    public open fun visitPatIdent(patIdent: Pat.Ident) {
        visitAttributesMut(patIdent.attrs)
        visitIdentMut(patIdent.ident)
        patIdent.subpat?.let { visitPatMut(it) }
    }

    public open fun visitPatOrMut(pat: Pat.Or) {
        visitAttributesMut(pat.attrs)
        for (i in 0 until pat.cases.size) visitPatMut(pat.cases[i])
    }

    public open fun visitPatParenMut(pat: Pat.PatParen) {
        visitAttributesMut(pat.attrs)
        visitPatMut(pat.pat)
    }

    public open fun visitPatReferenceMut(pat: Pat.Reference) {
        visitAttributesMut(pat.attrs)
        visitPatMut(pat.pat)
    }

    public open fun visitPatRestMut(pat: Pat.Rest) {
        visitAttributesMut(pat.attrs)
    }

    public open fun visitPatRestMut(rest: PatRest) {
        visitAttributesMut(rest.attrs)
    }

    public open fun visitPatSliceMut(pat: Pat.Slice) {
        visitAttributesMut(pat.attrs)
        for (i in 0 until pat.elems.size) visitPatMut(pat.elems[i])
    }

    public open fun visitPatStructMut(pat: Pat.Struct) {
        visitAttributesMut(pat.attrs)
        pat.qself?.let { visitQSelfMut(it) }
        visitPathMut(pat.path)
        for (i in 0 until pat.fields.size) visitFieldPatMut(pat.fields[i])
        pat.rest?.let { visitPatRestMut(it) }
    }

    public open fun visitPatTupleMut(pat: Pat.Tuple) {
        visitAttributesMut(pat.attrs)
        for (i in 0 until pat.elems.size) visitPatMut(pat.elems[i])
    }

    public open fun visitPatTupleStructMut(pat: Pat.TupleStruct) {
        visitAttributesMut(pat.attrs)
        pat.qself?.let { visitQSelfMut(it) }
        visitPathMut(pat.path)
        for (i in 0 until pat.elems.size) visitPatMut(pat.elems[i])
    }

    public open fun visitPatWildMut(pat: Pat.Wild) {
        visitAttributesMut(pat.attrs)
    }

    public open fun visitPatTypeMut(pat: Pat.TypeAscription) {
        visitAttributesMut(pat.attrs)
        visitPatMut(pat.pat)
        visitTypeMut(pat.ty)
    }

    public open fun visitTypePath(typePath: SynType.Path) {
        typePath.qself?.let { visitQSelfMut(it) }
        visitPathMut(typePath.path)
    }

    public open fun visitTypeReference(ty: SynType.Reference) {
        ty.lifetime?.let { visitLifetimeMut(it) }
        visitTypeMut(ty.elem)
    }

    public open fun visitTypeArray(ty: SynType.Array) {
        visitTypeMut(ty.elem)
        ty.len = visitExprMut(ty.len)
    }

    public open fun visitTypeGroup(ty: SynType.Group) {
        visitTypeMut(ty.elem)
    }

    public open fun visitTypeImplTrait(ty: SynType.ImplTrait) {
        for (i in 0 until ty.bounds.size) visitTypeParamBoundMut(ty.bounds[i])
    }

    public open fun visitTypeInfer(ty: SynType.Infer) { }

    public open fun visitTypeMacro(ty: SynType.Macro) {
        visitMacroMut(ty.mac)
    }

    public open fun visitTypeNever(ty: SynType.Never) { }

    public open fun visitTypePtr(ty: SynType.Ptr) {
        visitPointerMutabilityMut(ty.mutability)
        visitTypeMut(ty.elem)
    }

    public open fun visitPointerMutability(mutability: PointerMutability) { }

    public open fun visitPointerMutabilityMut(mutability: io.github.kotlinmania.syn.token.Mut?) { }

    public open fun visitTypeBareFn(ty: SynType.BareFn) {
        ty.lifetimes?.let { visitBoundLifetimesMut(it) }
        ty.abi?.let { visitAbiMut(it) }
        for (i in 0 until ty.inputs.size) visitBareFnArgMut(ty.inputs[i])
        ty.variadic?.let { visitBareVariadicMut(it) }
        visitReturnTypeMut(ty.output)
    }

    public open fun visitBareFnArg(arg: BareFnArg) {
        visitAttributesMut(arg.attrs)
        arg.name?.let { visitIdentMut(it.ident) }
        visitTypeMut(arg.ty)
    }

    public open fun visitBareVariadic(variadic: BareVariadic) {
        visitAttributesMut(variadic.attrs)
        variadic.name?.let { visitIdentMut(it.ident) }
    }

    public open fun visitTypeParen(ty: SynType.Paren) {
        visitTypeMut(ty.elem)
    }

    public open fun visitTypeSlice(ty: SynType.Slice) {
        visitTypeMut(ty.elem)
    }

    public open fun visitTypeTraitObject(ty: SynType.TraitObject) {
        for (i in 0 until ty.bounds.size) visitTypeParamBoundMut(ty.bounds[i])
    }

    public open fun visitTypeTuple(ty: SynType.Tuple) {
        for (i in 0 until ty.elems.size) visitTypeMut(ty.elems[i])
    }

    public open fun visitExprPath(exprPath: Expr.Path) {
        visitAttributesMut(exprPath.attrs)
        exprPath.qself?.let { visitQSelfMut(it) }
        visitPathMut(exprPath.path)
    }

    public open fun visitMacro(mac: Macro) {
        visitPathMut(mac.path)
        visitMacroDelimiterMut(mac.delimiter)
        visitTokenStreamMut(mac.tokens)
    }

    public open fun visitPathArguments(pathArgs: PathArguments) {
        when (pathArgs) {
            is PathArguments.None -> { }
            is PathArguments.AngleBracketed -> visitAngleBracketedGenericArgumentsMut(pathArgs)
            is PathArguments.Parenthesized -> visitParenthesizedGenericArgumentsMut(pathArgs)
        }
    }

    public open fun visitAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed) {
        for (i in 0 until pathArgs.args.size) visitGenericArgumentMut(pathArgs.args[i])
    }

    public open fun visitParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized) {
        for (i in 0 until pathArgs.inputs.size) visitTypeMut(pathArgs.inputs[i])
        visitReturnTypeMut(pathArgs.output)
    }

    public open fun visitGenericArgument(genArg: GenericArgument) {
        when (genArg) {
            is GenericArgument.LifetimeArg -> {
                visitLifetimeMut(genArg.lifetime)
            }
            is GenericArgument.TypeArg -> {
                visitTypeMut(genArg.type)
            }
            is GenericArgument.ConstArg -> {
                genArg.expr = visitExprMut(genArg.expr)
            }
            is GenericArgument.AssocTypeArg -> {
                visitAssocTypeMut(genArg.assoc)
            }
            is GenericArgument.AssocConstArg -> {
                visitAssocConstMut(genArg.assoc)
            }
            is GenericArgument.ConstraintArg -> {
                visitConstraintMut(genArg.constraint)
            }
        }
    }

    public open fun visitAssocType(assoc: AssocType) {
        visitIdentMut(assoc.ident)
        assoc.generics?.let { visitAngleBracketedGenericArgumentsMut(it) }
        visitTypeMut(assoc.ty)
    }

    public open fun visitAssocConst(assoc: AssocConst) {
        visitIdentMut(assoc.ident)
        assoc.generics?.let { visitAngleBracketedGenericArgumentsMut(it) }
        assoc.value = visitExprMut(assoc.value)
    }

    public open fun visitConstraint(constraint: Constraint) {
        visitIdentMut(constraint.ident)
        constraint.generics?.let { visitAngleBracketedGenericArgumentsMut(it) }
        for (i in 0 until constraint.bounds.size) visitTypeParamBoundMut(constraint.bounds[i])
    }

    public open fun visitTypeParamBound(bound: TypeParamBound) {
        when (bound) {
            is TypeParamBound.Trait -> visitTraitBoundMut(bound)
            is TypeParamBound.LifetimeBound -> {
                visitLifetimeMut(bound.lifetime)
            }
            is TypeParamBound.PreciseCapture -> visitPreciseCaptureMut(bound)
            is TypeParamBound.Verbatim -> {
                visitTokenStreamMut(bound.tokens)
            }
        }
    }

    public open fun visitTraitBound(bound: TypeParamBound.Trait) {
        visitTraitBoundModifierMut(bound.modifier)
        bound.lifetimes?.let { visitBoundLifetimesMut(it) }
        visitPathMut(bound.path)
    }

    public open fun visitTraitBoundModifier(modifier: TraitBoundModifier) { }

    public open fun visitBinOp(op: BinOp) { }

    public open fun visitUnOpMut(op: UnOp) { }

    public open fun visitBoundLifetimes(boundLifetimes: BoundLifetimes) {
        for (i in 0 until boundLifetimes.lifetimes.size) visitGenericParamMut(boundLifetimes.lifetimes[i])
    }

    public open fun visitCapturedParam(param: CapturedParam) {
        when (param) {
            is CapturedParam.Lifetime -> {
                visitLifetimeMut(param.lifetime)
            }
            is CapturedParam.Ident -> {
                visitIdentMut(param.ident)
            }
        }
    }

    public open fun visitPreciseCaptureMut(param: TypeParamBound.PreciseCapture) {
        for (i in 0 until param.params.size) visitCapturedParamMut(param.params[i])
    }

    public open fun visitPathSegment(segment: PathSegment) {
        visitIdentMut(segment.ident)
        visitPathArgumentsMut(segment.arguments)
    }

    public open fun visitArm(arm: Arm) {
        visitAttributesMut(arm.attrs)
        visitPatMut(arm.pat)
        arm.guard?.let { it.expr = visitExprMut(it.expr) }
        arm.body = visitExprMut(arm.body)
    }

    public open fun visitElseExpr(elseExpr: ElseExpr) {
        elseExpr.expr = visitExprMut(elseExpr.expr)
    }

    public open fun visitFieldPat(fieldPat: FieldPat) {
        visitAttributesMut(fieldPat.attrs)
        visitMemberMut(fieldPat.member)
        visitPatMut(fieldPat.pat)
    }

    public open fun visitFieldValue(fieldValue: FieldValue) {
        visitAttributesMut(fieldValue.attrs)
        visitMemberMut(fieldValue.member)
        fieldValue.expr = visitExprMut(fieldValue.expr)
    }

    public open fun visitGenericParam(param: GenericParam) {
        when (param) {
            is GenericParam.LifetimeParam -> visitLifetimeParamMut(param)
            is GenericParam.TypeParam -> visitTypeParamMut(param)
            is GenericParam.ConstParam -> visitConstParamMut(param)
        }
    }

    public open fun visitLifetimeParamMut(param: GenericParam.LifetimeParam) {
        visitAttributesMut(param.attrs)
        visitLifetimeMut(param.lifetime)
        for (i in 0 until param.bounds.size) visitLifetimeMut(param.bounds[i])
    }

    public open fun visitTypeParamMut(param: GenericParam.TypeParam) {
        visitAttributesMut(param.attrs)
        visitIdentMut(param.ident)
        for (i in 0 until param.bounds.size) visitTypeParamBoundMut(param.bounds[i])
        param.default?.let { visitTypeMut(it) }
    }

    public open fun visitConstParamMut(param: GenericParam.ConstParam) {
        visitAttributesMut(param.attrs)
        visitIdentMut(param.ident)
        visitTypeMut(param.ty)
        param.default = param.default?.let { visitExprMut(it) }
    }

    public open fun visitField(field: Field) {
        visitAttributesMut(field.attrs)
        visitVisibilityMut(field.vis)
        visitFieldMutabilityMut(field.mutability)
        field.ident?.let { visitIdentMut(it) }
        visitTypeMut(field.ty)
    }

    public open fun visitFieldMutability(fieldMutability: FieldMutability) { }

    public open fun visitFields(fields: Fields) {
        when (fields) {
            is Fields.Named -> {
                visitFieldsNamedMut(fields.fields)
            }
            is Fields.Unnamed -> {
                visitFieldsUnnamedMut(fields.fields)
            }
            Fields.Unit -> { }
        }
    }

    public open fun visitFieldsNamed(fields: FieldsNamed) {
        for (i in 0 until fields.named.size) visitFieldMut(fields.named[i])
    }

    public open fun visitFieldsUnnamed(fields: FieldsUnnamed) {
        for (i in 0 until fields.unnamed.size) visitFieldMut(fields.unnamed[i])
    }

    public open fun visitImplItem(item: ImplItem) {
        when (item) {
            is ImplItem.Const -> visitImplItemConstMut(item)
            is ImplItem.Fn -> visitImplItemFnMut(item)
            is ImplItem.AssocType -> visitImplItemTypeMut(item)
            is ImplItem.Macro -> visitImplItemMacroMut(item)
            is ImplItem.Verbatim -> {
                visitTokenStreamMut(item.tokens)
            }
        }
    }

    public open fun visitImplItemConst(item: ImplItem.Const) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        visitTypeMut(item.ty)
        item.expr = visitExprMut(item.expr)
    }

    public open fun visitImplItemFn(item: ImplItem.Fn) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitSignatureMut(item.sig)
        visitBlockMut(item.block)
    }

    public open fun visitImplItemMacro(item: ImplItem.Macro) {
        visitAttributesMut(item.attrs)
        visitMacroMut(item.mac)
    }

    public open fun visitImplItemType(item: ImplItem.AssocType) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        visitTypeMut(item.ty)
    }

    public open fun visitImplRestriction(restriction: ImplRestriction) { }

    public open fun visitItemConst(item: Item.Const) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitTypeMut(item.ty)
        item.expr = item.expr?.let { visitExprMut(it) }
    }

    public open fun visitItemEnum(item: Item.Enum) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        for (i in 0 until item.variants.size) visitVariantMut(item.variants[i])
    }

    public open fun visitItemExternCrate(item: Item.ExternCrate) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        item.rename?.let { visitIdentMut(it.ident) }
    }

    public open fun visitItemFn(item: Item.Fn) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitSignatureMut(item.sig)
        item.block?.let { visitBlockMut(it) }
    }

    public open fun visitItemForeignMod(item: Item.ForeignMod) {
        visitAttributesMut(item.attrs)
        visitAbiMut(item.abi)
        for (i in item.items.indices) visitForeignItemMut(item.items[i])
    }

    public open fun visitItemImpl(item: Item.Impl) {
        visitAttributesMut(item.attrs)
        visitGenericsMut(item.generics)
        item.traitPath?.let { visitPathTraitMut(it) }
        visitTypeMut(item.selfType)
        for (i in item.items.indices) visitImplItemMut(item.items[i])
    }

    public open fun visitItemMacro(item: Item.Macro) {
        visitAttributesMut(item.attrs)
        item.ident?.let { visitIdentMut(it) }
        visitMacroMut(item.mac)
    }

    public open fun visitItemMod(item: Item.Mod) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        item.content?.let { visitModContentMut(it) }
    }

    public open fun visitItemStatic(item: Item.Static) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitStaticMutabilityMut(item.mutability)
        visitIdentMut(item.ident)
        visitTypeMut(item.ty)
        item.expr = visitExprMut(item.expr)
    }

    public open fun visitItemStruct(item: Item.Struct) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        visitFieldsMut(item.fields)
    }

    public open fun visitItemTrait(item: Item.Trait) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        item.restriction?.let { visitImplRestrictionMut(it) }
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        for (i in 0 until item.supertraits.size) visitTypeParamBoundMut(item.supertraits[i])
        for (i in item.items.indices) visitTraitItemMut(item.items[i])
    }

    public open fun visitItemTraitAlias(item: Item.TraitAlias) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        for (i in 0 until item.bounds.size) visitTypeParamBoundMut(item.bounds[i])
    }

    public open fun visitItemType(item: Item.ItemType) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        visitTypeMut(item.ty)
    }

    public open fun visitItemUnion(item: Item.Union) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        visitFieldsNamedMut(item.fields)
    }

    public open fun visitItemUse(item: Item.Use) {
        visitAttributesMut(item.attrs)
        visitVisibilityMut(item.vis)
        visitUseTreeMut(item.tree)
    }

    public open fun visitStaticMutability(mutability: StaticMutability) { }

    public open fun visitRangeLimitsMut(limits: RangeLimits) { }

    public open fun visitMacroDelimiterMut(delimiter: MacroDelimiter) { }

    public open fun visitModContent(modContent: ModContent) {
        when (modContent) {
            is ModContent.Inline -> {
                for (i in modContent.items.indices) visitItemMut(modContent.items[i])
            }
            is ModContent.Unnamed -> { }
        }
    }

    public open fun visitLocalMut(local: Stmt.Local) {
        visitAttributesMut(local.attrs)
        visitPatMut(local.pat)
        local.init?.let { visitLocalInitMut(it) }
    }

    public open fun visitLocalInit(init: LocalInit) {
        init.expr = visitExprMut(init.expr)
        init.diverge?.let { visitElseExprMut(it) }
    }

    public open fun visitMember(member: Member) {
        when (member) {
            is Member.Named -> {
                visitIdentMut(member.ident)
            }
            is Member.Unnamed -> {
                visitIndexMut(member.index)
            }
        }
    }

    public open fun visitIndexMut(index: Index) {
        visitSpanMut(index.span)
    }

    public open fun visitQSelf(qself: QSelf) {
        visitTypeMut(qself.ty)
    }

    public open fun visitQselfMut(qself: QSelf) { visitQSelfMut(qself) }

    public open fun visitPathTrait(pathTrait: PathTrait) {
        visitPathMut(pathTrait.path)
    }

    public open fun visitTraitItem(item: TraitItem) {
        when (item) {
            is TraitItem.Const -> visitTraitItemConstMut(item)
            is TraitItem.Fn -> visitTraitItemFnMut(item)
            is TraitItem.AssocType -> visitTraitItemTypeMut(item)
            is TraitItem.Macro -> visitTraitItemMacroMut(item)
            is TraitItem.Verbatim -> {
                visitTokenStreamMut(item.tokens)
            }
        }
    }

    public open fun visitTraitItemConst(item: TraitItem.Const) {
        visitAttributesMut(item.attrs)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        visitTypeMut(item.ty)
        item.default?.let { it.expr = visitExprMut(it.expr) }
    }

    public open fun visitTraitItemFn(item: TraitItem.Fn) {
        visitAttributesMut(item.attrs)
        visitSignatureMut(item.sig)
        item.default?.let { visitBlockMut(it) }
    }

    public open fun visitTraitItemMacro(item: TraitItem.Macro) {
        visitAttributesMut(item.attrs)
        visitMacroMut(item.mac)
    }

    public open fun visitTraitItemType(item: TraitItem.AssocType) {
        visitAttributesMut(item.attrs)
        visitIdentMut(item.ident)
        visitGenericsMut(item.generics)
        for (i in 0 until item.bounds.size) visitTypeParamBoundMut(item.bounds[i])
        item.default?.let { visitTypeMut(it.type) }
    }

    public open fun visitUseTree(useTree: UseTree) {
        when (useTree) {
            is UseTree.Path -> visitUsePathMut(useTree)
            is UseTree.Name ->
                if (useTree.rename == null) {
                    visitUseNameMut(useTree)
                } else {
                    visitUseRenameMut(useTree)
                }
            is UseTree.Group -> visitUseGroupMut(useTree)
            is UseTree.Glob -> visitUseGlobMut(useTree)
        }
    }

    public open fun visitUseGlob(useTree: UseTree.Glob) { }

    public open fun visitUseGroup(useTree: UseTree.Group) {
        for (i in 0 until useTree.items.size) visitUseTreeMut(useTree.items[i])
    }

    public open fun visitUseName(useTree: UseTree.Name) {
        visitIdentMut(useTree.ident)
    }

    public open fun visitUsePath(useTree: UseTree.Path) {
        visitIdentMut(useTree.ident)
        useTree.tree?.let { visitUseTreeMut(it) }
    }

    public open fun visitUseRename(useTree: UseTree.Name) {
        visitIdentMut(useTree.ident)
        useTree.rename?.let { visitIdentMut(it.ident) }
    }

    public open fun visitVariadic(variadic: Variadic) {
        visitAttributesMut(variadic.attrs)
        variadic.pat?.let { visitPatMut(it.pat) }
    }

    public open fun visitVariant(variant: Variant) {
        visitAttributesMut(variant.attrs)
        visitIdentMut(variant.ident)
        visitFieldsMut(variant.fields)
        variant.discriminant?.let { it.expr = visitExprMut(it.expr) }
    }

    public open fun visitVisibility(visibility: Visibility) {
        when (visibility) {
            is Visibility.Public -> { }
            is Visibility.Restricted -> visitVisRestrictedMut(visibility)
            Visibility.Inherited -> { }
        }
    }

    public open fun visitVisRestrictedMut(visibility: Visibility.Restricted) {
        visitPathMut(visibility.path)
    }

    public open fun visitWhereClause(whereClause: WhereClause) {
        for (i in 0 until whereClause.predicates.size) visitWherePredicateMut(whereClause.predicates[i])
    }

    public open fun visitWherePredicate(wherePredicate: WherePredicate) {
        when (wherePredicate) {
            is WherePredicate.LifetimePredicate -> visitPredicateLifetimeMut(wherePredicate)
            is WherePredicate.TypePredicate -> visitPredicateTypeMut(wherePredicate)
        }
    }

    public open fun visitPredicateLifetimeMut(predicate: WherePredicate.LifetimePredicate) {
        visitLifetimeMut(predicate.lifetime)
        for (i in 0 until predicate.bounds.size) visitLifetimeMut(predicate.bounds[i])
    }

    public open fun visitPredicateTypeMut(predicate: WherePredicate.TypePredicate) {
        predicate.lifetimes?.let { visitBoundLifetimesMut(it) }
        visitTypeMut(predicate.boundedTy)
        for (i in 0 until predicate.bounds.size) visitTypeParamBoundMut(predicate.bounds[i])
    }

    public open fun visitStmtMacroMut(stmt: Stmt.MacroStmt) {
        visitAttributesMut(stmt.attrs)
        visitMacroMut(stmt.mac)
    }

    public open fun visitSpanMut(span: Span) { }

    public open fun visitTokenStreamMut(tokens: TokenStream) { }

    public open fun visitExprMut(e: Expr): Expr = visitExpr(e)

    public open fun visitTypeMut(t: SynType) { visitType(t) }

    public open fun visitPathMut(p: Path) { visitPath(p) }

    public open fun visitPatMut(p: Pat) { visitPat(p) }

    public open fun visitItemMut(i: Item) { visitItem(i) }

    public open fun visitFileMut(f: File) { visitFile(f) }

    public open fun visitAttributeMut(a: Attribute) { visitAttribute(a) }

    public open fun visitAttrStyleMut(style: AttrStyle) { visitAttrStyle(style) }

    public open fun visitMetaMut(m: Meta) { visitMeta(m) }

    public open fun visitMetaListMut(m: Meta.List) { visitMetaList(m) }

    public open fun visitMetaNameValueMut(m: Meta.NameValue) { visitMetaNameValue(m) }

    public open fun visitGenericsMut(g: Generics) { visitGenerics(g) }

    public open fun visitLitMut(l: Lit) { visitLit(l) }

    public open fun visitLifetimeMut(lt: Lifetime) { visitLifetime(lt) }

    public open fun visitIdentMut(id: Ident) { visitIdent(id) }

    public open fun visitStmtMut(s: Stmt): Stmt = visitStmt(s)

    public open fun visitDataMut(d: Data) { visitData(d) }

    public open fun visitDataEnumMut(d: DataEnum) { visitDataEnum(d) }

    public open fun visitDataStructMut(d: DataStruct) { visitDataStruct(d) }

    public open fun visitDataUnionMut(d: DataUnion) { visitDataUnion(d) }

    public open fun visitDeriveInputMut(di: DeriveInput) { visitDeriveInput(di) }

    public open fun visitBlockMut(block: Block) { visitBlock(block) }

    public open fun visitAttributesMut(attrs: MutableList<Attribute>) { visitAttributes(attrs) }

    public open fun visitSignatureMut(sig: Signature) { visitSignature(sig) }

    public open fun visitAbiMut(a: Abi) { visitAbi(a) }

    public open fun visitReturnTypeMut(rt: ReturnType) { visitReturnType(rt) }

    public open fun visitFnArgMut(arg: FnArg) { visitFnArg(arg) }

    public open fun visitReceiverMut(receiver: FnArg.Receiver) { visitReceiver(receiver) }

    public open fun visitPatTypeMut(patType: PatType) { visitPatType(patType) }

    public open fun visitPatIdentMut(patIdent: Pat.Ident) { visitPatIdent(patIdent) }

    public open fun visitTypePathMut(typePath: SynType.Path) { visitTypePath(typePath) }

    public open fun visitTypeReferenceMut(ty: SynType.Reference) { visitTypeReference(ty) }

    public open fun visitTypeArrayMut(ty: SynType.Array) { visitTypeArray(ty) }

    public open fun visitTypeGroupMut(ty: SynType.Group) { visitTypeGroup(ty) }

    public open fun visitTypeImplTraitMut(ty: SynType.ImplTrait) { visitTypeImplTrait(ty) }

    public open fun visitTypeInferMut(ty: SynType.Infer) { visitTypeInfer(ty) }

    public open fun visitTypeMacroMut(ty: SynType.Macro) { visitTypeMacro(ty) }

    public open fun visitTypeNeverMut(ty: SynType.Never) { visitTypeNever(ty) }

    public open fun visitTypePtrMut(ty: SynType.Ptr) { visitTypePtr(ty) }

    public open fun visitPointerMutabilityMut(mutability: PointerMutability) { visitPointerMutability(mutability) }

    public open fun visitTypeBareFnMut(ty: SynType.BareFn) { visitTypeBareFn(ty) }

    public open fun visitBareFnArgMut(arg: BareFnArg) { visitBareFnArg(arg) }

    public open fun visitBareVariadicMut(variadic: BareVariadic) { visitBareVariadic(variadic) }

    public open fun visitTypeParenMut(ty: SynType.Paren) { visitTypeParen(ty) }

    public open fun visitTypeSliceMut(ty: SynType.Slice) { visitTypeSlice(ty) }

    public open fun visitTypeTraitObjectMut(ty: SynType.TraitObject) { visitTypeTraitObject(ty) }

    public open fun visitTypeTupleMut(ty: SynType.Tuple) { visitTypeTuple(ty) }

    public open fun visitExprPathMut(exprPath: Expr.Path) { visitExprPath(exprPath) }

    public open fun visitMacroMut(mac: Macro) { visitMacro(mac) }

    public open fun visitPathArgumentsMut(pathArgs: PathArguments) { visitPathArguments(pathArgs) }

    public open fun visitAngleBracketedGenericArgumentsMut(pathArgs: PathArguments.AngleBracketed) { visitAngleBracketedGenericArguments(pathArgs) }

    public open fun visitParenthesizedGenericArgumentsMut(pathArgs: PathArguments.Parenthesized) { visitParenthesizedGenericArguments(pathArgs) }

    public open fun visitGenericArgumentMut(genArg: GenericArgument) { visitGenericArgument(genArg) }

    public open fun visitAssocTypeMut(assoc: AssocType) { visitAssocType(assoc) }

    public open fun visitAssocConstMut(assoc: AssocConst) { visitAssocConst(assoc) }

    public open fun visitConstraintMut(constraint: Constraint) { visitConstraint(constraint) }

    public open fun visitTypeParamBoundMut(bound: TypeParamBound) { visitTypeParamBound(bound) }

    public open fun visitTraitBoundMut(bound: TypeParamBound.Trait) { visitTraitBound(bound) }

    public open fun visitTraitBoundModifierMut(modifier: TraitBoundModifier) { visitTraitBoundModifier(modifier) }

    public open fun visitBinOpMut(op: BinOp) { visitBinOp(op) }

    public open fun visitBoundLifetimesMut(boundLifetimes: BoundLifetimes) { visitBoundLifetimes(boundLifetimes) }

    public open fun visitCapturedParamMut(param: CapturedParam) { visitCapturedParam(param) }

    public open fun visitPathSegmentMut(segment: PathSegment) { visitPathSegment(segment) }

    public open fun visitArmMut(arm: Arm) { visitArm(arm) }

    public open fun visitElseExprMut(elseExpr: ElseExpr) { visitElseExpr(elseExpr) }

    public open fun visitFieldPatMut(fieldPat: FieldPat) { visitFieldPat(fieldPat) }

    public open fun visitFieldValueMut(fieldValue: FieldValue) { visitFieldValue(fieldValue) }

    public open fun visitGenericParamMut(param: GenericParam) { visitGenericParam(param) }

    public open fun visitFieldMut(field: Field) { visitField(field) }

    public open fun visitFieldMutabilityMut(fieldMutability: FieldMutability) { visitFieldMutability(fieldMutability) }

    public open fun visitFieldsMut(fields: Fields) { visitFields(fields) }

    public open fun visitFieldsNamedMut(fields: FieldsNamed) { visitFieldsNamed(fields) }

    public open fun visitFieldsUnnamedMut(fields: FieldsUnnamed) { visitFieldsUnnamed(fields) }

    public open fun visitImplItemMut(item: ImplItem) { visitImplItem(item) }

    public open fun visitImplItemConstMut(item: ImplItem.Const) { visitImplItemConst(item) }

    public open fun visitImplItemFnMut(item: ImplItem.Fn) { visitImplItemFn(item) }

    public open fun visitImplItemMacroMut(item: ImplItem.Macro) { visitImplItemMacro(item) }

    public open fun visitImplItemTypeMut(item: ImplItem.AssocType) { visitImplItemType(item) }

    public open fun visitForeignItemMut(item: ForeignItem) { visitForeignItem(item) }

    public open fun visitForeignItemFnMut(item: ForeignItem.Fn) { visitForeignItemFn(item) }

    public open fun visitForeignItemMacroMut(item: ForeignItem.Macro) { visitForeignItemMacro(item) }

    public open fun visitForeignItemStaticMut(item: ForeignItem.Static) { visitForeignItemStatic(item) }

    public open fun visitForeignItemTypeMut(item: ForeignItem.ItemType) { visitForeignItemType(item) }

    public open fun visitImplRestrictionMut(restriction: ImplRestriction) { visitImplRestriction(restriction) }

    public open fun visitItemConstMut(item: Item.Const) { visitItemConst(item) }

    public open fun visitItemEnumMut(item: Item.Enum) { visitItemEnum(item) }

    public open fun visitItemExternCrateMut(item: Item.ExternCrate) { visitItemExternCrate(item) }

    public open fun visitItemFnMut(item: Item.Fn) { visitItemFn(item) }

    public open fun visitItemForeignModMut(item: Item.ForeignMod) { visitItemForeignMod(item) }

    public open fun visitItemImplMut(item: Item.Impl) { visitItemImpl(item) }

    public open fun visitItemMacroMut(item: Item.Macro) { visitItemMacro(item) }

    public open fun visitItemModMut(item: Item.Mod) { visitItemMod(item) }

    public open fun visitItemStaticMut(item: Item.Static) { visitItemStatic(item) }

    public open fun visitItemStructMut(item: Item.Struct) { visitItemStruct(item) }

    public open fun visitItemTraitMut(item: Item.Trait) { visitItemTrait(item) }

    public open fun visitItemTraitAliasMut(item: Item.TraitAlias) { visitItemTraitAlias(item) }

    public open fun visitItemTypeMut(item: Item.ItemType) { visitItemType(item) }

    public open fun visitItemUnionMut(item: Item.Union) { visitItemUnion(item) }

    public open fun visitItemUseMut(item: Item.Use) { visitItemUse(item) }

    public open fun visitStaticMutabilityMut(mutability: StaticMutability) { visitStaticMutability(mutability) }

    public open fun visitModContentMut(modContent: ModContent) { visitModContent(modContent) }

    public open fun visitLocalInitMut(init: LocalInit) { visitLocalInit(init) }

    public open fun visitMemberMut(member: Member) { visitMember(member) }

    public open fun visitQSelfMut(qself: QSelf) { visitQSelf(qself) }

    public open fun visitPathTraitMut(pathTrait: PathTrait) { visitPathTrait(pathTrait) }

    public open fun visitTraitItemMut(item: TraitItem) { visitTraitItem(item) }

    public open fun visitTraitItemConstMut(item: TraitItem.Const) { visitTraitItemConst(item) }

    public open fun visitTraitItemFnMut(item: TraitItem.Fn) { visitTraitItemFn(item) }

    public open fun visitTraitItemMacroMut(item: TraitItem.Macro) { visitTraitItemMacro(item) }

    public open fun visitTraitItemTypeMut(item: TraitItem.AssocType) { visitTraitItemType(item) }

    public open fun visitUseTreeMut(useTree: UseTree) { visitUseTree(useTree) }

    public open fun visitUseGlobMut(useTree: UseTree.Glob) { visitUseGlob(useTree) }

    public open fun visitUseGroupMut(useTree: UseTree.Group) { visitUseGroup(useTree) }

    public open fun visitUseNameMut(useTree: UseTree.Name) { visitUseName(useTree) }

    public open fun visitUsePathMut(useTree: UseTree.Path) { visitUsePath(useTree) }

    public open fun visitUseRenameMut(useTree: UseTree.Name) { visitUseRename(useTree) }

    public open fun visitVariadicMut(variadic: Variadic) { visitVariadic(variadic) }

    public open fun visitVariantMut(variant: Variant) { visitVariant(variant) }

    public open fun visitVisibilityMut(visibility: Visibility) { visitVisibility(visibility) }

    public open fun visitWhereClauseMut(whereClause: WhereClause) { visitWhereClause(whereClause) }

    public open fun visitWherePredicateMut(wherePredicate: WherePredicate) { visitWherePredicate(wherePredicate) }
}
