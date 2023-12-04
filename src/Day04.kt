import kotlin.math.pow
import kotlin.time.measureTimedValue

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val (wining, numbers) = line.substringAfter(":").split("|").map { it.trim().split(" +".toRegex()).toSet() }
            wining.intersect(numbers).size.let { if (it == 0) 0 else 2.0.pow(it - 1).toInt() }
        }
    }

    fun part2(input: List<String>): Int {
        val cards = Array(input.size) { 1 }
        input.forEachIndexed { i, line ->
            val (wining, numbers) = line.substringAfter(":").split("|").map { it.trim().split(" +".toRegex()).toSet() }
            val won = wining.intersect(numbers).size
            (i + 1..i + won).forEach { cards[it] += cards[i] }
        }
        return cards.sum()
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}