// port-lint: source item.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
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
import io.github.kotlinmania.syn.token.Underscore
import io.github.kotlinmania.syn.token.Unsafe

/**
 * Declarations and definitions that can appear directly inside a module or scope.
 */
public sealed class Item : ToTokens {
    public companion object {
        public fun parse(input: ParseStream): SynResult<Item> = ItemParse.parse(input)

        public fun from(input: DeriveInput): Item =
            when (val data = input.data) {
                is Data.Struct -> {
                    Struct(
                        input.attrs,
                        input.vis,
                        data.value.structToken,
                        input.ident,
                        input.generics,
                        data.value.fields,
                        data.value.semiToken,
                    )
                }

                is Data.Enum -> {
                    Enum(
                        input.attrs,
                        input.vis,
                        data.value.enumToken,
                        input.ident,
                        input.generics,
                        data.value.braceToken,
                        data.value.variants,
                    )
                }

                is Data.Union -> {
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
            generics.whereClause?.toTokens(tokens)
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
            when (val fields = fields) {
                is Fields.Named -> {
                    generics.whereClause?.toTokens(tokens)
                    fields.toTokens(tokens)
                }

                is Fields.Unnamed -> {
                    fields.toTokens(tokens)
                    generics.whereClause?.toTokens(tokens)
                    tokensOrDefault(semiToken, Semi::default).toTokens(tokens)
                }

                Fields.Unit -> {
                    generics.whereClause?.toTokens(tokens)
                    tokensOrDefault(semiToken, Semi::default).toTokens(tokens)
                }
            }
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
            for (attr in attrs) if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            vis.toTokens(tokens)
            unsafety?.toTokens(tokens)
            modToken.toTokens(tokens)
            ident.toTokens(tokens)
            when (val body = content) {
                is ModContent.Inline ->
                    body.braceToken.surround(tokens) { inner ->
                        for (attr in attrs) if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                        for (item in body.items) item.toTokens(inner)
                    }
                else -> body?.toTokens(tokens)
            }
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

    /**
     * A trait alias: `public interface SharableIterator = Iterator + Sync`.
     */
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
            colonToken?.let { colon ->
                colon.toTokens(tokens)
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

/**
 * A suffix of an import tree in a `use` item: `Type as Renamed` or `*`.
 *
 * # Syntax tree enum
 *
 * This type is a syntax tree enum; see [Expr].
 */
public sealed class UseTree : ToTokens {
    /**
     * A path prefix of imports in a `use` item.
     */
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

    /**
     * An identifier imported by a `use` item: `HashMap`.
     */
    public data class Name(
        var ident: Ident,
        var rename: AsIdent?,
    ) : UseTree() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
            rename?.toTokens(tokens)
        }
    }

    /**
     * A braced group of imports in a `use` item: `{A, B, C}`.
     */
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

    /**
     * A glob import in a `use` item: `*`.
     */
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
            variadic?.let { variadicArg ->
                if (!inputs.isEmpty() && !inputs.trailingPunct()) {
                    io.github.kotlinmania.syn.token.Comma
                        .default()
                        .toTokens(inner)
                }
                variadicArg.toTokens(inner)
            }
        }
        output.toTokens(tokens)
        generics.whereClause?.toTokens(tokens)
    }

    /** A method's receiver, such as a reference receiver or an explicit receiver type. */
    public fun receiver(): FnArg.Receiver? {
        val first = inputs.first() ?: return null
        return first as? FnArg.Receiver
    }

    public companion object {
        public fun parse(input: ParseStream): SynResult<Signature> = parseSignature(input)
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
    ) : StaticMutability()

    public data object None : StaticMutability()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Mut -> mutToken.toTokens(tokens)
            None -> Unit
        }
    }
}

/** Reserved for implementation restrictions. */
public sealed class ImplRestriction : ToTokens

/**
 * An item within an `extern` block.
 */
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

/**
 * An item declaration within the definition of a trait.
 *
 * # Syntax tree enum
 *
 * This type is a syntax tree enum; see [Expr].
 */
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
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            sig.toTokens(tokens)
            val default = this.default
            if (default != null) {
                default.braceToken.surround(tokens) { inner ->
                    for (attr in attrs) {
                        if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                    }
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

/**
 * An item within an implementation block.
 *
 * # Syntax tree enum
 *
 * This type is a syntax tree enum; see [Expr].
 */
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

public object ItemParse {
    public fun parse(input: ParseStream): SynResult<Item> {
        val begin = input.fork()
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        return parseRestOfItem(begin, attrs, input)
    }
}

private fun parseRestOfItem(
    begin: ParseStream,
    attrs: List<Attribute>,
    input: ParseStream,
): SynResult<Item> {
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }

    if (peekSignature(input)) {
        val sigResult = parseSignature(input)
        if (sigResult.isFailure) return asFailure(sigResult)
        return parseRestOfFn(input, attrs, vis, sigResult.getOrThrow())
    }
    if (input.peek(StructPeek)) {
        val structToken = StructParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        val data = dataStruct(input).getOrElse { return SynResult.failure(it) }
        generics.whereClause = data.whereClause
        return SynResult.success(
            Item.Struct(attrs, vis, structToken, ident, generics, data.fields, data.semiToken),
        )
    }
    if (input.peek(EnumPeek)) {
        val enumToken = EnumParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        val data = dataEnum(input).getOrElse { return SynResult.failure(it) }
        generics.whereClause = data.whereClause
        return SynResult.success(
            Item.Enum(attrs, vis, enumToken, ident, generics, data.braceToken, data.variants),
        )
    }
    if (input.peek(UnionPeek) && input.peek2(IdentPeek)) {
        val unionToken = UnionParse.parse(input).getOrThrow()
        val ident = IdentParse.parse(input).getOrThrow()
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        val data = dataUnion(input).getOrElse { return SynResult.failure(it) }
        generics.whereClause = data.whereClause
        return SynResult.success(
            Item.Union(attrs, vis, unionToken, ident, generics, data.fields),
        )
    }
    if (input.peek(ExternPeek)) {
        val ahead = input.fork()
        ExternParse.parse(ahead).getOrElse { return SynResult.failure(it) }
        if (ahead.peek(CratePeek)) {
            return parseItemExternCrate(attrs, vis, input)
        }
        if (!vis.isInherited()) {
            return SynResult.failure(input.error("expected foreign module"))
        }
        return parseItemForeignMod(attrs, input)
    }
    if (input.peek(UnsafePeek) && input.peek2(ExternPeek)) {
        if (!vis.isInherited()) {
            return SynResult.failure(input.error("expected foreign module"))
        }
        return parseItemForeignMod(attrs, input)
    }
    if (input.peek(UnsafePeek) && (input.peek2(TraitPeek) || input.peek2(AutoPeek))) {
        val unsafety = UnsafeParse.parse(input).getOrElse { return SynResult.failure(it) }
        val autoToken = AutoParse.parse(input).getOrNull()
        val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        return parseRestOfTrait(input, attrs, vis, unsafety, autoToken, traitToken, ident, generics)
            .map { it }
    }
    if (input.peek(AutoPeek) && input.peek2(TraitPeek)) {
        val autoToken = AutoParse.parse(input).getOrElse { return SynResult.failure(it) }
        val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        return parseRestOfTrait(input, attrs, vis, null, autoToken, traitToken, ident, generics)
            .map { it }
    }
    if (input.peek(TraitPeek)) {
        return parseTraitOrTraitAlias(input, attrs, vis)
    }
    val implResult = parseImpl(begin, attrs, vis, input, allowVerbatimImpl = true)
    if (implResult.isFailure) return asFailure(implResult)
    implResult.getOrThrow()?.let {
        return SynResult.success(it)
    }
    if (input.peek(StaticPeek)) {
        val staticToken = StaticParse.parse(input).getOrElse { return SynResult.failure(it) }
        val mutability = StaticMutabilityParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }

        if (input.peek(EqPeek)) {
            EqParse.parse(input).getOrElse { return SynResult.failure(it) }
            parseExprFull(input).getOrElse { return SynResult.failure(it) }
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(Item.Verbatim(between(begin, input)))
        }

        val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }

        if (input.peek(SemiPeek)) {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(Item.Verbatim(between(begin, input)))
        }

        val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
        val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
        val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(
            Item.Static(attrs, vis, staticToken, mutability, ident, colonToken, ty, eqToken, expr, semiToken),
        )
    }
    if (input.peek(ConstPeek)) {
        val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
        val lookahead = input.lookahead1()
        if (!lookahead.peek(IdentPeek) && !lookahead.peek(UnderscorePeek)) {
            return SynResult.failure(lookahead.error())
        }
        val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
        val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
        val colon = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        val value =
            if (input.peek(EqPeek)) {
                val eq = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
                EqExpr(eq, expr)
            } else {
                null
            }
        generics.whereClause =
            if (input.peek(WherePeek)) parseWhereClause(input).getOrElse { return SynResult.failure(it) } else null
        val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        return if (value != null && generics.ltToken == null && generics.whereClause == null) {
            SynResult.success(Item.Const(attrs, vis, constToken, ident, colon, ty, value.eqToken, value.expr, semi))
        } else {
            // Rust Syn keeps accepted macro-DSL syntax that has no typed constant representation.
            SynResult.success(Item.Verbatim(between(begin, input)))
        }
    }
    if (input.peek(SynTypePeek)) {
        return parseItemType(begin, attrs, vis, input)
    }
    if (input.peek(UsePeek)) {
        return parseItemUse(begin, attrs, vis, input, allowCrateRootInPath = true)
    }
    if (input.peek(ModPeek) || (input.peek(UnsafePeek) && input.peek2(ModPeek))) {
        val unsafety = UnsafeParse.parse(input).getOrNull()
        val modToken = ModParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident =
            if (input.peek(TryPeek)) {
                identParseAny(input).getOrElse { return SynResult.failure(it) }
            } else {
                IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
            }
        val lookahead = input.lookahead1()
        if (lookahead.peek(SemiPeek)) {
            val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(
                Item.Mod(attrs, vis, unsafety, modToken, ident, ModContent.Unnamed(semi)),
            )
        }
        if (!lookahead.peek(BracePeek)) return SynResult.failure(lookahead.error())
        val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
        val itemAttrs = attrs.toMutableList()
        parseInner(bracesVal.content, itemAttrs).getOrElse { return SynResult.failure(it) }
        val items = mutableListOf<Item>()
        while (!bracesVal.content.isEmpty()) {
            items.add(ItemParse.parse(bracesVal.content).getOrElse { return SynResult.failure(it) })
        }
        bracesVal.content.finishChildBuffer()
        return SynResult.success(
            Item.Mod(itemAttrs, vis, unsafety, modToken, ident, ModContent.Inline(bracesVal.token, items)),
        )
    }
    if (input.peek(MacroPeek)) {
        return parseMacro2(begin, input)
    }
    val macroAhead = input.fork()
    if (parseModStylePath(macroAhead).isSuccess && macroAhead.peek(NotPeek)) {
        return parseItemMacro(input, attrs)
    }
    if (input.cursor() != begin.cursor()) {
        return parseVerbatimItem(begin, input)
    }
    return SynResult.failure(input.error("expected an item"))
}

internal object StaticMutabilityParse {
    fun parse(input: ParseStream): SynResult<StaticMutability> {
        val mutToken = MutParse.parse(input).getOrNull()
        return SynResult.success(
            if (mutToken == null) {
                StaticMutability.None
            } else {
                StaticMutability.Mut(mutToken)
            },
        )
    }
}

private fun parseItemExternCrate(
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
): SynResult<Item> {
    val externToken = ExternParse.parse(input).getOrElse { return SynResult.failure(it) }
    val crateToken = CrateParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident =
        if (input.peek(SelfValuePeek)) {
            from(SelfValueParse.parse(input).getOrElse { return SynResult.failure(it) })
        } else {
            IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    val rename =
        if (input.peek(AsPeek)) {
            val asToken = AsParse.parse(input).getOrElse { return SynResult.failure(it) }
            val renamed =
                if (input.peek(UnderscorePeek)) {
                    from(UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) })
                } else {
                    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                }
            AsIdent(asToken, renamed)
        } else {
            null
        }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Item.ExternCrate(attrs, vis, externToken, crateToken, ident, rename, semiToken))
}

private fun parseItemForeignMod(
    attrs: List<Attribute>,
    input: ParseStream,
): SynResult<Item> {
    val itemAttrs = attrs.toMutableList()
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val abi = parseAbi(input).getOrElse { return SynResult.failure(it) }
    val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
    parseInner(bracesVal.content, itemAttrs).getOrElse { return SynResult.failure(it) }
    val items = mutableListOf<ForeignItem>()
    while (!bracesVal.content.isEmpty()) {
        val item = parseForeignItem(bracesVal.content)
        if (item.isFailure) return asFailure(item)
        items.add(item.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Item.ForeignMod(itemAttrs, unsafety, abi, bracesVal.token, items))
}

internal fun parseForeignItem(input: ParseStream): SynResult<ForeignItem> {
    val begin = input.fork()
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }

    if (peekSignature(input)) {
        val sig = parseSignature(input).getOrElse { return SynResult.failure(it) }
        if (input.peek(BracePeek)) {
            parseBlock(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(ForeignItem.Verbatim(between(begin, input)))
        }
        val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(ForeignItem.Fn(attrs, vis, sig, semiToken))
    }

    if (input.peek(StaticPeek) || (input.peek(UnsafePeek) && input.peek2(StaticPeek))) {
        val unsafety = UnsafeParse.parse(input).getOrNull()
        val staticToken = StaticParse.parse(input).getOrElse { return SynResult.failure(it) }
        val mutability = StaticMutabilityParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
        val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        val hasValue = input.peek(EqPeek)
        if (hasValue) {
            EqParse.parse(input).getOrElse { return SynResult.failure(it) }
            parseExprFull(input).getOrElse { return SynResult.failure(it) }
        }
        val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        return if (unsafety != null || hasValue) {
            SynResult.success(ForeignItem.Verbatim(between(begin, input)))
        } else {
            SynResult.success(ForeignItem.Static(attrs, vis, staticToken, mutability, ident, colonToken, ty, semiToken))
        }
    }

    if (input.peek(SynTypePeek)) {
        return parseForeignItemType(begin, attrs, vis, input)
    }

    val macroAhead = input.fork()
    if (vis.isInherited() && parseModStylePath(macroAhead).isSuccess && macroAhead.peek(NotPeek)) {
        return parseForeignItemMacro(attrs, input)
    }

    return SynResult.failure(input.error("expected foreign item"))
}

private fun parseForeignItemType(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
): SynResult<ForeignItem> {
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val (colonToken, _) = FlexibleItemType.parseOptionalBounds(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val ty = FlexibleItemType.parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }
    if (generics.whereClause == null) {
        generics.whereClause = parseWhereClause(input).getOrNull()
    }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    return if (colonToken != null || ty != null) {
        SynResult.success(ForeignItem.Verbatim(between(begin, input)))
    } else {
        SynResult.success(ForeignItem.ItemType(attrs, vis, typeToken, ident, generics, semiToken))
    }
}

private fun parseForeignItemMacro(
    attrs: List<Attribute>,
    input: ParseStream,
): SynResult<ForeignItem> {
    val mac = Macro.parse(input).getOrElse { return SynResult.failure(it) }
    val semiToken =
        if (mac.delimiter.isBrace) {
            null
        } else {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(ForeignItem.Macro(attrs, mac, semiToken))
}

private fun parseItemType(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
): SynResult<Item> {
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val (colonToken, _) = FlexibleItemType.parseOptionalBounds(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val ty = FlexibleItemType.parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    if (colonToken != null || ty == null) {
        return SynResult.success(Item.Verbatim(between(begin, input)))
    }

    return SynResult.success(
        Item.ItemType(
            attrs,
            vis,
            typeToken,
            ident,
            generics,
            ty.eqToken,
            ty.type,
            semiToken,
        ),
    )
}

private fun parseItemUse(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
    allowCrateRootInPath: Boolean,
): SynResult<Item> {
    val useToken = UseParse.parse(input).getOrElse { return SynResult.failure(it) }
    val leadingColon = PathSepParse.parse(input).getOrNull()
    val tree =
        parseUseTree(input, allowCrateRootInPath && leadingColon == null)
            .getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    return if (tree == null) {
        SynResult.success(Item.Verbatim(between(begin, input)))
    } else {
        SynResult.success(Item.Use(attrs, vis, useToken, leadingColon, tree, semiToken))
    }
}

private fun parseRestOfTrait(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    unsafety: io.github.kotlinmania.syn.token.Unsafe?,
    autoToken: io.github.kotlinmania.syn.token.Auto?,
    traitToken: io.github.kotlinmania.syn.token.Trait,
    ident: Ident,
    generics: Generics,
): SynResult<Item.Trait> {
    val supertraits = TypeParamBoundList()
    var colonToken: io.github.kotlinmania.syn.token.Colon? = null
    if (input.peek(ColonPeek)) {
        colonToken = ColonParse.parse(input).getOrThrow()
        if (!input.peek(WherePeek) && !input.peek(BracePeek)) {
            val bounds = parseTypeParamBounds(input, stopAtEq = false).getOrElse { return SynResult.failure(it) }
            for ((bound, plus) in bounds.pairsList()) {
                supertraits.pushValue(bound as TypeParamBound)
                plus?.let { supertraits.pushPunct(it) }
            }
        }
    }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val bracesVal = braced(input).getOrThrow()
    val items = mutableListOf<TraitItem>()
    while (!bracesVal.content.isEmpty()) {
        items.add(parseTraitItem(bracesVal.content).getOrElse { return SynResult.failure(it) })
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(
        Item.Trait(attrs, vis, unsafety, autoToken, null, traitToken, ident, generics, colonToken, supertraits, bracesVal.token, items),
    )
}

private data class TraitAliasStart(
    val attrs: List<Attribute>,
    val vis: Visibility,
    val traitToken: io.github.kotlinmania.syn.token.Trait,
    val ident: Ident,
    val generics: Generics,
)

private fun parseTraitOrTraitAlias(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
): SynResult<Item> {
    val start = parseStartOfTraitAlias(input, attrs, vis).getOrElse { return SynResult.failure(it) }
    val lookahead = input.lookahead1()
    return when {
        lookahead.peek(BracePeek) || lookahead.peek(ColonPeek) || lookahead.peek(WherePeek) -> {
            parseRestOfTrait(
                input,
                start.attrs,
                start.vis,
                unsafety = null,
                autoToken = null,
                start.traitToken,
                start.ident,
                start.generics,
            ).map { it }
        }

        lookahead.peek(EqPeek) -> {
            parseRestOfTraitAlias(
                input,
                start.attrs,
                start.vis,
                start.traitToken,
                start.ident,
                start.generics,
            ).map { it }
        }

        else -> {
            SynResult.failure(lookahead.error())
        }
    }
}

private fun parseStartOfTraitAlias(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
): SynResult<TraitAliasStart> {
    val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(TraitAliasStart(attrs, vis, traitToken, ident, generics))
}

private fun parseRestOfTraitAlias(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    traitToken: io.github.kotlinmania.syn.token.Trait,
    ident: Ident,
    generics: Generics,
): SynResult<Item.TraitAlias> {
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val bounds = TypeParamBoundList()

    while (true) {
        if (input.peek(WherePeek) || input.peek(SemiPeek)) break
        val bound =
            TypeParamBound
                .parseSingle(
                    input,
                    allowPreciseCapture = false,
                    allowConst = false,
                ).getOrElse { return SynResult.failure(it) }
        bounds.pushValue(bound)
        if (input.peek(WherePeek) || input.peek(SemiPeek)) break
        bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
    }

    generics.whereClause = parseWhereClause(input).getOrNull()
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    return SynResult.success(
        Item.TraitAlias(attrs, vis, traitToken, ident, generics, eqToken, bounds, semiToken),
    )
}

private fun parseVerbatimItem(
    begin: ParseStream,
    input: ParseStream,
): SynResult<Item> {
    TokenStreamParse.parse(input).getOrElse { return SynResult.failure(it) }
    val tokens = TokenStreamParse.parse(begin).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Item.Verbatim(tokens))
}

private fun parseItemMacro(input: ParseStream, attrs: List<Attribute>): SynResult<Item> {
    val path = parseModStylePath(input).getOrElse { return SynResult.failure(it) }
    val bangToken = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrNull()
    val delimiterResult = parseDelimiter(input)
    if (delimiterResult.isFailure) return asFailure(delimiterResult)
    val (delimiter, tokens) = delimiterResult.getOrThrow()
    val semiToken =
        if (delimiter.isBrace) {
            null
        } else {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(
        Item.Macro(attrs, ident, Macro(path, bangToken, delimiter, tokens), semiToken),
    )
}

private fun parseMacro2(
    begin: ParseStream,
    input: ParseStream,
): SynResult<Item> {
    MacroParse.parse(input).getOrElse { return SynResult.failure(it) }
    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }

    var lookahead = input.lookahead1()
    if (lookahead.peek(ParenPeek)) {
        val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
        TokenStreamParse.parse(parens.content).getOrElse { return SynResult.failure(it) }
        parens.content.finishChildBuffer()
        lookahead = input.lookahead1()
    }

    if (lookahead.peek(BracePeek)) {
        val braces = braced(input).getOrElse { return SynResult.failure(it) }
        TokenStreamParse.parse(braces.content).getOrElse { return SynResult.failure(it) }
        braces.content.finishChildBuffer()
    } else {
        return SynResult.failure(lookahead.error())
    }

    return SynResult.success(Item.Verbatim(between(begin, input)))
}

private fun parseImpl(
    begin: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    input: ParseStream,
    allowVerbatimImpl: Boolean,
): SynResult<Item?> {
    val implAhead = input.fork()
    val defaultness = DefaultParse.parse(implAhead).getOrNull()
    val unsafety = UnsafeParse.parse(implAhead).getOrNull()
    if (!implAhead.peek(ImplPeek)) {
        return SynResult.success(null)
    }
    if (allowVerbatimImpl && !vis.isInherited()) {
        return parseVerbatimItem(begin, input).map { it }
    }

    input.advanceTo(implAhead)
    val implToken = ImplParse.parse(input).getOrThrow()
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val traitPath: PathTrait?
    val selfType: SynType
    if (input.peek(NotPeek)) {
        val polarity = NotParse.parse(input).getOrThrow()
        if (input.peek(BracePeek) || input.peek(WherePeek)) {
            traitPath = null
            selfType = SynType.Never(polarity)
        } else {
            val path = PathParse.parse(input).getOrElse { return SynResult.failure(it) }
            if (!input.peek(ForPeek)) {
                return SynResult.failure(input.error("inherent impls cannot be negative"))
            }
            val forToken = ForParse.parse(input).getOrThrow()
            val parsedSelfType = parseTypeFull(input)
            if (parsedSelfType.isFailure) return asFailure(parsedSelfType)
            traitPath = PathTrait(polarity, path, forToken)
            selfType = parsedSelfType.getOrThrow()
        }
    } else {
        val traitAhead = input.fork()
        val pathResult = PathParse.parse(traitAhead)
        if (pathResult.isSuccess && traitAhead.peek(ForPeek)) {
            input.advanceTo(traitAhead)
            val forToken = ForParse.parse(input).getOrThrow()
            val parsedSelfType = parseTypeFull(input)
            if (parsedSelfType.isFailure) return asFailure(parsedSelfType)
            traitPath = PathTrait(null, pathResult.getOrThrow(), forToken)
            selfType = parsedSelfType.getOrThrow()
        } else {
            val parsedSelfType = parseTypeFull(input)
            if (parsedSelfType.isFailure) return asFailure(parsedSelfType)
            traitPath = null
            selfType = parsedSelfType.getOrThrow()
        }
    }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val bracesVal = braced(input).getOrThrow()
    val items = mutableListOf<ImplItem>()
    while (!bracesVal.content.isEmpty()) {
        items.add(parseImplItem(bracesVal.content).getOrElse { return SynResult.failure(it) })
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(
        Item.Impl(attrs, defaultness, unsafety, implToken, generics, traitPath, selfType, bracesVal.token, items),
    )
}

private data class FlexibleItemType(
    val vis: Visibility,
    val defaultness: Default?,
    val typeToken: SynTypeToken,
    val ident: Ident,
    val generics: Generics,
    val colonToken: Colon?,
    val bounds: TypeParamBoundList,
    val ty: EqSynType?,
    val semiToken: Semi,
) {
    companion object {
        fun parse(
            input: ParseStream,
            allowDefaultness: TypeDefaultness,
            whereClauseLocation: WhereClauseLocation,
        ): SynResult<FlexibleItemType> {
            val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val defaultness =
                when (allowDefaultness) {
                    TypeDefaultness.Optional -> DefaultParse.parse(input).getOrNull()
                    TypeDefaultness.Disallowed -> null
                }
            val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val (colonToken, bounds) = parseOptionalBounds(input).getOrElse { return SynResult.failure(it) }

            when (whereClauseLocation) {
                WhereClauseLocation.BeforeEq,
                WhereClauseLocation.Both,
                -> {
                    generics.whereClause = parseWhereClause(input).getOrNull()
                }

                WhereClauseLocation.AfterEq -> {}
            }

            val ty = parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }

            when (whereClauseLocation) {
                WhereClauseLocation.AfterEq,
                WhereClauseLocation.Both,
                -> {
                    if (generics.whereClause == null) {
                        generics.whereClause = parseWhereClause(input).getOrNull()
                    }
                }

                WhereClauseLocation.BeforeEq -> {}
            }

            val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(
                FlexibleItemType(
                    vis,
                    defaultness,
                    typeToken,
                    ident,
                    generics,
                    colonToken,
                    bounds,
                    ty,
                    semiToken,
                ),
            )
        }

        fun parseOptionalBounds(input: ParseStream): SynResult<Pair<Colon?, TypeParamBoundList>> {
            val colonToken = ColonParse.parse(input).getOrNull()
            val bounds = TypeParamBoundList()
            if (colonToken != null) {
                while (true) {
                    if (input.peek(WherePeek) || input.peek(EqPeek) || input.peek(SemiPeek)) break
                    val bound =
                        TypeParamBound
                            .parseSingle(
                                input,
                                allowPreciseCapture = false,
                                allowConst = true,
                            ).getOrElse { return SynResult.failure(it) }
                    bounds.pushValue(bound)
                    if (input.peek(WherePeek) || input.peek(EqPeek) || input.peek(SemiPeek)) break
                    bounds.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
                }
            }
            return SynResult.success(colonToken to bounds)
        }

        fun parseOptionalDefinition(input: ParseStream): SynResult<EqSynType?> {
            val eqToken = EqParse.parse(input).getOrNull() ?: return SynResult.success(null)
            val definition = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(EqSynType(eqToken, definition))
        }
    }
}

private enum class TypeDefaultness {
    Optional,
    Disallowed,
}

private enum class WhereClauseLocation {
    BeforeEq,
    AfterEq,
    Both,
}

private fun <T, R> asFailure(result: SynResult<T>): SynResult<R> =
    SynResult.failure((result as SynResult.Failure).error)

private fun Visibility.isInherited(): Boolean =
    this is Visibility.Inherited

internal fun peekSignature(input: ParseStream): Boolean {
    val fork = input.fork()
    ConstParse.parse(fork)
    AsyncParse.parse(fork)
    UnsafeParse.parse(fork)
    parseAbi(fork)
    return fork.peek(FnPeek)
}

public fun parseSignature(input: ParseStream): SynResult<Signature> {
    val constness = ConstParse.parse(input).getOrNull()
    val asyncness = AsyncParse.parse(input).getOrNull()
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val abi = parseAbi(input).getOrNull()
    val fnTokenResult = FnParse.parse(input)
    if (fnTokenResult.isFailure) return asFailure(fnTokenResult)
    val identResult = IdentParse.parse(input)
    if (identResult.isFailure) return asFailure(identResult)
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val parensResult = parenthesized(input)
    if (parensResult.isFailure) return asFailure(parensResult)
    val parensVal = parensResult.getOrThrow()
    val inputsResult = parseFnArgs(parensVal.content)
    if (inputsResult.isFailure) return asFailure(inputsResult)
    val (inputs, variadic) = inputsResult.getOrThrow()
    parensVal.content.finishChildBuffer()
    val outputResult = parseReturnType(input)
    if (outputResult.isFailure) return asFailure(outputResult)
    generics.whereClause = parseWhereClause(input).getOrNull()
    return SynResult.success(
        Signature(
            constness,
            asyncness,
            unsafety,
            abi,
            fnTokenResult.getOrThrow(),
            identResult.getOrThrow(),
            generics,
            parensVal.token,
            inputs,
            variadic,
            outputResult.getOrThrow(),
        ),
    )
}

internal fun parseAbi(input: ParseStream): SynResult<Abi> {
    val externResult = ExternParse.parse(input)
    if (externResult.isFailure) return asFailure(externResult)
    val name = LitStrParse.parse(input).getOrNull()
    return SynResult.success(Abi(externResult.getOrThrow(), name))
}

private sealed class FnArgOrVariadic {
    data class FnArgValue(
        val arg: FnArg,
    ) : FnArgOrVariadic()

    data class VariadicValue(
        val variadic: Variadic,
    ) : FnArgOrVariadic()
}

private fun parseFnArgs(content: ParseStream): SynResult<Pair<FnArgList, Variadic?>> {
    val inputs = FnArgList()
    var variadic: Variadic? = null
    var hasReceiver = false
    while (!content.isEmpty()) {
        val attrs = parseOuterAttributes(content).getOrElse { return SynResult.failure(it) }

        val dots = DotDotDotParse.parse(content).getOrNull()
        if (dots != null) {
            val comma =
                if (content.isEmpty()) {
                    null
                } else {
                    CommaParse.parse(content).getOrElse { return SynResult.failure(it) }
                }
            variadic = Variadic(attrs, null, dots, comma)
            break
        }

        val argOrVariadic =
            parseFnArgOrVariadic(content, attrs, allowVariadic = true)
                .getOrElse { return SynResult.failure(it) }
        val arg =
            when (argOrVariadic) {
                is FnArgOrVariadic.FnArgValue -> {
                    argOrVariadic.arg
                }

                is FnArgOrVariadic.VariadicValue -> {
                    val comma =
                        if (content.isEmpty()) {
                            null
                        } else {
                            CommaParse.parse(content).getOrElse { return SynResult.failure(it) }
                        }
                    variadic = argOrVariadic.variadic.copy(comma = comma)
                    break
                }
            }

        if (arg is FnArg.Receiver) {
            if (hasReceiver) return SynResult.failure(content.error("unexpected second method receiver"))
            if (!inputs.isEmpty()) return SynResult.failure(content.error("unexpected method receiver"))
            hasReceiver = true
        }
        inputs.pushValue(arg)
        if (content.isEmpty()) break
        val commaResult = CommaParse.parse(content)
        if (commaResult.isFailure) return asFailure(commaResult)
        inputs.pushPunct(commaResult.getOrThrow())
    }
    return SynResult.success(inputs to variadic)
}

private fun parseRestOfFn(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    sig: Signature,
): SynResult<Item.Fn> {
    val bracesResult = braced(input)
    if (bracesResult.isFailure) return asFailure(bracesResult)
    val bracesVal = bracesResult.getOrThrow()
    val itemAttrs = attrs.toMutableList()
    parseInner(bracesVal.content, itemAttrs).getOrElse { return SynResult.failure(it) }
    val stmtsResult = parseWithin(bracesVal.content)
    if (stmtsResult.isFailure) return asFailure(stmtsResult)
    val stmts = stmtsResult.getOrThrow()
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Item.Fn(itemAttrs, vis, sig, Block(bracesVal.token, stmts)))
}

internal fun parseFnArg(input: ParseStream): SynResult<FnArg> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    return when (val arg = parseFnArgOrVariadic(input, attrs, allowVariadic = false).getOrElse { return SynResult.failure(it) }) {
        is FnArgOrVariadic.FnArgValue -> SynResult.success(arg.arg)
        is FnArgOrVariadic.VariadicValue -> SynResult.failure(input.error("expected function argument"))
    }
}

private fun parseFnArgOrVariadic(
    input: ParseStream,
    attrs: List<Attribute>,
    allowVariadic: Boolean,
): SynResult<FnArgOrVariadic> {
    val ahead = input.fork()
    val receiverResult = parseReceiver(ahead)
    if (receiverResult.isSuccess) {
        input.advanceTo(ahead)
        val receiver = receiverResult.getOrThrow()
        return SynResult.success(FnArgOrVariadic.FnArgValue(receiver.copy(attrs = attrs)))
    }

    if (input.peek(IdentPeek) && input.peek2(LtPeek)) {
        val span = input.span()
        val tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return asFailure(tyResult)
        return SynResult.success(
            FnArgOrVariadic.FnArgValue(
                FnArg.Typed(
                    PatType(
                        attrs,
                        Pat.Wild(mutableListOf(), Underscore.from(span)),
                        Colon.from(span),
                        tyResult.getOrThrow(),
                    ),
                ),
            ),
        )
    }

    val patResult = parsePatFull(input)
    if (patResult.isFailure) return asFailure(patResult)
    val colonResult = ColonParse.parse(input)
    if (colonResult.isFailure) return asFailure(colonResult)

    if (allowVariadic) {
        val dots = DotDotDotParse.parse(input).getOrNull()
        if (dots != null) {
            return SynResult.success(
                FnArgOrVariadic.VariadicValue(
                    Variadic(attrs, PatColon(patResult.getOrThrow(), colonResult.getOrThrow()), dots, null),
                ),
            )
        }
    }

    val tyResult = parseTypeFull(input)
    if (tyResult.isFailure) return asFailure(tyResult)
    return SynResult.success(
        FnArgOrVariadic.FnArgValue(
            FnArg.Typed(PatType(attrs, patResult.getOrThrow(), colonResult.getOrThrow(), tyResult.getOrThrow())),
        ),
    )
}

private fun parseReceiver(input: ParseStream): SynResult<FnArg.Receiver> {
    val referenceResult = parseReceiverAnd(input)
    val reference =
        if (referenceResult.isSuccess) {
            val andToken = referenceResult.getOrThrow()
            val lifetime = LifetimeParse.parse(input).getOrNull()
            AndLifetime(andToken, lifetime)
        } else {
            null
        }
    val mutability = MutParse.parse(input).getOrNull()
    val selfTokenResult = SelfValueParse.parse(input)
    if (selfTokenResult.isFailure) return asFailure(selfTokenResult)
    val selfToken = selfTokenResult.getOrThrow()
    val colonToken =
        if (reference == null) {
            ColonParse.parse(input).getOrNull()
        } else {
            null
        }
    val ty =
        if (colonToken != null) {
            parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        } else {
            val selfTy =
                SynType.Path(
                    null,
                    Path.from(
                        io.github.kotlinmania.procmacro2.Ident
                            .new("Self", selfToken.span),
                    ),
                )
            if (reference != null) {
                SynType.Reference(reference.andToken, reference.lifetime, mutability, selfTy)
            } else {
                selfTy
            }
        }
    return SynResult.success(FnArg.Receiver(mutableListOf(), reference, mutability, selfToken, colonToken, ty))
}

private fun parseReceiverAnd(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.And> =
    input.step { cursor ->
        val (punct, rest) =
            cursor.punct()
                ?: return@step SynResult.failure(cursor.error("expected `&`"))
        if (punct.asChar() != '&') {
            return@step SynResult.failure(cursor.error("expected `&`"))
        }
        SynResult.success(
            io.github.kotlinmania.syn.token.And
                .from(punct.span()) to rest,
        )
    }

internal fun parseUseTree(input: ParseStream): SynResult<UseTree> {
    val tree = parseUseTree(input, allowCrateRootInPath = false).getOrElse { return SynResult.failure(it) }
    return tree?.let { SynResult.success(it) } ?: SynResult.failure(input.error("expected use tree"))
}

private fun parseUseTree(
    input: ParseStream,
    allowCrateRootInPath: Boolean,
): SynResult<UseTree?> {
    val lookahead = input.lookahead1()
    if (
        lookahead.peek(IdentPeek) ||
        lookahead.peek(SelfValuePeek) ||
        lookahead.peek(SuperPeek) ||
        lookahead.peek(CratePeek) ||
        lookahead.peek(TryPeek)
    ) {
        val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
        if (input.peek(PathSepPeek)) {
            val colon2Token = PathSepParse.parse(input).getOrElse { return SynResult.failure(it) }
            val tree = parseUseTree(input).getOrElse { return SynResult.failure(it) }
            return SynResult.success(UseTree.Path(ident, colon2Token, tree))
        }
        if (input.peek(AsPeek)) {
            val asToken = AsParse.parse(input).getOrElse { return SynResult.failure(it) }
            val rename =
                if (input.peek(IdentPeek)) {
                    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
                } else if (input.peek(UnderscorePeek)) {
                    from(UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) })
                } else {
                    return SynResult.failure(input.error("expected identifier or underscore"))
                }
            return SynResult.success(UseTree.Name(ident, AsIdent(asToken, rename)))
        }
        return SynResult.success(UseTree.Name(ident, null))
    }

    if (lookahead.peek(StarPeek)) {
        val starToken = StarParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(UseTree.Glob(starToken))
    }

    if (lookahead.peek(BracePeek)) {
        val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
        val items = UseTreeList()
        var hasAnyCrateRootInPath = false
        while (!bracesVal.content.isEmpty()) {
            val thisTreeStartsWithCrateRoot =
                if (allowCrateRootInPath && bracesVal.content.peek(PathSepPeek)) {
                    PathSepParse.parse(bracesVal.content).getOrElse { return SynResult.failure(it) }
                    true
                } else {
                    false
                }
            hasAnyCrateRootInPath = hasAnyCrateRootInPath || thisTreeStartsWithCrateRoot
            val tree =
                parseUseTree(
                    bracesVal.content,
                    allowCrateRootInPath && !thisTreeStartsWithCrateRoot,
                ).getOrElse { return SynResult.failure(it) }
            if (tree != null && !hasAnyCrateRootInPath) {
                items.pushValue(tree)
            } else {
                hasAnyCrateRootInPath = true
            }
            if (bracesVal.content.isEmpty()) break
            val comma = CommaParse.parse(bracesVal.content).getOrElse { return SynResult.failure(it) }
            if (!hasAnyCrateRootInPath) {
                items.pushPunct(comma)
            }
        }
        bracesVal.content.finishChildBuffer()
        return if (hasAnyCrateRootInPath) {
            SynResult.success(null)
        } else {
            SynResult.success(UseTree.Group(bracesVal.token, items))
        }
    }

    return SynResult.failure(lookahead.error())
}

internal fun parseTraitItem(input: ParseStream): SynResult<TraitItem> {
    val begin = input.fork()
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val defaultness =
        if (input.peek(DefaultPeek)) DefaultParse.parse(input).getOrElse { return SynResult.failure(it) } else null
    val item: TraitItem =
        if (peekSignature(input)) {
            val sig = parseSignature(input).getOrElse { return SynResult.failure(it) }
            if (input.peek(BracePeek)) {
                val braces = braced(input).getOrElse { return SynResult.failure(it) }
                val innerAttrs = mutableListOf<Attribute>()
                parseInner(braces.content, innerAttrs).getOrElse { return SynResult.failure(it) }
                val stmts = parseWithin(braces.content).getOrElse { return SynResult.failure(it) }
                braces.content.finishChildBuffer()
                TraitItem.Fn(innerAttrs, sig, Block(braces.token, stmts), null)
            } else {
                val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                TraitItem.Fn(emptyList(), sig, null, semi)
            }
        } else if (input.peek(ConstPeek)) {
            val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
            val lookahead = input.lookahead1()
            if (!lookahead.peek(IdentPeek) && !lookahead.peek(UnderscorePeek)) {
                return SynResult.failure(lookahead.error())
            }
            val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val colon = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            val default =
                if (input.peek(EqPeek)) {
                    val eq = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
                    EqExpr(eq, expr)
                } else {
                    null
                }
            generics.whereClause =
                if (input.peek(WherePeek)) parseWhereClause(input).getOrElse { return SynResult.failure(it) } else null
            val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            if (generics.ltToken != null || generics.whereClause != null) {
                return SynResult.success(TraitItem.Verbatim(between(begin, input)))
            }
            TraitItem.Const(emptyList(), constToken, ident, generics, colon, ty, default, semi)
        } else if (input.peek(SynTypePeek)) {
            parseTraitItemType(begin, input, emptyList()).getOrElse { return SynResult.failure(it) }
        } else if (vis.isInherited() && defaultness == null &&
            (input.peek(IdentPeek) || input.peek(SelfValuePeek) || input.peek(SuperPeek) || input.peek(CratePeek) || input.peek(PathSepPeek))
        ) {
            val mac = Macro.parse(input).getOrElse { return SynResult.failure(it) }
            val semi = if (mac.isBrace()) null else SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            TraitItem.Macro(emptyList(), mac, semi)
        } else {
            return SynResult.failure(input.error("expected trait item"))
        }
    if (!vis.isInherited() || defaultness != null) {
        return SynResult.success(TraitItem.Verbatim(between(begin, input)))
    }
    when (item) {
        is TraitItem.Const -> item.attrs = attrs + item.attrs
        is TraitItem.Fn -> item.attrs = attrs + item.attrs
        is TraitItem.AssocType -> item.attrs = attrs + item.attrs
        is TraitItem.Macro -> item.attrs = attrs + item.attrs
        is TraitItem.Verbatim -> Unit
    }
    return SynResult.success(item)
}

internal fun parseImplItem(input: ParseStream): SynResult<ImplItem> {
    val begin = input.fork()
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val ahead = input.fork()
    val vis = VisibilityParse.parse(ahead).getOrElse { return SynResult.failure(it) }
    val defaultness =
        if (ahead.peek(DefaultPeek) && !ahead.peek2(NotPeek)) {
            DefaultParse.parse(ahead).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }

    val item: ImplItem =
        if (ahead.peek(FnPeek) || peekSignature(ahead)) {
            // Like Rust Syn, preserve body-less methods as verbatim syntax for macro DSLs.
            parseImplItemFn(input, allowOmittedBody = true).getOrElse { return SynResult.failure(it) }
                ?: return SynResult.success(ImplItem.Verbatim(between(begin, input)))
        } else if (ahead.peek(ConstPeek)) {
            input.advanceTo(ahead)
            val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
            val lookahead = input.lookahead1()
            if (!lookahead.peek(IdentPeek) && !lookahead.peek(UnderscorePeek)) {
                return SynResult.failure(lookahead.error())
            }
            val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
            val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
            val colon = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            val value =
                if (input.peek(EqPeek)) {
                    val eq = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
                    EqExpr(eq, expr)
                } else {
                    null
                }
            generics.whereClause =
                if (input.peek(WherePeek)) parseWhereClause(input).getOrElse { return SynResult.failure(it) } else null
            val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return if (value != null && generics.ltToken == null && generics.whereClause == null) {
                SynResult.success(ImplItem.Const(attrs, vis, defaultness, constToken, ident, generics, colon, ty, value.eqToken, value.expr, semi))
            } else {
                SynResult.success(ImplItem.Verbatim(between(begin, input)))
            }
        } else if (ahead.peek(SynTypePeek)) {
            parseImplItemType(begin, input).getOrElse { return SynResult.failure(it) }
        } else if (vis.isInherited() && defaultness == null &&
            (ahead.peek(IdentPeek) || ahead.peek(SelfValuePeek) || ahead.peek(SuperPeek) || ahead.peek(CratePeek) || ahead.peek(PathSepPeek))
        ) {
            val mac = Macro.parse(input).getOrElse { return SynResult.failure(it) }
            val semi = if (mac.isBrace()) null else SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            ImplItem.Macro(emptyList(), mac, semi)
        } else {
            return SynResult.failure(ahead.error("expected impl item"))
        }

    when (item) {
        is ImplItem.Const -> item.attrs = attrs + item.attrs
        is ImplItem.Fn -> item.attrs = attrs + item.attrs
        is ImplItem.AssocType -> item.attrs = attrs + item.attrs
        is ImplItem.Macro -> item.attrs = attrs + item.attrs
        is ImplItem.Verbatim -> Unit
    }
    return SynResult.success(item)
}

private fun parseTraitItemType(begin: ParseStream, input: ParseStream, attrs: List<Attribute>): SynResult<TraitItem> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Disallowed,
                WhereClauseLocation.AfterEq,
            ).getOrElse { return SynResult.failure(it) }
    if (!itemType.vis.isInherited()) {
        return SynResult.success(TraitItem.Verbatim(between(begin, input)))
    }
    return SynResult.success(
        TraitItem.AssocType(
            attrs,
            itemType.typeToken,
            itemType.ident,
            itemType.generics,
            itemType.colonToken,
            itemType.bounds,
            itemType.ty,
            itemType.semiToken,
        ),
    )
}

private fun parseImplItemType(begin: ParseStream, input: ParseStream): SynResult<ImplItem> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Optional,
                WhereClauseLocation.AfterEq,
            ).getOrElse { return SynResult.failure(it) }
    val ty = itemType.ty
    if (itemType.colonToken != null || ty == null) {
        return SynResult.success(ImplItem.Verbatim(between(begin, input)))
    }
    return SynResult.success(
        ImplItem.AssocType(
            mutableListOf(),
            itemType.vis,
            itemType.defaultness,
            itemType.typeToken,
            itemType.ident,
            itemType.generics,
            ty.eqToken,
            ty.type,
            itemType.semiToken,
        ),
    )
}

private fun parseImplItemFn(
    input: ParseStream,
    allowOmittedBody: Boolean,
): SynResult<ImplItem.Fn?> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }.toMutableList()
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val defaultness = DefaultParse.parse(input).getOrNull()
    val sig = parseSignature(input).getOrElse { return SynResult.failure(it) }

    if (allowOmittedBody && SemiParse.parse(input).isSuccess) {
        return SynResult.success(null)
    }

    val bracesResult = braced(input)
    if (bracesResult.isFailure) return asFailure(bracesResult)
    val bracesVal = bracesResult.getOrThrow()
    parseInner(bracesVal.content, attrs).getOrElse { return SynResult.failure(it) }
    val stmtsResult = parseWithin(bracesVal.content)
    if (stmtsResult.isFailure) return asFailure(stmtsResult)
    val stmts = stmtsResult.getOrThrow()
    bracesVal.content.finishChildBuffer()

    return SynResult.success(ImplItem.Fn(attrs, vis, defaultness, sig, Block(bracesVal.token, stmts)))
}
