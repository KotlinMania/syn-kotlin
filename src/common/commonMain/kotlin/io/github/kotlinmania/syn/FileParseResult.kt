package io.github.kotlinmania.syn

/** A parsed source file or its diagnostic. Exactly one of [value] and [error] is present. */
public class FileParseResult private constructor(
    public val value: File?,
    public val error: SynError?,
) {
    public val isSuccess: Boolean
        get() = value != null

    public val isFailure: Boolean
        get() = error != null

    public fun getOrNull(): File? = value

    public fun exceptionOrNull(): SynError? = error

    /** Returns the file or raises an internal argument exception carrying its diagnostic. */
    public fun getOrThrow(): File = value ?: requireNotNull(error).raise()

    public companion object {
        public fun success(value: File): FileParseResult = FileParseResult(value, null)

        public fun failure(error: SynError): FileParseResult = FileParseResult(null, error)
    }
}

internal fun SynResult<File>.toFileParseResult(): FileParseResult =
    when (this) {
        is SynResult.Success -> FileParseResult.success(value)
        is SynResult.Failure -> FileParseResult.failure(error)
    }
