package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.gen.Fold
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FoldTest {
    @Test
    fun deriveInputFoldRecursesThroughDataAndGenerics() {
        val input =
            parseStr(
                DeriveInputParse,
                """
                #[derive(Clone)]
                pub(in crate::m) enum Demo<'a, T: for<'b> Into + Clone + 'a, const N: usize>
                where
                    T: Clone,
                    'a: 'static,
                {
                    #[flag]
                    A { field: T },
                    B(T),
                    C = N,
                    D = 1,
                }
                """.trimIndent(),
            ).getOrThrow()

        val folder = RecordingFold()

        folder.foldDeriveInput(input)

        folder.assertEvent("derive:Demo")
        folder.assertEvent("attr:derive")
        folder.assertEvent("attr:flag")
        folder.assertEvent("data:enum")
        folder.assertEvent("generic:lifetime:'a")
        folder.assertEvent("generic:type:T")
        folder.assertEvent("generic:const:N")
        folder.assertEvent("param:lifetime:'a")
        folder.assertEvent("param:type:T")
        folder.assertEvent("param:const:N")
        folder.assertEvent("variant:A")
        folder.assertEvent("variant:B")
        folder.assertEvent("variant:C")
        folder.assertEvent("field:field")
        folder.assertEvent("field:<unnamed>")
        folder.assertEvent("where:type")
        folder.assertEvent("where:lifetime:'a")
        folder.assertEvent("predicate:type")
        folder.assertEvent("predicate:lifetime:'a")
        folder.assertEvent("bound:trait:Clone")
        folder.assertEvent("bound:lifetime:'a")
        assertTrue(folder.exprCount >= 2, folder.dump())
        assertTrue(folder.events.any { it.startsWith("vis:") && it.contains("crate") && it.contains("m") }, folder.dump())
        assertTrue(folder.events.any { it.startsWith("path:") && it.contains("Into") }, folder.dump())
    }

    @Test
    fun itemFoldRecursesThroughTraitImplModuleAndUseTrees() {
        val items =
            listOf(
                parseItem(
                    """
                    trait Service<T>: Clone {
                        const ID: usize = 1;
                        type Output = T;
                        fn call(self, arg: T) -> T;
                    }
                    """.trimIndent(),
                ),
                parseItem(
                    """
                    impl<T> Service<T> for Worker<T> {
                        const ID: usize = 2;
                        type Output = T;
                        fn call(self, arg: T) -> T {}
                    }
                    """.trimIndent(),
                ),
                parseItem("trait Alias<T> = Iterator<Item = T> + Send;"),
                parseItem("static mut COUNT: usize = 0;"),
                parseItem(
                    """
                    mod nested {
                        helper!();
                        use crate::{alpha as beta, gamma::*};
                        struct Inner<T>(T);
                    }
                    """.trimIndent(),
                ),
            )
        val folder = RecordingFold()

        items.forEach { folder.foldItem(it) }

        folder.assertEvent("item:trait:Service")
        folder.assertEvent("traitItem:const:ID")
        folder.assertEvent("traitItem:type:Output")
        folder.assertEvent("traitItem:fn:call")
        folder.assertEvent("item:impl")
        folder.assertEvent("implItem:const:ID")
        folder.assertEvent("implItem:type:Output")
        folder.assertEvent("implItem:fn:call")
        folder.assertEvent("item:traitalias:Alias")
        folder.assertEvent("item:static:COUNT")
        folder.assertEvent("item:mod:nested")
        folder.assertEvent("item:macro")
        folder.assertEvent("item:use")
        folder.assertEvent("use:rename:alpha:beta")
        folder.assertEvent("use:glob")
        folder.assertEvent("item:struct:Inner")
        folder.assertEvent("pat:arg")
        assertTrue(folder.events.count { it == "signature:call" } >= 2, folder.dump())
        assertTrue(folder.events.count { it == "arg:receiver" } >= 2, folder.dump())
        assertTrue(folder.events.count { it == "arg:typed" } >= 2, folder.dump())
        assertTrue(folder.events.count { it == "block" } >= 1, folder.dump())
        assertTrue(folder.exprCount >= 2, folder.dump())
    }

    @Test
    fun genericArgumentFoldDispatchesThroughNamedHelpers() {
        val associatedArguments =
            parseStr(
                SynTypeParseExpr,
                "Iterator<Item = T, N = 1, Bound: Display>",
            ).getOrThrow()
        val parenthesizedArguments =
            parseStr(
                TypeParamBoundParse,
                "FnOnce(T) -> U",
            ).getOrThrow()
        val folder = RecordingFold()

        folder.foldType(associatedArguments)
        folder.foldTypeParamBound(parenthesizedArguments)

        folder.assertEvent("args:angle")
        folder.assertEvent("assoc:type:Item")
        folder.assertEvent("assoc:const:N")
        folder.assertEvent("constraint:Bound")
        folder.assertEvent("args:paren")
    }

    @Test
    fun exprAndPatternFoldDispatchesThroughGeneratedHelpers() {
        val expressions =
            listOf(
                parseStr(ExprParse, "a + b").getOrThrow(),
                parseStr(ExprParse, "!flag").getOrThrow(),
                parseStr(ExprParse, "a..=b").getOrThrow(),
                parseStr(ExprParse, "&raw mut place").getOrThrow(),
                parseStr(ExprParse, "'outer: loop {}").getOrThrow(),
            )
        val patterns =
            listOf(
                parsePat("A | B"),
                parsePat("(A | B)"),
                parsePat("&mut value"),
                parsePat("[a, b]"),
                parsePat("Point { x, .. }"),
                parsePat("(a, b)"),
                parsePat("Point(a, ..)"),
                parsePat(".."),
                parsePat("_"),
                parsePat("1..=2"),
            )
        val folder = RecordingFold()

        expressions.forEach { folder.foldExpr(it) }
        patterns.forEach { folder.foldPat(it) }

        folder.assertEvent("expr:binary")
        folder.assertEvent("expr:unary")
        folder.assertEvent("expr:range")
        folder.assertEvent("expr:rawaddr")
        folder.assertEvent("expr:loop")
        folder.assertEvent("binop:Add")
        folder.assertEvent("unop:NotOp")
        folder.assertEvent("range:closed")
        folder.assertEvent("pointer:mut")
        folder.assertEvent("label:'outer")
        folder.assertEvent("pat:or")
        folder.assertEvent("pat:paren")
        folder.assertEvent("pat:reference")
        folder.assertEvent("pat:rest")
        folder.assertEvent("pat:slice")
        folder.assertEvent("pat:struct")
        folder.assertEvent("pat:tuple")
        folder.assertEvent("pat:tupleStruct")
        folder.assertEvent("pat:wild")
        assertTrue(folder.events.count { it == "range:closed" } >= 2, folder.dump())
    }

    @Test
    fun foldReturnsRewrittenTree() {
        val input =
            parseStr(
                DeriveInputParse,
                "pub struct Demo<T> { field: T }",
            ).getOrThrow()

        val folded = RenamingFold().foldDeriveInput(input)

        assertEquals("Renamed", folded.ident.toString())
        val fields = assertIs<Fields.Named>(assertIs<Data.Struct>(folded.data).value.fields).fields.named.toList()
        assertEquals("renamedField", fields.single().ident?.toString())
    }

    private fun parseItem(source: String): Item =
        parseStr(ItemParse, source).getOrThrow()

    private fun parsePat(source: String): Pat =
        parserFromFunction(Pat.Companion::parseMulti).parseStr(source).getOrThrow()

    private class RenamingFold : Fold() {
        override fun foldIdent(id: Ident): Ident =
            when (id.toString()) {
                "Demo" -> Ident.new("Renamed", id.span())
                "field" -> Ident.new("renamedField", id.span())
                else -> super.foldIdent(id)
            }
    }

    private class RecordingFold : Fold() {
        val events = mutableListOf<String>()
        var exprCount = 0

        override fun foldDeriveInput(di: DeriveInput): DeriveInput {
            events += "derive:${di.ident}"
            return super.foldDeriveInput(di)
        }

        override fun foldAttribute(a: Attribute): Attribute {
            events += "attr:${a.path()}"
            return super.foldAttribute(a)
        }

        override fun foldDataEnum(d: DataEnum): DataEnum {
            events += "data:enum"
            return super.foldDataEnum(d)
        }

        override fun foldAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed {
            events += "args:angle"
            return super.foldAngleBracketedGenericArguments(pathArgs)
        }

        override fun foldAssocConst(assoc: AssocConst): AssocConst {
            events += "assoc:const:${assoc.ident}"
            return super.foldAssocConst(assoc)
        }

        override fun foldAssocType(assoc: AssocType): AssocType {
            events += "assoc:type:${assoc.ident}"
            return super.foldAssocType(assoc)
        }

        override fun foldConstraint(constraint: Constraint): Constraint {
            events += "constraint:${constraint.ident}"
            return super.foldConstraint(constraint)
        }

        override fun foldExpr(e: Expr): Expr {
            exprCount += 1
            return super.foldExpr(e)
        }

        override fun foldField(field: Field): Field {
            events += "field:${field.ident?.toString() ?: "<unnamed>"}"
            return super.foldField(field)
        }

        override fun foldFnArg(arg: FnArg): FnArg {
            events +=
                when (arg) {
                    is FnArg.Receiver -> "arg:receiver"
                    is FnArg.Typed -> "arg:typed"
                }
            return super.foldFnArg(arg)
        }

        override fun foldGenericParam(param: GenericParam): GenericParam {
            events +=
                when (param) {
                    is GenericParam.LifetimeParam -> "generic:lifetime:${param.lifetime}"
                    is GenericParam.TypeParam -> "generic:type:${param.ident}"
                    is GenericParam.ConstParam -> "generic:const:${param.ident}"
                }
            return super.foldGenericParam(param)
        }

        override fun foldConstParam(param: GenericParam.ConstParam): GenericParam.ConstParam {
            events += "param:const:${param.ident}"
            return super.foldConstParam(param)
        }

        override fun foldLifetimeParam(param: GenericParam.LifetimeParam): GenericParam.LifetimeParam {
            events += "param:lifetime:${param.lifetime}"
            return super.foldLifetimeParam(param)
        }

        override fun foldTypeParam(param: GenericParam.TypeParam): GenericParam.TypeParam {
            events += "param:type:${param.ident}"
            return super.foldTypeParam(param)
        }

        override fun foldBlock(block: Block): Block {
            events += "block"
            return super.foldBlock(block)
        }

        override fun foldBinOp(op: BinOp): BinOp {
            events += "binop:${op::class.simpleName}"
            return super.foldBinOp(op)
        }

        override fun foldExprBinary(expr: Expr.Binary): Expr.Binary {
            events += "expr:binary"
            return super.foldExprBinary(expr)
        }

        override fun foldExprLoop(expr: Expr.Loop): Expr.Loop {
            events += "expr:loop"
            return super.foldExprLoop(expr)
        }

        override fun foldExprRange(expr: Expr.Range): Expr.Range {
            events += "expr:range"
            return super.foldExprRange(expr)
        }

        override fun foldExprRawAddr(expr: Expr.RawAddr): Expr.RawAddr {
            events += "expr:rawaddr"
            return super.foldExprRawAddr(expr)
        }

        override fun foldExprUnary(expr: Expr.Unary): Expr.Unary {
            events += "expr:unary"
            return super.foldExprUnary(expr)
        }

        override fun foldImplItem(item: ImplItem): ImplItem {
            events +=
                when (item) {
                    is ImplItem.Const -> "implItem:const:${item.ident}"
                    is ImplItem.Fn -> "implItem:fn:${item.sig.ident}"
                    is ImplItem.AssocType -> "implItem:type:${item.ident}"
                    is ImplItem.Macro -> "implItem:macro"
                    is ImplItem.Verbatim -> "implItem:verbatim"
                }
            return super.foldImplItem(item)
        }

        override fun foldItem(i: Item): Item {
            events +=
                when (i) {
                    is Item.Const -> "item:const:${i.ident}"
                    is Item.Enum -> "item:enum:${i.ident}"
                    is Item.Fn -> "item:fn:${i.ident}"
                    is Item.Impl -> "item:impl"
                    is Item.Macro -> "item:macro"
                    is Item.Mod -> "item:mod:${i.ident}"
                    is Item.Static -> "item:static:${i.ident}"
                    is Item.Struct -> "item:struct:${i.ident}"
                    is Item.Trait -> "item:trait:${i.ident}"
                    is Item.TraitAlias -> "item:traitalias:${i.ident}"
                    is Item.ItemType -> "item:type:${i.ident}"
                    is Item.Union -> "item:union:${i.ident}"
                    is Item.Use -> "item:use"
                    is Item.Verbatim -> "item:verbatim"
                }
            return super.foldItem(i)
        }

        override fun foldPat(p: Pat): Pat {
            if (p is Pat.Ident) {
                events += "pat:${p.ident}"
            }
            return super.foldPat(p)
        }

        override fun foldLabel(label: Label): Label {
            events += "label:${label.name}"
            return super.foldLabel(label)
        }

        override fun foldPatOr(pat: Pat.Or): Pat.Or {
            events += "pat:or"
            return super.foldPatOr(pat)
        }

        override fun foldPatParen(pat: Pat.PatParen): Pat.PatParen {
            events += "pat:paren"
            return super.foldPatParen(pat)
        }

        override fun foldPatReference(pat: Pat.Reference): Pat.Reference {
            events += "pat:reference"
            return super.foldPatReference(pat)
        }

        override fun foldPatRest(pat: Pat.Rest): Pat.Rest {
            events += "pat:rest"
            return super.foldPatRest(pat)
        }

        override fun foldPatRest(patRest: PatRest): PatRest {
            events += "pat:rest"
            return super.foldPatRest(patRest)
        }

        override fun foldPatSlice(pat: Pat.Slice): Pat.Slice {
            events += "pat:slice"
            return super.foldPatSlice(pat)
        }

        override fun foldPatStruct(pat: Pat.Struct): Pat.Struct {
            events += "pat:struct"
            return super.foldPatStruct(pat)
        }

        override fun foldPatTuple(pat: Pat.Tuple): Pat.Tuple {
            events += "pat:tuple"
            return super.foldPatTuple(pat)
        }

        override fun foldPatTupleStruct(pat: Pat.TupleStruct): Pat.TupleStruct {
            events += "pat:tupleStruct"
            return super.foldPatTupleStruct(pat)
        }

        override fun foldPatWild(pat: Pat.Wild): Pat.Wild {
            events += "pat:wild"
            return super.foldPatWild(pat)
        }

        override fun foldPath(p: Path): Path {
            events += "path:$p"
            return super.foldPath(p)
        }

        override fun foldParenthesizedGenericArguments(pathArgs: PathArguments.Parenthesized): PathArguments.Parenthesized {
            events += "args:paren"
            return super.foldParenthesizedGenericArguments(pathArgs)
        }

        override fun foldPointerMutability(mutability: PointerMutability): PointerMutability {
            events +=
                when (mutability) {
                    is PointerMutability.Const -> "pointer:const"
                    is PointerMutability.Mut -> "pointer:mut"
                }
            return super.foldPointerMutability(mutability)
        }

        override fun foldPredicateLifetime(predicate: WherePredicate.LifetimePredicate): WherePredicate.LifetimePredicate {
            events += "predicate:lifetime:${predicate.lifetime}"
            return super.foldPredicateLifetime(predicate)
        }

        override fun foldPredicateType(predicate: WherePredicate.TypePredicate): WherePredicate.TypePredicate {
            events += "predicate:type"
            return super.foldPredicateType(predicate)
        }

        override fun foldSignature(sig: Signature): Signature {
            events += "signature:${sig.ident}"
            return super.foldSignature(sig)
        }

        override fun foldRangeLimits(limits: RangeLimits): RangeLimits {
            events +=
                when (limits) {
                    is RangeLimits.HalfOpen -> "range:halfOpen"
                    is RangeLimits.Closed -> "range:closed"
                }
            return super.foldRangeLimits(limits)
        }

        override fun foldTraitItem(item: TraitItem): TraitItem {
            events +=
                when (item) {
                    is TraitItem.Const -> "traitItem:const:${item.ident}"
                    is TraitItem.Fn -> "traitItem:fn:${item.sig.ident}"
                    is TraitItem.AssocType -> "traitItem:type:${item.ident}"
                    is TraitItem.Macro -> "traitItem:macro"
                    is TraitItem.Verbatim -> "traitItem:verbatim"
                }
            return super.foldTraitItem(item)
        }

        override fun foldTypeParamBound(bound: TypeParamBound): TypeParamBound {
            events +=
                when (bound) {
                    is TypeParamBound.Trait -> "bound:trait:${bound.path}"
                    is TypeParamBound.LifetimeBound -> "bound:lifetime:${bound.lifetime}"
                    is TypeParamBound.PreciseCapture -> "bound:precise"
                    is TypeParamBound.Verbatim -> "bound:verbatim"
                }
            return super.foldTypeParamBound(bound)
        }

        override fun foldUnOp(op: UnOp): UnOp {
            events += "unop:${op::class.simpleName}"
            return super.foldUnOp(op)
        }

        override fun foldUseGlob(useTree: UseTree.Glob): UseTree.Glob {
            events += "use:glob"
            return super.foldUseGlob(useTree)
        }

        override fun foldUseRename(useTree: UseTree.Name): UseTree.Name {
            events += "use:rename:${useTree.ident}:${useTree.rename?.ident}"
            return super.foldUseRename(useTree)
        }

        override fun foldVisibility(visibility: Visibility): Visibility {
            if (visibility is Visibility.Restricted) {
                events += "vis:${visibility.path}"
            }
            return super.foldVisibility(visibility)
        }

        override fun foldVariant(variant: Variant): Variant {
            events += "variant:${variant.ident}"
            return super.foldVariant(variant)
        }

        override fun foldWherePredicate(wherePredicate: WherePredicate): WherePredicate {
            events +=
                when (wherePredicate) {
                    is WherePredicate.LifetimePredicate -> "where:lifetime:${wherePredicate.lifetime}"
                    is WherePredicate.TypePredicate -> "where:type"
                }
            return super.foldWherePredicate(wherePredicate)
        }

        fun assertEvent(event: String) {
            assertTrue(events.contains(event), dump())
        }

        fun dump(): String =
            events.joinToString(separator = "\n")
    }
}
