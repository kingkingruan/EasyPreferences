package com.kingkingduanduan.easypreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashSet;

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
        Context context = this;
        SharedPreferences sharedPreferences = context.getSharedPreferences("ee", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("int", 1).commit();
        sharedPreferences.edit().putBoolean("boolean", true).commit();
        sharedPreferences.edit().putString("string", "string").commit();
        sharedPreferences.edit().putLong("long", 1l).commit();
        sharedPreferences.edit().putFloat("float", 0.1f).commit();
        sharedPreferences.edit().putStringSet("int", new HashSet<String>()).commit();

    }
}
