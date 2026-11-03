// port-lint: source group.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Bracket
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Group as GroupToken

/** Not public API. */
public class Parens internal constructor(
    public val token: Paren,
    public val content: ParseBuffer,
)

/** Not public API. */
public class Braces internal constructor(
    public val token: Brace,
    public val content: ParseBuffer,
)

/** Not public API. */
public class Brackets internal constructor(
    public val token: Bracket,
    public val content: ParseBuffer,
)

/** Not public API. */
public class Group internal constructor(
    public val token: GroupToken,
    public val content: ParseBuffer,
)

/** Not public API. */
public fun parseParens(input: ParseBuffer): SynResult<Parens> =
    parseDelimited(input, Delimiter.Parenthesis).map { (span, content) ->
        Parens(token = Paren.from(span), content = content)
    }

/** Not public API. */
public fun parseBraces(input: ParseBuffer): SynResult<Braces> =
    parseDelimited(input, Delimiter.Brace).map { (span, content) ->
        Braces(token = Brace.from(span), content = content)
    }

/** Not public API. */
public fun parseBrackets(input: ParseBuffer): SynResult<Brackets> =
    parseDelimited(input, Delimiter.Bracket).map { (span, content) ->
        Brackets(token = Bracket.from(span), content = content)
    }

internal fun parseGroup(input: ParseBuffer): SynResult<Group> =
    parseDelimited(input, Delimiter.None).map { (span, content) ->
        Group(token = GroupToken.from(span.join()), content = content)
    }

private fun parseDelimited(
    input: ParseBuffer,
    delimiter: Delimiter,
): SynResult<Pair<DelimSpan, ParseBuffer>> =
    input.step { cursor ->
        val grp = cursor.group(delimiter)
        if (grp != null) {
            val (content, span, rest) = grp
            val scope = span.close()
            val nested = advanceStepCursor(cursor, content)
            val unexpected = getUnexpected(input)
            val nestedBuffer = newParseBuffer(scope, nested, unexpected)
            SynResult.success((span to nestedBuffer) to rest)
        } else {
            val message =
                when (delimiter) {
                    Delimiter.Parenthesis -> "expected parentheses"
                    Delimiter.Brace -> "expected curly braces"
                    Delimiter.Bracket -> "expected square brackets"
                    Delimiter.None -> "expected invisible group"
                }
            SynResult.failure(cursor.error(message))
        }
    }

/**
 * Parse a set of parentheses and expose their content to subsequent parsers.
 *
 * # Example
 *
 * ```kotlin
 * import syn.Ident
 * import syn.SynResult
 * import syn.SynType
 * import syn.TypeListComma
 * import syn.TypeParse
 * import syn.parenthesized
 * import syn.parse.ParseStream
 * import syn.token.Paren
 * import syn.token.Semi
 * import syn.token.Struct
 *
 * // Parse a simplified tuple struct syntax like:
 * //
 * // struct S(A, B);
 * open class TupleStruct(
 *     public var structToken: Struct,
 *     public var ident: Ident,
 *     public var parenToken: Paren,
 *     public var fields: TypeListComma,
 *     public var semiToken: Semi,
 * ) {
 *     public companion object {
 *         public fun parse(input: ParseStream): SynResult<TupleStruct> {
 *             val structToken = StructParse.parse(input).getOrElse { return SynResult.failure(it) }
 *             val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
 *             val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
 *             val fields = TypeListComma.parseTerminated(parens.content, TypeParse::parse).getOrElse { return SynResult.failure(it) }
 *             parens.content.finishChildBuffer()
 *             val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
 *             return SynResult.success(TupleStruct(structToken, ident, parens.token, fields, semiToken))
 *         }
 *     }
 * }
 * ```
 *
 * The caller is responsible for invoking [ParseBuffer.finishChildBuffer] on
 * `parens.content` at the end of its scope so that leftover-token diagnostics
 * propagate up to the parent buffer.
 */
public fun parenthesized(input: ParseBuffer): SynResult<Parens> = parseParens(input)

/**
 * Parse a set of curly braces and expose their content to subsequent parsers.
 *
 * # Example
 *
 * ```kotlin
 * val braces = braced(input).getOrElse { return SynResult.failure(it) }
 * val braceToken = braces.token
 * val content = braces.content
 * ```
 */
public fun braced(input: ParseBuffer): SynResult<Braces> = parseBraces(input)

/**
 * Parse a set of square brackets and expose their content to subsequent parsers.
 *
 * # Example
 *
 * ```kotlin
 * val brackets = bracketed(input).getOrElse { return SynResult.failure(it) }
 * val bracketToken = brackets.token
 * val content = brackets.content
 * ```
 */
public fun bracketed(input: ParseBuffer): SynResult<Brackets> = parseBrackets(input)
