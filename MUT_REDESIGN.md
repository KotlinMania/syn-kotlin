# VisitMut in-place mutation redesign

## Problem

Rust's `&mut T` lets a method mutate the caller's field in place. Kotlin has no
equivalent. The current syn-kotlin `VisitMut` works around this by having `*Mut`
methods return the (possibly new) object, and requiring every call site to
assign the result back:

```kotlin
e.tokens = visitTokenStreamMut(e.tokens)
```

This is bug-prone. The previous audit found **14 call sites** where the result
was silently discarded — the mutation was lost. The root cause is a mismatch
between Rust's `&mut` semantics and Kotlin's value-returning methods.

## Design

### Part 1 — proc-macro2-kotlin: make wrapper `inner` mutable

Six wrapper types currently have `internal val inner: WrapperXxx`. Change all
to `var` so the wrapper object can be updated in place:

| Type          | Field                      |
|---------------|----------------------------|
| `TokenStream` | `inner: WrapperTokenStream`|
| `Span`        | `inner: WrapperSpan`       |
| `Ident`       | `inner: WrapperIdent`      |
| `Literal`     | `inner: WrapperLiteral`    |
| `Group`       | `inner: WrapperGroup`      |
| `Punct`       | `inner: WrapperPunct`      |

Add a `replaceFrom(other: T)` convenience method to each that copies `other.inner`
into `this.inner`:

```kotlin
fun replaceFrom(other: TokenStream) { inner = other.inner }
```

This lets a `*Mut` method build new content and copy it into the existing
wrapper without changing object identity.

**Published as proc-macro2-kotlin 0.1.6.**

### Part 2 — syn-kotlin VisitMut: change `*Mut` methods to return `Unit`

Match Rust's signature exactly. Rust's
`fn visit_token_stream_mut(&mut self, i: &mut TokenStream)` takes a mutable
reference and returns nothing. Kotlin should do the same:

```kotlin
// Before:
public open fun visitTokenStreamMut(tokens: TokenStream): TokenStream = tokens

// After:
public open fun visitTokenStreamMut(tokens: TokenStream) { }
```

**Every `*Mut` method changes from `(input: T): T` to `(input: T): Unit`.**

Call sites become simple — no assignment, no discarded return values:

```kotlin
// Before (bug-prone):
visitTokenStreamMut(e.tokens)              // silent bug if result discarded
e.tokens = visitTokenStreamMut(e.tokens)   // correct but easy to forget

// After (can't get wrong):
visitTokenStreamMut(e.tokens)              // mutates in place, nothing to forget
```

### Part 3 — Overrides use `replaceFrom` for "build new" pattern

The `FlattenParens.visitTokenStreamMut` override currently creates a new
`TokenStream` and returns it:

```kotlin
// Before:
override fun visitTokenStreamMut(tokens: TokenStream): TokenStream =
    TokenStream.fromTokenTrees(tokens.flatMap(::flattenTokenTree))

// After:
override fun visitTokenStreamMut(tokens: TokenStream) {
    val flattened = TokenStream.fromTokenTrees(tokens.flatMap(::flattenTokenTree))
    tokens.replaceFrom(flattened)
}
```

### Part 4 — Remove assignment-back fixes from VisitMut.kt

All 14 lines added in the audit (`e.tokens = visitTokenStreamMut(e.tokens)`,
`l.setSpan(visitSpanMut(l.span()))`, etc.) become plain calls
(`visitTokenStreamMut(e.tokens)`, `visitSpanMut(l.span())`) — no return value
to assign.

### Part 5 — Span/Ident in-place mutation

`visitSpanMut` and `visitIdentMut` now mutate in place via `replaceFrom`:

```kotlin
// Before:
public open fun visitIdent(id: Ident): Ident {
    id.setSpan(visitSpanMut(id.span()))
    return id
}

// After:
public open fun visitIdentMut(id: Ident) {
    visitSpanMut(id.span())
}
```

`Span` is now mutable (Part 1), so `visitSpanMut` can replace the span content
in place. `Ident` is now mutable, so `visitIdentMut` can replace the ident
content in place. No `setSpan` needed — the wrapper's `inner` is updated
directly.

### Part 6 — Update test files

Update all `*Mut` overrides in test files to return `Unit` and use
`replaceFrom`:

- `ExprTest.kt` — `FlattenParens.visitTokenStreamMut`
- `UnparenthesizeTest.kt` — `FlattenParens.visitTokenStreamMut`
- `VisitMutTest.kt` — any `*Mut` override signatures

### Part 7 — Update libs.versions.toml

Bump `procMacro2 = "0.1.6"`.

## What this eliminates

- The entire class of "forgot to assign back" bugs — methods return `Unit`,
  compiler enforces it
- The mismatch between Rust's `&mut` semantics and Kotlin's value-returning
  methods
- The need to audit every call site for correct assignment

## What stays the same

- `syn` AST types (`Expr`, `Pat`, `Type`, etc.) — already `data class` with
  `var` fields, already mutable
- `Punctuated.mapValuesInPlace` — already in-place, no change
- `Fold.kt` — still uses `.copy()`, still returns new objects (that's the
  point of Fold)
- `Clone.kt` — still creates new objects (that's the point of Clone)

## Aliasing safety

Same convention as Rust: if you hold a reference to a `TokenStream` and pass
it to a `*Mut` method, expect mutation. Rust enforces this via borrow rules;
we enforce it via convention (already the case for all `var` fields in syn
types). The `replaceFrom` pattern keeps object identity stable — all
references to the same `TokenStream` see the updated content, which is the
desired behavior.

## Scope of changes

| File                                      | Change                                      |
|-------------------------------------------|---------------------------------------------|
| `proc-macro2-kotlin/Lib.kt`               | `val inner` -> `var inner` on 6 types + `replaceFrom` |
| `proc-macro2-kotlin` publish              | Bump to 0.1.6, publish to Maven Central     |
| `syn-kotlin/libs.versions.toml`           | `procMacro2 = "0.1.6"`                      |
| `syn-kotlin/VisitMut.kt`                  | All `*Mut` methods: `): T` -> `): Unit`; remove return statements; all call sites drop assignment-back; overrides use `replaceFrom` |
| `syn-kotlin/ExprTest.kt`                  | `FlattenParens.visitTokenStreamMut` -> `Unit` + `replaceFrom` |
| `syn-kotlin/UnparenthesizeTest.kt`        | Same `FlattenParens` change                 |
| `syn-kotlin/VisitMutTest.kt`             | Any `*Mut` override signatures              |

## Implementation order

1. proc-macro2-kotlin: `val inner` -> `var inner` + `replaceFrom` methods
2. proc-macro2-kotlin: compile + test
3. proc-macro2-kotlin: bump version to 0.1.6, publish
4. syn-kotlin: bump `procMacro2 = "0.1.6"` in `libs.versions.toml`
5. syn-kotlin: rewrite VisitMut.kt `*Mut` methods to return `Unit`
6. syn-kotlin: update all call sites to drop assignment-back
7. syn-kotlin: update test file overrides
8. syn-kotlin: compile + test (JVM, macosArm64, wasmJs)
