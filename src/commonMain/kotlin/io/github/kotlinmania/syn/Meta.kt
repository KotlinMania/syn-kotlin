// port-lint: source meta.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span

public fun parseMetaPath(input: ParseStream): SynResult<Path> {
    val leadingColon =
        if (input.peek(PathSepPeek)) {
            PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }

    val segments = PathSegmentList()
    if (input.peek(IdentPeekAny)) {
        val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
        segments.pushValue(PathSegment.from(ident))
    } else if (input.isEmpty()) {
        return SynResult.failure(input.error("expected nested attribute"))
    } else if (input.peek(LitPeek)) {
        return SynResult.failure(input.error("unexpected literal in nested attribute, expected ident"))
    } else {
        return SynResult.failure(input.error("unexpected token in nested attribute, expected ident"))
    }

    while (input.peek(PathSepPeek)) {
        val punct = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
        segments.pushPunct(punct)
        val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
        segments.pushValue(PathSegment.from(ident))
    }

    return SynResult.success(Path(leadingColon, segments))
}

/**
 * Facility for interpreting structured content inside an [Attribute].
 *
 * Use [Attribute.parseNestedMeta] for the most common case of parsing
 * an entire attribute's content. Use [parser] if you are implementing
 * a procedural-macro handler and parsing the arguments to the attribute
 * macro directly.
 */

/**
 * Creates a parser usable with [parseMacroInput] from a
 * handler function that processes each nested attribute property.
 */
public fun parser(logic: (ParseNestedMeta) -> SynResult<Unit>): Parser =
    parserFromFunction { input ->
        if (input.isEmpty()) {
            SynResult.success(Unit)
        } else {
            parseNestedMetaInternal(input, logic)
        }
    }

public fun metaParser(logic: (ParseNestedMeta) -> SynResult<Unit>): Parser =
    parser(logic)

public class ParseNestedMeta(
    public val path: Path,
    public val input: ParseStream,
) {
    public fun value(): SynResult<ParseStream> {
        input.parse(EqParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(input)
    }

    public fun parseNestedMeta(logic: (ParseNestedMeta) -> SynResult<Unit>): SynResult<Unit> {
        val content = parenthesized(input).getOrElse { return SynResult.failure(it) }
        val result = parseNestedMetaInternal(content.content, logic)
        content.content.finishChildBuffer()
        return result
    }

    public fun error(msg: Any): SynError {
        val startSpan =
            path.segments
                .first()
                ?.ident
                ?.span()
                ?: Span.callSite()
        val endSpan = input.cursor().prevSpan()
        return SynError.new2(startSpan, endSpan, msg.toString())
    }
}

internal fun parseNestedMetaInternal(
    input: ParseStream,
    logic: (ParseNestedMeta) -> SynResult<Unit>,
): SynResult<Unit> {
    while (true) {
        val path = parseMetaPath(input).getOrElse { return SynResult.failure(it) }
        val result = logic(ParseNestedMeta(path, input))
        if (result.isFailure) return result
        if (input.isEmpty()) return SynResult.success(Unit)
        CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        if (input.isEmpty()) return SynResult.success(Unit)
    }
}
