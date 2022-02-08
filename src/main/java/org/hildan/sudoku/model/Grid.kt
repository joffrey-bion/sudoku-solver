package org.hildan.sudoku.model;

import org.hildan.sudoku.drawing.BoxChars;
import org.hildan.sudoku.drawing.Drawing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Represents a grid of Sudoku.
 *
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey Bion</a>
 */
public class Grid {

    /**
     * Size of the regions within each grid.
     */
    private static final int RSIZE = 3;

    /**
     * Size of the grids.
     */
    static final int SIZE = RSIZE * RSIZE;

    private Tile[][] tiles;

    private LinkedList<Tile> emptyTiles;

    /**
     * Creates a new {@code Grid} containing the specified numbers.
     *
     * @param numbers
     *            The numbers to put in this {@code Grid}, listed row by row, from the upper one to
     *            the lower one, in left-to-right order within a row.
     * @throws IllegalArgumentException
     *             If there is not enough numbers, or too many, or some other characters than
     *             numbers.
     */
    public Grid(String[] numbers) {
        // Deal with obvious input errors
        if (numbers.length < SIZE * SIZE)
            throw new IllegalArgumentException("too few input digits (blanks must be given by zeros)");
        if (numbers.length > SIZE * SIZE)
            throw new IllegalArgumentException("too many input digits, only " + SIZE * SIZE + " are needed");

        // Start initialization
        emptyTiles = new LinkedList<>();
        tiles = new Tile[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // parse the value at the position (i,j)
                int value;
                try {
                    value = Integer.parseInt(numbers[SIZE * i + j]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("wrong input, only digits are accepted");
                }
                // create the tile at the position (i,j)
                if (value == 0) {
                    tiles[i][j] = new Tile(this, i, j);
                    emptyTiles.add(tiles[i][j]);
                } else if (0 < value && value <= SIZE) {
                    tiles[i][j] = new Tile(this, i, j, value);
                } else {
                    throw new IllegalArgumentException("wrong input, only digits from 0 to " + SIZE + " are accepted");
                }
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                tiles[i][j].setSisters(getSisters(i, j));
            }
        }
    }

    /**
     * Gives the tiles which are either in the same row or column or region as the specified
     * coordinates.
     * 
     * @param row
     *            the row number of the cell
     * @param col
     *            the column number of the cell
     *
     * @return A set of the sisters of this tile.
     */
    HashSet<Tile> getSisters(int row, int col) {
        HashSet<Tile> sisters = new HashSet<>();
        // add the row
        for (int j = 0; j < Grid.SIZE; j++) {
            if (j == col) {
                continue;
            }
            sisters.add(tiles[row][j]);
        }
        // add the column
        for (int i = 0; i < Grid.SIZE; i++) {
            if (i == row) {
                continue;
            }
            sisters.add(tiles[i][col]);
        }
        // add the region
        int baserow = (row / Grid.RSIZE) * Grid.RSIZE;
        int basecol = (col / Grid.RSIZE) * Grid.RSIZE;
        for (int i = 0; i < Grid.RSIZE; i++) {
            if (baserow + i == row) {
                continue; // skips the row, already added
            }
            for (int j = 0; j < Grid.RSIZE; j++) {
                if (basecol + j == col) {
                    continue; // skips the column, already added
                }
                sisters.add(tiles[baserow + i][basecol + j]);
            }
        }
        return sisters;
    }

    /**
     * Iterates on complete {@link Tile}s and remove the corresponding value in the empty sister
     * {@code Tile}s.
     *
     * @return {@code false} if an empty {@link Tile} ends up with no possible value.
     */
    public boolean clearImpossibleValues() {
        for (Tile[] row : Arrays.asList(tiles)) {
            for (Tile tile : Arrays.asList(row)) {
                if (tile.isEmpty()) {
                    continue; // do not consider empty tiles
                }
                if (!tile.removeValueFromSisters()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the matrix of the tiles of this {@code Grid}.
     *
     * @return the matrix of the tiles of this {@code Grid}.
     */
    Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Returns the list of the empty tiles of this {@code Grid}.
     *
     * @return the list of the empty tiles of this {@code Grid}.
     */
    public LinkedList<Tile> getEmptyTiles() {
        return emptyTiles;
    }

    /**
     * Returns whether this {@code Grid} is complete.
     *
     * @return {@code true} if all tiles have a value, {@code false} otherwise.
     */
    public boolean isFull() {
        return emptyTiles.isEmpty();
    }

    private static final String LF = System.getProperty("line.separator");

    private static final String H = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_HORIZONTAL);

    private static final String V = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL);

    private static final String ULC = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_DOWN_AND_RIGHT);

    private static final String DLC = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_UP_AND_RIGHT);

    private static final String URC = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_DOWN_AND_LEFT);

    private static final String DRC = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_UP_AND_LEFT);

    private static final String DTEE = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_DOWN_AND_HORIZONTAL);

    private static final String UTEE = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_UP_AND_HORIZONTAL);

    private static final String RTEE = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL_AND_RIGHT);

    private static final String LTEE = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL_AND_LEFT);

    private static final String CROSS = String.valueOf(BoxChars.BOX_DRAWINGS_LIGHT_VERTICAL_AND_HORIZONTAL);

    /**
     * Prints the grid with fancy lines.
     */
    @Override
    public String toString() {
        String res = Drawing.repeat(H, 3, 5, DTEE, ULC, URC) + LF;
        for (int i = 0; i < SIZE; i++) {
            res += V;
            for (int j = 0; j < SIZE; j++) {
                res = res.concat(tiles[i][j].toString());
                if ((j + 1) % RSIZE == 0) {
                    res += V;
                } else {
                    res += " ";
                }
            }
            res += LF;
            if (i % RSIZE == RSIZE - 1 && i != SIZE - 1) {
                res += Drawing.repeat(H, 3, 5, CROSS, RTEE, LTEE) + LF;
            }
        }
        res += Drawing.repeat(H, 3, 5, UTEE, DLC, DRC);
        return res;
    }

    /**
     * Prints the possible values for each tile of the grid.
     */
    public void printState() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Tile tile = tiles[i][j];
                System.out.print("(" + i + "," + j + ") value = " + tile.getValue() + " - possible: ");
                for (int k : tile.possibleValues)
                    System.out.print(k + " ");
                System.out.println();
            }
        }
    }

}
