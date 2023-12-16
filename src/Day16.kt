import Direction.*
import kotlin.time.measureTimedValue

typealias Contraption = List<String>

fun Contraption.inBounds(p: Point) = p.y in (indices) && p.x in (0..<this[0].length)
operator fun Contraption.get(p: Point) = if (inBounds(p)) this[p.y][p.x] else null

data class Point(val x: Int, val y: Int) {
    operator fun plus(p: Point) = Point(x + p.x, y + p.y)
}

enum class Direction(val value: Point) {
    NORTH(Point(0, -1)),
    EAST(Point(1, 0)),
    SOUTH(Point(0, 1)),
    WEST(Point(-1, 0)),
}

data class Light(val point: Point, val direction: Direction) {
    fun next(direction: Direction) = Light(point + direction.value, direction)
}

fun main() {

    fun Contraption.energized(start: Light): Int {
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

    fun part1(contraption: Contraption) = contraption.energized(Light(Point(0, 0), EAST))

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
    measureTimedValue { part2(input) }.also { check(it.value == 7521) }
        .also { println("${it.value} in ${it.duration}") }
}