// port-lint: source custom_punctuation.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append

/**
 * Support for defining custom multi-character punctuation tokens.
 *
 * In the upstream Rust crate this is a macro (`custom_punctuation!`).
 * In Kotlin, custom punctuation is defined as a data class with companion
 * [Peek] and [Parse] implementations that match the character sequence.
 *
 * Example: `PathSeparator` for `</>` would be defined as a data class
 * holding the three spans, with a companion Peek that checks for the
 * character sequence `<`, `/`, `>` with joint spacing.
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
            object : CustomPunctuation() {
                override val spans: List<Span> = spans
                override val chars: String = chars
            }
    }
}


/**
 * Creates a [Peek] and [Parse] pair for a custom multi-character punctuation
 * sequence.
 *
 * In Rust, `custom_punctuation!` is a macro that defines data classes for
 * specific punctuation sequences like `<-`, `=>`, `||`, etc. In Kotlin,
 * this function provides a dynamic mechanism to peek and parse arbitrary
 * punctuation sequences.
 *
 * @param chars The character sequence for this punctuation (e.g., "<=>").
 * @return A pair of [Peek] and [Parse] implementations for this punctuation.
 */
public fun customPunctuation(chars: String): Pair<Peek, Parse<CustomPunctuation>> {
    val peek = CustomPunctuationPeek(chars)
    val parse = CustomPunctuationParse(chars)
    return peek to parse
}

/** Peek implementation for a custom punctuation sequence. */
internal class CustomPunctuationPeek(private val chars: String) : Peek {
    override fun peek(cursor: Cursor): Boolean {
        var current = cursor
        for ((i, ch) in chars.withIndex()) {
            val pair = current.punct() ?: return false
            val punct = pair.first
            val next = pair.second
            if (punct.asChar() != ch) return false
            if (i < chars.length - 1 && punct.spacing() != io.github.kotlinmania.procmacro2.Spacing.Joint) return false
            current = next
        }
        return true
    }
    override fun display(): String = "`$chars`"
}

/** Parse implementation for a custom punctuation sequence. */
internal class CustomPunctuationParse(private val chars: String) : Parse<CustomPunctuation> {
    override fun parse(input: ParseStream): SynResult<CustomPunctuation> =
        input.step { cursor ->
            val spans = mutableListOf<io.github.kotlinmania.procmacro2.Span>()
            var current = cursor.raw
            for ((i, ch) in chars.withIndex()) {
                val pair = current.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `$chars`"))
                val punct = pair.first
                val next = pair.second
                if (punct.asChar() != ch) return@step SynResult.failure(cursor.error("expected `$chars`"))
                if (i < chars.length - 1 && punct.spacing() != io.github.kotlinmania.procmacro2.Spacing.Joint) {
                    return@step SynResult.failure(cursor.error("expected `$chars`"))
                }
                spans.add(punct.span())
                current = next
            }
            SynResult.success(CustomPunctuation.fromSpans(chars, spans) to current)
        }
}
