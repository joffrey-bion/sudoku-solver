package org.hildan.sudoku.ui

import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.solver.HumanSolver
import org.hildan.sudoku.solver.solvePath
import org.hildan.sudoku.ui.components.StepViewer
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    // TODO make a text field with import button
    val encodedGrid = "4...9.8......2..5...75...69.....2...9...4..16.356..7..6.....4...1..............93"
    val grid = Grid(encodedGrid)
    grid.removeImpossibleCandidates()

    val path = HumanSolver().solve(grid).solvePath(encodedGrid)

    renderComposable(rootElementId = "root") {
        StepViewer(path)
        Style(SudokuStylesheet)
    }
}

private object SudokuStylesheet : StyleSheet() {
    init {
        "body" style {
            backgroundColor(rgb(66, 66, 66))
            color(Color.white)
            fontFamily("Helvetica Neue")
            margin(0.px)
        }
    }
}
