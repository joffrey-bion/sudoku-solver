package org.hildan.sudoku.model

/**
 * Represents one tile in a Sudoku [Grid].
 */
class Tile(
    private val grid: Grid,
    val row: Int,
    val col: Int
) {
    /**
     * The possible digits for this `Tile`. This set has to be manually updated.
     * This is to fully separate the solver's logic from the model's logic.
     */
    // used by forward checking
    val possibleValues: Set<Int>
        get() = _possibleValues

    private val _possibleValues = (1..Grid.SIZE).toMutableSet()

    /**
     * The current value of this `Tile`. 0 represents an empty `Tile`.
     */
    private var currentValue = 0

    /**
     * Whether this `Tile` is empty.
     */
    val isEmpty: Boolean
        get() = currentValue == 0

    /**
     * Returns a space if this tile is empty, the digit of its value otherwise.
     */
    val valueOrSpace
        get() = if (isEmpty) " " else currentValue.toString()

    /**
     * The current value of this `Tile`.
     */
    var value: Int
        get() {
            check(currentValue != 0) { "Cannot get the value from an empty tile." }
            return currentValue
        }
        set(value) {
            require(value in 1..Grid.SIZE) { "The value must be between 1 and ${Grid.SIZE}" }
            currentValue = value
            grid.emptyTiles.remove(this)
        }

    /**
     * The tiles which are either in the same row or column or region as this tile.
     */
    lateinit var sisters: Set<Tile>

    /**
     * The number of empty tiles which are sisters of this tile.
     */
    val nbOfEmptySisters: Int
        get() = sisters.count { it.isEmpty }

    /**
     * Creates a new `Tile` at the specified position with the specified value.
     *
     * @param grid The parent [Grid] for this `Tile`.
     * @param row The row of this `Tile` in the grid.
     * @param col The column of this `Tile` in the grid.
     * @param value The initial value for this `Tile`.
     */
    constructor(grid: Grid, row: Int, col: Int, value: Int) : this(grid, row, col) {
        this.value = value
        _possibleValues.clear()
    }

    /**
     * Sets this tile as empty.
     */
    fun setEmpty() {
        currentValue = 0
        grid.emptyTiles.add(this)
    }

    /**
     * Returns whether [value] is acceptable for this tile, in the current grid.
     */
    fun isConsistent(value: Int): Boolean = sisters.none { it.currentValue == value }

    /**
     * Remove this `Tile`'s current value from the list of possibilities of its sisters.
     *
     * @return `true` if success, `false` if one of the sisters has no more possible values.
     */
    fun removeValueFromSisters(): Boolean {
        for (sister in sisters) {
            if (sister.isEmpty) {
                sister._possibleValues.remove(currentValue)
                if (sister.possibleValues.isEmpty()) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Put back the [value] in the list of possibilities of the sisters (only if the value is
     * consistent with the current grid).
     */
    fun restoreValueInSisters(value: Int) {
        for (sister in sisters) {
            if (sister.isEmpty && sister.isConsistent(value)) {
                sister._possibleValues.add(value)
            }
        }
    }
}
