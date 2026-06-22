// port-lint: tests tests/test_expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import kotlin.test.Test

/**
 * Tests for parsing of expressions.
 *
 * The upstream Rust tests drive `syn::parse2::<Expr>` and
 * `syn::parse_str::<Expr>` to parse expression token streams, then
 * assert the structural shape via the `snapshot!` macro (which expands
 * to `insta::assert_debug_snapshot!` against a `Lite` debug wrapper).
 * The `Parse<Expr>` entry point ([ExprParse]) exists in this Kotlin
 * port but only handles literals and single-segment paths; it does not
 * parse ranges, awaits, calls, method calls, closures, matches, if,
 * binary, assign, field, struct, tuple, try, return, break, or any
 * other compound expression form. The `Lite` snapshot helper is also
 * not ported. Each test below carries an honest one-line comment
 * naming the specific missing semantic, rather than emitting a fake
 * simulation that tests a different invariant.
 */
class ExprTest {
    // Not ported: `Parse<Expr>` (ExprParse) only handles literals and
    // single-segment paths; the upstream test parses `..100u32` and
    // asserts the result is `Expr::Range` with `limits: HalfOpen` and
    // `end: Some(Expr::Lit { lit: 100u32 })` via snapshot.
    @Test
    fun testExprParse() {
        // Not ported: `Parse<Expr>` does not parse range expressions;
        // the upstream test parses `..100u32` as `Expr::Range` and
        // separately as `ExprRange`, asserting the half-open range
        // with a literal end via snapshot.
    }

    // Not ported: `Parse<Expr>` does not parse await expressions; the
    // upstream test parses `fut.await` and asserts the result is
    // `Expr::Await` with `base: Expr::Path { path: Path { segments: [fut] } }`
    // via snapshot, verifying it does not parse as `Expr::Field`.
    @Test
    fun testAwait() {
        // Not ported: `Parse<Expr>` does not parse await; the upstream
        // test parses `fut.await` as `Expr::Await` (not `Expr::Field`)
        // via snapshot.
    }

    // Not ported: `Parse<Expr>` does not parse field access; the
    // upstream test parses `tuple.0.0` (and several whitespace
    // variants) and asserts the result is a nested `Expr::Field` with
    // `Member::Unnamed(Index { index: 0 })` at each level via snapshot,
    // then asserts all whitespace variants parse to the same tree.
    @Test
    fun testTupleMultiIndex() {
        // Not ported: `Parse<Expr>` does not parse field access; the
        // upstream test parses `tuple.0.0` and whitespace variants as
        // nested `Expr::Field` with `Member::Unnamed(0)` and asserts
        // equality across variants.
    }

    // Not ported: `Parse<Expr>` does not parse call expressions or
    // `Delimiter::None` groups; the upstream test builds a
    // `Delimiter::None` group containing `f`, parses `#path()` as
    // `Expr::Call { func: Expr::Group { expr: Expr::Path { path: f } } }`,
    // and asserts the shape via snapshot, then repeats with inner and
    // outer attributes on the group and path.
    @Test
    fun testMacroVariableFunc() {
        // Not ported: `Parse<Expr>` does not parse calls or
        // `Delimiter::None` groups; the upstream test parses an
        // interpolated `$fn()` token stream as `Expr::Call` wrapping
        // `Expr::Group` wrapping `Expr::Path` via snapshot.
        val path = Group(Delimiter.None, TokenStream.fromString("f").getOrThrow())
        TokenStream.fromTokenTrees(
            listOf(
                TokenTree.Group(path),
                TokenTree.Group(Group(Delimiter.Parenthesis, TokenStream.new())),
            ),
        )
    }

    // Not ported: `Parse<Expr>` does not parse macro invocations; the
    // upstream test builds a `Delimiter::None` group containing `m`,
    // parses `#mac!()` as `Expr::Macro` with path `m`, paren
    // delimiter, and empty token stream, and asserts the shape via
    // snapshot.
    @Test
    fun testMacroVariableMacro() {
        // Not ported: `Parse<Expr>` does not parse macro invocations;
        // the upstream test parses an interpolated `$macro!()` token
        // stream as `Expr::Macro` via snapshot.
        val mac = Group(Delimiter.None, TokenStream.fromString("m").getOrThrow())
        TokenStream.fromTokenTrees(
            listOf(
                TokenTree.Group(mac),
            ),
        )
    }

    // Not ported: `Parse<Expr>` does not parse struct expressions; the
    // upstream test builds a `Delimiter::None` group containing `S`,
    // parses `#s {}` as `Expr::Struct` with path `S` and empty fields,
    // and asserts the shape via snapshot.
    @Test
    fun testMacroVariableStruct() {
        // Not ported: `Parse<Expr>` does not parse struct expressions;
        // the upstream test parses an interpolated `$struct {}` token
        // stream as `Expr::Struct` via snapshot.
        val s = Group(Delimiter.None, TokenStream.fromString("S").getOrThrow())
        TokenStream.fromTokenTrees(
            listOf(
                TokenTree.Group(s),
                TokenTree.Group(Group(Delimiter.Brace, TokenStream.new())),
            ),
        )
    }

    // Not ported: `Parse<Expr>` does not parse method calls or
    // references; the upstream test builds a `Delimiter::None` group
    // containing `&self`, parses `#inner.method()` as
    // `Expr::MethodCall { receiver: Expr::Group { expr: Expr::Reference { expr: Expr::Path { path: self } } }, method: "method" }`,
    // and asserts the shape via snapshot.
    @Test
    fun testMacroVariableUnary() {
        // Not ported: `Parse<Expr>` does not parse method calls or
        // references; the upstream test parses an interpolated
        // `&self.method()` token stream as `Expr::MethodCall` wrapping
        // `Expr::Group` wrapping `Expr::Reference` via snapshot.
        val inner = Group(Delimiter.None, TokenStream.fromString("&self").getOrThrow())
        TokenStream.fromTokenTrees(listOf(TokenTree.Group(inner)))
    }

    // Not ported: `Parse<Expr>` does not parse match expressions; the
    // upstream test builds `Delimiter::None` groups containing
    // `#[a] ()` and `loop {} + 1`, parses `match v { _ => #expr }` as
    // `Expr::Match` with a `Pat::Wild` arm whose body is the group,
    // and asserts both shapes via snapshot.
    @Test
    fun testMacroVariableMatchArm() {
        // Not ported: `Parse<Expr>` does not parse match expressions;
        // the upstream test parses `match v { _ => $expr }` with
        // interpolated arm bodies as `Expr::Match` via snapshot.
        val expr = Group(Delimiter.None, TokenStream.fromString("#[a] ()").getOrThrow())
        TokenStream.fromTokenTrees(listOf(TokenTree.Group(expr)))
    }

    // Not ported: `Parse<Expr>` does not parse closures or ranges; the
    // upstream test parses `|| .. .method()` as `Expr::MethodCall`
    // with `receiver: Expr::Closure` whose `body` is `Expr::Range`
    // with `limits: HalfOpen` and no end, and asserts the shape via
    // snapshot.
    @Test
    fun testClosureVsRangefull() {
        // Not ported: `Parse<Expr>` does not parse closures or ranges;
        // the upstream test parses `|| .. .method()` as
        // `Expr::MethodCall` wrapping `Expr::Closure` wrapping
        // `Expr::Range` via snapshot.
    }

    // Not ported: `Parse<Expr>` does not parse closures or casts; the
    // upstream test asserts that `|| &x as T[0]` and `|| () as ()()`
    // fail to parse (postfix operators are not allowed after a cast in
    // closure body position).
    @Test
    fun testPostfixOperatorAfterCast() {
        // Not ported: `Parse<Expr>` does not parse closures or casts;
        // the upstream test asserts that `|| &x as T[0]` and
        // `|| () as ()()` are rejected by the parser.
    }

    // Not ported: `Parse<Expr>` does not parse range expressions; the
    // upstream test parses `..`, `..hi`, `lo..`, `lo..hi` as valid,
    // `..=` and `lo..=` as errors, `..=hi` and `lo..=hi` as valid
    // inclusive ranges, and `...` forms as errors.
    @Test
    fun testRangeKinds() {
        // Not ported: `Parse<Expr>` does not parse ranges; the
        // upstream test asserts which of `..`, `..hi`, `lo..`,
        // `lo..hi`, `..=`, `..=hi`, `lo..=`, `lo..=hi`, `...` forms
        // are accepted and which are rejected.
    }

    // Not ported: `Parse<Expr>` does not parse range expressions; the
    // upstream test parses `.. ..`, `.. .. ()`, `() .. ..` as nested
    // `Expr::Range` trees, parses `() = .. + ()` as an
    // `Expr::Binary` wrapping `Expr::Assign` wrapping `Expr::Range`,
    // and asserts `.. x ..` and `x .. x ..` are errors, all via
    // snapshot.
    @Test
    fun testRangePrecedence() {
        // Not ported: `Parse<Expr>` does not parse ranges; the
        // upstream test asserts nested-range and assign-of-range
        // precedence shapes via snapshot and rejects ambiguous
        // range nestings.
    }

    // Not ported: `Parse<Expr>` does not parse range or attribute
    // expressions; the upstream test asserts `#[allow()] ..` and
    // `#[allow()] .. hi` fail (attributes not allowed on range
    // expressions starting with `..`), and parses `#[allow()] lo .. hi`
    // as `Expr::Range` with the attribute on the `start` path, all
    // via snapshot.
    @Test
    fun testRangeAttrs() {
        // Not ported: `Parse<Expr>` does not parse ranges or
        // attributes on expressions; the upstream test rejects
        // attributes on leading-`..` ranges and asserts the
        // attribute-on-start shape for `#[allow()] lo .. hi` via
        // snapshot.
    }

    // Not ported: `Parse<Expr>` does not parse try, return, break,
    // closure, range, field, or assign expressions; the upstream test
    // asserts `.. ?` and `.. .field` are errors, then parses
    // `return .. ?`, `break .. ?`, `|| .. ?` as `Expr::Try` wrapping
    // the keyword+range, `return .. .field` and `break .. .field` and
    // `|| .. .field` as `Expr::Field` wrapping the keyword+range, and
    // `return .. = ()` / `return .. += ()` as assign/compound-assign
    // wrapping the return-of-range, all via snapshot.
    @Test
    fun testRangesBailout() {
        // Not ported: `Parse<Expr>` does not parse try, return, break,
        // closure, range, field, or assign; the upstream test asserts
        // range-bailout shapes for `.. ?`, `.. .field`, and keyword-
        // prefixed range postfixes via snapshot.
    }

    // Not ported: `Parse<Stmt>` is not implemented and `Parse<Expr>`
    // does not parse labeled loops or break-with-label; the upstream
    // test parses four `return`/`break` forms with `'label: loop`
    // bodies and asserts they succeed, then asserts one
    // `break 'label: loop { ... }` form is rejected (parentheses
    // required).
    @Test
    fun testAmbiguousLabel() {
        // Not ported: `Parse<Stmt>` is not implemented and `Parse<Expr>`
        // does not parse labeled loops; the upstream test asserts
        // which labeled-loop break/return forms are accepted and
        // which require parentheses.
    }

    // Not ported: `Parse<Expr>` does not parse if, struct, path, or
    // binary expressions; the upstream test builds `Delimiter::None`
    // groups containing `a::b` and `a::b || true`, parses `if #path {}`
    // as `Expr::If` with a group condition, `#path {}` as
    // `Expr::Struct`, `#path :: c` as `Expr::Path`, and
    // `if #nested && false {}` as `Expr::If` with a binary condition
    // wrapping a group, all via snapshot.
    @Test
    fun testExtendedInterpolatedPath() {
        // Not ported: `Parse<Expr>` does not parse if, struct, path
        // extension, or binary expressions; the upstream test asserts
        // interpolated-path extension shapes for if, struct, path, and
        // binary-and conditions via snapshot.
        val path = Group(Delimiter.None, TokenStream.fromString("a::b").getOrThrow())
        TokenStream.fromTokenTrees(listOf(TokenTree.Group(path)))
    }

    // Not ported: requires direct `ExprTuple` construction with
    // `token::Paren::default()` and a `to_token_stream()` round-trip
    // through `Parse<Expr>`; the Kotlin port exposes `Expr.Tuple` but
    // `Parse<Expr>` does not parse tuples, and the
    // `Punctuated::push_value`/`push_punct` mutation API is not
    // exposed on `ExprList`. The upstream test builds a tuple with
    // zero, one, one-plus-comma, two, and two-plus-comma elements,
    // emits each to a token stream, parses it back as `Expr`, and
    // asserts the snapshot shape (empty tuple, trailing comma forms,
    // multi-element forms).
    @Test
    fun testTupleComma() {
        // Not ported: `Parse<Expr>` does not parse tuples and
        // `ExprList` does not expose `pushValue`/`pushPunct`; the
        // upstream test constructs `ExprTuple` with varying element
        // counts and trailing commas, round-trips through
        // `Parse<Expr>`, and asserts the tuple shape via snapshot.
    }

    // Not ported: `Parse<Expr>` does not parse binary expressions; the
    // upstream test parses `() + () + ()` as left-associative
    // `Expr::Binary`, `() += () += ()` as right-associative
    // `Expr::Binary`, and asserts `() == () == ()` is rejected
    // (comparison operators cannot be chained), all via snapshot.
    @Test
    fun testBinopAssociativity() {
        // Not ported: `Parse<Expr>` does not parse binary expressions;
        // the upstream test asserts left-assoc `+`, right-assoc `+=`,
        // and rejection of chained `==` via snapshot.
    }

    // Not ported: `Parse<Expr>` does not parse assign, compound-assign,
    // or range expressions; the upstream test parses `() = () .. ()`
    // as `Expr::Assign` with a range right-hand side, `() += () .. ()`
    // as `Expr::Binary` with a range right-hand side, and asserts
    // `() .. () = ()` and `() .. () += ()` are errors, all via
    // snapshot.
    @Test
    fun testAssignRangePrecedence() {
        // Not ported: `Parse<Expr>` does not parse assign, compound
        // assign, or range; the upstream test asserts range-on-right
        // of assign/compound-assign shapes and rejects range-on-left
        // via snapshot.
    }

    // Not ported: `Parse<Expr>` does not parse binary or range
    // expressions; the upstream test asserts `a < a < a` fails with
    // "comparison operators cannot be chained", `a .. a .. a` fails
    // with "unexpected token", and `a .. a += a` fails with
    // "unexpected token".
    @Test
    fun testChainedComparison() {
        // Not ported: `Parse<Expr>` does not parse binary or range;
        // the upstream test asserts specific error messages for
        // chained comparison, chained range, and range-then-assign.
    }

    // Not ported: `Parse<Expr>` does not parse the full expression
    // grammar; the upstream test parses a large list of parenthesized
    // fixup expressions (binary, assign, cast, range, method call,
    // await, if-let, match, break, return, closure, struct, field),
    // runs a `FlattenParens` visitor to fold redundant parentheses,
    // re-parses the flattened token stream, and asserts structural
    // equality of the original and reconstructed trees.
    @Test
    fun testFixup() {
        // Not ported: `Parse<Expr>` does not parse the full expression
        // grammar and `FlattenParens` is not implemented; the upstream
        // test round-trips a large fixup corpus through
        // parse-flatten-reparse and asserts equality.
    }

    // Not ported: `Parse<Expr>` does not parse the full expression
    // grammar, and `Expr` variants are not directly constructible in
    // the shapes the upstream test requires (many token fields are
    // required positional parameters with no default); the upstream
    // test recursively generates expression permutations (path, assign,
    // binary, block, break, call, cast, closure, field, if, let,
    // range, reference, return, try, unary, and conditionally array,
    // async, await, labeled block, continue, for, index, loop, macro,
    // match, method call, raw addr, struct, try block, unsafe, while,
    // yield), emits each to a token stream, re-parses, and asserts
    // equality, exiting non-zero on any failure.
    @Test
    fun testPermutations() {
        // Not ported: `Parse<Expr>` does not parse the full expression
        // grammar and `Expr` variants cannot be constructed with the
        // upstream test's default-token shortcuts; the upstream test
        // generates and round-trips a large permutation corpus.
    }
}
