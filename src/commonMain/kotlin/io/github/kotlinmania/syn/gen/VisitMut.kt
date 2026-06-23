// port-lint: source gen/visit_mut.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.Block
import io.github.kotlinmania.syn.CapturedParam
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.ElseExpr
import io.github.kotlinmania.syn.Expr
import io.github.kotlinmania.syn.FieldPat
import io.github.kotlinmania.syn.FieldValue
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.Item
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.LocalInit
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.Member
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.QSelf
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.TypeParamBound
import io.github.kotlinmania.syn.WhereClause
import io.github.kotlinmania.syn.WherePredicate

/**
 * AST mutable-visitor — walks a syntax tree and can mutate nodes in place.
 *
 * Kotlin data classes provide `copy()` for immutable mutation, and sealed
 * hierarchies support `deepCopy()` for deep cloning.
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
            is Expr.RawAddr -> e.copy(attrs = visitAttributes(e.attrs), expr = visitExpr(e.expr))
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
            is SynType.Array -> t.copy(elem = visitType(t.elem), len = visitExpr(t.len))
            is SynType.BareFn -> visitTypeBareFn(t)
            is SynType.Group -> t.copy(elem = visitType(t.elem))
            is SynType.ImplTrait -> t.copy(bounds = t.bounds.copy({ visitTypeParamBound(it) }, { it }))
            is SynType.Infer -> t
            is SynType.Macro -> t.copy(mac = visitMacro(t.mac))
            is SynType.Never -> t
            is SynType.Paren -> visitTypeParen(t)
            is SynType.Path -> visitTypePath(t)
            is SynType.Ptr -> visitTypePtr(t)
            is SynType.Reference -> visitTypeReference(t)
            is SynType.Slice -> t.copy(elem = visitType(t.elem))
            is SynType.TraitObject -> t.copy(bounds = t.bounds.copy({ visitTypeParamBound(it) }, { it }))
            is SynType.Tuple -> t.copy(elems = t.elems.copy({ visitType(it) }, { it }))
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
            is Pat.Or -> p.copy(cases = p.cases.copy({ visitPat(it) }, { it }))
            is Pat.PatParen -> p.copy(pat = visitPat(p.pat))
            is Pat.Path -> p.copy(attrs = visitAttributes(p.attrs), qself = p.qself?.let { visitQSelf(it) }, path = visitPath(p.path))
            is Pat.Range -> p.copy(attrs = visitAttributes(p.attrs), start = p.start?.let { visitExpr(it) }, end = p.end?.let { visitExpr(it) })
            is Pat.Reference -> p.copy(pat = visitPat(p.pat))
            is Pat.Rest -> p.copy(attrs = visitAttributes(p.attrs))
            is Pat.Slice -> p.copy(elems = p.elems.copy({ visitPat(it) }, { it }))
            is Pat.Struct ->
                p.copy(
                    qself = p.qself?.let { visitQSelf(it) },
                    path = visitPath(p.path),
                    fields = p.fields.copy({ visitFieldPat(it) }, { it }),
                    rest = p.rest?.deepCopy(),
                )
            is Pat.Tuple -> p.copy(elems = p.elems.copy({ visitPat(it) }, { it }))
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

    public open fun visitItem(i: Item): Item = i

    public open fun visitAttribute(a: Attribute): Attribute = a

    public open fun visitMeta(m: Meta): Meta = m

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

    public open fun visitData(d: Data): Data = d

    public open fun visitDeriveInput(di: DeriveInput): DeriveInput = di

    public open fun visitBlock(block: Block): Block = block.copy(stmts = block.stmts.map { visitStmt(it) })

    public open fun visitAttributes(attrs: List<Attribute>): List<Attribute> = attrs.map { visitAttribute(it) }

    public open fun visitSignature(sig: Signature): Signature {
        var result = sig
        result =
            result.copy(
                generics = visitGenerics(result.generics),
                inputs = result.inputs.copy({ visitFnArg(it) }, { it }),
                output = visitReturnType(result.output),
            )
        return result
    }

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

    public open fun visitReceiver(receiver: FnArg.Receiver): FnArg.Receiver = receiver.copy(`type` = visitType(receiver.type))

    public open fun visitPatType(patType: PatType): PatType = patType.copy(pat = visitPat(patType.pat), ty = visitType(patType.ty))

    public open fun visitPatIdent(patIdent: Pat.Ident): Pat =
        patIdent.copy(
            attrs = visitAttributes(patIdent.attrs),
            ident = visitIdent(patIdent.ident),
            subpat = patIdent.subpat?.let { visitPat(it) },
        )

    public open fun visitTypePath(typePath: SynType.Path): SynType = typePath.copy(path = visitPath(typePath.path))

    public open fun visitTypeReference(ty: SynType.Reference): SynType = ty.copy(lifetime = ty.lifetime?.let { visitLifetime(it) }, elem = visitType(ty.elem))

    public open fun visitTypeImplTrait(ty: SynType.ImplTrait): SynType = ty.copy(bounds = ty.bounds.copy({ visitTypeParamBound(it) }, { it }))

    public open fun visitTypePtr(ty: SynType.Ptr): SynType = ty.copy(elem = visitType(ty.elem))

    public open fun visitTypeBareFn(ty: SynType.BareFn): SynType =
        ty.copy(
            inputs = ty.inputs.copy({ it.copy(ty = visitType(it.ty)) }, { it }),
            output = visitReturnType(ty.output),
        )

    public open fun visitTypeParen(ty: SynType.Paren): SynType = ty.copy(elem = visitType(ty.elem))

    public open fun visitExprPath(exprPath: Expr.Path): Expr = exprPath.copy(path = visitPath(exprPath.path))

    public open fun visitMacro(mac: Macro): Macro = mac.copy(path = visitPath(mac.path))

    public open fun visitPathArguments(pathArgs: PathArguments): PathArguments =
        when (pathArgs) {
            is PathArguments.None -> pathArgs
            is PathArguments.AngleBracketed ->
                pathArgs.copy(
                    args = pathArgs.args.copy({ visitGenericArgument(it) }, { it }),
                )
            is PathArguments.Parenthesized ->
                pathArgs.copy(
                    inputs = pathArgs.inputs.copy({ visitType(it) }, { it }),
                    output = visitReturnType(pathArgs.output),
                )
        }

    public open fun visitGenericArgument(genArg: GenericArgument): GenericArgument =
        when (genArg) {
            is GenericArgument.LifetimeArg -> genArg.copy(lifetime = visitLifetime(genArg.lifetime))
            is GenericArgument.TypeArg -> genArg.copy(type = visitType(genArg.type))
            is GenericArgument.ConstArg -> genArg.copy(expr = visitExpr(genArg.expr))
            is GenericArgument.AssocTypeArg -> genArg.copy(assoc = genArg.assoc.copy(ty = visitType(genArg.assoc.ty)))
            is GenericArgument.AssocConstArg -> genArg.copy(assoc = genArg.assoc.copy(value = visitExpr(genArg.assoc.value)))
            is GenericArgument.ConstraintArg ->
                genArg.copy(
                    constraint = genArg.constraint.copy(bounds = genArg.constraint.bounds.copy({ visitTypeParamBound(it) }, { it })),
                )
        }

    public open fun visitTypeParamBound(bound: TypeParamBound): TypeParamBound =
        when (bound) {
            is TypeParamBound.Trait -> bound.copy(path = visitPath(bound.path))
            is TypeParamBound.LifetimeBound -> bound.copy(lifetime = visitLifetime(bound.lifetime))
            is TypeParamBound.PreciseCapture -> bound.copy(params = bound.params.copy({ visitCapturedParam(it) }, { it }))
            is TypeParamBound.Verbatim -> bound
        }

    public open fun visitCapturedParam(param: CapturedParam): CapturedParam =
        when (param) {
            is CapturedParam.Lifetime -> param.copy(lifetime = visitLifetime(param.lifetime))
            is CapturedParam.Ident -> param
        }

    public open fun visitPathSegment(segment: PathSegment): PathSegment = segment.copy(arguments = visitPathArguments(segment.arguments))

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
        fieldPat.copy(pat = visitPat(fieldPat.pat))

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
                    boundedTy = visitType(wherePredicate.boundedTy),
                    bounds = wherePredicate.bounds.copy({ visitTypeParamBound(it) }, { it }),
                )
        }
}
