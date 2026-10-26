package io.github.kotlinmania.syn.rust

import io.github.kotlinmania.procmacro2.Ident

/** Adapter to proc_macro2's lossless identifier spelling and separate fmt writes. */
public fun rustIdentDisplay(value: Ident, formatter: RustFormatter): RustFmtResult<Unit> {
    val spelling = value.toString()
    if (spelling.startsWith("r#")) {
        val prefix = formatter.writeStr("r#")
        if (prefix is RustFmtResult.Failure) return prefix
        return formatter.writeStr(spelling.substring(2))
    }
    return formatter.writeStr(spelling)
}

/** Rust compares identifier spellings as unsigned UTF-8 bytes, independently of span. */
public fun rustIdentCompare(left: Ident, right: Ident): Int {
    val a = left.toString().encodeToByteArray(throwOnInvalidSequence = true)
    val b = right.toString().encodeToByteArray(throwOnInvalidSequence = true)
    for (i in 0 until minOf(a.size, b.size)) {
        val order = (a[i].toInt() and 255).compareTo(b[i].toInt() and 255)
        if (order != 0) return order
    }
    return a.size.compareTo(b.size)
}

/** proc_macro2::Ident hashes its spelling through the caller's write_str override. */
public fun rustIdentHash(value: Ident, state: RustStringHasher) {
    state.writeStr(value.toString())
}
