// port-lint: source stmt.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.syn.gen.clone
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Let
import io.github.kotlinmania.syn.token.Semi

/**
 * A braced block containing statements.
 */
public data class Block(
    public var braceToken: Brace,
    public var stmts: List<Stmt>,
) : ToTokens {
    public companion object {
        public fun parse(input: ParseStream): SynResult<Block> = parseBlock(input)

        public fun parseWithin(input: ParseStream): SynResult<List<Stmt>> =
            io.github.kotlinmania.syn
                .parseWithin(input)
    }

    override fun toTokens(tokens: TokenStream) {
        braceToken.surround(tokens) { inner ->
            for (stmt in stmts) stmt.toTokens(inner)
        }
    }

    public fun deepCopy(): Block = Block(braceToken, stmts.mapTo(mutableListOf()) { it.deepCopy() })
}

/**
 * A statement, usually ending in a semicolon.
 */
public sealed class Stmt : ToTokens {
    public abstract fun deepCopy(): Stmt

    /** A local binding. */
    public data class Local(
        public var attrs: List<Attribute>,
        public var letToken: Let,
        public var modifiers: LocalModifiers,
        public var pat: Pat,
        public var init: LocalInit?,
        public var semiToken: Semi,
    ) : Stmt() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            letToken.toTokens(tokens)
            pat.toTokens(tokens)
            init?.toTokens(tokens)
            semiToken.toTokens(tokens)
        }

        override fun deepCopy(): Local =
            Local(
                attrs.mapTo(mutableListOf()) { it.deepCopy() },
                letToken,
                modifiers.deepCopy(),
                pat.deepCopy(),
                init?.deepCopy(),
                semiToken,
            )
    }

    /** An item definition. */
    public data class ItemStmt(
        public var item: Item,
    ) : Stmt() {
        override fun toTokens(tokens: TokenStream) {
            item.toTokens(tokens)
        }

        override fun deepCopy(): ItemStmt = ItemStmt(item.clone())
    }

    /** Expression, with or without trailing semicolon. */
    public data class ExprStmt(
        public var expr: Expr,
        public var semiToken: Semi?,
    ) : Stmt() {
        override fun toTokens(tokens: TokenStream) {
            expr.toTokensAsStmt(tokens)
            semiToken?.toTokens(tokens)
        }

        override fun deepCopy(): ExprStmt = ExprStmt(expr.deepCopy(), semiToken)
    }

    /** A macro invocation in statement position. */
    public data class MacroStmt(
        public var attrs: List<Attribute>,
        public var mac: Macro,
        public var semiToken: Semi?,
    ) : Stmt() {
        override fun toTokens(tokens: TokenStream) {
            for (attr in attrs) attr.toTokens(tokens)
            mac.toTokens(tokens)
            semiToken?.toTokens(tokens)
        }

        override fun deepCopy(): MacroStmt = MacroStmt(attrs.mapTo(mutableListOf()) { it.deepCopy() }, mac.deepCopy(), semiToken)
    }
}

/**
 * The expression assigned in a local `let` binding, including optional
 * diverging `else` block.
 *
 * `LocalInit` represents `= s.parse()` in `val x: ULong = s.parse()` and
 * `= r ?: return` in a local binding with a diverging `else` block.
 */
public data class LocalInit(
    public var eqToken: io.github.kotlinmania.syn.token.Eq,
    public var expr: Expr,
    public var diverge: ElseExpr?,
) : ToTokens {
    override fun toTokens(tokens: TokenStream) {
        eqToken.toTokens(tokens)
        expr.toTokens(tokens)
        diverge?.toTokens(tokens)
    }

    public fun deepCopy(): LocalInit = LocalInit(eqToken, expr.deepCopy(), diverge?.let { it.copy(expr = it.expr.deepCopy()) })
}

/** Additional optional information about a `let`-binding. */
public class LocalModifiers {
    public companion object {
        public fun default(): LocalModifiers = LocalModifiers()
    }

    public fun requireEmpty(): SynResult<Unit> = SynResult.success(Unit)

    public fun deepCopy(): LocalModifiers = LocalModifiers()

    public fun clone(): LocalModifiers = LocalModifiers()

    override fun equals(other: Any?): Boolean = other is LocalModifiers

    override fun hashCode(): Int = 0

    override fun toString(): String = "LocalModifiers"
}

public object StmtParse {
    fun parse(input: ParseStream): SynResult<Stmt> =
        parseStmt(input)
}

private data class AllowNoSemi(
    var value: Boolean,
)

public fun parseStmt(input: ParseStream): SynResult<Stmt> =
    parseStmt(input, AllowNoSemi(false))

private fun parseStmt(input: ParseStream, allowNoSemi: AllowNoSemi): SynResult<Stmt> {
    val stmt = parseStmtFull(input).getOrElse { return SynResult.failure(it) }
    if (!allowNoSemi.value && stmtRequiresSemicolon(stmt)) {
        return SynResult.failure(input.error("expected semicolon"))
    }
    return SynResult.success(stmt)
}

internal fun stmtLocal(input: ParseStream): SynResult<Stmt.Local> {
    val attrs = parseOuterAttributes(input).getOrElse { return SynResult.failure(it) }
    val letToken = LetParse.parse(input).getOrElse { return SynResult.failure(it) }

    var pat = parsePatSingle(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(ColonPeek)) {
        val colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        val ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        pat = Pat.TypeAscription(mutableListOf(), pat, colonToken, ty)
    }

    var init: LocalInit? = null
    if (input.peek(EqPeek)) {
        val eq = EqParse.parse(input).getOrElse { return SynResult.failure(it) }
        val expr = parseExprFull(input).getOrElse { return SynResult.failure(it) }

        var diverge: ElseExpr? = null
        if (!exprTrailingBrace(expr) && input.peek(ElsePeek)) {
            val elseToken = ElseParse.parse(input).getOrElse { return SynResult.failure(it) }
            val bracesResult = braced(input).getOrElse { return SynResult.failure(it) }
            val stmts = mutableListOf<Stmt>()
            while (!bracesResult.content.isEmpty()) {
                val stmtResult = parseStmtFull(bracesResult.content)
                if (stmtResult.isFailure) {
                    return SynResult.failure((stmtResult as SynResult.Failure).error)
                }
                stmts.add(stmtResult.getOrThrow())
            }
            bracesResult.content.finishChildBuffer()
            val block = Block(bracesResult.token, stmts)
            diverge = ElseExpr(elseToken, Expr.BlockExpr(mutableListOf(), null, block))
        }

        init = LocalInit(eq, expr, diverge)
    }
    val semi = SemiParse.parse(input).getOrElse { return SynResult.failure(it) }
    return SynResult.success(Stmt.Local(attrs, letToken, LocalModifiers(), pat, init, semi))
}

internal fun stmtExpr(input: ParseStream): SynResult<Stmt.ExprStmt> {
    val expr = parseExprFull(input)
    if (expr.isFailure) return expr.let { SynResult.failure((it as SynResult.Failure).error) }
    val semi = if (input.peek(SemiPeek)) SemiParse.parse(input).getOrThrow() else null
    return SynResult.success(Stmt.ExprStmt(expr.getOrThrow(), semi))
}

internal fun stmtMac(input: ParseStream): SynResult<Stmt.MacroStmt> {
    val pathResult = PathParse.parse(input)
    if (pathResult.isFailure) return pathResult.let { SynResult.failure((it as SynResult.Failure).error) }
    val bangResult = NotParse.parse(input)
    if (bangResult.isFailure) return bangResult.let { SynResult.failure((it as SynResult.Failure).error) }
    val delimResult = parseDelimiter(input)
    if (delimResult.isFailure) return delimResult.let { SynResult.failure((it as SynResult.Failure).error) }
    var (delim, tokens) = delimResult.getOrThrow()
    val mac = Macro(pathResult.getOrThrow(), bangResult.getOrThrow(), delim, tokens)
    val semi = if (input.peek(SemiPeek)) SemiParse.parse(input).getOrThrow() else null
    return SynResult.success(Stmt.MacroStmt(mutableListOf(), mac, semi))
}

public object BlockParse {
    public fun parse(input: ParseStream): SynResult<Block> = parseBlock(input)
}

internal fun parseBlock(input: ParseStream): SynResult<Block> {
    val bracesVal = braced(input).getOrElse { return SynResult.failure(it) }
    val stmts = parseWithin(bracesVal.content).getOrElse { return SynResult.failure(it) }
    bracesVal.content.finishChildBuffer()
    return SynResult.success(Block(bracesVal.token, stmts))
}

/**
 * Parse the body of a block as zero or more statements, possibly
 * including one trailing expression.
 */
public fun parseWithin(input: ParseStream): SynResult<List<Stmt>> {
    val stmts = mutableListOf<Stmt>()
    while (!input.isEmpty()) {
        while (input.peek(SemiPeek)) {
            stmts.add(Stmt.ExprStmt(Expr.Verbatim(TokenStream.new()), SemiParse.parse(input).getOrThrow()))
        }
        if (input.isEmpty()) break
        val stmt = parseStmt(input, AllowNoSemi(true)).getOrElse { return SynResult.failure(it) }
        val requiresSemicolon = stmtRequiresSemicolon(stmt)
        stmts.add(stmt)
        if (!input.isEmpty() && requiresSemicolon) {
            return SynResult.failure(input.error("unexpected token, expected `;`"))
        }
    }
    return SynResult.success(stmts)
}

internal fun stmtRequiresSemicolon(stmt: Stmt): Boolean =
    when (stmt) {
        is Stmt.ExprStmt -> stmt.semiToken == null && requiresSemiToBeStmt(stmt.expr)
        is Stmt.MacroStmt -> stmt.semiToken == null && !stmt.mac.isBrace()
        else -> false
    }
