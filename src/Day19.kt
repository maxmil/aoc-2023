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

    fun part2(input: String): Long {
        TODO()
    }

    val testInput = readInputText("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000)

    val input = readInputText("Day19")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}