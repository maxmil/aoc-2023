import Direction.*
import kotlin.time.measureTimedValue

fun main() {
    data class Light(val point: Point, val direction: Direction) {
        fun next(direction: Direction) = Light(point + direction.value, direction)
    }

    fun Grid.energized(start: Light): Int {
        val energised = mutableSetOf<Light>()
        val queue = ArrayDeque(listOf(start))
        while (queue.isNotEmpty()) {
            val light = queue.removeFirst()
            if (inBounds(light.point) && !energised.contains(light)) {
                energised.add(light)
                val c = this[light.point]
                if (c == '|' && light.direction.value.y == 0) {
                    queue.addAll(listOf(NORTH, SOUTH).map { light.next(it) })
                } else if (c == '-' && light.direction.value.x == 0) {
                    queue.addAll(listOf(EAST, WEST).map { light.next(it) })
                } else if (c == '/') {
                    val ordinal = light.direction.ordinal.let { (it / 2) * 2 + 1 - it % 2 }
                    queue.add(light.next(Direction.entries[ordinal]))
                } else if (c == '\\') {
                    val ordinal = light.direction.ordinal.let { (1 - it / 2) * 2 + (1 - it % 2) }
                    queue.add(light.next(Direction.entries[ordinal]))
                } else {
                    queue.add(light.next(light.direction))
                }
            }
        }
        return energised.map { it.point }.toSet().size
    }

    fun part1(contraption: Grid) = contraption.energized(Light(Point(0, 0), EAST))

    fun part2(contraption: List<String>): Int {
        return (0..contraption.size).flatMap {
            listOf(
                Light(Point(0, it), EAST),
                Light(Point(contraption[0].length - 1, it), WEST),
                Light(Point(it, 0), SOUTH),
                Light(Point(it, contraption.size - 1), NORTH),
            )
        }.maxOf { contraption.energized(it) }
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}