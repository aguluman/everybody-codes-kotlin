package ec.y2025

import ec.Answer
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

    @Test
    fun `part 3 samples`() {
        val sample = """
            1:GCAGGCGAGTATGATACCCGGCTAGCCACCCC
            2:TCTCGCGAGGATATTACTGGGCCAGACCCCCC
            3:GGTGGAACATTCGAAAGTTGCATAGGGTGGTG
            4:GCTCGCGAGTATATTACCGAACCAGCCCCTCA
            5:GCAGCTTAGTATGACCGCCAAATCGCGACTCA
            6:AGTGGAACCTTGGATAGTCTCATATAGCGGCA
            7:GGCGTAATAATCGGATGCTGCAGAGGCTGCTG
        """.trimIndent()
        // Two families, {3, 6, 7} and {1, 2, 4, 5}: the bigger one sums to 12.
        assertEquals(Answer(12), quest.part3(sample))

        // Scale 8's parents come from both families, so its egg joins all eight scales into one.
        val joined = sample + "\n8:GGCGTAAAGTATGGATGCTGGCTAGGCACCCG"
        assertEquals(Answer(36), quest.part3(joined))
    }

    @Test
    fun `two biggest families of the same size are rejected, not guessed between`() {
        // Two unrelated families of three: 1 and 2 have child 3; 4 and 5 have child 6.
        val tied = "1:AA\n2:CC\n3:AC\n4:GG\n5:TT\n6:GT"
        assertFailsWith<IllegalArgumentException> { quest.part3(tied) }
    }
}
