// port-lint: source export.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span

public typealias Formatter = Any

public typealias FmtResult = Unit

/**
 * Wraps a token-stream transformation with an associated span, used for producing
 * better error messages during proc-macro execution.
 */
public class TransparentFn(
    public val fn: TokenStreamTransform,
    public val span: Span,
)
