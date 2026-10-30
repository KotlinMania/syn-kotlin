package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ErrorTest {
    @Test
    fun failedResultPreservesTheDiagnosticAtTheThrowBoundary() {
        val error = SynError.new(Span.callSite(), "invalid syntax")
        val result: SynResult<TokenStream> = SynResult.failure(error)

        assertTrue(result.isFailure)
        assertNull(result.getOrNull())
        assertSame(error, result.exceptionOrNull())
        assertSame(error, result.map { it }.exceptionOrNull())
        val thrown = assertFailsWith<IllegalArgumentException> { result.getOrThrow() }
        assertSame(error, assertIs<SynParseException>(thrown).diagnostic)
        assertEquals(error.message, thrown.message)
        assertEquals("invalid syntax", error.message)
    }

    @Test
    fun resultAndErrorUseSynTypes() {
        val ok: SynResult<Int> = SynResult.success(7)
        val err: SynError = SynError.new(Span.callSite(), "nope")

        assertEquals(7, ok.getOrThrow())
        assertEquals("nope", err.toString())
    }

    @Test
    fun iterYieldsSeparateErrors() {
        val error = SynError.new(Span.callSite(), "first")
        error.combine(SynError.new(Span.callSite(), "second"))

        val iter = error.iter()

        assertIs<Iter>(iter)
        assertTrue(iter.hasNext())
        assertEquals("first", iter.next().toString())
        assertTrue(iter.hasNext())
        assertEquals("second", iter.next().toString())
        assertFalse(iter.hasNext())
    }

    @Test
    fun intoIterYieldsSeparateErrors() {
        val error = SynError.new(Span.callSite(), "first")
        error.combine(SynError.new(Span.callSite(), "second"))

        val iter = error.intoIter()

        assertIs<IntoIter>(iter)
        assertEquals("first", iter.next().toString())
        assertEquals("second", iter.next().toString())
        assertFalse(iter.hasNext())
    }

    @Test
    fun extendCombinesEachError() {
        val error = SynError.new(Span.callSite(), "first")
        val others =
            listOf(
                SynError.new(Span.callSite(), "second"),
                SynError.new(Span.callSite(), "third"),
            )

        error.extend(others)

        assertEquals(
            listOf("first", "second", "third"),
            error
                .iter()
                .asSequence()
                .map { it.toString() }
                .toList(),
        )
    }

    @Test
    fun displayAndDebugFormattingFollowRustShape() {
        val error = SynError.new(Span.callSite(), "first")

        assertEquals("first", error.toString())
        assertEquals("Error(\"first\")", error.debugString())

        error.combine(SynError.new(Span.callSite(), "line\nsecond"))

        assertEquals("first", error.toString())
        assertEquals("Error([\"first\", \"line\\nsecond\"])", error.debugString())
    }
}
