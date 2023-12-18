data class Cell(val x: Int, val y: Int) {
    operator fun plus(cell: Cell) = Cell(x + cell.x, y + cell.y)
    operator fun minus(cell: Cell) = Cell(x - cell.x, y - cell.y)
    fun adjacent() = listOf(-1, 1).flatMap { listOf(Cell(x, y + it), Cell(x + it, y)) }
}

typealias Point = Cell

typealias Grid = List<String>

fun Grid.inBounds(cell: Cell) = cell.y in indices && cell.x in this[0].indices
operator fun Grid.get(cell: Cell) = this[cell.y][cell.x]
fun Grid.manhattan(from:Cell, to:Cell) = (to - from).let { (x, y) -> x + y }
enum class Direction(val value: Point) {
    NORTH(Point(0, -1)),
    EAST(Point(1, 0)),
    SOUTH(Point(0, 1)),
    WEST(Point(-1, 0)),
}

