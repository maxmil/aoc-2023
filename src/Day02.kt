import kotlin.math.max
import kotlin.math.min

fun main() {

    data class Pick(val blue:Int, val red:Int, val green:Int)
    data class Game(val id:Int, val picks: List<Pick>)

    fun parseGames(input: List<String>): List<Game> {
        val games = input.map { line ->
            line.split(":").let { s ->
                val id = s[0].split(" ")[1].toInt()
                val picks = s[1].split(";").map { pick ->
                    pick.split(",").fold(Pick(0, 0, 0)) { acc, color ->
                        color.trim().split(" ").let {
                            if (it[1] == "blue") acc.copy(blue = acc.blue + it[0].toInt())
                            else if (it[1] == "green") acc.copy(green = acc.green + it[0].toInt())
                            else acc.copy(red = acc.red + it[0].toInt())
                        }
                    }
                }
                Game(id, picks)
            }
        }
        return games
    }

    fun part1(input: List<String>): Int {
        return parseGames(input).filter { g -> g.picks.all { p -> p.red < 13 && p.green < 14 && p.blue < 15 } }
            .sumOf { it.id }

    }

    fun part2(input: List<String>): Int {
        return parseGames(input).map{ game ->
            game.picks.drop(1).fold(game.picks.first()) { acc, pick ->
                Pick(max(acc.blue, pick.blue), max(acc.red, pick.red), max(acc.green, pick.green))
            }.let { it.red * it.blue * it.green}
        }.sum()
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput).also{ println(it) } == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}