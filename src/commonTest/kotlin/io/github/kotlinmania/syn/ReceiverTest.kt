// port-lint: tests tests/test_receiver.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import kotlin.test.Test

/**
 * Tests for parsing of method receivers.
 *
 * The upstream Rust tests drive `parse_quote! { fn ...(...) }` to produce
 * a `TraitItemFn`, then inspect `sig.inputs[0]` as a `FnArg::Receiver`.
 * This requires a `ParseQuote<TraitItemFn>` (or `Parse<TraitItemFn>`)
 * entry point and the `parse_quote!` macro expansion, neither of which is
 * ported to this Kotlin codebase yet. The `snapshot!` macro expands to
 * `insta::assert_debug_snapshot!` against a `Lite` debug wrapper, which
 * is also not ported. Each test below carries an honest one-line comment
 * naming the specific missing semantic, rather than emitting a fake
 * simulation that tests a different invariant.
 *
 * The Kotlin port exposes [FnArg.Receiver] (with `reference`, `mutability`,
 * `selfToken`, `colonToken`, and `type` fields) and [Signature.receiver],
 * but without a `Parse<TraitItemFn>` implementation the receiver cannot
 * be populated from a token stream for structural assertion.
 */
class ReceiverTest {
    // Not ported: `Parse<TraitItemFn>` (the trait-method parser entry
    // point) is not implemented in this Kotlin port, so `fn by_value(self:
    // Self);` cannot be parsed into a `TraitItemFn` whose `sig.inputs[0]`
    // is a `FnArg.Receiver` with `colonToken` set and `type` being a path
    // to `Self`.
    @Test
    fun testByValue() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has a colon token and a path
        // type `Self`, with no reference and no mutability.
        TokenStream.fromString("fn by_value(self: Self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn by_mut(mut self: Self);` and asserts the receiver
    // has `mutability` set, a colon token, and a path type `Self`.
    @Test
    fun testByMutValue() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has mutability set, a colon
        // token, and a path type `Self`.
        TokenStream.fromString("fn by_mut(mut self: Self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn by_ref(self: &Self);` and asserts the receiver has
    // a colon token and a reference type whose element is a path to `Self`.
    @Test
    fun testByRef() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has a colon token and a
        // reference type wrapping a path to `Self`.
        TokenStream.fromString("fn by_ref(self: &Self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn by_box(self: Box<Self>);` and asserts the receiver
    // type is a path to `Box` with angle-bracketed generic argument
    // `Self`.
    @Test
    fun testByBox() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver type is a path to `Box` with
        // a single angle-bracketed generic argument being a path to
        // `Self`.
        TokenStream.fromString("fn by_box(self: Box<Self>);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn by_pin(self: Pin<Self>);` and asserts the receiver
    // type is a path to `Pin` with angle-bracketed generic argument
    // `Self`.
    @Test
    fun testByPin() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver type is a path to `Pin` with
        // a single angle-bracketed generic argument being a path to
        // `Self`.
        TokenStream.fromString("fn by_pin(self: Pin<Self>);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn explicit_type(self: Pin<MyType>);` and asserts the
    // receiver type is a path to `Pin` with angle-bracketed generic
    // argument `MyType`.
    @Test
    fun testExplicitType() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver type is a path to `Pin` with
        // a single angle-bracketed generic argument being a path to
        // `MyType`.
        TokenStream.fromString("fn explicit_type(self: Pin<MyType>);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn value_shorthand(self);` and asserts the receiver
    // has no colon token and an implicit path type `Self`.
    @Test
    fun testValueShorthand() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has no colon token (shorthand
        // form) and the implicit type is a path to `Self`, with no
        // reference and no mutability.
        TokenStream.fromString("fn value_shorthand(self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn mut_value_shorthand(mut self);` and asserts the
    // receiver has mutability set, no colon token, and an implicit path
    // type `Self`.
    @Test
    fun testMutValueShorthand() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has mutability set, no colon
        // token, and an implicit path type `Self`.
        TokenStream.fromString("fn mut_value_shorthand(mut self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn ref_shorthand(&self);` and asserts the receiver has
    // `reference: Some(None)` (an ampersand with no lifetime) and a
    // reference type wrapping a path to `Self`.
    @Test
    fun testRefShorthand() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has `reference` set with no
        // lifetime and the type is a reference wrapping a path to `Self`.
        TokenStream.fromString("fn ref_shorthand(&self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn ref_shorthand(&'a self);` and asserts the receiver
    // has `reference: Some(Some(Lifetime { ident: "a" }))` and a
    // reference type with the same lifetime wrapping a path to `Self`.
    @Test
    fun testRefShorthandWithLifetime() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has `reference` set with a
        // lifetime named `a`, and the reference type carries the same
        // lifetime and wraps a path to `Self`.
        TokenStream.fromString("fn ref_shorthand(&'a self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn ref_mut_shorthand(&mut self);` and asserts the
    // receiver has `reference: Some(None)`, `mutability` set, and a
    // mutable reference type wrapping a path to `Self`.
    @Test
    fun testRefMutShorthand() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has `reference` set with no
        // lifetime, mutability set, and a mutable reference type wrapping
        // a path to `Self`.
        TokenStream.fromString("fn ref_mut_shorthand(&mut self);").getOrThrow()
    }

    // Not ported: `Parse<TraitItemFn>` is not implemented; the upstream
    // test parses `fn ref_mut_shorthand(&'a mut self);` and asserts the
    // receiver has `reference: Some(Some(Lifetime { ident: "a" }))`,
    // `mutability` set, and a mutable reference type with lifetime `a`
    // wrapping a path to `Self`.
    @Test
    fun testRefMutShorthandWithLifetime() {
        // Not ported: `Parse<TraitItemFn>` is not implemented; the
        // upstream test asserts the receiver has `reference` set with a
        // lifetime named `a`, mutability set, and a mutable reference
        // type carrying the same lifetime and wrapping a path to `Self`.
        TokenStream.fromString("fn ref_mut_shorthand(&'a mut self);").getOrThrow()
    }
}
