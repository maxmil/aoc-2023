import kotlin.time.measureTimedValue

fun main() {

    val cache = mutableMapOf<Pair<String, List<Int>>, Long>()

    var totalFound = 0

    fun arrangements(record: String, sizes: List<Int>, resolved: String = ""): Long {
        val key = Pair(record, sizes)
        if (cache.containsKey(key)) return cache[key]!!
        var found = 0L
        var i = 0
        val size = sizes.first()
        val rest = sizes.subList(1, sizes.size)
        while (i <= record.length - size - rest.sum() - rest.size
//            && found < 1000_000_000L
        ) {
            val candidate = record.substring(i, i + size)
            if (!candidate.contains('.')) {
                if (rest.isEmpty()) {
                    if (!record.substring(i + size, record.length).contains('#')) {
//                        val r = resolved + record.substring(0, i) + "#".repeat(size) + record.substring(i + size, record.length)
//                        println("Found " + r.replace('?', ' '))
//                        if (totalFound++ > 1000) System.exit(0)
                        found += 1
                    }
                } else {
                    if (record[i + size] != '#') {
                        val resolved1 = resolved + record.substring(0, i) + "#".repeat(size) + record[i + size]
                        found += arrangements(record.substring(i + size + 1, record.length), rest, resolved1)
                    }
                }
            }
            if (candidate.startsWith('#')) return found
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
//            .also { println("$line $it") }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { arrangements(it, 1).toInt() }
    }

    fun part2(input: List<String>): Long {
//        return arrangements("?????????#?..??. 2,1,1,1", 5)
        return input.sumOf { arrangements(it, 5) }
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    measureTimedValue { part1(input) }.also { check(it.value == 7047) }
        .also { println("${it.value} in ${it.duration}") }
//    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}")}
}