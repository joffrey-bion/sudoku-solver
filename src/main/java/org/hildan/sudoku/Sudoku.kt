package org.hildan.sudoku;

import org.hildan.sudoku.solver.Solver;

public class Sudoku {

    public static void main(String[] args) {
        Solver solver = new Solver();
        if (args.length > 0) {
            solver.solveAndPrintStats(args);
            return;
        }
        System.out.println("You must provide sudoku data.");
    }
}
