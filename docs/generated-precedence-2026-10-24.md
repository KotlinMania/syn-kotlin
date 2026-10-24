# Generated precedence adoption

`Precedence.kt` now comes from the complete 210-line Rust `src/precedence.rs`,
processed by rust-kotlin's existing proc-macro2/Syn frontend with full and printing
enabled. Two inactive not(full) match arms are retained in the configuration
receipt. This does not claim the other feature profiles were translated here.

Raw output and source evidence remain separate, under
`../rust-kotlin/tmp/syn-emission-12-7hgjfg6j/complete/`. The raw output SHA-256 is
`56421292ddf2a3dfab2691e140f92fa10cc554fcb58a47b721ca49a42acdce36`.
The generator implementation is protected by local checkpoint `5185921`.
All 122 translator tests pass. Raw generated Kotlin compiles unchanged and matches
88 Rust observations, including all binary operators, representative expression
variants, attributes, clone/equality/partial ordering and MIN.

Ordinary raw and ktfmt-formatted diffs exposed one hand-translation error:
`#[outer] continue` previously received Unambiguous precedence, while Rust and the
generated file produce Prefix. The complete port comparison agrees on all other
rows, with the hand port's partialCmp Int normalized by sign to Rust Ordering for
comparison. Regression tests cover outer, inner, mixed attributes and explicit
trait bodies. Existing prefix-helper tests now exercise the generated public
behavior through `of`; the Rust helper itself is private and nested.

Six production calls in Expr, Fixup and ScanExpr now call `of_binop` directly.
No hand-written classifier body or compatibility wrapper remains. Existing
precedence comparisons by ordinal retain their proven equivalence for this exact
implicit-discriminant enum. Arbitrary Rust trait behavior is not inferred from that
fact; the generator emits the actual explicit trait bodies.

`adoption/raw-to-integration.diff` records the integration-only changes: package
and provenance header, synthetic helper names prefixed `__rtkPrecedence`, internal
runtime tags, and ktfmt. The enum and its companion remain public because the
existing public FixupContext API exposes the type. This is an explicit Kotlin API
visibility accommodation, not a change to Rust semantic bodies. Helpers and
translated source methods remain internal/private as generated. The old public
camel-case helper names are removed; all in-repository callers are migrated.
RUST_CALLERS lists no Rust consumer of the crate-private Precedence type; this is
not a claim to have verified every possible external Kotlin consumer.

Local regressions pass: **408 JVM / 406 macOS tests**, including two new common
tests. Receipt: `adoption/syn-test-validation.json`. Other targets are unverified. No dependency
publication, push, CI, release, workflow or settings change is part of this work.
