package ec.y2025

import ec.Answer
import ec.Quest
import ec.blocks
import ec.trimmedLines

class Quest07 : Quest {

    /** The one name the letter rules can build. `single` fails loudly if the puzzle ever has 0 or 2. */
    override fun part1(input: String): Answer {
        val notes = Notes.parse(input)
        return Answer(notes.names.single { it in notes.rules })
    }

    /** The sum of the positions, counting from 1, of every name the rules can build. */
    override fun part2(input: String): Answer {
        val notes = Notes.parse(input)
        return Answer(notes.names.withIndex().sumOf { (i, name) -> if (name in notes.rules) i + 1 else 0 })
    }
    /** How many distinct names, 7 to 11 letters long, start with one of the prefixes and follow the rules. */
    override fun part3(input: String): Answer {
        val notes = Notes.parse(input)
        val valid = notes.names.distinct().filter { it in notes.rules }
        // A prefix that extends another one only repeats that one's names: keep the shortest.
        val starts = valid.filter { prefix -> valid.none { other -> other != prefix && prefix.startsWith(other) } }
        val counter = NameCounter(notes.rules, NAME_LENGTHS)
        return Answer(starts.sumOf { counter.namesStartingWith(it) })
    }

    /**
     * Counts names by walking the rule graph one letter at a time. What can follow depends only on
     * the last letter and the length so far, so each (letter, length) is counted once and remembered.
     */
    private class NameCounter(private val rules: Rules, private val lengths: IntRange) {
        private val known = HashMap<Pair<Char, Int>, Long>()

        /** The prefix itself if it is long enough, plus every allowed way to extend it. */
        fun namesStartingWith(prefix: String): Long = namesFrom(prefix.last(), prefix.length)

        private fun namesFrom(last: Char, length: Int): Long = known.getOrPut(last to length) {
            val thisOne = if (length in lengths) 1L else 0L
            if (length >= lengths.last) thisOne
            else thisOne + rules.after(last).sumOf { namesFrom(it, length + 1) }
        }
    }

    private companion object {
        val NAME_LENGTHS = 7..11
    }

    /** Veronica's page: the candidate names, then the letter rules. Built only by [parse]. */
    private class Notes private constructor(val names: List<String>, val rules: Rules) {
        companion object {
            fun parse(input: String): Notes {
                val sections = input.blocks()
                require(sections.size == 2) { "expected names, a blank line, then rules; got ${sections.size} sections" }
                val (names, rules) = sections
                return Notes(names.trim().split(','), Rules.parse(rules))
            }
        }
    }

    /** Which letters may come directly after which. Case matters: 'O' and 'o' are different letters. */
    private class Rules private constructor(private val followers: Map<Char, Set<Char>>) {

        /** `first to second in rules`: may [second] come directly after [first]? */
        operator fun contains(pair: Pair<Char, Char>): Boolean =
            pair.second in followers[pair.first].orEmpty()

        /** The letters allowed directly after [letter]; none if it has no rule. */
        fun after(letter: Char): Set<Char> = followers[letter].orEmpty()

        /** `name in rules`: does every pair of neighbouring letters in [name] follow a rule? */
        operator fun contains(name: String): Boolean = name.zipWithNext().all { it in this }

        companion object {
            /** One rule per line, `r > a,i,o`. A letter with two rule lines is rejected, not merged. */
            fun parse(block: String): Rules {
                val lines = block.trimmedLines()
                val followers = lines.associate { line ->
                    val (letter, after) = line.split(" > ").also {
                        require(it.size == 2 && it[0].length == 1) { "expected 'x > a,b,c', got '$line'" }
                    }
                    letter.single() to after.split(',').map { it.single() }.toSet()
                }
                require(followers.size == lines.size) { "a letter has more than one rule line" }
                return Rules(followers)
            }
        }
    }
}