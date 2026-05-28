// port-lint: source gen/visit_mut.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.*

/**
 * AST mutable-visitor trait — walks a syntax tree and can mutate nodes in place.
 *
 * Kotlin data classes provide `copy()` for immutable mutation, and sealed
 * hierarchies support `deepCopy()` for deep cloning.
 *
 * This `VisitMut` interface provides a mutable-visitor-style API. Override
 * methods to intercept and rewrite specific node types.
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
}
