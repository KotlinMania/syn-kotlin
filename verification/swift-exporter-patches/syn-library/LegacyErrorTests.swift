import Foundation
import Testing
import RuntimeDiagnostic
import KotlinRuntimeSupport
import KotlinRuntime

private enum LegacyFixtureError: Error { case unexpectedWrapper }

@MainActor
@Suite
struct LegacyErrorTests {
    private func diagnostic(_ message: String?) throws -> KotlinBase {
        guard let value = createDiagnostic(message: message) as? KotlinBase else {
            throw LegacyFixtureError.unexpectedWrapper
        }
        return value
    }

    @Test func preservesOriginalReference() throws {
        let original = try diagnostic("original")
        let error = KotlinError(wrapped: original)
        #expect(error.wrapped === original)
        #expect(error.description == "original")
    }

    @Test func observesMessageMutationLazily() throws {
        let original = try diagnostic("before")
        let error = KotlinError(wrapped: original)
        changeDiagnosticMessage(diagnostic: original, message: "after")
        #expect(error.wrapped === original)
        #expect(error.description == "after")
    }

    @Test func replacesWrappedReference() throws {
        let first = try diagnostic("first")
        let second = try diagnostic("second")
        var error = KotlinError(wrapped: first)
        error.wrapped = second
        #expect(error.wrapped === second)
        #expect(error.description == "second")
    }

    @Test func copiesKeepTheirOwnMutableSlot() throws {
        let original = try diagnostic("original")
        let replacement = try diagnostic("replacement")
        let first = KotlinError(wrapped: original)
        var second = first
        second.wrapped = replacement
        #expect(first.wrapped === original)
        #expect(second.wrapped === replacement)
        #expect(first.description == "original")
        #expect(second.description == "replacement")
    }

    @Test func retainsUntilErrorIsReleased() async throws {
        weak var released: AnyObject?
        var held: KotlinError? = try autoreleasepool {
            let original = try diagnostic("held")
            released = original
            return KotlinError(wrapped: original)
        }
        collectDiagnosticGarbage()
        #expect(released != nil)
        #expect(held?.description == "held")
        held = nil
        for _ in 0..<20 {
            collectDiagnosticGarbage()
            if released == nil { break }
            try await Task.sleep(for: .milliseconds(10))
        }
        #expect(released == nil)
    }
}
