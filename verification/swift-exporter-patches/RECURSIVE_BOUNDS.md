# Recursive upper-bound bridge-call generation

This is a separate producer failure from the RuntimeSupport import/Sendable errors and enum defaults. No producer correction or consumer adoption is represented by this analysis.

## Actual witness and provenance

BitFlags source `32d45a573509701d67d5f2697178b7bccdba4b9c`, full-root execution4506, fails macosArm64DebugSwiftExport before generated Native bridge/runtime compilation. The subsequent single direct stacktrace execution15478 fails with StackOverflowError and 73 repetitions of the type-renderer cycle. Raw log SHA256 is `6db4b70770ccaf71aa09477257eddb623e3f1a4228c228aefb80417c73cad954`; it is retained at workspace `automation-artifacts/2026-10-20-ansi-pr99/bitflags-public-flags/swift-stacktrace.log`.

The source explicitly selects exporter2.4.20, matching its catalog and property. Retained execution-history strings name that artifact, whose cached bytes match official SHA256 `24c6159bcf0087ecf86bfc9bad01a975e192abcf75005b248292d85a139c2055`. The complete resolved worker classpath was not captured. Cached presence and task-history strings must not be presented as a full classpath receipt. The owner's compiled JVM signature inventory is JVM evidence only.

The inspected official source revision is `890ac1d94fdb80eb85f0eeb5be5e4352df987b2f`. TypeRenderer.kt Git blob is `5a64ea9f1dd8b33c02533e03646cf1b03ef3ec87`, SHA256 `31dc646227822ee3c5837dbe8095f7987077a398e75c2efd096d61a3f7738d60`. Its stock method body was independently inspected in the official artifact. The classification receipt is workspace `automation-artifacts/2026-10-20-syn-isolated-library-validation/production-delivery/bitflags-recursive-bound-classification.json`, SHA256 `f8ae59a7d3e51df42752143d266ee45db9172330a0bc60eb5721b37214f3101b`.

## Source mechanism

UpperBoundTypeProjectionRenderer replaces a type parameter with its single upper bound, then calls renderType with the same renderer. For a genuine bound B:Flags<B>, rendering the Flags class argument encounters B again and repeats. There is no cycle guard in that path. The FIR annotation accessor at the top of the exhausted stack is not evidence that annotations caused the recursion.

The caller matters: [SirFunctionFromKtSymbol.buildForwardKotlinCall](https://github.com/JetBrains/kotlin/blob/890ac1d94fdb80eb85f0eeb5be5e4352df987b2f/native/swift/sir-light-classes/src/org/jetbrains/sir/lightclasses/nodes/SirFunctionFromKtSymbol.kt#L143) renders function type-parameter upper bounds with this renderer and inserts the text as explicit generic arguments in the generated Kotlin call. It is not merely diagnostic formatting. A terminating text substitution can still produce an invalid F-bounded call.

The public Iter, IterNames and IterDefinedNames generic factories are source-level candidate triggers, including their factories taking Flags<B>. The exact first declaration triggering the executed failure has not been isolated. Do not attribute it to one declaration solely from the repeating stack.

Published2.4.20 and2.4.21 contain a byte-identical UpperBoundTypeProjectionRenderer class, SHA256 `4bc3ea511103bc214625a99546133ae4957de9c579e0cac99d198bcc9f93a734`. The inspected2.5.0-Beta1 class differs and delegates upper-bound resolution to AnalysisApiUtilsKt.resolveUpperBound; its disassembly still resolves a parameter to its bound and passes it to the same renderer. No preview execution was performed, so this is not a reproduced preview failure or a demonstrated preview fix.

## Repair and validation contract

- Preserve the real recursive Kotlin bounds, public declarations and actual flag storage widths. Do not hide declarations or add aliases/facades to avoid the producer path.
- A recursion guard must be scoped to the active rendering chain and symbol identity, not shared mutable state on the singleton renderer. Repeated independent sibling arguments must remain valid.
- Resolve the generated-call problem as well as termination. Any inference-based call generation must be checked against actual captured parameter types. Substituting Any, a star projection or Nothing without proving the recursive bound and runtime behavior is not a repair.
- Isolate a genuine recursive generic factory as an expected-negative exporter regression, retain a nonrecursive control, and inspect the emitted Kotlin calls. Require actual Kotlin/Native bridge compilation and static linking before interpreting Swift behavior results.
- Validate the real BitFlags API and its existing target/Swift tests after the focused producer regression. Do not count the owner's 693 Kotlin cases as Swift acceptance. Preserve all warning and target gates.
- Component57182's six enum cases and three ProcMacro2 cases establish their own scoped behavior. They do not establish a fix for this recursive-bound family. A new source/class delta needs its own provenance, baseline audit and Native/Swift validation.

Only logs, pinned source and official tool bytes were inspected here. No BitFlags source, generated bridge, compiler installation or shared cache was modified, and no duplicate compiler/CI command was launched.
