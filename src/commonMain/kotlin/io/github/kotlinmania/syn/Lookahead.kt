// port-lint: source lookahead.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Span
import kotlin.native.HiddenFromObjC

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
 *
 * # Example
 *
 * ```
 * import io.github.kotlinmania.syn.parse.Parse
 * import io.github.kotlinmania.syn.parse.ParseStream
 *
 * // A generic parameter, a single one of the comma-separated elements inside
 * * // angle brackets in:
 * //
 * * // fun f<T : Clone, L, U : T, const N : USize>() { ... }
 * //
 * // On invalid input, lookahead gives us a reasonable error message.
 * //
 * * // error: expected one of: identifier, namedDuration, `const`
 * sealed class GenericParam {
 * data class Type(val inner: TypeParam) : GenericParam()
 * data class Lifetime(val inner: LifetimeParam) : GenericParam()
 * data class Const(val inner: ConstParam) : GenericParam()
 * }
 *
 * class GenericParamParser : Parse<GenericParam> {
 * override fun parse(input: ParseStream): SynResult<GenericParam> {
 * val lookahead = input.lookahead1()
 * return when {
 * lookahead.peek(Ident) -> input.parse<TypeParam>().map(GenericParam::Type)
 * lookahead.peek(Lifetime) -> input.parse<LifetimeParam>().map(GenericParam::Lifetime)
 * lookahead.peek(KwConst) -> input.parse<ConstParam>().map(GenericParam::Const)
 * else -> SynResult.failure(lookahead.error())
 * }
 * }
 * }
 * ```
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
 * # Syntax
 *
 * Pass the peek target.
 *
 * - `input.peek(StructKw)`
 * - `input.peek(EqEq)`
 * - `input.peek(Ident)`&emsp;*(does not accept keywords)*
 * - `input.peek(Lifetime)`
 * - `input.peek(Brace)`
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
 display = when (cursor.scopeDelimiter()) {
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
 /**
 * Returns true if [cursor] points at a token matching this peek target.
 *
 * In the upstream codebase this is a static function on the `Token` interface
 * that lives on the [Peek.Token] associated type. Kotlin has no
 * associated types, so the peek/display pair sits directly on this
 * interface and concrete peek targets implement both methods.
 */
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
 * - [ParseBuffer.peek2]
 * - [ParseBuffer.peek3]
 * - [Lookahead1.peek]
 *
 * The peek will return true if there are no remaining tokens after that
 * point in the parse stream.
 *
 * # Example
 *
 * Suppose we are parsing attributes containing fmt-inspired formatting
 * arguments:
 *
 * - `fmt("simple example")` attribute
 * - `fmt("interpolation e{}ample", self.x)` attribute
 *
 * and we want to recognize the cases where no interpolation occurs so that
 * more efficient code can be generated.
 *
 * The following implementation uses `input.peek(Comma) && input.peek2(End)`
 * to recognize the case of a trailing comma without consuming the comma from
 * the parse stream, because if it isn't a trailing comma, that same comma
 * needs to be parsed as part of `args`.
 */
public object End : Peek {
 override fun peek(cursor: Cursor): Boolean = cursor.eof()
 override fun display(): String = "`)`" // Lookahead1 error message will fill in the expected close delimiter
}

/**
 * Marker type used as the argument type for token-name peek closures.
 *
 * The upstream codebase defines this as a zero-variant enum `TokenMarker` — a
 * enum used so that the closure constraint cannot
 * ever be invoked at runtime. Kotlin has no zero-variant types other than
 * [Nothing], so this exists as a sealed class with no subclasses and a
 * private constructor that cannot be reached.
 */
@HiddenFromObjC
public sealed class TokenMarker private constructor() : IntoSpans<Any?> {
 override fun intoSpans(): Any? {
 throw IllegalStateException("TokenMarker has no inhabitants")
 }
}
