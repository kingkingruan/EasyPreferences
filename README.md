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
            simplePreferences.setCheckVersion(120);
            Log.e(TAG, "" + simplePreferences.getCheckVersion());
        }
    }

## Current Features

- get and set types of int long boolean float

## TODO

- [ ] support String
- [ ] remove key
- [ ] clear all keys
- [ ] define the name of SharedPreferences
- [ ] support setting default value
- [ ] support using method of Apply to submit
- [ ] support  type of Set<String>
- [ ] support all types
- [ ] support compile
