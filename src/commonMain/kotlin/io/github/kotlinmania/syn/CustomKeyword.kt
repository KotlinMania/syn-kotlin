// port-lint: source custom_keyword.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import kotlin.native.HiddenFromObjC

/**
 * Support for defining custom keywords that can be used as tokens in parsing.
 *
 * Use [customKeyword] to create a [Peek] and [Parse] pair for any identifier
 * string that is not already a built-in keyword.
 */
@HiddenFromObjC
public fun customKeyword(name: String): Pair<Peek, Parse<Ident>> {
    val peek = CustomKeywordPeek(name)
    val parse = CustomKeywordParse(name)
    return Pair<Peek, Parse<Ident>>(peek, parse)
}

/** Peek implementation for a custom keyword. */
@HiddenFromObjC
public class CustomKeywordPeek(private val name: String) : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == name
    }
    override fun display(): String = "`$name`"
}

/** Parse implementation for a custom keyword. */
@HiddenFromObjC
public class CustomKeywordParse(private val name: String) : Parse<Ident> {
    override fun parse(input: ParseStream): SynResult<Ident> =
        input.step { cursor ->
            val (ident, rest) = cursor.ident() ?: return@step SynResult.failure(cursor.error("expected `$name`"))
            if (ident.toString() != name) return@step SynResult.failure(cursor.error("expected `$name`"))
            SynResult.success(Ident.new(name, ident.span()) to rest)
        }
}
