package ec.y2025

import ec.Answer
import ec.Quest
import ec.trimmedLines

class Quest10 : Quest {

    /** Sheep standing on a square the dragon can reach in 4 moves or fewer. */
    override fun part1(input: String): Answer = Answer(sheepInReach(input, moves = 4))

    override fun part2(input: String): Answer = TODO("quest 10 part 2")
    override fun part3(input: String): Answer = TODO("quest 10 part 3")

    /** Part 1 with the number of moves as a parameter, so the 3-move sample can be tested. */
    internal fun sheepInReach(input: String, moves: Int): Int {
        require(moves >= 0) { "the dragon cannot make $moves moves" }
        val board = Board.parse(input)
        return board.reach(moves).count { it in board.sheep }
    }

    /** One square of the board: row 0 is the top line of the notes, column 0 its first character. */
    private data class Square(val row: Int, val column: Int) {
        /** The eight L-shaped jumps a dragon makes from here, some of which may leave the board. */
        fun jumps(): List<Square> = JUMPS.map { (down, right) -> Square(row + down, column + right) }
    }

    private companion object {
        /** Two squares one way, then one square perpendicular: the knight's move, in every direction. */
        val JUMPS = listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1)
    }

    /**
     * A rectangular board with exactly one dragon on it and any number of sheep. Built only by
     * [parse], so a board with no dragon, two dragons or ragged rows cannot exist.
     */
    private class Board private constructor(
        private val rows: Int,
        private val columns: Int,
        val dragon: Square,
        val sheep: Set<Square>,
    ) {
        /** `square in board`: is [square] on the board rather than off an edge? */
        operator fun contains(square: Square): Boolean = square.row in 0..<rows && square.column in 0..<columns

        /**
         * Every square the dragon can stand on after at most [moves] moves, its starting square
         * included. Breadth first: each round jumps only from squares first reached in the round
         * before, because anything reached earlier has already had its jumps followed.
         */
        fun reach(moves: Int): Set<Square> {
            val reached = mutableSetOf(dragon)
            var newest = setOf(dragon)
            repeat(moves) {
                newest = newest.flatMap { it.jumps() }.filter { it in this && reached.add(it) }.toSet()
            }
            return reached
        }

        companion object {
            /** One line per row: `.` an empty square, `S` a sheep, `D` the dragon. */
            fun parse(input: String): Board {
                val lines = input.trimmedLines()
                val columns = lines.first().length
                require(lines.all { it.length == columns }) { "every row must be $columns squares wide" }
                val squares = lines.flatMapIndexed { row, line ->
                    line.mapIndexed { column, symbol ->
                        require(symbol in ".SD") { "expected '.', 'S' or 'D', got '$symbol' at row $row" }
                        Square(row, column) to symbol
                    }
                }
                val dragon = squares.filter { it.second == 'D' }.also {
                    require(it.size == 1) { "a board has exactly one dragon, got ${it.size}" }
                }.single().first
                val sheep = squares.filter { it.second == 'S' }.map { it.first }.toSet()
                return Board(lines.size, columns, dragon, sheep)
            }
        }
    }
}
