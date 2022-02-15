package org.hildan.sudoku.checker

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.model.Cell
import org.hildan.sudoku.model.UnitId

sealed class CheckResult {
    object Valid : CheckResult()
    data class Invalid(val unit: UnitId): CheckResult()
}

fun Grid.check(): CheckResult {
    units.forEach {
        if (!it.cells.containsDistinctDigits()) {
            return CheckResult.Invalid(it.id)
        }
    }
    return CheckResult.Valid
}

private fun List<Cell>.containsDistinctDigits() = mapNotNullTo(HashSet()) { it.value }.size == count { !it.isEmpty }
