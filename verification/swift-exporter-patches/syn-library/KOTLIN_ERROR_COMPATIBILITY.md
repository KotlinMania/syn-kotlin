# KotlinError compatibility assessment

The tested String snapshot is a public contract change. Six real Syn tests pass with it, but that does not justify replacing the original object-backed error for every caller. A source-compatible mixed-language repair remains a concrete option: preserve the original error API and repair the generated warnings instead of disabling them. The existing two-project experiment establishes useful distinctions, not a production-ready implementation of that option.

## Exact successful build

The tested Kotlin source revision is `deaafb4edd143e82c13346244b35d1e5b4af0e46`. The durable incremental patches and results are committed at `1c71b20ecc216ea2e78e40629d310f79a042895d`. That later revision changes verification files only; it is not another tested product revision.

The exporter component hash is `11da4d91a1985c1e079e5b363ad33a1404fbd223d702131d19439446c01ba555`. The Native ownership header hash is `99186d1dd029a8ba5b66dae390a3aec0d6d0e5293eff641d85daab5e212723c3`. Native session38329 and Swift session42332 both exited0. All six unchanged named tests passed once with no failures or skips; eighteen product/test frontend commands across nine modules used Swift6, warnings as errors and macOS15. Native bridge compilation and static linking passed. The receipt hash is `f6e72bab92c954380c03f1a491e073f29bcdcc4ff6be9934e8e495a1bece1ca9`.

The final logs are workspace `automation-artifacts/2026-10-21-syn-real-swift-deaafb4/optional-set-corrected/{native.log,swift.log}`. Their hashes and the source, archive and input hashes are in `results.json`. This is not full KMP, AWS, OpenTelemetry, arbitrary reverse-callback or published-tooling acceptance.

## Public contract inventory

| Aspect | Official object-backed error | Tested snapshot |
|---|---|---|
| Type/conformances | Public struct, `Error` and `CustomStringConvertible` | Same names and conformances |
| Construction | `init(wrapped: KotlinBase)` stores the supplied reference | Same signature, but reads its diagnostic immediately; adds `init(message: String)` |
| Public payload | Read/write `var wrapped: KotlinBase` | Removed; read-only `let message: String` added |
| Assignment | A caller can replace `wrapped` | No equivalent setter |
| Identity | A copy of the struct initially retains the same object reference | The original object is not stored |
| Downcasting | A caller can inspect or downcast the original object through `wrapped` | That route is absent; a String does not retain subtype, cause, source or custom fields |
| Description | Reads the current original message each time, with a fallback | Returns the captured String, even if the original message later changes |
| Evaluation timing | Constructor does not evaluate the diagnostic getter | Constructor evaluates it; side effects or failures can occur earlier |
| Lifetime | Each error value retains its current wrapped reference | Error lifetime no longer keeps that wrapper alive |
| Copy semantics | Struct copies share the original referent until one copy replaces its field | Struct copies carry the String value |
| Error channel | The thrown Swift type can be caught as `KotlinError` | That type remains, but recovering the original Kotlin error through it no longer works |

The checked Native message helper is another, separate change: a plain KotlinBase now returns no Throwable message and reaches the fallback. The official helper force-casts to Throwable and can abort. The earlier fixture actually reproduced that abort. This is a deliberate bug fix, not evidence that every old error behavior is identical.

The five authored diagnostic tests actually verify message capture, post-construction mutation producing different snapshots, missing-message/plain-object fallback, release after actual Native GC, and transferring the String snapshot to a detached task. They intentionally test snapshot semantics. They do not prove preservation of the old object's identity, mutable property or subtype through Swift catch.

The six real Syn tests inspect concrete `SynError` values returned through concrete parser results. They do not exercise a thrown `KotlinError`. Keeping those diagnostics identical is distinct from preserving Throwable identity through Swift's error channel.

## Source and binary compatibility evidence

One small real client was compiled against the already-built strict Syn modules, without regenerating a library or comparing language modes. Session69114 returned the expected compiler failure: its old getter and setter uses each report `KotlinError has no member wrapped`. `init(wrapped:)` itself has no diagnostic. The client result hash is `1674b235aef595556643757a4e7fa0a65e38c99ec9a5c62b81cf9e2a963a4d74`; all pinned generated inputs remained unchanged. The source and command are under workspace `automation-artifacts/2026-10-21-syn-runtime-compatibility/`.

The old API records include the wrapped getter, setter and modify accessor. Removing them is an ABI surface change. Retaining the constructor's spelling does not prove binary compatibility: the struct's stored representation also changes. No old precompiled client was linked against the new runtime, and no stable-layout or binary-compatibility guarantee is asserted. The earlier baseline/patched API digests both describe an older object-backed stage; they must not be presented as ABI acceptance for the snapshot.

The retained-return header annotations preserve the C function and imported Swift method signatures while correcting actual factory ownership. The isolated SDK audit changes one header and no Native binary. Five diagnostic tests pass with the corrected header, including release of an existential wrapper. This does not establish every class-factory or GC/ARC lifetime case, and it does not authorize installing the SDK.

## Actual caller inventory

The pinned scan covers only Kotlin/Swift source and the existing Swift harnesses of five checked-out repositories: Syn112 files, AWS20, Libc9057, Anyhow30 and ProcMacro2 33. It found no handwritten `KotlinError` or `.wrapped` references in those ranges. `repository-scan.json` under the compatibility artifact directory records each repository head and all9252 file hashes. It excludes generated sources, other repositories, external applications and future releases. Zero matches are not an all-public-caller compatibility guarantee.

The inspected producer and generated callers have distinct roles:

* The synchronous generated throwing guard constructs `KotlinError(wrapped:)` when its error pointer is non-null. This constructor call remains source-compatible, while a user's subsequent `catch` and payload inspection can break. Ordinary unannotated Kotlin throwing methods do not automatically acquire that recoverable Swift channel.
* Both generated forward continuation helpers map a non-null Kotlin exception wrapper through `KotlinError(wrapped:)`. A null callback exception still means `CancellationError`. The construction signature survives; the original error inspection and lifetime semantics change.
* The inspected reverse path catches Swift errors, passes `Error?` through an NSError bridge, and resumes Kotlin with `SwiftException` for non-null errors. Neither that path nor the inspected type conversion reads `KotlinError.wrapped`. The existing `.wrapped.__externalRCRef()` expression belongs to typed Flow conversion, not KotlinError. An automatic original-Throwable round trip is not demonstrated; NSError's embedded Swift-error behavior also needs a real round-trip test before any identity claim.
* The real generated reverse witness remains blocked by separate receiver/callback isolation diagnostics. Its unexecuted cases cannot establish either error design's compatibility.
* AWS's newly annotated concrete `getOrThrow` methods should enter the generated throwing guard on fresh emission. The JVM metadata and earlier emitted nonthrowing declarations establish why the annotations matter; they do not prove fresh Swift throws behavior. Its six current Swift behaviors use result access rather than a Throwable downcast through Swift catch.

## Compatible options and rejected shortcuts

| Option | Evidence and status | Required next proof |
|---|---|---|
| Preserve object-backed error in Swift5 dependency modules, with Swift6 consumers and Werror everywhere | Plausible source-compatible route. Existing two-project comparisons demonstrate the language-mode distinction, but their positive controls disable dependency Werror and use suppressed Native bridge warnings. No accepted strict result exists. | Genuine producer fixes for all emitted warnings; unchanged real consumer tests; mutable wrapped identity/description/lifetime tests; consumer test and race repairs by their owners |
| Proper internal imports, redundant-conversion fixes and accurate Native ownership declarations | Source-compatible building blocks already exercised within coherent isolated builds. They do not themselves make the original stored KotlinBase pass strict Swift6 sendability checks. | A combined legacy-error build, without the snapshot and without warning suppressions |
| Retain the old error unchanged in all Swift6 modules | Actual strict diagnostics reject its non-Sendable stored KotlinBase. The SDK declares `Error: Sendable`; the observed imported-object diagnostic can be a warning without Werror, which does not satisfy this gate. | A sound checked object/importer ownership model, not an assertion that Native references are inherently Sendable |
| Put the original reference in a Mutex | Source/SDK review only; no accepted candidate. Mutex initialization and returning a value use `sending`. A lock cannot protect an alias after it escapes, nor establish that the caller surrendered its original alias. | End-to-end ownership and alias proof while preserving old unrestricted read/write semantics |
| Actor-owned storage or an actor-isolated wrapped property | A possible new API, not a drop-in compatible one: existing synchronous callers gain isolation/await restrictions. The separately tested explicit MainActor frame has a different registration contract. | Explicit caller migration, new API acceptance and real cross-language lifecycle tests |
| Reconstruct an error object from its String, weak storage, or retain an opaque registry token | Not equivalent to the old contract: identity, subtype/cause, lifetime or alias safety changes. No accepted implementation preserves all of them. | A separately specified contract; no placeholder getter returning a replacement object |
| Blanket Sendable, unchecked/unsafe annotations, preconcurrency, hiding the API, or disabling warnings | Not an accepted repair. No evidence makes arbitrary KotlinBase instances and Swift subclasses safe for unrestricted cross-actor use. | These shortcuts are excluded; no suppressed green run is counted |

A comprehensive checked Native/Swift object-ownership correction remains unexplored beyond the narrower verified header fix. It must account for cached wrappers, arbitrary Swift subclasses and aliases; the current evidence neither proves such a compatible correction nor establishes a universal language impossibility.

## Two-project comparison qualification

The OpenSSL report hash is `2ff2e0380ba11b70839a31ab086537b0a8797970212ebefaf3e66605db135226`. At23623166, unchanged tests execute0/3 because their constant name is stale. The corrected-name control passes3/3 serially and3/3 in parallel only with dependency mode5/Werror off and consumer mode6/Werror. Dependency mode6 with Werror off still fails two KotlinTask capture errors. Both dependency-Werror arms execute zero cases. Its Native prerequisite suppresses193 generated Kotlin warnings; it is not a strict Native pass.

The Async Trait report hash is `7902e419248e09a0868e0d989863a508c7d28a713e106a20e183d52aaec41ae3`. Dependency mode5 executes the unchanged serial cases with7/8 passing; parallel execution crashes. A fresh-TokenStream supplementary control passes8/8. The reports identify consumed-input reuse and a shared unsynchronized SourceMap as separate issues for the existing consumer owners. The Native suppression provenance is incomplete because post-suppression hashes are missing. These controls do not substitute for genuine implementation/test repairs or a strict accepted build.

No additional mode-comparison build was run by the shared producer owner. The raw JSON/on-disk receipts, rather than inconsistent prose hashes, are the comparison references.

## Concrete compatible preparation

`source-compatible-preparation/KotlinRuntimeSupport.swift` in the compatibility artifact directory restores the exact official KotlinError struct block while retaining the internal import and the two redundant-conversion fixes from the tested component. Its source hash is `dbfc468afd0cbf3325e555ec2ded72f583e1fe4ac3fd690cf831ff8bf3668266`. It has not been compiled, packaged or tested. The proposed closure also retains the checked message helper and corrected Native header; their behavior qualifications above still apply.

This preparation is an implementation option for a strict mixed-language repair, not an all-module Swift6 claim or an adopted architecture. The next meaningful validation is a coherent repaired producer and real consumer with both warning gates retained, plus original wrapped read/write/identity/lazy-message/retention checks. A suitable exported Throwable domain must test actual subtype/cause inspection; the existing private diagnostic fixture does not expose such a downcast. No architecture choice, new tooling repository, publication or production adoption follows from this assessment.
