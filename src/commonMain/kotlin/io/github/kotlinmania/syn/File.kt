// port-lint: source file.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append

/**
 * A complete file of source code.
 *
 * Typically [File] objects are created with [parseFile].
 */
public data class File(
 public val shebang: String?,
 public val attrs: List<Attribute>,
 public val items: List<Item>,
) : ToTokens {
 override fun toTokens(tokens: TokenStream) {
  for (attr in attrs) attr.toTokens(tokens)
  for (item in items) item.toTokens(tokens)
 }
}
