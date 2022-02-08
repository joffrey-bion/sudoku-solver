package org.hildan.sudoku.model

import org.hildan.sudoku.drawing.BoxChars
import org.hildan.sudoku.drawing.Drawing.repeat
import java.lang.IllegalArgumentException

/**
 * Represents a grid of Sudoku.
 *
 * @param numbers The numbers to put in this `Grid`, listed row by row, from the upper one to the lower one, in left-to-right order within a row.
 * @throws IllegalArgumentException If there is not enough numbers, or too many, or some other characters than numbers.
 */
class Grid(numbers: Array<String>) {
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
        require(numbers.size >= SIZE * SIZE) { "too few input digits (blanks must be given by zeros)" }
        require(numbers.size <= SIZE * SIZE) { "too many input digits, only " + SIZE * SIZE + " are needed" }

        emptyTiles = ArrayList()
        tiles = Array(SIZE) { i ->
            Array(SIZE) { j ->
                val value = numbers[SIZE * i + j].toIntOrNull()
                    ?: throw IllegalArgumentException("wrong input, only digits are accepted")
                when (value) {
                    0 -> Tile(this, i, j).also { emptyTiles.add(it) }
                    in 1..SIZE -> Tile(this, i, j, value)
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
        val baserow = row / RSIZE * RSIZE
        val basecol = col / RSIZE * RSIZE
        for (i in 0 until RSIZE) {
            if (baserow + i == row) {
                continue  // skips the row, already added
            }
            for (j in 0 until RSIZE) {
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
    override fun toString(): String {
        var res = repeat(H, 3, 5, DTEE, ULC, URC) + LF
        for (i in 0 until SIZE) {
            res += V
            for (j in 0 until SIZE) {
                res = res + tiles[i][j].toString()
                if ((j + 1) % RSIZE == 0) {
                    res += V
                } else {
                    res += " "
                }
            }
            res += LF
            if (i % RSIZE == RSIZE - 1 && i != SIZE - 1) {
                res += RTEE + repeat(H, 3, 5, CROSS) + LTEE + LF
            }
        }
        res += DLC + repeat(H, 3, 5, UTEE) + DRC
        return res
    }

    /**
     * Prints the possible values for each tile of the grid.
     */
    fun printState() {
        for (i in 0 until SIZE) {
            for (j in 0 until SIZE) {
                val tile = tiles[i][j]
                print("(" + i + "," + j + ") value = " + tile!!.value + " - possible: ")
                for (k in tile.possibleValues) print("$k ")
                println()
            }
        }
    }

    companion object {
        /**
         * Size of the regions within each grid.
         */
        private const val RSIZE = 3

        /**
         * Size of the grids.
         */
        const val SIZE = RSIZE * RSIZE

        private const val LF = "\n"
        private val H = BoxChars.BOX_DRAWINGS_LIGHT_HORIZONTAL.toString()
        private val V = BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL.toString()
        private val ULC = BoxChars.BOX_DRAWINGS_LIGHT_DOWN_AND_RIGHT.toString()
        private val DLC = BoxChars.BOX_DRAWINGS_LIGHT_UP_AND_RIGHT.toString()
        private val URC = BoxChars.BOX_DRAWINGS_LIGHT_DOWN_AND_LEFT.toString()
        private val DRC = BoxChars.BOX_DRAWINGS_LIGHT_UP_AND_LEFT.toString()
        private val DTEE = BoxChars.BOX_DRAWINGS_LIGHT_DOWN_AND_HORIZONTAL.toString()
        private val UTEE = BoxChars.BOX_DRAWINGS_LIGHT_UP_AND_HORIZONTAL.toString()
        private val RTEE = BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL_AND_RIGHT.toString()
        private val LTEE = BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL_AND_LEFT.toString()
        private val CROSS = BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL_AND_HORIZONTAL.toString()
    }
}
