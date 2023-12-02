import kotlin.math.max

fun main() {

    data class Pick(val blue:Int, val red:Int, val green:Int) {
        fun max(other: Pick) = Pick(max(other.blue, blue), max(other.red, red), max(other.green, green))
    }
    data class Game(val id:Int, val picks: List<Pick>)

    fun parseGames(input: List<String>): List<Game> = input.map { line ->
        line.split(":").let { (game, colors) ->
            val id = game.split(" ")[1].toInt()
            val picks = colors.split(";").map { pick ->
                pick.split(",").fold(Pick(0, 0, 0)) { acc, color ->
                    color.trim().split(" ").let { (count, color) ->
                        when (color) {
                            "blue" -> acc.copy(blue = acc.blue + count.toInt())
                            "green" -> acc.copy(green = acc.green + count.toInt())
                            else -> acc.copy(red = acc.red + count.toInt())
                        }
                    }
                }
            }
            Game(id, picks)
        }
    }

    fun part1(input: List<String>) = parseGames(input)
        .filter { it.picks.all { p -> p.red < 13 && p.green < 14 && p.blue < 15 } }
        .sumOf { it.id }

    fun part2(input: List<String>) = parseGames(input).sumOf { game ->
        val max = game.picks.drop(1).fold(game.picks.first()) { acc, pick -> acc.max(pick)}
        max.red * max.blue * max.green
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}