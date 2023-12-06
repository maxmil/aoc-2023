import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.measureTimedValue


fun main() {

    /*
        speed * (time - speed) > distance
        s(t - s) > d
        s^2 - st + d > 0

        Using quadratic formula the two intercepts are the results of
        (t +- sqrt(t^2 - 4d)) / 2
     */
    fun quadraticDiff(time: Double, distance: Double): Int {
        val variance = sqrt(time.pow(2) - 4 * distance)
        val min = ((time - variance) / 2).toInt() + 1
        val max = ((time + variance) / 2).let { if (floor(it) == it) it.toInt() - 1 else it.toInt() }
        return (max - min + 1)
    }

    fun part1(input: List<String>): Int {
        return input.map { it.substringAfter(":").trim().split(" +".toRegex()).map { it.toDouble() } }
            .let { it[0].zip(it[1]) }
            .map { (time, distance) -> quadraticDiff(time, distance) }.reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Int {
        return input.map { it.substringAfter(":").replace(" ", "").toDouble() }
            .let { (time, distance) -> quadraticDiff(time, distance) }
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}