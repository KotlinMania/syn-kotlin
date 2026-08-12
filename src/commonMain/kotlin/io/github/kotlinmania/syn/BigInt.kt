// port-lint: source bigint.rs
package io.github.kotlinmania.syn

/** Decimal accumulator used by integer-literal parsing. */
public class BigInt private constructor(
    digits: List<Int> = emptyList(),
) {
    private val digits: MutableList<Int> = digits.toMutableList()

    public companion object {
        public fun new(): BigInt = BigInt()
    }

    override fun toString(): String {
        val repr = StringBuilder(digits.size)
        var hasNonzero = false
        for (index in digits.indices.reversed()) {
            val digit = digits[index]
            hasNonzero = hasNonzero || digit != 0
            if (hasNonzero) {
                repr.append(('0'.code + digit).toChar())
            }
        }
        if (repr.isEmpty()) {
            repr.append('0')
        }
        return repr.toString()
    }

    private fun reserveTwoDigits() {
        var desired = digits.size
        if (!digits.endsWithZeros(2)) {
            desired += 1
        }
        if (!digits.endsWithZeros(1)) {
            desired += 1
        }
        while (digits.size < desired) {
            digits += 0
        }
    }

    public operator fun plusAssign(increment: Int) {
        require(increment in 0..15) { "increment must be less than 16" }
        reserveTwoDigits()

        var carry = increment
        var index = 0
        while (carry > 0) {
            val sum = digits[index] + carry
            digits[index] = sum % 10
            carry = sum / 10
            index += 1
        }
    }

    public fun addAssign(increment: Int) {
        plusAssign(increment)
    }

    public operator fun timesAssign(base: Int) {
        require(base in 0..16) { "base must be at most 16" }
        reserveTwoDigits()

        var carry = 0
        for (index in digits.indices) {
            val product = digits[index] * base + carry
            digits[index] = product % 10
            carry = product / 10
        }
    }

    public fun mulAssign(base: Int) {
        timesAssign(base)
    }
}

private fun List<Int>.endsWithZeros(count: Int): Boolean {
    if (size < count) {
        return false
    }
    for (index in size - count until size) {
        if (this[index] != 0) {
            return false
        }
    }
    return true
}
