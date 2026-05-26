# JS Test-Toolchain Security Patching Reference

This document details the Kotlin/JS npm toolchain resolutions, karma-webpack pins, and lockfile upgrade instructions extracted from `AGENTS.md`.

## 9. JS test-toolchain security patching

Security warnings from Kotlin/JS npm tooling (`glob@7`, `inflight`, `rimraf@3`, old `karma-webpack`, stale `karma` / `mocha` / `webpack` / `webpack-cli`) are real supply-chain findings. Patch via the vendored `karma-webpack` + yarn-resolution pattern. The full block (with all current pins) is below — copy it verbatim into `build.gradle.kts`:

```kotlin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec

rootProject.extensions.configure<NodeJsEnvSpec>("kotlinNodeJsSpec")     { version.set("22.22.2") }
rootProject.extensions.configure<WasmNodeJsEnvSpec>("kotlinWasmNodeJsSpec") { version.set("22.22.2") }
rootProject.extensions.configure<YarnRootEnvSpec>("kotlinYarnSpec")     { version.set("1.22.22") }
rootProject.extensions.configure<WasmYarnRootEnvSpec>("kotlinWasmYarnSpec") { version.set("1.22.22") }

rootProject.extensions.configure<YarnRootExtension>("kotlinYarn") {
    resolution("diff", "8.0.3")
    resolution("**/diff", "8.0.3")
    resolution("fast-uri", "3.1.2")
    resolution("**/fast-uri", "3.1.2")
    resolution("serialize-javascript", "7.0.5")
    resolution("**/serialize-javascript", "7.0.5")
    resolution("webpack", "5.106.2")
    resolution("**/webpack", "5.106.2")
    resolution("follow-redirects", "1.16.0")
    resolution("**/follow-redirects", "1.16.0")
    resolution("lodash", "4.18.1")
    resolution("**/lodash", "4.18.1")
    resolution("ajv", "8.20.0")
    resolution("**/ajv", "8.20.0")
    resolution("brace-expansion", "5.0.6")
    resolution("**/brace-expansion", "5.0.6")
    resolution("flatted", "3.4.2")
    resolution("**/flatted", "3.4.2")
    resolution("minimatch", "10.2.5")
    resolution("**/minimatch", "10.2.5")
    resolution("picomatch", "4.0.4")
    resolution("**/picomatch", "4.0.4")
    resolution("qs", "6.15.2")
    resolution("**/qs", "6.15.2")
    resolution("socket.io-parser", "4.2.6")
    resolution("**/socket.io-parser", "4.2.6")
    resolution("ws", "8.20.1")
    resolution("**/ws", "8.20.1")
}

val patchedKarmaWebpackPackage =
    rootProject.layout.projectDirectory.dir("gradle/npm/karma-webpack").asFile.absolutePath.replace("\\", "/")

rootProject.extensions.configure<NodeJsRootExtension>("kotlinNodeJs") {
    versions.webpack.version = "5.106.2"
    versions.webpackCli.version = "7.0.2"
    versions.karma.version = "npm:karma-maintained@6.4.7"
    versions.karmaWebpack.version = "file:$patchedKarmaWebpackPackage"
    versions.mocha.version = "12.0.0-beta-10"
    versions.kotlinWebHelpers.version = "3.1.0"
}
```

Vendored `karma-webpack` package shape (under `gradle/npm/karma-webpack/package.json`) is in the per-repo `SWIFT.md` / canonical `anstyle-kotlin`. 

Regenerate lockfiles via:

```bash
./gradlew kotlinUpgradeYarnLock kotlinWasmUpgradeYarnLock --rerun-tasks
```

Never hand-edit `kotlin-js-store/yarn.lock` or `kotlin-js-store/wasm/yarn.lock`. If `kotlinStoreYarnLock` reports the lock file changed, run the upgrade task — that failure is the guardrail.

### Verification of JS resolutions

```bash
rg -n '"karma-webpack"' build/js/packages -g package.json
rg -n "glob@\^7|rimraf@\^3|inflight@|karma-webpack@5\.0\.1|karma@github:Kotlin/karma|mocha@11|webpack@5\.101|webpack-cli@6" \
  kotlin-js-store build/js/yarn.lock build/wasm/yarn.lock -g yarn.lock
./gradlew jsNodeTest wasmJsNodeTest macosArm64Test
./gradlew test
```

### Dependabot push-time warnings = act immediately

If `git push` prints a `remote: GitHub found N vulnerability` banner, open a `security/<pkg>-<patched>` branch + PR in the same session. Bump the `kotlinYarn` resolution, regenerate locks via `./gradlew kotlinUpgradeYarnLock kotlinWasmUpgradeYarnLock --rerun-tasks`, and verify with `jsNodeTest` / `wasmJsNodeTest`. Don't footnote it.
