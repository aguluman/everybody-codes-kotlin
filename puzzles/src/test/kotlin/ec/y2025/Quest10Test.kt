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

    @Test
    fun `part 2 sample`() {
        val sample = """
            ...SSS##.....
            .S#.##..S#SS.
            ..S.##.S#..S.
            .#..#S##..SS.
            ..SSSS.#.S.#.
            .##..SS.#S.#S
            SS##.#D.S.#..
            S.S..S..S###.
            .##.S#.#....S
            .SSS.#SS..##.
            ..#.##...S##.
            .#...#.S#...S
            SS...#.S.#S..
        """.trimIndent()
        // 5 + 7 + 15 over the sample's 3 rounds; the puzzle itself asks for 20.
        assertEquals(5, quest.sheepEaten(sample, rounds = 1))
        assertEquals(12, quest.sheepEaten(sample, rounds = 2))
        assertEquals(27, quest.sheepEaten(sample, rounds = 3))
    }

    @Test
    fun `a sheep is eaten after either half of a round, but never in a hideout`() {
        // The dragon's one move lands on row 2, column 1 or 3.
        assertEquals(1, quest.sheepEaten("D...\n....\n.S..", rounds = 1))   // the dragon lands on it
        assertEquals(1, quest.sheepEaten("D...\n.S..\n....\n....", rounds = 1)) // it steps under the dragon
        assertEquals(0, quest.sheepEaten("D...\n.S..\n.#..\n....", rounds = 1)) // it steps into a hideout
        assertEquals(0, quest.sheepEaten("D...\n....\n.S..", rounds = 0))
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
