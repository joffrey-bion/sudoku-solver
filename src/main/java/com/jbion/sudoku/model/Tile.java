package com.jbion.sudoku.model;

import java.util.HashSet;

/**
 * Represents one tile in a Sudoku {@link Grid}.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey Bion</a>
 */
public class Tile {

    /**
     * The {@link Grid} containing this {@code Tile}.
     */
	private Grid grid;
    /**
     * The possible values for this {@code Tile}. This set has to be manually
     * updated. This is to fully separate the solver's logic from the model's logic.
     */
    public HashSet<Integer> possibleValues; // used by forward checking

    /**
     * The current value of this {@code Tile}. 0 represents an empty {@code Tile}.
     */
    private int currentValue;
    private int row;
    private int col;
    private HashSet<Tile> sisters;

    /**
     * Creates a new empty {@code Tile} at the specified position.
     * 
     * @param grid
     *            The parent {@link Grid} for this {@code Tile}.
     * @param row
     *            The row of this {@code Tile} in the grid.
     * @param col
     *            The column of this {@code Tile} in the grid.
     */
    public Tile(Grid grid, int row, int col) {
        this.grid = grid;
        this.row = row;
        this.col = col;
        possibleValues = new HashSet<>();
        for (int i = 1; i <= Grid.SIZE; i++) {
            possibleValues.add(i);
        }
    }

    /**
     * Creates a new {@code Tile} at the specified position with the specified value.
     * 
     * @param grid
     *            The parent {@link Grid} for this {@code Tile}.
     * @param row
     *            The row of this {@code Tile} in the grid.
     * @param col
     *            The column of this {@code Tile} in the grid.
     * @param value
     *            The initial value for this {@code Tile}.
     */
    public Tile(Grid grid, int row, int col, int value) {
        this(grid, row, col);
        this.setValue(value);
        possibleValues.clear();
    }

    /**
     * Sets the tiles which are either in the same row or column or region as this
     * tile.
     */
    void setSisters(HashSet<Tile> sisters) {
        this.sisters = sisters;
    }

    /**
     * Gives the tiles which are either in the same row or column or region as this
     * tile.
     * 
     * @return A set of the sisters of this {@code Tile}.
     */
    public HashSet<Tile> getSisters() {
        return sisters;
    }

    /**
     * Returns the current value of this {@code Tile}.
     * 
     * @return the current value of this {@code Tile}.
     */
    public int getValue() {
        if (currentValue == 0) {
            throw new RuntimeException("Cannot get the value from an empty tile.");
        }
        return currentValue;
    }

    /**
     * Sets the value of this {@code Tile}.
     * 
     * @param value
     *            The value to give to this {@code Tile}.
     */
    public void setValue(int value) {
        if (value < 1 || value > Grid.SIZE) {
            throw new IllegalArgumentException("The value must be between 1 and " + Grid.SIZE);
        }
        this.currentValue = value;
        grid.getEmptyTiles().remove(this);
    }

    /**
     * Returns whether this {@code Tile} is empty.
     * 
     * @return {@code true} if this {@code Tile} is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return currentValue == 0;
    }

    /**
     * Sets this tile as empty.
     */
    public void setEmpty() {
        this.currentValue = 0;
        grid.getEmptyTiles().add(this);
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
        for (Tile sister : getSisters()) {
            if (sister.currentValue == value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove this {@code Tile}'s current value from the list of possibilities of its
     * sisters.
     * 
     * @return {@code true} if success, {@code false} if one of the sisters has no
     *         more possible values.
     */
    public boolean removeValueFromSisters() {
        for (Tile sister : getSisters()) {
            if (sister.isEmpty()) {
                sister.possibleValues.remove(currentValue);
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
     * Gives the number of empty tiles which are sisters of this tile.
     * 
     * @return The number of empty tiles which are either on the same row, column or
     *         region as this tile.
     */
    public int getNbOfEmptySisters() {
        int result = 0;
        for (Tile t : this.getSisters()) {
            if (t.currentValue == 0) {
                result++;
            }
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
            return String.valueOf(currentValue);
        }
    }
}
