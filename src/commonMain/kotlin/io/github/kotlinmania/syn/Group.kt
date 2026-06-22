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
public class GroupContent internal constructor(
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

internal fun parseGroup(input: ParseBuffer): SynResult<GroupContent> =
    parseDelimited(input, Delimiter.None).map { (span, content) ->
        GroupContent(token = GroupToken.from(span.join()), content = content)
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
 * Mirrors the `parenthesized` macro. Callers write:
 *
 * ```kotlin
 * val content;
 * val paren = parenthesized!(content in input);
 * ```
 *
 * Kotlin callers write:
 *
 * ```kotlin
 * val parens = parenthesized(input).getOrThrow()
 * val paren = parens.token
 * val content = parens.content
 * ```
 *
 * The caller is responsible for invoking [ParseBuffer.finishChildBuffer] on
 * `content` at the end of its scope so that leftover-token diagnostics
 * propagate up to the parent buffer.
 */
public fun parenthesized(input: ParseBuffer): SynResult<Parens> = parseParens(input)

/** Parse a set of curly braces and expose their content to subsequent parsers. */
public fun braced(input: ParseBuffer): SynResult<Braces> = parseBraces(input)

/** Parse a set of square brackets and expose their content to subsequent parsers. */
public fun bracketed(input: ParseBuffer): SynResult<Brackets> = parseBrackets(input)
