// port-lint: source gen/clone.rs
package io.github.kotlinmania.syn.gen

/**
 * Clone implementations for syn AST types.
 *
 * Kotlin data classes automatically generate `copy()` and structural
 * `equals`/`hashCode`, so explicit clone implementations are not needed.
 * Sealed-class hierarchies provide `deepCopy()` on each subclass for deep
 * cloning that duplicates mutable `Punctuated` and `List` fields.
 *
 * This file exists for port-lint provenance tracking only.
 */
