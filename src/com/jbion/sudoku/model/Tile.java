package com.jbion.sudoku.model;

import java.util.HashSet;

public class Tile {

    public Grid grid;
    public Integer currentValue;
    public HashSet<Integer> possibleValues; // used by forward checking

    private Integer row;
    private Integer col;
    private HashSet<Tile> sisters;

    public Tile(Grid grid, int row, int col, int value) {
        this.grid = grid;
        this.row = row;
        this.col = col;
        this.currentValue = value;

        possibleValues = new HashSet<>();
        if (value == 0) {
            for (int i = 1; i <= 9; i++) {
                possibleValues.add(i);
            }
        }
    }

    /**
     * Returns whether {@code value} is acceptable for this tile, in the current
     * grid.
     * 
     * @param value
     *            The value to test.
     * @return Whether {@code value} is acceptable for this tile, in the current
     *         grid.
     */
    public boolean isConsistent(int value) {
        boolean res = true;
        // check the sisters
        for (Tile sister : getSisters()) {
            res = res && (sister.currentValue != value);
        }
        return res;
    }

    /**
     * Remove the value from the list of possibilities of the sisters.
     * 
     * @param value
     *            The value to remove.
     * @return {@code true} if success, {@code false} if one of the sisters has no
     *         more possible values.
     */
    public boolean removeValueFromSisters(int value) {
        for (Tile sister : getSisters()) {
            if (sister.currentValue == 0) {
                sister.possibleValues.remove(value);
                if (sister.possibleValues.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Put back the value in the list of possibilities of the sisters (only if the
     * value is consistent with the current grid).
     * 
     * @param value
     *            The value to restore.
     */
    public void restoreValueInSisters(int value) {
        for (Tile sister : getSisters()) {
            if (sister.currentValue == 0 && sister.isConsistent(value)) {
                sister.possibleValues.add(value);
            }
        }
    }

    /**
     * Gives the tiles which are either in the same row or column or region as this
     * tile.
     * 
     * @return A set of the sisters of this tile.
     */
    public HashSet<Tile> getSisters() {
        if (sisters != null)
            return sisters;
        sisters = new HashSet<>();
        // get the row
        for (int j = 0; j < 9; j++) {
            sisters.add(grid.tiles[row][j]);
        }
        // get the column
        for (int i = 0; i < 9; i++) {
            sisters.add(grid.tiles[i][col]);
        }
        // get the region
        int baserow = (row / 3) * 3;
        int basecol = (col / 3) * 3;
        for (int i = 0; i < 3; i++) {
            if (baserow + i == row)
                continue;
            for (int j = 0; j < 3; j++) {
                if (basecol + j == col)
                    continue;
                sisters.add(grid.tiles[baserow + i][basecol + j]);
            }
        }
        return sisters;
    }

    /**
     * Gives the number of empty tiles which are sisters of this tile.
     * 
     * @return The number of empty tiles which are either on the same row, column or
     *         region as this tile.
     */
    public int getNbOfEmptySisters() {
        int result = 0;
        for (Tile t : this.getSisters()) {
            if (t.currentValue == 0)
                result++;
        }
        return result;
    }

    /**
     * Returns a String representing this tile's coordinates.
     * 
     * @return a String of the form (row,column).
     */
    public String coords() {
        return "(" + row + "," + col + ")";
    }

    /**
     * Returns a space if this tile is empty, the digit of its value otherwise.
     */
    @Override
    public String toString() {
        if (currentValue == 0) {
            return " ";
        } else {
            return currentValue.toString();
        }
    }
}
