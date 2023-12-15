import kotlin.time.measureTimedValue


fun main() {

    data class Lens(val label: String, val focalLength: Int)

    fun String.hash() = fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }.toInt()

    fun part1(input: String) = input.split(",").sumOf { it.hash() }

    fun part2(input: String): Int {
        val boxes = Array(256) { mutableListOf<Lens>() }
        input.split(",").forEach { s ->
            s.split("[=\\-]".toRegex()).let { (label, focalLength) ->
                boxes[label.hash()].let { lenses ->
                    val index = lenses.indexOfFirst { it.label == label }
                    if (focalLength.isNotEmpty()) {
                        val lens = Lens(label, focalLength.toInt())
                        if (index > -1) lenses[index] = lens
                        else lenses.add(lens)
                    } else if (index > -1) lenses.removeAt(index)
                }
            }
        }

        return boxes.withIndex().sumOf { (boxIndex, lenses) ->
            lenses.withIndex().sumOf { (lensIndex, pair) -> (boxIndex + 1) * (lensIndex + 1) * pair.focalLength }
        }
    }

    val testInput = readInputText("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInputText("Day15")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { check(it.value == 268497) }
        .also { println("${it.value} in ${it.duration}") }
}