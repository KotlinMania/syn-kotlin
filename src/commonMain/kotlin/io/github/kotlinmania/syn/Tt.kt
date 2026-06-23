// port-lint: source tt.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

/**
 * Structural equality and hashing for token trees and token streams,
 * ignoring spans.
 */
internal class TokenTreeHelper(
    private val tree: TokenTree,
) {
    fun eq(other: TokenTreeHelper): Boolean =
        when {
            tree is TokenTree.Group && other.tree is TokenTree.Group -> {
                if (tree.value.delimiter() != other.tree.value.delimiter()) {
                    false
                } else {
                    TokenStreamHelper(tree.value.stream()).eq(TokenStreamHelper(other.tree.value.stream()))
                }
            }
            tree is TokenTree.Punct && other.tree is TokenTree.Punct ->
                tree.value.asChar() == other.tree.value.asChar() &&
                    tree.value.spacing() == other.tree.value.spacing()
            tree is TokenTree.Literal && other.tree is TokenTree.Literal ->
                tree.value.toString() == other.tree.value.toString()
            tree is TokenTree.Ident && other.tree is TokenTree.Ident ->
                tree.value == other.tree.value
            else -> false
        }

    fun hash(): Int {
        var result = 0

        fun mix(value: Int) {
            result = result * 31 + value
        }

        when (tree) {
            is TokenTree.Group -> {
                mix(0)
                mix(delimiterHash(tree.value.delimiter()))
                for (item in tree.value.stream()) {
                    mix(TokenTreeHelper(item).hash())
                }
                mix(0xFF)
            }
            is TokenTree.Punct -> {
                mix(1)
                mix(tree.value.asChar().code)
                mix(spacingHash(tree.value.spacing()))
            }
            is TokenTree.Literal -> {
                mix(2)
                mix(tree.value.toString().hashCode())
            }
            is TokenTree.Ident -> {
                mix(3)
                mix(tree.value.hashCode())
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean =
        other is TokenTreeHelper && eq(other)

    override fun hashCode(): Int =
        hash()
}

internal class TokenStreamHelper(
    private val stream: TokenStream,
) {
    fun eq(other: TokenStreamHelper): Boolean {
        val left = stream.iterator()
        val right = other.stream.iterator()
        while (left.hasNext()) {
            if (!right.hasNext()) return false
            if (!TokenTreeHelper(left.next()).eq(TokenTreeHelper(right.next()))) return false
        }
        return !right.hasNext()
    }

    fun hash(): Int {
        val tokens = stream.toList()
        var result = tokens.size
        for (token in tokens) {
            result = result * 31 + TokenTreeHelper(token).hash()
        }
        return result
    }

    override fun equals(other: Any?): Boolean =
        other is TokenStreamHelper && eq(other)

    override fun hashCode(): Int =
        hash()
}

public fun tokenTreeEq(a: TokenTree, b: TokenTree): Boolean =
    TokenTreeHelper(a).eq(TokenTreeHelper(b))

/** Structural hash of a token tree, ignoring spans. */
public fun tokenTreeHash(tree: TokenTree): Int =
    TokenTreeHelper(tree).hash()

/** Structural equality of two token streams, ignoring spans. */
public fun eq(a: TokenTree, b: TokenTree): Boolean = tokenTreeEq(a, b)

public fun hash(tree: TokenTree): Int = tokenTreeHash(tree)

public fun tokenStreamEq(left: TokenStream, right: TokenStream): Boolean =
    TokenStreamHelper(left).eq(TokenStreamHelper(right))

/** Structural hash of a token stream, ignoring spans. */
public fun tokenStreamHash(stream: TokenStream): Int =
    TokenStreamHelper(stream).hash()

private fun delimiterHash(delimiter: Delimiter): Int =
    when (delimiter) {
        Delimiter.Parenthesis -> 0
        Delimiter.Brace -> 1
        Delimiter.Bracket -> 2
        Delimiter.None -> 3
    }

private fun spacingHash(spacing: Spacing): Int =
    when (spacing) {
        Spacing.Alone -> 0
        Spacing.Joint -> 1
    }
