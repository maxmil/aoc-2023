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
        return grid.longestPath(start, listOf()).size - 1
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput).also { println(it) } == 94)
    // check(part2(testInput) == 1)

    val input = readInput("Day23")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    // measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}")}
}