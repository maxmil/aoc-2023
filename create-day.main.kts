#!/usr/bin/env kotlin

import java.io.File

if(args.size != 1) {
    throw UnsupportedOperationException("Usage create-day.main.kts <DAY>")
}

val day = "%02d".format(args[0].toInt())

val content = """
    fun main() {
        fun part1(input: List<String>): Int {
            TODO()
        }

        fun part2(input: List<String>): Int {
            TODO()
        }

        val testInput = readInput("Day%%DAY%%")
        check(part1(testInput) == 1)

        val input = readInput("Day%%DAY%%")
        part1(input).println()
        part2(input).println()
    }
""".trimIndent().replace("%%DAY%%", day)

File("src/Day${day}.kt").writeText(content)
File("src/Day${day}.txt").createNewFile()
File("src/Day${day}_test.txt").createNewFile()
