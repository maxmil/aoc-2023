import java.util.*
import kotlin.time.measureTimedValue

fun main() {
    data class LineHeat(val cells: List<Cell>, val heat: Int)

    fun Grid.end() = Cell(this[0].length - 1, this.size - 1)
    fun Cell.isTurn(line: List<Cell>) = line[0].x != x && line[0].y != y

    fun Grid.minHeat(predicate: Grid.(List<Cell>, Cell) -> Boolean): Int {
        val seen = mutableSetOf<List<Cell>>()
        val queue = PriorityQueue(compareBy<LineHeat> { it.heat })
        queue.add(LineHeat(listOf(Cell(0, 0)), 0))
        while (true) {
            val (line, heat) = queue.poll()
            if (line.last() == end()) return heat
            if (seen.contains(line)) continue
            seen.add(line)
            line.last().adjacent().asSequence()
                .filter { inBounds(it) }
                .filter { line.size < 2 || it != line[line.size - 2] }
                .filter { predicate(line, it) }
                .map { next -> (line + next).takeLastWhile { it.x == next.x || it.y == next.y } }
                .map { LineHeat(it, heat + this[it.last()].digitToInt()) }
                .let { queue.addAll(it) }
        }
    }

    fun part1(grid: Grid) = grid.minHeat { line, cell -> cell.isTurn(line) || line.size < 4 }

    fun part2(grid: Grid): Int {
        return grid.minHeat { line, cell ->
            val isTurn = cell.isTurn(line)
            if (cell == end()) !isTurn && line.size > 3
            else if (isTurn) line.size > 4
            else line.size < 11
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