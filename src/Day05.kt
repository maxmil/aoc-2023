import kotlin.time.measureTimedValue

fun main() {
    data class Range(val src: Long, val dest: Long, val range: Long)

    fun String.parseMaps(): List<List<Range>> = split("\n\n").drop(1).map { m ->
        m.split("\n")
            .drop(1).map { range ->
                range.split(" ").map { it.toLong() }.let { v -> Range(v[1], v[0], v[2]) }
            }
    }

    fun part1(input: String): Long {
        val seeds = input.substringBefore("\n").substringAfter(": ")
            .split(" ").map { it.toLong() }
        val maps = input.parseMaps()
        return seeds.minOf { s -> maps.fold(s) { src, ranges ->
                ranges.find { it.src <= src && it.src + it.range >= src }
                    ?.let { it.dest + src - it.src }
                    ?: src
            }
        }
    }

    fun part2(input: String): Long {
        val seedRanges = input.substringBefore("\n").substringAfter(": ")
            .split(" ").map { it.toLong() }.chunked(2).map { (a, b) -> a until a + b }
        val maps = input.parseMaps().reversed()

        var location = 0L
        while (true) {
            val seed = maps.fold(location) { dest, ranges ->
                ranges.find { it.dest <= dest && it.dest + it.range > dest }
                    ?.let { it.src + dest - it.dest }
                    ?: dest
            }
            if (seedRanges.any { it.contains(seed) }) return location
            location++
        }
    }

    val testInput = readInputText("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInputText("Day05")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}