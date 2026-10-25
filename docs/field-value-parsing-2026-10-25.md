# Struct field parsing follows Rust Syn

The direct translator exposed a failure at serde_json `src/de.rs:64`: an outer
`#[cfg(...)]` attribute on a struct initializer field. Rust Syn accepts that
source. The Kotlin port's `parseFieldValueImpl` started by parsing an identifier,
so it rejected both attributed fields and numeric members such as `S { 0: x }`.

`ScanExpr.kt` now follows the existing Rust implementation in
`tmp/syn/src/expr.rs`, `impl Parse for FieldValue` (line 2756): parse outer
attributes, parse the existing Member node, require a colon/value for numeric
members, and construct a path with a cloned identifier for named shorthand.
Attributes, colon spans, field order and values are retained. This uses the
existing parser and token-copy APIs; it adds no recognizer or text substitution.

This small engine repair is a manual source-faithful translation of that Rust
function. It is not presented as generated replacement of the complete Syn
expression parser. The generated/adopted Syn file count remains four of 55.

Three common tests cover attributed named/shorthand/numeric fields, token
round trips and AST copies, the actual JSON reader initializer, and invalid
numeric-field/inner-attribute/missing-value syntax. The original Rust Syn 2.0.114
oracle accepts/rejects the same fixtures and parses the complete JSON reader as
58 top-level items. Rust evidence is in
`tmp/field-value-repair-20261025/{oracle,rust.stdout}`.

The initial local build could not resolve `cloneToken`, including calls in the
previously repaired Buffer.kt. The source repair already existed in proc-macro2;
local Maven still contained the old artifact. The existing committed proc-macro
0.1.4 and proc-macro2 0.1.7 repairs were tested and published locally in dependency
order, preserving all unrelated dirty configuration. No pins, targets or source
exclusions were changed. Publication receipts are under
`rust-kotlin/tmp/foundation-publish-20261025/`.

The normal local Maven build passes **422 JVM and 420 macOS ARM64 tests**,
including all three new regressions. No test was excluded or weakened. The log
and source hashes are in `tmp/field-value-repair-20261025/tests-02.log` and
`validation.json`. Normal `publishToMavenLocal` also succeeds for Syn 0.4.0
(168 tasks, 48 seconds), with artifact hashes recorded in the same receipt.
The rebuilt direct translator now accepts unchanged complete serde_json `de.rs`
(58 items) and `value/de.rs` (61 items), including its AST/token correspondence
checks. All 186 translator tests pass using normal local Maven dependencies,
without the historical frozen-library init script. Its receipt is
`rust-kotlin/tmp/foundation-publish-20261025/translator-normal-validation.json`.
Serde derive's previously failing qualified-path tests also pass with the
published parser: 275 JVM and 37 macOS tests, no source edits in Serde.
Other platform execution and full project translation remain separate.
