// port-lint: source macros.rs
package io.github.kotlinmania.syn

internal const val CFG_FEATURE_FULL: Boolean = true

internal const val CFG_FEATURE_DERIVE: Boolean = true

internal const val CFG_FEATURE_PRINTING: Boolean = true

internal const val CFG_FEATURE_PARSING: Boolean = true

internal const val CFG_FEATURE_CLONE_IMPLS: Boolean = true

internal const val CFG_FEATURE_EXTRA_TRAITS: Boolean = true

internal const val CFG_DOC: Boolean = false

private const val AST_STRUCT_TEMPLATE: String =
    "checkKeywordMatches(public \$pub); checkKeywordMatches(class \$struct); \$attr \$pub \$struct \$name \$body"

private const val AST_STRUCT_FALLBACK_TEMPLATE: String =
    "checkKeywordMatches(public \$pub); checkKeywordMatches(class \$struct); \$attr \$pub \$struct \$name { noconstruct: Span }"

private const val AST_ENUM_TEMPLATE: String =
    "checkKeywordMatches(public \$pub); checkKeywordMatches(sealed class \$enumVal); \$enumAttr \$pub \$enumVal \$name \$body"

private const val AST_ENUM_OF_STRUCTS_TEMPLATE: String =
    "checkKeywordMatches(public \$pub); checkKeywordMatches(sealed class \$enumVal); \$enumAttr \$pub \$enumVal \$name \$body; " +
        "astEnumOfStructsImpl(\$name \$body); generateToTokens(() tokens \$name \$body);"

private const val AST_ENUM_OF_STRUCTS_IMPL_TEMPLATE: String =
    "astEnumFromStruct(\$name, \$variant, \$member, \$cfgAttr, \$docAttr);"

private const val AST_ENUM_FROM_STRUCT_TEMPLATE: String =
    "class From<\$member> : \$name { fun from(e: \$member): \$name = \$name.\$variant(e) }"

private const val GENERATE_TO_TOKENS_UNIT_TEMPLATE: String =
    "generateToTokens((\$arms \$cfgAttr \$docAttr \$name.\$variant -> {}) \$tokens \$name { \$next });"

private const val GENERATE_TO_TOKENS_MEMBER_TEMPLATE: String =
    "generateToTokens((\$arms \$cfgAttr \$docAttr \$name.\$variant(e) -> e.toTokens(\$tokens)) \$tokens \$name { \$next });"

private const val GENERATE_TO_TOKENS_FINAL_TEMPLATE: String =
    "class ToTokensImpl : ToTokens { fun toTokens(\$tokens: TokenStream) { when (\$name) { \$arms } } }"

private const val PUB_IF_NOT_DOC_TEMPLATE: String =
    "checkKeywordMatches(public \$pub); \$m \$pub \$item"

private const val RETURN_IMPL_TRAIT_TEMPLATE: String =
    "\$attr \$vis fun \$name(\$args): \$implTrait \$body"

private const val RETURN_CONCRETE_TEMPLATE: String =
    "\$attr \$vis fun \$name(\$args): \$concrete \$body"

internal fun astStruct(
    attr: String = "",
    pub: String = "",
    struct: String = "",
    name: String = "",
    body: String = "",
    clause: String = "",
): String =
    when {
        clause == "@full" && !CFG_FEATURE_FULL && name.isNotEmpty() ->
            AST_STRUCT_FALLBACK_TEMPLATE
                .replace("\$attr", attr)
                .replace("\$pub", pub)
                .replace("\$struct", struct)
                .replace("\$name", name)
        pub.isNotEmpty() && struct.isNotEmpty() && name.isNotEmpty() && body.isNotEmpty() ->
            AST_STRUCT_TEMPLATE
                .replace("\$attr", attr)
                .replace("\$pub", pub)
                .replace("\$struct", struct)
                .replace("\$name", name)
                .replace("\$body", body)
        else -> ""
    }

internal fun astEnum(
    enumAttr: String = "",
    pub: String = "",
    enumVal: String = "",
    name: String = "",
    body: String = "",
): String =
    if (CFG_FEATURE_FULL || CFG_FEATURE_DERIVE) {
        AST_ENUM_TEMPLATE
            .replace("\$enumAttr", enumAttr)
            .replace("\$pub", pub)
            .replace("\$enumVal", enumVal)
            .replace("\$name", name)
            .replace("\$body", body)
    } else {
        ""
    }

internal fun astEnumOfStructs(
    enumAttr: String = "",
    pub: String = "",
    enumVal: String = "",
    name: String = "",
    body: String = "",
): String =
    AST_ENUM_OF_STRUCTS_TEMPLATE
        .replace("\$enumAttr", enumAttr)
        .replace("\$pub", pub)
        .replace("\$enumVal", enumVal)
        .replace("\$name", name)
        .replace("\$body", body)

internal fun astEnumOfStructsImpl(
    name: String = "",
    cfgAttr: String = "",
    docAttr: String = "",
    variant: String = "",
    member: String = "",
): String =
    AST_ENUM_OF_STRUCTS_IMPL_TEMPLATE
        .replace("\$name", name)
        .replace("\$variant", variant)
        .replace("\$member", member)
        .replace("\$cfgAttr", cfgAttr)
        .replace("\$docAttr", docAttr)

internal fun astEnumFromStruct(
    name: String = "",
    member: String = "",
    variant: String = "",
    clause: String = "",
): String =
    when {
        clause == "Verbatim" && name.isNotEmpty() && member.isNotEmpty() -> ""
        name.isNotEmpty() && variant.isNotEmpty() && member.isNotEmpty() ->
            AST_ENUM_FROM_STRUCT_TEMPLATE
                .replace("\$name", name)
                .replace("\$variant", variant)
                .replace("\$member", member)
        else -> ""
    }

internal fun generateToTokens(
    arms: String = "",
    tokens: String = "",
    name: String = "",
    cfgAttr: String = "",
    docAttr: String = "",
    variant: String = "",
    next: String = "",
    member: String = "",
): String =
    if (CFG_FEATURE_PRINTING) {
        when {
            arms.isNotEmpty() && tokens.isNotEmpty() && name.isNotEmpty() && variant.isNotEmpty() && member.isNotEmpty() ->
                GENERATE_TO_TOKENS_MEMBER_TEMPLATE
                    .replace("\$arms", arms)
                    .replace("\$cfgAttr", cfgAttr)
                    .replace("\$docAttr", docAttr)
                    .replace("\$tokens", tokens)
                    .replace("\$name", name)
                    .replace("\$variant", variant)
                    .replace("\$next", next)
            arms.isNotEmpty() && tokens.isNotEmpty() && name.isNotEmpty() && variant.isNotEmpty() ->
                GENERATE_TO_TOKENS_UNIT_TEMPLATE
                    .replace("\$arms", arms)
                    .replace("\$cfgAttr", cfgAttr)
                    .replace("\$docAttr", docAttr)
                    .replace("\$tokens", tokens)
                    .replace("\$name", name)
                    .replace("\$variant", variant)
                    .replace("\$next", next)
            arms.isNotEmpty() && tokens.isNotEmpty() && name.isNotEmpty() ->
                GENERATE_TO_TOKENS_FINAL_TEMPLATE
                    .replace("\$arms", arms)
                    .replace("\$tokens", tokens)
                    .replace("\$name", name)
            else -> ""
        }
    } else {
        ""
    }

internal fun pubIfNotDoc(
    m: String = "",
    pub: String = "",
    item: String = "",
): String =
    if (!CFG_DOC && CFG_FEATURE_PARSING) {
        PUB_IF_NOT_DOC_TEMPLATE
            .replace("\$m", m)
            .replace("\$pub", pub)
            .replace("\$item", item)
    } else {
        item
    }

internal fun checkKeywordMatches(clause: String = ""): Boolean =
    when (clause) {
        "sealed class sealed class", "public public", "class class" -> true
        else -> false
    }

internal fun returnImplTrait(
    attr: String = "",
    vis: String = "",
    name: String = "",
    args: String = "",
    implTrait: String = "",
    concrete: String = "",
    body: String = "",
): String =
    if (CFG_FEATURE_FULL || CFG_FEATURE_DERIVE) {
        RETURN_IMPL_TRAIT_TEMPLATE
            .replace("\$attr", attr)
            .replace("\$vis", vis)
            .replace("\$name", name)
            .replace("\$args", args)
            .replace("\$implTrait", implTrait)
            .replace("\$body", body)
    } else {
        RETURN_CONCRETE_TEMPLATE
            .replace("\$attr", attr)
            .replace("\$vis", vis)
            .replace("\$name", name)
            .replace("\$args", args)
            .replace("\$concrete", concrete)
            .replace("\$body", body)
    }
