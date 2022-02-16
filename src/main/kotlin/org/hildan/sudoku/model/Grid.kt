package org.hildan.sudoku.model

import org.hildan.sudoku.drawing.format

/**
 * Represents a grid of Sudoku.
 *
 * @param digits The digits to put in this `Grid`, listed row by row, from top to bottom, from left to right within a row.
 */
class Grid(digits: String) {

    init {
        require(digits.length == NB_CELLS) { "there must be $NB_CELLS input digits, got ${digits.length}" }
    }

    /**
     * The list of the empty cells of this `Grid`.
     */
    val emptyCells: MutableCollection<Cell> = HashSet()

    /**
     * Whether this `Grid` is full of digits.
     */
    val isComplete: Boolean
        get() = emptyCells.isEmpty()

    val cells: List<Cell> = digits.mapIndexed { index, c ->
        Cell(row = index / SIZE, col = index % SIZE).also { cell ->
            when (c) {
                in '1'..'9' -> cell.value = c.digitToInt()
                '.', ' ', '0' -> emptyCells.add(cell)
                else -> throw IllegalArgumentException("wrong input at index $index, only digits 0-$SIZE, '.' or ' ' are accepted")
            }
        }
    }

    val rows: List<GridUnit> = List(SIZE) { row ->
        GridUnit(UnitId(UnitType.ROW, row), cells = List(SIZE) { col -> get(row, col) })
    }

    val cols: List<GridUnit> = List(SIZE) { col ->
        GridUnit(UnitId(UnitType.COLUMN, col), cells = List(SIZE) { row -> get(row, col) })
    }

    val boxes: List<GridUnit> = List(SIZE) { index ->
        val rowOffset = (index / BOX_GRID_SIZE) * BOX_SIDE_SIZE
        val colOffset = (index % BOX_GRID_SIZE) * BOX_SIDE_SIZE
        GridUnit(
            id = UnitId(UnitType.BOX, index),
            cells = buildList {
                for (r in 0 until BOX_SIDE_SIZE) {
                    for (c in 0 until BOX_SIDE_SIZE) {
                        add(get(rowOffset + r, colOffset + c))
                    }
                }
            },
        )
    }

    val units = rows + cols + boxes

    init {
        cells.forEach { cell ->
            cell.sisters = buildSet {
                addAll(rows[cell.row].cells)
                addAll(cols[cell.col].cells)
                addAll(boxes[cell.box].cells)
            } - cell
        }
    }

    operator fun get(row: Int, col: Int) = cells[row * SIZE + col]

    /**
     * Iterates on complete [Cell]s and remove the corresponding value in the empty sister `Cell`s.
     *
     * @return `false` if an empty [Cell] ends up with no possible value.
     */
    fun clearImpossibleValues(): Boolean {
        for (cell in cells) {
            if (cell.isEmpty) {
                continue  // do not consider empty cells
            }
            if (!cell.removeValueFromSistersCandidates()) {
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
         * Size of the boxes within each grid.
         */
        const val BOX_SIDE_SIZE = 3
        /**
         * Number of boxes horizontally (or vertically) in the grid of boxes.
         */
        const val BOX_GRID_SIZE = 3

        /**
         * The number of units of each type (row, column, box).
         * Also The number of cells in a unit (row, column, box).
         * Also the number of different digits in each unit of the grid.
         */
        const val SIZE = BOX_SIDE_SIZE * BOX_GRID_SIZE

        /**
         * The total number of cells in a grid.
         */
        const val NB_CELLS = SIZE * SIZE
    }
}
