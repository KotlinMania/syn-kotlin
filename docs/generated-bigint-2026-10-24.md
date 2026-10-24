# Generated bigint adoption

The complete 68-line upstream `tmp/syn/src/bigint.rs` is now machine-translated
by rust-kotlin's existing proc-macro2/Syn frontend. All seven top-level items and
three original comment regions are retained; no source is configured out.

Raw output and Rust behavior evidence are in the sibling rust-kotlin checkout:
`tmp/bigint-5pwdf42e/` and `docs/syn-emission-13.md`. Raw checked output SHA-256:
`7fe4d80ebf98e5d69d98e493cd4e694561f6201ab6a59af1f2ac144c8bf4fb2c`.
Both checked and wrapping variants compile unchanged and match 51 Rust output
rows each, including state after errors. Reusable vector fixtures add 26 rows per
mode. This port selects pointer-width 64, overflow checks on and panic unwind.

Integration changes are package/provenance, synthetic prefix `__rtkBigInt`,
internal helper visibility and ktfmt formatting. Generated semantic bodies are
not hand-edited. StringBuilder and MutableList<UByte> are existing Kotlin backing
facilities; the source decimal algorithm, byte width and iteration order remain.
Rust assumption comments are preserved as comments, not invented input guards.

Three production Lit.kt calls now invoke generated methods. Its base selection
and digit check prove the Int-to-UByte conversions are in range. The owned string
result is appended as a CharSequence; there is no rewritten decimal formatter.
BigInt becomes internal, matching Rust pub(crate); no production signature in
this project exposes it. Old public hand-port helper methods are removed.

The previous port agrees with Rust for all 42 comparison rows inside documented
input assumptions. Its extra require guards differ on nine out-of-assumption
rows. Production parser inputs obey those bounds, so this is not a demonstrated
caller-visible defect. Plain raw/ktfmt and behavioral diffs, original source/port
snapshots and exact integration adjustments remain in rust-kotlin's
`tmp/syn-emission-13-1lpa0q30/`.

411 JVM and 409 macOS tests pass, including three new generated-BigInt tests for
decimal carry, independent returned text, large mixed-radix literals and checked
byte failure/state preservation. Other targets are unverified. Allocator behavior,
storage beyond Kotlin Int limits and Rust panic ABI/hooks are not equivalence
claims; oversized target-only capacities reject with a distinct unsupported-domain
error instead of being passed off as Rust panics.
