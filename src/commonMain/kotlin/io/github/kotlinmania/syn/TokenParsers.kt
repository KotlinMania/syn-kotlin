// port-lint: source token.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Delimiter
import io.github.kotlinmania.procmacro2.Punct
import io.github.kotlinmania.procmacro2.Spacing
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.procmacro2.TokenTree
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
import io.github.kotlinmania.syn.token.Brace
import io.github.kotlinmania.syn.token.Bracket
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
import io.github.kotlinmania.syn.token.In
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
import io.github.kotlinmania.syn.token.Paren
import io.github.kotlinmania.syn.token.Percent
import io.github.kotlinmania.syn.token.PercentEq
import io.github.kotlinmania.syn.token.Plus
import io.github.kotlinmania.syn.token.PlusEq
import io.github.kotlinmania.syn.token.Pound
import io.github.kotlinmania.syn.token.Priv
import io.github.kotlinmania.syn.token.Pub
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
import io.github.kotlinmania.syn.token.Token
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
import kotlin.native.HiddenFromObjC

// ── Keyword Peek / Parse ──────────────────────────────────────────────────

/** Peeks for the `abstract` keyword. */
@HiddenFromObjC
public object AbstractPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "abstract"
 }
 override fun display(): String = "`abstract`"
}

/** Parses the `abstract` keyword. */
@HiddenFromObjC
public object AbstractParse : Parse<Abstract> {
 override fun parse(input: ParseStream): SynResult<Abstract> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `abstract`"))
   if (ident.toString() != "abstract")
    return@step SynResult.failure(cursor.error("expected `abstract`"))
   SynResult.success(Abstract.from(ident.span()) to rest)
  }
}
/** Peeks for the `as` keyword. */
@HiddenFromObjC
public object AsPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "as"
 }
 override fun display(): String = "`as`"
}

/** Parses the `as` keyword. */
@HiddenFromObjC
public object AsParse : Parse<As> {
 override fun parse(input: ParseStream): SynResult<As> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `as`"))
   if (ident.toString() != "as")
    return@step SynResult.failure(cursor.error("expected `as`"))
   SynResult.success(As.from(ident.span()) to rest)
  }
}
/** Peeks for the `async` keyword. */
@HiddenFromObjC
public object AsyncPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "async"
 }
 override fun display(): String = "`async`"
}

/** Parses the `async` keyword. */
@HiddenFromObjC
public object AsyncParse : Parse<Async> {
 override fun parse(input: ParseStream): SynResult<Async> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `async`"))
   if (ident.toString() != "async")
    return@step SynResult.failure(cursor.error("expected `async`"))
   SynResult.success(Async.from(ident.span()) to rest)
  }
}
/** Peeks for the `auto` keyword. */
@HiddenFromObjC
public object AutoPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "auto"
 }
 override fun display(): String = "`auto`"
}

/** Parses the `auto` keyword. */
@HiddenFromObjC
public object AutoParse : Parse<Auto> {
 override fun parse(input: ParseStream): SynResult<Auto> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `auto`"))
   if (ident.toString() != "auto")
    return@step SynResult.failure(cursor.error("expected `auto`"))
   SynResult.success(Auto.from(ident.span()) to rest)
  }
}
/** Peeks for the `await` keyword. */
@HiddenFromObjC
public object AwaitPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "await"
 }
 override fun display(): String = "`await`"
}

/** Parses the `await` keyword. */
@HiddenFromObjC
public object AwaitParse : Parse<Await> {
 override fun parse(input: ParseStream): SynResult<Await> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `await`"))
   if (ident.toString() != "await")
    return@step SynResult.failure(cursor.error("expected `await`"))
   SynResult.success(Await.from(ident.span()) to rest)
  }
}
/** Peeks for the `become` keyword. */
@HiddenFromObjC
public object BecomePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "become"
 }
 override fun display(): String = "`become`"
}

/** Parses the `become` keyword. */
@HiddenFromObjC
public object BecomeParse : Parse<Become> {
 override fun parse(input: ParseStream): SynResult<Become> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `become`"))
   if (ident.toString() != "become")
    return@step SynResult.failure(cursor.error("expected `become`"))
   SynResult.success(Become.from(ident.span()) to rest)
  }
}
/** Peeks for the `box` keyword. */
@HiddenFromObjC
public object BoxPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "box"
 }
 override fun display(): String = "`box`"
}

/** Parses the `box` keyword. */
@HiddenFromObjC
public object BoxParse : Parse<Box> {
 override fun parse(input: ParseStream): SynResult<Box> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `box`"))
   if (ident.toString() != "box")
    return@step SynResult.failure(cursor.error("expected `box`"))
   SynResult.success(Box.from(ident.span()) to rest)
  }
}
/** Peeks for the `break` keyword. */
@HiddenFromObjC
public object BreakPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "break"
 }
 override fun display(): String = "`break`"
}

/** Parses the `break` keyword. */
@HiddenFromObjC
public object BreakParse : Parse<Break> {
 override fun parse(input: ParseStream): SynResult<Break> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `break`"))
   if (ident.toString() != "break")
    return@step SynResult.failure(cursor.error("expected `break`"))
   SynResult.success(Break.from(ident.span()) to rest)
  }
}
/** Peeks for the `const` keyword. */
@HiddenFromObjC
public object ConstPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "const"
 }
 override fun display(): String = "`const`"
}

/** Parses the `const` keyword. */
@HiddenFromObjC
public object ConstParse : Parse<Const> {
 override fun parse(input: ParseStream): SynResult<Const> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `const`"))
   if (ident.toString() != "const")
    return@step SynResult.failure(cursor.error("expected `const`"))
   SynResult.success(Const.from(ident.span()) to rest)
  }
}
/** Peeks for the `continue` keyword. */
@HiddenFromObjC
public object ContinuePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "continue"
 }
 override fun display(): String = "`continue`"
}

/** Parses the `continue` keyword. */
@HiddenFromObjC
public object ContinueParse : Parse<Continue> {
 override fun parse(input: ParseStream): SynResult<Continue> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `continue`"))
   if (ident.toString() != "continue")
    return@step SynResult.failure(cursor.error("expected `continue`"))
   SynResult.success(Continue.from(ident.span()) to rest)
  }
}
/** Peeks for the `crate` keyword. */
@HiddenFromObjC
public object CratePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "crate"
 }
 override fun display(): String = "`crate`"
}

/** Parses the `crate` keyword. */
@HiddenFromObjC
public object CrateParse : Parse<Crate> {
 override fun parse(input: ParseStream): SynResult<Crate> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `crate`"))
   if (ident.toString() != "crate")
    return@step SynResult.failure(cursor.error("expected `crate`"))
   SynResult.success(Crate.from(ident.span()) to rest)
  }
}
/** Peeks for the `default` keyword. */
@HiddenFromObjC
public object DefaultPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "default"
 }
 override fun display(): String = "`default`"
}

/** Parses the `default` keyword. */
@HiddenFromObjC
public object DefaultParse : Parse<Default> {
 override fun parse(input: ParseStream): SynResult<Default> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `default`"))
   if (ident.toString() != "default")
    return@step SynResult.failure(cursor.error("expected `default`"))
   SynResult.success(Default.from(ident.span()) to rest)
  }
}
/** Peeks for the `do` keyword. */
@HiddenFromObjC
public object DoPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "do"
 }
 override fun display(): String = "`do`"
}

/** Parses the `do` keyword. */
@HiddenFromObjC
public object DoParse : Parse<Do> {
 override fun parse(input: ParseStream): SynResult<Do> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `do`"))
   if (ident.toString() != "do")
    return@step SynResult.failure(cursor.error("expected `do`"))
   SynResult.success(Do.from(ident.span()) to rest)
  }
}
/** Peeks for the `dyn` keyword. */
@HiddenFromObjC
public object DynPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "dyn"
 }
 override fun display(): String = "`dyn`"
}

/** Parses the `dyn` keyword. */
@HiddenFromObjC
public object DynParse : Parse<Dyn> {
 override fun parse(input: ParseStream): SynResult<Dyn> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `dyn`"))
   if (ident.toString() != "dyn")
    return@step SynResult.failure(cursor.error("expected `dyn`"))
   SynResult.success(Dyn.from(ident.span()) to rest)
  }
}
/** Peeks for the `else` keyword. */
@HiddenFromObjC
public object ElsePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "else"
 }
 override fun display(): String = "`else`"
}

/** Parses the `else` keyword. */
@HiddenFromObjC
public object ElseParse : Parse<Else> {
 override fun parse(input: ParseStream): SynResult<Else> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `else`"))
   if (ident.toString() != "else")
    return@step SynResult.failure(cursor.error("expected `else`"))
   SynResult.success(Else.from(ident.span()) to rest)
  }
}
/** Peeks for the `enum` keyword. */
@HiddenFromObjC
public object EnumPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "enum"
 }
 override fun display(): String = "`enum`"
}

/** Parses the `enum` keyword. */
@HiddenFromObjC
public object EnumParse : Parse<Enum> {
 override fun parse(input: ParseStream): SynResult<Enum> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `enum`"))
   if (ident.toString() != "enum")
    return@step SynResult.failure(cursor.error("expected `enum`"))
   SynResult.success(Enum.from(ident.span()) to rest)
  }
}
/** Peeks for the `extern` keyword. */
@HiddenFromObjC
public object ExternPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "extern"
 }
 override fun display(): String = "`extern`"
}

/** Parses the `extern` keyword. */
@HiddenFromObjC
public object ExternParse : Parse<Extern> {
 override fun parse(input: ParseStream): SynResult<Extern> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `extern`"))
   if (ident.toString() != "extern")
    return@step SynResult.failure(cursor.error("expected `extern`"))
   SynResult.success(Extern.from(ident.span()) to rest)
  }
}
/** Peeks for the `final` keyword. */
@HiddenFromObjC
public object FinalPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "final"
 }
 override fun display(): String = "`final`"
}

/** Parses the `final` keyword. */
@HiddenFromObjC
public object FinalParse : Parse<Final> {
 override fun parse(input: ParseStream): SynResult<Final> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `final`"))
   if (ident.toString() != "final")
    return@step SynResult.failure(cursor.error("expected `final`"))
   SynResult.success(Final.from(ident.span()) to rest)
  }
}
/** Peeks for the `fn` keyword. */
@HiddenFromObjC
public object FnPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "fn"
 }
 override fun display(): String = "`fn`"
}

/** Parses the `fn` keyword. */
@HiddenFromObjC
public object FnParse : Parse<Fn> {
 override fun parse(input: ParseStream): SynResult<Fn> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `fn`"))
   if (ident.toString() != "fn")
    return@step SynResult.failure(cursor.error("expected `fn`"))
   SynResult.success(Fn.from(ident.span()) to rest)
  }
}
/** Peeks for the `for` keyword. */
@HiddenFromObjC
public object ForPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "for"
 }
 override fun display(): String = "`for`"
}

/** Parses the `for` keyword. */
@HiddenFromObjC
public object ForParse : Parse<For> {
 override fun parse(input: ParseStream): SynResult<For> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `for`"))
   if (ident.toString() != "for")
    return@step SynResult.failure(cursor.error("expected `for`"))
   SynResult.success(For.from(ident.span()) to rest)
  }
}
/** Peeks for the `if` keyword. */
@HiddenFromObjC
public object IfPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "if"
 }
 override fun display(): String = "`if`"
}

/** Parses the `if` keyword. */
@HiddenFromObjC
public object IfParse : Parse<If> {
 override fun parse(input: ParseStream): SynResult<If> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `if`"))
   if (ident.toString() != "if")
    return@step SynResult.failure(cursor.error("expected `if`"))
   SynResult.success(If.from(ident.span()) to rest)
  }
}
/** Peeks for the `impl` keyword. */
@HiddenFromObjC
public object ImplPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "impl"
 }
 override fun display(): String = "`impl`"
}

/** Parses the `impl` keyword. */
@HiddenFromObjC
public object ImplParse : Parse<Impl> {
 override fun parse(input: ParseStream): SynResult<Impl> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `impl`"))
   if (ident.toString() != "impl")
    return@step SynResult.failure(cursor.error("expected `impl`"))
   SynResult.success(Impl.from(ident.span()) to rest)
  }
}
/** Peeks for the `let` keyword. */
@HiddenFromObjC
public object LetPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "let"
 }
 override fun display(): String = "`let`"
}

/** Parses the `let` keyword. */
@HiddenFromObjC
public object LetParse : Parse<Let> {
 override fun parse(input: ParseStream): SynResult<Let> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `let`"))
   if (ident.toString() != "let")
    return@step SynResult.failure(cursor.error("expected `let`"))
   SynResult.success(Let.from(ident.span()) to rest)
  }
}
/** Peeks for the `loop` keyword. */
@HiddenFromObjC
public object LoopPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "loop"
 }
 override fun display(): String = "`loop`"
}

/** Parses the `loop` keyword. */
@HiddenFromObjC
public object LoopParse : Parse<Loop> {
 override fun parse(input: ParseStream): SynResult<Loop> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `loop`"))
   if (ident.toString() != "loop")
    return@step SynResult.failure(cursor.error("expected `loop`"))
   SynResult.success(Loop.from(ident.span()) to rest)
  }
}
/** Peeks for the `macro` keyword. */
@HiddenFromObjC
public object MacroPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "macro"
 }
 override fun display(): String = "`macro`"
}

/** Parses the `macro` keyword. */
@HiddenFromObjC
public object MacroParse : Parse<Macro> {
 override fun parse(input: ParseStream): SynResult<Macro> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `macro`"))
   if (ident.toString() != "macro")
    return@step SynResult.failure(cursor.error("expected `macro`"))
   SynResult.success(Macro.from(ident.span()) to rest)
  }
}
/** Peeks for the `match` keyword. */
@HiddenFromObjC
public object MatchPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "match"
 }
 override fun display(): String = "`match`"
}

/** Parses the `match` keyword. */
@HiddenFromObjC
public object MatchParse : Parse<Match> {
 override fun parse(input: ParseStream): SynResult<Match> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `match`"))
   if (ident.toString() != "match")
    return@step SynResult.failure(cursor.error("expected `match`"))
   SynResult.success(Match.from(ident.span()) to rest)
  }
}
/** Peeks for the `mod` keyword. */
@HiddenFromObjC
public object ModPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "mod"
 }
 override fun display(): String = "`mod`"
}

/** Parses the `mod` keyword. */
@HiddenFromObjC
public object ModParse : Parse<Mod> {
 override fun parse(input: ParseStream): SynResult<Mod> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `mod`"))
   if (ident.toString() != "mod")
    return@step SynResult.failure(cursor.error("expected `mod`"))
   SynResult.success(Mod.from(ident.span()) to rest)
  }
}
/** Peeks for the `move` keyword. */
@HiddenFromObjC
public object MovePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "move"
 }
 override fun display(): String = "`move`"
}

/** Parses the `move` keyword. */
@HiddenFromObjC
public object MoveParse : Parse<Move> {
 override fun parse(input: ParseStream): SynResult<Move> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `move`"))
   if (ident.toString() != "move")
    return@step SynResult.failure(cursor.error("expected `move`"))
   SynResult.success(Move.from(ident.span()) to rest)
  }
}
/** Peeks for the `mut` keyword. */
@HiddenFromObjC
public object MutPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "mut"
 }
 override fun display(): String = "`mut`"
}

/** Parses the `mut` keyword. */
@HiddenFromObjC
public object MutParse : Parse<Mut> {
 override fun parse(input: ParseStream): SynResult<Mut> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `mut`"))
   if (ident.toString() != "mut")
    return@step SynResult.failure(cursor.error("expected `mut`"))
   SynResult.success(Mut.from(ident.span()) to rest)
  }
}
/** Peeks for the `override` keyword. */
@HiddenFromObjC
public object OverridePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "override"
 }
 override fun display(): String = "`override`"
}

/** Parses the `override` keyword. */
@HiddenFromObjC
public object OverrideParse : Parse<Override> {
 override fun parse(input: ParseStream): SynResult<Override> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `override`"))
   if (ident.toString() != "override")
    return@step SynResult.failure(cursor.error("expected `override`"))
   SynResult.success(Override.from(ident.span()) to rest)
  }
}
/** Peeks for the `priv` keyword. */
@HiddenFromObjC
public object PrivPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "priv"
 }
 override fun display(): String = "`priv`"
}

/** Parses the `priv` keyword. */
@HiddenFromObjC
public object PrivParse : Parse<Priv> {
 override fun parse(input: ParseStream): SynResult<Priv> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `priv`"))
   if (ident.toString() != "priv")
    return@step SynResult.failure(cursor.error("expected `priv`"))
   SynResult.success(Priv.from(ident.span()) to rest)
  }
}
/** Peeks for the `raw` keyword. */
@HiddenFromObjC
public object RawPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "raw"
 }
 override fun display(): String = "`raw`"
}

/** Parses the `raw` keyword. */
@HiddenFromObjC
public object RawParse : Parse<Raw> {
 override fun parse(input: ParseStream): SynResult<Raw> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `raw`"))
   if (ident.toString() != "raw")
    return@step SynResult.failure(cursor.error("expected `raw`"))
   SynResult.success(Raw.from(ident.span()) to rest)
  }
}
/** Peeks for the `ref` keyword. */
@HiddenFromObjC
public object RefPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "ref"
 }
 override fun display(): String = "`ref`"
}

/** Parses the `ref` keyword. */
@HiddenFromObjC
public object RefParse : Parse<Ref> {
 override fun parse(input: ParseStream): SynResult<Ref> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `ref`"))
   if (ident.toString() != "ref")
    return@step SynResult.failure(cursor.error("expected `ref`"))
   SynResult.success(Ref.from(ident.span()) to rest)
  }
}
/** Peeks for the `return` keyword. */
@HiddenFromObjC
public object ReturnPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "return"
 }
 override fun display(): String = "`return`"
}

/** Parses the `return` keyword. */
@HiddenFromObjC
public object ReturnParse : Parse<Return> {
 override fun parse(input: ParseStream): SynResult<Return> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `return`"))
   if (ident.toString() != "return")
    return@step SynResult.failure(cursor.error("expected `return`"))
   SynResult.success(Return.from(ident.span()) to rest)
  }
}
/** Peeks for the `static` keyword. */
@HiddenFromObjC
public object StaticPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "static"
 }
 override fun display(): String = "`static`"
}

/** Parses the `static` keyword. */
@HiddenFromObjC
public object StaticParse : Parse<Static> {
 override fun parse(input: ParseStream): SynResult<Static> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `static`"))
   if (ident.toString() != "static")
    return@step SynResult.failure(cursor.error("expected `static`"))
   SynResult.success(Static.from(ident.span()) to rest)
  }
}
/** Peeks for the `struct` keyword. */
@HiddenFromObjC
public object StructPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "struct"
 }
 override fun display(): String = "`struct`"
}

/** Parses the `struct` keyword. */
@HiddenFromObjC
public object StructParse : Parse<Struct> {
 override fun parse(input: ParseStream): SynResult<Struct> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `struct`"))
   if (ident.toString() != "struct")
    return@step SynResult.failure(cursor.error("expected `struct`"))
   SynResult.success(Struct.from(ident.span()) to rest)
  }
}
/** Peeks for the `super` keyword. */
@HiddenFromObjC
public object SuperPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "super"
 }
 override fun display(): String = "`super`"
}

/** Parses the `super` keyword. */
@HiddenFromObjC
public object SuperParse : Parse<Super> {
 override fun parse(input: ParseStream): SynResult<Super> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `super`"))
   if (ident.toString() != "super")
    return@step SynResult.failure(cursor.error("expected `super`"))
   SynResult.success(Super.from(ident.span()) to rest)
  }
}
/** Peeks for the `trait` keyword. */
@HiddenFromObjC
public object TraitPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "trait"
 }
 override fun display(): String = "`trait`"
}

/** Parses the `trait` keyword. */
@HiddenFromObjC
public object TraitParse : Parse<io.github.kotlinmania.syn.token.Trait> {
 override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Trait> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `trait`"))
   if (ident.toString() != "trait")
    return@step SynResult.failure(cursor.error("expected `trait`"))
   SynResult.success(io.github.kotlinmania.syn.token.Trait.from(ident.span()) to rest)
  }
}
/** Peeks for the `try` keyword. */
@HiddenFromObjC
public object TryPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "try"
 }
 override fun display(): String = "`try`"
}

/** Parses the `try` keyword. */
@HiddenFromObjC
public object TryParse : Parse<Try> {
 override fun parse(input: ParseStream): SynResult<Try> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `try`"))
   if (ident.toString() != "try")
    return@step SynResult.failure(cursor.error("expected `try`"))
   SynResult.success(Try.from(ident.span()) to rest)
  }
}
/** Peeks for the `union` keyword. */
@HiddenFromObjC
public object UnionPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "union"
 }
 override fun display(): String = "`union`"
}

/** Parses the `union` keyword. */
@HiddenFromObjC
public object UnionParse : Parse<Union> {
 override fun parse(input: ParseStream): SynResult<Union> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `union`"))
   if (ident.toString() != "union")
    return@step SynResult.failure(cursor.error("expected `union`"))
   SynResult.success(Union.from(ident.span()) to rest)
  }
}
/** Peeks for the `unsafe` keyword. */
@HiddenFromObjC
public object UnsafePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "unsafe"
 }
 override fun display(): String = "`unsafe`"
}

/** Parses the `unsafe` keyword. */
@HiddenFromObjC
public object UnsafeParse : Parse<Unsafe> {
 override fun parse(input: ParseStream): SynResult<Unsafe> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `unsafe`"))
   if (ident.toString() != "unsafe")
    return@step SynResult.failure(cursor.error("expected `unsafe`"))
   SynResult.success(Unsafe.from(ident.span()) to rest)
  }
}
/** Peeks for the `unsized` keyword. */
@HiddenFromObjC
public object UnsizedPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "unsized"
 }
 override fun display(): String = "`unsized`"
}

/** Parses the `unsized` keyword. */
@HiddenFromObjC
public object UnsizedParse : Parse<Unsized> {
 override fun parse(input: ParseStream): SynResult<Unsized> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `unsized`"))
   if (ident.toString() != "unsized")
    return@step SynResult.failure(cursor.error("expected `unsized`"))
   SynResult.success(Unsized.from(ident.span()) to rest)
  }
}
/** Peeks for the `use` keyword. */
@HiddenFromObjC
public object UsePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "use"
 }
 override fun display(): String = "`use`"
}

/** Parses the `use` keyword. */
@HiddenFromObjC
public object UseParse : Parse<Use> {
 override fun parse(input: ParseStream): SynResult<Use> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `use`"))
   if (ident.toString() != "use")
    return@step SynResult.failure(cursor.error("expected `use`"))
   SynResult.success(Use.from(ident.span()) to rest)
  }
}
/** Peeks for the `virtual` keyword. */
@HiddenFromObjC
public object VirtualPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "virtual"
 }
 override fun display(): String = "`virtual`"
}

/** Parses the `virtual` keyword. */
@HiddenFromObjC
public object VirtualParse : Parse<Virtual> {
 override fun parse(input: ParseStream): SynResult<Virtual> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `virtual`"))
   if (ident.toString() != "virtual")
    return@step SynResult.failure(cursor.error("expected `virtual`"))
   SynResult.success(Virtual.from(ident.span()) to rest)
  }
}
/** Peeks for the `where` keyword. */
@HiddenFromObjC
public object WherePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "where"
 }
 override fun display(): String = "`where`"
}

/** Parses the `where` keyword. */
@HiddenFromObjC
public object WhereParse : Parse<Where> {
 override fun parse(input: ParseStream): SynResult<Where> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `where`"))
   if (ident.toString() != "where")
    return@step SynResult.failure(cursor.error("expected `where`"))
   SynResult.success(Where.from(ident.span()) to rest)
  }
}
/** Peeks for the `while` keyword. */
@HiddenFromObjC
public object WhilePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "while"
 }
 override fun display(): String = "`while`"
}

/** Parses the `while` keyword. */
@HiddenFromObjC
public object WhileParse : Parse<While> {
 override fun parse(input: ParseStream): SynResult<While> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `while`"))
   if (ident.toString() != "while")
    return@step SynResult.failure(cursor.error("expected `while`"))
   SynResult.success(While.from(ident.span()) to rest)
  }
}
/** Peeks for the `yield` keyword. */
@HiddenFromObjC
public object YieldPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "yield"
 }
 override fun display(): String = "`yield`"
}

/** Parses the `yield` keyword. */
@HiddenFromObjC
public object YieldParse : Parse<Yield> {
 override fun parse(input: ParseStream): SynResult<Yield> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `yield`"))
   if (ident.toString() != "yield")
    return@step SynResult.failure(cursor.error("expected `yield`"))
   SynResult.success(Yield.from(ident.span()) to rest)
  }
}
/** Peeks for the `Self` keyword. */
@HiddenFromObjC
public object SelfTypePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "Self"
 }
 override fun display(): String = "`Self`"
}

/** Parses the `Self` keyword. */
@HiddenFromObjC
public object SelfTypeParse : Parse<io.github.kotlinmania.syn.token.SelfType> {
 override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.SelfType> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `Self`"))
   if (ident.toString() != "Self")
    return@step SynResult.failure(cursor.error("expected `Self`"))
   SynResult.success(io.github.kotlinmania.syn.token.SelfType.from(ident.span()) to rest)
  }
}
/** Peeks for the `self` keyword. */
@HiddenFromObjC
public object SelfValuePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "self"
 }
 override fun display(): String = "`self`"
}

/** Parses the `self` keyword. */
@HiddenFromObjC
public object SelfValueParse : Parse<io.github.kotlinmania.syn.token.SelfValue> {
 override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.SelfValue> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `self`"))
   if (ident.toString() != "self")
    return@step SynResult.failure(cursor.error("expected `self`"))
   SynResult.success(io.github.kotlinmania.syn.token.SelfValue.from(ident.span()) to rest)
  }
}
/** Peeks for the `type` keyword. */
@HiddenFromObjC
public object SynTypePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "type"
 }
 override fun display(): String = "`type`"
}

/** Parses the `type` keyword. */
@HiddenFromObjC
public object SynTypeParse : Parse<io.github.kotlinmania.syn.token.SynTypeToken> {
 override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.SynTypeToken> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `type`"))
   if (ident.toString() != "type")
    return@step SynResult.failure(cursor.error("expected `type`"))
   SynResult.success(io.github.kotlinmania.syn.token.SynTypeToken.from(ident.span()) to rest)
  }
}
/** Peeks for the `typeof` keyword. */
@HiddenFromObjC
public object TypeofPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "typeof"
 }
 override fun display(): String = "`typeof`"
}

/** Parses the `typeof` keyword. */
@HiddenFromObjC
public object TypeofParse : Parse<io.github.kotlinmania.syn.token.Typeof> {
 override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Typeof> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `typeof`"))
   if (ident.toString() != "typeof")
    return@step SynResult.failure(cursor.error("expected `typeof`"))
   SynResult.success(io.github.kotlinmania.syn.token.Typeof.from(ident.span()) to rest)
  }
}

// ── Punctuation Peek / Parse (single-char) ───────────────────────────────

/** Peeks for the `&` punctuation token. */
@HiddenFromObjC
public object AndPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '&' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`&`"
}

/** Parses the `&` punctuation token. */
@HiddenFromObjC
public object AndParse : Parse<And> {
 override fun parse(input: ParseStream): SynResult<And> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `&`"))
   if (punct.asChar() != '&' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `&`"))
   SynResult.success(And.from(punct.span()) to rest)
  }
}
/** Peeks for the `@` punctuation token. */
@HiddenFromObjC
public object AtPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '@' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`@`"
}

/** Parses the `@` punctuation token. */
@HiddenFromObjC
public object AtParse : Parse<At> {
 override fun parse(input: ParseStream): SynResult<At> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `@`"))
   if (punct.asChar() != '@' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `@`"))
   SynResult.success(At.from(punct.span()) to rest)
  }
}
/** Peeks for the `^` punctuation token. */
@HiddenFromObjC
public object CaretPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '^' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`^`"
}

/** Parses the `^` punctuation token. */
@HiddenFromObjC
public object CaretParse : Parse<Caret> {
 override fun parse(input: ParseStream): SynResult<Caret> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `^`"))
   if (punct.asChar() != '^' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `^`"))
   SynResult.success(Caret.from(punct.span()) to rest)
  }
}
/** Peeks for the `:` punctuation token. */
@HiddenFromObjC
public object ColonPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == ':' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`:`"
}

/** Parses the `:` punctuation token. */
@HiddenFromObjC
public object ColonParse : Parse<Colon> {
 override fun parse(input: ParseStream): SynResult<Colon> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `:`"))
   if (punct.asChar() != ':' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `:`"))
   SynResult.success(Colon.from(punct.span()) to rest)
  }
}
/** Peeks for the `$` punctuation token. */
@HiddenFromObjC
public object DollarPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '$' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`$`"
}

/** Parses the `$` punctuation token. */
@HiddenFromObjC
public object DollarParse : Parse<Dollar> {
 override fun parse(input: ParseStream): SynResult<Dollar> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `$`"))
   if (punct.asChar() != '$' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `$`"))
   SynResult.success(Dollar.from(punct.span()) to rest)
  }
}
/** Peeks for the `.` punctuation token. */
@HiddenFromObjC
public object DotPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '.' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`.`"
}

/** Parses the `.` punctuation token. */
@HiddenFromObjC
public object DotParse : Parse<Dot> {
 override fun parse(input: ParseStream): SynResult<Dot> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `.`"))
   if (punct.asChar() != '.' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `.`"))
   SynResult.success(Dot.from(punct.span()) to rest)
  }
}
/** Peeks for the `=` punctuation token. */
@HiddenFromObjC
public object EqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '=' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`=`"
}

/** Parses the `=` punctuation token. */
@HiddenFromObjC
public object EqParse : Parse<Eq> {
 override fun parse(input: ParseStream): SynResult<Eq> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `=`"))
   if (punct.asChar() != '=' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `=`"))
   SynResult.success(Eq.from(punct.span()) to rest)
  }
}
/** Peeks for the `>` punctuation token. */
@HiddenFromObjC
public object GtPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '>' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`>`"
}

/** Parses the `>` punctuation token. */
@HiddenFromObjC
public object GtParse : Parse<Gt> {
 override fun parse(input: ParseStream): SynResult<Gt> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>`"))
   if (punct.asChar() != '>' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `>`"))
   SynResult.success(Gt.from(punct.span()) to rest)
  }
}
/** Peeks for the `<` punctuation token. */
@HiddenFromObjC
public object LtPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '<' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`<`"
}

/** Parses the `<` punctuation token. */
@HiddenFromObjC
public object LtParse : Parse<Lt> {
 override fun parse(input: ParseStream): SynResult<Lt> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<`"))
   if (punct.asChar() != '<' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `<`"))
   SynResult.success(Lt.from(punct.span()) to rest)
  }
}
/** Peeks for the `-` punctuation token. */
@HiddenFromObjC
public object MinusPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '-' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`-`"
}

/** Parses the `-` punctuation token. */
@HiddenFromObjC
public object MinusParse : Parse<Minus> {
 override fun parse(input: ParseStream): SynResult<Minus> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `-`"))
   if (punct.asChar() != '-' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `-`"))
   SynResult.success(Minus.from(punct.span()) to rest)
  }
}
/** Peeks for the `!` punctuation token. */
@HiddenFromObjC
public object NotPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '!' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`!`"
}

/** Parses the `!` punctuation token. */
@HiddenFromObjC
public object NotParse : Parse<Not> {
 override fun parse(input: ParseStream): SynResult<Not> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `!`"))
   if (punct.asChar() != '!' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `!`"))
   SynResult.success(Not.from(punct.span()) to rest)
  }
}
/** Peeks for the `|` punctuation token. */
@HiddenFromObjC
public object OrPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '|' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`|`"
}

/** Parses the `|` punctuation token. */
@HiddenFromObjC
public object OrParse : Parse<Or> {
 override fun parse(input: ParseStream): SynResult<Or> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `|`"))
   if (punct.asChar() != '|' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `|`"))
   SynResult.success(Or.from(punct.span()) to rest)
  }
}
/** Peeks for the `%` punctuation token. */
@HiddenFromObjC
public object PercentPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '%' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`%`"
}

/** Parses the `%` punctuation token. */
@HiddenFromObjC
public object PercentParse : Parse<Percent> {
 override fun parse(input: ParseStream): SynResult<Percent> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `%`"))
   if (punct.asChar() != '%' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `%`"))
   SynResult.success(Percent.from(punct.span()) to rest)
  }
}
/** Peeks for the `+` punctuation token. */
@HiddenFromObjC
public object PlusPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '+' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`+`"
}

/** Parses the `+` punctuation token. */
@HiddenFromObjC
public object PlusParse : Parse<Plus> {
 override fun parse(input: ParseStream): SynResult<Plus> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `+`"))
   if (punct.asChar() != '+' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `+`"))
   SynResult.success(Plus.from(punct.span()) to rest)
  }
}
/** Peeks for the `#` punctuation token. */
@HiddenFromObjC
public object PoundPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '#' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`#`"
}

/** Parses the `#` punctuation token. */
@HiddenFromObjC
public object PoundParse : Parse<Pound> {
 override fun parse(input: ParseStream): SynResult<Pound> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `#`"))
   if (punct.asChar() != '#' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `#`"))
   SynResult.success(Pound.from(punct.span()) to rest)
  }
}
/** Peeks for the `?` punctuation token. */
@HiddenFromObjC
public object QuestionPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '?' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`?`"
}

/** Parses the `?` punctuation token. */
@HiddenFromObjC
public object QuestionParse : Parse<Question> {
 override fun parse(input: ParseStream): SynResult<Question> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `?`"))
   if (punct.asChar() != '?' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `?`"))
   SynResult.success(Question.from(punct.span()) to rest)
  }
}
/** Peeks for the `;` punctuation token. */
@HiddenFromObjC
public object SemiPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == ';' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`;`"
}

/** Parses the `;` punctuation token. */
@HiddenFromObjC
public object SemiParse : Parse<Semi> {
 override fun parse(input: ParseStream): SynResult<Semi> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `;`"))
   if (punct.asChar() != ';' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `;`"))
   SynResult.success(Semi.from(punct.span()) to rest)
  }
}
/** Peeks for the `/` punctuation token. */
@HiddenFromObjC
public object SlashPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '/' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`/`"
}

/** Parses the `/` punctuation token. */
@HiddenFromObjC
public object SlashParse : Parse<Slash> {
 override fun parse(input: ParseStream): SynResult<Slash> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `/`"))
   if (punct.asChar() != '/' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `/`"))
   SynResult.success(Slash.from(punct.span()) to rest)
  }
}
/** Peeks for the `*` punctuation token. */
@HiddenFromObjC
public object StarPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '*' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`*`"
}

/** Parses the `*` punctuation token. */
@HiddenFromObjC
public object StarParse : Parse<Star> {
 override fun parse(input: ParseStream): SynResult<Star> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `*`"))
   if (punct.asChar() != '*' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `*`"))
   SynResult.success(Star.from(punct.span()) to rest)
  }
}
/** Peeks for the `~` punctuation token. */
@HiddenFromObjC
public object TildePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (punct, _) = cursor.punct() ?: return false
  return punct.asChar() == '~' && punct.spacing() == Spacing.Alone
 }
 override fun display(): String = "`~`"
}

/** Parses the `~` punctuation token. */
@HiddenFromObjC
public object TildeParse : Parse<Tilde> {
 override fun parse(input: ParseStream): SynResult<Tilde> =
  input.step { cursor ->
   val (punct, rest) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `~`"))
   if (punct.asChar() != '~' || punct.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `~`"))
   SynResult.success(Tilde.from(punct.span()) to rest)
  }
}
/** Peeks for the `_` token. */
@HiddenFromObjC
public object UnderscorePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (ident, _) = cursor.ident() ?: return false
  return ident.toString() == "_"
 }
 override fun display(): String = "`_`"
}

/** Parses the `_` token. */
@HiddenFromObjC
public object UnderscoreParse : Parse<io.github.kotlinmania.syn.token.Underscore> {
 override fun parse(input: ParseStream): SynResult<io.github.kotlinmania.syn.token.Underscore> =
  input.step { cursor ->
   val (ident, rest) = cursor.ident()
    ?: return@step SynResult.failure(cursor.error("expected `_`"))
   if (ident.toString() != "_")
    return@step SynResult.failure(cursor.error("expected `_`"))
   SynResult.success(io.github.kotlinmania.syn.token.Underscore.from(ident.span()) to rest)
  }
}

// ── Multi-char Punctuation Peek / Parse (2-char) ─────────────────────────

/** Peeks for the `&&` punctuation token. */
@HiddenFromObjC
public object AndAndPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '&' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '&' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`&&`"
}

/** Parses the `&&` punctuation token. */
@HiddenFromObjC
public object AndAndParse : Parse<AndAnd> {
 override fun parse(input: ParseStream): SynResult<AndAnd> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `&&`"))
   if (first.asChar() != '&' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `&&`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `&&`"))
   if (second.asChar() != '&' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `&&`"))
   SynResult.success(AndAnd.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `&=` punctuation token. */
@HiddenFromObjC
public object AndEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '&' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`&=`"
}

/** Parses the `&=` punctuation token. */
@HiddenFromObjC
public object AndEqParse : Parse<AndEq> {
 override fun parse(input: ParseStream): SynResult<AndEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `&=`"))
   if (first.asChar() != '&' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `&=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `&=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `&=`"))
   SynResult.success(AndEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `^=` punctuation token. */
@HiddenFromObjC
public object CaretEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '^' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`^=`"
}

/** Parses the `^=` punctuation token. */
@HiddenFromObjC
public object CaretEqParse : Parse<CaretEq> {
 override fun parse(input: ParseStream): SynResult<CaretEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `^=`"))
   if (first.asChar() != '^' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `^=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `^=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `^=`"))
   SynResult.success(CaretEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `==` punctuation token. */
@HiddenFromObjC
public object EqEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '=' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`==`"
}

/** Parses the `==` punctuation token. */
@HiddenFromObjC
public object EqEqParse : Parse<EqEq> {
 override fun parse(input: ParseStream): SynResult<EqEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `==`"))
   if (first.asChar() != '=' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `==`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `==`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `==`"))
   SynResult.success(EqEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `=>` punctuation token. */
@HiddenFromObjC
public object FatArrowPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '=' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '>' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`=>`"
}

/** Parses the `=>` punctuation token. */
@HiddenFromObjC
public object FatArrowParse : Parse<FatArrow> {
 override fun parse(input: ParseStream): SynResult<FatArrow> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `=>`"))
   if (first.asChar() != '=' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `=>`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `=>`"))
   if (second.asChar() != '>' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `=>`"))
   SynResult.success(FatArrow.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `>=` punctuation token. */
@HiddenFromObjC
public object GePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '>' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`>=`"
}

/** Parses the `>=` punctuation token. */
@HiddenFromObjC
public object GeParse : Parse<Ge> {
 override fun parse(input: ParseStream): SynResult<Ge> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>=`"))
   if (first.asChar() != '>' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `>=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `>=`"))
   SynResult.success(Ge.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `<=` punctuation token. */
@HiddenFromObjC
public object LePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`<=`"
}

/** Parses the `<=` punctuation token. */
@HiddenFromObjC
public object LeParse : Parse<Le> {
 override fun parse(input: ParseStream): SynResult<Le> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<=`"))
   if (first.asChar() != '<' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `<=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `<=`"))
   SynResult.success(Le.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `<-` punctuation token. */
@HiddenFromObjC
public object LArrowPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '-' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`<-`"
}

/** Parses the `<-` punctuation token. */
@HiddenFromObjC
public object LArrowParse : Parse<LArrow> {
 override fun parse(input: ParseStream): SynResult<LArrow> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<-`"))
   if (first.asChar() != '<' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `<-`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<-`"))
   if (second.asChar() != '-' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `<-`"))
   SynResult.success(LArrow.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `-=` punctuation token. */
@HiddenFromObjC
public object MinusEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '-' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`-=`"
}

/** Parses the `-=` punctuation token. */
@HiddenFromObjC
public object MinusEqParse : Parse<MinusEq> {
 override fun parse(input: ParseStream): SynResult<MinusEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `-=`"))
   if (first.asChar() != '-' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `-=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `-=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `-=`"))
   SynResult.success(MinusEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `!=` punctuation token. */
@HiddenFromObjC
public object NePeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '!' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`!=`"
}

/** Parses the `!=` punctuation token. */
@HiddenFromObjC
public object NeParse : Parse<Ne> {
 override fun parse(input: ParseStream): SynResult<Ne> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `!=`"))
   if (first.asChar() != '!' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `!=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `!=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `!=`"))
   SynResult.success(Ne.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `|=` punctuation token. */
@HiddenFromObjC
public object OrEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '|' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`|=`"
}

/** Parses the `|=` punctuation token. */
@HiddenFromObjC
public object OrEqParse : Parse<OrEq> {
 override fun parse(input: ParseStream): SynResult<OrEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `|=`"))
   if (first.asChar() != '|' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `|=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `|=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `|=`"))
   SynResult.success(OrEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `||` punctuation token. */
@HiddenFromObjC
public object OrOrPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '|' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '|' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`||`"
}

/** Parses the `||` punctuation token. */
@HiddenFromObjC
public object OrOrParse : Parse<OrOr> {
 override fun parse(input: ParseStream): SynResult<OrOr> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `||`"))
   if (first.asChar() != '|' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `||`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `||`"))
   if (second.asChar() != '|' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `||`"))
   SynResult.success(OrOr.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `%=` punctuation token. */
@HiddenFromObjC
public object PercentEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '%' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`%=`"
}

/** Parses the `%=` punctuation token. */
@HiddenFromObjC
public object PercentEqParse : Parse<PercentEq> {
 override fun parse(input: ParseStream): SynResult<PercentEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `%=`"))
   if (first.asChar() != '%' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `%=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `%=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `%=`"))
   SynResult.success(PercentEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `+=` punctuation token. */
@HiddenFromObjC
public object PlusEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '+' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`+=`"
}

/** Parses the `+=` punctuation token. */
@HiddenFromObjC
public object PlusEqParse : Parse<PlusEq> {
 override fun parse(input: ParseStream): SynResult<PlusEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `+=`"))
   if (first.asChar() != '+' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `+=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `+=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `+=`"))
   SynResult.success(PlusEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `->` punctuation token. */
@HiddenFromObjC
public object RArrowPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '-' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '>' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`->`"
}

/** Parses the `->` punctuation token. */
@HiddenFromObjC
public object RArrowParse : Parse<RArrow> {
 override fun parse(input: ParseStream): SynResult<RArrow> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `->`"))
   if (first.asChar() != '-' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `->`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `->`"))
   if (second.asChar() != '>' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `->`"))
   SynResult.success(RArrow.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `/=` punctuation token. */
@HiddenFromObjC
public object SlashEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '/' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`/=`"
}

/** Parses the `/=` punctuation token. */
@HiddenFromObjC
public object SlashEqParse : Parse<SlashEq> {
 override fun parse(input: ParseStream): SynResult<SlashEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `/=`"))
   if (first.asChar() != '/' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `/=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `/=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `/=`"))
   SynResult.success(SlashEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `*=` punctuation token. */
@HiddenFromObjC
public object StarEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '*' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '=' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`*=`"
}

/** Parses the `*=` punctuation token. */
@HiddenFromObjC
public object StarEqParse : Parse<StarEq> {
 override fun parse(input: ParseStream): SynResult<StarEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `*=`"))
   if (first.asChar() != '*' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `*=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `*=`"))
   if (second.asChar() != '=' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `*=`"))
   SynResult.success(StarEq.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `<<` punctuation token. */
@HiddenFromObjC
public object ShlPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '<' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`<<`"
}

/** Parses the `<<` punctuation token. */
@HiddenFromObjC
public object ShlParse : Parse<Shl> {
 override fun parse(input: ParseStream): SynResult<Shl> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<<`"))
   if (first.asChar() != '<' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `<<`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<<`"))
   if (second.asChar() != '<' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `<<`"))
   SynResult.success(Shl.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `>>` punctuation token. */
@HiddenFromObjC
public object ShrPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '>' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '>' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`>>`"
}

/** Parses the `>>` punctuation token. */
@HiddenFromObjC
public object ShrParse : Parse<Shr> {
 override fun parse(input: ParseStream): SynResult<Shr> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>>`"))
   if (first.asChar() != '>' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `>>`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>>`"))
   if (second.asChar() != '>' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `>>`"))
   SynResult.success(Shr.from(listOf(first.span(), second.span())) to rest2)
  }
}
/** Peeks for the `..` punctuation token. */
@HiddenFromObjC
public object DotDotPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '.' || first.spacing() != Spacing.Joint) return false
  val second = rest1.punct()?.first ?: return false
  return second.asChar() == '.' && second.spacing() == Spacing.Alone
 }
 override fun display(): String = "`..`"
}

/** Parses the `..` punctuation token. */
@HiddenFromObjC
public object DotDotParse : Parse<DotDot> {
 override fun parse(input: ParseStream): SynResult<DotDot> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `..`"))
   if (first.asChar() != '.' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `..`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `..`"))
   if (second.asChar() != '.' || second.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `..`"))
   SynResult.success(DotDot.from(listOf(first.span(), second.span())) to rest2)
  }
}

// ── Multi-char Punctuation Peek / Parse (3-char) ─────────────────────────

/** Peeks for the `...` punctuation token. */
@HiddenFromObjC
public object DotDotDotPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '.' || first.spacing() != Spacing.Joint) return false
  val (second, rest2) = rest1.punct() ?: return false
  if (second.asChar() != '.' || second.spacing() != Spacing.Joint) return false
  val third = rest2.punct()?.first ?: return false
  return third.asChar() == '.' && third.spacing() == Spacing.Alone
 }
 override fun display(): String = "`...`"
}

/** Parses the `...` punctuation token. */
@HiddenFromObjC
public object DotDotDotParse : Parse<DotDotDot> {
 override fun parse(input: ParseStream): SynResult<DotDotDot> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `...`"))
   if (first.asChar() != '.' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `...`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `...`"))
   if (second.asChar() != '.' || second.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `...`"))
   val (third, rest3) = rest2.punct()
    ?: return@step SynResult.failure(cursor.error("expected `...`"))
   if (third.asChar() != '.' || third.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `...`"))
   SynResult.success(DotDotDot.from(listOf(first.span(), second.span(), third.span())) to rest3)
  }
}
/** Peeks for the `..=` punctuation token. */
@HiddenFromObjC
public object DotDotEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '.' || first.spacing() != Spacing.Joint) return false
  val (second, rest2) = rest1.punct() ?: return false
  if (second.asChar() != '.' || second.spacing() != Spacing.Joint) return false
  val third = rest2.punct()?.first ?: return false
  return third.asChar() == '=' && third.spacing() == Spacing.Alone
 }
 override fun display(): String = "`..=`"
}

/** Parses the `..=` punctuation token. */
@HiddenFromObjC
public object DotDotEqParse : Parse<DotDotEq> {
 override fun parse(input: ParseStream): SynResult<DotDotEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `..=`"))
   if (first.asChar() != '.' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `..=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `..=`"))
   if (second.asChar() != '.' || second.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `..=`"))
   val (third, rest3) = rest2.punct()
    ?: return@step SynResult.failure(cursor.error("expected `..=`"))
   if (third.asChar() != '=' || third.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `..=`"))
   SynResult.success(DotDotEq.from(listOf(first.span(), second.span(), third.span())) to rest3)
  }
}
/** Peeks for the `<<=` punctuation token. */
@HiddenFromObjC
public object ShlEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '<' || first.spacing() != Spacing.Joint) return false
  val (second, rest2) = rest1.punct() ?: return false
  if (second.asChar() != '<' || second.spacing() != Spacing.Joint) return false
  val third = rest2.punct()?.first ?: return false
  return third.asChar() == '=' && third.spacing() == Spacing.Alone
 }
 override fun display(): String = "`<<=`"
}

/** Parses the `<<=` punctuation token. */
@HiddenFromObjC
public object ShlEqParse : Parse<ShlEq> {
 override fun parse(input: ParseStream): SynResult<ShlEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<<=`"))
   if (first.asChar() != '<' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `<<=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<<=`"))
   if (second.asChar() != '<' || second.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `<<=`"))
   val (third, rest3) = rest2.punct()
    ?: return@step SynResult.failure(cursor.error("expected `<<=`"))
   if (third.asChar() != '=' || third.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `<<=`"))
   SynResult.success(ShlEq.from(listOf(first.span(), second.span(), third.span())) to rest3)
  }
}
/** Peeks for the `>>=` punctuation token. */
@HiddenFromObjC
public object ShrEqPeek : Peek {
 override fun peek(cursor: Cursor): Boolean {
  val (first, rest1) = cursor.punct() ?: return false
  if (first.asChar() != '>' || first.spacing() != Spacing.Joint) return false
  val (second, rest2) = rest1.punct() ?: return false
  if (second.asChar() != '>' || second.spacing() != Spacing.Joint) return false
  val third = rest2.punct()?.first ?: return false
  return third.asChar() == '=' && third.spacing() == Spacing.Alone
 }
 override fun display(): String = "`>>=`"
}

/** Parses the `>>=` punctuation token. */
@HiddenFromObjC
public object ShrEqParse : Parse<ShrEq> {
 override fun parse(input: ParseStream): SynResult<ShrEq> =
  input.step { cursor ->
   val (first, rest1) = cursor.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>>=`"))
   if (first.asChar() != '>' || first.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `>>=`"))
   val (second, rest2) = rest1.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>>=`"))
   if (second.asChar() != '>' || second.spacing() != Spacing.Joint)
    return@step SynResult.failure(cursor.error("expected `>>=`"))
   val (third, rest3) = rest2.punct()
    ?: return@step SynResult.failure(cursor.error("expected `>>=`"))
   if (third.asChar() != '=' || third.spacing() != Spacing.Alone)
    return@step SynResult.failure(cursor.error("expected `>>=`"))
   SynResult.success(ShrEq.from(listOf(first.span(), second.span(), third.span())) to rest3)
  }
}
