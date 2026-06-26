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
    public open fun visitExpr(e: Expr): Expr =
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
            is Expr.Verbatim -> {
                visitTokenStreamMut(e.tokens)
                e
            }
        }

    public open fun visitExprArrayMut(e: Expr.Array): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.elems.mapValuesInPlace(::visitExprMut)
        return e
    }

    public open fun visitExprAssignMut(e: Expr.Assign): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.left = visitExprMut(e.left)
        e.right = visitExprMut(e.right)
        return e
    }

    public open fun visitExprAsyncMut(e: Expr.Async): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.block = visitBlockMut(e.block)
        return e
    }

    public open fun visitExprAwaitMut(e: Expr.Await): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.base = visitExprMut(e.base)
        return e
    }

    public open fun visitExprBinaryMut(e: Expr.Binary): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.left = visitExprMut(e.left)
        e.op = visitBinOpMut(e.op)
        e.right = visitExprMut(e.right)
        return e
    }

    public open fun visitExprBlockMut(e: Expr.BlockExpr): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.label = e.label?.let { visitLabelMut(it) }
        e.block = visitBlockMut(e.block)
        return e
    }

    public open fun visitExprBreakMut(e: Expr.Break): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.label = e.label?.let { visitLifetimeMut(it) }
        e.expr = e.expr?.let { visitExprMut(it) }
        return e
    }

    public open fun visitExprCallMut(e: Expr.Call): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.func = visitExprMut(e.func)
        e.args.mapValuesInPlace(::visitExprMut)
        return e
    }

    public open fun visitExprCastMut(e: Expr.Cast): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        e.ty = visitTypeMut(e.ty)
        return e
    }

    public open fun visitExprClosureMut(e: Expr.Closure): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.inputs.mapValuesInPlace(::visitPatMut)
        e.output = visitReturnTypeMut(e.output)
        e.body = visitExprMut(e.body)
        return e
    }

    public open fun visitExprConstMut(e: Expr.Const): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.block = visitBlockMut(e.block)
        return e
    }

    public open fun visitExprContinueMut(e: Expr.Continue): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.label = e.label?.let { visitLifetimeMut(it) }
        return e
    }

    public open fun visitExprFieldMut(e: Expr.Field): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.base = visitExprMut(e.base)
        e.member = visitMemberMut(e.member)
        return e
    }

    public open fun visitExprForLoopMut(e: Expr.ForLoop): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.label = e.label?.let { visitLabelMut(it) }
        e.pat = visitPatMut(e.pat)
        e.expr = visitExprMut(e.expr)
        e.body = visitBlockMut(e.body)
        return e
    }

    public open fun visitExprGroupMut(e: Expr.Group): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        return e
    }

    public open fun visitExprIfMut(e: Expr.If): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.cond = visitExprMut(e.cond)
        e.thenBranch = visitBlockMut(e.thenBranch)
        e.elseBranch = e.elseBranch?.let { visitElseExprMut(it) }
        return e
    }

    public open fun visitExprIndexMut(e: Expr.Index): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        e.index = visitExprMut(e.index)
        return e
    }

    public open fun visitExprInferMut(e: Expr.Infer): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        return e
    }

    public open fun visitExprLetMut(e: Expr.Let): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.pat = visitPatMut(e.pat)
        e.expr = visitExprMut(e.expr)
        return e
    }

    public open fun visitExprLitMut(e: Expr.Lit): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.lit = visitLitMut(e.lit)
        return e
    }

    public open fun visitExprLoopMut(e: Expr.Loop): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.label = e.label?.let { visitLabelMut(it) }
        e.body = visitBlockMut(e.body)
        return e
    }

    public open fun visitExprMacroMut(e: Expr.Macro): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.mac = visitMacroMut(e.mac)
        return e
    }

    public open fun visitExprMatchMut(e: Expr.Match): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        for (i in e.arms.indices) e.arms[i] = visitArmMut(e.arms[i])
        return e
    }

    public open fun visitExprMethodCallMut(e: Expr.MethodCall): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.receiver = visitExprMut(e.receiver)
        e.method = visitIdentMut(e.method)
        e.turbofish = e.turbofish?.let { visitAngleBracketedGenericArgumentsMut(it) }
        e.args.mapValuesInPlace(::visitExprMut)
        return e
    }

    public open fun visitExprParenMut(e: Expr.Paren): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        return e
    }

    public open fun visitExprRangeMut(e: Expr.Range): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.start = e.start?.let { visitExprMut(it) }
        e.limits = visitRangeLimitsMut(e.limits)
        e.end = e.end?.let { visitExprMut(it) }
        return e
    }

    public open fun visitExprRawAddrMut(e: Expr.RawAddr): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.mutability = visitPointerMutabilityMut(e.mutability)
        e.expr = visitExprMut(e.expr)
        return e
    }

    public open fun visitExprReferenceMut(e: Expr.Reference): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        return e
    }

    public open fun visitExprRepeatMut(e: Expr.Repeat): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        e.len = visitExprMut(e.len)
        return e
    }

    public open fun visitExprReturnMut(e: Expr.Return): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = e.expr?.let { visitExprMut(it) }
        return e
    }

    public open fun visitExprStructMut(e: Expr.Struct): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.qself = e.qself?.let { visitQSelfMut(it) }
        e.path = visitPathMut(e.path)
        e.fields.mapValuesInPlace(::visitFieldValueMut)
        e.rest = e.rest?.let { visitExprMut(it) }
        return e
    }

    public open fun visitExprTryMut(e: Expr.Try): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = visitExprMut(e.expr)
        return e
    }

    public open fun visitExprTryBlockMut(e: Expr.TryBlock): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.block = visitBlockMut(e.block)
        return e
    }

    public open fun visitExprTupleMut(e: Expr.Tuple): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.elems.mapValuesInPlace(::visitExprMut)
        return e
    }

    public open fun visitExprUnaryMut(e: Expr.Unary): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.op = visitUnOpMut(e.op)
        e.expr = visitExprMut(e.expr)
        return e
    }

    public open fun visitExprUnsafeMut(e: Expr.Unsafe): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.block = visitBlockMut(e.block)
        return e
    }

    public open fun visitExprWhileMut(e: Expr.While): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.label = e.label?.let { visitLabelMut(it) }
        e.cond = visitExprMut(e.cond)
        e.body = visitBlockMut(e.body)
        return e
    }

    public open fun visitExprYieldMut(e: Expr.Yield): Expr {
        e.attrs = visitAttributesMut(e.attrs)
        e.expr = e.expr?.let { visitExprMut(it) }
        return e
    }

    public open fun visitType(t: SynType): SynType =
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
                t
            }
        }

    public open fun visitPath(p: Path): Path {
        p.segments.mapValuesInPlace(::visitPathSegmentMut)
        return p
    }

    public open fun visitPat(p: Pat): Pat =
        when (p) {
            is Pat.Const -> {
                p.attrs = visitAttributesMut(p.attrs)
                p.block = visitBlockMut(p.block)
                p
            }
            is Pat.Ident -> visitPatIdentMut(p)
            is Pat.Lit -> {
                p.attrs = visitAttributesMut(p.attrs)
                p.lit = visitLitMut(p.lit)
                p
            }
            is Pat.Macro -> {
                p.attrs = visitAttributesMut(p.attrs)
                p.mac = visitMacroMut(p.mac)
                p
            }
            is Pat.Or -> visitPatOrMut(p)
            is Pat.PatParen -> visitPatParenMut(p)
            is Pat.Path -> {
                p.attrs = visitAttributesMut(p.attrs)
                p.qself = p.qself?.let { visitQSelfMut(it) }
                p.path = visitPathMut(p.path)
                p
            }
            is Pat.Range -> {
                p.attrs = visitAttributesMut(p.attrs)
                p.start = p.start?.let { visitExprMut(it) }
                p.limits = visitRangeLimitsMut(p.limits)
                p.end = p.end?.let { visitExprMut(it) }
                p
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
                p
            }
        }

    public open fun visitItem(i: Item): Item =
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
                i
            }
        }

    public open fun visitFile(f: File): File {
        f.attrs = visitAttributesMut(f.attrs)
        for (i in f.items.indices) f.items[i] = visitItemMut(f.items[i])
        return f
    }

    public open fun visitAttribute(a: Attribute): Attribute {
        a.style = visitAttrStyleMut(a.style)
        a.meta = visitMetaMut(a.meta)
        return a
    }

    public open fun visitAttrStyle(style: AttrStyle): AttrStyle = style

    public open fun visitMeta(m: Meta): Meta =
        when (m) {
            is Meta.PathMeta -> {
                m.path = visitPathMut(m.path)
                m
            }
            is Meta.List -> visitMetaListMut(m)
            is Meta.NameValue -> visitMetaNameValueMut(m)
        }

    public open fun visitMetaList(m: Meta.List): Meta {
        visitMacroDelimiterMut(m.delimiter)
        visitTokenStreamMut(m.tokens)
        m.path = visitPathMut(m.path)
        return m
    }

    public open fun visitMetaNameValue(m: Meta.NameValue): Meta {
        m.path = visitPathMut(m.path)
        m.value = visitExprMut(m.value)
        return m
    }

    public open fun visitGenerics(g: Generics): Generics {
        g.params.mapValuesInPlace(::visitGenericParamMut)
        g.whereClause = g.whereClause?.let { visitWhereClauseMut(it) }
        return g
    }

    public open fun visitLit(l: Lit): Lit =
        when (l) {
            is Lit.Str -> {
                l.value = visitLitStrMut(l.value)
                l
            }
            is Lit.ByteStr -> {
                l.value = visitLitByteStrMut(l.value)
                l
            }
            is Lit.CStr -> {
                l.value = visitLitCStrMut(l.value)
                l
            }
            is Lit.Byte -> {
                l.value = visitLitByteMut(l.value)
                l
            }
            is Lit.Char -> {
                l.value = visitLitCharMut(l.value)
                l
            }
            is Lit.Int -> {
                l.value = visitLitIntMut(l.value)
                l
            }
            is Lit.Float -> {
                l.value = visitLitFloatMut(l.value)
                l
            }
            is Lit.Bool -> {
                l.value = visitLitBoolMut(l.value)
                l
            }
            is Lit.Verbatim -> l
        }

    public open fun visitLitBoolMut(l: LitBool): LitBool {
        visitSpanMut(l.span())
        return l
    }

    public open fun visitLitByteMut(l: LitByte): LitByte = l

    public open fun visitLitByteStrMut(l: LitByteStr): LitByteStr = l

    public open fun visitLitCStrMut(l: LitCStr): LitCStr = l

    public open fun visitLitCstrMut(l: LitCStr): LitCStr = visitLitCStrMut(l)

    public open fun visitLitCharMut(l: LitChar): LitChar = l

    public open fun visitLitFloatMut(l: LitFloat): LitFloat = l

    public open fun visitLitIntMut(l: LitInt): LitInt = l

    public open fun visitLitStrMut(l: LitStr): LitStr = l

    public open fun visitLifetime(lt: Lifetime): Lifetime {
        visitSpanMut(lt.apostrophe)
        visitIdentMut(lt.ident)
        return lt
    }

    public open fun visitIdent(id: Ident): Ident {
        visitSpanMut(id.span())
        return id
    }

    public open fun visitStmt(s: Stmt): Stmt =
        when (s) {
            is Stmt.Local -> visitLocalMut(s)
            is Stmt.ItemStmt -> {
                s.item = visitItemMut(s.item)
                s
            }
            is Stmt.ExprStmt -> {
                s.expr = visitExprMut(s.expr)
                s
            }
            is Stmt.MacroStmt -> visitStmtMacroMut(s)
        }

    public open fun visitData(d: Data): Data =
        when (d) {
            is Data.Struct -> {
                d.value = visitDataStructMut(d.value)
                d
            }
            is Data.Enum -> {
                d.value = visitDataEnumMut(d.value)
                d
            }
            is Data.Union -> {
                d.value = visitDataUnionMut(d.value)
                d
            }
        }

    public open fun visitDataEnum(d: DataEnum): DataEnum {
        d.variants.mapValuesInPlace(::visitVariantMut)
        return d
    }

    public open fun visitDataStruct(d: DataStruct): DataStruct {
        d.fields = visitFieldsMut(d.fields)
        return d
    }

    public open fun visitDataUnion(d: DataUnion): DataUnion {
        d.fields = visitFieldsNamedMut(d.fields)
        return d
    }

    public open fun visitLabelMut(label: Label): Label {
        label.name = visitLifetimeMut(label.name)
        return label
    }

    public open fun visitDeriveInput(di: DeriveInput): DeriveInput {
        di.attrs = visitAttributesMut(di.attrs)
        di.vis = visitVisibilityMut(di.vis)
        di.ident = visitIdentMut(di.ident)
        di.generics = visitGenericsMut(di.generics)
        di.data = visitDataMut(di.data)
        return di
    }

    public open fun visitBlock(block: Block): Block {
        for (i in block.stmts.indices) block.stmts[i] = visitStmtMut(block.stmts[i])
        return block
    }

    public open fun visitAttributes(attrs: MutableList<Attribute>): MutableList<Attribute> {
        for (i in attrs.indices) attrs[i] = visitAttributeMut(attrs[i])
        return attrs
    }

    public open fun visitSignature(sig: Signature): Signature {
        sig.abi = sig.abi?.let { visitAbiMut(it) }
        sig.ident = visitIdentMut(sig.ident)
        sig.generics = visitGenericsMut(sig.generics)
        sig.inputs.mapValuesInPlace(::visitFnArgMut)
        sig.variadic = sig.variadic?.let { visitVariadicMut(it) }
        sig.output = visitReturnTypeMut(sig.output)
        return sig
    }

    public open fun visitAbi(a: Abi): Abi = a

    public open fun visitReturnType(rt: ReturnType): ReturnType =
        when (rt) {
            is ReturnType.Default -> rt
            is ReturnType.TypeReturn -> {
                rt.ty = visitTypeMut(rt.ty)
                rt
            }
        }

    public open fun visitFnArg(arg: FnArg): FnArg =
        when (arg) {
            is FnArg.Receiver -> visitReceiverMut(arg)
            is FnArg.Typed -> {
                arg.patType = visitPatTypeMut(arg.patType)
                arg
            }
        }

    public open fun visitForeignItem(item: ForeignItem): ForeignItem =
        when (item) {
            is ForeignItem.Fn -> visitForeignItemFnMut(item)
            is ForeignItem.Static -> visitForeignItemStaticMut(item)
            is ForeignItem.ItemType -> visitForeignItemTypeMut(item)
            is ForeignItem.Macro -> visitForeignItemMacroMut(item)
            is ForeignItem.Verbatim -> {
                visitTokenStreamMut(item.tokens)
                item
            }
        }

    public open fun visitForeignItemFn(item: ForeignItem.Fn): ForeignItem.Fn {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.sig = visitSignatureMut(item.sig)
        return item
    }

    public open fun visitForeignItemMacro(item: ForeignItem.Macro): ForeignItem.Macro {
        item.attrs = visitAttributesMut(item.attrs)
        item.mac = visitMacroMut(item.mac)
        return item
    }

    public open fun visitForeignItemStatic(item: ForeignItem.Static): ForeignItem.Static {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.mutability = visitStaticMutabilityMut(item.mutability)
        item.ident = visitIdentMut(item.ident)
        item.ty = visitTypeMut(item.ty)
        return item
    }

    public open fun visitForeignItemType(item: ForeignItem.ItemType): ForeignItem.ItemType {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        return item
    }

    public open fun visitReceiver(receiver: FnArg.Receiver): FnArg.Receiver {
        receiver.attrs = visitAttributesMut(receiver.attrs)
        receiver.reference?.let { it.lifetime = it.lifetime?.let { lt -> visitLifetimeMut(lt) } }
        receiver.type = visitTypeMut(receiver.type)
        return receiver
    }

    public open fun visitPatType(patType: PatType): PatType {
        patType.pat = visitPatMut(patType.pat)
        patType.ty = visitTypeMut(patType.ty)
        return patType
    }

    public open fun visitPatIdent(patIdent: Pat.Ident): Pat {
        patIdent.attrs = visitAttributesMut(patIdent.attrs)
        patIdent.ident = visitIdentMut(patIdent.ident)
        patIdent.subpat = patIdent.subpat?.let { visitPatMut(it) }
        return patIdent
    }

    public open fun visitPatOrMut(pat: Pat.Or): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.cases.mapValuesInPlace(::visitPatMut)
        return pat
    }

    public open fun visitPatParenMut(pat: Pat.PatParen): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.pat = visitPatMut(pat.pat)
        return pat
    }

    public open fun visitPatReferenceMut(pat: Pat.Reference): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.pat = visitPatMut(pat.pat)
        return pat
    }

    public open fun visitPatRestMut(pat: Pat.Rest): Pat.Rest {
        pat.attrs = visitAttributesMut(pat.attrs)
        return pat
    }

    public open fun visitPatRestMut(rest: PatRest): PatRest {
        rest.attrs = visitAttributesMut(rest.attrs)
        return rest
    }

    public open fun visitPatSliceMut(pat: Pat.Slice): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.elems.mapValuesInPlace(::visitPatMut)
        return pat
    }

    public open fun visitPatStructMut(pat: Pat.Struct): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.qself = pat.qself?.let { visitQSelfMut(it) }
        pat.path = visitPathMut(pat.path)
        pat.fields.mapValuesInPlace(::visitFieldPatMut)
        pat.rest = pat.rest?.let { visitPatRestMut(it) }
        return pat
    }

    public open fun visitPatTupleMut(pat: Pat.Tuple): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.elems.mapValuesInPlace(::visitPatMut)
        return pat
    }

    public open fun visitPatTupleStructMut(pat: Pat.TupleStruct): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.qself = pat.qself?.let { visitQSelfMut(it) }
        pat.path = visitPathMut(pat.path)
        pat.elems.mapValuesInPlace(::visitPatMut)
        return pat
    }

    public open fun visitPatWildMut(pat: Pat.Wild): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        return pat
    }

    public open fun visitPatTypeMut(pat: Pat.TypeAscription): Pat {
        pat.attrs = visitAttributesMut(pat.attrs)
        pat.pat = visitPatMut(pat.pat)
        pat.ty = visitTypeMut(pat.ty)
        return pat
    }

    public open fun visitTypePath(typePath: SynType.Path): SynType {
        typePath.qself = typePath.qself?.let { visitQSelfMut(it) }
        typePath.path = visitPathMut(typePath.path)
        return typePath
    }

    public open fun visitTypeReference(ty: SynType.Reference): SynType {
        ty.lifetime = ty.lifetime?.let { visitLifetimeMut(it) }
        ty.elem = visitTypeMut(ty.elem)
        return ty
    }

    public open fun visitTypeArray(ty: SynType.Array): SynType {
        ty.elem = visitTypeMut(ty.elem)
        ty.len = visitExprMut(ty.len)
        return ty
    }

    public open fun visitTypeGroup(ty: SynType.Group): SynType {
        ty.elem = visitTypeMut(ty.elem)
        return ty
    }

    public open fun visitTypeImplTrait(ty: SynType.ImplTrait): SynType {
        ty.bounds.mapValuesInPlace(::visitTypeParamBoundMut)
        return ty
    }

    public open fun visitTypeInfer(ty: SynType.Infer): SynType = ty

    public open fun visitTypeMacro(ty: SynType.Macro): SynType {
        ty.mac = visitMacroMut(ty.mac)
        return ty
    }

    public open fun visitTypeNever(ty: SynType.Never): SynType = ty

    public open fun visitTypePtr(ty: SynType.Ptr): SynType {
        ty.mutability = visitPointerMutabilityMut(ty.mutability)
        ty.elem = visitTypeMut(ty.elem)
        return ty
    }

    public open fun visitPointerMutability(mutability: PointerMutability): PointerMutability = mutability

    public open fun visitPointerMutabilityMut(mutability: io.github.kotlinmania.syn.token.Mut?): io.github.kotlinmania.syn.token.Mut? = mutability

    public open fun visitTypeBareFn(ty: SynType.BareFn): SynType {
        ty.lifetimes = ty.lifetimes?.let { visitBoundLifetimesMut(it) }
        ty.abi = ty.abi?.let { visitAbiMut(it) }
        ty.inputs.mapValuesInPlace(::visitBareFnArgMut)
        ty.variadic = ty.variadic?.let { visitBareVariadicMut(it) }
        ty.output = visitReturnTypeMut(ty.output)
        return ty
    }

    public open fun visitBareFnArg(arg: BareFnArg): BareFnArg {
        arg.attrs = visitAttributesMut(arg.attrs)
        arg.name?.let { it.ident = visitIdentMut(it.ident) }
        arg.ty = visitTypeMut(arg.ty)
        return arg
    }

    public open fun visitBareVariadic(variadic: BareVariadic): BareVariadic {
        variadic.attrs = visitAttributesMut(variadic.attrs)
        variadic.name?.let { it.ident = visitIdentMut(it.ident) }
        return variadic
    }

    public open fun visitTypeParen(ty: SynType.Paren): SynType {
        ty.elem = visitTypeMut(ty.elem)
        return ty
    }

    public open fun visitTypeSlice(ty: SynType.Slice): SynType {
        ty.elem = visitTypeMut(ty.elem)
        return ty
    }

    public open fun visitTypeTraitObject(ty: SynType.TraitObject): SynType {
        ty.bounds.mapValuesInPlace(::visitTypeParamBoundMut)
        return ty
    }

    public open fun visitTypeTuple(ty: SynType.Tuple): SynType {
        ty.elems.mapValuesInPlace(::visitTypeMut)
        return ty
    }

    public open fun visitExprPath(exprPath: Expr.Path): Expr {
        exprPath.attrs = visitAttributesMut(exprPath.attrs)
        exprPath.qself = exprPath.qself?.let { visitQSelfMut(it) }
        exprPath.path = visitPathMut(exprPath.path)
        return exprPath
    }

    public open fun visitMacro(mac: Macro): Macro {
        mac.path = visitPathMut(mac.path)
        mac.delimiter = visitMacroDelimiterMut(mac.delimiter)
        mac.tokens = visitTokenStreamMut(mac.tokens)
        return mac
    }

    public open fun visitPathArguments(pathArgs: PathArguments): PathArguments =
        when (pathArgs) {
            is PathArguments.None -> pathArgs
            is PathArguments.AngleBracketed -> visitAngleBracketedGenericArgumentsMut(pathArgs)
            is PathArguments.Parenthesized -> visitParenthesizedGenericArgumentsMut(pathArgs)
        }

    public open fun visitAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed {
        pathArgs.args.mapValuesInPlace(::visitGenericArgumentMut)
        return pathArgs
    }

    public open fun visitParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized {
        pathArgs.inputs.mapValuesInPlace(::visitTypeMut)
        pathArgs.output = visitReturnTypeMut(pathArgs.output)
        return pathArgs
    }

    public open fun visitGenericArgument(genArg: GenericArgument): GenericArgument =
        when (genArg) {
            is GenericArgument.LifetimeArg -> {
                genArg.lifetime = visitLifetimeMut(genArg.lifetime)
                genArg
            }
            is GenericArgument.TypeArg -> {
                genArg.type = visitTypeMut(genArg.type)
                genArg
            }
            is GenericArgument.ConstArg -> {
                genArg.expr = visitExprMut(genArg.expr)
                genArg
            }
            is GenericArgument.AssocTypeArg -> {
                genArg.assoc = visitAssocTypeMut(genArg.assoc)
                genArg
            }
            is GenericArgument.AssocConstArg -> {
                genArg.assoc = visitAssocConstMut(genArg.assoc)
                genArg
            }
            is GenericArgument.ConstraintArg -> {
                genArg.constraint = visitConstraintMut(genArg.constraint)
                genArg
            }
        }

    public open fun visitAssocType(assoc: AssocType): AssocType {
        assoc.ident = visitIdentMut(assoc.ident)
        assoc.generics = assoc.generics?.let { visitAngleBracketedGenericArgumentsMut(it) }
        assoc.ty = visitTypeMut(assoc.ty)
        return assoc
    }

    public open fun visitAssocConst(assoc: AssocConst): AssocConst {
        assoc.ident = visitIdentMut(assoc.ident)
        assoc.generics = assoc.generics?.let { visitAngleBracketedGenericArgumentsMut(it) }
        assoc.value = visitExprMut(assoc.value)
        return assoc
    }

    public open fun visitConstraint(constraint: Constraint): Constraint {
        constraint.ident = visitIdentMut(constraint.ident)
        constraint.generics = constraint.generics?.let { visitAngleBracketedGenericArgumentsMut(it) }
        constraint.bounds.mapValuesInPlace(::visitTypeParamBoundMut)
        return constraint
    }

    public open fun visitTypeParamBound(bound: TypeParamBound): TypeParamBound =
        when (bound) {
            is TypeParamBound.Trait -> visitTraitBoundMut(bound)
            is TypeParamBound.LifetimeBound -> {
                bound.lifetime = visitLifetimeMut(bound.lifetime)
                bound
            }
            is TypeParamBound.PreciseCapture -> visitPreciseCaptureMut(bound)
            is TypeParamBound.Verbatim -> {
                visitTokenStreamMut(bound.tokens)
                bound
            }
        }

    public open fun visitTraitBound(bound: TypeParamBound.Trait): TypeParamBound {
        bound.modifier = visitTraitBoundModifierMut(bound.modifier)
        bound.lifetimes = bound.lifetimes?.let { visitBoundLifetimesMut(it) }
        bound.path = visitPathMut(bound.path)
        return bound
    }

    public open fun visitTraitBoundModifier(modifier: TraitBoundModifier): TraitBoundModifier = modifier

    public open fun visitBinOp(op: BinOp): BinOp = op

    public open fun visitUnOpMut(op: UnOp): UnOp = op

    public open fun visitBoundLifetimes(boundLifetimes: BoundLifetimes): BoundLifetimes {
        boundLifetimes.lifetimes.mapValuesInPlace(::visitGenericParamMut)
        return boundLifetimes
    }

    public open fun visitCapturedParam(param: CapturedParam): CapturedParam =
        when (param) {
            is CapturedParam.Lifetime -> {
                param.lifetime = visitLifetimeMut(param.lifetime)
                param
            }
            is CapturedParam.Ident -> {
                param.ident = visitIdentMut(param.ident)
                param
            }
        }

    public open fun visitPreciseCaptureMut(param: TypeParamBound.PreciseCapture): TypeParamBound {
        param.params.mapValuesInPlace(::visitCapturedParamMut)
        return param
    }

    public open fun visitPathSegment(segment: PathSegment): PathSegment {
        segment.ident = visitIdentMut(segment.ident)
        segment.arguments = visitPathArgumentsMut(segment.arguments)
        return segment
    }

    public open fun visitArm(arm: Arm): Arm {
        arm.attrs = visitAttributesMut(arm.attrs)
        arm.pat = visitPatMut(arm.pat)
        arm.guard?.let { it.expr = visitExprMut(it.expr) }
        arm.body = visitExprMut(arm.body)
        return arm
    }

    public open fun visitElseExpr(elseExpr: ElseExpr): ElseExpr {
        elseExpr.expr = visitExprMut(elseExpr.expr)
        return elseExpr
    }

    public open fun visitFieldPat(fieldPat: FieldPat): FieldPat {
        fieldPat.attrs = visitAttributesMut(fieldPat.attrs)
        fieldPat.member = visitMemberMut(fieldPat.member)
        fieldPat.pat = visitPatMut(fieldPat.pat)
        return fieldPat
    }

    public open fun visitFieldValue(fieldValue: FieldValue): FieldValue {
        fieldValue.attrs = visitAttributesMut(fieldValue.attrs)
        fieldValue.member = visitMemberMut(fieldValue.member)
        fieldValue.expr = visitExprMut(fieldValue.expr)
        return fieldValue
    }

    public open fun visitGenericParam(param: GenericParam): GenericParam =
        when (param) {
            is GenericParam.LifetimeParam -> visitLifetimeParamMut(param)
            is GenericParam.TypeParam -> visitTypeParamMut(param)
            is GenericParam.ConstParam -> visitConstParamMut(param)
        }

    public open fun visitLifetimeParamMut(param: GenericParam.LifetimeParam): GenericParam.LifetimeParam {
        param.attrs = visitAttributesMut(param.attrs)
        param.lifetime = visitLifetimeMut(param.lifetime)
        param.bounds.mapValuesInPlace(::visitLifetimeMut)
        return param
    }

    public open fun visitTypeParamMut(param: GenericParam.TypeParam): GenericParam.TypeParam {
        param.attrs = visitAttributesMut(param.attrs)
        param.ident = visitIdentMut(param.ident)
        param.bounds.mapValuesInPlace(::visitTypeParamBoundMut)
        param.default = param.default?.let { visitTypeMut(it) }
        return param
    }

    public open fun visitConstParamMut(param: GenericParam.ConstParam): GenericParam.ConstParam {
        param.attrs = visitAttributesMut(param.attrs)
        param.ident = visitIdentMut(param.ident)
        param.ty = visitTypeMut(param.ty)
        param.default = param.default?.let { visitExprMut(it) }
        return param
    }

    public open fun visitField(field: Field): Field {
        field.attrs = visitAttributesMut(field.attrs)
        field.vis = visitVisibilityMut(field.vis)
        field.mutability = visitFieldMutabilityMut(field.mutability)
        field.ident = field.ident?.let { visitIdentMut(it) }
        field.ty = visitTypeMut(field.ty)
        return field
    }

    public open fun visitFieldMutability(fieldMutability: FieldMutability): FieldMutability = fieldMutability

    public open fun visitFields(fields: Fields): Fields =
        when (fields) {
            is Fields.Named -> {
                fields.fields = visitFieldsNamedMut(fields.fields)
                fields
            }
            is Fields.Unnamed -> {
                fields.fields = visitFieldsUnnamedMut(fields.fields)
                fields
            }
            Fields.Unit -> fields
        }

    public open fun visitFieldsNamed(fields: FieldsNamed): FieldsNamed {
        fields.named.mapValuesInPlace(::visitFieldMut)
        return fields
    }

    public open fun visitFieldsUnnamed(fields: FieldsUnnamed): FieldsUnnamed {
        fields.unnamed.mapValuesInPlace(::visitFieldMut)
        return fields
    }

    public open fun visitImplItem(item: ImplItem): ImplItem =
        when (item) {
            is ImplItem.Const -> visitImplItemConstMut(item)
            is ImplItem.Fn -> visitImplItemFnMut(item)
            is ImplItem.AssocType -> visitImplItemTypeMut(item)
            is ImplItem.Macro -> visitImplItemMacroMut(item)
            is ImplItem.Verbatim -> {
                visitTokenStreamMut(item.tokens)
                item
            }
        }

    public open fun visitImplItemConst(item: ImplItem.Const): ImplItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.ty = visitTypeMut(item.ty)
        item.expr = visitExprMut(item.expr)
        return item
    }

    public open fun visitImplItemFn(item: ImplItem.Fn): ImplItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.sig = visitSignatureMut(item.sig)
        item.block = visitBlockMut(item.block)
        return item
    }

    public open fun visitImplItemMacro(item: ImplItem.Macro): ImplItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.mac = visitMacroMut(item.mac)
        return item
    }

    public open fun visitImplItemType(item: ImplItem.AssocType): ImplItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.ty = visitTypeMut(item.ty)
        return item
    }

    public open fun visitImplRestriction(restriction: ImplRestriction): ImplRestriction = restriction

    public open fun visitItemConst(item: Item.Const): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.ty = visitTypeMut(item.ty)
        item.expr = item.expr?.let { visitExprMut(it) }
        return item
    }

    public open fun visitItemEnum(item: Item.Enum): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.variants.mapValuesInPlace(::visitVariantMut)
        return item
    }

    public open fun visitItemExternCrate(item: Item.ExternCrate): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.rename?.let { it.ident = visitIdentMut(it.ident) }
        return item
    }

    public open fun visitItemFn(item: Item.Fn): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.sig = visitSignatureMut(item.sig)
        item.block = item.block?.let { visitBlockMut(it) }
        return item
    }

    public open fun visitItemForeignMod(item: Item.ForeignMod): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.abi = visitAbiMut(item.abi)
        for (i in item.items.indices) item.items[i] = visitForeignItemMut(item.items[i])
        return item
    }

    public open fun visitItemImpl(item: Item.Impl): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.generics = visitGenericsMut(item.generics)
        item.traitPath = item.traitPath?.let { visitPathTraitMut(it) }
        item.selfType = visitTypeMut(item.selfType)
        for (i in item.items.indices) item.items[i] = visitImplItemMut(item.items[i])
        return item
    }

    public open fun visitItemMacro(item: Item.Macro): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.ident = item.ident?.let { visitIdentMut(it) }
        item.mac = visitMacroMut(item.mac)
        return item
    }

    public open fun visitItemMod(item: Item.Mod): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.content = item.content?.let { visitModContentMut(it) }
        return item
    }

    public open fun visitItemStatic(item: Item.Static): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.mutability = visitStaticMutabilityMut(item.mutability)
        item.ident = visitIdentMut(item.ident)
        item.ty = visitTypeMut(item.ty)
        item.expr = visitExprMut(item.expr)
        return item
    }

    public open fun visitItemStruct(item: Item.Struct): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.fields = visitFieldsMut(item.fields)
        return item
    }

    public open fun visitItemTrait(item: Item.Trait): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.restriction = item.restriction?.let { visitImplRestrictionMut(it) }
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.supertraits.mapValuesInPlace(::visitTypeParamBoundMut)
        for (i in item.items.indices) item.items[i] = visitTraitItemMut(item.items[i])
        return item
    }

    public open fun visitItemTraitAlias(item: Item.TraitAlias): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.bounds.mapValuesInPlace(::visitTypeParamBoundMut)
        return item
    }

    public open fun visitItemType(item: Item.ItemType): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.ty = visitTypeMut(item.ty)
        return item
    }

    public open fun visitItemUnion(item: Item.Union): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.fields = visitFieldsNamedMut(item.fields)
        return item
    }

    public open fun visitItemUse(item: Item.Use): Item {
        item.attrs = visitAttributesMut(item.attrs)
        item.vis = visitVisibilityMut(item.vis)
        item.tree = visitUseTreeMut(item.tree)
        return item
    }

    public open fun visitStaticMutability(mutability: StaticMutability): StaticMutability = mutability

    public open fun visitRangeLimitsMut(limits: RangeLimits): RangeLimits = limits

    public open fun visitMacroDelimiterMut(delimiter: MacroDelimiter): MacroDelimiter = delimiter

    public open fun visitModContent(modContent: ModContent): ModContent =
        when (modContent) {
            is ModContent.Inline -> {
                modContent.items = modContent.items.map { visitItemMut(it) }
                modContent
            }
            is ModContent.Unnamed -> modContent
        }

    public open fun visitLocalMut(local: Stmt.Local): Stmt.Local {
        local.attrs = visitAttributesMut(local.attrs)
        local.pat = visitPatMut(local.pat)
        local.init = local.init?.let { visitLocalInitMut(it) }
        return local
    }

    public open fun visitLocalInit(init: LocalInit): LocalInit {
        init.expr = visitExprMut(init.expr)
        init.diverge = init.diverge?.let { visitElseExprMut(it) }
        return init
    }

    public open fun visitMember(member: Member): Member =
        when (member) {
            is Member.Named -> {
                member.ident = visitIdentMut(member.ident)
                member
            }
            is Member.Unnamed -> {
                member.index = visitIndexMut(member.index)
                member
            }
        }

    public open fun visitIndexMut(index: Index): Index {
        visitSpanMut(index.span)
        return index
    }

    public open fun visitQSelf(qself: QSelf): QSelf {
        qself.ty = visitTypeMut(qself.ty)
        return qself
    }

    public open fun visitQselfMut(qself: QSelf): QSelf = visitQSelfMut(qself)

    public open fun visitPathTrait(pathTrait: PathTrait): PathTrait {
        pathTrait.path = visitPathMut(pathTrait.path)
        return pathTrait
    }

    public open fun visitTraitItem(item: TraitItem): TraitItem =
        when (item) {
            is TraitItem.Const -> visitTraitItemConstMut(item)
            is TraitItem.Fn -> visitTraitItemFnMut(item)
            is TraitItem.AssocType -> visitTraitItemTypeMut(item)
            is TraitItem.Macro -> visitTraitItemMacroMut(item)
            is TraitItem.Verbatim -> {
                visitTokenStreamMut(item.tokens)
                item
            }
        }

    public open fun visitTraitItemConst(item: TraitItem.Const): TraitItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.ty = visitTypeMut(item.ty)
        item.default?.let { it.expr = visitExprMut(it.expr) }
        return item
    }

    public open fun visitTraitItemFn(item: TraitItem.Fn): TraitItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.sig = visitSignatureMut(item.sig)
        item.default = item.default?.let { visitBlockMut(it) }
        return item
    }

    public open fun visitTraitItemMacro(item: TraitItem.Macro): TraitItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.mac = visitMacroMut(item.mac)
        return item
    }

    public open fun visitTraitItemType(item: TraitItem.AssocType): TraitItem {
        item.attrs = visitAttributesMut(item.attrs)
        item.ident = visitIdentMut(item.ident)
        item.generics = visitGenericsMut(item.generics)
        item.bounds.mapValuesInPlace(::visitTypeParamBoundMut)
        item.default?.let { it.type = visitTypeMut(it.type) }
        return item
    }

    public open fun visitUseTree(useTree: UseTree): UseTree =
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

    public open fun visitUseGlob(useTree: UseTree.Glob): UseTree = useTree

    public open fun visitUseGroup(useTree: UseTree.Group): UseTree {
        useTree.items.mapValuesInPlace(::visitUseTreeMut)
        return useTree
    }

    public open fun visitUseName(useTree: UseTree.Name): UseTree {
        useTree.ident = visitIdentMut(useTree.ident)
        return useTree
    }

    public open fun visitUsePath(useTree: UseTree.Path): UseTree {
        useTree.ident = visitIdentMut(useTree.ident)
        useTree.tree = useTree.tree?.let { visitUseTreeMut(it) }
        return useTree
    }

    public open fun visitUseRename(useTree: UseTree.Name): UseTree {
        useTree.ident = visitIdentMut(useTree.ident)
        useTree.rename?.let { it.ident = visitIdentMut(it.ident) }
        return useTree
    }

    public open fun visitVariadic(variadic: Variadic): Variadic {
        variadic.attrs = visitAttributesMut(variadic.attrs)
        variadic.pat?.let { it.pat = visitPatMut(it.pat) }
        return variadic
    }

    public open fun visitVariant(variant: Variant): Variant {
        variant.attrs = visitAttributesMut(variant.attrs)
        variant.ident = visitIdentMut(variant.ident)
        variant.fields = visitFieldsMut(variant.fields)
        variant.discriminant?.let { it.expr = visitExprMut(it.expr) }
        return variant
    }

    public open fun visitVisibility(visibility: Visibility): Visibility =
        when (visibility) {
            is Visibility.Public -> visibility
            is Visibility.Restricted -> visitVisRestrictedMut(visibility)
            Visibility.Inherited -> visibility
        }

    public open fun visitVisRestrictedMut(visibility: Visibility.Restricted): Visibility.Restricted {
        visibility.path = visitPathMut(visibility.path)
        return visibility
    }

    public open fun visitWhereClause(whereClause: WhereClause): WhereClause {
        whereClause.predicates.mapValuesInPlace(::visitWherePredicateMut)
        return whereClause
    }

    public open fun visitWherePredicate(wherePredicate: WherePredicate): WherePredicate =
        when (wherePredicate) {
            is WherePredicate.LifetimePredicate -> visitPredicateLifetimeMut(wherePredicate)
            is WherePredicate.TypePredicate -> visitPredicateTypeMut(wherePredicate)
        }

    public open fun visitPredicateLifetimeMut(predicate: WherePredicate.LifetimePredicate): WherePredicate.LifetimePredicate {
        predicate.lifetime = visitLifetimeMut(predicate.lifetime)
        predicate.bounds.mapValuesInPlace(::visitLifetimeMut)
        return predicate
    }

    public open fun visitPredicateTypeMut(predicate: WherePredicate.TypePredicate): WherePredicate.TypePredicate {
        predicate.lifetimes = predicate.lifetimes?.let { visitBoundLifetimesMut(it) }
        predicate.boundedTy = visitTypeMut(predicate.boundedTy)
        predicate.bounds.mapValuesInPlace(::visitTypeParamBoundMut)
        return predicate
    }

    public open fun visitStmtMacroMut(stmt: Stmt.MacroStmt): Stmt.MacroStmt {
        stmt.attrs = visitAttributesMut(stmt.attrs)
        stmt.mac = visitMacroMut(stmt.mac)
        return stmt
    }

    public open fun visitSpanMut(span: Span): Span = span

    public open fun visitTokenStreamMut(tokens: TokenStream): TokenStream = tokens

    public open fun visitExprMut(e: Expr): Expr = visitExpr(e)

    public open fun visitTypeMut(t: SynType): SynType = visitType(t)

    public open fun visitPathMut(p: Path): Path = visitPath(p)

    public open fun visitPatMut(p: Pat): Pat = visitPat(p)

    public open fun visitItemMut(i: Item): Item = visitItem(i)

    public open fun visitFileMut(f: File): File = visitFile(f)

    public open fun visitAttributeMut(a: Attribute): Attribute = visitAttribute(a)

    public open fun visitAttrStyleMut(style: AttrStyle): AttrStyle = visitAttrStyle(style)

    public open fun visitMetaMut(m: Meta): Meta = visitMeta(m)

    public open fun visitMetaListMut(m: Meta.List): Meta = visitMetaList(m)

    public open fun visitMetaNameValueMut(m: Meta.NameValue): Meta = visitMetaNameValue(m)

    public open fun visitGenericsMut(g: Generics): Generics = visitGenerics(g)

    public open fun visitLitMut(l: Lit): Lit = visitLit(l)

    public open fun visitLifetimeMut(lt: Lifetime): Lifetime = visitLifetime(lt)

    public open fun visitIdentMut(id: Ident): Ident = visitIdent(id)

    public open fun visitStmtMut(s: Stmt): Stmt = visitStmt(s)

    public open fun visitDataMut(d: Data): Data = visitData(d)

    public open fun visitDataEnumMut(d: DataEnum): DataEnum = visitDataEnum(d)

    public open fun visitDataStructMut(d: DataStruct): DataStruct = visitDataStruct(d)

    public open fun visitDataUnionMut(d: DataUnion): DataUnion = visitDataUnion(d)

    public open fun visitDeriveInputMut(di: DeriveInput): DeriveInput = visitDeriveInput(di)

    public open fun visitBlockMut(block: Block): Block = visitBlock(block)

    public open fun visitAttributesMut(attrs: MutableList<Attribute>): MutableList<Attribute> = visitAttributes(attrs)

    public open fun visitSignatureMut(sig: Signature): Signature = visitSignature(sig)

    public open fun visitAbiMut(a: Abi): Abi = visitAbi(a)

    public open fun visitReturnTypeMut(rt: ReturnType): ReturnType = visitReturnType(rt)

    public open fun visitFnArgMut(arg: FnArg): FnArg = visitFnArg(arg)

    public open fun visitReceiverMut(receiver: FnArg.Receiver): FnArg.Receiver = visitReceiver(receiver)

    public open fun visitPatTypeMut(patType: PatType): PatType = visitPatType(patType)

    public open fun visitPatIdentMut(patIdent: Pat.Ident): Pat = visitPatIdent(patIdent)

    public open fun visitTypePathMut(typePath: SynType.Path): SynType = visitTypePath(typePath)

    public open fun visitTypeReferenceMut(ty: SynType.Reference): SynType = visitTypeReference(ty)

    public open fun visitTypeArrayMut(ty: SynType.Array): SynType = visitTypeArray(ty)

    public open fun visitTypeGroupMut(ty: SynType.Group): SynType = visitTypeGroup(ty)

    public open fun visitTypeImplTraitMut(ty: SynType.ImplTrait): SynType = visitTypeImplTrait(ty)

    public open fun visitTypeInferMut(ty: SynType.Infer): SynType = visitTypeInfer(ty)

    public open fun visitTypeMacroMut(ty: SynType.Macro): SynType = visitTypeMacro(ty)

    public open fun visitTypeNeverMut(ty: SynType.Never): SynType = visitTypeNever(ty)

    public open fun visitTypePtrMut(ty: SynType.Ptr): SynType = visitTypePtr(ty)

    public open fun visitPointerMutabilityMut(mutability: PointerMutability): PointerMutability = visitPointerMutability(mutability)

    public open fun visitTypeBareFnMut(ty: SynType.BareFn): SynType = visitTypeBareFn(ty)

    public open fun visitBareFnArgMut(arg: BareFnArg): BareFnArg = visitBareFnArg(arg)

    public open fun visitBareVariadicMut(variadic: BareVariadic): BareVariadic = visitBareVariadic(variadic)

    public open fun visitTypeParenMut(ty: SynType.Paren): SynType = visitTypeParen(ty)

    public open fun visitTypeSliceMut(ty: SynType.Slice): SynType = visitTypeSlice(ty)

    public open fun visitTypeTraitObjectMut(ty: SynType.TraitObject): SynType = visitTypeTraitObject(ty)

    public open fun visitTypeTupleMut(ty: SynType.Tuple): SynType = visitTypeTuple(ty)

    public open fun visitExprPathMut(exprPath: Expr.Path): Expr = visitExprPath(exprPath)

    public open fun visitMacroMut(mac: Macro): Macro = visitMacro(mac)

    public open fun visitPathArgumentsMut(pathArgs: PathArguments): PathArguments = visitPathArguments(pathArgs)

    public open fun visitAngleBracketedGenericArgumentsMut(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed = visitAngleBracketedGenericArguments(pathArgs)

    public open fun visitParenthesizedGenericArgumentsMut(pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized = visitParenthesizedGenericArguments(pathArgs)

    public open fun visitGenericArgumentMut(genArg: GenericArgument): GenericArgument = visitGenericArgument(genArg)

    public open fun visitAssocTypeMut(assoc: AssocType): AssocType = visitAssocType(assoc)

    public open fun visitAssocConstMut(assoc: AssocConst): AssocConst = visitAssocConst(assoc)

    public open fun visitConstraintMut(constraint: Constraint): Constraint = visitConstraint(constraint)

    public open fun visitTypeParamBoundMut(bound: TypeParamBound): TypeParamBound = visitTypeParamBound(bound)

    public open fun visitTraitBoundMut(bound: TypeParamBound.Trait): TypeParamBound = visitTraitBound(bound)

    public open fun visitTraitBoundModifierMut(modifier: TraitBoundModifier): TraitBoundModifier = visitTraitBoundModifier(modifier)

    public open fun visitBinOpMut(op: BinOp): BinOp = visitBinOp(op)

    public open fun visitBoundLifetimesMut(boundLifetimes: BoundLifetimes): BoundLifetimes = visitBoundLifetimes(boundLifetimes)

    public open fun visitCapturedParamMut(param: CapturedParam): CapturedParam = visitCapturedParam(param)

    public open fun visitPathSegmentMut(segment: PathSegment): PathSegment = visitPathSegment(segment)

    public open fun visitArmMut(arm: Arm): Arm = visitArm(arm)

    public open fun visitElseExprMut(elseExpr: ElseExpr): ElseExpr = visitElseExpr(elseExpr)

    public open fun visitFieldPatMut(fieldPat: FieldPat): FieldPat = visitFieldPat(fieldPat)

    public open fun visitFieldValueMut(fieldValue: FieldValue): FieldValue = visitFieldValue(fieldValue)

    public open fun visitGenericParamMut(param: GenericParam): GenericParam = visitGenericParam(param)

    public open fun visitFieldMut(field: Field): Field = visitField(field)

    public open fun visitFieldMutabilityMut(fieldMutability: FieldMutability): FieldMutability = visitFieldMutability(fieldMutability)

    public open fun visitFieldsMut(fields: Fields): Fields = visitFields(fields)

    public open fun visitFieldsNamedMut(fields: FieldsNamed): FieldsNamed = visitFieldsNamed(fields)

    public open fun visitFieldsUnnamedMut(fields: FieldsUnnamed): FieldsUnnamed = visitFieldsUnnamed(fields)

    public open fun visitImplItemMut(item: ImplItem): ImplItem = visitImplItem(item)

    public open fun visitImplItemConstMut(item: ImplItem.Const): ImplItem = visitImplItemConst(item)

    public open fun visitImplItemFnMut(item: ImplItem.Fn): ImplItem = visitImplItemFn(item)

    public open fun visitImplItemMacroMut(item: ImplItem.Macro): ImplItem = visitImplItemMacro(item)

    public open fun visitImplItemTypeMut(item: ImplItem.AssocType): ImplItem = visitImplItemType(item)

    public open fun visitForeignItemMut(item: ForeignItem): ForeignItem = visitForeignItem(item)

    public open fun visitForeignItemFnMut(item: ForeignItem.Fn): ForeignItem = visitForeignItemFn(item)

    public open fun visitForeignItemMacroMut(item: ForeignItem.Macro): ForeignItem = visitForeignItemMacro(item)

    public open fun visitForeignItemStaticMut(item: ForeignItem.Static): ForeignItem = visitForeignItemStatic(item)

    public open fun visitForeignItemTypeMut(item: ForeignItem.ItemType): ForeignItem = visitForeignItemType(item)

    public open fun visitImplRestrictionMut(restriction: ImplRestriction): ImplRestriction = visitImplRestriction(restriction)

    public open fun visitItemConstMut(item: Item.Const): Item = visitItemConst(item)

    public open fun visitItemEnumMut(item: Item.Enum): Item = visitItemEnum(item)

    public open fun visitItemExternCrateMut(item: Item.ExternCrate): Item = visitItemExternCrate(item)

    public open fun visitItemFnMut(item: Item.Fn): Item = visitItemFn(item)

    public open fun visitItemForeignModMut(item: Item.ForeignMod): Item = visitItemForeignMod(item)

    public open fun visitItemImplMut(item: Item.Impl): Item = visitItemImpl(item)

    public open fun visitItemMacroMut(item: Item.Macro): Item = visitItemMacro(item)

    public open fun visitItemModMut(item: Item.Mod): Item = visitItemMod(item)

    public open fun visitItemStaticMut(item: Item.Static): Item = visitItemStatic(item)

    public open fun visitItemStructMut(item: Item.Struct): Item = visitItemStruct(item)

    public open fun visitItemTraitMut(item: Item.Trait): Item = visitItemTrait(item)

    public open fun visitItemTraitAliasMut(item: Item.TraitAlias): Item = visitItemTraitAlias(item)

    public open fun visitItemTypeMut(item: Item.ItemType): Item = visitItemType(item)

    public open fun visitItemUnionMut(item: Item.Union): Item = visitItemUnion(item)

    public open fun visitItemUseMut(item: Item.Use): Item = visitItemUse(item)

    public open fun visitStaticMutabilityMut(mutability: StaticMutability): StaticMutability = visitStaticMutability(mutability)

    public open fun visitModContentMut(modContent: ModContent): ModContent = visitModContent(modContent)

    public open fun visitLocalInitMut(init: LocalInit): LocalInit = visitLocalInit(init)

    public open fun visitMemberMut(member: Member): Member = visitMember(member)

    public open fun visitQSelfMut(qself: QSelf): QSelf = visitQSelf(qself)

    public open fun visitPathTraitMut(pathTrait: PathTrait): PathTrait = visitPathTrait(pathTrait)

    public open fun visitTraitItemMut(item: TraitItem): TraitItem = visitTraitItem(item)

    public open fun visitTraitItemConstMut(item: TraitItem.Const): TraitItem = visitTraitItemConst(item)

    public open fun visitTraitItemFnMut(item: TraitItem.Fn): TraitItem = visitTraitItemFn(item)

    public open fun visitTraitItemMacroMut(item: TraitItem.Macro): TraitItem = visitTraitItemMacro(item)

    public open fun visitTraitItemTypeMut(item: TraitItem.AssocType): TraitItem = visitTraitItemType(item)

    public open fun visitUseTreeMut(useTree: UseTree): UseTree = visitUseTree(useTree)

    public open fun visitUseGlobMut(useTree: UseTree.Glob): UseTree = visitUseGlob(useTree)

    public open fun visitUseGroupMut(useTree: UseTree.Group): UseTree = visitUseGroup(useTree)

    public open fun visitUseNameMut(useTree: UseTree.Name): UseTree = visitUseName(useTree)

    public open fun visitUsePathMut(useTree: UseTree.Path): UseTree = visitUsePath(useTree)

    public open fun visitUseRenameMut(useTree: UseTree.Name): UseTree = visitUseRename(useTree)

    public open fun visitVariadicMut(variadic: Variadic): Variadic = visitVariadic(variadic)

    public open fun visitVariantMut(variant: Variant): Variant = visitVariant(variant)

    public open fun visitVisibilityMut(visibility: Visibility): Visibility = visitVisibility(visibility)

    public open fun visitWhereClauseMut(whereClause: WhereClause): WhereClause = visitWhereClause(whereClause)

    public open fun visitWherePredicateMut(wherePredicate: WherePredicate): WherePredicate = visitWherePredicate(wherePredicate)
}
