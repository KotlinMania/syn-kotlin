// port-lint: source ty.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.RArrow

/**
 * A type syntax tree node.
 *
 * Named `SynType` to avoid colliding with Swift's built-in `Type`
 * metatype expression (`foo.Type`), which the Swift compiler rejects
 * as `error: type member must not be named Type`. The `Type` class
 * preserves the original Kotlin API name.
 */
public sealed class SynType : ToTokens {
    public data class Array(
        val elem: SynType,
        val len: Expr,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            elem.toTokens(tokens)
            len.toTokens(tokens)
        }

        override fun deepCopy(): Array = Array(elem.deepCopy(), len.deepCopy())
    }

    public data class BareFn(
        val inputs: Punctuated<BareFnArg, io.github.kotlinmania.syn.token.Comma>,
        val output: ReturnType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            for ((input, comma) in inputs.pairs()) {
                input.toTokens(tokens)
                comma?.toTokens(tokens)
            }
            output.toTokens(tokens)
        }

        override fun deepCopy(): BareFn = BareFn(inputs.copy({ it.deepCopy() }, { it }), output.deepCopy())
    }

    public data class Group(
        val groupToken: io.github.kotlinmania.syn.token.Group,
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Group = Group(groupToken, elem.deepCopy())
    }

    public data class ImplTrait(
        val bounds: Punctuated<TypeParamBound, io.github.kotlinmania.syn.token.Plus>,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            for ((bound, plus) in bounds.pairs()) {
                bound.toTokens(tokens)
                plus?.toTokens(tokens)
            }
        }

        override fun deepCopy(): ImplTrait = ImplTrait(bounds.copy({ it.deepCopy() }, { it }))
    }

    public data class Infer(
        val underscoreToken: io.github.kotlinmania.syn.token.Underscore,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            underscoreToken.toTokens(tokens)
        }

        override fun deepCopy(): Infer = this
    }

    public data class Macro(
        val mac: io.github.kotlinmania.syn.Macro,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            mac.toTokens(tokens)
        }

        override fun deepCopy(): Macro = Macro(mac.deepCopy())
    }

    public data class Never(
        val bangToken: io.github.kotlinmania.syn.token.Not,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            bangToken.toTokens(tokens)
        }

        override fun deepCopy(): Never = this
    }

    public data class Paren(
        val parenToken: io.github.kotlinmania.syn.token.Paren,
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            parenToken.surround(tokens) { inner ->
                elem.toTokens(inner)
            }
        }

        override fun deepCopy(): Paren = Paren(parenToken, elem.deepCopy())
    }

    public data class Path(
        val qself: QSelf?,
        val path: io.github.kotlinmania.syn.Path,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
        }

        override fun deepCopy(): Path = Path(qself, path.deepCopy())
    }

    public data class Ptr(
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Ptr = Ptr(elem.deepCopy())
    }

    public data class Reference(
        val lifetime: Lifetime?,
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            lifetime?.toTokens(tokens)
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Reference = Reference(lifetime?.deepCopy(), elem.deepCopy())
    }

    public data class Slice(
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Slice = Slice(elem.deepCopy())
    }

    public data class TraitObject(
        val bounds: Punctuated<TypeParamBound, io.github.kotlinmania.syn.token.Plus>,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            for ((bound, plus) in bounds.pairs()) {
                bound.toTokens(tokens)
                plus?.toTokens(tokens)
            }
        }

        override fun deepCopy(): TraitObject = TraitObject(bounds.copy({ it.deepCopy() }, { it }))
    }

    public data class Tuple(
        val parenToken: io.github.kotlinmania.syn.token.Paren,
        val elems: Punctuated<SynType, io.github.kotlinmania.syn.token.Comma>,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            parenToken.surround(tokens) { inner ->
                for ((elem, comma) in elems.pairs()) {
                    elem.toTokens(inner)
                    comma?.toTokens(inner)
                }
            }
        }

        override fun deepCopy(): Tuple = Tuple(parenToken, elems.copy({ it.deepCopy() }, { it }))
    }

    public data class Verbatim(
        val tokens: TokenStream,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(tokens))
        }

        override fun deepCopy(): Verbatim = this
    }

    public abstract fun deepCopy(): SynType
}

public data class BareFnArg(
    public val name: Ident?,
    public val ty: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        name?.toTokens(tokens)
        ty.toTokens(tokens)
    }

    public fun deepCopy(): BareFnArg =
        BareFnArg(name?.copy(), ty.deepCopy())
}

public sealed class ReturnType : ToTokens {
    public data object Default : ReturnType() {
        override fun toTokens(tokens: TokenStream) {
            // default return type emits nothing
        }

        override fun deepCopy(): Default = this
    }

    public data class TypeReturn(
        val arrowToken: RArrow,
        val ty: SynType,
    ) : ReturnType() {
        override fun toTokens(tokens: TokenStream) {
            arrowToken.toTokens(tokens)
            ty.toTokens(tokens)
        }

        override fun deepCopy(): TypeReturn = TypeReturn(arrowToken, ty.deepCopy())
    }

    public abstract fun deepCopy(): ReturnType
}

public sealed class MacroDelimiter : ToTokens {
    public data class Paren(
        val token: io.github.kotlinmania.syn.token.Paren,
    ) : MacroDelimiter() {
        override fun toTokens(tokens: TokenStream) {
            token.surround(tokens) { }
        }
    }

    public data class Brace(
        val token: io.github.kotlinmania.syn.token.Brace,
    ) : MacroDelimiter() {
        override fun toTokens(tokens: TokenStream) {
            token.surround(tokens) { }
        }
    }

    public data class Bracket(
        val token: io.github.kotlinmania.syn.token.Bracket,
    ) : MacroDelimiter() {
        override fun toTokens(tokens: TokenStream) {
            token.surround(tokens) { }
        }
    }
}
