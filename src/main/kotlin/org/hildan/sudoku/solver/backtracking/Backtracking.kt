package org.hildan.sudoku.solver.backtracking

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.model.Tile

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
        // find the clue-tiles and remove the possible values in the impacted
        // empty tiles before starting the search.
        val stillValid = clearImpossibleValues()
        if (!stillValid) {
            error("Incorrect clues in the given grid.")
        }
    }
    return backtracking(this)
}

/**
 * Recursive backtracking search. If the forward checking is enabled, the possible values of the tiles must have
 * been already updated due to the constraints of the clues.
 */
private fun backtracking(grid: Grid): Int {
    if (grid.isComplete) {
        return 0
    }
    // Choose an empty tile (unassigned variable)
    val tile = grid.selectEmptyTile()
    var nbVisitedNodes = 1
    // Try the possible values for this tile
    for (value in tile.orderedCandidates()) {
        // Check whether the value is consistent in the current grid.
        if (!USE_FORWARD_CHECK && !tile.isConsistent(value)) {
            // This test is not necessary when forward-checking is enabled, since
            // FC reduces the set of possible values during the search
            continue
        }

        // Give a value to the tile (assign the variable)
        val success = tile.assignValue(value)
        // If the forward-checking detected failure, skip the recursion with this value, there is no point
        if (success) {
            // Recursive search, with that value given to the tile
            nbVisitedNodes += backtracking(grid)
            // Return the solution, if any were found
            if (grid.isComplete) return nbVisitedNodes
        }
        // Clear the tile (remove the variable from assignment) to try other values
        tile.unassignValue()
    }
    return nbVisitedNodes
}

/**
 * Choose the next empty tile to fill.
 */
private fun Grid.selectEmptyTile(): Tile {
    // Without heuristics, take the first one that comes
    if (!USE_MCV_HEURISTICS) {
        return emptyTiles.first()
    }

    // Most Constrained Variable heuristic (or Minimum Remaining Values)
    // We try here to choose a tile with the fewest remaining candidates
    val tilesWithFewestCandidates = emptyTiles.filterFewestCandidates()

    // Most Constraining Variable heuristic (or "degree" heuristic)
    // We choose the tile with the most empty sisters (thus the most impact) to discover dead-ends early
    return tilesWithFewestCandidates.maxByOrNull { it.nbOfEmptySisters } ?: error("No empty tiles")
}

private fun List<Tile>.filterFewestCandidates(): List<Tile> = buildList {
    var minLCV = 9
    for (tile in this@filterFewestCandidates) {
        val size = tile.possibleValues.size
        if (size == minLCV) {
            add(tile)
        } else if (size < minLCV) {
            clear()
            add(tile)
            minLCV = size
        }
    }
}

/**
 * Returns an ordered list of values to test for this [Tile].
 */
private fun Tile.orderedCandidates(): List<Int> {
    if (!USE_LCV_HEURISTIC || possibleValues.size <= 1) {
        // the second part of the test saves some time
        return possibleValues.toList()
    }
    // Least Constraining Value
    // We want to sort the possible values for the given tile, choosing first those which impose the fewest constraints
    // on the sisters (so it gives more chance to find a solution and avoid backtracking)
    return possibleValues.sortedBy { value ->
        // For each possible value of the Tile, we count the number of possibilities which will be ruled out by the
        // forward checking if we assign this value to this tile.
        // That is the number of sisters having this value in their possibilities.
        sisters.count { s -> s.isEmpty && value in s.possibleValues }
    }
}

/**
 * Assigns the given [value] to this [Tile], and if forward checking is enabled, updates the sisters' possibilities.
 *
 * @return `false` if the forward checking is enabled and one of the sisters has no more possible values,
 * `true` otherwise.
 */
private fun Tile.assignValue(value: Int): Boolean {
    this.value = value

    // Forward Checking: propagate the constraints right now to foresee the problems
    return if (USE_FORWARD_CHECK) removeValueFromSisters() else true
}

/**
 * Remove the assigned value to this [Tile], and if forward checking is enabled, update the sisters' possibilities.
 */
private fun Tile.unassignValue() {
    val value = value
    setEmpty()

    // Forward Checking: restore the possibilities that had been removed
    if (USE_FORWARD_CHECK) {
        restoreValueInSisters(value)
    }
}
