package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AssociatedItemRegressionTest {
    @Test
    fun traitMethodAttributesPreserveAllItems() {
        val item =
            assertIs<Item.Trait>(
                parseStr(
                    ItemParse::parse,
                    """
                    trait Trait {
                        const VALUE: u8;
                        type Output;
                        fn sync(&self) -> u8;
                        #[allow(non_snake_case)]
                        async fn camelCase();
                    }
                    """.trimIndent(),
                ).getOrThrow(),
            )

        assertEquals(4, item.items.size)
        assertIs<TraitItem.Const>(item.items[0])
        assertIs<TraitItem.AssocType>(item.items[1])
        assertEquals("sync", assertIs<TraitItem.Fn>(item.items[2]).sig.ident.toString())
        val method = assertIs<TraitItem.Fn>(item.items[3])
        assertEquals("camelCase", method.sig.ident.toString())
        assertNotNull(method.sig.asyncness)
        assertTrue(
            method.attrs
                .single()
                .path()
                .isIdent("allow"),
        )
        assertEquals(
            "# [allow (non_snake_case)]",
            method.attrs
                .single()
                .toTokenStream()
                .toString(),
        )
    }

    @Test
    fun traitAssociatedItemsRetainOuterAttributes() {
        val item =
            assertIs<Item.Trait>(
                parseStr(
                    ItemParse::parse,
                    "trait Trait { #[first] const VALUE: u8; #[second] type Output; #[third] #[fourth] fn f(); }",
                ).getOrThrow(),
            )
        assertTrue(
            assertIs<TraitItem.Const>(item.items[0])
                .attrs
                .single()
                .path()
                .isIdent("first"),
        )
        assertTrue(
            assertIs<TraitItem.AssocType>(item.items[1])
                .attrs
                .single()
                .path()
                .isIdent("second"),
        )
        val method = assertIs<TraitItem.Fn>(item.items[2])
        assertEquals(listOf("third", "fourth"), method.attrs.map { it.path().getIdent().toString() })
    }

    @Test
    fun traitDefaultMethodRetainsInnerAndOuterAttributes() {
        val item =
            assertIs<Item.Trait>(
                parseStr(ItemParse::parse, "trait Trait { #[outer] fn f() { #![inner] consume(); } }").getOrThrow(),
            )
        val method = assertIs<TraitItem.Fn>(item.items.single())
        assertEquals(2, method.attrs.size)
        assertIs<AttrStyle.Outer>(method.attrs[0].style)
        assertIs<AttrStyle.Inner>(method.attrs[1].style)
        assertEquals(1, assertNotNull(method.default).stmts.size)
        assertEquals(
            "trait Trait { # [outer] fn f () { # ! [inner] consume () ; } }",
            item.toTokenStream().toString(),
        )
    }

    @Test
    fun qualifiedSelectionMacroRetainsImplMethodAndTokens() {
        val body = "() = async { consume(self); }.fuse() => {}"
        val item =
            assertIs<Item.Impl>(
                parseStr(
                    ItemParse::parse,
                    "impl Trait for S { async fn f(&self) { futures::select! { $body } } }",
                ).getOrThrow(),
            )
        val method = assertIs<ImplItem.Fn>(item.items.single())
        assertEquals("f", method.sig.ident.toString())
        val statement = assertIs<Stmt.MacroStmt>(method.block.stmts.single())
        assertEquals(
            listOf("futures", "select"),
            statement.mac.path.segments
                .toList()
                .map { it.ident.toString() },
        )
        assertTrue(statement.mac.isBrace())
        assertEquals(TokenStream.fromString(body).getOrThrow().toString(), statement.mac.tokens.toString())
    }

    @Test
    fun qualifiedMacroPreservesSurroundingImplMethods() {
        val item =
            assertIs<Item.Impl>(
                parseStr(
                    ItemParse::parse,
                    "impl S { fn before() {} fn middle() { futures::select! {} } fn after() {} }",
                ).getOrThrow(),
            )
        assertEquals(listOf("before", "middle", "after"), item.items.map { assertIs<ImplItem.Fn>(it).sig.ident.toString() })
    }

    @Test
    fun malformedMethodBodiesFailInsteadOfDroppingMethods() {
        for (container in listOf("impl S", "trait Trait")) {
            for (prefix in listOf("", "fn before() {}")) {
                val result = parseStr(ItemParse::parse, "$container { $prefix fn broken() { 1 + } }")
                assertTrue(result.isFailure, "$container must propagate the malformed method body: $result")
            }
        }
    }

    @Test
    fun qualifiedMacroExpressionsAcceptAllDelimiters() {
        for (path in listOf("select", "futures::select", "::futures::select", "self::select", "crate::nested::select")) {
            for (body in listOf("()", "[]", "{}")) {
                val result = parseStr(ExprParse::parse, "$path!$body")
                assertTrue(result.isSuccess, "$path!$body: ${result.exceptionOrNull()}")
                val expression = assertIs<Expr.Macro>(result.getOrThrow())
                assertTrue(expression.mac.path.isModStyle())
                assertEquals(
                    path,
                    expression.mac.path
                        .toTokenStream()
                        .toString()
                        .replace(" ", ""),
                )
                assertTrue(expression.mac.tokens.isEmpty())
            }
        }
    }

    @Test
    fun qualifiedPathInequalityRemainsABinaryExpression() {
        val expression = assertIs<Expr.Binary>(parseStr(ExprParse::parse, "module::VALUE != other").getOrThrow())
        assertIs<BinOp.Ne>(expression.op)
    }

    @Test
    fun macroExpressionsRejectGenericAndQualifiedSelfPaths() {
        for (source in listOf("module::select::<T>!()", "<T as Trait>::select!()")) {
            assertTrue(parseStr(ExprParse::parse, source).isFailure, source)
        }
    }
}
