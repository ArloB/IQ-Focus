package arlob.iqfocus;

import arlob.iqfocus.gui.Orientation;
import arlob.iqfocus.gui.Piece;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class placementToOrientation {
    private void test(String sp, Orientation expected) {
        Piece piece = new Piece(sp);
        Orientation out = piece.getOrientation();

        assertTrue("Expected " + expected + " for input placement " + sp +
                ", but got " + out + ".", out == expected);
    }

    @Test
    public void  testOrientation1() {
        test("a001", Orientation.ONE);
        test("j441", Orientation.ONE);
    }
    @Test
    public void testOrientation2() {
        test("c092",Orientation.TWO);
        test("b252",Orientation.TWO);
    }
    @Test
    public void testOriValid3() {
        test("e003",Orientation.THREE);
        test("f353",Orientation.THREE);
    }
    @Test
    public void testOri4() {
        test("f330",Orientation.ZERO);
        test("d660",Orientation.ZERO);
    }
}
