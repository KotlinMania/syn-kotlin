// port-lint: source thread.rs
package io.github.kotlinmania.syn

internal expect fun currentThreadBoundToken(): Any

/**
 * ThreadBound is a Sync-maker and Send-maker that allows accessing a value
 * of type T only from the original thread on which the ThreadBound was
 * constructed.
 */
internal class ThreadBound<T> private constructor(
    private val value: T,
    private val threadId: Any,
) {
    internal companion object {
        internal fun <T> new(value: T): ThreadBound<T> =
            ThreadBound(value, currentThreadBoundToken())
    }

    internal fun get(): T? =
        if (currentThreadBoundToken() == threadId) value else null

    override fun toString(): String =
        when (val value = get()) {
            null -> "unknown"
            else -> value.toString()
        }

    internal fun fmt(): String = toString()

    // Copy the value even when the currently running thread is the wrong
    // thread. The original thread must not simultaneously mutate the value
    // through interior mutability, which would be a data race.
    //
    // In the source language, the Copy bound guarantees that the value has no
    // interior mutability. That guarantee depends on all interior mutability
    // being built on UnsafeCell, which does not implement Copy. This needs
    // revisiting if that restriction is relaxed.
    internal fun clone(): ThreadBound<T> = ThreadBound(value, threadId)
}
