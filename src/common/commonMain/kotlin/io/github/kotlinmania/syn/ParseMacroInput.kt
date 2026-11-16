// port-lint: source parse_macro_input.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro.TokenStream as ProcMacroTokenStream

private const val PARSE_MACRO_INPUT_AS_TEMPLATE: String =
    "parseMacroInput(\$tokenstream, \$ty::parse)"

private const val PARSE_MACRO_INPUT_WITH_TEMPLATE: String =
    "parseMacroInputWith(\$tokenstream, \$parser)"

private const val PARSE_MACRO_INPUT_INFERRED_TEMPLATE: String =
    "parseMacroInput(\$tokenstream)"

/**
 * Result of parsing procedural macro input via [parseMacroInput].
 */
public sealed class ParseMacroSynResult<out T> {
    public data class Success<out T>(
        public val value: T,
    ) : ParseMacroSynResult<T>()

    public data class CompileError<out T>(
        public val tokens: ProcMacroTokenStream,
    ) : ParseMacroSynResult<T>()
}

/**
 * Parse the input [ProcMacroTokenStream] of a macro, returning a compile error
 * token stream if the tokens fail to parse.
 *
 * # Intended usage
 *
 * ```kotlin
 * open class MyMacroInput {
 *     public companion object {
 *         public fun parse(input: ParseStream): SynResult<MyMacroInput> {
 *             // ...
 *         }
 *     }
 * }
 *
 * fun myMacro(tokens: ProcMacroTokenStream): ProcMacroTokenStream {
 *     val input = when (val parsed = parseMacroInput(tokens, MyMacroInput::parse)) {
 *         is ParseMacroSynResult.Success -> parsed.value
 *         is ParseMacroSynResult.CompileError -> return parsed.tokens
 *     }
 *     // ...
 * }
 * ```
 *
 * # Usage with [Parser]
 *
 * This helper can also be used with [parseMacroInputWith] for types that have
 * multiple ways that they can be parsed.
 */
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

internal fun parseMacroInputTemplate(
    tokenstream: String = "",
    ty: String = "",
    parser: String = "",
    clause: String = "",
): String =
    when {
        clause == "asTarget" && tokenstream.isNotEmpty() && ty.isNotEmpty() ->
            PARSE_MACRO_INPUT_AS_TEMPLATE
                .replace("\$tokenstream", tokenstream)
                .replace("\$ty", ty)
        clause == "with" && tokenstream.isNotEmpty() && parser.isNotEmpty() ->
            PARSE_MACRO_INPUT_WITH_TEMPLATE
                .replace("\$tokenstream", tokenstream)
                .replace("\$parser", parser)
        tokenstream.isNotEmpty() ->
            PARSE_MACRO_INPUT_INFERRED_TEMPLATE
                .replace("\$tokenstream", tokenstream)
        else -> ""
    }
