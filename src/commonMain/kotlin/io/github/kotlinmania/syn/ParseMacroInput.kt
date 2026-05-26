// port-lint: source parse_macro_input.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.native.HiddenFromObjC

/**
 * Parse the input [TokenStream] of a macro, returning either the parsed
 * syntax tree node or a fallback [TokenStream] containing a `compileError`
 * invocation. Refer to the [Parse] interface for more details about parsing
 * in Syn.
 *
 * <br>
 *
 * # Intended usage
 *
 * The upstream Rust spelling is a `macro_rules! parse_macro_input` macro
 * that calls [parse2] under the hood and early-returns the calling function
 * with a `compileError` token stream on parse failure. Kotlin has no macro
 * system and no way to early-return out of the calling function from a
 * helper, so [parseMacroInput] returns a sealed [ParseMacroResult] that the
 * caller is expected to fold into a [TokenStream] result.
 *
 * ```kotlin
 * fun myMacro(tokens: TokenStream): TokenStream =
 *     when (val input = parseMacroInput(tokens, MyMacroInput)) {
 *         is ParseMacroResult.Success -> {
 *             // … work with input.value …
 *             TokenStream.new()
 *         }
 *         is ParseMacroResult.CompileError -> input.tokens
 *     }
 * ```
 *
 * <br>
 *
 * # Usage with Parser
 *
 * This helper can also be used with the [Parser] trait for types that have
 * multiple ways that they can be parsed:
 *
 * ```kotlin
 * fun myMacro(tokens: TokenStream): TokenStream =
 *     when (val input = parseMacroInputWith(tokens, MyMacroInput.parseAlternate)) {
 *         is ParseMacroResult.Success -> /* … work with input.value … */
 *         is ParseMacroResult.CompileError -> input.tokens
 *     }
 * ```
 *
 * <br>
 *
 * # Expansion
 *
 * `parseMacroInput(variable, T)` is equivalent to:
 *
 * ```kotlin
 * when (val result = parse2(T, variable)) {
 *     is SynResult.Success -> ParseMacroResult.Success(result.value)
 *     is SynResult.Failure -> ParseMacroResult.CompileError(
 *         (result.exception as SynError).toCompileError(),
 *     )
 * }
 * ```
 */
@HiddenFromObjC
public sealed class ParseMacroSynResult<out T> {
    public data class Success<T>(public val value: T) : ParseMacroSynResult<T>()
    public data class CompileError<T>(public val tokens: TokenStream) : ParseMacroSynResult<T>()
}

/** Parse the macro input via the supplied [Parse] strategy. */
@HiddenFromObjC
public fun <T> parseMacroInput(tokens: TokenStream, parser: Parse<T>): ParseMacroSynResult<T> {
    val result = parse2(parser, tokens)
    if (result.isSuccess) {
        return ParseMacroResult.Success(result.getOrThrow())
    }
    val syntaxError = result.exceptionOrNull()
        ?: error("parseMacroInput parser returned no failure error")
    return ParseMacroResult.CompileError(syntaxError.toCompileError())
}

/** Parse the macro input via the supplied closure-style parser. */
@HiddenFromObjC
public fun <T> parseMacroInputWith(
    tokens: TokenStream,
    parser: (ParseStream) -> SynResult<T>,
): ParseMacroSynResult<T> {
    val result = parserFromFunction(parser).parse2(tokens)
    if (result.isSuccess) {
        return ParseMacroResult.Success(result.getOrThrow())
    }
    val syntaxError = result.exceptionOrNull()
        ?: error("parseMacroInputWith parser returned no failure error")
    return ParseMacroResult.CompileError(syntaxError.toCompileError())
}
