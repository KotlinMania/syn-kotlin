// port-lint: tests tests/test_visibility.rs
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

class VisibilityTest {
    private fun visRestParse(input: String): Pair<Visibility, String> {
        val parser =
            parser@ { stream: ParseStream ->
                val visResult = VisibilityParse.parse(stream)
                if (visResult.isFailure) {
                    return@parser SynResult.failure(visResult.exceptionOrNull()!!)
                }
                val restResult = TokenStreamParse.parse(stream)
                if (restResult.isFailure) {
                    return@parser SynResult.failure(restResult.exceptionOrNull()!!)
                }
                SynResult.success(visResult.getOrThrow() to restResult.getOrThrow().toString())
            }
        return parseStr(parser, input).getOrThrow()
    }

    private fun assertVisClass(input: String, expectedClass: kotlin.reflect.KClass<out Visibility>) {
        val (vis, _) = visRestParse(input)
        assertTrue(
            vis::class == expectedClass,
            "expected ${expectedClass.simpleName}, got ${vis::class.simpleName}",
        )
    }

    private fun assertVisClassRest(input: String, expectedClass: kotlin.reflect.KClass<out Visibility>, expectedRest: String) {
        val (vis, rest) = visRestParse(input)
        assertTrue(
            vis::class == expectedClass,
            "expected ${expectedClass.simpleName}, got ${vis::class.simpleName}",
        )
        // Round-trip through toString to avoid potential whitespace diffs.
        val expected = TokenStream.fromString(expectedRest).getOrThrow().toString()
        assertEquals(expected, rest)
    }

    private fun assertVisErr(input: String) {
        val parser =
            parser@ { stream: ParseStream ->
                val visResult = VisibilityParse.parse(stream)
                if (visResult.isFailure) {
                    return@parser SynResult.failure(visResult.exceptionOrNull()!!)
                }
                val restResult = TokenStreamParse.parse(stream)
                if (restResult.isFailure) {
                    return@parser SynResult.failure(restResult.exceptionOrNull()!!)
                }
                SynResult.success(visResult.getOrThrow() to restResult.getOrThrow().toString())
            }
        assertTrue(parseStr(parser, input).isFailure, "expected parse error for: $input")
    }

    @Test
    fun testPub() {
        assertVisClass("pub", Visibility.Public::class)
    }

    @Test
    fun testInherited() {
        assertVisClass("", Visibility.Inherited::class)
    }

    @Test
    fun testIn() {
        assertRestrictedVisibility(visRestParse("pub(in foo::bar)").first, hasInToken = true, "foo", "bar")
    }

    @Test
    fun testPubCrate() {
        assertRestrictedVisibility(visRestParse("pub(crate)").first, hasInToken = false, "crate")
    }

    @Test
    fun testPubSelf() {
        assertRestrictedVisibility(visRestParse("pub(self)").first, hasInToken = false, "self")
    }

    @Test
    fun testPubSuper() {
        assertRestrictedVisibility(visRestParse("pub(super)").first, hasInToken = false, "super")
    }

    @Test
    fun testMissingIn() {
        assertVisClassRest("pub(foo::bar)", Visibility.Public::class, "(foo::bar)")
    }

    @Test
    fun testMissingInPath() {
        assertVisErr("pub(in)")
    }

    @Test
    fun testCratePath() {
        assertVisClassRest("pub(crate::A, crate::B)", Visibility.Public::class, "(crate::A , crate::B)")
    }

    @Test
    fun testJunkAfterIn() {
        assertVisErr("pub(in some::path @@garbage)")
    }

    @Test
    fun testInheritedVisNamedField() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("struct", Span.callSite())),
                    TokenTree.Ident(Ident.new("S", Span.callSite())),
                    TokenTree.Group(
                        Group(
                            Delimiter.Brace,
                            TokenStream.fromTokenTrees(
                                listOf(
                                    TokenTree.Group(Group(Delimiter.None, TokenStream.new())),
                                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("f").getOrThrow())),
                                    TokenTree.Punct(Punct(':', Spacing.Alone, Span.callSite())),
                                    TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
                                ),
                            ),
                        ),
                    ),
                ),
            )

        val input = parse2(DeriveInputParse::parse, tokens).getOrThrow()
        assertIs<Visibility.Inherited>(input.vis)
        assertEquals("S", input.ident.toString())
        val fields = assertIs<Fields.Named>(assertIs<Data.Struct>(input.data).fields).fields.named.toList()
        val field = fields.single()
        assertIs<Visibility.Inherited>(field.vis)
        assertEquals("f", field.ident.toString())
        assertNotNull(field.colonToken)
        assertIs<SynType.Tuple>(field.ty)
    }

    @Test
    fun testInheritedVisUnnamedField() {
        val tokens =
            TokenStream.fromTokenTrees(
                listOf(
                    TokenTree.Ident(Ident.new("struct", Span.callSite())),
                    TokenTree.Ident(Ident.new("S", Span.callSite())),
                    TokenTree.Group(
                        Group(
                            Delimiter.Parenthesis,
                            TokenStream.fromTokenTrees(
                                listOf(
                                    TokenTree.Group(Group(Delimiter.None, TokenStream.new())),
                                    TokenTree.Group(Group(Delimiter.None, TokenStream.fromString("str").getOrThrow())),
                                ),
                            ),
                        ),
                    ),
                    TokenTree.Punct(Punct(';', Spacing.Alone, Span.callSite())),
                ),
            )

        val input = parse2(DeriveInputParse::parse, tokens).getOrThrow()
        assertIs<Visibility.Inherited>(input.vis)
        assertEquals("S", input.ident.toString())
        val data = assertIs<Data.Struct>(input.data).value
        assertNotNull(data.semiToken)
        val fields = assertIs<Fields.Unnamed>(data.fields).fields.unnamed.toList()
        val field = fields.single()
        assertIs<Visibility.Inherited>(field.vis)
        assertNull(field.ident)
        val group = assertIs<SynType.Group>(field.ty)
        assertTypePath(group.elem, "str")
    }

    private fun assertRestrictedVisibility(
        vis: Visibility,
        hasInToken: Boolean,
        vararg segments: String,
    ) {
        val restricted = assertIs<Visibility.Restricted>(vis)
        if (hasInToken) {
            assertNotNull(restricted.inToken)
        } else {
            assertNull(restricted.inToken)
        }
        assertEquals(segments.toList(), restricted.path.segments.toList().map { it.ident.toString() })
    }

    private fun assertTypePath(
        type: SynType,
        vararg segments: String,
    ) {
        val path = assertIs<SynType.Path>(type).path
        assertEquals(segments.toList(), path.segments.toList().map { it.ident.toString() })
    }
}
