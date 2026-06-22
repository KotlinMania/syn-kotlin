// port-lint: source export.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.procmacro2.TokenStream

/**
 * Re-export convenience aliases for types that live in other modules.
 *
 * Kotlin packages have flat visibility, so the re-exports that the upstream
 * module provides are unnecessary: downstream consumers import directly
 * from the source packages.
 *
 * This file exists for port-lint provenance tracking only.
 */

/**
 * Wraps a function reference with an associated span, used for producing
 * better error messages during proc-macro execution.
 *
 * In Kotlin, function references are already first-class, and span
 * information is carried through the parse buffer.
 */
public class TransparentFn(
    public val fn: (TokenStream) -> TokenStream,
    public val span: Span,
)