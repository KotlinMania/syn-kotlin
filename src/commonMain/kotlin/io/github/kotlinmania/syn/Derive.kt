// port-lint: source derive.rs
package io.github.kotlinmania.syn

public fun parseDeriveInput(input: ParseStream): SynResult<DeriveInput> =
    input.parse(DeriveInputParse)

public object DeriveInputParse : Parse<DeriveInput> {
    override fun parse(input: ParseStream): SynResult<DeriveInput> =
        DeriveInputParseImpl.parse(input)
}
