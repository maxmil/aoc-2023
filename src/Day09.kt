import kotlin.time.measureTimedValue

fun main() {

    fun appendValue(seq: List<Int>): List<Int> =
        if (seq.all { it == 0 }) {
            seq + listOf(0)
        } else {
            val child = appendValue(seq.windowed(2).map { (a, b) -> b - a })
            seq + listOf(seq.last() + child.last())
        }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            appendValue(it.split(" ").map { s -> s.toInt() }).last()
        }
    }

    fun prefixValue(seq: List<Int>): List<Int> =
        if (seq.all { it == 0 }) {
            listOf(0) + seq
        } else {
            val child = prefixValue(seq.windowed(2).map { (a, b) -> b - a })
            (listOf(seq.first() - child.first()) + seq)//.also { println(it) }
        }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            prefixValue(it.split(" ").map { s -> s.toInt() }).first()
        }
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput).also { println(it) } == 2)

    val input = readInput("Day09")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}")}
}