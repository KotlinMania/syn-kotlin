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
    public companion object : Parse<SynType> {
        override fun parse(input: ParseStream): SynResult<SynType> = parseTypeFull(input)

        public fun withoutPlus(input: ParseStream): SynResult<SynType> = parseTypeWithoutPlus(input)
    }

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
        val lifetimes: BoundLifetimes?,
        val unsafety: io.github.kotlinmania.syn.token.Unsafe?,
        val abi: Abi?,
        val fnToken: io.github.kotlinmania.syn.token.Fn,
        val parenToken: io.github.kotlinmania.syn.token.Paren,
        val inputs: BareFnArgList,
        val variadic: BareVariadic?,
        val output: ReturnType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            lifetimes?.toTokens(tokens)
            unsafety?.toTokens(tokens)
            abi?.toTokens(tokens)
            fnToken.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                inputs.toTokens(inner)
                if (variadic != null) {
                    if (!inputs.emptyOrTrailing()) {
                        io.github.kotlinmania.syn.token.Comma
                            .from(variadic.dots.spans.first())
                            .toTokens(inner)
                    }
                    variadic.toTokens(inner)
                }
            }
            output.toTokens(tokens)
        }

        override fun deepCopy(): BareFn =
            BareFn(
                lifetimes?.deepCopy(),
                unsafety,
                abi,
                fnToken,
                parenToken,
                inputs.copy({ it.deepCopy() }, { it }),
                variadic?.deepCopy(),
                output.deepCopy(),
            )
    }

    public data class Group(
        val groupToken: io.github.kotlinmania.syn.token.Group,
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            groupToken.surround(tokens) { inner -> elem.toTokens(inner) }
        }

        override fun deepCopy(): Group = Group(groupToken, elem.deepCopy())
    }

    public data class ImplTrait(
        val implToken: io.github.kotlinmania.syn.token.Impl,
        val bounds: TypeParamBoundList,
    ) : SynType() {
        public companion object : Parse<ImplTrait> {
            override fun parse(input: ParseStream): SynResult<ImplTrait> =
                parseTypeImplTrait(input, allowPlus = true)

            public fun withoutPlus(input: ParseStream): SynResult<ImplTrait> =
                parseTypeImplTrait(input, allowPlus = false)
        }

        override fun toTokens(tokens: TokenStream) {
            implToken.toTokens(tokens)
            bounds.toTokens(tokens)
        }

        override fun deepCopy(): ImplTrait = ImplTrait(implToken, bounds.copy({ it.deepCopy() }, { it }))
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
        val starToken: io.github.kotlinmania.syn.token.Star,
        val constToken: io.github.kotlinmania.syn.token.Const?,
        val mutability: io.github.kotlinmania.syn.token.Mut?,
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            starToken.toTokens(tokens)
            if (mutability != null) mutability.toTokens(tokens) else constToken?.toTokens(tokens)
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Ptr = Ptr(starToken, constToken, mutability, elem.deepCopy())
    }

    public data class Reference(
        val andToken: io.github.kotlinmania.syn.token.And,
        val lifetime: Lifetime?,
        val mutability: io.github.kotlinmania.syn.token.Mut?,
        val elem: SynType,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            andToken.toTokens(tokens)
            lifetime?.toTokens(tokens)
            mutability?.toTokens(tokens)
            elem.toTokens(tokens)
        }

        override fun deepCopy(): Reference = Reference(andToken, lifetime?.deepCopy(), mutability, elem.deepCopy())
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
        val dynToken: io.github.kotlinmania.syn.token.Dyn?,
        val bounds: TypeParamBoundList,
    ) : SynType() {
        public companion object : Parse<TraitObject> {
            override fun parse(input: ParseStream): SynResult<TraitObject> =
                parseTypeTraitObject(input, allowPlus = true)

            public fun withoutPlus(input: ParseStream): SynResult<TraitObject> =
                parseTypeTraitObject(input, allowPlus = false)
        }

        override fun toTokens(tokens: TokenStream) {
            dynToken?.toTokens(tokens)
            bounds.toTokens(tokens)
        }

        override fun deepCopy(): TraitObject = TraitObject(dynToken, bounds.copy({ it.deepCopy() }, { it }))
    }

    public data class Tuple(
        val parenToken: io.github.kotlinmania.syn.token.Paren,
        val elems: SynTypeList,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }

        override fun deepCopy(): Tuple = Tuple(parenToken, elems.copy({ it.deepCopy() }, { it }))
    }

    public data class Verbatim(
        val tokens: TokenStream,
    ) : SynType() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }

        override fun deepCopy(): Verbatim = this
    }

    public abstract fun deepCopy(): SynType
}

public data class BareFnArg(
    public val attrs: List<Attribute>,
    public val name: IdentColon?,
    public val ty: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        name?.ident?.toTokens(tokens)
        name?.colonToken?.toTokens(tokens)
        ty.toTokens(tokens)
    }

    public fun deepCopy(): BareFnArg =
        BareFnArg(attrs.map { it.deepCopy() }, name, ty.deepCopy())
}

/** The variadic argument of a function pointer. */
public data class BareVariadic(
    public val attrs: List<Attribute>,
    public val name: IdentColon?,
    public val dots: io.github.kotlinmania.syn.token.DotDotDot,
    public val comma: io.github.kotlinmania.syn.token.Comma?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        name?.ident?.toTokens(tokens)
        name?.colonToken?.toTokens(tokens)
        dots.toTokens(tokens)
        comma?.toTokens(tokens)
    }

    public fun deepCopy(): BareVariadic = BareVariadic(attrs.map { it.deepCopy() }, name, dots, comma)
}

public sealed class ReturnType : ToTokens {
    public companion object : Parse<ReturnType> {
        override fun parse(input: ParseStream): SynResult<ReturnType> = parseReturnType(input)

        public fun withoutPlus(input: ParseStream): SynResult<ReturnType> =
            parseReturnTypeWithoutPlus(input)
    }

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
