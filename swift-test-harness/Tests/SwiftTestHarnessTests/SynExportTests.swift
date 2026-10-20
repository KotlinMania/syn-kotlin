import Testing
import Syn
import IoGithubKotlinmaniaProcMacro2Kotlin

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

    @Test("Construct and inspect a syntax path through the exported API")
    func pathRemainsModuleStyleWithLeadingColon() {
        let span = ExportedKotlinPackages.io.github.kotlinmania.procmacro2.Span.Companion.shared.callSite()
        let ident = ExportedKotlinPackages.io.github.kotlinmania.procmacro2.Ident.Companion.shared.new(
            string: "select", span: span
        )
        let path = Path.Companion.shared.from(segment: ident)
        #expect(path.isIdent(ident: "select"))
        #expect(path.isModStyle())
        path.leadingColon = ExportedKotlinPackages.io.github.kotlinmania.syn.token.PathSep.Companion.shared.default()
        #expect(path.isModStyle())
        #expect(!path.isIdent(ident: "select"))
        #expect(path.deepCopy().isModStyle())
    }
}
