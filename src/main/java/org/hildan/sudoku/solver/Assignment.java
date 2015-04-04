package org.hildan.sudoku.solver;

import org.hildan.sudoku.model.Grid;

public class Assignment {
    
    public Grid grid;
    public int nbVisitedNodes;
    
    public Assignment(Grid grid) {
        super();
        this.grid = grid;
        this.nbVisitedNodes = 0;
    }
    
    public boolean isSolution() {
        return grid != null && grid.getEmptyTiles().isEmpty();
    }

    @Override
    public String toString() {
        if (isSolution())
            return grid.toString();
        return "No solution found.";
    }
}
