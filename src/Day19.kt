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
        while (wf != "A" && wf != "R") wf = executeWorkflow(workflows[wf]!!, part)
        return wf
    }

    fun part1(input: String): Int {
        val (workflows, parts) = input.split("\n\n").let { Pair(parseWorkflows(it[0]), parseParts(it[1])) }
        return parts.filter { executeWorkflows(workflows, it) == "A" }.sumOf { it.x + it.m + it.a + it.s }
    }

    fun Rule.negate(): Rule {
        val (comparator, target) = if (comparator == '>') Pair('<', target + 1) else Pair('>', target - 1)
        return Rule(source, comparator, target, nextWorkflow)
    }

    fun explodeRules(workflows: Map<String, Workflow>, id: String): List<Pair<List<Rule>, String>> {
        if (id == "A" || id == "R") return listOf(Pair(listOf(), id))
        val workflow = workflows[id]!!
        val negatedRules = workflow.rules.map { it.negate() }
        return (workflow.rules).flatMapIndexed { i, rule ->
            explodeRules(workflows, rule.nextWorkflow).map {
                Pair(negatedRules.subList(0, i) + rule + it.first, it.second)
            }
        } + explodeRules(workflows, workflow.nextWorkflow).map { Pair(negatedRules + it.first, it.second) }
    }

    fun List<Rule>.boundsFor(category: Char) =
        fold(Pair(1, 4000)) { (minBound, maxBound), rule ->
            if (rule.source != category) Pair(minBound, maxBound)
            else when (rule.comparator) {
                '>' -> Pair(max(minBound, rule.target + 1), maxBound)
                else -> Pair(minBound, min(maxBound, rule.target - 1))
            }
        }

    fun List<Rule>.rangeFor(category: Char): Long {
        val bounds = boundsFor(category)
        return (bounds.second - bounds.first + 1).toLong()
    }

    fun part2(input: String): Long {
        val workflows = parseWorkflows(input.split("\n\n")[0])
        return explodeRules(workflows, "in")
            .filter { (_, result) -> result == "A" }
            .sumOf { (rules, _) ->
                listOf('x', 'm', 'a', 's').fold(1L) { acc, category -> acc * rules.rangeFor(category) }
            }
    }

    val testInput = readInputText("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000)

    val input = readInputText("Day19")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}