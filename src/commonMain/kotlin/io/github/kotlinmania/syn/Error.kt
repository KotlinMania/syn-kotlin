// port-lint: source error.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.LexError
import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append

/**
 * The result of a Syn parser.
 *
 * Upstream parser results carry [SynError] as the dedicated failure type. This
 * sealed result keeps that typed failure in the Kotlin API instead of erasing
 * it behind a generic throwable. It also avoids the unchecked-cast bridge that
 * Swift export generates for the standard library result type under
 * warnings-as-errors builds.
 *
 * Named `SynResult` to avoid colliding with Swift's built-in `Result` type.
 *
 * The companion-object factories [SynResult.success] / [SynResult.failure], the
 * `isSuccess` / `isFailure` properties, and the `getOrThrow` /
 * `getOrNull` / `exceptionOrNull` / `getOrElse` / `fold` / `map` operations
 * match the standard result idioms used throughout this port.
 */
public sealed class SynResult<out T> {
    /** Successful parse result carrying the parsed value. */
    public class Success<out T>(public val value: T) : SynResult<T>()

    /** Failed parse result carrying a syn [SynError]. */
    public class Failure<out T>(public val error: SynError) : SynResult<T>()

    /** `true` when this is a [Success]. */
    public val isSuccess: Boolean
        get() = this is Success

    /** `true` when this is a [Failure]. */
    public val isFailure: Boolean
        get() = this is Failure

    /**
     * Returns the parsed value if this is a [Success], or throws the
     * carried [SynError] if this is a [Failure]. `SynError` extends
     * `IllegalArgumentException`, so the throw is well-typed.
     */
    public fun getOrThrow(): T = when (this) {
        is Success -> value
        is Failure -> throw error
    }

    /** Returns the parsed value if this is a [Success], or `null` if this is a [Failure]. */
    public fun getOrNull(): T? = (this as? Success)?.value

    /** Returns the carried [SynError] if this is a [Failure], or `null` if this is a [Success]. */
    public fun exceptionOrNull(): SynError? = (this as? Failure<*>)?.error

    /** Returns the parsed value if this is a [Success], or [onFailure]'s value for a [Failure]. */
    public inline fun getOrElse(onFailure: (SynError) -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> onFailure(error)
    }

    /**
     * Applies [onSuccess] to a [Success] value or [onFailure] to a
     * [Failure] error and returns the result.
     */
    public inline fun <R> fold(onSuccess: (T) -> R, onFailure: (SynError) -> R): R = when (this) {
        is Success -> onSuccess(value)
        is Failure -> onFailure(error)
    }

    /**
     * Returns a [SynResult] containing the [transform]-mapped value if
     * this is a [Success], or this [Failure] unchanged.
     */
    public inline fun <R> map(transform: (T) -> R): SynResult<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> Failure(error)
    }

    public companion object {
        /** Constructs a [Success] wrapping [value]. */
        public fun <T> success(value: T): SynResult<T> = Success(value)

        /** Constructs a [Failure] wrapping [error]. */
        public fun <T> failure(error: SynError): SynResult<T> = Failure(error)
    }
}


/**
 * Error returned when a Syn parser cannot parse the input tokens.
 *
 * Named `SynError` to avoid colliding with Swift's built-in `Error` protocol.
 *
 * # Error reporting in proc macros
 *
 * The correct way to report errors back to the compiler from a procedural
 * macro is by emitting an appropriately spanned invocation of
 * `compileError` in the generated code. This produces a better diagnostic
 * message than simply panicking the macro.
 *
 * When parsing macro input, the `parseMacroInput` helper handles the
 * conversion to `compileError` automatically.
 *
 * For errors that arise later than the initial parsing stage, the
 * `toCompileError` or `intoCompileError` methods can be used to perform an
 * explicit conversion to `compileError`.
 */
public class SynError private constructor(
    private val messages: MutableList<ErrorMessage>,
) : IllegalArgumentException(messages.first().message), Iterable<SynError> {
    public companion object {
        /**
         * Usually the `ParseStream.error` method will be used instead, which
         * automatically uses the correct span from the current position of the
         * parse stream.
         *
         * Use `SynError.new` when the error needs to be triggered on some span
         * other than where the parse stream is currently positioned.
         */
        public fun new(span: Span, message: Any): SynError =
            SynError(
                mutableListOf(
                    ErrorMessage(
                        span = ThreadBound.new(SpanRange(span, span)),
                        message = message.toString(),
                    ),
                ),
            )

        /**
         * Creates an error with the specified message spanning the given syntax
         * tree node.
         *
         * Unlike the `SynError.new` constructor, this constructor takes an
         * argument `tokens` which is a syntax tree node. This allows the
         * resulting `SynError` to attempt to span all tokens inside of `tokens`.
         * While you would typically be able to use the `Spanned` trait with
         * the above `SynError.new` constructor, implementation limitations today
         * mean that `SynError.newSpanned` may provide a higher-quality error
         * message on stable Rust.
         *
         * When in doubt it's recommended to stick to `SynError.new`!
         */
        public fun newSpanned(tokens: ToTokens, message: Any): SynError =
            newSpanned(tokens.intoTokenStream(), message)

        public fun newSpanned(tokens: TokenStream, message: Any): SynError {
            val iterator = tokens.iterator()
            val start = if (iterator.hasNext()) iterator.next().span() else Span.callSite()
            var end = start
            while (iterator.hasNext()) {
                end = iterator.next().span()
            }
            return SynError(
                mutableListOf(
                    ErrorMessage(
                        span = ThreadBound.new(SpanRange(start, end)),
                        message = message.toString(),
                    ),
                ),
            )
        }

        public fun from(err: LexError): SynError =
            new(err.span(), err)

        internal fun new2(start: Span, end: Span, message: Any): SynError =
            SynError(
                mutableListOf(
                    ErrorMessage(
                        span = ThreadBound.new(SpanRange(start, end)),
                        message = message.toString(),
                    ),
                ),
            )
    }

    /** The source location of the error. */
    public fun span(): Span {
        val range = messages[0].span.get() ?: return Span.callSite()
        return range.start.join(range.end) ?: range.start
    }

    /**
     * Render the error as an invocation of `compileError`.
     *
     * The `parseMacroInput` helper provides a convenient way to invoke this
     * method correctly in a procedural macro.
     */
    public fun toCompileError(): TokenStream {
        val tokens = TokenStream.new()
        for (msg in messages) {
            msg.toCompileError(tokens)
        }
        return tokens
    }

    /** Render the error as an invocation of `compileError`. */
    public fun intoCompileError(): TokenStream =
        toCompileError()

    /**
     * Add another error message to self such that when `toCompileError` is
     * called, both errors will be emitted together.
     */
    public fun combine(another: SynError) {
        messages.addAll(another.messages)
    }

    override fun iterator(): Iterator<SynError> =
        messages.map { SynError(mutableListOf(it.copy())) }.iterator()

    public fun iter(): Iterator<SynError> =
        iterator()

    override fun toString(): String =
        messages.first().message
}


private data class ErrorMessage(
    val span: ThreadBound<SpanRange>,
    val message: String,
) {
    fun toCompileError(tokens: TokenStream) {
        val range = span.get()
        val start = range?.start ?: Span.callSite()
        val end = range?.end ?: Span.callSite()

        tokens.append(TokenTree.Punct(Punct(':', Spacing.Joint, start)))
        tokens.append(TokenTree.Punct(Punct(':', Spacing.Alone, start)))
        tokens.append(TokenTree.Ident(Ident.new("core", start)))
        tokens.append(TokenTree.Punct(Punct(':', Spacing.Joint, start)))
        tokens.append(TokenTree.Punct(Punct(':', Spacing.Alone, start)))
        tokens.append(TokenTree.Ident(Ident.new("compile_error", start)))
        tokens.append(TokenTree.Punct(Punct('!', Spacing.Alone, start)))

        val string = Literal.string(message)
        string.setSpan(end)
        val group = Group(
            Delimiter.Brace,
            TokenStream.fromTokenTree(TokenTree.Literal(string)),
        )
        group.setSpan(end)
        tokens.append(TokenTree.Group(group))
    }
}

private data class SpanRange(
    val start: Span,
    val end: Span,
)

internal fun errorNewAt(scope: Span, cursor: Cursor, message: Any): SynError =
    if (cursor.eof()) {
        SynError.new(scope, "unexpected end of input, $message")
    } else {
        val span = openSpanOfGroup(cursor)
        SynError.new(span, message)
    }
