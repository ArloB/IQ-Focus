package arlob.iqfocus.classes;

public enum State {
    RED,
    GREEN,
    BLUE,
    WHITE,
    EMPTY;

    public char toChar(){
        return switch (this) {
            case RED -> 'R';
            case GREEN -> 'G';
            case BLUE -> 'B';
            case WHITE -> 'W';
            default -> 'E';
        };
    }
}
