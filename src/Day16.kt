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

enum class Axis(val directions: Pair<Direction, Direction>) {
    VERTICAL(Pair(NORTH, SOUTH)),
    HORIZONTAL(Pair(EAST, WEST)),
}

data class Light(val point: Point, val direction: Direction) {
    fun increment() = copy(point = point + direction.value)
}


fun main() {


//    fun Contraption.print(energized: List<Light>, current: Light) {
//        val points = energized.map { it.point }
//        forEachIndexed { y, row ->
//            row.mapIndexed { x, c ->
//                if (current.point == Point(x, y)) "*" else if (points.contains(Point(x, y))) "#" else c
//            }.joinToString("").println()
//        }
//        println("")
//    }


    fun Contraption.energized(start: Light, visited: MutableList<Light>) {
        var light = start
        while (inBounds(light.increment().point) && !visited.contains(light.increment())) {
            light = light.increment()
            visited.add(light)
            val c = this[light.point]
            if (c == '|' && light.direction.value.y == 0) {
                energized(light.copy(direction = NORTH), visited)
                energized(light.copy(direction = SOUTH), visited)
                return
            }
            if (c == '-' && light.direction.value.x == 0) {
                energized(light.copy(direction = EAST), visited)
                energized(light.copy(direction = WEST), visited)
                return
            }
            val direction = when (c) {
                '/' -> when (light.direction) {
                    NORTH -> EAST
                    EAST -> NORTH
                    SOUTH -> WEST
                    WEST -> SOUTH
                }

                '\\' -> when (light.direction) {
                    NORTH -> WEST
                    WEST -> NORTH
                    SOUTH -> EAST
                    EAST -> SOUTH
                }

                else -> light.direction
            }
            light = light.copy(direction = direction)
        }
    }

    fun Contraption.countEnergized(start: Light): Int {
        val visited = mutableListOf<Light>()
        energized(start, visited)
        val energized = visited.map { it.point }.toSet()
        return energized.size
    }

    fun part1(contraption: Contraption): Int {
        return contraption.countEnergized(Light(Point(-1, 0), EAST))
    }

    fun part2(contraption: List<String>): Int {
        val leftAndRight = contraption.indices.flatMap {
            listOf(
                Light(Point(-1, it), EAST),
                Light(Point(contraption[0].length, it), WEST)
            )
        }
        val topAndBottom = contraption[0].indices.flatMap {
            listOf(
                Light(Point(it, -1), SOUTH),
                Light(Point(it, contraption.size), NORTH),
            )
        }
        return (leftAndRight + topAndBottom).maxOf { contraption.countEnergized(it) }
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput).also { println(it) } == 46)
    check(part2(testInput) == 51)

    val input = readInput("Day16")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { check(it.value == 7521) }.also { println("${it.value} in ${it.duration}") }
}