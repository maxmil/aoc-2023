import kotlin.time.measureTimedValue

typealias Graph = MutableMap<String, MutableMap<String, Int>>

fun main() {

    data class MinCut(val s: String, val weight: Int)

    data class MinCutPhase(val st:String, val adjacencyList:List<String>, val graph: Graph, val minCut: MinCut)

    /*
     * Implementation of Stoer-Wagner min cut algorithm. This has time complexity of n2*log(n) which means it's a
     * bit slow but was fun to implement.
     *
     * Sped up a little by reusing the adjacency list. I've seen other versions that also increase speed by using a
     * priority queue however this requires a special priority queue that supports fast element lookups and removals
     * which is not the case for the JVM PriorityQueue.
     *
     * There is also an implementation using an adjacency matrix that is probably faster.
     *
     * See https://en.wikipedia.org/wiki/Stoer%E2%80%93Wagner_algorithm
     */
    fun minCutPhase(start: String, graph: Graph, lastA: List<String>, lastST: String): MinCutPhase {
        val A = mutableListOf(start)

        // Optimisation on standard Stoer Wagner that copies last max adjacency list members that have not changed
        // This is a little faster than recalculating them
        lastA.drop(1).takeWhile { lastST.indexOf(it) == -1 && graph.getValue(it).keys.none { it == lastST } }.forEach { A.add(it) }
        while (A.size < graph.keys.size) {
            val z = A.flatMap { graph.getValue(it).entries }.filter { !A.contains(it.key) }
                .groupBy ({ it.key }, {it.value})
                .mapValues { (_, v) -> v.sum() }
                .maxBy { (_, v) -> v }
                .key
            A.add(z)
        }

        val (s, t) = A.takeLast(2)
        val st = "$s:$t"
        val minCut = MinCut(t, graph.getValue(t).values.sum())

        val shrunk = graph.filter { it.key != s && it.key != t }.mapValues { (_, v) ->
            val weights = v.filterKeys { it != s && it != t }.toMutableMap()
            val weightST = (v[s] ?: 0) + (v[t] ?: 0)
            if (weightST > 0) weights[st] = weightST
            weights
        }.toMutableMap()

        val merged = (graph.getValue(s).keys + graph.getValue(t).keys).associateWith { key ->
            (graph.getValue(s)[key] ?: 0) + (graph.getValue(t)[key] ?: 0)
        }.filterKeys { it != s && it != t }
        shrunk[st] = merged.toMutableMap()

        return MinCutPhase(st, A, graph, minCut)
    }

    fun part1(input: List<String>): Int {
        var graph = mutableMapOf<String, MutableMap<String, Int>>()
        input.forEach { line ->
            line.split(": ").let { (id, nodes) ->
                nodes.split(" ").forEach {
                    graph.computeIfAbsent(id) { mutableMapOf() }[it] = 1
                    graph.computeIfAbsent(it) { mutableMapOf() }[id] = 1
                }
            }
        }

        val start = graph.keys.first()
        val nodes = graph.keys.size
        var A = listOf<String>()
        var st = ""
        while (graph.keys.size > 1) {
            val state = minCutPhase(start, graph, A, st)
            if (state.minCut.weight == 3) return state.minCut.s.split(":").size.let { it * (nodes - it) }
            graph = state.graph
            A = state.adjacencyList
            st = state.st
        }

        throw IllegalStateException("No min cut of 3 found")
    }

    val testInput = readInput("Day25_test")
    check(part1(testInput) == 54)

    val input = readInput("Day25")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
}