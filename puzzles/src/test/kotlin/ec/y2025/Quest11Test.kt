package ec.y2025

import ec.Answer
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Quest11Test {

    private val quest = Quest11()

    private val sample = """
        9
        1
        1
        4
        9
        6
    """.trimIndent()

    @Test
    fun `part 1 sample`() {
        assertEquals(Answer(109L), quest.part1(sample))
    }

    @Test
    fun `every round of the manual's table`() {
        // Rounds 1 to 6 are the first phase, 7 to 11 the second; 12 shows a balanced flock stays put.
        val table = listOf(111L, 114, 116, 118, 119, 121, 122, 117, 115, 113, 109, 105, 105)
        assertEquals(table, table.indices.map { quest.checksumAfter(sample, rounds = it) })
    }

    @Test
    fun `notes that break the manual are rejected, not flown`() {
        assertFailsWith<IllegalArgumentException> { quest.checksumAfter("1\n2", rounds = 1) } // 3 ducks, 2 columns
        assertFailsWith<IllegalArgumentException> { quest.checksumAfter("4\n-2", rounds = 1) }
        assertFailsWith<IllegalArgumentException> { quest.checksumAfter("4\nfour", rounds = 1) }
        assertFailsWith<IllegalArgumentException> { quest.checksumAfter(sample, rounds = -1) }
    }

    @Ignore("unlocks after part 1: paste its sample, then delete this line")
    @Test
    fun `part 2 sample`() {
        val sample = """
            REPLACE ME
        """.trimIndent()
        assertEquals(Answer("REPLACE ME"), quest.part2(sample))
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
