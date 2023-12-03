import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Cell(val x: Int, val y: Int)
//    operator fun List<String>.get(cell:Cell) = this[cell.y][cell.x]

    fun part1(input: List<String>): Int {
        val symbols = input.flatMapIndexed { y, line ->
            line.withIndex()
                .filter { (_, c) -> !c.isDigit() && c != '.' }
                .map { (x, _) -> Cell(x, y) }
        }.toSet()

        val parts = mutableListOf<Int>()
        input.forEachIndexed { y, line ->
            var word = ""
            line.forEachIndexed { x, c ->
                if (c.isDigit()) word += c
                if (word.isNotEmpty() && (x == line.length - 1 || !line[x + 1].isDigit())) {
                    val above =
                        if (y == 0) emptyList() else (max(x - word.length, 0) until min(line.length, x + 2)).map {
                            Cell(
                                it,
                                y - 1
                            )
                        }
                    val left = if (x < word.length) emptyList() else listOf(Cell(x - word.length, y))
                    val right = if (x == line.length - 1) emptyList() else listOf(Cell(x + 1, y))
                    val bottom = if (y == input.size - 1) emptyList() else (max(x - word.length, 0) until min(
                        line.length,
                        x + 2
                    )).map { Cell(it, y + 1) }
                    if ((above + left + right + bottom).any { symbols.contains(it) }) {
                        parts.add(word.toInt())
                    }
                    word = ""
                }
            }
        }

        return parts.sum()
    }

    fun part2(input: List<String>): Int {
        val symbols = input.flatMapIndexed { y, line ->
            line.withIndex()
                .filter { (_, c) -> !c.isDigit() && c != '.' }
                .map { (x, _) -> Cell(x, y) }
        }.toSet()

        val partsBySymbol = mutableMapOf<Cell, MutableList<Int>>()
        symbols.forEach { partsBySymbol[it] = mutableListOf<Int>() }
        input.forEachIndexed { y, line ->
            var word = ""
            line.forEachIndexed { x, c ->
                if (c.isDigit()) word += c
                if (word.isNotEmpty() && (x == line.length - 1 || !line[x + 1].isDigit())) {
                    val above =
                        if (y == 0) emptyList() else (max(x - word.length, 0) until min(line.length, x + 2)).map {
                            Cell(
                                it,
                                y - 1
                            )
                        }
                    val left = if (x < word.length) emptyList() else listOf(Cell(x - word.length, y))
                    val right = if (x == line.length - 1) emptyList() else listOf(Cell(x + 1, y))
                    val bottom = if (y == input.size - 1) emptyList() else (max(x - word.length, 0) until min(
                        line.length,
                        x + 2
                    )).map { Cell(it, y + 1) }
                    (above + left + right + bottom).filter { symbols.contains(it) }
                        .forEach { symbol -> partsBySymbol[symbol]!!.add(word.toInt()) }
                    word = ""
                }
            }
        }

//        println(partsBySymbol)

        return partsBySymbol.values.filter { it.size > 1 }
            .sumOf { it.reduce { a, b -> a * b } }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}