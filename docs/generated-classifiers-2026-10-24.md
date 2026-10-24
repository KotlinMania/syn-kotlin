# Generated classifier adoption

`Classify.kt` now uses machine-generated `requires_semi_to_be_stmt` and
`requires_comma_to_be_match_arm`. Five production call sites in `Expr.kt`,
`Fixup.kt`, and `Stmt.kt` call them directly; the superseded implementations
are removed. The third function's subsequent adoption is recorded below; two
top-level functions still remain hand-translated.
This is partial adoption, not another complete file: Syn stays at 1/55 complete
generated replacements (whitespace.rs).

Source: unchanged classify.rs line 2 and lines 16–68, one import and two complete
functions. Complete source SHA-256:
`200e81f2245dc23621026849542d95d1e7c320c775362e080857673b51bc82b7`.
Raw generated Kotlin SHA-256:
`c26f15c1154aaa99d9858f762d27bbd4a9487dfc5a2cd71b525446a8937402f8`.

Evidence: `../rust-kotlin/tmp/syn-emission-06-txkhdd7f/`.

- `final-first-two-output/` retains exact input/output, name/comment maps,
  consumed source declarations and explicit imported representation bindings.
- `behavior-repaired/` compiles raw Kotlin unchanged with warnings as errors.
  All 38 observations across 19 real parsed expressions match Rust Syn.
  `oracle-adaptations.json` records test-only adaptations for Rust crate privacy
  and the external non-exhaustive enum; generated Kotlin is never patched.
- `formatted.port.diff` is ordinary diff on ktfmt-normalized copies. Differences
  in these two functions are source spelling/order, restored comments, explicit
  borrowed payload locals and direct source predicate lowering. No behavioral
  defect was found in the superseded two classifiers.
- `adoption/` contains before/after snapshots, integration/caller diffs and tests.
  Integration adds package/provenance context and uses ktfmt output; it changes
  no generated algorithm. Untranslated portions remain in the canonical file.

The real-node test also exposed a parser omission: Rust Syn accepted `try {}`;
Kotlin Syn rejected it. `ScanExpr.kt` now dispatches TryBlock in both ordinary
and earlier-boundary expression parsing, following Rust expr.rs:1267,1807,2510.
It reuses TryParse, the existing Block parser and Expr.TryBlock. The Block parser
becomes internal for reuse; no public signature changes. Seven focused Rust
acceptance/rejection cases agree. Tests cover attributes, token structure,
match-arm boundaries and malformed contents. An initial new assertion compared
printer whitespace (`?;` versus `? ;`); it now compares token-tree structure,
with the node/statement assertions retained. Both test attempts are saved.

After adoption, **400 JVM and 398 macOS tests pass**, including the Rust-backed
classifier corpus and three parser regressions. Other platforms are unverified.
That increment's whole-file generator stopped at `core::ops::ControlFlow`;
subsequent translator increments now implement that value type and loops.
Recognizing TryBlock does not implement its execution semantics in the emitter.

All work is local. No custom lexer/parser, dependency pin change, publication,
push or CI run occurred.

## Third classifier: borrowed type traversal

`trailing_unparameterized_path` and its two nested helpers are now generated from
the unchanged complete Rust file under explicit printing:on/full:off configuration.
`Fixup.kt` calls the generated function directly; the old hand implementation was
removed. The existing other two classifiers and their five callers are preserved.
The whole-file count remains 1/55; this is three of five top-level functions, not
complete classify.rs translation.

Evidence: `../rust-kotlin/tmp/syn-emission-08-u6iwcz6i/`.
Raw Kotlin SHA-256:
`db949e2e15d2c6987f72bd97a7be65ce72b8a0c1be977fb6331e2b731b5ac3a7`.

- `type-classifier-behavior-02/validation.json`: 31 observations match original
  Rust, with raw generated output compiled unchanged and warnings treated as
  errors. Includes parsed nested types, explicit Group/Verbatim nodes, trailing
  punctuation and three empty-collection panic cases.
- `comparison/behavior-validation.json`: the old helper agrees on 28 cases but
  returns false for empty path/impl bounds/trait-object bounds. Rust and generated
  Kotlin panic. Those constructed AST cases are separate from valid parsed input;
  no ordinary parser reachability claim is made.
- `comparison/` retains plain and ktfmt-normalized diffs against the old port.
- `adoption/` records exact before/after snapshots and the integration patch:
  synthetic names gain a Classify prefix, three support classes become file-private,
  and ktfmt formats only the inserted region. Source algorithm/order is unchanged.
  Canonical Classify.kt and its remaining hand-written functions are retained.

Three new common tests cover the Rust-backed results and panic boundary.
**403 JVM and 401 macOS tests pass**, with no failures/errors/skips; exact counts
and logs are in `adoption/test-results.json` and `adoption/syn-tests.log`.
Other targets are not claimed. No dependency pins, build settings or workflows
were changed. Remaining full-profile emission starts at proc_macro2 Delimiter
binding and needs further Expr-field and token operations.
