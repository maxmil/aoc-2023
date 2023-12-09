import kotlin.time.measureTimedValue

fun main() {
    fun part1(input: List<String>): Int {
        val dirs = input.first()
        val nodes = input.drop(2).associate { it.substring(0, 3) to (it.substring(7..9) to it.substring(12..14)) }
        var i = 0
        var curr = "AAA"
        while (curr != "ZZZ") {
            curr = if (dirs[i % dirs.length] == 'L') nodes[curr]!!.first else nodes[curr]!!.second
            i++
        }
        return i
    }
    data class Cycle(val start: Int, val size: Int, val endNodes: List<Int>)

    fun findCycleIntercept(cycles: List<Cycle>): Long {
        val cycle = cycles.first()
        val others = cycles.drop(1)
        var cnt = 0L
        while (true) {
            cycle.endNodes.forEach { endNode ->
                val pos = cnt * cycle.size + endNode
                if (others.all { other -> other.endNodes.any { (pos - it) % other.size == 0L } }) {
                    return pos + 1
                }
                cnt++
            }
        }
    }

    fun detectCycles(allNodes: Map<String, Pair<String, String>>, dirs: String): Array<Cycle?> {
        data class NodeAndMove(val pos: Int, val node: String)

        var nodes = allNodes.keys.filter { it.endsWith('A') }
        val seenNodes = Array<MutableMap<NodeAndMove, Int>>(nodes.size) { mutableMapOf() }
        val cycles = Array<Cycle?>(nodes.size) { null }
        var i = 0
        while (cycles.any { it == null }) {
            val moveIndex = i % dirs.length
            nodes = nodes.map { if (dirs[moveIndex] == 'L') allNodes[it]!!.first else allNodes[it]!!.second }
            nodes.forEachIndexed { index, node ->
                if (cycles[index] == null) {
                    val seenAt = seenNodes[index][NodeAndMove(moveIndex, node)]
                    if (seenAt != null) {
                        val endNodes = seenNodes[index].filter { it.key.node.endsWith('Z') }.map { it.key.pos }
                        cycles[index] = Cycle(seenAt, i - seenAt, endNodes)
                    } else {
                        seenNodes[index][NodeAndMove(i, node)] = i
                    }
                }
            }
            i++
        }
        return cycles
    }

    fun part2(input: List<String>): Long {
        val dirs = input.first()
        val nodes = input.drop(2).associate { it.substring(0, 3) to (it.substring(7..9) to it.substring(12..14)) }
        val cycles = detectCycles(nodes, dirs)
        return findCycleIntercept(cycles.filterNotNull())
    }

    check(part1(readInput("Day08_test")) == 6)
    check(part2(readInput("Day08_part2_test")) == 6L)

    val input = readInput("Day08")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}