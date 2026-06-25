// port-lint: source item.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Default
import io.github.kotlinmania.syn.token.Semi
import io.github.kotlinmania.syn.token.SynTypeToken
import io.github.kotlinmania.syn.token.Underscore

internal object ItemParse {
    fun parse(input: ParseStream): SynResult<Item> {
        val begin = input.fork()
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        return parseRestOfItem(begin, attrs, input)
    }
}

private fun parseRestOfItem(
    begin: ParseStream,
    attrs: List<Attribute>,
    input: ParseStream,
): SynResult<Item> {
    val visResult = VisibilityParse.parse(input)
    val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited

    if (peekSignature(input)) {
        val sigResult = parseSignature(input)
        if (sigResult.isFailure) return asFailure(sigResult)
        return parseRestOfFn(input, attrs, vis, sigResult.getOrThrow())
    }
    if (input.peek(StructPeek)) {
        val structToken = StructParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        generics.whereClause = parseWhereClause(input).getOrNull()
        if (input.peek(BracePeek)) {
            val bracesVal = braced(input).getOrThrow()
            val fields = parseNamedFieldList(bracesVal.content).getOrElse { return SynResult.failure(it) }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Struct(
                    attrs,
                    vis,
                    structToken,
                    ident,
                    generics,
                    Fields.Named(FieldsNamed(bracesVal.token, fields)),
                    null,
                ),
            )
        }
        if (input.peek(ParenPeek)) {
            val parensVal = parenthesized(input).getOrThrow()
            val fields = parseUnnamedFieldList(parensVal.content).getOrElse { return SynResult.failure(it) }
            parensVal.content.finishChildBuffer()
            val semi = SemiParse.parse(input).getOrNull()
            return SynResult.success(
                Item.Struct(
                    attrs,
                    vis,
                    structToken,
                    ident,
                    generics,
                    Fields.Unnamed(FieldsUnnamed(parensVal.token, fields)),
                    semi,
                ),
            )
        }
        if (input.peek(SemiPeek)) {
            val semi = SemiParse.parse(input).getOrThrow()
            return SynResult.success(
                Item.Struct(attrs, vis, structToken, ident, generics, Fields.Unit, semi),
            )
        }
        return SynResult.failure(input.error("expected `{`, `(`, or `;`"))
    }
    if (input.peek(EnumPeek)) {
        val enumToken = EnumParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        generics.whereClause = parseWhereClause(input).getOrNull()
        val bracesVal = braced(input).getOrThrow()
        val variants = parseVariantList(bracesVal.content).getOrElse { return SynResult.failure(it) }
        bracesVal.content.finishChildBuffer()
        return SynResult.success(
            Item.Enum(attrs, vis, enumToken, ident, generics, bracesVal.token, variants),
        )
    }
    if (input.peek(ExternPeek)) {
        val ahead = input.fork()
        ExternParse.parse(ahead).getOrElse { return SynResult.failure(it) }
        if (ahead.peek(CratePeek)) {
            return parseItemExternCrate(attrs, vis, input)
        }
        if (!vis.isInherited()) {
            return SynResult.failure(input.error("expected foreign module"))
        }
        return parseItemForeignMod(attrs, input)
    }
    if (input.peek(UnsafePeek) && input.peek2(ExternPeek)) {
        if (!vis.isInherited()) {
            return SynResult.failure(input.error("expected foreign module"))
        }
        return parseItemForeignMod(attrs, input)
    }
    if (input.peek(UnsafePeek) && (input.peek2(TraitPeek) || input.peek2(AutoPeek))) {
        val unsafety = UnsafeParse.parse(input).getOrElse { return SynResult.failure(it) }
        val autoToken = AutoParse.parse(input).getOrNull()
        val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        return parseRestOfTrait(input, attrs, vis, unsafety, autoToken, traitToken, ident, generics)
            .map { it }
    }
    if (input.peek(AutoPeek) && input.peek2(TraitPeek)) {
        val autoToken = AutoParse.parse(input).getOrElse { return SynResult.failure(it) }
        val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        return parseRestOfTrait(input, attrs, vis, null, autoToken, traitToken, ident, generics)
            .map { it }
    }
    if (input.peek(TraitPeek)) {
        return parseTraitOrTraitAlias(input, attrs, vis)
    }
    val implResult = parseImpl(begin, attrs, vis, input, allowVerbatimImpl = true)
    if (implResult.isFailure) return asFailure(implResult)
    implResult.getOrThrow()?.let {
        return SynResult.success(it)
    }
    if (input.peek(StaticPeek)) {
        val staticToken = StaticParse.parse(input).getOrElse { return SynResult.failure(it) }
        val mutability = StaticMutabilityParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }

        if (input.peek(EqPeek)) {
            EqParse.parse(input).getOrElse { return SynResult.failure(it) }
            parseExprFull(input).getOrElse { return SynResult.failure(it) }
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(Item.Verbatim(between(begin, input)))
        }

        val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }

        if (input.peek(SemiPeek)) {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(Item.Verbatim(between(begin, input)))
        }

        val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
        val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
        val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(
            Item.Static(attrs, vis, staticToken, mutability, ident, colonToken, ty, eqToken, expr, semiToken),
        )
    }
    if (input.peek(ConstPeek)) {
        val constToken = ConstParse.parse(input).getOrThrow()
        val identResult = IdentParse.parse(input)
        if (identResult.isFailure) return asFailure(identResult)
        val colonResult = ColonParse.parse(input)
        if (colonResult.isFailure) return asFailure(colonResult)
        val ty = parseTypeFull(input)
        if (ty.isFailure) return asFailure(ty)
        val eqResult = EqParse.parse(input)
        val eqToken = if (eqResult.isSuccess) eqResult.getOrThrow() else null
        var expr: Expr? = null
        if (eqToken != null) {
            val exprResult = parseExprFull(input)
            if (exprResult.isSuccess) expr = exprResult.getOrThrow()
        }
        val semi = SemiParse.parse(input).getOrThrow()
        return SynResult.success(
            Item.Const(attrs, vis, constToken, identResult.getOrThrow(), colonResult.getOrThrow(), ty.getOrThrow(), eqToken, expr, semi),
        )
    }
    if (input.peek(SynTypePeek)) {
        return parseItemType(begin, attrs, vis, input)
    }
    if (input.peek(UsePeek)) {
        return parseItemUse(begin, attrs, vis, input, allowCrateRootInPath = true)
    }
    if (input.peek(ModPeek) || (input.peek(UnsafePeek) && input.peek2(ModPeek))) {
        val unsafety = UnsafeParse.parse(input).getOrNull()
        val modToken = ModParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val semiResult = SemiParse.parse(input)
        if (semiResult.isSuccess) {
            return SynResult.success(
                Item.Mod(attrs, vis, unsafety, modToken, ident, ModContent.Unnamed(semiResult.getOrThrow())),
            )
        }
        val bracesVal = braced(input).getOrThrow()
        val items = mutableListOf<Item>()
        while (!bracesVal.content.isEmpty()) {
            val i = ItemParse.parse(bracesVal.content)
            if (i.isFailure) break
            items.add(i.getOrThrow())
        }
        bracesVal.content.finishChildBuffer()
        return SynResult.success(
            Item.Mod(attrs, vis, unsafety, modToken, ident, ModContent.Inline(bracesVal.token, items)),
        )
    }
    if (input.peek(MacroPeek)) {
        return parseMacro2(begin, vis, input)
    }
    val macroAhead = input.fork()
    if (parseModStylePath(macroAhead).isSuccess && macroAhead.peek(NotPeek)) {
        return parseItemMacro(input, attrs)
    }
    if (input.cursor() != begin.cursor()) {
        return parseVerbatimItem(begin, input)
    }
    return SynResult.failure(input.error("expected an item"))
}

internal object StaticMutabilityParse {
    fun parse(input: ParseStream): SynResult<StaticMutability> {
        val mutToken = MutParse.parse(input).getOrNull()
        return SynResult.success(
            if (mutToken == null) {
                StaticMutability.None
            } else {
                StaticMutability.Mut(mutToken)
            },
        )
    }
}

private fun parseItemExternCrate(
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
): SynResult<Item> {
    val externToken = ExternParse.parse(input).getOrElse { return SynResult.failure(it) }
    val crateToken = CrateParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident =
        if (input.peek(SelfValuePeek)) {
            from(SelfValueParse.parse(input).getOrElse { return SynResult.failure(it) })
        } else {
            IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    val rename =
        if (input.peek(AsPeek)) {
            val asToken = AsParse.parse(input).getOrElse { return SynResult.failure(it) }
            val renamed =
                if (input.peek(UnderscorePeek)) {
                    from(UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) })
                } else {
                    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                }
            AsIdent(asToken, renamed)
        } else {
            null
        }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Item.ExternCrate(attrs, vis, externToken, crateToken, ident, rename, semiToken))
}

private fun parseItemForeignMod(
    attrs: List<Attribute>,
    input: ParseStream,
): SynResult<Item> {
    val itemAttrs = attrs.toMutableList()
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val abi = parseAbi(input).getOrElse { return SynResult.failure(it) }
    val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
    parseInner(bracesVal.content, itemAttrs).getOrElse { return SynResult.failure(it) }
    val items = mutableListOf<ForeignItem>()
    while (!bracesVal.content.isEmpty()) {
        val item = parseForeignItem(bracesVal.content)
        if (item.isFailure) return asFailure(item)
        items.add(item.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Item.ForeignMod(itemAttrs, unsafety, abi, bracesVal.token, items))
}

internal fun parseForeignItem(input: ParseStream): SynResult<ForeignItem> {
    val begin = input.fork()
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }

    if (peekSignature(input)) {
        val sig = parseSignature(input).getOrElse { return SynResult.failure(it) }
        if (input.peek(BracePeek)) {
            parseBlock(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(ForeignItem.Verbatim(between(begin, input)))
        }
        val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(ForeignItem.Fn(attrs, vis, sig, semiToken))
    }

    if (input.peek(StaticPeek) || (input.peek(UnsafePeek) && input.peek2(StaticPeek))) {
        val unsafety = UnsafeParse.parse(input).getOrNull()
        val staticToken = StaticParse.parse(input).getOrElse { return SynResult.failure(it) }
        val mutability = StaticMutabilityParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        val hasValue = input.peek(EqPeek)
        if (hasValue) {
            EqParse.parse(input).getOrElse { return SynResult.failure(it) }
            parseExprFull(input).getOrElse { return SynResult.failure(it) }
        }
        val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        return if (unsafety != null || hasValue) {
            SynResult.success(ForeignItem.Verbatim(between(begin, input)))
        } else {
            SynResult.success(ForeignItem.Static(attrs, vis, staticToken, mutability, ident, colonToken, ty, semiToken))
        }
    }

    if (input.peek(SynTypePeek)) {
        return parseForeignItemType(begin, attrs, vis, input)
    }

    val macroAhead = input.fork()
    if (vis.isInherited() && parseModStylePath(macroAhead).isSuccess && macroAhead.peek(NotPeek)) {
        return parseForeignItemMacro(attrs, input)
    }

    return SynResult.failure(input.error("expected foreign item"))
}

private fun parseForeignItemType(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
): SynResult<ForeignItem> {
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val (colonToken, _) = FlexibleItemType.parseOptionalBounds(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val ty = FlexibleItemType.parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }
    if (generics.whereClause == null) {
        generics.whereClause = parseWhereClause(input).getOrNull()
    }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    return if (colonToken != null || ty != null) {
        SynResult.success(ForeignItem.Verbatim(between(begin, input)))
    } else {
        SynResult.success(ForeignItem.ItemType(attrs, vis, typeToken, ident, generics, semiToken))
    }
}

private fun parseForeignItemMacro(
    attrs: List<Attribute>,
    input: ParseStream,
): SynResult<ForeignItem> {
    val mac = Macro.parse(input).getOrElse { return SynResult.failure(it) }
    val semiToken =
        if (mac.delimiter.isBrace) {
            null
        } else {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(ForeignItem.Macro(attrs, mac, semiToken))
}

private fun parseItemType(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
): SynResult<Item> {
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val (colonToken, _) = FlexibleItemType.parseOptionalBounds(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val ty = FlexibleItemType.parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    if (colonToken != null || ty == null) {
        return SynResult.success(Item.Verbatim(between(begin, input)))
    }

    return SynResult.success(
        Item.ItemType(
            attrs,
            vis,
            typeToken,
            ident,
            generics,
            ty.eqToken,
            ty.type,
            semiToken,
        ),
    )
}

private fun parseItemUse(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
    allowCrateRootInPath: Boolean,
): SynResult<Item> {
    val useToken = UseParse.parse(input).getOrElse { return SynResult.failure(it) }
    val leadingColon = PathSepParse.parse(input).getOrNull()
    val tree =
        parseUseTree(input, allowCrateRootInPath && leadingColon == null)
            .getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    return if (tree == null) {
        SynResult.success(Item.Verbatim(between(begin, input)))
    } else {
        SynResult.success(Item.Use(attrs, vis, useToken, leadingColon, tree, semiToken))
    }
}

private fun parseRestOfTrait(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    unsafety: io.github.kotlinmania.syn.token.Unsafe?,
    autoToken: io.github.kotlinmania.syn.token.Auto?,
    traitToken: io.github.kotlinmania.syn.token.Trait,
    ident: Ident,
    generics: Generics,
): SynResult<Item.Trait> {
    val supertraits = TypeParamBoundList()
    var colonToken: io.github.kotlinmania.syn.token.Colon? = null
    if (input.peek(ColonPeek)) {
        colonToken = ColonParse.parse(input).getOrThrow()
        if (!input.peek(WherePeek) && !input.peek(BracePeek)) {
            val bounds = parseTypeParamBounds(input, stopAtEq = false).getOrElse { return SynResult.failure(it) }
            for ((bound, plus) in bounds.pairsList()) {
                supertraits.pushValue(bound as TypeParamBound)
                plus?.let { supertraits.pushPunct(it) }
            }
        }
    }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val bracesVal = braced(input).getOrThrow()
    val items = mutableListOf<TraitItem>()
    while (!bracesVal.content.isEmpty()) {
        val i = parseTraitItem(bracesVal.content)
        if (i.isFailure) break
        items.add(i.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(
        Item.Trait(attrs, vis, unsafety, autoToken, null, traitToken, ident, generics, colonToken, supertraits, bracesVal.token, items),
    )
}

private data class TraitAliasStart(
    val attrs: List<Attribute>,
    val vis: Visibility,
    val traitToken: io.github.kotlinmania.syn.token.Trait,
    val ident: Ident,
    val generics: Generics,
)

private fun parseTraitOrTraitAlias(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
): SynResult<Item> {
    val start = parseStartOfTraitAlias(input, attrs, vis).getOrElse { return SynResult.failure(it) }
    val lookahead = input.lookahead1()
    return when {
        lookahead.peek(BracePeek) || lookahead.peek(ColonPeek) || lookahead.peek(WherePeek) ->
            parseRestOfTrait(
                input,
                start.attrs,
                start.vis,
                unsafety = null,
                autoToken = null,
                start.traitToken,
                start.ident,
                start.generics,
            ).map { it }
        lookahead.peek(EqPeek) ->
            parseRestOfTraitAlias(
                input,
                start.attrs,
                start.vis,
                start.traitToken,
                start.ident,
                start.generics,
            ).map { it }
        else -> SynResult.failure(lookahead.error())
    }
}

private fun parseStartOfTraitAlias(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
): SynResult<TraitAliasStart> {
    val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(TraitAliasStart(attrs, vis, traitToken, ident, generics))
}

private fun parseRestOfTraitAlias(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    traitToken: io.github.kotlinmania.syn.token.Trait,
    ident: Ident,
    generics: Generics,
): SynResult<Item.TraitAlias> {
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val bounds = TypeParamBoundList()

    while (true) {
        if (input.peek(WherePeek) || input.peek(SemiPeek)) break
        val bound =
            parseTypeParamBound(
                input,
                allowPreciseCapture = false,
                allowConst = false,
            ).getOrElse { return SynResult.failure(it) }
        bounds.pushValue(bound)
        if (input.peek(WherePeek) || input.peek(SemiPeek)) break
        bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
    }

    generics.whereClause = parseWhereClause(input).getOrNull()
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    return SynResult.success(
        Item.TraitAlias(attrs, vis, traitToken, ident, generics, eqToken, bounds, semiToken),
    )
}

private fun parseVerbatimItem(
    begin: ParseStream,
    input: ParseStream,
): SynResult<Item> {
    TokenStreamParse.parse(input).getOrElse { return SynResult.failure(it) }
    val tokens = TokenStreamParse.parse(begin).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Item.Verbatim(tokens))
}

private fun parseItemMacro(input: ParseStream, attrs: List<Attribute>): SynResult<Item> {
    val path = parseModStylePath(input).getOrElse { return SynResult.failure(it) }
    val bangToken = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrNull()
    val delimiterResult = parseDelimiter(input)
    if (delimiterResult.isFailure) return asFailure(delimiterResult)
    val (delimiter, tokens) = delimiterResult.getOrThrow()
    val semiToken =
        if (delimiter.isBrace) {
            null
        } else {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(
        Item.Macro(attrs, ident, Macro(path, bangToken, delimiter, tokens), semiToken),
    )
}

private fun parseMacro2(
    begin: ParseStream,
    _vis: Visibility,
    input: ParseStream,
): SynResult<Item> {
    MacroParse.parse(input).getOrElse { return SynResult.failure(it) }
    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }

    if (input.peek(ParenPeek)) {
        val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
        TokenStreamParse.parse(parens.content).getOrElse { return SynResult.failure(it) }
        parens.content.finishChildBuffer()
    }

    if (input.peek(BracePeek)) {
        val braces = braced(input).getOrElse { return SynResult.failure(it) }
        TokenStreamParse.parse(braces.content).getOrElse { return SynResult.failure(it) }
        braces.content.finishChildBuffer()
    } else {
        return SynResult.failure(input.lookahead1().error())
    }

    return SynResult.success(Item.Verbatim(between(begin, input)))
}

private fun parseImpl(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
    allowVerbatimImpl: Boolean,
): SynResult<Item?> {
    val implAhead = input.fork()
    val defaultness = DefaultParse.parse(implAhead).getOrNull()
    val unsafety = UnsafeParse.parse(implAhead).getOrNull()
    if (!implAhead.peek(ImplPeek)) {
        return SynResult.success(null)
    }
    if (allowVerbatimImpl && !vis.isInherited()) {
        return parseVerbatimItem(begin, input).map { it }
    }

    input.advanceTo(implAhead)
    val implToken = ImplParse.parse(input).getOrThrow()
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val traitPath: PathTrait?
    val selfType: SynType
    if (input.peek(NotPeek)) {
        val polarity = NotParse.parse(input).getOrThrow()
        if (input.peek(BracePeek) || input.peek(WherePeek)) {
            traitPath = null
            selfType = SynType.Never(polarity)
        } else {
            val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
            if (!input.peek(ForPeek)) {
                return SynResult.failure(input.error("inherent impls cannot be negative"))
            }
            val forToken = ForParse.parse(input).getOrThrow()
            val parsedSelfType = parseTypeFull(input)
            if (parsedSelfType.isFailure) return asFailure(parsedSelfType)
            traitPath = PathTrait(polarity, path, forToken)
            selfType = parsedSelfType.getOrThrow()
        }
    } else {
        val traitAhead = input.fork()
        val pathResult = PathParse.parse(traitAhead)
        if (pathResult.isSuccess && traitAhead.peek(ForPeek)) {
            input.advanceTo(traitAhead)
            val forToken = ForParse.parse(input).getOrThrow()
            val parsedSelfType = parseTypeFull(input)
            if (parsedSelfType.isFailure) return asFailure(parsedSelfType)
            traitPath = PathTrait(null, pathResult.getOrThrow(), forToken)
            selfType = parsedSelfType.getOrThrow()
        } else {
            val parsedSelfType = parseTypeFull(input)
            if (parsedSelfType.isFailure) return asFailure(parsedSelfType)
            traitPath = null
            selfType = parsedSelfType.getOrThrow()
        }
    }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val bracesVal = braced(input).getOrThrow()
    val items = mutableListOf<ImplItem>()
    while (!bracesVal.content.isEmpty()) {
        val i = parseImplItem(bracesVal.content)
        if (i.isFailure) break
        items.add(i.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(
        Item.Impl(attrs, defaultness, unsafety, implToken, generics, traitPath, selfType, bracesVal.token, items),
    )
}

private data class FlexibleItemType(
    val vis: Visibility,
    val defaultness: Default?,
    val typeToken: SynTypeToken,
    val ident: Ident,
    val generics: Generics,
    val colonToken: Colon?,
    val bounds: TypeParamBoundList,
    val ty: EqSynType?,
    val semiToken: Semi,
) {
    companion object {
        fun parse(
            input: ParseStream,
            allowDefaultness: TypeDefaultness,
            whereClauseLocation: WhereClauseLocation,
        ): SynResult<FlexibleItemType> {
            val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val defaultness =
                when (allowDefaultness) {
                    TypeDefaultness.Optional -> DefaultParse.parse(input).getOrNull()
                    TypeDefaultness.Disallowed -> null
                }
            val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val (colonToken, bounds) = parseOptionalBounds(input).getOrElse { return SynResult.failure(it) }

            when (whereClauseLocation) {
                WhereClauseLocation.BeforeEq,
                WhereClauseLocation.Both,
                -> generics.whereClause = parseWhereClause(input).getOrNull()
                WhereClauseLocation.AfterEq -> {}
            }

            val ty = parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }

            when (whereClauseLocation) {
                WhereClauseLocation.AfterEq,
                WhereClauseLocation.Both,
                ->
                    if (generics.whereClause == null) {
                        generics.whereClause = parseWhereClause(input).getOrNull()
                    }
                WhereClauseLocation.BeforeEq -> {}
            }

            val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(
                FlexibleItemType(
                    vis,
                    defaultness,
                    typeToken,
                    ident,
                    generics,
                    colonToken,
                    bounds,
                    ty,
                    semiToken,
                ),
            )
        }

        fun parseOptionalBounds(input: ParseStream): SynResult<Pair<Colon?, TypeParamBoundList>> {
            val colonToken = ColonParse.parse(input).getOrNull()
            val bounds = TypeParamBoundList()
            if (colonToken != null) {
                while (true) {
                    if (input.peek(WherePeek) || input.peek(EqPeek) || input.peek(SemiPeek)) break
                    val bound =
                        parseTypeParamBound(
                            input,
                            allowPreciseCapture = false,
                            allowConst = true,
                        ).getOrElse { return SynResult.failure(it) }
                    bounds.pushValue(bound)
                    if (input.peek(WherePeek) || input.peek(EqPeek) || input.peek(SemiPeek)) break
                    bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
                }
            }
            return SynResult.success(colonToken to bounds)
        }

        fun parseOptionalDefinition(input: ParseStream): SynResult<EqSynType?> {
            val eqToken = EqParse.parse(input).getOrNull() ?: return SynResult.success(null)
            val definition = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(EqSynType(eqToken, definition))
        }
    }
}

private enum class TypeDefaultness {
    Optional,
    Disallowed,
}

private enum class WhereClauseLocation {
    BeforeEq,
    AfterEq,
    Both,
}

private fun peekFlexibleItemType(
    input: ParseStream,
    allowDefaultness: TypeDefaultness,
): Boolean {
    val ahead = input.fork()
    VisibilityParse.parse(ahead)
    if (allowDefaultness == TypeDefaultness.Optional) {
        DefaultParse.parse(ahead)
    }
    return ahead.peek(SynTypePeek)
}

private fun <T, R> asFailure(result: SynResult<T>): SynResult<R> =
    SynResult.failure((result as SynResult.Failure).error)

private fun Visibility.isInherited(): Boolean =
    this is Visibility.Inherited

internal fun peekSignature(input: ParseStream): Boolean {
    val fork = input.fork()
    ConstParse.parse(fork)
    AsyncParse.parse(fork)
    UnsafeParse.parse(fork)
    parseAbi(fork)
    return fork.peek(FnPeek)
}

private fun parseSignature(input: ParseStream): SynResult<Signature> {
    val constness = ConstParse.parse(input).getOrNull()
    val asyncness = AsyncParse.parse(input).getOrNull()
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val abi = parseAbi(input).getOrNull()
    val fnTokenResult = FnParse.parse(input)
    if (fnTokenResult.isFailure) return asFailure(fnTokenResult)
    val identResult = IdentParse.parse(input)
    if (identResult.isFailure) return asFailure(identResult)
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val parensResult = parenthesized(input)
    if (parensResult.isFailure) return asFailure(parensResult)
    val parensVal = parensResult.getOrThrow()
    val inputsResult = parseFnArgs(parensVal.content)
    if (inputsResult.isFailure) return asFailure(inputsResult)
    val (inputs, variadic) = inputsResult.getOrThrow()
    parensVal.content.finishChildBuffer()
    val outputResult = parseReturnType(input)
    if (outputResult.isFailure) return asFailure(outputResult)
    generics.whereClause = parseWhereClause(input).getOrNull()
    return SynResult.success(
        Signature(
            constness,
            asyncness,
            unsafety,
            abi,
            fnTokenResult.getOrThrow(),
            identResult.getOrThrow(),
            generics,
            parensVal.token,
            inputs,
            variadic,
            outputResult.getOrThrow(),
        ),
    )
}

internal fun parseAbi(input: ParseStream): SynResult<Abi> {
    val externResult = ExternParse.parse(input)
    if (externResult.isFailure) return asFailure(externResult)
    val name = LitStrParse.parse(input).getOrNull()
    return SynResult.success(Abi(externResult.getOrThrow(), name))
}

private sealed class FnArgOrVariadic {
    data class FnArgValue(
        val arg: FnArg,
    ) : FnArgOrVariadic()

    data class VariadicValue(
        val variadic: Variadic,
    ) : FnArgOrVariadic()
}

private fun parseFnArgs(content: ParseStream): SynResult<Pair<FnArgList, Variadic?>> {
    val inputs = FnArgList()
    var variadic: Variadic? = null
    var hasReceiver = false
    while (!content.isEmpty()) {
        val attrs = parseOuterAttributes(content).getOrElse { return SynResult.failure(it) }

        val dots = DotDotDotParse.parse(content).getOrNull()
        if (dots != null) {
            val comma =
                if (content.isEmpty()) {
                    null
                } else {
                    CommaParse.parse(content).getOrElse { return SynResult.failure(it) }
                }
            variadic = Variadic(attrs, null, dots, comma)
            break
        }

        val argOrVariadic =
            parseFnArgOrVariadic(content, attrs, allowVariadic = true)
                .getOrElse { return SynResult.failure(it) }
        val arg =
            when (argOrVariadic) {
                is FnArgOrVariadic.FnArgValue -> argOrVariadic.arg
                is FnArgOrVariadic.VariadicValue -> {
                    val comma =
                        if (content.isEmpty()) {
                            null
                        } else {
                            CommaParse.parse(content).getOrElse { return SynResult.failure(it) }
                        }
                    variadic = argOrVariadic.variadic.copy(comma = comma)
                    break
                }
            }

        if (arg is FnArg.Receiver) {
            if (hasReceiver) return SynResult.failure(content.error("unexpected second method receiver"))
            if (!inputs.isEmpty()) return SynResult.failure(content.error("unexpected method receiver"))
            hasReceiver = true
        }
        inputs.pushValue(arg)
        if (content.isEmpty()) break
        val commaResult = CommaParse.parse(content)
        if (commaResult.isFailure) return asFailure(commaResult)
        inputs.pushPunct(commaResult.getOrThrow())
    }
    return SynResult.success(inputs to variadic)
}

internal fun parseReturnType(input: ParseStream): SynResult<ReturnType> =
    parseReturnType(input, allowPlus = true)

internal fun parseReturnTypeWithoutPlus(input: ParseStream): SynResult<ReturnType> =
    parseReturnType(input, allowPlus = false)

private fun parseReturnType(input: ParseStream, allowPlus: Boolean): SynResult<ReturnType> {
    if (!input.peek(RArrowPeek)) {
        return SynResult.success(ReturnType.Default)
    }
    val arrowResult = RArrowParse.parse(input)
    if (arrowResult.isFailure) return asFailure(arrowResult)
    val tyResult =
        if (allowPlus) {
            parseTypeFull(input)
        } else {
            parseTypeWithoutPlus(input)
        }
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow()))
}

private fun parseBlock(input: ParseStream): SynResult<Block> {
    val bracesResult = braced(input)
    if (bracesResult.isFailure) return asFailure(bracesResult)
    val bracesVal = bracesResult.getOrThrow()
    val stmts = mutableListOf<Stmt>()
    while (!bracesVal.content.isEmpty()) {
        val stmtResult = parseStmtFull(bracesVal.content)
        if (stmtResult.isFailure) return asFailure(stmtResult)
        stmts.add(stmtResult.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Block(bracesVal.token, stmts))
}

private fun parseRestOfFn(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    sig: Signature,
): SynResult<Item.Fn> {
    val bracesResult = braced(input)
    if (bracesResult.isFailure) return asFailure(bracesResult)
    val bracesVal = bracesResult.getOrThrow()
    val itemAttrs = attrs.toMutableList()
    parseInner(bracesVal.content, itemAttrs).getOrElse { return SynResult.failure(it) }
    val stmts = mutableListOf<Stmt>()
    while (!bracesVal.content.isEmpty()) {
        val stmtResult = parseStmtFull(bracesVal.content)
        if (stmtResult.isFailure) return asFailure(stmtResult)
        stmts.add(stmtResult.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Item.Fn(itemAttrs, vis, sig, Block(bracesVal.token, stmts)))
}

internal fun parseFnArg(input: ParseStream): SynResult<FnArg> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    return when (val arg = parseFnArgOrVariadic(input, attrs, allowVariadic = false).getOrElse { return SynResult.failure(it) }) {
        is FnArgOrVariadic.FnArgValue -> SynResult.success(arg.arg)
        is FnArgOrVariadic.VariadicValue -> SynResult.failure(input.error("expected function argument"))
    }
}

private fun parseFnArgOrVariadic(
    input: ParseStream,
    attrs: List<Attribute>,
    allowVariadic: Boolean,
): SynResult<FnArgOrVariadic> {
    val ahead = input.fork()
    val receiverResult = parseReceiver(ahead)
    if (receiverResult.isSuccess) {
        input.advanceTo(ahead)
        val receiver = receiverResult.getOrThrow()
        return SynResult.success(FnArgOrVariadic.FnArgValue(receiver.copy(attrs = attrs)))
    }

    if (input.peek(IdentPeek) && input.peek2(LtPeek)) {
        val span = input.span()
        val tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return asFailure(tyResult)
        return SynResult.success(
            FnArgOrVariadic.FnArgValue(
                FnArg.Typed(
                    PatType(
                        attrs,
                        Pat.Wild(emptyList(), Underscore.from(span)),
                        Colon.from(span),
                        tyResult.getOrThrow(),
                    ),
                ),
            ),
        )
    }

    val patResult = parsePatFull(input)
    if (patResult.isFailure) return asFailure(patResult)
    val colonResult = ColonParse.parse(input)
    if (colonResult.isFailure) return asFailure(colonResult)

    if (allowVariadic) {
        val dots = DotDotDotParse.parse(input).getOrNull()
        if (dots != null) {
            return SynResult.success(
                FnArgOrVariadic.VariadicValue(
                    Variadic(attrs, PatColon(patResult.getOrThrow(), colonResult.getOrThrow()), dots, null),
                ),
            )
        }
    }

    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(
        FnArgOrVariadic.FnArgValue(
            FnArg.Typed(PatType(attrs, patResult.getOrThrow(), colonResult.getOrThrow(), tyResult.getOrThrow())),
        ),
    )
}

private fun parseReceiver(input: ParseStream): SynResult<FnArg.Receiver> {
    val referenceResult = parseReceiverAnd(input)
    val reference =
        if (referenceResult.isSuccess) {
            val andToken = referenceResult.getOrThrow()
            val lifetime = LifetimeParse.parse(input).getOrNull()
            AndLifetime(andToken, lifetime)
        } else {
            null
        }
    val mutability = MutParse.parse(input).getOrNull()
    val selfTokenResult = SelfValueParse.parse(input)
    if (selfTokenResult.isFailure) return asFailure(selfTokenResult)
    val selfToken = selfTokenResult.getOrThrow()
    val colonToken =
        if (reference == null) {
            ColonParse.parse(input).getOrNull()
        } else {
            null
        }
    val ty =
        if (colonToken != null) {
            parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        } else {
            val selfTy =
                SynType.Path(
                    null,
                    Path.from(
                        io.github.kotlinmania.procmacro2.Ident
                            .new("Self", selfToken.span),
                    ),
                )
            if (reference != null) {
                SynType.Reference(reference.andToken, reference.lifetime, mutability, selfTy)
            } else {
                selfTy
            }
        }
    return SynResult.success(FnArg.Receiver(emptyList(), reference, mutability, selfToken, colonToken, ty))
}

private fun parseReceiverAnd(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.And> =
    input.step { cursor ->
        val (punct, rest) =
            cursor.punct()
                ?: return@step SynResult.failure(cursor.error("expected `&`"))
        if (punct.asChar() != '&') {
            return@step SynResult.failure(cursor.error("expected `&`"))
        }
        SynResult.success(
            io.github.kotlinmania.syn.token.And
                .from(punct.span()) to rest,
        )
    }

internal fun parseGenerics(input: ParseStream): SynResult<Generics> = Generics.parse(input)

internal object GenericsLtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '<'
    }

    override fun display(): String = "`<`"
}

internal object GenericsLtParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Lt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<`"))
            if (punct.asChar() != '<') return@step SynResult.failure(cursor.error("expected `<`"))
            SynResult.success(
                io.github.kotlinmania.syn.token.Lt
                    .from(punct.span()) to rest,
            )
        }
}

internal object GenericsGtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '>'
    }

    override fun display(): String = "`>`"
}

internal object GenericsGtParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Gt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>`"))
            if (punct.asChar() != '>') return@step SynResult.failure(cursor.error("expected `>`"))
            SynResult.success(
                io.github.kotlinmania.syn.token.Gt
                    .from(punct.span()) to rest,
            )
        }
}

public object GenericParamParse {
    fun parse(input: ParseStream): SynResult<GenericParam> =
        GenericParam.parse(input)
}

private fun parseGenericParam(input: ParseStream): SynResult<GenericParam> = GenericParam.parse(input)

public object WhereClauseParse {
    fun parse(input: ParseStream): SynResult<WhereClause> =
        parseWhereClause(input)
}

internal fun parseWhereClause(input: ParseStream): SynResult<WhereClause> = WhereClause.parse(input)

private fun parseWherePredicate(input: ParseStream): SynResult<WherePredicate> = WherePredicate.parse(input)

internal fun parseTypeParamBounds(
    input: ParseStream,
    stopAtEq: Boolean,
    allowPreciseCapture: Boolean = false,
    allowPlus: Boolean = true,
): SynResult<TypeParamBoundList> {
    val bounds = TypeParamBoundList()
    while (!input.isEmpty() && !input.peek(CommaPeek) && !input.peek(GenericsGtPeek) && !input.peek(BracePeek) && !input.peek(WherePeek) && !input.peek(SemiPeek) && !(stopAtEq && input.peek(EqPeek))) {
        val bound =
            TypeParamBound
                .parseSingle(
                    input,
                    allowPreciseCapture = allowPreciseCapture,
                    allowConst = false,
                ).getOrElse { return SynResult.failure(it) }
        bounds.pushValue(bound)
        if (!allowPlus || !input.peek(PlusPeek)) break
        bounds.pushPunct(PlusParse.parse(input).getOrThrow())
    }
    return SynResult.success(bounds)
}

internal fun parseTypeParamBoundsMultiple(
    input: ParseStream,
    allowPlus: Boolean,
    allowPreciseCapture: Boolean,
    allowConst: Boolean,
): SynResult<TypeParamBoundList> = TypeParamBound.parseMultiple(input, allowPlus, allowPreciseCapture, allowConst)

private fun parseTypeParamBound(
    input: ParseStream,
    allowPreciseCapture: Boolean,
    allowConst: Boolean = false,
): SynResult<TypeParamBound> = TypeParamBound.parseSingle(input, allowPreciseCapture, allowConst)

internal fun parseBoundLifetimes(input: ParseStream): SynResult<BoundLifetimes> = BoundLifetimes.parse(input)

public object TypeParamBoundParse {
    fun parse(input: ParseStream): SynResult<TypeParamBound> =
        TypeParamBound.parse(input)
}

private fun parsePreciseCapture(input: ParseStream): SynResult<TypeParamBound.PreciseCapture> = TypeParamBound.PreciseCapture.parse(input)

private fun parseCapturedParam(input: ParseStream): SynResult<CapturedParam> = CapturedParam.parse(input)

internal fun parseNamedFieldList(input: ParseStream): SynResult<FieldList> {
    val fields = FieldList()
    while (!input.isEmpty()) {
        val field = parseNamedField(input).getOrElse { return SynResult.failure(it) }
        fields.pushValue(field)
        if (input.isEmpty()) break
        val comma = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        fields.pushPunct(comma)
    }
    return SynResult.success(fields)
}

internal fun parseUnnamedFieldList(input: ParseStream): SynResult<FieldList> {
    val fields = FieldList()
    while (!input.isEmpty()) {
        val field = parseUnnamedField(input).getOrElse { return SynResult.failure(it) }
        fields.pushValue(field)
        if (input.isEmpty()) break
        val comma = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        fields.pushPunct(comma)
    }
    return SynResult.success(fields)
}

internal fun parseVariantList(input: ParseStream): SynResult<VariantList> {
    val variants = VariantList()
    while (!input.isEmpty()) {
        val variant = parseVariant(input).getOrElse { return SynResult.failure(it) }
        variants.pushValue(variant)
        if (input.isEmpty()) break
        val comma = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        variants.pushPunct(comma)
    }
    return SynResult.success(variants)
}

internal fun parseNamedField(input: ParseStream): SynResult<Field> = Field.parseNamed(input)

internal fun parseVariant(input: ParseStream): SynResult<Variant> = VariantParse.parse(input)

internal fun parseUnnamedField(input: ParseStream): SynResult<Field> = Field.parseUnnamed(input)

internal fun parseUseTree(input: ParseStream): SynResult<UseTree> {
    val tree = parseUseTree(input, allowCrateRootInPath = false).getOrElse { return SynResult.failure(it) }
    return tree?.let { SynResult.success(it) } ?: SynResult.failure(input.error("expected use tree"))
}

private fun parseUseTree(
    input: ParseStream,
    allowCrateRootInPath: Boolean,
): SynResult<UseTree?> {
    if (
        input.peek(IdentPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek) ||
        input.peek(TryPeek)
    ) {
        val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
        if (input.peek(PathSepPeek)) {
            val colon2Token = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
            val tree = parseUseTree(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(UseTree.Path(ident, colon2Token, tree))
        }
        if (input.peek(AsPeek)) {
            val asToken = AsParse.parse(input).getOrElse { return SynResult.failure(it) }
            val rename =
                if (input.peek(IdentPeek)) {
                    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                } else if (input.peek(UnderscorePeek)) {
                    from(UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) })
                } else {
                    return SynResult.failure(input.error("expected identifier or underscore"))
                }
            return SynResult.success(UseTree.Name(ident, AsIdent(asToken, rename)))
        }
        return SynResult.success(UseTree.Name(ident, null))
    }

    if (input.peek(StarPeek)) {
        val starToken = StarParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(UseTree.Glob(starToken))
    }

    if (input.peek(BracePeek)) {
        val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
        val items = UseTreeList()
        var hasAnyCrateRootInPath = false
        while (!bracesVal.content.isEmpty()) {
            val thisTreeStartsWithCrateRoot =
                if (allowCrateRootInPath && bracesVal.content.peek(PathSepPeek)) {
                    PathSepParse.parse(bracesVal.content).getOrElse { return SynResult.failure(it) }
                    true
                } else {
                    false
                }
            hasAnyCrateRootInPath = hasAnyCrateRootInPath || thisTreeStartsWithCrateRoot
            val tree =
                parseUseTree(
                    bracesVal.content,
                    allowCrateRootInPath && !thisTreeStartsWithCrateRoot,
                ).getOrElse { return SynResult.failure(it) }
            if (tree != null && !hasAnyCrateRootInPath) {
                items.pushValue(tree)
            } else {
                hasAnyCrateRootInPath = true
            }
            if (bracesVal.content.isEmpty()) break
            val comma = CommaParse.parse(bracesVal.content).getOrElse { return SynResult.failure(it) }
            if (!hasAnyCrateRootInPath) {
                items.pushPunct(comma)
            }
        }
        bracesVal.content.finishChildBuffer()
        return if (hasAnyCrateRootInPath) {
            SynResult.success(null)
        } else {
            SynResult.success(UseTree.Group(bracesVal.token, items))
        }
    }

    return SynResult.failure(input.lookahead1().error())
}

internal fun parseTraitItem(input: ParseStream): SynResult<TraitItem> {
    if (peekSignature(input)) {
        val sigResult = parseSignature(input)
        if (sigResult.isFailure) return asFailure(sigResult)
        if (input.peek(BracePeek)) {
            val defaultResult = parseBlock(input)
            if (defaultResult.isFailure) return asFailure(defaultResult)
            return SynResult.success(TraitItem.Fn(emptyList(), sigResult.getOrThrow(), defaultResult.getOrThrow(), null))
        }
        val semiResult = SemiParse.parse(input)
        if (semiResult.isFailure) return asFailure(semiResult)
        return SynResult.success(TraitItem.Fn(emptyList(), sigResult.getOrThrow(), null, semiResult.getOrThrow()))
    }
    if (input.peek(ConstPeek)) {
        val constToken = ConstParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val colon = ColonParse.parse(input).getOrThrow()
        val ty = parseTypeFull(input).getOrThrow()
        var default: EqExpr? = null
        if (input.peek(EqPeek)) {
            val eq = EqParse.parse(input).getOrThrow()
            val exprResult = parseExprFull(input)
            if (exprResult.isSuccess) {
                default = EqExpr(eq, exprResult.getOrThrow())
            }
        }
        val semi = SemiParse.parse(input).getOrThrow()
        return SynResult.success(TraitItem.Const(emptyList(), constToken, ident, Generics(), colon, ty, default, semi))
    }
    if (peekFlexibleItemType(input, TypeDefaultness.Disallowed)) {
        val begin = input.fork()
        return parseTraitItemType(begin, input)
    }
    if (input.peek(SemiPeek)) {
        SemiParse.parse(input).getOrThrow()
        return SynResult.success(
            TraitItem.Verbatim(
                io.github.kotlinmania.procmacro2.TokenStream
                    .new(),
            ),
        )
    }
    return SynResult.failure(input.error("expected trait item"))
}

internal fun parseImplItem(input: ParseStream): SynResult<ImplItem> {
    if (peekImplItemFn(input)) {
        val fnResult = parseImplItemFn(input, allowOmittedBody = false)
        if (fnResult.isFailure) return asFailure(fnResult)
        val itemFn = fnResult.getOrThrow() ?: return SynResult.failure(input.error("expected impl item function"))
        return SynResult.success(itemFn)
    }
    if (input.peek(ConstPeek)) {
        val constToken = ConstParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val colon = ColonParse.parse(input).getOrThrow()
        val ty = parseTypeFull(input).getOrThrow()
        val eq = EqParse.parse(input).getOrThrow()
        val expr = parseExprFull(input).getOrThrow()
        val semi = SemiParse.parse(input).getOrThrow()
        return SynResult.success(ImplItem.Const(emptyList(), Visibility.Inherited, null, constToken, ident, Generics(), colon, ty, eq, expr, semi))
    }
    if (peekFlexibleItemType(input, TypeDefaultness.Optional)) {
        val begin = input.fork()
        return parseImplItemType(begin, input)
    }
    if (input.peek(SemiPeek)) {
        SemiParse.parse(input).getOrThrow()
        return SynResult.success(
            ImplItem.Verbatim(
                io.github.kotlinmania.procmacro2.TokenStream
                    .new(),
            ),
        )
    }
    return SynResult.failure(input.error("expected impl item"))
}

private fun parseTraitItemType(begin: ParseStream, input: ParseStream): SynResult<TraitItem> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Disallowed,
                WhereClauseLocation.AfterEq,
            ).getOrElse { return SynResult.failure(it) }
    if (!itemType.vis.isInherited()) {
        return SynResult.success(TraitItem.Verbatim(between(begin, input)))
    }
    return SynResult.success(
        TraitItem.AssocType(
            emptyList(),
            itemType.typeToken,
            itemType.ident,
            itemType.generics,
            itemType.colonToken,
            itemType.bounds,
            itemType.ty,
            itemType.semiToken,
        ),
    )
}

private fun parseImplItemType(begin: ParseStream, input: ParseStream): SynResult<ImplItem> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Optional,
                WhereClauseLocation.AfterEq,
            ).getOrElse { return SynResult.failure(it) }
    val ty = itemType.ty
    if (itemType.colonToken != null || ty == null) {
        return SynResult.success(ImplItem.Verbatim(between(begin, input)))
    }
    return SynResult.success(
        ImplItem.AssocType(
            emptyList(),
            itemType.vis,
            itemType.defaultness,
            itemType.typeToken,
            itemType.ident,
            itemType.generics,
            ty.eqToken,
            ty.type,
            itemType.semiToken,
        ),
    )
}

private fun peekImplItemFn(input: ParseStream): Boolean {
    val ahead = input.fork()
    if (parseOuterAttributes(ahead).isFailure) return false
    VisibilityParse.parse(ahead).getOrElse { return false }
    DefaultParse.parse(ahead)
    return peekSignature(ahead)
}

private fun parseImplItemFn(
    input: ParseStream,
    allowOmittedBody: Boolean,
): SynResult<ImplItem.Fn?> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }.toMutableList()
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val defaultness = DefaultParse.parse(input).getOrNull()
    val sig = parseSignature(input).getOrElse { return SynResult.failure(it) }

    if (allowOmittedBody && SemiParse.parse(input).isSuccess) {
        return SynResult.success(null)
    }

    val bracesResult = braced(input)
    if (bracesResult.isFailure) return asFailure(bracesResult)
    val bracesVal = bracesResult.getOrThrow()
    parseInner(bracesVal.content, attrs).getOrElse { return SynResult.failure(it) }
    val stmts = mutableListOf<Stmt>()
    while (!bracesVal.content.isEmpty()) {
        val stmtResult = parseStmtFull(bracesVal.content)
        if (stmtResult.isFailure) return asFailure(stmtResult)
        stmts.add(stmtResult.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()

    return SynResult.success(ImplItem.Fn(attrs, vis, defaultness, sig, Block(bracesVal.token, stmts)))
}
