// port-lint: source gen/fold.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.AssocConst
import io.github.kotlinmania.syn.AssocType
import io.github.kotlinmania.syn.AttrStyle
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
import io.github.kotlinmania.syn.File
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
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
 * AST fold -- traverses a syntax tree and returns rewritten nodes.
 *
 * Override methods to intercept and transform specific node types. Default
 * implementations recurse into sub-nodes and rebuild the original shape.
 */
public open class Fold {
    public open fun foldExpr(e: Expr): Expr =
        when (e) {
            is Expr.Array -> foldExprArray(e)
            is Expr.Assign -> foldExprAssign(e)
            is Expr.Async -> foldExprAsync(e)
            is Expr.Await -> foldExprAwait(e)
            is Expr.Binary -> foldExprBinary(e)
            is Expr.BlockExpr -> foldExprBlock(e)
            is Expr.Break -> foldExprBreak(e)
            is Expr.Call -> foldExprCall(e)
            is Expr.Cast -> foldExprCast(e)
            is Expr.Closure -> foldExprClosure(e)
            is Expr.Const -> foldExprConst(e)
            is Expr.Continue -> foldExprContinue(e)
            is Expr.Field -> foldExprField(e)
            is Expr.ForLoop -> foldExprForLoop(e)
            is Expr.Group -> foldExprGroup(e)
            is Expr.If -> foldExprIf(e)
            is Expr.Index -> foldExprIndex(e)
            is Expr.Infer -> foldExprInfer(e)
            is Expr.Let -> foldExprLet(e)
            is Expr.Lit -> foldExprLit(e)
            is Expr.Loop -> foldExprLoop(e)
            is Expr.Macro -> foldExprMacro(e)
            is Expr.Match -> foldExprMatch(e)
            is Expr.MethodCall -> foldExprMethodCall(e)
            is Expr.Paren -> foldExprParen(e)
            is Expr.Path -> foldExprPath(e)
            is Expr.Range -> foldExprRange(e)
            is Expr.RawAddr -> foldExprRawAddr(e)
            is Expr.Reference -> foldExprReference(e)
            is Expr.Repeat -> foldExprRepeat(e)
            is Expr.Return -> foldExprReturn(e)
            is Expr.Struct -> foldExprStruct(e)
            is Expr.Try -> foldExprTry(e)
            is Expr.TryBlock -> foldExprTryBlock(e)
            is Expr.Tuple -> foldExprTuple(e)
            is Expr.Unary -> foldExprUnary(e)
            is Expr.Unsafe -> foldExprUnsafe(e)
            is Expr.While -> foldExprWhile(e)
            is Expr.Yield -> foldExprYield(e)
            is Expr.Verbatim -> e.copy(tokens = foldTokenStream(e.tokens))
        }

    public open fun foldType(t: SynType): SynType =
        when (t) {
            is SynType.Array -> foldTypeArray(t)
            is SynType.BareFn -> foldTypeBareFn(t)
            is SynType.Group -> foldTypeGroup(t)
            is SynType.ImplTrait -> foldTypeImplTrait(t)
            is SynType.Infer -> foldTypeInfer(t)
            is SynType.Macro -> foldTypeMacro(t)
            is SynType.Never -> foldTypeNever(t)
            is SynType.Paren -> foldTypeParen(t)
            is SynType.Path -> foldTypePath(t)
            is SynType.Ptr -> foldTypePtr(t)
            is SynType.Reference -> foldTypeReference(t)
            is SynType.Slice -> foldTypeSlice(t)
            is SynType.TraitObject -> foldTypeTraitObject(t)
            is SynType.Tuple -> foldTypeTuple(t)
            is SynType.Verbatim -> {
                foldTokenStream(t.tokens)
                t
            }
        }

    public open fun foldPath(p: Path): Path =
        Path(
            leadingColon = p.leadingColon,
            segments = p.segments.copy({ foldPathSegment(it) }, { it }),
        )

    public open fun foldPat(p: Pat): Pat =
        when (p) {
            is Pat.Const -> p.copy(attrs = foldAttributes(p.attrs), block = foldBlock(p.block))
            is Pat.Ident -> foldPatIdent(p)
            is Pat.Lit -> p.copy(attrs = foldAttributes(p.attrs), lit = foldLit(p.lit))
            is Pat.Macro -> p.copy(attrs = foldAttributes(p.attrs), mac = foldMacro(p.mac))
            is Pat.Or -> foldPatOr(p)
            is Pat.PatParen -> foldPatParen(p)
            is Pat.Path -> p.copy(attrs = foldAttributes(p.attrs), qself = p.qself?.let { foldQSelf(it) }, path = foldPath(p.path))
            is Pat.Range -> foldPatRange(p)
            is Pat.Reference -> foldPatReference(p)
            is Pat.Rest -> foldPatRest(p)
            is Pat.Slice -> foldPatSlice(p)
            is Pat.Struct -> foldPatStruct(p)
            is Pat.Tuple -> foldPatTuple(p)
            is Pat.TupleStruct -> foldPatTupleStruct(p)
            is Pat.TypeAscription -> p.copy(attrs = foldAttributes(p.attrs), pat = foldPat(p.pat), ty = foldType(p.ty))
            is Pat.Wild -> foldPatWild(p)
            is Pat.Verbatim -> {
                foldTokenStream(p.tokens)
                p
            }
        }

    public open fun foldItem(i: Item): Item =
        when (i) {
            is Item.Const -> foldItemConst(i)
            is Item.Enum -> foldItemEnum(i)
            is Item.ExternCrate -> foldItemExternCrate(i)
            is Item.Fn -> foldItemFn(i)
            is Item.ForeignMod -> foldItemForeignMod(i)
            is Item.Impl -> foldItemImpl(i)
            is Item.Macro -> foldItemMacro(i)
            is Item.Mod -> foldItemMod(i)
            is Item.Static -> foldItemStatic(i)
            is Item.Struct -> foldItemStruct(i)
            is Item.Trait -> foldItemTrait(i)
            is Item.TraitAlias -> foldItemTraitAlias(i)
            is Item.ItemType -> foldItemType(i)
            is Item.Union -> foldItemUnion(i)
            is Item.Use -> foldItemUse(i)
            is Item.Verbatim -> {
                foldTokenStream(i.tokens)
                i
            }
        }

    public open fun foldAttribute(a: Attribute): Attribute =
        a.copy(style = foldAttrStyle(a.style), meta = foldMeta(a.meta))

    public open fun foldAttrStyle(s: AttrStyle): AttrStyle = s

    public open fun foldMeta(m: Meta): Meta =
        when (m) {
            is Meta.PathMeta -> m.copy(path = foldPath(m.path))
            is Meta.List -> foldMetaList(m)
            is Meta.NameValue -> foldMetaNameValue(m)
        }

    public open fun foldMetaList(m: Meta.List): Meta.List {
        foldMacroDelimiter(m.delimiter)
        foldTokenStream(m.tokens)
        return m.copy(path = foldPath(m.path))
    }

    public open fun foldMetaNameValue(m: Meta.NameValue): Meta.NameValue =
        m.copy(path = foldPath(m.path), value = foldExpr(m.value))

    public open fun foldGenerics(g: Generics): Generics =
        Generics(
            ltToken = g.ltToken,
            params = g.params.copy({ foldGenericParam(it) }, { it }),
            gtToken = g.gtToken,
            whereClause = g.whereClause?.let { foldWhereClause(it) },
        )

    public open fun foldLit(l: Lit): Lit =
        when (l) {
            is Lit.Str -> Lit.Str(foldLitStr(l.value))
            is Lit.ByteStr -> Lit.ByteStr(foldLitByteStr(l.value))
            is Lit.CStr -> Lit.CStr(foldLitCStr(l.value))
            is Lit.Byte -> Lit.Byte(foldLitByte(l.value))
            is Lit.Char -> Lit.Char(foldLitChar(l.value))
            is Lit.Int -> Lit.Int(foldLitInt(l.value))
            is Lit.Float -> Lit.Float(foldLitFloat(l.value))
            is Lit.Bool -> Lit.Bool(foldLitBool(l.value))
            is Lit.Verbatim -> l
        }

    public open fun foldLitBool(l: LitBool): LitBool {
        foldSpan(l.span())
        return l
    }

    public open fun foldLitByte(l: LitByte): LitByte = l

    public open fun foldLitByteStr(l: LitByteStr): LitByteStr = l

    public open fun foldLitCStr(l: LitCStr): LitCStr = l

    public open fun foldLitChar(l: LitChar): LitChar = l

    public open fun foldLitFloat(l: LitFloat): LitFloat = l

    public open fun foldLitInt(l: LitInt): LitInt = l

    public open fun foldLitStr(l: LitStr): LitStr = l

    public open fun foldLifetime(lt: Lifetime): Lifetime =
        lt.copy(apostrophe = foldSpan(lt.apostrophe), ident = foldIdent(lt.ident))

    public open fun foldIdent(id: Ident): Ident {
        foldSpan(id.span())
        return id
    }

    public open fun foldStmt(s: Stmt): Stmt =
        when (s) {
            is Stmt.Local -> foldLocal(s)
            is Stmt.ItemStmt -> s.copy(item = foldItem(s.item))
            is Stmt.ExprStmt -> s.copy(expr = foldExpr(s.expr))
            is Stmt.MacroStmt -> foldStmtMacro(s)
        }

    public open fun foldData(d: Data): Data =
        when (d) {
            is Data.Struct -> Data.Struct(foldDataStruct(d.value))
            is Data.Enum -> Data.Enum(foldDataEnum(d.value))
            is Data.Union -> Data.Union(foldDataUnion(d.value))
        }

    public open fun foldDataEnum(d: DataEnum): DataEnum =
        d.copy(variants = d.variants.copy({ foldVariant(it) }, { it }))

    public open fun foldDataStruct(d: DataStruct): DataStruct =
        d.copy(fields = foldFields(d.fields))

    public open fun foldDataUnion(d: DataUnion): DataUnion =
        d.copy(fields = foldFieldsNamed(d.fields))

    public open fun foldDeriveInput(di: DeriveInput): DeriveInput =
        di.copy(
            attrs = foldAttributes(di.attrs),
            vis = foldVisibility(di.vis),
            ident = foldIdent(di.ident),
            generics = foldGenerics(di.generics),
            data = foldData(di.data),
        )

    public open fun foldBlock(block: Block): Block = block.copy(stmts = block.stmts.map { foldStmt(it) })

    public open fun foldAttributes(attrs: List<Attribute>): List<Attribute> = attrs.map { foldAttribute(it) }

    public open fun foldFile(file: File): File =
        file.copy(attrs = foldAttributes(file.attrs), items = file.items.map { foldItem(it) })

    public open fun foldSignature(sig: Signature): Signature {
        var result = sig
        result =
            result.copy(
                abi = result.abi?.let { foldAbi(it) },
                ident = foldIdent(result.ident),
                generics = foldGenerics(result.generics),
                inputs = result.inputs.copy({ foldFnArg(it) }, { it }),
                variadic = result.variadic?.let { foldVariadic(it) },
                output = foldReturnType(result.output),
            )
        return result
    }

    public open fun foldAbi(a: Abi): Abi = a.copy(name = a.name?.let { foldLitStr(it) })

    public open fun foldReturnType(rt: ReturnType): ReturnType =
        when (rt) {
            is ReturnType.Default -> rt
            is ReturnType.TypeReturn -> rt.copy(ty = foldType(rt.ty))
        }

    public open fun foldFnArg(arg: FnArg): FnArg =
        when (arg) {
            is FnArg.Receiver -> foldReceiver(arg)
            is FnArg.Typed -> arg.copy(patType = foldPatType(arg.patType))
        }

    public open fun foldReceiver(receiver: FnArg.Receiver): FnArg.Receiver =
        receiver.copy(
            attrs = foldAttributes(receiver.attrs),
            reference = receiver.reference?.copy(lifetime = receiver.reference.lifetime?.let { foldLifetime(it) }),
            `type` = foldType(receiver.type),
        )

    public open fun foldPatType(patType: PatType): PatType = patType.copy(pat = foldPat(patType.pat), ty = foldType(patType.ty))

    public open fun foldPatIdent(patIdent: Pat.Ident): Pat.Ident =
        patIdent.copy(
            attrs = foldAttributes(patIdent.attrs),
            ident = foldIdent(patIdent.ident),
            subpat = patIdent.subpat?.let { foldPat(it) },
        )

    public open fun foldPatOr(pat: Pat.Or): Pat.Or =
        pat.copy(attrs = foldAttributes(pat.attrs), cases = pat.cases.copy({ foldPat(it) }, { it }))

    public open fun foldPatParen(pat: Pat.PatParen): Pat.PatParen =
        pat.copy(attrs = foldAttributes(pat.attrs), pat = foldPat(pat.pat))

    public open fun foldPatRange(pat: Pat.Range): Pat.Range =
        pat.copy(
            attrs = foldAttributes(pat.attrs),
            start = pat.start?.let { foldExpr(it) },
            limits = foldRangeLimits(pat.limits),
            end = pat.end?.let { foldExpr(it) },
        )

    public open fun foldPatReference(pat: Pat.Reference): Pat.Reference =
        pat.copy(attrs = foldAttributes(pat.attrs), pat = foldPat(pat.pat))

    public open fun foldPatRest(pat: Pat.Rest): Pat.Rest =
        pat.copy(attrs = foldAttributes(pat.attrs))

    public open fun foldPatRest(patRest: PatRest): PatRest =
        patRest.copy(attrs = foldAttributes(patRest.attrs))

    public open fun foldPatSlice(pat: Pat.Slice): Pat.Slice =
        pat.copy(attrs = foldAttributes(pat.attrs), elems = pat.elems.copy({ foldPat(it) }, { it }))

    public open fun foldPatStruct(pat: Pat.Struct): Pat.Struct =
        pat.copy(
            attrs = foldAttributes(pat.attrs),
            qself = pat.qself?.let { foldQSelf(it) },
            path = foldPath(pat.path),
            fields = pat.fields.copy({ foldFieldPat(it) }, { it }),
            rest = pat.rest?.let { foldPatRest(it) },
        )

    public open fun foldPatTuple(pat: Pat.Tuple): Pat.Tuple =
        pat.copy(attrs = foldAttributes(pat.attrs), elems = pat.elems.copy({ foldPat(it) }, { it }))

    public open fun foldPatTupleStruct(pat: Pat.TupleStruct): Pat.TupleStruct =
        pat.copy(
            attrs = foldAttributes(pat.attrs),
            qself = pat.qself?.let { foldQSelf(it) },
            path = foldPath(pat.path),
            elems = pat.elems.copy({ foldPat(it) }, { it }),
        )

    public open fun foldPatWild(pat: Pat.Wild): Pat.Wild =
        pat.copy(attrs = foldAttributes(pat.attrs))

    public open fun foldTypePath(typePath: SynType.Path): SynType.Path =
        typePath.copy(qself = typePath.qself?.let { foldQSelf(it) }, path = foldPath(typePath.path))

    public open fun foldTypeArray(ty: SynType.Array): SynType.Array =
        ty.copy(elem = foldType(ty.elem), len = foldExpr(ty.len))

    public open fun foldTypeGroup(ty: SynType.Group): SynType.Group =
        ty.copy(elem = foldType(ty.elem))

    public open fun foldTypeReference(ty: SynType.Reference): SynType.Reference = ty.copy(lifetime = ty.lifetime?.let { foldLifetime(it) }, elem = foldType(ty.elem))

    public open fun foldTypeImplTrait(ty: SynType.ImplTrait): SynType.ImplTrait = ty.copy(bounds = ty.bounds.copy({ foldTypeParamBound(it) }, { it }))

    public open fun foldTypeInfer(ty: SynType.Infer): SynType.Infer = ty

    public open fun foldTypeMacro(ty: SynType.Macro): SynType.Macro =
        ty.copy(mac = foldMacro(ty.mac))

    public open fun foldTypeNever(ty: SynType.Never): SynType.Never = ty

    public open fun foldTypePtr(ty: SynType.Ptr): SynType.Ptr =
        ty.copy(elem = foldType(ty.elem))

    public open fun foldTypeSlice(ty: SynType.Slice): SynType.Slice =
        ty.copy(elem = foldType(ty.elem))

    public open fun foldTypeTraitObject(ty: SynType.TraitObject): SynType.TraitObject =
        ty.copy(bounds = ty.bounds.copy({ foldTypeParamBound(it) }, { it }))

    public open fun foldTypeTuple(ty: SynType.Tuple): SynType.Tuple =
        ty.copy(elems = ty.elems.copy({ foldType(it) }, { it }))

    public open fun foldPointerMutability(mutability: PointerMutability): PointerMutability =
        when (mutability) {
            is PointerMutability.Const -> mutability
            is PointerMutability.Mut -> mutability
        }

    public open fun foldTypeBareFn(ty: SynType.BareFn): SynType.BareFn =
        ty.copy(
            lifetimes = ty.lifetimes?.let { foldBoundLifetimes(it) },
            abi = ty.abi?.let { foldAbi(it) },
            inputs = ty.inputs.copy({ foldBareFnArg(it) }, { it }),
            variadic = ty.variadic?.let { foldBareVariadic(it) },
            output = foldReturnType(ty.output),
        )

    public open fun foldBareFnArg(arg: BareFnArg): BareFnArg =
        arg.copy(
            attrs = foldAttributes(arg.attrs),
            name = arg.name?.copy(ident = foldIdent(arg.name.ident)),
            ty = foldType(arg.ty),
        )

    public open fun foldBareVariadic(variadic: BareVariadic): BareVariadic =
        variadic.copy(
            attrs = foldAttributes(variadic.attrs),
            name = variadic.name?.copy(ident = foldIdent(variadic.name.ident)),
        )

    public open fun foldTypeParen(ty: SynType.Paren): SynType.Paren = ty.copy(elem = foldType(ty.elem))

    public open fun foldExprArray(expr: Expr.Array): Expr.Array =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            elems = expr.elems.copy({ foldExpr(it) }, { it }),
        )

    public open fun foldExprAssign(expr: Expr.Assign): Expr.Assign =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            left = foldExpr(expr.left),
            right = foldExpr(expr.right),
        )

    public open fun foldExprAsync(expr: Expr.Async): Expr.Async =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            block = foldBlock(expr.block),
        )

    public open fun foldExprAwait(expr: Expr.Await): Expr.Await =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            base = foldExpr(expr.base),
        )

    public open fun foldExprBinary(expr: Expr.Binary): Expr.Binary =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            left = foldExpr(expr.left),
            op = foldBinOp(expr.op),
            right = foldExpr(expr.right),
        )

    public open fun foldExprBlock(expr: Expr.BlockExpr): Expr.BlockExpr =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            label = expr.label?.let { foldLabel(it) },
            block = foldBlock(expr.block),
        )

    public open fun foldExprBreak(expr: Expr.Break): Expr.Break =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            label = expr.label?.let { foldLifetime(it) },
            expr = expr.expr?.let { foldExpr(it) },
        )

    public open fun foldExprCall(expr: Expr.Call): Expr.Call =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            func = foldExpr(expr.func),
            args = expr.args.copy({ foldExpr(it) }, { it }),
        )

    public open fun foldExprCast(expr: Expr.Cast): Expr.Cast =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
            ty = foldType(expr.ty),
        )

    public open fun foldExprClosure(expr: Expr.Closure): Expr.Closure =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            inputs = expr.inputs.copy({ foldPat(it) }, { it }),
            output = foldReturnType(expr.output),
            body = foldExpr(expr.body),
        )

    public open fun foldExprConst(expr: Expr.Const): Expr.Const =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            block = foldBlock(expr.block),
        )

    public open fun foldExprContinue(expr: Expr.Continue): Expr.Continue =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            label = expr.label?.let { foldLifetime(it) },
        )

    public open fun foldExprField(expr: Expr.Field): Expr.Field =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            base = foldExpr(expr.base),
            member = foldMember(expr.member),
        )

    public open fun foldExprForLoop(expr: Expr.ForLoop): Expr.ForLoop =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            label = expr.label?.let { foldLabel(it) },
            pat = foldPat(expr.pat),
            expr = foldExpr(expr.expr),
            body = foldBlock(expr.body),
        )

    public open fun foldExprGroup(expr: Expr.Group): Expr.Group =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
        )

    public open fun foldExprIf(expr: Expr.If): Expr.If =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            cond = foldExpr(expr.cond),
            thenBranch = foldBlock(expr.thenBranch),
            elseBranch = expr.elseBranch?.let { foldElseExpr(it) },
        )

    public open fun foldExprIndex(expr: Expr.Index): Expr.Index =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
            index = foldExpr(expr.index),
        )

    public open fun foldExprInfer(expr: Expr.Infer): Expr.Infer =
        expr.copy(attrs = foldAttributes(expr.attrs))

    public open fun foldExprLet(expr: Expr.Let): Expr.Let =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            pat = foldPat(expr.pat),
            expr = foldExpr(expr.expr),
        )

    public open fun foldExprLit(expr: Expr.Lit): Expr.Lit =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            lit = foldLit(expr.lit),
        )

    public open fun foldExprLoop(expr: Expr.Loop): Expr.Loop =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            label = expr.label?.let { foldLabel(it) },
            body = foldBlock(expr.body),
        )

    public open fun foldExprMacro(expr: Expr.Macro): Expr.Macro =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            mac = foldMacro(expr.mac),
        )

    public open fun foldExprMatch(expr: Expr.Match): Expr.Match =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
            arms = expr.arms.map { foldArm(it) },
        )

    public open fun foldExprMethodCall(expr: Expr.MethodCall): Expr.MethodCall =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            receiver = foldExpr(expr.receiver),
            method = foldIdent(expr.method),
            turbofish = expr.turbofish?.let { foldAngleBracketedGenericArguments(it) },
            args = expr.args.copy({ foldExpr(it) }, { it }),
        )

    public open fun foldExprParen(expr: Expr.Paren): Expr.Paren =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
        )

    public open fun foldExprPath(exprPath: Expr.Path): Expr.Path =
        exprPath.copy(
            attrs = foldAttributes(exprPath.attrs),
            qself = exprPath.qself?.let { foldQSelf(it) },
            path = foldPath(exprPath.path),
        )

    public open fun foldExprRange(expr: Expr.Range): Expr.Range =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            start = expr.start?.let { foldExpr(it) },
            limits = foldRangeLimits(expr.limits),
            end = expr.end?.let { foldExpr(it) },
        )

    public open fun foldExprRawAddr(expr: Expr.RawAddr): Expr.RawAddr =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            mutability = foldPointerMutability(expr.mutability),
            expr = foldExpr(expr.expr),
        )

    public open fun foldExprReference(expr: Expr.Reference): Expr.Reference =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
        )

    public open fun foldExprRepeat(expr: Expr.Repeat): Expr.Repeat =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
            len = foldExpr(expr.len),
        )

    public open fun foldExprReturn(expr: Expr.Return): Expr.Return =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = expr.expr?.let { foldExpr(it) },
        )

    public open fun foldExprStruct(expr: Expr.Struct): Expr.Struct =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            qself = expr.qself?.let { foldQSelf(it) },
            path = foldPath(expr.path),
            fields = expr.fields.copy({ foldFieldValue(it) }, { it }),
            rest = expr.rest?.let { foldExpr(it) },
        )

    public open fun foldExprTry(expr: Expr.Try): Expr.Try =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = foldExpr(expr.expr),
        )

    public open fun foldExprTryBlock(expr: Expr.TryBlock): Expr.TryBlock =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            block = foldBlock(expr.block),
        )

    public open fun foldExprTuple(expr: Expr.Tuple): Expr.Tuple =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            elems = expr.elems.copy({ foldExpr(it) }, { it }),
        )

    public open fun foldExprUnary(expr: Expr.Unary): Expr.Unary =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            op = foldUnOp(expr.op),
            expr = foldExpr(expr.expr),
        )

    public open fun foldExprUnsafe(expr: Expr.Unsafe): Expr.Unsafe =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            block = foldBlock(expr.block),
        )

    public open fun foldExprWhile(expr: Expr.While): Expr.While =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            label = expr.label?.let { foldLabel(it) },
            cond = foldExpr(expr.cond),
            body = foldBlock(expr.body),
        )

    public open fun foldExprYield(expr: Expr.Yield): Expr.Yield =
        expr.copy(
            attrs = foldAttributes(expr.attrs),
            expr = expr.expr?.let { foldExpr(it) },
        )

    public open fun foldBinOp(op: BinOp): BinOp =
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

    public open fun foldUnOp(op: UnOp): UnOp =
        when (op) {
            is UnOp.Deref -> op
            is UnOp.NotOp -> op
            is UnOp.Neg -> op
        }

    public open fun foldLabel(label: Label): Label =
        label.copy(name = foldLifetime(label.name))

    public open fun foldRangeLimits(limits: RangeLimits): RangeLimits =
        when (limits) {
            is RangeLimits.HalfOpen -> limits
            is RangeLimits.Closed -> limits
        }

    public open fun foldMacro(mac: Macro): Macro =
        mac.copy(path = foldPath(mac.path), delimiter = foldMacroDelimiter(mac.delimiter), tokens = foldTokenStream(mac.tokens))

    public open fun foldMacroDelimiter(delimiter: MacroDelimiter): MacroDelimiter = delimiter

    public open fun foldPathArguments(pathArgs: PathArguments): PathArguments =
        when (pathArgs) {
            is PathArguments.None -> pathArgs
            is PathArguments.AngleBracketed -> foldAngleBracketedGenericArguments(pathArgs)
            is PathArguments.Parenthesized -> foldParenthesizedGenericArguments(pathArgs)
        }

    public open fun foldAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed =
        pathArgs.copy(args = pathArgs.args.copy({ foldGenericArgument(it) }, { it }))

    public open fun foldParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized =
        pathArgs.copy(
            inputs = pathArgs.inputs.copy({ foldType(it) }, { it }),
            output = foldReturnType(pathArgs.output),
        )

    public open fun foldGenericArgument(genArg: GenericArgument): GenericArgument =
        when (genArg) {
            is GenericArgument.LifetimeArg -> genArg.copy(lifetime = foldLifetime(genArg.lifetime))
            is GenericArgument.TypeArg -> genArg.copy(type = foldType(genArg.type))
            is GenericArgument.ConstArg -> genArg.copy(expr = foldExpr(genArg.expr))
            is GenericArgument.AssocTypeArg -> genArg.copy(assoc = foldAssocType(genArg.assoc))
            is GenericArgument.AssocConstArg -> genArg.copy(assoc = foldAssocConst(genArg.assoc))
            is GenericArgument.ConstraintArg -> genArg.copy(constraint = foldConstraint(genArg.constraint))
        }

    public open fun foldAssocConst(assoc: AssocConst): AssocConst =
        assoc.copy(
            ident = foldIdent(assoc.ident),
            generics = assoc.generics?.let { foldAngleBracketedGenericArguments(it) },
            value = foldExpr(assoc.value),
        )

    public open fun foldAssocType(assoc: AssocType): AssocType =
        assoc.copy(
            ident = foldIdent(assoc.ident),
            generics = assoc.generics?.let { foldAngleBracketedGenericArguments(it) },
            ty = foldType(assoc.ty),
        )

    public open fun foldConstraint(constraint: Constraint): Constraint =
        constraint.copy(
            ident = foldIdent(constraint.ident),
            generics = constraint.generics?.let { foldAngleBracketedGenericArguments(it) },
            bounds = constraint.bounds.copy({ foldTypeParamBound(it) }, { it }),
        )

    public open fun foldTypeParamBound(bound: TypeParamBound): TypeParamBound =
        when (bound) {
            is TypeParamBound.Trait -> foldTraitBound(bound)
            is TypeParamBound.LifetimeBound -> bound.copy(lifetime = foldLifetime(bound.lifetime))
            is TypeParamBound.PreciseCapture -> foldPreciseCapture(bound)
            is TypeParamBound.Verbatim -> {
                foldTokenStream(bound.tokens)
                bound
            }
        }

    public open fun foldTraitBound(bound: TypeParamBound.Trait): TypeParamBound.Trait =
        bound.copy(
            modifier = foldTraitBoundModifier(bound.modifier),
            lifetimes = bound.lifetimes?.let { foldBoundLifetimes(it) },
            path = foldPath(bound.path),
        )

    public open fun foldTraitBoundModifier(modifier: TraitBoundModifier): TraitBoundModifier = modifier

    public open fun foldBoundLifetimes(boundLifetimes: BoundLifetimes): BoundLifetimes =
        boundLifetimes.copy(lifetimes = boundLifetimes.lifetimes.copy({ foldGenericParam(it) }, { it }))

    public open fun foldCapturedParam(param: CapturedParam): CapturedParam =
        when (param) {
            is CapturedParam.Lifetime -> param.copy(lifetime = foldLifetime(param.lifetime))
            is CapturedParam.Ident -> param.copy(ident = foldIdent(param.ident))
        }

    public open fun foldPreciseCapture(preciseCapture: TypeParamBound.PreciseCapture): TypeParamBound.PreciseCapture =
        preciseCapture.copy(params = preciseCapture.params.copy({ foldCapturedParam(it) }, { it }))

    public open fun foldPathSegment(segment: PathSegment): PathSegment =
        segment.copy(ident = foldIdent(segment.ident), arguments = foldPathArguments(segment.arguments))

    public open fun foldArm(arm: Arm): Arm =
        arm.copy(
            attrs = foldAttributes(arm.attrs),
            pat = foldPat(arm.pat),
            guard = arm.guard?.copy(expr = foldExpr(arm.guard.expr)),
            body = foldExpr(arm.body),
        )

    public open fun foldElseExpr(elseExpr: ElseExpr): ElseExpr =
        elseExpr.copy(expr = foldExpr(elseExpr.expr))

    public open fun foldFieldPat(fieldPat: FieldPat): FieldPat =
        fieldPat.copy(
            attrs = foldAttributes(fieldPat.attrs),
            member = foldMember(fieldPat.member),
            pat = foldPat(fieldPat.pat),
        )

    public open fun foldFieldValue(fieldValue: FieldValue): FieldValue =
        fieldValue.copy(
            attrs = foldAttributes(fieldValue.attrs),
            member = foldMember(fieldValue.member),
            expr = foldExpr(fieldValue.expr),
        )

    public open fun foldGenericParam(param: GenericParam): GenericParam =
        when (param) {
            is GenericParam.LifetimeParam -> foldLifetimeParam(param)
            is GenericParam.TypeParam -> foldTypeParam(param)
            is GenericParam.ConstParam -> foldConstParam(param)
        }

    public open fun foldConstParam(param: GenericParam.ConstParam): GenericParam.ConstParam =
        param.copy(
            attrs = foldAttributes(param.attrs),
            ident = foldIdent(param.ident),
            ty = foldType(param.ty),
            default = param.default?.let { foldExpr(it) },
        )

    public open fun foldLifetimeParam(param: GenericParam.LifetimeParam): GenericParam.LifetimeParam =
        param.copy(
            attrs = foldAttributes(param.attrs),
            lifetime = foldLifetime(param.lifetime),
            bounds = param.bounds.copy({ foldLifetime(it) }, { it }),
        )

    public open fun foldTypeParam(param: GenericParam.TypeParam): GenericParam.TypeParam =
        param.copy(
            attrs = foldAttributes(param.attrs),
            ident = foldIdent(param.ident),
            bounds = param.bounds.copy({ foldTypeParamBound(it) }, { it }),
            default = param.default?.let { foldType(it) },
        )

    public open fun foldField(field: Field): Field =
        field.copy(
            attrs = foldAttributes(field.attrs),
            vis = foldVisibility(field.vis),
            mutability = foldFieldMutability(field.mutability),
            ident = field.ident?.let { foldIdent(it) },
            ty = foldType(field.ty),
        )

    public open fun foldFieldMutability(fieldMutability: FieldMutability): FieldMutability = fieldMutability

    public open fun foldFields(fields: Fields): Fields =
        when (fields) {
            is Fields.Named -> Fields.Named(foldFieldsNamed(fields.fields))
            is Fields.Unnamed -> Fields.Unnamed(foldFieldsUnnamed(fields.fields))
            Fields.Unit -> fields
        }

    public open fun foldFieldsNamed(fields: FieldsNamed): FieldsNamed =
        fields.copy(named = fields.named.copy({ foldField(it) }, { it }))

    public open fun foldFieldsUnnamed(fields: FieldsUnnamed): FieldsUnnamed =
        fields.copy(unnamed = fields.unnamed.copy({ foldField(it) }, { it }))

    public open fun foldImplItem(item: ImplItem): ImplItem =
        when (item) {
            is ImplItem.Const -> foldImplItemConst(item)
            is ImplItem.Fn -> foldImplItemFn(item)
            is ImplItem.AssocType -> foldImplItemType(item)
            is ImplItem.Macro -> foldImplItemMacro(item)
            is ImplItem.Verbatim -> {
                foldTokenStream(item.tokens)
                item
            }
        }

    public open fun foldForeignItem(item: ForeignItem): ForeignItem =
        when (item) {
            is ForeignItem.Fn -> foldForeignItemFn(item)
            is ForeignItem.Static -> foldForeignItemStatic(item)
            is ForeignItem.ItemType -> foldForeignItemType(item)
            is ForeignItem.Macro -> foldForeignItemMacro(item)
            is ForeignItem.Verbatim -> ForeignItem.Verbatim(foldTokenStream(item.tokens))
        }

    public open fun foldForeignItemFn(item: ForeignItem.Fn): ForeignItem.Fn =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            sig = foldSignature(item.sig),
        )

    public open fun foldForeignItemMacro(item: ForeignItem.Macro): ForeignItem.Macro =
        item.copy(attrs = foldAttributes(item.attrs), mac = foldMacro(item.mac))

    public open fun foldForeignItemStatic(item: ForeignItem.Static): ForeignItem.Static =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            mutability = foldStaticMutability(item.mutability),
            ident = foldIdent(item.ident),
            ty = foldType(item.ty),
        )

    public open fun foldForeignItemType(item: ForeignItem.ItemType): ForeignItem.ItemType =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
        )

    public open fun foldImplItemConst(item: ImplItem.Const): ImplItem.Const =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            ty = foldType(item.ty),
            expr = foldExpr(item.expr),
        )

    public open fun foldImplItemFn(item: ImplItem.Fn): ImplItem.Fn =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            sig = foldSignature(item.sig),
            block = foldBlock(item.block),
        )

    public open fun foldImplItemMacro(item: ImplItem.Macro): ImplItem.Macro =
        item.copy(attrs = foldAttributes(item.attrs), mac = foldMacro(item.mac))

    public open fun foldImplItemType(item: ImplItem.AssocType): ImplItem.AssocType =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            ty = foldType(item.ty),
        )

    public open fun foldItemConst(item: Item.Const): Item.Const =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            ty = foldType(item.ty),
            expr = item.expr?.let { foldExpr(it) },
        )

    public open fun foldItemEnum(item: Item.Enum): Item.Enum =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            variants = item.variants.copy({ foldVariant(it) }, { it }),
        )

    public open fun foldItemExternCrate(item: Item.ExternCrate): Item.ExternCrate =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            rename = item.rename?.let { it.copy(ident = foldIdent(it.ident)) },
        )

    public open fun foldItemFn(item: Item.Fn): Item.Fn =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            sig = foldSignature(item.sig),
            block = item.block?.let { foldBlock(it) },
        )

    public open fun foldItemForeignMod(item: Item.ForeignMod): Item.ForeignMod =
        item.copy(
            attrs = foldAttributes(item.attrs),
            abi = foldAbi(item.abi),
            items = item.items.map { foldForeignItem(it) },
        )

    public open fun foldItemImpl(item: Item.Impl): Item.Impl =
        item.copy(
            attrs = foldAttributes(item.attrs),
            generics = foldGenerics(item.generics),
            traitPath = item.traitPath?.let { foldPathTrait(it) },
            selfType = foldType(item.selfType),
            items = item.items.map { foldImplItem(it) },
        )

    public open fun foldItemMacro(item: Item.Macro): Item.Macro =
        item.copy(
            attrs = foldAttributes(item.attrs),
            ident = item.ident?.let { foldIdent(it) },
            mac = foldMacro(item.mac),
        )

    public open fun foldItemMod(item: Item.Mod): Item.Mod =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            content = item.content?.let { foldModContent(it) },
        )

    public open fun foldItemStatic(item: Item.Static): Item.Static =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            mutability = foldStaticMutability(item.mutability),
            ident = foldIdent(item.ident),
            ty = foldType(item.ty),
            expr = foldExpr(item.expr),
        )

    public open fun foldItemStruct(item: Item.Struct): Item.Struct =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            fields = foldFields(item.fields),
        )

    public open fun foldItemTrait(item: Item.Trait): Item.Trait =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            restriction = item.restriction?.let { foldImplRestriction(it) },
            supertraits = item.supertraits.copy({ foldTypeParamBound(it) }, { it }),
            items = item.items.map { foldTraitItem(it) },
        )

    public open fun foldItemTraitAlias(item: Item.TraitAlias): Item.TraitAlias =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            bounds = item.bounds.copy({ foldTypeParamBound(it) }, { it }),
        )

    public open fun foldItemType(item: Item.ItemType): Item.ItemType =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            ty = foldType(item.ty),
        )

    public open fun foldItemUnion(item: Item.Union): Item.Union =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            fields = foldFieldsNamed(item.fields),
        )

    public open fun foldItemUse(item: Item.Use): Item.Use =
        item.copy(
            attrs = foldAttributes(item.attrs),
            vis = foldVisibility(item.vis),
            tree = foldUseTree(item.tree),
        )

    public open fun foldStaticMutability(mutability: StaticMutability): StaticMutability = mutability

    public open fun foldImplRestriction(restriction: ImplRestriction): ImplRestriction = restriction

    public open fun foldModContent(modContent: ModContent): ModContent =
        when (modContent) {
            is ModContent.Inline -> modContent.copy(items = modContent.items.map { foldItem(it) })
            is ModContent.Unnamed -> modContent
        }

    public open fun foldLocal(local: Stmt.Local): Stmt.Local =
        local.copy(
            attrs = foldAttributes(local.attrs),
            pat = foldPat(local.pat),
            init = local.init?.let { foldLocalInit(it) },
        )

    public open fun foldLocalInit(init: LocalInit): LocalInit =
        init.copy(
            expr = foldExpr(init.expr),
            diverge = init.diverge?.let { foldElseExpr(it) },
        )

    public open fun foldStmtMacro(stmt: Stmt.MacroStmt): Stmt.MacroStmt =
        stmt.copy(attrs = foldAttributes(stmt.attrs), mac = foldMacro(stmt.mac))

    public open fun foldMember(member: Member): Member =
        when (member) {
            is Member.Named -> member.copy(ident = foldIdent(member.ident))
            is Member.Unnamed -> member.copy(index = foldIndex(member.index))
        }

    public open fun foldIndex(index: Index): Index =
        index.copy(span = foldSpan(index.span))

    public open fun foldQSelf(qself: QSelf): QSelf =
        qself.copy(ty = foldType(qself.ty))

    public open fun foldPathTrait(pathTrait: PathTrait): PathTrait =
        pathTrait.copy(path = foldPath(pathTrait.path))

    public open fun foldTraitItem(item: TraitItem): TraitItem =
        when (item) {
            is TraitItem.Const -> foldTraitItemConst(item)
            is TraitItem.Fn -> foldTraitItemFn(item)
            is TraitItem.AssocType -> foldTraitItemType(item)
            is TraitItem.Macro -> foldTraitItemMacro(item)
            is TraitItem.Verbatim -> {
                foldTokenStream(item.tokens)
                item
            }
        }

    public open fun foldTraitItemConst(item: TraitItem.Const): TraitItem.Const =
        item.copy(
            attrs = foldAttributes(item.attrs),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            ty = foldType(item.ty),
            default = item.default?.copy(expr = foldExpr(item.default.expr)),
        )

    public open fun foldTraitItemFn(item: TraitItem.Fn): TraitItem.Fn =
        item.copy(
            attrs = foldAttributes(item.attrs),
            sig = foldSignature(item.sig),
            default = item.default?.let { foldBlock(it) },
        )

    public open fun foldTraitItemMacro(item: TraitItem.Macro): TraitItem.Macro =
        item.copy(attrs = foldAttributes(item.attrs), mac = foldMacro(item.mac))

    public open fun foldTraitItemType(item: TraitItem.AssocType): TraitItem.AssocType =
        item.copy(
            attrs = foldAttributes(item.attrs),
            ident = foldIdent(item.ident),
            generics = foldGenerics(item.generics),
            bounds = item.bounds.copy({ foldTypeParamBound(it) }, { it }),
            default = item.default?.copy(type = foldType(item.default.type)),
        )

    public open fun foldUseTree(useTree: UseTree): UseTree =
        when (useTree) {
            is UseTree.Path -> foldUsePath(useTree)
            is UseTree.Name ->
                if (useTree.rename == null) {
                    foldUseName(useTree)
                } else {
                    foldUseRename(useTree)
                }
            is UseTree.Group -> foldUseGroup(useTree)
            is UseTree.Glob -> foldUseGlob(useTree)
        }

    public open fun foldUseGlob(useTree: UseTree.Glob): UseTree.Glob = useTree

    public open fun foldUseGroup(useTree: UseTree.Group): UseTree.Group =
        useTree.copy(items = useTree.items.copy({ foldUseTree(it) }, { it }))

    public open fun foldUseName(useTree: UseTree.Name): UseTree.Name =
        useTree.copy(ident = foldIdent(useTree.ident))

    public open fun foldUsePath(useTree: UseTree.Path): UseTree.Path =
        useTree.copy(
            ident = foldIdent(useTree.ident),
            tree = useTree.tree?.let { foldUseTree(it) },
        )

    public open fun foldUseRename(useTree: UseTree.Name): UseTree.Name =
        useTree.copy(
            ident = foldIdent(useTree.ident),
            rename = useTree.rename?.copy(ident = foldIdent(useTree.rename.ident)),
        )

    public open fun foldVariadic(variadic: Variadic): Variadic =
        variadic.copy(
            attrs = foldAttributes(variadic.attrs),
            pat = variadic.pat?.copy(pat = foldPat(variadic.pat.pat)),
        )

    public open fun foldVariant(variant: Variant): Variant =
        variant.copy(
            attrs = foldAttributes(variant.attrs),
            ident = foldIdent(variant.ident),
            fields = foldFields(variant.fields),
            discriminant = variant.discriminant?.copy(expr = foldExpr(variant.discriminant.expr)),
        )

    public open fun foldVisibility(visibility: Visibility): Visibility =
        when (visibility) {
            is Visibility.Public -> visibility
            is Visibility.Restricted -> foldVisRestricted(visibility)
            Visibility.Inherited -> visibility
        }

    public open fun foldVisRestricted(visibility: Visibility.Restricted): Visibility.Restricted =
        visibility.copy(path = foldPath(visibility.path))

    public open fun foldWhereClause(whereClause: WhereClause): WhereClause =
        whereClause.copy(predicates = whereClause.predicates.copy({ foldWherePredicate(it) }, { it }))

    public open fun foldWherePredicate(wherePredicate: WherePredicate): WherePredicate =
        when (wherePredicate) {
            is WherePredicate.LifetimePredicate -> foldPredicateLifetime(wherePredicate)
            is WherePredicate.TypePredicate -> foldPredicateType(wherePredicate)
        }

    public open fun foldPredicateLifetime(predicate: WherePredicate.LifetimePredicate): WherePredicate.LifetimePredicate =
        predicate.copy(
            lifetime = foldLifetime(predicate.lifetime),
            bounds = predicate.bounds.copy({ foldLifetime(it) }, { it }),
        )

    public open fun foldPredicateType(predicate: WherePredicate.TypePredicate): WherePredicate.TypePredicate =
        predicate.copy(
            lifetimes = predicate.lifetimes?.let { foldBoundLifetimes(it) },
            boundedTy = foldType(predicate.boundedTy),
            bounds = predicate.bounds.copy({ foldTypeParamBound(it) }, { it }),
        )

    public open fun foldSpan(span: Span): Span = span

    public open fun foldTokenStream(tokens: TokenStream): TokenStream = tokens
}
