package com.selling.hussienalrubaye.androidselling;

import android.graphics.Bitmap;

/**
 * Created by hussienalrubaye on 12/19/15.
 */
// loadinge images info for grid view
public class AddToolItem {

    public String ImagePath;
    public int Image;
    public  Bitmap bitmap;
    AddToolItem(String ImagePath,int Image,Bitmap bitmap){
        this.ImagePath=ImagePath;
        this.Image=Image;
        this.bitmap=bitmap;
    }
}

