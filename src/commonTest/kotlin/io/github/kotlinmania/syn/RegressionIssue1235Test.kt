// port-lint: tests tests/regression/issue1235.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test

class RegressionIssue1235Test {
    @Test
    fun main() {
        parseFileTokens(
            TokenStream.fromString(
                """
                pub static FOO: usize;
                pub static BAR: usize;
                """.trimIndent(),
            ).getOrThrow(),
        )

        parseFileTokens(tokensWithPublicInvisibleGroup("static FOO: usize = 0; pub static BAR: usize = 0"))
        parseFileTokens(tokensWithPublicInvisibleGroup("static FOO: usize; pub static BAR: usize"))
    }

    private fun tokensWithPublicInvisibleGroup(source: String): TokenStream =
        TokenStream.fromTokenTrees(
            listOf(
                TokenTree.Ident(Ident.new("pub", Span.callSite())),
                TokenTree.Group(Group(Delimiter.None, TokenStream.fromString(source).getOrThrow())),
                TokenTree.Punct(Punct(';', Spacing.Alone, Span.callSite())),
            ),
        )

    private fun parseFileTokens(tokens: TokenStream): File =
        parse2(FileParse::parse, tokens).getOrThrow()
}
