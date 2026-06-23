// port-lint: source drops.rs
package io.github.kotlinmania.syn

/** A transparent wrapper for values whose cleanup is known to be trivial. */
public class NoDrop(
    public val value: Any?,
) {
    public typealias NeedsDrop = Unit

    public companion object {
        public fun new(value: Any?): NoDrop = NoDrop(value)
    }

    public fun get(): Any? = value

    public fun deref(): Any? = value

    public fun derefMut(): Any? = value

    public fun drop() {}

    override fun toString(): String = "NoDrop($value)"

    override fun equals(other: Any?): Boolean =
        other is NoDrop && value == other.value

    override fun hashCode(): Int = value?.hashCode() ?: 0

    public fun deepCopy(): NoDrop = NoDrop(value)
}

/** Marker for types whose cleanup is known to be trivial. */
public interface TrivialDrop