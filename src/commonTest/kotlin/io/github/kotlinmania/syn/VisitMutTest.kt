package io.github.kotlinmania.syn

import io.github.kotlinmania.syn.gen.VisitMut
import kotlin.test.Test
import kotlin.test.assertTrue

class VisitMutTest {
    @Test
    fun deriveInputVisitMutRecursesThroughDataAndGenerics() {
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

        val visitor = RecordingVisitMut()

        visitor.visitDeriveInput(input)

        visitor.assertEvent("derive:Demo")
        visitor.assertEvent("attr:derive")
        visitor.assertEvent("attr:flag")
        visitor.assertEvent("data:enum")
        visitor.assertEvent("generic:lifetime:'a")
        visitor.assertEvent("generic:type:T")
        visitor.assertEvent("generic:const:N")
        visitor.assertEvent("variant:A")
        visitor.assertEvent("variant:B")
        visitor.assertEvent("variant:C")
        visitor.assertEvent("field:field")
        visitor.assertEvent("field:<unnamed>")
        visitor.assertEvent("where:type")
        visitor.assertEvent("where:lifetime:'a")
        visitor.assertEvent("bound:trait:Clone")
        visitor.assertEvent("bound:lifetime:'a")
        assertTrue(visitor.exprCount >= 2, visitor.dump())
        assertTrue(visitor.events.any { it.startsWith("vis:") && it.contains("crate") && it.contains("m") }, visitor.dump())
        assertTrue(visitor.events.any { it.startsWith("path:") && it.contains("Into") }, visitor.dump())
    }

    @Test
    fun itemVisitMutRecursesThroughTraitImplModuleAndUseTrees() {
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
        val visitor = RecordingVisitMut()

        items.forEach { visitor.visitItem(it) }

        visitor.assertEvent("item:trait:Service")
        visitor.assertEvent("traitItem:const:ID")
        visitor.assertEvent("traitItem:type:Output")
        visitor.assertEvent("traitItem:fn:call")
        visitor.assertEvent("item:impl")
        visitor.assertEvent("implItem:const:ID")
        visitor.assertEvent("implItem:type:Output")
        visitor.assertEvent("implItem:fn:call")
        visitor.assertEvent("item:traitalias:Alias")
        visitor.assertEvent("item:static:COUNT")
        visitor.assertEvent("item:mod:nested")
        visitor.assertEvent("item:macro")
        visitor.assertEvent("item:use")
        visitor.assertEvent("use:rename:alpha:beta")
        visitor.assertEvent("use:glob")
        visitor.assertEvent("item:struct:Inner")
        visitor.assertEvent("pat:arg")
        assertTrue(visitor.events.count { it == "signature:call" } >= 2, visitor.dump())
        assertTrue(visitor.events.count { it == "arg:receiver" } >= 2, visitor.dump())
        assertTrue(visitor.events.count { it == "arg:typed" } >= 2, visitor.dump())
        assertTrue(visitor.events.count { it == "block" } >= 1, visitor.dump())
        assertTrue(visitor.exprCount >= 2, visitor.dump())
    }

    @Test
    fun fileAndTypeVisitMutDispatchesThroughNamedHelpers() {
        val file =
            parseFile(
                """
                #![allow(dead_code)]
                type Alias<T> = *mut T;
                """.trimIndent(),
            ).getOrThrow()
        val types =
            listOf(
                "*mut T",
                "[T; 3]",
                "[T]",
                "(T, U)",
                "_",
                "!",
                "mac!()",
            ).map { parseStr(SynTypeParseExpr, it).getOrThrow() }
        val rawAddress = parseStr(ExprParse, "&raw const place").getOrThrow()
        val visitor = RecordingVisitMut()

        visitor.visitFile(file)
        types.forEach { visitor.visitType(it) }
        visitor.visitExpr(rawAddress)

        visitor.assertEvent("file")
        visitor.assertEvent("item:type:Alias")
        visitor.assertEvent("type:ptr")
        visitor.assertEvent("type:array")
        visitor.assertEvent("type:slice")
        visitor.assertEvent("type:tuple")
        visitor.assertEvent("type:infer")
        visitor.assertEvent("type:never")
        visitor.assertEvent("type:macro")
        visitor.assertEvent("pointer:const")
    }

    @Test
    fun generatedVisitMutHooksAreReachedForGenericArgumentsAttrStyleAndBinOp() {
        val outer =
            parseStr(
                DeriveInputParse,
                """
                #[repr(C)]
                struct S<T>(T);
                """.trimIndent(),
            ).getOrThrow()
        val inner =
            parseFile(
                """
                #![allow(dead_code)]
                type Alias = ();
                """.trimIndent(),
            ).getOrThrow()
        val genericType = parseStr(SynTypeParseExpr, "Iterator<Item = T, LEN = 1, Output: Clone>").getOrThrow()
        val expr = parseStr(ExprParse, "left + right").getOrThrow()
        val visitor = RecordingVisitMut()

        visitor.visitDeriveInput(outer)
        visitor.visitFile(inner)
        visitor.visitType(genericType)
        visitor.visitExpr(expr)

        visitor.assertEvent("attrStyle:outer")
        visitor.assertEvent("attrStyle:inner")
        visitor.assertEvent("angleArgs")
        visitor.assertEvent("genericArg:assocType")
        visitor.assertEvent("genericArg:assocConst")
        visitor.assertEvent("genericArg:constraint")
        visitor.assertEvent("assocType:Item")
        visitor.assertEvent("assocConst:LEN")
        visitor.assertEvent("constraint:Output")
        visitor.assertEvent("binop:add")
    }

    private fun parseItem(source: String): Item =
        parseStr(ItemParse, source).getOrThrow()

    private class RecordingVisitMut : VisitMut() {
        val events = mutableListOf<String>()
        var exprCount = 0

        override fun visitDeriveInput(di: DeriveInput): DeriveInput {
            events += "derive:${di.ident}"
            return super.visitDeriveInput(di)
        }

        override fun visitAttribute(a: Attribute): Attribute {
            events += "attr:${a.path()}"
            return super.visitAttribute(a)
        }

        override fun visitAttrStyle(style: AttrStyle): AttrStyle {
            events +=
                when (style) {
                    AttrStyle.Outer -> "attrStyle:outer"
                    is AttrStyle.Inner -> "attrStyle:inner"
                }
            return super.visitAttrStyle(style)
        }

        override fun visitAngleBracketedGenericArguments(pathArgs: PathArguments.AngleBracketed): PathArguments.AngleBracketed {
            events += "angleArgs"
            return super.visitAngleBracketedGenericArguments(pathArgs)
        }

        override fun visitAssocConst(assoc: AssocConst): AssocConst {
            events += "assocConst:${assoc.ident}"
            return super.visitAssocConst(assoc)
        }

        override fun visitAssocType(assoc: AssocType): AssocType {
            events += "assocType:${assoc.ident}"
            return super.visitAssocType(assoc)
        }

        override fun visitBinOp(op: BinOp): BinOp {
            events +=
                when (op) {
                    is BinOp.Add -> "binop:add"
                    else -> "binop:other"
                }
            return super.visitBinOp(op)
        }

        override fun visitConstraint(constraint: Constraint): Constraint {
            events += "constraint:${constraint.ident}"
            return super.visitConstraint(constraint)
        }

        override fun visitDataEnum(d: DataEnum): DataEnum {
            events += "data:enum"
            return super.visitDataEnum(d)
        }

        override fun visitExpr(e: Expr): Expr {
            exprCount += 1
            return super.visitExpr(e)
        }

        override fun visitField(field: Field): Field {
            events += "field:${field.ident?.toString() ?: "<unnamed>"}"
            return super.visitField(field)
        }

        override fun visitFile(f: File): File {
            events += "file"
            return super.visitFile(f)
        }

        override fun visitFnArg(arg: FnArg): FnArg {
            events +=
                when (arg) {
                    is FnArg.Receiver -> "arg:receiver"
                    is FnArg.Typed -> "arg:typed"
                }
            return super.visitFnArg(arg)
        }

        override fun visitGenericParam(param: GenericParam): GenericParam {
            events +=
                when (param) {
                    is GenericParam.LifetimeParam -> "generic:lifetime:${param.lifetime}"
                    is GenericParam.TypeParam -> "generic:type:${param.ident}"
                    is GenericParam.ConstParam -> "generic:const:${param.ident}"
                }
            return super.visitGenericParam(param)
        }

        override fun visitGenericArgument(genArg: GenericArgument): GenericArgument {
            events +=
                when (genArg) {
                    is GenericArgument.AssocTypeArg -> "genericArg:assocType"
                    is GenericArgument.AssocConstArg -> "genericArg:assocConst"
                    is GenericArgument.ConstraintArg -> "genericArg:constraint"
                    else -> "genericArg:other"
                }
            return super.visitGenericArgument(genArg)
        }

        override fun visitBlock(block: Block): Block {
            events += "block"
            return super.visitBlock(block)
        }

        override fun visitImplItem(item: ImplItem): ImplItem {
            events +=
                when (item) {
                    is ImplItem.Const -> "implItem:const:${item.ident}"
                    is ImplItem.Fn -> "implItem:fn:${item.sig.ident}"
                    is ImplItem.AssocType -> "implItem:type:${item.ident}"
                    is ImplItem.Macro -> "implItem:macro"
                    is ImplItem.Verbatim -> "implItem:verbatim"
                }
            return super.visitImplItem(item)
        }

        override fun visitItem(i: Item): Item {
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
            return super.visitItem(i)
        }

        override fun visitPat(p: Pat): Pat {
            if (p is Pat.Ident) {
                events += "pat:${p.ident}"
            }
            return super.visitPat(p)
        }

        override fun visitPath(p: Path): Path {
            events += "path:$p"
            return super.visitPath(p)
        }

        override fun visitSignature(sig: Signature): Signature {
            events += "signature:${sig.ident}"
            return super.visitSignature(sig)
        }

        override fun visitTraitItem(item: TraitItem): TraitItem {
            events +=
                when (item) {
                    is TraitItem.Const -> "traitItem:const:${item.ident}"
                    is TraitItem.Fn -> "traitItem:fn:${item.sig.ident}"
                    is TraitItem.AssocType -> "traitItem:type:${item.ident}"
                    is TraitItem.Macro -> "traitItem:macro"
                    is TraitItem.Verbatim -> "traitItem:verbatim"
                }
            return super.visitTraitItem(item)
        }

        override fun visitTypeArray(ty: SynType.Array): SynType {
            events += "type:array"
            return super.visitTypeArray(ty)
        }

        override fun visitTypeInfer(ty: SynType.Infer): SynType {
            events += "type:infer"
            return super.visitTypeInfer(ty)
        }

        override fun visitTypeMacro(ty: SynType.Macro): SynType {
            events += "type:macro"
            return super.visitTypeMacro(ty)
        }

        override fun visitTypeNever(ty: SynType.Never): SynType {
            events += "type:never"
            return super.visitTypeNever(ty)
        }

        override fun visitTypePtr(ty: SynType.Ptr): SynType {
            events += "type:ptr"
            return super.visitTypePtr(ty)
        }

        override fun visitTypeSlice(ty: SynType.Slice): SynType {
            events += "type:slice"
            return super.visitTypeSlice(ty)
        }

        override fun visitTypeTuple(ty: SynType.Tuple): SynType {
            events += "type:tuple"
            return super.visitTypeTuple(ty)
        }

        override fun visitPointerMutability(mutability: PointerMutability): PointerMutability {
            events +=
                when (mutability) {
                    is PointerMutability.Const -> "pointer:const"
                    is PointerMutability.Mut -> "pointer:mut"
                }
            return super.visitPointerMutability(mutability)
        }

        override fun visitTypeParamBound(bound: TypeParamBound): TypeParamBound {
            events +=
                when (bound) {
                    is TypeParamBound.Trait -> "bound:trait:${bound.path}"
                    is TypeParamBound.LifetimeBound -> "bound:lifetime:${bound.lifetime}"
                    is TypeParamBound.PreciseCapture -> "bound:precise"
                    is TypeParamBound.Verbatim -> "bound:verbatim"
                }
            return super.visitTypeParamBound(bound)
        }

        override fun visitUseGlob(useTree: UseTree.Glob): UseTree {
            events += "use:glob"
            return super.visitUseGlob(useTree)
        }

        override fun visitUseRename(useTree: UseTree.Name): UseTree {
            events += "use:rename:${useTree.ident}:${useTree.rename?.ident}"
            return super.visitUseRename(useTree)
        }

        override fun visitVisibility(visibility: Visibility): Visibility {
            if (visibility is Visibility.Restricted) {
                events += "vis:${visibility.path}"
            }
            return super.visitVisibility(visibility)
        }

        override fun visitVariant(variant: Variant): Variant {
            events += "variant:${variant.ident}"
            return super.visitVariant(variant)
        }

        override fun visitWherePredicate(wherePredicate: WherePredicate): WherePredicate {
            events +=
                when (wherePredicate) {
                    is WherePredicate.LifetimePredicate -> "where:lifetime:${wherePredicate.lifetime}"
                    is WherePredicate.TypePredicate -> "where:type"
                }
            return super.visitWherePredicate(wherePredicate)
        }

        fun assertEvent(event: String) {
            assertTrue(events.contains(event), dump())
        }

        fun dump(): String =
            events.joinToString(separator = "\n")
    }
}
