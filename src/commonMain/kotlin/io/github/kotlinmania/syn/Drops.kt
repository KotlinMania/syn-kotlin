// port-lint: source drops.rs
package io.github.kotlinmania.syn

/**
 * A wrapper that prevents the Kotlin runtime from calling finalizers on
 * wrapped values whose drop semantics are trivial.
 *
 * Kotlin has garbage collection, so the `NoDrop` wrapper is a no-op:
 * all Kotlin objects are managed by the GC, and there is no deterministic
 * destructor to suppress.
 */
public class NoDrop<T>(public val value: T)
