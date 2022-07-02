package org.hildan.sudoku.solver

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.solver.techniques.Step

fun SolveResult.solvePath(grid: String): SolvePath {
    val initialGrid = Grid(grid).apply { removeImpossibleCandidates() }
    val initial = GridState(initialGrid, step = null)
    val grids = steps.runningFold(initial) { g, s -> GridState(grid = g.grid.applyStep(s), step = s) }
    return SolvePath(grids)
}

fun Grid.applyStep(s: Step): Grid = copy().apply {
    removeImpossibleCandidates()
    performActions(s.actions)
}

class SolvePath(val states: List<GridState>)

data class GridState(
    val grid: Grid,
    val step: Step?,
)
