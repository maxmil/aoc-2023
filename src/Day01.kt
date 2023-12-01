fun main() {
    fun part1(input: List<String>) =
        input.map { it.filter { c -> c.isDigit() } }
            .map { "${it.first()}${it.last()}" }
            .sumOf { it.toInt() }

    fun part2(input: List<String>): Int {
        val numbers = listOf(
            *(0..9).map { it.toString() }.toTypedArray(),
            "zero",
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine"
        )

        return input.sumOf { s ->
            "\\d|${numbers.joinToString("|")}".toRegex().findAll(s)
                .map { numbers.indexOf(it.value) % 10 }
                .let { it.first() * 10 + it.last() }
        }
    }

    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_part2_test")) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
