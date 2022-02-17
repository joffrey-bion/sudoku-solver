package org.hildan.sudoku.solver.techniques

import org.hildan.sudoku.helpers.*
import org.hildan.sudoku.model.*

object BoxLineReduction : Technique {

    override fun attemptOn(grid: Grid): List<BoxLineReductionStep> = ALL_DIGITS.flatMap { digit ->
        grid.lines.mapNotNull { line -> grid.findBoxLineReduction(line, digit) }
    }

    private fun Grid.findBoxLineReduction(line: GridUnit, digit: Int): BoxLineReductionStep? {
        val lineCells = line.emptyCellsWithCandidate(digit).groupBySets { it.box }
        // all cells of the line with this candidate are confined to a single box,
        // we can eliminate this candidate from the rest of that box
        val (boxIndex, cellsToKeep) = lineCells.entries.singleOrNull() ?: return null
        val box = boxes[boxIndex]
        val cellsToClean = box.cells.filter { it.isEmpty && digit in it.candidates && it !in cellsToKeep }
        val removals = cellsToClean.map { Action.RemoveCandidate(digit, it.index) }
        if (removals.isEmpty()) {
            return null
        }
        return BoxLineReductionStep(box.id, line.id, digit, cellsToKeep.mapToIndices(), removals)
    }
}

data class BoxLineReductionStep(
    val box: UnitId,
    val line: UnitId,
    val digit: Digit,
    val cells: Set<CellIndex>,
    val removals: List<Action.RemoveCandidate>,
): Step {
    override val techniqueName: String = "Box/Line Reduction"
    override val actions: List<Action>
        get() = removals
}
