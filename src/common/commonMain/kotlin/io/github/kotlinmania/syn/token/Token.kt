// port-lint: source token.rs

package io.github.kotlinmania.syn.token

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.syn.Cursor
import io.github.kotlinmania.syn.SpanIntoDelimSpan
import io.github.kotlinmania.procmacro2.Group as ProcMacroGroup

/**
 * Tokens representing punctuation, keywords, and delimiters.
 *
 * The type names in this package can be difficult to keep straight, so callers
 * usually use the root token facade when constructing syntax tree fields.
 */
sealed interface Sealed

/**
 * Marker trait for types that represent single tokens.
 *
 * This trait is sealed and cannot be implemented for types outside of Syn.
 */
sealed interface Token : Sealed

/**
 * Support writing `token.span` rather than `token.spans[0]` on tokens that
 * hold a single span.
 */
data class WithSpan(
    var span: Span,
)

sealed interface CustomToken : Token {
    fun peek(cursor: Cursor): Boolean

    fun display(): String
}

sealed interface SingleSpanToken : Token {
    val span: Span
}

sealed interface MultiSpanToken : Token {
    val spans: List<Span>
}

sealed class KeywordToken(
    final override val span: Span,
) : SingleSpanToken,
    ToTokens {
    protected abstract val text: String

    override fun toTokens(tokens: TokenStream) {
        printingKeyword(text, span, tokens)
    }

    override fun equals(other: Any?): Boolean =
        other != null && other::class == this::class

    override fun hashCode(): Int =
        this::class.hashCode()

    override fun toString(): String =
        "Token![$text]"
}

sealed class PunctuationToken(
    final override val spans: List<Span>,
    private val text: String,
) : MultiSpanToken,
    ToTokens {
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
        "Token![$text]"
}

private fun callSiteSpans(count: Int): List<Span> =
    List(count) { Span.callSite() }

private fun repeatedSpans(span: Span, count: Int): List<Span> =
    List(count) { span }

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
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
class Group(
    override val span: Span,
) : SingleSpanToken,
    ToTokens {
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

    fun clone(): Group =
        Group(span)

    fun fmt(): String = toString()

    fun eq(other: Group): Boolean = equals(other)

    fun hash(): Int = hashCode()

    override fun toTokens(tokens: TokenStream) {
        surround(tokens) { }
    }

    override fun equals(other: Any?): Boolean =
        other is Group

    override fun hashCode(): Int =
        Group::class.hashCode()

    override fun toString(): String =
        "Group"
}

public fun group(span: Span): Group =
    Group.from(span)

sealed class DelimiterToken(
    var span: DelimSpan,
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
            Brace(SpanIntoDelimSpan(Span.callSite()).intoSpans())

        fun from(span: Span): Brace =
            Brace(SpanIntoDelimSpan(span).intoSpans())

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
            Bracket(SpanIntoDelimSpan(Span.callSite()).intoSpans())

        fun from(span: Span): Bracket =
            Bracket(SpanIntoDelimSpan(span).intoSpans())

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
            Paren(SpanIntoDelimSpan(Span.callSite()).intoSpans())

        fun from(span: Span): Paren =
            Paren(SpanIntoDelimSpan(span).intoSpans())

        fun from(span: DelimSpan): Paren =
            Paren(span)

        operator fun invoke(span: Span): Paren =
            from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Abstract private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "abstract"

    companion object {
        fun default(): Abstract = Abstract(Span.callSite())

        fun from(span: Span): Abstract = Abstract(span)

        operator fun invoke(span: Span): Abstract = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class As private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "as"

    companion object {
        fun default(): As = As(Span.callSite())

        fun from(span: Span): As = As(span)

        operator fun invoke(span: Span): As = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Async private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "async"

    companion object {
        fun default(): Async = Async(Span.callSite())

        fun from(span: Span): Async = Async(span)

        operator fun invoke(span: Span): Async = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Auto private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "auto"

    companion object {
        fun default(): Auto = Auto(Span.callSite())

        fun from(span: Span): Auto = Auto(span)

        operator fun invoke(span: Span): Auto = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Await private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "await"

    companion object {
        fun default(): Await = Await(Span.callSite())

        fun from(span: Span): Await = Await(span)

        operator fun invoke(span: Span): Await = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Become private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "become"

    companion object {
        fun default(): Become = Become(Span.callSite())

        fun from(span: Span): Become = Become(span)

        operator fun invoke(span: Span): Become = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Box private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "box"

    companion object {
        fun default(): Box = Box(Span.callSite())

        fun from(span: Span): Box = Box(span)

        operator fun invoke(span: Span): Box = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Break private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "break"

    companion object {
        fun default(): Break = Break(Span.callSite())

        fun from(span: Span): Break = Break(span)

        operator fun invoke(span: Span): Break = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Const private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "const"

    companion object {
        fun default(): Const = Const(Span.callSite())

        fun from(span: Span): Const = Const(span)

        operator fun invoke(span: Span): Const = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Continue private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "continue"

    companion object {
        fun default(): Continue = Continue(Span.callSite())

        fun from(span: Span): Continue = Continue(span)

        operator fun invoke(span: Span): Continue = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Crate private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "crate"

    companion object {
        fun default(): Crate = Crate(Span.callSite())

        fun from(span: Span): Crate = Crate(span)

        operator fun invoke(span: Span): Crate = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Default private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "default"

    companion object {
        fun default(): Default = Default(Span.callSite())

        fun from(span: Span): Default = Default(span)

        operator fun invoke(span: Span): Default = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Do private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "do"

    companion object {
        fun default(): Do = Do(Span.callSite())

        fun from(span: Span): Do = Do(span)

        operator fun invoke(span: Span): Do = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Dyn private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "dyn"

    companion object {
        fun default(): Dyn = Dyn(Span.callSite())

        fun from(span: Span): Dyn = Dyn(span)

        operator fun invoke(span: Span): Dyn = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Else private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "else"

    companion object {
        fun default(): Else = Else(Span.callSite())

        fun from(span: Span): Else = Else(span)

        operator fun invoke(span: Span): Else = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Enum private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "enum"

    companion object {
        fun default(): Enum = Enum(Span.callSite())

        fun from(span: Span): Enum = Enum(span)

        operator fun invoke(span: Span): Enum = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Extern private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "extern"

    companion object {
        fun default(): Extern = Extern(Span.callSite())

        fun from(span: Span): Extern = Extern(span)

        operator fun invoke(span: Span): Extern = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Final private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "final"

    companion object {
        fun default(): Final = Final(Span.callSite())

        fun from(span: Span): Final = Final(span)

        operator fun invoke(span: Span): Final = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Fn private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "fn"

    companion object {
        fun default(): Fn = Fn(Span.callSite())

        fun from(span: Span): Fn = Fn(span)

        operator fun invoke(span: Span): Fn = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class For private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "for"

    companion object {
        fun default(): For = For(Span.callSite())

        fun from(span: Span): For = For(span)

        operator fun invoke(span: Span): For = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class If private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "if"

    companion object {
        fun default(): If = If(Span.callSite())

        fun from(span: Span): If = If(span)

        operator fun invoke(span: Span): If = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Impl private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "impl"

    companion object {
        fun default(): Impl = Impl(Span.callSite())

        fun from(span: Span): Impl = Impl(span)

        operator fun invoke(span: Span): Impl = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class In private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "in"

    companion object {
        fun default(): In = In(Span.callSite())

        fun from(span: Span): In = In(span)

        operator fun invoke(span: Span): In = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Let private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "let"

    companion object {
        fun default(): Let = Let(Span.callSite())

        fun from(span: Span): Let = Let(span)

        operator fun invoke(span: Span): Let = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Loop private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "loop"

    companion object {
        fun default(): Loop = Loop(Span.callSite())

        fun from(span: Span): Loop = Loop(span)

        operator fun invoke(span: Span): Loop = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Macro private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "macro"

    companion object {
        fun default(): Macro = Macro(Span.callSite())

        fun from(span: Span): Macro = Macro(span)

        operator fun invoke(span: Span): Macro = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Match private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "match"

    companion object {
        fun default(): Match = Match(Span.callSite())

        fun from(span: Span): Match = Match(span)

        operator fun invoke(span: Span): Match = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Mod private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "mod"

    companion object {
        fun default(): Mod = Mod(Span.callSite())

        fun from(span: Span): Mod = Mod(span)

        operator fun invoke(span: Span): Mod = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Move private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "move"

    companion object {
        fun default(): Move = Move(Span.callSite())

        fun from(span: Span): Move = Move(span)

        operator fun invoke(span: Span): Move = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Mut private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "mut"

    companion object {
        fun default(): Mut = Mut(Span.callSite())

        fun from(span: Span): Mut = Mut(span)

        operator fun invoke(span: Span): Mut = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Override private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "override"

    companion object {
        fun default(): Override = Override(Span.callSite())

        fun from(span: Span): Override = Override(span)

        operator fun invoke(span: Span): Override = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Priv private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "priv"

    companion object {
        fun default(): Priv = Priv(Span.callSite())

        fun from(span: Span): Priv = Priv(span)

        operator fun invoke(span: Span): Priv = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Pub private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "pub"

    companion object {
        fun default(): Pub = Pub(Span.callSite())

        fun from(span: Span): Pub = Pub(span)

        operator fun invoke(span: Span): Pub = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Raw private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "raw"

    companion object {
        fun default(): Raw = Raw(Span.callSite())

        fun from(span: Span): Raw = Raw(span)

        operator fun invoke(span: Span): Raw = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Ref private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "ref"

    companion object {
        fun default(): Ref = Ref(Span.callSite())

        fun from(span: Span): Ref = Ref(span)

        operator fun invoke(span: Span): Ref = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Return private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "return"

    companion object {
        fun default(): Return = Return(Span.callSite())

        fun from(span: Span): Return = Return(span)

        operator fun invoke(span: Span): Return = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Safe private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "safe"

    companion object {
        fun default(): Safe = Safe(Span.callSite())

        fun from(span: Span): Safe = Safe(span)

        operator fun invoke(span: Span): Safe = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class SelfType private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "Self"

    companion object {
        fun default(): SelfType = SelfType(Span.callSite())

        fun from(span: Span): SelfType = SelfType(span)

        operator fun invoke(span: Span): SelfType = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class SelfValue private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "self"

    companion object {
        fun default(): SelfValue = SelfValue(Span.callSite())

        fun from(span: Span): SelfValue = SelfValue(span)

        operator fun invoke(span: Span): SelfValue = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Static private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "static"

    companion object {
        fun default(): Static = Static(Span.callSite())

        fun from(span: Span): Static = Static(span)

        operator fun invoke(span: Span): Static = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Struct private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "struct"

    companion object {
        fun default(): Struct = Struct(Span.callSite())

        fun from(span: Span): Struct = Struct(span)

        operator fun invoke(span: Span): Struct = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Super private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "super"

    companion object {
        fun default(): Super = Super(Span.callSite())

        fun from(span: Span): Super = Super(span)

        operator fun invoke(span: Span): Super = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Trait private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "trait"

    companion object {
        fun default(): Trait = Trait(Span.callSite())

        fun from(span: Span): Trait = Trait(span)

        operator fun invoke(span: Span): Trait = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Try private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "try"

    companion object {
        fun default(): Try = Try(Span.callSite())

        fun from(span: Span): Try = Try(span)

        operator fun invoke(span: Span): Try = from(span)
    }
}

/**
 * The type-alias keyword token.
 *
 * Named SynTypeToken to avoid colliding with Swift's built-in Type
 * metatype expression, which the Swift compiler rejects
 * as a type member named Type. The SynTypeToken class
 * preserves the original Kotlin API name.
 */
class SynTypeToken private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "type"

    companion object {
        fun default(): SynTypeToken = SynTypeToken(Span.callSite())

        fun from(span: Span): SynTypeToken = SynTypeToken(span)

        operator fun invoke(span: Span): SynTypeToken = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Typeof private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "typeof"

    companion object {
        fun default(): Typeof = Typeof(Span.callSite())

        fun from(span: Span): Typeof = Typeof(span)

        operator fun invoke(span: Span): Typeof = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Union private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "union"

    companion object {
        fun default(): Union = Union(Span.callSite())

        fun from(span: Span): Union = Union(span)

        operator fun invoke(span: Span): Union = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Unsafe private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "unsafe"

    companion object {
        fun default(): Unsafe = Unsafe(Span.callSite())

        fun from(span: Span): Unsafe = Unsafe(span)

        operator fun invoke(span: Span): Unsafe = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Unsized private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "unsized"

    companion object {
        fun default(): Unsized = Unsized(Span.callSite())

        fun from(span: Span): Unsized = Unsized(span)

        operator fun invoke(span: Span): Unsized = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Use private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "use"

    companion object {
        fun default(): Use = Use(Span.callSite())

        fun from(span: Span): Use = Use(span)

        operator fun invoke(span: Span): Use = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Virtual private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "virtual"

    companion object {
        fun default(): Virtual = Virtual(Span.callSite())

        fun from(span: Span): Virtual = Virtual(span)

        operator fun invoke(span: Span): Virtual = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Where private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "where"

    companion object {
        fun default(): Where = Where(Span.callSite())

        fun from(span: Span): Where = Where(span)

        operator fun invoke(span: Span): Where = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class While private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "while"

    companion object {
        fun default(): While = While(Span.callSite())

        fun from(span: Span): While = While(span)

        operator fun invoke(span: Span): While = from(span)
    }
}

/**
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Yield private constructor(
    span: Span,
) : KeywordToken(span) {
    override val text: String = "yield"

    companion object {
        fun default(): Yield = Yield(Span.callSite())

        fun from(span: Span): Yield = Yield(span)

        operator fun invoke(span: Span): Yield = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class And private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "&") {
    companion object {
        fun default(): And = And(callSiteSpans(1))

        fun from(span: Span): And = And(repeatedSpans(span, 1))

        fun from(spans: List<Span>): And = And(spans)

        operator fun invoke(span: Span): And = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class AndAnd private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "&&") {
    companion object {
        fun default(): AndAnd = AndAnd(callSiteSpans(2))

        fun from(span: Span): AndAnd = AndAnd(repeatedSpans(span, 2))

        fun from(spans: List<Span>): AndAnd = AndAnd(spans)

        operator fun invoke(span: Span): AndAnd = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class AndEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "&=") {
    companion object {
        fun default(): AndEq = AndEq(callSiteSpans(2))

        fun from(span: Span): AndEq = AndEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): AndEq = AndEq(spans)

        operator fun invoke(span: Span): AndEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class At private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "@") {
    companion object {
        fun default(): At = At(callSiteSpans(1))

        fun from(span: Span): At = At(repeatedSpans(span, 1))

        fun from(spans: List<Span>): At = At(spans)

        operator fun invoke(span: Span): At = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Caret private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "^") {
    companion object {
        fun default(): Caret = Caret(callSiteSpans(1))

        fun from(span: Span): Caret = Caret(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Caret = Caret(spans)

        operator fun invoke(span: Span): Caret = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class CaretEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "^=") {
    companion object {
        fun default(): CaretEq = CaretEq(callSiteSpans(2))

        fun from(span: Span): CaretEq = CaretEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): CaretEq = CaretEq(spans)

        operator fun invoke(span: Span): CaretEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Colon private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ":") {
    companion object {
        fun default(): Colon = Colon(callSiteSpans(1))

        fun from(span: Span): Colon = Colon(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Colon = Colon(spans)

        operator fun invoke(span: Span): Colon = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Comma private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ",") {
    companion object {
        fun default(): Comma = Comma(callSiteSpans(1))

        fun from(span: Span): Comma = Comma(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Comma = Comma(spans)

        operator fun invoke(span: Span): Comma = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Dollar private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "\$") {
    companion object {
        fun default(): Dollar = Dollar(callSiteSpans(1))

        fun from(span: Span): Dollar = Dollar(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Dollar = Dollar(spans)

        operator fun invoke(span: Span): Dollar = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Dot private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ".") {
    companion object {
        fun default(): Dot = Dot(callSiteSpans(1))

        fun from(span: Span): Dot = Dot(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Dot = Dot(spans)

        operator fun invoke(span: Span): Dot = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class DotDot private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "..") {
    companion object {
        fun default(): DotDot = DotDot(callSiteSpans(2))

        fun from(span: Span): DotDot = DotDot(repeatedSpans(span, 2))

        fun from(spans: List<Span>): DotDot = DotDot(spans)

        operator fun invoke(span: Span): DotDot = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class DotDotDot private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "...") {
    companion object {
        fun default(): DotDotDot = DotDotDot(callSiteSpans(3))

        fun from(span: Span): DotDotDot = DotDotDot(repeatedSpans(span, 3))

        fun from(spans: List<Span>): DotDotDot = DotDotDot(spans)

        operator fun invoke(span: Span): DotDotDot = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class DotDotEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "..=") {
    companion object {
        fun default(): DotDotEq = DotDotEq(callSiteSpans(3))

        fun from(span: Span): DotDotEq = DotDotEq(repeatedSpans(span, 3))

        fun from(spans: List<Span>): DotDotEq = DotDotEq(spans)

        operator fun invoke(span: Span): DotDotEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Eq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "=") {
    companion object {
        fun default(): Eq = Eq(callSiteSpans(1))

        fun from(span: Span): Eq = Eq(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Eq = Eq(spans)

        operator fun invoke(span: Span): Eq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class EqEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "==") {
    companion object {
        fun default(): EqEq = EqEq(callSiteSpans(2))

        fun from(span: Span): EqEq = EqEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): EqEq = EqEq(spans)

        operator fun invoke(span: Span): EqEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class FatArrow private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "=>") {
    companion object {
        fun default(): FatArrow = FatArrow(callSiteSpans(2))

        fun from(span: Span): FatArrow = FatArrow(repeatedSpans(span, 2))

        fun from(spans: List<Span>): FatArrow = FatArrow(spans)

        operator fun invoke(span: Span): FatArrow = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Ge private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ">=") {
    companion object {
        fun default(): Ge = Ge(callSiteSpans(2))

        fun from(span: Span): Ge = Ge(repeatedSpans(span, 2))

        fun from(spans: List<Span>): Ge = Ge(spans)

        operator fun invoke(span: Span): Ge = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Gt private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ">") {
    companion object {
        fun default(): Gt = Gt(callSiteSpans(1))

        fun from(span: Span): Gt = Gt(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Gt = Gt(spans)

        operator fun invoke(span: Span): Gt = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class LArrow private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "<-") {
    companion object {
        fun default(): LArrow = LArrow(callSiteSpans(2))

        fun from(span: Span): LArrow = LArrow(repeatedSpans(span, 2))

        fun from(spans: List<Span>): LArrow = LArrow(spans)

        operator fun invoke(span: Span): LArrow = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Le private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "<=") {
    companion object {
        fun default(): Le = Le(callSiteSpans(2))

        fun from(span: Span): Le = Le(repeatedSpans(span, 2))

        fun from(spans: List<Span>): Le = Le(spans)

        operator fun invoke(span: Span): Le = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Lt private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "<") {
    companion object {
        fun default(): Lt = Lt(callSiteSpans(1))

        fun from(span: Span): Lt = Lt(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Lt = Lt(spans)

        operator fun invoke(span: Span): Lt = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Minus private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "-") {
    companion object {
        fun default(): Minus = Minus(callSiteSpans(1))

        fun from(span: Span): Minus = Minus(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Minus = Minus(spans)

        operator fun invoke(span: Span): Minus = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class MinusEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "-=") {
    companion object {
        fun default(): MinusEq = MinusEq(callSiteSpans(2))

        fun from(span: Span): MinusEq = MinusEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): MinusEq = MinusEq(spans)

        operator fun invoke(span: Span): MinusEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Ne private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "!=") {
    companion object {
        fun default(): Ne = Ne(callSiteSpans(2))

        fun from(span: Span): Ne = Ne(repeatedSpans(span, 2))

        fun from(spans: List<Span>): Ne = Ne(spans)

        operator fun invoke(span: Span): Ne = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Not private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "!") {
    companion object {
        fun default(): Not = Not(callSiteSpans(1))

        fun from(span: Span): Not = Not(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Not = Not(spans)

        operator fun invoke(span: Span): Not = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Or private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "|") {
    companion object {
        fun default(): Or = Or(callSiteSpans(1))

        fun from(span: Span): Or = Or(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Or = Or(spans)

        operator fun invoke(span: Span): Or = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class OrEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "|=") {
    companion object {
        fun default(): OrEq = OrEq(callSiteSpans(2))

        fun from(span: Span): OrEq = OrEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): OrEq = OrEq(spans)

        operator fun invoke(span: Span): OrEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class OrOr private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "||") {
    companion object {
        fun default(): OrOr = OrOr(callSiteSpans(2))

        fun from(span: Span): OrOr = OrOr(repeatedSpans(span, 2))

        fun from(spans: List<Span>): OrOr = OrOr(spans)

        operator fun invoke(span: Span): OrOr = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class PathSep private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "::") {
    companion object {
        fun default(): PathSep = PathSep(callSiteSpans(2))

        fun from(span: Span): PathSep = PathSep(repeatedSpans(span, 2))

        fun from(spans: List<Span>): PathSep = PathSep(spans)

        operator fun invoke(span: Span): PathSep = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Percent private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "%") {
    companion object {
        fun default(): Percent = Percent(callSiteSpans(1))

        fun from(span: Span): Percent = Percent(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Percent = Percent(spans)

        operator fun invoke(span: Span): Percent = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class PercentEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "%=") {
    companion object {
        fun default(): PercentEq = PercentEq(callSiteSpans(2))

        fun from(span: Span): PercentEq = PercentEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): PercentEq = PercentEq(spans)

        operator fun invoke(span: Span): PercentEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Plus private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "+") {
    companion object {
        fun default(): Plus = Plus(callSiteSpans(1))

        fun from(span: Span): Plus = Plus(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Plus = Plus(spans)

        operator fun invoke(span: Span): Plus = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class PlusEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "+=") {
    companion object {
        fun default(): PlusEq = PlusEq(callSiteSpans(2))

        fun from(span: Span): PlusEq = PlusEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): PlusEq = PlusEq(spans)

        operator fun invoke(span: Span): PlusEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Pound private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "#") {
    companion object {
        fun default(): Pound = Pound(callSiteSpans(1))

        fun from(span: Span): Pound = Pound(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Pound = Pound(spans)

        operator fun invoke(span: Span): Pound = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Question private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "?") {
    companion object {
        fun default(): Question = Question(callSiteSpans(1))

        fun from(span: Span): Question = Question(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Question = Question(spans)

        operator fun invoke(span: Span): Question = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class RArrow private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "->") {
    companion object {
        fun default(): RArrow = RArrow(callSiteSpans(2))

        fun from(span: Span): RArrow = RArrow(repeatedSpans(span, 2))

        fun from(spans: List<Span>): RArrow = RArrow(spans)

        operator fun invoke(span: Span): RArrow = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Semi private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ";") {
    companion object {
        fun default(): Semi = Semi(callSiteSpans(1))

        fun from(span: Span): Semi = Semi(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Semi = Semi(spans)

        operator fun invoke(span: Span): Semi = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Shl private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "<<") {
    companion object {
        fun default(): Shl = Shl(callSiteSpans(2))

        fun from(span: Span): Shl = Shl(repeatedSpans(span, 2))

        fun from(spans: List<Span>): Shl = Shl(spans)

        operator fun invoke(span: Span): Shl = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class ShlEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "<<=") {
    companion object {
        fun default(): ShlEq = ShlEq(callSiteSpans(3))

        fun from(span: Span): ShlEq = ShlEq(repeatedSpans(span, 3))

        fun from(spans: List<Span>): ShlEq = ShlEq(spans)

        operator fun invoke(span: Span): ShlEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Shr private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ">>") {
    companion object {
        fun default(): Shr = Shr(callSiteSpans(2))

        fun from(span: Span): Shr = Shr(repeatedSpans(span, 2))

        fun from(spans: List<Span>): Shr = Shr(spans)

        operator fun invoke(span: Span): Shr = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class ShrEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, ">>=") {
    companion object {
        fun default(): ShrEq = ShrEq(callSiteSpans(3))

        fun from(span: Span): ShrEq = ShrEq(repeatedSpans(span, 3))

        fun from(spans: List<Span>): ShrEq = ShrEq(spans)

        operator fun invoke(span: Span): ShrEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Slash private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "/") {
    companion object {
        fun default(): Slash = Slash(callSiteSpans(1))

        fun from(span: Span): Slash = Slash(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Slash = Slash(spans)

        operator fun invoke(span: Span): Slash = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class SlashEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "/=") {
    companion object {
        fun default(): SlashEq = SlashEq(callSiteSpans(2))

        fun from(span: Span): SlashEq = SlashEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): SlashEq = SlashEq(spans)

        operator fun invoke(span: Span): SlashEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Star private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "*") {
    companion object {
        fun default(): Star = Star(callSiteSpans(1))

        fun from(span: Span): Star = Star(repeatedSpans(span, 1))

        fun from(spans: List<Span>): Star = Star(spans)

        operator fun invoke(span: Span): Star = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class StarEq private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "*=") {
    companion object {
        fun default(): StarEq = StarEq(callSiteSpans(2))

        fun from(span: Span): StarEq = StarEq(repeatedSpans(span, 2))

        fun from(spans: List<Span>): StarEq = StarEq(spans)

        operator fun invoke(span: Span): StarEq = from(span)
    }
}

/**
 *
 * Usage:
 *
 * Don't try to remember the name of this type &mdash; use the
 * [Token] type definitions in `io.github.kotlinmania.syn.token` instead.
 */
class Tilde private constructor(
    spans: List<Span>,
) : PunctuationToken(spans, "~") {
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
