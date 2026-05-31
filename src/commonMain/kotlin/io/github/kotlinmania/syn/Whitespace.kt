// port-lint: source whitespace.rs
package io.github.kotlinmania.syn

/**
 * Skips leading whitespace and comments in the parsed language from [input]. Returns the
 * remaining suffix of the input after the skip.
 */
internal fun skipWhitespace(input: String): String {
    var s = input
    skip@ while (s.isNotEmpty()) {
        val byte = s[0]
        if (byte == '/') {
            if (s.startsWith("//") &&
                (!s.startsWith("///") || s.startsWith("////")) &&
                !s.startsWith("//!")
            ) {
                val i = s.indexOf('\n')
                if (i >= 0) {
                    s = s.substring(i + 1)
                    continue
                } else {
                    return ""
                }
            } else if (s.startsWith("/**/")) {
                s = s.substring(4)
                continue
            } else if (s.startsWith("/*") &&
                (!s.startsWith("/**") || s.startsWith("/***")) &&
                !s.startsWith("/*!")
            ) {
                var depth = 0
                val len = s.length
                var i = 0
                val upper = len - 1
                while (i < upper) {
                    if (s[i] == '/' && s[i + 1] == '*') {
                        depth += 1
                        i += 1
                    } else if (s[i] == '*' && s[i + 1] == '/') {
                        depth -= 1
                        if (depth == 0) {
                            s = s.substring(i + 2)
                            continue@skip
                        }
                        i += 1
                    }
                    i += 1
                }
                return s
            }
        }
        when {
            byte == ' ' || (byte.code in 0x09..0x0D) -> {
                s = s.substring(1)
                continue
            }
            byte.code <= 0x7F -> {
                // fall through to return
            }
            else -> {
                if (isWhitespaceChar(byte)) {
                    s = s.substring(byte.toString().length)
                    continue
                }
            }
        }
        return s
    }
    return s
}

private fun isWhitespaceChar(ch: Char): Boolean {
    return ch.isWhitespace() || ch == '\u200E' || ch == '\u200F'
}

/**
 * Returns whether the character is considered whitespace.
 * Includes left-to-right mark and right-to-left mark.
 */
public fun charIsWhitespace(ch: Char): Boolean = isWhitespaceChar(ch)

/**
 * Skips leading whitespace and comments in the parsed language.
 * Public wrapper for the internal [skipWhitespace] function.
 */
public fun skip(input: String): String =
    skipWhitespace(input)

/**
 * Returns whether the character is considered whitespace for parsing purposes.
 * Public wrapper for the internal [charIsWhitespace] function (which already
 * delegates to [isWhitespaceChar]).
 */
public fun isWhitespace(ch: Char): Boolean =
    charIsWhitespace(ch)


/**
 * Skips all whitespace and comments in the parse stream, advancing the cursor.
 * Returns the number of tokens consumed (always zero since whitespace is
 * not tokenized separately).
 */
internal fun ParseStream.skipWhitespace(): Int {
    // In Kotlin, whitespace is not tokenized as separate tokens;
    // the lexer already handles it. This is a no-op.
    return 0
}
