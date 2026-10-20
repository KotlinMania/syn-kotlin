package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ConcreteParseResultTest {
    @Test
    fun parsesAConcreteFileWithItsShebangAndAttributes() {
        val result: FileParseResult = parseFile("\uFEFF#!/usr/bin/env rustx\n#![allow(dead_code)]\nfn main() {}")
        val file: File = result.getOrThrow()

        assertTrue(result.isSuccess)
        assertFalse(result.isFailure)
        assertNull(result.error)
        assertSame(file, result.value)
        assertSame(file, result.getOrNull())
        assertEquals("#!/usr/bin/env rustx", file.shebang)
        assertEquals(1, file.attrs.size)
        assertEquals(1, file.items.size)
    }

    @Test
    fun malformedFileReturnsItsDiagnostic() {
        val result = parseFile("trait Service { async fn broken(")

        assertTrue(result.isFailure)
        assertFalse(result.isSuccess)
        assertNull(result.value)
        assertNull(result.getOrNull())
        val diagnostic = assertNotNull(result.error)
        assertSame(diagnostic, result.exceptionOrNull())
        assertTrue(diagnostic.toCompileError().toString().contains("compile_error"))
    }

    @Test
    fun concreteFailuresRetainDiagnosticIdentityAtTheKotlinThrowBoundary() {
        val diagnostic = SynError.new(Span.callSite(), "invalid syntax")
        val fileResult = FileParseResult.failure(diagnostic)
        val tokenResult = TokenStreamParseResult.failure(diagnostic)

        assertSame(diagnostic, fileResult.error)
        assertSame(diagnostic, tokenResult.error)
        val fileException = assertFailsWith<IllegalArgumentException> { fileResult.getOrThrow() }
        val tokenException = assertFailsWith<IllegalArgumentException> { tokenResult.getOrThrow() }
        assertSame(diagnostic, assertIs<SynParseException>(fileException).diagnostic)
        assertSame(diagnostic, assertIs<SynParseException>(tokenException).diagnostic)
    }

    @Test
    fun adaptsTokenResultsWithoutCopyingTheirValueOrDiagnostic() {
        val tokens = TokenStream.fromString("fn main() {}").getOrThrow()
        val diagnostic = SynError.new(Span.callSite(), "invalid syntax")
        val success = SynResult.success(tokens).toTokenStreamParseResult()
        val failure = SynResult.failure<TokenStream>(diagnostic).toTokenStreamParseResult()

        assertTrue(success.isSuccess)
        assertFalse(success.isFailure)
        assertNull(success.error)
        assertSame(tokens, success.value)
        assertSame(tokens, success.getOrThrow())
        assertSame(tokens, success.getOrNull())
        assertTrue(failure.isFailure)
        assertNull(failure.value)
        assertSame(diagnostic, failure.error)
        assertSame(diagnostic, failure.exceptionOrNull())
    }
}
