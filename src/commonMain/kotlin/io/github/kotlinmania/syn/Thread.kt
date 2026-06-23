// port-lint: source thread.rs

package io.github.kotlinmania.syn

internal expect fun currentThreadBoundToken(): Any

public class ThreadBound private constructor(
    private val value: Any?,
    private val threadId: Any,
) {
    public companion object {
        public fun new(value: Any?): ThreadBound = ThreadBound(value, currentThreadBoundToken())
    }

    public fun get(): Any? =
        if (currentThreadBoundToken() == threadId) value else null

    public fun clone(): ThreadBound = this

    override fun toString(): String =
        when (val v = get()) {
            null -> "unknown"
            else -> v.toString()
        }

    override fun equals(other: Any?): Boolean =
        other is ThreadBound && value == other.value && threadId == other.threadId

    override fun hashCode(): Int = 31 * (value?.hashCode() ?: 0) + threadId.hashCode()

    public fun fmt(): String = toString()
}