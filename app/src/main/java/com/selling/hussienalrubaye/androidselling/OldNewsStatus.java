package com.selling.hussienalrubaye.androidselling;

/**
 * Created by hussienalrubaye on 12/19/15.
 */
public class OldNewsStatus {
    // this keep save the stutus of last search operation
    public static int ToolTypeID=0;
    public static String q="@";
    public static  Boolean OnlyOneRequest=true; //only one call at time to the server
    public static int PrevfirstVisibleItem=0 ; //prevouse visble item
    public static boolean IsLoadMore=false;// if he look for new news
}
