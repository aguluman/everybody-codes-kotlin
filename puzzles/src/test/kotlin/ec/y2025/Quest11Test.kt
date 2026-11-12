package ec.y2025

import ec.Answer
import kotlin.random.Random
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

    private val bigger = """
        805
        706
        179
        48
        158
        150
        232
        885
        598
        524
        423
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
        assertFailsWith<IllegalArgumentException> { quest.checksumAfter("", rounds = 1) } // no columns at all
        assertFailsWith<IllegalArgumentException> { quest.checksumAfter(sample, rounds = -1) }
    }

    @Test
    fun `part 2 samples`() {
        assertEquals(Answer(11L), quest.part2(sample))
        assertEquals(Answer(1579L), quest.part2(bigger))
        // Already balanced: no round is needed.
        assertEquals(Answer(0L), quest.part2("5\n5\n5"))
    }

    @Test
    fun `part 3 sample`() {
        // Part III has no example of its own. The manual's flock after round 6 is sorted, like
        // Part III's notes, and its table shows it balanced 5 rounds later, at round 11.
        val inFlight = """
            3
            4
            4
            4
            7
            8
        """.trimIndent()
        assertEquals(Answer(5L), quest.part3(inFlight))
    }

    @Test
    fun `part 3 counts past Int without simulating`() {
        // Sorted, so no first-phase rounds; 3 billion ducks short of the mean, so 3 billion rounds.
        // Simulating them would take days; the closed form answers at once.
        assertEquals(Answer(3_000_000_000L), quest.part3("0\n6000000000"))
    }

    @Test
    fun `closed form agrees with simulation`() {
        // The second phase's round count is an observed property, not a proven one: keep checking it.
        assertEquals(quest.simulatedRounds(sample), quest.roundsToBalance(sample))
        assertEquals(quest.simulatedRounds(bigger), quest.roundsToBalance(bigger))
        val random = Random(11)
        repeat(5_000) {
            val columns = random.nextInt(1, 10)
            val birds = MutableList(columns) { random.nextLong(0, 31) }
            birds[birds.lastIndex] += Math.floorMod(-birds.sum(), columns.toLong())
            if (random.nextBoolean()) birds.sort()
            val input = birds.joinToString("\n")
            assertEquals(quest.simulatedRounds(input), quest.roundsToBalance(input), input)
        }
    }
}