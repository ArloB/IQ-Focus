package comp1110.ass2.gui;
import static comp1110.ass2.gui.State.*;
public class Piece {
    private PieceType pieceType;
    private Orientation orientation;
    private Location location;
    //public ArrayList <Location> coords;
    private int w;
    private int h;
    State[] states;

    public Piece(String placement) {
        this.pieceType = PieceType.valueOf(Character.toString(placement.charAt(0) - 32));
        this.orientation = placementToOrientation(placement);
        this.location = placementToLocation(placement);
        char ch=placement.charAt(0);
        if(ch=='a'){
            states = new  State[]{GREEN,WHITE,RED,EMPTY,RED,EMPTY};
            w=3;  h=2;
        }
        if(ch=='b'){
            states =new State[]{EMPTY,BLUE,GREEN,GREEN,WHITE,WHITE,EMPTY,EMPTY};
            w=4;h=2;
        }
        if(ch=='c'){
            states =new State[]{EMPTY,EMPTY,GREEN,EMPTY,RED,RED,WHITE,BLUE};
            w=4;h=2;
        }
        if(ch=='d'){
            states =new State[]{RED,RED,RED,EMPTY,EMPTY,BLUE};
            w=3;h=2;
        }
        if(ch=='e'){
            states =new State[]{BLUE,BLUE,BLUE,RED,RED,EMPTY};
            w=3;h=2;
        }
        if(ch=='f'){
            states = new State[]{WHITE,WHITE,WHITE};
            w=3;h=1;
        }
        if(ch=='g'){
            states = new State[]{WHITE,BLUE,EMPTY,EMPTY,BLUE,WHITE};
            w=3;h=2;
        }
        if(ch=='h'){
            states =new State[]{RED,GREEN,GREEN,WHITE,EMPTY,EMPTY,WHITE,EMPTY,EMPTY};
            w=3;h=3;
        }
        if(ch=='i'){
            states = new State[]{BLUE,BLUE,EMPTY,WHITE};
            w=2;h=2;
        }
        if(ch=='j'){
            states = new State[]{GREEN,GREEN,WHITE,RED,GREEN,EMPTY,EMPTY,EMPTY};
            w=4;h=2;
        }

    }
/*
    public void setStates(int w,int h,State[] states) {
        this.w=w;
        this.h=h;
        this.states = new State[states.length];
        for(int i = 0 ; i<states.length;i++)
            this.states[i] = states[i];
    }*/

    public Location getLocation() {
        return location;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public int getW(){return w;}

    public int getH(){return h;}

    public State[] getStates(){return states;}

    public void setLocation(int x,int y){
        this.location=new Location(x,y);
    }

    public void setOrientation(int ori){
        if(ori==0)  orientation=Orientation.ZERO;
        else if(ori==1)  orientation=Orientation.ONE;
        else if(ori==2)  orientation=Orientation.TWO;
        else  orientation=Orientation.THREE;

    }

    public static Orientation placementToOrientation(String placement) {
        char[] Placement = placement.toCharArray();
        if (Placement[3] == '0')
            return Orientation.ZERO;
        if (Placement[3] == '1')
            return Orientation.ONE;
        if (Placement[3] == '2')
            return Orientation.TWO;
        if (Placement[3] == '3')
            return Orientation.THREE;

        return Orientation.ZERO;
    }


    private static Location placementToLocation(String placement) {
        int x=placement.charAt(1)-'0';
        int y=placement.charAt(2)-'0';
        return new Location(x,y);
    }

    @Override
    public String toString() {
        return this.pieceType + "" + this.location.X + "" + this.location.Y + "" + this.orientation;
    }
}
