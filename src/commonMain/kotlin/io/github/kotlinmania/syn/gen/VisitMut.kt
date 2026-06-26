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

    public open fun visitExprArrayMut(e: Expr.Array): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), elems = e.elems.copy({ visitExprMut(it) }, { it }))

    public open fun visitExprAssignMut(e: Expr.Assign): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), left = visitExprMut(e.left), right = visitExprMut(e.right))

    public open fun visitExprAsyncMut(e: Expr.Async): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), block = visitBlockMut(e.block))

    public open fun visitExprAwaitMut(e: Expr.Await): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), base = visitExprMut(e.base))

    public open fun visitExprBinaryMut(e: Expr.Binary): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), left = visitExprMut(e.left), op = visitBinOpMut(e.op), right = visitExprMut(e.right))

    public open fun visitExprBlockMut(e: Expr.BlockExpr): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), label = e.label?.let { visitLabelMut(it) }, block = visitBlockMut(e.block))

    public open fun visitExprBreakMut(e: Expr.Break): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), label = e.label?.let { visitLifetimeMut(it) }, expr = e.expr?.let { visitExprMut(it) })

    public open fun visitExprCallMut(e: Expr.Call): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), func = visitExprMut(e.func), args = e.args.copy({ visitExprMut(it) }, { it }))

    public open fun visitExprCastMut(e: Expr.Cast): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr), ty = visitTypeMut(e.ty))

    public open fun visitExprClosureMut(e: Expr.Closure): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), inputs = e.inputs.copy({ visitPatMut(it) }, { it }), output = visitReturnTypeMut(e.output), body = visitExprMut(e.body))

    public open fun visitExprConstMut(e: Expr.Const): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), block = visitBlockMut(e.block))

    public open fun visitExprContinueMut(e: Expr.Continue): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), label = e.label?.let { visitLifetimeMut(it) })

    public open fun visitExprFieldMut(e: Expr.Field): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), base = visitExprMut(e.base), member = visitMemberMut(e.member))

    public open fun visitExprForLoopMut(e: Expr.ForLoop): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), label = e.label?.let { visitLabelMut(it) }, pat = visitPatMut(e.pat), expr = visitExprMut(e.expr), body = visitBlockMut(e.body))

    public open fun visitExprGroupMut(e: Expr.Group): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr))

    public open fun visitExprIfMut(e: Expr.If): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), cond = visitExprMut(e.cond), thenBranch = visitBlockMut(e.thenBranch), elseBranch = e.elseBranch?.let { visitElseExprMut(it) })

    public open fun visitExprIndexMut(e: Expr.Index): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr), index = visitExprMut(e.index))

    public open fun visitExprInferMut(e: Expr.Infer): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs))

    public open fun visitExprLetMut(e: Expr.Let): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), pat = visitPatMut(e.pat), expr = visitExprMut(e.expr))

    public open fun visitExprLitMut(e: Expr.Lit): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), lit = visitLitMut(e.lit))

    public open fun visitExprLoopMut(e: Expr.Loop): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), label = e.label?.let { visitLabelMut(it) }, body = visitBlockMut(e.body))

    public open fun visitExprMacroMut(e: Expr.Macro): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), mac = visitMacroMut(e.mac))

    public open fun visitExprMatchMut(e: Expr.Match): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr), arms = e.arms.map { visitArmMut(it) })

    public open fun visitExprMethodCallMut(e: Expr.MethodCall): Expr =
        e.copy(
            attrs = visitAttributesMut(e.attrs),
            receiver = visitExprMut(e.receiver),
            method = visitIdentMut(e.method),
            turbofish = e.turbofish?.let { visitAngleBracketedGenericArgumentsMut(it) },
            args = e.args.copy({ visitExprMut(it) }, { it }),
        )

    public open fun visitExprParenMut(e: Expr.Paren): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr))

    public open fun visitExprRangeMut(e: Expr.Range): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), start = e.start?.let { visitExprMut(it) }, limits = visitRangeLimitsMut(e.limits), end = e.end?.let { visitExprMut(it) })

    public open fun visitExprRawAddrMut(e: Expr.RawAddr): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), mutability = visitPointerMutabilityMut(e.mutability), expr = visitExprMut(e.expr))

    public open fun visitExprReferenceMut(e: Expr.Reference): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr))

    public open fun visitExprRepeatMut(e: Expr.Repeat): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr), len = visitExprMut(e.len))

    public open fun visitExprReturnMut(e: Expr.Return): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = e.expr?.let { visitExprMut(it) })

    public open fun visitExprStructMut(e: Expr.Struct): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), qself = e.qself?.let { visitQselfMut(it) }, path = visitPathMut(e.path), fields = e.fields.copy({ visitFieldValueMut(it) }, { it }), rest = e.rest?.let { visitExprMut(it) })

    public open fun visitExprTryMut(e: Expr.Try): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = visitExprMut(e.expr))

    public open fun visitExprTryBlockMut(e: Expr.TryBlock): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), block = visitBlockMut(e.block))

    public open fun visitExprTupleMut(e: Expr.Tuple): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), elems = e.elems.copy({ visitExprMut(it) }, { it }))

    public open fun visitExprUnaryMut(e: Expr.Unary): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), op = visitUnOpMut(e.op), expr = visitExprMut(e.expr))

    public open fun visitExprUnsafeMut(e: Expr.Unsafe): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), block = visitBlockMut(e.block))

    public open fun visitExprWhileMut(e: Expr.While): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), label = e.label?.let { visitLabelMut(it) }, cond = visitExprMut(e.cond), body = visitBlockMut(e.body))

    public open fun visitExprYieldMut(e: Expr.Yield): Expr =
        e.copy(attrs = visitAttributesMut(e.attrs), expr = e.expr?.let { visitExprMut(it) })

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

    public open fun visitPath(p: Path): Path =
        Path(
            leadingColon = p.leadingColon,
            segments = p.segments.copy({ visitPathSegmentMut(it) }, { it }),
        )

    public open fun visitPat(p: Pat): Pat =
        when (p) {
            is Pat.Const -> p.copy(attrs = visitAttributesMut(p.attrs), block = visitBlockMut(p.block))
            is Pat.Ident -> visitPatIdentMut(p)
            is Pat.Lit -> p.copy(attrs = visitAttributesMut(p.attrs), lit = visitLitMut(p.lit))
            is Pat.Macro -> p.copy(attrs = visitAttributesMut(p.attrs), mac = visitMacroMut(p.mac))
            is Pat.Or -> visitPatOrMut(p)
            is Pat.PatParen -> visitPatParenMut(p)
            is Pat.Path -> p.copy(attrs = visitAttributesMut(p.attrs), qself = p.qself?.let { visitQselfMut(it) }, path = visitPathMut(p.path))
            is Pat.Range -> p.copy(attrs = visitAttributesMut(p.attrs), start = p.start?.let { visitExprMut(it) }, limits = visitRangeLimitsMut(p.limits), end = p.end?.let { visitExprMut(it) })
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

    public open fun visitFile(f: File): File =
        f.copy(
            attrs = visitAttributesMut(f.attrs),
            items = f.items.map { visitItemMut(it) },
        )

    public open fun visitAttribute(a: Attribute): Attribute =
        a.copy(
            style = visitAttrStyleMut(a.style),
            meta = visitMetaMut(a.meta),
        )

    public open fun visitAttrStyle(style: AttrStyle): AttrStyle = style

    public open fun visitMeta(m: Meta): Meta =
        when (m) {
            is Meta.PathMeta -> m.copy(path = visitPathMut(m.path))
            is Meta.List -> visitMetaListMut(m)
            is Meta.NameValue -> visitMetaNameValueMut(m)
        }

    public open fun visitMetaList(m: Meta.List): Meta {
        visitMacroDelimiterMut(m.delimiter)
        visitTokenStreamMut(m.tokens)
        return m.copy(path = visitPathMut(m.path))
    }

    public open fun visitMetaNameValue(m: Meta.NameValue): Meta =
        m.copy(path = visitPathMut(m.path), value = visitExprMut(m.value))

    public open fun visitGenerics(g: Generics): Generics =
        Generics(
            ltToken = g.ltToken,
            params = g.params.copy({ visitGenericParamMut(it) }, { it }),
            gtToken = g.gtToken,
            whereClause = g.whereClause?.let { visitWhereClauseMut(it) },
        )

    public open fun visitLit(l: Lit): Lit =
        when (l) {
            is Lit.Str -> Lit.Str(visitLitStrMut(l.value))
            is Lit.ByteStr -> Lit.ByteStr(visitLitByteStrMut(l.value))
            is Lit.CStr -> Lit.CStr(visitLitCstrMut(l.value))
            is Lit.Byte -> Lit.Byte(visitLitByteMut(l.value))
            is Lit.Char -> Lit.Char(visitLitCharMut(l.value))
            is Lit.Int -> Lit.Int(visitLitIntMut(l.value))
            is Lit.Float -> Lit.Float(visitLitFloatMut(l.value))
            is Lit.Bool -> Lit.Bool(visitLitBoolMut(l.value))
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
            is Stmt.ItemStmt -> s.copy(item = visitItemMut(s.item))
            is Stmt.ExprStmt -> s.copy(expr = visitExprMut(s.expr))
            is Stmt.MacroStmt -> visitStmtMacroMut(s)
        }

    public open fun visitData(d: Data): Data =
        when (d) {
            is Data.Struct -> Data.Struct(visitDataStructMut(d.value))
            is Data.Enum -> Data.Enum(visitDataEnumMut(d.value))
            is Data.Union -> Data.Union(visitDataUnionMut(d.value))
        }

    public open fun visitDataEnum(d: DataEnum): DataEnum =
        d.copy(variants = d.variants.copy({ visitVariantMut(it) }, { it }))

    public open fun visitDataStruct(d: DataStruct): DataStruct =
        d.copy(fields = visitFieldsMut(d.fields))

    public open fun visitDataUnion(d: DataUnion): DataUnion =
        d.copy(fields = visitFieldsNamedMut(d.fields))

    public open fun visitLabelMut(label: Label): Label =
        label.copy(name = visitLifetimeMut(label.name))

    public open fun visitDeriveInput(di: DeriveInput): DeriveInput =
        di.copy(
            attrs = visitAttributesMut(di.attrs),
            vis = visitVisibilityMut(di.vis),
            ident = visitIdentMut(di.ident),
            generics = visitGenericsMut(di.generics),
            data = visitDataMut(di.data),
        )

    public open fun visitBlock(block: Block): Block = block.copy(stmts = block.stmts.mapTo(mutableListOf()) { visitStmtMut(it) })

    public open fun visitAttributes(attrs: MutableList<Attribute>): MutableList<Attribute> {
        for (i in attrs.indices) attrs[i] = visitAttributeMut(attrs[i])
        return attrs
    }

    public open fun visitSignature(sig: Signature): Signature {
        var result = sig
        result =
            result.copy(
                abi = result.abi?.let { visitAbiMut(it) },
                ident = visitIdentMut(result.ident),
                generics = visitGenericsMut(result.generics),
                inputs = result.inputs.copy({ visitFnArgMut(it) }, { it }),
                variadic = result.variadic?.let { visitVariadicMut(it) },
                output = visitReturnTypeMut(result.output),
            )
        return result
    }

    public open fun visitAbi(a: Abi): Abi = a

    public open fun visitReturnType(rt: ReturnType): ReturnType =
        when (rt) {
            is ReturnType.Default -> rt
            is ReturnType.TypeReturn -> rt.copy(ty = visitTypeMut(rt.ty))
        }

    public open fun visitFnArg(arg: FnArg): FnArg =
        when (arg) {
            is FnArg.Receiver -> visitReceiverMut(arg)
            is FnArg.Typed -> arg.copy(patType = visitPatTypeMut(arg.patType))
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

    public open fun visitForeignItemFn(item: ForeignItem.Fn): ForeignItem.Fn =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            sig = visitSignatureMut(item.sig),
        )

    public open fun visitForeignItemMacro(item: ForeignItem.Macro): ForeignItem.Macro =
        item.copy(attrs = visitAttributesMut(item.attrs), mac = visitMacroMut(item.mac))

    public open fun visitForeignItemStatic(item: ForeignItem.Static): ForeignItem.Static =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            mutability = visitStaticMutabilityMut(item.mutability),
            ident = visitIdentMut(item.ident),
            ty = visitTypeMut(item.ty),
        )

    public open fun visitForeignItemType(item: ForeignItem.ItemType): ForeignItem.ItemType =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
        )

    public open fun visitReceiver(receiver: FnArg.Receiver): FnArg.Receiver {
        val reference = receiver.reference
        return receiver.copy(
            attrs = visitAttributesMut(receiver.attrs),
            reference = reference?.copy(lifetime = reference.lifetime?.let { visitLifetimeMut(it) }),
            `type` = visitTypeMut(receiver.type),
        )
    }

    public open fun visitPatType(patType: PatType): PatType = patType.copy(pat = visitPatMut(patType.pat), ty = visitTypeMut(patType.ty))

    public open fun visitPatIdent(patIdent: Pat.Ident): Pat =
        patIdent.copy(
            attrs = visitAttributesMut(patIdent.attrs),
            ident = visitIdentMut(patIdent.ident),
            subpat = patIdent.subpat?.let { visitPatMut(it) },
        )

    public open fun visitPatOrMut(pat: Pat.Or): Pat =
        pat.copy(attrs = visitAttributesMut(pat.attrs), cases = pat.cases.copy({ visitPatMut(it) }, { it }))

    public open fun visitPatParenMut(pat: Pat.PatParen): Pat =
        pat.copy(attrs = visitAttributesMut(pat.attrs), pat = visitPatMut(pat.pat))

    public open fun visitPatReferenceMut(pat: Pat.Reference): Pat =
        pat.copy(attrs = visitAttributesMut(pat.attrs), pat = visitPatMut(pat.pat))

    public open fun visitPatRestMut(pat: Pat.Rest): Pat.Rest =
        pat.copy(attrs = visitAttributesMut(pat.attrs))

    public open fun visitPatRestMut(rest: PatRest): PatRest =
        rest.copy(attrs = visitAttributesMut(rest.attrs))

    public open fun visitPatSliceMut(pat: Pat.Slice): Pat =
        pat.copy(attrs = visitAttributesMut(pat.attrs), elems = pat.elems.copy({ visitPatMut(it) }, { it }))

    public open fun visitPatStructMut(pat: Pat.Struct): Pat =
        pat.copy(
            attrs = visitAttributesMut(pat.attrs),
            qself = pat.qself?.let { visitQselfMut(it) },
            path = visitPathMut(pat.path),
            fields = pat.fields.copy({ visitFieldPatMut(it) }, { it }),
            rest = pat.rest?.let { visitPatRestMut(it) },
        )

    public open fun visitPatTupleMut(pat: Pat.Tuple): Pat =
        pat.copy(attrs = visitAttributesMut(pat.attrs), elems = pat.elems.copy({ visitPatMut(it) }, { it }))

    public open fun visitPatTupleStructMut(pat: Pat.TupleStruct): Pat =
        pat.copy(
            attrs = visitAttributesMut(pat.attrs),
            qself = pat.qself?.let { visitQselfMut(it) },
            path = visitPathMut(pat.path),
            elems = pat.elems.copy({ visitPatMut(it) }, { it }),
        )

    public open fun visitPatWildMut(pat: Pat.Wild): Pat =
        pat.copy(attrs = visitAttributesMut(pat.attrs))

    public open fun visitPatTypeMut(pat: Pat.TypeAscription): Pat =
        pat.copy(attrs = visitAttributesMut(pat.attrs), pat = visitPatMut(pat.pat), ty = visitTypeMut(pat.ty))

    public open fun visitTypePath(typePath: SynType.Path): SynType =
        typePath.copy(qself = typePath.qself?.let { visitQselfMut(it) }, path = visitPathMut(typePath.path))

    public open fun visitTypeReference(ty: SynType.Reference): SynType = ty.copy(lifetime = ty.lifetime?.let { visitLifetimeMut(it) }, elem = visitTypeMut(ty.elem))

    public open fun visitTypeArray(ty: SynType.Array): SynType =
        ty.copy(elem = visitTypeMut(ty.elem), len = visitExprMut(ty.len))

    public open fun visitTypeGroup(ty: SynType.Group): SynType =
        ty.copy(elem = visitTypeMut(ty.elem))

    public open fun visitTypeImplTrait(ty: SynType.ImplTrait): SynType = ty.copy(bounds = ty.bounds.copy({ visitTypeParamBoundMut(it) }, { it }))

    public open fun visitTypeInfer(ty: SynType.Infer): SynType = ty

    public open fun visitTypeMacro(ty: SynType.Macro): SynType = ty.copy(mac = visitMacroMut(ty.mac))

    public open fun visitTypeNever(ty: SynType.Never): SynType = ty

    public open fun visitTypePtr(ty: SynType.Ptr): SynType =
        ty.copy(mutability = visitPointerMutabilityMut(ty.mutability), elem = visitTypeMut(ty.elem))

    public open fun visitPointerMutability(mutability: PointerMutability): PointerMutability = mutability

    public open fun visitPointerMutabilityMut(mutability: io.github.kotlinmania.syn.token.Mut?): io.github.kotlinmania.syn.token.Mut? = mutability

    public open fun visitTypeBareFn(ty: SynType.BareFn): SynType =
        ty.copy(
            lifetimes = ty.lifetimes?.let { visitBoundLifetimesMut(it) },
            abi = ty.abi?.let { visitAbiMut(it) },
            inputs = ty.inputs.copy({ visitBareFnArgMut(it) }, { it }),
            variadic = ty.variadic?.let { visitBareVariadicMut(it) },
            output = visitReturnTypeMut(ty.output),
        )

    public open fun visitBareFnArg(arg: BareFnArg): BareFnArg {
        val name = arg.name
        return arg.copy(
            attrs = visitAttributesMut(arg.attrs),
            name = name?.copy(ident = visitIdentMut(name.ident)),
            ty = visitTypeMut(arg.ty),
        )
    }

    public open fun visitBareVariadic(variadic: BareVariadic): BareVariadic {
        val name = variadic.name
        return variadic.copy(
            attrs = visitAttributesMut(variadic.attrs),
            name = name?.copy(ident = visitIdentMut(name.ident)),
        )
    }

    public open fun visitTypeParen(ty: SynType.Paren): SynType = ty.copy(elem = visitTypeMut(ty.elem))

    public open fun visitTypeSlice(ty: SynType.Slice): SynType = ty.copy(elem = visitTypeMut(ty.elem))

    public open fun visitTypeTraitObject(ty: SynType.TraitObject): SynType =
        ty.copy(bounds = ty.bounds.copy({ visitTypeParamBoundMut(it) }, { it }))

    public open fun visitTypeTuple(ty: SynType.Tuple): SynType = ty.copy(elems = ty.elems.copy({ visitTypeMut(it) }, { it }))

    public open fun visitExprPath(exprPath: Expr.Path): Expr =
        exprPath.copy(
            attrs = visitAttributesMut(exprPath.attrs),
            qself = exprPath.qself?.let { visitQselfMut(it) },
            path = visitPathMut(exprPath.path),
        )

    public open fun visitMacro(mac: Macro): Macro =
        mac.copy(path = visitPathMut(mac.path), delimiter = visitMacroDelimiterMut(mac.delimiter), tokens = visitTokenStreamMut(mac.tokens))

    public open fun visitPathArguments(pathArgs: PathArguments): PathArguments =
        when (pathArgs) {
            is PathArguments.None -> pathArgs
            is PathArguments.AngleBracketed -> visitAngleBracketedGenericArgumentsMut(pathArgs)
            is PathArguments.Parenthesized -> visitParenthesizedGenericArgumentsMut(pathArgs)
        }

    public open fun visitAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed =
        pathArgs.copy(args = pathArgs.args.copy({ visitGenericArgumentMut(it) }, { it }))

    public open fun visitParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized =
        pathArgs.copy(
            inputs = pathArgs.inputs.copy({ visitTypeMut(it) }, { it }),
            output = visitReturnTypeMut(pathArgs.output),
        )

    public open fun visitGenericArgument(genArg: GenericArgument): GenericArgument =
        when (genArg) {
            is GenericArgument.LifetimeArg -> genArg.copy(lifetime = visitLifetimeMut(genArg.lifetime))
            is GenericArgument.TypeArg -> genArg.copy(type = visitTypeMut(genArg.type))
            is GenericArgument.ConstArg -> genArg.copy(expr = visitExprMut(genArg.expr))
            is GenericArgument.AssocTypeArg -> genArg.copy(assoc = visitAssocTypeMut(genArg.assoc))
            is GenericArgument.AssocConstArg -> genArg.copy(assoc = visitAssocConstMut(genArg.assoc))
            is GenericArgument.ConstraintArg -> genArg.copy(constraint = visitConstraintMut(genArg.constraint))
        }

    public open fun visitAssocType(assoc: AssocType): AssocType =
        assoc.copy(
            ident = visitIdentMut(assoc.ident),
            generics = assoc.generics?.let { visitAngleBracketedGenericArgumentsMut(it) },
            ty = visitTypeMut(assoc.ty),
        )

    public open fun visitAssocConst(assoc: AssocConst): AssocConst =
        assoc.copy(
            ident = visitIdentMut(assoc.ident),
            generics = assoc.generics?.let { visitAngleBracketedGenericArgumentsMut(it) },
            value = visitExprMut(assoc.value),
        )

    public open fun visitConstraint(constraint: Constraint): Constraint =
        constraint.copy(
            ident = visitIdentMut(constraint.ident),
            generics = constraint.generics?.let { visitAngleBracketedGenericArgumentsMut(it) },
            bounds = constraint.bounds.copy({ visitTypeParamBoundMut(it) }, { it }),
        )

    public open fun visitTypeParamBound(bound: TypeParamBound): TypeParamBound =
        when (bound) {
            is TypeParamBound.Trait -> visitTraitBoundMut(bound)
            is TypeParamBound.LifetimeBound -> bound.copy(lifetime = visitLifetimeMut(bound.lifetime))
            is TypeParamBound.PreciseCapture -> visitPreciseCaptureMut(bound)
            is TypeParamBound.Verbatim -> {
                visitTokenStreamMut(bound.tokens)
                bound
            }
        }

    public open fun visitTraitBound(bound: TypeParamBound.Trait): TypeParamBound =
        bound.copy(
            modifier = visitTraitBoundModifierMut(bound.modifier),
            lifetimes = bound.lifetimes?.let { visitBoundLifetimesMut(it) },
            path = visitPathMut(bound.path),
        )

    public open fun visitTraitBoundModifier(modifier: TraitBoundModifier): TraitBoundModifier = modifier

    public open fun visitBinOp(op: BinOp): BinOp = op

    public open fun visitUnOpMut(op: UnOp): UnOp = op

    public open fun visitBoundLifetimes(boundLifetimes: BoundLifetimes): BoundLifetimes =
        boundLifetimes.copy(lifetimes = boundLifetimes.lifetimes.copy({ visitGenericParamMut(it) }, { it }))

    public open fun visitCapturedParam(param: CapturedParam): CapturedParam =
        when (param) {
            is CapturedParam.Lifetime -> param.copy(lifetime = visitLifetimeMut(param.lifetime))
            is CapturedParam.Ident -> param.copy(ident = visitIdentMut(param.ident))
        }

    public open fun visitPreciseCaptureMut(param: TypeParamBound.PreciseCapture): TypeParamBound =
        param.copy(params = param.params.copy({ visitCapturedParamMut(it) }, { it }))

    public open fun visitPathSegment(segment: PathSegment): PathSegment =
        segment.copy(ident = visitIdentMut(segment.ident), arguments = visitPathArgumentsMut(segment.arguments))

    public open fun visitArm(arm: Arm): Arm {
        val guard = arm.guard
        return arm.copy(
            attrs = visitAttributesMut(arm.attrs),
            pat = visitPatMut(arm.pat),
            guard = guard?.copy(expr = visitExprMut(guard.expr)),
            body = visitExprMut(arm.body),
        )
    }

    public open fun visitElseExpr(elseExpr: ElseExpr): ElseExpr =
        elseExpr.copy(expr = visitExprMut(elseExpr.expr))

    public open fun visitFieldPat(fieldPat: FieldPat): FieldPat =
        fieldPat.copy(
            attrs = visitAttributesMut(fieldPat.attrs),
            member = visitMemberMut(fieldPat.member),
            pat = visitPatMut(fieldPat.pat),
        )

    public open fun visitFieldValue(fieldValue: FieldValue): FieldValue =
        fieldValue.copy(
            attrs = visitAttributesMut(fieldValue.attrs),
            member = visitMemberMut(fieldValue.member),
            expr = visitExprMut(fieldValue.expr),
        )

    public open fun visitGenericParam(param: GenericParam): GenericParam =
        when (param) {
            is GenericParam.LifetimeParam -> visitLifetimeParamMut(param)
            is GenericParam.TypeParam -> visitTypeParamMut(param)
            is GenericParam.ConstParam -> visitConstParamMut(param)
        }

    public open fun visitLifetimeParamMut(param: GenericParam.LifetimeParam): GenericParam.LifetimeParam =
        param.copy(
            attrs = visitAttributesMut(param.attrs),
            lifetime = visitLifetimeMut(param.lifetime),
            bounds = param.bounds.copy({ visitLifetimeMut(it) }, { it }),
        )

    public open fun visitTypeParamMut(param: GenericParam.TypeParam): GenericParam.TypeParam =
        param.copy(
            attrs = visitAttributesMut(param.attrs),
            ident = visitIdentMut(param.ident),
            bounds = param.bounds.copy({ visitTypeParamBoundMut(it) }, { it }),
            default = param.default?.let { visitTypeMut(it) },
        )

    public open fun visitConstParamMut(param: GenericParam.ConstParam): GenericParam.ConstParam =
        param.copy(
            attrs = visitAttributesMut(param.attrs),
            ident = visitIdentMut(param.ident),
            ty = visitTypeMut(param.ty),
            default = param.default?.let { visitExprMut(it) },
        )

    public open fun visitField(field: Field): Field =
        field.copy(
            attrs = visitAttributesMut(field.attrs),
            vis = visitVisibilityMut(field.vis),
            mutability = visitFieldMutabilityMut(field.mutability),
            ident = field.ident?.let { visitIdentMut(it) },
            ty = visitTypeMut(field.ty),
        )

    public open fun visitFieldMutability(fieldMutability: FieldMutability): FieldMutability = fieldMutability

    public open fun visitFields(fields: Fields): Fields =
        when (fields) {
            is Fields.Named -> Fields.Named(visitFieldsNamedMut(fields.fields))
            is Fields.Unnamed -> Fields.Unnamed(visitFieldsUnnamedMut(fields.fields))
            Fields.Unit -> fields
        }

    public open fun visitFieldsNamed(fields: FieldsNamed): FieldsNamed =
        fields.copy(named = fields.named.copy({ visitFieldMut(it) }, { it }))

    public open fun visitFieldsUnnamed(fields: FieldsUnnamed): FieldsUnnamed =
        fields.copy(unnamed = fields.unnamed.copy({ visitFieldMut(it) }, { it }))

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

    public open fun visitImplItemConst(item: ImplItem.Const): ImplItem =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            ty = visitTypeMut(item.ty),
            expr = visitExprMut(item.expr),
        )

    public open fun visitImplItemFn(item: ImplItem.Fn): ImplItem =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            sig = visitSignatureMut(item.sig),
            block = visitBlockMut(item.block),
        )

    public open fun visitImplItemMacro(item: ImplItem.Macro): ImplItem =
        item.copy(attrs = visitAttributesMut(item.attrs), mac = visitMacroMut(item.mac))

    public open fun visitImplItemType(item: ImplItem.AssocType): ImplItem =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            ty = visitTypeMut(item.ty),
        )

    public open fun visitImplRestriction(restriction: ImplRestriction): ImplRestriction = restriction

    public open fun visitItemConst(item: Item.Const): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            ty = visitTypeMut(item.ty),
            expr = item.expr?.let { visitExprMut(it) },
        )

    public open fun visitItemEnum(item: Item.Enum): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            variants = item.variants.copy({ visitVariantMut(it) }, { it }),
        )

    public open fun visitItemExternCrate(item: Item.ExternCrate): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            rename = item.rename?.let { it.copy(ident = visitIdentMut(it.ident)) },
        )

    public open fun visitItemFn(item: Item.Fn): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            sig = visitSignatureMut(item.sig),
            block = item.block?.let { visitBlockMut(it) },
        )

    public open fun visitItemForeignMod(item: Item.ForeignMod): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            abi = visitAbiMut(item.abi),
            items = item.items.map { visitForeignItemMut(it) },
        )

    public open fun visitItemImpl(item: Item.Impl): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            generics = visitGenericsMut(item.generics),
            traitPath = item.traitPath?.let { visitPathTraitMut(it) },
            selfType = visitTypeMut(item.selfType),
            items = item.items.map { visitImplItemMut(it) },
        )

    public open fun visitItemMacro(item: Item.Macro): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            ident = item.ident?.let { visitIdentMut(it) },
            mac = visitMacroMut(item.mac),
        )

    public open fun visitItemMod(item: Item.Mod): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            content = item.content?.let { visitModContentMut(it) },
        )

    public open fun visitItemStatic(item: Item.Static): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            mutability = visitStaticMutabilityMut(item.mutability),
            ident = visitIdentMut(item.ident),
            ty = visitTypeMut(item.ty),
            expr = visitExprMut(item.expr),
        )

    public open fun visitItemStruct(item: Item.Struct): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            fields = visitFieldsMut(item.fields),
        )

    public open fun visitItemTrait(item: Item.Trait): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            restriction = item.restriction?.let { visitImplRestrictionMut(it) },
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            supertraits = item.supertraits.copy({ visitTypeParamBoundMut(it) }, { it }),
            items = item.items.map { visitTraitItemMut(it) },
        )

    public open fun visitItemTraitAlias(item: Item.TraitAlias): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            bounds = item.bounds.copy({ visitTypeParamBoundMut(it) }, { it }),
        )

    public open fun visitItemType(item: Item.ItemType): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            ty = visitTypeMut(item.ty),
        )

    public open fun visitItemUnion(item: Item.Union): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            fields = visitFieldsNamedMut(item.fields),
        )

    public open fun visitItemUse(item: Item.Use): Item =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            vis = visitVisibilityMut(item.vis),
            tree = visitUseTreeMut(item.tree),
        )

    public open fun visitStaticMutability(mutability: StaticMutability): StaticMutability = mutability

    public open fun visitRangeLimitsMut(limits: RangeLimits): RangeLimits = limits

    public open fun visitMacroDelimiterMut(delimiter: MacroDelimiter): MacroDelimiter = delimiter

    public open fun visitModContent(modContent: ModContent): ModContent =
        when (modContent) {
            is ModContent.Inline -> modContent.copy(items = modContent.items.map { visitItemMut(it) })
            is ModContent.Unnamed -> modContent
        }

    public open fun visitLocalMut(local: Stmt.Local): Stmt.Local =
        local.copy(
            attrs = visitAttributesMut(local.attrs),
            pat = visitPatMut(local.pat),
            init = local.init?.let { visitLocalInitMut(it) },
        )

    public open fun visitLocalInit(init: LocalInit): LocalInit =
        init.copy(
            expr = visitExprMut(init.expr),
            diverge = init.diverge?.let { visitElseExprMut(it) },
        )

    public open fun visitMember(member: Member): Member =
        when (member) {
            is Member.Named -> member.copy(ident = visitIdentMut(member.ident))
            is Member.Unnamed -> member.copy(index = visitIndexMut(member.index))
        }

    public open fun visitIndexMut(index: Index): Index {
        visitSpanMut(index.span)
        return index
    }

    public open fun visitQSelf(qself: QSelf): QSelf =
        qself.copy(ty = visitTypeMut(qself.ty))

    public open fun visitQselfMut(qself: QSelf): QSelf = visitQSelfMut(qself)

    public open fun visitPathTrait(pathTrait: PathTrait): PathTrait =
        pathTrait.copy(path = visitPathMut(pathTrait.path))

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
        val default = item.default
        return item.copy(
            attrs = visitAttributesMut(item.attrs),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            ty = visitTypeMut(item.ty),
            default = default?.copy(expr = visitExprMut(default.expr)),
        )
    }

    public open fun visitTraitItemFn(item: TraitItem.Fn): TraitItem =
        item.copy(
            attrs = visitAttributesMut(item.attrs),
            sig = visitSignatureMut(item.sig),
            default = item.default?.let { visitBlockMut(it) },
        )

    public open fun visitTraitItemMacro(item: TraitItem.Macro): TraitItem =
        item.copy(attrs = visitAttributesMut(item.attrs), mac = visitMacroMut(item.mac))

    public open fun visitTraitItemType(item: TraitItem.AssocType): TraitItem {
        val default = item.default
        return item.copy(
            attrs = visitAttributesMut(item.attrs),
            ident = visitIdentMut(item.ident),
            generics = visitGenericsMut(item.generics),
            bounds = item.bounds.copy({ visitTypeParamBoundMut(it) }, { it }),
            default = default?.copy(type = visitTypeMut(default.type)),
        )
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

    public open fun visitUseGroup(useTree: UseTree.Group): UseTree =
        useTree.copy(items = useTree.items.copy({ visitUseTreeMut(it) }, { it }))

    public open fun visitUseName(useTree: UseTree.Name): UseTree =
        useTree.copy(ident = visitIdentMut(useTree.ident))

    public open fun visitUsePath(useTree: UseTree.Path): UseTree =
        useTree.copy(
            ident = visitIdentMut(useTree.ident),
            tree = useTree.tree?.let { visitUseTreeMut(it) },
        )

    public open fun visitUseRename(useTree: UseTree.Name): UseTree {
        val rename = useTree.rename
        return useTree.copy(
            ident = visitIdentMut(useTree.ident),
            rename = rename?.copy(ident = visitIdentMut(rename.ident)),
        )
    }

    public open fun visitVariadic(variadic: Variadic): Variadic {
        val pat = variadic.pat
        return variadic.copy(
            attrs = visitAttributesMut(variadic.attrs),
            pat = pat?.copy(pat = visitPatMut(pat.pat)),
        )
    }

    public open fun visitVariant(variant: Variant): Variant {
        val discriminant = variant.discriminant
        return variant.copy(
            attrs = visitAttributesMut(variant.attrs),
            ident = visitIdentMut(variant.ident),
            fields = visitFieldsMut(variant.fields),
            discriminant = discriminant?.copy(expr = visitExprMut(discriminant.expr)),
        )
    }

    public open fun visitVisibility(visibility: Visibility): Visibility =
        when (visibility) {
            is Visibility.Public -> visibility
            is Visibility.Restricted -> visitVisRestrictedMut(visibility)
            Visibility.Inherited -> visibility
        }

    public open fun visitVisRestrictedMut(visibility: Visibility.Restricted): Visibility.Restricted =
        visibility.copy(path = visitPathMut(visibility.path))

    public open fun visitWhereClause(whereClause: WhereClause): WhereClause =
        whereClause.copy(predicates = whereClause.predicates.copy({ visitWherePredicateMut(it) }, { it }))

    public open fun visitWherePredicate(wherePredicate: WherePredicate): WherePredicate =
        when (wherePredicate) {
            is WherePredicate.LifetimePredicate -> visitPredicateLifetimeMut(wherePredicate)
            is WherePredicate.TypePredicate -> visitPredicateTypeMut(wherePredicate)
        }

    public open fun visitPredicateLifetimeMut(predicate: WherePredicate.LifetimePredicate): WherePredicate.LifetimePredicate =
        predicate.copy(
            lifetime = visitLifetimeMut(predicate.lifetime),
            bounds = predicate.bounds.copy({ visitLifetimeMut(it) }, { it }),
        )

    public open fun visitPredicateTypeMut(predicate: WherePredicate.TypePredicate): WherePredicate.TypePredicate =
        predicate.copy(
            lifetimes = predicate.lifetimes?.let { visitBoundLifetimesMut(it) },
            boundedTy = visitTypeMut(predicate.boundedTy),
            bounds = predicate.bounds.copy({ visitTypeParamBoundMut(it) }, { it }),
        )

    public open fun visitStmtMacroMut(stmt: Stmt.MacroStmt): Stmt.MacroStmt =
        stmt.copy(attrs = visitAttributesMut(stmt.attrs), mac = visitMacroMut(stmt.mac))

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
