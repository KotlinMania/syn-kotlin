@file:kotlin.jvm.JvmName("ExtKt")

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

/** Existing Kotlin call syntax selects the generated identifier implementation. */
public fun Ident.Companion.parseAny(input: ParseStream): SynResult<Ident> =
    SynExtImpl0.parse_any(input)

/** Peek any identifier, including keywords. */
public val Ident.Companion.peekAny: Peek
    get() = SynExtPeekAdapter

/** Existing parser callback entry point selects the generated implementation. */
public fun identParseAny(input: ParseStream): SynResult<Ident> = SynExtImpl0.parse_any(input)

/** Existing lookahead entry point selects the generated associated token witness. */
public val IdentPeekAny: Peek
    get() = SynExtPeekAdapter

/** Remove a raw marker and return an independent token, as the source requires. */
public fun Ident.unraw(): Ident = SynExtImpl0(this).unraw()

internal fun TokenStream.appendTokenTree(token: TokenTree) = SynExtImpl4(this).append(token)

internal fun punctNewSpanned(ch: Char, spacing: Spacing, span: Span): Punct =
    SynExtImpl5.new_spanned(SynExtChar(ch.code), spacing, span)
