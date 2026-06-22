// port-lint: tests tests/test_asyncness.rs
package io.github.kotlinmania.syn

import kotlin.test.Test

class AsyncnessTest {
    // Not ported: requires ItemParse (parsing "async fn process() {}" as Item.Fn)
    // and ExprParse support for closures (parsing "async || {}" as Expr.Closure);
    // neither parser is implemented in the Kotlin port yet.
    @Test
    fun testAsyncFn() {
    }

    @Test
    fun testAsyncClosure() {
    }
}
