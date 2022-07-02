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
    //val highlights: List<Highlight>
}

sealed class Action(open val cellIndex: CellIndex) {
    data class RemoveCandidate(val candidate: Digit, override val cellIndex: CellIndex) : Action(cellIndex)
    data class PlaceDigit(val digit: Digit, override val cellIndex: CellIndex) : Action(cellIndex)
}

sealed class Highlight(
    open val type: HighlightType,
    open val cellIndex: CellIndex,
) {
    data class Cell(
        override val type: HighlightType,
        override val cellIndex: CellIndex,
    ): Highlight(type, cellIndex)

    data class Candidate(
        override val type: HighlightType,
        override val cellIndex: CellIndex,
        val digit: Digit,
    ): Highlight(type, cellIndex)
}

// DIGIT_HIGHLIGHT naked single cells

// DIGIT_HIGHLIGHT hidden single cells
// NEGATIVE_MATCH other cells of the unit (without the digit)

// POSITIVE_MATCH cells with naked tuple
// AFFECTED_CELL rest of the unit (where we remove tuple candidates)

// POSITIVE_MATCH cells with hidden tuple (where we remove other candidates)

// POSITIVE_MATCH cells with Fish digits
// NEGATIVE_MATCH cells without fish digits in defining set
// AFFECTED_CELL affected cells in secondary set (where we remove fish digit)

enum class HighlightType {
    /**
     * Cell in which a new digit has been found.
     */
    DIGIT_HIGHLIGHT,
    /**
     * Cell which contains interesting candidates.
     */
    POSITIVE_MATCH,
    /**
     * Cell which don't contain interesting candidates.
     */
    NEGATIVE_MATCH,
    /**
     * Cell in which we (would) remove candidates as a result of the technique.
     */
    AFFECTED_CELL,
    /**
     * Cell in which we have both highlighted candidates and removed ones.
     */
    POSITIVE_AND_AFFECTED,
}
