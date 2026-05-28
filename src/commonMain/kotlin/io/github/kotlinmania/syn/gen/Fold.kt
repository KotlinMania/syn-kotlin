// port-lint: source gen/fold.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.*

/**
 * AST fold trait — traverses a syntax tree and can rewrite every node.
 *
 * Kotlin sealed classes and data classes provide `copy()` and `deepCopy()`
 * as idiomatic alternatives; this file provides the companion `deepCopy`
 * methods on the relevant sealed hierarchies directly.
 *
 * The `Fold` interface itself is kept as a visitor-style hook for callers who
 * need to transform trees while traversing them.
 */
public interface Fold<T, R> {
    public fun fold(node: T): R
}
