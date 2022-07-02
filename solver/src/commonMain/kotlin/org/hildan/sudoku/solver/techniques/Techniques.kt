package org.hildan.sudoku.solver.techniques

import org.hildan.sudoku.model.Digit
import org.hildan.sudoku.model.Grid

typealias CellIndex = Int // SIZE * row + col

fun cellRef(index: CellIndex) = "r${index / Grid.SIZE}c${index % Grid.SIZE}"
fun cellRefs(indices: Iterable<CellIndex>) = indices.map { cellRef(it) }.toString()

interface Technique {

    /**
     * Attempts to apply this technique on the given [grid] and returns the effect, or null if the technique could
     * not be applied.
     */
    fun attemptOn(grid: Grid): List<Step>
}

sealed interface Step {
    val techniqueName: String
    val description: String
    val actions: List<Action>
}

sealed class Action(open val cellIndex: CellIndex) {
    data class RemoveCandidate(val candidate: Digit, override val cellIndex: CellIndex) : Action(cellIndex)
    data class PlaceDigit(val digit: Digit, override val cellIndex: CellIndex) : Action(cellIndex)
}
