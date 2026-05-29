// port-lint: source sealed.rs
package io.github.kotlinmania.syn

/**
 * Marker interface used to seal the [Peek] hierarchy. Restricts implementation
 * to the library so that downstream packages cannot add new peek targets.
 *
 * Kotlin enforces this by making [Peek] a `sealed interface`; all
 * implementations must live inside this module. The [Lookahead.Sealed]
 * inner interface provides an additional sealing layer for internal
 * dispatch in the lookahead mechanism.
 */
public object Lookahead {
    /**
     * Internal sealing interface for the lookahead dispatch chain.
     *
     * This parallels the Rust `private::Sealed` trait pattern, ensuring that
     * only the syn library itself can participate in the lookahead protocol.
     */
    public interface Sealed
}
