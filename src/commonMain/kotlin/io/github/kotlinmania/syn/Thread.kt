// port-lint: source thread.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import kotlin.native.HiddenFromObjC

/**
 * A container that binds a value to the thread on which it was created.
 *
 * Always returns its value. Thread-affinity checks are not needed in Kotlin.
 */
@HiddenFromObjC
public class ThreadBound<T> private constructor(
    private val value: T,
) {
    public companion object {
        public fun <T> new(value: T): ThreadBound<T> = ThreadBound(value)
    }

    public fun get(): T = value

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean =
        other is ThreadBound<*> && value == other.value

    override fun hashCode(): Int = value?.hashCode() ?: 0

    public fun deepCopy(): ThreadBound<T> = ThreadBound(value)
}
