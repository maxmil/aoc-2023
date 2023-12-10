import kotlin.time.measureTimedValue


data class Cell(val x: Int, val y: Int) {
    operator fun plus(cell: Cell) = Cell(x + cell.x, y + cell.y)
    fun isInverse(cell: Cell) = x + cell.x == 0 && y + cell.y == 0
    fun inverse() = Cell(-x, -y)
}

typealias Grid = List<String>

operator fun Grid.get(cell: Cell) =
    if (cell.y < this.size && cell.y >= 0 && cell.x < this[cell.y].length && cell.x >= 0) this[cell.y][cell.x]
    else null

fun main() {

    val pipes = mapOf(
        '|' to listOf(Cell(0, -1), Cell(0, 1)),
        '-' to listOf(Cell(-1, 0), Cell(1, 0)),
        'L' to listOf(Cell(0, -1), Cell(1, 0)),
        'J' to listOf(Cell(0, -1), Cell(-1, 0)),
        '7' to listOf(Cell(0, 1), Cell(-1, 0)),
        'F' to listOf(Cell(0, 1), Cell(1, 0)),
    )

    fun moves(grid: Grid, start: Cell, firstMove: Cell): List<Cell>? {
        var move = firstMove
        val visited = mutableListOf(start)
        while (true) {
            val curr = visited.last() + move
            if (curr == visited.first()) return visited
            val pipe = pipes[grid[curr]] ?: return null
            if (pipe.all { !it.isInverse(move) }) return null
            visited.add(curr)
            move = pipe.first { !it.isInverse(move) }
        }
    }

    fun part1(input: Grid): Int {
        val start = input.indexOfFirst { it.indexOf('S') > -1 }.let { Cell(input[it].indexOf('S'), it) }
        return listOf(Cell(0, 1), Cell(1, 0), Cell(0, 1), Cell(-1, 0))
            .firstNotNullOf { moves(input, start, it) }
            .size / 2
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 1)

    val input = readInput("Day10")
    measureTimedValue { part1(input) }.also { check(it.value == 6909) }
        .also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}