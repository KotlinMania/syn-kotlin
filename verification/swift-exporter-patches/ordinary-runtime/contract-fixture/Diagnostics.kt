package runtimediagnostic

private class MutableDiagnostic(initial: String?) : Exception() {
    var text: String? = initial
    override val message: String? get() = text
}

fun createDiagnostic(message: String?): Any = MutableDiagnostic(message)

fun changeDiagnosticMessage(diagnostic: Any, message: String?) {
    (diagnostic as MutableDiagnostic).text = message
}

fun createPlainValue(): Any = Any()
