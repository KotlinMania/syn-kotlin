// port-lint: source parse_macro_input.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

public sealed class ParseMacroSynResult {
    public data class Success(
        public val value: Any?,
    ) : ParseMacroSynResult()

    public data class CompileError(
        public val tokens: TokenStream,
    ) : ParseMacroSynResult()
}

public fun <T> parseMacroInput(
    tokens: TokenStream,
    parser: (ParseStream) -> SynResult<T>,
): ParseMacroSynResult {
    val result = parse2(parser, tokens)
    if (result.isSuccess) {
        return ParseMacroSynResult.Success(result.getOrThrow())
    }
    val syntaxError =
        result.exceptionOrNull()
            ?: error("parseMacroInput parser returned no failure error")
    return ParseMacroSynResult.CompileError(syntaxError.toCompileError())
}

public fun parseMacroInputWith(
    tokens: TokenStream,
    parser: (ParseStream) -> SynResult<Any?>,
): ParseMacroSynResult {
    val result = parse2(parser, tokens)
    if (result.isSuccess) {
        return ParseMacroSynResult.Success(result.getOrThrow())
    }
    val syntaxError =
        result.exceptionOrNull()
            ?: error("parseMacroInputWith parser returned no failure error")
    return ParseMacroSynResult.CompileError(syntaxError.toCompileError())
}
