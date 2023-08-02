package arlob.iqfocus;

import arlob.iqfocus.gui.Orientation;
import arlob.iqfocus.gui.Piece;
import arlob.iqfocus.gui.PieceType;
import arlob.iqfocus.gui.State;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class stateFromOffset {

    public void test(String in, int xf, int yf, State state_ex) {
        Piece piece=new Piece(in);
        State state_out = piece.getState(xf,yf);
        assertTrue("Input was '" + in + "', expected " + state_ex + " but got " + state_out, state_out == state_ex);
    }
    @Test
    public void teststate(){
        test("a000",0,0,State.GREEN);
        test("a001",0,0,State.EMPTY);
    }


}
