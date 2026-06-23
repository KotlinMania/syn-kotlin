// port-lint: source item.rs
package io.github.kotlinmania.syn

internal object ItemParse : Parse<Item> {
    override fun parse(input: ParseStream): SynResult<Item> {
        val begin = input.fork()
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        val visResult = input.parse(VisibilityParse)
        val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited

        if (peekSignature(input)) {
            val sigResult = parseSignature(input)
            if (sigResult.isFailure) return asFailure(sigResult)
            val bodyResult = parseBlock(input)
            if (bodyResult.isFailure) return asFailure(bodyResult)
            return SynResult.success(
                Item.Fn(attrs, vis, sigResult.getOrThrow(), bodyResult.getOrThrow()),
            )
        }
        if (input.peek(StructPeek)) {
            val structToken = input.parse(StructParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
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
                val semi = input.parse(SemiParse).getOrNull()
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
                val semi = input.parse(SemiParse).getOrThrow()
                return SynResult.success(
                    Item.Struct(attrs, vis, structToken, ident, generics, Fields.Unit, semi),
                )
            }
            return SynResult.failure(input.error("expected `{`, `(`, or `;`"))
        }
        if (input.peek(EnumPeek)) {
            val enumToken = input.parse(EnumParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            generics.whereClause = parseWhereClause(input).getOrNull()
            val bracesVal = braced(input).getOrThrow()
            val variants = parseVariantList(bracesVal.content).getOrElse { return SynResult.failure(it) }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Enum(attrs, vis, enumToken, ident, generics, bracesVal.token, variants),
            )
        }
        if (input.peek(TraitPeek)) {
            val traitToken = input.parse(TraitParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val supertraits = TypeParamBoundList()
            var colonToken: io.github.kotlinmania.syn.token.Colon? = null
            if (input.peek(ColonPeek)) {
                colonToken = input.parse(ColonParse).getOrThrow()
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
                val i = bracesVal.content.call { parseTraitItem(it) }
                if (i.isFailure) break
                items.add(i.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Trait(attrs, vis, null, null, traitToken, ident, generics, colonToken, supertraits, bracesVal.token, items),
            )
        }
        val implAhead = input.fork()
        val defaultness = implAhead.parse(DefaultParse).getOrNull()
        val unsafety = implAhead.parse(UnsafeParse).getOrNull()
        if (implAhead.peek(ImplPeek)) {
            if (vis !is Visibility.Inherited) {
                return parseVerbatimItem(begin, input)
            }
            input.advanceTo(implAhead)
            val implToken = input.parse(ImplParse).getOrThrow()
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val traitPath: PathTrait?
            val selfType: SynType
            if (input.peek(NotPeek)) {
                val polarity = input.parse(NotParse).getOrThrow()
                if (input.peek(BracePeek) || input.peek(WherePeek)) {
                    traitPath = null
                    selfType = SynType.Never(polarity)
                } else {
                    val path = input.parse(PathParse).getOrElse { return SynResult.failure(it) }
                    if (!input.peek(ForPeek)) {
                        return SynResult.failure(input.error("inherent impls cannot be negative"))
                    }
                    val forToken = input.parse(ForParse).getOrThrow()
                    val parsedSelfType = parseTypeFull(input)
                    if (parsedSelfType.isFailure) return asFailure(parsedSelfType)
                    traitPath = PathTrait(polarity, path, forToken)
                    selfType = parsedSelfType.getOrThrow()
                }
            } else {
                val traitAhead = input.fork()
                val pathResult = traitAhead.parse(PathParse)
                if (pathResult.isSuccess && traitAhead.peek(ForPeek)) {
                    input.advanceTo(traitAhead)
                    val forToken = input.parse(ForParse).getOrThrow()
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
                val i = bracesVal.content.call { parseImplItem(it) }
                if (i.isFailure) break
                items.add(i.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Impl(attrs, defaultness, unsafety, implToken, generics, traitPath, selfType, bracesVal.token, items),
            )
        }
        if (input.peek(ConstPeek)) {
            val constToken = input.parse(ConstParse).getOrThrow()
            val identResult = input.parse(IdentParse)
            if (identResult.isFailure) return asFailure(identResult)
            val colonResult = input.parse(ColonParse)
            if (colonResult.isFailure) return asFailure(colonResult)
            val ty = parseTypeFull(input)
            if (ty.isFailure) return asFailure(ty)
            val eqResult = input.parse(EqParse)
            val eqToken = if (eqResult.isSuccess) eqResult.getOrThrow() else null
            var expr: Expr? = null
            if (eqToken != null) {
                val exprResult = parseExprFull(input)
                if (exprResult.isSuccess) expr = exprResult.getOrThrow()
            }
            val semi = input.parse(SemiParse).getOrThrow()
            return SynResult.success(
                Item.Const(attrs, vis, constToken, identResult.getOrThrow(), colonResult.getOrThrow(), ty.getOrThrow(), eqToken, expr, semi),
            )
        }
        if (input.peek(UsePeek)) {
            val useToken = input.parse(UseParse).getOrThrow()
            val treeResult = input.call { parseUseTree(it) }
            if (treeResult.isFailure) return asFailure(treeResult)
            return SynResult.success(
                Item.Use(attrs, vis, useToken, treeResult.getOrThrow()),
            )
        }
        if (input.peek(ModPeek)) {
            val modToken = input.parse(ModParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val semiResult = input.parse(SemiParse)
            if (semiResult.isSuccess) {
                return SynResult.success(
                    Item.Mod(attrs, vis, modToken, ident, ModContent.Unnamed(semiResult.getOrThrow())),
                )
            }
            val bracesVal = braced(input).getOrThrow()
            val items = mutableListOf<Item>()
            while (!bracesVal.content.isEmpty()) {
                val i = bracesVal.content.call { ItemParse.parse(it) }
                if (i.isFailure) break
                items.add(i.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Mod(attrs, vis, modToken, ident, ModContent.Inline(bracesVal.token, items)),
            )
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
}

private fun parseVerbatimItem(
    begin: ParseStream,
    input: ParseStream,
): SynResult<Item> {
    input.parse(TokenStreamParse).getOrElse { return SynResult.failure(it) }
    val tokens = begin.parse(TokenStreamParse).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Item.Verbatim(tokens))
}

private fun parseItemMacro(input: ParseStream, attrs: List<Attribute>): SynResult<Item> {
    val path = parseModStylePath(input).getOrElse { return SynResult.failure(it) }
    val bangToken = input.parse(NotParse).getOrElse { return SynResult.failure(it) }
    val ident = input.parse(IdentParse).getOrNull()
    val delimiterResult = parseDelimiter(input)
    if (delimiterResult.isFailure) return asFailure(delimiterResult)
    val (delimiter, tokens) = delimiterResult.getOrThrow()
    val semiToken =
        if (delimiter.isBrace) {
            null
        } else {
            input.parse(SemiParse).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(
        Item.Macro(attrs, ident, Macro(path, bangToken, delimiter, tokens), semiToken),
    )
}

private fun <T, R> asFailure(result: SynResult<T>): SynResult<R> =
    SynResult.failure((result as SynResult.Failure).error)

internal fun peekSignature(input: ParseStream): Boolean {
    val fork = input.fork()
    fork.parse(ConstParse)
    fork.parse(AsyncParse)
    fork.parse(UnsafeParse)
    parseAbi(fork)
    return fork.peek(FnPeek)
}

private fun parseSignature(input: ParseStream): SynResult<Signature> {
    val constness = input.parse(ConstParse).getOrNull()
    val asyncness = input.parse(AsyncParse).getOrNull()
    val unsafety = input.parse(UnsafeParse).getOrNull()
    val abi = parseAbi(input).getOrNull()
    val fnTokenResult = input.parse(FnParse)
    if (fnTokenResult.isFailure) return asFailure(fnTokenResult)
    val identResult = input.parse(IdentParse)
    if (identResult.isFailure) return asFailure(identResult)
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val parensResult = parenthesized(input)
    if (parensResult.isFailure) return asFailure(parensResult)
    val parensVal = parensResult.getOrThrow()
    val inputsResult = parseFnArgList(parensVal.content)
    if (inputsResult.isFailure) return asFailure(inputsResult)
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
            inputsResult.getOrThrow(),
            null,
            outputResult.getOrThrow(),
        ),
    )
}

internal fun parseAbi(input: ParseStream): SynResult<Abi> {
    val externResult = input.parse(ExternParse)
    if (externResult.isFailure) return asFailure(externResult)
    val name = input.parse(LitStrParse).getOrNull()
    return SynResult.success(Abi(externResult.getOrThrow(), name))
}

private fun parseFnArgList(content: ParseStream): SynResult<FnArgList> {
    val inputs = FnArgList()
    var hasReceiver = false
    while (!content.isEmpty()) {
        val argResult = content.call { parseFnArg(it) }
        if (argResult.isFailure) return asFailure(argResult)
        val arg = argResult.getOrThrow()
        if (arg is FnArg.Receiver) {
            if (hasReceiver) return SynResult.failure(content.error("unexpected second method receiver"))
            if (!inputs.isEmpty()) return SynResult.failure(content.error("unexpected method receiver"))
            hasReceiver = true
        }
        inputs.pushValue(arg)
        if (content.isEmpty()) break
        val commaResult = content.parse(CommaParse)
        if (commaResult.isFailure) return asFailure(commaResult)
        inputs.pushPunct(commaResult.getOrThrow())
    }
    return SynResult.success(inputs)
}

internal fun parseReturnType(input: ParseStream): SynResult<ReturnType> {
    if (!input.peek(RArrowPeek)) {
        return SynResult.success(ReturnType.Default)
    }
    val arrowResult = input.parse(RArrowParse)
    if (arrowResult.isFailure) return asFailure(arrowResult)
    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow()))
}

private fun parseBlock(input: ParseStream): SynResult<Block> {
    val bracesResult = braced(input)
    if (bracesResult.isFailure) return asFailure(bracesResult)
    val bracesVal = bracesResult.getOrThrow()
    val stmts = mutableListOf<Stmt>()
    while (!bracesVal.content.isEmpty()) {
        val stmtResult = bracesVal.content.call { parseStmtFull(it) }
        if (stmtResult.isFailure) return asFailure(stmtResult)
        stmts.add(stmtResult.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Block(bracesVal.token, stmts))
}

internal fun parseFnArg(input: ParseStream): SynResult<FnArg> {
    val ahead = input.fork()
    val receiverResult = parseReceiver(ahead)
    if (receiverResult.isSuccess) {
        input.advanceTo(ahead)
        return SynResult.success(receiverResult.getOrThrow())
    }
    val patResult = input.call { parsePatFull(it) }
    if (patResult.isFailure) return asFailure(patResult)
    val colonResult = input.parse(ColonParse)
    if (colonResult.isFailure) return asFailure(colonResult)
    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(FnArg.Typed(PatType(emptyList(), patResult.getOrThrow(), colonResult.getOrThrow(), tyResult.getOrThrow())))
}

private fun parseReceiver(input: ParseStream): SynResult<FnArg.Receiver> {
    val referenceResult = parseReceiverAnd(input)
    val reference =
        if (referenceResult.isSuccess) {
            val andToken = referenceResult.getOrThrow()
            val lifetime = input.parse(LifetimeParse).getOrNull()
            AndLifetime(andToken, lifetime)
        } else {
            null
        }
    val mutability = input.parse(MutParse).getOrNull()
    val selfTokenResult = input.parse(SelfValueParse)
    if (selfTokenResult.isFailure) return asFailure(selfTokenResult)
    val selfToken = selfTokenResult.getOrThrow()
    val colonToken =
        if (reference == null) {
            input.parse(ColonParse).getOrNull()
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
        SynResult.success(io.github.kotlinmania.syn.token.And.from(punct.span()) to rest)
    }

internal fun parseGenerics(input: ParseStream): SynResult<Generics> {
    if (!input.peek(GenericsLtPeek)) return SynResult.success(Generics())

    val ltToken = input.parse(GenericsLtParse).getOrElse { return SynResult.failure(it) }
    val params = GenericParamList()
    while (!input.isEmpty() && !input.peek(GenericsGtPeek)) {
        val param = parseGenericParam(input).getOrElse { return SynResult.failure(it) }
        params.pushValue(param)
        if (input.peek(GenericsGtPeek)) break
        val comma = input.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        params.pushPunct(comma)
        if (input.peek(GenericsGtPeek)) break
    }
    val gtToken = input.parse(GenericsGtParse).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Generics(ltToken, params, gtToken))
}

private object GenericsLtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '<'
    }

    override fun display(): String = "`<`"
}

private object GenericsLtParse : Parse<io.github.kotlinmania.syn.token.Lt> {
    override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Lt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<`"))
            if (punct.asChar() != '<') return@step SynResult.failure(cursor.error("expected `<`"))
            SynResult.success(io.github.kotlinmania.syn.token.Lt.from(punct.span()) to rest)
        }
}

private object GenericsGtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '>'
    }

    override fun display(): String = "`>`"
}

private object GenericsGtParse : Parse<io.github.kotlinmania.syn.token.Gt> {
    override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Gt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>`"))
            if (punct.asChar() != '>') return@step SynResult.failure(cursor.error("expected `>`"))
            SynResult.success(io.github.kotlinmania.syn.token.Gt.from(punct.span()) to rest)
        }
}

public object GenericParamParse : Parse<GenericParam> {
    override fun parse(input: ParseStream): SynResult<GenericParam> =
        parseGenericParam(input)
}

private fun parseGenericParam(input: ParseStream): SynResult<GenericParam> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(ConstPeek)) {
        val constToken = input.parse(ConstParse).getOrThrow()
        val ident = input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
        val colon = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        val eqToken = input.parse(EqParse).getOrNull()
        val default =
            if (eqToken != null) {
                parseExprFull(input).getOrElse { return SynResult.failure(it) }
            } else {
                null
            }
        return SynResult.success(GenericParam.ConstParam(attrs, constToken, ident, colon, ty, eqToken, default))
    }

    val lifetime = input.parse(LifetimeParse).getOrNull()
    if (lifetime != null) {
        val colon = input.parse(ColonParse).getOrNull()
        val bounds = LifetimeList()
        while (colon != null && !input.peek(CommaPeek) && !input.peek(GenericsGtPeek)) {
            val bound = input.parse(LifetimeParse).getOrElse { return SynResult.failure(it) }
            bounds.pushValue(bound)
            if (!input.peek(PlusPeek)) break
            bounds.pushPunct(input.parse(PlusParse).getOrThrow())
        }
        return SynResult.success(GenericParam.LifetimeParam(attrs, lifetime, colon, bounds))
    }

    val ident = input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
    val colon = input.parse(ColonParse).getOrNull()
    val bounds =
        if (colon != null) {
            parseTypeParamBounds(input, stopAtEq = true).getOrElse { return SynResult.failure(it) }
        } else {
            TypeParamBoundList()
        }
    val eqToken = input.parse(EqParse).getOrNull()
    val default =
        if (eqToken != null) {
            parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    return SynResult.success(GenericParam.TypeParam(attrs, ident, colon, bounds, eqToken, default))
}

public object WhereClauseParse : Parse<WhereClause> {
    override fun parse(input: ParseStream): SynResult<WhereClause> =
        parseWhereClause(input)
}

internal fun parseWhereClause(input: ParseStream): SynResult<WhereClause> {
    if (!input.peek(WherePeek)) return SynResult.failure(input.error("expected `where`"))
    val whereToken = input.parse(WhereParse).getOrThrow()
    val predicates = WherePredicateList()
    while (!input.isEmpty() && !input.peek(BracePeek) && !input.peek(SemiPeek)) {
        val predicate = parseWherePredicate(input).getOrElse { return SynResult.failure(it) }
        predicates.pushValue(predicate)
        if (!input.peek(CommaPeek)) break
        predicates.pushPunct(input.parse(CommaParse).getOrThrow())
        if (input.isEmpty() || input.peek(BracePeek) || input.peek(SemiPeek)) break
    }
    return SynResult.success(WhereClause(whereToken, predicates))
}

private fun parseWherePredicate(input: ParseStream): SynResult<WherePredicate> {
    val lifetime = input.parse(LifetimeParse).getOrNull()
    if (lifetime != null) {
        val colon = input.parse(ColonParse).getOrNull()
        val bounds = LifetimeList()
        while (colon != null && !input.peek(CommaPeek) && !input.peek(BracePeek) && !input.peek(SemiPeek)) {
            val bound = input.parse(LifetimeParse).getOrElse { return SynResult.failure(it) }
            bounds.pushValue(bound)
            if (!input.peek(PlusPeek)) break
            bounds.pushPunct(input.parse(PlusParse).getOrThrow())
        }
        return SynResult.success(WherePredicate.LifetimePredicate(lifetime, colon, bounds))
    }

    val boundedTy = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val colon = input.parse(ColonParse).getOrElse { return SynResult.failure(it) }
    val bounds = parseTypeParamBounds(input, stopAtEq = false).getOrElse { return SynResult.failure(it) }
    return SynResult.success(WherePredicate.TypePredicate(boundedTy, colon, bounds))
}

internal fun parseTypeParamBounds(
    input: ParseStream,
    stopAtEq: Boolean,
    allowPreciseCapture: Boolean = false,
): SynResult<TypeParamBoundList> {
    val bounds = TypeParamBoundList()
    while (!input.isEmpty() && !input.peek(CommaPeek) && !input.peek(GenericsGtPeek) && !input.peek(BracePeek) && !input.peek(WherePeek) && !input.peek(SemiPeek) && !(stopAtEq && input.peek(EqPeek))) {
        val bound = parseTypeParamBound(input, allowPreciseCapture).getOrElse { return SynResult.failure(it) }
        bounds.pushValue(bound)
        if (!input.peek(PlusPeek)) break
        bounds.pushPunct(input.parse(PlusParse).getOrThrow())
    }
    return SynResult.success(bounds)
}

private fun parseTypeParamBound(
    input: ParseStream,
    allowPreciseCapture: Boolean,
): SynResult<TypeParamBound> {
    if (input.peek(LifetimePeek)) {
        return SynResult.success(TypeParamBound.LifetimeBound(input.parse(LifetimeParse).getOrThrow()))
    }
    if (input.peek(UsePeek)) {
        val preciseCapture = parsePreciseCapture(input).getOrElse { return SynResult.failure(it) }
        if (allowPreciseCapture) {
            return SynResult.success(preciseCapture)
        }
        return SynResult.failure(input.error("`use<...>` precise capturing syntax is not allowed here"))
    }
    val lifetimesBeforeModifier =
        if (input.peek(ForPeek)) {
            parseBoundLifetimes(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }
    val modifier =
        if (input.peek(QuestionPeek)) {
            TraitBoundModifier.Maybe(input.parse(QuestionParse).getOrThrow())
        } else {
            TraitBoundModifier.None
        }
    if (lifetimesBeforeModifier != null && modifier is TraitBoundModifier.Maybe) {
        return SynResult.failure(input.error("`for<...>` binder not allowed with `?` trait polarity modifier"))
    }
    val lifetimesAfterModifier =
        if (input.peek(ForPeek)) {
            if (modifier is TraitBoundModifier.Maybe) {
                return SynResult.failure(input.error("`for<...>` binder not allowed with `?` trait polarity modifier"))
            }
            parseBoundLifetimes(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
    }
    val lifetimes = lifetimesBeforeModifier ?: lifetimesAfterModifier
    val path = input.parse(PathParse).getOrElse { return SynResult.failure(it) }
    val last = path.segments.last()
    if (last != null &&
        last.arguments.isEmpty() &&
        (input.peek(ParenPeek) || (input.peek(PathSepPeek) && input.peek3(ParenPeek)))
    ) {
        if (input.peek(PathSepPeek)) {
            input.parse(PathSepParse).getOrElse { return SynResult.failure(it) }
        }
        last.arguments = parseParenthesizedPathArguments(input).getOrElse { return SynResult.failure(it) }
    }
    return SynResult.success(TypeParamBound.Trait(null, modifier, lifetimes, path))
}

internal fun parseBoundLifetimes(input: ParseStream): SynResult<BoundLifetimes> {
    val forToken = input.parse(ForParse).getOrElse { return SynResult.failure(it) }
    val ltToken = input.parse(GenericsLtParse).getOrElse { return SynResult.failure(it) }
    val lifetimes = GenericParamList()
    while (!input.isEmpty() && !input.peek(GenericsGtPeek)) {
        val param = parseGenericParam(input).getOrElse { return SynResult.failure(it) }
        if (param !is GenericParam.LifetimeParam) {
            return SynResult.failure(input.error("expected lifetime parameter"))
        }
        lifetimes.pushValue(param)
        if (input.peek(GenericsGtPeek)) break
        val comma = input.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        lifetimes.pushPunct(comma)
    }
    val gtToken = input.parse(GenericsGtParse).getOrElse { return SynResult.failure(it) }
    return SynResult.success(BoundLifetimes(forToken, ltToken, lifetimes, gtToken))
}

public object TypeParamBoundParse : Parse<TypeParamBound> {
    override fun parse(input: ParseStream): SynResult<TypeParamBound> =
        parseTypeParamBound(input, allowPreciseCapture = true)
}

private fun parsePreciseCapture(input: ParseStream): SynResult<TypeParamBound.PreciseCapture> {
    val useToken = input.parse(UseParse).getOrElse { return SynResult.failure(it) }
    val ltToken = input.parse(GenericsLtParse).getOrElse { return SynResult.failure(it) }
    val params = CapturedParamList()
    while (!input.peek(GenericsGtPeek)) {
        val param = parseCapturedParam(input).getOrElse { return SynResult.failure(it) }
        params.pushValue(param)
        if (input.peek(CommaPeek)) {
            params.pushPunct(input.parse(CommaParse).getOrThrow())
            if (input.peek(GenericsGtPeek)) break
        } else if (!input.peek(GenericsGtPeek)) {
            return SynResult.failure(input.error("expected `,` or `>`"))
        }
    }
    val gtToken = input.parse(GenericsGtParse).getOrElse { return SynResult.failure(it) }
    return SynResult.success(TypeParamBound.PreciseCapture(useToken, ltToken, params, gtToken))
}

private fun parseCapturedParam(input: ParseStream): SynResult<CapturedParam> {
    if (input.peek(LifetimePeek)) {
        return SynResult.success(CapturedParam.Lifetime(input.parse(LifetimeParse).getOrThrow()))
    }
    val ident =
        if (input.peek(SelfTypePeek)) {
            identFromSelfType(input.parse(SelfTypeParse).getOrThrow())
        } else {
            identParseAny(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(CapturedParam.Ident(ident))
}

internal fun parseNamedFieldList(input: ParseStream): SynResult<FieldList> {
    val fields = FieldList()
    while (!input.isEmpty()) {
        val field = input.call { parseNamedField(it) }.getOrElse { return SynResult.failure(it) }
        fields.pushValue(field)
        if (input.isEmpty()) break
        val comma = input.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        fields.pushPunct(comma)
    }
    return SynResult.success(fields)
}

internal fun parseUnnamedFieldList(input: ParseStream): SynResult<FieldList> {
    val fields = FieldList()
    while (!input.isEmpty()) {
        val field = input.call { parseUnnamedField(it) }.getOrElse { return SynResult.failure(it) }
        fields.pushValue(field)
        if (input.isEmpty()) break
        val comma = input.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        fields.pushPunct(comma)
    }
    return SynResult.success(fields)
}

internal fun parseVariantList(input: ParseStream): SynResult<VariantList> {
    val variants = VariantList()
    while (!input.isEmpty()) {
        val variant = input.call { parseVariant(it) }.getOrElse { return SynResult.failure(it) }
        variants.pushValue(variant)
        if (input.isEmpty()) break
        val comma = input.parse(CommaParse).getOrElse { return SynResult.failure(it) }
        variants.pushPunct(comma)
    }
    return SynResult.success(variants)
}

internal fun parseNamedField(input: ParseStream): SynResult<Field> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val visResult = input.parse(VisibilityParse)
    val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited
    val identResult = input.parse(IdentParse)
    if (identResult.isFailure) return asFailure(identResult)
    val colonResult = input.parse(ColonParse)
    if (colonResult.isFailure) return asFailure(colonResult)
    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(Field(attrs, vis, FieldMutability.None, identResult.getOrThrow(), colonResult.getOrThrow(), tyResult.getOrThrow()))
}

internal fun parseVariant(input: ParseStream): SynResult<Variant> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val identResult = input.parse(IdentParse)
    if (identResult.isFailure) return asFailure(identResult)
    val ident = identResult.getOrThrow()
    if (input.peek(BracePeek)) {
        val bracesVal = braced(input).getOrThrow()
        val fields = parseNamedFieldList(bracesVal.content).getOrElse { return SynResult.failure(it) }
        bracesVal.content.finishChildBuffer()
        return SynResult.success(Variant(attrs, ident, Fields.Named(FieldsNamed(bracesVal.token, fields)), null))
    }
    if (input.peek(ParenPeek)) {
        val parensVal = parenthesized(input).getOrThrow()
        val fields = parseUnnamedFieldList(parensVal.content).getOrElse { return SynResult.failure(it) }
        parensVal.content.finishChildBuffer()
        return SynResult.success(Variant(attrs, ident, Fields.Unnamed(FieldsUnnamed(parensVal.token, fields)), null))
    }
    var discriminant: EqExpr? = null
    if (input.peek(EqPeek)) {
        val eq = input.parse(EqParse).getOrThrow()
        val exprResult = parseExprFull(input)
        if (exprResult.isSuccess) {
            discriminant = EqExpr(eq, exprResult.getOrThrow())
        }
    }
    return SynResult.success(Variant(attrs, ident, Fields.Unit, discriminant))
}

internal fun parseUnnamedField(input: ParseStream): SynResult<Field> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val visResult = input.parse(VisibilityParse)
    val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited
    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(Field(attrs, vis, FieldMutability.None, null, null, tyResult.getOrThrow()))
}

internal fun parseUseTree(input: ParseStream): SynResult<UseTree> {
    val pathResult = input.parse(PathParse)
    if (pathResult.isFailure) return asFailure(pathResult)
    val path = pathResult.getOrThrow()
    val lastSeg = path.segments.last()
    if (input.peek(PathSepPeek)) {
        val colon2 = input.parse(PathSepParse).getOrThrow()
        if (input.peek(BracePeek)) {
            val bracesVal = braced(input).getOrThrow()
            val items = UseTreeList()
            while (!bracesVal.content.isEmpty()) {
                val t = bracesVal.content.call { parseUseTree(it) }
                if (t.isFailure) break
                items.pushValue(t.getOrThrow())
                if (bracesVal.content.isEmpty()) break
                val c = bracesVal.content.parse(CommaParse)
                if (c.isFailure) break
                items.pushPunct(c.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            val prefix = pathWithoutLast(path)
            return SynResult.success(
                UseTree.Path(prefix.ident, prefix.colon2Token, UseTree.Group(bracesVal.token, items)),
            )
        }
        if (input.peek(StarPeek)) {
            val star = input.parse(StarParse).getOrThrow()
            val prefix = pathWithoutLast(path)
            return SynResult.success(
                UseTree.Path(prefix.ident, prefix.colon2Token, UseTree.Glob(star)),
            )
        }
        val prefix = pathWithoutLast(path)
        return SynResult.success(UseTree.Path(prefix.ident, prefix.colon2Token, null))
    }
    val ident =
        lastSeg?.ident
            ?: return SynResult.failure(input.error("expected use tree"))
    return SynResult.success(UseTree.Path(ident, null, null))
}

private data class PathPrefix(
    val ident: Ident,
    val colon2Token: io.github.kotlinmania.syn.token.PathSep?,
)

private fun pathWithoutLast(path: Path): PathPrefix {
    if (path.segments.trailingPunct()) path.segments.popPunctRaw()
    if (!path.segments.emptyOrTrailing()) path.segments.popPunctRaw()
    val last = path.segments.popRaw() as PathSegment
    return PathPrefix(last.ident, path.leadingColon)
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
        val semiResult = input.parse(SemiParse)
        if (semiResult.isFailure) return asFailure(semiResult)
        return SynResult.success(TraitItem.Fn(emptyList(), sigResult.getOrThrow(), null, semiResult.getOrThrow()))
    }
    if (input.peek(ConstPeek)) {
        val constToken = input.parse(ConstParse).getOrThrow()
        val ident = input.parse(IdentParse).getOrThrow()
        val colon = input.parse(ColonParse).getOrThrow()
        val ty = parseTypeFull(input).getOrThrow()
        var default: EqExpr? = null
        if (input.peek(EqPeek)) {
            val eq = input.parse(EqParse).getOrThrow()
            val exprResult = parseExprFull(input)
            if (exprResult.isSuccess) {
                default = EqExpr(eq, exprResult.getOrThrow())
            }
        }
        val semi = input.parse(SemiParse).getOrThrow()
        return SynResult.success(TraitItem.Const(emptyList(), constToken, ident, Generics(), colon, ty, default, semi))
    }
    if (input.peek(SynTypePeek)) {
        val typeToken = input.parse(SynTypeParse).getOrThrow()
        val ident = input.parse(IdentParse).getOrElse { return SynResult.failure(it) }
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        val colon = input.parse(ColonParse).getOrNull()
        val bounds =
            if (colon != null) {
                parseTypeParamBounds(input, stopAtEq = true).getOrElse { return SynResult.failure(it) }
            } else {
                TypeParamBoundList()
            }
        generics.whereClause = parseWhereClause(input).getOrNull()
        val eqToken = input.parse(EqParse).getOrNull()
        val default =
            if (eqToken != null) {
                EqSynType(eqToken, parseTypeFull(input).getOrElse { return SynResult.failure(it) })
            } else {
                null
            }
        val semi = input.parse(SemiParse).getOrElse { return SynResult.failure(it) }
        return SynResult.success(TraitItem.AssocType(emptyList(), typeToken, ident, generics, colon, bounds, default, semi))
    }
    if (input.peek(SemiPeek)) {
        input.parse(SemiParse).getOrThrow()
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
    if (peekSignature(input)) {
        val sigResult = parseSignature(input)
        if (sigResult.isFailure) return asFailure(sigResult)
        val blockResult = parseBlock(input)
        if (blockResult.isFailure) return asFailure(blockResult)
        return SynResult.success(ImplItem.Fn(emptyList(), Visibility.Inherited, null, sigResult.getOrThrow(), blockResult.getOrThrow()))
    }
    if (input.peek(ConstPeek)) {
        val constToken = input.parse(ConstParse).getOrThrow()
        val ident = input.parse(IdentParse).getOrThrow()
        val colon = input.parse(ColonParse).getOrThrow()
        val ty = parseTypeFull(input).getOrThrow()
        val eq = input.parse(EqParse).getOrThrow()
        val expr = parseExprFull(input).getOrThrow()
        val semi = input.parse(SemiParse).getOrThrow()
        return SynResult.success(ImplItem.Const(emptyList(), Visibility.Inherited, null, constToken, ident, Generics(), colon, ty, eq, expr, semi))
    }
    if (input.peek(SemiPeek)) {
        input.parse(SemiParse).getOrThrow()
        return SynResult.success(
            ImplItem.Verbatim(
                io.github.kotlinmania.procmacro2.TokenStream
                    .new(),
            ),
        )
    }
    return SynResult.failure(input.error("expected impl item"))
}

internal object DeriveInputParseImpl : Parse<DeriveInput> {
    override fun parse(input: ParseStream): SynResult<DeriveInput> {
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        val visResult = input.parse(VisibilityParse)
        val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited
        if (input.peek(StructPeek)) {
            val structToken = input.parse(StructParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            generics.whereClause = parseWhereClause(input).getOrNull()
            if (input.peek(BracePeek)) {
                val bracesVal = braced(input).getOrThrow()
                val fields = parseNamedFieldList(bracesVal.content).getOrElse { return SynResult.failure(it) }
                bracesVal.content.finishChildBuffer()
                return SynResult.success(
                    DeriveInput(attrs, vis, ident, generics, Data.Struct(DataStruct(structToken, Fields.Named(FieldsNamed(bracesVal.token, fields)), null))),
                )
            }
            if (input.peek(ParenPeek)) {
                val parensVal = parenthesized(input).getOrThrow()
                val fields = parseUnnamedFieldList(parensVal.content).getOrElse { return SynResult.failure(it) }
                parensVal.content.finishChildBuffer()
                val semi = input.parse(SemiParse).getOrThrow()
                return SynResult.success(
                    DeriveInput(attrs, vis, ident, generics, Data.Struct(DataStruct(structToken, Fields.Unnamed(FieldsUnnamed(parensVal.token, fields)), semi))),
                )
            }
            if (input.peek(SemiPeek)) {
                val semi = input.parse(SemiParse).getOrThrow()
                return SynResult.success(
                    DeriveInput(attrs, vis, ident, generics, Data.Struct(DataStruct(structToken, Fields.Unit, semi))),
                )
            }
        }
        if (input.peek(EnumPeek)) {
            val enumToken = input.parse(EnumParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            generics.whereClause = parseWhereClause(input).getOrNull()
            val bracesVal = braced(input).getOrThrow()
            val variants = parseVariantList(bracesVal.content).getOrElse { return SynResult.failure(it) }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                DeriveInput(
                    attrs,
                    vis,
                    ident,
                    generics,
                    Data.Enum(DataEnum(enumToken, bracesVal.token, variants)),
                ),
            )
        }
        if (input.peek(UnionPeek)) {
            val unionToken = input.parse(UnionParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            generics.whereClause = parseWhereClause(input).getOrNull()
            val bracesVal = braced(input).getOrThrow()
            val fields = parseNamedFieldList(bracesVal.content).getOrElse { return SynResult.failure(it) }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                DeriveInput(
                    attrs,
                    vis,
                    ident,
                    generics,
                    Data.Union(DataUnion(unionToken, FieldsNamed(bracesVal.token, fields))),
                ),
            )
        }
        return SynResult.failure(input.error("expected struct, enum, or union"))
    }
}
