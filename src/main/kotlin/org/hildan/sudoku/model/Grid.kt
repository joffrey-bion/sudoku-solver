package org.hildan.sudoku.model

import org.hildan.sudoku.drawing.format

/**
 * Represents a grid of Sudoku.
 *
 * @param numbers The numbers to put in this `Grid`, listed row by row, from the upper one to the lower one, in left-to-right order within a row.
 * @throws IllegalArgumentException If there is not enough numbers, or too many, or some other characters than numbers.
 */
class Grid(numbers: String) {
    /**
     * The matrix of the tiles of this `Grid`.
     */
    val tiles: Array<Array<Tile>>

    /**
     * The list of the empty tiles of this `Grid`.
     */
    val emptyTiles: MutableList<Tile>

    /**
     * Whether this `Grid` is full of digits.
     */
    val isFull: Boolean
        get() = emptyTiles.isEmpty()

    init {
        require(numbers.length >= SIZE * SIZE) { "too few input digits (blanks must be given by zeros)" }
        require(numbers.length <= SIZE * SIZE) { "too many input digits, only " + SIZE * SIZE + " are needed" }

        emptyTiles = ArrayList()
        tiles = Array(SIZE) { i ->
            Array(SIZE) { j ->
                when (val value = numbers[SIZE * i + j]) {
                    in '1'..'9' -> Tile(this, i, j, value.digitToInt())
                    '.', ' ', '0' -> Tile(this, i, j).also { emptyTiles.add(it) }
                    else -> throw IllegalArgumentException("wrong input, only digits from 0 to $SIZE are accepted")
                }
            }
        }
        for (i in 0 until SIZE) {
            for (j in 0 until SIZE) {
                tiles[i][j].sisters = getSisters(i, j)
            }
        }
    }

    /**
     * Returns the tiles which are either in the same row or column or region as the specified coordinates.
     */
    private fun getSisters(row: Int, col: Int): Set<Tile> {
        val sisters = HashSet<Tile>()
        // add the row
        for (j in 0 until SIZE) {
            if (j == col) {
                continue
            }
            sisters.add(tiles[row][j])
        }
        // add the column
        for (i in 0 until SIZE) {
            if (i == row) {
                continue
            }
            sisters.add(tiles[i][col])
        }
        // add the region
        val baserow = row / BOX_SIZE * BOX_SIZE
        val basecol = col / BOX_SIZE * BOX_SIZE
        for (i in 0 until BOX_SIZE) {
            if (baserow + i == row) {
                continue  // skips the row, already added
            }
            for (j in 0 until BOX_SIZE) {
                if (basecol + j == col) {
                    continue  // skips the column, already added
                }
                sisters.add(tiles[baserow + i][basecol + j])
            }
        }
        return sisters
    }

    /**
     * Iterates on complete [Tile]s and remove the corresponding value in the empty sister `Tile`s.
     *
     * @return `false` if an empty [Tile] ends up with no possible value.
     */
    fun clearImpossibleValues(): Boolean {
        for (row in tiles) {
            for (tile in row) {
                if (tile.isEmpty) {
                    continue  // do not consider empty tiles
                }
                if (!tile.removeValueFromSisters()) {
                    return false
                }
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
