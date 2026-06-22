// port-lint: source export.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream

/**
 * Re-export convenience aliases for types that live in other modules.
 *
 * Kotlin packages have flat visibility, so the re-exports that the upstream
 * Rust module provides via `pub use` are unnecessary: downstream consumers
 * import directly from `io.github.kotlinmania.procmacro2` and
 * `io.github.kotlinmania.quote`.
 *
 * This file exists for port-lint provenance tracking only.
 *
 * The original Rust `export.rs` also provides a `TransparentFn` type for
 * wrapping function pointers with `Span` information. In Kotlin, function
 * types are already first-class and carry no span data, so the wrapper is
 * not needed.
 */

/**
 * Wraps a function reference with an associated span, used for producing
 * better error messages during proc-macro execution.
 *
 * In Rust, `transparent_fn` wraps a `fn(TokenStream) -> TokenStream` with
 * its definition span. In Kotlin, function references are already
 * first-class, and span information is carried through the parse buffer.
 */
public class TransparentFn(
    public val fn: (TokenStream) -> TokenStream,
    public val span: Span,
)
