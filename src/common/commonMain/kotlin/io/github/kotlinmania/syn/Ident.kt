// port-lint: source ident.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.syn.token.Crate
import io.github.kotlinmania.syn.token.Extern
import io.github.kotlinmania.syn.token.SelfType
import io.github.kotlinmania.syn.token.SelfValue
import io.github.kotlinmania.syn.token.Super
import io.github.kotlinmania.syn.token.Underscore
import io.github.kotlinmania.unicodeident.isXidContinueCodePoint
import io.github.kotlinmania.unicodeident.isXidStartCodePoint

public fun Ident.copy(): Ident = cloneToken()

public fun Ident.clone(): Ident = copy()

public fun Ident(marker: TokenMarker): Ident =
    error("uninhabited TokenMarker: $marker")

public fun from(token: SelfValue): Ident = Ident.new("self", token.span.clone())

public fun from(token: SelfType): Ident = Ident.new("Self", token.span.clone())

public fun from(token: Super): Ident = Ident.new("super", token.span.clone())

public fun from(token: Crate): Ident = Ident.new("crate", token.span.clone())

public fun from(token: Extern): Ident = Ident.new("extern", token.span.clone())

public fun from(token: Underscore): Ident = Ident.new("_", token.span.clone())

public fun identFromSelfValue(token: SelfValue): Ident = from(token)

public fun identFromSelfType(token: SelfType): Ident = from(token)

public fun identFromSuper(token: Super): Ident = from(token)

public fun identFromCrate(token: Crate): Ident = from(token)

public fun identFromExtern(token: Extern): Ident = from(token)

public fun identFromUnderscore(token: Underscore): Ident = from(token)

internal fun xidOk(symbol: String): Boolean {
    if (symbol.isEmpty()) {
        error("called `Option::unwrap()` on a `None` value")
    }
    var pos = 0
    val firstChar = symbol[0]
    val firstCodePoint: Int
    if (firstChar.isHighSurrogate() && symbol.length > 1 && symbol[1].isLowSurrogate()) {
        firstCodePoint = ((firstChar.code - 0xD800) shl 10) + (symbol[1].code - 0xDC00) + 0x10000
        pos = 2
    } else if (firstChar.isSurrogate()) {
        return false
    } else {
        firstCodePoint = firstChar.code
        pos = 1
    }
    if (!(firstCodePoint == '_'.code || isXidStartCodePoint(firstCodePoint))) {
        return false
    }
    while (pos < symbol.length) {
        val ch = symbol[pos]
        val codePoint: Int
        if (ch.isHighSurrogate() && pos + 1 < symbol.length && symbol[pos + 1].isLowSurrogate()) {
            codePoint = ((ch.code - 0xD800) shl 10) + (symbol[pos + 1].code - 0xDC00) + 0x10000
            pos += 2
        } else if (ch.isSurrogate()) {
            return false
        } else {
            codePoint = ch.code
            pos += 1
        }
        if (!isXidContinueCodePoint(codePoint)) {
            return false
        }
    }
    return true
}

private fun acceptAsIdent(ident: Ident): Boolean =
    when (ident.toString()) {
        "_",
        // Based on https://doc.rust-lang.org/1.65.0/reference/keywords.html
        "abstract", "as", "async", "await", "become", "box", "break",
        "const", "continue", "crate", "do", "dyn", "else", "enum",
        "extern", "false", "final", "fn", "for", "if", "impl", "in",
        "let", "loop", "macro", "match", "mod", "move", "mut",
        "override", "priv", "pub", "ref", "return", "Self", "self",
        "static", "struct", "super", "trait", "true", "try", "type",
        "typeof", "unsafe", "unsized", "use", "virtual", "where",
        "while", "yield",
        -> false

        else -> true
    }

public object IdentParse {
    public fun parse(input: ParseStream): SynResult<Ident> =
        input.step { cursor ->
            val pair = cursor.ident()
            if (pair != null) {
                val (ident, rest) = pair
                if (acceptAsIdent(ident)) {
                    SynResult.success(ident to rest)
                } else {
                    SynResult.failure(cursor.error("expected identifier, found keyword `$ident`"))
                }
            } else {
                SynResult.failure(cursor.error("expected identifier"))
            }
        }
}

public object IdentPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val pair = cursor.ident() ?: return false
        return acceptAsIdent(pair.first)
    }

    override fun display(): String = "identifier"
}
