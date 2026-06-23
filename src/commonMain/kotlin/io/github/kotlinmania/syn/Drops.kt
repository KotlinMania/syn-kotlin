// port-lint: source drops.rs
package io.github.kotlinmania.syn

/** A transparent wrapper for values whose cleanup is known to be trivial. */
public class NoDrop<T>(
    public val value: T,
) {
    public typealias Target = T
    public typealias NeedsDrop = Unit

    public companion object {
        public fun <T> new(value: T): NoDrop<T> = NoDrop(value)
    }

    public fun get(): T = value

    public fun deref(): T = value

    public fun derefMut(): T = value

    public fun drop() {}

    override fun toString(): String = "NoDrop($value)"

    override fun equals(other: Any?): Boolean =
        other is NoDrop<*> && value == other.value

    override fun hashCode(): Int = value?.hashCode() ?: 0

    public fun deepCopy(): NoDrop<T> = NoDrop(value)
}

/** Marker for types whose cleanup is known to be trivial. */
public interface TrivialDrop
