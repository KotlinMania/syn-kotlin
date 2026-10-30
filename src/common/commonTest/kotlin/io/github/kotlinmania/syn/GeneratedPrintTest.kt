// port-lint: tests src/print.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.*
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Semi
import io.github.kotlinmania.syn.token.As
import io.github.kotlinmania.syn.token.PathSep
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratedPrintTest {
    @Test fun productionAdapterMatchesNinetyRustObservations() {
        val observations=mutableListOf<String>()
        defaults("semi",Semi::default,observations)
        defaults("as",As::default,observations)
        defaults("pathsep",PathSep::default,observations)
        assertEquals(expectedRust.lines(),observations)
    }
}

private class ProbePanic : RuntimeException()
private class State(val mode: Int) { var defaults=0; var prints=0; val events=mutableListOf<String>() }
private class Counted<T:ToTokens>(val value:T, val id:Int, val state:State):ToTokens {
    override fun toTokens(tokens:TokenStream) {
        state.prints++;state.events.add("p$id")
        Ident.new("v$id",Span.callSite()).cloneToken().toTokens(tokens)
        value.toTokens(tokens)
        if(state.mode==2) throw ProbePanic()
    }
}
private fun <T:ToTokens> defaults(label:String, make:()->T, observations:MutableList<String>) {
    for(present in listOf(false,true)) for(mode in 0..2) {
        val state=State(mode)
        val value: ToTokens? = if(present) Counted(make(),100,state) else null
        val wrapper=tokensOrDefault(value) {
            state.defaults++;val id=state.defaults;state.events.add("d$id")
            if(mode==1) throw ProbePanic()
            Counted(make(),id,state)
        }
        val output=TokenStream.fromString("prefix").getOrThrow()
        for(call in 0..4) {
            val status=try { wrapper.toTokens(output);"ok" } catch(_:ProbePanic) { "panic" }
            observations.add("default|$label|${if(present) 1 else 0}|$mode|$call|$status|${state.defaults}|${state.prints}|${state.events.joinToString(",")}|$output")
        }
    }
}

// Captured from complete frozen Rust print.rs, not the preceding handwritten Kotlin.
private val expectedRust = """
default|semi|0|0|0|ok|1|1|d1,p1|prefix v1 ;
default|semi|0|0|1|ok|2|2|d1,p1,d2,p2|prefix v1 ; v2 ;
default|semi|0|0|2|ok|3|3|d1,p1,d2,p2,d3,p3|prefix v1 ; v2 ; v3 ;
default|semi|0|0|3|ok|4|4|d1,p1,d2,p2,d3,p3,d4,p4|prefix v1 ; v2 ; v3 ; v4 ;
default|semi|0|0|4|ok|5|5|d1,p1,d2,p2,d3,p3,d4,p4,d5,p5|prefix v1 ; v2 ; v3 ; v4 ; v5 ;
default|semi|0|1|0|panic|1|0|d1|prefix
default|semi|0|1|1|panic|2|0|d1,d2|prefix
default|semi|0|1|2|panic|3|0|d1,d2,d3|prefix
default|semi|0|1|3|panic|4|0|d1,d2,d3,d4|prefix
default|semi|0|1|4|panic|5|0|d1,d2,d3,d4,d5|prefix
default|semi|0|2|0|panic|1|1|d1,p1|prefix v1 ;
default|semi|0|2|1|panic|2|2|d1,p1,d2,p2|prefix v1 ; v2 ;
default|semi|0|2|2|panic|3|3|d1,p1,d2,p2,d3,p3|prefix v1 ; v2 ; v3 ;
default|semi|0|2|3|panic|4|4|d1,p1,d2,p2,d3,p3,d4,p4|prefix v1 ; v2 ; v3 ; v4 ;
default|semi|0|2|4|panic|5|5|d1,p1,d2,p2,d3,p3,d4,p4,d5,p5|prefix v1 ; v2 ; v3 ; v4 ; v5 ;
default|semi|1|0|0|ok|0|1|p100|prefix v100 ;
default|semi|1|0|1|ok|0|2|p100,p100|prefix v100 ; v100 ;
default|semi|1|0|2|ok|0|3|p100,p100,p100|prefix v100 ; v100 ; v100 ;
default|semi|1|0|3|ok|0|4|p100,p100,p100,p100|prefix v100 ; v100 ; v100 ; v100 ;
default|semi|1|0|4|ok|0|5|p100,p100,p100,p100,p100|prefix v100 ; v100 ; v100 ; v100 ; v100 ;
default|semi|1|1|0|ok|0|1|p100|prefix v100 ;
default|semi|1|1|1|ok|0|2|p100,p100|prefix v100 ; v100 ;
default|semi|1|1|2|ok|0|3|p100,p100,p100|prefix v100 ; v100 ; v100 ;
default|semi|1|1|3|ok|0|4|p100,p100,p100,p100|prefix v100 ; v100 ; v100 ; v100 ;
default|semi|1|1|4|ok|0|5|p100,p100,p100,p100,p100|prefix v100 ; v100 ; v100 ; v100 ; v100 ;
default|semi|1|2|0|panic|0|1|p100|prefix v100 ;
default|semi|1|2|1|panic|0|2|p100,p100|prefix v100 ; v100 ;
default|semi|1|2|2|panic|0|3|p100,p100,p100|prefix v100 ; v100 ; v100 ;
default|semi|1|2|3|panic|0|4|p100,p100,p100,p100|prefix v100 ; v100 ; v100 ; v100 ;
default|semi|1|2|4|panic|0|5|p100,p100,p100,p100,p100|prefix v100 ; v100 ; v100 ; v100 ; v100 ;
default|as|0|0|0|ok|1|1|d1,p1|prefix v1 as
default|as|0|0|1|ok|2|2|d1,p1,d2,p2|prefix v1 as v2 as
default|as|0|0|2|ok|3|3|d1,p1,d2,p2,d3,p3|prefix v1 as v2 as v3 as
default|as|0|0|3|ok|4|4|d1,p1,d2,p2,d3,p3,d4,p4|prefix v1 as v2 as v3 as v4 as
default|as|0|0|4|ok|5|5|d1,p1,d2,p2,d3,p3,d4,p4,d5,p5|prefix v1 as v2 as v3 as v4 as v5 as
default|as|0|1|0|panic|1|0|d1|prefix
default|as|0|1|1|panic|2|0|d1,d2|prefix
default|as|0|1|2|panic|3|0|d1,d2,d3|prefix
default|as|0|1|3|panic|4|0|d1,d2,d3,d4|prefix
default|as|0|1|4|panic|5|0|d1,d2,d3,d4,d5|prefix
default|as|0|2|0|panic|1|1|d1,p1|prefix v1 as
default|as|0|2|1|panic|2|2|d1,p1,d2,p2|prefix v1 as v2 as
default|as|0|2|2|panic|3|3|d1,p1,d2,p2,d3,p3|prefix v1 as v2 as v3 as
default|as|0|2|3|panic|4|4|d1,p1,d2,p2,d3,p3,d4,p4|prefix v1 as v2 as v3 as v4 as
default|as|0|2|4|panic|5|5|d1,p1,d2,p2,d3,p3,d4,p4,d5,p5|prefix v1 as v2 as v3 as v4 as v5 as
default|as|1|0|0|ok|0|1|p100|prefix v100 as
default|as|1|0|1|ok|0|2|p100,p100|prefix v100 as v100 as
default|as|1|0|2|ok|0|3|p100,p100,p100|prefix v100 as v100 as v100 as
default|as|1|0|3|ok|0|4|p100,p100,p100,p100|prefix v100 as v100 as v100 as v100 as
default|as|1|0|4|ok|0|5|p100,p100,p100,p100,p100|prefix v100 as v100 as v100 as v100 as v100 as
default|as|1|1|0|ok|0|1|p100|prefix v100 as
default|as|1|1|1|ok|0|2|p100,p100|prefix v100 as v100 as
default|as|1|1|2|ok|0|3|p100,p100,p100|prefix v100 as v100 as v100 as
default|as|1|1|3|ok|0|4|p100,p100,p100,p100|prefix v100 as v100 as v100 as v100 as
default|as|1|1|4|ok|0|5|p100,p100,p100,p100,p100|prefix v100 as v100 as v100 as v100 as v100 as
default|as|1|2|0|panic|0|1|p100|prefix v100 as
default|as|1|2|1|panic|0|2|p100,p100|prefix v100 as v100 as
default|as|1|2|2|panic|0|3|p100,p100,p100|prefix v100 as v100 as v100 as
default|as|1|2|3|panic|0|4|p100,p100,p100,p100|prefix v100 as v100 as v100 as v100 as
default|as|1|2|4|panic|0|5|p100,p100,p100,p100,p100|prefix v100 as v100 as v100 as v100 as v100 as
default|pathsep|0|0|0|ok|1|1|d1,p1|prefix v1 ::
default|pathsep|0|0|1|ok|2|2|d1,p1,d2,p2|prefix v1 :: v2 ::
default|pathsep|0|0|2|ok|3|3|d1,p1,d2,p2,d3,p3|prefix v1 :: v2 :: v3 ::
default|pathsep|0|0|3|ok|4|4|d1,p1,d2,p2,d3,p3,d4,p4|prefix v1 :: v2 :: v3 :: v4 ::
default|pathsep|0|0|4|ok|5|5|d1,p1,d2,p2,d3,p3,d4,p4,d5,p5|prefix v1 :: v2 :: v3 :: v4 :: v5 ::
default|pathsep|0|1|0|panic|1|0|d1|prefix
default|pathsep|0|1|1|panic|2|0|d1,d2|prefix
default|pathsep|0|1|2|panic|3|0|d1,d2,d3|prefix
default|pathsep|0|1|3|panic|4|0|d1,d2,d3,d4|prefix
default|pathsep|0|1|4|panic|5|0|d1,d2,d3,d4,d5|prefix
default|pathsep|0|2|0|panic|1|1|d1,p1|prefix v1 ::
default|pathsep|0|2|1|panic|2|2|d1,p1,d2,p2|prefix v1 :: v2 ::
default|pathsep|0|2|2|panic|3|3|d1,p1,d2,p2,d3,p3|prefix v1 :: v2 :: v3 ::
default|pathsep|0|2|3|panic|4|4|d1,p1,d2,p2,d3,p3,d4,p4|prefix v1 :: v2 :: v3 :: v4 ::
default|pathsep|0|2|4|panic|5|5|d1,p1,d2,p2,d3,p3,d4,p4,d5,p5|prefix v1 :: v2 :: v3 :: v4 :: v5 ::
default|pathsep|1|0|0|ok|0|1|p100|prefix v100 ::
default|pathsep|1|0|1|ok|0|2|p100,p100|prefix v100 :: v100 ::
default|pathsep|1|0|2|ok|0|3|p100,p100,p100|prefix v100 :: v100 :: v100 ::
default|pathsep|1|0|3|ok|0|4|p100,p100,p100,p100|prefix v100 :: v100 :: v100 :: v100 ::
default|pathsep|1|0|4|ok|0|5|p100,p100,p100,p100,p100|prefix v100 :: v100 :: v100 :: v100 :: v100 ::
default|pathsep|1|1|0|ok|0|1|p100|prefix v100 ::
default|pathsep|1|1|1|ok|0|2|p100,p100|prefix v100 :: v100 ::
default|pathsep|1|1|2|ok|0|3|p100,p100,p100|prefix v100 :: v100 :: v100 ::
default|pathsep|1|1|3|ok|0|4|p100,p100,p100,p100|prefix v100 :: v100 :: v100 :: v100 ::
default|pathsep|1|1|4|ok|0|5|p100,p100,p100,p100,p100|prefix v100 :: v100 :: v100 :: v100 :: v100 ::
default|pathsep|1|2|0|panic|0|1|p100|prefix v100 ::
default|pathsep|1|2|1|panic|0|2|p100,p100|prefix v100 :: v100 ::
default|pathsep|1|2|2|panic|0|3|p100,p100,p100|prefix v100 :: v100 :: v100 ::
default|pathsep|1|2|3|panic|0|4|p100,p100,p100,p100|prefix v100 :: v100 :: v100 :: v100 ::
default|pathsep|1|2|4|panic|0|5|p100,p100,p100,p100,p100|prefix v100 :: v100 :: v100 :: v100 :: v100 ::
""".trimIndent()
