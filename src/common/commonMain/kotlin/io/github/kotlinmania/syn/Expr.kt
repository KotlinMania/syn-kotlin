// port-lint: source expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.quote.toTokens
import io.github.kotlinmania.syn.gen.exprDebug
import io.github.kotlinmania.syn.gen.exprEq
import io.github.kotlinmania.syn.gen.exprHash
import kotlin.jvm.JvmInline

internal fun Expr.toTokensAsStmt(tokens: TokenStream) {
    printExpr(this, tokens, FixupContext.newStmt())
}

/**
 * A Rust expression.
 *
 * *This type is available only if Syn is built with the `"derive"` or `"full"`
 * feature, but most of the variants are not available unless "full" is enabled.*
 *
 * # Syntax tree enums
 *
 * This type is a syntax tree enum. In Syn this and other syntax tree enums
 * are designed to be traversed using a `when` expression over the sealed variants:
 *
 * ```kotlin
 * import syn.Expr
 *
 * fun visitExpr(expr: Expr) {
 *     when (expr) {
 *         is Expr.MethodCall -> {
 *             // expr.receiver, expr.args, ...
 *         }
 *         is Expr.Cast -> {
 *             // ...
 *         }
 *         is Expr.If -> {
 *             // expr.cond, expr.thenBranch, expr.elseBranch
 *         }
 *         else -> {}
 *     }
 * }
 * ```
 *
 * We begin with a variable `expr` of type `Expr`, and by smart-casting on it
 * in each branch we get direct access to the variant's properties (`expr.receiver`,
 * `expr.args`, `expr.cond`, `expr.thenBranch`, `expr.elseBranch`, etc.) without
 * repetitive destructuring:
 *
 * ```kotlin
 * import syn.Expr
 *
 * fun example(expr: Expr) {
 *     // Repetitive; prefer direct smart-cast property access instead.
 *     when (expr) {
 *         is Expr.MethodCall -> {
 *             val method = expr.method
 *             val args = expr.args
 *         }
 *         else -> {}
 *     }
 * }
 * ```
 *
 * In general, the name to which a syntax tree enum variant is bound should
 * be a suitable name for the complete syntax tree enum type:
 *
 * ```kotlin
 * import syn.Expr
 *
 * fun example(discriminant: Expr.Field) {
 *     when (val base = discriminant.base) {
 *         is Expr.Tuple -> {
 *             // base.elems
 *         }
 *         else -> {}
 *     }
 * }
 * ```
 *
 * A sign that you may not be choosing the right variable names is if you
 * see names getting repeated in your code, like accessing
 * `receiver.receiver` or `pat.pat` or `cond.cond`.
 */
public sealed class Expr : ToTokens {
    /** A slice literal expression: `[a, b, c, d]`. */
    public data class Array(
        public var attrs: List<Attribute>,
        public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public var elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            bracketToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }

        override fun deepCopy(): Array = Array(attrs.mapTo(mutableListOf()) { it.deepCopy() }, bracketToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** An assignment expression: `a = compute()`. */
    public data class Assign(
        public var attrs: List<Attribute>,
        public var left: Expr,
        public var eqToken: io.github.kotlinmania.syn.token.Eq,
        public var right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprAssign(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Assign = Assign(attrs.mapTo(mutableListOf()) { it.deepCopy() }, left.deepCopy(), eqToken, right.deepCopy())
    }

    /** An async block: `async { ... }`. */
    public data class Async(
        public var attrs: List<Attribute>,
        public var asyncToken: io.github.kotlinmania.syn.token.Async,
        public var capture: io.github.kotlinmania.syn.token.Move?,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            asyncToken.toTokens(tokens)
            capture?.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Async = Async(attrs.mapTo(mutableListOf()) { it.deepCopy() }, asyncToken, capture, block.deepCopy())
    }

    /** An await expression: `fut.await`. */
    public data class Await(
        public var attrs: List<Attribute>,
        public var base: Expr,
        public var dotToken: io.github.kotlinmania.syn.token.Dot,
        public var awaitToken: io.github.kotlinmania.syn.token.Await,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprAwait(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Await = Await(attrs.mapTo(mutableListOf()) { it.deepCopy() }, base.deepCopy(), dotToken, awaitToken)
    }

    /** A binary operation: `a + b`, `a += b`. */
    public data class Binary(
        public var attrs: List<Attribute>,
        public var left: Expr,
        public var op: BinOp,
        public var right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprBinary(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Binary = Binary(attrs.mapTo(mutableListOf()) { it.deepCopy() }, left.deepCopy(), op, right.deepCopy())
    }

    /** A blocked scope: `{ ... }`. */
    public data class BlockExpr(
        public var attrs: List<Attribute>,
        public var label: Label?,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            label?.toTokens(tokens)
            block.braceToken.surround(tokens) { inner ->
                innerAttrsToTokens(attrs, inner)
                for (stmt in block.stmts) stmt.toTokens(inner)
            }
        }

        override fun deepCopy(): BlockExpr = BlockExpr(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), block.deepCopy())
    }

    /** A `break`, with an optional label to break and an optional expression. */
    public data class Break(
        public var attrs: List<Attribute>,
        public var breakToken: io.github.kotlinmania.syn.token.Break,
        public var label: Lifetime?,
        public var expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprBreak(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Break = Break(attrs.mapTo(mutableListOf()) { it.deepCopy() }, breakToken, label?.deepCopy(), expr?.deepCopy())
    }

    /** A function call expression: `invoke(a, b)`. */
    public data class Call(
        public var attrs: List<Attribute>,
        public var func: Expr,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var args: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprCall(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Call = Call(attrs.mapTo(mutableListOf()) { it.deepCopy() }, func.deepCopy(), parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A cast expression: `foo as f64`. */
    public data class Cast(
        public var attrs: List<Attribute>,
        public var expr: Expr,
        public var asToken: io.github.kotlinmania.syn.token.As,
        public var ty: SynType,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprCast(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Cast = Cast(attrs.mapTo(mutableListOf()) { it.deepCopy() }, expr.deepCopy(), asToken, ty.deepCopy())
    }

    /** A closure expression: `|a, b| a + b`. */
    public data class Closure(
        public var attrs: List<Attribute>,
        public var lifetimes: BoundLifetimes?,
        public var constness: io.github.kotlinmania.syn.token.Const?,
        public var movability: io.github.kotlinmania.syn.token.Static?,
        public var asyncness: io.github.kotlinmania.syn.token.Async?,
        public var capture: io.github.kotlinmania.syn.token.Move?,
        public var or1Token: io.github.kotlinmania.syn.token.Or,
        public var inputs: PatList,
        public var or2Token: io.github.kotlinmania.syn.token.Or,
        public var output: ReturnType,
        public var body: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprClosure(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Closure = Closure(attrs.mapTo(mutableListOf()) { it.deepCopy() }, lifetimes?.deepCopy(), constness, movability, asyncness, capture, or1Token, inputs.copy({ it.deepCopy() }, { it }), or2Token, output.deepCopy(), body.deepCopy())
    }

    /** A const block: `const { ... }`. */
    public data class Const(
        public var attrs: List<Attribute>,
        public var constToken: io.github.kotlinmania.syn.token.Const,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            constToken.toTokens(tokens)
            block.braceToken.surround(tokens) { inner ->
                innerAttrsToTokens(attrs, inner)
                for (stmt in block.stmts) stmt.toTokens(inner)
            }
        }

        override fun deepCopy(): Const = Const(attrs.mapTo(mutableListOf()) { it.deepCopy() }, constToken, block.deepCopy())
    }

    /** A `continue`, with an optional label. */
    public data class Continue(
        public var attrs: List<Attribute>,
        public var continueToken: io.github.kotlinmania.syn.token.Continue,
        public var label: Lifetime?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            continueToken.toTokens(tokens)
            label?.toTokens(tokens)
        }

        override fun deepCopy(): Continue = Continue(attrs.mapTo(mutableListOf()) { it.deepCopy() }, continueToken, label?.deepCopy())
    }

    /** Access of a named field of a data class (`obj.k`) or indexed element of a tuple-like compound (`obj.0`). */
    public data class Field(
        public var attrs: List<Attribute>,
        public var base: Expr,
        public var dotToken: io.github.kotlinmania.syn.token.Dot,
        public var member: Member,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprField(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Field = Field(attrs.mapTo(mutableListOf()) { it.deepCopy() }, base.deepCopy(), dotToken, member)
    }

    /** A for loop: `for pat in expr { ... }`. */
    public data class ForLoop(
        public var attrs: List<Attribute>,
        public var label: Label?,
        public var forToken: io.github.kotlinmania.syn.token.For,
        public var pat: Pat,
        public var inToken: io.github.kotlinmania.syn.token.In,
        public var expr: Expr,
        public var body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            label?.toTokens(tokens)
            forToken.toTokens(tokens)
            pat.toTokens(tokens)
            inToken.toTokens(tokens)
            printExpr(expr, tokens, FixupContext.newCondition())
            body.braceToken.surround(tokens) { inner ->
                innerAttrsToTokens(attrs, inner)
                for (stmt in body.stmts) stmt.toTokens(inner)
            }
        }

        override fun deepCopy(): ForLoop = ForLoop(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), forToken, pat.deepCopy(), inToken, expr.deepCopy(), body.deepCopy())
    }

    /**
     * An expression contained within invisible delimiters.
     *
     * This variant is important for faithfully representing the precedence
     * of expressions and is related to `None`-delimited spans in a
     * `TokenStream`.
     */
    public data class Group(
        public var attrs: List<Attribute>,
        public var groupToken: io.github.kotlinmania.syn.token.Group,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            groupToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Group = Group(attrs.mapTo(mutableListOf()) { it.deepCopy() }, groupToken, expr.deepCopy())
    }

    /**
     * An `if` expression with an optional `else` block: `if expr { ... }
     * else { ... }`.
     *
     * The `else` branch expression may only be an `If` or `Block`
     * expression, not any of the other types of expression.
     */
    public data class If(
        public var attrs: List<Attribute>,
        public var ifToken: io.github.kotlinmania.syn.token.If,
        public var cond: Expr,
        public var thenBranch: Block,
        public var elseBranch: ElseExpr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            var expr = this
            while (true) {
                expr.ifToken.toTokens(tokens)
                printExpr(expr.cond, tokens, FixupContext.newCondition())
                expr.thenBranch.toTokens(tokens)
                val elseBranch = expr.elseBranch ?: break
                elseBranch.elseToken.toTokens(tokens)
                when (val other = elseBranch.expr) {
                    is If -> expr = other
                    is BlockExpr -> {
                        other.toTokens(tokens)
                        break
                    }
                    else -> {
                        io.github.kotlinmania.syn.token.Brace.default().surround(tokens) { inner ->
                            printExpr(other, inner, FixupContext.newStmt())
                        }
                        break
                    }
                }
            }
        }

        override fun deepCopy(): If = If(attrs.mapTo(mutableListOf()) { it.deepCopy() }, ifToken, cond.deepCopy(), thenBranch, elseBranch?.let { it.copy(expr = it.expr.deepCopy()) })
    }

    /** A square bracketed indexing expression: `vector[2]`. */
    public data class Index(
        public var attrs: List<Attribute>,
        public var expr: Expr,
        public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public var index: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprIndex(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Index = Index(attrs.mapTo(mutableListOf()) { it.deepCopy() }, expr.deepCopy(), bracketToken, index.deepCopy())
    }

    /** The inferred value of a const generic argument, denoted `_`. */
    public data class Infer(
        public var attrs: List<Attribute>,
        public var underscoreToken: io.github.kotlinmania.syn.token.Underscore,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            underscoreToken.toTokens(tokens)
        }

        override fun deepCopy(): Infer = Infer(attrs.mapTo(mutableListOf()) { it.deepCopy() }, underscoreToken)
    }

    /** A pattern guard that tests whether a pattern matches a value. */
    public data class Let(
        public var attrs: List<Attribute>,
        public var letToken: io.github.kotlinmania.syn.token.Let,
        public var pat: Pat,
        public var eqToken: io.github.kotlinmania.syn.token.Eq,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprLet(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Let = Let(attrs.mapTo(mutableListOf()) { it.deepCopy() }, letToken, pat.deepCopy(), eqToken, expr.deepCopy())
    }

    /** A literal in place of an expression: `1`, `"foo"`. */
    public data class Lit(
        var attrs: List<Attribute>,
        var lit: io.github.kotlinmania.syn.Lit,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            lit.toTokens(tokens)
        }

        override fun deepCopy(): Lit = Lit(attrs.mapTo(mutableListOf()) { it.deepCopy() }, lit)
    }

    /** Conditionless loop: `loop { ... }`. */
    public data class Loop(
        public var attrs: List<Attribute>,
        public var label: Label?,
        public var loopToken: io.github.kotlinmania.syn.token.Loop,
        public var body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            label?.toTokens(tokens)
            loopToken.toTokens(tokens)
            body.braceToken.surround(tokens) { inner ->
                innerAttrsToTokens(attrs, inner)
                for (stmt in body.stmts) stmt.toTokens(inner)
            }
        }

        override fun deepCopy(): Loop = Loop(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), loopToken, body.deepCopy())
    }

    /** A macro invocation expression. */
    public data class Macro(
        var attrs: List<Attribute>,
        var mac: io.github.kotlinmania.syn.Macro,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            mac.toTokens(tokens)
        }

        override fun deepCopy(): Macro = Macro(attrs.mapTo(mutableListOf()) { it.deepCopy() }, mac.deepCopy())
    }

    /** A `match` expression. */
    public data class Match(
        public var attrs: List<Attribute>,
        public var matchToken: io.github.kotlinmania.syn.token.Match,
        public var expr: Expr,
        public var braceToken: io.github.kotlinmania.syn.token.Brace,
        public var arms: List<Arm>,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            matchToken.toTokens(tokens)
            printExpr(expr, tokens, FixupContext.newCondition())
            braceToken.surround(tokens) { inner ->
                innerAttrsToTokens(attrs, inner)
                for ((i, arm) in arms.withIndex()) {
                    arm.toTokens(inner)
                    val isLast = i == arms.size - 1
                    if (!isLast && requiresCommaToBeMatchArm(arm.body) && arm.comma == null) {
                        io.github.kotlinmania.syn.token.Comma
                            .default()
                            .toTokens(inner)
                    }
                }
            }
        }

        override fun deepCopy(): Match = Match(attrs.mapTo(mutableListOf()) { it.deepCopy() }, matchToken, expr.deepCopy(), braceToken, arms.mapTo(mutableListOf()) { it.deepCopy() })
    }

    /** A method call expression with optional turbofish and arguments. */
    public data class MethodCall(
        public var attrs: List<Attribute>,
        public var receiver: Expr,
        public var dotToken: io.github.kotlinmania.syn.token.Dot,
        public var method: Ident,
        public var turbofish: PathArguments.AngleBracketed?,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var args: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprMethodCall(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): MethodCall = MethodCall(attrs.mapTo(mutableListOf()) { it.deepCopy() }, receiver.deepCopy(), dotToken, method.copy(), turbofish?.deepCopy() as? PathArguments.AngleBracketed?, parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A parenthesized expression: `(a + b)`. */
    public data class Paren(
        public var attrs: List<Attribute>,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            parenToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Paren = Paren(attrs.mapTo(mutableListOf()) { it.deepCopy() }, parenToken, expr.deepCopy())
    }

    /**
     * A path like `core.mem.replace` possibly containing generic
     * parameters and a qualified self-type.
     *
     * A plain identifier like `x` is a path of length 1.
     */
    public data class Path(
        var attrs: List<Attribute>,
        var qself: QSelf?,
        var path: io.github.kotlinmania.syn.Path,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            printQpath(tokens, qself, path, PathStyle.Expr)
        }

        override fun deepCopy(): Path = Path(attrs.mapTo(mutableListOf()) { it.deepCopy() }, qself, path.deepCopy())
    }

    /** A range expression: `1..2`, `1..`, `..2`, `1..=2`, `..=2`. */
    public data class Range(
        public var attrs: List<Attribute>,
        public var start: Expr?,
        public var limits: RangeLimits,
        public var end: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprRange(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Range = Range(attrs.mapTo(mutableListOf()) { it.deepCopy() }, start?.deepCopy(), limits, end?.deepCopy())
    }

    /** Address-of operation: `&raw const place` or `&raw mut place`. */
    public data class RawAddr(
        public var attrs: List<Attribute>,
        public var andToken: io.github.kotlinmania.syn.token.And,
        public var raw: io.github.kotlinmania.syn.token.Raw,
        public var mutability: PointerMutability,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprRawAddr(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): RawAddr = RawAddr(attrs.mapTo(mutableListOf()) { it.deepCopy() }, andToken, raw, mutability, expr.deepCopy())
    }

    /** A referencing operation. */
    public data class Reference(
        public var attrs: List<Attribute>,
        public var andToken: io.github.kotlinmania.syn.token.And,
        public var mutability: io.github.kotlinmania.syn.token.Mut?,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprReference(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Reference = Reference(attrs.mapTo(mutableListOf()) { it.deepCopy() }, andToken, mutability, expr.deepCopy())
    }

    /** An array literal constructed from one repeated element: `[0u8; N]`. */
    public data class Repeat(
        public var attrs: List<Attribute>,
        public var bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public var expr: Expr,
        public var semiToken: io.github.kotlinmania.syn.token.Semi,
        public var len: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            bracketToken.surround(tokens) { inner ->
                expr.toTokens(inner)
                semiToken.toTokens(inner)
                len.toTokens(inner)
            }
        }

        override fun deepCopy(): Repeat = Repeat(attrs.mapTo(mutableListOf()) { it.deepCopy() }, bracketToken, expr.deepCopy(), semiToken, len.deepCopy())
    }

    /** A `return`, with an optional value to be returned. */
    public data class Return(
        public var attrs: List<Attribute>,
        public var returnToken: io.github.kotlinmania.syn.token.Return,
        public var expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprReturn(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Return = Return(attrs.mapTo(mutableListOf()) { it.deepCopy() }, returnToken, expr?.deepCopy())
    }

    /**
     * A struct literal expression: `Point { x: 1, y: 1 }`.
     *
     * The `rest` provides the value of the remaining fields as in `S { a:
     * 1, b: 1, ..rest }`.
     */
    public data class Struct(
        public var attrs: List<Attribute>,
        public var qself: QSelf?,
        public var path: io.github.kotlinmania.syn.Path,
        public var braceToken: io.github.kotlinmania.syn.token.Brace,
        public var fields: FieldValueList,
        public var dot2Token: io.github.kotlinmania.syn.token.DotDot?,
        public var rest: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            printQpath(tokens, qself, path, PathStyle.Expr)
            braceToken.surround(tokens) { inner ->
                fields.toTokens(inner)
                val dot2 = dot2Token
                val restExpr = rest
                if (dot2 != null) {
                    dot2.toTokens(inner)
                } else if (restExpr != null) {
                    io.github.kotlinmania.syn.token.DotDot
                        .default()
                        .toTokens(inner)
                }
                restExpr?.toTokens(inner)
            }
        }

        override fun deepCopy(): Struct = Struct(attrs.mapTo(mutableListOf()) { it.deepCopy() }, qself, path.deepCopy(), braceToken, fields.copy({ it.deepCopy() }, { it }), dot2Token, rest?.deepCopy())
    }

    /** A try-expression: `expr?`. */
    public data class Try(
        public var attrs: List<Attribute>,
        public var expr: Expr,
        public var questionToken: io.github.kotlinmania.syn.token.Question,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprTry(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Try = Try(attrs.mapTo(mutableListOf()) { it.deepCopy() }, expr.deepCopy(), questionToken)
    }

    /** A try block: `try { ... }`. */
    public data class TryBlock(
        public var attrs: List<Attribute>,
        public var tryToken: io.github.kotlinmania.syn.token.Try,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            tryToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): TryBlock = TryBlock(attrs.mapTo(mutableListOf()) { it.deepCopy() }, tryToken, block.deepCopy())
    }

    /** A tuple expression: `(a, b, c, d)`. */
    public data class Tuple(
        public var attrs: List<Attribute>,
        public var parenToken: io.github.kotlinmania.syn.token.Paren,
        public var elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
                if (elems.len() == 1 && !elems.trailingPunct()) {
                    io.github.kotlinmania.syn.token.Comma
                        .default()
                        .toTokens(inner)
                }
            }
        }

        override fun deepCopy(): Tuple = Tuple(attrs.mapTo(mutableListOf()) { it.deepCopy() }, parenToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** A unary prefix operation: negation or dereference. */
    public data class Unary(
        public var attrs: List<Attribute>,
        public var op: UnOp,
        public var expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprUnary(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Unary = Unary(attrs.mapTo(mutableListOf()) { it.deepCopy() }, op, expr.deepCopy())
    }

    /** A block expression that permits operations violating memory safety invariants. */
    public data class Unsafe(
        public var attrs: List<Attribute>,
        public var unsafeToken: io.github.kotlinmania.syn.token.Unsafe,
        public var block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            unsafeToken.toTokens(tokens)
            block.braceToken.surround(tokens) { inner ->
                innerAttrsToTokens(attrs, inner)
                for (stmt in block.stmts) stmt.toTokens(inner)
            }
        }

        override fun deepCopy(): Unsafe = Unsafe(attrs.mapTo(mutableListOf()) { it.deepCopy() }, unsafeToken, block.deepCopy())
    }

    /** A while loop: `while expr { ... }`. */
    public data class While(
        public var attrs: List<Attribute>,
        public var label: Label?,
        public var whileToken: io.github.kotlinmania.syn.token.While,
        public var cond: Expr,
        public var body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            outerAttrsToTokens(attrs, tokens)
            label?.toTokens(tokens)
            whileToken.toTokens(tokens)
            printExpr(cond, tokens, FixupContext.newCondition())
            body.braceToken.surround(tokens) { inner ->
                innerAttrsToTokens(attrs, inner)
                for (stmt in body.stmts) stmt.toTokens(inner)
            }
        }

        override fun deepCopy(): While = While(attrs.mapTo(mutableListOf()) { it.deepCopy() }, label?.deepCopy(), whileToken, cond.deepCopy(), body.deepCopy())
    }

    /** A yield expression: `yield expr`. */
    public data class Yield(
        public var attrs: List<Attribute>,
        public var yieldToken: io.github.kotlinmania.syn.token.Yield,
        public var expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            printExprYield(this, tokens, FixupContext.NONE)
        }

        override fun deepCopy(): Yield = Yield(attrs.mapTo(mutableListOf()) { it.deepCopy() }, yieldToken, expr?.deepCopy())
    }

    /** Tokens in expression position not interpreted by Syn. */
    public data class Verbatim(
        var tokens: TokenStream,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(this.tokens))
        }

        override fun deepCopy(): Verbatim = Verbatim(tokens.clone())
    }

    public abstract fun deepCopy(): Expr

    public fun eq(other: Expr): Boolean = exprEq(this, other)

    public fun hash(): Int = exprHash(this)

    public fun fmt(): String = exprDebug(this)
}

/** A member of a data structure or tuple. */
public sealed class Member : ToTokens {
    /**
     * A named field like `self.x`.
     */
    public data class Named(
        var ident: Ident,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
        }
    }

    /**
     * An unnamed field like `self.0`.
     */
    public data class Unnamed(
        var index: Index,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            index.toTokens(tokens)
        }
    }
}

/** A tuple field index such as `0` in `obj.0`. */
public data class Index(
    public var index: UInt,
    public var span: Span,
) : ToTokens {
    override fun equals(other: Any?): Boolean =
        other is Index && index == other.index

    override fun hashCode(): Int = index.hashCode()

    override fun toTokens(tokens: TokenStream) {
        val literal =
            io.github.kotlinmania.procmacro2.Literal
                .i64Unsuffixed(index.toLong())
        literal.setSpan(span)
        tokens.append(literal)
    }
}

/** A field-value pair in a data-object initialization. */
public data class FieldValue(
    public var attrs: List<Attribute>,
    public var member: Member,
    public var colonToken: io.github.kotlinmania.syn.token.Colon?,
    public var expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        outerAttrsToTokens(attrs, tokens)
        member.toTokens(tokens)
        colonToken?.let { colon ->
            colon.toTokens(tokens)
            expr.toTokens(tokens)
        }
    }

    public fun deepCopy(): FieldValue = FieldValue(attrs.mapTo(mutableListOf()) { it.deepCopy() }, member, colonToken, expr.deepCopy())
}

/** A label on a `for`, `while`, or `loop`. */
public data class Label(
    public var name: Lifetime,
    public var colonToken: io.github.kotlinmania.syn.token.Colon,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        name.toTokens(tokens)
        colonToken.toTokens(tokens)
    }

    public fun deepCopy(): Label = Label(name.deepCopy(), colonToken)
}

/** One arm of a `match` expression. */
public data class Arm(
    public var attrs: List<Attribute>,
    public var pat: Pat,
    public var guard: IfExpr?,
    public var fatArrowToken: io.github.kotlinmania.syn.token.FatArrow,
    public var body: Expr,
    public var comma: io.github.kotlinmania.syn.token.Comma?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        pat.toTokens(tokens)
        guard?.toTokens(tokens)
        fatArrowToken.toTokens(tokens)
        printExpr(body, tokens, FixupContext.newMatchArm())
        comma?.toTokens(tokens)
    }

    public fun deepCopy(): Arm = Arm(attrs.mapTo(mutableListOf()) { it.deepCopy() }, pat.deepCopy(), guard?.let { it.copy(expr = it.expr.deepCopy()) }, fatArrowToken, body.deepCopy(), comma)
}

/** Limit types of a range, inclusive or exclusive. */
public sealed class RangeLimits : ToTokens {
    /**
     * Inclusive at the beginning, exclusive at the end.
     */
    public data class HalfOpen(
        var token: io.github.kotlinmania.syn.token.DotDot,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    /**
     * Inclusive at the beginning and end.
     */
    public data class Closed(
        var token: io.github.kotlinmania.syn.token.DotDotEq,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

/**
 * Mutability of a raw pointer (`*const T`, `*mut T`), in which non-mutable
 * isn't the implicit default.
 */
public sealed class PointerMutability : ToTokens {
    public data class Const(
        var token: io.github.kotlinmania.syn.token.Const,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    public data class Mut(
        var token: io.github.kotlinmania.syn.token.Mut,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

public object ExprParse {
    fun parse(input: ParseStream): SynResult<Expr> = parseExpr(input)
}

public fun parseExpr(input: ParseStream): SynResult<Expr> = parseExprFull(input)

public fun parseExprWithEarlierBoundaryRule(input: ParseStream): SynResult<Expr> =
    parseExprWithEarlierBoundaryRuleImpl(input)

public fun peekPrecedence(input: ParseStream): Precedence = peekPrecedenceImpl(input)

public fun checkCast(input: ParseStream): SynResult<Unit> = checkCastImpl(input)

public fun ambiguousExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    ambiguousExprImpl(input, allowStruct)

public fun exprAttrs(input: ParseStream): SynResult<List<Attribute>> = exprAttrsImpl(input)

public fun unaryExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    unaryExprImpl(input, allowStruct)

public fun trailerExpr(input: ParseStream, allowStruct: Boolean, attrs: List<Attribute> = mutableListOf()): SynResult<Expr> =
    trailerExprImpl(input, allowStruct, attrs)

public fun trailerHelper(input: ParseStream, e: Expr, allowStruct: Boolean = true): SynResult<Expr> =
    trailerHelperImpl(input, e)

public fun atomExpr(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    atomExprImpl(input, allowStruct)

public fun pathOrMacroOrStruct(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    pathOrMacroOrStructImpl(input, allowStruct)

public fun parenOrTuple(input: ParseStream): SynResult<Expr> = parenOrTupleImpl(input)

public fun arrayOrRepeat(input: ParseStream): SynResult<Expr> = arrayOrRepeatImpl(input)

public fun parseExprBinary(input: ParseStream, lhs: Expr, allowStruct: Boolean, base: Precedence): SynResult<Expr> =
    parseExprBinaryImpl(input, lhs, allowStruct, base)

public fun parseBinopRhs(input: ParseStream, allowStruct: Boolean, left: Precedence): SynResult<Expr> =
    parseBinopRhsImpl(input, allowStruct, left)

public fun parseExprGroup(input: ParseStream): SynResult<Expr> = parseExprGroupImpl(input)

public fun parseExprLet(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Let> =
    parseExprLetImpl(input, allowStruct)

public fun parseFieldValue(input: ParseStream): SynResult<FieldValue> = parseFieldValueImpl(input)

public fun parseMember(input: ParseStream): SynResult<Member> = parseMemberImpl(input)

public fun continueParsingEarly(expr: Expr): Boolean = continueParsingEarlyImpl(expr)

public fun Expr.replaceAttrs(attrs: List<Attribute>): Expr =
    when (this) {
        is Expr.Binary -> copy(attrs = attrs)
        is Expr.Assign -> copy(attrs = attrs)
        is Expr.Unary -> copy(attrs = attrs)
        is Expr.Call -> copy(attrs = attrs)
        is Expr.MethodCall -> copy(attrs = attrs)
        is Expr.Field -> copy(attrs = attrs)
        is Expr.Index -> copy(attrs = attrs)
        is Expr.Try -> copy(attrs = attrs)
        is Expr.Await -> copy(attrs = attrs)
        is Expr.Paren -> copy(attrs = attrs)
        is Expr.Tuple -> copy(attrs = attrs)
        is Expr.Array -> copy(attrs = attrs)
        is Expr.Repeat -> copy(attrs = attrs)
        is Expr.Range -> copy(attrs = attrs)
        is Expr.If -> copy(attrs = attrs)
        is Expr.While -> copy(attrs = attrs)
        is Expr.ForLoop -> copy(attrs = attrs)
        is Expr.Loop -> copy(attrs = attrs)
        is Expr.Match -> copy(attrs = attrs)
        is Expr.BlockExpr -> copy(attrs = attrs)
        is Expr.Async -> copy(attrs = attrs)
        is Expr.Unsafe -> copy(attrs = attrs)
        is Expr.Return -> copy(attrs = attrs)
        is Expr.Break -> copy(attrs = attrs)
        is Expr.Continue -> copy(attrs = attrs)
        is Expr.Closure -> copy(attrs = attrs)
        is Expr.Macro -> copy(attrs = attrs)
        is Expr.Struct -> copy(attrs = attrs)
        is Expr.Path -> copy(attrs = attrs)
        is Expr.Lit -> copy(attrs = attrs)
        is Expr.Group -> copy(attrs = attrs)
        is Expr.Infer -> copy(attrs = attrs)
        is Expr.Let -> copy(attrs = attrs)
        is Expr.Yield -> copy(attrs = attrs)
        is Expr.TryBlock -> copy(attrs = attrs)
        is Expr.Const -> copy(attrs = attrs)
        is Expr.Reference -> copy(attrs = attrs)
        is Expr.RawAddr -> copy(attrs = attrs)
        is Expr.Cast -> copy(attrs = attrs)
        is Expr.Verbatim -> this
    }

public fun Expr.isNamed(name: String): Boolean {
    if (this is Expr.Path) {
        val last = path.segments.last()
        return last?.ident?.toString() == name
    }
    return false
}

public fun Expr.span(): io.github.kotlinmania.procmacro2.Span =
    (this as ToTokens).span()

public fun printSubexpression(
    expr: Expr,
    needsGroup: Boolean,
    tokens: TokenStream,
    fixup: FixupContext,
) {
    val context = if (needsGroup) FixupContext.NONE else fixup
    val doPrintExpr = { output: TokenStream -> printExpr(expr, output, context) }
    if (needsGroup) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, doPrintExpr)
    } else {
        doPrintExpr(tokens)
    }
}

public fun printExpr(expr: Expr, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    val needsGroup = fixup.parenthesize(expr)
    val context = if (needsGroup) FixupContext.NONE else fixup
    val doPrintExpr = { output: TokenStream ->
        when (expr) {
            is Expr.Assign -> printExprAssign(expr, output, context)
            is Expr.Await -> printExprAwait(expr, output, context)
            is Expr.Binary -> printExprBinary(expr, output, context)
            is Expr.Break -> printExprBreak(expr, output, context)
            is Expr.Call -> printExprCall(expr, output, context)
            is Expr.Cast -> printExprCast(expr, output, context)
            is Expr.Closure -> printExprClosure(expr, output, context)
            is Expr.Field -> printExprField(expr, output, context)
            is Expr.Index -> printExprIndex(expr, output, context)
            is Expr.Let -> printExprLet(expr, output, context)
            is Expr.MethodCall -> printExprMethodCall(expr, output, context)
            is Expr.Range -> printExprRange(expr, output, context)
            is Expr.RawAddr -> printExprRawAddr(expr, output, context)
            is Expr.Reference -> printExprReference(expr, output, context)
            is Expr.Return -> printExprReturn(expr, output, context)
            is Expr.Try -> printExprTry(expr, output, context)
            is Expr.Unary -> printExprUnary(expr, output, context)
            is Expr.Yield -> printExprYield(expr, output, context)
            else -> expr.toTokens(output)
        }
    }
    if (needsGroup) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, doPrintExpr)
    } else {
        doPrintExpr(tokens)
    }
}

public fun printExprAssign(e: Expr.Assign, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val needsGroup = e.attrs.isNotEmpty()
    val context = if (needsGroup) FixupContext.NONE else fixup
    val doPrintExpr = { output: TokenStream ->
        val (leftPrec, leftFixup) = context.leftmostSubexpressionWithOperator(e.left, false, false, Precedence.Assign)
        printSubexpression(e.left, leftPrec <= Precedence.Range, output, leftFixup)
        e.eqToken.toTokens(output)
        printExpr(e.right, output, context.rightmostSubexpressionFixup(false, false, Precedence.Assign))
    }
    if (needsGroup) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, doPrintExpr)
    } else {
        doPrintExpr(tokens)
    }
}

public fun printExprAwait(e: Expr.Await, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val (leftPrec, leftFixup) = fixup.leftmostSubexpressionWithDot(e.base)
    printSubexpression(e.base, leftPrec < Precedence.Unambiguous, tokens, leftFixup)
    e.dotToken.toTokens(tokens)
    e.awaitToken.toTokens(tokens)
}

public fun printExprBinary(e: Expr.Binary, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val needsGroup = e.attrs.isNotEmpty()
    val context = if (needsGroup) FixupContext.NONE else fixup
    val doPrintExpr = { output: TokenStream ->
        val binopPrec = Precedence.ofBinop(e.op)
        val (leftPrec, leftFixup) =
            context.leftmostSubexpressionWithOperator(
                e.left,
                when (e.op) {
                    is BinOp.Sub,
                    is BinOp.Mul,
                    is BinOp.And,
                    is BinOp.Or,
                    is BinOp.BitAnd,
                    is BinOp.BitOr,
                    is BinOp.Shl,
                    is BinOp.Lt,
                    -> true
                    else -> false
                },
                e.op is BinOp.Shl || e.op is BinOp.Lt,
                binopPrec,
            )
        val leftNeedsGroup =
            when (binopPrec) {
                Precedence.Assign -> leftPrec <= Precedence.Range
                Precedence.Compare -> leftPrec <= binopPrec
                else -> leftPrec < binopPrec
            }
        val rightFixup = context.rightmostSubexpressionFixup(false, false, binopPrec)
        val rightNeedsGroup =
            binopPrec != Precedence.Assign &&
                rightFixup.rightmostSubexpressionPrecedence(e.right) <= binopPrec
        printSubexpression(e.left, leftNeedsGroup, output, leftFixup)
        e.op.toTokens(output)
        printSubexpression(e.right, rightNeedsGroup, output, rightFixup)
    }
    if (needsGroup) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, doPrintExpr)
    } else {
        doPrintExpr(tokens)
    }
}

public fun printExprBlock(e: Expr.BlockExpr, tokens: TokenStream) {
    e.toTokens(tokens)
}

public fun printExprBreak(e: Expr.Break, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    e.breakToken.toTokens(tokens)
    e.label?.toTokens(tokens)
    e.expr?.let { value ->
        printSubexpression(
            value,
            e.label == null && exprLeadingLabel(value),
            tokens,
            fixup.rightmostSubexpressionFixup(true, true, Precedence.Jump),
        )
    }
}

public fun printExprCall(e: Expr.Call, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val (leftPrec, leftFixup) = fixup.leftmostSubexpressionWithOperator(e.func, true, false, Precedence.Unambiguous)
    val func = e.func
    val needsGroup = if (func is Expr.Field) func.member is Member.Named else leftPrec < Precedence.Unambiguous
    printSubexpression(func, needsGroup, tokens, leftFixup)
    e.parenToken.surround(tokens) { inner -> e.args.toTokens(inner) }
}

public fun printExprCast(e: Expr.Cast, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val needsGroup = e.attrs.isNotEmpty()
    val context = if (needsGroup) FixupContext.NONE else fixup
    val doPrintExpr = { output: TokenStream ->
        val (leftPrec, leftFixup) = context.leftmostSubexpressionWithOperator(e.expr, false, false, Precedence.Cast)
        printSubexpression(e.expr, leftPrec < Precedence.Cast, output, leftFixup)
        e.asToken.toTokens(output)
        e.ty.toTokens(output)
    }
    if (needsGroup) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, doPrintExpr)
    } else {
        doPrintExpr(tokens)
    }
}

internal fun printExprClosure(e: Expr.Closure, tokens: TokenStream, fixup: FixupContext) {
    outerAttrsToTokens(e.attrs, tokens)
    e.lifetimes?.toTokens(tokens)
    e.constness?.toTokens(tokens)
    e.movability?.toTokens(tokens)
    e.asyncness?.toTokens(tokens)
    e.capture?.toTokens(tokens)
    e.or1Token.toTokens(tokens)
    e.inputs.toTokens(tokens)
    e.or2Token.toTokens(tokens)
    e.output.toTokens(tokens)
    if (e.output == ReturnType.Default ||
        e.body.let { body -> body is Expr.BlockExpr && body.attrs.isEmpty() && body.label == null }
    ) {
        printExpr(
            e.body,
            tokens,
            fixup.rightmostSubexpressionFixup(false, false, Precedence.Jump),
        )
    } else {
        io.github.kotlinmania.syn.token.Brace.default().surround(tokens) { inner ->
            printExpr(e.body, inner, FixupContext.newStmt())
        }
    }
}

public fun printExprField(e: Expr.Field, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val (leftPrec, leftFixup) = fixup.leftmostSubexpressionWithDot(e.base)
    printSubexpression(e.base, leftPrec < Precedence.Unambiguous, tokens, leftFixup)
    e.dotToken.toTokens(tokens)
    e.member.toTokens(tokens)
}

public fun printExprIndex(e: Expr.Index, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val (leftPrec, leftFixup) = fixup.leftmostSubexpressionWithOperator(e.expr, true, false, Precedence.Unambiguous)
    printSubexpression(e.expr, leftPrec < Precedence.Unambiguous, tokens, leftFixup)
    e.bracketToken.surround(tokens) { inner -> e.index.toTokens(inner) }
}

public fun printExprLet(e: Expr.Let, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    e.letToken.toTokens(tokens)
    e.pat.toTokens(tokens)
    e.eqToken.toTokens(tokens)
    val (rightPrec, rightFixup) = fixup.rightmostSubexpression(e.expr, Precedence.Let)
    printSubexpression(e.expr, rightPrec < Precedence.Let, tokens, rightFixup)
}

public fun printExprMethodCall(e: Expr.MethodCall, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val (leftPrec, leftFixup) = fixup.leftmostSubexpressionWithDot(e.receiver)
    printSubexpression(e.receiver, leftPrec < Precedence.Unambiguous, tokens, leftFixup)
    e.dotToken.toTokens(tokens)
    e.method.toTokens(tokens)
    e.turbofish?.let { printAngleBracketedGenericArguments(tokens, it, PathStyle.Expr) }
    e.parenToken.surround(tokens) { inner -> e.args.toTokens(inner) }
}

public fun printExprRange(e: Expr.Range, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val needsGroup = e.attrs.isNotEmpty()
    val context = if (needsGroup) FixupContext.NONE else fixup
    val doPrintExpr: (TokenStream) -> Unit = { output ->
        e.start?.let { start ->
            val (leftPrec, leftFixup) = context.leftmostSubexpressionWithOperator(start, true, false, Precedence.Range)
            printSubexpression(start, leftPrec <= Precedence.Range, output, leftFixup)
        }
        e.limits.toTokens(output)
        e.end?.let { end ->
            val rightFixup = context.rightmostSubexpressionFixup(false, true, Precedence.Range)
            val rightPrec = rightFixup.rightmostSubexpressionPrecedence(end)
            printSubexpression(end, rightPrec <= Precedence.Range, output, rightFixup)
        }
    }
    if (needsGroup) {
        io.github.kotlinmania.syn.token.Paren
            .default()
            .surround(tokens, doPrintExpr)
    } else {
        doPrintExpr(tokens)
    }
}

public fun printExprRawAddr(e: Expr.RawAddr, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    e.andToken.toTokens(tokens)
    e.raw.toTokens(tokens)
    e.mutability.toTokens(tokens)
    val (rightPrec, rightFixup) = fixup.rightmostSubexpression(e.expr, Precedence.Prefix)
    printSubexpression(e.expr, rightPrec < Precedence.Prefix, tokens, rightFixup)
}

public fun printExprReference(e: Expr.Reference, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    e.andToken.toTokens(tokens)
    e.mutability?.toTokens(tokens)
    val (rightPrec, rightFixup) = fixup.rightmostSubexpression(e.expr, Precedence.Prefix)
    printSubexpression(e.expr, rightPrec < Precedence.Prefix, tokens, rightFixup)
}

public fun printExprReturn(e: Expr.Return, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    e.returnToken.toTokens(tokens)
    e.expr?.let { printExpr(it, tokens, fixup.rightmostSubexpressionFixup(true, false, Precedence.Jump)) }
}

public fun printExprTry(e: Expr.Try, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    val (leftPrec, leftFixup) = fixup.leftmostSubexpressionWithDot(e.expr)
    printSubexpression(e.expr, leftPrec < Precedence.Unambiguous, tokens, leftFixup)
    e.questionToken.toTokens(tokens)
}

public fun printExprUnary(e: Expr.Unary, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    e.op.toTokens(tokens)
    val (rightPrec, rightFixup) = fixup.rightmostSubexpression(e.expr, Precedence.Prefix)
    printSubexpression(e.expr, rightPrec < Precedence.Prefix, tokens, rightFixup)
}

public fun printExprYield(e: Expr.Yield, tokens: TokenStream, fixup: FixupContext = FixupContext.NONE) {
    outerAttrsToTokens(e.attrs, tokens)
    e.yieldToken.toTokens(tokens)
    e.expr?.let { printExpr(it, tokens, fixup.rightmostSubexpressionFixup(true, false, Precedence.Jump)) }
}

public fun outerAttrsToTokens(attrs: List<Attribute>, tokens: TokenStream) {
    for (attr in attrs) {
        if (attr.style is AttrStyle.Outer) {
            attr.toTokens(tokens)
        }
    }
}

public fun innerAttrsToTokens(attrs: List<Attribute>, tokens: TokenStream) {
    for (attr in attrs) {
        if (attr.style is AttrStyle.Inner) {
            attr.toTokens(tokens)
        }
    }
}

public fun peekExpr(input: ParseStream): Boolean =
    (input.peek(IdentPeekAny) && !input.peek(AsPeek)) ||
        input.peek(ParenPeek) ||
        input.peek(BracketPeek) ||
        input.peek(BracePeek) ||
        input.peek(LitPeek) ||
        (input.peek(NotPeek) && !input.peek(NePeek)) ||
        (input.peek(MinusPeek) && !input.peek(MinusEqPeek) && !input.peek(RArrowPeek)) ||
        (input.peek(StarPeek) && !input.peek(StarEqPeek)) ||
        (input.peek(OrPeek) && !input.peek(OrEqPeek)) ||
        (input.peek(AndPeek) && !input.peek(AndEqPeek)) ||
        input.peek(DotDotPeek) ||
        (input.peek(LtPeek) && !input.peek(LePeek) && !input.peek(ShlEqPeek)) ||
        input.peek(PathSepPeek) ||
        input.peek(LifetimePeek) ||
        input.peek(PoundPeek)

public fun memberFromIdent(ident: io.github.kotlinmania.procmacro2.Ident): Member =
    Member.Named(ident)

public fun memberFromIndex(index: Index): Member =
    Member.Unnamed(index)

public fun memberFromUSize(index: Int): Member =
    Member.Unnamed(indexFromUSize(index))

public fun indexFromUSize(index: Int): Index {
    require(index < 0xFFFFFFFF) { "index overflow" }
    return Index(
        index.toUInt(),
        io.github.kotlinmania.procmacro2.Span
            .callSite(),
    )
}

public fun atomLabeled(input: ParseStream): SynResult<Expr> {
    val labelResult = LifetimeParse.parse(input)
    if (labelResult.isFailure) return SynResult.failure((labelResult as SynResult.Failure).error)
    val theLabel = labelResult.getOrThrow()
    val colonResult = ColonParse.parse(input)
    if (colonResult.isFailure) return SynResult.failure((colonResult as SynResult.Failure).error)
    val theLabelColon = colonResult.getOrThrow()
    val label = Label(theLabel, theLabelColon)
    val expr: Expr =
        when {
            input.peek(WhilePeek) -> {
                val whileResult = parseExprWhileLabeled(input)
                if (whileResult.isFailure) return whileResult
                whileResult.getOrThrow()
            }

            input.peek(ForPeek) -> {
                val forResult = parseExprForLabeled(input)
                if (forResult.isFailure) return forResult
                forResult.getOrThrow()
            }

            input.peek(LoopPeek) -> {
                val loopResult = parseExprLoopLabeled(input)
                if (loopResult.isFailure) return loopResult
                loopResult.getOrThrow()
            }

            input.peek(BracePeek) -> {
                val blockResult = parseExprBlock(input)
                if (blockResult.isFailure) return blockResult
                blockResult.getOrThrow()
            }

            else -> {
                return SynResult.failure(input.error("expected loop or block expression"))
            }
        }
    return when (expr) {
        is Expr.While -> SynResult.success(expr.copy(label = label))
        is Expr.ForLoop -> SynResult.success(expr.copy(label = label))
        is Expr.Loop -> SynResult.success(expr.copy(label = label))
        is Expr.BlockExpr -> SynResult.success(expr.copy(label = label))
        else -> SynResult.success(expr)
    }
}

internal fun parseExprWhileLabeled(input: ParseStream): SynResult<Expr> =
    parseExprWhile(input)

internal fun parseExprForLabeled(input: ParseStream): SynResult<Expr> =
    parseExprFor(input)

internal fun parseExprLoopLabeled(input: ParseStream): SynResult<Expr> =
    parseExprLoop(input)

public fun exprBuiltin(input: ParseStream): SynResult<Expr> {
    val begin = input.fork()
    val kwResult = keyword(input, "builtin")
    if (kwResult.isFailure) return SynResult.failure((kwResult as SynResult.Failure).error)
    val poundResult = PoundParse.parse(input)
    if (poundResult.isFailure) return SynResult.failure((poundResult as SynResult.Failure).error)
    val identResult = IdentParse.parse(input)
    if (identResult.isFailure) return SynResult.failure((identResult as SynResult.Failure).error)
    val parens = parenthesized(input)
    if (parens.isFailure) return SynResult.failure((parens as SynResult.Failure).error)
    val parensVal = parens.getOrThrow()
    parensVal.content.finishChildBuffer()
    val tokens = between(begin, input)
    return SynResult.success(Expr.Verbatim(tokens))
}

public fun restOfPathOrMacroOrStruct(
    qself: QSelf?,
    path: Path,
    input: ParseStream,
    allowStruct: Boolean,
): SynResult<Expr> {
    if (qself == null && input.peek(NotPeek) && !input.peek(NePeek) && path.isModStyle()) {
        val bangResult = NotParse.parse(input)
        if (bangResult.isFailure) return SynResult.failure((bangResult as SynResult.Failure).error)
        val delimResult = parseDelimiter(input)
        if (delimResult.isFailure) return SynResult.failure((delimResult as SynResult.Failure).error)
        var (delimiter, tokens) = delimResult.getOrThrow()
        return SynResult.success(
            Expr.Macro(
                mutableListOf(),
                Macro(path, bangResult.getOrThrow(), delimiter, tokens),
            ),
        )
    }
    if (allowStruct && input.peek(BracePeek)) {
        val structResult = exprStructHelper(input, qself, path)
        if (structResult.isFailure) return structResult
        return SynResult.success(structResult.getOrThrow())
    }
    return SynResult.success(Expr.Path(mutableListOf(), qself, path))
}

public fun exprStructHelper(
    input: ParseStream,
    qself: QSelf?,
    path: Path,
): SynResult<Expr.Struct> {
    val braces = braced(input)
    if (braces.isFailure) return SynResult.failure((braces as SynResult.Failure).error)
    val bracesVal = braces.getOrThrow()
    val content = bracesVal.content
    val fields = FieldValueList()
    while (!content.isEmpty()) {
        if (content.peek(DotDotPeek)) {
            val dot2Result = DotDotParse.parse(content)
            if (dot2Result.isFailure) return SynResult.failure((dot2Result as SynResult.Failure).error)
            val rest: Expr? =
                if (content.isEmpty()) {
                    null
                } else {
                    val restResult = parseExprFull(content)
                    if (restResult.isFailure) return SynResult.failure((restResult as SynResult.Failure).error)
                    restResult.getOrThrow()
                }
            content.finishChildBuffer()
            return SynResult.success(
                Expr.Struct(
                    mutableListOf(),
                    qself,
                    path,
                    bracesVal.token,
                    fields,
                    dot2Result.getOrThrow(),
                    rest,
                ),
            )
        }
        val fieldResult = parseFieldValueImpl(content)
        if (fieldResult.isFailure) return SynResult.failure((fieldResult as SynResult.Failure).error)
        fields.pushValue(fieldResult.getOrThrow())
        if (content.isEmpty()) break
        val punctResult = CommaParse.parse(content)
        if (punctResult.isFailure) break
        fields.pushPunct(punctResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(
        Expr.Struct(mutableListOf(), qself, path, bracesVal.token, fields, null, null),
    )
}

public fun exprLet(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Let> {
    val letResult = LetParse.parse(input)
    if (letResult.isFailure) return SynResult.failure((letResult as SynResult.Failure).error)
    val patResult = parsePatMultiWithLeadingVert(input)
    if (patResult.isFailure) return SynResult.failure((patResult as SynResult.Failure).error)
    val eqResult = EqParse.parse(input)
    if (eqResult.isFailure) return SynResult.failure((eqResult as SynResult.Failure).error)
    val lhsResult = unaryExprImpl(input, allowStruct)
    if (lhsResult.isFailure) return SynResult.failure((lhsResult as SynResult.Failure).error)
    val exprResult = parseExprBinaryImpl(input, lhsResult.getOrThrow(), allowStruct, Precedence.Compare)
    if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
    return SynResult.success(
        Expr.Let(
            mutableListOf(),
            letResult.getOrThrow(),
            patResult.getOrThrow(),
            eqResult.getOrThrow(),
            exprResult.getOrThrow(),
        ),
    )
}

public fun exprUnary(input: ParseStream, attrs: List<Attribute>, allowStruct: Boolean): SynResult<Expr.Unary> {
    val opResult = UnOpParse.parse(input)
    if (opResult.isFailure) return SynResult.failure((opResult as SynResult.Failure).error)
    val innerResult = unaryExprImpl(input, allowStruct)
    if (innerResult.isFailure) return SynResult.failure((innerResult as SynResult.Failure).error)
    return SynResult.success(Expr.Unary(attrs, opResult.getOrThrow(), innerResult.getOrThrow()))
}

public fun exprBecome(input: ParseStream): SynResult<Expr> {
    val begin = input.fork()
    val becomeResult = BecomeParse.parse(input)
    if (becomeResult.isFailure) return SynResult.failure((becomeResult as SynResult.Failure).error)
    val exprResult = parseExprFull(input)
    if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
    val tokens = between(begin, input)
    return SynResult.success(Expr.Verbatim(tokens))
}

internal fun exprClosure(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Closure> {
    val lifetimes: BoundLifetimes? = BoundLifetimes.parseOptional(input).getOrElse { return SynResult.failure(it) }
    val constness: io.github.kotlinmania.syn.token.Const? =
        if (input.peek(ConstPeek)) ConstParse.parse(input).getOrElse { return SynResult.failure(it) } else null
    val movability: io.github.kotlinmania.syn.token.Static? =
        if (input.peek(StaticPeek)) StaticParse.parse(input).getOrElse { return SynResult.failure(it) } else null
    val asyncness: io.github.kotlinmania.syn.token.Async? =
        if (input.peek(AsyncPeek)) AsyncParse.parse(input).getOrElse { return SynResult.failure(it) } else null
    val capture: io.github.kotlinmania.syn.token.Move? =
        if (input.peek(MovePeek)) MoveParse.parse(input).getOrElse { return SynResult.failure(it) } else null
    val or1Token: io.github.kotlinmania.syn.token.Or = OrParse.parse(input).getOrElse { return SynResult.failure(it) }

    val inputs = PatList()
    while (true) {
        if (input.peek(OrPeek)) {
            break
        }
        val value = closureArg(input).getOrElse { return SynResult.failure(it) }
        inputs.pushValue(value)
        if (input.peek(OrPeek)) {
            break
        }
        val punct: io.github.kotlinmania.syn.token.Comma = CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
        inputs.pushPunct(punct)
    }

    val or2Token: io.github.kotlinmania.syn.token.Or = OrParse.parse(input).getOrElse { return SynResult.failure(it) }

    val (output, body) =
        if (input.peek(RArrowPeek)) {
            val arrowToken: io.github.kotlinmania.syn.token.RArrow = RArrowParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty: SynType = SynTypeParseExpr.parse(input).getOrElse { return SynResult.failure(it) }
            val braces = braced(input).getOrElse { return SynResult.failure(it) }
            val stmts = parseWithin(braces.content).getOrElse { return SynResult.failure(it) }
            braces.content.finishChildBuffer()
            val body = Block(braces.token, stmts)
            val output = ReturnType.TypeReturn(arrowToken, ty)
            val block =
                Expr.BlockExpr(
                    attrs = mutableListOf(),
                    label = null,
                    block = body,
                )
            Pair(output, block)
        } else {
            val body = ambiguousExpr(input, allowStruct).getOrElse { return SynResult.failure(it) }
            Pair(ReturnType.Default, body)
        }

    return SynResult.success(
        Expr.Closure(
            attrs = mutableListOf(),
            lifetimes = lifetimes,
            constness = constness,
            movability = movability,
            asyncness = asyncness,
            capture = capture,
            or1Token = or1Token,
            inputs = inputs,
            or2Token = or2Token,
            output = output,
            body = body,
        ),
    )
}

public fun closureArg(input: ParseStream): SynResult<Pat> {
    val patResult = parsePatSingle(input)
    if (patResult.isFailure) return patResult
    val pat = patResult.getOrThrow()
    if (input.peek(ColonPeek)) {
        val colonResult = ColonParse.parse(input)
        if (colonResult.isFailure) return SynResult.failure((colonResult as SynResult.Failure).error)
        val tyResult = parseTypeFull(input)
        if (tyResult.isFailure) return SynResult.failure((tyResult as SynResult.Failure).error)
        return SynResult.success(Pat.TypeAscription(mutableListOf(), pat, colonResult.getOrThrow(), tyResult.getOrThrow()))
    }
    return SynResult.success(pat)
}

public fun exprBreak(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Break> {
    val breakResult = BreakParse.parse(input)
    if (breakResult.isFailure) return SynResult.failure((breakResult as SynResult.Failure).error)
    val ahead = input.fork()
    val labelResult = LifetimeParse.parse(ahead)
    val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
    if (label != null && ahead.peek(ColonPeek)) {
        val exprResult = parseExprFull(input)
        if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
        return SynResult.failure(SynError.new2(label.apostrophe, input.span(), "parentheses required"))
    }
    input.advanceTo(ahead)
    val expr: Expr? =
        if (peekExpr(input) && (allowStruct || !input.peek(BracePeek))) {
            val exprResult = parseExprFull(input)
            if (exprResult.isFailure) return SynResult.failure((exprResult as SynResult.Failure).error)
            exprResult.getOrThrow()
        } else {
            null
        }
    return SynResult.success(
        Expr.Break(mutableListOf(), breakResult.getOrThrow(), label, expr),
    )
}

public fun exprRange(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Range> {
    val limitsResult = RangeLimitsParse.parse(input)
    if (limitsResult.isFailure) return SynResult.failure((limitsResult as SynResult.Failure).error)
    val limits = limitsResult.getOrThrow()
    val endResult = parseRangeEnd(input, limits, allowStruct)
    if (endResult.isFailure) return SynResult.failure((endResult as SynResult.Failure).error)
    return SynResult.success(
        Expr.Range(mutableListOf(), null, limits, endResult.getOrThrow()),
    )
}

public object RangeLimitsParse {
    fun parse(input: ParseStream): SynResult<RangeLimits> {
        if (input.peek(DotDotEqPeek)) {
            val result = DotDotEqParse.parse(input)
            if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
            return SynResult.success(RangeLimits.Closed(result.getOrThrow()))
        }
        if (input.peek(DotDotPeek) && !input.peek(DotDotDotPeek)) {
            val result = DotDotParse.parse(input)
            if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
            return SynResult.success(RangeLimits.HalfOpen(result.getOrThrow()))
        }
        return SynResult.failure(input.error("expected .. or ..="))
    }
}

public object ArmParse {
    fun parse(input: ParseStream): SynResult<Arm> {
        val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
        val pat = parsePatMultiWithLeadingVert(input).getOrElse { return SynResult.failure(it) }
        val guard =
            if (input.peek(IfPeek)) {
                val ifToken = IfParse.parse(input).getOrElse { return SynResult.failure(it) }
                val guardExpr = parseExprFull(input).getOrElse { return SynResult.failure(it) }
                IfExpr(ifToken, guardExpr)
            } else {
                null
            }
        val fatArrowToken = FatArrowParse.parse(input).getOrElse { return SynResult.failure(it) }
        val body = parseWithEarlierBoundaryRule(input).getOrElse { return SynResult.failure(it) }
        val comma =
            if (requiresCommaToBeMatchArm(body) && !input.isEmpty()) {
                CommaParse.parse(input).getOrElse { return SynResult.failure(it) }
            } else {
                CommaParse.parse(input).getOrNull()
            }
        return SynResult.success(
            Arm(
                attrs,
                pat,
                guard,
                fatArrowToken,
                body,
                comma,
            ),
        )
    }
}

public fun parseRangeEnd(input: ParseStream, limits: RangeLimits, allowStruct: Boolean): SynResult<Expr?> {
    val isHalfOpen = limits is RangeLimits.HalfOpen
    val stop =
        isHalfOpen &&
            (
                input.isEmpty() ||
                    input.peek(CommaPeek) ||
                    input.peek(SemiPeek) ||
                    (input.peek(DotPeek) && !input.peek(DotDotPeek)) ||
                    input.peek(QuestionPeek) ||
                    input.peek(FatArrowPeek) ||
                    (!allowStruct && input.peek(BracePeek)) ||
                    input.peek(EqPeek) ||
                    input.peek(PlusPeek) ||
                    input.peek(SlashPeek) ||
                    input.peek(PercentPeek) ||
                    input.peek(CaretPeek) ||
                    input.peek(GtPeek) ||
                    input.peek(LePeek) ||
                    input.peek(NePeek) ||
                    input.peek(MinusEqPeek) ||
                    input.peek(StarEqPeek) ||
                    input.peek(AndEqPeek) ||
                    input.peek(OrEqPeek) ||
                    input.peek(ShlEqPeek) ||
                    input.peek(AsPeek)
            )
    if (stop) {
        return SynResult.success(null)
    }
    val endResult = parseBinopRhsImpl(input, allowStruct, Precedence.Range)
    if (endResult.isFailure) return SynResult.failure((endResult as SynResult.Failure).error)
    return SynResult.success(endResult.getOrThrow())
}

public fun parseObsoleteRangeLimits(input: ParseStream): SynResult<RangeLimits> {
    val dotDot = input.peek(DotDotPeek)
    val dotDotEq = dotDot && input.peek(DotDotEqPeek)
    val dotDotDot = dotDot && input.peek(DotDotDotPeek)
    if (dotDotEq) {
        val result = DotDotEqParse.parse(input)
        if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
        return SynResult.success(RangeLimits.Closed(result.getOrThrow()))
    }
    if (dotDotDot) {
        val result = DotDotDotParse.parse(input)
        if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
        return SynResult.success(
            RangeLimits.Closed(
                io.github.kotlinmania.syn.token.DotDotEq
                    .from(result.getOrThrow().spans),
            ),
        )
    }
    if (dotDot) {
        val result = DotDotParse.parse(input)
        if (result.isFailure) return SynResult.failure((result as SynResult.Failure).error)
        return SynResult.success(RangeLimits.HalfOpen(result.getOrThrow()))
    }
    return SynResult.failure(input.error("expected .. or ..="))
}

public fun parseMultipleArms(input: ParseStream): SynResult<List<Arm>> {
    val arms = mutableListOf<Arm>()
    while (!input.isEmpty()) {
        val armResult = ArmParse.parse(input)
        if (armResult.isFailure) return SynResult.failure((armResult as SynResult.Failure).error)
        arms.add(armResult.getOrThrow())
    }
    return SynResult.success(arms)
}

public fun multiIndex(e: Expr, dotToken: io.github.kotlinmania.syn.token.Dot, float: LitFloat): SynResult<MultiIndexResult> {
    val floatToken = float.token()
    val floatSpan = floatToken.span()
    var floatRepr = floatToken.toString()
    val trailingDot = floatRepr.endsWith('.')
    if (trailingDot) {
        floatRepr = floatRepr.dropLast(1)
    }
    var offset = 0
    var currentExpr = e
    var currentDot = dotToken
    for (part in floatRepr.split('.')) {
        val index: Index = Index(part.toUInt(), floatSpan)
        val partEnd = offset + part.length
        val base = currentExpr
        currentExpr =
            Expr.Field(
                mutableListOf(),
                base,
                currentDot,
                Member.Unnamed(index),
            )
        currentDot =
            io.github.kotlinmania.syn.token.Dot
                .from(floatSpan)
        offset = partEnd + 1
    }
    return SynResult.success(MultiIndexResult(currentExpr, !trailingDot))
}

@JvmInline
internal value class AllowStruct(
    val value: Boolean,
)

/**
 * An alternative to the primary [ExprParse.parse] parser for ambiguous
 * syntactic positions in which a trailing brace should not be taken as part of
 * the expression.
 *
 * Rust grammar has an ambiguity where braces sometimes turn a path
 * expression into a struct initialization and sometimes do not. In the
 * following code, the expression `S {}` is one expression. Presumably
 * there is an empty struct `class S` defined somewhere which it is
 * instantiating.
 *
 * ```kotlin
 * S()
 * // parsed by rustc as: `*(S {})`
 * ```
 *
 * We would want to parse the above using [ExprParse.parse] after the `=`
 * token.
 *
 * But in the following, `S {}` is *not* a struct init expression.
 *
 * ```kotlin
 * if (s) {}
 * // parsed by rustc as:
 * //
 * //    if (*S) {
 * //        // empty block
 * //    }
 * //    {
 * //        // another empty block
 * //    }
 * ```
 *
 * For that reason we would want to parse if-conditions using
 * [parseWithoutEagerBrace] after the `if` token. Same for similar
 * syntactic positions such as the condition expr after a `while` token or
 * the expr at the top of a `when`.
 *
 * The Rust grammar's choices around which way this ambiguity is resolved
 * at various syntactic positions is fairly arbitrary. Really either parse
 * behavior could work in most positions, and language designers just
 * decide each case based on which is more likely to be what the programmer
 * had in mind most of the time.
 *
 * ```kotlin
 * if (return S()) {}
 * // parsed by rustc as:
 * //
 * //    if (return (S {})) {
 * //    }
 * //
 * // but could equally well have been this other arbitrary choice:
 * //
 * //    if (return S) {
 * //    }
 * //    {}
 * ```
 *
 * Note the grammar ambiguity on trailing braces is distinct from
 * precedence and is not captured by assigning a precedence level to the
 * braced struct init expr in relation to other operators. This can be
 * illustrated by `return 0..S {}` vs `when (0..S) {}`. The former parses as
 * `return (0..(S {}))` implying tighter precedence for struct init than
 * `..`, while the latter parses as `when ((0..S)) {}` implying tighter
 * precedence for `..` than struct init, a contradiction.
 */
public fun parseWithoutEagerBrace(input: ParseStream): SynResult<Expr> =
    ambiguousExprImpl(input, allowStruct = false)

public fun parseWithEarlierBoundaryRule(input: ParseStream): SynResult<Expr> =
    parseExprWithEarlierBoundaryRuleImpl(input)

/**
 * Returns whether the next token in the parse stream is one that might
 * possibly form the beginning of an expr.
 *
 * This classification is a load-bearing part of the grammar of some Rust
 * expressions, notably `return` and `break`. For example `return < …` will
 * never parse `<` as a binary operator regardless of what comes after,
 * because `<` is a legal starting token for an expression and so it's
 * required to be continued as a return value, such as a qualified path
 * constant. Meanwhile `return > …` treats the `>` as a binary
 * operator because it cannot be a starting token for any Rust expression.
 */
public fun peek(input: ParseStream): Boolean = peekExpr(input)

public fun from(ident: io.github.kotlinmania.procmacro2.Ident): Member = Member.Named(ident)

public fun from(index: Index): Member = Member.Unnamed(index)

public fun from(index: Int): Member = Member.Unnamed(Index(index.toUInt(), Span.callSite()))

public fun exprGroup(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    parseExprGroupImpl(input)

internal fun clone(allowStruct: AllowStruct): AllowStruct = allowStruct

public fun parseObsolete(input: ParseStream): SynResult<RangeLimits> = parseObsoleteRangeLimits(input)

public fun parseMultiple(input: ParseStream): SynResult<List<Arm>> = parseMultipleArms(input)

private fun <T, R> SynResult<T>.asFailure(): SynResult<R> =
    SynResult.failure((this as SynResult.Failure).error)

internal fun parseExprFull(input: ParseStream): SynResult<Expr> =
    ambiguousExprImpl(input, allowStruct = true)

internal fun parseExprWithEarlierBoundaryRuleImpl(input: ParseStream): SynResult<Expr> {
    val attrs = exprAttrsImpl(input).getOrElse { return SynResult.failure(it) }
    var expr =
        when {
            startsWithNoneGroup(input) -> {
                val atom = parseExprGroupImpl(input).getOrElse { return SynResult.failure(it) }
                if (continueParsingEarlyImpl(atom)) {
                    trailerHelperImpl(input, atom).getOrElse { return SynResult.failure(it) }
                } else {
                    atom
                }
            }
            input.peek(IfPeek) -> parseExprIf(input).getOrElse { return SynResult.failure(it) }
            input.peek(WhilePeek) -> parseExprWhile(input).getOrElse { return SynResult.failure(it) }
            input.peek(ForPeek) && !chooseGenericsOverQpathAfterKeyword(input) -> parseExprFor(input).getOrElse { return SynResult.failure(it) }
            input.peek(LoopPeek) -> parseExprLoop(input).getOrElse { return SynResult.failure(it) }
            input.peek(MatchPeek) -> parseExprMatch(input).getOrElse { return SynResult.failure(it) }
            input.peek(TryPeek) && input.peek2(BracePeek) -> parseExprTryBlock(input).getOrElse { return SynResult.failure(it) }
            input.peek(UnsafePeek) -> parseExprUnsafe(input).getOrElse { return SynResult.failure(it) }
            input.peek(ConstPeek) && input.peek2(BracePeek) -> parseExprConst(input).getOrElse { return SynResult.failure(it) }
            input.peek(BracePeek) -> parseExprBlock(input).getOrElse { return SynResult.failure(it) }
            input.peek(LifetimePeek) -> parseLabeledLoopOrWhile(input).getOrElse { return SynResult.failure(it) }
            else -> unaryExprImpl(input, allowStruct = true).getOrElse { return SynResult.failure(it) }
        }

    if (continueParsingEarlyImpl(expr)) {
        return parseExprBinaryImpl(input, expr.withPrependedAttrs(attrs), allowStruct = true, base = Precedence.MIN)
    }

    if ((input.peek(DotPeek) && !input.peek(DotDotPeek)) || input.peek(QuestionPeek)) {
        val trailed = trailerHelperImpl(input, expr)
        if (trailed.isFailure) return SynResult.failure((trailed as SynResult.Failure).error)
        expr = trailed.getOrThrow()
        return parseExprBinaryImpl(input, expr.withPrependedAttrs(attrs), allowStruct = true, base = Precedence.MIN)
    }
    return SynResult.success(expr.withPrependedAttrs(attrs))
}

internal fun continueParsingEarlyImpl(expr: Expr): Boolean =
    when (expr) {
        is Expr.Group -> continueParsingEarlyImpl(expr.expr)
        is Expr.If,
        is Expr.While,
        is Expr.ForLoop,
        is Expr.TryBlock,
        is Expr.Loop,
        is Expr.Match,
        is Expr.Unsafe,
        is Expr.Const,
        is Expr.BlockExpr,
        -> false

        else -> true
    }

internal fun ambiguousExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val lhs = unaryExprImpl(input, allowStruct)
    if (lhs.isFailure) return lhs
    return parseExprBinaryImpl(input, lhs.getOrThrow(), allowStruct, Precedence.MIN)
}

internal fun parseExprBinaryImpl(
    input: ParseStream,
    lhs: Expr,
    allowStruct: Boolean,
    base: Precedence,
): SynResult<Expr> {
    var current = lhs
    while (true) {
        if (current is Expr.Range) break
        val ahead = input.fork()
        val opResult = BinOpParse.parse(ahead)
        if (opResult.isSuccess) {
            val op = opResult.getOrThrow()
            val precedence = Precedence.ofBinop(op)
            if (precedence.ordinal < base.ordinal) break
            if (precedence == Precedence.Compare && current is Expr.Binary) {
                if (Precedence.ofBinop(current.op) == Precedence.Compare) {
                    return SynResult.failure(input.error("comparison operators cannot be chained"))
                }
            }
            input.advanceTo(ahead)
            val rhsResult = parseBinopRhsImpl(input, allowStruct, precedence)
            if (rhsResult.isFailure) return rhsResult
            current = Expr.Binary(mutableListOf(), current, op, rhsResult.getOrThrow())
        } else if (Precedence.Assign.ordinal >= base.ordinal &&
            input.peek(EqPeek) &&
            !input.peek(FatArrowPeek) &&
            current !is Expr.Range
        ) {
            val eqResult = EqParse.parse(input)
            if (eqResult.isFailure) return eqResult.asFailure()
            val rhsResult = parseBinopRhsImpl(input, allowStruct, Precedence.Assign)
            if (rhsResult.isFailure) return rhsResult
            current = Expr.Assign(mutableListOf(), current, eqResult.getOrThrow(), rhsResult.getOrThrow())
        } else if (Precedence.Range.ordinal >= base.ordinal &&
            (input.peek(DotDotPeek) || input.peek(DotDotEqPeek))
        ) {
            val limitsResult = parseRangeLimits(input)
            if (limitsResult.isFailure) return limitsResult.asFailure()
            val endResult = parseRangeEnd(input, limitsResult.getOrThrow(), allowStruct)
            if (endResult.isFailure) return endResult.asFailure()
            current = Expr.Range(mutableListOf(), current, limitsResult.getOrThrow(), endResult.getOrThrow())
        } else if (Precedence.Cast.ordinal >= base.ordinal && input.peek(AsPeek)) {
            val asResult = AsParse.parse(input)
            if (asResult.isFailure) return asResult.asFailure()
            val tyResult = parseTypeWithoutPlus(input, allowGroupGeneric = false)
            if (tyResult.isFailure) return tyResult.asFailure()
            val castCheck = checkCastImpl(input)
            if (castCheck.isFailure) return castCheck.asFailure()
            current = Expr.Cast(mutableListOf(), current, asResult.getOrThrow(), tyResult.getOrThrow())
        } else {
            break
        }
    }
    return SynResult.success(current)
}

internal fun parseBinopRhsImpl(
    input: ParseStream,
    allowStruct: Boolean,
    left: Precedence,
): SynResult<Expr> {
    val rhs = unaryExprImpl(input, allowStruct)
    if (rhs.isFailure) return rhs
    var rhsExpr = rhs.getOrThrow()
    while (true) {
        val next = peekPrecedenceImpl(input)
        if (next.ordinal <= left.ordinal && !(next == left && left == Precedence.Assign)) break
        val cursor = input.currentCursor
        val inner = parseExprBinaryImpl(input, rhsExpr, allowStruct, next)
        if (inner.isFailure) return inner
        if (sameCursor(cursor, input.currentCursor)) break
        rhsExpr = inner.getOrThrow()
    }
    return SynResult.success(rhsExpr)
}

private fun sameCursor(left: Cursor, right: Cursor): Boolean =
    left.entries === right.entries && left.index == right.index && left.scope == right.scope

internal fun peekPrecedenceImpl(input: ParseStream): Precedence {
    val op = BinOpParse.parse(input.fork())
    if (op.isSuccess) return Precedence.ofBinop(op.getOrThrow())
    if (input.peek(EqPeek) && !input.peek(FatArrowPeek)) return Precedence.Assign
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek)) return Precedence.Range
    if (input.peek(AsPeek)) return Precedence.Cast
    return Precedence.MIN
}

internal fun checkCastImpl(input: ParseStream): SynResult<Unit> {
    val kind =
        when {
            input.peek(DotPeek) && !input.peek(DotDotPeek) -> {
                if (input.peek2(AwaitPeek)) {
                    "`.await`"
                } else if (input.peek2(IdentPeek) && (input.peek3(ParenPeek) || input.peek3(PathSepPeek))) {
                    "a method call"
                } else {
                    "a field access"
                }
            }

            input.peek(QuestionPeek) -> {
                "`?`"
            }

            input.peek(BracketPeek) -> {
                "indexing"
            }

            input.peek(ParenPeek) -> {
                "a function call"
            }

            else -> {
                return SynResult.success(Unit)
            }
        }
    return SynResult.failure(input.error("casts cannot be followed by $kind"))
}

internal fun exprAttrsImpl(input: ParseStream): SynResult<List<Attribute>> {
    val attrs = mutableListOf<Attribute>()
    while (!startsWithNoneGroup(input) && input.peek(PoundPeek) && input.peek2(BracketPeek)) {
        attrs.add(AttributeParse.parse(input).getOrElse { return SynResult.failure(it) })
    }
    return SynResult.success(attrs)
}

internal fun unaryExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val attrs = exprAttrsImpl(input).getOrElse { return SynResult.failure(it) }
    if (peekExprGroup(input, allowStruct)) {
        return trailerExprImpl(input, allowStruct, attrs)
    }
    if (input.peek(AndPeek)) {
        val andToken = AndParse.parse(input)
        if (andToken.isFailure) return andToken.asFailure()
        if (input.peek(RawPeek) && (input.peek2(MutPeek) || input.peek2(ConstPeek))) {
            val rawToken = RawParse.parse(input)
            if (rawToken.isFailure) return rawToken.asFailure()
            val mutResult = MutParse.parse(input)
            val mutability =
                if (mutResult.isSuccess) {
                    PointerMutability.Mut(mutResult.getOrThrow())
                } else {
                    val constResult = ConstParse.parse(input)
                    if (constResult.isFailure) return constResult.asFailure()
                    PointerMutability.Const(constResult.getOrThrow())
                }
            val inner = unaryExprImpl(input, allowStruct)
            if (inner.isFailure) return inner
            return SynResult.success(
                Expr.RawAddr(attrs, andToken.getOrThrow(), rawToken.getOrThrow(), mutability, inner.getOrThrow()),
            )
        }
        val mutResult = MutParse.parse(input)
        val mutability = if (mutResult.isSuccess) mutResult.getOrThrow() else null
        val inner = unaryExprImpl(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Reference(attrs, andToken.getOrThrow(), mutability, inner.getOrThrow()))
    }
    if (input.peek(NotPeek) || input.peek(StarPeek) || input.peek(MinusPeek)) {
        val opResult = UnOpParse.parse(input)
        if (opResult.isFailure) return opResult.asFailure()
        val inner = unaryExprImpl(input, allowStruct)
        if (inner.isFailure) return inner
        return SynResult.success(Expr.Unary(attrs, opResult.getOrThrow(), inner.getOrThrow()))
    }
    return trailerExprImpl(input, allowStruct, attrs)
}

internal fun trailerExprImpl(input: ParseStream, allowStruct: Boolean, attrs: List<Attribute> = mutableListOf()): SynResult<Expr> {
    val atomResult = atomExprImpl(input, allowStruct)
    if (atomResult.isFailure) return atomResult
    val trailed = trailerHelperImpl(input, atomResult.getOrThrow())
    if (trailed.isFailure) return trailed
    val expr = trailed.getOrThrow()
    if (attrs.isEmpty()) return SynResult.success(expr)
    if (expr is Expr.Range && expr.start == null) {
        return SynResult.failure(input.error("attributes are not allowed on range expressions starting with `..`"))
    }
    return SynResult.success(expr.withPrependedAttrs(attrs))
}

private fun Expr.withPrependedAttrs(attrs: List<Attribute>): Expr =
    when (this) {
        is Expr.Array -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Assign -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Async -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Await -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Binary -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.BlockExpr -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Break -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Call -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Cast -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Closure -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Const -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Continue -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Field -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.ForLoop -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Group -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.If -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Index -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Infer -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Let -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Lit -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Loop -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Macro -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Match -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.MethodCall -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Paren -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Path -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Range -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.RawAddr -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Reference -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Repeat -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Return -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Struct -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Try -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.TryBlock -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Tuple -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Unary -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Unsafe -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.While -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Yield -> copy(attrs = (attrs + this.attrs).toMutableList())
        is Expr.Verbatim -> this
    }

internal fun trailerHelperImpl(input: ParseStream, e: Expr): SynResult<Expr> {
    var current = e
    while (true) {
        if (input.peek(ParenPeek)) {
            val parens = parenthesized(input)
            if (parens.isFailure) return parens.asFailure()
            val parensVal = parens.getOrThrow()
            val paren = parensVal.token
            val content = parensVal.content
            val args = ExprList()
            while (!content.isEmpty()) {
                val argResult = parseExprFull(content)
                if (argResult.isFailure) return argResult
                args.pushValue(argResult.getOrThrow())
                if (content.isEmpty()) break
                val commaResult = CommaParse.parse(content)
                if (commaResult.isFailure) break
                args.pushPunct(commaResult.getOrThrow())
            }
            content.finishChildBuffer()
            current = Expr.Call(mutableListOf(), current, paren, args)
        } else if (input.peek(DotPeek) && !input.peek(DotDotPeek) && current !is Expr.Range) {
            val dotResult = DotParse.parse(input)
            if (dotResult.isFailure) return dotResult.asFailure()
            val dotToken = dotResult.getOrThrow()
            val floatAhead = input.fork()
            val floatResult = LitFloatParse.parse(floatAhead)
            if (floatResult.isSuccess) {
                input.advanceTo(floatAhead)
                val multi = multiIndexImpl(current, dotToken, floatResult.getOrThrow()).getOrElse { return SynResult.failure(it) }
                current = multi.expr
                if (multi.complete) {
                    continue
                }
            }
            if (input.peek(AwaitPeek)) {
                val awaitResult = AwaitParse.parse(input)
                if (awaitResult.isFailure) return awaitResult.asFailure()
                current = Expr.Await(mutableListOf(), current, dotToken, awaitResult.getOrThrow())
                continue
            }
            val memberResult = parseMemberImpl(input)
            if (memberResult.isFailure) return memberResult.asFailure()
            val member = memberResult.getOrThrow()
            val turbofish =
                if (member is Member.Named && input.peek(PathSepPeek)) {
                    PathArguments.AngleBracketed.parseTurbofish(input).getOrElse { return SynResult.failure(it) }
                } else {
                    null
                }
            if ((turbofish != null || input.peek(ParenPeek)) && member is Member.Named) {
                val parens = parenthesized(input)
                if (parens.isFailure) return parens.asFailure()
                val parensVal = parens.getOrThrow()
                val paren = parensVal.token
                val content = parensVal.content
                val args = ExprList()
                while (!content.isEmpty()) {
                    val argResult = parseExprFull(content)
                    if (argResult.isFailure) return argResult
                    args.pushValue(argResult.getOrThrow())
                    if (content.isEmpty()) break
                    val commaResult = CommaParse.parse(content)
                    if (commaResult.isFailure) break
                    args.pushPunct(commaResult.getOrThrow())
                }
                content.finishChildBuffer()
                current = Expr.MethodCall(mutableListOf(), current, dotToken, member.ident, turbofish, paren, args)
                continue
            }
            current = Expr.Field(mutableListOf(), current, dotToken, member)
        } else if (input.peek(BracketPeek)) {
            val brackets = bracketed(input)
            if (brackets.isFailure) return brackets.asFailure()
            val bracketsVal = brackets.getOrThrow()
            val bracket = bracketsVal.token
            val content = bracketsVal.content
            val indexResult = parseExprFull(content)
            if (indexResult.isFailure) return indexResult
            content.finishChildBuffer()
            current = Expr.Index(mutableListOf(), current, bracket, indexResult.getOrThrow())
        } else if (input.peek(QuestionPeek) && current !is Expr.Range) {
            val qResult = QuestionParse.parse(input)
            if (qResult.isFailure) return qResult.asFailure()
            current = Expr.Try(mutableListOf(), current, qResult.getOrThrow())
        } else {
            break
        }
    }
    return SynResult.success(current)
}

internal fun parseMemberImpl(input: ParseStream): SynResult<Member> {
    if (input.peek(IdentPeek)) {
        val identResult = IdentParse.parse(input)
        if (identResult.isFailure) return identResult.asFailure()
        return SynResult.success(Member.Named(identResult.getOrThrow()))
    }
    val ahead = input.fork()
    val litResult = LitIntParse.parse(ahead)
    if (litResult.isSuccess) {
        val lit = litResult.getOrThrow()
        val index = parseIndex(lit).getOrElse { return SynResult.failure(it) }
        input.advanceTo(ahead)
        return SynResult.success(Member.Unnamed(index))
    }
    return SynResult.failure(input.error("expected field name or index"))
}

public data class MultiIndexResult(
    val expr: Expr,
    val complete: Boolean,
)

internal fun multiIndexImpl(
    expr: Expr,
    dotToken: io.github.kotlinmania.syn.token.Dot,
    float: LitFloat,
): SynResult<MultiIndexResult> {
    val floatToken = float.token()
    val floatSpan = floatToken.span()
    var floatRepr = floatToken.toString()
    val trailingDot = floatRepr.endsWith('.')
    if (trailingDot) {
        floatRepr = floatRepr.dropLast(1)
    }

    var current = expr
    var nextDot = dotToken
    var offset = 0
    for (part in floatRepr.split('.')) {
        if (part.isEmpty() && offset == 0) {
            offset = 1
            continue
        }
        val partEnd = offset + part.length
        val partSpan = floatToken.subspan(offset untilIntRange partEnd) ?: floatSpan
        val index = parseIndexPart(part, partSpan).getOrElse { return SynResult.failure(it) }
        current = Expr.Field(mutableListOf(), current, nextDot, Member.Unnamed(index))
        nextDot =
            io.github.kotlinmania.syn.token.Dot
                .from(floatToken.subspan(partEnd..partEnd) ?: floatSpan)
        offset = partEnd + 1
    }
    return SynResult.success(MultiIndexResult(current, complete = !trailingDot))
}

private infix fun Int.untilIntRange(endExclusive: Int): IntRange =
    if (this < endExclusive) this..(endExclusive - 1) else this..this

private fun parseIndexPart(part: String, span: Span): SynResult<Index> {
    if (part.isEmpty() || part.any { it !in '0'..'9' && it != '_' }) {
        return SynResult.failure(SynError.new(span, "expected unsuffixed integer"))
    }
    val digits = part.replace("_", "")
    val idx =
        digits.toUIntOrNull()
            ?: return SynResult.failure(SynError.new(span, "expected unsuffixed integer"))
    return SynResult.success(Index(idx, span))
}

private fun parseIndex(lit: LitInt): SynResult<Index> {
    val digits = lit.base10Digits()
    val idx =
        digits.toUIntOrNull()
            ?: return SynResult.failure(SynError.new(lit.span(), "expected unsuffixed integer"))
    return SynResult.success(Index(idx, lit.span()))
}

internal fun atomExprImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    if (peekExprGroup(input, allowStruct)) {
        return parseExprGroupImpl(input)
    }
    if (input.peek(UnderscorePeek)) {
        val underscore = UnderscoreParse.parse(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(Expr.Infer(mutableListOf(), underscore))
    }
    if (input.peek(LitPeek)) {
        val lit = LitParse.parse(input)
        if (lit.isSuccess) return SynResult.success(Expr.Lit(mutableListOf(), lit.getOrThrow()))
        return lit.asFailure()
    }
    if (input.peek(IfPeek)) return parseExprIf(input)
    if (input.peek(WhilePeek)) return parseExprWhile(input)
    if (input.peek(ForPeek) && !chooseGenericsOverQpathAfterKeyword(input)) return parseExprFor(input)
    if (input.peek(LoopPeek)) return parseExprLoop(input)
    if (input.peek(MatchPeek)) return parseExprMatch(input)
    if (input.peek(AsyncPeek) && (input.peek2(BracePeek) || (input.peek2(MovePeek) && input.peek3(BracePeek)))) return parseExprAsync(input)
    if (input.peek(TryPeek) && input.peek2(BracePeek)) return parseExprTryBlock(input)
    if (input.peek(UnsafePeek) && input.peek2(BracePeek)) return parseExprUnsafe(input)
    if (input.peek(ConstPeek) && input.peek2(BracePeek)) return parseExprConst(input)
    if (input.peek(BracePeek)) return parseExprBlock(input)
    if (input.peek(ParenPeek)) return parenOrTupleImpl(input)
    if (input.peek(BracketPeek)) return arrayOrRepeatImpl(input)
    if (input.peek(LifetimePeek)) {
        val labeled = parseLabeledLoopOrWhile(input)
        if (labeled.isSuccess) return labeled
    }
    if (input.peek(ReturnPeek)) {
        val retToken = ReturnParse.parse(input)
        if (retToken.isFailure) return retToken.asFailure()
        val expr =
            if (peekExpr(input)) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Return(mutableListOf(), retToken.getOrThrow(), expr.getOrThrow()))
    }
    if (input.peek(YieldPeek)) {
        val yldToken = YieldParse.parse(input)
        if (yldToken.isFailure) return yldToken.asFailure()
        val expr =
            if (peekExpr(input)) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Yield(mutableListOf(), yldToken.getOrThrow(), expr.getOrThrow()))
    }
    if (input.peek(LetPeek)) return parseExprLetImpl(input, allowStruct)
    if (input.peek(BreakPeek)) {
        val brkToken = BreakParse.parse(input)
        if (brkToken.isFailure) return brkToken.asFailure()
        val labelResult = LifetimeParse.parse(input)
        val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
        val expr =
            if (peekExpr(input) && (allowStruct || !input.peek(BracePeek))) {
                parseExprFull(input)
            } else {
                SynResult.success(null)
            }
        if (expr.isFailure) return expr.asFailure()
        return SynResult.success(Expr.Break(mutableListOf(), brkToken.getOrThrow(), label, expr.getOrThrow()))
    }
    if (input.peek(ContinuePeek)) {
        val contToken = ContinueParse.parse(input)
        if (contToken.isFailure) return contToken.asFailure()
        val labelResult = LifetimeParse.parse(input)
        val label = if (labelResult.isSuccess) labelResult.getOrThrow() else null
        return SynResult.success(Expr.Continue(mutableListOf(), contToken.getOrThrow(), label))
    }
    if (input.peek(DotDotPeek) || input.peek(DotDotEqPeek)) {
        return parseExprRange(input, null, allowStruct)
    }
    if (input.peek(MovePeek) ||
        input.peek(StaticPeek) ||
        (input.peek(ForPeek) && chooseGenericsOverQpathAfterKeyword(input)) ||
        input.peek(OrPeek) ||
        input.peek(OrOrPeek) ||
        (input.peek(ConstPeek) && !input.peek2(BracePeek)) ||
        (input.peek(AsyncPeek) && (input.peek2(OrPeek) || input.peek2(OrOrPeek) || (input.peek2(MovePeek) && !input.peek3(BracePeek))))
    ) {
        return parseExprClosure(input, allowStruct)
    }
    if (input.peek(IdentPeek) ||
        input.peek(PathSepPeek) ||
        input.peek(LtPeek) ||
        input.peek(SelfValuePeek) ||
        input.peek(SelfTypePeek) ||
        input.peek(SuperPeek) ||
        input.peek(CratePeek)
    ) {
        return pathOrMacroOrStructImpl(input, allowStruct)
    }
    if (input.peek(NotPeek) && input.peek2(IdentPeek)) {
        return pathOrMacroOrStructImpl(input, allowStruct)
    }
    return SynResult.failure(input.error("expected an expression"))
}

internal fun parseExprLetImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr.Let> {
    val letToken = LetParse.parse(input)
    if (letToken.isFailure) return letToken.asFailure()
    val pat = Pat.parseMultiWithLeadingVert(input).getOrElse { return SynResult.failure(it) }
    val eqToken = EqParse.parse(input)
    if (eqToken.isFailure) return eqToken.asFailure()
    val lhs = unaryExprImpl(input, allowStruct)
    if (lhs.isFailure) return lhs.asFailure()
    val expr = parseExprBinaryImpl(input, lhs.getOrThrow(), allowStruct, Precedence.Compare)
    if (expr.isFailure) return expr.asFailure()
    return SynResult.success(Expr.Let(mutableListOf(), letToken.getOrThrow(), pat, eqToken.getOrThrow(), expr.getOrThrow()))
}

private fun peekExprGroup(input: ParseStream, allowStruct: Boolean): Boolean {
    val ahead = input.fork()
    if (parseGroup(ahead).isFailure) return false
    if (ahead.peek(NotPeek) || ahead.peek(PathSepPeek)) return false
    if (allowStruct && ahead.peek(BracePeek)) return false
    return true
}

internal fun parseExprGroupImpl(input: ParseStream): SynResult<Expr> {
    val group = parseGroup(input).getOrElse { return SynResult.failure(it) }
    val expr = parseExprFull(group.content).getOrElse { return SynResult.failure(it) }
    group.content.finishChildBuffer()
    return SynResult.success(Expr.Group(mutableListOf(), group.token, expr))
}

private fun parseLabeledLoopOrWhile(input: ParseStream): SynResult<Expr> {
    val ahead = input.fork()
    val lifetime = LifetimeParse.parse(ahead).getOrElse { return SynResult.failure(it) }
    val colon = ColonParse.parse(ahead).getOrElse { return SynResult.failure(it) }
    if (!ahead.peek(LoopPeek) && !ahead.peek(WhilePeek) && !ahead.peek(ForPeek) && !ahead.peek(BracePeek)) {
        return SynResult.failure(input.error("expected loop or block expression"))
    }
    input.advanceTo(ahead)
    val label = Label(lifetime, colon)
    return if (input.peek(LoopPeek)) {
        parseExprLoop(input, label)
    } else if (input.peek(WhilePeek)) {
        parseExprWhile(input, label)
    } else if (input.peek(ForPeek)) {
        parseExprFor(input, label)
    } else {
        val block = parseExprBlock(input)
        if (block.isFailure) return block
        val expr = block.getOrThrow()
        if (expr !is Expr.BlockExpr) return SynResult.failure(input.error("expected block expression"))
        SynResult.success(expr.copy(label = label))
    }
}

internal fun pathOrMacroOrStructImpl(input: ParseStream, allowStruct: Boolean): SynResult<Expr> {
    val (qself, path) = qpath(input, exprStyle = true).getOrElse { return SynResult.failure(it) }
    return restOfPathOrMacroOrStruct(qself, path, input, allowStruct)
}

internal fun parseFieldValueImpl(input: ParseStream): SynResult<FieldValue> {
    val attrs = Attribute.parseOuter(input).getOrElse { return SynResult.failure(it) }
    val member = parseMemberImpl(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(ColonPeek) || member !is Member.Named) {
        val colon = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val value = parseExprFull(input).getOrElse { return SynResult.failure(it) }
        return SynResult.success(FieldValue(attrs, member, colon, value))
    }
    return SynResult.success(
        FieldValue(attrs, member, null, Expr.Path(mutableListOf(), null, Path.from(member.ident.cloneToken()))),
    )
}

internal fun parenOrTupleImpl(input: ParseStream): SynResult<Expr> {
    val parens = parenthesized(input)
    if (parens.isFailure) return parens.asFailure()
    val parensVal = parens.getOrThrow()
    val paren = parensVal.token
    val content = parensVal.content
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Tuple(mutableListOf(), paren, ExprList()))
    }
    val first = parseExprFull(content)
    if (first.isFailure) return first
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Paren(mutableListOf(), paren, first.getOrThrow()))
    }
    val elems = ExprList()
    elems.pushValue(first.getOrThrow())
    while (!content.isEmpty()) {
        val commaResult = CommaParse.parse(content)
        if (commaResult.isFailure) break
        elems.pushPunct(commaResult.getOrThrow())
        if (content.isEmpty()) break
        val valResult = parseExprFull(content)
        if (valResult.isFailure) return valResult
        elems.pushValue(valResult.getOrThrow())
    }
    content.finishChildBuffer()
    return SynResult.success(Expr.Tuple(mutableListOf(), paren, elems))
}

internal fun arrayOrRepeatImpl(input: ParseStream): SynResult<Expr> {
    val brackets = bracketed(input)
    if (brackets.isFailure) return brackets.asFailure()
    val bracketsVal = brackets.getOrThrow()
    val bracket = bracketsVal.token
    val content = bracketsVal.content
    if (content.isEmpty()) {
        content.finishChildBuffer()
        return SynResult.success(Expr.Array(mutableListOf(), bracket, ExprList()))
    }
    val first = parseExprFull(content)
    if (first.isFailure) return first
    if (content.isEmpty() || content.peek(CommaPeek)) {
        val elems = ExprList()
        elems.pushValue(first.getOrThrow())
        while (!content.isEmpty()) {
            val commaResult = CommaParse.parse(content)
            if (commaResult.isFailure) break
            elems.pushPunct(commaResult.getOrThrow())
            if (content.isEmpty()) break
            val valResult = parseExprFull(content)
            if (valResult.isFailure) return valResult
            elems.pushValue(valResult.getOrThrow())
        }
        content.finishChildBuffer()
        return SynResult.success(Expr.Array(mutableListOf(), bracket, elems))
    }
    if (content.peek(SemiPeek)) {
        val semiResult = SemiParse.parse(content)
        if (semiResult.isFailure) return semiResult.asFailure()
        val lenResult = parseExprFull(content)
        if (lenResult.isFailure) return lenResult
        content.finishChildBuffer()
        return SynResult.success(Expr.Repeat(mutableListOf(), bracket, first.getOrThrow(), semiResult.getOrThrow(), lenResult.getOrThrow()))
    }
    content.finishChildBuffer()
    return SynResult.failure(content.error("expected `,` or `;`"))
}

private fun parseExprRange(input: ParseStream, start: Expr?, allowStruct: Boolean): SynResult<Expr> {
    val limitsResult = parseRangeLimits(input)
    if (limitsResult.isFailure) return limitsResult.asFailure()
    val limits = limitsResult.getOrThrow()
    val endResult = parseRangeEnd(input, limits, allowStruct)
    if (endResult.isFailure) return endResult.asFailure()
    return SynResult.success(Expr.Range(mutableListOf(), start, limits, endResult.getOrThrow()))
}

private fun parseRangeLimits(input: ParseStream): SynResult<RangeLimits> =
    when {
        input.peek(DotDotEqPeek) -> {
            val dotDotEqResult = DotDotEqParse.parse(input)
            if (dotDotEqResult.isFailure) dotDotEqResult.asFailure() else SynResult.success(RangeLimits.Closed(dotDotEqResult.getOrThrow()))
        }

        input.peek(DotDotPeek) -> {
            val dotDotResult = DotDotParse.parse(input)
            if (dotDotResult.isFailure) dotDotResult.asFailure() else SynResult.success(RangeLimits.HalfOpen(dotDotResult.getOrThrow()))
        }

        else -> {
            SynResult.failure(input.error("expected range limits"))
        }
    }

private fun parseExprIf(input: ParseStream): SynResult<Expr> {
    val ifToken = IfParse.parse(input).getOrThrow()
    val cond = ambiguousExprImpl(input, allowStruct = false)
    if (cond.isFailure) return cond
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmtsResult = parseWithin(content)
    if (stmtsResult.isFailure) return stmtsResult.asFailure()
    val stmts = stmtsResult.getOrThrow()
    content.finishChildBuffer()
    val thenBranch = Block(brace, stmts)
    var elseBranch: ElseExpr? = null
    if (input.peek(ElsePeek)) {
        val elseToken = ElseParse.parse(input).getOrThrow()
        if (input.peek(IfPeek)) {
            val inner = parseExprIf(input)
            if (inner.isFailure) return inner
            elseBranch = ElseExpr(elseToken, inner.getOrThrow())
        } else if (input.peek(BracePeek)) {
            val elseBrace = braced(input)
            if (elseBrace.isFailure) return elseBrace.asFailure()
            val eBracesVal = elseBrace.getOrThrow()
            val eBrace = eBracesVal.token
            val eContent = eBracesVal.content
            val eStmtsResult = parseWithin(eContent)
            if (eStmtsResult.isFailure) return eStmtsResult.asFailure()
            val eStmts = eStmtsResult.getOrThrow()
            eContent.finishChildBuffer()
            elseBranch = ElseExpr(elseToken, Expr.BlockExpr(mutableListOf(), null, Block(eBrace, eStmts)))
        }
    }
    return SynResult.success(Expr.If(mutableListOf(), ifToken, cond.getOrThrow(), thenBranch, elseBranch))
}

internal fun parseExprWhile(input: ParseStream, label: Label? = null): SynResult<Expr> {
    val whileToken = WhileParse.parse(input).getOrThrow()
    val cond = ambiguousExprImpl(input, allowStruct = false)
    if (cond.isFailure) return cond
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val attrs = Attribute.parseInner(content).getOrElse { return SynResult.failure(it) }
    val stmtsResult = parseWithin(content)
    if (stmtsResult.isFailure) return stmtsResult.asFailure()
    val stmts = stmtsResult.getOrThrow()
    content.finishChildBuffer()
    return SynResult.success(Expr.While(attrs, label, whileToken, cond.getOrThrow(), Block(brace, stmts)))
}

internal fun parseExprFor(input: ParseStream, label: Label? = null): SynResult<Expr> {
    val forToken = ForParse.parse(input).getOrElse { return SynResult.failure(it) }
    val pat = Pat.parseMultiWithLeadingVert(input).getOrElse { return SynResult.failure(it) }
    val inToken = InParse.parse(input).getOrElse { return SynResult.failure(it) }
    val expr = ambiguousExprImpl(input, allowStruct = false).getOrElse { return SynResult.failure(it) }
    val braces = braced(input).getOrElse { return SynResult.failure(it) }
    val attrs = Attribute.parseInner(braces.content).getOrElse { return SynResult.failure(it) }
    val stmts = parseWithin(braces.content).getOrElse { return SynResult.failure(it) }
    braces.content.finishChildBuffer()
    return SynResult.success(Expr.ForLoop(attrs, label, forToken, pat, inToken, expr, Block(braces.token, stmts)))
}

internal fun parseExprLoop(input: ParseStream, label: Label? = null): SynResult<Expr> {
    val loopToken = LoopParse.parse(input).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val attrs = Attribute.parseInner(content).getOrElse { return SynResult.failure(it) }
    val stmtsResult = parseWithin(content)
    if (stmtsResult.isFailure) return stmtsResult.asFailure()
    val stmts = stmtsResult.getOrThrow()
    content.finishChildBuffer()
    return SynResult.success(Expr.Loop(attrs, label, loopToken, Block(brace, stmts)))
}

private fun parseExprMatch(input: ParseStream): SynResult<Expr> {
    val matchToken = MatchParse.parse(input).getOrThrow()
    val scrutinee = ambiguousExprImpl(input, allowStruct = false)
    if (scrutinee.isFailure) return scrutinee
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val attrs = Attribute.parseInner(content).getOrElse { return SynResult.failure(it) }
    val arms = parseMultipleArms(content).getOrElse { return SynResult.failure(it) }
    content.finishChildBuffer()
    return SynResult.success(Expr.Match(attrs, matchToken, scrutinee.getOrThrow(), brace, arms))
}

private fun parseExprAsync(input: ParseStream): SynResult<Expr> {
    val asyncToken = AsyncParse.parse(input).getOrThrow()
    val moveResult = MoveParse.parse(input)
    val capture = if (moveResult.isSuccess) moveResult.getOrThrow() else null
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val stmtsResult = parseWithin(content)
    if (stmtsResult.isFailure) return stmtsResult.asFailure()
    val stmts = stmtsResult.getOrThrow()
    content.finishChildBuffer()
    return SynResult.success(Expr.Async(mutableListOf(), asyncToken, capture, Block(brace, stmts)))
}

// expr.rs:2510: ExprTryBlock uses the existing Block parser (no inner attrs).
private fun parseExprTryBlock(input: ParseStream): SynResult<Expr> {
    val tryToken = TryParse.parse(input).getOrElse { return SynResult.failure(it) }
    val block = parseBlock(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Expr.TryBlock(emptyList(), tryToken, block))
}

private fun parseExprUnsafe(input: ParseStream): SynResult<Expr> {
    val unsafeToken = UnsafeParse.parse(input).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val attrs = Attribute.parseInner(content).getOrElse { return SynResult.failure(it) }
    val stmtsResult = parseWithin(content)
    if (stmtsResult.isFailure) return stmtsResult.asFailure()
    val stmts = stmtsResult.getOrThrow()
    content.finishChildBuffer()
    return SynResult.success(Expr.Unsafe(attrs, unsafeToken, Block(brace, stmts)))
}

private fun parseExprConst(input: ParseStream): SynResult<Expr> {
    val constToken = ConstParse.parse(input).getOrThrow()
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val attrs = Attribute.parseInner(content).getOrElse { return SynResult.failure(it) }
    val stmtsResult = parseWithin(content)
    if (stmtsResult.isFailure) return stmtsResult.asFailure()
    val stmts = stmtsResult.getOrThrow()
    content.finishChildBuffer()
    return SynResult.success(Expr.Const(attrs, constToken, Block(brace, stmts)))
}

internal fun parseExprBlock(input: ParseStream): SynResult<Expr> {
    val braceResult = braced(input)
    if (braceResult.isFailure) return braceResult.asFailure()
    val bracesVal = braceResult.getOrThrow()
    val brace = bracesVal.token
    val content = bracesVal.content
    val attrs = Attribute.parseInner(content).getOrElse { return SynResult.failure(it) }
    val stmtsResult = parseWithin(content)
    if (stmtsResult.isFailure) return stmtsResult.asFailure()
    val stmts = stmtsResult.getOrThrow()
    content.finishChildBuffer()
    return SynResult.success(Expr.BlockExpr(attrs, null, Block(brace, stmts)))
}

private fun parseExprClosure(input: ParseStream, allowStruct: Boolean): SynResult<Expr> =
    exprClosure(input, allowStruct)

internal fun parseStmtFull(input: ParseStream): SynResult<Stmt> {
    // Parse outer attributes that may precede any statement kind.
    // If the statement turns out to be an item, the item parser does its own
    // attribute parsing, so we reset to the fork point for the item path.
    val begin = input.fork()
    val stmtAttrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(LetPeek) && !startsWithNoneGroup(input)) {
        val letToken = LetParse.parse(input).getOrThrow()
        var pat = parsePatFull(input).getOrElse { return SynResult.failure(it) }
        if (input.peek(ColonPeek)) {
            val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
            val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
            pat = Pat.TypeAscription(mutableListOf(), pat, colonToken, ty)
        }
        val init: LocalInit? =
            if (input.peek(EqPeek)) {
                val eq = EqParse.parse(input).getOrThrow()
                val e = parseExprFull(input)
                if (e.isFailure) {
                    return e.asFailure()
                } else {
                    val diverge =
                        if (!exprTrailingBrace(e.getOrThrow()) && input.peek(ElsePeek)) {
                            val elseToken = ElseParse.parse(input).getOrThrow()
                            val block = parseExprBlock(input)
                            if (block.isFailure) return block.asFailure()
                            ElseExpr(elseToken, block.getOrThrow())
                        } else {
                            null
                        }
                    LocalInit(eq, e.getOrThrow(), diverge)
                }
            } else {
                null
            }
        val semi = SemiParse.parse(input).getOrThrow()
        return SynResult.success(Stmt.Local(stmtAttrs, letToken, pat, init, semi))
    }
    // If we consumed outer attrs but the statement is an item or expression,
    // reset to before the attrs so the item/expr parsers see the full input.
    if (stmtAttrs.isNotEmpty()) {
        input.advanceTo(begin)
    }
    if (peekItemStatement(input)) {
        val item = ItemParse.parse(input)
        if (item.isFailure) return item.asFailure()
        return SynResult.success(Stmt.ItemStmt(item.getOrThrow()))
    }
    val exprResult = parseExprWithEarlierBoundaryRule(input)
    if (exprResult.isFailure) return exprResult.asFailure()
    if (input.peek(SemiPeek)) {
        val semi = SemiParse.parse(input).getOrThrow()
        val expr = exprResult.getOrThrow()
        if (expr is Expr.Macro) {
            return SynResult.success(Stmt.MacroStmt(expr.attrs, expr.mac, semi))
        }
        return SynResult.success(Stmt.ExprStmt(exprResult.getOrThrow(), semi))
    }
    val expr = exprResult.getOrThrow()
    if (expr is Expr.Macro && expr.mac.isBrace()) {
        return SynResult.success(Stmt.MacroStmt(expr.attrs, expr.mac, null))
    }
    return SynResult.success(Stmt.ExprStmt(expr, null))
}

private fun startsWithNoneGroup(input: ParseStream): Boolean =
    parseGroup(input.fork()).isSuccess

private fun peekItemStatement(input: ParseStream): Boolean {
    val ahead = input.fork()
    if (Attribute.parseOuter(ahead).isFailure) return false
    return ahead.peek(PubPeek) ||
        (ahead.peek(CratePeek) && !ahead.peek2(PathSepPeek)) ||
        ahead.peek(ExternPeek) ||
        ahead.peek(UsePeek) ||
        (
            ahead.peek(StaticPeek) &&
                (
                    ahead.peek2(MutPeek) ||
                        (
                            ahead.peek2(IdentPeek) &&
                                !(ahead.peek2(AsyncPeek) && (ahead.peek3(MovePeek) || ahead.peek3(OrPeek)))
                        )
                )
        ) ||
        (
            ahead.peek(ConstPeek) &&
                !(
                    ahead.peek2(BracePeek) ||
                        ahead.peek2(StaticPeek) ||
                        (
                            ahead.peek2(AsyncPeek) &&
                                !(ahead.peek3(UnsafePeek) || ahead.peek3(ExternPeek) || ahead.peek3(FnPeek))
                        ) ||
                        ahead.peek2(MovePeek) ||
                        ahead.peek2(OrPeek)
                )
        ) ||
        (ahead.peek(UnsafePeek) && !ahead.peek2(BracePeek)) ||
        (ahead.peek(AsyncPeek) && (ahead.peek2(UnsafePeek) || ahead.peek2(ExternPeek) || ahead.peek2(FnPeek))) ||
        ahead.peek(FnPeek) ||
        ahead.peek(ModPeek) ||
        ahead.peek(SynTypePeek) ||
        ahead.peek(StructPeek) ||
        ahead.peek(EnumPeek) ||
        (ahead.peek(UnionPeek) && ahead.peek2(IdentPeek)) ||
        (ahead.peek(AutoPeek) && ahead.peek2(TraitPeek)) ||
        ahead.peek(TraitPeek) ||
        (ahead.peek(DefaultPeek) && (ahead.peek2(UnsafePeek) || ahead.peek2(ImplPeek))) ||
        ahead.peek(ImplPeek) ||
        ahead.peek(MacroPeek) ||
        peekItemMacro(ahead)
}

private fun peekItemMacro(input: ParseStream): Boolean {
    val ahead = input.fork()
    if (parseModStylePath(ahead).isFailure) return false
    if (!ahead.peek(NotPeek)) return false
    return ahead.peek2(IdentPeek) || ahead.peek2(TryPeek)
}
