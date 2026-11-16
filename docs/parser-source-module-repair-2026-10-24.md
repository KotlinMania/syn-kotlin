# Source-module parser parity repair

While translating classify.rs, the existing parser rejected upstream expr.rs and
ty.rs even though original Rust Syn accepted both. The targeted source corrections
are in ItemParsing.kt, Item.kt, Expr.kt and ScanExpr.kt:

- Propagate module child errors and retain module inner attributes in their proper
  source/serialization position.
- Restore for-loop dispatch, labels and inner attributes without misclassifying
  higher-ranked closures.
- Use the existing complete Pat parser for patterns, including `name @ pattern`,
  and remove the obsolete private duplicate helpers.
- Recognize underscore Expr.Infer nodes, including discard assignments.

Eight common regression tests were added. All 396 repository JVM tests plus one
artifact-local diagnostic probe, 394 macOS Native tests and 67 translator tests
pass. All 22 focused parser cases agree with original Rust Syn. Complete expr.rs
and ty.rs now pass parsing and token correspondence with 87/34 items respectively.
Earlier generated translations remain byte-identical. Other targets/full lint
are unverified here.

These are source-directed manual engine repairs, not complete generated replacements
of expr.rs, item.rs or ty.rs. Classify.kt is untouched. No public signature, target,
dependency pin, FFI file or build configuration changed.

Detailed evidence and exact stable hashes are in
[rust-kotlin's repair record](../../rust-kotlin/docs/syn-repair-03.md) and
`../rust-kotlin/tmp/syn-emission-04-87g9l3gt/`. ktfmt was used on comparison copies;
ordinary diffs preserve a reviewable before/after record. No CI, push or publication
ran; the existing freeze remains active.
