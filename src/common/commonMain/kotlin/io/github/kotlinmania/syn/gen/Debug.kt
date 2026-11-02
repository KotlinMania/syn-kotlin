// port-lint: source gen/debug.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.AndLifetime
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.AsIdent
import io.github.kotlinmania.syn.AssocConst
import io.github.kotlinmania.syn.AssocType
import io.github.kotlinmania.syn.AttrStyle
import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.BareFnArg
import io.github.kotlinmania.syn.BareVariadic
import io.github.kotlinmania.syn.BinOp
import io.github.kotlinmania.syn.Block
import io.github.kotlinmania.syn.BoundLifetimes
import io.github.kotlinmania.syn.CapturedParam
import io.github.kotlinmania.syn.Constraint
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DataEnum
import io.github.kotlinmania.syn.DataStruct
import io.github.kotlinmania.syn.DataUnion
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.ElseExpr
import io.github.kotlinmania.syn.EqExpr
import io.github.kotlinmania.syn.EqSynType
import io.github.kotlinmania.syn.Expr
import io.github.kotlinmania.syn.Field
import io.github.kotlinmania.syn.FieldMutability
import io.github.kotlinmania.syn.FieldPat
import io.github.kotlinmania.syn.FieldValue
import io.github.kotlinmania.syn.Fields
import io.github.kotlinmania.syn.FieldsNamed
import io.github.kotlinmania.syn.FieldsUnnamed
import io.github.kotlinmania.syn.File
import io.github.kotlinmania.syn.FnArg
import io.github.kotlinmania.syn.ForeignItem
import io.github.kotlinmania.syn.GenericArgument
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.Generics
import io.github.kotlinmania.syn.IdentColon
import io.github.kotlinmania.syn.IfExpr
import io.github.kotlinmania.syn.ImplItem
import io.github.kotlinmania.syn.ImplRestriction
import io.github.kotlinmania.syn.Index
import io.github.kotlinmania.syn.Item
import io.github.kotlinmania.syn.Label
import io.github.kotlinmania.syn.Lifetime
import io.github.kotlinmania.syn.Lit
import io.github.kotlinmania.syn.LitBool
import io.github.kotlinmania.syn.LitByte
import io.github.kotlinmania.syn.LitByteStr
import io.github.kotlinmania.syn.LitCStr
import io.github.kotlinmania.syn.LitChar
import io.github.kotlinmania.syn.LitFloat
import io.github.kotlinmania.syn.LitInt
import io.github.kotlinmania.syn.LitStr
import io.github.kotlinmania.syn.LocalInit
import io.github.kotlinmania.syn.Macro
import io.github.kotlinmania.syn.MacroDelimiter
import io.github.kotlinmania.syn.Member
import io.github.kotlinmania.syn.Meta
import io.github.kotlinmania.syn.ModContent
import io.github.kotlinmania.syn.Pat
import io.github.kotlinmania.syn.PatColon
import io.github.kotlinmania.syn.PatRest
import io.github.kotlinmania.syn.PatType
import io.github.kotlinmania.syn.Path
import io.github.kotlinmania.syn.PathArguments
import io.github.kotlinmania.syn.PathSegment
import io.github.kotlinmania.syn.PathTrait
import io.github.kotlinmania.syn.PointerMutability
import io.github.kotlinmania.syn.QSelf
import io.github.kotlinmania.syn.RangeLimits
import io.github.kotlinmania.syn.ReturnType
import io.github.kotlinmania.syn.Signature
import io.github.kotlinmania.syn.StaticMutability
import io.github.kotlinmania.syn.Stmt
import io.github.kotlinmania.syn.SynPunctuated
import io.github.kotlinmania.syn.SynType
import io.github.kotlinmania.syn.TraitBoundModifier
import io.github.kotlinmania.syn.TraitItem
import io.github.kotlinmania.syn.TypeParamBound
import io.github.kotlinmania.syn.UnOp
import io.github.kotlinmania.syn.UseTree
import io.github.kotlinmania.syn.Variadic
import io.github.kotlinmania.syn.Variant
import io.github.kotlinmania.syn.Visibility
import io.github.kotlinmania.syn.WhereClause
import io.github.kotlinmania.syn.WherePredicate

private fun debugStruct(
    name: String,
    fields: List<Pair<String, Any?>>,
): String =
    if (fields.isEmpty()) {
        name
    } else {
        fields.joinToString(prefix = "$name { ", postfix = " }") { (field, value) ->
            "$field: ${debugValue(value)}"
        }
    }

private fun debugTuple(
    name: String,
    values: List<Any?>,
): String =
    values.joinToString(prefix = "$name(", postfix = ")") { debugValue(it) }

private fun debugValue(value: Any?): String =
    when (value) {
        null -> "None"
        is Abi -> value.debug()
        is AndLifetime -> value.debug()
        is Arm -> value.debug()
        is AsIdent -> value.debug()
        is AssocConst -> value.debug()
        is AssocType -> value.debug()
        is AttrStyle -> value.debug()
        is Attribute -> value.debug()
        is BareFnArg -> value.debug()
        is BareVariadic -> value.debug()
        is BinOp -> value.debug()
        is Block -> value.debug()
        is BoundLifetimes -> value.debug()
        is CapturedParam -> value.debug()
        is Constraint -> value.debug()
        is Data -> value.debug()
        is DataEnum -> value.debug()
        is DataStruct -> value.debug()
        is DataUnion -> value.debug()
        is DeriveInput -> value.debug()
        is ElseExpr -> value.debug()
        is EqExpr -> value.debug()
        is EqSynType -> value.debug()
        is Expr -> value.debug()
        is Field -> value.debug()
        is FieldMutability -> value.debug()
        is FieldPat -> value.debug()
        is FieldValue -> value.debug()
        is Fields -> value.debug()
        is FieldsNamed -> value.debug()
        is FieldsUnnamed -> value.debug()
        is File -> value.debug()
        is FnArg -> value.debug()
        is ForeignItem -> value.debug()
        is GenericArgument -> value.debug()
        is GenericParam -> value.debug()
        is Generics -> value.debug()
        is IdentColon -> value.debug()
        is IfExpr -> value.debug()
        is ImplItem -> value.debug()
        is ImplRestriction -> value.debug()
        is Index -> value.debug()
        is Item -> value.debug()
        is Label -> value.debug()
        is Lifetime -> value.debug()
        is Lit -> value.debug()
        is LitBool -> value.debug()
        is LitByte -> value.debug()
        is LitByteStr -> value.debug()
        is LitCStr -> value.debug()
        is LitChar -> value.debug()
        is LitFloat -> value.debug()
        is LitInt -> value.debug()
        is LitStr -> value.debug()
        is LocalInit -> value.debug()
        is Macro -> value.debug()
        is MacroDelimiter -> value.debug()
        is Member -> value.debug()
        is Meta -> value.debug()
        is ModContent -> value.debug()
        is Pat -> value.debug()
        is PatColon -> value.debug()
        is PatRest -> value.debug()
        is PatType -> value.debug()
        is Path -> value.debug()
        is PathArguments -> value.debug()
        is PathSegment -> value.debug()
        is PathTrait -> value.debug()
        is PointerMutability -> value.debug()
        is QSelf -> value.debug()
        is RangeLimits -> value.debug()
        is ReturnType -> value.debug()
        is Signature -> value.debug()
        is StaticMutability -> value.debug()
        is Stmt -> value.debug()
        is SynType -> value.debug()
        is TraitBoundModifier -> value.debug()
        is TraitItem -> value.debug()
        is TypeParamBound -> value.debug()
        is UnOp -> value.debug()
        is UseTree -> value.debug()
        is Variadic -> value.debug()
        is Variant -> value.debug()
        is Visibility -> value.debug()
        is WhereClause -> value.debug()
        is WherePredicate -> value.debug()
        is Iterable<*> -> value.joinToString(prefix = "[", postfix = "]") { debugValue(it) }
        else -> value.toString()
    }

internal fun exprDebug(expr: Expr): String = expr.debug()

internal fun punctuatedDebug(list: SynPunctuated): String = debugValue(list)

public fun Abi.debug(): String =
    debugStruct(
        "Abi",
        listOf(
            "extern_token" to externToken,
            "name" to name,
        ),
    )

public fun AndLifetime.debug(): String =
    debugStruct(
        "AndLifetime",
        listOf(
            "and_token" to andToken,
            "lifetime" to lifetime,
        ),
    )

public fun Arm.debug(): String =
    debugStruct(
        "Arm",
        listOf(
            "attrs" to attrs,
            "pat" to pat,
            "guard" to guard,
            "fat_arrow_token" to fatArrowToken,
            "body" to body,
            "comma" to comma,
        ),
    )

public fun AsIdent.debug(): String =
    debugStruct(
        "AsIdent",
        listOf(
            "as_token" to asToken,
            "ident" to ident,
        ),
    )

public fun AssocConst.debug(): String =
    debugStruct(
        "AssocConst",
        listOf(
            "ident" to ident,
            "generics" to generics,
            "eq_token" to eqToken,
            "value" to value,
        ),
    )

public fun AssocType.debug(): String =
    debugStruct(
        "AssocType",
        listOf(
            "ident" to ident,
            "generics" to generics,
            "eq_token" to eqToken,
            "ty" to ty,
        ),
    )

public fun Attribute.debug(): String =
    debugStruct(
        "Attribute",
        listOf(
            "pound_token" to poundToken,
            "style" to style,
            "bracket_token" to bracketToken,
            "meta" to meta,
        ),
    )

public fun BareFnArg.debug(): String =
    debugStruct(
        "BareFnArg",
        listOf(
            "attrs" to attrs,
            "name" to name,
            "ty" to ty,
        ),
    )

public fun BareVariadic.debug(): String =
    debugStruct(
        "BareVariadic",
        listOf(
            "attrs" to attrs,
            "name" to name,
            "dots" to dots,
            "comma" to comma,
        ),
    )

public fun Block.debug(): String =
    debugStruct(
        "Block",
        listOf(
            "brace_token" to braceToken,
            "stmts" to stmts,
        ),
    )

public fun BoundLifetimes.debug(): String =
    debugStruct(
        "BoundLifetimes",
        listOf(
            "for_token" to forToken,
            "lt_token" to ltToken,
            "lifetimes" to lifetimes,
            "gt_token" to gtToken,
        ),
    )

public fun Constraint.debug(): String =
    debugStruct(
        "Constraint",
        listOf(
            "ident" to ident,
            "generics" to generics,
            "colon_token" to colonToken,
            "bounds" to bounds,
        ),
    )

public fun DataEnum.debug(): String =
    debug("DataEnum")

public fun DataEnum.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "enum_token" to enumToken,
            "brace_token" to braceToken,
            "variants" to variants,
        ),
    )

public fun DataStruct.debug(): String =
    debug("DataStruct")

public fun DataStruct.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "struct_token" to structToken,
            "fields" to fields,
            "semi_token" to semiToken,
        ),
    )

public fun DataUnion.debug(): String =
    debug("DataUnion")

public fun DataUnion.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "union_token" to unionToken,
            "fields" to fields,
        ),
    )

public fun DeriveInput.debug(): String =
    debugStruct(
        "DeriveInput",
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "ident" to ident,
            "generics" to generics,
            "data" to data,
        ),
    )

public fun ElseExpr.debug(): String =
    debugStruct(
        "ElseExpr",
        listOf(
            "else_token" to elseToken,
            "expr" to expr,
        ),
    )

public fun EqExpr.debug(): String =
    debugStruct(
        "EqExpr",
        listOf(
            "eq_token" to eqToken,
            "expr" to expr,
        ),
    )

public fun EqSynType.debug(): String =
    debugStruct(
        "EqSynType",
        listOf(
            "eq_token" to eqToken,
            "ty" to type,
        ),
    )

public fun Expr.Array.debug(): String =
    debug("ExprArray")

public fun Expr.Array.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "bracket_token" to bracketToken,
            "elems" to elems,
        ),
    )

public fun Expr.Assign.debug(): String =
    debug("ExprAssign")

public fun Expr.Assign.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "left" to left,
            "eq_token" to eqToken,
            "right" to right,
        ),
    )

public fun Expr.Async.debug(): String =
    debug("ExprAsync")

public fun Expr.Async.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "async_token" to asyncToken,
            "capture" to capture,
            "block" to block,
        ),
    )

public fun Expr.Await.debug(): String =
    debug("ExprAwait")

public fun Expr.Await.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "base" to base,
            "dot_token" to dotToken,
            "await_token" to awaitToken,
        ),
    )

public fun Expr.Binary.debug(): String =
    debug("ExprBinary")

public fun Expr.Binary.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "left" to left,
            "op" to op,
            "right" to right,
        ),
    )

public fun Expr.BlockExpr.debug(): String =
    debug("ExprBlock")

public fun Expr.BlockExpr.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "label" to label,
            "block" to block,
        ),
    )

public fun Expr.Break.debug(): String =
    debug("ExprBreak")

public fun Expr.Break.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "break_token" to breakToken,
            "label" to label,
            "expr" to expr,
        ),
    )

public fun Expr.Call.debug(): String =
    debug("ExprCall")

public fun Expr.Call.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "func" to func,
            "paren_token" to parenToken,
            "args" to args,
        ),
    )

public fun Expr.Cast.debug(): String =
    debug("ExprCast")

public fun Expr.Cast.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "expr" to expr,
            "as_token" to asToken,
            "ty" to ty,
        ),
    )

public fun Expr.Closure.debug(): String =
    debug("ExprClosure")

public fun Expr.Closure.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "lifetimes" to lifetimes,
            "constness" to constness,
            "movability" to movability,
            "asyncness" to asyncness,
            "capture" to capture,
            "or1_token" to or1Token,
            "inputs" to inputs,
            "or2_token" to or2Token,
            "output" to output,
            "body" to body,
        ),
    )

public fun Expr.Const.debug(): String =
    debug("ExprConst")

public fun Expr.Const.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "const_token" to constToken,
            "block" to block,
        ),
    )

public fun Expr.Continue.debug(): String =
    debug("ExprContinue")

public fun Expr.Continue.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "continue_token" to continueToken,
            "label" to label,
        ),
    )

public fun Expr.Field.debug(): String =
    debug("ExprField")

public fun Expr.Field.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "base" to base,
            "dot_token" to dotToken,
            "member" to member,
        ),
    )

public fun Expr.ForLoop.debug(): String =
    debug("ExprForLoop")

public fun Expr.ForLoop.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "label" to label,
            "for_token" to forToken,
            "pat" to pat,
            "in_token" to inToken,
            "expr" to expr,
            "body" to body,
        ),
    )

public fun Expr.Group.debug(): String =
    debug("ExprGroup")

public fun Expr.Group.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "group_token" to groupToken,
            "expr" to expr,
        ),
    )

public fun Expr.If.debug(): String =
    debug("ExprIf")

public fun Expr.If.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "if_token" to ifToken,
            "cond" to cond,
            "then_branch" to thenBranch,
            "else_branch" to elseBranch,
        ),
    )

public fun Expr.Index.debug(): String =
    debug("ExprIndex")

public fun Expr.Index.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "expr" to expr,
            "bracket_token" to bracketToken,
            "index" to index,
        ),
    )

public fun Expr.Infer.debug(): String =
    debug("ExprInfer")

public fun Expr.Infer.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "underscore_token" to underscoreToken,
        ),
    )

public fun Expr.Let.debug(): String =
    debug("ExprLet")

public fun Expr.Let.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "let_token" to letToken,
            "pat" to pat,
            "eq_token" to eqToken,
            "expr" to expr,
        ),
    )

public fun Expr.Lit.debug(): String =
    debug("ExprLit")

public fun Expr.Lit.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "lit" to lit,
        ),
    )

public fun Expr.Loop.debug(): String =
    debug("ExprLoop")

public fun Expr.Loop.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "label" to label,
            "loop_token" to loopToken,
            "body" to body,
        ),
    )

public fun Expr.Macro.debug(): String =
    debug("ExprMacro")

public fun Expr.Macro.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "mac" to mac,
        ),
    )

public fun Expr.Match.debug(): String =
    debug("ExprMatch")

public fun Expr.Match.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "match_token" to matchToken,
            "expr" to expr,
            "brace_token" to braceToken,
            "arms" to arms,
        ),
    )

public fun Expr.MethodCall.debug(): String =
    debug("ExprMethodCall")

public fun Expr.MethodCall.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "receiver" to receiver,
            "dot_token" to dotToken,
            "method" to method,
            "turbofish" to turbofish,
            "paren_token" to parenToken,
            "args" to args,
        ),
    )

public fun Expr.Paren.debug(): String =
    debug("ExprParen")

public fun Expr.Paren.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "paren_token" to parenToken,
            "expr" to expr,
        ),
    )

public fun Expr.Path.debug(): String =
    debug("ExprPath")

public fun Expr.Path.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "qself" to qself,
            "path" to path,
        ),
    )

public fun Expr.Range.debug(): String =
    debug("ExprRange")

public fun Expr.Range.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "start" to start,
            "limits" to limits,
            "end" to end,
        ),
    )

public fun Expr.RawAddr.debug(): String =
    debug("ExprRawAddr")

public fun Expr.RawAddr.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "and_token" to andToken,
            "raw" to raw,
            "mutability" to mutability,
            "expr" to expr,
        ),
    )

public fun Expr.Reference.debug(): String =
    debug("ExprReference")

public fun Expr.Reference.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "and_token" to andToken,
            "mutability" to mutability,
            "expr" to expr,
        ),
    )

public fun Expr.Repeat.debug(): String =
    debug("ExprRepeat")

public fun Expr.Repeat.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "bracket_token" to bracketToken,
            "expr" to expr,
            "semi_token" to semiToken,
            "len" to len,
        ),
    )

public fun Expr.Return.debug(): String =
    debug("ExprReturn")

public fun Expr.Return.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "return_token" to returnToken,
            "expr" to expr,
        ),
    )

public fun Expr.Struct.debug(): String =
    debug("ExprStruct")

public fun Expr.Struct.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "qself" to qself,
            "path" to path,
            "brace_token" to braceToken,
            "fields" to fields,
            "dot2_token" to dot2Token,
            "rest" to rest,
        ),
    )

public fun Expr.Try.debug(): String =
    debug("ExprTry")

public fun Expr.Try.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "expr" to expr,
            "question_token" to questionToken,
        ),
    )

public fun Expr.TryBlock.debug(): String =
    debug("ExprTryBlock")

public fun Expr.TryBlock.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "try_token" to tryToken,
            "block" to block,
        ),
    )

public fun Expr.Tuple.debug(): String =
    debug("ExprTuple")

public fun Expr.Tuple.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "paren_token" to parenToken,
            "elems" to elems,
        ),
    )

public fun Expr.Unary.debug(): String =
    debug("ExprUnary")

public fun Expr.Unary.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "op" to op,
            "expr" to expr,
        ),
    )

public fun Expr.Unsafe.debug(): String =
    debug("ExprUnsafe")

public fun Expr.Unsafe.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "unsafe_token" to unsafeToken,
            "block" to block,
        ),
    )

public fun Expr.While.debug(): String =
    debug("ExprWhile")

public fun Expr.While.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "label" to label,
            "while_token" to whileToken,
            "cond" to cond,
            "body" to body,
        ),
    )

public fun Expr.Yield.debug(): String =
    debug("ExprYield")

public fun Expr.Yield.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "yield_token" to yieldToken,
            "expr" to expr,
        ),
    )

public fun Field.debug(): String =
    debugStruct(
        "Field",
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "mutability" to mutability,
            "ident" to ident,
            "colon_token" to colonToken,
            "ty" to ty,
        ),
    )

public fun FieldPat.debug(): String =
    debugStruct(
        "FieldPat",
        listOf(
            "attrs" to attrs,
            "member" to member,
            "colon_token" to colonToken,
            "pat" to pat,
        ),
    )

public fun FieldValue.debug(): String =
    debugStruct(
        "FieldValue",
        listOf(
            "attrs" to attrs,
            "member" to member,
            "colon_token" to colonToken,
            "expr" to expr,
        ),
    )

public fun FieldsNamed.debug(): String =
    debug("FieldsNamed")

public fun FieldsNamed.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "brace_token" to braceToken,
            "named" to named,
        ),
    )

public fun FieldsUnnamed.debug(): String =
    debug("FieldsUnnamed")

public fun FieldsUnnamed.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "paren_token" to parenToken,
            "unnamed" to unnamed,
        ),
    )

public fun File.debug(): String =
    debugStruct(
        "File",
        listOf(
            "shebang" to shebang,
            "attrs" to attrs,
            "items" to items,
        ),
    )

public fun FnArg.Receiver.debug(): String =
    debug("Receiver")

public fun FnArg.Receiver.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "reference" to reference,
            "mutability" to mutability,
            "self_token" to selfToken,
            "colon_token" to colonToken,
            "ty" to type,
        ),
    )

public fun ForeignItem.Fn.debug(): String =
    debug("ForeignItemFn")

public fun ForeignItem.Fn.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "sig" to sig,
            "semi_token" to semiToken,
        ),
    )

public fun ForeignItem.Macro.debug(): String =
    debug("ForeignItemMacro")

public fun ForeignItem.Macro.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "mac" to mac,
            "semi_token" to semiToken,
        ),
    )

public fun ForeignItem.Static.debug(): String =
    debug("ForeignItemStatic")

public fun ForeignItem.Static.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "static_token" to staticToken,
            "mutability" to mutability,
            "ident" to ident,
            "colon_token" to colonToken,
            "ty" to ty,
            "semi_token" to semiToken,
        ),
    )

public fun ForeignItem.ItemType.debug(): String =
    debug("ForeignItemType")

public fun ForeignItem.ItemType.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "type_token" to typeToken,
            "ident" to ident,
            "generics" to generics,
            "semi_token" to semiToken,
        ),
    )

public fun GenericParam.ConstParam.debug(): String =
    debug("ConstParam")

public fun GenericParam.ConstParam.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "const_token" to constToken,
            "ident" to ident,
            "colon_token" to colonToken,
            "ty" to ty,
            "eq_token" to eqToken,
            "default" to default,
        ),
    )

public fun GenericParam.LifetimeParam.debug(): String =
    debug("LifetimeParam")

public fun GenericParam.LifetimeParam.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "lifetime" to lifetime,
            "colon_token" to colonToken,
            "bounds" to bounds,
        ),
    )

public fun GenericParam.TypeParam.debug(): String =
    debug("TypeParam")

public fun GenericParam.TypeParam.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "ident" to ident,
            "colon_token" to colonToken,
            "bounds" to bounds,
            "eq_token" to eqToken,
            "default" to default,
        ),
    )

public fun Generics.debug(): String =
    debugStruct(
        "Generics",
        listOf(
            "lt_token" to ltToken,
            "params" to params,
            "gt_token" to gtToken,
            "where_clause" to whereClause,
        ),
    )

public fun IdentColon.debug(): String =
    debugStruct(
        "IdentColon",
        listOf(
            "ident" to ident,
            "colon_token" to colonToken,
        ),
    )

public fun IfExpr.debug(): String =
    debugStruct(
        "IfExpr",
        listOf(
            "if_token" to ifToken,
            "expr" to expr,
        ),
    )

public fun ImplItem.Const.debug(): String =
    debug("ImplItemConst")

public fun ImplItem.Const.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "defaultness" to defaultness,
            "const_token" to constToken,
            "ident" to ident,
            "generics" to generics,
            "colon_token" to colonToken,
            "ty" to ty,
            "eq_token" to eqToken,
            "expr" to expr,
            "semi_token" to semiToken,
        ),
    )

public fun ImplItem.Fn.debug(): String =
    debug("ImplItemFn")

public fun ImplItem.Fn.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "defaultness" to defaultness,
            "sig" to sig,
            "block" to block,
        ),
    )

public fun ImplItem.Macro.debug(): String =
    debug("ImplItemMacro")

public fun ImplItem.Macro.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "mac" to mac,
            "semi_token" to semiToken,
        ),
    )

public fun ImplItem.AssocType.debug(): String =
    debug("ImplItemType")

public fun ImplItem.AssocType.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "defaultness" to defaultness,
            "type_token" to typeToken,
            "ident" to ident,
            "generics" to generics,
            "eq_token" to eqToken,
            "ty" to ty,
            "semi_token" to semiToken,
        ),
    )

public fun Index.debug(): String =
    debugStruct(
        "Index",
        listOf(
            "index" to index,
            "span" to span,
        ),
    )

public fun Item.Const.debug(): String =
    debug("ItemConst")

public fun Item.Const.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "const_token" to constToken,
            "ident" to ident,
            "colon_token" to colonToken,
            "ty" to ty,
            "eq_token" to eqToken,
            "expr" to expr,
            "semi_token" to semiToken,
        ),
    )

public fun Item.Enum.debug(): String =
    debug("ItemEnum")

public fun Item.Enum.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "enum_token" to enumToken,
            "ident" to ident,
            "generics" to generics,
            "brace_token" to braceToken,
            "variants" to variants,
        ),
    )

public fun Item.ExternCrate.debug(): String =
    debug("ItemExternCrate")

public fun Item.ExternCrate.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "extern_token" to externToken,
            "crate_token" to crateToken,
            "ident" to ident,
            "rename" to rename,
            "semi_token" to semiToken,
        ),
    )

public fun Item.Fn.debug(): String =
    debug("ItemFn")

public fun Item.Fn.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "sig" to sig,
            "block" to block,
        ),
    )

public fun Item.ForeignMod.debug(): String =
    debug("ItemForeignMod")

public fun Item.ForeignMod.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "unsafety" to unsafety,
            "abi" to abi,
            "brace_token" to braceToken,
            "items" to items,
        ),
    )

public fun Item.Impl.debug(): String =
    debug("ItemImpl")

public fun Item.Impl.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "defaultness" to defaultness,
            "unsafety" to unsafety,
            "impl_token" to implToken,
            "generics" to generics,
            "trait_" to traitPath,
            "self_ty" to selfType,
            "brace_token" to braceToken,
            "items" to items,
        ),
    )

public fun Item.Macro.debug(): String =
    debug("ItemMacro")

public fun Item.Macro.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "ident" to ident,
            "mac" to mac,
            "semi_token" to semiToken,
        ),
    )

public fun Item.Mod.debug(): String =
    debug("ItemMod")

public fun Item.Mod.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "unsafety" to unsafety,
            "mod_token" to modToken,
            "ident" to ident,
            "content" to content,
        ),
    )

public fun Item.Static.debug(): String =
    debug("ItemStatic")

public fun Item.Static.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "static_token" to staticToken,
            "mutability" to mutability,
            "ident" to ident,
            "colon_token" to colonToken,
            "ty" to ty,
            "eq_token" to eqToken,
            "expr" to expr,
            "semi_token" to semiToken,
        ),
    )

public fun Item.Struct.debug(): String =
    debug("ItemStruct")

public fun Item.Struct.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "struct_token" to structToken,
            "ident" to ident,
            "generics" to generics,
            "fields" to fields,
            "semi_token" to semiToken,
        ),
    )

public fun Item.Trait.debug(): String =
    debug("ItemTrait")

public fun Item.Trait.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "unsafety" to unsafety,
            "auto_token" to autoToken,
            "restriction" to restriction,
            "trait_token" to traitToken,
            "ident" to ident,
            "generics" to generics,
            "colon_token" to colonToken,
            "supertraits" to supertraits,
            "brace_token" to braceToken,
            "items" to items,
        ),
    )

public fun Item.TraitAlias.debug(): String =
    debug("ItemTraitAlias")

public fun Item.TraitAlias.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "trait_token" to traitToken,
            "ident" to ident,
            "generics" to generics,
            "eq_token" to eqToken,
            "bounds" to bounds,
            "semi_token" to semiToken,
        ),
    )

public fun Item.ItemType.debug(): String =
    debug("ItemType")

public fun Item.ItemType.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "type_token" to typeToken,
            "ident" to ident,
            "generics" to generics,
            "eq_token" to eqToken,
            "ty" to ty,
            "semi_token" to semiToken,
        ),
    )

public fun Item.Union.debug(): String =
    debug("ItemUnion")

public fun Item.Union.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "union_token" to unionToken,
            "ident" to ident,
            "generics" to generics,
            "fields" to fields,
        ),
    )

public fun Item.Use.debug(): String =
    debug("ItemUse")

public fun Item.Use.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "vis" to vis,
            "use_token" to useToken,
            "leading_colon" to leadingColon,
            "tree" to tree,
            "semi_token" to semiToken,
        ),
    )

public fun Label.debug(): String =
    debugStruct(
        "Label",
        listOf(
            "name" to name,
            "colon_token" to colonToken,
        ),
    )

public fun LocalInit.debug(): String =
    debugStruct(
        "LocalInit",
        listOf(
            "eq_token" to eqToken,
            "expr" to expr,
            "diverge" to diverge,
        ),
    )

public fun Macro.debug(): String =
    debugStruct(
        "Macro",
        listOf(
            "path" to path,
            "bang_token" to bangToken,
            "delimiter" to delimiter,
            "tokens" to tokens,
        ),
    )

public fun Meta.List.debug(): String =
    debug("MetaList")

public fun Meta.List.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "path" to path,
            "delimiter" to delimiter,
            "tokens" to tokens,
        ),
    )

public fun Meta.NameValue.debug(): String =
    debug("MetaNameValue")

public fun Meta.NameValue.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "path" to path,
            "eq_token" to eqToken,
            "value" to value,
        ),
    )

public fun Pat.Const.debug(): String =
    debug("PatConst")

public fun Pat.Const.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "const_token" to constToken,
            "block" to block,
        ),
    )

public fun Pat.Ident.debug(): String =
    debug("PatIdent")

public fun Pat.Ident.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "by_ref" to byRef,
            "mutability" to mutability,
            "ident" to ident,
            "at_token" to atToken,
            "subpat" to subpat,
        ),
    )

public fun Pat.Lit.debug(): String =
    debug("PatLit")

public fun Pat.Lit.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "lit" to lit,
        ),
    )

public fun Pat.Macro.debug(): String =
    debug("PatMacro")

public fun Pat.Macro.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "mac" to mac,
        ),
    )

public fun Pat.Or.debug(): String =
    debug("PatOr")

public fun Pat.Or.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "leading_vert" to leadingVert,
            "cases" to cases,
        ),
    )

public fun Pat.PatParen.debug(): String =
    debug("PatParen")

public fun Pat.PatParen.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "paren_token" to parenToken,
            "pat" to pat,
        ),
    )

public fun Pat.Path.debug(): String =
    debug("PatPath")

public fun Pat.Path.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "qself" to qself,
            "path" to path,
        ),
    )

public fun Pat.Range.debug(): String =
    debug("PatRange")

public fun Pat.Range.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "start" to start,
            "limits" to limits,
            "end" to end,
        ),
    )

public fun Pat.Reference.debug(): String =
    debug("PatReference")

public fun Pat.Reference.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "and_token" to andToken,
            "mutability" to mutability,
            "pat" to pat,
        ),
    )

public fun Pat.Rest.debug(): String =
    debug("PatRest")

public fun Pat.Rest.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "dot2_token" to dot2Token,
        ),
    )

public fun Pat.Slice.debug(): String =
    debug("PatSlice")

public fun Pat.Slice.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "bracket_token" to bracketToken,
            "elems" to elems,
        ),
    )

public fun Pat.Struct.debug(): String =
    debug("PatStruct")

public fun Pat.Struct.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "qself" to qself,
            "path" to path,
            "brace_token" to braceToken,
            "fields" to fields,
            "dot2_token" to dot2Token,
            "rest" to rest,
        ),
    )

public fun Pat.Tuple.debug(): String =
    debug("PatTuple")

public fun Pat.Tuple.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "paren_token" to parenToken,
            "elems" to elems,
        ),
    )

public fun Pat.TupleStruct.debug(): String =
    debug("PatTupleStruct")

public fun Pat.TupleStruct.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "qself" to qself,
            "path" to path,
            "paren_token" to parenToken,
            "elems" to elems,
        ),
    )

public fun Pat.TypeAscription.debug(): String =
    debug("PatType")

public fun Pat.TypeAscription.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "pat" to pat,
            "colon_token" to colonToken,
            "ty" to ty,
        ),
    )

public fun Pat.Wild.debug(): String =
    debug("PatWild")

public fun Pat.Wild.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "underscore_token" to underscoreToken,
        ),
    )

public fun PatColon.debug(): String =
    debugStruct(
        "PatColon",
        listOf(
            "pat" to pat,
            "colon_token" to colonToken,
        ),
    )

public fun PatRest.debug(): String =
    debug("PatRest")

public fun PatRest.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "dot2_token" to dot2Token,
        ),
    )

public fun PatType.debug(): String =
    debug("PatType")

public fun PatType.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "pat" to pat,
            "colon_token" to colonToken,
            "ty" to ty,
        ),
    )

public fun Path.debug(): String =
    debugStruct(
        "Path",
        listOf(
            "leading_colon" to leadingColon,
            "segments" to segments,
        ),
    )

public fun PathArguments.AngleBracketed.debug(): String =
    debug("AngleBracketedGenericArguments")

public fun PathArguments.AngleBracketed.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "colon2_token" to colon2Token,
            "lt_token" to ltToken,
            "args" to args,
            "gt_token" to gtToken,
        ),
    )

public fun PathArguments.Parenthesized.debug(): String =
    debug("ParenthesizedGenericArguments")

public fun PathArguments.Parenthesized.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "paren_token" to parenToken,
            "inputs" to inputs,
            "output" to output,
        ),
    )

public fun PathSegment.debug(): String =
    debugStruct(
        "PathSegment",
        listOf(
            "ident" to ident,
            "arguments" to arguments,
        ),
    )

public fun PathTrait.debug(): String =
    debugStruct(
        "PathTrait",
        listOf(
            "polarity" to polarity,
            "path" to path,
            "for_token" to forToken,
        ),
    )

public fun QSelf.debug(): String =
    debugStruct(
        "QSelf",
        listOf(
            "lt_token" to ltToken,
            "ty" to ty,
            "position" to position,
            "as_token" to asToken,
            "gt_token" to gtToken,
        ),
    )

public fun Signature.debug(): String =
    debugStruct(
        "Signature",
        listOf(
            "constness" to constness,
            "asyncness" to asyncness,
            "unsafety" to unsafety,
            "abi" to abi,
            "fn_token" to fnToken,
            "ident" to ident,
            "generics" to generics,
            "paren_token" to parenToken,
            "inputs" to inputs,
            "variadic" to variadic,
            "output" to output,
        ),
    )

public fun Stmt.Local.debug(): String =
    debug("Local")

public fun Stmt.Local.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "let_token" to letToken,
            "pat" to pat,
            "init" to init,
            "semi_token" to semiToken,
        ),
    )

public fun Stmt.MacroStmt.debug(): String =
    debug("StmtMacro")

public fun Stmt.MacroStmt.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "mac" to mac,
            "semi_token" to semiToken,
        ),
    )

public fun SynType.Array.debug(): String =
    debug("TypeArray")

public fun SynType.Array.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "bracket_token" to bracketToken,
            "elem" to elem,
            "semi_token" to semiToken,
            "len" to len,
        ),
    )

public fun SynType.BareFn.debug(): String =
    debug("TypeBareFn")

public fun SynType.BareFn.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "lifetimes" to lifetimes,
            "unsafety" to unsafety,
            "abi" to abi,
            "fn_token" to fnToken,
            "paren_token" to parenToken,
            "inputs" to inputs,
            "variadic" to variadic,
            "output" to output,
        ),
    )

public fun SynType.Group.debug(): String =
    debug("TypeGroup")

public fun SynType.Group.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "group_token" to groupToken,
            "elem" to elem,
        ),
    )

public fun SynType.ImplTrait.debug(): String =
    debug("TypeImplTrait")

public fun SynType.ImplTrait.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "impl_token" to implToken,
            "bounds" to bounds,
        ),
    )

public fun SynType.Infer.debug(): String =
    debug("TypeInfer")

public fun SynType.Infer.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "underscore_token" to underscoreToken,
        ),
    )

public fun SynType.Macro.debug(): String =
    debug("TypeMacro")

public fun SynType.Macro.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "mac" to mac,
        ),
    )

public fun SynType.Never.debug(): String =
    debug("TypeNever")

public fun SynType.Never.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "bang_token" to bangToken,
        ),
    )

public fun SynType.Paren.debug(): String =
    debug("TypeParen")

public fun SynType.Paren.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "paren_token" to parenToken,
            "elem" to elem,
        ),
    )

public fun SynType.Path.debug(): String =
    debug("TypePath")

public fun SynType.Path.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "qself" to qself,
            "path" to path,
        ),
    )

public fun SynType.Ptr.debug(): String =
    debug("TypePtr")

public fun SynType.Ptr.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "star_token" to starToken,
            "const_token" to constToken,
            "mutability" to mutability,
            "elem" to elem,
        ),
    )

public fun SynType.Reference.debug(): String =
    debug("TypeReference")

public fun SynType.Reference.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "and_token" to andToken,
            "lifetime" to lifetime,
            "mutability" to mutability,
            "elem" to elem,
        ),
    )

public fun SynType.Slice.debug(): String =
    debug("TypeSlice")

public fun SynType.Slice.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "elem" to elem,
        ),
    )

public fun SynType.TraitObject.debug(): String =
    debug("TypeTraitObject")

public fun SynType.TraitObject.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "dyn_token" to dynToken,
            "bounds" to bounds,
        ),
    )

public fun SynType.Tuple.debug(): String =
    debug("TypeTuple")

public fun SynType.Tuple.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "paren_token" to parenToken,
            "elems" to elems,
        ),
    )

public fun TraitItem.Const.debug(): String =
    debug("TraitItemConst")

public fun TraitItem.Const.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "const_token" to constToken,
            "ident" to ident,
            "generics" to generics,
            "colon_token" to colonToken,
            "ty" to ty,
            "default" to default,
            "semi_token" to semiToken,
        ),
    )

public fun TraitItem.Fn.debug(): String =
    debug("TraitItemFn")

public fun TraitItem.Fn.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "sig" to sig,
            "default" to default,
            "semi_token" to semiToken,
        ),
    )

public fun TraitItem.Macro.debug(): String =
    debug("TraitItemMacro")

public fun TraitItem.Macro.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "mac" to mac,
            "semi_token" to semiToken,
        ),
    )

public fun TraitItem.AssocType.debug(): String =
    debug("TraitItemType")

public fun TraitItem.AssocType.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "attrs" to attrs,
            "type_token" to typeToken,
            "ident" to ident,
            "generics" to generics,
            "colon_token" to colonToken,
            "bounds" to bounds,
            "default" to default,
            "semi_token" to semiToken,
        ),
    )

public fun TypeParamBound.PreciseCapture.debug(): String =
    debug("PreciseCapture")

public fun TypeParamBound.PreciseCapture.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "use_token" to useToken,
            "lt_token" to ltToken,
            "params" to params,
            "gt_token" to gtToken,
        ),
    )

public fun TypeParamBound.Trait.debug(): String =
    debug("TraitBound")

public fun TypeParamBound.Trait.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "paren_token" to parenToken,
            "modifier" to modifier,
            "lifetimes" to lifetimes,
            "path" to path,
        ),
    )

public fun UseTree.Glob.debug(): String =
    debug("UseGlob")

public fun UseTree.Glob.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "star_token" to starToken,
        ),
    )

public fun UseTree.Group.debug(): String =
    debug("UseGroup")

public fun UseTree.Group.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "brace_token" to braceToken,
            "items" to items,
        ),
    )

public fun UseTree.Name.debug(): String =
    debug("UseName")

public fun UseTree.Name.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "ident" to ident,
            "rename" to rename,
        ),
    )

public fun UseTree.Path.debug(): String =
    debug("UsePath")

public fun UseTree.Path.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "ident" to ident,
            "colon2_token" to colon2Token,
            "tree" to tree,
        ),
    )

public fun Variadic.debug(): String =
    debugStruct(
        "Variadic",
        listOf(
            "attrs" to attrs,
            "pat" to pat,
            "dots" to dots,
            "comma" to comma,
        ),
    )

public fun Variant.debug(): String =
    debugStruct(
        "Variant",
        listOf(
            "attrs" to attrs,
            "ident" to ident,
            "fields" to fields,
            "discriminant" to discriminant,
        ),
    )

public fun Visibility.Restricted.debug(): String =
    debug("VisRestricted")

public fun Visibility.Restricted.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "pub_token" to pubToken,
            "paren_token" to parenToken,
            "in_token" to inToken,
            "path" to path,
        ),
    )

public fun WhereClause.debug(): String =
    debugStruct(
        "WhereClause",
        listOf(
            "where_token" to whereToken,
            "predicates" to predicates,
        ),
    )

public fun WherePredicate.LifetimePredicate.debug(): String =
    debug("PredicateLifetime")

public fun WherePredicate.LifetimePredicate.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "lifetime" to lifetime,
            "colon_token" to colonToken,
            "bounds" to bounds,
        ),
    )

public fun WherePredicate.TypePredicate.debug(): String =
    debug("PredicateType")

public fun WherePredicate.TypePredicate.debug(name: String): String =
    debugStruct(
        name,
        listOf(
            "lifetimes" to lifetimes,
            "bounded_ty" to boundedTy,
            "colon_token" to colonToken,
            "bounds" to bounds,
        ),
    )

public fun Expr.Verbatim.debug(): String =
    debugTuple("ExprVerbatim", listOf(tokens))

public fun ForeignItem.Verbatim.debug(): String =
    debugTuple("ForeignItemVerbatim", listOf(tokens))

public fun ImplItem.Verbatim.debug(): String =
    debugTuple("ImplItemVerbatim", listOf(tokens))

public fun Item.Verbatim.debug(): String =
    debugTuple("ItemVerbatim", listOf(tokens))

public fun Pat.Verbatim.debug(): String =
    debugTuple("PatVerbatim", listOf(tokens))

public fun SynType.Verbatim.debug(): String =
    debugTuple("TypeVerbatim", listOf(tokens))

public fun TraitItem.Verbatim.debug(): String =
    debugTuple("TraitItemVerbatim", listOf(tokens))

public fun TypeParamBound.Verbatim.debug(): String =
    debugTuple("TypeParamBoundVerbatim", listOf(tokens))

public fun Lifetime.debug(): String =
    debugStruct(
        "Lifetime",
        listOf(
            "apostrophe" to apostrophe,
            "ident" to ident,
        ),
    )

public fun LitBool.debug(): String =
    debug("LitBool")

public fun LitByte.debug(): String =
    debug("LitByte")

public fun LitByteStr.debug(): String =
    debug("LitByteStr")

public fun LitCStr.debug(): String =
    debug("LitCStr")

public fun LitChar.debug(): String =
    debug("LitChar")

public fun LitFloat.debug(): String =
    debug("LitFloat")

public fun LitInt.debug(): String =
    debug("LitInt")

public fun LitStr.debug(): String =
    debug("LitStr")

public fun AttrStyle.debug(): String =
    "AttrStyle::" +
        when (this) {
            AttrStyle.Outer -> "Outer"
            is AttrStyle.Inner -> debugTuple("Inner", listOf(bangToken))
        }

public fun BinOp.debug(): String =
    "BinOp::" +
        when (this) {
            is BinOp.Add -> debugTuple("Add", listOf(token))
            is BinOp.Sub -> debugTuple("Sub", listOf(token))
            is BinOp.Mul -> debugTuple("Mul", listOf(token))
            is BinOp.Div -> debugTuple("Div", listOf(token))
            is BinOp.Rem -> debugTuple("Rem", listOf(token))
            is BinOp.And -> debugTuple("And", listOf(token))
            is BinOp.Or -> debugTuple("Or", listOf(token))
            is BinOp.BitXor -> debugTuple("BitXor", listOf(token))
            is BinOp.BitAnd -> debugTuple("BitAnd", listOf(token))
            is BinOp.BitOr -> debugTuple("BitOr", listOf(token))
            is BinOp.Shl -> debugTuple("Shl", listOf(token))
            is BinOp.Shr -> debugTuple("Shr", listOf(token))
            is BinOp.Eq -> debugTuple("Eq", listOf(token))
            is BinOp.Lt -> debugTuple("Lt", listOf(token))
            is BinOp.Le -> debugTuple("Le", listOf(token))
            is BinOp.Ne -> debugTuple("Ne", listOf(token))
            is BinOp.Ge -> debugTuple("Ge", listOf(token))
            is BinOp.Gt -> debugTuple("Gt", listOf(token))
            is BinOp.AddAssign -> debugTuple("AddAssign", listOf(token))
            is BinOp.SubAssign -> debugTuple("SubAssign", listOf(token))
            is BinOp.MulAssign -> debugTuple("MulAssign", listOf(token))
            is BinOp.DivAssign -> debugTuple("DivAssign", listOf(token))
            is BinOp.RemAssign -> debugTuple("RemAssign", listOf(token))
            is BinOp.BitXorAssign -> debugTuple("BitXorAssign", listOf(token))
            is BinOp.BitAndAssign -> debugTuple("BitAndAssign", listOf(token))
            is BinOp.BitOrAssign -> debugTuple("BitOrAssign", listOf(token))
            is BinOp.ShlAssign -> debugTuple("ShlAssign", listOf(token))
            is BinOp.ShrAssign -> debugTuple("ShrAssign", listOf(token))
        }

public fun CapturedParam.debug(): String =
    "CapturedParam::" +
        when (this) {
            is CapturedParam.Lifetime -> debugTuple("Lifetime", listOf(lifetime))
            is CapturedParam.Ident -> debugTuple("Ident", listOf(ident))
        }

public fun Data.debug(): String =
    "Data::" +
        when (this) {
            is Data.Struct -> value.debug("Struct")
            is Data.Enum -> value.debug("Enum")
            is Data.Union -> value.debug("Union")
        }

public fun Expr.debug(): String =
    "Expr::" +
        when (this) {
            is Expr.Array -> this.debug("Array")
            is Expr.Assign -> this.debug("Assign")
            is Expr.Async -> this.debug("Async")
            is Expr.Await -> this.debug("Await")
            is Expr.Binary -> this.debug("Binary")
            is Expr.BlockExpr -> this.debug("Block")
            is Expr.Break -> this.debug("Break")
            is Expr.Call -> this.debug("Call")
            is Expr.Cast -> this.debug("Cast")
            is Expr.Closure -> this.debug("Closure")
            is Expr.Const -> this.debug("Const")
            is Expr.Continue -> this.debug("Continue")
            is Expr.Field -> this.debug("Field")
            is Expr.ForLoop -> this.debug("ForLoop")
            is Expr.Group -> this.debug("Group")
            is Expr.If -> this.debug("If")
            is Expr.Index -> this.debug("Index")
            is Expr.Infer -> this.debug("Infer")
            is Expr.Let -> this.debug("Let")
            is Expr.Lit -> this.debug("Lit")
            is Expr.Loop -> this.debug("Loop")
            is Expr.Macro -> this.debug("Macro")
            is Expr.Match -> this.debug("Match")
            is Expr.MethodCall -> this.debug("MethodCall")
            is Expr.Paren -> this.debug("Paren")
            is Expr.Path -> this.debug("Path")
            is Expr.Range -> this.debug("Range")
            is Expr.RawAddr -> this.debug("RawAddr")
            is Expr.Reference -> this.debug("Reference")
            is Expr.Repeat -> this.debug("Repeat")
            is Expr.Return -> this.debug("Return")
            is Expr.Struct -> this.debug("Struct")
            is Expr.Try -> this.debug("Try")
            is Expr.TryBlock -> this.debug("TryBlock")
            is Expr.Tuple -> this.debug("Tuple")
            is Expr.Unary -> this.debug("Unary")
            is Expr.Unsafe -> this.debug("Unsafe")
            is Expr.While -> this.debug("While")
            is Expr.Yield -> this.debug("Yield")
            is Expr.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun FieldMutability.debug(): String =
    "FieldMutability::" +
        when (this) {
            FieldMutability.None -> "None"
            is FieldMutability.Mut -> debugTuple("Mut", listOf(token))
        }

public fun Fields.debug(): String =
    "Fields::" +
        when (this) {
            is Fields.Named -> fields.debug("Named")
            is Fields.Unnamed -> fields.debug("Unnamed")
            Fields.Unit -> "Unit"
        }

public fun FnArg.debug(): String =
    "FnArg::" +
        when (this) {
            is FnArg.Receiver -> this.debug("Receiver")
            is FnArg.Typed -> patType.debug("Typed")
        }

public fun FnArg.Typed.debug(): String =
    patType.debug("Typed")

public fun ForeignItem.debug(): String =
    "ForeignItem::" +
        when (this) {
            is ForeignItem.Fn -> this.debug("Fn")
            is ForeignItem.Static -> this.debug("Static")
            is ForeignItem.ItemType -> this.debug("Type")
            is ForeignItem.Macro -> this.debug("Macro")
            is ForeignItem.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun GenericArgument.debug(): String =
    "GenericArgument::" +
        when (this) {
            is GenericArgument.LifetimeArg -> debugTuple("Lifetime", listOf(lifetime))
            is GenericArgument.TypeArg -> debugTuple("Type", listOf(type))
            is GenericArgument.ConstArg -> debugTuple("Const", listOf(expr))
            is GenericArgument.AssocTypeArg -> debugTuple("AssocType", listOf(assoc))
            is GenericArgument.AssocConstArg -> debugTuple("AssocConst", listOf(assoc))
            is GenericArgument.ConstraintArg -> debugTuple("Constraint", listOf(constraint))
        }

public fun GenericParam.debug(): String =
    "GenericParam::" +
        when (this) {
            is GenericParam.LifetimeParam -> this.debug("Lifetime")
            is GenericParam.TypeParam -> this.debug("Type")
            is GenericParam.ConstParam -> this.debug("Const")
        }

public fun ImplItem.debug(): String =
    "ImplItem::" +
        when (this) {
            is ImplItem.Const -> this.debug("Const")
            is ImplItem.Fn -> this.debug("Fn")
            is ImplItem.AssocType -> this.debug("Type")
            is ImplItem.Macro -> this.debug("Macro")
            is ImplItem.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun ImplRestriction.debug(): String = "ImplRestriction"

public fun Item.debug(): String =
    "Item::" +
        when (this) {
            is Item.Const -> this.debug("Const")
            is Item.Enum -> this.debug("Enum")
            is Item.ExternCrate -> this.debug("ExternCrate")
            is Item.Fn -> this.debug("Fn")
            is Item.ForeignMod -> this.debug("ForeignMod")
            is Item.Impl -> this.debug("Impl")
            is Item.Macro -> this.debug("Macro")
            is Item.Mod -> this.debug("Mod")
            is Item.Static -> this.debug("Static")
            is Item.Struct -> this.debug("Struct")
            is Item.Trait -> this.debug("Trait")
            is Item.TraitAlias -> this.debug("TraitAlias")
            is Item.ItemType -> this.debug("Type")
            is Item.Union -> this.debug("Union")
            is Item.Use -> this.debug("Use")
            is Item.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun Lit.debug(): String =
    "Lit::" +
        when (this) {
            is Lit.Str -> value.debug("Str")
            is Lit.ByteStr -> value.debug("ByteStr")
            is Lit.CStr -> value.debug("CStr")
            is Lit.Byte -> value.debug("Byte")
            is Lit.Char -> value.debug("Char")
            is Lit.Int -> value.debug("Int")
            is Lit.Float -> value.debug("Float")
            is Lit.Bool -> value.debug("Bool")
            is Lit.Verbatim -> debugTuple("Verbatim", listOf(value))
        }

public fun MacroDelimiter.debug(): String =
    "MacroDelimiter::" +
        when (this) {
            is MacroDelimiter.Paren -> debugTuple("Paren", listOf(token))
            is MacroDelimiter.Brace -> debugTuple("Brace", listOf(token))
            is MacroDelimiter.Bracket -> debugTuple("Bracket", listOf(token))
        }

public fun Member.debug(): String =
    "Member::" +
        when (this) {
            is Member.Named -> debugTuple("Named", listOf(ident))
            is Member.Unnamed -> debugTuple("Unnamed", listOf(index))
        }

public fun Meta.debug(): String =
    "Meta::" +
        when (this) {
            is Meta.PathMeta -> debugTuple("Path", listOf(path))
            is Meta.List -> this.debug("List")
            is Meta.NameValue -> this.debug("NameValue")
        }

public fun Meta.PathMeta.debug(): String =
    debugTuple("Path", listOf(path))

public fun ModContent.debug(): String =
    "ModContent::" +
        when (this) {
            is ModContent.Unnamed -> debugTuple("Unnamed", listOf(semiToken))
            is ModContent.Inline -> debugTuple("Inline", listOf(braceToken, items))
        }

public fun Pat.debug(): String =
    "Pat::" +
        when (this) {
            is Pat.Const -> this.debug("Const")
            is Pat.Ident -> this.debug("Ident")
            is Pat.Lit -> this.debug("Lit")
            is Pat.Macro -> this.debug("Macro")
            is Pat.Or -> this.debug("Or")
            is Pat.PatParen -> this.debug("Paren")
            is Pat.Path -> this.debug("Path")
            is Pat.Range -> this.debug("Range")
            is Pat.Reference -> this.debug("Reference")
            is Pat.Rest -> this.debug("Rest")
            is Pat.Slice -> this.debug("Slice")
            is Pat.Struct -> this.debug("Struct")
            is Pat.Tuple -> this.debug("Tuple")
            is Pat.TupleStruct -> this.debug("TupleStruct")
            is Pat.TypeAscription -> this.debug("Type")
            is Pat.Wild -> this.debug("Wild")
            is Pat.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun PathArguments.debug(): String =
    "PathArguments::" +
        when (this) {
            PathArguments.None -> "None"
            is PathArguments.AngleBracketed -> this.debug("AngleBracketed")
            is PathArguments.Parenthesized -> this.debug("Parenthesized")
        }

public fun PointerMutability.debug(): String =
    "PointerMutability::" +
        when (this) {
            is PointerMutability.Const -> debugTuple("Const", listOf(token))
            is PointerMutability.Mut -> debugTuple("Mut", listOf(token))
        }

public fun RangeLimits.debug(): String =
    "RangeLimits::" +
        when (this) {
            is RangeLimits.HalfOpen -> debugTuple("HalfOpen", listOf(token))
            is RangeLimits.Closed -> debugTuple("Closed", listOf(token))
        }

public fun ReturnType.debug(): String =
    "ReturnType::" +
        when (this) {
            ReturnType.Default -> "Default"
            is ReturnType.TypeReturn -> debugTuple("Type", listOf(arrowToken, ty))
        }

public fun StaticMutability.debug(): String =
    "StaticMutability::" +
        when (this) {
            is StaticMutability.Mut -> debugTuple("Mut", listOf(mutToken))
            StaticMutability.None -> "None"
        }

public fun Stmt.debug(): String =
    "Stmt::" +
        when (this) {
            is Stmt.Local -> this.debug("Local")
            is Stmt.ItemStmt -> debugTuple("Item", listOf(item))
            is Stmt.ExprStmt -> debugTuple("Expr", listOf(expr, semiToken))
            is Stmt.MacroStmt -> this.debug("Macro")
        }

public fun Stmt.ItemStmt.debug(): String =
    debugTuple("Item", listOf(item))

public fun Stmt.ExprStmt.debug(): String =
    debugTuple("Expr", listOf(expr, semiToken))

public fun SynType.debug(): String =
    "Type::" +
        when (this) {
            is SynType.Array -> this.debug("Array")
            is SynType.BareFn -> this.debug("BareFn")
            is SynType.Group -> this.debug("Group")
            is SynType.ImplTrait -> this.debug("ImplTrait")
            is SynType.Infer -> this.debug("Infer")
            is SynType.Macro -> this.debug("Macro")
            is SynType.Never -> this.debug("Never")
            is SynType.Paren -> this.debug("Paren")
            is SynType.Path -> this.debug("Path")
            is SynType.Ptr -> this.debug("Ptr")
            is SynType.Reference -> this.debug("Reference")
            is SynType.Slice -> this.debug("Slice")
            is SynType.TraitObject -> this.debug("TraitObject")
            is SynType.Tuple -> this.debug("Tuple")
            is SynType.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun TraitBoundModifier.debug(): String =
    "TraitBoundModifier::" +
        when (this) {
            TraitBoundModifier.None -> "None"
            is TraitBoundModifier.Maybe -> debugTuple("Maybe", listOf(token))
        }

public fun TraitItem.debug(): String =
    "TraitItem::" +
        when (this) {
            is TraitItem.Const -> this.debug("Const")
            is TraitItem.Fn -> this.debug("Fn")
            is TraitItem.AssocType -> this.debug("Type")
            is TraitItem.Macro -> this.debug("Macro")
            is TraitItem.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun TypeParamBound.debug(): String =
    "TypeParamBound::" +
        when (this) {
            is TypeParamBound.Trait -> this.debug("Trait")
            is TypeParamBound.LifetimeBound -> debugTuple("Lifetime", listOf(lifetime))
            is TypeParamBound.PreciseCapture -> this.debug("PreciseCapture")
            is TypeParamBound.Verbatim -> debugTuple("Verbatim", listOf(tokens))
        }

public fun UnOp.debug(): String =
    "UnOp::" +
        when (this) {
            is UnOp.Deref -> debugTuple("Deref", listOf(token))
            is UnOp.NotOp -> debugTuple("Not", listOf(token))
            is UnOp.Neg -> debugTuple("Neg", listOf(token))
        }

public fun UseTree.debug(): String =
    "UseTree::" +
        when (this) {
            is UseTree.Path -> {
                this.debug("Path")
            }

            is UseTree.Name -> {
                val renameVal = rename
                if (renameVal == null) this.debug("Name") else this.debug("Rename")
            }

            is UseTree.Group -> {
                this.debug("Group")
            }

            is UseTree.Glob -> {
                this.debug("Glob")
            }
        }

public fun Visibility.debug(): String =
    "Visibility::" +
        when (this) {
            is Visibility.Public -> debugTuple("Public", listOf(pubToken))
            is Visibility.Restricted -> this.debug("Restricted")
            Visibility.Inherited -> "Inherited"
        }

public fun WherePredicate.debug(): String =
    "WherePredicate::" +
        when (this) {
            is WherePredicate.LifetimePredicate -> this.debug("Lifetime")
            is WherePredicate.TypePredicate -> this.debug("Type")
        }

public fun fmt(value: Any?): String = debugValue(value)
