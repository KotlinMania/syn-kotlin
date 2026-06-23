// port-lint: tests tests/test_receiver.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReceiverTest {
    private fun receiver(source: String): FnArg.Receiver {
        val item = parserFromFunction(::parseTraitItem).parseStr(source).getOrThrow()
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
        assertNull(receiver.reference)
        assertNull(receiver.mutability)
        assertNotNull(receiver.colonToken)
        assertPathType(receiver.type, "Self")
    }

    @Test
    fun testByMutValue() {
        val receiver = receiver("fn by_mut(mut self: Self);")
        assertNull(receiver.reference)
        assertNotNull(receiver.mutability)
        assertNotNull(receiver.colonToken)
        assertPathType(receiver.type, "Self")
    }

    @Test
    fun testByRef() {
        val receiver = receiver("fn by_ref(self: &Self);")
        assertNull(receiver.reference)
        assertNull(receiver.mutability)
        assertNotNull(receiver.colonToken)
        assertReferenceToSelf(receiver.type)
    }

    @Test
    fun testByBox() {
        val receiver = receiver("fn by_box(self: Box<Self>);")
        assertNull(receiver.reference)
        assertNull(receiver.mutability)
        assertNotNull(receiver.colonToken)
        assertGenericPathType(receiver.type, "Box", "Self")
    }

    @Test
    fun testByPin() {
        val receiver = receiver("fn by_pin(self: Pin<Self>);")
        assertNull(receiver.reference)
        assertNull(receiver.mutability)
        assertNotNull(receiver.colonToken)
        assertGenericPathType(receiver.type, "Pin", "Self")
    }

    @Test
    fun testExplicitType() {
        val receiver = receiver("fn explicit_type(self: Pin<MyType>);")
        assertNull(receiver.reference)
        assertNull(receiver.mutability)
        assertNotNull(receiver.colonToken)
        assertGenericPathType(receiver.type, "Pin", "MyType")
    }

    @Test
    fun testValueShorthand() {
        val receiver = receiver("fn value_shorthand(self);")
        assertNull(receiver.reference)
        assertNull(receiver.mutability)
        assertNull(receiver.colonToken)
        assertPathType(receiver.type, "Self")
    }

    @Test
    fun testMutValueShorthand() {
        val receiver = receiver("fn mut_value_shorthand(mut self);")
        assertNull(receiver.reference)
        assertNotNull(receiver.mutability)
        assertNull(receiver.colonToken)
        assertPathType(receiver.type, "Self")
    }

    @Test
    fun testRefShorthand() {
        val receiver = receiver("fn ref_shorthand(&self);")
        val reference = assertNotNull(receiver.reference)
        assertNull(reference.lifetime)
        assertNull(receiver.mutability)
        assertNull(receiver.colonToken)
        assertReferenceToSelf(receiver.type)
    }

    @Test
    fun testRefShorthandWithLifetime() {
        val receiver = receiver("fn ref_shorthand(&'a self);")
        val reference = assertNotNull(receiver.reference)
        assertEquals("a", reference.lifetime?.ident?.toString())
        assertNull(receiver.mutability)
        assertNull(receiver.colonToken)
        assertReferenceToSelf(receiver.type, lifetime = "a")
    }

    @Test
    fun testRefMutShorthand() {
        val receiver = receiver("fn ref_mut_shorthand(&mut self);")
        val reference = assertNotNull(receiver.reference)
        assertNull(reference.lifetime)
        assertNotNull(receiver.mutability)
        assertNull(receiver.colonToken)
        assertReferenceToSelf(receiver.type, mutable = true)
    }

    @Test
    fun testRefMutShorthandWithLifetime() {
        val receiver = receiver("fn ref_mut_shorthand(&'a mut self);")
        val reference = assertNotNull(receiver.reference)
        assertEquals("a", reference.lifetime?.ident?.toString())
        assertNotNull(receiver.mutability)
        assertNull(receiver.colonToken)
        assertReferenceToSelf(receiver.type, lifetime = "a", mutable = true)
    }
}
