package io.github.kotlinmania.syn

@kotlin.native.concurrent.ThreadLocal
private object NativeThreadBoundToken

internal actual fun currentThreadBoundToken(): Any =
    NativeThreadBoundToken
