// port-lint: source spanned.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.__span

/**
 * A type that can provide the [Span] of the complete contents of a syntax tree node.
 *
 * A procedural macro can use a field type's span when generating a check that the type can be shared
 * between threads. Attaching that span to the generated check makes a failed check point to the
 * original field type, rather than to the macro invocation.
 *
 * Joining spans depends on the underlying compiler's support. When joining is unavailable, only the
 * first token's span is returned. For an error covering a complete syntax tree node, [SynError.newSpanned]
 * can cover the node without requiring span joining.
 *
 * The span operation is available for every [ToTokens] implementation, as well as [Span] and
 * [DelimSpan].
 */
public sealed interface Spanned {
    /** Returns a span covering this syntax tree node, or [Span.callSite] if the node is empty. */
    public fun span(): Span
}

public fun ToTokens.span(): Span =
    object : ToTokens {
        override fun toTokens(tokens: TokenStream) {
            this@span.toTokens(tokens)
        }
    }.__span().clone()

public fun Span.span(): Span = __span().clone()

public fun DelimSpan.span(): Span = __span().clone()
