#!/usr/bin/env kotlin

import java.io.File

if(args.size != 1) {
    throw UnsupportedOperationException("Usage create-day.main.kts <DAY>")
}

val day = "%02d".format(args[0].toInt())

val content = """
    import kotlin.time.measureTimedValue
    
    fun main() {
        fun part1(input: List<String>): Int {
            TODO()
        }

        fun part2(input: List<String>): Int {
            TODO()
        }

        val testInput = readInput("Day%%DAY%%_test")
        check(part1(testInput) == 1)
        // check(part2(testInput) == 1)

        val input = readInput("Day%%DAY%%")
        measureTimedValue { part1(input) }.also { println("${'$'}{it.value} in ${'$'}{it.duration}")}
        // measureTimedValue { part2(input) }.also { println("${'$'}{it.value} in ${'$'}{it.duration}")}
    }
""".trimIndent().replace("%%DAY%%", day)

File("src/Day${day}.kt").writeText(content)
File("src/Day${day}.txt").createNewFile()
File("src/Day${day}_test.txt").createNewFile()
