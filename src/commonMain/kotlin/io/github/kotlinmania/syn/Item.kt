// port-lint: source item.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Default
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.For
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.PathSep
import io.github.kotlinmania.syn.token.Semi
import io.github.kotlinmania.syn.token.SynTypeToken
import io.github.kotlinmania.syn.token.Unsafe

/**
 * Things that can appear directly inside of a module or scope.
 */
public sealed class Item : ToTokens {
    public companion object {
        public fun from(input: DeriveInput): Item =
            when (val data = input.data) {
                is Data.Struct ->
                    Struct(
                        input.attrs,
                        input.vis,
                        data.value.structToken,
                        input.ident,
                        input.generics,
                        data.value.fields,
                        data.value.semiToken,
                    )
                is Data.Enum ->
                    Enum(
                        input.attrs,
                        input.vis,
                        data.value.enumToken,
                        input.ident,
                        input.generics,
                        data.value.braceToken,
                        data.value.variants,
                    )
                is Data.Union ->
                    Union(
                        input.attrs,
                        input.vis,
                        data.value.unionToken,
                        input.ident,
                        input.generics,
                        data.value.fields,
                    )
            }
    }

    internal data class AttrReplacement(
        var item: Item,
        var oldAttrs: List<Attribute>,
    )

    internal fun replaceAttrs(new: List<Attribute>): AttrReplacement =
        when (this) {
            is Const -> AttrReplacement(copy(attrs = new), attrs)
            is Enum -> AttrReplacement(copy(attrs = new), attrs)
            is ExternCrate -> AttrReplacement(copy(attrs = new), attrs)
            is Fn -> AttrReplacement(copy(attrs = new), attrs)
            is ForeignMod -> AttrReplacement(copy(attrs = new), attrs)
            is Impl -> AttrReplacement(copy(attrs = new), attrs)
            is ItemType -> AttrReplacement(copy(attrs = new), attrs)
            is Macro -> AttrReplacement(copy(attrs = new), attrs)
            is Mod -> AttrReplacement(copy(attrs = new), attrs)
            is Static -> AttrReplacement(copy(attrs = new), attrs)
            is Struct -> AttrReplacement(copy(attrs = new), attrs)
            is Trait -> AttrReplacement(copy(attrs = new), attrs)
            is TraitAlias -> AttrReplacement(copy(attrs = new), attrs)
            is Union -> AttrReplacement(copy(attrs = new), attrs)
            is Use -> AttrReplacement(copy(attrs = new), attrs)
            is Verbatim -> AttrReplacement(this, mutableListOf())
        }

    /** A constant item: `const MAX: UShort = 65535`. */
    public data class Const(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var colonToken: Colon?,
        public var ty: SynType,
        public var eqToken: Eq?,
        public var expr: Expr?,
        public var semiToken: Semi,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            constToken.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken?.toTokens(tokens)
            ty.toTokens(tokens)
            eqToken?.toTokens(tokens)
            expr?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** An enum definition. */
    public data class Enum(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var enumToken: io.github.kotlinmania.syn.token.Enum,
        public var ident: Ident,
        public var generics: Generics,
        public var braceToken: Brace,
        public var variants: VariantList,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            enumToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                variants.toTokens(inner)
            }
        }
    }

    /** An `extern crate` item. */
    public data class ExternCrate(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var externToken: io.github.kotlinmania.syn.token.Extern,
        public var crateToken: io.github.kotlinmania.syn.token.Crate,
        public var ident: Ident,
        public var rename: AsIdent?,
        public var semiToken: Semi,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            externToken.toTokens(tokens)
            crateToken.toTokens(tokens)
            ident.toTokens(tokens)
            rename?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A free-standing function. */
    public data class Fn(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var sig: Signature,
        public var block: Block?,
    ) : Item() {
        public constructor(
            attrs: List<Attribute>,
            vis: Visibility,
            fnToken: io.github.kotlinmania.syn.token.Fn,
            ident: Ident,
            generics: Generics,
            parenToken: Paren,
            inputs: FnArgList,
            output: ReturnType?,
            block: Block?,
        ) : this(
            attrs,
            vis,
            Signature(null, null, null, null, fnToken, ident, generics, parenToken, inputs, null, output ?: ReturnType.Default),
            block,
        )

        public val fnToken: io.github.kotlinmania.syn.token.Fn
            get() = sig.fnToken

        public val ident: Ident
            get() = sig.ident

        public val generics: Generics
            get() = sig.generics

        public val parenToken: Paren
            get() = sig.parenToken

        public val inputs: FnArgList
            get() = sig.inputs

        public val output: ReturnType
            get() = sig.output

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            sig.toTokens(tokens)
            block?.toTokens(tokens)
        }
    }

    /** A block of foreign items. */
    public data class ForeignMod(
        public var attrs: List<Attribute>,
        public var unsafety: Unsafe?,
        public var abi: Abi,
        public var braceToken: Brace,
        public var items: List<ForeignItem>,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            unsafety?.toTokens(tokens)
            abi.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (item in items) item.toTokens(inner)
            }
        }
    }

    /** A data class definition. */
    public data class Struct(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var structToken: io.github.kotlinmania.syn.token.Struct,
        public var ident: Ident,
        public var generics: Generics,
        public var fields: Fields,
        public var semiToken: Semi?,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            structToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            fields.toTokens(tokens)
            semiToken?.toTokens(tokens)
        }
    }

    /** A union definition. */
    public data class Union(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var unionToken: io.github.kotlinmania.syn.token.Union,
        public var ident: Ident,
        public var generics: Generics,
        public var fields: FieldsNamed,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            unionToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            fields.toTokens(tokens)
        }
    }

    /** A module or module declaration. */
    public data class Mod(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var unsafety: Unsafe?,
        public var modToken: io.github.kotlinmania.syn.token.Mod,
        public var ident: Ident,
        public var content: ModContent?,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            unsafety?.toTokens(tokens)
            modToken.toTokens(tokens)
            ident.toTokens(tokens)
            content?.toTokens(tokens)
        }
    }

    /** A use declaration. */
    public data class Use(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var useToken: io.github.kotlinmania.syn.token.Use,
        public var leadingColon: PathSep?,
        public var tree: UseTree,
        public var semiToken: Semi,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            useToken.toTokens(tokens)
            leadingColon?.toTokens(tokens)
            tree.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A static item. */
    public data class Static(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var staticToken: io.github.kotlinmania.syn.token.Static,
        public var mutability: StaticMutability,
        public var ident: Ident,
        public var colonToken: Colon,
        public var ty: SynType,
        public var eqToken: Eq,
        public var expr: Expr,
        public var semiToken: Semi,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            staticToken.toTokens(tokens)
            mutability.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            eqToken.toTokens(tokens)
            expr.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A trait definition. */
    public data class Trait(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var unsafety: Unsafe?,
        public var autoToken: io.github.kotlinmania.syn.token.Auto?,
        public var restriction: ImplRestriction?,
        public var traitToken: io.github.kotlinmania.syn.token.Trait,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon?,
        public var supertraits: TypeParamBoundList,
        public var braceToken: Brace,
        public var items: List<TraitItem>,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            unsafety?.toTokens(tokens)
            autoToken?.toTokens(tokens)
            restriction?.toTokens(tokens)
            traitToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            if (!supertraits.isEmpty()) {
                colonToken?.toTokens(tokens)
                supertraits.toTokens(tokens)
            }
            generics.whereClause?.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (item in items) item.toTokens(inner)
            }
        }
    }

    /** A trait alias. */
    public data class TraitAlias(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var traitToken: io.github.kotlinmania.syn.token.Trait,
        public var ident: Ident,
        public var generics: Generics,
        public var eqToken: Eq,
        public var bounds: TypeParamBoundList,
        public var semiToken: Semi,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            traitToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            eqToken.toTokens(tokens)
            bounds.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A type alias. */
    public data class ItemType(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var typeToken: SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var eqToken: Eq,
        public var ty: SynType,
        public var semiToken: Semi,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            typeToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            eqToken.toTokens(tokens)
            ty.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** An impl block providing trait or associated items. */
    public data class Impl(
        public var attrs: List<Attribute>,
        public var defaultness: Default?,
        public var unsafety: Unsafe?,
        public var implToken: io.github.kotlinmania.syn.token.Impl,
        public var generics: Generics,
        public var traitPath: PathTrait?,
        public var selfType: SynType,
        public var braceToken: Brace,
        public var items: List<ImplItem>,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            defaultness?.toTokens(tokens)
            unsafety?.toTokens(tokens)
            implToken.toTokens(tokens)
            generics.toTokens(tokens)
            traitPath?.let { (polarity, path, forToken) ->
                polarity?.toTokens(tokens)
                path.toTokens(tokens)
                forToken.toTokens(tokens)
            }
            selfType.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (item in items) item.toTokens(inner)
            }
        }
    }

    /** A macro invocation, including declarative macro definitions. */
    public data class Macro(
        public var attrs: List<Attribute>,
        public var ident: Ident?,
        public var mac: io.github.kotlinmania.syn.Macro,
        public var semiToken: Semi?,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.path.toTokens(tokens)
            mac.bangToken.toTokens(tokens)
            ident?.toTokens(tokens)
            mac.delimiter.surround(tokens, mac.tokens)
            semiToken?.toTokens(tokens)
        }
    }

    /** Tokens forming an item not interpreted by Syn. */
    public data class Verbatim(
        public var tokens: TokenStream,
    ) : Item() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }
    }
}

public fun from(input: DeriveInput): Item =
    Item.from(input)

public fun from(input: Item.Struct): DeriveInput =
    DeriveInput(
        input.attrs,
        input.vis,
        input.ident,
        input.generics,
        Data.Struct(DataStruct(input.structToken, input.fields, input.semiToken)),
    )

public fun from(input: Item.Enum): DeriveInput =
    DeriveInput(
        input.attrs,
        input.vis,
        input.ident,
        input.generics,
        Data.Enum(DataEnum(input.enumToken, input.braceToken, input.variants)),
    )

public fun from(input: Item.Union): DeriveInput =
    DeriveInput(
        input.attrs,
        input.vis,
        input.ident,
        input.generics,
        Data.Union(DataUnion(input.unionToken, input.fields)),
    )

/** An argument in a function signature. */
public sealed class FnArg : ToTokens {
    /** The receiver argument of an associated method. */
    public data class Receiver(
        public var attrs: List<Attribute>,
        public var reference: AndLifetime?,
        public var mutability: io.github.kotlinmania.syn.token.Mut?,
        public var selfToken: io.github.kotlinmania.syn.token.SelfValue,
        public var colonToken: Colon?,
        public var type: SynType,
    ) : FnArg() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            reference?.toTokens(tokens)
            mutability?.toTokens(tokens)
            selfToken.toTokens(tokens)
            if (colonToken != null) {
                colonToken.toTokens(tokens)
                type.toTokens(tokens)
            }
        }

        public fun lifetime(): Lifetime? = reference?.lifetime
    }

    /** A function argument accepted by pattern and type. */
    public data class Typed(
        public var patType: PatType,
    ) : FnArg() {
        override fun toTokens(tokens: TokenStream) {
            patType.toTokens(tokens)
        }
    }
}

/** Module content: either an inline block or just a semicolon. */
public sealed class ModContent : ToTokens {
    public data class Unnamed(
        var semiToken: Semi,
    ) : ModContent() {
        override fun toTokens(tokens: TokenStream) {
            semiToken.toTokens(tokens)
        }
    }

    public data class Inline(
        var braceToken: Brace,
        var items: List<Item>,
    ) : ModContent() {
        override fun toTokens(tokens: TokenStream) {
            braceToken.surround(tokens) { inner ->
                for (item in items) item.toTokens(inner)
            }
        }
    }
}

/** A use tree in a use declaration. */
public sealed class UseTree : ToTokens {
    public data class Path(
        var ident: Ident,
        var colon2Token: io.github.kotlinmania.syn.token.PathSep?,
        var tree: UseTree?,
    ) : UseTree() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
            colon2Token?.toTokens(tokens)
            tree?.toTokens(tokens)
        }
    }

    public data class Name(
        var ident: Ident,
        var rename: AsIdent?,
    ) : UseTree() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
            rename?.toTokens(tokens)
        }
    }

    public data class Group(
        var braceToken: Brace,
        var items: UseTreeList,
    ) : UseTree() {
        override fun toTokens(tokens: TokenStream) {
            braceToken.surround(tokens) { inner ->
                items.toTokens(inner)
            }
        }
    }

    public data class Glob(
        var starToken: io.github.kotlinmania.syn.token.Star,
    ) : UseTree() {
        override fun toTokens(tokens: TokenStream) {
            starToken.toTokens(tokens)
        }
    }
}

/** The trait path in an impl block. */
public data class PathTrait(
    public var polarity: io.github.kotlinmania.syn.token.Not?,
    public var path: Path,
    public var forToken: For,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        polarity?.toTokens(tokens)
        path.toTokens(tokens)
        forToken.toTokens(tokens)
    }
}

/** A function signature in a trait or implementation. */
public data class Signature(
    public var constness: io.github.kotlinmania.syn.token.Const?,
    public var asyncness: io.github.kotlinmania.syn.token.Async?,
    public var unsafety: Unsafe?,
    public var abi: Abi?,
    public var fnToken: io.github.kotlinmania.syn.token.Fn,
    public var ident: Ident,
    public var generics: Generics,
    public var parenToken: Paren,
    public var inputs: FnArgList,
    public var variadic: Variadic?,
    public var output: ReturnType,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        constness?.toTokens(tokens)
        asyncness?.toTokens(tokens)
        unsafety?.toTokens(tokens)
        abi?.toTokens(tokens)
        fnToken.toTokens(tokens)
        ident.toTokens(tokens)
        generics.toTokens(tokens)
        parenToken.surround(tokens) { inner ->
            inputs.toTokens(inner)
            if (variadic != null) {
                if (!inputs.isEmpty() && !inputs.trailingPunct()) {
                    io.github.kotlinmania.syn.token.Comma
                        .default()
                        .toTokens(inner)
                }
                variadic.toTokens(inner)
            }
        }
        output.toTokens(tokens)
        generics.whereClause?.toTokens(tokens)
    }

    /** A method's receiver, such as a reference receiver or an explicit receiver type. */
    public fun receiver(): FnArg.Receiver? {
        var first = inputs.first() ?: return null
        return first as? FnArg.Receiver
    }
}

/** The ABI name in a function signature. */
public data class Abi(
    public var externToken: io.github.kotlinmania.syn.token.Extern,
    public var name: LitStr?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        externToken.toTokens(tokens)
        name?.toTokens(tokens)
    }
}

/** The variadic argument of a foreign function. */
public data class Variadic(
    public var attrs: List<Attribute>,
    public var pat: PatColon?,
    public var dots: io.github.kotlinmania.syn.token.DotDotDot,
    public var comma: Comma?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        pat?.toTokens(tokens)
        dots.toTokens(tokens)
        comma?.toTokens(tokens)
    }
}

/** Mutability of a static item. */
public sealed class StaticMutability : ToTokens {
    public data class Mut(
        public var mutToken: io.github.kotlinmania.syn.token.Mut,
    ) : StaticMutability() {
        override fun toTokens(tokens: TokenStream) {
            mutToken.toTokens(tokens)
        }
    }

    public data object None : StaticMutability() {
        override fun toTokens(tokens: TokenStream) {
        }
    }
}

/** Reserved for implementation restrictions. */
public sealed class ImplRestriction : ToTokens

/** An item within an extern block. */
public sealed class ForeignItem : ToTokens {
    /** A foreign function in an extern block. */
    public data class Fn(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var sig: Signature,
        public var semiToken: Semi,
    ) : ForeignItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            sig.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A foreign static item in an extern block. */
    public data class Static(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var staticToken: io.github.kotlinmania.syn.token.Static,
        public var mutability: StaticMutability,
        public var ident: Ident,
        public var colonToken: Colon,
        public var ty: SynType,
        public var semiToken: Semi,
    ) : ForeignItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            staticToken.toTokens(tokens)
            mutability.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A foreign type in an extern block. */
    public data class ItemType(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var typeToken: SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var semiToken: Semi,
    ) : ForeignItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            typeToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A macro invocation within an extern block. */
    public data class Macro(
        public var attrs: List<Attribute>,
        public var mac: io.github.kotlinmania.syn.Macro,
        public var semiToken: Semi?,
    ) : ForeignItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
            semiToken?.toTokens(tokens)
        }
    }

    /** Tokens within an extern block not interpreted by Syn. */
    public data class Verbatim(
        public var tokens: TokenStream,
    ) : ForeignItem() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }
    }
}

/** An item within a trait definition. */
public sealed class TraitItem : ToTokens {
    /** An associated constant within the definition of a trait. */
    public data class Const(
        public var attrs: List<Attribute>,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon,
        public var ty: SynType,
        public var default: EqExpr?,
        public var semiToken: Semi,
    ) : TraitItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            default?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** An associated function within the definition of a trait. */
    public data class Fn(
        public var attrs: List<Attribute>,
        public var sig: Signature,
        public var default: Block?,
        public var semiToken: Semi?,
    ) : TraitItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            sig.toTokens(tokens)
            val default = this.default
            if (default != null) {
                default.braceToken.surround(tokens) { inner ->
                    for (stmt in default.stmts) stmt.toTokens(inner)
                }
            } else {
                semiToken?.toTokens(tokens)
            }
        }
    }

    /** An associated type within the definition of a trait. */
    public data class AssocType(
        public var attrs: List<Attribute>,
        public var typeToken: io.github.kotlinmania.syn.token.SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon?,
        public var bounds: TypeParamBoundList,
        public var default: EqSynType?,
        public var semiToken: Semi,
    ) : TraitItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            typeToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            if (!bounds.isEmpty()) {
                colonToken?.toTokens(tokens)
                bounds.toTokens(tokens)
            }
            default?.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A macro invocation within the definition of a trait. */
    public data class Macro(
        public var attrs: List<Attribute>,
        public var mac: io.github.kotlinmania.syn.Macro,
        public var semiToken: Semi?,
    ) : TraitItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
            semiToken?.toTokens(tokens)
        }
    }

    /** Tokens within the definition of a trait not interpreted by Syn. */
    public data class Verbatim(
        public var tokens: TokenStream,
    ) : TraitItem() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }
    }
}

/** An item within an impl block. */
public sealed class ImplItem : ToTokens {
    /** An associated constant within an impl block. */
    public data class Const(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var defaultness: Default?,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon,
        public var ty: SynType,
        public var eqToken: Eq,
        public var expr: Expr,
        public var semiToken: Semi,
    ) : ImplItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            defaultness?.toTokens(tokens)
            constToken.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            eqToken.toTokens(tokens)
            expr.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** An associated function within an impl block. */
    public data class Fn(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var defaultness: Default?,
        public var sig: Signature,
        public var block: Block,
    ) : ImplItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            defaultness?.toTokens(tokens)
            sig.toTokens(tokens)
            block.braceToken.surround(tokens) { inner ->
                for (stmt in block.stmts) stmt.toTokens(inner)
            }
        }
    }

    /** An associated type within an impl block. */
    public data class AssocType(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var defaultness: Default?,
        public var typeToken: io.github.kotlinmania.syn.token.SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var eqToken: Eq,
        public var ty: SynType,
        public var semiToken: Semi,
    ) : ImplItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            vis.toTokens(tokens)
            defaultness?.toTokens(tokens)
            typeToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            eqToken.toTokens(tokens)
            ty.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A macro invocation within an impl block. */
    public data class Macro(
        public var attrs: List<Attribute>,
        public var mac: io.github.kotlinmania.syn.Macro,
        public var semiToken: Semi?,
    ) : ImplItem() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
            semiToken?.toTokens(tokens)
        }
    }

    /** Tokens within an impl block not interpreted by Syn. */
    public data class Verbatim(
        public var tokens: TokenStream,
    ) : ImplItem() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }
    }
}
