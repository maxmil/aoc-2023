import java.lang.Integer.parseInt
import java.lang.Long.parseLong
import kotlin.math.abs
import kotlin.time.measureTimedValue

fun main() {

    data class Move(val direction: String, val distance: Int)

    fun lagoonSize(moves: List<Move>): Long {
        val points = moves.fold(listOf(Point(0, 0))) { acc, (direction, distance) ->
            val point = when (direction) {
                "U" -> acc.last() + Point(0, -1 * distance)
                "R" -> acc.last() + Point(1 * distance, 0)
                "D" -> acc.last() + Point(0, 1 * distance)
                else -> acc.last() + Point(-1 * distance, 0)
            }
            acc + point
        }

        val area = abs(points.foldIndexed(0L) { i, acc, point ->
            val next = points[(i + 1) % points.size]
            acc + point.x.toLong() * next.y - point.y.toLong() * next.x
        } / 2)//.also { println("area $it") }

        val distances = moves.sumOf { it.distance.toLong() }//.also { println("distances: $it") }

        return area + 1 - distances / 2 + distances
    }

    fun part1(input: List<String>): Long {
        val moves = input.map { it.split(" ").let { (dir, dist) -> Move(dir, dist.toInt()) } }
        return lagoonSize(moves)
    }

    fun part2(input: List<String>): Long {
        val moves = input.map {
            it.split(" ").let { (_, _, hex) ->
                val dir = when (hex[7]) {
                    '0' -> "R"
                    '1' -> "D"
                    '2' -> "L"
                    else -> "U"
                }
                Move(dir, parseInt(hex.substring(2, hex.length - 2), 16)).also { println("$hex $it")}
            }
        }
//        moves.forEach { println(it) }
        return lagoonSize(moves)
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput).also { println(it) } == 62L)
    check(part2(testInput).also { println(it) } == 952408144115)

    val input = readInput("Day18")
    measureTimedValue { part1(input) }.also { check(it.value == 74074L) }
        .also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}