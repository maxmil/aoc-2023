fun main() {
    fun part1(input: List<String>) = input
        .map { it.filter { c -> c.isDigit() } }
        .map { "${it.first()}${it.last()}" }
        .map { it.toInt() }
        .sum()

    fun part2(input: List<String>): Int {
        val numbers = listOf(
            *Array(10) { it.toString() },
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
        )
        val valid = numbers.joinToString("|").toRegex()
        val validReversed = numbers.joinToString("|").reversed().toRegex()
        fun String.toDigit() = numbers.indexOf(this) % 10

        return input.sumOf {
            valid.find(it)!!.value.toDigit() * 10 + validReversed.find(it.reversed())!!.value.reversed().toDigit()
        }
    }

    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_part2_test")) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}