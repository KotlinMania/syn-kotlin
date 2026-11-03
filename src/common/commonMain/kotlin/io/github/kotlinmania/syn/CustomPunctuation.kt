// port-lint: source custom_punctuation.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append

private const val CUSTOM_PUNCTUATION_TEMPLATE: String =
    "public class \$ident(public var spans: List<Span>); " +
        "implParseForCustomPunctuation(\$ident, \$tt); implToTokensForCustomPunctuation(\$ident, \$tt); " +
        "implCloneForCustomPunctuation(\$ident, \$tt); implExtraTraitsForCustomPunctuation(\$ident, \$tt);"

private const val IMPL_PARSE_FOR_CUSTOM_PUNCTUATION_TEMPLATE: String =
    "class CustomTokenFor\$ident : Peek { fun peek(cursor: Cursor): Boolean = peekPunct(cursor, \"\$tt\") }"

private const val IMPL_TO_TOKENS_FOR_CUSTOM_PUNCTUATION_TEMPLATE: String =
    "class ToTokensFor\$ident : ToTokens { fun toTokens(tokens: TokenStream) { printPunct(\"\$tt\", spans, tokens) } }"

private const val IMPL_CLONE_FOR_CUSTOM_PUNCTUATION_TEMPLATE: String =
    "fun \$ident.clone(): \$ident = \$ident(spans = spans)"

private const val IMPL_EXTRA_TRAITS_FOR_CUSTOM_PUNCTUATION_TEMPLATE: String =
    "fun \$ident.debugString(): String = \"\$ident\""

private const val CUSTOM_PUNCTUATION_REPR_TEMPLATE: String =
    "List<Span> /* \$tt */"

internal const val CUSTOM_PUNCTUATION_UNEXPECTED: String = ""

/**
 * Base representation for multi-character custom punctuation tokens.
 *
 * Custom punctuation matches an exact sequence of characters where every
 * character prior to the last has [Spacing.Joint] spacing.
 */
public abstract class CustomPunctuation : ToTokens {
    /** The spans covering each character of this punctuation. */
    public abstract val spans: List<Span>

    override fun toTokens(tokens: TokenStream) {
        for (i in spans.indices) {
            val spacing = if (i < spans.size - 1) Spacing.Joint else Spacing.Alone
            tokens.append(Punct(chars[i], spacing, spans[i]))
        }
    }

    /** The character sequence this punctuation represents. */
    public abstract val chars: String

    override fun toString(): String = chars

    override fun equals(other: Any?): Boolean =
        other is CustomPunctuation && chars == other.chars

    override fun hashCode(): Int = chars.hashCode()

    public companion object {
        /** Creates a custom punctuation from a character sequence and spans. */
        public fun fromSpans(chars: String, spans: List<Span>): CustomPunctuation =
            ExplicitCustomPunctuation(chars, spans)
    }
}

private class ExplicitCustomPunctuation(
    override val chars: String,
    override val spans: List<Span>,
) : CustomPunctuation()

/**
 * Define a type that supports parsing and printing a multi-character symbol
 * as if it were a punctuation token.
 *
 * # Usage
 *
 * ```kotlin
 * val (lrArrowPeek, lrArrowParse) = customPunctuation("<=>")
 * ```
 *
 * The generated syntax tree node supports the following operations just like
 * any built-in punctuation token.
 *
 * - Peeking — `input.peek(lrArrowPeek)`
 *
 * - Parsing — `lrArrowParse.parse(input)`
 *
 * - Printing — `lrarrow.toTokens(tokens)`
 *
 * - Construction from spans — `CustomPunctuation.fromSpans("<=>", listOf(sp, sp, sp))`
 *
 * - Field access to its spans — `val spans = lrarrow.spans`
 *
 * # Example
 *
 * ```kotlin
 * open class PathSegments(public var segments: Punctuated<Expr, CustomPunctuation>) {
 *     public companion object {
 *         public fun parse(input: ParseStream): SynResult<PathSegments> {
 *             val (sepPeek, sepParse) = customPunctuation("</>")
 *             val segments = Punctuated<Expr, CustomPunctuation>()
 *             val first = parseUntil(input, sepPeek).getOrElse { return SynResult.failure(it) }
 *             segments.pushValue(parse2(ExprParse::parse, first).getOrElse { return SynResult.failure(it) })
 *             while (input.peek(sepPeek)) {
 *                 segments.pushPunct(sepParse.parse(input).getOrElse { return SynResult.failure(it) })
 *                 val next = parseUntil(input, sepPeek).getOrElse { return SynResult.failure(it) }
 *                 segments.pushValue(parse2(ExprParse::parse, next).getOrElse { return SynResult.failure(it) })
 *             }
 *             return SynResult.success(PathSegments(segments = segments))
 *         }
 *     }
 * }
 * ```
 *
 * @param chars The character sequence for this punctuation.
 * @return A pair of [Peek] and concrete parser for this punctuation.
 */
public fun customPunctuation(chars: String): Pair<Peek, CustomPunctuationParse> {
    val peek = CustomPunctuationPeek(chars)
    val parse = CustomPunctuationParse(chars)
    return peek to parse
}

internal fun customPunctuationTemplate(ident: String = "", tt: String = ""): String =
    CUSTOM_PUNCTUATION_TEMPLATE
        .replace("\$ident", ident)
        .replace("\$tt", tt)

internal fun implParseForCustomPunctuation(ident: String = "", tt: String = ""): String =
    if (CFG_FEATURE_PARSING) {
        IMPL_PARSE_FOR_CUSTOM_PUNCTUATION_TEMPLATE
            .replace("\$ident", ident)
            .replace("\$tt", tt)
    } else {
        ""
    }

internal fun implToTokensForCustomPunctuation(ident: String = "", tt: String = ""): String =
    if (CFG_FEATURE_PRINTING) {
        IMPL_TO_TOKENS_FOR_CUSTOM_PUNCTUATION_TEMPLATE
            .replace("\$ident", ident)
            .replace("\$tt", tt)
    } else {
        ""
    }

internal fun implCloneForCustomPunctuation(ident: String = "", tt: String = ""): String =
    if (CFG_FEATURE_CLONE_IMPLS && (tt.isEmpty() || tt.isNotEmpty())) {
        IMPL_CLONE_FOR_CUSTOM_PUNCTUATION_TEMPLATE.replace("\$ident", ident)
    } else {
        ""
    }

internal fun implExtraTraitsForCustomPunctuation(ident: String = "", tt: String = ""): String =
    if (CFG_FEATURE_EXTRA_TRAITS && (tt.isEmpty() || tt.isNotEmpty())) {
        IMPL_EXTRA_TRAITS_FOR_CUSTOM_PUNCTUATION_TEMPLATE.replace("\$ident", ident)
    } else {
        ""
    }

internal fun customPunctuationRepr(tt: String = ""): String =
    CUSTOM_PUNCTUATION_REPR_TEMPLATE.replace("\$tt", tt)

internal fun customPunctuationLen(mode: String = "lenient", tt: String = ""): Int =
    when (tt) {
        "...", "..=", "<<=", ">>=" -> 3
        "&&", "&=", "^=", "::", "..", "==", "=>", ">=", "<-", "<=", "-=", "!=", "|=", "||", "%=", "+=", "->", "<<", ">>", "/=", "*=" -> 2
        "&", "@", "^", ":", ",", "$", ".", "=", ">", "<", "-", "!", "|", "%", "+", "#", "?", ";", "/", "*", "~" -> 1
        else ->
            if (mode == "strict") {
                throw IllegalArgumentException("Unexpected custom punctuation token: $tt")
            } else {
                0
            }
    }

internal fun stringifyPunct(tt: String = ""): String =
    tt.filter { !it.isWhitespace() }

/** Peek implementation for a custom punctuation sequence. */
internal class CustomPunctuationPeek(
    private val chars: String,
) : Peek {
    override fun peek(cursor: Cursor): Boolean {
        var current = cursor
        for ((i, ch) in chars.withIndex()) {
            val pair = current.punct() ?: return false
            val punct = pair.first
            val next = pair.second
            if (punct.asChar() != ch) return false
            if (i < chars.length - 1 && punct.spacing() != Spacing.Joint) return false
            current = next
        }
        return true
    }

    override fun display(): String = "`$chars`"
}

/** Parse implementation for a custom punctuation sequence. */
public class CustomPunctuationParse(
    private val chars: String,
) {
    public fun parse(input: ParseStream): SynResult<CustomPunctuation> =
        input.step { cursor ->
            val spans = mutableListOf<Span>()
            var current = cursor.raw
            for ((i, ch) in chars.withIndex()) {
                val pair =
                    current.punct()
                        ?: return@step SynResult.failure(cursor.error("expected `$chars`"))
                val punct = pair.first
                val next = pair.second
                if (punct.asChar() != ch) return@step SynResult.failure(cursor.error("expected `$chars`"))
                if (i < chars.length - 1 && punct.spacing() != Spacing.Joint) {
                    return@step SynResult.failure(cursor.error("expected `$chars`"))
                }
                spans.add(punct.span())
                current = next
            }
            SynResult.success(CustomPunctuation.fromSpans(chars, spans) to current)
        }
}
