// port-lint: source ident.rs
package io.github.kotlinmania.syn

public typealias Ident = io.github.kotlinmania.procmacro2.Ident

public fun Ident.copy(): Ident =
    Ident.new(toString(), span())

internal fun xidOk(symbol: String): Boolean {
    val first = symbol.first()
    if (first != '_' && !isXidStart(first)) {
        return false
    }
    for (ch in symbol.drop(1)) {
        if (!isXidContinue(ch)) {
            return false
        }
    }
    return true
}

private fun isXidStart(ch: Char): Boolean =
    ch == '_' || ch.isLetter()

private fun isXidContinue(ch: Char): Boolean =
    isXidStart(ch) || ch.isDigit()
