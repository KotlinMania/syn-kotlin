package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals

/** Observations from original Rust Syn, retained with the generator receipt. */
class GeneratedClassifyTest {
    @Test
    fun adoptedClassifiersMatchRustObservations() {
        val cases = listOf(
            "1" to "true|true",
            "f()" to "true|true",
            "m!()" to "true|true",
            "m!{}" to "false|true",
            "m![]" to "true|true",
            "{1}" to "false|false",
            "unsafe {1}" to "false|false",
            "if true {1} else {2}" to "false|false",
            "match x { _ => 1 }" to "false|false",
            "while false {}" to "false|false",
            "loop {}" to "false|false",
            "for x in xs {}" to "false|false",
            "async {}" to "true|true",
            "const {1}" to "false|false",
            "try {}" to "false|false",
            "x.await" to "true|true",
            "return 1" to "true|true",
            "'label: loop {}" to "false|false",
            "{ /* kept */ 1 }" to "false|false",
        )
        for ((source, expected) in cases) {
            val expr = parseStr(::parseExpr, source).getOrThrow()
            assertEquals(expected, "${requires_semi_to_be_stmt(expr)}|${requires_comma_to_be_match_arm(expr)}", source)
        }
    }
}
