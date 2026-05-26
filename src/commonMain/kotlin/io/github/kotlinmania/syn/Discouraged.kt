@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)
// port-lint: source discouraged.rs
package io.github.kotlinmania.syn
import kotlin.native.HiddenFromObjC

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter

// Extensions to the parsing API with niche applicability.

/**
 * Extensions to the [ParseStream] API to support speculative parsing.
 *
 * The upstream Rust spelling is a `pub trait Speculative` with a single
 * `advance_to` method, implemented by `ParseBuffer<'a>`. In Kotlin the
 * functionality is exposed as an extension function on [ParseBuffer] for the
 * same call-site ergonomics.
 *
 * # Drawbacks
 *
 * The main drawback of this style of speculative parsing is in error
 * presentation. Even if the lookahead is the "correct" parse, the error that
 * is shown is that of the "fallback" parse. Stick to LL(3)-parseable grammars
 * when you control them.
 *
 * # Performance
 *
 * This method performs a cheap fixed amount of work that does not depend on
 * how far apart the two streams are positioned.
 *
 * # Panics
 *
 * The forked stream in the argument of [advanceTo] must have been obtained by
 * forking the receiver. Attempting to advance to any other stream will cause
 * a panic.
 */
public fun ParseBuffer.advanceTo(fork: ParseBuffer) {
    require(sameScope(this.cursor(), fork.cursor())) {
        "fork was not derived from the advancing parse stream"
    }

    val (selfUnexp, selfSp) = innerUnexpected(this)
    val (forkUnexp, forkSp) = innerUnexpected(fork)
    if (selfUnexp !== forkUnexp) {
        when {
            // Unexpected set on the fork, but not on `self`, copy it over.
            forkSp != null && selfSp == null -> {
                selfUnexp.value = Unexpected.Some(forkSp.first, forkSp.second)
            }
            // Unexpected unset. Use chain to propagate errors from fork.
            forkSp == null && selfSp == null -> {
                forkUnexp.value = Unexpected.Chain(selfUnexp)

                // Ensure toplevel 'unexpected' tokens from the fork don't
                // propagate up the chain by replacing the root `unexpected`
                // pointer, only 'unexpected' tokens from existing group
                // parsers should propagate.
                fork.unexpected = UnexpectedRef(Unexpected.None)
            }
            // Unexpected has been set on `self`. No changes needed.
            else -> { }
        }
    }

    // See comment on `currentCursor` in the ParseBuffer definition.
    this.currentCursor = fork.cursor()
}

/**
 * Extensions to the [ParseStream] API to support manipulating invisible
 * delimiters the same as if they were visible.
 */
public data class AnyDelimiterResult(
    public val delimiter: Delimiter,
    public val span: DelimSpan,
    public val content: ParseBuffer,
)

/**
 * Returns the delimiter, the span of the delimiter token, and the nested
 * contents for further parsing.
 */
@HiddenFromObjC
public fun ParseBuffer.parseAnyDelimiter(): SynResult<AnyDelimiterResult> =
    step { cursor ->
        val any = cursor.anyGroup()
        if (any != null) {
            val scope = any.delimSpan.close()
            val nested = advanceStepCursor(cursor, any.inside)
            val unexpected = getUnexpected(this)
            val content = newParseBuffer(scope, nested, unexpected)
            SynResult.success(AnyDelimiterResult(any.delimiter, any.delimSpan, content) to any.after)
        } else {
            SynResult.failure(cursor.error("expected any delimiter"))
        }
    }
