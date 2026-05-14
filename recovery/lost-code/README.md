# syn-kotlin lost-code recovery

This directory records objects that were unreachable before the recovery pass on
2026-05-14.

## Inventory

- 7 unreachable commits are listed in `commit-map.tsv` and exported under
  `patches/`.
- 12 unreachable trees are listed in `unreachable-tree-map.tsv`.
- 8 loose blobs are listed in `blob-map.tsv` and exported under `blobs/`.

## Source review

The existing `chore/build-homogenization-part-2` branch, after a non-fast-forward
merge of `origin/main`, already contains the recovered Kotlin source work:

- `src/commonMain/kotlin/io/github/kotlinmania/syn/Print.kt`
- `src/commonMain/kotlin/io/github/kotlinmania/syn/Sealed.kt`
- `src/commonMain/kotlin/io/github/kotlinmania/syn/Error.kt`
- `src/commonMain/kotlin/io/github/kotlinmania/syn/Ident.kt`
- `src/commonMain/kotlin/io/github/kotlinmania/syn/Span.kt`
- `src/commonMain/kotlin/io/github/kotlinmania/syn/Thread.kt`
- `src/commonMain/kotlin/io/github/kotlinmania/syn/token/Token.kt`

Those recovered source blobs were byte-for-byte identical to the current files,
so duplicate source rows were removed from the tree map.

The remaining loose blobs are older workflow and README variants. They are
preserved as evidence but were not applied over the current build-template state.
