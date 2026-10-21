# Exhaustive enum switch source repair

This upstream source diff removes the default branch only from three exhaustive switches over nonempty Swift enum values: bridge conversion, description and rawValue. It retains empty-enum fallback, unknown native ordinal traps and invalid string/integer handling. It changes the Kotlin exporter source, not generated consumer Swift.

The exact source revision, Git blob and hashes are in provenance.json. The actual ProcMacro2 diagnostic2800 reproduced six strict Swift failures matching these three sites across Delimiter and Spacing. The three existing Swift runtime cases did not execute.

Retained isolated baseline and candidate producer compilations passed. Artifact packaging has not been accepted: one unchanged inline attributes method differs in the baseline reconstruction. The original audit stop is preserved; no normalization or baseline waiver is applied. The remaining eleven compiled classes match the existing strict normalization. Source preservation here is not a packaged-component, native ABI, enum behavior or release claim. The current49851 component does not contain this source correction.
