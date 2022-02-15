package org.hildan.sudoku.model

enum class UnitType {
    ROW, COLUMN, BOX
}

data class UnitId(
    val type: UnitType,
    val index: Int,
) {
    override fun toString(): String = "$type ${index + 1}"
}

class GridUnit(
    val id: UnitId,
    val cells: List<Cell>,
) {
    override fun toString(): String = id.toString()
}
