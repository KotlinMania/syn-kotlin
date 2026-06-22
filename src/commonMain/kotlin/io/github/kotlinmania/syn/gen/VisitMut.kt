// port-lint: source gen/visit_mut.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.Block
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.Expr
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.Ident
import io.github.kotlinmania.syn.Item
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynType

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
    public open fun visitExpr(e: Expr): Expr = e

    public open fun visitType(t: SynType): SynType = t

    public open fun visitPath(p: Path): Path = p

    public open fun visitPat(p: Pat): Pat = p

    public open fun visitItem(i: Item): Item = i

    public open fun visitAttribute(a: Attribute): Attribute = a

    public open fun visitMeta(m: Meta): Meta = m

    public open fun visitGenerics(g: Generics): Generics = g

    public open fun visitLit(l: Lit): Lit = l

    public open fun visitLifetime(lt: Lifetime): Lifetime = lt

    public open fun visitIdent(id: Ident): Ident = id

    public open fun visitStmt(s: Stmt): Stmt = s

    public open fun visitData(d: Data): Data = d

    public open fun visitDeriveInput(di: DeriveInput): DeriveInput = di

    public open fun visitBlock(block: Block): Block = block.copy(stmts = block.stmts.map { visitStmt(it) })

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

    public open fun visitPatIdent(patIdent: Pat.Ident): Pat = patIdent

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

    public open fun visitMacro(mac: Macro): Macro = mac

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
            else -> genArg
        }

    public open fun visitTypeParamBound(bound: io.github.kotlinmania.syn.TypeParamBound): io.github.kotlinmania.syn.TypeParamBound =
        when (bound) {
            is io.github.kotlinmania.syn.TypeParamBound.Trait -> bound.copy(path = visitPath(bound.path))
            is io.github.kotlinmania.syn.TypeParamBound.LifetimeBound -> bound.copy(lifetime = visitLifetime(bound.lifetime))
        }

    public open fun visitPathSegment(segment: PathSegment): PathSegment = segment.copy(arguments = visitPathArguments(segment.arguments))
}
