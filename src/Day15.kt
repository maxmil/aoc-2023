import kotlin.time.measureTimedValue


fun main() {

    fun String.hash() = fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }.toInt()

    fun part1(input: String) = input.split(",").sumOf { it.hash() }

    fun part2(input: String): Int {
        val boxes = Array(256) { LinkedHashMap<String, Int>() }
        input.split(",").forEach { s ->
            s.split("[=\\-]".toRegex()).let { (label, focalLength) ->
                boxes[label.hash()].let { lenses ->
                    if (focalLength.isNotEmpty()) lenses[label] = focalLength.toInt()
                    else lenses.remove(label)
                }
            }
        }

        return boxes.withIndex().sumOf { (boxIndex, lenses) ->
            lenses.values.withIndex().sumOf { (lensIndex, focalLength) -> (boxIndex + 1) * (lensIndex + 1) * focalLength }
        }
    }

    val testInput = readInputText("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInputText("Day15")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}