import java.lang.IllegalStateException
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {

    data class Cell(val x: Int, val y: Int) {
        fun adjacent(): List<Cell> {
            return listOf(
                Cell(x, y + 1),
                Cell(x, y - 1),
                Cell(x + 1, y),
                Cell(x - 1, y)
            )
        }

        operator fun plus(cell: Cell) = Cell(x + cell.x, y + cell.y)
        operator fun minus(cell: Cell) = Cell(x - cell.x, y - cell.y)
    }

    data class PathPoint(val cell: Cell, val direction: Cell, val index: Int)

    data class Path(val cells: List<Cell>, val heatLoss: Int)

    fun Grid.inBounds(cell: Cell) = cell.y in indices && cell.x in this[0].indices
    operator fun Grid.get(cell: Cell) = if (inBounds(cell)) this[cell.y][cell.x] else null

    fun getPathPoint(path: List<Cell>, cell: Cell): PathPoint {
        if (path.isEmpty()) return PathPoint(cell, Cell(0, 0), 1)
        val direction = cell - path.last()
        val index = path.reversed().windowed(2).takeWhile { (c1, c2) -> c1 - c2 == direction }.count() + 1
        return PathPoint(cell, direction, index)
    }

    fun printGrid(grid: Grid, path: Path) {
        grid.forEachIndexed { y, row ->
            row.mapIndexed { x, c -> if (path.cells.contains(Cell(x, y))) "#" else c }.joinToString("").println()
        }
    }

    fun minimumHeatLoss(grid: Grid, predicate: (List<Cell>, Cell) -> Boolean): Int {
        val queue = PriorityQueue(compareBy<Path> { it.heatLoss })
        val seen = mutableSetOf<PathPoint>()
        queue.add(Path(listOf(Cell(0, 0)), 0))
        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val cell = path.cells.last()
            val pathPoint = getPathPoint(path.cells.dropLast(1), cell)
            if (seen.contains(pathPoint)) continue
            if (cell == Cell(grid[0].length - 1, grid.size - 1)) {
                printGrid(grid, path)
                return path.heatLoss
            }
            seen.add(pathPoint)
            val cells = cell.adjacent()
                .filter { grid.inBounds(it) }
                .filter { path.cells.size < 2 || it != path.cells[path.cells.size - 2] } // don't go back
                .filter { !seen.contains(getPathPoint(path.cells, it)) }
                .filter { predicate(path.cells, it) }
                .map { Path(path.cells + listOf(it), path.heatLoss + grid[it]!!.digitToInt()) }
            queue.addAll(cells)
        }
        return -1
    }

    fun part1(grid: Grid): Int {
        return minimumHeatLoss(grid) { cells, cell -> getPathPoint(cells, cell).index < 4 }
    }

    fun getConsecutive(cells: List<Cell>): Int {
        if (cells.size < 3) return 1
        else {
            val reversed = cells.reversed()
            val direction = reversed[0] - reversed[1]
            return reversed.windowed(2).takeWhile { (c1, c2) -> c1 - c2 == direction }.count()
        }

    }

    fun part2(grid: List<String>): Int {
        return minimumHeatLoss(grid) { cells, cell ->
//            if (cells == (0..10).map { Cell(it, 0) } && cell == Cell(11, 0)) {
//                println("here")
//            }
            if (cells.size < 2) true
            else {
                val isTurn = (cell - cells.last()) != cells.reversed().let { it[0] - it[1] }
                if (cell == Cell(grid[0].length - 1, grid.size -1) && getConsecutive(cells + listOf(cell)) < 4){
                    false
                } else if (isTurn) {
                    getConsecutive(cells) > 3
                } else {
                    getConsecutive(cells + listOf(cell)) < 11
                }
            }
        }
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part2(readInput("Day17_test2")).also { println(it) } == 71)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}