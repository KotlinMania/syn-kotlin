// port-lint: source sealed.rs
package io.github.kotlinmania.syn

public object Lookahead {
 /**
 * Marker interface used to seal the [Peek] hierarchy. The upstream codebase
 * restricts this interface to the library so that downstream packages cannot
 * implement [Peek]. Kotlin has no library-private visibility, so the
 * sealing is enforced by [Peek] itself being a `sealed interface` — all
 * implementations must live inside this module.
 */
 public interface Sealed
}
