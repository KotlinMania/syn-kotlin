// port-lint: source export.rs
package io.github.kotlinmania.syn

/**
 * Re-export convenience aliases for types that live in other modules.
 *
 * Kotlin packages have flat visibility, so the re-exports that the upstream
 * module provides are unnecessary; downstream consumers import directly from
 * `io.github.kotlinmania.procmacro2` and `io.github.kotlinmania.quote`.
 *
 * This file exists for port-lint provenance tracking only.
 */
