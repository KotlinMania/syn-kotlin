# Preserve fixed-array type tokens

The Windows direct-translation pass exposed a source-fidelity defect in the Kotlin
Syn port. Upstream `tmp/syn/src/ty.rs:97` stores `bracket_token`, `elem`,
`semi_token` and `len` in `TypeArray`. Parsing retains the tokens (around line 588),
and `ToTokens` uses those original tokens (around line 1099).

The Kotlin `SynType.Array` previously stored only `elem` and `len`. Its serializer
created default brackets and a default semicolon, losing their source spans.
Token spelling could still look correct while an array field's computed span
extended outside that field. The translator safely rejected 2,595 record shapes
with an overlapping/out-of-range source-edit diagnostic in Windows pass 03.
That count is an observed symptom, not a claim that all records share one cause.

The repair retains `Bracket` and `Semi` in the existing node and parser, then uses
them in serialization, `deepCopy` and generated `clone`. There is no new scanner,
parser entry point or translator workaround. New constructor parameters default
to synthetic tokens for existing source callers that construct array nodes.
Compiled consumers must rebuild against the changed class; normal Maven pins and
published artifacts were not changed.

Two regression tests cover original delimiter/semicolon positions, nested and
multiline arrays, serialization, deep copy, generated clone and data-class copy.
The local suites passed **416 JVM tests and 414 macOS ARM64 tests**, with zero
failures or errors. Existing APIs outside this node were not changed.

Exact build log, XML results, pre-edit source/HEAD receipt and the separately saved
consumer KLIB are in `../rust-kotlin/tmp/windows-array-spans-01/`.
Its `tests.json` records KLIB SHA-256
`657629a73e0bb0d66cbd5c018826da2044564556af19ddd77b338b86e792af4d`.
Old frozen translator binaries and dependency snapshots remain unchanged.
