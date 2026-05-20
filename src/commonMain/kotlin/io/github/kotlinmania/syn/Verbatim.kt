// port-lint: source verbatim.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.append

internal fun verbatimBetween(begin: ParseStream, end: ParseStream): TokenStream {
    val endCursor = end.cursor()
    var cursor = begin.cursor()
    check(sameBuffer(endCursor, cursor)) {
        "verbatimBetween: begin and end must share the same TokenBuffer"
    }

    val tokens = TokenStream.new()
    while (cursor != endCursor) {
        val (tt, next) = cursor.tokenTree()
            ?: error("verbatimBetween: cursor advanced past end without reaching it")

        if (cmpAssumingSameBuffer(endCursor, next) < 0) {
            // A syntax node can cross the boundary of a None-delimited group
            // due to such groups being transparent to the parser in most cases.
            // Any time this occurs the group is known to be semantically
            // irrelevant. https://github.com/dtolnay/syn/issues/1235
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
