// port-lint: tests tests/test_shebang.rs
package io.github.kotlinmania.syn

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShebangTest {
    @Test
    fun testBasic() {
        val file = parseFile("#!/usr/bin/env rustx\nfn main() {}").getOrThrow()

        assertEquals("#!/usr/bin/env rustx", file.shebang)
        assertTrue(file.attrs.isEmpty())
        val item = assertIs<Item.Fn>(file.items.single())
        assertEquals("main", item.ident.toString())
        assertTrue(
            item.block
                ?.stmts
                .orEmpty()
                .isEmpty(),
        )
    }

    @Test
    fun testComment() {
        val file = parseFile("#!//am/i/a/comment\n[allow(dead_code)] fn main() {}").getOrThrow()

        assertNull(file.shebang)
        val attr = file.attrs.single()
        assertIs<AttrStyle.Inner>(attr.style)
        val meta = assertIs<Meta.List>(attr.meta)
        assertEquals(
            "allow",
            meta.path.segments
                .first()
                ?.ident
                .toString(),
        )
        assertTrue(meta.delimiter is MacroDelimiter.Paren)
        assertEquals("dead_code", meta.tokens.toString())
        val item = assertIs<Item.Fn>(file.items.single())
        assertEquals("main", item.ident.toString())
    }
}
