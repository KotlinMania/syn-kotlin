// port-lint: source gen/visit_mut.rs
package io.github.kotlinmania.syn.gen

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
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
import io.github.kotlinmania.syn.File
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.ImplItem
import io.github.kotlinmania.syn.Item
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.LocalInit
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.Member
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.ModContent
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.PathTrait
import io.github.kotlinmania.syn.PointerMutability
import io.github.kotlinmania.syn.QSelf
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.StaticMutability
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.TraitBoundModifier
import io.github.kotlinmania.syn.TraitItem
import io.github.kotlinmania.syn.TypeParamBound
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
            is Expr.Array -> e.copy(attrs = visitAttributes(e.attrs), elems = e.elems.copy({ visitExpr(it) }, { it }))
            is Expr.Assign ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    left = visitExpr(e.left),
                    right = visitExpr(e.right),
                )
            is Expr.Async -> e.copy(attrs = visitAttributes(e.attrs), block = visitBlock(e.block))
            is Expr.Await -> e.copy(attrs = visitAttributes(e.attrs), base = visitExpr(e.base))
            is Expr.Binary ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    left = visitExpr(e.left),
                    op = visitBinOp(e.op),
                    right = visitExpr(e.right),
                )
            is Expr.BlockExpr -> e.copy(attrs = visitAttributes(e.attrs), block = visitBlock(e.block))
            is Expr.Break -> e.copy(attrs = visitAttributes(e.attrs), label = e.label?.let { visitLifetime(it) }, expr = e.expr?.let { visitExpr(it) })
            is Expr.Call ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    func = visitExpr(e.func),
                    args = e.args.copy({ visitExpr(it) }, { it }),
                )
            is Expr.Cast ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    expr = visitExpr(e.expr),
                    ty = visitType(e.ty),
                )
            is Expr.Closure ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    inputs = e.inputs.copy({ visitPat(it) }, { it }),
                    output = visitReturnType(e.output),
                    body = visitExpr(e.body),
                )
            is Expr.Const -> e.copy(attrs = visitAttributes(e.attrs), block = visitBlock(e.block))
            is Expr.Continue -> e.copy(attrs = visitAttributes(e.attrs), label = e.label?.let { visitLifetime(it) })
            is Expr.Field ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    base = visitExpr(e.base),
                    member = visitMember(e.member),
                )
            is Expr.ForLoop ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    pat = visitPat(e.pat),
                    expr = visitExpr(e.expr),
                    body = visitBlock(e.body),
                )
            is Expr.Group -> e.copy(attrs = visitAttributes(e.attrs), expr = visitExpr(e.expr))
            is Expr.If ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    cond = visitExpr(e.cond),
                    thenBranch = visitBlock(e.thenBranch),
                    elseBranch = e.elseBranch?.let { visitElseExpr(it) },
                )
            is Expr.Index ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    expr = visitExpr(e.expr),
                    index = visitExpr(e.index),
                )
            is Expr.Infer -> e.copy(attrs = visitAttributes(e.attrs))
            is Expr.Let ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    pat = visitPat(e.pat),
                    expr = visitExpr(e.expr),
                )
            is Expr.Lit -> e.copy(attrs = visitAttributes(e.attrs), lit = visitLit(e.lit))
            is Expr.Loop -> e.copy(attrs = visitAttributes(e.attrs), body = visitBlock(e.body))
            is Expr.Macro -> e.copy(attrs = visitAttributes(e.attrs), mac = visitMacro(e.mac))
            is Expr.Match ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    expr = visitExpr(e.expr),
                    arms = e.arms.map { visitArm(it) },
                )
            is Expr.MethodCall ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    receiver = visitExpr(e.receiver),
                    method = visitIdent(e.method),
                    turbofish = e.turbofish?.let { visitPathArguments(it) as PathArguments.AngleBracketed },
                    args = e.args.copy({ visitExpr(it) }, { it }),
                )
            is Expr.Paren -> e.copy(attrs = visitAttributes(e.attrs), expr = visitExpr(e.expr))
            is Expr.Path -> visitExprPath(e)
            is Expr.Range ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    start = e.start?.let { visitExpr(it) },
                    end = e.end?.let { visitExpr(it) },
                )
            is Expr.RawAddr ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    mutability = visitPointerMutability(e.mutability),
                    expr = visitExpr(e.expr),
                )
            is Expr.Reference -> e.copy(attrs = visitAttributes(e.attrs), expr = visitExpr(e.expr))
            is Expr.Repeat ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    expr = visitExpr(e.expr),
                    len = visitExpr(e.len),
                )
            is Expr.Return -> e.copy(attrs = visitAttributes(e.attrs), expr = e.expr?.let { visitExpr(it) })
            is Expr.Struct ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    qself = e.qself?.let { visitQSelf(it) },
                    path = visitPath(e.path),
                    fields = e.fields.copy({ visitFieldValue(it) }, { it }),
                    rest = e.rest?.let { visitExpr(it) },
                )
            is Expr.Try -> e.copy(attrs = visitAttributes(e.attrs), expr = visitExpr(e.expr))
            is Expr.TryBlock -> e.copy(attrs = visitAttributes(e.attrs), block = visitBlock(e.block))
            is Expr.Tuple -> e.copy(attrs = visitAttributes(e.attrs), elems = e.elems.copy({ visitExpr(it) }, { it }))
            is Expr.Unary -> e.copy(attrs = visitAttributes(e.attrs), expr = visitExpr(e.expr))
            is Expr.Unsafe -> e.copy(attrs = visitAttributes(e.attrs), block = visitBlock(e.block))
            is Expr.While ->
                e.copy(
                    attrs = visitAttributes(e.attrs),
                    cond = visitExpr(e.cond),
                    body = visitBlock(e.body),
                )
            is Expr.Yield -> e.copy(attrs = visitAttributes(e.attrs), expr = e.expr?.let { visitExpr(it) })
            is Expr.Verbatim -> e
        }

    public open fun visitType(t: SynType): SynType =
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
            is SynType.Verbatim -> t
        }

    public open fun visitPath(p: Path): Path =
        Path(
            leadingColon = p.leadingColon,
            segments = p.segments.copy({ visitPathSegment(it) }, { it }),
        )

    public open fun visitPat(p: Pat): Pat =
        when (p) {
            is Pat.Const -> p.copy(attrs = visitAttributes(p.attrs), block = visitBlock(p.block))
            is Pat.Ident -> visitPatIdent(p)
            is Pat.Lit -> p.copy(attrs = visitAttributes(p.attrs), lit = visitLit(p.lit))
            is Pat.Macro -> p.copy(attrs = visitAttributes(p.attrs), mac = visitMacro(p.mac))
            is Pat.Or -> p.copy(attrs = visitAttributes(p.attrs), cases = p.cases.copy({ visitPat(it) }, { it }))
            is Pat.PatParen -> p.copy(attrs = visitAttributes(p.attrs), pat = visitPat(p.pat))
            is Pat.Path -> p.copy(attrs = visitAttributes(p.attrs), qself = p.qself?.let { visitQSelf(it) }, path = visitPath(p.path))
            is Pat.Range -> p.copy(attrs = visitAttributes(p.attrs), start = p.start?.let { visitExpr(it) }, end = p.end?.let { visitExpr(it) })
            is Pat.Reference -> p.copy(attrs = visitAttributes(p.attrs), pat = visitPat(p.pat))
            is Pat.Rest -> p.copy(attrs = visitAttributes(p.attrs))
            is Pat.Slice -> p.copy(attrs = visitAttributes(p.attrs), elems = p.elems.copy({ visitPat(it) }, { it }))
            is Pat.Struct ->
                p.copy(
                    attrs = visitAttributes(p.attrs),
                    qself = p.qself?.let { visitQSelf(it) },
                    path = visitPath(p.path),
                    fields = p.fields.copy({ visitFieldPat(it) }, { it }),
                    rest = p.rest?.copy(attrs = visitAttributes(p.rest.attrs)),
                )
            is Pat.Tuple -> p.copy(attrs = visitAttributes(p.attrs), elems = p.elems.copy({ visitPat(it) }, { it }))
            is Pat.TupleStruct ->
                p.copy(
                    attrs = visitAttributes(p.attrs),
                    qself = p.qself?.let { visitQSelf(it) },
                    path = visitPath(p.path),
                    elems = p.elems.copy({ visitPat(it) }, { it }),
                )
            is Pat.TypeAscription -> p.copy(attrs = visitAttributes(p.attrs), pat = visitPat(p.pat), ty = visitType(p.ty))
            is Pat.Wild -> p.copy(attrs = visitAttributes(p.attrs))
            is Pat.Verbatim -> p
        }

    public open fun visitItem(i: Item): Item =
        when (i) {
            is Item.Const -> visitItemConst(i)
            is Item.Enum -> visitItemEnum(i)
            is Item.Fn -> visitItemFn(i)
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
            is Item.Verbatim -> i
        }

    public open fun visitFile(f: File): File =
        f.copy(
            attrs = visitAttributes(f.attrs),
            items = f.items.map { visitItem(it) },
        )

    public open fun visitAttribute(a: Attribute): Attribute =
        a.copy(
            style = visitAttrStyle(a.style),
            meta = visitMeta(a.meta),
        )

    public open fun visitAttrStyle(style: AttrStyle): AttrStyle = style

    public open fun visitMeta(m: Meta): Meta =
        when (m) {
            is Meta.PathMeta -> m.copy(path = visitPath(m.path))
            is Meta.List -> visitMetaList(m)
            is Meta.NameValue -> visitMetaNameValue(m)
        }

    public open fun visitMetaList(m: Meta.List): Meta = m.copy(path = visitPath(m.path))

    public open fun visitMetaNameValue(m: Meta.NameValue): Meta =
        m.copy(path = visitPath(m.path), value = visitExpr(m.value))

    public open fun visitGenerics(g: Generics): Generics =
        Generics(
            ltToken = g.ltToken,
            params = g.params.copy({ visitGenericParam(it) }, { it }),
            gtToken = g.gtToken,
            whereClause = g.whereClause?.let { visitWhereClause(it) },
        )

    public open fun visitLit(l: Lit): Lit = l

    public open fun visitLifetime(lt: Lifetime): Lifetime = lt

    public open fun visitIdent(id: Ident): Ident = id

    public open fun visitStmt(s: Stmt): Stmt =
        when (s) {
            is Stmt.Local ->
                s.copy(
                    attrs = visitAttributes(s.attrs),
                    pat = visitPat(s.pat),
                    init = s.init?.let { visitLocalInit(it) },
                )
            is Stmt.ItemStmt -> s.copy(item = visitItem(s.item))
            is Stmt.ExprStmt -> s.copy(expr = visitExpr(s.expr))
            is Stmt.MacroStmt -> s.copy(attrs = visitAttributes(s.attrs), mac = visitMacro(s.mac))
        }

    public open fun visitData(d: Data): Data =
        when (d) {
            is Data.Struct -> Data.Struct(visitDataStruct(d.value))
            is Data.Enum -> Data.Enum(visitDataEnum(d.value))
            is Data.Union -> Data.Union(visitDataUnion(d.value))
        }

    public open fun visitDataEnum(d: DataEnum): DataEnum =
        d.copy(variants = d.variants.copy({ visitVariant(it) }, { it }))

    public open fun visitDataStruct(d: DataStruct): DataStruct =
        d.copy(fields = visitFields(d.fields))

    public open fun visitDataUnion(d: DataUnion): DataUnion =
        d.copy(fields = visitFieldsNamed(d.fields))

    public open fun visitDeriveInput(di: DeriveInput): DeriveInput =
        di.copy(
            attrs = visitAttributes(di.attrs),
            vis = visitVisibility(di.vis),
            ident = visitIdent(di.ident),
            generics = visitGenerics(di.generics),
            data = visitData(di.data),
        )

    public open fun visitBlock(block: Block): Block = block.copy(stmts = block.stmts.map { visitStmt(it) })

    public open fun visitAttributes(attrs: List<Attribute>): List<Attribute> = attrs.map { visitAttribute(it) }

    public open fun visitSignature(sig: Signature): Signature {
        var result = sig
        result =
            result.copy(
                abi = result.abi?.let { visitAbi(it) },
                ident = visitIdent(result.ident),
                generics = visitGenerics(result.generics),
                inputs = result.inputs.copy({ visitFnArg(it) }, { it }),
                variadic = result.variadic?.let { visitVariadic(it) },
                output = visitReturnType(result.output),
            )
        return result
    }

    public open fun visitAbi(a: Abi): Abi = a

    public open fun visitReturnType(rt: ReturnType): ReturnType =
        when (rt) {
            is ReturnType.Default -> rt
            is ReturnType.TypeReturn -> rt.copy(ty = visitType(rt.ty))
        }

    public open fun visitFnArg(arg: FnArg): FnArg =
        when (arg) {
            is FnArg.Receiver -> visitReceiver(arg)
            is FnArg.Typed -> arg.copy(patType = visitPatType(arg.patType))
        }

    public open fun visitReceiver(receiver: FnArg.Receiver): FnArg.Receiver =
        receiver.copy(
            attrs = visitAttributes(receiver.attrs),
            reference = receiver.reference?.copy(lifetime = receiver.reference.lifetime?.let { visitLifetime(it) }),
            `type` = visitType(receiver.type),
        )

    public open fun visitPatType(patType: PatType): PatType = patType.copy(pat = visitPat(patType.pat), ty = visitType(patType.ty))

    public open fun visitPatIdent(patIdent: Pat.Ident): Pat =
        patIdent.copy(
            attrs = visitAttributes(patIdent.attrs),
            ident = visitIdent(patIdent.ident),
            subpat = patIdent.subpat?.let { visitPat(it) },
        )

    public open fun visitTypePath(typePath: SynType.Path): SynType =
        typePath.copy(qself = typePath.qself?.let { visitQSelf(it) }, path = visitPath(typePath.path))

    public open fun visitTypeReference(ty: SynType.Reference): SynType = ty.copy(lifetime = ty.lifetime?.let { visitLifetime(it) }, elem = visitType(ty.elem))

    public open fun visitTypeArray(ty: SynType.Array): SynType =
        ty.copy(elem = visitType(ty.elem), len = visitExpr(ty.len))

    public open fun visitTypeGroup(ty: SynType.Group): SynType =
        ty.copy(elem = visitType(ty.elem))

    public open fun visitTypeImplTrait(ty: SynType.ImplTrait): SynType = ty.copy(bounds = ty.bounds.copy({ visitTypeParamBound(it) }, { it }))

    public open fun visitTypeInfer(ty: SynType.Infer): SynType = ty

    public open fun visitTypeMacro(ty: SynType.Macro): SynType = ty.copy(mac = visitMacro(ty.mac))

    public open fun visitTypeNever(ty: SynType.Never): SynType = ty

    public open fun visitTypePtr(ty: SynType.Ptr): SynType = ty.copy(elem = visitType(ty.elem))

    public open fun visitPointerMutability(mutability: PointerMutability): PointerMutability = mutability

    public open fun visitTypeBareFn(ty: SynType.BareFn): SynType =
        ty.copy(
            lifetimes = ty.lifetimes?.let { visitBoundLifetimes(it) },
            abi = ty.abi?.let { visitAbi(it) },
            inputs = ty.inputs.copy({ visitBareFnArg(it) }, { it }),
            variadic = ty.variadic?.let { visitBareVariadic(it) },
            output = visitReturnType(ty.output),
        )

    public open fun visitBareFnArg(arg: BareFnArg): BareFnArg =
        arg.copy(
            attrs = visitAttributes(arg.attrs),
            name = arg.name?.copy(ident = visitIdent(arg.name.ident)),
            ty = visitType(arg.ty),
        )

    public open fun visitBareVariadic(variadic: BareVariadic): BareVariadic =
        variadic.copy(
            attrs = visitAttributes(variadic.attrs),
            name = variadic.name?.copy(ident = visitIdent(variadic.name.ident)),
        )

    public open fun visitTypeParen(ty: SynType.Paren): SynType = ty.copy(elem = visitType(ty.elem))

    public open fun visitTypeSlice(ty: SynType.Slice): SynType = ty.copy(elem = visitType(ty.elem))

    public open fun visitTypeTraitObject(ty: SynType.TraitObject): SynType =
        ty.copy(bounds = ty.bounds.copy({ visitTypeParamBound(it) }, { it }))

    public open fun visitTypeTuple(ty: SynType.Tuple): SynType = ty.copy(elems = ty.elems.copy({ visitType(it) }, { it }))

    public open fun visitExprPath(exprPath: Expr.Path): Expr = exprPath.copy(path = visitPath(exprPath.path))

    public open fun visitMacro(mac: Macro): Macro = mac.copy(path = visitPath(mac.path))

    public open fun visitPathArguments(pathArgs: PathArguments): PathArguments =
        when (pathArgs) {
            is PathArguments.None -> pathArgs
            is PathArguments.AngleBracketed -> visitAngleBracketedGenericArguments(pathArgs)
            is PathArguments.Parenthesized -> visitParenthesizedGenericArguments(pathArgs)
        }

    public open fun visitAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed =
        pathArgs.copy(args = pathArgs.args.copy({ visitGenericArgument(it) }, { it }))

    public open fun visitParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized =
        pathArgs.copy(
            inputs = pathArgs.inputs.copy({ visitType(it) }, { it }),
            output = visitReturnType(pathArgs.output),
        )

    public open fun visitGenericArgument(genArg: GenericArgument): GenericArgument =
        when (genArg) {
            is GenericArgument.LifetimeArg -> genArg.copy(lifetime = visitLifetime(genArg.lifetime))
            is GenericArgument.TypeArg -> genArg.copy(type = visitType(genArg.type))
            is GenericArgument.ConstArg -> genArg.copy(expr = visitExpr(genArg.expr))
            is GenericArgument.AssocTypeArg -> genArg.copy(assoc = visitAssocType(genArg.assoc))
            is GenericArgument.AssocConstArg -> genArg.copy(assoc = visitAssocConst(genArg.assoc))
            is GenericArgument.ConstraintArg -> genArg.copy(constraint = visitConstraint(genArg.constraint))
        }

    public open fun visitAssocType(assoc: AssocType): AssocType =
        assoc.copy(
            ident = visitIdent(assoc.ident),
            generics = assoc.generics?.let { visitAngleBracketedGenericArguments(it) },
            ty = visitType(assoc.ty),
        )

    public open fun visitAssocConst(assoc: AssocConst): AssocConst =
        assoc.copy(
            ident = visitIdent(assoc.ident),
            generics = assoc.generics?.let { visitAngleBracketedGenericArguments(it) },
            value = visitExpr(assoc.value),
        )

    public open fun visitConstraint(constraint: Constraint): Constraint =
        constraint.copy(
            ident = visitIdent(constraint.ident),
            generics = constraint.generics?.let { visitAngleBracketedGenericArguments(it) },
            bounds = constraint.bounds.copy({ visitTypeParamBound(it) }, { it }),
        )

    public open fun visitTypeParamBound(bound: TypeParamBound): TypeParamBound =
        when (bound) {
            is TypeParamBound.Trait -> visitTraitBound(bound)
            is TypeParamBound.LifetimeBound -> bound.copy(lifetime = visitLifetime(bound.lifetime))
            is TypeParamBound.PreciseCapture -> bound.copy(params = bound.params.copy({ visitCapturedParam(it) }, { it }))
            is TypeParamBound.Verbatim -> bound
        }

    public open fun visitTraitBound(bound: TypeParamBound.Trait): TypeParamBound =
        bound.copy(
            modifier = visitTraitBoundModifier(bound.modifier),
            lifetimes = bound.lifetimes?.let { visitBoundLifetimes(it) },
            path = visitPath(bound.path),
        )

    public open fun visitTraitBoundModifier(modifier: TraitBoundModifier): TraitBoundModifier = modifier

    public open fun visitBinOp(op: BinOp): BinOp = op

    public open fun visitBoundLifetimes(boundLifetimes: BoundLifetimes): BoundLifetimes =
        boundLifetimes.copy(lifetimes = boundLifetimes.lifetimes.copy({ visitGenericParam(it) }, { it }))

    public open fun visitCapturedParam(param: CapturedParam): CapturedParam =
        when (param) {
            is CapturedParam.Lifetime -> param.copy(lifetime = visitLifetime(param.lifetime))
            is CapturedParam.Ident -> param.copy(ident = visitIdent(param.ident))
        }

    public open fun visitPathSegment(segment: PathSegment): PathSegment =
        segment.copy(ident = visitIdent(segment.ident), arguments = visitPathArguments(segment.arguments))

    public open fun visitArm(arm: Arm): Arm =
        arm.copy(
            attrs = visitAttributes(arm.attrs),
            pat = visitPat(arm.pat),
            guard = arm.guard?.copy(expr = visitExpr(arm.guard.expr)),
            body = visitExpr(arm.body),
        )

    public open fun visitElseExpr(elseExpr: ElseExpr): ElseExpr =
        elseExpr.copy(expr = visitExpr(elseExpr.expr))

    public open fun visitFieldPat(fieldPat: FieldPat): FieldPat =
        fieldPat.copy(
            attrs = visitAttributes(fieldPat.attrs),
            member = visitMember(fieldPat.member),
            pat = visitPat(fieldPat.pat),
        )

    public open fun visitFieldValue(fieldValue: FieldValue): FieldValue =
        fieldValue.copy(
            attrs = visitAttributes(fieldValue.attrs),
            member = visitMember(fieldValue.member),
            expr = visitExpr(fieldValue.expr),
        )

    public open fun visitGenericParam(param: GenericParam): GenericParam =
        when (param) {
            is GenericParam.LifetimeParam ->
                param.copy(
                    attrs = visitAttributes(param.attrs),
                    lifetime = visitLifetime(param.lifetime),
                    bounds = param.bounds.copy({ visitLifetime(it) }, { it }),
                )
            is GenericParam.TypeParam ->
                param.copy(
                    attrs = visitAttributes(param.attrs),
                    ident = visitIdent(param.ident),
                    bounds = param.bounds.copy({ visitTypeParamBound(it) }, { it }),
                    default = param.default?.let { visitType(it) },
                )
            is GenericParam.ConstParam ->
                param.copy(
                    attrs = visitAttributes(param.attrs),
                    ident = visitIdent(param.ident),
                    ty = visitType(param.ty),
                    default = param.default?.let { visitExpr(it) },
                )
        }

    public open fun visitField(field: Field): Field =
        field.copy(
            attrs = visitAttributes(field.attrs),
            vis = visitVisibility(field.vis),
            mutability = visitFieldMutability(field.mutability),
            ident = field.ident?.let { visitIdent(it) },
            ty = visitType(field.ty),
        )

    public open fun visitFieldMutability(fieldMutability: FieldMutability): FieldMutability = fieldMutability

    public open fun visitFields(fields: Fields): Fields =
        when (fields) {
            is Fields.Named -> Fields.Named(visitFieldsNamed(fields.fields))
            is Fields.Unnamed -> Fields.Unnamed(visitFieldsUnnamed(fields.fields))
            Fields.Unit -> fields
        }

    public open fun visitFieldsNamed(fields: FieldsNamed): FieldsNamed =
        fields.copy(named = fields.named.copy({ visitField(it) }, { it }))

    public open fun visitFieldsUnnamed(fields: FieldsUnnamed): FieldsUnnamed =
        fields.copy(unnamed = fields.unnamed.copy({ visitField(it) }, { it }))

    public open fun visitImplItem(item: ImplItem): ImplItem =
        when (item) {
            is ImplItem.Const -> visitImplItemConst(item)
            is ImplItem.Fn -> visitImplItemFn(item)
            is ImplItem.AssocType -> visitImplItemType(item)
            is ImplItem.Macro -> visitImplItemMacro(item)
            is ImplItem.Verbatim -> item
        }

    public open fun visitImplItemConst(item: ImplItem.Const): ImplItem =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            ty = visitType(item.ty),
            expr = visitExpr(item.expr),
        )

    public open fun visitImplItemFn(item: ImplItem.Fn): ImplItem =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            sig = visitSignature(item.sig),
            block = visitBlock(item.block),
        )

    public open fun visitImplItemMacro(item: ImplItem.Macro): ImplItem =
        item.copy(attrs = visitAttributes(item.attrs), mac = visitMacro(item.mac))

    public open fun visitImplItemType(item: ImplItem.AssocType): ImplItem =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            ty = visitType(item.ty),
        )

    public open fun visitItemConst(item: Item.Const): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            ty = visitType(item.ty),
            expr = item.expr?.let { visitExpr(it) },
        )

    public open fun visitItemEnum(item: Item.Enum): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            variants = item.variants.copy({ visitVariant(it) }, { it }),
        )

    public open fun visitItemFn(item: Item.Fn): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            sig = visitSignature(item.sig),
            block = item.block?.let { visitBlock(it) },
        )

    public open fun visitItemImpl(item: Item.Impl): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            generics = visitGenerics(item.generics),
            traitPath = item.traitPath?.let { visitPathTrait(it) },
            selfType = visitType(item.selfType),
            items = item.items.map { visitImplItem(it) },
        )

    public open fun visitItemMacro(item: Item.Macro): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            ident = item.ident?.let { visitIdent(it) },
            mac = visitMacro(item.mac),
        )

    public open fun visitItemMod(item: Item.Mod): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            content = item.content?.let { visitModContent(it) },
        )

    public open fun visitItemStatic(item: Item.Static): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            mutability = visitStaticMutability(item.mutability),
            ident = visitIdent(item.ident),
            ty = visitType(item.ty),
            expr = visitExpr(item.expr),
        )

    public open fun visitItemStruct(item: Item.Struct): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            fields = visitFields(item.fields),
        )

    public open fun visitItemTrait(item: Item.Trait): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            supertraits = item.supertraits.copy({ visitTypeParamBound(it) }, { it }),
            items = item.items.map { visitTraitItem(it) },
        )

    public open fun visitItemTraitAlias(item: Item.TraitAlias): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            bounds = item.bounds.copy({ visitTypeParamBound(it) }, { it }),
        )

    public open fun visitItemType(item: Item.ItemType): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            ty = visitType(item.ty),
        )

    public open fun visitItemUnion(item: Item.Union): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            fields = visitFieldsNamed(item.fields),
        )

    public open fun visitItemUse(item: Item.Use): Item =
        item.copy(
            attrs = visitAttributes(item.attrs),
            vis = visitVisibility(item.vis),
            tree = visitUseTree(item.tree),
        )

    public open fun visitStaticMutability(mutability: StaticMutability): StaticMutability = mutability

    public open fun visitModContent(modContent: ModContent): ModContent =
        when (modContent) {
            is ModContent.Inline -> modContent.copy(items = modContent.items.map { visitItem(it) })
            is ModContent.Unnamed -> modContent
        }

    public open fun visitLocalInit(init: LocalInit): LocalInit =
        init.copy(
            expr = visitExpr(init.expr),
            diverge = init.diverge?.let { visitElseExpr(it) },
        )

    public open fun visitMember(member: Member): Member =
        when (member) {
            is Member.Named -> member.copy(ident = visitIdent(member.ident))
            is Member.Unnamed -> member
        }

    public open fun visitQSelf(qself: QSelf): QSelf =
        qself.copy(ty = visitType(qself.ty))

    public open fun visitPathTrait(pathTrait: PathTrait): PathTrait =
        pathTrait.copy(path = visitPath(pathTrait.path))

    public open fun visitTraitItem(item: TraitItem): TraitItem =
        when (item) {
            is TraitItem.Const -> visitTraitItemConst(item)
            is TraitItem.Fn -> visitTraitItemFn(item)
            is TraitItem.AssocType -> visitTraitItemType(item)
            is TraitItem.Macro -> visitTraitItemMacro(item)
            is TraitItem.Verbatim -> item
        }

    public open fun visitTraitItemConst(item: TraitItem.Const): TraitItem =
        item.copy(
            attrs = visitAttributes(item.attrs),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            ty = visitType(item.ty),
            default = item.default?.copy(expr = visitExpr(item.default.expr)),
        )

    public open fun visitTraitItemFn(item: TraitItem.Fn): TraitItem =
        item.copy(
            attrs = visitAttributes(item.attrs),
            sig = visitSignature(item.sig),
            default = item.default?.let { visitBlock(it) },
        )

    public open fun visitTraitItemMacro(item: TraitItem.Macro): TraitItem =
        item.copy(attrs = visitAttributes(item.attrs), mac = visitMacro(item.mac))

    public open fun visitTraitItemType(item: TraitItem.AssocType): TraitItem =
        item.copy(
            attrs = visitAttributes(item.attrs),
            ident = visitIdent(item.ident),
            generics = visitGenerics(item.generics),
            bounds = item.bounds.copy({ visitTypeParamBound(it) }, { it }),
            default = item.default?.copy(type = visitType(item.default.type)),
        )

    public open fun visitUseTree(useTree: UseTree): UseTree =
        when (useTree) {
            is UseTree.Path -> visitUsePath(useTree)
            is UseTree.Name ->
                if (useTree.rename == null) {
                    visitUseName(useTree)
                } else {
                    visitUseRename(useTree)
                }
            is UseTree.Group -> visitUseGroup(useTree)
            is UseTree.Glob -> visitUseGlob(useTree)
        }

    public open fun visitUseGlob(useTree: UseTree.Glob): UseTree = useTree

    public open fun visitUseGroup(useTree: UseTree.Group): UseTree =
        useTree.copy(items = useTree.items.copy({ visitUseTree(it) }, { it }))

    public open fun visitUseName(useTree: UseTree.Name): UseTree =
        useTree.copy(ident = visitIdent(useTree.ident))

    public open fun visitUsePath(useTree: UseTree.Path): UseTree =
        useTree.copy(
            ident = visitIdent(useTree.ident),
            tree = useTree.tree?.let { visitUseTree(it) },
        )

    public open fun visitUseRename(useTree: UseTree.Name): UseTree =
        useTree.copy(
            ident = visitIdent(useTree.ident),
            rename = useTree.rename?.copy(ident = visitIdent(useTree.rename.ident)),
        )

    public open fun visitVariadic(variadic: Variadic): Variadic =
        variadic.copy(
            attrs = visitAttributes(variadic.attrs),
            pat = variadic.pat?.copy(pat = visitPat(variadic.pat.pat)),
        )

    public open fun visitVariant(variant: Variant): Variant =
        variant.copy(
            attrs = visitAttributes(variant.attrs),
            ident = visitIdent(variant.ident),
            fields = visitFields(variant.fields),
            discriminant = variant.discriminant?.copy(expr = visitExpr(variant.discriminant.expr)),
        )

    public open fun visitVisibility(visibility: Visibility): Visibility =
        when (visibility) {
            is Visibility.Public -> visibility
            is Visibility.Restricted -> visibility.copy(path = visitPath(visibility.path))
            Visibility.Inherited -> visibility
        }

    public open fun visitWhereClause(whereClause: WhereClause): WhereClause =
        whereClause.copy(predicates = whereClause.predicates.copy({ visitWherePredicate(it) }, { it }))

    public open fun visitWherePredicate(wherePredicate: WherePredicate): WherePredicate =
        when (wherePredicate) {
            is WherePredicate.LifetimePredicate ->
                wherePredicate.copy(
                    lifetime = visitLifetime(wherePredicate.lifetime),
                    bounds = wherePredicate.bounds.copy({ visitLifetime(it) }, { it }),
                )
            is WherePredicate.TypePredicate ->
                wherePredicate.copy(
                    lifetimes = wherePredicate.lifetimes?.let { visitBoundLifetimes(it) },
                    boundedTy = visitType(wherePredicate.boundedTy),
                    bounds = wherePredicate.bounds.copy({ visitTypeParamBound(it) }, { it }),
                )
        }
}
