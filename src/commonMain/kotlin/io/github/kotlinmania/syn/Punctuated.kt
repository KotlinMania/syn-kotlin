// port-lint: source punctuated.rs
package io.github.kotlinmania.syn

/**
 * A punctuated sequence of syntax tree nodes of type [T] separated by
 * punctuation of type [P].
 *
 * Rust syntax uses this shape for struct fields, path segments, generic
 * arguments, function inputs, and many other comma- or operator-separated
 * lists. Every node except possibly the final one is paired with punctuation.
 */
public class Punctuated<T, P> private constructor(
    private val inner: MutableList<Pair<T, P>>,
    private var last: T?,
) : Iterable<T> {
    public constructor() : this(mutableListOf(), null)

    public companion object {
        public fun <T, P> new(): Punctuated<T, P> =
            Punctuated()

        /**
         * Parses zero or more occurrences of [T] separated by punctuation of
         * type P, with optional trailing punctuation.
         *
         * Parsing continues until the end of this parse stream. The entire
         * content of this parse stream must consist of [T] and P.
         */
        public fun <T, P> parseTerminated(
            input: ParseStream,
            valueParse: Parse<T>,
            punctParse: Parse<P>,
        ): Result<Punctuated<T, P>> =
            parseTerminatedWith(input, valueParse::parse, punctParse)

        /**
         * Parses zero or more occurrences of [T] using the given parse
         * function, separated by punctuation of type P, with optional
         * trailing punctuation.
         */
        public fun <T, P> parseTerminatedWith(
            input: ParseStream,
            parser: (ParseStream) -> Result<T>,
            punctParse: Parse<P>,
        ): Result<Punctuated<T, P>> {
            val punctuated = Punctuated<T, P>()

            while (true) {
                if (input.isEmpty()) break
                val value = parser(input).getOrElse { return Result.failure(it) }
                punctuated.pushValue(value)
                if (input.isEmpty()) break
                val punct = punctParse.parse(input).getOrElse { return Result.failure(it) }
                punctuated.pushPunct(punct)
            }

            return Result.success(punctuated)
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
        public fun <T, P> parseSeparatedNonempty(
            input: ParseStream,
            valueParse: Parse<T>,
            punctParse: Parse<P>,
            punctPeek: Peek,
        ): Result<Punctuated<T, P>> =
            parseSeparatedNonemptyWith(input, valueParse::parse, punctParse, punctPeek)

        /**
         * Parses one or more occurrences of [T] using the given parse
         * function, separated by punctuation of type P, not accepting
         * trailing punctuation.
         */
        public fun <T, P> parseSeparatedNonemptyWith(
            input: ParseStream,
            parser: (ParseStream) -> Result<T>,
            punctParse: Parse<P>,
            punctPeek: Peek,
        ): Result<Punctuated<T, P>> {
            val punctuated = Punctuated<T, P>()

            while (true) {
                val value = parser(input).getOrElse { return Result.failure(it) }
                punctuated.pushValue(value)
                if (!punctPeek.peek(input.cursor())) break
                val punct = punctParse.parse(input).getOrElse { return Result.failure(it) }
                punctuated.pushPunct(punct)
            }

            return Result.success(punctuated)
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

    public fun copy(copyValue: (T) -> T = { it }, copyPunctuation: (P) -> P = { it }): Punctuated<T, P> =
        Punctuated(
            inner = inner.mapTo(mutableListOf()) { (value, punctuation) ->
                copyValue(value) to copyPunctuation(punctuation)
            },
            last = last?.let(copyValue),
        )
}

public sealed class PunctuatedPair<out T, out P> {
    public data class Punctuated<T, P>(val value: T, val punctuation: P) : PunctuatedPair<T, P>()
    public data class End<T>(val value: T) : PunctuatedPair<T, kotlin.Nothing>()
}

public fun <T> emptyPunctuatedIter(): Iterator<T> =
    emptyList<T>().iterator()
