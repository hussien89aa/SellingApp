package com.selling.hussienalrubaye.androidselling;


import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {
    AQuery aq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aq=new  AQuery(this);
    }

    public void busaveaccount(View view) {
        Location myLocation= getlocation();//new Location("phone"); //
        myLocation.setLatitude(20.3);
        myLocation.setLongitude(52.6);

        EditText UserName=(EditText)findViewById(R.id.EDTUserName);
        EditText Email=(EditText)findViewById(R.id.EDemail);
        EditText Password=(EditText)findViewById(R.id.EDTpassword);
        EditText PhoneNumber=(EditText)findViewById(R.id.EDTPhoneNumber);



        String url = "http://selling.alruabye.net//UsersWS.asmx/Register?UserName="+ UserName.getText()+" &Password="+Password.getText()+"&Email="+ Email.getText()+"&PhoneNumber="+PhoneNumber.getText()+"&Logtit="+String.valueOf(myLocation.getLongitude()) +"&Latitle=" + String.valueOf(myLocation.getLatitude());

        aq.ajax(url, JSONObject.class, this, "jsonCallback");
    }



    public void jsonCallback(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){
            //successful ajax call
            //successful ajax call
            String msg=json.getString("Message");
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }else{
            //ajax error
        }

    }


    Location getlocation(){
        Location myLocation=null;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {


            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLocation == null) {
                myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            }

        }
        return myLocation;
    }
}
