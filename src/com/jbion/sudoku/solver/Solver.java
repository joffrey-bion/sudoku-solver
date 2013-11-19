package com.jbion.sudoku.solver;

import java.util.Arrays;
import java.util.LinkedList;

import com.jbion.sudoku.model.Grid;
import com.jbion.sudoku.model.TestGrids;
import com.jbion.sudoku.model.Tile;

public class Solver {

    public static boolean FC = true; // use Forward-Checking
    public static boolean H = true; // use heuristics MCV1 and MCV2
    public static boolean H3 = true; // use heuristic LCV

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
    
    /**
     * Prepares the grid, solve the grid and print the execution time and number of
     * visited nodes.
     * 
     * @param input
     *            The list of all values in the initial grid, rows after rows, with 0
     *            for the empty tiles.
     */
    public void solveAndPrintStats(String[] input) {
        Grid grid;
        try {
            grid = new Grid(input);
        } catch (Exception e) {
            System.err.println("INPUT ERROR: " + e.getMessage());
            return;
        }
        System.out.println(grid);
        long startTime = System.nanoTime();
        Assignment solution = solve(grid);
        long executionTime = System.nanoTime() - startTime;
        System.out.println(solution);
        System.out.println(solution.nbVisitedNodes + " nodes have been visited");
        System.out.println("Execution time: " + executionTime / 1000000 + " ms");
    }

    /**
     * Solve the given grid and returns the solution.
     * 
     * @param grid
     *            The grid to solve.
     * @return The Assignment object corresponding to the solution, or to the failure
     *         if no solution were found.
     */
    public Assignment solve(Grid grid) {
        // Forward-Checking preparation
        if (FC) {
            // find the clue-tiles and remove the possible values in the impacted
            // empty tiles before starting the search.
            for (Tile[] row : Arrays.asList(grid.tiles)) {
                for (Tile tile : Arrays.asList(row)) {
                    if (tile.currentValue == 0) {
                        continue; // do not consider empty tiles
                    }
                    boolean success = tile.removeValueFromSisters(tile.currentValue);
                    if (!success) {
                        throw new RuntimeException("Incorrect clues in the given grid");
                    }
                }
            }
        }
        // Start recursive backtracking search
        Assignment assignment = new Assignment(grid);
        backtracking(assignment);
        return assignment;
    }

    /**
     * Recursive backtracking search. If the forward checking is enabled, the
     * possible values of the tiles must have been already updated due to the
     * constraints of the clues.
     * 
     * @param assignment
     *            The current assignment containing the current grid. It will be
     *            modified.
     * @return Whether a solution has been found or not.
     */
    private boolean backtracking(Assignment assignment) {
        if (assignment.isSolution()) {
            return true;
        }
        // Choose an empty tile (unassigned variable)
        Tile tile = selectUnassignedVariable(assignment.grid);
        assignment.nbVisitedNodes++;
        // Try the possible values for this tile
        for (int value : getOrderDomainValues(tile)) {
            // Check whether the value is consistent in the current grid.
            if (!FC && !tile.isConsistent(value)) {
                // This test is not necessary when forward-checking is enabled, since
                // FC reduces
                // the set of possible values during the search
                continue;
            }

            // Give a value to the tile (assign the variable)
            boolean success = assignValue(tile, value);
            // If the forward-checking detected failure, don't try the value
            if (success) {
                // Recursive search, with that value given to the tile
                backtracking(assignment);
                // Return the solution, if any were found
                if (assignment.isSolution())
                    return true;
            }
            // Clear the tile (remove the variable from assignment) to try other
            // values
            unassignValue(tile);
        }
        return false;
    }

    /**
     * Choose the next empty tile to fill.
     * 
     * @param grid
     *            The current grid.
     * @return The chosen tile.
     */
    private static Tile selectUnassignedVariable(Grid grid) {
        // Without heuristics, take the first one which comes
        if (!H) {
            return grid.emptyTiles.getFirst();
        }

        // MOST CONSTRAINED VARIABLE heuristic
        // We try here to choose a tile with the fewest possible values
        int minLCV = 9;
        LinkedList<Tile> listLCV = new LinkedList<>();
        for (Tile tile : grid.emptyTiles) {
            int size = tile.possibleValues.size();
            if (size == minLCV) {
                listLCV.add(tile);
            } else if (size < minLCV) {
                listLCV.removeAll(listLCV);
                listLCV.add(tile);
                minLCV = size;
            }
        }

        // MOST CONSTRAINING VARIABLE heuristic
        // We try here to choose a tile with the most empty sisters
        int maxMCV = 0;
        LinkedList<Tile> listMCV = new LinkedList<>();
        for (Tile tile : listLCV) {
            // compute the number of empty sisters of 'tile'
            int size = tile.getNbOfEmptySisters();
            if (size == maxMCV) {
                listMCV.add(tile);
            } else if (size > maxMCV) {
                listMCV.removeAll(listMCV);
                listMCV.add(tile);
                maxMCV = size;
            }
        }
        return listMCV.getFirst();
    }

    /**
     * Gives an ordered list of values to test for the given tile.
     * 
     * @param tile
     *            The tile we want to fill.
     * @return An LinkedList of values to try for the given tile, in the right order.
     */
    private static LinkedList<Integer> getOrderDomainValues(Tile tile) {
        if (!H3 || tile.possibleValues.size() <= 1) {
            // the second part of the test saves some time
            return new LinkedList<>(tile.possibleValues);
        }
        // Least Constraining Value
        // For each possible value of the Tile, we count the number of possibilities
        // which will be ruled out by the forward checking if we assign this value to
        // this tile. That is the number of sisters having this value in their
        // possibilities.
        Integer[] nbImpacted = new Integer[9];
        Arrays.fill(nbImpacted, 0);
        for (int value : tile.possibleValues) {
            for (Tile sister : tile.getSisters()) {
                if (sister.currentValue != 0)
                    continue; // skip the filled sisters
                if (sister.possibleValues.contains(value))
                    nbImpacted[value - 1]++;
            }
        }
        // Now we have to sort the possible values, choosing first those which have
        // the less impact on the sisters, according to the numbers we have just
        // computed.
        Integer[] sortedNbImpacted = nbImpacted.clone();
        Arrays.sort(sortedNbImpacted);
        LinkedList<Integer> sortedValues = new LinkedList<>();
        for (int n : sortedNbImpacted) {
            if (n > 0) {
                // we have to find the value corresponding to that number of impacted
                // sisters, it corresponds to the index (+1) of that number in the
                // first array
                for (int i = 0; i < nbImpacted.length; i++) {
                    if (n == nbImpacted[i]) {
                        sortedValues.add(i + 1);
                        nbImpacted[i] = 0; // to avoid duplicates
                    }
                }
            }
        }
        return sortedValues;
    }

    /**
     * Assign the given value to the given tile, and, if forward checking is enabled,
     * update the sisters' possibilities.
     * 
     * @param tile
     *            The tile to be filled
     * @param value
     *            The value to give to the tile
     * @return {@code false} if the forward checking is enabled and one of the
     *         sisters has no more possible values, {@code true} otherwise.
     */
    private static boolean assignValue(Tile tile, int value) {
        tile.currentValue = value;
        tile.grid.emptyTiles.remove(tile);

        // FORWARD CHECKING
        // Propagate the constraints right now to foresee the problems
        if (FC) {
            return tile.removeValueFromSisters(value);
        }
        return true;
    }

    /**
     * Remove the assigned value to the given tile, and, if forward checking is
     * enabled, update the sisters' possibilities.
     * 
     * @param tile
     *            The tile to be cleared
     */
    private static void unassignValue(Tile tile) {
        int value = tile.currentValue;
        tile.currentValue = 0;
        tile.grid.emptyTiles.add(tile);

        // FORWARD CHECKING
        // Restore the possibilities that had been removed
        if (FC) {
            tile.restoreValueInSisters(value);
        }
    }
}
