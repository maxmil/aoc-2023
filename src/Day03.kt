import java.util.function.Predicate
import kotlin.time.measureTimedValue

fun main() {
    data class Symbol(val pos: Cell, val value: Char)
    data class Part(val pos: Cell, val value: Int) {
        fun adjacentTo(cell: Cell) = cell.x >= pos.x - 1 &&
                cell.x <= pos.x + value.toString().length &&
                cell.y >= pos.y - 1 &&
                cell.y <= pos.y + 1
    }

    fun partsBySymbol(input: List<String>, isSymbol: Predicate<Char>): Map<Symbol, List<Part>> {
        val symbols = input.flatMapIndexed { y, line ->
            line.withIndex()
                .filter { (_, c) -> isSymbol.test(c)}
                .map { (x, c) -> Symbol(Cell(x, y), c) }
        }.toSet()

        return  input.flatMapIndexed { y, line ->
            var word = ""
            line.flatMapIndexed { x, c ->
                if (c.isDigit()) word += c
                if (word.isNotEmpty() && (x == line.length - 1 || !line[x + 1].isDigit())) {
                    val part = Part(Cell(x - word.length + 1, y), word.toInt())
                    word = ""
                    symbols.filter { part.adjacentTo(it.pos) }.map { Pair(it, part)}
                } else {
                    emptyList()
                }
            }
        }.groupBy({ it.first }, {it.second})
    }

    fun part1(input: List<String>): Int {
        return partsBySymbol(input) { !it.isDigit() && it != '.' }.values.flatten().toSet().sumOf { it.value }
    }

    fun part2(input: List<String>): Int {
        return partsBySymbol(input) { it == '*' }
            .filter { (_, parts) -> parts.size > 1 }
            .map { (_, parts) -> parts.fold(1) { acc, part -> acc * part.value } }
            .sum()
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}")}
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}")}
}