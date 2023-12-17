data class Cell(val x: Int, val y: Int) {
    operator fun plus(cell: Cell) = Cell(x + cell.x, y + cell.y)
    operator fun minus(cell: Cell) = Cell(x - cell.x, y - cell.y)
    fun adjacent() = listOf(-1, 1).flatMap { listOf(Cell(x, y + it), Cell(x + it, y)) }
}

typealias Grid = List<String>

fun Grid.inBounds(cell: Cell) = cell.y in indices && cell.x in this[0].indices
operator fun Grid.get(cell: Cell) = this[cell.y][cell.x]

