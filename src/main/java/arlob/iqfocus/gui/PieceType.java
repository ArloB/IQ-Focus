package arlob.iqfocus.gui;

public enum PieceType {
    A, B, C, D, E, F, G, H, I,J;



    public State returnStateAt(int x, int y, int orientation) {
        return typeStates[x][y];
    }

    private static State[][] typeStates = {{}};

    public State stateFromOffset(int xoff, int yoff,int h,int w,State[] states, Orientation orientation) {
        if (xoff < 0 || yoff < 0) return null;
        switch (orientation) {
            case ZERO:
                if(xoff>=w||yoff>=h) return null;
                return states[(yoff*w)    +xoff    ];
            case ONE:
                if(xoff>=h||yoff>=w) return null;
                return  states[((h-1-xoff)*w)+yoff    ];
            case TWO:
                if(xoff>=w||yoff>=h) return null;
                return states[((h-1-yoff)*w)+(w-1-xoff)];
            case THREE:
                if(xoff>=h||yoff>=w) return null;
                return states[(xoff*w)    +(w-1-yoff)];
        }
        return null;
    }
}
