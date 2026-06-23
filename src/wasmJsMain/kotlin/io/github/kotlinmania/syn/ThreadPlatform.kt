package io.github.kotlinmania.syn

private object WasmJsThreadBoundToken

internal actual fun currentThreadBoundToken(): Any =
    WasmJsThreadBoundToken
