// port-lint: source gen/visit.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.*

/**
 * AST visitor trait — walks a syntax tree without mutating it.
 *
 * Kotlin sealed classes let you achieve the same with exhaustive `when`
 * expressions on each sealed hierarchy.
 *
 * This `Visit` interface provides a visitor-style API for callers who need
 * to walk trees without rewriting them. Default implementations do nothing
 * (no-op traversal); override the methods you care about.
 */
public open class Visit {
    public open fun visitExpr(e: Expr) { /* default: no-op */ }
    public open fun visitType(t: SynType) { /* default: no-op */ }
    public open fun visitPath(p: Path) { /* default: no-op */ }
    public open fun visitPat(p: Pat) { /* default: no-op */ }
    public open fun visitItem(i: Item) { /* default: no-op */ }
    public open fun visitAttribute(a: Attribute) { /* default: no-op */ }
    public open fun visitMeta(m: Meta) { /* default: no-op */ }
    public open fun visitGenerics(g: Generics) { /* default: no-op */ }
    public open fun visitLit(l: Lit) { /* default: no-op */ }
    public open fun visitLifetime(lt: Lifetime) { /* default: no-op */ }
    public open fun visitIdent(id: Ident) { /* default: no-op */ }
    public open fun visitStmt(s: Stmt) { /* default: no-op */ }
    public open fun visitData(d: Data) { /* default: no-op */ }
    public open fun visitDeriveInput(di: DeriveInput) { /* default: no-op */ }
}
