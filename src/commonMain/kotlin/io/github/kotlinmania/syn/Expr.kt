// port-lint: source expr.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.quote.append
import io.github.kotlinmania.quote.toTokens

/** An expression syntax tree node. */
public sealed class Expr : ToTokens {
    /** A slice literal expression: `[a, b, c, d]`. */
    public data class Array(
        public val attrs: List<Attribute>,
        public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public val elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            bracketToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }

        override fun deepCopy(): Array = Array(attrs.map { it.deepCopy() }, bracketToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** An assignment expression: `a = compute()`. */
    public data class Assign(
        public val attrs: List<Attribute>,
        public val left: Expr,
        public val eqToken: io.github.kotlinmania.syn.token.Eq,
        public val right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            left.toTokens(tokens)
            eqToken.toTokens(tokens)
            right.toTokens(tokens)
        }

        override fun deepCopy(): Assign = Assign(attrs.map { it.deepCopy() }, left.deepCopy(), eqToken, right.deepCopy())
    }

    /** An async block: `async { ... }`. */
    public data class Async(
        public val attrs: List<Attribute>,
        public val asyncToken: io.github.kotlinmania.syn.token.Async,
        public val capture: io.github.kotlinmania.syn.token.Move?,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            asyncToken.toTokens(tokens)
            capture?.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Async = Async(attrs.map { it.deepCopy() }, asyncToken, capture, block)
    }

    /** An await expression: `fut.await`. */
    public data class Await(
        public val attrs: List<Attribute>,
        public val base: Expr,
        public val dotToken: io.github.kotlinmania.syn.token.Dot,
        public val awaitToken: io.github.kotlinmania.syn.token.Await,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            base.toTokens(tokens)
            dotToken.toTokens(tokens)
            awaitToken.toTokens(tokens)
        }

        override fun deepCopy(): Await = Await(attrs.map { it.deepCopy() }, base.deepCopy(), dotToken, awaitToken)
    }

    /** A binary operation: `a + b`, `a += b`. */
    public data class Binary(
        public val attrs: List<Attribute>,
        public val left: Expr,
        public val op: BinOp,
        public val right: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            left.toTokens(tokens)
            op.toTokens(tokens)
            right.toTokens(tokens)
        }

        override fun deepCopy(): Binary = Binary(attrs.map { it.deepCopy() }, left.deepCopy(), op, right.deepCopy())
    }

    /** A blocked scope: `{ ... }`. */
    public data class BlockExpr(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): BlockExpr = BlockExpr(attrs.map { it.deepCopy() }, label?.deepCopy(), block)
    }

    /** A `break`, with an optional label to break and an optional expression. */
    public data class Break(
        public val attrs: List<Attribute>,
        public val breakToken: io.github.kotlinmania.syn.token.Break,
        public val label: Lifetime?,
        public val expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            breakToken.toTokens(tokens)
            label?.toTokens(tokens)
            expr?.toTokens(tokens)
        }

        override fun deepCopy(): Break = Break(attrs.map { it.deepCopy() }, breakToken, label?.deepCopy(), expr?.deepCopy())
    }

    /** A function call expression: `invoke(a, b)`. */
    public data class Call(
        public val attrs: List<Attribute>,
        public val func: Expr,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val args: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            func.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                args.toTokens(inner)
            }
        }

        override fun deepCopy(): Call = Call(attrs.map { it.deepCopy() }, func.deepCopy(), parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A cast expression: `foo as f64`. */
    public data class Cast(
        public val attrs: List<Attribute>,
        public val expr: Expr,
        public val asToken: io.github.kotlinmania.syn.token.As,
        public val ty: SynType,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            expr.toTokens(tokens)
            asToken.toTokens(tokens)
            ty.toTokens(tokens)
        }

        override fun deepCopy(): Cast = Cast(attrs.map { it.deepCopy() }, expr.deepCopy(), asToken, ty.deepCopy())
    }

    /** A closure expression: `|a, b| a + b`. */
    public data class Closure(
        public val attrs: List<Attribute>,
        public val constness: io.github.kotlinmania.syn.token.Const?,
        public val asyncness: io.github.kotlinmania.syn.token.Async?,
        public val capture: io.github.kotlinmania.syn.token.Move?,
        public val or1Token: io.github.kotlinmania.syn.token.Or,
        public val inputs: PatList,
        public val or2Token: io.github.kotlinmania.syn.token.Or,
        public val output: ReturnType,
        public val body: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constness?.toTokens(tokens)
            asyncness?.toTokens(tokens)
            capture?.toTokens(tokens)
            or1Token.toTokens(tokens)
            inputs.toTokens(tokens)
            or2Token.toTokens(tokens)
            output.toTokens(tokens)
            body.toTokens(tokens)
        }

        override fun deepCopy(): Closure = Closure(attrs.map { it.deepCopy() }, constness, asyncness, capture, or1Token, inputs.copy({ it.deepCopy() }, { it }), or2Token, output.deepCopy(), body.deepCopy())
    }

    /** A const block: `const { ... }`. */
    public data class Const(
        public val attrs: List<Attribute>,
        public val constToken: io.github.kotlinmania.syn.token.Const,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            constToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Const = Const(attrs.map { it.deepCopy() }, constToken, block)
    }

    /** A `continue`, with an optional label. */
    public data class Continue(
        public val attrs: List<Attribute>,
        public val continueToken: io.github.kotlinmania.syn.token.Continue,
        public val label: Lifetime?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            continueToken.toTokens(tokens)
            label?.toTokens(tokens)
        }

        override fun deepCopy(): Continue = Continue(attrs.map { it.deepCopy() }, continueToken, label?.deepCopy())
    }

    /** Access of a named field of a data class (`obj.k`) or indexed element of a tuple-like compound (`obj.0`). */
    public data class Field(
        public val attrs: List<Attribute>,
        public val base: Expr,
        public val dotToken: io.github.kotlinmania.syn.token.Dot,
        public val member: Member,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            base.toTokens(tokens)
            dotToken.toTokens(tokens)
            member.toTokens(tokens)
        }

        override fun deepCopy(): Field = Field(attrs.map { it.deepCopy() }, base.deepCopy(), dotToken, member)
    }

    /** A for loop: `for pat in expr { ... }`. */
    public data class ForLoop(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val forToken: io.github.kotlinmania.syn.token.For,
        public val pat: Pat,
        public val inToken: io.github.kotlinmania.syn.token.In,
        public val expr: Expr,
        public val body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            forToken.toTokens(tokens)
            pat.toTokens(tokens)
            inToken.toTokens(tokens)
            expr.toTokens(tokens)
            body.toTokens(tokens)
        }

        override fun deepCopy(): ForLoop = ForLoop(attrs.map { it.deepCopy() }, label?.deepCopy(), forToken, pat.deepCopy(), inToken, expr.deepCopy(), body)
    }

    /** An expression contained within invisible delimiters. */
    public data class Group(
        public val attrs: List<Attribute>,
        public val groupToken: io.github.kotlinmania.syn.token.Group,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            groupToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Group = Group(attrs.map { it.deepCopy() }, groupToken, expr.deepCopy())
    }

    /** An `if` expression with an optional `else` block. */
    public data class If(
        public val attrs: List<Attribute>,
        public val ifToken: io.github.kotlinmania.syn.token.If,
        public val cond: Expr,
        public val thenBranch: Block,
        public val elseBranch: ElseExpr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            ifToken.toTokens(tokens)
            cond.toTokens(tokens)
            thenBranch.toTokens(tokens)
            elseBranch?.toTokens(tokens)
        }

        override fun deepCopy(): If = If(attrs.map { it.deepCopy() }, ifToken, cond.deepCopy(), thenBranch, elseBranch?.let { it.copy(expr = it.expr.deepCopy()) })
    }

    /** A square bracketed indexing expression: `vector[2]`. */
    public data class Index(
        public val attrs: List<Attribute>,
        public val expr: Expr,
        public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public val index: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            expr.toTokens(tokens)
            bracketToken.surround(tokens) { inner -> index.toTokens(inner) }
        }

        override fun deepCopy(): Index = Index(attrs.map { it.deepCopy() }, expr.deepCopy(), bracketToken, index.deepCopy())
    }

    /** The inferred value of a const generic argument, denoted `_`. */
    public data class Infer(
        public val attrs: List<Attribute>,
        public val underscoreToken: io.github.kotlinmania.syn.token.Underscore,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            underscoreToken.toTokens(tokens)
        }

        override fun deepCopy(): Infer = Infer(attrs.map { it.deepCopy() }, underscoreToken)
    }

    /** A `let` guard: `let Some(x) = opt`. */
    public data class Let(
        public val attrs: List<Attribute>,
        public val letToken: io.github.kotlinmania.syn.token.Let,
        public val pat: Pat,
        public val eqToken: io.github.kotlinmania.syn.token.Eq,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            letToken.toTokens(tokens)
            pat.toTokens(tokens)
            eqToken.toTokens(tokens)
            expr.toTokens(tokens)
        }

        override fun deepCopy(): Let = Let(attrs.map { it.deepCopy() }, letToken, pat.deepCopy(), eqToken, expr.deepCopy())
    }

    /** A literal in place of an expression: `1`, `"foo"`. */
    public data class Lit(
        val attrs: List<Attribute>,
        val lit: io.github.kotlinmania.syn.Lit,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            lit.toTokens(tokens)
        }

        override fun deepCopy(): Lit = Lit(attrs.map { it.deepCopy() }, lit)
    }

    /** Conditionless loop: `loop { ... }`. */
    public data class Loop(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val loopToken: io.github.kotlinmania.syn.token.Loop,
        public val body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            loopToken.toTokens(tokens)
            body.toTokens(tokens)
        }

        override fun deepCopy(): Loop = Loop(attrs.map { it.deepCopy() }, label?.deepCopy(), loopToken, body)
    }

    /** A macro invocation expression: `format!("{}", q)`. */
    public data class Macro(
        val attrs: List<Attribute>,
        val mac: io.github.kotlinmania.syn.Macro,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
        }

        override fun deepCopy(): Macro = Macro(attrs.map { it.deepCopy() }, mac.deepCopy())
    }

    /** A `match` expression. */
    public data class Match(
        public val attrs: List<Attribute>,
        public val matchToken: io.github.kotlinmania.syn.token.Match,
        public val expr: Expr,
        public val braceToken: io.github.kotlinmania.syn.token.Brace,
        public val arms: List<Arm>,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            matchToken.toTokens(tokens)
            expr.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                for (arm in arms) arm.toTokens(inner)
            }
        }

        override fun deepCopy(): Match = Match(attrs.map { it.deepCopy() }, matchToken, expr.deepCopy(), braceToken, arms.map { it.deepCopy() })
    }

    /** A method call expression: `x.foo::<T>(a, b)`. */
    public data class MethodCall(
        public val attrs: List<Attribute>,
        public val receiver: Expr,
        public val dotToken: io.github.kotlinmania.syn.token.Dot,
        public val method: Ident,
        public val turbofish: PathArguments.AngleBracketed?,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val args: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            receiver.toTokens(tokens)
            dotToken.toTokens(tokens)
            method.toTokens(tokens)
            turbofish?.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                args.toTokens(inner)
            }
        }

        override fun deepCopy(): MethodCall = MethodCall(attrs.map { it.deepCopy() }, receiver.deepCopy(), dotToken, method.copy(), turbofish?.deepCopy() as? PathArguments.AngleBracketed?, parenToken, args.copy({ it.deepCopy() }, { it }))
    }

    /** A parenthesized expression: `(a + b)`. */
    public data class Paren(
        public val attrs: List<Attribute>,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner -> expr.toTokens(inner) }
        }

        override fun deepCopy(): Paren = Paren(attrs.map { it.deepCopy() }, parenToken, expr.deepCopy())
    }

    /** A path like `core::mem::replace` possibly containing generic parameters. */
    public data class Path(
        val attrs: List<Attribute>,
        val qself: QSelf?,
        val path: io.github.kotlinmania.syn.Path,
    ) : Expr() {
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

        override fun deepCopy(): Path = Path(attrs.map { it.deepCopy() }, qself, path.deepCopy())
    }

    /** A range expression: `1..2`, `1..`, `..2`, `1..=2`, `..=2`. */
    public data class Range(
        public val attrs: List<Attribute>,
        public val start: Expr?,
        public val limits: RangeLimits,
        public val end: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            start?.toTokens(tokens)
            limits.toTokens(tokens)
            end?.toTokens(tokens)
        }

        override fun deepCopy(): Range = Range(attrs.map { it.deepCopy() }, start?.deepCopy(), limits, end?.deepCopy())
    }

    /** A referencing operation. */
    public data class Reference(
        public val attrs: List<Attribute>,
        public val andToken: io.github.kotlinmania.syn.token.And,
        public val mutability: io.github.kotlinmania.syn.token.Mut?,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            andToken.toTokens(tokens)
            mutability?.toTokens(tokens)
            expr.toTokens(tokens)
        }

        override fun deepCopy(): Reference = Reference(attrs.map { it.deepCopy() }, andToken, mutability, expr.deepCopy())
    }

    /** An array literal constructed from one repeated element: `[0u8; N]`. */
    public data class Repeat(
        public val attrs: List<Attribute>,
        public val bracketToken: io.github.kotlinmania.syn.token.Bracket,
        public val expr: Expr,
        public val semiToken: io.github.kotlinmania.syn.token.Semi,
        public val len: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            bracketToken.surround(tokens) { inner ->
                expr.toTokens(inner)
                semiToken.toTokens(inner)
                len.toTokens(inner)
            }
        }

        override fun deepCopy(): Repeat = Repeat(attrs.map { it.deepCopy() }, bracketToken, expr.deepCopy(), semiToken, len.deepCopy())
    }

    /** A `return`, with an optional value to be returned. */
    public data class Return(
        public val attrs: List<Attribute>,
        public val returnToken: io.github.kotlinmania.syn.token.Return,
        public val expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            returnToken.toTokens(tokens)
            expr?.toTokens(tokens)
        }

        override fun deepCopy(): Return = Return(attrs.map { it.deepCopy() }, returnToken, expr?.deepCopy())
    }

    /** A data-object initialization expression. */
    public data class Struct(
        public val attrs: List<Attribute>,
        public val qself: QSelf?,
        public val path: io.github.kotlinmania.syn.Path,
        public val braceToken: io.github.kotlinmania.syn.token.Brace,
        public val fields: FieldValueList,
        public val dot2Token: io.github.kotlinmania.syn.token.DotDot?,
        public val rest: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            qself?.let {
                it.ltToken.toTokens(tokens)
                it.ty.toTokens(tokens)
                it.asToken?.toTokens(tokens)
                it.gtToken.toTokens(tokens)
            }
            path.toTokens(tokens)
            braceToken.surround(tokens) { inner ->
                fields.toTokens(inner)
                dot2Token?.toTokens(inner)
                rest?.toTokens(inner)
            }
        }

        override fun deepCopy(): Struct = Struct(attrs.map { it.deepCopy() }, qself, path.deepCopy(), braceToken, fields.copy({ it.deepCopy() }, { it }), dot2Token, rest?.deepCopy())
    }

    /** A try-expression: `expr?`. */
    public data class Try(
        public val attrs: List<Attribute>,
        public val expr: Expr,
        public val questionToken: io.github.kotlinmania.syn.token.Question,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            expr.toTokens(tokens)
            questionToken.toTokens(tokens)
        }

        override fun deepCopy(): Try = Try(attrs.map { it.deepCopy() }, expr.deepCopy(), questionToken)
    }

    /** A try block: `try { ... }`. */
    public data class TryBlock(
        public val attrs: List<Attribute>,
        public val tryToken: io.github.kotlinmania.syn.token.Try,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            tryToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): TryBlock = TryBlock(attrs.map { it.deepCopy() }, tryToken, block)
    }

    /** A tuple expression: `(a, b, c, d)`. */
    public data class Tuple(
        public val attrs: List<Attribute>,
        public val parenToken: io.github.kotlinmania.syn.token.Paren,
        public val elems: ExprList,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            parenToken.surround(tokens) { inner ->
                elems.toTokens(inner)
            }
        }

        override fun deepCopy(): Tuple = Tuple(attrs.map { it.deepCopy() }, parenToken, elems.copy({ it.deepCopy() }, { it }))
    }

    /** A unary operation: `!x`, `*x`. */
    public data class Unary(
        public val attrs: List<Attribute>,
        public val op: UnOp,
        public val expr: Expr,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            op.toTokens(tokens)
            expr.toTokens(tokens)
        }

        override fun deepCopy(): Unary = Unary(attrs.map { it.deepCopy() }, op, expr.deepCopy())
    }

    /** An unsafe block expression. */
    public data class Unsafe(
        public val attrs: List<Attribute>,
        public val unsafeToken: io.github.kotlinmania.syn.token.Unsafe,
        public val block: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            unsafeToken.toTokens(tokens)
            block.toTokens(tokens)
        }

        override fun deepCopy(): Unsafe = Unsafe(attrs.map { it.deepCopy() }, unsafeToken, block)
    }

    /** A while loop: `while expr { ... }`. */
    public data class While(
        public val attrs: List<Attribute>,
        public val label: Label?,
        public val whileToken: io.github.kotlinmania.syn.token.While,
        public val cond: Expr,
        public val body: Block,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            label?.toTokens(tokens)
            whileToken.toTokens(tokens)
            cond.toTokens(tokens)
            body.toTokens(tokens)
        }

        override fun deepCopy(): While = While(attrs.map { it.deepCopy() }, label?.deepCopy(), whileToken, cond.deepCopy(), body)
    }

    /** A yield expression: `yield expr`. */
    public data class Yield(
        public val attrs: List<Attribute>,
        public val yieldToken: io.github.kotlinmania.syn.token.Yield,
        public val expr: Expr?,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            yieldToken.toTokens(tokens)
            expr?.toTokens(tokens)
        }

        override fun deepCopy(): Yield = Yield(attrs.map { it.deepCopy() }, yieldToken, expr?.deepCopy())
    }

    /** Tokens in expression position not interpreted by Syn. */
    public data class Verbatim(
        val tokens: TokenStream,
    ) : Expr() {
        override fun toTokens(tokens: TokenStream) {
            tokens.extendTokenStreams(listOf(tokens))
        }

        override fun deepCopy(): Verbatim = this
    }

    public abstract fun deepCopy(): Expr
}

/** A member of a data structure or tuple. */
public sealed class Member : ToTokens {
    public data class Named(
        val ident: Ident,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            ident.toTokens(tokens)
        }
    }

    public data class Unnamed(
        val index: Index,
    ) : Member() {
        override fun toTokens(tokens: TokenStream) {
            index.toTokens(tokens)
        }
    }
}

/** A tuple field index such as `0` in `obj.0`. */
public data class Index(
    public val index: UInt,
    public val span: Span,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        tokens.append(
            io.github.kotlinmania.procmacro2.Literal
                .i32Suffixed(index.toInt()),
        )
    }
}

/** A field-value pair in a data-object initialization. */
public data class FieldValue(
    public val attrs: List<Attribute>,
    public val member: Member,
    public val colonToken: io.github.kotlinmania.syn.token.Colon?,
    public val expr: Expr,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        member.toTokens(tokens)
        colonToken?.toTokens(tokens)
        expr.toTokens(tokens)
    }

    public fun deepCopy(): FieldValue = FieldValue(attrs.map { it.deepCopy() }, member, colonToken, expr.deepCopy())
}

/** A label on a `for`, `while`, or `loop`. */
public data class Label(
    public val name: Lifetime,
    public val colonToken: io.github.kotlinmania.syn.token.Colon,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        name.toTokens(tokens)
        colonToken.toTokens(tokens)
    }

    public fun deepCopy(): Label = Label(name.deepCopy(), colonToken)
}

/** One arm of a `match` expression. */
public data class Arm(
    public val attrs: List<Attribute>,
    public val pat: Pat,
    public val guard: IfExpr?,
    public val fatArrowToken: io.github.kotlinmania.syn.token.FatArrow,
    public val body: Expr,
    public val comma: io.github.kotlinmania.syn.token.Comma?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        for (attr in attrs) attr.toTokens(tokens)
        pat.toTokens(tokens)
        guard?.toTokens(tokens)
        fatArrowToken.toTokens(tokens)
        body.toTokens(tokens)
        comma?.toTokens(tokens)
    }

    public fun deepCopy(): Arm = Arm(attrs.map { it.deepCopy() }, pat.deepCopy(), guard?.let { it.copy(expr = it.expr.deepCopy()) }, fatArrowToken, body.deepCopy(), comma)
}

/** Limit types of a range, inclusive or exclusive. */
public sealed class RangeLimits : ToTokens {
    public data class HalfOpen(
        val token: io.github.kotlinmania.syn.token.DotDot,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    public data class Closed(
        val token: io.github.kotlinmania.syn.token.DotDotEq,
    ) : RangeLimits() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}

/** Mutability of a raw pointer. */
public sealed class PointerMutability : ToTokens {
    public data class Const(
        val token: io.github.kotlinmania.syn.token.Const,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }

    public data class Mut(
        val token: io.github.kotlinmania.syn.token.Mut,
    ) : PointerMutability() {
        override fun toTokens(tokens: TokenStream) {
            token.toTokens(tokens)
        }
    }
}
