# Rust core whitespace classification

`StandardStringRuntime.kt` contains a bounded Kotlin adaptation of Rust 1.99.0 `char::is_whitespace` and the Unicode 17.0.0 `white_space::lookup` table. The existing Rust implementation is the authority. ASCII whitespace is U+0009–U+000D and U+0020; the non-ASCII table contributes 19 scalars. The table lookup is expressed as its equivalent 25-scalar range/set decision in Kotlin, not replaced with the host platform's whitespace classification.

Local originals: `/opt/homebrew/Cellar/rust/1.99.0_1/lib/rustlib/src/rust/library/core/src/char/methods.rs:1087` and `unicode/unicode_data.rs:906`. Exact complete-file hashes are in `source-hashes.json`. Rust Project Contributors; MIT OR Apache-2.0, included here. Modification: map the existing table and ASCII fast path to a Kotlin `when` over a validated Unicode scalar. No source lexer or parser is introduced. The Rust-oracle tests cover the property members and adjacent exclusions.

Existing proc-macro2/Syn helpers delegate to Kotlin `Char.isWhitespace` and add direction marks; they are source-language whitespace helpers, not the Rust standard `char` operation. The inspected Unicode-ident/segmentation/width APIs do not expose this property. This small existing-core adaptation is used only when the Rust method is translated; the frontend still uses proc-macro2/Syn unchanged.
