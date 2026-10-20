// port-lint: source parse_quote.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

/**
 * Parsing interface for syntax tree types supported by `parseQuote`.
 */
public interface ParseQuote {
    public fun parse(input: ParseStream): SynResult<Any?>
}

/**
 * Parses [tokenStream] using [parser], throwing an argument exception if parsing fails.
 */
public fun <T> parse(tokenStream: TokenStream, parser: (ParseStream) -> SynResult<T>): T {
    val result: SynResult<T> = parse2(parser, tokenStream)
    return result.fold(
        onSuccess = { value: T -> value },
        onFailure = { err: SynError -> err.raise() },
    )
}

public fun parseQuoteAttribute(tokenStream: TokenStream): Attribute {
    val result: SynResult<Attribute> = parse2(AttributeParse::parse, tokenStream)
    return result.fold(
        onSuccess = { attribute: Attribute -> attribute },
        onFailure = { err: SynError -> err.raise() },
    )
}

public fun parseQuoteAttributeList(tokenStream: TokenStream): List<Attribute> {
    val result: SynResult<List<Attribute>> = parse2(::parseQuoteAttributeList, tokenStream)
    return result.fold(
        onSuccess = { attributes: List<Attribute> -> attributes },
        onFailure = { err: SynError -> err.raise() },
    )
}

private fun parseQuoteAttributeList(input: ParseStream): SynResult<List<Attribute>> {
    val attrs = mutableListOf<Attribute>()
    while (!input.isEmpty()) {
        attrs += AttributeParse.parse(input).getOrElse { return SynResult.failure(it) }
    }
    return SynResult.success(attrs)
}

public fun parseQuoteField(tokenStream: TokenStream): Field {
    val result: SynResult<Field> = parse2(::parseQuoteField, tokenStream)
    return result.fold(
        onSuccess = { field: Field -> field },
        onFailure = { err: SynError -> err.raise() },
    )
}

private fun parseQuoteField(input: ParseStream): SynResult<Field> {
    val ahead = input.fork()
    parseOuterAttributes(ahead).getOrElse { return SynResult.failure(it) }
    VisibilityParse.parse(ahead).getOrElse { return SynResult.failure(it) }
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

public fun parseQuotePat(tokenStream: TokenStream): Pat {
    val result: SynResult<Pat> = parse2(::parsePatMultiWithLeadingVert, tokenStream)
    return result.fold(
        onSuccess = { pat: Pat -> pat },
        onFailure = { err: SynError -> err.raise() },
    )
}

public fun parseQuoteStmtList(tokenStream: TokenStream): List<Stmt> {
    val result: SynResult<List<Stmt>> = parse2(::parseWithin, tokenStream)
    return result.fold(
        onSuccess = { stmts: List<Stmt> -> stmts },
        onFailure = { err: SynError -> err.raise() },
    )
}

public fun parseQuoteArmList(tokenStream: TokenStream): List<Arm> {
    val result: SynResult<List<Arm>> = parse2(::parseMultipleArms, tokenStream)
    return result.fold(
        onSuccess = { arms: List<Arm> -> arms },
        onFailure = { err: SynError -> err.raise() },
    )
}

public fun parseQuoteWherePredicate(tokenStream: TokenStream): WherePredicate {
    val result: SynResult<WherePredicate> = parse2(WherePredicate.Companion::parse, tokenStream)
    return result.fold(
        onSuccess = { pred: WherePredicate -> pred },
        onFailure = { err: SynError -> err.raise() },
    )
}
