// port-lint: source tests/test_punctuated.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.token.Comma
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** A token-like integer wrapper for testing Punctuated with ToTokens bounds. */
private data class IntToken(val v: Int) : io.github.kotlinmania.quote.ToTokens {
    override fun toTokens(tokens: io.github.kotlinmania.procmacro2.TokenStream) {
        val lit = io.github.kotlinmania.procmacro2.Literal.i32Suffixed(v)
        tokens.extendTokenTrees(listOf(io.github.kotlinmania.procmacro2.TokenTree.Literal(lit)))
    }
}

private fun punctuatedIntComma(vararg values: Int): Punctuated<IntToken, Comma> {
    val seq = Punctuated.new<IntToken, Comma>()
    for (value in values) {
        seq.push(IntToken(value), Comma::default)
    }
    return seq
}

class PunctuatedTest {
    @Test
    fun pairs() {
        val p = punctuatedIntComma(2, 3, 4)

        val pairsList = p.intoPairs()
        assertEquals(3, pairsList.size)
        val lastPair = pairsList.last()
        assertEquals(4, lastPair.intoValue().v)
    }

    @Test
    fun iter() {
        val p = punctuatedIntComma(2, 3, 4)

        val values = p.toList()
        assertEquals(3, values.size)
        assertEquals(4, values.last().v)
    }

    @Test
    fun mayDangle() {
        val p = punctuatedIntComma(2, 3, 4)
        for (element in p.toList()) {
            if (element.v == 2) {
                break
            }
        }

        val q = punctuatedIntComma(2, 3, 4)
        for (element in q.toList()) {
            if (element.v == 2) {
                break
            }
        }
    }

    @Test
    fun indexOutOfBounds() {
        val p = Punctuated.new<IntToken, Comma>()
        assertFailsWith<IndexOutOfBoundsException> {
            p[0]
        }
    }
}
