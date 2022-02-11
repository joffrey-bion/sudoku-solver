package org.hildan.sudoku.solver

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.model.Tile

private const val USE_FORWARD_CHECK = true

/** Heuristic of Most Constrained/Constraining Variables */
private const val USE_MCV_HEURISTICS = true

/** Heuristic of Least Constraining Value */
private const val USE_LCV_HEURISTIC = true

class Solver {

    /**
     * Solve the given [grid] and returns the number of visited nodes in the backtracking algorithm.
     */
    fun solve(grid: Grid): Int {
        // Forward-Checking preparation
        if (USE_FORWARD_CHECK) {
            // find the clue-tiles and remove the possible values in the impacted
            // empty tiles before starting the search.
            if (!grid.clearImpossibleValues()) {
                error("Incorrect clues in the given grid.")
            }
        }
        // Start recursive backtracking search
        return backtracking(grid)
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
        val tile = selectUnassignedVariable(grid)
        var nbVisitedNodes = 1
        // Try the possible values for this tile
        for (value in getOrderDomainValues(tile)) {
            // Check whether the value is consistent in the current grid.
            if (!USE_FORWARD_CHECK && !tile.isConsistent(value)) {
                // This test is not necessary when forward-checking is enabled, since
                // FC reduces the set of possible values during the search
                continue
            }

            // Give a value to the tile (assign the variable)
            val success = assignValue(tile, value)
            // If the forward-checking detected failure, don't try the value
            if (success) {
                // Recursive search, with that value given to the tile
                nbVisitedNodes += backtracking(grid)
                // Return the solution, if any were found
                if (grid.isComplete) return nbVisitedNodes
            }
            // Clear the tile (remove the variable from assignment) to try other values
            unassignValue(tile)
        }
        return nbVisitedNodes
    }
}

/**
 * Choose the next empty tile to fill.
 */
private fun selectUnassignedVariable(grid: Grid): Tile {
    // Without heuristics, take the first one which comes
    if (!USE_MCV_HEURISTICS) {
        return grid.emptyTiles.first()
    }

    // MOST CONSTRAINED VARIABLE heuristic
    // We try here to choose a tile with the fewest possible values
    var minLCV = 9
    val listLCV = ArrayList<Tile>()
    for (tile in grid.emptyTiles) {
        val size = tile.possibleValues.size
        if (size == minLCV) {
            listLCV.add(tile)
        } else if (size < minLCV) {
            listLCV.removeAll(listLCV)
            listLCV.add(tile)
            minLCV = size
        }
    }

    // MOST CONSTRAINING VARIABLE heuristic
    // We try here to choose a tile with the most empty sisters
    var maxMCV = 0
    val listMCV = ArrayList<Tile>()
    for (tile in listLCV) {
        // compute the number of empty sisters of 'tile'
        val size = tile.nbOfEmptySisters
        if (size == maxMCV) {
            listMCV.add(tile)
        } else if (size > maxMCV) {
            listMCV.removeAll(listMCV)
            listMCV.add(tile)
            maxMCV = size
        }
    }
    return listMCV.first()
}

/**
 * Returns an ordered list of values to test for the given [tile].
 */
private fun getOrderDomainValues(tile: Tile): List<Int> {
    if (!USE_LCV_HEURISTIC || tile.possibleValues.size <= 1) {
        // the second part of the test saves some time
        return ArrayList(tile.possibleValues)
    }
    // Least Constraining Value
    // For each possible value of the Tile, we count the number of possibilities
    // which will be ruled out by the forward checking if we assign this value to
    // this tile. That is the number of sisters having this value in their
    // possibilities.
    val nbImpacted = Array(9) { 0 }
    for (value in tile.possibleValues) {
        for (sister in tile.sisters) {
            if (!sister.isEmpty) continue  // skip the filled sisters
            if (sister.possibleValues.contains(value)) {
                nbImpacted[value - 1]++
            }
        }
    }
    // Now we have to sort the possible values, choosing first those which have
    // the less impact on the sisters, according to the numbers we have just
    // computed.
    val sortedNbImpacted = nbImpacted.sortedArray()
    val sortedValues = ArrayList<Int>()
    for (n in sortedNbImpacted) {
        if (n > 0) {
            // we have to find the value corresponding to that number of impacted
            // sisters, it corresponds to the index (+1) of that number in the
            // first array
            for (i in nbImpacted.indices) {
                if (n == nbImpacted[i]) {
                    sortedValues.add(i + 1)
                    nbImpacted[i] = 0 // to avoid duplicates
                }
            }
        }
    }
    return sortedValues
}

/**
 * Assign the given [value] to the given [tile], and if forward checking is enabled, update the sisters' possibilities.
 *
 * @return `false` if the forward checking is enabled and one of the sisters has no more possible values,
 * `true` otherwise.
 */
private fun assignValue(tile: Tile, value: Int): Boolean {
    tile.value = value

    // FORWARD CHECKING
    // Propagate the constraints right now to foresee the problems
    return if (USE_FORWARD_CHECK) tile.removeValueFromSisters() else true
}

/**
 * Remove the assigned value to the given [tile], and if forward checking is enabled, update the sisters' possibilities.
 */
private fun unassignValue(tile: Tile) {
    val value = tile.value
    tile.setEmpty()

    // FORWARD CHECKING
    // Restore the possibilities that had been removed
    if (USE_FORWARD_CHECK) {
        tile.restoreValueInSisters(value)
    }
}
