# Source contents and publication boundary

Publication, adoption and external upstream submission are unapproved. Local manually run reference tooling is permitted. Vendored frozen Rust source must not be included in published code; preserve the shared reference trees rather than deleting them.

Direct inspection of component57182 lists6263 file entries:6228 compiler-tooling classes,22 Kotlin module metadata files,6 XML files,1 manifest and6 runtime source resources. The source resources are2 Kotlin files,2 Swift files and2 C headers. There are no Rust source or Cargo manifest/lock entries. This describes the host exporter tooling package; the product's Kotlin/Native outputs remain native KLIBs/archives/executables.

The seven ordered source patches plus the enum/Job follow-ups modify eight distinct upstream files, all Kotlin, Swift or C headers. The ordinary-runtime directory is an explanatory subset. The recursive-bound experiment is rejected and is not part of the accepted delivery order. Syn currently tracks no Rust files, Cargo manifests/locks or tmp reference tree.

This inventory does not certify the implementation origin of every transitive binary or replace the required bundled-license review. No final release staging directory exists. No absence claim for an unbuilt future source archive is implied.

A no-vendored-frozen-Rust build can keep only hand-authored patches, manifests and fetch/build recipes in the tooling project. Fetch official Kotlin source inputs by immutable revision/Git blob and official tooling by checked artifact hashes during an isolated build; rebuild the modified producer sources. Keep local Rust oracle/reference tools outside source archives, POM product dependencies and binary publication staging. This is a declared source-patch build over an official binary base, not a full compiler rebuild.

Before any separately approved publication, inspect the actual final binary and source archives against an explicit release-content allowlist and reject Rust reference/toolchain trees. Preserve Apache-2.0 headers, license and applicable bundled notices, identify modifications, inventory third-party licenses/SBOM, resolve the artifact-derived header provenance qualification, and reproduce the release build twice. There is no reason to delete the local Rust reference material to satisfy this release boundary.

Exact read-only inventory: workspace automation-artifacts/2026-10-20-syn-isolated-library-validation/production-delivery/release-source-scope-inspection.json, SHA256 cc83fccc7222458518d9151b7cc87e39a64b1a96edc25f2827b877f53a0f44fe. Nothing has been published, adopted, externally submitted or created under the proposed repository/coordinate.
