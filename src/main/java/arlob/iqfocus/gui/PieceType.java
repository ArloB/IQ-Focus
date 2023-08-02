package arlob.iqfocus.gui;

public enum PieceType {
    A, B, C, D, E, F, G, H, I, J;

    public Character toChar() {
        return (char) (ordinal() + 'A');
    }
}
