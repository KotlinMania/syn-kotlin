// port-lint: tests src/bigint.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GeneratedBigIntTest {
    @Test
    fun decimalCarryAndReturnedTextUseIndependentOwnedStorage() {
        val value = BigInt.new()
        assertEquals("0", value.toString())
        repeat(4) {
            value.mulAssign(16u)
            value.addAssign(15u)
        }
        val previous = value.toString()
        assertEquals("65535", previous)
        value.mulAssign(0u)
        assertEquals("0", value.toString())
        assertEquals("65535", previous)
    }

    @Test
    fun literalParserUsesGeneratedArithmeticBeyondMachineIntegerWidth() {
        val cases =
            listOf(
                "0xffffffffffffffffffffffffffffffff" to "340282366920938463463374607431768211455",
                "0b1111111111111111111111111111111111111111111111111111111111111111" to
                    "18446744073709551615",
                "0o37777777777" to "4294967295",
                "123456789012345678901234567890" to "123456789012345678901234567890",
                "0x_00_7f" to "127",
            )
        for ((source, expected) in cases) {
            val literal = LitInt.new(source, "u128", Span.callSite())
            assertEquals(expected, literal.base10Digits(), source)
            assertEquals("u128", literal.suffix(), source)
        }
    }

    @Test
    fun checkedByteFailurePreservesThePriorDecimalValue() {
        val value = BigInt.new()
        value.addAssign(9u)
        val error =
            assertFailsWith<IllegalStateException> { value.mulAssign(255u) }
        assertEquals("attempt to multiply with overflow", error.message)
        assertEquals("9", value.toString())
        value.addAssign(1u)
        assertEquals("10", value.toString())
    }
}
