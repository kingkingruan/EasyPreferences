package com.kingkingduanduan.easypreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            Set<String> stringSet = new HashSet<>();
            stringSet.add("set1");
            stringSet.add("set2");
            simplePreferences.setSetString(stringSet);
            Log.e(TAG, "stringSet " + simplePreferences.getSetString());
            simplePreferences.clear();
            Log.e(TAG, "string " + simplePreferences.getString());
            Log.e(TAG, "default float " + simplePreferences.getDefaultFloat());
            Log.e(TAG, "default long" + simplePreferences.getDefaultLong());
            Log.e(TAG, "default set" + simplePreferences.getDefaultSetString().toString());
            User user = new User();
            user.setName("king2");
            simplePreferences.setUser(user);
            Log.e(TAG, "user " + simplePreferences.getUser());
        } else {
            Log.e(TAG, "Can't load class SimplePreferences");
            SharedPreferences sharedPreferences = getSharedPreferences("ee", MODE_PRIVATE);
            sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

                }
            });
        }
    }
}
