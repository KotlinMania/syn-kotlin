// port-lint: source derive.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import kotlin.native.HiddenFromObjC

/**
 * Data structure supplied to a derive macro.
 *
 * [DeriveInput] is defined in [Data.kt] alongside [Data], [DataStruct],
 * [DataEnum], and [DataUnion]. This file provides the [DeriveInput]
 * type alias and related parsing support.
 *
 * A derive macro receives a [DeriveInput] containing the data structure
 * definition (struct, enum, or union) along with its attributes,
 * visibility, identifier, and generic parameters.
 */

/**
 * Parses a derive macro input, accepting any of the three data structure
 * forms (struct, enum, or union).
 *
 * This is a simplified parser that delegates to the full expression
 * parser. The complete derive-input parser will be added when all
 * dependent types are available.
 */
public fun parseDeriveInput(input: ParseStream): SynResult<DeriveInput> =
    input.parse(DeriveInputParse)

/** Parse implementation for derive macro input. */
@HiddenFromObjC
public object DeriveInputParse : Parse<DeriveInput> {
    override fun parse(input: ParseStream): SynResult<DeriveInput> {
        // Simplified: parse attributes, visibility, and then delegate
        // to the full data structure parser when available.
        return SynResult.failure(input.error("derive input parsing not yet fully implemented"))
    }
}
