// port-lint: tests tests/regression/issue1108.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertTrue

class RegressionIssue1108Test {
    @Test
    fun issue1108() {
        val parsed = runCatching { parseFile("impl<x<>>::x for") }
        assertTrue(parsed.isSuccess)
    }
}
