// port-lint: source item.rs
package io.github.kotlinmania.syn

internal object ItemParse : Parse<Item> {
    override fun parse(input: ParseStream): SynResult<Item> {
        val visResult = input.parse(VisibilityParse)
        val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited

        if (input.peek(FnPeek)) {
            val fnToken = input.parse(FnParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = Generics()
            val parens = parenthesized(input)
            if (parens.isFailure) return asFailure(parens)
            val parensVal = parens.getOrThrow()
            val content = parensVal.content
            val inputs = FnArgList()
            while (!content.isEmpty()) {
                val argResult = content.call { parseFnArg(it) }
                if (argResult.isFailure) return asFailure(argResult)
                inputs.pushValue(argResult.getOrThrow())
                if (content.isEmpty()) break
                val c = content.parse(CommaParse)
                if (c.isFailure) break
                inputs.pushPunct(c.getOrThrow())
            }
            content.finishChildBuffer()
            var output: ReturnType = ReturnType.Default
            if (input.peek(RArrowPeek)) {
                val arrowResult = input.parse(RArrowParse)
                if (arrowResult.isSuccess) {
                    val tyResult = parseTypeFull(input)
                    if (tyResult.isSuccess) {
                        output = ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow())
                    }
                }
            }
            val bodyResult =
                if (input.peek(BracePeek)) {
                    braced(input).map { bracesVal ->
                        val stmts = mutableListOf<Stmt>()
                        while (!bracesVal.content.isEmpty()) {
                            val s = bracesVal.content.call { parseStmtFull(it) }
                            if (s.isFailure) break
                            stmts.add(s.getOrThrow())
                        }
                        bracesVal.content.finishChildBuffer()
                        Block(bracesVal.token, stmts)
                    }
                } else {
                    SynResult.success(null)
                }
            if (bodyResult.isFailure) return asFailure(bodyResult)
            return SynResult.success(
                Item.Fn(emptyList(), vis, fnToken, ident, generics, parensVal.token, inputs, output, bodyResult.getOrThrow()),
            )
        }
        if (input.peek(StructPeek)) {
            val structToken = input.parse(StructParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = Generics()
            if (input.peek(BracePeek)) {
                val bracesVal = braced(input).getOrThrow()
                val fields = FieldList()
                while (!bracesVal.content.isEmpty()) {
                    val f = bracesVal.content.call { parseNamedField(it) }
                    if (f.isFailure) break
                    fields.pushValue(f.getOrThrow())
                    if (bracesVal.content.isEmpty()) break
                    val c = bracesVal.content.parse(CommaParse)
                    if (c.isFailure) break
                    fields.pushPunct(c.getOrThrow())
                }
                bracesVal.content.finishChildBuffer()
                return SynResult.success(
                    Item.Struct(
                        emptyList(),
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
                val fields = FieldList()
                while (!parensVal.content.isEmpty()) {
                    val f = parensVal.content.call { parseUnnamedField(it) }
                    if (f.isFailure) break
                    fields.pushValue(f.getOrThrow())
                    if (parensVal.content.isEmpty()) break
                    val c = parensVal.content.parse(CommaParse)
                    if (c.isFailure) break
                    fields.pushPunct(c.getOrThrow())
                }
                parensVal.content.finishChildBuffer()
                val semi = input.parse(SemiParse).getOrNull()
                return SynResult.success(
                    Item.Struct(
                        emptyList(),
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
                    Item.Struct(emptyList(), vis, structToken, ident, generics, Fields.Unit, semi),
                )
            }
            return SynResult.failure(input.error("expected `{`, `(`, or `;`"))
        }
        if (input.peek(EnumPeek)) {
            val enumToken = input.parse(EnumParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = Generics()
            val bracesVal = braced(input).getOrThrow()
            val variants = VariantList()
            while (!bracesVal.content.isEmpty()) {
                val v = bracesVal.content.call { parseVariant(it) }
                if (v.isFailure) break
                variants.pushValue(v.getOrThrow())
                if (bracesVal.content.isEmpty()) break
                val c = bracesVal.content.parse(CommaParse)
                if (c.isFailure) break
                variants.pushPunct(c.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Enum(emptyList(), vis, enumToken, ident, generics, bracesVal.token, variants),
            )
        }
        if (input.peek(TraitPeek)) {
            val traitToken = input.parse(TraitParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val generics = Generics()
            val supertraits = TypeParamBoundList()
            var colonToken: io.github.kotlinmania.syn.token.Colon? = null
            if (input.peek(ColonPeek)) {
                colonToken = input.parse(ColonParse).getOrThrow()
                while (true) {
                    val pathResult = input.parse(PathParse)
                    if (pathResult.isFailure) return asFailure(pathResult)
                    supertraits.pushValue(TypeParamBound.Trait(null, TraitBoundModifier.None, null, pathResult.getOrThrow()))
                    if (!input.peek(PlusPeek)) break
                    val plus = input.parse(PlusParse).getOrThrow()
                    supertraits.pushPunct(plus)
                }
            }
            val bracesVal = braced(input).getOrThrow()
            val items = mutableListOf<TraitItem>()
            while (!bracesVal.content.isEmpty()) {
                val i = bracesVal.content.call { parseTraitItem(it) }
                if (i.isFailure) break
                items.add(i.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Trait(emptyList(), vis, null, null, traitToken, ident, generics, colonToken, supertraits, bracesVal.token, items),
            )
        }
        if (input.peek(ImplPeek)) {
            val implToken = input.parse(ImplParse).getOrThrow()
            val generics = Generics()
            val selfType = parseTypeFull(input)
            if (selfType.isFailure) return asFailure(selfType)
            val bracesVal = braced(input).getOrThrow()
            val items = mutableListOf<ImplItem>()
            while (!bracesVal.content.isEmpty()) {
                val i = bracesVal.content.call { parseImplItem(it) }
                if (i.isFailure) break
                items.add(i.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                Item.Impl(emptyList(), null, null, implToken, generics, null, selfType.getOrThrow(), bracesVal.token, items),
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
                Item.Const(emptyList(), vis, constToken, identResult.getOrThrow(), colonResult.getOrThrow(), ty.getOrThrow(), eqToken, expr, semi),
            )
        }
        if (input.peek(UsePeek)) {
            val useToken = input.parse(UseParse).getOrThrow()
            val treeResult = input.call { parseUseTree(it) }
            if (treeResult.isFailure) return asFailure(treeResult)
            return SynResult.success(
                Item.Use(emptyList(), vis, useToken, treeResult.getOrThrow()),
            )
        }
        if (input.peek(ModPeek)) {
            val modToken = input.parse(ModParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val semiResult = input.parse(SemiParse)
            if (semiResult.isSuccess) {
                return SynResult.success(
                    Item.Mod(emptyList(), vis, modToken, ident, ModContent.Unnamed(semiResult.getOrThrow())),
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
                Item.Mod(emptyList(), vis, modToken, ident, ModContent.Inline(bracesVal.token, items)),
            )
        }
        return SynResult.failure(input.error("expected an item"))
    }
}

private fun <T, R> asFailure(result: SynResult<T>): SynResult<R> =
    SynResult.failure((result as SynResult.Failure).error)

internal fun parseFnArg(input: ParseStream): SynResult<FnArg> {
    if (input.peek(SelfValuePeek)) {
        val selfToken = input.parse(SelfValueParse).getOrThrow()
        return SynResult.success(
            FnArg.Receiver(
                emptyList(),
                null,
                null,
                selfToken,
                null,
                SynType.Infer(
                    io.github.kotlinmania.syn.token.Underscore
                        .default(),
                ),
            ),
        )
    }
    if (input.peek(AndPeek) && input.peek2(SelfValuePeek)) {
        val andToken = input.parse(AndParse).getOrThrow()
        val ltResult = input.parse(LifetimeParse)
        val lifetime = if (ltResult.isSuccess) ltResult.getOrThrow() else null
        val mutResult = input.parse(MutParse)
        val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
        val selfToken = input.parse(SelfValueParse).getOrThrow()
        return SynResult.success(
            FnArg.Receiver(
                emptyList(),
                AndLifetime(andToken, lifetime),
                mutability,
                selfToken,
                null,
                SynType.Infer(
                    io.github.kotlinmania.syn.token.Underscore
                        .default(),
                ),
            ),
        )
    }
    val patResult = input.call { parsePatFull(it) }
    if (patResult.isFailure) return asFailure(patResult)
    val colonResult = input.parse(ColonParse)
    if (colonResult.isFailure) return asFailure(colonResult)
    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(FnArg.Typed(PatType(emptyList(), patResult.getOrThrow(), colonResult.getOrThrow(), tyResult.getOrThrow())))
}

internal fun parseNamedField(input: ParseStream): SynResult<Field> {
    val visResult = input.parse(VisibilityParse)
    val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited
    val identResult = input.parse(IdentParse)
    if (identResult.isFailure) return asFailure(identResult)
    val colonResult = input.parse(ColonParse)
    if (colonResult.isFailure) return asFailure(colonResult)
    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(Field(emptyList(), vis, FieldMutability.None, identResult.getOrThrow(), colonResult.getOrThrow(), tyResult.getOrThrow()))
}

internal fun parseVariant(input: ParseStream): SynResult<Variant> {
    val identResult = input.parse(IdentParse)
    if (identResult.isFailure) return asFailure(identResult)
    val ident = identResult.getOrThrow()
    if (input.peek(BracePeek)) {
        val bracesVal = braced(input).getOrThrow()
        val fields = FieldList()
        while (!bracesVal.content.isEmpty()) {
            val f = bracesVal.content.call { parseNamedField(it) }
            if (f.isFailure) break
            fields.pushValue(f.getOrThrow())
            if (bracesVal.content.isEmpty()) break
            val c = bracesVal.content.parse(CommaParse)
            if (c.isFailure) break
            fields.pushPunct(c.getOrThrow())
        }
        bracesVal.content.finishChildBuffer()
        return SynResult.success(Variant(emptyList(), ident, Fields.Named(FieldsNamed(bracesVal.token, fields)), null))
    }
    if (input.peek(ParenPeek)) {
        val parensVal = parenthesized(input).getOrThrow()
        val fields = FieldList()
        while (!parensVal.content.isEmpty()) {
            val f = parensVal.content.call { parseUnnamedField(it) }
            if (f.isFailure) break
            fields.pushValue(f.getOrThrow())
            if (parensVal.content.isEmpty()) break
            val c = parensVal.content.parse(CommaParse)
            if (c.isFailure) break
            fields.pushPunct(c.getOrThrow())
        }
        parensVal.content.finishChildBuffer()
        return SynResult.success(Variant(emptyList(), ident, Fields.Unnamed(FieldsUnnamed(parensVal.token, fields)), null))
    }
    var discriminant: EqExpr? = null
    if (input.peek(EqPeek)) {
        val eq = input.parse(EqParse).getOrThrow()
        val exprResult = parseExprFull(input)
        if (exprResult.isSuccess) {
            discriminant = EqExpr(eq, exprResult.getOrThrow())
        }
    }
    return SynResult.success(Variant(emptyList(), ident, Fields.Unit, discriminant))
}

internal fun parseUnnamedField(input: ParseStream): SynResult<Field> {
    val visResult = input.parse(VisibilityParse)
    val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited
    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(Field(emptyList(), vis, FieldMutability.None, null, null, tyResult.getOrThrow()))
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
    if (input.peek(FnPeek)) {
        val fnToken = input.parse(FnParse).getOrThrow()
        val ident = input.parse(IdentParse).getOrThrow()
        val parensVal = parenthesized(input).getOrThrow()
        val inputs = FnArgList()
        while (!parensVal.content.isEmpty()) {
            val argResult = parensVal.content.call { parseFnArg(it) }
            if (argResult.isFailure) break
            inputs.pushValue(argResult.getOrThrow())
            if (parensVal.content.isEmpty()) break
            val c = parensVal.content.parse(CommaParse)
            if (c.isFailure) break
            inputs.pushPunct(c.getOrThrow())
        }
        parensVal.content.finishChildBuffer()
        var output: ReturnType = ReturnType.Default
        if (input.peek(RArrowPeek)) {
            val arrowResult = input.parse(RArrowParse)
            if (arrowResult.isSuccess) {
                val tyResult = parseTypeFull(input)
                if (tyResult.isSuccess) {
                    output = ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow())
                }
            }
        }
        val semiResult = input.parse(SemiParse)
        val semi = if (semiResult.isSuccess) semiResult.getOrThrow() else null
        val sig = Signature(null, null, null, null, fnToken, ident, Generics(), parensVal.token, inputs, null, output)
        return SynResult.success(TraitItem.Fn(emptyList(), sig, null, semi))
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
    if (input.peek(FnPeek)) {
        val fnToken = input.parse(FnParse).getOrThrow()
        val ident = input.parse(IdentParse).getOrThrow()
        val parensVal = parenthesized(input).getOrThrow()
        val inputs = FnArgList()
        while (!parensVal.content.isEmpty()) {
            val argResult = parensVal.content.call { parseFnArg(it) }
            if (argResult.isFailure) break
            inputs.pushValue(argResult.getOrThrow())
            if (parensVal.content.isEmpty()) break
            val c = parensVal.content.parse(CommaParse)
            if (c.isFailure) break
            inputs.pushPunct(c.getOrThrow())
        }
        parensVal.content.finishChildBuffer()
        var output: ReturnType = ReturnType.Default
        if (input.peek(RArrowPeek)) {
            val arrowResult = input.parse(RArrowParse)
            if (arrowResult.isSuccess) {
                val tyResult = parseTypeFull(input)
                if (tyResult.isSuccess) {
                    output = ReturnType.TypeReturn(arrowResult.getOrThrow(), tyResult.getOrThrow())
                }
            }
        }
        val bracesVal = braced(input).getOrThrow()
        val stmts = mutableListOf<Stmt>()
        while (!bracesVal.content.isEmpty()) {
            val s = bracesVal.content.call { parseStmtFull(it) }
            if (s.isFailure) break
            stmts.add(s.getOrThrow())
        }
        bracesVal.content.finishChildBuffer()
        val sig = Signature(null, null, null, null, fnToken, ident, Generics(), parensVal.token, inputs, null, output)
        return SynResult.success(ImplItem.Fn(emptyList(), Visibility.Inherited, null, sig, Block(bracesVal.token, stmts)))
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
        val visResult = input.parse(VisibilityParse)
        val vis = if (visResult.isSuccess) visResult.getOrThrow() else Visibility.Inherited
        if (input.peek(StructPeek)) {
            val structToken = input.parse(StructParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            if (input.peek(BracePeek)) {
                val bracesVal = braced(input).getOrThrow()
                val fields = FieldList()
                while (!bracesVal.content.isEmpty()) {
                    val f = bracesVal.content.call { parseNamedField(it) }
                    if (f.isFailure) break
                    fields.pushValue(f.getOrThrow())
                    if (bracesVal.content.isEmpty()) break
                    val c = bracesVal.content.parse(CommaParse)
                    if (c.isFailure) break
                    fields.pushPunct(c.getOrThrow())
                }
                bracesVal.content.finishChildBuffer()
                return SynResult.success(
                    DeriveInput(emptyList(), vis, ident, Generics(), Data.Struct(DataStruct(structToken, Fields.Named(FieldsNamed(bracesVal.token, fields)), null))),
                )
            }
            if (input.peek(ParenPeek)) {
                val parensVal = parenthesized(input).getOrThrow()
                val fields = FieldList()
                while (!parensVal.content.isEmpty()) {
                    val f = parensVal.content.call { parseUnnamedField(it) }
                    if (f.isFailure) break
                    fields.pushValue(f.getOrThrow())
                    if (parensVal.content.isEmpty()) break
                    val c = parensVal.content.parse(CommaParse)
                    if (c.isFailure) break
                    fields.pushPunct(c.getOrThrow())
                }
                parensVal.content.finishChildBuffer()
                val semi = input.parse(SemiParse).getOrThrow()
                return SynResult.success(
                    DeriveInput(emptyList(), vis, ident, Generics(), Data.Struct(DataStruct(structToken, Fields.Unnamed(FieldsUnnamed(parensVal.token, fields)), semi))),
                )
            }
            if (input.peek(SemiPeek)) {
                val semi = input.parse(SemiParse).getOrThrow()
                return SynResult.success(
                    DeriveInput(emptyList(), vis, ident, Generics(), Data.Struct(DataStruct(structToken, Fields.Unit, semi))),
                )
            }
        }
        if (input.peek(EnumPeek)) {
            val enumToken = input.parse(EnumParse).getOrThrow()
            val ident = input.parse(IdentParse).getOrThrow()
            val bracesVal = braced(input).getOrThrow()
            val variants = VariantList()
            while (!bracesVal.content.isEmpty()) {
                val v = bracesVal.content.call { parseVariant(it) }
                if (v.isFailure) break
                variants.pushValue(v.getOrThrow())
                if (bracesVal.content.isEmpty()) break
                val c = bracesVal.content.parse(CommaParse)
                if (c.isFailure) break
                variants.pushPunct(c.getOrThrow())
            }
            bracesVal.content.finishChildBuffer()
            return SynResult.success(
                DeriveInput(
                    emptyList(),
                    vis,
                    ident,
                    Generics(),
                    Data.Enum(DataEnum(enumToken, bracesVal.token, variants)),
                ),
            )
        }
        return SynResult.failure(input.error("expected struct, enum, or union"))
    }
}
