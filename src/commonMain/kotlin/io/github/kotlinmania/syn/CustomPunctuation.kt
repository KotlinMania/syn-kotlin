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
