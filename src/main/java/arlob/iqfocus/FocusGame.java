package arlob.iqfocus;

import arlob.iqfocus.gui.Location;
import arlob.iqfocus.gui.Orientation;
import arlob.iqfocus.gui.Piece;
import arlob.iqfocus.gui.State;

import java.util.Arrays;
import java.util.HashSet;
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
        if (placement.length() % 4 == 0 && placement.length() > 0 && placement.length() / 4 < 11) {
            Set<Character> shapes = new HashSet<>();

            for (int i = 0; i < placement.length(); i += 4) {
                String piece = placement.substring(i, i+4);

                if (! isPiecePlacementWellFormed(piece) || !shapes.add(piece.charAt(0))) {
                    return false;
                }
            }
        
            return true;
        }
        
        return false;
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
        if(!isPlacementStringWellFormed(placement))
            return false;
        
        State[][] board = new State[9][5];

        for (int i = 0; i < placement.length(); i += 4) {
            Piece piece = new Piece(placement.substring(i,i+4));
            Location location = piece.getLocation();

            if (location.X + piece.getW() > 9 || location.Y + piece.getH() > 5 || location.X < 0 || location.Y < 0) {
                return false;
            }

            for (int y = 0; y < piece.getH(); y++) {
                for (int x = 0; x < piece.getW(); x++) {
                    int X = location.X + x;
                    int Y = location.Y + y;
                    State state = piece.getState(x, y);
                    
                    if (state != State.EMPTY && ((X == 0 && Y == 4) || (X == 8 && Y == 4))) {
                        return false;
                    }

                    if (board[X][Y] == null || board[X][Y] == State.EMPTY) {
                        board[X][Y] = state;
                    } else if (state != State.EMPTY) {
                        return false;
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
        Set<Character> unplacedShapes = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'));
        
        State[][] board = new State[9][5];
        
        for (int i = 0; i < placement.length(); i += 4) {
            Piece piece = new Piece(placement.substring(i, i+4));
            
            unplacedShapes.remove(Character.toLowerCase(placement.charAt(i)));

            for (int y = 0; y < piece.getH(); y++) {
                for (int x = 0; x < piece.getW(); x++) {
                    board[piece.getLocation().X + x][piece.getLocation().Y + y] = piece.getState(x, y);
                }
            }
        }

        Set<String> viablePlacements = new HashSet<>();

        for (char shape : unplacedShapes) {
            for (Orientation orientation : Orientation.values()) {
                Piece piece = new Piece(shape, col, row, orientation);
                int h = piece.getH(), w = piece.getW();

                for (int i = Math.max(row - h, 0); i <= Math.min(row + h, 4); i++) {
                    for (int j = Math.max(col - h, 0); j <= Math.min(col + w, 8); j++) {
                        if ((j == 0 && i == 4) || (j == 8 && i == 4) || j + w > 9 || i + h > 5 || j < 0 || i < 0 || !isPiecePlacementWellFormed(piece.toString())) continue;

                        if ((j + w > 2 || j - w < 6) && (i + h > 0 || i - h < 4)) {
                            boolean covers = false, valid = true;
                            
                            piece.setLocation(j, i);

                            for (int y = 0; y < h; y++) {
                                for (int x = 0; x < w; x++) {
                                    char colour = piece.getState(x, y).toChar();

                                    int X = j + x, Y = i + y;

                                    if (colour == 'E') continue;

                                    covers |= (X == col && Y == row);

                                    if ((board[X][Y] != null && board[X][Y] != State.EMPTY) || (X == 0 && Y == 4) || (X == 8 && Y == 4) || ((X > 2 && X < 6 && Y > 0 && Y < 4) && (challenge.charAt((X - 3) + (Y - 1) * 3) != colour))) {
                                        valid = false; break;
                                    }
                                }

                                if (!valid) break;
                            }
                        
                            if (covers && valid) {
                                viablePlacements.add(piece.toString());
                            }
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


        return "";
    }
}
