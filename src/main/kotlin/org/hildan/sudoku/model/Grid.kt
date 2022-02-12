package org.hildan.sudoku.model

import org.hildan.sudoku.drawing.format

/**
 * Represents a grid of Sudoku.
 *
 * @param numbers The numbers to put in this `Grid`, listed row by row, from the upper one to the lower one, in left-to-right order within a row.
 * @throws IllegalArgumentException If there is not enough numbers, or too many, or some other characters than numbers.
 */
class Grid(numbers: String) {

    init {
        require(numbers.length == SIZE * SIZE) {
            "there must be ${SIZE * SIZE} input digits, got ${numbers.length}"
        }
    }

    /**
     * The list of the empty tiles of this `Grid`.
     */
    val emptyTiles: MutableList<Tile> = ArrayList()

    /**
     * Whether this `Grid` is full of digits.
     */
    val isComplete: Boolean
        get() = emptyTiles.isEmpty()

    val tiles: List<Tile> = numbers.mapIndexed { index, c ->
        when (c) {
            in '1'..'9' -> Tile(this, index / SIZE, index % SIZE, c.digitToInt())
            '.', ' ', '0' -> Tile(this, index / SIZE, index % SIZE).also { emptyTiles.add(it) }
            else -> throw IllegalArgumentException("wrong input, only digits from 0 to $SIZE are accepted")
        }
    }

    val rows: List<List<Tile>> = tiles.chunked(SIZE)
    val cols: List<List<Tile>> = List(SIZE) { col -> rows.map { it[col] } }
    val boxes: List<List<Tile>> = List(SIZE) { index ->
        val rowOffset = (index / 3) * BOX_SIZE
        val colOffset = (index % 3) * BOX_SIZE
        buildList {
            for (r in 0 until BOX_SIZE) {
                for (c in 0 until BOX_SIZE) {
                    add(get(rowOffset + r, colOffset + c))
                }
            }
        }
    }

    init {
        tiles.forEach {
            it.sisters = buildSet {
                addAll(rows[it.row])
                addAll(cols[it.col])
                addAll(boxes[boxIndex(it.row, it.col)])
            }
        }
    }

    operator fun get(row: Int, col: Int) = tiles[row * SIZE + col]

    /**
     * Iterates on complete [Tile]s and remove the corresponding value in the empty sister `Tile`s.
     *
     * @return `false` if an empty [Tile] ends up with no possible value.
     */
    fun clearImpossibleValues(): Boolean {
        for (tile in tiles) {
            if (tile.isEmpty) {
                continue  // do not consider empty tiles
            }
            if (!tile.removeValueFromSisters()) {
                return false
            }
        }
        return true
    }

    /**
     * Prints the grid with fancy lines.
     */
    override fun toString(): String = format()

    companion object {
        /**
         * Size of the regions within each grid.
         */
        const val BOX_SIZE = 3

        /**
         * Size of the grids.
         */
        const val SIZE = BOX_SIZE * BOX_SIZE
    }
}

private fun boxIndex(row: Int, col: Int): Int = (row / Grid.BOX_SIZE) * 3 + col / Grid.BOX_SIZE
