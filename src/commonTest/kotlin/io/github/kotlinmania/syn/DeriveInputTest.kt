// port-lint: tests tests/test_derive_input.rs
package io.github.kotlinmania.syn

import kotlin.test.Test

/**
 * Tests for parsing of derive macro inputs.
 *
 * The upstream Rust tests drive `syn::parse2::<DeriveInput>` and
 * `syn::parse_quote!` to parse struct, enum, and union declarations,
 * then assert the structural shape via the `snapshot!` macro (which
 * expands to `insta::assert_debug_snapshot!` against a `Lite` debug
 * wrapper). The `Parse<DeriveInput>` entry point
 * ([DeriveInputParse]) exists in this Kotlin port but returns
 * `SynResult.failure` with "derive input parsing not yet fully
 * implemented", so no input can be driven through to a structural
 * assertion. The `Lite` snapshot helper is also not ported. Each test
 * below carries an honest one-line comment naming the specific missing
 * semantic, rather than emitting a fake simulation that tests a
 * different invariant.
 */
class DeriveInputTest {
    // Not ported: `Parse<DeriveInput>` (DeriveInputParse) returns
    // failure for all inputs; the upstream test parses `struct Unit;`
    // and asserts the result is a `DeriveInput` with
    // `vis: Inherited`, `ident: "Unit"`, `data: Data::Struct` with
    // `Fields::Unit` and a present `semi_token`.
    @Test
    fun testUnit() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `struct Unit;` and asserts the
        // unit-struct shape via snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses a `#[derive(Debug, Clone)] pub struct Item { ... }`
    // with two named fields and asserts the attribute, visibility,
    // identifier, and field shapes via snapshot.
    @Test
    fun testStruct() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses a derived pub struct with two named
        // fields and asserts the full shape including the `derive`
        // attribute meta and each field's visibility, identifier, and
        // type path.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `union MaybeUninit<T> { uninit: (), value: T }` and
    // asserts the generic parameter `T` and the two named union fields
    // via snapshot.
    @Test
    fun testUnion() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses a generic union with two named fields
        // and asserts the generics and `Data::Union` shape via
        // snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `pub enum Result<T, E> { Ok(T), Err(E), Surprise = 0isize, ProcMacroHack = (0, "data").0 }`
    // and asserts the doc and `must_use` attributes, two type
    // parameters, four variants (tuple, tuple, unit-with-discriminant,
    // unit-with-field-access-discriminant) via snapshot.
    @Test
    fun testEnum() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses a generic pub enum with four variants
        // and asserts the attributes, generics, and variant shapes
        // including discriminant expressions via snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `#[inert <T>] struct S;` and asserts that parsing
    // fails with an error (non-mod-style path in attribute).
    @Test
    fun testAttrWithNonModStylePath() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test asserts that `#[inert <T>] struct S;` fails
        // to parse because `<T>` is not a valid mod-style path.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `#[foo::self] struct S;` and asserts the attribute
    // path has segments `foo` and `self` via snapshot.
    @Test
    fun testAttrWithModStylePathWithSelf() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `#[foo::self] struct S;` and asserts
        // the attribute path segments `foo` and `self` via snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `pub(in m) struct Z(pub(in m::n) u8);` and asserts
    // the restricted visibility on both the struct and the field via
    // snapshot.
    @Test
    fun testPubRestricted() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `pub(in m) struct Z(pub(in m::n) u8);`
        // and asserts the `Visibility::Restricted` shapes on the item
        // and the tuple field via snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `pub(crate) struct S;` and asserts the restricted
    // visibility path is `crate` via snapshot.
    @Test
    fun testPubRestrictedCrate() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `pub(crate) struct S;` and asserts the
        // `Visibility::Restricted` path segment `crate` via snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `pub(super) struct S;` and asserts the restricted
    // visibility path is `super` via snapshot.
    @Test
    fun testPubRestrictedSuper() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `pub(super) struct S;` and asserts the
        // `Visibility::Restricted` path segment `super` via snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `pub(in super) struct S;` and asserts the restricted
    // visibility has `in_token: Some` and path `super` via snapshot.
    @Test
    fun testPubRestrictedInSuper() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `pub(in super) struct S;` and asserts
        // the `Visibility::Restricted` with `in_token` present and
        // path segment `super` via snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `struct S;` and asserts the unit-struct shape, then
    // matches `Data::Struct` and asserts `fields.iter().count() == 0`.
    @Test
    fun testFieldsOnUnitStruct() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `struct S;`, matches `Data::Struct`,
        // and asserts the field iterator yields zero elements.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `struct S { foo: i32, pub bar: String }` and asserts
    // the two named fields with their visibilities and types via
    // snapshot, then collects the fields and asserts the collected
    // vector shape.
    @Test
    fun testFieldsOnNamedStruct() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses a named-field struct, asserts the
        // `Fields::Named` shape via snapshot, and collects the fields
        // into a vector for a second snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `struct S(i32, pub String);` and asserts the two
    // unnamed fields with their visibilities and types via snapshot,
    // then iterates the fields and asserts the collected vector shape.
    @Test
    fun testFieldsOnTupleStruct() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses a tuple struct, asserts the
        // `Fields::Unnamed` shape via snapshot, and iterates the
        // fields for a second snapshot.
    }

    // Not ported: `Parse<DeriveInput>` is not implemented; the upstream
    // test parses `struct S(crate::X);` and asserts the field type is
    // a `Type::Path` with segments `crate` and `X` (not `crate (::X)`),
    // documenting the disambiguation rule for the `crate` keyword in
    // type position.
    @Test
    fun testAmbiguousCrate() {
        // Not ported: `Parse<DeriveInput>` is not implemented; the
        // upstream test parses `struct S(crate::X);` and asserts the
        // field type path is `crate::X` via snapshot.
    }
}
