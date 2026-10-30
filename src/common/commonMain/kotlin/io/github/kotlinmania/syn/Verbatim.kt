// port-lint: source verbatim.rs
@file:Suppress("ClassNaming", "UnusedPrivateFunction")

package io.github.kotlinmania.syn

// Generated from Syn AST; exact original retained in input.rs.
import io.github.kotlinmania.syn.appendTokenTree as __rtkVerbatimAppendTokenTree

// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
// Rust use resolved through explicit library bindings.
internal fun `between`(
    `begin`: io.github.kotlinmania.syn.ParseBuffer,
    `end`: io.github.kotlinmania.syn.ParseBuffer,
): io.github.kotlinmania.procmacro2.TokenStream = kotlin.run {
    val `end__binding0`: io.github.kotlinmania.syn.Cursor = (`end`).`cursor`()
    val `cursor`: io.github.kotlinmania.syn.Cursor = (`begin`).`cursor`()
    kotlin.run {
        if (!(io.github.kotlinmania.syn.sameBuffer(`end__binding0`, `cursor`)))
            throw __rtkVerbatimPanic("assertion failed: crate::buffer::same_buffer(end, cursor)")
    }
    val `tokens`: io.github.kotlinmania.procmacro2.TokenStream =
        io.github.kotlinmania.procmacro2.TokenStream.new()
    kotlin.run {
        while ((`cursor` != `end__binding0`)) kotlin.run {
            val __rtkVerbatimValue2:
                kotlin.Pair<
                    io.github.kotlinmania.procmacro2.TokenTree,
                    io.github.kotlinmania.syn.Cursor,
                > =
                __rtkVerbatimUnwrap(
                    kotlin.run {
                        val __rtkVerbatimValue1 = (`cursor`).tokenTree()
                        if (__rtkVerbatimValue1 == null) __rtkVerbatimOption.None
                        else __rtkVerbatimOption.Some(__rtkVerbatimValue1)
                    },
                )
            val `tt`: io.github.kotlinmania.procmacro2.TokenTree = (__rtkVerbatimValue2).first
            val `next`: io.github.kotlinmania.syn.Cursor = (__rtkVerbatimValue2).second
            kotlin.run {
                if (
                    (__rtkVerbatimOrderingFromComparison(
                        io.github.kotlinmania.syn.cmpAssumingSameBuffer(`end__binding0`, `next`),
                    ) == __rtkVerbatimOrdering.Less)
                )
                    ( // A syntax node can cross the boundary of a None-delimited group
                    // due to such groups being transparent to the parser in most cases.
                    // Any time this occurs the group is known to be semantically
                    // irrelevant. https://github.com/dtolnay/syn/issues/1235
                    kotlin.run {
                        val __rtkVerbatimValue4:
                            __rtkVerbatimOption<
                                kotlin.Triple<
                                    io.github.kotlinmania.syn.Cursor,
                                    io.github.kotlinmania.procmacro2.DelimSpan,
                                    io.github.kotlinmania.syn.Cursor,
                                >,
                            > =
                            kotlin.run {
                                val __rtkVerbatimValue3 =
                                    (`cursor`).group(
                                        io.github.kotlinmania.procmacro2.Delimiter.None,
                                    )
                                if (__rtkVerbatimValue3 == null) __rtkVerbatimOption.None
                                else __rtkVerbatimOption.Some(__rtkVerbatimValue3)
                            }
                        if (__rtkVerbatimValue4 is __rtkVerbatimOption.Some)
                            kotlin.run {
                                val `inside`: io.github.kotlinmania.syn.Cursor =
                                    (__rtkVerbatimValue4.value).first
                                val `after`: io.github.kotlinmania.syn.Cursor =
                                    (__rtkVerbatimValue4.value).third
                                kotlin.run {
                                    kotlin.run {
                                        if (!((`next` == `after`)))
                                            throw __rtkVerbatimPanic(
                                                "assertion failed: next == after",
                                            )
                                    }
                                    kotlin.run {
                                        val __rtkVerbatimValue5: io.github.kotlinmania.syn.Cursor =
                                            `inside`
                                        `cursor` = __rtkVerbatimValue5
                                    }
                                    continue
                                }
                            }
                        else
                            (kotlin.run {
                                throw __rtkVerbatimPanic(
                                    "" + "verbatim end must not be inside a delimited group",
                                )
                            })
                    })
            }
            (`tokens`).__rtkVerbatimAppendTokenTree(`tt`)
            kotlin.run {
                val __rtkVerbatimValue6: io.github.kotlinmania.syn.Cursor = `next`
                `cursor` = __rtkVerbatimValue6
            }
        }
    }
    `tokens`
}

// Rust core::cmp::Ordering: tags are distinct from Kotlin enum ordinals.
internal enum class __rtkVerbatimOrdering {
    Less,
    Equal,
    Greater,
}

private fun __rtkVerbatimOrderingFromComparison(value: kotlin.Int): __rtkVerbatimOrdering =
    when {
        value < 0 -> __rtkVerbatimOrdering.Less
        value > 0 -> __rtkVerbatimOrdering.Greater
        else -> __rtkVerbatimOrdering.Equal
    }

// Tagged Rust Option; owned payloads require a checked fresh-value context.
internal sealed class __rtkVerbatimOption<out T> {
    data object None : __rtkVerbatimOption<kotlin.Nothing>()

    data class Some<T>(val value: T) : __rtkVerbatimOption<T>()
}

private fun <T> __rtkVerbatimUnwrapOr(value: __rtkVerbatimOption<T>, fallback: T): T =
    when (value) {
        __rtkVerbatimOption.None -> fallback
        is __rtkVerbatimOption.Some -> value.value
    }

internal class __rtkVerbatimPanic(message: String) : kotlin.RuntimeException(message)

private fun <T> __rtkVerbatimUnwrap(value: __rtkVerbatimOption<T>): T =
    when (value) {
        is __rtkVerbatimOption.Some -> value.value
        __rtkVerbatimOption.None ->
            throw __rtkVerbatimPanic("called `Option::unwrap()` on a `None` value")
    }
