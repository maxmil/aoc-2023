import kotlin.time.measureTimedValue


data class Cell(val x: Int, val y: Int) {
    operator fun plus(cell: Cell) = Cell(x + cell.x, y + cell.y)
    operator fun minus(cell: Cell) = Cell(x - cell.x, y - cell.y)
    fun isInverse(cell: Cell) = x + cell.x == 0 && y + cell.y == 0
    fun inBounds(grid: Grid) = y < grid.size && y >= 0 && x < grid[y].length && x >= 0
    fun turn(clockwise: Boolean): Cell {
        val m = if (clockwise) 1 else -1
        return when (this) {
            Cell(0, -1) -> Cell(m * 1, 0)
            Cell(1, 0) -> Cell(0, m * 1)
            Cell(0, 1) -> Cell(m * -1, 0)
            Cell(-1, 0) -> Cell(0, m * -1)
            else -> throw IllegalArgumentException()
        }
    }
    fun adjacent(grid: Grid): List<Cell> =
        listOf(Cell(x, y - 1), Cell(x + 1, y), Cell(x, y + 1), Cell(x - 1, y))
            .filter { inBounds(grid) }
}

typealias Grid = List<String>

operator fun Grid.get(cell: Cell) = if (cell.inBounds(this)) this[cell.y][cell.x] else null

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

    fun isClockwiseLoop(moves: List<Cell>): Boolean {
        return moves.windowed(3).fold(0) { acc, (a, b, c) ->
            val prev = b - a
            when (c - b) {
                prev.turn(true) -> acc + 1
                prev.turn(false) -> acc - 1
                else -> acc
            }
        } > 0
    }

    fun inRegion(grid: Grid, start: Cell, edge: Collection<Cell>): Set<Cell> {
        val toVisit = ArrayDeque(listOf(start))
        val visited = edge.toMutableSet()
        val found = mutableSetOf<Cell>()
        while (toVisit.isNotEmpty()) {
            val cell = toVisit.removeFirst()
            if (!visited.contains(cell)) {
                found.add(cell)
                visited.add(cell)
                toVisit.addAll(cell.adjacent(grid))
            }
        }
        return found
    }

    fun part2(input: List<String>): Int {
        val start = input.indexOfFirst { it.indexOf('S') > -1 }.let { Cell(input[it].indexOf('S'), it) }
        val moves = listOf(Cell(0, 1), Cell(1, 0), Cell(0, 1), Cell(-1, 0))
            .firstNotNullOf { moves(input, start, it) }
        val clockwise = isClockwiseLoop(moves)
        val inLoop = mutableSetOf<Cell>()
        moves.windowed(3).forEach { (a, b, c) ->
            val cells = listOf(b + (b - a).turn(clockwise), (b + (c - b).turn(clockwise)))
            cells.forEach { inLoop.addAll(inRegion(input, it, (moves + inLoop).toSet())) }
        }
        return inLoop.size
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