package io.github.kotlinmania.syn.rust

/**
 * The caller-dispatched part of core::hash::Hasher used by Hash for str.
 * This is a capability contract, not a hash algorithm or the full Hasher API.
 * Overrides retain their write boundaries, state, and exceptional exits.
 */
public interface RustStringHasher {
    public fun write(bytes: ByteArray)
    public fun writeU8(value: UByte) { write(byteArrayOf(value.toByte())) }
    public fun writeStr(value: String) {
        write(value.encodeToByteArray(throwOnInvalidSequence = true))
        writeU8(255u)
    }
}
