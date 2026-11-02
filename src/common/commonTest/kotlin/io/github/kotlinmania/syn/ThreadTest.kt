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
        val clonedValue: String? = cloned.get()
        assertEquals("value", clonedValue)
        val number: Int? = ThreadBound.new(7).get()
        assertEquals(7, number)
    }
}
