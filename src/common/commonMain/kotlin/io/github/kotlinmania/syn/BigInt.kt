// port-lint: source bigint.rs
@file:Suppress("ClassNaming")

package io.github.kotlinmania.syn

// Generated from Syn AST; exact original retained in input.rs.
// Rust use resolved to typed standard-library adapter.
// Rust use resolved to typed standard-library adapter.
// Rust use resolved to typed standard-library adapter.

// For implementing base10_digits() accessor on LitInt.
internal class `BigInt`
internal constructor(internal var `digits`: kotlin.collections.MutableList<kotlin.UByte>) {
    companion object
}

internal fun `BigInt`.Companion.`new`(): `BigInt` = kotlin.run {
    val __rtkBigIntValue0: kotlin.collections.MutableList<kotlin.UByte> =
        kotlin.collections.mutableListOf<kotlin.UByte>()
    `BigInt`(__rtkBigIntValue0)
}

internal fun `BigInt`.`to_string`(): kotlin.text.StringBuilder = kotlin.run {
    var `repr`: kotlin.text.StringBuilder =
        __rtkBigIntStringWithCapacity(((this).`digits`).size.toULong())
    var `has_nonzero`: kotlin.Boolean = false
    kotlin.run {
        val __rtkBigIntValue0 = ((this).`digits`).asReversed()
        for (`digit` in __rtkBigIntValue0) kotlin.run {
            kotlin.run {
                val __rtkBigIntValue2: kotlin.Boolean = (`digit` != 0u.toUByte())
                `has_nonzero` = (`has_nonzero`).or(__rtkBigIntValue2)
            }
            kotlin.run {
                if (`has_nonzero`)
                    kotlin.run {
                        kotlin.run {
                            (`repr`).append(
                                (__rtkBigIntChar(
                                        ((__rtkBigIntAddU8(`digit`, 48u.toUByte()))).toInt(),
                                    ))
                                    .scalarString(),
                            )
                            kotlin.Unit
                        }
                    }
            }
        }
    }
    kotlin.run {
        if ((`repr`).isEmpty())
            kotlin.run {
                kotlin.run {
                    (`repr`).append((__rtkBigIntChar(48)).scalarString())
                    kotlin.Unit
                }
            }
    }
    `repr`
}

private fun `BigInt`.`reserve_two_digits`(): kotlin.Unit = kotlin.run {
    val `len`: kotlin.ULong = ((this).`digits`).size.toULong()
    val `desired`: kotlin.ULong =
        __rtkBigIntAddUsize(
            __rtkBigIntAddUsize(
                `len`,
                (if (
                    (!__rtkBigIntByteVectorEndsWith(
                        (this).`digits`,
                        kotlin.collections.listOf<kotlin.UByte>(0u.toUByte(), 0u.toUByte()),
                    ))
                )
                    1uL
                else 0uL),
            ),
            (if (
                (!__rtkBigIntByteVectorEndsWith(
                    (this).`digits`,
                    kotlin.collections.listOf<kotlin.UByte>(0u.toUByte()),
                ))
            )
                1uL
            else 0uL),
        )
    __rtkBigIntByteVectorResize((this).`digits`, `desired`, 0u.toUByte())
}

// Assumes increment <16.
internal fun `BigInt`.`__rtkBigIntAddAssign_add_assign`(`increment`: kotlin.UByte): kotlin.Unit =
    kotlin.run {
        var `increment__binding0`: kotlin.UByte = `increment`
        kotlin.run {
            (this).`reserve_two_digits`()
            var `i`: kotlin.ULong = 0uL
            kotlin.run {
                while ((`increment__binding0` > 0u.toUByte())) kotlin.run {
                    val `sum`: kotlin.UByte =
                        __rtkBigIntAddU8(
                            __rtkBigIntByteVectorGet((this).`digits`, `i`),
                            `increment__binding0`,
                        )
                    kotlin.run {
                        val __rtkBigIntValue1: kotlin.UByte = __rtkBigIntRemU8(`sum`, 10u.toUByte())
                        ((this).`digits`)[__rtkBigIntByteVectorIndex((this).`digits`, `i`)] =
                            __rtkBigIntValue1
                    }
                    kotlin.run {
                        val __rtkBigIntValue2: kotlin.UByte = __rtkBigIntDivU8(`sum`, 10u.toUByte())
                        `increment__binding0` = __rtkBigIntValue2
                    }
                    kotlin.run {
                        val __rtkBigIntValue3: kotlin.ULong = 1uL
                        `i` = __rtkBigIntAddUsize(`i`, __rtkBigIntValue3)
                    }
                }
            }
        }
    }

// Assumes base <=16.
internal fun `BigInt`.`__rtkBigIntMulAssign_mul_assign`(`base`: kotlin.UByte): kotlin.Unit =
    kotlin.run {
        (this).`reserve_two_digits`()
        var `carry`: kotlin.UByte = 0u.toUByte()
        kotlin.run {
            val __rtkBigIntValue0 = (this).`digits`
            for (__rtkBigIntValue1 in __rtkBigIntValue0.indices) kotlin.run {
                val `prod`: kotlin.UByte =
                    __rtkBigIntAddU8(
                        __rtkBigIntMulU8(__rtkBigIntValue0[__rtkBigIntValue1], `base`),
                        `carry`,
                    )
                kotlin.run {
                    val __rtkBigIntValue2: kotlin.UByte = __rtkBigIntRemU8(`prod`, 10u.toUByte())
                    __rtkBigIntValue0[__rtkBigIntValue1] = __rtkBigIntValue2
                }
                kotlin.run {
                    val __rtkBigIntValue3: kotlin.UByte = __rtkBigIntDivU8(`prod`, 10u.toUByte())
                    `carry` = __rtkBigIntValue3
                }
            }
        }
    }

internal class __rtkBigIntPanic(message: String) : kotlin.RuntimeException(message)

private fun __rtkBigIntAddUsize(left: ULong, right: ULong): ULong {
    if (right > ULong.MAX_VALUE - left) throw __rtkBigIntPanic("attempt to add with overflow")
    return left + right
}

internal data class __rtkBigIntChar(val codePoint: Int) {
    init {
        require(codePoint in 0..0x10ffff && codePoint !in 0xd800..0xdfff)
    }

    fun lenUtf8(): ULong =
        scalarString().encodeToByteArray(throwOnInvalidSequence = true).size.toULong()

    fun scalarString(): String =
        if (codePoint < 0x10000) codePoint.toChar().toString()
        else
            (0xd800 + ((codePoint - 0x10000) shr 10)).toChar().toString() +
                (0xdc00 + ((codePoint - 0x10000) and 1023)).toChar()
}

private fun __rtkBigIntAddU8(left: kotlin.UByte, right: kotlin.UByte): kotlin.UByte {

    val value = left.toInt() + right.toInt()
    if (value > 255) throw __rtkBigIntPanic("attempt to add with overflow")
    return value.toUByte()
}

private fun __rtkBigIntRemU8(left: kotlin.UByte, right: kotlin.UByte): kotlin.UByte {
    if (right == 0u.toUByte())
        throw __rtkBigIntPanic("attempt to calculate the remainder with a divisor of zero")
    val value = left.toInt() % right.toInt()

    return value.toUByte()
}

private fun __rtkBigIntDivU8(left: kotlin.UByte, right: kotlin.UByte): kotlin.UByte {
    if (right == 0u.toUByte()) throw __rtkBigIntPanic("attempt to divide by zero")
    val value = left.toInt() / right.toInt()

    return value.toUByte()
}

private fun __rtkBigIntMulU8(left: kotlin.UByte, right: kotlin.UByte): kotlin.UByte {

    val value = left.toInt() * right.toInt()
    if (value > 255) throw __rtkBigIntPanic("attempt to multiply with overflow")
    return value.toUByte()
}

internal class __rtkBigIntUnsupportedTargetValue(message: String) : kotlin.RuntimeException(message)

private fun __rtkBigIntStringWithCapacity(capacity: ULong): kotlin.text.StringBuilder {
    if (capacity > Long.MAX_VALUE.toULong()) throw __rtkBigIntPanic("capacity overflow")
    if (capacity > Int.MAX_VALUE.toULong())
        throw __rtkBigIntUnsupportedTargetValue("String capacity exceeds Kotlin Int storage domain")
    return kotlin.text.StringBuilder(capacity.toInt())
}

private fun __rtkBigIntByteVectorIndex(vector: List<UByte>, index: ULong): Int {
    if (index >= vector.size.toULong())
        throw __rtkBigIntPanic(
            "index out of bounds: the len is " + vector.size + " but the index is " + index,
        )
    return index.toInt()
}

private fun __rtkBigIntByteVectorGet(vector: List<UByte>, index: ULong): UByte =
    vector[__rtkBigIntByteVectorIndex(vector, index)]

private fun __rtkBigIntByteVectorEndsWith(vector: List<UByte>, suffix: List<UByte>): Boolean =
    vector.size >= suffix.size && vector.subList(vector.size - suffix.size, vector.size) == suffix

private fun __rtkBigIntByteVectorResize(vector: MutableList<UByte>, length: ULong, fill: UByte) {
    if (length > Long.MAX_VALUE.toULong()) throw __rtkBigIntPanic("capacity overflow")
    if (length > Int.MAX_VALUE.toULong())
        throw __rtkBigIntUnsupportedTargetValue("Vec length exceeds Kotlin Int storage domain")
    val size = length.toInt()
    if (size < vector.size) vector.subList(size, vector.size).clear()
    else while (vector.size < size) vector.add(fill)
}
