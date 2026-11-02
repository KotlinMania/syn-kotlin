// port-lint: source parse_macro_input.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro.TokenStream as ProcMacroTokenStream

public sealed class ParseMacroSynResult<out T> {
    public data class Success<out T>(
        public val value: T,
    ) : ParseMacroSynResult<T>()

    public data class CompileError<out T>(
        public val tokens: ProcMacroTokenStream,
    ) : ParseMacroSynResult<T>()
}

public fun <T> parseMacroInput(
    tokens: ProcMacroTokenStream,
    parser: (ParseStream) -> SynResult<T>,
): ParseMacroSynResult<T> {
    val result = parse(parser, tokens)
    if (result.isSuccess) {
        return ParseMacroSynResult.Success(result.getOrThrow())
    }
    val syntaxError =
        result.exceptionOrNull()
            ?: error("parseMacroInput parser returned no failure error")
    return ParseMacroSynResult.CompileError(syntaxError.toCompileError().intoProcMacro())
}

public fun <T> parseMacroInputWith(
    tokens: ProcMacroTokenStream,
    parser: Parser<T>,
): ParseMacroSynResult<T> {
    val result = parser.parse(tokens)
    if (result.isSuccess) {
        return ParseMacroSynResult.Success(result.getOrThrow())
    }
    val syntaxError =
        result.exceptionOrNull()
            ?: error("parseMacroInputWith parser returned no failure error")
    return ParseMacroSynResult.CompileError(syntaxError.toCompileError().intoProcMacro())
}

public fun <T> parseMacroInputWith(
    tokens: ProcMacroTokenStream,
    parser: (ParseStream) -> SynResult<T>,
): ParseMacroSynResult<T> =
    parseMacroInput(tokens, parser)

public sealed class ParseMacro2SynResult<out T> {
    public data class Success<out T>(
        public val value: T,
    ) : ParseMacro2SynResult<T>()

    public data class CompileError<out T>(
        public val tokens: TokenStream,
    ) : ParseMacro2SynResult<T>()
}

public fun <T> parseMacroInput2(
    tokens: TokenStream,
    parser: (ParseStream) -> SynResult<T>,
): ParseMacro2SynResult<T> {
    val result = parse2(parser, tokens)
    if (result.isSuccess) {
        return ParseMacro2SynResult.Success(result.getOrThrow())
    }
    val syntaxError =
        result.exceptionOrNull()
            ?: error("parseMacroInput2 parser returned no failure error")
    return ParseMacro2SynResult.CompileError(syntaxError.toCompileError())
}
