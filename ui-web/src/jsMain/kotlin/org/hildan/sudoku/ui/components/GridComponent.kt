package org.hildan.sudoku.ui.components

import androidx.compose.runtime.Composable
import org.hildan.sudoku.model.ALL_DIGITS
import org.hildan.sudoku.model.Cell
import org.hildan.sudoku.model.Digit
import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.model.GridUnit
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import kotlin.text.Typography.nbsp

val cellSize = 5.cssRem

@Composable
fun SudokuGrid(grid: Grid) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            width(cellSize * Grid.SIZE)
            border(width = 2.px, style = LineStyle.Solid)
        }
    }) {
        repeat(Grid.SIZE) { rowIndex ->
            SudokuRow(row = grid.rows[rowIndex])
        }
    }
}

@Composable
fun SudokuRow(row: GridUnit) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Row)
        }
    }) {
        repeat(Grid.SIZE) { col ->
            SudokuCell(cell = row.cells[col])
        }
    }
}

val thickBorder = CSSBorder().apply {
    style = LineStyle.Solid
    width = 2.px
}

@Composable
fun SudokuCell(cell: Cell) {
    Div({
        style {
            height(cellSize)
            width(cellSize)
            border(width = 1.px, style = LineStyle.Solid)
            cell.boxSides.forEach { side ->
                property("border-$side", thickBorder)
            }

            fontFamily("system-ui")
            if (!cell.isEmpty) {
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                fontSize(4.cssRem)
            }
        }
    }) {
        if (cell.isEmpty) {
            Candidates(cell.candidates)
        } else {
            CellDigitText(cell.value!!)
        }
    }
}

@Composable
private fun Candidates(candidates: Set<Digit>) {
    Div({
        style {
            display(DisplayStyle.Grid)
            gridTemplateColumns("1fr 1fr 1fr") // 3 cols with equal fractions
            fontSize(1.cssRem)
            height(100.percent)
            width(100.percent)
        }
    }) {
        ALL_DIGITS.forEach { digit ->
            Div({
                style {
                    textAlign("center")
                }
            }) {
                Text(if (digit in candidates) "$digit" else "$nbsp")
            }
        }
    }
}

@Composable
private fun CellDigitText(digit: Digit) {
    Div({
        style {
            fontSize(4.cssRem)
        }
    }) {
        Text("$digit")
    }
}

val Cell.boxSides: List<String> get() = buildList {
    when (row % Grid.BOX_SIDE_SIZE) {
        0 -> add("top")
        Grid.BOX_SIDE_SIZE - 1 -> add("bottom")
    }
    when (col % Grid.BOX_SIDE_SIZE) {
        0 -> add("left")
        Grid.BOX_SIDE_SIZE - 1 -> add("right")
    }
}
