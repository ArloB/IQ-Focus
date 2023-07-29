package arlob.iqfocus.gui;

public enum  Orientation {
    ZERO,ONE,TWO,THREE;
    //use number to represent orientation

    public char toChar(){
        switch (this) {
            case ONE:
                return '1';
            case TWO:
                return '2';
            case THREE:
                return '3';
            default:
                return '0';
        }
    }
}
