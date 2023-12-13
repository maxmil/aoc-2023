import kotlin.time.measureTimedValue

fun main() {

    val cache = mutableMapOf<Pair<String, List<Int>>, Long>()

    fun arrangements(record: String, sizes: List<Int>): Long {
        val key = Pair(record, sizes)
        if (cache.containsKey(key)) return cache[key]!!
        var found = 0L
        var i = 0
        val size = sizes.first()
        val rest = sizes.subList(1, sizes.size)
        while (i <= record.length - size - rest.sum() - rest.size) {
            val candidate = record.substring(i, i + size)
            if (!candidate.contains('.')) {
                if (rest.isEmpty()) {
                    if (!record.substring(i + size, record.length).contains('#')) found += 1
                } else if (record[i + size] != '#') {
                    found += arrangements(record.substring(i + size + 1, record.length), rest)
                }
            }
            if (candidate.startsWith('#')) break
            i++
        }
        cache[key] = found
        return found
    }

    fun arrangements(line: String, repeat: Int): Long {
        val (record, sizesString) = line.split(" ")
        val sizes = sizesString.split(",").map { it.toInt() }
        val recordRepeated = (Array(repeat) { record }).joinToString("?")
        val sizesRepeated = (Array(repeat) { sizes }).flatMap { it }
        return arrangements(recordRepeated, sizesRepeated)
    }

    fun part1(input: List<String>) = input.sumOf { arrangements(it, 1) }

    fun part2(input: List<String>) = input.sumOf { arrangements(it, 5) }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}