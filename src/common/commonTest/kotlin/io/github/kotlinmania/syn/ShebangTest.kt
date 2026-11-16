// port-lint: tests tests/test_shebang.rs
package io.github.kotlinmania.syn

import io.github.kotlinmania.procmacro2.TokenStream
import io.github.kotlinmania.syn.gen.clone
import io.github.kotlinmania.syn.gen.debug
import io.github.kotlinmania.syn.gen.eq
import io.github.kotlinmania.syn.gen.hash
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
        assertNull(file.frontmatter)
        assertTrue(file.attrs.isEmpty())
        val item = assertIs<Item.Fn>(file.items.single())
        assertEquals("main", item.ident.toString())
        assertIs<Visibility.Inherited>(item.vis)
        val generics = item.sig.generics
        assertNull(generics.ltToken)
        assertTrue(generics.params.isEmpty())
        assertNull(generics.gtToken)
        assertNull(generics.whereClause)
        assertIs<ReturnType.Default>(item.sig.output)
        assertTrue(item.block.stmts.isEmpty())
    }

    @Test
    fun testComment() {
        val file = parseFile("#!//am/i/a/comment\n[allow(dead_code)] fn main() {}").getOrThrow()

        assertNull(file.shebang)
        assertNull(file.frontmatter)
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
        assertIs<Visibility.Inherited>(item.vis)
        val generics = item.sig.generics
        assertNull(generics.ltToken)
        assertTrue(generics.params.isEmpty())
        assertNull(generics.gtToken)
        assertNull(generics.whereClause)
        assertIs<ReturnType.Default>(item.sig.output)
        assertTrue(item.block.stmts.isEmpty())
    }

    @Test
    fun testFrontmatterRepresentationAndTraits() {
        val base = parseFile("fn main() {}").getOrThrow()
        base.frontmatter = Frontmatter()
        val cloned = base.clone()
        assertTrue(base.eq(cloned))
        assertEquals(base.hash(), cloned.hash())
        assertTrue(base.debug().contains("Frontmatter"))
        val out = TokenStream.new()
        base.toTokens(out)
        assertEquals("fn main () { }", out.toString())
    }
}
