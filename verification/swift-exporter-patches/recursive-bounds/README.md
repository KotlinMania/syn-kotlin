# Recursive-bound bridge regression

The public Kotlin recursive contracts compile on Kotlin/Native. The Swift exporter expands a function type parameter through its own class upper bound without a terminating call representation.

Factories.kt is an authored minimal exporter fixture. Stock 2.4.20 export execution79429 compiles the main Native library and then throws StackOverflowError. Its actual complete exporter tooling classpath was captured and hashed. This is separate from the BitFlags owner's real published-component Native consumer proof.

SingleCapture.kt is a Native executable regression. Compile with the existing isolated Native2.4.20 SDK, target macos_arm64, program output, Werror and entry boundfixture.main. Actual34733 compiled/linked; actual77287 ran the ARM64 Mach-O and passed both identity/value assertions. The first program command omitted the packaged entry name and failed as a harness error; it is retained separately.

FactoryInference.kt retains the incompatible two-input case. Actual19399 rejects two independently captured argument types. Do not turn this negative into a passing test by changing the recursive bound, casting to an invented bound, hiding an API or weakening warnings.

The patch under rejected-inference is an experimental source change, **not an accepted repair or part of the delivery patch order**. It detects active type-parameter cycles and uses call inference instead of rendering explicit recursive arguments. It uses no replacement bound. Source revision890ac1d94fdb80eb85f0eeb5be5e4352df987b2f and official exporter24c615 are its baseline. Reconstructing the unchanged source requires its exact same-module AnnotationUtils, lazyWithSessions and TypeTranslationUtils inputs plus the pinned bootstrap. The supported name-based destructuring syntax option is required. Actual30095 matches all14 classes' full member flags, signatures and instructions using the existing unchanged comparator.

Candidate25902 compiles. Delta audit11305 finds one changed call-construction body and two added private helper methods, access flags18/26; the other13 classes' method/member graphs are unchanged. Isolated component d0d307231e0dc02dbfa6749aa607ba3d3e3348ebabc2a0f1bac7384b65ff8bd1 replaces one existing class atop57182, with6263 file entries and zero additions/removals. It is not installed, adopted or published.

Actual71898 passes export generation but fails generated Native compilation with five located errors. The two-argument withRemaining call loses the shared B capture. Allocation and initialization emit FlagIterator<Flags<*>>, which violates the real recursive bound. The generated source is preserved untouched. No Native bridge archive or Swift test result follows; Swift cases executed:0. A successful terminating renderer or host-tool compile is insufficient.

The required source correction must preserve shared generic relationships across related parameters and class allocation/initialization, then compile and link the actual generated Native bridge before strict Swift behavior tests. Public factory visibility also needs checking: this emitted fixture does not expose create. Do not treat a separately successful direct Native source call as evidence that the generated Swift factory exists.

Raw attempts, sources, pinned inputs, commands, full audits and logs are in workspace automation-artifacts/2026-10-20-syn-isolated-library-validation/recursive-bound-repair. results.json pins the exact checkpoint and generated source/log hashes. No BitFlags source, installed compiler, shared SDK or published dependency was changed. Component57182's ProcMacro2/enum successes remain separate.
