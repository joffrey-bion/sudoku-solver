package org.hildan.sudoku.test

import org.hildan.sudoku.solver.Solver
import org.junit.Test

class SolverTest {

    @Test
    fun test() {
        val solver = Solver()
        println("== EASY GRID =======================")
        solver.solveAndPrintStats(TestGrids.easyGrid)
        println("\n== MEDIUM GRID =====================")
        solver.solveAndPrintStats(TestGrids.mediumGrid)
        println("\n== HARD GRID =======================")
        solver.solveAndPrintStats(TestGrids.hardGrid)
        println("\n== EVIL GRID =======================")
        solver.solveAndPrintStats(TestGrids.evilGrid)
    }
}
