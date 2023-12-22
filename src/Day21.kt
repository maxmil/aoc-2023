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

    fun Int.positiveMod(n: Int) = ((this % n) + n) % n

    fun Grid.getNotNull(point: Point) = this[Point(point.x.positiveMod(this[0].length), point.y.positiveMod(this.size))]

    fun printGridWithReachable(reachable: MutableSet<Point>, grid: Grid) {
        val minX = reachable.minOf { it.x }
        val minY = reachable.minOf { it.y }
        val maxX = reachable.maxOf { it.x }
        val maxY = reachable.maxOf { it.y }

        (minY..maxY).forEach { y ->
            (minX..maxX).map { x ->
                if (reachable.contains(Point(x, y))) 'O' else grid.getNotNull(Point(x, y))
            }.joinToString("").println()
        }
    }

    fun part2(grid: Grid, steps: Int): Int {
        val start = grid.withIndex()
            .first { (_, row) -> row.contains('S') }
            .let { (y, row) -> Point(row.indexOf('S'), y) }

        var step = 0
        var curr = listOf(start)
        var visited = mutableSetOf<Point>()
        var reachable = mutableSetOf<Point>()
        while (step < steps) {
            val next =
                curr.flatMap { it.adjacent() }.filter { !visited.contains(it) }.filter { grid.getNotNull(it) != '#' }
            visited.addAll(next)
            step++

            curr = next.flatMap { it.adjacent() }.filter { !visited.contains(it) }.filter { grid.getNotNull(it) != '#' }
            visited.addAll(curr)
            reachable.addAll(curr)
            step++
            println("${step}, ${reachable.size}")
        }
        printGridWithReachable(reachable, grid)
        return reachable.size
    }


//     Are there any patterns in the example data?
//
//    val m = mapOf(6 to 16,
//    10 to  50,
//    50 to  1594,
//    100 to  6536,
//    500 to  167004,
//    1000 to  668697,
//    5000 to  16733044)
//
//    m.entries.windowed(2).forEach { (a, b) ->  println("${b.value.toDouble()/a.value} ${b.key.toDouble()/a.key}") }
//
//    println((m[100]!! / m[10]!!.toDouble()))
//    println((m[1000]!! / m[100]!!.toDouble()))
//    System.exit(1)

    val testInput = readInput("Day21_test")
//    check(part1(testInput, 6) == 16)
//    check(part2(testInput, 6) == 16)
//    check(part2(testInput, 10) == 50)
    check(part2(testInput, 50) == 1594)
//    check(part2(testInput, 100) == 6536)
//    check(part2(testInput, 500) == 167004)
//    check(part2(testInput, 1000) == 668697)
//    check(part2(testInput, 5000) == 16733044)

    val input = readInput("Day21")
//    measureTimedValue { part1(input, 64) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input, 26501365) }.also { println("${it.value} in ${it.duration}") }
}

/*

Rotate input 45 degrees, how do we do that

123
456
789

8 3 4
 2 6
1 5 9
 4 8
6 7 2

*/