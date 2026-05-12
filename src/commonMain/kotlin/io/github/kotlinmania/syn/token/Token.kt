// port-lint: source token.rs
package io.github.kotlinmania.syn.token

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group as ProcMacroGroup
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.syn.intoDelimSpan

/**
 * Tokens representing Rust punctuation, keywords, and delimiters.
 *
 * The type names in this package can be difficult to keep straight, so callers
 * usually use the root token facade when constructing syntax tree fields.
 */
interface Token

interface SingleSpanToken : Token {
    val span: Span
}

interface MultiSpanToken : Token {
    val spans: List<Span>
}

abstract class KeywordToken(
    final override val span: Span,
) : SingleSpanToken, ToTokens {
    protected abstract val text: String

    override fun toTokens(tokens: TokenStream) {
        printingKeyword(text, span, tokens)
    }

    override fun equals(other: Any?): Boolean =
        other != null && other::class == this::class

    override fun hashCode(): Int =
        this::class.hashCode()

    override fun toString(): String =
        this::class.simpleName ?: text
}

abstract class PunctuationToken(
    final override val spans: List<Span>,
    private val text: String,
) : MultiSpanToken, ToTokens {
    init {
        require(spans.size == text.length) { "expected ${text.length} span(s)" }
    }

    val span: Span
        get() = spans.first()

    override fun toTokens(tokens: TokenStream) {
        printingPunct(text, spans, tokens)
    }

    override fun equals(other: Any?): Boolean =
        other != null && other::class == this::class

    override fun hashCode(): Int =
        this::class.hashCode()

    override fun toString(): String =
        this::class.simpleName ?: text
}

private fun callSiteSpans(count: Int): List<Span> =
    List(count) { Span.callSite() }

private fun repeatedSpans(span: Span, count: Int): List<Span> =
    List(count) { span }

/** `_` */
class Underscore private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "_") {
    companion object {
        fun default(): Underscore =
            Underscore(callSiteSpans(1))

        fun from(span: Span): Underscore =
            Underscore(repeatedSpans(span, 1))

        operator fun invoke(span: Span): Underscore =
            from(span)
    }

    override fun toTokens(tokens: TokenStream) {
        tokens.append(Ident.new("_", span))
    }
}

/** None-delimited group. */
class Group private constructor(
    override val span: Span,
) : SingleSpanToken {
    companion object {
        fun default(): Group =
            Group(Span.callSite())

        fun from(span: Span): Group =
            Group(span)

        operator fun invoke(span: Span): Group =
            from(span)
    }

    fun surround(tokens: TokenStream, f: (TokenStream) -> Unit) {
        val inner = TokenStream.new()
        f(inner)
        printingDelim(Delimiter.None, span, tokens, inner)
    }

    override fun equals(other: Any?): Boolean =
        other is Group

    override fun hashCode(): Int =
        Group::class.hashCode()

    override fun toString(): String =
        "Group"
}

abstract class DelimiterToken(
    val span: DelimSpan,
    private val delimiter: Delimiter,
) : Token {
    fun surround(tokens: TokenStream, f: (TokenStream) -> Unit) {
        val inner = TokenStream.new()
        f(inner)
        printingDelim(delimiter, span.join(), tokens, inner)
    }

    override fun equals(other: Any?): Boolean =
        other != null && other::class == this::class

    override fun hashCode(): Int =
        this::class.hashCode()

    override fun toString(): String =
        this::class.simpleName ?: delimiter.name
}

/** `{`...`}` */
class Brace private constructor(
    span: DelimSpan,
) : DelimiterToken(span, Delimiter.Brace) {
    companion object {
        fun default(): Brace =
            Brace(Span.callSite().intoDelimSpan())

        fun from(span: Span): Brace =
            Brace(span.intoDelimSpan())

        fun from(span: DelimSpan): Brace =
            Brace(span)

        operator fun invoke(span: Span): Brace =
            from(span)
    }
}

/** `[`...`]` */
class Bracket private constructor(
    span: DelimSpan,
) : DelimiterToken(span, Delimiter.Bracket) {
    companion object {
        fun default(): Bracket =
            Bracket(Span.callSite().intoDelimSpan())

        fun from(span: Span): Bracket =
            Bracket(span.intoDelimSpan())

        fun from(span: DelimSpan): Bracket =
            Bracket(span)

        operator fun invoke(span: Span): Bracket =
            from(span)
    }
}

/** `(`...`)` */
class Paren private constructor(
    span: DelimSpan,
) : DelimiterToken(span, Delimiter.Parenthesis) {
    companion object {
        fun default(): Paren =
            Paren(Span.callSite().intoDelimSpan())

        fun from(span: Span): Paren =
            Paren(span.intoDelimSpan())

        fun from(span: DelimSpan): Paren =
            Paren(span)

        operator fun invoke(span: Span): Paren =
            from(span)
    }
}

class Abstract private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "abstract"
    companion object {
        fun default(): Abstract = Abstract(Span.callSite())
        fun from(span: Span): Abstract = Abstract(span)
        operator fun invoke(span: Span): Abstract = from(span)
    }
}

class As private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "as"
    companion object {
        fun default(): As = As(Span.callSite())
        fun from(span: Span): As = As(span)
        operator fun invoke(span: Span): As = from(span)
    }
}

class Async private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "async"
    companion object {
        fun default(): Async = Async(Span.callSite())
        fun from(span: Span): Async = Async(span)
        operator fun invoke(span: Span): Async = from(span)
    }
}

class Auto private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "auto"
    companion object {
        fun default(): Auto = Auto(Span.callSite())
        fun from(span: Span): Auto = Auto(span)
        operator fun invoke(span: Span): Auto = from(span)
    }
}

class Await private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "await"
    companion object {
        fun default(): Await = Await(Span.callSite())
        fun from(span: Span): Await = Await(span)
        operator fun invoke(span: Span): Await = from(span)
    }
}

class Become private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "become"
    companion object {
        fun default(): Become = Become(Span.callSite())
        fun from(span: Span): Become = Become(span)
        operator fun invoke(span: Span): Become = from(span)
    }
}

class Box private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "box"
    companion object {
        fun default(): Box = Box(Span.callSite())
        fun from(span: Span): Box = Box(span)
        operator fun invoke(span: Span): Box = from(span)
    }
}

class Break private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "break"
    companion object {
        fun default(): Break = Break(Span.callSite())
        fun from(span: Span): Break = Break(span)
        operator fun invoke(span: Span): Break = from(span)
    }
}

class Const private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "const"
    companion object {
        fun default(): Const = Const(Span.callSite())
        fun from(span: Span): Const = Const(span)
        operator fun invoke(span: Span): Const = from(span)
    }
}

class Continue private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "continue"
    companion object {
        fun default(): Continue = Continue(Span.callSite())
        fun from(span: Span): Continue = Continue(span)
        operator fun invoke(span: Span): Continue = from(span)
    }
}

class Crate private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "crate"
    companion object {
        fun default(): Crate = Crate(Span.callSite())
        fun from(span: Span): Crate = Crate(span)
        operator fun invoke(span: Span): Crate = from(span)
    }
}

class Default private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "default"
    companion object {
        fun default(): Default = Default(Span.callSite())
        fun from(span: Span): Default = Default(span)
        operator fun invoke(span: Span): Default = from(span)
    }
}

class Do private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "do"
    companion object {
        fun default(): Do = Do(Span.callSite())
        fun from(span: Span): Do = Do(span)
        operator fun invoke(span: Span): Do = from(span)
    }
}

class Dyn private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "dyn"
    companion object {
        fun default(): Dyn = Dyn(Span.callSite())
        fun from(span: Span): Dyn = Dyn(span)
        operator fun invoke(span: Span): Dyn = from(span)
    }
}

class Else private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "else"
    companion object {
        fun default(): Else = Else(Span.callSite())
        fun from(span: Span): Else = Else(span)
        operator fun invoke(span: Span): Else = from(span)
    }
}

class Enum private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "enum"
    companion object {
        fun default(): Enum = Enum(Span.callSite())
        fun from(span: Span): Enum = Enum(span)
        operator fun invoke(span: Span): Enum = from(span)
    }
}

class Extern private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "extern"
    companion object {
        fun default(): Extern = Extern(Span.callSite())
        fun from(span: Span): Extern = Extern(span)
        operator fun invoke(span: Span): Extern = from(span)
    }
}

class Final private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "final"
    companion object {
        fun default(): Final = Final(Span.callSite())
        fun from(span: Span): Final = Final(span)
        operator fun invoke(span: Span): Final = from(span)
    }
}

class Fn private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "fn"
    companion object {
        fun default(): Fn = Fn(Span.callSite())
        fun from(span: Span): Fn = Fn(span)
        operator fun invoke(span: Span): Fn = from(span)
    }
}

class For private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "for"
    companion object {
        fun default(): For = For(Span.callSite())
        fun from(span: Span): For = For(span)
        operator fun invoke(span: Span): For = from(span)
    }
}

class If private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "if"
    companion object {
        fun default(): If = If(Span.callSite())
        fun from(span: Span): If = If(span)
        operator fun invoke(span: Span): If = from(span)
    }
}

class Impl private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "impl"
    companion object {
        fun default(): Impl = Impl(Span.callSite())
        fun from(span: Span): Impl = Impl(span)
        operator fun invoke(span: Span): Impl = from(span)
    }
}

class In private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "in"
    companion object {
        fun default(): In = In(Span.callSite())
        fun from(span: Span): In = In(span)
        operator fun invoke(span: Span): In = from(span)
    }
}

class Let private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "let"
    companion object {
        fun default(): Let = Let(Span.callSite())
        fun from(span: Span): Let = Let(span)
        operator fun invoke(span: Span): Let = from(span)
    }
}

class Loop private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "loop"
    companion object {
        fun default(): Loop = Loop(Span.callSite())
        fun from(span: Span): Loop = Loop(span)
        operator fun invoke(span: Span): Loop = from(span)
    }
}

class Macro private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "macro"
    companion object {
        fun default(): Macro = Macro(Span.callSite())
        fun from(span: Span): Macro = Macro(span)
        operator fun invoke(span: Span): Macro = from(span)
    }
}

class Match private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "match"
    companion object {
        fun default(): Match = Match(Span.callSite())
        fun from(span: Span): Match = Match(span)
        operator fun invoke(span: Span): Match = from(span)
    }
}

class Mod private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "mod"
    companion object {
        fun default(): Mod = Mod(Span.callSite())
        fun from(span: Span): Mod = Mod(span)
        operator fun invoke(span: Span): Mod = from(span)
    }
}

class Move private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "move"
    companion object {
        fun default(): Move = Move(Span.callSite())
        fun from(span: Span): Move = Move(span)
        operator fun invoke(span: Span): Move = from(span)
    }
}

class Mut private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "mut"
    companion object {
        fun default(): Mut = Mut(Span.callSite())
        fun from(span: Span): Mut = Mut(span)
        operator fun invoke(span: Span): Mut = from(span)
    }
}

class Override private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "override"
    companion object {
        fun default(): Override = Override(Span.callSite())
        fun from(span: Span): Override = Override(span)
        operator fun invoke(span: Span): Override = from(span)
    }
}

class Priv private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "priv"
    companion object {
        fun default(): Priv = Priv(Span.callSite())
        fun from(span: Span): Priv = Priv(span)
        operator fun invoke(span: Span): Priv = from(span)
    }
}

class Pub private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "pub"
    companion object {
        fun default(): Pub = Pub(Span.callSite())
        fun from(span: Span): Pub = Pub(span)
        operator fun invoke(span: Span): Pub = from(span)
    }
}

class Raw private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "raw"
    companion object {
        fun default(): Raw = Raw(Span.callSite())
        fun from(span: Span): Raw = Raw(span)
        operator fun invoke(span: Span): Raw = from(span)
    }
}

class Ref private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "ref"
    companion object {
        fun default(): Ref = Ref(Span.callSite())
        fun from(span: Span): Ref = Ref(span)
        operator fun invoke(span: Span): Ref = from(span)
    }
}

class Return private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "return"
    companion object {
        fun default(): Return = Return(Span.callSite())
        fun from(span: Span): Return = Return(span)
        operator fun invoke(span: Span): Return = from(span)
    }
}

class SelfType private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "Self"
    companion object {
        fun default(): SelfType = SelfType(Span.callSite())
        fun from(span: Span): SelfType = SelfType(span)
        operator fun invoke(span: Span): SelfType = from(span)
    }
}

class SelfValue private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "self"
    companion object {
        fun default(): SelfValue = SelfValue(Span.callSite())
        fun from(span: Span): SelfValue = SelfValue(span)
        operator fun invoke(span: Span): SelfValue = from(span)
    }
}

class Static private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "static"
    companion object {
        fun default(): Static = Static(Span.callSite())
        fun from(span: Span): Static = Static(span)
        operator fun invoke(span: Span): Static = from(span)
    }
}

class Struct private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "struct"
    companion object {
        fun default(): Struct = Struct(Span.callSite())
        fun from(span: Span): Struct = Struct(span)
        operator fun invoke(span: Span): Struct = from(span)
    }
}

class Super private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "super"
    companion object {
        fun default(): Super = Super(Span.callSite())
        fun from(span: Span): Super = Super(span)
        operator fun invoke(span: Span): Super = from(span)
    }
}

class Trait private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "trait"
    companion object {
        fun default(): Trait = Trait(Span.callSite())
        fun from(span: Span): Trait = Trait(span)
        operator fun invoke(span: Span): Trait = from(span)
    }
}

class Try private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "try"
    companion object {
        fun default(): Try = Try(Span.callSite())
        fun from(span: Span): Try = Try(span)
        operator fun invoke(span: Span): Try = from(span)
    }
}

class Type private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "type"
    companion object {
        fun default(): Type = Type(Span.callSite())
        fun from(span: Span): Type = Type(span)
        operator fun invoke(span: Span): Type = from(span)
    }
}

class Typeof private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "typeof"
    companion object {
        fun default(): Typeof = Typeof(Span.callSite())
        fun from(span: Span): Typeof = Typeof(span)
        operator fun invoke(span: Span): Typeof = from(span)
    }
}

class Union private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "union"
    companion object {
        fun default(): Union = Union(Span.callSite())
        fun from(span: Span): Union = Union(span)
        operator fun invoke(span: Span): Union = from(span)
    }
}

class Unsafe private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "unsafe"
    companion object {
        fun default(): Unsafe = Unsafe(Span.callSite())
        fun from(span: Span): Unsafe = Unsafe(span)
        operator fun invoke(span: Span): Unsafe = from(span)
    }
}

class Unsized private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "unsized"
    companion object {
        fun default(): Unsized = Unsized(Span.callSite())
        fun from(span: Span): Unsized = Unsized(span)
        operator fun invoke(span: Span): Unsized = from(span)
    }
}

class Use private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "use"
    companion object {
        fun default(): Use = Use(Span.callSite())
        fun from(span: Span): Use = Use(span)
        operator fun invoke(span: Span): Use = from(span)
    }
}

class Virtual private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "virtual"
    companion object {
        fun default(): Virtual = Virtual(Span.callSite())
        fun from(span: Span): Virtual = Virtual(span)
        operator fun invoke(span: Span): Virtual = from(span)
    }
}

class Where private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "where"
    companion object {
        fun default(): Where = Where(Span.callSite())
        fun from(span: Span): Where = Where(span)
        operator fun invoke(span: Span): Where = from(span)
    }
}

class While private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "while"
    companion object {
        fun default(): While = While(Span.callSite())
        fun from(span: Span): While = While(span)
        operator fun invoke(span: Span): While = from(span)
    }
}

class Yield private constructor(span: Span) : KeywordToken(span) {
    override val text: String = "yield"
    companion object {
        fun default(): Yield = Yield(Span.callSite())
        fun from(span: Span): Yield = Yield(span)
        operator fun invoke(span: Span): Yield = from(span)
    }
}

class And private constructor(spans: List<Span>) : PunctuationToken(spans, "&") {
    companion object {
        fun default(): And = And(callSiteSpans(1))
        fun from(span: Span): And = And(repeatedSpans(span, 1))
        fun from(spans: List<Span>): And = And(spans)
        operator fun invoke(span: Span): And = from(span)
    }
}

class AndAnd private constructor(spans: List<Span>) : PunctuationToken(spans, "&&") {
    companion object {
        fun default(): AndAnd = AndAnd(callSiteSpans(2))
        fun from(span: Span): AndAnd = AndAnd(repeatedSpans(span, 2))
        fun from(spans: List<Span>): AndAnd = AndAnd(spans)
        operator fun invoke(span: Span): AndAnd = from(span)
    }
}

class AndEq private constructor(spans: List<Span>) : PunctuationToken(spans, "&=") {
    companion object {
        fun default(): AndEq = AndEq(callSiteSpans(2))
        fun from(span: Span): AndEq = AndEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): AndEq = AndEq(spans)
        operator fun invoke(span: Span): AndEq = from(span)
    }
}

class At private constructor(spans: List<Span>) : PunctuationToken(spans, "@") {
    companion object {
        fun default(): At = At(callSiteSpans(1))
        fun from(span: Span): At = At(repeatedSpans(span, 1))
        fun from(spans: List<Span>): At = At(spans)
        operator fun invoke(span: Span): At = from(span)
    }
}

class Caret private constructor(spans: List<Span>) : PunctuationToken(spans, "^") {
    companion object {
        fun default(): Caret = Caret(callSiteSpans(1))
        fun from(span: Span): Caret = Caret(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Caret = Caret(spans)
        operator fun invoke(span: Span): Caret = from(span)
    }
}

class CaretEq private constructor(spans: List<Span>) : PunctuationToken(spans, "^=") {
    companion object {
        fun default(): CaretEq = CaretEq(callSiteSpans(2))
        fun from(span: Span): CaretEq = CaretEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): CaretEq = CaretEq(spans)
        operator fun invoke(span: Span): CaretEq = from(span)
    }
}

class Colon private constructor(spans: List<Span>) : PunctuationToken(spans, ":") {
    companion object {
        fun default(): Colon = Colon(callSiteSpans(1))
        fun from(span: Span): Colon = Colon(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Colon = Colon(spans)
        operator fun invoke(span: Span): Colon = from(span)
    }
}

class Comma private constructor(spans: List<Span>) : PunctuationToken(spans, ",") {
    companion object {
        fun default(): Comma = Comma(callSiteSpans(1))
        fun from(span: Span): Comma = Comma(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Comma = Comma(spans)
        operator fun invoke(span: Span): Comma = from(span)
    }
}

class Dollar private constructor(spans: List<Span>) : PunctuationToken(spans, "\$") {
    companion object {
        fun default(): Dollar = Dollar(callSiteSpans(1))
        fun from(span: Span): Dollar = Dollar(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Dollar = Dollar(spans)
        operator fun invoke(span: Span): Dollar = from(span)
    }
}

class Dot private constructor(spans: List<Span>) : PunctuationToken(spans, ".") {
    companion object {
        fun default(): Dot = Dot(callSiteSpans(1))
        fun from(span: Span): Dot = Dot(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Dot = Dot(spans)
        operator fun invoke(span: Span): Dot = from(span)
    }
}

class DotDot private constructor(spans: List<Span>) : PunctuationToken(spans, "..") {
    companion object {
        fun default(): DotDot = DotDot(callSiteSpans(2))
        fun from(span: Span): DotDot = DotDot(repeatedSpans(span, 2))
        fun from(spans: List<Span>): DotDot = DotDot(spans)
        operator fun invoke(span: Span): DotDot = from(span)
    }
}

class DotDotDot private constructor(spans: List<Span>) : PunctuationToken(spans, "...") {
    companion object {
        fun default(): DotDotDot = DotDotDot(callSiteSpans(3))
        fun from(span: Span): DotDotDot = DotDotDot(repeatedSpans(span, 3))
        fun from(spans: List<Span>): DotDotDot = DotDotDot(spans)
        operator fun invoke(span: Span): DotDotDot = from(span)
    }
}

class DotDotEq private constructor(spans: List<Span>) : PunctuationToken(spans, "..=") {
    companion object {
        fun default(): DotDotEq = DotDotEq(callSiteSpans(3))
        fun from(span: Span): DotDotEq = DotDotEq(repeatedSpans(span, 3))
        fun from(spans: List<Span>): DotDotEq = DotDotEq(spans)
        operator fun invoke(span: Span): DotDotEq = from(span)
    }
}

class Eq private constructor(spans: List<Span>) : PunctuationToken(spans, "=") {
    companion object {
        fun default(): Eq = Eq(callSiteSpans(1))
        fun from(span: Span): Eq = Eq(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Eq = Eq(spans)
        operator fun invoke(span: Span): Eq = from(span)
    }
}

class EqEq private constructor(spans: List<Span>) : PunctuationToken(spans, "==") {
    companion object {
        fun default(): EqEq = EqEq(callSiteSpans(2))
        fun from(span: Span): EqEq = EqEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): EqEq = EqEq(spans)
        operator fun invoke(span: Span): EqEq = from(span)
    }
}

class FatArrow private constructor(spans: List<Span>) : PunctuationToken(spans, "=>") {
    companion object {
        fun default(): FatArrow = FatArrow(callSiteSpans(2))
        fun from(span: Span): FatArrow = FatArrow(repeatedSpans(span, 2))
        fun from(spans: List<Span>): FatArrow = FatArrow(spans)
        operator fun invoke(span: Span): FatArrow = from(span)
    }
}

class Ge private constructor(spans: List<Span>) : PunctuationToken(spans, ">=") {
    companion object {
        fun default(): Ge = Ge(callSiteSpans(2))
        fun from(span: Span): Ge = Ge(repeatedSpans(span, 2))
        fun from(spans: List<Span>): Ge = Ge(spans)
        operator fun invoke(span: Span): Ge = from(span)
    }
}

class Gt private constructor(spans: List<Span>) : PunctuationToken(spans, ">") {
    companion object {
        fun default(): Gt = Gt(callSiteSpans(1))
        fun from(span: Span): Gt = Gt(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Gt = Gt(spans)
        operator fun invoke(span: Span): Gt = from(span)
    }
}

class LArrow private constructor(spans: List<Span>) : PunctuationToken(spans, "<-") {
    companion object {
        fun default(): LArrow = LArrow(callSiteSpans(2))
        fun from(span: Span): LArrow = LArrow(repeatedSpans(span, 2))
        fun from(spans: List<Span>): LArrow = LArrow(spans)
        operator fun invoke(span: Span): LArrow = from(span)
    }
}

class Le private constructor(spans: List<Span>) : PunctuationToken(spans, "<=") {
    companion object {
        fun default(): Le = Le(callSiteSpans(2))
        fun from(span: Span): Le = Le(repeatedSpans(span, 2))
        fun from(spans: List<Span>): Le = Le(spans)
        operator fun invoke(span: Span): Le = from(span)
    }
}

class Lt private constructor(spans: List<Span>) : PunctuationToken(spans, "<") {
    companion object {
        fun default(): Lt = Lt(callSiteSpans(1))
        fun from(span: Span): Lt = Lt(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Lt = Lt(spans)
        operator fun invoke(span: Span): Lt = from(span)
    }
}

class Minus private constructor(spans: List<Span>) : PunctuationToken(spans, "-") {
    companion object {
        fun default(): Minus = Minus(callSiteSpans(1))
        fun from(span: Span): Minus = Minus(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Minus = Minus(spans)
        operator fun invoke(span: Span): Minus = from(span)
    }
}

class MinusEq private constructor(spans: List<Span>) : PunctuationToken(spans, "-=") {
    companion object {
        fun default(): MinusEq = MinusEq(callSiteSpans(2))
        fun from(span: Span): MinusEq = MinusEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): MinusEq = MinusEq(spans)
        operator fun invoke(span: Span): MinusEq = from(span)
    }
}

class Ne private constructor(spans: List<Span>) : PunctuationToken(spans, "!=") {
    companion object {
        fun default(): Ne = Ne(callSiteSpans(2))
        fun from(span: Span): Ne = Ne(repeatedSpans(span, 2))
        fun from(spans: List<Span>): Ne = Ne(spans)
        operator fun invoke(span: Span): Ne = from(span)
    }
}

class Not private constructor(spans: List<Span>) : PunctuationToken(spans, "!") {
    companion object {
        fun default(): Not = Not(callSiteSpans(1))
        fun from(span: Span): Not = Not(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Not = Not(spans)
        operator fun invoke(span: Span): Not = from(span)
    }
}

class Or private constructor(spans: List<Span>) : PunctuationToken(spans, "|") {
    companion object {
        fun default(): Or = Or(callSiteSpans(1))
        fun from(span: Span): Or = Or(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Or = Or(spans)
        operator fun invoke(span: Span): Or = from(span)
    }
}

class OrEq private constructor(spans: List<Span>) : PunctuationToken(spans, "|=") {
    companion object {
        fun default(): OrEq = OrEq(callSiteSpans(2))
        fun from(span: Span): OrEq = OrEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): OrEq = OrEq(spans)
        operator fun invoke(span: Span): OrEq = from(span)
    }
}

class OrOr private constructor(spans: List<Span>) : PunctuationToken(spans, "||") {
    companion object {
        fun default(): OrOr = OrOr(callSiteSpans(2))
        fun from(span: Span): OrOr = OrOr(repeatedSpans(span, 2))
        fun from(spans: List<Span>): OrOr = OrOr(spans)
        operator fun invoke(span: Span): OrOr = from(span)
    }
}

class PathSep private constructor(spans: List<Span>) : PunctuationToken(spans, "::") {
    companion object {
        fun default(): PathSep = PathSep(callSiteSpans(2))
        fun from(span: Span): PathSep = PathSep(repeatedSpans(span, 2))
        fun from(spans: List<Span>): PathSep = PathSep(spans)
        operator fun invoke(span: Span): PathSep = from(span)
    }
}

class Percent private constructor(spans: List<Span>) : PunctuationToken(spans, "%") {
    companion object {
        fun default(): Percent = Percent(callSiteSpans(1))
        fun from(span: Span): Percent = Percent(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Percent = Percent(spans)
        operator fun invoke(span: Span): Percent = from(span)
    }
}

class PercentEq private constructor(spans: List<Span>) : PunctuationToken(spans, "%=") {
    companion object {
        fun default(): PercentEq = PercentEq(callSiteSpans(2))
        fun from(span: Span): PercentEq = PercentEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): PercentEq = PercentEq(spans)
        operator fun invoke(span: Span): PercentEq = from(span)
    }
}

class Plus private constructor(spans: List<Span>) : PunctuationToken(spans, "+") {
    companion object {
        fun default(): Plus = Plus(callSiteSpans(1))
        fun from(span: Span): Plus = Plus(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Plus = Plus(spans)
        operator fun invoke(span: Span): Plus = from(span)
    }
}

class PlusEq private constructor(spans: List<Span>) : PunctuationToken(spans, "+=") {
    companion object {
        fun default(): PlusEq = PlusEq(callSiteSpans(2))
        fun from(span: Span): PlusEq = PlusEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): PlusEq = PlusEq(spans)
        operator fun invoke(span: Span): PlusEq = from(span)
    }
}

class Pound private constructor(spans: List<Span>) : PunctuationToken(spans, "#") {
    companion object {
        fun default(): Pound = Pound(callSiteSpans(1))
        fun from(span: Span): Pound = Pound(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Pound = Pound(spans)
        operator fun invoke(span: Span): Pound = from(span)
    }
}

class Question private constructor(spans: List<Span>) : PunctuationToken(spans, "?") {
    companion object {
        fun default(): Question = Question(callSiteSpans(1))
        fun from(span: Span): Question = Question(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Question = Question(spans)
        operator fun invoke(span: Span): Question = from(span)
    }
}

class RArrow private constructor(spans: List<Span>) : PunctuationToken(spans, "->") {
    companion object {
        fun default(): RArrow = RArrow(callSiteSpans(2))
        fun from(span: Span): RArrow = RArrow(repeatedSpans(span, 2))
        fun from(spans: List<Span>): RArrow = RArrow(spans)
        operator fun invoke(span: Span): RArrow = from(span)
    }
}

class Semi private constructor(spans: List<Span>) : PunctuationToken(spans, ";") {
    companion object {
        fun default(): Semi = Semi(callSiteSpans(1))
        fun from(span: Span): Semi = Semi(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Semi = Semi(spans)
        operator fun invoke(span: Span): Semi = from(span)
    }
}

class Shl private constructor(spans: List<Span>) : PunctuationToken(spans, "<<") {
    companion object {
        fun default(): Shl = Shl(callSiteSpans(2))
        fun from(span: Span): Shl = Shl(repeatedSpans(span, 2))
        fun from(spans: List<Span>): Shl = Shl(spans)
        operator fun invoke(span: Span): Shl = from(span)
    }
}

class ShlEq private constructor(spans: List<Span>) : PunctuationToken(spans, "<<=") {
    companion object {
        fun default(): ShlEq = ShlEq(callSiteSpans(3))
        fun from(span: Span): ShlEq = ShlEq(repeatedSpans(span, 3))
        fun from(spans: List<Span>): ShlEq = ShlEq(spans)
        operator fun invoke(span: Span): ShlEq = from(span)
    }
}

class Shr private constructor(spans: List<Span>) : PunctuationToken(spans, ">>") {
    companion object {
        fun default(): Shr = Shr(callSiteSpans(2))
        fun from(span: Span): Shr = Shr(repeatedSpans(span, 2))
        fun from(spans: List<Span>): Shr = Shr(spans)
        operator fun invoke(span: Span): Shr = from(span)
    }
}

class ShrEq private constructor(spans: List<Span>) : PunctuationToken(spans, ">>=") {
    companion object {
        fun default(): ShrEq = ShrEq(callSiteSpans(3))
        fun from(span: Span): ShrEq = ShrEq(repeatedSpans(span, 3))
        fun from(spans: List<Span>): ShrEq = ShrEq(spans)
        operator fun invoke(span: Span): ShrEq = from(span)
    }
}

class Slash private constructor(spans: List<Span>) : PunctuationToken(spans, "/") {
    companion object {
        fun default(): Slash = Slash(callSiteSpans(1))
        fun from(span: Span): Slash = Slash(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Slash = Slash(spans)
        operator fun invoke(span: Span): Slash = from(span)
    }
}

class SlashEq private constructor(spans: List<Span>) : PunctuationToken(spans, "/=") {
    companion object {
        fun default(): SlashEq = SlashEq(callSiteSpans(2))
        fun from(span: Span): SlashEq = SlashEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): SlashEq = SlashEq(spans)
        operator fun invoke(span: Span): SlashEq = from(span)
    }
}

class Star private constructor(spans: List<Span>) : PunctuationToken(spans, "*") {
    companion object {
        fun default(): Star = Star(callSiteSpans(1))
        fun from(span: Span): Star = Star(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Star = Star(spans)
        operator fun invoke(span: Span): Star = from(span)
    }
}

class StarEq private constructor(spans: List<Span>) : PunctuationToken(spans, "*=") {
    companion object {
        fun default(): StarEq = StarEq(callSiteSpans(2))
        fun from(span: Span): StarEq = StarEq(repeatedSpans(span, 2))
        fun from(spans: List<Span>): StarEq = StarEq(spans)
        operator fun invoke(span: Span): StarEq = from(span)
    }
}

class Tilde private constructor(spans: List<Span>) : PunctuationToken(spans, "~") {
    companion object {
        fun default(): Tilde = Tilde(callSiteSpans(1))
        fun from(span: Span): Tilde = Tilde(repeatedSpans(span, 1))
        fun from(spans: List<Span>): Tilde = Tilde(spans)
        operator fun invoke(span: Span): Tilde = from(span)
    }
}

private fun printingPunct(s: String, spans: List<Span>, tokens: TokenStream) {
    require(s.length == spans.size)

    val chars = s.toList()
    for (index in 0 until chars.lastIndex) {
        tokens.append(Punct(chars[index], Spacing.Joint, spans[index]))
    }
    tokens.append(Punct(chars.last(), Spacing.Alone, spans.last()))
}

private fun printingKeyword(s: String, span: Span, tokens: TokenStream) {
    tokens.append(Ident.new(s, span))
}

private fun printingDelim(
    delim: Delimiter,
    span: Span,
    tokens: TokenStream,
    inner: TokenStream,
) {
    val group = ProcMacroGroup(delim, inner)
    group.setSpan(span)
    tokens.append(group)
}
