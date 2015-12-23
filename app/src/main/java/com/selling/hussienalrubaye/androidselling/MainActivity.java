package com.selling.hussienalrubaye.androidselling;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity {
    AQuery aq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aq = new AQuery(this);
        // ask to eanbel gps
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }

    public void busaveaccount(View view) {
        //make sure he enable the gps
        // ask to eanbel gps
        final LocationManager manager = (LocationManager) getSystemService(  LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        Location myLocation= getlocation(); new Location("phone"); //
        if(myLocation== null){
            Toast.makeText(this,getResources().getString(R.string.gpsoff),Toast.LENGTH_LONG).show();
            return;}
        //myLocation.setLatitude(20.3);
       // myLocation.setLongitude(52.6);

        EditText UserName=(EditText)findViewById(R.id.EDTUserName);
        EditText Email=(EditText)findViewById(R.id.EDemail);
        EditText Password=(EditText)findViewById(R.id.EDTpassword);
        EditText PasswordR=(EditText)findViewById(R.id.EDTpasswordR);
        EditText PhoneNumber=(EditText)findViewById(R.id.EDTPhoneNumber);

        //password donot much
        if(!Password.getText().equals(Password.getText())   ) {

            Operations.DisplayMessage(this,getResources().getString(R.string.PasswordNotCorrect));
        return;
        }
        // check if the email is correct
        if(!isEmailValid(Email.getText().toString())){

            Operations.DisplayMessage(this, getResources().getString(R.string.EmailNotCorrect));
            return;
        }
//validate user info
        if((UserName.getText().length()<2 )||(Email.getText().length()<2 ) ||(Password.getText().length()<2 )) {

            Operations.DisplayMessage(this, getResources().getString(R.string.AddAllinfo));

        return;
        }

        String url = SaveSettings.ServerURL +"UsersWS.asmx/Register?UserName="+ UserName.getText()+" &Password="+Password.getText()+"&Email="+ Email.getText()+"&PhoneNumber="+PhoneNumber.getText()+"&Logtit="+String.valueOf(myLocation.getLongitude()) +"&Latitle=" + String.valueOf(myLocation.getLatitude());

        aq.ajax(url, JSONObject.class, this, "jsonCallback");




    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.GpsDistable))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void jsonCallback(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){
            //successful ajax call
            //successful ajax call
            String msg=json.getString("Message");
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            int Isadd=json.getInt("IsAdded");
            if(Isadd==1) {
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
            }
        }else{
            //ajax error
        }

    }


    Location getlocation(){
        Location myLocation=null;
       try{
           LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
           if (locationManager != null) {


               myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               if (myLocation == null) {
                   myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

               }

           }
       }
       catch (Exception ex){

       }
        return myLocation;
    }
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
