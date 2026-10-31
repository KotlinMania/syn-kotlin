// port-lint: source verbatim.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.TokenStream

internal fun between(
    begin: ParseStream,
    end: ParseStream,
): TokenStream {
    val endCursor = end.cursor()
    var cursor = begin.cursor()
    check(sameBuffer(endCursor, cursor)) {
        "assertion failed: sameBuffer(end, cursor)"
    }

    val tokens = TokenStream.new()
    while (cursor != endCursor) {
        val (tt, next) =
            cursor.tokenTree()
                ?: error("called unwrap on a null value")

        if (cmpAssumingSameBuffer(endCursor, next) < 0) {
            // A syntax node can cross the boundary of a None-delimited group
            // due to such groups being transparent to the parser in most cases.
            // Any time this occurs the group is known to be semantically
            // irrelevant. https://github.com/dtolnay/syn/issues/1235
            val noneGroup = cursor.group(Delimiter.None)
            if (noneGroup != null) {
                val (inside, _, after) = noneGroup
                check(next == after) { "assertion failed: next == after" }
                cursor = inside
                continue
            } else {
                error("verbatim end must not be inside a delimited group")
            }
        }

        tokens.appendTokenTree(tt)
        cursor = next
    }
    return tokens
}
