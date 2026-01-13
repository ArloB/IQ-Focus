package arlob.iqfocus;

import arlob.iqfocus.classes.Location;
import arlob.iqfocus.classes.Orientation;
import arlob.iqfocus.classes.Piece;
import arlob.iqfocus.classes.State;
import javafx.util.Pair;
import arlob.iqfocus.classes.BoardState;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
 

/**
 * This class provides the text interface for the IQ Focus Game
 * <p>
 * The game is based directly on Smart Games' IQ-Focus game
 * (https://www.smartgames.eu/uk/one-player-games/iq-focus)
 */
public class FocusGame {
    /**
     * Determine whether a piece placement is well-formed according to the
     * following criteria:
     * - it consists of exactly four characters
     * - the first character is in the range a .. j (shape)
     * - the second character is in the range 0 .. 8 (column)
     * - the third character is in the range 0 .. 4 (row)
     * - the fourth character is in the range 0 .. 3 (orientation)
     *
     * @param piecePlacement A string describing a piece placement
     * @return True if the piece placement is well-formed
     */
    static boolean isPiecePlacementWellFormed(String piecePlacement) {
        if (piecePlacement.length() != 4) {
            return false;
        }
        
        char shape = piecePlacement.charAt(0);
        char col = piecePlacement.charAt(1);
        char row = piecePlacement.charAt(2);
        char ori = piecePlacement.charAt(3);

        return shape >= 'a' && shape <= 'j'
                && col >= '0' && col <= '8'
                && row >= '0' && row <= '4'
                && ori >= '0' && ori <= '3';
    }

    /**
     * Determine whether a placement string is well-formed:
     * - it consists of exactly N four-character piece placements (where N = 1 .. 10);
     * - each piece placement is well-formed
     * - no shape appears more than once in the placement
     *
     * @param placement A string describing a placement of one or more pieces
     * @return True if the placement is well-formed
     */
    public static boolean isPlacementStringWellFormed(String placement) {
        int len = placement.length();

        if (len == 0 || len > 40 || len % 4 != 0) {
            return false;
        }

        Set<Character> shapes = new HashSet<>();
        for (int i = 0; i < len; i += 4) {
            String piece = placement.substring(i, i + 4);

            if (!isPiecePlacementWellFormed(piece) || !shapes.add(piece.charAt(0))) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Determine whether a placement string is valid.
     *
     * To be valid, the placement string must be:
     * - well-formed, and
     * - each piece placement must be a valid placement according to the
     *   rules of the game:
     *   - pieces must be entirely on the board
     *   - pieces must not overlap each other
     *
     * @param placement A placement string
     * @return True if the placement sequence is valid
     */
    public static boolean isPlacementStringValid(String placement) {
        if (!isPlacementStringWellFormed(placement))
            return false;

        long occupied = 0L;
        final long FORBIDDEN = (1L << (4*9 + 0)) | (1L << (4*9 + 8));

        for (int i = 0; i < placement.length(); i += 4) {
            Piece piece = new Piece(placement.substring(i, i + 4));
            Location loc = piece.getLocation();

            if (loc.X < 0 || loc.Y < 0 || loc.X + piece.getW() > 9 || loc.Y + piece.getH() > 5) {
                return false;
            }

            for (int y = 0; y < piece.getH(); y++) {
                for (int x = 0; x < piece.getW(); x++) {
                    if (piece.getState(x, y) != State.EMPTY) {
                        int pos = (loc.Y + y) * 9 + loc.X + x;

                        if ((FORBIDDEN & (1L << pos)) != 0 || (occupied & (1L << pos)) != 0) {
                            return false;
                        }

                        occupied |= (1L << pos);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Given a string describing a placement of pieces and a string describing
     * a challenge, return a set of all possible next viable piece placements
     * which cover a specific board location.
     *
     * For a piece placement to be viable
     * - it must be valid
     * - it must be consistent with the challenge
     *
     * @param placement A viable placement string
     * @param challenge The game's challenge is represented as a 9-character string
     *                  which represents the color of the 3*3 central board area
     *                  squares indexed as follows:
     *                  [0] [1] [2]
     *                  [3] [4] [5]
     *                  [6] [7] [8]
     *                  each character may be any of
     *                  - 'R' = RED square
     *                  - 'B' = Blue square
     *                  - 'G' = Green square
     *                  - 'W' = White square
     * @param col      The location's column.
     * @param row      The location's row.
     * @return A set of viable piece placements, or null if there are none.
     */
    public static Set<String> getViablePiecePlacements(String placement, String challenge, int col, int row) {
        if (!placement.isEmpty() && !isPlacementStringValid(placement))
            return null;
        
        BoardState boardState = new BoardState(placement);

        // If target cell already covered, nothing new can cover it
        if (boardState.isCellOccupied(col, row)) return null;

        Set<String> viablePlacements = new HashSet<>();

        for (char shape : boardState.getUnplacedShapes()) {
            for (Orientation orientation : Orientation.values()) {
                Piece piece = new Piece(shape, col, row, orientation);
                int h = piece.getH(), w = piece.getW();

                int minRow = Math.max(0, row - (h - 1));
                int maxRow = Math.min(row, 5 - h);
                int minCol = Math.max(0, col - (w - 1));
                int maxCol = Math.min(col, 9 - w);

                for (int i = minRow; i <= maxRow; i++) {
                    for (int j = minCol; j <= maxCol; j++) {
                        if ((j == 0 && i == 4) || (j == 8 && i == 4)) continue;

                        boolean covers = false, valid = true;
                        
                        piece.setLocation(j, i);

                        for (int y = 0; y < h && valid; y++) {
                            for (int x = 0; x < w; x++) {
                                State cell = piece.getState(x, y);
                                if (cell == State.EMPTY) continue;

                                int X = j + x, Y = i + y;

                                covers |= (X == col && Y == row);

                                if (boardState.isCellOccupied(X, Y) ||
                                        (X == 0 && Y == 4) || (X == 8 && Y == 4)) {
                                    valid = false; break;
                                }

                                if (X > 2 && X < 6 && Y > 0 && Y < 4) {
                                    if (challenge.charAt((X - 3) + (Y - 1) * 3) != cell.toChar()) {
                                        valid = false; break;
                                    }
                                }
                            }
                        }

                        if (covers && valid) {
                            viablePlacements.add(piece.toString());
                        }
                    }
                }
            }
        }

        return viablePlacements.size() == 0 ? null : viablePlacements;
    }

    /**
     * Return the canonical encoding of the solution to a particular challenge.
     *
     * A given challenge can only be solved with a single placement of pieces.
     *
     * Since so  me piece placements can be described two ways (due to symmetry),
     * you need to use a canonical encoding of the placement, which means you
     * must:
     * - Order the placement sequence by piece IDs
     * - If a piece exhibits rotational symmetry, only return the lowest
     *   orientation value (0 or 1)
     *
     * @param challenge A challenge string.
     * @return A placement string describing a canonical encoding of the solution to
     * the challenge.
     */
    public static String getSolution(String challenge) {   
        BoardState boardState = new BoardState();
        
        String solution = findSolution("", challenge, boardState);
        
        if (solution != null) {
            String pieces = "";

            for (int i = 0; i < solution.length(); i += 4) {
                String piece = solution.substring(i, i + 4);
                char pieceType = piece.charAt(0);

                if (pieceType == 'f' || pieceType == 'g') {
                    char rotation = piece.charAt(3);
                    if (rotation == '2') {
                        piece = piece.substring(0, 3) + '0';
                    } else if (rotation == '3') {
                        piece = piece.substring(0, 3) + '1';
                    }
                }

                pieces += piece;
            }
            
            return pieces;
        }
        
        return "";
    }
    
    /**
     * Recursive backtracking method to find a solution for the given challenge
     * 
     * @param placement Current placement string
     * @param challenge The challenge string
     * @return A valid placement string that solves the challenge, or null if no solution exists
     */
    private static String findSolution(String placement, String challenge, BoardState boardState) {
        if (placement.length() == 40) {
            return placement;
        }
        
        List<Pair<Integer, Integer>> emptyCells = boardState.getEmptyCellsByPriority();
    
        for (Pair<Integer, Integer> cell : emptyCells) {
            int col = cell.getKey();
            int row = cell.getValue();
                
            if (!boardState.isCellOccupied(col, row)) {
                Set<String> viablePlacements = getViablePiecePlacements(placement, challenge, col, row);
                
                if (viablePlacements != null) {
                    for (String piecePlacement : viablePlacements) {
                        BoardState newBoardState = boardState.copy();

                        newBoardState.placePiece(piecePlacement);

                        String newPlacement = placement + piecePlacement;
                        String result = findSolution(newPlacement, challenge, newBoardState);
                        
                        if (result != null) {
                            return result;
                        }
                    }
                }
                
                return null;
            }
        }

        return null;
    }
}
