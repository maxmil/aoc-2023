import kotlin.math.max
import kotlin.time.measureTimedValue

fun main() {

    data class Point3D(val x: Int, val y: Int, val z: Int) {
        operator fun minus(point: Point3D) = Point3D(x - point.x, y - point.y, z - point.z)
        operator fun plus(point: Point3D) = Point3D(x + point.x, y + point.y, z + point.z)
        operator fun div(n: Int) = Point3D(x / n, y / n, z / n)
        operator fun times(n: Int) = Point3D(x * n, y * n, z * n)
    }

    data class Brick(val location: Point3D, val size: Point3D) {
        fun points(): List<Point3D> {
            val dist = max(size.x, max(size.y, size.z))
            val inc = if (dist > 0) (size / dist) else Point3D(0, 0, 0)
            return (0..dist).map { location + inc * it }
        }

        fun moveTo(z: Int) = this.copy(location = location.copy(z = z))
    }

    fun List<String>.dropBricks(): MutableMap<Brick, Set<Brick>> {
        val initial = map { line ->
            line.split("~")
                .map { coords -> coords.split(",").map { it.toInt() }.let { (x, y, z) -> Point3D(x, y, z) } }
                .let { points -> points.let { Brick(it[0], it[1] - it[0]) } }
        }.sortedBy { it.location.z }

        val pile = mutableMapOf<Point3D, Brick>()
        val bricks = mutableMapOf<Brick, Set<Brick>>()
        initial.forEach { b ->
            var z = b.location.z
            while (b.moveTo(z - 1).points().all { !pile.contains(it) } && z > 1) z--
            val moved = b.moveTo(z)
            bricks[moved] = b.moveTo(z - 1).points().mapNotNull { pile[it] }.toSet()
            pile.putAll(moved.points().associateWith { moved })
        }
        return bricks
    }

    fun part1(input: List<String>): Int {
        val bricks = input.dropBricks()
        return bricks.keys.filter { !bricks.values.contains(setOf(it)) }.size
    }

    fun countFallen(bricks: Map<Brick, Set<Brick>>, brick: Brick): Int {
        val canFall = bricks.filter { it.value.isNotEmpty() }
        val fallen = mutableSetOf(brick)
        while (true) {
            val falling = canFall.filter { !fallen.contains(it.key) && it.value.all { b -> fallen.contains(b) } }.keys
            if (falling.isEmpty()) return fallen.count() - 1
            fallen.addAll(falling)
        }
    }

    fun part2(input: List<String>): Int {
        val bricks = input.dropBricks()
        return bricks.keys.sumOf { countFallen(bricks, it) }
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 7)

    val input = readInput("Day22")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}
