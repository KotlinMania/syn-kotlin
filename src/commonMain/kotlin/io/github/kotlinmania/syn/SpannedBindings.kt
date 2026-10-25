@file:kotlin.jvm.JvmName("SpannedKt")

package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.DelimSpan
import io.github.kotlinmania.procmacro2.Span
import io.github.kotlinmania.quote.ToTokens

/**
 * Kotlin call-site dispatch for the generated Rust blanket implementation. These adapters select
 * checked evidence; the operation lives in generated Spanned.kt.
 */
public fun ToTokens.span(): Span = SynSpannedImpl0(this, SynSpannedBound0.Case2<ToTokens>()).span()

/** Rust's Span implementation returns an independent Copy value. */
public fun Span.span(): Span = SynSpannedImpl0(this, SynSpannedBound0.Case0).span()

/** Rust also implements the bound for delimiter spans. */
public fun DelimSpan.span(): Span = SynSpannedImpl0(this, SynSpannedBound0.Case1).span()
