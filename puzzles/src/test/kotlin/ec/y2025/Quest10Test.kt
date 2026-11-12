package ec.y2025

import ec.Answer
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Quest10Test {

    private val quest = Quest10()

    @Test
    fun `part 1 sample`() {
        val sample = """
            ...SSS.......
            .S......S.SS.
            ..S....S...S.
            ..........SS.
            ..SSSS...S...
            .....SS..S..S
            SS....D.S....
            S.S..S..S....
            ....S.......S
            .SSS..SS.....
            .........S...
            .......S....S
            SS.....S..S..
        """.trimIndent()
        // The sample counts 3 moves; the puzzle itself asks for 4.
        assertEquals(27, quest.sheepInReach(sample, moves = 3))
    }

    @Test
    fun `the edges cut a jump short`() {
        // A sheep on every square, so the count is the number of squares one jump away.
        val centre = """
            SSSSSSS
            SSSSSSS
            SSSSSSS
            SSSDSSS
            SSSSSSS
            SSSSSSS
            SSSSSSS
        """.trimIndent()
        assertEquals(8, quest.sheepInReach(centre, moves = 1))

        val corner = """
            SSSSSSD
            SSSSSSS
            SSSSSSS
        """.trimIndent()
        assertEquals(2, quest.sheepInReach(corner, moves = 1))
        // No moves at all: only the dragon's own square, which holds no sheep.
        assertEquals(0, quest.sheepInReach(corner, moves = 0))
    }

    @Test
    fun `boards that break the rules are rejected, not counted`() {
        assertFailsWith<IllegalArgumentException> { quest.sheepInReach("S..\n...", moves = 1) }
        assertFailsWith<IllegalArgumentException> { quest.sheepInReach("D..\n..D", moves = 1) }
        assertFailsWith<IllegalArgumentException> { quest.sheepInReach("D..\n..", moves = 1) }
        assertFailsWith<IllegalArgumentException> { quest.sheepInReach("D.X", moves = 1) }
        assertFailsWith<IllegalArgumentException> { quest.sheepInReach("D..", moves = -1) }
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
