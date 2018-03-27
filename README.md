# EasyPreferences

Easy to use SharedPreferences

Use Annotations and Compile-time Processing to generate the code , so you can save value just with methods.

## Sample

Define an interface , and then Use class EasyPreferences to get the instance of interface.

    @Preferences
    public interface SimplePreferences {
        void setCheckVersion(int version);
        int getCheckVersion();
    }
    
    public class MainActivity extends AppCompatActivity {
        private static final String TAG = "MainActivity";
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            SimplePreferences simplePreferences = EasyPreferences.getSharedPreferences(this, SimplePreferences.class);
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
        }
    }

## Current Features

- get and set types of int long boolean float

## TODO

- [x] support String
- [ ] remove key
- [x] clear all keys
- [ ] define the name of SharedPreferences
- [ ] support setting default value
- [ ] support using method of Apply to submit
- [x] support  type of Set<String>
- [ ] support all types
- [ ] support compile
- [ ] support boolean when commit the value
