package com.selling.hussienalrubaye.androidselling;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyTool extends AppCompatActivity {
    AQuery aq;
    MyCustomAdapter myadapter;// news adapter
    ListView lsNews;
    public   ArrayList<ToolTicketItem> listnewsData = new ArrayList<ToolTicketItem>();// news data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tool);
        aq=new AQuery(this);
        lsNews=(ListView)findViewById(R.id.LVNews);
        listnewsData.add(0, new ToolTicketItem(null,getResources().getString(R.string.MsgDonothaveOffers),null,null, "loading_ticket",null ));

        myadapter=new MyCustomAdapter(listnewsData);

        lsNews.setAdapter(myadapter);//intisal with data
        String url = SaveSettings.ServerURL +"UsersWS.asmx"+"/GetMyTool?UserID=" + SaveSettings.UserID;
        aq.ajax(url, JSONObject.class, this, "jsonCallbackGetToolType");
    }
    public void jsonCallbackGetToolType(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){

            try {

                //  listnewsData.remove(NewsTicket.)


                int Tag = json.getInt("HasTool") ;
                if (Tag == 1)
                {// there is data
                    JSONArray newData = json.getJSONArray("ToolData");


                    for (int i = 0; i < newData.length(); i++) {
                        JSONObject newDataItem = newData.getJSONObject(i);

                        // laod news to list
                        listnewsData.add(new ToolTicketItem(newDataItem.getString("ToolID"), newDataItem.getString("ToolName")
                                , newDataItem.getString("ToolDes"), newDataItem.getString("ToolPrice"),
                                newDataItem.getString("DateAdd"), newDataItem.getString("PictureLink")
                        ));

                    }

                }
                else { // no more new data
                    listnewsData.add( new ToolTicketItem(null,getResources().getString(R.string.MsgDonothaveOffers),null,null, "No_new_data",null ));

                }


                listnewsData.remove(0);
myadapter.notifyDataSetChanged();


            } catch (Exception ex) {
            }
        }else{
            //ajax error
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.gbackmenu) {

           // this.finish();
            Intent myintent = new Intent(getApplicationContext(), ControlPanel.class);

            startActivity(myintent);
        }
        if (id == R.id.addtool) {

            Intent myintent = new Intent(getApplicationContext(), AddTool.class);

            startActivity(myintent);
        }
        return super.onOptionsItemSelected(item);
    }
    private  void callDetailsView(String ToolID ){
        Intent myintents = new Intent(getApplicationContext(), ToolDescription.class);

        myintents.putExtra("ToolID", ToolID);
        startActivity(myintents);

    }
    private class MyCustomAdapter extends BaseAdapter {
        public  ArrayList<ToolTicketItem>  listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<ToolTicketItem>  listnewsDataAdpater) {
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
        public View getView(final  int position, View convertView, ViewGroup parent) {

            LayoutInflater mInflater = getLayoutInflater();
           final ToolTicketItem s=listnewsDataAdpater.get(position);

            if( s.DateAdd.equals("No_new_data")){
                View myView = mInflater.inflate(R.layout.news_ticket_no_news, null);
                TextView txtMessage=( TextView)myView.findViewById(R.id.txtMessage);
                txtMessage.setText(s.ToolName);
                return myView;
            }
            else  if(s.DateAdd.equals("loading_ticket")) { //it is loading ticket

                View myView = mInflater.inflate(R.layout.news_ticket_loading, null);
                return myView;
            }
            else
            {

                View myView = mInflater.inflate(R.layout.my_tool_ticket, null);


                TextView textView = (TextView) myView.findViewById(R.id.txt_tool_name);
                textView.setText(s.ToolName);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //* call for details
                        callDetailsView(s.ToolID);
                    }
                });
                WebView webview=(WebView)myView.findViewById(R.id.WV_tool_image);
                String summary = "<html><body style='background:#FF0099CC;text-align:center'><img src='"+ SaveSettings.ServerURL +"Images/"+ s.PictureLink  +"' alt='Mountain View' height='40' width='40'></body></html>\n";
                webview.loadData(summary, "text/html", null);
                Button buRomveOffer=(Button)myView.findViewById(R.id.buRomveOffer);
                buRomveOffer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listnewsData.remove(position);
                        myadapter.notifyDataSetChanged();
                        String url = SaveSettings.ServerURL +"UsersWS.asmx"+"/RemoveTool?ToolID=" + s.ToolID;
                        aq.ajax(url, JSONObject.class, this, "jsonCallbackRmoveTool");
                    }
                });

                webview.setOnTouchListener(new View.OnTouchListener() {

                    public final static int FINGER_RELEASED = 0;
                    public final static int FINGER_TOUCHED = 1;
                    public final static int FINGER_DRAGGING = 2;
                    public final static int FINGER_UNDEFINED = 3;

                    private int fingerState = FINGER_RELEASED;


                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        switch (motionEvent.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                if (fingerState == FINGER_RELEASED)
                                    fingerState = FINGER_TOUCHED;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            case MotionEvent.ACTION_UP:
                                if (fingerState != FINGER_DRAGGING) {
                                    fingerState = FINGER_RELEASED;

                                    //* call for details
                                    callDetailsView(s.ToolID);

                                } else if (fingerState == FINGER_DRAGGING)
                                    fingerState = FINGER_RELEASED;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            case MotionEvent.ACTION_MOVE:
                                if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING)
                                    fingerState = FINGER_DRAGGING;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            default:
                                fingerState = FINGER_UNDEFINED;

                        }

                        return false;
                    }
                });


                return myView;



        }
    }

}

    public void jsonCallbackRmoveTool(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){

            try {

                //  listnewsData.remove(NewsTicket.)


                int Tag = json.getInt("IsDeleted") ;
                if (Tag == 1) { //tool is removed from server

                }
            } catch (Exception ex) {
            }
        }else{
            //ajax error
        }

    }


}
