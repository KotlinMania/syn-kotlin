# Parser parity repair — 2026-10-24

Targeted source-backed corrections are documented in [the rust-kotlin repair record](../../rust-kotlin/docs/syn-repair-01.md).

The local Rust Syn 2.0.114 oracle and Kotlin parser agree on all 40 tested inputs, including the full upstream `src/item.rs` (71 top-level items). Local validation passed 382 JVM tests and 379 macOS arm64 tests. Ten new common regressions cover impl prefixes, required initializers, Verbatim preservation, match-arm boundaries and closure AST preservation.

Exact starting files, pre-existing dirty changes, the task-only patch, ktfmt-normalized plain diffs and test receipts are retained in [syn-parser-repair-hame49cm](../../automation-artifacts/syn-parser-repair-hame49cm/). Existing Expr/ScanExpr work and the two original untracked regression files are preserved. No publication or dependency pin change occurred. A saved local KLIB is exercised by rust-kotlin through an artifact-local init script; it is not the installed Maven artifact.

Full Syn self-translation and all-target/Swift acceptance are not claimed. A separate direct ktlint attempt is blocked by incomplete cached runtime dependencies (missing CliktCommand); ktfmt normalization and git diff whitespace checks succeeded.

## Subsequent trait-item correction

After committing the complete starting state as `38a667a`, the next source-backed increment fixes trait macros, associated constants, prefix/Verbatim handling, empty semicolon rejection and malformed top-level visibility. [Repair 02](../../rust-kotlin/docs/syn-repair-02.md) records the exact Rust correspondence and ktfmt diff. All 387 JVM and 384 macOS tests pass, with 61/61 parser observations agreeing with original Rust.
