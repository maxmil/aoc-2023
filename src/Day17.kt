import java.lang.IllegalStateException
import java.util.*
import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {
    data class Cell(val x: Int, val y: Int) {
        fun adjacent() = listOf(-1, 1).flatMap { listOf(Cell(x, y + it), Cell(x + it, y)) }
        operator fun plus(cell: Cell) = Cell(x + cell.x, y + cell.y)
        operator fun minus(cell: Cell) = Cell(x - cell.x, y - cell.y)
    }

    data class Path(val cells: List<Cell>, val heatLoss: Int)

    fun Grid.inBounds(cell: Cell) = cell.y in indices && cell.x in this[0].indices
    operator fun Grid.get(cell: Cell) = if (inBounds(cell)) this[cell.y][cell.x] else null

    fun minimumHeatLoss(grid: Grid, predicate: (List<Cell>, Cell) -> Boolean): Int {
        val queue = PriorityQueue(compareBy<Path> { it.heatLoss })
        val seen = mutableSetOf<List<Cell>>()
        queue.add(Path(listOf(Cell(0, 0)), 0))
        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val cell = path.cells.last()
            if (cell == Cell(grid[0].length - 1, grid.size - 1)) return path.heatLoss
            val line = path.cells.takeLastWhile { it.x == cell.x || it.y == cell.y }
            if (seen.contains(line)) continue
            seen.add(line)
            val lines = cell.adjacent()
                .filter { grid.inBounds(it) }
                .filter { path.cells.size < 2 || it != path.cells[path.cells.size - 2] }
                .filter { predicate(line, it) }
                .filter { !seen.contains(path.cells + listOf(it)) }
                .map { Path(path.cells + listOf(it), path.heatLoss + grid[it]!!.digitToInt()) }
            queue.addAll(lines)
        }
        throw IllegalStateException()
    }

    fun part1(grid: Grid): Int {
        return minimumHeatLoss(grid) { cells, cell -> abs(cells[0].x - cell.x) < 4 && abs(cells[0].y - cell.y) < 4 }
    }

    fun part2(grid: Grid): Int {
        return minimumHeatLoss(grid) { cells, cell ->
            val isTurn = cells[0].x != cell.x && cells[0].y != cell.y
            if (cell == Cell(grid[0].length - 1, grid.size - 1)) !isTurn && cells.size > 3
            else if (isTurn) cells.size > 4
            else cells.size < 11
        }
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(readInput("Day17_test2")) == 71)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    measureTimedValue { part1(input) }.also { check(it.value == 907) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { check(it.value == 1057) }
        .also { println("${it.value} in ${it.duration}") }
}