import kotlin.time.measureTimedValue

fun main() {

    fun List<String>.rotateRight(): List<String> {
        val a = Array(this[0].length) { CharArray(size) }
        indices.forEach { y -> (0..<this[0].length).forEach { x -> a[x][y] = this[size - y - 1][x] } }
        return a.map { it.joinToString("") }
    }

    fun calculateLoad(grid: List<String>) =
        grid.sumOf { row -> row.withIndex().sumOf { (i, c) -> if (c == 'O') row.length - i else 0 } }

    fun List<String>.tiltLeft() = map { row ->
        val chars = row.toCharArray()
        var i = 0
        var j = 0
        while (i < chars.size) {
            if (chars[i] == '#') j = i + 1
            else if (chars[i] == 'O') {
                if (j != i) {
                    chars[j] = 'O'
                    chars[i] = '.'
                }
                j++
            }
            i++
        }
        chars.joinToString("")
    }

    fun part1(input: List<String>): Int {
        val platform = (1..3).fold(input) { platform, _ -> platform.rotateRight() }
        return calculateLoad(platform.tiltLeft())
    }

    fun part2(input: List<String>): Int {
        var platform = (1..3).fold(input) { platform, _ -> platform.rotateRight() }
        var cycles = 0
        val states = mutableMapOf<List<String>, Int>()
        while (cycles < 1000000000) {
            repeat(4) { platform = platform.tiltLeft().rotateRight() }
            cycles++
            val start = states[platform]
            if (start != null) {
                val position = (1000000000 - start) % (cycles - start)
                return calculateLoad(states.entries.first { (_, v) -> v == start + position }.key)
            }
            states[platform] = cycles
        }
        return calculateLoad(platform)
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}