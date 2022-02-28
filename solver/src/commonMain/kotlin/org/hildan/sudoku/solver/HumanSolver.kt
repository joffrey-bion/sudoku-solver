package org.hildan.sudoku.solver

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.model.removeValueFromSistersCandidates
import org.hildan.sudoku.solver.techniques.*

data class SolveResult(
    val solved: Boolean,
    val steps: List<Step>,
) {
    override fun toString(): String = steps.joinToString("\n") {
        "${it.techniqueName}:\n" + it.actions.joinToString("\n").prependIndent("  - ")
    }
}

class HumanSolver(
    private val orderedTechniques: List<Technique> = listOf(
        NakedSingles,
        HiddenSingles,
        NakedPairs,
        NakedTriples,
        NakedQuads,
        HiddenPairs,
        HiddenTriples,
        HiddenQuads,
        PointingTuples,
        BoxLineReduction,
        XWing,
        Swordfish,
        Jellyfish,
        Squirmbag,
    )
) {
    fun solve(grid: Grid): SolveResult {
        val stillValid = grid.removeImpossibleCandidates()
        require(stillValid) { "Incorrect clues in the given grid." }

        val steps = mutableListOf<Step>()
        while (!grid.isComplete) {
            val result = orderedTechniques.firstApplicableTo(grid) ?: return SolveResult(solved = false, steps)
            steps.addAll(result)
            grid.performActions(result.flatMap { it.actions })
        }
        return SolveResult(solved = true, steps)
    }

    private fun List<Technique>.firstApplicableTo(grid: Grid) = asSequence()
        .map { it.attemptOn(grid) }
        .firstOrNull { it.isNotEmpty() }
}

fun Grid.performActions(actions: List<Action>) {
    actions.forEach(::performAction)
}

private fun Grid.performAction(action: Action) {
    when(action) {
        is Action.PlaceDigit -> {
            val cell = cells[action.cellIndex]
            cell.value = action.digit
            cell.removeValueFromSistersCandidates()
            emptyCells.remove(cell)
        }
        is Action.RemoveCandidate -> {
            val cell = cells[action.cellIndex]
            cell.candidates.remove(action.candidate)
        }
    }
}
