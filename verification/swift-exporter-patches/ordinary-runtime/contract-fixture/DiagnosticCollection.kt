package runtimediagnostic

@OptIn(kotlin.native.runtime.NativeRuntimeApi::class)
fun collectDiagnosticGarbage() {
    kotlin.native.runtime.GC.collect()
}
