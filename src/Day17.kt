import java.util.*
import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {
    data class Path(val cells: List<Cell>, val heat: Int)

    val start = Cell(0, 0)
    fun Grid.end() = Cell(this[0].length - 1, this.size - 1)

    fun Grid.minHeat(predicate: (List<Cell>, Cell) -> Boolean): Int {
        val queue = PriorityQueue(compareBy<Path> { it.heat }).apply { add(Path(listOf(start), 0)) }
        val seen = mutableSetOf<List<Cell>>()
        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val cell = path.cells.last()
            if (cell == this.end()) return path.heat
            val line = path.cells.takeLastWhile { it.x == cell.x || it.y == cell.y }
            if (seen.contains(line)) continue
            seen.add(line)
            val lines = cell.adjacent().asSequence()
                .filter { inBounds(it) }
                .filter { path.cells.size < 2 || it != path.cells[path.cells.size - 2] }
                .filter { !seen.contains(path.cells + listOf(it)) }
                .filter { predicate(line, it) }
                .map { Path(path.cells + listOf(it), path.heat + this[it].digitToInt()) }
            queue.addAll(lines)
        }
        throw IllegalStateException()
    }

    fun part1(grid: Grid): Int {
        return grid.minHeat { cells, cell -> abs(cells[0].x - cell.x) < 4 && abs(cells[0].y - cell.y) < 4 }
    }

    fun part2(grid: Grid): Int {
        return grid.minHeat { cells, cell ->
            val isTurn = cells[0].x != cell.x && cells[0].y != cell.y
            if (cell == grid.end()) !isTurn && cells.size > 3
            else if (isTurn) cells.size > 4
            else cells.size < 11
        }
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(readInput("Day17_test2")) == 71)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}