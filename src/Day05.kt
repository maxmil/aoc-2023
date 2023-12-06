import kotlin.time.measureTimedValue

fun main() {

    fun String.parseMaps(): List<Map<LongRange, LongRange>> = split("\n\n").drop(1).map { m ->
        m.split("\n").drop(1).associate { range ->
            range.split(" ").map { it.toLong() }.let { (dest, src, range) ->
                Pair(src..<src + range, dest..<dest + range)
            }
        }
    }

    fun part1(input: String): Long {
        val seeds = input.substringBefore("\n").substringAfter(": ")
            .split(" ").map { it.toLong() }
        val maps = input.parseMaps()
        return seeds.minOf { s ->
            maps.fold(s) { v, map ->
                map.entries.firstOrNull { v in it.key }
                    ?.let { (src, dest) -> dest.first + v - src.first }
                    ?: v
            }
        }
    }

    fun part2(input: String): Long {
        val seedRanges = input.substringBefore("\n").substringAfter(": ")
            .split(" ").map { it.toLong() }.chunked(2).map { (a, b) -> a..<a + b }
        val maps = input.parseMaps().reversed()

        var location = 0L
        while (true) {
            val seed = maps.fold(location) { v, map ->
                map.entries.firstOrNull { v in it.value }
                    ?.let { (src, dest) -> src.first + v - dest.first }
                    ?: v
            }
            if (seedRanges.any { it.contains(seed) }) return location
            location++
        }
    }

    val testInput = readInputText("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInputText("Day05")
//    check(part1(input) == 51752125L)
//    check(part2(input) == 12634632L)
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}