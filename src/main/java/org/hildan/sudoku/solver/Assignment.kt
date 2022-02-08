package org.hildan.sudoku.solver

import org.hildan.sudoku.model.Grid

class Assignment(
    val grid: Grid,
) {
    var nbVisitedNodes = 0
    val isSolution: Boolean
        get() = grid.isFull

    override fun toString(): String = if (isSolution) grid.toString() else "No solution found."
}
