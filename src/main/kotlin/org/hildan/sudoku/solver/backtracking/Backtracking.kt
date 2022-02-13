package org.hildan.sudoku.solver.backtracking

import org.hildan.sudoku.model.*

private const val USE_FORWARD_CHECK = true

/** Heuristic of Most Constrained/Constraining Variables */
private const val USE_MCV_HEURISTICS = true

/** Heuristic of Least Constraining Value */
private const val USE_LCV_HEURISTIC = true

/**
 * Solve this grid and returns the number of visited nodes in the backtracking algorithm.
 */
fun Grid.solveWithBacktracking(): Int {
    // Forward-Checking preparation
    if (USE_FORWARD_CHECK) {
        // find the clue-cells and remove the possible values in the impacted
        // empty cells before starting the search.
        val stillValid = clearImpossibleValues()
        if (!stillValid) {
            error("Incorrect clues in the given grid.")
        }
    }
    return backtracking(this)
}

/**
 * Recursive backtracking search. If the forward checking is enabled, the possible values of the cells must have
 * been already updated due to the constraints of the clues.
 */
private fun backtracking(grid: Grid): Int {
    if (grid.isComplete) {
        return 0
    }
    // Choose an empty cell (unassigned variable)
    val cell = grid.selectEmptyCell()
    grid.emptyCells.remove(cell)

    var nbVisitedNodes = 1
    // Try the possible values for this cell
    for (value in cell.orderedCandidates()) {
        // Check whether the value is still consistent in the current grid.
        // (This test is not necessary when forward-checking is enabled, because invalid candidates are
        // eliminated during the search)
        if (!USE_FORWARD_CHECK && cell.sees(value)) {
            continue
        }

        val success = cell.assignValue(value)
        // If the forward-checking detected failure, skip the recursion with this value, there is no point
        if (success) {
            nbVisitedNodes += backtracking(grid)
            if (grid.isComplete) return nbVisitedNodes
        }
        // Clear the cell (remove the variable from assignment) to try other values
        cell.unassignValue()
    }

    grid.emptyCells.add(cell)
    return nbVisitedNodes
}

/**
 * Choose the next empty cell to fill.
 */
private fun Grid.selectEmptyCell(): Cell {
    // Without heuristics, take the first one that comes
    if (!USE_MCV_HEURISTICS) {
        return emptyCells.first()
    }

    // Most Constrained Variable heuristic (or Minimum Remaining Values)
    // We try here to choose a cell with the fewest remaining candidates
    val cellsWithFewestCandidates = emptyCells.filterFewestCandidates()

    // Most Constraining Variable heuristic (or "degree" heuristic)
    // We choose the cell with the most empty sisters (thus the most impact) to discover dead-ends early
    return cellsWithFewestCandidates.maxByOrNull { it.nbEmptySisters } ?: error("No empty cells")
}

private fun List<Cell>.filterFewestCandidates(): List<Cell> = buildList {
    var minLCV = 9
    for (cell in this@filterFewestCandidates) {
        val size = cell.candidates.size
        if (size == minLCV) {
            add(cell)
        } else if (size < minLCV) {
            clear()
            add(cell)
            minLCV = size
        }
    }
}

/**
 * Returns an ordered list of values to test for this [Cell].
 */
private fun Cell.orderedCandidates(): List<Digit> {
    if (!USE_LCV_HEURISTIC || candidates.size <= 1) {
        // the second part of the test saves some time
        return candidates.toList()
    }
    // Least Constraining Value
    // We want to sort the possible digits for the given cell, choosing first those which impose the fewest constraints
    // on the sisters (so it gives more chance to find a solution and avoid backtracking)
    return candidates.sortedBy { value ->
        // For each possible value of the Cell, we count the number of possibilities which will be ruled out by the
        // forward checking if we assign this value to this cell.
        // That is the number of sisters having this value in their possibilities.
        sisters.count { s -> s.isEmpty && value in s.candidates }
    }
}

/**
 * Assigns the given [value] to this [Cell], and if forward checking is enabled, updates the sisters' possibilities.
 *
 * @return `false` if the forward checking is enabled and one of the sisters has no more possible values,
 * `true` otherwise.
 */
private fun Cell.assignValue(value: Digit): Boolean {
    this.value = value

    // Forward Checking: propagate the constraints right now to foresee the problems
    return if (USE_FORWARD_CHECK) removeValueFromSistersCandidates() else true
}

/**
 * Remove the assigned value to this [Cell], and if forward checking is enabled, update the sisters' possibilities.
 */
private fun Cell.unassignValue() {
    val value = value ?: error("the cell should have a value to unassign")
    this.value = null

    // Forward Checking: restore the possibilities that had been removed
    if (USE_FORWARD_CHECK) {
        restoreValueInSisters(value)
    }
}
