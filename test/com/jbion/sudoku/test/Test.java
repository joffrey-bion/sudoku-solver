package com.jbion.sudoku.test;

import com.jbion.sudoku.solver.Solver;

public class Test {
    
    public static void main(String[] args) {
        Solver solver = new Solver();
        if (args.length != 0) {
            solver.solveAndPrintStats(args);
            return;
        }
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
