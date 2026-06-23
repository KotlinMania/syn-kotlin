// port-lint: source parse_quote.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

public fun parseQuote(tokenStream: TokenStream, parser: ParseQuote): Any? {
    val result = parserFromFunction(parser::parse).parse2(tokenStream)
    return result.getOrElse { err -> error(err.message ?: err.toString()) }
}

public interface ParseQuote {
    public fun parse(input: ParseStream): SynResult<Any?>
}

public fun parseQuoteFromParse(parse: Parse<Any?>): ParseQuote =
    object : ParseQuote {
        override fun parse(input: ParseStream): SynResult<Any?> = parse.parse(input)
    }

public object AttributeParseQuote : ParseQuote {
    override fun parse(input: ParseStream): SynResult<Attribute> =
        AttributeParse.parse(input)
}

public object AttributeListParseQuote : ParseQuote {
    override fun parse(input: ParseStream): SynResult<List<Attribute>> {
        val attrs = mutableListOf<Attribute>()
        while (!input.isEmpty()) {
            attrs += AttributeParseQuote.parse(input).getOrElse { return SynResult.failure(it) }
        }
        return SynResult.success(attrs)
    }
}

public object FieldParseQuote : ParseQuote {
    override fun parse(input: ParseStream): SynResult<Field> {
        val ahead = input.fork()
        parseOuterAttributes(ahead).getOrElse { return SynResult.failure(it) }
        ahead.parse(VisibilityParse).getOrElse { return SynResult.failure(it) }
        val isNamed =
            (ahead.peek(IdentPeek) || ahead.peek(UnderscorePeek)) &&
                ahead.peek2(ColonPeek) &&
                !ahead.peek2(PathSepPeek)
        return if (isNamed) {
            Field.parseNamed(input)
        } else {
            Field.parseUnnamed(input)
        }
    }
}

public object PatParseQuote : ParseQuote {
    override fun parse(input: ParseStream): SynResult<Pat> =
        parsePatMultiWithLeadingVert(input)
}

public object StmtListParseQuote : ParseQuote {
    override fun parse(input: ParseStream): SynResult<List<Stmt>> =
        parseWithin(input)
}