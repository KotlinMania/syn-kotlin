// port-lint: source whitespace.rs
package io.github.kotlinmania.syn

internal fun skip(s: String): String {
    var pos = 0
    skipLoop@ while (pos < s.length) {
        val ch = s[pos]
        if (ch == '/') {
            if (s.startsWith("//", pos) &&
                (!s.startsWith("///", pos) || s.startsWith("////", pos)) &&
                !s.startsWith("//!", pos)
            ) {
                val nl = s.indexOf('\n', pos)
                if (nl >= 0) {
                    pos = nl + 1
                    continue
                } else {
                    return ""
                }
            } else if (s.startsWith("/**/", pos)) {
                pos += 4
                continue
            } else if (s.startsWith("/*", pos) &&
                (!s.startsWith("/**", pos) || s.startsWith("/***", pos)) &&
                !s.startsWith("/*!", pos)
            ) {
                var depth = 0
                var i = pos
                val upper = s.length - 1
                while (i < upper) {
                    if (s[i] == '/' && s[i + 1] == '*') {
                        depth += 1
                        i += 1
                    } else if (s[i] == '*' && s[i + 1] == '/') {
                        depth -= 1
                        if (depth == 0) {
                            pos = i + 2
                            continue@skipLoop
                        }
                        i += 1
                    }
                    i += 1
                }
                return s.substring(pos)
            }
        }
        val code = ch.code
        when {
            code == 0x20 || code in 0x09..0x0D -> {
                pos += 1
                continue
            }
            code <= 0x7F -> {
                return s.substring(pos)
            }
            else -> {
                val codePoint: Int
                val charLen: Int
                if (ch.isHighSurrogate() && pos + 1 < s.length && s[pos + 1].isLowSurrogate()) {
                    codePoint = ((code - 0xD800) shl 10) + (s[pos + 1].code - 0xDC00) + 0x10000
                    charLen = 2
                } else {
                    codePoint = code
                    charLen = 1
                }
                if (isWhitespace(codePoint)) {
                    pos += charLen
                    continue
                }
                return s.substring(pos)
            }
        }
    }
    return s.substring(pos)
}

private fun isWhitespace(codePoint: Int): Boolean =
    // Left-to-right mark and right-to-left mark are treated as whitespace alongside Unicode whitespace.
    codePoint in 0x0009..0x000D ||
        codePoint == 0x0020 ||
        codePoint == 0x0085 ||
        codePoint == 0x00A0 ||
        codePoint == 0x1680 ||
        codePoint in 0x2000..0x200A ||
        codePoint == 0x200E ||
        codePoint == 0x200F ||
        codePoint == 0x2028 ||
        codePoint == 0x2029 ||
        codePoint == 0x202F ||
        codePoint == 0x205F ||
        codePoint == 0x3000
