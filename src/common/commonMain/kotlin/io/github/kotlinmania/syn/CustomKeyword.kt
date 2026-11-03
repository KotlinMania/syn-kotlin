// port-lint: source custom_keyword.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident

private const val CUSTOM_KEYWORD_TEMPLATE: String =
    "public class \$ident(public var span: Span = Span.callSite()); " +
        "implParseForCustomKeyword(\$ident); implToTokensForCustomKeyword(\$ident); " +
        "implCloneForCustomKeyword(\$ident); implExtraTraitsForCustomKeyword(\$ident);"

private const val IMPL_PARSE_FOR_CUSTOM_KEYWORD_TEMPLATE: String =
    "class CustomTokenFor\$ident : Peek { fun peek(cursor: Cursor): Boolean = cursor.ident()?.first?.toString() == \"\$ident\" }"

private const val IMPL_TO_TOKENS_FOR_CUSTOM_KEYWORD_TEMPLATE: String =
    "class ToTokensFor\$ident : ToTokens { fun toTokens(tokens: TokenStream) { tokens.append(Ident.new(\"\$ident\", span)) } }"

private const val IMPL_CLONE_FOR_CUSTOM_KEYWORD_TEMPLATE: String =
    "fun \$ident.clone(): \$ident = \$ident(span = span)"

private const val IMPL_EXTRA_TRAITS_FOR_CUSTOM_KEYWORD_TEMPLATE: String =
    "fun \$ident.debugString(): String = \"Keyword [\$ident]\""

/**
 * Define a type that supports parsing and printing a given identifier as if it
 * were a keyword.
 *
 * # Usage
 *
 * As a convention, it is recommended that this helper be invoked within a
 * module called `kw` or `keyword` and that the resulting parser be invoked
 * with a `kw.` or `keyword.` prefix.
 *
 * The generated syntax tree node supports the following operations just like
 * any built-in keyword token.
 *
 * - Peeking — `input.peek(kw.whatever)`
 *
 * - Parsing — `input.parse(kw.whatever::parse)`
 *
 * - Printing — `quote { ... }`
 *
 * - Construction from a [io.github.kotlinmania.procmacro2.Span] — `val whateverToken = kw.whatever(sp)`
 *
 * - Field access to its span — `val sp = whateverToken.span`
 *
 * # Example
 *
 * This example parses input that looks like `bool = true` or `str = "value"`.
 * The key must be either the identifier `bool` or the identifier `str`. If
 * `bool`, the value may be either `true` or `false`. If `str`, the value may
 * be any string literal.
 *
 * ```kotlin
 * sealed class Argument {
 *     public data class Bool(public var boolToken: Ident, public var eqToken: Eq, public var value: LitBool) : Argument()
 *     public data class Str(public var strToken: Ident, public var eqToken: Eq, public var value: LitStr) : Argument()
 *
 *     public companion object {
 *         public fun parse(input: ParseStream): SynResult<Argument> {
 *             val lookahead = input.lookahead1()
 *             return if (lookahead.peek(boolPeek)) {
 *                 SynResult.success(
 *                     Argument.Bool(
 *                         boolToken = boolParse.parse(input).getOrElse { return SynResult.failure(it) },
 *                         eqToken = Eq.parse(input).getOrElse { return SynResult.failure(it) },
 *                         value = LitBool.parse(input).getOrElse { return SynResult.failure(it) },
 *                     ),
 *                 )
 *             } else if (lookahead.peek(strPeek)) {
 *                 SynResult.success(
 *                     Argument.Str(
 *                         strToken = strParse.parse(input).getOrElse { return SynResult.failure(it) },
 *                         eqToken = Eq.parse(input).getOrElse { return SynResult.failure(it) },
 *                         value = LitStr.parse(input).getOrElse { return SynResult.failure(it) },
 *                     ),
 *                 )
 *             } else {
 *                 SynResult.failure(lookahead.error())
 *             }
 *         }
 *     }
 * }
 * ```
 */
public fun customKeyword(name: String): Pair<Peek, CustomKeywordParse> {
    val peek = CustomKeywordPeek(name)
    val parse = CustomKeywordParse(name)
    return peek to parse
}

internal fun customKeywordTemplate(ident: String = ""): String =
    CUSTOM_KEYWORD_TEMPLATE.replace("\$ident", ident)

internal fun implParseForCustomKeyword(ident: String = ""): String =
    if (CFG_FEATURE_PARSING) {
        IMPL_PARSE_FOR_CUSTOM_KEYWORD_TEMPLATE.replace("\$ident", ident)
    } else {
        ""
    }

internal fun implToTokensForCustomKeyword(ident: String = ""): String =
    if (CFG_FEATURE_PRINTING) {
        IMPL_TO_TOKENS_FOR_CUSTOM_KEYWORD_TEMPLATE.replace("\$ident", ident)
    } else {
        ""
    }

internal fun implCloneForCustomKeyword(ident: String = ""): String =
    if (CFG_FEATURE_CLONE_IMPLS) {
        IMPL_CLONE_FOR_CUSTOM_KEYWORD_TEMPLATE.replace("\$ident", ident)
    } else {
        ""
    }

internal fun implExtraTraitsForCustomKeyword(ident: String = ""): String =
    if (CFG_FEATURE_EXTRA_TRAITS) {
        IMPL_EXTRA_TRAITS_FOR_CUSTOM_KEYWORD_TEMPLATE.replace("\$ident", ident)
    } else {
        ""
    }

/** Peek implementation for a custom keyword. */
public class CustomKeywordPeek(
    private val name: String,
) : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == name
    }

    override fun display(): String = "`$name`"
}

/** Parse implementation for a custom keyword. */
public class CustomKeywordParse(
    private val name: String,
) {
    public fun parse(input: ParseStream): SynResult<Ident> =
        input.step { cursor ->
            val (ident, rest) = cursor.ident() ?: return@step SynResult.failure(cursor.error("expected `$name`"))
            if (ident.toString() != name) return@step SynResult.failure(cursor.error("expected `$name`"))
            SynResult.success(Ident.new(name, ident.span()) to rest)
        }
}

/**
 * Reserved keywords in Rust that cannot be used as identifiers.
 *
 * This set includes all strict and reserved keywords from the Rust
 * reference. When parsing macro input, these may appear as identifiers
 * and should be accepted by [identParseAny] but rejected by [IdentParse].
 */
public val RESERVED_KEYWORDS: Set<String> =
    setOf(
        "as",
        "break",
        "const",
        "continue",
        "crate",
        "else",
        "enum",
        "extern",
        "false",
        "fn",
        "for",
        "if",
        "impl",
        "in",
        "let",
        "loop",
        "match",
        "mod",
        "move",
        "mut",
        "pub",
        "ref",
        "return",
        "self",
        "Self",
        "static",
        "struct",
        "super",
        "trait",
        "true",
        "type",
        "unsafe",
        "use",
        "where",
        "while",
        // Reserved keywords
        "abstract",
        "async",
        "await",
        "become",
        "box",
        "do",
        "final",
        "macro",
        "override",
        "priv",
        "try",
        "typeof",
        "unsized",
        "virtual",
        "yield",
    )

/**
 * Weak keywords that have special meaning in some contexts but can be
 * used as identifiers in others.
 */
public val WEAK_KEYWORDS: Set<String> =
    setOf(
        "_",
        "dyn",
        "macro_rules",
        "union",
    )
