// port-lint: source export.rs
package io.github.kotlinmania.syn

/**
 * Re-export convenience aliases for types that live in other modules.
 *
 * In Rust, `export.rs` re-exports items from `std`, `proc_macro2`, and `quote`
 * under a private module so that the `custom_keyword!` and `custom_punctuation!`
 * macros can reference them without qualifying the paths. Kotlin packages have
 * flat visibility, so the re-exports are unnecessary; downstream consumers
 * import directly from `io.github.kotlinmania.procmacro2` and
 * `io.github.kotlinmania.quote`.
 *
 * This file exists for port-lint provenance tracking only.
 */
