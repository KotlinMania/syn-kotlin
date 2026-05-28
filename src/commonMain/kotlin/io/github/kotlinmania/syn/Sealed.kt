// port-lint: source sealed.rs
package io.github.kotlinmania.syn

/**
 * Marker interface used to seal the [Peek] hierarchy. Restricts implementation
 * to the library so that downstream packages cannot add new peek targets.
 *
 * Kotlin enforces this by making [Peek] a `sealed interface`; all
 * implementations must live inside this module.
 */
public object Lookahead {
    public interface Sealed
}
