package ec.y2025

import ec.Answer
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Quest09Test {

    private val quest = Quest09()

    @Test
    fun `part 1 sample`() {
        val sample = """
            1:CAAGCGCTAAGTTCGCTGGATGTGTGCCCGCG
            2:CTTGAATTGGGCCGTTTACCTGGTTTAACCAT
            3:CTAGCGCTGAGCTGGCTGCCTGGTTGACCGCG
        """.trimIndent()
        assertEquals(Answer(414), quest.part1(sample))
    }

    @Test
    fun `the child is found wherever it is listed`() {
        // The same three scales with the child first: 23 * 18 still.
        val sample = """
            3:CTAGCGCTGAGCTGGCTGCCTGGTTGACCGCG
            1:CAAGCGCTAAGTTCGCTGGATGTGTGCCCGCG
            2:CTTGAATTGGGCCGTTTACCTGGTTTAACCAT
        """.trimIndent()
        assertEquals(Answer(414), quest.part1(sample))
    }

    @Test
    fun `notes that break the rules are rejected, not counted`() {
        // A symbol that is not DNA.
        assertFailsWith<IllegalArgumentException> { quest.part1("1:ACGX\n2:ACGT\n3:ACGT") }
        // DNA of different lengths.
        assertFailsWith<IllegalArgumentException> { quest.part1("1:ACG\n2:ACGT\n3:ACGT") }
        // One scale listed twice.
        assertFailsWith<IllegalArgumentException> { quest.part1("1:ACGT\n1:ACGT\n3:ACGT") }
        // No scale can be the child: position 1 has three different symbols.
        assertFailsWith<NoSuchElementException> { quest.part1("1:A\n2:C\n3:G") }
    }

    @Test
    fun `part 2 sample`() {
        // 3 is the child of 6 and 7; 1 and 4 are both children of 2 and 5: 462 + 399 + 384.
        val sample = """
            1:GCAGGCGAGTATGATACCCGGCTAGCCACCCC
            2:TCTCGCGAGGATATTACTGGGCCAGACCCCCC
            3:GGTGGAACATTCGAAAGTTGCATAGGGTGGTG
            4:GCTCGCGAGTATATTACCGAACCAGCCCCTCA
            5:GCAGCTTAGTATGACCGCCAAATCGCGACTCA
            6:AGTGGAACCTTGGATAGTCTCATATAGCGGCA
            7:GGCGTAATAATCGGATGCTGCAGAGGCTGCTG
        """.trimIndent()
        assertEquals(Answer(1245), quest.part2(sample))
    }

    @Test
    fun `a child that fits two pairs of parents is rejected, not counted`() {
        // Four identical scales: each one could come from any two of the other three.
        assertFailsWith<IllegalArgumentException> { quest.part2("1:ACGT\n2:ACGT\n3:ACGT\n4:ACGT") }
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
