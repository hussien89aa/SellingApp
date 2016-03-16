package com.selling.hussienalrubaye.androidselling;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ControlPanel extends AppCompatActivity {
    public static ArrayList< ToolType> ToolType = new ArrayList<ToolType>();//llist tol type
    AQuery aq;

    public static ArrayList<ToolTicketItem> listnewsData = new ArrayList<ToolTicketItem>();// news data
    ArrayList<ToolTicketItem>    listnewsDataTemp = new ArrayList<ToolTicketItem>();//temp news data
    MyCustomAdapter myadapter;// news adapter
    ListView lsNews;
    int totalItemCountVisible=0; //totalItems visible
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        SaveSettings sv=new SaveSettings(this);
        sv.LoadData();
        //assigned list view to news
        lsNews=(ListView)findViewById(R.id.LVNews);
        myadapter=new MyCustomAdapter(listnewsData);
        lsNews.setAdapter(myadapter);//intisal with data
        listnewsData.clear();
// list view scrool
        lsNews.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
// scroll up loading


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //firstVisibleItem it have been seen
                //visibleItemCount visible now
                totalItemCountVisible = firstVisibleItem + visibleItemCount;
                if ((totalItemCountVisible <= totalItemCount - 10) && ((firstVisibleItem > 2)))//loading items before  he be in the end it 15 news
                {
                    if ((listnewsDataTemp.size() == 0) && (totalItemCount > 1) && (OldNewsStatus.OnlyOneRequest == true)) // to agnore if the result is one item for lading and load more only for
                    {

                       // get last news id
                        // remove any loading ticket
String ToolID="0";
                        for(int i=0;i<listnewsData.size();i++)
                        {
                            if ((!listnewsData.get(i).DateAdd.equals("loading_ticket")) &&
                                     (!listnewsData.get(i).DateAdd.equals("ticket_first_item"))&&
                                             (!listnewsData.get(i).DateAdd.equals("No_new_data")))
                            {
                                ToolID = listnewsData.get(i).ToolID;
break;
                            }
                        }


                    //one time  for request this code load items in hidded way
                        OldNewsStatus.IsLoadMore = true; // make this tag as llod more
                        // load only if we dont have tempror to vaoid repeat
                        loadUrl(OldNewsStatus.q, OldNewsStatus.ToolTypeID, 0, totalItemCount, totalItemCount + 20);
                    }
                } else if (totalItemCountVisible == totalItemCount)//loading if he reach end
                {
                    RefreshListView();// load news when he read the end
                }
                if (((firstVisibleItem == 0)) && (OldNewsStatus.PrevfirstVisibleItem != firstVisibleItem)) {
                    //if he be in first news

                    if (listnewsData.size() > 1)//if we have loaded data but isnot appeaed in list
                    {  //if isnot already loading know
                        if (!listnewsData.get(1).DateAdd.equals("loading_ticket")) {
                            OldNewsStatus.OnlyOneRequest = true;

                            loadUrl(OldNewsStatus.q, OldNewsStatus.ToolTypeID, 0, 1, 20);
                        }
                    }
                }
                OldNewsStatus.PrevfirstVisibleItem = firstVisibleItem;

            }
        });


        aq = new AQuery(this);
        //get tool type
        ToolType.clear();
        String url = SaveSettings.ServerURL +"UsersWS.asmx"+"/GetToolType?lng="+ Locale.getDefault().getLanguage().toString() ;
        aq.ajax(url, JSONObject.class, this, "jsonCallbackGetToolType");
    }

    public void jsonCallbackGetToolType(String url, JSONObject json, AjaxStatus status) throws JSONException {

        if(json != null){
            //successful ajax call
            //successful ajax call

            JSONArray ToolData=json.getJSONArray("ToolData");
            for (int i = 0; i <ToolData.length() ; i++) {
                JSONObject jsToolData=ToolData.getJSONObject(i);
                // JSONObject ToolTypeID=ToolData.getJSONObject(i);
                ToolType.add(new ToolType(jsToolData.getString("ToolTypeName") ,jsToolData.getInt("ToolTypeID")));
            }
//intilize spanner
            //add spanner


        }else{
            //ajax error
        }

    }


    @Override
    protected void onResume() {

        if(listnewsData.size()==0)//first time

        try { // if it is sub cateory call
            Bundle b = getIntent().getExtras(); // load the notifications
            int ToolTypeID = b.getInt("ToolTypeID");
            loadUrl("@", ToolTypeID, 0, 1, 20);
        }
        catch (Exception ex){
            //initail first call
            loadUrl("@", 0, 0, 1, 20);
        }
        super.onResume();
    }
    SearchView searchView;
    Menu myMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.control_menu, menu);
        myMenu=menu;
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //final Context co=this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//code for search
                OldNewsStatus.OnlyOneRequest=true;
                loadUrl(query, OldNewsStatus.ToolTypeID, 0, 1, 20);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu) {

            Intent myintents = new Intent(getApplicationContext(), SettingAPP.class);

            startActivity(myintents);
        }

        return super.onOptionsItemSelected(item);
    }
    public void buAddTool(View view) {
        Intent intent=new Intent(this,AddTool.class);
        startActivity(intent);
    }
    //this method update the list view with news when we load it
    private void RefreshListView(){
        //synchronized (listnewsDataTemp)  {
        if (listnewsDataTemp.size() > 0)//if we have loaded data but isnot appeaed in list
        {  //remove loading from first
            if (  listnewsData.get(1).DateAdd.equals("loading_ticket")) {
                listnewsData.clear();//clear for new items
                listnewsData.add( new ToolTicketItem(null,null,null,null, "ticket_first_item",null ));
            }
            // remove any loading ticket
            for(int i=0;i<listnewsData.size();i++){
                if (listnewsData.get(i).DateAdd.equals("loading_ticket"))
                    listnewsData.remove(i);}
//refresh the new data
            listnewsData.addAll(listnewsDataTemp);//get items from temp
            listnewsDataTemp.clear();
            myadapter.notifyDataSetChanged();

            if((listnewsData.size()<=22)&& (!  listnewsData.get( listnewsData.size()-1).ToolName.equals(R.string.no_search_result)) && (!  listnewsData.get( listnewsData.size()-1).DateAdd.equals("loading_ticket"))) // this mean he load first 20 news wih one divestment
            {
                lsNews.setSelection(1);
            }
        }

        else {
            if((listnewsData.size()>0)&&(OldNewsStatus.OnlyOneRequest==false))
            {if (!  listnewsData.get( listnewsData.size()-1).DateAdd.equals("loading_ticket"))
            {   listnewsData.add( new ToolTicketItem(null,null,null,null, "loading_ticket",null ));

                myadapter.notifyDataSetChanged();
            }}

        }
        // }

    }
// load news
private  void  loadUrl(String q,int ToolTypeID,int ToolID,int StratFrom,int EndTo) {

    if(OldNewsStatus.OnlyOneRequest==false)return  ;// no more than one request

    OldNewsStatus.OnlyOneRequest=false; // stop accept another request until this one done

    //if interent connection is feild
    if (!Operations.isConnectingToInternet(this)) {

        listnewsData.add(0, new ToolTicketItem(null,getResources().getString(R.string.nonetworkConnection),null,null, "No_new_data",null ));
        myadapter.notifyDataSetChanged();
        lsNews.setSelection(1);
        return; // do not counine if there is internet service

    }

//initailze the  data of var for load more purpose
    OldNewsStatus.ToolTypeID=ToolTypeID;
    OldNewsStatus.q=q;
    //clear list if he search from first
    if((StratFrom==1) ) //intail loading
    { listnewsData.clear();
        //if(listnewsData.size()==0)
            listnewsData.add(0, new ToolTicketItem(null,null,null,null, "ticket_first_item",null ));
        listnewsData.add(1, new ToolTicketItem(null,null,null,null, "loading_ticket",null ));
  myadapter.notifyDataSetChanged();
        lsNews.setSelection(1);

    }
//

    String url =SaveSettings.ServerURL +"UsersWS.asmx/GetToolListing?UserID="+SaveSettings.UserID+"&Distance="+ SaveSettings.Distance+"&From="+String.valueOf(StratFrom) +"&to="+String.valueOf(EndTo) +"&ToolTypeID="+String.valueOf(ToolTypeID) +"&q=" +q +"&ToolID="+String.valueOf(ToolID);
    // read news
    new MyAsyncTaskgetNews().execute(url, "news");


}

    public void bunhomepage(View view) {
        OldNewsStatus.OnlyOneRequest=true;
        loadUrl("@", 0, 0, 1, 20);
    }

    public void buMyTool(View view) {
        Intent myintents = new Intent(getApplicationContext(), MyTool.class);
        startActivity(myintents);
    }

    public void buToolType(View view) {
        Intent myintents = new Intent(getApplicationContext(), Categories.class);
        startActivity(myintents);
    }

    // get news from server
public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
    @Override
    protected void onPreExecute() {
        //before works
    }
    @Override
    protected String  doInBackground(String... params) {
        // TODO Auto-generated method stub
        String NewsData;
        InputStream inputStream;
        try {
            HttpClient httpClient =new DefaultHttpClient();
            HttpResponse httpResponse =httpClient.execute(new HttpGet(params[0]));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream!=null)
                NewsData=Operations.ConvertInputToStringNoChange(inputStream);
            else
                NewsData="Did not work";
            publishProgress(NewsData);

        } catch (Exception e) {
            // TODO Auto-generated catch block

        }
        return null;
    }
    protected void onProgressUpdate(String... progress) {

            try {
                JSONObject json = new JSONObject(progress[0]);

                //  listnewsData.remove(NewsTicket.)
                listnewsDataTemp.clear();

                int Tag = json.getInt("HasTool") ;
                if (Tag == 1)
                {// there is data
                    JSONArray newData = json.getJSONArray("ToolData");


                    for (int i = 0; i < newData.length(); i++) {
                        JSONObject newDataItem = newData.getJSONObject(i);

                        // laod news to list
                        listnewsDataTemp.add(new ToolTicketItem(newDataItem.getString("ToolID"), newDataItem.getString("ToolName")
                                        , newDataItem.getString("ToolDes"), newDataItem.getString("ToolPrice"),
                                        newDataItem.getString("DateAdd"), newDataItem.getString("PictureLink")
                        ));
                    }
                }
                else { // no more new data

                    listnewsDataTemp.add(  new ToolTicketItem(null,getResources().getString(R.string.no_search_result),null,null, "No_new_data",null ));

                }

                // myadapter=new MyCustomAdapter(listnewsData);
                if( OldNewsStatus.IsLoadMore ==false){

                    RefreshListView();

                }



            } catch (Exception ex) {
            }


        }

    protected void onPostExecute(String  result2){

        if((OldNewsStatus.IsLoadMore==true)&&(totalItemCountVisible>=listnewsData.size()-3)){ //this mean his reach the end (3) for loading and first packet
            RefreshListView();
        }
        OldNewsStatus.IsLoadMore=false;
        //when done
        OldNewsStatus. OnlyOneRequest=true; // enable another request
    }




}
    // news adapter for loading view and news notifcation
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
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();

            final   ToolTicketItem s = listnewsDataAdpater.get(position);
            if(s.DateAdd.equals("loading_ticket")) { //it is loading ticket

                View myView = mInflater.inflate(R.layout.news_ticket_loading, null);
                return myView;
            }
            else if( s.DateAdd.equals("No_new_data")){ //no more news
                View myView = mInflater.inflate(R.layout.news_ticket_no_news, null);
                TextView txtMessage=( TextView)myView.findViewById(R.id.txtMessage);
                txtMessage.setText(s.ToolName);
                return myView;
            }
            else if( s.DateAdd.equals("ticket_first_item")){
                View myView = mInflater.inflate(R.layout.news_ticket_first_item, null);
                return myView;
            }
            else
            {

                    View myView = mInflater.inflate(R.layout.news_ticket, null);
                    final   TextView txt_viewer = (TextView) myView.findViewById(R.id.txt_tool_name);
                    txt_viewer.setText(s.ToolName);
                    TextView txt_channel_name = (TextView) myView.findViewById(R.id.txt_tool_desc);
                    txt_channel_name.setText(s.ToolDes);
                    txt_channel_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //* call for details
                            callDetailsView(s.ToolID);
                        }
                    });
                //txt_channel_name.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    TextView txt_news_date = (TextView) myView.findViewById(R.id.txt_tool_date);
                    txt_news_date.setText(s.DateAdd);
                    TextView txt_news_title = (TextView) myView.findViewById(R.id.txt_tool_price);
                    txt_news_title.setText("$" +s.ToolPrice  );


                WebView webview=(WebView)myView.findViewById(R.id.wv_tool_image);
                     String html = "<html><body style='background:#ffffff;text-align:left'><img src='"+  SaveSettings.ServerURL +"Images/"+ s.PictureLink +"' alt='Mountain View' height='100px' width='100px'></body></html>\n";
                       webview.loadData(html, "text/html", null);
                   // webview.getSettings().setLoadWithOverviewMode(true);
              //  webview.getSettings().setUseWideViewPort(true);
               // String html = "<html><body><img src=\"" + SaveSettings.ServerURL +"Images/"+ s.PictureLink+ "\" width=\"100%\" style=' height:\"50px;'\"\"/></body></html>";
                   // webview.loadData(html, "text/html", null);
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

    private  void callDetailsView(String ToolID ){
        Intent myintents = new Intent(getApplicationContext(), ToolDescription.class);

        myintents.putExtra("ToolID", ToolID);
        startActivity(myintents);

    }
}
