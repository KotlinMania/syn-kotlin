package io.github.kotlinmania.syn.rust

/** The fixed error of Rust core::fmt, independent of parsing errors. */
public class RustFmtError

// generic by design: the Result representation retains its success type.
public sealed class RustFmtResult<out T> {
    public class Success<out T>(public val value: T) : RustFmtResult<T>()
    public class Failure<out T>(public val error: RustFmtError) : RustFmtResult<T>()
}

/** The Write operations needed by str and proc_macro2::Ident Display. */
public interface RustFmtWrite {
    public fun writeStr(value: String): RustFmtResult<Unit>

    /** Rust's default Write::write_char calls write_str once with one scalar. */
    public fun writeChar(value: Int): RustFmtResult<Unit> = writeStr(rustScalarString(value))
}

public enum class RustAlignment { Left, Right, Center }

/** Rust stores width and precision in u16; fill is a Unicode scalar. */
public class RustFormatOptions(
    public val width: Int? = null,
    public val precision: Int? = null,
    public val alignment: RustAlignment? = null,
    public val fill: Int = 32,
    public val signPlus: Boolean = false,
    public val signMinus: Boolean = false,
    public val alternate: Boolean = false,
    public val signAwareZeroPad: Boolean = false,
) {
    init {
        require(width == null || width in 0..65535)
        require(precision == null || precision in 0..65535)
        require(fill in 0..0x10ffff && fill !in 0xd800..0xdfff)
    }
}

/**
 * Direct Native contract for core::fmt::Formatter::pad, write_str and write_char.
 * The caller owns the writer. Errors stop at the first failed write; exceptions
 * escape with all previous writes retained. No general format_args interpreter.
 */
public class RustFormatter(
    private val writer: RustFmtWrite,
    public val options: RustFormatOptions = RustFormatOptions(),
) : RustFmtWrite {
    override fun writeStr(value: String): RustFmtResult<Unit> = writer.writeStr(value)
    override fun writeChar(value: Int): RustFmtResult<Unit> = writer.writeChar(value)

    public fun pad(value: String): RustFmtResult<Unit> {
        // Kotlin-only invalid UTF-16 has no Rust str counterpart.
        value.encodeToByteArray(throwOnInvalidSequence = true).let { Unit }
        if (options.width == null && options.precision == null) return writer.writeStr(value)
        var end = 0
        var count = 0
        while (end < value.length && (options.precision == null || count < options.precision)) {
            end += if (value[end].isHighSurrogate()) 2 else 1
            count += 1
        }
        val text = value.substring(0, end)
        val padding = (options.width ?: 0) - count
        if (padding <= 0) return writer.writeStr(text)
        val left = when (options.alignment ?: RustAlignment.Left) {
            RustAlignment.Left -> 0
            RustAlignment.Right -> padding
            RustAlignment.Center -> padding / 2
        }
        repeat(left) {
            val result = writer.writeChar(options.fill)
            if (result is RustFmtResult.Failure) return result
        }
        val result = writer.writeStr(text)
        if (result is RustFmtResult.Failure) return result
        repeat(padding - left) {
            val result = writer.writeChar(options.fill)
            if (result is RustFmtResult.Failure) return result
        }
        return RustFmtResult.Success(Unit)
    }
}

internal fun rustScalarString(value: Int): String {
    require(value in 0..0x10ffff && value !in 0xd800..0xdfff)
    if (value < 0x10000) return value.toChar().toString()
    val supplementary = value - 0x10000
    return charArrayOf((0xd800 + (supplementary shr 10)).toChar(), (0xdc00 + (supplementary and 1023)).toChar()).concatToString()
}
