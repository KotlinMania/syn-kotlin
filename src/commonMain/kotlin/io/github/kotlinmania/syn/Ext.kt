// port-lint: source ext.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree

/**
 * Extension functions to provide parsing methods on foreign types.
 *
 * Defines IdentExt, TokenStreamExt, and PunctExt interfaces
 * each with a single implementation block. Top-level
 * extension functions achieve the same call-site shape without the interface
 * indirection.
 */

/**
 * Additional methods for [Ident] not provided by procmacro2.
 *
 * These methods sit on the `IdentExt` interface sealed inside the
 * syn library. Kotlin exposes them as extension functions on
 * [io.github.kotlinmania.procmacro2.Ident]; sealing semantics are
 * inapplicable.
 */

/**
 * Parses any identifier including keywords.
 *
 * This is useful when parsing macro input which allows keywords as
 * identifiers.
 *
 * # Example
 *
 * ```kotlin
 * import io.github.kotlinmania.syn.identParseAny
 *
 * // Parses input that looks like `name = NAME` where `NAME` can be any
 * // identifier.
 * //
 * // Examples:
 * //
 * // name = anything
 * // name = implementation
 * fun parseDsl(input: ParseStream): SynResult<Ident> = runCatching {
 * input.parse(KwName).getOrThrow()
 * input.parse(EqToken).getOrThrow()
 * val name = input.call(::identParseAny).getOrThrow()
 * name
 * }
 * ```
 */
public fun identParseAny(input: ParseStream): SynResult<Ident> =
    input.step { cursor ->
        val pair =
            cursor.ident()
                ?: return@step SynResult.failure(cursor.error("expected ident"))
        SynResult.success(pair)
    }

/**
 * Peek any identifier including keywords. Usage: `input.peek(IdentPeekAny)`.
 *
 * This is different from `input.peek(Ident)` which only returns true in the
 * case of an ident which is not a keyword.
 */
public object IdentPeekAny : Peek {
    override fun peek(cursor: Cursor): Boolean = cursor.ident() != null

    override fun display(): String = "identifier"
}

/**
 * Strips the raw marker `r#`, if any, from the beginning of an ident.
 *
 * - unraw(`x`) = `x`
 * - unraw(`move`) = `move`
 * - unraw(`r#move`) = `move`
 *
 * # Example
 *
 * In the case of interop with other languages like Python that have a
 * different set of keywords, we might come across macro input that
 * involves raw identifiers to refer to ordinary variables in the other
 * language with a name that happens to be a keyword.
 *
 * The function below appends an identifier from the caller's input onto a
 * fixed prefix. Without using `unraw()`, this would tend to produce invalid
 * identifiers like `__pyo3_get_r#move`.
 *
 * ```kotlin
 * fun identForGetter(variable: Ident): Ident {
 * val getter = "__pyo3_get_${variable.unraw()}"
 * return Ident.new(getter, Span.callSite())
 * }
 * ```
 */
public fun Ident.unraw(): Ident {
    val string = this.toString()
    return if (string.startsWith("r#")) {
        Ident.new(string.removePrefix("r#"), this.span())
    } else {
        Ident.new(string, this.span())
    }
}

/**
 * Appends a single [TokenTree] onto a [TokenStream].
 *
 * Declared on the `TokenStreamExt` interface; here it
 * is an extension function with the same call-site shape. Mirrors the
 * `append` extension provided in quote-kotlin.
 */
internal fun TokenStream.appendTokenTree(token: TokenTree) {
    this.extendTokenTrees(listOf(token))
}

/**
 * Constructs a [Punct] with the given character, spacing, and span.
 *
 * Declared on the `PunctExt` interface; here it is a
 * companion-equivalent helper on [Punct].
 */
internal fun punctNewSpanned(ch: Char, spacing: Spacing, span: Span): Punct {
    val punct = Punct(ch, spacing)
    punct.setSpan(span)
    return punct
}
