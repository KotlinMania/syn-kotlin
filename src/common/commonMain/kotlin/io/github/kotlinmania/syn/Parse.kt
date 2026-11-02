// port-lint: source parse.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Group
import io.github.kotlinmania.procmacro2.Literal
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
import io.github.kotlinmania.quote.ToTokens

/**
 * Parsing interface for parsing a token stream into a syntax tree node.
 *
 * Parsing in Syn is built on parser functions that take in a [ParseStream]
 * and produce a [SynResult] of some syntax tree node. Underlying these parser
 * functions is a lower level mechanism built around the [Cursor] type. Cursor
 * is a cheaply copyable cursor over a range of tokens in a token stream.
 *
 * ## Example
 *
 * Here is a snippet of parsing code to get a feel for the style of the
 * library. We define data structures for a subset of the syntax including
 * enums (not shown) and data classes, then provide typed parser functions
 * to parse these syntax tree data structures from a token stream.
 *
 * Once parser functions have been defined, they can be called conveniently from a
 * procedural-macro handler through [parseMacroInput] or through the token-stream
 * parsing helpers. If the caller provides syntactically invalid input to the
 * procedural-macro handler, they will receive a helpful compiler error message
 * pointing out the exact token that triggered the failure to parse.
 *
 * ## The `parse*` functions
 *
 * The top-level `parse2` and `parseStr` helpers serve as entry points for
 * parsing syntax tree nodes from a token stream or string. These functions
 * accept parser functions with the signature `(ParseStream) -> SynResult<T>`,
 * which includes most types in Syn.
 *
 * ## Parser functions
 *
 * Some types can be parsed in several ways depending on context. For example
 * an `Attribute` can be either "outer" like `...` attribute or "inner" like `...` inner attribute
 * and parsing the wrong one would be a bug. Similarly [Punctuated] may or may
 * not allow trailing punctuation, and parsing it the wrong way would either
 * reject valid input or accept invalid input.
 *
 * A default parser function is not provided in these cases because there is no good
 * behavior to consider the default.
 *
 * In these cases the types provide a choice of parser functions rather than a
 * single default parser function, and those parser functions can be invoked
 * directly or through the top-level `parse*` helpers.
 */

/**
 * Parsing interface implemented by all types that can be parsed in a default
 * way from a token stream.
 *
 * Refer to the [module documentation] for details about implementing and using
 * the `Parse` trait.
 *
 * [module documentation]: self
 */
public interface Parse<T> {
    public fun parse(input: ParseStream): SynResult<T>
}

/**
 * Input to a Syn parser function.
 *
 * Spelled `ParseStream = ParseBuffer`. The shared-mutable part is the way a parser represents "may
 * mutate the cursor through shared mutable state." The typealias resolves directly to [ParseBuffer].
 */
public typealias ParseStream = ParseBuffer

/**
 * Cursor position within a buffered token stream.
 *
 * This type is more commonly used through the type alias [`ParseStream`] which
 * is an alias for `&ParseBuffer`.
 *
 * `ParseStream` is the input type for all parser functions in Syn. They have
 * the signature `fn(ParseStream) -> Result<T>`.
 *
 * ## Calling a parser function
 *
 * There is no public way to construct a `ParseBuffer`. Instead, if you are
 * looking to invoke a parser function that requires `ParseStream` as input,
 * you will need to go through one of the public parsing entry points.
 *
 * - The [`parseMacroInput!`] macro if parsing input of a procedural macro;
 * - One of [the `syn::parse*` functions][syn-parse]; or
 * - A method of the [`Parser`] trait.
 *
 * [`parseMacroInput!`]: crate::parseMacroInput!
 * [syn-parse]: self#the-synparse-functions
 */
public class ParseBuffer internal constructor(
    internal val scope: Span,
    internal var currentCursor: Cursor,
    internal var unexpected: UnexpectedRef?,
) : Speculative,
    AnyDelimiter {
    override fun toString(): String = currentCursor.tokenStream().toString()

    public fun fmt(): String = toString()

    /**
     * Parses a syntax tree node of type [T], advancing the position of our
     * parse stream past it.
     */
    public fun <T> parse(parser: (ParseStream) -> SynResult<T>): SynResult<T> = parser(this)

    /**
     * Calls the given parser function to parse a syntax tree node of type `T`
     * from this stream.
     *
     * # Example
     *
     * The parser below invokes [`Attribute::parseOuter`] to parse a vector of
     * zero or more outer attributes.
     *
     * [`Attribute::parseOuter`]: crate::Attribute::parseOuter
     *
     * ```
     * import syn..Attribute, Ident, Result, Token};
     * import syn.parse..Parse, ParseStream};
     *
     * // Parses a unit struct with attributes.
     * //
     * //
     * //     struct S;
     * struct UnitStruct {
     *     attrs: Vec<Attribute>,
     *     structToken: token[struct],
     *     name: Ident,
     *     semiToken: token[;],
     * }
     *
     * class UnitStruct : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         Ok(UnitStruct {
     *             attrs: input.call(Attribute::parseOuter)?,
     *             structToken: input.parse()?,
     *             name: input.parse()?,
     *             semiToken: input.parse()?,
     *         })
     *     }
     * }
     * ```
     */
    public fun <T> call(function: (ParseStream) -> SynResult<T>): SynResult<T> = function(this)

    /**
     * Looks at the next token in the parse stream to determine whether it
     * matches the requested type of token.
     *
     * Does not advance the position of the parse stream.
     *
     * # Syntax
     *
     * Note that this method does not use turbofish syntax. Pass the peek type
     * inside of parentheses.
     *
     * - `input.peek(token[struct])`
     * - `input.peek(token[==])`
     * - `input.peek(syn::Ident)`&emsp;*(does not accept keywords)*
     * - `input.peek(syn::Ident::peekAny)`
     * - `input.peek(scope)`
     * - `input.peek(token::Brace)`
     *
     * # Example
     *
     * In this example we finish parsing the list of supertraits when the next
     * token in the input is either `where` or an opening curly brace.
     *
     * ```
     * import syn..braced, token, Generics, Ident, Result, Token, TypeParamBound};
     * import syn.parse..Parse, ParseStream};
     * import syn.punctuated.Punctuated;
     *
     * // Parses a trait definition containing no associated items.
     * //
     * //     trait Marker<, T>: A + B<> where Holder<T>: Clone {}
     * struct MarkerTrait {
     *     traitToken: token[trait],
     *     ident: Ident,
     *     generics: Generics,
     *     colonToken: Option<token[:]>,
     *     supertraits: Punctuated<TypeParamBound, token[+]>,
     *     braceToken: token::Brace,
     * }
     *
     * class MarkerTrait : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         val traitToken: token[trait] = input.parse()?;
     *         val ident: Ident = input.parse()?;
     *         var generics: Generics = input.parse()?;
     *         val colonToken: Option<token[:]> = input.parse()?;
     *
     *         var supertraits = Punctuated::new();
     *         if colonToken.isSome() {
     *             loop {
     *                 supertraits.pushValue(input.parse()?);
     *                 if input.peek(token[where]) || input.peek(token::Brace) {
     *                     break;
     *                 }
     *                 supertraits.pushPunct(input.parse()?);
     *             }
     *         }
     *
     *         generics.whereClause = input.parse()?;
     *         val content;
     *         val emptyBraceToken = braced(content in input);
     *
     *         Ok(MarkerTrait {
     *             traitToken,
     *             ident,
     *             generics,
     *             colonToken,
     *             supertraits,
     *             braceToken: emptyBraceToken,
     *         })
     *     }
     * }
     * ```
     */
    public fun peek(token: Peek): Boolean = token.peek(currentCursor)

    /**
     * Looks at the second-next token in the parse stream.
     *
     * This is commonly useful as a way to implement contextual keywords.
     */
    public fun peek2(token: Peek): Boolean {
        val next = currentCursor.skip() ?: return false
        return token.peek(next)
    }

    /** Looks at the third-next token in the parse stream. */
    public fun peek3(token: Peek): Boolean {
        val next = currentCursor.skip()?.skip() ?: return false
        return token.peek(next)
    }

    /**
     * Parses zero or more occurrences of `T` separated by punctuation of type
     * `P`, with optional trailing punctuation.
     *
     * Parsing continues until the end of this parse stream. The entire content
     * of this parse stream must consist of `T` and `P`.
     *
     * # Example
     *
     * ```
     * import syn..parenthesized, token, Ident, Result, Token, Type};
     * import syn.parse..Parse, ParseStream};
     * import syn.punctuated.Punctuated;
     *
     * // Parse a simplified tuple struct syntax like:
     * //
     * //     struct S(A, B);
     * struct TupleStruct {
     *     structToken: token[struct],
     *     ident: Ident,
     *     parenToken: token::Paren,
     *     fields: Punctuated<Type, token[,]>,
     *     semiToken: token[;],
     * }
     *
     * class TupleStruct : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         val content;
     *         Ok(TupleStruct {
     *             structToken: input.parse()?,
     *             ident: input.parse()?,
     *             parenToken: parenthesized(content in input),
     *             fields: content.parseTerminated(Type::parse, token[,])?,
     *             semiToken: input.parse()?,
     *         })
     *     }
     * }
     * ```
     *
     * # See also
     *
     * If your separator is anything more complicated than an invocation of the
     * `Token!` macro, this method won't be applicable and you can instead
     * directly use `Punctuated`'s parser functions: [`parseTerminated`],
     * [`parseSeparatedNonempty`] etc.
     *
     * [`parseTerminated`]: Punctuated::parseTerminated
     * [`parseSeparatedNonempty`]: Punctuated::parseSeparatedNonempty
     *
     * ```
     * import syn..customKeyword, Expr, Result, Token};
     * import syn.parse..Parse, ParseStream};
     * import syn.punctuated.Punctuated;
     *
     * mod kw {
     *     syn::customKeyword(fin);
     * }
     *
     * struct Fin(kw::fin, token[;]);
     *
     * class Fin : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         Ok(Self(input.parse()?, input.parse()?))
     *     }
     * }
     *
     * struct Thing {
     *     steps: Punctuated<Expr, Fin>,
     * }
     *
     * class Thing : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         Ok(Thing {
     *             steps: Punctuated::parseTerminated(input)?,
     *         })
     *         // or equivalently, this means the same thing:
     *             steps: input.call(Punctuated::parseTerminated)?,
     *     }
     * }
     * ```
     */
    internal fun <T, P> parseTerminated(
        parser: (ParseStream) -> SynResult<T>,
        separator: Peek,
        punctuationParser: (ParseStream) -> SynResult<P>,
    ): SynResult<Punctuated<T, P>> {
        separator.peek(currentCursor)
        return Punctuated.parseTerminatedWith(this, parser, punctuationParser)
    }

    /**
     * Returns whether there are no more tokens remaining to be parsed from
     * this stream.
     *
     * This method returns true upon reaching the end of the content within a
     * set of delimiters, as well as at the end of the tokens provided to the
     * outermost parsing entry point.
     *
     * This is equivalent to
     * <code>.<a href="#method.peek">peek</a>(<a href="struct.End.html">syn::parse::End</a>)</code>.
     * Use `.peek2(End)` or `.peek3(End)` to look for the end of a parse stream
     * further ahead than the current position.
     *
     * # Example
     *
     * ```
     * import syn..braced, token, Ident, Item, Result, Token};
     * import syn.parse..Parse, ParseStream};
     *
     * // Parses a Rust `mod m { ... }` containing zero or more items.
     * struct Mod {
     *     modToken: token[mod],
     *     name: Ident,
     *     braceToken: token::Brace,
     *     items: Vec<Item>,
     * }
     *
     * class Mod : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         val content;
     *         Ok(Mod {
     *             modToken: input.parse()?,
     *             name: input.parse()?,
     *             braceToken: braced(content in input),
     *             items: {
     *                 var items = Vec::new();
     *                 while !content.isEmpty() {
     *                     items.push(content.parse()?);
     *                 }
     *                 items
     *             },
     *         })
     *     }
     * }
     * ```
     */
    public fun isEmpty(): Boolean = currentCursor.eof()

    /**
     * Constructs a helper for peeking at the next token in this stream and
     * building an error message if it is not one of a set of expected tokens.
     *
     * # Example
     *
     * ```
     * import syn..ConstParam, Ident, scope, LifetimeParam, Result, Token, TypeParam};
     * import syn.parse..Parse, ParseStream};
     *
     * // A generic parameter, a single one of the comma-separated elements inside
     * // angle brackets in:
     * //
     * //     fn f<T: Clone, , : , const N: Int>() { ... }
     * //
     * // On invalid input, lookahead gives us a reasonable error message.
     * //
     * //     error: expected one of: identifier, scope, `const`
     * //       |
     * //     5 |     fn f<!Sized>() {}
     * //       |          ^
     * enum GenericParam {
     *     Type(TypeParam),
     *     scope(LifetimeParam),
     *     Const(ConstParam),
     * }
     *
     * class GenericParam : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         val lookahead = input.lookahead1();
     *         if lookahead.peek(Ident) {
     *             input.parse().map(GenericParam::Type)
     *         } else if lookahead.peek(scope) {
     *             input.parse().map(GenericParam::scope)
     *         } else if lookahead.peek(token[const]) {
     *             input.parse().map(GenericParam::Const)
     *         } else {
     *             Err(lookahead.error())
     *         }
     *     }
     * }
     * ```
     */
    public fun lookahead1(): Lookahead1 = lookahead1New(scope, currentCursor)

    /**
     * Forks a parse stream so that parsing tokens out of either the original
     * or the fork does not advance the position of the other.
     *
     * # Performance
     *
     * Forking a parse stream is a cheap fixed amount of work and does not
     * involve copying token buffers. Where you might hit performance problems
     * is if your macro ends up parsing a large amount of content more than
     * once.
     *
     * ```
     * // Do not do this.
     * if input.fork().parse::<Expr>().isOk() {
     *     return input.parse::<Expr>();
     * }
     * ```
     *
     * As a rule, avoid parsing an unbounded amount of tokens out of a forked
     * parse stream. Only use a fork when the amount of work performed against
     * the fork is small and bounded.
     *
     * When complex speculative parsing against the forked stream is
     * unavoidable, use [`parse::discouraged::Speculative`] to advance the
     * original stream once the fork's parse is determined to have been
     * successful.
     *
     * For a lower level way to perform speculative parsing at the token level,
     * consider using [`ParseStream::step`] instead.
     *
     * [`parse::discouraged::Speculative`]: discouraged::Speculative
     * [`ParseStream::step`]: ParseBuffer::step
     *
     * # Example
     *
     * The parse implementation shown here parses possibly restricted `pub`
     * visibilities.
     *
     * - `pub`
     * - `pub(crate)`
     * - `pub(self)`
     * - `pub(super)`
     * - `pub(in some::path)`
     *
     * To handle the case of visibilities inside of tuple structs, the parser
     * needs to distinguish parentheses that specify visibility restrictions
     * from parentheses that form part of a tuple type.
     *
     * ```
     * struct S(pub(crate) A, pub (B, C));
     * ```
     *
     * In this example input the first tuple struct element of `S` has
     * `pub(crate)` visibility while the second tuple struct element has `pub`
     * visibility; the parentheses around `(B, C)` are part of the type rather
     * than part of a visibility restriction.
     *
     * The parser uses a forked parse stream to check the first token inside of
     * parentheses after the `pub` keyword. This is a small bounded amount of
     * work performed against the forked parse stream.
     *
     * ```
     * import syn..parenthesized, token, Ident, Path, Result, Token};
     * import syn.ext.IdentExt;
     * import syn.parse..Parse, ParseStream};
     *
     * struct PubVisibility {
     *     pubToken: token[pub],
     *     restricted: Option<Restricted>,
     * }
     *
     * struct Restricted {
     *     parenToken: token::Paren,
     *     inToken: Option<token[in]>,
     *     path: Path,
     * }
     *
     * class PubVisibility : Parse {
     *     fun parse(input: ParseStream) -> Result<Self> {
     *         val pubToken: token[pub] = input.parse()?;
     *
     *         if input.peek(token::Paren) {
     *             val ahead = input.fork();
     *             var content;
     *             parenthesized(content in ahead);
     *
     *             if content.peek(token[crate])
     *                 || content.peek(token[self])
     *                 || content.peek(token[super])
     *             {
     *                 return Ok(PubVisibility {
     *                     pubToken,
     *                     restricted: Some(Restricted {
     *                         parenToken: parenthesized(content in input),
     *                         inToken: None,
     *                         path: Path::from(content.call(Ident::parseAny)?),
     *                     }),
     *                 });
     *             } else if content.peek(token[in]) {
     *                 return Ok(PubVisibility {
     *                     pubToken,
     *                     restricted: Some(Restricted {
     *                         parenToken: parenthesized(content in input),
     *                         inToken: Some(content.parse()?),
     *                         path: content.call(Path::parseModStyle)?,
     *                     }),
     *                 });
     *             }
     *         }
     *
     *         Ok(PubVisibility {
     *             pubToken,
     *             restricted: None,
     *         })
     *     }
     * }
     * ```
     */
    public fun fork(): ParseBuffer =
        ParseBuffer(
            scope = scope,
            currentCursor = currentCursor,
            // Not the parent's unexpected. Nothing cares whether the clone parses
            // all the way unless we `advanceTo`.
            unexpected = UnexpectedRef(Unexpected.None),
        )

    /** Triggers an error at the current position of the parse stream. */
    public fun error(message: String): SynError = errorNewAt(scope, currentCursor, message)

    /**
     * Speculatively parses tokens from this parse stream, advancing the
     * position of this stream only if parsing succeeds.
     *
     * This is a powerful low-level API used for defining the `Parse` impls of
     * the basic built-in token types. It is not something that will be used
     * widely outside of the Syn codebase.
     *
     * # Example
     *
     * ```
     * import procMacro2.TokenTree;
     * import syn.Result;
     * import syn.parse.ParseStream;
     *
     * // This function advances the stream past the next occurrence of `@`. If
     * // no `@` is present in the stream, the stream position is unchanged and
     * // an error is returned.
     * fun skipPastNextAt(input: ParseStream) -> Result<()> {
     *     input.step(|cursor| {
     *         var rest = *cursor;
     *         while val some((tt, next)) = rest.tokenTree() {
     *             when (&tt) {
     *                 TokenTree::Punct(punct) if punct.asChar() == '@' => {
     *                     return Ok(((), next));
     *                 }
     *                 _ => rest = next,
     *             }
     *         }
     *         Err(cursor.error("no `@` was found after this point"))
     *     })
     * }
     * ```
     */
    public fun <R> step(function: (StepCursor) -> SynResult<Pair<R, Cursor>>): SynResult<R> {
        val stepCursor = StepCursor(scope, currentCursor)
        return function(stepCursor).map { (node, rest) ->
            currentCursor = rest
            node
        }
    }

    /**
     * Returns the [Span] of the next token in the parse stream, or
     * [Span.callSite] if this parse stream has completely exhausted its input
     * [TokenStream].
     */
    public fun span(): Span {
        val cursor = currentCursor
        return if (cursor.eof()) scope else openSpanOfGroup(cursor)
    }

    /**
     * Provides low-level access to the token representation underlying this
     * parse stream.
     *
     * Cursors are immutable so no operations you perform against the cursor
     * will affect the state of this parse stream.
     *
     * # Example
     *
     * ```
     * import procMacro2.TokenStream;
     * import syn.buffer.Cursor;
     * import syn.parse..ParseStream, Result};
     *
     * // Run a parser that returns T, but get its output as TokenStream instead of T.
     * // This works without T needing to implement ToTokens.
     * fn recognizeTokenStream<T>(
     *     recognizer: fn(ParseStream) -> Result<T>,
     * ) -> class Fn(ParseStream) -> Result<TokenStream> {
     *     move |input| {
     *         val begin = input.cursor();
     *         recognizer(input)?;
     *         val end = input.cursor();
     *         Ok(tokensBetween(begin, end))
     *     }
     * }
     *
     * // Collect tokens between two cursors as a TokenStream.
     * fun tokensBetween(begin: Cursor, end: Cursor) -> TokenStream {
     *     assert(begin <= end);
     *
     *     var cursor = begin;
     *     var tokens = TokenStream::new();
     *     while cursor < end {
     *         let (token, next) = cursor.tokenTree().unwrap();
     *         tokens.extend(core::iter::once(token));
     *         cursor = next;
     *     }
     *     tokens
     * }
     *
     * fun main() {
     *     import quote.quote;
     *     import syn.parse..Parse, Parser};
     *     import syn.Token;
     *
     *     // Parse syn::Type as a TokenStream, surrounded by angle brackets.
     *     fun example(input: ParseStream) -> Result<TokenStream> {
     *         val langle: token[<] = input.parse()?;
     *         val ty = recognizeTokenStream(syn::Type::parse)(input)?;
     *         val rangle: token[>] = input.parse()?;
     *         Ok(ty)
     *     }
     *
     *     val tokens = quote{ <fn() -> u8> };
     *     println("{}", example.parse2(tokens).unwrap());
     * }
     * ```
     */
    public fun cursor(): Cursor = currentCursor

    internal fun checkUnexpected(): SynResult<Unit> {
        val (_, info) = innerUnexpected(this)
        return if (info != null) {
            SynResult.failure(errUnexpectedToken(info.first, info.second))
        } else {
            SynResult.success(Unit)
        }
    }

    internal fun drop() {
        val info = spanOfUnexpectedIgnoringNones(currentCursor) ?: return
        val (inner, oldSpan) = innerUnexpected(this)
        if (oldSpan == null) {
            inner.value = Unexpected.Some(info.first, info.second)
        }
    }

    /**
     * Propagates any leftover unexpected-token info from this child buffer to
     * its parent's unexpected chain. Kotlin does not run deterministic
     * end-of-scope finalizers for this class, so callers that construct a child
     * [ParseBuffer] must invoke this when the child buffer's scope ends.
     */
    public fun finishChildBuffer() {
        drop()
    }

    /**
     * Advances this stream's position to match the position of [other], a
     * fork of this stream. Used to commit speculative parsing done in a fork.
     *
     * Throws [IllegalArgumentException] if [other] was not derived from this
     * parse stream.
     */
    public override fun advanceTo(fork: ParseBuffer) {
        advanceToSpeculative(fork)
    }

    public override fun parseAnyDelimiter(): SynResult<AnyDelimiterResult> =
        parseAnyDelimiterImpl()
}

/**
 * Cursor state associated with speculative parsing.
 *
 * This type is the input of the closure provided to [`ParseStream::step`].
 *
 * [`ParseStream::step`]: ParseBuffer::step
 *
 * # Example
 *
 * ```
 * import procMacro2.TokenTree;
 * import syn.Result;
 * import syn.parse.ParseStream;
 *
 * // This function advances the stream past the next occurrence of `@`. If
 * // no `@` is present in the stream, the stream position is unchanged and
 * // an error is returned.
 * fun skipPastNextAt(input: ParseStream) -> Result<()> {
 *     input.step(|cursor| {
 *         var rest = *cursor;
 *         while val some((tt, next)) = rest.tokenTree() {
 *             when (&tt) {
 *                 TokenTree::Punct(punct) if punct.asChar() == '@' => {
 *                     return Ok(((), next));
 *                 }
 *                 _ => rest = next,
 *             }
 *         }
 *         Err(cursor.error("no `@` was found after this point"))
 *     })
 * }
 * ```
 */
public class StepCursor internal constructor(
    private val scope: Span,
    private val cursor: Cursor,
) {
    public typealias Target = Cursor

    /**
     * Triggers an error at the current position of the parse stream.
     *
     * The [ParseBuffer.step] invocation will return this same error without
     * advancing the stream state.
     */
    public fun error(message: String): SynError = errorNewAt(scope, cursor, message)

    // Delegates each Cursor method by forwarding.
    public fun eof(): Boolean = cursor.eof()

    public fun ident(): Pair<io.github.kotlinmania.procmacro2.Ident, Cursor>? = cursor.ident()

    public fun punct(): Pair<Punct, Cursor>? = cursor.punct()

    public fun literal(): Pair<Literal, Cursor>? = cursor.literal()

    public fun lifetime(): Pair<Lifetime, Cursor>? = cursor.lifetime()

    public fun group(delim: Delimiter): Triple<Cursor, io.github.kotlinmania.procmacro2.DelimSpan, Cursor>? =
        cursor.group(delim)

    public fun anyGroup(): AnyGroup? = cursor.anyGroup()

    public fun tokenStream(): TokenStream = cursor.tokenStream()

    public fun tokenTree(): Pair<TokenTree, Cursor>? = cursor.tokenTree()

    public fun span(): Span = cursor.span()

    public fun deref(): Cursor = cursor

    /**
     * Surfaces the wrapped [Cursor]. Mirrors the delegation pattern where
     * callers could pass a [StepCursor] anywhere
     * a [Cursor] is expected. Callers explicitly extract via this property.
     */
    public val raw: Cursor get() = deref()
}

internal fun advanceStepCursor(proof: StepCursor, to: Cursor): Cursor {
    // The StepCursor parameter proves that the child cursor is within scope.
    // This is a runtime check that reads `proof` to surface the dependency to the type-checker.
    proof.eof()
    return to
}

internal fun newParseBuffer(
    scope: Span,
    cursor: Cursor,
    unexpected: UnexpectedRef,
): ParseBuffer = ParseBuffer(scope = scope, currentCursor = cursor, unexpected = unexpected)

/**
 * Shared mutable holder of an [Unexpected] state. Uses
 * a shared mutable reference so multiple parse buffers refer to and update
 * the same chain. Kotlin uses [UnexpectedRef] instead, so this single-field
 * class provides the same shared-mutable semantics.
 */
public class UnexpectedRef internal constructor(
    public var value: Unexpected,
)

/**
 * Chain of leftover-token markers used to surface unexpected-token errors
 * across child/parent parse buffers.
 */
public sealed class Unexpected {
    public companion object {
        public fun default(): Unexpected = None
    }

    public object None : Unexpected()

    public data class Some(
        val span: Span,
        val delimiter: Delimiter,
    ) : Unexpected()

    public data class Chain(
        val next: UnexpectedRef,
    ) : Unexpected()

    public fun clone(): Unexpected =
        when (this) {
            is None -> None
            is Some -> Some(span, delimiter)
            is Chain -> Chain(next)
        }
}

/** We call this on UnexpectedRef where temporarily swapping in a None is cheap. */
private fun cellClone(cell: UnexpectedRef): Unexpected {
    val prev = cell.value
    cell.value = Unexpected.None
    val ret = prev.clone()
    cell.value = prev
    return ret
}

internal fun innerUnexpected(buffer: ParseBuffer): Pair<UnexpectedRef, Pair<Span, Delimiter>?> {
    var unexpected = getUnexpected(buffer)
    while (true) {
        when (val cloned = cellClone(unexpected)) {
            is Unexpected.None -> return unexpected to null
            is Unexpected.Some -> return unexpected to (cloned.span to cloned.delimiter)
            is Unexpected.Chain -> unexpected = cloned.next
        }
    }
}

internal fun getUnexpected(buffer: ParseBuffer): UnexpectedRef =
    buffer.unexpected ?: error("ParseBuffer.unexpected was null")

private fun spanOfUnexpectedIgnoringNones(initial: Cursor): Pair<Span, Delimiter>? {
    var cursor = initial
    if (cursor.eof()) return null
    while (true) {
        val grp = cursor.group(Delimiter.None) ?: break
        val (inner, _, rest) = grp
        val nested = spanOfUnexpectedIgnoringNones(inner)
        if (nested != null) return nested
        cursor = rest
    }
    return if (cursor.eof()) null else cursor.span() to cursor.scopeDelimiter()
}

/** Parser functions for the procmacro2 token types and stdlib containers. */

/** Parser strategy that consumes the remainder of the stream as a [TokenStream]. */
public object TokenStreamParse {
    public fun parse(input: ParseStream): SynResult<TokenStream> =
        input.step { cursor ->
            SynResult.success(cursor.tokenStream() to Cursor.empty())
        }
}

/** Parser strategy for [TokenTree]. */
public object TokenTreeParse {
    public fun parse(input: ParseStream): SynResult<TokenTree> =
        input.step { cursor ->
            val pair = cursor.tokenTree() ?: return@step SynResult.failure(cursor.error("expected token tree"))
            SynResult.success(pair)
        }
}

/** Parser strategy for [Group]. */
public object GroupParse {
    public fun parse(input: ParseStream): SynResult<Group> =
        input.step { cursor ->
            val pair = cursor.raw.anyGroupToken()
            if (pair != null && pair.first.delimiter() != Delimiter.None) {
                SynResult.success(pair)
            } else {
                SynResult.failure(cursor.error("expected group token"))
            }
        }
}

/** Parser strategy for [Punct]. */
public object PunctParse {
    public fun parse(input: ParseStream): SynResult<Punct> =
        input.step { cursor ->
            val pair = cursor.punct() ?: return@step SynResult.failure(cursor.error("expected punctuation token"))
            SynResult.success(pair)
        }
}

/** Parser strategy for [Literal]. */
public object LiteralParse {
    public fun parse(input: ParseStream): SynResult<Literal> =
        input.step { cursor ->
            val pair = cursor.literal() ?: return@step SynResult.failure(cursor.error("expected literal token"))
            SynResult.success(pair)
        }
}

/**
 * Parser that can parse Rust tokens into a particular syntax tree node.
 *
 * Refer to the [module documentation] for details about parsing in Syn.
 *
 * [module documentation]: self
 */
public interface Parser<Output> {
    public fun parse2(tokens: TokenStream): SynResult<Output>

    public fun parse(tokens: io.github.kotlinmania.procmacro.TokenStream): SynResult<Output> =
        parse2(TokenStream.fromProcMacro(tokens))

    public fun parseStr(s: String): SynResult<Output> {
        val parseResult = TokenStream.fromString(s)
        if (parseResult.isFailure()) {
            return SynResult.failure(SynError.from(parseResult))
        }
        return parse2(parseResult.getOrThrow())
    }
}

internal fun tokensToParseBuffer(tokens: TokenBuffer): ParseBuffer {
    val scope = Span.callSite()
    val cursor = tokens.begin()
    val unexpected = UnexpectedRef(Unexpected.None)
    return newParseBuffer(scope, cursor, unexpected)
}

internal fun <T> parseScoped(
    parser: (ParseStream) -> SynResult<T>,
    scope: Span,
    tokens: TokenStream,
): SynResult<T> {
    val buf = TokenBuffer.new2(tokens)
    val cursor = buf.begin()
    val unexpected = UnexpectedRef(Unexpected.None)
    val state = newParseBuffer(scope, cursor, unexpected)
    val nodeResult = parser(state)
    if (nodeResult.isFailure) return nodeResult
    val node = nodeResult.getOrThrow()
    val check = state.checkUnexpected()
    if (check.isFailure) return SynResult.failure(check.exceptionOrNull()!!)
    val info = spanOfUnexpectedIgnoringNones(state.cursor())
    return if (info != null) {
        SynResult.failure(errUnexpectedToken(info.first, info.second))
    } else {
        SynResult.success(node)
    }
}

private fun errUnexpectedToken(span: Span, delimiter: Delimiter): SynError {
    val msg =
        when (delimiter) {
            Delimiter.Parenthesis -> "unexpected token, expected `)`"
            Delimiter.Brace -> "unexpected token, expected `}`"
            Delimiter.Bracket -> "unexpected token, expected `]`"
            Delimiter.None -> "unexpected token"
        }
    return SynError.new(span, msg)
}

/**
 * An empty syntax tree node that consumes no tokens when parsed.
 *
 * This is useful for attribute macros that want to ensure they are not
 * provided any attribute args.
 */
public object Nothing : ToTokens {
    public fun parse(input: ParseStream): SynResult<Nothing> = SynResult.success(Nothing)

    override fun toTokens(tokens: TokenStream) {}

    override fun toString(): String = "Nothing"

    override fun equals(other: Any?): Boolean = other === Nothing

    override fun hashCode(): Int = 0

    public fun fmt(): String = toString()

    public fun eq(other: Nothing): Boolean = true

    public fun hash(): Int = 0
}

/**
 * Parse procedural-macro tokens into the chosen syntax tree node, enforcing
 * that the entire stream is consumed.
 */
public fun <T> parse(
    parser: (ParseStream) -> SynResult<T>,
    tokens: io.github.kotlinmania.procmacro.TokenStream,
): SynResult<T> = parse2(parser, TokenStream.fromProcMacro(tokens))

/**
 * Parse a [TokenStream] into the chosen syntax tree node, enforcing that the
 * entire stream is consumed. Mirrors `parse2`.
 */
public fun <T> parse2(parser: (ParseStream) -> SynResult<T>, tokens: TokenStream): SynResult<T> =
    parseScoped(parser, Span.callSite(), tokens)

/**
 * Parse a string of source code into the chosen syntax tree node. Mirrors
 * `parseStr`.
 */
public fun <T> parseStr(parser: (ParseStream) -> SynResult<T>, s: String): SynResult<T> {
    val parseResult = TokenStream.fromString(s)
    if (parseResult.isFailure()) {
        return SynResult.failure(SynError.from(parseResult))
    }
    return parse2(parser, parseResult.getOrThrow())
}
