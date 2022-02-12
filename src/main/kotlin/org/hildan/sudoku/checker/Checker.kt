package org.hildan.sudoku.checker

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.model.Tile

sealed class SUnit {
    data class Row(val index: Int): SUnit()
    data class Col(val index: Int): SUnit()
    data class Box(val index: Int): SUnit()
}

sealed class CheckResult {
    object Valid : CheckResult()
    data class Invalid(val unit: SUnit): CheckResult()
}

fun Grid.check(): CheckResult {
    repeat(Grid.SIZE) { n ->
        if (rows[n].containsAllDigits()) {
            return CheckResult.Invalid(SUnit.Row(n))
        }
        if (cols[n].containsAllDigits()) {
            return CheckResult.Invalid(SUnit.Col(n))
        }
        if (boxes[n].containsAllDigits()) {
            return CheckResult.Invalid(SUnit.Box(n))
        }
    }
    return CheckResult.Valid
}

private fun List<Tile>.containsAllDigits() = mapTo(HashSet()) { it.value }.size != Grid.SIZE
