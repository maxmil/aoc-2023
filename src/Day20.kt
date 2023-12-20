import kotlin.time.measureTimedValue


interface PropagationModule {
    val name: String
    val destinations: List<String>
    fun processSignal(source: String, isHigh: Boolean): Boolean?
}

fun main() {

    class FlipFlopModule(
        override val name: String,
        override val destinations: List<String>,
        var isOn: Boolean = false
    ) : PropagationModule {
        override fun processSignal(source: String, isHigh: Boolean): Boolean? {
            return if (!isHigh) {
                isOn = !isOn
                isOn
            } else null
        }
    }

    class ConjunctionModule(
        override val name: String,
        override val destinations: List<String>,
        val inputs: MutableMap<String, Boolean>
    ) : PropagationModule {
        override fun processSignal(source: String, isHigh: Boolean): Boolean {
            inputs[source] = isHigh
            val emit = !inputs.values.all { it }
            return emit
        }
    }

    class BroadcastModule(override val name: String, override val destinations: List<String>) : PropagationModule {
        override fun processSignal(source: String, isHigh: Boolean) = false
    }

    data class Pulse(val source: String, val destination: String, val signal: Boolean)

    fun parseModuleType(
        input: List<String>,
        startsWith: String,
        moduleFactory: (String, List<String>) -> PropagationModule
    ) =
        input.filter { it.startsWith(startsWith) }.map { line ->
            val (name, destinations) = line.split(" -> ").let { Pair(it[0], it[1].split(", ")) }
            moduleFactory(name.substring(1), destinations)
        }

    fun parseModules(input: List<String>): Map<String, PropagationModule> {
        val flipFlopModules = parseModuleType(input, "%") { n, d -> FlipFlopModule(n, d) }
        val conjunctionModules = parseModuleType(input, "&") { name, destinations ->
            val inputs = flipFlopModules.filter { it.destinations.contains(name) }
                .map { it.name }
                .associateWith { false }
                .toMutableMap()
            ConjunctionModule(name, destinations, inputs)
        }
        val broadcastModules = parseModuleType(input, "broadcaster") { _, d -> BroadcastModule("broadcaster", d) }

        return (broadcastModules + conjunctionModules + flipFlopModules).associateBy { it.name }
    }

    fun Map<String, PropagationModule>.pushButton(callback: (pulse: Pulse) -> Unit = {}) {
        val q = ArrayDeque<Pulse>()
        q.add(Pulse("button", "broadcaster", false))
        while (q.isNotEmpty()) {
            val next = q.removeFirst()
            callback(next)
            val destination = this[next.destination]
            if (destination != null) {
                val output = destination.processSignal(next.source, next.signal)
                if (output != null) {
                    val pulses = destination.destinations.map { Pulse(destination.name, it, output) }
                    q.addAll(pulses)
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val modules = parseModules(input)
        var highs = 0
        var lows = 0
        repeat(1000) { modules.pushButton { next -> if (next.signal) highs++ else lows++ } }
        return highs * lows
    }

    fun gcd(a: Long, b: Long): Long = if (a == 0L) b else gcd(b % a, a)
    fun lcm(a: Long, b: Long): Long = (a * b) / gcd(a, b)
    fun lcmOf(longs: List<Long>) = longs.reduce { acc, curr -> lcm(acc, curr) }


    /*
    Back tracking from rx
    
    There are a series of Conjunction nodes that control signals sent to rx
    
    &gr ->  &js
    &st ->  &zb \
                \\
                  &hb -> rx
                //
    &bn ->  &bs /
    &lg ->  &rr
    
    1. Look for when hb sends low signal
    2. This is when its inputs (js, zb, bs, rr) all send a high signal
    3. This is when their inputs (gr, st, bn, lg) all send a low signal
    
    Assume cyclic and find the cycle on which each node (gr, st, bn, lg) sends a low signal
    Find LCM gr, st, bn, lg
    */
    fun part2(input: List<String>): Long {
        val modules = parseModules(input)
        val cycles = listOf("gr", "st", "bn", "lg").associateWith { mutableListOf<Int>() }
        var cnt = 0
        while (cycles.values.any { it.size < 2 }) {
            modules.pushButton { pulse ->
                val counts = cycles[pulse.source]
                if (counts != null && counts.size < 2 && !pulse.signal && !counts.contains(cnt)) {
                    counts.add(cnt)
                }
            }
            cnt++
        }
        val cycleSizes = cycles.values.map { it[1] - it[0] }.map { it.toLong() }
        return lcmOf(cycleSizes)
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 11687500)

    val input = readInput("Day20")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}



