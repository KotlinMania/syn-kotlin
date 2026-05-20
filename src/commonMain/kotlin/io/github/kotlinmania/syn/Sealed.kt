// port-lint: source sealed.rs
package io.github.kotlinmania.syn

public object Lookahead {
    /**
     * Marker interface used to seal the [Peek] hierarchy. The upstream Rust
     * declares this trait as `pub(crate)` so that downstream crates cannot
     * implement [Peek]. Kotlin has no crate-private visibility, so the
     * sealing is enforced by [Peek] itself being a `sealed interface` — all
     * implementations must live inside this module.
     */
    public interface Sealed
}
