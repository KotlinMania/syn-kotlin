// port-lint: tests discouraged.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DiscouragedTest {
    @Test
    fun speculativeAdvanceToPropagatesUnexpectedTokensFromFork() {
        val parser =
            parser@{ input: ParseStream ->
                val fork = input.fork()
                val parens = parenthesized(fork).getOrThrow()
                parens.content.finishChildBuffer()

                val speculative = assertIs<Speculative>(input)
                speculative.advanceTo(fork)
                SynResult.success(Unit)
            }

        val result = parseStr(parser, "(+)")

        assertTrue(result.isFailure)
        assertEquals("unexpected token, expected `)`", result.exceptionOrNull()?.toString())
    }

    @Test
    fun anyDelimiterTraitParsesInvisibleGroup() {
        val tokens =
            TokenStream.fromTokenTree(
                TokenTree.Group(
                    Group(
                        Delimiter.None,
                        TokenStream.fromString("inner").getOrThrow(),
                    ),
                ),
            )

        val parsed =
            parse2(
                parser@{ input: ParseStream ->
                    val anyDelimiter = assertIs<AnyDelimiter>(input)
                    val delimiter = anyDelimiter.parseAnyDelimiter().getOrElse { return@parser SynResult.failure(it) }
                    val ident = IdentParse.parse(delimiter.content).getOrElse { return@parser SynResult.failure(it) }
                    delimiter.content.finishChildBuffer()
                    SynResult.success(delimiter to ident)
                },
                tokens,
            ).getOrThrow()

        assertEquals(Delimiter.None, parsed.first.delimiter)
        assertEquals("inner", parsed.second.toString())
        assertTrue(parsed.first.content.isEmpty())
    }
}
