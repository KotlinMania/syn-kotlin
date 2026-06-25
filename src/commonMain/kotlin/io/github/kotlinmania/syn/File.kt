// port-lint: source file.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * A complete file of source code.
 *
 * Typically [File] objects are created with [parseFile].
 */
public data class File(
    public var shebang: String?,
    public var attrs: List<Attribute>,
    public var items: List<Item>,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        for (item in items) item.toTokens(tokens)
    }

    public fun deepCopy(): File = File(shebang, attrs.map { it.deepCopy() }, items.map { it })
}

public object FileParse {
    fun parse(input: ParseStream): SynResult<File> {
        val attrs = parseInnerAttributes(input).getOrElse { return SynResult.failure(it) }
        val items = mutableListOf<Item>()
        while (!input.isEmpty()) {
            items.add(ItemParse.parse(input).getOrElse { return SynResult.failure(it) })
        }
        return SynResult.success(File(null, attrs, items))
    }
}

public fun parseFile(content: String): SynResult<File> {
    var source = content
    val bom = "\uFEFF"
    if (source.startsWith(bom)) {
        source = source.substring(bom.length)
    }

    var shebang: String? = null
    if (source.startsWith("#!")) {
        val rest = source.length - skipWhitespace(source.substring(2)).length
        if (rest < source.length && source[rest] == '[') {
            source = "#!" + source.substring(rest)
        } else {
            val newline = source.indexOf('\n')
            if (newline >= 0) {
                shebang = source.substring(0, newline)
                source = source.substring(newline)
            } else {
                shebang = source
                source = ""
            }
        }
    }

    return parseStr(FileParse::parse, source).map { it.copy(shebang = shebang) }
}
