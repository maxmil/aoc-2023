fun main() {
    fun part1(input: List<String>) = input
        .map { it.filter { c -> c.isDigit() } }
        .map { "${it.first()}${it.last()}" }
        .sumOf { it.toInt() }

    fun part2(input: List<String>): Int {
        val numbers = listOf(
            *Array(10) { it.toString() },
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
        )
        fun String.first() = numbers.joinToString("|").toRegex()
            .find(this)!!.value
            .let { n -> numbers.indexOf(n) % 10 }
        fun String.last() = numbers.joinToString("|").reversed().toRegex()
            .find(this.reversed())!!.value
            .let { n -> numbers.indexOf(n.reversed()) % 10 }

        return input.sumOf { it.first() * 10 + it.last() }
    }

    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_part2_test")) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}