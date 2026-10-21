import KotlinRuntime
import KotlinRuntimeSupport

public func construct(_ original: KotlinBase) -> KotlinError {
    KotlinError(wrapped: original)
}

public func originalObject(_ error: KotlinError) -> KotlinBase {
    error.wrapped
}

public func replaceOriginal(_ error: inout KotlinError, with replacement: KotlinBase) {
    error.wrapped = replacement
}
