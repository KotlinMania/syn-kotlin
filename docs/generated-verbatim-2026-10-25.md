# Generated verbatim adoption

The complete unchanged 33-line `tmp/syn/src/verbatim.rs` is translated by
rust-kotlin's existing proc-macro2/Syn frontend. All five top-level items are
processed, zero items are configured out, and all four original comment lines
remain beside the transparent-group handling.

The raw generated Kotlin compiles unchanged with warnings as errors and matches
28 observations from the original Rust: empty and selected ranges, token kinds,
nested groups, spans, parser-state preservation, invalid boundary panic payloads
and returned-token mutation isolation. Evidence lives in the sibling rust-kotlin
checkout: `docs/syn-emission-18.md`, `tmp/syn-emission-18/` and
`tmp/syn-verbatim-5ilizzjv/validation.json`. Generator checkpoint: `16da8fa`.
Raw generated SHA-256:
`0e6d592badd5bfdb36630abf4292f0bc5eeb2f896055b1f98d816d6ba3f710a2`.

Integration changes are package/provenance, the collision-free synthetic prefix
`__rtkVerbatim`, internal helper visibility and ktfmt formatting. The generated
algorithm is not hand-edited. Two Expr.kt calls now use generated `between`;
the other 22 production calls already use that name. The duplicate hand algorithm
and public wrapper are removed. Rust's pub(crate) function becomes internal.
No caller was found in the Kotlin source directories of the downstream projects
listed by RUST_CALLERS.md; arbitrary external binary consumers were not surveyed.

The old port matches the tested valid-range token behavior. Its different-buffer
assertion payload differs from Rust, and it had omitted the original explanation
of transparent groups. The generated implementation restores both. These tests
do not establish a new caller-visible parsing defect in normal valid ranges.

427 JVM and 425 macOS tests pass, including five new GeneratedVerbatimTest cases.
No targets, dependency pins, settings or unrelated files were changed. Other
platform execution, compiler-host token contexts, Rust panic ABI/hooks, allocator
behavior and deterministic Drop timing are not covered by this result. The panic
adapter is a Kotlin RuntimeException with the source payload.
