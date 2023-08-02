package arlob.iqfocus.gui;

import static arlob.iqfocus.gui.State.*;

public class States {
    public static State[][] states = new State[][]{
        {GREEN,WHITE,RED,EMPTY,RED,EMPTY},
        {EMPTY,BLUE,GREEN,GREEN,WHITE,WHITE,EMPTY,EMPTY},
        {EMPTY,EMPTY,GREEN,EMPTY,RED,RED,WHITE,BLUE},
        {RED,RED,RED,EMPTY,EMPTY,BLUE},
        {BLUE,BLUE,BLUE,RED,RED,EMPTY},
        {WHITE,WHITE,WHITE},
        {WHITE,BLUE,EMPTY,EMPTY,BLUE,WHITE},
        {RED,GREEN,GREEN,WHITE,EMPTY,EMPTY,WHITE,EMPTY,EMPTY},
        {BLUE,BLUE,EMPTY,WHITE},
        {GREEN,GREEN,WHITE,RED,GREEN,EMPTY,EMPTY,EMPTY}
    };
}
