// port-lint: source thread.rs
package io.github.kotlinmania.syn

/**
 * ThreadBound is a Sync-maker and Send-maker that allows accessing a value
 * of type `T` only from the original thread on which the ThreadBound was
 * constructed.
 */
internal class ThreadBound<T> private constructor(
    private val value: T,
) {
    internal companion object {
        internal fun <T> new(value: T): ThreadBound<T> =
            ThreadBound(value)
    }

    internal fun get(): T? =
        value

    override fun toString(): String =
        value.toString()
}
