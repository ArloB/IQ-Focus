package comp1110.ass2;

import comp1110.ass2.gui.Orientation;
import comp1110.ass2.gui.Piece;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class placementToOrientation {
    private void test(String sp, Orientation expected) {
        Orientation out = Piece.placementToOrientation(sp);

        assertTrue("Expected " + expected + " for input placement " + sp +
                ", but got " + out + ".", out == expected);
    }


    @Test
    public void  testOrientation1(){
        test("a001", Orientation.ONE);
        test("j441", Orientation.ONE);
}
@Test
    public void testOrientation2(){
        test("c092",Orientation.TWO);
        test("b252",Orientation.TWO);

}
@Test
    public void testOriValid3(){
        test("e003",Orientation.THREE);
        test("f353",Orientation.THREE);

}
@Test
    public void testOri4(){
        test("f330",Orientation.ZERO);
        test("d660",Orientation.ZERO);
}

}
