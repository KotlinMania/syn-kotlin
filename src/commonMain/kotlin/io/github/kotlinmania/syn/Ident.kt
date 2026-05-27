// port-lint: source ident.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.syn.token.Crate
import io.github.kotlinmania.syn.token.Extern
import io.github.kotlinmania.syn.token.SelfType
import io.github.kotlinmania.syn.token.SelfValue
import io.github.kotlinmania.syn.token.Super
import io.github.kotlinmania.syn.token.Underscore
import kotlin.native.HiddenFromObjC

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

internal fun acceptAsIdent(ident: Ident): Boolean {
    val s = ident.toString()
    return when (s) {
        "_",
        "abstract", "as", "async", "await", "become", "box", "break",
        "const", "continue", "crate", "do", "dyn", "else", "enum",
        "extern", "false", "final", "fn", "for", "if", "impl", "in",
        "let", "loop", "macro", "match", "mod", "move", "mut",
        "override", "priv", "pub", "ref", "return", "Self", "self",
        "static", "struct", "super", "trait", "true", "try", "type",
        "typeof", "unsafe", "unsized", "use", "virtual", "where",
        "while", "yield" -> false
        else -> true
    }
}

@HiddenFromObjC
public object IdentParse : Parse<Ident> {
    override fun parse(input: ParseStream): SynResult<Ident> =
        input.step { cursor ->
            val (ident, rest) = cursor.ident()
                ?: return@step cursor.error("expected identifier").let { SynResult.failure(it) }
            if (acceptAsIdent(ident)) {
                SynResult.success(ident to rest)
            } else {
                SynResult.failure(cursor.error("expected identifier, found keyword `$ident`"))
            }
        }
}

@HiddenFromObjC
public object IdentPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return acceptAsIdent(ident)
    }

    override fun display(): String = "identifier"
}

public fun identFromSelfValue(token: SelfValue): Ident =
    Ident.new("self", token.span)

public fun identFromSelfType(token: SelfType): Ident =
    Ident.new("Self", token.span)

public fun identFromSuper(token: Super): Ident =
    Ident.new("super", token.span)

public fun identFromCrate(token: Crate): Ident =
    Ident.new("crate", token.span)

public fun identFromExtern(token: Extern): Ident =
    Ident.new("extern", token.span)

public fun identFromUnderscore(token: Underscore): Ident =
    Ident.new("_", token.span)
