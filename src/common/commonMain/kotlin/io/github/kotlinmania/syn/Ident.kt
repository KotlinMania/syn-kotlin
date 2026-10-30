// port-lint: source ident.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.Ident
import io.github.kotlinmania.syn.generated.ident.__rtkImpl1
import io.github.kotlinmania.syn.generated.ident.__rtkImpl2
import io.github.kotlinmania.syn.generated.ident.__rtkImpl3
import io.github.kotlinmania.syn.generated.ident.__rtkImpl4
import io.github.kotlinmania.syn.generated.ident.__rtkImpl5
import io.github.kotlinmania.syn.generated.ident.__rtkImpl6
import io.github.kotlinmania.syn.generated.ident.__rtkImpl7
import io.github.kotlinmania.syn.generated.ident.__rtkImpl8
import io.github.kotlinmania.syn.generated.ident.xid_ok
import io.github.kotlinmania.syn.token.*

// Existing Kotlin API adapters; all translated behavior is in generated/ident.
public fun Ident.copy(): Ident = cloneToken()
public fun Ident.clone(): Ident = copy()

public fun Ident(marker: TokenMarker): Ident =
    io.github.kotlinmania.syn.generated.ident
        .Ident(marker)

public fun from(token: SelfValue): Ident = __rtkImpl1.from(token)
public fun from(token: SelfType): Ident = __rtkImpl2.from(token)
public fun from(token: Super): Ident = __rtkImpl3.from(token)
public fun from(token: Crate): Ident = __rtkImpl4.from(token)
public fun from(token: Extern): Ident = __rtkImpl5.from(token)
public fun from(token: Underscore): Ident = __rtkImpl6.from(token)

internal fun xidOk(symbol: String): Boolean = xid_ok(symbol)

public object IdentParse {
    public fun parse(input: ParseStream): SynResult<Ident> = __rtkImpl7.parse(input)
}

public object IdentPeek : Peek {
    override fun peek(cursor: Cursor): Boolean = __rtkImpl8.peek(cursor)
    override fun display(): String = __rtkImpl8.display()
}

public fun identFromSelfValue(token: SelfValue): Ident = from(token)
public fun identFromSelfType(token: SelfType): Ident = from(token)
public fun identFromSuper(token: Super): Ident = from(token)
public fun identFromCrate(token: Crate): Ident = from(token)
public fun identFromExtern(token: Extern): Ident = from(token)
public fun identFromUnderscore(token: Underscore): Ident = from(token)
