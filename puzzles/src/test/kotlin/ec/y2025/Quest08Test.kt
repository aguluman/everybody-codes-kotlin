package ec.y2025

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Quest08Test {

    private val quest = Quest08()

    @Test
    fun `part 1 sample`() {
        assertEquals(4, quest.throughCentre("1,5,2,6,8,4,1,7,3", nails = 8))
    }

    @Test
    fun `part 2 sample`() {
        assertEquals(21L, quest.knots("1,5,2,6,8,4,1,7,3,5,7,8,2", nails = 8))
        // Threads that share a nail touch at the rim and never knot, however they are wound.
        assertEquals(0L, quest.knots("1,5,1,3,1", nails = 8))
    }

    @Test
    fun `part 3 sample`() {
        // Crossings alone give 6: the 7th is the thread the best strike lies exactly along.
        assertEquals(7, quest.bestCut("1,5,2,6,8,4,1,7,3,6", nails = 8))
    }

    @Test
    fun `illegal walks are rejected, not counted`() {
        assertFailsWith<IllegalArgumentException> { quest.knots("1,1", nails = 8) }
        assertFailsWith<IllegalArgumentException> { quest.knots("1,9", nails = 8) }
        assertFailsWith<IllegalArgumentException> { quest.throughCentre("1,4", nails = 7) }
    }
}
