package org.hildan.sudoku.model

/**
 * Represents one cell in a Sudoku [Grid].
 */
class Cell(
    val row: Int,
    val col: Int,
) {
    /** The digit set for this cell, or null if this cell is empty */
    var value: Digit? = null

    /** True if this cell's digit was not found. */
    val isEmpty: Boolean
        get() = value == null

    /** The possible digits for this cell. */
    val candidates: MutableSet<Digit> = (1..Grid.SIZE).toMutableSet()

    /** The cells which are either in the same unit (row or column or box) as this cell. */
    lateinit var sisters: Set<Cell>
}

/** The number of empty cells which are sisters of this cell. */
val Cell.nbEmptySisters: Int
    get() = sisters.count { it.isEmpty }

/**
 * Returns whether the given [digit] appears in the same unit as this cell
 */
fun Cell.sees(digit: Digit): Boolean = sisters.any { it.value == digit }

/**
 * Remove this `Cell`'s current value from the list of possibilities of its sisters.
 *
 * @return `true` if success, `false` if one of the sisters has no more possible values.
 */
fun Cell.removeValueFromSistersCandidates(): Boolean {
    for (sister in sisters) {
        if (sister.isEmpty) {
            sister.candidates.remove(value)
            if (sister.candidates.isEmpty()) {
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
fun Cell.restoreValueInSisters(value: Digit) {
    for (sister in sisters) {
        if (sister.isEmpty && !sister.sees(value)) {
            sister.candidates.add(value)
        }
    }
}
