package com.selling.hussienalrubaye.androidselling;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ToolDescription extends FragmentActivity implements OnMapReadyCallback {
AQuery aq;
    private GoogleMap mMap;
    // Add a marker in Sydney and move the camera
    LatLng OwnerLocation ; //location of the tool owner
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_description);
        Bundle b = getIntent().getExtras(); // load the notifications
        String ToolID = b.getString("ToolID");

        aq=new AQuery(this);
        String url = SaveSettings.ServerURL +"UsersWS.asmx"+"/GetToolDescription?ToolID="+ToolID;
        aq.ajax(url, JSONObject.class, this, "jsonCallbackGetToolType");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        LoadAdmob();

    }

    private  void    LoadAdmob(){
        try{
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.Pop_ad_unit_id));
            AdRequest adRequest = new AdRequest.Builder()
                    //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                }

                @Override
                public void onAdLoaded() {
                    DisplayAdmob();
                }
            });
        } catch (Exception ex) {
        }
    }
    private void DisplayAdmob() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


       // mMap.addMarker(new MarkerOptions().position(OwnerLocation).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(OwnerLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(OwnerLocation, 12));
    }
    public void jsonCallbackGetToolType(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){
            //successful ajax call
            //successful ajax call
            JSONObject newData = json.getJSONObject("ToolData");
            JSONArray PictureLinkar=newData.getJSONArray("PictureLinkar");
            final TextView txt_viewer = (TextView)  findViewById(R.id.txt_tool_name);
            txt_viewer.setText(newData.getString("ToolName"));
            TextView txt_channel_name = (TextView) findViewById(R.id.txt_tool_desc);
            txt_channel_name.setText(newData.getString("ToolDes") );
            TextView txt_news_date = (TextView) findViewById(R.id.txt_tool_date);
            txt_news_date.setText(newData.getString("DateAdd"));
            TextView txt_tool_price = (TextView) findViewById(R.id.txt_tool_price);
            txt_tool_price.setText("$"+  newData.getString("ToolPrice")  );

            TextView txt_Email = (TextView) findViewById(R.id.txt_Email);
            txt_Email.setText( getResources().getString(R.string.Emailu) + " "+newData.getString("Email") );
            TextView txt_PhoneNumber = (TextView) findViewById(R.id.txt_PhoneNumber);
            txt_PhoneNumber.setText(getResources().getString(R.string.PhoneNumberu) + " "+ newData.getString("PhoneNumber"));
if(newData.getString("PhoneNumber").length()<2)
    txt_PhoneNumber.setVisibility(View.GONE);
            ArrayList<String> OfferImages =new ArrayList<String>(); //
            for (int i = 0; i <PictureLinkar.length() ; i++) {
                OfferImages.add(PictureLinkar.getString(i).toString());
            }
            OwnerLocation = new LatLng(Double.parseDouble( newData.getString("Latitle")),Double.parseDouble(newData.getString("Logtit")));
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //load images
            ListView  ls=(ListView) findViewById(R.id.LVImages);

            ls.setAdapter( new MyCustomAdapter(OfferImages));
            Operations.setListViewHeightBasedOnChildren(ls);

        }else{
            //ajax error
        }

    }




    // add loading images into grid view
    private class MyCustomAdapter extends BaseAdapter {

        public  ArrayList<String> ImagesPAthAdp;

        public MyCustomAdapter(ArrayList<String>  listnewsDataAdpater) {
            this.ImagesPAthAdp =listnewsDataAdpater;
        }



        @Override
        public int getCount() {
            return ImagesPAthAdp.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();

            final   String imagepath = ImagesPAthAdp.get(position);

                final View myView = mInflater.inflate(R.layout.tool_image, null);

            WebView webview=(WebView)myView.findViewById(R.id.wv_tool_image);
              webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setUseWideViewPort(true);
            String html = "<html><body><img src=\"" + SaveSettings.ServerURL +"Images/"+ imagepath+ "\" width=\"100%\" style=' height:\"50px;'\"\"/></body></html>";
             webview.loadData(html, "text/html", null);



                return myView;


        }
    }


}
