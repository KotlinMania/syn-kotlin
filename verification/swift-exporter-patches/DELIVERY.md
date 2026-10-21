# Delivering the Native-to-Swift producer repair

The isolated component is validated for the concrete ProcMacro2 consumer described below. It is not installed, published or selected by production builds. No compatible official published repair has been verified. In particular, the examined published 2.4.20 and 2.4.21 RuntimeSupport resources retain the original defects. A new version number alone is not evidence of a fix.

## Verified boundary

Component SHA256 `57182d059e9c7f087206310aeaa6ccd56fa06ea552f31510ce58c8e7e2945e32` derives from the pinned official 2.4.20 exporter and the reviewed source patch series. The exporter is compiler tooling; its distribution format does not make the product's Kotlin/Native output a JVM library.

ProcMacro2 source `0e48b336c21fc4e1d7ed94d048d49d69ef0ef564`, actual execution 91639, passed Native bridge compilation, native static linking and all three existing Swift cases. Sixteen product/test frontend commands across eight modules used Swift 6, warnings-as-errors and macOS 15. The 282 public declaration records and C header match the fresh stock 2.4.20 generation. All 42 Native KLIB files match that baseline. The actual native archive hash is `500b261a2824efd916b586dbb86c8d738c03acc8f9246c1f784e70275535006d`.

The consumer's retained classification receipt is `automation-artifacts/2026-10-20-proc-macro2-publication-repair/strict-swift-runtime-0e48b33/actual-runtime-classification.json`, SHA256 `0476374eeaa8b236521eaf66372d131fe485855b6f828323066707c1ddfbc673`. Its raw wrapper exit 1 is preserved: the summary parser missed Swift Testing's optional display-name alias. Actual SwiftPM exit was 0. Seven offline parser checks established the correction without rerunning runtime tests.

Six separate enum fixture cases also pass against a freshly compiled and linked Native archive. Those cases cover empty, single and multiple enums, descriptions, integer/string rejection and native reference round trips. They do not replace the consumer's three cases or establish the original 22 asynchronous cases.

## Compatibility changes that must travel with delivery

- `KotlinError.wrapped` is removed. Its replacement stores an immutable String captured at the boundary. The `init(wrapped:)` label remains, but code reading or writing the property breaks; original Kotlin object identity and later message mutation are not preserved. This is a source and binary compatibility change.
- Swift resources, generated Native Kotlin runtime, C headers and exporter classes must be delivered coherently. Earlier experimental Flow changes cannot be mixed with independently selected runtime files. The retained legacy entry and new ready/take entry have distinct contracts.
- Generic reverse callbacks involving arbitrary cached non-Sendable Kotlin objects still lack a proven automatic registration/ownership contract. The original 22 cases have not passed. A separate six-case MainActor lexical-frame result requires explicit caller registration and does not prove that old API compatible.
- The tested scope is KGP/Native 2.4.20 and the recorded Swift toolchain. Neither 2.4.21 compatibility nor fleet-wide compatibility follows from it.

## Official upstream route

Submit the source patches against the exact upstream revision recorded in provenance. Include the strict compiler negatives, enum cases, runtime cancellation/lifetime cases, API impacts and real-consumer receipts. Incorporate the fixes into an official immutable release, then verify its actual contents rather than assuming an upstream tag contains them. Re-run the real Native/Swift gates with that published exporter and the matching supported KGP/Native version. Ordinary KotlinMania library publication can then use official tooling without a private override.

There is no full Kotlin Git checkout or upstream PR in this workspace. The durable patch series is committed in this existing Syn checkout. Creating a new source checkout is not part of this route as currently executed.

## KotlinMania-owned component route

If delivery is required before an official release, it needs an explicitly selected tooling artifact owned by KotlinMania. A proposed coordinate is `io.github.kotlinmania.tooling:swift-export-embeddable:2.4.20-km.1`; it is a proposal, not an existing publication. Never publish modified tooling under `org.jetbrains.kotlin`, replace an official version in a cache, or overwrite installed compiler files. Preserve upstream internal package names required for linkage; Maven ownership and implementation namespaces are different concerns.

Before promotion, preserve Apache-2.0 licensing, bundled notices and source provenance; review redistribution obligations; provide the complete patch set and checksum manifest; reproduce the build twice with pinned bootstrap/compiler inputs and deterministic archive timestamps/order; audit unchanged classes/resources and all intended deltas; and run actual Native/Swift consumer gates in CI. Publish an immutable new version through the existing Central Portal route only after that promotion is authorized. Dependency selection must be explicit, limited to validated consumers, and independently verified from the published artifact. A broad compatibility claim requires the outstanding asynchronous gates too.

Rollback changes only the explicit tooling selection to its previously recorded immutable version and regenerates coherent outputs. It does not restore known-broken Swift behavior or justify relaxing warnings/tests. Keep all failed and successful receipts; remove only owned artifact outputs after their evidence is preserved. Installed tools and shared caches require no rollback because they were not changed.

The actual remaining decision is whether to wait for official upstream delivery or authorize a KotlinMania-owned tooling release with the stated compatibility break and validated consumer scope. This is a production architecture/promotion decision, not permission to run another diagnostic. No tool denied that promotion; it has not been attempted.
