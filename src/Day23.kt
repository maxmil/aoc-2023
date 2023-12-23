import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {
    fun Grid.longestPath(point: Point, path: List<Point>): List<Point> {
        if (point == Cell(this[0].length - 2, this.size - 1)) return path + point

        val next = point.adjacent().asSequence()
            .filter { inBounds(it) }
            .filter { !path.contains(it) }
            .filter {
                when (this[it]) {
                    '#' -> false
                    '>' -> it - point != Point(-1, 0)
                    'v' -> it - point != Point(0, -1)
                    '<' -> it - point != Point(1, 0)
                    '^' -> it - point != Point(0, 1)
                    else -> true
                }
            }
            .filter {
                when (this[point]) {
                    '>' -> it - point == Point(1, 0)
                    'v' -> it - point == Point(0, 1)
                    '<' -> it - point == Point(-1, 0)
                    '^' -> it - point == Point(0, -1)
                    else -> true
                }
            }
        return next.map { longestPath(it, path + point) }.maxBy { it.size }
    }

    fun part1(grid: Grid): Int {
        val start = Point(1, 0)
        var longest = 0
        var current = listOf(listOf(start))
        while (current.isNotEmpty()) {
            current = current.flatMap { path ->
                val point = path.last()
                if (point == Cell(grid[0].length - 2, grid.size - 1)) {
                    println("Found path " + (path.size - 1))
                    longest = max(longest, path.size - 1)
                    listOf()
                } else {
                    path.last().adjacent()
                        .filter { grid.inBounds(it) }
                        .filter { !path.contains(it) }
                        .filter {
                            when (grid[it]) {
                                '#' -> false
                                '>' -> it - point != Point(-1, 0)
                                'v' -> it - point != Point(0, -1)
                                '<' -> it - point != Point(1, 0)
                                '^' -> it - point != Point(0, 1)
                                else -> true
                            }
                        }
                        .filter {
                            when (grid[point]) {
                                '>' -> it - point == Point(1, 0)
                                'v' -> it - point == Point(0, 1)
                                '<' -> it - point == Point(-1, 0)
                                '^' -> it - point == Point(0, -1)
                                else -> true
                            }
                        }
                        .map { path + it }
                }
            }
        }
        return longest
    }

    fun part2(grid: Grid): Int {
        val start = Point(1, 0)
        var longest = 0
        var current = listOf(listOf(start))
        while (current.isNotEmpty()) {
            current = current.flatMap { path ->
                val point = path.last()
                if (point == Cell(grid[0].length - 2, grid.size - 1)) {
                    println("Found path " + (path.size - 1))
                    longest = max(longest, path.size - 1)
                    listOf()
                } else {
                    path.last().adjacent()
                        .filter { grid.inBounds(it) }
                        .filter { grid[it] != '#' }
                        .filter { !path.contains(it) }
                        .map { path + it }
                }
            }
        }
        return longest
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput).also { println(it) } == 154)

    val input = readInput("Day23")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
     measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}")}
}