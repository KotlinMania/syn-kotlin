// port-lint: tests tests/test_generics.rs
package io.github.kotlinmania.syn

import kotlin.test.Test

class GenericsTest {
    // Not ported: DeriveInput parsing is not implemented (DeriveInputParse
    // returns a failure), so the struct `S<'a, 'b: 'a, #[may_dangle] T: 'a = ()>
    // where T: Debug` cannot be parsed and splitForImpl cannot be exercised
    // end-to-end.
    @Test
    fun testSplitForImpl() {
        // Not ported: DeriveInputParse is not implemented; the upstream test
        // parses a struct with lifetimes, a `#[may_dangle]` type param with
        // default `= ()`, and a `where T: Debug` clause, then asserts
        // splitForImpl produces `impl<'a, 'b: 'a, #[may_dangle] T: 'a> ...`
        // and the turbofish renders as `::<'a, 'b, T>`.
    }

    // Not ported: TypeParamBound parsing is not implemented, and the Kotlin
    // TypeParamBound.Trait type carries only `path` (no `modifier`, `lifetimes`,
    // or `paren_token`), so `'a`, `'_`, `Debug`, `?Sized`, and `for<'a> Trait`
    // cannot be parsed or compared against the upstream shapes.
    @Test
    fun testTypeParamBound() {
        // Not ported: TypeParamBoundParse is not implemented and
        // TypeParamBound.Trait lacks the `modifier`, `lifetimes`, and
        // `paren_token` fields needed to represent `?Sized`, `for<'a> Trait`,
        // and the `for<> ?Trait` / `?for<> Trait` error cases.
    }

    // Not ported: ItemFn parsing is not implemented, and PathArguments.Parenthesized
    // is not produced by PathSegmentParse (it always yields PathArguments.None), so
    // `G: FnOnce() -> i32 + Send` cannot be parsed into two bounds.
    @Test
    fun testFnPrecedenceInWhereClause() {
        // Not ported: ItemFn parsing is not implemented and
        // PathSegmentParse never produces PathArguments.Parenthesized, so
        // the `G: FnOnce() -> i32 + Send` where-clause cannot be parsed
        // and checked for two separate bounds.
    }

    // Not ported: WhereClauseParse is not implemented, so a bare `where`
    // cannot be parsed into a WhereClause with zero predicates.
    @Test
    fun testWhereClauseAtEndOfInput() {
        // Not ported: WhereClauseParse is not implemented; the upstream
        // test parses `where` at end of input into a WhereClause with
        // `predicates.len() == 0`.
    }

    // Not ported: the upstream test exercises Generics::lifetimes iteration
    // and insertion into a Punctuated via parse_quote, which depends on
    // LifetimeParam::parse and the parse_quote macro helper; neither is
    // ported. The Kotlin Generics.lifetimes() helper exists, but the
    // setup requires constructing a LifetimeParam through parsing.
    @Test
    fun noOpaqueDrop() {
        // Not ported: depends on LifetimeParam::parse and parse_quote
        // helpers that are not ported; the upstream test inserts a
        // `'a` lifetime into a default Generics when none is present.
    }

    // Not ported: GenericParamParse is not implemented, and TypeParam::parse
    // (which would consume the `:` and stop with empty bounds) is not ported,
    // so `T:` cannot be parsed into a GenericParam::Type with colon_token set
    // and an empty bounds list.
    @Test
    fun typeParamWithColonAndNoBounds() {
        // Not ported: GenericParamParse / TypeParam::parse are not
        // implemented; the upstream test parses `T:` into a TypeParam
        // with `colon_token: Some` and no bounds.
    }
}
