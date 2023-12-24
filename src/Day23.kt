import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {

    fun part1(grid: Grid): Int {
        val start = Point(1, 0)
        var longest = 0
        var current = listOf(listOf(start))
        while (current.isNotEmpty()) {
            current = current.flatMap { path ->
                val point = path.last()
                if (point == Cell(grid[0].length - 2, grid.size - 1)) {
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
        data class Node(val point: Point, val nodes: MutableMap<Point, Int> = mutableMapOf())

        val start = Node(Point(1, 0))
        val end = Node(Point(grid[0].length - 2, grid.size - 1))
        val nodes = mutableMapOf<Point, Node>()
        val seen = mutableSetOf<Point>()
        nodes[start.point] = start
        nodes[end.point] = end

        fun Grid.step(point: Point, start: Node) = point.adjacent()
            .filter { inBounds(it) && it != start.point && !seen.contains(it) && this[it] != '#' }

        fun Grid.walkGraph(start: Node) {
            step(start.point, start).forEach { adj ->
                var dist = 1
                var next = adj
                while (nodes[next] == null && step(next, start).size == 1) {
                    seen.add(next)
                    next = step(next, start)[0]
                    dist++
                }
                if (nodes[next] != null) {
                    nodes.getValue(next).nodes[start.point] = dist
                    start.nodes[next] = dist
                } else if (step(next, start).size > 1) {
                    nodes[next] = Node(next, mutableMapOf(start.point to dist))
                    start.nodes[next] = dist
                    walkGraph(nodes.getValue(next))
                }
            }
        }
        grid.walkGraph(start)

        fun longestDistance(from: Node, to: Node, visited: Set<Node>): Int? {
            return if (from == to) 0
            else from.nodes.map { (point, dist) -> Pair(nodes.getValue(point), dist) }
                .filter { (node, _) -> !visited.contains(node) }
                .mapNotNull { (node, dist) -> longestDistance(node, to, visited + from)?.let { dist + it } }
                .maxOrNull()
        }

        return longestDistance(start, end, setOf()) ?: throw IllegalStateException("No paths found")
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 94)
    check(part2(testInput) == 154)

    val input = readInput("Day23")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}