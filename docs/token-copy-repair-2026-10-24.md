# Restore Rust cursor token-copy semantics

Rust `buffer.rs` clones identifiers, punctuation, literals and groups when returning
them from Cursor. The Kotlin cursor previously returned references to its buffer's
mutable tokens. `Buffer.kt` now uses the lossless `proc-macro2` cloneToken API in
ident, punct, literal, anyGroupToken and tokenTree. tokenStream and lifetime receive
the corrected behavior through those existing operations. Cursor identity and
traversal remain unchanged.

The existing Ident.copy and Ident.clone entry points are retained. copy now delegates
to the library's lossless copy rather than reconstructing an identifier through the
currently selected backend. This retains rawness, spans and backend provenance.

The three tests committed as failing reproductions in cc11d57 now pass. Full local
suites pass: 414 JVM tests and 412 macOS ARM64 tests. The standalone Kotlin copy
oracle matches all 19 Rust output lines, including 12 isolation observations and
token data/span fingerprints. Additional dependency tests exercise compiler-backed
metadata, separate delimiter spans and nested groups; rustc-hosted macros and other
platforms have not been tested.

These tests consume the local dependency repairs through an explicit Gradle
composite; published dependency pins remain unchanged:

```
./gradlew --offline --no-daemon --include-build ../proc-macro-kotlin --include-build ../proc-macro2-kotlin -Pproject.repositories.mavenLocal=true -Dmaven.repo.local=/Users/sydney/.m2/repository jvmTest macosArm64Test
```

Evidence is retained in `rust-kotlin/tmp/token-copy-repair-tkw9djkt/`, including
`copy-validation.json`, original failing results, current XML, ordinary oracle diff,
source/dirty-state hashes and saved local libraries. This is a targeted dependency
repair, not completion or adoption of generated verbatim.rs. No CI or publication.
