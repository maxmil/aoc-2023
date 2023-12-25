import java.awt.geom.Line2D
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {

    data class Coords(val x: Long, val y: Long, val z: Long)
    data class Hailstone(val start: Coords, val velocity: Coords)

    fun part1(input: List<String>, testArea: Pair<Long, Long>): Int {
        val hailstones = input.map { line ->
            line.split(" @ ").map { coords ->
                coords.split(", ").map { it.trim().toLong() }.let { (x, y, z) -> Coords(x, y, z) }
            }.let { Hailstone(it[0], it[1]) }
        }

        fun line(h: Hailstone): Line2D? {
            val min = testArea.first.toDouble()
            val max = testArea.second.toDouble()
            val (t1x, t2x) = listOf((min - h.start.x) / h.velocity.x, (max - h.start.x) / h.velocity.x).sorted()
            val (t1y, t2y) = listOf((min - h.start.y) / h.velocity.y, (max - h.start.y) / h.velocity.y).sorted()
            val t1 = max(max(t1x, t1y), 0.0)
            val t2 = min(t2x, t2y)
            if (t2 < t1) return null
            val x1 = h.start.x + t1 * h.velocity.x
            val x2 = h.start.x + t2 * h.velocity.x
            val y1 = h.start.y + t1 * h.velocity.y
            val y2 = h.start.y + t2 * h.velocity.y
            return Line2D.Double(x1, y1, x2, y2)
        }

        val lines = hailstones.mapNotNull { line(it) }
        var collisions = 0
        lines.forEachIndexed { i, a ->
            lines.subList(i + 1, lines.size).forEach { b -> if (a.intersectsLine(b)) collisions++ }
        }
        return collisions
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    val testInput = readInput("Day24_test")
    check(part1(testInput, 7L to 27L) == 2)
    // check(part2(testInput) == 1)

    val input = readInput("Day24")
    val testArea = 200000000000000 to 400000000000000
    measureTimedValue { part1(input, testArea) }.also { println("${it.value} in ${it.duration}") }
    // measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}")}

}