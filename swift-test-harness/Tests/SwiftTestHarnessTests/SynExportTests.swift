import Testing
import Syn

private enum SampleError: Error {
    case failed
}

@Suite("Syn Swift Export Tests")
struct SynExportTests {
    @Test("Verify Syn module imports cleanly and does not shadow Swift.Error or Swift.Result")
    func smokeTest() {
        let result: Result<String, SampleError> = .success(LifetimePeek.shared.display())
        #expect((try? result.get()) == "lifetime")
    }
}
