import Foundation
import Testing
import RuntimeDiagnostic
import KotlinRuntimeSupport
import KotlinRuntime

private enum DiagnosticFixtureError: Error { case unexpectedWrapper }

@MainActor
@Suite
struct RuntimeSupportContractTests {
    private func exception(_ message: String?) throws -> KotlinRuntime.KotlinBase {
        guard let original = createDiagnostic(message: message) as? KotlinRuntime.KotlinBase else {
            throw DiagnosticFixtureError.unexpectedWrapper
        }
        return original
    }

    @Test func capturesMessage() throws {
        let original = try exception("diagnostic λ")
        let error = KotlinError(wrapped: original)
        #expect(error.message == "diagnostic λ")
        #expect(error.description == "diagnostic λ")
        let empty = KotlinError(wrapped: try exception(""))
        #expect(empty.message.isEmpty)
    }

    @Test func snapshotSurvivesMutation() throws {
        let original = try exception("before")
        let first = KotlinError(wrapped: original)
        changeDiagnosticMessage(diagnostic: original, message: "after")
        let second = KotlinError(wrapped: original)
        #expect(first.message == "before")
        #expect(first.description == "before")
        #expect(second.message == "after")
    }

    @Test func fallbackForMissingMessages() throws {
        let missing = try exception(nil)
        let missingDescription = missing.description
        let plain = try #require(createPlainValue() as? KotlinRuntime.KotlinBase)
        let plainDescription = plain.description
        #expect(KotlinError(wrapped: missing).message == "KotlinException(\(missingDescription))")
        #expect(KotlinError(wrapped: plain).message == "KotlinException(\(plainDescription))")
    }

    @Test func releasesOriginalWrapper() async throws {
        weak var released: AnyObject?
        let error: KotlinError = try autoreleasepool {
            let original = try exception("retained diagnostic")
            released = original
            return KotlinError(wrapped: original)
        }
        for _ in 0..<20 {
            collectDiagnosticGarbage()
            if released == nil { break }
            try await Task.sleep(for: .milliseconds(10))
        }
        #expect(released == nil)
        #expect(error.message == "retained diagnostic")
    }

    @Test func transfersOwnedDiagnostic() async throws {
        let original = try exception("transfer")
        let error = KotlinError(wrapped: original)
        let transferred = await Task.detached { error }.value
        #expect(transferred.message == "transfer")
        #expect(transferred.description == "transfer")
    }
}
