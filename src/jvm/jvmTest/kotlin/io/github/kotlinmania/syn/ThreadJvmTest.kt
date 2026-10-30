package io.github.kotlinmania.syn

import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ThreadJvmTest {
    @Test
    fun threadBoundHidesValueFromDifferentThread() {
        val bound = ThreadBound.new("value")
        var ran = false
        var observed: String? = "not read"
        var rendered: String? = null

        val worker =
            thread {
                observed = bound.get() as String?
                rendered = bound.toString()
                ran = true
            }
        worker.join()

        assertTrue(ran)
        assertNull(observed)
        assertEquals("unknown", rendered)
        assertEquals("value", bound.get())
    }
}
