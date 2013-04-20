package model;

import java.util.LinkedList;

public class Grid {

    private static final int N = 9; // size of the grid

    public Tile[][] tiles;
    public LinkedList<Tile> emptyTiles;

    public Grid(String[] args) throws Exception {
        // Deal with obvious input errors
        if (args.length < N * N)
            throw new Exception("too few input digits (blanks must be given by zeros)");
        if (args.length > N * N)
            throw new Exception("too many input digits, only " + N * N + " are needed");
        
        // Start initialization
        emptyTiles = new LinkedList<Tile>();
        tiles = new Tile[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // parse the value at the position (i,j)
                int value;
                try {
                    value = Integer.parseInt(args[N * i + j]);
                } catch (NumberFormatException e) {
                    throw new Exception("wrong input, only digits separated by spaces are accepted");
                }
                // create the tile at the position (i,j)
                if (value == 0) {
                    tiles[i][j] = new Tile(this, i, j, value);
                    emptyTiles.add(tiles[i][j]);
                } else if (0 < value && value <= N) {
                    tiles[i][j] = new Tile(this, i, j, value);
                } else {
                    throw new Exception("wrong input, only digits separated by spaces are accepted");
                }
            }
        }
    }

    /**
     * Prints the grid with fancy lines.
     */
    public String toString() {
        String res = "┌─────┬─────┬─────┐\n";
        for (int i = 0; i < N; i++) {
            res = res.concat("│");
            for (int j = 0; j < N; j++) {
                res = res.concat(tiles[i][j].toString());
                if ((j + 1) % 3 == 0) {
                    res = res.concat("│");
                } else {
                    res = res.concat(" ");
                }
            }
            res = res.concat("\n");
            if (i == 2 || i == 5) {
                res = res.concat("├─────┼─────┼─────┤\n");
            }
        }
        res = res.concat("└─────┴─────┴─────┘");
        return res;
    }

    /**
     * Prints the possible values for each tile of the grid.
     */
    public void printState() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Tile tile = tiles[i][j];
                System.out.print("(" + i + "," + j + ") value = " + tile.value + " - possible: ");
                for (int k : tile.possibleValues)
                    System.out.print(k + " ");
                System.out.println();
            }
        }
    }

}
