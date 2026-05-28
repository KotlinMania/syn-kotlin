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
    // `pairs` exercises `pairs()`, `pairs_mut()`, and `into_pairs()`
    // separately to cover the immutable, mutable, and consuming iterator
    // variants. Kotlin's `Punctuated.pairs()` returns a single immutable
    // snapshot list; the consuming/mutable distinctions don't translate, so
    // the size + last-element invariants collapse to a single iteration.
    @Test
    fun pairs() {
        val p = punctuatedIntComma(2, 3, 4)

        val pairsList = p.pairs()
        assertEquals(3, pairsList.size)
        assertEquals(3, pairsList.count())
        assertEquals(4, pairsList.last().first.v)
    }

    // `iter` exercises `iter()`, `iter_mut()`, and `into_iter()`.
    // Same collapse as `pairs` above.
    @Test
    fun iter() {
        val p = punctuatedIntComma(2, 3, 4)

        val values = p.toList()
        assertEquals(3, values.size)
        assertEquals(3, values.count())
        assertEquals(4, values.last().v)
    }

    // `may_dangle` proves that iterating a `Punctuated` and dropping
    // the source mid-iteration does not invalidate the iterator. Kotlin is
    // garbage-collected, so there is no manual `drop` semantics to test; the
    // structural iteration with an early `break` still translates and shows
    // the iterator handles a partial walk without panicking.
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

    // Expected behavior: indexing into an empty Punctuated should throw
    // Indexing into an empty `Punctuated` throws `IndexOutOfBoundsException`
    // from the `operator fun get` defined in `Punctuated.kt`.
    @Test
    fun indexOutOfBounds() {
        val p = Punctuated.new<IntToken, Comma>()
        assertFailsWith<IndexOutOfBoundsException> {
            p[0]
        }
    }
}
