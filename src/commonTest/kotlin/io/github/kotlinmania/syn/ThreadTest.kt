package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class ThreadTest {
    @Test
    fun threadBoundCloneCopiesWrapper() {
        val original = ThreadBound.new("value")
        val cloned = original.clone()

        assertNotSame(original, cloned)
        assertEquals("value", cloned.get())
        assertEquals(original, cloned)
    }
}
