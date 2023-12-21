import kotlin.time.measureTimedValue

fun main() {

    data class State(val point: Point, val stepsRemaining: Int)

    fun endPoints(grid: Grid, start: Point, steps: Int, cache: MutableMap<State, Set<Point>>): Set<Point> {
        if (steps == 0) return setOf(start)
        val state = State(start, steps)
        if (cache.containsKey(state)) return cache.getValue(state)
        val points = start.adjacent()
            .filter { grid.inBounds(it) }
            .filter { grid[it] != '#' }
            .fold(setOf<Point>()) { acc, point -> acc + endPoints(grid, point, steps - 1, cache) }
        cache[state] = points
        return points
    }

    fun part1(grid: Grid, steps: Int): Int {
        val start = grid.withIndex()
            .first { (_, row) -> row.contains('S') }
            .let { (y, row) -> Point(row.indexOf('S'), y) }

        val cache = mutableMapOf<State, Set<Point>>()

        return endPoints(grid, start, steps, cache).size
    }

    fun part2(input: Grid, steps: Int): Int {
        TODO()
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput, 6) == 16)
    check(part2(testInput, 10) == 50)
    check(part2(testInput, 50) == 1594)
    check(part2(testInput, 100) == 6536)
    check(part2(testInput, 500) == 167004)
    check(part2(testInput, 1000) == 668697)
    check(part2(testInput, 5000) == 16733044)

    val input = readInput("Day21")
    measureTimedValue { part1(input, 64) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input, 26501365) }.also { println("${it.value} in ${it.duration}") }
}