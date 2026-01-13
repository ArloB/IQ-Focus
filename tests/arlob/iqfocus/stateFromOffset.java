package arlob.iqfocus;

import org.junit.Test;

import arlob.iqfocus.classes.Orientation;
import arlob.iqfocus.classes.Piece;
import arlob.iqfocus.classes.PieceType;
import arlob.iqfocus.classes.State;

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
