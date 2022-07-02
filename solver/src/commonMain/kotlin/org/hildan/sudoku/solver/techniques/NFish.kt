package org.hildan.sudoku.solver.techniques

import org.hildan.sudoku.model.*

object XWing : NFish("X-Wing", dimension = 2)

object Swordfish : NFish("Swordfish", dimension = 3)

object Jellyfish : NFish("Jellyfish", dimension = 4)

object Squirmbag : NFish("Squirmbag", dimension = 5)

open class NFish(
    private val techniqueName: String,
    private val dimension: Int,
) : Technique {
    /**
     * Possible groups of [dimension] indices in a line.
     */
    private val cellIndicesSets = (0 until Grid.SIZE).toSet().allTuplesOfSize(dimension)

    override fun attemptOn(grid: Grid): List<FishStep> = buildList {
        ALL_DIGITS.forEach { digit ->
            addAll(findFishes(digit, grid.rows, grid.cols))
            addAll(findFishes(digit, grid.cols, grid.rows))
        }
    }

    private fun findFishes(
        digit: Int,
        definingUnits: List<GridUnit>,
        secondaryUnits: List<GridUnit>,
    ): List<FishStep> {
        val fishes = mutableListOf<FishStep>()
        val groups = definingUnits.groupUnitsByIndicesOfOccurrenceOf(digit = digit)
        groups.forEach { (indices, definingSet) ->
            if (definingSet.size == dimension) {
                val secondarySet = secondaryUnits.slice(indices)
                val removals = candidateRemovalActions(
                    digit = digit,
                    definingSet = definingSet,
                    secondarySet = secondarySet,
                )
                if (removals.isNotEmpty()) {
                    fishes.add(
                        FishStep(
                            techniqueName = techniqueName,
                            digit = digit,
                            cells = indices,
                            definingSet = definingSet,
                            secondarySet = secondarySet.toSet(),
                            actions = removals,
                        )
                    )
                }
            }
        }
        return fishes
    }

    private fun candidateRemovalActions(
        digit: Int,
        definingSet: Set<GridUnit>,
        secondarySet: List<GridUnit>,
    ): List<Action.RemoveCandidate> {
        val definingCells = definingSet.flatMapTo(HashSet()) { it.cells }
        return secondarySet.flatMap { secondaryUnit ->
            secondaryUnit.cells.filter { it.isEmpty && digit in it.candidates && it !in definingCells }.map {
                Action.RemoveCandidate(digit, it.index)
            }
        }
    }

    private fun List<GridUnit>.groupUnitsByIndicesOfOccurrenceOf(digit: Digit): Map<Set<Int>, Set<GridUnit>> {
        return cellIndicesSets.associateWith { cellsTuple ->
            // we consider all units who have candidates for the given digit only in the current set of cells
            filterTo(HashSet()) { unit ->
                val digitIndices = unit.indicesWithDigit(digit)
                digitIndices.isNotEmpty() && cellsTuple.containsAll(digitIndices)
            }
        }
    }

    private fun GridUnit.indicesWithDigit(digit: Digit): Set<Int> {
        return (0 until Grid.SIZE).filterTo(HashSet()) { cells[it].isEmpty && digit in cells[it].candidates }
    }
}

/** X-Wing, Swordfish, Jellyfish, Squirmbag */
data class FishStep(
    override val techniqueName: String,
    val digit: Int,
    val cells: Set<CellIndex>,
    val definingSet: Set<GridUnit>,
    val secondarySet: Set<GridUnit>,
    override val actions: List<Action.RemoveCandidate>,
): Step {
    override val description: String
        get() = "Within ${definingSet}, the digit $digit only appears in the same positions $secondarySet. " +
            "We know there must be ${definingSet.size} in those"
}
