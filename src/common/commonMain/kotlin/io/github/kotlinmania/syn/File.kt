// port-lint: source file.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens

/**
 * A complete file of Rust source code.
 *
 * Typically `File` objects are created with [`parseFile`].
 *
 * [`parseFile`]: crate::parseFile
 *
 * # Example
 *
 * Parse a Rust source file into a `syn::File` and print out a debug
 * representation of the syntax tree.
 *
 * ```
 * import std.env;
 * import std.fs;
 * import std.process;
 *
 * fun main() {
 *     var args = env::args();
 *     val unused = args.next(); // executable name
 *
 *     val filename = when ((args.next(), args.next())) {
 *         (Some(filename), None) => filename,
 *         _ => {
 *             eprintln("Usage: dump-syntax path/to/filename.rs");
 *             process::exit(1);
 *         }
 *     };
 *
 *     val src = fs::readToString(&filename).expect("unable to read file");
 *     val syntax = syn::parseFile(&src).expect("unable to parse file");
 *
 *     // Debug implementation is available if Syn is built with "extra-traits" feature.
 *     println("{:#?}", syntax);
 * }
 * ```
 *
 * Running with its own source code as input, this program prints output
 * that begins with:
 *
 * ```text
 * File {
 *     shebang: None,
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
    public var attrs: List<Attribute>,
    public var items: List<Item>,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        for (item in items) item.toTokens(tokens)
    }

    public fun deepCopy(): File = File(shebang, attrs.mapTo(mutableListOf()) { it.deepCopy() }, items.toMutableList())
}

public object FileParse {
    fun parse(input: ParseStream): SynResult<File> {
        val attrs = parseInnerAttributes(input).getOrElse { return SynResult.failure(it) }
        val items = mutableListOf<Item>()
        while (!input.isEmpty()) {
            items.add(ItemParse.parse(input).getOrElse { return SynResult.failure(it) })
        }
        return SynResult.success(File(null, attrs, items.toMutableList()))
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
