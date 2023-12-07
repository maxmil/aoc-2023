import kotlin.time.measureTimedValue

fun main() {

    val cards = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')

    fun compareHands(a: String, b: String, joker: Char? = null): Int {
        val jokersA = a.count { it == joker }
        val jokersB = b.count { it == joker }
        val groupsA = a.toCharArray().filter { it != joker }.groupBy { it }
        val groupsB = b.toCharArray().filter { it != joker }.groupBy { it }
        (groupsB.size.coerceAtLeast(1) - groupsA.size.coerceAtLeast(1)).let { if (it != 0) return it }
        val largestGroupA = (groupsA.values.maxOfOrNull { it.size } ?: 0) + jokersA
        val largestGroupB = (groupsB.values.maxOfOrNull { it.size } ?: 0) + jokersB
        (largestGroupA - largestGroupB).let { if (it != 0) return it }
        val cardsWithJoker = if (joker == null) cards else cards.toMutableList().apply { remove(joker);addFirst(joker) }
        return a.toCharArray().zip(b.toCharArray()).firstOrNull { (c1, c2) -> c1 != c2 }
            ?.let { (c1, c2) -> cardsWithJoker.indexOf(c1) - cardsWithJoker.indexOf(c2) }
            ?: 0
    }

    fun winnings(input: List<String>, comparator: Comparator<String>) = input.map { it.split(" ") }
        .sortedWith { (a, _), (b, _) -> comparator.compare(a, b) }
        .mapIndexed { i, (_, bid) -> (i + 1) * bid.toInt() }
        .sum()

    fun part1(input: List<String>) = winnings(input) { a, b -> compareHands(a, b) }

    fun part2(input: List<String>) = winnings(input) { a, b -> compareHands(a, b, 'J') }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    measureTimedValue { part1(input) }.also { println("${it.value} in ${it.duration}") }
    measureTimedValue { part2(input) }.also { println("${it.value} in ${it.duration}") }
}