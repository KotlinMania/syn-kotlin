// port-lint: source gen/eq.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.AndLifetime
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.AsIdent
import io.github.kotlinmania.syn.AssocConst
import io.github.kotlinmania.syn.AssocType
import io.github.kotlinmania.syn.AttrStyle
import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.BinOp
import io.github.kotlinmania.syn.Block
import io.github.kotlinmania.syn.BlockModifiers
import io.github.kotlinmania.syn.BoundLifetimes
import io.github.kotlinmania.syn.CapturedParam
import io.github.kotlinmania.syn.ClosureModifiers
import io.github.kotlinmania.syn.ConstModifiers
import io.github.kotlinmania.syn.Constraint
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DataEnum
import io.github.kotlinmania.syn.DataStruct
import io.github.kotlinmania.syn.DataUnion
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.ElseExpr
import io.github.kotlinmania.syn.EqExpr
import io.github.kotlinmania.syn.EqSynType
import io.github.kotlinmania.syn.Expr
import io.github.kotlinmania.syn.Field
import io.github.kotlinmania.syn.FieldModifiers
import io.github.kotlinmania.syn.FieldPat
import io.github.kotlinmania.syn.FieldValue
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
import io.github.kotlinmania.syn.File
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.FnModifiers
import io.github.kotlinmania.syn.FnPtrVariadic
import io.github.kotlinmania.syn.ForeignItem
import io.github.kotlinmania.syn.Frontmatter
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.IdentColon
import io.github.kotlinmania.syn.IfExpr
import io.github.kotlinmania.syn.ImplItem
import io.github.kotlinmania.syn.ImplModifiers
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
import io.github.kotlinmania.syn.LocalModifiers
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.MacroDelimiter
import io.github.kotlinmania.syn.Member
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.ModContent
import io.github.kotlinmania.syn.NamedArg
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatColon
import io.github.kotlinmania.syn.PatRest
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathForPair
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.PointerMutability
import io.github.kotlinmania.syn.QSelf
import io.github.kotlinmania.syn.RangeLimits
import io.github.kotlinmania.syn.ReceiverKind
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Safety
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.StaticMutability
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynPunctuated
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.TraitBoundModifiers
import io.github.kotlinmania.syn.TraitItem
import io.github.kotlinmania.syn.TraitModifiers
import io.github.kotlinmania.syn.TypeModifiers
import io.github.kotlinmania.syn.TypeParamBound
import io.github.kotlinmania.syn.UnOp
import io.github.kotlinmania.syn.UseTree
import io.github.kotlinmania.syn.Variadic
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.WhereClause
import io.github.kotlinmania.syn.WhereClausePlacement
import io.github.kotlinmania.syn.WherePredicate
import io.github.kotlinmania.syn.tokenStreamEq

private inline fun <T> eqOpt(
    left: T?,
    right: T?,
    eqElem: (T, T) -> Boolean,
): Boolean =
    if (left == null || right == null) {
        left == null && right == null
    } else {
        eqElem(left, right)
    }

private inline fun <T> eqList(
    left: List<T>,
    right: List<T>,
    eqElem: (T, T) -> Boolean,
): Boolean {
    if (left.size != right.size) return false
    for (i in left.indices) {
        if (!eqElem(left[i], right[i])) return false
    }
    return true
}

private inline fun <T> eqPunct(
    left: SynPunctuated,
    right: SynPunctuated,
    leftItems: List<T>,
    rightItems: List<T>,
    eqElem: (T, T) -> Boolean,
): Boolean =
    left.trailingPunct() == right.trailingPunct() && eqList(leftItems, rightItems, eqElem)

public fun Abi.eq(other: Abi): Boolean =
    eqOpt(name, other.name) { a, b -> a.eq(b) }

public fun AndLifetime.eq(other: AndLifetime): Boolean =
    eqOpt(lifetime, other.lifetime) { a, b -> a.eq(b) }

public fun Arm.eq(other: Arm): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        pat.eq(other.pat) &&
        body.eq(other.body) &&
        comma == other.comma

public fun AsIdent.eq(other: AsIdent): Boolean =
    ident == other.ident

public fun AssocConst.eq(other: AssocConst): Boolean =
    ident == other.ident &&
        eqOpt(generics, other.generics) { a, b -> a.eq(b) } &&
        value.eq(other.value)

public fun AssocType.eq(other: AssocType): Boolean =
    ident == other.ident &&
        eqOpt(generics, other.generics) { a, b -> a.eq(b) } &&
        ty.eq(other.ty)

public fun Attribute.eq(other: Attribute): Boolean =
    style.eq(other.style) &&
        meta.eq(other.meta)

public fun NamedArg.eq(other: NamedArg): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(name, other.name) { a, b -> a.eq(b) } &&
        ty.eq(other.ty)

public fun FnPtrVariadic.eq(other: FnPtrVariadic): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(name, other.name) { a, b -> a.eq(b) } &&
        comma == other.comma

public fun Block.eq(other: Block): Boolean =
    eqList(stmts, other.stmts) { a, b -> a.eq(b) }

public fun BlockModifiers.eq(other: BlockModifiers): Boolean = true

public fun BoundLifetimes.eq(other: BoundLifetimes): Boolean =
    eqPunct(lifetimes, other.lifetimes, lifetimes.toList(), other.lifetimes.toList()) { a, b -> a.eq(b) }

public fun ClosureModifiers.eq(other: ClosureModifiers): Boolean = true

public fun ConstModifiers.eq(other: ConstModifiers): Boolean =
    defaultness == other.defaultness

public fun Constraint.eq(other: Constraint): Boolean =
    ident == other.ident &&
        eqOpt(generics, other.generics) { a, b -> a.eq(b) } &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) }

public fun DataEnum.eq(other: DataEnum): Boolean =
    eqPunct(variants, other.variants, variants.toList(), other.variants.toList()) { a, b -> a.eq(b) }

public fun DataStruct.eq(other: DataStruct): Boolean =
    fields.eq(other.fields) &&
        semiToken == other.semiToken

public fun DataUnion.eq(other: DataUnion): Boolean =
    fields.eq(other.fields)

public fun DeriveInput.eq(other: DeriveInput): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        data.eq(other.data)

public fun ElseExpr.eq(other: ElseExpr): Boolean =
    expr.eq(other.expr)

public fun EqExpr.eq(other: EqExpr): Boolean =
    expr.eq(other.expr)

public fun EqSynType.eq(other: EqSynType): Boolean =
    type.eq(other.type)

private fun Expr.Array.eqVariant(other: Expr.Array): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqPunct(elems, other.elems, elems.toList(), other.elems.toList()) { a, b -> a.eq(b) }

private fun Expr.Assign.eqVariant(other: Expr.Assign): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        left.eq(other.left) &&
        right.eq(other.right)

private fun Expr.Async.eqVariant(other: Expr.Async): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        capture == other.capture &&
        modifiers.eq(other.modifiers) &&
        block.eq(other.block)

private fun Expr.Await.eqVariant(other: Expr.Await): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        base.eq(other.base)

private fun Expr.Binary.eqVariant(other: Expr.Binary): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        left.eq(other.left) &&
        op.eq(other.op) &&
        right.eq(other.right)

private fun Expr.BlockExpr.eqVariant(other: Expr.BlockExpr): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(label, other.label) { a, b -> a.eq(b) } &&
        block.eq(other.block)

private fun Expr.Break.eqVariant(other: Expr.Break): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(label, other.label) { a, b -> a.eq(b) } &&
        eqOpt(expr, other.expr) { a, b -> a.eq(b) }

private fun Expr.Call.eqVariant(other: Expr.Call): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        func.eq(other.func) &&
        eqPunct(args, other.args, args.toList(), other.args.toList()) { a, b -> a.eq(b) }

private fun Expr.Cast.eqVariant(other: Expr.Cast): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        expr.eq(other.expr) &&
        ty.eq(other.ty)

private fun Expr.Closure.eqVariant(other: Expr.Closure): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(lifetimes, other.lifetimes) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        constness == other.constness &&
        asyncness == other.asyncness &&
        capture == other.capture &&
        eqPunct(inputs, other.inputs, inputs.toList(), other.inputs.toList()) { a, b -> a.eq(b) } &&
        output.eq(other.output) &&
        body.eq(other.body)

private fun Expr.Const.eqVariant(other: Expr.Const): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        block.eq(other.block)

private fun Expr.Continue.eqVariant(other: Expr.Continue): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(label, other.label) { a, b -> a.eq(b) }

private fun Expr.Field.eqVariant(other: Expr.Field): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        base.eq(other.base) &&
        member.eq(other.member)

private fun Expr.ForLoop.eqVariant(other: Expr.ForLoop): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(label, other.label) { a, b -> a.eq(b) } &&
        pat.eq(other.pat) &&
        expr.eq(other.expr) &&
        body.eq(other.body)

private fun Expr.Group.eqVariant(other: Expr.Group): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        expr.eq(other.expr)

private fun Expr.If.eqVariant(other: Expr.If): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        cond.eq(other.cond) &&
        thenBranch.eq(other.thenBranch) &&
        eqOpt(elseBranch, other.elseBranch) { a, b -> a.eq(b) }

private fun Expr.Index.eqVariant(other: Expr.Index): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        expr.eq(other.expr) &&
        index.eq(other.index)

private fun Expr.Infer.eqVariant(other: Expr.Infer): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) }

private fun Expr.Let.eqVariant(other: Expr.Let): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        pat.eq(other.pat) &&
        expr.eq(other.expr)

private fun Expr.Lit.eqVariant(other: Expr.Lit): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        lit.eq(other.lit)

private fun Expr.Loop.eqVariant(other: Expr.Loop): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(label, other.label) { a, b -> a.eq(b) } &&
        body.eq(other.body)

private fun Expr.Macro.eqVariant(other: Expr.Macro): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mac.eq(other.mac)

private fun Expr.Match.eqVariant(other: Expr.Match): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        expr.eq(other.expr) &&
        eqList(arms, other.arms) { a, b -> a.eq(b) }

private fun Expr.MethodCall.eqVariant(other: Expr.MethodCall): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        receiver.eq(other.receiver) &&
        method == other.method &&
        eqOpt(turbofish, other.turbofish) { a, b -> a.eq(b) } &&
        eqPunct(args, other.args, args.toList(), other.args.toList()) { a, b -> a.eq(b) }

private fun Expr.Paren.eqVariant(other: Expr.Paren): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        expr.eq(other.expr)

private fun Expr.Path.eqVariant(other: Expr.Path): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(qself, other.qself) { a, b -> a.eq(b) } &&
        path.eq(other.path)

private fun Expr.Range.eqVariant(other: Expr.Range): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(start, other.start) { a, b -> a.eq(b) } &&
        limits.eq(other.limits) &&
        eqOpt(end, other.end) { a, b -> a.eq(b) }

private fun Expr.RawAddr.eqVariant(other: Expr.RawAddr): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mutability.eq(other.mutability) &&
        expr.eq(other.expr)

private fun Expr.Reference.eqVariant(other: Expr.Reference): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mutability == other.mutability &&
        expr.eq(other.expr)

private fun Expr.Repeat.eqVariant(other: Expr.Repeat): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        expr.eq(other.expr) &&
        len.eq(other.len)

private fun Expr.Return.eqVariant(other: Expr.Return): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(expr, other.expr) { a, b -> a.eq(b) }

private fun Expr.Struct.eqVariant(other: Expr.Struct): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(qself, other.qself) { a, b -> a.eq(b) } &&
        path.eq(other.path) &&
        eqPunct(fields, other.fields, fields.toList(), other.fields.toList()) { a, b -> a.eq(b) } &&
        dot2Token == other.dot2Token &&
        eqOpt(rest, other.rest) { a, b -> a.eq(b) }

private fun Expr.Try.eqVariant(other: Expr.Try): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        expr.eq(other.expr)

private fun Expr.TryBlock.eqVariant(other: Expr.TryBlock): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        block.eq(other.block)

private fun Expr.Tuple.eqVariant(other: Expr.Tuple): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqPunct(elems, other.elems, elems.toList(), other.elems.toList()) { a, b -> a.eq(b) }

private fun Expr.Unary.eqVariant(other: Expr.Unary): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        op.eq(other.op) &&
        expr.eq(other.expr)

private fun Expr.Unsafe.eqVariant(other: Expr.Unsafe): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        block.eq(other.block)

private fun Expr.While.eqVariant(other: Expr.While): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(label, other.label) { a, b -> a.eq(b) } &&
        cond.eq(other.cond) &&
        body.eq(other.body)

private fun Expr.Yield.eqVariant(other: Expr.Yield): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(expr, other.expr) { a, b -> a.eq(b) }

public fun Field.eq(other: Field): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        colonToken == other.colonToken &&
        ty.eq(other.ty) &&
        eqOpt(default, other.default) { a, b -> a.eq(b) }

public fun FieldModifiers.eq(other: FieldModifiers): Boolean = true

public fun FieldPat.eq(other: FieldPat): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        member.eq(other.member) &&
        colonToken == other.colonToken &&
        pat.eq(other.pat)

public fun FieldValue.eq(other: FieldValue): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        member.eq(other.member) &&
        colonToken == other.colonToken &&
        expr.eq(other.expr)

public fun FieldsNamed.eq(other: FieldsNamed): Boolean =
    eqPunct(named, other.named, named.toList(), other.named.toList()) { a, b -> a.eq(b) }

public fun FieldsUnnamed.eq(other: FieldsUnnamed): Boolean =
    eqPunct(unnamed, other.unnamed, unnamed.toList(), other.unnamed.toList()) { a, b -> a.eq(b) }

public fun File.eq(other: File): Boolean =
    shebang == other.shebang &&
        eqOpt(frontmatter, other.frontmatter) { a, b -> a.eq(b) } &&
        eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqList(items, other.items) { a, b -> a.eq(b) }

public fun FnArg.Receiver.eq(other: FnArg.Receiver): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mutability == other.mutability &&
        kind.eq(other.kind)

public fun FnModifiers.eq(other: FnModifiers): Boolean =
    defaultness == other.defaultness

public fun ForeignItem.Fn.eq(other: ForeignItem.Fn): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        sig.eq(other.sig)

public fun ForeignItem.Macro.eq(other: ForeignItem.Macro): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mac.eq(other.mac) &&
        semiToken == other.semiToken

public fun ForeignItem.Static.eq(other: ForeignItem.Static): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        safety.eq(other.safety) &&
        mutability.eq(other.mutability) &&
        ident == other.ident &&
        ty.eq(other.ty)

public fun ForeignItem.ItemType.eq(other: ForeignItem.ItemType): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        generics.eq(other.generics)

public fun Frontmatter.eq(other: Frontmatter): Boolean = true

public fun GenericParam.ConstParam.eq(other: GenericParam.ConstParam): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        ident == other.ident &&
        ty.eq(other.ty) &&
        eqOpt(default, other.default) { a, b -> a.eq(b) }

public fun GenericParam.LifetimeParam.eq(other: GenericParam.LifetimeParam): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        lifetime.eq(other.lifetime) &&
        colonToken == other.colonToken &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) }

public fun GenericParam.TypeParam.eq(other: GenericParam.TypeParam): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        ident == other.ident &&
        colonToken == other.colonToken &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) } &&
        eqOpt(default, other.default) { a, b -> a.eq(b) }

public fun Generics.eq(other: Generics): Boolean =
    ltToken == other.ltToken &&
        eqPunct(params, other.params, params.toList(), other.params.toList()) { a, b -> a.eq(b) } &&
        gtToken == other.gtToken &&
        eqOpt(whereClause, other.whereClause) { a, b -> a.eq(b) }

public fun IdentColon.eq(other: IdentColon): Boolean =
    ident == other.ident

public fun IfExpr.eq(other: IfExpr): Boolean =
    expr.eq(other.expr)

public fun ImplItem.Const.eq(other: ImplItem.Const): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        ty.eq(other.ty) &&
        expr.eq(other.expr)

public fun ImplItem.Fn.eq(other: ImplItem.Fn): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        sig.eq(other.sig) &&
        block.eq(other.block)

public fun ImplItem.Macro.eq(other: ImplItem.Macro): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mac.eq(other.mac) &&
        semiToken == other.semiToken

public fun ImplItem.AssocType.eq(other: ImplItem.AssocType): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        ty.eq(other.ty)

public fun ImplModifiers.eq(other: ImplModifiers): Boolean =
    defaultness == other.defaultness &&
        polarity == other.polarity

public fun Index.eq(other: Index): Boolean =
    index == other.index

public fun Item.Const.eq(other: Item.Const): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        ty.eq(other.ty) &&
        expr.eq(other.expr)

public fun Item.Enum.eq(other: Item.Enum): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        eqPunct(variants, other.variants, variants.toList(), other.variants.toList()) { a, b -> a.eq(b) }

public fun Item.ExternCrate.eq(other: Item.ExternCrate): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        ident == other.ident &&
        eqOpt(rename, other.rename) { a, b -> a.eq(b) }

public fun Item.Fn.eq(other: Item.Fn): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        sig.eq(other.sig) &&
        block.eq(other.block)

public fun Item.ForeignMod.eq(other: Item.ForeignMod): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        unsafety == other.unsafety &&
        abi.eq(other.abi) &&
        eqList(items, other.items) { a, b -> a.eq(b) }

public fun Item.Impl.eq(other: Item.Impl): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        unsafety == other.unsafety &&
        generics.eq(other.generics) &&
        eqOpt(traitPath, other.traitPath) { a, b -> a.eq(b) } &&
        selfType.eq(other.selfType) &&
        eqList(items, other.items) { a, b -> a.eq(b) }

public fun Item.Macro.eq(other: Item.Macro): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        ident == other.ident &&
        mac.eq(other.mac) &&
        semiToken == other.semiToken

public fun Item.Mod.eq(other: Item.Mod): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        unsafety == other.unsafety &&
        ident == other.ident &&
        eqOpt(content, other.content) { a, b -> a.eq(b) }

public fun Item.Static.eq(other: Item.Static): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        mutability.eq(other.mutability) &&
        ident == other.ident &&
        ty.eq(other.ty) &&
        expr.eq(other.expr)

public fun Item.Struct.eq(other: Item.Struct): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        fields.eq(other.fields) &&
        semiToken == other.semiToken

public fun Item.Trait.eq(other: Item.Trait): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        unsafety == other.unsafety &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        colonToken == other.colonToken &&
        eqPunct(supertraits, other.supertraits, supertraits.toList(), other.supertraits.toList()) { a, b -> a.eq(b) } &&
        eqList(items, other.items) { a, b -> a.eq(b) }

public fun Item.TraitAlias.eq(other: Item.TraitAlias): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) }

public fun Item.ItemType.eq(other: Item.ItemType): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        ty.eq(other.ty) &&
        whereClausePlacement.eq(other.whereClausePlacement)

public fun Item.Union.eq(other: Item.Union): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        fields.eq(other.fields)

public fun Item.Use.eq(other: Item.Use): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        vis.eq(other.vis) &&
        leadingColon == other.leadingColon &&
        tree.eq(other.tree)

public fun Label.eq(other: Label): Boolean =
    name.eq(other.name)

public fun LocalInit.eq(other: LocalInit): Boolean =
    expr.eq(other.expr) &&
        eqOpt(diverge, other.diverge) { a, b -> a.eq(b) }

public fun LocalModifiers.eq(other: LocalModifiers): Boolean = true

public fun Macro.eq(other: Macro): Boolean =
    path.eq(other.path) &&
        delimiter.eq(other.delimiter) &&
        tokenStreamEq(tokens, other.tokens)

public fun Meta.List.eq(other: Meta.List): Boolean =
    path.eq(other.path) &&
        delimiter.eq(other.delimiter) &&
        tokenStreamEq(tokens, other.tokens)

public fun Meta.NameValue.eq(other: Meta.NameValue): Boolean =
    path.eq(other.path) &&
        value.eq(other.value)

public fun Pat.Const.eq(other: Pat.Const): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        block.eq(other.block)

public fun Pat.Guard.eq(other: Pat.Guard): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        pat.eq(other.pat) &&
        guard.eq(other.guard)

public fun Pat.Ident.eq(other: Pat.Ident): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        byRef == other.byRef &&
        mutability == other.mutability &&
        ident == other.ident &&
        eqOpt(subpat, other.subpat) { a, b -> a.eq(b) }

public fun Pat.Lit.eq(other: Pat.Lit): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        lit.eq(other.lit)

public fun Pat.Macro.eq(other: Pat.Macro): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mac.eq(other.mac)

public fun Pat.Or.eq(other: Pat.Or): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        leadingVert == other.leadingVert &&
        eqPunct(cases, other.cases, cases.toList(), other.cases.toList()) { a, b -> a.eq(b) }

public fun Pat.PatParen.eq(other: Pat.PatParen): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        pat.eq(other.pat)

public fun Pat.Path.eq(other: Pat.Path): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(qself, other.qself) { a, b -> a.eq(b) } &&
        path.eq(other.path)

public fun Pat.Range.eq(other: Pat.Range): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(start, other.start) { a, b -> a.eq(b) } &&
        limits.eq(other.limits) &&
        eqOpt(end, other.end) { a, b -> a.eq(b) }

public fun Pat.Reference.eq(other: Pat.Reference): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mutability == other.mutability &&
        pat.eq(other.pat)

public fun Pat.Rest.eq(other: Pat.Rest): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) }

public fun Pat.Slice.eq(other: Pat.Slice): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqPunct(elems, other.elems, elems.toList(), other.elems.toList()) { a, b -> a.eq(b) }

public fun Pat.Struct.eq(other: Pat.Struct): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(qself, other.qself) { a, b -> a.eq(b) } &&
        path.eq(other.path) &&
        eqPunct(fields, other.fields, fields.toList(), other.fields.toList()) { a, b -> a.eq(b) } &&
        dot2Token == other.dot2Token &&
        eqOpt(rest, other.rest) { a, b -> a.eq(b) }

public fun Pat.Tuple.eq(other: Pat.Tuple): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqPunct(elems, other.elems, elems.toList(), other.elems.toList()) { a, b -> a.eq(b) }

public fun Pat.TupleStruct.eq(other: Pat.TupleStruct): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(qself, other.qself) { a, b -> a.eq(b) } &&
        path.eq(other.path) &&
        eqPunct(elems, other.elems, elems.toList(), other.elems.toList()) { a, b -> a.eq(b) }

public fun Pat.TypeAscription.eq(other: Pat.TypeAscription): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        pat.eq(other.pat) &&
        ty.eq(other.ty)

public fun Pat.Wild.eq(other: Pat.Wild): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) }

public fun PatColon.eq(other: PatColon): Boolean =
    pat.eq(other.pat)

public fun PatRest.eq(other: PatRest): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        dot2Token == other.dot2Token

public fun PatType.eq(other: PatType): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        pat.eq(other.pat) &&
        ty.eq(other.ty)

public fun Path.eq(other: Path): Boolean =
    leadingColon == other.leadingColon &&
        eqPunct(segments, other.segments, segments.toList(), other.segments.toList()) { a, b -> a.eq(b) }

public fun PathArguments.AngleBracketed.eq(other: PathArguments.AngleBracketed): Boolean =
    colon2Token == other.colon2Token &&
        eqPunct(args, other.args, args.toList(), other.args.toList()) { a, b -> a.eq(b) }

public fun PathArguments.Parenthesized.eq(other: PathArguments.Parenthesized): Boolean =
    eqPunct(inputs, other.inputs, inputs.toList(), other.inputs.toList()) { a, b -> a.eq(b) } &&
        output.eq(other.output)

public fun PathForPair.eq(other: PathForPair): Boolean =
    path.eq(other.path)

public fun PathSegment.eq(other: PathSegment): Boolean =
    ident == other.ident &&
        arguments.eq(other.arguments)

public fun QSelf.eq(other: QSelf): Boolean =
    ty.eq(other.ty) &&
        position == other.position &&
        asToken == other.asToken

public fun ReceiverKind.eq(other: ReceiverKind): Boolean =
    when {
        this is ReceiverKind.Value && other is ReceiverKind.Value -> true

        this is ReceiverKind.Reference && other is ReceiverKind.Reference -> {
            eqOpt(lifetime, other.lifetime) { a, b -> a.eq(b) } &&
                mutability == other.mutability
        }

        this is ReceiverKind.Typed && other is ReceiverKind.Typed -> ty.eq(other.ty)

        else -> false
    }

public fun Safety.eq(other: Safety): Boolean =
    when {
        this is Safety.Safe && other is Safety.Safe -> true
        this is Safety.Unsafe && other is Safety.Unsafe -> true
        this is Safety.Default && other is Safety.Default -> true
        else -> false
    }

public fun Signature.eq(other: Signature): Boolean =
    constness == other.constness &&
        asyncness == other.asyncness &&
        safety.eq(other.safety) &&
        eqOpt(abi, other.abi) { a, b -> a.eq(b) } &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        eqPunct(inputs, other.inputs, inputs.toList(), other.inputs.toList()) { a, b -> a.eq(b) } &&
        eqOpt(variadic, other.variadic) { a, b -> a.eq(b) } &&
        output.eq(other.output)

public fun Stmt.Local.eq(other: Stmt.Local): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        pat.eq(other.pat) &&
        eqOpt(init, other.init) { a, b -> a.eq(b) }

public fun Stmt.MacroStmt.eq(other: Stmt.MacroStmt): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mac.eq(other.mac) &&
        semiToken == other.semiToken

public fun SynType.Array.eq(other: SynType.Array): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        elem.eq(other.elem) &&
        len.eq(other.len)

public fun SynType.FnPtr.eq(other: SynType.FnPtr): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(lifetimes, other.lifetimes) { a, b -> a.eq(b) } &&
        unsafety == other.unsafety &&
        eqOpt(abi, other.abi) { a, b -> a.eq(b) } &&
        eqPunct(inputs, other.inputs, inputs.toList(), other.inputs.toList()) { a, b -> a.eq(b) } &&
        eqOpt(variadic, other.variadic) { a, b -> a.eq(b) } &&
        output.eq(other.output)

public fun SynType.Group.eq(other: SynType.Group): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        elem.eq(other.elem)

public fun SynType.ImplTrait.eq(other: SynType.ImplTrait): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) }

public fun SynType.Infer.eq(other: SynType.Infer): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) }

public fun SynType.Macro.eq(other: SynType.Macro): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mac.eq(other.mac)

public fun SynType.Never.eq(other: SynType.Never): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) }

public fun SynType.Paren.eq(other: SynType.Paren): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        elem.eq(other.elem)

public fun SynType.Path.eq(other: SynType.Path): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(qself, other.qself) { a, b -> a.eq(b) } &&
        path.eq(other.path)

public fun SynType.Ptr.eq(other: SynType.Ptr): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mutability.eq(other.mutability) &&
        elem.eq(other.elem)

public fun SynType.Reference.eq(other: SynType.Reference): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(lifetime, other.lifetime) { a, b -> a.eq(b) } &&
        mutability == other.mutability &&
        elem.eq(other.elem)

public fun SynType.Slice.eq(other: SynType.Slice): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        elem.eq(other.elem)

public fun SynType.TraitObject.eq(other: SynType.TraitObject): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        dynToken == other.dynToken &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) }

public fun SynType.Tuple.eq(other: SynType.Tuple): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqPunct(elems, other.elems, elems.toList(), other.elems.toList()) { a, b -> a.eq(b) }

public fun TraitBoundModifiers.eq(other: TraitBoundModifiers): Boolean = true

public fun TraitItem.Const.eq(other: TraitItem.Const): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        ty.eq(other.ty) &&
        eqOpt(default, other.default) { a, b -> a.eq(b) }

public fun TraitItem.Fn.eq(other: TraitItem.Fn): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        sig.eq(other.sig) &&
        eqOpt(default, other.default) { a, b -> a.eq(b) } &&
        semiToken == other.semiToken

public fun TraitItem.Macro.eq(other: TraitItem.Macro): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        mac.eq(other.mac) &&
        semiToken == other.semiToken

public fun TraitItem.AssocType.eq(other: TraitItem.AssocType): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        ident == other.ident &&
        generics.eq(other.generics) &&
        colonToken == other.colonToken &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) } &&
        eqOpt(default, other.default) { a, b -> a.eq(b) }

public fun TraitModifiers.eq(other: TraitModifiers): Boolean =
    autoToken == other.autoToken

public fun TypeModifiers.eq(other: TypeModifiers): Boolean =
    defaultness == other.defaultness

public fun TypeParamBound.PreciseCapture.eq(other: TypeParamBound.PreciseCapture): Boolean =
    eqPunct(params, other.params, params.toList(), other.params.toList()) { a, b -> a.eq(b) }

public fun TypeParamBound.Trait.eq(other: TypeParamBound.Trait): Boolean =
    parenToken == other.parenToken &&
        eqOpt(lifetimes, other.lifetimes) { a, b -> a.eq(b) } &&
        modifiers.eq(other.modifiers) &&
        maybe == other.maybe &&
        path.eq(other.path)

public fun UseTree.Glob.eq(other: UseTree.Glob): Boolean = true

public fun UseTree.Group.eq(other: UseTree.Group): Boolean =
    eqPunct(items, other.items, items.toList(), other.items.toList()) { a, b -> a.eq(b) }

public fun UseTree.Name.eq(other: UseTree.Name): Boolean =
    ident == other.ident &&
        eqOpt(rename, other.rename) { a, b -> a.eq(b) }

public fun UseTree.Path.eq(other: UseTree.Path): Boolean =
    ident == other.ident &&
        colon2Token == other.colon2Token &&
        eqOpt(tree, other.tree) { a, b -> a.eq(b) }

public fun Variadic.eq(other: Variadic): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(pat, other.pat) { a, b -> a.eq(b) } &&
        comma == other.comma

public fun Variant.eq(other: Variant): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        ident == other.ident &&
        fields.eq(other.fields) &&
        eqOpt(discriminant, other.discriminant) { a, b -> a.eq(b) }

public fun Visibility.Restricted.eq(other: Visibility.Restricted): Boolean =
    inToken == other.inToken &&
        path.eq(other.path)

public fun WhereClause.eq(other: WhereClause): Boolean =
    eqPunct(predicates, other.predicates, predicates.toList(), other.predicates.toList()) { a, b -> a.eq(b) }

public fun WhereClausePlacement.eq(other: WhereClausePlacement): Boolean =
    this == other

public fun WherePredicate.LifetimePredicate.eq(other: WherePredicate.LifetimePredicate): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        lifetime.eq(other.lifetime) &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) }

public fun WherePredicate.TypePredicate.eq(other: WherePredicate.TypePredicate): Boolean =
    eqList(attrs, other.attrs) { a, b -> a.eq(b) } &&
        eqOpt(lifetimes, other.lifetimes) { a, b -> a.eq(b) } &&
        boundedTy.eq(other.boundedTy) &&
        eqPunct(bounds, other.bounds, bounds.toList(), other.bounds.toList()) { a, b -> a.eq(b) }

private fun Expr.Verbatim.eqVariant(other: Expr.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun ForeignItem.Verbatim.eq(other: ForeignItem.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun ImplItem.Verbatim.eq(other: ImplItem.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun Item.Verbatim.eq(other: Item.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun Pat.Verbatim.eq(other: Pat.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun SynType.Verbatim.eq(other: SynType.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun TraitItem.Verbatim.eq(other: TraitItem.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun TypeParamBound.Verbatim.eq(other: TypeParamBound.Verbatim): Boolean =
    tokenStreamEq(tokens, other.tokens)

public fun LitBool.eq(other: LitBool): Boolean =
    this == other

public fun LitByte.eq(other: LitByte): Boolean =
    this == other

public fun LitByteStr.eq(other: LitByteStr): Boolean =
    this == other

public fun LitCStr.eq(other: LitCStr): Boolean =
    this == other

public fun LitChar.eq(other: LitChar): Boolean =
    this == other

public fun LitFloat.eq(other: LitFloat): Boolean =
    this == other

public fun LitInt.eq(other: LitInt): Boolean =
    this == other

public fun LitStr.eq(other: LitStr): Boolean =
    this == other

public fun AttrStyle.eq(other: AttrStyle): Boolean =
    when {
        this is AttrStyle.Outer && other is AttrStyle.Outer -> true
        this is AttrStyle.Inner && other is AttrStyle.Inner -> true
        else -> false
    }

public fun BinOp.eq(other: BinOp): Boolean =
    when {
        this is BinOp.Add && other is BinOp.Add -> true
        this is BinOp.Sub && other is BinOp.Sub -> true
        this is BinOp.Mul && other is BinOp.Mul -> true
        this is BinOp.Div && other is BinOp.Div -> true
        this is BinOp.Rem && other is BinOp.Rem -> true
        this is BinOp.And && other is BinOp.And -> true
        this is BinOp.Or && other is BinOp.Or -> true
        this is BinOp.BitXor && other is BinOp.BitXor -> true
        this is BinOp.BitAnd && other is BinOp.BitAnd -> true
        this is BinOp.BitOr && other is BinOp.BitOr -> true
        this is BinOp.Shl && other is BinOp.Shl -> true
        this is BinOp.Shr && other is BinOp.Shr -> true
        this is BinOp.Eq && other is BinOp.Eq -> true
        this is BinOp.Lt && other is BinOp.Lt -> true
        this is BinOp.Le && other is BinOp.Le -> true
        this is BinOp.Ne && other is BinOp.Ne -> true
        this is BinOp.Ge && other is BinOp.Ge -> true
        this is BinOp.Gt && other is BinOp.Gt -> true
        this is BinOp.AddAssign && other is BinOp.AddAssign -> true
        this is BinOp.SubAssign && other is BinOp.SubAssign -> true
        this is BinOp.MulAssign && other is BinOp.MulAssign -> true
        this is BinOp.DivAssign && other is BinOp.DivAssign -> true
        this is BinOp.RemAssign && other is BinOp.RemAssign -> true
        this is BinOp.BitXorAssign && other is BinOp.BitXorAssign -> true
        this is BinOp.BitAndAssign && other is BinOp.BitAndAssign -> true
        this is BinOp.BitOrAssign && other is BinOp.BitOrAssign -> true
        this is BinOp.ShlAssign && other is BinOp.ShlAssign -> true
        this is BinOp.ShrAssign && other is BinOp.ShrAssign -> true
        else -> false
    }

public fun CapturedParam.eq(other: CapturedParam): Boolean =
    when {
        this is CapturedParam.Lifetime && other is CapturedParam.Lifetime -> lifetime.eq(other.lifetime)
        this is CapturedParam.Ident && other is CapturedParam.Ident -> ident == other.ident
        else -> false
    }

public fun Data.eq(other: Data): Boolean =
    when {
        this is Data.Struct && other is Data.Struct -> value.eq(other.value)
        this is Data.Enum && other is Data.Enum -> value.eq(other.value)
        this is Data.Union && other is Data.Union -> value.eq(other.value)
        else -> false
    }

internal fun exprEq(left: Expr, right: Expr): Boolean =
    when {
        left is Expr.Array && right is Expr.Array -> left.eqVariant(right)
        left is Expr.Assign && right is Expr.Assign -> left.eqVariant(right)
        left is Expr.Async && right is Expr.Async -> left.eqVariant(right)
        left is Expr.Await && right is Expr.Await -> left.eqVariant(right)
        left is Expr.Binary && right is Expr.Binary -> left.eqVariant(right)
        left is Expr.BlockExpr && right is Expr.BlockExpr -> left.eqVariant(right)
        left is Expr.Break && right is Expr.Break -> left.eqVariant(right)
        left is Expr.Call && right is Expr.Call -> left.eqVariant(right)
        left is Expr.Cast && right is Expr.Cast -> left.eqVariant(right)
        left is Expr.Closure && right is Expr.Closure -> left.eqVariant(right)
        left is Expr.Const && right is Expr.Const -> left.eqVariant(right)
        left is Expr.Continue && right is Expr.Continue -> left.eqVariant(right)
        left is Expr.Field && right is Expr.Field -> left.eqVariant(right)
        left is Expr.ForLoop && right is Expr.ForLoop -> left.eqVariant(right)
        left is Expr.Group && right is Expr.Group -> left.eqVariant(right)
        left is Expr.If && right is Expr.If -> left.eqVariant(right)
        left is Expr.Index && right is Expr.Index -> left.eqVariant(right)
        left is Expr.Infer && right is Expr.Infer -> left.eqVariant(right)
        left is Expr.Let && right is Expr.Let -> left.eqVariant(right)
        left is Expr.Lit && right is Expr.Lit -> left.eqVariant(right)
        left is Expr.Loop && right is Expr.Loop -> left.eqVariant(right)
        left is Expr.Macro && right is Expr.Macro -> left.eqVariant(right)
        left is Expr.Match && right is Expr.Match -> left.eqVariant(right)
        left is Expr.MethodCall && right is Expr.MethodCall -> left.eqVariant(right)
        left is Expr.Paren && right is Expr.Paren -> left.eqVariant(right)
        left is Expr.Path && right is Expr.Path -> left.eqVariant(right)
        left is Expr.Range && right is Expr.Range -> left.eqVariant(right)
        left is Expr.RawAddr && right is Expr.RawAddr -> left.eqVariant(right)
        left is Expr.Reference && right is Expr.Reference -> left.eqVariant(right)
        left is Expr.Repeat && right is Expr.Repeat -> left.eqVariant(right)
        left is Expr.Return && right is Expr.Return -> left.eqVariant(right)
        left is Expr.Struct && right is Expr.Struct -> left.eqVariant(right)
        left is Expr.Try && right is Expr.Try -> left.eqVariant(right)
        left is Expr.TryBlock && right is Expr.TryBlock -> left.eqVariant(right)
        left is Expr.Tuple && right is Expr.Tuple -> left.eqVariant(right)
        left is Expr.Unary && right is Expr.Unary -> left.eqVariant(right)
        left is Expr.Unsafe && right is Expr.Unsafe -> left.eqVariant(right)
        left is Expr.While && right is Expr.While -> left.eqVariant(right)
        left is Expr.Yield && right is Expr.Yield -> left.eqVariant(right)
        left is Expr.Verbatim && right is Expr.Verbatim -> left.eqVariant(right)
        else -> false
    }

public fun Fields.eq(other: Fields): Boolean =
    when {
        this is Fields.Named && other is Fields.Named -> fields.eq(other.fields)
        this is Fields.Unnamed && other is Fields.Unnamed -> fields.eq(other.fields)
        this is Fields.Unit && other is Fields.Unit -> true
        else -> false
    }

public fun FnArg.eq(other: FnArg): Boolean =
    when {
        this is FnArg.Receiver && other is FnArg.Receiver -> this.eq(other)
        this is FnArg.Typed && other is FnArg.Typed -> this.eq(other)
        else -> false
    }

public fun FnArg.Typed.eq(other: FnArg.Typed): Boolean =
    patType.eq(other.patType)

public fun ForeignItem.eq(other: ForeignItem): Boolean =
    when {
        this is ForeignItem.Fn && other is ForeignItem.Fn -> this.eq(other)
        this is ForeignItem.Static && other is ForeignItem.Static -> this.eq(other)
        this is ForeignItem.ItemType && other is ForeignItem.ItemType -> this.eq(other)
        this is ForeignItem.Macro && other is ForeignItem.Macro -> this.eq(other)
        this is ForeignItem.Verbatim && other is ForeignItem.Verbatim -> this.eq(other)
        else -> false
    }

public fun GenericArgument.eq(other: GenericArgument): Boolean =
    when {
        this is GenericArgument.LifetimeArg && other is GenericArgument.LifetimeArg -> lifetime.eq(other.lifetime)
        this is GenericArgument.TypeArg && other is GenericArgument.TypeArg -> type.eq(other.type)
        this is GenericArgument.ConstArg && other is GenericArgument.ConstArg -> expr.eq(other.expr)
        this is GenericArgument.AssocTypeArg && other is GenericArgument.AssocTypeArg -> assoc.eq(other.assoc)
        this is GenericArgument.AssocConstArg && other is GenericArgument.AssocConstArg -> assoc.eq(other.assoc)
        this is GenericArgument.ConstraintArg && other is GenericArgument.ConstraintArg -> constraint.eq(other.constraint)
        else -> false
    }

public fun GenericParam.eq(other: GenericParam): Boolean =
    when {
        this is GenericParam.LifetimeParam && other is GenericParam.LifetimeParam -> this.eq(other)
        this is GenericParam.TypeParam && other is GenericParam.TypeParam -> this.eq(other)
        this is GenericParam.ConstParam && other is GenericParam.ConstParam -> this.eq(other)
        else -> false
    }

public fun ImplItem.eq(other: ImplItem): Boolean =
    when {
        this is ImplItem.Const && other is ImplItem.Const -> this.eq(other)
        this is ImplItem.Fn && other is ImplItem.Fn -> this.eq(other)
        this is ImplItem.AssocType && other is ImplItem.AssocType -> this.eq(other)
        this is ImplItem.Macro && other is ImplItem.Macro -> this.eq(other)
        this is ImplItem.Verbatim && other is ImplItem.Verbatim -> this.eq(other)
        else -> false
    }

public fun Item.eq(other: Item): Boolean =
    when {
        this is Item.Const && other is Item.Const -> this.eq(other)
        this is Item.Enum && other is Item.Enum -> this.eq(other)
        this is Item.ExternCrate && other is Item.ExternCrate -> this.eq(other)
        this is Item.Fn && other is Item.Fn -> this.eq(other)
        this is Item.ForeignMod && other is Item.ForeignMod -> this.eq(other)
        this is Item.Impl && other is Item.Impl -> this.eq(other)
        this is Item.Macro && other is Item.Macro -> this.eq(other)
        this is Item.Mod && other is Item.Mod -> this.eq(other)
        this is Item.Static && other is Item.Static -> this.eq(other)
        this is Item.Struct && other is Item.Struct -> this.eq(other)
        this is Item.Trait && other is Item.Trait -> this.eq(other)
        this is Item.TraitAlias && other is Item.TraitAlias -> this.eq(other)
        this is Item.ItemType && other is Item.ItemType -> this.eq(other)
        this is Item.Union && other is Item.Union -> this.eq(other)
        this is Item.Use && other is Item.Use -> this.eq(other)
        this is Item.Verbatim && other is Item.Verbatim -> this.eq(other)
        else -> false
    }

public fun Lit.eq(other: Lit): Boolean =
    when {
        this is Lit.Str && other is Lit.Str -> value.eq(other.value)
        this is Lit.ByteStr && other is Lit.ByteStr -> value.eq(other.value)
        this is Lit.CStr && other is Lit.CStr -> value.eq(other.value)
        this is Lit.Byte && other is Lit.Byte -> value.eq(other.value)
        this is Lit.Char && other is Lit.Char -> value.eq(other.value)
        this is Lit.Int && other is Lit.Int -> value.eq(other.value)
        this is Lit.Float && other is Lit.Float -> value.eq(other.value)
        this is Lit.Bool && other is Lit.Bool -> value.eq(other.value)
        this is Lit.Verbatim && other is Lit.Verbatim -> this == other
        else -> false
    }

public fun MacroDelimiter.eq(other: MacroDelimiter): Boolean =
    when {
        this is MacroDelimiter.Paren && other is MacroDelimiter.Paren -> true
        this is MacroDelimiter.Brace && other is MacroDelimiter.Brace -> true
        this is MacroDelimiter.Bracket && other is MacroDelimiter.Bracket -> true
        else -> false
    }

public fun Member.eq(other: Member): Boolean =
    when {
        this is Member.Named && other is Member.Named -> ident == other.ident
        this is Member.Unnamed && other is Member.Unnamed -> index.eq(other.index)
        else -> false
    }

public fun Meta.eq(other: Meta): Boolean =
    when {
        this is Meta.PathMeta && other is Meta.PathMeta -> path.eq(other.path)
        this is Meta.List && other is Meta.List -> this.eq(other)
        this is Meta.NameValue && other is Meta.NameValue -> this.eq(other)
        else -> false
    }

public fun Meta.PathMeta.eq(other: Meta.PathMeta): Boolean =
    path.eq(other.path)

public fun ModContent.eq(other: ModContent): Boolean =
    when {
        this is ModContent.Unnamed && other is ModContent.Unnamed -> true
        this is ModContent.Inline && other is ModContent.Inline -> eqList(items, other.items) { a, b -> a.eq(b) }
        else -> false
    }

public fun Pat.eq(other: Pat): Boolean =
    when {
        this is Pat.Const && other is Pat.Const -> this.eq(other)
        this is Pat.Guard && other is Pat.Guard -> this.eq(other)
        this is Pat.Ident && other is Pat.Ident -> this.eq(other)
        this is Pat.Lit && other is Pat.Lit -> this.eq(other)
        this is Pat.Macro && other is Pat.Macro -> this.eq(other)
        this is Pat.Or && other is Pat.Or -> this.eq(other)
        this is Pat.PatParen && other is Pat.PatParen -> this.eq(other)
        this is Pat.Path && other is Pat.Path -> this.eq(other)
        this is Pat.Range && other is Pat.Range -> this.eq(other)
        this is Pat.Reference && other is Pat.Reference -> this.eq(other)
        this is Pat.Rest && other is Pat.Rest -> this.eq(other)
        this is Pat.Slice && other is Pat.Slice -> this.eq(other)
        this is Pat.Struct && other is Pat.Struct -> this.eq(other)
        this is Pat.Tuple && other is Pat.Tuple -> this.eq(other)
        this is Pat.TupleStruct && other is Pat.TupleStruct -> this.eq(other)
        this is Pat.TypeAscription && other is Pat.TypeAscription -> this.eq(other)
        this is Pat.Wild && other is Pat.Wild -> this.eq(other)
        this is Pat.Verbatim && other is Pat.Verbatim -> this.eq(other)
        else -> false
    }

public fun PathArguments.eq(other: PathArguments): Boolean =
    when {
        this is PathArguments.None && other is PathArguments.None -> true
        this is PathArguments.AngleBracketed && other is PathArguments.AngleBracketed -> this.eq(other)
        this is PathArguments.Parenthesized && other is PathArguments.Parenthesized -> this.eq(other)
        else -> false
    }

public fun PointerMutability.eq(other: PointerMutability): Boolean =
    when {
        this is PointerMutability.Const && other is PointerMutability.Const -> true
        this is PointerMutability.Mut && other is PointerMutability.Mut -> true
        else -> false
    }

public fun RangeLimits.eq(other: RangeLimits): Boolean =
    when {
        this is RangeLimits.HalfOpen && other is RangeLimits.HalfOpen -> true
        this is RangeLimits.Closed && other is RangeLimits.Closed -> true
        else -> false
    }

public fun ReturnType.eq(other: ReturnType): Boolean =
    when {
        this is ReturnType.Default && other is ReturnType.Default -> true
        this is ReturnType.TypeReturn && other is ReturnType.TypeReturn -> ty.eq(other.ty)
        else -> false
    }

public fun StaticMutability.eq(other: StaticMutability): Boolean =
    when {
        this is StaticMutability.Mut && other is StaticMutability.Mut -> true
        this is StaticMutability.None && other is StaticMutability.None -> true
        else -> false
    }

public fun Stmt.eq(other: Stmt): Boolean =
    when {
        this is Stmt.Local && other is Stmt.Local -> this.eq(other)
        this is Stmt.ItemStmt && other is Stmt.ItemStmt -> item.eq(other.item)
        this is Stmt.ExprStmt && other is Stmt.ExprStmt -> expr.eq(other.expr) && semiToken == other.semiToken
        this is Stmt.MacroStmt && other is Stmt.MacroStmt -> this.eq(other)
        else -> false
    }

public fun Stmt.ItemStmt.eq(other: Stmt.ItemStmt): Boolean =
    item.eq(other.item)

public fun Stmt.ExprStmt.eq(other: Stmt.ExprStmt): Boolean =
    expr.eq(other.expr) && semiToken == other.semiToken

public fun SynType.eq(other: SynType): Boolean =
    when {
        this is SynType.Array && other is SynType.Array -> this.eq(other)
        this is SynType.FnPtr && other is SynType.FnPtr -> this.eq(other)
        this is SynType.Group && other is SynType.Group -> this.eq(other)
        this is SynType.ImplTrait && other is SynType.ImplTrait -> this.eq(other)
        this is SynType.Infer && other is SynType.Infer -> this.eq(other)
        this is SynType.Macro && other is SynType.Macro -> this.eq(other)
        this is SynType.Never && other is SynType.Never -> this.eq(other)
        this is SynType.Paren && other is SynType.Paren -> this.eq(other)
        this is SynType.Path && other is SynType.Path -> this.eq(other)
        this is SynType.Ptr && other is SynType.Ptr -> this.eq(other)
        this is SynType.Reference && other is SynType.Reference -> this.eq(other)
        this is SynType.Slice && other is SynType.Slice -> this.eq(other)
        this is SynType.TraitObject && other is SynType.TraitObject -> this.eq(other)
        this is SynType.Tuple && other is SynType.Tuple -> this.eq(other)
        this is SynType.Verbatim && other is SynType.Verbatim -> this.eq(other)
        else -> false
    }

public fun TraitItem.eq(other: TraitItem): Boolean =
    when {
        this is TraitItem.Const && other is TraitItem.Const -> this.eq(other)
        this is TraitItem.Fn && other is TraitItem.Fn -> this.eq(other)
        this is TraitItem.AssocType && other is TraitItem.AssocType -> this.eq(other)
        this is TraitItem.Macro && other is TraitItem.Macro -> this.eq(other)
        this is TraitItem.Verbatim && other is TraitItem.Verbatim -> this.eq(other)
        else -> false
    }

public fun TypeParamBound.eq(other: TypeParamBound): Boolean =
    when {
        this is TypeParamBound.Trait && other is TypeParamBound.Trait -> this.eq(other)
        this is TypeParamBound.LifetimeBound && other is TypeParamBound.LifetimeBound -> lifetime.eq(other.lifetime)
        this is TypeParamBound.PreciseCapture && other is TypeParamBound.PreciseCapture -> this.eq(other)
        this is TypeParamBound.Verbatim && other is TypeParamBound.Verbatim -> this.eq(other)
        else -> false
    }

public fun UnOp.eq(other: UnOp): Boolean =
    when {
        this is UnOp.Deref && other is UnOp.Deref -> true
        this is UnOp.NotOp && other is UnOp.NotOp -> true
        this is UnOp.Neg && other is UnOp.Neg -> true
        else -> false
    }

public fun UseTree.eq(other: UseTree): Boolean =
    when {
        this is UseTree.Path && other is UseTree.Path -> this.eq(other)
        this is UseTree.Name && other is UseTree.Name -> this.eq(other)
        this is UseTree.Group && other is UseTree.Group -> this.eq(other)
        this is UseTree.Glob && other is UseTree.Glob -> this.eq(other)
        else -> false
    }

public fun Visibility.eq(other: Visibility): Boolean =
    when {
        this is Visibility.Public && other is Visibility.Public -> true
        this is Visibility.Restricted && other is Visibility.Restricted -> this.eq(other)
        this is Visibility.Inherited && other is Visibility.Inherited -> true
        else -> false
    }

public fun WherePredicate.eq(other: WherePredicate): Boolean =
    when {
        this is WherePredicate.LifetimePredicate && other is WherePredicate.LifetimePredicate -> this.eq(other)
        this is WherePredicate.TypePredicate && other is WherePredicate.TypePredicate -> this.eq(other)
        else -> false
    }

internal fun punctuatedEq(left: SynPunctuated, right: SynPunctuated): Boolean {
    if (left::class != right::class) return false
    if (left.trailingPunct() != right.trailingPunct()) return false
    val a = left.iterator().asSequence().toList()
    val b = right.iterator().asSequence().toList()
    return eqList(a, b) { x, y -> anyAstEq(x, y) }
}

private fun anyAstEq(left: Any?, right: Any?): Boolean =
    when {
        left === right -> true
        left == null || right == null -> false
        left is CapturedParam && right is CapturedParam -> left.eq(right)
        left is Expr && right is Expr -> left.eq(right)
        left is Field && right is Field -> left.eq(right)
        left is FieldPat && right is FieldPat -> left.eq(right)
        left is FieldValue && right is FieldValue -> left.eq(right)
        left is FnArg && right is FnArg -> left.eq(right)
        left is GenericArgument && right is GenericArgument -> left.eq(right)
        left is GenericParam && right is GenericParam -> left.eq(right)
        left is Lifetime && right is Lifetime -> left.eq(right)
        left is NamedArg && right is NamedArg -> left.eq(right)
        left is Pat && right is Pat -> left.eq(right)
        left is PathSegment && right is PathSegment -> left.eq(right)
        left is SynType && right is SynType -> left.eq(right)
        left is TypeParamBound && right is TypeParamBound -> left.eq(right)
        left is UseTree && right is UseTree -> left.eq(right)
        left is Variant && right is Variant -> left.eq(right)
        left is WherePredicate && right is WherePredicate -> left.eq(right)
        else -> left == right
    }
