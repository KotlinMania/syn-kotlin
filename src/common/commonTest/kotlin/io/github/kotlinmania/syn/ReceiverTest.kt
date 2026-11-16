// port-lint: tests tests/test_receiver.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReceiverTest {
    private fun receiver(source: String): FnArg.Receiver {
        val item = parseStr(::parseTraitItem, source).getOrThrow()
        val fn = assertIs<TraitItem.Fn>(item)
        return assertIs<FnArg.Receiver>(fn.sig.inputs.first())
    }

    private fun assertPathType(type: SynType, ident: String): Path {
        val path = assertIs<SynType.Path>(type).path
        assertEquals(1, path.segments.len())
        val segment = path.segments.first()
        assertNotNull(segment)
        assertEquals(ident, segment.ident.toString())
        return path
    }

    private fun assertGenericPathType(type: SynType, outer: String, inner: String) {
        val path = assertPathType(type, outer)
        val segment = path.segments.first()
        assertNotNull(segment)
        val arguments = assertIs<PathArguments.AngleBracketed>(segment.arguments)
        val arg = assertIs<GenericArgument.TypeArg>(arguments.args.toList().single())
        assertPathType(arg.type, inner)
    }

    private fun assertReferenceToSelf(type: SynType, lifetime: String? = null, mutable: Boolean = false) {
        val reference = assertIs<SynType.Reference>(type)
        assertEquals(lifetime, reference.lifetime?.ident?.toString())
        if (mutable) {
            assertNotNull(reference.mutability)
        } else {
            assertNull(reference.mutability)
        }
        assertPathType(reference.elem, "Self")
    }

    @Test
    fun testByValue() {
        val receiver = receiver("fn by_value(self: Self);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Typed>(receiver.kind)
        assertPathType(kind.ty, "Self")
    }

    @Test
    fun testByMutValue() {
        val receiver = receiver("fn by_mut(mut self: Self);")
        assertNotNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Typed>(receiver.kind)
        assertPathType(kind.ty, "Self")
    }

    @Test
    fun testByRef() {
        val receiver = receiver("fn by_ref(self: &Self);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Typed>(receiver.kind)
        assertReferenceToSelf(kind.ty)
    }

    @Test
    fun testByBox() {
        val receiver = receiver("fn by_box(self: Box<Self>);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Typed>(receiver.kind)
        assertGenericPathType(kind.ty, "Box", "Self")
    }

    @Test
    fun testByPin() {
        val receiver = receiver("fn by_pin(self: Pin<Self>);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Typed>(receiver.kind)
        assertGenericPathType(kind.ty, "Pin", "Self")
    }

    @Test
    fun testExplicitType() {
        val receiver = receiver("fn explicit_type(self: Pin<MyType>);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Typed>(receiver.kind)
        assertGenericPathType(kind.ty, "Pin", "MyType")
    }

    @Test
    fun testValueShorthand() {
        val receiver = receiver("fn value_shorthand(self);")
        assertNull(receiver.mutability)
        assertEquals(ReceiverKind.Value, receiver.kind)
    }

    @Test
    fun testMutValueShorthand() {
        val receiver = receiver("fn mut_value_shorthand(mut self);")
        assertNotNull(receiver.mutability)
        assertEquals(ReceiverKind.Value, receiver.kind)
    }

    @Test
    fun testRefShorthand() {
        val receiver = receiver("fn ref_shorthand(&self);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Reference>(receiver.kind)
        assertNull(kind.lifetime)
        assertNull(kind.mutability)
    }

    @Test
    fun testRefShorthandWithLifetime() {
        val receiver = receiver("fn ref_shorthand(&'a self);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Reference>(receiver.kind)
        assertEquals("a", kind.lifetime?.ident?.toString())
        assertNull(kind.mutability)
    }

    @Test
    fun testRefMutShorthand() {
        val receiver = receiver("fn ref_mut_shorthand(&mut self);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Reference>(receiver.kind)
        assertNull(kind.lifetime)
        assertNotNull(kind.mutability)
    }

    @Test
    fun testRefMutShorthandWithLifetime() {
        val receiver = receiver("fn ref_mut_shorthand(&'a mut self);")
        assertNull(receiver.mutability)
        val kind = assertIs<ReceiverKind.Reference>(receiver.kind)
        assertEquals("a", kind.lifetime?.ident?.toString())
        assertNotNull(kind.mutability)
    }
}
