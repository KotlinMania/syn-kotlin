// port-lint: source stmt.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.quote.ToTokens
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Let
import io.github.kotlinmania.syn.token.Semi

/**
 * A braced block containing statements.
 */
public data class Block(
    public var braceToken: Brace,
    public var stmts: MutableList<Stmt>,
) : ToTokens {
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
        public var attrs: MutableList<Attribute>,
        public var letToken: Let,
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

        override fun deepCopy(): Local = Local(attrs.mapTo(mutableListOf()) { it.deepCopy() }, letToken, pat.deepCopy(), init?.deepCopy(), semiToken)
    }

    /** An item definition. */
    public data class ItemStmt(
        public var item: Item,
    ) : Stmt() {
        override fun toTokens(tokens: TokenStream) {
            item.toTokens(tokens)
        }

        override fun deepCopy(): ItemStmt = ItemStmt(item)
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
        public var attrs: MutableList<Attribute>,
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
 * The expression assigned in a local binding, including optional
 * diverging else block.
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
    var stmt = parseStmtFull(input).getOrElse { return SynResult.failure(it) }
    if (!allowNoSemi.value && stmtRequiresSemicolon(stmt)) {
        return SynResult.failure(input.error("expected semicolon"))
    }
    return SynResult.success(stmt)
}

internal fun stmtLocal(input: ParseStream): SynResult<Stmt.Local> {
    var letToken = LetParse.parse(input).getOrThrow()
    var pat = parsePatFull(input).getOrElse { return SynResult.failure(it) }
    if (input.peek(ColonPeek)) {
        var colonToken = ColonParse.parse(input).getOrElse { return SynResult.failure(it) }
        var ty = parseTypeFull(input).getOrElse { return SynResult.failure(it) }
        pat = Pat.TypeAscription(emptyList(), pat, colonToken, ty)
    }
    var init: LocalInit? = null
    if (input.peek(EqPeek)) {
        var eq = EqParse.parse(input).getOrThrow()
        var expr = parseExprFull(input).getOrThrow()
        init = LocalInit(eq, expr, null)
    }
    var semi = SemiParse.parse(input).getOrThrow()
    return SynResult.success(Stmt.Local(emptyList(), letToken, pat, init, semi))
}

internal fun stmtExpr(input: ParseStream): SynResult<Stmt.ExprStmt> {
    var expr = parseExprFull(input)
    if (expr.isFailure) return expr.let { SynResult.failure((it as SynResult.Failure).error) }
    var semi = if (input.peek(SemiPeek)) SemiParse.parse(input).getOrThrow() else null
    return SynResult.success(Stmt.ExprStmt(expr.getOrThrow(), semi))
}

internal fun stmtMac(input: ParseStream): SynResult<Stmt.MacroStmt> {
    var pathResult = PathParse.parse(input)
    if (pathResult.isFailure) return pathResult.let { SynResult.failure((it as SynResult.Failure).error) }
    var bangResult = NotParse.parse(input)
    if (bangResult.isFailure) return bangResult.let { SynResult.failure((it as SynResult.Failure).error) }
    var delimResult = parseDelimiter(input)
    if (delimResult.isFailure) return delimResult.let { SynResult.failure((it as SynResult.Failure).error) }
    var (delim, tokens) = delimResult.getOrThrow()
    var mac = Macro(pathResult.getOrThrow(), bangResult.getOrThrow(), delim, tokens)
    var semi = if (input.peek(SemiPeek)) SemiParse.parse(input).getOrThrow() else null
    return SynResult.success(Stmt.MacroStmt(emptyList(), mac, semi))
}

public fun parseWithin(input: ParseStream): SynResult<List<Stmt>> {
    var stmts = mutableListOf<Stmt>()
    while (!input.isEmpty()) {
        while (input.peek(SemiPeek)) {
            stmts.add(Stmt.ExprStmt(Expr.Verbatim(TokenStream.new()), SemiParse.parse(input).getOrThrow()))
        }
        if (input.isEmpty()) break
        var stmt = parseStmt(input, AllowNoSemi(true)).getOrElse { return SynResult.failure(it) }
        var requiresSemicolon = stmtRequiresSemicolon(stmt)
        stmts.add(stmt)
        if (!input.isEmpty() && requiresSemicolon) {
            return SynResult.failure(input.error("unexpected token, expected `;`"))
        }
    }
    return SynResult.success(stmts)
}

internal fun stmtRequiresSemicolon(stmt: Stmt): Boolean =
    when (stmt) {
        is Stmt.ExprStmt -> stmt.semiToken == null && Classify.requiresSemiToBeStmt(stmt.expr)
        is Stmt.MacroStmt -> stmt.semiToken == null && !stmt.mac.isBrace()
        else -> false
    }
