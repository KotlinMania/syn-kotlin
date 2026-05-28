// port-lint: source gen/hash.rs
package io.github.kotlinmania.syn.gen

/**
 * Hash implementations for syn AST types.
 *
 * In Rust syn, `gen/hash.rs` provides `Hash` impls for every syntax tree type.
 * Kotlin data classes automatically generate `hashCode()`. No additional code
 * is needed beyond what the data class synthesis provides.
 *
 * This file exists for port-lint provenance tracking only.
 */
