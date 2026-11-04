// port-lint: source item.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.token.And
import io.github.kotlinmania.syn.token.Auto
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Comma
import io.github.kotlinmania.syn.token.Default
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.Mut
import io.github.kotlinmania.syn.token.Not
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.PathSep
import io.github.kotlinmania.syn.token.Safe
import io.github.kotlinmania.syn.token.SelfValue
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
        public var modifiers: ConstModifiers = ConstModifiers(),
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon,
        public var ty: SynType,
        public var eqToken: Eq,
        public var expr: Expr,
        public var semiToken: Semi,
    ) : Item() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Const> = parseItemConst(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            constToken.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            eqToken.toTokens(tokens)
            expr.toTokens(tokens)
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Enum> = parseItemEnum(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<ExternCrate> = parseItemExternCrate(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public var modifiers: FnModifiers = FnModifiers(),
        public var sig: Signature,
        public var block: Block,
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
            block: Block,
        ) : this(
            attrs,
            vis,
            FnModifiers(),
            Signature(null, null, Safety.Default, null, fnToken, ident, generics, parenToken, inputs, null, output ?: ReturnType.Default),
            block,
        )

        public companion object {
            public fun parse(input: ParseStream): SynResult<Fn> = parseItemFn(input)
        }

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
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            sig.toTokens(tokens)
            block.braceToken.surround(tokens) { inner ->
                for (attr in attrs) {
                    if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                }
                for (stmt in block.stmts) stmt.toTokens(inner)
            }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<ForeignMod> = parseItemForeignMod(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            unsafety?.toTokens(tokens)
            abi.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (attr in attrs) {
                    if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Struct> = parseItemStruct(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Union> = parseItemUnion(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Mod> = parseItemMod(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            unsafety?.toTokens(tokens)
            modToken.toTokens(tokens)
            ident.toTokens(tokens)
            when (val body = content) {
                is ModContent.Inline ->
                    body.braceToken.surround(tokens) { inner ->
                        for (attr in attrs) {
                            if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                        }
                        for (item in body.items) item.toTokens(inner)
                    }
                is ModContent.Unnamed -> body.toTokens(tokens)
                null -> Semi.default().toTokens(tokens)
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Use> =
                parseItemUse(input, allowCrateRootInPath = false).map { it!! }
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Static> = parseItemStatic(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public var modifiers: TraitModifiers = TraitModifiers(),
        public var unsafety: Unsafe?,
        public var traitToken: io.github.kotlinmania.syn.token.Trait,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon?,
        public var supertraits: TypeParamBoundList,
        public var braceToken: Brace,
        public var items: List<TraitItem>,
    ) : Item() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Trait> = parseItemTrait(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            unsafety?.toTokens(tokens)
            modifiers.autoToken?.toTokens(tokens)
            traitToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            if (!supertraits.isEmpty()) {
                tokensOrDefault(colonToken, Colon::default).toTokens(tokens)
                supertraits.toTokens(tokens)
            }
            generics.whereClause?.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (attr in attrs) {
                    if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<TraitAlias> = parseItemTraitAlias(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public var modifiers: TypeModifiers = TypeModifiers(),
        public var typeToken: SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var eqToken: Eq,
        public var ty: SynType,
        public var semiToken: Semi,
        public var whereClausePlacement: WhereClausePlacement = WhereClausePlacement.Early,
    ) : Item() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<ItemType> = parseStandaloneItemType(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            typeToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            if (whereClausePlacement == WhereClausePlacement.Early) {
                generics.whereClause?.toTokens(tokens)
            }
            eqToken.toTokens(tokens)
            ty.toTokens(tokens)
            if (whereClausePlacement == WhereClausePlacement.Late) {
                generics.whereClause?.toTokens(tokens)
            }
            semiToken.toTokens(tokens)
        }
    }

    /** An impl block providing trait or associated items. */
    public data class Impl(
        public var attrs: List<Attribute>,
        public var modifiers: ImplModifiers = ImplModifiers(),
        public var unsafety: Unsafe?,
        public var implToken: io.github.kotlinmania.syn.token.Impl,
        public var generics: Generics,
        public var traitPath: PathForPair?,
        public var selfType: SynType,
        public var braceToken: Brace,
        public var items: List<ImplItem>,
    ) : Item() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Impl> = parseStandaloneItemImpl(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            modifiers.defaultness?.toTokens(tokens)
            unsafety?.toTokens(tokens)
            implToken.toTokens(tokens)
            generics.toTokens(tokens)
            modifiers.polarity?.toTokens(tokens)
            traitPath?.let { (path, forToken) ->
                path.toTokens(tokens)
                forToken.toTokens(tokens)
            }
            selfType.toTokens(tokens)
            generics.whereClause?.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (attr in attrs) {
                    if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Macro> = parseStandaloneItemMacro(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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

/** Additional optional information about a constant item. */
public data class ConstModifiers(
    public var defaultness: Default? = null,
) {
    public companion object {
        public fun default(): ConstModifiers = ConstModifiers()
    }

    public fun requireEmpty(): SynResult<Unit> {
        val defaultToken = defaultness
        if (defaultToken != null) {
            return SynResult.failure(SynError.new(defaultToken.span, "unexpected const item modifier"))
        }
        return SynResult.success(Unit)
    }

    public fun deepCopy(): ConstModifiers = ConstModifiers(defaultness)
}

/** Additional optional information about a function item. */
public data class FnModifiers(
    public var defaultness: Default? = null,
) {
    public companion object {
        public fun default(): FnModifiers = FnModifiers()
    }

    public fun requireEmpty(): SynResult<Unit> {
        val defaultToken = defaultness
        if (defaultToken != null) {
            return SynResult.failure(SynError.new(defaultToken.span, "unexpected function modifier"))
        }
        return SynResult.success(Unit)
    }

    public fun deepCopy(): FnModifiers = FnModifiers(defaultness)
}

/** Additional optional information about an implementation block. */
public data class ImplModifiers(
    public var defaultness: Default? = null,
    public var polarity: Not? = null,
) {
    public companion object {
        public fun default(): ImplModifiers = ImplModifiers()
    }

    public fun requireEmpty(): SynResult<Unit> {
        var error: SynError? = null
        defaultness?.let {
            error = SynError.new(it.span, "unexpected impl modifier")
        }
        polarity?.let {
            val err = SynError.new(it.span, "unexpected impl modifier")
            val prev = error
            if (prev == null) {
                error = err
            } else {
                prev.combine(err)
            }
        }
        return error?.let { SynResult.failure(it) } ?: SynResult.success(Unit)
    }

    public fun deepCopy(): ImplModifiers = ImplModifiers(defaultness, polarity)
}

/** Additional optional information about a trait item. */
public data class TraitModifiers(
    public var autoToken: Auto? = null,
) {
    public companion object {
        public fun default(): TraitModifiers = TraitModifiers()
    }

    public fun requireEmpty(): SynResult<Unit> {
        val autoVal = autoToken
        if (autoVal != null) {
            return SynResult.failure(SynError.new(autoVal.span, "unexpected trait modifier"))
        }
        return SynResult.success(Unit)
    }

    public fun deepCopy(): TraitModifiers = TraitModifiers(autoToken)
}

/** Additional optional information about a type alias item. */
public data class TypeModifiers(
    public var defaultness: Default? = null,
) {
    public companion object {
        public fun default(): TypeModifiers = TypeModifiers()
    }

    public fun requireEmpty(): SynResult<Unit> {
        val defaultToken = defaultness
        if (defaultToken != null) {
            return SynResult.failure(SynError.new(defaultToken.span, "unexpected type alias modifier"))
        }
        return SynResult.success(Unit)
    }

    public fun deepCopy(): TypeModifiers = TypeModifiers(defaultness)
}

/** Explicit or default memory-safety qualification on an item or signature. */
public sealed class Safety : ToTokens {
    /** The item carries an explicit positive safety qualifier. */
    public data class Safe(
        public var token: io.github.kotlinmania.syn.token.Safe,
    ) : Safety()

    /** The item carries an explicit unchecked-memory qualifier. */
    public data class Unsafe(
        public var token: io.github.kotlinmania.syn.token.Unsafe,
    ) : Safety()

    /** The item is not qualified either way. */
    public data object Default : Safety()

    override fun toTokens(tokens: TokenStream) {
        when (this) {
            is Safe -> token.toTokens(tokens)
            is Unsafe -> token.toTokens(tokens)
            Default -> Unit
        }
    }

    public fun deepCopy(): Safety =
        when (this) {
            is Safe -> Safe(token)
            is Unsafe -> Unsafe(token)
            Default -> Default
        }

    public companion object {
        public fun default(): Safety = Default

        /** Parses an explicit positive, unchecked, or omitted safety qualifier. */
        public fun parseSafeOrUnsafe(input: ParseStream): SynResult<Safety> {
            if (input.peek(SafePeek)) {
                val token = SafeParse.parse(input).getOrElse { return SynResult.failure(it) }
                return SynResult.success(Safe(token))
            }
            return parseUnsafeOnly(input)
        }

        /** Parses an explicit unchecked or omitted safety qualifier. */
        public fun parseUnsafeOnly(input: ParseStream): SynResult<Safety> {
            val token = UnsafeParse.parse(input).getOrNull()
            return if (token != null) {
                SynResult.success(Unsafe(token))
            } else {
                SynResult.success(Default)
            }
        }
    }
}

/** Different shorthand and explicit notations for a method receiver. */
public sealed class ReceiverKind {
    /** A value receiver shorthand. */
    public data object Value : ReceiverKind()

    /** A borrowed receiver shorthand. */
    public data class Reference(
        public var andToken: And,
        public var lifetime: Lifetime?,
        public var mutability: Mut?,
    ) : ReceiverKind()

    /** An explicitly typed receiver. */
    public data class Typed(
        public var colonToken: Colon,
        public var ty: SynType,
    ) : ReceiverKind()

    public fun deepCopy(): ReceiverKind =
        when (this) {
            Value -> Value
            is Reference -> Reference(andToken, lifetime?.deepCopy(), mutability)
            is Typed -> Typed(colonToken, ty.deepCopy())
        }
}

/** Placement of the `where` clause in a type alias item. */
public enum class WhereClausePlacement {
    Early,
    Late,
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
    public companion object {
        public fun parse(input: ParseStream): SynResult<FnArg> = parseFnArg(input)
    }

    /** The receiver argument of an associated method. */
    public data class Receiver(
        public var attrs: List<Attribute>,
        public var mutability: Mut?,
        public var selfToken: SelfValue,
        public var kind: ReceiverKind,
    ) : FnArg() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Receiver> = parseReceiver(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            when (val kindVal = kind) {
                ReceiverKind.Value -> {
                    mutability?.toTokens(tokens)
                    selfToken.toTokens(tokens)
                }

                is ReceiverKind.Reference -> {
                    kindVal.andToken.toTokens(tokens)
                    kindVal.lifetime?.toTokens(tokens)
                    kindVal.mutability?.toTokens(tokens)
                    selfToken.toTokens(tokens)
                }

                is ReceiverKind.Typed -> {
                    mutability?.toTokens(tokens)
                    selfToken.toTokens(tokens)
                    kindVal.colonToken.toTokens(tokens)
                    kindVal.ty.toTokens(tokens)
                }
            }
        }
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
    public companion object {
        public fun parse(input: ParseStream): SynResult<UseTree> = parseUseTree(input)
    }

    /**
     * A path prefix of imports in a `use` item.
     */
    public data class Path(
        var ident: Ident,
        var colon2Token: PathSep?,
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

/** A function signature in a trait or implementation. */
public data class Signature(
    public var constness: io.github.kotlinmania.syn.token.Const?,
    public var asyncness: io.github.kotlinmania.syn.token.Async?,
    public var safety: Safety,
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
        safety.toTokens(tokens)
        abi?.toTokens(tokens)
        fnToken.toTokens(tokens)
        ident.toTokens(tokens)
        generics.toTokens(tokens)
        parenToken.surround(tokens) { inner ->
            inputs.toTokens(inner)
            variadic?.let { variadicArg ->
                if (!inputs.isEmpty() && !inputs.trailingPunct()) {
                    Comma.default().toTokens(inner)
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
    public companion object {
        public fun parse(input: ParseStream): SynResult<Abi> = parseAbi(input)
    }

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
        for (attr in attrs) {
            if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
        }
        pat?.toTokens(tokens)
        dots.toTokens(tokens)
        comma?.toTokens(tokens)
    }
}

/** Mutability of a static item. */
public sealed class StaticMutability : ToTokens {
    public companion object {
        public fun parse(input: ParseStream): SynResult<StaticMutability> = StaticMutabilityParse.parse(input)
    }

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

/**
 * An item within an `extern` block.
 */
public sealed class ForeignItem : ToTokens {
    public companion object {
        public fun parse(input: ParseStream): SynResult<ForeignItem> = parseForeignItem(input)
    }

    /** A foreign function in an extern block. */
    public data class Fn(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var modifiers: FnModifiers = FnModifiers(),
        public var sig: Signature,
        public var semiToken: Semi,
    ) : ForeignItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Fn> = parseStandaloneForeignItemFn(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            sig.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** A foreign static item in an extern block. */
    public data class Static(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var safety: Safety,
        public var staticToken: io.github.kotlinmania.syn.token.Static,
        public var mutability: StaticMutability,
        public var ident: Ident,
        public var colonToken: Colon,
        public var ty: SynType,
        public var semiToken: Semi,
    ) : ForeignItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Static> = parseStandaloneForeignItemStatic(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            safety.toTokens(tokens)
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
        public var modifiers: TypeModifiers = TypeModifiers(),
        public var typeToken: SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var semiToken: Semi,
    ) : ForeignItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<ItemType> = parseStandaloneForeignItemType(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Macro> = parseStandaloneForeignItemMacro(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
    public companion object {
        public fun parse(input: ParseStream): SynResult<TraitItem> = parseTraitItem(input)
    }

    /** An associated constant within the definition of a trait. */
    public data class Const(
        public var attrs: List<Attribute>,
        public var modifiers: ConstModifiers = ConstModifiers(),
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon,
        public var ty: SynType,
        public var default: EqExpr?,
        public var semiToken: Semi,
    ) : TraitItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Const> = parseStandaloneTraitItemConst(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            constToken.toTokens(tokens)
            ident.toTokens(tokens)
            colonToken.toTokens(tokens)
            ty.toTokens(tokens)
            default?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }
    }

    /** An associated function within the definition of a trait. */
    public data class Fn(
        public var attrs: List<Attribute>,
        public var modifiers: FnModifiers = FnModifiers(),
        public var sig: Signature,
        public var default: Block?,
        public var semiToken: Semi?,
    ) : TraitItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Fn> = parseStandaloneTraitItemFn(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            sig.toTokens(tokens)
            val defaultBlock = this.default
            if (defaultBlock != null) {
                defaultBlock.braceToken.surround(tokens) { inner ->
                    for (attr in attrs) {
                        if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                    }
                    for (stmt in defaultBlock.stmts) stmt.toTokens(inner)
                }
            } else {
                tokensOrDefault(semiToken, Semi::default).toTokens(tokens)
            }
        }
    }

    /** An associated type within the definition of a trait. */
    public data class AssocType(
        public var attrs: List<Attribute>,
        public var modifiers: TypeModifiers = TypeModifiers(),
        public var typeToken: SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon?,
        public var bounds: TypeParamBoundList,
        public var default: EqSynType?,
        public var semiToken: Semi,
    ) : TraitItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<AssocType> = parseStandaloneTraitItemType(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            typeToken.toTokens(tokens)
            ident.toTokens(tokens)
            generics.toTokens(tokens)
            if (!bounds.isEmpty()) {
                tokensOrDefault(colonToken, Colon::default).toTokens(tokens)
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Macro> = parseStandaloneTraitItemMacro(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
    public companion object {
        public fun parse(input: ParseStream): SynResult<ImplItem> = parseImplItem(input)
    }

    /** An associated constant within an impl block. */
    public data class Const(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var modifiers: ConstModifiers = ConstModifiers(),
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var ident: Ident,
        public var generics: Generics,
        public var colonToken: Colon,
        public var ty: SynType,
        public var eqToken: Eq,
        public var expr: Expr,
        public var semiToken: Semi,
    ) : ImplItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Const> = parseStandaloneImplItemConst(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            modifiers.defaultness?.toTokens(tokens)
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
        public var modifiers: FnModifiers = FnModifiers(),
        public var sig: Signature,
        public var block: Block,
    ) : ImplItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<Fn> =
                parseImplItemFn(input, allowOmittedBody = false).map { it!! }
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            modifiers.defaultness?.toTokens(tokens)
            sig.toTokens(tokens)
            block.braceToken.surround(tokens) { inner ->
                for (attr in attrs) {
                    if (attr.style is AttrStyle.Inner) attr.toTokens(inner)
                }
                for (stmt in block.stmts) stmt.toTokens(inner)
            }
        }
    }

    /** An associated type within an impl block. */
    public data class AssocType(
        public var attrs: List<Attribute>,
        public var vis: Visibility,
        public var modifiers: TypeModifiers = TypeModifiers(),
        public var typeToken: SynTypeToken,
        public var ident: Ident,
        public var generics: Generics,
        public var eqToken: Eq,
        public var ty: SynType,
        public var semiToken: Semi,
    ) : ImplItem() {
        public companion object {
            public fun parse(input: ParseStream): SynResult<AssocType> = parseStandaloneImplItemType(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
            vis.toTokens(tokens)
            modifiers.defaultness?.toTokens(tokens)
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
        public companion object {
            public fun parse(input: ParseStream): SynResult<Macro> = parseStandaloneImplItemMacro(input)
        }

        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) {
                if (attr.style is AttrStyle.Outer) attr.toTokens(tokens)
            }
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
        val begin = input.cursor()
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        return parseRestOfItem(begin, attrs, input)
    }
}

internal fun parseRestOfItem(
    begin: Cursor,
    attrs: List<Attribute>,
    input: ParseStream,
): SynResult<Item> {
    val ahead = input.fork()
    val vis = VisibilityParse.parse(ahead).getOrElse { return SynResult.failure(it) }
    val lookahead = ahead.lookahead1()
    val allowSafe = false

    val itemResult: SynResult<Item> =
        if (lookahead.peek(FnPeek) || peekSignature(ahead, allowSafe)) {
            val parsedVis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val sig = parseSignature(input, allowSafe = false).getOrElse { return SynResult.failure(it) }
            if (input.peek(SemiPeek)) {
                SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                SynResult.success(Item.Verbatim(between(begin, input.cursor())))
            } else {
                parseRestOfFn(input, mutableListOf(), parsedVis, sig).map { it }
            }
        } else if (lookahead.peek(ExternPeek)) {
            ExternParse.parse(ahead).getOrElse { return SynResult.failure(it) }
            val externLookahead = ahead.lookahead1()
            if (externLookahead.peek(CratePeek)) {
                parseItemExternCrate(input).map { it }
            } else if (externLookahead.peek(BracePeek)) {
                parseItemForeignMod(input).map { it }
            } else if (externLookahead.peek(LitStrPeek)) {
                LitStrParse.parse(ahead).getOrElse { return SynResult.failure(it) }
                val braceLookahead = ahead.lookahead1()
                if (braceLookahead.peek(BracePeek)) {
                    parseItemForeignMod(input).map { it }
                } else {
                    SynResult.failure(braceLookahead.error())
                }
            } else {
                SynResult.failure(externLookahead.error())
            }
        } else if (lookahead.peek(UsePeek)) {
            val itemUse = parseItemUse(input, allowCrateRootInPath = true).getOrElse { return SynResult.failure(it) }
            if (itemUse != null) {
                SynResult.success(itemUse)
            } else {
                SynResult.success(Item.Verbatim(between(begin, input.cursor())))
            }
        } else if (lookahead.peek(StaticPeek)) {
            val parsedVis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val staticToken = StaticParse.parse(input).getOrElse { return SynResult.failure(it) }
            val mutability = StaticMutabilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
            if (input.peek(EqPeek)) {
                EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                parseExprFull(input).getOrElse { return SynResult.failure(it) }
                SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                SynResult.success(Item.Verbatim(between(begin, input.cursor())))
            } else {
                val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
                val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
                if (input.peek(SemiPeek)) {
                    SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(Item.Verbatim(between(begin, input.cursor())))
                } else {
                    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                    val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
                    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                    SynResult.success(
                        Item.Static(mutableListOf(), parsedVis, staticToken, mutability, ident, colonToken, ty, eqToken, expr, semiToken),
                    )
                }
            }
        } else if (lookahead.peek(ConstPeek)) {
            val parsedVis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
            val constLookahead = input.lookahead1()
            if ((constLookahead.peek(IdentPeek) && !(input.peek(AutoPeek) && input.peek2(TraitPeek))) ||
                constLookahead.peek(UnderscorePeek)
            ) {
                val ident = identParseAny(input).getOrElse { return SynResult.failure(it) }
                val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
                val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
                val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
                val value =
                    if (input.peek(EqPeek)) {
                        val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
                        val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
                        EqExpr(eqToken, expr)
                    } else {
                        null
                    }
                generics.whereClause = parseWhereClause(input).getOrNull()
                val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                if (value != null && generics.ltToken == null && generics.whereClause == null) {
                    SynResult.success(
                        Item.Const(
                            mutableListOf(),
                            parsedVis,
                            ConstModifiers(),
                            constToken,
                            ident,
                            generics,
                            colonToken,
                            ty,
                            value.eqToken,
                            value.expr,
                            semiToken,
                        ),
                    )
                } else {
                    SynResult.success(Item.Verbatim(between(begin, input.cursor())))
                }
            } else if (constLookahead.peek(TraitPeek) ||
                (constLookahead.peek(UnsafePeek) && !input.peek2(ImplPeek)) ||
                constLookahead.peek(AutoPeek)
            ) {
                parseTraitOrTraitAlias(input, mutableListOf(), parsedVis, hasImplRestriction = false)
                    .getOrElse { return SynResult.failure(it) }
                SynResult.success(Item.Verbatim(between(begin, input.cursor())))
            } else if (constLookahead.peek(ImplPeek) || input.peek(UnsafePeek)) {
                val unsafety = UnsafeParse.parse(input).getOrNull()
                parseImpl(
                    input,
                    mutableListOf(),
                    defaultness = null,
                    constness = constToken,
                    unsafety = unsafety,
                    allowVerbatimImpl = true,
                ).getOrElse { return SynResult.failure(it) }
                SynResult.success(Item.Verbatim(between(begin, input.cursor())))
            } else {
                return SynResult.failure(constLookahead.error())
            }
        } else if (lookahead.peek(UnsafePeek)) {
            UnsafeParse.parse(ahead).getOrElse { return SynResult.failure(it) }
            val unsafeLookahead = ahead.lookahead1()
            if (unsafeLookahead.peek(TraitPeek) || (unsafeLookahead.peek(AutoPeek) && ahead.peek2(TraitPeek))) {
                parseItemTrait(input).map { it }
            } else if (vis.isInherited() && unsafeLookahead.peek(ImplPeek)) {
                val unsafety = UnsafeParse.parse(input).getOrElse { return SynResult.failure(it) }
                val implItem =
                    parseImpl(
                        input,
                        mutableListOf(),
                        defaultness = null,
                        constness = null,
                        unsafety = unsafety,
                        allowVerbatimImpl = true,
                    ).getOrElse { return SynResult.failure(it) }
                if (implItem != null) {
                    SynResult.success(implItem)
                } else {
                    SynResult.success(Item.Verbatim(between(begin, input.cursor())))
                }
            } else if (unsafeLookahead.peek(ExternPeek)) {
                parseItemForeignMod(input).map { it }
            } else if (unsafeLookahead.peek(ModPeek)) {
                parseItemMod(input).map { it }
            } else {
                SynResult.failure(unsafeLookahead.error())
            }
        } else if (lookahead.peek(ModPeek)) {
            parseItemMod(input).map { it }
        } else if (lookahead.peek(SynTypePeek)) {
            parseItemType(begin, input)
        } else if (lookahead.peek(StructPeek)) {
            parseItemStruct(input).map { it }
        } else if (lookahead.peek(EnumPeek)) {
            parseItemEnum(input).map { it }
        } else if (lookahead.peek(UnionPeek) && ahead.peek2(IdentPeek)) {
            parseItemUnion(input).map { it }
        } else if (lookahead.peek(ImplPeek) &&
            ahead.peek2(ParenPeek) &&
            (ahead.peek3(ConstPeek) || ahead.peek3(UnsafePeek) || ahead.peek3(AutoPeek) || ahead.peek3(TraitPeek))
        ) {
            val parsedVis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            ImplParse.parse(input).getOrElse { return SynResult.failure(it) }
            val restrictionParens = parenthesized(input).getOrElse { return SynResult.failure(it) }
            val restriction = restrictionParens.content
            val restrictionLookahead = restriction.lookahead1()
            if (restrictionLookahead.peek(CratePeek) ||
                restrictionLookahead.peek(SelfValuePeek) ||
                restrictionLookahead.peek(SuperPeek)
            ) {
                identParseAny(restriction).getOrElse {
                    restriction.finishChildBuffer()
                    return SynResult.failure(it)
                }
            } else if (restrictionLookahead.peek(InPeek)) {
                InParse.parse(restriction).getOrElse {
                    restriction.finishChildBuffer()
                    return SynResult.failure(it)
                }
                parseModStylePath(restriction).getOrElse {
                    restriction.finishChildBuffer()
                    return SynResult.failure(it)
                }
            } else {
                val err = restrictionLookahead.error()
                restriction.finishChildBuffer()
                return SynResult.failure(err)
            }
            if (!restriction.isEmpty()) {
                val err = restriction.error("unexpected token")
                restriction.finishChildBuffer()
                return SynResult.failure(err)
            }
            restriction.finishChildBuffer()
            ConstParse.parse(input)
            parseTraitOrTraitAlias(input, mutableListOf(), parsedVis, hasImplRestriction = true)
                .getOrElse { return SynResult.failure(it) }
            SynResult.success(Item.Verbatim(between(begin, input.cursor())))
        } else if (lookahead.peek(TraitPeek)) {
            val parsedVis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            parseTraitOrTraitAlias(input, mutableListOf(), parsedVis, hasImplRestriction = false)
        } else if (lookahead.peek(AutoPeek) && ahead.peek2(TraitPeek)) {
            parseItemTrait(input).map { it }
        } else if (vis.isInherited() &&
            (ahead.peek(ImplPeek) || (lookahead.peek(DefaultPeek) && !ahead.peek2(NotPeek)))
        ) {
            val defaultness = DefaultParse.parse(input).getOrNull()
            val constness = ConstParse.parse(input).getOrNull()
            val unsafety = UnsafeParse.parse(input).getOrNull()
            val implItem =
                parseImpl(
                    input,
                    mutableListOf(),
                    defaultness = defaultness,
                    constness = constness,
                    unsafety = unsafety,
                    allowVerbatimImpl = true,
                ).getOrElse { return SynResult.failure(it) }
            if (implItem != null) {
                SynResult.success(implItem)
            } else {
                SynResult.success(Item.Verbatim(between(begin, input.cursor())))
            }
        } else if (lookahead.peek(MacroPeek)) {
            input.advanceTo(ahead)
            parseMacro2(begin, input)
        } else if (vis.isInherited() &&
            (
                lookahead.peek(IdentPeek) ||
                    lookahead.peek(SelfValuePeek) ||
                    lookahead.peek(SuperPeek) ||
                    lookahead.peek(CratePeek) ||
                    lookahead.peek(PathSepPeek)
            )
        ) {
            parseStandaloneItemMacro(input).map { it }
        } else {
            SynResult.failure(lookahead.error())
        }

    val item = itemResult.getOrElse { return SynResult.failure(it) }
    val combinedAttrs = attrs.toMutableList()
    val stripped = item.replaceAttrs(mutableListOf())
    combinedAttrs.addAll(stripped.oldAttrs)
    return SynResult.success(stripped.item.replaceAttrs(combinedAttrs).item)
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

private fun parseItemExternCrate(input: ParseStream): SynResult<Item.ExternCrate> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val externToken = ExternParse.parse(input).getOrElse { return SynResult.failure(it) }
    val crateToken = CrateParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident =
        if (input.peek(SelfValuePeek)) {
            identParseAny(input).getOrElse { return SynResult.failure(it) }
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

private fun parseItemStatic(input: ParseStream): SynResult<Item.Static> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val staticToken = StaticParse.parse(input).getOrElse { return SynResult.failure(it) }
    val mutability = StaticMutabilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Item.Static(attrs, vis, staticToken, mutability, ident, colonToken, ty, eqToken, expr, semiToken))
}

private fun parseItemConst(input: ParseStream): SynResult<Item.Const> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
    val lookahead = input.lookahead1()
    val ident =
        if (lookahead.peek(IdentPeek) || lookahead.peek(UnderscorePeek)) {
            identParseAny(input).getOrElse { return SynResult.failure(it) }
        } else {
            return SynResult.failure(lookahead.error())
        }
    val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(
        Item.Const(attrs, vis, ConstModifiers(), constToken, ident, Generics.default(), colonToken, ty, eqToken, expr, semiToken),
    )
}

private fun parseItemFn(input: ParseStream): SynResult<Item.Fn> {
    val outerAttrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val sig = parseSignature(input, allowSafe = false).getOrElse { return SynResult.failure(it) }
    return parseRestOfFn(input, outerAttrs, vis, sig)
}

private fun parseItemMod(input: ParseStream): SynResult<Item.Mod> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }.toMutableList()
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
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
        return SynResult.success(Item.Mod(attrs, vis, unsafety, modToken, ident, ModContent.Unnamed(semi)))
    }
    if (!lookahead.peek(BracePeek)) {
        return SynResult.failure(lookahead.error())
    }
    val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
    parseInner(bracesVal.content, attrs).getOrElse {
        bracesVal.content.finishChildBuffer()
        return SynResult.failure(it)
    }
    val items = mutableListOf<Item>()
    while (!bracesVal.content.isEmpty()) {
        items.add(
            ItemParse.parse(bracesVal.content).getOrElse {
                bracesVal.content.finishChildBuffer()
                return SynResult.failure(it)
            },
        )
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Item.Mod(attrs, vis, unsafety, modToken, ident, ModContent.Inline(bracesVal.token, items)))
}

private fun parseItemForeignMod(input: ParseStream): SynResult<Item.ForeignMod> {
    val itemAttrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }.toMutableList()
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val abi = parseAbi(input).getOrElse { return SynResult.failure(it) }
    val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
    parseInner(bracesVal.content, itemAttrs).getOrElse {
        bracesVal.content.finishChildBuffer()
        return SynResult.failure(it)
    }
    val items = mutableListOf<ForeignItem>()
    while (!bracesVal.content.isEmpty()) {
        val item = parseForeignItem(bracesVal.content)
        if (item.isFailure) {
            bracesVal.content.finishChildBuffer()
            return asFailure(item)
        }
        items.add(item.getOrThrow())
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Item.ForeignMod(itemAttrs, unsafety, abi, bracesVal.token, items))
}

internal fun parseForeignItem(input: ParseStream): SynResult<ForeignItem> {
    val begin = input.cursor()
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val ahead = input.fork()
    val vis = VisibilityParse.parse(ahead).getOrElse { return SynResult.failure(it) }
    val lookahead = ahead.lookahead1()
    val allowSafe = true

    val item: ForeignItem =
        if (lookahead.peek(FnPeek) || peekSignature(ahead, allowSafe)) {
            val parsedVis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val sig = parseSignature(input, allowSafe = true).getOrElse { return SynResult.failure(it) }
            if (input.peek(BracePeek)) {
                val braces = braced(input).getOrElse { return SynResult.failure(it) }
                Attribute.parseInner(braces.content).getOrElse {
                    braces.content.finishChildBuffer()
                    return SynResult.failure(it)
                }
                parseWithin(braces.content).getOrElse {
                    braces.content.finishChildBuffer()
                    return SynResult.failure(it)
                }
                braces.content.finishChildBuffer()
                ForeignItem.Verbatim(between(begin, input.cursor()))
            } else {
                val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                ForeignItem.Fn(mutableListOf(), parsedVis, FnModifiers(), sig, semiToken)
            }
        } else if (lookahead.peek(StaticPeek) ||
            ((ahead.peek(UnsafePeek) || ahead.peek(SafePeek)) && ahead.peek2(StaticPeek))
        ) {
            val parsedVis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
            val safety = Safety.parseSafeOrUnsafe(input).getOrElse { return SynResult.failure(it) }
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
            if (hasValue) {
                ForeignItem.Verbatim(between(begin, input.cursor()))
            } else {
                ForeignItem.Static(mutableListOf(), parsedVis, safety, staticToken, mutability, ident, colonToken, ty, semiToken)
            }
        } else if (lookahead.peek(SynTypePeek)) {
            parseForeignItemType(begin, input).getOrElse { return SynResult.failure(it) }
        } else if (vis.isInherited() &&
            (
                lookahead.peek(IdentPeek) ||
                    lookahead.peek(SelfValuePeek) ||
                    lookahead.peek(SuperPeek) ||
                    lookahead.peek(CratePeek) ||
                    lookahead.peek(PathSepPeek)
            )
        ) {
            parseStandaloneForeignItemMacro(input).getOrElse { return SynResult.failure(it) }
        } else {
            return SynResult.failure(lookahead.error())
        }

    when (item) {
        is ForeignItem.Fn -> item.attrs = attrs + item.attrs
        is ForeignItem.Static -> item.attrs = attrs + item.attrs
        is ForeignItem.ItemType -> item.attrs = attrs + item.attrs
        is ForeignItem.Macro -> item.attrs = attrs + item.attrs
        is ForeignItem.Verbatim -> return SynResult.success(item)
    }
    return SynResult.success(item)
}

private fun parseStandaloneForeignItemFn(input: ParseStream): SynResult<ForeignItem.Fn> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val sig = parseSignature(input, allowSafe = true).getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(ForeignItem.Fn(attrs, vis, FnModifiers(), sig, semiToken))
}

private fun parseStandaloneForeignItemStatic(input: ParseStream): SynResult<ForeignItem.Static> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val safety = Safety.parseSafeOrUnsafe(input).getOrElse { return SynResult.failure(it) }
    val staticToken = StaticParse.parse(input).getOrElse { return SynResult.failure(it) }
    val mutability = StaticMutabilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(ForeignItem.Static(attrs, vis, safety, staticToken, mutability, ident, colonToken, ty, semiToken))
}

private fun parseStandaloneForeignItemType(input: ParseStream): SynResult<ForeignItem.ItemType> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(ForeignItem.ItemType(attrs, vis, TypeModifiers(), typeToken, ident, generics, semiToken))
}

private fun parseForeignItemType(
    begin: Cursor,
    input: ParseStream,
): SynResult<ForeignItem> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Disallowed,
                WhereClausePlacement.Early,
            ).getOrElse { return SynResult.failure(it) }

    return if (itemType.colonToken != null || itemType.ty != null) {
        SynResult.success(ForeignItem.Verbatim(between(begin, input.cursor())))
    } else {
        SynResult.success(
            ForeignItem.ItemType(
                mutableListOf(),
                itemType.vis,
                TypeModifiers(),
                itemType.typeToken,
                itemType.ident,
                itemType.generics,
                itemType.semiToken,
            ),
        )
    }
}

private fun parseStandaloneForeignItemMacro(input: ParseStream): SynResult<ForeignItem.Macro> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val mac = Macro.parse(input).getOrElse { return SynResult.failure(it) }
    val semiToken =
        if (mac.delimiter.isBrace) {
            null
        } else {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(ForeignItem.Macro(attrs, mac, semiToken))
}

private fun parseStandaloneItemType(input: ParseStream): SynResult<Item.ItemType> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val whereClausePlacement = parseLateWhereClause(generics, input, WhereClausePlacement.Early)
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(
        Item.ItemType(attrs, vis, TypeModifiers(), typeToken, ident, generics, eqToken, ty, semiToken, whereClausePlacement),
    )
}

private fun parseItemType(
    begin: Cursor,
    input: ParseStream,
): SynResult<Item> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Disallowed,
                WhereClausePlacement.Early,
            ).getOrElse { return SynResult.failure(it) }

    val ty = itemType.ty
    if (itemType.colonToken != null || ty == null) {
        return SynResult.success(Item.Verbatim(between(begin, input.cursor())))
    }

    return SynResult.success(
        Item.ItemType(
            mutableListOf(),
            itemType.vis,
            TypeModifiers(),
            itemType.typeToken,
            itemType.ident,
            itemType.generics,
            ty.eqToken,
            ty.type,
            itemType.semiToken,
            itemType.whereClausePlacement,
        ),
    )
}

private fun parseItemStruct(input: ParseStream): SynResult<Item.Struct> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val structToken = StructParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val data = dataStruct(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = data.whereClause
    return SynResult.success(Item.Struct(attrs, vis, structToken, ident, generics, data.fields, data.semiToken))
}

private fun parseItemEnum(input: ParseStream): SynResult<Item.Enum> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val enumToken = EnumParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val data = dataEnum(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = data.whereClause
    return SynResult.success(Item.Enum(attrs, vis, enumToken, ident, generics, data.braceToken, data.variants))
}

private fun parseItemUnion(input: ParseStream): SynResult<Item.Union> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val unionToken = UnionParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val data = dataUnion(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = data.whereClause
    return SynResult.success(Item.Union(attrs, vis, unionToken, ident, generics, data.fields))
}

private fun parseItemUse(
    input: ParseStream,
    allowCrateRootInPath: Boolean,
): SynResult<Item.Use?> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val useToken = UseParse.parse(input).getOrElse { return SynResult.failure(it) }
    val leadingColon = PathSepParse.parse(input).getOrNull()
    val tree =
        parseUseTree(input, allowCrateRootInPath && leadingColon == null)
            .getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }

    return if (tree == null) {
        SynResult.success(null)
    } else {
        SynResult.success(Item.Use(attrs, vis, useToken, leadingColon, tree, semiToken))
    }
}

private fun parseItemTrait(input: ParseStream): SynResult<Item.Trait> {
    val outerAttrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val autoToken = AutoParse.parse(input).getOrNull()
    val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    return parseRestOfTrait(input, outerAttrs, vis, unsafety, autoToken, traitToken, ident, generics)
}

private fun parseRestOfTrait(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    unsafety: Unsafe?,
    autoToken: Auto?,
    traitToken: io.github.kotlinmania.syn.token.Trait,
    ident: Ident,
    generics: Generics,
): SynResult<Item.Trait> {
    val colonToken = ColonParse.parse(input).getOrNull()
    val supertraits = TypeParamBoundList()
    if (colonToken != null) {
        while (true) {
            if (input.peek(WherePeek) || input.peek(BracePeek)) break
            val bound =
                TypeParamBound
                    .parseSingle(
                        input,
                        allowPreciseCapture = false,
                        allowConst = true,
                    ).getOrElse { return SynResult.failure(it) }
            supertraits.pushValue(bound)
            if (input.peek(WherePeek) || input.peek(BracePeek)) break
            supertraits.pushPunct(PlusParse.parse(input).getOrElse { return SynResult.failure(it) })
        }
    }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
    val traitAttrs = attrs.toMutableList()
    parseInner(bracesVal.content, traitAttrs).getOrElse {
        bracesVal.content.finishChildBuffer()
        return SynResult.failure(it)
    }
    val items = mutableListOf<TraitItem>()
    while (!bracesVal.content.isEmpty()) {
        items.add(
            parseTraitItem(bracesVal.content).getOrElse {
                bracesVal.content.finishChildBuffer()
                return SynResult.failure(it)
            },
        )
    }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(
        Item.Trait(
            traitAttrs,
            vis,
            TraitModifiers(autoToken),
            unsafety,
            traitToken,
            ident,
            generics,
            colonToken,
            supertraits,
            bracesVal.token,
            items,
        ),
    )
}

private fun parseTraitOrTraitAlias(
    input: ParseStream,
    attrs: List<Attribute>,
    vis: Visibility,
    hasImplRestriction: Boolean,
): SynResult<Item> {
    val unsafety = UnsafeParse.parse(input).getOrNull()
    val autoToken = AutoParse.parse(input).getOrNull()
    val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val lookahead = input.lookahead1()
    return when {
        hasImplRestriction ||
            unsafety != null ||
            autoToken != null ||
            lookahead.peek(BracePeek) ||
            lookahead.peek(ColonPeek) ||
            lookahead.peek(WherePeek)
        -> {
            parseRestOfTrait(
                input,
                attrs,
                vis,
                unsafety,
                autoToken,
                traitToken,
                ident,
                generics,
            ).map { it }
        }

        lookahead.peek(EqPeek) -> {
            parseRestOfTraitAlias(
                input,
                attrs,
                vis,
                traitToken,
                ident,
                generics,
            ).map { it }
        }

        else -> {
            SynResult.failure(lookahead.error())
        }
    }
}

private fun parseItemTraitAlias(input: ParseStream): SynResult<Item.TraitAlias> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val traitToken = TraitParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    return parseRestOfTraitAlias(input, attrs, vis, traitToken, ident, generics)
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
                    allowConst = true,
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

private fun parseStandaloneItemMacro(input: ParseStream): SynResult<Item.Macro> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val path = parseModStylePath(input).getOrElse { return SynResult.failure(it) }
    val bangToken = NotParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident =
        if (input.peek(TryPeek)) {
            identParseAny(input).getOrElse { return SynResult.failure(it) }
        } else {
            IdentParse.parse(input).getOrNull()
        }
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
    begin: Cursor,
    input: ParseStream,
): SynResult<Item> {
    MacroParse.parse(input).getOrElse { return SynResult.failure(it) }
    IdentParse.parse(input).getOrElse { return SynResult.failure(it) }

    var lookahead = input.lookahead1()
    if (lookahead.peek(ParenPeek)) {
        val parens = parenthesized(input).getOrElse { return SynResult.failure(it) }
        TokenStreamParse.parse(parens.content).getOrElse {
            parens.content.finishChildBuffer()
            return SynResult.failure(it)
        }
        parens.content.finishChildBuffer()
        lookahead = input.lookahead1()
    }

    if (lookahead.peek(BracePeek)) {
        val braces = braced(input).getOrElse { return SynResult.failure(it) }
        TokenStreamParse.parse(braces.content).getOrElse {
            braces.content.finishChildBuffer()
            return SynResult.failure(it)
        }
        braces.content.finishChildBuffer()
    } else {
        return SynResult.failure(lookahead.error())
    }

    return SynResult.success(Item.Verbatim(between(begin, input.cursor())))
}

private fun parseStandaloneItemImpl(input: ParseStream): SynResult<Item.Impl> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val defaultness = DefaultParse.parse(input).getOrNull()
    val unsafety = UnsafeParse.parse(input).getOrNull()
    return parseImpl(
        input,
        attrs,
        defaultness = defaultness,
        constness = null,
        unsafety = unsafety,
        allowVerbatimImpl = false,
    ).map { it!! }
}

private fun parseImpl(
    input: ParseStream,
    attrs: List<Attribute>,
    defaultness: Default?,
    constness: io.github.kotlinmania.syn.token.Const?,
    unsafety: Unsafe?,
    allowVerbatimImpl: Boolean,
): SynResult<Item.Impl?> {
    val implToken = ImplParse.parse(input).getOrElse { return SynResult.failure(it) }
    val hasGenerics = chooseGenericsOverQpath(input)
    val generics =
        if (hasGenerics) {
            parseGenerics(input).getOrElse { return SynResult.failure(it) }
        } else {
            Generics.default()
        }

    val polarity =
        if (input.peek(NotPeek) && !input.peek2(BracePeek)) {
            NotParse.parse(input).getOrElse { return SynResult.failure(it) }
        } else {
            null
        }

    val firstTyBegin = input.cursor()
    val firstTy = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val firstTyEnd = input.cursor()
    val selfType: SynType
    val traitPath: PathForPair?

    val isImplFor = input.peek(ForPeek)
    if (isImplFor) {
        val forToken = ForParse.parse(input).getOrElse { return SynResult.failure(it) }
        var firstTyRef = firstTy
        while (firstTyRef is SynType.Group) {
            firstTyRef = firstTyRef.elem
        }
        if (firstTyRef is SynType.Path && firstTyRef.qself == null) {
            traitPath = PathForPair(firstTyRef.path, forToken)
        } else if (!allowVerbatimImpl) {
            return SynResult.failure(SynError.newRange(firstTyBegin, firstTyEnd, "expected trait path"))
        } else {
            traitPath = null
        }
        selfType = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    } else if (polarity != null) {
        return SynResult.failure(SynError.new(polarity.span, "inherent impls cannot be negative"))
    } else {
        traitPath = null
        selfType = firstTy
    }

    generics.whereClause = parseWhereClause(input).getOrNull()
    val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
    val implAttrs = attrs.toMutableList()
    parseInner(bracesVal.content, implAttrs).getOrElse {
        bracesVal.content.finishChildBuffer()
        return SynResult.failure(it)
    }
    val items = mutableListOf<ImplItem>()
    while (!bracesVal.content.isEmpty()) {
        items.add(
            parseImplItem(bracesVal.content).getOrElse {
                bracesVal.content.finishChildBuffer()
                return SynResult.failure(it)
            },
        )
    }
    bracesVal.content.finishChildBuffer()

    return if (constness != null || (isImplFor && traitPath == null)) {
        SynResult.success(null)
    } else {
        SynResult.success(
            Item.Impl(
                implAttrs,
                ImplModifiers(defaultness, polarity),
                unsafety,
                implToken,
                generics,
                traitPath,
                selfType,
                bracesVal.token,
                items,
            ),
        )
    }
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
    val whereClausePlacement: WhereClausePlacement,
) {
    companion object {
        fun parse(
            input: ParseStream,
            allowDefaultness: TypeDefaultness,
            defaultWhereClausePlacement: WhereClausePlacement,
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

            if (defaultWhereClausePlacement == WhereClausePlacement.Early) {
                generics.whereClause = parseWhereClause(input).getOrNull()
            }

            val ty = parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }
            val whereClausePlacement = parseLateWhereClause(generics, input, defaultWhereClausePlacement)
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
                    whereClausePlacement,
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

private fun parseLateWhereClause(
    generics: Generics,
    input: ParseStream,
    defaultPlacement: WhereClausePlacement,
): WhereClausePlacement {
    if (generics.whereClause != null) {
        return WhereClausePlacement.Early
    }
    generics.whereClause = parseWhereClause(input).getOrNull()
    return if (generics.whereClause != null) {
        WhereClausePlacement.Late
    } else {
        defaultPlacement
    }
}

private fun <T, R> asFailure(result: SynResult<T>): SynResult<R> =
    SynResult.failure((result as SynResult.Failure).error)

private fun Visibility.isInherited(): Boolean =
    this is Visibility.Inherited

internal fun peekSignature(input: ParseStream, allowSafe: Boolean = false): Boolean {
    val fork = input.fork()
    ConstParse.parse(fork)
    AsyncParse.parse(fork)
    if (allowSafe && fork.peek(SafePeek)) {
        SafeParse.parse(fork)
    } else {
        UnsafeParse.parse(fork)
    }
    parseAbi(fork)
    return fork.peek(FnPeek)
}

public fun parseSignature(input: ParseStream): SynResult<Signature> =
    parseSignature(input, allowSafe = false)

internal fun parseSignature(input: ParseStream, allowSafe: Boolean): SynResult<Signature> {
    val constness = ConstParse.parse(input).getOrNull()
    val asyncness = AsyncParse.parse(input).getOrNull()
    val safety =
        if (allowSafe) {
            Safety.parseSafeOrUnsafe(input)
        } else {
            Safety.parseUnsafeOnly(input)
        }.getOrElse { return SynResult.failure(it) }
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
    if (inputsResult.isFailure) {
        parensVal.content.finishChildBuffer()
        return asFailure(inputsResult)
    }
    val (inputs, variadic) = inputsResult.getOrThrow()
    parensVal.content.finishChildBuffer()
    val outputResult = parseReturnType(input)
    if (outputResult.isFailure) return asFailure(outputResult)
    generics.whereClause = parseWhereClause(input).getOrNull()
    return SynResult.success(
        Signature(
            constness,
            asyncness,
            safety,
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
            if (hasReceiver) {
                return SynResult.failure(SynError.new(arg.selfToken.span, "unexpected second method receiver"))
            }
            if (!inputs.isEmpty()) {
                return SynResult.failure(SynError.new(arg.selfToken.span, "unexpected method receiver"))
            }
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
    parseInner(bracesVal.content, itemAttrs).getOrElse {
        bracesVal.content.finishChildBuffer()
        return SynResult.failure(it)
    }
    val stmtsResult = parseWithin(bracesVal.content)
    if (stmtsResult.isFailure) {
        bracesVal.content.finishChildBuffer()
        return asFailure(stmtsResult)
    }
    val stmts = stmtsResult.getOrThrow()
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Item.Fn(itemAttrs, vis, FnModifiers(), sig, Block(bracesVal.token, stmts)))
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
    val receiverBeginResult = parseReceiverBegin(ahead)
    if (receiverBeginResult.isSuccess) {
        input.advanceTo(ahead)
        val beginParts = receiverBeginResult.getOrThrow()
        val receiver =
            parseRestOfReceiver(beginParts.reference, beginParts.mutability, beginParts.selfToken, input)
                .getOrElse { return SynResult.failure(it) }
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

    val patResult = parsePatSingle(input)
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

private data class ReceiverBeginParts(
    val reference: AndLifetime?,
    val mutability: Mut?,
    val selfToken: SelfValue,
)

private fun parseReceiver(input: ParseStream): SynResult<FnArg.Receiver> {
    val beginParts = parseReceiverBegin(input).getOrElse { return SynResult.failure(it) }
    return parseRestOfReceiver(beginParts.reference, beginParts.mutability, beginParts.selfToken, input)
}

private fun parseReceiverBegin(input: ParseStream): SynResult<ReceiverBeginParts> {
    val reference =
        if (input.peek(AndPeek)) {
            val andToken = AndParse.parse(input).getOrElse { return SynResult.failure(it) }
            val lifetime = Lifetime.parseOptionalAny(input)
            AndLifetime(andToken, lifetime)
        } else {
            null
        }
    val mutability = MutParse.parse(input).getOrNull()
    val selfToken = SelfValueParse.parse(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(PathSepPeek)) {
        return SynResult.failure(input.error("expected `:`"))
    }
    return SynResult.success(ReceiverBeginParts(reference, mutability, selfToken))
}

private fun parseRestOfReceiver(
    reference: AndLifetime?,
    mutability: Mut?,
    selfToken: SelfValue,
    input: ParseStream,
): SynResult<FnArg.Receiver> {
    val colonToken =
        if (reference != null) {
            null
        } else {
            ColonParse.parse(input).getOrNull()
        }
    var outerMutability = mutability
    val kind =
        if (colonToken != null) {
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            ReceiverKind.Typed(colonToken, ty)
        } else if (reference != null) {
            val refMutability = outerMutability
            outerMutability = null
            ReceiverKind.Reference(reference.andToken, reference.lifetime, refMutability)
        } else {
            ReceiverKind.Value
        }
    return SynResult.success(FnArg.Receiver(mutableListOf(), outerMutability, selfToken, kind))
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
                    PathSepParse.parse(bracesVal.content).getOrElse {
                        bracesVal.content.finishChildBuffer()
                        return SynResult.failure(it)
                    }
                    true
                } else {
                    false
                }
            hasAnyCrateRootInPath = hasAnyCrateRootInPath || thisTreeStartsWithCrateRoot
            val tree =
                parseUseTree(
                    bracesVal.content,
                    allowCrateRootInPath && !thisTreeStartsWithCrateRoot,
                ).getOrElse {
                    bracesVal.content.finishChildBuffer()
                    return SynResult.failure(it)
                }
            if (tree != null && !hasAnyCrateRootInPath) {
                items.pushValue(tree)
            } else {
                hasAnyCrateRootInPath = true
            }
            if (bracesVal.content.isEmpty()) break
            val comma =
                CommaParse.parse(bracesVal.content).getOrElse {
                    bracesVal.content.finishChildBuffer()
                    return SynResult.failure(it)
                }
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
    val begin = input.cursor()
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val defaultness = DefaultParse.parse(input).getOrNull()
    val ahead = input.fork()
    val lookahead = ahead.lookahead1()
    val allowSafe = false

    val item: TraitItem =
        if (lookahead.peek(FnPeek) || peekSignature(ahead, allowSafe)) {
            parseStandaloneTraitItemFn(input).getOrElse { return SynResult.failure(it) }
        } else if (lookahead.peek(ConstPeek)) {
            val constToken = ConstParse.parse(ahead).getOrElse { return SynResult.failure(it) }
            val constLookahead = ahead.lookahead1()
            if (constLookahead.peek(IdentPeek) || constLookahead.peek(UnderscorePeek)) {
                input.advanceTo(ahead)
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
                generics.whereClause = parseWhereClause(input).getOrNull()
                val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
                if (generics.ltToken == null && generics.whereClause == null) {
                    TraitItem.Const(mutableListOf(), ConstModifiers(), constToken, ident, generics, colon, ty, default, semi)
                } else {
                    return SynResult.success(TraitItem.Verbatim(between(begin, input.cursor())))
                }
            } else if (constLookahead.peek(AsyncPeek) ||
                constLookahead.peek(UnsafePeek) ||
                constLookahead.peek(ExternPeek) ||
                constLookahead.peek(FnPeek)
            ) {
                parseStandaloneTraitItemFn(input).getOrElse { return SynResult.failure(it) }
            } else {
                return SynResult.failure(constLookahead.error())
            }
        } else if (lookahead.peek(SynTypePeek)) {
            parseTraitItemType(begin, input).getOrElse { return SynResult.failure(it) }
        } else if (vis.isInherited() &&
            defaultness == null &&
            (
                lookahead.peek(IdentPeek) ||
                    lookahead.peek(SelfValuePeek) ||
                    lookahead.peek(SuperPeek) ||
                    lookahead.peek(CratePeek) ||
                    lookahead.peek(PathSepPeek)
            )
        ) {
            parseStandaloneTraitItemMacro(input).getOrElse { return SynResult.failure(it) }
        } else {
            return SynResult.failure(lookahead.error())
        }

    if (!vis.isInherited() || defaultness != null) {
        return SynResult.success(TraitItem.Verbatim(between(begin, input.cursor())))
    }
    when (item) {
        is TraitItem.Const -> item.attrs = attrs + item.attrs
        is TraitItem.Fn -> item.attrs = attrs + item.attrs
        is TraitItem.AssocType -> item.attrs = attrs + item.attrs
        is TraitItem.Macro -> item.attrs = attrs + item.attrs
        is TraitItem.Verbatim -> return SynResult.success(item)
    }
    return SynResult.success(item)
}

private fun parseStandaloneTraitItemConst(input: ParseStream): SynResult<TraitItem.Const> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
    val lookahead = input.lookahead1()
    val ident =
        if (lookahead.peek(IdentPeek) || lookahead.peek(UnderscorePeek)) {
            identParseAny(input).getOrElse { return SynResult.failure(it) }
        } else {
            return SynResult.failure(lookahead.error())
        }
    val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val default =
        if (input.peek(EqPeek)) {
            val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
            val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
            EqExpr(eqToken, expr)
        } else {
            null
        }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(
        TraitItem.Const(attrs, ConstModifiers(), constToken, ident, Generics.default(), colonToken, ty, default, semiToken),
    )
}

private fun parseStandaloneTraitItemFn(input: ParseStream): SynResult<TraitItem.Fn> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }.toMutableList()
    val sig = parseSignature(input, allowSafe = false).getOrElse { return SynResult.failure(it) }
    val lookahead = input.lookahead1()
    return if (lookahead.peek(BracePeek)) {
        val braces = braced(input).getOrElse { return SynResult.failure(it) }
        parseInner(braces.content, attrs).getOrElse {
            braces.content.finishChildBuffer()
            return SynResult.failure(it)
        }
        val stmts =
            parseWithin(braces.content).getOrElse {
                braces.content.finishChildBuffer()
                return SynResult.failure(it)
            }
        braces.content.finishChildBuffer()
        SynResult.success(TraitItem.Fn(attrs, FnModifiers(), sig, Block(braces.token, stmts), null))
    } else if (lookahead.peek(SemiPeek)) {
        val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        SynResult.success(TraitItem.Fn(attrs, FnModifiers(), sig, null, semiToken))
    } else {
        SynResult.failure(lookahead.error())
    }
}

private fun parseStandaloneTraitItemType(input: ParseStream): SynResult<TraitItem.AssocType> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val (colonToken, bounds) = FlexibleItemType.parseOptionalBounds(input).getOrElse { return SynResult.failure(it) }
    val default = FlexibleItemType.parseOptionalDefinition(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(
        TraitItem.AssocType(attrs, TypeModifiers(), typeToken, ident, generics, colonToken, bounds, default, semiToken),
    )
}

private fun parseStandaloneTraitItemMacro(input: ParseStream): SynResult<TraitItem.Macro> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val mac = Macro.parse(input).getOrElse { return SynResult.failure(it) }
    val semiToken =
        if (mac.delimiter.isBrace) {
            null
        } else {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(TraitItem.Macro(attrs, mac, semiToken))
}

internal fun parseImplItem(input: ParseStream): SynResult<ImplItem> {
    val begin = input.cursor()
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val ahead = input.fork()
    val vis = VisibilityParse.parse(ahead).getOrElse { return SynResult.failure(it) }
    var lookahead = ahead.lookahead1()
    val defaultness =
        if (lookahead.peek(DefaultPeek) && !ahead.peek2(NotPeek)) {
            val parsedDefault = DefaultParse.parse(ahead).getOrElse { return SynResult.failure(it) }
            lookahead = ahead.lookahead1()
            parsedDefault
        } else {
            null
        }

    val allowSafe = false
    val item: ImplItem =
        if (lookahead.peek(FnPeek) || peekSignature(ahead, allowSafe)) {
            parseImplItemFn(input, allowOmittedBody = true).getOrElse { return SynResult.failure(it) }
                ?: return SynResult.success(ImplItem.Verbatim(between(begin, input.cursor())))
        } else if (lookahead.peek(ConstPeek)) {
            input.advanceTo(ahead)
            val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
            val constLookahead = input.lookahead1()
            if (!constLookahead.peek(IdentPeek) && !constLookahead.peek(UnderscorePeek)) {
                return SynResult.failure(constLookahead.error())
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
            generics.whereClause = parseWhereClause(input).getOrNull()
            val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
            return if (value != null && generics.ltToken == null && generics.whereClause == null) {
                SynResult.success(
                    ImplItem.Const(
                        attrs,
                        vis,
                        ConstModifiers(defaultness),
                        constToken,
                        ident,
                        generics,
                        colon,
                        ty,
                        value.eqToken,
                        value.expr,
                        semi,
                    ),
                )
            } else {
                SynResult.success(ImplItem.Verbatim(between(begin, input.cursor())))
            }
        } else if (lookahead.peek(SynTypePeek)) {
            parseImplItemType(begin, input).getOrElse { return SynResult.failure(it) }
        } else if (vis.isInherited() &&
            defaultness == null &&
            (
                lookahead.peek(IdentPeek) ||
                    lookahead.peek(SelfValuePeek) ||
                    lookahead.peek(SuperPeek) ||
                    lookahead.peek(CratePeek) ||
                    lookahead.peek(PathSepPeek)
            )
        ) {
            parseStandaloneImplItemMacro(input).getOrElse { return SynResult.failure(it) }
        } else {
            return SynResult.failure(lookahead.error())
        }

    when (item) {
        is ImplItem.Const -> item.attrs = attrs + item.attrs
        is ImplItem.Fn -> item.attrs = attrs + item.attrs
        is ImplItem.AssocType -> item.attrs = attrs + item.attrs
        is ImplItem.Macro -> item.attrs = attrs + item.attrs
        is ImplItem.Verbatim -> return SynResult.success(item)
    }
    return SynResult.success(item)
}

private fun parseStandaloneImplItemConst(input: ParseStream): SynResult<ImplItem.Const> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val defaultness = DefaultParse.parse(input).getOrNull()
    val constToken = ConstParse.parse(input).getOrElse { return SynResult.failure(it) }
    val lookahead = input.lookahead1()
    val ident =
        if (lookahead.peek(IdentPeek) || lookahead.peek(UnderscorePeek)) {
            identParseAny(input).getOrElse { return SynResult.failure(it) }
        } else {
            return SynResult.failure(lookahead.error())
        }
    val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(
        ImplItem.Const(attrs, vis, ConstModifiers(defaultness), constToken, ident, Generics.default(), colonToken, ty, eqToken, expr, semiToken),
    )
}

private fun parseStandaloneImplItemType(input: ParseStream): SynResult<ImplItem.AssocType> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val vis = VisibilityParse.parse(input).getOrElse { return SynResult.failure(it) }
    val defaultness = DefaultParse.parse(input).getOrNull()
    val typeToken = SynTypeParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ident = IdentParse.parse(input).getOrElse { return SynResult.failure(it) }
    val generics = parseGenerics(input).getOrElse { return SynResult.failure(it) }
    val eqToken = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
    val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
    generics.whereClause = parseWhereClause(input).getOrNull()
    val semiToken = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(
        ImplItem.AssocType(attrs, vis, TypeModifiers(defaultness), typeToken, ident, generics, eqToken, ty, semiToken),
    )
}

private fun parseStandaloneImplItemMacro(input: ParseStream): SynResult<ImplItem.Macro> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val mac = Macro.parse(input).getOrElse { return SynResult.failure(it) }
    val semiToken =
        if (mac.delimiter.isBrace) {
            null
        } else {
            SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
        }
    return SynResult.success(ImplItem.Macro(attrs, mac, semiToken))
}

private fun parseTraitItemType(begin: Cursor, input: ParseStream): SynResult<TraitItem> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Disallowed,
                WhereClausePlacement.Late,
            ).getOrElse { return SynResult.failure(it) }
    if (!itemType.vis.isInherited()) {
        return SynResult.success(TraitItem.Verbatim(between(begin, input.cursor())))
    }
    return SynResult.success(
        TraitItem.AssocType(
            mutableListOf(),
            TypeModifiers(),
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

private fun parseImplItemType(begin: Cursor, input: ParseStream): SynResult<ImplItem> {
    val itemType =
        FlexibleItemType
            .parse(
                input,
                TypeDefaultness.Optional,
                WhereClausePlacement.Late,
            ).getOrElse { return SynResult.failure(it) }
    val ty = itemType.ty
    if (itemType.colonToken != null || ty == null) {
        return SynResult.success(ImplItem.Verbatim(between(begin, input.cursor())))
    }
    return SynResult.success(
        ImplItem.AssocType(
            mutableListOf(),
            itemType.vis,
            TypeModifiers(itemType.defaultness),
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
    val sig = parseSignature(input, allowSafe = false).getOrElse { return SynResult.failure(it) }

    if (allowOmittedBody && SemiParse.parse(input).isSuccess) {
        return SynResult.success(null)
    }

    val bracesResult = braced(input)
    if (bracesResult.isFailure) return asFailure(bracesResult)
    val bracesVal = bracesResult.getOrThrow()
    parseInner(bracesVal.content, attrs).getOrElse {
        bracesVal.content.finishChildBuffer()
        return SynResult.failure(it)
    }
    val stmtsResult = parseWithin(bracesVal.content)
    if (stmtsResult.isFailure) {
        bracesVal.content.finishChildBuffer()
        return asFailure(stmtsResult)
    }
    val stmts = stmtsResult.getOrThrow()
    bracesVal.content.finishChildBuffer()

    return SynResult.success(ImplItem.Fn(attrs, vis, FnModifiers(defaultness), sig, Block(bracesVal.token, stmts)))
}
