// port-lint: source verbatim.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.append

/**
 * Extracts the token stream between two parse positions without interpreting
 * the content. This is used for "verbatim" copying of token sequences.
 *
 * This is the public entry point; the internal implementation is in
 * [verbatimBetween].
 */
public fun between(begin: ParseStream, end: ParseStream): TokenStream =
    verbatimBetween(begin, end)

internal fun verbatimBetween(begin: ParseStream, end: ParseStream): TokenStream {
    val endCursor = end.cursor()
    var cursor = begin.cursor()
    check(sameBuffer(endCursor, cursor)) {
        "verbatimBetween: begin and end must share the same TokenBuffer"
    }

    val tokens = TokenStream.new()
    while (cursor != endCursor) {
        val (tt, next) =
            cursor.tokenTree()
                ?: error("verbatimBetween: cursor advanced past end without reaching it")

        if (cmpAssumingSameBuffer(endCursor, next) < 0) {
            val groupTriple = cursor.group(Delimiter.None)
            if (groupTriple != null) {
                val (inside, _, after) = groupTriple
                check(next == after) { "verbatim None-group end mismatch" }
                cursor = inside
                continue
            } else {
                error("verbatim end must not be inside a delimited group")
            }
        }

        tokens.append(tt)
        cursor = next
    }
    return tokens
}
