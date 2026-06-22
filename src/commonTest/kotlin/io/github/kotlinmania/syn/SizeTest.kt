// port-lint: tests tests/test_size.rs
package io.github.kotlinmania.syn

import kotlin.test.Test

/**
 * Tests for size and layout of syn syntax tree types.
 *
 * The upstream Rust tests assert byte sizes of enum discriminants
 * (`Expr`, `Item`, `Type`, `Pat`, `Lit`) via `mem::size_of`. There is
 * no Kotlin counterpart: object layout on the JVM, JS, and Native
 * targets is runtime-managed and not queryable, and the Kotlin sealed
 * class hierarchy does not expose a fixed in-memory representation.
 * Each test below carries an honest one-line comment naming the
 * specific Rust semantic that does not translate, rather than emitting
 * a fake simulation that tests a different invariant.
 */
class SizeTest {
    // Not ported: `mem::size_of::<Expr>()` has no Kotlin counterpart;
    // JVM/JS/Native object layout is runtime-managed, so the asserted
    // byte count (176 on 64-bit) cannot be measured or compared.
    @Test
    fun testExprSize() {
        // Not ported: Rust struct size/layout has no Kotlin counterpart
        // (JVM/JS/Native object layout is runtime-managed).
    }

    // Not ported: `mem::size_of::<Item>()` has no Kotlin counterpart;
    // JVM/JS/Native object layout is runtime-managed, so the asserted
    // byte count (352 on 64-bit) cannot be measured or compared.
    @Test
    fun testItemSize() {
        // Not ported: Rust struct size/layout has no Kotlin counterpart
        // (JVM/JS/Native object layout is runtime-managed).
    }

    // Not ported: `mem::size_of::<Type>()` has no Kotlin counterpart;
    // JVM/JS/Native object layout is runtime-managed, so the asserted
    // byte count (224 on 64-bit) cannot be measured or compared.
    @Test
    fun testTypeSize() {
        // Not ported: Rust struct size/layout has no Kotlin counterpart
        // (JVM/JS/Native object layout is runtime-managed).
    }

    // Not ported: `mem::size_of::<Pat>()` has no Kotlin counterpart;
    // JVM/JS/Native object layout is runtime-managed, so the asserted
    // byte count (184 on 64-bit) cannot be measured or compared.
    @Test
    fun testPatSize() {
        // Not ported: Rust struct size/layout has no Kotlin counterpart
        // (JVM/JS/Native object layout is runtime-managed).
    }

    // Not ported: `mem::size_of::<Lit>()` has no Kotlin counterpart;
    // JVM/JS/Native object layout is runtime-managed, so the asserted
    // byte count (24 on 64-bit) cannot be measured or compared.
    @Test
    fun testLitSize() {
        // Not ported: Rust struct size/layout has no Kotlin counterpart
        // (JVM/JS/Native object layout is runtime-managed).
    }
}
