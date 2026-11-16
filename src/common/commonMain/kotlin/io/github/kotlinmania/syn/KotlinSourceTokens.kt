package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro.TokenStreamParseOutcome
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.syn.token.Break
import io.github.kotlinmania.syn.token.Continue
import io.github.kotlinmania.syn.token.Else
import io.github.kotlinmania.syn.token.Fn
import io.github.kotlinmania.syn.token.For
import io.github.kotlinmania.syn.token.If
import io.github.kotlinmania.syn.token.KeywordToken
import io.github.kotlinmania.syn.token.Match
import io.github.kotlinmania.syn.token.Return
import io.github.kotlinmania.syn.token.While
import io.github.kotlinmania.procmacro.TokenStream as KotlinCompilerTokens

/**
 * Tokenizes Kotlin with the existing multiplatform compiler lexer and exposes
 * the shared token trees used by Syn. This does not parse a Kotlin syntax tree.
 */
public fun parseKotlinTokenStream(source: String): TokenStreamParseResult =
    when (val outcome = KotlinCompilerTokens.fromString(source)) {
        is TokenStreamParseOutcome.Ok ->
            TokenStreamParseResult.success(TokenStream.fromProcMacro(outcome.value))
        is TokenStreamParseOutcome.Err ->
            TokenStreamParseResult.failure(SynError.new(Span.callSite(), outcome.error.toString()))
    }

/**
 * Associates hard Kotlin keywords with existing Syn token families without
 * rewriting the input token. This is lexical correspondence, not semantic
 * equivalence or a Kotlin-to-Rust syntax-tree conversion.
 */
public fun kotlinKeywordRustToken(token: TokenTree): KeywordToken? {
    if (token !is TokenTree.Ident) return null
    val span = token.span()
    return when (token.value.toString()) {
        "fun" -> Fn(span)
        "if" -> If(span)
        "else" -> Else(span)
        "for" -> For(span)
        "while" -> While(span)
        "return" -> Return(span)
        "break" -> Break(span)
        "continue" -> Continue(span)
        "when" -> Match(span)
        else -> null
    }
}
