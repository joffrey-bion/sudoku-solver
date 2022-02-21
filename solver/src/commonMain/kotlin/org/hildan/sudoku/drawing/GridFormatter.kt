package org.hildan.sudoku.drawing

import org.hildan.sudoku.model.Grid

private const val HORIZONTAL_LINE = "\u2500"
private const val VERTICAL_LINE = "\u2502"
private const val CORNER_TOP_LEFT = "\u250C"
private const val CORNER_BOTTOM_LEFT = "\u2514"
private const val CORNER_TOP_RIGHT = "\u2510"
private const val CORNER_BOTTOM_RIGHT = "\u2518"
private const val TEE_TOP = "\u252C"
private const val TEE_BOTTOM = "\u2534"
private const val TEE_LEFT = "\u251C"
private const val TEE_RIGHT = "\u2524"
private const val MIDDLE_CROSS = "\u253C"

fun Grid.format() = buildString {
    appendLine(formatTopLine())
    repeat(Grid.SIZE) { row ->
        append(formatRow(row))
        if (row % Grid.BOX_SIDE_SIZE == Grid.BOX_SIDE_SIZE - 1 && row != Grid.SIZE - 1) {
            appendLine(formatMiddleLine())
        }
    }
    append(formatBottomLine())
}

private fun Grid.formatRow(row: Int) = buildString {
    append(VERTICAL_LINE)
    repeat(Grid.SIZE) { col ->
        append(get(row, col).value ?: " ")
        if ((col + 1) % Grid.BOX_SIDE_SIZE == 0) {
            append(VERTICAL_LINE)
        } else {
            append(" ")
        }
    }
    appendLine()
}

private fun formatTopLine() = horizontalLine(
    cSep = TEE_TOP,
    prefix = CORNER_TOP_LEFT,
    suffix = CORNER_TOP_RIGHT,
)

private fun formatMiddleLine() = horizontalLine(
    cSep = MIDDLE_CROSS,
    prefix = TEE_LEFT,
    suffix = TEE_RIGHT,
)

private fun formatBottomLine() = horizontalLine(
    cSep = TEE_BOTTOM,
    prefix = CORNER_BOTTOM_LEFT,
    suffix = CORNER_BOTTOM_RIGHT,
)

private fun horizontalLine(cSep: CharSequence, prefix: CharSequence, suffix: CharSequence): String =
    (0..2).joinToString(separator = cSep, prefix = prefix, postfix = suffix) {
        HORIZONTAL_LINE.repeat(5)
    }
