import java.lang.Integer.parseInt
import kotlin.math.abs
import kotlin.time.measureTimedValue

enum class DiggerDirection(val value: Point) {
    R(Point(1, 0)),
    D(Point(0, 1)),
    L(Point(-1, 0)),
    U(Point(0, -1)),
}

fun main() {
    data class Move(val direction: DiggerDirection, val distance: Int)

    fun List<Move>.lagoonSize(): Long {
        val points = fold(listOf(Point(0, 0))) { acc, (direction, distance) ->
            acc + (acc.last() + direction.value * distance)
        }
        val area = abs(points.foldIndexed(0L) { i, acc, point ->
            val next = points[(i + 1) % points.size]
            acc + point.x.toLong() * next.y - point.y.toLong() * next.x
        } / 2)
        val perimeter = sumOf { it.distance }
        return area + 1 + perimeter / 2
    }

    fun part1(input: List<String>): Long {
        val moves = input.map {
            it.split(" ").let { (dir, dist) -> Move(DiggerDirection.valueOf(dir), dist.toInt()) }
        }
        return moves.lagoonSize()
    }

    fun part2(input: List<String>): Long {
        val moves = input.map {
            it.split(" ").let { (_, _, hex) ->
                val dir = DiggerDirection.entries[hex[7].digitToInt()]
                Move(dir, parseInt(hex.substring(2, hex.length - 2), 16))
            }
        }
        return moves.lagoonSize()
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62L)
    check(part2(testInput) == 952408144115)

    val input = readInput("Day18")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}