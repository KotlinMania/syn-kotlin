// port-lint: source gen/eq.rs
package io.github.kotlinmania.syn.gen

/**
 * Equality implementations for syn AST types.
 *
 * Kotlin data classes automatically generate `equals()` and `hashCode()`,
 * and sealed hierarchies use `is` checks for dispatch. No additional code is
 * needed beyond what the data class synthesis provides.
 *
 * This file exists for port-lint provenance tracking only.
 */
