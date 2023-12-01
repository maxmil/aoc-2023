fun main() {
    fun part1(input: List<String>) = input
        .map { it.filter { c -> c.isDigit() } }
        .map { "${it.first()}${it.last()}" }
        .map { it.toInt() }
        .sum()

    fun part2(input: List<String>): Int {
        val numbers = listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        val valid = "\\d|(${numbers.joinToString(")|(")})".toRegex()
        val validReversed = "\\d|(${numbers.joinToString(")|(") { it.reversed() }})".toRegex()
        fun String.toDigit() = numbers.indexOf(this).let { if (it > -1) it.toString() else this }

        return input
            .map {
                Pair(
                    valid.find(it)!!.value.toDigit(),
                    validReversed.find(it.reversed())!!.value.reversed().toDigit()
                )
            }.sumOf { (a, b) -> (a + b).toInt() }
    }

    check(part1(readInput("Day01_test")) == 142)
    check(part2(readInput("Day01_part2_test")).also { println(it) } == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
