// port-lint: source parse_quote.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream

public fun parseQuoteAttribute(tokenStream: TokenStream): Attribute {
    val result: SynResult<Attribute> = parse2(AttributeParse::parse, tokenStream)
    return result.fold(
        onSuccess = { attribute: Attribute -> attribute },
        onFailure = { err: SynError -> throw err },
    )
}

public fun parseQuoteAttributeList(tokenStream: TokenStream): List<Attribute> {
    val result: SynResult<List<Attribute>> = parse2(::parseQuoteAttributeList, tokenStream)
    return result.fold(
        onSuccess = { attributes: List<Attribute> -> attributes },
        onFailure = { err: SynError -> throw err },
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
        onFailure = { err: SynError -> throw err },
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
        onFailure = { err: SynError -> throw err },
    )
}

public fun parseQuoteStmtList(tokenStream: TokenStream): List<Stmt> {
    val result: SynResult<List<Stmt>> = parse2(::parseWithin, tokenStream)
    return result.fold(
        onSuccess = { stmts: List<Stmt> -> stmts },
        onFailure = { err: SynError -> throw err },
    )
}

public fun parseQuoteArmList(tokenStream: TokenStream): List<Arm> {
    val result: SynResult<List<Arm>> = parse2(::parseMultipleArms, tokenStream)
    return result.fold(
        onSuccess = { arms: List<Arm> -> arms },
        onFailure = { err: SynError -> throw err },
    )
}

public fun parseQuoteWherePredicate(tokenStream: TokenStream): WherePredicate {
    val result: SynResult<WherePredicate> = parse2(WherePredicate.Companion::parse, tokenStream)
    return result.fold(
        onSuccess = { pred: WherePredicate -> pred },
        onFailure = { err: SynError -> throw err },
    )
}

