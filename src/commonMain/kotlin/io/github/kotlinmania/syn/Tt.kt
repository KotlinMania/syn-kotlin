// port-lint: source tt.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

/**
 * Compares two token trees for structural equality, ignoring spans.
 *
 * The upstream spelling is `TokenTreeHelper` with `equals` and `hashCode` implementations. Kotlin wrapper-class
 * idiom is heavier than just exposing helper free functions, so the helpers
 * surface as [tokenTreeEq] and [tokenStreamEq] (plus matching hash helpers).
 */
internal fun tokenTreeEq(a: TokenTree, b: TokenTree): Boolean = when {
 a is TokenTree.Group && b is TokenTree.Group -> {
 if (a.value.delimiter() != b.value.delimiter()) false
 else tokenStreamEq(a.value.stream(), b.value.stream())
 }
 a is TokenTree.Punct && b is TokenTree.Punct -> {
 a.value.asChar() == b.value.asChar() &&
 (
 (a.value.spacing() == Spacing.Alone && b.value.spacing() == Spacing.Alone) ||
 (a.value.spacing() == Spacing.Joint && b.value.spacing() == Spacing.Joint)
 )
 }
 a is TokenTree.Literal && b is TokenTree.Literal -> a.value.toString() == b.value.toString()
 a is TokenTree.Ident && b is TokenTree.Ident -> a.value == b.value
 else -> false
}

/**
 * Structural hash of a token tree, ignoring spans. Mirrors the upstream
 * `hashCode` implementation on `TokenTreeHelper`.
 */
internal fun tokenTreeHash(tree: TokenTree): Int {
 var hash = 0
 fun mix(value: Int) { hash = hash * 31 + value }
 when (tree) {
 is TokenTree.Group -> {
 mix(0)
 mix(tree.value.delimiter().ordinal)
 for (item in tree.value.stream()) {
 mix(tokenTreeHash(item))
 }
 mix(0xFF) // terminator, distinct from variant tags
 }
 is TokenTree.Punct -> {
 mix(1)
 mix(tree.value.asChar().code)
 mix(if (tree.value.spacing() == Spacing.Alone) 0 else 1)
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
 return hash
}

/**
 * Compares two token streams for structural equality, ignoring spans.
 * Mirrors the upstream `equals` implementation on `TokenStreamHelper`.
 */
internal fun tokenStreamEq(left: TokenStream, right: TokenStream): Boolean {
 val leftIter = left.iterator()
 val rightIter = right.iterator()
 while (leftIter.hasNext()) {
 if (!rightIter.hasNext()) return false
 val item1 = leftIter.next()
 val item2 = rightIter.next()
 if (!tokenTreeEq(item1, item2)) return false
 }
 return !rightIter.hasNext()
}

/**
 * Structural hash of a token stream, ignoring spans. Mirrors the upstream
 * `hashCode` implementation on `TokenStreamHelper`.
 */
internal fun tokenStreamHash(stream: TokenStream): Int {
 val items = stream.toList()
 var hash = items.size
 for (tt in items) {
 hash = hash * 31 + tokenTreeHash(tt)
 }
 return hash
}
