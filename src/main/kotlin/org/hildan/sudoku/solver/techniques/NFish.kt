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

    override fun attemptOn(grid: Grid): NFishUse? {
        val fishes = mutableListOf<Fish>()

        ALL_DIGITS.forEach { digit ->
            fishes.addAll(findFishes(digit, grid.rows, grid.cols))
            fishes.addAll(findFishes(digit, grid.cols, grid.rows))
        }

        if (fishes.isEmpty()) {
            return null
        }
        return NFishUse(techniqueName, fishes.flatMap { it.removals }.distinct(), fishes)
    }

    private fun findFishes(
        digit: Int,
        definingUnits: List<GridUnit>,
        secondaryUnits: List<GridUnit>
    ): List<Fish> {
        val fishes = mutableListOf<Fish>()
        val groups = definingUnits.groupUnitsByIndicesOfOccurrenceOf(digit = digit)
        groups.forEach { (indices, definingSet) ->
            if (definingSet.size == dimension) {
                val removals = candidateRemovalActions(
                    digit = digit,
                    definingSet = definingSet,
                    secondarySet = secondaryUnits.slice(indices),
                )
                if (removals.isNotEmpty()) {
                    fishes.add(Fish(digit = digit, cells = indices, removals = removals))
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
        val cellIndicesSets = (0 until Grid.SIZE).toSet().allTuplesOfSize(dimension)
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

data class NFishUse(
    override val techniqueName: String,
    override val actions: List<Action>,
    val patterns: List<Fish>,
): TechniqueUse

/** X-Wing, Swordfish, Jellyfish, Squirmbag */
data class Fish(
    val digit: Int,
    val cells: Set<CellIndex>,
    val removals: List<Action.RemoveCandidate>,
)
