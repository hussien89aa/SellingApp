package com.selling.hussienalrubaye.androidselling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ControlPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        SaveSettings sv=new SaveSettings(this);
        sv.LoadData();
    }
}
