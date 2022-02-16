package org.hildan.sudoku.solver.techniques

import org.hildan.sudoku.model.*

/**
 * Trivially sets the digit in a cell when this is the only cell of the unit with this candidate.
 */
object HiddenSingles : Technique {

    override fun attemptOn(grid: Grid): TechniqueUse? {
        val actions = grid.units.flatMap { unit ->
            ALL_DIGITS.mapNotNull { digit ->
                unit.cells.singleOrNull { it.isEmpty && digit in it.candidates }?.let { onlyCellWithDigit ->
                    Action.PlaceDigit(digit, onlyCellWithDigit.index)
                }
            }
        }

        return if (actions.isEmpty()) null else HiddenSinglesUse(actions.distinct())
    }
}

data class HiddenSinglesUse(
    override val actions: List<Action>,
): TechniqueUse {
    override val techniqueName: String = "Hidden Singles"
}

object HiddenPairs : HiddenTuples("Hidden Pairs", tupleSize = 2)

object HiddenTriples : HiddenTuples("Hidden Triples", tupleSize = 3)

object HiddenQuads : HiddenTuples("Hidden Quads", tupleSize = 4)

/**
 * Hidden tuples are groups of N candidates that are only present in the same N cells of a unit.
 * There can be other candidates in those N cells, but the no element of the tuple appears in any other cell of the
 * unit.
 * When this happens, only those N candidates can be in those N cells because they will fill them all, and thus other
 * candidates can be removed from those cells.
 */
open class HiddenTuples(
    private val techniqueName: String,
    private val tupleSize: Int,
) : Technique {

    override fun attemptOn(grid: Grid): HiddenTuplesUse? {
        val hiddenTuples = mutableListOf<HiddenTuple>()

        grid.units.forEach { unit ->
            val emptyCells = unit.cells.filterTo(HashSet()) { it.isEmpty }
            val cellsByTuple = emptyCells.groupByPotentialHiddenTuple()

            cellsByTuple.forEach { (tuple, cells) ->
                if (cells.size == tupleSize) {
                    // Exactly N cells with the same naked tuple of N candidates in the unit
                    // Those N candidates must all be in those N cells and can be removed from other cells in the unit
                    val cellIndices = cells.mapTo(HashSet()) { it.index }
                    val removals = tupleCandidatesRemovalActions(cells, tuple)
                    if (removals.isNotEmpty()) {
                        hiddenTuples.add(HiddenTuple(unit.id, tuple, cellIndices, removals))
                    }
                }
            }
        }

        return if (hiddenTuples.isEmpty()) null else HiddenTuplesUse(techniqueName, hiddenTuples)
    }

    private fun Set<Cell>.groupByPotentialHiddenTuple(): Map<Set<Int>, Set<Cell>> {
        val allCandidates = flatMapTo(HashSet()) { it.candidates }
        val potentialTuples = allCandidates.allTuplesOfSize(tupleSize)
        return potentialTuples.associateWith { tuple ->
            // we consider the cells that exclusively contain the tuple in their candidates
            this - filterCellsWithNoCandidatesIn(tuple)
        }
    }

    private fun Set<Cell>.filterCellsWithNoCandidatesIn(tuple: Set<Int>) = filterTo(HashSet()) { cell ->
        cell.candidates.none { it in tuple }
    }

    private fun tupleCandidatesRemovalActions(
        cellsWithHiddenTuple: Set<Cell>,
        tupleCandidates: Set<Digit>,
    ): List<Action.RemoveCandidate> = buildList {
        for (cell in cellsWithHiddenTuple) {
            val candidatesToRemove = cell.candidates - tupleCandidates
            addAll(candidatesToRemove.map { Action.RemoveCandidate(it, cell.index) })
        }
    }
}

data class HiddenTuplesUse(
    override val techniqueName: String,
    val hiddenTuples: List<HiddenTuple>,
): TechniqueUse {
    override val actions: List<Action>
        get() = hiddenTuples.flatMap { it.removals }.distinct()
}

data class HiddenTuple(
    val unit: UnitId,
    val tuple: Set<Digit>,
    val cells: Set<CellIndex>,
    val removals: List<Action.RemoveCandidate>,
)
