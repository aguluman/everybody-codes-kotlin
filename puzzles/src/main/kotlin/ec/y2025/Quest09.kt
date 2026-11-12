package ec.y2025

import ec.Answer
import ec.Quest
import ec.trimmedLines

class Quest09 : Quest {

    /** Of the three scales, the one that is the other two's child: its degree of similarity to them. */
    override fun part1(input: String): Answer =
        // `single` fails loudly if no scale is the child, or if more than one could be.
        Answer(Scales.parse(input).parentages().single().similarity)

    /** Every child on the list matched to its one pair of parents: the sum of their similarities. */
    override fun part2(input: String): Answer =
        Answer(Scales.parse(input).parentages().sumOf { it.similarity })

    /** The scale numbers of the biggest family added up. */
    override fun part3(input: String): Answer {
        val families = Scales.parse(input).families()
        val biggest = families.maxOf { it.size }
        // The puzzle asks for "the biggest family": two of the same size would make that question meaningless.
        val largest = families.filter { it.size == biggest }
        require(largest.size == 1) { "${largest.size} families share the biggest size, $biggest" }
        return Answer(largest.single().sumOf { it.number })
    }

    /**
     * A child and the two parents it could have inherited from: one link in a family tree. Built
     * only by [of], which refuses any child with a symbol neither parent has at that position, so a
     * [Parentage] is always possible.
     */
    private class Parentage private constructor(val child: Scale, val parents: Pair<Scale, Scale>) {

        /** Matching symbols with one parent times matching symbols with the other. */
        val similarity: Int get() = child.dna.matches(parents.first.dna) * child.dna.matches(parents.second.dna)

        companion object {
            /** The parentage, or null when [child] cannot have come from [one] and [other]. */
            fun of(child: Scale, one: Scale, other: Scale): Parentage? {
                // Plain comparisons, not a set: this runs for every child against every pair.
                require(child.number != one.number && child.number != other.number && one.number != other.number) {
                    "a parentage is three different scales, got ${child.number}, ${one.number}, ${other.number}"
                }
                return if (child.dna.inheritsFrom(one.dna, other.dna)) Parentage(child, one to other) else null
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
        fun parentages(): List<Parentage> = all.mapNotNull { child ->
            val others = all - child
            val found = others.indices.flatMap { i ->
                (i + 1 until others.size).mapNotNull { j -> Parentage.of(child, others[i], others[j]) }
            }
            require(found.size <= 1) {
                "scale ${child.number} fits ${found.size} pairs of parents, but a child has only one"
            }
            found.singleOrNull()
        }

        /**
         * Every family: the scales joined by any chain of child-to-parent links, whatever the
         * generations. A scale with no links is a family of one. Each scale is in exactly one family.
         */
        fun families(): List<Set<Scale>> {
            // Union-find: each scale points towards a representative of its family.
            val towards = all.associateWith { it }.toMutableMap()
            fun representative(scale: Scale): Scale {
                val next = towards.getValue(scale)
                return if (next == scale) scale else representative(next).also { towards[scale] = it }
            }
            for (link in parentages()) {
                val child = representative(link.child)
                towards[representative(link.parents.first)] = child
                towards[representative(link.parents.second)] = child
            }
            return all.groupBy(::representative).values.map { it.toSet() }
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
