package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.syn.gen.Visit
import kotlin.test.Test
import kotlin.test.assertTrue

class VisitTest {
    @Test
    fun deriveInputVisitRecursesThroughDataAndGenerics() {
        val input =
            parseStr(
                DeriveInputParse::parse,
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

        val visitor = RecordingVisit()

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
    fun itemVisitRecursesThroughTraitImplModuleAndUseTrees() {
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
                parseItem("extern crate alloc as memory;"),
                parseItem(
                    """
                    extern "C" {
                        pub fn puts(s: *const c_char);
                        static errno: i32;
                        type Opaque;
                        callback!();
                    }
                    """.trimIndent(),
                ),
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
        val visitor = RecordingVisit()

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
        visitor.assertEvent("item:externcrate:alloc")
        visitor.assertEvent("item:foreignmod")
        visitor.assertEvent("foreignItem:fn:puts")
        visitor.assertEvent("foreignItem:static:errno")
        visitor.assertEvent("foreignItem:type:Opaque")
        visitor.assertEvent("foreignItem:macro")
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
    fun fileAndTypeVisitDispatchesThroughNamedHelpers() {
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
            ).map { parseStr(SynTypeParseExpr::parse, it).getOrThrow() }
        val visitor = RecordingVisit()

        visitor.visitFile(file)
        types.forEach { visitor.visitType(it) }

        visitor.assertEvent("file")
        visitor.assertEvent("item:type:Alias")
        visitor.assertEvent("type:ptr")
        visitor.assertEvent("pointer:mut")
        visitor.assertEvent("type:array")
        visitor.assertEvent("type:slice")
        visitor.assertEvent("type:tuple")
        visitor.assertEvent("type:infer")
        visitor.assertEvent("type:never")
        visitor.assertEvent("type:macro")
    }

    @Test
    fun nonExprVisitHooksDispatchThroughGeneratedHelpers() {
        val visitor = RecordingVisit()
        val span = Span.callSite()

        visitor.visitPat(parsePat("1..=2"))
        visitor.visitLabel(Label(Lifetime.new("'lbl", span), io.github.kotlinmania.syn.token.Colon.from(span)))
        visitor.visitUnOp(UnOp.NotOp(io.github.kotlinmania.syn.token.Not.from(span)))
        visitor.visitLit(parseStr(LitParse::parse, "c\"hello\"").getOrThrow())
        visitor.visitType(parseStr(SynTypeParseExpr::parse, "<Self as Trait>::Assoc").getOrThrow())

        visitor.assertEvent("range:closed")
        visitor.assertEvent("label:'lbl")
        visitor.assertEvent("unop:not")
        visitor.assertEvent("lit:cstr")
        visitor.assertEvent("qself")
    }

    private fun parseItem(source: String): Item =
        parseStr(ItemParse::parse, source).getOrThrow()

    private fun parsePat(source: String): Pat =
        parserFromFunction(Pat.Companion::parseMulti).parseStr(source).getOrThrow()

    private class RecordingVisit : Visit() {
        val events = mutableListOf<String>()
        var exprCount = 0

        override fun visitDeriveInput(di: DeriveInput) {
            events += "derive:${di.ident}"
            super.visitDeriveInput(di)
        }

        override fun visitAttribute(a: Attribute) {
            events += "attr:${a.path()}"
            super.visitAttribute(a)
        }

        override fun visitDataEnum(d: DataEnum) {
            events += "data:enum"
            super.visitDataEnum(d)
        }

        override fun visitExpr(e: Expr) {
            exprCount += 1
            super.visitExpr(e)
        }

        override fun visitField(f: Field) {
            events += "field:${f.ident?.toString() ?: "<unnamed>"}"
            super.visitField(f)
        }

        override fun visitFile(f: File) {
            events += "file"
            super.visitFile(f)
        }

        override fun visitFnArg(a: FnArg) {
            events +=
                when (a) {
                    is FnArg.Receiver -> "arg:receiver"
                    is FnArg.Typed -> "arg:typed"
                }
            super.visitFnArg(a)
        }

        override fun visitGenericParam(g: GenericParam) {
            events +=
                when (g) {
                    is GenericParam.LifetimeParam -> "generic:lifetime:${g.lifetime}"
                    is GenericParam.TypeParam -> "generic:type:${g.ident}"
                    is GenericParam.ConstParam -> "generic:const:${g.ident}"
                }
            super.visitGenericParam(g)
        }

        override fun visitBlock(b: Block) {
            events += "block"
            super.visitBlock(b)
        }

        override fun visitImplItem(i: ImplItem) {
            events +=
                when (i) {
                    is ImplItem.Const -> "implItem:const:${i.ident}"
                    is ImplItem.Fn -> "implItem:fn:${i.sig.ident}"
                    is ImplItem.AssocType -> "implItem:type:${i.ident}"
                    is ImplItem.Macro -> "implItem:macro"
                    is ImplItem.Verbatim -> "implItem:verbatim"
                }
            super.visitImplItem(i)
        }

        override fun visitForeignItem(i: ForeignItem) {
            events +=
                when (i) {
                    is ForeignItem.Fn -> "foreignItem:fn:${i.sig.ident}"
                    is ForeignItem.Static -> "foreignItem:static:${i.ident}"
                    is ForeignItem.ItemType -> "foreignItem:type:${i.ident}"
                    is ForeignItem.Macro -> "foreignItem:macro"
                    is ForeignItem.Verbatim -> "foreignItem:verbatim"
                }
            super.visitForeignItem(i)
        }

        override fun visitItem(i: Item) {
            events +=
                when (i) {
                    is Item.Const -> "item:const:${i.ident}"
                    is Item.Enum -> "item:enum:${i.ident}"
                    is Item.ExternCrate -> "item:externcrate:${i.ident}"
                    is Item.Fn -> "item:fn:${i.ident}"
                    is Item.ForeignMod -> "item:foreignmod"
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
            super.visitItem(i)
        }

        override fun visitPat(p: Pat) {
            if (p is Pat.Ident) {
                events += "pat:${p.ident}"
            }
            super.visitPat(p)
        }

        override fun visitPath(p: Path) {
            events += "path:$p"
            super.visitPath(p)
        }

        override fun visitLitCstr(l: LitCStr) {
            events += "lit:cstr"
            super.visitLitCstr(l)
        }

        override fun visitLabel(label: Label) {
            events += "label:${label.name}"
            super.visitLabel(label)
        }

        override fun visitQself(q: QSelf) {
            events += "qself"
            super.visitQself(q)
        }

        override fun visitRangeLimits(limits: RangeLimits) {
            events +=
                when (limits) {
                    is RangeLimits.HalfOpen -> "range:halfOpen"
                    is RangeLimits.Closed -> "range:closed"
                }
            super.visitRangeLimits(limits)
        }

        override fun visitSignature(s: Signature) {
            events += "signature:${s.ident}"
            super.visitSignature(s)
        }

        override fun visitTraitItem(t: TraitItem) {
            events +=
                when (t) {
                    is TraitItem.Const -> "traitItem:const:${t.ident}"
                    is TraitItem.Fn -> "traitItem:fn:${t.sig.ident}"
                    is TraitItem.AssocType -> "traitItem:type:${t.ident}"
                    is TraitItem.Macro -> "traitItem:macro"
                    is TraitItem.Verbatim -> "traitItem:verbatim"
                }
            super.visitTraitItem(t)
        }

        override fun visitTypeParamBound(t: TypeParamBound) {
            events +=
                when (t) {
                    is TypeParamBound.Trait -> "bound:trait:${t.path}"
                    is TypeParamBound.LifetimeBound -> "bound:lifetime:${t.lifetime}"
                    is TypeParamBound.PreciseCapture -> "bound:precise"
                    is TypeParamBound.Verbatim -> "bound:verbatim"
                }
            super.visitTypeParamBound(t)
        }

        override fun visitTypeArray(t: SynType.Array) {
            events += "type:array"
            super.visitTypeArray(t)
        }

        override fun visitTypeInfer(t: SynType.Infer) {
            events += "type:infer"
            super.visitTypeInfer(t)
        }

        override fun visitTypeMacro(t: SynType.Macro) {
            events += "type:macro"
            super.visitTypeMacro(t)
        }

        override fun visitTypeNever(t: SynType.Never) {
            events += "type:never"
            super.visitTypeNever(t)
        }

        override fun visitTypePtr(t: SynType.Ptr) {
            events += "type:ptr"
            super.visitTypePtr(t)
        }

        override fun visitTypeSlice(t: SynType.Slice) {
            events += "type:slice"
            super.visitTypeSlice(t)
        }

        override fun visitTypeTuple(t: SynType.Tuple) {
            events += "type:tuple"
            super.visitTypeTuple(t)
        }

        override fun visitUnOp(op: UnOp) {
            events +=
                when (op) {
                    is UnOp.Deref -> "unop:deref"
                    is UnOp.NotOp -> "unop:not"
                    is UnOp.Neg -> "unop:neg"
                }
            super.visitUnOp(op)
        }

        override fun visitPointerMutability(mutability: io.github.kotlinmania.syn.token.Mut?) {
            events += if (mutability == null) "pointer:const" else "pointer:mut"
            super.visitPointerMutability(mutability)
        }

        override fun visitUseGlob(u: UseTree.Glob) {
            events += "use:glob"
            super.visitUseGlob(u)
        }

        override fun visitUseRename(u: UseTree.Name) {
            events += "use:rename:${u.ident}:${u.rename?.ident}"
            super.visitUseRename(u)
        }

        override fun visitVisibility(v: Visibility) {
            if (v is Visibility.Restricted) {
                events += "vis:${v.path}"
            }
            super.visitVisibility(v)
        }

        override fun visitVariant(v: Variant) {
            events += "variant:${v.ident}"
            super.visitVariant(v)
        }

        override fun visitWherePredicate(w: WherePredicate) {
            events +=
                when (w) {
                    is WherePredicate.LifetimePredicate -> "where:lifetime:${w.lifetime}"
                    is WherePredicate.TypePredicate -> "where:type"
                }
            super.visitWherePredicate(w)
        }

        fun assertEvent(event: String) {
            assertTrue(events.contains(event), dump())
        }

        fun dump(): String =
            events.joinToString(separator = "\n")
    }
}
