package com.jbion.sudoku.model;

import java.util.LinkedList;

import com.jbion.utils.strings.BoxDrawing;

public class Grid {

    private static final int N = 9; // size of the grid

    public Tile[][] tiles;
    public LinkedList<Tile> emptyTiles;

    /**
     * Creates a new {@code Grid} containing the specified numbers.
     * 
     * @param numbers
     *            The numbers to put in this {@code Grid}, listed row by row, from
     *            the upper one to the lower one, in left-to-right order within a
     *            row.
     * @throws IllegalArgumentException
     *             If there is not enough numbers, or too many, or some other
     *             characters than numbers.
     */
    public Grid(String[] numbers) {
        // Deal with obvious input errors
        if (numbers.length < N * N)
            throw new IllegalArgumentException(
                    "too few input digits (blanks must be given by zeros)");
        if (numbers.length > N * N)
            throw new IllegalArgumentException("too many input digits, only " + N * N
                    + " are needed");

        // Start initialization
        emptyTiles = new LinkedList<>();
        tiles = new Tile[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // parse the value at the position (i,j)
                int value;
                try {
                    value = Integer.parseInt(numbers[N * i + j]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("wrong input, only digits are accepted");
                }
                // create the tile at the position (i,j)
                if (value == 0) {
                    tiles[i][j] = new Tile(this, i, j, value);
                    emptyTiles.add(tiles[i][j]);
                } else if (0 < value && value <= N) {
                    tiles[i][j] = new Tile(this, i, j, value);
                } else {
                    throw new IllegalArgumentException("wrong input, only digits from 0 to " + N
                            + " are accepted");
                }
            }
        }
    }

    private static final String LF = BoxDrawing.NEW_LINE + "";
    private static final String H = BoxDrawing.BOX_DRAWINGS_LIGHT_HORIZONTAL + "";
    private static final String V = BoxDrawing.BOX_DRAWINGS_LIGHT_VERTICAL + "";
    private static final String H5 = H + H + H + H + H;
    private static final char ULC = BoxDrawing.BOX_DRAWINGS_LIGHT_DOWN_AND_RIGHT;
    private static final char DLC = BoxDrawing.BOX_DRAWINGS_LIGHT_UP_AND_RIGHT;
    private static final char URC = BoxDrawing.BOX_DRAWINGS_LIGHT_DOWN_AND_LEFT;
    private static final char DRC = BoxDrawing.BOX_DRAWINGS_LIGHT_UP_AND_LEFT;
    private static final char DTEE = BoxDrawing.BOX_DRAWINGS_LIGHT_DOWN_AND_HORIZONTAL;
    private static final char UTEE = BoxDrawing.BOX_DRAWINGS_LIGHT_UP_AND_HORIZONTAL;
    private static final char RTEE = BoxDrawing.BOX_DRAWINGS_LIGHT_VERTICAL_AND_RIGHT;
    private static final char LTEE = BoxDrawing.BOX_DRAWINGS_LIGHT_VERTICAL_AND_LEFT;
    private static final char CROSS = BoxDrawing.BOX_DRAWINGS_LIGHT_VERTICAL_AND_HORIZONTAL;

    /**
     * Prints the grid with fancy lines.
     */
    @Override
    public String toString() {

        String res = ULC + H5 + DTEE + H5 + DTEE + H5 + URC + LF;
        for (int i = 0; i < N; i++) {
            res = res.concat(V);
            for (int j = 0; j < N; j++) {
                res = res.concat(tiles[i][j].toString());
                if ((j + 1) % 3 == 0) {
                    res = res.concat(V);
                } else {
                    res = res.concat(" ");
                }
            }
            res = res.concat(LF);
            if (i == 2 || i == 5) {
                res = res.concat(RTEE + H5 + CROSS + H5 + CROSS + H5 + LTEE + LF);
            }
        }
        res = res.concat(DLC + H5 + UTEE + H5 + UTEE + H5 + DRC);
        return res;
    }

    /**
     * Prints the possible values for each tile of the grid.
     */
    public void printState() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Tile tile = tiles[i][j];
                System.out.print("(" + i + "," + j + ") value = " + tile.currentValue
                        + " - possible: ");
                for (int k : tile.possibleValues)
                    System.out.print(k + " ");
                System.out.println();
            }
        }
    }

}
