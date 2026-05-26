# Build Gate and Target Management Reference

This document contains the detailed build-gate requirements, canonical `build.gradle.kts` templates, and target retirement/deprecation checklists extracted from `AGENTS.md`.

## 5. The build-gate — `build` must compile every configured target

Default Kotlin Multiplatform `./gradlew build` does **not** link every native test binary or assemble every XCFramework. That makes `build` a fake-green gate in many repos. The 2026-05-19 `async-channel-kotlin` incident proved this workspace-wide. **Every repo with the broad target matrix needs the `fullTargetBuildTaskNames` wiring**:

```kotlin
val fullTargetBuildTaskNames = setOf(
    // Android KMP
    "compileAndroidMain", "compileAndroidHostTest", "compileAndroidDeviceTest",
    "assembleAndroidMain", "assembleUnitTest", "assembleAndroidTest",
    // JVM
    "jvmMainClasses", "jvmTestClasses",
    // JS / Wasm
    "jsMainClasses", "jsTestClasses",
    "wasmJsMainClasses", "wasmJsTestClasses",
    "wasmWasiMainClasses", "wasmWasiTestClasses",
    // Native binaries + test binaries (all configured native targets)
    "androidNativeArm32Binaries", "androidNativeArm32TestBinaries",
    "androidNativeArm64Binaries", "androidNativeArm64TestBinaries",
    "androidNativeX64Binaries",   "androidNativeX64TestBinaries",
    "androidNativeX86Binaries",   "androidNativeX86TestBinaries",
    "iosArm64Binaries",           "iosArm64TestBinaries",
    "iosSimulatorArm64Binaries",  "iosSimulatorArm64TestBinaries",
    "iosX64Binaries",             "iosX64TestBinaries",
    "linuxArm64Binaries",         "linuxArm64TestBinaries",
    "linuxX64Binaries",           "linuxX64TestBinaries",
    "macosArm64Binaries",         "macosArm64TestBinaries",
    "mingwX64Binaries",           "mingwX64TestBinaries",
    "tvosArm64Binaries",          "tvosArm64TestBinaries",
    "tvosSimulatorArm64Binaries", "tvosSimulatorArm64TestBinaries",
    // watchosArm32 is retired workspace-wide as of 2026-05-24 — see below.
    "watchosArm64Binaries",       "watchosArm64TestBinaries",
    "watchosDeviceArm64Binaries", "watchosDeviceArm64TestBinaries",
    "watchosSimulatorArm64Binaries", "watchosSimulatorArm64TestBinaries",
    // Swift Export bridge + XCFramework (substitute the repo's framework name)
    "embedSwiftExportForXcode",
    "assemble<Name>XCFramework",
)

tasks.named("build") {
    dependsOn(fullTargetBuildTaskNames)
}

afterEvaluate {
    tasks.named("build") {
        dependsOn(
            tasks.matching {
                name.endsWith("MainClasses") ||
                    name.endsWith("TestClasses") ||
                    name.endsWith("Binaries") ||
                    name.endsWith("XCFramework")
            },
        )
    }
}
```

The explicit set is the **audit contract**. The `afterEvaluate` matcher is a safety net for future generated tasks but is not enough alone — dynamic-only attempts dropped native test-binary links in the dry-run graph during the original investigation.

### Rules around the build gate

- **Never shrink the target gate.** If `fullTargetBuildTaskNames` fails on a missing task, **ADD the target to `kotlin { … }`**; never filter the gate to match the smaller surface. The gate is the contract; the target block is what the gate forces you to declare.
- **Real build, not `--dry-run`.** For all-target gate verification, run a real `./gradlew build`. `--dry-run` is fine as an *intermediate* check but never counts as the gate pass; failures escape to remote CI that way.
- **Local build before push.** Run the focused repo's narrowest local Gradle gate before pushing or PR-ing — even when the fix is identical to one verified elsewhere. "Identical shape" is not "verified here."
- **Acceptance commands:**
  ```bash
  ./gradlew build --dry-run --console=plain --no-daemon
  ./gradlew clean build --no-daemon
  ./gradlew build --no-daemon
  find build/bin -maxdepth 4 -type f \( -name 'test.kexe' -o -name 'test.exe' \) -print | sort
  find build/XCFrameworks -maxdepth 5 -type d -name '*.xcframework' -print | sort
  ```
  Expected `test.kexe` set covers every configured Android Native, iOS, Linux, macOS, MinGW, tvOS, watchOS target; XCFramework outputs cover both `debug/` and `release/`.

For long target surfaces, a single clean `build` may run long. Do not count a killed or truncated session as a pass — split only to isolate expensive pieces, then run a final `./gradlew build --no-daemon --console=plain --no-configuration-cache` that reaches `BUILD SUCCESSFUL`.

---

## 5.3 Canonical complete `build.gradle.kts` — model after this

The file `/Volumes/stuff/Projects/kotlinmania/canonical/build.gradle.kts.template` is the single source of truth for what every `*-kotlin/` repo's `build.gradle.kts` should look like, section-by-section, with rationale comments at every block. It was adapted from `kasuari-kotlin` v0.1.5 (the most recently verified end-to-end repo — Maven Central publish succeeded on 2026-05-24) with `watchosArm32` retired and the JVM-required guard enforced.

To check a repo against the canonical:

```bash
diff -u /Volumes/stuff/Projects/kotlinmania/canonical/build.gradle.kts.template \
        <repo>/build.gradle.kts \
  | grep -vE "<RepoName>|<repo-name>|<package|<Version>|<Description>|<inception>|<package_dir>"
```

Differences that are NOT placeholder substitutions need explanation.

The canonical has 10 sections, in this order — keep this order in any new repo:

1. **Plugins + project coordinates.** Pinned Kotlin 2.3.21, Android KMP library 9.2.1, vanniktech maven-publish 0.36.0. `group = "io.github.kotlinmania"`. `version` is the only per-release knob.
2. **Android SDK installer.** Gradle-backed, no shell scripts. Installs at configuration time so the Android Gradle plugin can resolve the SDK before any task runs (see [ANDROID.md](file:///Volumes/stuff/Projects/kotlinmania/ANDROID.md)).
3. **`kotlin { … }` target block.** The "never remove" set: `jvm()`, `macosArm64`, all three current iOS targets, both current tvOS targets, all three current watchOS targets (no `watchosArm32`), Linux x64/Arm64, MinGW, all four Android Native targets, `js { browser(); nodejs() }`, `wasmJs { browser(); nodejs() }`, `wasmWasi { nodejs() }`, `swiftExport { … }`, `android { … }`. Plus `XCFramework("<RepoName>")`. Plus `jvmToolchain(21)`. Plus `applyDefaultHierarchyTemplate()`. Plus `allWarningsAsErrors.set(true)`.
4. **Test logging.** Full stack traces + standard streams. Required so test failures in CI surface diagnostics.
5. **Kotlin/JS toolchain + security patches.** Node 24.16.0, Yarn 1.22.22, the workspace-wide yarn resolutions block, vendored `karma-webpack` (see [JS_SECURITY.md](file:///Volumes/stuff/Projects/kotlinmania/JS_SECURITY.md)).
6. **Maven publishing** (vanniktech plugin). Pinned coordinates, Apache 2.0 license, sydneyrenee developer block, SCM URLs.
7. **CodeQL extraction.** `codeqlCompileJvm` task with `prepareCodeqlCommonMainSources` (strips Swift Export annotations for plain JVM kotlinc). `codeqlAndroidAar` for sibling Android artifacts.
8. **Setup tasks.** `setupAndroidSdk` (Kotlin-backed, not Exec). `swiftExportSmokeTest` (runs `embedSwiftExportForXcode` + `swift test`). `test` umbrella that runs the host-portable subset including the Swift smoke test.
9. **Build-gate** (`fullTargetBuildTaskNames` + `tasks.named("build") { dependsOn(...) }` + `afterEvaluate` safety net). The explicit set is the **audit contract**.
10. **Wasm-WASI Node preopens patch.** `patchWasmWasiNodePreopens` runs before `wasmWasiNodeTest` so the runner can see the filesystem.

### The three blocks agents most often misedit (verbatim from the canonical)

When in doubt about these, **copy verbatim from the canonical** — don't infer.

#### (a) `kotlin { … }` target block — Apple target shape, with the iOS-Simulator-static + retired-targets rules baked in.
Substitute `<RepoName>` only:

```kotlin
kotlin {
    applyDefaultHierarchyTemplate()

    compilerOptions {
        allWarningsAsErrors.set(true)                  // never scope this down
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    val xcf = XCFramework("<RepoName>")

    macosArm64 {
        binaries.framework { baseName = "<RepoName>"; xcf.add(this) }
    }

    // ALL three iOS slices are static — iOS Simulator fat XCFramework
    // requires homogeneous static/dynamic across iosSimulatorArm64 +
    // iosX64; iosArm64 is also static for Swift Export bridge symmetry.
    iosArm64           { binaries.framework { baseName = "<RepoName>"; isStatic = true; xcf.add(this) } }
    iosSimulatorArm64  { binaries.framework { baseName = "<RepoName>"; isStatic = true; xcf.add(this) } }
    iosX64             { binaries.framework { baseName = "<RepoName>"; isStatic = true; xcf.add(this) } }

    // tvOS: device + Apple Silicon simulator. tvosX64 RETIRED.
    tvosArm64          { binaries.framework { baseName = "<RepoName>"; xcf.add(this) } }
    tvosSimulatorArm64 { binaries.framework { baseName = "<RepoName>"; xcf.add(this) } }

    // watchOS: 64-bit only. watchosArm32 RETIRED. watchosX64 RETIRED.
    watchosArm64           { binaries.framework { baseName = "<RepoName>"; xcf.add(this) } }
    watchosDeviceArm64     { binaries.framework { baseName = "<RepoName>"; xcf.add(this) } }
    watchosSimulatorArm64  { binaries.framework { baseName = "<RepoName>"; xcf.add(this) } }

    linuxX64();  linuxArm64();  mingwX64()

    androidNativeArm32();  androidNativeArm64()
    androidNativeX86();    androidNativeX64()

    js                                    { browser(); nodejs() }
    @OptIn(ExperimentalWasmDsl::class) wasmJs   { browser(); nodejs() }
    @OptIn(ExperimentalWasmDsl::class) wasmWasi { nodejs() }

    swiftExport {
        moduleName = "<RepoName>"
        flattenPackage = "<package.path>"
    }

    android {
        namespace = "<package.path>"
        compileSdk = 34
        minSdk = 24
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder { sourceSetTreeName = "test" }
    }

    jvm()                                              // REQUIRED — never remove

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("commonMain/src")
        }
        val commonTest by getting {
            kotlin.srcDir("commonTest/kotlin")
            dependencies { implementation(kotlin("test")) }
        }
    }

    jvmToolchain(21)
}
```

#### (b) `fullTargetBuildTaskNames` build-gate.
The retired targets are **NOT** in this set — adding them back will fail with `Task '…' not found`:

```kotlin
val fullTargetBuildTaskNames = setOf(
    // Android KMP
    "compileAndroidMain", "compileAndroidHostTest", "compileAndroidDeviceTest",
    "assembleAndroidMain", "assembleUnitTest", "assembleAndroidTest",
    "assembleAndroidDeviceTest", "testAndroidHostTest",
    // JVM — REQUIRED
    "jvmMainClasses", "jvmTestClasses",
    // JS / Wasm
    "jsMainClasses", "jsTestClasses",
    "wasmJsMainClasses", "wasmJsTestClasses",
    "wasmWasiMainClasses", "wasmWasiTestClasses",
    // Native binaries + test binaries
    "androidNativeArm32Binaries",    "androidNativeArm32TestBinaries",
    "androidNativeArm64Binaries",    "androidNativeArm64TestBinaries",
    "androidNativeX64Binaries",      "androidNativeX64TestBinaries",
    "androidNativeX86Binaries",      "androidNativeX86TestBinaries",
    "iosArm64Binaries",              "iosArm64TestBinaries",
    "iosSimulatorArm64Binaries",     "iosSimulatorArm64TestBinaries",
    "iosX64Binaries",                "iosX64TestBinaries",
    "linuxArm64Binaries",            "linuxArm64TestBinaries",
    "linuxX64Binaries",              "linuxX64TestBinaries",
    "macosArm64Binaries",            "macosArm64TestBinaries",
    "mingwX64Binaries",              "mingwX64TestBinaries",
    "tvosArm64Binaries",             "tvosArm64TestBinaries",
    "tvosSimulatorArm64Binaries",    "tvosSimulatorArm64TestBinaries",
    // watchosArm32* RETIRED — do not re-add
    "watchosArm64Binaries",          "watchosArm64TestBinaries",
    "watchosDeviceArm64Binaries",    "watchosDeviceArm64TestBinaries",
    "watchosSimulatorArm64Binaries", "watchosSimulatorArm64TestBinaries",
    // Swift Export + XCFramework
    "swiftExportSmokeTest",
    "assemble<RepoName>XCFramework",
)

tasks.named("build") { dependsOn(fullTargetBuildTaskNames) }

afterEvaluate {
    tasks.named("build") {
        dependsOn(tasks.matching {
            name.endsWith("MainClasses") || name.endsWith("TestClasses") ||
                name.endsWith("Binaries") || name.endsWith("XCFramework")
        })
    }
}
```

#### (c) `swiftExportSmokeTest` + `test` umbrella.
Wires `swift test` into `./gradlew test` so Swift Export failures surface locally:

```kotlin
val swiftExportSmokeTest = tasks.register("swiftExportSmokeTest") {
    group = "verification"
    description = "Builds the Swift Export SPM package and runs swift test against it."
    onlyIf {
        if (!isMacHost) logger.lifecycle("swiftExportSmokeTest: skipped (requires macOS)")
        isMacHost
    }
    outputs.upToDateWhen { false }
    doLast {
        val execOperations = serviceOf<ExecOperations>()
        val swiftBuildDir = layout.buildDirectory.dir("swift-test").get().asFile.absolutePath
        execOperations.exec {
            workingDir = projectDir
            commandLine("./gradlew", "embedSwiftExportForXcode",
                "--no-configuration-cache", "--no-daemon", "--console=plain")
            environment(mapOf(
                "BUILT_PRODUCTS_DIR" to swiftBuildDir,
                "TARGET_BUILD_DIR" to swiftBuildDir,
                "SDK_NAME" to "macosx", "CONFIGURATION" to "Debug", "ARCHS" to "arm64",
                "FRAMEWORKS_FOLDER_PATH" to "Frameworks",
                "MACOSX_DEPLOYMENT_TARGET" to "14.0",
                "DEPLOYMENT_TARGET_SETTING_NAME" to "MACOSX_DEPLOYMENT_TARGET",
            ))
        }.assertNormalExitValue()
        execOperations.exec {
            workingDir = layout.projectDirectory.dir("swift-test-harness").asFile
            commandLine("swift", "test")
        }.assertNormalExitValue()
    }
}

tasks.register("test") {
    group = "verification"
    description = "Runs the host-portable test suite (macOS + JS + WasmJS + Android unit + Swift smoke)."
    val defaultTestTasks = listOf(
        "macosArm64Test", "jvmTest", "jsNodeTest", "wasmJsNodeTest",
        "compileAndroidMain", "assembleUnitTest", "swiftExportSmokeTest",
    )
    dependsOn(defaultTestTasks.mapNotNull { taskName -> tasks.findByName(taskName) })
}
```

#### Companion files required

A complete repo also needs:

- **`gradle.properties`** with `kotlin.native.parallelThreads=0` (see [AGENTS.md](file:///Volumes/stuff/Projects/kotlinmania/AGENTS.md#10-kotlinnative--kotlinjs-gotchas)).
- **`gradle/npm/karma-webpack/package.json`** — vendored karma-webpack with hardened glob/rimraf/inflight pins (see [JS_SECURITY.md](file:///Volumes/stuff/Projects/kotlinmania/JS_SECURITY.md)).
- **`swift-test-harness/Package.swift`** + at least one `Tests/.../*.swift` smoke test that does `import <RepoName>` (see [SWIFT.md](file:///Volumes/stuff/Projects/kotlinmania/SWIFT.md)).
- **`.github/workflows/{ci,codeql,publish,ios,macos,tvos,watchos,android,android-native,linux,windows,js,wasm,swift}.yml`** (triggers and reusable workflows).

---

## 5.4 Targets you must NEVER remove

The retirement subsections cover **exactly three** kinds of target removal — `watchosArm32` (Apple-hardware-EOL + Mach-O limit) and the JetBrains-deprecated x86_64 simulators (`tvosX64`, `watchosX64`, `macosX64`). **Nothing else may be removed.** 

The following targets are **always present** in every `*-kotlin/` repo's `build.gradle.kts` target block unless the repo-local docs explicitly document the technical impossibility:

- **`jvm()`** — Java Virtual Machine target. Not optional. Required for `commonMain` consumers on the JVM, for `jvmTest` host verification, and for the Maven publication coordinate that downstream Kotlin/JVM and Android consumers actually depend on. **Never remove `jvm()`.**
- **`macosArm64`, `iosArm64`, `iosSimulatorArm64`, `iosX64`** — Apple device + simulator slices that ship in the XCFramework.
- **`tvosArm64`, `tvosSimulatorArm64`** — current Apple TV hardware + simulator. (`tvosX64` is retired.)
- **`watchosArm64`, `watchosDeviceArm64`, `watchosSimulatorArm64`** — current Apple Watch slices. (`watchosArm32` is retired.)
- **`linuxX64`, `linuxArm64`, `mingwX64`** — Linux + Windows native.
- **`androidNativeArm32`, `androidNativeArm64`, `androidNativeX86`, `androidNativeX64`** — Android Native NDK targets.
- **`js { browser(); nodejs() }`** — Kotlin/JS for both runtimes.
- **`wasmJs { browser(); nodejs() }`** — Kotlin/Wasm-JS for both runtimes.
- **`wasmWasi { nodejs() }`** — Kotlin/Wasm-WASI.
- **`android { … }`** — Android KMP library target with host + device test builders.
- **`swiftExport { … }`** — Swift Export bridge.
- **`XCFramework("<Name>")`** — repo-specific framework registration.

**The forbidden anti-pattern:** When `./gradlew build` fails because `fullTargetBuildTaskNames` references a task that doesn't exist, the **correct fix** is to **add the missing target back to the `kotlin { … }` block** — not to delete the task name from the gate.

If a target genuinely cannot compile (a real upstream impossibility, not "I don't feel like dealing with it"), follow this resolution process:

1. Verify it's a real impossibility.
2. Document the technical reason in the repo's `README.md` and `NEXT_ACTIONS.md`.
3. Open a PR titled `build: document <target> impossibility for this repo` with the technical evidence inline.
4. **Only then** remove the target + matching gate lines + source-set directories (per the retirement scrub procedure below).

---

## 5.5 Retired targets — `watchosArm32` (armv7k)

`watchosArm32` is **retired workspace-wide as of 2026-05-24**. It is no longer part of the standard target surface, the workflow surface, the build-gate, or the source-set layout. Every existing repo carries it as a legacy declaration that must be scrubbed; every new repo generated from the template inherits the corrected (no-armv7k) surface.

### Why retired
- *Apple hardware reality.* armv7k is the Apple `Series 3` and earlier chip family (S1, S1P, S2, S3). watchOS 9 (2022) dropped Series 3; current watchOS releases only run on Series 4+ (64-bit). No currently-supported Apple Watch is 32-bit.
- *Apple submission reality.* Xcode 15+ defaults new watchOS projects to `arm64` only; armv7k is an opt-in legacy slice with no current hardware to target.
- *Mach-O technical landmine.* armv7k uses Mach-O **scattered relocations** with **24-bit offset fields** (~16 MB). Any Kotlin source that embeds binary data (ICU tables, V8 snapshots, language grammars, Unicode property tables, certificate bundles) fails to link with `ld: cannot compile inline asm: can not encode offset '0x…' in resulting scattered relocation`.

### Scrub checklist — three places per repo, all required

1. **`build.gradle.kts` target block** — delete the `watchosArm32 { … }` stanza entirely. Keep `watchosArm64`, `watchosDeviceArm64`, `watchosSimulatorArm64`.

   ```kotlin
   // delete this whole block:
   watchosArm32 {
       binaries.framework { baseName = "<Name>"; xcf.add(this) }
   }
   ```

2. **`.github/workflows/watchos.yml`** — delete the `compileKotlinWatchosArm32` line from the build step. Keep the three 64-bit `compileKotlinWatchos*` lines and any `watchosSimulatorArm64Test`.

3. **Source sets with `actual` declarations** — delete `src/watchosArm32Main/` and `src/watchosArm32Test/` if either exists. If a repo used per-target `actual` (rather than a shared `watchosMain` actual), those files become orphan dead code after the target is removed. Inventory and delete:

   ```bash
   find src -maxdepth 3 -type d -name 'watchosArm32*' 2>/dev/null
   # Each hit is a directory tree to delete after confirming the
   # actuals are mirrored by the watchosMain/appleMain shared source set
   # or by per-target actuals on the surviving watchOS variants.
   ```

   If an `expect` declaration in `commonMain` previously had its only `actual` in `watchosArm32Main`, you must add the matching `actual` to `watchosArm64Main` / `watchosDeviceArm64Main` / `watchosSimulatorArm64Main` (or hoist it to a shared `watchosMain` intermediate source set) before deleting the armv7k actual.

### Verification after scrub

```bash
git grep -i 'watchosarm32'
# Expect: zero hits outside automation-artifacts/ and historical memory/feedback files.

./gradlew build --dry-run --no-daemon --console=plain --no-configuration-cache
# Expect: no *WatchosArm32* tasks in the graph.

./gradlew build --no-daemon
# Expect: BUILD SUCCESSFUL; watchosArm64 / Device / Simulator tasks still present.
```

---

## 5.5.2 Deprecated x86_64 Apple simulator targets — `tvosX64`, `watchosX64`, `macosX64`

JetBrains marked these three Apple x86_64 simulator/native targets **deprecated as of Kotlin 2.3.20**. They have working hardware (Intel Macs still exist) but the toolchain is on a removal trajectory and Apple no longer ships new Intel Mac hardware. Workspace policy: drop them from any repo that declares them.

| Target | Kotlin/Native classification | What it covers | Status |
|---|---|---|---|
| `tvosX64` | Deprecated (Kotlin 2.3.20) | tvOS simulator on Intel Macs | Drop |
| `watchosX64` | Deprecated (Kotlin 2.3.20) | watchOS simulator on Intel Macs | Drop |
| `macosX64` | Deprecated (Kotlin 2.3.20) | macOS native + simulator on Intel | Drop |

### Why
- Kotlin 2.3.20 marks all three as deprecated. Compilation against them now emits deprecation warnings, which fail `allWarningsAsErrors=true` (the starlark template default).
- Intel Mac production ended in 2023; current Xcode 16+ requires Apple Silicon. The hardware that runs these targets natively is EOL.
- The Apple-Silicon-Mac simulator targets (`tvosSimulatorArm64`, `watchosSimulatorArm64`) cover the same testing role on supported developer hardware.

**`tvosArm64` and `tvosSimulatorArm64` are KEPT.** All Apple TV hardware has always been 64-bit ARM (A8 chip onwards, 2015). The standard tvOS target shape (`tvosArm64` + `tvosSimulatorArm64`, no `tvosX64`) is already what most repos ship.

**`iosX64` is KEPT** (for now). It is Tier 3 in Kotlin/Native but NOT deprecated. Apple still publishes x86_64 iOS simulator runtimes.

### Workspace audit on 2026-05-24
Only one repo declares the deprecated targets:

| Repo | Targets to drop | Other cleanup |
|---|---|---|
| `ZLib.kotlin` | `tvosX64()`, `watchosX64()` (lines 53, 58 of `build.gradle.kts`) | Manual source-set wiring at lines 145–164 needs the matching `tvosX64Main`/`tvosX64Test`/`watchosX64Main`/`watchosX64Test` blocks deleted; `println` lines 254–256 reference `tvosX64Test` and `watchosX64Test` and need updating to the Arm64 simulator variants |

The scrub procedure is identical to `watchosArm32` — three places to clean (`build.gradle.kts`, `.github/workflows/<platform>.yml`, source-set directories under `src/<target>Main` and `src/<target>Test`), plus the `fullTargetBuildTaskNames` set if those entries were present. Verification is the same `git grep` + dry-run + build pattern.

**For new ports**: never add the three deprecated targets to a new `build.gradle.kts`. If you're aligning a repo with the canonical template, this is one of the things to scrub during alignment.
