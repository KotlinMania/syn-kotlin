// port-lint: source ty.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.RArrow

/** A type syntax tree node. */
public sealed class Type {
    public data class Array(val elem: Type, val len: Expr) : Type()
    public data class BareFn(val inputs: Punctuated<BareFnArg, io.github.kotlinmania.syn.token.Comma>, val output: ReturnType) : Type()
    public data class Group(val groupToken: io.github.kotlinmania.syn.token.Group, val elem: Type) : Type()
    public data class ImplTrait(val bounds: Punctuated<TypeParamBound, io.github.kotlinmania.syn.token.Plus>) : Type()
    public data class Infer(val underscoreToken: io.github.kotlinmania.syn.token.Underscore) : Type()
    public data class Macro(val mac: io.github.kotlinmania.syn.Macro) : Type()
    public data class Never(val bangToken: io.github.kotlinmania.syn.token.Not) : Type()
    public data class Paren(val parenToken: io.github.kotlinmania.syn.token.Paren, val elem: Type) : Type()
    public data class Path(val qself: QSelf?, val path: io.github.kotlinmania.syn.Path) : Type()
    public data class Ptr(val elem: Type) : Type()
    public data class Reference(val lifetime: Lifetime?, val elem: Type) : Type()
    public data class Slice(val elem: Type) : Type()
    public data class TraitObject(val bounds: Punctuated<TypeParamBound, io.github.kotlinmania.syn.token.Plus>) : Type()
    public data class Tuple(val parenToken: io.github.kotlinmania.syn.token.Paren, val elems: Punctuated<Type, io.github.kotlinmania.syn.token.Comma>) : Type()
    public data class Verbatim(val tokens: TokenStream) : Type()

    public fun copy(): Type =
        when (this) {
            is Array -> copy(elem = elem.copy(), len = len.copy())
            is BareFn -> copy(inputs = inputs.copy({ it.deepCopy() }, { it }), output = output.copy())
            is Group -> copy(elem = elem.copy())
            is ImplTrait -> copy()
            is Infer -> copy()
            is Macro -> copy(mac = mac.deepCopy())
            is Never -> copy()
            is Paren -> copy(elem = elem.copy())
            is Path -> copy(path = path.copy())
            is Ptr -> copy(elem = elem.copy())
            is Reference -> copy(lifetime = lifetime?.deepCopy(), elem = elem.copy())
            is Slice -> copy(elem = elem.copy())
            is TraitObject -> copy()
            is Tuple -> copy(elems = elems.copy({ it.copy() }, { it }))
            is Verbatim -> copy()
        }
}

public data class BareFnArg(
    public val name: Ident?,
    public val ty: Type,
) {
    public fun deepCopy(): BareFnArg =
        BareFnArg(name?.copy(), ty.copy())
}

public sealed class ReturnType {
    public data object Default : ReturnType()
    public data class TypeReturn(val arrowToken: RArrow, val ty: Type) : ReturnType()

    public fun copy(): ReturnType =
        when (this) {
            Default -> Default
            is TypeReturn -> copy(ty = ty.copy())
        }
}

public data class Macro(
    public val path: Path,
    public val delimiter: MacroDelimiter,
    public val tokens: TokenStream,
) {
    public fun deepCopy(): Macro =
        Macro(path.copy(), delimiter, tokens)
}

public sealed class MacroDelimiter {
    public data class Paren(val token: io.github.kotlinmania.syn.token.Paren) : MacroDelimiter()
    public data class Brace(val token: io.github.kotlinmania.syn.token.Brace) : MacroDelimiter()
    public data class Bracket(val token: io.github.kotlinmania.syn.token.Bracket) : MacroDelimiter()
}
