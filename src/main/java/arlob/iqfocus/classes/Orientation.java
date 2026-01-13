package arlob.iqfocus.classes;

public enum  Orientation {
    ZERO, ONE, TWO, THREE;

    public char toChar(){
        return switch (this) {
            case ONE -> '1';
            case TWO -> '2';
            case THREE -> '3';
            default -> '0';
        };
    }
}
