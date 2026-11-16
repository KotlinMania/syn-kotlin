# Generated sealed.rs adoption

The complete parsing-enabled `tmp/syn/src/sealed.rs` is adopted in
`src/common/commonMain/kotlin/io/github/kotlinmania/syn/Sealed.kt`. Its generated body is
unchanged except for the existing ktfmt formatting step and package/provenance
header. No items are excluded. This is the ninth of 55 adopted Syn source files.

Generator `a9d3bc5` adds an explicit, source-checked marker binding. Regenerated
Ext.kt uses `SynSealedModule_lookahead_Sealed` for its existing private witness,
instead of declaring another marker. All 19 Ext source items still emit. Ext is
an existing adoption and is not counted again. Its algorithms and integration
adapter are unchanged.

Public Peek remains a Kotlin sealed interface. Its former public
`Lookahead.Sealed` supertype and facade are removed: the upstream marker is
crate-visible, and exposing the generated internal marker as a public supertype
is rejected by Kotlin. The generated Ext witness consumes the internal marker;
Kotlin sealed inheritance preserves the external implementation restriction on
Peek. The removed facade is a source/API change; ordinary Peek use is preserved.
Copy and required marker implementations are checked against Rust declarations
by the generator, not inferred from Kotlin interface inheritance.

Verification: 280 Native generator tests, 37 unchanged raw Ext observations
matching Rust, and 436 Syn macOS ARM64 Native tests pass. Nine access checks cover
a valid public consumer, rejected external Peek/IdentExt implementations,
same-package external Peek inheritance, internal-marker visibility, and Rust
external/non-Copy rejection. Raw output compiles with `-Werror`; both installed
generated files are reproduced byte-for-byte using the recorded adoption recipe.

Receipts and original failure evidence are retained in
`../rust-kotlin/verification/sealed-adoption-01/`. Targets, FFI, dependency pins,
build configuration and workflows are unchanged. No local or public publication
was needed. Other platforms and Swift export were not validated for this change.
