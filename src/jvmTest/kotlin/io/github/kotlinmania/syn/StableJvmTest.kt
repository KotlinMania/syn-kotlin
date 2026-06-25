// port-lint: tests zzz_stable.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertTrue

private const val NOTICE_MESSAGE =
    "‖\n" +
        "‖   WARNING:\n" +
        "‖   This is not a nightly compiler so not all tests were able to\n" +
        "‖   run. Syn includes tests that compare Syn's parser against the\n" +
        "‖   compiler's parser, which requires access to unstable librustc\n" +
        "‖   data structures and a nightly compiler.\n" +
        "‖\n"

class StableJvmTest {
    @Test
    fun notice() {
        val header = "WARNING"
        val indexOfHeader = NOTICE_MESSAGE.indexOf(header)
        assertTrue(indexOfHeader >= 0)
        val before = NOTICE_MESSAGE.substring(0, indexOfHeader)
        val after = NOTICE_MESSAGE.substring(indexOfHeader + header.length)

        System.err.print(before)
        System.err.print(header)
        System.err.print(after)
        System.err.flush()
    }
}
