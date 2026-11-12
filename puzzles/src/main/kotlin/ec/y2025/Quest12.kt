package ec.y2025

import ec.Answer
import ec.Quest
import ec.trimmedLines

class Quest12 : Quest {

    /** Barrels destroyed by one fireball on the top-left barrel. */
    override fun part1(input: String): Answer {
        val clearing = Clearing.parse(input)
        return Answer(clearing.burned(from = setOf(clearing.topLeft)).size)
    }

    /**
     * Barrels destroyed by two fireballs at once, on opposite corners. The timing doesn't matter:
     * a barrel catches from any burning neighbour at least its size, whenever that one caught.
     */
    override fun part2(input: String): Answer {
        val clearing = Clearing.parse(input)
        return Answer(clearing.burned(from = setOf(clearing.topLeft, clearing.bottomRight)).size)
    }

    /**
     * Barrels destroyed by three fireballs, chosen greedily: each round picks the barrel that destroys
     * the most barrels still standing. Firing the three together destroys the same barrels as three
     * rounds in a row: a fire reaching a destroyed barrel could only reach what that barrel already lit.
     */
    override fun part3(input: String): Answer {
        val clearing = Clearing.parse(input)
        val destroyed = mutableSetOf<Barrel>()
        repeat(FIREBALLS) { destroyed += clearing.bestStrike(standing = { it !in destroyed }) }
        return Answer(destroyed.size)
    }

    private companion object {
        const val FIREBALLS = 3
    }

    /** One barrel's place in the grid: row 0 is the top line of the notes, column 0 its first character. */
    private data class Barrel(val row: Int, val column: Int) {
        /** The four barrels a fire can reach from here, some of which may be outside the clearing. */
        fun neighbours(): List<Barrel> =
            listOf(Barrel(row - 1, column), Barrel(row + 1, column), Barrel(row, column - 1), Barrel(row, column + 1))
    }

    /**
     * A rectangular grid of barrel sizes, 0 to 9. Built only by [parse], so a clearing with ragged
     * rows, no barrels or a size that isn't a digit cannot exist.
     */
    private class Clearing private constructor(private val sizes: List<String>) {

        /** `barrel in clearing`: is [barrel] in the grid rather than off an edge? */
        operator fun contains(barrel: Barrel): Boolean =
            barrel.row in sizes.indices && barrel.column in sizes[barrel.row].indices

        private fun size(barrel: Barrel): Char = sizes[barrel.row][barrel.column]

        val topLeft = Barrel(0, 0)
        val bottomRight: Barrel get() = Barrel(sizes.lastIndex, sizes.first().lastIndex)

        /** Every barrel, row by row. */
        fun barrels(): Sequence<Barrel> =
            sizes.indices.asSequence().flatMap { row -> sizes[row].indices.map { Barrel(row, it) } }

        /**
         * The barrels destroyed by the single fireball that destroys the most of those still
         * [standing]. A barrel inside an earlier fire this round is skipped: everything it could
         * ignite, that fire ignited too, so it can't do better. Ties go to the first barrel found.
         */
        fun bestStrike(standing: (Barrel) -> Boolean): Set<Barrel> {
            var best = emptySet<Barrel>()
            val tried = mutableSetOf<Barrel>()
            for (barrel in barrels()) {
                if (!standing(barrel) || barrel in tried) continue
                val strike = burned(from = setOf(barrel), standing = standing)
                tried += strike
                if (strike.size > best.size) best = strike
            }
            return best
        }

        /**
         * Every barrel the chain reaction from [from] destroys, [from] included. A burning barrel
         * ignites each [standing] neighbour of the same size or smaller. Each barrel burns once, so
         * the reaction visits each barrel at most once and always ends.
         */
        fun burned(from: Set<Barrel>, standing: (Barrel) -> Boolean = { true }): Set<Barrel> {
            val burned = from.toMutableSet()
            val burning = ArrayDeque(from)
            while (burning.isNotEmpty()) {
                val barrel = burning.removeFirst()
                for (next in barrel.neighbours()) {
                    // Digits compare as characters in the same order as their sizes.
                    if (next in this && standing(next) && size(next) <= size(barrel) && burned.add(next)) {
                        burning.addLast(next)
                    }
                }
            }
            return burned
        }

        companion object {
            /** One row per line, one digit per barrel. */
            fun parse(input: String): Clearing {
                val rows = input.trimmedLines().map(String::trim)
                require(rows.first().isNotEmpty()) { "a clearing needs at least one barrel" }
                require(rows.all { it.length == rows.first().length }) {
                    "every row must be ${rows.first().length} barrels wide"
                }
                rows.forEachIndexed { row, line ->
                    line.firstOrNull { it !in '0'..'9' }?.let {
                        throw IllegalArgumentException("barrel sizes are 0 to 9, got '$it' in row $row")
                    }
                }
                return Clearing(rows)
            }
        }
    }
}