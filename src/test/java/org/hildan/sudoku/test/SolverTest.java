package org.hildan.sudoku.test;

import org.hildan.sudoku.solver.Solver;
import org.junit.Test;

public class SolverTest {

    @Test
    public void test() {
        Solver solver = new Solver();
        System.out.println("== EASY GRID =======================");
        solver.solveAndPrintStats(TestGrids.easyGrid);
        System.out.println("\n== MEDIUM GRID =====================");
        solver.solveAndPrintStats(TestGrids.mediumGrid);
        System.out.println("\n== HARD GRID =======================");
        solver.solveAndPrintStats(TestGrids.hardGrid);
        System.out.println("\n== EVIL GRID =======================");
        solver.solveAndPrintStats(TestGrids.evilGrid);
    }

}
