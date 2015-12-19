package com.selling.hussienalrubaye.androidselling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.security.PublicKey;

/**
 * Created by hussienalrubaye on 12/17/15.
 */
public class SaveSettings {

    Context context;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs3" ;
    public static   String UserID = "0";
    public static String ServerURL="http://selling.alruabye.net/";
    public  SaveSettings(Context context) {
        this.context=context;
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }
    public void SaveData()  {

        try

        {

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("UserID",String.valueOf(UserID));
            editor.commit();
            LoadData( );
        }

        catch( Exception e){}
    }
    public   void LoadData( ) {

        String TempUserID=sharedpreferences.getString("UserID","empty");
        if(!TempUserID.equals("empty"))
            UserID=TempUserID;// load user name
        else {
            Intent intent=new Intent(context,Login.class);
            context.startActivity(intent);
        }
    }
}
