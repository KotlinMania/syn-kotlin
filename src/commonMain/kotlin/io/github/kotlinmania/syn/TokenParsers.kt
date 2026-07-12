// port-lint: source token.rs

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.syn.token.Abstract
import io.github.kotlinmania.syn.token.And
import io.github.kotlinmania.syn.token.AndAnd
import io.github.kotlinmania.syn.token.AndEq
import io.github.kotlinmania.syn.token.As
import io.github.kotlinmania.syn.token.Async
import io.github.kotlinmania.syn.token.At
import io.github.kotlinmania.syn.token.Auto
import io.github.kotlinmania.syn.token.Await
import io.github.kotlinmania.syn.token.Become
import io.github.kotlinmania.syn.token.Box
import io.github.kotlinmania.syn.token.Break
import io.github.kotlinmania.syn.token.Caret
import io.github.kotlinmania.syn.token.CaretEq
import io.github.kotlinmania.syn.token.Colon
import io.github.kotlinmania.syn.token.Const
import io.github.kotlinmania.syn.token.Continue
import io.github.kotlinmania.syn.token.Crate
import io.github.kotlinmania.syn.token.Default
import io.github.kotlinmania.syn.token.Do
import io.github.kotlinmania.syn.token.Dollar
import io.github.kotlinmania.syn.token.Dot
import io.github.kotlinmania.syn.token.DotDot
import io.github.kotlinmania.syn.token.DotDotDot
import io.github.kotlinmania.syn.token.DotDotEq
import io.github.kotlinmania.syn.token.Dyn
import io.github.kotlinmania.syn.token.Else
import io.github.kotlinmania.syn.token.Enum
import io.github.kotlinmania.syn.token.Eq
import io.github.kotlinmania.syn.token.EqEq
import io.github.kotlinmania.syn.token.Extern
import io.github.kotlinmania.syn.token.FatArrow
import io.github.kotlinmania.syn.token.Final
import io.github.kotlinmania.syn.token.Fn
import io.github.kotlinmania.syn.token.For
import io.github.kotlinmania.syn.token.Ge
import io.github.kotlinmania.syn.token.Gt
import io.github.kotlinmania.syn.token.If
import io.github.kotlinmania.syn.token.Impl
import io.github.kotlinmania.syn.token.LArrow
import io.github.kotlinmania.syn.token.Le
import io.github.kotlinmania.syn.token.Let
import io.github.kotlinmania.syn.token.Loop
import io.github.kotlinmania.syn.token.Lt
import io.github.kotlinmania.syn.token.Macro
import io.github.kotlinmania.syn.token.Match
import io.github.kotlinmania.syn.token.Minus
import io.github.kotlinmania.syn.token.MinusEq
import io.github.kotlinmania.syn.token.Mod
import io.github.kotlinmania.syn.token.Move
import io.github.kotlinmania.syn.token.Mut
import io.github.kotlinmania.syn.token.Ne
import io.github.kotlinmania.syn.token.Not
import io.github.kotlinmania.syn.token.Or
import io.github.kotlinmania.syn.token.OrEq
import io.github.kotlinmania.syn.token.OrOr
import io.github.kotlinmania.syn.token.Override
import io.github.kotlinmania.syn.token.Percent
import io.github.kotlinmania.syn.token.PercentEq
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.syn.token.PlusEq
import io.github.kotlinmania.syn.token.Pound
import io.github.kotlinmania.syn.token.Priv
import io.github.kotlinmania.syn.token.Question
import io.github.kotlinmania.syn.token.RArrow
import io.github.kotlinmania.syn.token.Raw
import io.github.kotlinmania.syn.token.Ref
import io.github.kotlinmania.syn.token.Return
import io.github.kotlinmania.syn.token.SelfType
import io.github.kotlinmania.syn.token.SelfValue
import io.github.kotlinmania.syn.token.Semi
import io.github.kotlinmania.syn.token.Shl
import io.github.kotlinmania.syn.token.ShlEq
import io.github.kotlinmania.syn.token.Shr
import io.github.kotlinmania.syn.token.ShrEq
import io.github.kotlinmania.syn.token.Slash
import io.github.kotlinmania.syn.token.SlashEq
import io.github.kotlinmania.syn.token.Star
import io.github.kotlinmania.syn.token.StarEq
import io.github.kotlinmania.syn.token.Static
import io.github.kotlinmania.syn.token.Struct
import io.github.kotlinmania.syn.token.Super
import io.github.kotlinmania.syn.token.SynTypeToken
import io.github.kotlinmania.syn.token.Tilde
import io.github.kotlinmania.syn.token.Trait
import io.github.kotlinmania.syn.token.Try
import io.github.kotlinmania.syn.token.Typeof
import io.github.kotlinmania.syn.token.Underscore
import io.github.kotlinmania.syn.token.Union
import io.github.kotlinmania.syn.token.Unsafe
import io.github.kotlinmania.syn.token.Unsized
import io.github.kotlinmania.syn.token.Use
import io.github.kotlinmania.syn.token.Virtual
import io.github.kotlinmania.syn.token.Where
import io.github.kotlinmania.syn.token.While
import io.github.kotlinmania.syn.token.Yield

// ── Keyword Peek / Parse ──────────────────────────────────────────────────

private val UNSAFE_KW = charArrayOf('u', 'n', 's', 'a', 'f', 'e').concatToString()

/** Peeks for the abstract keyword token. */
public object AbstractPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "abstract"
    }

    override fun display(): String = "`abstract`"
}

/** Parses the abstract keyword token. */
public object AbstractParse {
    fun parse(input: ParseStream): SynResult<Abstract> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `abstract`"))
            if (ident.toString() != "abstract") {
                return@step SynResult.failure(cursor.error("expected `abstract`"))
            }
            SynResult.success(Abstract.from(ident.span()) to rest)
        }
}

/** Peeks for the casting keyword token. */
public object AsPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "as"
    }

    override fun display(): String = "`as`"
}

/** Parses the casting keyword token. */
public object AsParse {
    fun parse(input: ParseStream): SynResult<As> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `as`"))
            if (ident.toString() != "as") {
                return@step SynResult.failure(cursor.error("expected `as`"))
            }
            SynResult.success(As.from(ident.span()) to rest)
        }
}

/** Peeks for the asynchronous keyword token. */
public object AsyncPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "async"
    }

    override fun display(): String = "`async`"
}

/** Parses the asynchronous keyword token. */
public object AsyncParse {
    fun parse(input: ParseStream): SynResult<Async> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `async`"))
            if (ident.toString() != "async") {
                return@step SynResult.failure(cursor.error("expected `async`"))
            }
            SynResult.success(Async.from(ident.span()) to rest)
        }
}

/** Peeks for the auto keyword token. */
public object AutoPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "auto"
    }

    override fun display(): String = "`auto`"
}

/** Parses the auto keyword token. */
public object AutoParse {
    fun parse(input: ParseStream): SynResult<Auto> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `auto`"))
            if (ident.toString() != "auto") {
                return@step SynResult.failure(cursor.error("expected `auto`"))
            }
            SynResult.success(Auto.from(ident.span()) to rest)
        }
}

/** Peeks for the await-expression keyword token. */
public object AwaitPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "await"
    }

    override fun display(): String = "`await`"
}

/** Parses the await-expression keyword token. */
public object AwaitParse {
    fun parse(input: ParseStream): SynResult<Await> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `await`"))
            if (ident.toString() != "await") {
                return@step SynResult.failure(cursor.error("expected `await`"))
            }
            SynResult.success(Await.from(ident.span()) to rest)
        }
}

/** Peeks for the become keyword token. */
public object BecomePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "become"
    }

    override fun display(): String = "`become`"
}

/** Parses the become keyword token. */
public object BecomeParse {
    fun parse(input: ParseStream): SynResult<Become> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `become`"))
            if (ident.toString() != "become") {
                return@step SynResult.failure(cursor.error("expected `become`"))
            }
            SynResult.success(Become.from(ident.span()) to rest)
        }
}

/** Peeks for the heap-alloc keyword token. */
public object BoxPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "box"
    }

    override fun display(): String = "`box`"
}

/** Parses the heap-alloc keyword token. */
public object BoxParse {
    fun parse(input: ParseStream): SynResult<Box> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `box`"))
            if (ident.toString() != "box") {
                return@step SynResult.failure(cursor.error("expected `box`"))
            }
            SynResult.success(Box.from(ident.span()) to rest)
        }
}

/** Peeks for the loop-break keyword token. */
public object BreakPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "break"
    }

    override fun display(): String = "`break`"
}

/** Parses the loop-break keyword token. */
public object BreakParse {
    fun parse(input: ParseStream): SynResult<Break> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `break`"))
            if (ident.toString() != "break") {
                return@step SynResult.failure(cursor.error("expected `break`"))
            }
            SynResult.success(Break.from(ident.span()) to rest)
        }
}

/** Peeks for the compile-time-constant keyword token. */
public object ConstPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "const"
    }

    override fun display(): String = "`const`"
}

/** Parses the compile-time-constant keyword token. */
public object ConstParse {
    fun parse(input: ParseStream): SynResult<Const> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `const`"))
            if (ident.toString() != "const") {
                return@step SynResult.failure(cursor.error("expected `const`"))
            }
            SynResult.success(Const.from(ident.span()) to rest)
        }
}

/** Peeks for the loop-continue keyword token. */
public object ContinuePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "continue"
    }

    override fun display(): String = "`continue`"
}

/** Parses the loop-continue keyword token. */
public object ContinueParse {
    fun parse(input: ParseStream): SynResult<Continue> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `continue`"))
            if (ident.toString() != "continue") {
                return@step SynResult.failure(cursor.error("expected `continue`"))
            }
            SynResult.success(Continue.from(ident.span()) to rest)
        }
}

/** Peeks for the crate-root keyword token. */
public object CratePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "crate"
    }

    override fun display(): String = "`crate`"
}

/** Parses the crate-root keyword token. */
public object CrateParse {
    fun parse(input: ParseStream): SynResult<Crate> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `crate`"))
            if (ident.toString() != "crate") {
                return@step SynResult.failure(cursor.error("expected `crate`"))
            }
            SynResult.success(Crate.from(ident.span()) to rest)
        }
}

/** Peeks for the default keyword token. */
public object DefaultPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "default"
    }

    override fun display(): String = "`default`"
}

/** Parses the default keyword token. */
public object DefaultParse {
    fun parse(input: ParseStream): SynResult<Default> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `default`"))
            if (ident.toString() != "default") {
                return@step SynResult.failure(cursor.error("expected `default`"))
            }
            SynResult.success(Default.from(ident.span()) to rest)
        }
}

/** Peeks for the do keyword token. */
public object DoPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "do"
    }

    override fun display(): String = "`do`"
}

/** Parses the do keyword token. */
public object DoParse {
    fun parse(input: ParseStream): SynResult<Do> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `do`"))
            if (ident.toString() != "do") {
                return@step SynResult.failure(cursor.error("expected `do`"))
            }
            SynResult.success(Do.from(ident.span()) to rest)
        }
}

/** Peeks for the dynamic-dispatch keyword token. */
public object DynPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "dyn"
    }

    override fun display(): String = "`dyn`"
}

/** Parses the dynamic-dispatch keyword token. */
public object DynParse {
    fun parse(input: ParseStream): SynResult<Dyn> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `dyn`"))
            if (ident.toString() != "dyn") {
                return@step SynResult.failure(cursor.error("expected `dyn`"))
            }
            SynResult.success(Dyn.from(ident.span()) to rest)
        }
}

/** Peeks for the alternative keyword token. */
public object ElsePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "else"
    }

    override fun display(): String = "`else`"
}

/** Parses the alternative keyword token. */
public object ElseParse {
    fun parse(input: ParseStream): SynResult<Else> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `else`"))
            if (ident.toString() != "else") {
                return@step SynResult.failure(cursor.error("expected `else`"))
            }
            SynResult.success(Else.from(ident.span()) to rest)
        }
}

/** Peeks for the enumeration keyword token. */
public object EnumPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "enum"
    }

    override fun display(): String = "`enum`"
}

/** Parses the enumeration keyword token. */
public object EnumParse {
    fun parse(input: ParseStream): SynResult<Enum> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `enum`"))
            if (ident.toString() != "enum") {
                return@step SynResult.failure(cursor.error("expected `enum`"))
            }
            SynResult.success(Enum.from(ident.span()) to rest)
        }
}

/** Peeks for the foreign-function keyword token. */
public object ExternPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "extern"
    }

    override fun display(): String = "`extern`"
}

/** Parses the foreign-function keyword token. */
public object ExternParse {
    fun parse(input: ParseStream): SynResult<Extern> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `extern`"))
            if (ident.toString() != "extern") {
                return@step SynResult.failure(cursor.error("expected `extern`"))
            }
            SynResult.success(Extern.from(ident.span()) to rest)
        }
}

/** Peeks for the final keyword token. */
public object FinalPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "final"
    }

    override fun display(): String = "`final`"
}

/** Parses the final keyword token. */
public object FinalParse {
    fun parse(input: ParseStream): SynResult<Final> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `final`"))
            if (ident.toString() != "final") {
                return@step SynResult.failure(cursor.error("expected `final`"))
            }
            SynResult.success(Final.from(ident.span()) to rest)
        }
}

/** Peeks for the function keyword token. */
public object FnPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "fn"
    }

    override fun display(): String = "`fn`"
}

/** Parses the function keyword token. */
public object FnParse {
    fun parse(input: ParseStream): SynResult<Fn> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `fn`"))
            if (ident.toString() != "fn") {
                return@step SynResult.failure(cursor.error("expected `fn`"))
            }
            SynResult.success(Fn.from(ident.span()) to rest)
        }
}

/** Peeks for the for-loop keyword token. */
public object ForPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "for"
    }

    override fun display(): String = "`for`"
}

/** Parses the for-loop keyword token. */
public object ForParse {
    fun parse(input: ParseStream): SynResult<For> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `for`"))
            if (ident.toString() != "for") {
                return@step SynResult.failure(cursor.error("expected `for`"))
            }
            SynResult.success(For.from(ident.span()) to rest)
        }
}

/** Peeks for the conditional keyword token. */
public object IfPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "if"
    }

    override fun display(): String = "`if`"
}

/** Parses the conditional keyword token. */
public object IfParse {
    fun parse(input: ParseStream): SynResult<If> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `if`"))
            if (ident.toString() != "if") {
                return@step SynResult.failure(cursor.error("expected `if`"))
            }
            SynResult.success(If.from(ident.span()) to rest)
        }
}

/** Peeks for the implementation keyword token. */
public object ImplPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "impl"
    }

    override fun display(): String = "`impl`"
}

/** Parses the implementation keyword token. */
public object ImplParse {
    fun parse(input: ParseStream): SynResult<Impl> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `impl`"))
            if (ident.toString() != "impl") {
                return@step SynResult.failure(cursor.error("expected `impl`"))
            }
            SynResult.success(Impl.from(ident.span()) to rest)
        }
}

/** Peeks for the binding keyword token. */
public object LetPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "let"
    }

    override fun display(): String = "`let`"
}

/** Parses the binding keyword token. */
public object LetParse {
    fun parse(input: ParseStream): SynResult<Let> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `let`"))
            if (ident.toString() != "let") {
                return@step SynResult.failure(cursor.error("expected `let`"))
            }
            SynResult.success(Let.from(ident.span()) to rest)
        }
}

/** Peeks for the infinite-loop keyword token. */
public object LoopPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "loop"
    }

    override fun display(): String = "`loop`"
}

/** Parses the infinite-loop keyword token. */
public object LoopParse {
    fun parse(input: ParseStream): SynResult<Loop> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `loop`"))
            if (ident.toString() != "loop") {
                return@step SynResult.failure(cursor.error("expected `loop`"))
            }
            SynResult.success(Loop.from(ident.span()) to rest)
        }
}

/** Peeks for the macro-definition keyword token. */
public object MacroPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "macro"
    }

    override fun display(): String = "`macro`"
}

/** Parses the macro-definition keyword token. */
public object MacroParse {
    fun parse(input: ParseStream): SynResult<Macro> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `macro`"))
            if (ident.toString() != "macro") {
                return@step SynResult.failure(cursor.error("expected `macro`"))
            }
            SynResult.success(Macro.from(ident.span()) to rest)
        }
}

/** Peeks for the pattern-match keyword token. */
public object MatchPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "match"
    }

    override fun display(): String = "`match`"
}

/** Parses the pattern-match keyword token. */
public object MatchParse {
    fun parse(input: ParseStream): SynResult<Match> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `match`"))
            if (ident.toString() != "match") {
                return@step SynResult.failure(cursor.error("expected `match`"))
            }
            SynResult.success(Match.from(ident.span()) to rest)
        }
}

/** Peeks for the module keyword token. */
public object ModPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "mod"
    }

    override fun display(): String = "`mod`"
}

/** Parses the module keyword token. */
public object ModParse {
    fun parse(input: ParseStream): SynResult<Mod> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `mod`"))
            if (ident.toString() != "mod") {
                return@step SynResult.failure(cursor.error("expected `mod`"))
            }
            SynResult.success(Mod.from(ident.span()) to rest)
        }
}

/** Peeks for the capture keyword token. */
public object MovePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "move"
    }

    override fun display(): String = "`move`"
}

/** Parses the capture keyword token. */
public object MoveParse {
    fun parse(input: ParseStream): SynResult<Move> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `move`"))
            if (ident.toString() != "move") {
                return@step SynResult.failure(cursor.error("expected `move`"))
            }
            SynResult.success(Move.from(ident.span()) to rest)
        }
}

/** Peeks for the mutable keyword token. */
public object MutPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "mut"
    }

    override fun display(): String = "`mut`"
}

/** Parses the mutable keyword token. */
public object MutParse {
    fun parse(input: ParseStream): SynResult<Mut> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `mut`"))
            if (ident.toString() != "mut") {
                return@step SynResult.failure(cursor.error("expected `mut`"))
            }
            SynResult.success(Mut.from(ident.span()) to rest)
        }
}

/** Peeks for the override keyword token. */
public object OverridePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "override"
    }

    override fun display(): String = "`override`"
}

/** Parses the override keyword token. */
public object OverrideParse {
    fun parse(input: ParseStream): SynResult<Override> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `override`"))
            if (ident.toString() != "override") {
                return@step SynResult.failure(cursor.error("expected `override`"))
            }
            SynResult.success(Override.from(ident.span()) to rest)
        }
}

/** Peeks for the priv keyword token. */
public object PrivPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "priv"
    }

    override fun display(): String = "`priv`"
}

/** Parses the priv keyword token. */
public object PrivParse {
    fun parse(input: ParseStream): SynResult<Priv> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `priv`"))
            if (ident.toString() != "priv") {
                return@step SynResult.failure(cursor.error("expected `priv`"))
            }
            SynResult.success(Priv.from(ident.span()) to rest)
        }
}

/** Peeks for the raw-identifier keyword token. */
public object RawPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "raw"
    }

    override fun display(): String = "`raw`"
}

/** Parses the raw-identifier keyword token. */
public object RawParse {
    fun parse(input: ParseStream): SynResult<Raw> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `raw`"))
            if (ident.toString() != "raw") {
                return@step SynResult.failure(cursor.error("expected `raw`"))
            }
            SynResult.success(Raw.from(ident.span()) to rest)
        }
}

/** Peeks for the reference-binding keyword token. */
public object RefPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "ref"
    }

    override fun display(): String = "`ref`"
}

/** Parses the reference-binding keyword token. */
public object RefParse {
    fun parse(input: ParseStream): SynResult<Ref> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `ref`"))
            if (ident.toString() != "ref") {
                return@step SynResult.failure(cursor.error("expected `ref`"))
            }
            SynResult.success(Ref.from(ident.span()) to rest)
        }
}

/** Peeks for the return-value keyword token. */
public object ReturnPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "return"
    }

    override fun display(): String = "`return`"
}

/** Parses the return-value keyword token. */
public object ReturnParse {
    fun parse(input: ParseStream): SynResult<Return> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `return`"))
            if (ident.toString() != "return") {
                return@step SynResult.failure(cursor.error("expected `return`"))
            }
            SynResult.success(Return.from(ident.span()) to rest)
        }
}

/** Peeks for the static-storage keyword token. */
public object StaticPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "static"
    }

    override fun display(): String = "`static`"
}

/** Parses the static-storage keyword token. */
public object StaticParse {
    fun parse(input: ParseStream): SynResult<Static> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `static`"))
            if (ident.toString() != "static") {
                return@step SynResult.failure(cursor.error("expected `static`"))
            }
            SynResult.success(Static.from(ident.span()) to rest)
        }
}

/** Peeks for the structure keyword token. */
public object StructPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "struct"
    }

    override fun display(): String = "`struct`"
}

/** Parses the structure keyword token. */
public object StructParse {
    fun parse(input: ParseStream): SynResult<Struct> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `struct`"))
            if (ident.toString() != "struct") {
                return@step SynResult.failure(cursor.error("expected `struct`"))
            }
            SynResult.success(Struct.from(ident.span()) to rest)
        }
}

/** Peeks for the parent-scope keyword token. */
public object SuperPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "super"
    }

    override fun display(): String = "`super`"
}

/** Parses the parent-scope keyword token. */
public object SuperParse {
    fun parse(input: ParseStream): SynResult<Super> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `super`"))
            if (ident.toString() != "super") {
                return@step SynResult.failure(cursor.error("expected `super`"))
            }
            SynResult.success(Super.from(ident.span()) to rest)
        }
}

/** Peeks for the trait-definition keyword token. */
public object TraitPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "trait"
    }

    override fun display(): String = "`trait`"
}

/** Parses the trait-definition keyword token. */
public object TraitParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Trait> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `trait`"))
            if (ident.toString() != "trait") {
                return@step SynResult.failure(cursor.error("expected `trait`"))
            }
            SynResult.success(
                io.github.kotlinmania.syn.token.Trait
                    .from(ident.span()) to rest,
            )
        }
}

/** Peeks for the try keyword token. */
public object TryPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "try"
    }

    override fun display(): String = "`try`"
}

/** Parses the try keyword token. */
public object TryParse {
    fun parse(input: ParseStream): SynResult<Try> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `try`"))
            if (ident.toString() != "try") {
                return@step SynResult.failure(cursor.error("expected `try`"))
            }
            SynResult.success(Try.from(ident.span()) to rest)
        }
}

/** Peeks for the union-definition keyword token. */
public object UnionPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "union"
    }

    override fun display(): String = "`union`"
}

/** Parses the union-definition keyword token. */
public object UnionParse {
    fun parse(input: ParseStream): SynResult<Union> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `union`"))
            if (ident.toString() != "union") {
                return@step SynResult.failure(cursor.error("expected `union`"))
            }
            SynResult.success(Union.from(ident.span()) to rest)
        }
}

/** Peeks for the memory-safety keyword. */
public object UnsafePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == UNSAFE_KW
    }

    override fun display(): String = "`$UNSAFE_KW`"
}

/** Parses the memory-safety keyword. */
public object UnsafeParse {
    fun parse(input: ParseStream): SynResult<Unsafe> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `$UNSAFE_KW`"))
            if (ident.toString() != UNSAFE_KW) {
                return@step SynResult.failure(cursor.error("expected `$UNSAFE_KW`"))
            }
            SynResult.success(Unsafe.from(ident.span()) to rest)
        }
}

/** Peeks for the unsized keyword token. */
public object UnsizedPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "unsized"
    }

    override fun display(): String = "`unsized`"
}

/** Parses the unsized keyword token. */
public object UnsizedParse {
    fun parse(input: ParseStream): SynResult<Unsized> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `unsized`"))
            if (ident.toString() != "unsized") {
                return@step SynResult.failure(cursor.error("expected `unsized`"))
            }
            SynResult.success(Unsized.from(ident.span()) to rest)
        }
}

/** Peeks for the import keyword token. */
public object UsePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "use"
    }

    override fun display(): String = "`use`"
}

/** Parses the import keyword token. */
public object UseParse {
    fun parse(input: ParseStream): SynResult<Use> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `use`"))
            if (ident.toString() != "use") {
                return@step SynResult.failure(cursor.error("expected `use`"))
            }
            SynResult.success(Use.from(ident.span()) to rest)
        }
}

/** Peeks for the virtual keyword token. */
public object VirtualPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "virtual"
    }

    override fun display(): String = "`virtual`"
}

/** Parses the virtual keyword token. */
public object VirtualParse {
    fun parse(input: ParseStream): SynResult<Virtual> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `virtual`"))
            if (ident.toString() != "virtual") {
                return@step SynResult.failure(cursor.error("expected `virtual`"))
            }
            SynResult.success(Virtual.from(ident.span()) to rest)
        }
}

/** Peeks for the constraint keyword token. */
public object WherePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "where"
    }

    override fun display(): String = "`where`"
}

/** Parses the constraint keyword token. */
public object WhereParse {
    fun parse(input: ParseStream): SynResult<Where> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `where`"))
            if (ident.toString() != "where") {
                return@step SynResult.failure(cursor.error("expected `where`"))
            }
            SynResult.success(Where.from(ident.span()) to rest)
        }
}

/** Peeks for the conditional-loop keyword token. */
public object WhilePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "while"
    }

    override fun display(): String = "`while`"
}

/** Parses the conditional-loop keyword token. */
public object WhileParse {
    fun parse(input: ParseStream): SynResult<While> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `while`"))
            if (ident.toString() != "while") {
                return@step SynResult.failure(cursor.error("expected `while`"))
            }
            SynResult.success(While.from(ident.span()) to rest)
        }
}

/** Peeks for the generator-yield keyword token. */
public object YieldPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "yield"
    }

    override fun display(): String = "`yield`"
}

/** Parses the generator-yield keyword token. */
public object YieldParse {
    fun parse(input: ParseStream): SynResult<Yield> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `yield`"))
            if (ident.toString() != "yield") {
                return@step SynResult.failure(cursor.error("expected `yield`"))
            }
            SynResult.success(Yield.from(ident.span()) to rest)
        }
}

/** Peeks for the capital-self keyword token. */
public object SelfTypePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "Self"
    }

    override fun display(): String = "`Self`"
}

/** Parses the capital-self keyword token. */
public object SelfTypeParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.SelfType> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `Self`"))
            if (ident.toString() != "Self") {
                return@step SynResult.failure(cursor.error("expected `Self`"))
            }
            SynResult.success(
                io.github.kotlinmania.syn.token.SelfType
                    .from(ident.span()) to rest,
            )
        }
}

/** Peeks for the lowercase-self keyword token. */
public object SelfValuePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "self"
    }

    override fun display(): String = "`self`"
}

/** Parses the lowercase-self keyword token. */
public object SelfValueParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.SelfValue> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `self`"))
            if (ident.toString() != "self") {
                return@step SynResult.failure(cursor.error("expected `self`"))
            }
            SynResult.success(
                io.github.kotlinmania.syn.token.SelfValue
                    .from(ident.span()) to rest,
            )
        }
}

/** Peeks for the type-alias keyword token. */
public object SynTypePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "type"
    }

    override fun display(): String = "`type`"
}

/** Parses the type-alias keyword token. */
public object SynTypeParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.SynTypeToken> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `type`"))
            if (ident.toString() != "type") {
                return@step SynResult.failure(cursor.error("expected `type`"))
            }
            SynResult.success(
                io.github.kotlinmania.syn.token.SynTypeToken
                    .from(ident.span()) to rest,
            )
        }
}

/** Peeks for the typeof keyword token. */
public object TypeofPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "typeof"
    }

    override fun display(): String = "`typeof`"
}

/** Parses the typeof keyword token. */
public object TypeofParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Typeof> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `typeof`"))
            if (ident.toString() != "typeof") {
                return@step SynResult.failure(cursor.error("expected `typeof`"))
            }
            SynResult.success(
                io.github.kotlinmania.syn.token.Typeof
                    .from(ident.span()) to rest,
            )
        }
}

// ── Punctuation Peek / Parse (single-char) ───────────────────────────────

/** Peeks for the `&` punctuation token. */
public object AndPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '&'
    }

    override fun display(): String = "`&`"
}

/** Parses the `&` punctuation token. */
public object AndParse {
    fun parse(input: ParseStream): SynResult<And> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `&`"))
            if (punct.asChar() != '&') {
                return@step SynResult.failure(cursor.error("expected `&`"))
            }
            SynResult.success(And.from(punct.span()) to rest)
        }
}

/** Peeks for the `@` punctuation token. */
public object AtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '@'
    }

    override fun display(): String = "`@`"
}

/** Parses the `@` punctuation token. */
public object AtParse {
    fun parse(input: ParseStream): SynResult<At> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `@`"))
            if (punct.asChar() != '@') {
                return@step SynResult.failure(cursor.error("expected `@`"))
            }
            SynResult.success(At.from(punct.span()) to rest)
        }
}

/** Peeks for the `^` punctuation token. */
public object CaretPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '^'
    }

    override fun display(): String = "`^`"
}

/** Parses the `^` punctuation token. */
public object CaretParse {
    fun parse(input: ParseStream): SynResult<Caret> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `^`"))
            if (punct.asChar() != '^') {
                return@step SynResult.failure(cursor.error("expected `^`"))
            }
            SynResult.success(Caret.from(punct.span()) to rest)
        }
}

/** Peeks for the `:` punctuation token. */
public object ColonPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == ':'
    }

    override fun display(): String = "`:`"
}

/** Parses the `:` punctuation token. */
public object ColonParse {
    fun parse(input: ParseStream): SynResult<Colon> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `:`"))
            if (punct.asChar() != ':') {
                return@step SynResult.failure(cursor.error("expected `:`"))
            }
            SynResult.success(Colon.from(punct.span()) to rest)
        }
}

/** Peeks for the `$` punctuation token. */
public object DollarPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '$'
    }

    override fun display(): String = "`$`"
}

/** Parses the `$` punctuation token. */
public object DollarParse {
    fun parse(input: ParseStream): SynResult<Dollar> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `$`"))
            if (punct.asChar() != '$') {
                return@step SynResult.failure(cursor.error("expected `$`"))
            }
            SynResult.success(Dollar.from(punct.span()) to rest)
        }
}

/** Peeks for the `.` punctuation token. */
public object DotPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '.'
    }

    override fun display(): String = "`.`"
}

/** Parses the `.` punctuation token. */
public object DotParse {
    fun parse(input: ParseStream): SynResult<Dot> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `.`"))
            if (punct.asChar() != '.') {
                return@step SynResult.failure(cursor.error("expected `.`"))
            }
            SynResult.success(Dot.from(punct.span()) to rest)
        }
}

/** Peeks for the `=` punctuation token. */
public object EqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '='
    }

    override fun display(): String = "`=`"
}

/** Parses the `=` punctuation token. */
public object EqParse {
    fun parse(input: ParseStream): SynResult<Eq> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `=`"))
            if (punct.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `=`"))
            }
            SynResult.success(Eq.from(punct.span()) to rest)
        }
}

/** Peeks for the `>` punctuation token. */
public object GtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '>'
    }

    override fun display(): String = "`>`"
}

/** Parses the `>` punctuation token. */
public object GtParse {
    fun parse(input: ParseStream): SynResult<Gt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>`"))
            if (punct.asChar() != '>') {
                return@step SynResult.failure(cursor.error("expected `>`"))
            }
            SynResult.success(Gt.from(punct.span()) to rest)
        }
}

/** Peeks for the `<` punctuation token. */
public object LtPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '<'
    }

    override fun display(): String = "`<`"
}

/** Parses the `<` punctuation token. */
public object LtParse {
    fun parse(input: ParseStream): SynResult<Lt> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<`"))
            if (punct.asChar() != '<') {
                return@step SynResult.failure(cursor.error("expected `<`"))
            }
            SynResult.success(Lt.from(punct.span()) to rest)
        }
}

/** Peeks for the `-` punctuation token. */
public object MinusPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '-'
    }

    override fun display(): String = "`-`"
}

/** Parses the `-` punctuation token. */
public object MinusParse {
    fun parse(input: ParseStream): SynResult<Minus> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `-`"))
            if (punct.asChar() != '-') {
                return@step SynResult.failure(cursor.error("expected `-`"))
            }
            SynResult.success(Minus.from(punct.span()) to rest)
        }
}

/** Peeks for the `!` punctuation token. */
public object NotPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '!'
    }

    override fun display(): String = "`!`"
}

/** Parses the `!` punctuation token. */
public object NotParse {
    fun parse(input: ParseStream): SynResult<Not> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `!`"))
            if (punct.asChar() != '!') {
                return@step SynResult.failure(cursor.error("expected `!`"))
            }
            SynResult.success(Not.from(punct.span()) to rest)
        }
}

/** Peeks for the `|` punctuation token. */
public object OrPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '|'
    }

    override fun display(): String = "`|`"
}

/** Parses the `|` punctuation token. */
public object OrParse {
    fun parse(input: ParseStream): SynResult<Or> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `|`"))
            if (punct.asChar() != '|') {
                return@step SynResult.failure(cursor.error("expected `|`"))
            }
            SynResult.success(Or.from(punct.span()) to rest)
        }
}

/** Peeks for the `%` punctuation token. */
public object PercentPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '%'
    }

    override fun display(): String = "`%`"
}

/** Parses the `%` punctuation token. */
public object PercentParse {
    fun parse(input: ParseStream): SynResult<Percent> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `%`"))
            if (punct.asChar() != '%') {
                return@step SynResult.failure(cursor.error("expected `%`"))
            }
            SynResult.success(Percent.from(punct.span()) to rest)
        }
}

/** Peeks for the `+` punctuation token. */
public object PlusPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '+'
    }

    override fun display(): String = "`+`"
}

/** Parses the `+` punctuation token. */
public object PlusParse {
    fun parse(input: ParseStream): SynResult<Plus> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `+`"))
            if (punct.asChar() != '+') {
                return@step SynResult.failure(cursor.error("expected `+`"))
            }
            SynResult.success(Plus.from(punct.span()) to rest)
        }
}

/** Peeks for the `#` punctuation token. */
public object PoundPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '#'
    }

    override fun display(): String = "`#`"
}

/** Parses the `#` punctuation token. */
public object PoundParse {
    fun parse(input: ParseStream): SynResult<Pound> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `#`"))
            if (punct.asChar() != '#') {
                return@step SynResult.failure(cursor.error("expected `#`"))
            }
            SynResult.success(Pound.from(punct.span()) to rest)
        }
}

/** Peeks for the `?` punctuation token. */
public object QuestionPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '?'
    }

    override fun display(): String = "`?`"
}

/** Parses the `?` punctuation token. */
public object QuestionParse {
    fun parse(input: ParseStream): SynResult<Question> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `?`"))
            if (punct.asChar() != '?') {
                return@step SynResult.failure(cursor.error("expected `?`"))
            }
            SynResult.success(Question.from(punct.span()) to rest)
        }
}

/** Peeks for the `;` punctuation token. */
public object SemiPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == ';'
    }

    override fun display(): String = "`;`"
}

/** Parses the `;` punctuation token. */
public object SemiParse {
    fun parse(input: ParseStream): SynResult<Semi> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `;`"))
            if (punct.asChar() != ';') {
                return@step SynResult.failure(cursor.error("expected `;`"))
            }
            SynResult.success(Semi.from(punct.span()) to rest)
        }
}

/** Peeks for the `/` punctuation token. */
public object SlashPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '/'
    }

    override fun display(): String = "`/`"
}

/** Parses the `/` punctuation token. */
public object SlashParse {
    fun parse(input: ParseStream): SynResult<Slash> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `/`"))
            if (punct.asChar() != '/') {
                return@step SynResult.failure(cursor.error("expected `/`"))
            }
            SynResult.success(Slash.from(punct.span()) to rest)
        }
}

/** Peeks for the `*` punctuation token. */
public object StarPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '*'
    }

    override fun display(): String = "`*`"
}

/** Parses the `*` punctuation token. */
public object StarParse {
    fun parse(input: ParseStream): SynResult<Star> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `*`"))
            if (punct.asChar() != '*') {
                return@step SynResult.failure(cursor.error("expected `*`"))
            }
            SynResult.success(Star.from(punct.span()) to rest)
        }
}

/** Peeks for the `~` punctuation token. */
public object TildePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (punct, _) = cursor.punct() ?: return false
        return punct.asChar() == '~'
    }

    override fun display(): String = "`~`"
}

/** Parses the `~` punctuation token. */
public object TildeParse {
    fun parse(input: ParseStream): SynResult<Tilde> =
        input.step { cursor ->
            val (punct, rest) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `~`"))
            if (punct.asChar() != '~') {
                return@step SynResult.failure(cursor.error("expected `~`"))
            }
            SynResult.success(Tilde.from(punct.span()) to rest)
        }
}

/** Peeks for the `_` token. */
public object UnderscorePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (ident, _) = cursor.ident() ?: return false
        return ident.toString() == "_"
    }

    override fun display(): String = "`_`"
}

/** Parses the `_` token. */
public object UnderscoreParse {
    fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Underscore> =
        input.step { cursor ->
            val (ident, rest) =
                cursor.ident()
                    ?: return@step SynResult.failure(cursor.error("expected `_`"))
            if (ident.toString() != "_") {
                return@step SynResult.failure(cursor.error("expected `_`"))
            }
            SynResult.success(
                io.github.kotlinmania.syn.token.Underscore
                    .from(ident.span()) to rest,
            )
        }
}

// ── Multi-char Punctuation Peek / Parse (2-char) ─────────────────────────

/** Peeks for the `&&` punctuation token. */
public object AndAndPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '&' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '&'
    }

    override fun display(): String = "`&&`"
}

/** Parses the `&&` punctuation token. */
public object AndAndParse {
    fun parse(input: ParseStream): SynResult<AndAnd> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `&&`"))
            if (first.asChar() != '&' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `&&`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `&&`"))
            if (second.asChar() != '&') {
                return@step SynResult.failure(cursor.error("expected `&&`"))
            }
            SynResult.success(AndAnd.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `&=` punctuation token. */
public object AndEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '&' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`&=`"
}

/** Parses the `&=` punctuation token. */
public object AndEqParse {
    fun parse(input: ParseStream): SynResult<AndEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `&=`"))
            if (first.asChar() != '&' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `&=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `&=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `&=`"))
            }
            SynResult.success(AndEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `^=` punctuation token. */
public object CaretEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '^' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`^=`"
}

/** Parses the `^=` punctuation token. */
public object CaretEqParse {
    fun parse(input: ParseStream): SynResult<CaretEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `^=`"))
            if (first.asChar() != '^' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `^=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `^=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `^=`"))
            }
            SynResult.success(CaretEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `==` punctuation token. */
public object EqEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '=' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`==`"
}

/** Parses the `==` punctuation token. */
public object EqEqParse {
    fun parse(input: ParseStream): SynResult<EqEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `==`"))
            if (first.asChar() != '=' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `==`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `==`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `==`"))
            }
            SynResult.success(EqEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `=>` punctuation token. */
public object FatArrowPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '=' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '>'
    }

    override fun display(): String = "`=>`"
}

/** Parses the `=>` punctuation token. */
public object FatArrowParse {
    fun parse(input: ParseStream): SynResult<FatArrow> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `=>`"))
            if (first.asChar() != '=' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `=>`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `=>`"))
            if (second.asChar() != '>') {
                return@step SynResult.failure(cursor.error("expected `=>`"))
            }
            SynResult.success(FatArrow.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `>=` punctuation token. */
public object GePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '>' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`>=`"
}

/** Parses the `>=` punctuation token. */
public object GeParse {
    fun parse(input: ParseStream): SynResult<Ge> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>=`"))
            if (first.asChar() != '>' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `>=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `>=`"))
            }
            SynResult.success(Ge.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `<=` punctuation token. */
public object LePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`<=`"
}

/** Parses the `<=` punctuation token. */
public object LeParse {
    fun parse(input: ParseStream): SynResult<Le> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<=`"))
            if (first.asChar() != '<' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `<=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `<=`"))
            }
            SynResult.success(Le.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `<-` punctuation token. */
public object LArrowPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '-'
    }

    override fun display(): String = "`<-`"
}

/** Parses the `<-` punctuation token. */
public object LArrowParse {
    fun parse(input: ParseStream): SynResult<LArrow> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<-`"))
            if (first.asChar() != '<' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `<-`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<-`"))
            if (second.asChar() != '-') {
                return@step SynResult.failure(cursor.error("expected `<-`"))
            }
            SynResult.success(LArrow.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `-=` punctuation token. */
public object MinusEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '-' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`-=`"
}

/** Parses the `-=` punctuation token. */
public object MinusEqParse {
    fun parse(input: ParseStream): SynResult<MinusEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `-=`"))
            if (first.asChar() != '-' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `-=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `-=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `-=`"))
            }
            SynResult.success(MinusEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `!=` punctuation token. */
public object NePeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '!' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`!=`"
}

/** Parses the `!=` punctuation token. */
public object NeParse {
    fun parse(input: ParseStream): SynResult<Ne> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `!=`"))
            if (first.asChar() != '!' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `!=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `!=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `!=`"))
            }
            SynResult.success(Ne.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `|=` punctuation token. */
public object OrEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '|' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`|=`"
}

/** Parses the `|=` punctuation token. */
public object OrEqParse {
    fun parse(input: ParseStream): SynResult<OrEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `|=`"))
            if (first.asChar() != '|' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `|=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `|=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `|=`"))
            }
            SynResult.success(OrEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `||` punctuation token. */
public object OrOrPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '|' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '|'
    }

    override fun display(): String = "`||`"
}

/** Parses the `||` punctuation token. */
public object OrOrParse {
    fun parse(input: ParseStream): SynResult<OrOr> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `||`"))
            if (first.asChar() != '|' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `||`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `||`"))
            if (second.asChar() != '|') {
                return@step SynResult.failure(cursor.error("expected `||`"))
            }
            SynResult.success(OrOr.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `%=` punctuation token. */
public object PercentEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '%' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`%=`"
}

/** Parses the `%=` punctuation token. */
public object PercentEqParse {
    fun parse(input: ParseStream): SynResult<PercentEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `%=`"))
            if (first.asChar() != '%' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `%=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `%=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `%=`"))
            }
            SynResult.success(PercentEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `+=` punctuation token. */
public object PlusEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '+' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`+=`"
}

/** Parses the `+=` punctuation token. */
public object PlusEqParse {
    fun parse(input: ParseStream): SynResult<PlusEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `+=`"))
            if (first.asChar() != '+' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `+=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `+=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `+=`"))
            }
            SynResult.success(PlusEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `->` punctuation token. */
public object RArrowPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '-' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '>'
    }

    override fun display(): String = "`->`"
}

/** Parses the `->` punctuation token. */
public object RArrowParse {
    fun parse(input: ParseStream): SynResult<RArrow> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `->`"))
            if (first.asChar() != '-' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `->`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `->`"))
            if (second.asChar() != '>') {
                return@step SynResult.failure(cursor.error("expected `->`"))
            }
            SynResult.success(RArrow.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `/=` punctuation token. */
public object SlashEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '/' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`/=`"
}

/** Parses the `/=` punctuation token. */
public object SlashEqParse {
    fun parse(input: ParseStream): SynResult<SlashEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `/=`"))
            if (first.asChar() != '/' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `/=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `/=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `/=`"))
            }
            SynResult.success(SlashEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `*=` punctuation token. */
public object StarEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '*' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '='
    }

    override fun display(): String = "`*=`"
}

/** Parses the `*=` punctuation token. */
public object StarEqParse {
    fun parse(input: ParseStream): SynResult<StarEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `*=`"))
            if (first.asChar() != '*' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `*=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `*=`"))
            if (second.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `*=`"))
            }
            SynResult.success(StarEq.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `<<` punctuation token. */
public object ShlPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '<'
    }

    override fun display(): String = "`<<`"
}

/** Parses the `<<` punctuation token. */
public object ShlParse {
    fun parse(input: ParseStream): SynResult<Shl> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<<`"))
            if (first.asChar() != '<' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `<<`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<<`"))
            if (second.asChar() != '<') {
                return@step SynResult.failure(cursor.error("expected `<<`"))
            }
            SynResult.success(Shl.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `>>` punctuation token. */
public object ShrPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '>' || first.spacing() != Spacing.Joint) return false
        val second = rest1.punct()?.first ?: return false
        return second.asChar() == '>'
    }

    override fun display(): String = "`>>`"
}

/** Parses the `>>` punctuation token. */
public object ShrParse {
    fun parse(input: ParseStream): SynResult<Shr> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>>`"))
            if (first.asChar() != '>' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `>>`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>>`"))
            if (second.asChar() != '>') {
                return@step SynResult.failure(cursor.error("expected `>>`"))
            }
            SynResult.success(Shr.from(listOf(first.span(), second.span())) to rest2)
        }
}

/** Peeks for the `..` punctuation token. */
public object DotDotPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '.' || first.spacing() != Spacing.Joint) return false
        val (second, _) = rest1.punct() ?: return false
        return second.asChar() == '.'
    }

    override fun display(): String = "`..`"
}

/** Parses the `..` punctuation token. */
public object DotDotParse {
    fun parse(input: ParseStream): SynResult<DotDot> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `..`"))
            if (first.asChar() != '.' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `..`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `..`"))
            if (second.asChar() != '.') {
                return@step SynResult.failure(cursor.error("expected `..`"))
            }
            SynResult.success(DotDot.from(listOf(first.span(), second.span())) to rest2)
        }
}

// ── Multi-char Punctuation Peek / Parse (3-char) ─────────────────────────

/** Peeks for the `...` punctuation token. */
public object DotDotDotPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '.' || first.spacing() != Spacing.Joint) return false
        val (second, rest2) = rest1.punct() ?: return false
        if (second.asChar() != '.' || second.spacing() != Spacing.Joint) return false
        val (third, _) = rest2.punct() ?: return false
        return third.asChar() == '.'
    }

    override fun display(): String = "`...`"
}

/** Parses the `...` punctuation token. */
public object DotDotDotParse {
    fun parse(input: ParseStream): SynResult<DotDotDot> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `...`"))
            if (first.asChar() != '.' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `...`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `...`"))
            if (second.asChar() != '.' || second.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `...`"))
            }
            val (third, rest3) =
                rest2.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `...`"))
            if (third.asChar() != '.') {
                return@step SynResult.failure(cursor.error("expected `...`"))
            }
            SynResult.success(DotDotDot.from(listOf(first.span(), second.span(), third.span())) to rest3)
        }
}

/** Peeks for the `..=` punctuation token. */
public object DotDotEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '.' || first.spacing() != Spacing.Joint) return false
        val (second, rest2) = rest1.punct() ?: return false
        if (second.asChar() != '.' || second.spacing() != Spacing.Joint) return false
        val third = rest2.punct()?.first ?: return false
        return third.asChar() == '='
    }

    override fun display(): String = "`..=`"
}

/** Parses the `..=` punctuation token. */
public object DotDotEqParse {
    fun parse(input: ParseStream): SynResult<DotDotEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `..=`"))
            if (first.asChar() != '.' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `..=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `..=`"))
            if (second.asChar() != '.' || second.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `..=`"))
            }
            val (third, rest3) =
                rest2.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `..=`"))
            if (third.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `..=`"))
            }
            SynResult.success(DotDotEq.from(listOf(first.span(), second.span(), third.span())) to rest3)
        }
}

/** Peeks for the `<<=` punctuation token. */
public object ShlEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
        val (second, rest2) = rest1.punct() ?: return false
        if (second.asChar() != '<' || second.spacing() != Spacing.Joint) return false
        val third = rest2.punct()?.first ?: return false
        return third.asChar() == '='
    }

    override fun display(): String = "`<<=`"
}

/** Parses the `<<=` punctuation token. */
public object ShlEqParse {
    fun parse(input: ParseStream): SynResult<ShlEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<<=`"))
            if (first.asChar() != '<' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `<<=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<<=`"))
            if (second.asChar() != '<' || second.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `<<=`"))
            }
            val (third, rest3) =
                rest2.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `<<=`"))
            if (third.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `<<=`"))
            }
            SynResult.success(ShlEq.from(listOf(first.span(), second.span(), third.span())) to rest3)
        }
}

/** Peeks for the `>>=` punctuation token. */
public object ShrEqPeek : Peek {
    override fun peek(cursor: Cursor): Boolean {
        val (first, rest1) = cursor.punct() ?: return false
        if (first.asChar() != '>' || first.spacing() != Spacing.Joint) return false
        val (second, rest2) = rest1.punct() ?: return false
        if (second.asChar() != '>' || second.spacing() != Spacing.Joint) return false
        val third = rest2.punct()?.first ?: return false
        return third.asChar() == '='
    }

    override fun display(): String = "`>>=`"
}

/** Parses the `>>=` punctuation token. */
public object ShrEqParse {
    fun parse(input: ParseStream): SynResult<ShrEq> =
        input.step { cursor ->
            val (first, rest1) =
                cursor.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>>=`"))
            if (first.asChar() != '>' || first.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `>>=`"))
            }
            val (second, rest2) =
                rest1.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>>=`"))
            if (second.asChar() != '>' || second.spacing() != Spacing.Joint) {
                return@step SynResult.failure(cursor.error("expected `>>=`"))
            }
            val (third, rest3) =
                rest2.punct()
                    ?: return@step SynResult.failure(cursor.error("expected `>>=`"))
            if (third.asChar() != '=') {
                return@step SynResult.failure(cursor.error("expected `>>=`"))
            }
            SynResult.success(ShrEq.from(listOf(first.span(), second.span(), third.span())) to rest3)
        }
}

internal fun keyword(input: ParseStream, token: String): SynResult<io.github.kotlinmania.procmacro2.Span> =
    input.step { cursor ->
        val pair = cursor.ident()
        if (pair != null) {
            val (ident, rest) = pair
            if (ident.toString() == token) {
                return@step SynResult.success(ident.span() to rest)
            }
        }
        SynResult.failure(cursor.error("expected `$token`"))
    }

internal fun peekKeyword(cursor: Cursor, token: String): Boolean {
    val pair = cursor.ident()
    if (pair == null) return false
    val (ident, _) = pair
    return ident.toString() == token
}

internal fun punct(input: ParseStream, token: String, count: Int): SynResult<List<io.github.kotlinmania.procmacro2.Span>> {
    val spans = MutableList(count) { input.span() }
    val helperResult = punctHelper(input, token, spans)
    if (helperResult.isFailure) return SynResult.failure((helperResult as SynResult.Failure).error)
    return SynResult.success(spans)
}

internal fun punctHelper(input: ParseStream, token: String, spans: MutableList<io.github.kotlinmania.procmacro2.Span>): SynResult<Unit> {
    return input.step { cursor ->
        var c = cursor.raw
        val chars = token.toList()
        for ((i, ch) in chars.withIndex()) {
            val punctPair = c.punct()
            if (punctPair == null) break
            val (punct, rest) = punctPair
            spans[i] = punct.span()
            if (punct.asChar() != ch) {
                break
            } else if (i == chars.size - 1) {
                return@step SynResult.success(Unit to rest)
            } else if (punct.spacing() != Spacing.Joint) {
                break
            }
            c = rest
        }
        SynResult.failure(cursor.error("expected `$token`"))
    }
}

internal fun peekPunct(cursor: Cursor, token: String): Boolean {
    var c = cursor
    val chars = token.toList()
    for ((i, ch) in chars.withIndex()) {
        val punctPair = c.punct()
        if (punctPair == null) break
        val (punct, rest) = punctPair
        if (punct.asChar() != ch) {
            break
        } else if (i == chars.size - 1) {
            return true
        } else if (punct.spacing() != Spacing.Joint) {
            break
        }
        c = rest
    }
    return false
}

internal fun delim(
    delim: io.github.kotlinmania.procmacro2.Delimiter,
    span: io.github.kotlinmania.procmacro2.Span,
    tokens: io.github.kotlinmania.procmacro2.TokenStream,
    inner: io.github.kotlinmania.procmacro2.TokenStream,
) {
    val group =
        io.github.kotlinmania.procmacro2
            .Group(delim, inner)
    group.setSpan(span)
    tokens.extendTokenTrees(
        listOf(
            io.github.kotlinmania.procmacro2.TokenTree
                .Group(group),
        ),
    )
}

internal fun printKeyword(s: String, span: io.github.kotlinmania.procmacro2.Span, tokens: io.github.kotlinmania.procmacro2.TokenStream) {
    tokens.extendIdents(
        listOf(
            io.github.kotlinmania.procmacro2.Ident
                .new(s, span),
        ),
    )
}

internal fun printPunct(s: String, spans: List<io.github.kotlinmania.procmacro2.Span>, tokens: io.github.kotlinmania.procmacro2.TokenStream) {
    val chars = s.toList()
    val puncts = mutableListOf<io.github.kotlinmania.procmacro2.Punct>()
    for (i in 0 until chars.size - 1) {
        puncts.add(
            io.github.kotlinmania.procmacro2
                .Punct(chars[i], Spacing.Joint, spans[i]),
        )
    }
    puncts.add(
        io.github.kotlinmania.procmacro2
            .Punct(chars.last(), Spacing.Alone, spans.last()),
    )
    tokens.extendPuncts(puncts)
}
