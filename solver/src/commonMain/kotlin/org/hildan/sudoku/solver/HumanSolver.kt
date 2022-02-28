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
            val result = findApplicableTechnique(grid) ?: return SolveResult(solved = false, steps)
            steps.addAll(result)
            result.flatMap { it.actions }.forEach {
                it.applyTo(grid)
            }
        }
        return SolveResult(solved = true, steps)
    }

    private fun findApplicableTechnique(grid: Grid) = orderedTechniques.asSequence()
        .map { it.attemptOn(grid) }
        .firstOrNull { it.isNotEmpty() }
}

private fun Action.applyTo(grid: Grid) {
    when(this) {
        is Action.PlaceDigit -> {
            val cell = grid.cells[cellIndex]
            cell.value = digit
            cell.removeValueFromSistersCandidates()
            grid.emptyCells.remove(cell)
        }
        is Action.RemoveCandidate -> {
            val cell = grid.cells[cellIndex]
            cell.candidates.remove(candidate)
        }
    }
}
