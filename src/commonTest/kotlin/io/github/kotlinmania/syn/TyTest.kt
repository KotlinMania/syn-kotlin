// port-lint: tests tests/test_ty.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for parsing of types.
 *
 * The type parser (`SynTypeParseExpr`, equivalent to upstream
 * `Parse<SynType>`) currently handles the infer (`_`), reference
 * (`&` / `&'lt` / `&mut`), pointer (`*const` / `*mut`), parenthesized,
 * tuple, path forms, invisible type groups, `impl Trait`, trait-object
 * forms, bare-fn types, `PreciseCapture` `use<...>` bounds, and the
 * none-delimited group path disambiguation covered by upstream `test_ty.rs`.
 */
class TyTest {
    @Test
    fun testMutSelf() {
        val mutSelf = assertIs<SynType.BareFn>(parseType("fn(mut self)"))
        assertNull(mutSelf.lifetimes)
        assertNull(mutSelf.unsafety)
        assertNull(mutSelf.abi)
        assertEquals(1, mutSelf.inputs.size)
        assertNull(mutSelf.inputs.first()!!.name)
        assertEquals("mut self", assertIs<SynType.Verbatim>(mutSelf.inputs.first()!!.ty).tokens.toString())
        assertNull(mutSelf.variadic)
        assertIs<ReturnType.Default>(mutSelf.output)

        val trailing = assertIs<SynType.BareFn>(parseType("fn(mut self,)"))
        assertEquals(1, trailing.inputs.size)
        assertTrue(trailing.inputs.trailingPunct())
        assertEquals("mut self", assertIs<SynType.Verbatim>(trailing.inputs.first()!!.ty).tokens.toString())

        val typed = assertIs<SynType.BareFn>(parseType("fn(mut self: ())"))
        assertEquals(1, typed.inputs.size)
        assertNull(typed.inputs.first()!!.name)
        assertEquals("mut self : ()", assertIs<SynType.Verbatim>(typed.inputs.first()!!.ty).tokens.toString())

        assertTrue(parseStr(SynTypeParseExpr, "fn(mut self: ...)").isFailure)
        assertTrue(parseStr(SynTypeParseExpr, "fn(mut self: mut self)").isFailure)
        assertTrue(parseStr(SynTypeParseExpr, "fn(mut self::T)").isFailure)
    }

    @Test
    fun testMacroVariableType() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("ty").getOrThrow())),
                    TokenTree.Punct(Punct('<', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("T", Span.callSite())),
                    TokenTree.Punct(Punct('>', Spacing.Alone, Span.callSite())),
                ),
            )
        val tyGeneric = assertIs<SynType.Path>(parseType(tokens))
        assertPath(tyGeneric.path, "ty")
        val args = assertIs<PathArguments.AngleBracketed>(tyGeneric.path.segments.first()!!.arguments)
        assertNull(args.colon2Token)
        assertPathTypeArg(args, 0, "T")

        val turbofishTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("ty").getOrThrow())),
                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                    TokenTree.Punct(Punct('<', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("T", Span.callSite())),
                    TokenTree.Punct(Punct('>', Spacing.Alone, Span.callSite())),
                ),
            )
        val tyTurbofish = assertIs<SynType.Path>(parseType(turbofishTokens))
        assertPath(tyTurbofish.path, "ty")
        val turbofishArgs = assertIs<PathArguments.AngleBracketed>(tyTurbofish.path.segments.first()!!.arguments)
        assertNotNull(turbofishArgs.colon2Token)
        assertPathTypeArg(turbofishArgs, 0, "T")
    }

    @Test
    fun testGroupAngleBrackets() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("Option", Span.callSite())),
                    TokenTree.Punct(Punct('<', Spacing.Alone, Span.callSite())),
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("Vec<u8>").getOrThrow())),
                    TokenTree.Punct(Punct('>', Spacing.Alone, Span.callSite())),
                ),
            )
        val option = assertIs<SynType.Path>(parseType(tokens))
        assertPath(option.path, "Option")
        val args = assertIs<PathArguments.AngleBracketed>(option.path.segments.first()!!.arguments)
        val arg = assertIs<GenericArgument.TypeArg>(args.args.toList().single())
        val group = assertIs<SynType.Group>(arg.type)
        val vec = assertIs<SynType.Path>(group.elem)
        assertPath(vec.path, "Vec")
        val vecArgs = assertIs<PathArguments.AngleBracketed>(vec.path.segments.first()!!.arguments)
        assertPathTypeArg(vecArgs, 0, "u8")
    }

    @Test
    fun testGroupColons() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("Vec<u8>").getOrThrow())),
                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("Item", Span.callSite())),
                ),
            )
        val path = assertIs<SynType.Path>(parseType(tokens))
        assertPath(path.path, "Vec", "Item")
        val vecArgs = assertIs<PathArguments.AngleBracketed>(path.path.segments.first()!!.arguments)
        assertPathTypeArg(vecArgs, 0, "u8")

        val qselfTokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("[T]").getOrThrow())),
                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("Element", Span.callSite())),
                ),
            )
        val qselfPath = assertIs<SynType.Path>(parseType(qselfTokens))
        val qself = assertNotNull(qselfPath.qself)
        assertEquals(0, qself.position)
        assertNull(qself.asToken)
        val slice = assertIs<SynType.Slice>(qself.ty)
        val elem = assertIs<SynType.Path>(slice.elem)
        assertPath(elem.path, "T")
        assertNotNull(qselfPath.path.leadingColon)
        assertPath(qselfPath.path, "Element")
    }

    @Test
    fun testTraitObject() {
        val withFor = assertIs<SynType.TraitObject>(parseType("dyn for<'a> Trait<'a> + 'static"))
        assertNotNull(withFor.dynToken)
        assertEquals(2, withFor.bounds.size)
        val withForBounds = withFor.bounds.toList()
        val trait = assertTraitBound(withForBounds[0], "Trait")
        val lifetimes = assertNotNull(trait.lifetimes)
        val lifetimeParam = assertIs<GenericParam.LifetimeParam>(lifetimes.lifetimes.toList().single())
        assertEquals("'a", lifetimeParam.lifetime.toString())
        val arguments = assertIs<PathArguments.AngleBracketed>(trait.path.segments.first()!!.arguments)
        val lifetimeArg = assertIs<GenericArgument.LifetimeArg>(arguments.args.toList().single())
        assertEquals("'a", lifetimeArg.lifetime.toString())
        assertLifetimeBound(withForBounds[1], "'static")

        val lifetimeFirst = assertIs<SynType.TraitObject>(parseType("dyn 'a + Trait"))
        assertNotNull(lifetimeFirst.dynToken)
        assertEquals(2, lifetimeFirst.bounds.size)
        val lifetimeFirstBounds = lifetimeFirst.bounds.toList()
        assertLifetimeBound(lifetimeFirstBounds[0], "'a")
        assertTraitBound(lifetimeFirstBounds[1], "Trait")

        assertTrue(parseStr(SynTypeParseExpr, "for<'a> dyn Trait<'a>").isFailure)
        assertTrue(parseStr(SynTypeParseExpr, "dyn for<'a> 'a + Trait").isFailure)
    }

    @Test
    fun testTrailingPlus() {
        val implTrait = assertIs<SynType.ImplTrait>(parseType("impl Trait +"))
        assertEquals(1, implTrait.bounds.size)
        assertTraitBound(implTrait.bounds.toList().single(), "Trait")
        assertTrue(implTrait.bounds.trailingPunct())

        val dynTrait = assertIs<SynType.TraitObject>(parseType("dyn Trait +"))
        assertNotNull(dynTrait.dynToken)
        assertEquals(1, dynTrait.bounds.size)
        assertTraitBound(dynTrait.bounds.toList().single(), "Trait")
        assertTrue(dynTrait.bounds.trailingPunct())

        val bareTrait = assertIs<SynType.TraitObject>(parseType("Trait +"))
        assertNull(bareTrait.dynToken)
        assertEquals(1, bareTrait.bounds.size)
        assertTraitBound(bareTrait.bounds.toList().single(), "Trait")
        assertTrue(bareTrait.bounds.trailingPunct())
    }

    @Test
    fun testTupleComma() {
        // Empty tuple `()` parses as `SynType.Tuple` with zero elements.
        val empty = parseStr(SynTypeParseExpr, "()").getOrThrow()
        assertIs<SynType.Tuple>(empty)
        assertEquals(0, empty.elems.size)

        // A single element with a trailing comma must parse as
        // `SynType.Tuple` (not `SynType.Paren`); the element is a
        // `SynType.Infer`.
        val oneTrailing = parseStr(SynTypeParseExpr, "(_,)").getOrThrow()
        assertIs<SynType.Tuple>(oneTrailing)
        assertEquals(1, oneTrailing.elems.size)
        assertTrue(oneTrailing.elems.trailingPunct())
        assertIs<SynType.Infer>(oneTrailing.elems.first())

        // Two elements without a trailing comma parse as `SynType.Tuple`.
        val two = parseStr(SynTypeParseExpr, "(_, _)").getOrThrow()
        assertIs<SynType.Tuple>(two)
        assertEquals(2, two.elems.size)
        val twoList = two.elems.toList()
        assertIs<SynType.Infer>(twoList[0])
        assertIs<SynType.Infer>(twoList[1])

        // Two elements with a trailing comma parse as `SynType.Tuple`
        // and retain the trailing punctuation.
        val twoTrailing = parseStr(SynTypeParseExpr, "(_, _,)").getOrThrow()
        assertIs<SynType.Tuple>(twoTrailing)
        assertEquals(2, twoTrailing.elems.size)
        assertTrue(twoTrailing.elems.trailingPunct())
    }

    @Test
    fun testImplTraitUse() {
        val implTrait = assertIs<SynType.ImplTrait>(parseType("impl Sized + use<'_, 'a, A, Test>"))
        assertEquals(2, implTrait.bounds.size)
        val bounds = implTrait.bounds.toList()
        assertTraitBound(bounds[0], "Sized")
        val preciseCapture = assertIs<TypeParamBound.PreciseCapture>(bounds[1])
        val params = preciseCapture.params.toList()
        assertEquals(4, params.size)
        assertEquals("'_", assertIs<CapturedParam.Lifetime>(params[0]).lifetime.toString())
        assertEquals("'a", assertIs<CapturedParam.Lifetime>(params[1]).lifetime.toString())
        assertEquals("A", assertIs<CapturedParam.Ident>(params[2]).ident.toString())
        assertEquals("Test", assertIs<CapturedParam.Ident>(params[3]).ident.toString())

        val trailing = assertIs<SynType.ImplTrait>(parseType("impl Sized + use<'_,>"))
        val trailingCapture = assertIs<TypeParamBound.PreciseCapture>(trailing.bounds.toList()[1])
        assertEquals(1, trailingCapture.params.size)
        assertEquals("'_", assertIs<CapturedParam.Lifetime>(trailingCapture.params.first()!!).lifetime.toString())
        assertTrue(trailingCapture.params.trailingPunct())
    }

    private fun parseType(source: String): SynType =
        parseStr(SynTypeParseExpr, source).getOrThrow()

    private fun parseType(tokens: TokenStream): SynType =
        parse2(SynTypeParseExpr, tokens).getOrThrow()

    private fun assertPathTypeArg(args: PathArguments.AngleBracketed, index: Int, vararg segments: String) {
        val arg = assertIs<GenericArgument.TypeArg>(args.args.toList()[index])
        val ty = assertIs<SynType.Path>(arg.type)
        assertPath(ty.path, *segments)
    }

    private fun assertTraitBound(bound: TypeParamBound, vararg segments: String): TypeParamBound.Trait {
        val trait = assertIs<TypeParamBound.Trait>(bound)
        assertIs<TraitBoundModifier.None>(trait.modifier)
        assertPath(trait.path, *segments)
        return trait
    }

    private fun assertLifetimeBound(bound: TypeParamBound, lifetime: String): TypeParamBound.LifetimeBound {
        val lifetimeBound = assertIs<TypeParamBound.LifetimeBound>(bound)
        assertEquals(lifetime, lifetimeBound.lifetime.toString())
        return lifetimeBound
    }

    private fun assertPath(path: Path, vararg segments: String) {
        assertEquals(segments.toList(), path.segments.toList().map { it.ident.toString() })
    }
}
