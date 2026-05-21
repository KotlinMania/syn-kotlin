// port-lint: source buffer.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.quote.append

// A stably addressed token buffer supporting efficient traversal based on a
// cheaply copyable cursor.

// This module is heavily commented as it contains most of the unsafe code in
// Syn, and caution should be used when editing it. The public-facing interface
// is 100% safe but the implementation is fragile internally.

/**
 * Internal type which is used instead of [TokenTree] to represent a token tree
 * within a [TokenBuffer].
 */
internal sealed class Entry {
    // Mimicking types from proc-macro.
    // Group entries contain the offset to the matching End entry.
    class GroupEntry(val group: Group, val endOffset: Int) : Entry()
    class IdentEntry(val ident: Ident) : Entry()
    class PunctEntry(val punct: Punct) : Entry()
    class LiteralEntry(val literal: Literal) : Entry()
    // End entries contain the offset (negative) to the start of the buffer, and
    // offset (negative) to the matching Group entry.
    class End(val toStart: Int, val toGroup: Int) : Entry()
}

/**
 * A buffer that can be efficiently traversed multiple times, unlike
 * [TokenStream] which requires a deep copy in order to traverse more than
 * once.
 */
public class TokenBuffer internal constructor(
    // NOTE: Do not implement clone on this — while the current design could be
    // cloned, other designs which could be desirable may not be cloneable.
    internal val entries: Array<Entry>,
) {
    public companion object {
        private fun recursiveNew(entries: MutableList<Entry>, stream: TokenStream) {
            for (tt in stream) {
                when (tt) {
                    is TokenTree.Ident -> entries.add(Entry.IdentEntry(tt.value))
                    is TokenTree.Punct -> entries.add(Entry.PunctEntry(tt.value))
                    is TokenTree.Literal -> entries.add(Entry.LiteralEntry(tt.value))
                    is TokenTree.Group -> {
                        val groupStartIndex = entries.size
                        entries.add(Entry.End(0, 0)) // we replace this below
                        recursiveNew(entries, tt.value.stream())
                        val groupEndIndex = entries.size
                        val groupOffset = groupEndIndex - groupStartIndex
                        entries.add(Entry.End(-groupEndIndex, -groupOffset))
                        entries[groupStartIndex] = Entry.GroupEntry(tt.value, groupOffset)
                    }
                }
            }
        }

        /**
         * Creates a [TokenBuffer] containing all the tokens from the input
         * [TokenStream].
         */
        public fun new2(stream: TokenStream): TokenBuffer {
            val entries = mutableListOf<Entry>()
            recursiveNew(entries, stream)
            entries.add(Entry.End(-entries.size, 0))
            return TokenBuffer(entries.toTypedArray())
        }
    }

    /**
     * Creates a cursor referencing the first token in the buffer and able to
     * traverse until the end of the buffer.
     */
    public fun begin(): Cursor = Cursor.create(entries, 0, entries.size - 1)
}

/**
 * A cheaply copyable cursor into a [TokenBuffer].
 *
 * This cursor holds a shared reference into the immutable data which is used
 * internally to represent a [TokenStream], and can be efficiently manipulated
 * and copied around.
 *
 * An empty [Cursor] can be created directly, or one may create a [TokenBuffer]
 * object and get a cursor to its first token with [TokenBuffer.begin].
 */
public class Cursor internal constructor(
    // The shared entries array backing this cursor's traversal.
    internal val entries: Array<Entry>,
    // The current entry which the [Cursor] is pointing at.
    internal val index: Int,
    // This is the only [Entry.End] object which this cursor is allowed to
    // point at. All other [Entry.End] objects are skipped over in [create].
    internal val scope: Int,
) {
    public companion object {
        // It's safe in this situation for us to put an [Entry] object in global
        // storage, despite the upstream Rust note about thread-locality
        // (`Ident` was a reference into a thread-local table). This is because
        // this entry never includes an `Ident` object.
        private val EMPTY_ENTRIES: Array<Entry> = arrayOf(Entry.End(0, 0))

        /** Creates a cursor referencing a static empty token stream. */
        public fun empty(): Cursor = Cursor(EMPTY_ENTRIES, 0, 0)

        /**
         * This create method intelligently exits non-explicitly-entered
         * [Delimiter.None]-delimited scopes when the cursor reaches the end of
         * them, allowing for them to be treated transparently.
         */
        internal fun create(entries: Array<Entry>, index: Int, scope: Int): Cursor {
            // NOTE: If we're looking at an [Entry.End], we want to advance the
            // cursor past it, unless `index == scope`, which means that we're at
            // the edge of our cursor's scope. We should only have
            // `index != scope` at the exit from None-delimited groups entered
            // with `ignoreNone`.
            var i = index
            while (entries[i] is Entry.End) {
                if (i == scope) {
                    break
                }
                i += 1
            }
            return Cursor(entries, i, scope)
        }
    }

    /** Get the current entry. */
    internal fun entry(): Entry = entries[index]

    /**
     * Bump the cursor to point at the next token after the current one. This
     * is undefined behavior if the cursor is currently looking at an
     * [Entry.End].
     *
     * If the cursor is looking at an [Entry.GroupEntry], the bumped cursor will
     * point at the first token in the group (with the same scope end).
     */
    internal fun bumpIgnoreGroup(): Cursor = create(entries, index + 1, scope)

    /**
     * While the cursor is looking at a [Delimiter.None]-delimited group, move
     * it to look at the first token inside instead. If the group is empty,
     * this will move the cursor past the [Delimiter.None]-delimited group.
     */
    private fun ignoreNone(): Cursor {
        var cursor = this
        while (true) {
            val e = cursor.entry()
            if (e is Entry.GroupEntry && e.group.delimiter() == Delimiter.None) {
                cursor = cursor.bumpIgnoreGroup()
            } else {
                break
            }
        }
        return cursor
    }

    /**
     * Checks whether the cursor is currently pointing at the end of its valid
     * scope.
     */
    public fun eof(): Boolean = index == scope

    /**
     * If the cursor is pointing at an [Ident], returns it along with a cursor
     * pointing at the next [TokenTree].
     */
    public fun ident(): Pair<Ident, Cursor>? {
        val c = ignoreNone()
        return when (val e = c.entry()) {
            is Entry.IdentEntry -> e.ident to c.bumpIgnoreGroup()
            else -> null
        }
    }

    /**
     * If the cursor is pointing at a [Punct], returns it along with a cursor
     * pointing at the next [TokenTree].
     */
    public fun punct(): Pair<Punct, Cursor>? {
        val c = ignoreNone()
        val e = c.entry()
        return if (e is Entry.PunctEntry && e.punct.asChar() != '\'') {
            e.punct to c.bumpIgnoreGroup()
        } else {
            null
        }
    }

    /**
     * If the cursor is pointing at a [Literal], return it along with a cursor
     * pointing at the next [TokenTree].
     */
    public fun literal(): Pair<Literal, Cursor>? {
        val c = ignoreNone()
        return when (val e = c.entry()) {
            is Entry.LiteralEntry -> e.literal to c.bumpIgnoreGroup()
            else -> null
        }
    }

    /**
     * If the cursor is pointing at a [Lifetime], returns it along with a
     * cursor pointing at the next [TokenTree].
     */
    public fun lifetime(): Pair<Lifetime, Cursor>? {
        val c = ignoreNone()
        val e = c.entry()
        if (e is Entry.PunctEntry && e.punct.asChar() == '\'' && e.punct.spacing() == Spacing.Joint) {
            val next = c.bumpIgnoreGroup()
            val (ident, rest) = next.ident() ?: return null
            val lifetime = Lifetime(apostrophe = e.punct.span(), ident = ident)
            return lifetime to rest
        }
        return null
    }

    /**
     * If the cursor is pointing at a [Group] with the given delimiter, returns
     * a cursor into that group and one pointing to the next [TokenTree].
     */
    public fun group(delim: Delimiter): Triple<Cursor, DelimSpan, Cursor>? {
        // If we're not trying to enter a none-delimited group, we want to
        // ignore them. We have to make sure to _not_ ignore them when we want
        // to enter them, of course. For obvious reasons.
        val c = if (delim != Delimiter.None) ignoreNone() else this

        val e = c.entry()
        if (e is Entry.GroupEntry && e.group.delimiter() == delim) {
            val span = e.group.delimSpan()
            val endOfGroup = c.index + e.endOffset
            val insideOfGroup = create(c.entries, c.index + 1, endOfGroup)
            val afterGroup = create(c.entries, endOfGroup, c.scope)
            return Triple(insideOfGroup, span, afterGroup)
        }

        return null
    }

    /**
     * If the cursor is pointing at a [Group], returns a cursor into the group
     * and one pointing to the next [TokenTree].
     */
    public fun anyGroup(): AnyGroup? {
        val e = entry()
        if (e is Entry.GroupEntry) {
            val delimiter = e.group.delimiter()
            val span = e.group.delimSpan()
            val endOfGroup = index + e.endOffset
            val insideOfGroup = create(entries, index + 1, endOfGroup)
            val afterGroup = create(entries, endOfGroup, scope)
            return AnyGroup(insideOfGroup, delimiter, span, afterGroup)
        }
        return null
    }

    internal fun anyGroupToken(): Pair<Group, Cursor>? {
        val e = entry()
        if (e is Entry.GroupEntry) {
            val endOfGroup = index + e.endOffset
            val afterGroup = create(entries, endOfGroup, scope)
            return e.group to afterGroup
        }
        return null
    }

    /**
     * Copies all remaining tokens visible from this cursor into a
     * [TokenStream].
     */
    public fun tokenStream(): TokenStream {
        val tokens = TokenStream.new()
        var cursor = this
        while (true) {
            val (tt, rest) = cursor.tokenTree() ?: break
            tokens.append(tt)
            cursor = rest
        }
        return tokens
    }

    /**
     * If the cursor is pointing at a [TokenTree], returns it along with a
     * cursor pointing at the next [TokenTree].
     *
     * Returns null if the cursor has reached the end of its stream.
     *
     * This method does not treat [Delimiter.None]-delimited groups as
     * transparent, and will return a `Group(None, ..)` if the cursor is
     * looking at one.
     */
    public fun tokenTree(): Pair<TokenTree, Cursor>? {
        val tree: TokenTree
        val len: Int
        when (val e = entry()) {
            is Entry.GroupEntry -> { tree = TokenTree.Group(e.group); len = e.endOffset }
            is Entry.LiteralEntry -> { tree = TokenTree.Literal(e.literal); len = 1 }
            is Entry.IdentEntry -> { tree = TokenTree.Ident(e.ident); len = 1 }
            is Entry.PunctEntry -> { tree = TokenTree.Punct(e.punct); len = 1 }
            is Entry.End -> return null
        }

        val rest = create(entries, index + len, scope)
        return tree to rest
    }

    /**
     * Returns the [Span] of the current token, or [Span.callSite] if this
     * cursor points to eof.
     */
    public fun span(): Span {
        return when (val e = entry()) {
            is Entry.GroupEntry -> e.group.span()
            is Entry.LiteralEntry -> e.literal.span()
            is Entry.IdentEntry -> e.ident.span()
            is Entry.PunctEntry -> e.punct.span()
            is Entry.End -> {
                val targetIndex = index + e.toGroup
                val target = entries[targetIndex]
                if (target is Entry.GroupEntry) target.group.spanClose() else Span.callSite()
            }
        }
    }

    /**
     * Returns the [Span] of the token immediately prior to the position of
     * this cursor, or of the current token if there is no previous one.
     */
    internal fun prevSpan(): Span {
        val startIndex = startOfBuffer(this)
        val target = if (startIndex < index) Cursor(entries, index - 1, scope) else this
        return target.span()
    }

    /**
     * Skip over the next token that is not a [Delimiter.None]-delimited group,
     * without cloning it. Returns null if this cursor points to eof.
     *
     * This method treats `'lifetimes` as a single token.
     */
    internal fun skip(): Cursor? {
        val c = ignoreNone()

        val e = c.entry()
        val len = when {
            e is Entry.End -> return null

            // Treat lifetimes as a single tt for the purposes of `skip`.
            e is Entry.PunctEntry && e.punct.asChar() == '\'' && e.punct.spacing() == Spacing.Joint -> {
                if (c.entries[c.index + 1] is Entry.IdentEntry) 2 else 1
            }

            e is Entry.GroupEntry -> e.endOffset
            else -> 1
        }

        return create(c.entries, c.index + len, c.scope)
    }

    internal fun scopeDelimiter(): Delimiter {
        val scopeEntry = entries[scope]
        check(scopeEntry is Entry.End) { "Cursor scope must point at an End entry" }
        val target = entries[scope + scopeEntry.toGroup]
        return if (target is Entry.GroupEntry) target.group.delimiter() else Delimiter.None
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cursor) return false
        return entries === other.entries && index == other.index
    }

    override fun hashCode(): Int = index
}

/**
 * The four-value result of [Cursor.anyGroup]: a cursor inside the group, the
 * group's delimiter, its delim span, and a cursor after the group. The
 * upstream `any_group` returns a 4-tuple; Kotlin has no tuple type that long,
 * so the four fields are surfaced as a small data class.
 */
public data class AnyGroup(
    public val inside: Cursor,
    public val delimiter: Delimiter,
    public val delimSpan: DelimSpan,
    public val after: Cursor,
)

internal fun sameScope(a: Cursor, b: Cursor): Boolean =
    a.entries === b.entries && a.scope == b.scope

internal fun sameBuffer(a: Cursor, b: Cursor): Boolean =
    a.entries === b.entries && startOfBuffer(a) == startOfBuffer(b)

private fun startOfBuffer(cursor: Cursor): Int {
    val scopeEntry = cursor.entries[cursor.scope]
    check(scopeEntry is Entry.End) { "Cursor scope must point at an End entry" }
    return cursor.scope + scopeEntry.toStart
}

internal fun cmpAssumingSameBuffer(a: Cursor, b: Cursor): Int = a.index.compareTo(b.index)

internal fun openSpanOfGroup(cursor: Cursor): Span {
    val e = cursor.entry()
    return if (e is Entry.GroupEntry) e.group.spanOpen() else cursor.span()
}
