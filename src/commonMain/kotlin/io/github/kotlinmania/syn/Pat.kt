// port-lint: source pat.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Underscore

/**
 * A pattern in a local binding, function signature, pattern-matching expression, or
 * various other places.
 */
public sealed class Pat : ToTokens {
    public abstract fun deepCopy(): Pat

    /** A pattern that binds a new variable, optionally with a reference, mutability, and sub-pattern. */
    public data class Ident(
        public val attrs: List<Attribute>,
        public val byRef: io.github.kotlinmania.syn.token.Ref?,
        public val mutability: FieldMutability,
        public val ident: io.github.kotlinmania.procmacro2.Ident,
        public val atToken: io.github.kotlinmania.syn.token.At?,
        public val subpat: Pat?,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() })

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            byRef?.toTokens(tokens)
            mutability.toTokens(tokens)
            ident.toTokens(tokens)
            atToken?.toTokens(tokens)
            subpat?.toTokens(tokens)
        }
    }

    /** A tuple pattern: `(A, B, C)`. */
    public data class Tuple(
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val elems: PatList,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(elems = elems.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }
    }

    /** A pattern that matches any one of a set of cases. */
    public data class Or(
        public val leadingVert: io.github.kotlinmania.syn.token.Or?,
        public val cases: PatList,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(cases = cases.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            leadingVert?.toTokens(tokens)
            for ((case, vert) in cases.pairsList()) {
                case.toTokens(tokens)
                vert?.toTokens(tokens)
            }
        }
    }

    /** A parenthesized pattern: `(A | B)`. */
    public data class PatParen(
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val pat: Pat,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(pat = pat.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            parenToken.surround(tokens) { inner -> pat.toTokens(inner) }
        }
    }

    /** A mutable reference pattern. */
    public data class Reference(
        public val andToken: io.github.kotlinmania.syn.token.And,
        public val mutability: FieldMutability,
        public val pat: Pat,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(andToken = andToken, mutability = mutability, pat = pat.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            andToken.toTokens(tokens)
            mutability.toTokens(tokens)
            pat.toTokens(tokens)
        }
    }

    /** A data-object pattern. */
    public data class Struct(
        public val qself: QSelf?,
        public val path: io.github.kotlinmania.syn.Path,
        public val braceToken: io.github.kotlinmania.syn.token.Brace,
        public val fields: FieldPatList,
        public val rest: PatRest?,
        public val dot2Token: io.github.kotlinmania.syn.token.DotDot?,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(qself = qself, path = path.deepCopy(), fields = fields.copy({ it.deepCopy() }, { it }), rest = rest?.deepCopy(), dot2Token = dot2Token)

        override fun toTokens(tokens: TokenStream) {
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                fields.toTokens(inner)
                rest?.toTokens(inner)
                dot2Token?.toTokens(inner)
            }
        }
    }

    /** A slice pattern: `[a, b.., c]`. */
    public data class Slice(
        public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public val elems: PatList,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(elems = elems.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            bracketToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }
    }

    /** A type ascription pattern. */
    public data class TypeAscription(
        public val attrs: List<Attribute>,
        public val pat: Pat,
        public val colonToken: Colon,
        public val ty: SynType,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() }, pat = pat.deepCopy(), ty = ty.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            pat.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
        }
    }

    /** A const block pattern: `const { ... }`. */
    public data class Const(
        public val attrs: List<Attribute>,
        public val constToken: io.github.kotlinmania.syn.token.Const,
        public val block: Block,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() }, block = block.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constToken.toTokens(tokens)
            block.toTokens(tokens)
        }
    }

    /** A literal pattern: `0`. */
    public data class Lit(
        public val attrs: List<Attribute>,
        public val lit: io.github.kotlinmania.syn.Lit,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() }, lit = lit)

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            lit.toTokens(tokens)
        }
    }

    /** A macro invocation in pattern position. */
    public data class Macro(
        public val attrs: List<Attribute>,
        public val mac: io.github.kotlinmania.syn.Macro,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() }, mac = mac.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
        }
    }

    /** A path pattern like `Color::Red`, optionally qualified with a self-type. */
    public data class Path(
        public val attrs: List<Attribute>,
        public val qself: QSelf?,
        public val path: io.github.kotlinmania.syn.Path,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() }, qself = qself, path = path.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
        }
    }

    /** A range pattern: `1..=2`. */
    public data class Range(
        public val attrs: List<Attribute>,
        public val start: Expr?,
        public val limits: RangeLimits,
        public val end: Expr?,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() }, start = start?.deepCopy(), end = end?.deepCopy())

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            start?.toTokens(tokens)
            limits.toTokens(tokens)
            end?.toTokens(tokens)
        }
    }

    /** The dots in a tuple or slice pattern: `[0, 1, ..]`. */
    public data class Rest(
        public val attrs: List<Attribute>,
        public val dot2Token: io.github.kotlinmania.syn.token.DotDot,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() })

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            dot2Token.toTokens(tokens)
        }
    }

    /** A tuple struct or tuple variant pattern: `Variant(x, y, .., z)`. */
    public data class TupleStruct(
        public val attrs: List<Attribute>,
        public val qself: QSelf?,
        public val path: io.github.kotlinmania.syn.Path,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val elems: PatList,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() }, qself = qself, path = path.deepCopy(), elems = elems.copy({ it.deepCopy() }, { it }))

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
            parenToken.surround(tokens) { inner -> elems.toTokens(inner) }
        }
    }

    /** A pattern that matches any value. */
    public data class Wild(
        public val attrs: List<Attribute>,
        public val underscoreToken: Underscore,
    ) : Pat() {
        override fun deepCopy(): Pat = copy(attrs = attrs.map { it.deepCopy() })

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            underscoreToken.toTokens(tokens)
        }
    }

    /** Tokens forming a pattern not interpreted by Syn. */
    public data class Verbatim(
        public val tokens: TokenStream,
    ) : Pat() {
        override fun deepCopy(): Pat = this

        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }
    }
}

/** A field in a data-object pattern. */
public data class FieldPat(
    public val member: Member,
    public val colonToken: Colon?,
    public val pat: Pat,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        member.toTokens(tokens)
        colonToken?.toTokens(tokens)
        pat.toTokens(tokens)
    }

    public fun deepCopy(): FieldPat = FieldPat(member, colonToken, pat.deepCopy())
}

/** The rest pattern in a data-object pattern. */
public data class PatRest(
    public val dot2Token: io.github.kotlinmania.syn.token.DotDot?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        dot2Token?.toTokens(tokens)
    }

    public fun deepCopy(): PatRest = this
}

/** A type ascription pattern. */
public data class PatType(
    public val attrs: List<Attribute>,
    public val pat: Pat,
    public val colonToken: Colon,
    public val ty: SynType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        pat.toTokens(tokens)
        colonToken.toTokens(tokens)
        ty.toTokens(tokens)
    }

    public fun deepCopy(): PatType = PatType(attrs.map { it.deepCopy() }, pat.deepCopy(), colonToken, ty.deepCopy())
}
