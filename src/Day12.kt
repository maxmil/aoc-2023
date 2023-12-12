import kotlin.time.measureTimedValue

fun main() {

    fun permutations(record: String, sizes: List<Int>, resolved: String = ""): Int {
        var found = 0
        var i = 0
        val size = sizes.first()
        val rest = sizes.subList(1, sizes.size)
        while (i <= record.length - size - rest.sum() - rest.size) {
            val candidate = record.substring(i, i + size)
            if (!candidate.contains('.')) {
                if (rest.isEmpty()) {
                    if (!record.substring(i + size, record.length).contains('#')) {
//                        val r = resolved + record.substring(0, i) + "#".repeat(size) + record.substring(i + size, record.length)
//                        println("Found " + r.replace('?', '.'))
                        found += 1
                    }
                } else {
                    if (record[i + size] != '#') {
                        val resolved1 = resolved + record.substring(0, i) + "#".repeat(size) + record[i + size + 1]
                        found += permutations(record.substring(i + size + 1, record.length), rest, resolved1)
                    }
                }
            }
            if (candidate.startsWith('#')) return found
            i++
        }
        return found
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val (record, sizesString) = line.split(" ")
            val sizes = sizesString.split(",").map { it.toInt() }
            permutations(record, sizes)
        }
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
    // check(part2(testInput) == 1)

    val input = readInput("Day12")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    // measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}")}
}