// port-lint: tests tests/test_punctuated.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.syn.token.Comma
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** A token-like integer wrapper for testing Punctuated with ToTokens bounds. */
private data class IntToken(
    val v: Int,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        val lit =
            Literal
                .i32Suffixed(v)
        tokens.extendTokenTrees(
            listOf(
                TokenTree
                    .Literal(lit),
            ),
        )
    }
}

private class BoxToken(
    var v: Int,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        IntToken(v).toTokens(tokens)
    }
}

private data class SepToken(
    val label: String,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        tokens.extendTokenTrees(
            listOf(
                TokenTree
                    .Literal(Literal.string(label)),
            ),
        )
    }
}

private fun punctuatedIntComma(vararg values: Int): Punctuated {
    val seq = Punctuated.new()
    for (value in values) {
        seq.push(IntToken(value), Comma::default)
    }
    return seq
}

private fun ToTokens.intToken(): IntToken = this as IntToken

private fun ToTokens.boxToken(): BoxToken = this as BoxToken

private fun ToTokens.litInt(): LitInt = this as LitInt

private fun Punctuated.intValues(): List<Int> =
    toList().map { it.intToken().v }

private fun Punctuated.boxValues(): List<Int> =
    toList().map { it.boxToken().v }

private fun Punctuated.litIntValues(): List<Long> =
    toList().map { it.litInt().base10Parse() }

private fun <T> Iterator<T>.remainingList(): List<T> =
    buildList {
        val iterator = this@remainingList
        while (iterator.hasNext()) add(iterator.next())
    }

private fun <T> Iterator<T>.remainingCount(): Int = remainingList().size

class PunctuatedTest {
    @Test
    fun pairs() {
        val p = punctuatedIntComma(2, 3, 4)

        val pairs = p.pairs()
        assertEquals(3, pairs.len())
        assertEquals(3 to 3, pairs.sizeHint())
        assertEquals(4, pairs.clone().nextBack()?.intoValue()?.intToken()?.v)

        val pairsList = p.intoPairs().remainingList()
        assertEquals(3, pairsList.size)
        val lastPair = pairsList.last()
        assertEquals(4, lastPair.value().intToken().v)
        assertEquals(4, lastPair.valueMut().intToken().v)
        assertEquals(4, lastPair.intoValue().intToken().v)
        assertNull(lastPair.punct())
        assertTrue(lastPair is Punctuated.Pair.End)
        val firstPair = pairsList.first()
        assertEquals(2, firstPair.intoTuple().first.intToken().v)
        assertNotNull(firstPair.punct())
        assertNotNull(firstPair.punctMut())
        assertEquals(2, firstPair.cloned().value().intToken().v)
        assertTrue(firstPair is Punctuated.Pair.Punctuated)
    }

    @Test
    fun extendPairs() {
        val p = Punctuated.new()
        p.pushValue(IntToken(1))

        p.extendPairs(
            listOf(
                Punctuated.Pair.Punctuated(IntToken(2), Comma.default()),
                Punctuated.Pair.End(IntToken(3)),
            ),
            Comma::default,
        )

        assertEquals(listOf(1, 2, 3), p.intValues())
        assertNotNull(p.punct(0))
        assertNotNull(p.punct(1))
        assertNull(p.punct(2))
    }

    @Test
    fun extendPairSequence() {
        val p = Punctuated.new()
        p.pushValue(IntToken(1))

        p.extend(
            sequenceOf(
                Punctuated.Pair.Punctuated(IntToken(2), Comma.default()),
                Punctuated.Pair.End(IntToken(3)),
            ),
            Comma::default,
        )

        assertEquals(listOf(1, 2, 3), p.intValues())
        assertNotNull(p.punct(0))
        assertNotNull(p.punct(1))
        assertNull(p.punct(2))
    }

    @Test
    fun fromIterAndDefault() {
        val empty = Punctuated.default()
        assertTrue(empty.isEmpty())

        val values = Punctuated.fromIter(listOf(IntToken(1), IntToken(2)), Comma::default)
        assertEquals(listOf(1, 2), values.intValues())
        assertFalse(values.trailingPunct())

        val pairs =
            Punctuated.fromIter(
                sequenceOf(
                    Punctuated.Pair.Punctuated(IntToken(3), Comma.default()),
                    Punctuated.Pair.End(IntToken(4)),
                ),
            )
        assertEquals(listOf(3, 4), pairs.intValues())
        assertFalse(pairs.trailingPunct())
    }

    @Test
    fun fromPairsPanicsAfterEndPair() {
        assertFailsWith<IllegalStateException> {
            Punctuated.fromPairs(
                listOf(
                    Punctuated.Pair.End(IntToken(1)),
                    Punctuated.Pair.Punctuated(IntToken(2), Comma.default()),
                ),
            )
        }
    }

    @Test
    fun iter() {
        val p = punctuatedIntComma(2, 3, 4)

        val values = p.toList()
        assertEquals(3, values.size)
        assertEquals(4, values.last().intToken().v)
        assertEquals(3, p.iter().remainingCount())
        assertEquals(3, p.iterMut().remainingCount())
        assertEquals(3, p.pairsMut().remainingCount())
        assertEquals(4, p.iter().nextBack()?.intToken()?.v)
        assertEquals(2, p.iterMut().next().intToken().v)
        assertEquals(3, p.intoIter().len())
        assertEquals(4, p.intoIter().nextBack()?.intToken()?.v)
    }

    @Test
    fun ownedIteratorsCloneAtCurrentPosition() {
        val p = punctuatedIntComma(1, 2, 3)

        val pairs = p.intoPairs()
        assertEquals(1, pairs.next().intoValue().intToken().v)
        val pairsClone = pairs.clone()
        assertEquals(2, pairs.next().intoValue().intToken().v)
        assertEquals(2, pairsClone.next().intoValue().intToken().v)
        assertEquals(3, pairs.nextBack()?.intoValue()?.intToken()?.v)
        assertEquals(3, pairsClone.nextBack()?.intoValue()?.intToken()?.v)

        val values = p.intoIter()
        assertEquals(1, values.next().intToken().v)
        val valuesClone = values.clone()
        assertEquals(2, values.next().intToken().v)
        assertEquals(2, valuesClone.next().intToken().v)
        assertEquals(3, values.nextBack()?.intToken()?.v)
        assertEquals(3, valuesClone.nextBack()?.intToken()?.v)
    }

    @Test
    fun emptyPunctuatedIterators() {
        assertFalse(emptyPunctuatedIter().hasNext())
        assertFalse(emptyPunctuatedIterMut().hasNext())
    }

    @Test
    fun toSynPunctuated() {
        val p = Punctuated.new()
        p.pushValue(IntToken(1))
        p.pushPunct(Comma.default())
        p.pushValue(IntToken(2))

        val syn = p.toSynPunctuated()
        val pairs = syn.pairsList()

        assertEquals(2, syn.len())
        assertEquals(listOf(1, 2), syn.map { (it as IntToken).v })
        assertNotNull(pairs.first().second)
        assertNull(pairs.last().second)
    }

    @Test
    fun mayDangle() {
        val p = punctuatedIntComma(2, 3, 4)
        for (element in p.toList()) {
            if (element.intToken().v == 2) {
                break
            }
        }

        val q = punctuatedIntComma(2, 3, 4)
        for (element in q.toList()) {
            if (element.intToken().v == 2) {
                break
            }
        }
    }

    @Test
    fun indexOutOfBounds() {
        val p = Punctuated.new()
        assertFailsWith<IndexOutOfBoundsException> {
            p[0]
        }
    }

    @Test
    fun concreteListIndexReturnsTrailingLastElement() {
        val lifetimes = LifetimeList()
        val first = Lifetime.new("'a", Span.callSite())
        val second = Lifetime.new("'b", Span.callSite())

        lifetimes.pushValue(first)
        lifetimes.pushPunct(Comma.default())
        lifetimes.pushValue(second)

        assertEquals(first, lifetimes[0])
        assertEquals(second, lifetimes[1])
    }

    @Test
    fun insert() {
        val p = punctuatedIntComma(1, 3)

        p.insert(1, IntToken(2), Comma::default)
        p.insert(3, IntToken(4), Comma::default)

        assertEquals(listOf(1, 2, 3, 4), p.intValues())
        assertNotNull(p.punct(1))
        assertNotNull(p.punctMut(1))
        assertNull(p.punct(3))
        assertFalse(p.trailingPunct())
    }

    @Test
    fun insertOutOfBounds() {
        val p = punctuatedIntComma(1)

        assertFailsWith<IllegalArgumentException> {
            p.insert(2, IntToken(2), Comma::default)
        }
    }

    @Test
    fun mutableAccessorsReturnStoredElements() {
        val p = Punctuated.new()
        p.push(BoxToken(1), Comma::default)
        p.push(BoxToken(2), Comma::default)

        p.firstMut()?.boxToken()?.v = 10
        p.lastMut()?.boxToken()?.v = 20
        p.getMut(0)?.boxToken()?.v = 11

        assertEquals(listOf(11, 20), p.boxValues())
    }

    @Test
    fun equalityIncludesPunctuation() {
        val left = Punctuated.new()
        left.pushValue(IntToken(1))
        left.pushPunct(SepToken("a"))
        left.pushValue(IntToken(2))

        val same = Punctuated.new()
        same.pushValue(IntToken(1))
        same.pushPunct(SepToken("a"))
        same.pushValue(IntToken(2))

        val differentPunctuation = Punctuated.new()
        differentPunctuation.pushValue(IntToken(1))
        differentPunctuation.pushPunct(SepToken("b"))
        differentPunctuation.pushValue(IntToken(2))

        assertEquals(left, same)
        assertEquals(left.hashCode(), same.hashCode())
        assertNotEquals(left, differentPunctuation)
    }

    @Test
    fun cloneFromReplacesContents() {
        val target = Punctuated.new()
        target.push(BoxToken(1), Comma::default)

        val source = Punctuated.new()
        source.push(BoxToken(2), Comma::default)
        source.push(BoxToken(3), Comma::default)

        target.cloneFrom(source, copyValue = { BoxToken(it.boxToken().v) })

        source.firstMut()?.boxToken()?.v = 20
        assertEquals(listOf(2, 3), target.boxValues())
    }

    @Test
    fun cloneCopiesSequenceContents() {
        val original = Punctuated.new()
        original.pushValue(BoxToken(1))
        original.pushPunct(SepToken("a"))
        original.pushValue(BoxToken(2))

        val cloned =
            original.clone(
                copyValue = { BoxToken(it.boxToken().v) },
                copyPunctuation = { (it as SepToken).copy() },
            )

        original.firstMut()?.boxToken()?.v = 10

        assertEquals(listOf(10, 2), original.boxValues())
        assertEquals(listOf(1, 2), cloned.boxValues())
        assertEquals(original.punct(0), cloned.punct(0))
    }

    @Test
    fun parseTerminatedAcceptsTrailingPunctuation() {
        val parser =
            parserFromFunction { input ->
                Punctuated.parseTerminated(input, LitIntParse::parse, CommaParse::parse)
            }

        val parsed = parser.parse2(TokenStream.fromString("1, 2,").getOrThrow()).getOrThrow()

        assertEquals(listOf(1L, 2L), parsed.litIntValues())
        assertTrue(parsed.trailingPunct())
    }

    @Test
    fun parseSeparatedNonemptyStopsBeforeNonSeparator() {
        val parser =
            parserFromFunction { input ->
                val parsed =
                    Punctuated.parseSeparatedNonempty(
                        input,
                        LitIntParse::parse,
                        CommaPeek,
                        CommaParse::parse,
                    ).getOrElse { return@parserFromFunction SynResult.failure(it) }

                val remaining = LitIntParse.parse(input).getOrElse { return@parserFromFunction SynResult.failure(it) }
                assertEquals(3L, remaining.base10Parse())
                SynResult.success(parsed)
            }

        val parsed = parser.parse2(TokenStream.fromString("1, 2 3").getOrThrow()).getOrThrow()

        assertEquals(listOf(1L, 2L), parsed.litIntValues())
        assertFalse(parsed.trailingPunct())
    }

    @Test
    fun parseSeparatedNonemptyRequiresOneElement() {
        val parser =
            parserFromFunction { input ->
                Punctuated.parseSeparatedNonempty(
                    input,
                    LitIntParse::parse,
                    CommaPeek,
                    CommaParse::parse,
                )
            }

        assertTrue(parser.parse2(TokenStream.fromString("").getOrThrow()).isFailure)
    }
}
