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
import android.widget.TextView;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {
    ArrayList<ToolType> fullsongpath =new ArrayList<ToolType>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        fullsongpath.addAll(ControlPanel.ToolType);
        ListView ls=( ListView) findViewById(R.id.listView);
        ls.setAdapter(new MyCustomAdapter());
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myintents = new Intent(getApplicationContext(), ControlPanel.class);
                myintents.putExtra("ToolTypeID",fullsongpath.get(position).ToolTypeID);
                startActivity(myintents);

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

        int id = item.getItemId();
        if (id == R.id.gbackmenu) {

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

            final   ToolType s = fullsongpath.get(position);


                View myView = mInflater.inflate(R.layout.settingitem, null);
                TextView textView = (TextView) myView.findViewById(R.id.textView);
                textView.setText(s.ToolTypeName);

                return myView;
        }
    }
}
