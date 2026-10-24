# Macro token ownership and delimiter spans

Direct Windows string-macro emission exposed two source-fidelity defects in the
existing Syn transliteration. Rust `tmp/syn/src/mac.rs` is the reference:

- `parse_body_with` (line 146) clones `self.tokens` before passing the owned stream
  into parsing. `ToTokens for Macro` (line 218) likewise surrounds a cloned stream.
  The Kotlin methods passed the original mutable stream, and `deepCopy` also shared
  it. They now use the existing `TokenStream.clone()` API. Generated `clone` already
  used that API and remains unchanged.
- `parse_delimiter` (line 153) retains the complete `g.delim_span()` when building
  Paren/Bracket/Brace tokens. Kotlin passed only `span.open()`, losing the closing
  delimiter position. It now calls each token's existing `from(DelimSpan)` overload.

The initial string-macro compile check reproduced invalid output with the original
literal left after the translated initializer. That failure is retained in Windows
`direct-translation/string-validation-01/`. The original expression's span stopped
at the opening delimiter; the generator did not have a reliable replacement range.
The repair is in Syn's representation of original tokens, not a custom translator
scanner or delimiter workaround.

Three focused regressions cover repeated parsing/printing with unchanged tokens and
positions, consuming independent deep/generated copies, and original opening/closing
positions for all three delimiter forms. Exact local test results and saved KLIBs
are retained under `../rust-kotlin/tmp/windows-string-macros-01/`. Older frozen
dependencies and candidate runs remain unchanged; normal pins are not changed and
nothing is published. Final validation passes **419 JVM and 417 macOS ARM64 tests**,
with zero failures/errors. `syn-final-tests.json` records the saved consumer KLIB
SHA-256 `d6feaa5a5104bc8e1b7eb1593d51ae5e84385d786b028213b4c807d624036cdd`.
This final result includes the delimiter regression; the earlier copy-only result
does not establish correct macro ranges.
