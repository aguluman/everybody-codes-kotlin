package ec.y2025

import ec.Answer
import ec.Quest
import ec.trimmedLines

class Quest11 : Quest {

    /** The flock's checksum after 10 rounds of exchanges. */
    override fun part1(input: String): Answer = Answer(checksumAfter(input, rounds = 10))

    /** How many rounds, both phases together, until every column holds the same number of ducks. */
    override fun part2(input: String): Answer = Answer(roundsToBalance(input))

    /**
     * The same question for a flock already in flight. Its columns are sorted, so the first phase
     * has nothing to do and the answer is just the second phase's closed form.
     */
    override fun part3(input: String): Answer = Answer(roundsToBalance(input))

    /** Part 1 with the number of rounds as a parameter, so the sample's whole table can be tested. */
    internal fun checksumAfter(input: String, rounds: Int): Long {
        require(rounds >= 0) { "a flight cannot have $rounds rounds" }
        val start = Flock.parse(input)
        // Once balanced, no duck moves again: a flock asked about a later round is still balanced.
        return (sequenceOf(start) + start.rounds()).take(rounds + 1).last().checksum
    }

    internal fun roundsToBalance(input: String): Long = Flock.parse(input).roundsToBalance()

    /** Round-by-round count, kept as the reference the closed form is tested against. */
    internal fun simulatedRounds(input: String): Long = Flock.parse(input).rounds().count().toLong()

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

        fun round(birds: List<Long>): List<Long> {
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

        /**
         * The columns after each round of this phase that moved a duck. The phase ends with the
         * first round in which no duck could move, and that round does not count.
         */
        fun rounds(from: List<Long>): Sequence<List<Long>> =
            generateSequence(from) { before -> round(before).takeIf { it != before } }.drop(1)
    }

    /**
     * Ducks per column, first column first. Built only by [parse], so every count is a whole,
     * non-negative number and the total divides evenly by the columns, as the manual promises.
     */
    @ConsistentCopyVisibility
    private data class Flock private constructor(private val birds: List<Long>) {

        /** Each column's ducks times it's number, counting columns from 1. */
        val checksum: Long get() = birds.withIndex().sumOf { (column, count) -> (column + 1L) * count }

        /** The flock after each round that moved a duck, through both phases in order. */
        fun rounds(): Sequence<Flock> = sequence {
            var current = birds
            for (phase in Phase.entries) {
                for (after in phase.rounds(current)) {
                    current = after
                    yield(Flock(after))
                }
            }
        }

        /**
         * Rounds until balanced, without simulating the second phase. The first phase stops only
         * when no column holds more than the next, that is, when the columns are non-decreasing.
         * From there, each settle round closes the gap below the mean by exactly one duck, so the
         * second phase lasts as many rounds as the columns below the mean are short in total.
         */
        fun roundsToBalance(): Long {
            var sorted = birds
            var spread = 0L
            for (after in Phase.Spread.rounds(birds)) {
                sorted = after
                spread++
            }
            val mean = sorted.sum() / sorted.size
            val settle = sorted.sumOf { (mean - it).coerceAtLeast(0) }
            return spread + settle
        }

        companion object {
            /** One column per line: how many ducks start in it. */
            fun parse(input: String): Flock {
                val birds = input.trimmedLines().map { line ->
                    line.trim().toLongOrNull()?.takeIf { it >= 0 }
                        ?: throw IllegalArgumentException("expected a count of ducks, got '$line'")
                }
                require(birds.isNotEmpty()) { "a flock needs at least one column" }
                require(birds.sum() % birds.size == 0L) {
                    "${birds.sum()} ducks cannot be shared evenly across ${birds.size} columns"
                }
                return Flock(birds)
            }
        }
    }
}