import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTimedValue

fun main() {

    data class Rule(val source: Char, val comparator: Char, val target: Int, val nextWorkflow: String)
    data class Workflow(val id: String, val rules: List<Rule>, val nextWorkflow: String)
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int)

    fun parseWorkflows(inputs: String) = inputs.split("\n").associate { line ->
        val rules = line.substringAfter("{").substringBefore("}")
            .split(",").dropLast(1)
            .map {
                Rule(
                    it[0], it[1],
                    it.substring(2).substringBefore(":").toInt(),
                    it.substringAfter(':')
                )
            }
        val id = line.substringBefore('{')
        val workflow = Workflow(id, rules, line.substringAfterLast(',').substringBefore('}'))
        Pair(id, workflow)
    }

    fun parseParts(inputs: String) = inputs.split("\n").map { line ->
        line.split("[=,}]".toRegex()).let { Part(it[1].toInt(), it[3].toInt(), it[5].toInt(), it[7].toInt()) }
    }

    fun executeRule(part: Part, rule: Rule): String? {
        val value = when (rule.source) {
            'x' -> part.x
            'm' -> part.m
            'a' -> part.a
            else -> part.s
        }
        val result = when (rule.comparator) {
            '>' -> value > rule.target
            else -> value < rule.target
        }
        return if (result) rule.nextWorkflow else null
    }

    fun executeWorkflow(workflow: Workflow, part: Part): String {
        for (rule in workflow.rules) executeRule(part, rule)?.let { return it }
        return workflow.nextWorkflow
    }

    fun executeWorkflows(workflows: Map<String, Workflow>, part: Part): String {
        var wf = executeWorkflow(workflows["in"]!!, part)
        while (wf != "A" && wf != "R") {
            wf = executeWorkflow(workflows[wf]!!, part)
        }
        return wf
    }

    fun part1(input: String): Int {
        val (workflows, parts) = input.split("\n\n").let { Pair(parseWorkflows(it[0]), parseParts(it[1])) }
        return parts.filter { executeWorkflows(workflows, it) == "A" }.sumOf { it.x + it.m + it.a + it.s }
    }

    fun Rule.negate():Rule {
        val comparator = if (comparator == '>') '<' else '>'
        val target = if (this.comparator == '>') target + 1 else target - 1
        return Rule(source, comparator, target, nextWorkflow)
    }

    fun explodeWorkflow(workflows: Map<String, Workflow>, id: String): List<Pair<List<Rule>, String>> {
        if (id == "A" || id == "R") return listOf(Pair(listOf(), id))
        val workflow = workflows[id]!!
        val negatedRules = workflow.rules.map { it.negate() }
        return (workflow.rules).flatMapIndexed { i, rule ->
            val negated = negatedRules.subList(0, i)
            explodeWorkflow(workflows, rule.nextWorkflow).map { Pair(negated + rule + it.first, it.second) }
        } + explodeWorkflow(workflows, workflow.nextWorkflow).map { Pair(negatedRules + it.first, it.second)}
    }

    fun boundsFor(rules: List<Rule>, category: Char) =
        rules.fold(Pair(1, 4000)) { (minBound, maxBound), rule ->
            if (rule.source != category) Pair(minBound, maxBound)
            else when (rule.comparator) {
                '>' -> Pair(max(minBound, rule.target + 1), maxBound)
                else -> Pair(minBound, min(maxBound, rule.target - 1))
            }
        }

    fun rangeFor(rules:List<Rule>, category: Char): Long {
        val bounds = boundsFor(rules, category)
        return (bounds.second - bounds.first + 1).toLong()
    }

    fun part2(input: String): Long {
        val workflows = parseWorkflows(input.split("\n\n")[0])
        val exploded = explodeWorkflow(workflows, "in")

//        println(exploded.size)
//        exploded.forEach { println(it) }

         val result = exploded.filter { (_, result) -> result == "A" }
             .map { println(it);it }
//            .map { (rules, _) -> listOf(boundsFor(rules, 'x'), boundsFor(rules, 'm'), boundsFor(rules, 'a'), boundsFor(rules, 's'))  }
//             .forEach { println(it) }
            .sumOf { (rules, _) -> rangeFor(rules, 'x') * rangeFor(rules, 'm') * rangeFor(rules, 'a') * rangeFor(rules, 's') }
        return result
//        return 0L
    }

    val testInput = readInputText("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput).also { println(it) } == 167409079868000)

    val input = readInputText("Day19")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}

//167409079868000
//167010937327821
//166847596592385
//166847596592385
//167245503449662