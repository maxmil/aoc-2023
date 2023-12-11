import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {

    data class Point(val x: Int, val y: Int)

    fun sumDistances(input: List<String>, expandFactor: Long): Long {
        val emptyCols = (0..<input[0].length).filter { col -> input.all { it[col] == '.' } }
        val emptyRows = input.withIndex().filter { (_, line) -> line.all { it == '.' } }.map { (i, _) -> i }

        val galaxies =
            input.flatMapIndexed { y, row -> row.withIndex().filter { it.value == '#' }.map { Point(it.index, y) } }

        return galaxies.foldIndexed(0) { index, acc, src ->
            acc + (index + 1..<galaxies.size).sumOf { i ->
                val dest = galaxies[i]
                val cols = emptyCols.count { it > min(src.x, dest.x) && it < max(src.x, dest.x) }
                val rows = emptyRows.count { it > min(src.y, dest.y) && it < max(src.y, dest.y) }
                abs(src.x - dest.x) + abs(src.y - dest.y) + (rows + cols) * (expandFactor - 1)
            }
        }
    }

    fun part1(input: List<String>) = sumDistances(input, 2L)

    fun part2(input: List<String>, expandFactor: Long = 1_000_000L) = sumDistances(input, expandFactor)

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)
    check(part2(testInput, 10) == 1030L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}