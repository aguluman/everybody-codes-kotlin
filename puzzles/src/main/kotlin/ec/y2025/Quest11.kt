package ec.y2025

import ec.Answer
import ec.Quest
import ec.trimmedLines

class Quest11 : Quest {

    /** The flock's checksum after 10 rounds of exchanges. */
    override fun part1(input: String): Answer = Answer(checksumAfter(input, rounds = 10))

    override fun part2(input: String): Answer = TODO("quest 11 part 2")
    override fun part3(input: String): Answer = TODO("quest 11 part 3")

    /** Part 1 with the number of rounds as a parameter, so the sample's whole table can be tested. */
    internal fun checksumAfter(input: String, rounds: Int): Long {
        require(rounds >= 0) { "a flight cannot have $rounds rounds" }
        val start = Flock.parse(input)
        // Once balanced, no duck moves again: a flock asked about a later round is still balanced.
        return (sequenceOf(start) + start.rounds()).take(rounds + 1).last().checksum
    }

    /**
     * The two phases share one rule, a duck moves from the fuller column of a pair to the emptier
     * one, and differ only in which way it may go. The pairs are checked left to right, and each
     * check sees the moves already made earlier in the same round.
     */
    private enum class Phase(private val forward: Boolean) {
        /** First: a duck moves on to the next column when that column has fewer. */
        Spread(forward = true),

        /** Second: a duck moves back from the next column when that column has more. */
        Settle(forward = false);

        fun round(birds: List<Int>): List<Int> {
            val after = birds.toMutableList()
            for (column in 0..<after.lastIndex) {
                val (from, to) = if (forward) column to column + 1 else column + 1 to column
                if (after[from] > after[to]) {
                    after[from]--
                    after[to]++
                }
            }
            return after
        }
    }

    /**
     * Ducks per column, first column first. Built only by [parse], so every count is a whole,
     * non-negative number and the total divides evenly by the columns, as the manual promises.
     */
    @ConsistentCopyVisibility
    private data class Flock private constructor(private val birds: List<Int>) {

        /** Each column's ducks times its number, counting columns from 1. */
        val checksum: Long get() = birds.withIndex().sumOf { (column, count) -> (column + 1L) * count }

        /**
         * The flock after each round that moved a duck. A phase ends with the first round in which
         * no duck could move, and that round does not count: the next round is already the next
         * phase's. The sequence ends when the second phase can move no duck either.
         */
        fun rounds(): Sequence<Flock> = sequence {
            var current = birds
            for (phase in Phase.entries) {
                while (true) {
                    val after = phase.round(current)
                    if (after == current) break
                    current = after
                    yield(Flock(current))
                }
            }
        }

        companion object {
            /** One column per line: how many ducks start in it. */
            fun parse(input: String): Flock {
                val birds = input.trimmedLines().map { line ->
                    line.trim().toIntOrNull()?.takeIf { it >= 0 }
                        ?: throw IllegalArgumentException("expected a count of ducks, got '$line'")
                }
                require(birds.sum() % birds.size == 0) {
                    "${birds.sum()} ducks cannot be shared evenly across ${birds.size} columns"
                }
                return Flock(birds)
            }
        }
    }
}
