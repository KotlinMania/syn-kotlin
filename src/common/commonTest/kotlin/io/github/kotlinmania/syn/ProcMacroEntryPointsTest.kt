package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro.bridge.BridgeClientState
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.formatIdent
import io.github.kotlinmania.quote.quote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProcMacroEntryPointsTest {
    @Test
    fun parseMacroInputAndTokenBufferOperateInsideCustomDeriveMacroSession() {
        val structTokens =
            io.github.kotlinmania.procmacro.TokenStream
                .fromString("pub struct Packet { pub id: u64 }")
                .getOrThrow()

        val generated =
            BridgeClientState.runCustomDeriveMacro("PacketDerive", emptyList(), structTokens) { incoming ->
                assertTrue(BridgeClientState.isAvailable())
                val buffered = TokenBuffer.new(incoming.clone())
                val firstIdent = assertNotNull(buffered.begin().ident())
                assertEquals("pub", firstIdent.first.toString())

                when (val parsed = parseMacroInput(incoming, ::parseDeriveInput)) {
                    is ParseMacroSynResult.Success -> {
                        val deriveInput = parsed.value
                        assertEquals("Packet", deriveInput.ident.toString())
                        val helperName = formatIdent("describe{}", deriveInput.ident)
                        val expanded =
                            quote(
                                "impl `#`ty { pub fn `#`helper () -> u64 { 1 } }",
                                mapOf("ty" to deriveInput.ident, "helper" to helperName),
                            )
                        expanded.intoProcMacro()
                    }

                    is ParseMacroSynResult.CompileError -> parsed.tokens
                }
            }

        assertEquals(
            "impl Packet { pub fn describePacket () -> u64 { 1 } }",
            generated.toString(),
        )
    }

    @Test
    fun parseMacroInputEmitsCompileErrorIntoProcMacroOnInvalidSyntax() {
        val brokenTokens =
            io.github.kotlinmania.procmacro.TokenStream
                .fromString("pub fn notAStruct() {}")
                .getOrThrow()

        val diagnosticOutput =
            BridgeClientState.runBangMacro("brokenDerive", brokenTokens) { incoming ->
                val result = parseMacroInput(incoming, ::parseDeriveInput)
                val compileError = assertIs<ParseMacroSynResult.CompileError<DeriveInput>>(result)
                compileError.tokens
            }

        assertTrue(diagnosticOutput.toString().contains("compile_error"))
        assertTrue(diagnosticOutput.toString().contains("expected struct, enum, or union"))
    }

    @Test
    fun parserInterfaceParseEntryPointConsumesProcMacroTokenStream() {
        val deriveParser =
            object : Parser<DeriveInput> {
                override fun parse2(tokens: TokenStream): SynResult<DeriveInput> =
                    parse2(::parseDeriveInput, tokens)
            }

        val input =
            io.github.kotlinmania.procmacro.TokenStream
                .fromString("enum Mode { Fast, Safe }")
                .getOrThrow()

        val out =
            BridgeClientState.runBangMacro("parserTrait", input) { incoming ->
                val parsed = deriveParser.parse(incoming).getOrThrow()
                assertEquals("Mode", parsed.ident.toString())
                val enumData = assertIs<Data.Enum>(parsed.data)
                assertEquals(2, enumData.variants.size)
                val expanded = quote("impl `#`name {}", mapOf("name" to parsed.ident))
                expanded.intoProcMacro()
            }

        assertEquals("impl Mode {  }", out.toString())
    }
}
