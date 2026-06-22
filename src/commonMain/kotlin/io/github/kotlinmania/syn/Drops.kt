// port-lint: source drops.rs
package io.github.kotlinmania.syn

/**
 * A wrapper that prevents the Kotlin runtime from calling finalizers on
 * wrapped values whose drop semantics are trivial.
 *
 * In Rust, `NoDrop<T>` wraps `ManuallyDrop<T>` and prevents the `Drop`
 * trait from running. In Kotlin, all objects are GC-managed, so this
 * wrapper is transparent: it simply holds the value and delegates
 * access to it.
 */
public class NoDrop<T>(
    public val value: T,
) {
    /** Returns the wrapped value. */
    public fun get(): T = value

    override fun toString(): String = "NoDrop($value)"

    override fun equals(other: Any?): Boolean =
        other is NoDrop<*> && value == other.value

    override fun hashCode(): Int = value?.hashCode() ?: 0

    public fun deepCopy(): NoDrop<T> = NoDrop(value)
}

/**
 * Marker for types whose drop implementation is trivial (does nothing).
 *
 * In Rust this is a trait bound that prevents `NoDrop` from being
 * instantiated for types that need their `Drop` impl to run. In Kotlin,
 * all types are trivially droppable since the GC handles cleanup, so
 * this interface carries no runtime significance beyond documentation.
 */
public interface TrivialDrop
