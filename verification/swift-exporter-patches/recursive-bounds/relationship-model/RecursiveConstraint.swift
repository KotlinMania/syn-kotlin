public class Flags<B: Flags<B>> {
    public let bits: UInt64
    public init(bits: UInt64) { self.bits = bits }
}

public final class ConcreteFlags: Flags<ConcreteFlags> {}

public final class FlagIterator<B: Flags<B>> {
    public let source: B
    public let remaining: B
    public init(source: B, remaining: B) {
        self.source = source
        self.remaining = remaining
    }
}

public func withRemaining<B: Flags<B>>(source: B, remaining: B) -> FlagIterator<B> {
    FlagIterator(source: source, remaining: remaining)
}
