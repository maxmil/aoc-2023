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

    data class NodePosition(val pos: Int, val node: String)
    data class Cycle(val startNode: String, val start: Int, val end: Int, val endNodes: List<NodePosition>) {
        fun size() = end - start
    }

    fun findSolution(cycles: MutableList<Cycle>):Long {
        val cycle = cycles.first()
        val others = cycles.drop(1)
        var cnt = 0L
        while(true) {
            val pos = cnt * cycle.size() + cycle.start + cycle.endNodes[0].pos
            if (cnt % 1000_000 == 0L) println("pos=${pos}")
//            others.map { other -> other.endNodes.map {endNode ->
//                println("${other.startNode} ${(pos - other.start - endNode.pos)} ${other.size()}")
//            } }
            if (others.all {other -> other.endNodes.any { (pos - other.start - it.pos) % other.size() == 0L } }){
                return pos + 1
            }
            cnt ++
        }
    }

    fun part2(input: List<String>): Long {
        val dirs = input.first()
        val nodes = input.drop(2).associate { it.substring(0, 3) to (it.substring(7..9) to it.substring(12..14)) }
        val firstSeenByStartNode = nodes.keys.filter { it.endsWith('A') }.associateWith { mutableMapOf<NodePosition, Int>() }
        val startNodes = firstSeenByStartNode.keys.toList()
        var currNodes = startNodes
        val cycles = mutableListOf<Cycle>()
        var i = 0
        while (cycles.size < startNodes.size) {
            val pos = i % dirs.length
            currNodes = currNodes.map { if (dirs[pos] == 'L') nodes[it]!!.first else nodes[it]!!.second }
            currNodes.forEachIndexed { nodeInd, node ->
                val startNode = startNodes[nodeInd]
                if (cycles.all { it.startNode != startNode }) {
                    val seenNodes = firstSeenByStartNode[startNode]!!
                    val seenAt = seenNodes[NodePosition(pos, node)]
                    if (seenAt != null) {
//                        println(seenNodes.entries.groupingBy { it.key.node }.eachCount())
                        val endNodes = seenNodes.entries.filter { it.key.node.endsWith('Z') }
                            .map { it.key.copy(pos = it.key.pos - seenAt) }
                        cycles.add(Cycle(startNode, seenAt, i, endNodes))
                    } else {
                        seenNodes[NodePosition(i, node)] = i
                    }
                }
            }
            i ++
        }

        println(cycles)

        return findSolution(cycles)
//        return 0
    }

    check(part1(readInput("Day08_test")) == 6)
//    check(part2(readInput("Day08_part2_test")).also { println(it) } == 6L)

    val input = readInput("Day08")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }

    // 217248968842 too low
    // 5328603012082
}