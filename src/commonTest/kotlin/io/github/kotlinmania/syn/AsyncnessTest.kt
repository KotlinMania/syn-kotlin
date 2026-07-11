// port-lint: tests tests/test_asyncness.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AsyncnessTest {
    @Test
    fun testAsyncFn() {
        val item = parseStr(ItemParse::parse, "async fn process() {}").getOrThrow()
        val fn = assertIs<Item.Fn>(item)

        assertIs<Visibility.Inherited>(fn.vis)
        assertNotNull(fn.sig.asyncness)
        assertEquals("process", fn.sig.ident.toString())
        assertTrue(
            fn.sig.generics.params
                .isEmpty(),
        )
        assertIs<ReturnType.Default>(fn.sig.output)
        assertTrue(
            fn.block
                ?.stmts
                .orEmpty()
                .isEmpty(),
        )
    }

    @Test
    fun testAsyncClosure() {
        val expr = parseStr(ExprParse::parse, "async || {}").getOrThrow()
        val closure = assertIs<Expr.Closure>(expr)

        assertNotNull(closure.asyncness)
        assertIs<ReturnType.Default>(closure.output)
        val body = assertIs<Expr.BlockExpr>(closure.body)
        assertTrue(body.block.stmts.isEmpty())
    }
}
