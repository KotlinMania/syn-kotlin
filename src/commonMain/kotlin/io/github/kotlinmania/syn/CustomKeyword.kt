// port-lint: source custom_keyword.rs
package io.github.kotlinmania.syn

/**
 * Support for defining custom keywords that can be used as tokens in parsing.
 *
 * Use [customKeyword] to create a [Peek] and concrete parser pair for any identifier
 * string that is not already a built-in keyword.
 */
public fun customKeyword(name: String): Pair<Peek, CustomKeywordParse> {
    val peek = CustomKeywordPeek(name)
    val parse = CustomKeywordParse(name)
    return peek to parse
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
