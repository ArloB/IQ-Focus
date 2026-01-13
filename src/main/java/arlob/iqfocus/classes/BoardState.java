package arlob.iqfocus.classes;

import arlob.iqfocus.FocusGame;

import javafx.util.Pair;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

public class BoardState {
    long occupied;
    Set<Character> unplacedShapes;
    String placementString = "";

    public BoardState() {
        occupied = 0L;
        unplacedShapes = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'));
    }

    public BoardState(String placement) {
        this();

        for (int i = 0; i < placement.length(); i += 4) {
            Piece piece = new Piece(placement.substring(i, i + 4));
            placePiece(piece);
        }
    }

    public BoardState copy() {
        BoardState copy = new BoardState();
        copy.occupied = occupied;
        copy.unplacedShapes = new HashSet<>(unplacedShapes);
        return copy;
    }

    public void placePiece(Piece piece) {
        for (int y = 0; y < piece.getH(); y++) {
            for (int x = 0; x < piece.getW(); x++) {
                if (piece.getState(x, y) != State.EMPTY) {
                    int pos = (piece.getLocation().Y + y) * 9 + piece.getLocation().X + x;
                    occupied |= (1L << pos);
                }
            }
        }

        placementString += piece.toString();
        
        if (!FocusGame.isPlacementStringValid(placementString)) {
            throw new IllegalArgumentException("Invalid placement string: " + placementString);
        }

        unplacedShapes.remove(piece.getPieceType().toChar());
    }

    public void placePiece(String pieceString) {
        Piece piece = new Piece(pieceString);

        placePiece(piece);
    }

    void removePiece(Piece piece) {
        for (int y = 0; y < piece.getH(); y++) {
            for (int x = 0; x < piece.getW(); x++) {
                if (piece.getState(x, y) != State.EMPTY) {
                    int pos = (piece.getLocation().Y + y) * 9 + piece.getLocation().X + x;
                    occupied &= ~(1L << pos);
                }
            }
        }

        unplacedShapes.add(piece.getPieceType().toChar());
    }

    public void removePiece(String pieceString) {
        Piece piece = new Piece(pieceString);

        removePiece(piece);
    }

    public boolean isCellOccupied(int col, int row) {
        return (occupied & (1L << (row * 9 + col))) != 0;
    }

    private static boolean isCellForbidden(int col, int row) {
        return (col == 0 && row == 4) || (col == 8 && row == 4);
    }

    public Set<Character> getUnplacedShapes() {
        return unplacedShapes;
    }

    public List<Pair<Integer, Integer>> getEmptyCellsByPriority() {
        List<int[]> emptyCells = new ArrayList<>();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 9; col++) {
                if (!isCellOccupied(col, row) && !isCellForbidden(col, row)) {
                    emptyCells.add(new int[] { col, row, calculateCellPriority(col, row) });
                }
            }
        }

        emptyCells.sort((a, b) -> Integer.compare(a[2], b[2]));

        List<Pair<Integer, Integer>> result = new ArrayList<>();
        for (int[] cell : emptyCells) {
            result.add(new Pair<>(cell[0], cell[1]));
        }

        return result;
    }

    private int calculateCellPriority(int col, int row) {
        // Central window gets highest priority
        if (col >= 3 && col <= 5 && row >= 1 && row <= 3) {
            return 1;
        }

        // Adjacent to central window
        if (col >= 2 && col <= 6 && row >= 0 && row <= 4) {
            return 2;
        }

        // Check constraint level
        if (countViablePlacementsForCell(col, row) <= 2) {
            return 3;
        }

        // Edge cells get lower priority
        if (col == 0 || col == 8 || row == 0 || row == 4) {
            return 4;
        }

        return 5;
    }

    private int countViablePlacementsForCell(int col, int row) {
        int count = 0;

        for (char shape : unplacedShapes) {
            for (Orientation orientation : Orientation.values()) {
                Piece piece = new Piece(shape, col, row, orientation);
                if (FocusGame.isPlacementStringValid(piece.toString())) {
                    count++;
                }
            }
        }

        return count;
    }
}
