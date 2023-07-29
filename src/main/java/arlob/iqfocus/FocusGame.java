package arlob.iqfocus;

import arlob.iqfocus.gui.Orientation;
import arlob.iqfocus.gui.Piece;
import arlob.iqfocus.gui.PieceType;
import arlob.iqfocus.gui.State;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class provides the text interface for the IQ Focus Game
 * <p>
 * The game is based directly on Smart Games' IQ-Focus game
 * (https://www.smartgames.eu/uk/one-player-games/iq-focus)
 */
public class FocusGame {
    private final static State[][] board = new State[9][5];
    String placement;

    /**
     * Determine whether a piece placement is well-formed according to the
     * following criteria:
     * - it consists of exactly four characters
     * - the first character is in the range a .. j (shape)
     * - the second character is in the range 0 .. 8 (column)
     * - the third character is in the range 0 .. 4 (row)
     * - the fourth character is in the range 0 .. 3 (orientation)
     *
     * @param piecePlacement A string describing a piece placement
     * @return True if the piece placement is well-formed
     */
    static boolean isPiecePlacementWellFormed(String piecePlacement) {

        boolean out = false;

        if (piecePlacement.length() == 4){
            char firstChar = piecePlacement.charAt(0);
            int secInt = Character.getNumericValue(piecePlacement.charAt(1));
            int thirdInt = Character.getNumericValue(piecePlacement.charAt(2));
            int fourthInt = Character.getNumericValue(piecePlacement.charAt(3));

            if ((firstChar >= 'a'&& firstChar <= 'j')
                && (secInt >=0 && secInt <= 8)
                && (thirdInt >=0 && thirdInt <= 4)
                && (fourthInt >=0 && fourthInt <= 3))
            { out = true; }
        }

        return out;
    }


    /**
     * Determine whether a placement string is well-formed:
     * - it consists of exactly N four-character piece placements (where N = 1 .. 10);
     * - each piece placement is well-formed
     * - no shape appears more than once in the placement
     *
     * @param placement A string describing a placement of one or more pieces
     * @return True if the placement is well-formed
     */
    public static boolean isPlacementStringWellFormed(String placement) {
        /*
        int n=placement.length();
        boolean[] a=new boolean[10]; //boolean default false; mark array with num(represent the first char of substring)

        if(n<1||n>40||n%4!=0) return false; // s ="", n=0 false

        for(int i=0;i<n;i=i+4){
            String s=placement.substring(i,i+4);
            if(!isPiecePlacementWellFormed(s)) return false;
            int j=s.charAt(0)-'a'; //char starts from a ASCII , 'a' -'a'=0
            if(a[j]==true) return false;
            else a[j]=true;
        }
        return true; */



        int count = 0;
        String[] a = placement.split("(?<=\\G.{4})"); // https://stackoverflow.com/a/3761521

        if(a.length < 11 && a.length != 0) {
            for (int i = 0; i < a.length; i++) {
                if (isPiecePlacementWellFormed(a[i])) {
                    int finalI = i;
                    count += (Arrays.stream(a).filter(x -> x.charAt(0) == a[finalI].charAt(0)).count() < 2 ? 1 : 0); // https://stackoverflow.com/a/1128728
                }
            }
        }

        return count == a.length;
    }



    /**
     * Determine whether a placement string is valid.
     *
     * To be valid, the placement string must be:
     * - well-formed, and
     * - each piece placement must be a valid placement according to the
     *   rules of the game:
     *   - pieces must be entirely on the board
     *   - pieces must not overlap each other
     *
     * @param placement A placement string
     * @return True if the placement sequence is valid
     */



    public static boolean isPlacementStringValid(String placement) {
        // FIXME Task 5: determine whether a placement string is valid

        if(!isPlacementStringWellFormed(placement))
            return false;

        State[][] boardcopy=new State[9][5];

        int l=placement.length();
        for(int i=0;i<l;i+=4){

            String string = placement.substring(i,i+4);
            Piece piece=new Piece(string);
            PieceType pieceType=piece.getPieceType();
            int x=piece.getLocation().getX();
            int y=piece.getLocation().getY();
            int w=piece.getW();
            int h=piece.getH();
            State[] states=piece.getStates();
            Orientation ori=piece.getOrientation();
            if(ori==Orientation.ONE||ori==Orientation.THREE){
               int temp=w;
               w=h;
               h=temp;
            }

            if(x+w>9||y+h>5||x<0||y<0) {
                return false;}

            for(int yoff=0;yoff<h;yoff++){
                for(int xoff=0;xoff<w;xoff++){
                    int xx=x+xoff;
                    int yy=y+yoff;
                    State state= pieceType.stateFromOffset(xoff,yoff,piece.getH(),piece.getW(),states,ori);
                    if((xx==0&&yy==4)||(xx==8&&yy==4)) {   // left and right corner
                        if(state!=State.EMPTY) return false;}
                    if(boardcopy[xx][yy]==null||boardcopy[xx][yy]==State.EMPTY) {
                        //System.out.println("this is"+" "+(x+xoff)+" "+(y+yoff)+" "+boardcopy[x+xoff][y+yoff]);
                        boardcopy[xx][yy]=state;
                        //System.out.println("NOW "+(x+xoff)+" "+(y+yoff)+" "+boardcopy[x+xoff][y+yoff]);
                        }

                    else if(state==State.EMPTY) continue;
                    else {
                        return false;}
                }
            }
        }
        return true;
    }

    private void updateBoardStates(Piece piece) {

    }

    /**
     * Given a string describing a placement of pieces and a string describing
     * a challenge, return a set of all possible next viable piece placements
     * which cover a specific board location.
     *
     * For a piece placement to be viable
     * - it must be valid
     * - it must be consistent with the challenge
     *
     * @param placement A viable placement string
     * @param challenge The game's challenge is represented as a 9-character string
     *                  which represents the color of the 3*3 central board area
     *                  squares indexed as follows:
     *                  [0] [1] [2]
     *                  [3] [4] [5]
     *                  [6] [7] [8]
     *                  each character may be any of
     *                  - 'R' = RED square
     *                  - 'B' = Blue square
     *                  - 'G' = Green square
     *                  - 'W' = White square
     * @param col      The location's column.
     * @param row      The location's row.
     * @return A set of viable piece placements, or null if there are none.
     */





        public static Set<String> getViablePiecePlacements(String placement, String challenge, int col, int row) {
            // FIXME Task 6: determine the set of all viable piece placements given existing placements and a challenge
            State[][] board=new State[9][5];

            Set<String> viable_placements= new HashSet<>();

            char[] pi_type={'a','b','c','d','e','f','g','h','i','j'};
            int l=placement.length();
            for(int i=0;i<l;i+=4){                           //  update board with a piece in string placement
                String string = placement.substring(i,i+4);
                char pi=string.charAt(0);
                for(int j=0;j<10;j++){
                    if(pi_type[j]==pi) pi_type[j]=' ';    // update pieces which have been used to be ' '
                }
                Piece piece = new Piece(string);
                State[] states=piece.getStates();
                int h=piece.getH();
                int w=piece.getW();
                PieceType pieceType=piece.getPieceType();
                int x=piece.getLocation().getX();
                int y=piece.getLocation().getY();
                Orientation ori=piece.getOrientation();

                int ww = w,hh= h;
                if(ori==Orientation.ONE||ori==Orientation.THREE){
                    ww = h; hh= w;
                }

                for(int xf=0;xf<ww;xf++){
                    for(int yf=0;yf<hh;yf++){
                        State state= pieceType.stateFromOffset(xf,yf,piece.getH(),piece.getW(),states,ori);
                        if(state!=State.EMPTY){
                            board[x+xf][y+yf]=state;
                        }
                    }
                }
            }


            // new pieces  the usable pieces
            State[][] cha=new State[9][5];
            Piece[] pieces;
            for(int k=0;k<10;k++){
                if(pi_type[k]!=' '){

                    String piecestring=pi_type[k]+"000";
                    Piece piece=new Piece(piecestring);
                    PieceType pieceType=piece.getPieceType();
                    State[] states=piece.getStates();

                    int w=piece.getW();
                    int h=piece.getH();
                    for (int ori=0;ori<4;ori++){



                        piece.setOrientation(ori);
                        Orientation orientation=piece.getOrientation();
                        int ww = w,hh= h;
                        if(ori%2!=0){
                           ww = h;  hh = w;
                        }
                        for(int xf=0;xf<ww;xf++){
                            for(int yf=0;yf<hh;yf++){
                                State state= pieceType.stateFromOffset(xf,yf,h,w,states,orientation);
                                if(state==State.EMPTY) continue;
                                int x=col-xf;
                                int y=row-yf;
                                if(pi_type[k]=='e'&&x==3&&y==2&&ori==3){
                                    int sss=0;
                                }
                                piece.setLocation(x,y);
                                // check challenge
                                if (isPieceValid(board,piece,challenge)) {
                                    String viable_string=Character.toString(pi_type[k])+x+y+ori;
                                    viable_placements.add(viable_string);
                                    //System.out.println("valid"+viable_string);
                                }
                                else {
                                   // System.out.println("invalid "+pi_type[k]+" "+x+" "+y+""+ori);
                                    continue;}
                            }
                        }
                    }
                }
            }
            if (viable_placements.size()!=0) return viable_placements;
            else  return null;
        }



        public static boolean isPieceValid(State[][] board,Piece piece,String challenge){
            // update board with new piece!!
            State[][] boardcopy=new State[9][5];
            for(int i=0;i<9;i++){
                for(int j=0;j<5;j++){
                    boardcopy[i][j]=board[i][j]; // should initialize the boardcopy to the original one after being put a piece on it
                }
            }
            State[] states=piece.getStates();
            int h=piece.getH();
            int w=piece.getW();
            PieceType pieceType=piece.getPieceType();
            int x=piece.getLocation().getX();
            int y=piece.getLocation().getY();
            Orientation ori=piece.getOrientation();

            int ww = w,hh= h;
            if(ori==Orientation.ONE||ori==Orientation.THREE){
                ww = h; hh= w;
            }

            if(x+ww>9||y+hh>5||x<0||y<0) {
                return false;}

            for(int xf=0;xf<ww;xf++){
                for(int yf=0;yf<hh;yf++){
                    State state= pieceType.stateFromOffset(xf,yf,piece.getH(),piece.getW(),states,ori);
                    int xx=x+xf;
                    int yy=y+yf;
                    if((xx==0&&yy==4)||(xx==8&&yy==4)) {   // left and right corner
                        if(state!=State.EMPTY) return false;}


                    if(boardcopy[xx][yy]==null||boardcopy[xx][yy]==State.EMPTY){
                        boardcopy[xx][yy]=state;
                    }
                    else if(state==State.EMPTY) continue;
                    else return false;
                }
            }

            // check the challenge
            State[] state_challenge=new State[9]; // the target challenge
            State[] cha_on_board=new State[9];   // the current state of the challenge on board
            int k=0;
            for(int r=1;r<=3;r++){
                for (int c=3;c<=5;c++){
                    cha_on_board[k]=boardcopy[c][r];
                    k++;
                }
            }

            for (k=0;k<=8;k++){
                if(challenge.charAt(k)=='R') state_challenge[k]=State.RED;
                else if(challenge.charAt(k)=='B') state_challenge[k]=State.BLUE;
                else if(challenge.charAt(k)=='W') state_challenge[k]=State.WHITE;
                else state_challenge[k]=State.GREEN;
            }

            for (k=0;k<=8;k++){
                if(cha_on_board[k]==null||cha_on_board[k]==State.EMPTY) continue;
                else if (cha_on_board[k]==state_challenge[k]) continue;
                else return false;
            }

            if(pieceType==PieceType.E&&x==3&&y==2&&ori==Orientation.THREE){
                System.out.println("");
            }

            return true;
        }




    static State[][] updateBoard(String placement,State[][] board){
        int length=placement.length();
        if(length==0) return board;
        for(int i=0;i<length;i+=4){                           //  update board with a piece in string placement
            String string = placement.substring(i,i+4);
            Piece piece = new Piece(string);
            State[] states=piece.getStates();
            int h=piece.getH();
            int w=piece.getW();
            PieceType pieceType=piece.getPieceType();
            int x=piece.getLocation().getX();
            int y=piece.getLocation().getY();
            Orientation ori=piece.getOrientation();

            int ww = w,hh= h;
            if(ori==Orientation.ONE||ori==Orientation.THREE){
                ww = h; hh= w;
            }

            for(int xf=0;xf<ww;xf++){
                for(int yf=0;yf<hh;yf++){
                    State state= pieceType.stateFromOffset(xf,yf,piece.getH(),piece.getW(),states,ori);
                    if(state!=State.EMPTY){
                        board[x+xf][y+yf]=state;
                    }
                }
            }
        }

        return board;
    }


    public static Boolean solution(StringBuffer placement,String challenge){
        if (placement.length()==10) return true;
        else {
            State[][] boardstate=new State[9][5];

            boardstate=updateBoard(placement.toString(),boardstate);
            Set<String> viableplacements=new HashSet<>();
            for(int i =0;i<9;i++){
                for(int j=0;j<5;j++){
                    if(boardstate[i][j]==null||boardstate[i][j]==State.EMPTY){
                        viableplacements=getViablePiecePlacements(placement.toString(),challenge,i,j);
                        if(viableplacements==null) continue; //
                        else{
                            for(String new_placement : viableplacements){
                                StringBuffer old_placement=placement;
                                placement.append(new_placement);
                                if(!solution(placement,challenge))
                                    placement=old_placement;
                                else{
                                    solution(placement,challenge);
                                }
                            }

                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Return the canonical encoding of the solution to a particular challenge.
     *
     * A given challenge can only be solved with a single placement of pieces.
     *
     * Since so  me piece placements can be described two ways (due to symmetry),
     * you need to use a canonical encoding of the placement, which means you
     * must:
     * - Order the placement sequence by piece IDs
     * - If a piece exhibits rotational symmetry, only return the lowest
     *   orientation value (0 or 1)
     *
     * @param challenge A challenge string.
     * @return A placement string describing a canonical encoding of the solution to
     * the challenge.
     */
    public static String getSolution(String challenge) {
        // FIXME Task 9: determine the solution to the game, given a particular challenge
        String placement="";
        StringBuffer pl=new StringBuffer(placement);
        int col;
        int row;
        State[][] board=new State[9][5];
        State[][] copyboard=new State[9][5];
        Set<String> viableplacements=new HashSet<>();
        solution(pl,challenge);
        return pl.toString();


        /*
        for(int i=3;i<=5;i++){
            for (int j=1;j<=3;j++){
                if(board[i][j]==null||board[i][j]==State.EMPTY){
                    viableplacements=getViablePiecePlacements(placement, challenge,i,j);
                    for (String vp:viableplacements){
                        placement=placement+vp;

                    }
                }
            }
        }
*/

    }


}
