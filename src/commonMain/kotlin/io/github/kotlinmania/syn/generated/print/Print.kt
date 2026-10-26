// port-lint: source print.rs
package io.github.kotlinmania.syn.generated.print

// Generated from Syn AST; complete original retained in input.rs.
// Retained shared Option: keep caller storage immutable while borrowed. Trait witnesses preserve Rust provider behavior; custom Drop/allocation identity are outside this lowering.
internal sealed class __rtkOption<out T> { data object None : __rtkOption<kotlin.Nothing>(); class Some<T>(val value:T) : __rtkOption<T>() }
internal class `TokensOrDefault`<`T`>(val __rtkField0: __rtkOption<`T`>, private val __rtkEmit: (`T`, io.github.kotlinmania.procmacro2.TokenStream) -> kotlin.Unit, private val __rtkDefault: () -> `T`) : io.github.kotlinmania.quote.ToTokens {
    override fun `toTokens`(tokens: io.github.kotlinmania.procmacro2.TokenStream) {
        when(val __rtkValue0 = this.__rtkField0) {
            is __rtkOption.Some -> __rtkEmit(__rtkValue0.value, tokens)
            __rtkOption.None -> __rtkEmit(__rtkDefault(), tokens)
        }
    }
}
