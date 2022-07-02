package org.hildan.sudoku.solver.techniques

import org.hildan.sudoku.model.*

/**
 * Trivially sets the digit of a cell when only one candidate remains for that cell.
 */
object NakedSingles : Technique {

    override fun attemptOn(grid: Grid): List<NakedSinglesStep> {
        val actions = grid.emptyCells.asSequence()
            .filter { it.candidates.size == 1 }
            .map { cell -> Action.PlaceDigit(cell.candidates.single(), cell.index) }
            .toList()

        return if (actions.isEmpty()) emptyList() else listOf(NakedSinglesStep(actions))
    }
}

data class NakedSinglesStep(
    override val actions: List<Action.PlaceDigit>,
): Step {
    override val techniqueName: String = "Naked Singles"
    override val description: String = "The cells ${cellRefs(actions.map { it.cellIndex })} only have 1 candidate " +
        "left. We can therefore place the corresponding digits."
}

object NakedPairs : NakedTuples("Naked Pairs", tupleSize = 2)

object NakedTriples : NakedTuples("Naked Triples", tupleSize = 3)

object NakedQuads : NakedTuples("Naked Quads", tupleSize = 4)

/**
 * Naked tuples are groups of N candidates that are the only ones present in N different cells of a unit.
 * When this happens, those N candidates must all be in those N cells and thus cannot be in the rest of the unit, so
 * they can be removed from the candidates of the other cells of the unit.
 */
open class NakedTuples(
    private val techniqueName: String,
    private val tupleSize: Int,
) : Technique {

    override fun attemptOn(grid: Grid): List<NakedTupleStep> {
        val nakedTuples = mutableListOf<NakedTupleStep>()

        grid.units.forEach { unit ->
            val emptyCells = unit.cells.filter { it.isEmpty }
            val cellsByTuple = emptyCells.groupByPotentialNakedTuple()

            cellsByTuple.forEach { (tuple, nakedCells) ->
                if (nakedCells.size == tupleSize) {
                    // Exactly N cells with the same naked tuple of N candidates in the unit
                    // Those N candidates must all be in those N cells and can be removed from other cells in the unit
                    val removals = tupleCandidatesRemovalActions(
                        unitCells = emptyCells,
                        cellsWithNakedTuple = nakedCells,
                        tupleCandidates = tuple,
                    )
                    if (removals.isNotEmpty()) {
                        nakedTuples.add(NakedTupleStep(techniqueName, unit.id, tuple, nakedCells.mapToIndices(), removals))
                    }
                }
            }
        }
        return nakedTuples
    }

    private fun List<Cell>.groupByPotentialNakedTuple(): Map<Set<Int>, Set<Cell>> {
        val allCandidates = flatMapTo(HashSet()) { it.candidates }
        val potentialTuples = allCandidates.allTuplesOfSize(tupleSize)
        return potentialTuples.associateWith { tuple ->
            // we consider all cells whose candidates are a subset of the tuple (or all of it)
            filterTo(HashSet()) { cell -> tuple.containsAll(cell.candidates) }
        }
    }

    private fun tupleCandidatesRemovalActions(
        unitCells: List<Cell>,
        cellsWithNakedTuple: Set<Cell>,
        tupleCandidates: Set<Digit>,
    ): List<Action.RemoveCandidate> = buildList {
        for (cell in unitCells) {
            if (cell !in cellsWithNakedTuple) {
                val candidatesToRemove = cell.candidates intersect tupleCandidates
                addAll(candidatesToRemove.map { Action.RemoveCandidate(it, cell.index) })
            }
        }
    }
}

data class NakedTupleStep(
    override val techniqueName: String,
    val unit: UnitId,
    val tuple: Set<Digit>,
    val cells: Set<CellIndex>,
    override val actions: List<Action.RemoveCandidate>,
): Step {
    override val description: String = "The ${tuple.size} digits $tuple are the only ones to appear in exactly " +
        "${tuple.size} cells (${cellRefs(cells)}) in $unit. All of those digits must therefore be in those cells, and" +
        " cannot be in any other cell of $unit."
}
