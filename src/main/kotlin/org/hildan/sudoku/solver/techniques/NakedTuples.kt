package org.hildan.sudoku.solver.techniques

import org.hildan.sudoku.model.*

object NakedSingles : Technique {

    override fun attemptOn(grid: Grid): NakedSinglesUse? {
        val actions = grid.emptyCells.asSequence()
            .filter { it.candidates.size == 1 }
            .map { cell -> Action.PlaceDigit(cell.candidates.single(), cell.index) }
            .distinct()
            .toList()

        return if (actions.isEmpty()) null else NakedSinglesUse(actions)
    }
}

data class NakedSinglesUse(
    override val actions: List<Action>,
): TechniqueUse {
    override val techniqueName: String = "Naked Singles"
}

object NakedPairs : NakedTuples("Naked Pairs", tupleSize = 2)

object NakedTriples : NakedTuples("Naked Triples", tupleSize = 3)

object NakedQuads : NakedTuples("Naked Quads", tupleSize = 4)

open class NakedTuples(
    private val techniqueName: String,
    private val tupleSize: Int,
) : Technique {

    override fun attemptOn(grid: Grid): NakedTuplesUse? {
        val nakedTuples = mutableListOf<NakedTuple>()

        grid.units.forEach { unit ->
            val emptyCells = unit.cells.filter { it.isEmpty }
            val cellsByTuple = emptyCells.groupByPotentialNakedTuple()

            cellsByTuple.forEach { (tuple, cells) ->
                if (cells.size == tupleSize) {
                    // Exactly N cells with the same naked tuple of N candidates in the unit
                    // Those N candidates must all be in those N cells and can be removed from other cells in the unit
                    val cellIndices = cells.mapTo(HashSet()) { it.index }
                    val removals = tupleCandidatesRemovalActions(
                        unitCells = emptyCells,
                        cellsWithNakedTuple = cellIndices,
                        tupleCandidates = tuple,
                    )
                    if (removals.isNotEmpty()) {
                        nakedTuples.add(NakedTuple(unit.id, tuple, cellIndices, removals))
                    }
                }
            }
        }

        return if (nakedTuples.isEmpty()) null else NakedTuplesUse(techniqueName, nakedTuples)
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
        cellsWithNakedTuple: Set<CellIndex>,
        tupleCandidates: Set<Digit>,
    ): List<Action.RemoveCandidate> = buildList {
        for (cell in unitCells) {
            val cellIndex = cell.index
            if (cellIndex !in cellsWithNakedTuple) {
                val candidatesToRemove = cell.candidates intersect tupleCandidates
                addAll(candidatesToRemove.map { Action.RemoveCandidate(it, cellIndex) })
            }
        }
    }
}

data class NakedTuplesUse(
    override val techniqueName: String,
    val nakedTuples: List<NakedTuple>,
): TechniqueUse {
    override val actions: List<Action>
        get() = nakedTuples.flatMap { it.removals }.distinct()
}

data class NakedTuple(
    val unit: UnitId,
    val tuple: Set<Digit>,
    val cells: Set<CellIndex>,
    val removals: List<Action.RemoveCandidate>,
)
