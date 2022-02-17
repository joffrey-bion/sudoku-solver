package org.hildan.sudoku.solver.techniques

import org.hildan.sudoku.model.*

/**
 * When the candidates for a digit in a box are all confined to a single line in that box, we know the digit for the
 * whole line must land in that box. Therefore, we can eliminate this digit from the candidates of other cells of the
 * line that are not in that box.
 */
object PointingTuples : Technique {

    override fun attemptOn(grid: Grid): List<PointingTupleStep> = ALL_DIGITS.flatMap { digit ->
        grid.findPointingTuples(digit)
    }

    private fun Grid.findPointingTuples(digit: Int): List<PointingTupleStep> = buildList {
        boxes.forEach { box ->
            val candidateCells = box.emptyCellsWithCandidate(digit)
            pointingTupleOrNull(box, digit, candidateCells) { rows[it.row] }?.let { add(it) }
            pointingTupleOrNull(box, digit, candidateCells) { cols[it.col] }?.let { add(it) }
        }
    }

    private inline fun pointingTupleOrNull(
        box: GridUnit,
        digit: Int,
        candidateCells: Set<Cell>,
        getLine: (Cell) -> GridUnit,
    ): PointingTupleStep? {
        val singleLine = candidateCells.mapTo(HashSet()) { getLine(it) }.singleOrNull() ?: return null
        val removals = candidateRemovals(box, singleLine, digit)
        if (removals.isEmpty()) {
            return null
        }
        return PointingTupleStep(box.id, singleLine.id, digit, candidateCells.mapToIndices(), removals)
    }

    private fun candidateRemovals(box: GridUnit, line: GridUnit, digit: Int): List<Action.RemoveCandidate> =
        line.cells
            .filter { it.isEmpty && it.box != box.id.index && digit in it.candidates }
            .map { Action.RemoveCandidate(digit, it.index) }
}

data class PointingTupleStep(
    val box: UnitId,
    val line: UnitId,
    val digit: Digit,
    val cells: Set<CellIndex>,
    override val actions: List<Action.RemoveCandidate>,
): Step {
    override val techniqueName: String = when {
        cells.size == 2 -> "Pointing Pairs"
        cells.size == 3 -> "Pointing Triples"
        else -> error("Cannot have a pointing tuple with more than 3 cells")
    }
}
