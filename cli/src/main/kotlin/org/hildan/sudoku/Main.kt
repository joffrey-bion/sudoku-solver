package org.hildan.sudoku

import org.hildan.sudoku.drawing.format
import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.solver.backtracking.solveWithBacktracking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val input = args.firstOrNull() ?: readlnOrNull()
    if (input == null) {
        println("Expected sudoku grid on stdin, 81 characters that are either digits 1-9, or '.' to denote empty cells")
        return
    }

    val grid: Grid = try {
        Grid(input)
    } catch (e: IllegalArgumentException) {
        System.err.println("INPUT ERROR: " + e.message)
        return
    }

    val (nbVisitedNodes, execTime) = measureTimedValue { grid.solveWithBacktracking() }
    println(if (grid.isComplete) grid.format() else "No solution found.")
    println("$nbVisitedNodes nodes have been visited")
    println("Execution time: $execTime")
}
