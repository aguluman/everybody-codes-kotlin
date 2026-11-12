package ec.y2025

import ec.Answer
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Quest12Test {

    private val quest = Quest12()

    private val sample = """
        989601
        857782
        746543
        766789
    """.trimIndent()

    @Test
    fun `part 1 sample`() {
        assertEquals(Answer(16), quest.part1(sample))
    }

    @Test
    fun `fire spreads to equal barrels but not larger ones`() {
        assertEquals(Answer(3), quest.part1("555"))  // equal: the whole row burns
        assertEquals(Answer(1), quest.part1("123"))  // every neighbour larger: only the first burns
        assertEquals(Answer(4), quest.part1("99\n99"))
    }

    @Test
    fun `notes that aren't a clearing are rejected`() {
        assertFailsWith<IllegalArgumentException> { quest.part1("") }          // no barrels
        assertFailsWith<IllegalArgumentException> { quest.part1("12\n3") }     // ragged rows
        assertFailsWith<IllegalArgumentException> { quest.part1("1a\n23") }    // not a size
    }

    @Test
    fun `part 2 sample`() {
        val sample = """
            9589233445
            9679121695
            8469121876
            8352919876
            7342914327
            7234193437
            6789193538
            6781219648
            5691219769
            5443329859
        """.trimIndent()
        assertEquals(Answer(58), quest.part2(sample))
        // One barrel is both corners: it still burns, and counts, once.
        assertEquals(Answer(1), quest.part2("5"))
    }

    @Ignore("unlocks after part 2: paste its sample, then delete this line")
    @Test
    fun `part 3 sample`() {
        val sample = """
            REPLACE ME
        """.trimIndent()
        assertEquals(Answer("REPLACE ME"), quest.part3(sample))
    }
}
