// port-lint: tests tests/test_path.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PathTest {
    @Test
    fun parseInterpolatedLeadingComponent() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("first").getOrThrow())),
                    TokenTree.Punct(Punct(':', Spacing.Joint, Span.callSite())),
                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                    TokenTree.Ident(Ident.new("rest", Span.callSite())),
                ),
            )

        val parser =
            parser@ { input: ParseStream ->
                PathParse.parse(input)
            }
        val path = parse2(parser, tokens).getOrThrow()
        val segments = path.segments.toList()
        assertEquals(2, segments.size)
        assertEquals("first", segments[0].ident.toString())
        assertEquals("rest", segments[1].ident.toString())
    }

    @Test
    fun parseParenthesizedPathArgumentsWithDisambiguator() {
        val ty = assertIs<SynType.TraitObject>(parseStr(SynTypeParseExpr::parse, "dyn FnOnce::() -> !").getOrThrow())
        val bound = assertIs<TypeParamBound.Trait>(ty.bounds.toList().single())
        val segment = bound.path.segments.toList().single()

        assertEquals("FnOnce", segment.ident.toString())
        val args = assertIs<PathArguments.Parenthesized>(segment.arguments)
        assertTrue(args.inputs.isEmpty())
        val output = assertIs<ReturnType.TypeReturn>(args.output)
        assertIs<SynType.Never>(output.ty)
    }

    @Test
    fun printIncompleteQpath() {
        val withAs = assertIs<SynType.Path>(parseStr(SynTypeParseExpr::parse, "<Self as A>::Q").getOrThrow())
        assertEquals("< Self as A > :: Q", tokensOf(withAs))
        assertNotNull(withAs.path.segments.pop())
        assertEquals("< Self as A > ::", tokensOf(withAs))
        assertNotNull(withAs.path.segments.pop())
        assertEquals("< Self >", tokensOf(withAs))
        assertNull(withAs.path.segments.pop())

        val withoutAs = assertIs<SynType.Path>(parseStr(SynTypeParseExpr::parse, "<Self>::A::B").getOrThrow())
        assertEquals("< Self > :: A :: B", tokensOf(withoutAs))
        assertNotNull(withoutAs.path.segments.pop())
        assertEquals("< Self > :: A ::", tokensOf(withoutAs))
        assertNotNull(withoutAs.path.segments.pop())
        assertEquals("< Self > ::", tokensOf(withoutAs))
        assertNull(withoutAs.path.segments.pop())

        val normal = assertIs<SynType.Path>(parseStr(SynTypeParseExpr::parse, "Self::A::B").getOrThrow())
        assertEquals("Self :: A :: B", tokensOf(normal))
        assertNotNull(normal.path.segments.pop())
        assertEquals("Self :: A ::", tokensOf(normal))
        assertNotNull(normal.path.segments.pop())
        assertEquals("Self ::", tokensOf(normal))
        assertNotNull(normal.path.segments.pop())
        assertEquals("", tokensOf(normal))
        assertNull(normal.path.segments.pop())
    }

    @Test
    fun qselfSpanUsesDelimiters() {
        val ty = assertIs<SynType.Path>(
            parseStr(SynTypeParseExpr::parse, "<Vec<T> as a::b::Trait>::AssociatedItem").getOrThrow(),
        )
        val qself = assertNotNull(ty.qself)

        assertEquals(qself.ltToken.span.join(qself.gtToken.span), qself.span())
    }

    @Test
    fun parseSimplePath() {
        val path = parseStr(PathParse::parse, "std::vec::Vec").getOrThrow()
        val segments = path.segments.toList()
        assertEquals(3, segments.size)
        assertEquals("std", segments[0].ident.toString())
        assertEquals("vec", segments[1].ident.toString())
        assertEquals("Vec", segments[2].ident.toString())
    }

    @Test
    fun parseLeadingColonPath() {
        val path = parseStr(PathParse::parse, "::core::mem").getOrThrow()
        assertNotNull(path.leadingColon)
        val segments = path.segments.toList()
        assertEquals(2, segments.size)
        assertEquals("core", segments[0].ident.toString())
        assertEquals("mem", segments[1].ident.toString())
    }

    @Test
    fun parseSingleSegmentPath() {
        val path = parseStr(PathParse::parse, "String").getOrThrow()
        assertNull(path.leadingColon)
        val segments = path.segments.toList()
        assertEquals(1, segments.size)
        assertEquals("String", segments[0].ident.toString())
    }

    @Test
    fun pathIsIdent() {
        val path = parseStr(PathParse::parse, "Foo").getOrThrow()
        assertTrue(path.isIdent("Foo"))
        assertTrue(!path.isIdent("Bar"))
    }

    @Test
    fun pathIsIdentReturnsFalseForMultiSegment() {
        val path = parseStr(PathParse::parse, "Foo::Bar").getOrThrow()
        assertTrue(!path.isIdent("Foo"))
    }

    @Test
    fun pathGetIdentSingleSegment() {
        val path = parseStr(PathParse::parse, "Foo").getOrThrow()
        assertEquals("Foo", path.getIdent()?.toString())
    }

    @Test
    fun pathGetIdentMultiSegmentReturnsNull() {
        val multi = parseStr(PathParse::parse, "Foo::Bar").getOrThrow()
        assertNull(multi.getIdent())
    }

    @Test
    fun pathGetIdentLeadingColonReturnsNull() {
        val leading = parseStr(PathParse::parse, "::Foo").getOrThrow()
        assertNull(leading.getIdent())
    }

    @Test
    fun pathToStringRoundtrips() {
        val path = parseStr(PathParse::parse, "a::b::c").getOrThrow()
        assertEquals("a::b::c", path.toString())
    }

    @Test
    fun pathToStringLeadingColon() {
        val path = parseStr(PathParse::parse, "::a::b").getOrThrow()
        assertEquals("::a::b", path.toString())
    }

    @Test
    fun pathFromSingleIdent() {
        val ident = Ident.new("Foo", Span.callSite())
        val path = Path.from(ident)
        assertEquals(1, path.segments.len())
        assertEquals(
            "Foo",
            path.segments
                .toList()
                .single()
                .ident
                .toString(),
        )
    }

    @Test
    fun pathDeepCopyIsEqual() {
        val path = parseStr(PathParse::parse, "a::b::c").getOrThrow()
        val copy = path.deepCopy()
        assertEquals(path, copy)
    }

    @Test
    fun pathParseFailureOnEmpty() {
        assertTrue(parseStr(PathParse::parse, "").isFailure)
    }

    @Test
    fun pathParseFailureOnPunct() {
        assertTrue(parseStr(PathParse::parse, "::").isFailure)
    }
}

private fun <T : Any> assertNotNull(value: T?): T =
    kotlin.test.assertNotNull(value)

private fun tokensOf(value: ToTokens): String {
    val tokens = TokenStream.new()
    value.toTokens(tokens)
    return tokens.toString()
}
