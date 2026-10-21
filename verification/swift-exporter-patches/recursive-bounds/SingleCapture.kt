package boundfixture

abstract class Flags<B : Flags<B>>(val bits: ULong) {
    abstract fun retain(bits: ULong): B
}

class ConcreteFlags(bits: ULong) : Flags<ConcreteFlags>(bits) {
    override fun retain(bits: ULong): ConcreteFlags = ConcreteFlags(bits)
}

class FlagIterator<B : Flags<B>>(val source: Flags<B>) {
    companion object {
        fun <B : Flags<B>> create(source: Flags<B>): FlagIterator<B> = FlagIterator(source)
        fun <B : Flags<B>> withRemaining(source: B, remaining: B): FlagIterator<B> =
            FlagIterator(source.retain(remaining.bits))
    }
}

fun inferredFactory(source: Flags<*>): FlagIterator<*> = FlagIterator.create(source)
fun main() {
    val flags = ConcreteFlags(7uL)
    check(inferredFactory(flags).source === flags)
    check(inferredFactory(ConcreteFlags(3uL)).source.bits == 3uL)
}
