package io.github.kotlinmania.syn

private object WasmWasiThreadBoundToken

internal actual fun currentThreadBoundToken(): Any =
    WasmWasiThreadBoundToken
