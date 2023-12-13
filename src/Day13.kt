import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {

    fun String.smudges(other: String) = zip(other).count { (a, b) -> a != b }

    fun List<String>.transpose(): List<String> {
        val a = Array(this[0].length) { CharArray(size) }
        indices.forEach { y -> (0..<this[0].length).forEach { x -> a[x][y] = this[y][x] } }
        return a.map { it.joinToString("") }
    }

    fun maxReflection(grid: List<String>, smudges: Int): Int {
        return grid.asSequence().windowed(2).withIndex()
            .filter { (_, rows) -> rows[0].smudges(rows[1]) <= smudges }.map { (i, _) -> i + 1 }
            .maxOfOrNull { row ->
                val n = min(grid.size - row, row)
                val smudged = (0..<n).count { grid[row - n + it].smudges(grid[row + n - it - 1]) == 1 }
                val different = (0..<n).count { grid[row - n + it].smudges(grid[row + n - it - 1]) > 1 }
                if (different == 0 && smudged == smudges) row else 0
            } ?: 0
    }

    fun summarizeReflections(input: String, smudges: Int): Int {
        return input.split("\n\n").sumOf { lines ->
            val grid = lines.split("\n")
            val x = maxReflection(grid, smudges)
            val y = maxReflection(grid.transpose(), smudges)
            x * 100 + y
        }
    }

    fun part1(input: String) = summarizeReflections(input, 0)

    fun part2(input: String) = summarizeReflections(input, 1)

    val testInput = readInputText("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInputText("Day13")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}