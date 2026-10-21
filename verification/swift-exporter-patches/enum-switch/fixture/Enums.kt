package enumfixture

enum class Empty

enum class Single {
    ONLY,
}

enum class Multiple {
    FIRST,
    SECOND,
    THIRD,
}

fun roundTrip(value: Multiple): Multiple = value

fun singleRoundTrip(value: Single): Single = value
