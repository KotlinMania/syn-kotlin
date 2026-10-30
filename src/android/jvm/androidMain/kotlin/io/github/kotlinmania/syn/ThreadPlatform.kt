package io.github.kotlinmania.syn

internal actual fun currentThreadBoundToken(): Any =
    Thread.currentThread()
