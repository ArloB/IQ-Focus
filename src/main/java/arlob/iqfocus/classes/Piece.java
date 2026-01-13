package arlob.iqfocus.classes;

public class Piece {
    private PieceType pieceType;
    private Orientation orientation;
    private Location location;
    private int w;
    private int h;
    State[] states;

    public Piece(String placement) {
        setShape(placement.charAt(0));
        setOrientation(placement.charAt(3) - '0');
        setLocation(placement.charAt(1) - '0', placement.charAt(2) - '0');
    }

    public Piece(char shape, int col, int row, Orientation ori) {
        setShape(shape);
        orientation = ori;
        setLocation(col, row);
    }

    public Location getLocation() {
        return location;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public int getW(){
        return orientation == Orientation.ONE || orientation == Orientation.THREE ? h : w;
    }

    public int getH(){
        return orientation == Orientation.ONE || orientation == Orientation.THREE ? w : h;
    }

    public State[] getStates(){
        return states;
    }

    public State getState(int x, int y) {
        if (x < 0 || x >= getW() || y < 0 || y >= getH()) {
            return null;
        }
        
        return switch (orientation) {
            case ZERO -> states[(y * w) + x];
            case ONE -> states[((h - 1 - x) * w) + y];
            case TWO-> states[((h - 1 - y) * w) + (w - 1 - x)];
            case THREE -> states[(x * w) + (w - 1 - y)];
        };
    }

    public void setShape(char shape) {
        pieceType = PieceType.valueOf(Character.toString(shape).toUpperCase());

        states = States.states[pieceType.ordinal()];

        w = switch (shape) {
            case 'a', 'd', 'e', 'f', 'g', 'h' -> 3;
            case 'b', 'c', 'j' -> 4;
            case 'i' -> 2;
            default -> throw new IllegalArgumentException("Invalid piece placement: " + shape);
        };

        h = switch (shape) {
            case 'a', 'b', 'c', 'd', 'e', 'g', 'i', 'j' -> 2;
            case 'h' -> 3;
            case 'f' -> 1;
            default -> throw new IllegalArgumentException("Invalid piece placement: " + shape);
        };
    }

    public void setLocation(int x,int y) {
        location = new Location(x, y);
    }

    public void setOrientation(int ori) {
        orientation = Orientation.values()[ori];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(pieceType.toChar());
        sb.append(location.X);
        sb.append(location.Y);
        sb.append(orientation.toChar());
        return sb.toString();
    }
}
