// port-lint: tests tests/test_derive_input.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeriveInputTest {
    @Test
    fun testUnit() {
        val input = parse("struct Unit;")

        assertIs<Visibility.Inherited>(input.vis)
        assertEquals("Unit", input.ident.toString())
        assertTrue(input.generics.params.isEmpty())
        val data = assertIs<Data.Struct>(input.data).value
        assertIs<Fields.Unit>(data.fields)
        assertNotNull(data.semiToken)
    }

    @Test
    fun testStruct() {
        val input =
            parse(
                """
                #[derive(Debug, Clone)]
                pub struct Item {
                    pub ident: Ident,
                    pub attrs: Vec<Attribute>
                }
                """.trimIndent(),
            )

        val derive = assertIs<Meta.List>(input.attrs.single().meta)
        assertEquals("derive", derive.path.toString())
        assertEquals("Debug , Clone", derive.tokens.toString())
        assertIs<Visibility.Public>(input.vis)
        assertEquals("Item", input.ident.toString())
        assertTrue(input.generics.params.isEmpty())

        val fields = namedFields(assertIs<Data.Struct>(input.data).fields)
        assertEquals(2, fields.size)
        assertPublicNamedPathField(fields[0], "ident", "Ident")
        assertPublicNamedPathField(fields[1], "attrs", "Vec")
        val vecArgs = assertIs<PathArguments.AngleBracketed>(assertPathType(fields[1].ty, "Vec").segments.toList().single().arguments)
        val arg = assertIs<GenericArgument.TypeArg>(vecArgs.args.toList().single())
        assertPathType(arg.type, "Attribute")
    }

    @Test
    fun testUnion() {
        val input =
            parse(
                """
                union MaybeUninit<T> {
                    uninit: (),
                    value: T
                }
                """.trimIndent(),
            )

        assertIs<Visibility.Inherited>(input.vis)
        assertEquals("MaybeUninit", input.ident.toString())
        assertTypeParams(input.generics, "T")
        val fields = assertIs<Data.Union>(input.data).fields.named.toList()
        assertEquals(2, fields.size)
        assertEquals("uninit", fields[0].ident.toString())
        assertIs<SynType.Tuple>(fields[0].ty)
        assertNamedPathField(fields[1], "value", "T")
    }

    @Test
    fun testEnum() {
        val input =
            parse(
                """
                #[doc = " See the std::result module documentation for details."]
                #[must_use]
                pub enum Result<T, E> {
                    Ok(T),
                    Err(E),
                    Surprise = 0isize,
                    ProcMacroHack = (0, "data").0
                }
                """.trimIndent(),
            )

        val doc = assertIs<Meta.NameValue>(input.attrs[0].meta)
        assertEquals("doc", doc.path.toString())
        assertStringLiteral(" See the std::result module documentation for details.", doc.value)
        assertEquals("must_use", assertIs<Meta.PathMeta>(input.attrs[1].meta).path.toString())
        assertIs<Visibility.Public>(input.vis)
        assertEquals("Result", input.ident.toString())
        assertTypeParams(input.generics, "T", "E")

        val variants = assertIs<Data.Enum>(input.data).variants.toList()
        assertEquals(4, variants.size)
        assertEquals("Ok", variants[0].ident.toString())
        assertPathType(unnamedFields(variants[0].fields).single().ty, "T")
        assertEquals("Err", variants[1].ident.toString())
        assertPathType(unnamedFields(variants[1].fields).single().ty, "E")
        assertEquals("Surprise", variants[2].ident.toString())
        assertIs<Fields.Unit>(variants[2].fields)
        assertIntLiteral("0isize", assertNotNull(variants[2].discriminant).expr)
        assertEquals("ProcMacroHack", variants[3].ident.toString())
        assertProcMacroHackDiscriminant(assertNotNull(variants[3].discriminant).expr)
    }

    @Test
    fun testAttrWithNonModStylePath() {
        assertTrue(parseStr(DeriveInputParse, "#[inert <T>] struct S;").isFailure)
    }

    @Test
    fun testAttrWithModStylePathWithSelf() {
        val input = parse("#[foo::self] struct S;")

        val meta = assertIs<Meta.PathMeta>(input.attrs.single().meta)
        assertEquals(listOf("foo", "self"), pathSegments(meta.path))
        assertEquals("S", input.ident.toString())
        assertIs<Fields.Unit>(assertIs<Data.Struct>(input.data).fields)
    }

    @Test
    fun testPubRestricted() {
        val input = parse("pub(in m) struct Z(pub(in m::n) u8);")

        assertRestrictedVisibility(input.vis, hasInToken = true, "m")
        val fields = unnamedFields(assertIs<Data.Struct>(input.data).fields)
        assertEquals(1, fields.size)
        assertRestrictedVisibility(fields.single().vis, hasInToken = true, "m", "n")
        assertPathType(fields.single().ty, "u8")
    }

    @Test
    fun testPubRestrictedCrate() {
        assertRestrictedVisibility(parse("pub(crate) struct S;").vis, hasInToken = false, "crate")
    }

    @Test
    fun testPubRestrictedSuper() {
        assertRestrictedVisibility(parse("pub(super) struct S;").vis, hasInToken = false, "super")
    }

    @Test
    fun testPubRestrictedInSuper() {
        assertRestrictedVisibility(parse("pub(in super) struct S;").vis, hasInToken = true, "super")
    }

    @Test
    fun testFieldsOnUnitStruct() {
        val data = assertIs<Data.Struct>(parse("struct S;").data).value

        assertIs<Fields.Unit>(data.fields)
        assertEquals(0, data.fields.count())
    }

    @Test
    fun testFieldsOnNamedStruct() {
        val data =
            assertIs<Data.Struct>(
                parse(
                    """
                    struct S {
                        foo: i32,
                        pub bar: String,
                    }
                    """.trimIndent(),
                ).data,
            ).value

        val fields = data.fields.toList()
        assertEquals(2, fields.size)
        assertNamedPathField(fields[0], "foo", "i32")
        assertIs<Visibility.Inherited>(fields[0].vis)
        assertPublicNamedPathField(fields[1], "bar", "String")
    }

    @Test
    fun testFieldsOnTupleStruct() {
        val data = assertIs<Data.Struct>(parse("struct S(i32, pub String);").data).value

        val fields = data.fields.toList()
        assertEquals(2, fields.size)
        assertNull(fields[0].ident)
        assertIs<Visibility.Inherited>(fields[0].vis)
        assertPathType(fields[0].ty, "i32")
        assertNull(fields[1].ident)
        assertIs<Visibility.Public>(fields[1].vis)
        assertPathType(fields[1].ty, "String")
    }

    @Test
    fun testTupleStructWhereClauseAfterFields() {
        val input = parse("struct S<T>(T) where T: Copy;")

        assertSingleTypeWhereClause(input.generics, "T", "Copy")
        val data = assertIs<Data.Struct>(input.data).value
        assertNotNull(data.semiToken)
        assertPathType(unnamedFields(data.fields).single().ty, "T")
    }

    @Test
    fun testWhereClauseBeforeDataBody() {
        val namedStruct = parse("struct S<T> where T: Copy { value: T }")
        assertSingleTypeWhereClause(namedStruct.generics, "T", "Copy")
        assertNamedPathField(namedFields(assertIs<Data.Struct>(namedStruct.data).fields).single(), "value", "T")

        val unitStruct = parse("struct Unit<T> where T: Copy;")
        assertSingleTypeWhereClause(unitStruct.generics, "T", "Copy")
        assertIs<Fields.Unit>(assertIs<Data.Struct>(unitStruct.data).fields)

        val enumInput = parse("enum E<T> where T: Copy { Value(T) }")
        assertSingleTypeWhereClause(enumInput.generics, "T", "Copy")
        val variant = assertIs<Data.Enum>(enumInput.data).variants.toList().single()
        assertPathType(unnamedFields(variant.fields).single().ty, "T")

        val unionInput = parse("union U<T> where T: Copy { value: T }")
        assertSingleTypeWhereClause(unionInput.generics, "T", "Copy")
        assertNamedPathField(assertIs<Data.Union>(unionInput.data).fields.named.toList().single(), "value", "T")
    }

    @Test
    fun testMalformedWhereClauseIsRejected() {
        assertTrue(parseStr(DeriveInputParse, "struct S where <T> { value: T }").isFailure)
        assertTrue(parseStr(DeriveInputParse, "enum E where <T> { Value }").isFailure)
        assertTrue(parseStr(DeriveInputParse, "union U where <T> { value: T }").isFailure)
    }

    @Test
    fun testDeriveInputToTokens() {
        val input = parse("pub struct S<T>(T) where T: Copy;")
        val tokens = TokenStream.new()

        input.toTokens(tokens)

        assertEquals("pub struct S < T > (T) where T : Copy ;", tokens.toString())
    }

    @Test
    fun deriveInputToTokensDefaultsMissingSemicolonForTupleAndUnitStructs() {
        val tupleInput = parse("struct S<T>(T) where T: Copy;")
        val tupleData = assertIs<Data.Struct>(tupleInput.data).value
        val tupleTokens = TokenStream.new()

        tupleInput.copy(data = Data.Struct(tupleData.copy(semiToken = null))).toTokens(tupleTokens)

        assertEquals("struct S < T > (T) where T : Copy ;", tupleTokens.toString())

        val unitInput = parse("struct Unit;")
        val unitData = assertIs<Data.Struct>(unitInput.data).value
        val unitTokens = TokenStream.new()

        unitInput.copy(data = Data.Struct(unitData.copy(semiToken = null))).toTokens(unitTokens)

        assertEquals("struct Unit ;", unitTokens.toString())
    }

    @Test
    fun testAmbiguousCrate() {
        val data = assertIs<Data.Struct>(parse("struct S(crate::X);").data).value

        val fields = unnamedFields(data.fields)
        assertEquals(1, fields.size)
        assertPathType(fields.single().ty, "crate", "X")
    }

    @Test
    fun fieldsMembersUseFieldTypeSpanForUnnamedFields() {
        val data = assertIs<Data.Struct>(parse("struct S(u8, (u8));").data).value
        val fields = assertIs<Fields.Unnamed>(data.fields)
        val fieldList = fields.fields.unnamed.toList()
        val members = fields.members().asSequence().toList()

        assertEquals(2, members.size)
        assertEquals(fieldList[0].ty.span(), assertIs<Member.Unnamed>(members[0]).index.span)
        assertEquals(fieldList[1].ty.span(), assertIs<Member.Unnamed>(members[1]).index.span)
    }

    private fun parse(source: String): DeriveInput =
        parseStr(DeriveInputParse, source).getOrThrow()

    private fun namedFields(fields: Fields): List<Field> =
        assertIs<Fields.Named>(fields).fields.named.toList()

    private fun unnamedFields(fields: Fields): List<Field> =
        assertIs<Fields.Unnamed>(fields).fields.unnamed.toList()

    private fun assertPublicNamedPathField(
        field: Field,
        ident: String,
        vararg segments: String,
    ) {
        assertIs<Visibility.Public>(field.vis)
        assertNamedPathField(field, ident, *segments)
    }

    private fun assertNamedPathField(
        field: Field,
        ident: String,
        vararg segments: String,
    ) {
        assertEquals(ident, field.ident.toString())
        assertNotNull(field.colonToken)
        assertPathType(field.ty, *segments)
    }

    private fun assertPathType(
        type: SynType,
        vararg segments: String,
    ): Path {
        val path = assertIs<SynType.Path>(type).path
        assertEquals(segments.toList(), pathSegments(path))
        return path
    }

    private fun pathSegments(path: Path): List<String> =
        path.segments.toList().map { it.ident.toString() }

    private fun assertTypeParams(
        generics: Generics,
        vararg names: String,
    ) {
        assertNotNull(generics.ltToken)
        assertNotNull(generics.gtToken)
        val params = generics.params.toList()
        assertEquals(names.size, params.size)
        for ((param, name) in params.zip(names)) {
            assertEquals(name, assertIs<GenericParam.TypeParam>(param).ident.toString())
        }
    }

    private fun assertSingleTypeWhereClause(
        generics: Generics,
        boundedTy: String,
        bound: String,
    ) {
        val whereClause = assertNotNull(generics.whereClause)
        val predicate = assertIs<WherePredicate.TypePredicate>(whereClause.predicates.toList().single())
        assertPathType(predicate.boundedTy, boundedTy)
        assertEquals(bound, assertIs<TypeParamBound.Trait>(predicate.bounds.toList().single()).path.toString())
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
        assertEquals(segments.toList(), pathSegments(restricted.path))
    }

    private fun assertIntLiteral(
        token: String,
        expr: Expr,
    ) {
        val lit = assertIs<Lit.Int>(assertIs<Expr.Lit>(expr).lit)
        assertEquals(token, lit.value.toString())
    }

    private fun assertStringLiteral(
        value: String,
        expr: Expr,
    ) {
        val lit = assertIs<Lit.Str>(assertIs<Expr.Lit>(expr).lit)
        assertEquals(value, lit.value.value())
    }

    private fun assertProcMacroHackDiscriminant(expr: Expr) {
        val field = assertIs<Expr.Field>(expr)
        val member = assertIs<Member.Unnamed>(field.member)
        assertEquals(0u, member.index.index)
        val tuple = assertIs<Expr.Tuple>(field.base)
        val elems = tuple.elems.toList()
        assertEquals(2, elems.size)
        assertIntLiteral("0", elems[0])
        assertStringLiteral("data", elems[1])
    }
}
