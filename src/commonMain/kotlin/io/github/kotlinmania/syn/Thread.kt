// port-lint: source thread.rs

package io.github.kotlinmania.syn

internal expect fun currentThreadBoundToken(): Any

/** A container that binds a value to the context on which it was created. */
public class ThreadBound<T> private constructor(
    private val value: T,
    private val threadId: Any,
) {
    public companion object {
        /** Creates a new thread-bound container wrapping [value]. */
        public fun <T> new(value: T): ThreadBound<T> = ThreadBound(value, currentThreadBoundToken())
    }

    /** Returns the contained value when accessed from the context that created it. */
    public fun get(): T? =
        if (currentThreadBoundToken() == threadId) value else null

    public fun clone(): ThreadBound<T> = ThreadBound(value, threadId)

    override fun toString(): String = get()?.toString() ?: "unknown"

    override fun equals(other: Any?): Boolean =
        other is ThreadBound<*> && value == other.value && threadId == other.threadId

    override fun hashCode(): Int = 31 * (value?.hashCode() ?: 0) + threadId.hashCode()

    public fun deepCopy(): ThreadBound<T> = clone()
}

/** A reference that is safe to send across contexts. */
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
