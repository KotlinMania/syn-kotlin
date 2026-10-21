# Shared generic relationship investigation

This authored Swift source is an expected-negative language representation check. It is not generated product output or a replacement for the Native/Swift consumer tests.

Actual3613 invokes the existing Swift compiler with object compilation, Swift6, warnings-as-errors, explicit MacOSX26.5 SDK, ARM64/macOS15 target and artifact-local output/module cache. The compiler rejects the direct Flags recursive superclass requirement and also reports a failed diagnostic at the factory expression. Preserve both diagnostics. The earlier sandbox startup denial73435 and missing-default-SDK harness error47120 are separate and are not type-system negatives.

Reproduce with the existing Swift compiler and SDK paths supplied by the local host, passing RecursiveConstraint.swift to -c with -swift-version6, -warnings-as-errors, -target arm64-apple-macosx15.0, -sdk and an artifact-local -module-cache-path/-o. Require the located self-referential-generic-requirements error; an unrelated nonzero exit is not this regression.

Inspection of the actual official2.4.20 SirFunction, SirClass and SirCallable member tables finds no declaration generic-parameter list. The original emitted withRemaining signature instead accepts two independent Flags values. The source recursive B equality is consequently absent from that signature. Merely declaring a generic B in a Native C entry would not turn those references into a checked common type witness.

Pinned upstream revision890ac1d94fdb80eb85f0eeb5be5e4352df987b2f StandaloneSirTypeNamer.kt confirms that parameterized naming replaces parameters with names of upper-bound classes. SirRegularInitFromKtSymbol requests that PARAMETRIZED name for allocation/initialization. This produces the actual invalid FlagIterator<Flags<*>> constructor shape recorded in71898. The old local reference tree was not treated as the authoritative version; exact revision sources and actual artifact disassemblies are retained separately.

This is the concrete representation blocker for the current repair: the emitted API does not carry shared generic equality, direct translation of the recursive superclass is rejected by Swift, and the generated Native allocation names violate the Kotlin bound. There is no verified compatible implementation in this experiment. This does not establish that every equivalent protocol/associated-type encoding is impossible. Such an encoding must preserve the original semantics and establish a checked Native witness, including a real mismatched-pair negative; public API erasure, placeholder bounds and unchecked conversion are not substitutes.

The original rejected inference candidate and five Native errors are preserved. No further Native bridge compilation or product Swift behavior test ran in this representation investigation. Publication, adoption, new repositories and external submission remain on hold. All commands are terminal.

Raw evidence is under workspace automation-artifacts/2026-10-20-syn-isolated-library-validation/recursive-bound-repair/relationship-model. results.json pins exact source/disassembly/log hashes. Representation checkpoint SHA256 b2d8933a6848189911b53a0b1f8167e2e61427403ac014ae58d3279a239310d0.
