// port-lint: source punctuated.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append

import kotlin.native.HiddenFromObjC

/**
 * A punctuated sequence of syntax tree nodes of type [T] separated by
 * punctuation of type [P].
 *
 * This shape is used for data structure fields, path segments, generic
 * arguments, function inputs, and many other comma- or operator-separated
 * lists. Every node except possibly the final one is paired with punctuation.
 *
 * Hidden from the Objective-C / Swift Export bridge: the bridge erases
 * `Punctuated<T, P>` type parameters to `Punctuated<Any?, Any?>` and the
 * generated `Syn.kt` re-compile fails with
 * `actual type is Punctuated<Any?, Any?>, but Punctuated<…, …> was
 * expected` against every strongly-typed call site (~10+ occurrences).
 */
@HiddenFromObjC
public class Punctuated<T : ToTokens, P : ToTokens> private constructor(
 private val inner: MutableList<Pair<T, P>>,
 private var last: T?,
) : ToTokens, Iterable<T> {
 public constructor() : this(mutableListOf(), null)

 public companion object {
 public fun <T : ToTokens, P : ToTokens> new(): Punctuated<T, P> =
 Punctuated()

 /**
 * Parses zero or more occurrences of [T] separated by punctuation of
 * type P, with optional trailing punctuation.
 *
 * Parsing continues until the end of this parse stream. The entire
 * content of this parse stream must consist of [T] and P.
 */
 public fun <T : ToTokens, P : ToTokens> parseTerminated(
 input: ParseStream,
 valueParse: Parse<T>,
 punctParse: Parse<P>,
 ): SynResult<Punctuated<T, P>> =
 parseTerminatedWith(input, valueParse::parse, punctParse)

 /**
 * Parses zero or more occurrences of [T] using the given parse
 * function, separated by punctuation of type P, with optional
 * trailing punctuation.
 */
 public fun <T : ToTokens, P : ToTokens> parseTerminatedWith(
 input: ParseStream,
 parser: (ParseStream) -> SynResult<T>,
 punctParse: Parse<P>,
 ): SynResult<Punctuated<T, P>> {
 val punctuated = Punctuated<T, P>()

 while (true) {
 if (input.isEmpty()) break
 val value = parser(input).getOrElse { return SynResult.failure(it) }
 punctuated.pushValue(value)
 if (input.isEmpty()) break
 val punct = punctParse.parse(input).getOrElse { return SynResult.failure(it) }
 punctuated.pushPunct(punct)
 }

 return SynResult.success(punctuated)
 }

 /**
 * Parses one or more occurrences of [T] separated by punctuation of
 * type P, not accepting trailing punctuation.
 *
 * Parsing continues as long as punctuation P is present at the head
 * of the stream. This method returns upon parsing a [T] and observing
 * that it is not followed by a P, even if there are remaining tokens
 * in the stream.
 */
 public fun <T : ToTokens, P : ToTokens> parseSeparatedNonempty(
 input: ParseStream,
 valueParse: Parse<T>,
 punctParse: Parse<P>,
 punctPeek: Peek,
 ): SynResult<Punctuated<T, P>> =
 parseSeparatedNonemptyWith(input, valueParse::parse, punctParse, punctPeek)

 /**
 * Parses one or more occurrences of [T] using the given parse
 * function, separated by punctuation of type P, not accepting
 * trailing punctuation.
 */
 public fun <T : ToTokens, P : ToTokens> parseSeparatedNonemptyWith(
 input: ParseStream,
 parser: (ParseStream) -> SynResult<T>,
 punctParse: Parse<P>,
 punctPeek: Peek,
 ): SynResult<Punctuated<T, P>> {
 val punctuated = Punctuated<T, P>()

 while (true) {
 val value = parser(input).getOrElse { return SynResult.failure(it) }
 punctuated.pushValue(value)
 if (!punctPeek.peek(input.cursor())) break
 val punct = punctParse.parse(input).getOrElse { return SynResult.failure(it) }
 punctuated.pushPunct(punct)
 }

 return SynResult.success(punctuated)
 }
 }

 public fun isEmpty(): Boolean =
 inner.isEmpty() && last == null

 public fun len(): Int =
 inner.size + if (last == null) 0 else 1

 public val size: Int
 get() = len()

 public fun first(): T? =
 iterator().asSequence().firstOrNull()

 public fun last(): T? =
 last ?: inner.lastOrNull()?.first

 public operator fun get(index: Int): T {
 require(index >= 0) { "index must be non-negative" }
 inner.getOrNull(index)?.let { return it.first }
 val tail = last
 if (index == inner.size && tail != null) {
 return tail
 }
 throw IndexOutOfBoundsException("index: $index")
 }

 public fun getOrNull(index: Int): T? =
 if (index < 0) {
 null
 } else {
 inner.getOrNull(index)?.first
 ?: if (index == inner.size) last else null
 }

 public fun pairs(): List<Pair<T, P?>> =
 buildList {
 for ((value, punctuation) in inner) {
 add(value to punctuation)
 }
 val tail = last
 if (tail != null) {
 add(tail to null)
 }
 }

 public fun pushValue(value: T) {
 require(emptyOrTrailing()) {
 "Punctuated.pushValue: cannot push value if Punctuated is missing trailing punctuation"
 }
 last = value
 }

 public fun pushPunct(punctuation: P) {
 val value = last
 require(value != null) {
 "Punctuated.pushPunct: cannot push punctuation if Punctuated is empty or already has trailing punctuation"
 }
 inner += value to punctuation
 last = null
 }

 public fun push(value: T, defaultPunctuation: () -> P) {
 if (!emptyOrTrailing()) {
 pushPunct(defaultPunctuation())
 }
 pushValue(value)
 }

 public fun add(value: T) {
 require(emptyOrTrailing()) {
 "Punctuated.add requires trailing punctuation before appending another value"
 }
 pushValue(value)
 }

 public fun pop(): PunctuatedPair<T, P>? {
 val tail = last
 return if (tail != null) {
 last = null
 PunctuatedPair.End(tail)
 } else {
 inner.removeLastOrNull()?.let { PunctuatedPair.Punctuated(it.first, it.second) }
 }
 }

 public fun popPunct(): P? =
 if (last != null) {
 null
 } else {
 inner.removeLastOrNull()?.let { (value, punctuation) ->
 last = value
 punctuation
 }
 }

 public fun trailingPunct(): Boolean =
 last == null && !isEmpty()

 public fun emptyOrTrailing(): Boolean =
 last == null

 public fun insert(index: Int, value: T, defaultPunctuation: () -> P) {
 require(index <= len()) { "insert index out of bounds" }
 if (index == len()) {
 push(value, defaultPunctuation)
 return
 }
 inner.add(index, value to defaultPunctuation())
 }

 public fun clear() {
 inner.clear()
 last = null
 }

 public fun toList(): List<T> =
 iterator().asSequence().toList()

 override fun iterator(): Iterator<T> =
 sequence {
 for ((value, _) in inner) {
 yield(value)
 }
 val tail = last
 if (tail != null) {
 yield(tail)
 }
 }.iterator()

 public fun iter(): Iterator<T> =
 iterator()

 public fun iterMut(): MutableList<T> =
 toMutableList()

 public fun firstMut(): T? =
 inner.firstOrNull()?.first

 public fun lastMut(): T? =
 last ?: inner.lastOrNull()?.first

 public fun getMut(index: Int): T? =
 getOrNull(index)

 public fun pairsMut(): MutableList<PunctuatedPair<T, P>> =
 buildList {
 for ((value, punctuation) in inner) {
 add(PunctuatedPair.Punctuated(value, punctuation))
 }
 val tail = last
 if (tail != null) {
 add(PunctuatedPair.End(tail))
 }
 }.toMutableList()

 public fun intoPairs(): List<PunctuatedPair<T, P>> =
 pairs().map { (value, punctOpt) ->
 if (punctOpt != null) PunctuatedPair.Punctuated(value, punctOpt) else PunctuatedPair.End(value)
 }

 public fun intoValue(): T? =
 if (len() == 1) first() else null

 public fun value(): T? =
 intoValue()

 public fun valueMut(): T? =
 intoValue()

 public fun punct(index: Int): P? =
 inner.getOrNull(index)?.second

 public fun punctMut(index: Int): P? =
 inner.getOrNull(index)?.second

 public fun intoTuple(): List<Triple<T, P?, Boolean>> =
 buildList {
 for ((i, pair) in inner.withIndex()) {
 add(Triple(pair.first, pair.second, false))
 }
 val tail = last
 if (tail != null) {
 add(Triple(tail, null, true))
 }
 }

 public fun <R> fold(initial: R, operation: (acc: R, T) -> R): R {
 var accumulator = initial
 for (item in this) {
 accumulator = operation(accumulator, item)
 }
 return accumulator
 }

 public fun cloned(): List<T> =
 toList()

 public operator fun set(index: Int, value: T) {
 require(index >= 0) { "index must be non-negative" }
 if (index < inner.size) {
 inner[index] = inner[index].copy(first = value)
 } else if (index == inner.size && last != null) {
 last = value
 } else {
 throw IndexOutOfBoundsException("index: $index")
 }
 }

 public fun fromIterable(elements: Iterable<T>, defaultPunctuation: () -> P): Punctuated<T, P> {
 val result = Punctuated<T, P>()
 for (element in elements) {
 result.push(element, defaultPunctuation)
 }
 return result
 }

 public fun extend(elements: Iterable<T>, defaultPunctuation: () -> P) {
 for (element in elements) {
 push(element, defaultPunctuation)
 }
 }

 public fun doExtend(elements: Iterable<T>, defaultPunctuation: () -> P) {
 extend(elements, defaultPunctuation)
 }

 override fun equals(other: Any?): Boolean {
 if (this === other) return true
 if (other !is Punctuated<*, *>) return false
 if (len() != other.len()) return false
 val thisItems = this.toList()
 val otherItems = other.toList()
 return thisItems == otherItems
 }

 override fun hashCode(): Int {
 var result = 1
 for (item in this) {
 result = 31 * result + (item.hashCode())
 }
 return result
 }

 override fun toString(): String =
 joinToString(", ", "[", "]") { it.toString() }

 public fun copy(copyValue: (T) -> T = { it }, copyPunctuation: (P) -> P = { it }): Punctuated<T, P> =
 Punctuated(
 inner = inner.mapTo(mutableListOf()) { (value, punctuation) ->
 copyValue(value) to copyPunctuation(punctuation)
 },
 last = last?.let(copyValue),
 )
 override fun toTokens(tokens: TokenStream) {
 for ((value, punct) in pairs()) {
 value.toTokens(tokens)
 punct?.toTokens(tokens)
 }
 }
}

@HiddenFromObjC
public sealed class PunctuatedPair<out T : ToTokens, out P : ToTokens> {
 public data class Punctuated<T : ToTokens, P : ToTokens>(val value: T, val punctuation: P) : PunctuatedPair<T, P>(), ToTokens {
  override fun toTokens(tokens: TokenStream) {
   value.toTokens(tokens)
   punctuation.toTokens(tokens)
  }
 }
 public data class End<T : ToTokens>(val value: T) : PunctuatedPair<T, kotlin.Nothing>(), ToTokens {
  override fun toTokens(tokens: TokenStream) {
   value.toTokens(tokens)
  }
 }
}

@HiddenFromObjC
public fun <T : ToTokens> emptyPunctuatedIter(): Iterator<T> =
 emptyList<T>().iterator()
