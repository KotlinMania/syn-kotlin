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

    @Test
    fun testParseNestedMeta() {
        val attrs = parserFromFunction(::parseOuterAttributes).parseStr("#[tea(kind = \"EarlGrey\", hot, with(sugar, milk),)]").getOrThrow()
        val attr = attrs.single()
        var kind: String? = null
        var hot = false
        val with = mutableListOf<String>()

        val result =
            attr.parseNestedMeta { meta ->
                when {
                    meta.path.isIdent("kind") -> {
                        val value = meta.value().getOrElse { return@parseNestedMeta SynResult.failure(it) }
                        val lit = value.parse(LitStrParse::parse).getOrElse { return@parseNestedMeta SynResult.failure(it) }
                        kind = lit.value()
                        SynResult.success(Unit)
                    }
                    meta.path.isIdent("hot") -> {
                        hot = true
                        SynResult.success(Unit)
                    }
                    meta.path.isIdent("with") ->
                        meta.parseNestedMeta { nested ->
                            with.add(nested.path.toString())
                            SynResult.success(Unit)
                        }
                    else -> SynResult.failure(meta.error("unsupported tea property"))
                }
            }

        assertTrue(result.isSuccess)
        assertEquals("EarlGrey", kind)
        assertTrue(hot)
        assertEquals(listOf("sugar", "milk"), with)
    }

    @Test
    fun testParseArgs() {
        val attr = parseOuterAttribute("#[precondition(value < 5)]")
        val expr = attr.parseArgs(ExprParse::parse).getOrThrow()
        assertIs<Expr.Binary>(expr)
    }

    @Test
    fun testRequireMetaKinds() {
        val path = parseStr(MetaParse::parse, "test").getOrThrow()
        assertTrue(path.requirePathOnly().isSuccess)
        assertTrue(path.requireList().isFailure)
        assertTrue(path.requireNameValue().isFailure)

        val list = parseStr(MetaParse::parse, "derive(Clone)").getOrThrow()
        assertTrue(list.requirePathOnly().isFailure)
        assertTrue(list.requireList().isSuccess)
        assertTrue(list.requireNameValue().isFailure)

        val nameValue = parseStr(MetaParse::parse, "path = \"sys.rs\"").getOrThrow()
        assertTrue(nameValue.requirePathOnly().isFailure)
        assertTrue(nameValue.requireList().isFailure)
        assertTrue(nameValue.requireNameValue().isSuccess)
    }

    @Test
    fun testOutermostMetaPathKeywordRule() {
        assertTrue(parseStr(MetaParse::parse, "unsafe").isSuccess)
        assertTrue(parseStr(MetaParse::parse, "async").isFailure)
    }

    @Test
    fun testParseOuterRejectsInnerAttribute() {
        val result = parserFromFunction(Attribute::parseOuter).parseStr("#![feature(test)]")
        assertTrue(result.isFailure)
    }
}

private fun parseOuterMeta(input: String): Meta {
    return parseOuterAttribute(input).meta
}

private fun parseOuterAttribute(input: String): Attribute {
    val attrs = parserFromFunction(::parseOuterAttributes).parseStr(input).getOrThrow()
    assertEquals(1, attrs.size)
    val attr = attrs.single()
    assertIs<AttrStyle.Outer>(attr.style)
    return attr
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
    assertEquals(value, boolLit.value.value())
}
