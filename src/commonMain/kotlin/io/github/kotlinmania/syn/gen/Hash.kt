// port-lint: source gen/hash.rs
package io.github.kotlinmania.syn.gen

import io.github.kotlinmania.syn.Abi
import io.github.kotlinmania.syn.Arm
import io.github.kotlinmania.syn.AssocConst
import io.github.kotlinmania.syn.AssocType
import io.github.kotlinmania.syn.AttrStyle
import io.github.kotlinmania.syn.Attribute
import io.github.kotlinmania.syn.BareFnArg
import io.github.kotlinmania.syn.BareVariadic
import io.github.kotlinmania.syn.BinOp
import io.github.kotlinmania.syn.Block
import io.github.kotlinmania.syn.BoundLifetimes
import io.github.kotlinmania.syn.CapturedParam
import io.github.kotlinmania.syn.Constraint
import io.github.kotlinmania.syn.Data
import io.github.kotlinmania.syn.DataEnum
import io.github.kotlinmania.syn.DataStruct
import io.github.kotlinmania.syn.DataUnion
import io.github.kotlinmania.syn.DeriveInput
import io.github.kotlinmania.syn.GenericParam
import io.github.kotlinmania.syn.PathArguments

public class SynHasher {
    private var value: Int = -0x7ee3623b

    public fun writeU8(byte: Int) {
        mix(byte and 0xff)
    }

    public fun write(value: Any?) {
        when (value) {
            null -> {
                writeU8(0)
            }
            is AttrStyle -> {
                writeU8(1)
                value.hash(this)
            }
            is BinOp -> {
                writeU8(1)
                value.hash(this)
            }
            is CapturedParam -> {
                writeU8(1)
                value.hash(this)
            }
            is Data -> {
                writeU8(1)
                value.hash(this)
            }
            is DataEnum -> {
                writeU8(1)
                value.hash(this)
            }
            is DataStruct -> {
                writeU8(1)
                value.hash(this)
            }
            is DataUnion -> {
                writeU8(1)
                value.hash(this)
            }
            is DeriveInput -> {
                writeU8(1)
                value.hash(this)
            }
            is GenericParam.ConstParam -> {
                writeU8(1)
                value.hash(this)
            }
            is PathArguments.AngleBracketed -> {
                writeU8(1)
                value.hash(this)
            }
            else -> {
                writeU8(1)
                mix(value.hashCode())
            }
        }
    }

    public fun finish(): Int =
        value

    private fun mix(component: Int) {
        value = value xor component
        value *= 16777619
    }
}

private inline fun hashToInt(hash: (SynHasher) -> Unit): Int {
    val state = SynHasher()
    hash(state)
    return state.finish()
}

public fun Abi.hash(state: SynHasher) {
    state.write(name)
}

public fun Abi.hash(): Int =
    hashToInt { hash(it) }

public fun PathArguments.AngleBracketed.hash(state: SynHasher) {
    state.write(colon2Token)
    state.write(args)
}

public fun PathArguments.AngleBracketed.hash(): Int =
    hashToInt { hash(it) }

public fun Arm.hash(state: SynHasher) {
    state.write(attrs)
    state.write(pat)
    state.write(guard)
    state.write(body)
    state.write(comma)
}

public fun Arm.hash(): Int =
    hashToInt { hash(it) }

public fun AssocConst.hash(state: SynHasher) {
    state.write(ident)
    state.write(generics)
    state.write(value)
}

public fun AssocConst.hash(): Int =
    hashToInt { hash(it) }

public fun AssocType.hash(state: SynHasher) {
    state.write(ident)
    state.write(generics)
    state.write(ty)
}

public fun AssocType.hash(): Int =
    hashToInt { hash(it) }

public fun AttrStyle.hash(state: SynHasher) {
    when (this) {
        AttrStyle.Outer -> state.writeU8(0)
        is AttrStyle.Inner -> state.writeU8(1)
    }
}

public fun AttrStyle.hash(): Int =
    hashToInt { hash(it) }

public fun Attribute.hash(state: SynHasher) {
    style.hash(state)
    state.write(meta)
}

public fun Attribute.hash(): Int =
    hashToInt { hash(it) }

public fun BareFnArg.hash(state: SynHasher) {
    state.write(attrs)
    state.write(name)
    state.write(ty)
}

public fun BareFnArg.hash(): Int =
    hashToInt { hash(it) }

public fun BareVariadic.hash(state: SynHasher) {
    state.write(attrs)
    state.write(name)
    state.write(comma)
}

public fun BareVariadic.hash(): Int =
    hashToInt { hash(it) }

public fun BinOp.hash(state: SynHasher) {
    when (this) {
        is BinOp.Add -> state.writeU8(0)
        is BinOp.Sub -> state.writeU8(1)
        is BinOp.Mul -> state.writeU8(2)
        is BinOp.Div -> state.writeU8(3)
        is BinOp.Rem -> state.writeU8(4)
        is BinOp.And -> state.writeU8(5)
        is BinOp.Or -> state.writeU8(6)
        is BinOp.BitXor -> state.writeU8(7)
        is BinOp.BitAnd -> state.writeU8(8)
        is BinOp.BitOr -> state.writeU8(9)
        is BinOp.Shl -> state.writeU8(10)
        is BinOp.Shr -> state.writeU8(11)
        is BinOp.Eq -> state.writeU8(12)
        is BinOp.Lt -> state.writeU8(13)
        is BinOp.Le -> state.writeU8(14)
        is BinOp.Ne -> state.writeU8(15)
        is BinOp.Ge -> state.writeU8(16)
        is BinOp.Gt -> state.writeU8(17)
        is BinOp.AddAssign -> state.writeU8(18)
        is BinOp.SubAssign -> state.writeU8(19)
        is BinOp.MulAssign -> state.writeU8(20)
        is BinOp.DivAssign -> state.writeU8(21)
        is BinOp.RemAssign -> state.writeU8(22)
        is BinOp.BitXorAssign -> state.writeU8(23)
        is BinOp.BitAndAssign -> state.writeU8(24)
        is BinOp.BitOrAssign -> state.writeU8(25)
        is BinOp.ShlAssign -> state.writeU8(26)
        is BinOp.ShrAssign -> state.writeU8(27)
    }
}

public fun BinOp.hash(): Int =
    hashToInt { hash(it) }

public fun Block.hash(state: SynHasher) {
    state.write(stmts)
}

public fun Block.hash(): Int =
    hashToInt { hash(it) }

public fun BoundLifetimes.hash(state: SynHasher) {
    state.write(lifetimes)
}

public fun BoundLifetimes.hash(): Int =
    hashToInt { hash(it) }

public fun CapturedParam.hash(state: SynHasher) {
    when (this) {
        is CapturedParam.Lifetime -> {
            state.writeU8(0)
            state.write(lifetime)
        }
        is CapturedParam.Ident -> {
            state.writeU8(1)
            state.write(ident)
        }
    }
}

public fun CapturedParam.hash(): Int =
    hashToInt { hash(it) }

public fun GenericParam.ConstParam.hash(state: SynHasher) {
    state.write(attrs)
    state.write(ident)
    state.write(ty)
    state.write(eqToken)
    state.write(default)
}

public fun GenericParam.ConstParam.hash(): Int =
    hashToInt { hash(it) }

public fun Constraint.hash(state: SynHasher) {
    state.write(ident)
    state.write(generics)
    state.write(bounds)
}

public fun Constraint.hash(): Int =
    hashToInt { hash(it) }

public fun Data.hash(state: SynHasher) {
    when (this) {
        is Data.Struct -> {
            state.writeU8(0)
            value.hash(state)
        }
        is Data.Enum -> {
            state.writeU8(1)
            value.hash(state)
        }
        is Data.Union -> {
            state.writeU8(2)
            value.hash(state)
        }
    }
}

public fun Data.hash(): Int =
    hashToInt { hash(it) }

public fun DataEnum.hash(state: SynHasher) {
    state.write(variants)
}

public fun DataEnum.hash(): Int =
    hashToInt { hash(it) }

public fun DataStruct.hash(state: SynHasher) {
    state.write(fields)
    state.write(semiToken)
}

public fun DataStruct.hash(): Int =
    hashToInt { hash(it) }

public fun DataUnion.hash(state: SynHasher) {
    state.write(fields)
}

public fun DataUnion.hash(): Int =
    hashToInt { hash(it) }

public fun DeriveInput.hash(state: SynHasher) {
    state.write(attrs)
    state.write(vis)
    state.write(ident)
    state.write(generics)
    data.hash(state)
}

public fun DeriveInput.hash(): Int =
    hashToInt { hash(it) }
