package org.hildan.sudoku

import org.hildan.sudoku.solver.Solver

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("You must provide sudoku data.")
        return
    }
    Solver().solveAndPrintStats(args)
}
