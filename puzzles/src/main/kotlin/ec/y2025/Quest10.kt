package ec.y2025

import ec.Answer
import ec.Quest
import ec.trimmedLines

class Quest10 : Quest {

    /** Sheep standing on a square the dragon can reach in 4 moves or fewer. */
    override fun part1(input: String): Answer = Answer(sheepInReach(input, moves = 4))

    /** Sheep the dragon can eat over 20 rounds, counting every way its moves could go. */
    override fun part2(input: String): Answer = Answer(sheepEaten(input, rounds = 20))

    /** Every distinct sequence of moves, sheep first, in which the dragon eats all the sheep. */
    override fun part3(input: String): Answer = Answer(Tournament(Board.parse(input)).winningGames())

    /** Part 1 with the number of moves as a parameter, so the 3-move sample can be tested. */
    internal fun sheepInReach(input: String, moves: Int): Int {
        require(moves >= 0) { "the dragon cannot make $moves moves" }
        val board = Board.parse(input)
        // Within range means after 0, 1, ... or [moves] moves: every one of those squares counts.
        val reach = board.dragonAfterEachMove().take(moves + 1).flatten().toSet()
        return reach.count { it in board.sheep }
    }

    /**
     * Part 2 with the number of rounds as a parameter. Each round the dragon moves, then every sheep
     * steps one square down. A sheep sharing a square with the dragon after either half is eaten,
     * unless that square is a hideout. "Every variant" means the dragon is wherever it could be.
     */
    internal fun sheepEaten(input: String, rounds: Int): Int {
        require(rounds >= 0) { "the game cannot last $rounds rounds" }
        val board = Board.parse(input)
        var sheep = board.sheep
        var eaten = 0

        /** Removes and counts the sheep standing, unhidden, on a square the dragon could be on. */
        fun feast(dragon: Set<Square>) {
            val caught = sheep.filter { it in dragon && it !in board.hideouts }
            eaten += caught.size
            sheep = sheep - caught.toSet()
        }

        // After move 0 is the starting square, which is not a round: rounds begin with move 1.
        for (dragon in board.dragonAfterEachMove().drop(1).take(rounds)) {
            feast(dragon)
            // A sheep that steps down off the bottom row has escaped. The dragon never leaves the
            // board, so it could not eat one anyway: dropping them just stops tracking the survivors.
            sheep = sheep.map { it.down() }.filter { it in board }.toSet()
            feast(dragon)
        }
        return eaten
    }

    /** One square of the board: row 0 is the top line of the notes, column 0 its first character. */
    private data class Square(val row: Int, val column: Int) {
        /** The eight L-shaped jumps a dragon makes from here, some of which may leave the board. */
        fun jumps(): List<Square> = JUMPS.map { (down, right) -> Square(row + down, column + right) }

        /** The square a sheep steps to: one towards the bottom. */
        fun down(): Square = Square(row + 1, column)
    }

    private companion object {
        /** Two squares one way, then one square perpendicular: the knight's move, in every direction. */
        val JUMPS = listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1)

        /**
         * Counts reach billions on small boards and grow fast with size. A plain `sumOf` would wrap
         * past [Long.MAX_VALUE] into a wrong answer without a word; this throws instead.
         */
        inline fun <T> List<T>.sumOfExact(count: (T) -> Long): Long =
            fold(0L) { total, item -> Math.addExact(total, count(item)) }
    }

    /**
     * A rectangular board with exactly one dragon, any number of sheep and any number of
     * hideouts. Built only by [parse], so a board with no dragon, two dragons or ragged rows
     * cannot exist. Every square holds one thing, so no piece starts on a hideout.
     */
    private class Board private constructor(
        val rows: Int,
        val columns: Int,
        val dragon: Square,
        val sheep: Set<Square>,
        val hideouts: Set<Square>,
    ) {
        /** `square in board`: is [square] on the board rather than off an edge? */
        operator fun contains(square: Square): Boolean = square.row in 0..<rows && square.column in 0..<columns

        /**
         * Where the dragon can stand after exactly 0, 1, 2, ... moves, across every way it could
         * have moved. Each set is every jump, staying on the board, from every square of the one
         * before. A knight changes square colour with every move, so it is never back where it
         * stood one move earlier: these sets are not cumulative.
         */
        fun dragonAfterEachMove(): Sequence<Set<Square>> =
            generateSequence(setOf(dragon)) { squares -> squares.flatMap { it.jumps() }.filter { it in this }.toSet() }

        companion object {
            /** One line per row: `.` an empty square, `S` a sheep, `D` the dragon, `#` a hideout. */
            fun parse(input: String): Board {
                val lines = input.trimmedLines()
                val columns = lines.first().length
                require(lines.all { it.length == columns }) { "every row must be $columns squares wide" }
                val squares = lines.flatMapIndexed { row, line ->
                    line.mapIndexed { column, symbol ->
                        require(symbol in ".SD#") { "expected '.', 'S', 'D' or '#', got '$symbol' at row $row" }
                        Square(row, column) to symbol
                    }
                }
                fun all(symbol: Char) = squares.filter { it.second == symbol }.map { it.first }
                val dragons = all('D')
                require(dragons.size == 1) { "a board has exactly one dragon, got ${dragons.size}" }
                return Board(lines.size, columns, dragons.single(), all('S').toSet(), all('#').toSet())
            }
        }
    }

    /**
     * Part 3's game. The sheep move first, one sheep per turn, one square down, and never onto the
     * dragon unless that square is a hideout. If no sheep can move, their turn is skipped. The
     * dragon must always move, and eats a sheep it lands on outside a hideout. A sheep stepping
     * off the bottom escapes, so no game through that move can end with every sheep eaten.
     *
     * The same position is reached by many different games, so each position's count of winning
     * continuations is worked out once and remembered.
     */
    private class Tournament(private val board: Board) {
        private val known = HashMap<Position, Long>()

        fun winningGames(): Long = games(Position(board.dragon, Flock.of(board), Turn.Sheep))

        private fun games(position: Position): Long = known.getOrPut(position) {
            when (position.turn) {
                Turn.Sheep -> sheepTurn(position)
                Turn.Dragon -> dragonTurn(position)
            }
        }

        private fun sheepTurn(position: Position): Long {
            val (dragon, flock) = position
            // A sheep may step anywhere below it except onto the dragon outside a hideout.
            val steps = flock.sheep().filter { sheep ->
                val to = sheep.down()
                to != dragon || to in board.hideouts
            }
            if (steps.isEmpty()) return games(position.copy(turn = Turn.Dragon))
            return steps.sumOfExact { sheep ->
                if (sheep.down() !in board) 0L // escaped: this game can no longer end with every sheep eaten
                else games(Position(dragon, flock.stepped(sheep), Turn.Dragon))
            }
        }

        private fun dragonTurn(position: Position): Long =
            position.dragon.jumps().filter { it in board }.sumOfExact { to ->
                val eats = to in position.flock && to !in board.hideouts
                val flock = if (eats) position.flock.without(to) else position.flock
                // Eating the last sheep ends the game: that is one complete winning sequence.
                if (flock.isEmpty) 1L else games(Position(to, flock, Turn.Sheep))
            }
    }

    private enum class Turn { Sheep, Dragon }

    /** Everything that decides how a game can go on from here; the moves that led here do not. */
    private data class Position(val dragon: Square, val flock: Flock, val turn: Turn)

    /**
     * The sheep still on the board, at most one per column: a column's sheep is just its row, or
     * null once that sheep is gone. Two sheep in one column cannot be written down. Built only by
     * [of]; its copy is as private as its constructor.
     */
    @ConsistentCopyVisibility
    private data class Flock private constructor(private val rows: List<Int?>) {

        val isEmpty: Boolean get() = rows.all { it == null }

        /** The square of every sheep still on the board. */
        fun sheep(): List<Square> = rows.mapIndexedNotNull { column, row -> row?.let { Square(it, column) } }

        /** `square in flock`: is a sheep standing on [square]? */
        operator fun contains(square: Square): Boolean = rows.getOrNull(square.column) == square.row

        /** The flock after [sheep] steps one square down. */
        fun stepped(sheep: Square): Flock = changed(sheep) { it + 1 }

        /** The flock after [sheep] is eaten. */
        fun without(sheep: Square): Flock = changed(sheep) { null }

        private fun changed(sheep: Square, row: (Int) -> Int?): Flock {
            require(sheep in this) { "no sheep stands on $sheep" }
            return Flock(rows.toMutableList().also { it[sheep.column] = row(sheep.row) })
        }

        companion object {
            fun of(board: Board): Flock {
                require(board.sheep.isNotEmpty()) { "a game with no sheep has nothing to eat" }
                val byColumn = board.sheep.groupBy { it.column }
                byColumn.values.firstOrNull { it.size > 1 }?.let {
                    throw IllegalArgumentException("column ${it.first().column} has ${it.size} sheep; it can hold one")
                }
                return Flock(List(board.columns) { column -> byColumn[column]?.single()?.row })
            }
        }
    }
}
