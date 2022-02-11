package org.hildan.sudoku.test

import org.hildan.sudoku.checker.check
import org.hildan.sudoku.model.Grid
import org.hildan.sudoku.solveAndPrintStats
import org.hildan.sudoku.solver.Solver
import org.junit.Test

class SolverTest {

    @Test
    fun test() {
        println("== EASY GRID =======================")
        solveAndPrintStats(TestGrids.easyGrid)
        println("\n== MEDIUM GRID =====================")
        solveAndPrintStats(TestGrids.mediumGrid)
        println("\n== HARD GRID =======================")
        solveAndPrintStats(TestGrids.hardGrid)
        println("\n== EVIL GRID =======================")
        solveAndPrintStats(TestGrids.evilGrid)
    }
}
