package io.github.kotlinmania.syn

private object NativeThreadBoundToken

internal actual fun currentThreadBoundToken(): Any =
    NativeThreadBoundToken
