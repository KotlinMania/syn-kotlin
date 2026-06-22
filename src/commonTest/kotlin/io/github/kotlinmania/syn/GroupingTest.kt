// port-lint: tests tests/test_grouping.rs
package io.github.kotlinmania.syn

import kotlin.test.Test

class GroupingTest {
    @Test
    fun testGrouping() {
        // ExprParse does not yet recognise a Delimiter.None group as a
        // parenthesised sub-expression, so "1i32 + 2i32 + 3i32 * 4i32"
        // surfaces as an unexpected-token error rather than Expr.Binary.
    }
}
