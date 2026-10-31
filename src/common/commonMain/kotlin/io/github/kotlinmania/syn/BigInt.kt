// port-lint: source bigint.rs
package io.github.kotlinmania.syn

// For implementing base10Digits() accessor on LitInt.
internal class BigInt internal constructor(
    internal val digits: MutableList<UByte>,
) {
    internal companion object {
        internal fun new(): BigInt = BigInt(mutableListOf())
    }

    override fun toString(): String {
        val repr = StringBuilder(digits.size)

        var hasNonzero = false
        for (digit in digits.asReversed()) {
            hasNonzero = hasNonzero || digit != 0u.toUByte()
            if (hasNonzero) {
                repr.append(('0'.code + digit.toInt()).toChar())
            }
        }

        if (repr.isEmpty()) {
            repr.append('0')
        }

        return repr.toString()
    }

    private fun reserveTwoDigits() {
        val len = digits.size
        val endsWithTwoZeros =
            len >= 2 && digits[len - 2] == 0u.toUByte() && digits[len - 1] == 0u.toUByte()
        val endsWithOneZero = len >= 1 && digits[len - 1] == 0u.toUByte()
        val desired =
            len + (if (!endsWithTwoZeros) 1 else 0) + (if (!endsWithOneZero) 1 else 0)
        while (digits.size < desired) {
            digits.add(0u)
        }
    }

    // Assumes increment < 16.
    internal fun addAssign(increment: UByte) {
        reserveTwoDigits()

        var rem = increment.toInt()
        var i = 0
        while (rem > 0) {
            val sum = digits[i].toInt() + rem
            if (sum > 255) {
                error("attempt to add with overflow")
            }
            digits[i] = (sum % 10).toUByte()
            rem = sum / 10
            i += 1
        }
    }

    // Assumes base <= 16.
    internal fun mulAssign(base: UByte) {
        reserveTwoDigits()

        var carry = 0
        val baseInt = base.toInt()
        for (i in digits.indices) {
            val scaled = digits[i].toInt() * baseInt
            if (scaled > 255) {
                error("attempt to multiply with overflow")
            }
            val prod = scaled + carry
            if (prod > 255) {
                error("attempt to add with overflow")
            }
            digits[i] = (prod % 10).toUByte()
            carry = prod / 10
        }
    }
}
