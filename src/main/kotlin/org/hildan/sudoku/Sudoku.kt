package org.hildan.sudoku

import org.hildan.sudoku.drawing.format
import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.solver.solveWithBacktracking

fun main() {
    val input = readlnOrNull()
    if (input == null) {
        println("Expected sudoku grid on stdin, 81 characters that are either digits 1-9 or '.' to denote empty cells")
        return
    }
    solveAndPrintStats(input)
}

/**
 * Prepares the grid, solve the grid and print the execution time and number of visited nodes.
 *
 * @param input a string of 81 characters representing the cells in the initial grid, row by row, with '.' for the
 * empty tiles.
 */
fun solveAndPrintStats(input: String) {
    val grid: Grid = try {
        Grid(input)
    } catch (e: Exception) {
        System.err.println("INPUT ERROR: " + e.message)
        return
    }
    //System.out.println(grid);
    val startTime = System.nanoTime()
    val nbVisitedNodes = grid.solveWithBacktracking()
    val executionTime = System.nanoTime() - startTime
    println(if (grid.isComplete) grid.format() else "No solution found.")
    println("$nbVisitedNodes nodes have been visited")
    println("Execution time: " + executionTime / 1000000 + " ms")
}
