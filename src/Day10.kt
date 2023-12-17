import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {

    fun Cell.isInverse(cell: Cell) = x + cell.x == 0 && y + cell.y == 0

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
            if (!grid.inBounds(curr)) return null
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
        val start = input.indexOfFirst { it.indexOf('S') > -1 }.let { Cell(input[it].indexOf('S'), it) }
        val moves = Cell(0, 0).adjacent().firstNotNullOf { moves(input, start, it) }

        // Using the shoelace formula https://en.wikipedia.org/wiki/Shoelace_formula
        val area = abs(moves.foldIndexed(0) { i, acc, cell ->
            val next = moves[(i + 1) % moves.size]
            acc + cell.x * next.y - cell.y * next.x
        } / 2)

        // Using Picks theorem https://en.wikipedia.org/wiki/Pick%27s_theorem
        return area + 1 - moves.size / 2
    }

    check(part1(readInput("Day10_test")) == 8)
    check(part2(readInput("Day10_part2_test_1")) == 4)
    check(part2(readInput("Day10_part2_test_2")) == 4)
    check(part2(readInput("Day10_part2_test_3")) == 8)
    check(part2(readInput("Day10_part2_test_4")) == 10)

    val input = readInput("Day10")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}