// port-lint: source parse_macro_input.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

public sealed class ParseMacroSynResult<out T> {
    public data class Success<out T>(
        public val value: T,
    ) : ParseMacroSynResult<T>()

    public data class CompileError<out T>(
        public val tokens: TokenStream,
    ) : ParseMacroSynResult<T>()
}

public fun <T> parseMacroInput(
    tokens: TokenStream,
    parser: (ParseStream) -> SynResult<T>,
): ParseMacroSynResult<T> {
    val result = parse2(parser, tokens)
    if (result.isSuccess) {
        return ParseMacroSynResult.Success(result.getOrThrow())
    }
    val syntaxError =
        result.exceptionOrNull()
            ?: error("parseMacroInput parser returned no failure error")
    return ParseMacroSynResult.CompileError(syntaxError.toCompileError())
}

public fun <T> parseMacroInputWith(
    tokens: TokenStream,
    parser: (ParseStream) -> SynResult<T>,
): ParseMacroSynResult<T> =
    parseMacroInput(tokens, parser)
