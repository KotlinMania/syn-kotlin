# Complete generated classifier adoption — 2026-10-24

All five functions of upstream `tmp/syn/src/classify.rs` now come from the
proc-macro2/Syn-based Rust-to-Kotlin generator. Four remaining production callers
in Expr, Fixup, Stmt and ScanExpr call the generated source names directly; the
previous six migrated calls remain. No Classify wrapper or hand traversal remains.

The unchanged complete Rust file was generated with full:on and printing:on,
64-bit usize, checked overflow and unwind panic transport. All 12 top-level items
and six nested helpers are emitted; zero items are configured out. All three
source comments are retained, including the expression-variant comment in place.

Raw generated Kotlin compiled unchanged with warnings as errors and matched Rust
on 108 result rows (309 scalar observations): 25 parsed types, six constructed type
nodes, 47 parsed expressions and ten token streams (including a delimiterless
group). Three additional constructed empty types inside casts expose a corrected
hand-port mismatch: Rust and generated code panic on unwrap, while the old port
returned false. These malformed ASTs are constructible; this is not a claim of
normal parser-input reachability. Panic checks cover exception/message behavior,
not Rust unwind ABI, abort, hooks or destructors.

Integration only adds the canonical package/provenance header, renames synthetic
helpers to a file-specific prefix, makes three runtime classes file-private and
runs ktfmt 0.65. Algorithms are not hand-rewritten. The exact original file, raw
output, ordinary/normalized diffs, saved previous port, source maps and explicit
raw-to-integration patch remain in:
`../rust-kotlin/tmp/syn-emission-09-y_ypslip/`.

Validation: 98 generator tests before adoption; **406 JVM and 404 macOS Syn tests
pass**, including three new common tests. Other targets and full-crate semantic
equivalence are unverified. No dependency pins, remote workflows or publication
were changed. Local checkpoint only.

Canonical design and progress are maintained in `../rust-kotlin/docs/design.md`
and `../rust-kotlin/docs/syn-coverage.md`; this completes the second of 55 Syn
source files, after whitespace.rs. Parser repairs and selected fixtures are not
counted as whole-file replacements.
