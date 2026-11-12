package ec.y2025

import ec.Answer
import ec.Quest
import ec.ints

class Quest08 : Quest {

    /** Threads through the centre of a 32-nail circle: those joining two opposite nails. */
    override fun part1(input: String): Answer = Answer(throughCentre(input, nails = 32))

    /** Knots on a 256-nail circle: every pair of threads that cross. */
    override fun part2(input: String): Answer = Answer(knots(input, nails = 256))

    /** The most threads one straight strike between two of 256 nails can cut. */
    override fun part3(input: String): Answer = Answer(bestCut(input, nails = 256))

    /** Part 1 with the nail count as a parameter, so the 8-nail samples can be tested. */
    internal fun throughCentre(input: String, nails: Int): Int {
        require(nails % 2 == 0) { "only an even number of nails has opposite pairs, got $nails" }
        return Art.parse(input, nails).threads.count { it.span == nails / 2 }
    }

    /** Part 2 with the nail count as a parameter. */
    internal fun knots(input: String, nails: Int): Long {
        val threads = Art.parse(input, nails).threads
        // Each thread is knotted to every earlier thread it crosses, so every crossing pair once.
        return threads.indices.sumOf { i -> (0 until i).count { j -> threads[i] crosses threads[j] }.toLong() }
    }

    /**
     * Part 3 with the nail count as a parameter. A strike is itself a line between two nails: it
     * cuts every thread it crosses, plus any thread lying exactly along it.
     */
    internal fun bestCut(input: String, nails: Int): Int {
        val threads = Art.parse(input, nails).threads
        return Thread.allBetween(nails).maxOf { strike ->
            threads.count { it == strike || it crosses strike }
        }
    }

    /**
     * A straight thread between two different nails, numbered 1 up round the circle. Stored as
     * [low] < [high] so the same thread is equal whichever way it was wound, and a thread from a
     * nail to itself cannot be built. Its copy is as private as its constructor, so neither can
     * `copy(high = low)`.
     */
    @ConsistentCopyVisibility
    private data class Thread private constructor(val low: Int, val high: Int) {

        /** How many nails apart the ends are, counting up from [low]. */
        val span: Int get() = high - low

        /**
         * Two chords of a circle cross exactly when each has one end strictly inside the other's
         * arc. Threads sharing a nail meet at the edge, never in the middle, so they do not cross.
         */
        infix fun crosses(other: Thread): Boolean =
            other.low != low && other.low != high && other.high != low && other.high != high &&
                (other.low in low..high) != (other.high in low..high)

        companion object {
            fun between(a: Int, b: Int): Thread {
                require(a != b) { "a thread needs two different nails, got $a twice" }
                return Thread(minOf(a, b), maxOf(a, b))
            }

            /** Every line between two of [nails] nails, each once. */
            fun allBetween(nails: Int): Sequence<Thread> = sequence {
                for (low in 1..nails) for (high in low + 1..nails) yield(Thread(low, high))
            }
        }
    }

    /** The artist's threads in the order they were wound. Built only by [parse]. */
    private class Art private constructor(val threads: List<Thread>) {
        companion object {
            /** A comma-separated walk of nails; each step from one nail to the next is a thread. */
            fun parse(input: String, nails: Int): Art {
                val walk = input.ints()
                require(walk.size >= 2) { "a walk needs at least two nails, got ${walk.size}" }
                walk.firstOrNull { it !in 1..nails }?.let {
                    throw IllegalArgumentException("nail $it is not on a circle of $nails")
                }
                return Art(walk.zipWithNext(Thread::between))
            }
        }
    }
}
