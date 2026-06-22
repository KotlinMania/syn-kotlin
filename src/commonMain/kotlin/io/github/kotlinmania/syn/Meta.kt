// port-lint: source meta.rs
package io.github.kotlinmania.syn

/**
 * Facility for interpreting structured content inside an [Attribute].
 *
 * Use [Attribute.parseNestedMeta] for the most common case of parsing
 * an entire attribute's content. Use [metaParser] if you are implementing
 * a procedural-macro handler and parsing the arguments to the attribute
 * macro directly.
 */

/**
 * Creates a parser usable with [parseMacroInput] from a
 * handler function that processes each nested attribute property.
 */
public fun metaParser(logic: (ParseNestedMeta) -> SynResult<Unit>): Parser<Unit> =
    parserFromFunction { input ->
        if (input.isEmpty()) {
            SynResult.success(Unit)
        } else {
            parseNestedMetaInternal(input, logic)
        }
    }

internal fun parseNestedMetaInternal(
    input: ParseStream,
    logic: (ParseNestedMeta) -> SynResult<Unit>,
): SynResult<Unit> {
    while (true) {
        val path = input.parse(PathParse).getOrElse { return SynResult.failure(it) }
        val result = logic(ParseNestedMeta(path, input.currentCursor.tokenStream()))
        if (result.isFailure) return result
        if (input.isEmpty()) return SynResult.success(Unit)
        // consume comma
        if (input.peek(CommaPeek)) {
            input.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        }
        if (input.isEmpty()) return SynResult.success(Unit)
    }
}
