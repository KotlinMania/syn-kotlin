package io.github.kotlinmania.syn

private object JsThreadBoundToken

internal actual fun currentThreadBoundToken(): Any =
    JsThreadBoundToken
