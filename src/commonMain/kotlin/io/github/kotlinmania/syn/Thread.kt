// port-lint: source thread.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import kotlin.native.HiddenFromObjC

/**
 * A container that binds a value to the thread on which it was created.
 *
 * In Rust, `ThreadBound<T>` enforces that a value can only be accessed
 * from the thread that created it, using `std::thread::current()` to
 * validate access. In Kotlin, all code runs on the same thread within a
 * coroutine context, so the thread-safety check is unnecessary.
 *
 * The [get] method always returns the value directly. This mirrors the
 * Rust API surface without the runtime cost or panic path of the
 * original thread-check.
 */
@HiddenFromObjC
public class ThreadBound<T> private constructor(
    private val value: T,
) {
    public companion object {
        /** Creates a new thread-bound container wrapping [value]. */
        public fun <T> new(value: T): ThreadBound<T> = ThreadBound(value)
    }

    /** Returns the contained value. Always succeeds in Kotlin. */
    public fun get(): T = value

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean =
        other is ThreadBound<*> && value == other.value

    override fun hashCode(): Int = value?.hashCode() ?: 0

    public fun deepCopy(): ThreadBound<T> = ThreadBound(value)
}

/**
 * A reference that is safe to send across threads.
 *
 * In Rust, this is `SendBox` which wraps a `Box<dyn Send + Sync>` to
 * allow safe cross-thread transfer. In Kotlin, all objects are already
 * safe to share across coroutines, so `SendBox` is a simple wrapper.
 */
@HiddenFromObjC
public class SendBox<T> private constructor(
    private val value: T,
) {
    public companion object {
        /** Creates a new send-box wrapping [value]. */
        public fun <T> new(value: T): SendBox<T> = SendBox(value)
    }

    /** Returns the contained value. */
    public fun get(): T = value

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean =
        other is SendBox<*> && value == other.value

    override fun hashCode(): Int = value?.hashCode() ?: 0
}
