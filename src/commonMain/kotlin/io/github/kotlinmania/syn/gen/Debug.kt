// port-lint: source gen/debug.rs
package io.github.kotlinmania.syn.gen

/**
 * Debug (toString) implementations for syn AST types.
 *
 * Kotlin data classes automatically generate `toString()` via their primary
 * constructor properties, and sealed hierarchies delegate to their subclasses.
 * No additional code is needed beyond what data class synthesis provides.
 *
 * This file exists for port-lint provenance tracking only.
 */

internal const val DebugMarker: String = ""
