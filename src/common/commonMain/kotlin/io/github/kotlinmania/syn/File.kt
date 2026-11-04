// port-lint: source file.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * A complete file of Rust source code.
 *
 * Typically `File` objects are created with [parseFile].
 *
 * # Example
 *
 * Parse a Rust source file into a `syn.File` and print out a debug
 * representation of the syntax tree.
 *
 * ```kotlin
 * fun main() {
 *     var args = env.args()
 *     args.next()
 *     // executable name
 *     val filename1 = run {
 *         val matchVal = Pair(args.next(), args.next())
 *         when {
 *             matchVal.first != null && matchVal.second == null -> {
 *                 val filename = matchVal.first
 *                 filename
 *             }
 *             else -> {
 *                 println("Usage: dump-syntax path/to/filename.rs")
 *                 process.exit(1)
 *             }
 *         }
 *     }
 *     val src = fs.readToString(filename1).expect("unable to read file")
 *     val syntax = parseFile(src).getOrThrow()
 *     // Debug representation is available on parsed syntax trees.
 *     println("$syntax")
 * }
 * ```
 *
 * Running with its own source code as input, this program prints output
 * that begins with:
 *
 * ```text
 * File {
 *     shebang: None,
 *     frontmatter: None,
 *     attrs: [],
 *     items: [
 *         Use(
 *             ItemUse {
 *                 attrs: [],
 *                 vis: Inherited,
 *                 useToken: Use,
 *                 leadingColon: None,
 *                 tree: Path(
 *                     UsePath {
 *                         ident: Ident(
 *                             std,
 *                         ),
 *                         colon2Token: Colon2,
 *                         tree: Name(
 *                             UseName {
 *                                 ident: Ident(
 *                                     env,
 *                                 ),
 *                             },
 *                         ),
 *                     },
 *                 ),
 *                 semiToken: Semi,
 *             },
 *         ),
 * ...
 * ```
 */
public data class File(
    public var shebang: String?,
    public var frontmatter: Frontmatter? = null,
    public var attrs: List<Attribute>,
    public var items: List<Item>,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        for (item in items) item.toTokens(tokens)
    }

    public fun deepCopy(): File =
        File(
            shebang,
            frontmatter?.deepCopy(),
            attrs.mapTo(mutableListOf()) { it.deepCopy() },
            items.toMutableList(),
        )
}

/** A frontmatter section fenced by `---`. */
public class Frontmatter {
    public fun deepCopy(): Frontmatter = Frontmatter()

    public fun clone(): Frontmatter = Frontmatter()

    override fun equals(other: Any?): Boolean = other is Frontmatter

    override fun hashCode(): Int = 0

    override fun toString(): String = "Frontmatter"
}

public object FileParse {
    fun parse(input: ParseStream): SynResult<File> {
        val attrs = parseInnerAttributes(input).getOrElse { return SynResult.failure(it) }
        val items = mutableListOf<Item>()
        while (!input.isEmpty()) {
            items.add(ItemParse.parse(input).getOrElse { return SynResult.failure(it) })
        }
        return SynResult.success(File(null, null, attrs, items.toMutableList()))
    }
}

public fun parseFile(content: String): FileParseResult {
    var source = content
    val bom = "\uFEFF"
    if (source.startsWith(bom)) {
        source = source.substring(bom.length)
    }

    var shebang: String? = null
    if (source.startsWith("#!")) {
        val rest = source.length - skip(source.substring(2)).length
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

    return parseStr(FileParse::parse, source).map { it.copy(shebang = shebang) }.toFileParseResult()
}
