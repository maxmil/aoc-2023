fun main() {
    fun part1(input: List<String>) =
        input.map { it.filter { c -> c.isDigit() } }
            .map { "${it.first()}${it.last()}" }
            .sumOf { it.toInt() }

    fun part2(input: List<String>): Int {
        val numbers = arrayOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        fun String.toDigit() = numbers.indexOf(this).let { if (it > -1) it else this.toInt() }

        return input.sumOf { s ->
            "\\d|(${numbers.joinToString(")|(")})".toRegex().findAll(s)
                .map { it.value.toDigit() }
                .let { it.first() * 10 + it.last() }
        }
    }

    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_part2_test")) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
