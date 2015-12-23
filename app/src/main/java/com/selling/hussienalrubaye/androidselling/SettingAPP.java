package com.selling.hussienalrubaye.androidselling;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingAPP extends AppCompatActivity {


    ArrayList<SettingItem> fullsongpath =new ArrayList<SettingItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_app);
        //intiixae items
        fullsongpath.add(new SettingItem(getResources().getString(R.string.generldepartment), R.drawable.settings));
       // fullsongpath.add(new SettingItem(getResources().getString(R.string.mytool), R.drawable.living1));
        fullsongpath.add(new SettingItem(getResources().getString(R.string.mytool), R.drawable.eye));
        fullsongpath.add(new SettingItem(getResources().getString(R.string.AddNewTool), R.drawable.add1));
        fullsongpath.add(new SettingItem(getResources().getString(R.string.action_settings), R.drawable.settings));
        fullsongpath.add(new SettingItem(getResources().getString(R.string.alerts), R.drawable.notifications));
        fullsongpath.add(new SettingItem(getResources().getString(R.string.callus),R.drawable.envelope91));
        fullsongpath.add(new SettingItem(getResources().getString(R.string.separeteapp), R.drawable.network));

        fullsongpath.add(new SettingItem(getResources().getString(R.string.rateapp), R.drawable.social));

        ListView ls=( ListView) findViewById(R.id.listView);
        ls.setAdapter(new MyCustomAdapter());
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {


                    switch (position) {
                        case 5:
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://selling.alruabye.net/"));
                            startActivity(intent);
                        } break;

                        case 6:
                        {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getResources().getString(R.string.sharemessage) + "  https://play.google.com/store/apps/details?id=" + SaveSettings.APPURL + ""));
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        } break;
                        case 1:
                        {
                            Intent intent = new Intent(getApplicationContext(), MyTool.class);
                            startActivity(intent);

                        }break;
                        case 7:
                        {
                            Uri uri = Uri.parse("market://details?id=" + SaveSettings.APPURL);
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            // To count with Play market backstack, After pressing back button,
                            // to taken back to our application, we need to add following flags to intent.
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + SaveSettings.APPURL)));
                            }

                        }

                        break;
                        case 2: // for my read history
                            Intent myintent = new Intent(getApplicationContext(), AddTool.class);

                            startActivity(myintent);
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_tool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.gbackmenu) { // stoped
            // Intent intent=new Intent(this,MainActivity.class);
            //startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // adapter for song list
    private class MyCustomAdapter extends BaseAdapter {


        public MyCustomAdapter() {

        }


        @Override
        public int getCount() {
            return fullsongpath.size();
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

            final   SettingItem s = fullsongpath.get(position);

            if((position==0) || (position==3)){

                View myView = mInflater.inflate(R.layout.setting_item_header, null);
                TextView textView = (TextView) myView.findViewById(R.id.textView);
                textView.setText(s.Name);
                return myView;
            }
            else if(position==4){
                View myView = mInflater.inflate(R.layout.setting_item_distance, null);
                TextView textView = (TextView) myView.findViewById(R.id.textView);
                textView.setText(s.Name);
                ImageView img=(ImageView)myView.findViewById(R.id.imgchannel);
                img.setImageResource(s.ImageURL);
              final TextView txtdisplaydis = (TextView) myView.findViewById(R.id.txtdisplaydis);
                txtdisplaydis.setText(String.valueOf(SaveSettings.Distance ) + "K");
                SeekBar SBDistance = (SeekBar) myView.findViewById(R.id.SBDistance);
                SBDistance.setProgress(SaveSettings.Distance/10);
                SBDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        SaveSettings.Distance=(progress+1)*10;
                        txtdisplaydis.setText(String.valueOf(SaveSettings.Distance ) + "K");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        SaveSettings mySaveSettings=new SaveSettings(getApplicationContext());
                        mySaveSettings.SaveData();
                    }
                });
                return myView;
            }
            else
            {
                View myView = mInflater.inflate(R.layout.settingitem, null);
                TextView textView = (TextView) myView.findViewById(R.id.textView);
                textView.setText(s.Name);
                ImageView img=(ImageView)myView.findViewById(R.id.imgchannel);
                img.setImageResource(s.ImageURL);
                return myView;}
        }
    }
}
