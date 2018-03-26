package com.kingkingduanduan.easypreferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimplePreferences simplePreferences = EasyPreferences.getSharedPreferences(this, SimplePreferences.class);
        Log.e(TAG, simplePreferences == null ? "null" : "not null");
        if (simplePreferences != null) {
            simplePreferences.setCheckVersion(120);
            Log.e(TAG, "" + simplePreferences.getCheckVersion());
        }
    }
}
