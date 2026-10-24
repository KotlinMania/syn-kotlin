package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Observations from original Rust Syn, retained with the generator receipt. */
class GeneratedClassifyTest {
    @Test
    fun adoptedClassifiersMatchRustObservations() {
        val cases = listOf(
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
            assertEquals(expected, "${requires_semi_to_be_stmt(expr)}|${requires_comma_to_be_match_arm(expr)}", source)
        }
    }

    @Test
    fun generatedTypeTraversalMatchesRust() {
        val cases = listOf(
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
            assertEquals(expected, trailing_unparameterized_path(type), source)
        }
    }

    @Test
    fun generatedTypeTraversalHandlesConstructedNodesAndTrailingPunctuation() {
        val inner = parseStr(SynType::parse, "u8").getOrThrow()
        assertEquals(false, trailing_unparameterized_path(SynType.Group(io.github.kotlinmania.syn.token.Group.default(), inner)))
        assertEquals(false, trailing_unparameterized_path(SynType.Verbatim(io.github.kotlinmania.procmacro2.TokenStream.new())))
        val trailing = parseStr(SynType::parse, "impl A + B").getOrThrow() as SynType.ImplTrait
        trailing.bounds.pushPunct(io.github.kotlinmania.syn.token.Plus.default())
        assertEquals(true, trailing_unparameterized_path(trailing))
    }

    @Test
    fun generatedTypeTraversalPreservesRustEmptySequencePanics() {
        // These are constructible ASTs, not claims about valid parsed type input.
        val empty = listOf(
            SynType.Path(null, Path(null, PathSegmentList())),
            SynType.ImplTrait(io.github.kotlinmania.syn.token.Impl.default(), TypeParamBoundList()),
            SynType.TraitObject(null, TypeParamBoundList()),
        )
        for (type in empty) {
            val error = assertFailsWith<RuntimeException> { trailing_unparameterized_path(type) }
            assertEquals("called `Option::unwrap()` on a `None` value", error.message)
        }
    }
}
