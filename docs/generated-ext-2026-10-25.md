# Generated ext.rs adoption

Commit `ebaf85a` adopts the complete generated ext.rs translation in Ext.kt and
regenerates Verbatim.kt through rust-kotlin `e90cbaa`. All 19 source items emit;
no configured-out nodes or partial sections are counted as translated. Source
comments and documentation examples are retained.

Package/provenance, ktfmt and a distinct generated JVM file-class name are adoption
metadata. ExtBindings.kt and the final Peek adapter forward existing Kotlin call
syntax to generated witnesses; they contain no replacement parsing algorithm.
The Peek adapter shares the file to access the source-private token witness. Four
ItemParsing calls now select the generated identifier implementation. The old
IdentExt object API is replaced by the source-derived trait, not guaranteed binary
compatible. Existing top-level/companion extension syntax is retained.

Rust's ordinary identifier unraw returns a clone. The hand port returned its
mutable original; a saved reproduction shows span mutation affecting the original.
Generated unraw corrects that aliasing. ParseBuffer.step remains the existing
implementation, preserving callback-once, original typed errors and cursor commit
only on success. Verbatim's generated change is only an aliased appendTokenTree
import and call into the generated implementation through existing integration.

Verification: 37 raw-generated Ext Rust/Native observations, 28 regenerated
Verbatim observations, 247 generator tests, 436 macOS Native and 438 JVM Syn tests.
The raw Ext file also compiles independently with warnings treated as errors.
Private-witness observations append test code to the exact raw output without
patching its declarations. The ten-file generator regression batch is retained;
it is not ten new adoptions. Other platform execution and compiler-host hygiene
are not established. No targets or dependency pins were changed.

Raw output, source maps, adoption recipe, ordinary diff and publication/bootstrap
receipts live in ../rust-kotlin/verification/syn-emission-23 and
../rust-kotlin/docs/syn-emission-23.md. Syn's generated-adoption count is now 8/55.
