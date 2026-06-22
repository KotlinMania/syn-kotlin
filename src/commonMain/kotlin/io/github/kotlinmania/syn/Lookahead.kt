// port-lint: source lookahead.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Bracket
import io.github.kotlinmania.syn.token.Paren

/**
 * Support for checking the next token in a stream to decide how to parse.
 *
 * An important advantage over [ParseBuffer.peek] is that here we automatically
 * construct an appropriate error message based on the token alternatives that
 * get peeked. If you are producing your own error message, go ahead and use
 * [ParseBuffer.peek] instead.
 *
 * Use [ParseBuffer.lookahead1] to construct this object.
 *
 * Consuming tokens from the source stream after constructing a lookahead
 * object does not also advance the lookahead object.
 */
public class Lookahead1 internal constructor(
    private val scope: Span,
    private val cursor: Cursor,
) {
    private val comparisons: MutableList<String> = mutableListOf()

    /**
     * Looks at the next token in the parse stream to determine whether it
     * matches the requested type of token.
     *
     * Pass the peek target:
     * - `input.peek(IdentPeek)`
     * - `input.peek(CommaPeek)`
     * - `input.peek(BracePeek)`
     * - `input.peek(LifetimePeek)`
     */
    public fun peek(token: Peek): Boolean {
        if (token.peek(cursor)) {
            return true
        }
        comparisons.add(token.display())
        return false
    }

    /**
     * Triggers an error at the current position of the parse stream.
     *
     * The error message will identify all of the expected token types that
     * have been peeked against this lookahead instance.
     */
    public fun error(): SynError {
        val pruned = mutableListOf<String>()
        for (item in comparisons) {
            var display = item
            if (display == "`)`") {
                display =
                    when (cursor.scopeDelimiter()) {
                        Delimiter.Parenthesis -> "`)`"
                        Delimiter.Brace -> "`}`"
                        Delimiter.Bracket -> "`]`"
                        Delimiter.None -> continue
                    }
            }
            pruned.add(display)
        }
        return when (pruned.size) {
            0 -> {
                if (cursor.eof()) {
                    SynError.new(scope, "unexpected end of input")
                } else {
                    SynError.new(cursor.span(), "unexpected token")
                }
            }
            1 -> errorNewAt(scope, cursor, "expected ${pruned[0]}")
            2 -> errorNewAt(scope, cursor, "expected ${pruned[0]} or ${pruned[1]}")
            else -> errorNewAt(scope, cursor, "expected one of: ${pruned.joinToString(", ")}")
        }
    }

    public override fun toString(): String =
        comparisons.joinToString(", ", "Lookahead1[", "]")
}

internal fun lookahead1New(scope: Span, cursor: Cursor): Lookahead1 = Lookahead1(scope, cursor)

/**
 * Types that can be parsed by looking at just one token.
 *
 * Use [ParseBuffer.peek] to peek one of these types in a parse stream without
 * consuming it from the stream.
 *
 * This interface is sealed and cannot be implemented for types outside of Syn.
 */
public sealed interface Peek : Lookahead.Sealed {
    /** Returns true if [cursor] points at a token matching this peek target. */
    public fun peek(cursor: Cursor): Boolean

    /** Returns the display string used in Lookahead error messages. */
    public fun display(): String
}

/**
 * Pseudo-token used for peeking the end of a parse stream.
 *
 * This type is only useful as an argument to one of the following functions:
 *
 * - [ParseBuffer.peek]
 * - [Lookahead1.peek]
 *
 * The peek will return true if there are no remaining tokens after that
 * point in the parse stream.
 */
public object End : Peek {
    override fun peek(cursor: Cursor): Boolean = cursor.eof()

    override fun display(): String = "end of input"
}

/** Peek for an opening brace `{`. */
public object BracePeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.group(Delimiter.Brace) != null

    override fun display(): String = "`{`"
}

/** Peek for an opening bracket `[`. */
public object BracketPeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.group(Delimiter.Bracket) != null

    override fun display(): String = "`[`"
}

/** Peek for an opening parenthesis `(`. */
public object ParenPeek : Peek {
    override fun peek(cursor: Cursor): Boolean =
        cursor.group(Delimiter.Parenthesis) != null

    override fun display(): String = "`(`"
}

/** Strongly-typed parse for an opening brace `{`. */
public object BraceParse : Parse<Brace> {
    override fun parse(input: ParseStream): SynResult<Brace> =
        input.step { cursor ->
            val (content, span, rest) =
                cursor.group(Delimiter.Brace)
                    ?: return@step SynResult.failure(cursor.error("expected `{`"))
            val scope = span.close()
            val nested = advanceStepCursor(cursor, content)
            val unexpected = getUnexpected(input)
            val nestedBuffer = newParseBuffer(scope, nested, unexpected)
            SynResult.success(Brace.from(span) to rest)
        }
}

/** Strongly-typed parse for an opening bracket `[`. */
public object BracketParse : Parse<Bracket> {
    override fun parse(input: ParseStream): SynResult<Bracket> =
        input.step { cursor ->
            val (content, span, rest) =
                cursor.group(Delimiter.Bracket)
                    ?: return@step SynResult.failure(cursor.error("expected `[`"))
            val scope = span.close()
            val nested = advanceStepCursor(cursor, content)
            val unexpected = getUnexpected(input)
            val nestedBuffer = newParseBuffer(scope, nested, unexpected)
            SynResult.success(Bracket.from(span) to rest)
        }
}

/** Strongly-typed parse for an opening parenthesis `(`. */
public object ParenParse : Parse<Paren> {
    override fun parse(input: ParseStream): SynResult<Paren> =
        input.step { cursor ->
            val (content, span, rest) =
                cursor.group(Delimiter.Parenthesis)
                    ?: return@step SynResult.failure(cursor.error("expected `(`"))
            val scope = span.close()
            val nested = advanceStepCursor(cursor, content)
            val unexpected = getUnexpected(input)
            val nestedBuffer = newParseBuffer(scope, nested, unexpected)
            SynResult.success(Paren.from(span) to rest)
        }
}

/**
 * Marker type used as the argument type for token-name peek closures.
 *
 * A sealed class with no subclasses and a private constructor, representing
 * a phantom token-marker type that can never be instantiated at runtime.
 */
public sealed class TokenMarker private constructor() : IntoSpans<Any?> {
    override fun intoSpans(): Any? = throw IllegalStateException("TokenMarker has no inhabitants")
}
