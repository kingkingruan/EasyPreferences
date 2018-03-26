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
        if (simplePreferences != null) {
            simplePreferences.setInt(120);
            Log.e(TAG, "int " + simplePreferences.getInt());
            simplePreferences.setString("Hello, World!");
            Log.e(TAG, "string " + simplePreferences.getString());
            simplePreferences.setBoolean(true);
            Log.e(TAG, "boolean " + String.valueOf(simplePreferences.getBoolean()));
            simplePreferences.setLong(222);
            Log.e(TAG, "long " + String.valueOf(simplePreferences.getLong()));
            simplePreferences.setFloat(0.234f);
            Log.e(TAG, "float " + String.valueOf(simplePreferences.getFloat()));
        } else {
            Log.e(TAG, "Can't load class SimplePreferences");
        }
    }
}
