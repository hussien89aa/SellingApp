package com.selling.hussienalrubaye.androidselling;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddTool extends AppCompatActivity {

    String TempToolID;
    GridView ls;
    MyCustomAdapter myadapter;
    Spinner spinner ;
    AQuery aq;
    ArrayList<AddToolItem> fullsongpath =new ArrayList<AddToolItem>();
    ArrayList< ToolType> ToolType = new ArrayList<ToolType>();
    int RESULT_LOAD_IMAGE=1;
    public Bitmap bitmap; // the selected image from gellary
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tool);

        // generate randam  number for the tool to salve all associate images
        final Random rand = new Random();
        String diceRoll =String.valueOf( rand.nextInt(7000000) + 5000);
       TempToolID=SaveSettings.UserID+ "000000"+ diceRoll ;

        // initialize the gridview
        fullsongpath.add(new AddToolItem("NewImage", R.drawable.loadimage,null));
       ls=(GridView) findViewById(R.id.gridView);
        myadapter=new MyCustomAdapter(fullsongpath);
        ls.setAdapter( myadapter);
        aq=new AQuery(this);
        spinner = (Spinner) findViewById(R.id.spinner);


// Array of choices




        String url = SaveSettings.ServerURL +"UsersWS.asmx"+"/GetToolType";
        aq.ajax(url, JSONObject.class, this, "jsonCallbackGetToolType");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.gbackmenu) {

            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void jsonCallbackGetToolType(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){
            //successful ajax call
            //successful ajax call

             JSONArray ToolData=json.getJSONArray("ToolData");
            String[] ToolTypeArray = new String[ToolData.length()];
            for (int i = 0; i <ToolData.length() ; i++) {
                JSONObject jsToolData=ToolData.getJSONObject(i);
               // JSONObject ToolTypeID=ToolData.getJSONObject(i);
                ToolType.add(new ToolType(jsToolData.getString("ToolTypeName") ,jsToolData.getInt("ToolTypeID")));
                ToolTypeArray[i]=jsToolData.getString("ToolTypeName");
            }
//intilize spanner
            //add spanner

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, ToolTypeArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            spinner.setAdapter(spinnerArrayAdapter);
        }else{
            //ajax error
        }

    }




    // when select image from gellary it will loaded here
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            //loading images
            int index = fullsongpath.size() - 1;
            if (index < 0)
                index = 0;
            fullsongpath.remove(index);
            fullsongpath.add(index, new AddToolItem("Loading", R.drawable.hu, null));
            myadapter.notifyDataSetChanged();

            ImageView IVADDPIC=new ImageView(this);
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            IVADDPIC.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //load images
            BitmapDrawable drawable = (BitmapDrawable) IVADDPIC.getDrawable();
            Bitmap largeBitmap = drawable.getBitmap();
            int h = 400; // height in pixels
            int w = 400; // width in pixels
           bitmap= Bitmap.createScaledBitmap(largeBitmap, h, w, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] Imagedata = baos.toByteArray();
           // ImageView image=new ImageView(this);
           // image.setImageBitmap(bitmap);
            String encodedImageData = Base64.encodeToString(Imagedata, 0);

            Map<String, Object> params = new HashMap<>();
            params.put("image",  encodedImageData );
            params.put("TempToolID", TempToolID);
            String url=SaveSettings.ServerURL +"UsersWS.asmx";
            new MyAsyncGetNewNews().execute(url,encodedImageData,TempToolID);
        }
    }

    public void buAddTool(View view) {
       // Toast.makeText(getApplicationContext(),String.valueOf(spinner.getSelectedItemPosition()),Toast.LENGTH_LONG).show();
        EditText ToolName=(EditText)findViewById(R.id.EDTToolName);
        EditText ToolDes=(EditText)findViewById(R.id.EDToolDes);
        EditText  ToolPrice=(EditText)findViewById(R.id.EDTToolPrice);
         String url = SaveSettings.ServerURL +"UsersWS.asmx"+"/AddTools?UserID="+ SaveSettings.UserID+" &ToolName="+ToolName.getText()+"&ToolDes="+ ToolDes.getText()+"&ToolPrice="+ToolPrice.getText()+"&ToolTypeID="+String.valueOf(ToolType.get(spinner.getSelectedItemPosition()).ToolTypeID) +"&TempToolID=" + String.valueOf(TempToolID);

        aq.ajax(url, JSONObject.class, this, "jsonCallback");

    }
    public void jsonCallback(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){
            //successful ajax call
            //successful ajax call
            String msg=json.getString("Message");
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            int IsAdded=json.getInt("IsAdded");
            if(IsAdded!=0){ // mean the tool is added go to manage tool later

            }
        }else{
            //ajax error
        }

    }
    // send image to the webservice
    public class MyAsyncGetNewNews extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected String  doInBackground(String... params) {
          final String NAMESPACE = "http://tempuri.org/";
          final String URL = params[0];
            final String METHOD_NAME = "UploadImage";
            final String SOAP_ACTION = "http://tempuri.org/UploadImage";
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("image", params[1]);
            request.addProperty("TempToolID",params[2]);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);


            try{
                androidHttpTransport.call(SOAP_ACTION, envelope);
                Object obj = envelope.bodyIn;
                SoapObject result = (SoapObject) envelope.getResponse();

            }

            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
// if he call it for news puprpose//***************************************************



        }
        protected void onPostExecute(String  result2){
            int index=fullsongpath.size() - 1;
            if(index<0)
                index=0;
            fullsongpath.remove(index);
            fullsongpath.add(index, new AddToolItem("NewImage", R.drawable.loadimage, null));
            fullsongpath.add(index, new AddToolItem("path", R.drawable.hu,  bitmap ));
            myadapter.notifyDataSetChanged();
            ls.smoothScrollToPosition( fullsongpath.size()-1);
        }




    }

    // add loading images into grid view
    private class MyCustomAdapter extends BaseAdapter {

        public  ArrayList<AddToolItem>  listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<AddToolItem>  listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
        }



        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
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

            final   AddToolItem s = listnewsDataAdpater.get(position);

if(s.ImagePath.equals("Loading")){
     View myView = mInflater.inflate(R.layout.add_tool_ticket_loading, null);
    return myView;

}else {
    final View myView = mInflater.inflate(R.layout.add_tool_ticket, null);

    ImageView IVloadimage = (ImageView) myView.findViewById(R.id.IVloadimage);
    if (s.ImagePath.equals("NewImage"))
        IVloadimage.setImageResource(s.Image);
    else
    IVloadimage.setImageBitmap(s.bitmap);

    IVloadimage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (s.ImagePath.equals("NewImage")) {
                //IVADDPIC = (ImageView) myView.findViewById(R.id.IVloadimage);
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        }
    });



    return myView;
}

        }
    }
}
