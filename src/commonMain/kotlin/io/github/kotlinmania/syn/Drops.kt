// port-lint: source drops.rs
package io.github.kotlinmania.syn

/**
 * A wrapper that prevents the Kotlin runtime from calling finalizers on
 * wrapped values whose drop semantics are trivial.
 *
 * All Kotlin objects are managed by the GC, so the `NoDrop` wrapper is a no-op.
 */
public class NoDrop<T>(public val value: T)
