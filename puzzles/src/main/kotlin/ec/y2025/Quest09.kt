package ec.y2025

import ec.Answer
import ec.Quest
import ec.trimmedLines

class Quest09 : Quest {

    /** Of the three scales, the one that is the other two's child: its degree of similarity to them. */
    override fun part1(input: String): Answer =
        // `single` fails loudly if no scale is the child, or if more than one could be.
        Answer(Scales.parse(input).families().single().similarity)

    /** Every child on the list matched to its one pair of parents: the sum of their similarities. */
    override fun part2(input: String): Answer =
        Answer(Scales.parse(input).families().sumOf { it.similarity })

    override fun part3(input: String): Answer = TODO("quest 09 part 3")

    /**
     * A child and the two parents it could have inherited from. Built only by [of], which refuses
     * any child with a symbol neither parent has at that position, so a [Family] is always possible.
     */
    private class Family private constructor(val child: Scale, val parents: Pair<Scale, Scale>) {

        /** Matching symbols with one parent times matching symbols with the other. */
        val similarity: Int get() = child.dna.matches(parents.first.dna) * child.dna.matches(parents.second.dna)

        companion object {
            /** The family, or null when [child] cannot have come from [one] and [other]. */
            fun of(child: Scale, one: Scale, other: Scale): Family? {
                require(setOf(child.number, one.number, other.number).size == 3) {
                    "a family is three different scales, got ${child.number}, ${one.number}, ${other.number}"
                }
                return if (child.dna.inheritsFrom(one.dna, other.dna)) Family(child, one to other) else null
            }
        }
    }

    /** One scale: the number Thaddeus wrote beside it, and the DNA on its underside. */
    private data class Scale(val number: Int, val dna: Dna)

    /**
     * A DNA identifier: only the symbols A, C, G and T, at least one of them. Built only by [parse].
     * Comparisons assume equal lengths, which [Scales.parse] guarantees for every scale it returns.
     */
    @JvmInline
    private value class Dna private constructor(private val symbols: String) {

        val length: Int get() = symbols.length

        /** How many positions hold the same symbol in both. */
        fun matches(other: Dna): Int = symbols.indices.count { symbols[it] == other.symbols[it] }

        /** Every symbol, at its own position, is one that [one] or [other] has there. */
        fun inheritsFrom(one: Dna, other: Dna): Boolean =
            symbols.indices.all { symbols[it] == one.symbols[it] || symbols[it] == other.symbols[it] }

        companion object {
            fun parse(text: String): Dna {
                require(text.isNotEmpty() && text.all { it in "ACGT" }) { "DNA is only A, C, G and T, got '$text'" }
                return Dna(text)
            }
        }
    }

    /** The scales from the notes, all with DNA of one length and each number once. Built only by [parse]. */
    private class Scales private constructor(val all: List<Scale>) {

        /**
         * Every scale that is a child, with its parents. A scale no pair could have produced is a
         * founder and is left out. The DNA allows each child one pair only, so finding two is an error.
         */
        fun families(): List<Family> = all.mapNotNull { child ->
            val others = all - child
            val found = others.indices.flatMap { i ->
                (i + 1 until others.size).mapNotNull { j -> Family.of(child, others[i], others[j]) }
            }
            require(found.size <= 1) {
                "scale ${child.number} fits ${found.size} pairs of parents, but a child has only one"
            }
            found.singleOrNull()
        }

        companion object {
            /** One scale per line, `1:CAAGCGCT...`. */
            fun parse(input: String): Scales {
                val scales = input.trimmedLines().map { line ->
                    val (number, dna) = line.split(':').also {
                        require(it.size == 2) { "expected 'number:DNA', got '$line'" }
                    }
                    Scale(number.trim().toInt(), Dna.parse(dna.trim()))
                }
                require(scales.map { it.dna.length }
                    .distinct().size == 1) { "every scale's DNA must be the same length" }
                require(scales.map { it.number }.distinct().size == scales.size) { "a scale number appears twice" }
                return Scales(scales)
            }
        }
    }
}
