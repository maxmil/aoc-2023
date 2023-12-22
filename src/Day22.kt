import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {

    data class Point(val x: Int, val y: Int, val z: Int) {
        operator fun minus(point: Point) = Point(x - point.x, y - point.y, z - point.z)
        operator fun plus(point: Point) = Point(x + point.x, y + point.y, z + point.z)
    }

    data class Brick(val id: Int, val start: Point, val end: Point) {
        fun minZ() = start.z
        fun points(z: Int): List<Point> {
            if (end == start) return listOf(start.copy(z = z))
            val diff = (end - start)
            val dist = max(diff.x, max(diff.y, diff.z))
            return (0..dist).map {
                start.copy(z = z) + Point(diff.x * it / dist, diff.y * it / dist, diff.z * it / dist)
            }
        }

        fun dropTo(z: Int): Brick {
            val inc = z - this.start.z
            return this.copy(start = start.copy(z = start.z + inc), end = end.copy(z = end.z + inc))
        }
    }

    fun processInput(input: List<String>): Pair<MutableMap<Brick, Set<Brick>>, List<Brick>> {
        val bricks = input.mapIndexed { i, line ->
            line.split("~")
                .map { coords -> coords.split(",").map { it.toInt() }.let { (x, y, z) -> Point(x, y, z) } }
                .let { points -> points.sortedBy { it.z }.let { Brick(i, it[0], it[1]) } }
        }.sortedBy { it.minZ() }

        val pile = mutableMapOf<Point, Brick>()
        val supportedBy = mutableMapOf<Brick, Set<Brick>>()
        val fallen = bricks.map { b ->
            var z = b.minZ()
            while (b.points(z - 1).all { !pile.contains(it) } && z > 1) z--
            val moved = b.dropTo(z)
            supportedBy[moved] = b.points(z - 1).mapNotNull { pile[it] }.toSet()
            pile.putAll(b.points(z).associateWith { moved })
            moved
        }
        return Pair(supportedBy, fallen)
    }

    fun part1(input: List<String>): Int {
        val (supportedBy, fallen) = processInput(input)
        return fallen.filter { !supportedBy.values.contains(setOf(it)) }.size
    }

    fun countFallen(supportedBy: Map<Brick, Set<Brick>>, brick: Brick): Int {
        val fallen = mutableSetOf(brick)
        while (true) {
            val fall = supportedBy.filter {
                !fallen.contains(it.key)
                        && it.value.isNotEmpty()
                        && it.value.all { b -> fallen.contains(b) }
            }.keys
            if (fall.isEmpty()) return fallen.count() - 1
            fallen.addAll(fall)
        }
    }

    fun part2(input: List<String>): Int {
        val (supportedBy, fallen) = processInput(input)
        return fallen.sumOf { countFallen(supportedBy, it) }
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)

    val input = readInput("Day22")
    measureTimedValue { part1(input) }.also { check(it.value == 446) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { check(it.value == 60287) }
        .also { println("${it.value} in ${it.duration}") }
}
