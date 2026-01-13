package arlob.iqfocus.classes;

public enum PieceType {
    A, B, C, D, E, F, G, H, I, J;

    public Character toChar() {
        return (char) (ordinal() + 'a');
    }
}
