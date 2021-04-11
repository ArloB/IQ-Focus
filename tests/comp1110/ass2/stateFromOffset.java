package comp1110.ass2;

import comp1110.ass2.gui.Orientation;
import comp1110.ass2.gui.Piece;
import comp1110.ass2.gui.PieceType;
import comp1110.ass2.gui.State;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class stateFromOffset {

    public void test(String in, int xf, int yf, State state_ex) {
        Piece piece=new Piece(in);
        PieceType pieceType=piece.getPieceType();
        int w=piece.getW();
        int h=piece.getH();
        State[] states=piece.getStates();
        Orientation ori=piece.getOrientation();
        int orientation=in.charAt(3);
        if(orientation%2!=0) {
            int temp=w;
            w=h;
            h=temp;
        }
        State state_out = pieceType.stateFromOffset(xf,yf,w,h, states, ori);
        System.out.println("state is "+state_out);
        System.out.println("ori is "+ori);
        assertTrue("Input was '" + in + "', expected " + state_ex + " but got " + state_out, state_out == state_ex);
    }
    @Test
    public void teststate(){
        test("a000",0,0,State.GREEN);
        test("a001",0,0,State.EMPTY);
    }


}
