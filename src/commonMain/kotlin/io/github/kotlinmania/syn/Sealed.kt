// port-lint: source sealed.rs
package io.github.kotlinmania.syn

/**
 * Private lookahead sealing surface. Implementing this marker is restricted to
 * Syn's own token marker types.
 */
internal object Lookahead {
    /**
     * Marker for lookahead tokens that are copyable value tokens in the
     * upstream parser.
     */
    internal interface Sealed<T : Sealed<T>> {
        fun copy(): T
    }
}
