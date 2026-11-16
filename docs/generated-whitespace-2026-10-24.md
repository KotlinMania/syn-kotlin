# Generated whitespace replacement

`Whitespace.kt` is derived from the complete local upstream `tmp/syn/src/whitespace.rs` by rust-kotlin's existing proc-macro2/Syn pipeline. The Rust source SHA-256 is `9cdcbfe9045b259046329a795bc1105ab5a871471a6d3f7318d275ee53f7a825`. Both functions and all three source comments are preserved; nothing is configured out.

Raw generator output and its exact integration patch remain in `../rust-kotlin/tmp/syn-emission-02-tra_fv7s/`. Integration adds this package/source header, makes generated runtime classes private to the file and applies ktfmt. It does not hand-rewrite the translated algorithm. The selected profile is 64-bit usize, checked overflow, panic unwind; a wrapping candidate is separately validated. See [the generator's evidence record](../../rust-kotlin/docs/syn-emission-02.md) and its final validation receipt for exact results and hashes.

Rust and unchanged generated Kotlin agree on 69 focused inputs in each overflow mode. The previous hand port incorrectly retained U+0085; Rust skips it. The regression corpus records original Rust outputs, not expected values copied from the old Kotlin implementation. A caller test checks that U+0085 after `#!` before an attribute does not turn that attribute into a shebang. `File.kt` uses the unchanged internal `skip(String): String` function positionally.

The helper types are private and add no public or Swift-exported API. Runtime character data comes from the existing Rust core implementation; its attribution is in `third_party/rust-core-whitespace`. Runtime support is currently inline, including Unicode debug data for boundary diagnostics. Source and output are preserved independently so future generator improvements can regenerate this file without losing provenance.

This is one adopted source file in the ongoing Syn-first effort, not a claim that the whole library has been regenerated. The local check results, platforms and commit receipt are recorded with the generator; CI/publication remain frozen.

Local validation: 388 JVM tests and 386 macOS tests passed. The translator was rebuilt against this Syn KLIB: 62 translator tests passed and regenerated output was byte-identical. Other target platforms are unverified; no CI was run.
