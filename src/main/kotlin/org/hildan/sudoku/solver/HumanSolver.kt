package org.hildan.sudoku.solver

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.model.removeValueFromSistersCandidates
import org.hildan.sudoku.solver.techniques.*

data class SolveResult(
    val solved: Boolean,
    val steps: List<TechniqueUse>
) {
    override fun toString(): String = steps.joinToString("\n") {
        "${it.techniqueName}:\n" + it.actions.joinToString("\n").prependIndent("  - ")
    }
}

private val coreTechniques = listOf(
    NakedSingles,
    HiddenSingles,
    NakedPairs,
    NakedTriples,
    NakedQuads,
    HiddenPairs,
    HiddenTriples,
    HiddenQuads,
    PointingTuples,
    XWing,
    Swordfish,
    Jellyfish,
    Squirmbag,
)

class HumanSolver(
    private val orderedTechniques: List<Technique> = coreTechniques
) {

    init {
        require(orderedTechniques.contains(NakedSingles)) { "Cannot operate without NakedSingles technique" }
        require(orderedTechniques.contains(HiddenSingles)) { "Cannot operate without HiddenSingles technique" }
    }

    fun solve(grid: Grid): SolveResult {
        val stillValid = grid.clearImpossibleValues()
        require(stillValid) { "Incorrect clues in the given grid." }

        val techniqueUses = mutableListOf<TechniqueUse>()
        while (!grid.isComplete) {
            val result = findApplicableTechnique(grid) ?: return SolveResult(solved = false, techniqueUses)
            techniqueUses.add(result)
            result.actions.forEach { it.applyTo(grid) }
        }
        return SolveResult(solved = true, techniqueUses)
    }

    private fun findApplicableTechnique(grid: Grid) = orderedTechniques.firstNotNullOfOrNull { it.attemptOn(grid) }
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
