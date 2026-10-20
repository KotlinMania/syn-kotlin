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

    @Test("Pass a typed token transformation through the exported callback")
    func tokenTransformPreservesTheTokenStream() {
        let tokens = ExportedKotlinPackages.io.github.kotlinmania.procmacro2.TokenStream.Companion.shared.new()
        let span = ExportedKotlinPackages.io.github.kotlinmania.procmacro2.Span.Companion.shared.callSite()
        let transform = tokenStreamTransform(function: { input in input })
        let handler = TransparentFn(fn: transform, span: span)
        #expect(handler.fn(tokens: tokens) === tokens)
    }

    @Test("Inspect and render a Syn diagnostic through a failure result")
    func diagnosticRetainsItsMessageAndIdentity() {
        let span = ExportedKotlinPackages.io.github.kotlinmania.procmacro2.Span.Companion.shared.callSite()
        let diagnostic = SynError.Companion.shared.new(span: span, message: "invalid syntax")
        let result = SynResult.Companion.shared.failure(error: diagnostic)
        #expect(result.isFailure)
        #expect(result.exceptionOrNull() === diagnostic)
        #expect(diagnostic.message == "invalid syntax")
        #expect(diagnostic.toCompileError().toString().contains("invalid syntax"))
    }
}
