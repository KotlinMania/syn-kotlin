// port-lint: tests tests/test_generics.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenericsTest {
    @Test
    fun testSplitForImpl() {
        val input =
            parseStr(
                DeriveInputParse::parse,
                "struct S<'a, 'b: 'a, #[may_dangle] T: 'a = ()> where T: Debug;",
            ).getOrThrow()

        val generics = input.generics
        assertEquals("S", input.ident.toString())
        assertNotNull(generics.ltToken)
        assertNotNull(generics.gtToken)
        val params = generics.params.toList()
        assertEquals(3, params.size)

        val a = assertIs<GenericParam.LifetimeParam>(params[0])
        assertEquals("'a", a.lifetime.toString())
        assertNull(a.colonToken)

        val b = assertIs<GenericParam.LifetimeParam>(params[1])
        assertEquals("'b", b.lifetime.toString())
        assertNotNull(b.colonToken)
        assertEquals(listOf("'a"), b.bounds.toList().map { it.toString() })

        val t = assertIs<GenericParam.TypeParam>(params[2])
        assertEquals(
            "may_dangle",
            t.attrs
                .single()
                .path()
                .toString(),
        )
        assertEquals("T", t.ident.toString())
        assertNotNull(t.colonToken)
        assertEquals("'a", assertIs<TypeParamBound.LifetimeBound>(t.bounds.toList().single()).lifetime.toString())
        assertNotNull(t.eqToken)
        assertIs<SynType.Tuple>(t.default)

        val whereClause = assertNotNull(generics.whereClause)
        val predicate = assertIs<WherePredicate.TypePredicate>(whereClause.predicates.toList().single())
        assertPathType(predicate.boundedTy, "T")
        assertEquals("Debug", assertIs<TypeParamBound.Trait>(predicate.bounds.toList().single()).path.toString())

        val split = generics.splitForImpl()
        assertEquals(whereClause, split.whereClause)
        val implParams = split.implGenerics.params.toList()
        assertEquals(3, implParams.size)
        assertNotNull(assertIs<GenericParam.LifetimeParam>(implParams[1]).colonToken)
        val implType = assertIs<GenericParam.TypeParam>(implParams[2])
        assertEquals("T", implType.ident.toString())
        assertNotNull(implType.colonToken)
        assertNull(implType.eqToken)
        assertNull(implType.default)

        val typeParams = split.typeGenerics.params.toList()
        assertEquals(listOf("'a", "'b"), typeParams.filterIsInstance<GenericParam.LifetimeParam>().map { it.lifetime.toString() })
        val typeOnlyT = assertIs<GenericParam.TypeParam>(typeParams[2])
        assertEquals("T", typeOnlyT.ident.toString())
        assertNull(typeOnlyT.colonToken)
        assertTrue(typeOnlyT.bounds.isEmpty())

        val turbofishArgs =
            split.typeGenerics
                .asTurbofish()
                .params
                .toList()
        assertEquals(3, turbofishArgs.size)
        assertEquals("'a", assertIs<GenericArgument.LifetimeArg>(turbofishArgs[0]).lifetime.toString())
        assertEquals("'b", assertIs<GenericArgument.LifetimeArg>(turbofishArgs[1]).lifetime.toString())
        assertPathType(assertIs<GenericArgument.TypeArg>(turbofishArgs[2]).type, "T")
    }

    @Test
    fun testTypeParamBound() {
        val lifetime = assertIs<TypeParamBound.LifetimeBound>(parseStr(TypeParamBoundParse::parse, "'a").getOrThrow())
        assertEquals("'a", lifetime.lifetime.toString())

        val inferred = assertIs<TypeParamBound.LifetimeBound>(parseStr(TypeParamBoundParse::parse, "'_").getOrThrow())
        assertEquals("'_", inferred.lifetime.toString())

        val debug = assertIs<TypeParamBound.Trait>(parseStr(TypeParamBoundParse::parse, "Debug").getOrThrow())
        assertIs<TraitBoundModifier.None>(debug.modifier)
        assertEquals("Debug", debug.path.toString())

        val sized = assertIs<TypeParamBound.Trait>(parseStr(TypeParamBoundParse::parse, "?Sized").getOrThrow())
        assertIs<TraitBoundModifier.Maybe>(sized.modifier)
        assertEquals("Sized", sized.path.toString())

        val bounded = assertIs<TypeParamBound.Trait>(parseStr(TypeParamBoundParse::parse, "for<'a> Trait").getOrThrow())
        val boundLifetimes = assertNotNull(bounded.lifetimes)
        val boundParams = boundLifetimes.lifetimes.toList()
        assertEquals(1, boundParams.size)
        assertEquals("'a", assertIs<GenericParam.LifetimeParam>(boundParams.single()).lifetime.toString())
        assertEquals("Trait", bounded.path.toString())

        val forThenMaybe = parseStr(TypeParamBoundParse::parse, "for<> ?Trait")
        assertTrue(forThenMaybe.isFailure)
        assertEquals(
            "`for<...>` binder not allowed with `?` trait polarity modifier",
            (forThenMaybe as SynResult.Failure).error.toString(),
        )

        val maybeThenFor = parseStr(TypeParamBoundParse::parse, "?for<> Trait")
        assertTrue(maybeThenFor.isFailure)
        assertEquals(
            "`for<...>` binder not allowed with `?` trait polarity modifier",
            (maybeThenFor as SynResult.Failure).error.toString(),
        )
    }

    @Test
    fun testFnPrecedenceInWhereClause() {
        val item =
            assertIs<Item.Fn>(
                parseStr(
                    ItemParse::parse,
                    """
                    fn f<G>()
                    where
                        G: FnOnce() -> i32 + Send,
                    {
                    }
                    """.trimIndent(),
                ).getOrThrow(),
            )

        assertEquals("f", item.sig.ident.toString())
        val params =
            item.sig.generics.params
                .toList()
        assertEquals(1, params.size)
        assertEquals("G", assertIs<GenericParam.TypeParam>(params.single()).ident.toString())

        val whereClause = assertNotNull(item.sig.generics.whereClause)
        val predicate = assertIs<WherePredicate.TypePredicate>(whereClause.predicates.toList().single())
        assertPathType(predicate.boundedTy, "G")
        val bounds = predicate.bounds.toList()
        assertEquals(2, bounds.size)

        val fnOnce = assertIs<TypeParamBound.Trait>(bounds[0])
        val fnOnceSegment =
            fnOnce.path.segments
                .toList()
                .single()
        assertEquals("FnOnce", fnOnceSegment.ident.toString())
        val args = assertIs<PathArguments.Parenthesized>(fnOnceSegment.arguments)
        assertEquals(0, args.inputs.len())
        val output = assertIs<ReturnType.TypeReturn>(args.output)
        assertPathType(output.ty, "i32")

        val send = assertIs<TypeParamBound.Trait>(bounds[1])
        assertEquals("Send", send.path.toString())
    }

    @Test
    fun testWhereClauseAtEndOfInput() {
        val whereClause = parseStr(WhereClauseParse::parse, "where").getOrThrow()

        assertEquals(0, whereClause.predicates.len())
    }

    @Test
    fun whereClauseRejectsReservedGenericParameters() {
        val result = parseStr(WhereClauseParse::parse, "where <T>")

        assertTrue(result.isFailure)
        assertEquals(
            "generic parameters on `where` clauses are reserved for future use",
            (result as SynResult.Failure).error.toString(),
        )
    }

    @Test
    fun whereTypePredicatePreservesBoundLifetimes() {
        val whereClause = parseStr(WhereClauseParse::parse, "where for<'a> Foo: Trait").getOrThrow()

        val predicate = assertIs<WherePredicate.TypePredicate>(whereClause.predicates.toList().single())
        val lifetimes = assertNotNull(predicate.lifetimes)
        val lifetime = assertIs<GenericParam.LifetimeParam>(lifetimes.lifetimes.toList().single())
        assertEquals("'a", lifetime.lifetime.toString())
        assertPathType(predicate.boundedTy, "Foo")
        val bound = assertIs<TypeParamBound.Trait>(predicate.bounds.toList().single())
        assertEquals(
            "Trait",
            bound.path.segments
                .toList()
                .single()
                .ident
                .toString(),
        )
    }

    @Test
    fun whereLifetimePredicateRequiresColon() {
        val result = parseStr(WhereClauseParse::parse, "where 'a")

        assertTrue(result.isFailure)
    }

    @Test
    fun noOpaqueDrop() {
        val generics = Generics.default()

        val lifetime =
            generics.lifetimes().firstOrNull()?.lifetime
                ?: Lifetime.new("'a", Span.callSite()).also {
                    generics.params.pushValue(GenericParam.LifetimeParam(emptyList(), it, null, LifetimeList()))
                }

        assertEquals("'a", lifetime.toString())
        assertEquals(listOf("'a"), generics.lifetimes().map { it.lifetime.toString() })
    }

    @Test
    fun typeParamWithColonAndNoBounds() {
        val param = assertIs<GenericParam.TypeParam>(parseStr(GenericParamParse::parse, "T:").getOrThrow())

        assertEquals("T", param.ident.toString())
        assertNotNull(param.colonToken)
        assertTrue(param.bounds.isEmpty())
    }

    @Test
    fun genericParamToTokensPrintsDefaultsAndConstTypes() {
        val typeParam = assertIs<GenericParam.TypeParam>(parseStr(GenericParamParse::parse, "T: Clone = Vec").getOrThrow())
        val typeTokens = TokenStream.new()
        typeParam.toTokens(typeTokens)
        assertEquals("T : Clone = Vec", typeTokens.toString())

        val constParam = assertIs<GenericParam.ConstParam>(parseStr(GenericParamParse::parse, "const N: usize = 3").getOrThrow())
        val constTokens = TokenStream.new()
        constParam.toTokens(constTokens)
        assertEquals("const N : usize = 3", constTokens.toString())
    }

    @Test
    fun genericsToTokensPrintsLifetimesFirst() {
        val input = parseStr(DeriveInputParse::parse, "struct S<T, 'a, const N: usize>;").getOrThrow()

        val tokens = TokenStream.new()
        input.generics.toTokens(tokens)
        assertEquals("< 'a , T , const N : usize >", tokens.toString())

        val turbofishArgs =
            input.generics
                .splitForImpl()
                .typeGenerics
                .asTurbofish()
                .params
                .toList()
        assertEquals("'a", assertIs<GenericArgument.LifetimeArg>(turbofishArgs[0]).lifetime.toString())
        assertPathType(assertIs<GenericArgument.TypeArg>(turbofishArgs[1]).type, "T")
        assertEquals("N", assertIs<Expr.Path>(assertIs<GenericArgument.ConstArg>(turbofishArgs[2]).expr).path.toString())
    }

    private fun assertPathType(
        type: SynType,
        vararg segments: String,
    ): Path {
        val path = assertIs<SynType.Path>(type).path
        assertEquals(segments.toList(), path.segments.toList().map { it.ident.toString() })
        return path
    }
}
