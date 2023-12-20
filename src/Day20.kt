import kotlin.time.measureTimedValue


interface Module {
    val name: String
    val destinations: List<String>
    fun processSignal(source: String, isHigh: Boolean): Boolean?
}

fun main() {

    class FlipFlopModule(
        override val name: String,
        override val destinations: List<String>,
        var isOn: Boolean = false
    ) : Module {
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
    ) : Module {
        override fun processSignal(source: String, isHigh: Boolean): Boolean {
            inputs[source] = isHigh
            return !inputs.values.all { it }
        }
    }

    class BroadcastModule(override val name: String, override val destinations: List<String>) : Module {
        override fun processSignal(source: String, isHigh: Boolean) = false
    }

    data class Pulse(val source: String, val destination: String, val signal: Boolean)

    fun parseModuleType(input: List<String>, startsWith: String, moduleFactory: (String, List<String>) -> Module) =
        input.filter { it.startsWith(startsWith) }.map { line ->
            val (name, destinations) = line.split(" -> ").let { Pair(it[0], it[1].split(", ")) }
            moduleFactory(name.substring(1), destinations)
        }

    fun parseModules(input: List<String>): Map<String, Module> {
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

    fun part1(input: List<String>): Int {
        val modules = parseModules(input)
        var highs = 0
        var lows = 0
        repeat(1000) {
            val q = ArrayDeque<Pulse>()
            q.add(Pulse("button", "broadcaster", false))
            while (q.isNotEmpty()) {
                val next = q.removeFirst()
                if (next.signal) highs++ else lows++
                val destination = modules[next.destination]
                if (destination != null) {
                    val output = destination.processSignal(next.source, next.signal)
                    if (output != null) {
                        val pulses = destination.destinations.map { Pulse(destination.name, it, output) }
                        q.addAll(pulses)
                    }
                }
            }
        }
        return highs * lows
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 11687500)

    val input = readInput("Day20")
    measureTimedValue { part1(input) }.also { check(it.value == 684125385) }
        .also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}
