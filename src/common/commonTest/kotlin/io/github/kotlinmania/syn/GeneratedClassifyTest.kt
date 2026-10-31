package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Observations from original Rust Syn, retained with the generator receipt. */
class GeneratedClassifyTest {
    @Test
    fun adoptedClassifiersMatchRustObservations() {
        val cases =
            listOf(
                "1" to "true|true",
                "f()" to "true|true",
                "m!()" to "true|true",
                "m!{}" to "false|true",
                "m![]" to "true|true",
                "{1}" to "false|false",
                "unsafe {1}" to "false|false",
                "if true {1} else {2}" to "false|false",
                "match x { _ => 1 }" to "false|false",
                "while false {}" to "false|false",
                "loop {}" to "false|false",
                "for x in xs {}" to "false|false",
                "async {}" to "true|true",
                "const {1}" to "false|false",
                "try {}" to "false|false",
                "x.await" to "true|true",
                "return 1" to "true|true",
                "'label: loop {}" to "false|false",
                "{ /* kept */ 1 }" to "false|false",
            )
        for ((source, expected) in cases) {
            val expr = parseStr(::parseExpr, source).getOrThrow()
            assertEquals(expected, "${requiresSemiToBeStmt(expr)}|${requiresCommaToBeMatchArm(expr)}", source)
        }
    }

    @Test
    fun generatedTypeTraversalMatchesRust() {
        val cases =
            listOf(
                "u8" to true,
                "Vec<u8>" to false,
                "&u8" to true,
                "*const u8" to true,
                "*mut Vec<u8>" to false,
                "fn()" to false,
                "fn() -> u8" to true,
                "fn() -> Vec<u8>" to false,
                "dyn Fn()" to false,
                "dyn Fn() -> u8" to true,
                "impl Fn() -> Vec<u8>" to false,
                "impl A + B" to true,
                "impl B + 'a" to false,
                "dyn A + 'a" to false,
                "[u8; 2]" to false,
                "[u8]" to false,
                "(u8)" to false,
                "(u8,)" to false,
                "()" to false,
                "_" to false,
                "!" to false,
                "m!()" to false,
                "dyn Fn() -> fn() -> u8" to true,
                "impl Fn() -> dyn A" to true,
                "dyn for<'a> Fn(&'a u8) -> u8" to true,
            )
        for ((source, expected) in cases) {
            val type = parseStr(SynType::parse, source).getOrThrow()
            assertEquals(expected, trailingUnparameterizedPath(type), source)
        }
    }

    @Test
    fun generatedTypeTraversalHandlesConstructedNodesAndTrailingPunctuation() {
        val inner = parseStr(SynType::parse, "u8").getOrThrow()
        assertEquals(
            false,
            trailingUnparameterizedPath(
                SynType.Group(
                    io.github.kotlinmania.syn.token.Group
                        .default(),
                    inner,
                ),
            ),
        )
        assertEquals(
            false,
            trailingUnparameterizedPath(
                SynType.Verbatim(
                    io.github.kotlinmania.procmacro2.TokenStream
                        .new(),
                ),
            ),
        )
        val trailing = parseStr(SynType::parse, "impl A + B").getOrThrow() as SynType.ImplTrait
        trailing.bounds.pushPunct(
            io.github.kotlinmania.syn.token.Plus
                .default(),
        )
        assertEquals(true, trailingUnparameterizedPath(trailing))
    }

    @Test
    fun generatedTypeTraversalPreservesRustEmptySequencePanics() {
        // These are constructible ASTs, not claims about valid parsed type input.
        val empty =
            listOf(
                SynType.Path(null, Path(null, PathSegmentList())),
                SynType.ImplTrait(
                    io.github.kotlinmania.syn.token.Impl
                        .default(),
                    TypeParamBoundList(),
                ),
                SynType.TraitObject(null, TypeParamBoundList()),
            )
        for (type in empty) {
            val error = assertFailsWith<RuntimeException> { trailingUnparameterizedPath(type) }
            assertEquals("called `Option::unwrap()` on a `None` value", error.message)
        }
    }

    @Test
    fun completeGeneratedExpressionTraversalMatchesRust() {
        val cases =
            listOf(
                "1" to "true|true|false|false",
                "f()" to "true|true|false|false",
                "x + y" to "true|true|false|false",
                "[a]" to "true|true|false|false",
                "a = b" to "true|true|false|false",
                "async {}" to "true|true|false|true",
                "x.await" to "true|true|false|false",
                "break" to "true|true|false|false",
                "break {}" to "true|true|false|true",
                "0 as u8" to "true|true|false|false",
                "|| {}" to "true|true|false|true",
                "const {}" to "false|false|false|true",
                "continue" to "true|true|false|false",
                "x.field" to "true|true|false|false",
                "for x in xs {}" to "false|false|false|true",
                "if a {}" to "false|false|false|true",
                "x[0]" to "true|true|false|false",
                "_" to "true|true|false|false",
                "loop {}" to "false|false|false|true",
                "m!{}" to "false|true|false|true",
                "m!()" to "true|true|false|false",
                "match a { _ => {} }" to "false|false|false|true",
                "x.method()" to "true|true|false|false",
                "(x)" to "true|true|false|false",
                "x.." to "true|true|false|false",
                "..{}" to "true|true|false|true",
                "&raw const {}" to "true|true|false|true",
                "&{}" to "true|true|false|true",
                "[a;2]" to "true|true|false|false",
                "return" to "true|true|false|false",
                "return {}" to "true|true|false|true",
                "Foo{}" to "true|true|false|true",
                "(x,)" to "true|true|false|false",
                "x?" to "true|true|false|false",
                "try {}" to "false|false|false|true",
                "!{}" to "true|true|false|true",
                "unsafe {}" to "false|false|false|true",
                "while a {}" to "false|false|false|true",
                "yield" to "true|true|false|false",
                "yield {}" to "true|true|false|true",
                "'lab: loop {}" to "false|false|true|true",
                "'lab: {}" to "false|false|true|true",
                "'lab: loop {} + y" to "true|true|true|false",
                "('lab: loop {}) + y" to "true|true|false|false",
                "0 as m!{}" to "true|true|false|true",
                "0 as fn() -> m!{}" to "true|true|false|true",
                "0 as dyn Fn() -> m!{}" to "true|true|false|true",
            )
        for ((source, expected) in cases) {
            val expr = parseStr(ExprParse::parse, source).getOrThrow()
            assertEquals(expected, "${requiresSemiToBeStmt(expr)}|${requiresCommaToBeMatchArm(expr)}|${exprLeadingLabel(expr)}|${exprTrailingBrace(expr)}", source)
        }
    }

    @Test
    fun generatedTokenTraversalPreservesOuterGroupsAndOriginalStream() {
        val cases =
            listOf(
                "" to false,
                "{}" to true,
                "x {}" to true,
                "{} x" to false,
                "()" to false,
                "[]" to false,
                "\"}\"" to false,
                "/* } */ {}" to true,
                "{} /* } */" to true,
            )
        fun check(tokens: io.github.kotlinmania.procmacro2.TokenStream, expected: Boolean) {
            val before = tokens.toString()
            assertEquals(expected, exprTrailingBrace(Expr.Verbatim(tokens.clone())))
            val cast =
                Expr.Cast(
                    mutableListOf(),
                    parseStr(ExprParse::parse, "0").getOrThrow(),
                    io.github.kotlinmania.syn.token.As
                        .default(),
                    SynType.Verbatim(tokens.clone()),
                )
            assertEquals(expected, exprTrailingBrace(cast))
            assertEquals(before, tokens.toString())
        }
        for ((source, expected) in cases) {
            check(
                io.github.kotlinmania.procmacro2.TokenStream
                    .fromString(source)
                    .getOrThrow(),
                expected,
            )
        }
        // Rust checks the last outer group: a delimiterless wrapper is not a brace.
        check(
            io.github.kotlinmania.procmacro2.TokenStream
                .fromTokenTree(
                    io.github.kotlinmania.procmacro2.TokenTree
                        .Group(
                            io.github.kotlinmania.procmacro2
                                .Group(
                                    io.github.kotlinmania.procmacro2.Delimiter.None,
                                    io.github.kotlinmania.procmacro2.TokenStream
                                        .fromString("{}")
                                        .getOrThrow(),
                                ),
                        ),
                ),
            false,
        )
    }

    @Test
    fun generatedCastTraversalPreservesRustEmptySequencePanics() {
        // Constructible malformed ASTs; no claim that parsed Rust reaches these.
        val types =
            listOf(
                SynType.Path(null, Path(null, PathSegmentList())),
                SynType.ImplTrait(
                    io.github.kotlinmania.syn.token.Impl
                        .default(),
                    TypeParamBoundList(),
                ),
                SynType.TraitObject(null, TypeParamBoundList()),
            )
        for (type in types) {
            val cast =
                Expr.Cast(
                    mutableListOf(),
                    parseStr(ExprParse::parse, "0").getOrThrow(),
                    io.github.kotlinmania.syn.token.As
                        .default(),
                    type,
                )
            val error = assertFailsWith<RuntimeException> { exprTrailingBrace(cast) }
            assertEquals("called `Option::unwrap()` on a `None` value", error.message)
        }
    }
}
