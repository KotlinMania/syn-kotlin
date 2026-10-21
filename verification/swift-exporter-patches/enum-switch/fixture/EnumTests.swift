import Testing
import EnumFixture

@Test func multipleCaseOrderAndValues() {
    #expect(Multiple.allCases == [.FIRST, .SECOND, .THIRD])
    #expect(Multiple.FIRST.rawValue == 0)
    #expect(Multiple.SECOND.rawValue == 1)
    #expect(Multiple.THIRD.rawValue == 2)
}

@Test func descriptionsAndStringInitializers() {
    #expect(Multiple.FIRST.description == "FIRST")
    #expect(Multiple("SECOND") == .SECOND)
    #expect(Multiple("THIRD") == .THIRD)
    #expect(Multiple("missing") == nil)
    #expect(Multiple("") == nil)
}

@Test func ordinalValidation() {
    #expect(Multiple(rawValue: 0) == .FIRST)
    #expect(Multiple(rawValue: 2) == .THIRD)
    #expect(Multiple(rawValue: -1) == nil)
    #expect(Multiple(rawValue: 3) == nil)
    #expect(Multiple(rawValue: Int32.max) == nil)
    #expect(Multiple(rawValue: Int32.min) == nil)
}

@Test func nativeReferenceRoundTrips() {
    for value in Multiple.allCases {
        #expect(roundTrip(value: value) == value)
    }
}

@Test func singleCaseRoundTripAndInvalidInputs() {
    #expect(Single.allCases == [.ONLY])
    #expect(Single.ONLY.description == "ONLY")
    #expect(Single.ONLY.rawValue == 0)
    #expect(Single("ONLY") == .ONLY)
    #expect(Single("missing") == nil)
    #expect(Single(rawValue: 0) == .ONLY)
    #expect(Single(rawValue: 1) == nil)
    #expect(singleRoundTrip(value: .ONLY) == .ONLY)
}

@Test func emptyEnumRejectsInputs() {
    #expect(Empty.allCases.isEmpty)
    #expect(Empty("missing") == nil)
    #expect(Empty("") == nil)
    #expect(Empty(rawValue: Int32.min) == nil)
    #expect(Empty(rawValue: 1) == nil)
    #expect(Empty(rawValue: 0) == nil)
    #expect(Empty(rawValue: -1) == nil)
    #expect(Empty(rawValue: Int32.max) == nil)
}
