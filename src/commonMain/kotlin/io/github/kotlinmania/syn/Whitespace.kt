// port-lint: source whitespace.rs
package io.github.kotlinmania.syn

/**
 * Skips leading whitespace and Rust-style comments from [input]. Returns the
 * remaining suffix of the input after the skip.
 *
 * Mirrors `(crate) fun skip(s: &str) -> &str` from upstream
 * whitespace.rs — the slice-returning upstream idiom translates to returning the
 * suffix substring.
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
 i += 1 // eat '*'
 } else if (s[i] == '*' && s[i + 1] == '/') {
 depth -= 1
 if (depth == 0) {
 s = s.substring(i + 2)
 continue@skip
 }
 i += 1 // eat '/'
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
 //fall through to return
 }
 else -> {
 if (isWhitespaceRust(byte)) {
 s = s.substring(byte.toString().length)
 continue
 }
 }
 }
 return s
 }
 return s
}

private fun isWhitespaceRust(ch: Char): Boolean {
 //Rust treats left-to-right mark and right-to-left mark as whitespace
 return ch.isWhitespace() || ch == '‎' || ch == '‏'
}
