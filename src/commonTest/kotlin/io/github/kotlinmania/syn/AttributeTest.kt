// port-lint: tests tests/test_attribute.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AttributeTest {
    @Test
    fun testMetaItemWord() {
        val meta = parseOuterMeta("#[foo]")

        val path = assertIs<Meta.PathMeta>(meta)
        assertEquals("foo", path.path.toString())
    }

    @Test
    fun testMetaItemNameValue() {
        val meta = assertIs<Meta.NameValue>(parseOuterMeta("#[foo = 5]"))

        assertEquals("foo", meta.path.toString())
        assertIntLiteral("5", meta.value)
    }

    @Test
    fun testMetaItemBoolValue() {
        val trueMeta = assertIs<Meta.NameValue>(parseOuterMeta("#[foo = true]"))
        assertEquals("foo", trueMeta.path.toString())
        assertBoolLiteral(true, trueMeta.value)

        val falseMeta = assertIs<Meta.NameValue>(parseOuterMeta("#[foo = false]"))
        assertEquals("foo", falseMeta.path.toString())
        assertBoolLiteral(false, falseMeta.value)
    }

    @Test
    fun testMetaItemListLit() {
        assertListMeta("#[foo(5)]", "foo", "5")
    }

    @Test
    fun testMetaItemListWord() {
        assertListMeta("#[foo(bar)]", "foo", "bar")
    }

    @Test
    fun testMetaItemListNameValue() {
        assertListMeta("#[foo(bar = 5)]", "foo", "bar = 5")
    }

    @Test
    fun testMetaItemListBoolValue() {
        assertListMeta("#[foo(bar = true)]", "foo", "bar = true")
    }

    @Test
    fun testMetaItemMultiple() {
        assertListMeta(
            "#[foo(word, name = 5, list(name2 = 6), word2)]",
            "foo",
            "word , name = 5 , list (name2 = 6) , word2",
        )
    }

    @Test
    fun testBoolLit() {
        assertListMeta("#[foo(true)]", "foo", "true")
    }

    @Test
    fun testNegativeLit() {
        assertListMeta("#[form(min = -1, max = 200)]", "form", "min = - 1 , max = 200")
    }
}

private fun parseOuterMeta(input: String): Meta {
    val attrs = parserFromFunction(::parseOuterAttributes).parseStr(input).getOrThrow()
    assertEquals(1, attrs.size)
    val attr = attrs.single()
    assertIs<AttrStyle.Outer>(attr.style)
    return attr.meta
}

private fun assertListMeta(
    input: String,
    path: String,
    tokens: String,
) {
    val meta = assertIs<Meta.List>(parseOuterMeta(input))
    assertEquals(path, meta.path.toString())
    assertTrue(meta.delimiter is MacroDelimiter.Paren)
    assertEquals(tokens, meta.tokens.toString())
}

private fun assertIntLiteral(
    digits: String,
    expr: Expr,
) {
    val lit = assertIs<Expr.Lit>(expr)
    val intLit = assertIs<Lit.Int>(lit.lit)
    assertEquals(digits, intLit.value.base10Digits())
}

private fun assertBoolLiteral(
    value: Boolean,
    expr: Expr,
) {
    val lit = assertIs<Expr.Lit>(expr)
    val boolLit = assertIs<Lit.Bool>(lit.lit)
    assertEquals(value, boolLit.value.value)
}
